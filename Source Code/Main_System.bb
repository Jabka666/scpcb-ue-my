Local InitErrorStr$ = ""

If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
If FileSize("BlitzMovie.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzMovie.dll" + Chr(13) + Chr(10)
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then
	RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr)
EndIf

Include "Source Code\FMod.bb"
Include "Source Code\StrictLoads.bb"
Include "Source Code\Fullscreen_Window_Fix.bb"
Include "Source Code\KeyName.bb"
Include "Source Code\INI_System.bb"

Include "Source Code\DevilParticleSystem.bb"

Global ErrorFile$ = "error_log_"

Local ErrorFileInd% = 0

While FileType(ErrorFile + Str(ErrorFileInd) + ".txt") <> 0
	ErrorFileInd = ErrorFileInd + 1
Wend
ErrorFile = ErrorFile + Str(ErrorFileInd) + ".txt"

Global Font1%, Font2%, Font3%, Font4%, Font5%
Global ConsoleFont%

Global MenuWhite%, MenuBlack%
Global ButtonSFX%

Global EnableSFXRelease_Prev% = EnableSFXRelease

Dim ArrowIMG(4)

Global Depth% = 0

Global LauncherIMG%

Global SelectedGFXMode%

Global Fresize_Image%, Fresize_Texture%, Fresize_Texture2%
Global Fresize_Cam%

Global WireframeState%
Global HalloweenTex%

Global TotalGFXModes% = CountGfxModes3D(), GFXModes%
Dim GfxModeWidths%(TotalGFXModes), GfxModeHeights%(TotalGFXModes)

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

Include "Source Code\AAText.bb"

If LauncherEnabled Then 
	AspectRatioRatio = 1.0
	UpdateLauncher()
	
	; ~ New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode -- Love Mark
	If BorderlessWindowed
		Graphics3DExt(G_Viewport_Width, G_VieWport_Height, 0, 2)
		
		; ~ Change the window style to 'WS_POPUP' and then set the window position to force the style to update.
		Api_SetWindowLong(G_App_Handle, C_GWL_STYLE, C_WS_POPUP)
		Api_SetWindowPos(G_App_Handle, C_HWND_TOP, G_Viewport_x, G_Viewport_y, G_Viewport_Width, G_Viewport_Height, C_SWP_SHOWWINDOW)
		
		RealGraphicWidth = G_Viewport_Width
		RealGraphicHeight = G_Viewport_Height
		
		AspectRatioRatio = (Float(GraphicWidth) / Float(GraphicHeight)) / (Float(RealGraphicWidth) / Float(RealGraphicHeight))
		
		FullScreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If FullScreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16 * Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
Else
	For i% = 1 To TotalGFXModes
		Local SameFound% = False
		
		For n% = 0 To TotalGFXModes - 1
			If GfxModeWidths(n) = GfxModeWidth(i) And GfxModeHeights(n) = GfxModeHeight(i) Then SameFound = True : Exit
		Next
		If SameFound = False Then
			If GraphicWidth = GfxModeWidth(i) And GraphicHeight = GfxModeHeight(i) Then SelectedGFXMode = GFXModes
			GfxModeWidths(GFXModes) = GfxModeWidth(i)
			GfxModeHeights(GFXModes) = GfxModeHeight(i)
			GFXModes = GFXModes + 1
		End If
	Next
	
	GraphicWidth = GfxModeWidths(SelectedGFXMode)
	GraphicHeight = GfxModeHeights(SelectedGFXMode)
	
	; ~ New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark,
	If BorderlessWindowed
		Graphics3DExt(G_Viewport_Width, G_Viewport_Height, 0, 2)
		
		; ~ Change the window style to 'WS_POPUP' and then set the window position to force the style to update.
		Api_SetWindowLong(G_App_Handle, C_GWL_STYLE, C_WS_POPUP)
		Api_SetWindowPos(G_App_Handle, C_HWND_TOP, G_Viewport_x, G_Viewport_y, G_Viewport_Width, G_Viewport_Height, C_SWP_SHOWWINDOW)
		
		RealGraphicWidth = G_Viewport_Width
		RealGraphicHeight = G_Viewport_Height
		
		AspectRatioRatio = (Float(GraphicWidth) / Float(GraphicHeight)) / (Float(RealGraphicWidth) / Float(RealGraphicHeight))
		
		FullScreen = False
	Else
		AspectRatioRatio = 1.0
		RealGraphicWidth = GraphicWidth
		RealGraphicHeight = GraphicHeight
		If FullScreen Then
			Graphics3DExt(GraphicWidth, GraphicHeight, (16 * Bit16Mode), 1)
		Else
			Graphics3DExt(GraphicWidth, GraphicHeight, 0, 2)
		End If
	EndIf
EndIf

Global MenuScale# = (GraphicHeight / 1024.0)

SetBuffer(BackBuffer())

Global CurTime%, PrevTime%, LoopDelay%, FPSfactor#, FPSfactor2#, PrevFPSFactor#

Local CheckFPS%, ElapsedLoops%, FPS%, ElapsedTime#

Global CurrFrameLimit# = (FrameLimit - 19.0) / 100.0

SeedRnd(MilliSecs())

Global GameSaved%

Global CanSave% = True

AppTitle "SCP - Containment Breach Ultimate Edition v" + VersionNumber

PlayStartupVideos()

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict("Loadingscreens\loadingback.jpg")
InitLoadingScreens("Loadingscreens\loadingscreens.ini")

InitAAFont()
; ~ For some reason, Blitz3D doesn't load fonts that have filenames that
; ~ Don't match their "internal name" (i.e. their display name in applications like Word and such).
; ~ As a workaround, I moved the files and renamed them so they
; ~ Can load without FastText.
Font1 = AALoadFont("GFX\font\cour\Courier New.ttf", Int(19 * (GraphicHeight / 1024.0)), 0, 0, 0)
Font2 = AALoadFont("GFX\font\courbd\Courier New.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)
Font3 = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0, 0, 0)
Font4 = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(60 * (GraphicHeight / 1024.0)), 0, 0, 0)
Font5 = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)

Global CreditsFont%, CreditsFont2%

ConsoleFont = AALoadFont("Blitz", Int(20 * (GraphicHeight / 1024.0)), 0, 0, 0, 1)

AASetFont(Font2)

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")

DrawLoading(0, True)

; ~ Viewport.
Global Viewport_Center_X% = GraphicWidth / 2, Viewport_Center_Y% = GraphicHeight / 2

; ~ Mouselook.
Global Mouselook_X_Inc# = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the X axis.
Global Mouselook_Y_Inc# = 0.3 ; ~ This sets both the sensitivity and direction (+ / -) of the mouse on the Y axis.
; ~ Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global Mouse_Left_Limit% = 250, Mouse_Right_Limit% = GraphicsWidth () - 250
Global Mouse_Top_Limit% = 150, Mouse_Bottom_Limit% = GraphicsHeight () - 150 ; ~ As above.
Global Mouse_X_Speed_1#, Mouse_Y_Speed_1#

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

Global KillTimer#, KillAnim%, FallTimer#, DeathTimer#
Global Sanity#, ForceMove#, ForceAngle#
Global RestoreSanity%

Global Playable% = True

Global BLINKFREQ#
Global BlinkTimer#, EyeIrritation#, EyeStuck#, BlinkEffect# = 1.0, BlinkEffectTimer#

Global Stamina#, StaminaEffect# = 1.0, StaminaEffectTimer#

Global CameraShakeTimer#, Vomit%, VomitTimer#, Regurgitate%

Global SCP1025State#[6]

Global HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#

Global WearingGasMask%, WearingHazmat%, WearingVest%, Wearing714%, WearingNightVision%
Global NVTimer#

Global SuperMan%, SuperManTimer#

Global Injuries#, Bloodloss#, Infect#, HealTimer#

Global RefinedItems%

Include "Source Code\Achievements_System.bb"

Global DropSpeed#, HeadDropSpeed#, CurrSpeed#
Global User_Camera_Pitch#, Side#
Global Crouch%, CrouchState#

Global PlayerZone%, PlayerRoom.Rooms

Global GrabbedEntity%

Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%

Global NoClipSpeed# = 2.0

Type Cheats
	Field GodMode%
	Field NoBlink%
	Field NoTarget%
	Field NoClip%
	Field InfiniteStamina%
	Field Cheats%
End Type

Global chs.Cheats = New Cheats

Global CoffinDistance# = 100.0

Global PlayerSoundVolume#

Global Shake#

Global ExplosionTimer#, ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

Global MainMenuOpen%, MenuOpen%, StopHidingTimer#, InvOpen%
Global OtherOpen.Items = Null

Global SelectedEnding$, EndingScreen%, EndingTimer#

Global MsgTimer#, Msg$, DeathMsg$

Global AccessCode%, KeypadInput$, KeypadTimer#, KeypadMsg$

Global DrawHandIcon%
Dim DrawArrowIcon%(4)

Include "Source Code\Difficulty.bb"

Global MTFTimer#, MTFRooms.Rooms[10], MTFRoomState%[10]

Dim RadioState#(10)
Dim RadioState3%(10)
Dim RadioState4%(9)
Dim RadioCHN%(8)

Dim OldAiPics%(5)

Global PlayTime%
Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global NVBlink%
Global IsNVGBlinking% = False

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
	
	If (c\R < 0) Then c\R = ConsoleR
	If (c\G < 0) Then c\G = ConsoleG
	If (c\B < 0) Then c\B = ConsoleB
End Function

Function UpdateConsole()
	Local e.Events
	
	If CanOpenConsole = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		AASetFont(ConsoleFont)
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		Local x% = 0, y% = GraphicHeight - 300 * MenuScale, Width% = GraphicWidth, Height% = 300 * MenuScale - 30 * MenuScale
		Local StrTemp$, Temp%,  i%
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
		InBar% = MouseOn(x + Width - 26 * MenuScale, y, 26 * MenuScale, Height)
		If InBar Then Color(70, 70, 70)
		Rect(x + Width - 26 * MenuScale, y, 26 * MenuScale, Height, True)
		
		Color(120, 120, 120)
		InBox% = MouseOn(x + Width - 23 * MenuScale, y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		If InBox Then Color(200, 200, 200)
		If ConsoleScrollDragging Then Color(255, 255, 255)
		Rect(x + Width - 23 * MenuScale, y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight, True)
		
		If Not MouseDown(1) Then
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
			ReissuePos = -ConsoleHeight + 15 * MenuScale
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
						ReissuePos = -ConsoleHeight + 15 * MenuScale
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
		
		If ConsoleScroll < -ConsoleHeight + Height Then ConsoleScroll = -ConsoleHeight + Height
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
			End If
			
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
							CreateConsoleMsg("- status")
							CreateConsoleMsg("- camerapick")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("- noblink")
							CreateConsoleMsg("- cheats")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- teleport [room name]")
							CreateConsoleMsg("- spawnitem [item name]")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- 173speed")
							CreateConsoleMsg("- 106speed")
							CreateConsoleMsg("- 173state")
							CreateConsoleMsg("- 106state")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help 2 / 3" + Chr(34) + " to find more commands.")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "2"
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 2 / 3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- spawn [npc type] [state]")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- scp-420-j")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- 096state")
							CreateConsoleMsg("- debughud")
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
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- camerafog [near] [far]")
							CreateConsoleMsg("- gamma [value]")
							CreateConsoleMsg("- injure [value]")
							CreateConsoleMsg("- infect [value]")
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
						Case "gamma"
							;[Block]
							CreateConsoleMsg("HELP - gamma")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the gamma correction.")
							CreateConsoleMsg("Should be set to a value between 0.0 and 2.0.")
							CreateConsoleMsg("Default is 1.0.")
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
						Case "cheats" 
							CreateConsoleMsg("HELP - cheats")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Actives GodMode, NoClip, NoTarget, InfiniteStamina and NoBlink")
							CreateConsoleMsg("is specified (on / off).")
							CreateConsoleMsg("******************************")
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
						Case "camerapick"
							;[Block]
							CreateConsoleMsg("HELP - camerapick")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Prints the texture name and coordinates of")
							CreateConsoleMsg("the model the camera is pointing at.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "status"
							;[Block]
							CreateConsoleMsg("HELP - status")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Prints player, camera, and room information.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "weed", "scp-420-j", "420j", "scp420-j", "scp-420j"
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
						Default
							;[Block]
							CreateConsoleMsg("There is no help available for that command.", 255, 150, 0)
							;[End Block]
					End Select
					;[End Block]
				Case "status"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					CreateConsoleMsg("******************************")
					CreateConsoleMsg("Status: ")
					CreateConsoleMsg("Coordinates: ")
					CreateConsoleMsg("    - collider: " + EntityX(Collider) + ", " + EntityY(Collider) + ", " + EntityZ(Collider))
					CreateConsoleMsg("    - camera: " + EntityX(Camera) + ", " + EntityY(Camera) + ", " + EntityZ(Camera))
				 	
					CreateConsoleMsg("Rotation: ")
					CreateConsoleMsg("    - collider: " + EntityPitch(Collider) + ", " + EntityYaw(Collider) + ", " + EntityRoll(Collider))
					CreateConsoleMsg("    - camera: " + EntityPitch(Camera) + ", " + EntityYaw(Camera)+", " + EntityRoll(Camera))
					
					CreateConsoleMsg("Room: " + PlayerRoom\RoomTemplate\Name)
					For ev.Events = Each Events
						If ev\room = PlayerRoom Then
							CreateConsoleMsg("Room event: " + ev\EventName)	
							CreateConsoleMsg("-    state: " + ev\EventState)
							CreateConsoleMsg("-    state2: " + ev\EventState2)	
							CreateConsoleMsg("-    state3: " + ev\EventState3)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("Room coordinates: " + Floor(EntityX(PlayerRoom\OBJ) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\OBJ) / 8.0 + 0.5))
					CreateConsoleMsg("Stamina: " + Stamina)
					CreateConsoleMsg("Death timer: " + KillTimer)					
					CreateConsoleMsg("Blinktimer: " + BlinkTimer)
					CreateConsoleMsg("Injuries: " + Injuries)
					CreateConsoleMsg("Bloodloss: " + Bloodloss)
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "camerapick"
					;[Block]
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					c = CameraPick(Camera, GraphicWidth / 2, GraphicHeight / 2)
					If c = 0 Then
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("No entity  picked")
						CreateConsoleMsg("******************************")								
					Else
						CreateConsoleMsg("******************************")
						CreateConsoleMsg("Picked entity:")
						SF = GetSurface(c, 1)
						b = GetSurfaceBrush(SF)
						t = GetBrushTexture(b, 0)
						TexName$ =  StripPath(TextureName(t))
						CreateConsoleMsg("Texture name: " + TexName)
						CreateConsoleMsg("Coordinates: " + EntityX(c) + ", " + EntityY(c) + ", " + EntityZ(c))
						CreateConsoleMsg("******************************")							
					EndIf
					;[End Block]
				Case "hidedistance"
					;[Block]
					HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("Hidedistance set to " + HideDistance)
					;[End Block]
				Case "ending"
					;[Block]
					SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					KillTimer = -0.1
					;[End Block]
				Case "noclipspeed"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					NoClipSpeed = Float(StrTemp)
					;[End Block]
				Case "injure"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Injuries = Float(StrTemp)
					;[End Block]
				Case "infect"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Infect = Float(StrTemp)
					;[End Block]
				Case "heal"
					;[Block]
					Injuries = 0.0
					Bloodloss = 0.0
					
					BlurTimer = 0.0
					
					Infect = 0.0
					
					DeathTimer = 0.0
					
					Stamina = 100.0
					
					For i = 0 To 5
						SCP1025State[i] = 0
					Next
					
					If I_427\Timer >= 70 * 360.0 Then I_427\Timer = 0.0
					
					For e.Events = Each Events
						If e\EventName = "1048a" Then 
							If PlayerRoom = e\room Then BlinkTimer = -10.0
							If e\room\Objects[0] <> 0 Then
								FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							EndIf
							RemoveEvent(e)
						EndIf
					Next
					;[End Block]
				Case "teleport"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							PositionEntity(Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.7, EntityZ(r\OBJ))
							ResetEntity(Collider)
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
						If (Lower(itt\Name) = StrTemp) Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(Collider), EntityY(Camera, True), EntityZ(Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						Else If (Lower(itt\TempName) = StrTemp) Then
							Temp = True
							CreateConsoleMsg(itt\Name + " spawned.")
							it.Items = CreateItem(itt\Name, itt\TempName, EntityX(Collider), EntityY(Camera, True), EntityZ(Collider))
							EntityType(it\Collider, HIT_ITEM)
							Exit
						End If
					Next
					
					If Temp = False Then CreateConsoleMsg("Item not found.", 255, 150, 0)
					;[End Block]
				Case "wireframe", "wf"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							WireframeState = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							WireframeState = False
							;[End Block]
						Default
							;[Block]
							WireframeState = Not WireframeState
							;[End Block]
					End Select
					
					If WireframeState Then
						CreateConsoleMsg("WIREFRAME ON")
					Else
						CreateConsoleMsg("WIREFRAME OFF")	
					EndIf
					
					WireFrame(WireframeState)
					;[End Block]
				Case "173speed"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr173\Speed = Float(StrTemp)
					CreateConsoleMsg("173's speed set to " + StrTemp)
					;[End Block]
				Case "106speed"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Curr106\Speed = Float(StrTemp)
					CreateConsoleMsg("106's speed set to " + StrTemp)
					;[End Block]
				Case "173state"
					;[Block]
					CreateConsoleMsg("SCP-173")
					CreateConsoleMsg("Position: " + EntityX(Curr173\OBJ) + ", " + EntityY(Curr173\OBJ) + ", " + EntityZ(Curr173\OBJ))
					CreateConsoleMsg("Idle: " + Curr173\Idle)
					CreateConsoleMsg("State: " + Curr173\State)
					;[End Block]
				Case "106state"
					;[Block]
					CreateConsoleMsg("SCP-106")
					CreateConsoleMsg("Position: " + EntityX(Curr106\OBJ) + ", " + EntityY(Curr106\OBJ) + ", " + EntityZ(Curr106\OBJ))
					CreateConsoleMsg("Idle: " + Curr106\Idle)
					CreateConsoleMsg("State: " + Curr106\State)
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
				Case "disable173"
					;[Block]
					Curr173\Idle = 3 ; ~ This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of 173 state checks.
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
					Contained106 = True
					;[End Block]
				Case "enable106"
					;[Block]
					Curr106\Idle = False
					Contained106 = False
					ShowEntity(Curr106\Collider)
					ShowEntity(Curr106\OBJ)
					;[End Block]
				Case "halloween"
					;[Block]
					HalloweenTex = Not HalloweenTex
					If HalloweenTex Then
						Local Tex% = LoadTexture_Strict("GFX\npcs\173h.pt", 1)
						
						EntityTexture(Curr173\OBJ, Tex, 0, 0)
						FreeTexture(Tex)
						CreateConsoleMsg("173 JACK-O-LANTERN ON")
					Else
						Local Tex2% = LoadTexture_Strict("GFX\npcs\173texture.jpg", 1)
						
						EntityTexture(Curr173\OBJ, Tex2, 0, 0)
						FreeTexture(Tex2)
						CreateConsoleMsg("173 JACK-O-LANTERN OFF")
					EndIf
					;[End Block]
				Case "sanic"
					;[Block]
					SuperMan = Not SuperMan
					If SuperMan = True Then
						CreateConsoleMsg("GOTTA GO FAST")
					Else
						CreateConsoleMsg("WHOA SLOW DOWN")
					EndIf
					;[End Block]
				Case "scp-420-j", "420", "weed", "scp420-j", "scp-420j"
					;[Block]
					For i = 1 To 20
						If Rand(2) = 1 Then
							it.Items = CreateItem("Some SCP-420-J", "scp420j", EntityX(Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						Else
							it.Items = CreateItem("Joint", "joint", EntityX(Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
						EndIf
						EntityType(it\Collider, HIT_ITEM)
					Next
					PlaySound_Strict(LoadTempSound("SFX\Music\420J.ogg"))
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
							chs\GodMode = Not chs\GodMode
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
					DropSpeed = -0.1
					HeadDropSpeed = 0.0
					Shake = 0.0
					CurrSpeed = 0.0
					
					HeartBeatVolume = 0.0
					
					CameraShake = 0.0
					Shake = 0.0
					LightFlash = 0.0
					BlurTimer = 0.0
					
					FallTimer = 0.0
					MenuOpen = False
					
					chs\InfiniteStamina = False
					chs\NoClip = False
					chs\NoBlink = False
					chs\NoTarget = False
					chs\Cheats = False
					
					; ~ If death by SCP-173, enable GodMode, prevent instant death again -- Salvage
					If Curr173\Idle Then
						CreateConsoleMsg("Death by SCP-173 causes GodMode to be enabled!")
						chs\GodMode = True
						Curr173\Idle = False
					Else
						chs\GodMode = False
					EndIf
					
					ShowEntity(Collider)
					
					KillTimer = 0.0
					KillAnim = 0.0
					;[End Block]
				Case "noclip", "fly"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\NoClip = True
							Playable = True
							UnableToMove = False
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							chs\NoClip = False	
							RotateEntity(Collider, 0.0, EntityYaw(Collider), 0.0)
							;[End Block]
						Default
							;[Block]
							chs\NoClip = Not chs\NoClip
							If chs\NoClip = False Then		
								RotateEntity(Collider, 0.0, EntityYaw(Collider), 0.0)
							Else
								Playable = True
								UnableToMove = False
							EndIf
							;[End Block]
					End Select
					
					If chs\NoClip Then
						CreateConsoleMsg("NOCLIP ON")
					Else
						CreateConsoleMsg("NOCLIP OFF")
					EndIf
					
					DropSpeed = 0.0
					;[End Block]
				Case "noblink", "nb"
					;[Block]
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
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
							chs\NoBlink = Not chs\NoBlink
							;[End Block]
					End Select	
					If chs\NoBlink Then
						CreateConsoleMsg("NOBLINK ON")
					Else
						CreateConsoleMsg("NOBLINK OFF")	
					EndIf
					;[End Block]
				Case "cheats" 
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							chs\Cheats = True	
							;[End Block]
						Case "off", "0", "false"
							;[Block]
	                        chs\Cheats = False
							;[End Block]
						Default
							;[Block]
							chs\Cheats = Not chs\Cheats
							;[End Block]
					End Select	
					
					If chs\Cheats = True Then
                        chs\GodMode = True
                        chs\NoTarget = True
                        chs\NoClip = True
                        chs\InfiniteStamina = True
                        chs\NoBlink = True
						CreateConsoleMsg("CHEATS ON")
					Else
					    chs\GodMode = False
                        chs\NoTarget = False
                        chs\NoClip = False
                        chs\InfiniteStamina = False
                        chs\NoBlink = False
						CreateConsoleMsg("CHEATS OFF")	
					EndIf
					;[End Block]
				Case "096state"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							CreateConsoleMsg("SCP-096")
							CreateConsoleMsg("Position: " + EntityX(n\OBJ) + ", " + EntityY(n\OBJ) + ", " + EntityZ(n\OBJ))
							CreateConsoleMsg("Idle: " + n\Idle)
							CreateConsoleMsg("State: " + n\State)
							Exit
						EndIf
					Next
					CreateConsoleMsg("SCP-096 has not spawned.")
					;[End Block]
				Case "debughud"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							DebugHUD = True
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							DebugHUD = False
							;[End Block]
						Default
							;[Block]
							DebugHUD = Not DebugHUD
							;[End Block]
					End Select
					
					If DebugHUD Then
						CreateConsoleMsg("Debug Mode On")
					Else
						CreateConsoleMsg("Debug Mode Off")
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
						If e\EventName = "alarm" Then 
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
					Args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CameraFogNear = Float(Left(Args, Len(Args) - Instr(Args, " ")))
					CameraFogFar = Float(Right(Args, Len(Args) - Instr(Args, " ")))
					CreateConsoleMsg("Near set to: " + CameraFogNear + ", far set to: " + CameraFogFar)
					;[End Block]
				Case "gamma"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					ScreenGamma = Int(StrTemp)
					CreateConsoleMsg("Gamma set to " + ScreenGamma)
					;[End Block]
				Case "spawn"
					;[Block]
					Args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args$, 1)
					StrTemp2$ = Piece(Args$, 2)
					
					; ~ Hacky fix for when the user doesn't input a second parameter.
					If (StrTemp <> StrTemp2) Then
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
							chs\InfiniteStamina = Not chs\InfiniteStamina
							;[End Block]
					End Select
					
					If chs\InfiniteStamina
						CreateConsoleMsg("INFINITE STAMINA ON")
					Else
						CreateConsoleMsg("INFINITE STAMINA OFF")	
					EndIf
					;[End Block]
				Case "toggle_warhead_lever"
					;[Block]
					For e.Events = Each Events
						If e\EventName = "room2nuke" Then
							e\EventState = (Not e\EventState)
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
									e\EventState3 = 1
									e\room\RoomDoors[1]\Open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg("Gate A is now unlocked.")	
							;[End Block]
						Case "b"
							;[Block]
							For e.Events = Each Events
								If e\EventName = "exit1" Then
									e\EventState3 = 1
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
									e\EventState3 = 1
									e\room\RoomDoors[1]\Open = True
								ElseIf e\EventName = "exit1" Then
									e\EventState3 = 1
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
					KillTimer = -1.0
					Select Rand(4)
						Case 1
							;[Block]
							DeathMsg = "[DATA REDACTED]"
							;[End Block]
						Case 2
							;[Block]
							DeathMsg = "Subject D-9341 found dead in Sector [DATA REDACTED]. "
							DeathMsg = DeathMsg + "The subject appears to have attained no physical damage, and there is no visible indication as to what killed him. "
							DeathMsg = DeathMsg + "Body was sent for autopsy."
							;[End Block]
						Case 3
							;[Block]
							DeathMsg = "EXCP_ACCESS_VIOLATION"
							;[End Block]
						Case 4
							;[Block]
							DeathMsg = "Subject D-9341 found dead in Sector [DATA REDACTED]. "
							DeathMsg = DeathMsg + "The subject appears to have scribbled the letters " + Chr(34) + "kys" + Chr(34) + " in his own blood beside him. "
							DeathMsg = DeathMsg + "No other signs of physical trauma or struggle can be observed. Body was sent for autopsy."
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
					
					If StrTemp <> ""
						PlayCustomMusic = True
						If CustomMusic <> 0 Then FreeSound_Strict(CustomMusic) : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel(MusicCHN)
						CustomMusic = LoadSound_Strict("SFX\Music\Custom\" + StrTemp)
						If CustomMusic = 0
							PlayCustomMusic = False
						EndIf
					Else
						PlayCustomMusic = False
						If CustomMusic <> 0 Then FreeSound_Strict(CustomMusic) : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel(MusicCHN)
					EndIf
					;[End Block]
				Case "tp"
					;[Block]
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtypeMTF
							If n\MTFLeader = Null
								PositionEntity(Collider, EntityX(n\Collider), EntityY(n\Collider) + 5.0, EntityZ(n\Collider))
								ResetEntity(Collider)
								Exit
							EndIf
						EndIf
					Next
					;[End Block]
				Case "tele"
					;[Block]
					Args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args$, 1, " ")
					StrTemp2$ = Piece(Args$, 2, " ")
					StrTemp3$ = Piece(Args$, 3, " ")
					PositionEntity(Collider, Float(StrTemp), Float(StrTemp2$), Float(StrTemp3$))
					PositionEntity(Camera, Float(StrTemp), Float(StrTemp2$), Float(StrTemp3$))
					ResetEntity(Collider)
					ResetEntity(Camera)
					CreateConsoleMsg("Teleported to coordinates (X|Y|Z): " + EntityX(Collider) + "|" + EntityY(Collider) + "|" + EntityZ(Collider))
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
							chs\NoTarget = Not chs\NoTarget
							;[End Block]
					End Select
					
					If chs\NoTarget = False Then
						CreateConsoleMsg("NOTARGET OFF")
					Else
						CreateConsoleMsg("NOTARGET ON")	
					EndIf
					;[End Block]
				Case "spawnradio"
					;[Block]
					it.Items = CreateItem("Radio Transceiver", "fineradio", EntityX(Collider), EntityY(Camera, True), EntityZ(Collider))
					EntityType(it\Collider, HIT_ITEM)
					it\State = 101.0
					;[End Block]
				Case "spawnnvg"
					;[Block]
					it.Items = CreateItem("Night Vision Goggles", "nvgoggles", EntityX(Collider), EntityY(Camera, True), EntityZ(Collider))
					EntityType(it\Collider, HIT_ITEM)
					it\State = 1000.0
					;[End Block]
				Case "spawnpumpkin", "pumpkin"
					;[Block]
					CreateConsoleMsg("What pumpkin?")
					;[End Block]
				Case "spawnnav"
					;[Block]
					it.Items = CreateItem("S-NAV Navigator Ultimate", "nav", EntityX(Collider), EntityY(Camera, True), EntityZ(Collider))
					EntityType(it\Collider, HIT_ITEM)
					it\State = 101.0
					;[End Block]
				Case "teleport173"
					;[Block]
					PositionEntity(Curr173\Collider, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
					ResetEntity(Curr173\Collider)
					;[End Block]
				Case "seteventstate"
					;[Block]
					Args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp = Piece(Args$, 1, " ")
					StrTemp2$ = Piece(Args$, 2, " ")
					StrTemp3$ = Piece(Args$, 3, " ")
					
					Local PL_Room_Found% = False
					
					If StrTemp = "" Or StrTemp2 = "" Or StrTemp3 = ""
						CreateConsoleMsg("Too few parameters. This command requires 3.", 255, 150, 0)
					Else
						For e.Events = Each Events
							If e\room = PlayerRoom
								If Lower(StrTemp) <> "keep"
									e\EventState = Float(StrTemp)
								EndIf
								If Lower(StrTemp2) <> "keep"
									e\EventState2 = Float(StrTemp2)
								EndIf
								If Lower(StrTemp3) <> "keep"
									e\EventState3 = Float(StrTemp3)
								EndIf
								CreateConsoleMsg("Changed event states from current player room to: " + e\EventState + "|" + e\EventState2 + "|" + e\EventState3)
								PL_Room_Found = True
								Exit
							EndIf
						Next
						If (Not PL_Room_Found)
							CreateConsoleMsg("The current room doesn't has any event applied.", 255, 150, 0)
						EndIf
					EndIf
					;[End Block]
				Case "spawnparticles"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					If Int(StrTemp) > -1 And Int(StrTemp) =< 1 ; ~ This is the maximum ID of particles by Devil Particle system, will be increased after time -- ENDSHN
						SetEmitter(Collider, ParticleEffect[Int(StrTemp)])
						CreateConsoleMsg("Spawned particle emitter with ID " + Int(StrTemp) + " at player's position.")
					Else
						CreateConsoleMsg("Particle emitter with ID " + Int(StrTemp) + " not found.", 255, 150, 0)
					EndIf
					;[End Block]
				Case "giveachievement"
					;[Block]
					If Instr(ConsoleInput, " ") <> 0 Then
						StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp = ""
					EndIf
					
					If Int(StrTemp) >= 0 And Int(StrTemp) < MAXACHIEVEMENTS
						Achievements(Int(StrTemp)) = True
						CreateConsoleMsg("Achievemt " + AchievementStrings(Int(StrTemp)) + " unlocked.")
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
				Case "setblinkeffect"
					;[Block]
					Args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					BlinkEffect = Float(Left(Args, Len(Args) - Instr(Args, " ")))
					BlinkEffectTimer = Float(Right(Args, Len(Args) - Instr(Args, " ")))
					CreateConsoleMsg("Set BlinkEffect to: " + BlinkEffect + "and BlinkEffect timer: " + BlinkEffectTimer)
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
		End If
		
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
						AAText(x + 20 * MenuScale, TempY, "> " + cm\Txt)
					Else
						AAText(x + 20 * MenuScale, TempY, cm\Txt)
					EndIf
				EndIf
				TempY = TempY - 15.0 * MenuScale
			EndIf
		Next
		Color(255, 255, 255)
		
		If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	End If
	
	AASetFont(Font1)
End Function

ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
CreateConsoleMsg("Console commands: ")
CreateConsoleMsg("  - teleport [room name]")
CreateConsoleMsg("  - godmode [on / off]")
CreateConsoleMsg("  - noclip [on / off]")
CreateConsoleMsg("  - cheats [on / off]")
CreateConsoleMsg("  - infinitestamina[on / off]")
CreateConsoleMsg("  - noblink [on / off]")
CreateConsoleMsg("  - notarget [on / off]")
CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
CreateConsoleMsg("  - wireframe [on / off]")
CreateConsoleMsg("  - debughud [on / off]")
CreateConsoleMsg("  - camerafog [near] [far]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - status")
CreateConsoleMsg("  - heal")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - spawnitem [item name]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - 173speed [x] (default = 35)")
CreateConsoleMsg("  - disable173 / enable173")
CreateConsoleMsg("  - disable106 / enable106")
CreateConsoleMsg("  - 173state / 106state / 096state")
CreateConsoleMsg("  - spawn [NPC type]")

Global DebugHUD%

Global BlurVolume#, BlurTimer#

Global LightBlink#, LightFlash#

Global Camera%, CameraShake#, CurrCameraZoom#

Global StoredCameraFogFar# = CameraFogFar

Include "Source Code\Dreamfilter.bb"

Dim LightSpriteTex(10)

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

; ~ The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Dim Music$(40)
Music(0) = "The Dread"
Music(1) = "HeavyContainment"
Music(2) = "EntranceZone"
Music(3) = "PD"
Music(4) = "079"
Music(5) = "GateB1"
Music(6) = "GateB2"
Music(7) = "Room3Storage"
Music(8) = "Room049"
Music(9) = "8601"
Music(10) = "106"
Music(11) = "Menu"
Music(12) = "8601Cancer"
Music(13) = "Intro"
Music(14) = "178"
Music(15) = "PDTrench"
Music(16) = "205"
Music(17) = "GateA"
Music(18) = "1499"
Music(19) = "1499Danger"
Music(20) = "049Chase"
Music(21) = "..\Ending\MenuBreath"
Music(22) = "914"
Music(23) = "Ending"
Music(24) = "Credits"
Music(25) = "SaveMeFrom"
Music(26) = "Room106"

Global MusicCHN%
MusicCHN = StreamSound_Strict("SFX\Music\" + Music(2) + ".ogg", MusicVolume, Mode)

Global CurrMusicVolume# = 1.0, NowPlaying% = 2, ShouldPlay% = 11
Global CurrMusic% = 1

DrawLoading(10, True)

Dim OpenDoorSFX%(3, 3), CloseDoorSFX%(3, 3)
Dim BigDoorErrorSFX%(3)

Global KeyCardSFX1% 
Global KeyCardSFX2% 
Global ButtonSFX2% 
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

Dim DecaySFX%(5)

Global BurstSFX% 

DrawLoading(20, True)

Dim RustleSFX%(3)

Global Use914SFX%
Global Death914SFX% 

Dim DripSFX%(4)

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX%(5, 10) 

Global RadioSquelch% 
Global RadioStatic% 
Global RadioBuzz% 

Global ElevatorBeepSFX%, ElevatorMoveSFX% 

Dim PickSFX%(10)

Global AmbientSFXCHN%, CurrAmbientSFX%
Dim AmbientSFXAmount%(6)
; ~ 0 = Light Containment Zone
; ~ 1 = Heavy Containment Zone
; ~ 2 = Entrance Zone
; ~ 3 = General
; ~ 4 = Pre-Breach
; ~ 5 = SCP-860-1

AmbientSFXAmount(0) = 8 
AmbientSFXAmount(1) = 11
AmbientSFXAmount(2) = 12
AmbientSFXAmount(3) = 15 
AmbientSFXAmount(4) = 5
AmbientSFXAmount(5) = 10

Dim AmbientSFX%(6, 15)

Dim OldManSFX%(8)

Dim Scp173SFX%(3)

Dim HorrorSFX%(20)

DrawLoading(25, True)

Dim IntroSFX%(12)

Dim AlarmSFX%(5)

Dim CommotionState%(25)

Global HeartBeatSFX% 

Global VomitSFX%

Dim BreathSFX%(2, 5)
Global BreathCHN%

Dim NeckSnapSFX%(3)

Dim DamageSFX%(9)

Dim MTFSFX%(8)

Dim CoughSFX%(3)
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX%
Global CurrStepSFX%
Dim StepSFX%(5, 2, 8) ; ~ (Normal / Metal, Walk / Run, ID)

Dim Step2SFX(6)

DrawLoading(30, True)

Global PlayCustomMusic% = False, CustomMusic% = 0

Global Monitor2%, Monitor3%, MonitorTexture2%, MonitorTexture3%, MonitorTexture4%, MonitorTextureOff%
Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

; ~ This variable is for when a camera detected the player
; ~ False: Player is not seen (will be set after every call of the Main Loop
; ~ True: The Player got detected by a camera
Global PlayerDetected%
Global PrevInjuries#, PrevBloodloss#

Global NVGImages% = LoadAnimImage("GFX\battery.png", 64, 64, 0, 2)
MaskImage(NVGImages, 255, 0, 255)

Global Wearing1499% = False
Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Dim UserTrackName$(256)

Global NTF_1499PrevX#
Global NTF_1499PrevY#
Global NTF_1499PrevZ#
Global NTF_1499PrevRoom.Rooms
Global NTF_1499X#
Global NTF_1499Y#
Global NTF_1499Z#
Global NTF_1499Sky%

Global OptionsMenu% = 0
Global QuitMsg% = 0

Global InFacility% = True

Global PrevMusicVolume# = MusicVolume
Global PrevSFXVolume# = SFXVolume
Global DeafPlayer% = False
Global DeafTimer# = 0.0

Global IsZombie% = False

Global room2gw_BrokenDoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Dim NavImages(5)
For i = 0 To 3
	NavImages(i) = LoadImage_Strict("GFX\navigator\roomborder" + i + ".png")
	MaskImage(NavImages(i), 255, 0, 255)
Next
NavImages(4) = LoadImage_Strict("GFX\navigator\batterymeter.png")

Global NavBG = CreateImage(GraphicWidth, GraphicHeight)

Global ParticleEffect%[10]

Global DTextures%[MaxDTextures]

Global NPC049OBJ%, NPC0492OBJ%
Global ClerkOBJ%

Global IntercomStreamCHN%

Global ForestNPC%, ForestNPCTex%, ForestNPCData#[3]

Global PauseMenuIMG%

Global SprintIcon%
Global BlinkIcon%
Global CrouchIcon%
Global HandIcon%
Global HandIcon2%

Global StaminaMeterIMG%

Global KeypadHUD

Global Panel294, Using294%, Input294$

DrawLoading(35, True)

Include "Source Code\Items_System.bb"

Include "Source Code\Particles_System.bb"

Global ClosestButton%, ClosestDoor.Doors
Global SelectedDoor.Doors, UpdateDoorsTimer#
Global DoorTempID%

Type Doors
	Field OBJ%, OBJ2%, FrameOBJ%, Buttons%[2]
	Field Locked%, Locked079%, Open%, Angle%, OpenState#, FastOpen%
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
	Field DoorHitOBJ%
End Type 

Dim BigDoorOBJ%(2), HeavyDoorOBJ%(2)
Dim OBJTunnel%(7)

Function CreateDoor.Doors(LVL, x#, y#, z#, Angle#, room.Rooms, dOpen% = False, Big% = False, Keycard% = False, Code$ = "", UseCollisionMesh% = False)
	Local d.Doors, Parent%, i%
	
	If room <> Null Then Parent = room\OBJ
	
	Local d2.Doors
	
	d.Doors = New Doors
	If Big = 1 Then
		d\OBJ = CopyEntity(BigDoorOBJ(0))
		ScaleEntity(d\OBJ, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		d\OBJ2 = CopyEntity(BigDoorOBJ(1))
		ScaleEntity(d\OBJ2, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		
		d\FrameOBJ = CopyEntity(DoorColl)				
		ScaleEntity(d\FrameOBJ, RoomScale, RoomScale, RoomScale)
		EntityType(d\FrameOBJ, HIT_MAP)
		EntityAlpha(d\FrameOBJ, 0.0)
	ElseIf Big = 2 Then
		d\OBJ = CopyEntity(HeavyDoorOBJ(0))
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(HeavyDoorOBJ(1))
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(DoorFrameOBJ)
	ElseIf Big = 3 Then
		For d2 = Each Doors
			If d2 <> d And d2\Dir = 3 Then
				d\OBJ = CopyEntity(d2\OBJ)
				d\OBJ2 = CopyEntity(d2\OBJ2)
				ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
				ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
				Exit
			EndIf
		Next
		If d\OBJ = 0 Then
			d\OBJ = LoadMesh_Strict("GFX\map\elevatordoor.b3d")
			d\OBJ2 = CopyEntity(d\OBJ)
			ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
			ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		EndIf
		d\FrameOBJ = CopyEntity(DoorFrameOBJ)
	Else
		d\OBJ = CopyEntity(DoorOBJ)
		ScaleEntity(d\OBJ, (204.0 * RoomScale) / MeshWidth(d\OBJ), 312.0 * RoomScale / MeshHeight(d\OBJ), 16.0 * RoomScale / MeshDepth(d\OBJ))
		d\OBJ2 = CopyEntity(DoorOBJ)
		ScaleEntity(d\OBJ2, (204.0 * RoomScale) / MeshWidth(d\OBJ), 312.0 * RoomScale / MeshHeight(d\OBJ), 16.0 * RoomScale / MeshDepth(d\OBJ))
		
		d\FrameOBJ = CopyEntity(DoorFrameOBJ)
	End If
	
	PositionEntity(d\FrameOBJ, x, y, z)
	ScaleEntity(d\FrameOBJ, (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
	EntityPickMode(d\FrameOBJ, 2)
	EntityType(d\OBJ, HIT_MAP)
	EntityType(d\OBJ2, HIT_MAP)
	
	d\ID = DoorTempID
	DoorTempID = DoorTempID + 1
	
	d\KeyCard = Keycard
	d\Code = Code
	d\Level = LVL
	d\LevelDest = 66
	
	For i = 0 To 1
		If Code <> "" Then 
			d\Buttons[i] = CopyEntity(ButtonCodeOBJ)
			EntityFX(d\Buttons[i], 1)
		Else
			If Keycard > 0 Then
				d\Buttons[i] = CopyEntity(ButtonKeyOBJ)
			ElseIf Keycard < 0
				d\Buttons[i] = CopyEntity(ButtonScannerOBJ)	
			Else
				d\Buttons[i] = CopyEntity(ButtonOBJ)
			End If
		EndIf
		ScaleEntity(d\Buttons[i], 0.03, 0.03, 0.03)
	Next
	
	If Big = 1 Then
		PositionEntity(d\Buttons[0], x - 432.0 * RoomScale, y + 0.7, z + 192.0 * RoomScale)
		RotateEntity(d\Buttons[0], 0.0, 90.0, 0.0)
		PositionEntity(d\Buttons[1], x + 432.0 * RoomScale, y + 0.7, z - 192.0 * RoomScale)
		RotateEntity(d\Buttons[1], 0.0, 270.0, 0.0)
	Else
		PositionEntity(d\Buttons[0], x + 0.6, y + 0.7, z - 0.1)
		PositionEntity(d\Buttons[1], x - 0.6, y + 0.7, z + 0.1)
		RotateEntity(d\Buttons[1], 0.0, 180.0, 0.0)
	End If
	EntityParent(d\Buttons[0], d\FrameOBJ)
	EntityParent(d\Buttons[1], d\FrameOBJ)
	EntityPickMode(d\Buttons[0], 2)
	EntityPickMode(d\Buttons[1], 2)
	
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
	d\Open = dOpen		
	
	EntityPickMode(d\OBJ, 2)
	If d\OBJ2 <> 0 Then
		EntityPickMode(d\OBJ2, 2)
	EndIf
	
	EntityPickMode(d\FrameOBJ, 2)
	
	If d\Open And Big = False And Rand(8) = 1 Then d\AutoClose = True
	d\Dir = Big
	d\room = room
	
	d\MTFClose = True
	
	If UseCollisionMesh Then
		For d2.Doors = Each Doors
			If d2 <> d Then
				If d2\DoorHitOBJ <> 0 Then
					d\DoorHitOBJ = CopyEntity(d2\DoorHitOBJ, d\FrameOBJ)
					EntityAlpha(d\DoorHitOBJ, 0.0)
					EntityFX(d\DoorHitOBJ, 1)
					EntityType(d\DoorHitOBJ, HIT_MAP)
					EntityColor(d\DoorHitOBJ, 255, 0, 0)
					HideEntity(d\DoorHitOBJ)
					Exit
				EndIf
			EndIf
		Next
		If d\DoorHitOBJ = 0 Then
			d\DoorHitOBJ = LoadMesh_Strict("GFX\doorhit.b3d", d\FrameOBJ)
			EntityAlpha(d\DoorHitOBJ, 0.0)
			EntityFX(d\DoorHitOBJ, 1)
			EntityType(d\DoorHitOBJ, HIT_MAP)
			EntityColor(d\DoorHitOBJ, 255, 0, 0)
			HideEntity(d\DoorHitOBJ)
		EndIf
	EndIf
	Return(d)
End Function

Function CreateButton(x#, y#, z#, Pitch#, Yaw#, Roll# = 0.0)
	Local OBJ% = CopyEntity(ButtonOBJ)	
	
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
			Local xDist# = Abs(EntityX(Collider) - EntityX(d\OBJ, True))
			Local zDist# = Abs(EntityZ(Collider) - EntityZ(d\OBJ, True))
			
			d\Dist = xDist + zDist
			
			If d\Dist > HideDistance * 2 Then
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
		UpdateDoorsTimer = Max(UpdateDoorsTimer - FPSfactor, 0.0)
	EndIf
	
	ClosestButton = 0
	ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\Dist < HideDistance * 2 Or d\IsElevatorDoor > 0 Then ; ~ Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel -- ENDSHN
			If (d\OpenState >= 180.0 Or d\OpenState =< 0.0) And GrabbedEntity = 0 Then
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then
						If Abs(EntityX(Collider) - EntityX(d\Buttons[i], True)) < 1.0 Then 
							If Abs(EntityZ(Collider) - EntityZ(d\Buttons[i], True)) < 1.0 Then 
								Dist = Distance(EntityX(Collider, True), EntityZ(Collider, True), EntityX(d\Buttons[i], True), EntityZ(d\Buttons[i], True))
								If Dist < 0.7 Then
									Local Temp% = CreatePivot()
									
									PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
									PointEntity(Temp, d\Buttons[i])
									
									If EntityPick(Temp, 0.6) = d\Buttons[i] Then
										If ClosestButton = 0 Then
											ClosestButton = d\Buttons[i]
											ClosestDoor = d
										Else
											If Dist < EntityDistance(Collider, ClosestButton) Then ClosestButton = d\Buttons[i] : ClosestDoor = d
										End If							
									End If
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
							d\OpenState = Min(180.0, d\OpenState + FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * FPSfactor / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * FPSfactor / 80.0, 0.0, 0.0)	
							;[End Block]
						Case 1
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + FPSfactor * 0.8)
							MoveEntity(d\OBJ, Sin(d\OpenState) * FPSfactor / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, -Sin(d\OpenState) * FPSfactor / 180.0, 0.0, 0.0)
							;[End Block]
						Case 2
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen + 1) * FPSfactor / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * FPSfactor / 120.0, 0.0, 0.0)
							;[End Block]
						Case 3
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * FPSfactor / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState)* (d\FastOpen * 2 + 1) * FPSfactor / 162.0, 0.0, 0.0)
							;[End Block]
						Case 4 ;Used for 914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + FPSfactor * 1.4)
							MoveEntity(d\OBJ, Sin(d\OpenState) * FPSfactor / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
				Else
					d\FastOpen = 0
					ResetEntity(d\OBJ)
					If d\OBJ2 <> 0 Then ResetEntity(d\OBJ2)
					If d\TimerState > 0.0 Then
						d\TimerState = Max(0.0, d\TimerState - FPSfactor)
						If d\TimerState + FPSfactor > 110.0 And d\TimerState =< 110.0 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\OBJ)
						
						Local Sound%
						
						Sound = Rand(0, 2)
						If d\TimerState = 0.0 Then 
							d\Open = (Not d\Open)
							d\SoundCHN = PlaySound2(CloseDoorSFX(d\Dir, Sound), Camera, d\OBJ)
						EndIf
					EndIf
					If d\AutoClose And RemoteDoorOn = True Then
						If EntityDistance(Camera, d\OBJ) < 2.1 Then
							If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then PlaySound_Strict(HorrorSFX(7))
							d\Open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\Dir, 1), Rand(0, 2)), Camera, d\OBJ) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\OpenState > 0.0 Then
					Select d\Dir
						Case 0
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-FPSfactor) * (d\FastOpen + 1) / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-FPSfactor) / 80.0, 0.0, 0.0)	
							;[End Block]
						Case 1
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - FPSfactor * 0.8)
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-FPSfactor) / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * FPSfactor / 180.0, 0.0, 0.0)
							If d\OpenState < 15.0 And d\OpenState + FPSfactor >= 15.0
								If ParticleAmount = 2
									For i = 0 To Rand(75, 99)
										Local Pvt% = CreatePivot()
										
										PositionEntity(Pvt, EntityX(d\FrameOBJ, True) + Rnd(-0.2, 0.2), EntityY(d\FrameOBJ, True) + Rnd(0.0, 1.2), EntityZ(d\FrameOBJ, True) + Rnd(-0.2, 0.2))
										RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
										
										Local p.Particles = CreateParticle(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 2, 0.002, 0.0, 300)
										
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
							d\OpenState = Max(0.0, d\OpenState - FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-FPSfactor) * (d\FastOpen + 1) / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-FPSfactor) / 120.0, 0.0, 0.0)
							;[End Block]
						Case 3
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - FPSfactor * 2.0 * (d\FastOpen + 1))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-FPSfactor) * (d\FastOpen + 1) / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-FPSfactor) / 162.0, 0.0, 0.0)
							;[End Block]
						Case 4 ;Used for 914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState - FPSfactor * 1.4)
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-FPSfactor) / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
					
					If d\Angle = 0.0 Or d\Angle = 180.0 Then
						If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(Collider)) < 0.15 Then
							If Abs(EntityX(d\FrameOBJ, True) - EntityX(Collider)) < 0.7 * (d\Dir * 2 + 1) Then
								z = CurveValue(EntityZ(d\FrameOBJ, True) + 0.15 * Sgn(EntityZ(Collider) - EntityZ(d\FrameOBJ, True)), EntityZ(Collider), 5)
								PositionEntity(Collider, EntityX(Collider), EntityY(Collider), z)
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\FrameOBJ, True) - EntityX(Collider)) < 0.15 Then	
							If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(Collider)) < 0.7 * (d\Dir * 2 + 1) Then
								x = CurveValue(EntityX(d\FrameOBJ, True) + 0.15 * Sgn(EntityX(Collider) - EntityX(d\FrameOBJ, True)), EntityX(Collider), 5)
								PositionEntity(Collider, x, EntityY(Collider), EntityZ(Collider))
							EndIf
						EndIf
					EndIf
					
					If d\DoorHitOBJ <> 0 Then
						ShowEntity(d\DoorHitOBJ)
					EndIf
				Else
					d\FastOpen = 0
					PositionEntity(d\OBJ, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
					If d\OBJ2 <> 0 Then PositionEntity(d\OBJ2, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
					If d\OBJ2 <> 0 And d\Dir = 0 Then
						MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
						MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
					EndIf
					If d\DoorHitOBJ <> 0 Then
						HideEntity(d\DoorHitOBJ)
					EndIf
				EndIf
			EndIf
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\FrameOBJ)
		
		If d\Dir = 1 And d\Locked079 Then
			If d\OpenState > 48.0 Then
				d\Open = False
				d\OpenState = Min(d\OpenState, 48.0)
			EndIf	
		EndIf
		
		If d\DoorHitOBJ <> 0 Then
			If DebugHUD Then
				EntityAlpha(d\DoorHitOBJ, 0.5)
			Else
				EntityAlpha(d\DoorHitOBJ, 0.0)
			EndIf
		EndIf
	Next
End Function

Function UseDoor(d.Doors, ShowMsg% = True, PlaySFX% = True)
	Local Temp% = 0
	
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If ShowMsg = True Then
				If (Instr(Msg, "The keycard") = 0 And Instr(Msg, "A keycard with") = 0) Or (MsgTimer < 70 * 3.0) Then
					Msg = "A keycard is required to operate this door."
					MsgTimer = 70 * 7.0
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\ItemTemplate\TempName
				Case "key1"
					;[Block]
					Temp = 1
					;[End Block]
				Case "key2"
					;[Block]
					Temp = 2
					;[End Block]
				Case "key3"
					;[Block]
					Temp = 3
					;[End Block]
				Case "key4"
					;[Block]
					Temp = 4
					;[End Block]
				Case "key5"
					;[Block]
					Temp = 5
					;[End Block]
				Case "key6"
					;[Block]
					Temp = 6
					;[End Block]
				Default
					;[Block]
					Temp = -1
					;[End Block]
			End Select
			
			If Temp = -1 Then 
				If ShowMsg = True Then
					If (Instr(Msg, "The keycard") = 0 And Instr(Msg, "A keycard with") = 0) Or (MsgTimer < 70 * 3.0) Then
						Msg = "A keycard is required to operate this door."
						MsgTimer = 70 * 7.0
					EndIf
				EndIf
				Return				
			ElseIf Temp >= d\KeyCard 
				SelectedItem = Null
				If ShowMsg = True Then
					If d\Locked Then
						PlaySound_Strict(KeyCardSFX2)
						Msg = "The keycard was inserted into the slot but nothing happened."
						MsgTimer = 70 * 7.0
						Return
					Else
						PlaySound_Strict(KeyCardSFX1)
						Msg = "The keycard was inserted into the slot."
						MsgTimer = 70 * 7.0
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If ShowMsg = True Then 
					PlaySound_Strict(KeyCardSFX2)					
					If d\Locked Then
						Msg = "The keycard was inserted into the slot but nothing happened."
					Else
						Msg = "A keycard with security clearance " + d\KeyCard + " or higher is required to operate this door."
					EndIf
					MsgTimer = 70 * 7.0					
				EndIf
				Return
			End If
		EndIf	
	ElseIf d\KeyCard < 0
		; ~ I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable - risingstar64
		If SelectedItem <> Null Then
			Temp = (SelectedItem\ItemTemplate\TempName = "hand" And d\KeyCard = -1) Or (SelectedItem\ItemTemplate\TempName = "hand2" And d\KeyCard = -2)
		EndIf
		SelectedItem = Null
		If Temp <> 0 Then
			PlaySound_Strict(ScannerSFX1)
			If (Instr(Msg, "You placed your") = 0) Or (MsgTimer < 70 * 3.0) Then
				Msg = "You place the palm of the hand onto the scanner. The scanner reads: " + Chr(34) + "DNA verified. Access granted." + Chr(34)
			EndIf
			MsgTimer = 70 * 10.0
		Else
			If ShowMsg = True Then 
				PlaySound_Strict(ScannerSFX2)
				Msg = "You placed your palm onto the scanner. The scanner reads: " + Chr(34) + "DNA does not match known sample. Access denied." + Chr(34)
				MsgTimer = 70 * 10.0
			EndIf
			Return			
		EndIf
	Else
		If d\Locked Then
			If ShowMsg = True Then 
				If Not (d\IsElevatorDoor > 0) Then
					PlaySound_Strict(ButtonSFX2)
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
                        If d\Open Then
                            Msg = "You pushed the button but nothing happened."
                        Else    
                            Msg = "The door appears to be locked."
                        EndIf    
                    Else
                        Msg = "The elevator appears to be broken."
                    EndIf
					MsgTimer = 70 * 5.0
				Else
					If d\IsElevatorDoor = 1 Then
						Msg = "You called the elevator."
						MsgTimer = 70 * 5.0
					ElseIf d\IsElevatorDoor = 3 Then
						Msg = "The elevator is already on this floor."
						MsgTimer = 70 * 5.0
					ElseIf (Msg <> "You called the elevator.")
						If (Msg = "You already called the elevator.") Or (MsgTimer < 70 * 3.0)	
							Select Rand(10)
								Case 1
									;[Block]
									Msg = "Stop spamming the button."
									MsgTimer = 70 * 7.0
									;[End Block]
								Case 2
									;[Block]
									Msg = "Pressing it harder does not make the elevator come faster."
									MsgTimer = 70 * 7.0
									;[End Block]
								Case 3
									;[Block]
									Msg = "If you continue pressing this button I will generate a Memory Access Violation."
									MsgTimer = 70 * 7.0
									;[End Block]
								Default
									;[Block]
									Msg = "You already called the elevator."
									MsgTimer = 70 * 7.0
									;[End Block]
							End Select
						EndIf
					Else
						Msg = "You already called the elevator."
						MsgTimer = 70 * 7.0
					EndIf
				EndIf
			EndIf
			Return
		EndIf	
	EndIf
	
	d\Open = (Not d\Open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\Open = (Not d\LinkedDoor\Open)
	
	Local Sound% = 0
	
	Sound = Rand(0, 2)
	
	If PlaySFX = True Then
		If d\Open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\TimerState = d\LinkedDoor\Timer
			d\TimerState = d\Timer
			If d\Locked079 Then
				d\SoundCHN = PlaySound2(BigDoorErrorSFX(Sound), Camera, d\OBJ)
			Else
				d\SoundCHN = PlaySound2(OpenDoorSFX(d\Dir, Sound), Camera, d\OBJ)
			EndIf
		Else
			d\SoundCHN = PlaySound2(CloseDoorSFX(d\Dir, Sound), Camera, d\OBJ)
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\OBJ)
	Else
		If d\Open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\TimerState = d\LinkedDoor\Timer
			d\TimerState = d\Timer
		EndIf
	EndIf
	
End Function

Function RemoveDoor(d.Doors)
	If d\Buttons[0] <> 0 Then EntityParent(d\Buttons[0], 0)
	If d\Buttons[1] <> 0 Then EntityParent(d\Buttons[1], 0)
	
	If d\OBJ <> 0 Then FreeEntity(d\OBJ) : d\OBJ = 0
	If d\OBJ2 <> 0 Then FreeEntity(d\OBJ2) : d\OBJ2 = 0
	If d\FrameOBJ <> 0 Then FreeEntity(d\FrameOBJ) : d\FrameOBJ = 0
	If d\Buttons[0] <> 0 Then FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
	If d\Buttons[1] <> 0 Then FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
	
	Delete(d)
End Function

DrawLoading(40, True)

Include "Source Code\Map_System.bb"

DrawLoading(80, True)

Include "Source Code\NPCs_System.bb"

Include "Source Code\Events_System.bb"

Collisions(HIT_PLAYER, HIT_MAP, 2, 2)
Collisions(HIT_PLAYER, HIT_PLAYER, 1, 3)
Collisions(HIT_ITEM, HIT_MAP, 2, 2)
Collisions(HIT_APACHE, HIT_APACHE, 1, 2)
Collisions(HIT_178, HIT_MAP, 2, 2)
Collisions(HIT_178, HIT_178, 1, 3)
Collisions(HIT_DEAD, HIT_MAP, 2, 2)

DrawLoading(90, True)

Global FogTexture%, Fog%
Global GasMaskTexture%, GasMaskOverlay%
Global InfectTexture%, InfectOverlay%
Global DarkTexture%, Dark%
Global Collider%, Head%

Global FogNVTexture%
Global NVTexture%, NVOverlay%

Global TeslaTexture%

Global LightTexture%, Light%
Dim LightSpriteTex%(5)
Global DoorOBJ%, DoorFrameOBJ%

Global LeverOBJ%, LeverBaseOBJ%

Global DoorColl%
Global ButtonOBJ%, ButtonKeyOBJ%, ButtonCodeOBJ%, ButtonScannerOBJ%

Dim DecalTextures%(20)

Global Monitor%, MonitorTexture%
Global CamBaseOBJ%, CamOBJ%

Global LiquidOBJ%, MTFOBJ%, GuardOBJ%, ClassDOBJ%
Global ApacheOBJ%, ApacheRotorOBJ%

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global Save_MSG$ = ""
Global Save_MSG_Timer# = 0.0
Global Save_MSG_Y# = 0.0

Global MTF_CameraCheckTimer# = 0.0
Global MTF_CameraCheckDetected% = False

Include "Source Code\Menu_System.bb"

MainMenuOpen = True

Type MEMORYSTATUS
    Field dwLength%
    Field dwMemoryLoad%
    Field dwTotalPhys%
    Field dwAvailPhys%
    Field dwTotalPageFile%
    Field dwAvailPageFile%
    Field dwTotalVirtual%
    Field dwAvailVirtual%
End Type

Global m.MEMORYSTATUS = New MEMORYSTATUS

FlushKeys()
FlushMouse()

DrawLoading(100, True)

LoopDelay = MilliSecs()

Global UpdateParticles_Time# = 0.0

Global CurrTrisAmount%

Global Input_ResetTime# = 0.0

Type SCP427
	Field Using%
	Field Timer#
	Field Sound[1]
	Field SoundCHN[1]
End Type

Global I_427.SCP427 = New SCP427

Type MapZones
	Field Transition%[1]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones = New MapZones

Repeat
	Cls
	
	CurTime = MilliSecs2()
	ElapsedTime = (CurTime - PrevTime) / 1000.0
	PrevTime = CurTime
	PrevFPSFactor = FPSfactor
	FPSfactor = Max(Min(ElapsedTime * 70.0, 5.0), 0.2)
	FPSfactor2 = FPSfactor
	
	If MenuOpen Or InvOpen Or OtherOpen <> Null Or ConsoleOpen Or SelectedDoor <> Null Or SelectedScreen <> Null Or Using294 Then FPSfactor = 0.0
	
	If FrameLimit > 0 Then
	    ; ~ Frame Limit
		Local WaitingTime% = (1000.0 / FrameLimit) - (MilliSecs2() - LoopDelay)
		
		Delay(WaitingTime)
		
		LoopDelay = MilliSecs2()
	EndIf
	
	; ~ Counting the fps
	If CheckFPS < MilliSecs2() Then
		FPS = ElapsedLoops
		ElapsedLoops = 0
		CheckFPS = MilliSecs2() + 1000.0
	EndIf
	ElapsedLoops = ElapsedLoops + 1
	
	If Input_ResetTime =< 0.0
		DoubleClick = False
		MouseHit1 = MouseHit(1)
		If MouseHit1 Then
			If MilliSecs2() - LastMouseHit1 < 800.0 Then DoubleClick = True
			LastMouseHit1 = MilliSecs2()
		EndIf
		
		Local PrevMouseDown1% = MouseDown1
		
		MouseDown1 = MouseDown(1)
		If PrevMouseDown1 = True And MouseDown1 = False Then MouseUp1 = True Else MouseUp1 = False
		
		MouseHit2 = MouseHit(2)
		
		If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
	Else
		Input_ResetTime = Max(Input_ResetTime - FPSfactor, 0.0)
	EndIf
	
	UpdateMusic()
	If EnableSFXRelease Then AutoReleaseSounds()
	
	If MainMenuOpen Then
		If ShouldPlay = 21 Then
			EndBreathSFX = LoadSound_Strict("SFX\Ending\MenuBreath.ogg")
			EndBreathCHN = PlaySound_Strict(EndBreathSFX)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66
			If ChannelPlaying(EndBreathCHN) = False Then
				FreeSound_Strict(EndBreathSFX)
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		UpdateMainMenu()
	Else
		UpdateStreamSounds()
		
		ShouldPlay = Min(PlayerZone, 2)
		
		DrawHandIcon = False
		
		RestoreSanity = True
		ShouldEntitiesFall = True
		
		If FPSfactor > 0.0 And PlayerRoom\RoomTemplate\Name <> "dimension1499" Then UpdateSecurityCams()
		
		If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And PlayerRoom\RoomTemplate\Name <> "exit1" And (Not MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then 
			
			If Rand(1500) = 1 Then
				For i = 0 To 5
					If AmbientSFX(i, CurrAmbientSFX) <> 0 Then
						If ChannelPlaying(AmbientSFXCHN) = False Then FreeSound_Strict(AmbientSFX(i, CurrAmbientSFX)) : AmbientSFX(i,CurrAmbientSFX) = 0
					EndIf			
				Next
				
				PositionEntity(SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
				
				If Rand(3) = 1 Then PlayerZone = 3
				
				If PlayerRoom\RoomTemplate\Name = "173" Then 
					PlayerZone = 4
				ElseIf PlayerRoom\RoomTemplate\Name = "room860"
					For e.Events = Each Events
						If e\EventName = "room860"
							If e\EventState = 1.0
								PlayerZone = 5
								PositionEntity(SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
							EndIf
							Exit
						EndIf
					Next
				EndIf
				
				CurrAmbientSFX = Rand(0, AmbientSFXAmount(PlayerZone) - 1)
				
				Select PlayerZone
					Case 0, 1, 2
						;[Block]
						If AmbientSFX(PlayerZone, CurrAmbientSFX) = 0 Then AmbientSFX(PlayerZone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Zone" + (PlayerZone + 1) + "\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 3
						;[Block]
						If AmbientSFX(PlayerZone, CurrAmbientSFX) = 0 Then AmbientSFX(PlayerZone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\General\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 4
						;[Block]
						If AmbientSFX(PlayerZone, CurrAmbientSFX) = 0 Then AmbientSFX(PlayerZone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Pre-breach\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
					Case 5
						;[Block]
						If AmbientSFX(PlayerZone, CurrAmbientSFX) = 0 Then AmbientSFX(PlayerZone, CurrAmbientSFX) = LoadSound_Strict("SFX\Ambient\Forest\Ambient" + (CurrAmbientSFX + 1) + ".ogg")
						;[End Block]
				End Select
				
				AmbientSFXCHN = PlaySound2(AmbientSFX(PlayerZone, CurrAmbientSFX), Camera, SoundEmitter)
			EndIf
			UpdateSoundOrigin(AmbientSFXCHN, Camera, SoundEmitter)
			
			If Rand(50000) = 3 Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				
				If RN <> "room860" And RN <> "room1123" And RN <> "173" And RN <> "dimension1499" Then
					If FPSfactor > 0.0 Then LightBlink = Rnd(1.0, 2.0)
					PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(1, 7) + ".ogg"))
				EndIf 
			EndIf
		EndIf
		
		UpdateCheckpoint1 = False
		UpdateCheckpoint2 = False
		
		If (Not MenuOpen) And (Not InvOpen) And (OtherOpen = Null) And (SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And EndingTimer >= 0.0 Then
			LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
			CameraFogRange(Camera, CameraFogNear * LightVolume, CameraFogFar * LightVolume)
			CameraFogColor(Camera, 0.0, 0.0, 0.0)
			CameraFogMode(Camera, 1)
			CameraRange(Camera, 0.05, Min(CameraFogFar * LightVolume * 1.5, 28.0))	
			If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then
				CameraClsColor(Camera, 0, 0, 0)
			EndIf
			
			AmbientLight(Brightness, Brightness, Brightness)
			PlayerSoundVolume = CurveValue(0.0, PlayerSoundVolume, 5.0)
			
			CanSave = True
			UpdateDeafPlayer()
			UpdateEmitters()
			MouseLook()
			If PlayerRoom\RoomTemplate\Name = "dimension1499" And QuickLoadPercent > 0 And QuickLoadPercent < 100
				ShouldEntitiesFall = False
			EndIf
			MovePlayer()
			InFacility = CheckForPlayerInFacility()
			If PlayerRoom\RoomTemplate\Name = "dimension1499"
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
					UpdateDimension1499()
				EndIf
				UpdateLeave1499()
			ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or (PlayerRoom\RoomTemplate\Name = "exit1" And EntityY(Collider) > 1040.0 * RoomScale)
				UpdateDoors()
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
					UpdateEndings()
				EndIf
				UpdateScreens()
				UpdateRoomLights(Camera)
			Else
				UpdateDoors()
				If QuickLoadPercent = -1 Or QuickLoadPercent = 100
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
			; ~ Added a simple code for updating the Particles function depending on the FPSFactor (still WIP, might not be the final version of it) -- ENDSHN
			UpdateParticles_Time = Min(1.0, UpdateParticles_Time + FPSfactor)
			If UpdateParticles_Time = 1.0
				UpdateDevilEmitters()
				UpdateParticles_Devil()
				UpdateParticles_Time = 0.0
			EndIf
		EndIf
		
		If chs\InfiniteStamina Then Stamina = Min(100.0, Stamina + (100.0 - Stamina) * 0.01 * FPSfactor)
		If chs\NoBlink Then BlinkTimer = Min(BLINKFREQ, BlinkTimer + (BLINKFREQ + BlinkTimer) * 0.01 * FPSfactor)
		
		If FPSfactor = 0.0
			UpdateWorld(0)
		Else
			UpdateWorld()
			ManipulateNPCBones()
		EndIf
		RenderWorld2()
		
		BlurVolume = Min(CurveValue(0.0, BlurVolume, 20.0), 0.95)
		If BlurTimer > 0.0 Then
			BlurVolume = Max(Min(0.95, BlurTimer / 1000.0), BlurVolume)
			BlurTimer = Max(BlurTimer - FPSfactor, 0.0)
		End If
		
		UpdateBlur(BlurVolume)
		
		Local DarkA# = 0.0
		
		If (Not MenuOpen)  Then
			If Sanity < 0.0 Then
				If RestoreSanity Then Sanity = Min(Sanity + FPSfactor, 0.0)
				If Sanity < -200.0 Then 
					DarkA = Max(Min((-Sanity - 200.0) / 700.0, 0.6), DarkA)
					If KillTimer >= 0 Then 
						HeartBeatVolume = Min(Abs(Sanity + 20.00) / 500.0, 1.0)
						HeartBeatRate = Max(70.0 + Abs(Sanity + 200.0) / 6.0, HeartBeatRate)
					EndIf
				EndIf
			End If
			
			If EyeStuck > 0 Then 
				BlinkTimer = BLINKFREQ
				EyeStuck = Max(EyeStuck - FPSfactor, 0.0)
				
				If EyeStuck < 9000.0 Then BlurTimer = Max(BlurTimer, (9000.0 - EyeStuck) * 0.5)
				If EyeStuck < 6000.0 Then DarkA = Min(Max(DarkA, (6000.0 - EyeStuck) / 5000.0), 1.0)
				If EyeStuck < 9000.0 And EyeStuck + FPSfactor >= 9000.0 Then 
					Msg = "The eyedrops are causing your eyes to tear up."
					MsgTimer = 70 * 6
				EndIf
			EndIf
			
			If BlinkTimer < 0 Then
				If BlinkTimer > - 5.0 Then
					DarkA = Max(DarkA, Sin(Abs(BlinkTimer * 18.0)))
				ElseIf BlinkTimer > - 15.0
					DarkA = 1.0
				Else
					DarkA = Max(DarkA, Abs(Sin(BlinkTimer * 18.0)))
				EndIf
				
				If BlinkTimer =< - 20 Then
					; ~ Randomizes the frequency of blinking. Scales with difficulty.
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							BLINKFREQ = Rnd(490.0, 700.0)
							;[End Block]
						Case NORMAL
							;[Block]
							BLINKFREQ = Rnd(455.0, 665.0)
							;[End Block]
						Case HARD
							;[Block]
							BLINKFREQ = Rnd(420.0, 630.0)
							;[End Block]
					End Select 
					BlinkTimer = BLINKFREQ
				EndIf
				
				BlinkTimer = BlinkTimer - FPSfactor
			Else
				BlinkTimer = BlinkTimer - FPSfactor * 0.6 * BlinkEffect
				If EyeIrritation > 0.0 Then BlinkTimer = BlinkTimer - Min(EyeIrritation / 100.0 + 1.0, 4.0) * FPSfactor
				
				DarkA = Max(DarkA, 0.0)
			End If
			
			EyeIrritation = Max(0.0, EyeIrritation - FPSfactor)
			
			If BlinkEffectTimer > 0.0 Then
				BlinkEffectTimer = BlinkEffectTimer - (FPSfactor / 70.0)
			Else
				If BlinkEffect <> 1.0 Then BlinkEffect = 1.0
			EndIf
			
			LightBlink = Max(LightBlink - (FPSfactor / 35.0), 0.0)
			If LightBlink > 0.0 And WearingNightVision = 0 Then DarkA = Min(Max(DarkA, LightBlink * Rnd(0.3, 0.8)), 1.0)
			
			If Using294 Then DarkA = 1.0
			
			If WearingNightVision = 0 Then DarkA = Max((1.0 - SecondaryLightOn) * 0.9, DarkA)
			
			If KillTimer < 0.0 Then
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(KillTimer * 5)
				KillTimer = KillTimer - (FPSfactor * 0.8)
				If KillTimer < - 360.0 Then 
					MenuOpen = True 
					If SelectedEnding <> "" Then EndingTimer = Min(KillTimer, -0.1)
				EndIf
				DarkA = Max(DarkA, Min(Abs(KillTimer / 400.0), 1.0))
			EndIf
			
			If FallTimer < 0.0 Then
				If SelectedItem <> Null Then
					If Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Or Instr(SelectedItem\ItemTemplate\TempName, "vest") Then
						If WearingHazmat = 0 And WearingVest = 0 Then
							DropItem(SelectedItem)
						EndIf
					EndIf
				EndIf
				InvOpen = False
				SelectedItem = Null
				SelectedScreen = Null
				SelectedMonitor = Null
				BlurTimer = Abs(FallTimer * 10)
				FallTimer = FallTimer - FPSfactor
				DarkA = Max(DarkA, Min(Abs(FallTimer / 400.0), 1.0))				
			EndIf
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\TempName = "navigator" Or SelectedItem\ItemTemplate\TempName = "nav" Then DarkA = Max(DarkA, 0.5)
			End If
			If SelectedScreen <> Null Then DarkA = Max(DarkA, 0.5)
			
			EntityAlpha(Dark, DarkA)	
		EndIf
		
		If LightFlash > 0 Then
			ShowEntity(Light)
			EntityAlpha(Light, Max(Min(LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
			LightFlash = Max(LightFlash - (FPSfactor / 70.0), 0.0)
		Else
			HideEntity(Light)
		EndIf
		
		EntityColor(Light, 255, 255, 255)
		
		If KeyHit(KEY_INV) And VomitTimer >= 0.0 Then
			If (Not UnableToMove) And (Not IsZombie) And (Not Using294) Then
				Local W$ = ""
				Local V# = 0
				
				If SelectedItem <> Null
					W = SelectedItem\ItemTemplate\TempName
					V = SelectedItem\State
				EndIf
				If (W <> "vest" And W <> "finevest" And W <> "hazmatsuit" And W <> "hazmatsuit2" And W <> "hazmatsuit3") Or V = 0 Or V = 100
					If InvOpen Then
						ResumeSounds()
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
					Else
						PauseSounds()
					EndIf
					InvOpen = Not InvOpen
					If OtherOpen <> Null Then OtherOpen = Null
					SelectedItem = Null
				EndIf
			EndIf
		EndIf
		
		If KeyHit(KEY_SAVE) Then
			If SelectedDifficulty\saveType = SAVEANYWHERE Then
				RN$ = PlayerRoom\RoomTemplate\Name
				If RN = "173" Or (RN = "exit1" And EntityY(Collider) > 1040.0 * RoomScale) Or RN = "gatea"
					Msg = "You cannot save in this location."
					MsgTimer = 70 * 4.0
				ElseIf (Not CanSave) Or QuickLoadPercent > -1
					Msg = "You cannot save at this moment."
					MsgTimer = 70 * 4.0
					If QuickLoadPercent > -1
						Msg = Msg + " (game is loading)"
					EndIf
				Else
					SaveGame(SavePath + CurrSave + "\")
				EndIf
			ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS
				If SelectedScreen = Null And SelectedMonitor = Null Then
					Msg = "Saving is only permitted on clickable monitors scattered throughout the facility."
					MsgTimer = 70 * 4.0
				Else
					RN = PlayerRoom\RoomTemplate\Name
					If RN = "173" Or (RN = "exit1" And EntityY(Collider) > 1040.0 * RoomScale) Or RN = "gatea"
						Msg = "You cannot save in this location."
						MsgTimer = 70 * 4.0
					ElseIf (Not CanSave) Or QuickLoadPercent > -1
						Msg = "You cannot save at this moment."
						MsgTimer = 70 * 4.0
						If QuickLoadPercent > -1
							Msg = Msg + " (game is loading)"
						EndIf
					Else
						If SelectedScreen <> Null
							GameSaved = False
							Playable = True
							DropSpeed = 0.0
						EndIf
						SaveGame(SavePath + CurrSave + "\")
					EndIf
				EndIf
			Else
				Msg = "Quick saving is disabled."
				MsgTimer = 70.0 * 4.0
			EndIf
		Else If SelectedDifficulty\saveType = SAVEONSCREENS And (SelectedScreen <> Null Or SelectedMonitor <> Null)
			If (Msg <> "Game progress saved." And Msg <> "You cannot save in this location." And Msg <> "You cannot save at this moment.") Or MsgTimer =< 0 Then
				Msg = "Press " + KeyName(KEY_SAVE) + " to save."
				MsgTimer = 70.0 * 4.0
			EndIf
			If MouseHit2 Then SelectedMonitor = Null
		EndIf
		
		If KeyHit(KEY_CONSOLE) Then
			If CanOpenConsole
				If ConsoleOpen Then
					UsedConsole = True
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		DrawGUI()
		
		If EndingTimer < 0.0 Then
			If SelectedEnding <> "" Then DrawEnding()
		Else
			DrawMenu()			
		EndIf
		
		UpdateConsole()
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "173" Then
				For e.Events = Each Events
					If e\EventName = "173" Then
						If e\EventState3 >= 40.0 And e\EventState3 < 50.0 Then
							If InvOpen Then
								Msg = "Double click on the document to view it."
								MsgTimer = 70.0 * 7.0
								e\EventState3 = 50.0
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		If MsgTimer > 0.0 Then
			Local Temp% = False
			
			If (Not InvOpen)
				If SelectedItem <> Null
					If SelectedItem\ItemTemplate\TempName = "paper" Or SelectedItem\ItemTemplate\TempName = "oldpaper"
						Temp = True
					EndIf
				EndIf
			EndIf
			
			If (Not Temp)
				Color(0, 0, 0)
				AAText((GraphicWidth / 2) + 1, (GraphicHeight / 2) + 201, Msg, True, False, Min(MsgTimer / 2.0, 255.0) / 255.0)
				Color(255, 255, 255)
				If Left(Msg, 14) = "Blitz3D Error!" Then
					Color(255, 0, 0)
				EndIf
				AAText((GraphicWidth / 2), (GraphicHeight / 2) + 200, Msg, True, False, Min(MsgTimer / 2.0, 255.0) / 255.0)
			Else
				Color(0, 0, 0)
				AAText((GraphicWidth / 2) + 1, (GraphicHeight * 0.94) + 1, Msg, True, False, Min(MsgTimer / 2.0, 255.0) / 255.0)
				Color(255, 255, 255)
				If Left(Msg, 14) = "Blitz3D Error!" Then
					Color(255, 0, 0)
				EndIf
				AAText((GraphicWidth / 2), (GraphicHeight * 0.94), Msg, True, False, Min(MsgTimer / 2.0, 255.0) / 255.0)
			EndIf
			MsgTimer = MsgTimer - FPSfactor2 
		End If
		
		Color(255, 255, 255)
		If ShowFPS Then AASetFont(ConsoleFont) : AAText(20, 20, "FPS: " + FPS) : AASetFont(Font1)
		
		DrawQuickLoading()
		
		UpdateAchievementMsg()
		RenderAchievementMsg()
	End If
	
	If BorderlessWindowed Then
		If (RealGraphicWidth <> GraphicWidth) Or (RealGraphicHeight <> GraphicHeight) Then
			SetBuffer(TextureBuffer(Fresize_Texture))
			ClsColor(0, 0, 0) : Cls
			CopyRect(0, 0, GraphicWidth, GraphicHeight, 1024 - GraphicWidth / 2, 1024 - GraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
			SetBuffer(BackBuffer())
			ClsColor(0, 0, 0) : Cls
			ScaleRender(0, 0, 2050.0 / Float(GraphicWidth) * AspectRatioRatio, 2050.0 / Float(GraphicWidth) * AspectRatioRatio)
			; ~ Might want to replace Float(GraphicWidth) with Max(GraphicWidth, GraphicHeight) if portrait sizes cause issues
			; ~ Everyone uses landscape so it's probably a non-issue
		EndIf
	EndIf
	
	; ~ Not by any means a perfect solution
	; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
	If ScreenGamma > 1.0 Then
		CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
		EntityBlend(Fresize_Image, 1)
		ClsColor(0, 0, 0) : Cls
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		EntityFX(Fresize_Image, 1 + 32)
		EntityBlend(Fresize_Image, 3)
		EntityAlpha(Fresize_Image, ScreenGamma - 1.0)
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
	ElseIf ScreenGamma < 1.0 Then ; ~ Maybe optimize this if it's too slow, alternatively give players the option to disable gamma
		CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
		EntityBlend(Fresize_Image, 1)
		ClsColor(0, 0, 0) : Cls
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		EntityFX(Fresize_Image, 1 + 32)
		EntityBlend(Fresize_Image, 2)
		EntityAlpha(Fresize_Image, 1.0)
		SetBuffer(TextureBuffer(Fresize_Texture2))
		ClsColor(255 * ScreenGamma, 255 * ScreenGamma, 255 * ScreenGamma)
		Cls
		SetBuffer(BackBuffer())
		ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		SetBuffer(TextureBuffer(Fresize_Texture2))
		ClsColor(0, 0, 0)
		Cls
		SetBuffer(BackBuffer())
	EndIf
	EntityFX(Fresize_Image, 1)
	EntityBlend(Fresize_Image, 1)
	EntityAlpha(Fresize_Image, 1.0)
	
	CatchErrors("Main loop / uncaught")
	
	If VSync = 0 Then
		Flip(0)
	Else 
		Flip(1)
	EndIf
Forever

Function Kill()
	If chs\GodMode Then Return
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then StopChannel(BreathCHN)
	EndIf
	
	If KillTimer >= 0.0 Then
		KillAnim = Rand(0, 1)
		PlaySound_Strict(DamageSFX(0))
		If SelectedDifficulty\permaDeath Then
			DeleteFile(CurrentDir() + SavePath + CurrSave + "\save.txt") 
			DeleteDir(SavePath + CurrSave) 
			LoadSaveGames()
		End If
		
		KillTimer = Min(-1.0, KillTimer)
		ShowEntity(Head)
		PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity(Head)
		RotateEntity(Head, 0.0, EntityYaw(Camera), 0.0)		
	EndIf
End Function

Function DrawEnding()
	ShowPointer()
	
	FPSfactor = 0.0
	If EndingTimer > -2000.0
		EndingTimer = Max(EndingTimer - FPSfactor2, -1111.0)
	Else
		EndingTimer = EndingTimer - FPSfactor2
	EndIf
	
	GiveAchievement(Achv055)
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\name = "Keter" Then GiveAchievement(AchvKeter)
	
	Local x%, y%, Width%, Height%, i%
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(SelectedEnding)
		Case "b2", "a1"
			;[Block]
			ClsColor(Max(255 + (EndingTimer) * 2.8, 0), Max(255 + (EndingTimer) * 2.8, 0), Max(255 + (EndingTimer) * 2.8, 0))
			;[End Block]
		Default
			;[Block]
			ClsColor(0, 0, 0)
			;[End Block]
	End Select
	
	ShouldPlay = 66
	
	Cls
	
	If EndingTimer < -200.0 Then
		
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN) : Stamina = 100.0
		EndIf
		
		If EndingScreen = 0 Then
			EndingScreen = LoadImage_Strict("GFX\endingscreen.pt")
			
			ShouldPlay = 23
			CurrMusicVolume = MusicVolume
			
			CurrMusicVolume = MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music(23) + ".ogg", CurrMusicVolume, 0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict(LightSFX)
		EndIf
		
		If EndingTimer > -700.0 Then 
			If Rand(1, 150) < Min((Abs(EndingTimer) - 200.0), 155.0) Then
				DrawImage(EndingScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
			Else
				Color(0, 0, 0)
				Rect(100, 100, GraphicWidth - 200, GraphicHeight - 200)
				Color(255, 255, 255)
			EndIf
			
			If EndingTimer + FPSfactor2 > -450.0 And EndingTimer =< -450.0 Then
				Select Lower(SelectedEnding)
					Case "a1", "a2"
						;[Block]
						PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Ending" + SelectedEnding + ".ogg"))
						;[End Block]
					Case "b1", "b2", "b3"
						;[Block]
						PlaySound_Strict(LoadTempSound("SFX\Ending\GateB\Ending" + SelectedEnding + ".ogg"))
						;[End Block]
				End Select
			EndIf			
		Else
			DrawImage(EndingScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
			
			If EndingTimer < -1000.0 And EndingTimer > -2000.0
				Width = ImageWidth(PauseMenuIMG)
				Height = ImageHeight(PauseMenuIMG)
				x = GraphicWidth / 2 - Width / 2
				y = GraphicHeight / 2 - Height / 2
				
				DrawImage(PauseMenuIMG, x, y)
				
				Color(255, 255, 255)
				AASetFont(Font2)
				AAText(x + Width / 2 + 40 * MenuScale, y + 20 * MenuScale, "THE END", True)
				AASetFont(Font1)
				
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
						If itt\tempname = "paper" Then
							DocAmount = DocAmount + 1
							DocsFound = DocsFound + itt\Found
						EndIf
					Next
					
					Local SCPsEncountered% = 1
					
					For i = 0 To 24
						SCPsEncountered = SCPsEncountered + Achievements(i)
					Next
					
					Local AchievementsUnlocked% = 0
					
					For i = 0 To MAXACHIEVEMENTS - 1
						AchievementsUnlocked = AchievementsUnlocked + Achievements(i)
					Next
					
					AAText(x, y, "SCPs encountered: " + SCPsEncountered)
					AAText(x, y + 20 * MenuScale, "Achievements unlocked: " + AchievementsUnlocked + "/" + (MAXACHIEVEMENTS))
					AAText(x, y + 40 * MenuScale, "Rooms found: " + RoomsFound + "/" + RoomAmount)
					AAText(x, y + 60 * MenuScale, "Documents discovered: " + DocsFound + "/" + DocAmount)
					AAText(x, y + 80 * MenuScale, "Items refined in SCP-914: " + RefinedItems)
					
					x = GraphicWidth / 2 - Width / 2
					y = GraphicHeight / 2 - Height / 2
					x = x + Width / 2
					y = y + Height - 100 * MenuScale
					
					If DrawButton(x - 145 * MenuScale, y - 200 * MenuScale, 390 * MenuScale, 60 * MenuScale, "ACHIEVEMENTS", True) Then
						AchievementsMenu = 1
					EndIf
					
					If DrawButton(x - 145 * MenuScale, y - 100 * MenuScale, 390 * MenuScale, 60 * MenuScale, "MAIN MENU", True)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i = 0 To 9
							If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict("SFX\Music\" + Music(NowPlaying) + ".ogg", 0.0, Mode)
						SetStreamVolume_Strict(MusicCHN, 1.0 * MusicVolume)
						FlushKeys()
						EndingTimer = -2000.0
						InitCredits()
					EndIf
				Else
					ShouldPlay = 23
					DrawMenu()
				EndIf
			; ~ Credits
			ElseIf EndingTimer =< -2000.0
				ShouldPlay = 24
				DrawCredits()
			EndIf
		EndIf
	EndIf
	If FullScreen Then DrawImage(CursorIMG), ScaledMouseX(), ScaledMouseY()
	
	AASetFont(Font1)
End Function

Type CreditsLine
	Field Txt$
	Field ID%
	Field Stay%
End Type

Global CreditsTimer# = 0.0
Global CreditsScreen%

Function InitCredits()
	Local cl.CreditsLine
	Local File% = OpenFile("Credits.txt")
	Local l$
	
	CreditsFont = LoadFont_Strict("GFX\font\cour\Courier New.ttf", Int(21 * (GraphicHeight / 1024)), 0, 0, 0)
	CreditsFont2 = LoadFont_Strict("GFX\font\courbd\Courier New.ttf", Int(35 * (GraphicHeight / 1024)), 0, 0, 0)
	
	If CreditsScreen = 0
		CreditsScreen = LoadImage_Strict("GFX\creditsscreen.pt")
	EndIf
	
	Repeat
		l = ReadLine(File)
		cl = New CreditsLine
		cl\Txt = l
	Until Eof(File)
	
	Delete First CreditsLine
	CreditsTimer = 0
End Function

Function DrawCredits()
    Local Credits_Y# = (EndingTimer + 2000.0) / 2 + (GraphicHeight + 10.0)
    Local cl.CreditsLine
    Local ID%
    Local EndLinesAmount%
	Local LastCreditLine.CreditsLine
	
    Cls
	
	If Rand(1, 300) > 1
		DrawImage(CreditsScreen, GraphicWidth / 2 - 400, GraphicHeight / 2 - 400)
	EndIf
	
	ID = 0
	EndLinesAmount = 0
	LastCreditLine = Null
	Color(255, 255, 255)
	For cl = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "*"
			SetFont(CreditsFont2)
			If cl\Stay = False
				Text(GraphicWidth / 2, Credits_Y + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
			EndIf
		ElseIf Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		Else
			SetFont(CreditsFont)
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
		CreditsTimer = CreditsTimer + (0.5 * FPSfactor2)
		If CreditsTimer >= 0.0 And CreditsTimer < 255.0
			Color(Max(Min(CreditsTimer, 255.0), 0.0), Max(Min(CreditsTimer, 255.0), 0.0), Max(Min(CreditsTimer, 255.0), 0.0))
		ElseIf CreditsTimer >= 255.0
			Color(255, 255, 255)
			If CreditsTimer > 500.0
				CreditsTimer = -255.0
			EndIf
		Else
			Color(Max(Min(-CreditsTimer, 255.0), 0.0), Max(Min(-CreditsTimer, 255.0), 0.0), Max(Min(-CreditsTimer, 255.0), 0.0))
			If CreditsTimer >= -1.0
				CreditsTimer = -1.0
			EndIf
		EndIf
	EndIf
	If CreditsTimer <> 0.0
		For cl = Each CreditsLine
			If cl\Stay
				SetFont(CreditsFont)
				If Left(cl\Txt, 1) = "/"
					Text(GraphicWidth / 2, (GraphicHeight / 2) + (EndLinesAmount / 2) + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
				Else
					Text(GraphicWidth / 2, (GraphicHeight / 2) + (24 * (cl\ID - LastCreditLine\ID) * MenuScale) - ((EndLinesAmount / 2) * 24 * MenuScale), cl\Txt, True)
				EndIf
			EndIf
		Next
	EndIf
	
	If GetKey() Then CreditsTimer = -1.0
	
	If CreditsTimer = -1.0
		FreeFont(CreditsFont)
		FreeFont(CreditsFont2)
		FreeImage(CreditsScreen)
		CreditsScreen = 0
		FreeImage(EndingScreen)
		EndingScreen = 0
		Delete Each CreditsLine
        NullGame(False)
        StopStream_Strict(MusicCHN)
        ShouldPlay = 21
        MenuOpen = False
        MainMenuOpen = True
        MainMenuTab = 0
        CurrSave = ""
        FlushKeys()
	EndIf
End Function

Function MovePlayer()
	CatchErrors("Uncaught (MovePlayer)")
	
	Local Sprint# = 1.0, Speed# = 0.018, i%, Angle#
	
	If SuperMan Then
		Speed = Speed * 3
		
		SuperManTimer = SuperManTimer + FPSfactor
		
		CameraShake = Sin(SuperManTimer / 5.0) * (SuperManTimer / 1500.0)
		
		If SuperManTimer > 70.0 * 50.0 Then
			DeathMsg = "A Class D jumpsuit found in [DATA REDACTED]. Upon further examination, the jumpsuit was found to be filled with 12.5 kilograms of blue ash-like substance. "
			DeathMsg = DeathMsg + "Chemical analysis of the substance remains non-conclusive. Most likely related to SCP-914."
			Kill()
			ShowEntity(Fog)
		Else
			BlurTimer = 500.0		
			HideEntity(Fog)
		EndIf
	End If
	
	If DeathTimer > 0.0 Then
		DeathTimer = DeathTimer - FPSfactor
		If DeathTimer < 1.0 Then DeathTimer = -1.0
	ElseIf DeathTimer < 0.0 
		Kill()
	EndIf
	
	If CurrSpeed > 0 Then
        Stamina = Min(Stamina + 0.15 * FPSfactor / 1.25, 100.0)
    Else
        Stamina = Min(Stamina + 0.15 * FPSfactor * 1.25, 100.0)
    EndIf
	
	If StaminaEffectTimer > 0.0 Then
		StaminaEffectTimer = StaminaEffectTimer - (FPSfactor / 70.0)
	Else
		If StaminaEffect <> 1.0 Then StaminaEffect = 1.0
	EndIf
	
	Local Temp#
	
	If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then 
		If KeyDown(KEY_SPRINT) Then
			If Stamina < 5.0 Then
				Temp = 0.0
				If WearingGasMask > 0 Or Wearing1499 > 0 Then Temp = 1
				If ChannelPlaying(BreathCHN) = False Then BreathCHN = PlaySound_Strict(BreathSFX((Temp), 0))
			ElseIf Stamina < 50.0
				If BreathCHN = 0 Then
					Temp = 0.0
					If WearingGasMask > 0 Or Wearing1499 > 0 Then Temp = 1
					BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
					ChannelVolume(BreathCHN, Min((70.0 - Stamina) / 70.0, 1.0) * SFXVolume)
				Else
					If ChannelPlaying(BreathCHN) = False Then
						Temp = 0.0
						If WearingGasMask > 0 Or Wearing1499 > 0 Then Temp = 1
						BreathCHN = PlaySound_Strict(BreathSFX((Temp), Rand(1, 3)))
						ChannelVolume(BreathCHN, Min((70.0 - Stamina) / 70.0, 1.0) * SFXVolume)		
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	For i = 0 To MaxItemAmount - 1
		If Inventory(i) <> Null Then
			If Inventory(i)\ItemTemplate\TempName = "finevest" Then Stamina = Min(Stamina, 60.0)
		EndIf
	Next
	
	If Wearing714 = 1 Then
		Stamina = Min(Stamina, 10.0)
		Sanity = Max(-850.0, Sanity)
	EndIf
	
	If IsZombie Then Crouch = False
	
	If Abs(CrouchState - Crouch) < 0.001 Then 
		CrouchState = Crouch
	Else
		CrouchState = CurveValue(Crouch, CrouchState, 10.0)
	EndIf
	
	If (Not chs\NoClip) Then 
		If ((KeyDown(KEY_DOWN) Or KeyDown(KEY_UP)) Or (KeyDown(KEY_RIGHT) Or KeyDown(KEY_LEFT)) And Playable) Or ForceMove > 0 Then
			If Crouch = 0 And (KeyDown(KEY_SPRINT)) And Stamina > 0.0 And (Not IsZombie) Then
				Sprint = 2.5
				Stamina = Stamina - FPSfactor * 0.4 * StaminaEffect
				If Stamina =< 0.0 Then Stamina = -20.0
			End If
			
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then 
				If EntityY(Collider) < 2000.0 * RoomScale Or EntityY(Collider) > 2608.0 * RoomScale Then
					Stamina = 0.0
					Speed = 0.015
					Sprint = 1.0					
				EndIf
			EndIf	
			
			If ForceMove > 0 Then Speed = Speed * ForceMove
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\TempName = "firstaid" Or SelectedItem\ItemTemplate\TempName = "finefirstaid" Or SelectedItem\ItemTemplate\TempName = "firstaid2" Then
					Sprint = 0.0
				EndIf
			EndIf
			
			Temp = (Shake Mod 360.0)
			
			Local TempCHN%
			
			If (Not UnableToMove) Then Shake = (Shake + FPSfactor * Min(Sprint, 1.5) * 7.0) Mod 720.0
			If Temp < 180.0 And (Shake Mod 360.0) >= 180.0 And KillTimer >= 0.0 Then
				If CurrStepSFX = 0 Then
					Temp = GetStepSound(Collider)
					
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0, PlayerSoundVolume)
						TempCHN = PlaySound_Strict(StepSFX(Temp, 0, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (Crouch * 0.6)) * SFXVolume)
					Else
						PlayerSoundVolume = Max(2.5 - (Crouch * 0.6), PlayerSoundVolume)
						TempCHN = PlaySound_Strict(StepSFX(Temp, 1, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (Crouch * 0.6)) * SFXVolume)
					End If
				ElseIf CurrStepSFX = 1
					TempCHN = PlaySound_Strict(Step2SFX(Rand(0, 2)))
					ChannelVolume(TempCHN, (1.0 - (Crouch * 0.4)) * SFXVolume)
				ElseIf CurrStepSFX = 2
					TempCHN = PlaySound_Strict(Step2SFX(Rand(3, 5)))
					ChannelVolume(TempCHN, (1.0 - (Crouch * 0.4)) * SFXVolume)
				ElseIf CurrStepSFX = 3
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0, PlayerSoundVolume)
						TempCHN = PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (Crouch * 0.6)) * SFXVolume)
					Else
						PlayerSoundVolume = Max(2.5 - (Crouch * 0.6), PlayerSoundVolume)
						TempCHN = PlaySound_Strict(StepSFX(0, 1, Rand(0, 7)))
						ChannelVolume(TempCHN, (1.0 - (Crouch * 0.6)) * SFXVolume)
					End If
				EndIf
			EndIf	
		EndIf
	Else
		If (KeyDown(KEY_SPRINT)) Then 
			Sprint = 2.5
		ElseIf KeyDown(KEY_CROUCH)
			Sprint = 0.5
		EndIf
	EndIf
	
	If KeyHit(KEY_CROUCH) And Playable Then Crouch = (Not Crouch)
	
	Local Temp2# = (Speed * Sprint) / (1.0 + CrouchState)
	
	If chs\NoClip Then 
		Shake = 0.0
		CurrSpeed = 0
		CrouchState = 0.0
		Crouch = 0
		
		RotateEntity(Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0.0)
		
		Temp2 = Temp2 * NoClipSpeed
		
		If KeyDown(KEY_DOWN) Then MoveEntity(Collider, 0.0, 0.0, -Temp2 * FPSfactor)
		If KeyDown(KEY_UP) Then MoveEntity(Collider, 0.0, 0.0, Temp2 * FPSfactor)
		
		If KeyDown(KEY_LEFT) Then MoveEntity(Collider, -Temp2 * FPSfactor, 0.0, 0.0)
		If KeyDown(KEY_RIGHT) Then MoveEntity(Collider, Temp2 * FPSfactor, 0.0, 0.0)
		
		ResetEntity(Collider)
	Else
		Temp2 = Temp2 / Max((Injuries + 3.0) / 3.0, 1.0)
		If Injuries > 0.5 Then 
			Temp2 = Temp2 * Min((Sin(Shake / 2.0) + 1.2), 1.0)
		EndIf
		
		Temp = False
		If (Not IsZombie)
			If KeyDown(KEY_DOWN) And Playable Then
				Temp = True 
				Angle = 180.0
				If KeyDown(KEY_LEFT) Then Angle = 135.0 
				If KeyDown(KEY_RIGHT) Then Angle = -135.0 
			ElseIf (KeyDown(KEY_UP) And Playable) Then
				Temp = True
				Angle = 0.0
				If KeyDown(KEY_LEFT) Then Angle = 45.0 
				If KeyDown(KEY_RIGHT) Then Angle = -45.0 
			ElseIf ForceMove > 0.0 Then
				Temp = True
				Angle = ForceAngle
			Else If Playable Then
				If KeyDown(KEY_LEFT) Then Angle = 90.0 : Temp = True
				If KeyDown(KEY_RIGHT) Then Angle = -90.0 : Temp = True 
			EndIf
		Else
			Temp = True
			Angle = ForceAngle
		EndIf
		
		Angle = WrapAngle(EntityYaw(Collider, True) + Angle + 90.0)
		
		If Temp Then 
			CurrSpeed = CurveValue(Temp2, CurrSpeed, 20.0)
		Else
			CurrSpeed = Max(CurveValue(0.0, CurrSpeed - 0.1, 1.0), 0.0)
		EndIf
		
		If (Not UnableToMove) Then TranslateEntity(Collider, Cos(Angle) * CurrSpeed * FPSfactor, 0.0, Sin(Angle) * CurrSpeed * FPSfactor, True)
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(Collider)
			If CollisionY(Collider, i) < EntityY(Collider) - 0.25 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			If DropSpeed < -0.07 Then 
				If CurrStepSFX = 0 Then
					PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX = 1
					PlaySound_Strict(Step2SFX(Rand(0, 2)))
				ElseIf CurrStepSFX = 2
					PlaySound_Strict(Step2SFX(Rand(3, 5)))
				ElseIf CurrStepSFX = 3
					PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
				EndIf
				PlayerSoundVolume = Max(3.0, PlayerSoundVolume)
			EndIf
			DropSpeed = 0
		Else
			If PlayerFallingPickDistance <> 0.0 Then
				Local Pick# = LinePick(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0.0, -PlayerFallingPickDistance, 0.0)
				
				If Pick
					DropSpeed = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
				Else
					DropSpeed = 0
				EndIf
			Else
				DropSpeed = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
			EndIf
		EndIf
		PlayerFallingPickDistance = 10.0
		
		If (Not UnableToMove) And ShouldEntitiesFall Then TranslateEntity(Collider, 0.0, DropSpeed * FPSfactor, 0.0)
	EndIf
	
	ForceMove = False
	
	If Injuries > 1.0 Then
		Temp2 = Bloodloss
		BlurTimer = Max(Max(Sin(MilliSecs2() / 100.0) * Bloodloss * 30.0, Bloodloss * 2.0 * (2.0 - CrouchState)), BlurTimer)
		If (Not I_427\Using And I_427\Timer < 70.0 * 360.0) Then
			Bloodloss = Min(Bloodloss + (Min(Injuries, 3.5) / 300.0) * FPSfactor, 100.0)
		EndIf
		
		If Temp2 =< 60.0 And Bloodloss > 60.0 Then
			Msg = "You are feeling faint from the amount of blood you have lost."
			MsgTimer = 70 * 4.0
		EndIf
	EndIf
	
	Update008()
	
	If Bloodloss > 0.0 Then
		If Rnd(200.0) < Min(Injuries, 4.0) Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(Collider) + Rnd(-0.05, 0.05), EntityY(Collider) - 0.05, EntityZ(Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			
			de.Decals = CreateDecal(Rand(15, 16), PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rand(360.0), 0.0)
			de\Size = Rnd(0.03, 0.08) * Min(Injuries, 3.0) : EntityAlpha(de\OBJ, 1.0)
			ScaleSprite de\OBJ, de\size, de\size
			TempCHN = PlaySound_Strict(DripSFX(Rand(0, 2)))
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			
			FreeEntity(Pvt)
		EndIf
		
		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * Bloodloss * 0.2)
		
		If Bloodloss > 60.0 Then Crouch = True
		If Bloodloss >= 100.0 Then 
			Kill()
			HeartBeatVolume = 0.0
		ElseIf Bloodloss > 80.0
			HeartBeatRate = Max(150.0 - (Bloodloss - 80.0) * 5.0, HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, 0.75 + (Bloodloss - 80.0) * 0.0125)	
		ElseIf Bloodloss > 35.0
			HeartBeatRate = Max(70.0 + Bloodloss, HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, (Bloodloss - 35.0) / 60.0)			
		EndIf
	EndIf
	
	If HealTimer > 0.0 Then
		HealTimer = HealTimer - (FPSfactor / 70.0)
		Bloodloss = Min(Bloodloss + (2.0 / 400.0) * FPSfactor, 100.0)
		Injuries = Max(Injuries - (FPSfactor / 70.0) / 30.0, 0.0)
	EndIf
		
	If Playable Then
		If KeyHit(KEY_BLINK) Then BlinkTimer = 0.0
		If KeyDown(KEY_BLINK) And BlinkTimer < - 10.0 Then BlinkTimer = -10.0
	EndIf
	
	If HeartBeatVolume > 0.0 Then
		If HeartBeatTimer =< 0.0 Then
			TempCHN = PlaySound_Strict(HeartBeatSFX)
			ChannelVolume(TempCHN, HeartBeatVolume * SFXVolume)
			
			HeartBeatTimer = 70.0 * (60.0 / Max(HeartBeatRate, 1.0))
		Else
			HeartBeatTimer = HeartBeatTimer - FPSfactor
		EndIf
		
		HeartBeatVolume = Max(HeartBeatVolume - FPSfactor * 0.05, 0.0)
	EndIf
	
	CatchErrors("MovePlayer")
End Function

Function MouseLook()
	Local i%
	
	CameraShake = Max(CameraShake - (FPSfactor / 10.0), 0.0)
	
	CameraZoom(Camera, Min(1.0 + (CurrCameraZoom / 400.0), 1.1))
	CurrCameraZoom = Max(CurrCameraZoom - FPSfactor, 0.0)
	
	If KillTimer >= 0.0 And FallTimer >= 0.0 Then
		HeadDropSpeed = 0.0
		
		; ~ Fixing the black screen bug with some bubblegum code 
		Local Zero# = 0.0
		Local Nan1# = 0.0 / Zero
		If Int(EntityX(Collider)) = Int(Nan1) Then
			PositionEntity(Collider, EntityX(Camera, True), EntityY(Camera, True) - 0.5, EntityZ(Camera, True), True)
		EndIf
		
		Local Up# = (Sin(Shake) / (20.0 + CrouchState * 20.0)) * 0.6	
		Local Roll# = Max(Min(Sin(Shake / 2.0) * 2.5 * Min(Injuries + 0.25, 3.0), 8.0), -8.0)
		
		PositionEntity(Camera, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
		RotateEntity(Camera, 0.0, EntityYaw(Collider), Roll * 0.5)
		
		MoveEntity(Camera, Side, Up + 0.6 + CrouchState * -0.3, 0.0)
		
		; ~ Update the smoothing que to smooth the movement of the mouse.
		Mouse_X_Speed_1 = CurveValue(MouseXSpeed() * (MouseSensitivity + 0.6) , Mouse_X_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing) 
		If Int(Mouse_X_Speed_1) = Int(Nan1) Then Mouse_X_Speed_1 = 0.0
		If PrevFPSFactor > 0.0 Then
            If Abs(FPSfactor / PrevFPSFactor - 1.0) > 1.0 Then
                ; ~ Lag spike detected - stop all camera movement
                Mouse_X_Speed_1 = 0.0
                Mouse_Y_Speed_1 = 0.0
            EndIf
        EndIf
		If InvertMouse Then
			Mouse_Y_Speed_1 = CurveValue(-MouseYSpeed() * (MouseSensitivity + 0.6), Mouse_Y_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing) 
		Else
			Mouse_Y_Speed_1 = CurveValue(MouseYSpeed () * (MouseSensitivity + 0.6), Mouse_Y_Speed_1, (6.0 / (MouseSensitivity + 1.0)) * MouseSmoothing) 
		EndIf
		If Int(Mouse_Y_Speed_1) = Int(Nan1) Then Mouse_Y_Speed_1 = 0.0
		
		Local The_Yaw# = Mouse_X_Speed_1 * Mouselook_X_Inc / (1.0 + WearingVest)
		Local The_Pitch# = Mouse_Y_Speed_1 * Mouselook_Y_Inc / (1.0 + WearingVest)
		
		TurnEntity(Collider, 0.0, -The_Yaw, 0.0) ; ~ Turn the user on the Y (Yaw) axis.
		User_Camera_Pitch = User_Camera_Pitch# + The_Pitch
		; ~ Limit the user's camera to within 180 degrees of pitch rotation. Returns useless values so we need to use a variable to keep track of the camera pitch.
		If User_Camera_Pitch > 70.0 Then User_Camera_Pitch = 70.0
		If User_Camera_Pitch < -70.0 Then User_Camera_Pitch = -70.0
		
		RotateEntity(Camera, WrapAngle(User_Camera_Pitch + Rnd(-CameraShake, CameraShake)), WrapAngle(EntityYaw(Collider) + Rnd(-CameraShake, CameraShake)), Roll) ; ~ Pitch the user's camera up and down.
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
			If EntityY(Collider) < 2000.0 * RoomScale Or EntityY(Collider) > 2608.0 * RoomScale Then
				RotateEntity(Camera, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), Roll + WrapAngle(Sin(MilliSecs2() / 150.0) * 30.0)) ; ~ Pitch the user's camera up and down.
			EndIf
		EndIf
	Else
		HideEntity(Collider)
		PositionEntity(Camera, EntityX(Head), EntityY(Head), EntityZ(Head))
		
		Local CollidedFloor% = False
		
		For i = 1 To CountCollisions(Head)
			If CollisionY(Head, i) < EntityY(Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			HeadDropSpeed = 0.0
		Else
			If KillAnim = 0 Then 
				MoveEntity(Head, 0.0, 0.0, HeadDropSpeed)
				RotateEntity(Head, CurveAngle(-90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity(Head, 0.0, 0.0, -HeadDropSpeed)
				RotateEntity(Head, CurveAngle(90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			HeadDropSpeed = HeadDropSpeed - 0.002 * FPSfactor
		EndIf
		
		If InvertMouse Then
			TurnEntity(Camera, -MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0.0)
		Else
			TurnEntity(Camera, MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0.0)
		End If
	EndIf
	
	If ParticleAmount = 2
		If Rand(35) = 1 Then
			Local Pvt% = CreatePivot()
			
			PositionEntity(Pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
			If Rand(2) = 1 Then
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			End If
			
			Local p.Particles = CreateParticle(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 2, 0.002, 0.0, 300.0)
			
			p\Speed = 0.001 : p\SizeChange = -0.00001
			RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
			FreeEntity(Pvt)
		End If
	EndIf
	
	; ~ Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop.
	If (MouseX() > Mouse_Right_Limit) Or (MouseX() < Mouse_Left_Limit) Or (MouseY() > Mouse_Bottom_Limit) Or (MouseY() < Mouse_Top_Limit)
		MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	EndIf
	
	If WearingGasMask > 0 Or Wearing1499 > 0 Or WearingHazmat > 0 Then
		If Wearing714 = 0 Then
			If WearingGasMask = 2 Or Wearing1499 = 2 Or WearingHazmat = 2 Then
				Stamina = Min(100.0, Stamina + (100.0 - Stamina) * 0.01 * FPSfactor)
			EndIf
		EndIf
		If WearingHazmat = 1 Then
			Stamina = Min(60.0, Stamina)
		EndIf
		ShowEntity(GasMaskOverlay)
	Else
		HideEntity(GasMaskOverlay)
	End If
	
	If WearingNightVision > 0 Then
		ShowEntity(NVOverlay)
		If WearingNightVision = 2 Then
			EntityColor(NVOverlay, 0, 100, 255)
			AmbientLightRooms(15)
		ElseIf WearingNightVision = 3 Then
			EntityColor(NVOverlay, 255, 0, 0)
			AmbientLightRooms(15)
		Else
			EntityColor(NVOverlay, 0, 255, 0)
			AmbientLightRooms(15)
		EndIf
		EntityTexture(Fog, FogNVTexture)
	Else
		AmbientLightRooms(0)
		HideEntity(NVOverlay)
		EntityTexture(Fog, FogTexture)
	EndIf
	
	For i = 0 To 5
		If SCP1025State[i] > 0.0 Then
			Select i
				Case 0 ; ~ Common cold
					;[Block]
					If FPSfactor > 0.0 Then 
						If Rand(1000) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If ChannelPlaying(CoughCHN) = False Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - FPSfactor * 0.3
					;[End Block]
				Case 1 ; ~ Chicken pox
					;[Block]
					If Rand(9000) = 1 And Msg = "" Then
						Msg = "Your skin is feeling itchy."
						MsgTimer = 70 * 4.0
					EndIf
					;[End Block]
				Case 2 ; ~ Cancer of the lungs
					;[Block]
					If FPSfactor > 0.0 Then 
						If Rand(800) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If ChannelPlaying(CoughCHN) = False Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
					EndIf
					Stamina = Stamina - FPSfactor * 0.1
					;[End Block]
				Case 3 ; ~ Appendicitis
					; ~ 0.035 / sec = 2.1 / min
					If I_427\Using = 0 And I_427\Timer < 70 * 360.0 Then
						SCP1025State[i] = SCP1025State[i] + FPSfactor * 0.0005
					EndIf
					If SCP1025State[i] > 20.0 Then
						If SCP1025State[i] - FPSfactor =< 20.0 Then Msg = "The pain in your stomach is becoming unbearable."
						Stamina = Stamina - FPSfactor * 0.3
					ElseIf SCP1025State[i] > 10.0
						If SCP1025State[i] - FPSfactor =< 10.0 Then Msg = "Your stomach is aching."
					EndIf
					;[End Block]
				Case 4 ; ~ Asthma
					;[Block]
					If Stamina < 35.0 Then
						If Rand(Int(140 + Stamina * 8)) = 1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If ChannelPlaying(CoughCHN) = False Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							End If
						EndIf
						CurrSpeed = CurveValue(0.0, CurrSpeed, 10.0 + Stamina * 15.0)
					EndIf
					;[End Block]
				Case 5 ; ~ Cardiac arrest
					;[Block]
					If I_427\Using = 0 And I_427\Timer < 70 * 360.0 Then
						SCP1025State[i] = SCP1025State[i] + FPSfactor * 0.35
					EndIf
					
					; ~ 35 / sec
					If SCP1025State[i] > 110.0 Then
						HeartBeatRate = 0.0
						BlurTimer = Max(BlurTimer, 500.0)
						If SCP1025State[i] > 140.0 Then 
							DeathMsg = Chr(34) + "He died of a cardiac arrest after reading SCP-1025, that's for sure. Is there such a thing as psychosomatic cardiac arrest, or does SCP-1025 have some "
							DeathMsg = DeathMsg + "anomalous properties we are not yet aware of?" + Chr(34)
							Kill()
						EndIf
					Else
						HeartBeatRate = Max(HeartBeatRate, 70.0 + SCP1025State[i])
						HeartBeatVolume = 1.0
					EndIf
					;[End Block]
			End Select 
		EndIf
	Next
End Function

Function DrawGUI()
	CatchErrors("Uncaught (DrawGUI)")
	
	Local Temp%, x#, y#, z#, i%, YawValue#, PitchValue#
	Local x2#, y2#, z2#
	Local n%, xTemp, yTemp, StrTemp$
	Local e.Events, it.Items
	
	If MenuOpen Or ConsoleOpen Or SelectedDoor <> Null Or InvOpen Or OtherOpen <> Null Or EndingTimer < 0.0 Then
		ShowPointer()
	Else
		HidePointer()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom Then
				If Float(e\EventStr) < 1000.0 Then
					If e\EventState > 600.0 Then
						If BlinkTimer < -3.0 And BlinkTimer > -10.0 Then
							If e\Img = 0 Then
								If BlinkTimer > -5 And Rand(30) = 1 Then
									PlaySound_Strict(DripSFX(0))
									If e\Img = 0 Then e\Img = LoadImage_Strict("GFX\npcs\106face.jpg")
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
					If BlinkTimer < -3.0 And BlinkTimer > -10.0 Then
						If e\Img = 0 Then
							If BlinkTimer > -5.0 Then
								If e\Img = 0 Then
									e\Img = LoadImage_Strict("GFX\kneelmortal.pd")
									If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage(e\Img, GraphicWidth / 2 - Rand(390, 310), GraphicHeight / 2 - Rand(290, 310))
						EndIf
					Else
						If e\Img <> 0 Then FreeImage(e\Img) : e\Img = 0
						If BlinkTimer < -3 Then
							If ChannelPlaying(e\SoundCHN) = False Then e\SoundCHN = PlaySound_Strict(e\Sound)
						Else
							If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
						EndIf
					EndIf
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	If ClosestButton <> 0 And SelectedDoor = Null And InvOpen = False And MenuOpen = False And OtherOpen = Null Then
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
		
		DrawImage(HandIcon, GraphicWidth / 2 + Sin(YawValue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(PitchValue) * (GraphicHeight / 3) - 32)
		
		If MouseUp1 Then
			MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf Playable Then
					PlaySound2(ButtonSFX, Camera, ClosestButton)
					UseDoor(ClosestDoor, True)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If ClosestItem <> Null Then
		YawValue = -DeltaYaw(Camera, ClosestItem\Collider)
		If YawValue > 90.0 And YawValue =< 180.0 Then YawValue = 90.0
		If YawValue > 180.0 And YawValue < 270.0 Then YawValue = 270.0
		PitchValue = -DeltaPitch(Camera, ClosestItem\Collider)
		If PitchValue > 90.0 And PitchValue =< 180.0 Then PitchValue = 90.0
		If PitchValue > 180.0 And PitchValue < 270.0 Then PitchValue = 270.0
		
		DrawImage(HandIcon2, GraphicWidth / 2 + Sin(YawValue) * (GraphicWidth / 3) - 32, GraphicHeight / 2 - Sin(PitchValue) * (GraphicHeight / 3) - 32)
	EndIf
	
	If DrawHandIcon Then DrawImage(HandIcon, GraphicWidth / 2 - 32, GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon(i) Then
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
			DrawImage(HandIcon, x, y)
			Color(0, 0, 0)
			Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(ArrowIMG(i), x + 21, y + 21)
			DrawArrowIcon(i) = False
		End If
	Next
	
	If Using294 Then Use294()
	
	If HUDenabled Then 
		Local Width% = 204, Height% = 20
		
		x = 80
		y = GraphicHeight - 95
		
		Color(255, 255, 255)
		Rect(x, y, Width, Height, False)
		For i = 1 To Int(((Width - 2) * (BlinkTimer / (BLINKFREQ))) / 10.0)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		Color(0, 0, 0)
		Rect(x - 50, y, 30, 30)
		
		If BlurTimer > 550.0 Or BlinkEffect > 1.0 Or LightFlash > 0.0 Or LightBlink > 0.0 Or EyeIrritation > 0.0 Then
			Color(200, 0, 0)
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		Else
		    If BlinkEffect < 1.0 Or chs\NoBlink Then
		        Color(0, 200, 0)
			    Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
            EndIf
		End If
		
		Color(255, 255, 255)
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		
		DrawImage(BlinkIcon, x - 50, y)
		
		y = GraphicHeight - 55.0
		
		Color(255, 255, 255)
		Rect(x, y, Width, Height, False)
		For i = 1 To Int(((Width - 2) * (Stamina / 100.0)) / 10.0)
			DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		
		Color(0, 0, 0)
		Rect(x - 50, y, 30, 30)
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or Wearing714 > 0 Or Injuries >= 1.5 Or StaminaEffect > 1.0 Or WearingHazmat > 0 Or WearingVest = 2
			Color(200, 0, 0)
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		Else
		    If chs\InfiniteStamina = True Or StaminaEffect < 1.0 Or WearingGasMask = 2 Or Wearing1499 = 2
                Color(0, 200, 0)
			    Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
            EndIf 
		End If
		
		Color(255, 255, 255)
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		If Crouch Then
			DrawImage(CrouchIcon, x - 50, y)
		Else
			DrawImage(SprintIcon, x - 50, y)
		EndIf
		
		If DebugHUD Then
			Color(255, 255, 255)
			AASetFont(ConsoleFont)
			AAText(x - 50, 50, "Player Position: (" + f2s(EntityX(Collider), 3) + ", " + f2s(EntityY(Collider), 3) + ", " + f2s(EntityZ(Collider), 3) + ")")
			AAText(x - 50, 70, "Camera Position: (" + f2s(EntityX(Camera), 3) + ", " + f2s(EntityY(Camera), 3) + ", " + f2s(EntityZ(Camera), 3) + ")")
			AAText(x - 50, 100, "Player Rotation: (" + f2s(EntityPitch(Collider), 3) + ", " + f2s(EntityYaw(Collider), 3) + ", " + f2s(EntityRoll(Collider), 3) + ")")
			AAText(x - 50, 120, "Camera Rotation: (" + f2s(EntityPitch(Camera), 3) + ", " + f2s(EntityYaw(Camera), 3) + ", " + f2s(EntityRoll(Camera), 3) + ")")
			AAText(x - 50, 150, "Room: " + PlayerRoom\RoomTemplate\Name)
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					AAText(x - 50, 170, "Room event: " + ev\EventName)   
					AAText(x - 50, 190, "state: " + ev\EventState)
					AAText(x - 50, 210, "state2: " + ev\EventState2)  
					AAText(x - 50, 230, "state3: " + ev\EventState3)
					AAText(x - 50, 250, "str: "+ ev\EventStr)
					Exit
				EndIf
			Next
			AAText(x - 50, 280, "Room coordinates: (" + Floor(EntityX(PlayerRoom\OBJ) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\OBJ) / 8.0 + 0.5) + ", angle: " + PlayerRoom\Angle + ")")
			AAText(x - 50, 300, "Stamina: " + f2s(Stamina, 3))
			AAText(x - 50, 320, "Death timer: " + f2s(KillTimer, 3))             
			AAText(x - 50, 340, "Blink timer: " + f2s(BlinkTimer, 3))
			AAText(x - 50, 360, "Injuries: " + Injuries)
			AAText(x - 50, 380, "Bloodloss: " + Bloodloss)
			If Curr173 <> Null Then
				AAText(x - 50, 410, "SCP - 173 Position (collider): (" + f2s(EntityX(Curr173\Collider), 3) + ", " + f2s(EntityY(Curr173\Collider), 3) + ", " + f2s(EntityZ(Curr173\Collider), 3) + ")")
				AAText(x - 50, 430, "SCP - 173 Position (obj): (" + f2s(EntityX(Curr173\OBJ), 3) + ", " + f2s(EntityY(Curr173\OBJ), 3) + ", " + f2s(EntityZ(Curr173\OBJ), 3) + ")")
				AAText(x - 50, 450, "SCP - 173 State: " + Curr173\State)
			EndIf
			If Curr106 <> Null Then
				AAText(x - 50, 470, "SCP - 106 Position: (" + f2s(EntityX(Curr106\OBJ), 3) + ", " + f2s(EntityY(Curr106\OBJ), 3) + ", " + f2s(EntityZ(Curr106\OBJ), 3) + ")")
				AAText(x - 50, 490, "SCP - 106 Idle: " + Curr106\Idle)
				AAText(x - 50, 510, "SCP - 106 State: " + Curr106\State)
			EndIf
			Offset% = 0
			For npc.NPCs = Each NPCs
				If npc\NPCtype = NPCtype096 Then
					AAText(x - 50, 530, "SCP - 096 Position: (" + f2s(EntityX(npc\OBJ), 3) + ", " + f2s(EntityY(npc\OBJ), 3) + ", " + f2s(EntityZ(npc\OBJ), 3) + ")")
					AAText(x - 50, 550, "SCP - 096 Idle: " + npc\Idle)
					AAText(x - 50, 570, "SCP - 096 State: " + npc\State)
					AAText(x - 50, 590, "SCP - 096 Speed: " + f2s(npc\CurrSpeed, 5.0))
				EndIf
				If npc\NPCtype = NPCtypeMTF Then
					AAText(x - 50, 620 + 60 * Offset, "MTF " + Offset + " Position: (" + f2s(EntityX(npc\OBJ), 3) + ", " + f2s(EntityY(npc\OBJ), 3) + ", " + f2s(EntityZ(npc\OBJ), 3) + ")")
					AAText(x - 50, 640 + 60 * Offset, "MTF " + Offset + " State: " + npc\State)
					AAText(x - 50, 660 + 60 * Offset, "MTF " + Offset + " LastSeen: " + npc\LastSeen)					
					Offset = Offset + 1
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name = "dimension1499"
				AAText(x + 350, 50, "Current Chunk X/Z: (" + (Int((EntityX(Collider) + 20) / 40)) + ", "+(Int((EntityZ(Collider) + 20) / 40)) + ")")
				
				Local CH_Amount% = 0
				
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				AAText(x + 350, 70, "Current Chunk Amount: " + CH_Amount)
			Else
				AAText(x + 350, 50, "Current Room Position: (" + PlayerRoom\x + ", " + PlayerRoom\y + ", " + PlayerRoom\z + ")")
			EndIf
			GlobalMemoryStatus m.MEMORYSTATUS
			AAText(x + 350, 90, (m\dwAvailPhys / 1024 / 1024) + " MB/" + (m\dwTotalPhys / 1024 / 1024) + " MB (" + (m\dwAvailPhys / 1024) + " KB/" + (m\dwTotalPhys / 1024) + " KB)")
			AAText(x + 350, 110, "Triangles rendered: " + CurrTrisAmount)
			AAText(x + 350, 130, "Active textures: " + ActiveTextures())
			AAText(x + 350, 150, "SCP-427 state (secs): " + Int(I_427\Timer / 70))
			AAText(x + 350, 170, "SCP-008 infection: " + Infect)
			For i = 0 To 5
				AAText(x + 350, 190 + (20 * i), "SCP-1025 State " + i + ": " + SCP1025State[i])
			Next
			If SelectedMonitor <> Null Then
				AAText(x + 350, 310, "Current monitor: " + SelectedMonitor\ScrOBJ)
			Else
				AAText(x + 350, 310, "Current monitor: NULL")
			EndIf
			AASetFont(Font1)
		EndIf
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage(SelectedScreen\Img, GraphicWidth / 2 - ImageWidth(SelectedScreen\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedScreen\Img) / 2)
		
		If MouseUp1 Or MouseHit2 Then
			FreeImage(SelectedScreen\Img) : SelectedScreen\Img = 0
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	Local ShouldDrawHUD% = True
	
	If SelectedDoor <> Null Then
		SelectedItem = Null
		If ShouldDrawHUD Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(ClosestButton, True), EntityY(ClosestButton, True), EntityZ(ClosestButton, True))
			RotateEntity(Pvt, 0.0, EntityYaw(ClosestButton, True) - 180.0, 0.0)
			MoveEntity(Pvt, 0.0, 0.0, 0.22)
			PositionEntity(Camera, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
			PointEntity(Camera, ClosestButton)
			FreeEntity(Pvt)
			
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) + MeshHeight(ButtonOBJ) * 0.015, EntityZ(ClosestButton, True))
			ProjY# = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton, True), EntityY(ClosestButton, True) - MeshHeight(ButtonOBJ) * 0.015, EntityZ(ClosestButton, True))
			Scale# = (ProjectedY() - Projy) / 462.0
			
			x = GraphicWidth / 2 - ImageWidth(KeypadHUD) * Scale / 2
			y = GraphicHeight / 2 - ImageHeight(KeypadHUD) * Scale / 2		
			
			AASetFont(Font3)
			If KeypadMsg <> "" Then 
				KeypadTimer = KeypadTimer - FPSfactor2
				
				If (KeypadTimer Mod 70.0) < 35.0 Then AAText(GraphicWidth / 2, y + 124 * Scale, KeypadMsg, True, True)
				If KeypadTimer =< 0 Then
					KeypadMsg = ""
					SelectedDoor = Null
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				EndIf
			Else
				AAText(GraphicWidth / 2, y + 70 * Scale, "ACCESS CODE: ", True, True)	
				AASetFont(Font4)
				AAText(GraphicWidth / 2, y + 124 * Scale, KeypadInput, True, True)
			EndIf
			
			x = x + 44 * Scale
			y = y + 249 * Scale
			
			For n = 0 To 3
				For i = 0 To 2
					xTemp = x + Int(58.5 * Scale * n)
					yTemp = y + (67.0 * Scale) * i
					
					Temp = False
					If MouseOn(xTemp, yTemp, 54 * Scale, 65 * Scale) And KeypadMsg = "" Then
						If MouseUp1 Then 
							PlaySound_Strict(ButtonSFX)
							
							Select (n + 1) + (i * 4)
								Case 1, 2, 3
									;[Block]
									KeypadInput = KeypadInput + ((n + 1) + (i * 4))
									;[End Block]
								Case 4
									;[Block]
									KeypadInput = KeypadInput + "0"
									;[End Block]
								Case 5, 6, 7
									;[Block]
									KeypadInput = KeypadInput + ((n + 1) + (i * 4) - 1)
									;[End Block]
								Case 8
									;[Block]
									If KeypadInput = SelectedDoor\Code Then
										PlaySound_Strict(ScannerSFX1)
										If SelectedDoor\Code = Str(AccessCode) Then
											GiveAchievement(AchvMaynard)
										ElseIf SelectedDoor\Code = "7816"
											GiveAchievement(AchvHarp)
										EndIf									
										
										SelectedDoor\Locked = 0
										UseDoor(SelectedDoor, True)
										SelectedDoor = Null
										MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
									Else
										PlaySound_Strict(ScannerSFX2)
										KeypadMsg = "ACCESS DENIED"
										KeypadTimer = 210
										KeypadInput = ""	
									EndIf
									;[End Block]
								Case 9, 10, 11
									;[Block]
									KeypadInput = KeypadInput + ((n + 1) + (i * 4) - 2)
									;[End Block]
								Case 12
									;[Block]
									KeypadInput = ""
									;[End Block]
							End Select 
							If Len(KeypadInput) > 4 Then KeypadInput = Left(KeypadInput, 4)
						EndIf
					Else
						Temp = False
					EndIf
				Next
			Next
			If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
			
			If MouseHit2 Then
				SelectedDoor = Null
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		KeypadInput = ""
		KeypadTimer = 0
		KeypadMsg = ""
	EndIf
	
	If KeyHit(1) And EndingTimer = 0.0 And (Not Using294) Then
		If MenuOpen Or InvOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
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
			If Instr(SelectedItem\ItemTemplate\TempName, "vest") Or Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") Then
				If WearingVest = 0 And WearingHazmat = 0 Then
					DropItem(SelectedItem)
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local Spacing%
	Local PrevOtherOpen.Items
	Local OtherSize%, OtherAmount%
	Local IsEmpty%
	Local IsMouseOn%
	Local ClosedInv%
	
	If OtherOpen <> Null Then
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity(Fog)
			CameraFogRange(Camera, 5, 30)
			CameraFogColor(Camera, 200, 200, 200)
			CameraClsColor(Camera, 200, 200, 200)					
			CameraRange(Camera, 0.05, 30)
		Else If (PlayerRoom\RoomTemplate\Name = "exit1") And (EntityY(Collider) > 1040.0 * RoomScale)
			HideEntity(Fog)
			CameraFogRange(Camera, 5, 45)
			CameraFogColor(Camera, 200, 200, 200)
			CameraClsColor(Camera, 200, 200, 200)					
			CameraRange(Camera, 0.05, 60)
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
		
		Width = 70
		Height = 70
		Spacing = 35
		
		x = GraphicWidth / 2 - (Width * MaxItemAmount / 2 + Spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - (Height * OtherSize / 5 + Spacing * (OtherSize / 5 - 1)) / 2
		
		ItemAmount = 0
		For n = 0 To OtherSize - 1
			IsMouseOn = False
			If ScaledMouseX() > x And ScaledMouseX() < x + Width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + Height Then
					IsMouseOn = True
				EndIf
			EndIf
			
			If IsMouseOn Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, Width + 2, Height + 2)
			EndIf
			
			DrawFrame(x, y, Width, Height, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (SelectedItem <> OtherOpen\SecondInv[n] Or IsMouseOn) Then DrawImage(OtherOpen\SecondInv[n]\InvImg, x + Width / 2 - 32, y + Height / 2 - 32)
			EndIf
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If IsMouseOn Then
					Color(255, 255, 255)	
					AAText(x + Width / 2, y + Height + Spacing - 15, OtherOpen\SecondInv[n]\ItemTemplate\Name, True)				
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							MouseHit1 = False
							
							If DoubleClick Then
								If OtherOpen\SecondInv[n]\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(OtherOpen\SecondInv[n]\ItemTemplate\Sound))
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
				If IsMouseOn And MouseHit1 Then
					For z = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
			EndIf					
			
			x = x + Width + Spacing
			TempX = TempX + 1
			If TempX = 5 Then 
				TempX = 0
				y = y + Height * 2 
				x = GraphicWidth / 2 - (Width * MaxItemAmount / 2 + Spacing * (MaxItemAmount / 2 - 1.0)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
					ShowEntity(SelectedItem\Collider)
					PositionEntity(SelectedItem\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\Collider, EntityPitch(Camera), EntityYaw(Camera), 0.0)
					MoveEntity(SelectedItem\Collider, 0.0, -0.1, 0.1)
					RotateEntity(SelectedItem\Collider, 0.0, Rand(360), 0.0)
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
									
									If Name <> "25ct" And Name <> "coin" And Name <> "key" And Name <> "scp860" Then
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
								Msg = "You cannot combine these two items."
								MsgTimer = 70 * 5.0
								;[End Block]
						End Select					
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
		If (ClosedInv) And (Not InvOpen) Then 
			ResumeSounds() 
			OtherOpen = Null
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	Else If InvOpen Then
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity(Fog)
			CameraFogRange(Camera, 5.0, 30.0)
			CameraFogColor(Camera, 200.0, 200.0, 200.0)
			CameraClsColor(Camera, 200.0, 200.0, 200.0)					
			CameraRange(Camera, 0.05, 30.0)
		ElseIf (PlayerRoom\RoomTemplate\Name = "exit1") And (EntityY(Collider) > 1040.0 * RoomScale)
			HideEntity(Fog)
			CameraFogRange(Camera, 5.0, 45.0)
			CameraFogColor(Camera, 200.0, 200.0, 200.0)
			CameraClsColor(Camera, 200.0, 200.0, 200.0)					
			CameraRange(Camera, 0.05, 60.0)
		EndIf
		
		SelectedDoor = Null
		
		Width = 70.0
		Height = 70.0
		Spacing = 35
		
		x = GraphicWidth / 2 - (Width * MaxItemAmount / 2 + Spacing * (MaxItemAmount / 2 - 1)) / 2
		y = GraphicHeight / 2 - Height
		
		ItemAmount = 0
		For n = 0 To MaxItemAmount - 1
			IsMouseOn = False
			If ScaledMouseX() > x And ScaledMouseX() < x + Width Then
				If ScaledMouseY() > y And ScaledMouseY() < y + Height Then
					IsMouseOn = True
				End If
			EndIf
			
			If Inventory(n) <> Null Then
				Color(200, 200, 200)
				Select Inventory(n)\ItemTemplate\TempName 
					Case "gasmask"
						;[Block]
						If WearingGasMask = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "supergasmask"
						;[Block]
						If WearingGasMask = 2 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "gasmask3"
						;[Block]
						If WearingGasMask = 3 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "hazmatsuit"
						;[Block]
						If WearingHazmat = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "hazmatsuit2"
						;[Block]
						If WearingHazmat = 2 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "hazmatsuit3
						;[Block]"
						If WearingHazmat = 3 Then Rect(x - 3, y - 3, Width + 6, Height + 6)	
						;[End Block]
					Case "vest"
						;[Block]
						If WearingVest = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "finevest"
						;[Block]
						If WearingVest = 2 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "scp714"
						;[Block]
						If Wearing714 = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "nvgoggles"
						;[Block]
						If WearingNightVision = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "supernv"
						;[Block]
						If WearingNightVision = 2 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "scp1499"
						;[Block]
						If Wearing1499 = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "super1499"
						;[Block]
						If Wearing1499 = 2 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "finenvgoggles"
						;[Block]
						If WearingNightVision = 3 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
					Case "scp427"
						;[Block]
						If I_427\Using = 1 Then Rect(x - 3, y - 3, Width + 6, Height + 6)
						;[End Block]
				End Select
			EndIf
			
			If IsMouseOn Then
				MouseSlot = n
				Color(255, 0, 0)
				Rect(x - 1, y - 1, Width + 2, Height + 2)
			EndIf
			
			Color(255, 255, 255)
			DrawFrame(x, y, Width, Height, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null Then
				If (SelectedItem <> Inventory(n) Or IsMouseOn) Then 
					DrawImage(Inventory(n)\InvImg, x + Width / 2 - 32, y + Height / 2 - 32)
				EndIf
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				If IsMouseOn Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory(n)
							MouseHit1 = False
							
							If DoubleClick Then
								If WearingHazmat > 0 And Instr(SelectedItem\ItemTemplate\TempName, "hazmatsuit") = 0 Then
									Msg = "You cannot use any items while wearing a hazmat suit."
									MsgTimer = 70 * 5.0
									SelectedItem = Null
									Return
								EndIf
								If Inventory(n)\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(Inventory(n)\ItemTemplate\Sound))
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
						
						AASetFont(Font1)
						Color(0, 0, 0)
						AAText(x + Width / 2 + 1, y + Height + Spacing - 15 + 1, Inventory(n)\Name, True)							
						Color(255, 255, 255)	
						AAText(x + Width / 2, y + Height + Spacing - 15, Inventory(n)\Name, True)	
					EndIf
				EndIf
				ItemAmount = ItemAmount + 1
			Else
				If IsMouseOn And MouseHit1 Then
					For z = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem Then Inventory(z) = Null
					Next
					Inventory(n) = SelectedItem
				End If
			EndIf					
			
			x = x + Width + Spacing
			If n = 4 Then 
				y = y + Height * 2 
				x = GraphicWidth / 2 - (Width * MaxItemAmount / 2 + Spacing * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawImage(SelectedItem\InvImg, ScaledMouseX() - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					Select SelectedItem\ItemTemplate\TempName
						Case "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
							;[Block]
							Msg = "Double click on this item to take it off."
							MsgTimer = 70 * 5.0
							;[End Block]
						Case "scp1499", "super1499"
							;[Block]
							If Wearing1499 > 0 Then
								Msg = "Double click on this item to take it off."
								MsgTimer = 70 * 5.0
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
					If Inventory(MouseSlot) = Null Then
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem Then Inventory(z) = Null
						Next
						Inventory(MouseSlot) = SelectedItem
						SelectedItem = Null
					ElseIf Inventory(MouseSlot) <> SelectedItem
						Select SelectedItem\ItemTemplate\TempName
							Case "paper", "key1", "key2", "key3", "key4", "key5", "key6", "misc", "oldpaper", "badge", "ticket", "25ct", "coin", "key", "scp860"
								;[Block]
								If Inventory(MouseSlot)\ItemTemplate\TempName = "clipboard" Then
									; ~ Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\ItemTemplate\TempName
									Local b2$ = SelectedItem\ItemTemplate\Name
									
									If (b <> "misc" And b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860") Or (b2 = "Playing Card" Or b2 = "Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\InvSlots - 1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\State = 1.0
													SetAnimTime(Inventory(MouseSlot)\Model, 0.0)
													Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = "The paperclip is not strong enough to hold any more items."
										Else
											If added\ItemTemplate\TempName = "paper" Or added\ItemTemplate\TempName = "oldpaper" Then
												Msg = "This document was added to the clipboard."
											ElseIf added\ItemTemplate\TempName = "badge"
												Msg = added\ItemTemplate\Name + " was added to the clipboard."
											Else
												Msg = "The " + added\ItemTemplate\Name + " was added to the clipboard."
											EndIf
										EndIf
										MsgTimer = 70 * 5.0
									Else
										Msg = "You cannot combine these two items."
										MsgTimer = 70 * 5.0
									EndIf
								ElseIf Inventory(MouseSlot)\ItemTemplate\TempName = "wallet" Then
									; ~ Add an item to clipboard
									added.Items = Null
									b = SelectedItem\ItemTemplate\TempName
									b2 = SelectedItem\ItemTemplate\Name
									If (b <> "misc" And b <> "paper" And b <> "oldpaper") Or (b2 = "Playing Card" Or b2 = "Mastercard") Then
										For c% = 0 To Inventory(MouseSlot)\InvSlots - 1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\State = 1.0
													If b <> "25ct" And b <> "coin" And b <> "key" And b <> "scp860"
														SetAnimTime(Inventory(MouseSlot)\Model, 3.0)
													EndIf
													Inventory(MouseSlot)\InvImg = Inventory(MouseSlot)\ItemTemplate\InvImg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = "The wallet is full."
										Else
											Msg = "You put " + added\ItemTemplate\Name + " into the wallet."
										EndIf
										MsgTimer = 70 * 5.0
									Else
										Msg = "You cannot combine these two items."
										MsgTimer = 70 * 5.0
									EndIf
								Else
									Msg = "You cannot combine these two items."
									MsgTimer = 70.0 * 5.0
								EndIf
								SelectedItem = Null
								;[End Block]
							Case "battery", "bat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\Name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										;[Block]
										If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))	
										RemoveItem(SelectedItem)
										SelectedItem = Null
										Inventory(MouseSlot)\State = 100.0
										Msg = "You replaced the navigator's battery."
										MsgTimer = 70 * 5.0
										;[End Block]
									Case "S-NAV Navigator Ultimate"
										;[Block]
										Msg = "There seems to be no place for batteries in this navigator."
										MsgTimer = 70 * 5.0
										;[End Block]
									Case "Radio Transceiver"
										;[Block]
										Select Inventory(MouseSlot)\ItemTemplate\TempName 
											Case "fineradio", "veryfineradio"
												;[Block]
												Msg = "There seems to be no place for batteries in this radio."
												MsgTimer = 70 * 5.0
												;[End Block]
											Case "18vradio"
												;[Block]
												Msg = "The battery does not fit inside this radio."
												MsgTimer = 70 * 5.0
												;[End Block]
											Case "radio"
												;[Block]
												If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))	
												RemoveItem(SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\State = 100.0
												Msg = "You replaced the radio's battery."
												MsgTimer = 70.0 * 5.0
												;[End Block]
										End Select
										;[End Block]
									Case "Night Vision Goggles"
										;[Block]
										Local NVName$ = Inventory(MouseSlot)\ItemTemplate\TempName
										
										If NVName = "nvgoggles" Or NVName = "supernv" Then
											If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))	
											RemoveItem (SelectedItem)
											SelectedItem = Null
											Inventory(MouseSlot)\State = 1000.0
											Msg = "You replaced the goggles' battery."
											MsgTimer = 70 * 5.0
										Else
											Msg = "There seems to be no place for batteries in these night vision goggles."
											MsgTimer = 70 * 5.0
										EndIf
										;[End Block]
									Default
										;[Block]
										Msg = "You cannot combine these two items."
										MsgTimer = 70.0 * 5.0
										;[End Block]
								End Select
								;[End Block]
							Case "18vbat"
								;[Block]
								Select Inventory(MouseSlot)\ItemTemplate\Name
									Case "S-NAV Navigator", "S-NAV 300 Navigator", "S-NAV 310 Navigator"
										;[Block]
										Msg = "The battery does not fit inside this navigator."
										MsgTimer = 70 * 5.0
										;[End Block]
									Case "S-NAV Navigator Ultimate"
										;[Block]
										Msg = "There seems to be no place for batteries in this navigator."
										MsgTimer = 70 * 5.0
										;[End Block]
									Case "Radio Transceiver"
										;[Block]
										Select Inventory(MouseSlot)\ItemTemplate\TempName 
											Case "fineradio", "veryfineradio"
												;[Block]
												Msg = "There seems to be no place for batteries in this radio."
												MsgTimer = 70 * 5.0	
												;[End Block]
											Case "18vradio"
												;[Block]
												If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))	
												RemoveItem(SelectedItem)
												SelectedItem = Null
												Inventory(MouseSlot)\State = 100.0
												Msg = "You replaced the radio's battery."
												MsgTimer = 70 * 5.0
												;[End Block]
										End Select 
										;[End Block]
									Default
										;[Block]
										Msg = "You cannot combine these two items."
										MsgTimer = 70 * 5.0	
										;[End Block]
								End Select
								;[End Block]
							Default
								;[Block]
								Msg = "You cannot combine these two items."
								MsgTimer = 70 * 5.0
								;[End Block]
						End Select					
					End If
				End If
				SelectedItem = Null
			End If
		End If
		
		If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
		
		If InvOpen = False Then 
			ResumeSounds() 
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	Else
		If SelectedItem <> Null Then
			Select SelectedItem\ItemTemplate\TempName
				Case "nvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingNightVision = 1 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30.0
						EndIf
						
						WearingNightVision = (Not WearingNightVision)
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5.0
					;[End Block]
				Case "supernv"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingNightVision = 2 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30.0
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 2
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5.0
					;[End Block]
				Case "finenvgoggles"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingNightVision = 3 Then
							Msg = "You removed the goggles."
							CameraFogFar = StoredCameraFogFar
						Else
							Msg = "You put on the goggles."
							WearingGasMask = 0
							WearingNightVision = 0
							StoredCameraFogFar = CameraFogFar
							CameraFogFar = 30
						EndIf
						
						WearingNightVision = (Not WearingNightVision) * 3
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the goggles."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the goggles."
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5.0
					;[End Block]
				Case "scp1123"
					;[Block]
					If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then
						If PlayerRoom\RoomTemplate\Name <> "room1123" Then
							ShowEntity(Light)
							LightFlash = 7.0
							PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
							DeathMsg = "Subject D-9341 was shot dead after attempting to attack a member of Nine-Tailed Fox. Surveillance tapes show that the subject had been "
							DeathMsg = DeathMsg + "wandering around the site approximately 9 minutes prior, shouting the phrase " + Chr(34) + "get rid of the four pests" + Chr(34)
							DeathMsg = DeathMsg + " in chinese. SCP-1123 was found in [DATA REDACTED] nearby, suggesting the subject had come into physical contact with it. How "
							DeathMsg = DeathMsg + "exactly SCP-1123 was removed from its containment chamber is still unknown."
							Kill()
							Return
						EndIf
						For e.Events = Each Events
							If e\EventName = "room1123" Then 
								If e\EventState = 0.0 Then
									ShowEntity(Light)
									LightFlash = 3.0
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
								EndIf
								e\EventState = Max(1.0, e\EventState)
								Exit
							EndIf
						Next
					EndIf
					;[End Block]
				Case "key1", "key2", "key3", "key4", "key5", "key6", "scp860", "hand", "hand2", "25ct"
					;[Block]
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					;[End Block]
				Case "scp513"
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell1.ogg"))
					
					If Curr5131 = Null
						Curr5131 = CreateNPC(NPCtype5131, 0.0, 0.0, 0.0)
					EndIf	
					SelectedItem = Null
					;[End Block]
				Case "scp500"
					;[Block]
					If CanUseItem(False, False, True)
						GiveAchievement(Achv500)
						
						If Infect > 0 Then
							Msg = "You swallowed the pill. Your nausea is fading."
						Else
							Msg = "You swallowed the pill."
						EndIf
						MsgTimer = 70 * 7.0
						
						DeathTimer = 0.0
						Infect = 0.0
						Stamina = 100.0
						
						For i = 0 To 5
							SCP1025State[i] = 0.0
						Next
						
						If StaminaEffect > 1.0 Then
							StaminaEffect = 1.0
							StaminaEffectTimer = 0.0
						EndIf
						
						If BlinkEffect > 1.0 Then
							BlinkEffect = 1.0
							BlinkEffectTimer = 0.0
						EndIf
						
						For e.Events = Each Events
							If e\EventName = "1048a" Then 
								If e\EventState2 > 0.0 Then
									If PlayerRoom = e\room Then BlinkTimer = -10.0
									If e\room\Objects[0] <> 0 Then
										FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
									EndIf
									Msg = "You swallowed the pill. Ear-like organs are falling from your body."
									MsgTimer = 70 * 7.0
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
					If CanUseItem(False, False, True)
						Select Rand(5)
							Case 1
								;[Block]
								Injuries = 3.5
								Msg = "You started bleeding heavily."
								MsgTimer = 70 * 7.0
								;[End Block]
							Case 2
								;[Block]
								Injuries = 0.0
								Bloodloss = 0.0
								Msg = "Your wounds are healing up rapidly."
								MsgTimer = 70 * 7.0
								;[End Block]
							Case 3
								;[Block]
								Injuries = Max(0.0, Injuries - Rnd(0.5, 3.5))
								Bloodloss = Max(0.0, Bloodloss - Rnd(10.0, 100.0))
								Msg = "You feel much better."
								MsgTimer = 70 * 7.0
								;[End Block]
							Case 4
								;[Block]
								BlurTimer = 10000.0
								Bloodloss = 0.0
								Msg = "You feel nauseated."
								MsgTimer = 70 * 7.0
								;[End Block]
							Case 5
								;[Block]
								BlinkTimer = -10.0
								
								Local RoomName$ = PlayerRoom\RoomTemplate\Name
								
								If RoomName = "dimension1499" Or RoomName = "gatea" Or (RoomName="exit1" And EntityY(Collider) > 1040.0 * RoomScale)
									Injuries = 2.5
									Msg = "You started bleeding heavily."
									MsgTimer = 70.0 * 7.0
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "pocketdimension" Then
											PositionEntity(Collider, EntityX(r\OBJ), 0.8, EntityZ(r\OBJ))		
											ResetEntity(Collider)									
											UpdateDoors()
											UpdateRooms()
											PlaySound_Strict(Use914SFX)
											DropSpeed = 0.0
											Curr106\State = -2500.0
											Exit
										EndIf
									Next
									Msg = "For some inexplicable reason, you find yourself inside the pocket dimension."
									MsgTimer = 70.0 * 8.0
								EndIf
								;[End Block]
						End Select
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "firstaid", "finefirstaid", "firstaid2"
					;[Block]
					If Bloodloss = 0.0 And Injuries = 0.0 Then
						Msg = "You do not need to use a first aid right now."
						MsgTimer = 70 * 5.0
						SelectedItem = Null
					Else
						If CanUseItem(False, True, True)
							CurrSpeed = CurveValue(0.0, CurrSpeed, 5.0)
							Crouch = True
							
							DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
							
							Width = 300.0
							Height = 20.0
							x = GraphicWidth / 2 - Width / 2
							y = GraphicHeight / 2 + 80
							Rect(x, y, Width + 4, Height, False)
							For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
							SelectedItem\State = Min(SelectedItem\State + (FPSfactor / 5.0), 100.0)			
							
							If SelectedItem\State = 100.0 Then
								If SelectedItem\ItemTemplate\TempName = "finefirstaid" Then
									Bloodloss = 0.0
									Injuries = Max(0.0, Injuries - 2.0)
									If Injuries = 0 Then
										Msg = "You bandaged the wounds and took a painkiller. You feel fine."
									ElseIf Injuries > 1.0
										Msg = "You bandaged the wounds and took a painkiller, but you were not able to stop the bleeding."
									Else
										Msg = "You bandaged the wounds and took a painkiller, but you still feel sore."
									EndIf
									MsgTimer = 70 * 5.0
									RemoveItem(SelectedItem)
								Else
									Bloodloss = Max(0.0, Bloodloss - Rnd(10.0, 20.0))
									If Injuries >= 2.5 Then
										Msg = "The wounds were way too severe to staunch the bleeding completely."
										Injuries = Max(2.5, Injuries - Rnd(0.3, 0.7))
									ElseIf Injuries > 1.0
										Injuries = Max(0.5, Injuries - Rnd(0.5, 1.0))
										If Injuries > 1.0 Then
											Msg = "You bandaged the wounds but were unable to staunch the bleeding completely."
										Else
											Msg = "You managed to stop the bleeding."
										EndIf
									Else
										If Injuries > 0.5 Then
											Injuries = 0.5
											Msg = "You took a painkiller, easing the pain slightly."
										Else
											Injuries = 0.5
											Msg = "You took a painkiller, but it still hurts to walk."
										EndIf
									EndIf
									
									If SelectedItem\ItemTemplate\TempName = "firstaid2" Then 
										Select Rand(6)
											Case 1
												;[Block]
												SuperMan = True
												Msg = "You have becomed overwhelmedwithadrenalineholyshitWOOOOOO~!"
												;[End Block]
											Case 2
												;[Block]
												InvertMouse = (Not InvertMouse)
												Msg = "You suddenly find it very difficult to turn your head."
												;[End Block]
											Case 3
												;[Block]
												BlurTimer = 5000.0
												Msg = "You feel nauseated."
												;[End Block]
											Case 4
												;[Block]
												BlinkEffect = 0.6
												BlinkEffectTimer = Rnd(20.0, 30.0)
												;[End Block]
											Case 5
												;[Block]
												Bloodloss = 0.0
												Injuries = 0.0
												Msg = "You bandaged the wounds. The bleeding stopped completely and you feel fine."
												;[End Block]
											Case 6
												;[Block]
												Msg = "You bandaged the wounds and blood started pouring heavily through the bandages."
												Injuries = 3.5
												;[End Block]
										End Select
									EndIf
									MsgTimer = 70 * 5.0
									RemoveItem(SelectedItem)
								EndIf							
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "eyedrops"
					;[Block]
					If CanUseItem(False, False, False)
						BlinkEffect = 0.6
						BlinkEffectTimer = Rnd(20.0, 30.0)
						BlurTimer = 200.0
						
						Msg = "You used the eyedrops. Your eyes feel moisturized."
				        MsgTimer = 70 * 5.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "fineeyedrops"
					;[Block]
					If CanUseItem(False, False, False)
						BlinkEffect = 0.4
						BlinkEffectTimer = Rnd(30.0, 40.0)
						Bloodloss = Max(Bloodloss - 1.0, 0.0)
						BlurTimer = 200.0
						
						Msg = "You used the eyedrops. Your eyes feel very moisturized."
					    MsgTimer = 70 * 5.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "supereyedrops"
					;[Block]
					If CanUseItem(False, False, False)
						BlinkEffect = 0.0
						BlinkEffectTimer = 60.0
						EyeStuck = 10000.0
						BlurTimer = 1000.0
						
						Msg = "You used the eyedrops. Your eyes feel very moisturized."
					    MsgTimer = 70 * 5.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "paper", "ticket"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						Select SelectedItem\ItemTemplate\Name
							Case "Burnt Note" 
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\bn.it")
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(0, 0, 0)
								AAText(277, 469, AccessCode, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Document SCP-372"
								;[Block]
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\Imgpath)	
								SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
								
								SetBuffer(ImageBuffer(SelectedItem\ItemTemplate\Img))
								Color(37, 45, 137)
								AASetFont(Font5)
								Temp = ((Int(AccessCode) * 3) Mod 10000)
								If Temp < 1000 Then Temp = Temp + 1000
								AAText(383 * MenuScale, 734 * MenuScale, Temp, True, True)
								Color(255, 255, 255)
								SetBuffer(BackBuffer())
								;[End Block]
							Case "Movie Ticket"
								;[Block]
								; ~ Don't resize because it messes up the masking
								SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
								
								If (SelectedItem\State = 0.0) Then
									Msg = Chr(34) + "Hey, I remember this movie!" + Chr(34)
									MsgTimer = 70 * 10.0
									PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(1, 5) + ".ogg"))
									SelectedItem\State = 1.0
								EndIf
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
						SelectedItem\State = Rand(0.0, 5.0)
						SelectedItem\ItemTemplate\Img = LoadImage_Strict("GFX\items\1025\1025_" + Int(SelectedItem\State) + ".jpg")	
						SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then SCP1025State[SelectedItem\State] = Max(1.0, SCP1025State[SelectedItem\State])
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					;[End Block]
				Case "cup"
					;[Block]
					If CanUseItem(False, False, True)
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
						If StrTemp <> "" Then Msg = StrTemp : MsgTimer = 70.0 * 6.0
						
						If GetINIInt2(INIStr, Loc, "lethal") Or GetINIInt2(INIStr, Loc, "deathtimer") Then 
							DeathMsg = GetINIString2(INIStr, Loc, "deathmessage")
							If GetINIInt2(INIStr, Loc, "lethal") Then Kill()
						EndIf
						BlurTimer = GetINIInt2(INIStr, Loc, "blur") * 70.0
						If VomitTimer = 0.0 Then VomitTimer = GetINIInt2(INIStr, Loc, "vomit")
						CameraShakeTimer = GetINIString2(INIStr, Loc, "camerashake")
						Injuries = Max(Injuries + GetINIInt2(INIStr, Loc, "damage"), 0.0)
						Bloodloss = Max(Bloodloss + GetINIInt2(INIStr, Loc, "blood loss"), 0.0)
						StrTemp =  GetINIString2(INIStr, Loc, "sound")
						If StrTemp <> "" Then
							PlaySound_Strict(LoadTempSound(StrTemp))
						EndIf
						If GetINIInt2(INIStr, Loc, "stomachache") Then SCP1025State[3] = 1.0
						
						DeathTimer = GetINIInt2(INIStr, Loc, "deathtimer") * 70.0
						
						BlinkEffect = Float(GetINIString2(INIStr, Loc, "blink effect", 1.0)) * x2
						BlinkEffectTimer = Float(GetINIString2(INIStr, Loc, "blink effect timer", 1.0)) * x2
						
						StaminaEffect = Float(GetINIString2(INIStr, Loc, "stamina effect", 1.0)) * x2
						StaminaEffectTimer = Float(GetINIString2(INIStr, Loc, "stamina effect timer", 1.0)) * x2
						
						StrTemp = GetINIString2(INIStr, Loc, "refusemessage")
						If StrTemp <> "" Then
							Msg = StrTemp 
							MsgTimer = 70 * 6.0		
						Else
							it.Items = CreateItem("Empty Cup", "emptycup", 0.0, 0.0, 0.0)
							it\Picked = True
							For i = 0 To MaxItemAmount - 1
								If Inventory(i) = SelectedItem Then Inventory(i) = it : Exit
							Next					
							EntityType(it\Collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)						
						EndIf
						
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "syringe"
					;[Block]
					If CanUseItem(False, True, True)
						HealTimer = 30.0
						StaminaEffect = 0.5
						StaminaEffectTimer = 20.0
						
						Msg = "You injected yourself with the syringe and feel a slight adrenaline rush."
						MsgTimer = 70 * 8.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "finesyringe"
					;[Block]
					If CanUseItem(False, True, True)
						HealTimer = Rnd(20.0, 40.0)
						StaminaEffect = Rnd(0.5, 0.8)
						StaminaEffectTimer = Rnd(20.0, 30.0)
						
						Msg = "You injected yourself with the syringe and feel an adrenaline rush."
						MsgTimer = 70 * 8.0
						
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					If CanUseItem(False, True, True)
						Select Rand(3)
							Case 1
								;[Block]
								HealTimer = Rnd(40.0, 60.0)
								StaminaEffect = 0.1
								StaminaEffectTimer = 30.0
								Msg = "You injected yourself with the syringe and feel a huge adrenaline rush."
								;[End Block]
							Case 2
								;[Block]
								SuperMan = True
								Msg = "You injected yourself with the syringe and feel a humongous adrenaline rush."
								;[End Block]
							Case 3
								;[Block]
								VomitTimer = 30.0
								Msg = "You injected yourself with the syringe and feel a pain in your stomach."
								;[End Block]
						End Select
						
						MsgTimer = 70 * 8.0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "radio", "18vradio", "fineradio", "veryfineradio"
					;[Block]
					If SelectedItem\State =< 100.0 Then SelectedItem\State = Max(0.0, SelectedItem\State - FPSfactor * 0.004)
					
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					; ~ RadioState(5) = has the "use the number keys" -message been shown yet (True / False)
					; ~ RadioState(6) = a timer for the "code channel"
					; ~ RadioState(7) = another timer for the "code channel"
					
					If RadioState(5) = 0 Then 
						Msg = "Use the numbered keys 1 through 5 to cycle between various channels."
						MsgTimer = 70 * 5.0
						RadioState(5) = 1
						RadioState(0) = -1
					EndIf
					
					StrTemp = ""
					
					x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img)
					y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img)
					
					DrawImage(SelectedItem\ItemTemplate\Img, x, y)
					
					If SelectedItem\State > 0.0 Then
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or CoffinDistance < 4.0 Then
							ResumeChannel(RadioCHN(5))
							If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)	
						Else
							Select Int(SelectedItem\State2)
								Case 0
									;[Block]
									ResumeChannel(RadioCHN(0))
									StrTemp = "        USER TRACK PLAYER - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										StrTemp = StrTemp + "NOT ENABLED     "
									ElseIf UserTrackMusicAmount < 1
										If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
										StrTemp = StrTemp + "NO TRACKS FOUND     "
									Else
										If ChannelPlaying(RadioCHN(0)) = False Then
											If (Not UserTrackFlag)
												If UserTrackMode
													If RadioState(0) < (UserTrackMusicAmount - 1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack)
										Else
											StrTemp = StrTemp + Upper(UserTrackName(RadioState(0))) + "          "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict(RadioSquelch)
											If (Not UserTrackFlag)
												If UserTrackMode
													If RadioState(0) < (UserTrackMusicAmount - 1)
														RadioState(0) = RadioState(0) + 1
													Else
														RadioState(0) = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState(0) = Rand(0, UserTrackMusicAmount - 1)
												EndIf
											EndIf
											If CurrUserTrack <> 0 Then FreeSound_Strict(CurrUserTrack) : CurrUserTrack = 0
											CurrUserTrack = LoadSound_Strict("SFX\Radio\UserTracks\" + UserTrackName(RadioState(0)))
											RadioCHN(0) = PlaySound_Strict(CurrUserTrack)
										EndIf
									EndIf
									;[End Block]
								Case 1
									;[Block]
									ResumeChannel(RadioCHN(1))
									StrTemp = "        WARNING - CONTAINMENT BREACH          "
									If ChannelPlaying(RadioCHN(1)) = False Then
										
										If RadioState(1) >= 5 Then
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1, 1))	
											RadioState(1) = 0
										Else
											RadioState(1) = RadioState(1) + 1	
											RadioCHN(1) = PlaySound_Strict(RadioSFX(1, 0))	
										EndIf
									EndIf
									;[End Block]
								Case 2
									;[Block]
									ResumeChannel(RadioCHN(2))
									StrTemp = "        SCP Foundation On-Site Radio          "
									If ChannelPlaying(RadioCHN(2)) = False Then
										RadioState(2) = RadioState(2) + 1
										If RadioState(2) = 17 Then RadioState(2) = 1
										If Floor(RadioState(2) / 2) = Ceil(RadioState(2) / 2) Then
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2, Int(RadioState(2) / 2)))	
										Else
											RadioCHN(2) = PlaySound_Strict(RadioSFX(2, 0))
										EndIf
									EndIf 
									;[End Block]
								Case 3
									;[Block]
									ResumeChannel(RadioCHN(3))
									StrTemp = "             EMERGENCY CHANNEL - RESERVED FOR COMMUNICATION IN THE EVENT OF A CONTAINMENT BREACH         "
									If ChannelPlaying(RadioCHN(3)) = False Then RadioCHN(3) = PlaySound_Strict(RadioStatic)
									
									If MTFTimer > 0.0 Then 
										RadioState(3) = RadioState(3) + Max(Rand(-10, 1), 0)
										Select RadioState(3)
											Case 40
												;[Block]
												If Not RadioState3(0) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState(3) = RadioState(3) + 1	
													RadioState3(0) = True	
												EndIf	
												;[End Block]
											Case 400
												;[Block]
												If Not RadioState3(1) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState(3) = RadioState(3) + 1	
													RadioState3(1) = True	
												EndIf	
												;[End Block]
											Case 800
												;[Block]
												If Not RadioState3(2) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState(3) = RadioState(3) + 1	
													RadioState3(2) = True
												EndIf		
												;[End Block]
											Case 1200
												;[Block]
												If Not RadioState3(3) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState(3) = RadioState(3) + 1	
													RadioState3(3) = True
												EndIf
												;[End Block]
											Case 1600
												;[Block]
												If Not RadioState3(4) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState(3) = RadioState(3) + 1
													RadioState3(4) = True
												EndIf
												;[End Block]
											Case 2000
												;[Block]
												If Not RadioState3(5) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState(3) = RadioState(3) + 1
													RadioState3(5) = True
												EndIf
												;[End Block]
											Case 2400
												;[Block]
												If Not RadioState3(6) Then
													RadioCHN(3) = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState(3) = RadioState(3) + 1
													RadioState3(6) = True
												EndIf
												;[End Block]
										End Select
									EndIf
									;[End Block]
								Case 4
									;[Block]
									ResumeChannel(RadioCHN(6))
									If ChannelPlaying(RadioCHN(6)) = False Then RadioCHN(6) = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN(4))
									If ChannelPlaying(RadioCHN(4)) = False Then 
										If RemoteDoorOn = False And RadioState(8) = False Then
											RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter3.ogg"))	
											RadioState(8) = True
										Else
											RadioState(4) = RadioState(4) + Max(Rand(-10, 1), 0)
											
											Select RadioState(4)
												Case 10
													;[Block]
													If (Not Contained106)
														If Not RadioState4(0) Then
															RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\OhGod.ogg"))
															RadioState(4) = RadioState(4) + 1
															RadioState4(0) = True
														EndIf
													EndIf
													;[End Block]
												Case 100
													;[Block]
													If Not RadioState4(1) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter2.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState4(1) = True
													EndIf	
													;[End Block]
												Case 158
													;[Block]
													If MTFTimer = 0.0 And (Not RadioState4(2)) Then 
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin1.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState(2) = True
													EndIf
													;[End Block]
												Case 200
													;[Block]
													If Not RadioState4(3) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter4.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState4(3) = True
													EndIf		
													;[End Block]
												Case 260
													;[Block]
													If Not RadioState4(4) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState4(4) = True
													EndIf		
													;[End Block]
												Case 300
													;[Block]
													If Not RadioState4(5) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Chatter1.ogg"))	
														RadioState(4) = RadioState(4) + 1	
														RadioState4(5) = True
													EndIf		
													;[End Block]
												Case 350
													;[Block]
													If Not RadioState4(6) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin2.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState4(6) = True
													EndIf		
													;[End Block]
												Case 400
													;[Block]
													If Not RadioState4(7) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState(4) = RadioState(4) + 1
														RadioState4(7) = True
													EndIf		
													;[End Block]
												Case 450
													;[Block]
													If Not RadioState4(8) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin3.ogg"))	
														RadioState(4) = RadioState(4) + 1		
														RadioState4(8) = True
													EndIf		
													;[End Block]
												Case 600
													;[Block]
													If Not RadioState4(9) Then
														RadioCHN(4) = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin4.ogg"))	
														RadioState(4) = RadioState(4) + 1	
														RadioState4(9) = True
													EndIf		
													;[End Block]
											End Select
										EndIf
									EndIf
									;[End Block]
								Case 5
									;[Block]
									ResumeChannel(RadioCHN(5))
									If ChannelPlaying(RadioCHN(5)) = False Then RadioCHN(5) = PlaySound_Strict(RadioStatic)
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
							
							AASetFont(Font3)
							AAText(x + 60, y, "CHN")						
							
							If SelectedItem\ItemTemplate\TempName = "veryfineradio" Then
								ResumeChannel(RadioCHN(0))
								If ChannelPlaying(RadioCHN(0)) = False Then RadioCHN(0) = PlaySound_Strict(RadioStatic)
								RadioState(6) = RadioState(6) + FPSfactor
								Temp = Mid(Str(AccessCode), RadioState(8) + 1, 1)
								If RadioState(6) - FPSfactor =< RadioState(7) * 50 And RadioState(6) > RadioState(7) * 50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState(7) = RadioState(7) + 1
									If RadioState(7) >= Temp Then
										RadioState(7) = 0
										RadioState(6) = -100
										RadioState(8) = RadioState(8) + 1
										If RadioState(8) = 4 Then RadioState(8) = 0 : RadioState(6) = -200
									EndIf
								EndIf
								
								StrTemp = ""
								For i = 0 To Rand(5, 30)
									StrTemp = StrTemp + Chr(Rand(1, 100))
								Next
								
								AASetFont(Font4)
								AAText(x + 97, y + 16.0, Rand(0, 9), True, True)
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\State2 <> i - 2 Then
											PlaySound_Strict(RadioSquelch)
											If RadioCHN(Int(SelectedItem\State2)) <> 0 Then PauseChannel(RadioCHN(Int(SelectedItem\State2)))
										EndIf
										SelectedItem\State2 = i - 2
										If RadioCHN(SelectedItem\State2) <> 0 Then ResumeChannel(RadioCHN(SelectedItem\State2))
									EndIf
								Next
								AASetFont(Font4)
								AAText(x + 97, y + 16, Int(SelectedItem\State2 + 1), True, True)
							EndIf
							
							AASetFont(Font3)
							If StrTemp <> "" Then
								StrTemp = Right(Left(StrTemp, (Int(MilliSecs2() / 300) Mod Len(StrTemp))), 10)
								AAText(x + 32, y + 33, StrTemp)
							EndIf
							AASetFont(Font1)
						EndIf
					EndIf
					;[End Block]
				Case "cigarette"
					;[Block]
					If CanUseItem(False, False, True) Then
						Select Rand(6)
							Case 1
								;[Block]
								Msg = Chr(34) + "I don't have anything to light it with. Umm, what about that... Nevermind." + Chr(34)
								;[End Block]
							Case 2
								;[Block]
								Msg = "You are unable to get lit."
								;[End Block]
							Case 3
								;[Block]
								Msg = Chr(34) + "I quit that a long time ago." + Chr(34)
								;[End Block]
							Case 4
								;[Block]
								Msg = Chr(34) + "Even if I wanted one, I have nothing to light it with." + Chr(34)
								;[End Block]
							Case 5
								;[Block]
								Msg = Chr(34) + "Could really go for one now... Wish I had a lighter." + Chr(34)
								;[End Block]
							Case 6
								;[Block]
								Msg = Chr(34) + "Don't plan on starting, even at a time like this." + Chr(34)
								;[End Block]
						End Select
						MsgTimer = 70.0 * 5.0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp420j"
					;[Block]
					If CanUseItem(False, False, True) Then
						If Wearing714 = 1 Or WearingGasMask = 3 Or WearingHazmat = 3 Then
							Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34)
						Else
							Msg = Chr(34) + "MAN DATS SUM GOOD ASS SHIT." + Chr(34)
							Injuries = Max(Injuries - 0.5, 0.0)
							BlurTimer = 500.0
							GiveAchievement(Achv420J)
							PlaySound_Strict(LoadTempSound("SFX\Music\420J.ogg"))
						EndIf
						MsgTimer = 70.0 * 5.0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "joint"
					;[Block]
					If CanUseItem(False, False, True) Then
						If Wearing714 = 1 Or WearingGasMask = 3 Or WearingHazmat = 3 Then
							Msg = Chr(34) + "DUDE WTF THIS SHIT DOESN'T EVEN WORK." + Chr(34)
						Else
							DeathMsg = "Subject D-9341 found in a comatose state in [DATA REDACTED]. The subject was holding what appears to be a cigarette while smiling widely. "
							DeathMsg = DeathMsg + "Chemical analysis of the cigarette has been inconclusive, although it seems to contain a high concentration of an unidentified chemical "
							DeathMsg = DeathMsg + "whose molecular structure is remarkably similar to that of tetrahydrocannabinol."
							Msg = Chr(34) + "UH WHERE... WHAT WAS I DOING AGAIN... MAN I NEED TO TAKE A NAP..." + Chr(34)
							KillTimer = -1.0						
						EndIf
						MsgTimer = 70.0 * 6.0
						RemoveItem(SelectedItem)
					EndIf
					;[End Block]
				Case "scp714"
					;[Block]
					If Wearing714 = 1 Then
						Msg = "You removed the ring."
						Wearing714 = 0
					Else
						GiveAchievement(Achv714)
						Msg = "You put on the ring."
						Wearing714 = 1
					EndIf
					MsgTimer = 70.0 * 5.0
					SelectedItem = Null	
					;[End Block]
				Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
					;[Block]
					If WearingVest = 0 Then
						CurrSpeed = CurveValue(0.0, CurrSpeed, 5.0)
						
						DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
						
						Width = 300
						Height = 20
						x = GraphicWidth / 2 - Width / 2
						y = GraphicHeight / 2 + 80.0
						Rect(x, y, Width + 4, Height, False)
						For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
						SelectedItem\State = Min(SelectedItem\State + (FPSfactor / 4.0), 100.0)
						
						If SelectedItem\State = 100.0 Then
							If WearingHazmat > 0 Then
								Msg = "You removed the hazmat suit."
								WearingHazmat = False
								DropItem(SelectedItem)
							Else
								If SelectedItem\ItemTemplate\TempName = "hazmatsuit" Then
									WearingHazmat = 1
								ElseIf SelectedItem\ItemTemplate\TempName = "hazmatsuit2" Then
									WearingHazmat = 2
								Else
									WearingHazmat = 3
								EndIf
								If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
								Msg = "You put on the hazmat suit."
								If WearingNightVision Then CameraFogFar = StoredCameraFogFar
								WearingGasMask = 0
								WearingNightVision = 0
							EndIf
							SelectedItem\State = 0.0
							MsgTimer = 70.0 * 5.0
							SelectedItem = Null
						EndIf
					EndIf
					;[End Block]
				Case "vest", "finevest"
					;[Block]
					CurrSpeed = CurveValue(0.0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					
					Width = 300
					Height = 20
					x = GraphicWidth / 2 - Width / 2
					y = GraphicHeight / 2 + 80.0
					Rect(x, y, Width + 4, Height, False)
					For i = 1 To Int((Width - 2.0) * (SelectedItem\State / 100.0) / 10.0)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					SelectedItem\State = Min(SelectedItem\State + (FPSfactor / (2.0 + (0.5 * (SelectedItem\ItemTemplate\TempName = "finevest")))), 100)
					
					If SelectedItem\State = 100.0 Then
						If WearingVest > 0 Then
							Msg = "You removed the vest."
							WearingVest = False
							DropItem(SelectedItem)
						Else
							If SelectedItem\ItemTemplate\TempName = "vest" Then
								Msg = "You put on the vest and feel slightly encumbered."
								WearingVest = 1
							Else
								Msg = "You put on the vest and feel heavily encumbered."
								WearingVest = 2
							EndIf
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
						EndIf
						SelectedItem\State = 0.0
						MsgTimer = 70.0 * 5.0
						SelectedItem = Null
					EndIf
					;[End Block]
				Case "gasmask", "supergasmask", "gasmask3"
					;[Block]
					If Wearing1499 = 0 And WearingHazmat = 0 Then
						If WearingGasMask Then
							Msg = "You removed the gas mask."
						Else
							If SelectedItem\ItemTemplate\TempName = "supergasmask"
								Msg = "You put on the gas mask and you can breathe easier."
							Else
								Msg = "You put on the gas mask."
							EndIf
							If WearingNightVision Then CameraFogFar = StoredCameraFogFar
							WearingNightVision = 0
							WearingGasMask = 0
						EndIf
						If SelectedItem\ItemTemplate\TempName = "gasmask3" Then
							If WearingGasMask = 0 Then WearingGasMask = 3 Else WearingGasMask = 0
						ElseIf SelectedItem\ItemTemplate\TempName = "supergasmask"
							If WearingGasMask = 0 Then WearingGasMask = 2 Else WearingGasMask = 0
						Else
							WearingGasMask = (Not WearingGasMask)
						EndIf
					ElseIf Wearing1499 > 0 Then
						Msg = "You need to take off SCP-1499 in order to put on the gas mask."
					Else
						Msg = "You need to take off the hazmat suit in order to put on the gas mask."
					EndIf
					SelectedItem = Null
					MsgTimer = 70.0 * 5.0
					;[End Block]
				Case "navigator", "nav"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					If SelectedItem\State =< 100.0 Then SelectedItem\State = Max(0.0, SelectedItem\State - FPSfactor * 0.005)
					
					x = GraphicWidth - ImageWidth(SelectedItem\ItemTemplate\Img) * 0.5 + 20.0
					y = GraphicHeight - ImageHeight(SelectedItem\ItemTemplate\Img) * 0.4 - 85.0
					Width = 287.0
					Height = 256.0
					
					Local PlayerX%, PlayerZ%
					
					DrawImage(SelectedItem\ItemTemplate\Img, x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85)
					
					AASetFont(Font3)
					
					Local NavWorks% = True
					
					If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "dimension1499" Then
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
						If (MilliSecs2() Mod 1000) > 300 Then
							Color(200, 0, 0)
							AAText(x, y + Height / 2 - 80, "ERROR 06", True)
							AAText(x, y + Height / 2 - 60, "LOCATION UNKNOWN", True)						
						EndIf
					Else
						If SelectedItem\State > 0.0 And (Rnd(CoffinDistance + 15.0) > 1.0 Or PlayerRoom\RoomTemplate\Name <> "coffin") Then
							PlayerX = Floor((EntityX(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							PlayerZ = Floor((EntityZ(PlayerRoom\OBJ) + 8.0) / 8.0 + 0.5)
							
							SetBuffer(ImageBuffer(NavBG))
							
							Local xx% = x - ImageWidth(SelectedItem\ItemTemplate\Img) / 2
							Local yy% = y - ImageHeight(SelectedItem\ItemTemplate\Img) / 2 + 85
							
							DrawImage(SelectedItem\ItemTemplate\Img, xx, yy)
							
							x = x - 12.0 + (((EntityX(Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							y = y + 12.0 - (((EntityZ(Collider) - 4.0) + 8.0) Mod 8.0) * 3.0
							For x2 = Max(0.0, PlayerX - 6.0) To Min(MapWidth, PlayerX + 6.0)
								For z2 = Max(0.0, PlayerZ - 6.0) To Min(MapHeight, PlayerZ + 6.0)
									If CoffinDistance > 16.0 Or Rnd(16.0) < CoffinDistance Then 
										If MapTemp(x2, z2) > 0 And (MapFound(x2, z2) > 0 Or SelectedItem\ItemTemplate\Name = "S-NAV 310 Navigator" Or SelectedItem\ItemTemplate\Name = "S-NAV Navigator Ultimate") Then
											Local DrawX% = x + (PlayerX - 1 - x2) * 24 , DrawY% = y - (PlayerZ - 1 - z2) * 24
											
											If x2 + 1.0 =< MapWidth Then
												If MapTemp(x2 + 1, z2) = False
													DrawImage(NavImages(3), DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(NavImages(3), DrawX - 12, DrawY - 12)
											EndIf
											If x2 - 1.0 >= 0.0 Then
												If MapTemp(x2 - 1, z2) = False
													DrawImage(NavImages(1), DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(NavImages(1), DrawX - 12, DrawY - 12)
											EndIf
											If z2 - 1.0 >= 0.0 Then
												If MapTemp(x2, z2 - 1) = False
													DrawImage(NavImages(0), DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(NavImages(0), DrawX - 12, DrawY - 12)
											EndIf
											If z2 + 1.0 =< MapHeight Then
												If MapTemp(x2, z2 + 1) = False
													DrawImage(NavImages(2), DrawX - 12, DrawY - 12)
												EndIf
											Else
												DrawImage(NavImages(2), DrawX - 12, DrawY - 12)
											EndIf
										EndIf
									EndIf
								Next
							Next
							
							SetBuffer(BackBuffer())
							DrawImageRect(NavBG, xx + 80, yy + 70, xx + 80, yy + 70, 270, 230)
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
							If (MilliSecs2() Mod 1000.0) > 300.0 Then
								If SelectedItem\ItemTemplate\Name <> "S-NAV 310 Navigator" And SelectedItem\ItemTemplate\Name <> "S-NAV Navigator Ultimate" Then
									AAText(x - Width / 2 + 10, y - Height / 2 + 10, "MAP DATABASE OFFLINE")
								EndIf
								
								YawValue = EntityYaw(Collider) - 90.0
								x1 = x + Cos(YawValue) * 6.0 : y1 = y - Sin(YawValue) * 6.0
								x2 = x + Cos(YawValue - 140.0) * 5.0 : y2 = y - Sin(YawValue - 140.0) * 5.0				
								x3 = x + Cos(YawValue + 140.0) * 5.0 : y3 = y - Sin(YawValue + 140.0) * 5.0
								
								Line(x1, y1, x2, y2)
								Line(x1, y1, x3, y3)
								Line(x2, y2, x3, y3)
							EndIf
							
							Local SCPs_Found% = 0
							
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator Ultimate" And (MilliSecs2() Mod 600.0) < 400.0 Then
								If Curr173 <> Null Then
									Local Dist# = EntityDistance(Camera, Curr173\OBJ)
									
									Dist = Ceil(Dist / 8.0) * 8.0
									If Dist < 8.0 * 4.0 Then
										Color(100, 0, 0)
										Oval(x - Dist * 3, y - 7 - Dist * 3, Dist * 3 * 2, Dist * 3 * 2, False)
										AAText(x - Width / 2 + 10, y - Height / 2 + 30, "SCP-173")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr106 <> Null Then
									Dist = EntityDistance(Camera, Curr106\OBJ)
									If Dist < 8.0 * 4.0 Then
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7 - Dist * 1.5, Dist * 3, Dist * 3, False)
										AAText(x - Width / 2 + 10, y - Height / 2 + 30 + (20 * SCPs_Found), "SCP-106")
										SCPs_Found = SCPs_Found + 1
									EndIf
								EndIf
								If Curr096 <> Null Then 
									Dist = EntityDistance(Camera, Curr096\OBJ)
									If Dist < 8.0 * 4.0 Then
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7 - Dist * 1.5, Dist * 3, Dist * 3, False)
										AAText(x - Width / 2 + 10, y - Height / 2 + 30 + (20 * SCPs_Found), "SCP-096")
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
												AAText(x - Width / 2 + 10, y - Height / 2 + 30 + (20 * SCPs_Found), "SCP-049")
												SCPs_Found = SCPs_Found + 1
											EndIf
										EndIf
										Exit
									EndIf
								Next
								If PlayerRoom\RoomTemplate\Name = "coffin" Then
									If CoffinDistance < 8.0 Then
										Dist = Rnd(4.0, 8.0)
										Color(100, 0, 0)
										Oval(x - Dist * 1.5, y - 7.0 - Dist * 1.5, Dist * 3.0, Dist * 3.0, False)
										AAText(x - Width / 2 + 10, y - Height / 2 + 30 + (20 * SCPs_Found), "SCP-895")
									EndIf
								EndIf
							End If
							
							Color(30, 30, 30)
							If SelectedItem\ItemTemplate\Name = "S-NAV Navigator" Then Color(100, 0, 0)
							If SelectedItem\State =< 100.0 Then
								xTemp = x - Width / 2.0 + 196.0
								yTemp = y - Height / 2.0 + 10.0
								Rect(xTemp, yTemp, 80, 20, False)
								
								For i = 1 To Ceil(SelectedItem\State / 10.0)
									DrawImage(NavImages(4), xTemp + i * 8 - 6, yTemp + 4)
								Next
								AASetFont(Font3)
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case "scp1499", "super1499"
					;[Block]
					If WearingHazmat > 0
						Msg = "You are not able to wear SCP-1499 and a hazmat suit at the same time."
						MsgTimer = 70 * 5
						SelectedItem = Null
						Return
					EndIf
					
					CurrSpeed = CurveValue(0.0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					
					Width = 300.0
					Height = 20.0
					x = GraphicWidth / 2 - Width / 2
					y = GraphicHeight / 2.0 + 80.0
					Rect(x, y, Width + 4, Height, False)
					For i = 1 To Int((Width - 2) * (SelectedItem\State / 100.0) / 10.0)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					SelectedItem\State = Min(SelectedItem\State + FPSfactor, 100.0)
					
					If SelectedItem\State = 100.0 Then
						If Wearing1499 > 0 Then
							Wearing1499 = False
							If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
						Else
							If SelectedItem\ItemTemplate\TempName = "scp1499" Then
								Wearing1499 = 1
							Else
								Wearing1499 = 2
							EndIf
							If SelectedItem\itemtemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
							GiveAchievement(Achv1499)
							If WearingNightVision Then CameraFogFar = StoredCameraFogFar
							WearingGasMask = 0
							WearingNightVision = 0
							For r.Rooms = Each Rooms
								If r\RoomTemplate\Name = "dimension1499" Then
									BlinkTimer = -1.0
									NTF_1499PrevRoom = PlayerRoom
									NTF_1499PrevX = EntityX(Collider)
									NTF_1499PrevY = EntityY(Collider)
									NTF_1499PrevZ = EntityZ(Collider)
									
									If NTF_1499X = 0.0 And NTF_1499Y = 0.0 And NTF_1499Z = 0.0 Then
										PositionEntity(Collider, r\x + 6086.0 * RoomScale, r\y + 304.0 * RoomScale, r\z + 2292.5 * RoomScale)
										RotateEntity(Collider, 0.0, 90.0, 0.0, True)
									Else
										PositionEntity(Collider, NTF_1499X, NTF_1499Y + 0.05, NTF_1499Z)
									EndIf
									ResetEntity(Collider)
									UpdateDoors()
									UpdateRooms()
									For it.Items = Each Items
										it\DistTimer = 0.0
									Next
									PlayerRoom = r
									PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Enter.ogg"))
									NTF_1499X = 0.0
									NTF_1499Y = 0.0
									NTF_1499Z = 0.0
									If Curr096 <> Null Then
										If Curr096\SoundCHN <> 0 Then
											SetStreamVolume_Strict(Curr096\SoundCHN, 0.0)
										EndIf
									EndIf
									For e.Events = Each Events
										If e\EventName = "dimension1499" Then
											If EntityDistance(e\room\OBJ, Collider) > 8300.0 * RoomScale Then
												If e\EventState2 < 5 Then
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
					;[End Block]
				Case "badge"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						Select SelectedItem\ItemTemplate\Name
							Case "Old Badge"
								;[Block]
								Msg = Chr(34) + "Huh? This guy looks just like me!" + Chr(34)
								MsgTimer = 70.0 * 10.0
								;[End Block]
						End Select
						
						SelectedItem\State = 1.0
					EndIf
					;[End Block]
				Case "key"
					;[Block]
					If SelectedItem\State = 0.0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\1162\NostalgiaCancer" + Rand(6, 10) + ".ogg"))
						
						Msg = Chr(34) + "Isn't this the key to that old shack? The one where I... No, it can't be." + Chr(34)
						MsgTimer = 70.0 * 10.0						
					EndIf
					
					SelectedItem\State = 1.0
					SelectedItem = Null
					;[End Block]
				Case "oldpaper"
					;[Block]
					If SelectedItem\ItemTemplate\Img = 0 Then
						SelectedItem\ItemTemplate\Img = LoadImage_Strict(SelectedItem\ItemTemplate\ImgPath)	
						SelectedItem\ItemTemplate\Img = ResizeImage2(SelectedItem\ItemTemplate\Img, ImageWidth(SelectedItem\ItemTemplate\Img) * MenuScale, ImageHeight(SelectedItem\ItemTemplate\Img) * MenuScale)
						
						MaskImage(SelectedItem\ItemTemplate\Img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\ItemTemplate\Img, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\Img) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\Img) / 2)
					
					If SelectedItem\State = 0.0
						Select SelectedItem\ItemTemplate\Name
							Case "Disciplinary Hearing DH-S-4137-17092"
								;[Block]
								BlurTimer = 1000.0
								
								Msg = Chr(34) + "Why does this seem so familiar?" + Chr(34)
								MsgTimer = 70.0 * 10.0
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
					DrawImage(SelectedItem\ItemTemplate\InvImg, GraphicWidth / 2 - ImageWidth(SelectedItem\ItemTemplate\InvImg) / 2, GraphicHeight / 2 - ImageHeight(SelectedItem\ItemTemplate\InvImg) / 2)
					;[End Block]
				Case "scp427"
					;[Block]
					If I_427\Using = 1 Then
						Msg = "You closed the locket."
						I_427\Using = False
					Else
						GiveAchievement(Achv427)
						Msg = "You opened the locket."
						I_427\Using = True
					EndIf
					MsgTimer = 70.0 * 5.0
					SelectedItem = Null
					;[End Block]
				Case "pill"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = "You swallowed the pill."
						MsgTimer = 70.0 * 7.0
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	
					;[End Block]
				Case "scp500death"
					;[Block]
					If CanUseItem(False, False, True)
						Msg = "You swallowed the pill."
						MsgTimer = 70.0 * 7.0
						
						If I_427\Timer < 70 * 360 Then
							I_427\Timer = 70 * 360
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
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
			
			If SelectedItem <> Null Then
				If SelectedItem\ItemTemplate\Img <> 0
					Local IN$ = SelectedItem\ItemTemplate\TempName
					
					If IN = "paper" Or IN = "badge" Or IN = "oldpaper" Or IN = "ticket" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\ItemTemplate\Tempname
								
								If IN2 = "paper" Or IN2 = "badge" Or IN2 = "oldpaper" Or IN2 = "ticket" Then
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
				EntityAlpha(Dark, 0.0)
				
				IN = SelectedItem\ItemTemplate\TempName
				If IN = "scp1025" Then
					If SelectedItem\ItemTemplate\Img <> 0 Then FreeImage(SelectedItem\ItemTemplate\Img)
					SelectedItem\ItemTemplate\Img = 0
				ElseIf IN = "firstaid" Or IN = "finefirstaid" Or IN = "firstaid2" Then
					SelectedItem\State = 0.0
				ElseIf IN = "vest" Or IN = "finevest"
					SelectedItem\State = 0.0
					If (Not WearingVest)
						DropItem(SelectedItem, False)
					EndIf
				ElseIf IN = "hazmatsuit" Or IN = "hazmatsuit2" Or IN = "hazmatsuit3"
					SelectedItem\State = 0.0
					If WearingHazmat = 0 Then
						DropItem(SelectedItem, False)
					EndIf
				ElseIf IN = "scp1499" Or IN = "super1499"
					SelectedItem\State = 0.0
				EndIf
				
				If SelectedItem\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\ItemTemplate\Sound))
				SelectedItem = Null
			EndIf
		End If		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 6
			If RadioCHN(i) <> 0 Then 
				If ChannelPlaying(RadioCHN(i)) Then PauseChannel(RadioCHN(i))
			EndIf
		Next
	EndIf
	
	For it.Items = Each Items
		If it <> SelectedItem
			Select it\ItemTemplate\TempName
				Case "firstaid", "finefirstaid", "firstaid2", "vest", "finevest", "hazmatsuit", "hazmatsuit2", "hazmatsuit3", "scp1499", "super1499"
					;[Block]
					it\State = 0.0
					;[End Block]
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse Viewport_Center_X, Viewport_Center_Y
	
	CatchErrors("DrawGUI")
End Function

Function DrawMenu()
	CatchErrors("Uncaught (DrawMenu)")
	
	Local x%, y%, Width%, Height%
	Local i%
	
	If api_GetFocus() = 0 Then ; ~ Game is out of focus then pause the game
		If (Not Using294) Then
			MenuOpen = True
			PauseSounds()
		EndIf
        Delay(1000.0) ; ~ Reduce the CPU take while game is not in focus
    EndIf
	If MenuOpen Then
		If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea"
			If StopHidingTimer = 0.0 Then
				If EntityDistance(Curr173\Collider, Collider) < 4.0 Or EntityDistance(Curr106\Collider, Collider) < 4.0 Then 
					StopHidingTimer = 1.0
				EndIf	
			ElseIf StopHidingTimer < 40.0
				If KillTimer >= 0.0 Then 
					StopHidingTimer = StopHidingTimer + FPSfactor
					
					If StopHidingTimer >= 40.0 Then
						PlaySound_Strict(HorrorSFX(15))
						Msg = "STOP HIDING"
						MsgTimer = 6 * 70
						MenuOpen = False
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		
		Width = ImageWidth(PauseMenuIMG)
		Height = ImageHeight(PauseMenuIMG)
		x = GraphicWidth / 2.0 - Width / 2.0
		y = GraphicHeight / 2.0 - Height / 2.0
		
		DrawImage(PauseMenuIMG, x, y)
		
		Color(255, 255, 255)
		
		x = x + 132.0 * MenuScale
		y = y + 122.0 * MenuScale	
		
		If (Not MouseDown1)
			OnSliderID = 0
		EndIf
		
		If AchievementsMenu > 0 Then
			AASetFont(Font2)
			AAText(x, y - (122 - 45) * MenuScale, "ACHIEVEMENTS", False, True)
			AASetFont(Font1)
		ElseIf OptionsMenu > 0 Then
			AASetFont(Font2)
			AAText(x, y - (122 - 45) * MenuScale, "OPTIONS", False, True)
			AASetFont(Font1)
		ElseIf QuitMsg > 0 Then
			AASetFont(Font2)
			AAText(x, y - (122 - 45) * MenuScale, "QUIT?", False, True)
			AASetFont(Font1)
		ElseIf KillTimer >= 0.0 Then
			AASetFont(Font2)
			AAText(x, y - (122 - 45) * MenuScale, "PAUSED", False, True)
			AASetFont(Font1)
		Else
			AASetFont(Font2)
			AAText(x, y - (122 - 45) * MenuScale, "YOU DIED", False, True)
			AASetFont(Font1)
		End If		
		
		Local AchvXIMG% = (x + (22.0 * MenuScale))
		Local Scale# = GraphicHeight / 768.0
		Local SeparationConst% = 76.0 * Scale
		Local ImgSize% = 64.0
		
		If AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg =< 0
			AASetFont(Font1)
			AAText(x, y, "Difficulty: " + SelectedDifficulty\name)
			AAText(x, y + 20 * MenuScale, "Save: " + CurrSave)
			AAText(x, y + 40 * MenuScale, "Map seed: " + RandomSeed)
		ElseIf AchievementsMenu =< 0 And OptionsMenu > 0 And QuitMsg =< 0 And KillTimer >= 0
			If DrawButton(x + 101 * MenuScale, y + 390 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias(Opt_AntiAlias)
				TextureLodBias(TextureFloat)
			EndIf
			
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
			
			If DrawButton(x - 5 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "GRAPHICS", False) Then OptionsMenu = 1
			If DrawButton(x + 105 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "AUDIO", False) Then OptionsMenu = 2
			If DrawButton(x + 215 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "CONTROLS", False) Then OptionsMenu = 3
			If DrawButton(x + 325 * MenuScale, y, 100 * MenuScale, 30 * MenuScale, "ADVANCED", False) Then OptionsMenu = 4
			
			Local tX# = (GraphicWidth / 2) + (Width / 2)
			Local tY# = y
			Local tW# = 400 * MenuScale
			Local tH# = 150 * MenuScale
			
			Color(255, 255, 255)
			Select OptionsMenu
				Case 1 ; ~ Graphics
					;[Block]
					AASetFont(Font1)
					
					y = y + 50 * MenuScale
					
					Color(100, 100, 100)
					AAText(x, y, "Enable bump mapping:")	
					BumpEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, BumpEnabled, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "bump")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "VSync:")
					VSync = DrawTick(x + 270 * MenuScale, y + MenuScale, VSync)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vsync")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Anti-aliasing:")
					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "antialias")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Enable room lights:")
					EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "roomlights")
					EndIf
					
					y = y + 30 * MenuScale
					
					ScreenGamma = (SlideBar(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale, ScreenGamma * 50.0) / 50.0)
					Color(255, 255, 255)
					AAText(x, y, "Screen gamma:")
					If MouseOn(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "gamma", ScreenGamma)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Particle amount:")
					ParticleAmount = Slider3(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale, ParticleAmount, 2, "MINIMAL", "REDUCED", "FULL")
					If (MouseOn(x + 270 * MenuScale, y - 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0) Or OnSliderID = 2
						DrawOptionsTooltip(tX, tY, tW, tH, "particleamount", ParticleAmount)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Texture LOD Bias:")
					TextureDetails = Slider5(x + 270 * MenuScale, y + 6 * MenuScale, 100 * MenuScale, TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
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
					If (MouseOn(x + 270 * MenuScale, y - 6 * MenuScale, 100 * MenuScale + 14, 20) And OnSliderID = 0) Or OnSliderID = 3
						DrawOptionsTooltip(tX, tY, tW, tH + 100 * MenuScale, "texquality")
					EndIf
					
					y = y + 50 * MenuScale
					Color(100, 100, 100)
					AAText(x, y, "Save textures in the VRAM:")	
					SaveTexturesInVRAM = DrawTick(x + 270 * MenuScale, y + MenuScale, SaveTexturesInVRAM, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vram")
					EndIf
					;[End Block]
				Case 2 ; ~ Audio
					;[Block]
					AASetFont(Font1)
					
					y = y + 50 * MenuScale
					
					MusicVolume = (SlideBar(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale, MusicVolume * 100.0) / 100.0)
					Color(255, 255, 255)
					AAText(x, y, "Music volume:")
					If MouseOn(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "musicvol", MusicVolume)
					EndIf
					
					y = y + 30 * MenuScale
					
					PrevSFXVolume = (SlideBar(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale, SFXVolume * 100.0) / 100.0)
					If (Not DeafPlayer) Then SFXVolume = PrevSFXVolume
					Color(255, 255, 255)
					AAText(x, y, "Sound volume:")
					If MouseOn(x + 250 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "soundvol", PrevSFXVolume)
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(100, 100, 100)
					AAText(x, y, "Sound auto-release:")
					EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableSFXRelease, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, "sfxautorelease")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(100, 100, 100)
					AAText(x, y, "Enable user tracks:")
					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "usertrack")
					EndIf
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						Color(255, 255, 255)
						AAText(x, y, "User track mode:")
						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							AAText(x, y + 20 * MenuScale, "Repeat")
						Else
							AAText(x, y + 20 * MenuScale, "Random")
						EndIf
						If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "usertrackmode")
						EndIf
					EndIf
					;[End Block]
				Case 3 ; ~ Controls
					;[Block]
					AASetFont(Font1)
					y = y + 50 * MenuScale
					
					MouseSensitivity = (SlideBar(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale, (MouseSensitivity + 0.5) * 100.0) / 100.0) - 0.5
					Color(255, 255, 255)
					AAText(x, y, "Mouse sensitivity:")
					If MouseOn(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesensitivity", MouseSensitivity)
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Invert mouse Y-axis:")
					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "mouseinvert")
					EndIf
					
					y = y + 40 * MenuScale
					
					MouseSmoothing = (SlideBar(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale, (MouseSmoothing) * 50.0) / 50.0)
					Color(255, 255, 255)
					AAText(x, y, "Mouse smoothing:")
					If MouseOn(x + 270 * MenuScale, y - 4 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesmoothing", MouseSmoothing)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30 * MenuScale
					AAText(x, y, "Control configuration:")
					y = y + 10 * MenuScale
					
					AAText(x, y + 20 * MenuScale, "Move Forward")
					InputBox(x + 200 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_UP, 210)), 5)		
					AAText(x, y + 40 * MenuScale, "Strafe Left")
					InputBox(x + 200 * MenuScale, y + 40 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_LEFT, 210)), 3)	
					AAText(x, y + 60 * MenuScale, "Move Backward")
					InputBox(x + 200 * MenuScale, y + 60 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_DOWN, 210)), 6)				
					AAText(x, y + 80 * MenuScale, "Strafe Right")
					InputBox(x + 200 * MenuScale, y + 80 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_RIGHT, 210)), 4)
					
					AAText(x, y + 100 * MenuScale, "Manual Blink")
					InputBox(x + 200 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_BLINK, 210)), 7)				
					AAText(x, y + 120 * MenuScale, "Sprint")
					InputBox(x + 200 * MenuScale, y + 120 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_SPRINT, 210)), 8)
					AAText(x, y + 140 * MenuScale, "Open/Close Inventory")
					InputBox(x + 200 * MenuScale, y + 140 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_INV, 210)), 9)
					AAText(x, y + 160 * MenuScale, "Crouch")
					InputBox(x + 200 * MenuScale, y + 160 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_CROUCH, 210)), 10)
					AAText(x, y + 180 * MenuScale, "Quick Save")
					InputBox(x + 200 * MenuScale, y + 180 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_SAVE, 210)), 11)	
					AAText(x, y + 200 * MenuScale, "Open/Close Console")
					InputBox(x + 200 * MenuScale, y + 200 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_CONSOLE, 210)), 12)
					
					If MouseOn(x, y, 300 * MenuScale, 220 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "controls")
					EndIf
					
					For i = 0 To 227
						If KeyHit(i) Then key = i : Exit
					Next
					If key <> 0 Then
						Select SelectedInputBox
							Case 3
								;[Block]
								KEY_LEFT = key
								;[End Block]
							Case 4
								;[Block]
								KEY_RIGHT = Key
								;[End Block]
							Case 5
								;[Block]
								KEY_UP = Key
								;[End Block]
							Case 6
								;[Block]
								KEY_DOWN = Key
								;[End Block]
							Case 7
								;[Block]
								KEY_BLINK = Key
								;[End Block]
							Case 8
								;[Block]
								KEY_SPRINT = Key
								;[End Block]
							Case 9
								;[Block]
								KEY_INV = Key
								;[End Block]
							Case 10
								;[Block]
								KEY_CROUCH = Key
								;[End Block]
							Case 11
								;[Block]
								KEY_SAVE = Key
								;[End Block]
							Case 12
								;[Block]
								KEY_CONSOLE = Key
								;[End Block]
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				Case 4 ; ~ Advanced
					;[Block]
					AASetFont(Font1)
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)			
					AAText(x, y, "Show HUD:")	
					HUDenabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "hud")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Enable console:")
					CanOpenConsole = DrawTick(x + 270 * MenuScale, y + MenuScale, CanOpenConsole)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "consoleenable")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Open console on error:")
					ConsoleOpening = DrawTick(x + 270 * MenuScale, y + MenuScale, ConsoleOpening)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "consoleerror")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Achievement popups:")
					AchvMSGenabled% = DrawTick(x + 270 * MenuScale, y, AchvMSGenabled%)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "achpopup")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Show FPS:")
					ShowFPS = DrawTick(x + 270 * MenuScale, y, ShowFPS)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "showfps")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Framelimit:")
					
					Color(255, 255, 255)
					If DrawTick(x + 270 * MenuScale, y, CurrFrameLimit > 0.0) Then
						CurrFrameLimit = (SlideBar(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale, CurrFrameLimit# * 99.0) / 99.0)
						CurrFrameLimit = Max(CurrFrameLimit, 0.01)
						FrameLimit = 19 + (CurrFrameLimit * 100.0)
						Color(255, 255, 0)
						AAText(x + 5 * MenuScale, y + 25 * MenuScale, FrameLimit + " FPS")
					Else
						CurrFrameLimit = 0.0
						FrameLimit = 0
					EndIf
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					If MouseOn(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					
					y = y + 80 * MenuScale
					
					Color(255, 255, 255)
					AAText(x, y, "Antialiased text:")
					AATextEnable% = DrawTick(x + 270 * MenuScale, y + MenuScale, AATextEnable%)
					If AATextEnable_Prev% <> AATextEnable
						For font.AAFont = Each AAFont
							FreeFont(font\LowResFont%)
							If (Not AATextEnable)
								FreeTexture(font\Texture)
								FreeImage(font\Backup)
							EndIf
							Delete(font)
						Next
						If (Not AATextEnable) Then
							FreeEntity(AATextCam)
						EndIf
						InitAAFont()
						Font1 = AALoadFont("GFX\font\cour\Courier New.ttf", Int(18 * (GraphicHeight / 1024.0)), 0, 0, 0)
						Font2 = AALoadFont("GFX\font\courbd\Courier New.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)
						Font3 = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0, 0, 0)
						Font4 = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(60 * (GraphicHeight / 1024.0)), 0, 0, 0)
						Font5 = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)
						ConsoleFont = AALoadFont("Blitz", Int(22 * (GraphicHeight / 1024.0)), 0, 0, 0, 1)
						AATextEnable_Prev% = AATextEnable
					EndIf
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "antialiastext")
					EndIf
					;[End Block]
			End Select
		ElseIf AchievementsMenu =< 0 And OptionsMenu =< 0 And QuitMsg > 0 And KillTimer >= 0
			Local QuitButton% = 60 
			
			If SelectedDifficulty\SaveType = SAVEONQUIT Or SelectedDifficulty\SaveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name
				Local AbleToSave% = True
				
				If RN = "173" Or RN = "exit1" Or RN = "gatea" Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave
					QuitButton = 140
					If DrawButton(x, y + 60 * MenuScale, 390 * MenuScale, 60 * MenuScale, "Save & Quit") Then
						DropSpeed = 0
						SaveGame(SavePath + CurrSave + "\")
						NullGame()
						MenuOpen = False
						MainMenuOpen = True
						MainMenuTab = 0
						CurrSave = ""
						FlushKeys()
					EndIf
				EndIf
			EndIf
			
			If DrawButton(x, y + QuitButton*MenuScale, 390 * MenuScale, 60 * MenuScale, "Quit") Then
				NullGame()
				MenuOpen = False
				MainMenuOpen = True
				MainMenuTab = 0
				CurrSave = ""
				FlushKeys()
			EndIf
			
			If DrawButton(x + 101 * MenuScale, y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
			EndIf
		Else
			If DrawButton(x + 101 * MenuScale, y + 344 * MenuScale, 230 * MenuScale, 60 * MenuScale, "Back") Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMsg = 0
				MouseHit1 = False
			EndIf
			
			If AchievementsMenu > 0 Then
				If AchievementsMenu =< Floor(Float(MAXACHIEVEMENTS - 1) / 12.0) Then 
					If DrawButton(x + 341 * MenuScale, y + 344 * MenuScale, 50 * MenuScale, 60 * MenuScale, ">") Then
						AchievementsMenu = AchievementsMenu + 1
					EndIf
				EndIf
				If AchievementsMenu > 1 Then
					If DrawButton(x + 41 * MenuScale, y + 344 * MenuScale, 50 * MenuScale, 60 * MenuScale, "<") Then
						AchievementsMenu = AchievementsMenu - 1
					EndIf
				EndIf
				
				For i = 0 To 11
					If i + ((AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG, y + ((i / 4) * 120 * MenuScale), i + ((AchievementsMenu - 1) * 12))
					Else
						Exit
					EndIf
				Next
				
				For i = 0 To 11
					If i + ((AchievementsMenu - 1) * 12) < MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG + ((i Mod 4) * SeparationConst), y + ((i /4 ) * 120 * MenuScale), 64 * Scale, 64 * Scale) Then
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
			If KillTimer >= 0 Then	
				y = y + 72 * MenuScale
				
				If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Resume", True, True) Then
					MenuOpen = False
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				EndIf
				
				y = y + 75 * MenuScale
				If (Not SelectedDifficulty\PermaDeath) Then
					If GameSaved Then
						If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Load Game") Then
							DrawLoading(0)
							
							MenuOpen = False
							LoadGameQuick(SavePath + CurrSave + "\")
							
							MoveMouse Viewport_Center_X, Viewport_Center_Y
							AASetFont(Font1)
							HidePointer ()
							
							FlushKeys()
							FlushMouse()
							Playable = True
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(Collider) - EntityX(r\OBJ))
								z = Abs(EntityZ(Collider) - EntityZ(r\OBJ))
								
								If x < 12.0 And z < 12.0 Then
									MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = Max(MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)), 1)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
										MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = 1
									EndIf
								End If
							Next
							
							DrawLoading(100)
							
							DropSpeed = 0
							
							UpdateWorld 0.0
							
							PrevTime = MilliSecs()
							FPSfactor = 0
							
							ResetInput()
						EndIf
					Else
						DrawFrame(x, y, 390 * MenuScale, 60 * MenuScale)
						Color(100, 100, 100)
						AASetFont(Font2)
						AAText(x + (390 * MenuScale) / 2, y + (60 * MenuScale) / 2, "Load Game", True, True)
					EndIf
					y = y + 75 * MenuScale
				EndIf
				
				If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Achievements") Then AchievementsMenu = 1
				y = y + 75 * MenuScale
				If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Options") Then OptionsMenu = 1
				y = y + 75 * MenuScale
			Else
				y = y + 104 * MenuScale
				If GameSaved And (Not SelectedDifficulty\PermaDeath) Then
					If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Load Game") Then
						DrawLoading(0)
						
						MenuOpen = False
						LoadGameQuick(SavePath + CurrSave + "\")
						
						MoveMouse(Viewport_Center_X, Viewport_Center_Y)
						AASetFont(Font1)
						HidePointer ()
						
						FlushKeys()
						FlushMouse()
						Playable = True
						
						UpdateRooms()
						
						For r.Rooms = Each Rooms
							x = Abs(EntityX(Collider) - EntityX(r\OBJ))
							z = Abs(EntityZ(Collider) - EntityZ(r\OBJ))
							
							If x < 12.0 And z < 12.0 Then
								MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = Max(MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)), 1)
								If x < 4.0 And z < 4.0 Then
									If Abs(EntityY(Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
									MapFound(Floor(EntityX(r\OBJ) / 8.0), Floor(EntityZ(r\OBJ) / 8.0)) = 1
								EndIf
							End If
						Next
						
						DrawLoading(100)
						
						DropSpeed = 0
						
						UpdateWorld(0.0)
						
						PrevTime = MilliSecs()
						FPSfactor = 0
						
						ResetInput()
					EndIf
				Else
					DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "")
					Color(50, 50, 50)
					AAText(x + 185 * MenuScale, y + 30 * MenuScale, "Load Game", True, True)
				EndIf
				If DrawButton(x, y + 80 * MenuScale, 390 * MenuScale, 60 * MenuScale, "Quit to Menu") Then
					NullGame()
					MenuOpen = False
					MainMenuOpen = True
					MainMenuTab = 0
					CurrSave = ""
					FlushKeys()
				EndIf
				y= y + 80 * MenuScale
			EndIf
			
			If KillTimer >= 0 And (Not MainMenuOpen)
				If DrawButton(x, y, 390 * MenuScale, 60 * MenuScale, "Quit") Then
					QuitMsg = 1
				EndIf
			EndIf
			
			AASetFont(Font1)
			If KillTimer < 0 Then RowText(DeathMsg, x, y + 80 * MenuScale, 390 * MenuScale, 600 * MenuScale)
		EndIf
		If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	End If
	
	AASetFont(Font1)
	
	CatchErrors("DrawMenu")
End Function

Function MouseOn%(x%, y%, Width%, Height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + Width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + Height Then
			Return(True)
		End If
	End If
	Return(False)
End Function

Include "Source Code\LoadAllSounds.bb"

Function LoadEntities()
	CatchErrors("Uncaught (LoadEntities)")
	DrawLoading(0)
	
	Local i%
	
	For i = 0 To 9
		TempSounds[i] = 0
	Next
	
	PauseMenuIMG = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	MaskImage(PauseMenuIMG, 255, 255, 0)
	ScaleImage(PauseMenuIMG, MenuScale, MenuScale)
	
	SprintIcon = LoadImage_Strict("GFX\sprinticon.png")
	BlinkIcon = LoadImage_Strict("GFX\blinkicon.png")
	CrouchIcon = LoadImage_Strict("GFX\sneakicon.png")
	HandIcon = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2 = LoadImage_Strict("GFX\handsymbol2.png")

	StaminaMeterIMG = LoadImage_Strict("GFX\staminameter.jpg")

	KeypadHUD =  LoadImage_Strict("GFX\keypadhud.jpg")
	MaskImage(KeypadHUD, 255, 0, 255)

	Panel294 = LoadImage_Strict("GFX\294panel.jpg")
	MaskImage(Panel294, 255, 0, 255)
	
	Brightness = GetINIFloat(OptionFile, "global", "brightness")
	CameraFogNear = GetINIFloat(OptionFile, "global", "camera fog near")
	CameraFogFar = GetINIFloat(OptionFile, "global", "camera fog far")
	StoredCameraFogFar = CameraFogFar
	
	AmbientLightRoomTex = CreateTexture(2, 2, 257)
	TextureBlend(AmbientLightRoomTex, 5)
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor(0, 0, 0)
	Cls
	SetBuffer(BackBuffer())
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport(Camera, 0, 0, GraphicWidth, GraphicHeight)
	CameraRange(Camera, 0.05, CameraFogFar)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, CameraFogNear, CameraFogFar)
	CameraFogColor(Camera, 0.0, 0.0, 0.0)
	AmbientLight(Brightness, Brightness, Brightness)
	
	ScreenTexs[0] = CreateTexture(512, 512, 1 + 256)
	ScreenTexs[1] = CreateTexture(512, 512, 1 + 256)
	
	CreateBlurImage()
	CameraProjMode(Ark_Blur_Cam, 0)
	
	FogTexture = LoadTexture_Strict("GFX\fog.jpg", 1)
	
	Fog = CreateSprite(Ark_Blur_Cam)
	ScaleSprite(Fog, Max(GraphicWidth / 1240, 1), Max(GraphicHeight / 960 * 0.8, 0.8))
	EntityTexture(Fog, FogTexture)
	EntityBlend(Fog, 2)
	EntityOrder(Fog, -1000)
	MoveEntity(Fog, 0.0, 0.0, 1.0)
	
	GasMaskTexture = LoadTexture_Strict("GFX\GasmaskOverlay.jpg", 1)
	GasMaskOverlay = CreateSprite(Ark_Blur_Cam)
	ScaleSprite(GasMaskOverlay, Max(GraphicWidth / 1024, 1), Max(GraphicHeight / 1024 * 0.8, 0.8))
	EntityTexture(GasMaskOverlay, GasMaskTexture)
	EntityBlend(GasMaskOverlay, 2)
	EntityFX(GasMaskOverlay, 1)
	EntityOrder(GasMaskOverlay, -1003)
	MoveEntity(GasMaskOverlay, 0.0, 0.0, 1.0)
	HideEntity(GasMaskOverlay)
	
	InfectTexture = LoadTexture_Strict("GFX\InfectOverlay.jpg", 1)
	InfectOverlay = CreateSprite(Ark_Blur_Cam)
	ScaleSprite(InfectOverlay, Max(GraphicWidth / 1024, 1), Max(GraphicHeight / 1024 * 0.8, 0.8))
	EntityTexture(InfectOverlay, InfectTexture)
	EntityBlend(InfectOverlay, 3)
	EntityFX(InfectOverlay, 1)
	EntityOrder(InfectOverlay, -1003)
	MoveEntity(InfectOverlay, 0.0, 0.0, 1.0)
	HideEntity(InfectOverlay)
	
	NVTexture = LoadTexture_Strict("GFX\NightVisionOverlay.jpg", 1)
	NVOverlay = CreateSprite(Ark_Blur_Cam)
	ScaleSprite(NVOverlay, Max(GraphicWidth / 1024, 1), Max(GraphicHeight / 1024 * 0.8, 0.8))
	EntityTexture(NVOverlay, NVTexture)
	EntityBlend(NVOverlay, 2)
	EntityFX(NVOverlay, 1)
	EntityOrder(NVOverlay, -1003)
	MoveEntity(NVOverlay, 0.0, 0.0, 1.0)
	HideEntity(NVOverlay)
	
	NVBlink = CreateSprite(Ark_Blur_Cam)
	ScaleSprite(NVBlink, Max(GraphicWidth / 1024, 1), Max(GraphicHeight / 1024 * 0.8, 0.8))
	EntityColor(NVBlink, 0, 0, 0)
	EntityFX(NVBlink, 1)
	EntityOrder(NVBlink, -1005)
	MoveEntity(NVBlink, 0.0, 0.0, 1.0)
	HideEntity(NVBlink)
	
	FogNVTexture = LoadTexture_Strict("GFX\fogNV.jpg", 1)
	
	DrawLoading(5)
	
	DarkTexture = CreateTexture(1024, 1024, 1 + 2)
	SetBuffer(TextureBuffer(DarkTexture))
	Cls
	SetBuffer(BackBuffer())
	
	Dark = CreateSprite(Camera)
	ScaleSprite(Dark, Max(GraphicWidth / 1240, 1), Max(GraphicHeight / 960 * 0.8, 0.8))
	EntityTexture(Dark, DarkTexture)
	EntityBlend(Dark, 1)
	EntityOrder(Dark, -1002)
	MoveEntity(Dark, 0.0, 0.0, 1.0)
	EntityAlpha(Dark, 0.0)
	
	LightTexture = CreateTexture(1024, 1024, 1 + 2 + 256)
	SetBuffer(TextureBuffer(LightTexture))
	ClsColor(255, 255, 255)
	Cls
	ClsColor(0, 0, 0)
	SetBuffer(BackBuffer())
	
	TeslaTexture = LoadTexture_Strict("GFX\map\tesla.jpg", 1 + 2)
	
	Light = CreateSprite(Camera)
	ScaleSprite(Light, Max(GraphicWidth / 1240, 1), Max(GraphicHeight / 960 * 0.8, 0.8))
	EntityTexture(Light, LightTexture)
	EntityBlend(Light, 1)
	EntityOrder(Light, -1002)
	MoveEntity(Light, 0.0, 0.0, 1.0)
	HideEntity(Light)
	
	Collider = CreatePivot()
	EntityRadius Collider, 0.15, 0.30
	EntityPickMode(Collider, 1)
	EntityType(Collider, HIT_PLAYER)
	
	Head = CreatePivot()
	EntityRadius(Head, 0.15)
	EntityType(Head, HIT_PLAYER)
	
	LiquidOBJ = LoadMesh_Strict("GFX\items\cupliquid.x") ; ~ Optimized the cups dispensed by 294
	
	MTFOBJ = LoadAnimMesh_Strict("GFX\npcs\MTF2.b3d") ; ~ Optimized MTFs
	GuardOBJ = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ; ~ Optimized Guards
	
	ClassDOBJ = LoadAnimMesh_Strict("GFX\npcs\classd.b3d") ; ~ Optimized Class-D's and scientists/researchers
	ApacheOBJ = LoadAnimMesh_Strict("GFX\apache.b3d") ; ~ Ooptimized Apaches (helicopters)
	ApacheRotorOBJ = LoadAnimMesh_Strict("GFX\apacherotor.b3d") ; ~ Optimized the Apaches even more
	
	HideEntity(LiquidOBJ)
	HideEntity(MTFOBJ)
	HideEntity(GuardOBJ)
	HideEntity(ClassDOBJ)
	HideEntity(ApacheOBJ)
	HideEntity(ApacheRotorOBJ)
	
	NPC049OBJ = LoadAnimMesh_Strict("GFX\npcs\scp-049.b3d")
	HideEntity(NPC049OBJ)
	NPC0492OBJ = LoadAnimMesh_Strict("GFX\npcs\zombie1.b3d")
	HideEntity(NPC0492OBJ)
	ClerkOBJ = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d")
	HideEntity(ClerkOBJ)	
	
	LightSpriteTex(0) = LoadTexture_Strict("GFX\light1.jpg", 1)
	LightSpriteTex(1) = LoadTexture_Strict("GFX\light2.jpg", 1)
	LightSpriteTex(2) = LoadTexture_Strict("GFX\lightsprite.jpg", 1)
	
	DrawLoading(10)
	
	DoorOBJ = LoadMesh_Strict("GFX\map\door01.x")
	HideEntity(DoorOBJ)
	DoorFrameOBJ = LoadMesh_Strict("GFX\map\doorframe.x")
	HideEntity(DoorFrameOBJ)
	
	HeavyDoorOBJ(0) = LoadMesh_Strict("GFX\map\heavydoor1.x")
	HideEntity(HeavyDoorOBJ(0))
	HeavyDoorOBJ(1) = LoadMesh_Strict("GFX\map\heavydoor2.x")
	HideEntity(HeavyDoorOBJ(1))
	
	DoorColl = LoadMesh_Strict("GFX\map\doorcoll.x")
	HideEntity(DoorColl)
	
	ButtonOBJ = LoadMesh_Strict("GFX\map\Button.x")
	HideEntity(ButtonOBJ)
	ButtonKeyOBJ = LoadMesh_Strict("GFX\map\ButtonKeycard.x")
	HideEntity(ButtonKeyOBJ)
	ButtonCodeOBJ = LoadMesh_Strict("GFX\map\ButtonCode.x")
	HideEntity(ButtonCodeOBJ)	
	ButtonScannerOBJ = LoadMesh_Strict("GFX\map\ButtonScanner.x")
	HideEntity(ButtonScannerOBJ)	
	
	BigDoorOBJ(0) = LoadMesh_Strict("GFX\map\ContDoorLeft.x")
	HideEntity(BigDoorOBJ(0))
	BigDoorOBJ(1) = LoadMesh_Strict("GFX\map\ContDoorRight.x")
	HideEntity(BigDoorOBJ(1))
	
	LeverBaseOBJ = LoadMesh_Strict("GFX\map\leverbase.x")
	HideEntity(LeverBaseOBJ)
	LeverOBJ = LoadMesh_Strict("GFX\map\leverhandle.x")
	HideEntity(LeverOBJ)
	
	DrawLoading(15)
	
	For i = 0 To 5
		GorePics(i) = LoadTexture_Strict("GFX\895pics\pic" + (i + 1) + ".jpg")
	Next
	
	OldAiPics(0) = LoadTexture_Strict("GFX\AIface.jpg")
	OldAiPics(1) = LoadTexture_Strict("GFX\AIface2.jpg")	
	
	DrawLoading(20)
	
	For i = 0 To 6
		DecalTextures(i) = LoadTexture_Strict("GFX\decal" + (i + 1) + ".png", 1 + 2)
	Next
	DecalTextures(7) = LoadTexture_Strict("GFX\items\INVpaperstrips.jpg", 1 + 2)
	For i = 8 To 12
		DecalTextures(i) = LoadTexture_Strict("GFX\decalpd" + (i - 7) + ".jpg", 1 + 2)	
	Next
	For i = 13 To 14
		DecalTextures(i) = LoadTexture_Strict("GFX\bullethole" + (i - 12) + ".jpg", 1 + 2)	
	Next	
	For i = 15 To 16
		DecalTextures(i) = LoadTexture_Strict("GFX\blooddrop" + (i - 14) + ".png", 1 + 2)	
	Next
	DecalTextures(17) = LoadTexture_Strict("GFX\decal8.png", 1 + 2)	
	DecalTextures(18) = LoadTexture_Strict("GFX\decalpd6.dc", 1 + 2)	
	DecalTextures(19) = LoadTexture_Strict("GFX\decal19.png", 1 + 2)
	DecalTextures(20) = LoadTexture_Strict("GFX\decal427.png", 1 + 2)
	
	DrawLoading(25)
	
	Monitor = LoadMesh_Strict("GFX\map\monitor.b3d")
	HideEntity(Monitor)
	MonitorTexture = LoadTexture_Strict("GFX\monitortexture.jpg")
	
	CamBaseOBJ = LoadMesh_Strict("GFX\map\cambase.x")
	HideEntity(CamBaseOBJ)
	CamOBJ = LoadMesh_Strict("GFX\map\CamHead.b3d")
	HideEntity(CamOBJ)
	
	Monitor2 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity(Monitor2)
	Monitor3 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity(Monitor3)
	MonitorTexture2 = LoadTexture_Strict("GFX\map\LockdownScreen2.jpg")
	MonitorTexture3 = LoadTexture_Strict("GFX\map\LockdownScreen.jpg")
	MonitorTexture4 = LoadTexture_Strict("GFX\map\LockdownScreen3.jpg")
	MonitorTextureOff = CreateTexture(1, 1)
	SetBuffer(TextureBuffer(MonitorTextureOff))
	ClsColor(0, 0, 0)
	Cls
	SetBuffer(BackBuffer())
	
	For i = 2 To CountSurfaces(Monitor2)
		SF = GetSurface(Monitor2, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name$ = StripPath(TextureName(t1))
				If Lower(Name) <> "monitortexture.jpg"
					BrushTexture(b, MonitorTextureOff, 0, 0)
					PaintSurface(SF, b)
				EndIf
				If name <> "" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	For i = 2 To CountSurfaces(Monitor3)
		sf = GetSurface(Monitor3, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name$ = StripPath(TextureName(t1))
				If Lower(Name) <> "monitortexture.jpg"
					BrushTexture(b, MonitorTextureOff, 0, 0)
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then FreeTexture(t1)
			EndIf
			FreeBrush b
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
			File$ = NextFile(Dir)
			If File$ = "" Then Exit
			If FileType("SFX\Radio\UserTracks\" + File$) = 1 Then
				Test = LoadSound("SFX\Radio\UserTracks\" + File$)
				If Test <> 0
					UserTrackName(UserTrackMusicAmount) = File$
					UserTrackMusicAmount = UserTrackMusicAmount + 1
				EndIf
				FreeSound(Test)
			EndIf
		Forever
		CloseDir(Dir)
	EndIf
	
	InitItemTemplates()
	
	ParticleTextures(0) = LoadTexture_Strict("GFX\smoke.png", 1 + 2)
	ParticleTextures(1) = LoadTexture_Strict("GFX\flash.jpg", 1 + 2)
	ParticleTextures(2) = LoadTexture_Strict("GFX\dust.jpg", 1 + 2)
	ParticleTextures(3) = LoadTexture_Strict("GFX\npcs\hg.pt", 1 + 2)
	ParticleTextures(4) = LoadTexture_Strict("GFX\map\sun.jpg", 1 + 2)
	ParticleTextures(5) = LoadTexture_Strict("GFX\bloodsprite.png", 1 + 2)
	ParticleTextures(6) = LoadTexture_Strict("GFX\smoke2.png", 1 + 2)
	ParticleTextures(7) = LoadTexture_Strict("GFX\spark.jpg", 1 + 2)
	ParticleTextures(8) = LoadTexture_Strict("GFX\particle.png", 1 + 2)
	
	SetChunkDataValues()
	
	; ~ NPCtypeD - different models with different textures (loaded using "CopyEntity") -- ENDSHN
	For i = 1 To MaxDTextures
		DTextures[i] = CopyEntity(ClassDOBJ)
		HideEntity(DTextures[i])
	Next
	
	; ~ Gonzales
	Tex = LoadTexture_Strict("GFX\npcs\Gonzales.png")
	EntityTexture(DTextures[1], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-970's corpse
	Tex = LoadTexture_Strict("GFX\npcs\scp_970_corpse.png")
	EntityTexture(DTextures[2], Tex)
	FreeTexture(Tex)
	
	; ~ Scientist
	Tex = LoadTexture_Strict("GFX\npcs\scientist.png")
	EntityTexture(DTextures[3], Tex)
	FreeTexture(Tex)
	
	; ~ Franklin
	Tex = LoadTexture_Strict("GFX\npcs\Franklin.png")
	EntityTexture(DTextures[4], Tex)
	FreeTexture(Tex)
	
	; ~ Janitor # 1
	Tex = LoadTexture_Strict("GFX\npcs\janitor.png")
	EntityTexture(DTextures[5], Tex)
	FreeTexture(Tex)
	
	; ~ Maynard
	Tex = LoadTexture_Strict("GFX\npcs\Maynard.png")
	EntityTexture(DTextures[6], Tex)
	FreeTexture(Tex)
	
	; ~ Afro-American Class-D
	Tex = LoadTexture_Strict("GFX\npcs\class_d(2).png")
	EntityTexture(DTextures[7], Tex)
	FreeTexture(Tex)
	
	; ~ 035 victim
	Tex = LoadTexture_Strict("GFX\npcs\scp_035_victim.png")
	EntityTexture(DTextures[8], Tex)
	FreeTexture(Tex)
	
	; ~ D-9341
	Tex = LoadTexture_Strict("GFX\npcs\D_9341.png")
	EntityTexture(DTextures[9], Tex)
	FreeTexture(Tex)
	
	; ~ Body # 1
	Tex = LoadTexture_Strict("GFX\npcs\body.png")
	EntityTexture(DTextures[10], Tex)
	FreeTexture(Tex)
	
	; ~ Body # 2
	Tex = LoadTexture_Strict("GFX\npcs\body(2).png")
	EntityTexture(DTextures[11], Tex)
	FreeTexture(Tex)
	
	; ~ Janitor # 2
	Tex = LoadTexture_Strict("GFX\npcs\janitor(2).png")
	EntityTexture(DTextures[12], Tex)
	FreeTexture(Tex)
	
	; ~ SCP-008-1's victim
	Tex = LoadTexture_Strict("GFX\npcs\scp_008_1_victim.png")
	EntityTexture(DTextures[13], Tex)
	FreeTexture(Tex)
	
	LoadMaterials("Data\materials.ini")
	
	OBJTunnel(0) = LoadRMesh("GFX\map\mt1.rmesh", Null)	
	HideEntity(OBJTunnel(0))	
	
	OBJTunnel(1) = LoadRMesh("GFX\map\mt2.rmesh", Null)	
	HideEntity(OBJTunnel(1))
	
	OBJTunnel(2) = LoadRMesh("GFX\map\mt2c.rmesh", Null)	
	HideEntity(OBJTunnel(2))
	
	OBJTunnel(3) = LoadRMesh("GFX\map\mt3.rmesh", Null)	
	HideEntity(OBJTunnel(3))
	
	OBJTunnel(4) = LoadRMesh("GFX\map\mt4.rmesh", Null)	
	HideEntity(OBJTunnel(4))
	
	OBJTunnel(5) = LoadRMesh("GFX\map\mt_elevator.rmesh", Null)
	HideEntity(OBJTunnel(5))
	
	OBJTunnel(6) = LoadRMesh("GFX\map\mt_generator.rmesh", Null)
	HideEntity(OBJTunnel(6))
	
	TextureLodBias(TextureFloat)
	; ~ Devil Particle System
	; ~ ParticleEffect[] numbers:
	; ~ 0 - electric spark
	; ~ 1 - smoke effect
	
	Local t0%
	
	InitParticles(Camera)
	
	; ~ Spark Effect (short)
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 3)
	SetTemplateInterval(ParticleEffect[0], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[0], 6)
	SetTemplateEmitterLifeTime(ParticleEffect[0], 6)
	SetTemplateParticleLifeTime(ParticleEffect[0], 20, 30)
	SetTemplateTexture(ParticleEffect[0], "GFX\Spark.png", 2, 3)
	SetTemplateOffset(ParticleEffect[0], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[0], -0.0375, 0.0375, -0.0375, 0.0375, -0.0375, 0.0375)
	SetTemplateAlignToFall(ParticleEffect[0], True, 45)
	SetTemplateGravity(ParticleEffect[0], 0.001)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	SetTemplateSize(ParticleEffect[0], 0.03125, 0.0625, 0.7, 1)
	SetTemplateColors(ParticleEffect[0], $0000FF, $6565FF)
	SetTemplateFloor(ParticleEffect[0], 0.0, 0.5)
	
	; ~ Smoke effect (for some vents)
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateInterval(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], 3)
	SetTemplateParticleLifeTime(ParticleEffect[1], 30, 45)
	SetTemplateTexture(ParticleEffect[1], "GFX\smoke2.png", 2, 1)
	SetTemplateOffset(ParticleEffect[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[1], 0.0, 0.0, 0.02, 0.025, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	SetTemplateSize(ParticleEffect[1], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], .01, 1.01)
	
	; ~ Smoke effect (for decontamination gas)
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateInterval(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 3)
	SetTemplateParticleLifeTime(ParticleEffect[2], 30, 45)
	SetTemplateTexture(ParticleEffect[2], "GFX\smoke.png", 2, 1)
	SetTemplateOffset(ParticleEffect[2], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[2], -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[2], True)
	SetTemplateSize(ParticleEffect[2], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[2], .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, 3)
	SetTemplateParticleLifeTime(t0, 30, 45)
	SetTemplateTexture(t0, "GFX\smoke2.png", 2, 1)
	SetTemplateOffset(t0, -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(t0, -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(t0, .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	SetTemplateSubTemplate(ParticleEffect[2], t0)
	
	Room2slCam = CreateCamera()
	CameraViewport(Room2slCam, 0, 0, 128, 128)
	CameraRange(Room2slCam, 0.05, 6.0)
	CameraZoom(Room2slCam, 0.8)
	HideEntity(Room2slCam)
	
	DrawLoading(30)
	
	CatchErrors("LoadEntities")
End Function

Function InitNewGame()
	CatchErrors("Uncaught (InitNewGame)")
	
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events
	
	DrawLoading(45)
	
	HideDistance = 15.0
	
	HeartBeatRate = 70
	
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
	Curr106 = CreateNPC(NPCtypeOldMan, 0.0, -30.0, 0.0)
	Curr106\State = 70 * 60.0 * Rnd(12.0, 17.0)
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		If d\Buttons[0] <> 0 Then EntityParent(d\Buttons[0], 0)
		If d\Buttons[1] <> 0 Then EntityParent(d\Buttons[1], 0)
		
		If d\OBJ2 <> 0 And d\Dir = 0 Then
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
		For i = 0 To MaxRoomLights
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
		
		If (r\RoomTemplate\Name = "start" And IntroEnabled = False) Then 
			PositionEntity(Collider, EntityX(r\OBJ) + 3584.0 * RoomScale, 704.0 * RoomScale, EntityZ(r\OBJ) + 1024.0 * RoomScale)
			PlayerRoom = r
			it = CreateItem("Class D Orientation Leaflet", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\ItemTemplate\Found = True
			Inventory(0) = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
			it = CreateItem("Document SCP-173", "paper", 1, 1, 1)
			it\Picked = True
			it\Dropped = -1
			it\ItemTemplate\Found = True
			Inventory(1) = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
		ElseIf (r\RoomTemplate\Name = "173" And IntroEnabled) Then
			PositionEntity(Collider, EntityX(r\OBJ), 1.0, EntityZ(r\OBJ))
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
	
	TurnEntity(Collider, 0.0, Rand(160.0, 200.0), 0.0)
	
	ResetEntity(Collider)
	
	If SelectedMap = "" Then InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2nuke"
			e\EventState = 1
		EndIf
		If e\EventName = "room106"
			e\EventState2 = 1
		EndIf	
		If e\EventName = "room2sl"
			e\EventState3 = 1
		EndIf
	Next
	
	MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	
	AASetFont(Font1)
	
	HidePointer()
	
	BlinkTimer = -10
	BlurTimer = 100
	Stamina = 100
	
	For i = 0 To 70
		FPSfactor = 1.0
		FlushKeys()
		MovePlayer()
		UpdateDoors()
		UpdateNPCs()
		UpdateWorld()
		If (Int(Float(i) * 0.27) <> Int(Float(i - 1) * 0.27)) Then
			DrawLoading(80 + Int(Float(i) * 0.27))
		EndIf
	Next
	
	FreeTextureCache
	DrawLoading(100)
	
	FlushKeys
	FlushMouse
	
	DropSpeed = 0
	
	PrevTime = MilliSecs()
	CatchErrors("InitNewGame")
End Function

Function InitLoadGame()
	CatchErrors("Uncaught (InitLoadGame)")
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	
	DrawLoading(80)
	
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
	
	ResetEntity(Collider)
	
	DrawLoading(90)
	
	MoveMouse(Viewport_Center_X, Viewport_Center_Y)
	
	AASetFont(Font1)
	
	HidePointer()
	
	BlinkTimer = BLINKFREQ
	Stamina = 100
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\OBJ <> 0 Then FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	DropSpeed = 0.0
	
	For e.Events = Each Events
		; ~ Loading the necessary stuff for dimension1499, but this will only be done if the player is in this dimension already
		If e\EventName = "dimension1499"
			If e\EventState = 2
				;[Block]
				DrawLoading(91)
				e\room\Objects[0] = CreatePlane()
				
				Local PlaneTex% = LoadTexture_Strict("GFX\map\dimension1499\grit3.jpg")
				
				EntityTexture(e\room\Objects[0], PlaneTex)
				FreeTexture(PlaneTex)
				PositionEntity(e\room\Objects[0], 0, EntityY(e\room\OBJ), 0)
				EntityType(e\room\Objects[0], HIT_MAP)
				DrawLoading(92)
				NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
				DrawLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict("GFX\map\dimension1499\1499object" + i + ".b3d")
					HideEntity(e\room\Objects[i])
				Next
				DrawLoading(96)
				CreateChunkParts(e\room)
				DrawLoading(97)
				x# = EntityX(e\room\OBJ)
				z# = EntityZ(e\room\OBJ)
				
				Local ch.Chunk
				
				For i = -2 To 2 Step 2
					ch = CreateChunk(-1, x# * (i * 2.5), EntityY(e\room\OBJ), z#)
				Next
				DrawLoading(98)
				UpdateChunks(e\room, 15, False)
				Exit
				;[End Block]
			EndIf
		EndIf
	Next
	
	FreeTextureCache
	
	CatchErrors("InitLoadGame")
	DrawLoading(100)
	
	PrevTime = MilliSecs()
	FPSfactor = 0
	ResetInput()
End Function

Function NullGame(PlayButtonSFX% = True)
	CatchErrors("Uncaught (NullGame)")
	
	Local i%, x%, y%, Lvl%
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items
	
	KillSounds()
	If PlayButtonSFX Then PlaySound_Strict(ButtonSFX)
	
	FreeParticles()
	
	ClearTextureCache
	
	DebugHUD = False
	
	UnableToMove = False
	
	QuickLoadPercent = -1
	QuickLoadPercent_DisplayTimer# = 0
	QuickLoad_CurrEvent = Null
	
	DeathMsg = ""
	
	chs\GodMode = False
	chs\NoTarget = False
	chs\InfiniteStamina = False
	chs\NoBlink = False
	chs\NoClip = False
	chs\Cheats = False
	
	SelectedMap = ""
	
	UsedConsole = False
	
	DoorTempID = 0
	RoomTempID = 0
	
	GameSaved = 0
	
	HideDistance# = 15.0
	
	For Lvl = 0 To 0
		For x = 0 To MapWidth + 1
			For y = 0 To MapHeight + 1
				MapTemp(x, y) = 0
				MapFound(x, y) = 0
			Next
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\Found = False
	Next
	
	DropSpeed = 0.0
	Shake = 0.0
	CurrSpeed = 0.0
	
	DeathTimer = 0.0
	
	HeartBeatVolume = 0.0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0.0
	BlinkEffect = 1.0
	BlinkEffectTimer = 0.0
	
	Bloodloss = 0.0
	Injuries = 0.0
	Infect = 0.0
	
	For i = 0 To 5
		SCP1025State[i] = 0.0
	Next
	
	SelectedEnding = ""
	EndingTimer = 0.0
	ExplosionTimer = 0.0
	
	CameraShake = 0.0
	Shake = 0.0
	LightFlash = 0.0
	
	WireframeState = 0
	WireFrame(0)
	WearingGasMask = 0
	WearingHazmat = 0
	WearingVest = 0
	Wearing714 = 0
	If WearingNightVision Then
		CameraFogFar = StoredCameraFogFar
		WearingNightVision = 0
	EndIf
	I_427\Using = 0
	I_427\Timer = 0.0
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
	CoffinDistance = 100.0
	
	Contained106 = False
	If Curr173 <> Null Then Curr173\Idle = False
	
	MTFTimer = 0.0
	For i = 0 To 9
		MTFRooms[i] = Null
		MTFRoomState[i] = 0
	Next
	
	For s.Screens = Each Screens
		If s\Img <> 0 Then FreeImage(s\Img) : s\Img = 0
		Delete(s)
	Next
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Achievements(i) = 0
	Next
	RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	EyeIrritation = 0
	EyeStuck = 0
	
	ShouldPlay = 0
	
	KillTimer = 0.0
	FallTimer = 0.0
	Stamina = 100.0
	BlurTimer = 0.0
	SuperMan = False
	SuperManTimer = 0.0
	Sanity = 0.0
	RestoreSanity = True
	Crouch = False
	CrouchState = 0.0
	LightVolume = 0.0
	Vomit = False
	VomitTimer = 0.0
	SecondaryLightOn# = True
	PrevSecondaryLightOn# = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	Msg = ""
	MsgTimer = 0.0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory(i) = Null
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
	
	For de.decals = Each Decals
		Delete(de)
	Next
	
	For n.NPCs = Each NPCs
		Delete(n)
	Next
	
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	
	For i = 0 To 6
		MTFRooms[i] = Null
	Next
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	
	For e.Events = Each Events
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound)
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2)
		
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
	
	For i = 0 To 5
		If ChannelPlaying(RadioCHN(i)) Then StopChannel(RadioCHN(i))
	Next
	
	NTF_1499PrevX = 0.0
	NTF_1499PrevY = 0.0
	NTF_1499PrevZ = 0.0
	NTF_1499PrevRoom = Null
	NTF_1499X = 0.0
	NTF_1499Y = 0.0
	NTF_1499Z = 0.0
	Wearing1499 = False
	DeleteChunks()
	
	DeleteDevilEmitters()
	
	OptionsMenu = -1
	QuitMsg = -1
	AchievementsMenu% = -1
	
	MusicVolume = PrevMusicVolume
	SFXVolume = PrevSFXVolume
	DeafPlayer = False
	DeafTimer = 0.0
	
	IsZombie = False
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	ClearWorld
	ReloadAAFont()
	Camera = 0
	Ark_Blur_Cam = 0
	Collider = 0
	Sky = 0
	InitFastResize()
	
	CatchErrors("NullGame")
End Function

Include "Source Code\Save_System.bb"

Function PlaySound2%(SoundHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	Local SoundCHN% = 0
	
	If Volume > 0.0 Then 
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SoundCHN = PlaySound_Strict(SoundHandle)
			
			ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * SFXVolume)
			ChannelPan(SoundCHN, PanValue)			
		EndIf
	EndIf
	Return(SoundCHN)
End Function

Function LoopSound2%(SoundHandle%, CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
		
		If CHN = 0 Then
			CHN = PlaySound_Strict(SoundHandle)
		Else
			If (Not ChannelPlaying(CHN)) Then CHN = PlaySound_Strict(SoundHandle)
		EndIf
		
		ChannelVolume(CHN, Volume * (1.0 - Dist) * SFXVolume)
		ChannelPan(CHN, PanValue)
	Else
		If CHN <> 0 Then
			ChannelVolume (CHN, 0)
		EndIf 
	EndIf
	Return(CHN)
End Function

Function LoadTempSound(File$)
	If TempSounds[TempSoundIndex] <> 0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(File)
	TempSounds[TempSoundIndex] = TempSound
	TempSoundIndex = (TempSoundIndex + 1) Mod 10
	Return(TempSound)
End Function

Function LoadEventSound(e.Events, File$, Number% = 0)
	If Number = 0 Then
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		e\Sound = LoadSound_Strict(File)
		Return(e\Sound)
	Else If Number = 1 Then
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		e\Sound2 = LoadSound_Strict(File)
		Return(e\Sound2)
	EndIf
End Function

Function UpdateMusic()
	If ConsoleFlush Then
		If Not ChannelPlaying(ConsoleMusPlay) Then ConsoleMusPlay = PlaySound(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay ; ~ Playing the wrong clip, fade out
			CurrMusicVolume = Max(CurrMusicVolume - (FPSfactor / 250.0), 0.0)
			If CurrMusicVolume = 0
				If NowPlaying < 66
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic = 0
			EndIf
		Else ; ~ Playing the right clip
			CurrMusicVolume = CurrMusicVolume + (MusicVolume - CurrMusicVolume) * (0.1 * FPSfactor)
		EndIf
		
		If NowPlaying < 66
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict("SFX\Music\" + Music(NowPlaying) + ".ogg", 0.0, Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN, CurrMusicVolume)
		EndIf
	Else
		If FPSfactor > 0 Or OptionsMenu = 2 Then
			If ChannelPlaying(MusicCHN) = False Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume(MusicCHN, 1.0 * MusicVolume)
		EndIf
	EndIf
	
End Function 

Function PauseSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) = True Then PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, True)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) = True Then PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, True)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then PauseChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, True)
				EndIf
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then PauseChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, True)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then PauseChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then PauseChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then PauseChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN, True)
	EndIf
End Function

Function ResumeSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) = True Then ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, False)
			EndIf
		EndIf
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) = True Then ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, False)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then ResumeChannel(n\SoundCHN)
			Else
				If n\soundchn_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, False)
				EndIf
			EndIf
		EndIf
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then ResumeChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, False)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then ResumeChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then ResumeChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then ResumeChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN, False)
	EndIf
End Function

Function KillSounds()
	Local i%, e.Events, n.NPCs, d.Doors, dem.DevilEmitters, snd.Sound
	
	For i = 0 To 9
		If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
	Next
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream)
				If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream)
				If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then StopChannel(n\SoundCHN)
			Else
				StopStream_Strict(n\SoundCHN)
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then StopChannel(n\SoundCHN2)
			Else
				StopStream_Strict(n\SoundCHN2)
			EndIf
		EndIf
	Next
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then StopChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then StopChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then StopChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then StopChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	If EnableSFXRelease
		For snd.Sound = Each Sound
			If snd\InternalHandle <> 0 Then
				FreeSound(snd\InternalHandle)
				snd\InternalHandle = 0
				snd\ReleaseTime = 0
			EndIf
		Next
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				StopChannel(snd\Channels[i])
			EndIf
		Next
	Next
End Function

Function GetStepSound(Entity%)
    Local Picker%, Brush%, Texture%, Name$
    Local mat.Materials
    
    Picker = LinePick(EntityX(Entity), EntityY(Entity), EntityZ(Entity), 0, -1, 0)
    If Picker <> 0 Then
        If GetEntityType(Picker) <> HIT_MAP Then Return(0)
        Brush = GetSurfaceBrush(GetSurface(Picker, CountSurfaces(Picker)))
        If Brush <> 0 Then
            Texture = GetBrushTexture(Brush, 3)
            If Texture <> 0 Then
                Name = StripPath(TextureName(Texture))
                If (Name <> "") Then FreeTexture(Texture)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							FreeBrush(Brush)
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			Texture = GetBrushTexture(Brush, 2)
			If Texture <> 0 Then
				Name = StripPath(TextureName(Texture))
				If (Name <> "") Then FreeTexture(Texture)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							FreeBrush(Brush)
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			Texture = GetBrushTexture(Brush, 1)
			If Texture <> 0 Then
				Name = StripPath(TextureName(Texture))
				If (Name <> "") Then FreeTexture(Texture)
				FreeBrush(Brush)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
		EndIf
	EndIf
	Return(0)
End Function

Function UpdateSoundOrigin2(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume * (1. - Dist))
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume(CHN, 0)
		EndIf 
	EndIf
End Function

Function UpdateSoundOrigin(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range#
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume# * (1.0 - Dist) * SFXVolume)
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume (CHN, 0)
		EndIf 
	EndIf
End Function

Function AnimateNPC(n.NPCs, Start#, Quit#, Speed#, Loop% = True)
	Local NewTime#
	
	If Speed > 0.0 Then 
		NewTime = Max(Min(n\Frame + Speed * FPSfactor, Quit), Start)
		
		If Loop And NewTime >= Quit Then
			NewTime = Start
		EndIf
	Else
		If Start < Quit Then
			Temp% = Start
			Start = Quit
			Quit = Temp
		EndIf
		
		If Loop Then
			NewTime = n\Frame + Speed * FPSfactor
			
			If NewTime < Quit Then 
				NewTime = Start
			Else If NewTime > Start 
				NewTime = Quit
			EndIf
		Else
			NewTime = Max(Min(n\Frame + Speed * FPSfactor, Start), Quit)
		EndIf
	EndIf
	SetNPCFrame(n, NewTime)
End Function

Function SetNPCFrame(n.NPCs, Frame#)
	If (Abs(n\Frame - Frame) < 0.001) Then Return
	
	SetAnimTime(n\OBJ, Frame)
	
	n\Frame = Frame
End Function

Function Animate2#(Entity%, Curr#, Start%, Quit%, Speed#, Loop% = True)
	Local NewTime#
	
	If Speed > 0.0 Then 
		NewTime = Max(Min(Curr + Speed * FPSfactor, Quit), Start)
		
		If Loop Then
			If NewTime >= Quit Then 
				NewTime = Start
			EndIf
		EndIf
	Else
		If Start < Quit Then
			Temp% = Start
			Start = Quit
			Quit = Temp
		EndIf
		
		If Loop Then
			NewTime = Curr + Speed * FPSfactor
			
			If NewTime < Quit Then NewTime = Start
			If NewTime > Start Then NewTime = Quit
		Else
			NewTime = Max(Min(Curr + Speed * FPSfactor, Start), Quit)
		EndIf
	EndIf
	
	SetAnimTime(Entity, NewTime)
	
	Return(NewTime)
End Function 

Function Use914(item.Items, Setting$, x#, y#, z#)
	RefinedItems = RefinedItems + 1
	
	Local it2.Items, i%
	
	Select item\itemtemplate\name
		Case "Gas Mask", "Heavy Gas Mask"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case "fine", "very fine"
					;[Block]
					it2 = CreateItem("Gas Mask", "supergasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "SCP-1499"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Gas Mask", "gasmask", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("SCP-1499", "super1499", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
					;[Block]
					n.NPCs = CreateNPC(NPCtype1499, x, y, z)
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
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
		Case "Clipboard"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\obj, d\Size, d\Size)
					
					For i = 0 To 19
						If item\SecondInv[i] <> Null Then RemoveItem(item\SecondInv[i])
						item\SecondInv[i] = Null
					Next
					RemoveItem(item)
					;[End Block]
				Case "1:1"
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case "fine"
					;[Block]
					item\InvSlots = Max(item\State2, 15.0)
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case "very fine"
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
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					RemoveItem(item)
					;[End Block]
				Case "1:1"
					;[Block]
					PositionEntity(item\Collider, x, y, z)
					ResetEntity(item\Collider)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Night Vision Goggles", "finenvgoggles", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Night Vision Goggles", "supernv", x, y, z)
					it2\State = 1000.0
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Case "Metal Panel", "SCP-148 Ingot"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					it2 = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "1:1", "fine", "very fine"
					;[Block]
					it2 = Null
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And it\Picked = False Then
							If Distance(EntityX(it\Collider, True), EntityZ(it\Collider, True), EntityX(item\Collider, True), EntityZ(item\Collider, True)) < (180.0 * RoomScale) Then
								it2 = it
								Exit
							ElseIf Distance(EntityX(it\Collider, True), EntityZ(it\Collider, True), x, z) < (180.0 * RoomScale)
								it2 = it
								Exit
							End If
						End If
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
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(3, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1", "fine", "very fine"
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
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.12
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					If Rand(2) = 1 Then
						it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					Else
						it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
					EndIf
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			
			RemoveItem(item)
			;[End Block]
		Case "Level 1 Key Card", "Level 2 Key Card", "Level 3 Key Card", "Level 4 Key Card", "Level 5 Key Card"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Playing Card", "misc", x, y, z)
					;[End Block]
				Case "fine"
					;[Block]
					Select item\ItemTemplate\Name
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
								If Achievements(i) = True
									CurrAchvAmount = CurrAchvAmount + 1
								EndIf
							Next
							
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0
										it2 = CreateItem("Key Card Omni", "key6", x, y, z)
									Else
										it2 = CreateItem("Mastercard", "misc", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
					End Select
					;[End Block]
				Case "very fine"
					;[Block]
					CurrAchvAmount = 0
					For i = 0 To MAXACHIEVEMENTS - 1
						If Achievements(i) = True
							CurrAchvAmount = CurrAchvAmount + 1
						EndIf
					Next
					
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
						Case NORMAL
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
							EndIf
							;[End Block]
						Case HARD
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0
								it2 = CreateItem("Key Card Omni", "key6", x, y, z)
							Else
								it2 = CreateItem("Mastercard", "misc", x, y, z)
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
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					If Rand(2) = 1 Then
						it2 = CreateItem("Mastercard", "misc", x, y, z)
					Else
						it2 = CreateItem("Playing Card", "misc", x, y, z)			
					EndIf	
					;[End Block]
				Case "fine", "very fine"
					;[Block]
					it2 = CreateItem("Key Card Omni", "key6", x, y, z)
					;[End Block]
			End Select		
			RemoveItem(item)
			;[End Block]
		Case "Playing Card", "Coin", "Quarter"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
			    Case "fine", "very fine"
					;[Block]
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Mastercard"
			;[Block]
			Select Setting
				Case "rough"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.07
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "coarse"
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
				Case "1:1"
					;[Block]
					it2 = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
			    Case "fine", "very fine"
					;[Block]
					it2 = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "S-NAV 300 Navigator", "S-NAV 310 Navigator", "S-NAV Navigator", "S-NAV Navigator Ultimate"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					it2 = CreateItem("Electronical components", "misc", x, y, z)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("S-NAV 310 Navigator", "nav", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case "very fine"
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
				Case "rough", "coarse"
					;[Block]
					it2 = CreateItem("Electronical components", "misc", x, y, z)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\State = 100.0
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					it2\State = 101.0
					;[End Block]
				Case "very fine"
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
				Case "rough", "coarse"
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype5131 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1", "fine", "very fine"
					;[Block]
					it2 = CreateItem("SCP-513", "scp513", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Some SCP-420-J", "Cigarette"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Joint", "joint", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Smelly Joint", "scp420s", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "9V Battery", "18V Battery", "Strange Battery"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("18V Battery", "18vbat", x, y, z)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "ReVision Eyedrops", "RedVision Eyedrops", "Eyedrops"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("RedVision Eyedrops", "eyedrops", x, y, z)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Eyedrops", "fineeyedrops", x, y, z)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Eyedrops", "supereyedrops", x, y, z)
					;[End Block]
			End Select
			RemoveItem(item)
			;[End Block]
		Case "Hazmat Suit"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit", x, y, z)
					;[End Block]
				Case "fine"
					;[Block]
					it2 = CreateItem("Hazmat Suit", "hazmatsuit2", x, y, z)
					;[End Block]
				Case "very fine"
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
						Case "rough", "coarse"
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
							d\Size = 0.07
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case "1:1"
							;[Block]
							it2 = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
							;[End Block]
						Case "fine"
							;[Block]
							it2 = CreateItem("Syringe", "finesyringe", x, y, z)
							;[End Block]
						Case "very fine"
							;[Block]
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							;[End Block]
					End Select
					;[End Block]
				Case "finesyringe"
					;[Block]
					Select Setting
						Case "rough"
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
							d\Size = 0.07
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case "coarse"
							;[Block]
							it2 = CreateItem("First Aid Kit", "firstaid", x, y, z)
							;[End Block]
						Case "1:1"
							;[Block]
							it2 = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
							;[End Block]
						Case "fine", "very fine"
							;[Block]
							it2 = CreateItem("Syringe", "veryfinesyringe", x, y, z)
							;[End Block]
					End Select
					;[End Block]
				Case "veryfinesyringe"
					;[Block]
					Select Setting
						Case "rough", "coarse", "1:1", "fine"
							;[Block]
							it2 = CreateItem("Electronical components", "misc", x, y, z)	
							;[End Block]
						Case "very fine"
							;[Block]
							n.NPCs = CreateNPC(NPCtype008, x, y, z)
							n\State = 2.0
							;[End Block]
					End Select
			End Select
			RemoveItem(item)
			;[End Block]
		Case "SCP-500-01", "Upgraded pill", "Pill"
			;[Block]
			Select Setting
				Case "rough", "coarse"
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
					d\Size = 0.2
					EntityAlpha(d\OBJ, 0.8)
					ScaleSprite(d\OBJ, d\Size, d\Size)
					;[End Block]
				Case "1:1"
					;[Block]
					it2 = CreateItem("Pill", "pill", x, y, z)
					RemoveItem(item)
					;[End Block]
				Case "fine"
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
						it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					EndIf
					RemoveItem(item)
					;[End Block]
				Case "very fine"
					;[Block]
					it2 = CreateItem("Upgraded pill", "scp500death", x, y, z)
					RemoveItem(item)
					;[End Block]
			End Select
			;[End Block]
		Default
			;[Block]
			Select item\itemtemplate\tempname
				Case "cup"
					;[Block]
					Select Setting
						Case "rough", "coarse"
							;[Block]
							d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rand(360.0), 0.0)
							d\Size = 0.2
							EntityAlpha(d\OBJ, 0.8)
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case "1:1"
							;[Block]
							it2 = CreateItem("cup", "cup", x, y, z)
							it2\Name = item\Name
							it2\R = 255 - item\R
							it2\G = 255 - item\G
							it2\B = 255 - item\B
							;[End Block]
						Case "fine"
							;[Block]
							it2 = CreateItem("cup", "cup", x,y,z)
							it2\Name = item\Name
							it2\State = 1.0
							it2\R = Min(item\R * Rnd(0.9, 1.1), 255)
							it2\G = Min(item\G * Rnd(0.9, 1.1), 255)
							it2\B = Min(item\B * Rnd(0.9, 1.1), 255)
							;[End Block]
						Case "very fine"
							;[Block]
							it2 = CreateItem("cup", "cup", x, y, z)
							it2\Name = item\Name
							it2\State = Max(it2\State * 2.0, 2.0)	
							it2\R = Min(item\R * Rnd(0.5, 1.5), 255)
							it2\G = Min(item\G * Rnd(0.5, 1.5), 255)
							it2\B = Min(item\B * Rnd(0.5, 1.5), 255)
							If Rand(5) = 1 Then
								ExplosionTimer = 135
							EndIf
							;[End Block]
					End Select	
					RemoveItem(item)
					;[End Block]
				Case "paper"
					;[Block]
					Select Setting
						Case "rough", "coarse"
							;[Block]
							d.Decals = CreateDecal(7, x, 8.0 * RoomScale + 0.005, z, 90.0, Rand(360.0), 0.0)
							d\Size = 0.12
							ScaleSprite(d\OBJ, d\Size, d\Size)
							;[End Block]
						Case "1:1"
							;[Block]
							Select Rand(19)
								Case 1
									;[Block]
									it2 = CreateItem("Document SCP-008", "paper", x, y, z)
									;[End Block]
								Case 2
									;[Block]
									it2 = CreateItem("Document SCP-012", "paper", x, y, z)
									;[End Block]
								Case 3
									;[Block]
									it2 = CreateItem("Document SCP-035", "paper", x, y, z)
									;[End Block]
								Case 4
									;[Block]
									it2 = CreateItem("Document SCP-049", "paper", x, y, z)
									;[End Block]
								Case 5
									;[Block]
									it2 = CreateItem("Document SCP-096", "paper", x, y, z)
									;[End Block]
								Case 6
									;[Block]
									it2 = CreateItem("Document SCP-106", "paper", x, y, z)
									;[End Block]
								Case 7
									;[Block]
									it2 = CreateItem("Document SCP-173", "paper", x, y, z)
									;[End Block]
								Case 8
									;[Block]
									it2 = CreateItem("Document SCP-513", "paper", x, y, z)
									;[End Block]
								Case 9
									;[Block]
									it2 = CreateItem("Document SCP-682", "paper", x, y, z)
									;[End Block]
								Case 10
									;[Block]
									it2 = CreateItem("Document SCP-714", "paper", x, y, z)
									;[End Block]
								Case 11
									;[Block]
									it2 = CreateItem("Document SCP-860", "paper", x, y, z)
									;[End Block]
								Case 12
									;[Block]
									it2 = CreateItem("Document SCP-860-1", "paper", x, y, z)
									;[End Block]
								Case 13
									;[Block]
									it2 = CreateItem("Document SCP-895", "paper", x, y, z)
									;[End Block]
								Case 14
									;[Block]
									it2 = CreateItem("Document SCP-939", "paper", x, y, z)
									;[End Block]
								Case 15
									;[Block]
									it2 = CreateItem("Document SCP-966", "paper", x, y, z)
									;[End Block]
								Case 16
									;[Block]
									it2 = CreateItem("Document SCP-970", "paper", x, y, z)
									;[End Block]
								Case 17
									;[Block]
									it2 = CreateItem("Document SCP-1048", "paper", x, y, z)
									;[End Block]
								Case 18
									;[Block]
									it2 = CreateItem("Document SCP-1162", "paper", x, y, z)
									;[End Block]
								Case 19
									;[Block]
									it2 = CreateItem("Document SCP-1499", "paper", x, y, z)
									;[End Block]
							End Select
							;[End Block]
						Case "fine", "very fine"
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
	
	ShowPointer()
	
	x = GraphicWidth / 2 - (ImageWidth(Panel294) / 2)
	y = GraphicHeight / 2 - (ImageHeight(Panel294) / 2)
	DrawImage(Panel294, x, y)
	If FullScreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	
	Temp = True
	If PlayerRoom\SoundCHN <> 0 Then Temp = False
	
	AAText(x + 907, y + 185, Input294, True, True)
	
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
							Input294 = Left(Input294, Max(Len(Input294) - 1, 0))
							;[End Block]
					End Select
				Case 4
					;[Block]
					StrTemp = " "
					;[End Block]
			End Select
			
			Input294 = Input294 + StrTemp
			
			Input294 = Left(Input294, Min(Len(Input294), 15))
			
			If Temp And Input294 <> "" Then ; ~ Dispense
				Input294 = Trim(Lower(Input294))
				If Left(Input294, Min(7, Len(Input294))) = "cup of " Then
					Input294 = Right(Input294, Len(Input294) - 7)
				ElseIf Left(Input294, Min(9, Len(Input294))) = "a cup of " 
					Input294 = Right(Input294, Len(Input294) - 9)
				EndIf
				
				If Input294 <> ""
					Local Loc% = GetINISectionLocation("Data\SCP-294.ini", Input294)
				EndIf
				
				If Loc > 0 Then
					StrTemp = GetINIString2("Data\SCP-294.ini", Loc, "dispensesound")
					If StrTemp = "" Then
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound(StrTemp))
					EndIf
					
					If GetINIInt2("Data\SCP-294.ini", Loc, "explosion") = True Then 
						ExplosionTimer = 135
						DeathMsg = GetINIString2("Data\SCP-294.ini", Loc, "deathmessage")
					EndIf
					
					StrTemp = GetINIString2("Data\SCP-294.ini", Loc, "color")
					
					Sep1 = Instr(StrTemp, ",", 1)
					Sep2 = Instr(StrTemp, ",", Sep1 + 1)
					R% = Trim(Left(StrTemp, Sep1 - 1))
					G% = Trim(Mid(StrTemp, Sep1 + 1, Sep2 - Sep1 - 1))
					B% = Trim(Right(StrTemp, Len(StrTemp) - Sep2))
					
					Alpha# = Float(GetINIString2("Data\SCP-294.ini", Loc, "alpha", 1.0))
					Glow = GetINIInt2("Data\SCP-294.ini", Loc, "glow")
					If Glow Then Alpha = -Alpha
					
					it.Items = CreateItem("Cup", "cup", EntityX(PlayerRoom\Objects[1], True), EntityY(PlayerRoom\Objects[1], True), EntityZ(PlayerRoom\Objects[1], True), R, G, B, Alpha)
					it\Name = "Cup of " + Input294
					EntityType(it\Collider, HIT_ITEM)
				Else
					; ~ Out of range
					Input294 = "OUT OF RANGE"
					PlayerRoom\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\OutOfRange.ogg"))
				EndIf
			EndIf	
		EndIf
		
		If MouseHit2 Or (Not Using294) Then 
			HidePointer()
			Using294 = False
			Input294 = ""
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
		EndIf
	Else ; ~ Playing a dispensing sound
		If Input294 <> "OUT OF RANGE" Then Input294 = "DISPENSING..."
		
		If Not ChannelPlaying(PlayerRoom\SoundCHN) Then
			If Input294 <> "OUT OF RANGE" Then
				HidePointer()
				Using294 = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : Mouse_X_Speed_1 = 0.0 : Mouse_Y_Speed_1 = 0.0
				
				Local e.Events
				
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0
						Exit
					EndIf
				Next
			EndIf
			Input294 = ""
			PlayerRoom\SoundCHN = 0
		EndIf
	EndIf
End Function

Function Use427()
	Local i%, Pvt%, de.Decals, TempCHN%
	Local PrevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70 * 360.0
		If I_427\Using = True Then
			I_427\Timer = I_427\Timer + FPSfactor
			For e.Events = Each Events
				If e\EventName = "1048a" Then
					If e\EventState2 > 0.0 Then
						e\EventState2 = Max(e\EventState2 - 0.5 * FPSfactor, 0.0)
					EndIf
				EndIf
			Next
			If Injuries > 0.0 Then
				Injuries = Max(Injuries - 0.0005 * FPSfactor, 0.0)
			EndIf
			If Bloodloss > 0.0 And Injuries =< 1.0 Then
				Bloodloss = Max(Bloodloss - 0.001 * FPSfactor, 0.0)
			EndIf
			If Infect > 0.0 Then
				Infect = Max(Infect - 0.001 * FPSfactor, 0.0)
			EndIf
			For i = 0 To 5
				If SCP1025State[i] > 0.0 Then
					SCP1025State[i] = Max(SCP1025State[i] - 0.001 * FPSfactor, 0.0)
				EndIf
			Next
			If I_427\Sound[0] = 0 Then
				I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			EndIf
			If ChannelPlaying(I_427\SoundCHN[0]) = False Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer >= 70 * 180.0 Then
				If I_427\Sound[1] = 0 Then
					I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				EndIf
				If ChannelPlaying(I_427\SoundCHN[1]) = False Then
					I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
				EndIf
			EndIf
			If PrevI427Timer < 70 * 60.0 And I_427\Timer >= 70 * 60.0 Then
				Msg = "You feel refreshed and energetic."
				MsgTimer = 70 * 5.0
			ElseIf PrevI427Timer < 70 * 180.0 And I_427\Timer >= 70 * 180.0 Then
				Msg = "You feel gentle muscle spasms all over your body."
				MsgTimer = 70 * 5.0
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i] <> 0 Then
					If ChannelPlaying(I_427\SoundCHN[i]) = True Then StopChannel(I_427\SoundCHN[i])
				EndIf
			Next
		EndIf
	Else
		If PrevI427Timer - FPSfactor < 70 * 360.0 And I_427\Timer >= 70 * 360.0 Then
			Msg = "Your muscles are swelling. You feel more powerful than ever."
			MsgTimer = 70 * 5
		ElseIf PrevI427Timer - FPSfactor < 70 * 390.0 And I_427\Timer >= 70 * 390.0 Then
			Msg = "You can't feel your legs. But you don't need legs anymore."
			MsgTimer = 70 * 5
		EndIf
		I_427\Timer = I_427\Timer + FPSfactor
		If I_427\Sound[0] = 0 Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If I_427\Sound[1] = 0 Then
			I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		EndIf
		For i = 0 To 1
			If ChannelPlaying(I_427\SoundCHN[i]) = False Then I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
		Next
		If Rnd(200) < 2.0 Then
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(Collider) + Rnd(-0.05, 0.05), EntityY(Collider) - 0.05, EntityZ(Collider) + Rnd(-0.05, 0.05))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(20, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rand(360.0), 0.0)
			de\Size = Rnd(0.03, 0.08) * 2.0
			EntityAlpha(de\OBJ, 1.0)
			ScaleSprite(de\OBJ, de\Size, de\Size)
			TempCHN = PlaySound_Strict(DripSFX(Rand(0, 2)))
			ChannelVolume(TempCHN, Rnd(0.0, 0.8) * SFXVolume)
			ChannelPitch(TempCHN, Rand(20000, 30000))
			FreeEntity(Pvt)
			BlurTimer = 800
		EndIf
		If I_427\Timer >= 70 * 420.0 Then
			Kill()
			DeathMsg = Chr(34) + "Requesting support from MTF Nu-7. We need more firepower to take this thing down." + Chr(34)
		ElseIf I_427\Timer >= 70 * 390.0 Then
			Crouch = True
		EndIf
	EndIf
End Function

Function UpdateMTF%()
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
				If Abs(EntityZ(entrance\OBJ) - EntityZ(Collider)) < 30.0 Then
					If PlayerInReachableRoom()
						PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
					EndIf
					
					MTFTimer = FPSfactor
					
					Local leader.NPCs
					
					For i = 0 To 2
						n.NPCs = CreateNPC(NPCtypeMTF, EntityX(entrance\OBJ) + 0.3 * (i - 1), 1.0, EntityZ(entrance\OBJ) + 8.0)
						
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
		If MTFTimer =< 70 * 120.0
			MTFTimer = MTFTimer + FPSfactor
		ElseIf MTFTimer > 70 * 120.0 And MTFTimer < 10000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			EndIf
			MTFTimer = 10000.0
		ElseIf MTFTimer >= 10000.0 And MTFTimer =< 10000.0 + (70 * 120.0)
			MTFTimer = MTFTimer + FPSfactor
		ElseIf MTFTimer > 10000.0 + (70 * 120.0) And MTFTimer < 20000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			EndIf
			MTFTimer = 20000.0
		ElseIf MTFTimer >= 20000.0 And MTFTimer =< 20000.0 + (70 * 60.0)
			MTFTimer = MTFTimer + FPSfactor
		ElseIf MTFTimer > 20000.0 + (70 * 60.0) And MTFTimer < 25000.0
			If PlayerInReachableRoom()
				; ~ If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount - 1
					If Inventory(i) <> Null Then
						If (Left(Inventory(i)\ItemTempLate\Name, 4) = "SCP-") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-035") And (Left(Inventory(i)\ItemTemplate\Name, 7) <> "SCP-093")
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
		ElseIf MTFTimer >= 25000.0 And MTFTimer =< 25000.0 + (70 * 60.0)
			MTFTimer = MTFTimer + FPSfactor
		ElseIf MTFTimer > 25000.0 + (70 * 60.0) And MTFTimer < 30000.0
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			EndIf
			MTFTimer = 30000.0
		EndIf
	EndIf
End Function

Function Update008()
	Local Temp#, i%, r.Rooms
	Local TeleportForInfect% = True
	
	If PlayerRoom\RoomTemplate\Name = "room860"
		For e.Events = Each Events
			If e\EventName = "room860"
				If e\EventState = 1.0
					TeleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "gatea"
		TeleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "exit1" And EntityY(Collider) > 1040.0 * RoomScale
		TeleportForInfect = False
	EndIf
	
	If Infect > 0.0 Then
		ShowEntity(InfectOverlay)
		If Infect < 93.0 Then
			Temp = Infect
			If (Not I_427\Using And I_427\Timer < 70 * 360.0) Then
				Infect = Min(Infect + FPSfactor * 0.002, 100.0)
			EndIf
			
			BlurTimer = Max(Infect * 3.0 * (2.0 - CrouchState), BlurTimer)
			
			HeartBeatRate = Max(HeartBeatRate, 100.0)
			HeartBeatVolume = Max(HeartBeatVolume, Infect / 120.0)
			
			EntityAlpha(InfectOverlay, Min(((Infect * 0.2) ^ 2.0) / 1000.0, 0.5) * (Sin(MilliSecs2() / 8.0) + 2.0))
			
			For i = 0 To 6
				If Infect > i * 15.0 + 10.0 And Temp =< i * 15.0 + 10.0 Then
					PlaySound_Strict(LoadTempSound("SFX\SCP\008\Voices" + i + ".ogg"))
				EndIf
			Next
			
			If Infect > 20.0 And Temp =< 20.0 Then
				Msg = "You feel kinda feverish."
				MsgTimer = 70 * 6.0
			ElseIf Infect > 40.0 And Temp =< 40.0
				Msg = "You feel nauseated."
				MsgTimer = 70 * 6.0
			ElseIf Infect > 60.0 And Temp =< 60.0
				Msg = "The nausea's getting worse."
				MsgTimer = 70 * 6.0
			ElseIf Infect > 80.0 And Temp =< 80.0
				Msg = "You feel very faint."
				MsgTimer = 70 * 6.0
			ElseIf Infect >= 91.5
				BlinkTimer = Max(Min((-10.0) * (Infect - 91.5), BlinkTimer), -10.0)
				IsZombie = True
				UnableToMove = True
				If Infect >= 92.7 And Temp < 92.7 Then
					If TeleportForInfect
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name = "008" Then
								PositionEntity(Collider, EntityX(r\Objects[7], True), EntityY(r\Objects[7], True), EntityZ(r\Objects[7], True), True)
								ResetEntity(Collider)
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6], True),EntityY(r\Objects[6], True) + 0.2, EntityZ(r\Objects[6], True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundCHN = PlaySound_Strict(r\NPC[0]\Sound)
								ChangeNPCTextureID(e\room\NPC[0], 12)
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
			Temp = Infect
			Infect = Min(Infect + FPSfactor * 0.004, 100.0)
			
			If TeleportForInfect
				If Infect < 94.7 Then
					EntityAlpha(InfectOverlay, 0.5 * (Sin(MilliSecs2() / 8.0) + 2.0))
					BlurTimer = 900.0
					
					If Infect > 94.5 Then BlinkTimer = Max(Min((-50.0) * (Infect - 94.5), BlinkTimer), -10.0)
					PointEntity(Collider, PlayerRoom\NPC[0]\Collider)
					PointEntity(PlayerRoom\NPC[0]\Collider, Collider)
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider, EntityRoll(Camera))
					ForceMove = 0.75
					Injuries = 2.5
					Bloodloss = 0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 357.0, 381.0, 0.3)
				ElseIf Infect < 98.5
					EntityAlpha(InfectOverlay, 0.5 * (Sin(MilliSecs2() / 5.0) + 2.0))
					BlurTimer = 950
					
					ForceMove = 0.0
					UnableToMove = True
					PointEntity(Camera, PlayerRoom\NPC[0]\Collider)
					
					If Temp < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundCHN = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						DeathMsg = "Subject D-9341 found ingesting Dr. [DATA REDACTED] at Sector [DATA REDACTED]. Subject was immediately terminated by Nine-Tailed Fox and sent for autopsy. "
						DeathMsg = DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
						
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544.0 * RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider), 90.0, Rnd(360.0), 0.0)
						de\Size = 0.8
						ScaleSprite(de\OBJ, de\Size, de\Size)
					ElseIf Infect > 96
						BlinkTimer = Max(Min((-10.0) * (Infect - 96.0), BlinkTimer), -10.0)
					Else
						KillTimer = Max(-350.0, KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2 = 0.0 Then
						Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 13.0, 19.0, 0.3, False)
						If AnimTime(PlayerRoom\NPC[0]\OBJ) >= 19.0 Then PlayerRoom\NPC[0]\State2 = 1.0
					Else
						Animate2(PlayerRoom\NPC[0]\OBJ, AnimTime(PlayerRoom\NPC[0]\OBJ), 19.0, 13.0, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\OBJ) =< 13.0 Then PlayerRoom\NPC[0]\State2 = 0.0
					EndIf
					
					If ParticleAmount > 0
						If Rand(50) = 1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider), EntityY(PlayerRoom\NPC[0]\Collider), EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05, 0.1), 0.15, 200)
							p\Speed = 0.01
							p\SizeChange = 0.01
							p\A = 0.5
							p\Achange = -0.01
							RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
						EndIf
					EndIf
					
					PositionEntity(Head, EntityX(PlayerRoom\NPC[0]\Collider, True), EntityY(PlayerRoom\NPC[0]\Collider, True) + 0.65, EntityZ(PlayerRoom\NPC[0]\Collider, True), True)
					RotateEntity(Head, (1.0 + Sin(MilliSecs2() / 5.0)) * 15.0, PlayerRoom\Angle - 180.0, 0.0, True)
					MoveEntity(Head, 0.0, 0.0, -0.4)
					TurnEntity(Head, 80.0 + (Sin(MilliSecs2() / 5.0)) * 30.0, (Sin(MilliSecs2() / 5.0)) * 40.0, 0.0)
				EndIf
			Else
				Kill()
				BlinkTimer = Max(Min((-10.0) * (Infect - 96.0), BlinkTimer), -10.0)
				If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
					DeathMsg = "The whereabouts of SCP-1499 are still unknown, but a recon team has been dispatched to investigate reports of a violent attack to a church in the Russian town of [DATA REDACTED]."
				ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Or PlayerRoom\RoomTemplate\Name = "exit1" Then
					DeathMsg = "Subject D-9341 found wandering around Gate "
					If PlayerRoom\RoomTemplate\Name = "gatea" Then
						DeathMsg = DeathMsg + "A"
					Else
						DeathMsg = DeathMsg + "B"
					EndIf
					DeathMsg = DeathMsg + ". Subject was immediately terminated by Nine-Tailed Fox and sent for autopsy. "
					DeathMsg = DeathMsg + "SCP-008 infection was confirmed, after which the body was incinerated."
				Else
					DeathMsg = ""
				EndIf
			EndIf
		EndIf
	Else
		HideEntity(InfectOverlay)
	EndIf
End Function

Include "Source Code\Math_System.bb"

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
	
	EntityTexture(d\OBJ, DecalTextures(ID))
	EntityFX(d\OBJ, 0)
	SpriteViewMode(d\OBJ, 2)
	PositionEntity(d\OBJ, x, y, z)
	RotateEntity(d\OBJ, Pitch, Yaw, Roll)
	
	d\ID = ID
	
	If DecalTextures(ID) = 0 Or d\OBJ = 0 Then Return(Null)
	
	Return(d)
End Function

Function UpdateDecals()
	Local d.Decals
	
	For d.Decals = Each Decals
		If d\SizeChange <> 0.0 Then
			d\Size = d\Size + d\SizeChange * FPSfactor
			ScaleSprite(d\OBJ, d\Size, d\Size)
			
			Select d\ID
				Case 0
					;[Block]
					If d\Timer =< 0.0 Then
						Local Angle# = Rnd(360.0)
						Local Temp# = Rnd(d\Size)
						Local d2.Decals = CreateDecal(1, EntityX(d\OBJ) + Cos(Angle) * Temp, EntityY(d\OBJ) - 0.0005, EntityZ(d\OBJ) + Sin(Angle) * Temp, EntityPitch(d\OBJ), Rnd(360.0), EntityRoll(d\OBJ))
						
						d2\Size = Rnd(0.1, 0.5) : ScaleSprite(d2\OBJ, d2\Size, d2\Size)
						PlaySound2(DecaySFX(Rand(1, 3)), Camera, d2\OBJ, 10.0, Rnd(0.1, 0.5))
						d\Timer = Rnd(50.0, 100.0)
					Else
						d\Timer = d\Timer - FPSfactor
					End If
					;[End Block]
			End Select
			
			If d\Size >= d\MaxSize Then d\SizeChange = 0.0 : d\Size = d\MaxSize
		End If
		
		If d\AlphaChange <> 0.0 Then
			d\Alpha = Min(d\Alpha + FPSfactor * d\AlphaChange, 1.0)
			EntityAlpha(d\OBJ, d\Alpha)
		End If
		
		If d\LifeTime > 0.0 Then
			d\LifeTime = Max(d\LifeTime - FPSfactor, 5.0)
		EndIf
		
		If d\Size =< 0.0 Or d\Alpha =< 0.0 Or d\LifeTime = 5.0  Then
			FreeEntity(d\OBJ)
			
			Delete(d)
		End If
	Next
End Function

; ~ Create a collision box For a mesh entity taking into account entity scale
; (~ Won't work in non-uniform scaled space)
Function MakeCollBox(Mesh%)
	Local sX# = EntityScaleX(Mesh, 1)
	Local sY# = Max(EntityScaleY(Mesh, 1), 0.001)
	Local sZ# = EntityScaleZ(Mesh, 1)
	
	GetMeshExtents(Mesh)
	EntityBox(Mesh, Mesh_MinX * sX, Mesh_MinY * sY, Mesh_MinZ * sZ, Mesh_MagX * sX, Mesh_MagY * sY, Mesh_MagZ * sZ)
End Function

; ~ Find mesh extents
Function GetMeshExtents(Mesh%)
	Local s%, Surf%, Surfs%, v%, Verts%, x#, y#, z#
	Local MinX# = INFINITY
	Local MinY# = INFINITY
	Local MinZ# = INFINITY
	Local MaxX# = -INFINITY
	Local MaxY# = -INFINITY
	Local MaxZ# = -INFINITY
	
	Surfs = CountSurfaces(Mesh)
	
	For s = 1 To Surfs
		Surf = GetSurface(Mesh, s)
		Verts = CountVertices(Surf)
		For v = 0 To Verts - 1
			x = VertexX(Surf, v)
			y = VertexY(Surf, v)
			z = VertexZ(Surf, v)
			
			If (x < MinX) Then MinX = x
			If (x > MaxX) Then MaxX = x
			If (y < MinY) Then MinY = y
			If (y > MaxY) Then MaxY = y
			If (z < MinZ) Then MinZ = z
			If (z > MaxZ) Then MaxZ = z
		Next
	Next
	
	Mesh_MinX = MinX
	Mesh_MinY = MinY
	Mesh_MinZ = MinZ
	Mesh_MaxX = MaxX
	Mesh_MaxY = MaxY
	Mesh_MaxZ = MaxZ
	Mesh_MagX = MaxX - MinX
	Mesh_MagY = MaxY - MinY
	Mesh_MagZ = MaxZ - MinZ
End Function

Function EntityScaleX#(Entity%, Globl% = False)
	If Globl Then 
		TFormVector(1.0, 0.0, 0.0, Entity, 0) 
	Else 
		TFormVector(1.0, 0.0, 0.0, Entity, GetParent(Entity))
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ()))
End Function 

Function EntityScaleY#(Entity%, Globl% = False)
	If Globl Then 
		TFormVector(0.0, 1.0, 0.0, Entity, 0)
	Else 
		TFormVector(0.0, 1.0, 0.0, Entity, GetParent(Entity))
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ()))
End Function 

Function EntityScaleZ#(Entity%, Globl% = False)
	If Globl Then 
		TFormVector(0.0, 0.0, 1.0, Entity, 0)
	Else 
		TFormVector(0.0, 0.0, 1.0, Entity, GetParent(Entity))
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ()))
End Function 

Function Graphics3DExt%(Width%, Height%, Depth% = 32, Mode% = 2)
	Graphics3D(Width, Height, Depth, Mode)
	InitFastResize()
	AntiAlias(Opt_AntiAlias)
End Function

Function ResizeImage2(Image%, Width%, Height%)
    Img% = CreateImage(Width, Height)
	
	OldWidth% = ImageWidth(Image)
	OldHeight% = ImageHeight(Image)
	CopyRect(0, 0, OldWidth, OldHeight, 1024 - OldWidth / 2, 1024 - OldHeight / 2, ImageBuffer(Image), TextureBuffer(Fresize_Texture))
	SetBuffer(BackBuffer())
	ScaleRender(0, 0, 2048.0 / Float(RealGraphicWidth) * Float(Width) / Float(OldWidth), 2048.0 / Float(RealGraphicWidth) * Float(Height) / Float(OldHeight))
	; ~ Might want to replace Float(GraphicWidth) with Max(GraphicWidth, GraphicHeight) if portrait sizes cause issues
	; ~ Everyone uses landscape so it's probably a non-issue
	CopyRect(RealGraphicWidth / 2 - Width / 2, RealGraphicHeight / 2 - Height / 2, Width, Height, 0, 0, BackBuffer(), ImageBuffer(Img))
	FreeImage(Image)
	Return(Img)
End Function

Function RenderWorld2()
	Local i%, Dist#
	
	CameraProjMode(Ark_Blur_Cam, 0)
	CameraProjMode(Camera, 1)
	
	If WearingNightVision > 0 And WearingNightVision < 3 Then
		AmbientLight(Min(Brightness * 2.0, 255.0), Min(Brightness * 2.0, 255.0), Min(Brightness * 2.0, 255.0))
	ElseIf WearingNightVision = 3
		AmbientLight(255.0, 255.0, 255.0)
	ElseIf PlayerRoom <> Null
		If (PlayerRoom\RoomTemplate\Name <> "173") And (PlayerRoom\RoomTemplate\Name <> "exit1") And (PlayerRoom\RoomTemplate\Name <> "gatea") Then
			AmbientLight(Brightness, Brightness, Brightness)
		EndIf
	EndIf
	
	IsNVGBlinking = False
	HideEntity(NVBlink)
	
	CameraViewport(Camera, 0, 0, GraphicWidth, GraphicHeight)
	
	Local HasBattery% = 2
	Local Power% = 0
	
	If (WearingNightVision = 1) Or (WearingNightVision = 2)
		For i = 0 To MaxItemAmount - 1
			If (Inventory(i) <> Null) Then
				If (WearingNightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernv") Then
					Inventory(i)\State = Inventory(i)\State - (FPSfactor * (0.02 * WearingNightVision))
					Power = Int(Inventory(i)\State)
					If Inventory(i)\State =< 0.0 Then ; ~ This NVG can't be used
						HasBattery = 0
						Msg = "The batteries in these night vision goggles died."
						BlinkTimer = -1.0
						MsgTimer = 350
						Exit
					ElseIf Inventory(i)\State =< 100.0 Then
						HasBattery = 1
					EndIf
				EndIf
			EndIf
		Next
		If (HasBattery) Then
			RenderWorld()
		EndIf
	Else
		RenderWorld()
	EndIf
	
	CurrTrisAmount = TrisRendered()

	If HasBattery = 0 And WearingNightVision <> 3
		IsNVGBlinking = True
		ShowEntity(NVBlink)
	EndIf
	
	If BlinkTimer < - 16.0 Or BlinkTimer > - 6.0
		If WearingNightVision = 2 And HasBattery <> 0 Then ; ~ Show a HUD
			NVTimer = NVTimer - FPSfactor
			If NVTimer =< 0.0 Then
				For np.NPCs = Each NPCs
					np\NVX = EntityX(np\Collider, True)
					np\NVY = EntityY(np\Collider, True)
					np\NVZ = EntityZ(np\Collider, True)
				Next
				IsNVGBlinking = True
				ShowEntity(NVBlink)
				If NVTimer =< -10.0 Then
					NVTimer = 600.0
				EndIf
			EndIf
			
			Color(255, 255, 255)
			
			AASetFont(Font3)
			
			Local PlusY% = 0
			
			If HasBattery = 1 Then PlusY% = 40
			
			AAText(GraphicWidth / 2, (20 + PlusY) * MenuScale, "REFRESHING DATA IN", True, False)
			
			AAText(GraphicWidth / 2, (60 + PlusY) * MenuScale, Max(f2s(NVTimer / 60.0, 1), 0.0), True, False)
			AAText(GraphicWidth / 2, (100 + PlusY) * MenuScale, "SECONDS", True, False)
			
			Temp% = CreatePivot() : Temp2% = CreatePivot()
			PositionEntity(Temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
			
			Color(255, 255, 255)
			
			For np.NPCs = Each NPCs
				If np\NVName <> "" And (Not np\HideFromNVG) Then ; ~ Don't waste your time if the string is empty
					PositionEntity(Temp2, np\NVX, np\NVY, np\NVZ)
					Dist = EntityDistance(Temp2, Collider)
					If Dist < 23.5 Then ; ~ Don't draw text if the NPC is too far away
						PointEntity(Temp, Temp2)
						YawValue# = WrapAngle(EntityYaw(Camera) - EntityYaw(Temp))
						xValue# = 0.0
						If YawValue > 90 And YawValue =< 180 Then
							xValue# = Sin(90) / 90 * YawValue
						Else If YawValue > 180 And YawValue < 270 Then
							xValue# = Sin(270) / YawValue * 270
						Else
							xValue = Sin(YawValue)
						EndIf
						PitchValue# = WrapAngle(EntityPitch(Camera) - EntityPitch(Temp))
						yValue# = 0.0
						If PitchValue > 90 And PitchValue =< 180 Then
							yValue# = Sin(90) / 90 * PitchValue
						Else If PitchValue > 180 And PitchValue < 270 Then
							yValue# = Sin(270) / PitchValue * 270
						Else
							yValue# = Sin(PitchValue)
						EndIf
						
						If (Not IsNVGBlinking)
							AAText(GraphicWidth / 2 + xValue * (GraphicWidth / 2), GraphicHeight / 2 - yValue * (GraphicHeight / 2), np\NVName, True, True)
							AAText(GraphicWidth / 2 + xValue * (GraphicWidth / 2), GraphicHeight / 2 - yValue * (GraphicHeight / 2) + 30 * MenuScale, f2s(Dist, 1) + " m", True, True)
						EndIf
					EndIf
				EndIf
			Next
			FreeEntity(Temp) : FreeEntity(Temp2)
			
			Color(0, 0, 55)
			For k = 0 To 10
				Rect(45, GraphicHeight * 0.5 - (k * 20), 54, 10, True)
			Next
			Color(0, 0, 255)
			For l = 0 To Floor((Power + 50) * 0.01)
				Rect(45, GraphicHeight * 0.5 - (l * 20), 54, 10, True)
			Next
			DrawImage(NVGImages, 40, GraphicHeight * 0.5 + 30, 1)
			
			Color(255, 255, 255)
		ElseIf WearingNightVision = 1 And HasBattery <> 0
			Color(0, 55, 0)
			For k = 0 To 10
				Rect(45, GraphicHeight * 0.5 - (k * 20), 54, 10, True)
			Next
			Color(0, 255, 0)
			For l = 0 To Floor((Power + 50) * 0.01)
				Rect(45, GraphicHeight * 0.5 - (l * 20), 54, 10, True)
			Next
			DrawImage(NVGImages, 40, GraphicHeight * 0.5 + 30, 0)
		EndIf
	EndIf
	
	; ~ Render sprites
	CameraProjMode(Ark_Blur_Cam, 2)
	CameraProjMode(Camera, 0)
	RenderWorld()
	CameraProjMode(Ark_Blur_Cam, 0)
	
	If BlinkTimer < - 16.0 Or BlinkTimer > - 6.0
		If (WearingNightVision = 1 Or WearingNightVision = 2) And (HasBattery = 1) And ((MilliSecs2() Mod 800) < 400) Then
			Color(255, 0, 0)
			AASetFont(Font3)
			
			AAText(GraphicWidth / 2, 20 * MenuScale, "WARNING: LOW BATTERY", True, False)
			Color(255, 255, 255)
		EndIf
	EndIf
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
	WireFrame(WireframeState)
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
	Cls
	SetBuffer(BackBuffer())
	EntityTexture(SPR, Fresize_Texture, 0, 0)
	EntityTexture(SPR, Fresize_Texture2, 0, 1)
	
	HideEntity(Fresize_Cam)
End Function

Function UpdateLeave1499()
	Local r.Rooms, it.Items, r2.Rooms, i%
	Local r1499.Rooms
	
	If (Not Wearing1499) And PlayerRoom\RoomTemplate\Name = "dimension1499"
		For r.Rooms = Each Rooms
			If r = NTF_1499PrevRoom
				BlinkTimer = -1.0
				NTF_1499X = EntityX(Collider)
				NTF_1499Y = EntityY(Collider)
				NTF_1499Z = EntityZ(Collider)
				PositionEntity(Collider, NTF_1499PrevX, NTF_1499PrevY + 0.05, NTF_1499PrevZ)
				ResetEntity(Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(Collider) < -4600.0 * RoomScale
						For i = 0 To 2
							PlayerRoom\NPC[i]\State = 2.0
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True), EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True) + 0.2, EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2], True))
							ResetEntity(PlayerRoom\NPC[i]\Collider)
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1.0
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState - 3)
						Next
					EndIf
				ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
					CameraFogColor(Camera, 0.0, 0.0, 0.0)
					CameraClsColor(Camera, 0.0, 0.0, 0.0)
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499"
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\DistTimer = 0.0
					If it\ItemTemplate\TempName = "scp1499" Or it\ItemTemplate\TempName = "super1499"
						If EntityY(it\Collider) >= EntityY(r1499\OBJ) - 5.0
							PositionEntity(it\Collider, NTF_1499PrevX, NTF_1499PrevY + (EntityY(it\Collider) - EntityY(r1499\OBJ)), NTF_1499PrevZ)
							ResetEntity(it\Collider)
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict(LoadTempSound("SFX\SCP\1499\Exit.ogg"))
				NTF_1499PrevX = 0.0
				NTF_1499PrevY = 0.0
				NTF_1499PrevZ = 0.0
				NTF_1499PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
End Function

Function CheckForPlayerInFacility()
	; ~ False (= 0): NPC is not in facility (mostly meant for "dimension1499")
	; ~ True (= 1): NPC is in facility
	; ~ 2: NPC is in tunnels (maintenance tunnels / 049 tunnels / 939 storage room, etc...)
	
	If EntityY(Collider) > 100.0
		Return(False)
	EndIf
	If EntityY(Collider) < -10.0
		Return(2)
	EndIf
	If EntityY(Collider) > 7.0 And EntityY(Collider) =< 100.0
		Return(2)
	EndIf
	Return(True)
End Function

Function IsItemGoodFor1162(itt.ItemTemplates)
	Local IN$ = itt\TempName
	
	Select itt\TempName
		Case "key1", "key2", "key3"
			;[Block]
			Return(True)
			;[End Block]
		Case "misc", "scp420j", "cigarette"
			;[Block]
			Return(True)
			;[End Block]
		Case "vest", "finevest","gasmask"
			;[Block]
			Return(True)
			;[End Block]
		Case "radio", "18vradio"
			;[Block]
			Return(True)
			;[End Block]
		Case "clipboard", "eyedrops", "nvgoggles"
			;[Block]
			Return(True)
			;[End Block]
		Case "drawing"
			;[Block]
			If itt\Img <> 0 Then FreeImage(itt\Img)	
			itt\Img = LoadImage_Strict("GFX\items\1048\1048_" + Rand(1, 20) + ".jpg") ; ~ Gives a random drawing.
			Return(True)
			;[End Block]
		Default
			;[Block]
			If itt\TempName <> "paper" Then
				Return(False)
			Else If Instr(itt\Name, "Leaflet")
				Return(False)
			Else
				; ~ If the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				; ~ (Because those are items created recently, which D-9341 has most likely never seen)
				Return(((Not Instr(itt\Name, "Note")) And (Not Instr(itt\Name, "Log"))))
			EndIf
			;[End Block]
	End Select
End Function

Function ControlSoundVolume()
	Local snd.Sound, i%
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			ChannelVolume(snd\Channels[i], SFXVolume)
		Next
	Next
End Function

Function UpdateDeafPlayer()
	If DeafTimer > 0
		DeafTimer = DeafTimer - FPSfactor
		SFXVolume = 0.0
		If SFXVolume > 0.0
			ControlSoundVolume()
		EndIf
	Else
		DeafTimer = 0
		SFXVolume = PrevSFXVolume
		If DeafPlayer Then ControlSoundVolume()
		DeafPlayer = False
	EndIf
End Function

Function CheckTriggers$()
	Local i%, sX#, sY#, sZ#
	Local Inside% = -1
	
	If PlayerRoom\TriggerBoxAmount = 0
		Return("")
	Else
		For i = 0 To PlayerRoom\TriggerBoxAmount - 1
			EntityAlpha(PlayerRoom\TriggerBox[i], 1.0)
			sX = EntityScaleX(PlayerRoom\TriggerBox[i], 1)
			sY = Max(EntityScaleY(PlayerRoom\TriggerBox[i], 1), 0.001)
			sZ = EntityScaleZ(PlayerRoom\TriggerBox[i], 1)
			GetMeshExtents(PlayerRoom\TriggerBox[i])
			If DebugHUD
				EntityColor(PlayerRoom\TriggerBox[i], 255, 255, 0)
				EntityAlpha(PlayerRoom\TriggerBox[i], 0.2)
			Else
				EntityColor(PlayerRoom\TriggerBox[i], 255, 255, 255)
				EntityAlpha(PlayerRoom\TriggerBox[i], 0.0)
 			EndIf
			If EntityX(Collider) > ((sX * Mesh_MinX) + PlayerRoom\x) And EntityX(Collider) < ((sX * Mesh_MaxX) + PlayerRoom\x)
				If EntityY(Collider) > ((sY * Mesh_MinY) + PlayerRoom\y) And EntityY(Collider) < ((sY * Mesh_MaxY) + PlayerRoom\y)
					If EntityZ(Collider) > ((sZ * Mesh_MinZ) + PlayerRoom\z) And EntityZ(Collider) < ((sZ * Mesh_MaxZ) + PlayerRoom\z)
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

Function CatchErrors(Location$)
	Local ErrStr$ = ErrorLog()
	Local ErrF%
	
	If Len(ErrStr) > 0 Then
		If FileType(ErrorFile) = 0 Then
			ErrF = WriteFile(ErrorFile)
			WriteLine(ErrF, "An error occured in SCP - Containment Breach!")
			WriteLine(ErrF, "Mod Version: v" + VersionNumber)
			WriteLine(ErrF, "Date and time: " + CurrentDate() + " at " + CurrentTime())
			WriteLine(ErrF, "Total video memory (MB): " + TotalVidMem() / 1024 / 1024)
			WriteLine(ErrF, "Available video memory (MB): " + AvailVidMem() / 1024 / 1024)
			GlobalMemoryStatus(m.MEMORYSTATUS)
			WriteLine(ErrF, "Global memory status: " + (m\dwAvailPhys / 1024 / 1024) + " MB/" + (m\dwTotalPhys / 1024 / 1024) + " MB (" + (m\dwAvailPhys / 1024) + " KB/" + (m\dwTotalPhys / 1024) + " KB)")
			WriteLine(ErrF, "Triangles rendered: " + CurrTrisAmount)
			WriteLine(ErrF, "Active textures: " + ActiveTextures())
			WriteLine(ErrF, "")
			WriteLine(ErrF, "Error(s):")
		Else
			Local CanWriteError% = True
			
			ErrF = OpenFile(ErrorFile)
			While (Not Eof(ErrF))
				Local l$ = ReadLine(ErrF)
				
				If Left(l, Len(Location)) = Location
					CanWriteError = False
					Exit
				EndIf
			Wend
			If CanWriteError
				SeekFile(ErrF, FileSize(ErrorFile))
			EndIf
		EndIf
		If CanWriteError
			WriteLine(ErrF, Location + " ***************")
			While Len(ErrStr) > 0
				WriteLine(ErrF, ErrStr)
				ErrStr = ErrorLog()
			Wend
		EndIf
		Msg = "Blitz3D Error! Details in " + Chr(34) + ErrorFile + Chr(34)
		MsgTimer = 20.0 * 70.0
		
		CloseFile(ErrF)
	EndIf
End Function

Function PlayAnnouncement(File$) ; ~ This function streams the announcement currently playing
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(File, SFXVolume, 0)
End Function

Function UpdateStreamSounds()
	Local e.Events
	
	If FPSfactor > 0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN, SFXVolume)
		EndIf
		For e = Each Events
			If e\SoundCHN <> 0 Then
				If e\SoundCHN_IsStream
					SetStreamVolume_Strict(e\SoundCHN, SFXVolume)
				EndIf
			EndIf
			
			If e\SoundCHN2 <> 0 Then
				If e\SoundCHN2_IsStream
					SetStreamVolume_Strict(e\SoundCHN2, SFXVolume)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
			If PlayerRoom\RoomTemplate\Name <> "dimension1499" Then
				For e = Each Events
					If e\SoundCHN <> 0 And e\SoundCHN_IsStream Then
						StopStream_Strict(e\SoundCHN)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
					
					If e\SoundCHN2 <> 0 And e\SoundCHN2_IsStream Then
						StopStream_Strict(e\SoundCHN2)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
				Next
			EndIf
		EndIf
	EndIf
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
	
	Local Cam% = CreateCamera() 
	
	CameraClsMode(Cam, 0, 1)
	
	Local Quad% = CreateQuad()
	Local Texture% = CreateTexture(2048, 2048, 256 Or 16 Or 32)
	
	EntityTexture(Quad, Texture)
	EntityFX(Quad, 1)
	CameraRange(Cam, 0.01, 100)
	TranslateEntity(Cam, 1.0 / 2048.0, -1.0 / 2048.0, -1.0)
	EntityParent(Quad, Cam, 1)
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(RealGraphicWidth) / Float(RealGraphicHeight)
	
	If Ratio > 1.76 And Ratio < 1.78
		ScaledGraphicHeight = RealGraphicHeight
	Else
		ScaledGraphicHeight = Float(RealGraphicWidth) / (16.0 / 9.0)
	EndIf
	
	Local MovieFile$ = "GFX\menu\startup_Undertow"
	
	BlitzMovie_Open(MovieFile + ".avi") ; ~ Get movie size
	
	Local MovieW% = BlitzMovie_GetWidth()
	Local MovieH% = BlitzMovie_GetHeight()
	
	BlitzMovie_Close()
	
	Local Image% = CreateImage(MovieW, MovieH)
	Local SplashScreenVideo% = BlitzMovie_OpenDecodeToImage(MovieFile + ".avi", Image, False)
	
	SplashScreenVideo = BlitzMovie_Play()
	
	Local SplashScreenAudio% = StreamSound_Strict(MovieFile + ".ogg", SFXVolume, 0)
	
	Repeat
		Cls
		ProjectImage(Image, RealGraphicWidth, ScaledGraphicHeight, Quad, Texture)
		Flip
	Until (GetKey() Or (Not IsStreamPlaying_Strict(SplashScreenAudio)))
	StopStream_Strict(SplashScreenAudio)
	BlitzMovie_Stop()
	BlitzMovie_Close()
	FreeImage(Image)
	
	Cls
	Flip
	
	MovieFile = "GFX\menu\startup_TSS"
	BlitzMovie_Open(MovieFile + ".avi") ; ~ Get movie size
	MovieW = BlitzMovie_GetWidth()
	MovieH = BlitzMovie_GetHeight()
	BlitzMovie_Close()
	Image = CreateImage(MovieW, MovieH)
	SplashScreenVideo = BlitzMovie_OpenDecodeToImage(MovieFile + ".avi", Image, False)
	SplashScreenVideo = BlitzMovie_Play()
	SplashScreenAudio = StreamSound_Strict(MovieFile + ".ogg", SFXVolume, 0)
	Repeat
		Cls
		ProjectImage(Image, RealGraphicWidth, ScaledGraphicHeight, Quad, Texture)
		Flip
	Until(GetKey() Or (Not IsStreamPlaying_Strict(SplashScreenAudio)))
	StopStream_Strict(SplashScreenAudio)
	BlitzMovie_Stop()
	BlitzMovie_Close()
	
	FreeTexture(Texture)
	FreeEntity(Quad)
	FreeEntity(Cam)
	FreeImage(Image)
	
	Cls
	Flip
End Function

Function ProjectImage(Img, Width#, Height#, Quad%, Texture%)
	Local Img_W# = ImageWidth(Img)
	Local Img_H# = ImageHeight(Img)
	
	If Img_W > 2048.0 Then Img_W = 2048.0
	If Img_H > 2048.0 Then Img_H = 2048.0
	If Img_W < 1.0 Then Img_W = 1.0
	If Img_H < 1.0 Then Img_H = 1.0
	
	If Width > 2048.0 Then Width = 2048.0
	If Height > 2048.0 Then Height = 2048.0
	If Width < 1.0 Then Width = 1.0
	If Height < 1.0 Then Height = 1.0
	
	Local Width_Rel# = Width / Img_W
	Local Height_Rel# = Height / Img_H
	Local Graphic_Rel# = 2048.0 / Float(RealGraphicWidth)
	Local DstX# = 1024.0 - (Img_W / 2.0)
	Local DstY# = 1024.0 - (Img_H / 2.0)
	
	CopyRect(0, 0, Img_W, Img_H, DstX, DstY, ImageBuffer(Img), TextureBuffer(Texture))
	ScaleEntity(Quad, Width_Rel * Graphic_Rel, Height_Rel * Graphic_Rel, 0.0001)
	RenderWorld()
End Function

Function CreateQuad()
	Mesh = CreateMesh()
	Surf = CreateSurface(Mesh)
	v0 = AddVertex(Surf, -1.0, 1.0, 0.0, 0.0, 0.0)
	v1 = AddVertex(Surf, 1.0, 1.0, 0.0, 1.0, 0.0)
	v2 = AddVertex(Surf, 1.0, -1.0, 0.0, 1.0, 1.0)
	v3 = AddVertex(Surf, -1.0, -1.0, 0.0, 0.0, 1.0)
	AddTriangle(Surf, v0, v1, v2)
	AddTriangle(Surf, v0, v2, v3)
	UpdateNormals(Mesh)
	Return(Mesh)
End Function

Function CanUseItem(CanUseWithHazmat%, CanUseWithGasMask%, CanUseWithEyewear%)
	If (CanUseWithHazmat = False And WearingHazmat) Then
		Msg = "You can't use that item while wearing a hazmat suit."
		MsgTimer = 70.0 * 5.0
		Return(False)
	Else If (CanUseWithGasMask = False And (WearingGasMask Or Wearing1499))
		Msg = "You can't use that item while wearing a gas mask."
		MsgTimer = 70.0 * 5.0
		Return(False)
	Else If (CanUseWithEyewear = False And (WearingNightVision))
		Msg = "You can't use that item while wearing headgear."
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

Function Update096ElevatorEvent#(e.Events, EventState#, d.Doors, ElevatorOBJ%)
	Local PrevEventState# = EventState
	
	If EventState < 0.0 Then
		EventState = 0.0
		PrevEventState = 0.0
	EndIf
	
	If d\OpenState = 0.0 And d\Open = False Then
		If Abs(EntityX(Collider) - EntityX(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
			If Abs(EntityZ(Collider) - EntityZ(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
				If Abs(EntityY(Collider) - EntityY(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
					d\Locked = True
					If EventState = 0 Then
						TeleportEntity(Curr096\Collider, EntityX(d\FrameOBJ), EntityY(d\FrameOBJ) + 1.0, EntityZ(d\FrameOBJ), Curr096\CollRadius)
						PointEntity(Curr096\Collider, ElevatorOBJ)
						RotateEntity(Curr096\Collider, 0.0, EntityYaw(Curr096\Collider), 0.0)
						MoveEntity(Curr096\Collider, 0.0, 0.0, -0.5)
						ResetEntity(Curr096\Collider)
						Curr096\State = 6.0
						SetNPCFrame(Curr096, 0.0)
						e\Sound = LoadSound_Strict("SFX\SCP\096\ElevatorSlam.ogg")
						EventState = EventState + FPSfactor * 1.4
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	If EventState > 0.0 Then
		If PrevEventState = 0.0 Then
			e\SoundCHN = PlaySound_Strict(e\Sound)
		EndIf
		
		If EventState > 70.0 * 1.9 And EventState < 70.0 * 2 + FPSfactor
			CameraShake = 7.0
		ElseIf EventState > 70 * 4.2 And EventState < 70.0 * 4.25 + FPSfactor
			CameraShake = 1.0
		ElseIf EventState > 70 * 5.9 And EventState < 70.0 * 5.95 + FPSfactor
			CameraShake = 1.0
		ElseIf EventState > 70 * 7.25 And EventState < 70.0 * 7.3 + FPSfactor
			CameraShake = 1.0
			d\FastOpen = True
			d\Open = True
			Curr096\State = 4.0
			Curr096\LastSeen = 1.0
		ElseIf EventState > 70 * 8.1 And EventState < 70.0 * 8.15 + FPSfactor
			CameraShake = 1.0
		EndIf
		
		If EventState =< 70 * 8.1 Then
			d\OpenState = Min(d\OpenState, 20.0)
		EndIf
		EventState = EventState + FPSfactor * 1.4
	EndIf
	Return(EventState)
End Function

Function RotateEntity90DegreeAngles(Entity%)
	Local Angle# = WrapAngle(Entity)
	
	If Angle < 45.0 Then
		Return(0.0)
	ElseIf Angle >= 45.0 And Angle < 135.0 Then
		Return(90.0)
	ElseIf Angle >= 135.0 And Angle < 225.0 Then
		Return(180.0)
	Else
		Return(270.0)
	EndIf
End Function
;~IDEal Editor Parameters:
;~B#EEB#1216#1A11
;~C#Blitz3D