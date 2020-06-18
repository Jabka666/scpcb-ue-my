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

Dim difficulties.Difficulty(4)

Global SelectedDifficulty.Difficulty

difficulties(SAFE) = New Difficulty
difficulties(SAFE)\Name = "Safe"
difficulties(SAFE)\Description = "The game can be saved any time. However, as in the case of SCP Objects, a Safe classification does not mean that handling it does not pose a threat."
difficulties(SAFE)\PermaDeath = False
difficulties(SAFE)\AggressiveNPCs = False
difficulties(SAFE)\SaveType = SAVEANYWHERE
difficulties(SAFE)\OtherFactors = EASY
difficulties(SAFE)\R = 120
difficulties(SAFE)\G = 150
difficulties(SAFE)\B = 50

difficulties(EUCLID) = New Difficulty
difficulties(EUCLID)\Name = "Euclid"
difficulties(EUCLID)\Description = "In Euclid difficulty, saving is only allowed at specific locations marked by lit up computer screens. "
difficulties(EUCLID)\Description = difficulties(EUCLID)\Description + "Euclid-class objects are inherently unpredictable, so that reliable containment is not always possible."
difficulties(EUCLID)\PermaDeath = False
difficulties(EUCLID)\AggressiveNPCs = False
difficulties(EUCLID)\SaveType = SAVEONSCREENS
difficulties(EUCLID)\OtherFactors = NORMAL
difficulties(EUCLID)\R = 200
difficulties(EUCLID)\G = 200
difficulties(EUCLID)\B = 0

difficulties(KETER) = New Difficulty
difficulties(KETER)\Name = "Keter"
difficulties(KETER)\Description = "Keter-class objects are considered the most dangerous ones in Foundation containment. "
difficulties(KETER)\Description = difficulties(KETER)\Description + "The same can be said for this difficulty level: the SCPs are more aggressive, and you have only one life - when you die, the game is over. "
difficulties(KETER)\PermaDeath = True
difficulties(KETER)\AggressiveNPCs = True
difficulties(KETER)\SaveType = SAVEONQUIT
difficulties(KETER)\OtherFactors = HARD
difficulties(KETER)\R = 200
difficulties(KETER)\G = 0
difficulties(KETER)\B = 0

difficulties(CUSTOM) = New Difficulty
difficulties(CUSTOM)\Name = "Custom"
difficulties(CUSTOM)\PermaDeath = False
difficulties(CUSTOM)\AggressiveNPCs = True
difficulties(CUSTOM)\SaveType = SAVEANYWHERE
difficulties(CUSTOM)\Customizable = True
difficulties(CUSTOM)\OtherFactors = EASY
difficulties(CUSTOM)\R = 255
difficulties(CUSTOM)\G = 255
difficulties(CUSTOM)\B = 255

SelectedDifficulty = difficulties(SAFE)

;~IDEal Editor Parameters:
;~C#Blitz3D