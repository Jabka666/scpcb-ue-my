Type Difficulty
	Field Name$
	Field Description$
	Field PermaDeath%
	Field AggressiveNPCs%
	Field SaveType%
	Field OtherFactors%
	Field Customizable%
	Field R%, G%, B%
	Field Locked%
End Type

Global difficulties.Difficulty[4]

Global SelectedDifficulty.Difficulty

; ~ Difficulty Constants
;[Block]
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
				opt\DifficultiesLocked = 2
				SetDifficultyColor(EUCLID, 200, 200, 0)
			EndIf
			;[End Block]
		Case "Euclid"
			;[Block]
			If difficulties[KETER]\Locked Then
				difficulties[KETER]\Locked = False
				opt\DifficultiesLocked = 1
				SetDifficultyColor(KETER, 200, 0, 0)
			EndIf
			;[End Block]
		Case "Keter"
			;[Block]
			If difficulties[ESOTERIC]\Locked Then
				difficulties[ESOTERIC]\Locked = False
				opt\DifficultiesLocked = 0
				SetDifficultyColor(ESOTERIC, 255, 255, 255)
			EndIf
			;[End Block]
	End Select
	PutINIValue(OptionFile, "Global", "Difficulties Locked", opt\DifficultiesLocked)
End Function

difficulties[SAFE] = New Difficulty
difficulties[SAFE]\Name = "Safe"
difficulties[SAFE]\Description = "The game can be saved any time. However, as in the case of SCP Objects, a Safe classification does not mean that handling it does not pose a threat."
difficulties[SAFE]\PermaDeath = False
difficulties[SAFE]\AggressiveNPCs = False
difficulties[SAFE]\SaveType = SAVEANYWHERE
difficulties[SAFE]\OtherFactors = EASY
difficulties[SAFE]\Locked = False
SetDifficultyColor(SAFE, 120, 150, 50)

difficulties[EUCLID] = New Difficulty
difficulties[EUCLID]\Name = "Euclid"
difficulties[EUCLID]\Description = "In Euclid difficulty, saving is only allowed at specific locations marked by lit up computer screens. "
difficulties[EUCLID]\Description = difficulties[EUCLID]\Description + "Euclid-class objects are inherently unpredictable, so that reliable containment is not always possible."
difficulties[EUCLID]\PermaDeath = False
difficulties[EUCLID]\AggressiveNPCs = False
difficulties[EUCLID]\SaveType = SAVEONSCREENS
difficulties[EUCLID]\OtherFactors = NORMAL
If opt\DifficultiesLocked > 2 Then difficulties[EUCLID]\Locked = True
SetDifficultyColor(EUCLID, 200 - (100 * difficulties[EUCLID]\Locked), 200 - (100 * difficulties[EUCLID]\Locked), 0)

difficulties[KETER] = New Difficulty
difficulties[KETER]\Name = "Keter"
difficulties[KETER]\Description = "Keter-class objects are considered the most dangerous ones in Foundation containment. "
difficulties[KETER]\Description = difficulties[KETER]\Description + "The same can be said for this difficulty level: the SCPs are more aggressive, and you have only one life - when you die, the game is over. "
difficulties[KETER]\PermaDeath = True
difficulties[KETER]\AggressiveNPCs = True
difficulties[KETER]\SaveType = SAVEONQUIT
difficulties[KETER]\OtherFactors = HARD
If opt\DifficultiesLocked > 1 Then difficulties[KETER]\Locked = True
SetDifficultyColor(KETER, 200 - (100 * difficulties[EUCLID]\Locked), 0, 0)

difficulties[ESOTERIC] = New Difficulty
difficulties[ESOTERIC]\Name = "Esoteric"
difficulties[ESOTERIC]\PermaDeath = False
difficulties[ESOTERIC]\AggressiveNPCs = True
difficulties[ESOTERIC]\Customizable = True
difficulties[ESOTERIC]\SaveType = SAVEANYWHERE
difficulties[ESOTERIC]\OtherFactors = EASY
If opt\DifficultiesLocked > 0 Then difficulties[ESOTERIC]\Locked = True
SetDifficultyColor(ESOTERIC, 255 - (155 * difficulties[ESOTERIC]\Locked), 255 - (155 * difficulties[ESOTERIC]\Locked), 255 - (155 * difficulties[ESOTERIC]\Locked))

SelectedDifficulty = difficulties[SAFE]

;~IDEal Editor Parameters:
;~C#Blitz3D