Global Ark_Blur_Image%, Ark_Blur_Texture%, Ark_Sw%, Ark_Sh%
Global Ark_Blur_Cam%

Function CreateBlurImage()
	; ~ Create blur Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0, 0, 10000)
	Ark_Blur_Cam = Cam
	
	Ark_Sw = GraphicWidth
	Ark_Sh = GraphicHeight
	CameraViewport(Cam, 0, 0, Ark_Sw, Ark_Sh)
	
	; ~ Create sprite
	Local SPR% = CreateMesh(Cam)
	Local SF% = CreateSurface(SPR)
	
	AddVertex(SF, -1, 1, 0, 0, 0)
	AddVertex(SF, 1, 1, 0, 1, 0)
	AddVertex(SF, -1, -1, 0, 0, 1)
	AddVertex(SF, 1, -1, 0, 1, 1)
	AddTriangle(SF, 0, 1, 2)
	AddTriangle(SF, 3, 2, 1)
	EntityFX(SPR, 17)
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(Ark_Sw), SMALLEST_POWER_TWO / Float(Ark_Sw), 1)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	Ark_Blur_Image = SPR
	
	; ~ Create blur texture
	Ark_Blur_Texture = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 256)
	EntityTexture(SPR, Ark_Blur_Texture)
End Function

Function UpdateBlur(Power#)
	EntityAlpha(Ark_Blur_Image, Power#)
	CopyRect(0, 0, Ark_Sw, Ark_Sh, SMALLEST_POWER_TWO_HALF - (Ark_Sw / 2), SMALLEST_POWER_TWO_HALF - (Ark_Sh / 2), BackBuffer(), TextureBuffer(Ark_Blur_Texture))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D