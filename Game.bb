; ~ The main file of the game
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ SCP - Containment Breach Ultimate Edition v1.1
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
Const LoadingScreensFile$ = "LoadingScreens\loading_screens.ini"
Const SCP294File$ = "Data\SCP-294.ini"
Const SCP1499ChunksFile$ = "Data\1499chunks.ini" ; ~ Unable to localize

Function CheckForDlls%() ; ~ Can't localized because IniControler.dll may not exist
	Local InitErrorStr$ = ""
	
	If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
	If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
	If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10) ; ~ Optional in fact
	If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)
	If FileSize("IniControler.dll") = 0 Then InitErrorStr = InitErrorStr + "IniControler.dll" + Chr(13) + Chr(10)

	If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr + ".")
End Function

Function SetLanguage%(Language$)
	lang\CurrentLanguage = Language
	If lang\CurrentLanguage = "en-US" Then
		lang\LanguagePath = ""
	Else
		lang\LanguagePath = "Localization\" + lang\CurrentLanguage + "\"
		IniWriteBuffer(lang\LanguagePath + LanguageFile)
		IniWriteBuffer(lang\LanguagePath + SubtitlesFile)
		IniWriteBuffer(lang\LanguagePath + AchievementsFile)
		IniWriteBuffer(lang\LanguagePath + LoadingScreensFile)
		IniWriteBuffer(lang\LanguagePath + SCP294File)
	EndIf
	If StringToBoolean(GetLocalString("global", "splitwithspace")) Then
		SplitSpace = " "
	Else
		SplitSpace = ""
	EndIf
	opt\Language = Language
End Function

CheckForDlls()

; ~ First, create a folder inside "AppData" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
; ~ Second, create a folder inside "scpcb-ue" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")

Global lang.Language = New Language

IniWriteBuffer(LanguageFile)
IniWriteBuffer(SubtitlesFile)
IniWriteBuffer(AchievementsFile)
IniWriteBuffer(LoadingScreensFile)
IniWriteBuffer(SCP294File)
IniWriteBuffer(SCP1499ChunksFile)

Include "Source Code\KeyBinds_Core.bb"
Include "Source Code\INI_Core.bb"

LoadOptionsINI()
SetLanguage(opt\Language)

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D