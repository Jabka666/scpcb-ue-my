; ~ ID: 2975
; ~ Author: RifRaf, further modified by MonocleBios
; ~ Date: 2012-09-11 11:44:22
; ~ Title: Safe Loads (b3d) ;strict loads sounds more appropriate IMO
; ~ Description: Get the missing filename reported

; ~ Safe loads for MAV trapping media issues

; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists

Function LoadImage_Strict(File$)
	If FileType(File) <> 1 Then RuntimeError("Image " + Chr(34) + File + Chr(34) + " missing. ")
	Tmp = LoadImage(File)
	Return(Tmp)
	; ~ Attempt to load the image again
	If Tmp = 0 Then Tmp2 = LoadImage(File)
	Return(Tmp2)
End Function

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
					snd\ReleaseTime = MilliSecs2() + 5000
					Exit
				EndIf
			EndIf
		Next
		
		If TryRelease Then
			If snd\ReleaseTime < MilliSecs2() Then
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
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						Else
							If EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
						EndIf
						If snd\InternalHandle = 0 Then
							CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\Name + Chr(34))
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\Channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\Channels[i] = PlaySound(snd\InternalHandle)
					EndIf
					ChannelVolume(snd\Channels[i], SFXVolume#)
					snd\ReleaseTime = MilliSecs2() + 5000 ; ~ Release after 5 seconds
					Return(snd\Channels[i])
				EndIf
			Else
				If snd\InternalHandle = 0 Then
					If FileType(snd\Name) <> 1 Then
						CreateConsoleMsg("Sound " + Chr(34) + snd\Name + Chr(34) + " not found.")
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					Else
						If EnableSFXRelease Then snd\InternalHandle = LoadSound(snd\Name)
					EndIf
						
					If snd\InternalHandle = 0 Then
						CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\Name + Chr(34))
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\Channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\Channels[i] = PlaySound(snd\InternalHandle)
				EndIf
				ChannelVolume(snd\Channels[i], SFXVolume#)
				snd\ReleaseTime = MilliSecs2() + 5000 ; ~ Release after 5 seconds
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
	Field SFX%
	Field CHN%
End Type

Function StreamSound_Strict(File$, Volume# = 1.0, CustomMode% = Mode)
	If FileType(File) <> 1
		CreateConsoleMsg("Sound " + Chr(34) + File + Chr(34) + " not found.")
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return(0)
	EndIf
	
	Local st.Stream = New Stream
	
	st\SFX = FSOUND_Stream_Open(File, CustomMode, 0)
	
	If st\SFX = 0
		CreateConsoleMsg("Failed to stream Sound (returned 0): " + Chr(34) + File + Chr(34))
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return(0)
	EndIf
	
	st\CHN = FSOUND_Stream_Play(FreeChannel, st\SFX)
	
	If st\CHN = -1
		CreateConsoleMsg("Failed to stream Sound (returned -1): " + Chr(34) + File + Chr(34))
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return(-1)
	EndIf
	
	FSOUND_SetVolume(st\CHN, Volume * 255)
	FSOUND_SetPaused(st\CHN, False)
	
	Return(Handle(st))
End Function

Function StopStream_Strict(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to stop stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Or st\CHN = -1
		CreateConsoleMsg("Failed to stop stream Sound: Return value " + st\CHN)
		Return
	EndIf
	
	FSOUND_StopSound(st\CHN)
	FSOUND_Stream_Stop(st\SFX)
	FSOUND_Stream_Close(st\SFX)
	
	Delete(st)
End Function

Function SetStreamVolume_Strict(StreamHandle%, Volume#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to set stream Sound volume: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Or st\CHN = -1
		CreateConsoleMsg("Failed to set stream Sound volume: Return value " + st\CHN)
		Return
	EndIf
	
	FSOUND_SetVolume(st\CHN, Volume * 255.0)
	FSOUND_SetPaused(st\CHN, False)
End Function

Function SetStreamPaused_Strict(StreamHandle%, Paused%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to pause / unpause stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Or st\CHN = -1
		CreateConsoleMsg("Failed to pause / unpause stream Sound: Return value " + st\CHN)
		Return
	EndIf
	FSOUND_SetPaused(st\CHN, Paused)
End Function

Function IsStreamPlaying_Strict(StreamHandle%)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Or st\CHN = -1
		CreateConsoleMsg("Failed to find stream Sound: Return value " + st\CHN)
		Return
	EndIf
	Return(FSOUND_IsPlaying(st\CHN))
End Function

Function SetStreamPan_Strict(StreamHandle%, Pan#)
	Local st.Stream = Object.Stream(StreamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\CHN = 0 Or st\CHN = -1
		CreateConsoleMsg("Failed to find stream Sound: Return value " + st\CHN)
		Return
	EndIf
	
	; ~ -1 = Left = 0
	; ~ 0 = Middle = 127.5 (127)
	; ~ 1 = Right = 255
	
	Local FMod_Pan% = 0
	
	FMod_Pan = Int((255.0 / 2.0) + ((255.0 / 2.0) * Pan#))
	FSOUND_SetPan(st\CHN, FMod_Pan)
End Function

Function UpdateStreamSoundOrigin(StreamHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist# < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SetStreamVolume_Strict(StreamHandle, Volume# * (1 - Dist#) * SFXVolume#)
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
	If FileType(File) <> 1 Then RuntimeError("3D Mesh " + File + " not found.")
	Tmp = LoadMesh(File, Parent)
	If Tmp = 0 Then RuntimeError("Failed to load 3D Mesh: " + File)
	Return(Tmp) 
End Function   

Function LoadAnimMesh_Strict(File$, Parent% = 0)
	If FileType(File) <> 1 Then RuntimeError("3D Animated Mesh " + File + " not found.")
	Tmp = LoadAnimMesh(File, Parent)
	If Tmp = 0 Then RuntimeError("Failed to load 3D Animated Mesh: " + File)
	Return(Tmp)
End Function   

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$, Flags% = 1)
	If FileType(File) <> 1 Then RuntimeError("Texture " + File + " not found.")
	Tmp = LoadTexture(File, Flags + (256 * (EnableVRam = True)))
	If Tmp = 0 Then RuntimeError("Failed to load Texture: " + File)
	Return(Tmp)
End Function   

Function LoadBrush_Strict(File$, Flags%, u# = 1.0, v# = 1.0)
	If FileType(File) <> 1 Then RuntimeError("Brush Texture " + File + "not found.")
	Tmp = LoadBrush(File, Flags, u, v)
	If Tmp = 0 Then RuntimeError("Failed to load Brush: " + File)
	Return(Tmp)
End Function 

Function LoadFont_Strict(File$ = "Tahoma", Height% = 13, Bold% = 0, Italic% = 0, UnderLine% = 0)
	If FileType(File) <> 1 Then RuntimeError("Font " + File + " not found.")
	Tmp = LoadFont(File, Height, Bold, Italic, UnderLine)  
	If Tmp = 0 Then RuntimeError("Failed to load Font: " + File)
	Return(Tmp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D