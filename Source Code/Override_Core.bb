; ~ Override some Blitz3D functions to do more things

Global TextOffset% = 0

Function SetFont%(Font%)
	Local FontName$ = "Default"
	Select Font
		Case fo\FontID[Font_Default]
			FontName = "Default"
		Case fo\FontID[Font_Default_Big]
			FontName = "Default_Big"
		Case fo\FontID[Font_Digital]
			FontName = "Digital"
		Case fo\FontID[Font_Digital_Big]
			FontName = "Digital_Big"
		Case fo\FontID[Font_Journal]
			FontName = "Journal"
		Case fo\FontID[Font_Console]
			FontName = "Console"
	End Select
	TextOffset = Int(GetFileLocalString(FontSettingsFile, FontName, "offset"))
	Blitz_SetFont(Font)
End Function
	
Function Text%(x%, y%, Txt$, AlignX% = 0, AlignY% = 0)
	Blitz_Text(x, y + TextOffset, Txt, AlignX, AlignY)
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D