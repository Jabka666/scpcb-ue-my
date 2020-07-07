Function GenerateSeedNumber(Seed$)
 	Local Temp% = 0
 	Local Shift% = 0
	Local i%
	
 	For i = 1 To Len(Seed)
 		Temp = Temp Xor (Asc(Mid(Seed, i, 1)) Shl Shift)
 		Shift = (Shift + 1) Mod 24
	Next
	Return(Temp)
End Function

Function CurveValue#(Number#, Old#, Smooth#)
	If fpst\FPSFactor[0] = 0.0 Then Return(Old)
	
	If Number < Old Then
		Return(Max(Old + (Number - Old) * (1.0 / Smooth * fpst\FPSFactor[0]), Number))
	Else
		Return(Min(Old + (Number - Old) * (1.0 / Smooth * fpst\FPSFactor[0]), Number))
	EndIf
End Function

Function CurveAngle#(Val#, Old#, Smooth#)
	If fpst\FPSFactor[0] = 0.0 Then Return(Old)
	
	Local Diff# = WrapAngle(Val) - WrapAngle(Old)
	
	If Diff > 180.0 Then Diff = Diff - 360.0
	If Diff < - 180.0 Then Diff = Diff + 360.0
	Return(WrapAngle(Old + Diff * (1.0 / Smooth * fpst\FPSFactor[0])))
End Function

Function WrapAngle#(Angle#)
	If Angle = INFINITY Then Return(0.0)
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

Function f2s$(n#, Count%)
	Return(Left(n, Len(Int(n)) + Count + 1))
End Function

Function Chance%(Chanc%)
	; ~ Perform a chance given a probability
	Return(Rand(0, 100) =< Chanc)
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
		Dir = (Dir + 2) Mod 4
		Deviated = True
	EndIf
	If RetVal = 0 Then 
		Return(Dir) 
	Else 
		Return(Deviated)
	EndIf
End Function

; ~ Find mesh extents
Function GetMeshExtents(Mesh%)
	Local s%, Surf%, Surfs%, v%, Verts%, x#, y#, z#
	Local MinX# = INFINITY
	Local MinY# = INFINITY
	Local MinZ# = INFINITY
	Local MaxX# = -INFINITY
	Local MaxY# = -INFINITY
	Local MaxZ# = -INFINITY
	
	Surfs = CountSurfaces(Mesh)
	
	For s = 1 To Surfs
		Surf = GetSurface(Mesh, s)
		Verts = CountVertices(Surf)
		For v = 0 To Verts - 1
			x = VertexX(Surf, v)
			y = VertexY(Surf, v)
			z = VertexZ(Surf, v)
			
			If x < MinX Then MinX = x
			If x > MaxX Then MaxX = x
			If y < MinY Then MinY = y
			If y > MaxY Then MaxY = y
			If z < MinZ Then MinZ = z
			If z > MaxZ Then MaxZ = z
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

;~IDEal Editor Parameters:
;~C#Blitz3D