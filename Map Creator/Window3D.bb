; ~ Window3D SCP-CB UER v1.2
;-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

; ~ First, create a folder inside "AppData" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
; ~ Second, create a folder inside "scpcb-ue" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")
; ~ After, put the "options.ini" file to the latest created folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini") <> 1 Then WriteFile(GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini")

Include "Source Code\Main_Core_3D.bb"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS