Function WrapAngle#(Angle#)
	If Angle = Infinity Then Return(0.0)
	If Angle < 0.0
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

;~IDEal Editor Parameters:
;~C#Blitz3D TSS