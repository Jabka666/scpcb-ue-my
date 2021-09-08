Local InitErrorStr$ = ""

If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10)
If FileSize("BlitzMovie.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzMovie.dll" + Chr(13) + Chr(10)
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr)

Include "Source Code\Keys_Core.bb"
Include "Source Code\INI_Core.bb"
Include "Source Code\Math_Core.bb"
Include "Source Code\Strict_Loads_Core.bb"

Const MaxFontIDAmount% = 8
; ~ Font IDs Constants
;[Block]
Const Font_Default% = 0
Const Font_Default_Big% = 1
Const Font_Digital% = 2
Const Font_Digital_Big% = 3
Const Font_Journal% = 4
Const Font_Console% = 5
Const Font_Credits% = 6
Const Font_Credits_Big% = 7
;[End Block]

Type Fonts
	Field FontID%[MaxFontIDAmount]
End Type

Global fo.Fonts = New Fonts

Global ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
Global ButtonSFX2% = LoadSound_Strict("SFX\Interact\Button2.ogg")

Global WireFrameState%

Global MenuWhite%, MenuGray%, MenuBlack%

LoadOptionsINI()

Type Mouse
	Field MouseHit1%, MouseHit2%
	Field MouseDown1%
	Field DoubleClick%, DoubleClickSlot%
	Field LastMouseHit1%
	Field MouseUp1%
	Field Mouselook_X_Inc#, Mouselook_Y_Inc#
	Field Mouse_Left_Limit%, Mouse_Right_Limit%
	Field Mouse_Top_Limit%, Mouse_Bottom_Limit%
	Field Mouse_X_Speed_1#, Mouse_Y_Speed_1#
	Field Viewport_Center_X%, Viewport_Center_Y%
End Type

Global mo.Mouse = New Mouse

mo\Mouselook_X_Inc = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the X axis
mo\Mouselook_Y_Inc = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the Y axis
mo\Mouse_Left_Limit = 250
mo\Mouse_Right_Limit = GraphicsWidth() - 250
mo\Mouse_Top_Limit = 150
mo\Mouse_Bottom_Limit = GraphicsHeight() - 150 ; ~ As above

Type Launcher
	Field TotalGFXModes%
	Field GFXModes%
	Field SelectedGFXMode%
	Field GFXModeWidths%[64], GFXModeHeights%[64]
End Type

If opt\LauncherEnabled Then
	Local lnchr.Launcher = New Launcher
	
	lnchr\TotalGFXModes = CountGfxModes3D()
	
	opt\AspectRatio = 1.0
	
	UpdateLauncher(lnchr)
	
	Delete(lnchr)
EndIf

; ~ New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode -- Love Mark
If opt\DisplayMode = 1 Then
	Graphics3DExt(DesktopWidth(), DesktopHeight(), 0, 4)
	
	opt\RealGraphicWidth = DesktopWidth()
	opt\RealGraphicHeight = DesktopHeight()
	
	opt\AspectRatio = (Float(opt\GraphicWidth) / Float(opt\GraphicHeight)) / (Float(opt\RealGraphicWidth) / Float(opt\RealGraphicHeight))
Else
	opt\AspectRatio = 1.0
	opt\RealGraphicWidth = opt\GraphicWidth
	opt\RealGraphicHeight = opt\GraphicHeight
	Graphics3DExt(opt\GraphicWidth, opt\GraphicHeight, 0, (opt\DisplayMode = 2) + 1)
EndIf

; ~ Viewport
mo\Viewport_Center_X = opt\GraphicWidth / 2
mo\Viewport_Center_Y = opt\GraphicHeight / 2
	
Global MenuScale# = opt\GraphicHeight / 1024.0

SetBuffer(BackBuffer())

Const TICK_DURATION# = 70.0 / 60.0

Type FramesPerSeconds
	Field Accumulator#
	Field PrevTime%
	Field CurrTime%
	Field FPS%
	Field TempFPS%
	Field Goal%
	Field Factor#[2]
End Type

Global fps.FramesPerSeconds = New FramesPerSeconds

SeedRnd(MilliSecs2())

Global GameSaved%

Global CanSave% = True

AppTitle("SCP - Containment Breach Ultimate Edition v" + VersionNumber)

If opt\PlayStartup Then PlayStartupVideos()

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")
CursorIMG = ScaleImage2(CursorIMG, MenuScale, MenuScale)

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict("LoadingScreens\loading_back.png")
LoadingBack = ScaleImage2(LoadingBack, MenuScale, MenuScale)

InitLoadingScreens("LoadingScreens\loading_screens.ini")

; ~ For some reason, Blitz3D doesn't load fonts that have filenames that
; ~ Don't match their "internal name" (i.e. their display name in applications like Word and such)
; ~ As a workaround, I moved the files and renamed them so they
; ~ Can load without FastText
fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\cour\Courier New.ttf", 16)
fo\FontID[Font_Default_Big] = LoadFont_Strict("GFX\fonts\cour\Courier New.ttf", 52)
fo\FontID[Font_Digital] = LoadFont_Strict("GFX\fonts\DS-DIGI\DS-Digital.ttf", 20)
fo\FontID[Font_Digital_Big] = LoadFont_Strict("GFX\fonts\DS-DIGI\DS-Digital.ttf", 60)
fo\FontID[Font_Journal] = LoadFont_Strict("GFX\fonts\Journal\Journal.ttf", 58)
fo\FontID[Font_Console] = LoadFont_Strict("GFX\fonts\Andale\Andale Mono.ttf", 16)

SetFont(fo\FontID[Font_Default_Big])

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blink_meter(1).png")

RenderLoading(0, "MAIN CORE")

Type Player
	Field KillTimer#, KillAnim%, FallTimer#, DeathTimer#
	Field Sanity#, RestoreSanity%
	Field ForceMove#, ForceAngle#
	Field Playable%, PlayTime%
	Field BlinkTimer#, BLINKFREQ#, BlinkEffect#, BlinkEffectTimer#, EyeIrritation#, EyeStuck#
	Field Stamina#, StaminaEffect#, StaminaEffectTimer#
	Field CameraShakeTimer#, Shake#, CameraShake#, BigCameraShake#
	Field Vomit%, VomitTimer#, Regurgitate%
	Field HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#
	Field Injuries#, Bloodloss#, PrevInjuries#, PrevBloodloss#, HealTimer#
	Field DropSpeed#, HeadDropSpeed#, CurrSpeed#
	Field Crouch%, CrouchState#
	Field SndVolume#
	Field SelectedEnding%, EndingScreen%, EndingTimer#
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
	Field StopHidingTimer#
	Field Funds%, UsedMastercard%
End Type

Global me.Player = New Player

Type WearableItems
	Field GasMask%, GasMaskFogTimer#
	Field HazmatSuit%
	Field BallisticVest%
	Field BallisticHelmet%
	Field NightVision%, NVGTimer#, IsNVGBlinking%
	Field SCRAMBLE%
End Type

Global wi.WearableItems = New WearableItems

RenderLoading(5, "ACHIEVEMENTS CORE")

Include "Source Code\Achievements_Core.bb"

Global CameraPitch#

Global GrabbedEntity%

Type Cheats
	Field GodMode%
	Field NoBlink%
	Field NoTarget%
	Field NoClip%, NoClipSpeed#
	Field InfiniteStamina%
	Field SuperMan%, SuperManTimer#
	Field DebugHUD%
End Type

Global chs.Cheats = New Cheats

Function ClearCheats%()
	chs\GodMode = False
	chs\NoBlink = False
	chs\NoTarget = False
	chs\NoClip = False
	chs\NoClipSpeed = 2.0
	chs\InfiniteStamina = False
	chs\SuperMan = False
	chs\SuperManTimer = 0.0
	chs\DebugHUD = 0
End Function

Function InitCheats%()
	chs\GodMode = True
	chs\NoBlink = True
	chs\NoTarget = True
	chs\NoClip = True
	chs\NoClipSpeed = 2.0
	chs\InfiniteStamina = True
	chs\SuperMan = False
	chs\SuperManTimer = 0.0
	chs\DebugHUD = Rand(1, 3)
End Function

Global CoffinDistance# = 100.0

Global SoundTransmission%

Global MainMenuOpen%, MenuOpen%, InvOpen%

Global AccessCode%

RenderLoading(10, "DIFFICULTY CORE")

Include "Source Code\Difficulty_Core.bb"

Global MTFTimer#

Global RadioState#[9]
Global RadioState2%[9]
Global RadioState3%[10]

; ~ Textures Constants
;[Block]
Const MaxMiscTextureIDAmount% = 24
Const MaxMonitorTextureIDAmount% = 5
Const MaxOverlayTextureIDAmount% = 11
Const MaxOverlayIDAmount% = 11
Const MaxDecalTextureIDAmount% = 21
Const MaxNPCTextureIDAmount% = 17
Const MaxParticleTextureIDAmount% = 8
Const MaxLightSpriteIDAmount% = 3
Const MaxIconIDAmount% = 7
Const MaxImageIDAmount% = 8
;[End Block]

Type Textures
	Field MiscTextureID%[MaxMiscTextureIDAmount]
	Field MonitorTextureID%[MaxMonitorTextureIDAmount]
	Field DecalTextureID%[MaxDecalTextureIDAmount]
	Field NPCTextureID%[MaxNPCTextureIDAmount]
	Field ParticleTextureID%[MaxParticleTextureIDAmount]
	Field LightSpriteID%[MaxLightSpriteIDAmount]
	Field IconID%[MaxIconIDAmount]
	Field ImageID%[MaxImageIDAmount]
	Field OverlayTextureID%[MaxOverlayTextureIDAmount]
	Field OverlayID%[MaxOverlayIDAmount]
End Type

Global t.Textures = New Textures

; ~ Objects Constants
;[Block]
Const MaxNPCModelIDAmount% = 34
Const MaxButtonModelIDAmount% = 5
Const MaxDoorModelIDAmount% = 13
Const MaxLeverModelIDAmount% = 2
Const MaxCamModelIDAmount% = 2
Const MaxMonitorModelIDAmount% = 2
Const MaxMTModelIDAmount% = 7
Const MaxMiscModelIDAmount% = 2
;[End Block]

Type Objects
	Field NPCModelID%[MaxNPCModelIDAmount]
	Field ButtonModelID%[MaxButtonModelIDAmount]
	Field DoorModelID%[MaxDoorModelIDAmount]
	Field LeverModelID%[MaxLeverModelIDAmount]
	Field CamModelID%[MaxCamModelIDAmount]
	Field MonitorModelID%[MaxMonitorModelIDAmount]
	Field MTModelID%[MaxMTModelIDAmount]
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

Function CreateConsoleMsg%(Txt$, R% = -1, G% = -1, B% = -1, IsCommand% = False)
	Local c.ConsoleMsg
	
	c.ConsoleMsg = New ConsoleMsg
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

Function UpdateConsole%()
	If (Not opt\CanOpenConsole) Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local ev.Events, e.Events, e2.Events, r.Rooms, it.Items, n.NPCs, snd.Sound, cm.ConsoleMsg, itt.ItemTemplates
		Local Tex%, Tex2%, InBar%, InBox%, MouseScroll#, Temp%, i%
		Local Args$, StrTemp$, StrTemp2$, StrTemp3$, StrTemp4$
		Local x%, y%, Width%, Height%
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		x = 0
		y = opt\GraphicHeight - 300 * MenuScale
		Width = opt\GraphicWidth
		Height = 270 * MenuScale
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + (15 * MenuScale)
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		If ScrollBarHeight > Height Then ScrollBarHeight = Height
		If ConsoleHeight < Height Then ConsoleHeight = Height
		
		InBar = MouseOn(x + Width - (26 * MenuScale), y, 26 * MenuScale, Height)
		InBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		If (Not mo\MouseDown1) Then
			ConsoleScrollDragging = False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll + ((ScaledMouseY() - ConsoleMouseMem) * Height / ScrollBarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If mo\MouseHit1 Then
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
			ConsoleScroll = ConsoleScroll - (15 * MenuScale)
		ElseIf MouseScroll= -1 Then
			ConsoleScroll = ConsoleScroll + (15 * MenuScale)
		EndIf
		
		Local ReissuePos%
		
		If KeyHit(200) Then
			ReissuePos = 0
			If ConsoleReissue = Null Then
				ConsoleReissue = First ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then
						Exit
					EndIf
					ReissuePos = ReissuePos - (15 * MenuScale)
					ConsoleReissue = After ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos - (15 * MenuScale)
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				ReissuePos = ReissuePos - (15 * MenuScale)
				
				While True
					If ConsoleReissue = Null Then
						ConsoleReissue = First ConsoleMsg
						ReissuePos = 0
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos - (15 * MenuScale)
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null Then
				ConsoleInput = ConsoleReissue\Txt
				ConsoleScroll = ReissuePos + (Height / 2)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			ReissuePos = (-ConsoleHeight) + (15 * MenuScale)
			If ConsoleReissue = Null Then
				ConsoleReissue = Last ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + (15 * MenuScale)
					ConsoleReissue = Before ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos + (15 * MenuScale)
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				ReissuePos = ReissuePos + (15 * MenuScale)
				
				While True
					If ConsoleReissue = Null Then
						ConsoleReissue = Last ConsoleMsg
						ReissuePos = (-ConsoleHeight) + (15 * MenuScale)
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + (15 * MenuScale)
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
		
		SelectedInputBox = 2
		
		Local OldConsoleInput$ = ConsoleInput
		
		ConsoleInput = UpdateMainMenuInputBox(x, y + Height, Width, 30 * MenuScale, ConsoleInput, 2)
		If OldConsoleInput <> ConsoleInput Then ConsoleReissue = Null
		ConsoleInput = Left(ConsoleInput, 100)
		
		If KeyHit(28) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput, 255, 255, 0, True)
			If Instr(ConsoleInput, " ") <> 0 Then
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
					
					Select StrTemp
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
							CreateConsoleMsg("- doorcontrol") 
							CreateConsoleMsg("- unlockcheckpoints") 
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- disablenuke")
							CreateConsoleMsg("- resetfunds")
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
							CreateConsoleMsg("- giveachievement [ID / All]")
							CreateConsoleMsg("- codes")
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
							CreateConsoleMsg("HELP - noblink")
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
						Case "spawnitem", "si", "giveitem"
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
							CreateConsoleMsg("008-1 / 049 / 049-2 / 066 / 096 / 106 / 173 / 860")
							CreateConsoleMsg("/ 372 / 513-1 / 966 / 1499-1 / class-d / 939")
							CreateConsoleMsg("/ guard / mtf / apache / tentacle / 1048_a/ 1048")
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
						Case "doorcontrol" 
							;[Block]
							CreateConsoleMsg("HELP - enablecontrol")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Turns on/off the Remote Door Control lever.")
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
						Case "resetfunds"
							;[Block]
							CreateConsoleMsg("HELP - resetfunds")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Resets your Mastercard funds.")
							CreateConsoleMsg("******************************")
						Case "giveachievement"
							;[Block]
							CreateConsoleMsg("HELP - giveachievement")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Grants any achievement you want.")
							CreateConsoleMsg("You can guess the ID by counting in the achievements menu (starts at 0).")
							CreateConsoleMsg("You can also use " + Chr(34) + "all" + Chr(34) + " to immediately unlock every achievement.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "codes" 
							;[Block]
							CreateConsoleMsg("HELP - codes")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Displays access codes for this save.")
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
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "A"
							;[Block]
							me\SelectedEnding = Rand(Ending_A1, Ending_A2)
							;[End Block]
						Case "B"
							;[Block]
							me\SelectedEnding = Rand(Ending_B1, Ending_B2)
							;[End Block]
						Default
							;[Block]
							me\SelectedEnding = Rand(Ending_A1, Ending_B2)
							;[End Block]
					End Select
					
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
				Case "cls", "clear"
					;[Block]
					ClearConsole()
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
					ResetNegativeStats()
					;[End Block]
				Case "teleport", "tp"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.7, EntityZ(r\OBJ))
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
					
					If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("Room not found.", 255, 0, 0)
					;[End Block]
				Case "spawnitem", "si", "giveitem"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Temp = False 
					For itt.ItemTemplates = Each ItemTemplates
						If Lower(itt\Name) = StrTemp Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						ElseIf Lower(itt\TempName) = StrTemp Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						EndIf
					Next
					
					If (Not Temp) Then CreateConsoleMsg("Item not found.", 255, 0, 0)
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
						If n\NPCType = NPCType096 Then
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
						If n\NPCType = NPCType372 Then
							RemoveNPC(n)
							CreateEvent("cont1_372", "cont1_372", 0, 0.0)   
							Exit
						EndIf
					Next
					;[End Block]
				Case "disable173"
					;[Block]
					Curr173\Idle = 3 ; ~ This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of SCP-173 state checks.
					HideEntity(Curr173\OBJ)
					HideEntity(Curr173\OBJ2)
					HideEntity(Curr173\Collider)
					;[End Block]
				Case "enable173"
					;[Block]
					Curr173\Idle = 0
					ShowEntity(Curr173\OBJ)
					ShowEntity(Curr173\OBJ2)
					ShowEntity(Curr173\Collider)
					;[End Block]
				Case "disable106"
					;[Block]
					Curr106\Idle = 1
					Curr106\State = 200000.0
					Curr106\Contained = True
					;[End Block]
				Case "enable106"
					;[Block]
					Curr106\Idle = 0
					Curr106\Contained = False
					ShowEntity(Curr106\Collider)
					ShowEntity(Curr106\OBJ)
					;[End Block]
				Case "disable966"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType966 Then
							n\State = -1.0
							HideEntity(n\Collider)
							HideEntity(n\OBJ)
							Exit
						EndIf
					Next
					;[End Block]
				Case "enable966"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType966 Then
							n\State = 0.0
							ShowEntity(n\Collider)
							If WearinNightVision > 0 Then ShowEntity(n\OBJ)
							Exit
						EndIf
					Next
					;[End Block]
				Case "disable049" 
					;[Block]
					Curr049\Idle = 1
					HideEntity(Curr049\Collider)
					HideEntity(Curr049\OBJ)
					;[End Block]
				Case "enable049"
					;[Block]
					Curr049\Idle = 0
					ShowEntity(Curr049\Collider)
					ShowEntity(Curr049\OBJ)
					;[End Block]
				Case "106retreat"
					;[Block]
					If Curr106\State =< 0.0 Then
						Curr106\State = Rnd(22000.0, 27000.0)
						PositionEntity(Curr106\Collider, 0.0, 500.0, 0.0)
						ResetEntity(Curr106\Collider)
					Else
						CreateConsoleMsg("SCP-106 is currently not active, so it cannot retreat.", 255, 150, 0)
					EndIf
					;[End Block]
				Case "halloween"
					;[Block]
					t\MiscTextureID[14] = (Not t\MiscTextureID[14])
					If t\MiscTextureID[14] Then
						Tex = LoadTexture_Strict("GFX\npcs\scp_173_H.png", 1)
						EntityTexture(Curr173\OBJ, Tex)
						EntityTexture(Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex)
						CreateConsoleMsg("173 JACK-O-LANTERN ON")
					Else
						If t\MiscTextureID[15] Then t\MiscTextureID[15] = (Not t\MiscTextureID[15])
						Tex2 = LoadTexture_Strict("GFX\npcs\scp_173.png", 1)
						EntityTexture(Curr173\OBJ, Tex2)
						EntityTexture(Curr173\OBJ2, Tex2)
						DeleteSingleTextureEntryFromCache(Tex2)
						CreateConsoleMsg("173 JACK-O-LANTERN OFF")
					EndIf
					;[End Block]
				Case "newyear" 
					;[Block]
					t\MiscTextureID[15] = (Not t\MiscTextureID[15])
					If t\MiscTextureID[15] Then
						Tex = LoadTexture_Strict("GFX\npcs\scp_173_NY.png", 1)
						EntityTexture(Curr173\OBJ, Tex)
						EntityTexture(Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex)
						CreateConsoleMsg("173 COOKIE ON")
					Else
						If t\MiscTextureID[14] Then t\MiscTextureID[14] = (Not t\MiscTextureID[14])
						Tex2 = LoadTexture_Strict("GFX\npcs\scp_173.png", 1)
						EntityTexture(Curr173\OBJ, Tex2)
						EntityTexture(Curr173\OBJ2, Tex2)
						DeleteSingleTextureEntryFromCache(Tex2)
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
					ResetNegativeStats(True)
					;[End Block]
				Case "noclip", "fly"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoClip = True
							me\Playable = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoClip = False	
							RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							;[End Block]
						Default
							;[Block]
							chs\NoClip = (Not chs\NoClip)
							If (Not chs\NoClip) Then		
								RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
							Else
								me\Playable = True
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
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					Select StrTemp
						Case "game", "1"
							;[Block]
							chs\DebugHUD = 1
							;[End Block]
						Case "player", "me", "2"
							;[Block]
							chs\DebugHUD = 2
							;[End Block]
						Case "scps", "scp", "3"
							;[Block]
							chs\DebugHUD = 3
							;[End Block]
						Case "off", "false", "0"
							;[Block]
							chs\DebugHUD = 0
							;[End Block]
						Default
							;[Block]
							CreateConsoleMsg("First, select the debug category.", 255, 150, 0)
							;[End Block]
					End Select
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					KillSounds()
					
					For e.Events = Each Events
						If e\EventID = e_cont1_173 Then
							For i = 0 To 2
								If e\room\NPC[i] <> Null Then RemoveNPC(e\room\NPC[i])
								If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
							Next
							If Curr173\Idle = 1 Then Curr173\Idle = 0
							PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(Curr173\Collider)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("Stopped all sounds.")
					;[End Block]
				Case "camerafog"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					opt\CameraFogNear = Float(Left(Args, Len(Args) - Instr(Args, " ")))
					opt\CameraFogFar = Float(Right(Args, Len(Args) - Instr(Args, " ")))
					CreateConsoleMsg("Near set to: " + opt\CameraFogNear + ", far set to: " + opt\CameraFogFar)
					;[End Block]
				Case "spawn"
					;[Block]
					Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args, 1)
					StrTemp2 = Piece(Args, 2)
					
					; ~ Hacky fix for when the user doesn't input a second parameter.
					If StrTemp <> StrTemp2 Then
						ConsoleSpawnNPC(StrTemp, StrTemp2)
					Else
						ConsoleSpawnNPC(StrTemp)
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
				Case "doorcontrol"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							RemoteDoorOn = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							RemoteDoorOn = False
							;[End Block]
						Default
							;[Block]
							RemoteDoorOn = (Not RemoteDoorOn)
							;[End Block]
					End Select
					
					If RemoteDoorOn Then
						CreateConsoleMsg("REMOTE DOOR CONTROL ENABLED")
					Else
						CreateConsoleMsg("REMOTE DOOR CONTROL DISABLED")
					EndIf
					
					For e2.Events = Each Events
						If e2\EventID = e_room2c_ec Then
							UpdateLever(e2\room\Objects[5])
							RotateEntity(e2\room\Objects[5], 0.0, EntityYaw(e2\room\Objects[5]), RemoteDoorOn * 30.0)
							Exit
						EndIf
					Next
					;[End Block]
				Case "unlockcheckpoints"
					;[Block]
					For e2.Events = Each Events
						If e2\EventID = e_room2_sl Then
							e2\EventState3 = 0.0
							UpdateLever(e2\room\Levers[0])
							RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 0.0)
							TurnCheckpointMonitorsOff()
						ElseIf e2\EventID = e_cont2_008
							e2\EventState = 2.0
							UpdateLever(e2\room\Levers[0])
							RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 30.0)
							TurnCheckpointMonitorsOff(False)
						EndIf
					Next
					
					CreateConsoleMsg("Checkpoints are now unlocked.")								
					;[End Block]
				Case "disablenuke"
					;[Block]
					For e2.Events = Each Events
						If e2\EventID = e_room2_nuke
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
								If e\EventID = e_gate_a_entrance Then
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
								If e\EventID = e_gate_b_entrance Then
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
									Exit
								EndIf
							Next	
							CreateConsoleMsg("Gate B is now unlocked.")	
							;[End Block]
						Default
							;[Block]
							For e.Events = Each Events
								If e\EventID = e_gate_b_entrance Lor e\EventID = e_gate_a_entrance Then
									e\EventState3 = 1.0
									e\room\RoomDoors[1]\Open = True
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
						If (Not CustomMusic) Then
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
					
					opt\CameraFogFar = 50.0
					
					KillSounds()
					
					For e.Events = Each Events
						If e\EventID = e_cont1_173 Then
							For i = 0 To 2
								If e\room\NPC[i] <> Null Then RemoveNPC(e\room\NPC[i])
								If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
							Next
							If Curr173\Idle = 1 Then Curr173\Idle = 0
							PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(Curr173\Collider)
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
					
					If (Not chs\NoTarget) Then
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
							If PlayerRoom = e\room
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
					
					If StrTemp = "all" Then
						For i = 0 To MAXACHIEVEMENTS-1
							achv\Achievement[i] = True
						Next
						CreateConsoleMsg("All the achievements have been unlocked.")
					EndIf
					
					If Int(StrTemp) >= 0 And Int(StrTemp) < MAXACHIEVEMENTS And StrTemp <> "all" Then
						achv\Achievement[Int(StrTemp)] = True
						CreateConsoleMsg("Achievement " + achv\AchievementStrings[Int(StrTemp)] + " unlocked.")
					ElseIf StrTemp <> "all"
						CreateConsoleMsg("Achievement with ID " + Int(StrTemp) + " doesn't exist.", 255, 0, 0)
					EndIf
					;[End Block]
				Case "427state"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = 70.0 * Float(StrTemp)
					;[End Block]
				Case "teleport106"
					;[Block]
					Curr106\State = 0.0
					Curr106\Idle = 0
					;[End Block]
				Case "jorge"
					;[Block]	
					CreateConsoleMsg(Chr(74) + Chr(79) + Chr(82) + Chr(71) + Chr(69) + Chr(32) + Chr(72) + Chr(65) + Chr(83) + Chr(32) + Chr(66) + Chr(69) + Chr(69) + Chr(78) + Chr(32) + Chr(69) + Chr(88) + Chr(80) + Chr(69) + Chr(67) + Chr(84) + Chr(73) + Chr(78) + Chr(71) + Chr(32) + Chr(89) + Chr(79) + Chr(85) + Chr(46))
					;[End Block]
				Case "resetfunds"
					;[Block]
					me\Funds = Rand(0, 6)
					;[End Block]
				Case "codes"
					;[Block]
					Temp = ((Int(AccessCode) * 3) Mod 10000)
					If Temp < 1000 Then Temp = Temp + 1000
					
					CreateConsoleMsg("Access Codes:")
					CreateConsoleMsg("")
					CreateConsoleMsg("Dr. Maynard: " + AccessCode)
					CreateConsoleMsg("Dr. Harp: 7816")
					;CreateConsoleMsg("Dr Gears: 1311") ~ Removed since Gears office is locked
					CreateConsoleMsg("Dr. L.: 1234")
					CreateConsoleMsg("O5 Council Office: 2411")
					CreateConsoleMsg("Maintenance Tunnel: " + Temp)
					CreateConsoleMsg(Chr(34) + "cont1_035" + Chr(34) + " storage room: 5731")
					CreateConsoleMsg("")
					CreateConsoleMsg("All the others doors don't have a code.")
					;[End Block]
				Default
					;[Block]
					CreateConsoleMsg("Command not found.", 255, 0, 0)
					;[End Block]
			End Select
			ConsoleInput = ""
		EndIf
		
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000 Then
				Delete(cm)
			EndIf
		Next
	EndIf
	
	SetFont(fo\FontID[Font_Default])
End Function

Function RenderConsole()
	If (Not opt\CanOpenConsole) Then
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		Local InBar%, InBox%
		Local x%, y%, Width%, Height%
		
		SetFont(fo\FontID[Font_Console])
		
		x = 0
		y = opt\GraphicHeight - 300 * MenuScale
		Width = opt\GraphicWidth
		Height = 270 * MenuScale
		
		RenderFrame(x, y, Width, Height + (30 * MenuScale))
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + (15 * MenuScale)
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		If ScrollBarHeight > Height Then ScrollBarHeight = Height
		If ConsoleHeight < Height Then ConsoleHeight = Height
		
		Color(50, 50, 50)
		InBar = MouseOn(x + Width - (26 * MenuScale), y, 26 * MenuScale, Height)
		If InBar Then Color(70, 70, 70)
		Rect(x + Width - (26 * MenuScale), y, 26 * MenuScale, Height)
		
		Color(120, 120, 120)
		InBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		If InBox Then Color(200, 200, 200)
		If ConsoleScrollDragging Then Color(255, 255, 255)
		Rect(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		Color(255, 255, 255)
		
		Local TempY# = y + Height - (25.0 * MenuScale) - ConsoleScroll
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000 Then
				Delete(cm)
			Else
				If TempY >= y And TempY < y + Height - (20 * MenuScale) Then
					If cm = ConsoleReissue Then
						Color(cm\R / 4, cm\G / 4, cm\B / 4)
						Rect(x, TempY - (2 * MenuScale), Width - (30 * MenuScale), 24 * MenuScale, True)
					EndIf
					Color(cm\R, cm\G, cm\B)
					If cm\IsCommand Then
						Text(x + (20 * MenuScale), TempY, "> " + cm\Txt)
					Else
						Text(x + (20 * MenuScale), TempY, cm\Txt)
					EndIf
				EndIf
				TempY = TempY - (15.0 * MenuScale)
			EndIf
		Next
		Color(255, 255, 255)
		
		RenderMenuInputBoxes()
		
		If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	EndIf
	SetFont(fo\FontID[Font_Default])
End Function

Function ClearConsole()
	Local c.ConsoleMsg
	
	For c.ConsoleMsg = Each ConsoleMsg
		Delete(c)
	Next
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("Console commands: ")
	CreateConsoleMsg("  - help [page]")
	CreateConsoleMsg("  - teleport [room name]")
	CreateConsoleMsg("  - godmode [on / off]")
	CreateConsoleMsg("  - noclip [on / off]")
	CreateConsoleMsg("  - infinitestamina [on / off]")
	CreateConsoleMsg("  - noblink [on / off]")
	CreateConsoleMsg("  - notarget [on / off]")
	CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
	CreateConsoleMsg("  - wireframe [on / off]")
	CreateConsoleMsg("  - debughud [category]")
	CreateConsoleMsg("  - camerafog [near] [far]")
	CreateConsoleMsg("  - heal")
	CreateConsoleMsg("  - revive")
	CreateConsoleMsg("  - asd")
	CreateConsoleMsg("  - spawnitem [item name]")
	CreateConsoleMsg("  - 106retreat")
	CreateConsoleMsg("  - disable173 / enable173")
	CreateConsoleMsg("  - disable106 / enable106")
	CreateConsoleMsg("  - spawn [NPC type]")
End Function

Const SubjectName$ = "Subject D-9341"

Type Messages
	Field Txt$
	Field Timer#
	Field DeathMsg$
	Field KeyPadMsg$
	Field KeyPadTimer#
	Field KeyPadInput$
	Field HintTxt$
	Field HintTimer#
	Field HintY#
End Type

Global msg.Messages = New Messages

Function CreateMsg(Txt$, Sec# = 6.0)
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	msg\Txt = Txt
	msg\Timer = 70.0 * Sec
End Function

Function UpdateMessages()
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	If msg\Timer > 0.0 Then
		msg\Timer = msg\Timer - fps\Factor[0]
	Else
		If msg\Txt <> "" Then msg\Txt = ""
	EndIf
End Function

Function RenderMessages()
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	If msg\Timer > 0.0 Then
		Local Temp% = False
		
		If (Not (InvOpen Lor OtherOpen <> Null)) Then
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\TempName = "paper" Lor SelectedItem\ItemTemplate\TempName = "oldpaper" Then
					Temp = True
				EndIf
			ElseIf I_294\Using Lor SelectedDoor <> Null Lor SelectedScreen <> Null
				Temp = True
			EndIf
		EndIf
		
		Local Temp2% = Min(msg\Timer / 2.0, 255.0)
		
		SetFont(fo\FontID[Font_Default])
		If (Not Temp) Then
			Color(Temp2, Temp2, Temp2)
			Text(mo\Viewport_Center_X, mo\Viewport_Center_Y + (200 * MenuScale), msg\Txt, True)
		Else
			Color(Temp2, Temp2, Temp2)
			Text(mo\Viewport_Center_X, opt\GraphicHeight * 0.94, msg\Txt, True)
		EndIf
	EndIf
	Color(255, 255, 255)
	If opt\ShowFPS Then
		SetFont(fo\FontID[Font_Console])
		Text(20 * MenuScale, 20 * MenuScale, "FPS: " + fps\FPS)
		SetFont(fo\FontID[Font_Default])
	EndIf
End Function

Function CreateHintMsg(Txt$, Sec# = 6.0)
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	msg\HintTxt = Txt
	msg\HintTimer = 70.0 * Sec
End Function

Function UpdateHintMessages()
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width = StringWidth(msg\HintTxt) + (20 * Scale)
	Local Height% = 30 * Scale
	
	If msg\HintTxt <> "" Then
		If msg\HintTimer > 0.0 Then
			If msg\HintY < Height Then
				msg\HintY = Min(msg\HintY + (2.0 * fps\Factor[0]), Height)
			Else
				msg\HintY = Height
			EndIf
			msg\HintTimer = msg\HintTimer - fps\Factor[0]
		Else
			If msg\HintY > 0.0 Then
				msg\HintY = Max(msg\HintY - (2.0 * fps\Factor[0]), 0.0)
			Else
				msg\HintTxt = ""
				msg\HintTimer = 0.0
				msg\HintY = 0.0
			EndIf
		EndIf
	EndIf
	
End Function

Function RenderHintMessages()
	If SelectedDifficulty\OtherFactors = EXTREME Then Return
	
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width = StringWidth(msg\HintTxt) + (20 * Scale)
	Local Height% = 30 * Scale
	Local x% = mo\Viewport_Center_X - (Width / 2)
	Local y% = (-Height) + msg\HintY
	
	If msg\HintTxt <> "" Then
		RenderFrame(x, y, Width, Height)
		Color(255, 255, 255)
		SetFont(fo\FontID[Font_Default])
		Text(mo\Viewport_Center_X, y + (Height / 2), msg\HintTxt, True, True)
	EndIf
End Function

Global Camera%

RenderLoading(15, "SUBTITLES CORE")

Include "Source Code\Subtitles_Core.bb"

RenderLoading(20, "SOUNDS CORE")

Include "Source Code\Sounds_Core.bb"

Global OptionsMenu% = 0
Global QuitMsg% = 0

Global InFacility% = True

Global ForestNPC%, ForestNPCTex%, ForestNPCData#[3]

RenderLoading(25, "ITEMS CORE")

Include "Source Code\Items_Core.bb"

RenderLoading(30, "PARTICLES CORE")

Include "Source Code\Particles_Core.bb"

RenderLoading(35, "GRAPHICS CORE")

Include "Source Code\Graphics_Core.bb"

RenderLoading(40, "MAP CORE")

Include "Source Code\Map_Core.bb"

RenderLoading(60, "NPCs CORE")

Include "Source Code\NPCs_Core.bb"

RenderLoading(65, "EVENTS CORE")

Include "Source Code\Events_Core.bb"

; ~ Collisions Constants
;[Block]
Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_178% = 5
Const HIT_DEAD% = 6
;[End Block]

Collisions(HIT_PLAYER, HIT_MAP, 2, 2)
Collisions(HIT_PLAYER, HIT_PLAYER, 1, 3)
Collisions(HIT_ITEM, HIT_MAP, 2, 2)
Collisions(HIT_APACHE, HIT_APACHE, 1, 2)
Collisions(HIT_178, HIT_MAP, 2, 2)
Collisions(HIT_178, HIT_178, 1, 3)
Collisions(HIT_DEAD, HIT_MAP, 2, 2)

Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global MTFCameraCheckTimer# = 0.0
Global MTFCameraCheckDetected% = False

RenderLoading(70, "SAVE CORE")

Include "Source Code\Save_Core.bb"

RenderLoading(80, "MENU CORE")

Include "Source Code\Menu_Core.bb"

InitMainMenuAssets()
MainMenuOpen = True

FlushKeys()
FlushMouse()

RenderLoading(100)

Global Input_ResetTime# = 0.0

Type SCP005
	Field ChanceToSpawn%
End Type

Global I_005.SCP005 = New SCP005

Type SCP008
	Field Timer#
	Field Revert%
End Type

Global I_008.SCP008 = New SCP008

Type SCP035
	Field Sad%
End Type

Global I_035.SCP035 = New SCP035

Type SCP294
	Field Using%
	Field ToInput$
End Type

Global I_294.SCP294 = New SCP294

Type SCP409
	Field Timer#
	Field Revert%
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
	Field State#[8]
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

Type SCP500
	Field Taken%
End Type

Global I_500.SCP500 = New SCP500

Type MapZones
	Field Transition%[2]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones = New MapZones

Function CatchErrors(Location$)
	InitErrorMsgs(6)
	SetErrorMsg(0, "An error occured in SCP - Containment Breach Ultimate Edition v" + VersionNumber)
	SetErrorMsg(1, "Map Seed: " + RandomSeed)
	SetErrorMsg(2, "Date and time: " + CurrentDate() + " at " + CurrentTime() + Chr(10) + "OS: " + SystemProperty("os") + " " + (32 + (GetEnv("ProgramFiles(X86)") <> 0) * 32) + " bit (Build: " + SystemProperty("osbuild") + ")" + Chr(10))
	SetErrorMsg(3, "Video memory: " + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB" + Chr(10))
	SetErrorMsg(4, "Global memory status: " + ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB" + Chr(10))
	SetErrorMsg(5, "Error located in: " + Location + Chr(10) + Chr(10) + "Please take a screenshot of this error and send it to us!") 
End Function

Repeat
	Cls()
	
	Local ElapsedMilliSecs%
	
	fps\CurrTime = MilliSecs2()
	
	ElapsedMilliSecs = fps\CurrTime - fps\PrevTime
	If (ElapsedMilliSecs > 0 And ElapsedMilliSecs < 500) Then
		fps\Accumulator = fps\Accumulator + Max(0.0, Float(ElapsedMilliSecs) * 70.0 / 1000.0)
	EndIf
	fps\PrevTime = fps\CurrTime
	
	If opt\Framelimit > 0.0 Then
		Local LoopDelay% = MilliSecs2()
		Local WaitingTime% = (1000.0 / opt\Framelimit) - (MilliSecs2() - LoopDelay)
		
		Delay(WaitingTime)
	EndIf
	
	fps\Factor[0] = TICK_DURATION
	fps\Factor[1] = fps\Factor[0]
	
	If MainMenuOpen Then
		UpdateMainMenu()
	Else
		MainLoop()
	EndIf
	
	RenderGamma()
	
	If KeyHit(key\SCREENSHOT) Then GetScreenshot()
	
	If opt\ShowFPS Then
		If fps\Goal < MilliSecs2() Then
			fps\FPS = fps\TempFPS
			fps\TempFPS = 0
			fps\Goal = MilliSecs2() + 1000
		Else
			fps\TempFPS = fps\TempFPS + 1
		EndIf
	EndIf
	
	If (Not opt\VSync) Then
		Flip(False)
	Else
		Flip(True)
	EndIf
Forever

; ~ Fog Constants
;[Block]
Const FogColorLCZ$ = "008008008"
Const FogColorHCZ$ = "010005005"
Const FogColorEZ$ = "010010015"
Const FogColorStorageTunnels$ = "005015003"
Const FogColorOutside$ = "255255255"
Const FogColordimension_1499$ = "096097104"
Const FogColorPD$ = "000000000"
Const FogColorPDTrench$ = "038055047"
Const FogColorForest$ = "098133162"
;[End Block]

Function MainLoop()
	CatchErrors("Uncaught (MainLoop)")
	
	Local e.Events, r.Rooms
	Local i%
	
	While fps\Accumulator > 0.0
		fps\Accumulator = fps\Accumulator - TICK_DURATION
		If fps\Accumulator =< 0.0 Then CaptureWorld()
		
		If MenuOpen Lor ConsoleOpen Then fps\Factor[0] = 0.0
		
		If Input_ResetTime > 0.0 Then
			Input_ResetTime = Max(Input_ResetTime - fps\Factor[0], 0.0)
		Else
			mo\DoubleClick = False
			mo\MouseHit1 = MouseHit(1)
			If mo\MouseHit1 Then
				If MilliSecs2() - mo\LastMouseHit1 < 800 Then mo\DoubleClick = True
				mo\LastMouseHit1 = MilliSecs2()
			EndIf
			
			Local PrevMouseDown1% = mo\MouseDown1
			
			mo\MouseDown1 = MouseDown(1)
			If PrevMouseDown1 = True And (Not mo\MouseDown1) Then 
				mo\MouseUp1 = True 
			Else
				mo\MouseUp1 = False
			EndIf
			
			mo\MouseHit2 = MouseHit(2)
		EndIf
		
		If (Not mo\MouseDown1) And (Not mo\MouseHit1) Then GrabbedEntity = 0
		
		If mm\ShouldDeleteGadgets Then
			DeleteMenuGadgets()
		EndIf
		mm\ShouldDeleteGadgets = False
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		
		UpdateStreamSounds()
		
		If ga\DrawHandIcon Then ga\DrawHandIcon = False
		For i = 0 To 3
			If ga\DrawArrowIcon[i] Then ga\DrawArrowIcon[i] = False
		Next
		
		me\RestoreSanity = True
		ShouldEntitiesFall = True
		
		If fps\Factor[0] > 0.0 And PlayerRoom\RoomTemplate\Name <> "dimension_1499" Then UpdateSecurityCams()
		
		If (Not MenuOpen) And (Not ConsoleOpen) And me\EndingTimer >= 0.0 Then
			ShouldPlay = Min(me\Zone, 2.0)
		EndIf
		
		If PlayerRoom\RoomTemplate\Name <> "dimension_106" And PlayerRoom\RoomTemplate\Name <> "gate_b" And PlayerRoom\RoomTemplate\Name <> "gate_a" And (Not MenuOpen) And (Not ConsoleOpen) Then 
			If Rand(1500) = 1 Then
				For i = 0 To 5
					If AmbientSFX(i, CurrAmbientSFX) <> 0 Then
						If (Not ChannelPlaying(AmbientSFXCHN)) Then FreeSound_Strict(AmbientSFX(i, CurrAmbientSFX)) : AmbientSFX(i, CurrAmbientSFX) = 0
					EndIf			
				Next
				
				PositionEntity(SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
				
				If Rand(3) = 1 Then me\Zone = 3
				
				If PlayerRoom\RoomTemplate\Name = "cont1_173_intro" Then 
					me\Zone = 4
				ElseIf PlayerRoom\RoomTemplate\Name = "cont2_860_1"
					If forest_event\EventState = 1.0 Then
						me\Zone = 5
						PositionEntity(SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
					EndIf
				EndIf
				
				CurrAmbientSFX = Rand(0, AmbientSFXAmount[me\Zone] - 1)
				
				Select me\Zone
					Case 0, 1, 2
						;[Block]
						If (Not AmbientSFX(me\Zone, CurrAmbientSFX)) Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Zone" + (me\Zone + 1) + "\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 3
						;[Block]
						If (Not AmbientSFX(me\Zone, CurrAmbientSFX)) Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\General\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 4
						;[Block]
						If (Not AmbientSFX(me\Zone, CurrAmbientSFX)) Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Pre-breach\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 5
						;[Block]
						If (Not AmbientSFX(me\Zone, CurrAmbientSFX)) Then AmbientSFX(me\Zone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Forest\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
				End Select
				
				AmbientSFXCHN = PlaySound2(AmbientSFX(me\Zone, CurrAmbientSFX), Camera, SoundEmitter)
			EndIf
			UpdateSoundOrigin(AmbientSFXCHN, Camera, SoundEmitter)
			
			If Rand(50000) = 3 Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				
				If RN <> "cont2_860_1" And RN <> "cont2_1123" And RN <> "cont1_173_intro" And RN <> "dimension_1499" And RN <> "dimension_106" Then
					If fps\Factor[0] > 0.0 Then me\LightBlink = Rnd(1.0, 2.0)
					PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(1, 8) + ".ogg"))
				EndIf 
			EndIf
		EndIf
		
		UpdateCheckpoint1 = False
		UpdateCheckpoint2 = False
		
		If (Not MenuOpen) And (Not ConsoleOpen) And me\EndingTimer >= 0.0 Then
			LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
			If PlayerRoom\RoomTemplate\Name = "cont1_173_intro" Lor PlayerRoom\RoomTemplate\Name = "gate_b" Lor PlayerRoom\RoomTempLate\Name = "gate_a" Then
				CameraFogMode(Camera, 0)
				CameraFogRange(Camera, 5.0, 30.0)
				CameraRange(Camera, 0.01, 60.0)
				HideEntity(t\OverlayID[0])
			Else
				CameraFogMode(Camera, 1)
				CameraFogRange(Camera, opt\CameraFogNear * LightVolume, opt\CameraFogFar * LightVolume)
				CameraRange(Camera, 0.01, Min(opt\CameraFogFar * LightVolume * 1.5, 28.0))
				ShowEntity(t\OverlayID[0])
			EndIf
			For r.Rooms = Each Rooms
				For i = 0 To r\MaxLights - 1
					If r\Lights[i] <> 0 Then
						EntityAutoFade(r\LightSprites[i], opt\CameraFogNear * LightVolume, opt\CameraFogFar * LightVolume)
					EndIf
				Next
			Next
			me\SndVolume = CurveValue(0.0, me\SndVolume, 5.0)
			
			CanSave = True
			UpdateDeaf()
			UpdateEmitters()
			If PlayerRoom\RoomTemplate\Name = "dimension_1499" And QuickLoadPercent > 0 And QuickLoadPercent < 100 Then ShouldEntitiesFall = False
			UpdateMouseLook()
			UpdateMoving()
			InFacility = CheckForPlayerInFacility()
			If PlayerRoom\RoomTemplate\Name = "dimension_1499"
				If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
					UpdateDimension1499()
				EndIf
				UpdateLeave1499()
			Else
				UpdateDoors()
				UpdateScreens()
				UpdateRoomLights(Camera)
				If PlayerRoom\RoomTemplate\Name = "gate_b" Lor PlayerRoom\RoomTemplate\Name = "gate_a"Then
					If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
						UpdateEndings()
					EndIf
				Else
					If QuickLoadPercent = -1 Lor QuickLoadPercent = 100
						UpdateEvents()
					EndIf
					TimeCheckpointMonitors()
					UpdateVomit()
				EndIf
			EndIf
			UpdateDecals()
			UpdateMTF()
			UpdateNPCs()
			UpdateItems()
			UpdateParticles()
			Use427()
			UpdateMonitorSaving()
		EndIf
		
		Local CurrFogColor$ = ""
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "room3_storage" And EntityY(me\Collider) < -4100.0 * RoomScale Then
				CurrFogColor = FogColorStorageTunnels
			ElseIf PlayerRoom\RoomTemplate\Name = "gate_b" Lor PlayerRoom\RoomTemplate\Name = "gate_a" Then
				CurrFogColor = FogColorOutside
			ElseIf PlayerRoom\RoomTemplate\Name = "dimension_1499"
				CurrFogColor = FogColordimension_1499
			ElseIf PlayerRoom\RoomTemplate\Name = "cont2_860_1"
				If forest_event\EventState = 1.0 Then CurrFogColor = FogColorForest
			ElseIf PlayerRoom\RoomTemplate\Name = "dimension_106"
				For e.Events = Each Events
					If e\EventID = e_dimension_106 Then
						If EntityY(me\Collider) > 2608.0 * RoomScale Lor e\EventState2 > 1.0 Then
							CurrFogColor = FogColorPDTrench
						ElseIf EntityY(me\Collider) >= 2000.0 * RoomScale And EntityY(me\Collider) =< 2608.0 * RoomScale
							CurrFogColor = FogColorHCZ
						Else
							CurrFogColor = FogColorPD
						EndIf
						Exit
					EndIf
				Next
			ElseIf PlayerRoom\RoomTemplate\Name = "room2_mt" And (EntityY(me\Collider, True) >= 8.0 And EntityY(me\Collider, True) =< 12.0) Then
				CurrFogColor = FogColorHCZ
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
		
		If fps\Factor[0] = 0.0 Then
			UpdateWorld(0.0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
		EndIf
		
		me\BlurVolume = Min(CurveValue(0.0, me\BlurVolume, 20.0), 0.95)
		If me\BlurTimer > 0.0 Then
			me\BlurVolume = Max(Min(0.95, me\BlurTimer / 1000.0), me\BlurVolume)
			me\BlurTimer = Max(me\BlurTimer - fps\Factor[0], 0.0)
		EndIf
		
		Local DarkA# = 0.0
		
		If (Not MenuOpen)  Then
			If me\Sanity < 0.0 Then
				If me\RestoreSanity Then me\Sanity = Min(me\Sanity + fps\Factor[0], 0.0)
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
				me\EyeStuck = Max(me\EyeStuck - fps\Factor[0], 0.0)
				
				If me\EyeStuck < 9000.0 Then me\BlurTimer = Max(me\BlurTimer, (9000.0 - me\EyeStuck) * 0.5)
				If me\EyeStuck < 6000.0 Then DarkA = Min(Max(DarkA, (6000.0 - me\EyeStuck) / 5000.0), 1.0)
				If me\EyeStuck < 9000.0 And me\EyeStuck + fps\Factor[0] >= 9000.0 Then 
					CreateMsg("The eyedrops are causing your eyes to tear up.")
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
						Case EXTREME
							;[Block]
							me\BLINKFREQ = Rnd(200.0, 400.0)
							;[End Block]
					End Select 
					me\BlinkTimer = me\BLINKFREQ
				EndIf
				me\BlinkTimer = me\BlinkTimer - fps\Factor[0]
			Else
				me\BlinkTimer = me\BlinkTimer - (fps\Factor[0] * 0.6 * me\BlinkEffect)
				If wi\NightVision = 0 And (Not wi\SCRAMBLE) Then
					If me\EyeIrritation > 0.0 Then me\BlinkTimer = me\BlinkTimer - Min(me\EyeIrritation / 100.0 + 1.0, 4.0) * fps\Factor[0]
				EndIf
				DarkA = Max(DarkA, 0.0)
			EndIf
			
			me\EyeIrritation = Max(0.0, me\EyeIrritation - fps\Factor[0])
			
			If me\BlinkEffectTimer > 0.0 Then
				me\BlinkEffectTimer = me\BlinkEffectTimer - (fps\Factor[0] / 70.0)
			Else
				If me\BlinkEffect <> 1.0 Then me\BlinkEffect = 1.0
			EndIf
			
			me\LightBlink = Max(me\LightBlink - (fps\Factor[0] / 35.0), 0.0)
			If me\LightBlink > 0.0 And wi\NightVision = 0 Then DarkA = Min(Max(DarkA, me\LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If I_294\Using Then DarkA = 1.0
			
			If wi\NightVision = 0 Then DarkA = Max((1.0 - SecondaryLightOn) * 0.9, DarkA)
			
			If me\KillTimer < 0.0 Then
				NullSelectedStuff()
				me\BlurTimer = Abs(me\KillTimer * 5.0)
				me\KillTimer = me\KillTimer - (fps\Factor[0] * 0.8)
				If me\KillTimer < -360.0 Then 
					MenuOpen = True 
					If me\SelectedEnding <> -1 Then me\EndingTimer = Min(me\KillTimer, -0.1)
				EndIf
				DarkA = Max(DarkA, Min(Abs(me\KillTimer / 400.0), 1.0))
			Else
				HideEntity(t\OverlayID[9])
			EndIf
			
			If me\FallTimer < 0.0 Then
				If SelectedItem <> Null Then
					If Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Lor Instr(SelectedItem\ItemTemplate\TempName, "vest") Then
						If wi\HazmatSuit = 0 And wi\BallisticVest = 0 Then DropItem(SelectedItem)
					EndIf
				EndIf
				NullSelectedStuff()
				me\BlurTimer = Abs(me\FallTimer * 10.0)
				me\FallTimer = me\FallTimer - fps\Factor[0]
				DarkA = Max(DarkA, Min(Abs(me\FallTimer / 400.0), 1.0))				
			EndIf
			
			If SelectedItem <> Null And (Not InvOpen) And OtherOpen = Null Then
				If IsItemInFocus() Then
					DarkA = Max(DarkA, 0.5)
				EndIf
			EndIf
			
			If SelectedScreen <> Null Then DarkA = Max(DarkA, 0.5)
			
			EntityAlpha(t\OverlayID[5], DarkA)	
		EndIf
		
		If me\LightFlash > 0.0 Then
			ShowEntity(t\OverlayID[6])
			EntityAlpha(t\OverlayID[6], Max(Min(me\LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
			EntityColor(t\OverlayID[6], 255.0, 255.0, 255.0)
			me\LightFlash = Max(me\LightFlash - (fps\Factor[0] / 70.0), 0.0)
		Else
			HideEntity(t\OverlayID[6])
		EndIf
		
		UpdateWorld2()
		
		UpdateGUI()
		
		If KeyHit(key\INVENTORY) And SelectedDoor = Null And SelectedScreen = Null And (Not I_294\Using) Then
			If me\Playable And (Not me\Zombie) And me\VomitTimer >= 0.0 And me\KillTimer >= 0.0 And me\SelectedEnding = -1 Then
				Local W$ = ""
				Local V# = 0.0
				
				If SelectedItem <> Null
					W = SelectedItem\ItemTemplate\TempName
					V = SelectedItem\State
					; ~ Reset SCP-1025
					If SelectedItem\ItemTemplate\TempName = "scp1025" Then
						If SelectedItem\ItemTemplate\Img <> 0 Then
							FreeImage(SelectedItem\ItemTemplate\Img) : SelectedItem\ItemTemplate\Img = 0
						EndIf
					EndIf
				EndIf
				If (W <> "vest" And W <> "finevest" And W <> "hazmatsuit" And W <> "hazmatsuit2" And W <> "hazmatsuit3") Lor V = 0.0 Lor V = 100.0
					If InvOpen Then
						StopMouseMovement()
					Else
						mo\DoubleClickSlot = -1
					EndIf
					InvOpen = (Not InvOpen)
					If OtherOpen <> Null Then OtherOpen = Null
					SelectedItem = Null
				EndIf
			EndIf
		EndIf
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "cont1_173_intro" Then
				For e.Events = Each Events
					If e\EventID = e_cont1_173_intro Then
						If e\EventState3 >= 40.0 And e\EventState3 < 50.0 Then
							If InvOpen Then
								CreateHintMsg("Double click on the document to view it.")
								e\EventState3 = 50.0
							EndIf
						EndIf
						Exit
					EndIf
				Next
			EndIf
		EndIf
		
		If KeyHit(key\SAVE) Then
			If SelectedDifficulty\SaveType = SAVEANYWHERE Then
				If (Not CanSave) Lor QuickLoadPercent > -1 Then
					RN = PlayerRoom\RoomTemplate\Name
					If RN = "cont1_173_intro" Lor RN = "gate_b" Lor RN = "gate_a"
						CreateHintMsg("You can't save in this location.")
					Else
						CreateHintMsg("You can't save at this moment.")
						If QuickLoadPercent > -1 Then
							CreateHintMsg(msg\HintTxt + " (game is loading)")
						EndIf
					EndIf
				Else
					If as\Timer =< 70.0 * 5.0 Then
						CancelAutoSave()
					Else
						SaveGame(SavePath + CurrSave + "\")
					EndIf
				EndIf
			ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS
				If SelectedScreen = Null And SelectedMonitor = Null Then
					CreateHintMsg("Saving is only permitted on clickable monitors scattered throughout the facility.")
				Else
					RN = PlayerRoom\RoomTemplate\Name
					If RN = "cont1_173_intro" Lor RN = "gate_b" Lor RN = "gate_a"
						CreateHintMsg("You can't save in this location.")
					ElseIf (Not CanSave) Lor QuickLoadPercent > -1
						CreateHintMsg("You can't save at this moment.")
						If QuickLoadPercent > -1 Then
							CreateHintMsg(msg\HintTxt + " (game is loading)")
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
				CreateHintMsg("Quick saving is disabled.")
			EndIf
		ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS And (SelectedScreen <> Null Lor SelectedMonitor <> Null)
			If (msg\HintTxt <> "Game progress saved." And msg\HintTxt <> "You can't save in this location." And msg\HintTxt <> "You can't save at this moment.") Lor msg\HintTimer =< 0.0 Then
				CreateHintMsg("Press " + key\Name[key\SAVE] + " to save.")
			EndIf
			If mo\MouseHit2 Then SelectedMonitor = Null
		EndIf
		UpdateAutoSave()
		
		If KeyHit(key\CONSOLE) Then
			If opt\CanOpenConsole Then
				If ConsoleOpen Then
					UsedConsole = True
					ResumeSounds()
					StopMouseMovement()
					mm\ShouldDeleteGadgets = True
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		If me\EndingTimer < 0.0 Then
			If me\SelectedEnding <> -1 Then UpdateEnding()
		Else
			If me\SelectedEnding = -1 Then UpdateMenu()			
		EndIf
		
		UpdateMessages()
		UpdateHintMessages()
		UpdateSubtitles()
		
		UpdateConsole()
		
		UpdateQuickLoading()
		
		UpdateAchievementMsg()
	Wend
	
	; ~ Go out of function immediately if the game has been quit
	If MainMenuOpen Then Return
	
	If fps\Factor[0] > 0.0 And PlayerRoom\RoomTemplate\Name <> "dimension_1499" Then RenderSecurityCams()
	
	If (Not MenuOpen) And (Not ConsoleOpen) And me\EndingTimer >= 0.0 Then
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name <> "dimension_1499" Then
				RenderRoomLights(Camera)
			EndIf
		EndIf
	EndIf
	
	RenderWorld2(Max(0.0, 1.0 + (fps\Accumulator / TICK_DURATION)))
	
	RenderBlur(me\BlurVolume)
	
	RenderGUI()
	
	RenderMessages()
	RenderHintMessages()
	RenderSubtitles()
	
	If me\EndingTimer < 0.0 Then
		If me\SelectedEnding <> -1 Then RenderEnding()
	Else
		If me\SelectedEnding = -1 Then RenderMenu()			
	EndIf
	
	RenderConsole()
	
	RenderQuickLoading()
	
	RenderAchievementMsg()
	
	CatchErrors("MainLoop")
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
		If IsBloody Then ShowEntity(t\OverlayID[9])
		
		me\KillAnim = Rand(0, 1)
		PlaySound_Strict(DamageSFX[0])
		If SelectedDifficulty\SaveType = NOSAVES Then
			DeleteFile(CurrentDir() + SavePath + CurrSave + "\save.cb") 
			DeleteDir(SavePath + CurrSave)
			LoadSavedGames()
		EndIf
		
		me\KillTimer = Min(-1.0, me\KillTimer)
		ShowEntity(me\Head)
		PositionEntity(me\Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity(me\Head)
		RotateEntity(me\Head, 0.0, EntityYaw(Camera), 0.0)		
	EndIf
End Function

Function StopMouseMovement()
	MouseXSpeed() : MouseYSpeed() : MouseZSpeed()
	mo\Mouse_X_Speed_1 = 0.0
	mo\Mouse_Y_Speed_1 = 0.0
End Function

Function ResetInput()
	FlushKeys()
	FlushMouse()
	mo\MouseHit1 = False
	mo\MouseHit2 = False
	mo\MouseDown1 = False
	mo\MouseUp1 = False
	GrabbedEntity = 0
	Input_ResetTime = 10.0
End Function

Function NullSelectedStuff()
	InvOpen = False
	I_294\Using = False
	SelectedDoor = Null
	SelectedScreen = Null
	SelectedMonitor = Null
	OtherOpen = Null
	ClosestButton = 0
	GrabbedEntity = 0
End Function

Function ResetNegativeStats(Revive% = False)
	Local e.Events
	Local i%
	
	me\Injuries = 0.0
	me\Bloodloss = 0.0
	
	me\BlurTimer = 0.0
	me\LightFlash = 0.0
	me\LightBlink = 0.0
	me\CameraShake = 0.0
	
	me\DeafTimer = 0.0
	
	me\DeathTimer = 0.0
	
	me\VomitTimer = 0.0
	me\HeartBeatVolume = 0.0
	
	If me\BlinkEffect > 1.0 Then 
		me\BlinkEffect = 1.0
		me\BlinkEffectTimer = 0.0
	EndIf
	
	If me\StaminaEffect > 1.0 Then
		me\StaminaEffect = 1.0
		me\StaminaEffectTimer = 0.0
	EndIf
	me\Stamina = 100.0
	
	For i = 0 To 6
		I_1025\State[i] = 0.0
	Next
	
	If I_427\Timer >= 70.0 * 360.0 Then I_427\Timer = 0.0
	I_008\Timer = 0.0
	I_409\Timer = 0.0
	
	If Revive Then
		ClearCheats()
		
		; ~ If death by SCP-173 or SCP-106, enable GodMode, prevent instant death again -- Salvage
		If Curr173 <> Null Then
			If Curr173\Idle = 1 Then
				CreateConsoleMsg("Death by SCP-173 causes GodMode to be enabled!")
				chs\GodMode = True
				Curr173\Idle = 0
			EndIf
		ElseIf Curr106 <> Null
			If EntityDistanceSquared(me\Collider, Curr173\Collider) < 4.0 Then
				CreateConsoleMsg("Death by SCP-173 causes GodMode to be enabled!")
				chs\GodMode = True
			EndIf
		EndIf
		
		me\DropSpeed = -0.1
		me\HeadDropSpeed = 0.0
		me\Shake = 0.0
		me\CurrSpeed = 0.0
		
		me\FallTimer = 0.0
		MenuOpen = False
		
		ShowEntity(me\Collider)
		
		me\KillTimer = 0.0
		me\KillAnim = 0
	EndIf
End Function

Function SetCrouch(NewCrouch%)
	Local Temp%
	
	If me\Stamina > 0.0 Then 
		If NewCrouch <> me\Crouch Then 
			PlaySound_Strict(CrouchSFX)
			me\Stamina = me\Stamina - Rnd(8.0, 16.0)
		EndIf
		If me\Stamina < 10.0 Then
			Temp = 0
			If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
			If (Not ChannelPlaying(BreathCHN)) Then BreathCHN = PlaySound_Strict(BreathSFX((Temp), 0))
		EndIf
		me\Crouch = NewCrouch
	EndIf
End Function

Function InjurePlayer(Injuries_#, Infection# = 0.0, BlurTimer_# = 0.0, VestFactor# = 0.0, HelmetFactor# = 0.0)
	If Injuries_ <> 0.0 Then me\Injuries = me\Injuries + Injuries_ - ((wi\BallisticVest = 1) * VestFactor) - ((wi\BallisticVest = 2) * VestFactor * 1.35) - (me\Crouch * wi\BallisticHelmet * HelmetFactor)
	If BlurTimer_ <> 0.0 And Injuries_ <> 0.0 Then me\BlurTimer = BlurTimer_
	If Infection <> 0.0 Then I_008\Timer = I_008\Timer + (Infection * (wi\HazmatSuit = 0))
End Function

Function UpdateMoving()
	CatchErrors("Uncaught (UpdateMoving)")
	
	Local de.Decals
	Local Sprint# = 1.0, Speed# = 0.018
	Local Pvt%, i%, Angle#
	
	If chs\SuperMan Then
		Speed = Speed * 3.0
		
		chs\SuperManTimer = chs\SuperManTimer + fps\Factor[0]
		
		me\CameraShake = Sin(chs\SuperManTimer / 5.0) * (chs\SuperManTimer / 1500.0)
		
		If chs\SuperManTimer > 70.0 * 50.0 Then
			msg\DeathMsg = "A Class D jumpsuit found in [DATA REDACTED]. Upon further examination, the jumpsuit was found to be filled with 12.5 kilograms of blue ash-like substance. "
			msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the substance remains non-conclusive. Most likely related to SCP-914."
			Kill()
			ShowEntity(t\OverlayID[0])
		Else
			me\BlurTimer = 500.0		
			HideEntity(t\OverlayID[0])
		EndIf
	EndIf
	
	If me\DeathTimer > 0.0 Then
		me\DeathTimer = me\DeathTimer - fps\Factor[0]
		If me\DeathTimer < 1.0 Then me\DeathTimer = -1.0
	ElseIf me\DeathTimer < 0.0 
		Kill()
	EndIf
	
	If me\CurrSpeed > 0.0 Then
		me\Stamina = Min(me\Stamina + 0.15 * fps\Factor[0] / 1.25, 100.0)
	Else
		me\Stamina = Min(me\Stamina + 0.15 * fps\Factor[0] * 1.25, 100.0)
	EndIf
	
	If me\StaminaEffectTimer > 0.0 Then
		me\StaminaEffectTimer = me\StaminaEffectTimer - (fps\Factor[0] / 70.0)
	Else
		If me\StaminaEffect <> 1.0 Then me\StaminaEffect = 1.0
	EndIf
	
	Local Temp#, Temp3%
	
	If me\KillTimer >= 0.0 Then
		If PlayerRoom\RoomTemplate\Name <> "dimension_106" Then 
			If (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) And (Not chs\NoClip) Then
				If me\Stamina < 5.0 Then
					Temp = 0
					If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
					If (Not ChannelPlaying(BreathCHN)) Then BreathCHN = PlaySound_Strict(BreathSFX((Temp), 0))
				ElseIf me\Stamina < 40.0
					If (Not BreathCHN) Then
						Temp = 0.0
						If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
						BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
						ChannelVolume(BreathCHN, Min((70.0 - me\Stamina) / 70.0, 1.0) * opt\SFXVolume)
					Else
						If (Not ChannelPlaying(BreathCHN)) Then
							Temp = 0.0
							If wi\GasMask > 0 Lor I_1499\Using > 0 Then Temp = 1
							BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
							ChannelVolume(BreathCHN, Min((70.0 - me\Stamina) / 70.0, 1.0) * opt\SFXVolume)		
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount - 1
		If Inventory(i) <> Null Then
			If Inventory(i)\ItemTemplate\TempName = "finevest" Then me\Stamina = Min(me\Stamina, 60.0)
		EndIf
	Next
	
	If I_714\Using Then
		me\Stamina = Min(me\Stamina, 10.0)
		me\Sanity = Max(-850.0, me\Sanity)
	EndIf
	
	If me\Zombie Then 
		If me\Crouch Then SetCrouch(False)
	EndIf
	
	If Abs(me\CrouchState - me\Crouch) < 0.001 Then 
		me\CrouchState = me\Crouch
	Else
		me\CrouchState = CurveValue(me\Crouch, me\CrouchState, 10.0)
	EndIf
	
	If SelectedDoor = Null And SelectedScreen = Null And (Not I_294\Using) Then
		If (Not chs\NoClip) Then 
			If (me\Playable And (KeyDown(key\MOVEMENT_DOWN) Xor KeyDown(key\MOVEMENT_UP)) Lor (KeyDown(key\MOVEMENT_RIGHT) Xor KeyDown(key\MOVEMENT_LEFT))) Lor me\ForceMove > 0.0 Then
				If (Not me\Crouch) And (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) And me\Stamina > 0.0 And (Not me\Zombie) Then
					Sprint = 2.5
					me\Stamina = me\Stamina - fps\Factor[0] * 0.4 * me\StaminaEffect
					If me\Stamina =< 0.0 Then me\Stamina = -20.0
				EndIf
				
				If PlayerRoom\RoomTemplate\Name = "dimension_106" Then 
					If EntityY(me\Collider) < 2000.0 * RoomScale Lor EntityY(me\Collider) > 2608.0 * RoomScale Then
						me\Stamina = 0.0
						Speed = 0.015
						Sprint = 1.0					
					EndIf
				EndIf	
				
				If InvOpen Lor OtherOpen <> Null Then Speed = 0.009
				
				If me\ForceMove > 0.0 Then Speed = Speed * me\ForceMove
				
				If SelectedItem <> Null Then
					If SelectedItem\ItemTemplate\TempName = "firstaid" Lor SelectedItem\ItemTemplate\TempName = "finefirstaid" Lor SelectedItem\ItemTemplate\TempName = "firstaid2" Then
						Sprint = 0.0
					EndIf
				EndIf
				
				Temp = (me\Shake Mod 360.0)
				
				Local TempCHN%
				
				If me\Playable Then me\Shake = ((me\Shake + fps\Factor[0] * Min(Sprint, 1.5) * 7.0) Mod 720.0)
				If Temp < 180.0 And (me\Shake Mod 360.0) >= 180.0 And me\KillTimer >= 0.0 Then
					If CurrStepSFX = 0 Then
						Temp = GetStepSound(me\Collider)
						
						If PlayerRoom\RoomTemplate\Name = "dimension_106" Lor PlayerRoom\RoomTemplate\Name = "room2_scientists_2" Then
							Temp3 = 5
						Else
							Temp3 = 0
						EndIf
						
						If Sprint = 1.0 Then
							me\SndVolume = Max(4.0, me\SndVolume)
							TempCHN = PlaySound_Strict(StepSFX(Temp, 0, Rand(0, 7 - Temp3)))
							ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume)
						Else
							me\SndVolume = Max(2.5 - (me\Crouch * 0.6), me\SndVolume)
							TempCHN = PlaySound_Strict(StepSFX(Temp, 1 - (Temp3 / 5), Rand(0, 7 - Temp3)))
							ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.6)) * opt\SFXVolume)
						EndIf
					ElseIf CurrStepSFX = 1
						TempCHN = PlaySound_Strict(StepSFX(2, 0, Rand(0, 2)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.4)) * opt\SFXVolume)
					ElseIf CurrStepSFX = 2
						TempCHN = PlaySound_Strict(StepSFX(3, 0, Rand(0, 2)))
						ChannelVolume(TempCHN, (1.0 - (me\Crouch * 0.4)) * opt\SFXVolume)
					EndIf
				EndIf	
			EndIf
		Else
			If (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) Then 
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
			
			RotateEntity(me\Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0.0)
			
			Temp2 = Temp2 * chs\NoClipSpeed
			
			If KeyDown(key\MOVEMENT_DOWN) Then MoveEntity(me\Collider, 0.0, 0.0, (-Temp2) * fps\Factor[0])
			If KeyDown(key\MOVEMENT_UP) Then MoveEntity(me\Collider, 0.0, 0.0, Temp2 * fps\Factor[0])
			
			If KeyDown(key\MOVEMENT_LEFT) Then MoveEntity(me\Collider, (-Temp2) * fps\Factor[0], 0.0, 0.0)
			If KeyDown(key\MOVEMENT_RIGHT) Then MoveEntity(me\Collider, Temp2 * fps\Factor[0], 0.0, 0.0)
			
			ResetEntity(me\Collider)
		Else
			Temp2 = Temp2 / Max((me\Injuries + 3.0) / 3.0, 1.0)
			If me\Injuries > 0.5 Then Temp2 = Temp2 * Min((Sin(me\Shake / 2.0) + 1.2), 1.0)
			Temp = False
			If (Not me\Zombie) Then
				If KeyDown(key\MOVEMENT_DOWN) And me\Playable Then
					If (Not KeyDown(key\MOVEMENT_UP)) Then
						Temp = True
						Angle = 180.0
						If KeyDown(key\MOVEMENT_LEFT) Then
							If (Not KeyDown(key\MOVEMENT_RIGHT)) Then Angle = 135.0
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
						If (Not KeyDown(key\MOVEMENT_RIGHT)) Then Angle = 45.0
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
			
			If me\Playable Then TranslateEntity(me\Collider, Cos(Angle) * me\CurrSpeed * fps\Factor[0], 0.0, Sin(Angle) * me\CurrSpeed * fps\Factor[0], True)
			
			Local CollidedFloor% = False
			
			For i = 1 To CountCollisions(me\Collider)
				If CollisionY(me\Collider, i) < EntityY(me\Collider) - 0.25 Then CollidedFloor = True
			Next
			
			If CollidedFloor Then
				If PlayerRoom\RoomTemplate\Name = "dimension_106" Lor PlayerRoom\RoomTemplate\Name = "room2_scientists_2" Then
					Temp3 = 5
				Else
					Temp3 = 0
				EndIf
				
				If me\DropSpeed < -0.07 Then 
					If CurrStepSFX = 0 Then
						PlaySound_Strict(StepSFX(GetStepSound(me\Collider), 0, Rand(0, 7 - Temp3)))
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
						me\DropSpeed = Min(Max(me\DropSpeed - 0.006 * fps\Factor[0], -2.0), 0.0)
					Else
						me\DropSpeed = 0.0
					EndIf
				Else
					me\DropSpeed = Min(Max(me\DropSpeed - 0.006 * fps\Factor[0], -2.0), 0.0)
				EndIf
			EndIf
			PlayerFallingPickDistance = 10.0
			
			If me\Playable And ShouldEntitiesFall Then TranslateEntity(me\Collider, 0.0, me\DropSpeed * fps\Factor[0], 0.0)
		EndIf
		
		me\ForceMove = False
	EndIf
	
	If me\Injuries > 1.0 Then
		Temp2 = me\Bloodloss
		me\BlurTimer = Max(Max(Sin(MilliSecs2() / 100.0) * me\Bloodloss * 30.0, me\Bloodloss * 2.0 * (2.0 - me\CrouchState)), me\BlurTimer)
		If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
			me\Bloodloss = Min(me\Bloodloss + (Min(me\Injuries, 3.5) / 300.0) * fps\Factor[0], 100.0)
		EndIf
		If Temp2 =< 60.0 And me\Bloodloss > 60.0 Then
			CreateMsg("You are feeling faint from the amount of blood you have lost.")
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
			
			de.Decals = CreateDecal(Rand(16, 17), PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, Rnd(0.03, 0.08) * Min(me\Injuries, 2.5))
			de\SizeChange = Rnd(0.001, 0.0015) : de\MaxSize = de\Size + Rnd(0.008, 0.009)
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 2)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * opt\SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			
			FreeEntity(Pvt)
		EndIf
		
		me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * me\Bloodloss * 0.2)
		
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
		me\HealTimer = me\HealTimer - (fps\Factor[0] / 70.0)
		me\Bloodloss = Min(me\Bloodloss + (2.0 / 400.0) * fps\Factor[0], 100.0)
		me\Injuries = Max(me\Injuries - (fps\Factor[0] / 70.0) / 30.0, 0.0)
	EndIf
		
	If me\Playable Then
		If KeyHit(key\BLINK) Then me\BlinkTimer = 0.0
		If KeyDown(key\BLINK) And me\BlinkTimer < -10.0 Then me\BlinkTimer = -10.0
	EndIf
	
	If me\HeartBeatVolume > 0.0 Then
		If me\HeartBeatTimer =< 0.0 Then
			TempCHN = PlaySound_Strict(HeartBeatSFX)
			ChannelVolume(TempCHN, me\HeartBeatVolume * opt\SFXVolume)
			
			me\HeartBeatTimer = 70.0 * (60.0 / Max(me\HeartBeatRate, 1.0))
		Else
			me\HeartBeatTimer = me\HeartBeatTimer - fps\Factor[0]
		EndIf
		me\HeartBeatVolume = Max(me\HeartBeatVolume - fps\Factor[0] * 0.05, 0.0)
	EndIf
	
	CatchErrors("UpdateMoving")
End Function

Function UpdateMouseLook()
	CatchErrors("Uncaught (UpdateMouseLook)")
	
	Local p.Particles
	Local i%
	
	me\CameraShake = Max(me\CameraShake - (fps\Factor[0] / 10.0), 0.0)
	me\BigCameraShake = Max(me\BigCameraShake - (fps\Factor[0] / 10.0), 0.0)
	
	CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / (Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * (Float(opt\RealGraphicWidth) / Float(opt\RealGraphicHeight)))) / 2.0)))
	me\CurrCameraZoom = Max(me\CurrCameraZoom - fps\Factor[0], 0.0)
	
	If me\KillTimer >= 0.0 And me\FallTimer >= 0.0 Then
		me\HeadDropSpeed = 0.0
		
		If IsNaN(EntityX(me\Collider)) Then
			PositionEntity(me\Collider, EntityX(Camera, True), EntityY(Camera, True) - 0.5, EntityZ(Camera, True), True)
			CreateConsoleMsg("RESETTING COORDINATES! New coordinates: " + EntityX(me\Collider))				
		EndIf
		
		Local Up# = (Sin(me\Shake) / (20.0 + me\CrouchState * 20.0)) * 0.6		
		Local Roll# = Max(Min(Sin(me\Shake / 2.0) * 2.5 * Min(me\Injuries + 0.25, 3.0), 8.0), -8.0)
		
		PositionEntity(Camera, EntityX(me\Collider), EntityY(me\Collider) + Up + 0.6 + me\CrouchState * (-0.3), EntityZ(me\Collider))
		RotateEntity(Camera, 0.0, EntityYaw(me\Collider), Roll * 0.5)
		
		; ~ Update the smoothing que to smooth the movement of the mouse
		mo\Mouse_X_Speed_1 = CurveValue(MouseXSpeed() * (opt\MouseSensitivity + 0.6) , mo\Mouse_X_Speed_1, (6.0 / (opt\MouseSensitivity + 1.0)) * opt\MouseSmoothing)
		If IsNaN(mo\Mouse_X_Speed_1) Then mo\Mouse_X_Speed_1 = 0.0
		If opt\InvertMouse Then
			mo\Mouse_Y_Speed_1 = CurveValue(-MouseYSpeed() * (opt\MouseSensitivity + 0.6), mo\Mouse_Y_Speed_1, (6.0 / (opt\MouseSensitivity + 1.0)) * opt\MouseSmoothing)
		Else
			mo\Mouse_Y_Speed_1 = CurveValue(MouseYSpeed() * (opt\MouseSensitivity + 0.6), mo\Mouse_Y_Speed_1, (6.0 / (opt\MouseSensitivity + 1.0)) * opt\MouseSmoothing)
		EndIf
		If IsNaN(mo\Mouse_Y_Speed_1) Then mo\Mouse_Y_Speed_1 = 0.0
		
		If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor SelectedDoor <> Null Lor SelectedScreen <> Null Then StopMouseMovement()
		
		Local The_Yaw# = ((mo\Mouse_X_Speed_1)) * mo\Mouselook_X_Inc / (1.0 + wi\BallisticVest)
		Local The_Pitch# = ((mo\Mouse_Y_Speed_1)) * mo\Mouselook_Y_Inc / (1.0 + wi\BallisticVest)
		
		TurnEntity(me\Collider, 0.0, -The_Yaw, 0.0) ; ~ Turn the user on the Y (Yaw) axis
		CameraPitch = CameraPitch + The_Pitch
		; ~ Limit the user's camera to within 180.0 degrees of pitch rotation. Returns useless values so we need to use a variable to keep track of the camera pitch
		If CameraPitch > 70.0 Then CameraPitch = 70.0
		If CameraPitch < -70.0 Then CameraPitch = -70.0
		
		Local ShakeTimer# = me\CameraShake + me\BigCameraShake
		
		RotateEntity(Camera, WrapAngle(CameraPitch + Rnd(-ShakeTimer, ShakeTimer)), WrapAngle(EntityYaw(me\Collider) + Rnd(-ShakeTimer, ShakeTimer)), Roll) ; ~ Pitch the user's camera up and down
		
		If PlayerRoom\RoomTemplate\Name = "dimension_106" Then
			If EntityY(me\Collider) < 2000.0 * RoomScale Lor EntityY(me\Collider) > 2608.0 * RoomScale Then
				RotateEntity(Camera, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), Roll + WrapAngle(Sin(MilliSecs2() / 150.0) * 30.0)) ; ~ Pitch the user's camera up and down
			EndIf
		EndIf
	Else
		HideEntity(me\Collider)
		PositionEntity(Camera, EntityX(me\Head), EntityY(me\Head), EntityZ(me\Head))
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(me\Head)
			If CollisionY(me\Head, i) < EntityY(me\Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor Then
			me\HeadDropSpeed = 0.0
		Else
			If (Not me\KillAnim) Then 
				MoveEntity(me\Head, 0.0, 0.0, me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(-90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity(me\Head, 0.0, 0.0, -me\HeadDropSpeed)
				RotateEntity(me\Head, CurveAngle(90.0, EntityPitch(me\Head), 20.0), EntityYaw(me\Head), EntityRoll(me\Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(me\Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			me\HeadDropSpeed = me\HeadDropSpeed - (0.002 * fps\Factor[0])
		EndIf
		
		If opt\InvertMouse Then
			TurnEntity(Camera, (-MouseYSpeed()) * 0.05 * fps\Factor[0], (-MouseXSpeed()) * 0.15 * fps\Factor[0], 0.0)
		Else
			TurnEntity(Camera, MouseYSpeed() * 0.05 * fps\Factor[0], (-MouseXSpeed()) * 0.15 * fps\Factor[0], 0.0)
		EndIf
	EndIf
	
	UpdateDust()
	
	; ~ Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop
	If (Not InvOpen) And (Not I_294\Using) And OtherOpen = Null And SelectedDoor = Null And SelectedScreen = Null Then
		If (MouseX() > mo\Mouse_Right_Limit) Lor (MouseX() < mo\Mouse_Left_Limit) Lor (MouseY() > mo\Mouse_Bottom_Limit) Lor (MouseY() < mo\Mouse_Top_Limit)
			MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
		EndIf
	EndIf
	
	If wi\GasMask > 0 Lor I_1499\Using > 0 Then
		If (Not I_714\Using) Then
			If wi\GasMask = 2 Lor I_1499\Using = 2 Then me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.01 * fps\Factor[0])
		EndIf
		If me\KillTimer >= 0.0 Then
			If (Not ChannelPlaying(BreathCHN)) Then
				If (Not ChannelPlaying(BreathGasRelaxedCHN)) Then BreathGasRelaxedCHN = PlaySound_Strict(BreathGasRelaxedSFX)
			Else
				If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
			EndIf
		EndIf
		
		ShowEntity(t\OverlayID[1])
		If wi\GasMaskFogTimer > 0.0 Then ShowEntity(t\OverlayID[10])
		
		If ChannelPlaying(BreathCHN) Then
			wi\GasMaskFogTimer = Min(wi\GasMaskFogTimer + (fps\Factor[0] * 2.0), 100.0)
		Else
			If wi\GasMask = 2 Lor I_1499\Using = 2 Then
				If me\CurrSpeed > 0.0 And (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) Then
					wi\GasMaskFogTimer = Min(wi\GasMaskFogTimer + (fps\Factor[0] * 0.2), 100.0)
				Else
					wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fps\Factor[0] * 0.32))
				EndIf
			Else
				wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fps\Factor[0] * 0.32))
			EndIf
		EndIf
		EntityAlpha(t\OverlayID[10], Min(((wi\GasMaskFogTimer * 0.2) ^ 2.0) / 1000.0, 0.45))
	Else
		If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
		wi\GasMaskFogTimer = Max(0.0, wi\GasMaskFogTimer - (fps\Factor[0] * 0.32))
		HideEntity(t\OverlayID[1])
		HideEntity(t\OverlayID[10])
	EndIf
	
	If wi\HazmatSuit > 0 Then
		If wi\HazmatSuit = 1 Then
			me\Stamina = Min(60.0, me\Stamina)
		EndIf
		If (Not I_714\Using) Then
			If wi\HazmatSuit = 2 Then me\Stamina = Min(100.0, me\Stamina + (100.0 - me\Stamina) * 0.01 * fps\Factor[0])
		EndIf
		ShowEntity(t\OverlayID[2])
	Else
		HideEntity(t\OverlayID[2])
	EndIf
	
	If wi\BallisticHelmet Then
		ShowEntity(t\OverlayID[8])
	Else
		HideEntity(t\OverlayID[8])
	EndIf
	
	If wi\NightVision > 0 Lor wi\SCRAMBLE Then
		ShowEntity(t\OverlayID[4])
		If wi\NightVision = 2 Then
			EntityColor(t\OverlayID[4], 0.0, 100.0, 255.0)
			AmbientLightRooms(15)
		ElseIf wi\NightVision = 3
			EntityColor(t\OverlayID[4], 255.0, 0.0, 0.0)
			AmbientLightRooms(15)
		ElseIf wi\NightVision = 1
			EntityColor(t\OverlayID[4], 0.0, 255.0, 0.0)
			AmbientLightRooms(15)
		Else
			EntityColor(t\OverlayID[4], 128.0, 128.0, 128.0)
			AmbientLightRooms(0)
		EndIf
		EntityTexture(t\OverlayID[0], t\MiscTextureID[20])
	Else
		AmbientLightRooms(0)
		HideEntity(t\OverlayID[4])
		EntityTexture(t\OverlayID[0], t\OverlayTextureID[0])
	EndIf
	
	Local Factor1025# = fps\Factor[0] * I_1025\State[7]
	
	For i = 0 To 6
		If I_1025\State[i] > 0.0 Then
			Select i
				Case 0 ; ~ Common cold
					;[Block]
					If fps\Factor[0] > 0.0 Then 
						If Rand(1000) = 1 Then
							If (Not CoughCHN) Then
								CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							Else
								If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							EndIf
						EndIf
					EndIf
					me\Stamina = me\Stamina - (Factor1025 * 0.3)
					;[End Block]
				Case 1 ; ~ Chicken pox
					;[Block]
					If Rand(9000) = 1 Then CreateMsg("Your skin is feeling itchy.")
					;[End Block]
				Case 2 ; ~ Cancer of the lungs
					;[Block]
					If fps\Factor[0] > 0.0 Then 
						If Rand(800) = 1 Then
							If (Not CoughCHN) Then
								CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							Else
								If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
							EndIf
						EndIf
					EndIf
					me\Stamina = me\Stamina - (Factor1025 * 0.1)
					;[End Block]
				Case 3 ; ~ Appendicitis
					; ~ 0.035 / sec = 2.1 / min
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
						I_1025\State[i] = I_1025\State[i] + (Factor1025 * 0.0005)
					EndIf
					If I_1025\State[i] > 20.0 Then
						If I_1025\State[i] - Factor1025 =< 20.0 Then CreateMsg("The pain in your stomach is becoming unbearable.")
						me\Stamina = me\Stamina - (Factor1025 * 0.3)
					ElseIf I_1025\State[i] > 10.0
						If I_1025\State[i] - Factor1025 =< 10.0 Then CreateMsg("Your stomach is aching.")
					EndIf
					;[End Block]
				Case 4 ; ~ Asthma
					;[Block]
					If me\Stamina < 35.0 Then
						If Rand(Int(140.0 + me\Stamina * 8.0)) = 1 Then
							If (Not CoughCHN) Then
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
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
						I_1025\State[i] = I_1025\State[i] + (Factor1025 * 0.35)
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
				Case 6 ; ~ Secondary polycythemia
					;[Block]
					If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
						I_1025\State[i] = I_1025\State[i] + 0.00025 * Factor1025 * (100.0 / I_1025\State[i])
					EndIf
					me\Stamina = Min(100.0, me\Stamina + (90.0 - me\Stamina) * I_1025\State[i] * Factor1025 * 0.00008)
					If I_1025\State[i] > 15.0 And I_1025\State[i] - Factor1025 =< 15.0 Then
						CreateMsg("You begin feeling energetic.")
					EndIf
					;[End Block]
			End Select 
		EndIf
	Next
	
	CatchErrors("UpdateMouseLook")
End Function

; ~ Navigator Constants
;[Block]
Const NAV_WIDTH% = 287
Const NAV_HEIGHT% = 256
;[End Block]

; ~ Iventory Constants
;[Block]
Const INVENTORY_GFX_SIZE% = 70
Const INVENTORY_GFX_SPACING% = 35
;[End Block]

Function UpdateGUI()
	CatchErrors("Uncaught (UpdateGUI)")
	
	Local e.Events, it.Items, r.Rooms
	Local Temp%, x%, y%, z%, i%
	Local x2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$
	
	If I_294\Using Then Update294()
	
	If ClosestButton <> 0 And (Not InvOpen) And (Not I_294\Using) And OtherOpen = Null And SelectedDoor = Null And SelectedScreen = Null And (Not MenuOpen) And (Not ConsoleOpen) Then
		If mo\MouseUp1 Then
			mo\MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf me\Playable Then
					UseDoor(ClosestDoor)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If SelectedScreen <> Null Then
		If mo\MouseUp1 Lor mo\MouseHit2 Then
			SelectedScreen = Null
			mo\MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If SelectedDoor <> Null Then
		If SelectedItem <> Null Then
			If SelectedItem\ItemTemplate\TempName = "scp005" Then
				UseDoor(SelectedDoor)
				ShouldDrawHUD = False
			Else
				SelectedItem = Null
			EndIf
		EndIf
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
			
			x = mo\Viewport_Center_X - ImageWidth(t\ImageID[4]) * (Scale / 2)
			y = mo\Viewport_Center_Y - ImageHeight(t\ImageID[4]) * (Scale / 2)	
			
			If msg\KeyPadMsg <> "" Then 
				msg\KeyPadTimer = msg\KeyPadTimer - fps\Factor[0]
				If msg\KeyPadTimer =< 0.0 Then
					msg\KeyPadMsg = ""
					SelectedDoor = Null
					StopMouseMovement()
				EndIf
			EndIf
			
			x = x + (44 * Scale)
			y = y + (249 * Scale)
			
			For n = 0 To 3
				For i = 0 To 2
					xTemp = x + Int(58.5 * Scale * n)
					yTemp = y + (67.0 * Scale) * i
					
					Temp = False
					If MouseOn(xTemp, yTemp, 54 * Scale, 65 * Scale) And msg\KeyPadMsg = "" Then
						If mo\MouseUp1 Then 
							PlaySound_Strict(ButtonSFX)
							
							Select (n + 1) + (i * 4)
								Case 1, 2, 3
									;[Block]
									msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4))
									;[End Block]
								Case 4
									;[Block]
									msg\KeyPadInput = msg\KeyPadInput + "0"
									;[End Block]
								Case 5, 6, 7
									;[Block]
									msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4) - 1)
									;[End Block]
								Case 8
									;[Block]
									UseDoor(SelectedDoor)
									If msg\KeyPadInput = SelectedDoor\Code Then
										SelectedDoor = Null
										StopMouseMovement()
									Else
										msg\KeyPadMsg = "ACCESS DENIED"
										msg\KeyPadTimer = 210.0
										msg\KeyPadInput = ""	
									EndIf
									;[End Block]
								Case 9, 10, 11
									;[Block]
									msg\KeyPadInput = msg\KeyPadInput + ((n + 1) + (i * 4) - 2)
									;[End Block]
								Case 12
									;[Block]
									msg\KeyPadInput = ""
									;[End Block]
							End Select 
							If Len(msg\KeyPadInput) > 4 Then msg\KeyPadInput = Left(msg\KeyPadInput, 4)
						EndIf
					Else
						Temp = False
					EndIf
				Next
			Next
			
			If mo\MouseHit2 Then
				SelectedDoor = Null
				StopMouseMovement()
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		If msg\KeyPadInput <> "" Then msg\KeyPadInput = ""
		If msg\KeyPadTimer <> 0.0 Then msg\KeyPadTimer = 0.0
		If msg\KeyPadMsg <> "" Then msg\KeyPadMsg = ""
	EndIf
	
	If KeyHit(1) And me\EndingTimer = 0.0 And me\SelectedEnding = -1 Then
		If MenuOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			StopMouseMovement()
		Else
			PauseSounds()
		EndIf
		mm\ShouldDeleteGadgets = True
		MenuOpen = (Not MenuOpen)
		
		mm\AchievementsMenu = 0
		OptionsMenu = 0
		QuitMsg = 0
		
		SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
		I_294\Using = False
		If SelectedItem <> Null Then
			If Instr(SelectedItem\ItemTemplate\TempName, "vest") Lor Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Then
				If wi\BallisticVest = 0 And wi\HazmatSuit = 0 Then DropItem(SelectedItem)
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local PrevOtherOpen.Items, PrevItem.Items
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
		
		InvOpen = False
		SelectedDoor = Null
		
		Local TempX% = 0
		
		x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * 10 / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE * (Float(OtherSize) / 10 * 2 - 1) - INVENTORY_GFX_SPACING
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n Then
				MouseSlot = n
			EndIf
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						If mo\MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							
							If mo\DoubleClick And mo\DoubleClickSlot = n Then
								If OtherOpen\SecondInv[n]\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[OtherOpen\SecondInv[n]\ItemTemplate\Sound])
								OtherOpen = Null
								ClosedInv = True
								InvOpen = False
								mo\DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And mo\MouseHit1 Then
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
				y = y + (INVENTORY_GFX_SIZE * 2)
				x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * 10 / 2 + INVENTORY_GFX_SPACING * (10 / 2 - 1.0)) / 2
			EndIf
		Next
		
		If mo\MouseHit1 Then
			mo\DoubleClickSlot = IsMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If (Not mo\MouseDown1) Then
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
					
					MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
				Else
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then
								PrevOtherOpen\SecondInv[z] = Null
								Exit
							EndIf
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						PrevItem = PrevOtherOpen\SecondInv[MouseSlot]
						
						Select SelectedItem\ItemTemplate\TempName
							Default
								;[Block]
								For z = 0 To MaxItemAmount - 1
									If PrevOtherOpen\SecondInv[z] = SelectedItem Then
										PrevOtherOpen\SecondInv[z] = PrevItem
										Exit
									EndIf
								Next
								PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
								SelectedItem = Null
								;[End Block]
						End Select					
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If ClosedInv And (Not InvOpen) Then 
			OtherOpen = Null
			StopMouseMovement()
		EndIf
	ElseIf InvOpen Then
		SelectedDoor = Null
		
		x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		If MaxItemAmount = 2 Then
			y = y + INVENTORY_GFX_SIZE
			x = x - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING) / 2
		EndIf
		
		ItemAmount = 0
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n Then
				MouseSlot = n
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						If mo\MouseHit1 Then
							SelectedItem = Inventory(n)
							
							If mo\DoubleClick And mo\DoubleClickSlot = n Then
								If wi\HazmatSuit > 0 And (Not Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit")) Then
									CreateMsg("You can't use any items while wearing a hazmat suit.")
									SelectedItem = Null
									Return
								EndIf
								If Inventory(n)\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[Inventory(n)\ItemTemplate\Sound])
								InvOpen = False
								mo\DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn = n And mo\MouseHit1 Then
					For z = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem Then
							Inventory(z) = Null
							Exit
						EndIf
					Next
					Inventory(n) = SelectedItem
					SelectedItem = Null
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If MaxItemAmount >= 4 And n = MaxItemAmount / 2 - 1 Then 
				y = y + INVENTORY_GFX_SIZE * 2 
				x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If mo\MouseHit1 Then
			mo\DoubleClickSlot = IsMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If (Not mo\MouseDown1) Then
				If MouseSlot = 66 Then
					Select SelectedItem\ItemTemplate\TempName
						Case "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
							;[Block]
							CreateHintMsg("Double click on this item to take it off.")
							;[End Block]
						Case "scp1499", "super1499"
							;[Block]
							If I_1499\Using > 0 Then
								CreateHintMsg("Double click on this item to take it off.")
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
							;[End Block]
						Case "gasmask", "gasmask3", "supergasmask"
							;[Block]
							If wi\GasMask > 0 Then
								CreateHintMsg("Double click on this item to take it off.")
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
							;[End Block]
						Case "helmet"
							;[Block]
							If wi\BallisticHelmet Then
								CreateHintMsg("Double click on this item to take it off.")
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
							;[End Block] 
						Case "nvg", "supernvg", "finenvg"
							;[Block]
							If wi\NightVision > 0 Then
								CreateHintMsg("Double click on this item to take it off.")
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
							;[End Block]
						Case "scramble"
							;[Block]
							If wi\SCRAMBLE Then
								CreateHintMsg("Double click on this item to take it off.")
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
							;[End Block]
						Default
							;[Block]
							DropItem(SelectedItem)
							InvOpen = False
							;[End Block]
					End Select
					
					MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
					StopMouseMovement()
				Else
					If Inventory(MouseSlot) = Null Then
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem Then
								Inventory(z) = Null
								Exit
							EndIf
						Next
						Inventory(MouseSlot) = SelectedItem
						SelectedItem = Null
					ElseIf Inventory(MouseSlot) <> SelectedItem
						PrevItem = Inventory(MouseSlot)
						
						Select SelectedItem\ItemTemplate\TempName
							Case "paper", "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "playcard", "mastercard", "oldpaper", "badge", "ticket", "25ct", "coin", "key", "scp860", "scp500pill", "scp500pilldeath", "scp005"
								;[Block]
								If Inventory(MouseSlot)\ItemTemplate\TempName = "clipboard" Then
									; ~ Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\ItemTemplate\TempName
									Local c%, ri%
									
									If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "scp005" Then
										For c = 0 To Inventory(MouseSlot)\InvSlots - 1
											If Inventory(MouseSlot)\SecondInv[c] = Null Then
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\State = 1.0
													SetAnimTime(Inventory(MouseSlot)\Model, 0.0)
													Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
													
													For ri = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null
													Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											CreateMsg("The paperclip is not strong enough to hold any more items.")
										Else
											If added\ItemTemplate\TempName = "paper" Lor added\ItemTemplate\TempName = "oldpaper" Then
												CreateMsg("This document was added to the clipboard.")
											ElseIf added\ItemTemplate\TempName = "badge"
												CreateMsg(added\ItemTemplate\Name + " was added to the clipboard.")
											Else
												CreateMsg("The " + added\ItemTemplate\Name + " was added to the clipboard.")
											EndIf
										EndIf
									Else
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
									EndIf
								ElseIf Inventory(MouseSlot)\ItemTemplate\TempName = "wallet" Then
									; ~ Add an item to clipboard
									added.Items = Null
									b = SelectedItem\ItemTemplate\TempName
									If b <> "paper" And b <> "oldpaper" Then
										For c = 0 To Inventory(MouseSlot)\InvSlots - 1
											If Inventory(MouseSlot)\SecondInv[c] = Null Then
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\State = 1.0
													If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860" And b <> "scp500pill" And b <> "scp500pilldeath" And b <> "scp005" Then
														SetAnimTime(Inventory(MouseSlot)\Model, 3.0)
													EndIf
													Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
													
													For ri = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null
													Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											CreateMsg("The wallet is full.")
										Else
											CreateMsg("You put " + added\ItemTemplate\Name + " into the wallet.")
										EndIf
									Else
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
									EndIf
								Else
									For z = 0 To MaxItemAmount - 1
										If Inventory(z) = SelectedItem Then
											Inventory(z) = PrevItem
											Exit
										EndIf
									Next
									Inventory(MouseSlot) = SelectedItem
									SelectedItem = Null
								EndIf
								SelectedItem = Null
								;[End Block]
							Case "badbat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav", "nav310"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(50.0)
										CreateMsg("You replaced the navigator's battery.")
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this navigator.")
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(50.0)
										CreateMsg("You replaced the radio's battery.")
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg("The battery doesn't fit inside this radio.")
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this radio.")
										;[End Block]
									Case "nvg", "supernvg"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(500.0)
										CreateMsg("You replaced the goggles' battery.")
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg("There seems to be no place for batteries in these goggles.")
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(500.0)
										CreateMsg("You replaced the gear's battery.")
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "bat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav", "nav310"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(100.0)
										CreateMsg("You replaced the navigator's battery.")
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this navigator.")
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(100.0)
										CreateMsg("You replaced the radio's battery.")
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg("The battery doesn't fit inside this radio.")
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this radio.")
										;[End Block]
									Case "nvg", "supernvg"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(1000.0)
										CreateMsg("You replaced the goggles' battery.")
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg("There seems to be no place for batteries in these goggles.")
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(1000.0)
										CreateMsg("You replaced the gear's battery.")
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "finebat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav", "nav310"
										;[Block]
										CreateMsg("The battery doesn't fit inside this navigator.")
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this navigator.")
										;[End Block]
									Case "radio"
										;[Block]
										CreateMsg("The battery doesn't fit inside this radio.")
										;[End Block]
									Case "18vradio"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(200.0)
										CreateMsg("You replaced the radio's battery.")
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this radio.")	
										;[End Block]
									Case "nvg", "supernvg"
										;[Block]
										CreateMsg("The battery doesn't fit inside these goggles.")
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg("There seems to be no place for batteries in these goggles.")
										;[End Block]
									Case "scramble"
										;[Block]
										CreateMsg("The battery doesn't fit inside this gear.")
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Case "superbat", "killbat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\TempName
									Case "nav", "nav310"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(1000.0)
										CreateMsg("You replaced the navigator's battery.")
										;[End Block]
									Case "navulti", "nav300"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this navigator.")
										;[End Block]
									Case "radio"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(1000.0)
										CreateMsg("You replaced the radio's battery.")
										;[End Block]
									Case "18vradio"
										;[Block]
										CreateMsg("The battery doesn't fit inside this radio.")
										;[End Block]
									Case "fineradio", "veryfineradio"
										;[Block]
										CreateMsg("There seems to be no place for batteries in this radio.")
										;[End Block]
									Case "nvg", "supernvg"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(10000.0)
										CreateMsg("You replaced the goggles' battery.")
										;[End Block]
									Case "finenvg"
										;[Block]
										CreateMsg("There seems to be no place for batteries in these goggles.")
										;[End Block]
									Case "scramble"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\State = Rnd(10000.0)
										CreateMsg("You replaced the gear's battery.")
										;[End Block]
									Default
										;[Block]
										For z = 0 To MaxItemAmount - 1
											If Inventory(z) = SelectedItem Then
												Inventory(z) = PrevItem
												Exit
											EndIf
										Next
										Inventory(MouseSlot) = SelectedItem
										SelectedItem = Null
										;[End Block]
								End Select
								;[End Block]
							Default
								;[Block]
								For z = 0 To MaxItemAmount - 1
									If Inventory(z) = SelectedItem Then
										Inventory(z) = PrevItem
										Exit
									EndIf
								Next
								Inventory(MouseSlot) = SelectedItem
								SelectedItem = Null
								;[End Block]
						End Select					
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If (Not InvOpen) Then 
			StopMouseMovement()
		EndIf
	Else
		If SelectedItem <> Null Then
			Select SelectedItem\ItemTemplate\TempName
				Case "nvg", "supernvg", "finenvg"
					;[Block]
					If (Not PreventItemOverlapping(False, True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "nvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 1, "pairs of goggles") Then Return
								;[End Block]
							Case "supernvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 2, "pairs of goggles") Then Return
								;[End Block]
							Case "finenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 3, "pairs of goggles") Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State3 = Min(SelectedItem\State3 + (fps\Factor[0] / 1.6), 100.0)
						
						If SelectedItem\State3 = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\NightVision > 0 Then
								CreateMsg("You removed the goggles.")
								wi\NightVision = 0
								opt\CameraFogFar = opt\StoredCameraFogFar
								If SelectedItem\State > 0.0 Then PlaySound_Strict(NVGSFX[1])
							Else
								CreateMsg("You put on the goggles.")
								Select SelectedItem\ItemTemplate\TempName
									Case "nvg"
										;[Block]
										wi\NightVision = 1
										;[End Block]
									Case "supernvg"
										;[Block]
										wi\NightVision = 2
										;[End Block]
									Case "finenvg"
										;[Block]
										wi\NightVision = 3
										;[End Block]
								End Select
								opt\StoredCameraFogFar = opt\CameraFogFar
								opt\CameraFogFar = 30.0
								If SelectedItem\State > 0.0 Then PlaySound_Strict(NVGSFX[0])
							EndIf
							SelectedItem\State3 = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell.ogg"))
					
					If Curr513_1 = Null Then Curr513_1 = CreateNPC(NPCType513_1, 0.0, 0.0, 0.0)
					SelectedItem = Null
					;[End Block]
				Case "scp500pill"
					;[Block]
					If CanUseItem(False, True) Then
						GiveAchievement(Achv500)
						
						If I_008\Timer > 0.0 Then
							CreateMsg("You swallowed the pill. Your nausea is fading.")
							I_008\Revert = True
						ElseIf I_409\Timer > 0.0 Then
							CreateMsg("You swallowed the pill. Your body is getting warmer and the crystals are receding.")
							I_409\Revert = True
						Else
							CreateMsg("You swallowed the pill.")
						EndIf
						
						me\DeathTimer = 0.0
						me\Stamina = 100.0
						
						For i = 0 To 6
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
							If e\EventID = e_1048_a Then
								If e\EventState2 > 0.0 Then
									CreateMsg("You swallowed the pill. Ear-like organs are falling from your body.")
									
									If PlayerRoom = e\room Then me\BlinkTimer = -10.0
									If e\room\Objects[0] <> 0 Then
										FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
									EndIf
									RemoveEvent(e)
								EndIf
								Exit
							EndIf
						Next
						RemoveItem(SelectedItem)
					EndIf	
					;[End Block]
				Case "veryfinefirstaid"
					;[Block]
					If CanUseItem(False, True) Then
						Select Rand(5)
							Case 1
								;[Block]
								me\Injuries = 3.5
								CreateMsg("You started bleeding heavily.")
								;[End Block]
							Case 2
								;[Block]
								me\Injuries = 0.0
								me\Bloodloss = 0.0
								CreateMsg("Your wounds are healing up rapidly.")
								;[End Block]
							Case 3
								;[Block]
								me\Injuries = Max(0.0, me\Injuries - Rnd(0.5, 3.5))
								me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 100.0))
								CreateMsg("You feel much better.")
								;[End Block]
							Case 4
								;[Block]
								me\BlurTimer = 10000.0
								me\Bloodloss = 0.0
								CreateMsg("You feel nauseated.")
								;[End Block]
							Case 5
								;[Block]
								me\BlinkTimer = -10.0
								
								Local RoomName$ = PlayerRoom\RoomTemplate\Name
								
								If RoomName = "dimension_1499" Lor RoomName = "gate_b" Lor RoomName = "gate_a" Then
									me\Injuries = 2.5
									CreateMsg("You started bleeding heavily.")
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "dimension_106" Then
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
									CreateMsg("For some inexplicable reason, you find yourself inside the pocket dimension.")
								EndIf
								;[End Block]
						End Select
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If me\Bloodloss = 0.0 And me\Injuries = 0.0 Then
						CreateMsg("You don't need to use a first aid kit right now.")
						SelectedItem = Null
						Return
					Else
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						If (Not me\Crouch) Then SetCrouch(True)
						
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 5.0), 100.0)			
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\TempName = "finefirstaid" Then
								me\Bloodloss = 0.0
								me\Injuries = Max(0.0, me\Injuries - 2.0)
								If me\Injuries = 0.0 Then
									CreateMsg("You bandaged the wounds and took a painkiller. You feel fine.")
								ElseIf me\Injuries > 1.0
									CreateMsg("You bandaged the wounds and took a painkiller, but you were not able to stop the bleeding.")
								Else
									CreateMsg("You bandaged the wounds and took a painkiller, but you still feel sore.")
								EndIf
								RemoveItem(SelectedItem)
							Else
								me\Bloodloss = Max(0.0, me\Bloodloss - Rnd(10.0, 20.0))
								If me\Injuries >= 2.5 Then
									CreateMsg("The wounds were way too severe to staunch the bleeding completely.")
									me\Injuries = Max(2.5, me\Injuries - Rnd(0.3, 0.7))
								ElseIf me\Injuries > 1.0
									me\Injuries = Max(0.5, me\Injuries - Rnd(0.5, 1.0))
									If me\Injuries > 1.0 Then
										CreateMsg("You bandaged the wounds but were unable to staunch the bleeding completely.")
									Else
										CreateMsg("You managed to stop the bleeding.")
									EndIf
								Else
									If me\Injuries > 0.5 Then
										me\Injuries = 0.5
										CreateMsg("You took a painkiller, easing the pain slightly.")
									Else
										me\Injuries = 0.5
										CreateMsg("You took a painkiller, but it still hurts to walk.")
									EndIf
								EndIf
								
								If SelectedItem\ItemTemplate\TempName = "firstaid2" Then 
									Select Rand(6)
										Case 1
											;[Block]
											chs\SuperMan = True
											CreateMsg("You have becomed overwhelmedwithadrenalineholyshitWOOOOOO~!")
											;[End Block]
										Case 2
											;[Block]
											opt\InvertMouse = (Not opt\InvertMouse)
											CreateMsg("You suddenly find it very difficult to turn your head.")
											;[End Block]
										Case 3
											;[Block]
											me\BlurTimer = 5000.0
											CreateMsg("You feel nauseated.")
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
											CreateMsg("You bandaged the wounds. The bleeding stopped completely and you feel fine.")
											;[End Block]
										Case 6
											;[Block]
											CreateMsg("You bandaged the wounds and blood started pouring heavily through the bandages.")
											me\Injuries = 3.5
											;[End Block]
									End Select
								EndIf
								RemoveItem(SelectedItem)
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops", "eyedrops2"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.6
						me\BlinkEffectTimer = Rnd(20.0, 30.0)
						me\BlurTimer = 200.0
						
						CreateMsg("You used the eyedrops. Your eyes feel moisturized.")
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.4
						me\BlinkEffectTimer = Rnd(30.0, 40.0)
						me\Bloodloss = Max(me\Bloodloss - 1.0, 0.0)
						me\BlurTimer = 200.0
						
						CreateMsg("You used the eyedrops. Your eyes feel very moisturized.")
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem(False, False) Then
						me\BlinkEffect = 0.0
						me\BlinkEffectTimer = 60.0
						me\EyeStuck = 10000.0
						me\BlurTimer = 1000.0
						
						CreateMsg("You used the eyedrops. Your eyes feel very moisturized.")
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "ticket"
					;[Block]
					If SelectedItem\State = 0.0 Then
						CreateMsg(Chr(34) + "Hey, I remember this movie!" + Chr(34))
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(1, 5) + ".ogg"))
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "scp1025"
					;[Block]
					If SelectedItem\State3 = 0.0 Then
						If (Not I_714\Using) And wi\GasMask <> 3 And wi\HazmatSuit <> 3 Then
							If SelectedItem\State = 7.0 Then
								If I_008\Timer = 0.0 Then I_008\Timer = 1.0
							Else
								I_1025\State[SelectedItem\State] = Max(1.0, I_1025\State[SelectedItem\State])
								I_1025\State[7] = 1 + (SelectedItem\State2 = 2.0) * 2.0 ; ~ 3x as fast if VERYFINE
							EndIf
						EndIf
						If Rand(3 - (SelectedItem\State2 <> 2.0) * SelectedItem\State2) = 1 Then ; ~ Higher chance for good illness if FINE, lower change for good illness if COARSE
							SelectedItem\State = 6.0
						Else
							SelectedItem\State = Rand(0, 7)
						EndIf
						SelectedItem\State3 = 1.0
					EndIf
				Case "book"
					;[Block]
					CreateMsg(Chr(34) + "I really don't have the time for that right now..." + Chr(34))
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
						x2 = SelectedItem\State + 1.0
						
						Local Loc% = GetINISectionLocation(SCP294File, SelectedItem\Name)
						
						StrTemp = GetINIString2(SCP294File, Loc, "Message")
						If StrTemp <> "" Then CreateMsg(StrTemp)
						
						If GetINIInt2(SCP294File, Loc, "Lethal")
							msg\DeathMsg = GetINIString2(SCP294File, Loc, "Death Message")
							If GetINIInt2(SCP294File, Loc, "Lethal") Then Kill()
						EndIf
						me\BlurTimer = GetINIInt2(SCP294File, Loc, "Blur") * 70.0
						If me\VomitTimer = 0.0 Then me\VomitTimer = GetINIInt2(SCP294File, Loc, "Vomit")
						me\CameraShakeTimer = GetINIString2(SCP294File, Loc, "Camera Shake")
						me\Injuries = Max(me\Injuries + GetINIInt2(SCP294File, Loc, "Damage"), 0.0)
						me\Bloodloss = Max(me\Bloodloss + GetINIInt2(SCP294File, Loc, "Blood Loss"), 0.0)
						StrTemp =  GetINIString2(SCP294File, Loc, "Sound")
						If StrTemp <> "" Then
							PlaySound_Strict(LoadTempSound(StrTemp))
						EndIf
						If GetINIInt2(SCP294File, Loc, "Stomach Ache") Then I_1025\State[3] = 1.0
						
						If GetINIInt2(SCP294File, Loc, "Infection") Then I_008\Timer = 1.0
						
						If GetINIInt2(SCP294File, Loc, "Crystallization") Then I_409\Timer = 1.0
						
						me\DeathTimer = GetINIInt2(SCP294File, Loc, "Death Timer") * 70.0
						
						me\BlinkEffect = Float(GetINIString2(SCP294File, Loc, "Blink Effect", 1.0)) * x2
						me\BlinkEffectTimer = Float(GetINIString2(SCP294File, Loc, "Blink Effect Timer", 1.0)) * x2
						
						me\StaminaEffect = Float(GetINIString2(SCP294File, Loc, "Stamina Effect", 1.0)) * x2
						me\StaminaEffectTimer = Float(GetINIString2(SCP294File, Loc, "Stamina Effect Timer", 1.0)) * x2
						
						StrTemp = GetINIString2(SCP294File, Loc, "Refuse Message")
						If StrTemp <> "" Then
							CreateMsg(StrTemp)
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0.0, 0.0, 0.0)
							it\Picked = True
							For i = 0 To MaxItemAmount - 1
								If Inventory(i) = SelectedItem Then
									Inventory(i) = it
									Exit
								EndIf
							Next					
							EntityType(it\Collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)
						EndIf
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					me\HealTimer = 30.0
					me\StaminaEffect = 0.5
					me\StaminaEffectTimer = 20.0
					
					CreateMsg("You injected yourself with the syringe and feel a slight adrenaline rush.")
					
					RemoveItem(SelectedItem)
					;[End Block]
				Case "finesyringe"
					;[Block]
					me\HealTimer = Rnd(20.0, 40.0)
					me\StaminaEffect = Rnd(0.5, 0.8)
					me\StaminaEffectTimer = Rnd(20.0, 30.0)
					
					CreateMsg("You injected yourself with the syringe and feel an adrenaline rush.")
					
					RemoveItem(SelectedItem)
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					Select Rand(3)
						Case 1
							;[Block]
							me\HealTimer = Rnd(40.0, 60.0)
							me\StaminaEffect = 0.1
							me\StaminaEffectTimer = 30.0
							CreateMsg("You injected yourself with the syringe and feel a huge adrenaline rush.")
							;[End Block]
						Case 2
							;[Block]
							chs\SuperMan = True
							CreateMsg("You injected yourself with the syringe and feel a humongous adrenaline rush.")
							;[End Block]
						Case 3
							;[Block]
							me\VomitTimer = 30.0
							CreateMsg("You injected yourself with the syringe and feel a pain in your stomach.")
							;[End Block]
					End Select
					
					RemoveItem(SelectedItem)
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					If (Not SelectedItem\ItemTemplate\Img) Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\ItemTemplate\TempName <> "fineradio" And SelectedItem\ItemTemplate\TempName <> "veryfineradio" Then SelectedItem\State = Max(0.0, SelectedItem\State - fps\Factor[0] * 0.004)
					
					; ~ RadioState[5] = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState[6] = A timer for the "code channel"
					; ~ RadioState[7] = Another timer for the "code channel"
					
					x = opt\GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img)
					y = opt\GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img)
					
					If SelectedItem\State > 0.0 Lor (SelectedItem\ItemTemplate\TempName = "fineradio" Lor SelectedItem\ItemTemplate\TempName = "veryfineradio") Then
						If RadioState[5] = 0.0 Then 
							CreateMsg("Use the numbered keys 1 through 5 to cycle between various channels.")
							RadioState[5] = 1.0
							RadioState[0] = -1.0
						EndIf
						
						If PlayerRoom\RoomTemplate\Name = "dimension_106" Then
							For i = 0 To 4
								If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
							Next
							If ChannelPlaying(RadioCHN[6]) Then PauseChannel(RadioCHN[6])
							
							ResumeChannel(RadioCHN[5])
							If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
						ElseIf CoffinDistance < 8.0
							For i = 0 To 4
								If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
							Next
							If ChannelPlaying(RadioCHN[6]) Then PauseChannel(RadioCHN[6])
							
							ResumeChannel(RadioCHN[5])
							If (Not ChannelPlaying(RadioCHN[5])) Then RadioCHN[5] = PlaySound_Strict(RadioStatic895)	
						Else
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
									
									ResumeChannel(RadioCHN[0])
									If (Not opt\EnableUserTracks) Then
										If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
									ElseIf UserTrackMusicAmount < 1
										If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
									Else
										If (Not ChannelPlaying(RadioCHN[0])) Then
											If (Not UserTrackFlag) Then
												If opt\UserTrackMode Then
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
											If CurrUserTrack <> 0 Then
												FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											EndIf
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										Else
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict(RadioSquelch)
											If (Not UserTrackFlag) Then
												If opt\UserTrackMode Then
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
											If CurrUserTrack <> 0 Then
												FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											EndIf
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName[RadioState[0]])
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack)
										EndIf
									EndIf
									;[End Block]
								Case 1
									;[Block]
									If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
									
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
									If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
									
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
									If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
									
									ResumeChannel(RadioCHN[3])
									If (Not ChannelPlaying(RadioCHN[3])) Then RadioCHN[3] = PlaySound_Strict(RadioStatic)
									
									If MTFTimer > 0.0 Then 
										RadioState[3] = RadioState[3] + Max(Rand(-10, 1), 0.0)
										Select RadioState[3]
											Case 40
												;[Block]
												If (Not RadioState2[0]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState2[0] = True	
												EndIf	
												;[End Block]
											Case 400
												;[Block]
												If (Not RadioState2[1]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState2[1] = True	
												EndIf	
												;[End Block]
											Case 800
												;[Block]
												If (Not RadioState2[2]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState[3] = RadioState[3] + 1.0	
													RadioState2[2] = True
												EndIf		
												;[End Block]
											Case 1200
												;[Block]
												If (Not RadioState2[3]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState[3] = RadioState[3] + 1.0	
													RadioState2[3] = True
												EndIf
												;[End Block]
											Case 1600
												;[Block]
												If (Not RadioState2[4]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[4] = True
												EndIf
												;[End Block]
											Case 2000
												;[Block]
												If (Not RadioState2[5]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[5] = True
												EndIf
												;[End Block]
											Case 2400
												;[Block]
												If (Not RadioState2[6]) Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState[3] = RadioState[3] + 1.0
													RadioState2[6] = True
												EndIf
												;[End Block]
										End Select
									EndIf
									;[End Block]
								Case 4
									;[Block]
									If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
									
									ResumeChannel(RadioCHN[6])
									If (Not ChannelPlaying(RadioCHN[6])) Then RadioCHN[6] = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN[4])
									If (Not ChannelPlaying(RadioCHN[4])) Then 
										If (Not RemoteDoorOn) And RadioState[8] = 0 Then
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter3.ogg"))	
											RadioState[8] = 1
										Else
											RadioState[4] = RadioState[4] + Max(Rand(-10, 1), 0.0)
											
											Select RadioState[4]
												Case 10
													;[Block]
													If (Not Curr106\Contained) Then
														If (Not RadioState3[0]) Then
															RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\OhGod.ogg"))
															RadioState[4] = RadioState[4] + 1.0
															RadioState3[0] = True
														EndIf
													EndIf
													;[End Block]
												Case 100
													;[Block]
													If (Not RadioState3[1]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[1] = True
													EndIf	
													;[End Block]
												Case 158
													;[Block]
													If MTFTimer = 0.0 And (Not RadioState3[2]) Then 
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin1.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState[2] = True
													EndIf
													;[End Block]
												Case 200
													;[Block]
													If (Not RadioState3[3]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter4.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[3] = True
													EndIf		
													;[End Block]
												Case 260
													;[Block]
													If (Not RadioState3[4]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[4] = True
													EndIf		
													;[End Block]
												Case 300
													;[Block]
													If (Not RadioState3[5]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter1.ogg"))	
														RadioState[4] = RadioState[4] + 1.0	
														RadioState3[5] = True
													EndIf		
													;[End Block]
												Case 350
													;[Block]
													If (Not RadioState3[6]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[6] = True
													EndIf		
													;[End Block]
												Case 400
													;[Block]
													If (Not RadioState3[7]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState[4] = RadioState[4] + 1.0
														RadioState3[7] = True
													EndIf		
													;[End Block]
												Case 450
													;[Block]
													If (Not RadioState3[8]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin3.ogg"))	
														RadioState[4] = RadioState[4] + 1.0		
														RadioState3[8] = True
													EndIf		
													;[End Block]
												Case 600
													;[Block]
													If (Not RadioState3[9]) Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin4.ogg"))	
														RadioState[4] = RadioState[4] + 1.0	
														RadioState3[9] = True
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
							
							x = x + 66
							y = y + 419
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio" Then
								If ChannelPlaying(RadioCHN[5]) Then PauseChannel(RadioCHN[5])
								
								ResumeChannel(RadioCHN[0])
								If (Not ChannelPlaying(RadioCHN[0])) Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
								RadioState[6] = RadioState[6] + fps\Factor[0]
								Temp = Mid(Str(AccessCode), RadioState[8] + 1.0, 1)
								If RadioState[6] - fps\Factor[0] =< RadioState[7] * 50.0 And RadioState[6] > RadioState[7] * 50.0 Then
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
						
						If SelectedItem\ItemTemplate\TempName = "radio" Lor SelectedItem\ItemTemplate\TempName = "18vradio" Then
							If SelectedItem\State =< 20.0 And ((MilliSecs2() Mod 800) < 200) Then
								If (Not LowBatteryCHN[0]) Then
									LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
								ElseIf (Not ChannelPlaying(LowBatteryCHN[0])) Then
									LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
								EndIf
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
								CreateMsg(Chr(34) + "I don't have anything to light it with. Umm, what about that... Nevermind." + Chr(34))
								;[End Block]
							Case 2
								;[Block]
								CreateMsg("You are unable to get lit.")
								;[End Block]
							Case 3
								;[Block]
								CreateMsg(Chr(34) + "I quit that a long time ago." + Chr(34))
								;[End Block]
							Case 4
								;[Block]
								CreateMsg(Chr(34) + "Even if I wanted one, I have nothing to light it with." + Chr(34))
								;[End Block]
							Case 5
								;[Block]
								CreateMsg(Chr(34) + "Could really go for one now... Wish I had a lighter." + Chr(34))
								;[End Block]
							Case 6
								;[Block]
								CreateMsg(Chr(34) + "Don't plan on starting, even at a time like this." + Chr(34))
								;[End Block]
						End Select
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp420j"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							CreateMsg(Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34))
						Else
							CreateMsg(Chr(34) + "MAN DATS SUM GOOD ASS SHIT." + Chr(34))
							me\Injuries = Max(me\Injuries - 0.5, 0.0)
							me\BlurTimer = 500.0
							GiveAchievement(Achv420J)
							PlaySound_Strict(LoadTempSound("SFX\Music\Using420J.ogg"))
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "joint"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							CreateMsg(Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34))
						Else
							CreateMsg(Chr(34) + "UH WHERE... WHAT WAS I DOING AGAIN... MAN I NEED TO TAKE A NAP..." + Chr(34))
							msg\DeathMsg = SubjectName + " found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							msg\DeathMsg = msg\DeathMsg + "whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							Kill()						
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp420s"
					;[Block]
					If CanUseItem(False, True) Then
						If I_714\Using Lor wi\GasMask = 3 Lor wi\HazmatSuit = 3 Then
							CreateMsg(Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34))
						Else
							CreateMsg(Chr(34) + "UUUUUUUUUUUUHHHHHHHHHHHH..." + Chr(34))
							msg\DeathMsg = SubjectName + " found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							msg\DeathMsg = msg\DeathMsg + "Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							msg\DeathMsg = msg\DeathMsg + "whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							Kill()						
						EndIf
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If I_714\Using Then
						CreateMsg("You removed the ring.")
						I_714\Using = False
					Else
						CreateMsg("You put on the ring.")
						GiveAchievement(Achv714)
						I_714\Using = True
					EndIf
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If wi\BallisticVest = 0 Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / 4.0), 100.0)
						
						If SelectedItem\State = 100.0 Then
							If wi\HazmatSuit > 0 Then
								CreateMsg("You removed the hazmat suit.")
								wi\HazmatSuit = 0
								DropItem(SelectedItem)
							Else
								CreateMsg("You put on the hazmat suit.")
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
								If SelectedItem\ItemTemplate\TempName = "hazmatsuit" Then
									wi\HazmatSuit = 1
								ElseIf SelectedItem\ItemTemplate\TempName = "hazmatsuit2"
									wi\HazmatSuit = 2
								Else
									wi\HazmatSuit = 3
								EndIf
								If wi\NightVision > 0 Then opt\CameraFogFar = opt\StoredCameraFogFar : wi\NightVision = 0
								wi\GasMask = 0
								wi\BallisticHelmet = False
								wi\SCRAMBLE = False
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
					
					SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0] / (2.0 + (0.5 * (SelectedItem\ItemTemplate\TempName = "finevest")))), 100)
					
					If SelectedItem\State = 100.0 Then
						If wi\BallisticVest > 0 Then
							CreateMsg("You removed the vest.")
							wi\BallisticVest = 0
							DropItem(SelectedItem)
						Else
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							Select SelectedItem\ItemTemplate\TempName
								Case "vest"
									;[Block]
									CreateMsg("You put on the vest and feel slightly encumbered.")
									wi\BallisticVest = 1
									;[End Block]
								Case "finevest"
									;[Block]
									CreateMsg("You put on the vest and feel heavily encumbered.")
									wi\BallisticVest = 2
									;[End Block]
							End Select
						EndIf
						SelectedItem\State = 0.0
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If (Not PreventItemOverlapping(True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "gasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 1, "gas masks") Then Return
								;[End Block]
							Case "supergasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 2, "gas masks") Then Return
								;[End Block]
							Case "gasmask3"
								;[Block]
								If IsDoubleItem(wi\GasMask, 3, "gas masks") Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + (fps\Factor[0]) / 1.6, 100.0)
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\GasMask > 0 Then
								CreateMsg("You removed the gas mask.")
								wi\GasMask = 0
							Else
								Select SelectedItem\ItemTemplate\TempName
									Case "gasmask"
										;[Block]
										CreateMsg("You put on the gas mask.")
										wi\GasMask = 1
										;[End Block]
									Case "supergasmask"
										;[Block]
										CreateMsg("You put on the gas mask and you can breathe easier.")
										wi\GasMask = 2
										;[End Block]
									Case "gasmask3"
										;[Block]
										CreateMsg("You put on the gas mask.")
										wi\GasMask = 3
										;[End Block]
								End Select
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "nav", "nav310"
					;[Block]
					SelectedItem\State = Max(0.0, SelectedItem\State - fps\Factor[0] * 0.005)
					
					If SelectedItem\State > 0.0 Then
						If SelectedItem\State =< 20.0 And ((MilliSecs2() Mod 800) < 200) Then
							If (Not LowBatteryCHN[0]) Then
								LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
							ElseIf (Not ChannelPlaying(LowBatteryCHN[0])) Then
								LowBatteryCHN[0] = PlaySound_Strict(LowBatterySFX[0])
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					If (Not PreventItemOverlapping(False, False, True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "scp1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 1, "gas masks") Then Return
								;[End Block]
							Case "super1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 2, "gas masks") Then Return
								;[End Block]
						End Select
						
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fps\Factor[0] / 1.6, 100.0)
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If I_1499\Using > 0 Then
								CreateMsg("You removed the gas mask.")
								I_1499\Using = 0
							Else
								Select SelectedItem\ItemTemplate\TempName
									Case "scp1499"
										;[Block]
										CreateMsg("You put on the gas mask.")
										I_1499\Using = 1
										;[End Block]
									Case "super1499"
										;[Block]
										CreateMsg("You put on the gas mask and you can breathe easier.")
										I_1499\Using = 2
										;[End Block]
								End Select
								GiveAchievement(Achv1499)
								For r.Rooms = Each Rooms
									If r\RoomTemplate\Name = "dimension_1499" Then
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
											If e\EventID = e_dimension_1499 Then
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
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						Select SelectedItem\ItemTemplate\Name
							Case "Old Badge"
								;[Block]
								CreateMsg(Chr(34) + "Huh? This guy looks just like me!" + Chr(34))
								;[End Block]
						End Select
						
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						
						CreateMsg(Chr(34) + "Isn't this the key to that old shack? The one where I... No, it can't be." + Chr(34))					
					EndIf
					
					SelectedItem\State = 1.0
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\State = 0.0 Then
						Select SelectedItem\ItemTemplate\Name
							Case "Disciplinary Hearing DH-S-4137-17092"
								;[Block]
								me\BlurTimer = 1000.0
								
								CreateMsg(Chr(34) + "Why does this seem so familiar?" + Chr(34))
								PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
								SelectedItem\state = 1.0
								;[End Block]
						End Select
					EndIf
					;[End Block]
				Case "coin"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\NostalgiaCancer" + Rand(1, 5) + ".ogg"))
						
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using Then
						CreateMsg("You closed the locket.")
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						CreateMsg("You opened the locket.")
						I_427\Using = True
					EndIf
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(False, True) Then
						CreateMsg("You swallowed the pill.")
						
						RemoveItem(SelectedItem)
					EndIf	
					;[End Block]
				Case "scp500pilldeath"
					;[Block]
					If CanUseItem(False, True) Then
						CreateMsg("You swallowed the pill.")
						
						If I_427\Timer < 70.0 * 360.0 Then
							I_427\Timer = 70.0 * 360.0
						EndIf
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "syringeinf"
					;[Block]
					CreateMsg("You injected yourself the syringe.")
					
					me\VomitTimer = 70.0 * 1.0
					
					I_008\Timer = I_008\Timer + (1.0 + (1.0 * SelectedDifficulty\AggressiveNPCs))
					RemoveItem(SelectedItem)
					;[End Block]
				Case "helmet"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, True)) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State = Min(SelectedItem\State + fps\Factor[0], 100.0)
						
						If SelectedItem\State = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\BallisticHelmet Then
								CreateMsg("You removed the helmet.")
								wi\BallisticHelmet = False
							Else
								CreateMsg("You put on the helmet.")
								wi\BallisticHelmet = True
							EndIf
							SelectedItem\State = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "scramble"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, True)) Then
						me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, 5.0)
						
						SelectedItem\State3 = Min(SelectedItem\State3 + (fps\Factor[0] / 1.6), 100.0)
						
						If SelectedItem\State3 = 100.0 Then
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
							
							If wi\SCRAMBLE Then
								CreateMsg("You removed the gear.")
								wi\SCRAMBLE = False
							Else
								CreateMsg("You put on the gear.")
								wi\SCRAMBLE = True
							EndIf
							SelectedItem\State3 = 0.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "scp500"
					;[Block]
					If I_500\Taken < Rand(10) Then
						If ItemAmount < MaxItemAmount Then
							For i = 0 To ItemAmount
								If Inventory(i) = Null Then
									Inventory(i) = CreateItem("SCP-500-01", "scp500pill", 0.0, 0.0, 0.0)
									Inventory(i)\Picked = True
									Inventory(i)\Dropped = -1
									Inventory(i)\ItemTemplate\Found = True
									HideEntity(Inventory(i)\Collider)
									EntityType(Inventory(i)\Collider, HIT_ITEM)
									EntityParent(Inventory(i)\Collider, 0)
									
									msg\Txt = "You took SCP-500-01 from the bottle."
									msg\Timer = 70.0 * 6.0
									
									I_500\Taken = I_500\Taken + 1
									Exit
								EndIf
							Next
						Else
							msg\Txt = "You can't carry any more items."
							msg\Timer = 70.0 * 6.0				
						EndIf
					Else
						I_500\Taken = 0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp1123"
					;[Block]
					Use1123(True)
					SelectedItem = Null
					;[End Block]
				Case "nav300", "navulti", "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "hand3", "25ct", "scp005", "key", "coin", "mastercard", "paper"
					;[Block]
					; ~ Skip this line
					;[End Block]
				Default
					;[Block]
					; ~ Check if the item is an inventory-type object
					If SelectedItem\InvSlots > 0 Then OtherOpen = SelectedItem
					mo\DoubleClick = False
					mo\MouseHit1 = False
					mo\MouseDown1 = False
					mo\LastMouseHit1 = False
					SelectedItem = Null
					;[End Block]
			End Select
			
			If mo\MouseHit2 Then
				EntityAlpha(t\OverlayID[5], 0.0)
				
				Select SelectedItem\ItemTemplate\TempName
					Case "firstaid", "finefirstaid", "firstaid2", "scp1499", "super1499", "gasmask", "supergasmask", "gasmask3", "helmet"
						;[Block]
						SelectedItem\State = 0.0
						;[End Block]
					Case "vest", "finevest"
						;[Block]
						SelectedItem\State = 0.0
						If (Not wi\BallisticVest) Then
							DropItem(SelectedItem, False)
						EndIf
						;[End Block]
					Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
						;[Block]
						SelectedItem\State = 0.0
						If wi\HazmatSuit = 0 Then
							DropItem(SelectedItem, False)
						EndIf
						;[End Block]
					Case "nvg", "supernvg", "finenvg", "scramble", "scp1025"
						;[Block]
						SelectedItem\State3 = 0.0
						;[End Block]
				End Select
				If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[SelectedItem\ItemTemplate\Sound])
				SelectedItem = Null
			EndIf
		Else
			For i = 0 To 6
				If RadioCHN[i] <> 0 Then 
					If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
				EndIf
			Next
			
			If LowBatteryCHN[0] <> 0 Then
				If ChannelPlaying(LowBatteryCHN[0]) Then StopChannel(LowBatteryCHN[0])
			EndIf
		EndIf		
	EndIf
	
	For it.Items = Each Items
		If it <> SelectedItem Then
			Select it\ItemTemplate\TempName
				Case "firstaid", "finefirstaid", "firstaid2", "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3", "scp1499", "super1499", "gasmask", "supergasmask", "gasmask3", "helmet"
					;[Block]
					it\State = 0.0
					;[End Block]
				Case "nvg", "supernvg", "finenvg", "scramble", "scp1025"
					;[Block]
					it\State3 = 0.0
					;[End Block]
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	CatchErrors("UpdateGUI")
End Function

Function RenderHUD()
	Local x%, y%, Width%, Height%, WalkIconID%, BlinkIconID%
	
	Width = 200 * MenuScale
	Height = 20 * MenuScale
	x = 80 * MenuScale
	y = opt\GraphicHeight - (95 * MenuScale)
	
	Color(255, 255, 255)
	If me\BlinkTimer < 150.0 Then
		RenderBar(t\ImageID[1], x, y, Width, Height, me\BlinkTimer, me\BLINKFREQ, 100, 0, 0)
	Else
		RenderBar(BlinkMeterIMG, x, y, Width, Height, me\BlinkTimer, me\BLINKFREQ)
	EndIf
	Color(0, 0, 0)
	Rect(x - (50 * MenuScale), y, 30 * MenuScale, 30 * MenuScale)
	
	If me\BlurTimer > 550.0 Lor me\BlinkEffect > 1.0 Lor me\LightFlash > 0.0 Lor (((me\LightBlink > 0.0 And (Not chs\NoBlink)) Lor me\EyeIrritation > 0.0) And wi\NightVision = 0) Then
		Color(200, 0, 0)
		Rect(x - (53 * MenuScale), y - (3 * MenuScale), 36 * MenuScale, 36 * MenuScale)
	Else
		If me\BlinkEffect < 1.0 Lor chs\NoBlink Then
			Color(0, 200, 0)
			Rect(x - (53 * MenuScale), y - (3 * MenuScale), 36 * MenuScale, 36 * MenuScale)
		EndIf
	EndIf
	
	Color(255, 255, 255)
	Rect(x - (51 * MenuScale), y - MenuScale, 32 * MenuScale, 32 * MenuScale, False)
	
	If me\BlinkTimer < 0.0
		BlinkIconID = 4
	Else
		BlinkIconID = 3
	EndIf
	DrawImage(t\IconID[BlinkIconID], x - (50 * MenuScale), y)
	
	y = opt\GraphicHeight - (55 * MenuScale)
	
	If me\Stamina =< 25.0 Then
		RenderBar(t\ImageID[3], x, y, Width, Height, me\Stamina, 100.0, 50, 0, 0)
	Else
		RenderBar(t\ImageID[2], x, y, Width, Height, me\Stamina, 100.0, 50, 50, 50)
	EndIf
	Color(0, 0, 0)
	Rect(x - (50 * MenuScale), y, 30 * MenuScale, 30 * MenuScale)
	
	If PlayerRoom\RoomTemplate\Name = "dimension_106" Lor I_714\Using Lor me\Injuries >= 1.5 Lor me\StaminaEffect > 1.0 Lor wi\HazmatSuit = 1 Lor wi\BallisticVest = 2 Lor I_409\Timer >= 55.0 Then
		Color(200, 0, 0)
		Rect(x - (53 * MenuScale), y - (3 * MenuScale), 36 * MenuScale, 36 * MenuScale)
	Else
		If chs\InfiniteStamina Lor me\StaminaEffect < 1.0 Lor wi\GasMask = 2 Lor I_1499\Using = 2 Lor wi\HazmatSuit = 2 Then
			Color(0, 200, 0)
			Rect(x - (53 * MenuScale), y - (3 * MenuScale), 36 * MenuScale, 36 * MenuScale)
		EndIf 
	EndIf
	
	Color(255, 255, 255)
	Rect(x - (51 * MenuScale), y - MenuScale, 32 * MenuScale, 32 * MenuScale, False)
	If me\Crouch Then
		WalkIconID = 2
	ElseIf (KeyDown(key\SPRINT) And (Not InvOpen) And OtherOpen = Null) And me\CurrSpeed > 0.0 And (Not chs\NoClip) And me\Stamina > 0.0 Then
		WalkIconID = 1
	Else
		WalkIconID = 0
	EndIf
	DrawImage(t\IconID[WalkIconID], x - (50 * MenuScale), y)
End Function

Function RenderDebugHUD()
	Local ev.Events, ch.Chunk
	Local x%, y%, i%
	
	x = 20 * MenuScale
	y = 40 * MenuScale
	
	Color(255, 255, 255)
	SetFont(fo\FontID[Font_Console])
	
	Select chs\DebugHUD
		Case 1
			;[Block]
			Text(x, y, "Room: " + PlayerRoom\RoomTemplate\Name)
			Text(x, y + (20 * MenuScale), "Room Coordinates: (" + Floor(EntityX(PlayerRoom\OBJ) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\OBJ) / 8.0 + 0.5) + ", Angle: " + PlayerRoom\Angle + ")")
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					Text(x, y + (40 * MenuScale), "Room Event: " + ev\EventName + ", ID: " + ev\EventID) 
					Text(x, y + (60 * MenuScale), "State: " + ev\EventState)
					Text(x, y + (80 * MenuScale), "State2: " + ev\EventState2)   
					Text(x, y + (100 * MenuScale), "State3: " + ev\EventState3)
					Text(x, y + (120 * MenuScale), "State4: " + ev\EventState4)
					Text(x, y + (140 * MenuScale), "Str: "+ ev\EventStr)
					Exit
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name = "dimension_1499" Then
				Text(x, y + (180 * MenuScale), "Current Chunk X / Z: (" + (Int((EntityX(me\Collider) + 20) / 40)) + ", "+(Int((EntityZ(me\Collider) + 20) / 40)) + ")")
				
				Local CH_Amount% = 0
				
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				Text(x, y + (200 * MenuScale), "Current Chunk Amount: " + CH_Amount)
			Else
				Text(x, y + (200 * MenuScale), "Current Room Position: (" + PlayerRoom\x + ", " + PlayerRoom\y + ", " + PlayerRoom\z + ")")
			EndIf
			
			If SelectedMonitor <> Null Then
				Text(x, y + (240 * MenuScale), "Current Monitor: " + SelectedMonitor\ScrOBJ)
			Else
				Text(x, y + (240 * MenuScale), "Current Monitor: Null")
			EndIf
			
			If SelectedItem <> Null Then
				Text(x, y + (280 * MenuScale), "Current Button: " + SelectedItem\ItemTemplate\Name)
			Else
				Text(x, y + (280 * MenuScale), "Current Button: Null")
			EndIf
			
			Text(x, y + (320 * MenuScale), "Current Floor: " + PlayerElevatorFloor)
			Text(x, y + (340 * MenuScale), "Room floor: " + ToElevatorFloor)
			If PlayerInsideElevator Then
				Text(x, y + (360 * MenuScale), "Player is inside elevator: True")
			Else
				Text(x, y + (360 * MenuScale), "Player is inside elevator: False")
			EndIf
			
			Text(x, y + (400 * MenuScale), "Date and Time: " + CurrentDate() + ", " + CurrentTime())
			Text(x, y + (420 * MenuScale), "Video memory: " + ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)) + " MB/" + (TotalVidMem() / 1024) + " MB" + Chr(10))
			Text(x, y + (440 * MenuScale), "Global memory status: " + ((TotalPhys() / 1024) - (AvailPhys() / 1024)) + " MB/" + (TotalPhys() / 1024) + " MB")
			Text(x, y + (460 * MenuScale), "Triangles Rendered: " + CurrTrisAmount)
			Text(x, y + (480 * MenuScale), "Active Textures: " + ActiveTextures())
			;[End Block]
		Case 2
			;[Block]
			Text(x, y, "Player Position: (" + f2s(EntityX(me\Collider), 1) + ", " + f2s(EntityY(me\Collider), 1) + ", " + f2s(EntityZ(me\Collider), 1) + ")")
			Text(x, y + (20 * MenuScale), "Player Rotation: (" + f2s(EntityPitch(me\Collider), 1) + ", " + f2s(EntityYaw(me\Collider), 1) + ", " + f2s(EntityRoll(me\Collider), 1) + ")")
			
			Text(x, y + (60 * MenuScale), "Injuries: " + me\Injuries)
			Text(x, y + (80 * MenuScale), "Bloodloss: " + me\Bloodloss)
			
			Text(x, y + (120 * MenuScale), "Blur Timer: " + me\BlurTimer)
			Text(x, y + (140 * MenuScale), "Light Blink: " + me\LightBlink)
			Text(x, y + (160 * MenuScale), "Light Flash: " + me\LightFlash)
			
			Text(x, y + (200 * MenuScale), "Blink Frequency: " + me\BLINKFREQ)
			Text(x, y + (220 * MenuScale), "Blink Timer: " + me\BlinkTimer)
			Text(x, y + (240 * MenuScale), "Blink Effect: " + me\BlinkEffect)
			Text(x, y + (260 * MenuScale), "Blink Effect Timer: " + me\BlinkEffectTimer)
			Text(x, y + (280 * MenuScale), "Eye Irritation: " + me\EyeIrritation)
			Text(x, y + (300 * MenuScale), "Eye Stuck: " + me\EyeStuck)
			
			Text(x, y + (340 * MenuScale), "Stamina: " + me\Stamina)
			Text(x, y + (360 * MenuScale), "Stamina Effect: " + me\StaminaEffect)
			Text(x, y + (380 * MenuScale), "Stamina Effect Timer: " + me\StaminaEffectTimer)
			
			Text(x, y + (420 * MenuScale), "Deaf Timer: " + me\DeafTimer)
			
			Text(x + (380 * MenuScale), y, "Kill Timer: " + me\KillTimer)
			Text(x + (380 * MenuScale), y + (20 * MenuScale), "Death Timer: " + me\DeathTimer)
			Text(x + (380 * MenuScale), y + (40 * MenuScale), "Fall Timer: " + me\FallTimer)
			
			Text(x + (380 * MenuScale), y + (80 * MenuScale), "Heal Timer: " + me\HealTimer)
			
			Text(x + (380 * MenuScale), y + (120 * MenuScale), "Heart Beat Timer: " + me\HeartBeatTimer)
			
			Text(x + (380 * MenuScale), y + (160 * MenuScale), "Explosion Timer: " + me\ExplosionTimer)
			
			Text(x + (380 * MenuScale), y + (200 * MenuScale), "Current Speed: " + me\CurrSpeed)
			
			Text(x + (380 * MenuScale), y + (240 * MenuScale), "Camera Shake Timer: " + me\CameraShakeTimer)
			Text(x + (380 * MenuScale), y + (260 * MenuScale), "Current Camera Zoom: " + me\CurrCameraZoom)
			
			Text(x + (380 * MenuScale), y + (300 * MenuScale), "Vomit Timer: " + me\VomitTimer)
			
			If me\Playable Then
				Text(x + (380 * MenuScale), y + (340 * MenuScale), "Is Playable: True")
			Else
				Text(x + (380 * MenuScale), y + (340 * MenuScale), "Is Playable: False")
			EndIf
			
			Text(x + (380 * MenuScale), y + (380 * MenuScale), "Refined Items: " + me\RefinedItems)
			Text(x + (380 * MenuScale), y + (400 * MenuScale), "Funds: " + me\Funds)
			;[End Block]
		Case 3
			;[Block]
			If Curr049 <> Null Then
				Text(x, y, "SCP-049 Position: (" + f2s(EntityX(Curr049\OBJ), 2) + ", " + f2s(EntityY(Curr049\OBJ), 2) + ", " + f2s(EntityZ(Curr049\OBJ), 2) + ")")
				Text(x, y + (20 * MenuScale), "SCP-049 Idle: " + Curr049\Idle)
				Text(x, y + (40 * MenuScale), "SCP-049 State: " + Curr049\State)
			EndIf
			If Curr096 <> Null Then
				Text(x, y + (60 * MenuScale), "SCP-096 Position: (" + f2s(EntityX(Curr096\OBJ), 2) + ", " + f2s(EntityY(Curr096\OBJ), 2) + ", " + f2s(EntityZ(Curr096\OBJ), 2) + ")")
				Text(x, y + (80 * MenuScale), "SCP-096 Idle: " + Curr096\Idle)
				Text(x, y + (100 * MenuScale), "SCP-096 State: " + Curr096\State)
			EndIf
			If Curr106 <> Null Then
				Text(x, y + (120 * MenuScale), "SCP-106 Position: (" + f2s(EntityX(Curr106\OBJ), 2) + ", " + f2s(EntityY(Curr106\OBJ), 2) + ", " + f2s(EntityZ(Curr106\OBJ), 2) + ")")
				Text(x, y + (140 * MenuScale), "SCP-106 Idle: " + Curr106\Idle)
				Text(x, y + (160 * MenuScale), "SCP-106 State: " + Curr106\State)
			EndIf
			If Curr173 <> Null Then
				Text(x, y + (180 * MenuScale), "SCP-173 Position: (" + f2s(EntityX(Curr173\OBJ), 2) + ", " + f2s(EntityY(Curr173\OBJ), 2) + ", " + f2s(EntityZ(Curr173\OBJ), 2) + ")")
				Text(x, y + (200 * MenuScale), "SCP-173 Idle: " + Curr173\Idle)
				Text(x, y + (220 * MenuScale), "SCP-173 State: " + Curr173\State)
			EndIf
			
			Text(x, y + (260 * MenuScale), "Pills Taken: " + I_500\Taken)
			
			Text(x, y + (300 * MenuScale), "SCP-008 Infection: " + I_008\Timer)
			Text(x, y + (320 * MenuScale), "SCP-409 Crystallization: " + I_409\Timer)
			Text(x, y + (340 * MenuScale), "SCP-427 State (Secs): " + Int(I_427\Timer / 70.0))
			For i = 0 To 7
				Text(x, y + ((360 + (20 * i)) * MenuScale), "SCP-1025 State " + i + ": " + I_1025\State[i])
			Next
			
			If I_005\ChanceToSpawn = 1 Then
				Text(x, y + (540 * MenuScale), "SCP-005 Spawned in the Chamber!")
			ElseIf I_005\ChanceToSpawn = 2
				Text(x, y + (540 * MenuScale), "SCP-005 Spawned in Dr.Maynard's Office!")
			ElseIf I_005\ChanceToSpawn = 3
				Text(x, y + (540 * MenuScale), "SCP-005 Spawned in SCP-409's Containment Chamber!")
			EndIf
			;[End Block]
	End Select
	SetFont(fo\FontID[Font_Default])
End Function

Function RenderGUI()
	CatchErrors("Uncaught (RenderGUI)")
	
	Local e.Events, it.Items, a_it.Items
	Local Temp%, x%, y%, z%, i%, YawValue#, PitchValue#
	Local x1#, x2#, x3#, y1#, y2#, y3#, z2#, ProjY#, Scale#, Pvt%
	Local n%, xTemp%, yTemp%, StrTemp$
	Local Width%, Height%
	
	If MenuOpen Lor ConsoleOpen Lor SelectedDoor <> Null Lor InvOpen Lor OtherOpen <> Null Lor me\EndingTimer < 0.0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "dimension_106" Then
		For e.Events = Each Events
			If PlayerRoom = e\room Then
				If Float(e\EventStr) < 1000.0 Then
					If e\EventState > 600.0 Then
						If me\BlinkTimer < -3.0 And me\BlinkTimer > -10.0 Then
							If (Not e\Img) Then
								If me\BlinkTimer > -5.0 And Rand(30) = 1 Then
									PlaySound_Strict(DripSFX[Rand(0, 5)])
									If (Not e\Img) Then e\Img = LoadImage_Strict("GFX\npcs\scp_106_face.png")
								EndIf
							Else
								DrawImage(e\Img, mo\Viewport_Center_X - Rand(390, 310), mo\Viewport_Center_Y - Rand(290, 310))
							EndIf
						Else
							If e\Img <> 0 Then
								FreeImage(e\Img) : e\Img = 0
							EndIf
						EndIf
						Exit
					EndIf
				Else
					If me\BlinkTimer < -3.0 And me\BlinkTimer > -10.0 Then
						If (Not e\Img) Then
							If me\BlinkTimer > -5.0 Then
								If (Not e\Img) Then
									e\Img = LoadImage_Strict("GFX\kneel_mortal.png")
									If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage(e\Img, mo\Viewport_Center_X - Rand(390, 310), mo\Viewport_Center_Y - Rand(290, 310))
						EndIf
					Else
						If e\Img <> 0 Then
							FreeImage(e\Img) : e\Img = 0
						EndIf
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
	
	If I_294\Using Then Render294()
	
	If ClosestButton <> 0 And (Not InvOpen) And (Not I_294\Using) And OtherOpen = Null And SelectedDoor = Null And SelectedScreen = Null And (Not MenuOpen) And (Not ConsoleOpen) And SelectedDifficulty\OtherFactors <> EXTREME Then
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
		
		DrawImage(t\IconID[5], mo\Viewport_Center_X + Sin(YawValue) * (opt\GraphicWidth / 3) - 32, mo\Viewport_Center_Y - Sin(PitchValue) * (opt\GraphicHeight / 3) - 32)
	EndIf
	
	If ClosestItem <> Null And (Not InvOpen) And (Not I_294\Using) And OtherOpen = Null And SelectedDoor = Null And SelectedScreen = Null And (Not MenuOpen) And (Not ConsoleOpen) And SelectedDifficulty\OtherFactors <> EXTREME Then
		YawValue = -DeltaYaw(Camera, ClosestItem\Collider)
		If YawValue > 90.0 And YawValue =< 180.0 Then YawValue = 90.0
		If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
		PitchValue = -DeltaPitch(Camera, ClosestItem\Collider)
		If PitchValue > 90.0 And PitchValue =< 180.0 Then PitchValue = 90.0
		If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
		
		DrawImage(t\IconID[6], mo\Viewport_Center_X + Sin(YawValue) * (opt\GraphicWidth / 3) - 32, mo\Viewport_Center_Y - Sin(PitchValue) * (opt\GraphicHeight / 3) - 32)
	EndIf
	
	If (Not InvOpen) And (Not I_294\Using) And OtherOpen = Null And SelectedDoor = Null And SelectedScreen = Null And (Not MenuOpen) And (Not ConsoleOpen) And SelectedDifficulty\OtherFactors <> EXTREME Then
		If ga\DrawHandIcon Then DrawImage(t\IconID[5], mo\Viewport_Center_X - 32, mo\Viewport_Center_Y - 32)
		For i = 0 To 3
			If ga\DrawArrowIcon[i] Then
				x = mo\Viewport_Center_X - 32
				y = mo\Viewport_Center_Y - 32		
				Select i
					Case 0
						;[Block]
						y = y - 69
						;[End Block]
					Case 1
						;[Block]
						x = x + 69
						;[End Block]
					Case 2
						;[Block]
						y = y + 69
						;[End Block]
					Case 3
						;[Block]
						x = x - 69
						;[End Block]
				End Select
				DrawImage(t\IconID[5], x, y)
				Color(0, 0, 0)
				Rect(x + 4, y + 4, 56, 56)
				DrawImage(ga\ArrowIMG[i], x + 21, y + 21)
			EndIf
		Next
	EndIf
	
	If opt\HUDEnabled And SelectedDifficulty\OtherFactors <> EXTREME Then 
		RenderHUD()
	EndIf
	
	If chs\DebugHUD <> 0 Then
		RenderDebugHUD()
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage(SelectedScreen\Img, mo\Viewport_Center_X - ImageWidth(SelectedScreen\Img) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedScreen\Img) / 2)
		
		If mo\MouseUp1 Lor mo\MouseHit2 Then
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
			
			x = mo\Viewport_Center_X - ImageWidth(t\ImageID[4]) * (Scale / 2)
			y = mo\Viewport_Center_Y - ImageHeight(t\ImageID[4]) * (Scale / 2)	
			
			SetFont(fo\FontID[Font_Digital])
			If msg\KeyPadMsg <> "" Then 
				If (msg\KeyPadTimer Mod 70.0) < 35.0 Then Text(mo\Viewport_Center_X, y + (124 * Scale), msg\KeyPadMsg, True, True)
			Else
				Text(mo\Viewport_Center_X, y + (70 * Scale), "ACCESS CODE: ", True, True)	
				SetFont(fo\FontID[Font_Digital_Big])
				Text(mo\Viewport_Center_X, y + (124 * Scale), msg\KeyPadInput, True, True)
			EndIf
			
			x = x + (44 * Scale)
			y = y + (249 * Scale)
			
			If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
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
		
		x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * 10 / 2 + INVENTORY_GFX_SPACING * (10 / 2 - 1)) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE * (Float(OtherSize) / 10 * 2 - 1) - INVENTORY_GFX_SPACING
		
		IsMouseOn = -1
		For n = 0 To OtherSize - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If IsMouseOn = n Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			RenderFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (IsMouseOn = n Lor SelectedItem <> OtherOpen\SecondInv[n]) Then DrawImage(OtherOpen\SecondInv[n]\InvImg, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
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
				x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * 10 / 2 + INVENTORY_GFX_SPACING * (10 / 2 - 1.0)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If mo\MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			EndIf
		EndIf
		
		If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	ElseIf InvOpen Then
		x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = mo\Viewport_Center_Y - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		If MaxItemAmount = 2 Then
			y = y + INVENTORY_GFX_SIZE
			x = x - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING) / 2
		EndIf
		
		IsMouseOn = -1
		For n = 0 To MaxItemAmount - 1
			If MouseOn(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE) Then IsMouseOn = n
			
			If Inventory(n) <> Null Then
				Color(200, 200, 200)
				Select Inventory(n)\ItemTemplate\TempName 
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
						If wi\BallisticHelmet Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
					Case "scp714"
						;[Block]
						If I_714\Using Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
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
						If wi\SCRAMBLE Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
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
						If I_427\Using Then Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
						;[End Block]
				End Select
			EndIf
			
			If IsMouseOn = n Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			Color(255, 255, 255)
			RenderFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null Then
				If IsMouseOn = n Lor SelectedItem <> Inventory(n) Then 
					DrawImage(Inventory(n)\InvImg, x + (INVENTORY_GFX_SIZE / 2) - 32, y + (INVENTORY_GFX_SIZE / 2) - 32)
				EndIf
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				If IsMouseOn = n Then
					If SelectedItem = Null Then
						SetFont(fo\FontID[Font_Default])
						Color(255, 255, 255)	
						Text(x + INVENTORY_GFX_SIZE / 2, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15, Inventory(n)\Name, True)	
					EndIf
				EndIf
			EndIf					
			
			x = x + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If MaxItemAmount >= 4 And n = MaxItemAmount / 2 - 1 Then 
				y = y + INVENTORY_GFX_SIZE * 2 
				x = mo\Viewport_Center_X - (INVENTORY_GFX_SIZE * MaxItemAmount / 2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2 
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If mo\MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			EndIf
		EndIf
		
		If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	Else
		If SelectedItem <> Null Then
			Select SelectedItem\ItemTemplate\TempName
				Case "nvg", "supernvg", "finenvg"
					;[Block]
					If (Not PreventItemOverlapping(False, True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "nvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 1, "pairs of goggles") Then Return
								;[End Block]
							Case "supernvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 2, "pairs of goggles") Then Return
								;[End Block]
							Case "finenvg"
								;[Block]
								If IsDoubleItem(wi\NightVision, 3, "pairs of goggles") Then Return
								;[End Block]
						End Select
						
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State3)
					EndIf
					;[End Block]
				Case "key0", "key1", "key2", "key3", "key4", "key5", "key6", "keyomni", "scp860", "hand", "hand2", "hand3", "25ct", "scp005", "key", "coin", "mastercard"
					;[Block]
					DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If me\Bloodloss = 0.0 And me\Injuries = 0.0 Then
						Return
					Else
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "paper"
					;[Block]
					If I_035\Sad = 1 Then
						If SelectedItem\ItemTemplate\Name = "Document SCP-035" Then
							If SelectedItem\ItemTemplate\Img <> 0 Then
								FreeImage(SelectedItem\ItemTemplate\Img) : SelectedItem\ItemTemplate\Img = 0
								I_035\Sad = 2
							EndIf
						EndIf
					EndIf
					
					If (Not SelectedItem\ItemTemplate\Img) Then
						Select SelectedItem\ItemTemplate\Name
							Case "Burnt Note" 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\note_Maynard.png")
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(0, 0, 0)
								SetFont(fo\FontID[Font_Default])
								Text(277, 469, AccessCode, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Document SCP-372"
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(37, 45, 137)
								SetFont(fo\FontID[Font_Journal])
								Temp = ((Int(AccessCode) * 3) Mod 10000)
								If Temp < 1000 Then Temp = Temp + 1000
								Text(383 * MenuScale, 734 * MenuScale, Temp, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Document SCP-035"
								;[Block]
								If I_035\Sad <> 0 Then
									SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\doc_035_sad.png")
								Else
									SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								EndIf
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								;[End Block]
							Default 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
								;[End Block]
						End Select
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					DrawImage(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "scp1025"
					;[Block]
					GiveAchievement(Achv1025)
					If (Not SelectedItem\ItemTemplate\Img) Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\1025\1025(" + (Int(SelectedItem\State) + 1) + ").png")	
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					; ~ RadioState[5] = Has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState[6] = A timer for the "code channel"
					; ~ RadioState[7] = Another timer for the "code channel"
					
					If (Not SelectedItem\ItemTemplate\Img) Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					StrTemp = ""
					
					x = opt\GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img)
					y = opt\GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img)
					
					DrawImage(SelectedItem\ItemTemplate\Img, x, y)
					
					If SelectedItem\State > 0.0 Lor (SelectedItem\ItemTemplate\TempName = "fineradio" Lor SelectedItem\ItemTemplate\TempName = "veryfineradio") Then
						If PlayerRoom\RoomTemplate\Name <> "dimension_106" And CoffinDistance >= 8.0 Then
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									StrTemp = "        USER TRACK PLAYER - "
									If (Not opt\EnableUserTracks) Then
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
							
							x = x + 66
							y = y + 419
							
							Color(30, 30, 30)
							
							If SelectedItem\ItemTemplate\TempName = "radio" Lor SelectedItem\ItemTemplate\TempName = "18vradio" Then
								For i = 0 To 4
									Rect(x, y + (8 * i), 43 - (i * 6), 4, Ceil(SelectedItem\State / 20.0) > 4 - i)
								Next
							EndIf	
							
							SetFont(fo\FontID[Font_Digital])
							Text(x + 60, y, "CHN")	
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio" Then
								StrTemp = ""
								For i = 0 To Rand(5, 30)
									StrTemp = StrTemp + Chr(Rand(1, 100))
								Next
								
								SetFont(fo\FontID[Font_Digital_Big])
								Text(x + 97, y + 16, Rand(0, 9), True, True)
							Else
								SetFont(fo\FontID[Font_Digital_Big])
								Text(x + 97, y + 16, Int(SelectedItem\State2 + 1.0), True, True)
							EndIf
							
							SetFont(fo\FontID[Font_Digital])
							If StrTemp <> "" Then
								StrTemp = Right(Left(StrTemp, (Int(MilliSecs2() / 300) Mod Len(StrTemp))), 10)
								Text(x + 32, y + 33, StrTemp)
							EndIf
							SetFont(fo\FontID[Font_Default])
						EndIf
					EndIf
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If wi\BallisticVest = 0 Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					
					Width = 300
					Height = 20
					x = mo\Viewport_Center_X - (Width / 2)
					y = mo\Viewport_Center_Y + 80
					
					RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If (Not PreventItemOverlapping(True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "gasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 1, "gas masks") Then Return
								;[End Block]
							Case "supergasmask"
								;[Block]
								If IsDoubleItem(wi\GasMask, 2, "gas masks") Then Return
								;[End Block]
							Case "gasmask3"
								;[Block]
								If IsDoubleItem(wi\GasMask, 3, "gas masks") Then Return
								;[End Block]
						End Select
						
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "nav", "nav300", "nav310", "navulti"
					;[Block]
					If (Not SelectedItem\ItemTemplate\Img) Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					x = opt\GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img) * 0.5 + 20.0
					y = opt\GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img) * 0.4 - 85.0
					
					Local PlayerX%, PlayerZ%
					
					DrawImage(SelectedItem\ItemTemplate\Img, x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85)
					
					SetFont(fo\FontID[Font_Digital])
					
					Local NavWorks% = True
					
					If PlayerRoom\RoomTemplate\Name = "dimension_106" Lor PlayerRoom\RoomTemplate\Name = "dimension_1499" Then
						NavWorks = False
					ElseIf PlayerRoom\RoomTemplate\Name = "cont2_860_1" Then
						If forest_event\EventState = 1.0 Then NavWorks = False
					EndIf
					
					If (Not NavWorks) Then
						If (MilliSecs2() Mod 800) < 200 Then
							Color(200, 0, 0)
							Text(x, y + (NAV_HEIGHT / 2) - 80, "ERROR 06", True)
							Text(x, y + (NAV_HEIGHT / 2) - 60, "LOCATION UNKNOWN", True)						
						EndIf
					Else
						If (SelectedItem\State > 0.0 Lor (SelectedItem\ItemTemplate\TempName = "nav300" Lor SelectedItem\ItemTemplate\TempName = "navulti")) And (Rnd(CoffinDistance + 15.0) > 1.0 Lor PlayerRoom\RoomTemplate\Name <> "cont1_895") Then
							PlayerX = Floor((EntityX(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							PlayerZ = Floor((EntityZ(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							
							SetBuffer(ImageBuffer(t\ImageID[7]))
							
							Local xx% = x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2
							Local yy% = y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85
							
							DrawImage(SelectedItem\ItemTemplate\Img, xx, yy)
							
							x = x - 12.0 + (((EntityX(me\Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							y = y + 12.0 - (((EntityZ(me\Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							For x2 = Max(0.0, PlayerX - 6) To Min(MapGridSize, PlayerX + 6)
								For z2 = Max(0.0, PlayerZ - 6) To Min(MapGridSize, PlayerZ + 6)
									If CoffinDistance > 16.0 Lor Rnd(16.0) < CoffinDistance Then 
										If CurrMapGrid\Grid[x2 + (z2 * MapGridSize)] > MapGrid_NoTile And (CurrMapGrid\Found[x2 + (z2 * MapGridSize)] > MapGrid_NoTile Lor SelectedItem\ItemTemplate\TempName = "nav310" Lor SelectedItem\ItemTemplate\TempName = "navulti") Then
											Local DrawX% = x + (PlayerX - 1 - x2) * 24, DrawY% = y - (PlayerZ - 1 - z2) * 24
											
											Color(30, 30, 30)
											If x2 + 1.0 =< MapGridSize Then
												If CurrMapGrid\Grid[(x2 + 1) + (z2 * MapGridSize)] = MapGrid_NoTile
													Rect(DrawX - 12, DrawY - 12, 1, 24)
												EndIf
											Else
												Rect(DrawX - 12, DrawY - 12, 1, 24)
											EndIf
											If x2 - 1.0 >= 0.0 Then
												If CurrMapGrid\Grid[(x2 - 1) + (z2 * MapGridSize)] = MapGrid_NoTile
													Rect(DrawX + 12, DrawY - 12, 1, 24)
												EndIf
											Else
												Rect(DrawX + 12, DrawY - 12, 1, 24)
											EndIf
											If z2 - 1.0 >= 0.0 Then
												If CurrMapGrid\Grid[x2 + ((z2 - 1) * MapGridSize)] = MapGrid_NoTile
													Rect(DrawX - 12, DrawY - 12, 24, 1)
												EndIf
											Else
												Rect(DrawX - 12, DrawY - 12, 24, 1)
											EndIf
											If z2 + 1.0 =< MapGridSize Then
												If CurrMapGrid\Grid[x2 + ((z2 + 1) * MapGridSize)] = MapGrid_NoTile
													Rect(DrawX - 12, DrawY + 12, 24, 1)
												EndIf
											Else
												Rect(DrawX - 12, DrawY + 12, 24, 1)
											EndIf
										EndIf
									EndIf
								Next
							Next
							
							SetBuffer(BackBuffer())
							DrawImageRect(t\ImageID[7], xx + 80, yy + 70, xx + 80, yy + 70, 270, 230)
							If SelectedItem\ItemTemplate\TempName = "nav" Lor SelectedItem\ItemTemplate\TempName = "nav300" Then
								Color(100, 0, 0)
							Else
								Color(30, 30, 30)
							EndIf
							Rect(xx + 80, yy + 70, 270, 230, False)
							
							x = opt\GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img) * 0.5 + 20.0
							y = opt\GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img) * 0.4 - 85.0
							
							If SelectedItem\ItemTemplate\TempName = "nav" Lor SelectedItem\ItemTemplate\TempName = "nav300" Then 
								Color(100, 0, 0)
							Else
								Color(30, 30, 30)
							EndIf
							If ((MilliSecs2() Mod 800) < 200) Then
								If SelectedItem\ItemTemplate\TempName = "nav" Lor SelectedItem\ItemTemplate\TempName = "nav300" Then
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
							
							If SelectedItem\ItemTemplate\TempName = "navulti" And (MilliSecs2() Mod 600) < 400 Then
								If Curr173 <> Null Then
									Dist = EntityDistanceSquared(Camera, Curr173\OBJ)
									If Dist < 900.0 Then
										Dist = Sqr(Ceil(Dist / 8.0) * 8.0) ; ~ This is probably done to disguise SCP-173's teleporting behavior
										Color(100, 0, 0)
										Oval(x - (Dist * 3), y - 7 - (Dist * 3), Dist * 6, Dist * 6, False)
										Text(x - (NAV_WIDTH / 2) + 10, y - (NAV_HEIGHT / 2) + 30, "SCP-173")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr106 <> Null Then
									Dist = EntityDistanceSquared(Camera, Curr106\OBJ)
									If Dist < 900.0 Then
										Dist = Sqr(Dist)
										Color(100, 0, 0)
										Oval(x - (Dist * 1.5), y - 7 - (Dist * 1.5), Dist * 3, Dist * 3, False)
										Text(x - (NAV_WIDTH / 2) + 10, y - (NAV_HEIGHT / 2) + 30 + (20 * SCPs_Found), "SCP-106")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr096 <> Null Then 
									Dist = EntityDistanceSquared(Camera, Curr096\OBJ)
									If Dist < 900.0 Then
										Dist = Sqr(Dist)
										Color(100, 0, 0)
										Oval(x - (Dist * 1.5), y - 7 - (Dist * 1.5), Dist * 3, Dist * 3, False)
										Text(x - (NAV_WIDTH / 2) + 10, y - (NAV_HEIGHT / 2) + 30 + (20 * SCPs_Found), "SCP-096")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr049 <> Null Then
									If (Not Curr049\HideFromNVG) Then
										Dist = EntityDistanceSquared(Camera, Curr049\OBJ)
										If Dist < 900.0 Then
											Dist = Sqr(Dist)
											Color(100, 0, 0)
											Oval(x - (Dist * 1.5), y - 7 - (Dist * 1.5), Dist * 3, Dist * 3, False)
											Text(x - (NAV_WIDTH / 2) + 10, y - (NAV_HEIGHT / 2) + 30 + (20 * SCPs_Found), "SCP-049")
											SCPs_Found = SCPs_Found + 1
										EndIf
									EndIf
								EndIf
								If PlayerRoom\RoomTemplate\Name = "cont1_895" Then
									If CoffinDistance < 8.0 Then
										Dist = Rnd(4.0, 8.0)
										Color(100, 0, 0)
										Oval(x - (Dist * 1.5), y - 7.0 - (Dist * 1.5), Dist * 3, Dist * 3, False)
										Text(x - (NAV_WIDTH / 2) + 10, y - (NAV_HEIGHT / 2) + 30 + (20 * SCPs_Found), "SCP-895")
									EndIf
								EndIf
							EndIf
							
							Color(30, 30, 30)
							If SelectedItem\ItemTemplate\TempName = "nav" Lor SelectedItem\ItemTemplate\TempName = "nav300" Then
								Color(100, 0, 0)
								xTemp = x - (NAV_WIDTH / 2) + 196.0
								yTemp = y - (NAV_HEIGHT / 2) + 10.0
								Rect(xTemp, yTemp, 80, 20, False)
								
								; ~ Battery
								If SelectedItem\State =< 20.0 Then
									Color(100, 0, 0)
								Else
									Color(30, 30, 30)
								EndIf
								For i = 1 To Min(Ceil(SelectedItem\State / 10.0), 10.0)
									Rect(xTemp + (i * 8) - 6, yTemp + 4, 4, 12)
								Next
								SetFont(fo\FontID[Font_Digital])
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					If (Not PreventItemOverlapping(False, False, True)) Then
						Select SelectedItem\ItemTemplate\TempName
							Case "gasmask"
								;[Block]
								If IsDoubleItem(I_1499\Using, 1, "gas masks") Then Return
								;[End Block]
							Case "super1499"
								;[Block]
								If IsDoubleItem(I_1499\Using, 2, "gas masks") Then Return
								;[End Block]
						End Select
						
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "badge", "oldpaper", "ticket"
					;[Block]
					If (Not SelectedItem\ItemTemplate\Img) Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						SelectedItem\ItemTemplate\Img = ScaleImage2(SelectedItem\ItemTemplate\Img, MenuScale, MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					;[End Block]
				Case "helmet"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, True)) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State)
					EndIf
					;[End Block]
				Case "scramble"
					;[Block]
					If (Not PreventItemOverlapping(False, False, False, False, True)) Then
						DrawImage(SelectedItem\ItemTemplate\InvImg, mo\Viewport_Center_X - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, mo\Viewport_Center_Y - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = mo\Viewport_Center_X - (Width / 2)
						y = mo\Viewport_Center_Y + 80
						
						RenderBar(BlinkMeterIMG, x, y, Width, Height, SelectedItem\State3)
					EndIf
					;[End Block]
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\Img <> 0 Then
					Local IN$ = SelectedItem\ItemTemplate\TempName
					
					If IN = "paper" Lor IN = "badge" Lor IN = "oldpaper" Lor IN = "ticket" Lor IN = "scp1025" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\ItemTemplate\Tempname
								
								If IN2 = "paper" Lor IN2 = "badge" Lor IN2 = "oldpaper" Lor IN2 = "ticket" Lor IN2 = "scp1025" Then
									If a_it\ItemTemplate\Img <> 0 Then
										If a_it\ItemTemplate\Img <> SelectedItem\ItemTemplate\Img Then
											FreeImage(a_it\ItemTemplate\Img) : a_it\ItemTemplate\Img = 0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		EndIf		
	EndIf
	
	CatchErrors("RenderGUI")
End Function

Function UpdateMenu()
	CatchErrors("Uncaught (UpdateMenu)")
	
	Local r.Rooms
	Local x%, y%, z%, Width%, Height%, i%
	
	If MenuOpen Then
		If PlayerRoom\RoomTemplate\Name <> "gate_b" And PlayerRoom\RoomTemplate\Name <> "gate_a" Then
			If me\StopHidingTimer = 0.0 Then
				If Curr173 <> Null And Curr106 <> Null Then
					If EntityDistanceSquared(Curr173\Collider, me\Collider) < 16.0 Lor EntityDistanceSquared(Curr106\Collider, me\Collider) < 16.0 Then 
						me\StopHidingTimer = 1.0
					EndIf	
				EndIf
			ElseIf me\StopHidingTimer < 40.0
				If me\KillTimer >= 0.0 Then 
					me\StopHidingTimer = me\StopHidingTimer + fps\Factor[0]
					If me\StopHidingTimer >= 40.0 Then
						PlaySound_Strict(HorrorSFX[15])
						CreateMsg("STOP HIDING!")
						MenuOpen = False
						mm\ShouldDeleteGadgets = True
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		ConsoleOpen = False
		
		Width = ImageWidth(t\ImageID[0])
		Height = ImageHeight(t\ImageID[0])
		x = mo\Viewport_Center_X - (Width / 2)
		y = mo\Viewport_Center_Y - (Height / 2)
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		If (Not mo\MouseDown1) Then mm\OnSliderID = 0
		
		If mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			; ~ Just save this line, ok?
		ElseIf mm\AchievementsMenu =< 0 And OptionsMenu > 0 And QuitMsg =< 0 And me\KillTimer >= 0.0
			If UpdateMainMenuButton(x + (101 * MenuScale), y + (460 * MenuScale), 230 * MenuScale, 60 * MenuScale, "Back") Then
				mm\AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				mo\MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias(opt\AntiAliasing)
				TextureLodBias(opt\TextureDetailsLevel)
				TextureAnisotropic(opt\AnisotropicLevel)
				mm\ShouldDeleteGadgets = True
			EndIf
			
			If UpdateMainMenuButton(x - (5 * MenuScale), y, 100 * MenuScale, 30 * MenuScale, "GRAPHICS", False) Then
				OptionsMenu = 1
				mm\ShouldDeleteGadgets = True
			EndIf
			If UpdateMainMenuButton(x + (105 * MenuScale), y, 100 * MenuScale, 30 * MenuScale, "AUDIO", False) Then
				OptionsMenu = 2
				mm\ShouldDeleteGadgets = True
			EndIf
			If UpdateMainMenuButton(x + (215 * MenuScale), y, 100 * MenuScale, 30 * MenuScale, "CONTROLS", False) Then
				OptionsMenu = 3
				mm\ShouldDeleteGadgets = True
			EndIf
			If UpdateMainMenuButton(x + (325 * MenuScale), y, 100 * MenuScale, 30 * MenuScale, "ADVANCED", False) Then
				OptionsMenu = 4
				mm\ShouldDeleteGadgets = True
			EndIf
			
			Select OptionsMenu
				Case 1 ; ~ Graphics
					;[Block]
					y = y + (50 * MenuScale)
					
					opt\BumpEnabled = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\BumpEnabled, True)
					
					y = y + (30 * MenuScale)
					
					opt\VSync = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\VSync)
					
					y = y + (30 * MenuScale)
					
					opt\AntiAliasing = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\AntiAliasing, opt\DisplayMode <> 0)
					
					y = y + (30 * MenuScale)
					
					opt\EnableRoomLights = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\EnableRoomLights)
					
					y = y + (40 * MenuScale)
					
					opt\ScreenGamma = UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, opt\ScreenGamma * 50.0) / 50.0
					
					y = y + (45 * MenuScale)
					
					opt\ParticleAmount = UpdateMainMenuSlider3(x + (270 * MenuScale), y, 100 * MenuScale, opt\ParticleAmount, 2, "MINIMAL", "REDUCED", "FULL")
					
					y = y + (45 * MenuScale)
					
					opt\TextureDetails = UpdateMainMenuSlider5(x + (270 * MenuScale), y, 100 * MenuScale, opt\TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
					Select opt\TextureDetails
						Case 0
							;[Block]
							opt\TextureDetailsLevel = 0.8
							;[End Block]
						Case 1
							;[Block]
							opt\TextureDetailsLevel = 0.4
							;[End Block]
						Case 2
							;[Block]
							opt\TextureDetailsLevel = 0.0
							;[End Block]
						Case 3
							;[Block]
							opt\TextureDetailsLevel = -0.4
							;[End Block]
						Case 4
							;[Block]
							opt\TextureDetailsLevel = -0.8
							;[End Block]
					End Select
					TextureLodBias(opt\TextureDetailsLevel)
					
					y = y + (35 * MenuScale)
					
					opt\SaveTexturesInVRAM = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\SaveTexturesInVRAM, True)
					
					y = y + (40 * MenuScale)
					
					opt\CurrFOV = UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, opt\CurrFOV * 2.0) / 2.0
					opt\FOV = opt\CurrFOV + 40
					CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * opt\RealGraphicWidth / opt\RealGraphicHeight)) / 2.0))
					
					y = y + (45 * MenuScale)
					
					opt\Anisotropic = UpdateMainMenuSlider5(x + (270 * MenuScale), y, 100 * MenuScale, opt\Anisotropic, 4, "Trilinear", "2x", "4x", "8x", "16x")
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
					TextureAnisotropic(opt\AnisotropicLevel)
					
					y = y + (35 * MenuScale)
					
					opt\Atmosphere = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\Atmosphere, True)
				Case 2 ; ~ Audio
					;[Block]
					y = y + (50 * MenuScale)
					
					opt\MusicVolume = (UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, opt\MusicVolume * 100.0) / 100.0)
					
					y = y + (40 * MenuScale)
					
					opt\PrevSFXVolume = (UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, opt\SFXVolume * 100.0) / 100.0)
					If (Not me\Deaf) Then opt\SFXVolume = opt\PrevSFXVolume
					
					y = y + (40 * MenuScale)
					
					opt\EnableSFXRelease = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\EnableSFXRelease, True)
					
					y = y + (30 * MenuScale)
					
					opt\EnableUserTracks = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\EnableUserTracks, True)
					
					If opt\EnableUserTracks Then
						y = y + (30 * MenuScale)
						
						opt\UserTrackMode = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\UserTrackMode)
						
						UpdateMainMenuButton(x, y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale, "Scan for User Tracks", False, False, True)
					EndIf
					;[End Block]
				Case 3 ; ~ Controls
					;[Block]
					y = y + (50 * MenuScale)
					
					opt\MouseSensitivity = (UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, (opt\MouseSensitivity + 0.5) * 100.0) / 100.0) - 0.5
					
					y = y + (40 * MenuScale)
					
					opt\InvertMouse = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\InvertMouse)
					
					y = y + 40 * MenuScale
					
					opt\MouseSmoothing = UpdateMainMenuSlideBar(x + (270 * MenuScale), y, 100 * MenuScale, (opt\MouseSmoothing) * 50.0) / 50.0
					
					y = y + (70 * MenuScale)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, 210.0)], 3)		
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (20 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, 210.0)], 4)	
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (40 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, 210.0)], 5)				
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (60 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, 210.0)], 6)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (80 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, 210.0)], 7)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (100 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, 210.0)], 8)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (120 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, 210.0)], 9)				
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (140 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, 210.0)], 10)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (160 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, 210.0)], 11)	
					
					If opt\CanOpenConsole Then UpdateMainMenuInputBox(x + (200 * MenuScale), y + 180 * MenuScale, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, 210.0)], 12)
					
					UpdateMainMenuInputBox(x + (200 * MenuScale), y + (200 * MenuScale), 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, 210.0)], 13)
					
					Local TempKey%
					
					For i = 0 To 227
						If KeyHit(i) Then
							TempKey = i
							Exit
						EndIf
					Next
					If TempKey <> 0 Then
						Select SelectedInputBox
							Case 3
								;[Block]
								key\MOVEMENT_UP = TempKey
								;[End Block]
							Case 4
								;[Block]
								key\MOVEMENT_LEFT = TempKey
								;[End Block]
							Case 5
								;[Block]
								key\MOVEMENT_DOWN = TempKey
								;[End Block]
							Case 6
								;[Block]
								key\MOVEMENT_RIGHT = TempKey
								;[End Block]
							Case 7
								;[Block]
								key\SPRINT = TempKey
								;[End Block]
							Case 8
								;[Block]
								key\CROUCH = TempKey
								;[End Block]
							Case 9
								;[Block]
								key\BLINK = TempKey
								;[End Block]
							Case 10
								;[Block]
								key\INVENTORY = TempKey
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
					y = y + (50 * MenuScale)
					
					opt\HUDEnabled = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\HUDEnabled)
					
					y = y + (30 * MenuScale)
					
					Local PrevCanOpenConsole% = opt\CanOpenConsole
					
					opt\CanOpenConsole = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\CanOpenConsole)
					
					If PrevCanOpenConsole Then
						If PrevCanOpenConsole <> opt\CanOpenConsole Then
							mm\ShouldDeleteGadgets = True
						EndIf
					EndIf
					
					y = y + (30 * MenuScale)
					
					If opt\CanOpenConsole Then opt\ConsoleOpening = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\ConsoleOpening)
					
					y = y + (30 * MenuScale)
					
					opt\AchvMsgEnabled = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\AchvMsgEnabled)
					
					y = y + (30 * MenuScale)
					
					opt\AutoSaveEnabled = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\AutoSaveEnabled, SelectedDifficulty\SaveType <> SAVEANYWHERE)
					
					y = y + (30 * MenuScale)
					
					opt\ShowFPS = UpdateMainMenuTick(x + (270 * MenuScale), y, opt\ShowFPS)
					
					y = y + (30 * MenuScale)
					
					Local PrevCurrFrameLimit% = opt\CurrFrameLimit > 0.0
					
					If UpdateMainMenuTick(x + (270 * MenuScale), y, opt\CurrFrameLimit > 0.0) Then
						opt\CurrFrameLimit = UpdateMainMenuSlideBar(x + (150 * MenuScale), y + (40 * MenuScale), 100 * MenuScale, opt\CurrFrameLimit# * 99.0) / 99.0
						opt\CurrFrameLimit = Max(opt\CurrFrameLimit, 0.01)
						opt\FrameLimit = 19 + (opt\CurrFrameLimit * 100.0)
					Else
						opt\CurrFrameLimit = 0.0
						opt\FrameLimit = 0
					EndIf
					
					If PrevCurrFrameLimit Then
						If PrevCurrFrameLimit <> opt\CurrFrameLimit Then
							mm\ShouldDeleteGadgets = True
						EndIf
					EndIf
					;[End Block]
			End Select
		ElseIf mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg > 0 And me\KillTimer >= 0.0
			Local QuitButton% = 60 
			
			If SelectedDifficulty\SaveType = SAVEONQUIT Lor SelectedDifficulty\SaveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				Local AbleToSave% = True
				
				If RN = "cont1_173_intro" Lor RN = "gate_b" Lor RN = "gate_a" Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave Then
					QuitButton = 140
					If UpdateMainMenuButton(x, y + (60 * MenuScale), 430 * MenuScale, 60 * MenuScale, "Save & Quit") Then
						me\DropSpeed = 0.0
						SaveGame(SavePath + CurrSave + "\")
						NullGame()
						CurrSave = ""
						ResetInput()
						Return
					EndIf
				EndIf
			EndIf
			
			If UpdateMainMenuButton(x, y + (QuitButton * MenuScale), 430 * MenuScale, 60 * MenuScale, "Quit") Then
				NullGame()
				CurrSave = ""
				ResetInput()
				Return
			EndIf
			
			If UpdateMainMenuButton(x + (101 * MenuScale), y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				mm\AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				mo\MouseHit1 = False
				mm\ShouldDeleteGadgets = True
			EndIf
		Else
			If UpdateMainMenuButton(x + (101 * MenuScale), y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				mm\AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				mo\MouseHit1 = False
				mm\ShouldDeleteGadgets = True
			EndIf
			
			If mm\AchievementsMenu > 0 Then
				If mm\AchievementsMenu =< Floor(Float(MAXACHIEVEMENTS - 1) / 12.0) Then 
					If UpdateMainMenuButton(x + (341 * MenuScale), y + (344 * MenuScale), 50 * MenuScale, 60 * MenuScale, ">") Then
						mm\AchievementsMenu = mm\AchievementsMenu + 1
						mm\ShouldDeleteGadgets = True
					EndIf
				EndIf
				If mm\AchievementsMenu > 1 Then
					If UpdateMainMenuButton(x + (41 * MenuScale), y + (344 * MenuScale), 50 * MenuScale, 60 * MenuScale, "<") Then
						mm\AchievementsMenu = mm\AchievementsMenu - 1
						mm\ShouldDeleteGadgets = True
					EndIf
				EndIf
			EndIf
		EndIf
		
		y = y + (10 * MenuScale)
		
		If mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			If me\KillTimer >= 0.0 Then	
				y = y + (72 * MenuScale)
				
				If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Resume", True, True) Then
					MenuOpen = False
					ResumeSounds()
					StopMouseMovement()
					DeleteMenuGadgets()
					Return
				EndIf
				
				y = y + (75 * MenuScale)
				
				If SelectedDifficulty\SaveType <> NOSAVES Then
					If GameSaved Then
						If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game") Then
							RenderLoading(0, "GAME FILES")
							
							MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0 Then
									CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)] = Max(CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)], 1.0)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)] = MapGrid_Tile
									EndIf
								EndIf
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							
							ResetInput()
							Return
						EndIf
					Else
						UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game", True, False, True)
					EndIf
					y = y + (75 * MenuScale)
				EndIf
				
				If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Achievements") Then 
					mm\AchievementsMenu = 1
					mm\ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
				
				If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Options") Then 
					OptionsMenu = 1
					mm\ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
			Else
				y = y + (104 * MenuScale)
				
				If SelectedDifficulty\SaveType <> NOSAVES Then
					If GameSaved Then
						If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game") Then
							RenderLoading(0, "GAME FILES")
							
							MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(me\Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(me\Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0 Then
									CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)] = Max(CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)], 1.0)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										CurrMapGrid\Found[Floor(EntityX(r\OBJ) / 8.0) + (Floor(EntityZ(r\OBJ) / 8.0) * MapGridSize)] = MapGrid_Tile
									EndIf
								EndIf
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							
							ResetInput()
							Return
						EndIf
					Else
						UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Load Game", True, False, True)
					EndIf
					y = y + (80 * MenuScale)
				EndIf
				If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Quit to Menu") Then
					NullGame()
					CurrSave = ""
					ResetInput()
					Return
				EndIf
				y = y + (80 * MenuScale)
			EndIf
			
			If me\KillTimer >= 0.0 And (Not MainMenuOpen) Then
				If UpdateMainMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, "Quit") Then
					QuitMsg = 1
					mm\ShouldDeleteGadgets = True
				EndIf
			EndIf
		EndIf
	EndIf
	
	CatchErrors("UpdateMenu")
End Function

Function RenderMenu()
	CatchErrors("Uncaught (RenderMenu)")
	
	Local x%, y%, Width%, Height%, i%
	
	If (Not InFocus()) Then ; ~ Game is out of focus then pause the game
		MenuOpen = True
		PauseSounds()
		Delay(1000) ; ~ Reduce the CPU take while game is not in focus
	EndIf
	If MenuOpen Then
		Width = ImageWidth(t\ImageID[0])
		Height = ImageHeight(t\ImageID[0])
		x = mo\Viewport_Center_X - (Width / 2)
		y = mo\Viewport_Center_Y - (Height / 2)
		
		DrawImage(t\ImageID[0], x, y)
		
		Color(255, 255, 255)
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		If mm\AchievementsMenu > 0 Then
			SetFont(fo\FontID[Font_Default_Big])
			Text(x, y - (77 * MenuScale), "ACHIEVEMENTS", False, True)
			SetFont(fo\FontID[Font_Default])
		ElseIf OptionsMenu > 0 Then
			SetFont(fo\FontID[Font_Default_Big])
			Text(x, y - (77 * MenuScale), "OPTIONS", False, True)
			SetFont(fo\FontID[Font_Default])
		ElseIf QuitMsg > 0 Then
			SetFont(fo\FontID[Font_Default_Big])
			Text(x, y - (77 * MenuScale), "QUIT?", False, True)
			SetFont(fo\FontID[Font_Default])
		ElseIf me\KillTimer >= 0.0 Then
			SetFont(fo\FontID[Font_Default_Big])
			Text(x, y - (77 * MenuScale), "PAUSED", False, True)
			SetFont(fo\FontID[Font_Default])
		Else
			SetFont(fo\FontID[Font_Default_Big])
			Text(x, y - (77 * MenuScale), "YOU DIED", False, True)
			SetFont(fo\FontID[Font_Default])
		EndIf		
		
		Local AchvXIMG% = x + (22.0 * MenuScale)
		Local Scale# = opt\GraphicHeight / 768.0
		Local SeparationConst% = 76.0 * Scale
		
		If mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0
			SetFont(fo\FontID[Font_Default])
			Text(x, y, "Difficulty: " + SelectedDifficulty\Name)
			Text(x, y + (20 * MenuScale), "Save: " + CurrSave)
			Text(x, y + (40 * MenuScale), "Map seed: " + RandomSeed)
		ElseIf mm\AchievementsMenu =< 0 And OptionsMenu > 0 And QuitMsg =< 0 And me\KillTimer >= 0.0
			Color(0, 255, 0)
			If OptionsMenu = 1
				Rect(x - (10 * MenuScale), y - (5 * MenuScale), 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 2
				Rect(x + (100 * MenuScale), y - (5 * MenuScale), 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 3
				Rect(x + (210 * MenuScale), y - (5 * MenuScale), 110 * MenuScale, 40 * MenuScale, True)
			ElseIf OptionsMenu = 4
				Rect(x + (320 * MenuScale), y - (5 * MenuScale), 110 * MenuScale, 40 * MenuScale, True)
			EndIf
			
			Local tX# = mo\Viewport_Center_X + (Width / 2)
			Local tY# = y
			Local tW# = 400.0 * MenuScale
			Local tH# = 150.0 * MenuScale
			
			Color(255, 255, 255)
			Select OptionsMenu
				Case 1 ; ~ Graphics
					;[Block]
					SetFont(fo\FontID[Font_Default])
					
					y = y + (50 * MenuScale)
					
					Color(100, 100, 100)
					Text(x, y + (5 * MenuScale), "Enable bump mapping:")	
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_BumpMapping)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "VSync:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VSync)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)), 255 - (155 * (opt\DisplayMode <> 0)))
					Text(x, y + (5 * MenuScale), "Anti-aliasing:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AntiAliasing)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Enable room lights:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_RoomLights)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Screen gamma:")
					If MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ScreenGamma, opt\ScreenGamma)
					EndIf
					
					y = y + (45 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y, "Particle amount:")
					If (MouseOn(x + (270 * MenuScale), y - (9 * MenuScale), 114 * MenuScale, 20) And mm\OnSliderID = 0) Lor mm\OnSliderID = 2
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParticleAmount, opt\ParticleAmount)
					EndIf
					
					y = y + (45 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y, "Texture LOD Bias:")
					If (MouseOn(x + (270 * MenuScale), y - (9 * MenuScale), 114 * MenuScale, 20) And mm\OnSliderID = 0) Lor mm\OnSliderID = 3
						RenderOptionsTooltip(tX, tY, tW, tH + 100 * MenuScale, Tooltip_TextureLODBias)
					EndIf
					
					y = y + (35 * MenuScale)
					
					Color(100, 100, 100)
					Text(x, y + (5 * MenuScale), "Save textures in the VRAM:")	
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SaveTexturesInVRAM)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Field of view:")
					Color(255, 255, 0)
					If MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FOV)
					EndIf
					
					y = y + (45 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y, "Anisotropic filtering:")
					If (MouseOn(x + (270 * MenuScale), y - (9 * MenuScale), 114 * MenuScale, 20) And mm\OnSliderID = 0) Lor mm\OnSliderID = 4
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AnisotropicFiltering)
					EndIf
					;[End Block]
					
					y = y + (35 * MenuScale)
					
					Color(100, 100, 100)
					If opt\Atmosphere Then
						Text(x, y + (5 * MenuScale), "Atmosphere: Bright")
					Else
						Text(x, y + (5 * MenuScale), "Atmosphere: Dark")
					EndIf
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale) And mm\OnSliderID = 0
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Atmosphere)
					EndIf
				Case 2 ; ~ Audio
					;[Block]
					SetFont(fo\FontID[Font_Default])
					
					y = y + (50 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Music volume:")
					If MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MusicVolume, opt\MusicVolume)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Sound volume:")
					If MouseOn(x + (250 * MenuScale), y, 114 * MenuScale, 20)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SoundVolume, opt\PrevSFXVolume)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(100, 100, 100)
					Text(x, y + (5 * MenuScale), "Sound auto-release:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, Tooltip_SoundAutoRelease)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(100, 100, 100)
					Text(x, y + (5 * MenuScale), "Enable user tracks:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracks)
					EndIf
					
					If opt\EnableUserTracks Then
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						Text(x, y + (5 * MenuScale), "User track mode:")
						If opt\UserTrackMode Then
							Text(x + (310 * MenuScale), y + 5 * MenuScale, "Repeat")
						Else
							Text(x + (310 * MenuScale), y + 5 * MenuScale, "Random")
						EndIf
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
							RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracksMode)
						EndIf
						If MouseOn(x + (270 * MenuScale), y + 30 * MenuScale, 210 * MenuScale, 30 * MenuScale)
							RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTrackScan)
						EndIf
					EndIf
					;[End Block]
				Case 3 ; ~ Controls
					;[Block]
					SetFont(fo\FontID[Font_Default])
					y = y + (50 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Mouse sensitivity:")
					If MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSensitivity, opt\MouseSensitivity)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Invert mouse Y-axis:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvert)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Mouse smoothing:")
					If MouseOn(x + (270 * MenuScale), y, 114 * MenuScale, 20)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSmoothing, opt\MouseSmoothing)
					EndIf
					
					y = y + (40 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Control configuration:")
					
					y = y + (30 * MenuScale)
					
					Text(x, y + (5 * MenuScale), "Move Forward:")
					
					Text(x, y + (25 * MenuScale), "Strafe Left:")
					
					Text(x, y + (45 * MenuScale), "Move Backward:")
					
					Text(x, y + (65 * MenuScale), "Strafe Right:")
					
					Text(x, y + (85 * MenuScale), "Sprint:")
					
					Text(x, y + (105 * MenuScale), "Crouch:")
					
					Text(x, y + (125 * MenuScale), "Manual Blink:")
					
					Text(x, y + (145 * MenuScale), "Inventory:")
					
					Text(x, y + (165 * MenuScale), "Quick Save:")
					
					If opt\CanOpenConsole Then Text(x, y + (185 * MenuScale), "Console:")
					
					Text(x, y + (205 * MenuScale), "Take Screenshot:")
					
					If MouseOn(x, y, 310 * MenuScale, 220 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ControlConfiguration)
					EndIf
					;[End Block]
				Case 4 ; ~ Advanced
					;[Block]
					SetFont(fo\FontID[Font_Default])
					
					y = y + (50 * MenuScale)
					
					Color(255, 255, 255)			
					Text(x, y + (5 * MenuScale), "Show HUD:")	
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HUD)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Enable console:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Console)
					EndIf
					
					y = y + (30 * MenuScale)
					
					If opt\CanOpenConsole Then
						Color(255, 255, 255)
						Text(x, y + (5 * MenuScale), "Open console on error:")
						If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
							RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ConsoleOnError)
						EndIf
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Achievement popups:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AchievementPopups)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255 - (155 * SelectedDifficulty\SaveType <> SAVEANYWHERE), 255 - (155 * SelectedDifficulty\SaveType <> SAVEANYWHERE), 255 - (155 * SelectedDifficulty\SaveType <> SAVEANYWHERE))
					Text(x, y + (5 * MenuScale), "Enable auto save:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AutoSave)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Show FPS:")
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FPS)
					EndIf
					
					y = y + (30 * MenuScale)
					
					Color(255, 255, 255)
					Text(x, y + (5 * MenuScale), "Frame limit:")
					Color(255, 255, 255)
					If opt\CurrFrameLimit > 0.0 Then
						Color(255, 255, 0)
						Text(x, y + (45 * MenuScale), opt\FrameLimit + " FPS")
					EndIf
					If MouseOn(x + (270 * MenuScale), y, 20 * MenuScale, 20 * MenuScale)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
					EndIf
					If MouseOn(x + (150 * MenuScale), y + (40 * MenuScale), 114 * MenuScale, 20)
						RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
					EndIf
					;[End Block]
			End Select
		ElseIf mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg > 0 And me\KillTimer >= 0.0
			; ~ Just save this line, ok?
		Else
			If mm\AchievementsMenu > 0 Then
				For i = 0 To 11
					If i + ((mm\AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG, y + ((i / 4) * 120 * MenuScale), i + ((mm\AchievementsMenu - 1) * 12))
					Else
						Exit
					EndIf
				Next
				For i = 0 To 11
					If i + ((mm\AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG + ((i Mod 4) * SeparationConst), y + ((i / 4) * 120 * MenuScale), 64 * Scale, 64 * Scale) Then
							AchievementTooltip(i + ((mm\AchievementsMenu - 1) * 12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
			EndIf
		EndIf
		
		y = y + (10 * MenuScale)
		
		If mm\AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0 Then
			If me\KillTimer >= 0.0 Then	
				y = y + (297 * MenuScale)
				If SelectedDifficulty\SaveType <> NOSAVES Then
					y = y + (75 * MenuScale)
				EndIf
			Else
				y = y + (184 * MenuScale)
				If SelectedDifficulty\SaveType <> NOSAVES Then
					y = y + (80 * MenuScale)
				EndIf
			EndIf
			If me\KillTimer < 0.0 Then
				SetFont(fo\FontID[Font_Default])
				RowText(msg\DeathMsg, x, y, 430 * MenuScale, 600 * MenuScale)
			EndIf
		EndIf
		
		RenderMenuButtons()
		RenderMenuTicks()
		RenderMenuInputBoxes()
		RenderMenuSlideBars()
		RenderMenuSliders()
		
		If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	EndIf
	
	SetFont(fo\FontID[Font_Default])
	
	CatchErrors("RenderMenu")
End Function

; ~ Ending IDs Constants
;[Block]
Const Ending_A1% = 0
Const Ending_A2% = 1
Const Ending_B1% = 2
Const Ending_B2% = 3
;[End Block]

Function UpdateEnding()
	Local x%, y%, Width%, Height%, i%
	
	fps\Factor[0] = 0.0
	If me\EndingTimer > -2000.0 Then
		me\EndingTimer = Max(me\EndingTimer - fps\Factor[1], -1111.0)
	Else
		me\EndingTimer = me\EndingTimer - fps\Factor[1]
	EndIf
	
	GiveAchievement(Achv055)
	If ((Not UsedConsole) Lor opt\DebugMode) And SelectedMap = "" Then
		GiveAchievement(AchvConsole)
		If SelectedDifficulty\Name = "Keter" Then
			GiveAchievement(AchvKeter)
			SaveAchievementsFile()
		EndIf
	EndIf
	
	ShouldPlay = 66
	
	If me\EndingTimer < -200.0 Then
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN) : me\Stamina = 100.0
		EndIf
		
		If BreathGasRelaxedCHN <> 0 Then
			If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
		EndIf
		
		If (Not me\EndingScreen) Then
			me\EndingScreen = LoadImage_Strict("GFX\menu\ending_screen.png")
			
			ShouldPlay = 23
			opt\CurrMusicVolume = opt\MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music[23] + ".ogg", opt\CurrMusicVolume, 0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict(LightSFX)
		EndIf
		
		If me\EndingTimer > -700.0 Then 
			If me\EndingTimer + fps\Factor[1] > -450.0 And me\EndingTimer =< -450.0 Then
				PlaySound_Strict(LoadTempSound("SFX\Ending\Ending" + (me\SelectedEnding + 1) + ".ogg"))
			EndIf			
		Else
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0 Then
				Width = ImageWidth(t\ImageID[0])
				Height = ImageHeight(t\ImageID[0])
				x = mo\Viewport_Center_X - (Width / 2)
				y = mo\Viewport_Center_Y - (Height / 2)
				
				If mm\AchievementsMenu = 0 Then 
					x = x + (132 * MenuScale)
					y = y + (122 * MenuScale)
					
					x = mo\Viewport_Center_X - (Width / 2)
					y = mo\Viewport_Center_Y - (Height / 2)
					x = x + (Width / 2)
					y = y + Height - (100 * MenuScale)
					
					If UpdateMainMenuButton(x - (170 * MenuScale), y - (200 * MenuScale), 430 * MenuScale, 60 * MenuScale, "ACHIEVEMENTS", True) Then
						mm\AchievementsMenu = 1
						mm\ShouldDeleteGadgets = True
					EndIf
					
					If UpdateMainMenuButton(x - (170 * MenuScale), y - (100 * MenuScale), 430 * MenuScale, 60 * MenuScale, "MAIN MENU", True)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i = 0 To 9
							If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
						SetStreamVolume_Strict(MusicCHN, 1.0 * opt\MusicVolume)
						me\EndingTimer = -2000.0
						mm\ShouldDeleteGadgets = True
						ResetInput()
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

Function RenderEnding()
	ShowPointer()
	
	Local itt.ItemTemplates, r.Rooms
	Local x%, y%, Width%, Height%, i%
	
	Select me\SelectedEnding
		Case Ending_A1, Ending_B2
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
				DrawImage(me\EndingScreen, mo\Viewport_Center_X - 400, mo\Viewport_Center_Y - 400)
			Else
				Color(0, 0, 0)
				Rect(100, 100, opt\GraphicWidth - 200, opt\GraphicHeight - 200)
				Color(255, 255, 255)
			EndIf
		Else
			DrawImage(me\EndingScreen, mo\Viewport_Center_X - 400, mo\Viewport_Center_Y - 400)
			
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0 Then
				Width = ImageWidth(t\ImageID[0])
				Height = ImageHeight(t\ImageID[0])
				x = mo\Viewport_Center_X - (Width / 2)
				y = mo\Viewport_Center_Y - (Height / 2)
				
				DrawImage(t\ImageID[0], x, y)
				
				Color(255, 255, 255)
				SetFont(fo\FontID[Font_Default_Big])
				Text(x + (Width / 2) + (40 * MenuScale), y + (20 * MenuScale), "THE END", True)
				SetFont(fo\FontID[Font_Default])
				
				If mm\AchievementsMenu = 0 Then 
					x = x + (132 * MenuScale)
					y = y + (122 * MenuScale)
					
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
					
					For i = 0 To 30
						SCPsEncountered = SCPsEncountered + achv\Achievement[i]
					Next
					
					Local AchievementsUnlocked% = 0
					
					For i = 0 To MAXACHIEVEMENTS - 1
						AchievementsUnlocked = AchievementsUnlocked + achv\Achievement[i]
					Next
					
					Text(x, y, "SCPs encountered: " + SCPsEncountered)
					Text(x, y + (20 * MenuScale), "Achievements unlocked: " + AchievementsUnlocked + "/" + (MAXACHIEVEMENTS))
					Text(x, y + (40 * MenuScale), "Rooms found: " + RoomsFound + "/" + RoomAmount)
					Text(x, y + (60 * MenuScale), "Documents discovered: " + DocsFound + "/" + DocAmount)
					Text(x, y + (80 * MenuScale), "Items refined in SCP-914: " + me\RefinedItems)
				Else
					RenderMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer =< -2000.0
				RenderCredits()
			EndIf
		EndIf
	EndIf
	
	RenderMenuButtons()
	
	If opt\DisplayMode = 0 Then DrawImage(CursorIMG), ScaledMouseX(), ScaledMouseY()
	
	SetFont(fo\FontID[Font_Default])
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
	
	fo\FontID[Font_Credits] = LoadFont_Strict("GFX\fonts\cour\Courier New.ttf", 21)
	fo\FontID[Font_Credits_Big] = LoadFont_Strict("GFX\fonts\cour\Courier New.ttf", 35)
	
	If (Not me\CreditsScreen) Then me\CreditsScreen = LoadImage_Strict("GFX\menu\credits_screen.png")
	
	InitLoadingTextColor()
	
	Repeat
		l = ReadLine(File)
		cl.CreditsLine = New CreditsLine
		cl\Txt = l
	Until Eof(File)
	
	Delete First CreditsLine
	me\CreditsTimer = 0.0
End Function

Function UpdateCredits()
	Local cl.CreditsLine, LastCreditLine.CreditsLine, ltc.LoadingTextColor
	Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (opt\GraphicHeight + 10.0)
	Local ID%
	Local EndLinesAmount%
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "/" Then LastCreditLine = Before(cl)
		If LastCreditLine <> Null Then
			If cl\ID > LastCreditLine\ID Then cl\Stay = True
		EndIf
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		me\CreditsTimer = me\CreditsTimer + (0.5 * fps\Factor[1])
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			; ~ Just save this line, ok?
		ElseIf me\CreditsTimer >= 255.0
			If me\CreditsTimer > 500.0 Then me\CreditsTimer = -255.0
		Else
			If me\CreditsTimer >= -1.0 Then me\CreditsTimer = -1.0
		EndIf
	EndIf
	
	If GetKey() <> 0 Then me\CreditsTimer = -1.0
	
	If me\CreditsTimer = -1.0 Then
		DeInitLoadingTextColor(ltc)
		Delete Each CreditsLine
		NullGame(False)
		StopStream_Strict(MusicCHN)
		ShouldPlay = 21
		CurrSave = ""
		ResetInput()
	EndIf
End Function

Function RenderCredits()
	Local cl.CreditsLine, LastCreditLine.CreditsLine
	Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (opt\GraphicHeight + 10.0)
	Local ID%
	Local EndLinesAmount%
	
	Cls()
	
	If Rand(1, 300) > 1 Then
		DrawImage(me\CreditsScreen, mo\Viewport_Center_X - 400, mo\Viewport_Center_Y - 400)
	EndIf
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	Color(255, 255, 255)
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "*"
			SetFont(fo\FontID[Font_Credits_Big])
			If (Not cl\Stay) Then Text(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
		ElseIf Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		Else
			SetFont(fo\FontID[Font_Credits])
			If (Not cl\Stay) Then Text(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), cl\Txt, True)
		EndIf
		If LastCreditLine <> Null Then
			If cl\ID > LastCreditLine\ID Then cl\Stay = True
		EndIf
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
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
			If cl\Stay Then
				SetFont(fo\FontID[Font_Credits])
				If Left(cl\Txt, 1) = "/" Then
					Text(mo\Viewport_Center_X, mo\Viewport_Center_Y + (EndLinesAmount / 2) + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
				Else
					Text(mo\Viewport_Center_X, mo\Viewport_Center_Y + (24 * (cl\ID - LastCreditLine\ID) * MenuScale) - ((EndLinesAmount / 2) * 24 * MenuScale), cl\Txt, True)
				EndIf
			EndIf
		Next
	EndIf
	
	RenderLoadingText(20 * MenuScale, opt\GraphicHeight - (35 * MenuScale))
	
	If me\CreditsTimer = -1.0 Then
		FreeFont(fo\FontID[Font_Credits])
		FreeFont(fo\FontID[Font_Credits_Big])
		If me\CreditsScreen <> 0 Then
			FreeImage(me\CreditsScreen) : me\CreditsScreen = 0
		EndIf
		If me\EndingScreen <> 0 Then
			FreeImage(me\EndingScreen) : me\EndingScreen = 0
		EndIf
	EndIf
End Function

Function LoadEntities()
	CatchErrors("Uncaught (LoadEntities)")
	
	Local i%, Tex%
	Local b%, t1%, SF%
	Local Name$, Test%, File$
	
	RenderLoading(0, "PLAYER")
	
	DeInitMainMenuAssets()
	
	SoundEmitter = CreatePivot()
	
	me\Collider = CreatePivot()
	EntityRadius(me\Collider, 0.15, 0.30)
	EntityPickMode(me\Collider, 1)
	EntityType(me\Collider, HIT_PLAYER)
	
	me\Head = CreatePivot()
	EntityRadius(me\Head, 0.15)
	EntityType(me\Head, HIT_PLAYER)
	
	RenderLoading(5, "CAMERA")
	
	Camera = CreateCamera()
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	CameraRange(Camera, 0.01, opt\CameraFogFar)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, opt\CameraFogNear, opt\CameraFogFar)
	
	RenderLoading(10, "ICONS")
	
	t\IconID[0] = LoadImage_Strict("GFX\walk_icon.png")
	t\IconID[0] = ScaleImage2(t\IconID[0], MenuScale, MenuScale)
	t\IconID[1] = LoadImage_Strict("GFX\sprint_icon.png")
	t\IconID[1] = ScaleImage2(t\IconID[1], MenuScale, MenuScale)
	t\IconID[2] = LoadImage_Strict("GFX\crouch_icon.png")
	t\IconID[2] = ScaleImage2(t\IconID[2], MenuScale, MenuScale)
	For i = 3 To 4
		t\IconID[i] = LoadImage_Strict("GFX\blink_icon(" + (i - 2) + ").png")
		t\IconID[i] = ScaleImage2(t\IconID[i], MenuScale, MenuScale)
	Next
	For i = 5 To 6
		t\IconID[i] = LoadImage_Strict("GFX\hand_symbol(" + (i - 4) + ").png")
		t\IconID[i] = ScaleImage2(t\IconID[i], MenuScale, MenuScale)
	Next
	
	QuickLoadIcon = LoadImage_Strict("GFX\menu\QuickLoading.png")
	QuickLoadIcon = ScaleImage2(QuickLoadIcon, MenuScale, MenuScale)
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Local Loc2% = GetINISectionLocation(AchievementsFile, "a" + Str(i))
		
		achv\AchievementStrings[i] = GetINIString2(AchievementsFile, Loc2, "AchvName")
		achv\AchievementDescs[i] = GetINIString2(AchievementsFile, Loc2, "AchvDesc")
		
		Local Image$ = GetINIString2(AchievementsFile, Loc2, "AchvImage") 
		
		achv\AchvIMG[i] = LoadImage_Strict("GFX\menu\achievements\" + Image + ".png")
		achv\AchvIMG[i] = ScaleImage2(achv\AchvIMG[i], opt\GraphicHeight / 768.0, opt\GraphicHeight / 768.0)
		BufferDirty(ImageBuffer(achv\AchvIMG[i]))
	Next
	
	achv\AchvLocked = LoadImage_Strict("GFX\menu\achievements\AchvLocked.png")
	achv\AchvLocked = ScaleImage2(achv\AchvLocked, opt\GraphicHeight / 768.0, opt\GraphicHeight / 768.0)
	BufferDirty(ImageBuffer(achv\AchvLocked))
	
	RenderLoading(15, "IMAGES")
	
	t\ImageID[0] = LoadImage_Strict("GFX\menu\pause_menu.png")
	t\ImageID[0] = ScaleImage2(t\ImageID[0], MenuScale, MenuScale)
	MaskImage(t\ImageID[0], 255, 255, 0)
	
	If (Not opt\SmoothHUD) Then
		t\ImageID[1] = LoadImage_Strict("GFX\blink_meter(2).png")
		t\ImageID[1] = ScaleImage2(t\ImageID[1], MenuScale, MenuScale)
		
		For i = 2 To 3
			t\ImageID[i] = LoadImage_Strict("GFX\stamina_meter(" + (i - 1) + ").png")
			t\ImageID[i] = ScaleImage2(t\ImageID[i], MenuScale, MenuScale)
		Next
	EndIf
	
	t\ImageID[4] = LoadImage_Strict("GFX\keypad_HUD.png")
	t\ImageID[4] = ScaleImage2(t\ImageID[4], MenuScale, MenuScale)
	MaskImage(t\ImageID[4], 255, 0, 255)
	
	t\ImageID[5] = LoadImage_Strict("GFX\scp_294_panel.png")
	t\ImageID[5] = ScaleImage2(t\ImageID[5], MenuScale, MenuScale)
	MaskImage(t\ImageID[5], 255, 0, 255)
	
	t\ImageID[6] = LoadImage_Strict("GFX\night_vision_goggles_battery.png")
	t\ImageID[6] = ScaleImage2(t\ImageID[6], MenuScale, MenuScale)
	MaskImage(t\ImageID[6], 255, 0, 255)
	
	t\ImageID[7] = CreateImage(opt\GraphicWidth, opt\GraphicHeight)
	
	RenderLoading(20, "TEXTURES")
	
	LoadMissingTexture()
	
	AmbientLightRoomTex = CreateTextureUsingCacheSystem(2, 2)
	TextureBlend(AmbientLightRoomTex, 3)
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	AmbientLightRoomVal = 255
	
	ScreenTexs[0] = CreateTextureUsingCacheSystem(512, 512, 1)
	ScreenTexs[1] = CreateTextureUsingCacheSystem(512, 512, 1)
	
	CreateBlurImage()
	CameraProjMode(ArkBlurCam, 0)
	
	t\OverlayTextureID[0] = LoadTexture_Strict("GFX\fog.png", 1, DeleteAllTextures) ; ~ FOG
	t\OverlayID[0] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[0], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[0], t\OverlayTextureID[0])
	EntityBlend(t\OverlayID[0], 2)
	EntityOrder(t\OverlayID[0], -1000)
	MoveEntity(t\OverlayID[0], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[1] = LoadTexture_Strict("GFX\gas_mask_overlay.png", 1, DeleteAllTextures) ; ~ GAS MASK
	t\OverlayID[1] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[1], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[1], t\OverlayTextureID[1])
	EntityBlend(t\OverlayID[1], 2)
	EntityFX(t\OverlayID[1], 1)
	EntityOrder(t\OverlayID[1], -1003)
	MoveEntity(t\OverlayID[1], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[2] = LoadTexture_Strict("GFX\hazmat_suit_overlay.png", 1, DeleteAllTextures) ; ~ HAZMAT SUIT
	t\OverlayID[2] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[2], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[2], t\OverlayTextureID[2])
	EntityBlend(t\OverlayID[2], 2)
	EntityFX(t\OverlayID[2], 1)
	EntityOrder(t\OverlayID[2], -1003)
	MoveEntity(t\OverlayID[2], 0, 0, 1.0)
	
	t\OverlayTextureID[3] = LoadTexture_Strict("GFX\scp_008_overlay.png", 1, DeleteAllTextures) ; ~ SCP-008
	t\OverlayID[3] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[3], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[3], t\OverlayTextureID[3])
	EntityBlend(t\OverlayID[3], 3)
	EntityFX(t\OverlayID[3], 1)
	EntityOrder(t\OverlayID[3], -1003)
	MoveEntity(t\OverlayID[3], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[4] = LoadTexture_Strict("GFX\night_vision_goggles_overlay.png", 1, DeleteAllTextures) ; ~ NIGHT VISION GOGGLES
	t\OverlayID[4] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[4], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[4], t\OverlayTextureID[4])
	EntityBlend(t\OverlayID[4], 2)
	EntityFX(t\OverlayID[4], 1)
	EntityOrder(t\OverlayID[4], -1003)
	MoveEntity(t\OverlayID[4], 0.0, 0.0, 1.0)
	
	For i = 1 To 4
		HideEntity(t\OverlayID[i])
	Next
	
	t\OverlayTextureID[5] = CreateTextureUsingCacheSystem(1024, 1024, 1 + 2) ; ~ DARK
	SetBuffer(TextureBuffer(t\OverlayTextureID[5]))
	Cls()
	SetBuffer(BackBuffer())
	t\OverlayID[5] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[5], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[5], t\OverlayTextureID[5])
	EntityBlend(t\OverlayID[5], 1)
	EntityOrder(t\OverlayID[5], -1002)
	MoveEntity(t\OverlayID[5], 0.0, 0.0, 1.0)
	EntityAlpha(t\OverlayID[5], 0.0)
	
	t\OverlayTextureID[6] = CreateTextureUsingCacheSystem(1024, 1024, 1 + 2) ; ~ LIGHT
	SetBuffer(TextureBuffer(t\OverlayTextureID[6]))
	ClsColor(255, 255, 255)
	Cls()
	ClsColor(0, 0, 0)
	SetBuffer(BackBuffer())
	t\OverlayID[6] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[6], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[6], t\OverlayTextureID[6])
	EntityBlend(t\OverlayID[6], 1)
	EntityOrder(t\OverlayID[6], -1002)
	MoveEntity(t\OverlayID[6], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[7] = LoadTexture_Strict("GFX\scp_409_overlay.png", 1, DeleteAllTextures) ; ~ SCP-409
	t\OverlayID[7] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[7], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[7], t\OverlayTextureID[7])
	EntityBlend(t\OverlayID[7], 3)
	EntityFX(t\OverlayID[7], 1)
	EntityOrder(t\OverlayID[7], -1001)
	MoveEntity(t\OverlayID[7], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[8] = LoadTexture_Strict("GFX\helmet_overlay.png", 1, DeleteAllTextures) ; ~ HELMET
	t\OverlayID[8] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[8], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[8], t\OverlayTextureID[8])
	EntityBlend(t\OverlayID[8], 2)
	EntityFX(t\OverlayID[8], 1)
	EntityOrder(t\OverlayID[8], -1003)
	MoveEntity(t\OverlayID[8], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[9] = LoadTexture_Strict("GFX\bloody_overlay.png", 1, DeleteAllTextures) ; ~ BLOOD
	t\OverlayID[9] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[9], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[9], t\OverlayTextureID[9])
	EntityBlend(t\OverlayID[9], 2)
	EntityFX(t\OverlayID[9], 1)
	EntityOrder(t\OverlayID[9], -1003)
	MoveEntity(t\OverlayID[9], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[10] = LoadTexture_Strict("GFX\fog_gas_mask.png", 1, DeleteAllTextures) ; ~ FOG IN GAS MASK
	t\OverlayID[10] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[10], 1.0, Float(opt\GraphicHeight) / Float(opt\GraphicWidth))
	EntityTexture(t\OverlayID[10], t\OverlayTextureID[10])
	EntityBlend(t\OverlayID[10], 3)
	EntityFX(t\OverlayID[10], 1)
	EntityOrder(t\OverlayID[10], -1000)
	MoveEntity(t\OverlayID[10], 0.0, 0.0, 1.0)
	
	For i = 7 To 10
		HideEntity(t\OverlayID[i])
	Next
	
	For i = 0 To 1
		t\LightSpriteID[i] = LoadTexture_Strict("GFX\light(" + (i + 1) + ").png", 1, DeleteAllTextures)
	Next
	t\LightSpriteID[2] = LoadTexture_Strict("GFX\light_sprite.png", 1, DeleteAllTextures)
	
	For i = 0 To 7
		t\DecalTextureID[i] = LoadTexture_Strict("GFX\decal(" + (i + 1) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	For i = 8 To 13
		t\DecalTextureID[i] = LoadTexture_Strict("GFX\decal_pd(" + (i - 7) + ").png", 1 + 2, DeleteAllTextures)	
	Next
	
	For i = 14 To 15
		t\DecalTextureID[i] = LoadTexture_Strict("GFX\bullet_hole(" + (i - 13) + ").png", 1 + 2, DeleteAllTextures)	
	Next
	
	For i = 16 To 17
		t\DecalTextureID[i] = LoadTexture_Strict("GFX\blood_drop(" + (i - 15) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	t\DecalTextureID[18] = LoadTexture_Strict("GFX\decal_scp_427.png", 1 + 2, DeleteAllTextures)
	
	t\DecalTextureID[19] = LoadTexture_Strict("GFX\decal_scp_409.png", 1 + 2, DeleteAllTextures)
	
	t\DecalTextureID[20] = LoadTexture_Strict("GFX\decal_water.png", 1 + 2, DeleteAllTextures)
	
	t\MonitorTextureID[0] = LoadTexture_Strict("GFX\monitor_overlay.png", 1, DeleteAllTextures)
	For i = 1 To 3
		t\MonitorTextureID[i] = LoadTexture_Strict("GFX\map\textures\lockdown_screen(" + i + ").png", 1, DeleteAllTextures)
	Next
	t\MonitorTextureID[4] = CreateTextureUsingCacheSystem(1, 1)
	SetBuffer(TextureBuffer(t\MonitorTextureID[4]))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	
	t\ParticleTextureID[0] = LoadTexture_Strict("GFX\smoke(1).png", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[1] = LoadAnimTexture_Strict("GFX\smoke(2).png", 1 + 2, 256, 256, 0, 4, DeleteAllTextures)
	t\ParticleTextureID[2] = LoadTexture_Strict("GFX\flash.png", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[3] = LoadTexture_Strict("GFX\dust.png", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[4] = LoadTexture_Strict("GFX\npcs\hg.pt", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[5] = LoadTexture_Strict("GFX\map\textures\sun.png", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[6] = LoadTexture_Strict("GFX\blood_sprite.png", 1 + 2, DeleteAllTextures)
	t\ParticleTextureID[7] = LoadTexture_Strict("GFX\spark.png", 1 + 2, DeleteAllTextures)
	
	For i = 0 To 6
		t\MiscTextureID[i] = LoadTexture_Strict("GFX\scp_079_overlay(" + (i + 1) + ").png", 1, DeleteAllTextures)
	Next
	
	For i = 7 To 12
		t\MiscTextureID[i] = LoadTexture_Strict("GFX\scp_895_overlay(" + (i - 6) + ").png", 1, DeleteAllTextures)
	Next
	
	t\MiscTextureID[13] = LoadTexture_Strict("GFX\tesla_overlay.png", 1 + 2, DeleteAllTextures)
	
	t\MiscTextureID[16] = LoadTexture_Strict("GFX\map\textures\keypad.jpg", 1, DeleteAllTextures)
	t\MiscTextureID[17] = LoadTexture_Strict("GFX\map\textures\keypad_locked.png", 1, DeleteAllTextures)
	
	For i = 18 To 19
		t\MiscTextureID[i] = LoadTexture_Strict("GFX\map\textures\camera(" + (i - 17) + ").png", 1, DeleteAllTextures)
	Next
	
	t\MiscTextureID[20] = LoadTexture_Strict("GFX\fog_night_vision_goggles.png", 1, DeleteAllTextures) ; ~ FOG IN NIGHT VISION GOGGLES
	
	For i = 21 To 23
		t\MiscTextureID[i] = LoadTexture_Strict("GFX\map\textures\elevator_panel_" + (i - 20) + ".png", 1, DeleteAllTextures) ; ~ Elevator arrows
	Next
	
	t\NPCTextureID[0] = LoadTexture_Strict("GFX\npcs\Gonzales.png", 1, DeleteAllTextures) ; ~ Gonzales
	
	t\NPCTextureID[1] = LoadTexture_Strict("GFX\npcs\D_9341(2).png", 1, DeleteAllTextures) ; ~ SCP-970's corpse
	
	t\NPCTextureID[2] = LoadTexture_Strict("GFX\npcs\scientist.png", 1, DeleteAllTextures) ; ~ Scientist
	
	t\NPCTextureID[3] = LoadTexture_Strict("GFX\npcs\Franklin.png", 1, DeleteAllTextures) ; ~ Franklin
	
	t\NPCTextureID[4] = LoadTexture_Strict("GFX\npcs\janitor.png", 1, DeleteAllTextures) ; ~ Janitor # 1
	
	t\NPCTextureID[5] = LoadTexture_Strict("GFX\npcs\Maynard.png", 1, DeleteAllTextures) ; ~ Maynard
	
	t\NPCTextureID[6] = LoadTexture_Strict("GFX\npcs\class_d(2).png", 1, DeleteAllTextures) ; ~ Afro-American Class-D
	
	If opt\IntroEnabled Then t\NPCTextureID[7] = LoadTexture_Strict("GFX\npcs\D_9341.png", 1, DeleteAllTextures) ; ~ D-9341
	
	t\NPCTextureID[8] = LoadTexture_Strict("GFX\npcs\body.png", 1, DeleteAllTextures) ; ~ Body # 1
	
	t\NPCTextureID[9] = LoadTexture_Strict("GFX\npcs\body(2).png", 1, DeleteAllTextures) ; ~ Body # 2
	
	t\NPCTextureID[10] = LoadTexture_Strict("GFX\npcs\janitor(2).png", 1, DeleteAllTextures) ; ~ Janitor # 2
	
	t\NPCTextureID[11] = LoadTexture_Strict("GFX\npcs\scp_008_1_victim.png", 1, DeleteAllTextures) ; ~ SCP-008-1's victim
	
	t\NPCTextureID[12] = LoadTexture_Strict("GFX\npcs\body(3).png", 1, DeleteAllTextures) ; ~ SCP-409's victim
	
	t\NPCTextureID[13] = LoadTexture_Strict("GFX\npcs\scp_939_victim.png", 1, DeleteAllTextures) ; ~ SCP-939's victim # 2
	
	t\NPCTextureID[14] = LoadTexture_Strict("GFX\npcs\scp_939_victim(2).png", 1, DeleteAllTextures) ; ~ SCP-939's victim # 1
	
	t\NPCTextureID[15] = LoadTexture_Strict("GFX\npcs\scp_035_victim.png", 1, DeleteAllTextures) ; ~ SCP-035's victim
	
	t\NPCTextureID[16] = LoadTexture_Strict("GFX\npcs\scp_096_bloody.png", 1, DeleteAllTextures) ; ~ SCP-096 bloody texture
	
	LoadMaterials(MaterialsFile)
	
	RenderLoading(25, "MODELS")
	
	; ~ [NPCs]
	
	o\NPCModelID[NPCType008_1] = LoadAnimMesh_Strict("GFX\npcs\scp_008_1.b3d") ; ~ SCP-008-1
	
	o\NPCModelID[NPCType035_Tentacle] = LoadAnimMesh_Strict("GFX\npcs\scp_035_tentacle.b3d") ; ~ SCP-035's Tentacle
	
	o\NPCModelID[NPCType049] = LoadAnimMesh_Strict("GFX\npcs\scp_049.b3d") ; ~ SCP-049
	
	o\NPCModelID[NPCType049_2] = LoadAnimMesh_Strict("GFX\npcs\scp_049_2.b3d") ; ~ SCP-049-2
	
	o\NPCModelID[NPCType066] = LoadAnimMesh_Strict("GFX\npcs\scp_066.b3d") ; ~ SCP-066
	
	o\NPCModelID[NPCType096] = LoadAnimMesh_Strict("GFX\npcs\scp_096.b3d") ; ~ SCP-096
	
	o\NPCModelID[NPCType106] = LoadAnimMesh_Strict("GFX\npcs\scp_106.b3d") ; ~ SCP-106
	
	o\NPCModelID[NPCType173] = LoadMesh_Strict("GFX\npcs\scp_173_body.b3d") ; ~ SCP-173's Body
	
	o\NPCModelID[NPCType173_Head] = LoadMesh_Strict("GFX\npcs\scp_173_head.b3d") ; ~ SCP-173's Head
	
	o\NPCModelID[NPCType173_Box] = LoadMesh_Strict("GFX\npcs\scp_173_box.b3d") ; ~ SCP-173's Box
	
	o\NPCModelID[NPCType205_Demon] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon.b3d") ; ~ SCP-205's Demon #1
	
	o\NPCModelID[NPCType205_Demon2] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon(2).b3d") ; ~ SCP-205's Demon #2
	
	o\NPCModelID[NPCType205_Demon3] = LoadAnimMesh_Strict("GFX\npcs\scp_205_demon(3).b3d") ; ~ SCP-205's Demon #3
	
	o\NPCModelID[NPCType205_Woman] = LoadAnimMesh_Strict("GFX\npcs\scp_205_woman.b3d") ; ~ SCP-205's Woman
	
	o\NPCModelID[NPCType372] = LoadAnimMesh_Strict("GFX\npcs\scp_372.b3d") ; ~ SCP-372
	
	o\NPCModelID[NPCType513_1] = LoadAnimMesh_Strict("GFX\npcs\scp_513_1.b3d") ; ~ SCP-513-1
	
	o\NPCModelID[NPCType682_Arm] = LoadMesh_Strict("GFX\npcs\scp_682_arm.b3d") ; ~ SCP-682's Arm
	
	o\NPCModelID[NPCType860_2] = LoadAnimMesh_Strict("GFX\npcs\scp_860_2.b3d") ; ~ SCP-860-2
	
	o\NPCModelID[NPCType939] = LoadAnimMesh_Strict("GFX\npcs\scp_939.b3d") ; ~ SCP-939
	
	o\NPCModelID[NPCType966] = LoadAnimMesh_Strict("GFX\npcs\scp_966.b3d") ; ~ SCP-966
	
	o\NPCModelID[NPCType1048] = LoadAnimMesh_Strict("GFX\npcs\scp_1048.b3d") ; ~ SCP-1048
	
	o\NPCModelID[NPCType1048_A] = LoadAnimMesh_Strict("GFX\npcs\scp_1048_a.b3d") ; ~ SCP-1048-A
	
	o\NPCModelID[NPCType1499_1] = LoadAnimMesh_Strict("GFX\npcs\scp_1499_1.b3d") ; ~ SCP-1499-1
	
	o\NPCModelID[NPCTypeApache] = LoadAnimMesh_Strict("GFX\npcs\apache.b3d") ; ~ Apache Helicopter
	
	o\NPCModelID[NPCTypeApache_Rotor] = LoadAnimMesh_Strict("GFX\npcs\apache_rotor.b3d") ; ~ Helicopter's Rotor #1
	
	o\NPCModelID[NPCTypeApache_Rotor2] = LoadAnimMesh_Strict("GFX\npcs\apache_rotor(2).b3d") ; ~ Helicopter's Rotor #2
	
	o\NPCModelID[NPCTypeVehicle] = LoadAnimMesh_Strict("GFX\npcs\vehicle.b3d") ; ~ Vehicle
	
	o\NPCModelID[NPCTypeCI] = LoadAnimMesh_Strict("GFX\npcs\CI.b3d") ; ~ CI
	
	o\NPCModelID[NPCTypeClerk] = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d") ; ~ Clerk
	
	o\NPCModelID[NPCTypeD] = LoadAnimMesh_Strict("GFX\npcs\class_d.b3d") ; ~ Class-D
	
	o\NPCModelID[NPCTypeDuck] = LoadAnimMesh_Strict("GFX\npcs\duck.b3d") ; ~ Anomalous Duck
	
	o\NPCModelID[NPCTypeGuard] = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ; ~ Guard
	
	o\NPCModelID[NPCTypeMTF] = LoadAnimMesh_Strict("GFX\npcs\MTF.b3d") ; ~ MTF
	
	o\NPCModelID[NPCTypeNazi] = LoadAnimMesh_Strict("GFX\npcs\nazi_officer.b3d") ; ~ Nazi Officer
	
	For i = 0 To MaxNPCModelIDAmount - 1
		HideEntity(o\NPCModelID[i])
	Next
	
	; ~ [BUTTONS]
	
	o\ButtonModelID[0] = LoadMesh_Strict("GFX\map\Props\Button.b3d") ; ~ Button
	
	o\ButtonModelID[1] = LoadMesh_Strict("GFX\map\Props\ButtonKeycard.b3d") ; ~ Keycard Button
	
	o\ButtonModelID[2] = LoadMesh_Strict("GFX\map\Props\ButtonCode.b3d") ; ~ Code Button
	
	o\ButtonModelID[3] = LoadMesh_Strict("GFX\map\Props\ButtonScanner.b3d") ; ~ Scanner Button
	
	o\ButtonModelID[4] = LoadMesh_Strict("GFX\map\Props\ButtonElevator.b3d") ; ~ Elevator Button
	
	For i = 0 To MaxButtonModelIDAmount - 1
		HideEntity(o\ButtonModelID[i])
	Next	
	
	; ~ [DOORS]
	
	o\DoorModelID[Default_Door] = LoadMesh_Strict("GFX\map\Props\Door01.x") ; ~ Default Door
	
	o\DoorModelID[Elevator_Door] = LoadMesh_Strict("GFX\map\Props\ElevatorDoor.b3d") ; ~ Elevator Door
	
	o\DoorModelID[Heavy_Door] = LoadMesh_Strict("GFX\map\Props\HeavyDoor1.x") ; ~ Heavy Door #1
	
	o\DoorModelID[Big_Door] = LoadMesh_Strict("GFX\map\Props\contdoorleft.x") ; ~ Big Door Left
	
	o\DoorModelID[Office_Door] = LoadAnimMesh_Strict("GFX\map\Props\officedoor.b3d") ; ~ Office Door
	
	o\DoorModelID[Wooden_Door] = LoadMesh_Strict("GFX\map\Props\DoorWooden.b3d") ; ~ Wooden Door
	
	o\DoorModelID[One_Sided_Door] = LoadMesh_Strict("GFX\map\Props\Door02.x") ; ~ One-sided Door
	
	o\DoorModelID[Heavy_Door + 5] = LoadMesh_Strict("GFX\map\Props\HeavyDoor2.x") ; ~ Heavy Door #2
	
	o\DoorModelID[Big_Door + 5] = LoadMesh_Strict("GFX\map\Props\contdoorright.x") ; ~ Big Door Right
	
	o\DoorModelID[9] = LoadMesh_Strict("GFX\map\Props\DoorFrame.b3d") ; ~ Door Frame
	
	o\DoorModelID[10] = LoadMesh_Strict("GFX\map\Props\officedoorframe.b3d") ; ~ Ofifce Door Frame
	
	o\DoorModelID[11] = LoadMesh_Strict("GFX\map\Props\DoorWoodenFrame.b3d") ; ~ Wooden Door Frame
	
	o\DoorModelID[12] = LoadMesh_Strict("GFX\map\Props\ContDoorFrame.b3d") ; ~ Big Door Frame
	
	For i = 0 To MaxDoorModelIDAmount - 1
		HideEntity(o\DoorModelID[i])
	Next
	
	; ~ [LEVERS]
	
	o\LeverModelID[0] = LoadMesh_Strict("GFX\map\Props\LeverBase.b3d") ; ~ Lever Base
	
	o\LeverModelID[1] = LoadMesh_Strict("GFX\map\Props\LeverHandle.b3d") ; ~ Lever Handle
	
	For i = 0 To MaxLeverModelIDAmount - 1
		HideEntity(o\LeverModelID[i])
	Next
	
	; ~ [CAMS]
	
	o\CamModelID[0] = LoadMesh_Strict("GFX\map\Props\CamBase.b3d") ; ~ Cam Base
	
	o\CamModelID[1] = LoadMesh_Strict("GFX\map\Props\CamHead.b3d") ; ~ Cam Head
	
	For i = 0 To MaxCamModelIDAmount - 1
		HideEntity(o\CamModelID[i])
	Next
	
	; ~ [MONITORS]
	
	o\MonitorModelID[0] = LoadMesh_Strict("GFX\map\Props\monitor2.b3d") ; ~ Monitor
	
	o\MonitorModelID[1] = LoadMesh_Strict("GFX\map\Props\monitor_checkpoint.b3d") ; ~ Checkpoint Monitor LCZ / HCZ
	
	For i = 0 To MaxMonitorModelIDAmount - 1
		HideEntity(o\MonitorModelID[i])
	Next
	
	For i = 2 To CountSurfaces(o\MonitorModelID[1])
		SF = GetSurface(o\MonitorModelID[1], i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitor_overlay.png"
					BrushTexture(b, t\MonitorTextureID[4], 0, 0)
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then DeleteSingleTextureEntryFromCache(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	
	; ~ [ITEMS]
	
	InitItemTemplates()
	
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
	
	; ~ [MISC]
	
	o\MiscModelID[0] = LoadMesh_Strict("GFX\items\cup_liquid.b3d") ; ~ Liquid for cups dispensed by SCP-294
	
	o\MiscModelID[1] = LoadMesh_Strict("GFX\map\Props\elevator_panel.b3d") ; ~ Elevator floor panel
	
	For i = 0 To 1
		HideEntity(o\MiscModelID[i])
	Next
	
	RenderLoading(30, "CHUNKS")
	
	SetChunkDataValues()
	
	RenderLoading(35, "USER TRACKS")
	
	UserTrackMusicAmount = 0
	If opt\EnableUserTracks Then
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
	
	RenderLoading(40, "GRAPHICS")
	
	TextureLodBias(opt\TextureDetailsLevel)
	TextureAnisotropic(opt\AnisotropicLevel)
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("Console commands: ")
	CreateConsoleMsg("  - help [page]")
	CreateConsoleMsg("  - teleport [room name]")
	CreateConsoleMsg("  - godmode [on / off]")
	CreateConsoleMsg("  - noclip [on / off]")
	CreateConsoleMsg("  - infinitestamina [on / off]")
	CreateConsoleMsg("  - noblink [on / off]")
	CreateConsoleMsg("  - notarget [on / off]")
	CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
	CreateConsoleMsg("  - wireframe [on / off]")
	CreateConsoleMsg("  - debughud [category]")
	CreateConsoleMsg("  - camerafog [near] [far]")
	CreateConsoleMsg("  - heal")
	CreateConsoleMsg("  - revive")
	CreateConsoleMsg("  - asd")
	CreateConsoleMsg("  - spawnitem [item name]")
	CreateConsoleMsg("  - 106retreat")
	CreateConsoleMsg("  - disable173 / enable173")
	CreateConsoleMsg("  - disable106 / enable106")
	CreateConsoleMsg("  - spawn [NPC type]")
	
	CatchErrors("LoadEntities")
End Function

Function InitStats()
	me\Playable = True : me\SelectedEnding = -1
	
	HideDistance = 15.0
	as\Timer = 70.0 * 120.0
	
	If opt\DebugMode Then
		InitCheats()
	Else
		ClearCheats()
	EndIf
	
	LoadAchievementsFile()
	
	MaxItemAmount = SelectedDifficulty\InventorySlots
	Dim Inventory.Items(MaxItemAmount - 1)
End Function

Function InitNewGame()
	CatchErrors("Uncaught (InitNewGame)")
	
	Local de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events, rt.RoomTemplates, tw.TempWayPoints
	Local i%
	
	LoadEntities()
	LoadSounds()
	
	InitStats()
	
	RenderLoading(50, "STATS")
	
	me\BlinkTimer = -10.0 : me\BlinkEffect = 1.0 : me\Stamina = 100.0 : me\StaminaEffect = 1.0 : me\HeartBeatRate = 70.0 : me\Funds = Rand(0, 6)
	
	I_005\ChanceToSpawn = Rand(1, 3)
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1, 9) * (10 ^ i)
	Next	
	
	RenderLoading(55, "ROOMS")
	
	If SelectedMap = "" Then
		CreateMap()
	Else
		LoadMap("Map Creator\Maps\" + SelectedMap)
	EndIf
	
	InitWayPoints()
	
	Curr173 = CreateNPC(NPCType173, 0.0, -30.0, 0.0)
	Curr106 = CreateNPC(NPCType106, 0.0, -30.0, 0.0)
	Curr106\State = 70.0 * 60.0 * Rnd(12.0, 17.0)
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\DoorType = Default_Door Lor d\DoorType = One_Sided_Door Lor d\DoorType = SCP_914_Door Then
			MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
		ElseIf d\DoorType = Office_Door Lor d\DoorType = Wooden_Door
			MoveEntity(d\OBJ, (((d\DoorType = Office_Door) * 92.0) + ((d\DoorType = Wooden_Door) * 70.0)) * RoomScale, 0.0, 0.0)
		EndIf
		If d\OBJ2 <> 0 Then
			EntityParent(d\OBJ2, 0)
			If d\DoorType = Default_Door Lor d\DoorType = One_Sided_Door Lor d\DoorType = SCP_914_Door Then
				MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
			EndIf
		EndIf
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		For i = 0 To 1
			If d\Buttons[i] <> 0 Then EntityParent(d\Buttons[i], 0)
			If d\ElevatorPanel[i] <> 0 Then EntityParent(d\ElevatorPanel[i], 0)
		Next
	Next
	
	For it.Items = Each Items
		EntityType(it\Collider, HIT_ITEM)
		EntityParent(it\Collider, 0)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\Angle = EntityYaw(sc\OBJ) + sc\Angle
		EntityParent(sc\OBJ, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights - 1
			If r\Lights[i] <> 0 Then EntityParent(r\Lights[i], 0)
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then de.Decals = CreateDecal(Rand(2, 3), EntityX(r\OBJ) + Rnd(- 2.0, 2.0), r\y + 0.005, EntityZ(r\OBJ) + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.4), Rnd(0.85, 0.95))
			If Rand(4) = 1 Then de.Decals = CreateDecal(0, EntityX(r\OBJ) + Rnd(-2.0, 2.0), r\y + 0.005, EntityZ(r\OBJ) + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0, Rnd(0.5, 0.7), Rnd(0.7, 0.85))
		EndIf
		
		If r\RoomTemplate\Name = "cont1_173" And (Not opt\IntroEnabled) Then 
			PositionEntity(me\Collider, EntityX(r\OBJ) + 3584.0 * RoomScale, r\y + 704.0 * RoomScale, EntityZ(r\OBJ) + 1024.0 * RoomScale)
			PlayerRoom = r
			it.Items = CreateItem("Class D Orientation Leaflet", "paper", 1.0, 1.0, 1.0)
			it\Picked = True : it\Dropped = -1 : it\ItemTemplate\Found = True
			Inventory(0) = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
			it.Items = CreateItem("Document SCP-173", "paper", 1.0, 1.0, 1.0)
			it\Picked = True : it\Dropped = -1 : it\ItemTemplate\Found = True
			Inventory(1) = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
			Exit
		ElseIf r\RoomTemplate\Name = "cont1_173_intro" And opt\IntroEnabled Then
			PositionEntity(me\Collider, EntityX(r\OBJ), 1.0, EntityZ(r\OBJ))
			PlayerRoom = r
			Exit
		EndIf
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\OBJ <> 0 Then FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next	
	
	For tw.TempWayPoints = Each TempWayPoints
		Delete(tw)
	Next
	
	RenderLoading(85, "EVENTS")
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventID = e_room2_nuke
			e\EventState = 1.0
		EndIf
		If e\EventID = e_cont1_106
			e\EventState2 = 1.0
		EndIf	
		If e\EventID = e_room2_sl
			e\EventState3 = 1.0
		EndIf
	Next
	
	RenderLoading(90, "PLAYER POSITION")
	
	TurnEntity(me\Collider, 0.0, Rnd(160.0, 200.0), 0.0)
	
	ResetEntity(me\Collider)
	
	MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	SetFont(fo\FontID[Font_Default])
	
	HidePointer()
	
	fps\Factor[0] = 0.0
	
	ResetInput()
	
	me\DropSpeed = 0.0
	
	DeleteTextureEntriesFromCache(DeleteMapTextures)
	
	RenderLoading(100)
	
	CatchErrors("InitNewGame")
End Function

Function InitLoadGame()
	CatchErrors("Uncaught (InitLoadGame)")
	
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	Local i%, x#, z#
	
	InitStats()
	InitWayPoints()
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		For i = 0 To 1
			If d\Buttons[i] <> 0 Then EntityParent(d\Buttons[i], 0)
			If d\ElevatorPanel[i] <> 0 Then EntityParent(d\ElevatorPanel[i], 0)
		Next
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\Angle = EntityYaw(sc\OBJ) + sc\Angle
		EntityParent(sc\OBJ, 0)
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\OBJ <> 0 Then FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	RenderLoading(85, "EVENTS")
	
	For e.Events = Each Events
		; ~ Loading the necessary stuff for dimension_1499, but this will only be done if the player is in this dimension already
		If e\EventID = e_dimension_1499 Then
			If e\EventState = 2.0 Then
				RenderLoading(91)
				e\room\Objects[0] = CreatePlane()
				
				Local PlaneTex% = LoadTexture_Strict("GFX\map\Dimension1499\grit3.jpg")
				
				EntityTexture(e\room\Objects[0], PlaneTex)
				DeleteSingleTextureEntryFromCache(PlaneTex)
				PositionEntity(e\room\Objects[0], 0.0, EntityY(e\room\OBJ), 0.0)
				EntityType(e\room\Objects[0], HIT_MAP)
				RenderLoading(92)
				I_1499\Sky = CreateSky("GFX\map\sky\1499sky")
				RenderLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict("GFX\map\Dimension1499\1499object" + i + ".b3d")
					HideEntity(e\room\Objects[i])
				Next
				RenderLoading(96)
				CreateChunkParts(e\room)
				RenderLoading(97)
				x = EntityX(e\room\OBJ)
				z = EntityZ(e\room\OBJ)
				
				Local ch.Chunk
				
				For i = -2 To 2 Step 2
					ch.Chunk = CreateChunk(-1, x * (i * 2.5), EntityY(e\room\OBJ), z)
				Next
				RenderLoading(98)
				UpdateChunks(e\room, 15, False)
				Exit
			EndIf
		EndIf
	Next
	
	RenderLoading(90, "PLAYER POSITION")
	
	ResetEntity(me\Collider)
	
	MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	SetFont(fo\FontID[Font_Default])
	
	HidePointer()
	
	fps\Factor[0] = 0.0
	
	ResetInput()
	
	me\DropSpeed = 0.0
	
	DeleteTextureEntriesFromCache(DeleteMapTextures)
	
	RenderLoading(100)
	
	CatchErrors("InitLoadGame")
End Function

Function NullGame(PlayButtonSFX% = True)
	CatchErrors("Uncaught (NullGame)")
	
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials, de.Decals, sc.SecurityCams
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items, pr.Props, c.ConsoleMsg, n.NPCs, em.Emitters
	Local rt.RoomTemplates, p.Particles, e.Events, sub.Subtitles
	Local i%, x%, y%, Lvl%
	
	KillSounds()
	If PlayButtonSFX Then PlaySound_Strict(ButtonSFX)
	
	DeleteTextureEntriesFromCache(DeleteAllTextures)
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer = 0.0
	QuickLoad_CurrEvent = Null
	
	SelectedMap = ""
	
	UsedConsole = False
	
	ItemAmount = 0
	MaxItemAmount = 0
	
	DoorTempID = 0
	RoomTempID = 0
	
	HideDistance = 0.0
	
	GameSaved = 0
	
	NullSelectedStuff()
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\Found = False
	Next
	
	; ~ Just remove the Type and create again
	Delete(me)
	me.Player = New Player
	
	If wi\NightVision > 0 Then
		opt\CameraFogFar = opt\StoredCameraFogFar
	Else
		opt\CameraFogFar = 6.0
	EndIf
	
	Delete(wi)
	wi.WearableItems = New WearableItems
	
	Delete(I_005)
	I_005.SCP005 = New SCP005
	
	Delete(I_008)
	I_008.SCP008 = New SCP008
	
	Delete(I_035)
	I_035.SCP035 = New SCP035
	
	Delete(I_294)
	I_294.SCP294 = New SCP294
	
	Delete(I_409)
	I_409.SCP409 = New SCP409
	
	Delete(I_427)
	I_427.SCP427 = New SCP427
	
	Delete(I_500)
	I_500.SCP500 = New SCP500
	
	Delete(I_714)
	I_714.SCP714 = New SCP714
	
	Delete(I_1025)
	I_1025.SCP1025 = New SCP1025
	
	Delete(I_1499)
	I_1499.SCP1499 = New SCP1499
	
	ClearCheats()
	WireFrameState = 0
	WireFrame(0)
	
	CoffinDistance = 100.0
	
	MTFTimer = 0.0
	
	Delete(achv)
	achv.Achievements = New Achievements
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	Delete(as)
	as.AutoSave = New AutoSave
	
	ShouldPlay = 0
	
	LightVolume = 0.0
	
	SecondaryLightOn = True
	PrevSecondaryLightOn = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	Delete(msg)
	msg.Messages = New Messages
	
	For sub.Subtitles = Each Subtitles
		Delete(sub)
	Next
	
	Delete(CurrMapGrid)
	Delete(I_Zone)
	I_Zone.MapZones = New MapZones
	
	For s.Screens = Each Screens
		Delete(s)
	Next
	
	For i = 0 To MaxItemAmount - 1
		If Inventory(i) <> Null Then Inventory(i) = Null
	Next
	If SelectedItem <> Null Then SelectedItem = Null
	
	Delete(bk)
	bk.BrokenDoor = New BrokenDoor
	
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
	
	For c.ConsoleMsg = Each ConsoleMsg
		Delete(c)
	Next
	
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	Curr513_1 = Null
	Curr049 = Null
	
	ForestNPC = 0
	ForestNPCTex = 0
	
	For e.Events = Each Events
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
		If rt\OBJ <> 0 Then FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	Delete(o)
	o.Objects = New Objects
	
	Delete(t)
	t.Textures = New Textures
	
	DeleteChunks()
	
	OptionsMenu = -1
	QuitMsg = -1
	mm\AchievementsMenu = -1
	
	opt\MusicVolume = opt\PrevMusicVolume
	opt\SFXVolume = opt\PrevSFXVolume
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	ClearWorld()
	ResetTimingAccumulator()
	If Camera <> 0 Then Camera = 0
	If ArkBlurCam <> 0 Then ArkBlurCam = 0
	If Sky <> 0 Then Sky = 0
	InitFastResize()
	
	; ~ Load main menu assets and open main menu
	mm\ShouldDeleteGadgets = True
	InitMainMenuAssets()
	MenuOpen = False
	MainMenuOpen = True
	mm\MainMenuTab = MainMenuTab_Default
	
	CatchErrors("NullGame")
End Function

Const SCP294File$ = "Data\SCP-294.ini"

Function Update294()
	Local it.Items
	Local x#, y#, xTemp%, yTemp%, StrTemp$, Temp%
	Local Sep1%, Sep2%, Alpha#, Glow%
	Local R%, G%, B%
	
	x = mo\Viewport_Center_X - (ImageWidth(t\ImageID[5]) / 2)
	y = mo\Viewport_Center_Y - (ImageHeight(t\ImageID[5]) / 2)
	
	Temp = True
	If PlayerRoom\SoundCHN <> 0 Then Temp = False
	
	If Temp Then
		If mo\MouseHit1 Then
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
					StrTemp = ((xTemp + 1) Mod 10)
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
				
				If I_294\ToInput <> "" Then
					Local Loc% = GetINISectionLocation(SCP294File, I_294\ToInput)
				EndIf
				
				If Loc > 0 Then
					StrTemp = GetINIString2(SCP294File, Loc, "Dispense Sound")
					If StrTemp = "" Then
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound(StrTemp))
					EndIf
					
					If me\UsedMastercard Then PlaySound_Strict(LoadTempSound("SFX\SCP\294\PullMasterCard.ogg"))
					
					If GetINIInt2(SCP294File, Loc, "Explosion") Then 
						me\ExplosionTimer = 135.0
						msg\DeathMsg = GetINIString2(SCP294File, Loc, "Death Message")
					EndIf
					
					StrTemp = GetINIString2(SCP294File, Loc, "Color")
					
					Sep1 = Instr(StrTemp, ", ", 1)
					Sep2 = Instr(StrTemp, ", ", Sep1 + 1)
					R = Trim(Left(StrTemp, Sep1 - 1))
					G = Trim(Mid(StrTemp, Sep1 + 1, Sep2 - Sep1 - 1))
					B = Trim(Right(StrTemp, Len(StrTemp) - Sep2))
					
					Alpha = Float(GetINIString2(SCP294File, Loc, "Alpha", 1.0))
					Glow = GetINIInt2(SCP294File, Loc, "Glow")
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
		
		If mo\MouseHit2 Lor (Not I_294\Using) Then 
			I_294\Using = False
			I_294\ToInput = ""
			StopMouseMovement()
		EndIf
	Else ; ~ Playing a dispensing sound
		If I_294\ToInput <> "OUT OF RANGE" Then I_294\ToInput = "DISPENSING..."
		
		If (Not ChannelPlaying(PlayerRoom\SoundCHN)) Then
			If I_294\ToInput <> "OUT OF RANGE" Then
				I_294\Using = False
				me\UsedMastercard = False
				StopMouseMovement()
				
				Local e.Events
				
				For e.Events = Each Events
					If PlayerRoom = e\room Then
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

Function Render294()
	Local x#, y#, xTemp%, yTemp%, Temp%
	
	ShowPointer()
	
	x = mo\Viewport_Center_X - (ImageWidth(t\ImageID[5]) / 2)
	y = mo\Viewport_Center_Y - (ImageHeight(t\ImageID[5]) / 2)
	DrawImage(t\ImageID[5], x, y)
	If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	
	Temp = True
	If PlayerRoom\SoundCHN <> 0 Then Temp = False
	
	Text(x + 907 * MenuScale, y + 185 * MenuScale, I_294\ToInput, True, True)
	
	If Temp Then
		If mo\MouseHit2 Lor (Not I_294\Using) Then 
			HidePointer()
		EndIf
	Else ; ~ Playing a dispensing sound
		If (Not ChannelPlaying(PlayerRoom\SoundCHN)) Then
			If I_294\ToInput <> "OUT OF RANGE" Then
				HidePointer()
			EndIf
		EndIf
	EndIf
End Function

Function Use427()
	Local de.Decals, e.Events
	Local i%, Pvt%, TempCHN%
	Local PrevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70.0 * 360.0 Then
		If I_427\Using Then
			I_427\Timer = I_427\Timer + fps\Factor[0]
			For e.Events = Each Events
				If e\EventID = e_1048_a Then
					If e\EventState2 > 0.0 Then e\EventState2 = Max(e\EventState2 - (fps\Factor[0] * 0.5), 0.0)
					Exit
				EndIf
			Next
			If me\Injuries > 0.0 Then me\Injuries = Max(me\Injuries - (fps\Factor[0] * 0.0005), 0.0)
			If me\Bloodloss > 0.0 And me\Injuries =< 1.0 Then me\Bloodloss = Max(me\Bloodloss - (fps\Factor[0] * 0.001), 0.0)
			If I_008\Timer > 0.0 Then I_008\Timer = Max(I_008\Timer - (fps\Factor[0] * 0.001), 0.0)
			If I_409\Timer > 0.0 Then I_409\Timer = Max(I_409\Timer - (fps\Factor[0] * 0.003), 0.0)
			For i = 0 To 6
				If I_1025\State[i] > 0.0 Then I_1025\State[i] = Max(I_1025\State[i] - (0.001 * fps\Factor[0] * I_1025\State[7]), 0.0)
			Next
			If (Not I_427\Sound[0]) Then I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			If I_427\Timer >= 70.0 * 180.0 Then
				If (Not I_427\Sound[1]) Then I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
			EndIf
			If PrevI427Timer < 70.0 * 60.0 And I_427\Timer >= 70.0 * 60.0 Then
				CreateMsg("You feel refreshed and energetic.")
			ElseIf PrevI427Timer < 70.0 * 180.0 And I_427\Timer >= 70.0 * 180.0
				CreateMsg("You feel gentle muscle spasms all over your body.")
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i] <> 0 Then If ChannelPlaying(I_427\SoundCHN[i]) Then StopChannel(I_427\SoundCHN[i])
			Next
		EndIf
	Else
		If PrevI427Timer - fps\Factor[0] < 70.0 * 360.0 And I_427\Timer >= 70.0 * 360.0 Then
			CreateMsg("Your muscles are swelling. You feel more powerful than ever.")
		ElseIf PrevI427Timer - fps\Factor[0] < 70.0 * 390.0 And I_427\Timer >= 70.0 * 390.0 Then
			CreateMsg("You can't feel your legs. But you don't need legs anymore.")
		EndIf
		I_427\Timer = I_427\Timer + fps\Factor[0]
		If (Not I_427\Sound[0]) Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If (Not I_427\Sound[1]) Then
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
			de.Decals = CreateDecal(18, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, Rnd(0.03, 0.08) * 2.0)
			de\SizeChange = Rnd(0.001, 0.0015) : de\MaxSize = de\Size + 0.009 
			TempCHN = PlaySound_Strict(DripSFX[Rand(0, 2)])
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * opt\SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			FreeEntity(Pvt)
			me\BlurTimer = 800.0
		EndIf
		If I_427\Timer >= 70.0 * 420.0 Then
			Kill()
			msg\DeathMsg = Chr(34) + "Requesting support from MTF Nu-7. We need more firepower to take this thing down." + Chr(34)
		ElseIf I_427\Timer >= 70.0 * 390.0
			If (Not me\Crouch) Then SetCrouch(True)
		EndIf
	EndIf
End Function

Function UpdateMTF()
	If PlayerRoom\RoomTemplate\Name = "gate_a_entrance" Then Return
	
	Local r.Rooms, n.NPCs
	Local Dist#, i%
	
	If MTFTimer = 0.0 Then
		If Rand(30) = 1 And PlayerRoom\RoomTemplate\Name <> "dimension_1499" Then
			Local entrance.Rooms = Null
			
			For r.Rooms = Each Rooms
				If r\RoomTemplate\Name = "gate_a_entrance" Then 
					entrance = r
					Exit
				EndIf
			Next
			
			If entrance <> Null Then 
				If me\Zone = 2 Then
					If PlayerInReachableRoom() Then
						PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
					EndIf
					
					MTFTimer = fps\Factor[0]
					
					Local leader.NPCs
					
					For i = 0 To 2
						n.NPCs = CreateNPC(NPCTypeMTF, EntityX(entrance\OBJ) + 0.3 * (i - 1), 0.6, EntityZ(entrance\OBJ) + 8.0)
						
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
	Else
		If MTFTimer =< 70.0 * 120.0 Then
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 70.0 * 120.0 And MTFTimer < 10000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			MTFTimer = 10000.0
		ElseIf MTFTimer >= 10000.0 And MTFTimer =< 10000.0 + (70.0 * 120.0)
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 10000.0 + (70.0 * 120.0) And MTFTimer < 20000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			MTFTimer = 20000.0
		ElseIf MTFTimer >= 20000.0 And MTFTimer =< 20000.0 + (70.0 * 60.0)
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 20000.0 + (70.0 * 60.0) And MTFTimer < 25000.0
			If PlayerInReachableRoom() Then
				; ~ If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount - 1
					If Inventory(i) <> Null Then
						If (Left(Inventory(i)\ItemTemplate\Name, 4) = "SCP-") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-035") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-093")
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
			MTFTimer = MTFTimer + fps\Factor[0]
		ElseIf MTFTimer > 25000.0 + (70.0 * 60.0) And MTFTimer < 30000.0
			If PlayerInReachableRoom() Then PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			MTFTimer = 30000.0
		EndIf
	EndIf
End Function

Function UpdateCameraCheck()
	If MTFCameraCheckTimer > 0.0 And MTFCameraCheckTimer < 70.0 * 90.0 Then
		MTFCameraCheckTimer = MTFCameraCheckTimer + fps\Factor[0]
	ElseIf MTFCameraCheckTimer >= 70.0 * 90.0
		MTFCameraCheckTimer = 0.0
		If (Not me\Detected) Then
			If MTFCameraCheckDetected Then
				PlayAnnouncement("SFX\Character\MTF\AnnouncCameraFound" + Rand(1, 2) + ".ogg")
				me\Detected = True
				MTFCameraCheckTimer = 70.0 * 60.0
			Else
				PlayAnnouncement("SFX\Character\MTF\AnnouncCameraNoFound.ogg")
			EndIf
		EndIf
		MTFCameraCheckDetected = False
		If MTFCameraCheckTimer = 0.0 Then me\Detected = False
	EndIf
End Function

Function UpdateExplosion()
	Local p.Particles
	Local i%
	
	; ~ This here is necessary because the SCP-294's drinks with explosion effect didn't worked anymore -- ENDSHN
	If me\ExplosionTimer > 0.0 Then
		me\ExplosionTimer = me\ExplosionTimer + fps\Factor[0]
		If me\ExplosionTimer < 140.0 Then
			If me\ExplosionTimer - fps\Factor[0] < 5.0 Then
				ExplosionSFX = LoadSound_Strict("SFX\Ending\gateb\Nuke1.ogg")
				PlaySound_Strict(ExplosionSFX)
				me\BigCameraShake = 10.0
				me\ExplosionTimer = 5.0
			EndIf
			me\BigCameraShake = CurveValue(me\ExplosionTimer / 60.0, me\BigCameraShake, 50.0)
		Else
			me\BigCameraShake = Min((me\ExplosionTimer / 20.0), 20.0)
			If me\ExplosionTimer - fps\Factor[0] < 140.0 Then
				me\BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\gateb\Nuke2.ogg")
				PlaySound_Strict(ExplosionSFX)				
				For i = 0 To (10 + (10 * (opt\ParticleAmount + 1)))
					p.Particles = CreateParticle(0, EntityX(me\Collider) + Rnd(-0.5, 0.5), EntityY(me\Collider) - Rnd(0.2, 1.5), EntityZ(me\Collider) + Rnd(-0.5, 0.5), Rnd(0.2, 0.6), 0.0, 350.0)	
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

Function UpdateVomit()
	CatchErrors("Uncaught (UpdateVomit)")
	
	Local de.Decals
	Local Pvt%
	
	If me\CameraShakeTimer > 0.0 Then
		me\CameraShakeTimer = me\CameraShakeTimer - (fps\Factor[0] / 70.0)
		me\CameraShake = 2.0
	EndIf
	
	If me\VomitTimer > 0.0 Then
		me\VomitTimer = me\VomitTimer - (fps\Factor[0] / 70.0)
		
		If (MilliSecs2() Mod 1600) < Rand(200, 400) Then
			If me\BlurTimer = 0.0 Then me\BlurTimer = 70.0 * Rnd(10.0, 20.0)
			me\CameraShake = Rnd(0.0, 2.0)
		EndIf
		
		If Rand(50) = 50 And (MilliSecs2() Mod 4000) < 200 Then PlaySound_Strict(CoughSFX[Rand(0, 2)])
		
		; ~ Regurgitate when timer is below 10 seconds
		If me\VomitTimer < 10.0 And Rnd(0.0, 500.0 * me\VomitTimer) < 2.0 Then
			If (Not ChannelPlaying(VomitCHN)) And (Not me\Regurgitate) Then
				VomitCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Retch" + Rand(1, 2) + ".ogg"))
				me\Regurgitate = MilliSecs2() + 50
			EndIf
		EndIf
		
		If me\Regurgitate > MilliSecs2() And me\Regurgitate <> 0 Then
			mo\Mouse_Y_Speed_1 = mo\Mouse_Y_Speed_1 + 1.0
		Else
			me\Regurgitate = 0
		EndIf
	ElseIf me\VomitTimer < 0.0 Then ; ~ Vomit
		me\VomitTimer = me\VomitTimer - (fps\Factor[0] / 70.0)
		
		If me\VomitTimer > -5.0 Then
			If (MilliSecs2() Mod 400) < 50 Then me\CameraShake = 4.0 
			mo\Mouse_X_Speed_1 = 0.0
			me\Playable = False
		Else
			me\Playable = True
		EndIf
		
		If (Not me\Vomit) Then
			me\BlurTimer = 70.0 * 40.0
			VomitSFX = LoadSound_Strict("SFX\SCP\294\Vomit.ogg")
			VomitCHN = PlaySound_Strict(VomitSFX)
			me\PrevInjuries = me\Injuries
			me\PrevBloodloss = me\Bloodloss
			If (Not me\Crouch) Then SetCrouch(True)
			me\Injuries = 1.5
			me\Bloodloss = 70.0
			me\EyeIrritation = 70.0 * 9.0
			
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(Camera), EntityY(me\Collider) - 0.05, EntityZ(Camera))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(5, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, 180.0, 0.0, 0.001, 1.0, 0, 1, 0, Rand(200, 255), 0)
			de\SizeChange = 0.001 : de\MaxSize = 0.6
			FreeEntity(Pvt)
			me\Vomit = True
		EndIf
		
		mo\Mouse_Y_Speed_1 = mo\Mouse_Y_Speed_1 + Max((1.0 + me\VomitTimer / 10.0), 0.0)
		
		If me\VomitTimer < -15.0 Then
			FreeSound_Strict(VomitSFX)
			me\VomitTimer = 0.0
			If me\KillTimer >= 0.0 Then PlaySound_Strict(BreathSFX(0, 0))
			me\Injuries = me\PrevInjuries
			me\Bloodloss = me\PrevBloodloss
			me\Vomit = False
		EndIf
	EndIf
	
	CatchErrors("UpdateVomit")
End Function

Function Update008()
	Local r.Rooms, e.Events, p.Particles, de.Decals
	Local PrevI008Timer#, i%
	Local TeleportForInfect% = True
	
	If PlayerRoom\RoomTemplate\Name = "cont2_860_1"
		If forest_event\EventState = 1.0 Then TeleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension_1499" Lor PlayerRoom\RoomTemplate\Name = "dimension_106" Lor PlayerRoom\RoomTemplate\Name = "gate_b" Lor PlayerRoom\RoomTemplate\Name = "gate_a"
		TeleportForInfect = False
	EndIf
	
	If I_008\Timer > 0.0 Then
		ShowEntity(t\OverlayID[3])
		If I_008\Timer < 93.0 Then
			PrevI008Timer = I_008\Timer
			If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
				If I_008\Revert Then
					I_008\Timer = Max(0.0, I_008\Timer - (fps\Factor[0] * 0.01))
				Else
					I_008\Timer = Min(I_008\Timer + (fps\Factor[0] * 0.002), 100.0)
				EndIf
			EndIf
			
			me\BlurTimer = Max(I_008\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
			
			me\HeartBeatRate = Max(me\HeartBeatRate, 100.0)
			me\HeartBeatVolume = Max(me\HeartBeatVolume, I_008\Timer / 120.0)
			
			EntityAlpha(t\OverlayID[3], Min(((I_008\Timer * 0.2) ^ 2.0) / 1000.0, 0.5) * (Sin(MilliSecs2() / 8.0) + 2.0))
			
			For i = 0 To 6
				If I_008\Timer > (i * 15.0) + 10.0 And PrevI008Timer =< (i * 15.0) + 10.0 Then
					If (Not I_008\Revert) Then PlaySound_Strict(LoadTempSound("SFX\SCP\008\Voices" + i + ".ogg"))
				EndIf
			Next
			
			If I_008\Timer > 20.0 And PrevI008Timer =< 20.0 Then
				If I_008\Revert Then
					CreateMsg("You feel better.")
				Else
					CreateMsg("You feel kinda feverish.")
				EndIf
			ElseIf I_008\Timer > 40.0 And PrevI008Timer =< 40.0
				If I_008\Revert Then
					CreateMsg("Your nausea is fading.")
				Else
					CreateMsg("You feel nauseated.")
				EndIf
			ElseIf I_008\Timer > 60.0 And PrevI008Timer =< 60.0
				If I_008\Revert Then
					CreateMsg("The headache is fading.")
				Else
					CreateMsg("The nausea's getting worse.")
				EndIf
			ElseIf I_008\Timer > 80.0 And PrevI008Timer =< 80.0
				If I_008\Revert Then
					CreateMsg("You feel more energetic.")
				Else
					CreateMsg("You feel very faint.")
				EndIf
			ElseIf I_008\Timer >= 91.5
				me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 91.5), me\BlinkTimer), -10.0)
				me\Zombie = True
				If I_008\Timer >= 92.7 And PrevI008Timer < 92.7 Then
					If TeleportForInfect Then
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name = "cont2_008" Then
								PositionEntity(me\Collider, EntityX(r\Objects[7], True), EntityY(r\Objects[7], True), EntityZ(r\Objects[7], True), True)
								ResetEntity(me\Collider)
								r\NPC[0] = CreateNPC(NPCTypeD, EntityX(r\Objects[6], True), EntityY(r\Objects[6], True) + 0.2, EntityZ(r\Objects[6], True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundCHN = PlaySound_Strict(r\NPC[0]\Sound)
								ChangeNPCTextureID(r\NPC[0], 11)
								r\NPC[0]\State = 6.0
								PlayerRoom = r
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			PrevI008Timer = I_008\Timer
			I_008\Timer = Min(I_008\Timer + (fps\Factor[0] * 0.004), 100.0)
			
			If TeleportForInfect Then
				If I_008\Timer < 94.7 Then
					EntityAlpha(t\OverlayID[3], 0.5 * (Sin(MilliSecs2() / 8.0) + 2.0))
					me\BlurTimer = 900.0
					
					If I_008\Timer > 94.5 Then me\BlinkTimer = Max(Min((-50.0) * (I_008\Timer - 94.5), me\BlinkTimer), -10.0)
					PointEntity(me\Collider, PlayerRoom\NPC[0]\Collider)
					PointEntity(PlayerRoom\NPC[0]\Collider, me\Collider)
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider, EntityRoll(Camera))
					me\ForceMove = 0.75
					me\Injuries = 2.5
					me\Bloodloss = 0.0
					
					Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 357.0, 381.0, 0.3)
				ElseIf I_008\Timer < 98.5
					EntityAlpha(t\OverlayID[3], 0.5 * (Sin(MilliSecs2() / 5.0) + 2.0))
					me\BlurTimer = 950.0
					
					me\ForceMove = 0.0
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider)
					
					If PrevI008Timer < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundCHN = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						msg\DeathMsg = SubjectName + " found ingesting Dr. [DATA REDACTED] at Sector [DATA REDACTED]. Subject was immediately terminated by Nine-Tailed Fox and sent for autopsy. "
						msg\DeathMsg = msg\DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544.0 * RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider), 90.0, Rnd(360.0), 0.0, 0.8)
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
					
					If opt\ParticleAmount > 0 Then
						If Rand(50) = 1 Then
							p.Particles = CreateParticle(6, EntityX(PlayerRoom\NPC[0]\Collider), EntityY(PlayerRoom\NPC[0]\Collider), EntityZ(PlayerRoom\NPC[0]\Collider), Rnd(0.05, 0.1), 0.15, 200)
							p\Speed = 0.01 : p\SizeChange = 0.01 : p\Alpha = 0.5 : p\AlphaChange = -0.01
							RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
						EndIf
					EndIf
					
					PositionEntity(me\Head, EntityX(PlayerRoom\NPC[0]\Collider, True), EntityY(PlayerRoom\NPC[0]\Collider, True) + 0.65, EntityZ(PlayerRoom\NPC[0]\Collider, True), True)
					RotateEntity(me\Head, (1.0 + Sin(MilliSecs2() / 5.0)) * 15.0, PlayerRoom\Angle - 180.0, 0.0, True)
					MoveEntity(me\Head, 0.0, 0.0, -0.4)
					TurnEntity(me\Head, 80.0 + (Sin(MilliSecs2() / 5.0)) * 30.0, (Sin(MilliSecs2() / 5.0)) * 40.0, 0.0)
				EndIf
			Else
				Kill()
				me\BlinkTimer = Max(Min((-10.0) * (I_008\Timer - 96.0), me\BlinkTimer), -10.0)
				If PlayerRoom\RoomTemplate\Name = "dimension_1499" Then
					msg\DeathMsg = "The whereabouts of SCP-1499 are still unknown, but a recon team has been dispatched to investigate reports of a violent attack to a church in the Russian town of [DATA REDACTED]."
				ElseIf PlayerRoom\RoomTemplate\Name = "gate_b" Lor PlayerRoom\RoomTemplate\Name = "gate_a" Then
					msg\DeathMsg = SubjectName + " found wandering around Gate "
					If PlayerRoom\RoomTemplate\Name = "gate_a" Then
						msg\DeathMsg = msg\DeathMsg + "A"
					Else
						msg\DeathMsg = msg\DeathMsg + "B"
					EndIf
					msg\DeathMsg = msg\DeathMsg + ". Subject was immediately terminated by Nine-Tailed Fox and sent for autopsy. "
					msg\DeathMsg = msg\DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
				Else
					msg\DeathMsg = ""
				EndIf
			EndIf
		EndIf
	Else
		If I_008\Revert Then I_008\Revert = False
		HideEntity(t\OverlayID[3])
	EndIf
End Function

Function Update409()
	Local PrevI409Timer# = I_409\Timer
	
	If I_409\Timer > 0.0 Then
		ShowEntity(t\OverlayID[7])
		
		If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0 Then
			If I_409\Revert Then
				I_409\Timer = Max(0.0, I_409\Timer - (fps\Factor[0] * 0.01))
			Else
				I_409\Timer = Min(I_409\Timer + (fps\Factor[0] * 0.004), 100.0)
			EndIf
		EndIf	
		EntityAlpha(t\OverlayID[7], Min(((I_409\Timer * 0.2) ^ 2.0) / 1000.0, 0.5))
		me\BlurTimer = Max(I_409\Timer * 3.0 * (2.0 - me\CrouchState), me\BlurTimer)
		
		If I_409\Timer > 40.0 And PrevI409Timer =< 40.0 Then
			If I_409\Revert Then
				CreateMsg("Crystals are falling from the skin on your legs.")
			Else
				CreateMsg("Crystals are enveloping the skin on your legs.")
			EndIf
		ElseIf I_409\Timer > 55.0 And PrevI409Timer =< 55.0
			If I_409\Revert Then
				CreateMsg("Crystals are falling from your abdomen.")
			Else
				CreateMsg("Crystals are enveloping your abdomen.")
			EndIf
		ElseIf I_409\Timer > 70.0 And PrevI409Timer =< 70.0
			If I_409\Revert Then
				CreateMsg("Crystals are falling from your arms.")
			Else
				CreateMsg("Crystals are starting to envelop your arms.")
			EndIf
		ElseIf I_409\Timer > 85.0 And PrevI409Timer =< 85.0
			If I_409\Revert Then
				CreateMsg("Crystals starting to envelop your head.")
			Else
				CreateMsg("Crystals starting to envelop your head.")
			EndIf
		ElseIf I_409\Timer > 93.0 And PrevI409Timer =< 93.0
			If (Not I_409\Revert) Then
				PlaySound_Strict(DamageSFX[13])
				me\Injuries = Max(me\Injuries, 2.0)
			EndIf
		ElseIf I_409\Timer > 94.0
			I_409\Timer = Min(I_409\Timer + (fps\Factor[0] * 0.004), 100.0)
			me\Playable = False
			me\BlurTimer = 4.0
			me\CameraShake = 3.0
		EndIf
		If I_409\Timer >= 55.0 Then
			me\StaminaEffect = 1.2
			me\StaminaEffectTimer = 1.0
			me\Stamina = Min(me\Stamina, 60.0)
		EndIf
		If I_409\Timer >= 96.9222 Then
			msg\DeathMsg = "Pile of SCP-409 crystals found and, by comparing list of the dead, was found to be " + SubjectName + " who had physical contact with SCP-409. "
			msg\DeathMsg = msg\DeathMsg + "Remains were incinerated along with crystal-infested areas of facility."
			Kill(True)
		EndIf
	Else
		If I_409\Revert Then I_409\Revert = False
		HideEntity(t\OverlayID[7])	
	EndIf
End Function

Function UpdateLeave1499()
	Local r.Rooms, it.Items, r2.Rooms, r1499.Rooms
	Local i%
	
	If I_1499\Using = 0 And PlayerRoom\RoomTemplate\Name = "dimension_1499" Then
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
				If PlayerRoom\RoomTemplate\Name = "room3_storage"
					If EntityY(me\Collider) < (-4600.0) * RoomScale
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
					If r2\RoomTemplate\Name = "dimension_1499" Then
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\DistTimer = 0.0
					If it\ItemTemplate\TempName = "scp1499" Lor it\ItemTemplate\TempName = "super1499" Then
						If EntityY(it\Collider) >= EntityY(r1499\OBJ) - 5.0 Then
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
	; ~ False (= 0): Player is not in facility (mostly meant for "dimension_1499")
	; ~ True (= 1): Player is in facility
	; ~ 2: Player is in tunnels (maintenance tunnels / SCP-049's tunnels / SCP-939's storage room, etc...)
	
	If EntityY(me\Collider) > 100.0 Then Return(0)
	If EntityY(me\Collider) < -10.0 Then Return(2)
	If EntityY(me\Collider) > 7.0 And EntityY(me\Collider) =< 100.0 Then Return(2)
	Return(1)
End Function

Function TeleportEntity(Entity%, x#, y#, z#, CustomRadius# = 0.3, IsGlobal% = False, PickRange# = 2.0, Dir% = False)
	Local Pvt%, Pick#
	; ~ Dir = 0 - towards the floor (default)
	; ~ Dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	Pvt = CreatePivot()
	PositionEntity(Pvt, x, y + 0.05, z, IsGlobal)
	If (Not Dir)
		RotateEntity(Pvt, 90.0, 0.0, 0.0)
	Else
		RotateEntity(Pvt, -90.0, 0.0, 0.0)
	EndIf
	Pick = EntityPick(Pvt, PickRange)
	If Pick <> 0 Then
		If (Not Dir) Then
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

Function InteractObject%(OBJ%, Dist#, Arrow% = False, ArrowID% = 0, MouseDown_% = False)
	If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor SelectedDoor <> Null Lor SelectedScreen <> Null Then Return
	
	If EntityDistanceSquared(me\Collider, OBJ) < Dist Then
		If EntityInView(OBJ, Camera) Then
			If Arrow Then ga\DrawArrowIcon[ArrowID] = True
			ga\DrawHandIcon = True
			If MouseDown_ Then
				If mo\MouseDown1 Then Return(True)
			Else
				If mo\MouseHit1 Then Return(True)
			EndIf
		EndIf
	EndIf
	Return(False)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D