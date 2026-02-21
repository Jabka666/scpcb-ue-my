; BlitzToolbox - A part of BlitzToolbox
; Custom library for scpcb-ue.
; v1.0 2023.9.12
; https://github.com/ZiYueCommentary/BlitzToolbox/tree/ziyue

.lib "BlitzToolbox.dll"

FindNextDirectory$(path$, directory$, default$):"_FindNextDirectory@12"
DownloadFileThread(url$, file$):"_DownloadFileThread@8"
GetDownloadFileThreadSize%():"_GetDownloadFileThreadSize@0"
CreateS2IMap%():"_CreateS2IMap@0"
S2IMapSize%(map%):"_S2IMapSize@4"
S2IMapErase(map%, key$):"_S2IMapErase@8"
S2IMapSet(map%, key$, value%):"_S2IMapSet@12"
S2IMapGet%(map%, key$):"_S2IMapGet@8"
S2IMapContains%(map%, key$):"_S2IMapContains@8"
ClearS2IMap(map%):"_ClearS2IMap@4"
DestroyS2IMap(map%):"_DestroyS2IMap@4"

CreateI2IMap%():"_CreateI2IMap@0"
I2IMapSize%(map%):"_I2IMapSize@4"
I2IMapErase(map%, key%):"_I2IMapErase@8"
I2IMapSet(map%, key%, value%):"_I2IMapSet@12"
I2IMapGet%(map%, key%):"_I2IMapGet@8"
I2IMapContains%(map%, key%):"_I2IMapContains@8"
ClearI2IMap(map%):"_ClearI2IMap@4"
DestroyI2IMap(map%):"_DestroyI2IMap@4"