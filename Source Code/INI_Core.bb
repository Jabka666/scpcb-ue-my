; ~ IniControler - A part of BlitzToolBox
; ~ Write & Read ini file.
; ~ v1.06 2022.11.12
; ~ v1.06.2 2023.9.10
; ~ https://github.com/ZiYueCommentary/BlitzToolbox

Function IniWriteBuffer%(File$, ClearPrevious% = True)
	IniWriteBuffer_(File, ClearPrevious)
End Function

Function IniGetBufferString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Return(IniGetBufferString_(File, Section, Parameter, DefaultValue))
End Function

Function IniWriteString%(File$, Section$, Parameter$, Value$, UpdateBuffer% = True)
	IniWriteString_(File, Section, Parameter, Value, UpdateBuffer)
End Function

Function IniWriteInt%(File$, Section$, Parameter$, Value%, UpdateBuffer% = True)
	IniWriteInt_(File, Section, Parameter, Value, UpdateBuffer)
End Function

Function IniWriteFloat%(File$, Section$, Parameter$, Value#, UpdateBuffer% = True)
	IniWriteFloat_(File, Section, Parameter, Value, UpdateBuffer)
End Function

Function IniGetString$(File$, Section$, Parameter$, DefaultValue$ = "", AllowBuffer% = True)
	Return(IniGetString_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function IniGetInt%(File$, Section$, Parameter$, DefaultValue% = 0, AllowBuffer% = True)
	Local Result$ = IniGetString(File, Section, Parameter, DefaultValue, AllowBuffer)
	
	Select Result
		Case "True", "true", "1"
			;[Block]
			Return(True)
			;[End Block]
		Case "False", "false", "0"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(Result))
			;[End Block]
	End Select
End Function

Function IniGetFloat#(File$, Section$, Parameter$, DefaultValue# = 0.0, AllowBuffer% = True)
	Return(IniGetFloat_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function IniSectionExist%(File$, Section$, AllowBuffer% = True)
	Return(IniSectionExist_(File, Section, AllowBuffer))
End Function

Function GetFileLocalString$(File$, Name$, Parameter$, DefaultValue$ = "", CheckRootFile% = True)
	Local DefaultValue1$
	
	If CheckRootFile
		DefaultValue1 = IniGetBufferString(File, Name, Parameter, DefaultValue)
	Else
		DefaultValue1 = DefaultValue
	EndIf

	Return(IniGetBufferString(lang\LanguagePath + File, Name, Parameter, DefaultValue1))
End Function

Function GetLocalString$(Section$, Parameter$)
	Return(GetFileLocalString(LanguageFile, Section, Parameter, Section + "," + Parameter))
End Function

Function Format$(String_$, Parameter$, Replace_$ = "%s")
	Return(Replace(String_, Replace_, Parameter))
End Function

Function StringToBoolean%(String_$, DefaultValue% = False)
	Select String_
		Case "True", "true", "1"
			;[Block]
			Return(True)
			;[End Block]
		Case "False", "false", "0"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(DefaultValue)
			;[End Block]
	End Select
End Function

Function StripFileName$(File$)
	Local LastSlash% = 0
	Local FileLen% = Len(File)
	Local i%

	If FileLen = 0 Then Return("")

	For i = FileLen To 1 Step -1
		Local Middle$ = Mid(File, i, 1)
		
		If Middle = "\" Lor Middle = "/" ; ~ Detect a delimiter
			LastSlash = i
			Exit
		EndIf
	Next
	Return(Left(File, LastSlash))
End Function

Function StripPath$(File$)
	Local LastSlash% = 0
	Local FileLen% = Len(File)
	Local i%
	
	If FileLen = 0 Then Return("")
	
	For i = FileLen To 1 Step -1
		Local Middle$ = Mid(File, i, 1)
		
		If Middle = "\" Lor Middle = "/" ; ~ Detect a delimiter
			LastSlash = i
			Exit
		EndIf
	Next
	Return(Right(File, FileLen - LastSlash))
End Function

Function Piece$(s$, Entry%, Char$ = " ")
	Local n%, p%, a$
	
	While Instr(s, Char + Char)
		s = Replace(s, Char + Char, Char)
	Wend
	For n = 1 To Entry - 1
		p = Instr(s, Char)
		s = Mid(s, p + 1)
	Next
	p = Instr(s, Char)
	If p < 1
		a = s
	Else
		a = Left(s, p - 1)
	EndIf
	Return(a)
End Function

Function GetNPCManipulationValue$(NPC$, Bone$, Section$, ValueType% = 0)
	; ~ Valuetype determines what type of variable should the Output be returned
	; ~ 0: String
	; ~ 1: Int
	; ~ 2: Float
	; ~ 3: Boolean
	
	Local Value$ = IniGetString("Data\NPCBones.ini", NPC, Bone + "_" + Section)
	
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
			Return(StringToBoolean(Value))
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
	Field AdvancedRoomLights%
	Field VSync%
	Field ScreenGamma#, PrevScreenGamma#
	Field TextureDetails%, TextureDetailsLevel#
	Field FOV#, CurrFOV#
	Field Anisotropic%, AnisotropicLevel%
	Field SecurityCamRenderInterval%, SecurityCamRenderIntervalLevel#
	; ~ [AUDIO]
	Field MasterVolume#, PrevMasterVolume#
	Field MusicVolume#, CurrMusicVolume#
	Field SFXVolume#
	Field VoiceVolume#
	Field EnableSFXRelease%, PrevEnableSFXRelease%
	Field UserTrackMode%
	Field EnableSubtitles%
	Field OverrideSubColor%
	Field SubColorR%, SubColorG%, SubColorB%
	; ~ [ADVANCED]
	Field AchvMsgEnabled%
	Field CanOpenConsole%
	Field HUDEnabled%
	Field SmoothBars%
	Field ShowFPS%
	Field ConsoleOpening%
	Field FrameLimit%, CurrFrameLimit#
	Field AutoSaveEnabled%
	Field TextShadow%
	Field PlayStartup%
	Field LauncherEnabled%
	Field NoProgressBar%
	; ~ [CONTROLS]
	Field MouseSmoothing#
	Field InvertMouseX%, InvertMouseY%
	Field MouseSensitivity#
	; ~ [GLOBAL]
	Field AspectRatio#
	Field GraphicWidth%, RealGraphicWidth%
	Field GraphicHeight%, RealGraphicHeight%
	Field DisplayMode%
	Field GFXDriver%
	Field IntroEnabled%
	Field DebugMode%
	Field Language$
	Field GFXDriversAmount%
	Field TotalVidMemory%, TotalPhysMemory%
End Type

Global opt.Options = New Options

opt\GFXDriversAmount = CountGfxDrivers()
opt\TotalVidMemory% = TotalVidMem() / 1024
opt\TotalPhysMemory% = TotalPhys() / 1024

Function LoadOptionsINI%()
	; ~ [GRAPHICS]
	
	opt\BumpEnabled = IniGetInt(OptionFile, "Graphics", "Enable Bump Mapping", True)
	
	opt\VSync = IniGetInt(OptionFile, "Graphics", "VSync", True)
	
	opt\AntiAliasing = IniGetInt(OptionFile, "Graphics", "Anti-Aliasing", True)
	
	opt\AdvancedRoomLights = IniGetInt(OptionFile, "Graphics", "Advanced Room Lighting", True)
	
	opt\ScreenGamma = IniGetFloat(OptionFile, "Graphics", "Screen Gamma", 1.0)
	opt\PrevScreenGamma = 1.0
	
	opt\ParticleAmount = IniGetInt(OptionFile, "Graphics", "Particle Amount", 2)
	
	opt\TextureDetails = IniGetInt(OptionFile, "Graphics", "Texture Details", 4)
	
	Select opt\TextureDetails
		Case 0
			;[Block]
			opt\TextureDetailsLevel = 0.8
			;[End Block]
		Case 1
			;[Block]
			opt\TextureDetailsLevel = 0.4
			;[End Block]
		Case 2
			;[Block]
			opt\TextureDetailsLevel = 0.0
			;[End Block]
		Case 3
			;[Block]
			opt\TextureDetailsLevel = -0.4
			;[End Block]
		Case 4
			;[Block]
			opt\TextureDetailsLevel = -0.8
			;[End Block]
	End Select
	
	opt\SaveTexturesInVRAM = IniGetInt(OptionFile, "Graphics", "Save Textures In VRAM", True)
	
	opt\FOV = IniGetFloat(OptionFile, "Graphics", "FOV", 60.0)
	opt\CurrFOV = opt\FOV - 40.0
	
	opt\Anisotropic = IniGetInt(OptionFile, "Graphics", "Anisotropic Filtering", 4)
	
	Select opt\Anisotropic
		Case 0
			;[Block]
			opt\AnisotropicLevel = 0
			;[End Block]
		Case 1
			;[Block]
			opt\AnisotropicLevel = 2
			;[End Block]
		Case 2
			;[Block]
			opt\AnisotropicLevel = 4
			;[End Block]
		Case 3
			;[Block]
			opt\AnisotropicLevel = 8
			;[End Block]
		Case 4
			;[Block]
			opt\AnisotropicLevel = 16
			;[End Block]
	End Select
	
	opt\SecurityCamRenderInterval = IniGetInt(OptionFile, "Graphics", "Security Cam Render Interval", 2)
	
	Select opt\SecurityCamRenderInterval
		Case 0
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 24.0
			;[End Block]
		Case 1
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 18.0
			;[End Block]
		Case 2
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 12.0
			;[End Block]
		Case 3
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 6.0
			;[End Block]
		Case 4
			;[Block]
			opt\SecurityCamRenderIntervalLevel = 0.0
			;[End Block]
	End Select
	
	; ~ [AUDIO]
	
	opt\PrevMasterVolume = IniGetFloat(OptionFile, "Audio", "Master Volume", 0.5)
	opt\MasterVolume = opt\PrevMasterVolume
	
	opt\MusicVolume = IniGetFloat(OptionFile, "Audio", "Music Volume", 0.5)
	opt\CurrMusicVolume = 1.0
	
	opt\SFXVolume = IniGetFloat(OptionFile, "Audio", "Sound Volume", 0.5)
	
	opt\VoiceVolume = IniGetFloat(OptionFile, "Audio", "Voice Volume", 0.5)
	
	opt\EnableSFXRelease = IniGetInt(OptionFile, "Audio", "SFX Release", True)
	opt\PrevEnableSFXRelease = opt\EnableSFXRelease
	
	opt\UserTrackMode = IniGetInt(OptionFile, "Audio", "User Track Setting", 0)
	
	opt\EnableSubtitles = IniGetInt(OptionFile, "Audio", "Enable Subtitles", False)
	
	opt\OverrideSubColor = IniGetInt(OptionFile, "Audio", "Override Subtitle Color", False)
	
	opt\SubColorR = IniGetInt(OptionFile, "Audio", "Subtitles Color R", 255)
	
	opt\SubColorG = IniGetInt(OptionFile, "Audio", "Subtitles Color G", 255)
	
	opt\SubColorB = IniGetInt(OptionFile, "Audio", "Subtitles Color B", 255)
	
	; ~ [CONTROLS]
	
	opt\MouseSensitivity = IniGetFloat(OptionFile, "Controls", "Mouse Sensitivity", 0.0)
	
	opt\InvertMouseX = IniGetInt(OptionFile, "Controls", "Invert Mouse By X-Axis", False)
	
	opt\InvertMouseY = IniGetInt(OptionFile, "Controls", "Invert Mouse By Y-Axis", False)
	
	opt\MouseSmoothing = IniGetFloat(OptionFile, "Controls", "Mouse Smoothing", 1.0)
	
	key\MOVEMENT_UP = IniGetInt(OptionFile, "Controls", "Up Key", 17)
	
	key\MOVEMENT_LEFT = IniGetInt(OptionFile, "Controls", "Left Key", 30)
	
	key\MOVEMENT_DOWN = IniGetInt(OptionFile, "Controls", "Down Key", 31)
	
	key\MOVEMENT_RIGHT = IniGetInt(OptionFile, "Controls", "Right Key", 32)
	
	key\SPRINT = IniGetInt(OptionFile, "Controls", "Sprint Key", 42)
	
	key\CROUCH = IniGetInt(OptionFile, "Controls", "Crouch Key", 29)
	
	key\BLINK = IniGetInt(OptionFile, "Controls", "Blink Key", 57)
	
	key\INVENTORY = IniGetInt(OptionFile, "Controls", "Inventory Key", 15)
	
	key\SAVE = IniGetInt(OptionFile, "Controls", "Save Key", 63)
	
	key\CONSOLE = IniGetInt(OptionFile, "Controls", "Console Key", 61)
	
	key\SCREENSHOT = IniGetInt(OptionFile, "Controls", "Screenshot Key", 59)
	
	; ~ [ADVANCED]
	
	opt\HUDEnabled = IniGetInt(OptionFile, "Advanced", "Enable HUD", True)
	
	opt\CanOpenConsole = IniGetInt(OptionFile, "Advanced", "Enable Console", False)
	
	opt\ConsoleOpening = IniGetInt(OptionFile, "Advanced", "Console Auto Opening", False)
	
	opt\AchvMsgEnabled = IniGetInt(OptionFile, "Advanced", "Enable Achievement Popup", True)
	
	opt\AutoSaveEnabled = IniGetInt(OptionFile, "Advanced", "Enable Auto Save", True)
	
	opt\TextShadow = IniGetInt(OptionFile, "Advanced", "Enable Text Shadow", False)
	
	opt\ShowFPS = IniGetInt(OptionFile, "Advanced", "Show FPS", False)
	
	opt\FrameLimit = IniGetInt(OptionFile, "Advanced", "Frame Limit", 0.0)
	opt\CurrFrameLimit = (opt\FrameLimit - 19.0) / 100.0
	
	opt\SmoothBars = IniGetInt(OptionFile, "Advanced", "Smooth Bars", True)
	
	opt\PlayStartup = IniGetInt(OptionFile, "Advanced", "Play Startup Videos", True)
	
	opt\LauncherEnabled = IniGetInt(OptionFile, "Advanced", "Launcher Enabled", True)

	opt\NoProgressBar = IniGetInt(OptionFile, "Advanced", "No Progress Bar", False)
	
	; ~ [GLOBAL]
	
	opt\GraphicWidth = IniGetInt(OptionFile, "Global", "Width", DesktopWidth())
	
	opt\GraphicHeight = IniGetInt(OptionFile, "Global", "Height", DesktopHeight())
	
	opt\DisplayMode = IniGetInt(OptionFile, "Global", "Display Mode", 1)
	
	opt\GFXDriver = IniGetInt(OptionFile, "Global", "GFX Driver", 1)
	
	opt\IntroEnabled = IniGetInt(OptionFile, "Global", "Enable Intro", True)
	
	opt\DebugMode = IniGetInt(OptionFile, "Global", "Debug Mode", False)
	
	opt\Language = IniGetString(OptionFile, "Global", "Language", "en")
End Function

Function SaveOptionsINI%(SaveGlobal% = False)
	; ~ [GRAPHICS]
	;[Block]
	IniWriteString(OptionFile, "Graphics", "Enable Bump Mapping", opt\BumpEnabled)
	
	IniWriteString(OptionFile, "Graphics", "VSync", opt\VSync)
	
	IniWriteString(OptionFile, "Graphics", "Anti-Aliasing", opt\AntiAliasing)
	
	IniWriteString(OptionFile, "Graphics", "Advanced Room Lighting", opt\AdvancedRoomLights)
	
	IniWriteString(OptionFile, "Graphics", "Screen Gamma", opt\ScreenGamma)
	
	IniWriteString(OptionFile, "Graphics", "Particle Amount", opt\ParticleAmount)
	
	IniWriteString(OptionFile, "Graphics", "Texture Details", opt\TextureDetails)
	
	IniWriteString(OptionFile, "Graphics", "Save Textures In VRAM", opt\SaveTexturesInVRAM)
	
	IniWriteString(OptionFile, "Graphics", "FOV", Int(opt\FOV))
	
	IniWriteString(OptionFile, "Graphics", "Anisotropic Filtering", opt\Anisotropic)
	
	IniWriteString(OptionFile, "Graphics", "Security Cam Render Interval", opt\SecurityCamRenderInterval)
	;[End Block]
	
	; ~ [AUDIO]
	;[Block]
	IniWriteString(OptionFile, "Audio", "Master Volume", opt\PrevMasterVolume)
	
	IniWriteString(OptionFile, "Audio", "Music Volume", opt\MusicVolume)
	
	IniWriteString(OptionFile, "Audio", "Sound Volume", opt\SFXVolume)
	
	IniWriteString(OptionFile, "Audio", "Voice Volume", opt\VoiceVolume)
	
	IniWriteString(OptionFile, "Audio", "SFX Release", opt\EnableSFXRelease)
	
	IniWriteString(OptionFile, "Audio", "User Track Setting", opt\UserTrackMode)
	
	IniWriteString(OptionFile, "Audio", "Enable Subtitles", opt\EnableSubtitles)
	
	IniWriteString(OptionFile, "Audio", "Override Subtitle Color", opt\OverrideSubColor)
	
	IniWriteString(OptionFile, "Audio", "Subtitles Color R", opt\SubColorR)
	
	IniWriteString(OptionFile, "Audio", "Subtitles Color G", opt\SubColorG)
	
	IniWriteString(OptionFile, "Audio", "Subtitles Color B", opt\SubColorB)
	;[End Block]
	
	; ~ [CONTROLS]
	;[Block]
	IniWriteString(OptionFile, "Controls", "Mouse Sensitivity", opt\MouseSensitivity)
	
	IniWriteString(OptionFile, "Controls", "Invert Mouse By X-Axis", opt\InvertMouseX)
	
	IniWriteString(OptionFile, "Controls", "Invert Mouse By Y-Axis", opt\InvertMouseY)
	
	IniWriteString(OptionFile, "Controls", "Mouse Smoothing", opt\MouseSmoothing)
	
	IniWriteString(OptionFile, "Controls", "Up Key", key\MOVEMENT_UP)
	
	IniWriteString(OptionFile, "Controls", "Left Key", key\MOVEMENT_LEFT)
	
	IniWriteString(OptionFile, "Controls", "Down Key", key\MOVEMENT_DOWN)
	
	IniWriteString(OptionFile, "Controls", "Right Key", key\MOVEMENT_RIGHT)
	
	IniWriteString(OptionFile, "Controls", "Sprint Key", key\SPRINT)
	
	IniWriteString(OptionFile, "Controls", "Crouch Key", key\CROUCH)
	
	IniWriteString(OptionFile, "Controls", "Blink Key", key\BLINK)
	
	IniWriteString(OptionFile, "Controls", "Inventory Key", key\INVENTORY)
	
	IniWriteString(OptionFile, "Controls", "Save Key", key\SAVE)
	
	IniWriteString(OptionFile, "Controls", "Console Key", key\CONSOLE)
	
	IniWriteString(OptionFile, "Controls", "Screenshot Key", key\SCREENSHOT)
	;[End Block]
	
	; ~ [ADVANCED]
	;[Block]
	IniWriteString(OptionFile, "Advanced", "Enable HUD", opt\HUDEnabled)
	
	IniWriteString(OptionFile, "Advanced", "Enable Console", opt\CanOpenConsole)
	
	IniWriteString(OptionFile, "Advanced", "Console Auto Opening", opt\ConsoleOpening)
	
	IniWriteString(OptionFile, "Advanced", "Enable Achievement Popup", opt\AchvMsgEnabled)
	
	IniWriteString(OptionFile, "Advanced", "Enable Auto Save", opt\AutoSaveEnabled)
	
	IniWriteString(OptionFile, "Advanced", "Enable Text Shadow", opt\TextShadow)
	
	IniWriteString(OptionFile, "Advanced", "Show FPS", opt\ShowFPS)
	
	IniWriteString(OptionFile, "Advanced", "Frame Limit", opt\FrameLimit)
	
	IniWriteString(OptionFile, "Advanced", "Smooth Bars", opt\SmoothBars)
	
	IniWriteString(OptionFile, "Advanced", "Play Startup Videos", opt\PlayStartup)
	
	IniWriteString(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)

	IniWriteString(OptionFile, "Advanced", "No Progress Bar", opt\NoProgressBar)
	;[End Block]
	
	; ~ [GLOBAL]
	;[Block]
	If SaveGlobal Then IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
	
	IniWriteString(OptionFile, "Global", "Language", opt\Language)
	;[End Block]
End Function

Function ResetOptionsINI%()
	; ~ [GRAPHICS]
	
	opt\BumpEnabled = True
	
	opt\VSync = True
	
	opt\AntiAliasing = (opt\DisplayMode = 0)
	
	opt\AdvancedRoomLights = True
	
	opt\ScreenGamma = 1.0
	opt\PrevScreenGamma = 1.0
	
	opt\ParticleAmount = 2
	
	opt\TextureDetails = 4
	opt\TextureDetailsLevel = -0.8
	
	opt\SaveTexturesInVRAM = True
	
	opt\FOV = 60.0
	opt\CurrFOV = opt\FOV - 40.0
	
	opt\Anisotropic = 4
	opt\AnisotropicLevel = 16
	
	opt\SecurityCamRenderInterval = 2
	opt\SecurityCamRenderIntervalLevel = 12.0
	
	; ~ [AUDIO]
	
	opt\PrevMasterVolume = 0.5
	opt\MasterVolume = opt\PrevMasterVolume
	
	opt\MusicVolume = 0.5
	
	opt\SFXVolume = 0.5
	
	opt\VoiceVolume = 0.5
	
	opt\EnableSFXRelease = True
	
	opt\UserTrackMode = False
	
	opt\EnableSubtitles = False
	
	opt\SubColorR = 255
	
	opt\SubColorG = 255
	
	opt\SubColorB = 255
	
	; ~ [CONTROLS]
	
	opt\MouseSensitivity = 0.0
	
	opt\InvertMouseX = False
	
	opt\InvertMouseY = False
	
	opt\MouseSmoothing = 1.0
	
	key\MOVEMENT_UP = 17
	
	key\MOVEMENT_LEFT = 30
	
	key\MOVEMENT_DOWN = 31
	
	key\MOVEMENT_RIGHT = 32
	
	key\SPRINT = 42
	
	key\CROUCH = 29
	
	key\BLINK = 57
	
	key\INVENTORY = 15
	
	key\SAVE = 63
	
	key\CONSOLE = 61
	
	key\SCREENSHOT = 59
	
	; ~ [ADVANCED]
	
	opt\HUDEnabled = True
	
	opt\CanOpenConsole = False
	
	opt\ConsoleOpening = False
	
	opt\AchvMsgEnabled = True
	
	opt\AutoSaveEnabled = True
	
	opt\TextShadow = False
	
	opt\ShowFPS = False
	
	opt\CurrFrameLimit = 0.0
	opt\FrameLimit = 0
	
	opt\SmoothBars = True
	
	opt\PlayStartup = True
	
	opt\LauncherEnabled = True
	
	; ~ [GLOBAL]
	
	ShouldDeleteGadgets = 1
	
	opt\IntroEnabled = True
	
	opt\Language = "en"
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS