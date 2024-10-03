Function GenerateSeedNumber%(Seed$)
	Local Temp% = 0
	Local Shift% = 0
	Local i%
	
	For i = 1 To Len(Seed)
		Temp = Temp Xor (Asc(Mid(Seed, i, 1)) Shl Shift)
		Shift = ((Shift + 1) Mod 24)
	Next
	Return(Temp)
End Function

Function WrapAngle#(Angle#)
	If Angle = Infinity Then Return(0.0)
	If Angle < 0.0
		Return(360.0 + (Angle Mod 360.0))
	Else
		Return(Angle Mod 360.0)
	EndIf
End Function

Function CurveValue#(Value#, Old#, Smooth#)
	If fps\Factor[0] = 0.0 Then Return(Old)
	
	Local Val# = Old + (Value - Old) * (1.0 / Smooth * fps\Factor[0])
	
	If Value < Old
		Return(Max(Val, Value))
	Else
		Return(Min(Val, Value))
	EndIf
End Function

Function CurveAngle#(Value#, Old#, Smooth#)
	If fps\Factor[0] = 0.0 Then Return(Old)
	
	Return(WrapAngle(Old + AngleDist(Value, Old) * (1.0 / Smooth * fps\Factor[0])))
End Function

Function PointDirection#(x1#, z1#, x2#, z2#)
	Return(ATan2(z1 - z2, x1 - x2))
End Function

Function AngleDist#(a0#, a1#)
	Local b# = a0 - a1

	If b < -180.0 Then Return(b + 360.0)
	If b > 180.0 Then Return(b - 360.0)

	Return(b)
End Function

Function FloatToString$(n#, Count%)
	Return(Left(n, Len(Int(Str(n))) + Count + 1))
End Function

Function Chance%(Percent%)
	; ~ Perform a chance given a probability
	Return(Rand(0, 100) <= Percent)
End Function

Function MoveForward%(Dir%, PathX%, PathY%, RetVal% = False)
	; ~ Move 1 unit along the grid in the designated direction
	If Dir = 1
		If RetVal
			Return(PathY + 1)
		Else
			Return(PathX)
		EndIf
	EndIf
	If RetVal
		Return(PathY)
	Else
		Return(PathX - 1 + Dir)
	EndIf
End Function

Function TurnIfDeviating%(Max_Deviation_Distance_%, Pathx%, Center_%, Dir%, RetVal% = False)
	; ~ Calculate the current deviation 
	Local Current_Deviation% = Center_ - Pathx
	; ~ Check if the deviation exceeds the maximum. If so, turn around
	Local Deviated% = ((Dir = 0 And Current_Deviation >= Max_Deviation_Distance_) Lor (Dir = 2 And Current_Deviation <= -Max_Deviation_Distance_))
	
	If Deviated Then Dir = ((Dir + 2) Mod 4)
	
	If RetVal
		Return(Deviated)
	Else
		Return(Dir)
	EndIf
End Function

Function ChangeAngleValueForCorrectBoneAssigning#(Value#)
	If Value <= 180.0 Then Return(Value)
	
	Return((-360.0) + Value)
End Function

Function ReadPixelColor%(Pixel%, Shrid%)
	Return(Pixel Shr Shrid) And $FF
End Function

Function MouseOn%(x%, y%, Width%, Height%)
	Return((MousePosX > x And MousePosX < x + Width) And (MousePosY > y And MousePosY < y + Height))
End Function

Function Find860Angle#(n.NPCs, fr.Forest)
	TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, fr\Forest_Pivot)
	
	Local PlayerX# = Floor((TFormedX() + 6.0) / 12.0)
	Local PlayerZ# = Floor((TFormedZ() + 6.0) / 12.0)
	
	TFormPoint(EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider), 0, fr\Forest_Pivot)
	
	Local x# = (TFormedX() + 6.0) / 12.0
	Local z# = (TFormedZ() + 6.0) / 12.0
	Local xt% = Floor(x), zt% = Floor(z)
	Local x2%, z2%
	
	If xt <> PlayerX Lor zt <> PlayerZ ; ~ The monster is not on the same tile as the player
		For x2 = Max(xt - 1, 0) To Min(xt + 1, ForestGridSize - 1)
			For z2 = Max(zt - 1, 0) To Min(zt + 1, ForestGridSize - 1)
				If fr\Grid[(z2 * ForestGridSize) + x2] > 0 And (x2 <> xt Lor z2 <> zt) And (x2 = xt Lor z2 = zt)
					; ~ Tile (x2, z2) is closer to the player than the monsters current tile
					If (Abs(PlayerX - x2) + Abs(PlayerZ - z2)) < (Abs(PlayerX - xt) + Abs(PlayerZ - zt))
						; ~ Calculate the position of the tile in world coordinates
						TFormPoint(x2 * 12.0, 0.0, z2 * 12.0, fr\Forest_Pivot, 0)
						Return(PointDirection(EntityX(n\Collider), EntityZ(n\Collider), TFormedX(), TFormedZ()) + 180.0)
					EndIf
				EndIf
			Next
		Next
	Else
		Return(PointDirection(EntityX(n\Collider), EntityZ(n\Collider), EntityX(me\Collider), EntityZ(me\Collider)) + 180.0)
	EndIf
End Function

Function IsInsideElevator%(x1#, y1#, z1#, ElevatorPivot%)
	Local Offset# = 280.0 * RoomScale + (0.015 * fps\Factor[0])
	
	Return(Abs(x1 - EntityX(ElevatorPivot, True)) < Offset And Abs(z1 - EntityZ(ElevatorPivot, True)) < Offset And Abs(y1 - EntityY(ElevatorPivot, True)) < Offset)
End Function

Function CreateLine%(x1#, y1#, z1#, x2#, y2#, z2#, Mesh% = 0)
	Local Surf%, Verts%
	
	If Mesh = 0
		Mesh = CreateMesh()
		EntityFX(Mesh, 16)
		Surf = CreateSurface(Mesh)
		Verts = 0
		
		AddVertex(Surf, x1, y1, z1, 0.0, 0.0)
	Else
		Surf = GetSurface(Mesh, 1)
		Verts = CountVertices(Surf) - 1
	EndIf
	
	AddVertex(Surf, (x1 + x2) / 2.0, (y1 + y2) / 2.0, (z1 + z2) / 2.0, 0.0, 0.0)
	; ~ You could skip creating the above vertex and change the line below to
	; ~ So your line mesh would use less vertices, the drawback is that some videocards (like the matrox g400)
	; ~ Aren't able to create a triangle with 2 vertices. so, it's your call :)
	AddVertex(Surf, x2, y2, z2, 1.0, 0.0)
	
	AddTriangle(Surf, Verts, Verts + 2, Verts + 1)
	
	Return(Mesh)
End Function

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

; ~ Find mesh extents
Function GetMeshExtents%(Mesh%)
	Local su%, s%, v%, x#, y#, z#
	Local MinX# = Infinity
	Local MinY# = Infinity
	Local MinZ# = Infinity
	Local MaxX# = -Infinity
	Local MaxY# = -Infinity
	Local MaxZ# = -Infinity
	Local SurfCount% = CountSurfaces(Mesh)
	Local VertCount%
	
	For su = 1 To SurfCount
		s = GetSurface(Mesh, su)
		VertCount = CountVertices(s) - 1
		
		For v = 0 To VertCount
			x = VertexX(s, v)
			y = VertexY(s, v)
			z = VertexZ(s, v)
			
			MinX = Min(MinX, x)
			MaxX = Max(MaxX, x)
			MinY = Min(MinY, y)
			MaxY = Max(MaxY, y)
			MinZ = Min(MinZ, z)
			MaxZ = Max(MaxZ, z)
		Next
	Next
	
	Mesh_MinX = MinX
	Mesh_MinY = MinY
	Mesh_MinZ = MinZ
	Mesh_MaxX = MaxX
	Mesh_MaxY = MaxY
	Mesh_MaxZ = MaxZ
	Mesh_MagX = MaxX - MinX
	Mesh_MagY = MaxY - MinY
	Mesh_MagZ = MaxZ - MinZ
End Function

; ~ Create a collision box for a mesh entity taking into account entity scale (won't work in non-uniform scaled space)
Function CreateCollBox%(Mesh%)
	Local sX# = EntityScaleX(Mesh, 1)
	Local sY# = Max(EntityScaleY(Mesh, 1), 0.001)
	Local sZ# = EntityScaleZ(Mesh, 1)
	
	GetMeshExtents(Mesh)
	EntityBox(Mesh, Mesh_MinX * sX, Mesh_MinY * sY, Mesh_MinZ * sZ, Mesh_MagX * sX, Mesh_MagY * sY, Mesh_MagZ * sZ)
End Function

Const ZONEAMOUNT% = 3

Function GetZone%(y%)
	Return(Min(Floor((Float(MapGridSize - y) / MapGridSize * ZONEAMOUNT)), ZONEAMOUNT - 1))
End Function

Function CalculateRoomTemplateExtents%(r.RoomTemplates)
	If r\DisableOverlapCheck Then Return
	
	GetMeshExtents(GetChild(r\OBJ, 2))
	r\MinX = Mesh_MinX
	r\MinY = Mesh_MinY
	r\MinZ = Mesh_MinZ
	r\MaxX = Mesh_MaxX
	r\MaxY = Mesh_MaxY
	r\MaxZ = Mesh_MaxZ
End Function

; ~ Shrink the extents slightly, so we don't care if the overlap is smaller than the thickness of the walls
Const ShrinkAmount# = 0.05

Function CalculateRoomExtents%(r.Rooms)
	If r\RoomTemplate\DisableOverlapCheck Then Return
	
	; ~ Convert from the rooms local space to world space
	TFormVector(r\RoomTemplate\MinX, r\RoomTemplate\MinY, r\RoomTemplate\MinZ, r\OBJ, 0)
	r\MinX = TFormedX()
	r\MinY = TFormedY()
	r\MinZ = TFormedZ()
	
	; ~ Convert from the rooms local space to world space
	TFormVector(r\RoomTemplate\MaxX, r\RoomTemplate\MaxY, r\RoomTemplate\MaxZ, r\OBJ, 0)
	r\MaxX = TFormedX()
	r\MaxY = TFormedY()
	r\MaxZ = TFormedZ()
	
	If r\MinX > r\MaxX
		Local TempX# = r\MaxX
		
		r\MaxX = r\MinX
		r\MinX = TempX
	EndIf
	If r\MinZ > r\MaxZ
		Local TempZ# = r\MaxZ
		
		r\MaxZ = r\MinZ
		r\MinZ = TempZ
	EndIf
	
	r\MinX = r\MinX + ShrinkAmount + r\x
	r\MinY = r\MinY + ShrinkAmount
	r\MinZ = r\MinZ + ShrinkAmount + r\z
	
	r\MaxX = r\MaxX - ShrinkAmount - r\x
	r\MaxY = r\MaxY - ShrinkAmount
	r\MaxZ = r\MaxZ - ShrinkAmount - r\z
End Function

Function CheckRoomOverlap%(r1.Rooms, r2.Rooms)
	If r1\MaxX <= r2\MinX Lor r1\MaxY <= r2\MinY Lor r1\MaxZ <= r2\MinZ Then Return(False)
	If r1\MinX >= r2\MaxX Lor r1\MinY >= r2\MaxY Lor r1\MinZ >= r2\MaxZ Then Return(False)
	
	Return(True)
End Function

Function IsInFacility%(y#)
	Local Y2# = Floor(y)
	
	If Y2 < -6.5
		Return(LowerFloor)
	ElseIf Y2 > 6.5 And Y2 <= 100.0
		Return(UpperFloor)
	ElseIf Y2 > 100.0 And Y2 < 700.0
		Return(FloorOther)
	ElseIf Y2 > 400.0
		Return(Floor1499)
	EndIf
	Return(NullFloor)
End Function

; ~ This must be called after the room angle has been finalized!
;Function SetupTriggerBoxes%(r.Rooms)
;	Local t.TriggerBox
;	Local sX#, sY#, sZ#
;	Local pXMin#, pXMax#
;	Local pZMin#, pZMax#
;	Local i%
;	Local SinValue#, CosValue#
;	
;	For i = 0 To r\TriggerBoxAmount - 1
;		t = r\TriggerBoxes[i]
;		sX = EntityScaleX(t\OBJ, 1)
;		sY = Max(EntityScaleY(t\OBJ, 1), 0.001)
;		sZ = EntityScaleZ(t\OBJ, 1)
;		
;		GetMeshExtents(t\OBJ)
;		
;		SinValue = Sin(r\Angle)
;		CosValue = Cos(r\Angle)
;		
;		pXMin = CosValue * sX * Mesh_MinX - SinValue * sZ * Mesh_MinZ + r\x
;		pZMin = SinValue * sX * Mesh_MinX + CosValue * sZ * Mesh_MinZ + r\z
;		
;		pXMax = CosValue * sX * Mesh_MaxX - SinValue * sZ * Mesh_MaxZ + r\x
;		pZMax = SinValue * sX * Mesh_MaxX + CosValue * sZ * Mesh_MaxZ + r\z
;		
;		If pXMin > pXMax
;			t\MinX = pXMax
;			t\MaxX = pXMin
;		Else
;			t\MinX = pXMin
;			t\MaxX = pXMax
;		EndIf
;		
;		If pZMin > pZMax
;			t\MinZ = pZMax
;			t\MaxZ = pZMin
;		Else
;			t\MinZ = pZMin
;			t\MaxZ = pZMax
;		EndIf
;		
;		t\MinY = ((sY * Mesh_MinY) + r\y)
;		t\MaxY = ((sY * Mesh_MaxY) + r\y)
;	Next
;End Function

;Function CheckTriggers$()
;	Local i%
;	
;	If PlayerRoom\TriggerBoxAmount = 0
;		Return
;	Else
;		For i = 0 To PlayerRoom\TriggerBoxAmount - 1
;			If chs\DebugHUD <> 0
;				EntityAlpha(PlayerRoom\TriggerBoxes[i]\OBJ, 0.2)
;			Else
;				EntityAlpha(PlayerRoom\TriggerBoxes[i]\OBJ, 0.0)
;			EndIf
;			
;			If EntityX(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinX And EntityX(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxX
;				If EntityY(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinY And EntityY(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxY
;					If EntityZ(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinZ And EntityZ(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxZ
;						Return(PlayerRoom\TriggerBoxes[i]\Name)
;					EndIf
;				EndIf
;			EndIf
;		Next
;	EndIf
;End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS