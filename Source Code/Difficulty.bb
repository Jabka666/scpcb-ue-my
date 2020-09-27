Type Difficulty
	Field Name$
	Field Description$
	Field PermaDeath%
	Field AggressiveNPCs%
	Field SaveType%
	Field OtherFactors%
	Field Customizable%
	Field R%
	Field G%
	Field B%
End Type

Global difficulties.Difficulty[4]

Global SelectedDifficulty.Difficulty

; ~ Difficulty Settings Constants
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

difficulties[SAFE] = New Difficulty
difficulties[SAFE]\Name = "Safe"
difficulties[SAFE]\Description = "The game can be saved any time. However, as in the case of SCP Objects, a Safe classification does not mean that handling it does not pose a threat."
difficulties[SAFE]\PermaDeath = False
difficulties[SAFE]\AggressiveNPCs = False
difficulties[SAFE]\SaveType = SAVEANYWHERE
difficulties[SAFE]\OtherFactors = EASY
difficulties[SAFE]\R = 120
difficulties[SAFE]\G = 150
difficulties[SAFE]\B = 50

difficulties[EUCLID] = New Difficulty
difficulties[EUCLID]\Name = "Euclid"
difficulties[EUCLID]\Description = "In Euclid difficulty, saving is only allowed at specific locations marked by lit up computer screens. "
difficulties[EUCLID]\Description = difficulties[EUCLID]\Description + "Euclid-class objects are inherently unpredictable, so that reliable containment is not always possible."
difficulties[EUCLID]\PermaDeath = False
difficulties[EUCLID]\AggressiveNPCs = False
difficulties[EUCLID]\SaveType = SAVEONSCREENS
difficulties[EUCLID]\OtherFactors = NORMAL
difficulties[EUCLID]\R = 200
difficulties[EUCLID]\G = 200
difficulties[EUCLID]\B = 0

difficulties[KETER] = New Difficulty
difficulties[KETER]\Name = "Keter"
difficulties[KETER]\Description = "Keter-class objects are considered the most dangerous ones in Foundation containment. "
difficulties[KETER]\Description = difficulties[KETER]\Description + "The same can be said for this difficulty level: the SCPs are more aggressive, and you have only one life - when you die, the game is over. "
difficulties[KETER]\PermaDeath = True
difficulties[KETER]\AggressiveNPCs = True
difficulties[KETER]\SaveType = SAVEONQUIT
difficulties[KETER]\OtherFactors = HARD
difficulties[KETER]\R = 200
difficulties[KETER]\G = 0
difficulties[KETER]\B = 0

difficulties[ESOTERIC] = New Difficulty
difficulties[ESOTERIC]\Name = "Esoteric"
difficulties[ESOTERIC]\PermaDeath = False
difficulties[ESOTERIC]\AggressiveNPCs = True
difficulties[ESOTERIC]\SaveType = SAVEANYWHERE
difficulties[ESOTERIC]\Customizable = True
difficulties[ESOTERIC]\OtherFactors = EASY
difficulties[ESOTERIC]\R = 255
difficulties[ESOTERIC]\G = 255
difficulties[ESOTERIC]\B = 255

SelectedDifficulty = difficulties[SAFE]

;~IDEal Editor Parameters:
;~C#Blitz3D