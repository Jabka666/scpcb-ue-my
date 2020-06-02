Global MenuBack% = LoadImage_Strict("GFX\menu\back.png")
Global MenuText% = LoadImage_Strict("GFX\menu\SCP_text.png")
Global Menu173% = LoadImage_Strict("GFX\menu\scp_173_back.png")
MenuWhite = LoadImage_Strict("GFX\menu\menu_white.png")
MenuBlack = LoadImage_Strict("GFX\menu\menu_black.png")
MaskImage(MenuBlack, 255, 255, 0)
Global QuickLoadIcon% = LoadImage_Strict("GFX\menu\QuickLoading.png")

ResizeImage(MenuBack, ImageWidth(MenuBack) * MenuScale, ImageHeight(MenuBack) * MenuScale)
ResizeImage(MenuText, ImageWidth(MenuText) * MenuScale, ImageHeight(MenuText) * MenuScale)
ResizeImage(Menu173, ImageWidth(Menu173) * MenuScale, ImageHeight(Menu173) * MenuScale)
ResizeImage(QuickLoadIcon, ImageWidth(QuickLoadIcon) * MenuScale, ImageHeight(QuickLoadIcon) * MenuScale)

For i = 0 To 3
	ArrowIMG(i) = LoadImage_Strict("GFX\menu\arrow.png")
	RotateImage(ArrowIMG(i), 90.0 * i)
	HandleImage(ArrowIMG(i), 0, 0)
Next

Global RandomSeed$

Dim MenuBlinkTimer%(2), MenuBlinkDuration%(2)
MenuBlinkTimer%(0) = 1
MenuBlinkTimer%(1) = 1

Global MenuStr$, MenuStrX%, MenuStrY%

Global MainMenuTab%

Global SelectedInputBox%

Global SavePath$ = "Saves\"
Global SaveMSG$

Global CurrSave$
Global SaveGameAmount%

Dim SaveGames$(SaveGameAmount + 1) 
Dim SaveGameTime$(SaveGameAmount + 1)
Dim SaveGameDate$(SaveGameAmount + 1)
Dim SaveGameVersion$(SaveGameAmount + 1)

Global SavedMapsAmount% = 0
Dim SavedMaps$(SavedMapsAmount + 1)
Dim SavedMapsAuthor$(SavedMapsAmount + 1)

Global SelectedMap$

LoadSaveGames()

Global CurrLoadGamePage% = 0

Function UpdateMainMenu()
	Local x%, y%, Width%, Height%, Temp%, i%
	Local fo.Fonts = First Fonts
	Local fpst.FramesPerSecondsTemplate = First FramesPerSecondsTemplate
	
	Color(0, 0, 0)
	Rect(0, 0, GraphicWidth, GraphicHeight, True)
	
	ShowPointer()
	
	DrawImage(MenuBack, 0, 0)
	
	If (MilliSecs2() Mod MenuBlinkTimer(0)) >= Rand(MenuBlinkDuration(0)) Then
		DrawImage(Menu173, GraphicWidth - ImageWidth(Menu173), GraphicHeight - ImageHeight(Menu173))
	EndIf
	
	If Rand(300) = 1 Then
		MenuBlinkTimer(0) = Rand(4000, 8000)
		MenuBlinkDuration(0) = Rand(200, 500)
	End If
	
	AASetFont(fo\FontID[0])
	
	MenuBlinkTimer(1) = MenuBlinkTimer(1) - fpst\FPSFactor[0]
	If MenuBlinkTimer(1) < MenuBlinkDuration(1) Then
		Color(50, 50, 50)
		AAText(MenuStrX + Rand(-5, 5), MenuStrY + Rand(-5, 5), MenuStr, True)
		If MenuBlinkTimer(1) < 0 Then
			MenuBlinkTimer(1) = Rand(700, 800)
			MenuBlinkDuration(1) = Rand(10, 35)
			MenuStrX = Rand(700, 1000) * MenuScale
			MenuStrY = Rand(100, 600) * MenuScale
			
			Select Rand(0, 23)
				Case 0, 2, 3
					;[Block]
					MenuStr = "DON'T BLINK"
					;[End Block]
				Case 4, 5
					;[Block]
					MenuStr = "Secure. Contain. Protect."
					;[End Block]
				Case 6, 7, 8
					;[Block]
					MenuStr = "You want happy endings? Fuck you."
					;[End Block]
				Case 9, 10, 11
					;[Block]
					MenuStr = "Sometimes we would have had time to scream."
					;[End Block]
				Case 12, 19
					;[Block]
					MenuStr = "NIL"
					;[End Block]
				Case 13
					;[Block]
					MenuStr = "NO"
					;[End Block]
				Case 14
					;[Block]
					MenuStr = "black white black white black white gray"
					;[End Block]
				Case 15
					;[Block]
					MenuStr = "Stone does not care"
					;[End Block]
				Case 16
					;[Block]
					MenuStr = "9341"
					;[End Block]
				Case 17
					;[Block]
					MenuStr = "It controls the doors"
					;[End Block]
				Case 18
					;[Block]
					MenuStr = "e8m106]af173o+079m895w914"
					;[End Block]
				Case 20
					;[Block]
					MenuStr = "It has taken over everything"
					;[End Block]
				Case 21
					;[Block]
					MenuStr = "The spiral is growing"
					;[End Block]
				Case 22
					;[Block]
					MenuStr = Chr(34) + "Some kind of gestalt effect due to massive reality damage." + Chr(34)
					;[End Block]
				Case 23
					;[Block]
					MenuStr = "Howls the Black Moon? Yes. No. Yes. No."
					;[End Block]
			End Select
		EndIf
	EndIf
	
	AASetFont(fo\FontID[1])
	
	DrawImage(MenuText, GraphicWidth / 2 - ImageWidth(MenuText) / 2, GraphicHeight - 20 * MenuScale - ImageHeight(MenuText))
	
	If GraphicWidth > 1240 * MenuScale Then
		DrawTiledImageRect(MenuWhite, 0, 5, 512, 7 * MenuScale, 985.0 * MenuScale, 407.0 * MenuScale, (GraphicWidth - 1240 * MenuScale) + 300, 7 * MenuScale)
	EndIf
	
	If (Not MouseDown1)
		OnSliderID = 0
	EndIf
	
	If MainMenuTab = 0 Then
		For i = 0 To 3
			Temp = False
			x = 159 * MenuScale
			y = (286 + 100 * i) * MenuScale
			
			Width = 400 * MenuScale
			Height = 70 * MenuScale
			
			Temp = (MouseHit1 And MouseOn(x, y, Width, Height))
			
			Local Txt$
			
			Select i
				Case 0
					;[Block]
					Txt = "NEW GAME"
					RandomSeed = ""
					If Temp Then 
						If Rand(15) = 1 Then 
							Select Rand(13)
								Case 1 
									;[Block]
									RandomSeed = "NIL"
									;[End Block]
								Case 2
									;[Block]
									RandomSeed = "NO"
									;[End Block]
								Case 3
									;[Block]
									RandomSeed = "d9341"
									;[End Block]
								Case 4
									;[Block]
									RandomSeed = "5CP_I73"
									;[End Block]
								Case 5
									;[Block]
									RandomSeed = "DONTBLINK"
									;[End Block]
								Case 6
									;[Block]
									RandomSeed = "CRUNCH"
									;[End Block]
								Case 7
									;[Block]
									RandomSeed = "die"
									;[End Block]
								Case 8
									;[Block]
									RandomSeed = "HTAED"
									;[End Block]
								Case 9
									;[Block]
									RandomSeed = "rustledjim"
									;[End Block]
								Case 10
									;[Block]
									RandomSeed = "larry"
									;[End Block]
								Case 11
									;[Block]
									RandomSeed = "JORGE"
									;[End Block]
								Case 12
									;[Block]
									RandomSeed = "dirtymetal"
									;[End Block]
								Case 13
									;[Block]
									RandomSeed = "whatpumpkin"
									;[End Block]
							End Select
						Else
							n = Rand(4, 8)
							For i = 1 To n
								If Rand(3) = 1 Then
									RandomSeed = RandomSeed + Rand(0, 9)
								Else
									RandomSeed = RandomSeed + Chr(Rand(97, 122))
								EndIf
							Next							
						EndIf
						MainMenuTab = 1
					EndIf
					;[End Block]
				Case 1
					;[Block]
					Txt = "LOAD GAME"
					If Temp Then
						LoadSaveGames()
						MainMenuTab = 2
					EndIf
					;[End Block]
				Case 2
					;[Block]
					Txt = "OPTIONS"
					If Temp Then MainMenuTab = 3
					;[End Block]
				Case 3
					;[Block]
					Txt = "QUIT"
					If Temp Then
						FSOUND_Stream_Stop(CurrMusicStream)
						End
					EndIf
					;[End Block]
			End Select
			
			DrawButton(x, y, Width, Height, Txt)
		Next	
	Else
		x = 159 * MenuScale
		y = 286 * MenuScale
		
		Width = 400 * MenuScale
		Height = 70 * MenuScale
		
		DrawFrame(x, y, Width, Height)
		
		If DrawButton(x + Width + 20 * MenuScale, y, 580 * MenuScale - Width - 20 * MenuScale, Height, "BACK", False) Then 
			Select MainMenuTab
				Case 1
					;[Block]
					PutINIValue(OptionFile, "Global", "Enable Intro", IntroEnabled)
					MainMenuTab = 0
					;[End Block]
				Case 2
					;[Block]
					CurrLoadGamePage = 0
					MainMenuTab = 0
					;[End Block]
				Case 3, 5, 6, 7 ; ~ Save the options
					;[Block]
					SaveOptionsINI()
					
					UserTrackCheck = 0
					UserTrackCheck2 = 0
					
					AntiAlias(Opt_AntiAlias)
					MainMenuTab = 0
					;[End Block]
				Case 4 ; ~ Move back to the "New Game" tab
					;[Block]
					MainMenuTab = 1
					CurrLoadGamePage = 0
					MouseHit1 = False
					;[End Block]
				Default
					;[Block]
					MainMenuTab = 0
					;[End Block]
			End Select
		EndIf
		
		Select MainMenuTab
			Case 1 ; ~ New Game
				;[Block]
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				Width = 400 * MenuScale
				Height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont(fo\FontID[1])
				AAText(x + Width / 2, y + Height / 2, "NEW GAME", True, True)
				
				x = 160 * MenuScale
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 330 * MenuScale
				
				DrawFrame(x, y, Width, Height)				
				
				AASetFont(fo\FontID[0])
				
				AAText(x + 20 * MenuScale, y + 20 * MenuScale, "Name:")
				CurrSave = InputBox(x + 150 * MenuScale, y + 15 * MenuScale, 200 * MenuScale, 30 * MenuScale, CurrSave, 1)
				CurrSave = Left(CurrSave, 15)
				CurrSave = Replace(CurrSave, ":", "")
				CurrSave = Replace(CurrSave, ".", "")
				CurrSave = Replace(CurrSave, "/", "")
				CurrSave = Replace(CurrSave, "\", "")
				CurrSave = Replace(CurrSave, "<", "")
				CurrSave = Replace(CurrSave, ">", "")
				CurrSave = Replace(CurrSave, "|", "")
				CurrSave = Replace(CurrSave, "?", "")
				CurrSave = Replace(CurrSave, Chr(34), "")
				CurrSave = Replace(CurrSave, "*", "")
				
				Color(255, 255, 255)
				If SelectedMap = "" Then
					AAText(x + 20 * MenuScale, y + 60 * MenuScale, "Map seed:")
					RandomSeed = Left(InputBox(x + 150 * MenuScale, y + 55 * MenuScale, 200 * MenuScale, 30 * MenuScale, RandomSeed, 3), 15)	
				Else
					AAText(x + 20 * MenuScale, y + 60 * MenuScale, "Selected map:")
					Color(255, 255, 255)
					Rect(x + 150 * MenuScale, y + 55 * MenuScale, 200 * MenuScale, 30 * MenuScale)
					Color(0, 0, 0)
					Rect(x + 150 * MenuScale + 2, y + 55 * MenuScale + 2, 200 * MenuScale - 4, 30 * MenuScale - 4)
					
					Color(255, 0, 0)
					If Len(SelectedMap) > 15 Then
						AAText(x + 150 * MenuScale + 100 * MenuScale, y + 55 * MenuScale + 15 * MenuScale, Left(SelectedMap, 14) + "...", True, True)
					Else
						AAText(x + 150 * MenuScale + 100 * MenuScale, y + 55 * MenuScale + 15 * MenuScale, SelectedMap, True, True)
					EndIf
					
					If DrawButton(x + 370 * MenuScale, y + 55 * MenuScale, 120 * MenuScale, 30 * MenuScale, "Deselect", False) Then
						SelectedMap = ""
					EndIf
				EndIf	
				
				AAText(x + 20 * MenuScale, y + 110 * MenuScale, "Enable intro sequence:")
				IntroEnabled = DrawTick(x + 280 * MenuScale, y + 110 * MenuScale, IntroEnabled)	
				
				AAText(x + 20 * MenuScale, y + 150 * MenuScale, "Difficulty:")				
				For i = SAFE To CUSTOM
					If DrawTick(x + 20 * MenuScale, y + (180 + 30 * i) * MenuScale, (SelectedDifficulty = difficulties(i))) Then SelectedDifficulty = difficulties(i)
					Color(difficulties(i)\R, difficulties(i)\G, difficulties(i)\B)
					AAText(x + 60 * MenuScale, y + (180 + 30 * i) * MenuScale, difficulties(i)\Name)
				Next
				
				Color(255, 255, 255)
				DrawFrame(x + 150 * MenuScale, y + 155 * MenuScale, 410 * MenuScale, 150 * MenuScale)
				
				If SelectedDifficulty\Customizable Then
					SelectedDifficulty\PermaDeath =  DrawTick(x + 160 * MenuScale, y + 165 * MenuScale, (SelectedDifficulty\PermaDeath))
					AAText(x + 200 * MenuScale, y + 165 * MenuScale, "Permadeath")
					
					If DrawTick(x + 160 * MenuScale, y + 195 * MenuScale, SelectedDifficulty\SaveType = SAVEANYWHERE And (Not SelectedDifficulty\PermaDeath), SelectedDifficulty\PermaDeath) Then 
						SelectedDifficulty\SaveType = SAVEANYWHERE
					Else
						SelectedDifficulty\SaveType = SAVEONSCREENS
					EndIf
					
					AAText(x + 200 * MenuScale, y + 195 * MenuScale, "Save anywhere")	
					
					SelectedDifficulty\AggressiveNPCs =  DrawTick(x + 160 * MenuScale, y + 225 * MenuScale, SelectedDifficulty\AggressiveNPCs)
					AAText(x + 200 * MenuScale, y + 225 * MenuScale, "Aggressive NPCs")
					
					; ~ Other factor's difficulty
					Color(255, 255, 255)
					DrawImage(ArrowIMG(1), x + 155 * MenuScale, y + 251 * MenuScale)
					If MouseHit1 Then
						If ImageRectOverlap(ArrowIMG(1), x + 155 * MenuScale, y + 251 * MenuScale, ScaledMouseX(), ScaledMouseY(), 0, 0)
							If SelectedDifficulty\OtherFactors < HARD
								SelectedDifficulty\OtherFactors = SelectedDifficulty\OtherFactors + 1
							Else
								SelectedDifficulty\otherFactors = EASY
							EndIf
							PlaySound_Strict(ButtonSFX)
						EndIf
					EndIf
					Color(255, 255, 255)
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Easy")
							;[End Block]
						Case NORMAL
							;[Block]
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Normal")
							;[End Block]
						Case HARD
							;[Block]
							AAText(x + 200 * MenuScale, y + 255 * MenuScale, "Other difficulty factors: Hard")
							;[End Block]
					End Select
				Else
					RowText(SelectedDifficulty\Description, x + 160 * MenuScale, y + 160 * MenuScale, (410 - 20) * MenuScale, 200)					
				EndIf
				
				If DrawButton(x, y + Height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "Load map", False) Then
					MainMenuTab = 4
					LoadSavedMaps()
				EndIf
				
				AASetFont(fo\FontID[1])
				
				If DrawButton(x + 420 * MenuScale, y + Height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, "START", False) Then
					If CurrSave = "" Then CurrSave = "untitled"
					
					If RandomSeed = "" Then
						RandomSeed = Abs(MilliSecs())
					EndIf
					
					SeedRnd(GenerateSeedNumber(RandomSeed))
					
					Local SameFound% = False
					
					For i = 1 To SaveGameAmount
						If SaveGames(i - 1) = CurrSave Then SameFound = SameFound + 1
					Next
						
					If SameFound > 0 Then CurrSave = CurrSave + " (" + (SameFound + 1) + ")"
					
					LoadEntities()
					LoadAllSounds()
					InitNewGame()
					MainMenuOpen = False
					FlushKeys()
					FlushMouse()
					
					PutINIValue(OptionFile, "Global", "Enable Intro", IntroEnabled)
				EndIf
				;[End Block]
			Case 2 ; ~ Load Game
				;[Block]
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 510 * MenuScale
				
				DrawFrame(x, y, Width, Height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				Width = 400 * MenuScale
				Height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont(fo\FontID[1])
				AAText(x + Width / 2, y + Height / 2, "LOAD GAME", True, True)
				
				x = 160 * MenuScale
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 296 * MenuScale
				
				AASetFont(fo\FontID[1])
				
				If CurrLoadGamePage < Ceil(Float(SaveGameAmount) / 6.0) - 1 And SaveMSG = "" Then 
					If DrawButton(x + 530 * MenuScale, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage + 1
					EndIf
				Else
					DrawFrame(x + 530 * MenuScale, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale)
					Color(100, 100, 100)
					AAText(x + 555 * MenuScale, y + 537.5 * MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 And SaveMSG = "" Then
					If DrawButton(x, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage - 1
					EndIf
				Else
					DrawFrame(x, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale)
					Color(100, 100, 100)
					AAText(x + 25 * MenuScale, y + 537.5 * MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x + 50 * MenuScale, y + 510 * MenuScale, Width - 100 * MenuScale, 55 * MenuScale)
				
				AAText(x + (Width / 2.0), y + 536 * MenuScale, "Page " + Int(Max((CurrLoadGamePage + 1), 1)) + "/" + Int(Max((Int(Ceil(Float(SaveGameAmount) / 6.0))), 1)), True, True)
				
				AASetFont(fo\FontID[0])
				
				If CurrLoadGamePage > Ceil(Float(SaveGameAmount) / 6.0) - 1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				If SaveGameAmount = 0 Then
					AAText(x + 20 * MenuScale, y + 20 * MenuScale, "No saved games.")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					
					For i = (1 + (6 * CurrLoadGamePage)) To 6 + (6 * CurrLoadGamePage)
						If i =< SaveGameAmount Then
							DrawFrame(x, y, 540 * MenuScale, 70 * MenuScale)
							
							If SaveGameVersion(i - 1) <> VersionNumber Then
								Color(255, 0, 0)
							Else
								Color(255, 255, 255)
							EndIf
							
							AAText(x + 20 * MenuScale, y + 10 * MenuScale, SaveGames(i - 1))
							AAText(x + 20 * MenuScale, y + (10 + 18) * MenuScale, SaveGameTime(i - 1))
							AAText(x + 120 * MenuScale, y + (10 + 18) * MenuScale, SaveGameDate(i - 1))
							AAText(x + 20 * MenuScale, y + (10 + 36) * MenuScale, SaveGameVersion(i - 1))
							
							If SaveMSG = "" Then
								If SaveGameVersion(i - 1) <> VersionNumber Then
									DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
									Color(255, 0, 0)
									AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
								Else
									If DrawButton(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Load", False) Then
										LoadEntities()
										LoadAllSounds()
										LoadGame(SavePath + SaveGames(i - 1) + "\")
										CurrSave = SaveGames(i - 1)
										InitLoadGame()
										MainMenuOpen = False
									EndIf
								EndIf
									
								If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Delete", False) Then
									SaveMSG = SaveGames(i - 1)
									Exit
								EndIf
							Else
								DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								If SaveGameVersion(i - 1) <> VersionNumber Then
									Color(255, 0, 0)
								Else
									Color(100, 100, 100)
								EndIf
								AAText(x + 330 * MenuScale, y + 34 * MenuScale, "Load", True, True)
								
								DrawFrame(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								Color(100, 100, 100)
								AAText(x + 450 * MenuScale, y + 34 * MenuScale, "Delete", True, True)
							EndIf
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
					
					If SaveMSG <> "" Then
						x = 740 * MenuScale
						y = 376 * MenuScale
						DrawFrame(x, y, 420 * MenuScale, 200 * MenuScale)
						RowText("Are you sure you want to delete this save?", x + 20 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
						If DrawButton(x + 50 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Yes", False) Then
							DeleteFile(CurrentDir() + SavePath + SaveMSG + "\Save.txt")
							DeleteDir(CurrentDir() + SavePath + SaveMSG)
							SaveMSG = ""
							LoadSaveGames()
						EndIf
						If DrawButton(x + 250 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, "No", False) Then
							SaveMSG = ""
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case 3, 5, 6, 7 ; ~ Options
				;[Block]
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				Width = 400 * MenuScale
				Height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont(fo\FontID[1])
				AAText(x + Width / 2, y + Height / 2, "OPTIONS", True, True)
				
				x = 160 * MenuScale
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 60 * MenuScale
				DrawFrame(x, y, Width, Height)
				
				Color(0, 255, 0)
				If MainMenuTab = 3
					Rect(x + 15 * MenuScale, y + 10 * MenuScale, (Width / 5) + 10 * MenuScale, (Height / 2) + 10 * MenuScale, True)
				ElseIf MainMenuTab = 5
					Rect(x + 155 * MenuScale, y + 10 * MenuScale, (Width / 5) + 10 * MenuScale, (Height / 2) + 10 * MenuScale, True)
				ElseIf MainMenuTab = 6
					Rect(x + 295 * MenuScale, y + 10 * MenuScale, (Width / 5) + 10 * MenuScale, (Height / 2) + 10 * MenuScale, True)
				ElseIf MainMenuTab = 7
					Rect(x + 435 * MenuScale, y + 10 * MenuScale, (Width / 5) + 10 * MenuScale, (Height / 2) + 10 * MenuScale, True)
				EndIf
				
				Color(255, 255, 255)
				If DrawButton(x + 20 * MenuScale, y + 15 * MenuScale, Width / 5, Height / 2, "GRAPHICS", False) Then MainMenuTab = 3
				If DrawButton(x + 160 * MenuScale, y + 15 * MenuScale, Width / 5, Height / 2, "AUDIO", False) Then MainMenuTab = 5
				If DrawButton(x + 300 * MenuScale, y + 15 * MenuScale, Width / 5, Height / 2, "CONTROLS", False) Then MainMenuTab = 6
				If DrawButton(x + 440 * MenuScale, y + 15 * MenuScale, Width / 5, Height / 2, "ADVANCED", False) Then MainMenuTab = 7
				
				AASetFont(fo\FontID[0])
				y = y + 70 * MenuScale
				
				If MainMenuTab <> 5 Then
					UserTrackCheck = 0
					UserTrackCheck2 = 0
				EndIf
				
				Local tX# = x + Width
				Local tY# = y
				Local tW# = 400.0 * MenuScale
				Local tH# = 150.0 * MenuScale
				
				If MainMenuTab = 3 ; ~ Graphics
					;[Block]
					Height = 410 * MenuScale
					DrawFrame(x, y, Width, Height)
					
					y = y + 20 * MenuScale
					
					Color(255, 255, 255)				
					AAText(x + 20 * MenuScale, y, "Enable bump mapping:")	
					BumpEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, BumpEnabled)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "bump")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "VSync:")
					VSync = DrawTick(x + 310 * MenuScale, y + MenuScale, VSync)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vsync")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Anti-aliasing:")
					Opt_AntiAlias = DrawTick(x + 310 * MenuScale, y + MenuScale, Opt_AntiAlias)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "antialias")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Enable room lights:")
					EnableRoomLights = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "roomlights")
					EndIf
					
					y = y + 30 * MenuScale
					
					ScreenGamma = (SlideBar(x + 310 * MenuScale, y + 6 * MenuScale, 150 * MenuScale, ScreenGamma * 50.0) / 50.0)
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Screen gamma:")
					If MouseOn(x + 310 * MenuScale, y + 6 * MenuScale, 150 * MenuScale + 14, 20) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "gamma", ScreenGamma)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Particle amount:")
					ParticleAmount = Slider3(x + 310  * MenuScale, y + 6 * MenuScale, 150 * MenuScale, ParticleAmount, 2, "MINIMAL", "REDUCED", "FULL")
					If (MouseOn(x + 310 * MenuScale, y - 6 * MenuScale, 150 * MenuScale + 14, 20) And OnSliderID = 0) Or OnSliderID = 2
						DrawOptionsTooltip(tX, tY, tW, tH, "particleamount", ParticleAmount)
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Texture LOD Bias:")
					TextureDetails = Slider5(x + 310 * MenuScale, y + 6 * MenuScale, 150 * MenuScale, TextureDetails, 3, "0.8", "0.4", "0.0", "-0.4", "-0.8")
					Select TextureDetails
						Case 0
							;[Block]
							TextureFloat = 0.8
							;[End Block]
						Case 1
							;[Block]
							TextureFloat = 0.4
							;[End Block]
						Case 2
							;[Block]
							TextureFloat = 0.0
							;[End Block]
						Case 3
							;[Block]
							TextureFloat = -0.4
							;[End Block]
						Case 4
							;[Block]
							TextureFloat = -0.8
							;[End Block]
					End Select
					TextureLodBias(TextureFloat)
					If (MouseOn(x + 310 * MenuScale, y - 6 * MenuScale, 150 * MenuScale + 14, 20) And OnSliderID = 0) Or OnSliderID = 3
						DrawOptionsTooltip(tX, tY, tW, tH + 100 * MenuScale, "texquality")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Save textures in the VRAM:")
					SaveTexturesInVRAM = DrawTick(x + 310 * MenuScale, y + MenuScale, SaveTexturesInVRAM)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale) And OnSliderID = 0
						DrawOptionsTooltip(tX, tY, tW, tH, "vram")
					EndIf
					
					y = y + 50 * MenuScale
					
					CurrFOV = (SlideBar(x + 310 * MenuScale, y + 6 * MenuScale, 150 * MenuScale, CurrFOV * 2.0) / 2.0)
					FOV = CurrFOV + 40
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Field of view:")
					Color(255, 255, 0)
					AAText(x + 25 * MenuScale, y + 25 * MenuScale, Int(FOV) + "°")
					If MouseOn(x + 310 * MenuScale, y + 6 * MenuScale, 150 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "fov")
					EndIf
					;[End Block]
				ElseIf MainMenuTab = 5 ; ~ Audio
					;[Block]
					Height = 220 * MenuScale
					DrawFrame(x, y, Width, Height)	
					
					y = y + 20 * MenuScale
					
					MusicVolume = (SlideBar(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale, MusicVolume * 100.0) / 100.0)
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Music volume:")
					If MouseOn(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "musicvol", MusicVolume)
					EndIf
					
					y = y + 40 * MenuScale
					
					PrevSFXVolume = (SlideBar(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale, SFXVolume * 100.0) / 100.0)
					SFXVolume = PrevSFXVolume
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Sound volume:")
					If MouseOn(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "soundvol", PrevSFXVolume)
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Sound auto-release:")
					EnableSFXRelease = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableSFXRelease)
					If EnableSFXRelease_Prev <> EnableSFXRelease
						If EnableSFXRelease Then
							For snd.Sound = Each Sound
								For i = 0 To 31
									If snd\Channels[i] <> 0 Then
										If ChannelPlaying(snd\Channels[i]) = True Then
											StopChannel(snd\Channels[i])
										EndIf
									EndIf
								Next
								If snd\InternalHandle <> 0 Then
									FreeSound(snd\InternalHandle)
									snd\InternalHandle = 0
								EndIf
								snd\ReleaseTime = 0
							Next
						Else
							For snd.Sound = Each Sound
								If snd\InternalHandle = 0 Then snd\InternalHandle = LoadSound(snd\Name)
							Next
						EndIf
						EnableSFXRelease_Prev = EnableSFXRelease
					EndIf
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, "sfxautorelease")
					EndIf
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Enable user tracks:")
					EnableUserTracks = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableUserTracks)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "usertrack")
					EndIf
					
					If EnableUserTracks Then
						y = y + 30 * MenuScale
						Color(255, 255, 255)
						AAText(x + 20 * MenuScale, y, "User track mode:")
						UserTrackMode = DrawTick(x + 310 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							AAText(x + 350 * MenuScale, y + MenuScale, "Repeat")
						Else
							AAText(x + 350 * MenuScale, y + MenuScale, "Random")
						EndIf
						If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "usertrackmode")
						EndIf
						If DrawButton(x + 20 * MenuScale, y + 30 * MenuScale, 190 * MenuScale, 25 * MenuScale, "Scan for User Tracks", False)
							UserTrackCheck = 0
							UserTrackCheck2 = 0
							
							Dir = ReadDir("SFX\Radio\UserTracks\")
							Repeat
								File$ = NextFile(Dir)
								If File$ = "" Then Exit
								If FileType("SFX\Radio\UserTracks\" + File$) = 1 Then
									UserTrackCheck = UserTrackCheck + 1
									Test = LoadSound("SFX\Radio\UserTracks\" + File$)
									If Test <> 0 Then
										UserTrackCheck2 = UserTrackCheck2 + 1
									EndIf
									FreeSound(Test)
								EndIf
							Forever
							CloseDir(Dir)
						EndIf
						If MouseOn(x + 20 * MenuScale, y + 30 * MenuScale, 190 * MenuScale, 25 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "usertrackscan")
						EndIf
						If UserTrackCheck > 0 Then
							AAText(x + 20 * MenuScale, y + 100 * MenuScale, "User tracks found (" + UserTrackCheck2 + "/" + UserTrackCheck + " successfully loaded)")
						EndIf
					Else
						UserTrackCheck = 0
					EndIf
					;[End Block]
				ElseIf MainMenuTab = 6 ; ~ Controls
					;[Block]
					Height = 280 * MenuScale
					DrawFrame(x, y, Width, Height)	
					
					y = y + 20 * MenuScale
					
					MouseSensitivity = (SlideBar(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale, (MouseSensitivity + 0.5) * 100.0) / 100.0) - 0.5
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Mouse sensitivity:")
					If MouseOn(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesensitivity", MouseSensitivity)
					EndIf
					
					y = y + 40 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Invert mouse Y-axis:")
					InvertMouse = DrawTick(x + 310 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "mouseinvert")
					EndIf
					
					y = y + 40 * MenuScale
					
					MouseSmoothing = (SlideBar(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale, (MouseSmoothing) * 50.0) / 50.0)
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Mouse smoothing:")
					If MouseOn(x + 310 * MenuScale, y - 4 * MenuScale, 150 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "mousesmoothing", MouseSmoothing)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30 * MenuScale
					AAText(x + 20 * MenuScale, y, "Control configuration:")
					y = y + 10 * MenuScale
					
					AAText(x + 20 * MenuScale, y + 20 * MenuScale, "Move Forward")
					InputBox(x + 160 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_UP, 210)), 5)		
					AAText(x + 20 * MenuScale, y + 40 * MenuScale, "Strafe Left")
					InputBox(x + 160 * MenuScale, y + 40 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_LEFT, 210)), 3)	
					AAText(x + 20 * MenuScale, y + 60 * MenuScale, "Move Backward")
					InputBox(x + 160 * MenuScale, y + 60 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_DOWN, 210)), 6)				
					AAText(x + 20 * MenuScale, y + 80 * MenuScale, "Strafe Right")
					InputBox(x + 160 * MenuScale, y + 80 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_RIGHT, 210)), 4)	
					AAText(x + 20 * MenuScale, y + 100 * MenuScale, "Quick Save")
					InputBox(x + 160 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_SAVE, 210)), 11)
					
					AAText(x + 280 * MenuScale, y + 20 * MenuScale, "Manual Blink")
					InputBox(x + 470 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_BLINK, 210)), 7)				
					AAText(x + 280 * MenuScale, y + 40 * MenuScale, "Sprint")
					InputBox(x + 470 * MenuScale, y + 40 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_SPRINT, 210)), 8)
					AAText(x + 280 * MenuScale, y + 60 * MenuScale, "Open/Close Inventory")
					InputBox(x + 470 * MenuScale, y + 60 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_INV, 210)), 9)
					AAText(x + 280 * MenuScale, y + 80 * MenuScale, "Crouch")
					InputBox(x + 470 * MenuScale, y + 80 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_CROUCH, 210)), 10)	
					AAText(x + 280 * MenuScale, y + 100 * MenuScale, "Open/Close Console")
					InputBox(x + 470 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 20 * MenuScale, KeyName(Min(KEY_CONSOLE, 210)), 12)
					
					If MouseOn(x + 20 * MenuScale, y, Width - 40 * MenuScale, 120 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "controls")
					EndIf
					
					Local Key%
					
					For i = 0 To 227
						If KeyHit(i) Then Key = i : Exit
					Next
					If Key <> 0 Then
						Select SelectedInputBox
							Case 3
								;[Block]
								KEY_LEFT = Key
								;[End Block]
							Case 4
								;[Block]
								KEY_RIGHT = Key
								;[End Block]
							Case 5
								;[Block]
								KEY_UP = Key
								;[End Block]
							Case 6
								;[Block]
								KEY_DOWN = Key
								;[End Block]
							Case 7
								;[Block]
								KEY_BLINK = Key
								;[End Block]
							Case 8
								;[Block]
								KEY_SPRINT = Key
								;[End Block]
							Case 9
								;[Block]
								KEY_INV = Key
								;[End Block]
							Case 10
								;[Block]
								KEY_CROUCH = Key
								;[End Block]
							Case 11
								;[Block]
								KEY_SAVE = Key
								;[End Block]
							Case 12
								;[Block]
								KEY_CONSOLE = Key
								;[End Block]
						End Select
						SelectedInputBox = 0
					EndIf
					;[End Block]
				ElseIf MainMenuTab = 7 ; ~ Advanced
					;[Block]
					Height = 330 * MenuScale
					DrawFrame(x, y, Width, Height)	
					
					y = y + 20 * MenuScale
					
					Color(255, 255, 255)				
					AAText(x + 20 * MenuScale, y, "Show HUD:")	
					HUDenabled = DrawTick(x + 310 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "hud")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Enable console:")
					CanOpenConsole = DrawTick(x + 310 * MenuScale, y + MenuScale, CanOpenConsole)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "consoleenable")
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
						Color(255, 255, 255)
						AAText(x + 20 * MenuScale, y, "Open console on error:")
						ConsoleOpening = DrawTick(x + 310 * MenuScale, y + MenuScale, ConsoleOpening)
						If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "consoleerror")
						EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					If CanOpenConsole Then
					    Color(255, 255, 255)
					    AAText(x + 20 * MenuScale, y, "Console Version:")
				        ConsoleVersion = DrawTick(x + 310 * MenuScale, y + MenuScale, ConsoleVersion)
					    If ConsoleVersion = 1 Then
							AAText(x + 350 * MenuScale, y, "New Version")
					    Else
					        AAText(x + 350 * MenuScale, y, "Classic Version")
					    EndIf    
					    If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
							DrawOptionsTooltip(tX, tY, tW, tH, "consoleversion")
					    EndIf
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Achievement popups:")
					AchvMSGenabled = DrawTick(x + 310 * MenuScale, y + MenuScale, AchvMSGenabled)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "achpopup")
					EndIf
					
					y = y + 50 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Show FPS:")
					ShowFPS = DrawTick(x + 310 * MenuScale, y + MenuScale, ShowFPS)
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "showfps")
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Framelimit:")
					Color(255, 255, 255)
					If DrawTick(x + 310 * MenuScale, y, CurrFrameLimit > 0.0) Then
						CurrFrameLimit = (SlideBar(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale, CurrFrameLimit * 99.0) / 99.0)
						CurrFrameLimit = Max(CurrFrameLimit, 0.01)
						FrameLimit = 19 + (CurrFrameLimit * 100.0)
						Color(255, 255, 0)
						AAText(x + 25 * MenuScale, y + 25 * MenuScale, FrameLimit + " FPS")
					Else
						CurrFrameLimit = 0.0
						FrameLimit = 0
					EndIf
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					If MouseOn(x + 150 * MenuScale, y + 30 * MenuScale, 100 * MenuScale + 14, 20)
						DrawOptionsTooltip(tX, tY, tW, tH, "framelimit", FrameLimit)
					EndIf
					
					y = y + 70 * MenuScale
					
					Color(255, 255, 255)
					AAText(x + 20 * MenuScale, y, "Antialiased text:")
					AATextEnable = DrawTick(x + 310 * MenuScale, y + MenuScale, AATextEnable)
					If AATextEnable_Prev <> AATextEnable
						For font.AAFont = Each AAFont
							FreeFont(font\lowResFont)
							If (Not AATextEnable)
								FreeTexture(font\Texture)
								FreeImage(font\Backup)
							EndIf
							Delete(font)
						Next
						If (Not AATextEnable) Then
							FreeEntity(AATextCam)
						EndIf
						InitAAFont()
						fo\FontID[0] = AALoadFont("GFX\font\cour\Courier New.ttf", Int(18 * (GraphicHeight / 1024.0)), 0, 0, 0)
						fo\FontID[1] = AALoadFont("GFX\font\courbd\Courier New.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)
						fo\FontID[2] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(22 * (GraphicHeight / 1024.0)), 0, 0, 0)
						fo\FontID[3] = AALoadFont("GFX\font\DS-DIGI\DS-Digital.ttf", Int(60 * (GraphicHeight / 1024.0)), 0, 0, 0)
						fo\FontID[4] = AALoadFont("GFX\font\Journal\Journal.ttf", Int(58 * (GraphicHeight / 1024.0)), 0, 0, 0)
						fo\ConsoleFont = AALoadFont("Blitz", Int(22 * (GraphicHeight / 1024.0)), 0, 0, 0, 1)
						AATextEnable_Prev = AATextEnable
					EndIf
					If MouseOn(x + 310 * MenuScale, y + MenuScale, 20 * MenuScale, 20 * MenuScale)
						DrawOptionsTooltip(tX, tY, tW, tH, "antialiastext")
					EndIf
					;[End Block]
				EndIf
				;[End Block]
			Case 4 ; ~ Load Map
				;[Block]
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 510 * MenuScale
				
				DrawFrame(x, y, Width, Height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				Width = 400 * MenuScale
				Height = 70 * MenuScale
				
				Color(255, 255, 255)
				AASetFont(fo\FontID[1])
				AAText(x + Width / 2, y + Height / 2, "LOAD MAP", True, True)
				
				x = 160 * MenuScale
				y = y + Height + 20 * MenuScale
				Width = 580 * MenuScale
				Height = 350 * MenuScale
				
				AASetFont(fo\FontID[1])
				
				tX = x + Width
				tY = y
				tW = 400 * MenuScale
				tH = 150 * MenuScale
				
				If CurrLoadGamePage < Ceil(Float(SavedMapsAmount) / 6.0) - 1 Then 
					If DrawButton(x + 530 * MenuScale, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage + 1
					EndIf
				Else
					DrawFrame(x + 530 * MenuScale, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale)
					Color(100, 100, 100)
					AAText(x + 555 * MenuScale, y + 537.5 * MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 Then
					If DrawButton(x, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage - 1
					EndIf
				Else
					DrawFrame(x, y + 510 * MenuScale, 50 * MenuScale, 55 * MenuScale)
					Color(100, 100, 100)
					AAText(x + 25 * MenuScale, y + 537.5 * MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x + 50 * MenuScale, y + 510 * MenuScale, Width - 100 * MenuScale, 55 * MenuScale)
				
				AAText(x + (Width / 2.0), y + 536 * MenuScale, "Page " + Int(Max((CurrLoadGamePage + 1), 1)) + "/" + Int(Max((Int(Ceil(Float(SavedMapsAmount) / 6.0))), 1)), True, True)
				
				AASetFont(fo\FontID[0])
				
				If CurrLoadGamePage > Ceil(Float(SavedMapsAmount) / 6.0) - 1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				AASetFont(fo\FontID[0])
				
				If SavedMaps(0) = "" Then 
					AAText(x + 20 * MenuScale, y + 20 * MenuScale, "No saved maps. Use the Map Creator to create new maps.")
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					For i = (1 + (6 * CurrLoadGamePage)) To 6 + (6 * CurrLoadGamePage)
						If i =< SavedMapsAmount Then
							DrawFrame(x, y, 540 * MenuScale, 70 * MenuScale)
							
							AAText(x + 20 * MenuScale, y + 10 * MenuScale, SavedMaps(i - 1))
							AAText(x + 20 * MenuScale, y + (10 + 27) * MenuScale, SavedMapsAuthor(i - 1))
							
							If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, "Load", False) Then
								SelectedMap = SavedMaps(i - 1)
								MainMenuTab = 1
							EndIf
							If MouseOn(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								DrawMapCreatorTooltip(tX, tY, tW, tH, SavedMaps(i - 1))
							EndIf
							
							y = y + 80 * MenuScale
						Else
							Exit
						EndIf
					Next
				EndIf
				;[End Block]
		End Select
	End If
	
	Color(255, 255, 255)
	AASetFont(fo\ConsoleFont)
	AAText(20, GraphicHeight - 50, "v" + VersionNumber)
	
	If Fullscreen Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
	
	AASetFont(fo\FontID[0])
End Function

Function UpdateLauncher()
	Local fo.Fonts = First Fonts
	Local i%, n%
	
	MenuScale = 1
	
	Graphics3DExt(LauncherWidth, LauncherHeight, 0, 2)
	
	SetBuffer(BackBuffer())
	
	RealGraphicWidth = GraphicWidth
	RealGraphicHeight = GraphicHeight
	
	fo\FontID[0] = LoadFont_Strict("GFX\font\cour\Courier New.ttf", 18, 0,0,0)
	SetFont(fo\FontID[0])
	MenuWhite = LoadImage_Strict("GFX\menu\menu_white.png")
	MenuBlack = LoadImage_Strict("GFX\menu\menu_black.png")	
	MaskImage(MenuBlack, 255, 255, 0)
	LauncherIMG = LoadImage_Strict("GFX\menu\launcher.png")
	
	For i = 1 To TotalGFXModes
		Local SameFound% = False
		
		For n = 0 To TotalGFXModes - 1
			If GfxModeWidths(n) = GfxModeWidth(i) And GfxModeHeights(n) = GfxModeHeight(i) Then SameFound = True : Exit
		Next
		If SameFound = False Then
			If GraphicWidth = GfxModeWidth(i) And GraphicHeight = GfxModeHeight(i) Then SelectedGFXMode = GFXModes
			GfxModeWidths(GFXModes) = GfxModeWidth(i)
			GfxModeHeights(GFXModes) = GfxModeHeight(i)
			GFXModes = GFXModes + 1 
		End If
	Next
	
	AppTitle("SCP - Containment Breach Ultimate Edition Launcher")
	
	Repeat
		Color(0, 0, 0)
		Rect(0, 0, LauncherWidth, LauncherHeight, True)
		
		MouseHit1 = MouseHit(1)
		
		Color(255, 255, 255)
		DrawImage(LauncherIMG, 0, 0)
		
		Text(20, 240 - 65, "Resolution: ")
		
		Local x% = 40
		Local y% = 270 - 65
		
		For i = 0 To (GFXModes - 1)
			Color(0, 0, 0)
			If SelectedGFXMode = i Then Rect(x - 1, y - 1, 100, 20, False)
			
			Text(x, y, (GfxModeWidths(i) + "x" + GfxModeHeights(i)))
			If MouseOn(x - 1, y - 1, 100, 20) Then
				Color(100, 100, 100)
				Rect(x - 1, y - 1, 100, 20, False)
				If MouseHit1 Then SelectedGFXMode = i
			EndIf
			
			y = y + 20
			If y >= 250 - 65 + (LauncherHeight - 80 - 260) Then y = 270 - 65 : x = x + 100
		Next
		
		Color(255, 255, 255)
		x = 30
		y = 369
		Rect(x - 10, y, 340, 95)
		Text(x - 10, y - 25, "Graphics:")
		
		y = y + 10
		For i = 1 To CountGfxDrivers()
			Color(0, 0, 0)
			If SelectedGFXDriver = i Then Rect(x - 1, y - 1, 290, 20, False)
			LimitText(GfxDriverName(i), x, y, 290, False)
			If MouseOn(x - 1, y - 1, 290, 20) Then
				Color(100, 100, 100)
				Rect(x - 1, y - 1, 290, 20, False)
				If MouseHit1 Then SelectedGFXDriver = i
			EndIf
			
			y = y + 20
		Next
		
		Fullscreen = DrawTick(40 + 430 - 15, 260 - 55 + 5 - 8, Fullscreen, BorderlessWindowed)
		BorderlessWindowed = DrawTick(40 + 430 - 15, 260 - 55 + 35, BorderlessWindowed)
		
		Local Lock% = False
		
		If BorderlessWindowed Or (Not Fullscreen) Then Lock = True
		Bit16Mode = DrawTick(40 + 430 - 15, 260 - 55 + 65 + 8, Bit16Mode, Lock)
		LauncherEnabled = DrawTick(40 + 430 - 15, 260 - 55 + 95 + 8, LauncherEnabled)
		
		If BorderlessWindowed Then
			Color(255, 0, 0)
 		   Fullscreen = False
		Else
			Color(255, 255, 255)
		EndIf
		
		Text(40 + 430 + 15, 262 - 55 + 5 - 8, "Fullscreen")
		Color(255, 255, 255)
		Text(40 + 430 + 15, 262 - 55 + 35 - 8, "Borderless", False, False)
		Text(40 + 430 + 15, 262 - 55 + 35 + 12, "windowed mode", False, False)
		
		If BorderlessWindowed Or (Not Fullscreen) Then
			Color(255, 0, 0)
 		   Bit16Mode = False
		Else
		    Color(255, 255, 255)
		EndIf
		
		Text(40 + 430 + 15, 262 - 55 + 65 + 8, "16 Bit")
		Color(255, 255, 255)
		Text(40 + 430 + 15, 262 - 55 + 95 + 8, "Use launcher")
		
		If (Not BorderlessWindowed) Then
			If Fullscreen Then
				Text(40 + 260 + 15, 262 - 55 + 140, "Current Resolution: " + (GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + "," + (16 + (16 * (Not Bit16Mode)))))
			Else
				Text(40 + 260 + 15, 262 - 55 + 140, "Current Resolution: " + (GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32"))
			EndIf
		Else
	        Text(40 + 260 + 15, 262 - 55 + 140, "Current Resolution: " + GfxModeWidths(SelectedGFXMode) + "x" + GfxModeHeights(SelectedGFXMode) + ",32")
			If GfxModeWidths(SelectedGFXMode) < G_Viewport_Width Then
				Text(40 + 260 + 65, 262 - 55 + 200, "(upscaled to " + G_Viewport_Width + "x" + G_Viewport_Height + ",32)")
			ElseIf GfxModeWidths(SelectedGFXMode) > G_Viewport_Width Then
				Text(40 + 260 + 65, 262 - 55 + 200, "(downscaled to " + G_Viewport_Width + "x" + G_Viewport_Height + ",32)")
			EndIf
		EndIf
		
		If DrawButton(LauncherWidth - 275, LauncherHeight - 50 - 55, 150, 30, "REPORT A BUG!", False, False, False) Then
		    ExecFile("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition/news/bug-reports1")
			End
		EndIf
		
		If DrawButton(LauncherWidth - 275, LauncherHeight - 50, 150, 30, "SEE CHANGELOG", False, False, False) Then
		    ExecFile("Changelog_Reborn.txt")
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 90, LauncherHeight - 50 - 55, 100, 30, "LAUNCH", False, False, False) Then
			GraphicWidth = GfxModeWidths(SelectedGFXMode)
			GraphicHeight = GfxModeHeights(SelectedGFXMode)
			RealGraphicWidth = GraphicWidth
			RealGraphicHeight = GraphicHeight
			Exit
		EndIf
		
		If DrawButton(LauncherWidth - 30 - 90, LauncherHeight - 50, 100, 30, "EXIT", False, False, False) Then End
		Flip
	Forever
	
	PutINIValue(OptionFile, "Global", "Width", GfxModeWidths(SelectedGFXMode))
	PutINIValue(OptionFile, "Global", "Height", GfxModeHeights(SelectedGFXMode))
	If Fullscreen Then
		PutINIValue(OptionFile, "Global", "Fullscreen", "true")
	Else
		PutINIValue(OptionFile, "Global", "Fullscreen", "false")
	EndIf
	If LauncherEnabled Then
		PutINIValue(OptionFile, "Launcher", "Launcher Enabled", "true")
	Else
		PutINIValue(OptionFile, "Launcher", "Launcher Enabled", "false")
	EndIf
	If BorderlessWindowed Then
		PutINIValue(OptionFile, "Global", "Borderless Windowed", "true")
	Else
		PutINIValue(OptionFile, "Global", "Borderless Windowed", "false")
	EndIf
	If Bit16Mode Then
		PutINIValue(OptionFile, "Global", "16Bit", "true")
	Else
		PutINIValue(OptionFile, "Global", "16Bit", "false")
	EndIf
	PutINIValue(OptionFile, "Global", "GFX Driver", SelectedGFXDriver)
End Function

Function DrawTiledImageRect(Img%, SrcX%, SrcY%, SrcWidth#, SrcHeight#, x%, y%, Width%, Height%)
	Local x2% = x
	
	While x2 < x + Width
		Local y2% = y
		
		While y2 < y + Height
			If x2 + SrcWidth > x + Width Then SrcWidth = SrcWidth - Max((x2 + SrcWidth) - (x + Width), 1)
			If y2 + SrcHeight > y + Height Then SrcHeight = SrcHeight - Max((y2 + SrcHeight) - (y + Height), 1)
			DrawImageRect(Img, x2, y2, SrcX, SrcY, SrcWidth, SrcHeight)
			y2 = y2 + SrcHeight
		Wend
		x2 = x2 + SrcWidth
	Wend
End Function

Type LoadingScreens
	Field ImgPath$
	Field Img%
	Field ID%
	Field Title$
	Field AlignX%, AlignY%
	Field DisableBackground%
	Field Txt$[5], TxtAmount%
End Type

Function InitLoadingScreens(File$)
	Local TemporaryString$, i%
	Local ls.LoadingScreens
	
	Local f% = OpenFile(File)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			ls.LoadingScreens = New LoadingScreens
			LoadingScreenAmount = LoadingScreenAmount + 1
			ls\ID = LoadingScreenAmount
			
			ls\Title = TemporaryString
			ls\ImgPath = GetINIString(File, TemporaryString, "ImgPath")
			
			For i = 0 To 4
				ls\Txt[i] = GetINIString(File, TemporaryString, "Desc" + (i + 1))
				If ls\Txt[i] <> "" Then ls\TxtAmount = ls\TxtAmount + 1
			Next
			
			ls\DisableBackground = GetINIInt(File, TemporaryString, "DisableBackground")
			
			Select Lower(GetINIString(File, TemporaryString, "Align X"))
				Case "left"
					;[Block]
					ls\AlignX = -1
					;[End Block]
				Case "middle", "center"
					;[Block]
					ls\AlignX = 0
					;[End Block]
				Case "right" 
					;[Block]
					ls\AlignX = 1
					;[End Block]
			End Select 
			
			Select Lower(GetINIString(File, TemporaryString, "Align Y"))
				Case "top", "up"
					;[Block]
					ls\AlignY = -1
					;[End Block]
				Case "middle", "center"
					;[Block]
					ls\AlignY = 0
					;[End Block]
				Case "bottom", "down"
					;[Block]
					ls\AlignY = 1
					;[End Block]
			End Select 			
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function DrawLoading(Percent%, ShortLoading% = False)
	Local fo.Fonts = First Fonts
	Local x%, y%, Temp%, FirstLoop%
	
	If Percent = 0 Then
		LoadingScreenText = 0
		
		Temp = Rand(1, LoadingScreenAmount)
		For ls.LoadingScreens = Each LoadingScreens
			If ls\ID = Temp Then
				If ls\Img = 0 Then ls\Img = LoadImage_Strict("LoadingScreens\" + ls\ImgPath + ".png")
				SelectedLoadingScreen = ls 
				Exit
			EndIf
		Next
	EndIf	
	
	FirstLoop = True
	
	Repeat 
		ClsColor(0, 0, 0)
		Cls
		
		If Percent > 20 Then
			UpdateMusic()
		EndIf
		
		If ShortLoading = False Then
			If Percent > (100.0 / SelectedLoadingScreen\TxtAmount) * (LoadingScreenText + 1) Then
				LoadingScreenText = LoadingScreenText + 1
			EndIf
		EndIf
		
		If (Not SelectedLoadingScreen\DisableBackground) Then
			DrawImage(LoadingBack, GraphicWidth / 2 - ImageWidth(LoadingBack) / 2, GraphicHeight / 2 - ImageHeight(LoadingBack) / 2)
		EndIf	
		
		If SelectedLoadingScreen\AlignX = 0 Then
			x = GraphicWidth / 2 - ImageWidth(SelectedLoadingScreen\Img) / 2 
		ElseIf  SelectedLoadingScreen\AlignX = 1
			x = GraphicWidth - ImageWidth(SelectedLoadingScreen\Img)
		Else
			x = 0
		EndIf
		
		If SelectedLoadingScreen\AlignY = 0 Then
			y = GraphicHeight / 2 - ImageHeight(SelectedLoadingScreen\Img) / 2 
		ElseIf  SelectedLoadingScreen\AlignY = 1
			y = GraphicHeight - ImageHeight(SelectedLoadingScreen\Img)
		Else
			y = 0
		EndIf	
		
		DrawImage(SelectedLoadingScreen\Img, x, y)
		
		Local Width% = 300, Height% = 20, i%
		
		x = GraphicWidth / 2 - Width / 2
		y = GraphicHeight / 2 + 30 - 100
		
		Rect(x, y, Width + 4, Height, False)
		For i = 1 To Int((Width - 2) * (Percent / 100.0) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next
		
		If SelectedLoadingScreen\Title = "CWM" Then
			
			If (Not ShortLoading) Then 
				If FirstLoop Then 
					If Percent = 0 Then
						PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm1.cwm"))
					ElseIf Percent = 100
						PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm2.cwm"))
					EndIf
				EndIf
			EndIf
			
			AASetFont(fo\FontID[1])
			
			Local Strtemp$ = ""
			
			Temp = Rand(2, 9)
			For i = 0 To Temp
				Strtemp = Strtemp + Chr(Rand(48, 122))
			Next
			AAText(GraphicWidth / 2, GraphicHeight / 2 + 80, Strtemp, True, True)
			
			If Percent = 0 Then 
				If Rand(5) = 1 Then
					Select Rand(2)
						Case 1
							;[Block]
							SelectedLoadingScreen\Txt[0] = "It will happen on " + CurrentDate() + "."
							;[End Block]
						Case 2
							;[Block]
							SelectedLoadingScreen\Txt[0] = CurrentTime()
							;[End Block]
					End Select
				Else
					Select Rand(13)
						Case 1
							;[Block]
							SelectedLoadingScreen\Txt[0] = "A very fine radio might prove to be useful."
							;[End Block]
						Case 2
							;[Block]
							SelectedLoadingScreen\Txt[0] = "ThIS PLaCE WiLL BUrN"
							;[End Block]
						Case 3
							;[Block]
							SelectedLoadingScreen\Txt[0] = "You cannot control it."
							;[End Block]
						Case 4
							;[Block]
							SelectedLoadingScreen\Txt[0] = "eof9nsd3jue4iwe1fgj"
						Case 5
							;[Block]
							SelectedLoadingScreen\Txt[0] = "YOU NEED TO TRUST IT"
							;[End Block]
						Case 6 
							;[Block]
							SelectedLoadingScreen\Txt[0] = "Look my friend in the eye when you address him, isn't that the way of the gentleman?"
							;[End Block]
						Case 7
							;[Block]
							SelectedLoadingScreen\Txt[0] = "???____??_???__????n?"
							;[End Block]
						Case 8, 9
							;[Block]
							SelectedLoadingScreen\Txt[0] = "Jorge has been expecting you."
							;[End Block]
						Case 10
							;[Block]
							SelectedLoadingScreen\Txt[0] = "???????????"
							;[End Block]
						Case 11
							;[Block]
							SelectedLoadingScreen\Txt[0] = "Make her a member of the midnight crew."
							;[End Block]
						Case 12
							;[Block]
							SelectedLoadingScreen\Txt[0] = "Concluded that coming here was a mistake. We have to turn back."
							;[End Block]
						Case 13
							;[Block]
							SelectedLoadingScreen\Txt[0] = "This alloy contains the essence of my life."
							;[End Block]
					End Select
				EndIf
			EndIf
			
			Strtemp = SelectedLoadingScreen\Txt[0]
			Temp = Int(Len(SelectedLoadingScreen\Txt[0]) - Rand(5))
			For i = 0 To Rand(10, 15)
				Strtemp = Replace(SelectedLoadingScreen\Txt[0], Mid(SelectedLoadingScreen\Txt[0], Rand(1, Len(Strtemp) - 1), 1), Chr(Rand(130, 250)))
			Next		
			AASetFont(fo\FontID[0])
			RowText(Strtemp, GraphicWidth / 2 - 200, GraphicHeight / 2 + 120, 400, 300, True)		
		Else
			
			Color(0, 0, 0)
			AASetFont(fo\FontID[1])
			AAText(GraphicWidth / 2 + 1, GraphicHeight / 2 + 80 + 1, SelectedLoadingScreen\Title, True, True)
			AASetFont(fo\FontID[0])
			RowText(SelectedLoadingScreen\Txt[LoadingScreenText], GraphicWidth / 2 - 200 + 1, GraphicHeight / 2 + 120 + 1, 400, 300, True)
			
			Color(255, 255, 255)
			AASetFont(fo\FontID[1])
			AAText(GraphicWidth / 2, GraphicHeight / 2 + 80, SelectedLoadingScreen\Title, True, True)
			AASetFont(fo\FontID[0])
			RowText(SelectedLoadingScreen\Txt[LoadingScreenText], GraphicWidth / 2 - 200, GraphicHeight / 2 + 120, 400, 300, True)
		EndIf
		
		Color(0, 0, 0)
		AAText(GraphicWidth / 2 + 1, GraphicHeight / 2 - 100 + 1, "LOADING - " + Percent + " %", True, True)
		Color(255, 255, 255)
		AAText(GraphicWidth / 2, GraphicHeight / 2 - 100, "LOADING - " + Percent + " %", True, True)
		
		If Percent = 100 Then 
			If FirstLoop And SelectedLoadingScreen\title <> "CWM" Then PlaySound_Strict LoadTempSound(("SFX\Horror\Horror8.ogg"))
			AAText(GraphicWidth / 2, GraphicHeight - 50, "PRESS ANY KEY TO CONTINUE", True, True)
		Else
			FlushKeys()
			FlushMouse()
		EndIf
		
		If BorderlessWindowed Then
			If (RealGraphicWidth <> GraphicWidth) Or (RealGraphicHeight <> GraphicHeight) Then
				SetBuffer(TextureBuffer(Fresize_Texture))
				ClsColor(0, 0, 0) : Cls
				CopyRect(0, 0, GraphicWidth, GraphicHeight, 1024 - GraphicWidth / 2, 1024 - GraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
				SetBuffer(BackBuffer())
				ClsColor(0, 0, 0) : Cls
				ScaleRender(0, 0, 2050.0 / Float(GraphicWidth) * AspectRatioRatio, 2050.0 / Float(GraphicWidth) * AspectRatioRatio)
				; ~ Might want to replace Float(GraphicWidth) with Max(GraphicWidth, GraphicHeight) if portrait sizes cause issues
				; ~ Everyone uses landscape so it's probably a non-issue
			EndIf
		EndIf
		
		; ~ Not by any means a perfect solution
		; ~ Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
		If ScreenGamma > 1.0 Then
			CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
			EntityBlend(Fresize_Image, 1)
			ClsColor(0, 0, 0) : Cls
			ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
			EntityFX(Fresize_Image, 1 + 32)
			EntityBlend(Fresize_Image, 3)
			EntityAlpha(Fresize_Image, ScreenGamma - 1.0)
			ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
		ElseIf ScreenGamma < 1.0 Then ; ~ Todo: maybe optimize this if it's too slow, alternatively give players the option to disable gamma
			CopyRect(0, 0, RealGraphicWidth, RealGraphicHeight, 1024 - RealGraphicWidth / 2, 1024 - RealGraphicHeight / 2, BackBuffer(), TextureBuffer(Fresize_Texture))
			EntityBlend(Fresize_Image, 1)
			ClsColor(0, 0, 0) : Cls
			ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
			EntityFX(Fresize_Image, 1 + 32)
			EntityBlend(Fresize_Image, 2)
			EntityAlpha(Fresize_Image, 1.0)
			SetBuffer(TextureBuffer(Fresize_Texture2))
			ClsColor(255 * ScreenGamma, 255 * ScreenGamma, 255 * ScreenGamma)
			Cls
			SetBuffer(BackBuffer())
			ScaleRender((-1.0) / Float(RealGraphicWidth), 1.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicWidth))
			SetBuffer(TextureBuffer(Fresize_Texture2))
			ClsColor(0, 0, 0)
			Cls
			SetBuffer(BackBuffer())
		EndIf
		EntityFX(Fresize_Image, 1)
		EntityBlend(Fresize_Image, 1)
		EntityAlpha(Fresize_Image, 1.0)
		
		Flip(False)
		
		FirstLoop = False
		If Percent <> 100 Then Exit
		
	Until GetKey() <> 0 Or MouseHit(1)
End Function

Global SymMilliSecs%, SymMilliSecs2%

Function rInput$(aString$)
	Local Value% = GetKey()
	Local Length% = Len(aString$)
	
	If Value = 8 Then
		Value = 0
		If Length > 0 Then aString = Left(aString, Length - 1)
		SymMilliSecs = MilliSecs() + 500
	EndIf
	If KeyDown(14) And MilliSecs() > SymMilliSecs Then
		If Length > 0 And MilliSecs() > SymMilliSecs2 Then
			aString = Left(aString, Length - 1)
			SymMilliSecs2 = MilliSecs() + 20
		EndIf
		Return(aString)
	EndIf
	If Value = 13 Or Value = 0 Then
		Return(aString)
	ElseIf Value > 0 And Value < 7 Or Value > 26 And Value < 32 Or Value = 9
		Return(aString)
	Else
		aString = aString + Chr(Value)
		Return(aString)
	End If
End Function

Function InputBox$(x%, y%, Width%, Height%, Txt$, ID% = 0)
	Color(255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, Width, Height)
	Color(0, 0, 0)
	
	Local MouseOnBox% = False
	
	If MouseOn(x, y, Width, Height) Then
		Color(50, 50, 50)
		MouseOnBox = True
		If MouseHit1 Then SelectedInputBox = ID : FlushKeys
	EndIf
	
	Rect(x + 2, y + 2, Width - 4, Height - 4)
	Color (255, 255, 255)	
	
	If (Not MouseOnBox) And MouseHit1 And SelectedInputBox = ID Then SelectedInputBox = 0
	
	If SelectedInputBox = ID Then
		Txt = rInput(Txt)
		If (MilliSecs2() Mod 800) < 400 Then Rect (x + Width / 2 + AAStringWidth(Txt) / 2 + 2, y + Height / 2 - 5, 2, 12)
	EndIf	
	
	AAText(x + Width / 2, y + Height / 2, Txt, True, True)
	
	Return(Txt)
End Function

Function DrawFrame(x%, y%, Width%, Height%, xOffset% = 0, yOffset% = 0)
	Color(255, 255, 255)
	DrawTiledImageRect(MenuWhite, xOffset, (y Mod 256), 512, 512, x, y, Width, Height)
	DrawTiledImageRect(MenuBlack, yOffset, (y Mod 256), 512, 512, x + 3 * MenuScale, y + 3 * MenuScale, Width - 6 * MenuScale, Height - 6 * MenuScale)	
End Function

Function DrawButton%(x%, y%, Width%, Height%, Txt$, BigFont% = True, WaitForMouseUp% = False, UsingAA% = True)
	Local fo.Fonts = First Fonts
	Local Clicked% = False
	
	DrawFrame(x, y, Width, Height)
	If MouseOn(x, y, Width, Height) Then
		Color(30, 30, 30)
		If (MouseHit1 And (Not WaitForMouseUp)) Or (MouseUp1 And WaitForMouseUp) Then 
			Clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
		Rect(x + 4, y + 4, Width - 8, Height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color(255, 255, 255)
	If UsingAA Then
		If BigFont Then 
			AASetFont(fo\FontID[1]) 
		Else 
			AASetFont(fo\FontID[0])
		EndIf
		AAText(x + Width / 2, y + Height / 2, Txt, True, True)
	Else
		If BigFont Then 
			SetFont(fo\FontID[1])
		Else 
			SetFont(fo\FontID[0])
		EndIf
		Text(x + Width / 2, y + Height / 2, Txt, True, True)
	EndIf
	
	Return(Clicked)
End Function

Function DrawButton2%(x%, y%, Width%, Height%, Txt$, BigFont% = True)
	Local fo.Fonts = First Fonts
	Local Clicked% = False
	
	DrawFrame(x, y, Width, Height)
	
	Local Hit% = MouseHit(1)
	
	If MouseOn(x, y, Width, Height) Then
		Color(30, 30, 30)
		If Hit Then Clicked = True : PlaySound_Strict(ButtonSFX)
		Rect(x + 4, y + 4, Width - 8, Height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color(255, 255, 255)
	If BigFont Then 
		SetFont(fo\FontID[1])
	Else 
		SetFont(fo\FontID[0])
	EndIf
	Text(x + Width / 2, y + Height / 2, Txt, True, True)
	
	Return(Clicked)
End Function

Function DrawTick%(x%, y%, Selected%, Locked% = False)
	Local Width% = 20 * MenuScale, Height% = 20 * MenuScale
	
	Color(255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, Width, Height)
	
	Local Highlight% = MouseOn(x, y, Width, Height)
	
	If Highlight Then
		Color(50, 50, 50)
		If MouseHit1 Then 
			If Locked Then
				PlaySound_Strict(ButtonSFX2)
			Else
				Selected = (Not Selected)
				PlaySound_Strict(ButtonSFX)
			EndIf
		EndIf
	Else
		Color(0, 0, 0)		
	End If
	
	Rect(x + 2, y + 2, Width - 4, Height - 4)
	
	If Selected Then
		If Highlight Then
			Color(255, 255, 255)
		Else
			Color(200, 200, 200)
		EndIf
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, Width - 8, Height - 8)
	EndIf
	
	Color(255, 255, 255)
	
	Return(Selected)
End Function

Function SlideBar#(x%, y%, Width%, Value#, TextLeft$ = "LOW", TextRight$ = "HIGH")
	If MouseDown1 And OnSliderID = 0 Then
		If ScaledMouseX() >= x And ScaledMouseX() =< x + Width + 14 And ScaledMouseY() >= y And ScaledMouseY() =< y + 20 Then
			Value = Min(Max((ScaledMouseX() - x) * 100 / Width, 0), 100)
		EndIf
	EndIf
	
	Color(255, 255, 255)
	Rect(x, y, Width + 14, 20, False)
	
	DrawImage(BlinkMeterIMG, x + Width * Value / 100.0 + 3, y + 3)
	
	Color(170, 170, 170)
	AAText(x - 50 * MenuScale, y + 4 * MenuScale, TextLeft)					
	AAText(x + Width + 38 * MenuScale, y + 4 * MenuScale, TextRight)	
	
	Return(Value)
End Function

Function RowText(A$, x%, y%, W%, H%, Align% = 0, Leading# = 1.0)
	; ~ Display A$ starting at x, y - no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = 2048
	
	Local LinesShown% = 0
	Local Height% = AAStringHeight(A) + Leading
	Local b$
	
	While Len(A) > 0
		Local Space$ = Instr(A, " ")
		
		If Space = 0 Then Space = Len(A$)
		
		Local Temp$ = Left(A, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If (AAStringWidth (b + Temp) > W) And (AAStringWidth(b + Trimmed) =< W) Then
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If AAStringWidth(b + Temp) > W Then ; ~ Too big, so print what will fit
			If Align Then
				AAText(x + W / 2 - (AAStringWidth(b) / 2), LinesShown * Height + y, b)
			Else
				AAText(x, LinesShown * Height + y, b)
			EndIf			
			
			LinesShown = LinesShown + 1
			b = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			b = b + Temp
			A = Right(A, Len(A) - (Len(Temp) + Extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	If (b <> "") And ((LinesShown + 1) =< H) Then
		If Align Then
			AAText(x + W / 2 - (AAStringWidth(b) / 2), LinesShown * Height + y, b) ; ~ Print any remaining text if it'll fit vertically
		Else
			AAText(x, LinesShown * Height + y, b) ; ~ Print any remaining text if it'll fit vertically
		EndIf
	EndIf
End Function

Function RowText2(A$, x%, y%, W%, H%, Align% = 0, Leading# = 1.0)
	; ~ Display A$ starting at x, y - no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = 2048
	
	Local LinesShown% = 0
	Local Height% = StringHeight(A) + Leading
	Local b$
	
	While Len(A) > 0
		Local Space$ = Instr(A$, " ")
		
		If Space = 0 Then Space = Len(A)
		Local Temp$ = Left(A, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If (StringWidth(b + Temp) > W) And (StringWidth (b + Trimmed) =< W) Then
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(b + Temp) > W Then ; ~ Too big, so print what will fit
			If Align Then
				Text(x + W / 2 - (StringWidth(b) / 2), LinesShown * Height + y, b)
			Else
				Text(x, LinesShown * Height + y, b)
			EndIf
			
			LinesShown = LinesShown + 1
			b = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			b = b + Temp
			A = Right(A, Len(A) - (Len(Temp$) + Extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	If (b <> "") And((LinesShown + 1) =< H) Then
		If Align Then
			Text(x + W / 2 - (StringWidth(b) / 2), LinesShown * Height + y, b) ; ~ Print any remaining text if it'll fit vertically
		Else
			Text(x, LinesShown * Height + y, b) ; ~ Print any remaining text if it'll fit vertically
		EndIf
	EndIf
End Function

Function GetLineAmount(A$, W%, H%, Leading# = 1.0)
	; ~ Display A$ no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = 2048
	
	Local LinesShown% = 0
	Local Height% = AAStringHeight(A) + Leading
	Local b$
	
	While Len(A) > 0
		Local Space$ = Instr(A, " ")
		
		If Space = 0 Then Space = Len(A)
		
		Local Temp$ = Left(A, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If (AAStringWidth(b + Temp) > W) And (AAStringWidth(b + Trimmed) =< W) Then
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If AAStringWidth(b + Temp) > W Then ; ~ Too big, so print what will fit
			LinesShown = LinesShown + 1
			b = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			b = b + Temp
			A = Right(A, Len(A) - (Len(Temp) + Extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	Return(LinesShown + 1)
End Function

Function GetLineAmount2(A$, W%, H%, Leading# = 1.0)
	; ~ Display A$ no wider than W And no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = 2048
	
	Local LinesShown% = 0
	Local Height% = StringHeight(A) + Leading
	Local b$
	
	While Len(A) > 0
		Local Space$ = Instr(A, " ")
		
		If Space = 0 Then Space = Len(A)
		
		Local Temp$ = Left(A, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If (StringWidth(b + Temp) > W) And (StringWidth(b + Trimmed) =< W) Then
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(b + Temp) > W Then ; ~ Too big, so print what will fit
			LinesShown = LinesShown + 1
			b = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			b = b + Temp
			A = Right(A, Len(A) - (Len(Temp) + Extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	Return(LinesShown + 1)
End Function

Function LimitText%(Txt$, x%, y%, Width%, UsingAA% = True)
	Local TextLength%
	Local UnFitting%
	Local LetterWidth%
	
	If UsingAA Then
		If Txt = "" Or Width = 0 Then Return(0)
		TextLength = AAStringWidth(Txt)
		UnFitting = TextLength - Width
		If UnFitting =< 0 Then
			AAText(x, y, Txt)
		Else
			LetterWidth = TextLength / Len(Txt)
			
			AAText(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...")
		End If
	Else
		If Txt = "" Or Width = 0 Then Return(0)
		TextLength = StringWidth(Txt)
		UnFitting = TextLength - Width
		If UnFitting =< 0 Then
			Text(x, y, Txt)
		Else
			LetterWidth = TextLength / Len(Txt)
			
			Text(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...")
		End If
	EndIf
End Function

Function DrawTooltip(Message$)
	Local Scale# = GraphicHeight / 768.0
	Local Width% = (AAStringWidth(Message)) + 20 * MenuScale
	Local fo.Fonts = First Fonts
	
	Color(25, 25, 25)
	Rect(ScaledMouseX() + 20, ScaledMouseY(), Width, 19 * Scale, True)
	Color(150, 150, 150)
	Rect(ScaledMouseX() + 20, ScaledMouseY(), Width, 19 * Scale, False)
	AASetFont(fo\FontID[0])
	AAText(ScaledMouseX() + (20 * MenuScale) + (Width / 2), ScaledMouseY() + (12 * MenuScale), Message, True, True)
End Function

Global QuickLoadPercent% = -1
Global QuickLoadPercent_DisplayTimer# = 0.0
Global QuickLoad_CurrEvent.Events

Function DrawQuickLoading()
	Local fo.Fonts = First Fonts
	Local fpst.FramesPerSecondsTemplate = First FramesPerSecondsTemplate
	
	If QuickLoadPercent > -1
		MidHandle(QuickLoadIcon)
		DrawImage(QuickLoadIcon, GraphicWidth - 90, GraphicHeight - 150)
		Color(255, 255, 255)
		AASetFont(fo\FontID[0])
		AAText(GraphicWidth - 100, GraphicHeight - 90, "LOADING: " + QuickLoadPercent + "%", 1)
		If QuickLoadPercent > 99
			If QuickLoadPercent_DisplayTimer < 70.0
				QuickLoadPercent_DisplayTimer = Min(QuickLoadPercent_DisplayTimer + fpst\FPSFactor[0], 70.0)
			Else
				QuickLoadPercent = -1
			EndIf
		EndIf
		QuickLoadEvents()
	Else
		QuickLoadPercent = -1
		QuickLoadPercent_DisplayTimer = 0.0
		QuickLoad_CurrEvent = Null
	EndIf
End Function

Function DrawOptionsTooltip(x%, y%, Width%, Height%, Option$, Value# = 0.0, InGame% = False)
	Local fX# = x + 6.0 * MenuScale
	Local fY# = y + 6.0 * MenuScale
	Local fW# = Width - 12.0 * MenuScale
	Local fH# = Height - 12.0 * MenuScale
	Local Lines% = 0, Lines2% = 0
	Local Txt$ = ""
	Local Txt2$ = "", R% = 0, G% = 0, B% = 0
	Local ExtraSpace% = 0
	Local fo.Fonts = First Fonts
	
	AASetFont(fo\FontID[0])
	Color(255, 255, 255)
	Select Lower(Option)
		Case "bump"
			;[Block]
			Txt = Chr(34) + "Bump mapping" + Chr(34) + " is used to simulate bumps and dents by distorting the lightmaps."
			Txt2 = "This option cannot be changed in-game."
			R = 255
			;[End Block]
		Case "vsync"
			;[Block]
			Txt = Chr(34) + "Vertical sync" + Chr(34) + " waits for the display to finish its current refresh cycle before calculating the next frame, preventing issues such as "
			Txt = Txt + "screen tearing. This ties the game's frame rate to your display's refresh rate and may cause some input lag."
			;[End Block]
		Case "antialias"
			;[Block]
			Txt = Chr(34) + "Anti-Aliasing" + Chr(34) + " is used to smooth the rendered image before displaying in order to reduce aliasing around the edges of models."
			Txt2 = "This option only takes effect in fullscreen."
			R = 255
			;[End Block]
		Case "roomlights"
			;[Block]
			Txt = "Toggles the artificial lens flare effect generated over specific light sources."
			;[End Block]
		Case "gamma"
			;[Block]
			Txt = Chr(34) + "Gamma correction" + Chr(34) + " is used to achieve a good brightness factor to balance out your display's gamma if the game appears either too dark or bright. "
			Txt = Txt + "Setting it too high or low can cause the graphics to look less detailed."
			R = 255
			G = 255
			B = 255
			Txt2 = "Current value: " + Int(Value * 100.0) + "% (default is 100%)"
			;[End Block]
		Case "texquality"
			;[Block]
			Txt = Chr(34) + "Texture LOD Bias" + Chr(34) + " affects the distance at which texture detail will change to prevent aliasing. Change this option if textures flicker or look too blurry."
			;[End Block]
		Case "particleamount"
			;[Block]
			Txt = "Determines the amount of particles that can be rendered per tick."
			Select Value
				Case 0
					;[Block]
					R = 255
					Txt2 = "Only smoke emitters will produce particles."
					;[End Block]
				Case 1
					;[Block]
					R = 255
					G = 255
					Txt2 = "Only a few particles will be rendered per tick."
					;[End Block]
				Case 2
					;[Block]
					G = 255
					Txt2 = "All particles are rendered."
					;[End Block]
			End Select
			;[End Block]
		Case "vram"
			;[Block]
			Txt = "Textures that are stored in the Video-RAM will load faster, but this also has negative effects on the texture quality as well."
			Txt2 = "This option cannot be changed in-game."
			R = 255
			;[End Block]
		Case "musicvol"
			;[Block]
			Txt = "Adjusts the volume of background music. Sliding the bar fully to the left will mute all music."
			R = 255
			G = 255
			B = 255
			Txt2 = "Current value: " + Int(Value * 100.0) + "% (default is 50%)"
			;[End Block]
		Case "soundvol"
			;[Block]
			Txt = "Adjusts the volume of sound effects. Sliding the bar fully to the left will mute all sounds."
			R = 255
			G = 255
			B = 255
			Txt2 = "Current value: " + Int(Value * 100.0) + "% (default is 100%)"
			;[End Block]
		Case "sfxautorelease"
			;[Block]
			Txt = Chr(34)+"Sound auto-release" + Chr(34) + " will free a sound from memory if it not used after 5 seconds. Prevents memory allocation issues."
			R = 255
			Txt2 = "This option cannot be changed in-game."
			;[End Block]
		Case "usertrack"
			;[Block]
			Txt = "Toggles the ability to play custom tracks over channel 1 of the radio. These tracks are loaded from the " + Chr(34) + "SFX\Radio\UserTracks\" + Chr(34)
			Txt = Txt + " directory. Press " + Chr(34) + "1" + Chr(34) + " when the radio is selected to change track."
			R = 255
			Txt2 = "This option cannot be changed in-game."
			;[End Block]
		Case "usertrackmode"
			;[Block]
			Txt = "Sets the playing mode for the custom tracks. " + Chr(34) + "Repeat" + Chr(34) + " plays every file in alphabetical order. " + Chr(34) + "Random" + Chr(34) + " chooses the "
			Txt = Txt + "next track at random."
			R = 255
			G = 255
			Txt2 = "Note that the random mode does not prevent previously played tracks from repeating."
			;[End Block]
		Case "usertrackscan"
			;[Block]
			Txt = "Re-checks the user tracks directory for any new or removed sound files."
			;[End Block]
		Case "mousesensitivity"
			;[Block]
			Txt = "Adjusts the speed of the mouse pointer."
			R = 255
			G = 255
			B = 255
			Txt2 = "Current value: " + Int((0.5 + Value) * 100.0) + "% (default is 50%)"
			;[End Block]
		Case "mouseinvert"
			;[Block]
			Txt = Chr(34)+"Invert mouse Y-axis" + Chr(34) + " is self-explanatory."
			;[End Block]
		Case "mousesmoothing"
			;[Block]
			Txt = "Adjusts the amount of smoothing of the mouse pointer."
			R = 255
			G = 255
			B = 255
			Txt2 = "Current value: " + Int(Value * 100.0) + "% (default is 100%)"
			;[End Block]
		Case "controls"
			;[Block]
			Txt = "Configure the in-game control scheme."
			;[End Block]
		Case "hud"
			;[Block]
			Txt = "Display the blink and stamina meters."
			;[End Block]
		Case "consoleenable"
			;[Block]
			Txt = "Toggles the use of the developer console. Can be used in-game by pressing " + KeyName(KEY_CONSOLE) + "."
			;[End Block]
		Case "consoleerror"
			;[Block]
			Txt = Chr(34) + "Open console on error" + Chr(34) + " is self-explanatory."
			;[End Block]
		Case "achpopup"
			;[Block]
			Txt = "Displays a pop-up notification when an achievement is unlocked."
			;[End Block]
		Case "showfps"
			;[Block]
			Txt = "Displays the frames per second counter at the top left-hand corner."
			;[End Block]
		Case "framelimit"
			;[Block]
			Txt = "Limits the frame rate that the game can run at to a desired value."
			If Value > 0 And Value < 60 Then
				R = 255
				G = 255
				Txt2 = "Usually, 60 FPS or higher is preferred. If you are noticing excessive stuttering at this setting, try lowering it to make your framerate more consistent."
			EndIf
			;[End Block]
		Case "antialiastext"
			;[Block]
			Txt = Chr(34) + "Antialiased text" + Chr(34) + " smooths out the text before displaying. Makes text easier to read at high resolutions."
			;[End Block]
		Case "consoleversion"
			;[Block]
		    Txt = Chr(34) + "Change console version" + Chr(34) + " is self-explanatory."
			;[End Block]
		Case "fov"
			;[Block]
			Txt = Chr(34) + "Field of view" + Chr(34) + " is the amount of game view that is on display during a game."
			Txt2 = "Current value: " + Int(FOV) + "° (default is 74°)"
			;[End Block]
	End Select
	
	Lines = GetLineAmount(Txt, fW, fH)
	If Txt2 = ""
		DrawFrame(x, y, Width, ((AAStringHeight(Txt) * Lines) + (10 + Lines) * MenuScale) + ExtraSpace)
	Else
		Lines2 = GetLineAmount(Txt2, fW, fH)
		DrawFrame(x, y, Width, (((AAStringHeight(Txt) * Lines) + (10 + Lines) * MenuScale) + (AAStringHeight(Txt2) * Lines2) + (10 + Lines2) * MenuScale) + ExtraSpace)
	EndIf
	RowText(Txt, fX, fY, fW, fH)
	If Txt2 <> "" Then
		Color(R, G, B)
		RowText(Txt2, fX, (fY + (AAStringHeight(Txt) * Lines) + (5 + Lines) * MenuScale), fW, fH)
	EndIf
End Function

Function DrawMapCreatorTooltip(x%, y%, Width%, Height%, MapName$)
	Local fX# = x + 6.0 * MenuScale
	Local fY# = y + 6.0 * MenuScale
	Local fW# = Width - 12.0 * MenuScale
	Local fH# = Height - 12.0 * MenuScale
	Local Lines% = 0
	Local fo.Fonts = First Fonts
	
	AASetFont(fo\FontID[0])
	Color(255, 255, 255)
	
	Local Txt$[5]
	
	If Right(MapName, 6) = "cbmap2" Then
		Txt[0] = Left(MapName, Len(MapName) - 7)
		
		Local f% = OpenFile("Map Creator\Maps\" + MapName)
		
		Local Author$ = ReadLine(f)
		Local Descr$ = ReadLine(f)
		
		ReadByte(f)
		ReadByte(f)
		
		Local rAmount% = ReadInt(f)
		
		If ReadInt(f) > 0 Then
			Local HasForest% = True
		Else
			HasForest = False
		EndIf
		If ReadInt(f) > 0 Then
			Local HasMT% = True
		Else
			HasMT = False
		EndIf
		
		CloseFile(f)
	Else
		Txt[0] = Left(MapName, Len(MapName) - 6)
		Author = "[Unknown]"
		Descr = "[No description]"
		rAmount = 0
		HasForest = False
		HasMT = False
	EndIf
	Txt[1] = "Made by: " + Author
	Txt[2] = "Description: " + Descr
	If rAmount > 0 Then
		Txt[3] = "Room amount: " + rAmount
	Else
		Txt[3] = "Room amount: [Unknown]"
	EndIf
	If HasForest Then
		Txt[4] = "Has custom forest: Yes"
	Else
		Txt[4] = "Has custom forest: No"
	EndIf
	If HasMT Then
		Txt[5] = "Has custom maintenance tunnel: Yes"
	Else
		Txt[5] = "Has custom maintenance tunnel: No"
	EndIf
	
	Lines = GetLineAmount(Txt[2], fW, fH)
	DrawFrame(x, y, Width, (AAStringHeight(Txt[0]) * 6) + AAStringHeight(Txt[2]) * Lines + 5 * MenuScale)
	
	Color(255, 255, 255)
	AAText(fX, fY,Txt[0])
	AAText(fX, fY + AAStringHeight(Txt[0]), Txt[1])
	RowText(Txt[2], fX, fY + (AAStringHeight(Txt[0]) * 2), fW, fH)
	AAText(fX, fY + ((AAStringHeight(Txt[0]) * 2) + AAStringHeight(Txt[2]) * Lines + 5 * MenuScale), Txt[3])
	AAText(fX, fY + ((AAStringHeight(Txt[0]) * 3) + AAStringHeight(Txt[2]) * Lines + 5 * MenuScale), Txt[4])
	AAText(fX, fY + ((AAStringHeight(Txt[0]) * 4) + AAStringHeight(Txt[2]) * Lines + 5 * MenuScale), Txt[5])
End Function

Global OnSliderID% = 0

Function Slider3(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color(200, 200, 200)
	Rect(x, y, Width + 14, 10, True)
	Rect(x, y - 8, 4, 14, True)
	Rect(x + (Width / 2) + 5, y - 8, 4, 14, True)
	Rect(x + Width + 10, y - 8, 4, 14, True)
	
	If ID = OnSliderID Then
		If (ScaledMouseX() =< x + 8 )
			Value = 0
		ElseIf (ScaledMouseX() >= x + Width / 2) And (ScaledMouseX() =< x + (Width / 2) + 8)
			Value = 1
		ElseIf (ScaledMouseX() >= x + Width)
			Value = 2
		EndIf
		Color(0, 255, 0)
		Rect(x, y, Width + 14, 10, True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			Color(0, 200, 0)
			Rect(x, y, Width + 14, 10, False)
		EndIf
	EndIf
	
	If Value = 0
		DrawImage(BlinkMeterIMG, x, y - 8)
	ElseIf Value = 1
		DrawImage(BlinkMeterIMG, x + (Width / 2) + 3, y - 8)
	Else
		DrawImage(BlinkMeterIMG, x + Width + 6, y - 8)
	EndIf
	
	Color(170, 170, 170)
	If Value = 0
		AAText(x + 2, y + 10 + MenuScale, Val1, True)
	ElseIf Value = 1
		AAText(x + (Width / 2) + 7, y + 10 + MenuScale, Val2, True)
	Else
		AAText(x + Width + 12, y + 10 + MenuScale, Val3, True)
	EndIf
	
	Return(Value)
End Function

Function Slider4(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$, Val4$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color(200, 200, 200)
	Rect(x, y, Width + 14, 10, True)
	Rect(x, y - 8, 4, 14, True)
	Rect(x + (Width * (1.0 / 3.0)) + (10.0 / 3.0), y - 8, 4, 14, True)
	Rect(x + (Width * (2.0 / 3.0)) + (20.0 / 3.0), y - 8, 4, 14, True)
	Rect(x + Width + 10, y - 8, 4, 14, True)
	
	If ID = OnSliderID Then
		If (ScaledMouseX() =< x + 8)
			Value = 0
		ElseIf (ScaledMouseX() >= x + Width * (1.0 / 3.0)) And (ScaledMouseX() =< x + Width * (1.0 / 3.0) + 8)
			Value = 1
		ElseIf (ScaledMouseX() >= x + Width * (2.0 / 3.0)) And (ScaledMouseX() =< x + Width * (2.0 / 3.0) + 8)
			Value = 2
		ElseIf (ScaledMouseX() >= x + Width)
			Value = 3
		EndIf
		Color(0, 255, 0)
		Rect(x, y, Width + 14, 10, True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			Color(0, 200, 0)
			Rect(x, y, Width + 14, 10, False)
		EndIf
	EndIf
	
	If Value = 0
		DrawImage(BlinkMeterIMG, x, y - 8)
	ElseIf Value = 1
		DrawImage(BlinkMeterIMG, x + Width * (1.0 / 3.0) + 2, y - 8)
	ElseIf Value = 2
		DrawImage(BlinkMeterIMG, x + Width * (2.0 / 3.0) + 4, y - 8)
	Else
		DrawImage(BlinkMeterIMG, x + Width + 6, y - 8)
	EndIf
	
	Color(170, 170, 170)
	If Value = 0
		AAText(x + 2, y + 10 + MenuScale, Val1, True)
	ElseIf Value = 1
		AAText(x + Width * (1.0 / 3.0) + 2 + (10.0 / 3.0), y + 10 + MenuScale, Val2, True)
	ElseIf Value = 2
		AAText(x + Width * (2.0 / 3.0) + 2 + ((10.0 / 3.0) * 2), y + 10 + MenuScale, Val3, True)
	Else
		AAText(x + Width + 12, y + 10 + MenuScale, Val4, True)
	EndIf
	
	Return(Value)
End Function

Function Slider5(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$, Val4$, Val5$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color(200, 200, 200)
	Rect(x, y, Width + 14, 10, True)
	Rect(x, y - 8, 4, 14, True)
	Rect(x + (Width / 4) + 2.5, y - 8, 4, 14, True)
	Rect(x + (Width / 2) + 5, y - 8, 4, 14, True)
	Rect(x + (Width * 0.75) + 7.5, y - 8, 4, 14, True)
	Rect(x + Width + 10, y - 8, 4, 14, True)
	
	If ID = OnSliderID Then
		If (ScaledMouseX() =< x + 8)
			Value = 0
		ElseIf (ScaledMouseX() >= x + Width / 4) And (ScaledMouseX() =< x + (Width / 4) + 8)
			Value = 1
		ElseIf (ScaledMouseX() >= x + Width / 2) And (ScaledMouseX() =< x + (Width / 2) + 8)
			Value = 2
		ElseIf (ScaledMouseX() >= x + Width * 0.75) And (ScaledMouseX() =< x + (Width * 0.75) + 8)
			Value = 3
		ElseIf (ScaledMouseX() >= x + Width)
			Value = 4
		EndIf
		Color(0, 255, 0)
		Rect(x, y, Width + 14, 10, True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			Color(0, 200, 0)
			Rect(x, y, Width + 14, 10, False)
		EndIf
	EndIf
	
	If Value = 0
		DrawImage(BlinkMeterIMG, x, y - 8)
	ElseIf Value = 1
		DrawImage(BlinkMeterIMG, x + (Width / 4) + 1.5, y - 8)
	ElseIf Value = 2
		DrawImage(BlinkMeterIMG, x + (Width / 2) + 3, y - 8)
	ElseIf Value = 3
		DrawImage(BlinkMeterIMG, x + (Width * 0.75) + 4.5, y - 8)
	Else
		DrawImage(BlinkMeterIMG, x + Width + 6, y - 8)
	EndIf
	
	Color(170, 170, 170)
	If Value = 0
		AAText(x + 2, y + 10 + MenuScale, Val1, True)
	ElseIf Value = 1
		AAText(x + (Width / 4) + 4.5, y + 10 + MenuScale, Val2, True)
	ElseIf Value = 2
		AAText(x + (Width / 2) + 7, y + 10 + MenuScale, Val3, True)
	ElseIf Value = 3
		AAText(x + (Width * 0.75) + 9.5, y + 10 + MenuScale, Val4, True)
	Else
		AAText(x + Width + 12, y + 10 + MenuScale, Val5, True)
	EndIf
	
	Return(Value)
End Function

Function Slider7(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$, Val4$, Val5$, Val6$, Val7$)
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color(200, 200, 200)
	Rect(x, y, Width + 14, 10, True)
	Rect(x, y - 8, 4, 14, True)
	Rect(x + (Width * (1.0 / 6.0)) + (10.0 / 6.0), y - 8, 4, 14, True)
	Rect(x + (Width * (2.0 / 6.0)) + (20.0 / 6.0), y - 8, 4, 14, True)
	Rect(x + (Width * (3.0 / 6.0)) + (30.0 / 6.0), y - 8, 4, 14, True)
	Rect(x + (Width * (4.0 / 6.0)) + (40.0 / 6.0), y - 8, 4, 14, True)
	Rect(x + (Width * (5.0 / 6.0)) + (50.0 / 6.0), y - 8, 4, 14, True)
	Rect(x + Width + 10, y - 8, 4, 14, True)
	
	If ID = OnSliderID Then
		If (ScaledMouseX() =< x + 8)
			Value = 0
		ElseIf (ScaledMouseX() >= x + (Width * (1.0 / 6.0))) And (ScaledMouseX() =< x + (Width * (1.0 / 6.0)) + 8)
			Value = 1
		ElseIf (ScaledMouseX() >= x + (Width * (2.0 / 6.0))) And (ScaledMouseX() =< x + (Width * (2.0 / 6.0)) + 8)
			Value = 2
		ElseIf (ScaledMouseX() >= x + (Width * (3.0 / 6.0))) And (ScaledMouseX() =< x + (Width * (3.0 / 6.0)) + 8)
			Value = 3
		ElseIf (ScaledMouseX() >= x + (Width * (4.0 / 6.0))) And (ScaledMouseX() =< x + (Width * (4.0 / 6.0)) + 8)
			Value = 4
		ElseIf (ScaledMouseX() >= x + (Width * (5.0 / 6.0))) And (ScaledMouseX() =< x + (Width * (5.0 / 6.0)) + 8)
			Value = 5
		ElseIf (ScaledMouseX() >= x + Width)
			Value = 6
		EndIf
		Color(0, 255, 0)
		Rect(x, y, Width + 14, 10, True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() =< x + Width + 14) And (ScaledMouseY() >= y - 8) And (ScaledMouseY() =< y + 10)
			Color(0, 200, 0)
			Rect(x, y, Width + 14, 10, False)
		EndIf
	EndIf
	
	If Value = 0 Then
		DrawImage(BlinkMeterIMG, x, y - 8)
	ElseIf Value = 1
		DrawImage(BlinkMeterIMG, x + (Width * (1.0 / 6.0)) + 1, y - 8)
	ElseIf Value = 2
		DrawImage(BlinkMeterIMG, x + (Width * (2.0 / 6.0)) + 2, y - 8)
	ElseIf Value = 3
		DrawImage(BlinkMeterIMG, x + (Width * (3.0 / 6.0)) + 3, y - 8)
	ElseIf Value = 4
		DrawImage(BlinkMeterIMG, x + (Width * (4.0 / 6.0)) + 4, y - 8)
	ElseIf Value = 5
		DrawImage(BlinkMeterIMG, x + (Width * (5.0 / 6.0)) + 5, y - 8)
	Else
		DrawImage(BlinkMeterIMG, x + Width + 6, y - 8)
	EndIf
	
	Color(170, 170, 170)
	If Value = 0
		AAText(x + 2, y + 10 + MenuScale, Val1, True)
	ElseIf Value = 1
		AAText(x + (Width * (1.0 / 6.0)) + 2 + (10.0 / 6.0), y + 10 + MenuScale, Val2, True)
	ElseIf Value = 2
		AAText(x + (Width * (2.0 / 6.0)) + 2 + ((10.0 / 6.0) * 2), y + 10 + MenuScale, Val3, True)
	ElseIf Value = 3
		AAText(x + (Width * (3.0 / 6.0)) + 2 + ((10.0 / 6.0) * 3), y + 10 + MenuScale, Val4, True)
	ElseIf Value = 4
		AAText(x + (Width * (4.0 / 6.0)) + 2 + ((10.0 / 6.0) * 4), y + 10 + MenuScale, Val5, True)
	ElseIf Value = 5
		AAText(x + (Width * (5.0 / 6.0)) + 2 + ((10.0 / 6.0) * 5), y + 10 + MenuScale, Val6, True)
	Else
		AAText(x + Width + 12, y + 10 + MenuScale, Val7, True)
	EndIf
	
	Return(Value)
End Function

Global OnBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function DrawScrollBar#(x%, y%, Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Bar#, Dir% = 0)
	Local MouseSpeedX# = MouseXSpeed()
	Local MouseSpeedY# = MouseYSpeed()
	
	Color(0, 0, 0)
	Button(BarX, BarY, BarWidth, BarHeight, "")
	
	If Dir = 0 Then
		If Height > 10 Then
			Color(250, 250, 250)
			Rect(BarX + BarWidth / 2, BarY + 5 * MenuScale, 2 * MenuScale, BarHeight - 10)
			Rect(BarX + BarWidth / 2 - 3 * MenuScale, BarY + 5 * MenuScale, 2 * MenuScale, BarHeight - 10)
			Rect(BarX + BarWidth / 2 + 3 * MenuScale, BarY + 5 * MenuScale, 2 * MenuScale, BarHeight - 10)
		EndIf
	Else
		If Width > 10 Then
			Color(250, 250, 250)
			Rect(BarX + 4 * MenuScale, BarY + BarHeight / 2, BarWidth - 10 * MenuScale, 2 * MenuScale)
			Rect(BarX + 4 * MenuScale, BarY + BarHeight / 2 - 3 * MenuScale, BarWidth - 10 * MenuScale, 2 * MenuScale)
			Rect(BarX + 4 * MenuScale, BarY + BarHeight / 2 + 3 * MenuScale, BarWidth - 10 * MenuScale, 2 * MenuScale)
		EndIf
	EndIf
	
	If MouseX() > BarX And MouseX() < BarX + BarWidth
		If MouseY() > BarY And MouseY() < BarY + BarHeight
			OnBar = True
		Else
			If (Not MouseDown1) Then
				OnBar = False
			EndIf
		EndIf
	Else
		If (Not MouseDown1) Then
			OnBar = False
		EndIf
	EndIf
	
	If MouseDown1 Then
		If OnBar Then
			If Dir = 0
				Return(Min(Max(Bar + MouseSpeedX / Float(Width - BarWidth), 0), 1))
			Else
				Return(Min(Max(Bar + MouseSpeedY / Float(Height - BarHeight), 0), 1))
			EndIf
		EndIf
	EndIf
	
	Return(Bar)
End Function

Function Button%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
	Local Pushed% = False
	
	Color(50, 50, 50)
	If Not Disabled Then 
		If MouseX() > x And MouseX() < x + Width Then
			If MouseY() > y And MouseY() < y + Height Then
				If MouseDown1 Then
					Pushed = True
					Color(50 * 0.6, 50 * 0.6, 50 * 0.6)
				Else
					Color(Min(50 * 1.2, 255), Min(50 * 1.2, 255), Min(50 * 1.2, 255))
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect(x, y, Width, Height)
		Color(133, 130, 125)
		Rect(x + 1 * MenuScale, y + 1 * MenuScale, Width - 1 * MenuScale, Height - 1 * MenuScale, False)
		Color(10, 10, 10)
		Rect(x, y, Width, Height, False)
		Color(250, 250, 250)
		Line(x, y + Height - 1 * MenuScale, x + Width - 1  * MenuScale, y + Height - 1 * MenuScale)
		Line(x + Width - 1 * MenuScale, y, x + Width - 1 * MenuScale, y + Height - 1 * MenuScale)
	Else
		Rect(x, y, Width, Height)
		Color(133, 130, 125)
		Rect(x, y, Width - 1 * MenuScale, Height - 1 * MenuScale, False)	
		Color(250, 250, 250)
		Rect(x, y, Width, Height, False)
		Color(10, 10, 10)
		Line(x, y + Height - 1, x + Width - 1, y + Height - 1)
		Line(x + Width - 1, y, x + Width - 1, y + Height - 1)	
	EndIf
	
	Color(255, 255, 255)
	If Disabled Then Color(70, 70, 70)
	Text(x + Width / 2, y + Height / 2 - 1 * MenuScale, Txt, True, True)
	
	Color(0, 0, 0)
	
	If Pushed And MouseHit1 Then PlaySound_Strict(ButtonSFX) : Return(True)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D