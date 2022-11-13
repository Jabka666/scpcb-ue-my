; ~ Safe loads for MAV trapping media issues

; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists.

Type Sound
	Field InternalHandle%
	Field Name$
	Field HasSubtitles%
	Field Channels%[32]
	Field ReleaseTime%
End Type

Function AutoReleaseSounds%()
	Local snd.Sound
	
	For snd.Sound = Each Sound
		Local TryRelease% = True
		Local i%
		
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				If ChannelPlaying(snd\Channels[i]) Then
					TryRelease = False
					snd\ReleaseTime = MilliSecs2() + 5000
					Exit
				EndIf
			EndIf
		Next
		
		If TryRelease Then
			If snd\ReleaseTime < MilliSecs2() Then
				If snd\InternalHandle <> 0 Then FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(SoundHandle%)
	Local snd.Sound = Object.Sound(SoundHandle)
	
	If snd <> Null Then
		Local ShouldPlay% = True
		Local i%
		
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				If (Not ChannelPlaying(snd\Channels[i])) Then
					If (Not snd\InternalHandle) Then
						If FileType(snd\Name) <> 1 Then
							CreateConsoleMsg(Format(GetLocalString("runerr", "sound.notfound"), snd\Name))
							OpenConsoleOnError()
						Else
							If opt\EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
						EndIf
						If (Not snd\InternalHandle) Then
							CreateConsoleMsg(Format(GetLocalString("runerr", "sound.failed.load"), snd\Name))
							OpenConsoleOnError()
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\Channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\Channels[i] = PlaySound(snd\InternalHandle)
					EndIf
					If opt\EnableSubtitles Then 
						If snd\HasSubtitles Then ShowSubtitles(snd\Name)
					EndIf
					ChannelVolume(snd\Channels[i], opt\SFXVolume * opt\MasterVolume)
					snd\ReleaseTime = MilliSecs2() + 5000 ; ~ Release after 5 seconds
					Return(snd\Channels[i])
				EndIf
			Else
				If (Not snd\InternalHandle) Then
					If FileType(snd\Name) <> 1 Then
						CreateConsoleMsg(Format(GetLocalString("runerr", "sound.notfound"), snd\Name))
						OpenConsoleOnError()
					Else
						If opt\EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
					EndIf
						
					If (Not snd\InternalHandle) Then
						CreateConsoleMsg(Format(GetLocalString("runerr", "sound.failedload"), snd\Name))
						OpenConsoleOnError()
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\Channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\Channels[i] = PlaySound(snd\InternalHandle)
				EndIf
				If opt\EnableSubtitles Then 
					If snd\HasSubtitles Then ShowSubtitles(snd\Name)
				EndIf
				ChannelVolume(snd\Channels[i], opt\SFXVolume * opt\MasterVolume)
				snd\ReleaseTime = MilliSecs2() + 5000 ; ~ Release after 5 seconds
				Return(snd\Channels[i])
			EndIf
		Next
	EndIf
	Return(0)
End Function

Function LoadSound_Strict%(File$)
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	
	Local snd.Sound
	
	snd.Sound = New Sound
	snd\Name = File
	snd\InternalHandle = 0
	snd\ReleaseTime = 0
	If opt\EnableSubtitles Then
		; ~ Check if the sound has subtitles
		If IniBufferSectionExist(SubtitlesFile, File) Then snd\HasSubtitles = True
	EndIf
	If (Not opt\EnableSFXRelease) Then
		If (Not snd\InternalHandle) Then  snd\InternalHandle = LoadSound(snd\Name)
	EndIf
	Return(Handle(snd))
End Function

Function FreeSound_Strict%(SoundHandle%)
	Local snd.Sound = Object.Sound(SoundHandle)
	
	If snd <> Null Then
		If snd\InternalHandle <> 0 Then FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
		snd\ReleaseTime = 0
		Delete(snd)
	EndIf
End Function

Type Stream
	Field CHN%
End Type

Const Mode% = 2
Const TwoD% = 8192

Function StreamSound_Strict%(File$, Volume# = 1.0, CustomMode% = Mode)
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If FileType(File) <> 1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr", "sound.notfound"), File))
		OpenConsoleOnError()
		Return(0)
	EndIf
	
	Local st.Stream = New Stream
	
	st\CHN = PlayMusic(File, CustomMode + TwoD)
	
	If st\CHN = -1 Then
		CreateConsoleMsg(Format(Format(GetLocalString("runerr", "sound.stream.failed.n1"), File, "{0}"), st\CHN, "{1}"))
		OpenConsoleOnError()
		Return(-1)
	EndIf
	ChannelVolume(st\CHN, Volume)
	
	Return(Handle(st))
End Function

Function StopStream_Strict%(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		CreateConsoleMsg(GetLocalString("runerr", "sound.stream.failed.stop"))
		OpenConsoleOnError()
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr", "sound.stream.failed.stop.v"), st\CHN))
		OpenConsoleOnError()
		Return
	EndIf
	StopChannel(st\CHN) : st\CHN = 0
	
	Delete(st)
End Function

Function SetStreamVolume_Strict%(StreamHandle%, Volume#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		CreateConsoleMsg(GetLocalString("runerr", "sound.stream.failed.set"))
		OpenConsoleOnError()
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr", "sound.stream.failed.set.v"), st\CHN))
		OpenConsoleOnError()
		Return
	EndIf
	ChannelVolume(st\CHN, Volume)
End Function

Function SetStreamPaused_Strict%(StreamHandle%, Paused%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		CreateConsoleMsg(GetLocalString("runerr", "sound.stream.failed.pause"))
		OpenConsoleOnError()
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr", "sound.stream.failed.pause.v"), st\CHN))
		OpenConsoleOnError()
		Return
	EndIf
	If Paused Then
		PauseChannel(st\CHN)
	Else
		ResumeChannel(st\CHN)
	EndIf
End Function

Function IsStreamPlaying_Strict%(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		CreateConsoleMsg(GetLocalString("runerr", "sound.stream.failed.find"))
		OpenConsoleOnError()
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr","sound.stream.failed.find.v"), st\CHN))
		OpenConsoleOnError()
		Return
	EndIf
	Return(ChannelPlaying(st\CHN))
End Function

Function SetStreamPan_Strict%(StreamHandle%, Pan#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		CreateConsoleMsg(GetLocalString("runerr", "sound.stream.failed.find"))
		OpenConsoleOnError()
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg(Format(GetLocalString("runerr","sound.stream.failed.find.v"), st\CHN))
		OpenConsoleOnError()
		Return
	EndIf
	ChannelPan(st\CHN, Pan)
End Function

Function UpdateStreamSoundOrigin%(StreamHandle, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	If StreamHandle <> 0 Then
		If IsStreamPlaying_Strict(StreamHandle) Then
			Range = Max(Range, 1.0)
			
			If Volume > 0.0 Then
				Local Dist# = EntityDistance(Cam, Entity) / Range
				
				If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
					Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
					
					SetStreamVolume_Strict(StreamHandle, Volume * (1.0 - Dist) * opt\SFXVolume * opt\MasterVolume)
					SetStreamPan_Strict(StreamHandle, PanValue)
				Else
					SetStreamVolume_Strict(StreamHandle, 0.0)
				EndIf
			Else
				SetStreamVolume_Strict(StreamHandle, 0.0)
			EndIf
		EndIf
	EndIf
End Function

Function LoadMesh_Strict%(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, t2%, Texture%
	Local TexAlpha% = 0
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "mesh.notfound"), File))
		Tmp = LoadMesh(File, Parent)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "mesh.failed.load"), File))
	EndIf
	
	For i = 1 To CountSurfaces(Tmp)
		SF = GetSurface(Tmp, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			Texture = 0
			t1 = 0 : t2 = 0
			t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
			If t1 <> 0 Then
				TexAlpha = IsTexAlpha(t1)
				If TexAlpha <> 2 Then
					Texture = CheckForTexture(t1, TexAlpha)
					If Texture <> 0 Then
						BrushTexture(b, Texture, 0, 0)
					Else
						; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
				Else
					t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if Lightmap is existing)
					Texture = CheckForTexture(t1, 1)
					If Texture <> 0 Then
						TextureCoords(Texture, 1)
						BrushTexture(b, Texture, 0, 0)
					Else
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
					
					Texture = CheckForTexture(t2, TexAlpha)
					If Texture <> 0 Then
						TextureCoords(Texture, 0)
						BrushTexture(b, Texture, 0, 1)
					Else
						BrushTexture(b, MissingTexture, 0, 1)
					EndIf
					FreeTexture(t2)
				EndIf
				PaintSurface(SF, b)
				FreeTexture(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	Return(Tmp)
End Function

Function LoadAnimMesh_Strict%(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, Texture%
	Local TexAlpha% = 0
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "animmesh.notfound"), File))
		Tmp = LoadAnimMesh(File, Parent)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "animmesh.failed.load"), File))
	EndIf
	
	For i = 1 To CountSurfaces(Tmp)
		SF = GetSurface(Tmp, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			Texture = 0
			t1 = 0
			t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
			If t1 <> 0 Then
				TexAlpha = IsTexAlpha(t1)
				Texture = CheckForTexture(t1, TexAlpha)
				If Texture <> 0 Then
					BrushTexture(b, Texture, 0, 0)
				Else
					; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
					BrushTexture(b, MissingTexture, 0, 0)
				EndIf
				PaintSurface(SF, b)
				FreeTexture(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	Return(Tmp)
End Function   

Function LoadTexture_Cache%(File$, Flags% = 1)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then Tmp = LoadTexture(File, Flags)
	Return(Tmp)
End Function

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict%(File$, Flags% = 1, TexDeleteType% = DeleteMapTextures)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "texture.notfound"), File))
		Tmp = LoadTextureCheckingIfInCache(File, Flags, TexDeleteType)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "texture.failed.load"), File))
	EndIf
	Return(Tmp) 
End Function

Function ExecFile_Strict%(File$)
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	ExecFile(File)
End Function

Function OpenMovie_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "movie.notfound"), File))
		Tmp = OpenMovie(File)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "movie.failed.load"), File))
	EndIf
	Return(Tmp) 
End Function

Function OpenFile_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "openfile.notfound"), File))
		Tmp = OpenFile(File)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "openfile.failed.open"), File))
	EndIf
	Return(Tmp)
End Function

Function ReadFile_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "readfile.notfound"), File))
		Tmp = ReadFile(File)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "readfile.failed.read"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadAnimTexture_Strict%(File$, Flags%, Width%, Height%, FirstFrame%, Count%, TexDeleteType% = DeleteMapTextures)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "animtexture.notfound"), File))
		Tmp = LoadAnimTextureCheckingIfInCache(File, Flags, Width, Height, FirstFrame, Count, TexDeleteType)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "animtexture.failed.load"), File))
	EndIf
	Return(Tmp)
End Function   

Function LoadBrush_Strict%(File$, Flags% = 1, u# = 1.0, v# = 1.0)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "brush.notfound"), File))
		Tmp = LoadBrush(File, Flags, u, v)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "brush.failed.load"), File))
	EndIf
	Return(Tmp)
End Function 

Function LoadFont_Strict%(File$, Height% = 13, IgnoreScaling% = False)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "font.notfound"), File))
		Tmp = LoadFont(File, (Int(Height * (opt\GraphicHeight / 1024.0))) * (Not IgnoreScaling) + IgnoreScaling * Height)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "font.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadImage_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "image.notfound"), File))
		Tmp = LoadImage(File)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "image.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadAnimImage_Strict%(File$, Width%, Height%, FirstFrame%, Count%)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "animimage.notfound"), File))
		Tmp = LoadAnimImage(File, Width, Height, FirstFrame, Count)
		If (Not Tmp) Then RuntimeError(Format(GetLocalString("runerr", "animimage.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D