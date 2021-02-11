Type INIFile
	Field Name$
	Field Bank%
	Field BankOffset% = 0
	Field Size%
End Type

Function ReadINILine$(ini.INIFile)
	Local ReadByte_%
	Local FirstByte% = True
	Local Offset% = ini\BankOffset
	Local Bank% = ini\Bank
	Local RetStr$ = ""
	
	ReadByte_ = PeekByte(Bank, Offset)
	While ((FirstByte) Lor ((ReadByte_ <> 13) And (ReadByte_ <> 10))) And (Offset < ini\Size)
		ReadByte_ = PeekByte(Bank, Offset)
		If ReadByte_ <> 13 And ReadByte_ <> 10 Then
			FirstByte = False
			RetStr = RetStr + Chr(ReadByte_)
		EndIf
		Offset = Offset + 1
	Wend
	ini\BankOffset = Offset
	
	Return(RetStr)
End Function

Function UpdateINIFile$(File$)
	Local ini.INIFile = Null
	Local k.INIFile
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(File) Then
			ini = k
			Exit
		EndIf
	Next
	
	If ini = Null Then Return
	If ini\Bank <> 0 Then FreeBank(ini\Bank)
	
	Local f% = ReadFile(ini\Name)
	Local FileSize_% = 1
	
	While FileSize_ < FileSize(ini\Name)
		FileSize_ = FileSize_ * 2
	Wend
	ini\Bank = CreateBank(FileSize_)
	ini\Size = 0
	While (Not Eof(f))
		PokeByte(ini\Bank, ini\Size, ReadByte(f))
		ini\Size = ini\Size + 1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Local ini.INIFile = Null
	Local k.INIFile
	Local TemporaryString$ = ""
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(File) Then
			ini = k
			Exit
		EndIf
	Next
	
	If ini = Null Then
		ini.INIFile = New INIFile
		ini\Name = Lower(File)
		ini\Bank = 0
		UpdateINIFile(ini\Name)
	EndIf
	
	ini\BankOffset = 0
	
	Section = Lower(Section)
	
	While ini\BankOffset < ini\Size
		Local StrTemp$ = ReadINILine(ini)
		
		If Left(StrTemp, 1) = "[" Then
			StrTemp = Lower(StrTemp)
			If Mid(StrTemp, 2, Len(StrTemp) - 2) = Section Then
				Repeat
					TemporaryString = ReadINILine(ini)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(Parameter) Then
						Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
					EndIf
				Until (Left(TemporaryString, 1) = "[") Lor (ini\BankOffset >= ini\Size)
				Return(DefaultValue)
			EndIf
		EndIf
	Wend
	Return(DefaultValue)
End Function

Function GetINIInt%(File$, Section$, Parameter$, DefaultValue% = 0)
	Local StrTemp$ = GetINIString(File, Section, Parameter, DefaultValue)
	
	Select StrTemp
		Case "True"
			;[Block]
			Return(True)
			;[End Block]
		Case "False"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(StrTemp))
			;[End Block]
	End Select
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
	Local StrTemp$ = GetINIString2(File, Start, Parameter, DefaultValue)
	
	Select StrTemp
		Case "True"
			;[Block]
			Return(True)
			;[End Block]
		Case "False"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(StrTemp))
			;[End Block]
	End Select
End Function

Function GetINIFloat2#(File$, Section$, Parameter$, DefaultValue# = 0.0)
	Return(Float(GetINIString2(File, Section, Parameter, DefaultValue)))
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

Function INI_FileToString$(INI_sFilename$)
	Local INI_sString$ = ""
	Local INI_lFileHandle% = ReadFile(INI_sFilename)
	
	If INI_lFileHandle <> 0 Then
		While (Not Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine(INI_lFileHandle) + Chr(0)
		Wend
		CloseFile(INI_lFileHandle)
	EndIf
	Return(INI_sString)
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine(INI_lFileHandle, "") ; ~ Blank line between sections
	WriteLine(INI_lFileHandle, INI_sNewSection)
	Return(INI_sNewSection)
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine(INI_lFileHandle, INI_sKey + " = " + INI_sValue)
	Return(True)
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
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY = VALUE line
	If (Not INI_bWrittenKey) Then
		If (Not INI_bSectionFound) Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	EndIf
	CloseFile(INI_lFileHandle)
	Return(True) ; ~ Success
End Function

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

Function GetNPCManipulationValue$(NPC$, Bone$, Section$, ValueType% = 0)
	; ~ Valuetype determines what type of variable should the Output be returned
	; ~ 0: String
	; ~ 1: Int
	; ~ 2: Float
	; ~ 3: Boolean
	
	Local Value$ = GetINIString("Data\NPCBones.ini", NPC, Bone + "_" + Section)
	
	Select ValueType%
		Case 0
			;[Block]
			Return(Value)
			;[End Block]
		Case 1
			;[Block]
			Return(Int(Value))
			;[End Block]
		Case 2
			;[Block]
			Return(Float(Value))
			;[End Block]
		Case 3
			;[Block]
			If Value = "True" Lor Value = "1"
				Return(True)
			Else
				Return(False)
			EndIf
			;[End Block]
	End Select
End Function

Global OptionFile$ = GetEnv("AppData") + "\scpcb-ue\Data\options.ini"

Type Options
	; ~ [GRAPHICS]
	Field ParticleAmount%
	Field AntiAliasing%
	Field BumpEnabled%
	Field SaveTexturesInVRAM%
	Field EnableRoomLights%
	Field VSync%
	Field ScreenGamma#
	Field TextureDetails%, TextureDetailsLevel#
	Field FOV#, CurrFOV#
	Field Anisotropic%, AnisotropicLevel%
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
	Field SmoothHUD%
	Field ShowFPS%
	Field ConsoleOpening%
	Field FrameLimit%, CurrFrameLimit#
	Field PlayStartup%
	Field LauncherEnabled%
	Field EnableSubtitles%
	Field SubColorR%, SubColorG%, SubColorB%
	; ~ [CONTROLS]
	Field MouseSmoothing#
	Field InvertMouse%
	Field MouseSensitivity#
	; ~ [GLOBAL]
	Field AspectRatio#
	Field GraphicWidth%, RealGraphicWidth%
	Field GraphicHeight%, RealGraphicHeight%
	Field DisplayMode%
	Field CameraFogNear#
	Field CameraFogFar#, StoredCameraFogFar#
	Field IntroEnabled%
	Field DebugMode%
End Type

Global opt.Options = New Options

Function LoadOptionsINI()
	; ~ [GRAPHICS]
	
	opt\ParticleAmount = GetINIInt(OptionFile, "Graphics", "Particle Amount", 2)
	
	opt\AntiAliasing = GetINIInt(OptionFile, "Graphics", "Anti-Aliasing", True)
	
	opt\BumpEnabled = GetINIInt(OptionFile, "Graphics", "Enable Bump Mapping", True)
	
	opt\SaveTexturesInVRAM = GetINIInt(OptionFile, "Graphics", "Save Textures In VRAM", True)
	
	opt\EnableRoomLights = GetINIInt(OptionFile, "Graphics", "Enable Room Lights", True)
	
	opt\VSync = GetINIInt(OptionFile, "Graphics", "VSync", True)
	
	opt\ScreenGamma = GetINIFloat(OptionFile, "Graphics", "Screen Gamma", 1.0)
	
	opt\TextureDetails = GetINIInt(OptionFile, "Graphics", "Texture Details", 4)
	
	opt\FOV = GetINIFloat(OptionFile, "Graphics", "FOV", 74.0)
	opt\CurrFOV = opt\FOV - 40.0
	
	opt\Anisotropic = GetINIInt(OptionFile, "Graphics", "Anisotropic Filtering", 4)
	
	; ~ [AUDIO]
	
	opt\MusicVolume = GetINIFloat(OptionFile, "Audio", "Music Volume", 0.5)
	opt\PrevMusicVolume = opt\MusicVolume
	opt\CurrMusicVolume = 1.0
	
	opt\EnableUserTracks = GetINIInt(OptionFile, "Audio", "Enable User Tracks", False)
	
	opt\UserTrackMode = GetINIInt(OptionFile, "Audio", "User Track Setting", False)
	
	opt\SFXVolume = GetINIFloat(OptionFile, "Audio", "Sound Volume", 0.5)
	opt\PrevSFXVolume = opt\SFXVolume
	
	opt\EnableSFXRelease = GetINIInt(OptionFile, "Audio", "SFX Release", True)
	opt\PrevEnableSFXRelease = opt\EnableSFXRelease
	
	; ~ [ADVANCED]
	
	opt\AchvMsgEnabled = GetINIInt(OptionFile, "Advanced", "Enable Achievement Popup", True)
	
	opt\CanOpenConsole = GetINIInt(OptionFile, "Advanced", "Enable Console", False)
	
	opt\HUDEnabled = GetINIInt(OptionFile, "Advanced", "Enable HUD", True)
	
	opt\SmoothHUD = GetINIInt(OptionFile, "Advanced", "Smooth HUD", True)
	
	opt\ShowFPS = GetINIInt(OptionFile, "Advanced", "Show FPS", False)
	
	opt\ConsoleOpening = GetINIInt(OptionFile, "Advanced", "Console Auto Opening", False)
	
	opt\FrameLimit = GetINIInt(OptionFile, "Advanced", "Frame Limit", 0.0)
	opt\CurrFrameLimit = (opt\FrameLimit - 19.0) / 100.0
	
	opt\PlayStartup = GetINIInt(OptionFile, "Advanced", "Play Startup Videos", True)
	
	opt\LauncherEnabled = GetINIInt(OptionFile, "Advanced", "Launcher Enabled", True)
	
	opt\EnableSubtitles = GetINIInt(OptionFile, "Advanced", "Enable Subtitles", True)
	
	opt\SubColorR = GetINIInt(OptionFile, "Advanced", "Subtitles Color R", 255)
	
	opt\SubColorG = GetINIInt(OptionFile, "Advanced", "Subtitles Color G", 255)
	
	opt\SubColorB = GetINIInt(OptionFile, "Advanced", "Subtitles Color B", 255)
	
	; ~ [CONTROLS]
	
	key\MOVEMENT_RIGHT = GetINIInt(OptionFile, "Controls", "Right Key", 32)
	
	key\MOVEMENT_LEFT = GetINIInt(OptionFile, "Controls", "Left Key", 30)
	
	key\MOVEMENT_UP = GetINIInt(OptionFile, "Controls", "Up Key", 17)
	
	key\MOVEMENT_DOWN = GetINIInt(OptionFile, "Controls", "Down Key", 31)
	
	key\BLINK = GetINIInt(OptionFile, "Controls", "Blink Key", 57)
	
	key\SPRINT = GetINIInt(OptionFile, "Controls", "Sprint Key", 42)
	
	key\INVENTORY = GetINIInt(OptionFile, "Controls", "Inventory Key", 15)
	
	key\CROUCH = GetINIInt(OptionFile, "Controls", "Crouch Key", 29)
	
	key\SAVE = GetINIInt(OptionFile, "Controls", "Save Key", 63)
	
	key\CONSOLE = GetINIInt(OptionFile, "Controls", "Console Key", 61)
	
	key\SCREENSHOT = GetINIInt(OptionFile, "Controls", "Screenshot Key", 59)
	
	opt\MouseSmoothing = GetINIFloat(OptionFile, "Controls", "Mouse Smoothing", 1.0)
	
	opt\InvertMouse = GetINIInt(OptionFile, "Controls", "Invert Mouse By Y-Axis", False)
	
	opt\MouseSensitivity = GetINIFloat(OptionFile, "Controls", "Mouse Sensitivity", 0.0)
	
	; ~ [GLOBAL]
	
	opt\GraphicWidth = GetINIInt(OptionFile, "Global", "Width", 1280)
	
	opt\GraphicHeight = GetINIInt(OptionFile, "Global", "Height", 1024)
	
	opt\DisplayMode = GetINIInt(OptionFile, "Global", "Display Mode", 0)
	
	opt\CameraFogNear = GetINIFloat(OptionFile, "Global", "Camera Fog Near", 0.1)
	
	opt\CameraFogFar = GetINIFloat(OptionFile, "Global", "Camera Fog Far", 6.0)
	opt\StoredCameraFogFar = opt\CameraFogFar
	
	opt\IntroEnabled = GetINIInt(OptionFile, "Global", "Enable Intro", True)
	
	opt\DebugMode = GetINIInt(OptionFile, "Global", "Debug Mode", False)
End Function

Function SaveOptionsINI(SaveGlobal% = False)
	; ~ [GRAPHICS]
	
	PutINIValue(OptionFile, "Graphics", "Enable Bump Mapping", opt\BumpEnabled)
	
	PutINIValue(OptionFile, "Graphics", "Screen Gamma", opt\ScreenGamma)
	
	PutINIValue(OptionFile, "Graphics", "Anti-Aliasing", opt\AntiAliasing)
	
	PutINIValue(OptionFile, "Graphics", "VSync", opt\VSync)
	
	PutINIValue(OptionFile, "Graphics", "Particle Amount", opt\ParticleAmount)
	
	PutINIValue(OptionFile, "Graphics", "Save Textures In VRAM", opt\SaveTexturesInVRAM)
	
	PutINIValue(OptionFile, "Graphics", "Enable Room Lights", opt\EnableRoomLights)
	
	PutINIValue(OptionFile, "Graphics", "Texture Details", opt\TextureDetails)
	
	PutINIValue(OptionFile, "Graphics", "FOV", Int(opt\FOV))
	
	PutINIValue(OptionFile, "Graphics", "Anisotropic Filtering", opt\Anisotropic)
	
	; ~ [ADVANCED]
	
	PutINIValue(OptionFile, "Advanced", "Enable HUD", opt\HUDEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Smooth HUD", opt\SmoothHUD)
	
	PutINIValue(OptionFile, "Advanced", "Show FPS", opt\ShowFPS)
	
	PutINIValue(OptionFile, "Advanced", "Frame Limit", opt\FrameLimit)
	
	PutINIValue(OptionFile, "Advanced", "Enable Achievement Popup", opt\AchvMsgEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Enable Console", opt\CanOpenConsole)
	
	PutINIValue(OptionFile, "Advanced", "Console Auto Opening", opt\ConsoleOpening)
	
	PutINIValue(OptionFile, "Advanced", "Play Startup Videos", opt\PlayStartup)
	
	PutINIValue(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	
	PutINIValue(OptionFile, "Advanced", "Enable Subtitles", opt\EnableSubtitles)
	
	PutINIValue(OptionFile, "Advanced", "Subtitles Color R", opt\SubColorR)
	
	PutINIValue(OptionFile, "Advanced", "Subtitles Color G", opt\SubColorG)
	
	PutINIValue(OptionFile, "Advanced", "Subtitles Color B", opt\SubColorB)
	
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
	
	PutINIValue(OptionFile, "Controls", "Invert Mouse By Y-Axis", opt\InvertMouse)
	
	PutINIValue(OptionFile, "Controls", "Mouse Smoothing", opt\MouseSmoothing)
	
	; ~ [AUDIO]
	
	PutINIValue(OptionFile, "Audio", "Music Volume", opt\MusicVolume)
	
	PutINIValue(OptionFile, "Audio", "Sound Volume", opt\PrevSFXVolume)
	
	PutINIValue(OptionFile, "Audio", "SFX Release", opt\EnableSFXRelease)
	
	PutINIValue(OptionFile, "Audio", "Enable User Tracks", opt\EnableUserTracks)
	
	PutINIValue(OptionFile, "Audio", "User Track Setting", opt\UserTrackMode)
	
	If SaveGlobal Then
		; ~ [GLOBAL]
		
		PutINIValue(OptionFile, "Global", "Camera Fog Near", opt\CameraFogNear)
		
		PutINIValue(OptionFile, "Global", "Camera Fog Far", opt\CameraFogFar)
		
		PutINIValue(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
	EndIf
End Function

Function ResetOptionsINI()
	; ~ [GRAPHICS]
	
	opt\BumpEnabled = True
	
	opt\ScreenGamma = 1.0
	
	If opt\DisplayMode = 0 Then opt\AntiAliasing = True
	
	opt\VSync = True
	
	opt\ParticleAmount = 2
	
	opt\SaveTexturesInVRAM = True
	
	opt\EnableRoomLights = True
	
	opt\TextureDetails = 4
	
	opt\CurrFOV = 34.0 ; ~ Don't forget to decrease by "40.0"
	opt\FOV = 74.0
	
	opt\Anisotropic = 4
	
	; ~ [ADVANCED]
	
	opt\HUDEnabled = True
	
	opt\SmoothHUD = True
	
	opt\ShowFPS = False
	
	opt\CurrFrameLimit = 0.0
	opt\FrameLimit = 0
	
	opt\AchvMsgEnabled = True
	
	opt\CanOpenConsole = False
	
	opt\ConsoleOpening = False
	
	opt\PlayStartup = True
	
	opt\LauncherEnabled = True
	
	opt\EnableSubtitles = True
	
	opt\SubColorR = 255
	
	opt\SubColorG = 255
	
	opt\SubColorB = 255
	
	; ~ [CONTROLS]
	
	opt\MouseSensitivity = 0.0
	
	opt\InvertMouse = False
	
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
	
	opt\EnableSFXRelease = True
	
	opt\EnableUserTracks = False
	
	opt\UserTrackMode = False
	
	; ~ [GLOBAL]
	
	opt\CameraFogNear = 0.1
	
	opt\CameraFogFar = 6.0
	
	opt\IntroEnabled = True
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D