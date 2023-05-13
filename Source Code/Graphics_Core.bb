Global FresizeImage%, FresizeTexture%, FresizeTexture2%
Global FresizeCam%

Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

Function InitFastResize%()
	; ~ Create Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0.0, 0.0, -10000.0)
	
	FresizeCam = Cam
	
	; ~ Create sprite
	Local SPR% = CreateMesh(Cam)
	Local SF% = CreateSurface(SPR)
	
	AddVertex(SF, -1.0, 1.0, 0.0, 0.0, 0.0)
	AddVertex(SF, 1.0, 1.0, 0.0, 1.0, 0.0)
	AddVertex(SF, -1.0, -1.0, 0.0, 0, 1.0)
	AddVertex(SF, 1.0, -1.0, 0.0, 1.0, 1.0)
	AddTriangle(SF, 0, 1, 2)
	AddTriangle(SF, 3, 2, 1)
	EntityFX(SPR, 17)
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicHeight), 1.0)
	PositionEntity(SPR, 0.0, 0.0, 1.0001)
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	FresizeImage = SPR
	
	; ~ Create texture
	FresizeTexture = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + (256 * opt\SaveTexturesInVRAM))
	FresizeTexture2 = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + (256 * opt\SaveTexturesInVRAM))
	TextureBlend(FresizeTexture2, 3)
	SetBuffer(TextureBuffer(FresizeTexture2))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	EntityTexture(SPR, FresizeTexture, 0, 0)
	EntityTexture(SPR, FresizeTexture2, 0, 1)
	
	HideEntity(FresizeCam)
End Function

Function Graphics3DExt%(Width%, Height%, Depth% = 32, Mode% = 2)
	SetGfxDriver(opt\GFXDriver)
	Graphics3D(Width, Height, Depth, Mode)
	TextureFilter("", 8192) ; ~ This turns on Anisotropic filtering for textures
	TextureAnisotropic(opt\AnisotropicLevel)
	SMALLEST_POWER_TWO = 512.0
	While SMALLEST_POWER_TWO < Width Lor SMALLEST_POWER_TWO < Height
		SMALLEST_POWER_TWO = SMALLEST_POWER_TWO * 2.0
	Wend
	SMALLEST_POWER_TWO_HALF = SMALLEST_POWER_TWO / 2.0
	InitFastResize()
	AntiAlias(opt\AntiAliasing)
End Function

Function ScaleImage2%(SrcImage%, ScaleX#, ScaleY#, ExactSize% = False)
	Local SrcWidth%, SrcHeight%
	Local DestWidth%, DestHeight%
	Local ScratchImage%, DestImage%
	Local SrcBuffer%, ScratchBuffer%, DestBuffer%
	Local X1%, Y1%, X2%, Y2%
	
	; ~ Get the width and height of the source image
	SrcWidth = ImageWidth(SrcImage)
	SrcHeight = ImageHeight(SrcImage)
	
	; ~ Calculate the width and height of the dest image, or the scale
	If (Not ExactSize) Then
		DestWidth = Floor(SrcWidth * ScaleX)
		DestHeight = Floor(SrcHeight * ScaleY)
	Else
		DestWidth = ScaleX
		DestHeight = ScaleY
		
		ScaleX = Float(DestWidth) / Float(SrcWidth)
		ScaleY = Float(DestHeight) / Float(SrcHeight)
	EndIf
	
	; ~ If the image does not need to be scaled, just copy the image and exit the function
	If (SrcWidth = DestWidth) And (SrcHeight = DestHeight) Then Return(SrcImage)
	
	; ~ Create a scratch image that is as tall as the source image, and as wide as the destination image
	ScratchImage = CreateImage(DestWidth, SrcHeight)
	
	; ~ Create the destination image
	DestImage = CreateImage(DestWidth, DestHeight)
	
	; ~ Get pointers to the image buffers
	SrcBuffer = ImageBuffer(SrcImage)
	ScratchBuffer = ImageBuffer(ScratchImage)
	DestBuffer = ImageBuffer(DestImage)
	
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
	
	; ~ Free the scratch image
	FreeImage(ScratchImage) : ScratchImage = 0
	
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
	If opt\DisplayMode = 1 Then
		If opt\RealGraphicWidth <> opt\GraphicWidth Lor opt\RealGraphicHeight <> opt\GraphicHeight Then
			SetBuffer(TextureBuffer(FresizeTexture))
			ClsColor(0, 0, 0)
			Cls()
			CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(FresizeTexture))
			SetBuffer(BackBuffer())
			ClsColor(0, 0, 0)
			Cls()
			ScaleRender(0, 0, SMALLEST_POWER_TWO / Float(opt\GraphicWidth) * opt\AspectRatio, SMALLEST_POWER_TWO / Float(opt\GraphicWidth) * opt\AspectRatio)
			; ~ Might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth, opt\GraphicHeight) if portrait sizes cause issues
			; ~ Everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If opt\ScreenGamma > 1.0 Then
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, SMALLEST_POWER_TWO_HALF - opt\RealGraphicWidth / 2, SMALLEST_POWER_TWO_HALF - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 3)
		EntityAlpha(FresizeImage, opt\ScreenGamma - 1.0)
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
	ElseIf opt\ScreenGamma < 1.0 ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, SMALLEST_POWER_TWO_HALF - opt\RealGraphicWidth / 2, SMALLEST_POWER_TWO_HALF - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 2)
		EntityAlpha(FresizeImage, 1.0)
		SetBuffer(TextureBuffer(FresizeTexture2))
		ClsColor(255 * opt\ScreenGamma, 255 * opt\ScreenGamma, 255 * opt\ScreenGamma)
		Cls()
		SetBuffer(BackBuffer())
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
		SetBuffer(TextureBuffer(FresizeTexture2))
		ClsColor(0, 0, 0)
		Cls()
		SetBuffer(BackBuffer())
	EndIf
	EntityFX(FresizeImage, 1)
	EntityBlend(FresizeImage, 1)
	EntityAlpha(FresizeImage, 1.0)
End Function

Global BatMsgTimer#

Function UpdateBatteryTimer%()
	BatMsgTimer = BatMsgTimer + fps\Factor[0]
	If BatMsgTimer >= 70.0 * 1.5 Then BatMsgTimer = 0.0
End Function

Function UpdateWorld2%()
	Local np.NPCs
	Local i%
	
	wi\IsNVGBlinking = False
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE > 0 Then
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) <> Null Then
				If (wi\NightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory(i)\ItemTemplate\TempName = "veryfinenvg") Lor (wi\SCRAMBLE = 1 And Inventory(i)\ItemTemplate\TempName = "scramble") Then
					Inventory(i)\State = Max(0.0, Inventory(i)\State - (fps\Factor[0] * (0.02 * wi\NightVision) + (0.17 * (wi\SCRAMBLE))))
					Power = Int(Inventory(i)\State)
					If Power = 0 Then ; ~ This NVG or SCRAMBLE can't be used
						HasBattery = 0
						If wi\SCRAMBLE = 1 Then
							CreateMsg(GetLocalString("msg", "battery.died"))
						Else
							CreateMsg(GetLocalString("msg", "battery.died.nvg"))
						EndIf
						wi\IsNVGBlinking = True
						me\BlinkTimer = -1.0
					ElseIf Power <= 100
						HasBattery = 1
					Else
						HasBattery = 2
					EndIf
					Exit
				EndIf
			EndIf
		Next
		
		If wi\NightVision = 2 Then
			If wi\NVGTimer <= 0.0 Then
				For np.NPCs = Each NPCs
					np\NVGX = EntityX(np\Collider, True)
					np\NVGY = EntityY(np\Collider, True)
					np\NVGZ = EntityZ(np\Collider, True)
				Next
				If wi\NVGTimer <= -10.0 Then wi\NVGTimer = 600.0
				wi\IsNVGBlinking = True
			EndIf
			wi\NVGTimer = wi\NVGTimer - fps\Factor[0]
		EndIf
	EndIf
	
	If (wi\SCRAMBLE = 1 And HasBattery <> 0) Lor wi\SCRAMBLE = 2 Then
		If (Not ChannelPlaying(SCRAMBLECHN)) Then SCRAMBLECHN = PlaySound_Strict(SCRAMBLESFX)
	Else
		If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN) : SCRAMBLECHN = 0
	EndIf
	
	If HasBattery = 1 Then
		UpdateBatteryTimer()
		If BatMsgTimer >= 70.0 * 1.0 Then
			If (Not ChannelPlaying(LowBatteryCHN[1])) Then LowBatteryCHN[1] = PlaySound_Strict(LowBatterySFX[1])
		EndIf
	EndIf
End Function

Global CurrTrisAmount%

Function RenderWorld2%(Tween#)
	Local np.NPCs
	Local i%, k%, l%
	
	CameraProjMode(ArkBlurCam, 0)
	CameraProjMode(Camera, 1)
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE > 0 Then
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) <> Null Then
				If (wi\NightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory(i)\ItemTemplate\TempName = "veryfinenvg") Lor (wi\SCRAMBLE = 1 And Inventory(i)\ItemTemplate\TempName = "scramble") Then
					Power = Int(Inventory(i)\State)
					If Power = 0 Then ; ~ This NVG or SCRAMBLE can't be used
						HasBattery = 0
					ElseIf Power <= 100
						HasBattery = 1
					Else
						HasBattery = 2
					EndIf
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not wi\IsNVGBlinking) Then RenderWorld(Tween)
	
	CurrTrisAmount = TrisRendered()
	
	If HasBattery > 0 Then
		If wi\NightVision = 2 Then ; ~ Show a HUD
			Color(255, 255, 255)
			
			SetFont2(fo\FontID[Font_Digital])
			
			Local PlusY% = 0
			
			If HasBattery = 1 Then PlusY = 40
			
			Local RefreshHint$ = GetLocalString("msg", "refresh")
			
			Text2(mo\Viewport_Center_X, (20 + PlusY) * MenuScale, Trim(Left(RefreshHint, Instr(RefreshHint, "%s") - 1)), True, False)
			Text2(mo\Viewport_Center_X, (60 + PlusY) * MenuScale, Max(FloatToString(wi\NVGTimer / 60.0, 1), 0.0), True, False)
			Text2(mo\Viewport_Center_X, (100 + PlusY) * MenuScale, Trim(Right(RefreshHint, Len(RefreshHint) - Instr(RefreshHint, "%s") - 1)), True, False)
			
			Local Temp% = CreatePivot()
			Local Temp2% = CreatePivot()
			
			PositionEntity(Temp, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
			
			For np.NPCs = Each NPCs
				If np\NVGName <> "" And (Not np\HideFromNVG) Then ; ~ Don't waste your time if the string is empty
					PositionEntity(Temp2, np\NVGX, np\NVGY, np\NVGZ)
					
					Local Dist# = EntityDistanceSquared(Temp2, me\Collider)
					
					If Dist < 552.25 Then ; ~ Don't draw text if the NPC is too far away
						PointEntity(Temp, Temp2)
						
						Local YawValue# = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
						Local xValue# = 0.0
						
						If YawValue > 90.0 And YawValue <= 180.0 Then
							xValue = 1.0 / 90.0 * YawValue
						ElseIf YawValue > 180 And YawValue < 270.0
							xValue = (-1.0) / YawValue * 270.0
						Else
							xValue = Sin(YawValue)
						EndIf
						
						Local PitchValue# = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
						Local yValue# = 0.0
						
						If PitchValue > 90.0 And PitchValue <= 180.0 Then
							yValue = 1.0 / 90.0 * PitchValue
						ElseIf PitchValue > 180.0 And PitchValue < 270.0
							yValue = (-1.0) / PitchValue * 270.0
						Else
							yValue = Sin(PitchValue)
						EndIf
						
						If (Not wi\IsNVGBlinking) Then
							Text2(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y), np\NVGName, True, True)
							Text2(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y) + (30 * MenuScale), FloatToString(Sqr(Dist), 1) + " m", True, True)
						EndIf
					EndIf
				EndIf
			Next
			
			FreeEntity(Temp) : Temp = 0
			FreeEntity(Temp2) : Temp2 = 0
			
			Color(0, 0, 55)
		ElseIf wi\NightVision = 1
			Color(0, 55, 0)
		Else ; ~ SCRAMBLE
			Color(55, 55, 55)
		EndIf
		For k = 0 To 10
			Rect(45 * MenuScale, mo\Viewport_Center_Y - ((k * 20) * MenuScale), 54 * MenuScale, 10 * MenuScale)
		Next
		If wi\NightVision = 2
			Color(0, 0, 255)
		ElseIf wi\NightVision = 1
			Color(0, 255, 0)
		Else ; ~ SCRAMBLE
			Color(255, 255, 255)
		EndIf
		For l = 0 To Min(Floor((Power + 50) * 0.01), 11.0)
			Rect(45 * MenuScale, mo\Viewport_Center_Y - ((l * 20) * MenuScale), 54 * MenuScale, 10 * MenuScale)
		Next
		DrawImage(t\ImageID[6], 40 * MenuScale, mo\Viewport_Center_Y + (30 * MenuScale))
	EndIf
	
	; ~ Render sprites
	CameraProjMode(ArkBlurCam, 2)
	CameraProjMode(Camera, 0)
	RenderWorld()
	CameraProjMode(ArkBlurCam, 0)
	
	If HasBattery = 1 Then
		If BatMsgTimer >= 70.0 * 1.0 Then
			Color(255, 0, 0)
			SetFont2(fo\FontID[Font_Digital])
			
			Text2(mo\Viewport_Center_X, 20 * MenuScale, GetLocalString("msg", "battery.low"), True, False)
		EndIf
	EndIf
	Color(255, 255, 255)
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
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(opt\GraphicWidth), SMALLEST_POWER_TWO / Float(opt\GraphicWidth), 1.0)
	PositionEntity(SPR, 0.0, 0.0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ArkBlurImage = SPR
	
	; ~ Create blur texture
	ArkBlurTexture = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO)
	EntityTexture(SPR, ArkBlurTexture)
End Function

Function RenderBlur%(Power#)
	EntityAlpha(ArkBlurImage, Power)
	CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(ArkBlurTexture))
End Function

Function FreeBlur%()
	If ArkBlurCam <> 0 Then ArkBlurCam = 0
	If ArkBlurTexture <> 0 Then DeleteSingleTextureEntryFromCache(ArkBlurTexture)
End Function

Function PlayStartupVideos%()
	HidePointer()
	
	fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"))
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(opt\RealGraphicWidth) / Float(opt\RealGraphicHeight)
	
	; ~ TODO: Rework scaling for 4k resolutions
	If Ratio > 1.76 And Ratio < 1.78 Then
		ScaledGraphicHeight = opt\RealGraphicHeight
	Else
		ScaledGraphicHeight = Float(opt\RealGraphicWidth) / (16.0 / 9.0)
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
			DrawMovie(Movie, 0, (opt\RealGraphicHeight / 2 - ScaledGraphicHeight / 2), opt\RealGraphicWidth, ScaledGraphicHeight)
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"), True, True)
			Flip()
			
			Local Close% = False
			
			If GetKey() <> 0 Lor MouseHit(1) Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)) Then
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
	
	Local Bank% = CreateBank(opt\RealGraphicWidth * opt\RealGraphicHeight * 3)
	
	LockBuffer(BackBuffer())
	For x = 0 To opt\RealGraphicWidth - 1
		For y = 0 To opt\RealGraphicHeight - 1
			Local Pixel% = ReadPixelFast(x, y, BackBuffer())
			
			PokeByte(Bank, (y * (opt\RealGraphicWidth * 3)) + (x * 3), ReadPixelColor(Pixel, 0))
			PokeByte(Bank, (y * (opt\RealGraphicWidth * 3)) + (x * 3) + 1, ReadPixelColor(Pixel, 8))
			PokeByte(Bank, (y * (opt\RealGraphicWidth * 3)) + (x * 3) + 2, ReadPixelColor(Pixel, 16))
		Next
	Next
	UnlockBuffer(BackBuffer())
	
	Local fiBuffer% = FI_ConvertFromRawBits(Bank, opt\RealGraphicWidth, opt\RealGraphicHeight, opt\RealGraphicWidth * 3, 24, $FF0000, $00FF00, $0000FF, True)
	
	FI_Save(13, fiBuffer, "Screenshots\Screenshot" + ScreenshotCount + ".png", 0)
	FI_Unload(fiBuffer)
	FreeBank(Bank)
	If (Not MainMenuOpen) Then CreateHintMsg(GetLocalString("msg", "screenshot"))
	PlaySound_Strict(LoadTempSound("SFX\General\Screenshot.ogg"))
	ScreenshotCount = ScreenshotCount + 1
End Function

Global TextOffset% = 0

Function SetFont2%(Font%)
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

Function Text2%(x%, y%, Txt$, AlignX% = False, AlignY% = False)
	If opt\TextShadow Then
		Local ColorR# = ColorRed()
		Local ColorG# = ColorGreen()
		Local ColorB# = ColorBlue()
		
		If ColorR = 0.0 And ColorG = 0.0 And ColorB = 1.0 Then
			Color(200, 200, 200)
		Else
			Color(55, 55, 55)
		EndIf
		Text(x + (2 * MenuScale), y + TextOffset + (2 * MenuScale), Txt, AlignX, AlignY)
		Color(ColorR, ColorG, ColorB)
		Text(x, y + TextOffset, Txt, AlignX, AlignY)
	Else
		Text(x, y + TextOffset, Txt, AlignX, AlignY)
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS