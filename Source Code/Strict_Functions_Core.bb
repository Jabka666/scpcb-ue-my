; ~ Author: RifRaf, further modified by MonocleBios
; ~ Safe loads for MAV trapping media issues
; ~ Functions with extra parameters
; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists.

Function RuntimeErrorEx%(Message$)
	If ErrorMessageInitialized
		CatchErrors(Message)
		MemoryAccessViolation()
	Else
		RuntimeError(Message)
	EndIf
End Function

Const MaxChannelsAmount% = 16 ; ~ 32

Global AutoReleaseSoundTime%

Type Sound
	Field InternalHandle%
	Field Name$
	Field Channels%[MaxChannelsAmount]
	Field ReleaseTime%
End Type

Function AutoReleaseSounds%()
	Local snd.Sound
	Local CurrTime% = MilliSecs()
	
	If AutoReleaseSoundTime < MilliSecs()
		For snd.Sound = Each Sound
			Local TryRelease% = True
			Local i%
			
			For i = 0 To MaxChannelsAmount - 1
				If snd\Channels[i] <> 0
					If ChannelPlaying(snd\Channels[i])
						TryRelease = False
						snd\ReleaseTime = CurrTime + 5000 ; ~ Release after 5 seconds
						Exit
					EndIf
				EndIf
			Next
			
			If TryRelease
				If snd\ReleaseTime < CurrTime
					If snd\InternalHandle <> 0
						FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
						RemoveSubtitlesToken(snd)
					EndIf
				EndIf
			EndIf
		Next
		AutoReleaseSoundTime = MilliSecs() + 1000
	EndIf
End Function

Function PlaySound_Strict%(SoundHandle%, IsVoice% = False)
	Local snd.Sound = Object.Sound(SoundHandle)
	Local CurrTime% = MilliSecs()
	
	If snd <> Null
		Local i%
		
		For i = 0 To MaxChannelsAmount - 1
			If snd\Channels[i] <> 0
				If (Not ChannelPlaying(snd\Channels[i]))
					If snd\InternalHandle = 0
						If FileType(snd\Name) <> 1
							OpenConsoleOnError(Format(GetLocalString("runerr", "sound.notfound"), snd\Name))
						ElseIf opt\EnableSFXRelease
							snd\InternalHandle = LoadSound(snd\Name)
							CreateSubtitlesToken(snd\Name, snd)
						EndIf
						If snd\InternalHandle = 0 Then OpenConsoleOnError(Format(GetLocalString("runerr", "sound.failed.load"), snd\Name))
					EndIf
					snd\Channels[i] = PlaySound(snd\InternalHandle)
					ChannelVolumeEx(snd\Channels[i], ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)
					snd\ReleaseTime = CurrTime + 5000 ; ~ Release after 5 seconds
					Return(snd\Channels[i])
				EndIf
			Else
				If snd\InternalHandle = 0
					If FileType(snd\Name) <> 1
						OpenConsoleOnError(Format(GetLocalString("runerr", "sound.notfound"), snd\Name))
					ElseIf opt\EnableSFXRelease
						snd\InternalHandle = LoadSound(snd\Name)
						CreateSubtitlesToken(snd\Name, snd)
					EndIf
					If snd\InternalHandle = 0 Then OpenConsoleOnError(Format(GetLocalString("runerr", "sound.failed.load"), snd\Name))
				EndIf
				snd\Channels[i] = PlaySound(snd\InternalHandle)
				ChannelVolumeEx(snd\Channels[i], ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)
				snd\ReleaseTime = CurrTime + 5000 ; ~ Release after 5 seconds
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
	If (Not opt\EnableSFXRelease)
		If snd\InternalHandle = 0
			snd\InternalHandle = LoadSound(snd\Name)
			CreateSubtitlesToken(snd\Name, snd)
		EndIf
	EndIf
	Return(Handle(snd))
End Function

Function FreeSound_Strict%(SoundHandle%)
	Local snd.Sound = Object.Sound(SoundHandle)
	
	If snd <> Null
		If snd\InternalHandle <> 0
			FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
			RemoveSubtitlesToken(snd)
		EndIf
		Delete(snd)
	EndIf
End Function

Type Stream
	Field CHN%
End Type

Const ModeStandart% = 0
Const ModeLoop% = 2

Function StreamSound_Strict%(File$, Volume# = 1.0, Mode% = ModeStandart)
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If FileType(File) <> 1
		OpenConsoleOnError(Format(GetLocalString("runerr", "sound.notfound"), File))
		Return(0)
	EndIf
	
	Local st.Stream = New Stream
	
	st\CHN = PlayMusic(File, Mode + 8192.0)
	
	If st\CHN = -1
		OpenConsoleOnError(Format(Format(GetLocalString("runerr", "sound.stream.failed.n1"), File, "{0}"), st\CHN, "{1}"))
		Return(-1)
	EndIf
	ChannelVolumeEx(st\CHN, Volume)
	
	CreateSubtitlesToken(File, Null)
	
	Return(Handle(st))
End Function

Function StopStream_Strict%(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		OpenConsoleOnError(GetLocalString("runerr", "sound.stream.failed.stop"))
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1
		OpenConsoleOnError(Format(GetLocalString("runerr", "sound.stream.failed.stop.v"), st\CHN))
		Return
	EndIf
	StopChannel(st\CHN) : st\CHN = 0
	
	Delete(st)
End Function

Function SetStreamVolume_Strict%(StreamHandle%, Volume#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		OpenConsoleOnError(GetLocalString("runerr", "sound.stream.failed.set"))
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1
		OpenConsoleOnError(Format(GetLocalString("runerr", "sound.stream.failed.set.v"), st\CHN))
		Return
	EndIf
	ChannelVolumeEx(st\CHN, Volume)
End Function

Function SetStreamPaused_Strict%(StreamHandle%, Paused%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		OpenConsoleOnError(GetLocalString("runerr", "sound.stream.failed.pause"))
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1
		OpenConsoleOnError(Format(GetLocalString("runerr", "sound.stream.failed.pause.v"), st\CHN))
		Return
	EndIf
	If Paused
		PauseChannel(st\CHN)
	Else
		ResumeChannel(st\CHN)
	EndIf
End Function

Function IsStreamPlaying_Strict%(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		OpenConsoleOnError(GetLocalString("runerr", "sound.stream.failed.find"))
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1
		OpenConsoleOnError(Format(GetLocalString("runerr","sound.stream.failed.find.v"), st\CHN))
		Return
	EndIf
	Return(ChannelPlaying(st\CHN))
End Function

Function SetStreamPan_Strict%(StreamHandle%, Pan#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		OpenConsoleOnError(GetLocalString("runerr", "sound.stream.failed.find"))
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1
		OpenConsoleOnError(Format(GetLocalString("runerr", "sound.stream.failed.find.v"), st\CHN))
		Return
	EndIf
	ChannelPan(st\CHN, Pan)
End Function

Function UpdateStreamSoundOrigin%(StreamHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, IsVoice% = False)
	If StreamHandle <> 0
		If IsStreamPlaying_Strict(StreamHandle)
			Range = Max(Range, 1.0)
			
			If Volume > 0.0
				Local Dist# = 1.0 - (EntityDistance(Cam, Entity) / Range)
				
				If Dist > 0.0 And Dist < 1.0
					Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
					
					SetStreamVolume_Strict(StreamHandle, Volume * Dist * ((opt\VoiceVolume * IsVoice) + (opt\SFXVolume * (Not (IsVoice)))) * opt\MasterVolume)
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

Function LoadMesh_Strict%(File$, Parent% = 0, CheckTexture% = True)
	Local Tmp%, i%, SF%, b%, t1%, t2%, Texture%
	Local TexAlpha% = 0
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "mesh.notfound"), File))
		Tmp = LoadMesh(File, Parent)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "mesh.failed.load"), File))
	EndIf
	
	If CheckTexture Then
		Local SurfCount% = CountSurfaces(Tmp)
		
		For i = 1 To SurfCount
			SF = GetSurface(Tmp, i)
			b = GetSurfaceBrush(SF)
			If b <> 0
				Texture = 0
				t1 = 0 : t2 = 0
				t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
				If t1 <> 0
					TexAlpha = IsTexAlpha(t1)
					If TexAlpha <> 2
						Texture = CheckForTexture(t1, TexAlpha)
						If Texture <> 0
							BrushTexture(b, Texture, 0, 0)
							DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
						Else
								; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
							BrushTexture(b, MissingTexture, 0, 0)
						EndIf
					Else
						Texture = CheckForTexture(t1, 1)
						If Texture <> 0
							TextureCoords(Texture, 1)
							BrushTexture(b, Texture, 0, 0)
							DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
						Else
							BrushTexture(b, MissingTexture, 0, 0)
						EndIf
						
						t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if Lightmap is existing)
						If t2 <> 0
							Texture = CheckForTexture(t2, TexAlpha)
							If Texture <> 0
								TextureCoords(Texture, 0)
								BrushTexture(b, Texture, 0, 1)
								DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
							Else
								BrushTexture(b, MissingTexture, 0, 1)
							EndIf
							FreeTexture(t2) : t2 = 0
						EndIf
					EndIf
					PaintSurface(SF, b)
					FreeTexture(t1) : t1 = 0
				EndIf
				FreeBrush(b) : b = 0
			EndIf
		Next
	EndIf
	
	SetDeferredEntity(Tmp)
	Return(Tmp)
End Function

Function LoadAnimMesh_Strict%(File$, Parent% = 0, CheckTexture% = True)
	Local Tmp%, i%, SF%, b%, t1%, Texture%
	Local TexAlpha% = 0
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animmesh.notfound"), File))
		Tmp = LoadAnimMesh(File, Parent)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animmesh.failed.load"), File))
	EndIf
	
	If CheckTexture Then
		Local SurfCount% = CountSurfaces(Tmp)
		
		For i = 1 To SurfCount
			SF = GetSurface(Tmp, i)
			b = GetSurfaceBrush(SF)
			If b <> 0
				Texture = 0
				t1 = 0
				t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
				If t1 <> 0
					TexAlpha = IsTexAlpha(t1)
					Texture = CheckForTexture(t1, TexAlpha)
					If Texture <> 0
						BrushTexture(b, Texture, 0, 0)
						DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
					Else
						; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
					PaintSurface(SF, b)
					FreeTexture(t1) : t1 = 0
				EndIf
				FreeBrush(b) : b = 0
			EndIf
		Next
	EndIf
	
	SetDeferredEntity(Tmp)
	Return(Tmp)
End Function

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict%(File$, Flags% = 1, TexDeleteType% = DeleteMapTextures, Blend5% = True, Scale# = 1.0)
	Local Tmp%
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "texture.notfound"), File))
		Tmp = LoadTextureCheckingIfInCache(File, Flags, TexDeleteType, Scale)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "texture.failed.load"), File))
		If Blend5 Then TextureBlend(Tmp, 5)
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
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "movie.notfound"), File))
		Tmp = OpenMovie(File)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "movie.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function OpenFile_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "openfile.notfound"), File))
		Tmp = OpenFile(File)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "openfile.failed.open"), File))
	EndIf
	Return(Tmp)
End Function

Function ReadFile_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "readfile.notfound"), File))
		Tmp = ReadFile(File)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "readfile.failed.read"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadAnimTexture_Strict%(File$, Flags%, Width%, Height%, FirstFrame%, Count%, TexDeleteType% = DeleteMapTextures)
	Local Tmp%
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animtexture.notfound"), File))
		Tmp = LoadAnimTextureCheckingIfInCache(File, Flags, Width, Height, FirstFrame, Count, TexDeleteType)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animtexture.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadBrush_Strict%(File$, Flags% = 1, u# = 1.0, v# = 1.0)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "brush.notfound"), File))
		Tmp = LoadBrush(File, Flags, u, v)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "brush.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadFont_Strict%(File$, Height% = 13, IgnoreScaling% = False)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "font.notfound"), File))
		Tmp = LoadFont(File, (Int(Height * (opt\GraphicHeight / 1024.0))) * (Not IgnoreScaling) + IgnoreScaling * Height)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "font.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadImage_Strict%(File$)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "image.notfound"), File))
		Tmp = LoadImage(File)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "image.failed.load"), File))
		If opt\DisplayMode = 0 Then BufferDirty(ImageBuffer(Tmp))
	EndIf
	Return(Tmp)
End Function

Function LoadAnimImage_Strict%(File$, Width%, Height%, FirstFrame%, Count%)
	Local Tmp%
	
	If FileType(lang\LanguagePath + File) = 1 Then File = lang\LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animimage.notfound"), File))
		Tmp = LoadAnimImage(File, Width, Height, FirstFrame, Count)
		If Tmp = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "animimage.failed.load"), File))
		If opt\DisplayMode = 0 Then BufferDirty(ImageBuffer(Tmp))
	EndIf
	Return(Tmp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS