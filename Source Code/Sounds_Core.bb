Function PlaySound2%(SoundHandle%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	Local SoundCHN% = 0
	
	If Volume > 0.0 Then 
		Local Dist# = EntityDistance(Cam, Entity) / Range
		
		If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
			Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
			
			SoundCHN = PlaySound_Strict(SoundHandle)
			
			ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * opt\SFXVolume * opt\MasterVolume)
			ChannelPan(SoundCHN, PanValue)
		EndIf
	Else
		If SoundCHN <> 0 Then
			ChannelVolume(SoundCHN, 0.0)
		EndIf 
	EndIf
	Return(SoundCHN)
End Function

Function LoopSound2%(SoundHandle%, SoundCHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0)
	Range = Max(Range, 1.0)
	
	If Volume > 0.0 Then
		Local Dist# = EntityDistance(Cam, Entity) / Range
		Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
		
		If (Not SoundCHN) Then
			SoundCHN = PlaySound_Strict(SoundHandle)
		Else
			If (Not ChannelPlaying(SoundCHN)) Then SoundCHN = PlaySound_Strict(SoundHandle)
		EndIf
		
		ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * opt\SFXVolume * opt\MasterVolume)
		ChannelPan(SoundCHN, PanValue)
	Else
		If SoundCHN <> 0 Then
			ChannelVolume(SoundCHN, 0.0)
		EndIf 
	EndIf
	Return(SoundCHN)
End Function

Function UpdateSoundOrigin%(SoundCHN%, Cam%, Entity%, Range# = 10.0, Volume# = 1.0, SFXVolume% = True)
	If SoundCHN <> 0 Then
		If ChannelPlaying(SoundCHN) Then
			Range = Max(Range, 1.0)
			
			If Volume > 0.0 Then
				Local Dist# = EntityDistance(Cam, Entity) / Range
				
				If 1.0 - Dist > 0.0 And 1.0 - Dist < 1.0 Then
					Local PanValue# = Sin(-DeltaYaw(Cam, Entity))
					
					ChannelVolume(SoundCHN, Volume * (1.0 - Dist) * ((Not SFXVolume) + (SFXVolume * opt\SFXVolume * opt\MasterVolume)))
					ChannelPan(SoundCHN, PanValue)
				Else
					ChannelVolume(SoundCHN, 0.0)
				EndIf
			Else
				ChannelVolume(SoundCHN, 0.0)
			EndIf
		EndIf
	EndIf
End Function

Function PlayMTFSound%(SoundHandle%, n.NPCs)
	If n <> Null Then
		n\SoundCHN = PlaySound2(SoundHandle, Camera, n\Collider, 8.0)	
	EndIf
	
	If SelectedItem <> Null Then
		If SelectedItem\State2 = 3.0 And SelectedItem\State > 0.0 Then 
			Select SelectedItem\ItemTemplate\TempName 
				Case "radio", "fineradio", "18vradio"
					;[Block]
					If SoundHandle <> MTFSFX[0] Lor (Not ChannelPlaying(RadioCHN[3])) Then
						If RadioCHN[3] <> 0 Then StopChannel(RadioCHN[3])
						RadioCHN[3] = PlaySound_Strict(SoundHandle)
					EndIf
					;[End Block]
			End Select
		EndIf
	EndIf 
End Function

Function LoadEventSound%(e.Events, File$, Number% = 0)
	If Number = 0 Then
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		e\Sound = LoadSound_Strict(File)
		Return(e\Sound)
	ElseIf Number = 1 Then
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		e\Sound2 = LoadSound_Strict(File)
		Return(e\Sound2)
	ElseIf Number = 2 Then
		If e\Sound3 <> 0 Then FreeSound_Strict(e\Sound3) : e\Sound3 = 0
		e\Sound3 = LoadSound_Strict(File)
		Return(e\Sound3)
	EndIf
End Function

Function LoadTempSound%(File$)
	Local TempSound%
	
	If TempSounds[TempSoundIndex] <> 0 Then FreeSound_Strict(TempSounds[TempSoundIndex])
	TempSound = LoadSound_Strict(File)
	TempSounds[TempSoundIndex] = TempSound
	TempSoundIndex = ((TempSoundIndex + 1) Mod 10)
	Return(TempSound)
End Function

Function UpdateMusic%()
	If ConsoleFlush Then
		If (Not ChannelPlaying(ConsoleMusPlay)) Then ConsoleMusPlay = PlaySound_Strict(ConsoleMusFlush)
	ElseIf (Not PlayCustomMusic)
		If NowPlaying <> ShouldPlay Then ; ~ Playing the wrong clip, fade out
			opt\CurrMusicVolume = Max(opt\CurrMusicVolume - (fps\Factor[0] / 250.0), 0.0)
			If opt\CurrMusicVolume = 0.0 Then
				If NowPlaying < 66 Then
					StopStream_Strict(MusicCHN)
				EndIf
				NowPlaying = ShouldPlay
				MusicCHN = 0
				CurrMusic = False
			EndIf
		Else ; ~ Playing the right clip
			opt\CurrMusicVolume = opt\CurrMusicVolume + (opt\MusicVolume - opt\CurrMusicVolume) * (0.1 * fps\Factor[0])
		EndIf
		
		If NowPlaying < 66 Then
			If (Not CurrMusic) Then
				MusicCHN = StreamSound_Strict("SFX\Music\" + Music[NowPlaying] + ".ogg", 0.0, Mode)
				CurrMusic = True
			EndIf
			SetStreamVolume_Strict(MusicCHN, opt\CurrMusicVolume * opt\MasterVolume)
		EndIf
	Else
		If fps\Factor[0] > 0.0 Lor OptionsMenu = 2 Then
			If (Not ChannelPlaying(MusicCHN)) Then MusicCHN = PlaySound_Strict(CustomMusic)
			ChannelVolume(MusicCHN, opt\MusicVolume * opt\MasterVolume)
		EndIf
	EndIf
End Function 

Function PauseSounds%()
	Local e.Events, n.NPCs, d.Doors
	Local i%
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				PauseChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, True)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				PauseChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, True)
			EndIf
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				PauseChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, True)
			EndIf
		EndIf		
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				PauseChannel(n\SoundCHN)
			Else
				SetStreamPaused_Strict(n\SoundCHN, True)
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				PauseChannel(n\SoundCHN2)
			Else
				SetStreamPaused_Strict(n\SoundCHN2, True)
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			PauseChannel(d\SoundCHN)
		EndIf
		If d\SoundCHN2 <> 0 Then
			PauseChannel(d\SoundCHN2)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		PauseChannel(AmbientSFXCHN)
	EndIf
	
	If BreathCHN <> 0 Then
		PauseChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		PauseChannel(BreathGasRelaxedCHN)
	EndIf
	
	If VomitCHN <> 0 Then
		PauseChannel(VomitCHN)
	EndIf
	
	If CoughCHN <> 0 Then
		PauseChannel(CoughCHN)
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		PauseChannel(SCRAMBLECHN)
	EndIf
	
	For i = 0 To 1
		If LowBatteryCHN[i] <> 0 Then
			PauseChannel(LowBatteryCHN[i])
		EndIf
	Next
	
	; ~ TODO:
	;For i = 0 To 6
	;	If RadioCHN[i] <> 0 Then
	;		PauseChannel(RadioCHN[i])
	;	EndIf
	;Next
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN, True)
	EndIf
End Function

Function ResumeSounds%()
	Local e.Events, n.NPCs, d.Doors
	Local i%
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				ResumeChannel(e\SoundCHN)
			Else
				SetStreamPaused_Strict(e\SoundCHN, False)
			EndIf
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				ResumeChannel(e\SoundCHN2)
			Else
				SetStreamPaused_Strict(e\SoundCHN2, False)
			EndIf
		EndIf
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				ResumeChannel(e\SoundCHN3)
			Else
				SetStreamPaused_Strict(e\SoundCHN3, False)
			EndIf
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream)
				ResumeChannel(n\SoundCHN)
			Else
				SetStreamPaused_Strict(n\SoundCHN, False)
			EndIf
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream)
				ResumeChannel(n\SoundCHN2)
			Else
				SetStreamPaused_Strict(n\SoundCHN2, False)
			EndIf
		EndIf
	Next	
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			ResumeChannel(d\SoundCHN)
		EndIf
		If d\SoundCHN2 <> 0 Then
			ResumeChannel(d\SoundCHN2)
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		ResumeChannel(AmbientSFXCHN)
	EndIf	
	
	If BreathCHN <> 0 Then
		ResumeChannel(BreathCHN)
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		ResumeChannel(BreathGasRelaxedCHN)
	EndIf
	
	If VomitCHN <> 0 Then
		ResumeChannel(VomitCHN)
	EndIf
	
	If CoughCHN <> 0 Then
		ResumeChannel(CoughCHN)
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		ResumeChannel(SCRAMBLECHN)
	EndIf
	
	For i = 0 To 1
		If LowBatteryCHN[i] <> 0 Then
			ResumeChannel(LowBatteryCHN[i])
		EndIf
	Next
	
	; ~ TODO:
	;For i = 0 To 6
	;	If RadioCHN[i] <> 0 Then
	;		ResumeChannel(RadioCHN[i])
	;	EndIf
	;Next
	
	If IntercomStreamCHN <> 0 Then
		SetStreamPaused_Strict(IntercomStreamCHN, False)
	EndIf
End Function

Function KillSounds%()
	Local e.Events, n.NPCs, d.Doors, snd.Sound, sub.Subtitles
	Local i%
	
	For i = 0 To 9
		If TempSounds[i] <> 0 Then FreeSound_Strict(TempSounds[i]) : TempSounds[i] = 0
	Next
	
	For e.Events = Each Events
		If e\SoundCHN <> 0 Then
			If (Not e\SoundCHN_IsStream) Then
				StopChannel(e\SoundCHN)
			Else
				StopStream_Strict(e\SoundCHN)
			EndIf
			e\SoundCHN = 0
		EndIf
		
		If e\SoundCHN2 <> 0 Then
			If (Not e\SoundCHN2_IsStream) Then
				StopChannel(e\SoundCHN2)
			Else
				StopStream_Strict(e\SoundCHN2)
			EndIf
			e\SoundCHN2 = 0
		EndIf	
		
		If e\SoundCHN3 <> 0 Then
			If (Not e\SoundCHN3_IsStream) Then
				StopChannel(e\SoundCHN3)
			Else
				StopStream_Strict(e\SoundCHN3)
			EndIf
			e\SoundCHN3 = 0
		EndIf	
	Next
	
	For n.NPCs = Each NPCs
		If n\SoundCHN <> 0 Then
			If (Not n\SoundCHN_IsStream) Then
				StopChannel(n\SoundCHN)
			Else
				StopStream_Strict(n\SoundCHN)
			EndIf
			n\SoundCHN = 0
		EndIf
		
		If n\SoundCHN2 <> 0 Then
			If (Not n\SoundCHN2_IsStream) Then
				StopChannel(n\SoundCHN2)
			Else
				StopStream_Strict(n\SoundCHN2)
			EndIf
			n\SoundCHN = 0
		EndIf
	Next
	
	For d.Doors = Each Doors
		If d\SoundCHN <> 0 Then
			StopChannel(d\SoundCHN) : d\SoundCHN = 0
		EndIf
		If d\SoundCHN2 <> 0 Then
			StopChannel(d\SoundCHN2) : d\SoundCHN2 = 0
		EndIf
	Next
	
	If AmbientSFXCHN <> 0 Then
		StopChannel(AmbientSFXCHN) : AmbientSFXCHN = 0
	EndIf
	
	If BreathCHN <> 0 Then
		StopChannel(BreathCHN) : BreathCHN = 0
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
	EndIf
	
	If VomitCHN <> 0 Then
		StopChannel(VomitCHN) : VomitCHN = 0
	EndIf
	
	If CoughCHN <> 0 Then
		StopChannel(CoughCHN) : CoughCHN = 0
	EndIf
	
	If SCRAMBLECHN <> 0 Then
		StopChannel(SCRAMBLECHN) : SCRAMBLECHN = 0
	EndIf
	
	For i = 0 To 1
		If LowBatteryCHN[i] <> 0 Then
			StopChannel(LowBatteryCHN[i]) : LowBatteryCHN[i] = 0
		EndIf
	Next
		
	For i = 0 To 6
		If RadioCHN[i] <> 0 Then
			StopChannel(RadioCHN[i]) : RadioCHN[i] = 0
		EndIf
	Next
	
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
	EndIf
	
	If opt\EnableSFXRelease Then
		For snd.Sound = Each Sound
			If snd\InternalHandle <> 0 Then
				FreeSound_Strict(snd\InternalHandle) : snd\InternalHandle = 0
				snd\ReleaseTime = 0
			EndIf
		Next
	EndIf
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			If snd\Channels[i] <> 0 Then
				StopChannel(snd\Channels[i]) : snd\Channels[i] = 0
			EndIf
		Next
	Next
	
	Delete Each Subtitles
End Function

Function StopBreathSound%()
	If BreathCHN <> 0 Then
		StopChannel(BreathCHN) : BreathCHN = 0
	EndIf
	
	If BreathGasRelaxedCHN <> 0 Then
		StopChannel(BreathGasRelaxedCHN) : BreathGasRelaxedCHN = 0
	EndIf
End Function

Function GetStepSound%(Entity%)
	Local mat.Materials
	Local Picker%, Brush%, Texture%, Name$
	
	Picker = LinePick(EntityX(Entity), EntityY(Entity), EntityZ(Entity), 0.0, -1.0, 0.0)
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

Function PlayAnnouncement%(File$) ; ~ This function streams the announcement currently playing
	If IntercomStreamCHN <> 0 Then
		StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
	EndIf
	
	IntercomStreamCHN = StreamSound_Strict(File, opt\SFXVolume * opt\MasterVolume, 0)
End Function

Function UpdateStreamSounds%()
	Local e.Events
	
	If fps\Factor[0] > 0.0 Then
		If IntercomStreamCHN <> 0 Then
			SetStreamVolume_Strict(IntercomStreamCHN, opt\SFXVolume * opt\MasterVolume)
		EndIf
		For e.Events = Each Events
			If e\SoundCHN <> 0 Then
				If e\SoundCHN_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN, opt\SFXVolume * opt\MasterVolume)
				EndIf
			EndIf
			
			If e\SoundCHN2 <> 0 Then
				If e\SoundCHN2_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN2, opt\SFXVolume * opt\MasterVolume)
				EndIf
			EndIf
			
			If e\SoundCHN3 <> 0 Then
				If e\SoundCHN3_IsStream Then
					SetStreamVolume_Strict(e\SoundCHN3, opt\SFXVolume * opt\MasterVolume)
				EndIf
			EndIf
		Next
	EndIf
	
	If (Not PlayerInReachableRoom()) Then
		Local RN$ = PlayerRoom\RoomTemplate\Name
		
		If RN <> "gate_b" And RN <> "gate_a" Then
			If IntercomStreamCHN <> 0 Then
				StopStream_Strict(IntercomStreamCHN) : IntercomStreamCHN = 0
			EndIf
			If RN <> "dimension_1499" Then
				For e.Events = Each Events
					If e\SoundCHN <> 0 Then
						If e\SoundCHN_IsStream Then
							StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
						EndIf
					EndIf
					
					If e\SoundCHN2 <> 0 Then
						If e\SoundCHN2_IsStream Then
							StopStream_Strict(e\SoundCHN2) : e\SoundCHN2 = 0 : e\SoundCHN2_IsStream = False
						EndIf
					EndIf
					
					If e\SoundCHN3 <> 0 Then
						If e\SoundCHN3_IsStream Then
							StopStream_Strict(e\SoundCHN3) : e\SoundCHN3 = 0 : e\SoundCHN3_IsStream = False
						EndIf
					EndIf
				Next
			EndIf
		EndIf
	EndIf
End Function

Function ControlSoundVolume%()
	Local snd.Sound
	Local i%
	
	For snd.Sound = Each Sound
		For i = 0 To 31
			ChannelVolume(snd\Channels[i], opt\SFXVolume * opt\MasterVolume)
		Next
	Next
End Function

Function UpdateDeaf%()
	If me\DeafTimer > 0.0 Then
		me\DeafTimer = me\DeafTimer - fps\Factor[0]
		opt\MasterVolume = 0.0
		If opt\MasterVolume > 0.0 Then
			ControlSoundVolume()
		EndIf
	Else
		me\DeafTimer = 0.0
		If me\Deaf Then ControlSoundVolume()
		me\Deaf = False
	EndIf
End Function

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

; ~ The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Global Music$[30]

Music[0] = "LightContainmentZone"
Music[1] = "HeavyContainmentZone"
Music[2] = "EntranceZone"
Music[3] = "PD"
Music[4] = "Cont_079"
Music[5] = "Gate_B1"
Music[6] = "Gate_B2"
Music[7] = "Room3_storage"
Music[8] = "Cont_049"
Music[9] = "Cont_860_1"
Music[10] = "106Chase"
Music[11] = "Menu"
Music[12] = "860_2Chase"
Music[13] = "Cont_173_Intro"
Music[14] = "Using178"
Music[15] = "PDTrench"
Music[16] = "Cont_205"
Music[17] = "Gate_A"
Music[18] = "1499"
Music[19] = "1499_1Chase"
Music[20] = "049Chase"
Music[21] = "..\Ending\MenuBreath"
Music[22] = "Cont_914"
Music[23] = "Ending"
Music[24] = "Credits"
Music[25] = "SaveMeFrom"
Music[26] = "Cont_106"
Music[27] = "Cont_035"
Music[28] = "Cont_409"
Music[29] = "MaintenanceTunnels"

Global MusicCHN%
MusicCHN = StreamSound_Strict("SFX\Music\" + Music[2] + ".ogg", opt\MusicVolume, Mode)

Global NowPlaying% = 2, ShouldPlay% = 11
Global CurrMusic% = True

Dim OpenDoorSFX%(5, 2), CloseDoorSFX%(3, 2)
Global BigDoorErrorSFX%[3]

Global KeyCardSFX1% 
Global KeyCardSFX2% 
Global ScannerSFX1%
Global ScannerSFX2%

Global DoorBudgeSFX1%
Global DoorBudgeSFX2%
Global DoorLockSFX%

Global OpenDoorFastSFX%
Global CautionSFX% 

Global NuclearSirenSFX%

Global CameraSFX% 

Global StoneDragSFX% 

Global GunshotSFX% 
Global Gunshot2SFX% 
Global Gunshot3SFX% 
Global BulletHitSFX% 

Global TeslaIdleSFX% 
Global TeslaActivateSFX% 
Global TeslaPowerUpSFX% 
Global TeslaShockSFX%

Global MagnetUpSFX%, MagnetDownSFX%
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Global CrouchSFX%

Global DecaySFX%[5]

Global BurstSFX% 

Global HissSFX%

Global RustleSFX%[6]

Global Use914SFX%
Global Death914SFX% 

Global DripSFX%[4]

Global KnobSFX%[2]

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX%(2, 8) 

Global RadioSquelch% 
Global RadioStatic% 
Global RadioStatic895%
Global RadioBuzz% 

Global SCRAMBLESFX%
Global SCRAMBLECHN%

Global NVGSFX%[2]

Global LowBatterySFX%[2]
Global LowBatteryCHN%[2]

Global ElevatorBeepSFX%, ElevatorMoveSFX% 

Global PickSFX%[4]

Global AmbientSFXCHN%, CurrAmbientSFX%
Global AmbientSFXAmount%[6]

Dim AmbientSFX%(5, 15)

Global OldManSFX%[9]

Global Scp173SFX%[3]

Global HorrorSFX%[20]

Global MissSFX%

Global IntroSFX%[12]

Global AlarmSFX%[4]

Global CommotionState%[25]

Global HeartBeatSFX% 

Global VomitSFX%

Dim BreathSFX%(1, 4)
Global BreathCHN%

Global BreathGasRelaxedSFX%
Global BreathGasRelaxedCHN%

Global NeckSnapSFX%[3]

Global DamageSFX%[14]

Global MTFSFX%[2]

Global CoughSFX%[3]
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX%

Global CurrStepSFX%
Dim StepSFX%(2, 1, 7) ; ~ (Normal / Metal, Walk / Run, ID)

Global Step2SFX%[15]

Global VehicleSFX%[2]

Global ExplosionSFX%

Global RadioCHN%[7]

Global IntercomStreamCHN%

Global PlayCustomMusic% = False, CustomMusic% = 0

Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Global UserTrackName$[256]

Function LoadSounds%()
	Local i%
	
	RenderLoading(45, GetLocalString("loading", "sounds"))
	
	For i = 0 To 2
		OpenDoorSFX(DEFAULT_DOOR, i) = LoadSound_Strict("SFX\Door\DoorOpen" + (i + 1) + ".ogg") ; ~ Also one-sided door
		CloseDoorSFX(DEFAULT_DOOR, i) = LoadSound_Strict("SFX\Door\DoorClose" + (i + 1) + ".ogg") ; ~ Also one-sided door
		OpenDoorSFX(ELEVATOR_DOOR, i) = LoadSound_Strict("SFX\Door\ElevatorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(ELEVATOR_DOOR, i) = LoadSound_Strict("SFX\Door\ElevatorClose" + (i + 1) + ".ogg")
		OpenDoorSFX(HEAVY_DOOR, i) = LoadSound_Strict("SFX\Door\Door2Open" + (i + 1) + ".ogg")
		CloseDoorSFX(HEAVY_DOOR, i) = LoadSound_Strict("SFX\Door\Door2Close" + (i + 1) + ".ogg")
		OpenDoorSFX(BIG_DOOR, i) = LoadSound_Strict("SFX\Door\BigDoorOpen" + (i + 1) + ".ogg")
		CloseDoorSFX(BIG_DOOR, i) = LoadSound_Strict("SFX\Door\BigDoorClose" + (i + 1) + ".ogg")
		OpenDoorSFX(OFFICE_DOOR, i) = LoadSound_Strict("SFX\Door\OfficeDoorOpen" + (i + 1) + ".ogg")
		OpenDoorSFX(WOODEN_DOOR, i) = LoadSound_Strict("SFX\Door\WoodenDoorOpen" + (i + 1) + ".ogg")
		BigDoorErrorSFX[i] = LoadSound_Strict("SFX\Door\BigDoorError" + (i + 1) + ".ogg")
	Next
	
	KeyCardSFX1 = LoadSound_Strict("SFX\Interact\KeyCardUse1.ogg")
	KeyCardSFX2 = LoadSound_Strict("SFX\Interact\KeyCardUse2.ogg")
	ScannerSFX1 = LoadSound_Strict("SFX\Interact\ScannerUse1.ogg")
	ScannerSFX2 = LoadSound_Strict("SFX\Interact\ScannerUse2.ogg")
	
	DoorBudgeSFX1 = LoadSound_Strict("SFX\Interact\DoorBudge1.ogg")
	DoorBudgeSFX2 = LoadSound_Strict("SFX\Interact\DoorBudge2.ogg")
	
	DoorLockSFX = LoadSound_Strict("SFX\Interact\DoorLock.ogg")
	
	OpenDoorFastSFX = LoadSound_Strict("SFX\Door\DoorOpenFast.ogg")
	CautionSFX = LoadSound_Strict("SFX\Room\LockroomSiren.ogg")
	
	CameraSFX = LoadSound_Strict("SFX\General\Camera.ogg") 
	
	StoneDragSFX = LoadSound_Strict("SFX\SCP\173\StoneDrag.ogg")
	
	GunshotSFX = LoadSound_Strict("SFX\General\Gunshot.ogg")
	Gunshot2SFX = LoadSound_Strict("SFX\General\Gunshot2.ogg")
	Gunshot3SFX = LoadSound_Strict("SFX\General\BulletMiss.ogg")
	BulletHitSFX = LoadSound_Strict("SFX\General\BulletHit.ogg")
	
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
	
	HissSFX = LoadSound_Strict("SFX\General\Hiss.ogg")
	
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
	
	; ~ 0 = Light Containment Zone
	; ~ 1 = Heavy Containment Zone
	; ~ 2 = Entrance Zone
	; ~ 3 = General
	; ~ 4 = Pre-Breach
	; ~ 5 = SCP-860-1
	AmbientSFXAmount[0] = 8 
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
	
	For i = 0 To 3
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
		EndIf
	Next
	For i = 0 To 2
		Step2SFX[i] = LoadSound_Strict("SFX\Step\StepForest" + (i + 1) + ".ogg")
	Next
	For i = 3 To 5
		Step2SFX[i] = LoadSound_Strict("SFX\Character\MTF\Step" + (i - 2) + ".ogg")
	Next
	For i = 6 To 12
		Step2SFX[i] = LoadSound_Strict("SFX\Step\SCP\StepMetal" + (i - 5) + ".ogg")
	Next
	For i = 13 To 14
		Step2SFX[i] = LoadSound_Strict("SFX\Step\StepFluid" + (i - 12) + ".ogg")
	Next
	
	VehicleSFX[0] = LoadSound_Strict("SFX\Character\Vehicle\Idle.ogg")
	VehicleSFX[1] = LoadSound_Strict("SFX\Character\Vehicle\Move.ogg")
	
	MissSFX = LoadSound_Strict("SFX\General\Miss.ogg")
	
	BreathGasRelaxedSFX = LoadSound_Strict("SFX\Character\D9341\BreathGasRelaxed.ogg")
	
	CrouchSFX = LoadSound_Strict("SFX\Character\D9341\Crouch.ogg")
	
	SCRAMBLESFX = LoadSound_Strict("SFX\General\SCRAMBLE.ogg")
	
	NVGSFX[0] = LoadSound_Strict("SFX\General\NVGOn.ogg")
	NVGSFX[1] = LoadSound_Strict("SFX\General\NVGOff.ogg")
	
	For i = 0 To 1
		LowBatterySFX[i] = LoadSound_Strict("SFX\General\LowBattery" + (i + 1) + ".ogg")
	Next
	
	For i = 0 To 1
		KnobSFX[i] = LoadSound_Strict("SFX\Room\914Chamber\Knob" + (i + 1) + ".ogg")
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D