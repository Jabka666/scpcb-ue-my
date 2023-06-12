; ~ Map Creator SCP-CB UER v1.2
;-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

; ~ First, create a folder inside "AppData" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue")
; ~ Second, create a folder inside "scpcb-ue" folder
If FileType(GetEnv("AppData") + "\scpcb-ue\Data\") <> 2 Then CreateDir(GetEnv("AppData") + "\scpcb-ue\Data")

Include "Source Code\Main_Core.bb"

;~IDEal Editor Parameters:
;~C#BlitzPlus