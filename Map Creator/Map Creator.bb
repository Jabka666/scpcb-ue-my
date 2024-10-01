; ~ The main file of the game
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Map Creator for SCP-CB UER v1.3.3
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/n7KdW4u
;----------------------------------------------------------------------------------------------------------------------------------------------------

Local InitErrorStr$ = ""

If FileSize("FMod.dll") = 0 Then InitErrorStr = InitErrorStr + "FMod.dll" + Chr(13) + Chr(10)
If FileSize("dplayx.dll") = 0 Then InitErrorStr = InitErrorStr + "dplayx.dll" + Chr(13) + Chr(10)
If FileSize("BlitzEncode.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzEncode.dll" + Chr(13) + Chr(10)
If FileSize("IniController.dll") = 0 Then InitErrorStr = InitErrorStr + "IniController.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then RuntimeError("The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr + ".")

If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#BlitzPlus