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

Global ParticleAmount% = GetINIInt(OptionFile, "Graphics", "Particle Amount")

Global Opt_AntiAlias% = GetINIInt(OptionFile, "Graphics", "Antialias")

Global BumpEnabled% = GetINIInt(OptionFile, "Graphics", "Enable Bump Mapping")

Global SaveTexturesInVRAM% = GetINIInt(OptionFile, "Graphics", "Save Textures In VRAM")

Global EnableRoomLights% = GetINIInt(OptionFile, "Graphics", "Enable Room Lights")

Global VSync% = GetINIInt(OptionFile, "Graphics", "VSync")

Global ScreenGamma# = GetINIFloat(OptionFile, "Graphics", "Screen gamma")

Global TextureDetails% = GetINIInt(OptionFile, "Graphics", "Texture details")

; ~ [AUDIO]

Global MusicVolume# = GetINIFloat(OptionFile, "Audio", "Music Volume")

Global EnableUserTracks% = GetINIInt(OptionFile, "Audio", "Enable User Tracks")

Global UserTrackMode% = GetINIInt(OptionFile, "Audio", "User Track Setting")

Global SFXVolume# = GetINIFloat(OptionFile, "Audio", "Sound Volume")

Global EnableSFXRelease% = GetINIInt(OptionFile, "Audio", "SFX Release")

; ~ [ADVANCED]

Global AATextEnable% = GetINIInt(OptionFile, "Advanced", "Antialiased Text")

Global AchvMsgEnabled% = GetINIInt(OptionFile, "Advanced", "Enable Achievement Popup")

Global CanOpenConsole% = GetINIInt(OptionFile, "Advanced", "Enable Console")

Global HUDenabled% = GetINIInt(OptionFile, "Advanced", "Enable HUD")

Global ShowFPS% = GetINIInt(OptionFile, "Advanced", "Show FPS")

Global ConsoleOpening% = GetINIInt(OptionFile, "Advanced", "Console Auto Opening")

Global FrameLimit% = GetINIInt(OptionFile, "Advanced", "Frame Limit")

Global ConsoleVersion% = GetINIInt(OptionFile, "Advanced", "Console Version")

; ~ [CONTROLS]

Global KEY_RIGHT% = GetINIInt(OptionFile, "Controls", "Right Key")

Global KEY_LEFT% = GetINIInt(OptionFile, "Controls", "Left Key")

Global KEY_UP% = GetINIInt(OptionFile, "Controls", "Up Key")

Global KEY_DOWN% = GetINIInt(OptionFile, "Controls", "Down Key")

Global KEY_BLINK% = GetINIInt(OptionFile, "Controls", "Blink Key")

Global KEY_SPRINT% = GetINIInt(OptionFile, "Controls", "Sprint Key")

Global KEY_INV% = GetINIInt(OptionFile, "Controls", "Inventory Key")

Global KEY_CROUCH% = GetINIInt(OptionFile, "Controls", "Crouch Key")

Global KEY_SAVE% = GetINIInt(OptionFile, "Controls", "Save Key")

Global KEY_CONSOLE% = GetINIInt(OptionFile, "Controls", "Console Key")

Global MouseSmoothing# = GetINIFloat(OptionFile, "Controls", "Mouse Smoothing", 1.0)

Global InvertMouse% = GetINIInt(OptionFile, "Controls", "Invert Mouse By Y")

Global MouseSensitivity# = GetINIFloat(OptionFile, "Controls", "Mouse Sensitivity")

; ~ [LAUNCHER]

Global LauncherWidth% = Min(GetINIInt(OptionFile, "Launcher", "Launcher Width"), 1024)

Global LauncherHeight% = Min(GetINIInt(OptionFile, "Launcher", "Launcher Height"), 768)

Global LauncherEnabled% = GetINIInt(OptionFile, "Launcher", "Launcher Enabled")

; ~ [GLOBAL]

Global GraphicWidth% = GetINIInt(OptionFile, "Global", "Width")

Global GraphicHeight% = GetINIInt(OptionFile, "Global", "Height")

Global FullScreen% = GetINIInt(OptionFile, "Global", "Fullscreen")

Global SelectedGFXDriver% = Max(GetINIInt(OptionFile, "Global", "GFX Driver"), 1)

Global BorderlessWindowed% = GetINIInt(OptionFile, "Global", "Borderless Windowed")

Global Bit16Mode% = GetINIInt(OptionFile, "Global", "16Bit")

Global Brightness% = GetINIFloat(OptionFile, "Global", "Brightness")

Global CameraFogNear# = GetINIFloat(OptionFile, "Global", "camera Fog Near")

Global CameraFogFar# = GetINIFloat(OptionFile, "Global", "Camera Fog Far")

Global PlayStartup% = GetINIInt(OptionFile, "Global", "Play Startup Video")

Global MapWidth% = GetINIInt(OptionFile, "Global", "Map Size")

Global MapHeight% = GetINIInt(OptionFile, "Global", "Map Size")

Global IntroEnabled% = GetINIInt(OptionFile, "Global", "Enable Intro")

Function SaveOptionsINI()
	; ~ [GRAPHICS]
	
	PutINIValue(OptionFile, "Graphics", "Enable Bump Mapping", BumpEnabled)
	
	PutINIValue(OptionFile, "Graphics", "Screen Gamma", ScreenGamma)
	
	PutINIValue(OptionFile, "Graphics", "Antialias", Opt_AntiAlias)
	
	PutINIValue(OptionFile, "Graphics", "VSync", VSync)
	
	PutINIValue(OptionFile, "Graphics", "Particle Amount", ParticleAmount)
	
	PutINIValue(OptionFile, "Graphics", "Save Textures In VRAM", SaveTexturesInVRAM)
	
	PutINIValue(OptionFile, "Graphics", "Enable Room Lights", EnableRoomLights)
	
	PutINIValue(OptionFile, "Graphics", "Texture Details", TextureDetails)
	
	; ~ [ADVANCED]
	
	PutINIValue(OptionFile, "Advanced", "Enable HUD", HUDenabled)
	
	PutINIValue(OptionFile, "Advanced", "Show FPS", ShowFPS)
	
	PutINIValue(OptionFile, "Advanced", "Frame Limit", FrameLimit)
	
	PutINIValue(OptionFile, "Advanced", "Enable Achievement Popup", AchvMsgEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Enable Console", CanOpenConsole)
	
	PutINIValue(OptionFile, "Advanced", "Console Auto Opening", ConsoleOpening)
	
	PutINIValue(OptionFile, "Advanced", "Console Version", ConsoleVersion)
	
	PutINIValue(OptionFile, "Advanced", "Antialiased Text", AATextEnable)
	
	; ~ [CONTROLS]
	
	PutINIValue(OptionFile, "Controls", "Mouse Sensitivity", MouseSensitivity)
	
	PutINIValue(OptionFile, "Controls", "Invert Mouse By Y", InvertMouse)
	
	PutINIValue(OptionFile, "Controls", "Mouse Smoothing", MouseSmoothing)
	
	PutINIValue(OptionFile, "Controls", "Right Key", KEY_RIGHT)
	
	PutINIValue(OptionFile, "Controls", "Left Key", KEY_LEFT)
	
	PutINIValue(OptionFile, "Controls", "Up Key", KEY_UP)
	
	PutINIValue(OptionFile, "Controls", "Down Key", KEY_DOWN)
	
	PutINIValue(OptionFile, "Controls", "Blink Key", KEY_BLINK)
	
	PutINIValue(OptionFile, "Controls", "Sprint Key", KEY_SPRINT)
	
	PutINIValue(OptionFile, "Controls", "Inventory Key", KEY_INV)
	
	PutINIValue(OptionFile, "Controls", "Crouch Key", KEY_CROUCH)
	
	PutINIValue(OptionFile, "Controls", "Save Key", KEY_SAVE)
	
	PutINIValue(OptionFile, "Controls", "Console Key", KEY_CONSOLE)
	
	; ~ [AUDIO]
	
	PutINIValue(OptionFile, "Audio", "Music Volume", MusicVolume)
	
	PutINIValue(OptionFile, "Audio", "Sound Volume", PrevSFXVolume)
	
	PutINIValue(OptionFile, "Audio", "SFX Release", EnableSFXRelease)
	
	PutINIValue(OptionFile, "Audio", "Enable User Tracks", EnableUserTracks)
	
	PutINIValue(OptionFile, "Audio", "User Track Setting", UserTrackMode)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D