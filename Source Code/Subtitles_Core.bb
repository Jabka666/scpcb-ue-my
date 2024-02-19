Global SubFile%, LocalSubFile%
Global SubColors%, LocalSubColors%

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
	
	SetFontEx(fo\FontID[Font_Default])
	subassets\TextHeight = FontHeight() * 2.5
End Function

Global SubtitlesInit%

Type SubtitlesMsg
	Field SoundPath$
	Field sound.Sound
	Field yPos#
	Field CurrYPos#
	Field Txt$
	Field R%, G%, B%
	Field TimeLeft#
	Field Alpha#
End Type

Type QueuedSubtitlesMsg
	Field SoundPath$
	Field subtitles.SubtitlesMsg
	Field sound.Sound
	Field Txt$
	Field R%, G%, B%
	Field TimeStart#
	Field TimeLeft#
End Type

Function UpdateSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local queue.QueuedSubtitlesMsg
	Local lastSubtitles.SubtitlesMsg
	Local CoordEx% = (10 * MenuScale)
	
	For queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If queue\TimeStart > 0.0
			queue\TimeStart = queue\TimeStart - fps\Factor[0]
		Else
			Local TxtLine$ = queue\Txt
			Local HasSplit% = False
			
			lastSubtitles.SubtitlesMsg = Last SubtitlesMsg
			
			; ~ Split long lines of text into multiple lines
			While Len(TxtLine) > 0
				Local StringRight# = subassets\BoxLeft + CoordEx + StringWidth(TxtLine)
				
				If StringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
					Local NextLine$ = ""
					
					While StringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0.0))
						StringRight = subassets\BoxLeft + CoordEx + StringWidth(TxtLine)
					Wend
					
					Local OldTxtLine$ = TxtLine
					
					While Right(TxtLine, 1) <> " "
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0.0))
						
						Local NextStringRight# = subassets\BoxLeft + CoordEx + StringWidth(NextLine)
						
						; ~ If a very long single word exceeds the box size, split it
						If NextStringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
							While NextStringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
								TxtLine = Right(NextLine, 1) + TxtLine
								NextLine = Left(NextLine, Len(NextLine) - 1)
								NextStringRight = subassets\BoxLeft + CoordEx + StringWidth(NextLine)
							Wend
							Exit
						EndIf
						
						; ~ No need to split the text
						If TxtLine = ""
							TxtLine = OldTxtLine
							Exit
						EndIf
					Wend
					
					; ~ Move previously added lines upwards
					If HasSplit
						lastSubtitles\yPos = lastSubtitles\yPos - subassets\TextHeight
						lastSubtitles\CurrYPos = lastSubtitles\CurrYPos - subassets\TextHeight
						
						If Before lastSubtitles <> Null Then lastSubtitles = Before lastSubtitles
					EndIf
					CreateSubtitlesMsg(queue\SoundPath, queue\sound, Trim(TxtLine), queue\TimeLeft, queue\R, queue\G, queue\B)
					HasSplit = True
					TxtLine = NextLine
				Else
					CreateSubtitlesMsg(queue\SoundPath, queue\sound, Trim(TxtLine), queue\TimeLeft, queue\R, queue\G, queue\B)
					TxtLine = ""
				EndIf
			Wend
			Delete(queue)
		EndIf
	Next
	
	Local sub.SubtitlesMsg
	Local FPSFactorEx# = fps\Factor[0] / 7.0
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		sub\TimeLeft = sub\TimeLeft - fps\Factor[0]
		If sub\TimeLeft < 0.0
			sub\Alpha = Max(sub\Alpha - FPSFactorEx, 0.0)
			If sub\Alpha <= 0.0 Then Delete(sub)
		Else
			sub\Alpha = Min(1.0, sub\Alpha + FPSFactorEx)
		EndIf
	Next
End Function

Function RenderSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local sub.SubtitlesMsg
	Local Lines% = 0
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
	Next
	
	Local BoxTop# = (subassets\BoxTop + subassets\TextHeight) - subassets\TextHeight * Lines
	Local CoordEx% = (5 * MenuScale)
	Local BoxHeight# = (subassets\TextHeight * Lines) + CoordEx
	
	If Lines = 0 Then BoxHeight = BoxHeight - CoordEx
	
	subassets\CurrentBoxTop = CurveValue(BoxTop, subassets\CurrentBoxTop, 7.0)
	subassets\CurrentBoxHeight = CurveValue(BoxHeight, subassets\CurrentBoxHeight, 7.0)
	
	; ~ Render a box
	Color(20, 20, 20)
	Rect(subassets\BoxLeft, subassets\CurrentBoxTop, subassets\BoxWidth, subassets\CurrentBoxHeight)
	
	; ~ Render a text
	Lines = -1
	SetFontEx(fo\FontID[Font_Default])
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
		
		Local Txt$ = sub\Txt
		
		sub\yPos = BoxTop + (subassets\TextHeight * Lines) + (CoordEx * 2)
		sub\CurrYPos = CurveValue(sub\yPos, sub\CurrYPos, 7.0)
		
		If opt\OverrideSubColor
			Color((sub\R + opt\SubColorR) / 2.0, (sub\G + opt\SubColorG) / 2.0, (sub\B + opt\SubColorB) / 2.0)
		Else
			Color(sub\R, sub\G, sub\B)
		EndIf
		TextEx(subassets\BoxLeft + (10 * MenuScale), sub\CurrYPos, Txt)
	Next
End Function

Function CreateSubtitlesToken%(SoundPath$, sound.Sound)
	If (Not opt\EnableSubtitles) Lor (Not SubtitlesInit) Then Return
	
	Local i%
	Local Token% = JsonGetValue(LocalSubFile, SoundPath)
	
	If JsonIsNull(Token) Lor (Not JsonIsArray(Token))
		If (JsonGetBool(JsonGetValue(LocalSubFile, "fallback"))) Lor (JsonIsNull(JsonGetValue(LocalSubFile, "fallback"))) Token = JsonGetValue(SubFile, SoundPath)
		If (Not JsonIsArray(Token)) Then Return
	EndIf
	
	Local Arr% = JsonGetArray(Token)

	For i = 0 To JsonGetArraySize(Arr) - 1
		Local Subtitle% = JsonGetArrayValue(Arr, i)
		Local TxtVal% = JsonGetValue(Subtitle, "text")
		Local DelayVal% = JsonGetValue(Subtitle, "delay")
		Local LengthVal% = JsonGetValue(Subtitle, "length")
		Local ColorArray%
		Local Txt$ = "missingno"
		Local DelayTime# = 0.0
		Local Length# = 10.0
		Local ColorR% = 255
		Local ColorG% = 255
		Local ColorB% = 255
		
		If JsonIsString(TxtVal) Then Txt = JsonGetString(TxtVal)
		If JsonIsFloat(DelayVal) Then DelayTime = JsonGetFloat(DelayVal)
		If JsonIsFloat(LengthVal) Then Length = JsonGetFloat(LengthVal)
		If (Not JsonIsNull(JsonGetValue(Subtitle, "color")))
			ColorArray = JsonGetValue(SubColors, JsonGetString(JsonGetValue(Subtitle, "color")))
			If (Not JsonIsArray(ColorArray)) Then ColorArray = JsonGetValue(LocalSubColors, JsonGetString(JsonGetValue(Subtitle, "color")))
			If JsonIsArray(ColorArray)
				ColorR = JsonGetInt(JsonGetArrayValue(JsonGetArray(ColorArray), 0))
				ColorG = JsonGetInt(JsonGetArrayValue(JsonGetArray(ColorArray), 1))
				ColorB = JsonGetInt(JsonGetArrayValue(JsonGetArray(ColorArray), 2))
			EndIf
		EndIf
		QueueSubtitlesMsg(SoundPath, sound, Txt, DelayTime, Length, ColorR, ColorG, ColorB)
	Next
End Function

Function RemoveSubtitlesToken%(sound.Sound)
	Local queue.QueuedSubtitlesMsg
	
	For queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If queue\sound = sound Then Delete(queue)
	Next
End Function

Function ClearSubtitles%()
	Local sub.SubtitlesMsg
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Delete(sub)
	Next
End Function

Function QueueSubtitlesMsg%(SoundPath$, sound.Sound, Txt$, TimeStart#, TimeLeft#, R% = 255, G% = 255, B% = 255)
	If Txt = "" Lor Left(Txt, 1) = "[" Then Return
	
	Local queue.QueuedSubtitlesMsg
	
	queue.QueuedSubtitlesMsg = New QueuedSubtitlesMsg
	queue\SoundPath = SoundPath
	queue\sound = sound
	
	queue\Txt = Txt
	
	queue\R = R : queue\G = G : queue\B = B
	
	queue\TimeLeft = TimeLeft * 70.0
	queue\TimeStart = TimeStart * 70.0
	
	Insert queue Before First QueuedSubtitlesMsg
End Function

Function CreateSubtitlesMsg%(SoundPath$, sound.Sound, Txt$, TimeLeft#, R% = 255, G% = 255, B% = 255)
	If me\Deaf Then Return
	
	If sound <> Null
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
	sub\TimeLeft = TimeLeft
	sub\Alpha = 0.0
	
	Local Lines% = 0
	Local subtitles.SubtitlesMsg
	
	For subtitles.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
	Next
	
	Local BoxTop# = (subassets\BoxTop + subassets\TextHeight) - subassets\TextHeight * Lines
	Local BoxHeight# = subassets\TextHeight * Lines
	Local CoordEx% = 10 * MenuScale
	
	sub\yPos = (BoxTop + BoxHeight) - subassets\TextHeight + CoordEx
	sub\CurrYPos = (BoxTop + BoxHeight) - subassets\TextHeight + CoordEx
	
	Insert sub After Last SubtitlesMsg
End Function

Function DeInitSubtitlesAssets%()
	Local snd.Sound
	
	For snd.Sound = Each Sound
		RemoveSubtitlesToken(snd)
	Next
	Delete Each SubtitlesAssets
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS