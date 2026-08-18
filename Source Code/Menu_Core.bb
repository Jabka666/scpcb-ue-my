Global MainMenuOpen%

Type MainMenu
	Field MainMenuBlinkTimer#[2]
	Field MainMenuBlinkDuration#[2]
	Field MainMenuStr$, MainMenuStrX%, MainMenuStrY%
	Field MainMenuTab%, PrevMainMenuTab%
	Field CurrMenuPage%
	Field QuitMenu%
End Type

Global mm.MainMenu

Type MainMenuAssets
	Field BackGround%
	Field SECURE_CONTAIN_PROTECT%
	Field SECURE_CONTAIN_PROTECT_WIDTH%
	Field SECURE_CONTAIN_PROTECT_HEIGHT%
	Field SCP173%
	Field SCP173Width%
	Field SCP173Height%
End Type

Global mma.MainMenuAssets

MenuWhite = LoadImage_Strict("GFX\Menu\menu_white.png")
MenuGray = LoadImage_Strict("GFX\Menu\menu_gray.png")
MenuBlack = LoadImage_Strict("GFX\Menu\menu_black.png")

Function InitMainMenuAssets%()
	Local i%
	
	mm.MainMenu = New MainMenu
	mma.MainMenuAssets = New MainMenuAssets
	
	mma\BackGround = ResizeImageEx(LoadImage_Strict("GFX\Menu\back.png"), MenuScale, MenuScale)
	
	mma\SECURE_CONTAIN_PROTECT = ResizeImageEx(LoadImage_Strict("GFX\Menu\SCP_text.png"), MenuScale, MenuScale)
	mma\SECURE_CONTAIN_PROTECT_WIDTH = ImageWidth(mma\SECURE_CONTAIN_PROTECT)
	mma\SECURE_CONTAIN_PROTECT_HEIGHT = ImageHeight(mma\SECURE_CONTAIN_PROTECT)
	
	mma\SCP173 = ResizeImageEx(LoadImage_Strict("GFX\Menu\scp_173_back.png"), MenuScale, MenuScale)
	mma\SCP173Width = ImageWidth(mma\SCP173)
	mma\SCP173Height = ImageHeight(mma\SCP173)
	
	
	For i = 0 To 2
		If i < 2 Then mm\MainMenuBlinkTimer[i] = 1.0
		ButtonSFX[i] = LoadSound_Strict("SFX\Interact\Button" + i + ".ogg")
		ButtonLockedSFX[i] = LoadSound_Strict("SFX\Interact\ButtonLocked" + i + ".ogg")
	Next
End Function

Function DeInitMainMenuAssets%()
	FreeImage(mma\BackGround) : mma\BackGround = 0
	FreeImage(mma\SECURE_CONTAIN_PROTECT) : mma\SECURE_CONTAIN_PROTECT = 0
	FreeImage(mma\SCP173) : mma\SCP173 = 0
	Delete(mma) : mma = Null
	Delete(mm) : mm = Null
End Function

Global RandomSeed$

Global SelectedInputBox%, CursorPos% = -1
Global ShouldDeleteGadgets%

; ~ Main Menu Tab Constants
;[Block]
Const MainMenuTab_Default% = 0
Const MainMenuTab_New_Game% = 1
Const MainMenuTab_Load_Game% = 2
Const MainMenuTab_Load_Map% = 3
Const MainMenuTab_Options% = 4
Const MainMenuTab_Options_Graphics% = 5
Const MainMenuTab_Options_Audio% = 6
Const MainMenuTab_Options_Controls% = 7
Const MainMenuTab_Options_Advanced% = 8
;[End Block]

Function ChangeOptionTab%(Page%, MainMenu% = True)
	If MainMenu
		mm\MainMenuTab = Page
	Else
		igm\OptionsMenu = Page
		ShouldDeleteGadgets = True
	EndIf
	ResetInput()
End Function

Function ChangePage%(Page%)
	mm\CurrMenuPage = Page
	ShouldDeleteGadgets = True
End Function

Function UpdateMainMenu%()
	CatchErrors("UpdateMainMenu()")
	
	Local sv.Save, cm.CustomMaps, snd.Sound
	Local x%, y%, Width%, Height%, Temp%, i%, j%
	Local File$, Test%
	
	While fps\Accumulator > 0.0
		fps\Accumulator = fps\Accumulator - TICK_DURATION
		
		UpdateMouseInput()
		
		If ShouldDeleteGadgets Then DeleteMenuGadgets()
		ShouldDeleteGadgets = False
		
		UpdateMusic()
		If opt\EnableSFXRelease Then AutoReleaseSounds()
		
		If ShouldPlay = 20
			EndBreathSFX = LoadSound_Strict("SFX\Ending\MenuBreath.ogg")
			EndBreathCHN = PlaySound_Strict(EndBreathSFX, True)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66
			If (Not ChannelPlaying(EndBreathCHN))
				FreeSound_Strict(EndBreathSFX) : EndBreathSFX = 0
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		
		If Rand(300) = 1
			mm\MainMenuBlinkTimer[0] = Rnd(4000.0, 8000.0)
			mm\MainMenuBlinkDuration[0] = Rnd(200.0, 500.0)
		EndIf
		
		mm\MainMenuBlinkTimer[1] = mm\MainMenuBlinkTimer[1] - fps\Factor[0]
		
		If (Not mo\MouseDown1) Then OnSliderID = 0
		
		If mm\PrevMainMenuTab <> mm\MainMenuTab Then DeleteMenuGadgets() : mm\CurrMenuPage = 0
		mm\PrevMainMenuTab = mm\MainMenuTab
		
		x = 159 * MenuScale
		If mm\MainMenuTab = MainMenuTab_Default
			y = 286 * MenuScale
			Width = 400 * MenuScale
			Height = 70 * MenuScale
			
			If mm\QuitMenu = 0
				RandomSeed = ""
				If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "new"), Font_Default_Big)
					If opt\NumericSeed
						RandomSeed = MilliSecs()
					Else
						If Rand(15) = 1
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
							i = Rand(4, 8)
							For j = 1 To i
								If Rand(3) = 1
									RandomSeed = RandomSeed + Rand(0, 9)
								Else
									RandomSeed = RandomSeed + Chr(Rand(97, 122))
								EndIf
							Next
						EndIf
					EndIf
					LoadSavedGames()
					CurrSave = New Save
					;LoadCustomMaps()
					CurrCustomMap = New CustomMaps
					mm\MainMenuTab = MainMenuTab_New_Game
				EndIf
				
				y = y + 100 * MenuScale
				
				If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "load"), Font_Default_Big)
					LoadSavedGames()
					mm\MainMenuTab = MainMenuTab_Load_Game
				EndIf
				
				y = y + 100 * MenuScale
				
				If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "options"), Font_Default_Big) Then mm\MainMenuTab = MainMenuTab_Options
				
				y = y + 100 * MenuScale
				
				If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "quit"), Font_Default_Big)
					ShouldDeleteGadgets = True
					mm\QuitMenu = 1
					Return
				EndIf
			Else
				y = y + 100 * MenuScale
				
				If UpdateMenuButton(x, y, Width, Height, GetLocalString("menu", "back"), Font_Default_Big)
					ShouldDeleteGadgets = True
					mm\QuitMenu = 0
					Return
				EndIf
				
				y = y + 100 * MenuScale
				
				Local TempStr$ = GetLocalString("menu", "quit")
				
				If mm\MainMenuBlinkTimer[1] < mm\MainMenuBlinkDuration[1] Then TempStr = GetLocalString("menu", "escape")
				
				If UpdateMenuButton(x, y, Width, Height, TempStr, Font_Default_Big)
					StopStream_Strict(MusicCHN) : MusicCHN = 0
					End()
				EndIf
			EndIf
		Else
			y = 376 * MenuScale
			Width = 580 * MenuScale
			If mm\MainMenuTab < MainMenuTab_Options_Graphics
				Select mm\MainMenuTab
					Case MainMenuTab_New_Game
						;[Block]
						Height = 345 * MenuScale
						
						CurrSave\Name = UpdateMenuInputBox(x + 150 * MenuScale, y + 15 * MenuScale, 200 * MenuScale, 30 * MenuScale, CurrSave\Name, Font_Default, 1, 15)
						If SelectedInputBox = 1
							CurrSave\Name = Replace(CurrSave\Name, ":", "")
							CurrSave\Name = Replace(CurrSave\Name, ".", "")
							CurrSave\Name = Replace(CurrSave\Name, "/", "")
							CurrSave\Name = Replace(CurrSave\Name, "\", "")
							CurrSave\Name = Replace(CurrSave\Name, "<", "")
							CurrSave\Name = Replace(CurrSave\Name, ">", "")
							CurrSave\Name = Replace(CurrSave\Name, "|", "")
							CurrSave\Name = Replace(CurrSave\Name, "?", "")
							CurrSave\Name = Replace(CurrSave\Name, Chr(34), "")
							CurrSave\Name = Replace(CurrSave\Name, "*", "")
							CursorPos = Min(CursorPos, Len(CurrSave\Name))
						EndIf
						
						If SelectedCustomMap = Null
							RandomSeed = UpdateMenuInputBox(x + 150 * MenuScale, y + 55 * MenuScale, 200 * MenuScale, 30 * MenuScale, RandomSeed, Font_Default, 2, 15)
						ElseIf UpdateMenuButton(x + 370 * MenuScale, y + 55 * MenuScale, 120 * MenuScale, 30 * MenuScale, GetLocalString("menu", "deselect"))
							ShouldDeleteGadgets = True
							SelectedCustomMap = Null
						EndIf
						
						opt\IntroEnabled = UpdateMenuTick(x + 280 * MenuScale, y + 110 * MenuScale, opt\IntroEnabled)
						
						For i = DIFFICULTY_SAFE To DIFFICULTY_ESOTERIC
							Local PrevSelectedDifficulty.Difficulty = SelectedDifficulty
							
							If UpdateMenuTick(x + 20 * MenuScale, y + (180 + 30 * i) * MenuScale, (SelectedDifficulty = difficulties[i])) Then SelectedDifficulty = difficulties[i]
							
							If PrevSelectedDifficulty <> SelectedDifficulty Then ShouldDeleteGadgets = (PrevSelectedDifficulty = difficulties[DIFFICULTY_ESOTERIC])
						Next
						
						If SelectedDifficulty\Customizable
							; ~ Save type
							If UpdateMenuButton(x + 160 * MenuScale, y + 180 * MenuScale, 20 * MenuScale, 20 * MenuScale, ">")
								If SelectedDifficulty\SaveType < DIFFICULTY_SAVE_TYPE_NO_SAVES
									SelectedDifficulty\SaveType = SelectedDifficulty\SaveType + 1
								Else
									SelectedDifficulty\SaveType = DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
								EndIf
							EndIf
							
							; ~ Aggressive NPCs
							SelectedDifficulty\AggressiveNPCs = UpdateMenuTick(x + 160 * MenuScale, y + 210 * MenuScale, SelectedDifficulty\AggressiveNPCs)
							
							; ~ Inventory slots
							If UpdateMenuButton(x + 160 * MenuScale, y + 240 * MenuScale, 20 * MenuScale, 20 * MenuScale, "<")
								SelectedDifficulty\InventorySlots = SelectedDifficulty\InventorySlots - 2
								If SelectedDifficulty\InventorySlots <= 0 Then SelectedDifficulty\InventorySlots = 10
							ElseIf UpdateMenuButton(x + 405 * MenuScale, y + 240 * MenuScale, 20 * MenuScale, 20 * MenuScale, ">")
								SelectedDifficulty\InventorySlots = SelectedDifficulty\InventorySlots + 2
								If SelectedDifficulty\InventorySlots > 10 Then SelectedDifficulty\InventorySlots = 2
							EndIf
							
							; ~ Other factor's difficulty
							If UpdateMenuButton(x + 160 * MenuScale, y + 270 * MenuScale, 20 * MenuScale, 20 * MenuScale, ">")
								If SelectedDifficulty\OtherFactors < DIFFICULTY_FACTOR_EXTREME
									SelectedDifficulty\OtherFactors = SelectedDifficulty\OtherFactors + 1
								Else
									SelectedDifficulty\OtherFactors = DIFFICULTY_FACTOR_EASY
								EndIf
							EndIf
						EndIf
						
						If UpdateMenuButton(x, y + Height + 20 * MenuScale, 160 * MenuScale, 75 * MenuScale, GetLocalString("menu", "loadmap"))
							;LoadCustomMaps()
							mm\MainMenuTab = MainMenuTab_Load_Map
						EndIf
						
						If UpdateMenuButton(x + 420 * MenuScale, y + Height + 20 * MenuScale, 160 * MenuScale, 75 * MenuScale, GetLocalString("menu", "start"))
							If CurrSave\Name = "" Then CurrSave\Name = ConvertToANSI(GetLocalString("save", "untitled"))
							CurrSave\Name = Trim(CurrSave\Name)
							
							RandomSeed = Trim(RandomSeed)
							If RandomSeed = "" Then RandomSeed = MilliSecs()
							
							SeedRnd(GenerateSeedNumber(RandomSeed))
							
							Local SameFound% = 0
							Local LowestPossible% = 2
							
							For sv.Save = Each Save
								If (CurrSave <> sv And CurrSave\Name = sv\Name)
									SameFound = 1
									Exit
								EndIf
							Next
							
							While SameFound = 1
								SameFound = 2
								For sv.Save = Each Save
									If (sv\Name = (CurrSave\Name + " (" + LowestPossible + ")"))
										LowestPossible = LowestPossible + 1
										SameFound = 1
										Exit
									EndIf
								Next
							Wend
							
							If SameFound = 2 Then CurrSave\Name = CurrSave\Name + " (" + LowestPossible + ")"
							
							InitNewGame()
							
							IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
							
							ShouldDeleteGadgets = True
							MainMenuOpen = False
							Return
						EndIf
						;[End Block]
					Case MainMenuTab_Load_Game
						;[Block]
						Height = 296 * MenuScale
						
						Temp = Ceil(Float(SavedGamesAmount) / 5.0) - 1
						If mm\CurrMenuPage < Temp And DelSave = Null
							If UpdateMenuButton(x + Width - 50 * MenuScale, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big) Then ChangePage(mm\CurrMenuPage + 1)
						Else
							UpdateMenuButton(x + Width - 50 * MenuScale, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > 0 And DelSave = Null
							If UpdateMenuButton(x, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big) Then ChangePage(mm\CurrMenuPage - 1)
						Else
							UpdateMenuButton(x, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > Temp Then ChangePage(mm\CurrMenuPage - 1)
						
						If SavedGamesAmount > 0
							x = x + 20 * MenuScale
							y = y + 20 * MenuScale
							
							CurrSave = First Save
							
							For i = 0 To 4 + (5 * mm\CurrMenuPage)
								If i > 0 Then CurrSave = After CurrSave
								If CurrSave = Null Then Exit
								If i >= (5 * mm\CurrMenuPage)
									If DelSave = Null
										If CurrSave\Version <> VersionNumber
											UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True, 255, 0, 0)
										ElseIf UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"))
											LoadEntities()
											LoadSounds()
											LoadGame(CurrSave\Name)
											InitLoadGame()
											ShouldDeleteGadgets = True
											MainMenuOpen = False
											Return
										EndIf
										
										If UpdateMenuButton(x + 420 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"))
											DelSave = CurrSave
											Exit
										EndIf
									Else
										If CurrSave\Version <> VersionNumber
											UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True, 255, 0, 0)
										Else
											UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True)
										EndIf
										UpdateMenuButton(x + 420 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"), Font_Default, False, True)
									EndIf
									If CurrSave = Last Save Then Exit
									y = y + 80 * MenuScale
								EndIf
							Next
							
							If DelSave <> Null
								x = 739 * MenuScale
								y = 376 * MenuScale
								
								If UpdateMenuButton(x + 74 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "yes"))
									DeleteGame(DelSave)
									ShouldDeleteGadgets = True
								EndIf
								If UpdateMenuButton(x + 246 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "no"))
									DelSave = Null
									ShouldDeleteGadgets = True
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case MainMenuTab_Load_Map
						;[Block]
						Height = 350 * MenuScale
						
						Temp = Ceil(Float(CustomMapsAmount) / 5.0) - 1
						If mm\CurrMenuPage < Temp And DelCustomMap = Null
							If UpdateMenuButton(x + Width - 50 * MenuScale, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big) Then ChangePage(mm\CurrMenuPage + 1)
						Else
							UpdateMenuButton(x + Width - 50 * MenuScale, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, ">", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > 0 And DelCustomMap = Null
							If UpdateMenuButton(x, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big) Then ChangePage(mm\CurrMenuPage - 1)
						Else
							UpdateMenuButton(x, y + 440 * MenuScale, 50 * MenuScale, 50 * MenuScale, "<", Font_Default_Big, False, True)
						EndIf
						If mm\CurrMenuPage > Temp Then ChangePage(mm\CurrMenuPage - 1)
						
						If CustomMapsAmount > 0
							x = x + 20 * MenuScale
							y = y + 20 * MenuScale
							
							CurrCustomMap = First CustomMaps
							
							For i = 0 To 4 + (5 * mm\CurrMenuPage)
								If i > 0 Then CurrCustomMap = After CurrCustomMap
								If CurrCustomMap = Null Then Exit
								If i >= (5 * mm\CurrMenuPage)
									If DelCustomMap = Null
										If UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"))
											SelectedCustomMap = CurrCustomMap
											mm\MainMenuTab = MainMenuTab_New_Game
											ShouldDeleteGadgets = True
										EndIf
										
										If UpdateMenuButton(x + 420 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"))
											DelCustomMap = CurrCustomMap
											Exit
										EndIf
									Else
										UpdateMenuButton(x + 300 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "btnload"), Font_Default, False, True)
										UpdateMenuButton(x + 420 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "delete"), Font_Default, False, True)
									EndIf
									If CurrCustomMap = Last CustomMaps Then Exit
									y = y + 80 * MenuScale
								EndIf
							Next
							
							If DelCustomMap <> Null
								x = 739 * MenuScale
								y = 376 * MenuScale
								
								If UpdateMenuButton(x + 74 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "yes"))
									DeleteCustomMap(DelCustomMap)
									ShouldDeleteGadgets = True
								EndIf
								If UpdateMenuButton(x + 246 * MenuScale, y + 150 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("menu", "no"))
									DelCustomMap = Null
									ShouldDeleteGadgets = True
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case MainMenuTab_Options
						;[Block]
						Height = 60 * MenuScale
						
						If UpdateMenuButton(x + 20 * MenuScale, y + 15 * MenuScale, Width / 5 + 420 * MenuScale, Height, GetLocalString("options", "grap"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Graphics)
						If UpdateMenuButton(x + 20 * MenuScale, y + 85  * MenuScale, Width / 5 + 420 * MenuScale, Height, GetLocalString("options", "audio"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Audio)
						If UpdateMenuButton(x + 20 * MenuScale, y + 155 * MenuScale, Width / 5 + 420 * MenuScale, Height, GetLocalString("options", "ctrl"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Controls)
						If UpdateMenuButton(x + 20 * MenuScale, y + 225 * MenuScale, Width / 5 + 420 * MenuScale, Height, GetLocalString("options", "avc"), Font_Default_Big) Then ChangeOptionTab(MainMenuTab_Options_Advanced)
						;[End Block]
				End Select
			Else
				x = 469 * MenuScale
				Height = 60 * MenuScale
				
				Select mm\MainMenuTab
					Case MainMenuTab_Options_Graphics
						;[Block]
						Local SliderVeryLow$ = GetLocalString("options", "slider.very.low")
						Local SliderLow$ = GetLocalString("options", "slider.low")
						Local SliderMedium$ = GetLocalString("options", "slider.medium")
						Local SliderHigh$ = GetLocalString("options", "slider.high")
						Local SliderUltra$ = GetLocalString("options", "slider.ultra")
						
						y = y + 20 * MenuScale
						
						opt\ScreenGamma = UpdateMenuSlideBar(x, y + 5 * MenuScale, 150 * MenuScale, opt\ScreenGamma * 50.0, 1) / 50.0
						
						y = y + 45 * MenuScale
						
						opt\CurrFOV = (UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\CurrFOV * 2.0, 2) / 2.0)
						opt\FOV = opt\CurrFOV + 40
						
						y = y + 45 * MenuScale
						
						opt\ParticleAmount = UpdateMenuSlider3(x, y, 150 * MenuScale, opt\ParticleAmount, 3, GetLocalString("options", "min"), GetLocalString("options", "red"), GetLocalString("options", "full"))
						
						y = y + 40 * MenuScale
						
						Local PrevTextureQuality% = opt\TextureQuality
						
						opt\TextureQuality = UpdateMenuSlider3(x, y, 150 * MenuScale, opt\TextureQuality, 4, SliderLow, SliderMedium, SliderHigh)
						Select opt\TextureQuality
							Case 0
								;[Block]
								opt\TextureQualityLevel = 4
								;[End Block]
							Case 1
								;[Block]
								opt\TextureQualityLevel = 2
								;[End Block]
							Case 2
								;[Block]
								opt\TextureQualityLevel = 1
								;[End Block]
						End Select
						If opt\TextureQuality <> PrevTextureQuality Then ClearUnusedTextures()
						
						y = y + 40 * MenuScale
						
						opt\Anisotropic = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\Anisotropic, 5, GetLocalString("options", "tri"), "2X", "4X", "8X", "16X")
						SetTextureAnisotropic()
						
						y = y + 40 * MenuScale
						
						opt\LightingQuality = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\LightingQuality, 6, SliderVeryLow, SliderLow, SliderMedium, SliderHigh, SliderUltra)
						
						y = y + 40 * MenuScale
						
						opt\Reflections = UpdateMenuSlider5(x, y, 150 * MenuScale, opt\Reflections, 7, SliderVeryLow, SliderLow, SliderMedium, SliderHigh, SliderUltra)
						
						x = x - 65 * MenuScale
						y = y + 35 * MenuScale
						
						opt\VolumetricLights = UpdateMenuTick(x, y, opt\VolumetricLights)
						opt\AntiAliasing = UpdateMenuTick(x + 210 * MenuScale, y, opt\AntiAliasing)
						
						y = y + 25 * MenuScale
						
						opt\VSync = UpdateMenuTick(x, y, opt\VSync)
						opt\VignetteEnabled = UpdateMenuTick(x + 210 * MenuScale, y, opt\VignetteEnabled)
						
						y = y + 25 * MenuScale
						
						opt\Bloom = UpdateMenuTick(x, y, opt\Bloom)
						opt\MotionBlur = UpdateMenuTick(x + 210 * MenuScale, y, opt\MotionBlur)
						
						y = y + 25 * MenuScale
						
						opt\ParallaxOcclusion = UpdateMenuTick(x, y, opt\ParallaxOcclusion)
						opt\AmbientOcclusion = UpdateMenuTick(x + 210 * MenuScale, y, opt\AmbientOcclusion)
						
						y = y + (25 * MenuScale)
						
						opt\HDRRender = UpdateMenuTick(x, y, opt\HDRRender)
						
						ApplyGraphicOptions()
						;[End Block]
					Case MainMenuTab_Options_Audio
						;[Block]
						x = 469 * MenuScale
						y = 376 * MenuScale
						
						Width = 580 * MenuScale
						Height = 60 * MenuScale
						
						y = y + 20 * MenuScale
						
						opt\PrevMasterVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\MasterVolume * 100.0, 1) / 100.0
						opt\MasterVolume = opt\PrevMasterVolume
						
						y = y + 40 * MenuScale
						
						opt\MusicVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\MusicVolume * 100.0, 2) / 100.0
						
						y = y + 40 * MenuScale
						
						opt\SFXVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\SFXVolume * 100.0, 3) / 100.0
						
						y = y + 40 * MenuScale
						
						opt\VoiceVolume = UpdateMenuSlideBar(x, y, 150 * MenuScale, opt\VoiceVolume * 100.0, 4) / 100.0
						
						y = y + 40 * MenuScale
						
						opt\EnableSFXRelease = UpdateMenuTick(x, y, opt\EnableSFXRelease)
						If opt\PrevEnableSFXRelease <> opt\EnableSFXRelease
							If opt\EnableSFXRelease
								For snd.Sound = Each Sound
									For i = 0 To MaxChannelsAmount - 1
										StopChannel(snd\Channels[i]) : snd\Channels[i] = 0
									Next
									If snd\InternalHandle <> 0 Then FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
									snd\ReleaseTime = 0
								Next
							Else
								For snd.Sound = Each Sound
									If snd\InternalHandle = 0 Then snd\InternalHandle = LoadSound(snd\Name)
								Next
							EndIf
							opt\PrevEnableSFXRelease = opt\EnableSFXRelease
						EndIf
						
						y = y + 30 * MenuScale
						
						Local PrevEnableUserTracks% = opt\UserTrackMode
						
						If UpdateMenuButton(x, y, 20 * MenuScale, 20 * MenuScale, ">")
							If opt\UserTrackMode < 2
								opt\UserTrackMode = opt\UserTrackMode + 1
							Else
								opt\UserTrackMode = 0
							EndIf
						EndIf
						
						If opt\UserTrackMode > 0
							If UpdateMenuButton(x - 290 * MenuScale, y + 30 * MenuScale, 220 * MenuScale, 30 * MenuScale, GetLocalString("options", "scantracks"))
								UserTrackCheck = 0
								UserTrackCheck2 = 0
								
								Local DirPath$ = "SFX\Radio\UserTracks\"
								
								If FileType(DirPath) <> 2 Then CreateDir(DirPath)
								
								Local Dir% = ReadDir(DirPath)
								
								Repeat
									File = NextFile(Dir)
									If File = "" Then Exit
									If FileType(DirPath + File) = 1
										UserTrackCheck = UserTrackCheck + 1
										Test = LoadSound(DirPath + File)
										If Test <> 0 Then UserTrackCheck2 = UserTrackCheck2 + 1
										FreeSound(Test) : Test = 0
									EndIf
								Forever
								CloseDir(Dir)
							EndIf
							y = y + 40 * MenuScale
						EndIf
						
						y = y + 30 * MenuScale
						
						Local PrevEnableSubtitles% = opt\EnableSubtitles
						
						opt\EnableSubtitles = UpdateMenuTick(x, y, opt\EnableSubtitles)
						
						If PrevEnableSubtitles Lor PrevEnableUserTracks <> 1 Then ShouldDeleteGadgets = (PrevEnableSubtitles <> opt\EnableSubtitles) Lor PrevEnableUserTracks <> opt\UserTrackMode
						;[End Block]
					Case MainMenuTab_Options_Controls
						;[Block]
						y = y + 20 * MenuScale
						
						opt\MouseSensitivity = (UpdateMenuSlideBar(x, y, 150 * MenuScale, (opt\MouseSensitivity + 0.5) * 100.0, 1) / 100.0) - 0.5
						
						y = y + 40 * MenuScale
						
						opt\MouseSmoothing = UpdateMenuSlideBar(x, y, 150 * MenuScale, (opt\MouseSmoothing) * 50.0, 2) / 50.0
						
						y = y + 40 * MenuScale
						
						opt\InvertMouseX = UpdateMenuTick(x, y, opt\InvertMouseX)
						
						y = y + 40 * MenuScale
						
						opt\InvertMouseY = UpdateMenuTick(x, y, opt\InvertMouseY)
						
						y = y + 60 * MenuScale
						
						UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, MaxKeyNames)], Font_Default, 3)
						UpdateMenuInputBox(x + 140 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, MaxKeyNames)], Font_Default, 8)
						
						y = y + 20 * MenuScale
						
						UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, MaxKeyNames)], Font_Default, 4)
						UpdateMenuInputBox(x + 140 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, MaxKeyNames)], Font_Default, 9)
						
						y = y + 20 * MenuScale
						
						UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, MaxKeyNames)], Font_Default, 5)
						UpdateMenuInputBox(x + 140 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, MaxKeyNames)], Font_Default, 10)
						
						y = y + 20 * MenuScale
						
						UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, MaxKeyNames)], Font_Default, 6)
						UpdateMenuInputBox(x + 140 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, MaxKeyNames)], Font_Default, 11)
						
						y = y + 20 * MenuScale
						
						UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, MaxKeyNames)], Font_Default, 7)
						UpdateMenuInputBox(x + 140 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, MaxKeyNames)], Font_Default, 13)
						
						If opt\CanOpenConsole
							y = y + 20 * MenuScale
							
							UpdateMenuInputBox(x - 150 * MenuScale, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, MaxKeyNames)], Font_Default, 12)
						EndIf
						
						Local TempKey%
						
						For i = 0 To 227
							If KeyHit(i)
								TempKey = i
								Exit
							EndIf
						Next
						If TempKey <> 0
							Select SelectedInputBox
								Case 3
									;[Block]
									key\MOVEMENT_UP = TempKey
									;[End Block]
								Case 4
									;[Block]
									key\MOVEMENT_LEFT = TempKey
									;[End Block]
								Case 5
									;[Block]
									key\MOVEMENT_DOWN = TempKey
									;[End Block]
								Case 6
									;[Block]
									key\MOVEMENT_RIGHT = TempKey
									;[End Block]
								Case 7
									;[Block]
									key\SPRINT = TempKey
									;[End Block]
								Case 8
									;[Block]
									key\CROUCH = TempKey
									;[End Block]
								Case 9
									;[Block]
									key\BLINK = TempKey
									;[End Block]
								Case 10
									;[Block]
									key\INVENTORY = TempKey
									;[End Block]
								Case 11
									;[Block]
									key\SAVE = TempKey
									;[End Block]
								Case 12
									;[Block]
									key\CONSOLE = TempKey
									;[End Block]
								Case 13
									;[Block]
									key\SCREENSHOT = TempKey
									;[End Block]
							End Select
							SelectedInputBox = 0
						EndIf
						;[End Block]
					Case MainMenuTab_Options_Advanced
						;[Block]
						y = y + 20 * MenuScale
						
						opt\HUDEnabled = UpdateMenuTick(x, y, opt\HUDEnabled)
						
						y = y + 30 * MenuScale
						
						opt\FirstPersonBodyEnabled = UpdateMenuTick(x, y, opt\FirstPersonBodyEnabled)
						
						y = y + 30 * MenuScale
						
						opt\DirectSight = UpdateMenuTick(x, y, opt\DirectSight)
						
						y = y + 30 * MenuScale
						
						opt\NumericSeed = UpdateMenuTick(x, y, opt\NumericSeed)
						
						y = y + 30 * MenuScale
						
						opt\CanOpenConsole = UpdateMenuTick(x, y, opt\CanOpenConsole)
						
						y = y + 30 * MenuScale
						
						opt\AchvMsgEnabled = UpdateMenuTick(x, y, opt\AchvMsgEnabled)
						
						y = y + 30 * MenuScale
						
						opt\AutoSaveEnabled = UpdateMenuTick(x, y, opt\AutoSaveEnabled)
						
						y = y + 30 * MenuScale
						
						opt\ShowFPS = UpdateMenuTick(x, y, opt\ShowFPS)
						
						y = y + 30 * MenuScale
						
						Local PrevCurrFrameLimit% = opt\CurrFrameLimit > 0.0
						
						If UpdateMenuTick(x, y, opt\CurrFrameLimit > 0.0)
							opt\CurrFrameLimit = UpdateMenuSlideBar(x - 160 * MenuScale, y + 40 * MenuScale, 150 * MenuScale, opt\CurrFrameLimit * 100.0, 1) / 100.0
							opt\CurrFrameLimit = Max(opt\CurrFrameLimit, 0.01)
							opt\FrameLimit = 20 + (opt\CurrFrameLimit * 280.0)
							
							y = y + 80 * MenuScale
						Else
							opt\CurrFrameLimit = 0.0
							opt\FrameLimit = 0
							
							y = y + 30 * MenuScale
						EndIf
						
						If PrevCurrFrameLimit Then ShouldDeleteGadgets = (PrevCurrFrameLimit <> opt\CurrFrameLimit)
						
						opt\SmoothBars = UpdateMenuTick(x, y, opt\SmoothBars)
						
						y = y + 30 * MenuScale
						
						opt\PlayStartup = UpdateMenuTick(x, y, opt\PlayStartup)
						
						y = y + 30 * MenuScale
						
						opt\LauncherEnabled = UpdateMenuTick(x, y, opt\LauncherEnabled)
						
						y = y + 40 * MenuScale
						
						If UpdateMenuButton(x - 290 * MenuScale, y, 195 * MenuScale, 30 * MenuScale, GetLocalString("options", "reset"))
							DeleteFile(OptionFile)
							ResetOptionsINI()
							SaveOptionsINI(True)
						EndIf
						;[End Block]
				End Select
			EndIf
			
			x = 159 * MenuScale
			y = 286 * MenuScale
			
			Width = 400 * MenuScale
			Height = 70 * MenuScale
			
			If mm\MainMenuTab <> MainMenuTab_Options_Audio
				UserTrackCheck = 0
				UserTrackCheck2 = 0
			EndIf
			
			If DelSave = Null And DelCustomMap = Null
				If UpdateMenuButton(x + Width + 20 * MenuScale, y, 580 * MenuScale - Width - 20 * MenuScale, Height, GetLocalString("menu", "back")) Lor KeyDown(1)
					Select mm\MainMenuTab
						Case MainMenuTab_New_Game
							;[Block]
							IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
							For sv.Save = Each Save
								Delete(sv)
							Next
							For cm.CustomMaps = Each CustomMaps
								Delete(cm)
							Next
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
						Case MainMenuTab_Load_Game
							;[Block]
							mm\CurrMenuPage = 0
							For sv.Save = Each Save
								Delete(sv)
							Next
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
						Case MainMenuTab_Load_Map ; ~ Move back to the "New Game" tab
							;[Block]
							mm\CurrMenuPage = 0
							For cm.CustomMaps = Each CustomMaps
								Delete(cm)
							Next
							mm\MainMenuTab = MainMenuTab_New_Game
							;[End Block]
						Case MainMenuTab_Options
							;[Block]
							SaveOptionsINI()
							
							UserTrackCheck = 0
							UserTrackCheck2 = 0
							
							TextureAnisotropic(opt\AnisotropicLevel)
							
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
						Case MainMenuTab_Options_Graphics, MainMenuTab_Options_Audio, MainMenuTab_Options_Controls, MainMenuTab_Options_Advanced ; ~ Save the options
							;[Block]
							mm\MainMenuTab = MainMenuTab_Options
							;[End Block]
						Default
							;[Block]
							mm\MainMenuTab = MainMenuTab_Default
							;[End Block]
					End Select
					ResetInput()
				EndIf
			Else
				UpdateMenuButton(x + Width + 20 * MenuScale, y, 580 * MenuScale - Width - 20 * MenuScale, Height, GetLocalString("menu", "back"), Font_Default, False, True)
			EndIf
		EndIf
	Wend
	
	; ~ Go out of function immediately if the game has been start
	If (Not MainMenuOpen) Then Return
	
	RenderMainMenu()
	
	CatchErrors("Uncaught: UpdateMainMenu()")
End Function

Function RenderMainMenu%()
	CatchErrors("RenderMainMenu()")
	
	Local x%, y%, Width%, Height%, Temp%, i%
	Local tX#, tY#, tW#, tH#
	Local TempStr$, TempStr2$, Name$
	Local Clr%
	
	;RenderGamma()
	
	ShowPointer()
	
	DrawBlock(mma\BackGround, 0, 0)
	If (MilliSec Mod mm\MainMenuBlinkTimer[0]) >= Rand(mm\MainMenuBlinkDuration[0]) Then DrawBlock(mma\SCP173, opt\GraphicWidth - mma\SCP173Width, opt\GraphicHeight - mma\SCP173Height)
	SetFontEx(fo\FontID[Font_Default])
	If mm\MainMenuBlinkTimer[1] < mm\MainMenuBlinkDuration[1]
		Color(50, 50, 50)
		TextEx(mm\MainMenuStrX + Rand(-5, 5), mm\MainMenuStrY + Rand(-5, 5), mm\MainMenuStr, True)
		If mm\MainMenuBlinkTimer[1] < 0.0
			mm\MainMenuBlinkTimer[1] = Rnd(700.0, 800.0)
			mm\MainMenuBlinkDuration[1] = Rnd(10.0, 35.0)
			mm\MainMenuStrX = Rand(700, 1000) * MenuScale
			mm\MainMenuStrY = Rand(100, 600) * MenuScale
			
			Select Rand(0, 23)
				Case 0, 2, 3
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "dontblink")
					;[End Block]
				Case 4, 5
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "scp")
					;[End Block]
				Case 6, 7, 8
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "happyending")
					;[End Block]
				Case 9, 10, 11
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "scream")
					;[End Block]
				Case 12, 19
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "nil")
					;[End Block]
				Case 13
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "menuno")
					;[End Block]
				Case 14
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "bwg")
					;[End Block]
				Case 15
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "173care")
					;[End Block]
				Case 16
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "9341")
					;[End Block]
				Case 17
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "079doors")
					;[End Block]
				Case 18
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "???")
					;[End Block]
				Case 20
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "079king")
					;[End Block]
				Case 21
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "spiral")
					;[End Block]
				Case 22
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "damage")
					;[End Block]
				Case 23
					;[Block]
					mm\MainMenuStr = GetLocalString("menu", "howl")
					;[End Block]
			End Select
		EndIf
	EndIf
	SetFontEx(fo\FontID[Font_Default_Big])
	DrawBlock(mma\SECURE_CONTAIN_PROTECT, mo\Viewport_Center_X - mma\SECURE_CONTAIN_PROTECT_WIDTH / 2, opt\GraphicHeight - 20 * MenuScale - mma\SECURE_CONTAIN_PROTECT_HEIGHT)
	If opt\GraphicWidth > 1240 Then RenderTiledImageRect(MenuWhite, 0, 5 * MenuScale, 512.0 * MenuScale, 5.0 * MenuScale, 985 * MenuScale, 407 * MenuScale, (opt\GraphicWidth - 940 * MenuScale), 5 * MenuScale)
	If mm\MainMenuTab <> MainMenuTab_Default
		x = 159 * MenuScale
		y = 286 * MenuScale
		
		Width = 400 * MenuScale
		Height = 70 * MenuScale
		
		RenderFrame(x, y, Width, Height)
		
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default_Big])
		Select mm\MainMenuTab
			Case MainMenuTab_New_Game
				;[Block]
				TempStr = GetLocalString("menu", "new")
				;[End Block]
			Case MainMenuTab_Load_Game
				;[Block]
				TempStr = GetLocalString("menu", "load")
				;[End Block]
			Case MainMenuTab_Load_Map
				;[Block]
				TempStr = GetLocalString("menu", "loadmap")
				;[End Block]
			Case MainMenuTab_Options
				;[Block]
				TempStr = GetLocalString("menu", "options")
				;[End Block]
			Case MainMenuTab_Options_Graphics
				;[Block]
				TempStr = GetLocalString("options", "grap")
				;[End Block]
			Case MainMenuTab_Options_Audio
				;[Block]
				TempStr = GetLocalString("options", "audio")
				;[End Block]
			Case MainMenuTab_Options_Controls
				;[Block]
				TempStr = GetLocalString("options", "ctrl")
				;[End Block]
			Case MainMenuTab_Options_Advanced
				;[Block]
				TempStr = GetLocalString("options", "avc")
				;[End Block]
		End Select
		TextEx(x + Width / 2, y + Height / 2, TempStr, True, True)
		
		y = y + Height + 20 * MenuScale
		
		Width = 580 * MenuScale
		
		If mm\MainMenuTab < MainMenuTab_Options_Graphics
			Select mm\MainMenuTab
				Case MainMenuTab_New_Game
					;[Block]
					Height = 345 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					SetFontEx(fo\FontID[Font_Default])
					
					TextEx(x + 20 * MenuScale, y + 25 * MenuScale, GetLocalString("menu", "new.name"))
					
					If SelectedCustomMap = Null
						TempStr = Format(GetLocalString("menu", "new.seed2"), "")
					Else
						TempStr = Format(GetLocalString("menu", "new.map"), "")
						RenderFrame(x + 150 * MenuScale, y + 55 * MenuScale, 200 * MenuScale, 30 * MenuScale, x Mod 256, y Mod 256, True)
						
						Color(255, 0, 0)
						Name = ConvertToUTF8(SelectedCustomMap\Name)
						If Len(Name) > 15
							TempStr2 = Left(Name, 14) + ".."
						Else
							TempStr2 = Name
						EndIf
						TextEx(x + 250 * MenuScale, y + 70 * MenuScale, TempStr2, True, True)
					EndIf
					Color(255, 255, 255)
					TextEx(x + 20 * MenuScale, y + 65 * MenuScale, TempStr)
					
					TextEx(x + 20 * MenuScale, y + 115 * MenuScale, GetLocalString("menu", "new.intro"))
					
					TextEx(x + 20 * MenuScale, y + 155 * MenuScale, GetLocalString("menu", "new.diff"))
					For i = DIFFICULTY_SAFE To DIFFICULTY_ESOTERIC
						Color(difficulties[i]\R, difficulties[i]\G, difficulties[i]\B)
						TextEx(x + 50 * MenuScale, y + (185 + 30 * i) * MenuScale, difficulties[i]\Name)
					Next
					
					Color(255, 255, 255)
					RenderFrame(x + 150 * MenuScale, y + 170 * MenuScale, 410 * MenuScale, 160 * MenuScale)
					
					If SelectedDifficulty\Customizable
						; ~ Save type
						Select SelectedDifficulty\SaveType
							Case DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
								;[Block]
								TempStr = GetLocalString("menu", "new.saveany")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS
								;[Block]
								TempStr = GetLocalString("menu", "new.savescreen")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT
								;[Block]
								TempStr = GetLocalString("menu", "new.savequit")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_NO_SAVES
								;[Block]
								TempStr = GetLocalString("menu", "new.saveno")
								;[End Block]
						End Select
						TextEx(x + 200 * MenuScale, y + 186 * MenuScale, GetLocalString("menu", "new.savetype") + TempStr)
						
						; ~ Aggressive NPCs
						TextEx(x + 200 * MenuScale, y + 215 * MenuScale, GetLocalString("menu", "new.dangernpc"))
						; ~ Inventory slots
						TextEx(x + 200 * MenuScale, y + 246 * MenuScale, Format(GetLocalString("menu", "new.invslots"), SelectedDifficulty\InventorySlots))
						
						; ~ Other factor's difficulty
						Select SelectedDifficulty\OtherFactors
							Case DIFFICULTY_FACTOR_EASY
								;[Block]
								TempStr = GetLocalString("menu", "new.easy")
								;[End Block]
							Case DIFFICULTY_FACTOR_NORMAL
								;[Block]
								TempStr = GetLocalString("menu", "new.normal")
								;[End Block]
							Case DIFFICULTY_FACTOR_HARD
								;[Block]
								TempStr = GetLocalString("menu", "new.hard")
								;[End Block]
							Case DIFFICULTY_FACTOR_EXTREME
								;[Block]
								TempStr = GetLocalString("menu", "new.extreme")
								;[End Block]
						End Select
						TextEx(x + 200 * MenuScale, y + 276 * MenuScale, Format(GetLocalString("menu", "new.factors"), TempStr))
					Else
						RowText(SelectedDifficulty\Description, x + 160 * MenuScale, y + 180 * MenuScale, 390 * MenuScale, 140 * MenuScale)
						RenderFrame(x + 590 * MenuScale, y + 50 * MenuScale, 350 * MenuScale, 90 * MenuScale)
						Select SelectedDifficulty\SaveType
							Case DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
								;[Block]
								TempStr = GetLocalString("menu", "new.saveany")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS
								;[Block]
								TempStr = GetLocalString("menu", "new.savescreen")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT
								;[Block]
								TempStr = GetLocalString("menu", "new.savequit")
								;[End Block]
							Case DIFFICULTY_SAVE_TYPE_NO_SAVES
								;[Block]
								TempStr = GetLocalString("menu", "new.saveno")
								;[End Block]
						End Select
						TextEx(x + 600 * MenuScale, y + 58 * MenuScale, GetLocalString("menu", "new.savetype") + TempStr)
						
						Select SelectedDifficulty\AggressiveNPCs
							Case 0
								;[Block]
								TempStr = GetLocalString("menu", "no")
								;[End Block]
							Case 1
								;[Block]
								TempStr = GetLocalString("menu", "yes")
								;[End Block]
						End Select
						TextEx(x + 600 * MenuScale, y + 74 * MenuScale, GetLocalString("menu", "new.dangernpc") + ": "  + TempStr)
						
						TextEx(x + 600 * MenuScale, y + 90 * MenuScale, Format(GetLocalString("menu", "new.invslots"), SelectedDifficulty\InventorySlots))
						
						Select SelectedDifficulty\OtherFactors
							Case DIFFICULTY_FACTOR_EASY
								;[Block]
								TempStr = GetLocalString("menu", "new.easy")
								;[End Block]
							Case DIFFICULTY_FACTOR_NORMAL
								;[Block]
								TempStr = GetLocalString("menu", "new.normal")
								;[End Block]
							Case DIFFICULTY_FACTOR_HARD
								;[Block]
								TempStr = GetLocalString("menu", "new.hard")
								;[End Block]
							Case DIFFICULTY_FACTOR_EXTREME
								;[Block]
								TempStr = GetLocalString("menu", "new.extreme")
								;[End Block]
						End Select
						
						TextEx(x + 600 * MenuScale, y + 106 * MenuScale, Format(GetLocalString("menu", "new.factors"), TempStr))
						
						If SelectedDifficulty\Name = difficulties[DIFFICULTY_APOLLYON]\Name Then TextEx(x + 600 * MenuScale, y + 122 * MenuScale, GetLocalString("menu", "nohud"))
					EndIf
					
					SetFontEx(fo\FontID[Font_Default_Big])
					;[End Block]
				Case MainMenuTab_Load_Game
					;[Block]
					Height = 430 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					y = 376 * MenuScale
					Height = 296 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default_Big])
					
					RenderFrame(x + 60 * MenuScale, y + 440 * MenuScale, Width - 120 * MenuScale, 50 * MenuScale)
					
					TextEx(x + Width / 2, y + 465 * MenuScale, Format(Format(GetLocalString("menu", "page"), Int(Max((mm\CurrMenuPage + 1), 1)), "{0}"), Int(Max((Int(Ceil(Float(SavedGamesAmount) / 5.0))), 1)), "{1}"), True, True)
					
					SetFontEx(fo\FontID[Font_Default])
					
					If SavedGamesAmount = 0
						RowText(GetLocalString("menu", "save.nosaves"), x + 20 * MenuScale, y + 20 * MenuScale, 540 * MenuScale, 390 * MenuScale)
					Else
						x = x + 20 * MenuScale
						y = y + 20 * MenuScale
						
						CurrSave = First Save
						
						For i = 0 To 4 + (5 * mm\CurrMenuPage)
							If i > 0 Then CurrSave = After CurrSave
							If CurrSave = Null Then Exit
							If i >= (5 * mm\CurrMenuPage)
								RenderFrame(x, y, 540 * MenuScale, 70 * MenuScale)
								
								Clr = 255 - (255 * (CurrSave\Version <> VersionNumber))
								Color(255, Clr, Clr)
								
								Name = CurrSave\Name
								If Len(Name) > 10
									TempStr2 = Left(Name, 9) + ".."
								Else
									TempStr2 = Name
								EndIf
								TextEx(x + 20 * MenuScale, y + 10 * MenuScale, TempStr2)
								If Len(CurrSave\Seed) > 16
									TempStr2 = Left(CurrSave\Seed, 15) + ".."
								Else
									TempStr2 = CurrSave\Seed
								EndIf
								TextEx(x + 150 * MenuScale, y + 10 * MenuScale, TempStr2)
								TextEx(x + 20 * MenuScale, y + 30 * MenuScale, CurrSave\Time)
								TextEx(x + 150 * MenuScale, y + 30 * MenuScale, CurrSave\Date)
								TextEx(x + 20 * MenuScale, y + 50 * MenuScale, "v" + CurrSave\Version)
								If CurrSave\Version <> VersionNumber
									Color(255, 0, 0)
								Else
									For Temp = DIFFICULTY_SAFE To DIFFICULTY_ESOTERIC
										If CurrSave\Difficulty = difficulties[Temp]\Name
											Color(difficulties[Temp]\R, difficulties[Temp]\G, difficulties[Temp]\B)
											Exit
										EndIf
									Next
								EndIf
								TextEx(x + 150 * MenuScale, y + 50 * MenuScale, CurrSave\Difficulty)
								
								If CurrSave = Last Save Then Exit
								y = y + 80 * MenuScale
							EndIf
						Next
						
						Color(255, 255, 255)
						If DelSave <> Null
							x = 739 * MenuScale
							y = 376 * MenuScale
							RenderFrame(x, y, 420 * MenuScale, 200 * MenuScale)
							RowText(GetLocalString("menu", "save.delete?"), x + 20 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
						EndIf
					EndIf
					;[End Block]
				Case MainMenuTab_Load_Map
					;[Block]
					Height = 430 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					
					y = 376 * MenuScale
					Height = 350 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default_Big])
					
					tX = x + Width
					tY = y
					tW = 400 * MenuScale
					tH = 150 * MenuScale
					
					RenderFrame(x + 60 * MenuScale, y + 440 * MenuScale, Width - 120 * MenuScale, 50 * MenuScale)
					
					TextEx(x + Width / 2, y + 465 * MenuScale, Format(Format(GetLocalString("menu", "page"), Int(Max((mm\CurrMenuPage + 1), 1)), "{0}"), Int(Max((Int(Ceil(Float(CustomMapsAmount) / 5.0))), 1)), "{1}"), True, True)
					
					SetFontEx(fo\FontID[Font_Default])
					
					If CustomMapsAmount = 0
						RowText(GetLocalString("menu", "nomap"), x + 20 * MenuScale, y + 20 * MenuScale, 540 * MenuScale, 390 * MenuScale)
					Else
						x = x + 20 * MenuScale
						y = y + 20 * MenuScale
						
						CurrCustomMap = First CustomMaps
						
						For i = 0 To 4 + (5 * mm\CurrMenuPage)
							If i > 0 Then CurrCustomMap = After CurrCustomMap
							If CurrCustomMap = Null Then Exit
							If i >= (5 * mm\CurrMenuPage)
								RenderFrame(x, y, 540 * MenuScale, 70 * MenuScale)
								
								Name = ConvertToUTF8(CurrCustomMap\Name)
								If Len(Name) > 20
									TextEx(x + 20 * MenuScale, y + 15 * MenuScale, Left(Name, 19) + "..")
								Else
									TextEx(x + 20 * MenuScale, y + 15 * MenuScale, Name)
								EndIf
								TextEx(x + 20 * MenuScale, y + 45 * MenuScale, ConvertToUTF8(CurrCustomMap\Author))
								
								If MouseOn(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale) Lor MouseOn(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale) Then RenderMapCreatorTooltip(tX, tY, tW, tH, CurrCustomMap\Name)
								
								If CurrCustomMap = Last CustomMaps Then Exit
								y = y + 80 * MenuScale
							EndIf
						Next
						
						If DelCustomMap <> Null
							x = 739 * MenuScale
							y = 376 * MenuScale
							RenderFrame(x, y, 420 * MenuScale, 200 * MenuScale)
							RowText(GetLocalString("menu", "map.delete?"), x + 20 * MenuScale, y + 15 * MenuScale, 400 * MenuScale, 200 * MenuScale)
						EndIf
					EndIf
					;[End Block]
				Case MainMenuTab_Options
					;[Block]
					Height = 300 * MenuScale
					
					RenderFrame(x, y, Width, Height)
					;[End Block]
			End Select
		Else
			x = x + 20 * MenuScale
			
			tX = x - 20 * MenuScale + Width
			tY = y
			tW = 400.0 * MenuScale
			tH = 150.0 * MenuScale
			
			Local MouseOnCoord% = 20 * MenuScale
			
			Select mm\MainMenuTab
				Case MainMenuTab_Options_Graphics
					;[Block]
					Height = 440 * MenuScale
					RenderFrame(x - 20 * MenuScale, y, Width, Height)
					
					y = y + 25 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default])
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "gamma"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ScreenGamma, opt\ScreenGamma)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "fov"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FOV)
					
					y = y + 35 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "particle"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParticleAmount, opt\ParticleAmount)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "texquality"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 4 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_TextureQuality)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "filter"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 5 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AnisotropicFiltering)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "lightingquality"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 6 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_LightingQuality)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "reflectionsquality"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 7 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ReflectionsQuality)
					
					y = y + 45 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "volumetriclights"))
					If MouseOn(x + 225 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VolumetricLights)
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "antialias"))
					If MouseOn(x + 435 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AntiAliasing)
					
					y = y + 25 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "vsync"))
					If MouseOn(x + 225 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VSync)
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "vignette"))
					If MouseOn(x + 435 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Vignette)
					
					y = y + 25 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "bloom"))
					If MouseOn(x + 225 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Bloom)
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "motionblur"))
					If MouseOn(x + 435 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MotionBlur)
					
					y = y + 25 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "parallaxocclusion"))
					If MouseOn(x + 225 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParallaxOcclusion)
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "ambientocclusion"))
					If MouseOn(x + 435 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AmbientOcclusion)
					
					y = y + (25 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "hdrrender"))
					If MouseOn(x + 225 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HDRRender)
					;[End Block]
				Case MainMenuTab_Options_Audio
					;[Block]
					Height = (280 + (40 * (opt\UserTrackMode > 0))) * MenuScale
					RenderFrame(x - 20 * MenuScale, y, Width, Height)
					
					y = y + 20 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "mastervolume"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MasterVolume, opt\PrevMasterVolume)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "musicvolume"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MusicVolume, opt\MusicVolume)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "soundvolume"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SoundVolume, opt\SFXVolume)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "voicevolume"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 4 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VoiceVolume, opt\VoiceVolume)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "autorelease"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, Tooltip_SoundAutoRelease)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "trackmode"))
					Select opt\UserTrackMode
						Case 0
							;[Block]
							TempStr = GetLocalString("options", "track.disabled")
							;[End Block]
						Case 1
							;[Block]
							TempStr = GetLocalString("options", "track.repeat")
							;[End Block]
						Case 2
							;[Block]
							TempStr = GetLocalString("options", "track.random")
							;[End Block]
					End Select
					TextEx(x + 330 * MenuScale, y + 5 * MenuScale, TempStr)
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracksMode)
					If opt\UserTrackMode > 0
						If MouseOn(x, y + 30 * MenuScale, 210 * MenuScale, 30 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTrackScan)
						If UserTrackCheck > 0 Then TextEx(x + 240 * MenuScale, y + 40 * MenuScale, Format(Format(GetLocalString("options", "track.found"), UserTrackCheck2, "{0}"), UserTrackCheck, "{1}"))
						y = y + 40 * MenuScale
					EndIf
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "subtitles"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Subtitles)
					;[End Block]
				Case MainMenuTab_Options_Controls
					;[Block]
					Height = (320 + 20 * opt\CanOpenConsole) * MenuScale
					RenderFrame(x - 20 * MenuScale, y, Width, Height)
					
					y = y + 20 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "mousesensitive"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSensitivity, opt\MouseSensitivity)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "mousesmooth"))
					If (MouseOn(x + 290 * MenuScale, y, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSmoothing, opt\MouseSmoothing)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "invertx"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertX)
					
					y = y + 40 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "inverty"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertY)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("menu", "controlconfig"))
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.forward"))
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "key.crouch"))
					
					y = y + 20 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.left"))
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "key.blink"))
					
					y = y + 20 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.backward"))
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "key.inv"))
					
					y = y + 20 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.right"))
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "key.save"))
					
					y = y + 20 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.sprint"))
					TextEx(x + 260 * MenuScale, y + 5 * MenuScale, GetLocalString("options", "key.screenshot"))
					
					If opt\CanOpenConsole
						y = y + 20 * MenuScale
						
						TextEx(x, y + 5 * MenuScale, GetLocalString("options", "key.console"))
					EndIf
					
					If MouseOn(x, y - (60 + 20 * opt\CanOpenConsole) * MenuScale, Width - MouseOnCoord * 2, 120 + (20 * opt\CanOpenConsole)) * MenuScale Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ControlConfiguration)
					;[End Block]
				Case MainMenuTab_Options_Advanced
					;[Block]
					Height = (490 - (50 * (opt\CurrFrameLimit = 0.0))) * MenuScale
					RenderFrame(x - 20 * MenuScale, y, Width, Height)
					
					y = y + 20 * MenuScale
					
					SetFontEx(fo\FontID[Font_Default])
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "hud"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HUD)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "fpb"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FirstPersonBody)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "ds"))
					If MouseOn(x + (290 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_DirectSight)
						
					y = y + (30 * MenuScale)
					
					TextEx(x, y + (5 * MenuScale), GetLocalString("options", "uns"))
					If MouseOn(x + (290 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_NumericSeed)
						
					y = y + (30 * MenuScale)
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "console"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Console)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "achipop"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AchievementPopups)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "save"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AutoSave)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "fps"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FPS)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "frame"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
					If opt\CurrFrameLimit > 0.0
						Color(255, 255, 0)
						TextEx(x, y + 45 * MenuScale, opt\FrameLimit + " FPS")
						If (MouseOn(x + 130 * MenuScale, y + MouseOnCoord * 2, MouseOnCoord * 8.2, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
						
						y = y + 50 * MenuScale
					EndIf
					
					y = y + 30 * MenuScale
					
					Color(255, 255, 255)
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "bar"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SmoothBars)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "startvideo"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_StartupVideos)
					
					y = y + 30 * MenuScale
					
					TextEx(x, y + 5 * MenuScale, GetLocalString("options", "launcher"))
					If MouseOn(x + 290 * MenuScale, y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Launcher)
					
					y = y + 40 * MenuScale
					
					If MouseOn(x, y, 195 * MenuScale, 30 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ResetOptions)
					;[End Block]
			End Select
		EndIf
	EndIf
	
	RenderMenuButtons()
	RenderMenuTicks()
	RenderMenuInputBoxes()
	RenderMenuSlideBars()
	RenderMenuSliders()
	
	If opt\HUDEnabled
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Console])
		TextEx(20 * MenuScale, opt\GraphicHeight - 50 * MenuScale, "v" + VersionNumber)
		If opt\ShowFPS
			SetFontEx(fo\FontID[Font_Console])
			TextEx(20 * MenuScale, opt\GraphicHeight - 30 * MenuScale, "FPS: " + fps\RealFPS)
		EndIf
	EndIf
	
	RenderCursor()
	
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderMainMenu()")
End Function

Function RenderCursor%()
	If opt\DisplayMode <> 0 Then Return
	DrawImage(CursorIMG, MousePosX, MousePosY)
End Function

Global TextR# = 0.0, TextG# = 0.0, TextB# = 0.0
Global ChangeColor%

Function RenderLoadingText%(x%, y%, Txt$, AlignX% = False, AlignY% = False)
	If TextR = 0.0
		ChangeColor = True
	ElseIf TextR = 255.0
		ChangeColor = False
	EndIf
	
	If (Not ChangeColor)
		TextR = Max(0.0, TextR - 3.0)
		TextG = Max(0.0, TextG - 3.0)
		TextB = Max(0.0, TextB - 3.0)
	Else
		TextR = Min(TextR + 3.0, 255.0)
		TextG = Min(TextG + 3.0, 255.0)
		TextB = Min(TextB + 3.0, 255.0)
	EndIf
	SetFontEx(fo\FontID[Font_Default])
	Color(TextR, TextG, TextB)
	TextEx(x, y, Txt, AlignX, AlignY)
End Function

Function ResetLoadingTextColor%()
	TextR = 0.0 : TextG = 0.0 : TextB = 0.0
	ChangeColor = True
End Function

Global LoadingScreens%, LoadingScreensDoc% = 0
Global LoadingBack%, LoadingBackWidth%, LoadingBackHeight%, LoadingImage%
Global SelectedLoadingScreens%, LoadingScreenTitle$
Global Descriptions%, DescriptionIndex%, DescriptionDoc%
Global ImageAlignX$, ImageAlignY$
Global CWMText$
Global CurrentLoadingPercent#, CurrentAssetsText$, CurrentLoadingContinuous%, CurrentLoadingSpeed#

Global DescTimer#

Function UpdateLoadingContinuous%()
	If LoadingImage = 0 Then Return
	
	Local Continuous# = Min(CurrentLoadingPercent + CurrentLoadingSpeed, CurrentLoadingContinuous)
	
	If CurrentLoadingContinuous = 0
		RenderLoading(CurrentLoadingPercent, CurrentAssetsText, CurrentLoadingContinuous, CurrentLoadingSpeed)
	Else
		RenderLoading(Continuous, CurrentAssetsText, CurrentLoadingContinuous, CurrentLoadingSpeed)
	EndIf
End Function

Function RenderLoading%(Percent#, Assets$ = "", Continuous% = 0, ContinuosSpeed# = 0.025)
	CatchErrors("RenderLoading(" + Int(Floor(Percent)) + ", " + Assets + ")")
	
	Local PrevSeed% = RndSeed()
	Local x%, y%
	Local ArraySize% = JsonGetArraySize(LoadingScreens)
	
	HidePointer()
	
	If Percent = 0
		If LoadingImage = 0
			DescriptionIndex = 0
			
			SelectedLoadingScreens = JsonGetArrayValue(LoadingScreens, Rand(0, ArraySize - 1))
			LoadingScreenTitle = JsonGetString(JsonGetValue(SelectedLoadingScreens, "title"))
			If JsonIsNull(JsonGetValue(SelectedLoadingScreens, "descriptions"))
				If DescriptionDoc <> 0 Then JsonFreeDocument(DescriptionDoc) : DescriptionDoc = 0
				DescriptionDoc = JsonParseFromString("[" + Chr(34) + Chr(34) + "]")
				Descriptions = JsonGetArray(DescriptionDoc) ; ~ Create an empty description array
			Else
				Descriptions = JsonGetArray(JsonGetValue(SelectedLoadingScreens, "descriptions"))
			EndIf
			ImageAlignX = JsonGetString(JsonGetValue(SelectedLoadingScreens, "align_x"))
			ImageAlignY = JsonGetString(JsonGetValue(SelectedLoadingScreens, "align_y"))
			LoadingImage = ResizeImageEx(LoadImage_Strict("GFX\LoadingScreens\" + JsonGetString(JsonGetValue(SelectedLoadingScreens, "image"))), MenuScale, MenuScale)
			MaskImage(LoadingImage, 0, 0, 0)
			If JsonGetBool(JsonGetValue(SelectedLoadingScreens, "background"))
				If LoadingBack = 0
					LoadingBack = ResizeImageEx(LoadImage_Strict("GFX\LoadingScreens\loading_back.png"), MenuScale, MenuScale)
					MaskImage(LoadingBack, 0, 0, 0)
					LoadingBackWidth = ImageWidth(LoadingBack) / 2
					LoadingBackHeight = ImageHeight(LoadingBack) / 2
				EndIf
			EndIf
			
			If (LoadingScreenTitle = "CWM") Then PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm0.cwm"))
		EndIf
	ElseIf LoadingImage = 0
		SeedRnd(PrevSeed)
		Return
	EndIf
	
	CurrentLoadingPercent = Percent
	If Assets <> "" Then CurrentAssetsText = Assets
	CurrentLoadingContinuous = Continuous
	CurrentLoadingSpeed = ContinuosSpeed
	
	Local FirstLoop% = True
	
	Local DescrArraySize% = JsonGetArraySize(Descriptions)
	Local IsCWM% = (LoadingScreenTitle = "CWM")
	
	SeedRnd(MilliSecs())
	
	Repeat
		ClsColor(0, 0, 0)
		Cls()
		
		If Percent > 20 Then UpdateMusic()
		DescTimer = DescTimer + TICK_DURATION
		If DescTimer > 70.0 * 8.0
			If DescriptionIndex < DescrArraySize - 1
				DescriptionIndex = DescriptionIndex + 1
			Else
				DescriptionIndex = 0
			EndIf
			DescTimer = 0.0
		EndIf
		
		If LoadingBack <> 0 Then DrawBlock(LoadingBack, mo\Viewport_Center_X - LoadingBackWidth, mo\Viewport_Center_Y - LoadingBackHeight)
		
		If ImageAlignX = "center"
			x = mo\Viewport_Center_X - ImageWidth(LoadingImage) / 2
		ElseIf ImageAlignX = "right"
			x = opt\GraphicWidth - ImageWidth(LoadingImage)
		Else ; ~ ImageAlignX = "left"
			x = 0
		EndIf
		
		If ImageAlignY = "center"
			y = mo\Viewport_Center_Y - ImageHeight(LoadingImage) / 2
		ElseIf ImageAlignY = "bottom"
			y = opt\GraphicHeight - ImageHeight(LoadingImage)
		Else ; ~ ImageAlignY = "top"
			y = 0
		EndIf
		
		DrawImage(LoadingImage, x, y)
		
		Local Width% = 300 * MenuScale
		Local Height% = 20 * MenuScale
		Local i%
		
		x = mo\Viewport_Center_X - Width / 2
		y = opt\GraphicHeight - 80 * MenuScale
		
		RenderBar(BlinkMeterIMG, x, y, Width, Height, Percent)
		
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(x + Width / 2, opt\GraphicHeight - 70 * MenuScale, Int(Floor(Percent)) + "%", True, True)
		
		If IsCWM
			If FirstLoop
				If Int(Percent) = 100 And Frac(Percent) = 0 Then PlaySound_Strict(LoadTempSound("SFX\SCP\990\cwm1.cwm"))
			EndIf
			
			Local StrTemp$ = ""
			Local Temp% = Rand(2, 9)
			
			For i = 0 To Temp
				StrTemp = StrTemp + Chr(Rand(48, 122))
			Next
			SetFontEx(fo\FontID[Font_Default_Big])
			TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y - 450 * MenuScale, StrTemp, True, True)
			
			If Percent = 0
				If Rand(5) = 1
					Select Rand(2)
						Case 1
							;[Block]
							CWMText = Format(GetLocalString("menu", "happend"), CurrentTime())
							;[End Block]
						Case 2
							;[Block]
							CWMText = CurrentTime()
							;[End Block]
					End Select
				Else
					Select Rand(16)
						Case 1
							;[Block]
							CWMText = GetLocalString("menu", "990_1")
							;[End Block]
						Case 2
							;[Block]
							CWMText = GetLocalString("menu", "990_2")
							;[End Block]
						Case 3
							;[Block]
							CWMText = GetLocalString("menu", "990_3")
							;[End Block]
						Case 4
							;[Block]
							CWMText = "eof9nsd3jue4iwe1fgj"
							;[End Block]
						Case 5
							;[Block]
							CWMText = GetLocalString("menu", "990_4")
							;[End Block]
						Case 6 
							;[Block]
							CWMText = GetLocalString("menu", "990_5")
							;[End Block]
						Case 7
							;[Block]
							CWMText = "???____??_???__????n?"
							;[End Block]
						Case 8, 9
							;[Block]
							CWMText = GetLocalString("menu", "990_6")
							;[End Block]
						Case 10
							;[Block]
							CWMText = "???????????"
							;[End Block]
						Case 11
							;[Block]
							CWMText = GetLocalString("menu", "990_7")
							;[End Block]
						Case 12
							;[Block]
							CWMText = GetLocalString("menu", "990_8")
							;[End Block]
						Case 13
							;[Block]
							CWMText = GetLocalString("menu", "990_9")
							;[End Block]
						Case 14
							;[Block]
							CWMText = GetLocalString("menu", "990_10")
							;[End Block]
						Case 15
							;[Block]
							CWMText = GetLocalString("menu", "990_11")
							;[End Block]
						Case 16
							;[Block]
							CWMText = GetLocalString("menu", "990_12")
							;[End Block]
					End Select
				EndIf
			EndIf
			
			StrTemp = CWMText
			Temp = Int(Len(CWMText) - Rand(5))
			For i = 0 To Rand(10, 15)
				StrTemp = Replace(CWMText, Mid(CWMText, Rand(Len(StrTemp) - 1), 1), Chr(Rand(130, 250)))
			Next
			SetFontEx(fo\FontID[Font_Default])
			RowText(StrTemp, mo\Viewport_Center_X - 200 * MenuScale, mo\Viewport_Center_Y + 250 * MenuScale, 400 * MenuScale, 300 * MenuScale, True)
		Else
			Color(255, 255, 255)
			SetFontEx(fo\FontID[Font_Default_Big])
			TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y - 450 * MenuScale, LoadingScreenTitle, True, True)
			SetFontEx(fo\FontID[Font_Default])
			RowText(JsonGetString(JsonGetArrayValue(Descriptions, DescriptionIndex)), mo\Viewport_Center_X - 200 * MenuScale, mo\Viewport_Center_Y + 250 * MenuScale, 400 * MenuScale, 300 * MenuScale, True)
		EndIf
		
		If Int(Floor(Percent)) <> 100
			Color(255, 255, 255)
			TextEx(mo\Viewport_Center_X, opt\GraphicHeight - 35 * MenuScale, Format(GetLocalString("loading", "assets"), CurrentAssetsText), True, True)
			
			ResetInput()
		Else
			If IsCWM
				StrTemp = GetLocalString("menu", "wakeup")
			Else
				If FirstLoop Then PlaySound_Strict(LoadTempSound(("SFX\Horror\Horror8.ogg")))
				StrTemp = GetLocalString("menu", "anykey")
			EndIf
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - 35 * MenuScale, StrTemp, True, True)
		EndIf
		
		Flip(0)
		
		FirstLoop = False
		If Int(Floor(Percent)) <> 100
			Exit
		Else
			Delay(16)
		EndIf
		
		If GetKey() <> 0 Lor MouseHit(1)
			ResetLoadingTextColor()
			ResetInput()
			ResetTimingAccumulator()
			SetFontEx(fo\FontID[Font_Default])
			DeleteMenuGadgets()
			FreeImage(LoadingImage) : LoadingImage = 0
			FreeImage(LoadingBack) : LoadingBack = 0
			LoadingBackWidth = 0 : LoadingBackHeight = 0
			SelectedLoadingScreens = 0
			Descriptions = 0 : DescriptionIndex = 0
			ImageAlignX = "" : ImageAlignY = ""
			DescTimer = 0.0
			CurrentLoadingPercent = 0
			CurrentAssetsText = ""
			CurrentLoadingContinuous = 0
			Exit
		EndIf
	Forever
	
	SeedRnd(PrevSeed)
	
	CatchErrors("Uncaught: RenderLoading(" + Int(Floor(Percent)) + ", " + CurrentAssetsText + ")")
End Function

Type InGameMenu
	Field AchievementsMenu% = 0
	Field QuitMenu% = 0
	Field OptionsMenu% = 0
End Type

Global igm.InGameMenu

; ~ Menu Tab Options Constants
;[Block]
Const MenuTab_Options_Graphics% = 2
Const MenuTab_Options_Audio% = 3
Const MenuTab_Options_Controls% = 4
Const MenuTab_Options_Advanced% = 5
;[End Block]

Global MenuOpen%

Function UpdateMenu%()
	CatchErrors("UpdateMenu()")
	
	Local r.Rooms, sc.SecurityCams, amsg.AchievementMsg, it.Items, n.NPCs
	Local z%, i%
	
	If MenuOpen
		If (Not (IsPlayerOutsideFacility() Lor me\Terminated Lor me\Zombie))
			If me\StopHidingTimer = 0.0
				If (EntityDistanceSquared(n_I\Curr173\Collider, me\Collider) < 0.64 And n_I\Curr173\Idle < 2) Lor EntityDistanceSquared(n_I\Curr106\Collider, me\Collider) < 0.64 Lor (n_I\Curr049 <> Null And EntityDistanceSquared(n_I\Curr049\Collider, me\Collider) < 0.64) Lor (n_I\Curr096 <> Null And EntityDistanceSquared(n_I\Curr096\Collider, me\Collider) < 0.64) Then me\StopHidingTimer = 1.0
			ElseIf me\StopHidingTimer < Rnd(120.0, 240.0)
				me\StopHidingTimer = me\StopHidingTimer + 1
			Else
				me\StopHidingTimer = 0.0
				PlaySound_Strict(LoadTempSound("SFX\General\STOPHIDING.ogg"))
				CreateHintMsg(GetLocalString("msg", "stophiding"))
				ShouldDeleteGadgets = True
				MenuOpen = False
				Return
			EndIf
		EndIf
		
		InvOpen = False
		
		Local Width% = ImageWidth(t\ImageID[0])
		Local Height% = ImageHeight(t\ImageID[0])
		Local x% = mo\Viewport_Center_X - (Width / 2)
		Local y% = mo\Viewport_Center_Y - (Height / 2)
		Local Temp%
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		If (Not mo\MouseDown1) Then OnSliderID = 0
		
		If igm\AchievementsMenu <= 0 And igm\OptionsMenu > 0 And igm\QuitMenu <= 0
			If igm\OptionsMenu = 1
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "grap"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Graphics, False)
				If UpdateMenuButton(x, y + (75 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "audio"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Audio, False)
				If UpdateMenuButton(x, y + (150 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "ctrl"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Controls, False)
				If UpdateMenuButton(x, y + (225 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("options", "avc"), Font_Default_Big) Then ChangeOptionTab(MenuTab_Options_Advanced, False)
				
				If UpdateMenuButton(x + (101 * MenuScale), y + (455 * MenuScale), 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
					igm\AchievementsMenu = 0
					igm\OptionsMenu = 0
					igm\QuitMenu = 0
					ResetInput()
					
					ShouldDeleteGadgets = True
				EndIf
			Else
				If UpdateMenuButton(x + (101 * MenuScale), y + (455 * MenuScale), 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
					igm\AchievementsMenu = 0
					igm\OptionsMenu = 1
					igm\QuitMenu = 0
					ResetInput()
					SaveOptionsINI()
					
					ShouldDeleteGadgets = True
				EndIf
				
				x = x + (270 * MenuScale)
				
				Select igm\OptionsMenu
					Case MenuTab_Options_Graphics
						;[Block]
						Local SliderVeryLow$ = GetLocalString("options", "slider.very.low")
						Local SliderLow$ = GetLocalString("options", "slider.low")
						Local SliderMedium$ = GetLocalString("options", "slider.medium")
						Local SliderHigh$ = GetLocalString("options", "slider.high")
						Local SliderUltra$ = GetLocalString("options", "slider.ultra")
						
						opt\ScreenGamma = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\ScreenGamma * 50.0, 1) / 50.0
						
						y = y + (40 * MenuScale)
						
						opt\CurrFOV = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\CurrFOV * 2.0, 2) / 2.0
						opt\FOV = opt\CurrFOV + 40
						CameraZoomValue = Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * (GraphicWidthFloat / GraphicHeightFloat))) / 2.0)
						CameraZoom(Camera, Min(1.0 + (me\CurrCameraZoom / 400.0), 1.1) / CameraZoomValue)
						
						y = y + (45 * MenuScale)
						
						opt\ParticleAmount = UpdateMenuSlider3(x, y, 100 * MenuScale, opt\ParticleAmount, 3, GetLocalString("options", "min"), GetLocalString("options", "red"), GetLocalString("options", "full"))
						
						y = y + (40 * MenuScale)
						
						opt\Anisotropic = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\Anisotropic, 5, "Trilinear", "2x", "4x", "8x", "16x")
						SetTextureAnisotropic()
						
						y = y + (40 * MenuScale)
						
						opt\LightingQuality = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\LightingQuality, 6, SliderVeryLow, SliderLow, SliderMedium, SliderHigh, SliderUltra)
						SetLightingQuality(opt\LightingQuality)
						
						y = y + (40 * MenuScale)
						
						opt\Reflections = UpdateMenuSlider5(x, y, 100 * MenuScale, opt\Reflections, 7, SliderVeryLow, SliderLow, SliderMedium, SliderHigh, SliderUltra)
						
						y = y + (30 * MenuScale)
						
						opt\AntiAliasing = UpdateMenuTick(x, y, opt\AntiAliasing)
						
						y = y + (25 * MenuScale)
						
						opt\VSync = UpdateMenuTick(x, y, opt\VSync)
						
						y = y + (25 * MenuScale)
						
						opt\VignetteEnabled = UpdateMenuTick(x, y, opt\VignetteEnabled)
						If opt\VignetteEnabled 
							If EntityHidden(t\OverlayID[OVERLAY_VIGNETTE]) Then ShowEntity(t\OverlayID[OVERLAY_VIGNETTE])
						Else
							If (Not EntityHidden(t\OverlayID[OVERLAY_VIGNETTE])) Then HideEntity(t\OverlayID[OVERLAY_VIGNETTE])
						EndIf
						
						
						y = y + (25 * MenuScale)
						
						opt\Bloom = UpdateMenuTick(x, y, opt\Bloom)
						
						y = y + (25 * MenuScale)
						
						opt\MotionBlur = UpdateMenuTick(x, y, opt\MotionBlur)
						
						y = y + (25 * MenuScale)
						
						opt\VolumetricLights = UpdateMenuTick(x, y, opt\VolumetricLights)
						
						y = y + (25 * MenuScale)
						
						opt\ParallaxOcclusion = UpdateMenuTick(x, y, opt\ParallaxOcclusion)
						If (Not opt\ParallaxOcclusion)
							ProhibitInputEffect(GetProhibitedInputEffect() Or DEFERRED_DIFFHEIGHTMAP)
						Else
							ProhibitInputEffect(GetProhibitedInputEffect() And (GetProhibitedInputEffect() Xor DEFERRED_DIFFHEIGHTMAP))
						EndIf
						
						y = y + (25 * MenuScale)
						
						opt\AmbientOcclusion = UpdateMenuTick(x, y, opt\AmbientOcclusion)
						
						y = y + (25 * MenuScale)
						
						opt\HDRRender = UpdateMenuTick(x, y, opt\HDRRender)
						SetRenderParameters(-1.0, -1.0, opt\HDRRender)
						
						ApplyGraphicOptions()
						;[End Block]
					Case MenuTab_Options_Audio
						;[Block]
						opt\PrevMasterVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\MasterVolume * 100.0, 1) / 100.0
						If (Not me\Deaf) Then opt\MasterVolume = opt\PrevMasterVolume
						
						y = y + (40 * MenuScale)
						
						opt\MusicVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\MusicVolume * 100.0, 2) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\SFXVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\SFXVolume * 100.0, 3) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\VoiceVolume = UpdateMenuSlideBar(x, y, 100 * MenuScale, opt\VoiceVolume * 100.0, 4) / 100.0
						
						y = y + (40 * MenuScale)
						
						opt\EnableSFXRelease = UpdateMenuTick(x, y, opt\EnableSFXRelease, True)
						
						y = y + (30 * MenuScale)
						
						Local PrevEnableUserTracks% = opt\UserTrackMode
						
						If UpdateMenuButton(x, y, 20 * MenuScale, 20 * MenuScale, ">")
							If opt\UserTrackMode < 2
								opt\UserTrackMode = opt\UserTrackMode + 1
							Else
								opt\UserTrackMode = 0
							EndIf
						EndIf
						
						If opt\UserTrackMode > 0
							UpdateMenuButton(x - (270 * MenuScale), y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale, GetLocalString("options", "scantracks"), Font_Default, False, True)
							y = y + (40 * MenuScale)
						EndIf
						
						y = y + (30 * MenuScale)
						
						Local PrevEnableSubtitles% = opt\EnableSubtitles
						
						opt\EnableSubtitles = UpdateMenuTick(x, y, opt\EnableSubtitles)
						If PrevEnableSubtitles <> opt\EnableSubtitles
							If opt\EnableSubtitles Then ClearSubtitles()
						EndIf
						
						If PrevEnableSubtitles Lor PrevEnableUserTracks <> 1 Then ShouldDeleteGadgets = (PrevEnableSubtitles <> opt\EnableSubtitles) Lor PrevEnableUserTracks <> opt\UserTrackMode
						;[End Block]
					Case MenuTab_Options_Controls
						;[Block]
						opt\MouseSensitivity = (UpdateMenuSlideBar(x, y, 100 * MenuScale, (opt\MouseSensitivity + 0.5) * 100.0, 1) / 100.0) - 0.5
						
						y = y + (40 * MenuScale)
						
						opt\MouseSmoothing = UpdateMenuSlideBar(x, y, 100 * MenuScale, (opt\MouseSmoothing) * 50.0, 2) / 50.0
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseX = UpdateMenuTick(x, y, opt\InvertMouseX)
						
						y = y + (40 * MenuScale)
						
						opt\InvertMouseY = UpdateMenuTick(x, y, opt\InvertMouseY)
						
						y = y + (60 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_UP, MaxKeyNames)], Font_Default, 3)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_LEFT, MaxKeyNames)], Font_Default, 4)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_DOWN, MaxKeyNames)], Font_Default, 5)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\MOVEMENT_RIGHT, MaxKeyNames)], Font_Default, 6)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SPRINT, MaxKeyNames)], Font_Default, 7)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CROUCH, MaxKeyNames)], Font_Default, 8)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\BLINK, MaxKeyNames)], Font_Default, 9)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\INVENTORY, MaxKeyNames)], Font_Default, 10)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SAVE, MaxKeyNames)], Font_Default, 11)
						
						y = y + (20 * MenuScale)
						
						UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\SCREENSHOT, MaxKeyNames)], Font_Default, 13)
						
						If opt\CanOpenConsole
							y = y + (20 * MenuScale)
							
							UpdateMenuInputBox(x, y, 110 * MenuScale, 20 * MenuScale, key\Name[Min(key\CONSOLE, MaxKeyNames)], Font_Default, 12)
						EndIf
						
						Local TempKey%
						
						For i = 0 To 227
							If KeyHit(i)
								TempKey = i
								Exit
							EndIf
						Next
						If TempKey <> 0
							Select SelectedInputBox
								Case 3
									;[Block]
									key\MOVEMENT_UP = TempKey
									;[End Block]
								Case 4
									;[Block]
									key\MOVEMENT_LEFT = TempKey
									;[End Block]
								Case 5
									;[Block]
									key\MOVEMENT_DOWN = TempKey
									;[End Block]
								Case 6
									;[Block]
									key\MOVEMENT_RIGHT = TempKey
									;[End Block]
								Case 7
									;[Block]
									key\SPRINT = TempKey
									;[End Block]
								Case 8
									;[Block]
									key\CROUCH = TempKey
									;[End Block]
								Case 9
									;[Block]
									key\BLINK = TempKey
									;[End Block]
								Case 10
									;[Block]
									key\INVENTORY = TempKey
									;[End Block]
								Case 11
									;[Block]
									key\SAVE = TempKey
									;[End Block]
								Case 12
									;[Block]
									key\CONSOLE = TempKey
									;[End Block]
								Case 13
									;[Block]
									key\SCREENSHOT = TempKey
									;[End Block]
							End Select
							SelectedInputBox = 0
						EndIf
						;[End Block]
					Case MenuTab_Options_Advanced
						;[Block]
						opt\HUDEnabled = UpdateMenuTick(x, y, opt\HUDEnabled)
						
						y = y + (30 * MenuScale)
						
						opt\FirstPersonBodyEnabled = UpdateMenuTick(x, y, opt\FirstPersonBodyEnabled)
						If opt\FirstPersonBodyEnabled
							If EntityHidden(pm\OBJ) Then ShowEntity(pm\OBJ)
						Else
							If (Not EntityHidden(pm\OBJ)) Then HideEntity(pm\OBJ)
						EndIf
						
						y = y + (30 * MenuScale)
						
						opt\DirectSight = UpdateMenuTick(x, y, opt\DirectSight)
						
						y = y + (30 * MenuScale)
						
						opt\NumericSeed = UpdateMenuTick(x, y, opt\NumericSeed)
						
						y = y + (30 * MenuScale)
						
						opt\CanOpenConsole = UpdateMenuTick(x, y, opt\CanOpenConsole)
						
						y = y + (30 * MenuScale)
						
						opt\AchvMsgEnabled = UpdateMenuTick(x, y, opt\AchvMsgEnabled)
						
						y = y + (30 * MenuScale)
						
						opt\AutoSaveEnabled = UpdateMenuTick(x, y, opt\AutoSaveEnabled, SelectedDifficulty\SaveType <> DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE)
						
						y = y + (30 * MenuScale)
						
						opt\ShowFPS = UpdateMenuTick(x, y, opt\ShowFPS)
						
						y = y + (30 * MenuScale)
						
						Local PrevCurrFrameLimit% = opt\CurrFrameLimit > 0.0
						
						If UpdateMenuTick(x, y, opt\CurrFrameLimit > 0.0)
							opt\CurrFrameLimit = UpdateMenuSlideBar(x - (120 * MenuScale), y + (40 * MenuScale), 100 * MenuScale, opt\CurrFrameLimit * 99.0, 1) / 99.0
							opt\CurrFrameLimit = Max(opt\CurrFrameLimit, 0.01)
							opt\FrameLimit = 20 + (opt\CurrFrameLimit * 280.0)
							
							y = y + (80 * MenuScale)
						Else
							opt\CurrFrameLimit = 0.0
							opt\FrameLimit = 0
							
							y = y + (30 * MenuScale)
						EndIf
						If PrevCurrFrameLimit Then ShouldDeleteGadgets = (PrevCurrFrameLimit <> opt\CurrFrameLimit)
						
						opt\SmoothBars = UpdateMenuTick(x, y, opt\SmoothBars)
						
						y = y + (30 * MenuScale)
						
						opt\PlayStartup = UpdateMenuTick(x, y, opt\PlayStartup)
						
						y = y + (30 * MenuScale)
						
						opt\LauncherEnabled = UpdateMenuTick(x, y, opt\LauncherEnabled)
						
						y = y + (40 * MenuScale)
						
						UpdateMenuButton(x - (270 * MenuScale), y, 195 * MenuScale, 30 * MenuScale, GetLocalString("options", "reset"), Font_Default, False, True)
						;[End Block]
				End Select
			EndIf
		ElseIf igm\AchievementsMenu <= 0 And igm\OptionsMenu <= 0 And igm\QuitMenu > 0
			Local QuitButton% = 85
			
			If SelectedDifficulty\SaveType = DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT Lor SelectedDifficulty\SaveType = DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
				QuitButton = 160
				If UpdateMenuButton(x, y + (85 * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "savequit"), Font_Default_Big, False, CanSave < 3)
					me\DropSpeed = 0.0
					SaveGame(CurrSave\RealName)
					NullGame()
					CurrSave = Null
					ResetInput()
					Return
				EndIf
			EndIf
			
			If UpdateMenuButton(x, y + (QuitButton * MenuScale), 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quit"), Font_Default_Big)
				NullGame()
				CurrSave = Null
				ResetInput()
				Return
			EndIf
			
			If UpdateMenuButton(x + (101 * MenuScale), y + 385 * MenuScale, 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
				igm\AchievementsMenu = 0
				igm\OptionsMenu = 0
				igm\QuitMenu = 0
				ResetInput()
				ShouldDeleteGadgets = True
			EndIf
		ElseIf igm\AchievementsMenu > 0 And igm\OptionsMenu <= 0 And igm\QuitMenu <= 0
			If UpdateMenuButton(x + (101 * MenuScale), y + 345 * MenuScale, 230 * MenuScale, 60 * MenuScale, GetLocalString("menu", "back"), Font_Default_Big)
				igm\AchievementsMenu = 0
				igm\OptionsMenu = 0
				igm\QuitMenu = 0
				ResetInput()
				ShouldDeleteGadgets = True
			EndIf
			
			If igm\AchievementsMenu > 0
				If igm\AchievementsMenu <= Floor(Float(S2IMapSize(AchievementsIndex) - 1) / 12.0)
					If UpdateMenuButton(x + (341 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, ">", Font_Default_Big)
						igm\AchievementsMenu = igm\AchievementsMenu + 1
						ShouldDeleteGadgets = True
					EndIf
				Else
					UpdateMenuButton(x + (341 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, ">", Font_Default_Big, False, True)
				EndIf
				If igm\AchievementsMenu > 1
					If UpdateMenuButton(x + (31 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, "<", Font_Default_Big)
						igm\AchievementsMenu = igm\AchievementsMenu - 1
						ShouldDeleteGadgets = True
					EndIf
				Else
					UpdateMenuButton(x + (31 * MenuScale), y + (345 * MenuScale), 60 * MenuScale, 60 * MenuScale, "<", Font_Default_Big, False, True)
				EndIf
			EndIf
		Else
			y = y + (10 * MenuScale)
			
			If (Not (me\Terminated Lor me\Zombie)) Lor me\SelectedEnding <> -1
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "resume"), Font_Default_Big, True)
					ResumeSounds()
					StopMouseMovement()
					DeleteMenuGadgets()
					MenuOpen = False
					Return
				EndIf
				
				y = y + (75 * MenuScale)
				
				If SelectedDifficulty\SaveType < DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT
					If GameSaved
						If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big)
							RenderLoading(0, GetLocalString("loading", "files"))
							
							If t\OverlayID[OVERLAY_BLOODY] <> 0 Then FreeEntity(t\OverlayID[OVERLAY_BLOODY]) : t\OverlayID[OVERLAY_BLOODY] = 0
							For i = 0 To MaxNPCSounds - 1
								If NPCSound[i] <> 0 Then FreeSound_Strict(NPCSound[i]) : NPCSound[i] = 0
							Next
							KillSounds()
							LoadGameQuick(CurrSave\Name)
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							ResetRender()
							
							For amsg.AchievementMsg = Each AchievementMsg
								Delete(amsg)
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							fps\PrevTime = MilliSecs()
							fps\LoopDelay = fps\PrevTime
							
							ResetInput()
							MenuOpen = False
							DeleteTextureEntriesFromCache(DeleteMapTextures)
							Return
						EndIf
					Else
						UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big, False, True)
					EndIf
					y = y + (75 * MenuScale)
				EndIf
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "achievements"), Font_Default_Big)
					igm\AchievementsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "options"), Font_Default_Big)
					igm\OptionsMenu = 1
					ShouldDeleteGadgets = True
				EndIf
				
				y = y + (75 * MenuScale)
				
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quit"), Font_Default_Big)
					igm\QuitMenu = 1
					ShouldDeleteGadgets = True
				EndIf
			Else
				y = y + (75 * MenuScale)
				
				If SelectedDifficulty\SaveType < DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT
					If GameSaved
						If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big)
							RenderLoading(0, GetLocalString("loading", "files"))
							
							If t\OverlayID[OVERLAY_BLOODY] <> 0 Then FreeEntity(t\OverlayID[OVERLAY_BLOODY]) : t\OverlayID[OVERLAY_BLOODY] = 0
							For i = 0 To MaxNPCSounds - 1
								If NPCSound[i] <> 0 Then FreeSound_Strict(NPCSound[i]) : NPCSound[i] = 0
							Next
							KillSounds()
							LoadGameQuick(CurrSave\Name)
							
							MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
							HidePointer()
							
							ResetRender()
							
							For amsg.AchievementMsg = Each AchievementMsg
								Delete(amsg)
							Next
							
							RenderLoading(100)
							
							me\DropSpeed = 0.0
							
							UpdateWorld(0.0)
							
							fps\Factor[0] = 0.0
							fps\PrevTime = MilliSecs()
							fps\LoopDelay = fps\PrevTime
							
							ResetInput()
							MenuOpen = False
							DeleteTextureEntriesFromCache(DeleteMapTextures)
							Return
						EndIf
					Else
						UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "load"), Font_Default_Big, False, True)
					EndIf
					y = y + (75 * MenuScale)
				EndIf
				If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "quitmenu"), Font_Default_Big)
					NullGame()
					CurrSave = Null
					ResetInput()
					Return
				EndIf
			EndIf
		EndIf
	EndIf
	
	CatchErrors("Uncaught: UpdateMenu()")
End Function

Function RenderMenu%()
	CatchErrors("RenderMenu()")
	
	If api_GetForegroundWindow() <> opt\HWND ; ~ Game is out of focus then pause the game
		MenuOpen = True
		PauseSounds()
		Delay(1000) ; ~ Reduce the CPU take while game is not in focus
	EndIf
	If MenuOpen
		Local Width% = ImageWidth(t\ImageID[0])
		Local Height% = ImageHeight(t\ImageID[0])
		Local x% = mo\Viewport_Center_X - (Width / 2)
		Local y% = mo\Viewport_Center_Y - (Height / 2)
		Local TempStr$
		Local i%
		
		ShowPointer()
		
		DrawBlock(t\ImageID[0], x, y)
		
		Color(255, 255, 255)
		
		If igm\AchievementsMenu > 0
			TempStr = GetLocalString("menu", "achievements")
		ElseIf igm\OptionsMenu > 0
			Select igm\OptionsMenu
				Case 1 ; ~ Options Tab
					TempStr = GetLocalString("menu", "options")
				Case MenuTab_Options_Graphics
					;[Block]
					TempStr = GetLocalString("options", "grap")
					;[End Block]
				Case MenuTab_Options_Audio
					;[Block]
					TempStr = GetLocalString("options", "audio")
					;[End Block]
				Case MenuTab_Options_Controls
					;[Block]
					TempStr = GetLocalString("options", "ctrl")
					;[End Block]
				Case MenuTab_Options_Advanced
					;[Block]
					TempStr = GetLocalString("options", "avc")
					;[End Block]
			End Select
		ElseIf igm\QuitMenu > 0
			TempStr = GetLocalString("menu", "quit?")
		ElseIf (Not (me\Terminated Lor me\Zombie)) Lor me\SelectedEnding <> -1
			TempStr = GetLocalString("menu", "paused")
		Else
			TempStr = GetLocalString("menu", "died")
		EndIf
		SetFontEx(fo\FontID[Font_Default_Big])
		TextEx(x + (Width / 2) + (47 * MenuScale), y + (48 * MenuScale), TempStr, True, True)
		SetFontEx(fo\FontID[Font_Default])
		
		x = x + (132 * MenuScale)
		y = y + (122 * MenuScale)
		
		If igm\AchievementsMenu <= 0 And igm\OptionsMenu > 0 And igm\QuitMenu <= 0
			If igm\OptionsMenu > 1
				Local tX# = mo\Viewport_Center_X + (Width / 2)
				Local tY# = y
				Local tW# = 400.0 * MenuScale
				Local tH# = 150.0 * MenuScale
				Local MouseOnCoord% = 20 * MenuScale
				Local Clr%
				
				Color(255, 255, 255)
				Select igm\OptionsMenu
					Case MenuTab_Options_Graphics
						;[Block]
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "gamma"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ScreenGamma, opt\ScreenGamma)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fov"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FOV)
						
						y = y + (35 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "particle"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParticleAmount, opt\ParticleAmount)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "filter"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 5 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AnisotropicFiltering)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "lightingquality"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 6 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_LightingQuality)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "reflectionsquality"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 7 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ReflectionsQuality)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "antialias"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AntiAliasing)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vsync"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VSync)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "vignette"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Vignette)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bloom"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Bloom)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "motionblur"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MotionBlur)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "volumetriclights"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VolumetricLights)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "parallaxocclusion"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ParallaxOcclusion)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "ambientocclusion"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AmbientOcclusion)
						
						y = y + (25 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "hdrrender"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HDRRender)
						
						RenderMenuButtons()
						RenderMenuTicks()
						RenderMenuSlideBars()
						RenderMenuSliders()
						;[End Block]
					Case MenuTab_Options_Audio
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mastervolume"))
						If (MouseOn(x + (250 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MasterVolume, opt\PrevMasterVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "musicvolume"))
						If (MouseOn(x + (250 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MusicVolume, opt\MusicVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "soundvolume"))
						If (MouseOn(x + (250 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 3 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SoundVolume, opt\SFXVolume)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "voicevolume"))
						If (MouseOn(x + (250 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 4 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_VoiceVolume, opt\VoiceVolume)
						
						y = y + (40 * MenuScale)
						
						Color(100, 100, 100)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "autorelease"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH + 220 * MenuScale, Tooltip_SoundAutoRelease)
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "trackmode"))
						Select opt\UserTrackMode
							Case 0
								;[Block]
								TempStr = GetLocalString("options", "track.disabled")
								;[End Block]
							Case 1
								;[Block]
								TempStr = GetLocalString("options", "track.repeat")
								;[End Block]
							Case 2
								;[Block]
								TempStr = GetLocalString("options", "track.random")
								;[End Block]
						End Select
						TextEx(x + (310 * MenuScale), y + (5 * MenuScale), TempStr)
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTracksMode)
						If opt\UserTrackMode > 0
							If MouseOn(x, y + (30 * MenuScale), 210 * MenuScale, 30 * MenuScale) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_UserTrackScan)
							y = y + (40 * MenuScale)
						EndIf
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "subtitles"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Subtitles)
						
						RenderMenuButtons()
						RenderMenuTicks()
						RenderMenuSlideBars()
						RenderMenuInputBoxes()
						;[End Block]
					Case MenuTab_Options_Controls
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesensitive"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSensitivity, opt\MouseSensitivity)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "mousesmooth"))
						If (MouseOn(x + (270 * MenuScale), y, MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 2 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseSmoothing, opt\MouseSmoothing)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "invertx"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertX)
						
						y = y + (40 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "inverty"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_MouseInvertY)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("menu", "controlconfig"))
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.forward"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.left"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.backward"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.right"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.sprint"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.crouch"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.blink"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.inv"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.save"))
						
						y = y + (20 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.screenshot"))
						
						If opt\CanOpenConsole
							y = y + (20 * MenuScale)
							
							TextEx(x, y + (5 * MenuScale), GetLocalString("options", "key.console"))
						EndIf
						
						If MouseOn(x, y - ((140 + (20 * opt\CanOpenConsole)) * MenuScale), 380 * MenuScale, ((160 + (20 * opt\CanOpenConsole)) * MenuScale)) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ControlConfiguration)
						
						RenderMenuButtons()
						RenderMenuTicks()
						RenderMenuInputBoxes()
						RenderMenuSlideBars()
						;[End Block]
					Case MenuTab_Options_Advanced
						;[Block]
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "hud"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_HUD)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fpb"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FirstPersonBody)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "ds"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_DirectSight)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "uns"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_NumericSeed)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "console"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Console)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "achipop"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AchievementPopups)
						
						y = y + (30 * MenuScale)
						
						Clr = 255 - (155 * (SelectedDifficulty\SaveType <> DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE))
						Color(Clr, Clr, Clr)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "save"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_AutoSave)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "fps"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FPS)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "frame"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) And OnSliderID = 0 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
						If opt\CurrFrameLimit > 0.0
							Color(255, 255, 0)
							TextEx(x, y + (45 * MenuScale), opt\FrameLimit + " FPS")
							If (MouseOn(x + (150 * MenuScale), y + (40 * MenuScale), MouseOnCoord * 5.7, MouseOnCoord) And OnSliderID = 0) Lor OnSliderID = 1 Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_FrameLimit, opt\FrameLimit)
							RenderMenuSliders()
							y = y + (50 * MenuScale)
						EndIf
						
						y = y + (30 * MenuScale)
						
						Color(255, 255, 255)
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "bar"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_SmoothBars)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "startvideo"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_StartupVideos)
						
						y = y + (30 * MenuScale)
						
						TextEx(x, y + (5 * MenuScale), GetLocalString("options", "launcher"))
						If MouseOn(x + (270 * MenuScale), y, MouseOnCoord, MouseOnCoord) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_Launcher)
						
						y = y + (40 * MenuScale)
						
						If MouseOn(x, y, 195 * MenuScale, 30 * MenuScale) Then RenderOptionsTooltip(tX, tY, tW, tH, Tooltip_ResetOptions)
						
						RenderMenuButtons()
						RenderMenuTicks()
						RenderMenuSlideBars()
						RenderMenuInputBoxes()
						;[End Block]
				End Select
			Else
				RenderMenuButtons()
			EndIf
		ElseIf igm\AchievementsMenu <= 0 And igm\OptionsMenu <= 0 And igm\QuitMenu > 0
			RenderMenuButtons()
		ElseIf igm\AchievementsMenu > 0 And igm\OptionsMenu <= 0 And igm\QuitMenu <= 0
			RenderMenuButtons()
			
			If igm\AchievementsMenu > 0
				Local Achievements% = JsonGetArray(JsonGetValue(AchievementsArray, "achievements"))
				Local AchvXIMG% = x + (22 * MenuScale)
				Local SeparationConst% = 101 * MenuScale
				Local ArraySize% = ((igm\AchievementsMenu - 1) * 12)
				Local AchvIndexSize% = S2IMapSize(AchievementsIndex)
				
				For i = 0 To 11
					If i + ArraySize < AchvIndexSize
						RenderAchvIMG(AchvXIMG, y + ((i / 4) * 120 * MenuScale), i, JsonGetString(JsonGetValue(JsonGetArrayValue(Achievements, i + ArraySize), "id")))
					Else
						Exit
					EndIf
				Next
				For i = 0 To 11
					If i + ArraySize < AchvIndexSize
						If MouseOn(AchvXIMG + ((i Mod 4) * SeparationConst), y + ((i / 4) * 120 * MenuScale), 85 * MenuScale, 85 * MenuScale)
							AchievementTooltip(JsonGetString(JsonGetValue(JsonGetArrayValue(Achievements, i + ArraySize), "id")))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
			EndIf
		Else
			RenderMenuButtons()
			
			SetFontEx(fo\FontID[Font_Default])
			TextEx(x, y, GetLocalString("menu", "new.diff") + SelectedDifficulty\Name)
			If CurrSave = Null
				TempStr = GetLocalString("menu", "dataredacted")
			Else
				TempStr = ConvertToUTF8(CurrSave\Name)
			EndIf
			TextEx(x, y + (20 * MenuScale), Format(GetLocalString("menu", "save"), TempStr))
			
			If SelectedCustomMap = Null
				TempStr = Format(GetLocalString("menu", "new.seed2"), RandomSeed)
			Else
				If Len(ConvertToUTF8(SelectedCustomMap\Name)) > 15
					TempStr = Format(GetLocalString("menu", "new.map"), Left(ConvertToUTF8(SelectedCustomMap\Name), 14) + "..")
				Else
					TempStr = Format(GetLocalString("menu", "new.map"), ConvertToUTF8(SelectedCustomMap\Name))
				EndIf
			EndIf
			TextEx(x, y + (40 * MenuScale), TempStr)
			
			If (me\Terminated Lor me\Zombie) And me\SelectedEnding = -1
				y = y + (175 * MenuScale)
				If SelectedDifficulty\SaveType < DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT Then y = y + (75 * MenuScale)
				SetFontEx(fo\FontID[Font_Default])
				RowText(msg\DeathMsg, x, y, 430 * MenuScale, 600 * MenuScale)
			EndIf
		EndIf
		RenderCursor()
	EndIf
	
	SetFontEx(fo\FontID[Font_Default])
	
	CatchErrors("Uncaught: RenderMenu()")
End Function

; ~ Endings ID Constants
;[Block]
Const Ending_A1% = 0
Const Ending_A2% = 1
Const Ending_B1% = 2
Const Ending_B2% = 3
;[End Block]

Function UpdateEnding%()
	fps\Factor[0] = 0.0
	If me\EndingTimer > -2000.0
		me\EndingTimer = Max(me\EndingTimer - fps\Factor[1], -1111.0)
	Else
		me\EndingTimer = me\EndingTimer - fps\Factor[1]
	EndIf
	
	GiveAchievement("055")
	If (Not UsedConsole) Lor opt\DebugMode
		GiveAchievement("console")
		If SelectedCustomMap = Null Lor opt\DebugMode
			Select SelectedDifficulty\Name
				Case difficulties[DIFFICULTY_KETER]\Name
					;[Block]
					GiveAchievement("keter")
					;[End Block]
				Case difficulties[DIFFICULTY_APOLLYON]\Name
					;[Block]
					GiveAchievement("keter")
					GiveAchievement("apollyon")
					;[End Block]
			End Select
			SaveAchievementsFile()
		EndIf
	EndIf
	
	ShouldPlay = 66
	
	If me\EndingTimer < -200.0
		StopBreathSound() : me\Stamina = 100.0
		
		If me\EndingScreen = 0
			me\EndingScreen = ResizeImageEx(LoadImage_Strict("GFX\Menu\ending_screen.png"), MenuScale, MenuScale)
			
			ShouldPlay = 22
			opt\CurrMusicVolume = opt\MusicVolume
			StopStream_Strict(MusicCHN) : MusicCHN = 0
			MusicCHN = StreamSound_Strict("SFX\Music\" + Music[22] + ".ogg", opt\CurrMusicVolume * opt\MasterVolume)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict(snd_I\LightOffSFX)
		EndIf
		
		If me\EndingTimer > -700.0
			If me\EndingTimer + fps\Factor[1] > -450.0 And me\EndingTimer <= -450.0 Then PlaySound_Strict(LoadTempSound("SFX\Ending\Ending" + me\SelectedEnding + ".ogg"), True)
		Else
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0
				If igm\AchievementsMenu =< 0
					Local Width% = ImageWidth(t\ImageID[0])
					Local Height% = ImageHeight(t\ImageID[0])
					Local x% = mo\Viewport_Center_X - (Width / 2)
					Local y% = mo\Viewport_Center_Y - (Height / 2)
					Local i%
					
					x = x + (132 * MenuScale)
					y = y + (432 * MenuScale)
					
					If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "achievements"), Font_Default_Big)
						igm\AchievementsMenu = 1
						ShouldDeleteGadgets = True
					EndIf
					
					y = y + 75 * MenuScale
					
					If UpdateMenuButton(x, y, 430 * MenuScale, 60 * MenuScale, GetLocalString("menu", "mainmenu"), Font_Default_Big)
						ShouldPlay = 23
						NowPlaying = ShouldPlay
						me\EndingTimer = -2000.0
						ShouldDeleteGadgets = True
						ResetInput()
						KillSounds()
						StopStream_Strict(MusicCHN) : MusicCHN = 0
						MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, ModeLoop)
						SetStreamVolume_Strict(MusicCHN, opt\MusicVolume * opt\MasterVolume)
						InitCredits()
					EndIf
				Else
					ShouldPlay = 22
					UpdateMenu()
				EndIf
			; ~ Credits
			ElseIf me\EndingTimer <= -2000.0
				ShouldPlay = 23
				UpdateCredits()
			EndIf
		EndIf
	EndIf
End Function

Function RenderEnding%()
	ShowPointer()
	
	Local Clr% = Max(255.0 + (me\EndingTimer) * 2.8, 0.0)
	
	Select me\SelectedEnding
		Case Ending_A1, Ending_B2
			;[Block]
			ClsColor(Clr, Clr, Clr)
			;[End Block]
		Default
			;[Block]
			ClsColor(0, 0, 0)
			;[End Block]
	End Select
	
	Cls()
	
	If me\EndingTimer < -200.0
		If me\EndingTimer > -700.0
			If Rand(150) < Min((Abs(me\EndingTimer) - 200.0), 155.0)
				DrawBlock(me\EndingScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
			Else
				Color(0, 0, 0)
				Rect(100, 100, opt\GraphicWidth - 200, opt\GraphicHeight - 200)
				Color(255, 255, 255)
			EndIf
		Else
			DrawBlock(me\EndingScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
			
			If me\EndingTimer < -1000.0 And me\EndingTimer > -2000.0
				Local Width% = ImageWidth(t\ImageID[0])
				Local Height% = ImageHeight(t\ImageID[0])
				Local x% = mo\Viewport_Center_X - (Width / 2)
				Local y% = mo\Viewport_Center_Y - (Height / 2)
				
				DrawBlock(t\ImageID[0], x, y)
				
				Color(255, 255, 255)
				SetFontEx(fo\FontID[Font_Default_Big])
				TextEx(x + (Width / 2) + (47 * MenuScale), y + (48 * MenuScale), GetLocalString("menu", "end"), True, True)
				SetFontEx(fo\FontID[Font_Default])
				
				If igm\AchievementsMenu =< 0
					Local itt.ItemTemplates, r.Rooms
					Local i%
					
					x = x + (132 * MenuScale)
					y = y + (122 * MenuScale)
					
					Local RoomsAmount% = 0, RoomsFound% = 0
					
					For r.Rooms = Each Rooms
						Local RID% = r\RoomTemplate\RoomID
						
						If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499
							RoomsAmount = RoomsAmount + 1
							RoomsFound = RoomsFound + r\Found
						EndIf
					Next
					
					If RoomsAmount = RoomsFound Then SNAVUnlocked = True
					
					Local DocsAmount% = 0, DocsFound% = 0
					
					For itt.ItemTemplates = Each ItemTemplates
						If itt\ID = it_paper
							i = (Not (itt\Name = "Leaflet" Lor itt\Name = "Drawing" Lor itt\Name = "Blank Paper" Lor (itt\Name = "Note from Maynard #1" And I_005\ChanceToSpawn <> 3.0)))
							If i
								DocsAmount = DocsAmount + 1
								DocsFound = DocsFound + itt\Found
							EndIf
						EndIf
					Next
					
					If DocsAmount = DocsFound Then EReaderUnlocked = True
					
					Local SCPsEncountered% = 1
					Local Achievements% = JsonGetArray(JsonGetValue(AchievementsArray, "achievements"))
					Local ArraySize% = JsonGetArraySize(Achievements)
					
					For i = 0 To ArraySize - 1
						Local ID$ = JsonGetString(JsonGetValue(JsonGetArrayValue(Achievements, i), "id"))
						
						If S2IMapContains(UnlockedAchievements, ID)
							If JsonGetBool(JsonGetValue(JsonGetArrayValue(Achievements, i), "scp")) Then SCPsEncountered = SCPsEncountered + 1
						EndIf
					Next
					
					Local AchievementsUnlocked% = S2IMapSize(UnlockedAchievements)
					Local EscapeSeconds% = EscapeTimer Mod 60
					Local EscapeMinutes% = Floor(EscapeTimer / 60)
					Local EscapeHours% = Floor(EscapeMinutes / 60)
					
					EscapeMinutes = EscapeMinutes - (EscapeHours * 60)
					
					TextEx(x, y, Format(GetLocalString("menu", "end.scps"), SCPsEncountered))
					TextEx(x, y + (20 * MenuScale), Format(Format(GetLocalString("menu", "end.achi"), AchievementsUnlocked, "{0}"), S2IMapSize(AchievementsIndex), "{1}"))
					TextEx(x, y + (40 * MenuScale), Format(Format(GetLocalString("menu", "end.room"), RoomsFound, "{0}"), RoomsAmount, "{1}"))
					TextEx(x, y + (60 * MenuScale), Format(Format(GetLocalString("menu", "end.doc"), DocsFound, "{0}"), DocsAmount, "{1}"))
					TextEx(x, y + (80 * MenuScale), Format(GetLocalString("menu", "end.914"), me\RefinedItems))
					TextEx(x, y + (100 * MenuScale), Format(Format(Format(GetLocalString("menu", "end.escape"), EscapeHours, "{0}"), EscapeMinutes, "{1}"), EscapeSeconds, "{2}"))
					
					RenderMenuButtons()
					RenderCursor()
				Else
					RenderMenu()
				EndIf
				; ~ Credits
			ElseIf me\EndingTimer <= -2000.0
				RenderCredits()
			EndIf
		EndIf
	EndIf
	
	SetFontEx(fo\FontID[Font_Default])
End Function

Type CreditsLine
	Field Txt$
	Field ID%
	Field Stay%
End Type

Function InitCredits%()
	Local cl.CreditsLine
	Local File% = OpenFile_Strict("Credits.txt")
	Local l$
	
	fo\FontID[Font_Credits] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Credits", "File"), GetFileLocalString(FontsFile, "Credits", "Size"))
	fo\FontID[Font_Credits_Big] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Credits_Big", "File"), GetFileLocalString(FontsFile, "Credits_Big", "Size"))
	
	If me\CreditsScreen = 0 Then me\CreditsScreen = ResizeImageEx(LoadImage_Strict("GFX\Menu\credits_screen.png"), MenuScale, MenuScale)
	
	Repeat
		l = ReadLine(File)
		cl.CreditsLine = New CreditsLine
		cl\Txt = l
	Until Eof(File)
	
	Delete First CreditsLine
	me\CreditsTimer = 0.0
End Function

Function UpdateCredits%()
	Local cl.CreditsLine, LastCreditLine.CreditsLine
	Local Credits_Y# = ((me\EndingTimer + 2000.0) / 2) + (opt\GraphicHeight + 10.0)
	Local ID% = 0
	Local EndLinesAmount% = 0
	
	LastCreditLine = Null
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "/" Then LastCreditLine = Before(cl)
		If LastCreditLine <> Null Then cl\Stay = (cl\ID > LastCreditLine\ID)
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		me\CreditsTimer = me\CreditsTimer + (0.5 * fps\Factor[1])
		If me\CreditsTimer >= 0.0
			; ~ Just save this line, ok?
			If me\CreditsTimer > 1600.0 Then me\CreditsTimer = -255.0
		Else
			If me\CreditsTimer >= -1.0 Then me\CreditsTimer = -1.0
		EndIf
	EndIf
	
	If GetKey() <> 0 Lor MouseHit(1) Then me\CreditsTimer = -1.0
	
	If me\CreditsTimer = -1.0
		Delete Each CreditsLine
		NullGame(False)
		StopStream_Strict(MusicCHN) : MusicCHN = 0
		ShouldPlay = 20
		CurrSave = Null
		ResetLoadingTextColor()
		ResetInput()
		Return
	EndIf
End Function

Function RenderCredits%()
	Local cl.CreditsLine, LastCreditLine.CreditsLine
	Local Credits_Y# = (me\EndingTimer + 2000.0) / 2 + (opt\GraphicHeight + 10.0)
	Local ID% = 0
	Local EndLinesAmount% = 0
	
	Cls()
	HidePointer()
	
	If Rand(300) > 1 Then DrawBlock(me\CreditsScreen, mo\Viewport_Center_X - (400 * MenuScale), mo\Viewport_Center_Y - (400 * MenuScale))
	
	LastCreditLine = Null
	Color(255, 255, 255)
	For cl.CreditsLine = Each CreditsLine
		cl\ID = ID
		If Left(cl\Txt, 1) = "*"
			SetFontEx(fo\FontID[Font_Credits_Big])
			If (Not cl\Stay) Then TextEx(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
		ElseIf Left(cl\Txt, 1) = "/"
			LastCreditLine = Before(cl)
		Else
			SetFontEx(fo\FontID[Font_Credits])
			If (Not cl\Stay) Then TextEx(mo\Viewport_Center_X, Credits_Y + (24 * cl\ID * MenuScale), cl\Txt, True)
		EndIf
		If LastCreditLine <> Null Then cl\Stay = (cl\ID > LastCreditLine\ID)
		If cl\Stay Then EndLinesAmount = EndLinesAmount + 1
		ID = ID + 1
	Next
	If (Credits_Y + (24 * LastCreditLine\ID * MenuScale)) < -StringHeight(LastCreditLine\Txt)
		Local Clr%
		
		If me\CreditsTimer >= 0.0 And me\CreditsTimer < 255.0
			Clr = Clamp(me\CreditsTimer, 0.0, 255.0)
			Color(Clr, Clr, Clr)
		ElseIf me\CreditsTimer >= 255.0
			Color(255, 255, 255)
		Else
			Clr = Clamp(-me\CreditsTimer, 0.0, 255.0)
			Color(Clr, Clr, Clr)
		EndIf
	EndIf
	If me\CreditsTimer <> 0.0
		For cl.CreditsLine = Each CreditsLine
			If cl\Stay
				SetFontEx(fo\FontID[Font_Credits])
				If Left(cl\Txt, 1) = "/"
					TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y + (EndLinesAmount / 2) + (24 * cl\ID * MenuScale), Right(cl\Txt, Len(cl\Txt) - 1), True)
				Else
					TextEx(mo\Viewport_Center_X, mo\Viewport_Center_Y + (24 * (cl\ID - LastCreditLine\ID) * MenuScale) - ((EndLinesAmount / 2) * 24 * MenuScale), cl\Txt, True)
				EndIf
			EndIf
		Next
	EndIf
	
	RenderLoadingText(20 * MenuScale, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"))
	
	If me\CreditsTimer = -1.0
		FreeFont(fo\FontID[Font_Credits]) : fo\FontID[Font_Credits] = 0
		FreeFont(fo\FontID[Font_Credits_Big]) : fo\FontID[Font_Credits_Big] = 0
		FreeImage(me\CreditsScreen) : me\CreditsScreen = 0
		FreeImage(me\EndingScreen) : me\EndingScreen = 0
		Return
	EndIf
End Function

Function RenderTiledImageRect%(Img%, SrcX%, SrcY%, SrcWidth%, SrcHeight%, x%, y%, Width%, Height%)
	Local TempSrcWidth%, TempSrcHeight%
	Local WhileToX% = x + Width
	Local WhileToY% = y + Height
	Local x2% = x
	
    While x2 < WhileToX
        TempSrcWidth = SrcWidth
        If x2 + SrcWidth > WhileToX Then TempSrcWidth = WhileToX - x2
        
        Local y2% = y
		
        While y2 < WhileToY
            TempSrcHeight = SrcHeight
            If y2 + SrcHeight > WhileToY Then TempSrcHeight = WhileToY - y2
            
            DrawBlockRect(Img, x2, y2, SrcX, SrcY, TempSrcWidth, TempSrcHeight)
            y2 = y2 + TempSrcHeight
        Wend
        x2 = x2 + TempSrcWidth
    Wend
End Function

Function RenderFrame%(x%, y%, Width%, Height%, xOffset% = 0, yOffset% = 0, Locked% = False)
	Local Shift% = 6 * MenuScale
	Local IMG%
	
	If Locked
		IMG = MenuGray
	Else
		IMG = MenuWhite
	EndIf
	RenderTiledImageRect(IMG, xOffset, yOffset, 512, 512, x, y, Width, Height)
	RenderTiledImageRect(MenuBlack, xOffset, yOffset, 512, 512, x + Shift / 2, y + Shift / 2, Width - Shift, Height - Shift)
End Function

Function RenderBar%(Img%, x%, y%, Width%, Height%, Value1#, Value2# = 100.0, R% = 100, G% = 100, B% = 100)
	Local i%
	
	Rect(x, y, Width + 4 * MenuScale, Height, False)
	If opt\SmoothBars
		Color(R, G, B)
		Rect(x + (3 * MenuScale), y + 3 * MenuScale, Float((Width - 2 * MenuScale) * (Value1 / Value2)), Height - 6 * MenuScale)
	Else
		Local ArrayTo% = Int(((Width - (2 * MenuScale)) * ((Value1 / Value2) / 10.0)) / MenuScale)
		
		For i = 1 To ArrayTo
			DrawBlock(Img, x + (3 + (10 * (i - 1))) * MenuScale, y + 3 * MenuScale)
		Next
	EndIf
End Function

Type MenuButton
	Field x%, y%, Width%, Height%
	Field Txt$
	Field FontID%
	Field Locked%
	Field R%, G%, B%
End Type

Function UpdateMenuButton%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, WaitForMouseUp% = False, Locked% = False, R% = 255, G% = 255, B% = 255)
	Local mb.MenuButton, CurrButton.MenuButton
	Local Clicked% = False
	Local ButtonExists% = False
	
	For mb.MenuButton = Each MenuButton
		If mb\x = x And mb\y = y And mb\Width = Width And mb\Height = Height
			ButtonExists = True
			Exit
		EndIf
	Next
	If (Not ButtonExists)
		mb.MenuButton = New MenuButton
		mb\x = x
		mb\y = y
		mb\Width = Width
		mb\Height = Height
		mb\Txt = Txt
		mb\FontID = FontID
		mb\Locked = Locked
		mb\R = R
		mb\B = B
		mb\G = G
	Else
		CurrButton = mb
		CurrButton\Txt = Txt
		CurrButton\FontID = FontID
		CurrButton\Locked = Locked
	EndIf
	
	If MouseOn(x, y, Width, Height)
		If (mo\MouseHit1 And (Not WaitForMouseUp)) Lor (mo\MouseUp1 And WaitForMouseUp)
			If Locked
				PlaySound_Strict(ButtonLockedSFX[0])
			Else
				Clicked = True
				PlaySound_Strict(ButtonSFX[0])
			EndIf
		EndIf
	EndIf
	Return(Clicked)
End Function

Function RenderMenuButtons%()
	Local Shift% = 6 * MenuScale
	Local mb.MenuButton
	
	For mb.MenuButton = Each MenuButton
		RenderFrame(mb\x, mb\y, mb\Width, mb\Height, mb\x Mod 256, mb\y Mod 256, mb\Locked)
		If MouseOn(mb\x, mb\y, mb\Width, mb\Height)
			Color(50, 50, 50, 255.0 * 0.7)
			Rect(mb\x + Shift / 2, mb\y + Shift / 2, mb\Width - Shift, mb\Height - Shift)
		Else
			Color(0, 0, 0)
		EndIf
		
		If mb\Locked
			If mb\R <> 255 Lor mb\G <> 255 Lor mb\B <> 255
				Color(mb\R, mb\G, mb\B)
			Else
				Color(100, 100, 100)
			EndIf
		Else
			Color(mb\R, mb\G, mb\B)
		EndIf
		SetFontEx(fo\FontID[mb\FontID])
		TextEx(mb\x + mb\Width / 2, mb\y + mb\Height / 2, mb\Txt, True, True)
	Next
End Function

Function DeleteMenuButton%(mb.MenuButton)
	Delete(mb)
End Function

Type MenuTick
	Field x%, y%
	Field Selected%
	Field Locked%
End Type

Function UpdateMenuTick%(x%, y%, Selected%, Locked% = False)
	Local mt.MenuTick, CurrTick.MenuTick
	Local TickExists% = False
	Local Width% = 20 * MenuScale
	Local Height% = 20 * MenuScale
	
	For mt.MenuTick = Each MenuTick
		If mt\x = x And mt\y = y
			TickExists = True
			Exit
		EndIf
	Next
	If (Not TickExists)
		mt.MenuTick = New MenuTick
		mt\x = x
		mt\y = y
		mt\Selected = Selected
		mt\Locked = Locked
	Else
		CurrTick = mt
		CurrTick\Selected = Selected
		CurrTick\Locked = Locked
	EndIf
	
	Local Highlight% = MouseOn(x, y, Width, Height)
	
	If Highlight
		If mo\MouseHit1
			If Locked
				PlaySound_Strict(ButtonLockedSFX[0])
			Else
				Selected = (Not Selected)
				PlaySound_Strict(ButtonSFX[0])
			EndIf
		EndIf
	EndIf
	Return(Selected)
End Function

Function RenderMenuTicks%()
	Local Width% = 20 * MenuScale
	Local Height% = 20 * MenuScale
	Local Shift% = 4 * MenuScale
	Local mt.MenuTick
	Local IMG%
	
	For mt.MenuTick = Each MenuTick
		If mt\Locked
			IMG = MenuGray
		Else
			IMG = MenuWhite
		EndIf
		
		RenderTiledImageRect(IMG, mt\x Mod 256, mt\y Mod 256, 512, 512, mt\x, mt\y, Width, Height)
		
		Local Highlight% = MouseOn(mt\x, mt\y, Width, Height)
		Local ColorR% = 50 * Highlight, ColorG% = 50 * Highlight, ColorB% = 50 * Highlight
		
		Color(ColorR, ColorG, ColorB)
		Rect(mt\x + Shift/ 2, mt\y + Shift/ 2, Width - Shift, Height - Shift)
		
		If mt\Selected
			If Highlight
				ColorR = ColorR * 5.1 : ColorG = ColorG * 5.1 : ColorB = ColorB * 5.1
			Else
				ColorR = ColorR + 200 : ColorG = ColorG + 200 : ColorB = ColorB + 200
			EndIf
			Color(ColorR, ColorG, ColorB)
			RenderTiledImageRect(IMG, mt\x Mod 256, mt\y Mod 256, 512, 512, mt\x + Shift, mt\y + Shift, Width - Shift * 2, Height - Shift * 2)
		EndIf
		Color(255, 255, 255)
	Next
End Function

Function DeleteMenuTick%(mt.MenuTick)
	Delete(mt)
End Function

Function ChrCanDisplay%(Char%)
	Return((Char >= 32) And (Char <= 126))
End Function

Global PrevInputBoxCtrl%, InsertMode% = False

Function UpdateInput$(aString$, MaxChr%)
	Local Value% = GetKey()
	Local Length% = Len(aString)
	
	If (CursorPos < 0) And (CursorPos <> -1) Then CursorPos = Length
	CursorPos = Max(CursorPos, 0)
	
	If KeyHit(210) Then InsertMode = (Not InsertMode) ; ~ Insert key
	If KeyHit(199) Then CursorPos = 0 ; ~ Home key
	If KeyHit(207) Then CursorPos = Length ; ~ End key
	If KeyHit(211) ; ~ Delete key
		aString = Left(aString, CursorPos) + Right(aString, Max(Length - CursorPos - 1, 0))
		CursorPos = CursorPos + 1
	EndIf
	
	If KeyDown(29) Lor KeyDown(157) ; ~ Control key
		If Value = 30 Then CursorPos = Length ; ~ Control & Right arrow
		If Value = 31 Then CursorPos = 0 ; ~ Control & Left arrow
		If Value = 3 Then SetClipboardContents(aString) ; ~ Control & C
		If Value = 22 ; ~ Control & V
			Local Clipboard$ = GetClipboardContents()
			
			If Clipboard <> ""
				aString = Left(aString, CursorPos) + Clipboard + Right(aString, Max(Length - CursorPos, 0))
				CursorPos = CursorPos + Len(Clipboard)
				If MaxChr > 0 And MaxChr < Len(aString)
					aString = Left(aString, MaxChr) 
					CursorPos = MaxChr
				EndIf
			EndIf
		EndIf
		Return(aString)
	EndIf
	
	If Value = 30
		CursorPos = Min(CursorPos + 1, Length)
		PrevInputBoxCtrl = MilliSecs()
		Return(aString)
	EndIf
	If Value = 31
		CursorPos = Max(CursorPos - 1, 0)
		PrevInputBoxCtrl = MilliSecs()
		Return(aString)
	EndIf
	
	If KeyDown(205) And ((MilliSecs() - PrevInputBoxCtrl) > 500) ; ~ Right arrow
		If (MilliSecs() Mod 100) < 25 Then CursorPos = Min(CursorPos + 1, Length)
	ElseIf KeyDown(203) And ((MilliSecs() - PrevInputBoxCtrl) > 500) ; ~ Left arrow
		If (MilliSecs() Mod 100) < 25 Then CursorPos = Max(CursorPos - 1, 0)
	Else
		If InsertMode
			If ChrCanDisplay(Value)
				aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 2)
				CursorPos = CursorPos + 1
			ElseIf Value = 8 ; ~ Backspace
				aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 1)
			ElseIf Value = 4 ; ~ Delete
				aString = Left(aString, CursorPos) + Right(aString, Max(Length - CursorPos - 1, 0))
			EndIf
		Else
			aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos + 1)
		EndIf
		CursorPos = CursorPos + Len(aString) - Length
		If MaxChr > 0 And MaxChr < Len(aString)
			aString = Left(aString, MaxChr)
			CursorPos = Min(CursorPos, MaxChr)
		EndIf
	EndIf
	Return(aString)
End Function

Type MenuInputBox
	Field x%, y%, Width%, Height%
	Field Txt$, FontID%
	Field ID%
End Type

Function UpdateMenuInputBox$(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, ID% = 0, MaxChr% = 0)
	Local mib.MenuInputBox, CurrInputBox.MenuInputBox
	Local InputBoxExists% = False
	
	For mib.MenuInputBox = Each MenuInputBox
		If mib\x = x And mib\y = y And mib\Width = Width And mib\Height = Height
			InputBoxExists = True
			Exit
		EndIf
	Next
	If (Not InputBoxExists)
		mib.MenuInputBox = New MenuInputBox
		mib\x = x
		mib\y = y
		mib\Width = Width
		mib\Height = Height
		mib\Txt = Txt
		mib\FontID = FontID
		mib\ID = ID
	Else
		CurrInputBox = mib
		CurrInputBox\Txt = Txt
		CurrInputBox\FontID = FontID
	EndIf
	
	Local MouseOnBox% = False
	
	If MouseOn(x, y, Width, Height)
		MouseOnBox = True
		If mo\MouseHit1
			SelectedInputBox = ID
			FlushKeys()
			CursorPos = -2
		EndIf
	EndIf
	
	If (Not MouseOnBox) And mo\MouseHit1 And SelectedInputBox = ID
		SelectedInputBox = 0
		CursorPos = -2
	EndIf
	
	If SelectedInputBox = ID Then Txt = UpdateInput(Txt, MaxChr)
	Return(Txt)
End Function

Function RenderMenuInputBoxes%()
	Local mib.MenuInputBox
	Local Shift% = 6 * MenuScale
	Local CurrCursorPos% = Max(CursorPos, 0)
	
	For mib.MenuInputBox = Each MenuInputBox
		RenderFrame(mib\x, mib\y, mib\Width, mib\Height, mib\x Mod 256, mib\y Mod 256)
		
		If MouseOn(mib\x, mib\y, mib\Width, mib\Height)
			Color(50, 50, 50, 255.0 * 0.7)
			Rect(mib\x + Shift / 2, mib\y + Shift / 2, mib\Width - Shift, mib\Height - Shift)
		EndIf
		
		Color(255, 255, 255)
		If SelectedInputBox = mib\ID
			If ((MilliSec Mod 800) < 400) Lor KeyDown(205) Lor KeyDown(203) Lor InsertMode Then Rect(mib\x + mib\Width / 2 - (StringWidth(mib\Txt) / 2) + StringWidth(Left(mib\Txt, CurrCursorPos)), mib\y + (mib\Height / 2) - (5 * MenuScale), 2 * MenuScale, 12 * MenuScale)
		EndIf
		
		SetFontEx(fo\FontID[mib\FontID])
		TextEx(mib\x + mib\Width / 2, mib\y + mib\Height / 2, mib\Txt, True, True)
	Next
End Function

Function DeleteMenuInputBox%(mib.MenuInputBox)
	Delete(mib)
End Function

Type MenuSlideBar
	Field x%, y%, Width%
	Field Value#
End Type

Function UpdateMenuSlideBar#(x%, y%, Width%, Value#, ID%)
	Local msb.MenuSlideBar, CurrSlideBar.MenuSlideBar
	Local SlideBarExists% = False
	
	For msb.MenuSlideBar = Each MenuSlideBar
		If msb\x = x And msb\y = y And msb\Width = Width
			SlideBarExists = True
			Exit
		EndIf
	Next
	If (Not SlideBarExists)
		msb.MenuSlideBar = New MenuSlideBar
		msb\x = x
		msb\y = y
		msb\Width = Width
		msb\Value = Value
	Else
		CurrSlideBar = msb
		CurrSlideBar\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y, Width + 14 * MenuScale, 20 * MenuScale) Then OnSliderID = ID
	EndIf
	If ID = OnSliderID Then Value = Clamp((MousePosX - x) * 100.0 / Width, 0.0, 100.0)
	Return(Floor(Value))
End Function

Function RenderMenuSlideBars%()
	Local msb.MenuSlideBar
	
	For msb.MenuSlideBar = Each MenuSlideBar
		Local ColorR% = 255, ColorG% = 255, ColorB% = 255
		
		If MouseOn(msb\x, msb\y, msb\Width + 14 * MenuScale, 20 * MenuScale) Then ColorR = 0 : ColorG = 200 : ColorB = 0
		Color(ColorR, ColorG, ColorB)
		Rect(msb\x, msb\y, msb\Width + 14 * MenuScale, 20 * MenuScale, False)
		
		DrawBlock(BlinkMeterIMG, msb\x + msb\Width * msb\Value / 100.0 + 3 * MenuScale, msb\y + 3 * MenuScale)
		
		Color(170, 170, 170)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(msb\x - 50 * MenuScale, msb\y + 5 * MenuScale, GetLocalString("options", "slider.low"))
		TextEx(msb\x + msb\Width + 34 * MenuScale, msb\y + 5 * MenuScale, GetLocalString("options", "slider.high"))
	Next
End Function

Function DeleteMenuSlideBar%(msb.MenuSlideBar)
	Delete(msb)
End Function

Type MenuSlider
	Field x%, y%, Width%
	Field Value%
	Field ID%
	Field Val1$, Val2$, Val3$, Val4$, Val5$
	Field Amount%
End Type

Global OnSliderID%

Function UpdateMenuSlider3%(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$)
	Local ms.MenuSlider, CurrSlider.MenuSlider
	Local Slider3Exists% = False
	Local WidthHalf% = Width / 2
	Local xPosShift% = 8 * MenuScale
	
	For ms.MenuSlider = Each MenuSlider
		If ms\x = x And ms\y = y And ms\Width = Width And ms\Amount = 3
			Slider3Exists = True
			Exit
		EndIf
	Next
	If (Not Slider3Exists)
		ms.MenuSlider = New MenuSlider
		ms\x = x
		ms\y = y
		ms\Width = Width
		ms\ID = ID
		ms\Value = Value
		ms\Val1 = Val1
		ms\Val2 = Val2
		ms\Val3 = Val3
		ms\Amount = 3
	Else
		CurrSlider = ms
		CurrSlider\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y, Width + 14 * MenuScale, 20 * MenuScale) Then OnSliderID = ID
	EndIf
	
	If ID = OnSliderID
		If MousePosX <= x + xPosShift
			Value = 0
		ElseIf (MousePosX >= x + WidthHalf) And (MousePosX <= x + WidthHalf + xPosShift)
			Value = 1
		ElseIf MousePosX >= x + Width
			Value = 2
		EndIf
	EndIf
	Return(Value)
End Function

Function UpdateMenuSlider5%(x%, y%, Width%, Value%, ID%, Val1$, Val2$, Val3$, Val4$, Val5$)
	Local ms.MenuSlider, CurrSlider.MenuSlider
	Local Slider5Exists% = False
	Local WidthFourth% = Width / 4
	Local WidthHalf% = Width / 2
	Local Width075% = Width * 0.75
	Local xPosShift% = 8 * MenuScale
	
	For ms.MenuSlider = Each MenuSlider
		If ms\x = x And ms\y = y And ms\Width = Width And ms\Amount = 5
			Slider5Exists = True
			Exit
		EndIf
	Next
	If (Not Slider5Exists)
		ms.MenuSlider = New MenuSlider
		ms\x = x
		ms\y = y
		ms\Width = Width
		ms\ID = ID
		ms\Value = Value
		ms\Val1 = Val1
		ms\Val2 = Val2
		ms\Val3 = Val3
		ms\Val4 = Val4
		ms\Val5 = Val5
		ms\Amount = 5
	Else
		CurrSlider = ms
		CurrSlider\Value = Value
	EndIf
	
	If mo\MouseDown1 And OnSliderID = 0
		If MouseOn(x, y, Width + 14 * MenuScale, 20 * MenuScale) Then OnSliderID = ID
	EndIf
	
	If ID = OnSliderID
		If MousePosX <= x + xPosShift
			Value = 0
		ElseIf (MousePosX >= x + WidthFourth) And (MousePosX <= x + WidthFourth + xPosShift)
			Value = 1
		ElseIf (MousePosX >= x + WidthHalf) And (MousePosX <= x + WidthHalf + xPosShift)
			Value = 2
		ElseIf (MousePosX >= x + Width075) And (MousePosX <= x + Width075 + xPosShift)
			Value = 3
		ElseIf MousePosX >= x + Width
			Value = 4
		EndIf
	EndIf
	Return(Value)
End Function

Function RenderMenuSliders%()
	Local ms.MenuSlider
	Local PrevHeight% = ImageHeight(BlinkMeterIMG)
	
	ResizeImage(BlinkMeterIMG, ImageWidth(BlinkMeterIMG), PrevHeight * 1.4)
	
	For ms.MenuSlider = Each MenuSlider
		Local x1% = ms\x, y1% = ms\y - 8 * MenuScale, y2 = ms\y
		Local w1% = ms\Width + 14 * MenuScale, w2% = 2 * MenuScale
		Local h1% = ImageHeight(BlinkMeterIMG), h2% = ImageHeight(BlinkMeterIMG)
		Local ColorR% = 200, ColorG% = 200, ColorB% = 200
		
		If ms\ID = OnSliderID Lor MouseOn(x1, y1, w1, (h1 + h2) / 2.0)
			Color(ColorR - 200, ColorG, ColorB - 200, 255.0 * 0.7)
		Else
			Color(ColorR, ColorG, ColorB)
		EndIf
		Rect(x1, y1, w1, h1, False)
		Rect(x1, y1, w2, h1)
		
		SetFontEx(fo\FontID[Font_Default])
		Select ms\Amount
			Case 3
				;[Block]
				Rect(x1 + ms\Width / 2 + 5 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width + 11 * MenuScale, y1, w2, h1)
				
				Color(170, 170, 170)
				Select ms\Value
					Case 0
						;[Block]
						DrawBlock(BlinkMeterIMG, x1, y1)
						TextEx(x1 + 2 * MenuScale, y2 + 12 * MenuScale, ms\Val1, True)
						;[End Block]
					Case 1
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width / 2 + 3 * MenuScale, y1)
						TextEx(x1 + ms\Width / 2 + 7 * MenuScale, y2 + 12 * MenuScale, ms\Val2, True)
						;[End Block]
					Case 2
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width + 6 * MenuScale, y1)
						TextEx(x1 + ms\Width + 12 * MenuScale, y2 + 12 * MenuScale, ms\Val3, True)
						;[End Block]
				End Select
				;[End Block]
			Case 4
				;[Block]
				Rect(x1 + ms\Width / 3 + 3.33 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width * 0.66 + 6.67 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width + 11 * MenuScale, y1, w2, h1)
				
				Color(170, 170, 170)
				Select ms\Value
					Case 0
						;[Block]
						DrawBlock(BlinkMeterIMG, x1, y1)
						TextEx(x1 + 2 * MenuScale, y2 + 12 * MenuScale, ms\Val1, True)
						;[End Block]
					Case 1
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width / 3 + 2 * MenuScale, y1)
						TextEx(x1 + ms\Width / 3 + 5.33 * MenuScale, y2 + 12 * MenuScale, ms\Val2, True)
						;[End Block]
					Case 2
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width * 0.66 + 4.67 * MenuScale, y1)
						TextEx(x1 + ms\Width * 0.66 + 9.67 * MenuScale, y2 + 12 * MenuScale, ms\Val3, True)
						;[End Block]
					Case 3
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width + 6 * MenuScale, y1)
						TextEx(x1 + ms\Width + (12 * MenuScale), y2 + (12 * MenuScale), ms\Val4, True)
						;[End Block]
				End Select
				;[End Block]
			Case 5
				;[Block]
				Rect(x1 + ms\Width / 4 + 2.5 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width / 2 + 5 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width * 0.75 + 7.5 * MenuScale, y1, w2, h1)
				Rect(x1 + ms\Width + 11 * MenuScale, y1, w2, h1)
				
				Color(170, 170, 170)
				Select ms\Value
					Case 0
						;[Block]
						DrawBlock(BlinkMeterIMG, x1, y1)
						TextEx(x1 + 2 * MenuScale, y2 + 12 * MenuScale, ms\Val1, True)
						;[End Block]
					Case 1
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width / 4 + 1.5 * MenuScale, y1)
						TextEx(x1 + ms\Width / 4 + 4.5 * MenuScale, y2 + 12 * MenuScale, ms\Val2, True)
						;[End Block]
					Case 2
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + (ms\Width / 2) + 3 * MenuScale, y1)
						TextEx(x1 + ms\Width / 2 + 7 * MenuScale, y2 + 12 * MenuScale, ms\Val3, True)
						;[End Block]
					Case 3
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width * 0.75 + 4.5 * MenuScale, y1)
						TextEx(x1 + ms\Width * 0.75 + 9.5 * MenuScale, y2 + 12 * MenuScale, ms\Val4, True)
						;[End Block]
					Case 4
						;[Block]
						DrawBlock(BlinkMeterIMG, x1 + ms\Width + 6 * MenuScale, y1)
						TextEx(x1 + ms\Width + 12 * MenuScale, y2 + 12 * MenuScale, ms\Val5, True)
						;[End Block]
				End Select
				;[End Block]
		End Select
	Next
	
	ResizeImage(BlinkMeterIMG, ImageWidth(BlinkMeterIMG), PrevHeight)
End Function

Function DeleteMenuSlider%(ms.MenuSlider)
	Delete(ms)
End Function

;Type MenuScrollBar
;	Field x%, y%
;	Field Width%, Height%
;	Field BarX%, BarY%
;	Field BarWidth%, BarHeight%
;	Field Value#
;	Field Vertical%, Locked%
;End Type

Global OnScrollBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

;Function UpdateMenuScrollBar#(Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Value#, Vertical% = False, Locked% = False)
;	Local msb.MenuScrollBar, CurrScrollBar.MenuScrollBar
;	Local ScrollBarExist% = False
;	
;	For msb.MenuScrollBar = Each MenuScrollBar
;		If msb\BarX = BarX And msb\BarY = BarY And msb\BarWidth = BarWidth And msb\BarHeight = BarHeight
;			ScrollBarExist = True
;			Exit
;		EndIf
;	Next
;	If (Not ScrollBarExist)
;		msb.MenuScrollBar = New MenuScrollBar
;		msb\Width = Width
;		msb\Height = Height
;		msb\BarX = BarX
;		msb\BarY = BarY
;		msb\BarWidth = BarWidth
;		msb\BarHeight = BarHeight
;		msb\Value = Value
;		msb\Vertical = Vertical
;		msb\Locked = Locked
;	Else
;		currScrollBar = msb
;		currScrollBar\Width = Width
;		currScrollBar\Height = Height
;		currScrollBar\Value = Value
;		currScrollBar\Vertical = Vertical
;		currScrollBar\Locked = Locked
;	EndIf
;	
;	Local MouseSpeedX# = MouseXSpeed()
;	Local MouseSpeedY# = MouseYSpeed()
;	
;	OnScrollBar = (mo\MouseDown1 And MouseOn(BarX, BarY, BarWidth, BarHeight))
;	If OnScrollBar
;		If mo\MouseHit1
;			If Locked
;				PlaySound_Strict(ButtonLockedSFX[0])
;			Else
;				PlaySound_Strict(ButtonSFX[0])
;			EndIf
;		EndIf
;		If (Not Vertical)
;			Return(Clamp(Value + MouseSpeedX / Float(Width - BarWidth), 0.0, 1.0))
;		Else
;			Return(Clamp(Value + MouseSpeedY / Float(Height - BarHeight), 0.0, 1.0))
;		EndIf
;	EndIf
;	
;	Local MouseSpeedZ# = MouseZSpeed()
;	
;	; ~ Only for vertical scroll bars
;	If MouseSpeedZ <> 0.0 Then Return(Clamp(Value - (MouseSpeedZ * 3.0) / Float(Height - BarHeight), 0.0, 1.0))
;	
;	Return(Value)
;End Function

;Function RenderMenuScrollBars%()
;	Local msb.MenuScrollBar
;	
;	For msb.MenuScrollBar = Each MenuScrollBar
;		If OnScrollBar
;			Color(30, 30, 30)
;			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight)
;			Color(130, 130, 130)
;			Rect(msb\BarX + 1, msb\BarY + 1, msb\BarWidth - 1, msb\BarHeight - 1, False)
;			Color(10, 10, 10)
;			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight, False)
;			Color(255, 255, 255)
;			Line(msb\BarX, msb\BarY + msb\BarHeight - 1, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
;			Line(msb\BarX + msb\BarWidth - 1, msb\BarY, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
;		Else
;			Color(100, 100, 100)
;			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight)
;			Color(130, 130, 130)
;			Rect(msb\BarX, msb\BarY, msb\BarWidth - 1, msb\BarHeight - 1, False)
;			Color(255, 255, 255)
;			Rect(msb\BarX, msb\BarY, msb\BarWidth, msb\BarHeight, False)
;			Color(10, 10, 10)
;			Line(msb\BarX, msb\BarY + msb\BarHeight - 1, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
;			Line(msb\BarX + msb\BarWidth - 1, msb\BarY, msb\BarX + msb\BarWidth - 1, msb\BarY + msb\BarHeight - 1)
;		EndIf
;		
;		If (Not msb\Vertical) ; ~ Horizontal
;			If msb\Height > (10 * MenuScale)
;				Color(255, 255, 255)
;				Rect(msb\BarX + (msb\BarWidth / 2), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
;				Rect(msb\BarX + (msb\BarWidth / 2) - (3 * MenuScale), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
;				Rect(msb\BarX + (msb\BarWidth / 2) + (3 * MenuScale), msb\BarY + (5 * MenuScale), 2 * MenuScale, msb\BarHeight - (10 * MenuScale))
;			EndIf
;		Else ; ~ Vertical
;			If msb\Width > (10 * MenuScale)
;				Color(255, 255, 255)
;				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
;				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2) - (3 * MenuScale), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
;				Rect(msb\BarX + (4 * MenuScale), msb\BarY + (msb\BarHeight / 2) + (3 * MenuScale), msb\BarWidth - (10 * MenuScale), 2 * MenuScale)
;			EndIf
;		EndIf
;	Next
;End Function

;Function DeleteMenuScrollBar%(msb.MenuScrollBar)
;	Delete(msb)
;End Function

Function DeleteMenuGadgets%()
	Local mb.MenuButton, mt.MenuTick, mib.MenuInputBox, msb.MenuSlideBar, ms.MenuSlider;, msb.MenuScrollBar
	
	For mb.MenuButton = Each MenuButton
		DeleteMenuButton(mb)
	Next
	For mt.MenuTick = Each MenuTick
		DeleteMenuTick(mt)
	Next
	For mib.MenuInputBox = Each MenuInputBox
		DeleteMenuInputBox(mib)
	Next
	For msb.MenuSlideBar = Each MenuSlideBar
		DeleteMenuSlideBar(msb)
	Next
	For ms.MenuSlider = Each MenuSlider
		DeleteMenuSlider(ms)
	Next
End Function

Function RowText%(Txt$, x%, y%, W%, H%, Align% = False, Leading# = 1.0)
	; ~ Display A$ starting at x, y - no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = SMALLEST_POWER_TWO
	
	Local LinesShown% = 0
	Local Height%, s$
	
	If Leading >= 0.0
		Height = StringHeight(Txt) + Leading
	Else
		Height = StringHeight(Txt) * Abs(Leading)
	EndIf
	
	While Len(Txt) > 0
		Local Space% = Instr(Txt, SplitSpace)
		
		If Space = 0 Then Space = Min(W * Len(Txt) / Max(StringWidth(Txt), 1), Len(Txt))
		
		Local Temp$ = Left(Txt, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space 
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If StringWidth(s + Temp) > W And StringWidth(s + Trimmed) <= W
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(s + Temp) > W ; ~ Too big, so print what will fit
			If Align
				TextEx(x + W / 2 - StringWidth(s) / 2, y + LinesShown * Height, s)
			Else
				TextEx(x, y + LinesShown * Height, s)
			EndIf
			
			LinesShown = LinesShown + 1
			s = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			s = s + Temp
			Txt = Right(Txt, Len(Txt) - (Len(Temp) + Extra))
		EndIf
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	
	If s <> "" And (LinesShown + 1) <= H
		If Align
			TextEx(x + W / 2 - StringWidth(s) / 2, y + LinesShown * Height, s) ; ~ Print any remaining text if it'll fit vertically
		Else
			TextEx(x, y + LinesShown * Height, s) ; ~ Print any remaining text if it'll fit vertically
		EndIf
	EndIf
End Function

Function GetLineAmount%(Txt$, W%, H%, Leading# = 1.0)
	; ~ Display A$ no wider than W and no taller than H (all in pixels)
	; ~ Leading is optional extra vertical spacing in pixels
	
	If H < 1 Then H = SMALLEST_POWER_TWO
	
	Local LinesShown% = 0
	Local Height% = StringHeight(Txt) + Leading
	Local s$
	
	While Len(Txt) > 0
		Local Space% = Instr(Txt, SplitSpace)
		
		If Space = 0 Then Space = Min(W * Len(Txt) / Max(StringWidth(Txt), 1), Len(Txt))
		
		Local Temp$ = Left(Txt, Space)
		Local Trimmed$ = Trim(Temp) ; ~ We might ignore a final space
		Local Extra% = 0 ; ~ We haven't ignored it yet
		
		; ~ Ignore final space if doing so would make a word fit at end of line:
		If StringWidth(s + Temp) > W And StringWidth(s + Trimmed) <= W
			Temp = Trimmed
			Extra = 1
		EndIf
		
		If StringWidth(s + Temp) > W ; ~ Too big, so print what will fit
			LinesShown = LinesShown + 1
			s = ""
		Else ; ~ Append it to b$ (which will eventually be printed) and remove it from A$
			s = s + Temp
			Txt = Right(Txt, Len(Txt) - (Len(Temp) + Extra))
		EndIf
		If ((LinesShown + 1) * Height) > H Then Exit ; ~ The next line would be too tall, so leave
	Wend
	Return(LinesShown + 1)
End Function

; ~ Graphics Tooltips Constants
;[Block]
Const Tooltip_ScreenGamma% = 0
Const Tooltip_FOV% = 1
Const Tooltip_ParticleAmount% = 2
Const Tooltip_TextureQuality% = 3
Const Tooltip_AnisotropicFiltering% = 4
Const Tooltip_LightingQuality% = 5
Const Tooltip_ShadowQuality% = 6
Const Tooltip_ReflectionsQuality% = 7
Const Tooltip_AntiAliasing% = 8
Const Tooltip_VSync% = 9
Const Tooltip_Vignette% = 10
Const Tooltip_Bloom% = 11
Const Tooltip_MotionBlur% = 12
Const Tooltip_VolumetricLights% = 13
Const Tooltip_ParallaxOcclusion% = 14
Const Tooltip_AmbientOcclusion% = 15
Const Tooltip_HDRRender% = 16
;[End Block]

; ~ Audio Tooltips Constants
;[Block]
Const Tooltip_MasterVolume% = 17
Const Tooltip_MusicVolume% = 18
Const Tooltip_SoundVolume% = 19
Const Tooltip_VoiceVolume% = 20
Const Tooltip_SoundAutoRelease% = 21
Const Tooltip_UserTracksMode% = 22
Const Tooltip_UserTrackScan% = 23
Const Tooltip_Subtitles% = 24
;[End Block]

; ~ Controls Tooltips Constants
;[Block]
Const Tooltip_MouseSensitivity% = 25
Const Tooltip_MouseSmoothing% = 26
Const Tooltip_MouseInvertX% = 27
Const Tooltip_MouseInvertY% = 28
Const Tooltip_ControlConfiguration% = 29
;[End Block]

; ~ Advanced Tooltips Constants
;[Block]
Const Tooltip_HUD% = 30
Const Tooltip_FirstPersonBody% = 31
Const Tooltip_DirectSight% = 32
Const Tooltip_NumericSeed% = 33
Const Tooltip_Console% = 34
Const Tooltip_AchievementPopups% = 35
Const Tooltip_FPS% = 36
Const Tooltip_FrameLimit% = 37
Const Tooltip_AutoSave% = 38
Const Tooltip_SmoothBars% = 39
Const Tooltip_StartupVideos% = 40
Const Tooltip_Launcher% = 41
Const Tooltip_ResetOptions% = 42
;[End Block]

Function RenderOptionsTooltip%(x%, y%, Width%, Height%, Option%, Value# = 0.0)
	Local fX# = x + 6.0 * MenuScale
	Local fY# = y + 6.0 * MenuScale
	Local fW# = Width - 12.0 * MenuScale
	Local fH# = Height - 12.0 * MenuScale
	Local Lines% = 0, Lines2% = 0
	Local Txt$ = "", Txt2$ = ""
	Local R% = 0, G% = 0, B% = 0
	
	SetFontEx(fo\FontID[Font_Default])
	Color(255, 255, 255)
	Select Lower(Option)
			; ~ [GRAPHICS]
		Case Tooltip_ScreenGamma
			;[Block]
			Txt = GetLocalString("tooltip", "gamma")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "100%", "{1}")
			;[End Block]
		Case Tooltip_FOV
			;[Block]
			Txt = GetLocalString("tooltip", "fov")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(opt\FOV)) + "°", "{0}"), "60°", "{1}")
			;[End Block]
		Case Tooltip_ParticleAmount
			;[Block]
			Txt = GetLocalString("tooltip", "particle_1")
			Select Value
				Case 0.0
					;[Block]
					R = 255
					Txt2 = GetLocalString("tooltip", "particle_2.1")
					;[End Block]
				Case 1.0
					;[Block]
					R = 255
					G = 255
					Txt2 = GetLocalString("tooltip", "particle_2.2")
					;[End Block]
				Case 2.0
					;[Block]
					G = 255
					Txt2 = GetLocalString("tooltip", "particle_2.3")
					;[End Block]
			End Select
			;[End Block]
		Case Tooltip_TextureQuality
			;[Block]
			Txt = GetLocalString("tooltip", "texquality")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
		Case Tooltip_AnisotropicFiltering
			;[Block]
			Txt = GetLocalString("tooltip", "anisotropic")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
		Case Tooltip_LightingQuality
			;[Block]
			Txt = GetLocalString("tooltip", "lightingquality")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high")
			;[End Block]
		Case Tooltip_ShadowQuality
			;[Block]
			Txt = GetLocalString("tooltip", "shadowquality")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high")
			;[End Block]
		Case Tooltip_ReflectionsQuality
			;[Block]
			Txt = GetLocalString("tooltip", "reflectionsquality")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high") + GetLocalString("tooltip", "perf.effect.vid.memory")
			;[End Block]
		Case Tooltip_AntiAliasing
			;[Block]
			Txt = GetLocalString("tooltip", "alias")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
		Case Tooltip_VSync
			;[Block]
			Txt = GetLocalString("tooltip", "vsync")
			;[End Block]
		Case Tooltip_Vignette
			;[Block]
			Txt = GetLocalString("tooltip", "vignette")
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.low")
			;[End Block]
		Case Tooltip_Bloom
			;[Block]
			Txt = GetLocalString("tooltip", "bloom")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
		Case Tooltip_MotionBlur
			;[Block]
			Txt = GetLocalString("tooltip", "motionblur")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
		Case Tooltip_VolumetricLights
			;[Block]
			Txt = GetLocalString("tooltip", "volumetriclights")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high")
			;[End Block]
		Case Tooltip_ParallaxOcclusion
			;[Block]
			Txt = GetLocalString("tooltip", "parallaxocclusion")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high")
			;[End Block]
		Case Tooltip_AmbientOcclusion
			;[Block]
			Txt = GetLocalString("tooltip", "ambientocclusion")
			R = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.high")
			;[End Block]
		Case Tooltip_HDRRender
			;[Block]
			Txt = GetLocalString("tooltip", "hdrrender")
			R = 255
			G = 255
			Txt2 = GetLocalString("tooltip", "perf.effect.mid")
			;[End Block]
			; ~ [AUDIO]
		Case Tooltip_MasterVolume
			;[Block]
			Txt = GetLocalString("tooltip", "mastervolume")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "50%", "{1}")
			;[End Block]
		Case Tooltip_MusicVolume
			;[Block]
			Txt = GetLocalString("tooltip", "musicvolume")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "50%", "{1}")
			;[End Block]
		Case Tooltip_SoundVolume
			;[Block]
			Txt = GetLocalString("tooltip", "soundvolume")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "50%", "{1}")
			;[End Block]
		Case Tooltip_VoiceVolume
			;[Block]
			Txt = GetLocalString("tooltip", "voicevolume")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "50%", "{1}")
			;[End Block]
		Case Tooltip_SoundAutoRelease
			;[Block]
			Txt = GetLocalString("tooltip", "autorelease")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchange")
			;[End Block]
		Case Tooltip_UserTracksMode
			;[Block]
			Txt = GetLocalString("tooltip", "trackmode")
			R = 255 : G = 255
			Txt2 = GetLocalString("tooltip", "modenote")
			;[End Block]
		Case Tooltip_UserTrackScan
			;[Block]
			Txt = GetLocalString("tooltip", "scantrack")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchangebtn")
			;[End Block]
		Case Tooltip_Subtitles
			;[Block]
			Txt = GetLocalString("tooltip", "subtitles")
			;[End Block]
			; ~ [CONTROLS]
		Case Tooltip_MouseSensitivity
			;[Block]
			Txt = GetLocalString("tooltip", "mousespeed")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "0%", "{1}")
			;[End Block]
		Case Tooltip_MouseInvertX
			;[Block]
			Txt = GetLocalString("tooltip", "invertx")
			;[End Block]
		Case Tooltip_MouseInvertY
			;[Block]
			Txt = GetLocalString("tooltip", "inverty")
			;[End Block]
		Case Tooltip_MouseSmoothing
			;[Block]
			Txt = GetLocalString("tooltip", "mousesmooth")
			R = 255 : G = 255
			Txt2 = Format(Format(GetLocalString("tooltip", "default.value"), Str(Int(Value * 100.0)) + "%", "{0}"), "100%", "{1}")
			;[End Block]
		Case Tooltip_ControlConfiguration
			;[Block]
			Txt = GetLocalString("tooltip", "configcontrol")
			;[End Block]
			; ~ [ADVANCED]
		Case Tooltip_HUD
			;[Block]
			Txt = GetLocalString("tooltip", "hud")
			;[End Block]
		Case Tooltip_FirstPersonBody
			;[Block]
			Txt = GetLocalString("tooltip", "fpb")
			;[End Block]
		Case Tooltip_DirectSight
			;[Block]
			Txt = GetLocalString("tooltip", "ds")
			;[End Block]
		Case Tooltip_NumericSeed
			;[Block]
			Txt = GetLocalString("tooltip", "uns")
			;[End Block]
		Case Tooltip_Console
			;[Block]
			Txt = Format(GetLocalString("tooltip", "console"), key\Name[key\CONSOLE])
			;[End Block]
		Case Tooltip_AchievementPopups
			;[Block]
			Txt = GetLocalString("tooltip", "achipop")
			;[End Block]
		Case Tooltip_AutoSave
			;[Block]
			Txt = Format(GetLocalString("tooltip", "autosave"), key\Name[key\SAVE])
			R = 255 : G = 255
			Txt2 = GetLocalString("tooltip", "autosave.note")
			;[End Block]
		Case Tooltip_FPS
			;[Block]
			Txt = GetLocalString("tooltip", "fps")
			;[End Block]
		Case Tooltip_FrameLimit
			;[Block]
			Txt = GetLocalString("tooltip", "frame")
			If Value > 0 And Value < 60
				R = 255 : G = 255
				Txt2 = GetLocalString("tooltip", "frame.note")
			EndIf
			;[End Block]
		Case Tooltip_SmoothBars
			;[Block]
			Txt = GetLocalString("tooltip", "bar")
			;[End Block]
		Case Tooltip_StartupVideos
			;[Block]
			Txt = GetLocalString("tooltip", "startvideo")
			;[End Block]
		Case Tooltip_Launcher
			;[Block]
			Txt = GetLocalString("tooltip", "launcher")
			;[End Block]
		Case Tooltip_ResetOptions
			;[Block]
			Txt = GetLocalString("tooltip", "reset")
			R = 255
			Txt2 = GetLocalString("tooltip", "cantchangebtn")
			;[End Block]
	End Select
	
	Lines = GetLineAmount(Txt, fW, fH)
	
	Local StringHeightTxt% = StringHeight(Txt) * Lines
	Local CoordEx% = (10 + Lines) * MenuScale
	
	If Txt2 = ""
		RenderFrame(x, y, Width, StringHeightTxt + CoordEx)
	Else
		Lines2 = GetLineAmount(Txt2, fW, fH)
		RenderFrame(x, y, Width, (StringHeightTxt + CoordEx) + (StringHeight(Txt2) * Lines2) + (10 + Lines2) * MenuScale)
	EndIf
	RowText(Txt, fX, fY, fW, fH)
	If Txt2 <> ""
		Color(R, G, B)
		RowText(Txt2, fX, fY + StringHeightTxt + (5 + Lines) * MenuScale, fW, fH)
		Color(255, 255, 255)
	EndIf
End Function

Function RenderMapCreatorTooltip%(x%, y%, Width%, Height%, MapName$)
	Local fX# = x + (6.0 * MenuScale)
	Local fY# = y + (6.0 * MenuScale)
	Local fW# = Width - (12.0 * MenuScale)
	Local fH# = Height - (12.0 * MenuScale)
	Local Lines% = 0
	
	SetFontEx(fo\FontID[Font_Default])
	Color(255, 255, 255)
	
	Local Txt$[5]
	
	If Right(MapName, 6) = "cbmap2"
		Local Name$ = ConvertToUTF8(MapName)
		
		Txt[0] = Left(Name, Len(Name) - 7)
		
		Local f% = OpenFile_Strict(CustomMapsPath + MapName)
		Local Author$ = ConvertToUTF8(ReadLine(f))
		Local Descr$ = ConvertToUTF8(ReadLine(f))
		
		ReadByte(f)
		ReadByte(f)
		
		Local rAmount% = ReadInt(f)
		Local HasForest%
		
		HasForest = (ReadInt(f) > 0)
		
		CloseFile(f)
	Else
		Txt[0] = Left(MapName, Len(MapName) - 6)
		Author = GetLocalString("creator", "unknown")
		Descr = GetLocalString("creator", "nodesc")
		rAmount = 0
		HasForest = False
	EndIf
	Txt[1] = Format(GetLocalString("creator", "author"), Author)
	Txt[2] = Format(GetLocalString("creator", "desc"), Descr)
	If rAmount > 0
		Txt[3] = Format(GetLocalString("creator", "ramount"), rAmount)
	Else
		Txt[3] = Format(GetLocalString("creator", "ramount"), GetLocalString("creator", "unknown"))
	EndIf
	If HasForest
		Txt[4] = Format(GetLocalString("creator", "forest"), GetLocalString("creator", "yes"))
	Else
		Txt[4] = Format(GetLocalString("creator", "forest"), GetLocalString("creator", "no"))
	EndIf
	
	Local StringHeightTxt0% = StringHeight(Txt[0])
	Local StringHeightTxt2% = StringHeight(Txt[2])
	Local CoordEx% = 5 * MenuScale
	
	Lines = GetLineAmount(Txt[2], fW, fH)
	RenderFrame(x, y, Width, (StringHeightTxt0 * 6) + StringHeightTxt2 * Lines + CoordEx)
	
	Color(255, 255, 255)
	TextEx(fX, fY,Txt[0])
	TextEx(fX, fY + StringHeightTxt0, Txt[1])
	RowText(Txt[2], fX, fY + (StringHeightTxt0 * 2), fW, fH)
	TextEx(fX, fY + ((StringHeightTxt0 * 2) + StringHeightTxt2 * Lines + CoordEx), Txt[3])
	TextEx(fX, fY + ((StringHeightTxt0 * 3) + StringHeightTxt2 * Lines + CoordEx), Txt[4])
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS