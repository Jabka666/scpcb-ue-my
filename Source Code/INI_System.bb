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
	While ((FirstByte) Lor ((RdByte <> 13) And (RdByte <> 10))) And (Offset < file\Size)
		RdByte = PeekByte(Bank, Offset)
		If RdByte <> 13 And RdByte <> 10 Then
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
	While (Not Eof(f))
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
		lfile.INIFile = New INIFile
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
				Until (Left(TemporaryString, 1) = "[") Lor (lfile\BankOffset >= lfile\Size)
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
	
	While (Not Eof(f))
		Local StrTemp$ = ReadLine(f)
		
		n = n + 1
		If n = Start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(Parameter) Then
					CloseFile(f)
					Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
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
	
	While (Not Eof(f))
		Local StrTemp$ = ReadLine(f)
		
		n = n + 1
		If Left(StrTemp, 1) = "[" Then
			StrTemp = Lower(StrTemp)
			Temp = Instr(StrTemp, Section)
			If Temp > 0 Then
				If Mid(StrTemp, Temp - 1, 1) = "[" Lor Mid(StrTemp, Temp - 1, 1) = "|" Then
					CloseFile(f)
					Return(n)
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function PutINIValue%(File$, INI_sSection$, INI_sKey$, INI_sValue$)
	; ~ Returns: True (Success) or False (Failed)
	INI_sSection = "[" + Trim(INI_sSection) + "]"
	
	Local INI_sUpperSection$ = Upper(INI_sSection)
	
	INI_sKey = Trim(INI_sKey)
	INI_sValue = Trim(INI_sValue)
	
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
	
	While INI_lPos <> 0
		Local INI_sTemp$ = Mid(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If INI_sTemp <> "" Then
			If Left(INI_sTemp, 1) = "[" And Right(INI_sTemp, 1) = "]" Then
				; ~ Process SECTION
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				EndIf
				INI_sCurrentSection = Upper(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If INI_sCurrentSection = INI_sUpperSection Then INI_bSectionFound = True
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine(INI_lFileHandle, INI_sTemp)
				Else
					; ~ KEY = VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					
					If lEqualsPos <> 0 Then
						If INI_sCurrentSection = INI_sUpperSection And Upper(Trim(Left(INI_sTemp, (lEqualsPos - 1)))) = Upper(INI_sKey) Then
							If INI_sValue <> "" Then INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
							INI_bWrittenKey = True
						Else
							WriteLine(INI_lFileHandle, INI_sTemp)
						EndIf
					EndIf
				EndIf
			EndIf	
		EndIf
		; ~ Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos = Instr(INI_sContents, Chr(0), INI_lOldPos)
	Wend
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION If required and create our KEY = VALUE line
	If (INI_bWrittenKey = False) Then
		If INI_bSectionFound = False Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	EndIf
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
	EndIf
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

Const OptionFile$ = "Data\options.ini"

; ~ [GRAPHICS]

Global ParticleAmount% = GetINIInt(OptionFile, "Graphics", "Particle Amount")

Global Opt_AntiAlias% = GetINIInt(OptionFile, "Graphics", "Antialias")

Global BumpEnabled% = GetINIInt(OptionFile, "Graphics", "Enable Bump Mapping")

Global SaveTexturesInVRAM% = GetINIInt(OptionFile, "Graphics", "Save Textures In VRAM")

Global EnableRoomLights% = GetINIInt(OptionFile, "Graphics", "Enable Room Lights")

Global VSync% = GetINIInt(OptionFile, "Graphics", "VSync")

Global ScreenGamma# = GetINIFloat(OptionFile, "Graphics", "Screen gamma")

Global TextureDetails% = GetINIInt(OptionFile, "Graphics", "Texture details")

Global FOV# = GetINIFloat(OptionFile, "Graphics", "FOV")

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

Global HUDEnabled% = GetINIInt(OptionFile, "Advanced", "Enable HUD")

Global ShowFPS% = GetINIInt(OptionFile, "Advanced", "Show FPS")

Global ConsoleOpening% = GetINIInt(OptionFile, "Advanced", "Console Auto Opening")

Global FrameLimit% = GetINIInt(OptionFile, "Advanced", "Frame Limit")

Global ConsoleVersion% = GetINIInt(OptionFile, "Advanced", "Console Version")

Global PlayStartup% = GetINIInt(OptionFile, "Advanced", "Play Startup Videos")

Global LauncherEnabled% = GetINIInt(OptionFile, "Advanced", "Launcher Enabled")

Global BarStyle% = GetINIInt(OptionFile, "Advanced", "Bar Style")

; ~ [CONTROLS]

key\MOVEMENT_RIGHT = GetINIInt(OptionFile, "Controls", "Right Key")

key\MOVEMENT_LEFT = GetINIInt(OptionFile, "Controls", "Left Key")

key\MOVEMENT_UP = GetINIInt(OptionFile, "Controls", "Up Key")

key\MOVEMENT_DOWN = GetINIInt(OptionFile, "Controls", "Down Key")

key\BLINK = GetINIInt(OptionFile, "Controls", "Blink Key")

key\SPRINT = GetINIInt(OptionFile, "Controls", "Sprint Key")

key\INVENTORY = GetINIInt(OptionFile, "Controls", "Inventory Key")

key\CROUCH = GetINIInt(OptionFile, "Controls", "Crouch Key")

key\SAVE = GetINIInt(OptionFile, "Controls", "Save Key")

key\CONSOLE = GetINIInt(OptionFile, "Controls", "Console Key")

key\SCREENSHOT = GetINIInt(OptionFile, "Controls", "Screenshot Key")

Global MouseSmoothing# = GetINIFloat(OptionFile, "Controls", "Mouse Smoothing", 1.0)

Global InvertMouse% = GetINIInt(OptionFile, "Controls", "Invert Mouse By Y")

Global MouseSensitivity# = GetINIFloat(OptionFile, "Controls", "Mouse Sensitivity")

; ~ [GLOBAL]

Global GraphicWidth% = GetINIInt(OptionFile, "Global", "Width")

Global GraphicHeight% = GetINIInt(OptionFile, "Global", "Height")

Global DisplayMode% = GetINIInt(OptionFile, "Global", "Display Mode")

Global Brightness% = GetINIFloat(OptionFile, "Global", "Brightness")

Global CameraFogNear# = GetINIFloat(OptionFile, "Global", "Camera Fog Near")

Global CameraFogFar# = GetINIFloat(OptionFile, "Global", "Camera Fog Far")

Global IntroEnabled% = GetINIInt(OptionFile, "Global", "Enable Intro")

Function SaveOptionsINI(SaveGlobal% = False)
	; ~ [GRAPHICS]
	
	PutINIValue(OptionFile, "Graphics", "Enable Bump Mapping", BumpEnabled)
	
	PutINIValue(OptionFile, "Graphics", "Screen Gamma", ScreenGamma)
	
	PutINIValue(OptionFile, "Graphics", "Antialias", Opt_AntiAlias)
	
	PutINIValue(OptionFile, "Graphics", "VSync", VSync)
	
	PutINIValue(OptionFile, "Graphics", "Particle Amount", ParticleAmount)
	
	PutINIValue(OptionFile, "Graphics", "Save Textures In VRAM", SaveTexturesInVRAM)
	
	PutINIValue(OptionFile, "Graphics", "Enable Room Lights", EnableRoomLights)
	
	PutINIValue(OptionFile, "Graphics", "Texture Details", TextureDetails)
	
	PutINIValue(OptionFile, "Graphics", "FOV", Int(FOV))
	
	; ~ [ADVANCED]
	
	PutINIValue(OptionFile, "Advanced", "Enable HUD", HUDEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Show FPS", ShowFPS)
	
	PutINIValue(OptionFile, "Advanced", "Frame Limit", FrameLimit)
	
	PutINIValue(OptionFile, "Advanced", "Enable Achievement Popup", AchvMsgEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Enable Console", CanOpenConsole)
	
	PutINIValue(OptionFile, "Advanced", "Console Auto Opening", ConsoleOpening)
	
	PutINIValue(OptionFile, "Advanced", "Console Version", ConsoleVersion)
	
	PutINIValue(OptionFile, "Advanced", "Play Startup Videos", PlayStartup)
	
	PutINIValue(OptionFile, "Advanced", "Launcher Enabled", LauncherEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Bar Style", BarStyle)
	
	; ~ [CONTROLS]
	
	PutINIValue(OptionFile, "Controls", "Mouse Sensitivity", MouseSensitivity)
	
	PutINIValue(OptionFile, "Controls", "Invert Mouse By Y", InvertMouse)
	
	PutINIValue(OptionFile, "Controls", "Mouse Smoothing", MouseSmoothing)
	
	PutINIValue(OptionFile, "Controls", "Right Key", key\MOVEMENT_RIGHT)
	
	PutINIValue(OptionFile, "Controls", "Left Key", key\MOVEMENT_LEFT)
	
	PutINIValue(OptionFile, "Controls", "Up Key", key\MOVEMENT_UP)
	
	PutINIValue(OptionFile, "Controls", "Down Key", key\MOVEMENT_DOWN)
	
	PutINIValue(OptionFile, "Controls", "Blink Key", key\BLINK)
	
	PutINIValue(OptionFile, "Controls", "Sprint Key", key\SPRINT)
	
	PutINIValue(OptionFile, "Controls", "Inventory Key", key\INVENTORY)
	
	PutINIValue(OptionFile, "Controls", "Crouch Key", key\CROUCH)
	
	PutINIValue(OptionFile, "Controls", "Save Key", key\SAVE)
	
	PutINIValue(OptionFile, "Controls", "Console Key", key\CONSOLE)
	
	PutINIValue(OptionFile, "Controls", "Screenshot Key", key\SCREENSHOT)
	
	; ~ [AUDIO]
	
	PutINIValue(OptionFile, "Audio", "Music Volume", MusicVolume)
	
	PutINIValue(OptionFile, "Audio", "Sound Volume", PrevSFXVolume)
	
	PutINIValue(OptionFile, "Audio", "SFX Release", EnableSFXRelease)
	
	PutINIValue(OptionFile, "Audio", "Enable User Tracks", EnableUserTracks)
	
	PutINIValue(OptionFile, "Audio", "User Track Setting", UserTrackMode)
	
	If SaveGlobal Then
		; ~ [GLOBAL]
		
		PutINIValue(OptionFile, "Global", "Brightness", Brightness)
		
		PutINIValue(OptionFile, "Global", "Camera Fog Near", CameraFogNear)
		
		PutINIValue(OptionFile, "Global", "Camera Fog Far", CameraFogFar)
		
		PutINIValue(OptionFile, "Global", "Enable Intro", IntroEnabled)
	EndIf
End Function

Function ResetOptionsINI()
	; ~ [GRAPHICS]
	
	BumpEnabled = 1
	
	ScreenGamma = 1.0
	
	Opt_AntiAlias = 1
	
	VSync = 1
	
	ParticleAmount = 2
	
	SaveTexturesInVRAM = 0
	
	EnableRoomLights = 1
	
	TextureDetails = 3
	
	CurrFOV = 34.0 ; ~ Don't forget to decrease by "40.0"
	FOV = 74.0
	
	; ~ [ADVANCED]
	
	HUDEnabled = 1
	
	ShowFPS = 0
	
	CurrFrameLimit = 0.0
	FrameLimit = 0
	
	AchvMsgEnabled = 1
	
	CanOpenConsole = 0
	
	ConsoleOpening = 0
	
	ConsoleVersion = 1
	
	PlayStartup = 1
	
	LauncherEnabled = 1
	
	BarStyle = 1
	
	; ~ [CONTROLS]
	
	MouseSensitivity = 0.0
	
	InvertMouse = 0
	
	MouseSmoothing = 1.0
	
	key\MOVEMENT_RIGHT = 32
	
	key\MOVEMENT_LEFT = 30
	
	key\MOVEMENT_UP = 17
	
	key\MOVEMENT_DOWN = 31
	
	key\BLINK = 57
	
	key\SPRINT = 42
	
	key\INVENTORY = 15
	
	key\CROUCH = 29
	
	key\SAVE = 63
	
	key\CONSOLE = 61
	
	key\SCREENSHOT = 59
	
	; ~ [AUDIO]
	
	MusicVolume = 0.5
	
	PrevSFXVolume = 0.5
	SFXVolume = 0.5
	
	EnableSFXRelease = 1
	
	EnableUserTracks = 0
	
	UserTrackMode = 0
	
	; ~ [GLOBAL]
	
	Brightness = 50
	
	CameraFogNear = 0.1
	
	CameraFogFar = 6.0
	
	IntroEnabled = 1
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D