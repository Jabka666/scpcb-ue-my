Function PlaySound2%(SoundHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	Local SoundCHN% = 0
	
	If Volume > 0.0 Then 
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SoundCHN = PlaySound_Strict(SoundHandle)
			
			ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * SFXVolume)
			ChannelPan(SoundCHN, PanValue)			
		EndIf
	EndIf
	Return(SoundCHN)
End Function

Function LoopSound2%(SoundHandle%, CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
		
		If CHN = 0 Then
			CHN = PlaySound_Strict(SoundHandle)
		Else
			If (Not ChannelPlaying(CHN)) Then CHN = PlaySound_Strict(SoundHandle)
		EndIf
		
		ChannelVolume(CHN, Volume * (1.0 - Dist) * SFXVolume)
		ChannelPan(CHN, PanValue)
	Else
		If CHN <> 0 Then
			ChannelVolume (CHN, 0)
		EndIf 
	EndIf
	Return(CHN)
End Function

Function LoadTempSound(File$)
	If TempSounds[TempSoundIndex] <> 0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(File)
	TempSounds[TempSoundIndex] = TempSound
	TempSoundIndex = (TempSoundIndex + 1) Mod 10
	Return(TempSound)
End Function

Function UpdateMusic()
	If ConsoleFlush Then
		If Not ChannelPlaying(ConsoleMusPlay) Then ConsoleMusPlay = PlaySound(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay ; ~ Playing the wrong clip, fade out
			CurrMusicVolume = Max(CurrMusicVolume - (FPSfactor / 250.0), 0.0)
			If CurrMusicVolume = 0
				If NowPlaying < 66
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic = 0
			EndIf
		Else ; ~ Playing the right clip
			CurrMusicVolume = CurrMusicVolume + (MusicVolume - CurrMusicVolume) * (0.1 * FPSfactor)
		EndIf
		
		If NowPlaying < 66
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict("SFX\Music\" + Music(NowPlaying) + ".ogg", 0.0, Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN, CurrMusicVolume)
		EndIf
	Else
		If FPSfactor > 0 Or OptionsMenu = 2 Then
			If ChannelPlaying(MusicCHN) = False Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume(MusicCHN, 1.0 * MusicVolume)
		EndIf
	EndIf
	
End Function 

Function PauseSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) = True Then PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, True)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) = True Then PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, True)
			EndIf
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) = True Then PauseChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, True)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then PauseChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, True)
				EndIf
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then PauseChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, True)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then PauseChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then PauseChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then PauseChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN, True)
	EndIf
End Function

Function ResumeSounds()
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) = True Then ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, False)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) = True Then ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, False)
			EndIf
		EndIf
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) = True Then ResumeChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, False)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then ResumeChannel(n\SoundCHN)
			Else
				If n\soundchn_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, False)
				EndIf
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then ResumeChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, False)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then ResumeChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then ResumeChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then ResumeChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		SetStreamPaused_Strict(IntercomStreamCHN, False)
	EndIf
End Function

Function KillSounds()
	Local i%, e.Events, n.NPCs, d.Doors, dem.DevilEmitters, snd.Sound
	
	For i = 0 To 9
		If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
	Next
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) = True Then StopChannel(e\SoundCHN3)
			Else
				StopStream_Strict(e\SoundCHN3)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) = True Then StopChannel(n\SoundCHN)
			Else
				StopStream_Strict(n\SoundCHN)
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) = True Then StopChannel(n\SoundCHN2)
			Else
				StopStream_Strict(n\SoundCHN2)
			EndIf
		EndIf
	Next
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) = True Then StopChannel(d\SoundCHN)
		EndIf
	Next
	
	For dem.DevilEmitters = Each DevilEmitters
		If dem\SoundCHN <> 0 Then
			If ChannelPlaying(dem\SoundCHN) = True Then StopChannel(dem\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) = True Then StopChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) = True Then StopChannel(BreathCHN)
	EndIf
	
	If IntercomStreamCHN <> 0
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	If EnableSFXRelease
		For snd.Sound = Each Sound
			If snd\InternalHandle <> 0 Then
				FreeSound(snd\InternalHandle)
				snd\InternalHandle = 0
				snd\ReleaseTime = 0
			EndIf
		Next
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				StopChannel(snd\Channels[i])
			EndIf
		Next
	Next
End Function

Function GetStepSound(Entity%)
    Local Picker%, Brush%, Texture%, Name$
    Local mat.Materials
    
    Picker = LinePick(EntityX(Entity), EntityY(Entity), EntityZ(Entity), 0, -1, 0)
    If Picker <> 0 Then
        If GetEntityType(Picker) <> HIT_MAP Then Return(0)
        Brush = GetSurfaceBrush(GetSurface(Picker, CountSurfaces(Picker)))
        If Brush <> 0 Then
            Texture = GetBrushTexture(Brush, 3)
            If Texture <> 0 Then
                Name = StripPath(TextureName(Texture))
                If (Name <> "") Then FreeTexture(Texture)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							FreeBrush(Brush)
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			Texture = GetBrushTexture(Brush, 2)
			If Texture <> 0 Then
				Name = StripPath(TextureName(Texture))
				If (Name <> "") Then FreeTexture(Texture)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							FreeBrush(Brush)
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
			Texture = GetBrushTexture(Brush, 1)
			If Texture <> 0 Then
				Name = StripPath(TextureName(Texture))
				If (Name <> "") Then FreeTexture(Texture)
				FreeBrush(Brush)
				For mat.Materials = Each Materials
					If mat\Name = Name Then
						If mat\StepSound > 0 Then
							Return(mat\StepSound - 1)
						EndIf
						Exit
					EndIf
				Next                
			EndIf
		EndIf
	EndIf
	Return(0)
End Function

Function UpdateSoundOrigin2(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume * (1. - Dist))
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume(CHN, 0)
		EndIf 
	EndIf
End Function

Function UpdateSoundOrigin(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range#
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume# * (1.0 - Dist) * SFXVolume)
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume (CHN, 0)
		EndIf 
	EndIf
End Function

Function PlayAnnouncement(File$) ; ~ This function streams the announcement currently playing
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(File, SFXVolume, 0)
End Function

Function UpdateStreamSounds()
	Local e.Events
	
	If FPSfactor > 0.0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN, SFXVolume)
		EndIf
		For e = Each Events
			If e\SoundCHN <> 0 Then
				If e\SoundCHN_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN, SFXVolume)
				EndIf
			EndIf
			
			If e\SoundCHN2 <> 0 Then
				If e\SoundCHN2_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN2, SFXVolume)
				EndIf
			EndIf
			
			If e\SoundCHN3 <> 0 Then
				If e\SoundCHN3_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN3, SFXVolume)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
			If PlayerRoom\RoomTemplate\Name <> "dimension1499" Then
				For e = Each Events
					If e\SoundCHN <> 0 And e\SoundCHN_IsStream Then
						StopStream_Strict(e\SoundCHN)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
					
					If e\SoundCHN2 <> 0 And e\SoundCHN2_IsStream Then
						StopStream_Strict(e\SoundCHN2)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
					
					If e\SoundCHN3 <> 0 And e\SoundCHN3_IsStream Then
						StopStream_Strict(e\SoundCHN3)
						e\SoundCHN = 0
						e\SoundCHN_IsStream = 0
					EndIf
				Next
			EndIf
		EndIf
	EndIf
End Function

Function ControlSoundVolume()
	Local snd.Sound, i%
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			ChannelVolume(snd\Channels[i], SFXVolume)
		Next
	Next
End Function

Function LoadAllSounds()
	Local i%
	
	For i = 0 To 2
		OpenDoorSFX(0, i) = LoadSound_Strict("SFX\Door\DoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(0, i) = LoadSound_Strict("SFX\Door\DoorClose" + (i + 1) + ".ogg")
		OpenDoorSFX(2, i) = LoadSound_Strict("SFX\Door\Door2Open" + (i + 1) + ".ogg")
		CloseDoorSFX(2, i) = LoadSound_Strict("SFX\Door\Door2Close" + (i + 1) + ".ogg")
		OpenDoorSFX(3, i) = LoadSound_Strict("SFX\Door\ElevatorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(3, i) = LoadSound_Strict("SFX\Door\ElevatorClose" + (i + 1) + ".ogg")
	Next
	For i = 0 To 2
		OpenDoorSFX(1, i) = LoadSound_Strict("SFX\Door\BigDoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(1, i) = LoadSound_Strict("SFX\Door\BigDoorClose" + (i + 1) + ".ogg")
	Next
	For i = 0 To 2
		BigDoorErrorSFX(i) = LoadSound_Strict("SFX\Door\BigDoorError" + (i + 1) + ".ogg")
	Next
	
	KeyCardSFX1 = LoadSound_Strict("SFX\Interact\KeyCardUse1.ogg")
	KeyCardSFX2 = LoadSound_Strict("SFX\Interact\KeyCardUse2.ogg")
	ButtonSFX2 = LoadSound_Strict("SFX\Interact\Button2.ogg")
	ScannerSFX1 = LoadSound_Strict("SFX\Interact\ScannerUse1.ogg")
	ScannerSFX2 = LoadSound_Strict("SFX\Interact\ScannerUse2.ogg")
	
	OpenDoorFastSFX = LoadSound_Strict("SFX\Door\DoorOpenFast.ogg")
	CautionSFX% = LoadSound_Strict("SFX\Room\LockroomSiren.ogg")
	
	CameraSFX = LoadSound_Strict("SFX\General\Camera.ogg") 
	
	StoneDragSFX = LoadSound_Strict("SFX\SCP\173\StoneDrag.ogg")
	
	GunshotSFX = LoadSound_Strict("SFX\General\Gunshot.ogg")
	Gunshot2SFX = LoadSound_Strict("SFX\General\Gunshot2.ogg")
	Gunshot3SFX = LoadSound_Strict("SFX\General\BulletMiss.ogg")
	BullethitSFX = LoadSound_Strict("SFX\General\BulletHit.ogg")
	
	TeslaIdleSFX = LoadSound_Strict("SFX\Room\Tesla\Idle.ogg")
	TeslaActivateSFX = LoadSound_Strict("SFX\Room\Tesla\WindUp.ogg")
	TeslaPowerUpSFX = LoadSound_Strict("SFX\Room\Tesla\PowerUp.ogg")
	TeslaShockSFX = LoadSound_Strict("SFX\Room\Tesla\Shock.ogg")
	
	MagnetUpSFX = LoadSound_Strict("SFX\Room\106Chamber\MagnetUp.ogg") 
	MagnetDownSFX = LoadSound_Strict("SFX\Room\106Chamber\MagnetDown.ogg")
	
	For i = 0 To 3
		DecaySFX(i) = LoadSound_Strict("SFX\SCP\106\Decay" + i + ".ogg")
	Next
	
	BurstSFX = LoadSound_Strict("SFX\Room\TunnelBurst.ogg")
	
	For i = 0 To 2
		RustleSFX(i) = LoadSound_Strict("SFX\SCP\372\Rustle" + i + ".ogg")
	Next
	
	Death914SFX = LoadSound_Strict("SFX\SCP\914\PlayerDeath.ogg") 
	Use914SFX = LoadSound_Strict("SFX\SCP\914\PlayerUse.ogg")
	MachineSFX = LoadSound_Strict("SFX\SCP\914\Refining.ogg")
	
	For i = 0 To 3
		DripSFX(i) = LoadSound_Strict("SFX\Character\D9341\BloodDrip" + i + ".ogg")
	Next
	
	LeverSFX = LoadSound_Strict("SFX\Interact\LeverFlip.ogg") 
	
	LightSFX = LoadSound_Strict("SFX\General\LightSwitch.ogg")
	
	ButtGhostSFX = LoadSound_Strict("SFX\SCP\Joke\789J.ogg")
	
	RadioSFX(1, 0) = LoadSound_Strict("SFX\Radio\RadioAlarm.ogg")
	RadioSFX(1, 1) = LoadSound_Strict("SFX\Radio\RadioAlarm2.ogg")
	For i = 0 To 8
		RadioSFX(2, i) = LoadSound_Strict("SFX\Radio\SCPRadio" + i + ".ogg")
	Next
	RadioSquelch = LoadSound_Strict("SFX\Radio\Squelch.ogg")
	RadioStatic = LoadSound_Strict("SFX\Radio\Static.ogg")
	RadioBuzz = LoadSound_Strict("SFX\Radio\Buzz.ogg")
	
	ElevatorBeepSFX = LoadSound_Strict("SFX\General\Elevator\Beep.ogg") 
	ElevatorMoveSFX = LoadSound_Strict("SFX\General\Elevator\Moving.ogg") 
	
	For i = 0 To 3
		PickSFX(i) = LoadSound_Strict("SFX\Interact\PickItem" + i + ".ogg")
	Next
	
	AmbientSFXAmount(0) = 11
	AmbientSFXAmount(1) = 11
	AmbientSFXAmount(2) = 12
	AmbientSFXAmount(3) = 15
	AmbientSFXAmount(4) = 5
	AmbientSFXAmount(5) = 10
	
	For i = 0 To 2
		OldManSFX(i) = LoadSound_Strict("SFX\SCP\106\Corrosion" + (i + 1) + ".ogg")
	Next
	OldManSFX(3) = LoadSound_Strict("SFX\SCP\106\Laugh.ogg")
	OldManSFX(4) = LoadSound_Strict("SFX\SCP\106\Breathing.ogg")
	OldManSFX(5) = LoadSound_Strict("SFX\Room\PocketDimension\Enter.ogg")
	For i = 0 To 2
		OldManSFX(i + 6) = LoadSound_Strict("SFX\SCP\106\WallDecay" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 2
		Scp173SFX(i) = LoadSound_Strict("SFX\SCP\173\Rattle" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 11
		HorrorSFX(i) = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	For i = 14 To 15
		HorrorSFX(i) = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	
	For i = 5 To 7
		IntroSFX(i) = LoadSound_Strict("SFX\Room\Intro\Bang" + (i - 4) + ".ogg")
	Next
	For i = 8 To 10
		IntroSFX(i) = LoadSound_Strict("SFX\Room\Intro\Light" + (i - 7) + ".ogg")
	Next
	IntroSFX(11) = LoadSound_Strict("SFX\Room\Intro\173Vent.ogg")
	
	AlarmSFX(0) = LoadSound_Strict("SFX\Alarm\Alarm.ogg")
	For i = 2 To 4
		AlarmSFX(i) = LoadSound_Strict("SFX\Alarm\Alarm" + (i + 1) + ".ogg")
	Next
	
	HeartBeatSFX = LoadSound_Strict("SFX\Character\D9341\Heartbeat.ogg")
	
	For i = 0 To 4
		BreathSFX(0, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + ".ogg")
		BreathSFX(1, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + "Gas.ogg")
	Next
	
	For i = 0 To 2
		NeckSnapSFX(i) =  LoadSound_Strict("SFX\SCP\173\NeckSnap" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 12
		DamageSFX(i) = LoadSound_Strict("SFX\Character\D9341\Damage" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 2
		CoughSFX(i) = LoadSound_Strict("SFX\Character\D9341\Cough" + (i + 1) + ".ogg")
	Next
	
	ApacheSFX = LoadSound_Strict("SFX\Character\Apache\Propeller.ogg")
	
	For i = 0 To 7
		StepSFX(0, 0, i) = LoadSound_Strict("SFX\Step\Step" + (i + 1) + ".ogg")
		StepSFX(1, 0, i) = LoadSound_Strict("SFX\Step\StepMetal" + (i + 1) + ".ogg")
		StepSFX(0, 1, i)= LoadSound_Strict("SFX\Step\Run" + (i + 1) + ".ogg")
		StepSFX(1, 1, i) = LoadSound_Strict("SFX\Step\RunMetal" + (i + 1) + ".ogg")
		If i < 3
			StepSFX(2, 0, i) = LoadSound_Strict("SFX\Character\MTF\Step" + (i + 1) + ".ogg")
			StepSFX(3, 0, i) = LoadSound_Strict("SFX\SCP\049\Step"+ (i + 1) + ".ogg")
		EndIf
		If i < 4
			StepSFX(4, 0, i) = LoadSound_Strict("SFX\Step\SCP\StepSCP" + (i + 1) + ".ogg")
		EndIf
	Next
	
	For i = 0 To 2
		Step2SFX(i) = LoadSound_Strict("SFX\Step\StepPD" + (i + 1) + ".ogg")
		Step2SFX(i + 3) = LoadSound_Strict("SFX\Step\StepForest" + (i + 1) + ".ogg")
	Next 
	
	MissSFX = LoadSound_Strict("SFX\General\Miss.ogg")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D