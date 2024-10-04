Global FresizeImage%, FresizeTexture%, FresizeTexture2%
Global FresizeCam%

Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

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
End Function

Function Graphics3DExt%(Width%, Height%, Depth% = 32, Mode% = 2)
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
	Local SrcWidth%, SrcHeight%
	Local DestWidth%, DestHeight%
	Local ScratchImage%, DestImage%
	Local SrcBuffer%, ScratchBuffer%, DestBuffer%
	Local X1%, Y1%, X2%, Y2%, Frame%
	
	; ~ Get the width and height of the source image
	SrcWidth = ImageWidth(SrcImage)
	SrcHeight = ImageHeight(SrcImage)
	
	; ~ Calculate the width and height of the dest image, or the scale
	DestWidth = Floor(SrcWidth * ScaleX)
	DestHeight = Floor(SrcHeight * ScaleY)
	
	; ~ If the image does not need to be scaled, just copy the image and exit the function
	If (SrcWidth = DestWidth) And (SrcHeight = DestHeight) Then Return(SrcImage)
	
	; ~ Create a scratch image that is as tall as the source image, and as wide as the destination image
	ScratchImage = CreateImage(DestWidth, SrcHeight, Frames)
	
	; ~ Create the destination image
	DestImage = CreateImage(DestWidth, DestHeight, Frames)
	
	For Frame = 0 To Frames - 1
		; ~ Get pointers to the image buffers for each frame
		SrcBuffer = ImageBuffer(SrcImage, Frame)
		ScratchBuffer = ImageBuffer(ScratchImage, Frame)
		DestBuffer = ImageBuffer(DestImage, Frame)
		
		; ~ Duplicate columns from source image to scratch image
		For X2 = 0 To DestWidth - 1
			X1 = Floor(X2 / ScaleX)
			CopyRect(X1, 0, 1, SrcHeight, X2, 0, SrcBuffer, ScratchBuffer)
		Next
		
		; ~ Duplicate rows from scratch image to destination image
		For Y2 = 0 To DestHeight - 1
			Y1 = Floor(Y2 / ScaleY)
			CopyRect(0, Y1, DestWidth, 1, 0, Y2, ScratchBuffer, DestBuffer)
		Next
	Next
	
	; ~ Free the scratch image
	FreeImage(ScratchImage) : ScratchImage = 0
	; ~ Free the source image
	FreeImage(SrcImage) : SrcImage = 0
	
	; ~ Return the new image
	Return(DestImage)
End Function

Function ScaleRender%(x#, y#, hScale# = 1.0, vScale# = 1.0)
	If Camera <> 0 Then HideEntity(Camera)
	WireFrame(0)
	ShowEntity(FresizeImage)
	ScaleEntity(FresizeImage, hScale, vScale, 1.0)
	PositionEntity(FresizeImage, x, y, 1.0001)
	ShowEntity(FresizeCam)
	RenderWorld()
	HideEntity(FresizeCam)
	HideEntity(FresizeImage)
	WireFrame(WireFrameState)
	If Camera <> 0 Then ShowEntity(Camera)
End Function

Function RenderGamma%()
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	Local RenderScale#
	Local Ratio#
	
	If opt\ScreenGamma > 1.0
		RenderScale = 1.0 / GraphicWidthFloat
		Ratio = SMALLEST_POWER_TWO / GraphicWidthFloat
		
		CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender(-RenderScale, RenderScale, Ratio, Ratio)
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 3)
		EntityAlpha(FresizeImage, opt\ScreenGamma - 1.0)
		ScaleRender(-RenderScale, RenderScale, Ratio, Ratio)
	ElseIf opt\ScreenGamma < 1.0 ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		RenderScale = 1.0 / GraphicWidthFloat
		Ratio = SMALLEST_POWER_TWO / GraphicWidthFloat
		
		Local Gamma% = 255 * opt\ScreenGamma
		Local BufferBack% = BackBuffer()
		Local BufferFresize% = TextureBuffer(FresizeTexture2)
		
		CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BufferBack, TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender(-RenderScale, RenderScale, Ratio, Ratio)
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 2)
		EntityAlpha(FresizeImage, 1.0)
		SetBuffer(BufferFresize)
		ClsColor(Gamma, Gamma, Gamma)
		Cls()
		SetBuffer(BufferBack)
		ScaleRender(-RenderScale, RenderScale, Ratio, Ratio)
		SetBuffer(BufferFresize)
		ClsColor(0, 0, 0)
		Cls()
		SetBuffer(BufferBack)
	EndIf
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
	MoveEntity(Cam, 0, 0, 10000)
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
	ArkBlurTexture = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 0)
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

Function PlayStartupVideos%()
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
	
	Local MovieFile$, i%
	Local StartupPath$ = "GFX\Menu\"
	
	For i = 0 To 3
		Select i
			Case 0
				;[Block]
				MovieFile = StartupPath + "startup_Undertow"
				;[End Block]
			Case 1
				;[Block]
				MovieFile = StartupPath + "startup_TSS"
				;[End Block]
			Case 2
				;[Block]
				MovieFile = StartupPath + "startup_UET"
				;[End Block]
			Case 3
				;[Block]
				MovieFile = StartupPath + "startup_Warning"
				;[End Block]
		End Select
		
		Local Movie% = OpenMovie_Strict(MovieFile + ".wmv")
		Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", opt\SFXVolume * opt\MasterVolume, 0)
		
		Repeat
			Cls()
			DrawMovie(Movie, 0, (mo\Viewport_Center_Y - ScaledGraphicHeight / 2), opt\GraphicWidth, ScaledGraphicHeight)
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"), True, True)
			Flip(True)
			
			Local Close% = False
			
			If GetKey() <> 0 Lor MouseHit(1) Lor (Not IsStreamPlaying_Strict(SplashScreenAudio))
				ResetLoadingTextColor()
				StopStream_Strict(SplashScreenAudio) : SplashScreenAudio = 0
				CloseMovie(Movie)
				Close = True
			EndIf
		Until Close
		
		Cls()
		Flip()
	Next
	ShowPointer()
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

Function GetRescaledTexture%(Texture$, Flags%, Width%, Height%, Brush% = False)
	If FileType(lang\LanguagePath + Texture) = 1 Then Texture = lang\LanguagePath + Texture
	
	; ~ Load the original image
	Local ImgType% = FI_GetFIFFromFilename(Texture)
	Local SrcImg% = FI_Load(ImgType, Texture, Flags)
	
	; ~ Rescale the image
	Local RescaledImg% = FI_Rescale(SrcImg, Width, Height, 0)
	Local TexPath$ = GetEnv("Temp") + "\" + StripPath(Texture)
	
	; ~ Save the rescaled image to a temporary file
	FI_Save(ImgType, RescaledImg, TexPath, Flags)
	
	Local Ret%
	
	; ~ Load the rescaled image as a Brush or Texture
	If Brush
		Ret = LoadBrush_Strict(TexPath, Flags)
	Else
		Ret = LoadTexture_Strict(TexPath, Flags)
	EndIf
	; ~ Unload the original and rescaled images
	FI_Unload(SrcImg) : SrcImg = 0
	FI_Unload(RescaledImg) : RescaledImg = 0
	
	; ~ Delete the temporary path
	DeleteFile(TexPath)
	
	Return(Ret)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS