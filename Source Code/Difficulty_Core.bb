Type Difficulty
	Field Name$
	Field Description$
	Field AggressiveNPCs%
	Field SaveType%
	Field OtherFactors%
	Field R%, G%, B%
	Field InventorySlots%
	Field Customizable%
End Type

Global DifficultyDMGMult#
Global difficulties.Difficulty[5]

Global SelectedDifficulty.Difficulty

; ~ Difficulties ID Constants
;[Block]
Const DIFFICULTY_SAFE% = 0
Const DIFFICULTY_EUCLID% = 1
Const DIFFICULTY_KETER% = 2
Const DIFFICULTY_APOLLYON% = 3
Const DIFFICULTY_ESOTERIC% = 4
;[End Block]

; ~ Save Types ID Constants
;[Block]
Const DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE% = 0
Const DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS% = 1
Const DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT% = 2
Const DIFFICULTY_SAVE_TYPE_NO_SAVES% = 3
;[End Block]

; ~ Other Factors ID Constants
;[Block]
Const DIFFICULTY_FACTOR_EASY% = 0
Const DIFFICULTY_FACTOR_NORMAL% = 1
Const DIFFICULTY_FACTOR_HARD% = 2
Const DIFFICULTY_FACTOR_EXTREME% = 3
;[End Block]

Function CreateDifficulty.Difficulty(Name$, Description$, AggressiveNPCs%, InventorySlots%, SaveType%, OtherFactors%, R%, G%, B%, Customizable% = False)
	Local difficulty.Difficulty = New Difficulty
	
	difficulty\Name = Name
	difficulty\Description = Description
	difficulty\AggressiveNPCs = AggressiveNPCs
	difficulty\InventorySlots = InventorySlots
	difficulty\SaveType = SaveType
	difficulty\OtherFactors = OtherFactors
	difficulty\R = R
	difficulty\G = G
	difficulty\B = B
	difficulty\Customizable = Customizable
	
	Return(difficulty)
End Function

; ~ Configure difficulties
;[Block]
difficulties[DIFFICULTY_SAFE] = CreateDifficulty(GetLocalString("menu", "new.safe"), GetLocalString("msg", "diff.safe"), False, 10, DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE, DIFFICULTY_FACTOR_EASY, 120, 150, 50)
difficulties[DIFFICULTY_EUCLID] = CreateDifficulty(GetLocalString("menu", "new.euclid"), GetLocalString("msg", "diff.euclid"), False, 8, DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS, DIFFICULTY_FACTOR_NORMAL, 200, 200, 50)
difficulties[DIFFICULTY_KETER] = CreateDifficulty(GetLocalString("menu", "new.keter"), GetLocalString("msg", "diff.keter"), True, 6, DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT, DIFFICULTY_FACTOR_HARD, 200, 50, 50)
difficulties[DIFFICULTY_APOLLYON] = CreateDifficulty(GetLocalString("menu", "new.apollyon"), GetLocalString("msg", "diff.apollyon"), True, 2, DIFFICULTY_SAVE_TYPE_NO_SAVES, DIFFICULTY_FACTOR_EXTREME, 150, 150, 150)
difficulties[DIFFICULTY_ESOTERIC] = CreateDifficulty(GetLocalString("menu", "new.esoteric"), "", False, 10, DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE, DIFFICULTY_FACTOR_EASY, 200, 50, 200, True)
;[End Block]

SelectedDifficulty = difficulties[(Not opt\DebugMode)] ; ~ NOTICE: Const DIFFICULTY_SAFE% = 0 and Const DIFFICULTY_EUCLID% = 1

;~IDEal Editor Parameters:
;~C#Blitz3D TSS