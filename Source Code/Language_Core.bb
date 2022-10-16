Type ListLanguage
	Field Name$
	Field ID$
	Field Author$
	Field LastModify$
	Field Flag$
End Type

Function LanguageSelector%()
	Local BasePath$ = GetEnv("AppData") + "\scpcb-ue\temp\"
	
	If FileType(BasePath) <> 2 Then CreateDir(BasePath) ; ~ Create temporary folder
	If FileType("Localization\") <> 2 Then CreateDir("Localization\")
	CreateDir(BasePath + "flags/")
	DownloadFile("https://files.ziyuesinicization.site/cbue/list.txt", BasePath + "temp.txt") ; ~ List of languages
	
	Local lan.ListLanguage
	Local File% = OpenFile(BasePath + "temp.txt")
	Local l$
	
	If File <> 0 Then
		While Not Eof(File)
			l = ReadLine(File)
			If l <> "" Then
				lan.ListLanguage = New ListLanguage
				lan\Name = ParseDomainTXT(l, "name")
				lan\ID = ParseDomainTXT(l, "id")
				lan\Author = ParseDomainTXT(l, "author")
				lan\LastModify = ParseDomainTXT(l, "mod")
				lan\Flag = ParseDomainTXT(l, "flag")
				DownloadFile("https://files.ziyuesinicization.site/cbue/flags/" + lan\Flag, BasePath + "flags/" + lan\Flag) ; ~ Flags of languages
			Else
				Exit
			EndIf
		Wend
		CloseFile(File)
		DeleteFile(BasePath + "temp.txt")
	EndIf
	
	AppTitle(GetLocalString("language", "title"))
	SetBuffer(BackBuffer())
	Cls()
	Flip(True)
	
	Local LanguageBG% = LoadImage_Strict("GFX\Menu\Language.png")
	Local LanguageIMG% = CreateImage(452, 254)
	Local ButtonImages% = LoadAnimImage_Strict("GFX\menu\buttons.png", 21, 21, 0, 5)
	Local SelectedLanguage.ListLanguage = Null
	
	Repeat
		mo\MouseHit1 = MouseHit(1)
		
		Local x#, y#, LinesAmount%
		
		SetBuffer(BackBuffer())
		Cls()
		Color(255, 255, 255)
		DrawImage(LanguageBG, 0, 0)
		Rect(479, 195, 140, 100)
		Color(0, 0, 0)
		
		If LinesAmount > 13 Then
			y = 200 - (20 * ScrollMenuHeight * ScrollBarY)
			LinesAmount% = 0
			SetBuffer(ImageBuffer(LanguageIMG))
			DrawImage(LanguageBG, -20, -195)
			For lan.ListLanguage = Each ListLanguage
				Color(1, 0, 0)
				LimitTextWithImage(lan\Name + "(" + lan\ID + ")", 2, y# - 195, 432, LoadImage(BasePath + "flags\" + lan\Flag))
				If lan\ID = opt\Language Then
					Color(200, 0, 0)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
				EndIf
				If (SelectedLanguage <> Null) And (lan = SelectedLanguage) Then
					Color(1, 0, 0)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
				EndIf
				If MouseOn(20, y - FontHeight() / 2, 432, 20) Then
					Color(150, 150, 150)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
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
			ScrollBarY = RenderDownloadScrollBar(452, 195, 20, 254, 452, 195 + (254 - (254 - 4 * ScrollMenuHeight)) * ScrollBarY, 20, 254 - (4 * ScrollMenuHeight), ScrollBarY, 1)
		Else
			Color(0, 0, 0)
			y# = 201
			LinesAmount% = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 0)
				LimitTextWithImage(lan\Name$ + "(" + lan\ID$ + ")", 21, y#, 432, LoadImage(BasePath + "flags\"+lan\Flag$))
				If lan\ID$ = opt\Language Then 
					Color(200, 0, 0)
					Rect(20, y - FontHeight() / 2, 430, 20, False)
				EndIf
				If (SelectedLanguage <> Null) And (lan = SelectedLanguage) Then
					Color(0, 0, 0)
					Rect(20, y - FontHeight() / 2, 430, 20, False)
				EndIf
				If MouseOn(20, y - FontHeight() / 2, 432, 20) Then
					Color(150, 150, 150)
					Rect(20, y - FontHeight() / 2, 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y# = y# + 20
				LinesAmount = LinesAmount + 1
			Next
			ScrollMenuHeight# = LinesAmount
		EndIf
		
		If SelectedLanguage <> Null Then
			Color(0, 0, 0)
			RowText(Format(Format(GetLocalString("language", "author"), SelectedLanguage\Author, "{0}"), SelectedLanguage\LastModify, "{1}"), 481, 197, 140, 100)
			If SelectedLanguage\ID = opt\Language Then
				; ~ Do nothing
			ElseIf (SelectedLanguage\Name = "English") Then
				If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
					AppTitle(GetLocalString("language", "title"))
				EndIf
			ElseIf (FileType("Localization\" + SelectedLanguage\ID) = 2) Then
				If SelectedLanguage\ID <> opt\Language Then
					If ButtonWithImage(479, LauncherHeight - 65 - 50 - 50, 140, 30, GetLocalString("language", "uninstall"), ButtonImages, 3) Then
						DeleteFolder("Localization\" + SelectedLanguage\ID)
					EndIf
					If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
						SetLanguage(SelectedLanguage\ID)
						fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
						AppTitle(GetLocalString("language", "title"))
					EndIf
				EndIf
			Else
				If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "download"), ButtonImages, 1) Then
					DownloadFile("https://files.ziyuesinicization.site/cbue/" + SelectedLanguage\ID + ".zip", BasePath + "/local.zip")
					CreateDir("Localization\" + SelectedLanguage\ID)
					Unzip(BasePath + "/local.zip", "Localization/" + SelectedLanguage\ID)
				EndIf
			EndIf
		Else
			If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "contribute"), ButtonImages, 4) Then 
				ExecFile("https://gist.github.com/ZiYueCommentary/97424394a0daf69d3a1220253b0a1cbb#file-ue-contribute-md")
			EndIf
		EndIf
		
		If ButtonWithImage(479, LauncherHeight - 65, 140, 30, GetLocalString("menu", "back"), ButtonImages, 0) Then 
			Exit
		EndIf
		
		Flip(True)
		Delay(8)
	Forever
	
	mo\MouseHit1 = False
	Delete Each ListLanguage
	If LanguageIMG <> 0 Then FreeImage LanguageIMG
	DeleteFolder(BasePath) ; ~ Delete temporary folder
	AppTitle GetLocalString("launcher", "title")
End Function

; ~ Re-added
Global OnScrollBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function RenderDownloadScrollBar#(x%, y%, Width%, Height%, BarX%, BarY%, BarWidth%, BarHeight%, Bar#, Dir% = False)
	Local MouseSpeedX# = MouseXSpeed()
	Local MouseSpeedY# = MouseYSpeed()
	
	Color(0, 0, 0)
	RenderDownloadButton(BarX, BarY, BarWidth, BarHeight, "")
	
	If Dir = 0 Then ; ~ Horizontal
		If Height > 10 Then
			Color 250,250,250
			Rect(BarX + (BarWidth / 2), BarY + (5 * MenuScale), 2 * MenuScale, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) - (3 * MenuScale), BarY + 5 * MenuScale, 2 * MenuScale, BarHeight - 10)
			Rect(BarX + (BarWidth / 2) + (3 * MenuScale), BarY + 5 * MenuScale, 2 * MenuScale, BarHeight - 10)
		EndIf
	Else ; ~ Vertical
		If Width > 10 Then
			Color(250, 250, 250)
			Rect(BarX + (4 * MenuScale), BarY + (BarHeight / 2), BarWidth - (10 * MenuScale), 2 * MenuScale)
			Rect(BarX + (4 * MenuScale), BarY + (BarHeight / 2) - (3 * MenuScale), BarWidth - (10 * MenuScale), 2 * MenuScale)
			Rect(BarX + (4 * MenuScale), BarY + (BarHeight / 2) + (3 * MenuScale), BarWidth - (10 * MenuScale), 2 * MenuScale)
		EndIf
	EndIf
	
	If MouseX() > BarX And MouseX() < BarX + BarWidth
		If MouseY() > BarY And MouseY() < BarY + BarHeight
			OnScrollBar = True
		Else
			If (Not MouseDown(1)) Then OnScrollBar = False
		EndIf
	Else
		If (Not MouseDown(1)) Then OnScrollBar = False
	EndIf
	
	If MouseDown(1) Then
		If OnScrollBar Then
			If Dir Then
				Return(Min(Max(Bar + MouseSpeedX / Float(Width - BarWidth), 0.0), 1.0))
			Else
				Return(Min(Max(Bar + MouseSpeedY / Float(Height - BarHeight), 0.0), 1.0))
			EndIf
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
	
	If Txt = "" Or Width = 0 Then Return 0
	TextLength = StringWidth(Txt)
	UnFitting = TextLength - Width
	If UnFitting <= 0 Then
		Text(x, y, Txt, 0, 0)
	Else
		LetterWidth = TextLength / Len(Txt)
		Text(x, y, Left(Txt, Max(Len(Txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
	End If
End Function

Function RenderDownloadButton%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
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
		Line(x, y + Height - 1,x + Width - 1, y + Height - 1)
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

Function ButtonWithImage%(x%, y%, Width%, Height%, Txt$, Img%, Frame% = 0)
	Txt = String(" ", ImageWidth(Img) / 8) + Txt
	
	Local Result% = UpdateLauncherButton(x, y, Width, Height, Txt, False, False)
	
	DrawImage(Img, x + (Width / 2) - (StringWidth(Txt) / 2) - 3, y + (Height / 2) - ImageHeight(Img) / 2, Frame)
	Return(Result)
End Function

Function LimitTextWithImage(Txt$, x%, y%, Width%, Img%, Frame% = 0)
	DrawImage(Img, x, y + (StringHeight(Txt) / 2) - (ImageHeight(Img) / 2) - 1, Frame)
	LimitText(Txt, x + 3 + ImageWidth(Img), y, Width - ImageWidth(Img) - 3)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D