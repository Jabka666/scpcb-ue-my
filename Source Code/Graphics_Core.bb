Global FresizeImage%, FresizeTexture%, FresizeTexture2%
Global FresizeCam%

Function InitFastResize()
	; ~ Create Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0.0, 0.0, -10000)
	
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
	ScaleEntity(SPR, 4096.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicHeight), 1.0)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	FresizeImage = SPR
	
	; ~ Create texture
	FresizeTexture = CreateTexture(4096.0, 4096.0, 1 + 256)
	FresizeTexture2 = CreateTexture(4096.0, 4096.0, 1 + 256)
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
	Graphics3D(Width, Height, Depth, Mode)
	TextureFilter("", 8192) ; ~ This turns on Anisotropic filtering for textures
	TextureAnisotropic(opt\AnisotropicLevel)
	InitFastResize()
	AntiAlias(opt\AntiAliasing)
End Function

Function ResizeImage2%(SrcImage%, ScaleX#, ScaleY#, ExactSize% = False)
	Local SrcWidth%, SrcHeight%
	Local DestWidth%, DestHeight%
	Local ScratchImage%, DestImage%
	Local SrcBuffer%, ScratchBuffer%, DestBuffer%
	Local X1%, Y1%, X2%, Y2%
	
	; ~ Get the width and height of the source image
	SrcWidth = ImageWidth(SrcImage)
	SrcHeight = ImageHeight(SrcImage)
	
	; ~ Calculate the width and height of the dest image, or the scale
	If ExactSize = False
		DestWidth = Floor(SrcWidth * ScaleX)
		DestHeight = Floor(SrcHeight * ScaleY)
	Else
		DestWidth = ScaleX
		DestHeight = ScaleY
		
		ScaleX = Float(DestWidth) / Float(SrcWidth)
		ScaleY = Float(DestHeight) / Float(SrcHeight)
	EndIf
	
	; ~ If the image does not need to be scaled, just copy the image and exit the function
	If (SrcWidth = DestWidth) And (SrcHeight = DestHeight) Then Return(CopyImage(SrcImage))
	
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
	FreeImage(ScratchImage)
	
	; ~ Return the new image
	Return(DestImage)
End Function

Function ScaleRender(x#, y#, hScale# = 1.0, vScale# = 1.0)
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

Function RenderGamma()
	If opt\DisplayMode = 1 Then
		If opt\RealGraphicWidth <> opt\GraphicWidth Lor opt\RealGraphicHeight <> opt\GraphicHeight Then
			SetBuffer(TextureBuffer(FresizeTexture))
			ClsColor(0, 0, 0)
			Cls()
			CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, 2048.0 - mo\Viewport_Center_X, 2048.0 - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(FresizeTexture))
			SetBuffer(BackBuffer())
			ClsColor(0, 0, 0)
			Cls()
			ScaleRender(0, 0, 4096.0 / Float(opt\GraphicWidth) * opt\AspectRatio, 4096.0 / Float(opt\GraphicWidth) * opt\AspectRatio)
			; ~ Might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth, opt\GraphicHeight) if portrait sizes cause issues
			; ~ Everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If opt\ScreenGamma >= 1.0 Then
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, 2048.0 - opt\RealGraphicWidth / 2, 2048.0 - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth))
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 3)
		EntityAlpha(FresizeImage, opt\ScreenGamma - 1.0)
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth))
	Else ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, 2048.0 - opt\RealGraphicWidth / 2, 2048.0 - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0)
		Cls()
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth))
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 2)
		EntityAlpha(FresizeImage, 1.0)
		SetBuffer(TextureBuffer(FresizeTexture2))
		ClsColor(255 * opt\ScreenGamma, 255 * opt\ScreenGamma, 255 * opt\ScreenGamma)
		Cls()
		SetBuffer(BackBuffer())
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth), 4096.0 / Float(opt\RealGraphicWidth))
		SetBuffer(TextureBuffer(FresizeTexture2))
		ClsColor(0, 0, 0)
		Cls()
		SetBuffer(BackBuffer())
	EndIf
	EntityFX(FresizeImage, 1)
	EntityBlend(FresizeImage, 1)
	EntityAlpha(FresizeImage, 1.0)
End Function

Function UpdateWorld2()
	Local np.NPCs
	Local i%
	
	wi\IsNVGBlinking = False
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE Then
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) <> Null Then
				If (wi\NightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernvg") Lor (wi\SCRAMBLE And Inventory(i)\ItemTemplate\TempName = "scramble") Then
					Inventory(i)\State = Max(0.0, Inventory(i)\State - (fps\Factor[0] * (0.02 * wi\NightVision) + (0.15 * (wi\SCRAMBLE))))
					Power = Int(Inventory(i)\State)
					If Power = 0 Then ; ~ This NVG or SCRAMBLE can't be used
						HasBattery = 0
						If wi\SCRAMBLE Then
							CreateMsg("The batteries in this gear died.")
						Else
							CreateMsg("The batteries in these night vision goggles died.")
						EndIf
						wi\IsNVGBlinking = True
						me\BlinkTimer = -1.0
					ElseIf Power =< 100
						HasBattery = 1
					Else
						HasBattery = 2
					EndIf
					Exit
				EndIf
			EndIf
		Next
		
		If wi\NightVision = 2 Then
			If wi\NVGTimer =< 0.0 Then
				For np.NPCs = Each NPCs
					np\NVGX = EntityX(np\Collider, True)
					np\NVGY = EntityY(np\Collider, True)
					np\NVGZ = EntityZ(np\Collider, True)
				Next
				If wi\NVGTimer =< -10.0 Then wi\NVGTimer = 600.0
				wi\IsNVGBlinking = True
			EndIf
			wi\NVGTimer = wi\NVGTimer - fps\Factor[0]
		EndIf
	EndIf
	
	If wi\SCRAMBLE Then
		If HasBattery = 0 Then
			If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN)
		Else
			SCRAMBLECHN = LoopSound2(SCRAMBLESFX, SCRAMBLECHN, Camera, Camera)
		EndIf
	Else
		If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN)
	EndIf
	
	If fps\Factor[0] > 0.0 Then
		If HasBattery = 1 And ((MilliSecs2() Mod 800) < 200) Then
			If (Not LowBatteryCHN[1]) Then
				LowBatteryCHN[1] = PlaySound_Strict(LowBatterySFX[1])
			ElseIf (Not ChannelPlaying(LowBatteryCHN[1])) Then
				LowBatteryCHN[1] = PlaySound_Strict(LowBatterySFX[1])
			EndIf
		EndIf
	EndIf
End Function

Const BRIGHTNESS# = 40.0

Global CurrTrisAmount%

Function RenderWorld2(Tween#)
	Local np.NPCs
	Local i%, k%, l%
	
	CameraProjMode(ArkBlurCam, 0)
	CameraProjMode(Camera, 1)
	
	If wi\NightVision > 0 And wi\NightVision < 3 Then
		AmbientLight(Min(BRIGHTNESS * 2.0, 255.0), Min(BRIGHTNESS * 2.0, 255.0), Min(BRIGHTNESS * 2.0, 255.0))
	ElseIf wi\NightVision = 3
		AmbientLight(255.0, 255.0, 255.0)
	ElseIf PlayerRoom <> Null
		AmbientLight(BRIGHTNESS, BRIGHTNESS, BRIGHTNESS)
	EndIf
	
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE Then
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) <> Null Then
				If (wi\NightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernvg") Lor (wi\SCRAMBLE And Inventory(i)\ItemTemplate\TempName = "scramble") Then
					Power = Int(Inventory(i)\State)
					If Power = 0 Then ; ~ This NVG or SCRAMBLE can't be used
						HasBattery = 0
					ElseIf Power =< 100
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
			
			SetFont(fo\FontID[Font_Digital])
			
			Local PlusY% = 0
			
			If HasBattery = 1 Then PlusY = 40
			
			Text(mo\Viewport_Center_X, (20 + PlusY) * MenuScale, "REFRESHING DATA IN", True, False)
			Text(mo\Viewport_Center_X, (60 + PlusY) * MenuScale, Max(f2s(wi\NVGTimer / 60.0, 1), 0.0), True, False)
			Text(mo\Viewport_Center_X, (100 + PlusY) * MenuScale, "SECONDS", True, False)
			
			Local Temp% = CreatePivot()
			Local Temp2% = CreatePivot()
			
			PositionEntity(Temp, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
			
			Color(255, 255, 255)
			
			For np.NPCs = Each NPCs
				If np\NVGName <> "" And (Not np\HideFromNVG) Then ; ~ Don't waste your time if the string is empty
					PositionEntity(Temp2, np\NVGX, np\NVGY, np\NVGZ)
					
					Local Dist# = EntityDistanceSquared(Temp2, me\Collider)
					
					If Dist < 552.25 Then ; ~ Don't draw text if the NPC is too far away
						PointEntity(Temp, Temp2)
						
						Local YawValue# = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
						Local xValue# = 0.0
						
						If YawValue > 90.0 And YawValue =< 180.0 Then
							xValue = Sin(90.0) / 90.0 * YawValue
						ElseIf YawValue > 180 And YawValue < 270.0
							xValue = Sin(270.0) / YawValue * 270.0
						Else
							xValue = Sin(YawValue)
						EndIf
						
						Local PitchValue# = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
						Local yValue# = 0.0
						
						If PitchValue > 90.0 And PitchValue =< 180.0 Then
							yValue = Sin(90.0) / 90.0 * PitchValue
						ElseIf PitchValue > 180.0 And PitchValue < 270
							yValue = Sin(270.0) / PitchValue * 270.0
						Else
							yValue = Sin(PitchValue)
						EndIf
						
						If (Not wi\IsNVGBlinking) Then
							Text(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y), np\NVGName, True, True)
							Text(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y) + (30 * MenuScale), f2s(Sqr(Dist), 1) + " m", True, True)
						EndIf
					EndIf
				EndIf
			Next
			
			FreeEntity(Temp)
			FreeEntity(Temp2)
			
			Color(0, 0, 55)
		ElseIf wi\NightVision = 1
			Color(0, 55, 0)
		Else ; ~ SCRAMBLE
			Color(55, 55, 55)
		EndIf
		For k = 0 To 10
			Rect(45, mo\Viewport_Center_Y - (k * 20), 54, 10)
		Next
		If wi\NightVision = 2
			Color(0, 0, 255)
		ElseIf wi\NightVision = 1
			Color(0, 255, 0)
		Else ; ~ SCRAMBLE
			Color(255, 255, 255)
		EndIf
		For l = 0 To Min(Floor((Power + 50) * 0.01), 11)
			Rect(45, mo\Viewport_Center_Y - (l * 20), 54, 10)
		Next
		DrawImage(t\ImageID[6], 40, mo\Viewport_Center_Y + 30)
	EndIf
	
	; ~ Render sprites
	CameraProjMode(ArkBlurCam, 2)
	CameraProjMode(Camera, 0)
	RenderWorld()
	CameraProjMode(ArkBlurCam, 0)
	
	If fps\Factor[0] > 0.0 Then
		If HasBattery = 1 And ((MilliSecs2() Mod 800) < 400)
			Color(255, 0, 0)
			SetFont(fo\FontID[Font_Digital])
			
			Text(mo\Viewport_Center_X, 20 * MenuScale, "WARNING: LOW BATTERY", True, False)
		EndIf
	EndIf
	Color(255, 255, 255)
End Function

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
	ScaleEntity(SPR, 4096.0 / Float(ArkSw), 4096.0 / Float(ArkSw), 1.0)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100000)
	EntityBlend(SPR, 1)
	ArkBlurImage = SPR
	
	; ~ Create blur texture
	ArkBlurTexture = CreateTextureUsingCacheSystem(4096.0, 4096.0, 0)
	EntityTexture(SPR, ArkBlurTexture)
End Function

Function RenderBlur(Power#)
	EntityAlpha(ArkBlurImage, Power)
	CopyRect(0, 0, ArkSw, ArkSh, 2048.0 - (ArkSw / 2), 2048.0 - (ArkSh / 2), BackBuffer(), TextureBuffer(ArkBlurTexture))
End Function

Function PlayStartupVideos()
	HidePointer()
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(opt\RealGraphicWidth) / Float(opt\RealGraphicHeight)
	
	If Ratio > 1.76 And Ratio < 1.78 Then
		ScaledGraphicHeight = opt\RealGraphicHeight
	Else
		ScaledGraphicHeight = Float(opt\RealGraphicWidth) / (16.0 / 9.0)
	EndIf
	
	Local MovieFile$, i%
	Local StartupPath$ = "GFX\menu\"
	
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
		
		Local Movie% = OpenMovie(MovieFile + ".avi")
		
		If (Not Movie) Then
			PutINIValue(OptionFile, "Advanced", "Play Startup Videos", 0)
			RuntimeError("Movie " + Chr(34) + MovieFile + Chr(34) + " not found.")
		EndIf
		Movie = OpenMovie(MovieFile + ".avi")
		
		Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", opt\SFXVolume, 0)
		
		Repeat
			Cls()
			DrawMovie(Movie, 0, (opt\RealGraphicHeight / 2 - ScaledGraphicHeight / 2), opt\RealGraphicWidth, ScaledGraphicHeight)
			Flip()
		Until (GetKey() Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		StopStream_Strict(SplashScreenAudio)
		CloseMovie(Movie)
		
		Cls()
		Flip()
	Next
	ShowPointer()
End Function

Global ScreenshotCount% = 1

While FileType("Screenshots\Screenshot" + ScreenshotCount + ".png") = 1
	ScreenshotCount = ScreenshotCount + 1
Wend

Function GetScreenshot()
	Local x%, y%
	
	If FileType("Screenshots\") <> 2 Then
		CreateDir("Screenshots")
	EndIf
	
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
	If (Not MainMenuOpen) Then CreateHintMsg("Screenshot Taken.")
	PlaySound_Strict(LoadTempSound("SFX\General\Screenshot.ogg"))
	ScreenshotCount = ScreenshotCount + 1
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D