Type INIFile
	Field Name$
	Field Bank%
	Field BankOffset% = 0
	Field Size%
End Type

Function ReadINILine$(file.INIFile)
	Local RdByte%
	Local FirstByte% = True
	Local Offset% = file\BankOffset
	Local Bank% = file\Bank
	Local RetStr$ = ""
	
	RdByte = PeekByte(Bank, Offset)
	While ((FirstByte) Or ((RdByte <> 13) And (RdByte <> 10))) And (Offset < file\Size)
		RdByte = PeekByte(Bank, Offset)
		If ((RdByte <> 13) And (RdByte <> 10)) Then
			FirstByte = False
			RetStr = RetStr + Chr(RdByte)
		EndIf
		Offset = Offset + 1
	Wend
	file\BankOffset = Offset
	
	Return(RetStr)
End Function

Function UpdateINIFile$(FileName$)
	Local file.INIFile = Null
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(FileName) Then
			file = k
		EndIf
	Next
	
	If file = Null Then Return
	If file\Bank <> 0 Then FreeBank(file\Bank)
	
	Local f% = ReadFile(file\Name)
	Local FleSize% = 1
	
	While FleSize < FileSize(file\Name)
		FleSize = FleSize * 2
	Wend
	file\Bank = CreateBank(FleSize)
	file\Size = 0
	While Not Eof(f)
		PokeByte(file\Bank, file\Size, ReadByte(f))
		file\Size = file\Size + 1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Local TemporaryString$ = ""
	Local lfile.INIFile = Null
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(File) Then
			lfile = k
		EndIf
	Next
	
	If lfile = Null Then
		lfile = New INIFile
		lfile\Name = Lower(File)
		lfile\Bank = 0
		UpdateINIFile(lfile\Name)
	EndIf
	
	lfile\BankOffset = 0
	
	Section = Lower(Section)
	
	While lfile\BankOffset < lfile\Size
		Local StrTemp$ = ReadINILine(lfile)
		
		If Left(StrTemp, 1) = "[" Then
			StrTemp = Lower(StrTemp)
			If Mid(StrTemp, 2, Len(StrTemp) - 2) = Section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(Parameter) Then
						Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
					EndIf
				Until (Left(TemporaryString, 1) = "[") Or (lfile\BankOffset >= lfile\Size)
				Return(DefaultValue)
			EndIf
		EndIf
	Wend
	Return(DefaultValue)
End Function

Function GetINIInt%(File$, Section$, Parameter$, DefaultValue% = 0)
	Local Txt$ = GetINIString(File, Section, Parameter, DefaultValue)
	
	If Lower(Txt) = "true" Then
		Return(1)
	ElseIf Lower(Txt) = "false"
		Return(0)
	Else
		Return(Int(Txt))
	EndIf
End Function

Function GetINIFloat#(File$, Section$, Parameter$, DefaultValue# = 0.0)
	Return(Float(GetINIString(File, Section, Parameter, DefaultValue)))
End Function

Function GetINIString2$(File$, Start%, Parameter$, DefaultValue$ = "")
	Local TemporaryString$ = ""
	Local f% = ReadFile(File)
	Local n% = 0
	
	While Not Eof(f)
		Local StrTemp$ = ReadLine(f)
		
		n = n + 1
		If n = Start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(Parameter) Then
					CloseFile(f)
					Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
				EndIf
			Until Left(TemporaryString, 1) = "[" Or Eof(f)
			CloseFile(f)
			Return(DefaultValue)
		EndIf
	Wend
	CloseFile(f)	
	Return(DefaultValue)
End Function

Function GetINIInt2%(File$, Start%, Parameter$, DefaultValue$ = "")
	Local Txt$ = GetINIString2(File, Start, Parameter, DefaultValue)
	
	If Lower(Txt) = "True" Then
		Return(1)
	ElseIf Lower(Txt) = "False"
		Return(0)
	Else
		Return(Int(Txt))
	EndIf
End Function

Function GetINISectionLocation%(File$, Section$)
	Local Temp%
	Local f% = ReadFile(File)
	
	Section = Lower(Section)
	
	Local n% = 0
	
	While Not Eof(f)
		Local StrTemp$ = ReadLine(f)
		
		n = n + 1
		If Left(StrTemp, 1) = "[" Then
			StrTemp = Lower(StrTemp)
			Temp = Instr(StrTemp, Section)
			If Temp > 0 Then
				If Mid(StrTemp, Temp - 1, 1) = "[" Or Mid(StrTemp, Temp - 1, 1) = "|" Then
					CloseFile(f)
					Return(n)
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function PutINIValue%(File$, INI_sSection$, INI_sKey$, INI_sValue$)
	; ~ Returns: True (Success) Or False (Failed)
	INI_sSection = "[" + Trim(INI_sSection) + "]"
	
	Local INI_sUpperSection$ = Upper(INI_sSection)
	
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	
	Local INI_sFilename$ = File
	; ~ Retrieve the INI data (if it exists)
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	; ~ (Re)Create the INI file updating / adding the SECTION, KEY and VALUE
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	
	If INI_lFileHandle = 0 Then Return(False) ; ~ Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr(0))
	
	While (INI_lPos <> 0)
		Local INI_sTemp$ = Mid(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			If Left(INI_sTemp, 1) = "[" And Right(INI_sTemp, 1) = "]" Then
				; ~ Process SECTION
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine(INI_lFileHandle, INI_sTemp)
				Else
					; ~ KEY = VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine(INI_lFileHandle, INI_sTemp)
						End If
					End If
				EndIf
			End If	
		End If
		; ~ Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos = Instr(INI_sContents, Chr(0), INI_lOldPos)
	Wend
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION If required and create our KEY = VALUE line
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	End If
	CloseFile(INI_lFileHandle)
	Return(True) ; ~ Success
End Function

Function INI_FileToString$(INI_sFilename$)
	Local INI_sString$ = ""
	Local INI_lFileHandle% = ReadFile(INI_sFilename)
	
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine(INI_lFileHandle) + Chr(0)
		Wend
		CloseFile(INI_lFileHandle)
	End If
	Return(INI_sString)
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine(INI_lFileHandle, "") ; ~ Blank Line between sections
	WriteLine(INI_lFileHandle, INI_sNewSection)
	Return(INI_sNewSection)
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine(INI_lFileHandle, INI_sKey + " = " + INI_sValue)
	Return(True)
End Function

; ~ [GRAPHICS]

Global ParticleAmount% = GetINIInt(OptionFile, "graphics", "particle amount")

Global Opt_AntiAlias% = GetINIInt(OptionFile, "graphics", "antialias")

Global BumpEnabled% = GetINIInt(OptionFile, "graphics", "enable bump mapping")

Global SaveTexturesInVRAM% = GetINIInt(OptionFile, "graphics", "save in vram")

Global EnableRoomLights% = GetINIInt(OptionFile, "graphics", "enable room lights")

Global VSync% = GetINIInt(OptionFile, "graphics", "vsync")

Global ScreenGamma# = GetINIFloat(OptionFile, "graphics", "screen gamma")

Global TextureDetails% = GetINIInt(OptionFile, "graphics", "texture details")

; ~ [AUDIO]

Global MusicVolume# = GetINIFloat(OptionFile, "audio", "music volume")

Global EnableUserTracks% = GetINIInt(OptionFile, "audio", "enable user tracks")

Global UserTrackMode% = GetINIInt(OptionFile, "audio", "user track setting")

Global SFXVolume# = GetINIFloat(OptionFile, "audio", "Sound volume")

Global EnableSFXRelease% = GetINIInt(OptionFile, "audio", "sfx release")

; ~ [ADVANCED]

Global AATextEnable% = GetINIInt(OptionFile, "advanced", "antialiased text")

Global AchvMsgEnabled% = GetINIInt(OptionFile, "advanced", "enable achievement popup")

Global CanOpenConsole% = GetINIInt(OptionFile, "advanced", "console enabled")

Global HUDenabled% = GetINIInt(OptionFile, "advanced", "hid enabled")

Global ShowFPS% = GetINIInt(OptionFile, "advanced", "show fps")

Global ConsoleOpening% = GetINIInt(OptionFile, "advanced", "console auto opening")

Global FrameLimit% = GetINIInt(OptionFile, "advanced", "framelimit")

; ~ [CONTROLS]

Global KEY_RIGHT% = GetINIInt(OptionFile, "controls", "right key")

Global KEY_LEFT% = GetINIInt(OptionFile, "controls", "left key")

Global KEY_UP% = GetINIInt(OptionFile, "controls", "up key")

Global KEY_DOWN% = GetINIInt(OptionFile, "controls", "down key")

Global KEY_BLINK% = GetINIInt(OptionFile, "controls", "blink key")

Global KEY_SPRINT% = GetINIInt(OptionFile, "controls", "sprint key")

Global KEY_INV% = GetINIInt(OptionFile, "controls", "inventory key")

Global KEY_CROUCH% = GetINIInt(OptionFile, "controls", "crouch key")

Global KEY_SAVE% = GetINIInt(OptionFile, "controls", "save key")

Global KEY_CONSOLE% = GetINIInt(OptionFile, "controls", "console key")

Global MouseSmoothing# = GetINIFloat(OptionFile, "controls", "mouse smoothing", 1.0)

Global InvertMouse% = GetINIInt(OptionFile, "controls", "invert mouse y")

Global MouseSensitivity# = GetINIFloat(OptionFile, "controls", "mouse sensitivity")

; ~ [LAUNCHER]

Global LauncherWidth% = Min(GetINIInt(OptionFile, "launcher", "launcher width"), 1024)

Global LauncherHeight% = Min(GetINIInt(OptionFile, "launcher", "launcher height"), 768)

Global LauncherEnabled% = GetINIInt(OptionFile, "launcher", "launcher enabled")

; ~ [GLOBAL]

Global GraphicWidth% = GetINIInt(OptionFile, "global", "width")

Global GraphicHeight% = GetINIInt(OptionFile, "global", "height")

Global FullScreen% = GetINIInt(OptionFile, "global", "fullscreen")

Global SelectedGFXDriver% = Max(GetINIInt(OptionFile, "global", "gfx driver"), 1)

Global BorderlessWindowed% = GetINIInt(OptionFile, "global", "borderless windowed")

Global Bit16Mode% = GetINIInt(OptionFile, "global", "16bit")

Global Brightness% = GetINIFloat(OptionFile, "global", "brightness")

Global CameraFogNear# = GetINIFloat(OptionFile, "global", "camera fog near")

Global CameraFogFar# = GetINIFloat(OptionFile, "global", "camera fog far")

Global PlayStartup% = GetINIInt(OptionFile, "global", "play startup video")

Global MapWidth% = GetINIInt(OptionFile, "global", "map size")

Global MapHeight% = GetINIInt(OptionFile, "global", "map size")

Global IntroEnabled% = GetINIInt(OptionFile, "global", "enable intro")

Function SaveOptionsINI()
	; ~ [GRAPHICS]
	
	PutINIValue(OptionFile, "graphics", "enable bump mapping", BumpEnabled)
	
	PutINIValue(OptionFile, "graphics", "screen Gamma", ScreenGamma)
	
	PutINIValue(OptionFile, "graphics", "antialias", Opt_AntiAlias)
	
	PutINIValue(OptionFile, "graphics", "vsync", VSync)
	
	PutINIValue(OptionFile, "graphics", "particle amount", ParticleAmount)
	
	PutINIValue(OptionFile, "graphics", "save in vram", SaveTexturesInVRAM)
	
	PutINIValue(OptionFile, "graphics", "enable room lights", EnableRoomLights)
	
	PutINIValue(OptionFile, "graphics", "texture details", TextureDetails)
	
	; ~ [ADVANCED]
	
	PutINIValue(OptionFile, "advanced", "hud enabled", HUDenabled)
	
	PutINIValue(OptionFile, "advanced", "show fps", ShowFPS)
	
	PutINIValue(OptionFile, "advanced", "framelimit", FrameLimit)
	
	PutINIValue(OptionFile, "advanced", "enable achievement popup", AchvMsgEnabled)
	
	PutINIValue(OptionFile, "advanced", "console enabled", CanOpenConsole)
	
	PutINIValue(OptionFile, "advanced", "console auto opening", ConsoleOpening)
	
	PutINIValue(OptionFile, "advanced", "antialiased text", AATextEnable)
	
	; ~ [CONTROLS]
	
	PutINIValue(OptionFile, "controls", "mouse sensitivity", MouseSensitivity)
	
	PutINIValue(OptionFile, "controls", "invert mouse y", InvertMouse)
	
	PutINIValue(OptionFile, "controls", "mouse smoothing", MouseSmoothing)
	
	PutINIValue(OptionFile, "controls", "right key", KEY_RIGHT)
	
	PutINIValue(OptionFile, "controls", "left key", KEY_LEFT)
	
	PutINIValue(OptionFile, "controls", "up key", KEY_UP)
	
	PutINIValue(OptionFile, "controls", "down key", KEY_DOWN)
	
	PutINIValue(OptionFile, "controls", "blink key", KEY_BLINK)
	
	PutINIValue(OptionFile, "controls", "sprint key", KEY_SPRINT)
	
	PutINIValue(OptionFile, "controls", "inventory key", KEY_INV)
	
	PutINIValue(OptionFile, "controls", "crouch key", KEY_CROUCH)
	
	PutINIValue(OptionFile, "controls", "save key", KEY_SAVE)
	
	PutINIValue(OptionFile, "controls", "console key", KEY_CONSOLE)
	
	; ~ [AUDIO]
	
	PutINIValue(OptionFile, "audio", "music volume", MusicVolume)
	
	PutINIValue(OptionFile, "audio", "sound volume", PrevSFXVolume)
	
	PutINIValue(OptionFile, "audio", "sfx release", EnableSFXRelease)
	
	PutINIValue(OptionFile, "audio", "enable user tracks", EnableUserTracks)
	
	PutINIValue(OptionFile, "audio", "user track setting", UserTrackMode)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D