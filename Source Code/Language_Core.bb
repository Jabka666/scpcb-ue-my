Type ListLanguage
	Field Name$
	Field ID$
	Field Author$
	Field LastModify$
	Field Flag$
End Type

Function LanguageSelector()
	Local BasePath$ = "";GetEnv("AppData") + "\scpcb-ue\temp\"
	;DownloadFile("https://files.ziyuesinicization.site/cbue/list.txt", BasePath + "temp.txt") ; ~ List of languages
	Local File% = OpenFile("temp.txt")
	If File <> 0 Then
		While Not Eof(File)
			l$ = ReadLine(File)
			If l <> ""
				Local lan.ListLanguage = New ListLanguage
				lan\Name$ = ParseDomainTXT(l, "name")
				lan\ID$ = ParseDomainTXT(l, "id")
				lan\Author$ = ParseDomainTXT(l, "author")
				lan\LastModify$ = ParseDomainTXT(l, "mod")
				lan\Flag$ = ParseDomainTXT(l, "flag")
				;DownloadFile("https://files.ziyuesinicization.site/cbue/flags" + lan\Flag$, BasePath + "flags/" + lan\Flag$) ; ~ Flags of languages
			Else
				Exit
			EndIf
		Wend
		CloseFile(File)
		;DeleteFile(BasePath + "temp.txt")
	EndIf
	
	AppTitle(GetLocalString("language", "title"))
	SetBuffer(BackBuffer())
	Cls()
	Flip(True)
	Local LanguageBG% = LoadImage_Strict("GFX\menu\language.jpg")
	Local LanguageIMG% = CreateImage(452, 254)
	Local ButtonImages% = LoadAnimImage_Strict("GFX\menu\buttons.png", 21, 21, 0, 5)
	Local SelectedLanguage.ListLanguage = Null
	Local RowTextContent$ = ""
	
	Repeat
		mo\MouseHit1 = MouseHit(1)
		
		SetBuffer(BackBuffer())
		Cls()
		Color(255, 255, 255)
		DrawImage(LanguageBG%, 0, 0)
		Rect 479, 195, 140, 150
		Color 0,0,0
		RowText(RowTextContent, 482, 198, 137, 150)
		
		If LinesAmount > 13 Then
			y# = 200 - (20 * ScrollMenuHeight * ScrollBarY)
			LinesAmount% = 0
			SetBuffer(ImageBuffer(LanguageIMG))
			DrawImage(LanguageBG, -20, -195)
			For lan.ListLanguage = Each ListLanguage
				Color(1, 0, 0)
				If lan\Name$ = "UserLanguage" Then 
					LimitTextWithImage(GetLocalString("language", "local"), 2, y# - 195, 432, LoadImage(BasePath + "flags\"+lan\Flag$)) 
				Else 
					LimitTextWithImage(lan\Name$ + "(" + lan\ID$ + ")", 2, y# - 195, 432, LoadImage(BasePath + "flags\"+lan\Flag$))
				EndIf
				If lan\Name$ = opt\Language Then
					Color(200, 0, 0)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
				EndIf
				If (SelectedLanguage <> Null) And (lan\Name$ = SelectedLanguage\Name$) Then
					Color(1, 0, 0)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
				EndIf
				If MouseOn(20, y - FontHeight() / 2, 432, 20) Then
					Color(150, 150, 150)
					Rect(0, y - 195 - FontHeight() / 2, 430, 20, False)
					If mo\MouseHit1 Then SelectedLanguage = lan
				EndIf
				y# = y# + 20
				LinesAmount = LinesAmount + 1
			Next
			SetBuffer(BackBuffer())
			DrawImage(LanguageIMG, 20, 195)
			Color(10, 10, 10)
			Rect(452, 195, 20, 254, True)
			ScrollMenuHeight# = LinesAmount - 12
			ScrollBarY# = DrawScrollBar(452, 195, 20, 254, 452, 195 + (254 - (254 - 4 * ScrollMenuHeight)) * ScrollBarY, 20, 254 - (4 * ScrollMenuHeight), ScrollBarY, 1)
		Else
			Color(0, 0, 0)
			y# = 201
			LinesAmount% = 0
			For lan.ListLanguage = Each ListLanguage
				Color(0, 0, 0)
				If lan\Name$ = "UserLanguage" Then 
					LimitTextWithImage(GetLocalString("language", "local"), 21, y#, 432, LoadImage(BasePath + "flags\"+lan\Flag$)) 
				Else 
					LimitTextWithImage(lan\Name$ + "(" + lan\ID$ + ")", 21, y#, 432, LoadImage(BasePath + "flags\"+lan\Flag$))
				EndIf
				If lan\Name$ = opt\Language Then 
					Color(200, 0, 0)
					Rect(20, y - FontHeight() / 2, 430, 20, False)
				EndIf
				If (SelectedLanguage <> Null) And (lan\Name$ = SelectedLanguage\Name$) Then
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
			If SelectedLanguage\Name = "English" Then
				If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
					AppTitle(GetLocalString("language", "title"))
				EndIf
			ElseIf FileType("Localization\" + SelectedLanguage\ID) = 2 Then
				If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "setting"), ButtonImages, 2) Then
					SetLanguage(SelectedLanguage\ID)
					fo\FontID[Font_Default] = LoadFont_Strict("GFX\fonts\Courier New.ttf", 16, True)
					AppTitle(GetLocalString("language", "title"))
				EndIf
			Else
				If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "download"), ButtonImages, 1) Then
					; ~ todo
				EndIf
			EndIf
		Else
			If ButtonWithImage(479, LauncherHeight - 65 - 50, 140, 30, GetLocalString("language", "contribute"), ButtonImages, 4) Then 
				Exit
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
	;DeleteDir(BasePath + "flags\")
	If LanguageIMG <> 0 Then FreeImage LanguageIMG
	AppTitle GetLocalString("launcher", "title")
End Function

; ~ Re-added
Global OnBar%
Global ScrollBarY# = 0.0
Global ScrollMenuHeight# = 0.0

Function DrawScrollBar#(x, y, width, height, barx, bary, barwidth, barheight, bar#, dir = 0)
	;0 = vaakasuuntainen, 1 = pystysuuntainen
	Local MouseSpeedX = MouseXSpeed()
	Local MouseSpeedY = MouseYSpeed()
	
	Color(0, 0, 0)
	Button(barx, bary, barwidth, barheight, "")
	
	If dir = 0 Then ;vaakasuunnassa
		If height > 10 Then
			Color 250,250,250
			Rect(barx + barwidth / 2, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 - 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
			Rect(barx + barwidth / 2 + 3*MenuScale, bary + 5*MenuScale, 2*MenuScale, barheight - 10)
		EndIf
	Else ;pystysuunnassa
		If width > 10 Then
			Color 250,250,250
			Rect(barx + 4*MenuScale, bary + barheight / 2, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 - 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
			Rect(barx + 4*MenuScale, bary + barheight / 2 + 3*MenuScale, barwidth - 10*MenuScale, 2*MenuScale)
		EndIf
	EndIf
	
	If MouseX()>barx And MouseX()<barx+barwidth
		If MouseY()>bary And MouseY()<bary+barheight
			OnBar = True
		Else
			If (Not MouseDown(1))
				OnBar = False
			EndIf
		EndIf
	Else
		If (Not MouseDown(1))
			OnBar = False
		EndIf
	EndIf
	
	If MouseDown(1)
		If OnBar
			If dir = 0
				Return Min(Max(bar + MouseSpeedX / Float(width - barwidth), 0), 1)
			Else
				Return Min(Max(bar + MouseSpeedY / Float(height - barheight), 0), 1)
			EndIf
		EndIf
	EndIf
	
	Local MouseSpeedZ = MouseZSpeed()
	
	If MouseSpeedZ<>0 Then ;Only for vertical scroll bars
		Return Min(Max(bar - (MouseSpeedZ*3) / Float(height - barheight), 0), 1)
    EndIf
	
	Return bar
End Function

; ~ Another reborn
Function LimitText%(txt$, x%, y%, width%)
	Local TextLength%
	Local UnFitting%
	Local LetterWidth%
	If txt = "" Or width = 0 Then Return 0
	TextLength = StringWidth(txt)
	UnFitting = TextLength - width
	If UnFitting <= 0 Then ;mahtuu
		Text(x, y, txt, 0, 0)
	Else ;ei mahdu
		LetterWidth = TextLength / Len(txt)
		Text(x, y, Left(txt, Max(Len(txt) - UnFitting / LetterWidth - 4, 1)) + "...", 0, 0)
	End If
End Function

Function Button%(x,y,width,height,txt$, disabled%=False)
	Local Pushed = False
	
	Color 50, 50, 50
	If Not disabled Then 
		If MouseX() > x And MouseX() < x+width Then
			If MouseY() > y And MouseY() < y+height Then
				If MouseDown(1) Then
					Pushed = True
					Color 50*0.6, 50*0.6, 50*0.6
				Else
					Color Min(50*1.2, 255), Min(50*1.2, 255), Min(50*1.2, 255)
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect x,y,width,height
		Color 133,130,125
		Rect x+1*MenuScale,y+1*MenuScale,width-1*MenuScale,height-1*MenuScale,False	
		Color 10,10,10
		Rect x,y,width,height,False
		Color 250,250,250
		Line x,y+height-1*MenuScale,x+width-1*MenuScale,y+height-1*MenuScale
		Line x+width-1*MenuScale,y,x+width-1*MenuScale,y+height-1*MenuScale
	Else
		Rect x,y,width,height
		Color 133,130,125
		Rect x,y,width-1*MenuScale,height-1*MenuScale,False	
		Color 250,250,250
		Rect x,y,width,height,False
		Color 10,10,10
		Line x,y+height-1,x+width-1,y+height-1
		Line x+width-1,y,x+width-1,y+height-1		
	EndIf
	
	Color 255,255,255
	If disabled Then Color(70, 70, 70)
	Text x+width/2, y+height/2-1*MenuScale, txt, True, True
	
	Color 0,0,0
	
	If Pushed And mo\MouseHit1 Then PlaySound_Strict(ButtonSFX) : Return True
End Function

Function ButtonWithImage%(x, y, width, height, txt$, image%, frame% = 0)
	txt = String(" ", ImageWidth(image)/8) + txt
	Local result = UpdateLauncherButton(x, y, width, height, txt, False, False)
	DrawImage(image, x + width / 2 - StringWidth(txt) / 2 - 3, y + (height / 2) - ImageHeight(image) / 2, frame)
	Return result
End Function

Function LimitTextWithImage(txt$, x, y, width, image%, frame% = 0)
	DrawImage(image, x, y + StringHeight(txt) / 2 - ImageHeight(image) / 2 - 1, frame)
	LimitText(txt, x + 3 + ImageWidth(image), y, width - ImageWidth(image) - 3)
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D