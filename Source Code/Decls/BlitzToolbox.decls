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

CreateL2IMap%():"_CreateL2IMap@0"
L2IMapSize%(map%):"_L2IMapSize@4"
L2IMapErase(map%, low%, high%):"_L2IMapErase@12"
L2IMapSet(map%, low%, high%, value%):"_L2IMapSet@16"
L2IMapGet%(map%, low%, high%):"_L2IMapGet@12"
L2IMapContains%(map%, low%, high%):"_L2IMapContains@12"
ClearL2IMap(map%):"_ClearL2IMap@4"
DestroyL2IMap(map%):"_DestroyL2IMap@4"

CreateIntVector%():"_CreateIntVector@0"
IntVectorSize%(vec%):"_IntVectorSize@4"
IntVectorPushBack(vec%, value%):"_IntVectorPushBack@8"
IntVectorPopBack(vec%):"_IntVectorPopBack@4"
IntVectorGet%(vec%, index%):"_IntVectorGet@8"
IntVectorSet(vec%, index%, value%):"_IntVectorSet@12"
IntVectorFind%(vec%, value%):"_IntVectorFind@8"
IntVectorErase(vec%, index%):"_IntVectorErase@8"
IntVectorReserve(vec%, capacity%):"_IntVectorReserve@8"
ClearIntVector(vec%):"_ClearIntVector@4"
DestroyIntVector(vec%):"_DestroyIntVector@4"