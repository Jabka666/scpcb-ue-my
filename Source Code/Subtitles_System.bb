Type Subtitles
	Field Txt$
	Field Timer#
End Type

Function UpdateSubtitles()
	Local sub.Subtitles
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			If sub\Timer > 0.0 Then
				sub\Timer = sub\Timer - fpst\FPSFactor[0 + (SelectedDoor <> Null)]
			Else
				Delete(sub)
			EndIf
		EndIf
	Next
End Function

Function RenderSubtitles()
	Local sub.Subtitles
	
	For sub.Subtitles = Each Subtitles
		If sub.Subtitles = First Subtitles Then
			If sub\Timer > 0.0 Then
				SetFont(fo\FontID[Font_Default])
				Color(0, 0, 0)
				Text((opt\GraphicWidth / 2) + 1, (opt\GraphicHeight / 2) + 291, sub\Txt, True, False)
				Color(opt\SubColorR, opt\SubColorG, opt\SubColorB)
				Text((opt\GraphicWidth / 2), (opt\GraphicHeight / 2) + 290, sub\Txt, True, False)
			EndIf
		EndIf
	Next
End Function

Const SubtitlesFile$ = "Data\subtitles.ini"

Function ShowSubtitles(Name$)
	Local Loc% = GetINISectionLocation(SubtitlesFile, Name)
	Local LinesAmount% = GetINIInt2(SubtitlesFile, Loc, "LinesAmount")
	Local i%, sub.Subtitles
	
	For sub.Subtitles = Each Subtitles
		Delete Each Subtitles
	Next
	
	For i = 1 To LinesAmount
		sub.Subtitles = New Subtitles
		sub\Txt = GetINIString2(SubtitlesFile, Loc, "Txt" + i)
		sub\Timer = 70.0 * GetINIFloat2(SubtitlesFile, Loc, "Timer" + i)
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D