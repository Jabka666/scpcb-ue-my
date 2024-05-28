Const MaxAchievements% = 45

Type Achievements
	Field Achievement%[MaxAchievements]
	Field AchievementStrings$[MaxAchievements]
	Field AchievementDescs$[MaxAchievements]
	Field AchvIMG%[MaxAchievements]
	Field AchvLocked%
End Type

Global achv.Achievements

Global UsedConsole%

; ~ Achievements ID Constants
;[Block]
Const Achv005% = 0, Achv008% = 1, Achv012% = 2, Achv035% = 3, Achv049% = 4, Achv055% = 5, Achv066% = 6, Achv079% = 7, Achv096% = 8, Achv106% = 9
Const Achv148% = 10, Achv205% = 11, Achv268% = 12, Achv294% = 13, Achv372% = 14, Achv409% = 15, Achv420_J% = 16, Achv427% = 17, Achv458% = 18
Const Achv500% = 19, Achv513% = 20, Achv714% = 21, Achv789_J% = 22, Achv860% = 23, Achv895% = 24, Achv914% = 25, Achv939% = 26, Achv966% = 27
Const Achv970% = 28, Achv1025% = 29, Achv1048% = 30, Achv1123% = 31, Achv1162_ARC% = 32, Achv1499% = 33

Const AchvConsole% = 34, AchvGears% = 35, AchvHarp% = 36, AchvKeter% = 37, AchvKeyCard6% = 38, AchvMaynard% = 39, AchvOmni% = 40
Const AchvO5% = 41, AchvPD% = 42, AchvSNAV% = 43, AchvTesla% = 44
;[End Block]

Function GiveAchievement%(AchvName%, ShowMessage% = True)
	If achv\Achievement[AchvName] <> True
		achv\Achievement[AchvName] = True
		If opt\AchvMsgEnabled And ShowMessage
			Local AchievementName$ = GetFileLocalString(AchievementsFile, "a" + AchvName, "AchvName")
			
			CreateAchievementMsg(AchvName, AchievementName)
		EndIf
	EndIf
End Function

Function AchievementTooltip%(AchvNo%)
	Local Scale# = opt\GraphicHeight / 768.0
	Local CoordEx% = 20 * MenuScale
	
	SetFontEx(fo\FontID[Font_Digital])
	
	Local Width% = StringWidth(achv\AchievementStrings[AchvNo])
	Local Height% = 38 * Scale
	
	SetFontEx(fo\FontID[Font_Default])
	
	Local Width2% = StringWidth(achv\AchievementDescs[AchvNo])
	
	If Width2 > Width Then Width = Width2
	Width = Width + CoordEx
	
	Local RectPosx% = MousePosX + CoordEx
	Local RectPosY% = MousePosY + CoordEx
	Local TextPosX% = RectPosx + (Width / 2)
	
	Color(25, 25, 25)
	Rect(RectPosx, RectPosY, Width, Height, True)
	Color(150, 150, 150)
	Rect(RectPosx, RectPosY, Width, Height, False)
	SetFontEx(fo\FontID[Font_Digital])
	TextEx(TextPosX, MousePosY + (35 * MenuScale), achv\AchievementStrings[AchvNo], True, True)
	SetFontEx(fo\FontID[Font_Default])
	TextEx(TextPosX, MousePosY + (55 * MenuScale), achv\AchievementDescs[AchvNo], True, True)
End Function

Function RenderAchvIMG%(x%, y%, AchvNo%)
	Local IMG%
	Local Scale# = opt\GraphicHeight / 768.0
	Local Row% = (AchvNo Mod 4)
	Local SeparationConst2# = 76.0 * Scale
	Local IMGSize% = 64 * Scale
	Local RectPosX% = x + (Row * SeparationConst2)
	
	Color(0, 0, 0)
	Rect(RectPosX, y, IMGSize, IMGSize, True)
	If achv\Achievement[AchvNo]
		IMG = achv\AchvIMG[AchvNo]
	Else
		IMG = achv\AchvLocked
	EndIf
	DrawBlock(IMG, RectPosX, y)
	Color(50, 50, 50)
	
	Rect(RectPosX, y, IMGSize, IMGSize, False)
End Function

Global CurrAchvMSGID% = 0

Type AchievementMsg
	Field AchvID%
	Field Txt$
	Field MsgX#
	Field MsgTime#
	Field MsgID%
End Type

Function CreateAchievementMsg.AchievementMsg(ID%, Txt$)
	Local amsg.AchievementMsg
	
	amsg.AchievementMsg = New AchievementMsg
	amsg\AchvID = ID
	amsg\Txt = Txt
	amsg\MsgX = 0.0
	amsg\MsgTime = fps\Factor[1]
	amsg\MsgID = CurrAchvMSGID
	CurrAchvMSGID = CurrAchvMSGID + 1
	
	achv\AchvIMG[ID] = LoadImage_Strict("GFX\Menu\achievements\" + GetFileLocalString(AchievementsFile, "a" + ID, "AchvImage") + ".png")
	achv\AchvIMG[ID] = ScaleImage2(achv\AchvIMG[ID], opt\GraphicHeight / 768.0, opt\GraphicHeight / 768.0)
	
	Return(amsg)
End Function

Function UpdateAchievementMsg%()
	Local amsg.AchievementMsg, amsg2.AchievementMsg
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width% = 264.0 * Scale
	Local Height% = 84.0 * Scale
	Local x%, y%
	
	For amsg.AchievementMsg = Each AchievementMsg
		If amsg\MsgTime <> 0.0
			If amsg\MsgTime > 0.0 And amsg\MsgTime < 70.0 * 7.0
				amsg\MsgTime = amsg\MsgTime + fps\Factor[1]
				If amsg\MsgX > -Width Then amsg\MsgX = Max(amsg\MsgX - (4.0 * fps\Factor[1]), -Width)
			ElseIf amsg\MsgTime >= 70.0 * 7.0
				amsg\MsgTime = -1.0
			ElseIf amsg\MsgTime = -1.0
				If amsg\MsgX < 0.0
					amsg\MsgX = Min(amsg\MsgX + (4.0 * fps\Factor[1]), 0.0)
				Else
					amsg\MsgTime = 0.0
				EndIf
			EndIf
		Else
			Delete(amsg)
		EndIf
	Next
End Function

Function RenderAchievementMsg%()
	If SelectedDifficulty\Name = "Apollyon" Lor (Not opt\HUDEnabled) Then Return
	
	Local amsg.AchievementMsg, amsg2.AchievementMsg
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width% = 264.0 * Scale
	Local Height% = 84.0 * Scale
	Local IMGSize% = 64 * Scale
	Local CoordEx% = 10 * Scale
	Local x%, y%
	
	For amsg.AchievementMsg = Each AchievementMsg
		If amsg\MsgTime <> 0.0
			x = opt\GraphicWidth + amsg\MsgX
			y = 0
			For amsg2.AchievementMsg = Each AchievementMsg
				If amsg2 <> amsg
					If amsg2\MsgID > amsg\MsgID Then y = y + Height 
				EndIf
			Next
			RenderFrame(x, y, Width, Height)
			Color(0, 0, 0)
			Rect(x + CoordEx, y + CoordEx, IMGSize, IMGSize)
			DrawBlock(achv\AchvIMG[amsg\AchvID], x + CoordEx, y + CoordEx)
			Color(50, 50, 50)
			Rect(x + CoordEx, y + CoordEx, IMGSize, IMGSize, False)
			Color(255, 255, 255)
			SetFontEx(fo\FontID[Font_Default])
			RowText(Format(GetLocalString("msg", "achv.unlocked"), amsg\Txt), x + (84.0 * Scale), y + CoordEx, Width - (94.0 * Scale), y - (20.0 * Scale))
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS