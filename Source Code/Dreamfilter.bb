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
	
	ArkSw = opt\GraphicWidth
	ArkSh = opt\GraphicHeight
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
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(ArkSw), SMALLEST_POWER_TWO / Float(ArkSw), 1)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ArkBlurImage = SPR
	
	; ~ Create blur texture
	ArkBlurTexture = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 0)
	EntityTexture(SPR, ArkBlurTexture)
End Function

Function UpdateBlur(Power#)
	EntityAlpha(ArkBlurImage, Power)
	CopyRect(0, 0, ArkSw, ArkSh, SMALLEST_POWER_TWO_HALF - (ArkSw / 2), SMALLEST_POWER_TWO_HALF - (ArkSh / 2), BackBuffer(), TextureBuffer(ArkBlurTexture))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D