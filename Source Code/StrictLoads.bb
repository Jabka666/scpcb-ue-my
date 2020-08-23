; ~ Safe loads for MAV trapping media issues

; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists

Type Sound
	Field InternalHandle%
	Field Name$
	Field Channels%[32]
	Field ReleaseTime%
End Type

Function AutoReleaseSounds()
	Local snd.Sound
	
	For snd.Sound = Each Sound
		Local TryRelease% = True
		Local i%
		
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				If ChannelPlaying(snd\Channels[i]) = True Then
					TryRelease = False
					snd\ReleaseTime = MilliSecs() + 5000
					Exit
				EndIf
			EndIf
		Next
		
		If TryRelease Then
			If snd\ReleaseTime < MilliSecs() Then
				If snd\InternalHandle <> 0 Then
					FreeSound(snd\InternalHandle)
					snd\InternalHandle = 0
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(SNDHandle%)
	Local snd.Sound = Object.Sound(SNDHandle)
	
	If snd <> Null Then
		Local ShouldPlay% = True
		Local i%
		
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				If ChannelPlaying(snd\Channels[i]) = False Then
					If snd\InternalHandle = 0 Then
						If FileType(snd\Name) <> 1 Then
							CreateConsoleMsg("Sound " + Chr(34) + snd\Name + Chr(34) + " not found.")
							If ConsoleOpening And CanOpenConsole Then
								ConsoleOpen = True
							EndIf
						Else
							If EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
						EndIf
						If snd\InternalHandle = 0 Then
							CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\Name + Chr(34))
							If ConsoleOpening And CanOpenConsole Then
								ConsoleOpen = True
							EndIf
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\Channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\Channels[i] = PlaySound(snd\InternalHandle)
					EndIf
					ChannelVolume(snd\Channels[i], SFXVolume)
					snd\ReleaseTime = MilliSecs() + 5000 ; ~ Release after 5 seconds
					Return(snd\Channels[i])
				EndIf
			Else
				If snd\InternalHandle = 0 Then
					If FileType(snd\Name) <> 1 Then
						CreateConsoleMsg("Sound " + Chr(34) + snd\Name + Chr(34) + " not found.")
						If ConsoleOpening And CanOpenConsole Then
							ConsoleOpen = True
						EndIf
					Else
						If EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
					EndIf
						
					If snd\InternalHandle = 0 Then
						CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\Name + Chr(34))
						If ConsoleOpening And CanOpenConsole Then
							ConsoleOpen = True
						EndIf
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\Channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\Channels[i] = PlaySound(snd\InternalHandle)
				EndIf
				ChannelVolume(snd\Channels[i], SFXVolume)
				snd\ReleaseTime = MilliSecs() + 5000 ; ~ Release after 5 seconds
				Return(snd\Channels[i])
			EndIf
		Next
	EndIf
	Return(0)
End Function

Function LoadSound_Strict(File$)
	Local snd.Sound = New Sound
	
	snd\Name = File
	snd\InternalHandle = 0
	snd\ReleaseTime = 0
	If (Not EnableSFXRelease) Then
		If snd\InternalHandle = 0 Then 
			snd\InternalHandle = LoadSound(snd\Name)
		EndIf
	EndIf
	Return(Handle(snd))
End Function

Function FreeSound_Strict(SNDHandle%)
	Local snd.Sound = Object.Sound(SNDHandle)
	
	If snd <> Null Then
		If snd\InternalHandle <> 0 Then
			FreeSound(snd\InternalHandle)
			snd\InternalHandle = 0
		EndIf
		Delete(snd)
	EndIf
End Function

Type Stream
	Field CHN%
End Type

Const Mode% = 2
Const TwoD% = 8192

Function StreamSound_Strict(File$, Volume# = 1.0, CustomMode% = Mode)
	If FileType(File) <> 1 Then
		CreateConsoleMsg("Sound " + Chr(34) + File + Chr(34) + " not found.")
		If ConsoleOpening And CanOpenConsole Then
			ConsoleOpen = True
		EndIf
		Return(0)
	EndIf
	
	Local st.Stream = New Stream
	
	st\CHN = PlayMusic(File, CustomMode + TwoD)
	
	If st\CHN = -1 Then
		CreateConsoleMsg("Failed to stream Sound (returned -1): " + Chr(34) + File + Chr(34))
		If ConsoleOpening And CanOpenConsole Then
			ConsoleOpen = True
		EndIf
		Return(-1)
	EndIf
	ChannelVolume(st\CHN, Volume * 1.0)
	
	Return(Handle(st))
End Function

Function StopStream_Strict(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		;CreateConsoleMsg("Failed to stop stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg("Failed to stop stream Sound: Return value " + st\CHN)
		Return
	EndIf
	StopChannel(st\CHN)
	
	Delete(st)
End Function

Function SetStreamVolume_Strict(StreamHandle%, Volume#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		;CreateConsoleMsg("Failed to set stream Sound volume: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg("Failed to set stream Sound volume: Return value " + st\CHN)
		Return
	EndIf
	ChannelVolume(st\CHN, Volume * 1.0)
End Function

Function SetStreamPaused_Strict(StreamHandle%, Paused%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		;CreateConsoleMsg("Failed to pause / unpause stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg("Failed to pause / unpause stream Sound: Return value " + st\CHN)
		Return
	EndIf
	If Paused Then
		PauseChannel(st\CHN)
	Else
		ResumeChannel(st\CHN)
	EndIf
End Function

Function IsStreamPlaying_Strict(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		;CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg("Failed to find stream Sound: Return value " + st\CHN)
		Return
	EndIf
	Return(ChannelPlaying(st\CHN))
End Function

Function SetStreamPan_Strict(StreamHandle%, Pan#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null Then
		;CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Lor st\CHN = -1 Then
		CreateConsoleMsg("Failed to find stream Sound: Return value " + st\CHN)
		Return
	EndIf
	ChannelPan(st\CHN, Pan)
End Function

Function UpdateStreamSoundOrigin(StreamHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SetStreamVolume_Strict(StreamHandle, Volume * (1.0 - Dist) * SFXVolume)
			SetStreamPan_Strict(StreamHandle, PanValue)
		Else
			SetStreamVolume_Strict(StreamHandle, 0.0)
		EndIf
	Else
		If StreamHandle <> 0 Then
			SetStreamVolume_Strict(StreamHandle, 0.0)
		EndIf 
	EndIf
End Function

Function LoadMesh_Strict(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, Name$, Texture%, t2%
	Local TexAlpha% = 0
	Local BumpTex%, Temp$
	Local mat.Materials
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("3D Mesh " + File + " not found.")
		Tmp = LoadMesh(File, Parent)
		If Tmp = 0 Then RuntimeError("Failed to load 3D Mesh: " + File)
	EndIf
	
	For i = 1 To CountSurfaces(Tmp)
		SF = GetSurface(Tmp, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			Texture = 0
			Name = ""
			t1 = 0 : t2 = 0
			t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
			If t1 <> 0 Then
				TexAlpha = IsTexAlpha(t1)
				If TexAlpha <> 2 Then
					If BumpEnabled Then
						BumpTex = 0
					Else
						BumpTex = 0
					EndIf
					If BumpTex = 0
						Texture = CheckForTexture(t1, TexAlpha)
						If Texture <> 0 Then
							TextureBlend(Texture, 5)
							BrushTexture(b, Texture, 0, 0)
						Else
							; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
							BrushTexture(b, MissingTexture, 0, 0)
						EndIf
					Else
						Texture = CheckForTexture(t1, TexAlpha)
						If Texture <> 0 Then
							TextureBlend(Texture, 5)
							BrushTexture(b, Texture, 0, 1)
						Else
							; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
							BrushTexture(b, MissingTexture, 0, 1)
						EndIf
						BrushTexture(b, BumpTex, 0, 0)
					EndIf
				Else
					t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if lightmap is existing)
					If BumpEnabled Then
						BumpTex = 0
					Else
						BumpTex = 0
					EndIf
					If BumpTex = 0 Then
						Texture = CheckForTexture(t1, TexAlpha)
						If Texture <> 0 Then
							TextureCoords(Texture, 1)
							TextureBlend(Texture, 2)
							BrushTexture(b, Texture, 0, 0)
						Else
							BrushTexture(b, MissingTexture, 0, 0)
						EndIf
						
						Texture = CheckForTexture(t2, TexAlpha)
						If Texture <> 0 Then
							TextureCoords(Texture, 0)
							TextureBlend(Texture, 5)
							BrushTexture(b, Texture, 0, 1)
						Else
							BrushTexture(b, MissingTexture, 0, 1)
						EndIf
					Else
						Texture = CheckForTexture(t1, TexAlpha)
						If Texture <> 0 Then
							TextureCoords(Texture, 1)
							TextureBlend(Texture, 2)
							BrushTexture(b, Texture, 0, 0)
						Else
							BrushTexture(b, MissingTexture, 0, 0)
						EndIf
						
						Texture = CheckForTexture(t2, TexAlpha)
						If Texture <> 0 Then
							TextureCoords(Texture, 0)
							TextureBlend(Texture, 5)
							BrushTexture(b, Texture, 0, 2)
						Else
							BrushTexture(b, MissingTexture, 0, 2)
						EndIf
						BrushTexture(b, BumpTex, 0, 1)
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

Function LoadAnimMesh_Strict(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, Name$, Texture%
	Local TexAlpha% = 0
	Local mat.Materials
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("3D Animated Mesh " + File + " not found.")
		Tmp = LoadAnimMesh(File, Parent)
		If Tmp = 0 Then RuntimeError("Failed to load 3D Animated Mesh: " + File)
	EndIf
	
	For i = 1 To CountSurfaces(Tmp)
		SF = GetSurface(Tmp,i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			Texture = 0
			Name = ""
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

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$, Flags% = 1, TexDeleteType% = DeleteMapTextures)
	If FileType(File) <> 1 Then RuntimeError("Texture " + File + " not found.")
	
	Local Tmp%
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("Texture " + File + " not found.")
		Tmp = LoadTextureCheckingIfInCache(File, TexDeleTetype, Flags)
		If Tmp = 0 Then RuntimeError("Failed to load Texture: " + File)
	EndIf
	Return(Tmp) 
End Function   

Function LoadBrush_Strict(File$, Flags%, u# = 1.0, v# = 1.0)
	Local Tmp%
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("Brush Texture " + File + "not found.")
		Tmp = LoadBrush(File, Flags, u, v)
		If Tmp = 0 Then RuntimeError("Failed to load Brush: " + File)
	EndIf
	Return(Tmp)
End Function 

Function LoadFont_Strict(File$ = "Tahoma", Height% = 13, IgnoreScaling% = 0)
	Local Tmp%
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("Font " + File + " not found.")
		Tmp = LoadFont(File, (Int(Height * (GraphicHeight / 1024.0))) * (Not IgnoreScaling) + IgnoreScaling * Height)
		If Tmp = 0 Then RuntimeError("Failed to load Font: " + File)
	EndIf
	Return(Tmp)
End Function

Function LoadImage_Strict(File$)
	Local Tmp%, Tmp2%
	
	If FileType(File) <> 1 Then RuntimeError("Image " + Chr(34) + File + Chr(34) + " missing. ")
	Tmp = LoadImage(File)
	Return(Tmp)
	; ~ Attempt to load the image again
	If Tmp = 0 Then Tmp2 = LoadImage(File)
	Return(Tmp2)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D