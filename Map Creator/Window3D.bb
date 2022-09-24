; ~ Window3D SCP-CB UE v1.0.31
;-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

; ~ If these folders don't exist, create them again (also for debugging)

; ~ First, create a folder inside "AppData" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then
	CreateDir(GetEnv("AppData") + "\scpcb-ue")
EndIf
; ~ Second, create a folder inside "scpcb-ue" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then
	CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")
EndIf
; ~ After, put the "options.ini" file to the latest created folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini") <> 1 Then
	WriteFile(GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini")
EndIf

Include "Source Code\Main_Core_3D.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D