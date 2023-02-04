Const LauncherWidth% = 640
Const LauncherHeight% = 480

Global LauncherBG%

Function UpdateLauncher%(lnchr.Launcher)
	Local i%, n%
	
	MenuScale = 1
	
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
		Local y% = LauncherHeight - 275
		
		For i = 0 To lnchr\GFXModes - 1
			Color(0, 0, 0)
			If lnchr\SelectedGFXMode = i Then Rect(x - 1, y - 5, 100, 20, False)
			
			Text2(x, y, (lnchr\GFXModeWidths[i] + "x" + lnchr\GFXModeHeights[i]))
			If MouseOn(x - 1, y - 5, 100, 20) Then
				Color(100, 100, 100)
				Rect(x - 1, y - 5, 100, 20, False)
				If mo\MouseHit1 Then lnchr\SelectedGFXMode = i
			EndIf
			
			y = y + 20
			If y >= LauncherHeight - 155 Then
				y = LauncherHeight - 275
				x = x + 100
			EndIf
		Next
		; ~ Display selector
		Color(255, 255, 255)
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
		RenderFrame(LauncherWidth - 185, LauncherHeight - 226, 145, 30)
		Text2(LauncherWidth - 112.5, LauncherHeight - 216, Txt, True)
		If UpdateLauncherButton(LauncherWidth - 40, LauncherHeight - 226, 30, 30, ">", False) Then opt\DisplayMode = ((opt\DisplayMode + 1) Mod 3)
		; ~ Launcher tick
		Text2(LauncherWidth - 155, LauncherHeight - 275, GetLocalString("launcher", "launcher"))
		opt\LauncherEnabled = UpdateLauncherTick(LauncherWidth - 185, LauncherHeight - 278, opt\LauncherEnabled)
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
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 105, 165, 30, GetLocalString("launcher", "report"), False, False) Then ExecFile_Strict("https://www.moddb.com/mods/scp-containment-breach-ultimate-edition/news/bug-reports1")
		; ~ Changelog button
		If UpdateLauncherButton(LauncherWidth - 300, LauncherHeight - 50, 165, 30, GetLocalString("launcher", "changelog"), False, False) Then ExecFile_Strict("Changelog.txt")
		; ~ Launch button
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 105, 100, 30, GetLocalString("launcher", "launch"), False, False) Then
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
		If UpdateLauncherButton(LauncherWidth - 120, LauncherHeight - 50, 100, 30, GetLocalString("launcher", "exit"), False, False) Then
			Quit = True
			Exit
		EndIf
		Flip()
	Forever
	
	IniWriteString(OptionFile, "Global", "Width", lnchr\GFXModeWidths[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Global", "Height", lnchr\GFXModeHeights[lnchr\SelectedGFXMode])
	IniWriteString(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	IniWriteString(OptionFile, "Global", "Display Mode", opt\DisplayMode)
	IniWriteString(OptionFile, "Global", "Language", opt\Language)
	
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
	Field WeblateID$
	Field Author$
	Field LastModify$
	Field Flag$
	Field FlagImg%
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
	If lang\CurrentLanguage = "en-US" Then
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
				lan\WeblateID = ParseDomainTXT(l, "weblate") ; ~ Language ID in ZiYue Weblate
				lan\Author = ParseDomainTXT(l, "author") ; ~ Author of translation
				lan\LastModify = ParseDomainTXT(l, "mod") ; ~ Last modify date
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
				DownloadFile("https://files.ziyuesinicization.site/cbue/" + RequestLanguage\ID + ".zip", BasePath + "/local.zip")
				DownloadFile("http://weblate.ziyuesinicization.site/download/scpcb-ue/local-ini/" + RequestLanguage\WeblateID + "/", LocalizaitonPath + RequestLanguage\ID + "/Data/local.ini") ; ~ Download local.ini from ZiYue Weblate
				CurrentStatus = LANGUAGE_STATUS_UNPACK_REQUEST
				;[End Block]
			Case LANGUAGE_STATUS_UNPACK_START
				;[Block]
				CreateDir(LocalizaitonPath + RequestLanguage\ID)
				Unzip(BasePath + "/local.zip", LocalizaitonPath + RequestLanguage\ID)
				StatusTimer = MilliSecs2()
				CurrentStatus = LANGUAGE_STATUS_DONE
				;[End Block]
			Case LANGUAGE_STATUS_UNINSTALLING_START
				;[Block]
				DeleteFolder(LocalizaitonPath + SelectedLanguage\ID)
				StatusTimer = MilliSecs2()
				CurrentStatus = LANGUAGE_STATUS_DONE
				;[End Block]
		End Select
		If CurrentStatus = LANGUAGE_STATUS_DONE Then
			If (MilliSecs2() - StatusTimer) > 1500 Then CurrentStatus = LANGUAGE_STATUS_NULL
		EndIf
		
		SetBuffer(BackBuffer())
		Cls()
		
		mo\MouseHit1 = MouseHit(1)
		mo\MouseDown1 = MouseDown(1)
		
		Local x#, y#, LinesAmount%
		
		Color(255, 255, 255)
		If (Not LanguageBG) Then LanguageBG = LoadImage_Strict("GFX\Menu\Language.png")
		DrawBlock(LanguageBG, 0, 0)
		Rect(479, 195, 155, 110)
		
		If LinesAmount > 13 Then
			y = 200 - (20 * ScrollMenuHeight * ScrollBarY)
			SetBuffer(ImageBuffer(LanguageIMG))
			DrawImage(LanguageBG, -20, -195)
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 0)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 2, y - 195, 432, lan\FlagImg)
				If MouseOn(20, y - CurrFontHeight, 430, 20) Then
					DrawImage(ButtonImages, 405, y - 199, 5)
					If MouseOn(425, y - 4, 21, 21) Then MouseHoverLanguage = lan
				EndIf
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 0)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(20, y - CurrFontHeight, 432, 20) Then
					Color(150, 150, 150)
					Rect(0, y - 195 - CurrFontHeight, 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y = y + 20
				LinesAmount = LinesAmount + 1
			Next
			SetBuffer(BackBuffer())
			DrawBlock(LanguageIMG, 20, 195)
			Color(10, 10, 10)
			Rect(452, 195, 20, 254, True)
			ScrollMenuHeight = LinesAmount - 12
			ScrollBarY = UpdateLauncherScrollBar(452, 195, 20, 254, 452, 195 + (254 - (254 - (4 * ScrollMenuHeight))) * ScrollBarY, 20, 254 - (4 * ScrollMenuHeight), ScrollBarY, 1)
		Else
			y = 200
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 0)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 21, y, 432, lan\FlagImg)
				If MouseOn(20, y - CurrFontHeight, 430, 20) Then
					DrawImage(ButtonImages, 425, y - 4, 5)
					If MouseOn(425, y - 4, 21, 21) Then MouseHoverLanguage = lan
				EndIf
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(20, y - CurrFontHeight, 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 0)
					Rect(20, y - CurrFontHeight, 430, 20, False)
				EndIf
				If MouseOn(20, y - CurrFontHeight, 432, 20) Then
					Color(150, 150, 150)
					Rect(20, y - CurrFontHeight, 430, 20, False)
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
		
		Color(0, 0, 0)
		RowText(InfoBoxContent, 481, 199, 151, 102)
		
		If SelectedLanguage <> Null Then
			If SelectedLanguage\ID = opt\Language Then
				; ~ Just save this line, okay?
			ElseIf SelectedLanguage\Name = "English"
				If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
					AppTitle(GetLocalString("language", "title"))
					FreeImage(LanguageBG) : LanguageBG = 0
				EndIf
			ElseIf FileType(LocalizaitonPath + SelectedLanguage\ID) = 2
				If SelectedLanguage\ID <> opt\Language Then
					If UpdateLauncherButtonWithImage(479, LauncherHeight - 165, 155, 30, GetLocalString("language", "uninstall"), ButtonImages, 3) Then
						CurrentStatus = LANGUAGE_STATUS_UNINSTALLING_REQUEST
						RequestLanguage = SelectedLanguage
					EndIf
					If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 155, 30, GetLocalString("language", "set"), ButtonImages, 2) Then
						SetLanguage(SelectedLanguage\ID)
						fo\FontID[Font_Default] = LoadFont_Strict(FontsPath + GetFileLocalString(FontsFile, "Default", "File"), GetFileLocalString(FontsFile, "Default", "Size"), True)
						AppTitle(GetLocalString("language", "title"))
						FreeImage(LanguageBG) : LanguageBG = 0
					EndIf
				EndIf
			Else
				If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 155, 30, GetLocalString("language", "download"), ButtonImages, 1) Then
					CurrentStatus = LANGUAGE_STATUS_DOWNLOAD_REQUEST
					RequestLanguage = SelectedLanguage
				EndIf
			EndIf
		Else
			If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 155, 30, GetLocalString("language", "contribute"), ButtonImages, 4) Then ExecFile_Strict("https://github.com/Jabka666/scpcb-ue-my/wiki/How-to-contribute-a-language")
		EndIf
		If UpdateLauncherButtonWithImage(479, LauncherHeight - 65, 155, 30, GetLocalString("menu", "back"), ButtonImages) Then Exit
		
		If MouseHoverLanguage <> Null Then
			Local Name$ = Format(GetLocalString("language", "name"), MouseHoverLanguage\Name)
			Local ID$ = Format(GetLocalString("language", "id"), MouseHoverLanguage\ID)
			
			If MouseHoverLanguage\ID <> "en-US" Then
				Local Author$ = Format(GetLocalString("language", "author"), MouseHoverLanguage\Author)
				Local Prefect$ = Format(GetLocalString("language", "full"), GetLocalString("language", "yes")) ; ~ Get width only
				Local Prefect2$ = Format(GetLocalString("language", "full"), GetLocalString("language", "no"))
				Local Compatible$ = Format(GetLocalString("language", "compatible"), "v" + MouseHoverLanguage\Compatible)
				Local Size$ = Format(GetLocalString("language", "size"), MouseHoverLanguage\FileSize)
				Local Height% = FontHeight() * 11
			Else
				Author = ""
				Prefect = ""
				Compatible = ""
				Prefect2 = ""
				Size = ""
				Height = FontHeight() * 4.5
			EndIf
			
			Local Width% = Max(Max(Max(Max(Max(Max(StringWidth(Name), StringWidth(ID)), StringWidth(Author)), StringWidth(Prefect)), StringWidth(Size)), StringWidth(Compatible)), StringWidth(Prefect2))
			
			x = MouseX() + 10
			y = MouseY() + 10
			If (x + Width + FontWidth()) > LauncherWidth Then x = x - Width - 10 ; ~ If tooltip is too long then move tooltip to the left
			RenderFrame(x, y, Width + FontWidth(), Height)
			x = x + 5
			Text2(x, y + 8, Name)
			Text2(x, y + 23, ID)
			If MouseHoverLanguage\ID <> "en-US" Then
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
				Text2(x, y + 83, Size)
			EndIf
			If mo\MouseHit1 Then ExecFile("https://wiki.ziyuesinicization.site/index.php?title=How_to_contribute_a_language/Language_List")
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
End Function

Global OnScrollBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function UpdateLauncherScrollBar#(x%, y%, Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Bar#, Vertical% = False)
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
	
	If MouseX() > BarX And MouseX() < BarX + BarWidth
		If MouseY() > BarY And MouseY() < BarY + BarHeight
			OnScrollBar = True
		Else
			If (Not mo\MouseDown1) Then OnScrollBar = False
		EndIf
	Else
		If (Not mo\MouseDown1) Then OnScrollBar = False
	EndIf
	
	If mo\MouseDown1 And OnScrollBar Then
		If (Not Vertical) Then
			Return(Min(Max(Bar + MouseSpeedX / Float(Width - BarWidth), 0.0), 1.0))
		Else
			Return(Min(Max(Bar + MouseSpeedY / Float(Height - BarHeight), 0.0), 1.0))
		EndIf
	EndIf
	
	Local MouseSpeedZ# = MouseZSpeed()
	
	; ~ Only for vertical scroll bars
	If MouseSpeedZ <> 0.0 Then Return(Min(Max(Bar - (MouseSpeedZ * 3.0) / Float(Height - BarHeight), 0.0), 1.0))
	
	Return(Bar)
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

Function UpdateLauncherButtonWithImage%(x%, y%, Width%, Height%, Txt$, Img%, Frame% = 0)
	Txt = String(" ", ImageWidth(Img) / 8) + Txt
	
	Local Result% = UpdateLauncherButton(x, y, Width, Height, Txt, False, False)
	
	DrawImage(Img, x + (Width / 2) - (StringWidth(Txt) / 2) - 3, y + (Height / 2) - ImageHeight(Img) / 2, Frame) ; ~ No DrawBlock please
	Return(Result)
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
;~C#Blitz3D