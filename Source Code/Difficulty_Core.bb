Type Difficulty
	Field Name$
	Field Description$
	Field AggressiveNPCs%
	Field SaveType%
	Field OtherFactors%
	Field Customizable%
	Field R%, G%, B%
	Field Locked%
	Field InventorySlots%
End Type

Global difficulties.Difficulty[5]

Global SelectedDifficulty.Difficulty

; ~ Difficulty Constants
;[Block]
Const SAFE% = 0
Const EUCLID% = 1
Const KETER% = 2
Const APOLLYON% = 3
Const ESOTERIC% = 4

Const SAVEANYWHERE% = 0
Const SAVEONQUIT% = 1
Const SAVEONSCREENS% = 2
Const NOSAVES% = 3

Const EASY% = 0
Const NORMAL% = 1
Const HARD% = 2
Const EXTREME% = 3
;[End Block]

Function SetDifficultyColor(ID%, R%, G%, B%)
	difficulties[ID]\R = R
	difficulties[ID]\G = G
	difficulties[ID]\B = B
End Function

Function UnlockDifficulties()
	Select SelectedDifficulty\Name
		Case "Safe"
			;[Block]
			If difficulties[EUCLID]\Locked Then
				difficulties[EUCLID]\Locked = False
				SetDifficultyColor(EUCLID, 200, 200, 0)
				opt\DifficultiesLocked = 3
			EndIf
			;[End Block]
		Case "Euclid"
			;[Block]
			If difficulties[KETER]\Locked Then
				difficulties[KETER]\Locked = False
				SetDifficultyColor(KETER, 200, 0, 0)
				opt\DifficultiesLocked = 2
			EndIf
			;[End Block]
		Case "Keter"
			;[Block]
			If difficulties[APOLLYON]\Locked Then
				difficulties[APOLLYON]\Locked = False
				SetDifficultyColor(APOLLYON, 110, 0, 0)
				opt\DifficultiesLocked = 1
			EndIf
			;[End Block]
		Case "Apollyon"
			;[Block]
			If difficulties[ESOTERIC]\Locked Then
				difficulties[ESOTERIC]\Locked = False
				; ~ ESOTERIC color already set
				opt\DifficultiesLocked = 1
			EndIf
			;[End Block]
	End Select
	PutINIValue(OptionFile, "Global", "Difficulties Locked", opt\DifficultiesLocked)
End Function

difficulties[SAFE] = New Difficulty
difficulties[SAFE]\Name = "Safe"
difficulties[SAFE]\Description = "The game can be saved any time. However, as in the case of SCP Objects, a Safe classification does not mean that handling it does not pose a threat."
difficulties[SAFE]\AggressiveNPCs = False
difficulties[SAFE]\InventorySlots = 10
difficulties[SAFE]\SaveType = SAVEANYWHERE
difficulties[SAFE]\OtherFactors = EASY
difficulties[SAFE]\Locked = False
SetDifficultyColor(SAFE, 120, 150, 50)

difficulties[EUCLID] = New Difficulty
difficulties[EUCLID]\Name = "Euclid"
difficulties[EUCLID]\Description = "In Euclid difficulty, saving is only allowed at specific locations marked by lit up computer screens. "
difficulties[EUCLID]\Description = difficulties[EUCLID]\Description + "Euclid-class objects are inherently unpredictable, so that reliable containment is not always possible."
difficulties[EUCLID]\AggressiveNPCs = False
difficulties[EUCLID]\InventorySlots = 6
difficulties[EUCLID]\SaveType = SAVEONSCREENS
difficulties[EUCLID]\OtherFactors = NORMAL
If opt\DifficultiesLocked > 3 Then difficulties[EUCLID]\Locked = True
SetDifficultyColor(EUCLID, 200 - (100 * difficulties[EUCLID]\Locked), 200 - (100 * difficulties[EUCLID]\Locked), 0)

difficulties[KETER] = New Difficulty
difficulties[KETER]\Name = "Keter"
difficulties[KETER]\Description = "Keter-class objects are considered the most dangerous ones in Foundation containment. "
difficulties[KETER]\Description = difficulties[KETER]\Description + "The same can be said for this difficulty level: the SCPs are more aggressive, and you have only one life - when you die, the game is over."
difficulties[KETER]\AggressiveNPCs = True
difficulties[KETER]\InventorySlots = 4
difficulties[KETER]\SaveType = SAVEONQUIT
difficulties[KETER]\OtherFactors = HARD
If opt\DifficultiesLocked > 2 Then difficulties[KETER]\Locked = True
SetDifficultyColor(KETER, 200 - (100 * difficulties[KETER]\Locked), 0, 0)

difficulties[APOLLYON] = New Difficulty
difficulties[APOLLYON]\Name = "Apollyon"
difficulties[APOLLYON]\Description = "Apollyon-class object is either completely impossible to contain or about to irrevocably breach containment, resulting in unimaginable consequences. "
difficulties[APOLLYON]\Description = difficulties[APOLLYON]\Description + "God help the humble subject attempting this difficulty."
difficulties[APOLLYON]\AggressiveNPCs = True
difficulties[APOLLYON]\InventorySlots = 2
difficulties[APOLLYON]\SaveType = NOSAVES
difficulties[APOLLYON]\OtherFactors = EXTREME
If opt\DifficultiesLocked > 1 Then difficulties[APOLLYON]\Locked = True
SetDifficultyColor(APOLLYON, 150 - (50 * difficulties[APOLLYON]\Locked), 150 - (50 * difficulties[APOLLYON]\Locked), 150 - (50 * difficulties[APOLLYON]\Locked))

difficulties[ESOTERIC] = New Difficulty
difficulties[ESOTERIC]\Name = "Esoteric"
difficulties[ESOTERIC]\AggressiveNPCs = True
difficulties[ESOTERIC]\InventorySlots = 10
difficulties[ESOTERIC]\Customizable = True
difficulties[ESOTERIC]\SaveType = SAVEANYWHERE
difficulties[ESOTERIC]\OtherFactors = EASY
If opt\DifficultiesLocked > 0 Then difficulties[ESOTERIC]\Locked = True
SetDifficultyColor(ESOTERIC, 200, 50, 200) ; ~ ESOTERIC is hidden difficulty, so don't change the color

SelectedDifficulty = difficulties[SAFE]

;~IDEal Editor Parameters:
;~C#Blitz3D