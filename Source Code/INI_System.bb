Function StripFileName$(File$)
	Local mi$ = "", LastSlash% = 0, i%
	
	If Len(File) > 0 Then
		For i = 1 To Len(File)
			mi = Mid(File, i, 1)
			If mi = "\" Lor mi = "/" Then
				LastSlash = i
			EndIf
		Next
	EndIf
	Return(Left(File, LastSlash))
End Function

Function StripPath$(File$) 
	Local Name$ = "", i%, mi$
	
	If Len(File) > 0 Then
		For i = Len(File) To 1 Step -1 
			mi = Mid(File, i, 1) 
			If mi = "\" Lor mi = "/" Then Return(Name)
			Name = mi + Name 
		Next 
	EndIf 
	Return(Name) 
End Function

Function Piece$(s$, Entry%, Char$ = " ")
	Local n%, p%, a$
	
	While Instr(s, Char + Char)
		s = Replace(s, Char + Char, Char)
	Wend
	For n = 1 To Entry - 1
		p = Instr(s, Char)
		s = Right(s, Len(s) - p)
	Next
	p = Instr(s, Char)
	If p < 1 Then
		a = s
	Else
		a = Left(s, p - 1)
	EndIf
	Return(a)
End Function

Function KeyValue$(Entity%, Key$, DefaultValue$ = "")
	Local p%, Value$, Properties$, TestKey$, Test$
	
	Properties = EntityName(Entity)
	Properties = Replace(Properties, Chr(13), "")
	Key = Lower(Key)
	Repeat
		p = Instr(Properties, Chr(10))
		If p Then Test = (Left(Properties, p - 1)) Else Test = Properties
		TestKey = Piece(Test, 1, "=")
		TestKey = Trim(TestKey)
		TestKey = Replace(TestKey, Chr(34), "")
		TestKey = Lower(TestKey)
		If TestKey = Key Then
			Value = Piece(Test, 2, "=")
			Value = Trim(Value)
			Value = Replace(Value, Chr(34), "")
			Return(Value)
		EndIf
		If (Not p) Then Return(DefaultValue)
		Properties = Right(Properties, Len(Properties) - p)
	Forever 
End Function

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
	Local k.INIFile
	
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
	Local k.INIFile
	
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
				If INI_sCurrentSection = INI_sUpperSection And (Not INI_bWrittenKey) Then
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
	If (Not INI_bWrittenKey) Then
		If (Not INI_bSectionFound) Then INI_CreateSection(INI_lFileHandle, INI_sSection)
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

Type Options
	; ~ [GRAPHICS]
	Field ParticleAmount%
	Field AntiAliasing%
	Field BumpEnabled%
	Field SaveTexturesInVRAM%
	Field EnableRoomLights%
	Field VSync%
	Field ScreenGamma#
	Field TextureDetails%, TextureFloat#
	Field FOV#, CurrFOV#
	; ~ [AUDIO]
	Field MusicVolume#, PrevMusicVolume#, CurrMusicVolume#
	Field EnableUserTracks%
	Field UserTrackMode%
	Field SFXVolume#, PrevSFXVolume#
	Field EnableSFXRelease%, PrevEnableSFXRelease%
	; ~ [ADVANCED]
	Field AchvMsgEnabled%
	Field CanOpenConsole%
	Field HUDEnabled%
	Field ShowFPS%
	Field ConsoleOpening%
	Field FrameLimit%, CurrFrameLimit#
	Field PlayStartup%
	Field LauncherEnabled%
	Field BarStyle%
	; ~ [CONTROLS]
	Field MouseSmoothing#
	Field InvertMouse%
	Field MouseSensitivity#
	; ~ [GLOBAL]
	Field AspectRatio#
	Field GraphicWidth%, RealGraphicWidth%
	Field GraphicHeight%, RealGraphicHeight%
	Field DisplayMode%
	Field Brightness%
	Field CameraFogNear#
	Field CameraFogFar#, StoredCameraFogFar#
	Field IntroEnabled%
End Type

Global opt.Options = New Options

Function LoadOptionsINI()
	; ~ [GRAPHICS]
	
	opt\ParticleAmount = GetINIInt(OptionFile, "Graphics", "Particle Amount")
	
	opt\AntiAliasing = GetINIInt(OptionFile, "Graphics", "Antialias")
	
	opt\BumpEnabled = GetINIInt(OptionFile, "Graphics", "Enable Bump Mapping")
	
	opt\SaveTexturesInVRAM = GetINIInt(OptionFile, "Graphics", "Save Textures In VRAM")
	
	opt\EnableRoomLights = GetINIInt(OptionFile, "Graphics", "Enable Room Lights")
	
	opt\VSync = GetINIInt(OptionFile, "Graphics", "VSync")
	
	opt\ScreenGamma = GetINIFloat(OptionFile, "Graphics", "Screen Gamma")
	
	opt\TextureDetails = GetINIInt(OptionFile, "Graphics", "Texture Details")
	
	opt\FOV = GetINIFloat(OptionFile, "Graphics", "FOV")
	opt\CurrFOV = opt\FOV - 40.0
	
	; ~ [AUDIO]
	
	opt\MusicVolume = GetINIFloat(OptionFile, "Audio", "Music Volume")
	opt\PrevMusicVolume = opt\MusicVolume
	opt\CurrMusicVolume = 1.0
	
	opt\EnableUserTracks = GetINIInt(OptionFile, "Audio", "Enable User Tracks")
	
	opt\UserTrackMode = GetINIInt(OptionFile, "Audio", "User Track Setting")
	
	opt\SFXVolume = GetINIFloat(OptionFile, "Audio", "Sound Volume")
	opt\PrevSFXVolume = opt\SFXVolume
	
	opt\EnableSFXRelease = GetINIInt(OptionFile, "Audio", "SFX Release")
	opt\PrevEnableSFXRelease = opt\EnableSFXRelease
	
	; ~ [ADVANCED]
	
	opt\AchvMsgEnabled = GetINIInt(OptionFile, "Advanced", "Enable Achievement Popup")
	
	opt\CanOpenConsole = GetINIInt(OptionFile, "Advanced", "Enable Console")
	
	opt\HUDEnabled = GetINIInt(OptionFile, "Advanced", "Enable HUD")
	
	opt\ShowFPS = GetINIInt(OptionFile, "Advanced", "Show FPS")
	
	opt\ConsoleOpening = GetINIInt(OptionFile, "Advanced", "Console Auto Opening")
	
	opt\FrameLimit = GetINIInt(OptionFile, "Advanced", "Frame Limit")
	opt\CurrFrameLimit = (opt\FrameLimit - 19.0) / 100.0
	
	opt\PlayStartup = GetINIInt(OptionFile, "Advanced", "Play Startup Videos")
	
	opt\LauncherEnabled = GetINIInt(OptionFile, "Advanced", "Launcher Enabled")
	
	opt\BarStyle = GetINIInt(OptionFile, "Advanced", "Bar Style")
	
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
	
	opt\MouseSmoothing = GetINIFloat(OptionFile, "Controls", "Mouse Smoothing", 1.0)
	
	opt\InvertMouse = GetINIInt(OptionFile, "Controls", "Invert Mouse By Y")
	
	opt\MouseSensitivity = GetINIFloat(OptionFile, "Controls", "Mouse Sensitivity")
	
	; ~ [GLOBAL]
	
	opt\GraphicWidth = GetINIInt(OptionFile, "Global", "Width")
	
	opt\GraphicHeight = GetINIInt(OptionFile, "Global", "Height")
	
	opt\DisplayMode = GetINIInt(OptionFile, "Global", "Display Mode")
	
	opt\Brightness = GetINIFloat(OptionFile, "Global", "Brightness")
	
	opt\CameraFogNear = GetINIFloat(OptionFile, "Global", "Camera Fog Near")
	
	opt\CameraFogFar = GetINIFloat(OptionFile, "Global", "Camera Fog Far")
	opt\StoredCameraFogFar = opt\CameraFogFar
	
	opt\IntroEnabled = GetINIInt(OptionFile, "Global", "Enable Intro")
End Function

Function SaveOptionsINI(SaveGlobal% = False)
	; ~ [GRAPHICS]
	
	PutINIValue(OptionFile, "Graphics", "Enable Bump Mapping", opt\BumpEnabled)
	
	PutINIValue(OptionFile, "Graphics", "Screen Gamma", opt\ScreenGamma)
	
	PutINIValue(OptionFile, "Graphics", "Antialias", opt\AntiAliasing)
	
	PutINIValue(OptionFile, "Graphics", "VSync", opt\VSync)
	
	PutINIValue(OptionFile, "Graphics", "Particle Amount", opt\ParticleAmount)
	
	PutINIValue(OptionFile, "Graphics", "Save Textures In VRAM", opt\SaveTexturesInVRAM)
	
	PutINIValue(OptionFile, "Graphics", "Enable Room Lights", opt\EnableRoomLights)
	
	PutINIValue(OptionFile, "Graphics", "Texture Details", opt\TextureDetails)
	
	PutINIValue(OptionFile, "Graphics", "FOV", Int(opt\FOV))
	
	; ~ [ADVANCED]
	
	PutINIValue(OptionFile, "Advanced", "Enable HUD", opt\HUDEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Show FPS", opt\ShowFPS)
	
	PutINIValue(OptionFile, "Advanced", "Frame Limit", opt\FrameLimit)
	
	PutINIValue(OptionFile, "Advanced", "Enable Achievement Popup", opt\AchvMsgEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Enable Console", opt\CanOpenConsole)
	
	PutINIValue(OptionFile, "Advanced", "Console Auto Opening", opt\ConsoleOpening)
	
	PutINIValue(OptionFile, "Advanced", "Play Startup Videos", opt\PlayStartup)
	
	PutINIValue(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Bar Style", opt\BarStyle)
	
	; ~ [CONTROLS]
	
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
	
	PutINIValue(OptionFile, "Controls", "Mouse Sensitivity", opt\MouseSensitivity)
	
	PutINIValue(OptionFile, "Controls", "Invert Mouse By Y", opt\InvertMouse)
	
	PutINIValue(OptionFile, "Controls", "Mouse Smoothing", opt\MouseSmoothing)
	
	; ~ [AUDIO]
	
	PutINIValue(OptionFile, "Audio", "Music Volume", opt\MusicVolume)
	
	PutINIValue(OptionFile, "Audio", "Sound Volume", opt\PrevSFXVolume)
	
	PutINIValue(OptionFile, "Audio", "SFX Release", opt\EnableSFXRelease)
	
	PutINIValue(OptionFile, "Audio", "Enable User Tracks", opt\EnableUserTracks)
	
	PutINIValue(OptionFile, "Audio", "User Track Setting", opt\UserTrackMode)
	
	If SaveGlobal Then
		; ~ [GLOBAL]
		
		PutINIValue(OptionFile, "Global", "Brightness", opt\Brightness)
		
		PutINIValue(OptionFile, "Global", "Camera Fog Near", opt\CameraFogNear)
		
		PutINIValue(OptionFile, "Global", "Camera Fog Far", opt\CameraFogFar)
		
		PutINIValue(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
	EndIf
End Function

Function ResetOptionsINI()
	; ~ [GRAPHICS]
	
	opt\BumpEnabled = 1
	
	opt\ScreenGamma = 1.0
	
	opt\AntiAliasing = 1
	
	opt\VSync = 1
	
	opt\ParticleAmount = 2
	
	opt\SaveTexturesInVRAM = 1
	
	opt\EnableRoomLights = 1
	
	opt\TextureDetails = 3
	
	opt\CurrFOV = 34.0 ; ~ Don't forget to decrease by "40.0"
	opt\FOV = 74.0
	
	; ~ [ADVANCED]
	
	opt\HUDEnabled = 1
	
	opt\ShowFPS = 0
	
	opt\CurrFrameLimit = 0.0
	opt\FrameLimit = 0
	
	opt\AchvMsgEnabled = 1
	
	opt\CanOpenConsole = 0
	
	opt\ConsoleOpening = 0
	
	opt\PlayStartup = 1
	
	opt\LauncherEnabled = 1
	
	opt\BarStyle = 0
	
	; ~ [CONTROLS]
	
	opt\MouseSensitivity = 0.0
	
	opt\InvertMouse = 0
	
	opt\MouseSmoothing = 1.0
	
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
	
	opt\MusicVolume = 0.5
	opt\PrevMusicVolume = 0.5
	
	opt\SFXVolume = 0.5
	opt\PrevSFXVolume = 0.5
	
	opt\EnableSFXRelease = 1
	
	opt\EnableUserTracks = 0
	
	opt\UserTrackMode = 0
	
	; ~ [GLOBAL]
	
	opt\Brightness = 50
	
	opt\CameraFogNear = 0.1
	
	opt\CameraFogFar = 6.0
	
	opt\IntroEnabled = 1
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D