; ~ The main file of the game
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ SCP - Containment Breach Ultimate Edition v1.0.3
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This is a modification of the game "SCP - Containment Breach"
; ~ The mod is developed by the "Ultimate Edition Team" (https://www.moddb.com/company/ultimate-edition-team)
; ~ It is released under the CC-BY-SA 3.0 license as it is a derivative work based on SCP - Containment Breach and the SCP Foundation
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/n7KdW4u
;----------------------------------------------------------------------------------------------------------------------------------------------------

Type Language
	Field CurrentLanguage$
	Field LanguagePath$
End Type

Const LanguageFile$ = "Data\local.ini"
Const SubtitlesFile$ = "Data\subtitles.ini"
Const AchievementsFile$ = "Data\Achievements.ini"

Function CheckForDlls%() ; ~ Can't localized because IniControler.dll may not exist
	Local InitErrorStr$ = ""
	
	If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
	If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
	If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10)
	If FileSize("BlitzMovie.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzMovie.dll" + Chr(13) + Chr(10)
	If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)
	If FileSize("IniControler.dll") = 0 Then InitErrorStr = InitErrorStr + "IniControler.dll" + Chr(13) + Chr(10)
	
	If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr)
End Function

Function SetLanguage(Language$)
	If Language = "UserLanguage" Then
		lang\CurrentLanguage$ = GetUserLanguage()
	Else 
		lang\CurrentLanguage$ = Language
	EndIf
	lang\LanguagePath$ = "Localization\" + lang\CurrentLanguage$
	IniWriteBuffer_(lang\LanguagePath + "\" + LanguageFile, 1)
	IniWriteBuffer_(lang\LanguagePath + "\" + SubtitlesFile, 1)
	IniWriteBuffer_(lang\LanguagePath + "\" + AchievementsFile, 1)
End Function

CheckForDlls()

; ~ First, create a folder inside "AppData" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then
	CreateDir(GetEnv("AppData") + "\scpcb-ue")
EndIf
; ~ Second, create a folder inside "scpcb-ue" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then
	CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")
EndIf
; ~ After, put the "options.ini" file to the latest created folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\options.ini") <> 1 Then
	WriteFile(GetEnv("AppData") + "\scpcb-ue\Data\options.ini")
EndIf

Global lang.Language = New Language

IniWriteBuffer_(LanguageFile, 1)
IniWriteBuffer_(SubtitlesFile, 1)
IniWriteBuffer_(AchievementsFile, 1)

Include "Source Code\KeyBinds_Core.bb"
Include "Source Code\INI_Core.bb"

LoadOptionsINI()

SetLanguage(GetINIString(OptionFile, "Global", "Language"))

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D