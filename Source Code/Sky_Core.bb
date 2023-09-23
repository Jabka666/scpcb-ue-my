Global Sky%

Function CreateSky%(FileName$, Parent% = 0)
	Local Sky%, Face%, Direction$, Vert%
	Local s%, x%, y%, z%, u%, v%

	Restore sky_SkyboxData
	Sky = CreateMesh(Parent)
	For Face = 1 To 6
		Read Direction
		
		Local FName$ = FileName + Direction + ".png"
		
		If FileType(FName) = 1
			Local b% = LoadBrush_Strict(FName, %110001)
			
			s = CreateSurface(Sky, b)
			For Vert = 1 To 4
				Read x, y, z, u, v
				AddVertex(s, x, y, z, u, v)
			Next
			AddTriangle(s, 0, 1, 2)
			AddTriangle(s, 0, 2, 3)
			FreeBrush(b) : b = 0
		EndIf
	Next
	FlipMesh(Sky)
	EntityFX(Sky, 1 + 8)
	EntityOrder(Sky, 1000)
	Return(Sky)
End Function

Function UpdateSky%(SkyOBJ%)
	PositionEntity(SkyOBJ, EntityX(Camera), EntityY(Camera), EntityZ(Camera), True)
End Function

.sky_SkyboxData
Data "_back"
Data -1, +1, -1, 0, 0
Data +1, +1, -1, 1, 0
Data +1, -1, -1, 1, 1
Data -1, -1, -1, 0, 1
Data "_left"
Data +1, +1, -1, 0, 0
Data +1, +1, +1, 1, 0
Data +1, -1, +1, 1, 1
Data +1, -1, -1, 0, 1
Data "_front"
Data +1, +1, +1, 0, 0
Data -1, +1, +1, 1, 0
Data -1, -1, +1, 1, 1
Data +1, -1, +1, 0, 1
Data "_right"
Data -1, +1, +1, 0, 0
Data -1, +1, -1, 1, 0
Data -1, -1, -1, 1, 1
Data -1, -1, +1, 0, 1
Data "_up"
Data -1, +1, +1, 0, 0
Data +1, +1, +1, 1, 0
Data +1, +1, -1, 1, 1
Data -1, +1, -1, 0, 1
Data "_down"
Data -1, -1, -1, 1, 0
Data +1, -1, -1, 1, 1
Data +1, -1, +1, 0, 1
Data -1, -1, +1, 0, 0

;~IDEal Editor Parameters:
;~C#Blitz3D TSS