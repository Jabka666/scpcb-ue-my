Global ArkBlurImage%, ArkBlurTexture%, ArkSw%, ArkSh%
Global ArkBlurCam%

Function CreateBlurImage()
	; ~ Create blur Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0, 0, 10000)
	ArkBlurCam = Cam
	
	ArkSw = GraphicWidth
	ArkSh = GraphicHeight
	CameraViewport(Cam, 0, 0, ArkSw, ArkSh)
	
	; ~ Create sprite
	Local SPR% = CreateMesh(Cam)
	Local SF% = CreateSurface(SPR)
	
	AddVertex(SF, -1.0, 1.0, 0.0, 0.0, 0.0)
	AddVertex(SF, 1.0, 1.0, 0.0, 1.0, 0.0)
	AddVertex(SF, -1.0, -1.0, 0.0, 0.0, 1.0)
	AddVertex(SF, 1.0, -1.0, 0.0, 1.0, 1.0)
	AddTriangle(SF, 0, 1, 2)
	AddTriangle(SF, 3, 2, 1)
	EntityFX(SPR, 17)
	ScaleEntity(SPR, 2048.0 / Float(ArkSw), 2048.0 / Float(ArkSw), 1)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ArkBlurImage = SPR
	
	; ~ Create blur texture
	ArkBlurTexture = CreateTextureUsingCacheSystem(2048, 2048, 0)
	EntityTexture(SPR, ArkBlurTexture)
End Function

Function UpdateBlur(Power#)
	EntityAlpha(ArkBlurImage, Power#)
	CopyRect(0, 0, ArkSw, ArkSh, 1024 - ArkSw / 2, 1024 - ArkSh / 2, BackBuffer(), TextureBuffer(ArkBlurTexture))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D