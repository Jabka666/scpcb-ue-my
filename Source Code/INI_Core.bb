; ~ IniController - A part of BlitzToolBox
; ~ Write & Read ini file.
; ~ v1.08 2024.9.16
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

Function StripAbsolutePath$(File$, Dir$)
	Local Pos% = Instr(Lower(File), Dir)
	
	If Pos > 0 Then File = Mid(File, Pos)
	
	Return(File)
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

Global OptionFile$ = AppDataPath + "\scpcb-ue\Data\options.ini"

Type Options
	; ~ [GRAPHICS]
	Field ScreenGamma#, PrevScreenGamma#
	Field FOV#, CurrFOV#
	Field ParticleAmount%
	Field TextureQuality%, TextureQualityLevel%
	Field Anisotropic%, AnisotropicLevel%
	Field LightingQuality%
	Field AmbientOcclusion%
	Field SecurityCamRenderInterval%, SecurityCamRenderIntervalLevel#
	Field AntiAliasing%
	Field VSync%
	Field ColorCorrection%
	Field Bloom%
	Field MotionBlur%
	Field VolumetricLights%
	Field VignetteEnabled%
	Field HighlightInteractable%
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
	Field FirstPersonBodyEnabled%
	Field ShowFPS%
	Field ConsoleOpening%
	Field FrameLimit%, CurrFrameLimit#
	Field AutoSaveEnabled%
	Field SmoothBars%
	Field PlayStartup%
	Field LauncherEnabled%
	; ~ [CONTROLS]
	Field MouseSmoothing#
	Field InvertMouseX%, InvertMouseY%
	Field MouseSensitivity#
	; ~ [GLOBAL]
	Field GraphicWidth%
	Field GraphicHeight%
	Field DisplayMode%
	Field GFXDriver%
	Field IntroEnabled%
	Field DebugMode%
	Field Language$
	Field GFXDriversAmount%
	Field TotalVidMemory%, TotalPhysMemory%
	Field NoProgressBar%
End Type

Global opt.Options = New Options

opt\GFXDriversAmount = CountGfxDrivers()
opt\TotalVidMemory = TotalVidMem() / 1024
opt\TotalPhysMemory = TotalPhys() / 1024

Function LoadOptionsINI%()
	; ~ [GRAPHICS]
	;[Block]
	opt\ScreenGamma = IniGetFloat(OptionFile, "Graphics", "Screen Gamma", 1.0)
	opt\PrevScreenGamma = 1.0
	
	opt\FOV = IniGetFloat(OptionFile, "Graphics", "FOV", 60.0)
	opt\CurrFOV = opt\FOV - 40.0
	
	opt\ParticleAmount = IniGetInt(OptionFile, "Graphics", "Particle Amount", 2)
	
	opt\TextureQuality = IniGetInt(OptionFile, "Graphics", "Texture quality", 3)
	Select opt\TextureQuality
		Case 0
			;[Block]
			opt\TextureQualityLevel = 8
			;[End Block]
		Case 1
			;[Block]
			opt\TextureQualityLevel = 4
			;[End Block]
		Case 2
			;[Block]
			opt\TextureQualityLevel = 2
			;[End Block]
		Case 3
			;[Block]
			opt\TextureQualityLevel = 1
			;[End Block]
	End Select
	
	opt\Anisotropic = IniGetInt(OptionFile, "Graphics", "Anisotropic Filtering", 2)
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
	
	opt\LightingQuality = IniGetInt(OptionFile, "Graphics", "Lighting Quality", 1)
	
	opt\AmbientOcclusion = IniGetInt(OptionFile, "Graphics", "Ambient Occlusion", 2)
	
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
	
	opt\AntiAliasing = IniGetInt(OptionFile, "Graphics", "Anti-Aliasing", True)
	
	opt\VSync = IniGetInt(OptionFile, "Graphics", "VSync", True)
	
	opt\ColorCorrection = IniGetInt(OptionFile, "Graphics", "Color Correction", True)
	
	opt\Bloom = IniGetInt(OptionFile, "Graphics", "Bloom", True)
	
	opt\MotionBlur = IniGetInt(OptionFile, "Graphics", "Motion Blur", True)
	
	opt\VolumetricLights = IniGetInt(OptionFile, "Graphics", "Volumetric Lighting", True)
	
	opt\VignetteEnabled = IniGetInt(OptionFile, "Graphics", "Vignette Enabled", True)
	
	opt\HighlightInteractable = IniGetInt(OptionFile, "Graphics", "Hightlight Interactable", True)
	;[End Block]
	
	; ~ [AUDIO]
	;[Block]
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
	;[End Block]
	
	; ~ [CONTROLS]
	;[Block]
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
	;[End Block]
	
	; ~ [ADVANCED]
	;[Block]
	opt\HUDEnabled = IniGetInt(OptionFile, "Advanced", "Enable HUD", True)
	
	opt\FirstPersonBodyEnabled = IniGetInt(OptionFile, "Advanced", "First Person Body", True)
	
	opt\CanOpenConsole = IniGetInt(OptionFile, "Advanced", "Enable Console", False)
	
	opt\ConsoleOpening = IniGetInt(OptionFile, "Advanced", "Console Auto Opening", False)
	
	opt\AchvMsgEnabled = IniGetInt(OptionFile, "Advanced", "Enable Achievement Popup", True)
	
	opt\AutoSaveEnabled = IniGetInt(OptionFile, "Advanced", "Enable Auto Save", True)
	
	opt\ShowFPS = IniGetInt(OptionFile, "Advanced", "Show FPS", False)
	
	opt\FrameLimit = IniGetInt(OptionFile, "Advanced", "Frame Limit", 0.0)
	opt\CurrFrameLimit = (opt\FrameLimit - 19.0) / 100.0
	
	opt\SmoothBars = IniGetInt(OptionFile, "Advanced", "Smooth Bars", True)
	
	opt\PlayStartup = IniGetInt(OptionFile, "Advanced", "Play Startup Videos", True)
	
	opt\LauncherEnabled = IniGetInt(OptionFile, "Advanced", "Launcher Enabled", True)
	;[End Block]
	
	; ~ [GLOBAL]
	;[Block]
	opt\GraphicWidth = IniGetInt(OptionFile, "Global", "Width", DesktopWidth())
	
	opt\GraphicHeight = IniGetInt(OptionFile, "Global", "Height", DesktopHeight())
	
	opt\DisplayMode = IniGetInt(OptionFile, "Global", "Display Mode", 1)
	
	opt\GFXDriver = IniGetInt(OptionFile, "Global", "GFX Driver", 1)
	
	opt\IntroEnabled = IniGetInt(OptionFile, "Global", "Enable Intro", True)
	
	opt\DebugMode = IniGetInt(OptionFile, "Global", "Debug Mode", False)
	
	opt\Language = IniGetString(OptionFile, "Global", "Language", "en")
	
	opt\NoProgressBar = IniGetInt(OptionFile, "Global", "No Progress Bar", False)
	;[End Block]
End Function

Function SaveOptionsINI%(SaveGlobal% = False)
	; ~ [GRAPHICS]
	;[Block]
	IniWriteFloat(OptionFile, "Graphics", "Screen Gamma", opt\ScreenGamma)
	
	IniWriteFloat(OptionFile, "Graphics", "FOV", Int(opt\FOV))
	
	IniWriteInt(OptionFile, "Graphics", "Particle Amount", opt\ParticleAmount)
	
	IniWriteString(OptionFile, "Graphics", "Texture quality", opt\TextureQuality)
	
	IniWriteInt(OptionFile, "Graphics", "Anisotropic Filtering", opt\Anisotropic)
	
	IniWriteString(OptionFile, "Graphics", "Lighting Quality", opt\LightingQuality)
	
	IniWriteString(OptionFile, "Graphics", "Ambient Occlusion", opt\AmbientOcclusion)
	
	IniWriteFloat(OptionFile, "Graphics", "Security Cam Render Interval", opt\SecurityCamRenderInterval)
	
	IniWriteInt(OptionFile, "Graphics", "Anti-Aliasing", opt\AntiAliasing)
	
	IniWriteInt(OptionFile, "Graphics", "VSync", opt\VSync)
	
	IniWriteString(OptionFile, "Graphics", "Color Correction", opt\ColorCorrection)
	
	IniWriteString(OptionFile, "Graphics", "Bloom", opt\Bloom)
	
	IniWriteString(OptionFile, "Graphics", "Motion Blur", opt\MotionBlur)
	
	IniWriteString(OptionFile, "Graphics", "Volumetric Lighting", opt\VolumetricLights)
	
	IniWriteInt(OptionFile, "Graphics", "Vignette Enabled", opt\VignetteEnabled)
	
	IniWriteInt(OptionFile, "Graphics", "Highlight Interactable", opt\HighlightInteractable)
	;[End Block]
	
	; ~ [AUDIO]
	;[Block]
	IniWriteFloat(OptionFile, "Audio", "Master Volume", opt\PrevMasterVolume)
	
	IniWriteFloat(OptionFile, "Audio", "Music Volume", opt\MusicVolume)
	
	IniWriteFloat(OptionFile, "Audio", "Sound Volume", opt\SFXVolume)
	
	IniWriteFloat(OptionFile, "Audio", "Voice Volume", opt\VoiceVolume)
	
	IniWriteInt(OptionFile, "Audio", "SFX Release", opt\EnableSFXRelease)
	
	IniWriteInt(OptionFile, "Audio", "User Track Setting", opt\UserTrackMode)
	
	IniWriteInt(OptionFile, "Audio", "Enable Subtitles", opt\EnableSubtitles)
	
	IniWriteInt(OptionFile, "Audio", "Override Subtitle Color", opt\OverrideSubColor)
	
	IniWriteFloat(OptionFile, "Audio", "Subtitles Color R", opt\SubColorR)
	
	IniWriteFloat(OptionFile, "Audio", "Subtitles Color G", opt\SubColorG)
	
	IniWriteFloat(OptionFile, "Audio", "Subtitles Color B", opt\SubColorB)
	;[End Block]
	
	; ~ [CONTROLS]
	;[Block]
	IniWriteFloat(OptionFile, "Controls", "Mouse Sensitivity", opt\MouseSensitivity)
	
	IniWriteInt(OptionFile, "Controls", "Invert Mouse By X-Axis", opt\InvertMouseX)
	
	IniWriteInt(OptionFile, "Controls", "Invert Mouse By Y-Axis", opt\InvertMouseY)
	
	IniWriteFloat(OptionFile, "Controls", "Mouse Smoothing", opt\MouseSmoothing)
	
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
	IniWriteInt(OptionFile, "Advanced", "Enable HUD", opt\HUDEnabled)
	
	IniWriteInt(OptionFile, "Advanced", "First Person Body", opt\FirstPersonBodyEnabled)
	
	IniWriteInt(OptionFile, "Advanced", "Enable Console", opt\CanOpenConsole)
	
	IniWriteInt(OptionFile, "Advanced", "Console Auto Opening", opt\ConsoleOpening)
	
	IniWriteInt(OptionFile, "Advanced", "Enable Achievement Popup", opt\AchvMsgEnabled)
	
	IniWriteInt(OptionFile, "Advanced", "Enable Auto Save", opt\AutoSaveEnabled)
	
	IniWriteInt(OptionFile, "Advanced", "Show FPS", opt\ShowFPS)
	
	IniWriteString(OptionFile, "Advanced", "Frame Limit", opt\FrameLimit)
	
	IniWriteInt(OptionFile, "Advanced", "Smooth Bars", opt\SmoothBars)
	
	IniWriteInt(OptionFile, "Advanced", "Play Startup Videos", opt\PlayStartup)
	
	IniWriteInt(OptionFile, "Advanced", "Launcher Enabled", opt\LauncherEnabled)
	;[End Block]
	
	; ~ [GLOBAL]
	;[Block]
	If SaveGlobal Then IniWriteString(OptionFile, "Global", "Enable Intro", opt\IntroEnabled)
	
	IniWriteString(OptionFile, "Global", "Language", opt\Language)
	
	IniWriteString(OptionFile, "Global", "No Progress Bar", opt\NoProgressBar)
	;[End Block]
End Function

Function ResetOptionsINI%()
	; ~ [GRAPHICS]
	
	opt\ScreenGamma = 1.0
	opt\PrevScreenGamma = 1.0
	
	opt\FOV = 60.0
	opt\CurrFOV = opt\FOV - 40.0
	
	opt\ParticleAmount = 2
	
	opt\Anisotropic = 4
	opt\AnisotropicLevel = 16
	
	opt\TextureQuality = 3
	opt\TextureQualityLevel = 1
	
	opt\LightingQuality = 1
	
	opt\AmbientOcclusion = 2
	
	opt\SecurityCamRenderInterval = 2
	opt\SecurityCamRenderIntervalLevel = 12.0
	
	opt\AntiAliasing = True
	
	opt\VSync = True
	
	opt\ColorCorrection = True
	
	opt\Bloom = True
	
	opt\MotionBlur = True
	
	opt\VolumetricLights = True
	
	opt\VignetteEnabled = True
	
	opt\HighlightInteractable = True
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
	
	opt\FirstPersonBodyEnabled = True
	
	opt\CanOpenConsole = False
	
	opt\ConsoleOpening = False
	
	opt\AchvMsgEnabled = True
	
	opt\AutoSaveEnabled = True
	
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