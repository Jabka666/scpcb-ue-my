Function PlaySound2%(SoundHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, HasSubtitles% = False, SubID% = ANNOUNCEMENT)
	Range = Max(Range, 1.0)
	
	Local SoundCHN% = 0
	
	If Volume > 0.0 Then 
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SoundCHN = PlaySound_Strict(SoundHandle, HasSubtitles, SubID)
			
			ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * opt\SFXVolume)
			ChannelPan(SoundCHN, PanValue)			
		EndIf
	EndIf
	Return(SoundCHN)
End Function

Function LoopSound2%(SoundHandle%, CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, HasSubtitles% = False, SubID% = ANNOUNCEMENT)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
		
		If CHN = 0 Then
			CHN = PlaySound_Strict(SoundHandle, HasSubtitles, SubID)
		Else
			If (Not ChannelPlaying(CHN)) Then CHN = PlaySound_Strict(SoundHandle, HasSubtitles, SubID)
		EndIf
		
		ChannelVolume(CHN, Volume * (1.0 - Dist) * opt\SFXVolume)
		ChannelPan(CHN, PanValue)
	Else
		If CHN <> 0 Then
			ChannelVolume(CHN, 0)
		EndIf 
	EndIf
	Return(CHN)
End Function

Function LoadTempSound(File$)
	If TempSounds[TempSoundIndex] <> 0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(File)
	TempSounds[TempSoundIndex] = TempSound
	TempSoundIndex = ((TempSoundIndex + 1) Mod 10)
	Return(TempSound)
End Function

Function UpdateMusic()
	If ConsoleFlush Then
		If (Not ChannelPlaying(ConsoleMusPlay)) Then ConsoleMusPlay = PlaySound_Strict(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay Then ; ~ Playing the wrong clip, fade out
			opt\CurrMusicVolume = Max(opt\CurrMusicVolume - (fpst\FPSFactor[0] / 250.0), 0.0)
			If opt\CurrMusicVolume = 0.0 Then
				If NowPlaying < 66 Then
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic = 0
			EndIf
		Else ; ~ Playing the right clip
			opt\CurrMusicVolume = opt\CurrMusicVolume + (opt\MusicVolume - opt\CurrMusicVolume) * (0.1 * fpst\FPSFactor[0])
		EndIf
		
		If NowPlaying < 66 Then
			If CurrMusic = 0
				MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
				CurrMusic = 1
			EndIf
			SetStreamVolume_Strict(MusicCHN, opt\CurrMusicVolume)
		EndIf
	Else
		If fpst\FPSFactor[0] > 0.0 Lor OptionsMenu = 2 Then
			If (Not ChannelPlaying(MusicCHN)) Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume(MusicCHN, 1.0 * opt\MusicVolume)
		EndIf
	EndIf
End Function 

Function PauseSounds()
	Local e.Events, n.NPCs, d.Doors
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) Then PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, True)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) Then PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, True)
			EndIf
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) Then PauseChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, True)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) Then PauseChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, True)
				EndIf
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then PauseChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, True)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then PauseChannel(d\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then PauseChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		If ChannelPlaying(BreathGasRelaxedCHN) Then PauseChannel(BreathGasRelaxedCHN)
	EndIf
	
	If VomitCHN <> 0 Then
		If ChannelPlaying(VomitCHN) Then PauseChannel(VomitCHN)
	EndIf
	
	If CoughCHN <> 0 Then
		If ChannelPlaying(CoughCHN) Then PauseChannel(CoughCHN)
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		If ChannelPlaying(SCRAMBLECHN) Then PauseChannel(SCRAMBLECHN)
	EndIf
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN, True)
	EndIf
End Function

Function ResumeSounds()
	Local e.Events, n.NPCs, d.Doors
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) Then ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, False)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) Then ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, False)
			EndIf
		EndIf
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) Then ResumeChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, False)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) Then ResumeChannel(n\SoundCHN)
			Else
				If n\SoundCHN_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN, False)
				EndIf
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then ResumeChannel(n\SoundCHN2)
			Else
				If n\SoundCHN2_IsStream = True
					SetStreamPaused_Strict(n\SoundCHN2, False)
				EndIf
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then ResumeChannel(d\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then ResumeChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		If ChannelPlaying(BreathGasRelaxedCHN) Then ResumeChannel(BreathGasRelaxedCHN)
	EndIf
	
	If VomitCHN <> 0 Then
		If ChannelPlaying(VomitCHN) Then ResumeChannel(VomitCHN)
	EndIf
	
	If CoughCHN <> 0 Then
		If ChannelPlaying(CoughCHN) Then ResumeChannel(CoughCHN)
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		If ChannelPlaying(SCRAMBLECHN) Then ResumeChannel(SCRAMBLECHN)
	EndIf
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN, False)
	EndIf
End Function

Function KillSounds()
	Local i%, e.Events, n.NPCs, d.Doors, snd.Sound
	
	For i = 0 To 9
		If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
	Next
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				If ChannelPlaying(e\SoundCHN3) Then StopChannel(e\SoundCHN3)
			Else
				StopStream_Strict(e\SoundCHN3)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN)
			Else
				StopStream_Strict(n\SoundCHN)
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2)
			Else
				StopStream_Strict(n\SoundCHN2)
			EndIf
		EndIf
	Next
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			If ChannelPlaying(d\SoundCHN) Then StopChannel(d\SoundCHN)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		If ChannelPlaying(AmbientSFXCHN) Then StopChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		If ChannelPlaying(BreathGasRelaxedCHN) Then StopChannel(BreathGasRelaxedCHN)
	EndIf
	
	If VomitCHN <> 0 Then
		If ChannelPlaying(VomitCHN) Then StopChannel(VomitCHN)
	EndIf
	
	If CoughCHN <> 0 Then
		If ChannelPlaying(CoughCHN) Then StopChannel(CoughCHN)
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		If ChannelPlaying(SCRAMBLECHN) Then StopChannel(SCRAMBLECHN)
	EndIf
	
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	If opt\EnableSFXRelease Then
		For snd.Sound = Each Sound
			If snd\InternalHandle <> 0 Then
				FreeSound_Strict(snd\InternalHandle)
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
                If Name <> "" Then DeleteSingleTextureEntryFromCache(Texture)
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
				If Name <> "" Then DeleteSingleTextureEntryFromCache(Texture)
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
				If Name <> "" Then DeleteSingleTextureEntryFromCache(Texture)
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

Function UpdateSoundOrigin(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume * (1.0 - Dist) * opt\SFXVolume)
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume(CHN, 0.0)
		EndIf 
	EndIf
End Function

Function UpdateSoundOrigin2(CHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			ChannelVolume(CHN, Volume * (1.0 - Dist))
			ChannelPan(CHN, PanValue)
		EndIf
	Else
		If CHN <> 0 Then
			ChannelVolume(CHN, 0.0)
		EndIf 
	EndIf
End Function

Function PlayAnnouncement(File$) ; ~ This function streams the announcement currently playing
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN)
		IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(File, opt\SFXVolume, 0)
End Function

Function UpdateStreamSounds()
	If fpst\FPSFactor[0] > 0.0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN, opt\SFXVolume)
		EndIf
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		If (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(me\Collider) =< 1040.0 * RoomScale) And PlayerRoom\RoomTemplate\Name <> "gatea" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN)
				IntercomStreamCHN = 0
			EndIf
		EndIf
	EndIf
End Function

Function ControlSoundVolume()
	Local snd.Sound, i%
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			ChannelVolume(snd\Channels[i], opt\SFXVolume)
		Next
	Next
End Function

Function UpdateDeaf()
	If me\DeafTimer > 0.0
		me\DeafTimer = me\DeafTimer - fpst\FPSFactor[0]
		opt\SFXVolume = 0.0
		If opt\SFXVolume > 0.0 Then
			ControlSoundVolume()
		EndIf
	Else
		me\DeafTimer = 0.0
		opt\SFXVolume = opt\PrevSFXVolume
		If me\Deaf Then ControlSoundVolume()
		me\Deaf = False
	EndIf
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
		OpenDoorSFX(1, i) = LoadSound_Strict("SFX\Door\BigDoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(1, i) = LoadSound_Strict("SFX\Door\BigDoorClose" + (i + 1) + ".ogg")
		BigDoorErrorSFX[i] = LoadSound_Strict("SFX\Door\BigDoorError" + (i + 1) + ".ogg")
	Next
	
	KeyCardSFX1 = LoadSound_Strict("SFX\Interact\KeyCardUse1.ogg")
	KeyCardSFX2 = LoadSound_Strict("SFX\Interact\KeyCardUse2.ogg")
	ScannerSFX1 = LoadSound_Strict("SFX\Interact\ScannerUse1.ogg")
	ScannerSFX2 = LoadSound_Strict("SFX\Interact\ScannerUse2.ogg")
	
	OpenDoorFastSFX = LoadSound_Strict("SFX\Door\DoorOpenFast.ogg")
	CautionSFX = LoadSound_Strict("SFX\Room\LockroomSiren.ogg")
	
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
		DecaySFX[i] = LoadSound_Strict("SFX\SCP\106\Decay" + i + ".ogg")
	Next
	
	BurstSFX = LoadSound_Strict("SFX\Room\TunnelBurst.ogg")
	
	For i = 0 To 5
		RustleSFX[i] = LoadSound_Strict("SFX\SCP\372\Rustle" + i + ".ogg")
	Next
	
	Death914SFX = LoadSound_Strict("SFX\SCP\914\PlayerDeath.ogg") 
	Use914SFX = LoadSound_Strict("SFX\SCP\914\PlayerUse.ogg")
	MachineSFX = LoadSound_Strict("SFX\SCP\914\Refining.ogg")
	
	For i = 0 To 3
		DripSFX[i] = LoadSound_Strict("SFX\Character\D9341\BloodDrip" + i + ".ogg")
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
	RadioStatic895 = LoadSound_Strict("SFX\Radio\Static895.ogg")
	RadioBuzz = LoadSound_Strict("SFX\Radio\Buzz.ogg")
	
	ElevatorBeepSFX = LoadSound_Strict("SFX\General\Elevator\Beep.ogg") 
	ElevatorMoveSFX = LoadSound_Strict("SFX\General\Elevator\Moving.ogg") 
	
	For i = 0 To 3
		PickSFX[i] = LoadSound_Strict("SFX\Interact\PickItem" + i + ".ogg")
	Next
	
	AmbientSFXAmount[0] = 11
	AmbientSFXAmount[1] = 11
	AmbientSFXAmount[2] = 12
	AmbientSFXAmount[3] = 15
	AmbientSFXAmount[4] = 5
	AmbientSFXAmount[5] = 10
	
	For i = 0 To 2
		OldManSFX[i] = LoadSound_Strict("SFX\SCP\106\Corrosion" + (i + 1) + ".ogg")
	Next
	OldManSFX[3] = LoadSound_Strict("SFX\SCP\106\Laugh.ogg")
	OldManSFX[4] = LoadSound_Strict("SFX\SCP\106\Breathing.ogg")
	OldManSFX[5] = LoadSound_Strict("SFX\Room\PocketDimension\Enter.ogg")
	For i = 0 To 2
		OldManSFX[i + 6] = LoadSound_Strict("SFX\SCP\106\WallDecay" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 2
		Scp173SFX[i] = LoadSound_Strict("SFX\SCP\173\Rattle" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 11
		HorrorSFX[i] = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	For i = 14 To 15
		HorrorSFX[i] = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
	Next
	
	For i = 5 To 7
		IntroSFX[i] = LoadSound_Strict("SFX\Room\Intro\Bang" + (i - 4) + ".ogg")
	Next
	For i = 8 To 10
		IntroSFX[i] = LoadSound_Strict("SFX\Room\Intro\Light" + (i - 7) + ".ogg")
	Next
	IntroSFX[11] = LoadSound_Strict("SFX\Room\Intro\173Vent.ogg")
	
	AlarmSFX[0] = LoadSound_Strict("SFX\Alarm\Alarm.ogg")
	For i = 2 To 4
		AlarmSFX[i] = LoadSound_Strict("SFX\Alarm\Alarm" + (i + 1) + ".ogg")
	Next
	
	HeartBeatSFX = LoadSound_Strict("SFX\Character\D9341\HeartBeat.ogg")
	
	For i = 0 To 4
		BreathSFX(0, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + ".ogg")
		BreathSFX(1, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + "Gas.ogg")
	Next
	
	For i = 0 To 2
		NeckSnapSFX[i] = LoadSound_Strict("SFX\SCP\173\NeckSnap" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 13
		DamageSFX[i] = LoadSound_Strict("SFX\Character\D9341\Damage" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 2
		CoughSFX[i] = LoadSound_Strict("SFX\Character\D9341\Cough" + (i + 1) + ".ogg")
	Next
	
	ApacheSFX = LoadSound_Strict("SFX\Character\Apache\Propeller.ogg")
	
	For i = 0 To 7
		StepSFX(0, 0, i) = LoadSound_Strict("SFX\Step\Step" + (i + 1) + ".ogg")
		StepSFX(1, 0, i) = LoadSound_Strict("SFX\Step\StepMetal" + (i + 1) + ".ogg")
		StepSFX(0, 1, i) = LoadSound_Strict("SFX\Step\Run" + (i + 1) + ".ogg")
		StepSFX(1, 1, i) = LoadSound_Strict("SFX\Step\RunMetal" + (i + 1) + ".ogg")
		If i < 3 Then
			StepSFX(2, 0, i) = LoadSound_Strict("SFX\Step\StepPD" + (i + 1) + ".ogg")
			StepSFX(3, 0, i) = LoadSound_Strict("SFX\Step\StepForest" + (i + 1) + ".ogg")
			StepSFX(4, 0, i) = LoadSound_Strict("SFX\Character\MTF\Step" + (i + 1) + ".ogg")
		EndIf
		If i < 7 Then
			StepSFX(5, 0, i) = LoadSound_Strict("SFX\Step\SCP\StepMetal" + (i + 1) + ".ogg")
		EndIf
	Next
	
	MissSFX = LoadSound_Strict("SFX\General\Miss.ogg")
	
	BreathGasRelaxedSFX = LoadSound_Strict("SFX\Character\D9341\BreathGasRelaxed.ogg")
	
	CrouchSFX = LoadSound_Strict("SFX\Character\D9341\Crouch.ogg")
	
	SCRAMBLESFX = LoadSound_Strict("SFX\General\SCRAMBLE.ogg")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D