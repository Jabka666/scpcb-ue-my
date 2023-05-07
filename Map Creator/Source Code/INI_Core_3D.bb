; ~ IniControler - A part of BlitzToolBox
; ~ Write & Read ini file.
; ~ v1.06 2022.11.12
; ~ https://github.com/ZiYueCommentary/BlitzToolbox

Function IniWriteBuffer%(File$, ClearPrevious% = True)
	IniWriteBuffer_(File, ClearPrevious)
End Function

Function IniGetBufferString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Return(IniGetBufferString_(File, Section, Parameter, DefaultValue))
End Function

Function IniGetString$(File$, Section$, Parameter$, DefaultValue$ = "", AllowBuffer% = True)
	Return(IniGetString_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function IniGetInt%(File$, Section$, Parameter$, DefaultValue% = 0, AllowBuffer% = True)
	Local Result$ = IniGetString(File, Section, Parameter, DefaultValue, AllowBuffer)
	
	Select Result
		Case "True", "true", "1"
			;[Block]
			Return(True)
			;[End Block]
		Case "False", "false", "0"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(Result))
			;[End Block]
	End Select
End Function

Function IniGetFloat#(File$, Section$, Parameter$, DefaultValue# = 0.0, AllowBuffer% = True)
	Return(IniGetFloat_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function GetLocalString$(Section$, Parameter$, CheckRootFile% = True)
	Local DefaultValue$
	
	If CheckRootFile Then
		DefaultValue = IniGetBufferString("..\" + LanguageFile, Section, Parameter, Section + "," + Parameter)
	Else 
		DefaultValue = Section + "," + Parameter
	EndIf
	Return(IniGetBufferString("..\" + LanguagePath + LanguageFile, Section, Parameter, DefaultValue))
End Function

Function Format$(String_$, Parameter$, Replace_$ = "%s")
	Return(Replace(String_, Replace_, Parameter))
End Function

Function StripFileName$(File$)
	Local mi$ = "", LastSlash% = 0, i%
	
	If Len(File) > 0 Then
		For i = 1 To Len(File)
			mi = Mid(File, i, 1)
			If mi = "\" Lor mi = "/" Then LastSlash = i
		Next
	EndIf
	Return(Left(File, LastSlash))
End Function

Function StripPath$(File$) 
	Local Name$ = "", i%, mi$
	
	If Len(File) > 0 Then
		For i = Len(File) To 1 Step -1 
			mi = Mid(File, i, 1) 
			If mi = "\" Lor mi = "/" Then Return(Name)
			Name = mi + Name 
		Next 
	EndIf 
	Return(Name) 
End Function

Global OptionFileMC$ = GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini"

Type Options
	Field FogR%, FogG%, FogB%
	Field CursorR%, CursorB%, CursorG%
	Field VSync%
	Field ShowFPS%
	Field CamRange#
End Type

Global opt.Options = New Options

Function LoadOptionsINI%()
	; ~ [3-D SCENE]
	
	; ~ TODO: Make colored fog
	opt\FogR = IniGetInt(OptionFileMC, "3-D Scene", "BG Color R", 0)
	
	opt\FogG = IniGetInt(OptionFileMC, "3-D Scene", "BG Color G", 0)
	
	opt\FogB = IniGetInt(OptionFileMC, "3-D Scene", "BG Color B", 0)
	
	opt\CursorR% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color R", 255)
	
	opt\CursorG% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color G", 0)
	
	opt\CursorB% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color B", 0)
	
	opt\VSync% = IniGetInt(OptionFileMC, "3-D Scene", "VSync", True)
	
	opt\ShowFPS% = IniGetInt(OptionFileMC, "3-D Scene", "Show FPS", False)
	
	opt\CamRange# = IniGetFloat(OptionFileMC, "3-D Scene", "Camera Range", 50.0)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS