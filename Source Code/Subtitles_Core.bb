Global SubFile%, LocalSubFile%
Global SubColors%, LocalSubColors%

Type SubtitlesAssets
	Field BoxTexture%
	Field TextHeight%
	Field CurrentBoxTop#
	Field CurrentBoxHeight#
	Field BoxWidth#, BoxLeft#, BoxTop#
End Type

Global subassets.SubtitlesAssets

Function InitSubtitlesAssets%()
	subassets.SubtitlesAssets = New SubtitlesAssets
	
	subassets\BoxWidth = opt\GraphicWidth * 0.7
	subassets\BoxLeft = mo\Viewport_Center_X + 1 - (subassets\BoxWidth / 2)
	subassets\BoxTop = opt\GraphicHeight * 0.82
	
	subassets\BoxTexture = CreateTextureUsingCacheSystem(1, 1, 1 + 256)
	
	SetFontEx(fo\FontID[Font_Default])
	subassets\TextHeight = FontHeight() * 2.5
End Function

Global SubtitlesInit%

Type SubtitlesMsg
	Field SoundPath$
	Field snd.Sound
	Field yPos#
	Field CurrYPos#
	Field Txt$
	Field R%, G%, B%
	Field TimeLeft#
	Field Alpha#
	Field CurrentText$
	Field TextIndex#
End Type

Type QueuedSubtitlesMsg
	Field SoundPath$
	Field subtitles.SubtitlesMsg
	Field snd.Sound
	Field Txt$
	Field R%, G%, B%
	Field TimeStart#
	Field TimeLeft#
End Type

Function UpdateSubtitles%()
	If SelectedDifficulty\Name = difficulties[DIFFICULTY_APOLLYON]\Name Lor (Not opt\EnableSubtitles) Then Return
	
	Local Queue.QueuedSubtitlesMsg
	Local LastSubtitles.SubtitlesMsg
	Local CoordEx% = (10 * MenuScale)
	Local FactorToken% = (me\SelectedEnding <> -1)
	
	For Queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If Queue\TimeStart > 0.0
			Queue\TimeStart = Queue\TimeStart - fps\Factor[FactorToken]
		Else
			Local TxtLine$ = Queue\Txt
			Local HasSplit% = False
			
			LastSubtitles.SubtitlesMsg = Last SubtitlesMsg
			
			; ~ Split long lines of text into multiple lines
			While Len(TxtLine) > 0
				Local StringRight# = subassets\BoxLeft + CoordEx + StringWidth(TxtLine)
				
				If StringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
					Local NextLine$ = ""
					
					While StringRight > subassets\BoxLeft + subassets\BoxWidth - CoordEx
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0))
						StringRight = subassets\BoxLeft + CoordEx + StringWidth(TxtLine)
					Wend
					
					Local OldTxtLine$ = TxtLine
					
					While Right(TxtLine, 1) <> " "
						NextLine = Right(TxtLine, 1) + NextLine
						TxtLine = Left(TxtLine, Max(Len(TxtLine) - 1, 0))
						
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
						LastSubtitles\yPos = LastSubtitles\yPos - subassets\TextHeight
						LastSubtitles\CurrYPos = LastSubtitles\CurrYPos - subassets\TextHeight
						
						If Before LastSubtitles <> Null Then LastSubtitles = Before LastSubtitles
					EndIf
					CreateSubtitlesMsg(Queue\SoundPath, Queue\snd, Trim(TxtLine), Queue\TimeLeft, Queue\R, Queue\G, Queue\B)
					HasSplit = True
					TxtLine = NextLine
				Else
					CreateSubtitlesMsg(Queue\SoundPath, Queue\snd, Trim(TxtLine), Queue\TimeLeft, Queue\R, Queue\G, Queue\B)
					TxtLine = ""
				EndIf
			Wend
			Delete(Queue)
		EndIf
	Next
	
	Local sub.SubtitlesMsg
	Local FPSFactorEx# = fps\Factor[FactorToken] / 7.0
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		; ~ Gradually reveal the text
		If sub\TextIndex < Len(sub\Txt)
			sub\TextIndex = sub\TextIndex + fps\Factor[FactorToken]
			sub\CurrentText = Left(sub\Txt, Floor(sub\TextIndex))
		EndIf
		
		sub\TimeLeft = sub\TimeLeft - fps\Factor[FactorToken]
		If sub\TimeLeft < 0.0
			sub\Alpha = Max(sub\Alpha - FPSFactorEx, 0.0)
			If sub\Alpha <= 0.0 Then Delete(sub)
		Else
			sub\Alpha = Min(1.0, sub\Alpha + FPSFactorEx)
		EndIf
	Next
End Function

Function RenderSubtitles%()
	If SelectedDifficulty\Name = difficulties[DIFFICULTY_APOLLYON]\Name Lor (Not opt\EnableSubtitles) Then Return
	
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
	Local TexBuffer% = TextureBuffer(subassets\BoxTexture)
	
	If TexBuffer <> 0
		Color(20, 20, 20, 255 * 0.75)
		DrawBuffer(TexBuffer, subassets\BoxLeft, subassets\CurrentBoxTop, subassets\BoxWidth, subassets\CurrentBoxHeight, 1)
	EndIf
	
	; ~ Render a text
	Lines = -1
	SetFontEx(fo\FontID[Font_Default])
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
		
		sub\yPos = BoxTop + (subassets\TextHeight * Lines) + (CoordEx * 2)
		sub\CurrYPos = CurveValue(sub\yPos, sub\CurrYPos, 7.0)
		
		Color(sub\R, sub\G, sub\B)
		TextEx(subassets\BoxLeft + (10 * MenuScale), sub\CurrYPos, sub\CurrentText)
	Next
	Color(255, 255, 255)
End Function

Function CreateSubtitlesToken%(SoundPath$, snd.Sound)
	If (Not opt\EnableSubtitles) Lor (Not SubtitlesInit) Then Return
	If SelectedDifficulty\Name = difficulties[DIFFICULTY_APOLLYON]\Name Then Return ; ~ Call this line when first line is passed
	
	Local i%
	Local Token% = JsonGetValue(LocalSubFile, SoundPath)
	
	If JsonIsNull(Token) Lor (Not JsonIsArray(Token))
		Local FallBack% = JsonGetValue(LocalSubFile, "fallback")
		
		If (JsonGetBool(FallBack)) Lor (JsonIsNull(FallBack)) Then Token = JsonGetValue(SubFile, SoundPath)
		If (Not JsonIsArray(Token)) Then Return
	EndIf
	
	Local Arr% = JsonGetArray(Token)
	Local ArraySize% = JsonGetArraySize(Arr)
	
	For i = 0 To ArraySize - 1
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
				Local JsonArrayVal% = JsonGetArray(ColorArray)
				
				ColorR = JsonGetInt(JsonGetArrayValue(JsonArrayVal, 0))
				ColorG = JsonGetInt(JsonGetArrayValue(JsonArrayVal, 1))
				ColorB = JsonGetInt(JsonGetArrayValue(JsonArrayVal, 2))
			EndIf
		EndIf
		QueueSubtitlesMsg(SoundPath, snd, Txt, DelayTime, Length, ColorR, ColorG, ColorB)
	Next
End Function

Function RemoveSubtitlesToken%(snd.Sound)
	Local Queue.QueuedSubtitlesMsg
	
	For Queue.QueuedSubtitlesMsg = Each QueuedSubtitlesMsg
		If Queue\snd = snd Then Delete(Queue)
	Next
End Function

Function ClearSubtitles%()
	Local sub.SubtitlesMsg
	
	For sub.SubtitlesMsg = Each SubtitlesMsg
		Delete(sub)
	Next
End Function

Function QueueSubtitlesMsg%(SoundPath$, snd.Sound, Txt$, TimeStart#, TimeLeft#, R% = 255, G% = 255, B% = 255)
	If Txt = "" Lor Left(Txt, 1) = "[" Then Return
	
	Local Queue.QueuedSubtitlesMsg
	
	Queue.QueuedSubtitlesMsg = New QueuedSubtitlesMsg
	Queue\SoundPath = SoundPath
	Queue\snd = snd
	
	Queue\Txt = Txt
	
	Queue\R = R : Queue\G = G : Queue\B = B
	
	Queue\TimeLeft = TimeLeft * 70.0
	Queue\TimeStart = TimeStart * 70.0
	
	Insert Queue Before First QueuedSubtitlesMsg
End Function

Function CreateSubtitlesMsg%(SoundPath$, snd.Sound, Txt$, TimeLeft#, R% = 255, G% = 255, B% = 255)
	If me\Deaf Then Return
	
	If snd <> Null
		Local IsChannelPlaying% = False
		Local i%
		
		For i = 0 To MaxChannelsAmount - 1
			If ChannelPlaying(snd\Channels[i]) Then IsChannelPlaying = True
		Next
		If (Not IsChannelPlaying) Then Return
	EndIf
	
	Local sub.SubtitlesMsg
	
	sub.SubtitlesMsg = New SubtitlesMsg
	sub\SoundPath = SoundPath
	sub\snd = snd
	sub\Txt = Txt
	sub\R = R
	sub\G = G
	sub\B = B
	sub\TimeLeft = TimeLeft
	sub\Alpha = 0.0
	sub\CurrentText = ""
	sub\TextIndex = 0
	
	Local Lines% = 0
	Local sub2.SubtitlesMsg
	
	For sub2.SubtitlesMsg = Each SubtitlesMsg
		Lines = Lines + 1
	Next
	
	Local BoxTop# = (subassets\BoxTop + subassets\TextHeight) - subassets\TextHeight * Lines
	Local BoxHeight# = subassets\TextHeight * Lines
	Local CoordEx% = subassets\TextHeight + 10 * MenuScale
	
	sub\yPos = (BoxTop + BoxHeight) - CoordEx
	sub\CurrYPos = (BoxTop + BoxHeight) - CoordEx
	
	Insert sub After Last SubtitlesMsg
End Function

Function DeInitSubtitlesAssets%()
	Local snd.Sound
	
	For snd.Sound = Each Sound
		RemoveSubtitlesToken(snd)
	Next
	
	FreeTexture(subassets\BoxTexture) : subassets\BoxTexture = 0
	
	Delete(subassets) : subassets = Null
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS