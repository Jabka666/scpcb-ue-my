; ~ [ITEMS]

Const ItemsPath$ = "GFX\items\"

; ~ [OPTIONS]

Const OptionFile$ = "options.ini"

; ~ [VERSION]

Const VersionNumber$ = "5.6"

; ~ [OBJECTS]

Const MaxMTModelIDAmount% = 7

Const MaxMonitorModelIDAmount% = 3

Const MaxDoorModelIDAmount% = 12

Const MaxButtonModelIDAmount% = 5

Const MaxLeverModelIDAmount% = 2

Const MaxCamModelIDAmount% = 2

Const MaxMiscModelIDAmount% = 1

Const MaxNPCModelIDAmount% = 34

; ~ [NPCs]

Const NPCtype008_1% = 1, NPCtype035_Tentacle% = 2, NPCtype049% = 3, NPCtype049_2% = 4, NPCtype066% = 5, NPCtype096% = 6
Const NPCtype106% = 7, NPCtype173% = 8, NPCtype372% = 9, NPCtype513_1% = 10, NPCtype860_2% = 11, NPCtype939% = 12
Const NPCtype966% = 13, NPCtype1499_1% = 14

Const NPCtypeApache% = 15, NPCtypeClerk% = 16, NPCtypeD% = 17, NPCtypeGuard% = 18, NPCtypeMTF% = 19, NPCtypeVehicle% = 20

; ~ [FOREST GENERATION]

Const GridSize% = 10
Const Deviation_Chance% = 40 ; ~ Out of 100
Const Branch_Chance% = 65
Const Branch_Max_Life% = 4
Const Branch_Die_Chance% = 18
Const Max_Deviation_Distance% = 3
Const Return_Chance% = 27
Const Center% = 5

; ~ [MAP]

Const MaxRoomLights% = 32
Const MaxRoomEmitters% = 8
Const MaxRoomObjects% = 30

Const ROOM1% = 1
Const ROOM2% = 2
Const ROOM2C% = 3
Const ROOM3% = 4
Const ROOM4% = 5

Const ZONEAMOUNT% = 3

Const GridSZ% = 19 ; ~ Same size as the main map itself (better for the map creator)

Const RoomScale# = 8.0 / 2048.0

; ~ [COLLISIONS]

Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_178% = 5
Const HIT_DEAD% = 6

; ~ [MATH]

Const INFINITY# = 999.0 ^ 99999.0
Const NAN# = (-1.0) ^ 0.5

; ~ [TEXTURES]

Const MaxDTextures% = 16

Const MaxMiscTextureIDAmount% = 18

Const MaxMonitorTextureIDAmount% = 5

Const MaxOverlayTextureIDAmount% = 11
Const MaxOverlayIDAmount% = 11

Const MaxDecalTextureIDAmount% = 20

Const MaxParticleTextureIDAmount% = 9

Const MaxLightSpriteIDAmount% = 3

Const MaxIconIDAmount% = 6
Const MaxImageIDAmount% = 13

; ~ [ITEMS]

Const MaxItemAmount% = 10

Const NAV_WIDTH% = 287
Const NAV_HEIGHT% = 256

; ~ [FULLSCREEN FIX]

Const C_GWL_STYLE% = -16
Const C_WS_POPUP% = $80000000
Const C_HWND_TOP% = 0
Const C_SWP_SHOWWINDOW% = $0040

; ~ [SOUNDS]

Const Freq% = 44100 ; ~ HZ
Const Channels% = 64 ; ~ Standartwert
Const Flags% = 0
Const Mode% = 2 ; ~ Mode = 2 (Means that the sounds play in a loop)
Const F_Offset% = 0
Const Lenght% = 0
Const MaxVol% = 255
Const MinVol% = 0
Const PanLeft% = 0
Const PanRight% = 255
Const PanMid% = -1
Const AllChannel% = -3
Const FreeChannel% = -1

; ~ [DIFFICULTY]

Const SAFE% = 0
Const EUCLID% = 1
Const KETER% = 2
Const ESOTERIC% = 3

Const SAVEANYWHERE% = 0
Const SAVEONQUIT% = 1
Const SAVEONSCREENS% = 2

Const EASY% = 0
Const NORMAL% = 1
Const HARD% = 2

; ~ [ACHIEVEMENTS]

Const MAXACHIEVEMENTS% = 41

Const Achv005% = 0, Achv008% = 1, Achv012% = 2, Achv035% = 3, Achv049% = 4, Achv055% = 5,  Achv079% = 6, Achv096% = 7, Achv106% = 8
Const Achv148% = 9, Achv205% = 10, Achv294% = 11, Achv372% = 12, Achv409% = 13, Achv420J% = 14, Achv427% = 15, Achv500% = 16, Achv513% = 17
Const Achv714% = 18, Achv789J% = 19, Achv860% = 20, Achv895% = 21, Achv914% = 22, Achv939% = 23, Achv966% = 24, Achv970% = 25
Const Achv1025% = 26, Achv1048% = 27, Achv1123% = 28, Achv1162% = 29, Achv1499% = 30

Const AchvConsole% = 31, AchvHarp% = 32, AchvKeter% = 33, AchvKeyCard6% = 34, AchvMaynard% = 35, AchvOmni% = 36
Const AchvO5% = 37, AchvPD% = 38, AchvSNAV% = 39, AchvTesla% = 40

; ~ [FONTS]

Const MaxFontIDAmount% = 5

Const MaxCreditsFontIDAmount% = 2

; ~ [PLAYER]

Const SubjectName$ = "Subject D-9341"

; ~ [LAUNCHER]

Const LauncherWidth% = 640
Const LauncherHeight% = 480

;~IDEal Editor Parameters:
;~C#Blitz3D