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
	ScaleEntity(SPR, 2048.0 / Float(Ark_Sw), 2048.0 / Float(Ark_Sw), 1)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	Ark_Blur_Image = SPR
	
	; ~ Create blur texture
	Ark_Blur_Texture = CreateTexture(2048, 2048, 256)
	EntityTexture(SPR, Ark_Blur_Texture)
End Function

Function UpdateBlur(Power#)
	EntityAlpha(Ark_Blur_Image, Power#)
	CopyRect(0, 0, GraphicWidth, GraphicHeight, 1024 - GraphicWidth / 2, 1024 - GraphicHeight / 2, BackBuffer(), TextureBuffer(Ark_Blur_Texture))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D