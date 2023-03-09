; ~ Subtitles ID constants
;[Block]
Const ANNOUNCEMENT% = 0
Const FIRST_PERSON% = 1
Const SECOND_PERSON% = 2
Const THIRD_PERSON% = 3
;[End Block]

Type Subtitles
	Field Txt$[4]
	Field Timer#[4]
End Type

Function UpdateSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local sub.Subtitles
	Local ShouldDeleteSubtitles% = True, i%
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					ShouldDeleteSubtitles = False
					sub\Timer[i] = sub\Timer[i] - fps\Factor[0]
				EndIf
			Next
			If ShouldDeleteSubtitles Then Delete(sub)
		EndIf
	Next
End Function

Function RenderSubtitles%()
	If (Not opt\EnableSubtitles) Then Return
	
	Local sub.Subtitles
	Local i%
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					SetFont2(fo\FontID[Font_Default])
					Color(opt\SubColorR, opt\SubColorG, opt\SubColorB)
					Text2(mo\Viewport_Center_X, mo\Viewport_Center_Y + (290 * MenuScale) - ((i * 20) * MenuScale), sub\Txt[i], True, False)
				EndIf
			Next
		EndIf
	Next
End Function

Function ShowSubtitles%(Name$)
	CatchErrors("ShowSubtitles(" + Name + ")")
	
	If (Not opt\EnableSubtitles) Lor (Not IniBufferSectionExist(lang\LanguagePath + SubtitlesFile, Name)) Then Return
	
	Local sub.Subtitles, CurrSub.Subtitles
	Local Person% = Int(GetFileLocalString(SubtitlesFile, Name, "Person", "", False))
	Local SubID%
	Local i% = 1
	
	Select Person
		Case 1
			;[Block]
			SubID = FIRST_PERSON
			;[End Block]
		Case 2
			;[Block]
			SubID = SECOND_PERSON
			;[End Block]
		Case 3
			SubID = THIRD_PERSON
			;[End Block]
		Default
			;[Block]
			SubID = ANNOUNCEMENT
			;[End Block]
	End Select
	
	For sub.Subtitles = Each Subtitles
		If sub\Txt[SubID] = "" Then
			CurrSub.Subtitles = sub.Subtitles
			Exit
		EndIf
	Next
	
	Repeat
		Local TxtExist% = IniBufferKeyExist(lang\LanguagePath + SubtitlesFile, Name, "Txt" + i)
		Local TimerExist% = IniBufferKeyExist(lang\LanguagePath + SubtitlesFile, Name, "Timer" + i)
		
		If (Not (TxtExist And TimerExist)) Then Exit ; ~ Seems there is no more subtitle
		
		If CurrSub = Null Then
			sub.Subtitles = New Subtitles
		Else
			sub.Subtitles = CurrSub.Subtitles
		EndIf
		sub\Txt[SubID] = GetFileLocalString(SubtitlesFile, Name, "Txt" + i, "", False)
		sub\Timer[SubID] = 70.0 * Float(GetFileLocalString(SubtitlesFile, Name, "Timer" + i, "", False))
		
		i = i + 1
	Forever
	
	CatchErrors("Uncaught: ShowSubtitles(" + Name + ")")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D