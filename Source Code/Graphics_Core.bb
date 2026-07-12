Global FresizeImage%, FresizeTexture%, FresizeTexture2%
Global FresizeCam%

Global ResizeQuad%, ResizeTexture%, ResizeImageCamera%

Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

Global GammaEffect%
Global ItemSpecularFX%

Function CreateQuad%()
	Local Quad% = CreateMesh()
	Local SF% = CreateSurface(Quad)
	Local v0% = AddVertex(SF, -1.0, 1.0, 0.0, 0.0, 0.0)
	Local v1% = AddVertex(SF, 1.0, 1.0, 0.0, 1.0, 0.0)
	Local v2% = AddVertex(SF, 1.0, -1.0, 0.0, 1.0, 1.0)
	Local v3% = AddVertex(SF, -1.0, -1.0, 0.0, 0.0, 1.0)
	
	AddTriangle(SF, v0, v1, v2)
	AddTriangle(SF, v0, v2, v3)
	UpdateNormals(Quad)
	Return(Quad)
End Function

Function InitFastResize%()
	; ~ Create and configure a camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0.0, 0.0, -10000.0)
	
	FresizeCam = Cam
	
	; ~ Create and configure a sprite
	Local SPR% = CreateMesh(Cam)
	Local SF% = CreateSurface(SPR)
	
	AddVertex(SF, -1.0, 1.0, 0.0, 0.0, 0.0)
	AddVertex(SF, 1.0, 1.0, 0.0, 1.0, 0.0)
	AddVertex(SF, -1.0, -1.0, 0.0, 0, 1.0)
	AddVertex(SF, 1.0, -1.0, 0.0, 1.0, 1.0)
	AddTriangle(SF, 0, 1, 2)
	AddTriangle(SF, 3, 2, 1)
	EntityFX(SPR, 17)
	ScaleEntity(SPR, SMALLEST_POWER_TWO / GraphicWidthFloat, SMALLEST_POWER_TWO / GraphicHeightFloat, 1.0)
	PositionEntity(SPR, 0.0, 0.0, 1.0001)
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	FresizeImage = SPR
	
	; ~ Create texture and associate it with the sprite
	FresizeTexture = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256)
	FresizeTexture2 = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256)
	TextureBlend(FresizeTexture2, 3)
	SetBuffer(TextureBuffer(FresizeTexture2))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	EntityTexture(SPR, FresizeTexture, 0, 0)
	EntityTexture(SPR, FresizeTexture2, 0, 1)
	
	; ~ Hide the camera until needed
	HideEntity(FresizeCam)
	
	; ~ Create another texture for image scaling
	ResizeTexture = CreateTexture(Max(SMALLEST_POWER_TWO, 2048.0), Max(SMALLEST_POWER_TWO, 2048.0), 16 + 32 + 256)
	
	ResizeImageCamera = CreateCamera()
	CameraRange(ResizeImageCamera, 0.1, 1.5)
	TranslateEntity(ResizeImageCamera, (1.0 / Float(TextureWidth(ResizeTexture))), -(1.0 / Float(TextureHeight(ResizeTexture))), -1.0)
	
	ResizeQuad = CreateQuad()
	EntityTexture(ResizeQuad, ResizeTexture)
	EntityFX(ResizeQuad, 1)
	EntityParent(ResizeQuad, ResizeImageCamera)
	MoveEntity(ResizeImageCamera, 0.0, 0.0, -10000.0)
	HideEntity(ResizeImageCamera)

	GammaEffect = LoadEffect("GFX\Shaders\Gamma.fx")
	If GammaEffect = 0 Then RuntimeError("Failed to load Gamma.fx")
	ItemSpecularFX = LoadEffect("GFX\Shaders\ItemSpecular.fx")
	If ItemSpecularFX = 0 Then RuntimeError("Failed to load ItemSpecular.fx")
End Function

Function Graphics3DEx%(Width%, Height%, Depth% = 32, Mode% = 2)
	SetGfxDriver(opt\GFXDriver)
	Graphics3D(Width, Height, Depth, Mode)
	TextureFilter("", 8192) ; ~ This turns on Anisotropic filtering for textures
	SMALLEST_POWER_TWO = 512.0
	While SMALLEST_POWER_TWO < Width Lor SMALLEST_POWER_TWO < Height
		SMALLEST_POWER_TWO = SMALLEST_POWER_TWO * 2.0
	Wend
	SMALLEST_POWER_TWO_HALF = SMALLEST_POWER_TWO / 2.0
	InitFastResize()
End Function

Function ScaleImageEx%(SrcImage%, ScaleX#, ScaleY#, Frames% = 1)
    Local SrcWidth%  = ImageWidth(SrcImage)
    Local SrcHeight% = ImageHeight(SrcImage)
    Local DestWidth%  = Floor(SrcWidth * ScaleX)
    Local DestHeight% = Floor(SrcHeight * ScaleY)
    
    If SrcWidth = DestWidth And SrcHeight = DestHeight Then Return SrcImage
    
    Local DestImage% = CreateImage(DestWidth, DestHeight, Frames)
    Local f%
    
    For f = 0 To Frames - 1
        CopyRectStretch(0, 0, SrcWidth, SrcHeight, 0, 0, DestWidth, DestHeight, ImageBuffer(SrcImage, f), ImageBuffer(DestImage, f))
        If opt\DisplayMode = 0 Then BufferDirty(ImageBuffer(DestImage, f))
    Next
    
    FreeImage SrcImage
    Return DestImage
End Function

Function RenderImage(WidthScale#, HeightScale#)
	If Camera <> 0 Then HideEntity(Camera)
	WireFrame(0)
	ScaleEntity(ResizeQuad, WidthScale, HeightScale, 0.01)
	ShowEntity(ResizeImageCamera)
	RenderWorld()
	HideEntity(ResizeImageCamera)
	WireFrame(WireFrameState)
	If Camera <> 0 Then ShowEntity(Camera)
End Function

Function ResizeImageEx%(SrcImage%, ScaleX#, ScaleY#, Frames% = 1)
    Local SrcWidth%  = ImageWidth(SrcImage)
    Local SrcHeight% = ImageHeight(SrcImage)
    Local DestWidth%  = Floor(SrcWidth * ScaleX)
    Local DestHeight% = Floor(SrcHeight * ScaleY)
    
    If SrcWidth = DestWidth And SrcHeight = DestHeight Then Return SrcImage
    
    Local DestImage% = CreateImage(DestWidth, DestHeight, Frames)
    Local f%
    
    For f = 0 To Frames - 1
        CopyRectStretch(0, 0, SrcWidth, SrcHeight, 0, 0, DestWidth, DestHeight, ImageBuffer(SrcImage, f), ImageBuffer(DestImage, f))
        If opt\DisplayMode = 0 Then BufferDirty(ImageBuffer(DestImage, f))
    Next
    
    FreeImage SrcImage
    Return DestImage
End Function

Function RescaleTexture%(SrcTexture%, ScaleX#, ScaleY#, Flags% = 1)
    Local SrcWidth%  = TextureWidth(SrcTexture)
    Local SrcHeight% = TextureHeight(SrcTexture)
    Local DestWidth%  = Floor(SrcWidth * ScaleX)
    Local DestHeight% = Floor(SrcHeight * ScaleY)
    
    Local DestTexture% = CreateTexture(DestWidth, DestHeight, Flags)
    
    CopyRectStretch(0, 0, SrcWidth, SrcHeight, 0, 0, DestWidth, DestHeight, TextureBuffer(SrcTexture), TextureBuffer(DestTexture))
    
    FreeTexture SrcTexture
    Return DestTexture
End Function

Function GetLightingSize#(Quality%)
	Select Quality
		Case 2
			;[Block]
			Return(1.0)
			;[End Block]
		Case 1
			;[Block]
			Return(0.5)
			;[End Block]
		Case 0
			;[Block]
			Return(0.25)
			;[End Block]
	End Select
End Function

Function ScaleRender%(x#, y#, HeightScale# = 1.0, WidthScale# = 1.0)
	If Camera <> 0 Then HideEntity(Camera)
	WireFrame(0)
	ShowEntity(FresizeImage)
	ScaleEntity(FresizeImage, HeightScale, WidthScale, 1.0)
	PositionEntity(FresizeImage, x, y, 1.0001)
	ShowEntity(FresizeCam)
	RenderWorld()
	HideEntity(FresizeCam)
	HideEntity(FresizeImage)
	WireFrame(WireFrameState)
	If Camera <> 0 Then ShowEntity(Camera)
End Function

Function RenderGamma%()
	If opt\ScreenGamma = 1.0 Then Return

	Local RenderScale# = 1.0 / GraphicWidthFloat
	Local Ratio# = SMALLEST_POWER_TWO / GraphicWidthFloat

	CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(FresizeTexture))

	SetEntityEffect(FresizeImage, GammaEffect)
	SetEffectFloat(GammaEffect, "gammaValue", opt\ScreenGamma)
	SetEffectTexture(GammaEffect, "SceneTex", FresizeTexture)

	EntityFX(FresizeImage, 1)
	EntityBlend(FresizeImage, 1)
	EntityAlpha(FresizeImage, 1.0)

	ScaleRender(-RenderScale, RenderScale, Ratio, Ratio)

	SetEntityEffect(FresizeImage, 0)

	EntityFX(FresizeImage, 1)
	EntityBlend(FresizeImage, 1)
	EntityAlpha(FresizeImage, 1.0)
End Function

Global CurrTrisAmount%

Function RenderWorldEx%(Tween#)
	CameraProjMode(ArkBlurCam, 0)
	CameraProjMode(Camera, 1)
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	If (Not wi\IsNVGBlinking) Then RenderWorld(Tween)
	
	CurrTrisAmount = TrisRendered()
	
	CameraProjMode(ArkBlurCam, 2)
	CameraProjMode(Camera, 0)
	If (Not wi\IsNVGBlinking) Then RenderWorld(Tween)
	CameraProjMode(ArkBlurCam, 0)
End Function

Global ArkBlurImage%, ArkBlurTexture%
Global ArkBlurCam%

Function CreateBlurImage%()
	; ~ Create blur Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0.0, 0.0, 10000.0)
	CameraProjMode(Cam, 0)
	ArkBlurCam = Cam
	
	CameraViewport(Cam, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	
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
	ScaleEntity(SPR, SMALLEST_POWER_TWO / GraphicWidthFloat, SMALLEST_POWER_TWO / GraphicWidthFloat, 1.0)
	PositionEntity(SPR, 0.0, 0.0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ArkBlurImage = SPR
	
	; ~ Create blur texture
	ArkBlurTexture = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256)
	EntityTexture(SPR, ArkBlurTexture)
End Function

Function RenderBlur%(Power#)
	EntityAlpha(ArkBlurImage, Power)
	CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(ArkBlurTexture))
End Function

Function FreeBlur%()
	ArkBlurTexture = 0
	FreeEntity(ArkBlurImage) : ArkBlurImage = 0
	FreeEntity(ArkBlurCam) : ArkBlurCam = 0
End Function

Function UpdateItemSpecular()
	If ItemSpecularFX = 0 Then Return
	SetEffectVector(ItemSpecularFX, "CameraPos", EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 0.0)
	SetEffectVector(ItemSpecularFX, "AmbientColor", fog\AmbientR / 255.0, fog\AmbientG / 255.0, fog\AmbientB / 255.0, 0.0)
	SetEffectVector(ItemSpecularFX, "LightPos", EntityX(Camera, True), EntityY(Camera, True) + 1.0, EntityZ(Camera, True), 0.0)
	SetEffectVector(ItemSpecularFX, "LightColor", 1.0, 1.0, 1.0, 0.0)
	SetEffectFloat(ItemSpecularFX, "LightRange", 12.0)
End Function

Function PlayMovie%(MoviePath$)
	If RunningOnWine() Then Return
	If (Not opt\PlayStartup) Then Return
	
	HidePointer()
	
	fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"))
	
	Local ScaledGraphicHeight%
	; ~ The aspect ratio to target
	Local TargetAspectRatio# = 16.0 / 9.0
	; ~ Calculate the target height based on the screen's aspect ratio
	Local Ratio# = GraphicWidthFloat / GraphicHeightFloat
	
	If Ratio > TargetAspectRatio
		ScaledGraphicHeight = opt\GraphicHeight
	Else
		ScaledGraphicHeight = Int(opt\GraphicWidth / TargetAspectRatio)
	EndIf
	
	Local i%
	Local MovieFile$ = "GFX\Menu\" + MoviePath
	Local Movie% = OpenMovie_Strict(MovieFile + ".wmv")
	Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", opt\SFXVolume * opt\MasterVolume)
	
	Repeat
		Cls()
		DrawMovie(Movie, 0, (mo\Viewport_Center_Y - ScaledGraphicHeight / 2), opt\GraphicWidth, ScaledGraphicHeight)
		RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"), True, True)
		Flip(True)
		
		Local Close% = False
		
		If GetKey() <> 0 Lor MouseHit(1) Lor (Not IsStreamPlaying_Strict(SplashScreenAudio))
			ResetLoadingTextColor()
			StopStream_Strict(SplashScreenAudio) : SplashScreenAudio = 0
			CloseMovie(Movie) : Movie = 0
			Close = True
		EndIf
	Until Close
	
	Cls()
	Flip()
	ShowPointer()
End Function

Function PlayStartupVideos%()
	Local i%
	Local MovieFile$
	
	For i = 0 To 3
		Select i
			Case 0
				;[Block]
				MovieFile = "startup_Undertow"
				;[End Block]
			Case 1
				;[Block]
				MovieFile = "startup_TSS"
				;[End Block]
			Case 2
				;[Block]
				MovieFile = "startup_UET"
				;[End Block]
			Case 3
				;[Block]
				MovieFile = "startup_Warning"
				;[End Block]
		End Select
		PlayMovie(MovieFile)
	Next
End Function

Global ScreenshotCount% = 1

While FileType("Screenshots\Screenshot" + ScreenshotCount + ".png") = 1
	ScreenshotCount = ScreenshotCount + 1
Wend

Function GetScreenshot%()
	Local x%, y%
	
	If FileType("Screenshots\") <> 2 Then CreateDir("Screenshots")
	
	Local Bank% = CreateBank(opt\GraphicWidth * opt\GraphicHeight * 3)
	Local BufferBack% = BackBuffer()
	
	LockBuffer(BufferBack)
	For x = 0 To opt\GraphicWidth - 1
		For y = 0 To opt\GraphicHeight - 1
			Local Pixel% = ReadPixelFast(x, y, BufferBack)
			Local TempY% = (y * (opt\GraphicWidth * 3)) + (x * 3)
			
			PokeByte(Bank, TempY, ReadPixelColor(Pixel, 0))
			PokeByte(Bank, TempY + 1, ReadPixelColor(Pixel, 8))
			PokeByte(Bank, TempY + 2, ReadPixelColor(Pixel, 16))
		Next
	Next
	UnlockBuffer(BufferBack)
	
	Local fiBuffer% = FI_ConvertFromRawBits(Bank, opt\GraphicWidth, opt\GraphicHeight, opt\GraphicWidth * 3, 24, $FF0000, $00FF00, $0000FF, True)
	
	FI_Save(13, fiBuffer, "Screenshots\Screenshot" + ScreenshotCount + ".png", 0)
	FI_Unload(fiBuffer) : fiBuffer = 0
	FreeBank(Bank) : Bank = 0
	If (Not MainMenuOpen) Then CreateHintMsg(GetLocalString("msg", "screenshot"))
	PlaySound_Strict(LoadTempSound("SFX\General\Screenshot.ogg"))
	ScreenshotCount = ScreenshotCount + 1
End Function

Global TextOffset% = 0

Function SetFontEx%(Font%)
	Local FontName$ = "Default"
	
	Select Font
		Case fo\FontID[Font_Default]
			;[Block]
			FontName = "Default"
			;[End Block]
		Case fo\FontID[Font_Default_Big]
			;[Block]
			FontName = "Default_Big"
			;[End Block]
		Case fo\FontID[Font_Digital]
			;[Block]
			FontName = "Digital"
			;[End Block]
		Case fo\FontID[Font_Digital_Big]
			;[Block]
			FontName = "Digital_Big"
			;[End Block]
		Case fo\FontID[Font_Journal]
			;[Block]
			FontName = "Journal"
			;[End Block]
		Case fo\FontID[Font_Console]
			;[Block]
			FontName = "Console"
			;[End Block]
		Case fo\FontID[Font_Credits]
			;[Block]
			FontName = "Credits"
			;[End Block]
		Case fo\FontID[Font_Credits_Big]
			;[Block]
			FontName = "Credits_Big"
			;[End Block]
	End Select
	TextOffset = Int(GetFileLocalString(FontsFile, FontName, "Offset"))
	SetFont(Font)
End Function

Function TextEx%(x%, y%, Txt$, AlignX% = False, AlignY% = False)
	Text(x, y + TextOffset, Txt, AlignX, AlignY)
End Function

Function GetRescaledTexture%(Brush% = False, TexName$, Flags%, TexDeleteType%, Width%, Height%)
	If FileType(lang\LanguagePath + TexName) = 1 Then TexName = lang\LanguagePath + TexName
	
	; ~ Load the original image
	Local ImgType% = FI_GetFIFFromFilename(TexName)
	Local SrcImg% = FI_Load(ImgType, TexName, Flags)
	
	; ~ Rescale the image
	Local RescaledImg% = FI_Rescale(SrcImg, Width, Height, 0)
	Local TexPath$ = GetEnv("Temp") + "\" + StripPath(TexName)
	
	; ~ Save the rescaled image to a temporary file
	FI_Save(ImgType, RescaledImg, TexPath, Flags)
	
	Local Ret%
	
	; ~ Load the rescaled image as a Brush or Texture
	If Brush
		Ret = LoadBrush_Strict(TexPath, Flags)
	Else
		Ret = LoadTexture_Strict(TexPath, Flags, TexDeleteType)
	EndIf
	; ~ Unload the original and rescaled images
	FI_Unload(SrcImg) : SrcImg = 0
	FI_Unload(RescaledImg) : RescaledImg = 0
	
	; ~ Delete the temporary path
	DeleteFile(TexPath)
	
	Return(Ret)
End Function

Function ApplyGraphicOptions%()
	AntiAlias(opt\AntiAliasing)
	TextureAnisotropic(opt\AnisotropicLevel)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS