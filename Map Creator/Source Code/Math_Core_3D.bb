Function WrapAngle#(Angle#)
	If Angle = Infinity Then Return(0.0)
	If Angle < 0.0 Then
		Return(360.0 + (Angle Mod 360.0))
	Else
		Return(Angle Mod 360.0)
	EndIf
End Function

Const ZONEAMOUNT% = 3

Function GetZone%(y%)
	Return(Min(Floor((Float(MapGridSize - y) / MapGridSize * ZONEAMOUNT)), ZONEAMOUNT - 1))
End Function

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

Function GetMeshExtents%(Mesh%)
	Local su%, s%, i%, x#, y#, z#
	Local MinX# = Infinity
	Local MinY# = Infinity
	Local MinZ# = Infinity
	Local MaxX# = -Infinity
	Local MaxY# = -Infinity
	Local MaxZ# = -Infinity
	
	For su = 1 To CountSurfaces(Mesh)
		s = GetSurface(Mesh, su)
		For i = 0 To CountVertices(s) - 1
			x = VertexX(s, i)
			y = VertexY(s, i)
			z = VertexZ(s, i)
			TFormPoint(x, y, z, Mesh, 0)
			x = TFormedX()
			y = TFormedY()
			z = TFormedZ()
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

;~IDEal Editor Parameters:
;~C#Blitz3D TSS