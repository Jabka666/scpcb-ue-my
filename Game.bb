 ; ~ The main file of the game
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ SCP - Containment Breach Ultimate Edition Reborn v1.6
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This is a modification of the game "SCP - Containment Breach"
; ~ The mod is developed by the "Ultimate Edition Team" (https://www.moddb.com/company/ultimate-edition-team)
; ~ It is released under the CC-BY-SA 3.0 license as it is a derivative work based on SCP - Containment Breach and the SCP Foundation
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/KnGVpTRN6a
;----------------------------------------------------------------------------------------------------------------------------------------------------

Local InitErrorStr$ = ""

Global ErrorMessageInitialized% = False

If FileSize("BlitzToolbox.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzToolbox.dll" + Chr(13) + Chr(10)
If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("d3dx9_43.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dx9_43.dll" + Chr(13) + Chr(10) ; ~ Optional in fact
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)
If FileSize("IniController.dll") = 0 Then InitErrorStr = InitErrorStr + "IniController.dll" + Chr(13) + Chr(10)
If FileSize("RapidBson.dll") = 0 Then InitErrorStr = InitErrorStr + "RapidBson.dll" + Chr(13) + Chr(10)
; ffmpeg dlls
If FileSize("avfilter-12.dll") = 0 Then InitErrorStr = InitErrorStr + "avfilter-12.dll" + Chr(13) + Chr(10)
If FileSize("avformat-63.dll") = 0 Then InitErrorStr = InitErrorStr + "avformat-63.dll" + Chr(13) + Chr(10)
If FileSize("avutil-61.dll") = 0 Then InitErrorStr = InitErrorStr + "avutil-61.dll" + Chr(13) + Chr(10)
If FileSize("swresample-7.dll") = 0 Then InitErrorStr = InitErrorStr + "swresample-7.dll" + Chr(13) + Chr(10)
If FileSize("swscale-10.dll") = 0 Then InitErrorStr = InitErrorStr + "swscale-10.dll" + Chr(13) + Chr(10)
If FileSize("avcodec-63.dll") = 0 Then InitErrorStr = InitErrorStr + "avcodec-63.dll" + Chr(13) + Chr(10)
If FileSize("avdevice-63.dll") = 0 Then InitErrorStr = InitErrorStr + "avdevice-63.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr + ".")

Const VersionNumber$ = "1.6"

If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")

Include "Source Code\KeyBinds_Core.bb"
Include "Source Code\INI_Core.bb"
Include "Source Code\Strict_Functions_Core.bb"

LoadOptionsINI()

Const LanguageFile$ = "Data\local.ini"
Const SubtitlesFile$ = "Data\subtitles.jsonc"
Const AchievementsFile$ = "Data\achievements.jsonc"
Const LoadingScreensFile$ = "Data\loading_screens.jsonc"
Const SCP294File$ = "Data\SCP-294.jsonc"
Const FontsFile$ = "Data\fonts.ini"
Const SCP1499ChunksFile$ = "Data\1499chunks.jsonc"

IniWriteBuffer(LanguageFile)
IniWriteBuffer(AchievementsFile)
IniWriteBuffer(FontsFile)

Include "Source Code\Launcher_Core.bb"

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS