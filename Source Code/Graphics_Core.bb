Global ResizeTexture%

Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

Function InitFastResize%()
	ResizeTexture = CreateTexture(Max(SMALLEST_POWER_TWO, 2048.0), Max(SMALLEST_POWER_TWO, 2048.0), 1 + 2 + 256 + 1024)
	
	ClsColor(0, 0, 0)
	
	InitDeferred()
	InitShaders()
End Function

Function Graphics3DEx%(Width%, Height%, Depth% = 32, Mode% = 2)
	SetGfxDriver(opt\GFXDriver)
	If GetGraphicsLevel() >= 100
		If Mode = 1 Then Mode = 4 ; ~ Forcely borderless because fullscreen is glitched on DX11 like DX7
	EndIf
	Graphics3D(Width, Height, Depth, Mode)
	HardwareSkinning(True) ; ~ This turns on hardware skinning (animations) from HLSL (x3 fps boost)
	TexturePersistentCaching(True) ; ~ Manual texture clear
	TextureLodBias(0.0)
	GetCaps()
	SMALLEST_POWER_TWO = 512.0
	While SMALLEST_POWER_TWO < Width Lor SMALLEST_POWER_TWO < Height
		SMALLEST_POWER_TWO = SMALLEST_POWER_TWO * 2.0
	Wend
	SMALLEST_POWER_TWO_HALF = SMALLEST_POWER_TWO / 2.0
	InitFastResize()
End Function

Function ScaleImageEx%(SrcImage%, ScaleX#, ScaleY#)
	; ~ Scale image and return
	ScaleImage(SrcImage, ScaleX, ScaleY)
	Return(SrcImage)
End Function

Function ResizeImageEx%(SrcImage%, ScaleX#, ScaleY#)
	; ~ Get the width and height of the source image
	Local SrcWidth# = ImageWidth(SrcImage)
	Local SrcHeight# = ImageHeight(SrcImage)
	
	; ~ Calculate the width and height of the dest image, or the scale
	Local DestWidth# = SrcWidth * ScaleX
	Local DestHeight# = SrcHeight * ScaleY
	
	; ~ Resize the image and return
	ResizeImage(SrcImage, DestWidth, DestHeight)
	Return(SrcImage)
End Function

Function RescaleTexture%(SrcTexture%, ScaleX#, ScaleY#, Flags% = 1)
	; ~ Get the width and height of the source texture
	Local SrcWidth# = TextureWidth(SrcTexture)
	Local SrcHeight# = TextureHeight(SrcTexture)
	
	; ~ Create the Scratch image
	Local ScratchImage% = CreateImage(SrcWidth, SrcHeight)
	; ~ Create the destination texture
	Local DestTexture% = CreateTexture(SrcWidth * ScaleX, SrcHeight * ScaleY, Flags)
	
	; ~ Get the width and height of the destination texture
	Local DestWidth% = TextureWidth(DestTexture)
	Local DestHeight% = TextureHeight(DestTexture)
	
	; ~ Copy rects of the source texture
	CopyRect(0, 0, SrcWidth, SrcHeight, 0, 0, TextureBuffer(SrcTexture), ImageBuffer(ScratchImage))
	
	; ~ Resize scratch image
	ResizeImage(ScratchImage, DestWidth, DestHeight)
	
	; ~ Copy rects of the destination texture
	CopyRect(0, 0, DestWidth, DestHeight, 0, 0, ImageBuffer(ScratchImage), TextureBuffer(DestTexture))
	
	; ~ Free the scratch image
	FreeImage(ScratchImage) : ScratchImage = 0
	; ~ Free the source texture
	FreeTexture(SrcTexture) : SrcTexture = 0
	
	; ~ Return the new texture
	Return(DestTexture)
End Function

Function CreateQuad%(Parent% = 0)
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

Function CreateFullscreenQuad%(Width%, Height%, Parent% = 0)
	Local Quad% = CreateSprite(Parent)
	
	ScaleSprite(Quad, 1.0, (Float(Height) / Float(Width)))
	
	Local PixelWidth# = 0.0
	Local PixelHeight# = 0.0
	
	If GetGraphicsLevel() < 100 ; ~ DX9 has half-pixel offset
		PixelWidth# = 0.5 / Width
		PixelHeight# = 0.5 / Height
	EndIf
	
	MoveEntity(Quad, -PixelWidth, PixelHeight, 1.0001)
	Return(Quad)
End Function

;Function RenderGamma%() ; ~ Render gamma for current BackBuffer()
;	If opt\ScreenGamma <> 1.0
;		CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, 0, 0, BackBuffer(), TextureBuffer(MRTColor))
;		ProcessGamma(Lerp(opt\ScreenGamma, 1.0, 0.5))
;		PresentGBuffer(MRTColor, BackBuffer())
;		SetBuffer(BackBuffer())
;	EndIf
;End Function

Global CurrTrisAmount%, BatchesAmount%

Function RenderWorldEx%(Tween#)
	Local i%
	
	CameraProjMode(Camera, 1)
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	ProcessDeferred(Camera, Tween)
	CameraProjMode(Camera, 0)
	
	Local TexBuffer%
	
	For i = 0 To MaxOverlayIDAmount - 1
		Local Overlay% = t\OverlayID[i]
		
		If Overlay <> 0 And (Not EntityHidden(Overlay))
			TexBuffer = GetEntityTextureBuffer(Overlay, 0)
			If TexBuffer <> 0
				Color(EntityColorR(Overlay), EntityColorG(Overlay), EntityColorB(Overlay), 255 * GetEntityAlpha(Overlay))
				DrawBuffer(TexBuffer, 0, 0, opt\GraphicWidth, opt\GraphicHeight, GetEntityBlend(Overlay))
			EndIf
		EndIf
	Next
	
	If ArkBlurImage <> 0 And (Not EntityHidden(ArkBlurImage))
		TexBuffer = GetEntityTextureBuffer(ArkBlurImage, 0)
		If TexBuffer <> 0
			Color(EntityColorR(ArkBlurImage), EntityColorG(ArkBlurImage), EntityColorB(ArkBlurImage), 255 * GetEntityAlpha(ArkBlurImage))
			DrawBufferRect(TexBuffer, 0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, opt\GraphicWidth, opt\GraphicHeight, GetEntityBlend(ArkBlurImage))
		EndIf
	EndIf
End Function

Global ArkBlurImage%, ArkBlurTexture%
Global ArkBlurCam%

Function CreateBlurImage%()
	; ~ Create blur Camera
	ArkBlurCam = CreatePivot()
	MoveEntity(ArkBlurCam, 0.0, 0.0, 10000.0)
	HideEntity(ArkBlurCam)
	; ~ Create sprite
	Local SPR% = CreateMesh(ArkBlurCam)
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
	ArkBlurTexture = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256 + 1024)
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

Function PlayMovie%(MoviePath$)
	If RunningOnWine() Then Return
	If (Not opt\PlayStartup) Then Return
	HidePointer()
	
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
	
	Local i%, SkipMessage$
	Local MovieFile$ = "GFX\Menu\" + MoviePath
	Local Movie% = OpenMovie_Strict(MovieFile + ".wmv")
	Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", opt\SFXVolume * opt\MasterVolume)
	
	Repeat
		Cls()
		DrawMovie(Movie, 0, (mo\Viewport_Center_Y - ScaledGraphicHeight / 2), opt\GraphicWidth, ScaledGraphicHeight)
		SkipMessage = GetLocalString("menu", "anykey")
		RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), SkipMessage, True, True)
		Delay(10)
		Flip(False)
		
		Local Close% = False
		
		If GetKey() <> 0 Lor MouseHit(1) Lor (Not IsStreamPlaying_Strict(SplashScreenAudio))
			ResetLoadingTextColor()
			StopStream_Strict(SplashScreenAudio) : SplashScreenAudio = 0
			CloseMovie(Movie) : Movie = 0
			Close = True
		EndIf
	Until Close
	
	Cls()
	Flip(opt\VSync)
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
Global ScreenshotCooldown%

While FileType("Screenshots\Screenshot" + ScreenshotCount + ".png") = 1
	ScreenshotCount = ScreenshotCount + 1
Wend

Function GetScreenshot%()
	If ScreenshotCooldown > MilliSecs() Then Return
	
	Local x%, y%
	
	If FileType("Screenshots\") <> 2 Then CreateDir("Screenshots")
	SaveBuffer(BackBuffer(), "Screenshots\Screenshot" + ScreenshotCount + ".png")
	If (Not MainMenuOpen) Then CreateHintMsg(GetLocalString("msg", "screenshot"))
	PlaySound_Strict(LoadTempSound("SFX\General\Screenshot.ogg"))
	ScreenshotCount = ScreenshotCount + 1
	
	ScreenshotCooldown = MilliSecs() + 1000
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
	If Len(Txt) > 1 ; ~ Non formatted
		Batching(True)
		Text(x, y + TextOffset, Txt, AlignX, AlignY)
		Batching(False)
		Return
	EndIf
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

Function SetCameraRenderInterval%()
	Select opt\SecurityCamRenderInterval
		Case 0
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 24.0
			;[End Block]
		Case 1
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 18.0
			;[End Block]
		Case 2
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 12.0
			;[End Block]
		Case 3
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 6.0
			;[End Block]
		Case 4
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 0.0
			;[End Block]
	End Select
End Function

Function SetTextureAnisotropic%()
	Select opt\Anisotropic
		Case 0
			;[Block]
			opt\AnisotropicLevel = 0
			;[End Block]
		Case 1
			;[Block]
			opt\AnisotropicLevel = 2
			;[End Block]
		Case 2
			;[Block]
			opt\AnisotropicLevel = 4
			;[End Block]
		Case 3
			;[Block]
			opt\AnisotropicLevel = 8
			;[End Block]
		Case 4
			;[Block]
			opt\AnisotropicLevel = 16
			;[End Block]
	End Select
End Function

Function SetLightingQuality%(Setting%)
	Local p.Props, it.Items, n.NPCs
	
	Select Setting
		Case 0 ; ~ Very low
			;[Block]
			; ~ All shadows disabled
			;[End Block]
		Case 1 ; ~ Low
			;[Block]
			; ~ Static shadows disabled
			;[End Block]
		Case 2 ; ~ Medium (only props)
			;[Block]
			For p.Props = Each Props
				If p\OBJ <> 0 Then SetShadowsCasting(p\OBJ, True)
			Next
			For it.Items = Each Items
				If it\OBJ <> 0 Then SetShadowsCasting(it\OBJ, False)
			Next
			For n.NPCs = Each NPCs
				If n\OBJ <> 0 Then SetShadowsCasting(n\OBJ, False)
				If n\OBJ2 <> 0 Then SetShadowsCasting(n\OBJ2, False)
			Next
			;[End Block]
		Case 3 ; ~ High (props + items)
			;[Block]
			For p.Props = Each Props
				If p\OBJ <> 0 Then SetShadowsCasting(p\OBJ, True)
			Next
			For it.Items = Each Items
				If it\OBJ <> 0 Then SetShadowsCasting(it\OBJ, True)
			Next
			For n.NPCs = Each NPCs
				If n\OBJ <> 0 Then SetShadowsCasting(n\OBJ, False)
				If n\OBJ2 <> 0 Then SetShadowsCasting(n\OBJ2, False)
			Next
			;[End Block]
		Case 4 ; ~ Ultra (props + items + NPCs)
			;[Block]
			For p.Props = Each Props
				If p\OBJ <> 0 Then SetShadowsCasting(p\OBJ, True)
			Next
			For it.Items = Each Items
				If it\OBJ <> 0 Then SetShadowsCasting(it\OBJ, True)
			Next
			For n.NPCs = Each NPCs
				If n\OBJ <> 0 Then SetShadowsCasting(n\OBJ, True)
				If n\OBJ2 <> 0 Then SetShadowsCasting(n\OBJ2, True)
			Next
			;[End Block]
	End Select
End Function

Function ApplyGraphicOptions%()
	TextureAnisotropic(opt\AnisotropicLevel)
	TextureDivisor(opt\TextureQualityLevel)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS