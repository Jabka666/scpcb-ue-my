Type Mouse
	Field MouseHit1%, MouseHit2%
	Field MouseDown1%
	Field DoubleClick%, DoubleClickSlot%
	Field LastMouseHit1%
	Field MouseUp1%
	Field Mouselook_X_Inc#, Mouselook_Y_Inc#
	Field Mouse_Left_Limit%, Mouse_Right_Limit%
	Field Mouse_Top_Limit%, Mouse_Bottom_Limit%
	Field Mouse_X_Speed_1#, Mouse_Y_Speed_1#
	Field Viewport_Center_X%, Viewport_Center_Y%
End Type

Global mo.Mouse = New Mouse

Type Fonts
	Field FontID%[MaxFontIDAmount]
End Type

Const MaxFontIDAmount% = 8
; ~ Fonts ID Constants
;[Block]
Const Font_Default% = 0
Const Font_Default_Big% = 1
Const Font_Digital% = 2
Const Font_Digital_Big% = 3
Const Font_Journal% = 4
Const Font_Console% = 5
Const Font_Credits% = 6
Const Font_Credits_Big% = 7
;[End Block]

Global fo.Fonts = New Fonts

Const FontsPath$ = "GFX\Fonts\"

Const LauncherWidth% = 640
Const LauncherHeight% = 480

Global LauncherBG%

Type Language ; ~ Game Language
	Field CurrentLanguage$
	Field LanguagePath$
End Type

Type ListLanguage ; ~ Languages in the list
	Field Name$
	Field ID$
	Field Author$
	Field LastModify$
	Field Flag$
	Field FlagImg%
	Field MajorOnly%
	Field Full%
	Field FileSize%
	Field Compatible$
End Type

; ~ Language status constants
;[Block]
Const LANGUAGE_STATUS_NULL% = 0
Const LANGUAGE_STATUS_DOWNLOAD_REQUEST% = 1
Const LANGUAGE_STATUS_DOWNLOAD_START% = 2
Const LANGUAGE_STATUS_DOWNLOADING% = 3
Const LANGUAGE_STATUS_UNPACK_REQUEST% = 4
Const LANGUAGE_STATUS_UNPACK_START% = 5
Const LANGUAGE_STATUS_UNINSTALLING_REQUEST% = 6
Const LANGUAGE_STATUS_UNINSTALLING_START% = 7
Const LANGUAGE_STATUS_DONE% = 8
;[End Block]
Const LocalizaitonPath$ = "Localization\"

Global lang.Language = New Language

Function SetLanguage%(Language$, FromSelector% = True)
	If lang\LanguagePath <> ""
		; ~ Clear previous buffers info
		IniClearBuffer(lang\LanguagePath + LanguageFile)
		IniClearBuffer(lang\LanguagePath + LoadingScreensFile)
		IniClearBuffer(lang\LanguagePath + FontsFile)
		IniClearBuffer(lang\LanguagePath + AchievementsFile)
	EndIf

	lang\CurrentLanguage = Language
	If lang\CurrentLanguage = "en"
		lang\LanguagePath = ""
	Else
		lang\LanguagePath = LocalizaitonPath + lang\CurrentLanguage + "\"
		
		; ~ Write a new buffer
		IniWriteBuffer(lang\LanguagePath + LanguageFile)
		IniWriteBuffer(lang\LanguagePath + LoadingScreensFile)
		IniWriteBuffer(lang\LanguagePath + FontsFile)
		IniWriteBuffer(lang\LanguagePath + AchievementsFile)
	EndIf
	If StringToBoolean(GetLocalString("global", "splitwithspace"))
		SplitSpace = " "
	Else
		SplitSpace = ""
	EndIf
	opt\Language = Language
	InitKeyNames()
	
	; ~ Reload some stuff manually
	If fo\FontID[Font_Default] <> 0 Then FreeFont(fo\FontID[Font_Default])
	fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
	If FromSelector
		AppTitle(GetLocalString("language", "title"))
	Else
		AppTitle(GetLocalString("launcher", "title"))
	EndIf
End Function

SetLanguage(opt\Language)

Type Launcher
	Field TotalGFXModes%
	Field GFXModes%
	Field SelectedGFXMode%
	Field GFXModeWidths%[64], GFXModeHeights%[64]
End Type

Function UpdateLauncher%(lnchr.Launcher)
	Local i%, n%
	
	MenuScale = 1.0
	
	Graphics3D(LauncherWidth, LauncherHeight, 32, 2)
	
	SetBuffer(BackBuffer())
	
	fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
	SetFontEx(fo\FontID[Font_Default])
	
	MenuWhite = LoadImage_Strict("GFX\Menu\menu_white.png")
	MenuGray = LoadImage_Strict("GFX\Menu\menu_gray.png")
	MenuBlack = LoadImage_Strict("GFX\Menu\menu_black.png")
	
	ButtonSFX = LoadSound_Strict("SFX\Interact\Button.ogg")
	ButtonSFX2 = LoadSound_Strict("SFX\Interact\Button2.ogg")
	
	Local LauncherIMG%[2]
	Local LauncherMediaWidth%
	
	LauncherIMG[0] = LoadAnimImage_Strict("GFX\menu\launcher_media.png", 64, 64, 0, 3)
	LauncherMediaWidth = ImageWidth(LauncherIMG[0]) / 2
	LauncherIMG[1] = LoadAnimImage_Strict("GFX\menu\language_button.png", 40, 40, 0, 4)
	
	For i = 1 To lnchr\TotalGFXModes
		Local SameFound% = False
		
		For n = 0 To lnchr\TotalGFXModes - 1
			If lnchr\GFXModeWidths[n] = GfxModeWidth(i) And lnchr\GFXModeHeights[n] = GfxModeHeight(i)
				SameFound = True
				Exit
			EndIf
		Next
		If (Not SameFound)
			If GfxModeWidth(i) >= 800 And GfxModeHeight(i) >= 600
				If opt\GraphicWidth = GfxModeWidth(i) And opt\GraphicHeight = GfxModeHeight(i) Then lnchr\SelectedGFXMode = lnchr\GFXModes
				lnchr\GFXModeWidths[lnchr\GFXModes] = GfxModeWidth(i)
				lnchr\GFXModeHeights[lnchr\GFXModes] = GfxModeHeight(i)
				lnchr\GFXModes = lnchr\GFXModes + 1
			EndIf
		EndIf
	Next
	
	AppTitle(GetLocalString("launcher", "title"))
	
	Local Quit% = False
	Local SelectorDeniedTimer% = 0
	
	Repeat
		Cls()
		
		MousePosX = MouseX()
		MousePosY = MouseY()
		mo\MouseHit1 = MouseHit(1)
		
		Color(255, 255, 255)
		If LauncherBG = 0 Then LauncherBG = LoadImage_Strict("GFX\menu\launcher.png")
		DrawBlock(LauncherBG, 0, 0)
		
		; ~ Resolution selector
		TextEx(LauncherWidth - 620, LauncherHeight - 303, GetLocalString("launcher", "resolution"))
		
		Local x% = LauncherWidth - 600
		Local y% = LauncherHeight - 269
		
		For i = 0 To lnchr\GFXModes - 1
			Color(0, 0, 1)
			If lnchr\SelectedGFXMode = i Then Rect(x - 1, y - 5, 100, 20, False)
			
			TextEx(x, y, (lnchr\GFXModeWidths[i] + "x" + lnchr\GFXModeHeights[i]))
			If MouseOn(x - 1, y - 5, 100, 20)
				Color(100, 100, 100)
				Rect(x - 1, y - 5, 100, 20, False)
				If mo\MouseHit1 Then lnchr\SelectedGFXMode = i
			EndIf
			
			y = y + 20
			If y >= LauncherHeight - 155
				y = LauncherHeight - 269
				x = x + 100
			EndIf
		Next
		; ~ Driver selector
		Color(255, 255, 255)
		TextEx(LauncherWidth - 185, LauncherHeight - 303, GetLocalString("launcher", "gfx"))
		RenderFrame(LauncherWidth - 185, LauncherHeight - 283, 155, 30)
		
		Local DriverName$
		
		If opt\GFXDriver = 1
			DriverName = GetLocalString("launcher", "gfx.primary")
		Else
			DriverName = Format(GetLocalString("launcher", "gfx.num"), opt\GFXDriver - 1)
		EndIf
		TextEx(LauncherWidth - 107.5, LauncherHeight - 273, DriverName, True)
		If UpdateLauncherButton(LauncherWidth - 35, LauncherHeight - 283, 30, 30, ">", False) Then opt\GFXDriver = (opt\GFXDriver + 1)
		If opt\GFXDriver > CountGfxDrivers() Then opt\GFXDriver = 1
		
		; ~ Display selector
		TextEx(LauncherWidth - 185, LauncherHeight - 245, GetLocalString("launcher", "display"))
		
		Local Txt$
		
		Select opt\DisplayMode
			Case 0
				;[Block]
				Txt = GetLocalString("launcher", "display.fullscreen")
				;[End Block]
			Case 1
				;[Block]
				Txt = GetLocalString("launcher", "display.borderless")
				If lnchr\GFXModeWidths[lnchr\SelectedGFXMode] < DesktopWidth()
					TextEx(LauncherWidth - 290, LauncherHeight - 68, Format(Format(GetLocalString("launcher", "upscale"), DesktopWidth(), "{0}"), DesktopHeight(), "{1}"))
				ElseIf lnchr\GFXModeWidths[lnchr\SelectedGFXMode] > DesktopWidth()
					TextEx(LauncherWidth - 290, LauncherHeight - 68, Format(Format(GetLocalString("launcher", "downscale"), DesktopWidth(), "{0}"), DesktopHeight(), "{1}"))
				EndIf
				;[End Block]
			Case 2
				;[Block]
				Txt = GetLocalString("launcher", "display.windowed")
				;[End Block]
		End Select
		
		TextEx(LauncherWidth - 162, LauncherHeight - 133, Format(Format(GetLocalString("launcher", "currres"), lnchr\GFXModeWidths[lnchr\SelectedGFXMode], "{0}"), lnchr\GFXModeHeights[lnchr\SelectedGFXMode], "{1}"), True)
		RenderFrame(LauncherWidth - 185, LauncherHeight - 226, 155, 30)
		TextEx(LauncherWidth - 107.5, LauncherHeight - 216, Txt, True)
		If UpdateLauncherButton(LauncherWidth - 35, LauncherHeight - 226, 30, 30, ">") Then opt\DisplayMode = ((opt\DisplayMode + 1) Mod 3)
		; ~ Launcher tick
		Text(LauncherWidth - 590, LauncherHeight - 127, GetLocalString("launcher", "launcher"))
		opt\LauncherEnabled = UpdateLauncherTick(LauncherWidth - 620, LauncherHeight - 133, opt\LauncherEnabled)
		; ~ Media buttons
		If MouseOn(LauncherWidth - 620, LauncherHeight - 86, 64, 64)
			Rect(LauncherWidth - 621, LauncherHeight - 87, 66, 66, False)
			TextEx(LauncherWidth - 620 + LauncherMediaWidth, LauncherHeight - 106, "DISCORD", True)
			If mo\MouseHit1
				PlaySound_Strict(ButtonSFX)
				ExecFile_Strict("https://discord.gg/n7KdW4u")
			EndIf
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 620, LauncherHeight - 86, 0)
		If MouseOn(LauncherWidth - 510, LauncherHeight - 86, 64, 64)
			Rect(LauncherWidth - 511, LauncherHeight - 87, 66, 66, False)
			TextEx(LauncherWidth - 510 + LauncherMediaWidth, LauncherHeight - 106, "MODDB", True)
			If mo\MouseHit1
				PlaySound_Strict(ButtonSFX)
				ExecFile_Strict("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition")
			EndIf
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 510, LauncherHeight - 86, 1)
		If MouseOn(LauncherWidth - 400, LauncherHeight - 86, 64, 64)
			Rect(LauncherWidth - 401, LauncherHeight - 87, 66, 66, False)
			TextEx(LauncherWidth - 400 + LauncherMediaWidth, LauncherHeight - 106, "YOUTUBE", True)
			If mo\MouseHit1
				PlaySound_Strict(ButtonSFX)
				ExecFile_Strict("https://www.youtube.com/channel/UCPqWOCPfKooDnrLNzA67Acw")
			EndIf
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 400, LauncherHeight - 86, 2)
		; ~ Language selector
		If SelectorDeniedTimer <> 0
			Color(255, 0, 0)
			DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 3)
			Rect(LauncherWidth - 185, LauncherHeight - 186, 40, 40, False)
			Color(255, 255, 255)
			TextEx(LauncherWidth - 185 + 45, LauncherHeight - 166, GetLocalString("launcher", "language.failed"), False, True)
			If (MilliSecs() - SelectorDeniedTimer) > 5000 Then SelectorDeniedTimer = 0
		Else
			If MouseOn(LauncherWidth - 185, LauncherHeight - 186, 40, 40)
				If KeyDown(29) ; ~ LCtrl
					DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 2)
					Rect(LauncherWidth - 185, LauncherHeight - 186, 40, 40, False)
					TextEx(LauncherWidth - 185 + 45, LauncherHeight - 166, GetLocalString("launcher", "language.iter"), False, True)
					If mo\MouseHit1
						PlaySound_Strict(ButtonSFX)
						If FileType("Localization") = 2
							SetLanguage(FindNextDirectory("Localization", opt\Language, "en"), False)
							FreeImage(LauncherBG) : LauncherBG = 0
							IniWriteString(OptionFile, "Global", "Language", opt\Language)
						EndIf
					EndIf
				Else
					DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 1)
					Rect(LauncherWidth - 185, LauncherHeight - 186, 40, 40, False)
					TextEx(LauncherWidth - 185 + 45, LauncherHeight - 166, GetLocalString("launcher", "language"), False, True)
					If mo\MouseHit1
						PlaySound_Strict(ButtonSFX)
						If UpdateLanguageSelector() Then SelectorDeniedTimer = MilliSecs()
					EndIf
				EndIf
			Else
				DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 0)
			EndIf
		EndIf
		; ~ Report button
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 105, 165, 30, GetLocalString("launcher", "report")) Then ExecFile_Strict("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition/news/bug-reports1")
		; ~ Changelog button
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 50, 165, 30, GetLocalString("launcher", "changelog")) Then ExecFile_Strict("Changelog.txt")
		; ~ Launch button
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 105, 100, 30, GetLocalString("launcher", "launch"))
			If opt\DisplayMode = 1
				opt\GraphicWidth = DesktopWidth()
				opt\GraphicHeight = DesktopHeight()
			Else
				opt\GraphicWidth = lnchr\GFXModeWidths[lnchr\SelectedGFXMode]
				opt\GraphicHeight = lnchr\GFXModeHeights[lnchr\SelectedGFXMode]
			EndIf
			GraphicWidthFloat = Float(opt\GraphicWidth) : GraphicHeightFloat = Float(opt\GraphicHeight)
			opt\RealGraphicWidth = opt\GraphicWidth : RealGraphicWidthFloat = Float(opt\RealGraphicWidth)
			opt\RealGraphicHeight = opt\GraphicHeight : RealGraphicHeightFloat = Float(opt\RealGraphicHeight)
			Exit
		EndIf
		; ~ Exit button
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 50, 100, 30, GetLocalString("launcher", "exit"))
			Quit = True
			Exit
		EndIf
		
		; ~ Driver name (tooltip)
		If MouseOn(LauncherWidth - 185, LauncherHeight - 283, 155, 30)
			Local TooltipX% = MousePosX + 5
			Local TooltipY% = MousePosY + 10
			Local Tooltip$ = ConvertToUTF8(GfxDriverName(opt\GFXDriver))
			Local TooltipWidth% = StringWidth(Tooltip)
			
			If (TooltipX + TooltipWidth + FontWidth()) > LauncherWidth Then TooltipX = TooltipX - TooltipWidth - 10
			RenderFrame(TooltipX, TooltipY, TooltipWidth + FontWidth(), FontHeight() + 16)
			TextEx(TooltipX + 8, TooltipY + 8, Tooltip)
		EndIf
		Flip()
	Forever
	
	IniWriteString(OptionFile, "Global", "Width", lnchr\GFXModeWidths[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Global", "Height", lnchr\GFXModeHeights[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	IniWriteString(OptionFile, "Global", "Display Mode", opt\DisplayMode)
	IniWriteString(OptionFile, "Global", "GFX Driver", opt\GFXDriver)
	IniWriteString(OptionFile, "Advanced", "No Progress Bar", opt\NoProgressBar)
	
	For i = 0 To 1
		FreeImage(LauncherIMG[i]) : LauncherIMG[i] = 0
	Next	
	
	MousePosX = 0
	MousePosY = 0
	mo\MouseHit1 = False
	
	FreeImage(LauncherBG) : LauncherBG = 0
	FreeImage(MenuBlack) : MenuBlack = 0
	FreeImage(MenuGray) : MenuGray = 0
	FreeImage(MenuWhite) : MenuWhite = 0
	
	FreeSound_Strict(ButtonSFX) : ButtonSFX = 0
	FreeSound_Strict(ButtonSFX2) : ButtonSFX2 = 0
	
	EndGraphics()
	
	If Quit Then End()
End Function

Function UpdateLanguageSelector%()
	Local BasePath$ = GetEnv("AppData") + "\scpcb-ue\temp\"
	
	DeleteFolder(BasePath) : CreateDir(BasePath) ; ~ Create temporary folder
	If FileType(LocalizaitonPath) <> 2 Then CreateDir(LocalizaitonPath)
	CreateDir(BasePath + "flags/")
	DownloadFile("https://files.ziyuesinicization.site/cbue/list.txt", BasePath + "temp.txt") ; ~ List of languages
	
	Local lan.ListLanguage
	Local File% = OpenFile_Strict(BasePath + "temp.txt")
	Local l$
	
	If File <> 0
		While (Not Eof(File))
			l = ReadLine(File)
			If l <> ""
				lan.ListLanguage = New ListLanguage
				lan\Name = ParseDomainTXT(l, "name") ; ~ Name of localization
				lan\ID = ParseDomainTXT(l, "id") ; ~ Language ID of localization
				lan\Author = ParseDomainTXT(l, "author") ; ~ Author of translation
				lan\LastModify = ParseDomainTXT(l, "mod") ; ~ Last modify date
				lan\MajorOnly = Int(ParseDomainTXT(l, "majoronly")) ; ~ loca.ini only?
				lan\Full = Int(ParseDomainTXT(l, "full")) ; ~ Full complete translation
				lan\Flag = ParseDomainTXT(l, "flag") ; ~ Flag of country
				lan\FileSize = Int(ParseDomainTXT(l, "size")) ; ~ Size of localization
				lan\Compatible = ParseDomainTXT(l, "compatible") ; ~ Compatible version
				If FileType(BasePath + "flags/" + lan\Flag) <> 1 Then DownloadFile("https://files.ziyuesinicization.site/cbue/flags/" + lan\Flag, BasePath + "flags/" + lan\Flag) ; ~ Flags of languages
				If lan\FlagImg = 0 Then lan\FlagImg = LoadImage_Strict(BasePath + "flags\" + lan\Flag)
			Else
				Exit
			EndIf
		Wend
		CloseFile(File)
		DeleteFile(BasePath + "temp.txt")
	Else
		Return(True)
	EndIf
	
	SetBuffer(BackBuffer())
	Cls()
	Flip()
	
	Local LanguageBG%
	Local LanguageIMG% = CreateImage(452, 254)
	Local ButtonImages% = LoadAnimImage_Strict("GFX\menu\buttons.png", 21, 21, 0, 6)
	Local CurrFontHeight% = FontHeight() / 2
	Local SelectedLanguage.ListLanguage = Null
	Local MouseHoverLanguage.ListLanguage = Null
	Local CurrentStatus% = LANGUAGE_STATUS_NULL
	Local RequestLanguage.ListLanguage = Null
	Local StatusTimer% = 0
	
	AppTitle(GetLocalString("language", "title"))
	
	Repeat
		MousePosX = MouseX()
		MousePosY = MouseY()
		mo\MouseHit1 = MouseHit(1)
		mo\MouseDown1 = MouseDown(1)
		
		Select CurrentStatus
			Case LANGUAGE_STATUS_DOWNLOAD_START
				;[Block]
				If (Not RequestLanguage\MajorOnly) Then 
					If opt\NoProgressBar Then
						DownloadFile("https://files.ziyuesinicization.site/cbue/" + RequestLanguage\ID + ".zip", BasePath + "/local.zip")
					Else
						DownloadFileThread("https://files.ziyuesinicization.site/cbue/" + RequestLanguage\ID + ".zip", BasePath + "/local.zip")
					EndIf
				EndIf
				DownloadFile("https://weblate.ziyuesinicization.site/api/translations/scpcb-ue/local-ini/" + RequestLanguage\ID + "/file/", BasePath + "/local.ini")
				DownloadFile("https://weblate.ziyuesinicization.site/api/translations/scpcb-ue/achievements-ini/" + RequestLanguage\ID + "/file/", BasePath + "/achievements.ini")
				CurrentStatus = LANGUAGE_STATUS_DOWNLOADING
				;[End Block]
			Case LANGUAGE_STATUS_UNPACK_START
				;[Block]
				; ~ Unzip function will delete everything in the directory, so we need to move local.ini to directory after unziping
				CreateDir(LocalizaitonPath + RequestLanguage\ID)
				If (Not RequestLanguage\MajorOnly) Then Unzip(BasePath + "/local.zip", LocalizaitonPath + RequestLanguage\ID)
				CreateDir(LocalizaitonPath + RequestLanguage\ID + "/Data")
				CopyFile(BasePath + "/local.ini", LocalizaitonPath + RequestLanguage\ID + "/Data/local.ini")
				CopyFile(BasePath + "/achievements.ini", LocalizaitonPath + RequestLanguage\ID + "/Data/achievements.ini")
				StatusTimer = MilliSecs()
				CurrentStatus = LANGUAGE_STATUS_DONE
				;[End Block]
			Case LANGUAGE_STATUS_UNINSTALLING_START
				;[Block]
				DeleteFolder(LocalizaitonPath + SelectedLanguage\ID)
				StatusTimer = MilliSecs()
				CurrentStatus = LANGUAGE_STATUS_DONE
				;[End Block]
		End Select
		If CurrentStatus = LANGUAGE_STATUS_DONE
			If (MilliSecs() - StatusTimer) > 1500 Then CurrentStatus = LANGUAGE_STATUS_NULL
		EndIf
		
		SetBuffer(BackBuffer())
		Cls()
		
		Local x#, y#, LinesAmount%
		
		Color(255, 255, 255)
		If LanguageBG = 0 Then LanguageBG = LoadImage_Strict("GFX\Menu\Language.png")
		DrawBlock(LanguageBG, 0, 0)
		Rect(LauncherWidth - 161, LauncherHeight - 285, 155, 110)
		
		If LinesAmount > 13
			y = LauncherHeight - 280 - (20 * ScrollMenuHeight * ScrollBarY)
			SetBuffer(ImageBuffer(LanguageIMG))
			DrawImage(LanguageBG, -20, -195)
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 1)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 2, y - 195, 432, lan\FlagImg)
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 432, 20)
					DrawImage(ButtonImages, 410, y - 195 - CurrFontHeight, 5)
					If MouseOn(430, y - CurrFontHeight, 21, 21)
						Color(150, 150, 150)
						Rect(410, y - 195 - CurrFontHeight, 20, 20, False)
						MouseHoverLanguage = lan
					EndIf
				EndIf
				If lan\ID = opt\Language
					Color(200, 0, 0)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage
					Color(0, 0, 1)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 432, 20)
					Color(150, 150, 150)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y = y + 20
				LinesAmount = LinesAmount + 1
			Next
			SetBuffer(BackBuffer())
			DrawBlock(LanguageIMG, LauncherWidth - 620, LauncherHeight - 285)
			Color(10, 10, 10)
			Rect(LauncherWidth - 188, LauncherHeight - 285, 20, 254, True)
			ScrollMenuHeight = LinesAmount - 12
			ScrollBarY = UpdateLauncherScrollBar(20, 254, 452, 195 + (254 - (254 - (4 * ScrollMenuHeight))) * ScrollBarY, 20, 254 - (4 * ScrollMenuHeight), ScrollBarY, True)
		Else
			y = LauncherHeight - 280
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 1)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", LauncherWidth - 619, y, 432, lan\FlagImg)
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 430, 20)
					DrawImage(ButtonImages, LauncherWidth - 210, y - 4, 5)
					If MouseOn(LauncherWidth - 210, y - 4, 21, 21)
						Color(150, 150, 150)
						Rect(LauncherWidth - 210, y - 4, 20, 20, False)
						MouseHoverLanguage = lan
					EndIf
				EndIf
				If lan\ID = opt\Language
					Color(200, 0, 0)
					Rect(LauncherWidth - 620, y - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage
					Color(0, 0, 1)
					Rect(LauncherWidth - 620, y - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 432, 20)
					Color(150, 150, 150)
					Rect(LauncherWidth - 620, y - CurrFontHeight, 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y = y + 20
				LinesAmount = LinesAmount + 1
			Next
			ScrollMenuHeight = LinesAmount
		EndIf
		
		Local InfoBoxContent$ = GetLocalString("language", "more")
		
		Color(100, 100, 100)
		If CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_REQUEST
			InfoBoxContent = GetLocalString("language", "downloading")
			If Not opt\NoProgressBar Then UpdateLauncherButton(LauncherWidth - 161, LauncherHeight - 165, 155, 30, "0%", Font_Default, False, True)
			CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_START
			If RequestLanguage\MajorOnly
				CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
			Else 
				CurrentStatus = LANGUAGE_STATUS_DOWNLOADING
			EndIf
		ElseIf CurrentStatus = LANGUAGE_STATUS_DOWNLOADING
			If Not opt\NoProgressBar Then
				InfoBoxContent = Format(Format(GetLocalString("language", "downloading.filesize"), SimpleFileSize(FileSize(BasePath + "/local.zip")), "{0}"), SimpleFileSize(RequestLanguage\FileSize), "{1}")
				UpdateLauncherButton(LauncherWidth - 161, LauncherHeight - 165, 155, 30, Str(Int(Ceil((Float(FileSize(BasePath + "/local.zip")) / Float(RequestLanguage\FileSize)) * 100))) + "%", Font_Default, False, True)
				If FileSize(BasePath + "/local.zip") >= RequestLanguage\FileSize Then CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
			Else
				CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
			EndIf
		ElseIf CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
			InfoBoxContent = GetLocalString("language", "unpacking")
			UpdateLauncherButton(LauncherWidth - 161, LauncherHeight - 165, 155, 30, "100%", Font_Default, False, True)
			CurrentStatus = LANGUAGE_STATUS_UNPACK_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_REQUEST
			InfoBoxContent = GetLocalString("language", "uninstalling")
			CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_DONE
			InfoBoxContent = GetLocalString("language", "done")
		EndIf
		
		Color(0, 0, 1)
		RowText(InfoBoxContent, LauncherWidth - 159, LauncherHeight - 281, 151, 102)
		Local NoProgressBar%
		
		If SelectedLanguage <> Null
			If SelectedLanguage\ID = opt\Language
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "contribute"), Font_Default, ButtonImages, 4, IsDownloadingLanguage(CurrentStatus)) Then ExecFile_Strict("https://github.ziyuesinicization.site/Jabka666/scpcb-ue-my/wiki/How-to-contribute-a-language")
			ElseIf SelectedLanguage\Name = "English"
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), Font_Default, ButtonImages, 2, IsDownloadingLanguage(CurrentStatus))
					SetLanguage(SelectedLanguage\ID)
					FreeImage(LanguageBG) : LanguageBG = 0
					IniWriteString(OptionFile, "Global", "Language", opt\Language)
				EndIf
			ElseIf FileType(LocalizaitonPath + SelectedLanguage\ID) = 2
				If SelectedLanguage\ID <> opt\Language
					If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 165, 155, 30, GetLocalString("language", "uninstall"), Font_Default, ButtonImages, 3, IsDownloadingLanguage(CurrentStatus))
						CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_REQUEST
						RequestLanguage = SelectedLanguage
					EndIf
					If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), Font_Default, ButtonImages, 2, IsDownloadingLanguage(CurrentStatus))
						SetLanguage(SelectedLanguage\ID)
						FreeImage(LanguageBG) : LanguageBG = 0
						IniWriteString(OptionFile, "Global", "Language", opt\Language)
					EndIf
				EndIf
			Else
				If (CurrentStatus = LANGUAGE_STATUS_NULL) Lor (CurrentStatus = LANGUAGE_STATUS_DONE) Then
					Color(255, 255, 255)
					Text(LauncherWidth - 131, LauncherHeight - 148, GetLocalString("language", "speedup"), False, True)
					NoProgressBar = UpdateLauncherTick(LauncherWidth - 161, LauncherHeight - 157, opt\NoProgressBar)
					If NoProgressBar <> opt\NoProgressBar Then 
						If NoProgressBar Then
							Color(255, 255, 255)
							Repeat
								MousePosX = MouseX()
								MousePosY = MouseY()
								mo\MouseHit1 = MouseHit(1)
								Text(320, 180, GetLocalString("language", "speedup.notice_1"), True)
								Text(320, 200, GetLocalString("language", "speedup.notice_2"), True)
								Text(320, 220, GetLocalString("language", "speedup.notice_3"), True)
								Text(320, 260, GetLocalString("language", "speedup.notice_4"), True)
								If UpdateLauncherButton(200, 300, 100, 30, GetLocalString("language", "yes"))
									Delay(100)
									opt\NoProgressBar = True
									Exit
								EndIf
								If UpdateLauncherButton(LauncherWidth - 300, 300, 100, 30, GetLocalString("language", "no"))
									Delay(100)
									Exit
								EndIf
								Delay(10)
								Flip(True)
								Cls
							Forever
						Else
							opt\NoProgressBar = False
						EndIf
					EndIf
				EndIf
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "download"), Font_Default, ButtonImages, 1, IsDownloadingLanguage(CurrentStatus))
					CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_REQUEST
					RequestLanguage = SelectedLanguage
				EndIf
			EndIf
		EndIf
		If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 65, 155, 30, GetLocalString("menu", "back"), Font_Default, ButtonImages, 0, IsDownloadingLanguage(CurrentStatus)) Then Exit
		
		If MouseHoverLanguage <> Null
			Color(255, 255, 255)
			Local Name$ = Format(GetLocalString("language", "name"), MouseHoverLanguage\Name)
			Local ID$ = Format(GetLocalString("language", "id"), MouseHoverLanguage\ID)
			
			If MouseHoverLanguage\ID <> "en"
				Local Author$ = Format(GetLocalString("language", "author"), MouseHoverLanguage\Author)
				Local Prefect$ = Format(GetLocalString("language", "full"), GetLocalString("language", "yes")) ; ~ Get width only
				Local Prefect2$ = Format(GetLocalString("language", "full"), GetLocalString("language", "no"))
				Local Compatible$ = Format(GetLocalString("language", "compatible"), "v" + MouseHoverLanguage\Compatible)
				
				If (Not MouseHoverLanguage\MajorOnly) 
					Local Size$ = Format(GetLocalString("language", "size"), SimpleFileSize(MouseHoverLanguage\FileSize))
					Local LastMod$ = Format(GetLocalString("language", "lastmod"), MouseHoverLanguage\LastModify)
					Local Height% = FontHeight() * 13
				Else
					Height = FontHeight() * 10
				EndIf
			Else
				Author = ""
				LastMod	= ""
				Prefect = ""
				Compatible = ""
				Prefect2 = ""
				Size = ""
				Height = FontHeight() * 4.5
			EndIf
			
			Local Width% = Max(Max(Max(Max(Max(Max(Max(StringWidth(Name), StringWidth(ID)), StringWidth(Author)), StringWidth(Prefect)), StringWidth(Size)), StringWidth(Compatible)), StringWidth(Prefect2)), StringWidth(LastMod))
			
			x = MousePosX + 10
			y = MousePosY + 10
			If (x + Width + FontWidth()) > LauncherWidth Then x = x - Width - 10 ; ~ If tooltip is too long, then move tooltip to the left
			If (y + Height + FontHeight()) > LauncherHeight Then y = y - Height - 15
			RenderFrame(x, y, Width + FontWidth(), Height)
			x = x + 5
			TextEx(x, y + 8, Name)
			TextEx(x, y + 23, ID)
			If MouseHoverLanguage\ID <> "en"
				TextEx(x, y + 38, Author)
				If MouseHoverLanguage\Full
					DualColorText(x, y + 53, Format(GetLocalString("language", "full"), ""), GetLocalString("language", "yes"), 255, 255, 255, 0, 200, 0)
				Else
					DualColorText(x, y + 53, Format(GetLocalString("language", "full"), ""), GetLocalString("language", "no"), 255, 255, 255, 200, 0, 0)
				EndIf
				If MouseHoverLanguage\Compatible = VersionNumber
					DualColorText(x, y + 68, Format(GetLocalString("language", "compatible"), ""), "v" + MouseHoverLanguage\Compatible, 255, 255, 255, 0, 200, 0)
				Else
					DualColorText(x, y + 68, Format(GetLocalString("language", "compatible"), ""), "v" + MouseHoverLanguage\Compatible, 255, 255, 255, 200, 0, 0)
				EndIf
				If (Not MouseHoverLanguage\MajorOnly)
					TextEx(x, y + 83, LastMod)
					TextEx(x, y + 98, Size) ; ~ local.ini only -> unable to get the file size
				EndIf
			EndIf
			If mo\MouseHit1 Then ExecFile("https://github.ziyuesinicization.site/Jabka666/scpcb-ue-my/wiki/Language-List-of-Ultimate-Edition")
		EndIf
		MouseHoverLanguage = Null
		
		Flip()
	Forever
	
	mo\MouseHit1 = False
	mo\MouseDown1 = False
	ScrollBarY = 0.0
	ScrollMenuHeight = 0.0
	
	For lan.ListLanguage = Each ListLanguage
		If lan\FlagImg <> 0 Then FreeImage(lan\FlagImg) : lan\FlagImg = 0
		Delete(lan)
	Next
	
	FreeImage(LanguageIMG) : LanguageIMG = 0
	FreeImage(LanguageBG) : LanguageBG = 0
	FreeImage(ButtonImages) : ButtonImages = 0
	
	FreeImage(LauncherBG) : LauncherBG = 0
	
	DeleteFolder(BasePath) ; ~ Delete temporary folder
	
	AppTitle(GetLocalString("launcher", "title"))
End Function

Function IsDownloadingLanguage$(Status%) ; ~ Kind of inline
	Return(Not (Status = LANGUAGE_STATUS_DONE Lor Status = LANGUAGE_STATUS_NULL))
End Function

Function UpdateLauncherButton%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, WaitForMouseUp% = False, Locked% = False, R% = 255, G% = 255, B% = 255)
	Local Clicked% = False
	
	RenderFrame(x, y, Width, Height, 0, 0, Locked)
	If MouseOn(x, y, Width, Height)
		Color(30, 30, 30)
		If (mo\MouseHit1 And (Not WaitForMouseUp)) Lor (mo\MouseUp1 And WaitForMouseUp)
			If Locked
				PlaySound_Strict(ButtonSFX2)
			Else
				Clicked = True
				PlaySound_Strict(ButtonSFX)
			EndIf
		EndIf
		Rect(x + 3, y + 3, Width - 6, Height - 6)
	Else
		Color(0, 0, 0)
	EndIf
	
	If Locked
		If R <> 255 Lor G <> 255 Lor B <> 255
			Color(R, G, B)
		Else
			Color(100, 100, 100)
		EndIf
	Else
		Color(R, G, B)
	EndIf
	SetFontEx(fo\FontID[FontID])
	TextEx(x + (Width / 2), y + (Height / 2), Txt, True, True)
	Return(Clicked)
End Function

Function UpdateLauncherDownloadButton%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
	Local Pushed% = False
	
	Color(50, 50, 50)
	If (Not Disabled)
		If MouseOn(x, y, Width, Height)
			If mo\MouseDown1
				Pushed = True
				Color(30, 30, 30)
			Else
				Color(100, 100, 100)
			EndIf
		EndIf
	EndIf
	
	If Pushed
		Rect(x, y, Width, Height)
		Color(130, 130, 130)
		Rect(x + 1, y + 1, Width - 1, Height - 1, False)
		Color(10, 10, 10)
		Rect(x, y, Width, Height, False)
		Color(255, 255, 255)
		Line(x, y + Height - 1, x + Width - 1, y + Height - 1)
		Line(x + Width - 1, y, x + Width - 1, y + Height - 1)
	Else
		Rect(x, y, Width, Height)
		Color(130, 130, 130)
		Rect(x, y, Width - 1, Height - 1, False)
		Color(255, 255, 255)
		Rect(x, y, Width, Height, False)
		Color(10, 10, 10)
		Line(x, y + Height - 1, x + Width - 1, y + Height - 1)
		Line(x + Width - 1, y, x + Width - 1, y + Height - 1)
	EndIf
	
	Color(255, 255, 255)
	If Disabled Then Color(100, 100, 100)
	TextEx(x + (Width / 2), y + (Height / 2) - 1, Txt, True, True)
	
	Color(0, 0, 0)
	
	If Pushed And mo\MouseHit1
		PlaySound_Strict(ButtonSFX)
		Return(True)
	EndIf
End Function

Function UpdateLauncherButtonWithImage%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, Img% = 0, Frame% = 0, Locked% = False)
	Txt = String(" ", ImageWidth(Img) / 8) + Txt
	
	Local Result% = UpdateLauncherButton(x, y, Width, Height, Txt, FontID, False, Locked)
	
	DrawImage(Img, x + (Width / 2) - (StringWidth(Txt) / 2) - 3, y + (Height / 2) - ImageHeight(Img) / 2, Frame) ; ~ No DrawBlock please
	Return(Result)
End Function

Function UpdateLauncherTick%(x%, y%, Selected%, Locked% = False)
	Local Width% = 20, Height% = 20
	Local Highlight% = MouseOn(x, y, Width, Height)
	Local IMG%
	
	If Locked
		IMG = MenuGray
	Else
		IMG = MenuWhite
	EndIf
	
	Color(255, 255, 255)
	RenderTiledImageRect(IMG, (x Mod 256), (y Mod 256), 512, 512, x, y, Width, Height)
	
	If Highlight
		If Locked
			Color(0, 0, 0)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX2)
		Else
			Color(50, 50, 50)
			If mo\MouseHit1
				Selected = (Not Selected)
				PlaySound_Strict(ButtonSFX)
			EndIf
		EndIf
	Else
		Color(0, 0, 0)
	EndIf
	
	Rect(x + 2, y + 2, Width - 4, Height - 4)
	
	If Selected
		If Highlight
			Color(255, 255, 255)
		Else
			Color(200, 200, 200)
		EndIf
		RenderTiledImageRect(IMG, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, Width - 8, Height - 8)
	EndIf
	Color(255, 255, 255)
	Return(Selected)
End Function

Function UpdateLauncherScrollBar#(Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Value#, Vertical% = False)
	Local MouseSpeedX# = MouseXSpeed()
	Local MouseSpeedY# = MouseYSpeed()
	
	Color(0, 0, 0)
	UpdateLauncherDownloadButton(BarX, BarY, BarWidth, BarHeight, "")
	
	If (Not Vertical) ; ~ Horizontal
		If Height > 10
			Color(255, 255, 255)
			Rect(BarX + (BarWidth / 2), BarY + 5, 2, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) - 3, BarY + 5, 2, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) + 3, BarY + 5, 2, BarHeight - 10)
		EndIf
	Else ; ~ Vertical
		If Width > 10
			Color(255, 255, 255)
			Rect(BarX + 4, BarY + (BarHeight / 2), BarWidth - 10, 2)
			Rect(BarX + 4, BarY + (BarHeight / 2) - 3, BarWidth - 10, 2)
			Rect(BarX + 4, BarY + (BarHeight / 2) + 3, BarWidth - 10, 2)
		EndIf
	EndIf
	
	OnScrollBar = (mo\MouseDown1 And MouseOn(BarX, BarY, BarWidth, BarHeight))
	If OnScrollBar
		If (Not Vertical)
			Return(Min(Max(Value + MouseSpeedX / Float(Width - BarWidth), 0.0), 1.0))
		Else
			Return(Min(Max(Value + MouseSpeedY / Float(Height - BarHeight), 0.0), 1.0))
		EndIf
	EndIf
	
	Local MouseSpeedZ# = MouseZSpeed()
	
	; ~ Only for vertical scroll bars
	If MouseSpeedZ <> 0.0 Then Return(Min(Max(Value - (MouseSpeedZ * 3.0) / Float(Height - BarHeight), 0.0), 1.0))
	
	Return(Value)
End Function

Function LimitText%(Txt$, x%, y%, Width%)
	Local TextLength%
	Local UnFitting%
	Local LetterWidth%
	
	If Txt = "" Lor Width = 0 Then Return(0)
	TextLength = StringWidth(Txt)
	UnFitting = TextLength - Width
	If UnFitting <= 0
		TextEx(x, y, Txt, 0, 0)
	Else
		LetterWidth = TextLength / Len(Txt)
		TextEx(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
	EndIf
End Function

Function LimitTextWithImage%(Txt$, x%, y%, Width%, Img%, Frame% = 0)
	DrawBlock(Img, x, y + (StringHeight(Txt) / 2) - (ImageHeight(Img) / 2) - 1, Frame)
	LimitText(Txt, x + 3 + ImageWidth(Img), y, Width - ImageWidth(Img) - 3)
End Function

Function DualColorText%(x%, y%, Txt1$, Txt2$, ColorR1%, ColorG1%, ColorB1%, ColorR2%, ColorG2%, ColorB2%)
	Local OldR% = ColorRed()
	Local OldG% = ColorGreen()
	Local OldB% = ColorBlue()
	
	Color(ColorR1, ColorG1, ColorB1)
	TextEx(x, y, Txt1)
	Color(ColorR2, ColorG2, ColorB2)
	TextEx(x + StringWidth(Txt1), y, Txt2)
	Color(OldR, OldG, OldB)
End Function

Function SimpleFileSize$(Size%)
	Local fSize# = Float(Size)
	
	If Size >= 1048576 ; >= 1 MB
		If Size >= 1073741824 ; >= 1 GB
			Return(Str(Ceil((fSize / 1024 / 1024 / 1024) * 100) / 100) + "GB")
		Else
			Return(Str(Ceil((fSize / 1024 / 1024) * 100) / 100) + "MB")
		EndIf
	Else
		Return(Str(Ceil((fSize / 1024) * 100) / 100) + "KB")
	EndIf
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D TSS