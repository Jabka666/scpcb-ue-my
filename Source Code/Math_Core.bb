Function GenerateSeedNumber(Seed$)
 	Local Temp% = 0
 	Local Shift% = 0
	Local i%
	
 	For i = 1 To Len(Seed)
 		Temp = Temp Xor (Asc(Mid(Seed, i, 1)) Shl Shift)
 		Shift = ((Shift + 1) Mod 24)
	Next
	Return(Temp)
End Function

Function CurveValue#(Number#, Old#, Smooth#)
	If fps\FPSFactor[0] = 0.0 Then Return(Old)
	
	If Number < Old Then
		Return(Max(Old + (Number - Old) * (1.0 / Smooth * fps\FPSFactor[0]), Number))
	Else
		Return(Min(Old + (Number - Old) * (1.0 / Smooth * fps\FPSFactor[0]), Number))
	EndIf
End Function

Function CurveAngle#(Value#, Old#, Smooth#)
	If fps\FPSFactor[0] = 0.0 Then Return(Old)
	
	Local Diff# = WrapAngle(Value) - WrapAngle(Old)
	
	If Diff > 180.0 Then Diff = Diff - 360.0
	If Diff < -180.0 Then Diff = Diff + 360.0
	Return(WrapAngle(Old + Diff * (1.0 / Smooth * fps\FPSFactor[0])))
End Function

Function WrapAngle#(Angle#)
	If Angle = Infinity Then Return(0.0)
	If Angle < 0.0 Then
		Return(360.0 + (Angle Mod 360.0))
	Else
		Return(Angle Mod 360.0)
	EndIf
End Function

Function PointDirection#(x1#, z1#, x2#, z2#)
	Local dx#, dz#
	
	dx = x1 - x2
	dz = z1 - z2
	
	Return(ATan2(dz, dx))
End Function

Function AngleDist#(a0#, a1#)
	Local b# = a0 - a1
	Local bb#
	
	If b < -180.0 Then
		bb = b + 360.0
	ElseIf b > 180.0 Then
		bb = b - 360.0
	Else
		bb = b
	EndIf
	
	Return(bb)
End Function

Function ScaledMouseX%()
	Return(Float(MouseX() - (opt\RealGraphicWidth * 0.5 * (1.0 - opt\AspectRatio))) * Float(opt\GraphicWidth) / Float(opt\RealGraphicWidth * opt\AspectRatio))
End Function

Function ScaledMouseY%()
	Return(Float(MouseY()) * Float(opt\GraphicHeight) / Float(opt\RealGraphicHeight))
End Function

Function MouseOn%(x%, y%, Width%, Height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + Width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + Height Then
			Return(True)
		EndIf
	EndIf
	Return(False)
End Function

Function f2s$(n#, Count%)
	Return(Left(n, Len(Int(n)) + Count + 1))
End Function

Function Chance%(Percent%)
	; ~ Perform a chance given a probability
	Return(Rand(0, 100) =< Percent)
End Function

Function MoveForward%(Dir%, PathX%, PathY%, RetVal% = 0)
	; ~ Move 1 unit along the grid in the designated direction
	If Dir = 1 Then
		If RetVal = 0 Then
			Return(PathX)
		Else
			Return(PathY + 1)
		EndIf
	EndIf
	If RetVal = 0 Then
		Return(PathX - 1 + Dir)
	Else
		Return(PathY)
	EndIf
End Function

Function TurnIfDeviating%(Max_Deviation_Distance_%, Pathx%, Center_%, Dir%, RetVal% = 0)
	; ~ Check if deviating and return the answer. if deviating, turn around
	Local Current_Deviation% = Center_ - Pathx
	Local Deviated% = False
	
	If (Dir = 0 And Current_Deviation >= Max_Deviation_Distance_) Lor (Dir = 2 And Current_Deviation =< -Max_Deviation_Distance_) Then
		Dir = ((Dir + 2) Mod 4)
		Deviated = True
	EndIf
	If RetVal = 0 Then 
		Return(Dir) 
	Else 
		Return(Deviated)
	EndIf
End Function

Function ReadPixelColor%(Pixel%, Shrid%)
	Return(Pixel Shr Shrid) And $FF
End Function

Function ChangeAngleValueForCorrectBoneAssigning%(Value#)
	Local Number#
	
	If Value =< 180.0
		Number = Value
	Else
		Number = (-360.0) + Value
	EndIf
	Return(Number)
End Function

Function Find860Angle(n.NPCs, fr.Forest)
	TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, fr\Forest_Pivot)
	
	Local PlayerX# = Floor((TFormedX() + 6.0) / 12.0)
	Local PlayerZ# = Floor((TFormedZ() + 6.0) / 12.0)
	
	TFormPoint(EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider), 0, fr\Forest_Pivot)
	
	Local x# = (TFormedX() + 6.0) / 12.0
	Local z# = (TFormedZ() + 6.0) / 12.0
	Local xt% = Floor(x), zt% = Floor(z)
	Local x2%, z2%
	
	If xt <> PlayerX Lor zt <> PlayerZ Then ; ~ The monster is not on the same tile as the player
		For x2 = Max(xt - 1, 0) To Min(xt + 1, GridSize - 1)
			For z2 = Max(zt - 1, 0) To Min(zt + 1, GridSize - 1)
				If fr\grid[(z2 * GridSize) + x2] > 0 And (x2 <> xt Lor z2 <> zt) And (x2 = xt Lor z2 = zt) Then
					; ~ Tile (x2, z2) is closer to the player than the monsters current tile
					If (Abs(PlayerX - x2) + Abs(PlayerZ - z2)) < (Abs(PlayerX - xt) + Abs(PlayerZ - zt)) Then
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

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

; ~ Find mesh extents
Function GetMeshExtents(Mesh%)
	Local su%, s%, v%, x#, y#, z#
	Local MinX# = Infinity
	Local MinY# = Infinity
	Local MinZ# = Infinity
	Local MaxX# = -Infinity
	Local MaxY# = -Infinity
	Local MaxZ# = -Infinity
	
	For su = 1 To CountSurfaces(Mesh)
		s = GetSurface(Mesh, su)
		For v = 0 To CountVertices(s) - 1
			x = VertexX(s, v)
			y = VertexY(s, v)
			z = VertexZ(s, v)
			If x > MaxX Then MaxX = x
			If x < MinX Then MinX = x
			If y > MaxY Then MaxY = y
			If y < MinY Then MinY = y
			If z > MaxZ Then MaxZ = z
			If z < MinZ Then MinZ = z
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
Function MakeCollBox(Mesh%)
	Local sX# = EntityScaleX(Mesh, 1)
	Local sY# = Max(EntityScaleY(Mesh, 1), 0.001)
	Local sZ# = EntityScaleZ(Mesh, 1)
	
	GetMeshExtents(Mesh)
	EntityBox(Mesh, Mesh_MinX * sX, Mesh_MinY * sY, Mesh_MinZ * sZ, Mesh_MagX * sX, Mesh_MagY * sY, Mesh_MagZ * sZ)
End Function

Const ZONEAMOUNT% = 3

Function GetZone%(y%)
	Return(Min(Floor((Float(MapSize - y) / MapSize * ZONEAMOUNT)), ZONEAMOUNT - 1))
End Function

Function CalculateRoomTemplateExtents(r.RoomTemplates)
	If r\DisableOverlapCheck Then Return
	
	GetMeshExtents(GetChild(r\OBJ, 2))
	r\MinX = Mesh_MinX
	r\MinY = Mesh_MinY
	r\MinZ = Mesh_MinZ
	r\MaxX = Mesh_MaxX
	r\MaxY = Mesh_MaxY
	r\MaxZ = Mesh_MaxZ
End Function

; ~ Shrink the extents slightly - we don't care if the overlap is smaller than the thickness of the walls
Const ShrinkAmount# = 0.05

Function CalculateRoomExtents(r.Rooms)
	If r\RoomTemplate\DisableOverlapCheck Then Return
	
	; ~ Convert from the rooms local space to world space
	TFormVector(r\RoomTemplate\MinX, r\RoomTemplate\MinY, r\RoomTemplate\MinZ, r\OBJ, 0)
	r\MinX = TFormedX() + ShrinkAmount + r\x
	r\MinY = TFormedY() + ShrinkAmount
	r\MinZ = TFormedZ() + ShrinkAmount + r\z
	
	; ~ Convert from the rooms local space to world space
	TFormVector(r\RoomTemplate\MaxX, r\RoomTemplate\MaxY, r\RoomTemplate\MaxZ, r\OBJ, 0)
	r\MaxX = TFormedX() - ShrinkAmount + r\x
	r\MaxY = TFormedY() - ShrinkAmount
	r\MaxZ = TFormedZ() - ShrinkAmount + r\z
	
	If r\MinX > r\MaxX Then
		Local TempX# = r\MaxX
		
		r\MaxX = r\MinX
		r\MinX = TempX
	EndIf
	If r\MinZ > r\MaxZ Then
		Local TempZ# = r\MaxZ
		
		r\MaxZ = r\MinZ
		r\MinZ = TempZ
	EndIf
End Function

Function CheckRoomOverlap(r1.Rooms, r2.Rooms)
	If r1\MaxX =< r2\MinX Lor r1\MaxY =< r2\MinY Lor r1\MaxZ =< r2\MinZ Then Return(False)
	If r1\MinX >= r2\MaxX Lor r1\MinY >= r2\MaxY Lor r1\MinZ >= r2\MaxZ Then Return(False)
	
	Return(True)
End Function

Function CalculateSubtitlesDistance(SubID%, Dist#)
	If (Not opt\EnableSubtitles) Then Return
	
	Local sub.Subtitles
	
	For sub.Subtitles = Each Subtitles
		If sub\Dist[SubID] = Dist Then
			Exit
		Else
			sub\Dist[SubID] = Dist
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D