; ~ [OPTIONS]

Const OptionFile$ = "options.ini"

; ~ [VERSION]

Const VersionNumber$ = "5.6"

; ~ [NPCs]

Const NPCtype008% = 1, NPCtype049% = 2, NPCtype066% = 3, NPCtype096% = 4, NPCtype173% = 5
Const NPCtype372% = 6, NPCtype5131% = 7, NPCtype860% = 8, NPCtype939% = 9, NPCtype966% = 10, NPCtype1499% = 11

Const NPCtypeApache% = 12, NPCtypeClerk% = 13, NPCtypeD% = 14, NPCtypeGuard% = 15, NPCtypeMTF% = 16, NPCtypeOldman% = 17
Const NPCtypeTentacle% = 18, NPCtypeZombie% = 19

; ~ [FOREST GENERATION]

Const GridSize% = 10
Const Deviation_Chance% = 40 ; ~ Out of 100
Const Branch_Chance% = 65
Const Branch_Max_Life% = 4
Const Branch_Die_Chance% = 18
Const Max_Deviation_Distance% = 3
Const Return_Chance% = 27
Const Center% = 5

; ~ [MAP GENERATION]

Const MaxRoomLights% = 32
Const MaxRoomEmitters% = 8
Const MaxRoomObjects% = 30

Const ROOM1% = 1
Const ROOM2% = 2
Const ROOM2C% = 3
Const ROOM3% = 4
Const ROOM4% = 5

Const ZONEAMOUNT% = 3

Const GridSZ% = 19 ; ~ Same size as the main map itself (better for the map creator). What means "sz? - Jabka

; ~ [COLLISIONS]

Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_178% = 5
Const HIT_DEAD% = 6

; ~ [MATH]

Const INFINITY# = (999.0) ^ (99999.0)
Const NAN# = (-1.0) ^ (0.5)

; ~ [TEXTURES]

Const MaxDTextures% = 14

; ~ [ITEMS]

Const MaxItemAmount% = 10

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
Const CUSTOM% = 3

Const SAVEANYWHERE% = 0
Const SAVEONQUIT% = 1
Const SAVEONSCREENS% = 2

Const EASY% = 0
Const NORMAL% = 1
Const HARD% = 2

; ~ [ACHIEVEMENTS]

Const MAXACHIEVEMENTS% = 37

Const Achv008% = 0, Achv012% = 1, Achv035% = 2, Achv049% = 3, Achv055% = 4,  Achv079% = 5, Achv096% = 6, Achv106% = 7
Const Achv148% = 8, Achv205% = 9, Achv294% = 10, Achv372% = 11, Achv420J% = 12, Achv427% = 13, Achv500% = 14, Achv513% = 15
Const Achv714% = 16, Achv789% = 17, Achv860% = 18, Achv895% = 19, Achv914% = 20, Achv939% = 21, Achv966% = 22, Achv970% = 23
Const Achv1025% = 24, Achv1048% = 25, Achv1123% = 26, Achv1162% = 27, Achv1499% = 28

Const AchvConsole% = 29, AchvHarp% = 30, AchvKeter% = 31, AchvMaynard% = 32, AchvOmni% = 33, AchvPD% = 34, AchvSNAV% = 25, AchvTesla% = 36

;~IDEal Editor Parameters:
;~C#Blitz3D