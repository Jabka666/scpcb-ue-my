Local InitErrorStr$ = ""

If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10)
If FileSize("BlitzMovie.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzMovie.dll" + Chr(13) + Chr(10)
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr)

Include "Source Code\Math_System.bb"
Include "Source Code\StrictLoads.bb"
Include "Source Code\Keys_System.bb"
Include "Source Code\INI_System.bb"

Const MaxFontIDAmount% = 5
Const MaxCreditsFontIDAmount% = 2

Type Fonts
	Field FontID%[MaxFontIDAmount]
	Field ConsoleFont%
	Field CreditsFontID%[MaxCreditsFontIDAmount]
End Type

Global fo.Fonts = New Fonts

Global MenuWhite%, MenuBlack%
Global ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
Global ButtonSFX2% = LoadSound_Strict("SFX\Interact\Button2.ogg")

Global EnableSFXRelease_Prev% = EnableSFXRelease

Global ArrowIMG%[4]

Global Fresize_Image%, Fresize_Texture%, Fresize_Texture2%
Global Fresize_Cam%

Global WireFrameState%

Global RealGraphicWidth%, RealGraphicHeight%
Global AspectRatioRatio#

Global TextureFloat#
Select TextureDetails
	Case 0
		;[Block]
		TextureFloat = 0.8
		;[End Block]
	Case 1
		;[Block]
		TextureFloat = 0.4
		;[End Block]
	Case 2
		;[Block]
		TextureFloat = 0.0
		;[End Block]
	Case 3
		;[Block]
		TextureFloat = -0.4
		;[End Block]
	Case 4
		;[Block]
		TextureFloat = -0.8
		;[End Block]
End Select

Type Launcher
	Field TotalGFXModes%
	Field GFXModes%
	Field SelectedGFXMode%
	Field GFXModeWidths%[64], GFXModeHeights%[64]
End Type

If LauncherEnabled Then
	Local lnchr.Launcher = New Launcher
	
	lnchr\TotalGFXModes = CountGfxModes3D()
	
	AspectRatioRatio = 1.0
	
	UpdateLauncher(lnchr)
	
	Delete(lnchr)
EndIf

; ~ New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode -- Love Mark
If DisplayMode = 1 Then
	Graphics3DExt(DesktopWidth(), DesktopHeight(), 4)
	
	RealGraphicWidth = DesktopWidth()
	RealGraphicHeight = DesktopHeight()
	
	AspectRatioRatio = (Float(GraphicWidth) / Float(GraphicHeight)) / (Float(RealGraphicWidth) / Float(RealGraphicHeight))
Else
	Graphics3DExt(GraphicWidth, GraphicHeight, (DisplayMode = 2) + 1)
	
	RealGraphicWidth = GraphicWidth
	RealGraphicHeight = GraphicHeight
	
	AspectRatioRatio = 1.0
EndIf

Global MenuScale# = GraphicHeight / 1024.0

SetBuffer(BackBuffer())

Type FramesPerSecondsTemplate
	Field CurTime%
	Field PrevTime%
	Field LoopDelay%
	Field FPSFactor#[2]
	Field PrevFPSFactor#
End Type

Global fpst.FramesPerSecondsTemplate = New FramesPerSecondsTemplate

Type FixedTimesteps
	Field Accumulator#
	Field PrevTime%
	Field CurrTime%
	Field FPS%
	Field TempFPS%
	Field FPSGoal%
End Type

Global ft.FixedTimesteps = New FixedTimesteps

Function ResetTimingAccumulator()
	ft\Accumulator = 0.0
End Function
	
Global CurrFrameLimit# = (FrameLimit - 19.0) / 100.0

Global CurrFOV# = FOV - 40

SeedRnd(MilliSecs())

Global GameSaved%

Global CanSave% = True

AppTitle("SCP - Containment Breach Ultimate Edition v" + VersionNumber)

PlayStartupVideos()

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict("LoadingScreens\loading_back.png")
InitLoadingScreens("LoadingScreens\loading_screens.ini")

; ~ For some reason, Blitz3D doesn't load fonts that have filenames that
; ~ Don't match their "internal name" (i.e. their display name in applications like Word and such)
; ~ As a workaround, I moved the files and renamed them so they
; ~ Can load without FastText
fo\FontID[0] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 16)
fo\FontID[1] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 52)
fo\FontID[2] = LoadFont_Strict("GFX\font\DS-DIGI\DS-Digital.ttf", 20)
fo\FontID[3] = LoadFont_Strict("GFX\font\DS-DIGI\DS-Digital.ttf", 60)
fo\FontID[4] = LoadFont_Strict("GFX\font\Journal\Journal.ttf", 58)
fo\ConsoleFont = LoadFont_Strict("GFX\font\Andale\Andale Mono.ttf", 16)

SetFont(fo\FontID[1])

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blink_meter.png")

DrawLoading(0, True)

; ~ Viewport
Global Viewport_Center_X% = GraphicWidth / 2, Viewport_Center_Y% = GraphicHeight / 2

; ~ Mouselook
Global Mouselook_X_Inc# = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the X axis
Global Mouselook_Y_Inc# = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the Y axis
; ~ Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop
Global Mouse_Left_Limit% = 250, Mouse_Right_Limit% = GraphicsWidth() - 250
Global Mouse_Top_Limit% = 150, Mouse_Bottom_Limit% = GraphicsHeight() - 150 ; ~ As above
Global Mouse_X_Speed_1#, Mouse_Y_Speed_1#

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

Type PlayerStats
	Field KillTimer#, KillAnim%, FallTimer#, DeathTimer#
	Field Sanity#, RestoreSanity%
	Field ForceMove#, ForceAngle#
	Field Playable%, PlayTime%
	Field BlinkTimer#, BLINKFREQ#, BlinkEffect#, BlinkEffectTimer#, EyeIrritation#, EyeStuck#
	Field Stamina#, StaminaEffect#, StaminaEffectTimer#
	Field CameraShakeTimer#, Shake#, CameraShake#
	Field Vomit%, VomitTimer#, Regurgitate%
	Field HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#
	Field Injuries#, Bloodloss#, PrevInjuries#, PrevBloodloss#, HealTimer#
	Field DropSpeed#, HeadDropSpeed#, CurrSpeed#
	Field Crouch%, CrouchState#
	Field SndVolume#
	Field SelectedEnding$, EndingScreen%, EndingTimer#
	Field CreditsScreen%, CreditsTimer#
	Field BlurVolume#, BlurTimer#
	Field LightBlink#, LightFlash#
	Field CurrCameraZoom#
	Field RefinedItems%
	Field Deaf%, DeafTimer#
	Field Zombie%
	Field Detected%
	Field ExplosionTimer#
	Field Zone%
	Field Collider%, Head%
End Type

Global me.PlayerStats = New PlayerStats

Type WearableItems
	Field GasMask%, GasMaskFogTimer#
	Field HazmatSuit%
	Field BallisticVest%
	Field BallisticHelmet%
	Field NightVision%, NVGTimer#, IsNVGBlinking%
	Field SCRAMBLE%
End Type

Global wi.WearableItems = New WearableItems

Include "Source Code\Achievements_System.bb"

Global CameraPitch#, Side#

Global PlayerRoom.Rooms

Global GrabbedEntity%

Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%, DoubleClickSlot%

Type Cheats
	Field GodMode%
	Field NoBlink%
	Field NoTarget%
	Field NoClip%, NoClipSpeed#
	Field InfiniteStamina%
	Field SuperMan%, SuperManTimer#
	Field DebugHUD%
End Type

Function ClearCheats(chs.Cheats)
	chs\GodMode = 0
	chs\NoBlink = 0
	chs\NoTarget = 0
	chs\NoClip = 0
	chs\NoClipSpeed = 2.0
	chs\InfiniteStamina = 0
	chs\SuperMan = 0
	chs\SuperManTimer = 0.0
	chs\DebugHUD = 0
End Function

Global chs.Cheats = New Cheats

ClearCheats(chs)

Global CoffinDistance# = 100.0

Global ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

Global MainMenuOpen%, MenuOpen%, StopHidingTimer#, InvOpen%
Global OtherOpen.Items = Null

Const SubjectName$ = "Subject D-9341"

Type Messages
	Field Msg$
	Field Timer#
	Field DeathMsg$
	Field KeypadMsg$, KeypadInput$, KeypadTimer#
End Type

Global msg.Messages = New Messages

Global AccessCode%

Global DrawHandIcon%
Global DrawArrowIcon%[4]

Include "Source Code\Difficulty.bb"

Global MTFTimer#

Global RadioState#[9]
Global RadioState3%[9]
Global RadioState4%[10]
Global RadioCHN%[7]

Const MaxMiscTextureIDAmount% = 18
Const MaxMonitorTextureIDAmount% = 5
Const MaxOverlayTextureIDAmount% = 12
Const MaxOverlayIDAmount% = 12
Const MaxDecalTextureIDAmount% = 20
Const MaxParticleTextureIDAmount% = 9
Const MaxLightSpriteIDAmount% = 3
Const MaxIconIDAmount% = 6
Const MaxImageIDAmount% = 13

Type TextureTemplate
	Field MiscTextureID%[MaxMiscTextureIDAmount]
	Field MonitorTextureID%[MaxMonitorTextureIDAmount]
	Field DecalTextureID%[MaxDecalTextureIDAmount]
	Field ParticleTextureID%[MaxParticleTextureIDAmount]
	Field LightSpriteID%[MaxLightSpriteIDAmount]
	Field IconID%[MaxIconIDAmount]
	Field ImageID%[MaxImageIDAmount]
	Field OverlayTextureID%[MaxOverlayTextureIDAmount]
	Field OverlayID%[MaxOverlayIDAmount]
End Type

Global tt.TextureTemplate = New TextureTemplate

Const MaxMTModelIDAmount% = 7
Const MaxMonitorModelIDAmount% = 3
Const MaxDoorModelIDAmount% = 11
Const MaxButtonModelIDAmount% = 5
Const MaxLeverModelIDAmount% = 2
Const MaxCamModelIDAmount% = 2
Const MaxMiscModelIDAmount% = 1
Const MaxNPCModelIDAmount% = 34

Type Objects
	Field DoorModelID%[MaxDoorModelIDAmount]
	Field NPCModelID%[MaxNPCModelIDAmount]
	Field MTModelID%[MaxMTModelIDAmount]
	Field ButtonModelID%[MaxButtonModelIDAmount]
	Field LeverModelID%[MaxLeverModelIDAmount]
	Field CamModelID%[MaxCamModelIDAmount]
	Field MonitorModelID%[MaxMonitorModelIDAmount]
	Field MiscModelID%[MaxMiscModelIDAmount]
End Type

Global o.Objects = New Objects

Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#, ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR% = 255, ConsoleG% = 255, ConsoleB% = 255

Type ConsoleMsg
	Field Txt$
	Field IsCommand%
	Field R%, G%, B%
End Type

Function CreateConsoleMsg(Txt$, R% = -1, G% = -1, B% = -1, IsCommand% = False)
	Local c.ConsoleMsg = New ConsoleMsg
	
	Insert c Before First ConsoleMsg
	
	c\Txt = Txt
	c\IsCommand = IsCommand
	
	c\R = R
	c\G = G
	c\B = B
	
	If c\R < 0 Then c\R = ConsoleR
	If c\G < 0 Then c\G = ConsoleG
	If c\B < 0 Then c\B = ConsoleB
End Function

Function UpdateConsole()
	Local Tex%, Tex2%, InBar%, InBox%, MouseScroll#
	Local e.Events
	Local x%, y%, Width%, Height%
	
	If CanOpenConsole = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		SetFont(fo\ConsoleFont)
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		If ConsoleVersion = 1 Then
			x = 0
			y = GraphicHeight - 300 * MenuScale
			Width = GraphicWidth
			Height = 300 * MenuScale - 30 * MenuScale
		Else
			x = 20
		    y = 40
		    Width = 400
		    Height = 500
		EndIf
		
		Local Temp%, i%
		Local Args$, StrTemp$, StrTemp2$, StrTemp3$, StrTemp4$
		Local ev.Events, r.Rooms, it.Items
		
		DrawFrame(x, y, Width, Height + 30 * MenuScale)
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + 15 * MenuScale
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		If ScrollBarHeight > Height Then ScrollBarHeight = Height
		If ConsoleHeight < Height Then ConsoleHeight = Height
		
		Color(50, 50, 50)
		InBar = MouseOn(x + Width - 26 * MenuScale, y, 26 * MenuScale, Height)
		If InBar Then Color(70, 70, 70)
		Rect(x + Width - 26 * MenuScale, y, 26 * MenuScale, Height, True)
		
		Color(120, 120, 120)
		InBox = MouseOn(x + Width - 23 * MenuScale, y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		If InBox Then Color(200, 200, 200)
		If ConsoleScrollDragging Then Color(255, 255, 255)
		Rect(x + Width - 23 * MenuScale, y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight, True)
		
		If (Not MouseDown(1)) Then
			ConsoleScrollDragging = False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll + ((ScaledMouseY() - ConsoleMouseMem) * Height / ScrollBarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If MouseHit1 Then
				If InBox Then
					ConsoleScrollDragging = True
					ConsoleMouseMem = ScaledMouseY()
				ElseIf InBar Then
					ConsoleScroll = ConsoleScroll + ((ScaledMouseY() - (y + Height)) * ConsoleHeight / Height + (Height / 2))
					ConsoleScroll = ConsoleScroll / 2
				EndIf
			EndIf
		EndIf
		
		MouseScroll = MouseZSpeed()
		If MouseScroll = 1 Then
			ConsoleScroll = ConsoleScroll - 15 * MenuScale
		ElseIf MouseScroll= -1 Then
			ConsoleScroll = ConsoleScroll + 15 * MenuScale
		EndIf
		
		Local ReissuePos%
		
		If KeyHit(200) Then
			ReissuePos = 0
			If (ConsoleReissue = Null) Then
				ConsoleReissue = First ConsoleMsg
				
				While (ConsoleReissue <> Null)
					If (ConsoleReissue\IsCommand) Then
						Exit
					EndIf
					ReissuePos = ReissuePos - 15 * MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos - 15 * MenuScale
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				ReissuePos = ReissuePos - 15 * MenuScale
				
				While True
					If (ConsoleReissue = Null) Then
						ConsoleReissue = First ConsoleMsg
						ReissuePos = 0
					EndIf
					
					If (ConsoleReissue\IsCommand) Then
						Exit
					EndIf
					ReissuePos = ReissuePos - 15 * MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null Then
				ConsoleInput = ConsoleReissue\Txt
				ConsoleScroll = ReissuePos + (Height / 2)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			ReissuePos = (-ConsoleHeight) + 15 * MenuScale
			If (ConsoleReissue = Null) Then
				ConsoleReissue = Last ConsoleMsg
				
				While (ConsoleReissue <> Null)
					If (ConsoleReissue\IsCommand) Then
						Exit
					EndIf
					ReissuePos = ReissuePos + 15 * MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos + 15 * MenuScale
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				ReissuePos = ReissuePos + 15 * MenuScale
				
				While True
					If (ConsoleReissue = Null) Then
						ConsoleReissue = Last ConsoleMsg
						ReissuePos = (-ConsoleHeight) + 15 * MenuScale
					EndIf
					
					If (ConsoleReissue\IsCommand) Then
						Exit
					EndIf
					ReissuePos = ReissuePos + 15 * MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null Then
				ConsoleInput = ConsoleReissue\Txt
				ConsoleScroll = ReissuePos + (Height / 2)
			EndIf
		EndIf
		
		If ConsoleScroll < (-ConsoleHeight) + Height Then ConsoleScroll = (-ConsoleHeight) + Height
		If ConsoleScroll > 0 Then ConsoleScroll = 0
		
		Color(255, 255, 255)
		
		SelectedInputBox = 2
		
		Local OldConsoleInput$ = ConsoleInput
		
		ConsoleInput = InputBox(x, y + Height, Width, 30 * MenuScale, ConsoleInput, 2)
		If OldConsoleInput <> ConsoleInput Then
			ConsoleReissue = Null
		EndIf
		ConsoleInput = Left(ConsoleInput, 100)
		
		If KeyHit(28) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput, 255, 255, 0, True)
			If Instr(ConsoleInput, " ") > 0 Then
				StrTemp = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp = Lower(ConsoleInput)
			EndIf
			
			Select Lower(StrTemp)
				Case "help"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
					
					Select Lower(StrTemp)
						Case "1", ""
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 1 / 3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("- noblink")
							CreateConsoleMsg("- asd")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- newyear") 
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- weed")
							CreateConsoleMsg("- money")
							CreateConsoleMsg("- debughud")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help 2 / 3" + Chr(34) + " to find more commands.")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "2"
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 2 / 3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- reset372")
							CreateConsoleMsg("- 106retreat")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- disable049")
							CreateConsoleMsg("- enable049")
							CreateConsoleMsg("- disable966")
							CreateConsoleMsg("- enable966")
							CreateConsoleMsg("- enablecontrol") 
							CreateConsoleMsg("- disablecontrol") 
							CreateConsoleMsg("- unlockcheckpoints") 
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- disablenuke")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help 3 / 3" + Chr(34) + " to find more commands.")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "3"
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 3 / 3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- playmusic [clip + .wav / .ogg]")
							CreateConsoleMsg("- camerafog [near] [far]")
							CreateConsoleMsg("- spawn [npc type] [state]")
							CreateConsoleMsg("- injure [value]")
							CreateConsoleMsg("- infect [value]")
							CreateConsoleMsg("- crystal [value]") 
							CreateConsoleMsg("- teleport [room name]")
							CreateConsoleMsg("- spawnitem [item name]")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "camerafog"
							;[Block]
							CreateConsoleMsg("HELP - camerafog")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the draw distance of the fog.")
							CreateConsoleMsg("The fog begins generating at 'CameraFogNear' units")
							CreateConsoleMsg("away from the camera and becomes completely opaque")
							CreateConsoleMsg("at 'CameraFogFar' units away from the camera.")
							CreateConsoleMsg("Example: camerafog 20 40")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "noclip", "fly"
							;[Block]
							CreateConsoleMsg("HELP - noclip")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles NoClip, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Allows the camera to move in any direction while")
							CreateConsoleMsg("by passing collision.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "noblink", "nb"
							;[Block]
							CreateConsoleMsg("HELP - noblonk")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles NoBlink, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Removes player's blinking.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "godmode", "god"
							;[Block]
							CreateConsoleMsg("HELP - godmode")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles GodMode, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Prevents player death under normal circumstances.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "infinitestamina", "is"
							;[Block]
							CreateConsoleMsg("HELP - infinitestamina")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles InfiniteStamina, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Increases player's stamina to infinite value.")
							CreateConsoleMsg("******************************")
						Case "notarget", "nt"
							;[Block]
							CreateConsoleMsg("HELP - notarget")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles NoTarget, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Makes player to be invisible for NPCs.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "wireframe", "wf"
							;[Block]
							CreateConsoleMsg("HELP - wireframe")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles wireframe, unless a valid parameter")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("Allows only the edges of geometry to be rendered,")
							CreateConsoleMsg("making everything else transparent.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "spawnitem"
							;[Block]
							CreateConsoleMsg("HELP - spawnitem")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an item at the player's location.")
							CreateConsoleMsg("Any name that can appear in your inventory")
							CreateConsoleMsg("is a valid parameter.")
							CreateConsoleMsg("Example: spawnitem Key Card Omni")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "spawn"
							;[Block]
							CreateConsoleMsg("HELP - spawn")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an NPC at the player's location.")
							CreateConsoleMsg("Valid parameters are:")
							CreateConsoleMsg("008zombie / 049 / 049-2 / 066 / 096 / 106 / 173 / 860")
							CreateConsoleMsg("/ 178-1 / 372 / 513-1 / 966 / 1499-1 / class-d / 939")
							CreateConsoleMsg("/ guard / mtf / apache / tentacle")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "reset372" 
							;[Block]
						    CreateConsoleMsg("HELP - reset372")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-372 to inactive state.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "106retreat" 
							;[Block]
						    CreateConsoleMsg("HELP - 106retreat")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-106 to inactive state.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable106"
							;[Block]
						   	CreateConsoleMsg("HELP - disable106")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-106 from the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable106"
							;[Block]
						   	CreateConsoleMsg("HELP - enable106")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-106 to the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable173"
							;[Block]
						   	CreateConsoleMsg("HELP - disable173")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-173 from the map.")
							CreateConsoleMsg("******************************")	
							;[End Block]
						Case "enable173"
							;[Block]
						   	CreateConsoleMsg("HELP - enable173")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-173 to the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "reset096" 
							;[Block]
						    CreateConsoleMsg("HELP - reset096")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-096 to idle state.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enablecontrol" 
							;[Block]
							CreateConsoleMsg("HELP - enablecontrol")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Turns on the Remote Door Control lever.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disablecontrol" 
							;[Block]
							CreateConsoleMsg("HELP - disablecontrol")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Turns off the Remote Door Control lever.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "asd"
							;[Block]
							CreateConsoleMsg("HELP - asd")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Activates all cheats.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "unlockcheckpoints" 
							;[Block]
							CreateConsoleMsg("HELP - unlockcheckpoints")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Unlocks all checkpoints.")
							CreateConsoleMsg("******************************")	
							;[End Block]
						Case "disable049"
							;[Block]
						   	CreateConsoleMsg("HELP - disable049")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-049 from the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "enable049"
							;[Block]
						   	CreateConsoleMsg("HELP - enable049")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-049 to the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "disable966"
							;[Block]
						   	CreateConsoleMsg("HELP - disable966")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Removes SCP-966 from the map.")
							CreateConsoleMsg("******************************")	
							;[End Block]
						Case "enable966"
							;[Block]
						   	CreateConsoleMsg("HELP - enable966")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Returns SCP-966 to the map.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "revive", "undead", "resurrect"
							;[Block]
							CreateConsoleMsg("HELP - revive")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Resets the player's death timer after the dying")
							CreateConsoleMsg("animation triggers.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "teleport"
							;[Block]
							CreateConsoleMsg("HELP - teleport")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Teleports the player to the first instance")
							CreateConsoleMsg("of the specified room. Any room that appears")
							CreateConsoleMsg("in rooms.ini is a valid parameter.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "stopsound", "stfu"
							;[Block]
							CreateConsoleMsg("HELP - stopsound")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Stops all currently playing sounds.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "weed", "scp-420-j", "420j", "scp420-j", "scp-420j", "420"
							;[Block]
							CreateConsoleMsg("HELP - weed")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Generates dank memes.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "playmusic"
							;[Block]
							CreateConsoleMsg("HELP - playmusic")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Will play tracks in .ogg / .wav format")
							CreateConsoleMsg("from " + Chr(34) + "SFX\Music\Custom\" + Chr(34) + ".")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "infect"
							;[Block]
							CreateConsoleMsg("HELP - infect")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("SCP-008 infects player.")
							CreateConsoleMsg("Example: infect 80")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "crystal" 
							;[Block]
							CreateConsoleMsg("HELP - crystal")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("SCP-409 crystallizes player.")
							CreateConsoleMsg("Example: crystal 52")
							CreateConsoleMsg("******************************")
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg("There is no help available for that command.", 255, 150, 0)
							;[End Block]
					End Select
					;[End Block]
				Case "ending"
					;[Block]
					me\SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					me\KillTimer = -0.1
					;[End Block]
				Case "noclipspeed"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					chs\NoClipSpeed = Float(StrTemp)
					;[End Block]
				Case "injure"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					me\Injuries = Float(StrTemp)
					;[End Block]
				Case "infect"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_008\Timer = Float(StrTemp)
					;[End Block]
				Case "crystal"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_409\Timer = Float(StrTemp)
					;[End Block]
				Case "heal"
					;[Block]
					me\Injuries = 0.0
					me\Bloodloss = 0.0
					
					me\BlurTimer = 0.0
					
					I_008\Timer = 0.0
					I_409\Timer = 0.0
					
					me\DeafTimer = 0.0
					me\DeathTimer = 0.0
					
					me\Stamina = 100.0
					
					For i = 0 To 5
						I_1025\State[i] = 0.0
					Next
					
					If I_427\Timer >= 70.0 * 360.0 Then I_427\Timer = 0.0
					
					For e.Events = Each Events
						If e\EventName = "1048a" Then
							If PlayerRoom = e\room Then me\BlinkTimer = -10.0
							If e\room\Objects[0] <> 0 Then
								FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							EndIf
							RemoveEvent(e)
						EndIf
					Next
					
					If me\BlinkEffect > 1.0 Then 
						me\BlinkEffect = 1.0
						me\BlinkEffectTimer = 0.0
					EndIf
					
					If me\StaminaEffect > 1.0 Then
						me\StaminaEffect = 1.0
						me\StaminaEffectTimer = 0.0
					EndIf
					;[End Block]
				Case "teleport"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							PositionEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.7, EntityZ(r\OBJ))
							ResetEntity(me\Collider)
							UpdateDoors()
							UpdateRooms()
							For it.Items = Each Items
								it\DistTimer = 0.0
							Next
							PlayerRoom = r
							CreateConsoleMsg("Successfully teleported to: " + StrTemp + ".")
							Exit
						EndIf
					Next
					
					If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("Room not found.", 255, 150, 0)
					;[End Block]
				Case "spawnitem"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Temp = False 
					For itt.Itemtemplates = Each ItemTemplates
						If Lower(itt\Name) = StrTemp Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						ElseIf (Lower(itt\TempName) = StrTemp) Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						EndIf
					Next
					
					If Temp = False Then CreateConsoleMsg("Item not found.", 255, 150, 0)
					;[End Block]
				Case "wireframe", "wf"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							WireFrameState = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							WireFrameState = False
							;[End Block]
						Default
							;[Block]
							WireFrameState = (Not WireFrameState)
							;[End Block]
					End Select
					
					If WireFrameState Then
						CreateConsoleMsg("WIREFRAME ON")
					Else
						CreateConsoleMsg("WIREFRAME OFF")	
					EndIf
					
					WireFrame(WireFrameState)
					;[End Block]
				Case "reset096"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							n\State = 0.0
							StopStream_Strict(n\SoundCHN) : n\SoundCHN = 0
							If n\SoundCHN2 <> 0
								StopStream_Strict(n\SoundCHN2) : n\SoundCHN2 = 0
							EndIf
							Exit
						EndIf
					Next
					;[End Block]
				Case "reset372" 
					;[Block]				
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype372 Then
							RemoveNPC(n)
							CreateEvent("room372", "room372", 0, 0.0)   
							Exit
						EndIf
				    Next
					;[End Block]
				Case "disable173"
					;[Block]
					Curr173\Idle = 3 ; ~ This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of SCP-173 state checks.
					HideEntity(Curr173\OBJ)
					HideEntity(Curr173\Collider)
					;[End Block]
				Case "enable173"
					;[Block]
					Curr173\Idle = False
					ShowEntity(Curr173\OBJ)
					ShowEntity(Curr173\Collider)
					;[End Block]
				Case "disable106"
					;[Block]
					Curr106\Idle = True
					Curr106\State = 200000.0
					Curr106\Contained = True
					;[End Block]
				Case "enable106"
					;[Block]
					Curr106\Idle = False
					Curr106\Contained = False
					ShowEntity(Curr106\Collider)
					ShowEntity(Curr106\OBJ)
					;[End Block]
				Case "disable966"
				    ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype966
			                n\State = -1.0
			                HideEntity(n\Collider)
                            HideEntity(n\OBJ)
			            EndIf
			        Next
			        ;[End Block]
			    Case "enable966"
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype966
			                n\State = 0.0
			                ShowEntity(n\Collider)
			                If WearinNightVision > 0 Then ShowEntity(n\OBJ)
			            EndIf
			        Next
					;[End Block]
				Case "disable049" 
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype049
							n\Idle = True
			                n\State = 0.0
			                HideEntity(n\Collider)
			                HideEntity(n\OBJ)
			            EndIf
			        Next
			        ;[End Block]
			    Case "enable049"
			        ;[Block]
			        For n.NPCs = Each NPCs
			            If n\NPCtype = NPCtype049
			                n\State = 2.0
			                ShowEntity(n\Collider)
			                ShowEntity(n\OBJ)
							n\Idle = False
			            EndIf
			        Next
					;[End Block]
				Case "106retreat"
					;[Block]
					If Curr106\State =< 0.0 Then
						Curr106\State = Rnd(22000.0, 27000.0)
						PositionEntity(Curr106\Collider, 0.0, 500.0, 0.0)
					Else
						CreateConsoleMsg("SCP-106 is currently not active, so it cannot retreat.")
					EndIf
					;[End Block]
				Case "halloween"
					;[Block]
					tt\MiscTextureID[14] = (Not tt\MiscTextureID[14])
					If tt\MiscTextureID[14] Then
						Tex = LoadTexture_Strict("GFX\npcs\scp_173_H.png", 1)
						EntityTexture(Curr173\OBJ, Tex, 0, 0)
						FreeTexture(Tex)
						CreateConsoleMsg("173 JACK-O-LANTERN ON")
					Else
						If tt\MiscTextureID[15] Then tt\MiscTextureID[15] = (Not tt\MiscTextureID[15])
						Tex2 = LoadTexture_Strict("GFX\npcs\scp_173.png", 1)
						EntityTexture(Curr173\OBJ, Tex2, 0, 0)
						FreeTexture(Tex2)
						CreateConsoleMsg("173 JACK-O-LANTERN OFF")
					EndIf
					;[End Block]
				Case "newyear" 
					;[Block]
					tt\MiscTextureID[15] = (Not tt\MiscTextureID[15])
					If tt\MiscTextureID[15] Then
						Tex = LoadTexture_Strict("GFX\npcs\scp_173_NY.png", 1)
						EntityTexture(Curr173\OBJ, Tex, 0, 0)
						FreeTexture(Tex)
						CreateConsoleMsg("173 COOKIE ON")
					Else
						If tt\MiscTextureID[14] Then tt\MiscTextureID[14] = (Not tt\MiscTextureID[14])
						Tex2 = LoadTexture_Strict("GFX\npcs\scp_173.png", 1)
						EntityTexture(Curr173\OBJ, Tex2, 0, 0)
						FreeTexture(Tex2)
						CreateConsoleMsg("173 COOKIE OFF")
					EndIf
					;[End Block]
				Case "sanic"
					;[Block]
					chs\SuperMan = (Not chs\SuperMan)
					If chs\SuperMan = True Then
						CreateConsoleMsg("GOTTA GO FAST")
					Else
						CreateConsoleMsg("WHOA SLOW DOWN")
					EndIf
					;[End Block]
				Case "scp-420-j", "420", "weed", "scp420-j", "scp-420j", "420j"
					;[Block]
					For i = 1 To 20
						If Rand(2) = 1 Then
							it.Items = CreateItem("Some SCP-420-J", "scp420j", EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						Else
							it.Items = CreateItem("Joint", "joint", EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						EndIf
						EntityType(it\Collider, HIT_ITEM)
					Next
					PlaySound_Strict(LoadTempSound("SFX\Music\Using420J.ogg"))
					;[End Block]
				Case "godmode", "god"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\GodMode = True		
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\GodMode = False
							;[End Block]
						Default
							;[Block]
							chs\GodMode = (Not chs\GodMode)
							;[End Block]
					End Select	
					If chs\GodMode Then
						CreateConsoleMsg("GODMODE ON")
					Else
						CreateConsoleMsg("GODMODE OFF")	
					EndIf
					;[End Block]
				Case "revive", "undead", "resurrect"
					;[Block]
					me\DropSpeed = -0.1
					me\HeadDropSpeed = 0.0
					me\Shake = 0.0
					me\CurrSpeed = 0.0
					
					me\HeartBeatVolume = 0.0
					
					me\CameraShake = 0.0
					me\Shake = 0.0
					me\LightFlash = 0.0
					me\BlurTimer = 0.0
					
					me\FallTimer = 0.0
					MenuOpen = False
					
					ClearCheats(chs)
					
					; ~ If death by SCP-173, enable GodMode, prevent instant death again -- Salvage
					If Curr173\Idle Then
						CreateConsoleMsg("Death by SCP-173 causes GodMode to be enabled!")
						chs\GodMode = True
						Curr173\Idle = False
					Else
						chs\GodMode = False
					EndIf
					
					ShowEntity(me\Collider)
					
					me\KillTimer = 0.0
					me\KillAnim = 0
					;[End Block]
				Case "noclip", "fly"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoClip = True
							me\Playable = True
							UnableToMove = False
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoClip = False	
							RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							;[End Block]
						Default
							;[Block]
							chs\NoClip = (Not chs\NoClip)
							If chs\NoClip = False Then		
								RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							Else
								me\Playable = True
								UnableToMove = False
							EndIf
							;[End Block]
					End Select
					
					If chs\NoClip Then
						CreateConsoleMsg("NOCLIP ON")
					Else
						CreateConsoleMsg("NOCLIP OFF")
					EndIf
					
					me\DropSpeed = 0.0
					;[End Block]
				Case "noblink", "nb"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoBlink = True		
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoBlink = False
							;[End Block]
						Default
							;[Block]
							chs\NoBlink = (Not chs\NoBlink)
							;[End Block]
					End Select	
					If chs\NoBlink Then
						CreateConsoleMsg("NOBLINK ON")
					Else
						CreateConsoleMsg("NOBLINK OFF")	
					EndIf
					;[End Block]
				Case "debughud"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\DebugHUD = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\DebugHUD = False
							;[End Block]
						Default
							;[Block]
							chs\DebugHUD = (Not chs\DebugHUD)
							;[End Block]
					End Select
					
					If chs\DebugHUD Then
						CreateConsoleMsg("DEBUG MODE ON")
					Else
						CreateConsoleMsg("DEBUG MODE OFF")
					EndIf
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\Channels[i] <> 0 Then
								StopChannel(snd\Channels[i])
							EndIf
						Next
					Next
					
					If IntercomStreamCHN <> 0 Then
						StopStream_Strict(IntercomStreamCHN)
						IntercomStreamCHN = 0
					EndIf
					
					For e.Events = Each Events
						If e\EventName = "room173" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							FreeEntity(e\room\Objects[1]) : e\room\Objects[1] = 0
							PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(Curr173\Collider)
							ShowEntity(Curr173\OBJ)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("Stopped all sounds.")
					;[End Block]
				Case "camerafog"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CameraFogNear = Float(Left(Args, Len(Args) - Instr(Args, " ")))
					CameraFogFar = Float(Right(Args, Len(Args) - Instr(Args, " ")))
					CreateConsoleMsg("Near set to: " + CameraFogNear + ", far set to: " + CameraFogFar)
					;[End Block]
				Case "spawn"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1)
					StrTemp2 = Piece(Args, 2)
					
					; ~ Hacky fix for when the user doesn't input a second parameter.
					If StrTemp <> StrTemp2 Then
						Console_SpawnNPC(StrTemp, StrTemp2)
					Else
						Console_SpawnNPC(StrTemp)
					EndIf
					;[End Block]
				Case "infinitestamina", "infstam", "is"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\InfiniteStamina = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\InfiniteStamina = False
							;[End Block]
						Default
							;[Block]
							chs\InfiniteStamina = (Not chs\InfiniteStamina)
							;[End Block]
					End Select
					
					If chs\InfiniteStamina
						CreateConsoleMsg("INFINITE STAMINA ON")
					Else
						CreateConsoleMsg("INFINITE STAMINA OFF")	
					EndIf
					;[End Block]
				Case "money", "rich"
					;[Block]
					For i = 1 To 20
					    If Rand(2) = 1 Then
						    it.Items = CreateItem("Quarter", "25ct", EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
					    Else
					        it.Items = CreateItem("Coin", "coin", EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
					    EndIf
					    EntityType(it\Collider, HIT_ITEM)
					Next
					;[End Block]
				Case "disablecontrol"
				    ;[Block]
				    For e2.Events = Each Events
				        If e2\EventName = "room2ccont"
							UpdateLever(e2\room\Objects[5])
							RotateEntity(e2\room\Objects[5], 0.0, EntityYaw(e2\room\Objects[5]), 0.0)
							RemoteDoorOn = False
						EndIf
					Next
				    CreateConsoleMsg("Remote door control disabled.", 255, 255, 255)
				    ;[End Block]
				Case "enablecontrol"
				    ;[Block]
				    For e2.Events = Each Events
				        If e2\EventName = "room2ccont"
							UpdateLever(e2\room\Objects[5])
							RotateEntity(e2\room\Objects[5], 0.0, EntityYaw(e2\room\Objects[5]), 30.0)
							RemoteDoorOn = True
						EndIf
					Next
				    CreateConsoleMsg("Remote door control enabled.", 255, 255, 255)
				    ;[End Block]
			    Case "unlockcheckpoints"
			        ;[Block]
			        For e2.Events = Each Events
				        If e2\EventName = "room2sl"
							e2\EventState3 = 0.0
							UpdateLever(e2\room\Levers[0])
							RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 0.0)
							TurnCheckpointMonitorsOff(0)
						EndIf
					Next
					
					For e2.Events = Each Events
				        If e2\EventName = "room008"
							e2\EventState = 2.0
							UpdateLever(e2\room\Levers[0])
							RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 30.0)
							TurnCheckpointMonitorsOff(1)
							Exit
						EndIf
				    Next
					
				    CreateConsoleMsg("Checkpoints are now unlocked.", 255, 255, 255)								
					;[End Block]
				Case "disablenuke"
					;[Block]
					For e2.Events = Each Events
				        If e2\EventName = "room2nuke"
							e2\EventState = 0.0
							UpdateLever(e2\room\Objects[1])
							UpdateLever(e2\room\Objects[3])
							RotateEntity(e2\room\Objects[1], 0.0, EntityYaw(e2\room\Objects[1]), 0.0)
							RotateEntity(e2\room\Objects[3], 0.0, EntityYaw(e2\room\Objects[3]), 0.0)
							Exit
						EndIf
				    Next
					;[End Block]
				Case "unlockexits"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "a"
							;[Block]
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg("Gate A is now unlocked.")	
							;[End Block]
						Case "b"
							;[Block]
							For e.Events = Each Events
								If e\EventName = "gateb" Then
									e\EventState3 = 1.0
									e\room\RoomDoors[4]\Open = True
									Exit
								EndIf
							Next	
							CreateConsoleMsg("Gate B is now unlocked.")	
							;[End Block]
						Default
							;[Block]
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
								ElseIf e\EventName = "gateb" Then
									e\EventState3 = 1.0
									e\room\RoomDoors[4]\Open = True
								EndIf
							Next
							CreateConsoleMsg("Gate A and B are now unlocked.")	
							;[End Block]
					End Select
					RemoteDoorOn = True
					;[End Block]
				Case "kill", "suicide"
					;[Block]
					me\KillTimer = -1.0
					Select Rand(4)
						Case 1
							;[Block]
							msg\DeathMsg = "[DATA REDACTED]"
							;[End Block]
						Case 2
							;[Block]
							msg\DeathMsg = SubjectName + " found dead in Sector [DATA REDACTED]. "
							msg\DeathMsg = msg\DeathMsg + "The subject appears to have attained no physical damage, and there is no visible indication as to what killed him. "
							msg\DeathMsg = msg\DeathMsg + "Body was sent for autopsy."
							;[End Block]
						Case 3
							;[Block]
							msg\DeathMsg = "EXCP_ACCESS_VIOLATION"
							;[End Block]
						Case 4
							;[Block]
							msg\DeathMsg = SubjectName + " found dead in Sector [DATA REDACTED]. "
							msg\DeathMsg = msg\DeathMsg + "The subject appears to have scribbled the letters " + Chr(34) + "kys" + Chr(34) + " in his own blood beside him. "
							msg\DeathMsg = msg\DeathMsg + "No other signs of physical trauma or struggle can be observed. Body was sent for autopsy."
							;[End Block]
					End Select
					;[End Block]
				Case "playmusic"
					;[Block]
					; ~ I think this might be broken since the FMod library streaming was added -- Mark
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					If StrTemp <> "" Then
						PlayCustomMusic = True
						If CustomMusic <> 0 Then FreeSound_Strict(CustomMusic) : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel(MusicCHN)
						CustomMusic = LoadSound_Strict("SFX\Music\Custom\" + StrTemp)
						If CustomMusic = 0 Then
							PlayCustomMusic = False
						EndIf
					Else
						PlayCustomMusic = False
						If CustomMusic <> 0 Then FreeSound_Strict(CustomMusic) : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel(MusicCHN)
					EndIf
					;[End Block]
				Case "tele"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1, " ")
					StrTemp2 = Piece(Args, 2, " ")
					StrTemp3 = Piece(Args, 3, " ")
					PositionEntity(me\Collider, Float(StrTemp), Float(StrTemp2), Float(StrTemp3))
					PositionEntity(Camera, Float(StrTemp), Float(StrTemp2), Float(StrTemp3))
					ResetEntity(me\Collider)
					ResetEntity(Camera)
					CreateConsoleMsg("Teleported to coordinates (X|Y|Z): " + EntityX(me\Collider) + "|" + EntityY(me\Collider) + "|" + EntityZ(me\Collider))
					;[End Block]
				Case "asd"
					;[Block]
					chs\NoBlink = True
					chs\NoTarget = True
					chs\NoClip = True
					chs\GodMode = True
					chs\InfiniteStamina = True
					
					CameraFogFar = 50.0
					
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\Channels[i] <> 0 Then
								StopChannel(snd\Channels[i])
							EndIf
						Next
					Next
					
					If IntercomStreamCHN <> 0 Then
						StopStream_Strict(IntercomStreamCHN)
						IntercomStreamCHN = 0
					EndIf
					
					For e.Events = Each Events
						If e\EventName = "room173" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							FreeEntity(e\room\Objects[1]) : e\room\Objects[1] = 0
							PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(Curr173\Collider)
							ShowEntity(Curr173\OBJ)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("Stopped all sounds.")
					;[End Block]
				Case "notarget", "nt"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoTarget = True	
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoTarget = False
							;[End Block]
						Default
							;[Block]
							chs\NoTarget = (Not chs\NoTarget)
							;[End Block]
					End Select
					
					If chs\NoTarget = False Then
						CreateConsoleMsg("NOTARGET OFF")
					Else
						CreateConsoleMsg("NOTARGET ON")	
					EndIf
					;[End Block]
				Case "spawnpumpkin", "pumpkin"
					;[Block]
					CreateConsoleMsg("What pumpkin?")
					;[End Block]
				Case "teleport173"
					;[Block]
					PositionEntity(Curr173\Collider, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
					ResetEntity(Curr173\Collider)
					;[End Block]
				Case "seteventstate"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1, " ")
					StrTemp2 = Piece(Args, 2, " ")
					StrTemp3 = Piece(Args, 3, " ")
					StrTemp4 = Piece(Args, 4, " ")
					
					Local PL_Room_Found% = False
					
					If StrTemp = "" Lor StrTemp2 = "" Lor StrTemp3 = "" Lor StrTemp4 = "" Then
						CreateConsoleMsg("Too few parameters. This command requires 4.", 255, 150, 0)
					Else
						For e.Events = Each Events
							If e\room = PlayerRoom
								If Lower(StrTemp) <> "keep" Then
									e\EventState = Float(StrTemp)
								EndIf
								If Lower(StrTemp2) <> "keep" Then
									e\EventState2 = Float(StrTemp2)
								EndIf
								If Lower(StrTemp3) <> "keep" Then
									e\EventState3 = Float(StrTemp3)
								EndIf
								If Lower(StrTemp4) <> "keep" Then
									e\EventState4 = Float(StrTemp4)
								EndIf
								CreateConsoleMsg("Changed event states from current player room to: " + e\EventState + "|" + e\EventState2 + "|" + e\EventState3 + "|" + e\EventState4)
								PL_Room_Found = True
								Exit
							EndIf
						Next
						If (Not PL_Room_Found) Then
							CreateConsoleMsg("The current room doesn't has any event applied.", 255, 150, 0)
						EndIf
					EndIf
					;[End Block]
				Case "giveachievement"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					If Int(StrTemp) >= 0 And Int(StrTemp) < MAXACHIEVEMENTS Then
						Achievements[Int(StrTemp)] = True
						CreateConsoleMsg("Achievemt " + AchievementStrings[Int(StrTemp)] + " unlocked.")
					Else
						CreateConsoleMsg("Achievement with ID " + Int(StrTemp) + " doesn't exist.", 255, 150, 0)
					EndIf
					;[End Block]
				Case "427state"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = Float(StrTemp) * 70.0
					;[End Block]
				Case "teleport106"
					;[Block]
					Curr106\State = 0.0
					Curr106\Idle = False
					;[End Block]
				Case "jorge"
					;[Block]	
					CreateConsoleMsg(Chr(74) + Chr(79) + Chr(82) + Chr(71) + Chr(69) + Chr(32) + Chr(72) + Chr(65) + Chr(83) + Chr(32) + Chr(66) + Chr(69) + Chr(69) + Chr(78) + Chr(32) + Chr(69) + Chr(88) + Chr(80) + Chr(69) + Chr(67) + Chr(84) + Chr(73) + Chr(78) + Chr(71) + Chr(32) + Chr(89) + Chr(79) + Chr(85) + Chr(46))
					;[End Block]
				Default
					;[Block]
					CreateConsoleMsg("Command not found.", 255, 0, 0)
					;[End Block]
			End Select
			ConsoleInput = ""
		EndIf
		
		Local TempY# = y + Height - 25.0 * MenuScale - ConsoleScroll
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000 Then
				Delete(cm)
			Else
				If TempY >= y And TempY < y + Height - 20 * MenuScale Then
					If cm = ConsoleReissue Then
						Color(cm\R / 4, cm\G / 4, cm\B / 4)
						Rect(x, TempY - 2 * MenuScale, Width - 30 * MenuScale, 24 * MenuScale, True)
					EndIf
					Color(cm\R, cm\G, cm\B)
					If cm\IsCommand Then
						Text(x + 20 * MenuScale, TempY, "> " + cm\Txt)
					Else
						Text(x + 20 * MenuScale, TempY, cm\Txt)
					EndIf
				EndIf
				TempY = TempY - 15.0 * MenuScale
			EndIf
		Next
		Color(255, 255, 255)
		
		RenderMenuInputBoxes()
		
		If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	EndIf
	
	SetFont(fo\FontID[0])
End Function

ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
CreateConsoleMsg("Console commands: ")
CreateConsoleMsg("  - teleport [room name]")
CreateConsoleMsg("  - godmode [on / off]")
CreateConsoleMsg("  - noclip [on / off]")
CreateConsoleMsg("  - infinitestamina [on / off]")
CreateConsoleMsg("  - noblink [on / off]")
CreateConsoleMsg("  - notarget [on / off]")
CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
CreateConsoleMsg("  - wireframe [on / off]")
CreateConsoleMsg("  - debughud [on / off]")
CreateConsoleMsg("  - camerafog [near] [far]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - heal")
CreateConsoleMsg("  - revive")
CreateConsoleMsg("  - asd")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - spawnitem [item name]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - 106retreat")
CreateConsoleMsg("  - disable173 / enable173")
CreateConsoleMsg("  - disable106 / enable106")
CreateConsoleMsg("  - spawn [NPC type]")

Global Camera%

Global StoredCameraFogFar# = CameraFogFar

Include "Source Code\Dreamfilter.bb"

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

; ~ The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Global Music$[29]

Music[0] = "LightContainmentZone"
Music[1] = "HeavyContainmentZone"
Music[2] = "EntranceZone"
Music[3] = "PD"
Music[4] = "Room079"
Music[5] = "GateB1"
Music[6] = "GateB2"
Music[7] = "Room3Storage"
Music[8] = "Room049"
Music[9] = "Room860_1"
Music[10] = "106Chase"
Music[11] = "Menu"
Music[12] = "860_2Chase"
Music[13] = "Room173Intro"
Music[14] = "Using178"
Music[15] = "PDTrench"
Music[16] = "Room205"
Music[17] = "GateA"
Music[18] = "1499"
Music[19] = "1499_1Chase"
Music[20] = "049Chase"
Music[21] = "..\Ending\MenuBreath"
Music[22] = "Room914"
Music[23] = "Ending"
Music[24] = "Credits"
Music[25] = "SaveMeFrom"
Music[26] = "Room106"
Music[27] = "Room035"
Music[28] = "Room409" 

Global MusicCHN%
MusicCHN = StreamSound_Strict("SFX\Music\" + Music[2] + ".ogg", MusicVolume, Mode)

Global CurrMusicVolume# = 1.0, NowPlaying% = 2, ShouldPlay% = 11
Global CurrMusic% = 1

DrawLoading(10, True)

Dim OpenDoorSFX%(3, 3), CloseDoorSFX%(3, 3)
Global BigDoorErrorSFX%[3]

Global KeyCardSFX1% 
Global KeyCardSFX2% 
Global ScannerSFX1%
Global ScannerSFX2%

Global OpenDoorFastSFX%
Global CautionSFX% 

Global NuclearSirenSFX%

Global CameraSFX% 

Global StoneDragSFX% 

Global GunshotSFX% 
Global Gunshot2SFX% 
Global Gunshot3SFX% 
Global BullethitSFX% 

Global TeslaIdleSFX% 
Global TeslaActivateSFX% 
Global TeslaPowerUpSFX% 
Global TeslaShockSFX%

Global MagnetUpSFX%, MagnetDownSFX%
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Global CrouchSFX%

Global DecaySFX%[5]

Global BurstSFX% 

DrawLoading(20, True)

Global RustleSFX%[6]

Global Use914SFX%
Global Death914SFX% 

Global DripSFX%[4]

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX%(5, 10) 

Global RadioSquelch% 
Global RadioStatic% 
Global RadioStatic895%
Global RadioBuzz% 

Global SCRAMBLESFX%
Global SCRAMBLECHN%

Global ElevatorBeepSFX%, ElevatorMoveSFX% 

Global PickSFX%[4]

Global AmbientSFXCHN%, CurrAmbientSFX%
Global AmbientSFXAmount%[6]
; ~ 0 = Light Containment Zone
; ~ 1 = Heavy Containment Zone
; ~ 2 = Entrance Zone
; ~ 3 = General
; ~ 4 = Pre-Breach
; ~ 5 = SCP-860-1

AmbientSFXAmount[0] = 8 
AmbientSFXAmount[1] = 11
AmbientSFXAmount[2]= 12
AmbientSFXAmount[3] = 15 
AmbientSFXAmount[4] = 5
AmbientSFXAmount[5] = 10

Dim AmbientSFX%(6, 15)

Global OldManSFX%[9]

Global Scp173SFX%[3]

Global HorrorSFX%[20]

Global MissSFX%

DrawLoading(25, True)

Global IntroSFX%[12]

Global AlarmSFX%[5]

Global CommotionState%[25]

Global HeartBeatSFX% 

Global VomitSFX%

Dim BreathSFX%(2, 5)
Global BreathCHN%

Global BreathGasRelaxedSFX%
Global BreathGasRelaxedCHN%

Global NeckSnapSFX%[3]

Global DamageSFX%[14]

Global MTFSFX%[2]

Global CoughSFX%[3]
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX%

Global CurrStepSFX%
Dim StepSFX%(6, 2, 8) ; ~ (Normal / Metal, Walk / Run, ID)

DrawLoading(30, True)

Global PlayCustomMusic% = False, CustomMusic% = 0

Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Global UserTrackName$[256]

Global OptionsMenu% = 0
Global QuitMsg% = 0

Global InFacility% = True

Global PrevMusicVolume# = MusicVolume
Global PrevSFXVolume# = SFXVolume

Global room2gw_BrokenDoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Global DTextures%[16]

Global IntercomStreamCHN%

Global ForestNPC%, ForestNPCTex%, ForestNPCData#[3]

DrawLoading(35, True)

Include "Source Code\Items_System.bb"

Include "Source Code\Particles_System.bb"

Global ClosestButton%, ClosestDoor.Doors
Global SelectedDoor.Doors, UpdateDoorsTimer#
Global DoorTempID%

Type Doors
	Field OBJ%, OBJ2%, FrameOBJ%, Buttons%[2]
	Field Locked%, LockedUpdated%, Open%, Angle%, OpenState#, FastOpen%
	Field Dir%
	Field Timer%, TimerState#
	Field KeyCard%
	Field room.Rooms
	Field DisableWaypoint%
	Field Dist#
	Field SoundCHN%
	Field Code$
	Field ID%
	Field Level%
	Field LevelDest%
	Field AutoClose%
	Field LinkedDoor.Doors
	Field IsElevatorDoor% = False
	Field MTFClose% = True
	Field NPCCalledElevator% = False
End Type 

Function CreateDoor.Doors(Lvl, x#, y#, z#, Angle#, room.Rooms, Open% = False, Big% = False, Keycard% = False, Code$ = "", CheckIfZeroCard% = False)
	Local d.Doors, Parent%, i%
	
	If room <> Null Then Parent = room\OBJ
	
	d.Doors = New Doors
	If Big = 1 Then ; ~ Big Door
		d\OBJ = CopyEntity(o\DoorModelID[5])
		ScaleEntity(d\OBJ, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		d\OBJ2 = CopyEntity(o\DoorModelID[6])
		ScaleEntity(d\OBJ2, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[4])				
		ScaleEntity(d\FrameOBJ, RoomScale, RoomScale, RoomScale)
		EntityType(d\FrameOBJ, HIT_MAP)
		EntityAlpha(d\FrameOBJ, 0.0)
	ElseIf Big = 2 Then ; ~ Heavy Door
		d\OBJ = CopyEntity(o\DoorModelID[2])
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(o\DoorModelID[3])
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	ElseIf Big = 3 Then ; ~ Elevator Door
		d\OBJ = CopyEntity(o\DoorModelID[7])
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(d\OBJ)
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	ElseIf Big = 4 ; ~ One-sided Door
		d\OBJ = CopyEntity(o\DoorModelID[10])
		ScaleEntity(d\OBJ, (203.0 * RoomScale) / MeshWidth(d\OBJ), 313.0 * RoomScale / MeshHeight(d\OBJ), 15.0 * RoomScale / MeshDepth(d\OBJ))
		d\OBJ2 = CopyEntity(o\DoorModelID[10])
		ScaleEntity(d\OBJ2, (203.0 * RoomScale) / MeshWidth(d\OBJ2), 313.0 * RoomScale / MeshHeight(d\OBJ2), 15.0 * RoomScale / MeshDepth(d\OBJ2))
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	Else
		d\OBJ = CopyEntity(o\DoorModelID[0])
		ScaleEntity(d\OBJ, (203.0 * RoomScale) / MeshWidth(d\OBJ), 313.0 * RoomScale / MeshHeight(d\OBJ), 15.0 * RoomScale / MeshDepth(d\OBJ))
		d\OBJ2 = CopyEntity(o\DoorModelID[0])
		ScaleEntity(d\OBJ2, (203.0 * RoomScale) / MeshWidth(d\OBJ2), 313.0 * RoomScale / MeshHeight(d\OBJ2), 15.0 * RoomScale / MeshDepth(d\OBJ2))
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	EndIf
	
	PositionEntity(d\FrameOBJ, x, y, z)
	ScaleEntity(d\FrameOBJ, 8.0 / 2048.0, 8.0 / 2048.0, 8.0 / 2048.0)
	EntityPickMode(d\FrameOBJ, 2)
	EntityType(d\OBJ, HIT_MAP)
	If d\OBJ2 <> 0 Then EntityType(d\OBJ2, HIT_MAP)
	
	d\ID = DoorTempID
	DoorTempID = DoorTempID + 1
	
	If Keycard > 0 Then
		If CheckIfZeroCard Then
			d\KeyCard = Keycard + 1
		Else
			d\KeyCard = Keycard + 2
		EndIf
	Else
		d\KeyCard = Keycard 
	EndIf
	
	d\Code = Code
	d\Level = Lvl
	d\LevelDest = 66
	
	For i = 0 To 1
		If Code <> "" Then 
			d\Buttons[i] = CopyEntity(o\ButtonModelID[2])
		Else
			If Keycard > 0 Then
				d\Buttons[i] = CopyEntity(o\ButtonModelID[1])
			ElseIf Keycard < 0
				d\Buttons[i] = CopyEntity(o\ButtonModelID[3])	
			ElseIf Big = 3
				d\Buttons[i] = CopyEntity(o\ButtonModelID[(i * 4)])
			Else
				d\Buttons[i] = CopyEntity(o\ButtonModelID[0])
			EndIf
		EndIf
		
		If d\Buttons[i] <> 0 Then
			ScaleEntity(d\Buttons[i], 0.03, 0.03, 0.03)
			
			If Big = 1 Then
				PositionEntity(d\Buttons[i], x + (-432.0 + (i * 864.0)) * RoomScale, y + 0.7, z + (192.0 + (i * (-384.0))) * RoomScale)
				RotateEntity(d\Buttons[i], 0.0, 90.0 + (i * 180.0), 0.0)
			Else
				PositionEntity(d\Buttons[i], x + 0.6 + (i * (-1.2)), y + 0.7, z - 0.1 + (i * 0.2))
				RotateEntity(d\Buttons[i], 0.0, (i * 180.0), 0.0)
			EndIf
			EntityParent(d\Buttons[i], d\FrameOBJ)
			EntityPickMode(d\Buttons[i], 2)
		EndIf
	Next
	
	PositionEntity(d\OBJ, x, y, z)
	
	RotateEntity(d\OBJ, 0.0, Angle, 0.0)
	RotateEntity(d\FrameOBJ, 0.0, Angle, 0.0)
	
	If d\OBJ2 <> 0 Then
		PositionEntity(d\OBJ2, x, y, z)
		If Big = 1 Then
			RotateEntity(d\OBJ2, 0.0, Angle, 0.0)
		Else
			RotateEntity(d\OBJ2, 0.0, Angle + 180.0, 0.0)
		EndIf
		EntityParent(d\OBJ2, Parent)
	EndIf
	
	EntityParent(d\FrameOBJ, Parent)
	EntityParent(d\OBJ, Parent)
	
	d\Angle = Angle
	d\Open = Open		
	
	EntityPickMode(d\OBJ, 2)
	MakeCollBox(d\OBJ)
	If d\OBJ2 <> 0 Then
		EntityPickMode(d\OBJ2, 3)
		MakeCollBox(d\OBJ2)
	EndIf
	
	EntityPickMode(d\FrameOBJ, 2)
	
	If d\Open And Big = False And Rand(8) = 1 Then d\AutoClose = True
	d\Dir = Big
	d\room = room
	
	d\MTFClose = True
	
	Return(d)
End Function

Function CreateButton(x#, y#, z#, Pitch#, Yaw#, Roll# = 0.0, ButtonID% = 0, Locked% = False)
	Local OBJ% = CopyEntity(o\ButtonModelID[ButtonID])	
	
	If Locked Then EntityTexture(OBJ, tt\MiscTextureID[17])
	ScaleEntity(OBJ, 0.03, 0.03, 0.03)
	PositionEntity(OBJ, x, y, z)
	RotateEntity(OBJ, Pitch, Yaw, Roll)
	EntityPickMode(OBJ, 2)
	Return(OBJ)
End Function

Function UpdateDoors()
	Local i%, d.Doors, x#, z#, Dist#
	
	If UpdateDoorsTimer =< 0.0 Then
		For d.Doors = Each Doors
			Local xDist# = Abs(EntityX(me\Collider) - EntityX(d\OBJ, True))
			Local zDist# = Abs(EntityZ(me\Collider) - EntityZ(d\OBJ, True))
			
			d\Dist = xDist + zDist
			
			If d\Dist > HideDistance * 2.0 Then
				If d\OBJ <> 0 Then HideEntity(d\OBJ)
				If d\FrameOBJ <> 0 Then HideEntity(d\FrameOBJ)
				If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
				If d\Buttons[0] <> 0 Then HideEntity(d\Buttons[0])
				If d\Buttons[1] <> 0 Then HideEntity(d\Buttons[1])				
			Else
				If d\OBJ <> 0 Then ShowEntity(d\OBJ)
				If d\FrameOBJ <> 0 Then ShowEntity(d\FrameOBJ)
				If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
				If d\Buttons[0] <> 0 Then ShowEntity(d\Buttons[0])
				If d\Buttons[1] <> 0 Then ShowEntity(d\Buttons[1])
			EndIf
		Next
		UpdateDoorsTimer = 30.0
	Else
		UpdateDoorsTimer = Max(UpdateDoorsTimer - fpst\FPSFactor[0], 0.0)
	EndIf
	
	ClosestButton = 0
	ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\Dist < HideDistance * 2.0 Lor d\IsElevatorDoor > 0 Then ; ~ Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel -- ENDSHN
			If (d\OpenState >= 180.0 Lor d\OpenState =< 0.0) And GrabbedEntity = 0 Then
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then
						If Abs(EntityX(me\Collider) - EntityX(d\Buttons[i], True)) < 1.0 Then 
							If Abs(EntityZ(me\Collider) - EntityZ(d\Buttons[i], True)) < 1.0 Then 
								Dist = DistanceSquared(EntityX(me\Collider, True), EntityX(d\Buttons[i], True), EntityZ(me\Collider, True), EntityZ(d\Buttons[i], True))
								If Dist < 0.49 Then
									Local Temp% = CreatePivot()
									
									PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
									PointEntity(Temp, d\Buttons[i])
									
									If EntityPick(Temp, 0.6) = d\Buttons[i] Then
										If ClosestButton = 0 Then
											ClosestButton = d\Buttons[i]
											ClosestDoor = d
										Else
											If Dist < EntityDistanceSquared(me\Collider, ClosestButton) Then ClosestButton = d\Buttons[i] : ClosestDoor = d
										EndIf							
									EndIf
									FreeEntity(Temp)
								EndIf							
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
			If d\Open Then
				If d\OpenState < 180.0 Then
					Select d\Dir
						Case 0
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fpst\FPSFactor[0] / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * fpst\FPSFactor[0] / 80.0, 0.0, 0.0)	
							;[End Block]
						Case 1
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 0.8))
							MoveEntity(d\OBJ, Sin(d\OpenState) * fpst\FPSFactor[0] / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, (-Sin(d\OpenState)) * fpst\FPSFactor[0] / 180.0, 0.0, 0.0)
							;[End Block]
						Case 2
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen + 1) * fpst\FPSFactor[0] / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fpst\FPSFactor[0] / 120.0, 0.0, 0.0)
							;[End Block]
						Case 3
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fpst\FPSFactor[0] / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState)* (d\FastOpen * 2 + 1) * fpst\FPSFactor[0] / 162.0, 0.0, 0.0)
							;[End Block]
						Case 4
						    ;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fpst\FPSFactor[0] / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fpst\FPSFactor[0]) / 80.0, 0.0, 0.0)	
							;[End Block]	
						Case 5 ;Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fpst\FPSFactor[0] * 1.4))
							MoveEntity(d\OBJ, Sin(d\OpenState) * fpst\FPSFactor[0] / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
				Else
					d\FastOpen = 0
					ResetEntity(d\OBJ)
					If d\OBJ2 <> 0 Then ResetEntity(d\OBJ2)
					If d\TimerState > 0.0 Then
						d\TimerState = Max(0.0, d\TimerState - fpst\FPSFactor[0])
						If d\TimerState + fpst\FPSFactor[0] > 110.0 And d\TimerState =< 110.0 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\OBJ)
						
						If d\TimerState = 0.0 Then 
							d\Open = (Not d\Open)
							If d\Dir <> 0 And d\Dir <> 4 Then
								d\SoundCHN = PlaySound2(CloseDoorSFX(d\Dir, Rand(0, 2)), Camera, d\OBJ)
							Else
								d\SoundCHN = PlaySound2(CloseDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
							EndIf
						EndIf
					EndIf
					If d\AutoClose And RemoteDoorOn = True Then
						If EntityDistanceSquared(Camera, d\OBJ) < 4.41 Then
							If I_714\Using = 0 And wi\GasMask < 3 And wi\HazmatSuit < 3 Then PlaySound_Strict(HorrorSFX[7])
							d\Open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\Dir, 1), Rand(0, 2)), Camera, d\OBJ) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\OpenState > 0.0 Then
					Select d\Dir
						Case 0
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) * (d\FastOpen + 1) / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fpst\FPSFactor[0]) / 80.0, 0.0, 0.0)	
							;[End Block]
						Case 1
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fpst\FPSFactor[0] * 0.8))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * fpst\FPSFactor[0] / 180.0, 0.0, 0.0)
							If d\OpenState < 15.0 And d\OpenState + fpst\FPSFactor[0] >= 15.0
								If ParticleAmount = 2
									For i = 0 To Rand(75, 99)
										Local Pvt% = CreatePivot()
										
										PositionEntity(Pvt, EntityX(d\FrameOBJ, True) + Rnd(-0.2, 0.2), EntityY(d\FrameOBJ, True) + Rnd(0.0, 1.2), EntityZ(d\FrameOBJ, True) + Rnd(-0.2, 0.2))
										RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
										
										Local p.Particles = CreateParticle(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 2, 0.002, 0.0, 300.0)
										
										p\Speed = 0.005 : p\SizeChange = -0.00001 : p\Size = 0.01 : p\Achange = -0.01
										RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
										ScaleSprite(p\OBJ, p\Size, p\Size)
										EntityOrder(p\OBJ, -1)
										FreeEntity(Pvt)
									Next
								EndIf
							EndIf
							;[End Block]
						Case 2
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) * (d\FastOpen + 1) / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fpst\FPSFactor[0]) / 120.0, 0.0, 0.0)
							;[End Block]
						Case 3
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) * (d\FastOpen + 1) / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fpst\FPSFactor[0]) / 162.0, 0.0, 0.0)
							;[End Block]
						Case 4
						    ;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fpst\FPSFactor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) * (d\FastOpen + 1) / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * fpst\FPSFactor[0] / 80.0, 0.0, 0.0)
							;[End Block]	
						Case 5 ;Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState - (fpst\FPSFactor[0] * 1.4))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fpst\FPSFactor[0]) / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
					
					If d\Angle = 0.0 Lor d\Angle = 180.0 Then
						If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(me\Collider)) < 0.15 Then
							If Abs(EntityX(d\FrameOBJ, True) - EntityX(me\Collider)) < 0.7 * (d\Dir * 2 + 1) Then
								z = CurveValue(EntityZ(d\FrameOBJ, True) + 0.15 * Sgn(EntityZ(me\Collider) - EntityZ(d\FrameOBJ, True)), EntityZ(me\Collider), 5)
								PositionEntity(me\Collider, EntityX(me\Collider), EntityY(me\Collider), z)
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\FrameOBJ, True) - EntityX(me\Collider)) < 0.15 Then	
							If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(me\Collider)) < 0.7 * (d\Dir * 2 + 1) Then
								x = CurveValue(EntityX(d\FrameOBJ, True) + 0.15 * Sgn(EntityX(me\Collider) - EntityX(d\FrameOBJ, True)), EntityX(me\Collider), 5)
								PositionEntity(me\Collider, x, EntityY(me\Collider), EntityZ(me\Collider))
							EndIf
						EndIf
					EndIf
				Else
					d\FastOpen = 0
					PositionEntity(d\OBJ, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
					If d\OBJ2 <> 0 Then PositionEntity(d\OBJ2, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
					If d\OBJ2 <> 0 And (d\Dir = 0 Lor d\Dir = 4) Then
						MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
						MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
					EndIf
				EndIf
			EndIf
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\FrameOBJ)
		
		If d\Locked <> d\LockedUpdated Then
			If d\Locked = True Then
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then EntityTexture(d\Buttons[i], tt\MiscTextureID[17])
				Next
			Else
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then EntityTexture(d\Buttons[i], tt\MiscTextureID[16])
				Next
			EndIf
			d\LockedUpdated = d\Locked
		EndIf
		
		If d\Dir = 1 And d\Locked = 2 Then
			If d\OpenState > 48.0 Then
				d\Open = False
				d\OpenState = Min(d\OpenState, 48.0)
			EndIf	
		EndIf
	Next
End Function

Function UseDoor(d.Doors, ShowMsg% = True, PlaySFX% = True)
	Local Temp% = 0
	
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If ShowMsg = True Then
				If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "The keycard") Lor Instr(msg\Msg, "A keycard with") Lor Instr(msg\Msg, "You hold the"))) Then
					msg\Msg = "A keycard is required to operate this door."
					msg\Timer = 70.0 * 6.0
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\ItemTemplate\TempName
				Case "key6"
					;[Block]
					Temp = 1
					;[End Block]
				Case "key0"
					;[Block]
					Temp = 2
					;[End Block]
				Case "key1"
					;[Block]
					Temp = 3
					;[End Block]
				Case "key2"
					;[Block]
					Temp = 4
					;[End Block]
				Case "key3"
					;[Block]
					Temp = 5
					;[End Block]
				Case "key4"
					;[Block]
					Temp = 6
					;[End Block]
				Case "key5"
					;[Block]
					Temp = 7
					;[End Block]
				Case "keyomni"
					;[Block]
					Temp = 8
					;[End Block]
				Case "scp005"
				    ;[Block]
					Temp = 9
					;[End Block]
				Default
					;[Block]
					Temp = -1
					;[End Block]
			End Select
			
			If Temp = -1 Then 
				If ShowMsg = True Then
					If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "The keycard") Lor Instr(msg\Msg, "A keycard with") Lor Instr(msg\Msg, "You hold the"))) Then
						msg\Msg = "A keycard is required to operate this door."
						msg\Timer = 70.0 * 6.0
					EndIf
				EndIf
				Return				
			ElseIf Temp >= d\KeyCard 
				SelectedItem = Null
				If ShowMsg = True Then
					If d\Locked = True Then
						PlaySound_Strict(KeyCardSFX2)
						If Temp = 1 Then 
							msg\Msg = "The keycard was inserted into the slot. UNKNOWN ERROR! " Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34)
							msg\Timer = 70.0 * 8.0
						Else
							If Temp = 9 Then
								msg\Msg = "You hold the key close to the slot but nothing happened."
							Else
								msg\Msg = "The keycard was inserted into the slot but nothing happened."
							EndIf
							msg\Timer = 70.0 * 6.0
						EndIf
						Return
					Else
						PlaySound_Strict(KeyCardSFX1)
						If Temp = 9 Then
							msg\Msg = "You hold the key close to the slot."
						Else
							msg\Msg = "The keycard was inserted into the slot."
						EndIf
						msg\Timer = 70.0 * 6.0
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If ShowMsg = True Then 
					PlaySound_Strict(KeyCardSFX2)					
					If d\Locked = True Then
						If Temp = 1 Then 
							msg\Msg = "The keycard was inserted into the slot. UNKNOWN ERROR! " +  Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34)
							msg\Timer = 70.0 * 8.0
						Else
							If Temp = 9 Then
								msg\Msg = "You hold the key close to the slot but nothing happened."
							Else
								msg\Msg = "The keycard was inserted into the slot but nothing happened."
							EndIf
							msg\Timer = 70.0 * 6.0
						EndIf
					Else
						If Temp = 1 Then 
							msg\Msg = "The keycard was inserted into the slot. UNKNOWN ERROR! " + Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34)
							msg\Timer = 70.0 * 8.0
						Else
							If Temp = 9 Then
								msg\Msg = "You hold the key close to the slot but nothing happened."
							Else
								msg\Msg = "A keycard with security clearance " + (d\KeyCard - 2) + " or higher is required to operate this door."
							EndIf
							msg\Timer = 70.0 * 6.0
						EndIf
					EndIf
				EndIf
				Return
			EndIf
		EndIf	
	ElseIf d\KeyCard < 0
		; ~ I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable -- risingstar64
		If SelectedItem <> Null Then
			If SelectedItem\ItemTemplate\TempName = "scp005" Then
				Temp = 2
			ElseIf SelectedItem\ItemTemplate\TempName = "key0" Lor SelectedItem\ItemTemplate\TempName = "key1" Lor SelectedItem\ItemTemplate\TempName = "key2" Lor SelectedItem\ItemTemplate\TempName = "key3" Lor SelectedItem\ItemTemplate\TempName = "key4" Lor SelectedItem\ItemTemplate\TempName = "key5" Lor SelectedItem\ItemTemplate\TempName = "key6" Lor SelectedItem\ItemTemplate\TempName = "keyomni"
				Temp = 3
			ElseIf SelectedItem\ItemTemplate\TempName = "key" Lor SelectedItem\ItemTemplate\TempName = "scp860"
				Temp = 4
			Else
				Temp = (SelectedItem\ItemTemplate\TempName = "hand" And d\KeyCard = -1) Lor (SelectedItem\ItemTemplate\TempName = "hand2" And d\KeyCard = -2)
			EndIf
		EndIf
		SelectedItem = Null
		If ShowMsg = True Then
			If Temp <> 0 Then
				If Temp >= 3 Then
					PlaySound_Strict(ButtonSFX)
					If Temp = 4 Then
						If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "You placed your") Lor Instr(msg\Msg, "You place") Lor Instr(msg\Msg, "You hold the") Lor Instr(msg\Msg, "The type of"))) Then
							msg\Msg = "There is no place to insert the key."
							msg\Timer = 70 * 6.0
						EndIf
					Else
						If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "You placed your") Lor Instr(msg\Msg, "You place") Lor Instr(msg\Msg, "You hold the") Lor Instr(msg\Msg, "There is"))) Then
							msg\Msg = "The type of this slot doesn't require keycards."
							msg\Timer = 70 * 6.0
						EndIf
					EndIf
					Return
				Else
					PlaySound_Strict(ScannerSFX1)
					If Temp = 2 Then
						If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "You placed your") Lor Instr(msg\Msg, "You place") Lor Instr(msg\Msg, "The type of") Lor Instr(msg\Msg, "There is"))) Then
							msg\Msg = "You hold the key onto the scanner. The scanner reads: " + Chr(34) + "Unknown DNA verified. ERROR! Access granted." + Chr(34)
							msg\Timer = 70.0 * 8.0
						EndIf
					Else
						If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "You place") Lor Instr(msg\Msg, "You hold the") Lor Instr(msg\Msg, "The type of") Lor Instr(msg\Msg, "There is"))) Then
							msg\Msg = "You place the palm of the hand onto the scanner. The scanner reads: " + Chr(34) + "DNA verified. Access granted." + Chr(34)
							msg\Timer = 70.0 * 8.0
						EndIf
					EndIf
				EndIf
			Else
				PlaySound_Strict(ScannerSFX2)
				If (msg\Timer < 70.0 * 3.0) Lor (Not (Instr(msg\Msg, "You place") Lor Instr(msg\Msg, "You hold the") Lor Instr(msg\Msg, "The type of") Lor Instr(msg\Msg, "There is"))) Then
					msg\Msg = "You placed your palm onto the scanner. The scanner reads: " + Chr(34) + "DNA does not match known sample. Access denied." + Chr(34)
					msg\Timer = 70.0 * 8.0
				EndIf
				Return			
			EndIf
		EndIf
	Else
		If d\Locked = True Then
			If ShowMsg = True Then 
				If (Not (d\IsElevatorDoor > 0)) Then
					PlaySound_Strict(ButtonSFX2)
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
                        If d\Open Then
                            msg\Msg = "You pushed the button but nothing happened."
                        Else    
                            msg\Msg = "The door appears to be locked."
                        EndIf    
                    Else
                        msg\Msg = "The elevator appears to be broken."
                    EndIf
					msg\Timer = 70.0 * 6.0
				Else
					If d\IsElevatorDoor = 1 Then
						msg\Msg = "You called the elevator."
						msg\Timer = 70.0 * 6.0
					ElseIf d\IsElevatorDoor = 3 Then
						msg\Msg = "The elevator is already on this floor."
						msg\Timer = 70.0 * 6.0
					ElseIf msg\Msg <> "You called the elevator."
						If msg\Timer < 70.0 * 3.0 Lor msg\Msg = "You already called the elevator."
							Select Rand(10)
								Case 1
									;[Block]
									msg\Msg = "Stop spamming the button."
									msg\Timer = 70.0 * 6.0
									;[End Block]
								Case 2
									;[Block]
									msg\Msg = "Pressing it harder does not make the elevator come faster."
									msg\Timer = 70.0 * 6.0
									;[End Block]
								Case 3
									;[Block]
									msg\Msg = "If you continue pressing this button I will generate a Memory Access Violation."
									msg\Timer = 70.0 * 6.0
									;[End Block]
								Default
									;[Block]
									msg\Msg = "You already called the elevator."
									msg\Timer = 70.0 * 6.0
									;[End Block]
							End Select
						EndIf
					Else
						msg\Msg = "You already called the elevator."
						msg\Timer = 70.0 * 6.0
					EndIf
				EndIf
			EndIf
			Return
		EndIf	
	EndIf
	
	d\Open = (Not d\Open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\Open = (Not d\LinkedDoor\Open)
	
	If d\Open Then
		If d\LinkedDoor <> Null Then d\LinkedDoor\TimerState = d\LinkedDoor\Timer
		d\TimerState = d\Timer
	EndIf
	
	If PlaySFX = True Then
		If d\Open Then
			If d\Dir = 1 And d\Locked = 2 Then
				d\SoundCHN = PlaySound2(BigDoorErrorSFX[Rand(0, 2)], Camera, d\OBJ)
			Else
				If d\Dir <> 0 And d\Dir <> 4 Then
					d\SoundCHN = PlaySound2(OpenDoorSFX(d\Dir, Rand(0, 2)), Camera, d\OBJ)
				Else
					d\SoundCHN = PlaySound2(OpenDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
				EndIf
			EndIf
		Else
			If d\Dir <> 0 And d\Dir <> 4 Then
				d\SoundCHN = PlaySound2(CloseDoorSFX(d\Dir, Rand(0, 2)), Camera, d\OBJ)
			Else
				d\SoundCHN = PlaySound2(CloseDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
			EndIf
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\OBJ)
	EndIf
End Function

Function RemoveDoor(d.Doors)
	Local i%
	
	For i = 0 To 1
		If d\Buttons[i] <> 0 Then EntityParent(d\Buttons[i], 0)
	Next
	If d\OBJ <> 0 Then FreeEntity(d\OBJ) : d\OBJ = 0
	If d\OBJ2 <> 0 Then FreeEntity(d\OBJ2) : d\OBJ2 = 0
	If d\FrameOBJ <> 0 Then FreeEntity(d\FrameOBJ) : d\FrameOBJ = 0
	For i = 0 To 1
		If d\Buttons[i] <> 0 Then FreeEntity(d\Buttons[i]) : d\Buttons[0] = 0
	Next
	
	Delete(d)
End Function

DrawLoading(40, True)

Include "Source Code\Map_System.bb"

DrawLoading(80, True)

Include "Source Code\NPCs_System.bb"

Include "Source Code\Events_System.bb"

Const HIT_MAP% = 1, HIT_PLAYER% = 2, HIT_ITEM% = 3, HIT_APACHE% = 4, HIT_178% = 5, HIT_DEAD% = 6

Collisions(HIT_PLAYER, HIT_MAP, 2, 2)
Collisions(HIT_PLAYER, HIT_PLAYER, 1, 3)
Collisions(HIT_ITEM, HIT_MAP, 2, 2)
Collisions(HIT_APACHE, HIT_APACHE, 1, 2)
Collisions(HIT_178, HIT_MAP, 2, 2)
Collisions(HIT_178, HIT_178, 1, 3)
Collisions(HIT_DEAD, HIT_MAP, 2, 2)

DrawLoading(90, True)

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global Save_Msg$ = ""
Global Save_Msg_Timer# = 0.0
Global Save_Msg_Y# = 0.0

Global MTFCameraCheckTimer# = 0.0
Global MTFCameraCheckDetected% = False

Include "Source Code\Menu_System.bb"

MainMenuOpen = True

FlushKeys()
FlushMouse()

DrawLoading(100, True)

fpst\LoopDelay = MilliSecs()

Global UpdateParticles_Time# = 0.0

Global CurrTrisAmount%

Global Input_ResetTime# = 0.0

Type SCP005
	Field ChanceToSpawn%
End Type

Global I_005.SCP005 = New SCP005

Type SCP008
	Field Timer#
End Type

Global I_008.SCP008 = New SCP008

Type SCP294
	Field Using%
	Field ToInput$
End Type

Global I_294.SCP294 = New SCP294

Type SCP409
    Field Timer#
End Type 

Global I_409.SCP409 = New SCP409

Type SCP427
	Field Using%
	Field Timer#
	Field Sound%[2]
	Field SoundCHN%[2]
End Type

Global I_427.SCP427 = New SCP427

Type SCP714
	Field Using%
End Type

Global I_714.SCP714 = New SCP714

Type SCP1025
	Field State#[6]
End Type

Global I_1025.SCP1025 = New SCP1025

Type SCP1499
	Field Using%
    Field PrevX#, PrevY#, PrevZ#
    Field PrevRoom.Rooms
    Field x#, y#, z#
	Field Sky%
End Type

Global I_1499.SCP1499 = New SCP1499

Type MapZones
	Field Transition%[2]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones = New MapZones

Function CatchErrors(Location$)
	InitErrorMsgs(8)
	SetErrorMsg(0, "An error occured in SCP - Containment Breach Ultimate Edition v" + VersionNumber + ". Engine version: " + SystemProperty("blitzversion") + Chr(10))
	SetErrorMsg(1, "Date and time: " + CurrentDate() + " at " + CurrentTime() + Chr(10) + "OS: " + SystemProperty("os") + " " + (32 + (GetEnv("ProgramFiles(X86)") <> 0) * 32) + " bit (Build: " + SystemProperty("osbuild") + ")" + Chr(10))
	SetErrorMsg(2, "CPU: " + GetEnv("PROCESSOR_IDENTIFIER") + " (Arch: " + GetEnv("PROCESSOR_ARCHITECTURE") + ", " + GetEnv("NUMBER_OF_PROCESSORS") + " Threads)" + Chr(10))
	SetErrorMsg(3, "GPU: " + GfxDriverName(CountGfxDrivers()) + " (" + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB)" + Chr(10))
	SetErrorMsg(4, "Video memory: " + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB" + Chr(10))
	SetErrorMsg(5, "Global memory status: " + ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB" + Chr(10))
	SetErrorMsg(6, "Triangles rendered: " + CurrTrisAmount + ", Active textures: " + ActiveTextures() + Chr(10) + Chr(10))
	SetErrorMsg(7, "Error located in: " + Location + Chr(10) + Chr(10) + "Please take a screenshot of this error and send it to us!") 
End Function

Repeat
	Cls()
	
	Local ElapsedMilliSeconds%
	
	ft\CurrTime = MilliSecs()
	ElapsedMilliSeconds = ft\CurrTime - ft\PrevTime
	If ElapsedMilliSeconds > 0 And ElapsedMilliSeconds < 500 Then
		ft\Accumulator = ft\Accumulator + Max(0.0, Float(ElapsedMilliSeconds) * 70.0 / 1000.0)
	EndIf
	ft\PrevTime = ft\CurrTime
	
	If Framelimit > 0.0 Then
		; ~ Framelimit
		Local WaitingTime% = (1000.0 / Framelimit) - (MilliSecs() - fpst\LoopDelay)
		
		Delay(WaitingTime)
		
		fpst\LoopDelay = MilliSecs()
	EndIf
	
	fpst\FPSFactor[0] = TICK_DURATION
	fpst\FPSFactor[1] = fpst\FPSFactor[0]
	
	If MainMenuOpen Then
		UpdateMainMenu()
	Else
		MainLoop()
	EndIf
	
	GammaUpdate()
	
	Local x%, y%, ScreenshotCount%
	
	If KeyHit(key\SCREENSHOT) Then
		If FileType("Screenshots\") <> 2 Then
			CreateDir("Screenshots")
		EndIf
		
		Local Bank% = CreateBank(RealGraphicWidth * RealGraphicHeight * 3)
		
		LockBuffer(BackBuffer())
		For x = 0 To RealGraphicWidth - 1
			For y = 0 To RealGraphicHeight - 1
				Local Pixel% = ReadPixelFast(x, y, BackBuffer())
				
				PokeByte(Bank, (y * (RealGraphicWidth * 3)) + (x * 3), (Pixel Shr 0) And $FF)
				PokeByte(Bank, (y * (RealGraphicWidth * 3)) + (x * 3) + 1, (Pixel Shr 8) And $FF)
				PokeByte(Bank, (y * (RealGraphicWidth * 3)) + (x * 3) + 2, (Pixel Shr 16) And $FF)
			Next
		Next
		UnlockBuffer(BackBuffer())
		
		Local fiBuffer% = FI_ConvertFromRawBits(Bank, RealGraphicWidth, RealGraphicHeight, RealGraphicWidth * 3, 24, $FF0000, $00FF00, $0000FF, True)
		
		FI_Save(13, fiBuffer, "Screenshots\Screenshot" + ScreenshotCount + ".png", 0)
		FI_Unload(fiBuffer)
		FreeBank(Bank)
		ScreenshotCount = ScreenshotCount + 1
		If (Not MainMenuOpen) Then
			msg\Msg = "Screenshot Taken."
			msg\Timer = 70.0 * 6.0
		EndIf
		PlaySound_Strict(LoadTempSound("SFX\General\Screenshot.ogg"))
	EndIf
	
	If ShowFPS Then
		If ft\FPSGoal < MilliSecs() Then
			ft\FPS = ft\TempFPS
			ft\TempFPS = 0
			ft\FPSGoal = MilliSecs() + 1000
		Else
			ft\TempFPS = ft\TempFPS + 1
		EndIf
	EndIf
	
	CatchErrors("Main Loop / Uncaught")
	
	If VSync = 0 Then
		Flip(0)
	Else 
		Flip(1)
	EndIf
Forever

Const FogColorLCZ$ = "010010010"
Const FogColorHCZ$ = "010006006"
Const FogColorEZ$ = "010010020"
Const FogColorStorageTunnels$ = "005015003"
Const FogColorOutside$ = "255255255"
Const FogColorDimension1499$ = "096097104"
Const FogColorPD$ = "000000000"
Const FogColorPDTrench$ = "038055047"
Const FogColorForest$ = "098133162"

Const TICK_DURATION# = 70.0 / 60.0

Function MainLoop()
	Local e.Events, r.Rooms, i%
	
	While ft\Accumulator > 0.0
		ft\Accumulator = ft\Accumulator - TICK_DURATION
		If ft\Accumulator =< 0.0 Then CaptureWorld()
		
		If MenuOpen Lor InvOpen Lor OtherOpen <> Null Lor ConsoleOpen Lor SelectedDoor <> Null Lor SelectedScreen <> Null Lor I_294\Using Then fpst\FPSFactor[0] = 0.0
		
		If Input_ResetTime > 0.0 Then
			Input_ResetTime = Max(Input_ResetTime - fpst\FPSFactor[0], 0.0)
		Else
			DoubleClick = False
			MouseHit1 = MouseHit(1)
			If MouseHit1 Then
				If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
				LastMouseHit1 = MilliSecs()
			EndIf
			
			Local PrevMouseDown1% = MouseDown1
			
			MouseDown1 = MouseDown(1)
			If PrevMouseDown1 = True And MouseDown1 = False Then MouseUp1 = True Else MouseUp1 = False
			
			MouseHit2 = MouseHit(2)
		EndIf
		
		If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
		
		UpdateMusic()
		If EnableSFXRelease Then AutoReleaseSounds()
		
		UpdateStreamSounds()
		
		DrawHandIcon = False
		
		me\RestoreSanity = True
		ShouldEntitiesFall = True
		
		If fpst\FPSFactor[0] > 0.0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then UpdateSecurityCams()
		
		If (Not MenuOpen) And (Not InvOpen) And (OtherOpen = Null) And (SelectedDoor = Null) And (ConsoleOpen = False) And (I_294\Using = False) And (SelectedScreen = Null) And me\EndingTimer >= 0.0 Then
			ShouldPlay = Min(me\Zone, 2.0)
		EndIf
		
		If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(me\Collider) =< 1040.0 * RoomScale) And (Not MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then 
			If Rand(1500) = 1 Then
				For i = 0 To 5
					If AmbientSFX(i, CurrAmbientSFX) <> 0 Then
						If (Not ChannelPlaying(AmbientSFXCHN)) Then FreeSound_Strict(AmbientSFX(i, CurrAmbientSFX)) : AmbientSFX(i, CurrAmbientSFX) = 0
					EndIf			
				Next
				
				PositionEntity(SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
				
				If Rand(3) = 1 Then me\Zone = 3
				
				If PlayerRoom\RoomTemplate\Name = "room173intro" Then 
					me\Zone = 4
				ElseIf PlayerRoom\RoomTemplate\Name = "room860"
					For e.Events = Each Events
						If e\EventName = "room860" Then
							If e\EventState = 1.0 Then
								me\Zone = 5
								PositionEntity(SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
							EndIf
							Exit
						EndIf
					Next
				EndIf
				
				CurrAmbientSFX = Rand(0, AmbientSFXAmount[me\Zone] - 1)
				
				Select me\Zone
					Case 0, 1, 2
						;[Block]
						If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Zone" + (me\Zone + 1) + "\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 3
						;[Block]
						If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\General\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 4
						;[Block]
						If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Pre-breach\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 5
						;[Block]
						If AmbientSFX(me\Zone, CurrAmbientSFX) = 0 Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Forest\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
				End Select
				
				AmbientSFXCHN = PlaySound2(AmbientSFX(me\Zone, CurrAmbientSFX), Camera, SoundEmitter)
			EndIf
			UpdateSoundOrigin(AmbientSFXCHN, Camera, SoundEmitter)
			
			If Rand(50000) = 3 Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				
				If RN <> "room860" And RN <> "room1123" And RN <> "room173intro" And RN <> "dimension1499" And RN <> "pocketdimension" Then
					If fpst\FPSFactor[0] > 0.0 Then me\LightBlink = Rnd(1.0, 2.0)
					PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(1, 8) + ".ogg"))
				EndIf 
			EndIf
		EndIf
		
		UpdateCheckpoint1 = False
		UpdateCheckpoint2 = False
		
		If (Not MenuOpen) And (Not InvOpen) And OtherOpen = Null And SelectedDoor = Null And (Not ConsoleOpen) And (Not I_294\Using) And SelectedScreen = Null And me\EndingTimer >= 0.0 Then
			LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
			CameraFogRange(Camera, CameraFogNear * LightVolume, CameraFogFar * LightVolume)
			CameraFogMode(Camera, 1)
			CameraRange(Camera, 0.01, Min(CameraFogFar * LightVolume * 1.5, 28.0))	
			For r.Rooms = Each Rooms
				For i = 0 To r\MaxLights - 1
					If r\Lights[i] <> 0 Then
						EntityAutoFade(r\LightSprites[i], CameraFogNear * LightVolume, CameraFogFar * LightVolume)
					EndIf
				Next
			Next
			
			AmbientLight(Brightness, Brightness, Brightness)
			me\SndVolume = CurveValue(0.0, me\SndVolume, 5.0)
			
			CanSave = True
			UpdateDeaf()
			UpdateEmitters()
			MouseLook()
			If PlayerRoom\RoomTemplate\Name = "dimension1499" And QuickLoadPercent > 0 And QuickLoadPercent < 100
				ShouldEntitiesFall = False
			EndIf
			MovePlayer()
			InFacility = CheckForPlayerInFacility()
			If PlayerRoom\RoomTemplate\Name = "dimension1499"
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
					UpdateDimension1499()
				EndIf
				UpdateLeave1499()
			ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Lor (PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale)
				UpdateDoors()
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
					UpdateEndings()
				EndIf
				UpdateScreens()
				UpdateRoomLights(Camera)
			Else
				UpdateDoors()
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
					UpdateEvents()
				EndIf
				UpdateScreens()
				TimeCheckpointMonitors()
				Update294()
				UpdateRoomLights(Camera)
			EndIf
			UpdateDecals()
			UpdateMTF()
			UpdateNPCs()
			UpdateItems()
			UpdateParticles()
			Use427()
			UpdateMonitorSaving()
			; ~ Added a simple code for updating the Particles function depending on the fpst\FPSFactor[0] (still WIP, might not be the final version of it) -- ENDSHN
			UpdateParticles_Time = Min(1.0, UpdateParticles_Time + fpst\FPSFactor[0])
			If UpdateParticles_Time = 1.0
				UpdateParticles_Time = 0.0
			EndIf
		EndIf
		
		Local CurrFogColor$ = ""
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "room3storage" And EntityY(me\Collider) < -4100.0 * RoomScale Then
				CurrFogColor = FogColorStorageTunnels
			ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Lor (PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale) Then
				CurrFogColor = FogColorOutside
			ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499"
				CurrFogColor = FogColorDimension1499
			ElseIf PlayerRoom\RoomTemplate\Name = "room860"
				For e.Events = Each Events
					If e\EventName = "room860" Then
						If e\EventState = 1.0 Then
							CurrFogColor = FogColorForest
						EndIf
						Exit
					EndIf
				Next
			ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
				For e.Events = Each Events
					If e\EventName = "pocketdimension"
						If EntityY(me\Collider) > 2608.0 * RoomScale Lor e\EventState2 > 1.0 Then
							CurrFogColor = FogColorPDTrench
						Else
							CurrFogColor = FogColorPD
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		If CurrFogColor = "" Then
			Select me\Zone
				Case 0
					;[Block]
					CurrFogColor = FogColorLCZ
					;[End Block]
				Case 1
					;[Block]
					CurrFogColor = FogColorHCZ
					;[End Block]
				Case 2
					;[Block]
					CurrFogColor = FogColorEZ
					;[End Block]
			End Select
		EndIf
		
		Local FogColorR% = Left(CurrFogColor, 3)
		Local FogColorG% = Mid(CurrFogColor, 4, 3)
		Local FogColorB% = Right(CurrFogColor, 3)
		
		CameraFogColor(Camera, FogColorR, FogColorG, FogColorB)
		CameraClsColor(Camera, FogColorR, FogColorG, FogColorB)
		
		If chs\InfiniteStamina Then me\Stamina = 100.0
		If chs\NoBlink Then me\BlinkTimer = me\BLINKFREQ
		
		If fpst\FPSFactor[0] = 0.0
			UpdateWorld(0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
		EndIf
		
		me\BlurVolume = Min(CurveValue(0.0, me\BlurVolume, 20.0), 0.95)
		If me\BlurTimer > 0.0 Then
			me\BlurVolume = Max(Min(0.95, me\BlurTimer / 1000.0), me\BlurVolume)
			me\BlurTimer = Max(me\BlurTimer - fpst\FPSFactor[0], 0.0)
		EndIf
		
		Local DarkA# = 0.0
		
		If (Not MenuOpen)  Then
			If me\Sanity < 0.0 Then
				If me\RestoreSanity Then me\Sanity = Min(me\Sanity + fpst\FPSFactor[0], 0.0)
				If me\Sanity < -200.0 Then 
					DarkA = Max(Min((-me\Sanity - 200.0) / 700.0, 0.6), DarkA)
					If me\KillTimer >= 0.0 Then 
						me\HeartBeatVolume = Min(Abs(me\Sanity + 20.00) / 500.0, 1.0)
						me\HeartBeatRate = Max(70.0 + Abs(me\Sanity + 200.0) / 6.0, me\HeartBeatRate)
					EndIf
				EndIf
			EndIf
			
			If me\EyeStuck > 0.0 Then 
				me\BlinkTimer = me\BLINKFREQ
				me\EyeStuck = Max(me\EyeStuck - fpst\FPSFactor[0], 0.0)
				
				If me\EyeStuck < 9000.0 Then me\BlurTimer = Max(me\BlurTimer, (9000.0 - me\EyeStuck) * 0.5)
				If me\EyeStuck < 6000.0 Then DarkA = Min(Max(DarkA, (6000.0 - me\EyeStuck) / 5000.0), 1.0)
				If me\EyeStuck < 9000.0 And me\EyeStuck + fpst\FPSFactor[0] >= 9000.0 Then 
					msg\Msg = "The eyedrops are causing your eyes to tear up."
					msg\Timer = 70.0 * 6.0
				EndIf
			EndIf
			
			If me\BlinkTimer < 0.0 Then
				If me\BlinkTimer > -5.0 Then
					DarkA = Max(DarkA, Sin(Abs(me\BlinkTimer * 18.0)))
				ElseIf me\BlinkTimer > -15.0
					DarkA = 1.0
				Else
					DarkA = Max(DarkA, Abs(Sin(me\BlinkTimer * 18.0)))
				EndIf
				
				If me\BlinkTimer =< -20.0 Then
					; ~ Randomizes the frequency of blinking. Scales with difficulty
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							me\BLINKFREQ = Rnd(490.0, 700.0)
							;[End Block]
						Case NORMAL
							;[Block]
							me\BLINKFREQ = Rnd(455.0, 665.0)
							;[End Block]
						Case HARD
							;[Block]
							me\BLINKFREQ = Rnd(420.0, 630.0)
							;[End Block]
					End Select 
					me\BlinkTimer = me\BLINKFREQ
				EndIf
				me\BlinkTimer = me\BlinkTimer - fpst\FPSFactor[0]
			Else
				me\BlinkTimer = me\BlinkTimer - fpst\FPSFactor[0] * 0.6 * me\BlinkEffect
				If wi\NightVision = 0 Then
					If me\EyeIrritation > 0.0 Then me\BlinkTimer = me\BlinkTimer - Min(me\EyeIrritation / 100.0 + 1.0, 4.0) * fpst\FPSFactor[0]
				EndIf
				DarkA = Max(DarkA, 0.0)
			EndIf
			
			me\EyeIrritation = Max(0.0, me\EyeIrritation - fpst\FPSFactor[0])
			
			If me\BlinkEffectTimer > 0.0 Then
				me\BlinkEffectTimer = me\BlinkEffectTimer - (fpst\FPSFactor[0] / 70.0)
			Else
				If me\BlinkEffect <> 1.0 Then me\BlinkEffect = 1.0
			EndIf
			
			me\LightBlink = Max(me\LightBlink - (fpst\FPSFactor[0] / 35.0), 0.0)
			If me\LightBlink > 0.0 And wi\NightVision = 0 Then DarkA = Min(Max(DarkA, me\LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If I_294\Using Then DarkA = 1.0
			
			If wi\NightVision = 0 Then DarkA = Max((1.0 - SecondaryLightOn) * 0.9, DarkA)
			
			If me\KillTimer < 0.0 Then
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				me\BlurTimer = Abs(me\KillTimer * 5.0)
				me\KillTimer = me\KillTimer - (fpst\FPSFactor[0] * 0.8)
				If me\KillTimer < -360.0 Then 
					MenuOpen = True 
					If me\SelectedEnding <> "" Then me\EndingTimer = Min(me\KillTimer, -0.1)
				EndIf
				DarkA = Max(DarkA, Min(Abs(me\KillTimer / 400.0), 1.0))
			Else
				HideEntity(tt\OverlayID[10])
			EndIf
			
			If me\FallTimer < 0.0 Then
				If SelectedItem <> Null Then
					If Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Lor Instr(SelectedItem\ItemTemplate\TempName, "vest") Then
						If wi\HazmatSuit = 0 And wi\BallisticVest = 0 Then
							DropItem(SelectedItem)
						EndIf
					EndIf
				EndIf
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				me\BlurTimer = Abs(me\FallTimer * 10.0)
				me\FallTimer = me\FallTimer - fpst\FPSFactor[0]
				DarkA = Max(DarkA, Min(Abs(me\FallTimer / 400.0), 1.0))				
			EndIf
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\TempName = "navigator" Lor SelectedItem\ItemTemplate\TempName = "nav" Then DarkA = Max(DarkA, 0.5)
			EndIf
			If SelectedScreen <> Null Then DarkA = Max(DarkA, 0.5)
			
			EntityAlpha(tt\OverlayID[6], DarkA)	
		EndIf
		
		If me\LightFlash > 0.0 Then
			ShowEntity(tt\OverlayID[7])
			EntityAlpha(tt\OverlayID[7], Max(Min(me\LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
			me\LightFlash = Max(me\LightFlash - (fpst\FPSFactor[0] / 70.0), 0.0)
		Else
			HideEntity(tt\OverlayID[7])
		EndIf
		
		EntityColor(tt\OverlayID[7], 255.0, 255.0, 255.0)
		
		UpdateGUI()
		
		If KeyHit(key\INVENTORY) And me\VomitTimer >= 0.0 Then
			If (Not UnableToMove) And (Not me\Zombie) And (Not I_294\Using) Then
				Local W$ = ""
				Local V# = 0
				
				If SelectedItem <> Null
					W = SelectedItem\ItemTemplate\TempName
					V = SelectedItem\State
					; ~ Reset SCP-1025
					If SelectedItem\ItemTemplate\TempName = "scp1025" Then
						If SelectedItem\ItemTemplate\Img <> 0 Then FreeImage(SelectedItem\ItemTemplate\Img)
						SelectedItem\ItemTemplate\Img = 0
					EndIf
				EndIf
				If (W <> "vest" And W <> "finevest" And W <> "hazmatsuit" And W <> "hazmatsuit2" And W <> "hazmatsuit3") Lor V = 0 Lor V = 100
					If InvOpen Then
						ResumeSounds()
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
					Else
						DoubleClickSlot = -1
						PauseSounds()
					EndIf
					InvOpen = (Not InvOpen)
					If OtherOpen <> Null Then OtherOpen = Null
					SelectedItem = Null
				EndIf
			EndIf
		EndIf
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "room173intro" Then
				For e.Events = Each Events
					If e\EventName = "room173intro" Then
						If e\EventState3 >= 40.0 And e\EventState3 < 50.0 Then
							If InvOpen Then
								msg\Msg = "Double click on the document to view it."
								msg\Timer = 70.0 * 6.0
								e\EventState3 = 50.0
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		If KeyHit(key\SAVE) Then
			If SelectedDifficulty\SaveType = SAVEANYWHERE Then
				RN = PlayerRoom\RoomTemplate\Name
				If RN = "room173intro" Lor (RN = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale) Lor RN = "gatea"
					msg\Msg = "You cannot save in this location."
					msg\Timer = 70.0 * 6.0
				ElseIf (Not CanSave) Lor QuickLoadPercent > -1
					msg\Msg = "You cannot save at this moment."
					msg\Timer = 70.0 * 6.0
					If QuickLoadPercent > -1 Then
						msg\Msg = msg\Msg + " (game is loading)"
					EndIf
				Else
					SaveGame(SavePath + CurrSave + "\")
				EndIf
			ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS
				If SelectedScreen = Null And SelectedMonitor = Null Then
					msg\Msg = "Saving is only permitted on clickable monitors scattered throughout the facility."
					msg\Timer = 70.0 * 6.0
				Else
					RN = PlayerRoom\RoomTemplate\Name
					If RN = "room173intro" Lor (RN = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale) Lor RN = "gatea"
						msg\Msg = "You cannot save in this location."
						msg\Timer = 70.0 * 6.0
					ElseIf (Not CanSave) Lor QuickLoadPercent > -1
						msg\Msg = "You cannot save at this moment."
						msg\Timer = 70.0 * 6.0
						If QuickLoadPercent > -1 Then
							msg\Msg = msg\Msg + " (game is loading)"
						EndIf
					Else
						If SelectedScreen <> Null Then
							GameSaved = False
							me\Playable = True
							me\DropSpeed = 0.0
						EndIf
						SaveGame(SavePath + CurrSave + "\")
					EndIf
				EndIf
			Else
				msg\Msg = "Quick saving is disabled."
				msg\Timer = 70.0 * 6.0
			EndIf
		ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS And (SelectedScreen <> Null Lor SelectedMonitor <> Null)
			If (msg\Msg <> "Game progress saved." And msg\Msg <> "You cannot save in this location." And msg\Msg <> "You cannot save at this moment.") Lor msg\Timer =< 0 Then
				msg\Msg = "Press " + key\Name[key\SAVE] + " to save."
				msg\Timer = 70.0 * 6.0
			EndIf
			If MouseHit2 Then SelectedMonitor = Null
		EndIf
		
		If KeyHit(key\CONSOLE) Then
			If CanOpenConsole Then
				If ConsoleOpen Then
					UsedConsole = True
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
					DeleteMenuGadgets()
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		If me\EndingTimer < 0.0 Then
			If me\SelectedEnding <> "" Then UpdateEnding()
		Else
			UpdateMenu()			
		EndIf
		
		UpdateMessages()
		
		UpdateQuickLoading()
		
		UpdateAchievementMsg()
	Wend
	
	; ~ Go out of function immediately if the game has been quit
	If MainMenuOpen Then Return
	
	RenderWorld2(Max(0.0, 1.0 + (ft\Accumulator / TICK_DURATION)))
	
	UpdateBlur(me\BlurVolume)
	
	DrawGUI()
	
	RenderMessages()
	
	If me\EndingTimer < 0.0 Then
		If me\SelectedEnding <> "" Then DrawEnding()
	Else
		DrawMenu()			
	EndIf
	
	UpdateConsole()
	
	DrawQuickLoading()
	
	RenderAchievementMsg()
End Function

Function UpdateMessages()
	If msg\Timer > 0.0 Then
		msg\Timer = msg\Timer - fpst\FPSFactor[0] 
	EndIf
End Function

Function RenderMessages()
	If msg\Timer > 0.0 Then
		Local Temp% = False
		
		If (Not (InvOpen Lor OtherOpen <> Null)) And SelectedItem <> Null And (SelectedItem\ItemTemplate\TempName = "paper" Lor SelectedItem\ItemTemplate\TempName = "oldpaper") Then
			Temp = True
		EndIf
		
		Local Temp2% = Min(msg\Timer / 2.0, 255.0)
		
		SetFont(fo\FontID[0])
		If (Not Temp) Then
			Color(0, 0, 0)
			Text((GraphicWidth / 2) + 1, (GraphicHeight / 2) + 201, msg\Msg, True, False)
			Color(Temp2, Temp2, Temp2)
			Text((GraphicWidth / 2), (GraphicHeight / 2) + 200, msg\Msg, True, False)
		Else
			Color(0, 0, 0)
			Text((GraphicWidth / 2) + 1, (GraphicHeight * 0.94) + 1, msg\Msg, True, False)
			Color(Temp2, Temp2, Temp2)
			Text((GraphicWidth / 2), (GraphicHeight * 0.94), msg\Msg, True, False)
		EndIf
	EndIf
	
	Color(255, 255, 255)
	If ShowFPS Then SetFont(fo\ConsoleFont) : Text(20, 20, "FPS: " + ft\FPS) : SetFont(fo\FontID[0])
End Function

Function Kill(IsBloody% = False)
	If chs\GodMode Then Return
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
	EndIf
	
	If me\KillTimer >= 0.0 Then
		If IsBloody Then ShowEntity(tt\OverlayID[10])
		
		me\KillAnim = Rand(0, 1)
		PlaySound_Strict(DamageSFX[0])
		If SelectedDifficulty\PermaDeath Then
			DeleteFile(CurrentDir() + SavePath + CurrSave + "\Save.txt") 
			DeleteDir(SavePath + CurrSave) 
			LoadSaveGames()
		EndIf
		
		me\KillTimer = Min(-1.0, me\KillTimer)
		ShowEntity(me\Head)
		PositionEntity(me\Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity(me\Head)
		RotateEntity(me\Head, 0.0, EntityYaw(Camera), 0.0)		
	EndIf
End Function

Function DrawEnding()
	ShowPointer()
	
	Local x%, y%, Width%, Height%, i%
	Local itt.ItemTemplates, r.Rooms
	
	Select me\SelectedEnding
		Case "B2", "A1"
			;[Block]
			ClsColor(Max(255.0 + (me\EndingTimer) * 2.8, 0.0), Max(255.0 + (me\EndingTimer) * 2.8, 0.0), Max(255.0 + (me\EndingTimer) * 2.8, 0.0))
			;[End Block]
		Default
			;[Block]
			ClsColor(0, 0, 0)
			;[End Block]
	End Select
	
	Cls()
	
	If me\EndingTimer < -200.0 Then
		If me\EndingTimer > -700.0 Then 
			If Rand(1, 150) < Min((Abs(me\EndingTimer) - 200.0), 155.0) Then
				DrawImage(me\EndingScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
			Else
				Color(0, 0, 0)
				Rect(100, 100, GraphicWidth - 200, GraphicHeight - 200)
				Color(255, 255, 255)
			EndIf
		Else
			DrawImage(me\EndingScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
			
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0 Then
				Width = ImageWidth(tt\ImageID[0])
				Height = ImageHeight(tt\ImageID[0])
				x = GraphicWidth / 2 - Width / 2
				y = GraphicHeight / 2 - Height / 2
				
				DrawImage(tt\ImageID[0], x, y)
				
				Color(255, 255, 255)
				SetFont(fo\FontID[1])
				Text(x + Width / 2 + 40 * MenuScale, y + 20 * MenuScale, "THE END", True)
				SetFont(fo\FontID[0])
				
				If AchievementsMenu = 0 Then 
					x = x + 132 * MenuScale
					y = y + 122 * MenuScale
					
					Local RoomAmount% = 0, RoomsFound% = 0
					
					For r.Rooms = Each Rooms
						RoomAmount = RoomAmount + 1
						RoomsFound = RoomsFound + r\Found
					Next
					
					Local DocAmount% = 0, DocsFound% = 0
					
					For itt.ItemTemplates = Each ItemTemplates
						If itt\TempName = "paper" Then
							DocAmount = DocAmount + 1
							DocsFound = DocsFound + itt\Found
						EndIf
					Next
					
					Local SCPsEncountered% = 1
					
					For i = 0 To 24
						SCPsEncountered = SCPsEncountered + Achievements[i]
					Next
					
					Local AchievementsUnlocked% = 0
					
					For i = 0 To MAXACHIEVEMENTS - 1
						AchievementsUnlocked = AchievementsUnlocked + Achievements[i]
					Next
					
					Text(x, y, "SCPs encountered: " + SCPsEncountered)
					Text(x, y + 20 * MenuScale, "Achievements unlocked: " + AchievementsUnlocked + "/" + (MAXACHIEVEMENTS))
					Text(x, y + 40 * MenuScale, "Rooms found: " + RoomsFound + "/" + RoomAmount)
					Text(x, y + 60 * MenuScale, "Documents discovered: " + DocsFound + "/" + DocAmount)
					Text(x, y + 80 * MenuScale, "Items refined in SCP-914: " + me\RefinedItems)
				Else
					DrawMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer =< -2000.0
				DrawCredits()
			EndIf
		EndIf
	EndIf
	
	RenderMenuButtons()
	
	If DisplayMode = 0 Then DrawImage(CursorIMG), ScaledMouseX(), ScaledMouseY()
	
	SetFont(fo\FontID[0])
End Function

Function UpdateEnding()
	fpst\FPSFactor[0] = 0.0
	If me\EndingTimer > -2000.0 Then
		me\EndingTimer = Max(me\EndingTimer - fpst\FPSFactor[1], -1111.0)
	Else
		me\EndingTimer = me\EndingTimer - fpst\FPSFactor[1]
	EndIf
	
	GiveAchievement(Achv055)
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\Name = "Keter" Then GiveAchievement(AchvKeter)
	
	Local x%, y%, Width%, Height%, i%
	Local itt.ItemTemplates, r.Rooms
	
	ShouldPlay = 66
	
	If me\EndingTimer < -200.0 Then
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN) : me\Stamina = 100.0
		EndIf
		
		If BreathGasRelaxedCHN <> 0 Then
			If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
		EndIf
		
		If me\EndingScreen = 0 Then
			me\EndingScreen = LoadImage_Strict("GFX\ending_screen.png")
			
			ShouldPlay = 23
			CurrMusicVolume = MusicVolume
			
			CurrMusicVolume = MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music[23] + ".ogg", CurrMusicVolume, 0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict(LightSFX)
		EndIf
		
		If me\EndingTimer > -700.0 Then 
			If me\EndingTimer + fpst\FPSFactor[1] > -450.0 And me\EndingTimer =< -450.0 Then
				Select me\SelectedEnding
					Case "A1", "A2"
						;[Block]
						PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Ending" + me\SelectedEnding + ".ogg"))
						;[End Block]
					Case "B1", "B2", "B3"
						;[Block]
						PlaySound_Strict(LoadTempSound("SFX\Ending\GateB\Ending" + me\SelectedEnding + ".ogg"))
						;[End Block]
				End Select
			EndIf			
		Else
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0 Then
				Width = ImageWidth(tt\ImageID[0])
				Height = ImageHeight(tt\ImageID[0])
				x = GraphicWidth / 2 - Width / 2
				y = GraphicHeight / 2 - Height / 2
				
				If AchievementsMenu = 0 Then 
					x = x + 132 * MenuScale
					y = y + 122 * MenuScale
					
					x = GraphicWidth / 2 - Width / 2
					y = GraphicHeight / 2 - Height / 2
					x = x + Width / 2
					y = y + Height - 100 * MenuScale
					
					If DrawButton(x - 170 * MenuScale, y - 200 * MenuScale, 430 * MenuScale, 60 * MenuScale, "ACHIEVEMENTS", True) Then
						AchievementsMenu = 1
						DeleteMenuGadgets()
					EndIf
					
					If DrawButton(x - 170 * MenuScale, y - 100 * MenuScale, 430 * MenuScale, 60 * MenuScale, "MAIN MENU", True)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i = 0 To 9
							If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
						SetStreamVolume_Strict(MusicCHN, 1.0 * MusicVolume)
						FlushKeys()
						me\EndingTimer = -2000.0
						DeleteMenuGadgets()
						InitCredits()
					EndIf
				Else
					ShouldPlay = 23
					UpdateMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer =< -2000.0
				ShouldPlay = 24
				UpdateCredits()
			EndIf
		EndIf
	EndIf
End Function

Type CreditsLine
	Field Txt$
	Field ID%
	Field Stay%
End Type

Function InitCredits()
	Local cl.CreditsLine
	Local File% = OpenFile("Credits.txt")
	Local l$
	
	fo\CreditsFontID[0] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 21)
	fo\CreditsFontID[1] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 35)
	
	If me\CreditsScreen = 0
		me\CreditsScreen = LoadImage_Strict("GFX\credits_screen.png")
	EndIf
	
	Repeat
		l = ReadLine(File)
		cl.CreditsLine = New CreditsLine
		cl\Txt = l
	Until Eof(File)
	
	Delete First CreditsLine
	me\CreditsTimer = 0.0
End Function

Function DrawCredits()
    Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (GraphicHeight + 10.0)
    Local cl.CreditsLine
    Local ID%
    Local EndLinesAmount%
	Local LastCreditLine.CreditsLine
	
    Cls()
	
	If Rand(1, 300) > 1 Then
		DrawImage(me\CreditsScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
	EndIf
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	Color(255, 255, 255)
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "*"
			SetFont(fo\CreditsFontID[1])
			If cl\Stay = False
				Text(GraphicWidth / 2, Credits_Y + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
			EndIf
		ElseIf Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		Else
			SetFont(fo\CreditsFontID[0])
			If cl\Stay = False
				Text(GraphicWidth / 2, Credits_Y + (24 * cl\ID * MenuScale), cl\Txt, True)
			EndIf
		EndIf
		If LastCreditLine <> Null
			If cl\ID > LastCreditLine\ID
				cl\Stay = True
			EndIf
		EndIf
		If cl\Stay
			EndLinesAmount = EndLinesAmount + 1
		EndIf
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			Color(Max(Min(me\CreditsTimer, 255.0), 0.0), Max(Min(me\CreditsTimer, 255.0), 0.0), Max(Min(me\CreditsTimer, 255.0), 0.0))
		ElseIf me\CreditsTimer >= 255.0
			Color(255, 255, 255)
		Else
			Color(Max(Min(-me\CreditsTimer, 255.0), 0.0), Max(Min(-me\CreditsTimer, 255.0), 0.0), Max(Min(-me\CreditsTimer, 255.0), 0.0))
		EndIf
	EndIf
	If me\CreditsTimer <> 0.0
		For cl.CreditsLine = Each CreditsLine
			If cl\Stay
				SetFont(fo\CreditsFontID[0])
				If Left(cl\Txt, 1) = "/"
					Text(GraphicWidth / 2, (GraphicHeight / 2) + (EndLinesAmount / 2) + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
				Else
					Text(GraphicWidth / 2, (GraphicHeight / 2) + (24 * (cl\ID - LastCreditLine\ID) * MenuScale) - ((EndLinesAmount / 2) * 24 * MenuScale), cl\Txt, True)
				EndIf
			EndIf
		Next
	EndIf
	
	If me\CreditsTimer = -1.0 Then
		FreeFont(fo\CreditsFontID[0])
		FreeFont(fo\CreditsFontID[1])
		FreeImage(me\CreditsScreen)
		me\CreditsScreen = 0
		FreeImage(me\EndingScreen)
		me\EndingScreen = 0
	EndIf
End Function

Function UpdateCredits()
    Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (GraphicHeight + 10.0)
    Local cl.CreditsLine
    Local ID%
    Local EndLinesAmount%
	Local LastCreditLine.CreditsLine
	
    ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		EndIf
		If LastCreditLine <> Null
			If cl\ID > LastCreditLine\ID
				cl\Stay = True
			EndIf
		EndIf
		If cl\Stay
			EndLinesAmount = EndLinesAmount + 1
		EndIf
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		me\CreditsTimer = me\CreditsTimer + (0.5 * fpst\FPSFactor[1])
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			; ~ Just save this line, ok?
		ElseIf me\CreditsTimer >= 255.0
			If me\CreditsTimer > 500.0 Then
				me\CreditsTimer = -255.0
			EndIf
		Else
			If me\CreditsTimer >= -1.0 Then
				me\CreditsTimer = -1.0
			EndIf
		EndIf
	EndIf
	
	If GetKey() Then me\CreditsTimer = -1.0
	
	If me\CreditsTimer = -1.0 Then
		Delete Each CreditsLine
        NullGame(False)
        StopStream_Strict(MusicCHN)
        ShouldPlay = 21
        MenuOpen = False
        MainMenuOpen = True
        MainMenuTab = MainMenuTab_Default
        CurrSave = ""
        FlushKeys()
	EndIf
End Function

Function SetCrouch(NewCrouch%)
	If me\Stamina > 5.0 Then 
		If NewCrouch <> me\Crouch Then 
			PlaySound_Strict(CrouchSFX)
			me\Stamina = me\Stamina - Rnd(15.0, 25.0)
		EndIf
		me\Crouch = NewCrouch
	EndIf
End Function

Function InjurePlayer(Injuries_#, Infection# = 0.0, BlurTimer_# = 0.0, VestFactor# = 0.0, HelmetFactor# = 0.0)
	me\Injuries = me\Injuries + Injuries_ - (wi\BallisticVest * VestFactor) - (me\Crouch * wi\BallisticHelmet * HelmetFactor)
	me\BlurTimer = BlurTimer_
	I_008\Timer = I_008\Timer + Infection
End Function

Function MovePlayer()
	CatchErrors("Uncaught (MovePlayer)")
	
	Local Sprint# = 1.0, Speed# = 0.018
	Local Pvt%, i%, Angle#
	
	If chs\SuperMan Then
		Speed = Speed * 3.0
		
		chs\SuperManTimer = chs\SuperManTimer + fpst\FPSFactor[0]
		
		me\CameraShake = Sin(chs\SuperManTimer / 5.0) * (chs\SuperManTimer / 1500.0)
		
		If chs\SuperManTimer > 70.0 * 50.0 Then
			msg\DeathMsg = "A Class D jumpsuit found in [DATA REDACTED]. Upon further examination, the jumpsuit was found to be filled with 12.5 kilograms of blue ash-like substance. "
			msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the substance remains non-conclusive. Most likely related to SCP-914."
			Kill()
			ShowEntity(tt\OverlayID[0])
		Else
			me\BlurTimer = 500.0		
			HideEntity(tt\OverlayID[0])
		EndIf
	EndIf
	
	If me\DeathTimer > 0.0 Then
		me\DeathTimer = me\DeathTimer - fpst\FPSFactor[0]
		If me\DeathTimer < 1.0 Then me\DeathTimer = -1.0
	ElseIf me\DeathTimer < 0.0 
		Kill()
	EndIf
	
	If me\CurrSpeed > 0.0 Then
        me\Stamina = Min(me\Stamina + 0.15 * fpst\FPSFactor[0] / 1.25, 100.0)
    Else
        me\Stamina = Min(me\Stamina + 0.15 * fpst\FPSFactor[0] * 1.25, 100.0)
    EndIf
	
	If me\StaminaEffectTimer > 0.0 Then
		me\StaminaEffectTimer = me\StaminaEffectTimer - (fpst\FPSFactor[0] / 70.0)
	Else
		If me\StaminaEffect <> 1.0 Then me\StaminaEffect = 1.0
	EndIf
	
	Local Temp#
	
	If me\KillTimer >= 0.0 Then
		If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then 
			If KeyDown(key\SPRINT) And (Not chs\NoClip) Then
				If me\Stamina < 5.0 Then
					Temp = 0.0
					If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
					If (Not ChannelPlaying(BreathCHN)) Then BreathCHN = PlaySound_Strict(BreathSFX((Temp), 0))
				ElseIf me\Stamina < 40.0
					If BreathCHN = 0 Then
						Temp = 0.0
						If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
						BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
						ChannelVolume(BreathCHN, Min((70.0 - me\Stamina) / 70.0, 1.0) * SFXVolume)
					Else
						If (Not ChannelPlaying(BreathCHN)) Then
							Temp = 0.0
							If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
							BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
							ChannelVolume(BreathCHN, Min((70.0 - me\Stamina) / 70.0, 1.0) * SFXVolume)		
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount - 1
		If Inventory[i] <> Null Then
			If Inventory[i]\ItemTemplate\TempName = "finevest" Then me\Stamina = Min(me\Stamina, 60.0)
		EndIf
	Next
	
	If I_714\Using = 1 Then
		me\Stamina = Min(me\Stamina, 10.0)
		me\Sanity = Max(-850.0, me\Sanity)
	EndIf
	
	If I_409\Timer > 10.0 Then 
		me\Stamina = Max(me\Stamina, I_409\Timer / 15.0)
	EndIf
	
	If me\Zombie Then 
		If me\Crouch Then SetCrouch(False)
	EndIf
	
	If Abs(me\CrouchState - me\Crouch) < 0.001 Then 
		me\CrouchState = me\Crouch
	Else
		me\CrouchState = CurveValue(me\Crouch, me\CrouchState, 10.0)
	EndIf
	
	If (Not chs\NoClip) Then 
		If (me\Playable And (KeyDown(key\MOVEMENT_DOWN) Xor KeyDown(key\MOVEMENT_UP)) Lor (KeyDown(key\MOVEMENT_RIGHT) Xor KeyDown(key\MOVEMENT_LEFT))) Lor me\ForceMove > 0 Then
			If me\Crouch = False And (KeyDown(key\SPRINT)) And me\Stamina > 0.0 And (Not me\Zombie) Then
				Sprint = 2.5
				me\Stamina = me\Stamina - fpst\FPSFactor[0] * 0.4 * me\StaminaEffect
				If me\Stamina =< 0.0 Then me\Stamina = -20.0
			EndIf
			
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then 
				If EntityY(me\Collider) < 2000.0 * RoomScale Lor EntityY(me\Collider) > 2608.0 * RoomScale Then
					me\Stamina = 0.0
					Speed = 0.015
					Sprint = 1.0					
				EndIf
			EndIf	
			
			If me\ForceMove > 0.0 Then Speed = Speed * me\ForceMove
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\TempName = "firstaid" Lor SelectedItem\ItemTemplate\TempName = "finefirstaid" Lor SelectedItem\ItemTemplate\TempName = "firstaid2" Then
					Sprint = 0.0
				EndIf
			EndIf
			
			Temp = (me\Shake Mod 360.0)
			
			Local TempCHN%
			
			If (Not UnableToMove) Then me\Shake = (me\Shake + fpst\FPSFactor[0] * Min(Sprint, 1.5) * 7.0) Mod 720.0
			If Temp < 180.0 And (me\Shake Mod 360.0) >= 180.0 And me\KillTimer >= 0.0 Then
				If CurrStepSFX = 0 Then
					Temp = GetStepSound(me\Collider)
					
					If Sprint = 1.0 Then
						me\SndVolume = Max(4.0, me\SndVolume)
						TempCHN = PlaySound_Strict(StepSFX(Temp, 0, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * SFXVolume)
					Else
						me\SndVolume = Max(2.5 - (me\Crouch * 0.6), me\SndVolume)
						TempCHN = PlaySound_Strict(StepSFX(Temp, 1, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * SFXVolume)
					EndIf
				ElseIf CurrStepSFX = 1
					TempCHN = PlaySound_Strict(StepSFX(2, 0, Rand(0, 2)))
					ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.4)) * SFXVolume)
				ElseIf CurrStepSFX = 2
					TempCHN = PlaySound_Strict(StepSFX(3, 0, Rand(0, 2)))
					ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.4)) * SFXVolume)
				ElseIf CurrStepSFX = 3
					If Sprint = 1.0 Then
						me\SndVolume = Max(4.0, me\SndVolume)
						TempCHN = PlaySound_Strict(StepSFX(1, 0, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * SFXVolume)
					Else
						me\SndVolume = Max(2.5 - (me\Crouch * 0.6), me\SndVolume)
						TempCHN = PlaySound_Strict(StepSFX(1, 1, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * SFXVolume)
					EndIf
				EndIf
			EndIf	
		EndIf
	Else
		If KeyDown(key\SPRINT) Then 
			Sprint = 2.5
		ElseIf KeyDown(key\CROUCH)
			Sprint = 0.5
		EndIf
	EndIf
	
	If KeyHit(key\CROUCH) And me\Playable And (Not me\Zombie) And me\Bloodloss < 60.0 And I_427\Timer < 70.0 * 390.0 And (Not chs\NoClip) And (SelectedItem = Null Lor (SelectedItem\ItemTemplate\TempName <> "firstaid" And SelectedItem\ItemTemplate\TempName <> "finefirstaid" And SelectedItem\ItemTemplate\TempName <> "firstaid2")) Then 
		SetCrouch((Not me\Crouch))
	EndIf
	
	Local Temp2# = (Speed * Sprint) / (1.0 + me\CrouchState)
	
	If chs\NoClip Then 
		me\Shake = 0.0
		me\CurrSpeed = 0.0
		me\CrouchState = 0.0
		me\Crouch = False
		
		RotateEntity(me\Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0.0)
		
		Temp2 = Temp2 * chs\NoClipSpeed
		
		If KeyDown(key\MOVEMENT_DOWN) Then MoveEntity(me\Collider, 0.0, 0.0, (-Temp2) * fpst\FPSFactor[0])
		If KeyDown(key\MOVEMENT_UP) Then MoveEntity(me\Collider, 0.0, 0.0, Temp2 * fpst\FPSFactor[0])
		
		If KeyDown(key\MOVEMENT_LEFT) Then MoveEntity(me\Collider, (-Temp2) * fpst\FPSFactor[0], 0.0, 0.0)
		If KeyDown(key\MOVEMENT_RIGHT) Then MoveEntity(me\Collider, Temp2 * fpst\FPSFactor[0], 0.0, 0.0)
		
		ResetEntity(me\Collider)
	Else
		Temp2 = Temp2 / Max((me\Injuries + 3.0) / 3.0, 1.0)
		If me\Injuries > 0.5 Then 
			Temp2 = Temp2 * Min((Sin(me\Shake / 2.0) + 1.2), 1.0)
		EndIf
		
		Temp = False
		If (Not me\Zombie) Then
			If KeyDown(key\MOVEMENT_DOWN) And me\Playable Then
				If (Not KeyDown(key\MOVEMENT_UP)) Then
					Temp = True
					Angle = 180.0
					If KeyDown(key\MOVEMENT_LEFT) Then
						If (Not KeyDown(key\MOVEMENT_RIGHT)) Then
							Angle = 135.0
						EndIf
					ElseIf KeyDown(key\MOVEMENT_RIGHT)
						Angle = -135.0
					EndIf
				Else
					If KeyDown(key\MOVEMENT_LEFT) Then
						If (Not KeyDown(key\MOVEMENT_RIGHT)) Then
							Temp = True
							Angle = 90.0
						EndIf
					ElseIf KeyDown(key\MOVEMENT_RIGHT)
						Temp = True
						Angle = -90.0
					EndIf
				EndIf
			ElseIf KeyDown(key\MOVEMENT_UP) And me\Playable
				Temp = True
				Angle = 0.0
				If KeyDown(key\MOVEMENT_LEFT) Then
					If (Not KeyDown(key\MOVEMENT_RIGHT)) Then
						Angle = 45.0
					EndIf
				ElseIf KeyDown(key\MOVEMENT_RIGHT)
					Angle = -45.0
				EndIf
			ElseIf me\ForceMove > 0.0
				Temp = True
				Angle = me\ForceAngle
			ElseIf me\Playable
				If KeyDown(key\MOVEMENT_LEFT) Then
					If (Not KeyDown(key\MOVEMENT_RIGHT)) Then
						Temp = True
						Angle = 90.0
					EndIf
				ElseIf KeyDown(key\MOVEMENT_RIGHT)
					Temp = True
					Angle = -90.0
				EndIf
			EndIf
		Else
			Temp = True
			Angle = me\ForceAngle
		EndIf
		
		Angle = WrapAngle(EntityYaw(me\Collider, True) + Angle + 90.0)
		
		If Temp Then 
			me\CurrSpeed = CurveValue(Temp2, me\CurrSpeed, 20.0)
		Else
			me\CurrSpeed = Max(CurveValue(0.0, me\CurrSpeed - 0.1, 1.0), 0.0)
		EndIf
		
		If (Not UnableToMove) Then TranslateEntity(me\Collider, Cos(Angle) * me\CurrSpeed * fpst\FPSFactor[0], 0.0, Sin(Angle) * me\CurrSpeed * fpst\FPSFactor[0], True)
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(me\Collider)
			If CollisionY(me\Collider, i) < EntityY(me\Collider) - 0.25 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			If me\DropSpeed < -0.07 Then 
				If CurrStepSFX = 0 Then
					PlaySound_Strict(StepSFX(GetStepSound(me\Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX = 1
					PlaySound_Strict(StepSFX(2, 0, Rand(0, 2)))
				ElseIf CurrStepSFX = 2
					PlaySound_Strict(StepSFX(3, 0, Rand(0, 2)))
				EndIf
				me\SndVolume = Max(3.0, me\SndVolume)
			EndIf
			me\DropSpeed = 0.0
		Else
			If PlayerFallingPickDistance <> 0.0 Then
				Local Pick# = LinePick(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0.0, -PlayerFallingPickDistance, 0.0)
				
				If Pick Then
					me\DropSpeed = Min(Max(me\DropSpeed - 0.006 * fpst\FPSFactor[0], -2.0), 0.0)
				Else
					me\DropSpeed = 0.0
				EndIf
			Else
				me\DropSpeed = Min(Max(me\DropSpeed - 0.006 * fpst\FPSFactor[0], -2.0), 0.0)
			EndIf
		EndIf
		PlayerFallingPickDistance = 10.0
		
		If (Not UnableToMove) And ShouldEntitiesFall Then TranslateEntity(me\Collider, 0.0, me\DropSpeed * fpst\FPSFactor[0], 0.0)
	EndIf
	
	me\ForceMove = False
	
	If me\Injuries > 1.0 Then
		Temp2 = me\Bloodloss
		me\BlurTimer = Max(Max(Sin(MilliSecs() / 100.0) * me\Bloodloss * 30.0, me\Bloodloss * 2.0 * (2.0 - me\CrouchState)), me\BlurTimer)
		If I_427\Using = 0 And I_427\Timer < 70.0 * 360.0 Then
			me\Bloodloss = Min(me\Bloodloss + (Min(me\Injuries, 3.5) / 300.0) * fpst\FPSFactor[0], 100.0)
		EndIf
		
		If Temp2 =< 60.0 And me\Bloodloss > 60.0 Then
			msg\Msg = "You are feeling faint from the amount of blood you have lost."
			msg\Timer = 70.0 * 6.0
		EndIf
	EndIf
	
	If me\Injuries < 0.0 Then me\Injuries = 0.0
	
	Update008()
	Update409()
	
	If me\Bloodloss > 0.0 And me\VomitTimer >= 0.0 Then
		If Rnd(200.0) < Min(me\Injuries, 4.0) Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(me\Collider) + Rnd(-0.05, 0.05), EntityY(me\Collider) - 0.05, EntityZ(me\Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			
			de.Decals = CreateDecal(Rand(15, 16), PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rand(360.0), 0.0)
			de\Size = Rnd(0.03, 0.08) * Min(me\Injuries, 3.0) : EntityAlpha(de\OBJ, 1.0)
			ScaleSprite(de\OBJ, de\Size, de\Size)
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 2)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			
			FreeEntity(Pvt)
		EndIf
		
		me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSecs()) / 20.0) + 1.0) * me\Bloodloss * 0.2)
		
		If me\Bloodloss > 60.0 Then 
			If (Not me\Crouch) Then SetCrouch(True)
		EndIf
		If me\Bloodloss >= 100.0 Then 
			Kill(True)
			me\HeartBeatVolume = 0.0
		ElseIf me\Bloodloss > 80.0
			me\HeartBeatRate = Max(150.0 - (me\Bloodloss - 80.0) * 5.0, me\HeartBeatRate)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, 0.75 + (me\Bloodloss - 80.0) * 0.0125)	
		ElseIf me\Bloodloss > 35.0
			me\HeartBeatRate = Max(70.0 + me\Bloodloss, me\HeartBeatRate)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, (me\Bloodloss - 35.0) / 60.0)			
		EndIf
	EndIf
	
	If me\HealTimer > 0.0 Then
		me\HealTimer = me\HealTimer - (fpst\FPSFactor[0] / 70.0)
		me\Bloodloss = Min(me\Bloodloss + (2.0 / 400.0) * fpst\FPSFactor[0], 100.0)
		me\Injuries = Max(me\Injuries - (fpst\FPSFactor[0] / 70.0) / 30.0, 0.0)
	EndIf
		
	If me\Playable Then
		If KeyHit(key\BLINK) Then me\BlinkTimer = 0.0
		If KeyDown(key\BLINK) And me\BlinkTimer < -10.0 Then me\BlinkTimer = -10.0
	EndIf
	
	If me\HeartBeatVolume > 0.0 Then
		If me\HeartBeatTimer =< 0.0 Then
			TempCHN = PlaySound_Strict(HeartBeatSFX)
			ChannelVolume(TempCHN, me\HeartBeatVolume * SFXVolume)
			
			me\HeartBeatTimer = 70.0 * (60.0 / Max(me\HeartBeatRate, 1.0))
		Else
			me\HeartBeatTimer = me\HeartBeatTimer - fpst\FPSFactor[0]
		EndIf
		me\HeartBeatVolume = Max(me\HeartBeatVolume - fpst\FPSFactor[0] * 0.05, 0.0)
	EndIf
	
	CatchErrors("MovePlayer")
End Function

Function MouseLook()
	Local i%
	
	me\CameraShake = Max(me\CameraShake - (fpst\FPSFactor[0] / 10.0), 0.0)
	
	CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / (Tan((2.0 * ATan(Tan((FOV) / 2.0) * (Float(RealGraphicWidth) / Float(RealGraphicHeight)))) / 2.0)))
	me\CurrCameraZoom = Max(me\CurrCameraZoom - fpst\FPSFactor[0], 0.0)
	
	If me\KillTimer >= 0.0 And me\FallTimer >= 0.0 Then
		me\HeadDropSpeed = 0.0
		
		Local Up# = (Sin(me\Shake) / (20.0 + me\CrouchState * 20.0)) * 0.6		
		Local Roll# = Max(Min(Sin(me\Shake / 2.0) * 2.5 * Min(me\Injuries + 0.25, 3.0), 8.0), -8.0)
		
		PositionEntity(Camera, EntityX(me\Collider) + Side, EntityY(me\Collider) + Up + 0.6 + me\CrouchState * -0.3, EntityZ(me\Collider))
		RotateEntity(Camera, 0.0, EntityYaw(me\Collider), Roll * 0.5)
		
		; ~ Update the smoothing que to smooth the movement of the mouse
		Mouse_X_Speed_1 = CurveValue(MouseXSpeed() * (MouseSensitivity + 0.6) , Mouse_X_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing) 
		If IsNaN(Mouse_X_Speed_1) Then Mouse_X_Speed_1 = 0.0
		If fpst\PrevFPSFactor > 0.0 Then
			If Abs(fpst\FPSFactor[0] / fpst\PrevFPSFactor - 1.0) > 1.0 Then
				; ~ Stop all camera movement
				Mouse_X_Speed_1 = 0.0
				Mouse_Y_Speed_1 = 0.0
			EndIf
		EndIf
		If InvertMouse Then
			Mouse_Y_Speed_1 = CurveValue(-MouseYSpeed() * (MouseSensitivity + 0.6), Mouse_Y_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing)
		Else
			Mouse_Y_Speed_1 = CurveValue(MouseYSpeed () * (MouseSensitivity + 0.6), Mouse_Y_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing)
		EndIf
		If IsNaN(Mouse_Y_Speed_1) Then Mouse_Y_Speed_1 = 0.0
		
		Local The_Yaw# = ((Mouse_X_Speed_1)) * Mouselook_X_Inc / (1.0 + wi\BallisticVest)
		Local The_Pitch# = ((Mouse_Y_Speed_1)) * Mouselook_Y_Inc / (1.0 + wi\BallisticVest)
		
		TurnEntity(me\Collider, 0.0, -The_Yaw, 0.0) ; ~ Turn the user on the Y (Yaw) axis
		CameraPitch = CameraPitch + The_Pitch
		; ~ Limit the user's camera To within 180.0 degrees of pitch rotation. Returns useless values so we need to use a variable to keep track of the camera pitch
		If CameraPitch > 70.0 Then CameraPitch = 70.0
		If CameraPitch < -70.0 Then CameraPitch = -70.0
		
		RotateEntity(Camera, WrapAngle(CameraPitch + Rnd(-me\CameraShake, me\CameraShake)), WrapAngle(EntityYaw(me\Collider) + Rnd(-me\CameraShake, me\CameraShake)), Roll) ; ~ Pitch the user's camera up and down
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
			If EntityY(me\Collider) < 2000.0 * RoomScale Lor EntityY(me\Collider) > 2608.0 * RoomScale Then
				RotateEntity(Camera, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), Roll + WrapAngle(Sin(MilliSecs() / 150.0) * 30.0)) ; ~ Pitch the user's camera up and down
			EndIf
		EndIf
	Else
		HideEntity(me\Collider)
		PositionEntity(Camera, EntityX(me\Head), EntityY(me\Head), EntityZ(me\Head))
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(me\Head)
			If CollisionY(me\Head, i) < EntityY(me\Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			me\HeadDropSpeed = 0.0
		Else
			If me\KillAnim = 0 Then 
				MoveEntity(me\Head, 0.0, 0.0, me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(-90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity(me\Head, 0.0, 0.0, -me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			me\HeadDropSpeed = me\HeadDropSpeed - (0.002 * fpst\FPSFactor[0])
		EndIf
		
		If InvertMouse Then
			TurnEntity(Camera, (-MouseYSpeed()) * 0.05 * fpst\FPSFactor[0], (-MouseXSpeed()) * 0.15 * fpst\FPSFactor[0], 0.0)
		Else
			TurnEntity(Camera, MouseYSpeed() * 0.05 * fpst\FPSFactor[0], (-MouseXSpeed()) * 0.15 * fpst\FPSFactor[0], 0.0)
		EndIf
	EndIf
	
	If ParticleAmount = 2 Then
		If Rand(35) = 1 Then
			Local Pvt% = CreatePivot()
			
			PositionEntity(Pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
			If Rand(2) = 1 Then
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			EndIf
			
			Local p.Particles = CreateParticle(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 2, 0.002, 0.0, 300.0)
			
			p\Speed = 0.001 : p\SizeChange = -0.00001
			RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
			FreeEntity(Pvt)
		EndIf
	EndIf
	
	; ~ Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop.
	If (MouseX() > Mouse_Right_Limit) Lor (MouseX() < Mouse_Left_Limit) Lor (MouseY() > Mouse_Bottom_Limit) Lor (MouseY() < Mouse_Top_Limit)
		MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	EndIf
	
	If wi\GasMask > 0 Lor I_1499\Using > 0 Then
		If I_714\Using = 0 Then
			If wi\GasMask = 2 Lor I_1499\Using = 2 Then
				me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.01 * fpst\FPSFactor[0])
			EndIf
		EndIf
		If me\KillTimer >= 0.0 Then
			If (Not ChannelPlaying(BreathCHN)) Then
				If (Not ChannelPlaying(BreathGasRelaxedCHN)) Then BreathGasRelaxedCHN = PlaySound_Strict(BreathGasRelaxedSFX)
			Else
				If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
			EndIf
		EndIf
		
		ShowEntity(tt\OverlayID[1])
		ShowEntity(tt\OverlayID[11])
		
		If ChannelPlaying(BreathCHN) Then
			wi\GasMaskFogTimer = Min(wi\GasMaskFogTimer + fpst\FPSFactor[0] * 2.0, 100.0)
		Else
			If wi\GasMask = 2 Lor I_1499\Using = 2 Then
				If me\CurrSpeed > 0.0 And KeyDown(key\SPRINT) Then
					wi\GasMaskFogTimer = Min(wi\GasMaskFogTimer + fpst\FPSFactor[0] * 0.2, 100.0)
				Else
					wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - fpst\FPSFactor[0] * 0.15)
				EndIf
			Else
				wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - fpst\FPSFactor[0] * 0.15)
			EndIf
		EndIf
		
		EntityAlpha(tt\OverlayID[11], Min(((wi\GasMaskFogTimer * 0.2) ^ 2.0) / 1000.0, 0.45))
	Else
		If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
		wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fpst\FPSFactor[0] * 0.15))
		HideEntity(tt\OverlayID[1])
		HideEntity(tt\OverlayID[11])
	EndIf
	
	If wi\HazmatSuit > 0 Then
		If wi\HazmatSuit = 1 Then
            me\Stamina = Min(60.0, me\Stamina)
        EndIf
		If I_714\Using = 0 Then
			If wi\HazmatSuit = 2 Then
				me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.01 * fpst\FPSFactor[0])
			EndIf
		EndIf
		
		ShowEntity(tt\OverlayID[2])
	Else
		HideEntity(tt\OverlayID[2])
	EndIf
	
	If wi\BallisticHelmet > 0 Then
        ShowEntity(tt\OverlayID[9])
    Else
        HideEntity(tt\OverlayID[9])
    EndIf
	
	If wi\NightVision > 0 Lor wi\SCRAMBLE > 0 Then
		ShowEntity(tt\OverlayID[4])
		If wi\NightVision = 2 Then
			EntityColor(tt\OverlayID[4], 0.0, 100.0, 255.0)
			AmbientLightRooms(15)
		ElseIf wi\NightVision = 3
			EntityColor(tt\OverlayID[4], 255.0, 0.0, 0.0)
			AmbientLightRooms(15)
		ElseIf wi\NightVision = 1
			EntityColor(tt\OverlayID[4], 0.0, 255.0, 0.0)
			AmbientLightRooms(15)
		Else
			EntityColor(tt\OverlayID[4], 128.0, 128.0, 128.0)
		EndIf
		EntityTexture(tt\OverlayID[0], tt\OverlayTextureID[5])
	Else
		AmbientLightRooms(0)
		HideEntity(tt\OverlayID[4])
		EntityTexture(tt\OverlayID[0], tt\OverlayTextureID[0])
	EndIf
	
	For i = 0 To 5
		If I_1025\State[i] > 0.0 Then
			Select i
				Case 0 ; ~ Common cold
					;[Block]
					If fpst\FPSFactor[0] > 0.0 Then 
						If Rand(1000) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							Else
								If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							EndIf
						EndIf
					EndIf
					me\Stamina = me\Stamina - (fpst\FPSFactor[0] * 0.3)
					;[End Block]
				Case 1 ; ~ Chicken pox
					;[Block]
					If Rand(9000) = 1 And msg\Msg = "" Then
						msg\Msg = "Your skin is feeling itchy."
						msg\Timer = 70.0 * 6.0
					EndIf
					;[End Block]
				Case 2 ; ~ Cancer of the lungs
					;[Block]
					If fpst\FPSFactor[0] > 0.0 Then 
						If Rand(800) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							Else
								If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							EndIf
						EndIf
					EndIf
					me\Stamina = me\Stamina - (fpst\FPSFactor[0] * 0.1)
					;[End Block]
				Case 3 ; ~ Appendicitis
					; ~ 0.035 / sec = 2.1 / min
					If I_427\Using = 0 And I_427\Timer < 70.0 * 360.0 Then
						I_1025\State[i] = I_1025\State[i] + (fpst\FPSFactor[0] * 0.0005)
					EndIf
					If I_1025\State[i] > 20.0 Then
						If I_1025\State[i] - fpst\FPSFactor[0] =< 20.0 Then msg\Msg = "The pain in your stomach is becoming unbearable." : msg\Timer = 70.0 * 6.0
						me\Stamina = me\Stamina - (fpst\FPSFactor[0] * 0.3)
					ElseIf I_1025\State[i] > 10.0
						If I_1025\State[i] - fpst\FPSFactor[0] =< 10.0 Then msg\Msg = "Your stomach is aching." : msg\Timer = 70.0 * 6.0
					EndIf
					;[End Block]
				Case 4 ; ~ Asthma
					;[Block]
					If me\Stamina < 35.0 Then
						If Rand(Int(140.0 + me\Stamina * 8.0)) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							Else
								If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							EndIf
						EndIf
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 10.0 + me\Stamina * 15.0)
					EndIf
					;[End Block]
				Case 5 ; ~ Cardiac arrest
					;[Block]
					If I_427\Using = 0 And I_427\Timer < 70.0 * 360.0 Then
						I_1025\State[i] = I_1025\State[i] + (fpst\FPSFactor[0] * 0.35)
					EndIf
					
					; ~ 35 / sec
					If I_1025\State[i] > 110.0 Then
						me\HeartBeatRate = 0.0
						me\BlurTimer = Max(me\BlurTimer, 500.0)
						If I_1025\State[i] > 140.0 Then 
							msg\DeathMsg = Chr(34) + "He died of a cardiac arrest after reading SCP-1025, that's for sure. Is there such a thing as psychosomatic cardiac arrest, or does SCP-1025 have some "
							msg\DeathMsg = msg\DeathMsg + "anomalous properties we are not yet aware of?" + Chr(34)
							Kill()
						EndIf
					Else
						me\HeartBeatRate = Max(me\HeartBeatRate, 70.0 + I_1025\State[i])
						me\HeartBeatVolume = 1.0
					EndIf
					;[End Block]
			End Select 
		EndIf
	Next
End Function

Const NAV_WIDTH% = 287
Const NAV_HEIGHT% = 256

Const INVENTORY_GFX_SIZE% = 70
Const INVENTORY_GFX_SPACING% = 35

Function DrawGUI()
	CatchErrors("Uncaught (DrawGUI)")
	
	Local Temp%, x%, y%, z%, i%, YawValue#, PitchValue#
	Local x1#, x2#, x3#, y1#, y2#, y3#, z2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$
	Local Width%, Height%
	Local e.Events, it.Items
	
	If MenuOpen Lor ConsoleOpen Lor SelectedDoor <> Null Lor InvOpen Lor OtherOpen <> Null Lor me\EndingTimer < 0.0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom Then
				If Float(e\EventStr) < 1000.0 Then
					If e\EventState > 600.0 Then
						If me\BlinkTimer < -3.0 And me\BlinkTimer > -10.0 Then
							If e\Img = 0 Then
								If me\BlinkTimer > -5.0 And Rand(30) = 1 Then
									PlaySound_Strict(DripSFX[Rand(0, 5)])
									If e\Img = 0 Then e\Img = LoadImage_Strict("GFX\npcs\scp_106_face.png")
								EndIf
							Else
								DrawImage(e\Img, GraphicWidth / 2 - Rand(390, 310), GraphicHeight / 2 - Rand(290, 310))
							EndIf
						Else
							If e\Img <> 0 Then FreeImage(e\Img) : e\Img = 0
						EndIf
						Exit
					EndIf
				Else
					If me\BlinkTimer < -3.0 And me\BlinkTimer > -10.0 Then
						If e\Img = 0 Then
							If me\BlinkTimer > -5.0 Then
								If e\Img = 0 Then
									e\Img = LoadImage_Strict("GFX\kneel_mortal.png")
									If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage(e\Img, GraphicWidth / 2 - Rand(390, 310), GraphicHeight / 2 - Rand(290, 310))
						EndIf
					Else
						If e\Img <> 0 Then FreeImage(e\Img) : e\Img = 0
						If me\BlinkTimer < -3.0 Then
							If (Not ChannelPlaying(e\SoundCHN)) Then e\SoundCHN = PlaySound_Strict(e\Sound)
						Else
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
						EndIf
					EndIf
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	If ClosestButton <> 0 And SelectedDoor = Null And (Not InvOpen) And (Not MenuOpen) And OtherOpen = Null And (Not ConsoleOpen) Then
		Temp = CreatePivot()
		PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
		PointEntity(Temp, ClosestButton)
		YawValue = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
		If YawValue > 90.0 And YawValue =< 180.0 Then YawValue = 90.0
		If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
		PitchValue = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
		If PitchValue > 90.0 And PitchValue =< 180.0 Then PitchValue = 90.0
		If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
		
		FreeEntity(Temp)
		
		DrawImage(tt\IconID[4], GraphicWidth / 2 + Sin(YawValue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(PitchValue) * (GraphicHeight / 3) - 32)
	EndIf
	
	If ClosestItem <> Null Then
		YawValue = -DeltaYaw(Camera, ClosestItem\Collider)
		If YawValue > 90.0 And YawValue =< 180.0 Then YawValue = 90.0
		If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
		PitchValue = -DeltaPitch(Camera, ClosestItem\Collider)
		If PitchValue > 90.0 And PitchValue =< 180.0 Then PitchValue = 90.0
		If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
		
		DrawImage(tt\IconID[5], GraphicWidth / 2 + Sin(YawValue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(PitchValue) * (GraphicHeight / 3) - 32)
	EndIf
	
	If DrawHandIcon Then DrawImage(tt\IconID[4], GraphicWidth / 2 - 32, GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon[i] Then
			x = GraphicWidth / 2 - 32
			y = GraphicHeight / 2 - 32		
			Select i
				Case 0
					;[Block]
					y = y - 64 - 5
					;[End Block]
				Case 1
					;[Block]
					x = x + 64 + 5
					;[End Block]
				Case 2
					;[Block]
					y = y + 64 + 5
					;[End Block]
				Case 3
					;[Block]
					x = x - 5 - 64
					;[End Block]
			End Select
			DrawImage(tt\IconID[4], x, y)
			Color(0, 0, 0)
			Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(ArrowIMG[i], x + 21, y + 21)
			DrawArrowIcon[i] = False
		EndIf
	Next
	
	If I_294\Using Then Use294()
	
	If HUDEnabled Then 
		Width = 204
		Height = 20
		x = 80
		y = GraphicHeight - 95
		
		Color(255, 255, 255)
		Rect(x, y, Width, Height, False)
		If BarStyle = 1 Then
			If me\BlinkTimer < 160.0 Then
		    	Color(100, 0, 0)
	    	Else
	        	Color(100, 100, 100)
	    	EndIf		
			Rect(x + 3, y + 3, Float(me\BlinkTimer * ((Width - 6.0) / me\BLINKFREQ)), 14)
		Else
			For i = 1 To Int(((Width - 2) * (me\BlinkTimer / (me\BLINKFREQ))) / 10.0)
				If me\BlinkTimer < 160.0 Then
					DrawImage(tt\ImageID[1], x + 3 + 10 * (i - 1), y + 3)
				Else
					DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
				EndIf
			Next	
		EndIf
		Color(0, 0, 0)
		Rect(x - 50, y, 30, 30)
		
		If me\BlurTimer > 550.0 Lor me\BlinkEffect > 1.0 Lor me\LightFlash > 0.0 Lor (((me\LightBlink > 0.0 And (Not chs\NoBlink)) Lor me\EyeIrritation > 0.0) And wi\NightVision = 0) Then
			Color(200, 0, 0)
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		Else
		    If me\BlinkEffect < 1.0 Lor chs\NoBlink Then
		        Color(0, 200, 0)
			    Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
            EndIf
		EndIf
		
		Color(255, 255, 255)
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		
		DrawImage(tt\IconID[3], x - 50, y)
		
		y = GraphicHeight - 55.0
		
		Color(255, 255, 255)
		Rect(x, y, Width, Height, False)
		If BarStyle = 1 Then
			If me\Stamina < 27.0 Then
		  	    Color(50, 0, 0)
	    	Else
	     	    Color(50, 50, 50)
	    	EndIf		
			Rect(x + 3, y + 3, Float(me\Stamina * (Width - 6.0) / 100.0), 14)	
		Else
			For i = 1 To Int(((Width - 2) * (me\Stamina / 100.0)) / 10.0)
				If me\Stamina < 27.0 Then
					DrawImage(tt\ImageID[3], x + 3 + 10 * (i - 1), y + 3)
				Else
					DrawImage(tt\ImageID[2], x + 3 + 10 * (i - 1), y + 3)
				EndIf
			Next
		EndIf
		Color(0, 0, 0)
		Rect(x - 50, y, 30, 30)
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor I_714\Using > 0 Lor me\Injuries >= 1.5 Lor me\StaminaEffect > 1.0 Lor wi\HazmatSuit = 1 Lor wi\BallisticVest = 2 Then
			Color(200, 0, 0)
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		Else
		    If chs\InfiniteStamina = True Lor me\StaminaEffect < 1.0 Lor wi\GasMask = 2 Lor I_1499\Using = 2 Lor wi\HazmatSuit = 2 Then
                Color(0, 200, 0)
			    Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
            EndIf 
		EndIf
		
		Color(255, 255, 255)
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		If me\Crouch Then
			DrawImage(tt\IconID[2], x - 50, y)
		ElseIf KeyDown(key\SPRINT) And me\CurrSpeed > 0.0 And (Not chs\NoClip) And me\Stamina > 0.0 Then
			DrawImage(tt\IconID[1], x - 50, y)
		Else
			DrawImage(tt\IconID[0], x - 50, y)
		EndIf
		
		If chs\DebugHUD Then
			Color(255, 255, 255)
			SetFont(fo\ConsoleFont)
			
			Text(x - 60, 40, "Room: " + PlayerRoom\RoomTemplate\Name)
            Text(x - 60, 60, "Room Coordinates: (" + Floor(EntityX(PlayerRoom\OBJ) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\OBJ) / 8.0 + 0.5) + ", Angle: " + PlayerRoom\Angle + ")")
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					Text(x - 60, 80, "Room Event: " + ev\EventName) 
					Text(x - 60, 100, "State: " + ev\EventState)
					Text(x - 60, 120, "State2: " + ev\EventState2)   
					Text(x - 60, 140, "State3: " + ev\EventState3)
					Text(x - 60, 160, "State4: " + ev\EventState4)
					Text(x - 60, 180, "Str: "+ ev\EventStr)
					Exit
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name = "dimension1499"
				Text(x - 60, 220, "Current Chunk X / Z: (" + (Int((EntityX(me\Collider) + 20) / 40)) + ", "+(Int((EntityZ(me\Collider) + 20) / 40)) + ")")
				
				Local CH_Amount% = 0
				
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				Text(x - 60, 240, "Current Chunk Amount: " + CH_Amount)
			Else
				Text(x - 60, 240, "Current Room Position: (" + PlayerRoom\x + ", " + PlayerRoom\y + ", " + PlayerRoom\z + ")")
			EndIf
			
			If SelectedMonitor <> Null Then
				Text(x - 60, 280, "Current Monitor: " + SelectedMonitor\ScrOBJ)
			Else
				Text(x - 60, 280, "Current Monitor: Null")
			EndIf
			
			Text(x - 60, 320, "Video memory: " + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB" + Chr(10))
			Text(x - 60, 340, "Global memory status: " + ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB")
			Text(x - 60, 360, "Triangles Rendered: " + CurrTrisAmount)
			Text(x - 60, 380, "Active Textures: " + ActiveTextures())	
			
			Text(x + 440, 40, "Player Position: (" + f2s(EntityX(me\Collider), 1) + ", " + f2s(EntityY(me\Collider), 1) + ", " + f2s(EntityZ(me\Collider), 1) + ")")
			Text(x + 440, 60, "Player Rotation: (" + f2s(EntityPitch(me\Collider), 1) + ", " + f2s(EntityYaw(me\Collider), 1) + ", " + f2s(EntityRoll(me\Collider), 1) + ")")
			
			Text(x + 440, 100, "Injuries: " + me\Injuries)
			Text(x + 440, 120, "Bloodloss: " + me\Bloodloss)
			Text(x + 440, 140, "Blur Timer: " + me\BlurTimer)
			Text(x + 440, 160, "Blink Timer: " + me\BlinkTimer)
			Text(x + 440, 180, "Stamina: " + me\Stamina)
			
			Text(x + 440, 220, "SCP-008 Infection: " + I_008\Timer)
			Text(x + 440, 240, "SCP-409 Crystallization: " + I_409\Timer)
			Text(x + 440, 260, "SCP-427 State (Secs): " + Int(I_427\Timer / 70.0))
			For i = 0 To 5
				Text(x + 440, 280 + (20 * i), "SCP-1025 State " + i + ": " + I_1025\State[i])
			Next
			
			SetFont(fo\FontID[0])
		EndIf
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage(SelectedScreen\Img, GraphicWidth / 2 - ImageWidth(SelectedScreen\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedScreen\Img) / 2)
		
		If MouseUp1 Lor MouseHit2 Then
			FreeImage(SelectedScreen\Img) : SelectedScreen\Img = 0
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If SelectedDoor <> Null Then
		If SelectedItem <> Null Then
			If SelectedItem\ItemTemplate\TempName = "scp005" Then 
				ShouldDrawHUD = False
			EndIf
		EndIf
		
		SelectedItem = Null
		If ShouldDrawHUD Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(ClosestButton, True), EntityY(ClosestButton, True), EntityZ(ClosestButton, True))
			RotateEntity(Pvt, 0.0, EntityYaw(ClosestButton, True) - 180.0, 0.0)
			MoveEntity(Pvt, 0.0, 0.0, 0.22)
			PositionEntity(Camera, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
			PointEntity(Camera, ClosestButton)
			FreeEntity(Pvt)
			
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) + MeshHeight(o\ButtonModelID[0]) * 0.015, EntityZ(ClosestButton, True))
			ProjY = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) - MeshHeight(o\ButtonModelID[0]) * 0.015, EntityZ(ClosestButton, True))
			Scale = (ProjectedY() - ProjY) / 462.0
			
			x = GraphicWidth / 2 - ImageWidth(tt\ImageID[4]) * Scale / 2
			y = GraphicHeight / 2 - ImageHeight(tt\ImageID[4]) * Scale / 2		
			
			SetFont(fo\FontID[2])
			If msg\KeypadMsg <> "" Then 
				If (msg\KeypadTimer Mod 70.0) < 35.0 Then Text(GraphicWidth / 2, y + 124 * Scale, msg\KeypadMsg, True, True)
			Else
				Text(GraphicWidth / 2, y + 70 * Scale, "ACCESS CODE: ", True, True)	
				SetFont(fo\FontID[3])
				Text(GraphicWidth / 2, y + 124 * Scale, msg\KeypadInput, True, True)
			EndIf
			
			x = x + 44 * Scale
			y = y + 249 * Scale
			
			If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
		EndIf
	EndIf
	
	Local PrevOtherOpen.Items
	Local OtherSize%, OtherAmount%
	Local IsEmpty%
	Local IsMouseOn%
	Local ClosedInv%
	
	If OtherOpen <> Null Then
		PrevOtherOpen = OtherOpen
		OtherSize = OtherOpen\InvSlots
		
		For i = 0 To OtherSize - 1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount + 1
			EndIf
		Next
		
		Local TempX% = 0
		
		x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - INVENTORY_GFX_SIZE * (Float(OtherSize) / MaxItemAmount * 2 - 1) - INVENTORY_GFX_SPACING
		
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					IsMouseOn = n
				EndIf
			EndIf
			
			If IsMouseOn = n Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			DrawFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (SelectedItem <> OtherOpen\SecondInv[n] Lor IsMouseOn = n) Then DrawImage(OtherOpen\SecondInv[n]\InvImg, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
			EndIf
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If IsMouseOn = n Then
					Color(255, 255, 255)	
					Text(x + INVENTORY_GFX_SIZE / 2, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15, OtherOpen\SecondInv[n]\ItemTemplate\Name, True)				
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			TempX = TempX + 1
			If TempX = 5 Then 
				TempX = 0
				y = y + INVENTORY_GFX_SIZE * 2 
				x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1.0)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			EndIf
		EndIf
		
		If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	ElseIf InvOpen Then
		x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					IsMouseOn = n
				EndIf
			EndIf
			
			If Inventory[n] <> Null Then
				Color(200, 200, 200)
				Select Inventory[n]\ItemTemplate\TempName 
					Case "gasmask"
						;[Block]
						If wi\GasMask = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "supergasmask"
						;[Block]
						If wi\GasMask = 2 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "gasmask3"
						;[Block]
						If wi\GasMask = 3 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "hazmatsuit"
						;[Block]
						If wi\HazmatSuit = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "hazmatsuit2"
						;[Block]
						If wi\HazmatSuit = 2 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "hazmatsuit3
						;[Block]"
						If wi\HazmatSuit = 3 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)	
						;[End Block]
					Case "vest"
						;[Block]
						If wi\BallisticVest = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "finevest"
						;[Block]
						If wi\BallisticVest = 2 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "helmet"
						;[Block]
						If wi\BallisticHelmet = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "scp714"
						;[Block]
						If I_714\Using = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "nvg"
						;[Block]
						If wi\NightVision = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "supernvg"
						;[Block]
						If wi\NightVision = 2 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "finenvg"
						;[Block]
						If wi\NightVision = 3 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "scramble"
						;[Block]
						If wi\SCRAMBLE = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "scp1499"
						;[Block]
						If I_1499\Using = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "super1499"
						;[Block]
						If I_1499\Using = 2 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "scp427"
						;[Block]
						If I_427\Using = 1 Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
				End Select
			EndIf
			
			If IsMouseOn = n Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			Color(255, 255, 255)
			DrawFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If Inventory[n] <> Null Then
				If (SelectedItem <> Inventory[n] Lor IsMouseOn = n) Then 
					DrawImage(Inventory[n]\InvImg, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
				EndIf
			EndIf
			
			If Inventory[n] <> Null And SelectedItem <> Inventory[n] Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						SetFont(fo\FontID[0])
						Color(0, 0, 0)
						Text(x + INVENTORY_GFX_SIZE / 2 + 1, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15 + 1, Inventory[n]\Name, True)							
						Color(255, 255, 255)	
						Text(x + INVENTORY_GFX_SIZE / 2, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15, Inventory[n]\Name, True)	
					EndIf
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If n = 4 Then 
				y = y + INVENTORY_GFX_SIZE * 2 
				x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> Inventory[MouseSlot]
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			EndIf
		EndIf
		
		If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	Else
		If SelectedItem <> Null Then
			Select SelectedItem\ItemTemplate\TempName
				Case "nvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						If wi\NightVision > 0 And wi\NightVision <> 1 Then
							Return
						Else
							DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
							
							Width = 300
							Height = 20
							x = GraphicWidth / 2 - Width / 2
							y = GraphicHeight / 2 + 80
							Rect(x, y, Width + 4, Height, False)
							If BarStyle = 1 Then
								Color(100, 100, 100)	
								Rect(x + 3, y + 3, Float(SelectedItem\State3 * (Width - 6.0) / 100.0), 14)	
							Else
								For i = 1 To Int((Width - 2) * (SelectedItem\State3 / 100.0) / 10.0)
									DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
								Next
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "supernvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						If wi\NightVision > 0 And wi\NightVision <> 2 Then
							Return
						Else
							DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
							
							Width = 300
							Height = 20
							x = GraphicWidth / 2 - Width / 2
							y = GraphicHeight / 2 + 80
							Rect(x, y, Width + 4, Height, False)
							If BarStyle = 1 Then
								Color(100, 100, 100)	
								Rect(x + 3, y + 3, Float(SelectedItem\State3 * (Width - 6.0) / 100.0), 14)	
							Else
								For i = 1 To Int((Width - 2) * (SelectedItem\State3 / 100.0) / 10.0)
									DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
								Next
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "finenvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						If wi\NightVision > 0 And wi\NightVision <> 3 Then
							Return
						Else
							DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
							
							Width = 300
							Height = 20
							x = GraphicWidth / 2 - Width / 2
							y = GraphicHeight / 2 + 80
							Rect(x, y, Width + 4, Height, False)
							If BarStyle = 1 Then
								Color(100, 100, 100)	
								Rect(x + 3, y + 3, Float(SelectedItem\State3 * (Width - 6.0) / 100.0), 14)	
							Else
								For i = 1 To Int((Width - 2) * (SelectedItem\State3 / 100.0) / 10.0)
									DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
								Next
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "25ct", "scp005", "key", "coin"
					;[Block]
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If me\Bloodloss = 0.0 And me\Injuries = 0.0 Then
						Return
					Else
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300.0
						Height = 20.0
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80
						Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
							Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						Select SelectedItem\ItemTemplate\Name
							Case "Burnt Note" 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\note_Maynard.png")
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(0, 0, 0)
								SetFont(fo\FontID[0])
								Text(277, 469, AccessCode, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Document SCP-372"
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
								
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(37, 45, 137)
								SetFont(fo\FontID[4])
								Temp = ((Int(AccessCode) * 3) Mod 10000)
								If Temp < 1000 Then Temp = Temp + 1000
								Text(383 * MenuScale, 734 * MenuScale, Temp, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Movie Ticket"
								;[Block]
								; ~ Don't resize because it messes up the masking
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								;[End Block]
							Default 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
								;[End Block]
						End Select
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025) 
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\1025\1025(" + Int(SelectedItem\State) + ").png")	
						SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					; ~ RadioState(5) = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState(6) = A timer for the "code channel"
					; ~ RadioState(7) = Another timer for the "code channel"
					
					StrTemp = ""
					
					x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img)
					y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img)
					
					DrawImage(SelectedItem\ItemTemplate\Img, x, y)
					
					If SelectedItem\State > 0.0 Then
						If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And CoffinDistance >= 8.0 Then
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									StrTemp = "        USER TRACK PLAYER - "
									If (Not EnableUserTracks) Then
										StrTemp = StrTemp + "NOT ENABLED     "
									ElseIf UserTrackMusicAmount < 1
										StrTemp = StrTemp + "NO TRACKS FOUND     "
									Else
										If ChannelPlaying(RadioCHN[0]) Then StrTemp = StrTemp + Upper(UserTrackName[RadioState[0]]) + "          "
									EndIf
									;[End Block]
								Case 1
									;[Block]
									StrTemp = "        WARNING - CONTAINMENT BREACH          "
									;[End Block]
								Case 2
									;[Block]
									StrTemp = "        SCP Foundation On-Site Radio          "
									;[End Block]
								Case 3
									;[Block]
									StrTemp = "             EMERGENCY CHANNEL - RESERVED FOR COMMUNICATION IN THE EVENT OF A CONTAINMENT BREACH         "
									;[End Block]
							End Select 
							
							x = x + 66.0
							y = y + 419.0
							
							Color(30, 30, 30)
							
							If SelectedItem\State =< 100.0 Then
								For i = 0 To 4
									Rect(x, y + 8 * i, 43 - i * 6, 4, Ceil(SelectedItem\State / 20.0) > 4.0 - i )
								Next
							EndIf	
							
							SetFont(fo\FontID[2])
							Text(x + 60, y, "CHN")	
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio" Then
								StrTemp = ""
								For i = 0 To Rand(5, 30)
									StrTemp = StrTemp + Chr(Rand(1, 100))
								Next
								
								SetFont(fo\FontID[3])
								Text(x + 97, y + 16.0, Rand(0, 9), True, True)
							Else
								SetFont(fo\FontID[3])
								Text(x + 97, y + 16, Int(SelectedItem\State2 + 1.0), True, True)
							EndIf
							
							SetFont(fo\FontID[2])
							If StrTemp <> "" Then
								StrTemp = Right(Left(StrTemp, (Int(MilliSecs() / 300) Mod Len(StrTemp))), 10)
								Text(x + 32, y + 33, StrTemp)
							EndIf
							SetFont(fo\FontID[0])
						EndIf
					EndIf
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If wi\BallisticVest = 0 Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80.0
						Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
					        Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					
					Width = 300
					Height = 20
					x = GraphicWidth / 2 - Width / 2
					y = GraphicHeight / 2 + 80.0
					Rect(x, y, Width + 4, Height, False)
					If BarStyle = 1 Then
						Color(100, 100, 100)	
						Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
					Else
						For i = 1 To Int((Width - 2.0) * (SelectedItem\State / 100.0) / 10.0)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If PreventItemOverlapping(True, False, False, False, False) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80
						Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
					        Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img) * 0.5 + 20.0
					y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img) * 0.4 - 85.0
					
					Local PlayerX%, PlayerZ%
					
					DrawImage(SelectedItem\ItemTemplate\Img, x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85)
					
					SetFont(fo\FontID[2])
					
					Local NavWorks% = True
					
					If PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor PlayerRoom\RoomTemplate\Name = "dimension1499" Then
						NavWorks = False
					ElseIf PlayerRoom\RoomTemplate\Name = "room860" Then
						For e.Events = Each Events
							If e\EventName = "room860" Then
								If e\EventState = 1.0 Then
									NavWorks = False
								EndIf
								Exit
							EndIf
						Next
					EndIf
					
					If (Not NavWorks) Then
						If (MilliSecs() Mod 1000) > 300 Then
							Color(200, 0, 0)
							Text(x, y + NAV_HEIGHT / 2 - 80, "ERROR 06", True)
							Text(x, y + NAV_HEIGHT / 2 - 60, "LOCATION UNKNOWN", True)						
						EndIf
					Else
						If SelectedItem\State > 0.0 And (Rnd(CoffinDistance + 15.0) > 1.0 Lor PlayerRoom\RoomTemplate\Name <> "room895") Then
							PlayerX = Floor((EntityX(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							PlayerZ = Floor((EntityZ(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							
							SetBuffer(ImageBuffer(tt\ImageID[12]))
							
							Local xx% = x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2
							Local yy% = y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85
							
							DrawImage(SelectedItem\ItemTemplate\Img, xx, yy)
							
							x = x - 12.0 + (((EntityX(me\Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							y = y + 12.0 - (((EntityZ(me\Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							For x2 = Max(0.0, PlayerX - 6.0) To Min(MapWidth, PlayerX + 6.0)
								For z2 = Max(0.0, PlayerZ - 6.0) To Min(MapHeight, PlayerZ + 6.0)
									If CoffinDistance > 16.0 Lor Rnd(16.0) < CoffinDistance Then 
										If MapTemp(x2, z2) > 0 And (MapFound(x2, z2) > 0 Lor SelectedItem\ItemTemplate\Name = "S-NAV 310 Navigator" Lor SelectedItem\ItemTemplate\Name = "S-NAV Navigator Ultimate") Then
											Local DrawX% = x + (PlayerX - 1 - x2) * 24 , DrawY% = y - (PlayerZ - 1 - z2) * 24
											
											If x2 + 1.0 =< MapWidth Then
												If MapTemp(x2 + 1, z2) = False
													DrawImage(tt\ImageID[10], DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(tt\ImageID[10], DrawX - 12, DrawY - 12)
											EndIf
											If x2 - 1.0 >= 0.0 Then
												If MapTemp(x2 - 1, z2) = False
													DrawImage(tt\ImageID[8], DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(tt\ImageID[8], DrawX - 12, DrawY - 12)
											EndIf
											If z2 - 1.0 >= 0.0 Then
												If MapTemp(x2, z2 - 1) = False
													DrawImage(tt\ImageID[7], DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(tt\ImageID[7], DrawX - 12, DrawY - 12)
											EndIf
											If z2 + 1.0 =< MapHeight Then
												If MapTemp(x2, z2 + 1) = False
													DrawImage(tt\ImageID[9], DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(tt\ImageID[9], DrawX - 12, DrawY - 12)
											EndIf
										EndIf
									EndIf
								Next
							Next
							
							SetBuffer(BackBuffer())
							DrawImageRect(tt\ImageID[12], xx + 80, yy + 70, xx + 80, yy + 70, 270, 230)
							Color(30, 30, 30)
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator" Then Color(100, 0, 0)
							Rect(xx + 80, yy + 70, 270, 230, False)
							
							x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img) * 0.5 + 20.0
							y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img) * 0.4 - 85.0
							
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator" Then 
								Color(100, 0, 0)
							Else
								Color(30, 30, 30)
							EndIf
							If (MilliSecs() Mod 1000.0) > 300.0 Then
								If SelectedItem\ItemTemplate\Name <> "S-NAV 310 Navigator" And SelectedItem\ItemTemplate\Name <> "S-NAV Navigator Ultimate" Then
									Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 10, "MAP DATABASE OFFLINE")
								EndIf
								
								YawValue = EntityYaw(me\Collider) - 90.0
								x1 = x + Cos(YawValue) * 6.0 : y1 = y - Sin(YawValue) * 6.0
								x2 = x + Cos(YawValue - 140.0) * 5.0 : y2 = y - Sin(YawValue - 140.0) * 5.0				
								x3 = x + Cos(YawValue + 140.0) * 5.0 : y3 = y - Sin(YawValue + 140.0) * 5.0
								
								Line(x1, y1, x2, y2)
								Line(x1, y1, x3, y3)
								Line(x2, y2, x3, y3)
							EndIf
							
							Local SCPs_Found% = 0, Dist#
							
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator Ultimate" And (MilliSecs() Mod 600.0) < 400.0 Then
								If Curr173 <> Null Then
									Dist = EntityDistance(Camera, Curr173\OBJ)
									If Dist < 8.0 * 4.0 Then
										Dist = Ceil(Dist / 8.0) * 8.0
										Color(100, 0, 0)
										Oval(x - Dist * 3, y - 7 - Dist * 3, Dist * 3 * 2, Dist * 3 * 2, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30, "SCP-173")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr106 <> Null Then
									Dist = EntityDistance(Camera, Curr106\OBJ)
									If Dist < 8.0 * 4.0 Then
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7 - Dist * 1.5, Dist * 3, Dist * 3, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20 * SCPs_Found), "SCP-106")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr096 <> Null Then 
									Dist = EntityDistance(Camera, Curr096\OBJ)
									If Dist < 8.0 * 4.0 Then
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7 - Dist * 1.5, Dist * 3, Dist * 3, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20 * SCPs_Found), "SCP-096")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								For np.NPCs = Each NPCs
									If np\NPCtype = NPCtype049
										Dist = EntityDistance(Camera, np\OBJ)
										If Dist < 8.0 * 4.0 Then
											If (Not np\HideFromNVG) Then
												Color(100, 0, 0)
												Oval(x - Dist * 1.5, y - 7 - Dist * 1.5, Dist * 3, Dist * 3, False)
												Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20 * SCPs_Found), "SCP-049")
												SCPs_Found = SCPs_Found + 1
											EndIf
										EndIf
										Exit
									EndIf
								Next
								If PlayerRoom\RoomTemplate\Name = "room895" Then
									If CoffinDistance < 8.0 Then
										Dist = Rnd(4.0, 8.0)
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7.0 - Dist * 1.5, Dist * 3.0, Dist * 3.0, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20 * SCPs_Found), "SCP-895")
									EndIf
								EndIf
							EndIf
							
							Color(30, 30, 30)
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator" Then Color(100, 0, 0)
							If SelectedItem\State =< 100.0 Then
								xTemp = x - NAV_WIDTH / 2.0 + 196.0
								yTemp = y - NAV_HEIGHT / 2.0 + 10.0
								Rect(xTemp, yTemp, 80, 20, False)
								
								For i = 1 To Ceil(SelectedItem\State / 10.0)
									DrawImage(tt\ImageID[11], xTemp + i * 8 - 6, yTemp + 4)
								Next
								SetFont(fo\FontID[2])
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					If PreventItemOverlapping(False, False, True, False, False) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300.0
						Height = 20.0
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80
						Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
					        Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "helmet"
					;[Block]
					If PreventItemOverlapping(False, False, False, True, False) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
					    Width = 300
					    Height = 20
					    x = GraphicWidth / 2 - Width / 2
					    y = GraphicHeight / 2 + 80
					    Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
					        Rect(x + 3, y + 3, Float(SelectedItem\State * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
				Case "scramble"
					;[Block]
					If PreventItemOverlapping(False, False, False, False, True) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80
						Rect(x, y, Width + 4, Height, False)
						If BarStyle = 1 Then
							Color(100, 100, 100)	
							Rect(x + 3, y + 3, Float(SelectedItem\State3 * (Width - 6.0) / 100.0), 14)	
						Else
							For i = 1 To Int((Width - 2) * (SelectedItem\State3 / 100.0) / 10.0)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
						EndIf
					EndIf
					;[End Block]
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\Img <> 0
					Local IN$ = SelectedItem\ItemTemplate\TempName
					
					If IN = "paper" Lor IN = "badge" Lor IN = "oldpaper" Lor IN = "ticket" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\ItemTemplate\Tempname
								
								If IN2 = "paper" Lor IN2 = "badge" Lor IN2 = "oldpaper" Lor IN2 = "ticket" Then
									If a_it\ItemTemplate\Img <> 0
										If a_it\ItemTemplate\Img <> SelectedItem\ItemTemplate\Img
											FreeImage(a_it\ItemTemplate\Img)
											a_it\ItemTemplate\Img = 0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf			
			EndIf
			
			If MouseHit2 Then
				IN = SelectedItem\ItemTemplate\TempName
				If IN = "scp1025" Then
					If SelectedItem\ItemTemplate\Img <> 0 Then FreeImage(SelectedItem\ItemTemplate\Img)
					SelectedItem\ItemTemplate\Img = 0
				EndIf
			EndIf
		EndIf		
	EndIf
	
	CatchErrors("DrawGUI")
End Function

Function UpdateGUI()
	CatchErrors("Uncaught (UpdateGUI)")
	
	Local Temp%, x%, y%, z%, i%
	Local x2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$, GroupDesignation$
	Local e.Events, it.Items, r.Rooms
	
	If ClosestButton <> 0 And SelectedDoor = Null And (Not InvOpen) And (Not MenuOpen) And OtherOpen = Null And (Not ConsoleOpen) Then
		Temp = CreatePivot()
		PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
		PointEntity(Temp, ClosestButton)
		FreeEntity(Temp)
		
		If MouseUp1 Then
			MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf me\Playable Then
					PlaySound2(ButtonSFX, Camera, ClosestButton)
					UseDoor(ClosestDoor, True)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If SelectedScreen <> Null Then
		If MouseUp1 Lor MouseHit2 Then
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If SelectedDoor <> Null Then
		If SelectedItem <> Null Then
			If SelectedItem\ItemTemplate\TempName = "scp005" Then 
				ShouldDrawHUD = False
				If SelectedDoor\Code <> "GEAR" Then
					SelectedDoor\Locked = 1					
					
					If SelectedDoor\Code = Str(AccessCode) Then
						GiveAchievement(AchvMaynard)
					ElseIf SelectedDoor\Code = "7816"
						GiveAchievement(AchvHarp)
				    ElseIf SelectedDoor\Code = "2411"
				        GiveAchievement(AchvO5)
					EndIf
					
					SelectedDoor\Locked = 0					
					UseDoor(SelectedDoor, True)
					SelectedDoor = Null
					PlaySound_Strict(ScannerSFX1)
					msg\Msg = "You hold the key close to the keypad."
					msg\Timer = 70.0 * 6.0
				Else
					SelectedDoor = Null
					PlaySound_Strict(ScannerSFX2)
					msg\Msg = "You hold the key close to the keypad but nothing happens."
					msg\Timer = 70.0 * 6.0
				EndIf
			EndIf
		EndIf
		
		SelectedItem = Null
		If ShouldDrawHUD Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(ClosestButton, True), EntityY(ClosestButton, True), EntityZ(ClosestButton, True))
			RotateEntity(Pvt, 0.0, EntityYaw(ClosestButton, True) - 180.0, 0.0)
			MoveEntity(Pvt, 0.0, 0.0, 0.22)
			PositionEntity(Camera, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
			PointEntity(Camera, ClosestButton)
			FreeEntity(Pvt)
			
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) + MeshHeight(o\ButtonModelID[0]) * 0.015, EntityZ(ClosestButton, True))
			ProjY = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) - MeshHeight(o\ButtonModelID[0]) * 0.015, EntityZ(ClosestButton, True))
			Scale = (ProjectedY() - ProjY) / 462.0
			
			x = GraphicWidth / 2 - ImageWidth(tt\ImageID[4]) * Scale / 2
			y = GraphicHeight / 2 - ImageHeight(tt\ImageID[4]) * Scale / 2		
			
			msg\Msg = ""
			msg\Timer = 0.0
			
			If msg\KeypadMsg <> "" Then 
				msg\KeypadTimer = msg\KeypadTimer - fpst\FPSFactor[1]
				If msg\KeypadTimer =< 0.0 Then
					msg\KeypadMsg = ""
					SelectedDoor = Null
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				EndIf
			EndIf
			
			x = x + 44 * Scale
			y = y + 249 * Scale
			
			For n = 0 To 3
				For i = 0 To 2
					xTemp = x + Int(58.5 * Scale * n)
					yTemp = y + (67.0 * Scale) * i
					
					Temp = False
					If MouseOn(xTemp, yTemp, 54 * Scale, 65 * Scale) And msg\KeypadMsg = "" Then
						If MouseUp1 Then 
							PlaySound_Strict(ButtonSFX)
							
							Select (n + 1) + (i * 4)
								Case 1, 2, 3
									;[Block]
									msg\KeypadInput = msg\KeypadInput + ((n + 1) + (i * 4))
									;[End Block]
								Case 4
									;[Block]
									msg\KeypadInput = msg\KeypadInput + "0"
									;[End Block]
								Case 5, 6, 7
									;[Block]
									msg\KeypadInput = msg\KeypadInput + ((n + 1) + (i * 4) - 1)
									;[End Block]
								Case 8
									;[Block]
									If msg\KeypadInput = SelectedDoor\Code Then
										PlaySound_Strict(ScannerSFX1)
										If SelectedDoor\Code = Str(AccessCode) Then
											GiveAchievement(AchvMaynard)
										ElseIf SelectedDoor\Code = "7816"
											GiveAchievement(AchvHarp)
										ElseIf SelectedDoor\Code = "2411"
										    GiveAchievement(AchvO5)
										EndIf									
										
										SelectedDoor\Locked = 0
										UseDoor(SelectedDoor, True)
										SelectedDoor = Null
										MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
									Else
										PlaySound_Strict(ScannerSFX2)
										msg\KeypadMsg = "ACCESS DENIED"
										msg\KeypadTimer = 210.0
										msg\KeypadInput = ""	
									EndIf
									;[End Block]
								Case 9, 10, 11
									;[Block]
									msg\KeypadInput = msg\KeypadInput + ((n + 1) + (i * 4) - 2)
									;[End Block]
								Case 12
									;[Block]
									msg\KeypadInput = ""
									;[End Block]
							End Select 
							If Len(msg\KeypadInput) > 4 Then msg\KeypadInput = Left(msg\KeypadInput, 4)
						EndIf
					Else
						Temp = False
					EndIf
				Next
			Next
			
			If MouseHit2 Then
				SelectedDoor = Null
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		msg\KeypadInput = ""
		msg\KeypadTimer = 0.0
		msg\KeypadMsg = ""
	EndIf
	
	If KeyHit(1) And me\EndingTimer = 0.0 Then
		If MenuOpen Lor InvOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
			DeleteMenuGadgets()
		Else
			PauseSounds()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMsg = 0
		
		SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
		If SelectedItem <> Null Then
			If Instr(SelectedItem\ItemTemplate\TempName, "vest") Lor Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Then
				If wi\BallisticVest = 0 And wi\HazmatSuit = 0 Then
					DropItem(SelectedItem)
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local PrevOtherOpen.Items
	Local OtherSize%, OtherAmount%
	Local IsEmpty%
	Local IsMouseOn%
	Local ClosedInv%
	
	If OtherOpen <> Null Then
		If PlayerRoom\RoomTemplate\Name = "gatea" Then
			HideEntity(tt\OverlayID[0])
			CameraFogRange(Camera, 5.0, 30.0)
			CameraRange(Camera, 0.01, 30.0)
		ElseIf PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale
			HideEntity(tt\OverlayID[0])
			CameraFogRange(Camera, 5.0, 45.0)
			CameraRange(Camera, 0.01, 60.0)
		EndIf
		
		PrevOtherOpen = OtherOpen
		OtherSize = OtherOpen\InvSlots
		
		For i = 0 To OtherSize - 1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount + 1
			EndIf
		Next
		
		InvOpen = False
		SelectedDoor = Null
		
		Local TempX% = 0
		
		x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - INVENTORY_GFX_SIZE * (Float(OtherSize) / MaxItemAmount * 2 - 1) - INVENTORY_GFX_SPACING
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					IsMouseOn = n
				EndIf
			EndIf
			
			If IsMouseOn = n Then
				MouseSlot = n
			EndIf
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							
							If DoubleClick And DoubleClickSlot = n Then
								If OtherOpen\SecondInv[n]\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[OtherOpen\SecondInv[n]\ItemTemplate\Sound])
								OtherOpen = Null
								ClosedInv = True
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And MouseHit1 Then
					For z = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			TempX = TempX + 1
			If TempX = 5 Then 
				TempX = 0
				y = y + INVENTORY_GFX_SIZE * 2 
				x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1.0)) / 2
			EndIf
		Next
		
		If MouseHit1 Then
			DoubleClickSlot = IsMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If (Not MouseDown1) Then
				If MouseSlot = 66 Then
					If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
					ShowEntity(SelectedItem\Collider)
					PositionEntity(SelectedItem\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\Collider, EntityPitch(Camera), EntityYaw(Camera), 0.0)
					MoveEntity(SelectedItem\Collider, 0.0, -0.1, 0.1)
					RotateEntity(SelectedItem\Collider, 0.0, Rnd(360.0), 0.0)
					ResetEntity(SelectedItem\Collider)
					SelectedItem\DropSpeed = 0.0
					SelectedItem\Picked = False
					For z = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					
					IsEmpty = True
					If OtherOpen\ItemTemplate\TempName = "wallet" Then
						If (Not IsEmpty) Then
							For z = 0 To OtherSize - 1
								If OtherOpen\SecondInv[z] <> Null Then
									Local Name$ = OtherOpen\SecondInv[z]\ItemTemplate\TempName
									
									If Name <> "25ct" And Name <> "coin" And Name <> "key" And Name <> "scp860" And Name <> "scp500pill" And Name <> "scp500pilldeath" And Name <> "scp005" Then
										IsEmpty = False
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						For z = 0 To OtherSize - 1
							If OtherOpen\SecondInv[z] <> Null
								IsEmpty = False
								Exit
							EndIf
						Next
					EndIf
					
					If IsEmpty Then
						Select OtherOpen\ItemTemplate\TempName
							Case "clipboard"
								;[Block]
								OtherOpen\InvImg = OtherOpen\ItemTemplate\InvImg2
								SetAnimTime(OtherOpen\Model, 17.0)
								;[End Block]
							Case "wallet"
								;[Block]
								SetAnimTime(OtherOpen\Model, 0.0)
								;[End Block]
						End Select
					EndIf
					
					SelectedItem = Null
					OtherOpen = Null
					ClosedInv = True
					
					MoveMouse(Viewport_Center_X, Viewport_Center_Y)
				Else
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then PrevOtherOpen\SecondInv[z] = Null
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						Select SelectedItem\ItemTemplate\TempName
							Default
								;[Block]
								msg\Msg = "You cannot combine these two items."
								msg\Timer = 70.0 * 6.0
								;[End Block]
						End Select					
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If ClosedInv And (Not InvOpen) Then 
			ResumeSounds() 
			OtherOpen = Null
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	ElseIf InvOpen Then
		If PlayerRoom\RoomTemplate\Name = "gatea" Then
			HideEntity(tt\OverlayID[0])
			CameraFogRange(Camera, 5.0, 30.0)
			CameraRange(Camera, 0.01, 30.0)
		ElseIf PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale
			HideEntity(tt\OverlayID[0])
			CameraFogRange(Camera, 5.0, 45.0)
			CameraRange(Camera, 0.01, 60.0)
		EndIf
		
		SelectedDoor = Null
		
		x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					IsMouseOn = n
				EndIf
			EndIf
			
			If IsMouseOn = n Then
				MouseSlot = n
			EndIf
			
			If Inventory[n] <> Null And SelectedItem <> Inventory[n] Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory[n]
							
							If DoubleClick And DoubleClickSlot = n Then
								If wi\HazmatSuit > 0 And Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") = 0 Then
									msg\Msg = "You cannot use any items while wearing a hazmat suit."
									msg\Timer = 70.0 * 6.0
									SelectedItem = Null
									Return
								EndIf
								If Inventory[n]\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[Inventory[n]\ItemTemplate\Sound])
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And MouseHit1 Then
					For z = 0 To MaxItemAmount - 1
						If Inventory[z] = SelectedItem Then Inventory[z] = Null
					Next
					Inventory[n] = SelectedItem
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If n = 4 Then 
				y = y + INVENTORY_GFX_SIZE * 2 
				x = GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If MouseHit1 Then
			DoubleClickSlot = IsMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If (Not MouseDown1) Then
				If MouseSlot = 66 Then
					Select SelectedItem\ItemTemplate\TempName
						Case "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
							;[Block]
							msg\Msg = "Double click on this item to take it off."
							msg\Timer = 70.0 * 6.0
							;[End Block]
						Case "scp1499", "super1499"
							;[Block]
							If I_1499\Using > 0 Then
								msg\Msg = "Double click on this item to take it off."
								msg\Timer = 70.0 * 6.0
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
							;[End Block]
						Case "gasmask", "gasmask3", "supergasmask"
                            ;[Block]
							If wi\GasMask > 0 Then
								msg\Msg = "Double click on this item to take it off."
								msg\Timer = 70.0 * 6.0
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block]
						Case "helmet"
                            ;[Block]
							If wi\BallisticHelmet > 0 Then
								msg\Msg = "Double click on this item to take it off."
								msg\Timer = 70.0 * 6.0
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block] 
						Case "nvg", "supernvg", "finenvg"
                            ;[Block]
							If wi\NightVision > 0 Then
								msg\Msg = "Double click on this item to take it off."
								msg\Timer = 70.0 * 6.0
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block]
						Case "scramble"
                            ;[Block]
							If wi\SCRAMBLE > 0 Then
								msg\Msg = "Double click on this item to take it off."
								msg\Timer = 70.0 * 6.0
							Else
								DropItem(SelectedItem)
								SelectedItem = Null
								InvOpen = False
							EndIf
						    ;[End Block]
						Default
							;[Block]
							DropItem(SelectedItem)
							SelectedItem = Null
							InvOpen = False
							;[End Block]
					End Select
					
					MoveMouse(Viewport_Center_X, Viewport_Center_Y)
				Else
					If Inventory[MouseSlot] = Null Then
						For z = 0 To MaxItemAmount - 1
							If Inventory[z] = SelectedItem Then Inventory[z] = Null
						Next
						Inventory[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf Inventory[MouseSlot] <> SelectedItem
						Select SelectedItem\ItemTemplate\TempName
							Case "paper", "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "misc", "oldpaper", "badge", "ticket", "25ct", "coin", "key", "scp860", "scp500pill", "scp500pilldeath", "scp005"
								;[Block]
								If Inventory[MouseSlot]\ItemTemplate\TempName = "clipboard" Then
									; ~ Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\ItemTemplate\TempName
									Local b2$ = SelectedItem\ItemTemplate\Name
									Local c%, ri%
									
									If (b <> "misc" And b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "scp005") Lor (b2 = "Playing Card" Lor b2 = "Mastercard") Then
										For c = 0 To Inventory[MouseSlot]\InvSlots - 1
											If Inventory[MouseSlot]\SecondInv[c] = Null Then
												If SelectedItem <> Null Then
													Inventory[MouseSlot]\SecondInv[c] = SelectedItem
													Inventory[MouseSlot]\State = 1.0
													SetAnimTime(Inventory[MouseSlot]\Model, 0.0)
													Inventory[MouseSlot]\InvImg = Inventory[MouseSlot]\ItemTemplate\InvImg
													
													For ri = 0 To MaxItemAmount - 1
														If Inventory[ri] = SelectedItem Then
															Inventory[ri] = Null
															PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											msg\Msg = "The paperclip is not strong enough to hold any more items."
										Else
											If added\ItemTemplate\TempName = "paper" Lor added\ItemTemplate\TempName = "oldpaper" Then
												msg\Msg = "This document was added to the clipboard."
											ElseIf added\ItemTemplate\TempName = "badge"
												msg\Msg = added\ItemTemplate\Name + " was added to the clipboard."
											Else
												msg\Msg = "The " + added\ItemTemplate\Name + " was added to the clipboard."
											EndIf
										EndIf
										msg\Timer = 70.0 * 6.0
									Else
										msg\Msg = "You cannot combine these two items."
										msg\Timer = 70.0 * 6.0
									EndIf
								ElseIf Inventory[MouseSlot]\ItemTemplate\TempName = "wallet" Then
									; ~ Add an item to clipboard
									added.Items = Null
									b = SelectedItem\ItemTemplate\TempName
									b2 = SelectedItem\ItemTemplate\Name
									If (b <> "misc" And b <> "paper" And b <> "oldpaper") Lor (b2 = "Playing Card" Lor b2 = "Mastercard") Then
										For c = 0 To Inventory[MouseSlot]\InvSlots - 1
											If Inventory[MouseSlot]\SecondInv[c] = Null Then
												If SelectedItem <> Null Then
													Inventory[MouseSlot]\SecondInv[c] = SelectedItem
													Inventory[MouseSlot]\State = 1.0
													If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "scp005"
														SetAnimTime(Inventory[MouseSlot]\Model, 3.0)
													EndIf
													Inventory[MouseSlot]\InvImg = Inventory[MouseSlot]\ItemTemplate\InvImg
													
													For ri = 0 To MaxItemAmount - 1
														If Inventory[ri] = SelectedItem Then
															Inventory[ri] = Null
															PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											msg\Msg = "The wallet is full."
										Else
											msg\Msg = "You put " + added\ItemTemplate\Name + " into the wallet."
										EndIf
										msg\Timer = 70.0 * 6.0
									Else
										msg\Msg = "You cannot combine these two items."
										msg\Timer = 70.0 * 6.0
									EndIf
								Else
									msg\Msg = "You cannot combine these two items."
									msg\Timer = 70.0 * 6.0
								EndIf
								SelectedItem = Null
								;[End Block]
							Case "bat"
								;[Block]
								Select Inventory[MouseSlot]\ItemTemplate\Name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										SelectedItem = Null
										Inventory[MouseSlot]\State = 100.0
										msg\Msg = "You replaced the navigator's battery."
										msg\Timer = 70.0 * 6.0
										;[End Block]
									Case "S-NAV Navigator Ultimate"
										;[Block]
										msg\Msg = "There seems to be no place for batteries in this navigator."
										msg\Timer = 70.0 * 6.0
										;[End Block]
									Case "Radio Transceiver"
										;[Block]
										Select Inventory[MouseSlot]\ItemTemplate\TempName 
											Case "fineradio", "veryfineradio"
												;[Block]
												msg\Msg = "There seems to be no place for batteries in this radio."
												msg\Timer = 70.0 * 6.0
												;[End Block]
											Case "18vradio"
												;[Block]
												msg\Msg = "The battery does not fit inside this radio."
												msg\Timer = 70.0 * 6.0
												;[End Block]
											Case "radio"
												;[Block]
												If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
												RemoveItem(SelectedItem)
												SelectedItem = Null
												Inventory[MouseSlot]\State = 100.0
												msg\Msg = "You replaced the radio's battery."
												msg\Timer = 70.0 * 6.0
												;[End Block]
										End Select
										;[End Block]
									Case "Night Vision Goggles"
										;[Block]
										Local NVName$ = Inventory[MouseSlot]\ItemTemplate\TempName
										
										If NVName = "nvg" Lor NVName = "supernvg" Then
											If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
											RemoveItem(SelectedItem)
											SelectedItem = Null
											Inventory[MouseSlot]\State = 1000.0
											msg\Msg = "You replaced the goggles' battery."
											msg\Timer = 70.0 * 6.0
										Else
											msg\Msg = "There seems to be no place for batteries in these night vision goggles."
											msg\Timer = 70.0 * 6.0
										EndIf
										;[End Block]
									Case "SCRAMBLE Gear"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										SelectedItem = Null
										Inventory[MouseSlot]\State = 1000.0
										msg\Msg = "You replaced the gear's battery."
										msg\Timer = 70.0 * 6.0
										;[End Block]
									Default
										;[Block]
										msg\Msg = "You cannot combine these two items."
										msg\Timer = 70.0 * 6.0
										;[End Block]
								End Select
								;[End Block]
							Case "18vbat"
								;[Block]
								Select Inventory[MouseSlot]\ItemTemplate\Name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										;[Block]
										msg\Msg = "The battery does not fit inside this navigator."
										msg\Timer = 70.0 * 6.0
										;[End Block]
									Case "S-NAV Navigator Ultimate"
										;[Block]
										msg\Msg = "There seems to be no place for batteries in this navigator."
										msg\Timer = 70.0 * 6.0
										;[End Block]
									Case "Radio Transceiver"
										;[Block]
										Select Inventory[MouseSlot]\ItemTemplate\TempName 
											Case "fineradio", "veryfineradio"
												;[Block]
												msg\Msg = "There seems to be no place for batteries in this radio."
												msg\Timer = 70.0 * 6.0	
												;[End Block]
											Case "18vradio"
												;[Block]
												If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
												RemoveItem(SelectedItem)
												SelectedItem = Null
												Inventory[MouseSlot]\State = 100.0
												msg\Msg = "You replaced the radio's battery."
												msg\Timer = 70.0 * 6.0
												;[End Block]
										End Select 
										;[End Block]
									Default
										;[Block]
										msg\Msg = "You cannot combine these two items."
										msg\Timer = 70.0 * 6.0	
										;[End Block]
								End Select
								;[End Block]
							Default
								;[Block]
								msg\Msg = "You cannot combine these two items."
								msg\Timer = 70.0 * 6.0
								;[End Block]
						End Select					
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If (Not InvOpen) Then 
			ResumeSounds() 
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	Else
		If SelectedItem <> Null Then
			Select SelectedItem\ItemTemplate\TempName
				Case "nvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						; ~ A hacky fix for a fog
						If wi\NightVision > 0 And wi\NightVision <> 1 Then
							msg\Msg = "You can't use two pairs of the goggles at the same time."
							msg\Timer = 70 * 5.0
							SelectedItem = Null
							Return
						Else
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
							
							SelectedItem\State3 = Min(SelectedItem\State3 + (fpst\FPSFactor[0] / 1.6), 100.0)
							
							If SelectedItem\State3 = 100.0 Then
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								
								If wi\NightVision = 1 Then
									msg\Msg = "You removed the goggles."
									CameraFogFar = StoredCameraFogFar
								Else
									msg\Msg = "You put on the goggles."
									wi\NightVision = 0
									StoredCameraFogFar = CameraFogFar
									CameraFogFar = 30.0
								EndIf
								wi\NightVision = (Not wi\NightVision)
								SelectedItem\State3 = 0.0
								msg\Timer = 70.0 * 6.0
								SelectedItem = Null
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "supernvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						If wi\NightVision > 0 And wi\NightVision <> 2 Then
							msg\Msg = "You can't use two pairs of the goggles at the same time."
							msg\Timer = 70 * 5.0
							SelectedItem = Null
							Return
						Else
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
							
							SelectedItem\State3 = Min(SelectedItem\State3 + (fpst\FPSFactor[0] / 1.6), 100.0)
							
							If SelectedItem\State3 = 100.0 Then
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								
								If wi\NightVision = 2 Then
									msg\Msg = "You removed the goggles."
									CameraFogFar = StoredCameraFogFar
								Else
									msg\Msg = "You put on the goggles."
									wi\NightVision = 0
									StoredCameraFogFar = CameraFogFar
									CameraFogFar = 30.0
								EndIf
								wi\NightVision = (Not wi\NightVision) * 2
								SelectedItem\State3 = 0.0
								msg\Timer = 70.0 * 6.0
								SelectedItem = Null
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "finenvg"
					;[Block]
					If PreventItemOverlapping(False, True, False, False, False) Then
						If wi\NightVision > 0 And wi\NightVision <> 3 Then
							msg\Msg = "You can't use two pairs of the goggles at the same time."
							msg\Timer = 70 * 5.0
							SelectedItem = Null
							Return
						Else
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
							
							SelectedItem\State3 = Min(SelectedItem\State3 + (fpst\FPSFactor[0] / 1.6), 100.0)
							
							If SelectedItem\State3 = 100.0 Then
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								
								If wi\NightVision = 3 Then
									msg\Msg = "You removed the goggles."
									CameraFogFar = StoredCameraFogFar
								Else
									msg\Msg = "You put on the goggles."
									wi\NightVision = 0
									StoredCameraFogFar = CameraFogFar
									CameraFogFar = 30.0
								EndIf
								wi\NightVision = (Not wi\NightVision) * 3
								SelectedItem\State3 = 0.0
								msg\Timer = 70.0 * 6.0
								SelectedItem = Null
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1123"
					;[Block]
					If I_714\Using = 0 And wi\GasMask < 3 And wi\HazmatSuit < 3 Then
						If PlayerRoom\RoomTemplate\Name <> "room1123" Then
							ShowEntity(tt\OverlayID[7])
							me\LightFlash = 7.0
							PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))	
							
							If Rand(2) = 1 Then
								GroupDesignation = "Nine-Tailed Fox"
							Else
								GroupDesignation = "See No Evil"
							EndIf
							msg\DeathMsg = SubjectName + " was shot dead after attempting to attack a member of " + GroupDesignation + ". Surveillance tapes show that the subject had been "
							msg\DeathMsg = msg\DeathMsg + "wandering around the site approximately 9 (nine) minutes prior, shouting the phrase " + Chr(34) + "get rid of the four pests" + Chr(34)
							msg\DeathMsg = msg\DeathMsg + " in chinese. SCP-1123 was found in [DATA REDACTED] nearby, suggesting the subject had come into physical contact with it. How "
							msg\DeathMsg = msg\DeathMsg + "exactly SCP-1123 was removed from its containment chamber is still unknown."
							Kill()
							Return
						EndIf
						For e.Events = Each Events
							If e\EventName = "room1123" Then 
								If e\EventState = 0.0 Then
									ShowEntity(tt\OverlayID[7])
									me\LightFlash = 3.0
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
								EndIf
								e\EventState = Max(1.0, e\EventState)
								Exit
							EndIf
						Next
					EndIf
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell.ogg"))
					
					If Curr513_1 = Null Then
						Curr513_1 = CreateNPC(NPCtype513_1, 0.0, 0.0, 0.0)
					EndIf	
					SelectedItem = Null
					;[End Block]
				Case "scp500pill"
					;[Block]
					If CanUseItem(False, True) Then
						GiveAchievement(Achv500)
						
						If I_008\Timer > 0.0 Then
							msg\Msg = "You swallowed the pill. Your nausea is fading."
						ElseIf I_409\Timer > 0.0 Then
						    msg\Msg = "You swallowed the pill. Your body is getting warmer and the crystals are receding."
						Else
							msg\Msg = "You swallowed the pill."
						EndIf
						msg\Timer = 70.0 * 6.0
						
						I_008\Timer = 0.0
						I_409\Timer = 0.0
						
						me\DeathTimer = 0.0
						me\Stamina = 100.0
						
						For i = 0 To 5
							I_1025\State[i] = 0.0
						Next
						
						If me\StaminaEffect > 1.0 Then
							me\StaminaEffect = 1.0
							me\StaminaEffectTimer = 0.0
						EndIf
						
						If me\BlinkEffect > 1.0 Then
							me\BlinkEffect = 1.0
							me\BlinkEffectTimer = 0.0
						EndIf
						
						For e.Events = Each Events
							If e\EventName = "1048a" Then
								If e\EventState2 > 0.0 Then
									msg\Msg = "You swallowed the pill. Ear-like organs are falling from your body."
									msg\Timer = 70.0 * 6.0
									
									If PlayerRoom = e\room Then me\BlinkTimer = -10.0
									If e\room\Objects[0] <> 0 Then
										FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
									EndIf
									RemoveEvent(e)
								EndIf
							EndIf
						Next
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If CanUseItem(False, True) Then
						Select Rand(5)
							Case 1
								;[Block]
								me\Injuries = 3.5
								msg\Msg = "You started bleeding heavily."
								msg\Timer = 70.0 * 6.0
								;[End Block]
							Case 2
								;[Block]
								me\Injuries = 0.0
								me\Bloodloss = 0.0
								msg\Msg = "Your wounds are healing up rapidly."
								msg\Timer = 70.0 * 6.0
								;[End Block]
							Case 3
								;[Block]
								me\Injuries = Max(0.0, me\Injuries - Rnd(0.5, 3.5))
								me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 100.0))
								msg\Msg = "You feel much better."
								msg\Timer = 70.0 * 6.0
								;[End Block]
							Case 4
								;[Block]
								me\BlurTimer = 10000.0
								me\Bloodloss = 0.0
								msg\Msg = "You feel nauseated."
								msg\Timer = 70.0 * 6.0
								;[End Block]
							Case 5
								;[Block]
								me\BlinkTimer = -10.0
								
								Local RoomName$ = PlayerRoom\RoomTemplate\Name
								
								If RoomName = "dimension1499" Lor RoomName = "gatea" Lor (RoomName = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale)
									me\Injuries = 2.5
									msg\Msg = "You started bleeding heavily."
									msg\Timer = 70.0 * 6.0
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "pocketdimension" Then
											PositionEntity(me\Collider, EntityX(r\OBJ), 0.8, EntityZ(r\OBJ))		
											ResetEntity(me\Collider)									
											UpdateDoors()
											UpdateRooms()
											PlaySound_Strict(Use914SFX)
											me\DropSpeed = 0.0
											Curr106\State = -2500.0
											Exit
										EndIf
									Next
									msg\Msg = "For some inexplicable reason, you find yourself inside the pocket dimension."
									msg\Timer = 70.0 * 6.0
								EndIf
								;[End Block]
						End Select
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If me\Bloodloss = 0.0 And me\Injuries = 0.0 Then
						msg\Msg = "You do not need to use a first aid right now."
						msg\Timer = 70.0 * 6.0
						SelectedItem = Null
						Return
					Else
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						If (Not me\Crouch) Then SetCrouch(True)
						
						SelectedItem\State = Min(SelectedItem\State + (fpst\FPSFactor[0] / 5.0), 100.0)			
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\TempName = "finefirstaid" Then
								me\Bloodloss = 0.0
								me\Injuries = Max(0.0, me\Injuries - 2.0)
								If me\Injuries = 0.0 Then
									msg\Msg = "You bandaged the wounds and took a painkiller. You feel fine."
								ElseIf me\Injuries > 1.0
									msg\Msg = "You bandaged the wounds and took a painkiller, but you were not able to stop the bleeding."
								Else
									msg\Msg = "You bandaged the wounds and took a painkiller, but you still feel sore."
								EndIf
								msg\Timer = 70.0 * 6.0
								RemoveItem(SelectedItem)
								SelectedItem = Null
							Else
								me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 20.0))
								If me\Injuries >= 2.5 Then
									msg\Msg = "The wounds were way too severe to staunch the bleeding completely."
									me\Injuries = Max(2.5, me\Injuries - Rnd(0.3, 0.7))
								ElseIf me\Injuries > 1.0
									me\Injuries = Max(0.5, me\Injuries - Rnd(0.5, 1.0))
									If me\Injuries > 1.0 Then
										msg\Msg = "You bandaged the wounds but were unable to staunch the bleeding completely."
									Else
										msg\Msg = "You managed to stop the bleeding."
									EndIf
								Else
									If me\Injuries > 0.5 Then
										me\Injuries = 0.5
										msg\Msg = "You took a painkiller, easing the pain slightly."
									Else
										me\Injuries = 0.5
										msg\Msg = "You took a painkiller, but it still hurts to walk."
									EndIf
								EndIf
								
								If SelectedItem\ItemTemplate\TempName = "firstaid2" Then 
									Select Rand(6)
										Case 1
											;[Block]
											chs\SuperMan = True
											msg\Msg = "You have becomed overwhelmedwithadrenalineholyshitWOOOOOO~!"
											;[End Block]
										Case 2
											;[Block]
											InvertMouse = (Not InvertMouse)
											msg\Msg = "You suddenly find it very difficult to turn your head."
											;[End Block]
										Case 3
											;[Block]
											me\BlurTimer = 5000.0
											msg\Msg = "You feel nauseated."
											;[End Block]
										Case 4
											;[Block]
											me\BlinkEffect = 0.6
											me\BlinkEffectTimer = Rnd(20.0, 30.0)
											;[End Block]
										Case 5
											;[Block]
											me\Bloodloss = 0.0
											me\Injuries = 0.0
											msg\Msg = "You bandaged the wounds. The bleeding stopped completely and you feel fine."
											;[End Block]
										Case 6
											;[Block]
											msg\Msg = "You bandaged the wounds and blood started pouring heavily through the bandages."
											me\Injuries = 3.5
											;[End Block]
									End Select
								EndIf
								msg\Timer = 70.0 * 6.0
								RemoveItem(SelectedItem)
								SelectedItem = Null
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.6
						me\BlinkEffectTimer = Rnd(20.0, 30.0)
						me\BlurTimer = 200.0
						
						msg\Msg = "You used the eyedrops. Your eyes feel moisturized."
				        msg\Timer = 70.0 * 6.0
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.4
						me\BlinkEffectTimer = Rnd(30.0, 40.0)
						me\Bloodloss = Max(me\Bloodloss - 1.0, 0.0)
						me\BlurTimer = 200.0
						
						msg\Msg = "You used the eyedrops. Your eyes feel very moisturized."
					    msg\Timer = 70.0 * 6.0
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.0
						me\BlinkEffectTimer = 60.0
						me\EyeStuck = 10000.0
						me\BlurTimer = 1000.0
						
						msg\Msg = "You used the eyedrops. Your eyes feel very moisturized."
					    msg\Timer = 70.0 * 6.0
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						Select SelectedItem\ItemTemplate\Name
							Case "Movie Ticket"
								;[Block]
								If (SelectedItem\State = 0.0) Then
									msg\Msg = Chr(34) + "Hey, I remember this movie!" + Chr(34)
									msg\Timer = 70.0 * 10.0
									PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(1, 5) + ".ogg"))
									SelectedItem\State = 1.0
								EndIf
								;[End Block]
						End Select
					EndIf
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025) 
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\State = Rand(0.0, 5.0)
					EndIf
					
					If I_714\Using = 0 And wi\GasMask < 3 And wi\HazmatSuit < 3 Then I_1025\State[SelectedItem\State] = Max(1.0, I_1025\State[SelectedItem\State])
					;[End Block]
				Case "cup"
					;[Block]
					If CanUseItem(False, True) Then
						SelectedItem\Name = Trim(Lower(SelectedItem\Name))
						If Left(SelectedItem\Name, Min(6, Len(SelectedItem\Name))) = "cup of" Then
							SelectedItem\Name = Right(SelectedItem\Name, Len(SelectedItem\Name) - 7)
						ElseIf Left(SelectedItem\Name, Min(8, Len(SelectedItem\Name))) = "a cup of" 
							SelectedItem\Name = Right(SelectedItem\Name, Len(SelectedItem\Name) - 9)
						EndIf
						
						; ~ The state of refined items is more than 1.0 (fine setting increases it by 1, very fine doubles it)
						x2 = (SelectedItem\State + 1.0)
						
						Local INIStr$ = "Data\SCP-294.ini"
						Local Loc% = GetINISectionLocation(INIStr, SelectedItem\Name)
						
						StrTemp = GetINIString2(INIStr, Loc, "message")
						If StrTemp <> "" Then msg\Msg = StrTemp : msg\Timer = 70.0 * 6.0
						
						If GetINIInt2(INIStr, Loc, "lethal") Lor GetINIInt2(INIStr, Loc, "deathtimer") Then 
							msg\DeathMsg = GetINIString2(INIStr, Loc, "deathmessage")
							If GetINIInt2(INIStr, Loc, "lethal") Then Kill()
						EndIf
						me\BlurTimer = GetINIInt2(INIStr, Loc, "blur") * 70.0
						If me\VomitTimer = 0.0 Then me\VomitTimer = GetINIInt2(INIStr, Loc, "vomit")
						me\CameraShakeTimer = GetINIString2(INIStr, Loc, "camerashake")
						me\Injuries = Max(me\Injuries + GetINIInt2(INIStr, Loc, "damage"), 0.0)
						me\Bloodloss = Max(me\Bloodloss + GetINIInt2(INIStr, Loc, "blood loss"), 0.0)
						StrTemp =  GetINIString2(INIStr, Loc, "sound")
						If StrTemp <> "" Then
							PlaySound_Strict(LoadTempSound(StrTemp))
						EndIf
						If GetINIInt2(INIStr, Loc, "stomachache") Then I_1025\State[3] = 1.0
						
						If GetINIInt2(INIStr, Loc, "infection") Then I_008\Timer = 1.0
						
						If GetINIInt2(INIStr, Loc, "crystallization") Then I_409\Timer = 1.0
						
						me\DeathTimer = GetINIInt2(INIStr, Loc, "deathtimer") * 70.0
						
						me\BlinkEffect = Float(GetINIString2(INIStr, Loc, "blink effect", 1.0)) * x2
						me\BlinkEffectTimer = Float(GetINIString2(INIStr, Loc, "blink effect timer", 1.0)) * x2
						
						me\StaminaEffect = Float(GetINIString2(INIStr, Loc, "stamina effect", 1.0)) * x2
						me\StaminaEffectTimer = Float(GetINIString2(INIStr, Loc, "stamina effect timer", 1.0)) * x2
						
						StrTemp = GetINIString2(INIStr, Loc, "refusemessage")
						If StrTemp <> "" Then
							msg\Msg = StrTemp 
							msg\Timer = 70.0 * 6.0		
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0.0, 0.0, 0.0)
							it\Picked = True
							For i = 0 To MaxItemAmount - 1
								If Inventory[i] = SelectedItem Then Inventory[i] = it : Exit
							Next					
							EntityType(it\Collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)
							SelectedItem = Null
						EndIf
						
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "bat", "18vbat"
					;[Block]
					; ~ A hacky fix for weird selecting
					SelectedItem = Null
					;[End Block]
				Case "syringe"
					;[Block]
					me\HealTimer = 30.0
					me\StaminaEffect = 0.5
					me\StaminaEffectTimer = 20.0
					
					msg\Msg = "You injected yourself with the syringe and feel a slight adrenaline rush."
					msg\Timer = 70.0 * 6.0
					
					RemoveItem(SelectedItem)
					SelectedItem = Null
					;[End Block]
				Case "finesyringe"
					;[Block]
					me\HealTimer = Rnd(20.0, 40.0)
					me\StaminaEffect = Rnd(0.5, 0.8)
					me\StaminaEffectTimer = Rnd(20.0, 30.0)
					
					msg\Msg = "You injected yourself with the syringe and feel an adrenaline rush."
					msg\Timer = 70.0 * 6.0
					
					RemoveItem(SelectedItem)
					SelectedItem = Null
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					Select Rand(3)
						Case 1
							;[Block]
							me\HealTimer = Rnd(40.0, 60.0)
							me\StaminaEffect = 0.1
							me\StaminaEffectTimer = 30.0
							msg\Msg = "You injected yourself with the syringe and feel a huge adrenaline rush."
							;[End Block]
						Case 2
							;[Block]
							chs\SuperMan = True
							msg\Msg = "You injected yourself with the syringe and feel a humongous adrenaline rush."
							;[End Block]
						Case 3
							;[Block]
							me\VomitTimer = 30.0
							msg\Msg = "You injected yourself with the syringe and feel a pain in your stomach."
							;[End Block]
					End Select
					
					msg\Timer = 70.0 * 6.0
					RemoveItem(SelectedItem)
					SelectedItem = Null
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					If SelectedItem\State =< 100.0 Then SelectedItem\State = Max(0.0, SelectedItem\State - fpst\FPSFactor[0] * 0.004)
					
					; ~ RadioState(5) = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState(6) = A timer for the "code channel"
					; ~ RadioState(7) = Another timer for the "code channel"
					
					If RadioState[5] = 0.0 Then 
						msg\Msg = "Use the numbered keys 1 through 5 to cycle between various channels."
						msg\Timer = 70.0 * 6.0
						RadioState[5] = 1.0
						RadioState[0] = -1.0
					EndIf
					
					x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img)
					y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img)
					
					If SelectedItem\State > 0.0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
							ResumeChannel(RadioCHN[5])
							If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic)	
						ElseIf CoffinDistance < 8.0
							If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic895)	
						Else
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									ResumeChannel(RadioCHN[0])
									If (Not EnableUserTracks) Then
										If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
									ElseIf UserTrackMusicAmount < 1
										If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
									Else
										If (Not ChannelPlaying(RadioCHN[0])) Then
											If (Not UserTrackFlag) Then
												If UserTrackMode
													If RadioState[0] < (UserTrackMusicAmount - 1)
														RadioState[0] = RadioState[0] + 1.0
													Else
														RadioState[0] = 0.0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0.0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										Else
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict(RadioSquelch)
											If (Not UserTrackFlag) Then
												If UserTrackMode Then
													If RadioState[0] < (UserTrackMusicAmount - 1)
														RadioState[0] = RadioState[0] + 1.0
													Else
														RadioState[0] = 0.0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0.0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										EndIf
									EndIf
									;[End Block]
								Case 1
									;[Block]
									ResumeChannel(RadioCHN[1])
									If (Not ChannelPlaying(RadioCHN[1])) Then
										If RadioState[1] >= 5.0 Then
											RadioCHN[1] = PlaySound_Strict(RadioSFX(1, 1))	
											RadioState[1] = 0.0
										Else
											RadioState[1] = RadioState[1] + 1.0	
											RadioCHN[1] = PlaySound_Strict(RadioSFX(1, 0))	
										EndIf
									EndIf
									;[End Block]
								Case 2
									;[Block]
									ResumeChannel(RadioCHN[2])
									If (Not ChannelPlaying(RadioCHN[2])) Then
										RadioState[2] = RadioState[2] + 1.0
										If RadioState[2] = 17.0 Then RadioState[2] = 1.0
										If Floor(RadioState[2] / 2.0) = Ceil(RadioState[2] / 2.0) Then
											RadioCHN[2] = PlaySound_Strict(RadioSFX(2, Int(RadioState[2] / 2.0)))	
										Else
											RadioCHN[2] = PlaySound_Strict(RadioSFX(2, 0))
										EndIf
									EndIf 
									;[End Block]
								Case 3
									;[Block]
									ResumeChannel(RadioCHN[3])
									If (Not ChannelPlaying(RadioCHN[3])) Then RadioCHN[3] = PlaySound_Strict(RadioStatic)
									
									If MTFTimer > 0.0 Then 
										RadioState[3] = RadioState[3] + Max(Rand(-10, 1), 0.0)
										Select RadioState[3]
											Case 40
												;[Block]
												If (Not RadioState3[0]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState3[0] = True	
												EndIf	
												;[End Block]
											Case 400
												;[Block]
												If (Not RadioState3[1]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState3[1] = True	
												EndIf	
												;[End Block]
											Case 800
												;[Block]
												If (Not RadioState3[2]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState3[2] = True
												EndIf		
												;[End Block]
											Case 1200
												;[Block]
												If (Not RadioState3[3]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState[3] = RadioState[3] + 1.0	
													RadioState3[3] = True
												EndIf
												;[End Block]
											Case 1600
												;[Block]
												If (Not RadioState3[4]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState3[4] = True
												EndIf
												;[End Block]
											Case 2000
												;[Block]
												If (Not RadioState3[5]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState3[5] = True
												EndIf
												;[End Block]
											Case 2400
												;[Block]
												If (Not RadioState3[6]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState3[6] = True
												EndIf
												;[End Block]
										End Select
									EndIf
									;[End Block]
								Case 4
									;[Block]
									ResumeChannel(RadioCHN[6])
									If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN[4])
									If (Not ChannelPlaying(RadioCHN[4])) Then 
										If RemoteDoorOn = False And RadioState[8] = False Then
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter3.ogg"))	
											RadioState[8] = True
										Else
											RadioState[4] = RadioState[4] + Max(Rand(-10, 1), 0.0)
											
											Select RadioState[4]
												Case 10
													;[Block]
													If (Not Curr106\Contained) Then
														If (Not RadioState4[0]) Then
															RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\OhGod.ogg"))
															RadioState[4] = RadioState[4] + 1.0
															RadioState4[0] = True
														EndIf
													EndIf
													;[End Block]
												Case 100
													;[Block]
													If (Not RadioState4[1]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState4[1] = True
													EndIf	
													;[End Block]
												Case 158
													;[Block]
													If MTFTimer = 0.0 And (Not RadioState4[2]) Then 
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin1.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState[2] = True
													EndIf
													;[End Block]
												Case 200
													;[Block]
													If (Not RadioState4[3]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter4.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState4[3] = True
													EndIf		
													;[End Block]
												Case 260
													;[Block]
													If (Not RadioState4[4]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState4[4] = True
													EndIf		
													;[End Block]
												Case 300
													;[Block]
													If (Not RadioState4[5]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter1.ogg"))	
														RadioState[4] = RadioState[4] + 1.0	
														RadioState4[5] = True
													EndIf		
													;[End Block]
												Case 350
													;[Block]
													If (Not RadioState4[6]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState4[6] = True
													EndIf		
													;[End Block]
												Case 400
													;[Block]
													If (Not RadioState4[7]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState4[7] = True
													EndIf		
													;[End Block]
												Case 450
													;[Block]
													If (Not RadioState4[8]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin3.ogg"))	
														RadioState[4] = RadioState[4] + 1.0		
														RadioState4[8] = True
													EndIf		
													;[End Block]
												Case 600
													;[Block]
													If (Not RadioState4[9]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin4.ogg"))	
														RadioState[4] = RadioState[4] + 1.0	
														RadioState4[9] = True
													EndIf		
													;[End Block]
											End Select
										EndIf
									EndIf
									;[End Block]
								Case 5
									;[Block]
									ResumeChannel(RadioCHN[5])
									If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
									;[End Block]
							End Select 
							
							x = x + 66.0
							y = y + 419.0
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio" Then
								ResumeChannel(RadioCHN[0])
								If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
								RadioState[6] = RadioState[6] + fpst\FPSFactor[0]
								Temp = Mid(Str(AccessCode), RadioState[8] + 1.0, 1)
								If RadioState[6] - fpst\FPSFactor[0] =< RadioState[7] * 50.0 And RadioState[6] > RadioState[7] * 50.0 Then
									PlaySound_Strict(RadioBuzz)
									RadioState[7] = RadioState[7] + 1.0
									If RadioState[7] >= Temp Then
										RadioState[7] = 0.0
										RadioState[6] = -100.0
										RadioState[8] = RadioState[8] + 1.0
										If RadioState[8] = 4.0 Then RadioState[8] = 0.0 : RadioState[6] = -200.0
									EndIf
								EndIf
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\State2 <> i - 2 Then
											PlaySound_Strict(RadioSquelch)
											If RadioCHN[Int(SelectedItem\State2)] <> 0 Then PauseChannel(RadioCHN[Int(SelectedItem\State2)])
										EndIf
										SelectedItem\State2 = i - 2
										If RadioCHN[SelectedItem\State2] <> 0 Then ResumeChannel(RadioCHN[SelectedItem\State2])
									EndIf
								Next
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "cigarette"
					;[Block]
					If CanUseItem(False, True) Then
						Select Rand(6)
							Case 1
								;[Block]
								msg\Msg = Chr(34) + "I don't have anything to light it with. Umm, what about that... Nevermind." + Chr(34)
								;[End Block]
							Case 2
								;[Block]
								msg\Msg = "You are unable to get lit."
								;[End Block]
							Case 3
								;[Block]
								msg\Msg = Chr(34) + "I quit that a long time ago." + Chr(34)
								;[End Block]
							Case 4
								;[Block]
								msg\Msg = Chr(34) + "Even if I wanted one, I have nothing to light it with." + Chr(34)
								;[End Block]
							Case 5
								;[Block]
								msg\Msg = Chr(34) + "Could really go for one now... Wish I had a lighter." + Chr(34)
								;[End Block]
							Case 6
								;[Block]
								msg\Msg = Chr(34) + "Don't plan on starting, even at a time like this." + Chr(34)
								;[End Block]
						End Select
						msg\Timer = 70.0 * 6.0
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "scp420j"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using = 1 Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							msg\Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34)
						Else
							msg\Msg = Chr(34) + "MAN DATS SUM GOOD ASS SHIT." + Chr(34)
							me\Injuries = Max(me\Injuries - 0.5, 0.0)
							me\BlurTimer = 500.0
							GiveAchievement(Achv420J)
							PlaySound_Strict(LoadTempSound("SFX\Music\Using420J.ogg"))
						EndIf
						msg\Timer = 70.0 * 6.0
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "joint"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using = 1 Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							msg\Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34)
						Else
							msg\DeathMsg = SubjectName + " found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							msg\DeathMsg = msg\DeathMsg + "whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							msg\Msg = Chr(34) + "UH WHERE... WHAT WAS I DOING AGAIN... MAN I NEED TO TAKE A NAP..." + Chr(34)
							Kill()						
						EndIf
						msg\Timer = 70.0 * 6.0
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "scp420s"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using = 1 Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							msg\Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34)
						Else
							msg\DeathMsg = SubjectName + " found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							msg\DeathMsg = msg\DeathMsg + "whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							msg\Msg = Chr(34) + "UUUUUUUUUUUUHHHHHHHHHHHH..." + Chr(34)
							Kill()						
						EndIf
						msg\Timer = 70.0 * 6.0
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If I_714\Using = 1 Then
						msg\Msg = "You removed the ring."
						I_714\Using = 0
					Else
						GiveAchievement(Achv714)
						msg\Msg = "You put on the ring."
						I_714\Using = 1
					EndIf
					msg\Timer = 70.0 * 6.0
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If wi\BallisticVest = 0 Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + (fpst\FPSFactor[0] / 4.0), 100.0)
						
						If SelectedItem\State = 100.0 Then
							If wi\HazmatSuit > 0 Then
								msg\Msg = "You removed the hazmat suit."
								wi\HazmatSuit = 0
								DropItem(SelectedItem)
							Else
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								msg\Msg = "You put on the hazmat suit."
								If SelectedItem\ItemTemplate\TempName = "hazmatsuit" Then
									wi\HazmatSuit = 1
								ElseIf SelectedItem\ItemTemplate\TempName = "hazmatsuit2" Then
									wi\HazmatSuit = 2
								Else
									wi\HazmatSuit = 3
								EndIf
								If wi\NightVision Then CameraFogFar = StoredCameraFogFar
								wi\GasMask = 0
								wi\NightVision = 0
								wi\BallisticHelmet = 0
								wi\SCRAMBLE = 0
							EndIf
							SelectedItem\State = 0.0
							msg\Timer = 70.0 * 6.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
					
					SelectedItem\State = Min(SelectedItem\State + (fpst\FPSFactor[0] / (2.0 + (0.5 * (SelectedItem\ItemTemplate\TempName = "finevest")))), 100)
					
					If SelectedItem\State = 100.0 Then
						If wi\BallisticVest > 0 Then
							msg\Msg = "You removed the vest."
							wi\BallisticVest = 0
							DropItem(SelectedItem)
						Else
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							If SelectedItem\ItemTemplate\TempName = "vest" Then
								msg\Msg = "You put on the vest and feel slightly encumbered."
								wi\BallisticVest = 1
							Else
								msg\Msg = "You put on the vest and feel heavily encumbered."
								wi\BallisticVest = 2
							EndIf
						EndIf
						SelectedItem\State = 0.0
						msg\Timer = 70.0 * 6.0
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If PreventItemOverlapping(True, False, False, False, False) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + (fpst\FPSFactor[0]) / 1.6, 100.0)
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\GasMask > 0 Then
								msg\Msg = "You removed the gas mask."
								wi\GasMask = 0
							Else
								If SelectedItem\ItemTemplate\TempName = "supergasmask"
									msg\Msg = "You put on the gas mask and you can breathe easier."
									wi\GasMask = 2
								Else
									msg\Msg = "You put on the gas mask."
									If SelectedItem\Itemtemplate\TempName = "gasmask3"
										wi\GasMask = 3
									Else
										wi\GasMask = 1
									EndIf
								EndIf
							EndIf
							SelectedItem\State = 0.0
							msg\Timer = 70.0 * 6.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					If SelectedItem\State =< 100.0 Then SelectedItem\State = Max(0.0, SelectedItem\State - fpst\FPSFactor[0] * 0.005)
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					If PreventItemOverlapping(False, False, True, False, False) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fpst\FPSFactor[0] / 1.6, 100.0)
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If I_1499\Using > 0 Then
								msg\Msg = "You removed the gas mask."
								I_1499\Using = 0
							Else
								If SelectedItem\ItemTemplate\TempName = "scp1499" Then
									msg\Msg = "You put on the gas mask."
									I_1499\Using = 1
								Else
									msg\Msg = "You put on the gas mask and you can breathe easier."
									I_1499\Using = 2
								EndIf
								GiveAchievement(Achv1499)
								For r.Rooms = Each Rooms
									If r\RoomTemplate\Name = "dimension1499" Then
										me\BlinkTimer = -1.0
										I_1499\PrevRoom = PlayerRoom
										I_1499\PrevX = EntityX(me\Collider)
										I_1499\PrevY = EntityY(me\Collider)
										I_1499\PrevZ = EntityZ(me\Collider)
										
										If I_1499\x = 0.0 And I_1499\y = 0.0 And I_1499\z = 0.0 Then
											PositionEntity(me\Collider, r\x + 6086.0 * RoomScale, r\y + 304.0 * RoomScale, r\z + 2292.5 * RoomScale)
											RotateEntity(me\Collider, 0.0, 90.0, 0.0, True)
										Else
											PositionEntity(me\Collider, I_1499\x, I_1499\y + 0.05, I_1499\z)
										EndIf
										ResetEntity(me\Collider)
										UpdateDoors()
										UpdateRooms()
										For it.Items = Each Items
											it\DistTimer = 0.0
										Next
										PlayerRoom = r
										PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Enter.ogg"))
										I_1499\x = 0.0
										I_1499\y = 0.0
										I_1499\z = 0.0
										If Curr096 <> Null Then
											If Curr096\SoundCHN <> 0 Then
												SetStreamVolume_Strict(Curr096\SoundCHN, 0.0)
											EndIf
										EndIf
										For e.Events = Each Events
											If e\EventName = "dimension1499" Then
												If EntityDistanceSquared(e\room\OBJ, me\Collider) > PowTwo(8300.0 * RoomScale) Then
													If e\EventState2 < 5.0 Then
														e\EventState2 = e\EventState2 + 1.0
													EndIf
												EndIf
												Exit
											EndIf
										Next
										Exit
									EndIf
								Next
							EndIf
							SelectedItem\State = 0.0
							msg\Timer = 70.0 * 6.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						Select SelectedItem\ItemTemplate\Name
							Case "Old Badge"
								;[Block]
								msg\Msg = Chr(34) + "Huh? This guy looks just like me!" + Chr(34)
								msg\Timer = 70.0 * 10.0
								;[End Block]
						End Select
						
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						
						msg\Msg = Chr(34) + "Isn't this the key to that old shack? The one where I... No, it can't be." + Chr(34)
						msg\Timer = 70.0 * 10.0						
					EndIf
					
					SelectedItem\State = 1.0
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\State = 0.0
						Select SelectedItem\ItemTemplate\Name
							Case "Disciplinary Hearing DH-S-4137-17092"
								;[Block]
								me\BlurTimer = 1000.0
								
								msg\Msg = Chr(34) + "Why does this seem so familiar?" + Chr(34)
								msg\Timer = 70.0 * 10.0
								PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
								SelectedItem\state = 1.0
								;[End Block]
						End Select
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\State = 0.0
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(1, 5) + ".ogg"))
					EndIf
					
					SelectedItem\State = 1.0
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using = 1 Then
						msg\Msg = "You closed the locket."
						I_427\Using = 0
					Else
						GiveAchievement(Achv427)
						msg\Msg = "You opened the locket."
						I_427\Using = 1
					EndIf
					msg\Timer = 70.0 * 6.0
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(False, True) Then
						msg\Msg = "You swallowed the pill."
						msg\Timer = 70.0 * 6.0
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "scp500pilldeath"
					;[Block]
					If CanUseItem(False, True) Then
						msg\Msg = "You swallowed the pill."
						msg\Timer = 70.0 * 6.0
						
						If I_427\Timer < 70.0 * 360.0 Then
							I_427\Timer = 70.0 * 360.0
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "syringeinf"
					;[Block]
					msg\Msg = "You injected yourself the syringe."
					msg\Timer = 70 * 8.0
					
					me\VomitTimer = 70.0 * 1.0
					
				    I_008\Timer = I_008\Timer + (1 + (1 * SelectedDifficulty\AggressiveNPCs))
					RemoveItem(SelectedItem)
					SelectedItem = Null
					;[End Block]
				Case "helmet"
					;[Block]
					If PreventItemOverlapping(False, False, False, True, False) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fpst\FPSFactor[0], 100.0)
						
					    If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\BallisticHelmet > 0 Then
								msg\Msg = "You removed the helmet."
                                wi\BallisticHelmet = 0
							Else
								msg\Msg = "You put on the helmet."
								wi\BallisticHelmet = 1
							EndIf
						    SelectedItem\State = 0.0
						    msg\Timer = 70.0 * 6.0
						    SelectedItem = Null
					    EndIf
					EndIf
					;[End Block]
				Case "scramble"
					;[Block]
					If PreventItemOverlapping(False, False, False, False, True) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
							
						SelectedItem\State3 = Min(SelectedItem\State3 + (fpst\FPSFactor[0] / 1.6), 100.0)
							
						If SelectedItem\State3 = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								
							If wi\SCRAMBLE > 0 Then
								msg\Msg = "You removed the gear."
								wi\SCRAMBLE = 0
							Else
								msg\Msg = "You put on the gear."
								wi\SCRAMBLE = 1
							EndIf
							SelectedItem\State3 = 0.0
							msg\Timer = 70.0 * 6.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Default
					;[Block]
					; ~ Check if the item is an inventory-type object
					If SelectedItem\InvSlots > 0 Then
						DoubleClick = 0
						MouseHit1 = 0
						MouseDown1 = 0
						LastMouseHit1 = 0
						OtherOpen = SelectedItem
						SelectedItem = Null
					EndIf
					;[End Block]
			End Select
			
			If MouseHit2 Then
				EntityAlpha(tt\OverlayID[6], 0.0)
				
				Local IN$ = SelectedItem\ItemTemplate\TempName
				
				If IN = "firstaid" Lor IN = "finefirstaid" Lor IN = "firstaid2"
					SelectedItem\State = 0.0
				ElseIf IN = "vest" Lor IN = "finevest"
					SelectedItem\State = 0.0
					If (Not wi\BallisticVest) Then
						DropItem(SelectedItem, False)
					EndIf
				ElseIf IN = "hazmatsuit" Lor IN = "hazmatsuit2" Lor IN = "hazmatsuit3"
					SelectedItem\State = 0.0
					If wi\HazmatSuit = 0 Then
						DropItem(SelectedItem, False)
					EndIf
				ElseIf IN = "scp1499" Lor IN = "super1499" Lor IN = "gasmask" Lor IN = "supergasmask" Lor IN = "gasmask3" Lor IN = "helmet"
					SelectedItem\State = 0.0
				ElseIf IN = "nvg" Lor IN = "supernvg" Lor IN = "finenvg"
				ElseIf IN = "nvg" Lor IN = "supernvg" Lor IN = "finenvg" Lor IN = "scramble"
					SelectedItem\State3 = 0.0
				EndIf
				
				If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
				SelectedItem = Null
			EndIf
		EndIf		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 6
			If RadioCHN[i] <> 0 Then 
				If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
			EndIf
		Next
	EndIf
	
	For it.Items = Each Items
		If it <> SelectedItem Then
			Select it\ItemTemplate\TempName
				Case "firstaid", "finefirstaid", "firstaid2", "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3", "scp1499", "super1499", "gasmask", "supergasmask", "gasmask3", "helmet"
					;[Block]
					it\State = 0.0
					;[End Block]
				Case "nvg", "supernvg", "finenvg", "scramble"
					;[Block]
					it\State3 = 0.0
					;[End Block]
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	
	CatchErrors("UpdateGUI")
End Function

Function DrawMenu()
	CatchErrors("Uncaught (DrawMenu)")
	
	Local x%, y%, Width%, Height%, i%
	
	If (Not InFocus()) Then ; ~ Game is out of focus then pause the game
		MenuOpen = True
		PauseSounds()
		Delay(1000.0) ; ~ Reduce the CPU take while game is not in focus
    EndIf
	If MenuOpen Then
		Width = ImageWidth(tt\ImageID[0])
		Height = ImageHeight(tt\ImageID[0])
		x = GraphicWidth / 2.0 - Width / 2.0
		y = GraphicHeight / 2.0 - Height / 2.0
		
		DrawImage(tt\ImageID[0], x, y)
		
		Color(255, 255, 255)
		
		x = x + 132.0 * MenuScale
		y = y + 122.0 * MenuScale	
		
		If AchievementsMenu > 0 Then
			SetFont(fo\FontID[1])
			Text(x, y - (122 - 45) * MenuScale, "ACHIEVEMENTS", False, True)
			SetFont(fo\FontID[0])
		ElseIf OptionsMenu > 0 Then
			SetFont(fo\FontID[1])
			Text(x, y - (122 - 45) * MenuScale, "OPTIONS", False, True)
			SetFont(fo\FontID[0])
		ElseIf QuitMsg > 0 Then
			SetFont(fo\FontID[1])
			Text(x, y - (122 - 45) * MenuScale, "QUIT?", False, True)
			SetFont(fo\FontID[0])
		ElseIf me\KillTimer >= 0.0 Then
			SetFont(fo\FontID[1])
			Text(x, y - (122 - 45) * MenuScale, "PAUSED", False, True)
			SetFont(fo\FontID[0])
		Else
			SetFont(fo\FontID[1])
			Text(x, y - (122 - 45) * MenuScale, "YOU DIED", False, True)
			SetFont(fo\FontID[0])
		EndIf		
		
		Local AchvXIMG% = (x + (22.0 * MenuScale))
		Local Scale# = GraphicHeight / 768.0
		Local SeparationConst% = 76.0 * Scale
		Local ImgSize% = 64.0
		
		If AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0
			SetFont(fo\FontID[0])
			Text(x, y, "Difficulty: " + SelectedDifficulty\Name)
			Text(x, y + 20 * MenuScale, "Save: " + CurrSave)
			Text(x, y + 40 * MenuScale, "Map seed: " + RandomSeed)
		ElseIf AchievementsMenu =< 0 And OptionsMenu > 0 And QuitMsg =< 0 And me\KillTimer >= 0.0
			Color(0, 255, 0)
			If OptionsMenu = 1
				Rect(x - 10 * MenuScale, y - 5 * MenuScale, 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 2
				Rect(x + 100 * MenuScale, y - 5 * MenuScale, 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 3
				Rect(x + 210 * MenuScale, y - 5 * MenuScale, 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 4
				Rect(x + 320 * MenuScale, y - 5 * MenuScale, 110 * MenuScale, 40 * MenuScale, True)
			EndIf
			
			Local tX# = (GraphicWidth / 2.0) + (Width / 2.0)
			Local tY# = y
			Local tW# = 400.0 * MenuScale
			Local tH# = 150.0 * MenuScale
			
			Color(255, 255, 255)
			Select OptionsMenu
				Case 1 ; ~ Graphics
					;[Block]
					SetFont(fo\FontID[0])
					
					y = y + 50 * MenuScale
					
					Color(100, 100, 100)
					Text(x, y + 4 * MenuScale, "Enable bump mapping:")	
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "bump")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "VSync:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vsync")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Anti-aliasing:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "antialias")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Enable room lights:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "roomlights")
					EndIf
					
					y = y + 40 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Screen gamma:")
					If MouseOn(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "gamma", ScreenGamma)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Particle amount:")
					If (MouseOn(x + 270 * MenuScale, y - 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0) Lor OnSliderID = 2
						DrawOptionsTooltip(tX, tY, tW, tH, "particleamount", ParticleAmount)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Texture LOD Bias:")
					If (MouseOn(x + 270 * MenuScale, y - 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0) Lor OnSliderID = 3
						DrawOptionsTooltip(tX, tY, tW, tH + 100 * MenuScale, "texquality")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(100, 100, 100)
					Text(x, y + 4 * MenuScale, "Save textures in the VRAM:")	
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vram")
					EndIf
					
					y = y + 40 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Field of view:")
					Color(255, 255, 0)
					Text(x + 5 * MenuScale, y + 29 * MenuScale, Int(FOV) + "°")
					If MouseOn(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "fov")
					EndIf
					;[End Block]
				Case 2 ; ~ Audio
					;[Block]
					SetFont(fo\FontID[0])
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Music volume:")
					If MouseOn(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "musicvol", MusicVolume)
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Sound volume:")
					If MouseOn(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "soundvol", PrevSFXVolume)
					EndIf
					
					y = y + 40 * MenuScale
					
					Color(100, 100, 100)
					Text(x, y + 4 * MenuScale, "Sound auto-release:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, "sfxautorelease")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(100, 100, 100)
					Text(x, y + 4 * MenuScale, "Enable user tracks:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "usertrack")
					EndIf
					
					If EnableUserTracks Then
						y = y + 30 * MenuScale
						
						Color(255, 255, 255)
						Text(x, y + 4 * MenuScale, "User track mode:")
						If UserTrackMode Then
							Text(x, y + 20 * MenuScale, "Repeat")
						Else
							Text(x, y + 20 * MenuScale, "Random")
						EndIf
						If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "usertrackmode")
						EndIf
					EndIf
					;[End Block]
				Case 3 ; ~ Controls
					;[Block]
					SetFont(fo\FontID[0])
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Mouse sensitivity:")
					If MouseOn(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesensitivity", MouseSensitivity)
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Invert mouse Y-axis:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "mouseinvert")
					EndIf
					
					y = y + 40 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Mouse smoothing:")
					If MouseOn(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesmoothing", MouseSmoothing)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30 * MenuScale
					
					Text(x, y + 4 * MenuScale, "Control configuration:")
					
					y = y + 10 * MenuScale
					
					Text(x, y + 24 * MenuScale, "Move Forward")
					
					Text(x, y + 44 * MenuScale, "Strafe Left")
					
					Text(x, y + 64 * MenuScale, "Move Backward")
					
					Text(x, y + 84 * MenuScale, "Strafe Right")
					
					Text(x, y + 104 * MenuScale, "Manual Blink")
					
					Text(x, y + 124 * MenuScale, "Sprint")
					
					Text(x, y + 144 * MenuScale, "Open/Close Inventory")
					
					Text(x, y + 164 * MenuScale, "Crouch")
					
					Text(x, y + 184 * MenuScale, "Quick Save")
					
					Text(x, y + 204 * MenuScale, "Open/Close Console")
					
					Text(x, y + 224 * MenuScale, "Take Screenshot")
					
					If MouseOn(x, y, 300 * MenuScale, 220 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "controls")
					EndIf
					;[End Block]
				Case 4 ; ~ Advanced
					;[Block]
					SetFont(fo\FontID[0])
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)			
					Text(x, y + 4 * MenuScale, "Show HUD:")	
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "hud")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Enable console:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "consoleenable")
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
						Color(255, 255, 255)
						Text(x, y + 4 * MenuScale, "Open console on error:")
						If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "consoleerror")
						EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
					    Color(255, 255, 255)
					    Text(x, y + 4 * MenuScale, "Console Version:")
						If ConsoleVersion = 1 Then
					        Text(x + 310 * MenuScale, y + 4 * MenuScale, "New")
					    Else
					        Text(x + 310 * MenuScale, y + 4 * MenuScale, "Classic")
					    EndIf    
					    If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						    DrawOptionsTooltip(tX, tY, tW, tH, "consoleversion")
					    EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Achievement popups:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "achpopup")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Show FPS:")
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "showfps")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					Text(x, y + 4 * MenuScale, "Framelimit:")
					
					Color(255, 255, 255)
					If CurrFrameLimit > 0.0 Then
						Color(255, 255, 0)
						Text(x + 5 * MenuScale, y + 34 * MenuScale, FrameLimit + " FPS")
					EndIf
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					If MouseOn(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					;[End Block]
			End Select
		ElseIf AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg > 0 And me\KillTimer >= 0.0
			; ~ Just save this line, ok?
		Else
			If AchievementsMenu > 0 Then
				For i = 0 To 11
					If i + ((AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG, y + ((i / 4) * 120 * MenuScale), i + ((AchievementsMenu - 1) * 12))
					Else
						Exit
					EndIf
				Next
				
				For i = 0 To 11
					If i + ((AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG + ((i Mod 4) * SeparationConst), y + ((i / 4) * 120 * MenuScale), 64 * Scale, 64 * Scale) Then
							AchievementTooltip(i + ((AchievementsMenu - 1) * 12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
			EndIf
		EndIf
		
		y = y + 10
		
		If AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			If me\KillTimer >= 0.0 Then	
				y = y + 72 * MenuScale
				
				y = y + 75 * MenuScale
				If (Not SelectedDifficulty\PermaDeath) Then
					y = y + 75 * MenuScale
				EndIf
				
				y = y + 75 * MenuScale
				
				y = y + 75 * MenuScale
			Else
				y = y + 104 * MenuScale
				
				y = y + 80 * MenuScale
			EndIf
			
			SetFont(fo\FontID[0])
			If me\KillTimer < 0.0 Then RowText(msg\DeathMsg, x, y + 80 * MenuScale, 430 * MenuScale, 600 * MenuScale)
		EndIf
		
		RenderMenuButtons()
		RenderMenuTicks()
		RenderMenuInputBoxes()
		RenderMenuSlideBars()
		RenderMenuSliders()
		
		If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	EndIf
	
	SetFont(fo\FontID[0])
	
	CatchErrors("DrawMenu")
End Function

Function UpdateMenu()
	CatchErrors("Uncaught (UpdateMenu)")
	
	Local x%, y%, z%, Width%, Height%, i%
	
	If MenuOpen Then
		If ShouldDeleteGadgets Then
			DeleteMenuGadgets()
		EndIf
		ShouldDeleteGadgets = False
		
		If (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(me\Collider) =< 1040.0 * RoomScale) And PlayerRoom\RoomTemplate\Name <> "gatea"
			If StopHidingTimer = 0.0 Then
				If Curr173 <> Null And Curr106 <> Null Then
					If EntityDistanceSquared(Curr173\Collider, me\Collider) < 16.0 Lor EntityDistanceSquared(Curr106\Collider, me\Collider) < 16.0 Then 
						StopHidingTimer = 1.0
					EndIf	
				EndIf
			ElseIf StopHidingTimer < 40.0
				If me\KillTimer >= 0.0 Then 
					StopHidingTimer = StopHidingTimer + fpst\FPSFactor[0]
					
					If StopHidingTimer >= 40.0 Then
						PlaySound_Strict(HorrorSFX[15])
						msg\Msg = "STOP HIDING"
						msg\Timer = 70.0 * 6.0
						MenuOpen = False
						DeleteMenuGadgets()
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		
		Width = ImageWidth(tt\ImageID[0])
		Height = ImageHeight(tt\ImageID[0])
		x = GraphicWidth / 2.0 - Width / 2.0
		y = GraphicHeight / 2.0 - Height / 2.0
		
		x = x + 132.0 * MenuScale
		y = y + 122.0 * MenuScale	
		
		If (Not MouseDown1) Then OnSliderID = 0
			
		Local AchvXIMG% = (x + (22.0 * MenuScale))
		Local Scale# = GraphicHeight / 768.0
		Local SeparationConst% = 76.0 * Scale
		Local ImgSize% = 64.0
		
		If AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			; ~ Just save this line, ok?
		ElseIf AchievementsMenu =< 0 And OptionsMenu > 0 And QuitMsg =< 0 And me\KillTimer >= 0.0
			If DrawButton(x + 101 * MenuScale, y + 410 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias(Opt_AntiAlias)
				TextureLodBias(TextureFloat)
				ShouldDeleteGadgets = True
			EndIf
			
			If DrawButton(x - 5 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "GRAPHICS", False) Then
				OptionsMenu = 1
				ShouldDeleteGadgets = True
			EndIf
			If DrawButton(x + 105 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "AUDIO", False) Then
				OptionsMenu = 2
				ShouldDeleteGadgets = True
			EndIf
			If DrawButton(x + 215 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "CONTROLS", False) Then
				OptionsMenu = 3
				ShouldDeleteGadgets = True
			EndIf
			If DrawButton(x + 325 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "ADVANCED", False) Then
				OptionsMenu = 4
				ShouldDeleteGadgets = True
			EndIf
			
			Select OptionsMenu
				Case 1 ; ~ Graphics
					;[Block]
					y = y + 50 * MenuScale
					
					BumpEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, BumpEnabled, True)
					
					y = y + 30 * MenuScale
					
					VSync = DrawTick(x + 270 * MenuScale, y + MenuScale, VSync)
					
					y = y + 30 * MenuScale
					
					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias)
					
					y = y + 30 * MenuScale
					
					EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableRoomLights)
					
					y = y + 40 * MenuScale
					
					ScreenGamma = (SlideBar(x + 270 * MenuScale, y, 100 * MenuScale, ScreenGamma * 50.0) / 50.0)
					
					y = y + 50 * MenuScale
					
					ParticleAmount = Slider3(x + 270 * MenuScale, y, 100 * MenuScale, ParticleAmount, 2, "MINIMAL", "REDUCED", "FULL")
					
					y = y + 50 * MenuScale
					
					TextureDetails = Slider5(x + 270 * MenuScale, y, 100 * MenuScale, TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
					Select TextureDetails
						Case 0
							;[Block]
							TextureFloat = 0.8
							;[End Block]
						Case 1
							;[Block]
							TextureFloat = 0.4
							;[End Block]
						Case 2
							;[Block]
							TextureFloat = 0.0
							;[End Block]
						Case 3
							;[Block]
							TextureFloat = -0.4
							;[End Block]
						Case 4
							;[Block]
							TextureFloat = -0.8
							;[End Block]
					End Select
					TextureLodBias(TextureFloat)
					
					y = y + 50 * MenuScale
					
					SaveTexturesInVRAM = DrawTick(x + 270 * MenuScale, y + MenuScale, SaveTexturesInVRAM, True)
					
					y = y + 40 * MenuScale
					
					CurrFOV = (SlideBar(x + 270 * MenuScale, y, 100 * MenuScale, CurrFOV * 2.0) / 2.0)
					FOV = CurrFOV + 40
					CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / Tan((2.0 * ATan(Tan((FOV) / 2.0) * RealGraphicWidth / RealGraphicHeight)) / 2.0))
					;[End Block]
				Case 2 ; ~ Audio
					;[Block]
					y = y + 50 * MenuScale
					
					MusicVolume = (SlideBar(x + 250 * MenuScale, y, 100 * MenuScale, MusicVolume * 100.0) / 100.0)
					
					y = y + 30 * MenuScale
					
					PrevSFXVolume = (SlideBar(x + 250 * MenuScale, y, 100 * MenuScale, SFXVolume * 100.0) / 100.0)
					If (Not me\Deaf) Then SFXVolume = PrevSFXVolume
					
					y = y + 40 * MenuScale
					
					EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableSFXRelease, True)
					
					y = y + 30 * MenuScale
					
					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks, True)
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						
						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
					EndIf
					;[End Block]
				Case 3 ; ~ Controls
					;[Block]
					y = y + 50 * MenuScale
					
					MouseSensitivity = (SlideBar(x + 270 * MenuScale, y, 100 * MenuScale, (MouseSensitivity + 0.5) * 100.0) / 100.0) - 0.5
					
					y = y + 30 * MenuScale
					
					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
					
					y = y + 40 * MenuScale
					
					MouseSmoothing = (SlideBar(x + 270 * MenuScale, y, 100 * MenuScale, (MouseSmoothing) * 50.0) / 50.0)
					
					y = y + 30 * MenuScale
					
					y = y + 10 * MenuScale
					
					InputBox(x + 200 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, 210.0)], 5)		
					
					InputBox(x + 200 * MenuScale, y + 40 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, 210.0)], 3)	
					
					InputBox(x + 200 * MenuScale, y + 60 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, 210.0)], 6)				
					
					InputBox(x + 200 * MenuScale, y + 80 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, 210.0)], 4)
					
					InputBox(x + 200 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, 210.0)], 7)				
					
					InputBox(x + 200 * MenuScale, y + 120 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, 210.0)], 8)
					
					InputBox(x + 200 * MenuScale, y + 140 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, 210.0)], 9)
					
					InputBox(x + 200 * MenuScale, y + 160 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, 210.0)], 10)
					
					InputBox(x + 200 * MenuScale, y + 180 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, 210.0)], 11)	
					
					InputBox(x + 200 * MenuScale, y + 200 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, 210.0)], 12)
					
					InputBox(x + 200 * MenuScale, y + 220 * MenuScale, 100 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, 210.0)], 13)
					
					Local TempKey%
					
					For i = 0 To 227
						If KeyHit(i) Then TempKey = i : Exit
					Next
					If TempKey <> 0 Then
						Select SelectedInputBox
							Case 3
								;[Block]
								Key\MOVEMENT_LEFT = TempKey
								;[End Block]
							Case 4
								;[Block]
								Key\MOVEMENT_RIGHT = TempKey
								;[End Block]
							Case 5
								;[Block]
								Key\MOVEMENT_UP = TempKey
								;[End Block]
							Case 6
								;[Block]
								Key\MOVEMENT_DOWN = TempKey
								;[End Block]
							Case 7
								;[Block]
								key\BLINK = TempKey
								;[End Block]
							Case 8
								;[Block]
								key\SPRINT = TempKey
								;[End Block]
							Case 9
								;[Block]
								key\INVENTORY = TempKey
								;[End Block]
							Case 10
								;[Block]
								key\CROUCH = TempKey
								;[End Block]
							Case 11
								;[Block]
								key\SAVE = TempKey
								;[End Block]
							Case 12
								;[Block]
								key\CONSOLE = TempKey
								;[End Block]
							Case 13
								;[Block]
								key\SCREENSHOT = TempKey
								;[End Block]
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				Case 4 ; ~ Advanced
					;[Block]
					y = y + 50 * MenuScale
					
					HUDEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDEnabled)
					
					y = y + 30 * MenuScale
					
					Local PrevCanOpenConsole% = CanOpenConsole
					
					CanOpenConsole = DrawTick(x + 270 * MenuScale, y + MenuScale, CanOpenConsole)
					
					If PrevCanOpenConsole Then
						If PrevCanOpenConsole <> CanOpenConsole Then
							ShouldDeleteGadgets = True
						EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
						ConsoleOpening = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleOpening)
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
						Local PrevConsoleVersion% = ConsoleVersion
						
						ConsoleVersion = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleVersion)
						
						If PrevConsoleVersion Then
							If PrevConsoleVersion <> ConsoleVersion Then
								ShouldDeleteGadgets = True
							EndIf
						EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					AchvMSGEnabled = DrawTick(x + 270 * MenuScale, y, AchvMSGEnabled)
					
					y = y + 50 * MenuScale
					
					ShowFPS = DrawTick(x + 270 * MenuScale, y, ShowFPS)
					
					y = y + 30 * MenuScale
					
					Local PrevCurrFrameLimit% = CurrFrameLimit > 0.0
					
					If DrawTick(x + 270 * MenuScale, y, CurrFrameLimit > 0.0) Then
						CurrFrameLimit = (SlideBar(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale, CurrFrameLimit# * 99.0) / 99.0)
						CurrFrameLimit = Max(CurrFrameLimit, 0.01)
						FrameLimit = 19 + (CurrFrameLimit * 100.0)
					Else
						CurrFrameLimit = 0.0
						FrameLimit = 0
					EndIf
					
					If PrevCurrFrameLimit Then
						If PrevCurrFrameLimit <> CurrFrameLimit Then
							ShouldDeleteGadgets = True
						EndIf
					EndIf
					;[End Block]
			End Select
		ElseIf AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg > 0 And me\KillTimer >= 0.0
			Local QuitButton% = 60 
			
			If SelectedDifficulty\SaveType = SAVEONQUIT Lor SelectedDifficulty\SaveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				Local AbleToSave% = True
				
				If RN = "room173intro" Lor (RN = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale) Lor RN = "gatea" Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave Then
					QuitButton = 140
					If DrawButton(x, y + 60 * MenuScale, 430 * MenuScale, 60 * MenuScale, "Save & Quit") Then
						me\DropSpeed = 0.0
						SaveGame(SavePath + CurrSave + "\")
						NullGame()
						MenuOpen = False
						MainMenuOpen = True
						MainMenuTab = MainMenuTab_Default
						CurrSave = ""
						ResetInput()
						Return
					EndIf
				EndIf
			EndIf
			
			If DrawButton(x, y + QuitButton * MenuScale, 430 * MenuScale, 60 * MenuScale, "Quit") Then
				NullGame()
				MenuOpen = False
				MainMenuOpen = True
				MainMenuTab = MainMenuTab_Default
				CurrSave = ""
				ResetInput()
				Return
			EndIf
			
			If DrawButton(x + 101 * MenuScale, y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
				ShouldDeleteGadgets = True
			EndIf
		Else
			If DrawButton(x + 101 * MenuScale, y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
				ShouldDeleteGadgets = True
			EndIf
			
			If AchievementsMenu > 0 Then
				If AchievementsMenu =< Floor(Float(MAXACHIEVEMENTS - 1) / 12.0) Then 
					If DrawButton(x + 341 * MenuScale, y + 344 * MenuScale, 50 * MenuScale, 60 * MenuScale, ">") Then
						AchievementsMenu = AchievementsMenu + 1
						ShouldDeleteGadgets = True
					EndIf
				EndIf
				If AchievementsMenu > 1 Then
					If DrawButton(x + 41 * MenuScale, y + 344 * MenuScale, 50 * MenuScale, 60 * MenuScale, "<") Then
						AchievementsMenu = AchievementsMenu - 1
						ShouldDeleteGadgets = True
					EndIf
				EndIf
			EndIf
		EndIf
		
		y = y + 10
		
		If AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			If me\KillTimer >= 0.0 Then	
				y = y + 72 * MenuScale
				
				If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Resume", True, True) Then
					MenuOpen = False
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
					DeleteMenuGadgets()
					Return
				EndIf
				
				y = y + 75 * MenuScale
				
				If (Not SelectedDifficulty\PermaDeath) Then
					If GameSaved Then
						If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game") Then
							DrawLoading(0)
							
							MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse(Viewport_Center_X, Viewport_Center_Y)
							HidePointer()
							
							FlushKeys()
							FlushMouse()
							me\Playable = True
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0 Then
									MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = Max(MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)), 1)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = 1
									EndIf
								EndIf
							Next
							
							DrawLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fpst\PrevTime = MilliSecs()
							fpst\FPSFactor[0] = 0.0
							
							ResetInput()
							Return
						EndIf
					Else
						DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game", True, False, True)
					EndIf
					y = y + 75 * MenuScale
				EndIf
				
				If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Achievements") Then 
					AchievementsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + 75 * MenuScale
				
				If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Options") Then 
					OptionsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + 75 * MenuScale
			Else
				y = y + 104 * MenuScale
				If GameSaved And (Not SelectedDifficulty\PermaDeath) Then
					If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game") Then
						DrawLoading(0)
						
						MenuOpen = False
						LoadGameQuick(SavePath + CurrSave + "\")
						
						MoveMouse(Viewport_Center_X, Viewport_Center_Y)
						HidePointer()
						
						FlushKeys()
						FlushMouse()
						me\Playable = True
						
						UpdateRooms()
						
						For r.Rooms = Each Rooms
							x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
							z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
							
							If x < 12.0 And z < 12.0 Then
								MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = Max(MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)), 1)
								If x < 4.0 And z < 4.0 Then
									If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
									MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = 1
								EndIf
							EndIf
						Next
						
						DrawLoading(100)
						
						me\DropSpeed = 0.0
						
						UpdateWorld(0.0)
						
						fpst\PrevTime = MilliSecs()
						fpst\FPSFactor[0] = 0.0
						
						ResetInput()
						Return
					EndIf
				Else
					DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game", True, False, True)
				EndIf
				If DrawButton(x, y + 80 * MenuScale, 430 * MenuScale, 60 * MenuScale, "Quit to Menu") Then
					NullGame()
					MenuOpen = False
					MainMenuOpen = True
					MainMenuTab = MainMenuTab_Default
					CurrSave = ""
					FlushKeys()
					Return
				EndIf
				y = y + 80 * MenuScale
			EndIf
			
			If me\KillTimer >= 0.0 And (Not MainMenuOpen) Then
				If DrawButton(x, y, 430 * MenuScale, 60 * MenuScale, "Quit") Then
					QuitMsg = 1
					ShouldDeleteGadgets = True
				EndIf
			EndIf
		EndIf
	EndIf
	
	CatchErrors("UpdateMenu")
End Function

Function MouseOn%(x%, y%, Width%, Height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + Width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + Height Then
			Return(True)
		EndIf
	EndIf
	Return(False)
End Function

Include "Source Code\Sound_System.bb"

Const MaterialsFile$ = "Data\materials.ini"

Function LoadEntities()
	CatchErrors("Uncaught (LoadEntities)")
	
	DrawLoading(0)
	
	Local i%, Tex%
	Local b%, t1%, SF%
	Local Name$, Test%, File$
	
	For i = 0 To 9
		TempSounds[i] = 0
	Next
	
	tt\ImageID[0] = LoadImage_Strict("GFX\menu\pause_menu.png")
	MaskImage(tt\ImageID[0], 255, 255, 0)
	ScaleImage(tt\ImageID[0], MenuScale, MenuScale)
	
	If BarStyle = 0 Then
		tt\ImageID[1] = LoadImage_Strict("GFX\blink_meter_red.png")
		
		tt\ImageID[2] = LoadImage_Strict("GFX\stamina_meter.png")
		tt\ImageID[3] = LoadImage_Strict("GFX\stamina_meter_red.png")
	EndIf
	
	tt\ImageID[4] = LoadImage_Strict("GFX\keypad_HUD.png")
	MaskImage(tt\ImageID[4], 255, 0, 255)
	
	tt\ImageID[5] = LoadImage_Strict("GFX\scp_294_panel.png")
	MaskImage(tt\ImageID[5], 255, 0, 255)
	
	tt\ImageID[6] = LoadImage_Strict("GFX\night_vision_goggles_battery.png")
	MaskImage(tt\ImageID[6], 255, 0, 255)
	
	tt\ImageID[7] = LoadImage_Strict("GFX\items\navigator_room_border.png")
	MaskImage(tt\ImageID[7], 255, 0, 255)
	For i = 8 To 10
		tt\ImageID[i] = LoadImage_Strict("GFX\items\navigator_room_border(" + (i - 6) + ").png")
		MaskImage(tt\ImageID[i], 255, 0, 255)
	Next
	tt\ImageID[11] = LoadImage_Strict("GFX\items\navigator_battery_meter.png")
	
	tt\ImageID[12] = CreateImage(GraphicWidth, GraphicHeight)
	
	tt\IconID[0] = LoadImage_Strict("GFX\walk_icon.png")
	tt\IconID[1]= LoadImage_Strict("GFX\sprint_icon.png")
	tt\IconID[2] = LoadImage_Strict("GFX\crouch_icon.png")
	tt\IconID[3] = LoadImage_Strict("GFX\blink_icon.png")
	tt\IconID[4] = LoadImage_Strict("GFX\hand_symbol.png")
	tt\IconID[5] = LoadImage_Strict("GFX\hand_symbol(2).png")
	
	Brightness = GetINIFloat(OptionFile, "Global", "Brightness")
	CameraFogNear = GetINIFloat(OptionFile, "Global", "Camera Fog Near")
	CameraFogFar = GetINIFloat(OptionFile, "Global", "Camera Fog Far")
	StoredCameraFogFar = CameraFogFar
	
	AmbientLightRoomTex = CreateTexture(2, 2, 257)
	TextureBlend(AmbientLightRoomTex, 5)
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport(Camera, 0, 0, GraphicWidth, GraphicHeight)
	CameraRange(Camera, 0.01, CameraFogFar)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, CameraFogNear, CameraFogFar)
	AmbientLight(Brightness, Brightness, Brightness)
	
	ScreenTexs[0] = CreateTexture(512, 512, 1 + 256)
	ScreenTexs[1] = CreateTexture(512, 512, 1 + 256)
	
	CreateBlurImage()
	CameraProjMode(ArkBlurCam, 0)
	
	tt\OverlayTextureID[0] = LoadTexture_Strict("GFX\fog.png", 1) ; ~ FOG
	tt\OverlayID[0] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[0], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[0], tt\OverlayTextureID[0])
	EntityBlend(tt\OverlayID[0], 2)
	EntityOrder(tt\OverlayID[0], -1000)
	MoveEntity(tt\OverlayID[0], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[1] = LoadTexture_Strict("GFX\gas_mask_overlay.png", 1) ; ~ GAS MASK
	tt\OverlayID[1] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[1], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[1], tt\OverlayTextureID[1])
	EntityBlend(tt\OverlayID[1], 2)
	EntityFX(tt\OverlayID[1], 1)
	EntityOrder(tt\OverlayID[1], -1003)
	MoveEntity(tt\OverlayID[1], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[2] = LoadTexture_Strict("GFX\hazmat_suit_overlay.png", 1) ; ~ HAZMAT SUIT
	tt\OverlayID[2] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[2], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[2], tt\OverlayTextureID[2])
	EntityBlend(tt\OverlayID[2], 2)
	EntityFX(tt\OverlayID[2], 1)
	EntityOrder(tt\OverlayID[2], -1003)
	MoveEntity(tt\OverlayID[2], 0, 0, 1.0)
	
	tt\OverlayTextureID[3] = LoadTexture_Strict("GFX\scp_008_overlay.png", 1) ; ~ SCP-008
	tt\OverlayID[3] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[3], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[3], tt\OverlayTextureID[3])
	EntityBlend(tt\OverlayID[3], 3)
	EntityFX(tt\OverlayID[3], 1)
	EntityOrder(tt\OverlayID[3], -1003)
	MoveEntity(tt\OverlayID[3], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[4] = LoadTexture_Strict("GFX\night_vision_goggles_overlay.png", 1) ; ~ NIGHT VISION GOGGLES
	tt\OverlayID[4] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[4], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[4], tt\OverlayTextureID[4])
	EntityBlend(tt\OverlayID[4], 2)
	EntityFX(tt\OverlayID[4], 1)
	EntityOrder(tt\OverlayID[4], -1003)
	MoveEntity(tt\OverlayID[4], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[5] = LoadTexture_Strict("GFX\fog_night_vision_goggles.png", 1) ; ~ FOG IN NIGHT VISION GOGGLES
	tt\OverlayID[5] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[5], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityColor(tt\OverlayID[5], 0.0, 0.0, 0.0)
	EntityFX(tt\OverlayID[5], 1)
	EntityOrder(tt\OverlayID[5], -1005)
	MoveEntity(tt\OverlayID[5], 0.0, 0.0, 1.0)
	
	For i = 1 To 5
		HideEntity(tt\OverlayID[i])
	Next
	
	DrawLoading(5)
	
	tt\OverlayTextureID[6] = CreateTexture(1024, 1024, 1 + 2) ; ~ DARK
	SetBuffer(TextureBuffer(tt\OverlayTextureID[6]))
	Cls()
	SetBuffer(BackBuffer())
	tt\OverlayID[6] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[6], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[6], tt\OverlayTextureID[6])
	EntityBlend(tt\OverlayID[6], 1)
	EntityOrder(tt\OverlayID[6], -1002)
	MoveEntity(tt\OverlayID[6], 0.0, 0.0, 1.0)
	EntityAlpha(tt\OverlayID[6], 0.0)
	
	tt\OverlayTextureID[7] = CreateTexture(1024, 1024, 1 + 2 + 256) ; ~ LIGHT
	SetBuffer(TextureBuffer(tt\OverlayTextureID[7]))
	ClsColor(255, 255, 255)
	Cls()
	ClsColor(0, 0, 0)
	SetBuffer(BackBuffer())
	tt\OverlayID[7] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[7], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[7], tt\OverlayTextureID[7])
	EntityBlend(tt\OverlayID[7], 1)
	EntityOrder(tt\OverlayID[7], -1002)
	MoveEntity(tt\OverlayID[7], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[8] = LoadTexture_Strict("GFX\scp_409_overlay.png", 1) ; ~ SCP-409
	tt\OverlayID[8] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[8], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[8], tt\OverlayTextureID[8])
	EntityBlend(tt\OverlayID[8], 3)
	EntityFX(tt\OverlayID[8], 1)
	EntityOrder(tt\OverlayID[8], -1001)
	MoveEntity(tt\OverlayID[8], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[9] = LoadTexture_Strict("GFX\helmet_overlay.png", 1) ; ~ HELMET
	tt\OverlayID[9] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[9], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[9], tt\OverlayTextureID[9])
	EntityBlend(tt\OverlayID[9], 2)
	EntityFX(tt\OverlayID[9], 1)
	EntityOrder(tt\OverlayID[9], -1003)
	MoveEntity(tt\OverlayID[9], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[10] = LoadTexture_Strict("GFX\bloody_overlay.png", 1) ; ~ BLOOD
	tt\OverlayID[10] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[10], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[10], tt\OverlayTextureID[10])
	EntityBlend(tt\OverlayID[10], 2)
	EntityFX(tt\OverlayID[10], 1)
	EntityOrder(tt\OverlayID[10], -1003)
	MoveEntity(tt\OverlayID[10], 0.0, 0.0, 1.0)
	
	tt\OverlayTextureID[11] = LoadTexture_Strict("GFX\fog_gas_mask.png", 1) ; ~ FOG IN GAS MASK
	tt\OverlayID[11] = CreateSprite(ArkBlurCam)
	ScaleSprite(tt\OverlayID[11], 1.0, Float(GraphicHeight) / Float(GraphicWidth))
	EntityTexture(tt\OverlayID[11], tt\OverlayTextureID[11])
	EntityBlend(tt\OverlayID[11], 3)
	EntityFX(tt\OverlayID[11], 1)
	EntityOrder(tt\OverlayID[11], -1000)
	MoveEntity(tt\OverlayID[11], 0.0, 0.0, 1.0)
	
	For i = 7 To 11
		HideEntity(tt\OverlayID[11])
	Next
	
	me\Collider = CreatePivot()
	EntityRadius(me\Collider, 0.15, 0.30)
	EntityPickMode(me\Collider, 1)
	EntityType(me\Collider, HIT_PLAYER)
	
	me\Head = CreatePivot()
	EntityRadius(me\Head, 0.15)
	EntityType(me\Head, HIT_PLAYER)
	
	; ~ [NPCs]
	
	o\NPCModelID[0] = LoadMesh_Strict("GFX\npcs\scp_173.b3d") ; ~ SCP-173
	
    o\NPCModelID[1] = LoadAnimMesh_Strict("GFX\npcs\scp_106.b3d") ; ~ SCP-106
	
	o\NPCModelID[2] = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ; ~ Guard
	
    o\NPCModelID[3] = LoadAnimMesh_Strict("GFX\npcs\class_d.b3d") ; ~ Class-D
	
	o\NPCModelID[4] = LoadAnimMesh_Strict("GFX\npcs\scp_372.b3d") ; ~ SCP-372
	
	o\NPCModelID[5] = LoadAnimMesh_Strict("GFX\npcs\apache.b3d") ; ~ Apache Helicopter
	
	o\NPCModelID[6] = LoadAnimMesh_Strict("GFX\npcs\apache_rotor.b3d") ; ~ Helicopter's Rotor #1
	
	o\NPCModelID[7] = LoadAnimMesh_Strict("GFX\npcs\MTF.b3d") ; ~ MTF
	
	o\NPCModelID[8] = LoadAnimMesh_Strict("GFX\npcs\scp_096.b3d") ; ~ SCP-096
	
	o\NPCModelID[9] = LoadAnimMesh_Strict("GFX\npcs\scp_049.b3d") ; ~ SCP-049
	
	o\NPCModelID[10] = LoadAnimMesh_Strict("GFX\npcs\scp_049_2.b3d") ; ~ SCP-049-2
	
	o\NPCModelID[11] = LoadAnimMesh_Strict("GFX\npcs\scp_513_1.b3d") ; ~ SCP-513-1
	
	o\NPCModelID[12] = LoadAnimMesh_Strict("GFX\npcs\scp_035_tentacle.b3d") ; ~ SCP-035's Tentacle
	
	o\NPCModelID[13] = LoadAnimMesh_Strict("GFX\npcs\scp_860_2.b3d") ; ~ SCP-860-2
	
	o\NPCModelID[14] = LoadAnimMesh_Strict("GFX\npcs\scp_939.b3d") ; ~ SCP-939
	
	o\NPCModelID[15] = LoadAnimMesh_Strict("GFX\npcs\scp_066.b3d") ; ~ SCP-066
	
	o\NPCModelID[16] = LoadAnimMesh_Strict("GFX\npcs\scp_966.b3d") ; ~ SCP-966
	
	o\NPCModelID[17] = LoadAnimMesh_Strict("GFX\npcs\scp_1048_a.b3d") ; ~ SCP-1048-A
	
	o\NPCModelID[18] = LoadAnimMesh_Strict("GFX\npcs\scp_1499_1.b3d") ; ~ SCP-1499-1
	
	o\NPCModelID[19] = LoadAnimMesh_Strict("GFX\npcs\scp_008_1.b3d") ; ~ SCP-008-1
	
	o\NPCModelID[20] = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d") ; ~ Clerk
	
	o\NPCModelID[21] = LoadAnimMesh_Strict("GFX\npcs\apache_rotor(2).b3d") ; ~ Helicopter's Rotor #2
	
	o\NPCModelID[22] = LoadAnimMesh_Strict("GFX\npcs\nazi_officer.b3d") ; ~ Nazi Officer
	
	o\NPCModelID[23] = LoadAnimMesh_Strict("GFX\npcs\scp_1048.b3d") ; ~ SCP-1048
	
	o\NPCModelID[24] = LoadAnimMesh_Strict("GFX\npcs\duck.b3d") ; ~ Anomalous Duck
	
	o\NPCModelID[25] = LoadAnimMesh_Strict("GFX\npcs\CI.b3d") ; ~ CI
	
	o\NPCModelID[26] = LoadAnimMesh_Strict("GFX\npcs\scp_035.b3d") ; ~ SCP-035
	
	o\NPCModelID[27] = LoadMesh_Strict("GFX\npcs\scp_682_arm.b3d") ; ~ SCP-682's Arm
	
	o\NPCModelID[28] = LoadMesh_Strict("GFX\npcs\scp_173_box.b3d") ; ~ SCP-173's Box
	
	o\NPCModelID[29] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon.b3d") ; ~ SCP-205's Demon #1
	
	o\NPCModelID[30] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon(2).b3d") ; ~ SCP-205's Demon #2
	
	o\NPCModelID[31] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon(3).b3d") ; ~ SCP-205's Demon #3
	
	o\NPCModelID[32] = LoadAnimMesh_Strict("GFX\npcs\scp_205_woman.b3d") ; ~ SCP-205's Woman
	
	o\NPCModelID[33] = LoadAnimMesh_Strict("GFX\npcs\vehicle.b3d") ; ~ Vehicle
	
    For i = 0 To MaxNPCModelIDAmount - 1
        HideEntity(o\NPCModelID[i])
    Next
	
	; ~ [DOORS]
	
	o\DoorModelID[0] = LoadMesh_Strict("GFX\map\Door01.x") ; ~ Default Door
	
	o\DoorModelID[1] = LoadMesh_Strict("GFX\map\DoorFrame.x") ; ~ Frame #1
	
	o\DoorModelID[2] = LoadMesh_Strict("GFX\map\HeavyDoor1.x") ; ~ Heavy door #1
	
	o\DoorModelID[3] = LoadMesh_Strict("GFX\map\HeavyDoor2.x") ; ~ Heavy door #2
	
	o\DoorModelID[4] = LoadMesh_Strict("GFX\map\DoorColl.x") ; ~ Collider
	
	o\DoorModelID[5] = LoadMesh_Strict("GFX\map\ContDoorLeft.x") ; ~ Big Door #1
	
	o\DoorModelID[6] = LoadMesh_Strict("GFX\map\ContDoorRight.x") ; ~ Big Door #2
	
	o\DoorModelID[7] = LoadMesh_Strict("GFX\map\ElevatorDoor.b3d") ; ~ Elevator Door
	
	o\DoorModelID[8] = LoadMesh_Strict("GFX\map\forest\Door_Frame.b3d") ; ~ Frame #2
	
	o\DoorModelID[9] = LoadMesh_Strict("GFX\map\forest\Door.b3d") ; ~ Wooden Door
	
	o\DoorModelID[10] = LoadMesh_Strict("GFX\map\Door02.x") ; ~ One-sided Door
	
	For i = 0 To MaxDoorModelIDAmount - 1
	    HideEntity(o\DoorModelID[i])
	Next
	
	; ~ [LEVERS]
	
	o\LeverModelID[0] = LoadMesh_Strict("GFX\map\leverbase.x") ; ~ Lever Base
	
	o\LeverModelID[1] = LoadMesh_Strict("GFX\map\leverhandle.x") ; ~ Lever Handle
	
	For i = 0 To MaxLeverModelIDAmount - 1
	    HideEntity(o\LeverModelID[i])
	Next
	
	; ~ [BUTTONS]
	
	o\ButtonModelID[0] = LoadMesh_Strict("GFX\map\Button.b3d") ; ~ Button
	
	o\ButtonModelID[1] = LoadMesh_Strict("GFX\map\ButtonKeycard.b3d") ; ~ Keycard Button
	
	o\ButtonModelID[2] = LoadMesh_Strict("GFX\map\ButtonCode.b3d") ; ~ Code Button
	
	o\ButtonModelID[3] = LoadMesh_Strict("GFX\map\ButtonScanner.b3d") ; ~ Scanner Button
	
	o\ButtonModelID[4] = LoadMesh_Strict("GFX\map\ButtonElevator.b3d") ; ~ Elevator Button
	
	For i = 0 To MaxButtonModelIDAmount - 1
        HideEntity(o\ButtonModelID[i])
    Next	
	
	; ~ [MISC]
	
	o\MiscModelID[0] = LoadMesh_Strict("GFX\items\cup_liquid.b3d") ; ~ Liquid for cups dispensed by SCP-294
	HideEntity(o\MiscModelID[0])
	
	tt\LightSpriteID[0] = LoadTexture_Strict("GFX\light.png", 1)
	tt\LightSpriteID[1] = LoadTexture_Strict("GFX\light(2).png", 1)
	tt\LightSpriteID[2] = LoadTexture_Strict("GFX\light_sprite.png", 1)
	
	DrawLoading(15)
	
	tt\MiscTextureID[0] = LoadTexture_Strict("GFX\scp_079_overlay.png")
	
	For i = 1 To 6
		tt\MiscTextureID[i] = LoadTexture_Strict("GFX\scp_079_overlay(" + (i + 1) + ").png")
	Next
	
	tt\MiscTextureID[7] = LoadTexture_Strict("GFX\scp_895_overlay.png")
	
	For i = 8 To 12
		tt\MiscTextureID[i] = LoadTexture_Strict("GFX\scp_895_overlay(" + (i - 6) + ").png")
	Next
	
	tt\MiscTextureID[13] = LoadTexture_Strict("GFX\map\tesla.png", 1 + 2)
	
	tt\MiscTextureID[16] = LoadTexture_Strict("GFX\map\keypad.jpg")
	tt\MiscTextureID[17] = LoadTexture_Strict("GFX\map\keypad_locked.png")
	
	DrawLoading(20)
	
	tt\DecalTextureID[0] = LoadTexture_Strict("GFX\decal.png", 1 + 2)
	For i = 1 To 7
		tt\DecalTextureID[i] = LoadTexture_Strict("GFX\decal(" + (i + 1) + ").png", 1 + 2)
	Next
	
	tt\DecalTextureID[8] = LoadTexture_Strict("GFX\decal_pd.png", 1 + 2)	
	For i = 9 To 12
		tt\DecalTextureID[i] = LoadTexture_Strict("GFX\decal_pd(" + (i - 7) + ").png", 1 + 2)	
	Next
	
	tt\DecalTextureID[13] = LoadTexture_Strict("GFX\bullet_hole.png", 1 + 2)	
	tt\DecalTextureID[14] = LoadTexture_Strict("GFX\bullet_hole(2).png", 1 + 2)	
	
	tt\DecalTextureID[15] = LoadTexture_Strict("GFX\blood_drop.png", 1 + 2)
	tt\DecalTextureID[16] = LoadTexture_Strict("GFX\blood_drop(2).png", 1 + 2)
	
	tt\DecalTextureID[17] = LoadTexture_Strict("GFX\decal_scp_427.png", 1 + 2)
	
	tt\DecalTextureID[18] = LoadTexture_Strict("GFX\decal_pd(6).png", 1 + 2)	
	
	tt\DecalTextureID[19] = LoadTexture_Strict("GFX\decal_scp_409.png", 1 + 2)
	
	DrawLoading(25)
	
	; ~ [CAMS]
	
	o\CamModelID[0] = LoadMesh_Strict("GFX\map\cambase.x") ; ~ Cam Base
	
	o\CamModelID[1] = LoadMesh_Strict("GFX\map\CamHead.b3d") ; ~ Cam Head
	
	For i = 0 To MaxCamModelIDAmount - 1
        HideEntity(o\CamModelID[i])
    Next
	
	; ~ [MONITORS]
	
	o\MonitorModelID[0] = LoadMesh_Strict("GFX\map\monitor.b3d") ; ~ Monitor
	
	o\MonitorModelID[1] = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d") ; ~ Checkpoint Monitor LCZ
	
	o\MonitorModelID[2] = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d") ; ~ Checkpoint Monitor HCZ
	
    For i = 0 To MaxMonitorModelIDAmount - 1
        HideEntity(o\MonitorModelID[i])
    Next
	
	tt\MonitorTextureID[0] = LoadTexture_Strict("GFX\monitor_overlay.png")
	tt\MonitorTextureID[1] = LoadTexture_Strict("GFX\map\lockdown_screen(2).png")
	tt\MonitorTextureID[2] = LoadTexture_Strict("GFX\map\lockdown_screen.png")
	tt\MonitorTextureID[3] = LoadTexture_Strict("GFX\map\lockdown_screen(3).png")
	tt\MonitorTextureID[4] = CreateTexture(1, 1)
	SetBuffer(TextureBuffer(tt\MonitorTextureID[4]))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	
	For i = 2 To CountSurfaces(o\MonitorModelID[1])
		SF = GetSurface(o\MonitorModelID[1], i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitor_overlay.png"
					BrushTexture(b, tt\MonitorTextureID[4], 0, 0)
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then FreeTexture(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	For i = 2 To CountSurfaces(o\MonitorModelID[2])
		SF = GetSurface(o\MonitorModelID[2], i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitor_overlay.png"
					BrushTexture(b, tt\MonitorTextureID[4], 0, 0)
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then FreeTexture(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	
	UserTrackMusicAmount = 0
	If EnableUserTracks Then
		Local DirPath$ = "SFX\Radio\UserTracks\"
		
		If FileType(DirPath) <> 2 Then
			CreateDir(DirPath)
		EndIf
		
		Local Dir% = ReadDir("SFX\Radio\UserTracks\")
		
		Repeat
			File = NextFile(Dir)
			If File = "" Then Exit
			If FileType("SFX\Radio\UserTracks\" + File) = 1 Then
				Test = LoadSound("SFX\Radio\UserTracks\" + File)
				If Test <> 0 Then
					UserTrackName[UserTrackMusicAmount] = File
					UserTrackMusicAmount = UserTrackMusicAmount + 1
				EndIf
				FreeSound(Test)
			EndIf
		Forever
		CloseDir(Dir)
	EndIf
	
	InitItemTemplates()
	
	tt\ParticleTextureID[0] = LoadTexture_Strict("GFX\smoke.png", 1 + 2)
	tt\ParticleTextureID[1] = LoadTexture_Strict("GFX\flash.png", 1 + 2)
	tt\ParticleTextureID[2] = LoadTexture_Strict("GFX\dust.png", 1 + 2)
	tt\ParticleTextureID[3] = LoadTexture_Strict("GFX\npcs\hg.pt", 1 + 2)
	tt\ParticleTextureID[4] = LoadTexture_Strict("GFX\map\sun.png", 1 + 2)
	tt\ParticleTextureID[5] = LoadTexture_Strict("GFX\blood_sprite.png", 1 + 2)
	tt\ParticleTextureID[6] = LoadTexture_Strict("GFX\smoke(2).png", 1 + 2)
	tt\ParticleTextureID[7] = LoadTexture_Strict("GFX\spark.png", 1 + 2)
	tt\ParticleTextureID[8] = LoadTexture_Strict("GFX\particle.png", 1 + 2)
	
	SetChunkDataValues()
	
	; ~ NPCtypeD - different models with different textures (loaded using "CopyEntity") -- ENDSHN
	For i = 0 To 15
		DTextures[i] = CopyEntity(o\NPCModelID[3])
		HideEntity(DTextures[i])
	Next
	
	; ~ Gonzales
	Tex = LoadTexture_Strict("GFX\npcs\Gonzales.png")
	EntityTexture(DTextures[0], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-970's corpse
	Tex = LoadTexture_Strict("GFX\npcs\D_9341(2).png")
	EntityTexture(DTextures[1], Tex)
	FreeTexture(Tex)
	
	; ~ Scientist
	Tex = LoadTexture_Strict("GFX\npcs\scientist.png")
	EntityTexture(DTextures[2], Tex)
	FreeTexture(Tex)
	
	; ~ Franklin
	Tex = LoadTexture_Strict("GFX\npcs\Franklin.png")
	EntityTexture(DTextures[3], Tex)
	FreeTexture(Tex)
	
	; ~ Janitor # 1
	Tex = LoadTexture_Strict("GFX\npcs\janitor.png")
	EntityTexture(DTextures[4], Tex)
	FreeTexture(Tex)
	
	; ~ Maynard
	Tex = LoadTexture_Strict("GFX\npcs\Maynard.png")
	EntityTexture(DTextures[5], Tex)
	FreeTexture(Tex)
	
	; ~ Afro-American Class-D
	Tex = LoadTexture_Strict("GFX\npcs\class_d(2).png")
	EntityTexture(DTextures[6], Tex)
	FreeTexture(Tex)
	
	; ~ 035 victim
	Tex = LoadTexture_Strict("GFX\npcs\scp_035_victim.png")
	EntityTexture(DTextures[7], Tex)
	FreeTexture(Tex)
	
	If IntroEnabled Then
		; ~ D-9341
		Tex = LoadTexture_Strict("GFX\npcs\D_9341.png")
		EntityTexture(DTextures[8], Tex)
		FreeTexture(Tex)
	EndIf
	
	; ~ Body # 1
	Tex = LoadTexture_Strict("GFX\npcs\body.png")
	EntityTexture(DTextures[9], Tex)
	FreeTexture(Tex)
	
	; ~ Body # 2
	Tex = LoadTexture_Strict("GFX\npcs\body(2).png")
	EntityTexture(DTextures[10], Tex)
	FreeTexture(Tex)
	
	; ~ Janitor # 2
	Tex = LoadTexture_Strict("GFX\npcs\janitor(2).png")
	EntityTexture(DTextures[11], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-008-1's victim
	Tex = LoadTexture_Strict("GFX\npcs\scp_008_1_victim.png")
	EntityTexture(DTextures[12], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-409's victim
	Tex = LoadTexture_Strict("GFX\npcs\body(3).png")
	EntityTexture(DTextures[13], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-939's victim # 2
	Tex = LoadTexture_Strict("GFX\npcs\scp_939_victim.png")
	EntityTexture(DTextures[14], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-939's victim # 1
	Tex = LoadTexture_Strict("GFX\npcs\scp_939_victim(2).png")
	EntityTexture(DTextures[15], Tex)
	FreeTexture(Tex)
	
	LoadMaterials(MaterialsFile)
	
	; ~ [MAINTENANCE TUNNELS]
	
	o\MTModelID[0] = LoadRMesh("GFX\map\mt1.rmesh", Null) ; ~ End Room
	
	o\MTModelID[1] = LoadRMesh("GFX\map\mt2.rmesh", Null) ; ~ Two-way Hallway
	
	o\MTModelID[2] = LoadRMesh("GFX\map\mt2c.rmesh", Null) ; ~ Corner Room
	
	o\MTModelID[3] = LoadRMesh("GFX\map\mt3.rmesh", Null) ; ~ Three-way Room
	
	o\MTModelID[4] = LoadRMesh("GFX\map\mt4.rmesh", Null) ; ~ Four-way Room
	
	o\MTModelID[5] = LoadRMesh("GFX\map\mt_elevator.rmesh", Null) ; ~ Elevator Tunnel
	
	o\MTModelID[6] = LoadRMesh("GFX\map\mt_generator.rmesh", Null) ; ~ Generator Room
	
	For i = 0 To MaxMTModelIDAmount - 1
        HideEntity(o\MTModelID[i])
    Next
	
	TextureLodBias(TextureFloat)
	
	DrawLoading(30)
	
	CatchErrors("LoadEntities")
End Function

Function InitNewGame()
	CatchErrors("Uncaught (InitNewGame)")
	
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events
	
	DrawLoading(45)
	
	HideDistance = 15.0
	
	me\BlinkEffect = 1.0
	me\StaminaEffect = 1.0
	me\Playable = True
	
	me\HeartBeatRate = 70.0
	
	I_005\ChanceToSpawn = Rand(1, 6)
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1, 9) * (10 ^ i)
	Next	
	
	If SelectedMap = "" Then
		CreateMap()
	Else
		LoadMap("Map Creator\Maps\" + SelectedMap)
	EndIf
	InitWayPoints()
	
	DrawLoading(79)
	
	Curr173 = CreateNPC(NPCtype173, 0.0, -30.0, 0.0)
	Curr106 = CreateNPC(NPCtype106, 0.0, -30.0, 0.0)
	Curr106\State = 70.0 * 60.0 * Rnd(12.0, 17.0)
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		If d\Buttons[0] <> 0 Then EntityParent(d\Buttons[0], 0)
		If d\Buttons[1] <> 0 Then EntityParent(d\Buttons[1], 0)
		
		If d\OBJ2 <> 0 And (d\Dir = 0 Lor d\Dir = 4) Then
			MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
			MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
		EndIf	
	Next
	
	For it.Items = Each Items
		EntityType(it\Collider, HIT_ITEM)
		EntityParent(it\Collider, 0)
	Next
	
	DrawLoading(80)
	For sc.SecurityCams = Each SecurityCams
		sc\Angle = EntityYaw(sc\OBJ) + sc\Angle
		EntityParent(sc\OBJ, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights - 1
			If r\Lights[i] <> 0 Then EntityParent(r\Lights[i], 0)
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(Rand(2, 3), EntityX(r\OBJ) + Rnd(- 2.0, 2.0), 0.003, EntityZ(r\OBJ) + Rnd(-2.0, 2.0), 90.0, Rand(360.0), 0.0)
				de\Size = Rnd(0.1, 0.4)
				EntityAlpha(de\OBJ, Rnd(0.85, 0.95))
				ScaleSprite(de\OBJ, de\Size, de\Size)
			EndIf
			
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(0, EntityX(r\OBJ) + Rnd(- 2.0, 2.0), 0.003, EntityZ(r\OBJ) + Rnd(-2.0, 2.0), 90.0, Rand(360.0), 0.0)
				de\Size = Rnd(0.5, 0.7) : EntityAlpha(de\OBJ, 0.7) : de\ID = 1
				EntityAlpha(de\OBJ, Rnd(0.7, 0.85))
				ScaleSprite(de\OBJ, de\Size, de\Size)
			EndIf
		EndIf
		
		If r\RoomTemplate\Name = "room173" And IntroEnabled = False Then 
			PositionEntity(me\Collider, EntityX(r\OBJ) + 3584.0 * RoomScale, 704.0 * RoomScale, EntityZ(r\OBJ) + 1024.0 * RoomScale)
			PlayerRoom = r
			it = CreateItem("Class D Orientation Leaflet", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\ItemTemplate\Found = True
			Inventory[0] = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
			it = CreateItem("Document SCP-173", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\ItemTemplate\Found = True
			Inventory[1] = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
		ElseIf r\RoomTemplate\Name = "room173intro" And IntroEnabled Then
			PositionEntity(me\Collider, EntityX(r\OBJ), 1.0, EntityZ(r\OBJ))
			PlayerRoom = r
		EndIf
	Next
	
	Local rt.RoomTemplates
	
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity(rt\OBJ)
	Next	
	
	Local tw.TempWayPoints
	
	For tw.TempWayPoints = Each TempWayPoints
		Delete(tw)
	Next
	
	TurnEntity(me\Collider, 0.0, Rnd(160.0, 200.0), 0.0)
	
	ResetEntity(me\Collider)
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2nuke"
			e\EventState = 1.0
		EndIf
		If e\EventName = "room106"
			e\EventState2 = 1.0
		EndIf	
		If e\EventName = "room2sl"
			e\EventState3 = 1.0
		EndIf
	Next
	
	MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	
	SetFont(fo\FontID[0])
	
	HidePointer()
	
	me\BlinkTimer = -10.0
	me\BlurTimer = 100.0
	me\Stamina = 100.0
	
	For i = 0 To 70
		fpst\FPSFactor[0] = 1.0
		FlushKeys()
		MovePlayer()
		UpdateDoors()
		UpdateNPCs()
		UpdateWorld()
		If (Int(Float(i) * 0.27) <> Int(Float(i - 1) * 0.27)) Then
			DrawLoading(80 + Int(Float(i) * 0.27))
		EndIf
	Next
	
	FreeTextureCache()
	DrawLoading(100)
	
	FlushKeys()
	FlushMouse()
	
	me\DropSpeed = 0.0
	
	fpst\PrevTime = MilliSecs()
	
	CatchErrors("InitNewGame")
End Function

Function InitLoadGame()
	CatchErrors("Uncaught (InitLoadGame)")
	
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events, i%, x#, z#
	
	DrawLoading(80)
	
	me\Playable = True
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		If d\Buttons[0] <> 0 Then EntityParent(d\Buttons[0], 0)
		If d\Buttons[1] <> 0 Then EntityParent(d\Buttons[1], 0)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\Angle = EntityYaw(sc\OBJ) + sc\Angle
		EntityParent(sc\OBJ, 0)
	Next
	
	ResetEntity(me\Collider)
	
	DrawLoading(90)
	
	MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	
	SetFont(fo\FontID[0])
	
	HidePointer()
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\OBJ <> 0 Then FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	me\DropSpeed = 0.0
	
	For e.Events = Each Events
		; ~ Loading the necessary stuff for dimension1499, but this will only be done if the player is in this dimension already
		If e\EventName = "dimension1499"
			If e\EventState = 2.0
				;[Block]
				DrawLoading(91)
				e\room\Objects[0] = CreatePlane()
				
				Local PlaneTex% = LoadTexture_Strict("GFX\map\dimension1499\grit3.jpg")
				
				EntityTexture(e\room\Objects[0], PlaneTex)
				FreeTexture(PlaneTex)
				PositionEntity(e\room\Objects[0], 0.0, EntityY(e\room\OBJ), 0.0)
				EntityType(e\room\Objects[0], HIT_MAP)
				DrawLoading(92)
				I_1499\Sky = CreateSky("GFX\map\sky\1499sky")
				DrawLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict("GFX\map\dimension1499\1499object" + i + ".b3d")
					HideEntity(e\room\Objects[i])
				Next
				DrawLoading(96)
				CreateChunkParts(e\room)
				DrawLoading(97)
				x = EntityX(e\room\OBJ)
				z = EntityZ(e\room\OBJ)
				
				Local ch.Chunk
				
				For i = -2 To 2 Step 2
					ch = CreateChunk(-1, x * (i * 2.5), EntityY(e\room\OBJ), z)
				Next
				DrawLoading(98)
				UpdateChunks(e\room, 15, False)
				Exit
				;[End Block]
			EndIf
		EndIf
	Next
	
	FreeTextureCache()
	
	CatchErrors("InitLoadGame")
	
	DrawLoading(100)
	
	fpst\PrevTime = MilliSecs()
	fpst\FPSFactor[0] = 0.0
	ResetInput()
End Function

Function NullGame(PlayButtonSFX% = True)
	CatchErrors("Uncaught (NullGame)")
	
	Local i%, x%, y%, Lvl%
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items
	
	KillSounds()
	If PlayButtonSFX Then PlaySound_Strict(ButtonSFX)
	
	ClearTextureCache()
	
	UnableToMove = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer = 0.0
	QuickLoad_CurrEvent = Null
	
	msg\DeathMsg = ""
	
	SelectedMap = ""
	
	UsedConsole = False
	
	DoorTempID = 0
	RoomTempID = 0
	
	GameSaved = 0
	
	HideDistance = 15.0
	
	For x = 0 To MapWidth + 1
		For y = 0 To MapHeight + 1
			MapTemp(x, y) = 0
			MapFound(x, y) = 0
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\Found = False
	Next
	
	me\DropSpeed = 0.0
	me\CurrSpeed = 0.0
	
	me\DeathTimer = 0.0
	
	me\HeartBeatVolume = 0.0
	
	me\StaminaEffect = 1.0
	me\StaminaEffectTimer = 0.0
	me\BlinkEffect = 1.0
	me\BlinkEffectTimer = 0.0
	
	me\Bloodloss = 0.0
	me\Injuries = 0.0
	I_008\Timer = 0.0
	I_409\Timer = 0.0
	
	For i = 0 To 5
		I_1025\State[i] = 0.0
	Next
	
	I_005\ChanceToSpawn = 0
	
	me\SelectedEnding = ""
	me\EndingTimer = 0.0
	me\ExplosionTimer = 0.0
	
	me\CameraShake = 0.0
	me\Shake = 0.0
	me\LightFlash = 0.0
	
	ClearCheats(chs)
	WireFrameState = 0
	WireFrame(0)
	
	wi\GasMaskFogTimer = 0.0
	wi\GasMask = 0
	wi\HazmatSuit = 0
	wi\BallisticVest = 0
	wi\BallisticHelmet = 0
	If wi\NightVision Then
		CameraFogFar = StoredCameraFogFar
		wi\NightVision = 0
	EndIf
	wi\SCRAMBLE = 0
	
	I_714\Using = 0
	
	I_427\Using = 0
	I_427\Timer = 0.0
	
	me\ForceMove = 0.0
	me\ForceAngle = 0.0	
	me\Playable = True
	
	CoffinDistance = 100.0
	
	MTFTimer = 0.0
	
	For s.Screens = Each Screens
		If s\Img <> 0 Then FreeImage(s\Img) : s\Img = 0
		Delete(s)
	Next
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Achievements[i] = 0
	Next
	me\RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	me\EyeIrritation = 0.0
	me\EyeStuck = 0.0
	
	ShouldPlay = 0
	
	me\KillTimer = 0.0
	me\FallTimer = 0.0
	me\Stamina = 100.0
	me\BlurTimer = 0.0
	me\Sanity = 0.0
	me\RestoreSanity = True
	me\Crouch = False
	me\CrouchState = 0.0
	LightVolume = 0.0
	me\Vomit = False
	me\VomitTimer = 0.0
	SecondaryLightOn = True
	PrevSecondaryLightOn = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	msg\Msg = ""
	msg\Timer = 0.0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory[i] = Null
	Next
	SelectedItem = Null
	
	ClosestButton = 0
	
	For d.Doors = Each Doors
		Delete(d)
	Next
	
	For lt.LightTemplates = Each LightTemplates
		Delete(lt)
	Next 
	
	For m.Materials = Each Materials
		Delete(m)
	Next
	
	For wp.WayPoints = Each WayPoints
		Delete(wp)
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete(twp)
	Next	
	
	For r.Rooms = Each Rooms
		Delete(r)
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		Delete(itt)
	Next 
	
	For it.Items = Each Items
		Delete(it)
	Next
	
	For pr.Props = Each Props
		Delete(pr)
	Next
	
	For de.Decals = Each Decals
		Delete(de)
	Next
	
	For n.NPCs = Each NPCs
		Delete(n)
	Next
	
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	Curr513_1 = Null
	
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	
	For e.Events = Each Events
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		If e\Sound3 <> 0 Then FreeSound_Strict(e\Sound3) : e\Sound3 = 0
		
		Delete(e)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		Delete(sc)
	Next
	
	For em.Emitters = Each Emitters
		Delete(em)
	Next	
	
	For p.Particles = Each Particles
		Delete(p)
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\OBJ = 0
	Next
	
	For i = 0 To 6
		If ChannelPlaying(RadioCHN[i]) Then StopChannel(RadioCHN[i])
	Next
	
	I_1499\PrevX = 0.0
	I_1499\PrevY = 0.0
	I_1499\PrevZ = 0.0
	I_1499\PrevRoom = Null
	I_1499\x = 0.0
	I_1499\y = 0.0
	I_1499\z = 0.0
	I_1499\Using = 0
	DeleteChunks()
	
	OptionsMenu = -1
	QuitMsg = -1
	AchievementsMenu = -1
	
	MusicVolume = PrevMusicVolume
	SFXVolume = PrevSFXVolume
	me\Deaf = False
	me\DeafTimer = 0.0
	
	me\Zombie = False
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	ClearWorld()
	Camera = 0
	ArkBlurCam = 0
	me\Collider = 0
	Sky = 0
	InitFastResize()
	
	DeleteMenuGadgets()
	
	CatchErrors("NullGame")
End Function

Include "Source Code\Save_System.bb"

Const ROUGH% = 0, COARSE% = 1, ONETOONE% = 2, FINE% = 3, VERYFINE% = 4

Function Use914(item.Items, Setting%, x#, y#, z#)
	me\RefinedItems = me\RefinedItems + 1
	
	Local it.Items, it2.Items, i%
	
	Select item\ItemTemplate\Name
		Case "Gas Mask", "Heavy Gas Mask"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2 = CreateItem("Gas Mask", "supergasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "SCP-1499"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Gas Mask", "gasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("SCP-1499", "super1499", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case VERYFINE
					;[Block]
					n.NPCs = CreateNPC(NPCtype1499_1, x, y, z)
					n\State = 1.0
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
					n\State3 = 1.0
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Ballistic Vest"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case COARSE
					;[Block]
					it2 = CreateItem("Corrosive Ballistic Vest", "corrvest", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Ballistic Helmet"
		    ;[Block]
			Select Setting
				Case ROUGH, COARSE
				    ;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
				    ;[Block]
					it2 = CreateItem("Ballistic Vest", "vest", x, y, z)
					;[End Block]	
			    Case FINE, VERYFINE
			        ;[Block]
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Clipboard"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					
					For i = 0 To 19
						If item\SecondInv[i] <> Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i] = Null
					Next
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case FINE
					;[Block]
					item\InvSlots = Max(item\State2, 15.0)
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case VERYFINE
					;[Block]
					item\InvSlots = Max(item\State2, 20.0)
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
			End Select
		Case "Wallet"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					
					For i = 0 To 19
						If item\SecondInv[i] <> Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i] = Null
					Next
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case FINE
					;[Block]
					item\InvSlots = Max(item\State2, 15.0)
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case VERYFINE
					;[Block]
					item\InvSlots = Max(item\State2, 20.0)
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
			End Select
			;[End Block]
		Case "Night Vision Goggles"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case ONETOONE
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Night Vision Goggles", "finenvg", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Night Vision Goggles", "supernvg", x, y, z)
					it2\State = 1000.0
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Metal Panel", "SCP-148 Ingot"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2 = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2 = Null
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And it\Picked = False Then
							If DistanceSquared(EntityX(it\Collider, True), EntityX(item\Collider, True), EntityZ(it\Collider, True), EntityZ(item\Collider, True)) < PowTwo(180.0 * RoomScale) Then
								it2 = it
								Exit
							ElseIf DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								it2 = it
								Exit
							EndIf
						EndIf
					Next
					
					If it2 <> Null Then
						Select it2\ItemTemplate\TempName
							Case "gasmask", "supergasmask"
								;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Heavy Gas Mask", "gasmask3", x, y, z)
								;[End Block]
							Case "vest"
								;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
								;[End Block]
							Case "hazmatsuit", "hazmatsuit2"
								;[Block]
								RemoveItem(it2)
								RemoveItem(item)
								
								it2 = CreateItem("Heavy Hazmat Suit", "hazmatsuit3", x, y, z)
								;[End Block]
						End Select
					Else 
						If item\ItemTemplate\Name = "SCP-148 Ingot" Then
							it2 = CreateItem("Metal Panel", "scp148", x, y, z)
							RemoveItem(item)
						Else
							PositionEntity(item\Collider, x, y, z)
							ResetEntity(item\Collider)							
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "Severed Hand", "Black Severed Hand"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(3, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					If (item\ItemTemplate\Name = "Severed Hand")
						it2 = CreateItem("Black Severed Hand", "hand2", x, y, z)
					Else
						it2 = CreateItem("Severed Hand", "hand", x, y, z)
					EndIf
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "First Aid Kit", "Blue First Aid Kit"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1 Then
						it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					Else
						it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Level 0 Key Card", "Level 1 Key Card", "Level 2 Key Card", "Level 3 Key Card", "Level 4 Key Card", "Level 5 Key Card", "Level 6 Key Card"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Playing Card", "misc", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					Select item\ItemTemplate\Name
						Case "Level 0 Key Card"
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(6) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(5) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 1 Key Card"
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(5) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(4) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 2 Key Card"
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(3) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 3 Key Card"
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(10) = 1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(15) = 1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(20) = 1 Then
										it2 = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2 = CreateItem("Playing Card", "misc", x, y, z)	
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 4 Key Card"
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(3) = 1 Then
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									Else
										it2 = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 5 Key Card"	
							;[Block]
							Local CurrAchvAmount% = 0
							
							For i = 0 To MAXACHIEVEMENTS - 1
								If Achievements[i] = True
									CurrAchvAmount = CurrAchvAmount + 1
								EndIf
							Next
							
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(10) = 1 Then
											it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2 = CreateItem("Mastercard", "misc", x, y, z)
										EndIf
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(15) = 1 Then
											it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2 = CreateItem("Mastercard", "misc", x, y, z)
										EndIf
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(20) = 1 Then
											it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2 = CreateItem("Mastercard", "misc", x, y, z)
										EndIf
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case "Level 6 Key Card"	
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(3) = 1 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(5) = 1 Then
										it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
					End Select
					;[End Block]
				Case VERYFINE
					;[Block]
					CurrAchvAmount = 0
					For i = 0 To MAXACHIEVEMENTS - 1
						If Achievements[i] = True
							CurrAchvAmount = CurrAchvAmount + 1
						EndIf
					Next
					
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(20) = 1 Then
									it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case NORMAL
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(25) = 1 Then
									it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case HARD
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(30) = 1 Then
									it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2 = CreateItem("Mastercard", "misc", x, y, z)
								EndIf
							EndIf
							;[End Block]
					End Select
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Key Card Omni"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1 Then
						it2 = CreateItem("Mastercard", "misc", x, y, z)
					Else
						it2 = CreateItem("Playing Card", "misc", x, y, z)			
					EndIf	
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							If Rand(3) = 1 Then
								it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
						Case NORMAL
							;[Block]
							If Rand(4) = 1 Then
								it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
						Case HARD
							;[Block]
							If Rand(5) = 1 Then
								it2 = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
					End Select
					;[End Block]
			End Select		
			RemoveItem(item)
			;[End Block]
		Case "Playing Card", "Coin", "Quarter"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]
			    Case FINE
					;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Mastercard"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case COARSE
					;[Block]
					it2 = CreateItem("Quarter", "25ct", x, y, z)
					
					Local it3.Items, it4.Items, it5.Items
					
					it3 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					
					it4 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType(it4\Collider, HIT_ITEM)
					
					it5 = CreateItem("Quarter", "25ct", x, y, z)
					EntityType(it5\Collider, HIT_ITEM)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]
			    Case FINE
					;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "S-NAV 300 Navigator", "S-NAV 310 Navigator", "S-NAV Navigator", "S-NAV Navigator Ultimate"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2 = CreateItem("Electronical Components", "misc", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("S-NAV 310 Navigator", "nav", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("S-NAV Navigator Ultimate", "nav", x, y, z)
					it2\State = 101.0
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Radio Transceiver"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2 = CreateItem("Electronical Components", "misc", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					it2\State = 101.0
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
					it2\State = 101.0
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "SCP-513"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype513_1 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2 = CreateItem("SCP-513", "scp513", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Some SCP-420-J", "Cigarette"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Joint", "joint", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Smelly Joint", "scp420s", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "9V Battery", "18V Battery", "Strange Battery"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("18V Battery", "18vbat", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "ReVision Eyedrops", "RedVision Eyedrops", "Eyedrops"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("RedVision Eyedrops", "eyedrops", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Eyedrops", "fineeyedrops", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Eyedrops", "supereyedrops", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Hazmat Suit"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Syringe"
			;[Block]
			Select item\ItemTemplate\TempName
				Case "syringe"
					;[Block]
					Select Setting
						Case ROUGH, COARSE
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
							d\Size = 0.07
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case ONETOONE
							;[Block]
							it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
							;[End Block]
						Case FINE
							;[Block]
							it2 = CreateItem("Syringe", "finesyringe", x, y, z)
							;[End Block]
						Case VERYFINE
							;[Block]
							If Rand(3) = 1 Then
								it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							Else
								it2 = CreateItem("Syringe", "syringeinf", x, y, z)
							EndIf
							;[End Block]
					End Select
					;[End Block]
				Case "finesyringe"
					;[Block]
					Select Setting
						Case ROUGH
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
							d\Size = 0.07
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case COARSE
							;[Block]
							it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
							;[End Block]
						Case ONETOONE
							;[Block]
							it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
							;[End Block]
						Case FINE, VERYFINE
							;[Block]
							If Rand(3) = 1 Then
								it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							Else
								it2 = CreateItem("Syringe", "syringeinf", x, y, z)
							EndIf
							;[End Block]
					End Select
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					Select Setting
						Case ROUGH, COARSE, ONETOONE
							;[Block]
							it2 = CreateItem("Electronical Components", "misc", x, y, z)	
							;[End Block]
						Case FINE
							it2 = CreateItem("Syringe", "syringeinf", x, y, z)
						Case VERYFINE
							;[Block]
							If Rand(2) = 1 Then
								n.NPCs = CreateNPC(NPCtype008_1, x, y, z)
								n\State = 2.0
							Else
								it2 = CreateItem("Syringe", "syringeinf", x, y, z)
							EndIf
							;[End Block]
					End Select
				Case "syringeinf"
				    ;[Block]
			        Select Setting
					    Case ROUGH, COARSE
					        ;[Block]
					        d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0)
							d\Size = 0.07 : ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case ONETOONE
						    ;[Block]
						    n.NPCs = CreateNPC(NPCtype008_1, x, y, z)
							n\State = 2.0
							;[End Block]	
						Case FINE
						    ;[Block]
						    it2 = CreateItem("Syringe", "syringe", x, y, z)
						    ;[End Block]
						Case VERYFINE
						    ;[Block]
							If Rand(4) = 1 Then
								it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
							Else
								it2 = CreateItem("Syringe", "finesyringe", x, y, z)
							EndIf
						    ;[End Block]
					End Select
			End Select
			RemoveItem(item)
			;[End Block]
		Case "SCP-500-01", "Upgraded Pill", "Pill"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2 = CreateItem("Pill", "pill", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case FINE
					;[Block]
					Local NO427Spawn% = False
					
					For it3.Items = Each Items
						If it3\ItemTemplate\TempName = "scp427" Then
							NO427Spawn = True
							Exit
						EndIf
					Next
					If (Not NO427Spawn) Then
						it2 = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2 = CreateItem("Upgraded Pill", "scp500pilldeath", x, y, z)
					EndIf
					RemoveItem(item)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2 = CreateItem("Upgraded Pill", "scp500pilldeath", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Origami"
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case COARSE
					;[Block]
					it2 = CreateItem("Blank Paper", "paper", x, y, z)
					;[End Block]
				Case ONETOONE, VERYFINE, FINE
					;[Block]
					Select Rand(22)
						Case 1
							;[Block]
							it2 = CreateItem("Document SCP-005", "paper", x, y, z)
							;[End Block]
						Case 2
							;[Block]
							it2 = CreateItem("Document SCP-008", "paper", x, y, z)
							;[End Block]
						Case 3
							;[Block]
							it2 = CreateItem("Document SCP-012", "paper", x, y, z)
							;[End Block]
						Case 4
							;[Block]
							it2 = CreateItem("Document SCP-035", "paper", x, y, z)
							;[End Block]
						Case 5
							;[Block]
							it2 = CreateItem("Document SCP-049", "paper", x, y, z)
							;[End Block]
						Case 6
							;[Block]
							it2 = CreateItem("Document SCP-096", "paper", x, y, z)
							;[End Block]
						Case 7
							;[Block]
							it2 = CreateItem("Document SCP-106", "paper", x, y, z)
							;[End Block]
						Case 8
							;[Block]
							it2 = CreateItem("Document SCP-173", "paper", x, y, z)
							;[End Block]
						Case 9
							;[Block]
							it2 = CreateItem("Document SCP-205", "paper", x, y, z)
							;[End Block]
						Case 10
							;[Block]
							it2 = CreateItem("Document SCP-409", "paper", x, y, z)
							;[End Block]
						Case 11
							;[Block]
							it2 = CreateItem("Document SCP-513", "paper", x, y, z)
							;[End Block]
						Case 12
							;[Block]
							it2 = CreateItem("Document SCP-682", "paper", x, y, z)
							;[End Block]
						Case 13
							;[Block]
							it2 = CreateItem("Document SCP-714", "paper", x, y, z)
							;[End Block]
						Case 14
							;[Block]
							it2 = CreateItem("Document SCP-860", "paper", x, y, z)
							;[End Block]
						Case 15
							;[Block]
							it2 = CreateItem("Document SCP-860-1", "paper", x, y, z)
							;[End Block]
						Case 16
							;[Block]
							it2 = CreateItem("Document SCP-895", "paper", x, y, z)
							;[End Block]
						Case 17
							;[Block]
							it2 = CreateItem("Document SCP-939", "paper", x, y, z)
							;[End Block]
						Case 18
							;[Block]
							it2 = CreateItem("Document SCP-966", "paper", x, y, z)
							;[End Block]
						Case 19
							;[Block]
							it2 = CreateItem("Document SCP-970", "paper", x, y, z)
							;[End Block]
						Case 20
							;[Block]
							it2 = CreateItem("Document SCP-1048", "paper", x, y, z)
							;[End Block]
						Case 21
							;[Block]
							it2 = CreateItem("Document SCP-1162", "paper", x, y, z)
							;[End Block]
						Case 22
							;[Block]
							it2 = CreateItem("Document SCP-1499", "paper", x, y, z)
							;[End Block]
					End Select
					;[End Block]
			End Select
		Default
			;[Block]
			Select item\ItemTemplate\TempName
				Case "cup"
					;[Block]
					Select Setting
						Case ROUGH, COARSE
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
							d\Size = 0.2
							EntityAlpha(d\OBJ, 0.8)
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case ONETOONE
							;[Block]
							it2 = CreateItem("Cup", "cup", x, y, z)
							it2\Name = item\Name
							it2\R = 255 - item\R
							it2\G = 255 - item\G
							it2\B = 255 - item\B
							;[End Block]
						Case FINE
							;[Block]
							it2 = CreateItem("Cup", "cup", x, y, z)
							it2\Name = item\Name
							it2\State = 1.0
							it2\R = Min(item\R * Rnd(0.9, 1.1), 255)
							it2\G = Min(item\G * Rnd(0.9, 1.1), 255)
							it2\B = Min(item\B * Rnd(0.9, 1.1), 255)
							;[End Block]
						Case VERYFINE
							;[Block]
							it2 = CreateItem("Cup", "cup", x, y, z)
							it2\Name = item\Name
							it2\State = Max(it2\State * 2.0, 2.0)	
							it2\R = Min(item\R * Rnd(0.5, 1.5), 255)
							it2\G = Min(item\G * Rnd(0.5, 1.5), 255)
							it2\B = Min(item\B * Rnd(0.5, 1.5), 255)
							If Rand(5) = 1 Then
								me\ExplosionTimer = 135.0
							EndIf
							;[End Block]
					End Select	
					RemoveItem(item)
					;[End Block]
				Case "paper"
					;[Block]
					Select Setting
						Case ROUGH
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0)
							d\Size = 0.2
							EntityAlpha(d\OBJ, 0.8)
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case COARSE
							;[Block]
							it2 = CreateItem("Blank Paper", "paper", x, y, z)
							;[End Block]
						Case ONETOONE
							;[Block]
							Select Rand(22)
								Case 1
									;[Block]
									it2 = CreateItem("Document SCP-005", "paper", x, y, z)
									;[End Block]
								Case 2
									;[Block]
									it2 = CreateItem("Document SCP-008", "paper", x, y, z)
									;[End Block]
								Case 3
									;[Block]
									it2 = CreateItem("Document SCP-012", "paper", x, y, z)
									;[End Block]
								Case 4
									;[Block]
									it2 = CreateItem("Document SCP-035", "paper", x, y, z)
									;[End Block]
								Case 5
									;[Block]
									it2 = CreateItem("Document SCP-049", "paper", x, y, z)
									;[End Block]
								Case 6
									;[Block]
									it2 = CreateItem("Document SCP-096", "paper", x, y, z)
									;[End Block]
								Case 7
									;[Block]
									it2 = CreateItem("Document SCP-106", "paper", x, y, z)
									;[End Block]
								Case 8
									;[Block]
									it2 = CreateItem("Document SCP-173", "paper", x, y, z)
									;[End Block]
								Case 9
									;[Block]
									it2 = CreateItem("Document SCP-205", "paper", x, y, z)
									;[End Block]
								Case 10
									;[Block]
									it2 = CreateItem("Document SCP-409", "paper", x, y, z)
									;[End Block]
								Case 11
									;[Block]
									it2 = CreateItem("Document SCP-513", "paper", x, y, z)
									;[End Block]
								Case 12
									;[Block]
									it2 = CreateItem("Document SCP-682", "paper", x, y, z)
									;[End Block]
								Case 13
									;[Block]
									it2 = CreateItem("Document SCP-714", "paper", x, y, z)
									;[End Block]
								Case 14
									;[Block]
									it2 = CreateItem("Document SCP-860", "paper", x, y, z)
									;[End Block]
								Case 15
									;[Block]
									it2 = CreateItem("Document SCP-860-1", "paper", x, y, z)
									;[End Block]
								Case 16
									;[Block]
									it2 = CreateItem("Document SCP-895", "paper", x, y, z)
									;[End Block]
								Case 17
									;[Block]
									it2 = CreateItem("Document SCP-939", "paper", x, y, z)
									;[End Block]
								Case 18
									;[Block]
									it2 = CreateItem("Document SCP-966", "paper", x, y, z)
									;[End Block]
								Case 19
									;[Block]
									it2 = CreateItem("Document SCP-970", "paper", x, y, z)
									;[End Block]
								Case 20
									;[Block]
									it2 = CreateItem("Document SCP-1048", "paper", x, y, z)
									;[End Block]
								Case 21
									;[Block]
									it2 = CreateItem("Document SCP-1162", "paper", x, y, z)
									;[End Block]
								Case 22
									;[Block]
									it2 = CreateItem("Document SCP-1499", "paper", x, y, z)
									;[End Block]
							End Select
							;[End Block]
						Case FINE, VERYFINE
							;[Block]
							it2 = CreateItem("Origami", "misc", x, y, z)
							;[End Block]
					End Select
					RemoveItem(item)
					;[End Block]
				Default
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)	
					;[End Block]
			End Select
	End Select
	
	If it2 <> Null Then EntityType(it2\Collider, HIT_ITEM)
End Function

Function Use294()
	Local x#, y#, xTemp%, yTemp%, StrTemp$, Temp%
	Local Sep1%, Sep2%, Alpha#, Glow%
	Local R%, G%, B%
	
	ShowPointer()
	
	x = GraphicWidth / 2 - (ImageWidth(tt\ImageID[5]) / 2)
	y = GraphicHeight / 2 - (ImageHeight(tt\ImageID[5]) / 2)
	DrawImage(tt\ImageID[5], x, y)
	If DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	
	Temp = True
	If PlayerRoom\SoundCHN <> 0 Then Temp = False
	
	Text(x + 907, y + 185, I_294\ToInput, True, True)
	
	If Temp Then
		If MouseHit1 Then
			xTemp = Floor((ScaledMouseX() - x - 228) / 35.5)
			yTemp = Floor((ScaledMouseY() - y - 342) / 36.5)
			
			If yTemp >= 0 And yTemp < 5 Then
				If xTemp >= 0 And xTemp < 10 Then PlaySound_Strict(ButtonSFX)
			EndIf
			
			StrTemp = ""
			
			Temp = False
			
			Select yTemp
				Case 0
					;[Block]
					StrTemp = (xTemp + 1) Mod 10
					;[End Block]
				Case 1
					;[Block]
					Select xTemp
						Case 0
							;[Block]
							StrTemp = "Q"
							;[End Block]
						Case 1
							;[Block]
							StrTemp = "W"
							;[End Block]
						Case 2
							;[Block]
							StrTemp = "E"
							;[End Block]
						Case 3
							;[Block]
							StrTemp = "R"
							;[End Block]
						Case 4
							;[Block]
							StrTemp = "T"
							;[End Block]
						Case 5
							;[Block]
							StrTemp = "Y"
							;[End Block]
						Case 6
							;[Block]
							StrTemp = "U"
							;[End Block]
						Case 7
							;[Block]
							StrTemp = "I"
							;[End Block]
						Case 8
							;[Block]
							StrTemp = "O"
							;[End Block]
						Case 9
							;[Block]
							StrTemp = "P"
							;[End Block]
					End Select
					;[End Block]
				Case 2
					;[Block]
					Select xTemp
						Case 0
							;[Block]
							StrTemp = "A"
							;[End Block]
						Case 1
							;[Block]
							StrTemp = "S"
							;[End Block]
						Case 2
							;[Block]
							StrTemp = "D"
							;[End Block]
						Case 3
							;[Block]
							StrTemp = "F"
							;[End Block]
						Case 4
							;[Block]
							StrTemp = "G"
							;[End Block]
						Case 5
							;[Block]
							StrTemp = "H"
							;[End Block]
						Case 6
							;[Block]
							StrTemp = "J"
							;[End Block]
						Case 7
							;[Block]
							StrTemp = "K"
							;[End Block]
						Case 8
							;[Block]
							StrTemp = "L"
							;[End Block]
						Case 9 ; ~ Dispense
							;[Block]
							Temp = True
							;[End Block]
					End Select
				Case 3
					;[Block]
					Select xTemp
						Case 0
							;[Block]
							StrTemp = "Z"
							;[End Block]
						Case 1
							;[Block]
							StrTemp = "X"
							;[End Block]
						Case 2
							;[Block]
							StrTemp = "C"
							;[End Block]
						Case 3
							;[Block]
							StrTemp = "V"
							;[End Block]
						Case 4
							;[Block]
							StrTemp = "B"
							;[End Block]
						Case 5
							;[Block]
							StrTemp = "N"
							;[End Block]
						Case 6
							;[Block]
							StrTemp = "M"
							;[End Block]
						Case 7
							;[Block]
							StrTemp = "-"
							;[End Block]
						Case 8
							;[Block]
							StrTemp = " "
							;[End Block]
						Case 9
							;[Block]
							I_294\ToInput = Left(I_294\ToInput, Max(Len(I_294\ToInput) - 1, 0))
							;[End Block]
					End Select
				Case 4
					;[Block]
					StrTemp = " "
					;[End Block]
			End Select
			
			I_294\ToInput = I_294\ToInput + StrTemp
			
			I_294\ToInput = Left(I_294\ToInput, Min(Len(I_294\ToInput), 15))
			
			If Temp And I_294\ToInput <> "" Then ; ~ Dispense
				I_294\ToInput = Trim(Lower(I_294\ToInput))
				If Left(I_294\ToInput, Min(7, Len(I_294\ToInput))) = "cup of " Then
					I_294\ToInput = Right(I_294\ToInput, Len(I_294\ToInput) - 7)
				ElseIf Left(I_294\ToInput, Min(9, Len(I_294\ToInput))) = "a cup of " 
					I_294\ToInput = Right(I_294\ToInput, Len(I_294\ToInput) - 9)
				EndIf
				
				If I_294\ToInput <> ""
					Local Loc% = GetINISectionLocation("Data\SCP-294.ini", I_294\ToInput)
				EndIf
				
				If Loc > 0 Then
					StrTemp = GetINIString2("Data\SCP-294.ini", Loc, "dispensesound")
					If StrTemp = "" Then
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound(StrTemp))
					EndIf
					
					If GetINIInt2("Data\SCP-294.ini", Loc, "explosion") = True Then 
						me\ExplosionTimer = 135.0
						msg\DeathMsg = GetINIString2("Data\SCP-294.ini", Loc, "deathmessage")
					EndIf
					
					StrTemp = GetINIString2("Data\SCP-294.ini", Loc, "color")
					
					Sep1 = Instr(StrTemp, ",", 1)
					Sep2 = Instr(StrTemp, ",", Sep1 + 1)
					R = Trim(Left(StrTemp, Sep1 - 1))
					G = Trim(Mid(StrTemp, Sep1 + 1, Sep2 - Sep1 - 1))
					B = Trim(Right(StrTemp, Len(StrTemp) - Sep2))
					
					Alpha = Float(GetINIString2("Data\SCP-294.ini", Loc, "alpha", 1.0))
					Glow = GetINIInt2("Data\SCP-294.ini", Loc, "glow")
					If Glow Then Alpha = -Alpha
					
					it.Items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1], True), EntityY(PlayerRoom\Objects[1], True), EntityZ(PlayerRoom\Objects[1], True), R, G, B, Alpha)
					it\Name = "Cup of " + I_294\ToInput
					EntityType(it\Collider, HIT_ITEM)
				Else
					; ~ Out of range
					I_294\ToInput = "OUT OF RANGE"
					PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\OutOfRange.ogg"))
				EndIf
			EndIf	
		EndIf
		
		If MouseHit2 Lor (Not I_294\Using) Then 
			HidePointer()
			I_294\Using = False
			I_294\ToInput = ""
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	Else ; ~ Playing a dispensing sound
		If I_294\ToInput <> "OUT OF RANGE" Then I_294\ToInput = "DISPENSING..."
		
		If (Not ChannelPlaying(PlayerRoom\SoundCHN)) Then
			If I_294\ToInput <> "OUT OF RANGE" Then
				HidePointer()
				I_294\Using = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				
				Local e.Events
				
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0.0
						Exit
					EndIf
				Next
			EndIf
			I_294\ToInput = ""
			PlayerRoom\SoundCHN = 0
		EndIf
	EndIf
End Function

Function Use427()
	Local i%, Pvt%, de.Decals, TempCHN%
	Local PrevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70.0 * 360.0
		If I_427\Using = 1 Then
			I_427\Timer = I_427\Timer + fpst\FPSFactor[0]
			For e.Events = Each Events
				If e\EventName = "1048a" Then
					If e\EventState2 > 0.0 Then
						e\EventState2 = Max(e\EventState2 - (fpst\FPSFactor[0] * 0.5), 0.0)
					EndIf
				EndIf
			Next
			If me\Injuries > 0.0 Then
				me\Injuries = Max(me\Injuries - (fpst\FPSFactor[0] * 0.0005), 0.0)
			EndIf
			If me\Bloodloss > 0.0 And me\Injuries =< 1.0 Then
				me\Bloodloss = Max(me\Bloodloss - (fpst\FPSFactor[0] * 0.001), 0.0)
			EndIf
			If I_008\Timer > 0.0 Then
				I_008\Timer = Max(I_008\Timer - (fpst\FPSFactor[0] * 0.001), 0.0)
			EndIf
			If I_409\Timer > 0.0 Then
				I_409\Timer = Max(I_409\Timer - (fpst\FPSFactor[0] * 0.003), 0.0)
			EndIf
			For i = 0 To 5
				If I_1025\State[i] > 0.0 Then
					I_1025\State[i] = Max(I_1025\State[i] - (fpst\FPSFactor[0] * 0.001), 0.0)
				EndIf
			Next
			If I_427\Sound[0] = 0 Then
				I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			EndIf
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer >= 70.0 * 180.0 Then
				If I_427\Sound[1] = 0 Then I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
			EndIf
			If PrevI427Timer < 70.0 * 60.0 And I_427\Timer >= 70.0 * 60.0 Then
				msg\Msg = "You feel refreshed and energetic."
				msg\Timer = 70.0 * 6.0
			ElseIf PrevI427Timer < 70.0 * 180.0 And I_427\Timer >= 70.0 * 180.0 Then
				msg\Msg = "You feel gentle muscle spasms all over your body."
				msg\Timer = 70.0 * 6.0
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i] <> 0 Then If ChannelPlaying(I_427\SoundCHN[i]) Then StopChannel(I_427\SoundCHN[i])
			Next
		EndIf
	Else
		If PrevI427Timer - fpst\FPSFactor[0] < 70.0 * 360.0 And I_427\Timer >= 70.0 * 360.0 Then
			msg\Msg = "Your muscles are swelling. You feel more powerful than ever."
			msg\Timer = 70.0 * 6.0
		ElseIf PrevI427Timer - fpst\FPSFactor[0] < 70.0 * 390.0 And I_427\Timer >= 70.0 * 390.0 Then
			msg\Msg = "You can't feel your legs. But you don't need legs anymore."
			msg\Timer = 70.0 * 6.0
		EndIf
		I_427\Timer = I_427\Timer + fpst\FPSFactor[0]
		If I_427\Sound[0] = 0 Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If I_427\Sound[1] = 0 Then
			I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		EndIf
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
		Next
		If Rnd(200) < 2.0 Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(me\Collider) + Rnd(-0.05, 0.05), EntityY(me\Collider) - 0.05, EntityZ(me\Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(17, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rand(360.0), 0.0)
			de\Size = Rnd(0.03, 0.08) * 2.0
			EntityAlpha(de\OBJ, 1.0)
			ScaleSprite(de\OBJ, de\Size, de\Size)
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 2)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			FreeEntity(Pvt)
			me\BlurTimer = 800.0
		EndIf
		If I_427\Timer >= 70.0 * 420.0 Then
			Kill()
			msg\DeathMsg = Chr(34) + "Requesting support from MTF Nu-7. We need more firepower to take this thing down." + Chr(34)
		ElseIf I_427\Timer >= 70.0 * 390.0 Then
			If (Not me\Crouch) Then SetCrouch(True)
		EndIf
	EndIf
End Function

Function UpdateMTF()
	If PlayerRoom\RoomTemplate\Name = "gateaentrance" Then Return
	
	Local r.Rooms, n.NPCs
	Local Dist#, i%
	
	If MTFTimer = 0.0 Then
		If Rand(30) = 1 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then
			
			Local entrance.Rooms = Null
			
			For r.Rooms = Each Rooms
				If Lower(r\RoomTemplate\Name) = "gateaentrance" Then entrance = r : Exit
			Next
			
			If entrance <> Null Then 
				If me\Zone = 2 Then
					If Abs(EntityZ(entrance\OBJ) - EntityZ(me\Collider)) < 30.0 Then
						If PlayerInReachableRoom()
							PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
						EndIf
						
						MTFTimer = fpst\FPSFactor[0]
						
						Local leader.NPCs
						
						For i = 0 To 2
							n.NPCs = CreateNPC(NPCtypeMTF, EntityX(entrance\OBJ) + 0.3 * (i - 1), 0.6, EntityZ(entrance\OBJ) + 8.0)
							
							If i = 0 Then 
								leader = n
							Else
								n\MTFLeader = leader
							EndIf
							
							n\PrevX = i
						Next
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		If MTFTimer =< 70.0 * 120.0 Then
			MTFTimer = MTFTimer + fpst\FPSFactor[0]
		ElseIf MTFTimer > 70.0 * 120.0 And MTFTimer < 10000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			EndIf
			MTFTimer = 10000.0
		ElseIf MTFTimer >= 10000.0 And MTFTimer =< 10000.0 + (70.0 * 120.0)
			MTFTimer = MTFTimer + fpst\FPSFactor[0]
		ElseIf MTFTimer > 10000.0 + (70.0 * 120.0) And MTFTimer < 20000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			EndIf
			MTFTimer = 20000.0
		ElseIf MTFTimer >= 20000.0 And MTFTimer =< 20000.0 + (70.0 * 60.0)
			MTFTimer = MTFTimer + fpst\FPSFactor[0]
		ElseIf MTFTimer > 20000.0 + (70.0 * 60.0) And MTFTimer < 25000.0
			If PlayerInReachableRoom()
				; ~ If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount - 1
					If Inventory[i] <> Null Then
						If (Left(Inventory[i]\ItemTempLate\Name, 4) = "SCP-") And (Left(Inventory[i]\ItemTemplate\Name, 7) <> "SCP-035") And (Left(Inventory[i]\ItemTemplate\Name, 7) <> "SCP-093")
							PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncPossession.ogg")
							MTFTimer = 25000.0
							Return
							Exit
						EndIf
					EndIf
				Next
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnounc" + Rand(1, 3) + ".ogg")
			EndIf
			MTFTimer = 25000.0
		ElseIf MTFTimer >= 25000.0 And MTFTimer =< 25000.0 + (70.0 * 60.0)
			MTFTimer = MTFTimer + fpst\FPSFactor[0]
		ElseIf MTFTimer > 25000.0 + (70.0 * 60.0) And MTFTimer < 30000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			EndIf
			MTFTimer = 30000.0
		EndIf
	EndIf
End Function

Function UpdateExplosion()
	Local i%, p.Particles
	
	; ~ This here is necessary because the SCP-294's drinks with explosion effect didn't worked anymore -- ENDSHN
	If me\ExplosionTimer > 0.0 Then
		me\ExplosionTimer = me\ExplosionTimer + fpst\FPSFactor[0]
		If me\ExplosionTimer < 140.0 Then
			If me\ExplosionTimer - fpst\FPSFactor[0] < 5.0 Then
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke1.ogg")
				PlaySound_Strict(ExplosionSFX)
				me\CameraShake = 10.0
				me\ExplosionTimer = 5.0
			EndIf
			me\CameraShake = CurveValue(me\ExplosionTimer / 60.0, me\CameraShake, 50.0)
		Else
			me\CameraShake = Min((me\ExplosionTimer / 20.0), 20.0)
			If me\ExplosionTimer - fpst\FPSFactor[0] < 140.0 Then
				me\BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke2.ogg")
				PlaySound_Strict(ExplosionSFX)				
				For i = 0 To (10 + (10 * (ParticleAmount + 1)))
					p.Particles = CreateParticle(EntityX(me\Collider) + Rnd(-0.5, 0.5), EntityY(me\Collider) - Rnd(0.2, 1.5), EntityZ(me\Collider) + Rnd(-0.5, 0.5), 0, Rnd(0.2, 0.6), 0.0, 350.0)	
					RotateEntity(p\Pvt, -90.0, 0.0, 0.0, True)
					p\Speed = Rnd(0.05, 0.07)
				Next
			EndIf
			me\LightFlash = Min((me\ExplosionTimer - 140.0) / 10.0, 5.0)
			
			If me\ExplosionTimer > 160.0 Then me\KillTimer = Min(me\KillTimer, -0.1)
			If me\ExplosionTimer > 500.0 Then me\ExplosionTimer = 0.0
			
			; ~ A dirty workaround to prevent the collider from falling down into the facility once the nuke goes off, causing the UpdateEvents() function to be called again and crashing the game
			PositionEntity(me\Collider, EntityX(me\Collider), 200.0, EntityZ(me\Collider))
		EndIf
	EndIf
End Function

Function Update008()
	Local PrevI008Timer#, i%, r.Rooms, e.Events
	Local TeleportForInfect% = True
	Local GroupDesignation$
	
	If PlayerRoom\RoomTemplate\Name = "room860"
		For e.Events = Each Events
			If e\EventName = "room860"
				If e\EventState = 1.0
					TeleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Lor PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor PlayerRoom\RoomTemplate\Name = "gatea"
		TeleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale
		TeleportForInfect = False
	EndIf
	
	If I_008\Timer > 0.0 Then
		ShowEntity(tt\OverlayID[3])
		If I_008\Timer < 93.0 Then
			PrevI008Timer = I_008\Timer
			If I_427\Using = 0 And I_427\Timer < 70.0 * 360.0 Then
				I_008\Timer = Min(I_008\Timer + (fpst\FPSFactor[0] * 0.002), 100.0)
			EndIf
			
			me\BlurTimer = Max(I_008\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
			
			me\HeartBeatRate = Max(me\HeartBeatRate, 100.0)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, I_008\Timer / 120.0)
			
			EntityAlpha(tt\OverlayID[3], Min(((I_008\Timer * 0.2) ^ 2.0) / 1000.0, 0.5) * (Sin(MilliSecs() / 8.0) + 2.0))
			
			For i = 0 To 6
				If I_008\Timer > i * 15.0 + 10.0 And PrevI008Timer =< i * 15.0 + 10.0 Then
					PlaySound_Strict(LoadTempSound("SFX\SCP\008\Voices" + i + ".ogg"))
				EndIf
			Next
			
			If I_008\Timer > 20.0 And PrevI008Timer =< 20.0 Then
				msg\Msg = "You feel kinda feverish."
				msg\Timer = 70.0 * 6.0
			ElseIf I_008\Timer > 40.0 And PrevI008Timer =< 40.0
				msg\Msg = "You feel nauseated."
				msg\Timer = 70.0 * 6.0
			ElseIf I_008\Timer > 60.0 And PrevI008Timer =< 60.0
				msg\Msg = "The nausea's getting worse."
				msg\Timer = 70.0 * 6.0
			ElseIf I_008\Timer > 80.0 And PrevI008Timer =< 80.0
				msg\Msg = "You feel very faint."
				msg\Timer = 70.0 * 6.0
			ElseIf I_008\Timer >= 91.5
				me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 91.5), me\BlinkTimer), -10.0)
				me\Zombie = True
				UnableToMove = True
				If I_008\Timer >= 92.7 And PrevI008Timer < 92.7 Then
					If TeleportForInfect Then
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name = "room008" Then
								PositionEntity(me\Collider, EntityX(r\Objects[7], True), EntityY(r\Objects[7], True), EntityZ(r\Objects[7], True), True)
								ResetEntity(me\Collider)
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6], True), EntityY(r\Objects[6], True) + 0.2, EntityZ(r\Objects[6], True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundCHN = PlaySound_Strict(r\NPC[0]\Sound)
								ChangeNPCTextureID(r\NPC[0], 12)
								r\NPC[0]\State = 6.0
								PlayerRoom = r
								UnableToMove = False
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			PrevI008Timer = I_008\Timer
			I_008\Timer = Min(I_008\Timer + (fpst\FPSFactor[0] * 0.004), 100.0)
			
			If TeleportForInfect Then
				If I_008\Timer < 94.7 Then
					EntityAlpha(tt\OverlayID[3], 0.5 * (Sin(MilliSecs() / 8.0) + 2.0))
					me\BlurTimer = 900.0
					
					If I_008\Timer > 94.5 Then me\BlinkTimer = Max(Min((-50.0) * (I_008\Timer - 94.5), me\BlinkTimer), -10.0)
					PointEntity(me\Collider, PlayerRoom\NPC[0]\Collider)
					PointEntity(PlayerRoom\NPC[0]\Collider, me\Collider)
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider, EntityRoll(Camera))
					me\ForceMove = 0.75
					me\Injuries = 2.5
					me\Bloodloss = 0.0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 357.0, 381.0, 0.3)
				ElseIf I_008\Timer < 98.5
					EntityAlpha(tt\OverlayID[3], 0.5 * (Sin(MilliSecs() / 5.0) + 2.0))
					me\BlurTimer = 950.0
					
					me\ForceMove = 0.0
					UnableToMove = True
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider)
					
					If PrevI008Timer < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundCHN = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						If Rand(2) = 1 Then
							GroupDesignation = "Nine-Tailed Fox"
						Else
							GroupDesignation = "See No Evil"
						EndIf
						msg\DeathMsg = SubjectName + " found ingesting Dr. [DATA REDACTED] at Sector [DATA REDACTED]. Subject was immediately terminated by " + GroupDesignation + " and sent for autopsy. "
						msg\DeathMsg = msg\DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544.0 * RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider), 90.0, Rnd(360.0), 0.0)
						de\Size = 0.8
						ScaleSprite(de\OBJ, de\Size, de\Size)
					ElseIf I_008\Timer > 96.0
						me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 96.0), me\BlinkTimer), -10.0)
					Else
						me\KillTimer = Max(-350.0, me\KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2 = 0.0 Then
						Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 13.0, 19.0, 0.3, False)
						If AnimTime(PlayerRoom\NPC[0]\OBJ) >= 19.0 Then PlayerRoom\NPC[0]\State2 = 1.0
					Else
						Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 19.0, 13.0, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\OBJ) =< 13.0 Then PlayerRoom\NPC[0]\State2 = 0.0
					EndIf
					
					If ParticleAmount > 0 Then
						If Rand(50) = 1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider), EntityY(PlayerRoom\NPC[0]\Collider), EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05, 0.1), 0.15, 200)
							p\Speed = 0.01 : p\SizeChange = 0.01 : p\A = 0.5 : p\Achange = -0.01
							RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
						EndIf
					EndIf
					
					PositionEntity(me\Head, EntityX(PlayerRoom\NPC[0]\Collider, True), EntityY(PlayerRoom\NPC[0]\Collider, True) + 0.65, EntityZ(PlayerRoom\NPC[0]\Collider, True), True)
					RotateEntity(me\Head, (1.0 + Sin(MilliSecs() / 5.0)) * 15.0, PlayerRoom\Angle - 180.0, 0.0, True)
					MoveEntity(me\Head, 0.0, 0.0, -0.4)
					TurnEntity(me\Head, 80.0 + (Sin(MilliSecs() / 5.0)) * 30.0, (Sin(MilliSecs() / 5.0)) * 40.0, 0.0)
				EndIf
			Else
				Kill()
				me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 96.0), me\BlinkTimer), -10.0)
				If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
					msg\DeathMsg = "The whereabouts of SCP-1499 are still unknown, but a recon team has been dispatched to investigate reports of a violent attack to a church in the Russian town of [DATA REDACTED]."
				ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Lor (PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(me\Collider) > 1040.0 * RoomScale) Then
					msg\DeathMsg = SubjectName + " found wandering around Gate "
					If PlayerRoom\RoomTemplate\Name = "gatea" Then
						msg\DeathMsg = msg\DeathMsg + "A"
					Else
						msg\DeathMsg = msg\DeathMsg + "B"
					EndIf
					If Rand(2) = 1 Then
						GroupDesignation = "Nine-Tailed Fox"
					Else
						GroupDesignation = "See No Evil"
					EndIf
					msg\DeathMsg = msg\DeathMsg + ". Subject was immediately terminated by " + GroupDesignation + " and sent for autopsy. "
					msg\DeathMsg = msg\DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
				Else
					msg\DeathMsg = ""
				EndIf
			EndIf
		EndIf
	Else
		HideEntity(tt\OverlayID[3])
	EndIf
End Function

Function Update409()
	Local PrevI409Timer# = I_409\Timer
	
	If I_409\Timer > 0.0 Then
		ShowEntity(tt\OverlayID[8])
		
		If I_427\Using = 0 And I_427\Timer < 70.0 * 360.0 Then
			I_409\Timer = Min(I_409\Timer + (fpst\FPSFactor[0] * 0.004), 100.0)
		EndIf	
		EntityAlpha(tt\OverlayID[8], Min(((I_409\Timer * 0.2) ^ 2.0) / 1000.0, 0.5))
	    me\BlurTimer = Max(I_409\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
		
        If I_409\Timer > 40.0 And PrevI409Timer =< 40.0 Then
			msg\Msg = "Crystals are enveloping the skin on your legs."
			msg\Timer = 70.0 * 6.0
		ElseIf I_409\Timer > 55.0 And PrevI409Timer =< 55.0 Then
			msg\Msg = "Crystals are up to your abdomen."
			msg\Timer = 70.0 * 6.0
		ElseIf I_409\Timer > 70.0 And PrevI409Timer =< 70.0 Then
			msg\Msg = "Crystals are starting to envelop your arms."
			msg\Timer = 70.0 * 6.0
		ElseIf I_409\Timer > 85.0 And PrevI409Timer =< 85.0 Then
			msg\Msg = "Crystals starting to envelop your head."
			msg\Timer = 70.0 * 6.0
		ElseIf I_409\Timer > 93.0 And PrevI409Timer =< 93.0 Then
			PlaySound_Strict(DamageSFX[13])
			me\Injuries = Max(me\Injuries, 2.0)
		ElseIf I_409\Timer > 94.0 Then
			I_409\Timer = Min(I_409\Timer + fpst\FPSFactor[0] * 0.004, 100.0)
			me\Playable = False
			me\BlurTimer = 4.0
			me\CameraShake = 3.0
		EndIf
		If I_409\Timer >= 96.9222 Then
			msg\DeathMsg = "Pile of SCP-409 crystals found and, by comparing list of the dead, was found to be " + SubjectName + " who had physical contact with SCP-409. "
			msg\DeathMsg = msg\DeathMsg + "Remains were incinerated along with crystal-infested areas of facility."
			Kill(True)
        EndIf
    Else
		HideEntity(tt\OverlayID[8])	
    EndIf
End Function

Type Decals
	Field OBJ%
	Field SizeChange#, Size#, MaxSize#
	Field AlphaChange#, Alpha#
	Field BlendMode%
	Field FX%
	Field ID%
	Field Timer#
	Field LifeTime#
	Field x#, y#, z#
	Field Pitch#, Yaw#, Roll#
End Type

Function CreateDecal.Decals(ID%, x#, y#, z#, Pitch#, Yaw#, Roll#)
	Local d.Decals = New Decals
	
	d\x = x
	d\y = y
	d\z = z
	d\Pitch = Pitch
	d\Yaw = Yaw
	d\Roll = Roll
	
	d\MaxSize = 1.0
	
	d\Alpha = 1.0
	d\Size = 1.0
	d\OBJ = CreateSprite()
	d\BlendMode = 1
	
	EntityTexture(d\OBJ, tt\DecalTextureID[ID])
	EntityFX(d\OBJ, 0)
	SpriteViewMode(d\OBJ, 2)
	PositionEntity(d\OBJ, x, y, z)
	RotateEntity(d\OBJ, Pitch, Yaw, Roll)
	
	d\ID = ID
	
	If tt\DecalTextureID[ID] = 0 Lor d\OBJ = 0 Then Return(Null)
	
	Return(d)
End Function

Function UpdateDecals()
	Local d.Decals
	
	For d.Decals = Each Decals
		If d\SizeChange <> 0.0 Then
			d\Size = d\Size + d\SizeChange * fpst\FPSFactor[0]
			ScaleSprite(d\OBJ, d\Size, d\Size)
			
			Select d\ID
				Case 0
					;[Block]
					If d\Timer =< 0.0 Then
						Local Angle# = Rnd(360.0)
						Local Temp# = Rnd(d\Size)
						Local d2.Decals = CreateDecal(1, EntityX(d\OBJ) + Cos(Angle) * Temp, EntityY(d\OBJ) - 0.0005, EntityZ(d\OBJ) + Sin(Angle) * Temp, EntityPitch(d\OBJ), Rnd(360.0), EntityRoll(d\OBJ))
						
						d2\Size = Rnd(0.1, 0.5) : ScaleSprite(d2\OBJ, d2\Size, d2\Size)
						PlaySound2(DecaySFX[Rand(1, 3)], Camera, d2\OBJ, 10.0, Rnd(0.1, 0.5))
						d\Timer = Rnd(50.0, 100.0)
					Else
						d\Timer = d\Timer - fpst\FPSFactor[0]
					EndIf
					;[End Block]
			End Select
			
			If d\Size >= d\MaxSize Then d\SizeChange = 0.0 : d\Size = d\MaxSize
		EndIf
		
		If d\AlphaChange <> 0.0 Then
			d\Alpha = Min(d\Alpha + fpst\FPSFactor[0] * d\AlphaChange, 1.0)
			EntityAlpha(d\OBJ, d\Alpha)
		EndIf
		
		If d\LifeTime > 0.0 Then
			d\LifeTime = Max(d\LifeTime - fpst\FPSFactor[0], 5.0)
		EndIf
		
		If d\Size =< 0.0 Lor d\Alpha =< 0.0 Lor d\LifeTime = 5.0  Then
			FreeEntity(d\OBJ)
			
			Delete(d)
		EndIf
	Next
End Function

Function Graphics3DExt%(Width%, Height%, Mode% = 2)
	Graphics3D(Width, Height, 32, Mode)
	TextureFilter("", 8192) ; ~ This turns on Anisotropic filtering for textures
	TextureAnisotropic(16)
	InitFastResize()
	AntiAlias(Opt_AntiAlias)
End Function

Function ResizeImage2(Image%, Width%, Height%)
    Local Img% = CreateImage(Width, Height)
	Local OldWidth% = ImageWidth(Image)
	Local OldHeight% = ImageHeight(Image)
	
	CopyRect(0, 0, OldWidth, OldHeight, 1024 - OldWidth / 2, 1024 - OldHeight / 2, ImageBuffer(Image), TextureBuffer(Fresize_Texture))
	SetBuffer(BackBuffer())
	ScaleRender(0.0, 0.0, 2048.0 / Float(RealGraphicWidth) * Float(Width) / Float(OldWidth), 2048.0 / Float(RealGraphicWidth) * Float(Height) / Float(OldHeight))
	; ~ Might want to replace Float(GraphicWidth) with Max(GraphicWidth, GraphicHeight) if portrait sizes cause issues
	; ~ Everyone uses landscape so it's probably a non-issue
	CopyRect(RealGraphicWidth / 2 - Width / 2, RealGraphicHeight / 2 - Height / 2, Width, Height, 0, 0, BackBuffer(), ImageBuffer(Img))
	FreeImage(Image)
	Return(Img)
End Function

Function RenderWorld2(Tween#)
	Local i%, Dist#, Temp%, Temp2%
	Local l%, k%, xValue#, yValue#, PitchValue#, YawValue#
	
	CameraProjMode(ArkBlurCam, 0)
	CameraProjMode(Camera, 1)
	
	If wi\NightVision > 0 And wi\NightVision < 3 Then
		AmbientLight(Min(Brightness * 2.0, 255.0), Min(Brightness * 2.0, 255.0), Min(Brightness * 2.0, 255.0))
	ElseIf wi\NightVision = 3
		AmbientLight(255.0, 255.0, 255.0)
	ElseIf PlayerRoom <> Null
		If (PlayerRoom\RoomTemplate\Name <> "room173intro") And (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(me\Collider) =< 1040.0 * RoomScale) And (PlayerRoom\RoomTemplate\Name <> "gatea") Then
			AmbientLight(Brightness, Brightness, Brightness)
		EndIf
	EndIf
	
	wi\IsNVGBlinking = False
	HideEntity(tt\OverlayID[5])
	
	CameraViewport(Camera, 0, 0, GraphicWidth, GraphicHeight)
	
	Local HasBattery% = 2
	Local Power% = 0
	
	If wi\NightVision = 1 Lor wi\NightVision = 2 Lor wi\SCRAMBLE > 0 Then
		For i = 0 To MaxItemAmount - 1
			If Inventory[i] <> Null Then
				If (wi\NightVision = 1 And Inventory[i]\ItemTemplate\TempName = "nvg") Lor (wi\NightVision = 2 And Inventory[i]\ItemTemplate\TempName = "supernvg") Lor (wi\SCRAMBLE > 0 And Inventory[i]\ItemTemplate\TempName = "scramble") Then
					Inventory[i]\State = Inventory[i]\State - (fpst\FPSFactor[0] * (0.02 * wi\NightVision) + (0.25 * (wi\SCRAMBLE > 0)))
					Power = Int(Inventory[i]\State)
					If Power =< 0.0 Then ; ~ This NVG can't be used
						HasBattery = 0
						If wi\SCRAMBLE > 0 Then
							msg\Msg = "The batteries in this gear died."
						Else
							msg\Msg = "The batteries in these night vision goggles died."
						EndIf	
						msg\Timer = 70 * 5.0
						me\BlinkTimer = -1.0
						Exit
					ElseIf Power =< 100.0 Then
						HasBattery = 1
					EndIf
				EndIf
			EndIf
		Next
		If HasBattery Then
			RenderWorld()
		EndIf
	Else
		RenderWorld(Tween)
	EndIf
	
	CurrTrisAmount = TrisRendered()

	If HasBattery = 0 And wi\NightVision <> 3 Then
		wi\IsNVGBlinking = True
		ShowEntity(tt\OverlayID[5])
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
	
	If me\BlinkTimer < -16.0 Lor me\BlinkTimer > -6.0 Then
		If HasBattery <> 0 And (wi\NightVision = 1 Lor wi\NightVision = 2 Lor wi\SCRAMBLE > 0) Then
			If wi\NightVision = 2 Then ; ~ Show a HUD
				wi\NVGTimer = wi\NVGTimer - fpst\FPSFactor[0]
				If wi\NVGTimer =< 0.0 Then
					For np.NPCs = Each NPCs
						np\NVX = EntityX(np\Collider, True)
						np\NVY = EntityY(np\Collider, True)
						np\NVZ = EntityZ(np\Collider, True)
					Next
					wi\IsNVGBlinking = True
					ShowEntity(tt\OverlayID[5])
					If wi\NVGTimer =< -10.0 Then
						wi\NVGTimer = 600.0
					EndIf
				EndIf
				
				Color(255, 255, 255)
				
				SetFont(fo\FontID[2])
				
				Local PlusY% = 0
				
				If HasBattery = 1 Then PlusY% = 40
				
				Text(GraphicWidth / 2, (20 + PlusY) * MenuScale, "REFRESHING DATA IN", True, False)
				
				Text(GraphicWidth / 2, (60 + PlusY) * MenuScale, Max(f2s(wi\NVGTimer / 60.0, 1), 0.0), True, False)
				Text(GraphicWidth / 2, (100 + PlusY) * MenuScale, "SECONDS", True, False)
				
				Temp = CreatePivot() : Temp2 = CreatePivot()
				PositionEntity(Temp, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
				
				Color(255, 255, 255)
				
				For np.NPCs = Each NPCs
					If np\NVName <> "" And (Not np\HideFromNVG) Then ; ~ Don't waste your time if the string is empty
						PositionEntity(Temp2, np\NVX, np\NVY, np\NVZ)
						Dist = EntityDistance(Temp2, me\Collider)
						If Dist < 23.5 Then ; ~ Don't draw text if the NPC is too far away
							PointEntity(Temp, Temp2)
							YawValue = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
							xValue = 0.0
							If YawValue > 90.0 And YawValue =< 180.0 Then
								xValue = Sin(90.0) / 90.0 * YawValue
							ElseIf YawValue > 180.0 And YawValue < 270.0 Then
								xValue = Sin(270.0) / YawValue * 270.0
							Else
								xValue = Sin(YawValue)
							EndIf
							PitchValue = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
							yValue = 0.0
							If PitchValue > 90.0 And PitchValue =< 180.0 Then
								yValue = Sin(90.0) / 90.0 * PitchValue
							ElseIf PitchValue > 180.0 And PitchValue < 270.0 Then
								yValue = Sin(270.0) / PitchValue * 270.0
							Else
								yValue = Sin(PitchValue)
							EndIf
							
							If (Not wi\IsNVGBlinking) Then
								Text(GraphicWidth / 2 + xValue * (GraphicWidth / 2), GraphicHeight / 2 - yValue * (GraphicHeight / 2), np\NVName, True, True)
								Text(GraphicWidth / 2 + xValue * (GraphicWidth / 2), GraphicHeight / 2 - yValue * (GraphicHeight / 2) + 30 * MenuScale, f2s(Dist, 1) + " m", True, True)
							EndIf
						EndIf
					EndIf
				Next
				FreeEntity(Temp) : FreeEntity(Temp2)
				
				Color(0, 0, 55)
			ElseIf wi\NightVision = 1
				Color(0, 55, 0)
			Else
				Color(55, 55, 55)
			EndIf
			For k = 0 To 10
				Rect(45, GraphicHeight * 0.5 - (k * 20), 54, 10, True)
			Next
			If wi\NightVision = 2 Then
				Color(0, 0, 255)
			ElseIf wi\NightVision = 1
				Color(0, 255, 0)
			Else
				Color(255, 255, 255)
			EndIf
			For l = 0 To Floor((Power + 50) * 0.01)
				Rect(45, GraphicHeight * 0.5 - (l * 20), 54, 10, True)
			Next
			DrawImage(tt\ImageID[6], 40, GraphicHeight * 0.5 + 30)
		EndIf
	EndIf
	
	If me\BlinkTimer < -16.0 Lor me\BlinkTimer > -6.0 Then
		If (wi\NightVision = 1 Lor wi\NightVision = 2 Lor wi\SCRAMBLE > 0) And (HasBattery = 1) And ((MilliSecs() Mod 800) < 400) Then
			Color(255, 0, 0)
			SetFont(fo\FontID[2])
			
			Text(GraphicWidth / 2, 20 * MenuScale, "WARNING: LOW BATTERY", True, False)
			Color(255, 255, 255)
		EndIf
	EndIf
	
	; ~ Render sprites
	CameraProjMode(ArkBlurCam, 2)
	CameraProjMode(Camera, 0)
	RenderWorld()
	CameraProjMode(ArkBlurCam, 0)
End Function

Function ScaleRender(x#, y#, hScale# = 1.0, vScale# = 1.0)
	If Camera <> 0 Then HideEntity(Camera)
	WireFrame(0)
	ShowEntity(Fresize_Image)
	ScaleEntity(Fresize_Image, hScale, vScale, 1.0)
	PositionEntity(Fresize_Image, x, y, 1.0001)
	ShowEntity(Fresize_Cam)
	RenderWorld()
	HideEntity(Fresize_Cam)
	HideEntity(Fresize_Image)
	WireFrame(WireFrameState)
	If Camera <> 0 Then ShowEntity(Camera)
End Function

Function InitFastResize()
    ; ~ Create Camera
	Local Cam% = CreateCamera()
	
	CameraProjMode(Cam, 2)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0.0, 0.0, -10000)
	
	Fresize_Cam = Cam
	
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
	ScaleEntity(SPR, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1.0)
	PositionEntity(SPR, 0, 0, 1.0001)
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	Fresize_Image = SPR
	
    ; ~ Create texture
	Fresize_Texture = CreateTexture(2048, 2048, 1 + 256)
	Fresize_Texture2 = CreateTexture(2048, 2048, 1 + 256)
	TextureBlend(Fresize_Texture2, 3)
	SetBuffer(TextureBuffer(Fresize_Texture2))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	EntityTexture(SPR, Fresize_Texture, 0, 0)
	EntityTexture(SPR, Fresize_Texture2, 0, 1)
	
	HideEntity(Fresize_Cam)
End Function

Function GammaUpdate()
	If DisplayMode = 1 Then
		If (RealGraphicWidth <> GraphicWidth) Lor (RealGraphicHeight <> GraphicHeight) Then
			SetBuffer(TextureBuffer(Fresize_Texture))
			ClsColor(0, 0, 0) : Cls()
			CopyRect(0, 0, GraphicWidth, GraphicHeight, 1024 - GraphicWidth / 2, 1024 - GraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
			SetBuffer(BackBuffer())
			ClsColor(0, 0, 0) : Cls()
			ScaleRender(0, 0, 2048.0 / Float(GraphicWidth) * AspectRatioRatio, 2048.0 / Float(GraphicWidth) * AspectRatioRatio)
			; ~ Might want to replace Float(GraphicWidth) with Max(GraphicWidth, GraphicHeight) if portrait sizes cause issues
			; ~ Everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If ScreenGamma > 1.0 Then
		CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
		EntityBlend(Fresize_Image, 1)
		ClsColor(0, 0, 0) : Cls()
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		EntityFX(Fresize_Image, 1 + 32)
		EntityBlend(Fresize_Image, 3)
		EntityAlpha(Fresize_Image, ScreenGamma - 1.0)
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
	ElseIf ScreenGamma < 1.0 Then ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
		EntityBlend(Fresize_Image, 1)
		ClsColor(0, 0, 0) : Cls()
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		EntityFX(Fresize_Image, 1 + 32)
		EntityBlend(Fresize_Image, 2)
		EntityAlpha(Fresize_Image, 1.0)
		SetBuffer(TextureBuffer(Fresize_Texture2))
		ClsColor(255 * ScreenGamma, 255 * ScreenGamma, 255 * ScreenGamma)
		Cls()
		SetBuffer(BackBuffer())
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		SetBuffer(TextureBuffer(Fresize_Texture2))
		ClsColor(0, 0, 0)
		Cls()
		SetBuffer(BackBuffer())
	EndIf
	EntityFX(Fresize_Image, 1)
	EntityBlend(Fresize_Image, 1)
	EntityAlpha(Fresize_Image, 1.0)
End Function

Function UpdateLeave1499()
	Local r.Rooms, it.Items, r2.Rooms, i%
	Local r1499.Rooms
	
	If I_1499\Using = 0 And PlayerRoom\RoomTemplate\Name = "dimension1499" Then
		For r.Rooms = Each Rooms
			If r = I_1499\PrevRoom Then
				me\BlinkTimer = -1.0
				I_1499\x = EntityX(me\Collider)
				I_1499\y = EntityY(me\Collider)
				I_1499\z = EntityZ(me\Collider)
				PositionEntity(me\Collider, I_1499\PrevX, I_1499\PrevY + 0.05, I_1499\PrevZ)
				ResetEntity(me\Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(me\Collider) < -4600.0 * RoomScale
						For i = 0 To 3
							PlayerRoom\NPC[i]\State = 2.0
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True), EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True) + 0.2, EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True))
							ResetEntity(PlayerRoom\NPC[i]\Collider)
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1.0
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState - 3)
						Next
					EndIf
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499" Then
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\DistTimer = 0.0
					If it\ItemTemplate\TempName = "scp1499" Lor it\ItemTemplate\TempName = "super1499"
						If EntityY(it\Collider) >= EntityY(r1499\OBJ) - 5.0
							PositionEntity(it\Collider, I_1499\PrevX, I_1499\PrevY + (EntityY(it\Collider) - EntityY(r1499\OBJ)), I_1499\PrevZ)
							ResetEntity(it\Collider)
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Exit.ogg"))
				I_1499\PrevX = 0.0
				I_1499\PrevY = 0.0
				I_1499\PrevZ = 0.0
				I_1499\PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
End Function

Function CheckForPlayerInFacility()
	; ~ False (= 0): NPC is not in facility (mostly meant for "dimension1499")
	; ~ True (= 1): NPC is in facility
	; ~ 2: NPC is in tunnels (maintenance tunnels / SCP-049's tunnels / SCP-939's storage room, etc...)
	
	If EntityY(me\Collider) > 100.0
		Return(False)
	EndIf
	If EntityY(me\Collider) < -10.0
		Return(2)
	EndIf
	If EntityY(me\Collider) > 7.0 And EntityY(me\Collider) =< 100.0
		Return(2)
	EndIf
	Return(True)
End Function

Function CheckTriggers$()
	Local i%, sX#, sY#, sZ#
	Local Inside% = -1
	
	If PlayerRoom\TriggerBoxAmount = 0 Then
		Return("")
	Else
		For i = 0 To PlayerRoom\TriggerBoxAmount - 1
			EntityAlpha(PlayerRoom\TriggerBox[i], 1.0)
			sX = EntityScaleX(PlayerRoom\TriggerBox[i], 1)
			sY = Max(EntityScaleY(PlayerRoom\TriggerBox[i], 1), 0.001)
			sZ = EntityScaleZ(PlayerRoom\TriggerBox[i], 1)
			GetMeshExtents(PlayerRoom\TriggerBox[i])
			If chs\DebugHUD Then
				EntityColor(PlayerRoom\TriggerBox[i], 255.0, 255.0, 0.0)
				EntityAlpha(PlayerRoom\TriggerBox[i], 0.2)
			Else
				EntityColor(PlayerRoom\TriggerBox[i], 255.0, 255.0, 255.0)
				EntityAlpha(PlayerRoom\TriggerBox[i], 0.0)
 			EndIf
			If EntityX(me\Collider) > ((sX * Mesh_MinX) + PlayerRoom\x) And EntityX(me\Collider) < ((sX * Mesh_MaxX) + PlayerRoom\x)
				If EntityY(me\Collider) > ((sY * Mesh_MinY) + PlayerRoom\y) And EntityY(me\Collider) < ((sY * Mesh_MaxY) + PlayerRoom\y)
					If EntityZ(me\Collider) > ((sZ * Mesh_MinZ) + PlayerRoom\z) And EntityZ(me\Collider) < ((sZ * Mesh_MaxZ) + PlayerRoom\z)
						Inside = i 
						Exit
					EndIf
				EndIf
			EndIf
		Next
		If Inside > -1 Then Return(PlayerRoom\TriggerBoxName[Inside])
	EndIf
End Function

Function ScaledMouseX%()
	Return(Float(MouseX() - (RealGraphicWidth * 0.5 * (1.0 - AspectRatioRatio))) * Float(GraphicWidth) / Float(RealGraphicWidth*AspectRatioRatio))
End Function

Function ScaledMouseY%()
	Return(Float(MouseY()) * Float(GraphicHeight) / Float(RealGraphicHeight))
End Function

Function TeleportEntity(Entity%, x#, y#, z#, CustomRadius# = 0.3, IsGlobal% = False, PickRange# = 2.0, Dir% = 0)
	Local Pvt%, Pick#
	; ~ Dir = 0 - towards the floor (default)
	; ~ Dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	Pvt = CreatePivot()
	PositionEntity(Pvt, x, y + 0.05, z, IsGlobal)
	If Dir = 0
		RotateEntity(Pvt, 90.0, 0.0, 0.0)
	Else
		RotateEntity(Pvt, -90.0, 0.0, 0.0)
	EndIf
	Pick = EntityPick(Pvt, PickRange)
	If Pick <> 0
		If Dir = 0
			PositionEntity(Entity, x, PickedY() + CustomRadius + 0.02, z, IsGlobal)
		Else
			PositionEntity(Entity, x, PickedY() + CustomRadius - 0.02, z, IsGlobal)
		EndIf
	Else
		PositionEntity(Entity, x, y, z, IsGlobal)
	EndIf
	FreeEntity(Pvt)
	ResetEntity(Entity)
End Function

Function PlayStartupVideos()
	If PlayStartup = 0 Then Return
	
	HidePointer()
	
	fo\FontID[0] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 16)
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(RealGraphicWidth) / Float(RealGraphicHeight)
	
	If Ratio > 1.76 And Ratio < 1.78 Then
		ScaledGraphicHeight = RealGraphicHeight
	Else
		ScaledGraphicHeight = Float(RealGraphicWidth) / (16.0 / 9.0)
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
		
		Local SplashScreenVideo% = BlitzMovie_OpenD3D(MovieFile + ".avi", SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"))
		
		If SplashScreenVideo = 0 Then
			PutINIValue(OptionFile, "Advanced", "Play Startup Videos", 0)
			Return
		EndIf
		SplashScreenVideo = BlitzMovie_Play()
		
		Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", SFXVolume, 0)
		
		Repeat
			Cls()
			BlitzMovie_DrawD3D(0, (RealGraphicHeight / 2 - ScaledGraphicHeight / 2), RealGraphicWidth, ScaledGraphicHeight)
			SetFont(fo\FontID[0])
			Color(255, 255, 255)
	        Text(GraphicWidth / 2, GraphicHeight - 50, "PRESS ANY KEY TO SKIP", True, True)
			Flip()
		Until (GetKey() Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		StopStream_Strict(SplashScreenAudio)
		BlitzMovie_Stop()
		BlitzMovie_Close()
		
		Cls()
		Flip()
	Next
	
	FreeFont(fo\FontID[0])
	
	ShowPointer()
End Function

Function CanUseItem(CanUseWithGasMask%, CanUseWithEyewear%)
	If CanUseWithGasMask = False And (wi\GasMask > 0 Lor I_1499\Using > 0) Then
		msg\Msg = "You can't use that item while wearing a gas mask."
		msg\Timer = 70.0 * 6.0
		Return(False)
	ElseIf CanUseWithEyewear = False And (wi\NightVision > 0 Lor wi\SCRAMBLE > 0)
		msg\Msg = "You can't use that item while wearing headgear."
		msg\Timer = 70.0 * 6.0
		Return(False)
	EndIf
	Return(True)
End Function

Function PreventItemOverlapping(GasMask%, NVG%, SCP1499%, Helmet%, SCRAMBLE%)
	If GasMask = False And wi\GasMask > 0 Then
		msg\Msg = "You need to take off the gas mask in order to use that item."
		msg\Timer = 70.0 * 6.0
		SelectedItem = Null
		Return(False)
	ElseIf SCP1499 = False And I_1499\Using > 0
		msg\Msg = "You need to take off SCP-1499 in order to use that item."
		msg\Timer = 70.0 * 6.0
		SelectedItem = Null
		Return(False)
	ElseIf NVG = False And wi\NightVision > 0 Then
		msg\Msg = "You need to take off the goggles in order to use that item."
		msg\Timer = 70.0 * 6.0
		SelectedItem = Null
		Return(False)
	ElseIf Helmet = False And wi\BallisticHelmet > 0
		msg\Msg = "You need to take off the helmet in order to use that item."
		msg\Timer = 70.0 * 6.0
		SelectedItem = Null
		Return(False)
	ElseIf SCRAMBLE = False And wi\SCRAMBLE > 0
		msg\Msg = "You need to take off the gear in order to use that item."
		msg\Timer = 70.0 * 6.0
		SelectedItem = Null
		Return(False)
	EndIf
	Return(True)
End Function

Function ResetInput()
	FlushKeys()
	FlushMouse()
	MouseHit1 = 0
	MouseHit2 = 0
	MouseDown1 = 0
	MouseUp1 = 0
	MouseHit(1)
	MouseHit(2)
	MouseDown(1)
	GrabbedEntity = 0
	Input_ResetTime = 10.0
End Function

;~IDEal Editor Parameters:
;~B#108B#1324#1E2F
;~C#Blitz3D