Type ListLanguage
	Field Name$
	Field ID$
	Field Author$
	Field LastModify$
	Field Flag$
	Field Full%
End Type

Function LanguageSelector%()
	Local BasePath$ = GetEnv("AppData") + "\scpcb-ue\temp\"
	
	If FileType(BasePath) <> 2 Then CreateDir(BasePath) ; ~ Create temporary folder
	If FileType("Localization\") <> 2 Then CreateDir("Localization\")
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
				lan\Name = ParseDomainTXT(l, "name") ; Name of localization
				lan\ID = ParseDomainTXT(l, "id") ; ~ Language ID of localization
				lan\Author = ParseDomainTXT(l, "author") ; ~ Author of translation
				lan\LastModify = ParseDomainTXT(l, "mod") ; ~ Last modify date
				lan\Flag = ParseDomainTXT(l, "flag") ; ~ Flag of country
				lan\Full = Int(ParseDomainTXT(l, "full")) ; ~ Full complete translation
				DownloadFile("https://files.ziyuesinicization.site/cbue/flags/" + lan\Flag, BasePath + "flags/" + lan\Flag) ; ~ Flags of languages
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
	
	Local LanguageBG% = LoadImage_Strict("GFX\Menu\Language.png")
	Local LanguageIMG% = CreateImage(452, 254)
	Local ButtonImages% = LoadAnimImage_Strict("GFX\menu\buttons.png", 21, 21, 0, 5)
	Local SelectedLanguage.ListLanguage = Null
	
	AppTitle(GetLocalString("language", "title"))
	
	Repeat
		SetBuffer(BackBuffer())
		Cls()
		
		mo\MouseHit1 = MouseHit(1)
		mo\MouseDown1 = MouseDown(1)
		
		Local x#, y#, LinesAmount%
		
		Color(255, 255, 255)
		DrawImage(LanguageBG, 0, 0)
		Rect(479, 195, 140, 100)
		
		If LinesAmount > 13 Then
			y = 200 - (20 * ScrollMenuHeight * ScrollBarY)
			SetBuffer(ImageBuffer(LanguageIMG))
			Cls()
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(1, 0, 0)
				If lan\Full Then
					LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 2, y - 195, 432, LoadImage(BasePath + "flags\" + lan\Flag))
				Else
					LimitTextWithImage(lan\Name + "(" + lan\ID + ") - " + GetLocalString("language", "unfull"), 2, y - 195, 432, LoadImage(BasePath + "flags\" + lan\Flag))
				EndIf
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(0, y - 195 - (FontHeight() / 2), 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 0)
					Rect(0, y - 195 - (FontHeight() / 2), 430, 20, False)
				EndIf
				If MouseOn(20, y - (FontHeight() / 2), 432, 20) Then
					Color(150, 150, 150)
					Rect(0, y - 195 - (FontHeight() / 2), 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y = y + 20
				LinesAmount = LinesAmount + 1
			Next
			SetBuffer(BackBuffer())
			DrawImage(LanguageIMG, 20, 195)
			Color(10, 10, 10)
			Rect(452, 195, 20, 254, True)
			ScrollMenuHeight = LinesAmount - 12
			ScrollBarY = UpdateLauncherScrollBar(452, 195, 20, 254, 452, 195 + (254 - (254 - (4 * ScrollMenuHeight))) * ScrollBarY, 20, 254 - (4 * ScrollMenuHeight), ScrollBarY, 1)
		Else
			y = 200
			LinesAmount = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 0)
				If lan\Full Then
					LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 21, y, 432, LoadImage(BasePath + "flags\" + lan\Flag))
				Else
					LimitTextWithImage(lan\Name + "(" + lan\ID + ") - " + GetLocalString("language", "unfull"), 21, y, 432, LoadImage(BasePath + "flags\" + lan\Flag))
				EndIf
				If lan\ID = opt\Language Then 
					Color(200, 0, 0)
					Rect(20, y - (FontHeight() / 2), 430, 20, False)
				EndIf
				If SelectedLanguage <> Null And lan = SelectedLanguage Then
					Color(0, 0, 0)
					Rect(20, y - (FontHeight() / 2), 430, 20, False)
				EndIf
				If MouseOn(20, y - (FontHeight() / 2), 432, 20) Then
					Color(150, 150, 150)
					Rect(20, y - (FontHeight() / 2), 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y = y + 20
				LinesAmount = LinesAmount + 1
			Next
			ScrollMenuHeight = LinesAmount
		EndIf
		
		If SelectedLanguage <> Null Then
			Color(0, 0, 0)
			RowText(Format(Format(GetLocalString("language", "author"), SelectedLanguage\Author, "{0}"), SelectedLanguage\LastModify, "{1}"), 481, 197, 140, 100)
			If SelectedLanguage\ID = opt\Language Then
				; ~ Just save this line, okay?
			ElseIf SelectedLanguage\Name = "English"
				If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
					AppTitle(GetLocalString("language", "title"))
				EndIf
			ElseIf FileType("Localization\" + SelectedLanguage\ID) = 2
				If SelectedLanguage\ID <> opt\Language Then
					If UpdateLauncherButtonWithImage(479, LauncherHeight - 165, 140, 30, GetLocalString("language", "uninstall"), ButtonImages, 3) Then
						DeleteFolder("Localization\" + SelectedLanguage\ID)
					EndIf
					If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
						SetLanguage(SelectedLanguage\ID)
						fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
						AppTitle(GetLocalString("language", "title"))
					EndIf
				EndIf
			Else
				If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 140, 30, GetLocalString("language", "download"), ButtonImages, 1) Then
					DownloadFile("https://files.ziyuesinicization.site/cbue/" + SelectedLanguage\ID + ".zip", BasePath + "/local.zip")
					CreateDir("Localization\" + SelectedLanguage\ID)
					Unzip(BasePath + "/local.zip", "Localization/" + SelectedLanguage\ID)
				EndIf
			EndIf
		Else
			If UpdateLauncherButtonWithImage(479, LauncherHeight - 115, 140, 30, GetLocalString("language", "contribute"), ButtonImages, 4) Then 
				ExecFile("https://gist.github.com/ZiYueCommentary/97424394a0daf69d3a1220253b0a1cbb#file-ue-contribute-md")
			EndIf
		EndIf
		
		If UpdateLauncherButtonWithImage(479, LauncherHeight - 65, 140, 30, GetLocalString("menu", "back"), ButtonImages) Then 
			Exit
		EndIf
		
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
	
	If MouseSpeedZ <> 0.0 Then ; ~ Only for vertical scroll bars
		Return(Min(Max(Bar - (MouseSpeedZ * 3.0) / Float(Height - BarHeight), 0.0), 1.0))
    EndIf
	
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
		Text(x, y, Txt, 0, 0)
	Else
		LetterWidth = TextLength / Len(Txt)
		Text(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
	EndIf
End Function

Function UpdateLauncherDownloadButton%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
	Local Pushed% = False
	
	Color(50, 50, 50)
	If (Not Disabled) Then 
		If MouseX() > x And MouseX() < x + Width Then
			If MouseY() > y And MouseY() < y + Height Then
				If MouseDown(1) Then
					Pushed = True
					Color(30, 30, 30)
				Else
					Color(100, 100, 100)
				EndIf
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
	Text(x + (Width / 2), y + (Height / 2) - 1, Txt, True, True)
	
	Color(0, 0, 0)
	
	If Pushed And mo\MouseHit1 Then
		PlaySound_Strict(ButtonSFX)
		Return(True)
	EndIf
End Function

Function UpdateLauncherButtonWithImage%(x%, y%, Width%, Height%, Txt$, Img%, Frame% = 0)
	Txt = String(" ", ImageWidth(Img) / 8) + Txt
	
	Local Result% = UpdateLauncherButton(x, y, Width, Height, Txt, False, False)
	
	DrawImage(Img, x + (Width / 2) - (StringWidth(Txt) / 2) - 3, y + (Height / 2) - ImageHeight(Img) / 2, Frame)
	Return(Result)
End Function

Function LimitTextWithImage%(Txt$, x%, y%, Width%, Img%, Frame% = 0)
	DrawImage(Img, x, y + (StringHeight(Txt) / 2) - (ImageHeight(Img) / 2) - 1, Frame)
	LimitText(Txt, x + 3 + ImageWidth(Img), y, Width - ImageWidth(Img) - 3)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D