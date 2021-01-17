Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

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
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicHeight), 1.0)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	FresizeImage = SPR
	
    ; ~ Create texture
	FresizeTexture = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256)
	FresizeTexture2 = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1 + 256)
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
	SMALLEST_POWER_TWO = 512.0
	While SMALLEST_POWER_TWO < Width Lor SMALLEST_POWER_TWO < Height
		SMALLEST_POWER_TWO = SMALLEST_POWER_TWO * 2.0
	Wend
	SMALLEST_POWER_TWO_HALF = SMALLEST_POWER_TWO / 2.0
	InitFastResize()
	AntiAlias(opt\AntiAliasing)
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

Function ResizeImage2(Image%, Width%, Height%)
    Local Img% = CreateImage(Width, Height)
	Local OldWidth% = ImageWidth(Image)
	Local OldHeight% = ImageHeight(Image)
	
	CopyRect(0, 0, OldWidth, OldHeight, SMALLEST_POWER_TWO_HALF - OldWidth / 2, SMALLEST_POWER_TWO_HALF - OldHeight / 2, ImageBuffer(Image), TextureBuffer(FresizeTexture))
	SetBuffer(BackBuffer())
	ScaleRender(0.0, 0.0, SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth) * Float(Width) / Float(OldWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth) * Float(Height) / Float(OldHeight))
	; ~ Might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth, opt\GraphicHeight) if portrait sizes cause issues
	; ~ Everyone uses landscape so it's probably a non-issue
	CopyRect(opt\RealGraphicWidth / 2 - Width / 2, opt\RealGraphicHeight / 2 - Height / 2, Width, Height, 0, 0, BackBuffer(), ImageBuffer(Img))
	FreeImage(Image)
	Return(Img)
End Function

Function UpdateGamma2()
	If opt\DisplayMode = 1 Then
		If opt\RealGraphicWidth <> opt\GraphicWidth Lor opt\RealGraphicHeight <> opt\GraphicHeight Then
			SetBuffer(TextureBuffer(FresizeTexture))
			ClsColor(0, 0, 0) : Cls()
			CopyRect(0, 0, opt\GraphicWidth, opt\GraphicHeight, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_X, SMALLEST_POWER_TWO_HALF - mo\Viewport_Center_Y, BackBuffer(), TextureBuffer(FresizeTexture))
			SetBuffer(BackBuffer())
			ClsColor(0, 0, 0) : Cls()
			ScaleRender(0, 0, SMALLEST_POWER_TWO / Float(opt\GraphicWidth) * opt\AspectRatio, SMALLEST_POWER_TWO / Float(opt\GraphicWidth) * opt\AspectRatio)
			; ~ Might want to replace Float(opt\GraphicWidth) with Max(opt\GraphicWidth, opt\GraphicHeight) if portrait sizes cause issues
			; ~ Everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If opt\ScreenGamma >= 1.0 Then
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, SMALLEST_POWER_TWO_HALF - opt\RealGraphicWidth / 2, SMALLEST_POWER_TWO_HALF - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0) : Cls()
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
		EntityFX(FresizeImage, 1 + 32)
		EntityBlend(FresizeImage, 3)
		EntityAlpha(FresizeImage, opt\ScreenGamma - 1.0)
		ScaleRender((-1.0) / Float(opt\RealGraphicWidth), 1.0 / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth), SMALLEST_POWER_TWO / Float(opt\RealGraphicWidth))
	ElseIf opt\ScreenGamma < 1.0 ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect(0, 0, opt\RealGraphicWidth, opt\RealGraphicHeight, SMALLEST_POWER_TWO_HALF - opt\RealGraphicWidth / 2, SMALLEST_POWER_TWO_HALF - opt\RealGraphicHeight / 2, BackBuffer(), TextureBuffer(FresizeTexture))
		EntityBlend(FresizeImage, 1)
		ClsColor(0, 0, 0) : Cls()
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

Function UpdateWorld2()
	Local i%, np.NPCs
	
	wi\IsNVGBlinking = False
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE > 0 Then
		For i = 0 To MaxItemAmount - 1
			If Inventory[i] <> Null Then
				If (wi\NightVision = 1 And Inventory[i]\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory[i]\ItemTemplate\TempName = "supernvg") Lor (wi\SCRAMBLE > 0 And Inventory[i]\ItemTemplate\TempName = "scramble") Then
					Inventory[i]\State = Max(0.0, Inventory[i]\State - (fps\FPSFactor[0] * (0.02 * wi\NightVision) + (0.25 * (wi\SCRAMBLE > 0))))
					Power = Int(Inventory[i]\State)
					If Power = 0 Then ; ~ This NVG or SCRAMBLE can't be used
						HasBattery = 0
						If wi\SCRAMBLE > 0 Then
							CreateMsg("The batteries in this gear died.", 6.0)
						Else
							CreateMsg("The batteries in these night vision goggles died.", 6.0)
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
					np\NVX = EntityX(np\Collider, True)
					np\NVY = EntityY(np\Collider, True)
					np\NVZ = EntityZ(np\Collider, True)
				Next
				If wi\NVGTimer =< -10.0 Then wi\NVGTimer = 600.0
				wi\IsNVGBlinking = True
			EndIf
			wi\NVGTimer = wi\NVGTimer - fps\FPSFactor[0]
		EndIf
	EndIf
	
	If wi\SCRAMBLE > 0 Then
		If HasBattery = 0 
			If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN)
		Else
			SCRAMBLECHN = LoopSound2(SCRAMBLESFX, SCRAMBLECHN, Camera, Camera)
		EndIf
	Else
		If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN)
	EndIf
	
	If fps\FPSFactor[0] > 0.0 Then
		If HasBattery = 1 And ((MilliSecs() Mod 800) < 200) Then
			If (Not LowBatteryCHN[1]) Then
				LowBatteryCHN[1] = PlaySound_Strict(LowBatterySFX[1])
			ElseIf (Not ChannelPlaying(LowBatteryCHN[1])) Then
				LowBatteryCHN[1] = PlaySound_Strict(LowBatterySFX[1])
			EndIf
		EndIf
	EndIf
End Function

Const BRIGHTNESS# = 50.0

Global CurrTrisAmount%

Function RenderWorld2(Tween#)
	Local i%, k%, l%, np.NPCs
	
	CameraProjMode(ArkBlurCam, 0)
	CameraProjMode(Camera, 1)
	
	If wi\NightVision > 0 And wi\NightVision < 3 Then
		AmbientLight(Min(BRIGHTNESS * 2.0, 255.0), Min(BRIGHTNESS * 2.0, 255.0), Min(BRIGHTNESS * 2.0, 255.0))
	ElseIf wi\NightVision = 3
		AmbientLight(255.0, 255.0, 255.0)
	ElseIf PlayerRoom <> Null
		If PlayerRoom\RoomTemplate\Name <> "room173intro" And (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(me\Collider) =< 1040.0 * RoomScale) And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			AmbientLight(BRIGHTNESS, BRIGHTNESS, BRIGHTNESS)
		EndIf
	EndIf
	
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	
	Local HasBattery%
	Local Power%
	
	If (wi\NightVision > 0 And wi\NightVision <> 3) Lor wi\SCRAMBLE > 0 Then
		For i = 0 To MaxItemAmount - 1
			If Inventory[i] <> Null Then
				If (wi\NightVision = 1 And Inventory[i]\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory[i]\ItemTemplate\TempName = "supernvg") Lor (wi\SCRAMBLE > 0 And Inventory[i]\ItemTemplate\TempName = "scramble") Then
					Power = Int(Inventory[i]\State)
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
				If np\NVName <> "" And (Not np\HideFromNVG) Then ; ~ Don't waste your time if the string is empty
					PositionEntity(Temp2, np\NVX, np\NVY, np\NVZ)
					
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
							Text(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y), np\NVName, True, True)
							Text(mo\Viewport_Center_X + (xValue * mo\Viewport_Center_X), mo\Viewport_Center_Y - (yValue * mo\Viewport_Center_Y) + (30 * MenuScale), f2s(Sqr(Dist), 1) + " m", True, True)
						EndIf
					EndIf
				EndIf
			Next
			
			FreeEntity (Temp) : FreeEntity (Temp2)
			
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
		DrawImage(tt\ImageID[6], 40, mo\Viewport_Center_Y + 30)
	EndIf
	
	; ~ Render sprites
	CameraProjMode(ArkBlurCam, 2)
	CameraProjMode(Camera, 0)
	RenderWorld()
	CameraProjMode(ArkBlurCam, 0)
	
	If fps\FPSFactor[0] > 0.0 Then
		If HasBattery = 1 And ((MilliSecs() Mod 800) < 400)
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
	ScaleEntity(SPR, SMALLEST_POWER_TWO / Float(ArkSw), SMALLEST_POWER_TWO / Float(ArkSw), 1.0)
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
	
	For i = 0 To 2
		Select i
			Case 0
				;[Block]
				MovieFile = "GFX\menu\startup_Undertow"
				;[End Block]
			Case 1
				;[Block]
				MovieFile = "GFX\menu\startup_TSS"
				;[End Block]
			Case 2
				;[Block]
				MovieFile = "GFX\menu\startup_UET"
				;[End Block]
		End Select
		
		Local Movie% = OpenMovie(MovieFile + ".avi")
		
		If (Not Movie) Then
			PutINIValue(OptionFile, "Advanced", "Play Startup Videos", 0)
			Return
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

;~IDEal Editor Parameters:
;~C#Blitz3D