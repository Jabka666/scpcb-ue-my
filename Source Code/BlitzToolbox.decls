; BlitzToolbox - A part of BlitzToolbox
; Custom library for scpcb-ue.
; v1.0 2023.9.12
; https://github.com/ZiYueCommentary/BlitzToolbox/tree/ziyue

.lib "BlitzToolbox.dll"

FindNextDirectory$(path$, directory$, default$):"_FindNextDirectory@12"
DownloadFileThread(url$, file$):"_DownloadFileThread@8"
CreateS2IMap%():"_CreateS2IMap@0"
S2IMapSet(map%, key$, value%):"_S2IMapSet@12"
S2IMapGet%(map%, key$):"_S2IMapGet@8"
S2IMapContains%(map%, key$):"_S2IMapContains@8"
ClearS2IMap(map%):"_ClearS2IMap@4"
DestroyS2IMap(map%):"_DestroyS2IMap@4"