Global SubFile$

Type SubtitlesAssets
	Field TextHeight%
	Field CurrentBoxTop#
	Field CurrentBoxHeight#
	Field BoxWidth#, BoxLeft#, BoxTop#
End Type

Global subassets.SubtitlesAssets

Function InitSubtitlesAssets%()
	subassets.SubtitlesAssets = New SubtitlesAssets
	
	subassets\BoxWidth = opt\GraphicWidth * 0.75
	subassets\BoxLeft = mo\Viewport_Center_X + 1 - (subassets\BoxWidth / 2)
	subassets\BoxTop = opt\GraphicHeight * 0.82
	
	SetFont2(fo\FontID[Font_Default])
	subassets\TextHeight = FontHeight() * 2.5
	
	CreateSubtitlesColor("announcement", 130, 130, 130)
	CreateSubtitlesColor("ci", 0, 130, 0)
	CreateSubtitlesColor("crew", 135, 160, 130)
	CreateSubtitlesColor("d", 225, 120, 0)
	CreateSubtitlesColor("guard", 180, 180, 150)
	CreateSubtitlesColor("janitor", 120, 140, 170)
	CreateSubtitlesColor("mtf", 100, 60, 45)
	CreateSubtitlesColor("nazi", 130, 0, 20)
	CreateSubtitlesColor("035angry", 150, 0, 0)
	CreateSubtitlesColor("049", 50, 70, 70)
	CreateSubtitlesColor("066", 180, 35, 60)
	CreateSubtitlesColor("106", 10, 5, 5)
	CreateSubtitlesColor("682", 180, 160, 135)
	CreateSubtitlesColor("860-2", 110, 55, 80)
End Function

Global SubtitlesInit%

Type SubtitlesMsg
	Field SoundPath$
	Field sound.Sound
	Field yPos#
	Field CurrYPos#
	Field Txt$
	Field R%, G%, B%
	Field IsGlitch%
	Field TimeLeft#
	Field Alpha#
End Type

Type QueuedSubtitlesMsg
	Field SoundPath$
	Field subtitles.SubtitlesMsg
	Field sound.Sound
	Field Txt$
	Field R%, G%, B%
	Field IsGlitch%
	Field TimeStart#
	Field TimeLeft#
End Type

Type SubtitlesColor
	Field Name$
	Field R%, G%, B%
End Type

Function UpdateSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local queue.QueuedSubtitlesMsg
	Local lastSubtitles.SubtitlesMsg
	
	For queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If queue\TimeStart > 0.0 Then
			queue\TimeStart = queue\TimeStart - fps\Factor[0]
		Else
			Local TxtLine$ = queue\Txt
			Local HasSplit% = False
			
			lastSubtitles.SubtitlesMsg = Last SubtitlesMsg
			
			; ~ Split long lines of text into multiple lines.
			While Len(TxtLine) > 0
				Local StringRight# = subassets\BoxLeft + (10 * MenuScale) + StringWidth(TxtLine)
				
				If StringRight > subassets\BoxLeft + subassets\BoxWidth - (10 * MenuScale) Then
					Local NextLine$ = ""
					
					While StringRight > subassets\BoxLeft + subassets\BoxWidth - (10 * MenuScale)
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0.0))
						StringRight = subassets\BoxLeft + (10 * MenuScale) + StringWidth(TxtLine)
					Wend
					
					Local OldTxtLine$ = TxtLine
					
					While Right(TxtLine, 1) <> " "
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0.0))
						
						Local NextStringRight# = subassets\BoxLeft + (10 * MenuScale) + StringWidth(NextLine)
						
						; ~ If a very long single word exceeds the box size, split it.
						If NextStringRight > subassets\BoxLeft + subassets\BoxWidth- (10 * MenuScale) Then
							While NextStringRight > subassets\BoxLeft + subassets\BoxWidth - (10 * MenuScale)
								TxtLine = Right(NextLine, 1) + TxtLine
								NextLine = Left(NextLine, Len(NextLine) - 1)
								NextStringRight = subassets\BoxLeft + (10 * MenuScale) + StringWidth(NextLine)
							Wend
							Exit
						EndIf
						
						; ~ No need to split the text
						If TxtLine = "" Then
							TxtLine = OldTxtLine
							Exit
						EndIf
					Wend
					
					; ~ Move previously added lines upwards
					If HasSplit Then
						lastSubtitles\yPos = lastSubtitles\yPos - subassets\TextHeight
						lastSubtitles\CurrYPos = lastSubtitles\CurrYPos - subassets\TextHeight
						
						If Before lastSubtitles <> Null Then lastSubtitles = Before lastSubtitles
					EndIf
					CreateSubtitlesMsg(queue\SoundPath, queue\sound, Trim(TxtLine), queue\TimeLeft, queue\R, queue\G, queue\B, queue\IsGlitch)
					HasSplit = True
					TxtLine = NextLine
				Else
					CreateSubtitlesMsg(queue\SoundPath, queue\sound, Trim(TxtLine), queue\TimeLeft, queue\R, queue\G, queue\B, queue\IsGlitch)
					TxtLine = ""
				EndIf
			Wend
			Delete(queue)
		EndIf
	Next
	
	Local sub.SubtitlesMsg
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		sub\TimeLeft = sub\TimeLeft - fps\Factor[0]
		If sub\TimeLeft < 0.0 Then
			sub\Alpha = Max(sub\Alpha - (fps\Factor[0] / 7.0), 0.0)
			If sub\Alpha <= 0.0 Then Delete(sub)
		Else
			sub\Alpha = Min(1.0, sub\Alpha + (fps\Factor[0] / 7.0))
		EndIf
	Next
End Function

Function SubtitlesGetINIFileSectionLocation%(Section$)
	Local Temp%
	
	SeekFile(SubFile, 0)
	
	Local f% = SubFile
	
	Section = Lower(Section)
	
	Local n%=0
	
	While (Not Eof(f))
		Local Strtemp$ = ReadLine(f)
		
		n = n + 1
		If Left(Strtemp, 1) = "[" Then
			Strtemp = Lower(Strtemp)
			Temp = Instr(Strtemp, Section)
			If Temp > 0 Then
				If Mid(Strtemp, Temp - 1, 1) = "[" Lor Mid(Strtemp, Temp - 1, 1) = "|" Then
					SeekFile(SubFile, 0)
					Return(n)
				EndIf
			EndIf
		EndIf
	Wend
	
	SeekFile(SubFile, 0)
End Function

Function RenderSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local sub.SubtitlesMsg
	Local Lines% = 0
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
	Next
	
	Local BoxTop# = (subassets\BoxTop + subassets\TextHeight) - subassets\TextHeight * Lines
	Local BoxHeight# = (subassets\TextHeight * Lines) + (5 * MenuScale)
	
	If Lines = 0 Then BoxHeight = BoxHeight - (5 * MenuScale)
	
	subassets\CurrentBoxTop = CurveValue(BoxTop, subassets\CurrentBoxTop, 7.0)
	subassets\CurrentBoxHeight = CurveValue(BoxHeight, subassets\CurrentBoxHeight, 7.0)
	
	; ~ Render a box
	Color(20, 20, 20)
	Rect(subassets\BoxLeft, subassets\CurrentBoxTop, subassets\BoxWidth, subassets\CurrentBoxHeight)
	
	; ~ Render a text
	Lines = -1
	SetFont2(fo\FontID[Font_Default])
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
		
		Local Txt$ = sub\Txt
		
		sub\yPos = BoxTop + (subassets\TextHeight * Lines) + (10 * MenuScale)
		sub\CurrYPos = CurveValue(sub\yPos, sub\CurrYPos, 7.0)
		
		Local i%
		
		If sub\IsGlitch Then
			For i = 0 To Rand(10, 15)
				Txt = Replace(sub\Txt, Mid(sub\Txt, Rand(1, Len(Txt) - 1), 1), Chr(Rand(130, 250)))
			Next
		EndIf
		
		If opt\OverrideSubColor Then
			Color(opt\SubColorR, opt\SubColorG, opt\SubColorB)
		Else
			Color(sub\R, sub\G, sub\B)
		EndIf
		Text2(subassets\BoxLeft + (10 * MenuScale), sub\CurrYPos, Txt)
	Next
End Function

; ~ Parse the caption settings
; ~ Example of caption settings: <color=mtf,glitch=true>; <length=0.5,r=255,g=255,b=220>
Function ParseSubtitlesSettings$(qsub.QueuedSubtitlesMsg)
	Local Txt$ = qsub\Txt
	Local StartLeft% = 0
	Local StartRight% = 0
	Local Temp% = 0
	
	While Temp < Len(Txt)
		Temp = Temp + 1
		
		If Mid(Txt, Temp, 1) = "<" Then
			StartLeft = Temp - 1
			While Mid(Txt, Temp, 1) <> ">" And Temp < Len(Txt)
				If Mid(Txt, Temp, 1) = "<" Then Temp = Temp + 1
				
				Local IniKey$ = ""
				Local IniValue$ = ""
				
				While Mid(Txt, Temp, 1) <> "=" And Mid(Txt, Temp, 1) <> ">" And Temp < Len(Txt)
					IniKey = IniKey + Mid(Txt, Temp, 1)
					Temp = Temp + 1
				Wend
				
				If Mid(Txt, Temp, 1) = "=" Then
					Temp = Temp + 1
				EndIf
				
				While Mid(Txt, Temp, 1) <> "," And Mid(Txt, Temp, 1) <> ">" And Temp < Len(Txt)
					IniValue = IniValue + Mid(Txt, Temp, 1)
					Temp = Temp + 1
				Wend
				
				If Mid(Txt, Temp, 1) = "," Then Temp = Temp + 1
				
				If Trim(Lower(IniKey)) = "color" Then
					Local subcolor.SubtitlesColor
					
					For subcolor.SubtitlesColor = Each SubtitlesColor
						If Trim(Lower(IniValue)) = subcolor\Name Then
							qsub\R = subcolor\R
							qsub\G = subcolor\G
							qsub\B = subcolor\B
						EndIf
					Next
				EndIf
				If Trim(Lower(IniKey)) = "r" Then qsub\R = Int(Trim(Lower(IniValue)))
				If Trim(Lower(IniKey)) = "g" Then qsub\G = Int(Trim(Lower(IniValue)))
				If Trim(Lower(IniKey)) = "b" Then qsub\B = Int(Trim(Lower(IniValue)))
				
				If Trim(Lower(IniKey)) = "glitch" Then
					If Trim(Lower(IniValue)) = "true" Then
						qsub\IsGlitch = True
					ElseIf Trim(Lower(IniValue)) = "false" Then
						qsub\IsGlitch = False
					EndIf
				EndIf
				If Trim(Lower(IniKey)) = "length" Then qsub\TimeLeft = (Float(Trim(Lower(IniValue))) + 5.0) * 70.0
			Wend
		EndIf
		
		If Mid(Txt, Temp, 1) = ">" Then
			Txt = Left(Txt, StartLeft) + Right(Txt, Len(Txt) - Temp)
			Temp = 0
		EndIf
	Wend
	
	Return(Txt)
End Function

Function CreateSubtitlesToken%(SoundPath$, sound.Sound)
	If (Not opt\EnableSubtitles) Lor (Not SubtitlesInit) Then Return
	
	Local TemporaryString$ = ""
	Local Start% = SubtitlesGetINIFileSectionLocation(SoundPath)
	
	SeekFile(SubFile, 0)
	
	Local f% = SubFile
	Local n% = 0
	
	While (Not Eof(f))
		Local Strtemp$ = ReadLine(f)
		
		n = n + 1
		If n = Start Then
			Repeat
				TemporaryString = ReadLine(f)
				
				If Instr(TemporaryString, "=") <> 0 Then
					If Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0.0))) = "text" Then
						QueueSubtitlesMsg(SoundPath, sound, Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))), 0.0, 10.0)
					Else
						QueueSubtitlesMsg(SoundPath, sound, Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))), Float(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0.0)))), 10.0)
					EndIf
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
			SeekFile(SubFile, 0)
			Return
		EndIf
	Wend

	SeekFile(SubFile, 0)
End Function

Function RemoveSubtitlesToken%(sound.Sound)
	Local queue.QueuedSubtitlesMsg
	
	For queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If queue\sound = sound Then Delete(queue)
	Next
End Function

Function ClearSubtitles%()
	Local subtitles.SubtitlesMsg
	
	For subtitles.SubtitlesMsg = Each SubtitlesMsg
		Delete(subtitles)
	Next
End Function

Function QueueSubtitlesMsg%(SoundPath$, sound.Sound, Txt$, TimeStart#, TimeLeft#)
	If Txt = "" Lor Left(Txt, 1) = "[" Then Return
	
	Local queue.QueuedSubtitlesMsg
	
	queue.QueuedSubtitlesMsg = New QueuedSubtitlesMsg
	queue\SoundPath = SoundPath
	queue\sound = sound
	
	queue\Txt = Txt
	
	queue\R = 255 : queue\G = 255 : queue\B = 255
	queue\IsGlitch = False
	
	queue\TimeLeft = TimeLeft * 70.0
	queue\TimeStart = TimeStart * 70.0
	
	queue\Txt = ParseSubtitlesSettings(queue)
	
	Insert queue Before First QueuedSubtitlesMsg
End Function

Function CreateSubtitlesMsg%(SoundPath$, sound.Sound, Txt$, TimeLeft#, R% = 255, G% = 255, B% = 255, IsGlitch% = False)
	If me\Deaf Then Return
	
	If sound <> Null Then
		Local IsChannelPlaying% = False
		Local i%
		
		For i = 0 To MaxChannelsAmount - 1
			If ChannelPlaying(sound\Channels[i]) Then IsChannelPlaying = True
		Next
		
		If (Not IsChannelPlaying) Then Return
	EndIf
	
	Local sub.SubtitlesMsg
	
	sub.SubtitlesMsg = New SubtitlesMsg
	sub\SoundPath = SoundPath
	sub\sound = sound
	sub\Txt = Txt
	sub\R = R
	sub\G = G
	sub\B = B
	sub\IsGlitch = IsGlitch
	sub\TimeLeft = TimeLeft
	sub\Alpha = 0.0
	
	Local Lines% = 0
	Local subtitles.SubtitlesMsg
	
	For subtitles.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
	Next
	
	Local BoxTop# = (subassets\BoxTop + subassets\TextHeight) - subassets\TextHeight * Lines
	Local BoxHeight# = subassets\TextHeight * Lines
	
	sub\yPos = (BoxTop + BoxHeight) - subassets\TextHeight + (10 * MenuScale)
	sub\CurrYPos = (BoxTop + BoxHeight) - subassets\TextHeight + (10 * MenuScale)
	
	Insert sub After Last SubtitlesMsg
End Function

Function CreateSubtitlesColor%(Name$, R%, G%, B%)
	Local subcolor.SubtitlesColor
	
	subcolor.SubtitlesColor = New SubtitlesColor
	subcolor\Name = Name
	subcolor\R = R
	subcolor\G = G
	subcolor\B = B
End Function

Function DeInitSubtitlesAssets%()
	Local subcolor.SubtitlesColor, snd.Sound
	
	For subcolor.SubtitlesColor = Each SubtitlesColor
		Delete(subcolor)
	Next
	For snd.Sound = Each Sound
		RemoveSubtitlesToken(snd)
	Next
	Delete(subassets)
End Function

SubFile = ReadFile_Strict(SubtitlesFile)

SubtitlesInit = True

;~IDEal Editor Parameters:
;~C#Blitz3D TSS