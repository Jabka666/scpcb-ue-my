.lib "uemp.dll"

; ===================================================== Timers
GetMicroseconds%():"_GetMicroseconds@0"
GetMilliseconds%():"_GetMilliseconds@0"
GetNanoseconds%():"_GetNanoseconds@0"

; ===================================================== Library loading
LoadDLL%(filename$):"_LoadDLL@4"
GetDLLFunctionPointer%(dll%, declaration$):"_GetDLLFunctionPointer@8"
; ===================================================== Memory
Memory_Alloc%(size%):"_Memory_Alloc@4"
Memory_Free%(pMemory%):"_Memory_Dealloc@4"

Memory_PeekByte%(pMemory%):"_Memory_PeekByte@4"
Memory_PeekShort%(pMemory%):"_Memory_PeekShort@4"
Memory_PeekInt%(pMemory%):"_Memory_PeekInt@4"
Memory_PeekFloat#(pMemory%):"_Memory_PeekFloat@4"
Memory_PeekString$(pMemory%):"_Memory_PeekString@4"
Memory_PeekConstChar$(pMemory%):"_Memory_PeekConstChar@4"
Memory_PeekConstCharBank$(pMemory*):"_Memory_PeekConstChar@4"

Memory_CopyBank%(srcMemory*, destMemory%, size%):"_Memory_Copy@12"
Memory_Copy%(srcMemory%, destMemory%, size%):"_Memory_Copy@12"
Memory_PokeByte(pMemory%, value%):"_Memory_PokeByte@8"
Memory_PokeShort(pMemory%, value%):"_Memory_PokeShort@8"
Memory_PokeInt(pMemory%, value%):"_Memory_PokeInt@8"
Memory_PokeFloat(pMemory%, value#):"_Memory_PokeFloat@8"
Memory_PokeString(pMemory%, value$):"_Memory_PokeString@8"

; ===================================================== Steam API

Steam_RestartAppIfNecessary%(appid%):"_Steam_RestartAppIfNecessary@4"
Steam_Init%():"_Steam_Init@0"
Steam_Update():"_Steam_Update@0"
Steam_Shutdown():"_Steam_Shutdown@0"

Steam_RegisterCallback%(func%, callbackId%):"_Steam_RegisterCallback@8"
Steam_RegisterCallResult%(func%, callbackId%, apiCall$):"_Steam_RegisterCallResult@12"
Steam_UnregisterCallResult(callresult%):"_Steam_UnregisterCallResult@4"

Steam_ActivateGameOverlayToUser(dialog$, steamid$):"_Steam_ActivateGameOverlayToUser@8"
Steam_RequestUserInformation%(steamid$, onlynick%):"_Steam_RequestUserInformation@8"
Steam_GetUserNickname$(steamid$):"_Steam_GetUserNickname@4"
Steam_GetSmallFriendAvatar%(steamid$):"_Steam_GetSmallFriendAvatar@4"
Steam_GetMediumFriendAvatar%(steamid$):"_Steam_GetMediumFriendAvatar@4"
Steam_GetLargeFriendAvatar%(steamid$):"_Steam_GetLargeFriendAvatar@4"
Steam_GetImageSize%(iImage%, pnWidthPtr%, pnHeightPtr%):"_Steam_GetImageSize@12"
Steam_GetImageRGBA%(iImage%, pubDest%, nDestBufferSize):"_Steam_GetImageRGBA@12"

Steam_GiveAchievement_(achname$):"_Steam_GiveAchievement@4"
Steam_GetAchievement_%(achname$):"_Steam_GetAchievement@4"
Steam_GetPlayerID$():"_Steam_GetPlayerID@0"
Steam_GetPlayerName_$():"_Steam_GetPlayerName@0"
Steam_SetRichPresence_%(pchKey$, pchValue$):"_Steam_SetRichPresence@8"
Steam_AdvertiseGame(IP%, Port%):"_Steam_AdvertiseGame@8"

Steam_CreateLobby$(type%, maxmembers%):"_Steam_CreateLobby@8"
Steam_JoinLobby$(lobby$):"_Steam_JoinLobby@4"
Steam_SetLobbyData%(lobby$, key$, value$):"_Steam_SetLobbyData@12"
Steam_GetLobbyData$(lobby$, key$):"_Steam_GetLobbyData@8"
Steam_LeaveLobby(lobby$):"_Steam_LeaveLobby@4"
Steam_GetLobbyOwner$(lobby$):"_Steam_GetLobbyOwner@4"

Steam_AcceptP2PSession%(steamid$):"_Steam_AcceptP2PSession@4"
Steam_CloseP2PSession%(steamid$):"_Steam_CloseP2PSession@4"
Steam_SendP2PPacket%(steamid$, src*, size%, type%):"_Steam_SendP2PPacket@16"
Steam_IsP2PPacketAvailable%(sizeptr%):"_Steam_IsP2PPacketAvailable@4"
Steam_ReadP2PPacket$(dest*, destsize%, msgsizeptr%):"_Steam_ReadP2PPacket@12"

Steam_GetAuthSessionTicket%(pTicket*, cbMaxTicket%, pcbTicket%, steamid$):"_Steam_GetAuthSessionTicket@16"
Steam_CancelAuthTicket(ticket%):"_Steam_CancelAuthTicket@4"

Steam_SetServerListCallback(func%):"_Steam_SetServerListCallback@4"
Steam_RequestInternetServerList():"_Steam_RequestInternetServerList@0"
Steam_GetServerDetails%(iServer%):"_Steam_GetServerDetails@4"
Steam_GetServersCount%():"_Steam_GetServersCount@0"

Steam_DownloadFile$(url$, handleptr%, context%):"_Steam_DownloadFile@12"
Steam_ReleaseDownload%(handle%):"_Steam_ReleaseDownload@4"
Steam_GetDownloadHeader$(handle%, header$):"_Steam_GetDownloadHeader@8"
Steam_GetDownloadData%(handle%, buffer*, offset%, size%):"_Steam_GetDownloadData@16"
Steam_GetDownloadProgress#(handle%):"_Steam_GetDownloadProgress@4"

Steam_PeekCSteamID$(pMemory%):"_Steam_PeekCSteamID@4"
Steam_GetUInt64String$(upper%, lower%):"_Steam_GetUInt64String@8"
Steam_GetUInt64(steamid$, upperptr%, lowerptr%):"_Steam_GetUInt64@12"

; ===================================================== Discord API

Discord_Init(handlers%):"_Discord_Init@4"
Discord_SetPresence(state$, details$, largeImageKey$):"_Discord_SetPresence@12"
Discord_Update():"_Discord_Update@0"
Discord_ResetPresence():"_Discord_ResetPresence@0"
Discord_Flush():"_Discord_Flush@0"

; ===================================================== Some stuff
; Deque
CreateDeque%():"_CreateDeque@0"
Deque_PushBack(d%, x#, y#, z#):"_Deque_PushBack@16"
Deque_PushFront(d%, x#, y#, z#):"_Deque_PushFront@16"
Deque_PopBack(d%):"_Deque_PopBack@4"
Deque_PopFront(d%):"_Deque_PopFront@4"
Deque_GetData(d%, index%, xptr%, yptr%, zptr%):"_Deque_GetData@20"
GetDequeSize%(d%):"_GetDequeSize@4"
ResizeDeque(d%, size%):"_ResizeDeque@8"
FreeDeque(d%):"_FreeDeque@4"

CountSplitString%(txt$, sep$):"_CountSplitString@8"
SplitString$(txt$, sep$, index%):"_SplitString@12"
CallHandleFunction(funcptr%, hnd%):"_CallHandleFunction@8"
SimulateMouseMovement(xspeed%, yspeed%):"_SimulateMouseMovement@8"
StrHex%(hex$):"_StrHex@4"

FileHash$(filename$):"_FileHash@4"
GetHardwareInfo$():"_GetHardwareInfo@0"
GetHardwareUUID$():"_GetHardwareUUID@0"
; Sort
PushSortValue_Float(identifier%, val#):"_PushSortValue_Float@8"
PushSortValue_Int(identifier%, val%):"_PushSortValue_Int@8"
PushSortValue_String(identifier%, val$):"_PushSortValue_String@8"
SortStringValues(descending%):"_SortStringValues@4"
SortIntValues(descending%):"_SortIntValues@4"
SortFloatValues(descending%):"_SortFloatValues@4"
GetSortIdentifier%(index%):"_GetSortIdentifier@4"
GetSortValue_String$(index%):"_GetSortValue_String@4"
GetSortValue_Int%(index%):"_GetSortValue_Int@4"
GetSortValue_Float#(index%):"_GetSortValue_Float@4"
ClearSortValues():"_ClearSortValues@0"

; Console
CreateConsole():"_CreateConsole@0"
ConsoleTitle(title$):"_ConsoleTitle@4"
ConsoleInput$(message$):"_ConsoleInput@4"
ConsoleColor(col%):"_ConsoleColor@4"
ConsoleMessage(message$):"_ConsoleMessage@4"
RemoveConsole():"_CreateConsole@0"