Function IniWriteString%(File$, Section$, Parameter$, Value$, UpdateBuffer% = True)
	IniWriteString_(File, Section, Parameter, Value, UpdateBuffer)
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
		DefaultValue = IniGetBufferString_("..\Data\local.ini", Section, Parameter, Section + "," + Parameter)
	Else 
		DefaultValue = Section + "," + Parameter
	EndIf
	Return(ConvertUTF8toANSI(IniGetBufferString_("..\Localization\" + Language + "\Data\local.ini", Section, Parameter, DefaultValue))) ; ~ local.ini -> UTF8, MapCreator -> ANSI
End Function

Function Format$(String_$, Parameter$, Replace_$ = "%s")
	Return(Replace(String_, Replace_, Parameter))
End Function

Global OptionFileMC$ = GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini"

;[Block]
; ~ BlitzEncode - A part of BlitzToolbox
; ~ Encoding converter.
; ~ v1.0 2022.9.22
; ~ https://github.com/ZiYueCommentary/BlitzToolbox

Const UTF8% = 65001

Function ConvertANSItoUTF8$(Txt$)
	Return(ConvertEncoding(Txt, GetCodePage(), UTF8))
End Function

Function ConvertUTF8toANSI$(Txt$)
	Return(ConvertEncoding(Txt, UTF8, GetCodePage()))
End Function
;[End Block]

Type Options
	Field FogR%, FogG%, FogB%
	Field CursorR%, CursorG%, CursorB%
	Field VSync%
	Field ShowFPS%
	Field CamRange#
	Field Events%
End Type

Global opt.Options = New Options

Function LoadOptionsINI()
	; ~ [3-D SCENE]

	opt\FogR = IniGetInt(OptionFileMC, "3-D Scene", "BG Color R", 0)
	
	opt\FogG = IniGetInt(OptionFileMC, "3-D Scene", "BG Color G", 0)
	
	opt\FogB = IniGetInt(OptionFileMC, "3-D Scene", "BG Color B", 0)
	
	opt\CursorR% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color R", 255)
	
	opt\CursorG% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color G", 0)
	
	opt\CursorB% = IniGetInt(OptionFileMC, "3-D Scene", "Cursor Color B", 0)
	
	opt\VSync = IniGetInt(OptionFileMC, "3-D Scene", "VSync", True)
	
	opt\ShowFPS = IniGetInt(OptionFileMC, "3-D Scene", "Show FPS", False)
	
	opt\CamRange = IniGetFloat(OptionFileMC, "3-D Scene", "Camera Range", 50.0)
	
	opt\Events% = IniGetInt(OptionFileMC, "General", "Events_Default", True)
End Function

;~IDEal Editor Parameters:
;~C#BlitzPlus