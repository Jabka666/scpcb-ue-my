Global UsedConsole%
Global AchievementsIndex%
Global AchievementsImages%
Global UnlockedAchievements%
Global AchievementsArray%, LocalAchievementsArray%

Function InitAchievements%()
	Local i%
	
	AchievementsIndex = CreateS2IMap()
	AchievementsImages = CreateS2IMap()
	UnlockedAchievements = CreateS2IMap()

	AchievementsArray = JsonParseFromFile(AchievementsFile)
	LocalAchievementsArray = JsonParseFromFile(lang\LanguagePath + AchievementsFile)

	Local Defines% = JsonGetArray(JsonGetValue(AchievementsArray, "achievements"))
	Local ArraySize% = JsonGetArraySize(Defines)
	
	For i = 0 To ArraySize - 1
		Local ID$ = JsonGetString(JsonGetValue(JsonGetArrayValue(Defines, i), "id"))
		Local Image$ = JsonGetString(JsonGetValue(JsonGetArrayValue(Defines, i), "image"))
		
		S2IMapSet(AchievementsIndex, ID, i)
		S2IMapSet(AchievementsImages, ID, ScaleImage2(LoadImage_Strict("GFX\Menu\achievements\" + Image), opt\GraphicHeight / 768.0, opt\GraphicHeight / 768.0))
	Next
	S2IMapSet(AchievementsImages, "locked", ScaleImage2(LoadImage_Strict("GFX\Menu\achievements\AchvLocked.png"), opt\GraphicHeight / 768.0, opt\GraphicHeight / 768.0))
End Function

Function GiveAchievement%(AchvID$, ShowMessage% = True)
	If S2IMapContains(UnlockedAchievements, AchvID) Then Return

	S2IMapSet(UnlockedAchievements, AchvID, True)
	If opt\AchvMsgEnabled And ShowMessage Then
		CreateAchievementMsg(AchvID)
	EndIf
End Function

Function AchievementTooltip%(AchvID$)
	Local Scale# = opt\GraphicHeight / 768.0
	Local CoordEx% = 20 * MenuScale
	
	SetFontEx(fo\FontID[Font_Digital])

	Local Width%
	Local LocValue% = JsonGetValue(LocalAchievementsArray, "translations")
	Local Value% = JsonGetValue(AchievementsArray, "translations")
	Local AchvName% = JsonGetValue(JsonGetValue(LocValue, AchvID), "name")
	
	If JsonIsNull(AchvName) Then AchvName = JsonGetValue(JsonGetValue(Value, AchvID), "name")
	Width = StringWidth(JsonGetString(AchvName))
	
	Local Height% = 38 * Scale
	
	SetFontEx(fo\FontID[Font_Default])
	
	Local Width2%
	Local AchvDesc% = JsonGetValue(JsonGetValue(LocValue, AchvID), "description")
	
	If JsonIsNull(AchvDesc) Then AchvDesc = JsonGetValue(JsonGetValue(Value, AchvID), "description")
	Width2 = StringWidth(JsonGetString(AchvDesc))
	
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
	TextEx(TextPosX, MousePosY + (35 * MenuScale), JsonGetString(AchvName), True, True)
	SetFontEx(fo\FontID[Font_Default])
	TextEx(TextPosX, MousePosY + (55 * MenuScale), JsonGetString(AchvDesc), True, True)
End Function

Function RenderAchvIMG%(x%, y%, i%, AchvID$)
	Local IMG%
	Local Scale# = opt\GraphicHeight / 768.0
	Local Row% = (i Mod 4)
	Local SeparationConst2# = 76.0 * Scale
	Local IMGSize% = 64 * Scale
	Local RectPosX% = x + (Row * SeparationConst2)
	
	Color(0, 0, 0)
	Rect(RectPosX, y, IMGSize, IMGSize, True)
	If S2IMapContains(UnlockedAchievements, AchvID)
		IMG = S2IMapGet(AchievementsImages, AchvID)
	Else
		IMG = S2IMapGet(AchievementsImages, "locked")
	EndIf
	DrawBlock(IMG, RectPosX, y)
	Color(50, 50, 50)
	
	Rect(RectPosX, y, IMGSize, IMGSize, False)
End Function

Global CurrAchvMSGID% = 0

Type AchievementMsg
	Field Txt$
	Field Desc$
	Field MsgX#
	Field MsgTime#
	Field MsgID%
	Field Image%
End Type

Function CreateAchievementMsg.AchievementMsg(AchvID$)
	Local amsg.AchievementMsg
	
	amsg.AchievementMsg = New AchievementMsg
	
	Local LocValue% = JsonGetValue(LocalAchievementsArray, "translations")
	Local Value% = JsonGetValue(AchievementsArray, "translations")
	Local AchvName% = JsonGetValue(JsonGetValue(LocValue, AchvID), "name")
	
	If JsonIsNull(AchvName) Then AchvName = JsonGetValue(JsonGetValue(Value, AchvID), "name")
	amsg\Txt = JsonGetString(AchvName)
	
	Local AchvDesc% = JsonGetValue(JsonGetValue(LocValue, AchvID), "description")
	
	If JsonIsNull(AchvDesc) Then AchvDesc = JsonGetValue(JsonGetValue(Value, AchvID), "description")
	amsg\Desc = JsonGetString(AchvDesc)
	amsg\MsgX = 0.0
	amsg\MsgTime = fps\Factor[1]
	amsg\MsgID = CurrAchvMSGID
	amsg\Image = S2IMapGet(AchievementsImages, AchvID)
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
	If SelectedDifficulty\Name = difficulties[APOLLYON]\Name Lor (Not opt\HUDEnabled) Then Return
	
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
			DrawBlock(amsg\Image, x + CoordEx, y + CoordEx)
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