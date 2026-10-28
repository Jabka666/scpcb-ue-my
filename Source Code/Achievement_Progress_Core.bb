Global AchievementProgressFound%
Global AchievementProgressAmount%

Function UpdateAchievementProgress%(AchvID$)
	Local itt.ItemTemplates, r.Rooms
	Local RID%, ShouldCount%
	
	AchievementProgressFound = 0
	AchievementProgressAmount = 0
	
	Select AchvID
		Case "idcollector"
			For itt.ItemTemplates = Each ItemTemplates
				If itt\ID = it_badge Lor itt\ID = it_badge2
					AchievementProgressAmount += 1
					AchievementProgressFound += itt\Found
				EndIf
			Next
		Case "explorer"
			For r.Rooms = Each Rooms
				RID = r\RoomTemplate\RoomID
				
				If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499
					AchievementProgressAmount += 1
					AchievementProgressFound += r\Found
				EndIf
			Next
		Case "archivist"
			For itt.ItemTemplates = Each ItemTemplates
				If itt\ID = it_paper
					ShouldCount = (Not (itt\Name = "Leaflet" Lor itt\Name = "Drawing" Lor itt\Name = "Blank Paper" Lor (itt\Name = "Note from Maynard #1" And I_005\ChanceToSpawn <> 3.0)))
					If ShouldCount
						AchievementProgressAmount += 1
						AchievementProgressFound += itt\Found
					EndIf
				EndIf
			Next
	End Select
End Function
