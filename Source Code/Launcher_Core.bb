Const LauncherWidth% = 640
Const LauncherHeight% = 480

Global LauncherBG%

Function UpdateLauncher%(lnchr.Launcher)
	Local i%, n%
	
	MenuScale = 1.0
	
	Graphics3D(LauncherWidth, LauncherHeight, 32, 2)
	
	SetBuffer(BackBuffer())
	
	opt\RealGraphicWidth = opt\GraphicWidth
	opt\RealGraphicHeight = opt\GraphicHeight
	
	fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
	SetFont2(fo\FontID[Font_Default])
	
	MenuWhite = LoadImage_Strict("GFX\Menu\menu_white.png")
	MenuBlack = LoadImage_Strict("GFX\Menu\menu_black.png")
	
	Local LauncherIMG%[2]
	Local LauncherMediaWidth%
	
	LauncherIMG[0] = LoadAnimImage_Strict("GFX\menu\launcher_media.png", 64, 64, 0, 3)
	LauncherMediaWidth = ImageWidth(LauncherIMG[0]) / 2
	LauncherIMG[1] = LoadAnimImage_Strict("GFX\menu\language_button.png", 40, 40, 0, 2)
	
	For i = 1 To lnchr\TotalGFXModes
		Local SameFound% = False
		
		For n = 0 To lnchr\TotalGFXModes - 1
			If lnchr\GFXModeWidths[n] = GfxModeWidth(i) And lnchr\GFXModeHeights[n] = GfxModeHeight(i) Then
				SameFound = True
				Exit
			EndIf
		Next
		If (Not SameFound) Then
			If GfxModeWidth(i) >= 800 And GfxModeHeight(i) >= 600 Then
				If opt\GraphicWidth = GfxModeWidth(i) And opt\GraphicHeight = GfxModeHeight(i) Then lnchr\SelectedGFXMode = lnchr\GFXModes
				lnchr\GFXModeWidths[lnchr\GFXModes] = GfxModeWidth(i)
				lnchr\GFXModeHeights[lnchr\GFXModes] = GfxModeHeight(i)
				lnchr\GFXModes = lnchr\GFXModes + 1
			EndIf
		EndIf
	Next
	
	AppTitle(GetLocalString("launcher", "title"))
	
	Local Quit% = False
	
	Repeat
		Cls()
		
		mo\MouseHit1 = MouseHit(1)
		
		Color(255, 255, 255)
		If (Not LauncherBG) Then LauncherBG = LoadImage_Strict("GFX\menu\launcher.png")
		DrawBlock(LauncherBG, 0, 0)
		
		; ~ Resolution selector
		Text2(LauncherWidth - 620, LauncherHeight - 303, GetLocalString("launcher", "resolution"))
		
		Local x% = LauncherWidth - 600
		Local y% = LauncherHeight - 269
		
		For i = 0 To lnchr\GFXModes - 1
			Color(0, 0, 1)
			If lnchr\SelectedGFXMode = i Then Rect(x - 1, y - 5, 100, 20, False)
			
			Text2(x, y, (lnchr\GFXModeWidths[i] + "x" + lnchr\GFXModeHeights[i]))
			If MouseOn(x - 1, y - 5, 100, 20) Then
				Color(100, 100, 100)
				Rect(x - 1, y - 5, 100, 20, False)
				If mo\MouseHit1 Then lnchr\SelectedGFXMode = i
			EndIf
			
			y = y + 20
			If y >= LauncherHeight - 155 Then
				y = LauncherHeight - 269
				x = x + 100
			EndIf
		Next
		; ~ Driver selector
		Color(255, 255, 255)
		Text2(LauncherWidth - 185, LauncherHeight - 303, GetLocalString("launcher", "gfx"))
		RenderFrame(LauncherWidth - 185, LauncherHeight - 283, 155, 30)
		
		Local DriverName$
		
		If opt\GFXDriver = 1 Then
			DriverName = GetLocalString("launcher", "gfx.primary")
		Else
			DriverName = Format(GetLocalString("launcher", "gfx.num"), opt\GFXDriver - 1)
		EndIf
		Text2(LauncherWidth - 107.5, LauncherHeight - 273, DriverName, True)
		If UpdateLauncherButton(LauncherWidth - 35, LauncherHeight - 283, 30, 30, ">", False) Then opt\GFXDriver = (opt\GFXDriver + 1)
		If opt\GFXDriver > CountGfxDrivers() Then opt\GFXDriver = 1
		
		; ~ Display selector
		Text2(LauncherWidth - 185, LauncherHeight - 245, GetLocalString("launcher", "display"))
		
		Local Txt$
		
		Select opt\DisplayMode
			Case 0
				;[Block]
				Txt = GetLocalString("launcher", "display.fullscreen")
				;[End Block]
			Case 1
				;[Block]
				Txt = GetLocalString("launcher", "display.borderless")
				If lnchr\GFXModeWidths[lnchr\SelectedGFXMode] < DesktopWidth() Then
					Text2(LauncherWidth - 290, LauncherHeight - 68, Format(Format(GetLocalString("launcher", "upscale"), DesktopWidth(), "{0}"), DesktopHeight(), "{1}"))
				ElseIf lnchr\GFXModeWidths[lnchr\SelectedGFXMode] > DesktopWidth() Then
					Text2(LauncherWidth - 290, LauncherHeight - 68, Format(Format(GetLocalString("launcher", "downscale"), DesktopWidth(), "{0}"), DesktopHeight(), "{1}"))
				EndIf
				;[End Block]
			Case 2
				;[Block]
				Txt = GetLocalString("launcher", "display.windowed")
				;[End Block]
		End Select
		
		Text2(LauncherWidth - 162, LauncherHeight - 133, Format(Format(GetLocalString("launcher", "currres"), lnchr\GFXModeWidths[lnchr\SelectedGFXMode], "{0}"), lnchr\GFXModeHeights[lnchr\SelectedGFXMode], "{1}"), True)
		RenderFrame(LauncherWidth - 185, LauncherHeight - 226, 155, 30)
		Text2(LauncherWidth - 107.5, LauncherHeight - 216, Txt, True)
		If UpdateLauncherButton(LauncherWidth - 35, LauncherHeight - 226, 30, 30, ">") Then opt\DisplayMode = ((opt\DisplayMode + 1) Mod 3)
		; ~ Launcher tick
		Text(LauncherWidth - 620, LauncherHeight - 130, GetLocalString("launcher", "launcher"))
		opt\LauncherEnabled = UpdateLauncherTick(LauncherWidth - 480, LauncherHeight - 133, opt\LauncherEnabled)
		; ~ Media buttons
		If MouseOn(LauncherWidth - 620, LauncherHeight - 86, 64, 64) Then
			Rect(LauncherWidth - 621, LauncherHeight - 87, 66, 66, False)
			Text2(LauncherWidth - 620 + LauncherMediaWidth, LauncherHeight - 106, "DISCORD", True)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX) : ExecFile_Strict("https://discord.gg/n7KdW4u")
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 620, LauncherHeight - 86, 0)
		If MouseOn(LauncherWidth - 510, LauncherHeight - 86, 64, 64) Then
			Rect(LauncherWidth - 511, LauncherHeight - 87, 66, 66, False)
			Text2(LauncherWidth - 510 + LauncherMediaWidth, LauncherHeight - 106, "MODDB", True)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX) : ExecFile_Strict("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition")
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 510, LauncherHeight - 86, 1)
		If MouseOn(LauncherWidth - 400, LauncherHeight - 86, 64, 64) Then
			Rect(LauncherWidth - 401, LauncherHeight - 87, 66, 66, False)
			Text2(LauncherWidth - 400 + LauncherMediaWidth, LauncherHeight - 106, "YOUTUBE", True)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX) : ExecFile_Strict("https://www.youtube.com/channel/UCPqWOCPfKooDnrLNzA67Acw")
		EndIf
		DrawBlock(LauncherIMG[0], LauncherWidth - 400, LauncherHeight - 86, 2)
		; ~ Language selector
		If MouseOn(LauncherWidth - 185, LauncherHeight - 186, 40, 40) Then
			DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 1)
			Rect(LauncherWidth - 185, LauncherHeight - 186, 40, 40, False)
			Text2(LauncherWidth - 185 + 45, LauncherHeight - 166, GetLocalString("launcher", "language"), False, True)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX) : LanguageSelector()
		Else
			DrawImage(LauncherIMG[1], LauncherWidth - 185, LauncherHeight - 186, 0)
		EndIf
		; ~ Report button
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 105, 165, 30, GetLocalString("launcher", "report")) Then ExecFile_Strict("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition/news/bug-reports1")
		; ~ Changelog button
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 50, 165, 30, GetLocalString("launcher", "changelog")) Then ExecFile_Strict("Changelog.txt")
		; ~ Launch button
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 105, 100, 30, GetLocalString("launcher", "launch")) Then
			If opt\DisplayMode = 1 Then
				opt\GraphicWidth = DesktopWidth()
				opt\GraphicHeight = DesktopHeight()
			Else
				opt\GraphicWidth = lnchr\GFXModeWidths[lnchr\SelectedGFXMode]
				opt\GraphicHeight = lnchr\GFXModeHeights[lnchr\SelectedGFXMode]
			EndIf
			opt\RealGraphicWidth = opt\GraphicWidth
			opt\RealGraphicHeight = opt\GraphicHeight
			Exit
		EndIf
		; ~ Exit button
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 50, 100, 30, GetLocalString("launcher", "exit")) Then
			Quit = True
			Exit
		EndIf
		Flip()
	Forever
	
	IniWriteString(OptionFile, "Global", "Width", lnchr\GFXModeWidths[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Global", "Height", lnchr\GFXModeHeights[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	IniWriteString(OptionFile, "Global", "Display Mode", opt\DisplayMode)
	IniWriteString(OptionFile, "Global", "GFX Driver", opt\GFXDriver)
	
	For i = 0 To 1
		FreeImage(LauncherIMG[i]) : LauncherIMG[i] = 0
	Next
	
	If Quit Then End()
End Function

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
	Field FileSize$
	Field Compatible$
End Type

; ~ Language status constants
;[Block]
Const LANGUAGE_STATUS_NULL% = 0
Const LANGUAGE_STATUS_DOWNLOAD_REQUEST% = 1
Const LANGUAGE_STATUS_DOWNLOAD_START% = 2
Const LANGUAGE_STATUS_UNPACK_REQUEST% = 3
Const LANGUAGE_STATUS_UNPACK_START% = 4
Const LANGUAGE_STATUS_UNINSTALLING_REQUEST% = 5
Const LANGUAGE_STATUS_UNINSTALLING_START% = 6
Const LANGUAGE_STATUS_DONE% = 7
;[End Block]
Const LocalizaitonPath$ = "Localization\"

Global lang.Language = New Language

Function SetLanguage%(Language$)
	lang\CurrentLanguage = Language
	If lang\CurrentLanguage = "en" Then
		lang\LanguagePath = ""
	Else
		lang\LanguagePath = LocalizaitonPath + lang\CurrentLanguage + "\"
		IniWriteBuffer(lang\LanguagePath + LanguageFile)
		IniWriteBuffer(lang\LanguagePath + SubtitlesFile)
		IniWriteBuffer(lang\LanguagePath + AchievementsFile)
		IniWriteBuffer(lang\LanguagePath + LoadingScreensFile)
		IniWriteBuffer(lang\LanguagePath + SCP294File)
		IniWriteBuffer(lang\LanguagePath + FontsFile)
	EndIf
	If StringToBoolean(GetLocalString("global", "splitwithspace")) Then
		SplitSpace = " "
	Else
		SplitSpace = ""
	EndIf
	opt\Language = Language
	InitKeyNames()
End Function

Function LanguageSelector%()
	Local BasePath$ = GetEnv("AppData") + "\scpcb-ue\temp\"
	
	If FileType(BasePath) <> 2 Then CreateDir(BasePath) ; ~ Create temporary folder
	If FileType(LocalizaitonPath) <> 2 Then CreateDir(LocalizaitonPath)
	CreateDir(BasePath + "flags/")
	DownloadFile("https://files.ziyuesinicization.site/cbue/list.txt", BasePath + "temp.txt") ; ~ List of languages
	
	Local lan.ListLanguage
	Local File% = OpenFile_Strict(BasePath + "temp.txt")
	Local l$
	
	If File <> 0 Then
		While (Not Eof(File))
			l = ReadLine(File)
			If l <> "" Then
				lan.ListLanguage = New ListLanguage
				lan\Name = ParseDomainTXT(l, "name") ; ~ Name of localization
				lan\ID = ParseDomainTXT(l, "id") ; ~ Language ID of localization
				lan\Author = ParseDomainTXT(l, "author") ; ~ Author of translation
				lan\LastModify = ParseDomainTXT(l, "mod") ; ~ Last modify date
				lan\MajorOnly = Int(ParseDomainTXT(l, "majoronly")) ; ~ loca.ini only?
				lan\Full = Int(ParseDomainTXT(l, "full")) ; ~ Full complete translation
				lan\Flag = ParseDomainTXT(l, "flag") ; ~ Flag of country
				lan\FileSize = ParseDomainTXT(l, "size") ; ~ Size of localization
				lan\Compatible = ParseDomainTXT(l, "compatible") ; ~ Compatible version
				If FileType(BasePath + "flags/" + lan\Flag) <> 1 Then DownloadFile("https://files.ziyuesinicization.site/cbue/flags/" + lan\Flag, BasePath + "flags/" + lan\Flag) ; ~ Flags of languages
				If (Not lan\FlagImg) Then lan\FlagImg = LoadImage_Strict(BasePath + "flags\" + lan\Flag)
			Else
				Exit
			EndIf
		Wend
		CloseFile(File)
		DeleteFile(BasePath + "temp.txt")
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
		Select CurrentStatus
			Case LANGUAGE_STATUS_DOWNLOAD_START
				;[Block]
				If Not RequestLanguage\MajorOnly Then DownloadFile("https://files.ziyuesinicization.site/cbue/" + RequestLanguage\ID + ".zip", BasePath + "/local.zip")
				DownloadFile("https://weblate.ziyuesinicization.site/api/translations/scpcb-ue/local-ini/" + RequestLanguage\ID + "/file/", BasePath + "/local.ini")
				DownloadFile("https://weblate.ziyuesinicization.site/api/translations/scpcb-ue/achievements-ini/" + RequestLanguage\ID + "/file/", BasePath + "/achievements.ini")
				CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
				;[End Block]
			Case LANGUAGE_STATUS_UNPACK_START
				;[Block]
				; ~ Unzip function will delete everything in the directory, so we need move local.ini to directory after unziping
				CreateDir(LocalizaitonPath + RequestLanguage\ID)
				If Not RequestLanguage\MajorOnly Then Unzip(BasePath + "/local.zip", LocalizaitonPath + RequestLanguage\ID)
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
		If CurrentStatus = LANGUAGE_STATUS_DONE Then
			If (MilliSecs() - StatusTimer) > 1500 Then CurrentStatus = LANGUAGE_STATUS_NULL
		EndIf
		
		SetBuffer(BackBuffer())
		Cls()
		
		mo\MouseHit1 = MouseHit(1)
		mo\MouseDown1 = MouseDown(1)
		
		Local x#, y#, LinesAmount%
		
		Color(255, 255, 255)
		If (Not LanguageBG) Then LanguageBG = LoadImage_Strict("GFX\Menu\Language.png")
		DrawBlock(LanguageBG, 0, 0)
		Rect(LauncherWidth - 161, LauncherHeight - 285, 155, 110)
		
		If LinesAmount > 13 Then
			y = LauncherHeight - 280 - (20 * ScrollMenuHeight * ScrollBarY)
			SetBuffer(ImageBuffer(LanguageIMG))
			DrawImage(LanguageBG, -20, -195)
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 1)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 2, y - 195, 432, lan\FlagImg)
				If MouseOn(430, y - CurrFontHeight, 21, 21) Then
					DrawImage(ButtonImages, 410, y - 195 - CurrFontHeight, 5)
					If MouseOn(430, y - CurrFontHeight, 21, 21) Then
						Color(150, 150, 150)
						Rect(410, y - 195 - CurrFontHeight, 20, 20, False)
						MouseHoverLanguage = lan
					EndIf
				EndIf
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 1)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 432, 20) Then
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
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 430, 20) Then
					DrawImage(ButtonImages, LauncherWidth - 210, y - 4, 5)
					If MouseOn(LauncherWidth - 210, y - 4, 21, 21) Then
						Color(150, 150, 150)
						Rect(LauncherWidth - 210, y - 4, 20, 20, False)
						MouseHoverLanguage = lan
					EndIf
				EndIf
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(LauncherWidth - 620, y - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 1)
					Rect(LauncherWidth - 620, y - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(LauncherWidth - 620, y - CurrFontHeight, 432, 20) Then
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
		
		If CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_REQUEST Then
			InfoBoxContent = GetLocalString("language", "downloading")
			CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
			InfoBoxContent = GetLocalString("language", "unpacking")
			CurrentStatus = LANGUAGE_STATUS_UNPACK_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_REQUEST
			InfoBoxContent = GetLocalString("language", "uninstalling")
			CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_START
		ElseIf CurrentStatus = LANGUAGE_STATUS_DONE
			InfoBoxContent = GetLocalString("language", "done")
		EndIf
		
		Color(0, 0, 1)
		RowText(InfoBoxContent, LauncherWidth - 159, LauncherHeight - 281, 151, 102)
		
		If SelectedLanguage <> Null Then
			If SelectedLanguage\ID = opt\Language Then
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "contribute"), Font_Default, ButtonImages, 4) Then ExecFile_Strict("https://github.com/Jabka666/scpcb-ue-my/wiki/How-to-contribute-a-language")
			ElseIf SelectedLanguage\Name = "English"
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), Font_Default, ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					; ~ Reload some stuff manually
					FreeFont(fo\FontID[Font_Default])
					fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
					AppTitle(GetLocalString("language", "title"))
					FreeImage(LanguageBG) : LanguageBG = 0
				EndIf
			ElseIf FileType(LocalizaitonPath + SelectedLanguage\ID) = 2
				If SelectedLanguage\ID <> opt\Language Then
					If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 165, 155, 30, GetLocalString("language", "uninstall"), Font_Default, ButtonImages, 3) Then
						CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_REQUEST
						RequestLanguage = SelectedLanguage
					EndIf
					If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), Font_Default, ButtonImages, 2) Then
						SetLanguage(SelectedLanguage\ID)
						; ~ Reload some stuff manually
						FreeFont(fo\FontID[Font_Default])
						fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
						AppTitle(GetLocalString("language", "title"))
						FreeImage(LanguageBG) : LanguageBG = 0
					EndIf
				EndIf
			Else
				If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 115, 155, 30, GetLocalString("language", "download"), Font_Default, ButtonImages, 1) Then
					CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_REQUEST
					RequestLanguage = SelectedLanguage
				EndIf
			EndIf
		EndIf
		If UpdateLauncherButtonWithImage(LauncherWidth - 161, LauncherHeight - 65, 155, 30, GetLocalString("menu", "back"), Font_Default, ButtonImages) Then Exit
		
		If MouseHoverLanguage <> Null Then
			Local Name$ = Format(GetLocalString("language", "name"), MouseHoverLanguage\Name)
			Local ID$ = Format(GetLocalString("language", "id"), MouseHoverLanguage\ID)
			
			If MouseHoverLanguage\ID <> "en" Then
				Local Author$ = Format(GetLocalString("language", "author"), MouseHoverLanguage\Author)
				Local Prefect$ = Format(GetLocalString("language", "full"), GetLocalString("language", "yes")) ; ~ Get width only
				Local Prefect2$ = Format(GetLocalString("language", "full"), GetLocalString("language", "no"))
				Local Compatible$ = Format(GetLocalString("language", "compatible"), "v" + MouseHoverLanguage\Compatible)
				If Not MouseHoverLanguage\MajorOnly Then 
					Local Size$ = Format(GetLocalString("language", "size"), MouseHoverLanguage\FileSize)
					Local LastMod$ = Format(GetLocalString("language", "lastmod"), MouseHoverLanguage\LastModify)
					Local Height% = FontHeight() * 13
				Else
					Height% = FontHeight() * 10
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
			
			x = MouseX() + 10
			y = MouseY() + 10
			If (x + Width + FontWidth()) > LauncherWidth Then x = x - Width - 10 ; ~ If tooltip is too long then move tooltip to the left
			RenderFrame(x, y, Width + FontWidth(), Height)
			x = x + 5
			Text2(x, y + 8, Name)
			Text2(x, y + 23, ID)
			If MouseHoverLanguage\ID <> "en" Then
				Text2(x, y + 38, Author)
				If MouseHoverLanguage\Full Then
					DualColorText(x, y + 53, Format(GetLocalString("language", "full"), ""), GetLocalString("language", "yes"), 255, 255, 255, 0, 200, 0)
				Else
					DualColorText(x, y + 53, Format(GetLocalString("language", "full"), ""), GetLocalString("language", "no"), 255, 255, 255, 200, 0, 0)
				EndIf
				If MouseHoverLanguage\Compatible = VersionNumber Then
					DualColorText(x, y + 68, Format(GetLocalString("language", "compatible"), ""), "v" + MouseHoverLanguage\Compatible, 255, 255, 255, 0, 200, 0)
				Else
					DualColorText(x, y + 68, Format(GetLocalString("language", "compatible"), ""), "v" + MouseHoverLanguage\Compatible, 255, 255, 255, 200, 0, 0)
				EndIf
				If Not MouseHoverLanguage\MajorOnly Then
					Text2(x, y + 83, LastMod)
					Text2(x, y + 98, Size) ; ~ local.ini only -> unable to get the file size
				EndIf
			EndIf
			If mo\MouseHit1 Then ExecFile("https://github.com/Jabka666/scpcb-ue-my/wiki/Language-List-of-Ultimate-Edition")
		EndIf
		MouseHoverLanguage = Null
		
		Flip()
	Forever
	
	mo\MouseHit1 = False
	mo\MouseDown1 = False
	ScrollBarY = 0.0
	ScrollMenuHeight = 0.0
	
	Delete Each ListLanguage
	
	FreeImage(LanguageIMG) : LanguageIMG = 0
	FreeImage(LanguageBG) : LanguageBG = 0
	
	DeleteFolder(BasePath) ; ~ Delete temporary folder
	
	AppTitle(GetLocalString("launcher", "title"))
	FreeImage(LauncherBG) : LauncherBG = 0
	
	IniWriteString(OptionFile, "Global", "Language", opt\Language)
End Function

Function UpdateLauncherButton%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, WaitForMouseUp% = False, Locked% = False, R% = 255, G% = 255, B% = 255)
	Local Clicked% = False
	
	RenderFrame(x, y, Width, Height, 0, 0, Locked)
	If MouseOn(x, y, Width, Height) Then
		Color(30, 30, 30)
		If (mo\MouseHit1 And (Not WaitForMouseUp)) Lor (mo\MouseUp1 And WaitForMouseUp) Then
			If Locked Then
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
	
	If Locked Then
		If R <> 255 Lor G <> 255 Lor B <> 255 Then
			Color(R, G, B)
		Else
			Color(100, 100, 100)
		EndIf
	Else
		Color(R, G, B)
	EndIf
	SetFont2(fo\FontID[FontID])
	Text2(x + (Width / 2), y + (Height / 2), Txt, True, True)
	Return(Clicked)
End Function

Function UpdateLauncherDownloadButton%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
	Local Pushed% = False
	
	Color(50, 50, 50)
	If (Not Disabled) Then
		If MouseOn(x, y, Width, Height)
			If mo\MouseDown1 Then
				Pushed = True
				Color(30, 30, 30)
			Else
				Color(100, 100, 100)
			EndIf
		EndIf
	EndIf
	
	If Pushed Then
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
	Text2(x + (Width / 2), y + (Height / 2) - 1, Txt, True, True)
	
	Color(0, 0, 0)
	
	If Pushed And mo\MouseHit1 Then
		PlaySound_Strict(ButtonSFX)
		Return(True)
	EndIf
End Function

Function UpdateLauncherButtonWithImage%(x%, y%, Width%, Height%, Txt$, FontID% = Font_Default, Img% = 0, Frame% = 0)
	Txt = String(" ", ImageWidth(Img) / 8) + Txt
	
	Local Result% = UpdateLauncherButton(x, y, Width, Height, Txt, FontID)
	
	DrawImage(Img, x + (Width / 2) - (StringWidth(Txt) / 2) - 3, y + (Height / 2) - ImageHeight(Img) / 2, Frame) ; ~ No DrawBlock please
	Return(Result)
End Function

Function UpdateLauncherTick%(x%, y%, Selected%, Locked% = False)
	Local Width% = 20, Height% = 20
	Local Highlight% = MouseOn(x, y, Width, Height)
	Local IMG%
	
	If Locked Then
		IMG = MenuGray
	Else
		IMG = MenuWhite
	EndIf
	
	Color(255, 255, 255)
	RenderTiledImageRect(IMG, (x Mod 256), (y Mod 256), 512, 512, x, y, Width, Height)
	
	If Highlight Then
		If Locked Then
			Color(0, 0, 0)
			If mo\MouseHit1 Then PlaySound_Strict(ButtonSFX2)
		Else
			Color(50, 50, 50)
			If mo\MouseHit1 Then Selected = (Not Selected) : PlaySound_Strict(ButtonSFX)
		EndIf
	Else
		Color(0, 0, 0)
	EndIf
	
	Rect(x + 2, y + 2, Width - 4, Height - 4)
	
	If Selected Then
		If Highlight Then
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
	
	If (Not Vertical) Then ; ~ Horizontal
		If Height > 10 Then
			Color(255, 255, 255)
			Rect(BarX + (BarWidth / 2), BarY + 5, 2, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) - 3, BarY + 5, 2, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) + 3, BarY + 5, 2, BarHeight - 10)
		EndIf
	Else ; ~ Vertical
		If Width > 10 Then
			Color(255, 255, 255)
			Rect(BarX + 4, BarY + (BarHeight / 2), BarWidth - 10, 2)
			Rect(BarX + 4, BarY + (BarHeight / 2) - 3, BarWidth - 10, 2)
			Rect(BarX + 4, BarY + (BarHeight / 2) + 3, BarWidth - 10, 2)
		EndIf
	EndIf
	
	OnScrollBar = (mo\MouseDown1 And MouseOn(BarX, BarY, BarWidth, BarHeight))
	If OnScrollBar Then
		If (Not Vertical) Then
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
	If UnFitting <= 0 Then
		Text2(x, y, Txt, 0, 0)
	Else
		LetterWidth = TextLength / Len(Txt)
		Text2(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
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
	Text2(x, y, Txt1)
	Color(ColorR2, ColorG2, ColorB2)
	Text2(x + StringWidth(Txt1), y, Txt2)
	Color(OldR, OldG, OldB)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS