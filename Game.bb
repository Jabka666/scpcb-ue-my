; ~ The main file of the game
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ SCP - Containment Breach Ultimate Edition Reborn v1.2
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This is a modification of the game "SCP - Containment Breach"
; ~ The mod is developed by the "Ultimate Edition Team" (https://www.moddb.com/company/ultimate-edition-team)
; ~ It is released under the CC-BY-SA 3.0 license as it is a derivative work based on SCP - Containment Breach and the SCP Foundation
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/n7KdW4u
;----------------------------------------------------------------------------------------------------------------------------------------------------

Local InitErrorStr$ = ""

If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10) ; ~ Optional in fact
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)
If FileSize("IniControler.dll") = 0 Then InitErrorStr = InitErrorStr + "IniControler.dll" + Chr(13) + Chr(10)
If FileSize("RapidBson.dll") = 0 Then InitErrorStr = InitErrorStr + "RapidBson.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr + ".")

Const VersionNumber$ = "1.2"

If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")

Include "Source Code\KeyBinds_Core.bb"
Include "Source Code\INI_Core.bb"

LoadOptionsINI()

Const LanguageFile$ = "Data\local.ini"
Const SubtitlesFile$ = "Data\subtitles.jsonc"
Const AchievementsFile$ = "Data\achievements.ini"
Const LoadingScreensFile$ = "Data\loading_screens.ini"
Const SCP294File$ = "Data\SCP-294.ini"
Const FontsFile$ = "Data\fonts.ini"
Const SCP1499ChunksFile$ = "Data\1499chunks.jsonc"

IniWriteBuffer(LanguageFile)
IniWriteBuffer(AchievementsFile)
IniWriteBuffer(SCP294File)
IniWriteBuffer(FontsFile)

Include "Source Code\Launcher_Core.bb"

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS