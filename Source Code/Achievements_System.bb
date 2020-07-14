Global Achievements%[MAXACHIEVEMENTS]

Global UsedConsole%

Global AchievementsMenu%
Global AchievementStrings$[MAXACHIEVEMENTS]
Global AchievementDescs$[MAXACHIEVEMENTS]
Global AchvIMG%[MAXACHIEVEMENTS]

For i = 0 To MAXACHIEVEMENTS - 1
	Local Loc2% = GetINISectionLocation("Data\Achievements.ini", "s" + Str(i))
	
	AchievementStrings[i] = GetINIString2("Data\Achievements.ini", Loc2, "AchvName")
	AchievementDescs[i] = GetINIString2("Data\Achievements.ini", Loc2, "AchvDesc")
	
	Local Image$ = GetINIString2("Data\Achievements.ini", Loc2, "AchvImage") 
	
	AchvIMG[i] = LoadImage_Strict("GFX\menu\Achievements\" + Image + ".png")
	AchvIMG[i] = ResizeImage2(AchvIMG[i], ImageWidth(AchvIMG[i]) * GraphicHeight / 768.0, ImageHeight(AchvIMG[i]) * GraphicHeight / 768.0)
	BufferDirty(ImageBuffer(AchvIMG[i]))
Next

Global AchvLocked% = LoadImage_Strict("GFX\menu\Achievements\AchvLocked.png")

AchvLocked = ResizeImage2(AchvLocked, ImageWidth(AchvLocked) * GraphicHeight / 768.0, ImageHeight(AchvLocked) * GraphicHeight / 768.0)
BufferDirty(ImageBuffer(AchvLocked))

Function GiveAchievement(AchvName%, ShowMessage% = True)
	If Achievements[AchvName] <> True Then
		Achievements[AchvName] = True
		If AchvMsgEnabled And ShowMessage Then
			Local Loc2% = GetINISectionLocation("Data\Achievements.ini", "s" + AchvName)
			Local AchievementName$ = GetINIString2("Data\Achievements.ini", Loc2, "AchvName")
			
			CreateAchievementMsg(AchvName, AchievementName)
		EndIf
	EndIf
End Function

Function AchievementTooltip(AchvNo%)
    Local Scale# = GraphicHeight / 768.0

    SetFont(fo\FontID[2])
	
    Local Width% = StringWidth(AchievementStrings[AchvNo])
	
    SetFont(fo\FontID[0])
    If StringWidth(AchievementDescs[AchvNo]) > Width Then
        Width = StringWidth(AchievementDescs[AchvNo])
    EndIf
    Width = Width + 20 * MenuScale
    
    Local Height% = 38 * Scale
    
    Color(25, 25, 25)
    Rect(ScaledMouseX() + (20 * MenuScale), ScaledMouseY() + (20 * MenuScale), Width, Height, True)
    Color(150, 150, 150)
    Rect(ScaledMouseX() + (20 * MenuScale), ScaledMouseY() + (20 * MenuScale), Width, Height, False)
    SetFont(fo\FontID[2])
    Text(ScaledMouseX() + (20 * MenuScale) + (Width / 2), ScaledMouseY() + (35 * MenuScale), AchievementStrings[AchvNo], True, True)
    SetFont(fo\FontID[0])
    Text(ScaledMouseX() + (20 * MenuScale) + (Width / 2), ScaledMouseY() + (55 * MenuScale), AchievementDescs[AchvNo], True, True)
End Function

Function DrawAchvIMG(x%, y%, AchvNo%)
	Local Row%
	Local Scale# = GraphicHeight / 768.0
	Local SeparationConst2# = 76.0 * Scale
	
	Row = AchvNo Mod 4
	Color(0, 0, 0)
	Rect((x + ((Row) * SeparationConst2)), y, 64 * Scale, 64 * Scale, True)
	If Achievements[AchvNo] = True Then
		DrawImage(AchvIMG[AchvNo], (x + (Row * SeparationConst2)), y)
	Else
		DrawImage(AchvLocked, (x + (Row * SeparationConst2)), y)
	EndIf
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
	Local amsg.AchievementMsg = New AchievementMsg
	
	amsg\AchvID = ID
	amsg\Txt = Txt
	amsg\MsgX = 0.0
	amsg\MsgTime = fpst\FPSFactor[1]
	amsg\MsgID = CurrAchvMSGID
	CurrAchvMSGID = CurrAchvMSGID + 1
	
	Return(amsg)
End Function

Function UpdateAchievementMsg()
	Local amsg.AchievementMsg, amsg2.AchievementMsg
	Local Scale# = GraphicHeight / 768.0
	Local Width% = 264.0 * Scale
	Local Height% = 84.0 * Scale
	Local x%, y%
	
	For amsg.AchievementMsg = Each AchievementMsg
		If amsg\MsgTime <> 0.0
			If amsg\MsgTime > 0.0 And amsg\MsgTime < 70.0 * 7.0
				amsg\MsgTime = amsg\MsgTime + fpst\FPSFactor[1]
				If amsg\MsgX > -Width
					amsg\MsgX = Max(amsg\MsgX - 4.0 * fpst\FPSFactor[1], -Width)
				EndIf
			ElseIf amsg\MsgTime >= 70.0 * 7.0
				amsg\MsgTime = -1.0
			ElseIf amsg\MsgTime = -1.0
				If amsg\MsgX < 0.0
					amsg\MsgX = Min(amsg\MsgX + 4.0 * fpst\FPSFactor[1], 0.0)
				Else
					amsg\MsgTime = 0.0
				EndIf
			EndIf
		Else
			Delete(amsg)
		EndIf
	Next
End Function

Function RenderAchievementMsg()
	Local amsg.AchievementMsg, amsg2.AchievementMsg
	Local Scale# = GraphicHeight / 768.0
	Local Width% = 264.0 * Scale
	Local Height% = 84.0 * Scale
	Local x%, y%
	
	For amsg.AchievementMsg = Each AchievementMsg
		If amsg\MsgTime <> 0.0
            x = GraphicWidth + amsg\MsgX
			y = 0
			For amsg2.AchievementMsg = Each AchievementMsg
				If amsg2 <> amsg
					If amsg2\MsgID > amsg\MsgID
						y = y + Height 
					EndIf
				EndIf
			Next
			DrawFrame(x, y, Width, Height)
			Color(0, 0, 0)
			Rect(x + 10.0 * Scale, y + 10.0 * Scale, 64.0 * Scale, 64.0 * Scale, True)
			DrawImage(AchvIMG[amsg\AchvID], x + 10 * Scale, y + 10 * Scale)
			Color(50, 50, 50)
			Rect(x + 10.0 * Scale, y + 10.0 * Scale, 64.0 * Scale, 64.0 * Scale, False)
			Color(255, 255, 255)
			SetFont(fo\FontID[0])
			RowText("Achievement Unlocked - " + amsg\Txt, x + 84.0 * Scale, y + 10.0 * Scale, Width - 94.0 * Scale, y - 20.0 * Scale)
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D