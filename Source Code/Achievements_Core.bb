Const MaxAchievements% = 43

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
Const Achv148% = 10, Achv205% = 11, Achv294% = 12, Achv372% = 13, Achv409% = 14, Achv420_J% = 15, Achv427% = 16, Achv500% = 17, Achv513% = 18
Const Achv714% = 19, Achv789_J% = 20, Achv860% = 21, Achv895% = 22, Achv914% = 23, Achv939% = 24, Achv966% = 25, Achv970% = 26
Const Achv1025% = 27, Achv1048% = 28, Achv1123% = 29, Achv1162_ARC% = 30, Achv1499% = 31

Const AchvConsole% = 32, AchvHarp% = 33, AchvKeter% = 34, AchvKeyCard6% = 35, AchvMaynard% = 36, AchvOmni% = 37
Const AchvO5% = 38, AchvPD% = 39, AchvSNAV% = 40, AchvTesla% = 41, Achv268% = 42
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

	SetFont2(fo\FontID[Font_Digital])
	
	Local Width% = StringWidth(achv\AchievementStrings[AchvNo])
	
	SetFont2(fo\FontID[Font_Default])
	If StringWidth(achv\AchievementDescs[AchvNo]) > Width Then Width = StringWidth(achv\AchievementDescs[AchvNo])
	Width = Width + (20 * MenuScale)
	
	Local Height% = 38 * Scale
	
	Color(25, 25, 25)
	Rect(ScaledMouseX() + (20 * MenuScale), ScaledMouseY() + (20 * MenuScale), Width, Height, True)
	Color(150, 150, 150)
	Rect(ScaledMouseX() + (20 * MenuScale), ScaledMouseY() + (20 * MenuScale), Width, Height, False)
	SetFont2(fo\FontID[Font_Digital])
	Text2(ScaledMouseX() + (20 * MenuScale) + (Width / 2), ScaledMouseY() + (35 * MenuScale), achv\AchievementStrings[AchvNo], True, True)
	SetFont2(fo\FontID[Font_Default])
	Text2(ScaledMouseX() + (20 * MenuScale) + (Width / 2), ScaledMouseY() + (55 * MenuScale), achv\AchievementDescs[AchvNo], True, True)
End Function

Function RenderAchvIMG%(x%, y%, AchvNo%)
	Local Row%, IMG%
	Local Scale# = opt\GraphicHeight / 768.0
	Local SeparationConst2# = 76.0 * Scale
	
	Row = (AchvNo Mod 4)
	Color(0, 0, 0)
	Rect((x + ((Row) * SeparationConst2)), y, 64 * Scale, 64 * Scale, True)
	If achv\Achievement[AchvNo]
		IMG = achv\AchvIMG[AchvNo]
	Else
		IMG = achv\AchvLocked
	EndIf
	DrawBlock(IMG, (x + (Row * SeparationConst2)), y)
	Color(50, 50, 50)
	
	Rect((x + (Row * SeparationConst2)), y, 64 * Scale, 64 * Scale, False)
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
	Local amsg.AchievementMsg, amsg2.AchievementMsg
	Local Scale# = opt\GraphicHeight / 768.0
	Local Width% = 264.0 * Scale
	Local Height% = 84.0 * Scale
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
			Rect(x + (10.0 * Scale), y + (10.0 * Scale), 64.0 * Scale, 64.0 * Scale)
			DrawBlock(achv\AchvIMG[amsg\AchvID], x + (10 * Scale), y + 10 * Scale)
			Color(50, 50, 50)
			Rect(x + (10.0 * Scale), y + (10.0 * Scale), 64.0 * Scale, 64.0 * Scale, False)
			Color(255, 255, 255)
			SetFont2(fo\FontID[Font_Default])
			RowText(Format(GetLocalString("msg", "achv.unlocked"), amsg\Txt), x + (84.0 * Scale), y + (10.0 * Scale), Width - (94.0 * Scale), y - (20.0 * Scale))
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS