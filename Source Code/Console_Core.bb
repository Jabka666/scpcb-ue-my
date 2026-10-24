Global WireFrameState%

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#, ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR%, ConsoleG%, ConsoleB%
Global ConsoleInBox% = False, ConsoleInBar% = False

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

Function CreateConsoleMultiMsg%(Txt$, R% = -1, G% = -1, B% = -1, IsCommand% = False)
	While Instr(Txt, "\n") <> 0 
		CreateConsoleMsg(Left(Txt, Instr(Txt, "\n") - 1), R, G, B, IsCommand)
		Txt = Right(Txt, Len(Txt) - Instr(Txt, "\n") - 1)
	Wend
	CreateConsoleMsg(Txt, R, G, B, IsCommand)
End Function

Type Cheats
	Field GodMode%
	Field NoBlink%
	Field NoTarget%
	Field NoClip%, NoClipSpeed#
	Field InfiniteStamina%
	Field SuperMan%, SuperManTimer#
	Field DebugHUD%
End Type

Global chs.Cheats

Function ClearCheats%()
	chs\GodMode = False
	chs\NoBlink = False
	chs\NoTarget = False
	chs\NoClip = False
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
	chs\InfiniteStamina = True
	chs\SuperMan = False
	chs\SuperManTimer = 0.0
	chs\DebugHUD = Rand(3)
End Function

Function ResetNegativeStats%(Revive% = False)
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
	
	If me\BlinkEffect > 1.0
		me\BlinkEffect = 1.0
		me\BlinkEffectTimer = 0.0
	EndIf
	me\EyeStuck = 0.0
	me\EyeIrritation = 0.0
	me\Suffocation = 0.0
	
	If me\StaminaEffect > 1.0
		me\StaminaEffect = 1.0
		me\StaminaEffectTimer = 0.0
	EndIf
	me\Stamina = me\StaminaMax
	
	For i = 0 To 6
		I_1025\State[i] = 0.0
	Next
	If I_1025\FineState[0] > 0.0
		; ~ Drop two latest items
		For i = MaxItemAmount - 2 To MaxItemAmount - 1
			If Inventory(i) <> Null Then DropItem(Inventory(i))
		Next
		MaxItemAmount = MaxItemAmount - 2
		I_1025\FineState[0] = 0.0
	EndIf
	For i = 1 To 5
		I_1025\FineState[i] = 0.0
	Next
	
	If I_427\Timer >= 70.0 * 360.0 Then I_427\Timer = 0.0
	I_008\Timer = 0.0
	I_009\Timer = 0.0
	I_409\Timer = 0.0
	I_1048A\EarGrowTimer = 0.0
	I_966\HasInsomnia = 0.0
	I_966\InsomniaEffectTimer = 0.0
	
	If Revive
		ClearCheats()
		
		; ~ If death by SCP-173 or SCP-106, enable GodMode, prevent instant death again -- Salvage
		If n_I\Curr173\Idle = 1
			CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-173"))
			chs\GodMode = True
			n_I\Curr173\Idle = 0
		EndIf
		If EntityDistanceSquared(me\Collider, n_I\Curr106\Collider) < 4.0
			CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-106"))
			chs\GodMode = True
		EndIf
		If n_I\Curr049 <> Null
			If n_I\Curr049\State <> 66.0 Then n_I\Curr049\State = 1.0 ; ~ Reset SCP-049
			If EntityDistanceSquared(me\Collider, n_I\Curr049\Collider) < 4.0
				CreateConsoleMsg(Format(GetLocalString("console", "revive.by"), "SCP-049"))
				chs\GodMode = True
			EndIf
		EndIf
		
		me\DropSpeed = -0.1
		me\HeadDropSpeed = 0.0
		me\Shake = 0.0
		me\CurrSpeed = 0.0
		
		me\FallTimer = 0.0
		MenuOpen = False
		
		HideEntity(me\Head)
		ShowEntity(me\Collider)
		
		me\Terminated = False
		me\KillAnim = 0
	EndIf
End Function

Function UpdateConsole%()
	CatchErrors("UpdateConsole()")
	
	If (Not opt\CanOpenConsole)
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen
		Local cm.ConsoleMsg, itt.ItemTemplates, rt.RoomTemplates
		Local CoordEx% = 15 * MenuScale
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		Local x% = 0
		Local y% = opt\GraphicHeight - 300 * MenuScale
		Local Width% = opt\GraphicWidth
		Local Height% = 270 * MenuScale
		Local HeightHalf% = Height / 2
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + CoordEx
		Next
		ScrollBarHeight = (Float(Height) / Float(ConsoleHeight)) * Height
		ScrollBarHeight = Min(ScrollBarHeight, Height)
		ConsoleHeight = Max(ConsoleHeight, Height)
		
		ConsoleInBar = MouseOn(x + Width - (26 * MenuScale), y, 26 * MenuScale, Height)
		ConsoleInBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		If (Not mo\MouseDown1)
			ConsoleScrollDragging = False
		ElseIf ConsoleScrollDragging
			ConsoleScroll = ConsoleScroll + ((MousePosY - ConsoleMouseMem) * Height / ScrollBarHeight)
			ConsoleMouseMem = MousePosY
		EndIf
		
		If (Not ConsoleScrollDragging)
			If mo\MouseHit1
				If ConsoleInBox
					ConsoleScrollDragging = True
					ConsoleMouseMem = MousePosY
				ElseIf ConsoleInBar
					ConsoleScroll = ConsoleScroll + ((MousePosY - (y + Height)) * ConsoleHeight / Height + HeightHalf)
					ConsoleScroll = ConsoleScroll / 2
				EndIf
			EndIf
		EndIf
		
		ConsoleScroll = ConsoleScroll + (-MouseZSpeed()) * CoordEx
		
		Local ReissuePos%
		
		If KeyHit(200)
			ReissuePos = 0
			If ConsoleReissue = Null
				ConsoleReissue = First ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos - CoordEx
					ConsoleReissue = After ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos - CoordEx
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				ReissuePos = ReissuePos - CoordEx
				
				While True
					If ConsoleReissue = Null
						ConsoleReissue = First ConsoleMsg
						ReissuePos = 0
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos - CoordEx
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null
				ConsoleInput = ConsoleReissue\Txt
				CursorPos = Len(ConsoleInput)
				ConsoleScroll = ReissuePos + HeightHalf
			EndIf
		EndIf
		
		If KeyHit(208)
			ReissuePos = (-ConsoleHeight) + CoordEx
			If ConsoleReissue = Null
				ConsoleReissue = Last ConsoleMsg
				
				While ConsoleReissue <> Null
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + CoordEx
					ConsoleReissue = Before ConsoleReissue
				Wend
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm <> Null
					If cm = ConsoleReissue Then Exit
					ReissuePos = ReissuePos + CoordEx
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				ReissuePos = ReissuePos + CoordEx
				
				While True
					If ConsoleReissue = Null
						ConsoleReissue = Last ConsoleMsg
						ReissuePos = (-ConsoleHeight) + CoordEx
					EndIf
					
					If ConsoleReissue\IsCommand Then Exit
					ReissuePos = ReissuePos + CoordEx
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue <> Null
				ConsoleInput = ConsoleReissue\Txt
				CursorPos = Len(ConsoleInput)
				ConsoleScroll = ReissuePos + HeightHalf
			EndIf
		EndIf
		
		ConsoleScroll = Clamp(ConsoleScroll, (-ConsoleHeight) + Height, 0)
		
		SelectedInputBox = 19
		CursorPos = Len(ConsoleInput)
		
		Local OldConsoleInput$ = ConsoleInput
		
		ConsoleInput = UpdateMenuInputBox(x, y + Height, Width, 30 * MenuScale, ConsoleInput, Font_Console, 19)
		If OldConsoleInput <> ConsoleInput Then ConsoleReissue = Null
		ConsoleInput = Left(ConsoleInput, 100)
		
		If KeyHit(28) And ConsoleInput <> ""
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput, 255, 255, 0, True)
			ExecuteConsoleCommand(ConsoleInput)
			ConsoleInput = ""
		EndIf
		
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000 Then Delete(cm)
		Next
	EndIf
	
	CatchErrors("Uncaught: UpdateConsole()")
End Function

Function RenderConsole%()
	If (Not opt\CanOpenConsole) Then Return
	
	CatchErrors("RenderConsole()")
	
	If ConsoleOpen
		Local cm.ConsoleMsg
		Local x%, y%, Width%, Height%
		Local TempStr$
		Local CoordEx% = 26 * MenuScale
		
		SetFontEx(fo\FontID[Font_Console])
		
		x = 0
		y = opt\GraphicHeight - 300 * MenuScale
		Width = opt\GraphicWidth
		Height = 270 * MenuScale
		
		RenderFrame(x, y, Width, Height + (30 * MenuScale))
		
		Local ConsoleHeight% = 0
		Local ScrollBarHeight% = 0
		Local ConsoleHeightShift% = 15 * MenuScale
		
		For cm.ConsoleMsg = Each ConsoleMsg
			ConsoleHeight = ConsoleHeight + ConsoleHeightShift
		Next
		ScrollBarHeight = Min((Float(Height) / Float(ConsoleHeight)) * Height, Height)
		ConsoleHeight = Max(ConsoleHeight, Height)
		
		ConsoleInBar = MouseOn(x + Width - CoordEx, y, CoordEx, Height)
		
		Local Clr% = 50 + (20 * ConsoleInBar)
		
		Color(Clr, Clr, Clr)
		Rect(x + Width - CoordEx, y, CoordEx, Height)
		
		ConsoleInBox = MouseOn(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		Clr = 120 + ((80 * ConsoleInBox) + (55 * ConsoleScrollDragging))
		Color(Clr, Clr, Clr)
		Rect(x + Width - (23 * MenuScale), y + Height - ScrollBarHeight + (ConsoleScroll * ScrollBarHeight / Height), 20 * MenuScale, ScrollBarHeight)
		
		Color(255, 255, 255)
		
		Local TempY# = y + Height - 25 * MenuScale - ConsoleScroll
		Local Count% = 0
		
		For cm.ConsoleMsg = Each ConsoleMsg
			Count = Count + 1
			If Count > 1000
				Delete(cm)
			Else
				If TempY >= y And TempY < y + Height - (20 * MenuScale)
					If cm = ConsoleReissue
						Color(cm\R / 4, cm\G / 4, cm\B / 4)
						Rect(x + 3 * MenuScale, TempY - 2 * MenuScale, Width - 30 * MenuScale, 24 * MenuScale, True)
					EndIf
					Color(cm\R, cm\G, cm\B)
					If cm\IsCommand
						TempStr = "> " + cm\Txt
					Else
						TempStr = cm\Txt
					EndIf
					TextEx(x + (20 * MenuScale), TempY + 5 * MenuScale, TempStr)
				EndIf
				TempY = TempY - 15 * MenuScale
			EndIf
		Next
		Color(255, 255, 255)
		
		RenderMenuInputBoxes()
		
		RenderCursor()
	EndIf
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderConsole()")
End Function

Function ExecuteConsoleCommand%(ConsoleMessage$)
	Local ev.Events, e.Events, e2.Events, r.Rooms, it.Items, n.NPCs, snd.Sound, itt.ItemTemplates, rt.RoomTemplates
	Local Tex%, Tex2%, Temp%, i%
	Local Args$, StrTemp$, StrTemp2$, StrTemp3$, StrTemp4$
	
	If Instr(ConsoleInput, " ") <> 0
		StrTemp = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
	Else
		StrTemp = Lower(ConsoleInput)
	EndIf
	
	Select Lower(StrTemp)
		Case "help"
			;[Block]
			If Instr(ConsoleInput, " ") <> 0
				StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Else
				StrTemp = ""
			EndIf
			ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
			
			Select StrTemp
				Case "1", ""
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "help_1.1"))
					CreateConsoleMsg("******************************")
					CreateConsoleMsg("- teleport [room name]")
					CreateConsoleMsg("- roomlist")
					CreateConsoleMsg("- spawnitem [item name / ID]")
					CreateConsoleMsg("- spawndrink [drink name]")
					CreateConsoleMsg("- itemlist")
					CreateConsoleMsg("- ending [ID]")
					CreateConsoleMsg("- notarget")
					CreateConsoleMsg("- godmode")
					CreateConsoleMsg("- noclip")
					CreateConsoleMsg("- noclipspeed")
					CreateConsoleMsg("- infinitestamina")
					CreateConsoleMsg("- noblink")
					CreateConsoleMsg("- asd")
					CreateConsoleMsg("- revive")
					CreateConsoleMsg("- heal")
					CreateConsoleMsg("- money")
					CreateConsoleMsg("- debughud [category]")
					CreateConsoleMsg("- codes")
					CreateConsoleMsg("******************************")
					CreateConsoleMsg(GetLocalString("console", "help_1.2"))
					CreateConsoleMsg(GetLocalString("console", "help.command"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "2"
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "help_2.1"))
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
					CreateConsoleMsg("- disable066")
					CreateConsoleMsg("- enable066")
					CreateConsoleMsg("- disable096")
					CreateConsoleMsg("- enable096")
					CreateConsoleMsg("- disable513-1")
					CreateConsoleMsg("- enable513-1")
					CreateConsoleMsg("- disable966")
					CreateConsoleMsg("- enable966")
					CreateConsoleMsg("- doorcontrol")
					CreateConsoleMsg("- unlockcheckpoints")
					CreateConsoleMsg("- unlockexits")
					CreateConsoleMsg("- disablenuke")
					CreateConsoleMsg("- resetfunds")
					CreateConsoleMsg("******************************")
					CreateConsoleMsg(GetLocalString("console", "help_2.2"))
					CreateConsoleMsg(GetLocalString("console", "help.command"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "3"
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "help_3.1"))
					CreateConsoleMsg("******************************")
					CreateConsoleMsg("- camerafog [x]")
					CreateConsoleMsg("- npclist")
					CreateConsoleMsg("- spawn [npc type] [state]")
					CreateConsoleMsg("- injure [value]")
					CreateConsoleMsg("- infect [value]")
					CreateConsoleMsg("- crystal [value]")
					CreateConsoleMsg("- ice [value]")
					CreateConsoleMsg("- giveachievement [ID / All]")
					CreateConsoleMsg("- wireframe")
					CreateConsoleMsg("- halloween")
					CreateConsoleMsg("- newyear")
					CreateConsoleMsg("- sanic")
					CreateConsoleMsg("- weed")
					CreateConsoleMsg("******************************")
					CreateConsoleMsg(GetLocalString("console", "help.command"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "camerafog"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "camerafog"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.camerafog"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "noclip", "fly"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "noclip"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.noclip"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "noblink", "nb"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "noblink"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.noblink"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "godmode", "god"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "god"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.god"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "infinitestamina", "is"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "infinitestamina"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.is"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "notarget", "nt"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "notarget"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.nt"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "wireframe", "wf"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "wireframe"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.wf"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "spawnitem", "si", "giveitem"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "spawnitem"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.si"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "spawncup", "givecup", "spawndrink", "givedrink"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "spawndrink"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.sd"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "spawn", "s"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "spawn"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.s"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable106"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable106"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-106"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable106"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable106"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-106"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "reset106", "106retreat" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "106retreat"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.106r"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable173"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable173"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-173"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable173"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable173"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-173"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable049"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable049"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-049"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable049"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable049"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-049"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable066"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable066"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-066"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable066"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable066"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-066"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable096"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable096"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-096"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable096"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable096"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-096"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "reset096" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "reset096"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.r096"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "reset372" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "reset372"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.r372"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable513-1"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable513-1"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-513-1"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable513-1"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable513-1"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-513-1"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "disable966"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "disable966"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.dis"), "SCP-966"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "enable966"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "enable966"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(Format(GetLocalString("console", "help.SCP.en"), "SCP-966"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "doorcontrol" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "doorcontrol"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.dc"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "asd"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "asd"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.asd"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "unlockcheckpoints" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "unlockcheckpoints"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.uc"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "revive", "undead", "resurrect"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "revive"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.revive"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "teleport"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "teleport"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.teleport"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "stopsound", "stfu"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "stopsound"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.stfu"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "weed", "scp-420-j", "420j", "scp420-j", "scp-420j", "420"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "weed"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.weed"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "infect"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "infect"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.infect"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "crystal" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "crystal"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.crystal"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "ice" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "ice"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.ice"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "resetfunds"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "resetfunds"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.rf"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "giveachievement"
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "giveachievement"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.ac"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Case "codes" 
					;[Block]
					CreateConsoleMsg(Format(GetLocalString("console", "help.title"), "codes"))
					CreateConsoleMsg("******************************")
					CreateConsoleMultiMsg(GetLocalString("console", "help.codes"))
					CreateConsoleMsg("******************************")
					;[End Block]
				Default
					;[Block]
					CreateConsoleMsg(GetLocalString("console", "help.no"), 255, 150, 0)
					;[End Block]
			End Select
			;[End Block]
		Case "ending"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			me\SelectedEnding = Clamp(Int(StrTemp), Ending_A1, Ending_B2)
			me\Terminated = True
			;[End Block]
		Case "noclipspeed"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			chs\NoClipSpeed = Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "fly.speed"), StrTemp))
			;[End Block]
		Case "injure"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			me\Injuries = Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "inj"), StrTemp))
			;[End Block]
		Case "cls", "clear"
			;[Block]
			ClearConsole()
			;[End Block]
		Case "infect"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			I_008\Timer = Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "008"), StrTemp))
			;[End Block]
		Case "crystal"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			I_409\Timer = Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "409"), StrTemp))
			;[End Block]
		Case "ice"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			I_009\Timer = Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "009"), StrTemp))
			;[End Block]
		Case "givedisease", "gd"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Int(Min(StrTemp, 6))
			If I_1025\State[StrTemp] = 0.0 Then I_1025\State[StrTemp] = 0.001
			
			CreateConsoleMsg(Format(GetLocalString("console", "1025"), StrTemp))
			;[End Block]
		Case "removedisease", "rd"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Int(Min(StrTemp, 6))
			If I_1025\State[StrTemp] = 0.0 Then I_1025\State[StrTemp] = 0.0
			
			CreateConsoleMsg(Format(GetLocalString("console", "1025"), StrTemp))
			;[End Block]
		Case "givefinedisease", "gfd"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Int(Min(StrTemp, 5))
			If I_1025\FineState[StrTemp] = 0.0
				If StrTemp = 0
					MaxItemAmount = MaxItemAmount + 2
					InjurePlayer(1.5, 0.0, 1000.0)
					PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\BodyHorrorExchange" + Rand(0, 3) + ".ogg"))
					CreateMsg(GetLocalString("msg", "extraparts"))
					I_1025\FineState[0] = 1.0
				Else
					I_1025\FineState[StrTemp] = 1.0
				EndIf
			EndIf
			
			CreateConsoleMsg(Format(GetLocalString("console", "1025"), StrTemp))
			;[End Block]
		Case "removefinedisease", "rfd"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Int(Min(StrTemp, 5))
			If I_1025\FineState[StrTemp] > 0.0
				If StrTemp = 0 Then
					MaxItemAmount = MaxItemAmount - 2
				EndIf
				I_1025\FineState[StrTemp] = 0.0
			EndIf
			
			CreateConsoleMsg(Format(GetLocalString("console", "1025"), StrTemp))
			;[End Block]
		Case "heal"
			;[Block]
			ResetNegativeStats()
			CreateConsoleMsg(GetLocalString("console", "heal"))
			;[End Block]
		Case "teleport", "tp"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Temp = False
			i = FindRoomID(StrTemp)
			
			For r.Rooms = Each Rooms
				If r\RoomTemplate\RoomID = i Lor Str(r\RoomTemplate\RoomID) = StrTemp
					Temp = True
					If r\RoomCenter <> 0
						TeleportEntity(me\Collider, EntityX(r\RoomCenter, True), EntityY(r\OBJ) + 0.5, EntityZ(r\RoomCenter, True), 0.3, True)
					Else
						TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.5, EntityZ(r\OBJ))
					EndIf
					TeleportToRoom(r)
					CreateConsoleMsg(Format(GetLocalString("console", "tp.success"), StrTemp))
					Exit
				EndIf
			Next
			
			If (Not Temp) Then CreateConsoleMsg(GetLocalString("console", "tp.failed"), 255, 0, 0)
			;[End Block]
		Case "roomlist", "roomslist", "rooms", "room list"
			;[Block]
			CreateConsoleMsg("--------------------------------")
			For rt.RoomTemplates = Each RoomTemplates
				CreateConsoleMsg("ID: " + rt\RoomID + "; Name: " + rt\Name)
			Next
			CreateConsoleMsg("--------------------------------")
			;[End Block]
		Case "spawnitem", "si", "giveitem", "gi"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Temp = False 
			For itt.ItemTemplates = Each ItemTemplates
				If Lower(itt\Name) = StrTemp Lor Lower(itt\DisplayName) = StrTemp Lor Str(itt\ID) = StrTemp
					it.Items = CreateItem(itt\Name, itt\ID, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider))
					CreateConsoleMsg(Format(GetLocalString("console", "si.success"), itt\DisplayName))
					Temp = True
					Exit
				EndIf
			Next
			
			If (Not Temp) Then CreateConsoleMsg(GetLocalString("console", "si.failed"), 255, 0, 0)
			;[End Block]
		Case "spawncup", "givecup", "spawndrink", "givedrink"
			;[Block]
			StrTemp = Upper(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Temp = False
			
			If S2IMapContains(I_294\DrinksMap, StrTemp)
				Local Drink% = JsonGetArrayValue(I_294\Drinks, S2IMapGet(I_294\DrinksMap, StrTemp))
				Local Temp2% = 0
				
				Temp2 = JsonGetValue(Drink, "explosion")
				If (Not JsonIsNull(Temp2))
					If JsonGetBool(Temp2)
						me\ExplosionTimer = 135.0
						Temp2 = JsonGetValue(Drink, "death_message")
						If (Not JsonIsNull(Temp2)) Then msg\DeathMsg = JsonGetString(Temp2)
					EndIf
				EndIf
				
				Local DrinkColor% = JsonGetArray(JsonGetValue(Drink, "color"))
				Local Alpha# = JsonGetFloat(JsonGetValue(Drink, "alpha"))
				
				Temp2 = JsonGetValue(Drink, "glow")
				If (Not JsonIsNull(Temp2))
					If JsonGetBool(Temp2) Then Alpha = -Alpha
				EndIf
				
				it.Items = CreateItem("Cup", it_cup, EntityX(me\Collider), EntityY(Camera, True), EntityZ(me\Collider), JsonGetInt(JsonGetArrayValue(DrinkColor, 0)), JsonGetInt(JsonGetArrayValue(DrinkColor, 1)), JsonGetInt(JsonGetArrayValue(DrinkColor, 2)), Alpha)
				it\Name = StrTemp
				it\DisplayName = Format(GetLocalString("items", "cupof"), StrTemp)
				CreateConsoleMsg(Format(GetLocalString("console", "si.success"), it\DisplayName))
				Temp = True
			EndIf
			If (Not Temp) Then CreateConsoleMsg(GetLocalString("console", "si.failed"), 255, 0, 0)
			;[End Block]
		Case "itemlist", "itemslist", "items", "item list"
			;[Block]
			CreateConsoleMsg("--------------------------------")
			For itt.ItemTemplates = Each ItemTemplates
				CreateConsoleMsg(Format(Format(Format(GetLocalString("console", "itemlist"), itt\ID, "{0}"), itt\Name, "{1}"), itt\DisplayName, "{2}"), 255, 150, 0)
			Next
			CreateConsoleMsg("--------------------------------")
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
			
			If WireFrameState
				CreateConsoleMsg(GetLocalString("console", "wf.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "wf.off"))
			EndIf
			
			WireFrame(WireFrameState)
			;[End Block]
		Case "disable049", "dis049"
			;[Block]
			If n_I\Curr049 <> Null
				n_I\Curr049\Idle = 1
				PositionEntity(n_I\Curr049\Collider, 0.0, -500.0, 0.0)
				ResetEntity(n_I\Curr049\Collider)
				HideEntity(n_I\Curr049\Collider)
				HideEntity(n_I\Curr049\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-049"))
			;[End Block]
		Case "enable049", "en049"
			;[Block]
			If n_I\Curr049 <> Null
				n_I\Curr049\Idle = 0
				ShowEntity(n_I\Curr049\Collider)
				ShowEntity(n_I\Curr049\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-049"))
			;[End Block]
		Case "disable066", "dis066"
			;[Block]
			If n_I\Curr066 <> Null
				n_I\Curr066\Idle = 1
				HideEntity(n_I\Curr066\Collider)
				HideEntity(n_I\Curr066\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-066"))
			;[End Block]
		Case "enable066", "en066"
			;[Block]
			If n_I\Curr066 <> Null
				n_I\Curr066\Idle = 0
				ShowEntity(n_I\Curr066\Collider)
				ShowEntity(n_I\Curr066\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-066"))
			;[End Block]
		Case "disable096", "dis096"
			;[Block]
			If n_I\Curr096 <> Null
				n_I\Curr096\Idle = 1
				HideEntity(n_I\Curr096\Collider)
				HideEntity(n_I\Curr096\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-096"))
			;[End Block]
		Case "enable096", "en096"
			;[Block]
			If n_I\Curr096 <> Null
				n_I\Curr096\Idle = 0
				ShowEntity(n_I\Curr096\Collider)
				ShowEntity(n_I\Curr096\OBJ)
			EndIf
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-096"))
			;[End Block]
		Case "reset096", "r096"
			;[Block]
			If n_I\Curr096 <> Null
				n_I\Curr096\State = 0.0
				StopStream_Strict(n_I\Curr096\SoundCHN) : n_I\Curr096\SoundCHN = 0 : n_I\Curr096\SoundCHN_IsStream = False
				If n_I\Curr096\SoundCHN2 <> 0 Then StopStream_Strict(n_I\Curr096\SoundCHN2) : n_I\Curr096\SoundCHN2 = 0 : n_I\Curr096\SoundCHN2_IsStream = False
				GiveAchievement("096", False)
			EndIf
			CreateConsoleMsg(GetLocalString("console", "r096"))
			;[End Block]
		Case "disable106", "dis106"
			;[Block]
			n_I\Curr106\State = 0.0
			n_I\Curr106\State2 = Rnd(23500.0, 29000.0)
			n_I\Curr106\Contained = True
			HideEntity(n_I\Curr106\Collider)
			HideEntity(n_I\Curr106\OBJ)
			HideEntity(n_I\Curr106\OBJ2)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-106"))
			;[End Block]
		Case "enable106", "en106"
			;[Block]
			n_I\Curr106\Idle = 0
			n_I\Curr106\Contained = False
			ShowEntity(n_I\Curr106\Collider)
			ShowEntity(n_I\Curr106\OBJ)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-106"))
			;[End Block]
		Case "reset106", "r106", "106retreat", "106r"
			;[Block]
			If n_I\Curr106\State > 1.0
				n_I\Curr106\State = 0.0
				n_I\Curr106\State2 = Rnd(23500.0, 29000.0)
				CreateConsoleMsg(GetLocalString("console", "106r"))
			Else
				CreateConsoleMsg(GetLocalString("console", "106r.failed"), 255, 150, 0)
			EndIf
			;[End Block]
		Case "disable173", "dis173"
			;[Block]
			n_I\Curr173\Idle = 3 ; ~ This phenominal comment is brought to you by PolyFox. His absolute wisdom in this fatigue of knowledge brought about a new era of SCP-173 state checks.
			HideEntity(n_I\Curr173\OBJ)
			HideEntity(n_I\Curr173\OBJ2)
			HideEntity(n_I\Curr173\Collider)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-173"))
			;[End Block]
		Case "enable173", "en173"
			;[Block]
			n_I\Curr173\Idle = 0
			ShowEntity(n_I\Curr173\OBJ)
			ShowEntity(n_I\Curr173\OBJ2)
			ShowEntity(n_I\Curr173\Collider)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-173"))
			;[End Block]
		Case "reset372", "r372"
			;[Block]
			For n.NPCs = Each NPCs
				If n\NPCType = NPCType372
					RemoveNPC(n)
					CreateEvent(e_cont3_372, r_cont3_372, 0, 0.0)
					Exit
				EndIf
			Next
			CreateConsoleMsg(GetLocalString("console", "r372"))
			;[End Block]
		Case "disable513-1", "dis513-1"
			;[Block]
			RemoveNPC(n_I\Curr513_1)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-513"))
			;[End Block]
		Case "enable513-1", "en513-1"
			;[Block]
			If n_I\Curr513_1 = Null Then n_I\Curr513_1 = CreateNPC(NPCType513_1, 0.0, 0.0, 0.0)
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-13"))
			;[End Block]
		Case "disable966", "dis966"
			;[Block]
			For n.NPCs = Each NPCs
				If n\NPCType = NPCType966
					If n\State <> 66.0
						n\State = -1.0
						PositionEntity(n\Collider, 0.0, -500.0, 0.0)
						ResetEntity(n\Collider)
					EndIf
					HideEntity(n\Collider)
					HideEntity(n\OBJ)
				EndIf
			Next
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.dis"), "SCP-966"))
			;[End Block]
		Case "enable966", "en966"
			;[Block]
			For n.NPCs = Each NPCs
				If n\NPCType = NPCType966
					If n\State <> 66.0 Then n\State = 0.0
					ShowEntity(n\Collider)
					If wi\NightVision > 0 Then ShowEntity(n\OBJ)
				EndIf
			Next
			CreateConsoleMsg(Format(GetLocalString("console", "SCP.en"), "SCP-966"))
			;[End Block]
		Case "halloween"
			;[Block]
			n_I\IsHalloween = (Not n_I\IsHalloween)
			If n_I\IsHalloween
				n_I\IsNewYear = False
				n_I\IsAprilFools = False
				Tex = LoadTexture_Strict("GFX\NPCs\scp_173_H.png")
				EntityTexture(n_I\Curr173\OBJ, Tex)
				EntityTexture(n_I\Curr173\OBJ2, Tex)
				DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
				CreateConsoleMsg(GetLocalString("console", "halloween.on"))
			Else
				Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png")
				EntityTexture(n_I\Curr173\OBJ, Tex2)
				EntityTexture(n_I\Curr173\OBJ2, Tex2)
				DeleteSingleTextureEntryFromCache(Tex2) : Tex2 = 0
				CreateConsoleMsg(GetLocalString("console", "halloween.off"))
			EndIf
			;[End Block]
		Case "newyear" 
			;[Block]
			n_I\IsNewYear = (Not n_I\IsNewYear)
			If n_I\IsNewYear
				n_I\IsHalloween = False
				n_I\IsAprilFools = False
				Tex = LoadTexture_Strict("GFX\NPCs\scp_173_NY.png")
				EntityTexture(n_I\Curr173\OBJ, Tex)
				EntityTexture(n_I\Curr173\OBJ2, Tex)
				DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
				CreateConsoleMsg(GetLocalString("console", "newyear.on"))
			Else
				Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png")
				EntityTexture(n_I\Curr173\OBJ, Tex2)
				EntityTexture(n_I\Curr173\OBJ2, Tex2)
				DeleteSingleTextureEntryFromCache(Tex2) : Tex2 = 0
				CreateConsoleMsg(GetLocalString("console", "newyear.off"))
			EndIf
			;[End Block]
		Case "joke" 
			;[Block]
			n_I\IsAprilFools = (Not n_I\IsAprilFools)
			If n_I\IsAprilFools
				n_I\IsHalloween = False
				n_I\IsNewYear = False
				Tex = LoadTexture_Strict("GFX\NPCs\scp_173_J.png")
				EntityTexture(n_I\Curr173\OBJ, Tex)
				EntityTexture(n_I\Curr173\OBJ2, Tex)
				DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
				CreateConsoleMsg(GetLocalString("console", "aprilfools.on"))
			Else
				Tex2 = LoadTexture_Strict("GFX\NPCs\scp_173.png")
				EntityTexture(n_I\Curr173\OBJ, Tex2)
				EntityTexture(n_I\Curr173\OBJ2, Tex2)
				DeleteSingleTextureEntryFromCache(Tex2) : Tex2 = 0
				CreateConsoleMsg(GetLocalString("console", "aprilfools.off"))
			EndIf
			;[End Block]
		Case "sanic"
			;[Block]
			chs\SuperMan = (Not chs\SuperMan)
			If chs\SuperMan
				CreateConsoleMsg(GetLocalString("console", "sanic.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "sanic.off"))
			EndIf
			;[End Block]
		Case "scp-420-j", "420", "weed", "scp420-j", "scp-420j", "420j"
			;[Block]
			For i = 1 To 20
				If Rand(2) = 1
					StrTemp = "Some SCP-420-J"
					Temp = it_scp420j
				Else
					StrTemp = "Joint"
					Temp = it_joint
				EndIf
				it.Items = CreateItem(StrTemp, Temp, EntityX(me\Collider, True) + Cos((360.0 / 20.0) * i) * Rnd(0.3, 0.5), EntityY(Camera, True), EntityZ(me\Collider, True) + Sin((360.0 / 20.0) * i) * Rnd(0.3, 0.5))
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
			If chs\GodMode
				CreateConsoleMsg(GetLocalString("console", "god.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "god.off"))
			EndIf
			;[End Block]
		Case "revive", "resurrect"
			;[Block]
			ResetNegativeStats(True)
			If t\OverlayID[MaxOverlayIDAmount - 1] <> 0 Then FreeEntity(t\OverlayID[MaxOverlayIDAmount - 1]) : t\OverlayID[MaxOverlayIDAmount - 1] = 0
			me\Playable = 2
			;[End Block]
		Case "noclip", "fly"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			Select StrTemp
				Case "on", "1", "true"
					;[Block]
					chs\NoClip = True
					me\Playable = 2
					;[End Block]
				Case "off", "0", "false"
					;[Block]
					chs\NoClip = False
					RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
					;[End Block]
				Default
					;[Block]
					chs\NoClip = (Not chs\NoClip)
					If (Not chs\NoClip)
						RotateEntity(me\Collider, 0.0, EntityYaw(me\Collider), 0.0)
					Else
						me\Playable = 2
					EndIf
					;[End Block]
			End Select
			
			If chs\NoClip
				CreateConsoleMsg(GetLocalString("console", "fly.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "fly.off"))
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
			If chs\NoBlink
				CreateConsoleMsg(GetLocalString("console", "nb.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "nb.off"))
			EndIf
			;[End Block]
		Case "debughud", "dbh"
			;[Block]
			If Instr(ConsoleInput, " ") <> 0
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
					CreateConsoleMsg(GetLocalString("console", "debug.cate"), 255, 150, 0)
					;[End Block]
			End Select
			;[End Block]
		Case "stopsound", "stfu"
			;[Block]
			KillSounds()
			
			For e.Events = Each Events
				If e\EventID = e_cont1_173
					For i = 0 To 2
						RemoveNPC(e\room\NPC[i])
						If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
					Next
					If e\room\NPC[7] <> Null
						e\room\NPC[7]\State3 = 2.0
						e\room\NPC[7]\IsDead = True
					EndIf
					If n_I\Curr173\Idle = 1 Then n_I\Curr173\Idle = 0
					PositionEntity(n_I\Curr173\Collider, 0.0, -500.0, 0.0)
					ResetEntity(n_I\Curr173\Collider)
					
					PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[2], True), e\room\y + 384.0 * RoomScale, EntityZ(e\room\Objects[2], True), True)
					RotateEntity(e\room\Objects[2], EntityPitch(e\room\Objects[2], True), EntityYaw(e\room\Objects[2], True), EntityRoll(e\room\Objects[2], True), True)
					RemoveEvent(e)
					Exit
				EndIf
			Next
			CreateConsoleMsg(GetLocalString("console", "stfu"))
			;[End Block]
		Case "camerafog", "cf"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			fog\FarDist = Max(StrTemp, 1.0)
			
			CreateConsoleMsg(Format(GetLocalString("console", "fog"), fog\FarDist, "{0}"))
			;[End Block]
		Case "npclist", "npcslist", "npc list", "npcs list", "npcs"
			;[Block]
			CreateConsoleMsg("--------------------------------")
			CreateConsoleMsg("008-1", 255, 150, 0)
			CreateConsoleMsg("049", 255, 150, 0)
			CreateConsoleMsg("049-2", 255, 150, 0)
			CreateConsoleMsg("066", 255, 150, 0)
			CreateConsoleMsg("096", 255, 150, 0)
			CreateConsoleMsg("106", 255, 150, 0)
			CreateConsoleMsg("173", 255, 150, 0)
			CreateConsoleMsg("372", 255, 150, 0)
			CreateConsoleMsg("457", 255, 150, 0)
			CreateConsoleMsg("513-1", 255, 150, 0)
			CreateConsoleMsg("860-2", 255, 150, 0)
			CreateConsoleMsg("939", 255, 150, 0)
			CreateConsoleMsg("966", 255, 150, 0)
			CreateConsoleMsg("999", 255, 150, 0)
			CreateConsoleMsg("1048", 255, 150, 0)
			CreateConsoleMsg("1048-a", 255, 150, 0)
			CreateConsoleMsg("1499-1", 255, 150, 0)
			CreateConsoleMsg("apache", 255, 150, 0)
			CreateConsoleMsg("class-d", 255, 150, 0)
			CreateConsoleMsg("cockroach", 255, 150, 0)
			CreateConsoleMsg("guard", 255, 150, 0)
			CreateConsoleMsg("mtf", 255, 150, 0)
			CreateConsoleMsg("tentacle", 255, 150, 0)
			CreateConsoleMsg("--------------------------------")
			;[End Block]
		Case "spawn", "s"
			;[Block]
			Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Piece(Args, 1)
			StrTemp2 = Piece(Args, 2)
			
			; ~ Hacky fix for when the user doesn't input a second parameter.
			If StrTemp <> StrTemp2
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
				CreateConsoleMsg(GetLocalString("console", "is.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "is.off"))
			EndIf
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
			
			If RemoteDoorOn
				CreateConsoleMsg(GetLocalString("console", "door.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "door.off"))
			EndIf
			
			For e2.Events = Each Events
				If e2\EventID = e_room2c_ec
					UpdateLever(e2\room\RoomLevers[2]\OBJ)
					RotateEntity(e2\room\RoomLevers[2]\OBJ, 80.0 + (-160.0 * RemoteDoorOn), EntityYaw(e2\room\RoomLevers[2]\OBJ), 0.0)
					Exit
				EndIf
			Next
			;[End Block]
		Case "unlockcheckpoints"
			;[Block]
			For e2.Events = Each Events
				If e2\EventID = e_room2_sl
					e2\EventState3 = 0.0
					UpdateLever(e2\room\RoomLevers[0]\OBJ)
					RotateEntity(e2\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e2\room\RoomLevers[0]\OBJ), 0.0)
				ElseIf e2\EventID = e_cont2_008
					e2\EventState = 2.0
					UpdateLever(e2\room\Objects[1])
					RotateEntity(e2\room\Objects[1], 0.0, EntityYaw(e2\room\Objects[1]), 0.0)
					If e2\room\RoomEmitters[0] <> Null Then FreeEmitter(e2\room\RoomEmitters[0])
				EndIf
			Next
			
			CreateConsoleMsg(GetLocalString("console", "uc"))
			;[End Block]
		Case "disablenuke"
			;[Block]
			For e.Events = Each Events
				If e\EventID = e_room2_nuke
					e\EventState = 0.0
					UpdateLever(e\room\RoomLevers[0]\OBJ)
					UpdateLever(e\room\RoomLevers[1]\OBJ)
					RotateEntity(e\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e\room\RoomLevers[0]\OBJ), 0.0)
					RotateEntity(e\room\RoomLevers[1]\OBJ, 80.0, EntityYaw(e\room\RoomLevers[1]\OBJ), 0.0)
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
						If e\EventID = e_gate_a_entrance
							e\EventState3 = 1.0
							e\room\RoomDoors[1]\Open = True
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "ue.a"))
					;[End Block]
				Case "b"
					;[Block]
					For e.Events = Each Events
						If e\EventID = e_gate_b_entrance
							e\EventState3 = 1.0
							e\room\RoomDoors[1]\Open = True
							Exit
						EndIf
					Next
					CreateConsoleMsg(GetLocalString("console", "ue.b"))
					;[End Block]
				Default
					;[Block]
					For e.Events = Each Events
						If e\EventID = e_gate_b_entrance Lor e\EventID = e_gate_a_entrance
							e\EventState3 = 1.0
							e\room\RoomDoors[1]\Open = True
						EndIf
						If e\EventID = e_room2_office_2 Then e\EventState = 1.0
					Next
					CreateConsoleMsg(GetLocalString("console", "ue"))
					;[End Block]
			End Select
			RemoteDoorOn = True
			;[End Block]
		Case "kill", "suicide"
			;[Block]
			Kill()
			Select Rand(4)
				Case 1
					;[Block]
					msg\DeathMsg = GetLocalString("death", "kill_1")
					;[End Block]
				Case 2
					;[Block]
					msg\DeathMsg = Format(GetLocalString("death", "kill_2"), SubjectName)
					;[End Block]
				Case 3
					;[Block]
					msg\DeathMsg = GetLocalString("death", "kill_3")
					;[End Block]
				Case 4
					;[Block]
					msg\DeathMsg = Format(GetLocalString("death", "kill_4"), SubjectName)
					;[End Block]
			End Select
			;[End Block]
		Case "asd"
			;[Block]
			chs\NoBlink = True
			chs\NoTarget = True
			chs\NoClip = True
			chs\GodMode = True
			chs\InfiniteStamina = True
			
			fog\FarDist = 17.0
			
			KillSounds()
			
			For e.Events = Each Events
				If e\EventID = e_cont1_173
					For i = 0 To 2
						RemoveNPC(e\room\NPC[i])
						If i < 2 Then FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
					Next
					If e\room\NPC[7] <> Null
						e\room\NPC[7]\State3 = 2.0
						e\room\NPC[7]\IsDead = True
					EndIf
					If n_I\Curr173\Idle = 1 Then n_I\Curr173\Idle = 0
					PositionEntity(n_I\Curr173\Collider, 0.0, -500.0, 0.0)
					ResetEntity(n_I\Curr173\Collider)
					
					PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[2], True), e\room\y + 384.0 * RoomScale, EntityZ(e\room\Objects[2], True), True)
					RotateEntity(e\room\Objects[2], EntityPitch(e\room\Objects[2], True), EntityYaw(e\room\Objects[2], True), EntityRoll(e\room\Objects[2], True), True)
					RemoveEvent(e)
					Exit
				EndIf
			Next
			CreateConsoleMsg(GetLocalString("console", "stfu"))
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
			
			If chs\NoTarget
				CreateConsoleMsg(GetLocalString("console", "nt.on"))
			Else
				CreateConsoleMsg(GetLocalString("console", "nt.off"))
			EndIf
			;[End Block]
		Case "teleport173"
			;[Block]
			PositionEntity(n_I\Curr173\Collider, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ResetEntity(n_I\Curr173\Collider)
			;[End Block]
		Case "seteventstate"
			;[Block]
			Args = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			StrTemp = Piece(Args, 1, " ")
			StrTemp2 = Piece(Args, 2, " ")
			StrTemp3 = Piece(Args, 3, " ")
			StrTemp4 = Piece(Args, 4, " ")
			
			Local PL_Room_Found% = False
			
			If StrTemp = "" Lor StrTemp2 = "" Lor StrTemp3 = "" Lor StrTemp4 = ""
				CreateConsoleMsg(GetLocalString("console", "ses.failed"), 255, 150, 0)
			Else
				For e.Events = Each Events
					If PlayerRoom = e\room
						If Lower(StrTemp) <> "keep" Then e\EventState = Float(StrTemp)
						If Lower(StrTemp2) <> "keep" Then e\EventState2 = Float(StrTemp2)
						If Lower(StrTemp3) <> "keep" Then e\EventState3 = Float(StrTemp3)
						If Lower(StrTemp4) <> "keep" Then e\EventState4 = Float(StrTemp4)
						CreateConsoleMsg(Format(Format(Format(Format(GetLocalString("console", "ses.success"), e\EventState, "{0}"), e\EventState2, "{1}"), e\EventState3, "{2}"), e\EventState4, "{3}"))
						PL_Room_Found = True
						Exit
					EndIf
				Next
				If (Not PL_Room_Found) Then CreateConsoleMsg(GetLocalString("console", "ses.failed.apply"), 255, 150, 0)
			EndIf
			;[End Block]
		Case "giveachievement"
			;[Block]
			If Instr(ConsoleInput, " ") <> 0
				StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Else
				StrTemp = ""
			EndIf
			
			If StrTemp = "all"
				Local Defines% = JsonGetArray(JsonGetValue(AchievementsArray, "achievements"))
				Local ArraySize% = JsonGetArraySize(Defines)
				
				For i = 0 To ArraySize - 1
					Local ID$ = JsonGetString(JsonGetValue(JsonGetArrayValue(Defines, i), "id"))
					
					If ID <> "console" And ID <> "keter" And ID <> "apollyon" Then GiveAchievement(ID)
				Next
				SaveProgressFile()
				CreateConsoleMsg(GetLocalString("console", "ga.all"))
			ElseIf S2IMapContains(AchievementsIndex, StrTemp)
				If StrTemp <> "console" And StrTemp <> "keter" And StrTemp <> "apollyon"
					GiveAchievement(StrTemp)
					Local AchvName% = JsonGetValue(JsonGetValue(JsonGetValue(LocalAchievementsArray, "translations"), StrTemp), "name")
					
					If JsonIsNull(AchvName) Then AchvName = JsonGetValue(JsonGetValue(JsonGetValue(AchievementsArray, "translations"), StrTemp), "name")
					CreateConsoleMsg(Format(GetLocalString("console", "ga.success"), JsonGetString(AchvName)))
				Else
					CreateConsoleMsg(Format(GetLocalString("console", "ga.cheat"), StrTemp), 255, 0, 0)
				EndIf
			ElseIf StrTemp <> "all"
				CreateConsoleMsg(Format(GetLocalString("console", "ga.failed"), StrTemp), 255, 0, 0)
			EndIf
			;[End Block]
		Case "427state"
			;[Block]
			StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			
			I_427\Timer = 70.0 * Float(StrTemp)
			
			CreateConsoleMsg(Format(GetLocalString("console", "427"), StrTemp))
			;[End Block]
		Case "teleport106"
			;[Block]
			If (Not n_I\Curr106\Contained)
				n_I\Curr106\State = 2.0
				n_I\Curr106\Idle = 0
				n_I\Curr106\EnemyX = EntityX(me\Collider)
				n_I\Curr106\EnemyY = EntityY(me\Collider)
				n_I\Curr106\EnemyZ = EntityZ(me\Collider)
			EndIf
			;[End Block]
		Case "resetfunds", "money", "rich"
			;[Block]
			For it.Items = Each Items
				Select it\ItemTemplate\ID
					Case it_mastercard
						;[Block]
						it\State = 10
						;[End Block]
					Case it_mastercard_golden
						;[Block]
						it\State = 1000
						;[End Block]
				End Select
			Next
			CreateConsoleMsg(GetLocalString("console", "funds"))
			;[End Block]
		Case "codes"
			;[Block]
			CreateConsoleMsg(GetLocalString("console", "codes_1"))
			CreateConsoleMsg("")
			CreateConsoleMsg(Format(GetLocalString("console", "codes_9"), CODE_DR_GEARS))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_3"), CODE_DR_HARP))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_4"), CODE_DR_L))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_2"), CODE_DR_MAYNARD))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_10"), CODE_CMR))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_6"), CODE_MAINTENANCE_TUNNELS))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_7"), CODE_CONT1_035))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_12"), CODE_CONT3_009))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_5"), CODE_DR_BURTON))
			CreateConsoleMsg(Format(GetLocalString("console", "codes_11"), CODE_SC_FRANKLIN))
			
			CreateConsoleMsg("")
			CreateConsoleMsg(GetLocalString("console", "codes_8"))
			;[End Block]
		Case "save"
			;[Block]
			If Instr(ConsoleInput, " ") <> 0
				StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
			Else
				StrTemp = ""
			EndIf
			
			If StrTemp = ""
				SaveGame(CurrSave\RealName)
			Else
				StrTemp = Replace(StrTemp, ":", "")
				StrTemp = Replace(StrTemp, ".", "")
				StrTemp = Replace(StrTemp, "/", "")
				StrTemp = Replace(StrTemp, "\", "")
				StrTemp = Replace(StrTemp, "<", "")
				StrTemp = Replace(StrTemp, ">", "")
				StrTemp = Replace(StrTemp, "|", "")
				StrTemp = Replace(StrTemp, "?", "")
				StrTemp = Replace(StrTemp, Chr(34), "")
				StrTemp = Replace(StrTemp, "*", "")
				StrTemp = Trim(StrTemp)
				SaveGame(StrTemp)
			EndIf
			PlaySound_Strict(LoadTempSound("SFX\General\Save0.ogg"))
			as\Timer = 70.0 * 70.0
			CreateConsoleMsg(GetLocalString("save", "saved"))
			;[End Block]
		Case "mav"
			;[Block]
			RuntimeErrorEx("memori acces volishen")
			;[End Block]
		Default
			;[Block]
			CreateConsoleMsg(GetLocalString("console", "notfound"), 255, 0, 0)
			;[End Block]
	End Select
End Function

Function ClearConsole%()
	Local c.ConsoleMsg
	
	For c.ConsoleMsg = Each ConsoleMsg
		Delete(c)
	Next
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("")
	CreateConsoleMsg("Console commands: ")
	CreateConsoleMsg(" - help [page]")
	CreateConsoleMsg(" - roomlist")
	CreateConsoleMsg(" - teleport [room name]")
	CreateConsoleMsg(" - godmode [on / off]")
	CreateConsoleMsg(" - noclip [on / off]")
	CreateConsoleMsg(" - infinitestamina [on / off]")
	CreateConsoleMsg(" - noblink [on / off]")
	CreateConsoleMsg(" - notarget [on / off]")
	CreateConsoleMsg(" - noclipspeed [x] (default = 2.0)")
	CreateConsoleMsg(" - wireframe [on / off]")
	CreateConsoleMsg(" - debughud [category]")
	CreateConsoleMsg(" - camerafog [x]")
	CreateConsoleMsg(" - heal")
	CreateConsoleMsg(" - revive")
	CreateConsoleMsg(" - asd")
	CreateConsoleMsg(" - npclist")
	CreateConsoleMsg(" - itemlist")
	CreateConsoleMsg(" - spawnitem [item name / ID]")
	CreateConsoleMsg(" - disable049 / enable049")
	CreateConsoleMsg(" - disable066 / enable066")
	CreateConsoleMsg(" - disable096 / enable096")
	CreateConsoleMsg(" - reset096")
	CreateConsoleMsg(" - disable106 / enable106")
	CreateConsoleMsg(" - reset106")
	CreateConsoleMsg(" - disable173 / enable173")
	CreateConsoleMsg(" - reset372")
	CreateConsoleMsg(" - disable513-1 / enable513-1")
	CreateConsoleMsg(" - disable966 / enable966")
	CreateConsoleMsg(" - spawn [NPC type]")
End Function

