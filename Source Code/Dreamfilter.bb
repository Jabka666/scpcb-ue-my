Global ark_blur_image%, ark_blur_texture%, ark_sw%, ark_sh%
Global ark_blur_cam%

Function CreateBlurImage()
	; ~ Create blur Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0, 0, 10000)
	ark_blur_cam = Cam
	
	ark_sw = GraphicWidth
	ark_sh = GraphicHeight
	CameraViewport(Cam, 0, 0, ark_sw, ark_sh)
	
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
	ScaleEntity(SPR, 2048.0 / Float(ark_sw), 2048.0 / Float(ark_sw), 1)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ark_blur_image = SPR
	
	; ~ Create blur texture
	ark_blur_texture = CreateTexture(2048, 2048, 256)
	EntityTexture(SPR, ark_blur_texture)
End Function

Function UpdateBlur(Power#)
	EntityAlpha(ark_blur_image, Power#)
	CopyRect(0, 0, GraphicWidth, GraphicHeight, 1024.0 - GraphicWidth / 2, 1024.0 - GraphicHeight / 2, BackBuffer(), TextureBuffer(ark_blur_texture))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D