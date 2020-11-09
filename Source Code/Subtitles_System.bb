; ~ Subtitles IDs constants
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

Function UpdateSubtitles()
	Local sub.Subtitles, i%
	Local ShouldDeleteSubtitles% = True
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					ShouldDeleteSubtitles = False
					sub\Timer[i] = sub\Timer[i] - fpst\FPSFactor[0 + (SelectedDoor <> Null)]
				EndIf
			Next
			If ShouldDeleteSubtitles Then Delete(sub)
		EndIf
	Next
End Function

Function RenderSubtitles()
	Local sub.Subtitles, i%
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			For i = ANNOUNCEMENT To THIRD_PERSON
				If sub\Timer[i] > 0.0 Then
					SetFont(fo\FontID[Font_Default])
					Color(0, 0, 0)
					Text((opt\GraphicWidth / 2) + 1, (opt\GraphicHeight / 2) + 291 - (i * 20), sub\Txt[i], True, False)
					Color(opt\SubColorR, opt\SubColorG, opt\SubColorB)
					Text((opt\GraphicWidth / 2), (opt\GraphicHeight / 2) + 290 - (i * 20), sub\Txt[i], True, False)
				EndIf
			Next
		EndIf
	Next
End Function

Const SubtitlesFile$ = "Data\subtitles.ini"

Function ShowSubtitles(Name$, SubID%) ; ~ Check why the second one renders only after first one
	Local Loc% = GetINISectionLocation(SubtitlesFile, Name)
	Local LinesAmount% = GetINIInt2(SubtitlesFile, Loc, "LinesAmount")
	Local i%, sub.Subtitles
	
	For i = 1 To LinesAmount
		sub.Subtitles = New Subtitles
		sub\Txt[SubID] = GetINIString2(SubtitlesFile, Loc, "Txt" + i)
		sub\Timer[SubID] = 70.0 * GetINIFloat2(SubtitlesFile, Loc, "Timer" + i)
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D