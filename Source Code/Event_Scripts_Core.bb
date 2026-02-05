Function UpdateEvent_Room1_Dead_End_LCZ_106%(e.Events)
	Local de.Decals
	
	If (Not n_I\Curr106\Contained)
		If e\EventState = 0.0
			If e\room\Dist > 0.0 And e\room\Dist < 8.0
				If n_I\Curr106\State > 1.0
					RemoveEvent(e)
					Return
				Else
					e\room\NPC[0] = CreateNPC(NPCTypeD, EntityX(e\room\RoomDoors[0]\FrameOBJ, True), 0.5, EntityZ(e\room\RoomDoors[0]\FrameOBJ, True))
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_JANITOR_TEXTURE)
					PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0, True)
					MoveEntity(e\room\NPC[0]\Collider, 0.0, 0.0, 0.5) 
					
					PlaySoundEx(LoadTempSound("SFX\Door\EndroomDoor.ogg"), Camera, e\room\OBJ, 15.0)
					
					e\EventState = 1.0
				EndIf
			EndIf
		ElseIf e\EventState = 1.0
			If PlayerRoom = e\room And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
				StopChannel(e\SoundCHN) : e\SoundCHN = 0
				
				e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Character\Janitor\106Abduct.ogg"), True)
				
				e\EventState = 2.0
			ElseIf e\room\Dist < 8.0
				If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Character\Janitor\Idle.ogg")
				e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\OBJ, 15.0, 1.0, True)
			EndIf
		ElseIf e\EventState = 2.0
			e\EventState2 = e\EventState2 + fps\Factor[0]
			If e\EventState2 > 85.0 Then e\room\NPC[0]\State = 1.0
			If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\RoomDoors[0]\FrameOBJ) > 3.49
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, e\room\x, e\room\y + 0.005, e\room\z, 90.0, Rnd(360.0), 0.0, 0.05)
				de\SizeChange = 0.008 : de\Timer = 10000.0
				
				e\EventState = 3.0
			EndIf
		Else
			PositionEntity(n_I\Curr106\OBJ, EntityX(e\room\OBJ, True), 0.0, EntityZ(e\room\OBJ, True))
			PointEntity(n_I\Curr106\OBJ, e\room\NPC[0]\Collider)
			RotateEntity(n_I\Curr106\OBJ, 0.0, EntityYaw(n_I\Curr106\OBJ), 0.0, True)
			
			n_I\Curr106\Idle = 1
			
			If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\RoomDoors[0]\FrameOBJ) > 7.74
				If e\room\NPC[0]\State = 1.0
					e\room\NPC[0]\State = -1.0
					SetNPCFrame(e\room\NPC[0], 41.0)
				EndIf
				e\room\NPC[0]\CurrSpeed = CurveValue(0.0, e\room\NPC[0]\CurrSpeed, 25.0)
				PositionEntity(e\room\NPC[0]\Collider, CurveValue(EntityX(e\room\OBJ, True), EntityX(e\room\NPC[0]\Collider), 25.0), 0.3 - e\EventState / 70.0, CurveValue(EntityZ(e\room\OBJ, True), EntityZ(e\room\NPC[0]\Collider), 25.0))
				ResetEntity(e\room\NPC[0]\Collider)
				
				AnimateNPC(e\room\NPC[0], 41.0, 58.0, 0.1, False)
				
				e\EventState = e\EventState + (fps\Factor[0] / 2.0)
			EndIf
			AnimateNPC(n_I\Curr106, 495.0, 604.0, 0.7, False)
			
			me\CurrSpeed = Min(me\CurrSpeed - (me\CurrSpeed * (0.15 / EntityDistance(e\room\NPC[0]\Collider, me\Collider)) * fps\Factor[0]), me\CurrSpeed)
			If e\EventState > 100.0
				n_I\Curr106\State = 0.0
				n_I\Curr106\State2 = Rnd(22000.0, 27000.0)
				n_I\Curr106\Idle = 0
				If EntityDistanceSquared(me\Collider, e\room\OBJ) < 6.25 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					n_I\Curr106\EnemyX = EntityX(me\Collider) : n_I\Curr106\EnemyY = EntityY(me\Collider) : n_I\Curr106\EnemyZ = EntityZ(me\Collider)
					n_I\Curr106\State = 2.0
				EndIf
				
				RemoveNPC(e\room\NPC[0])
				
				RemoveEvent(e)
				Return
			EndIf
		EndIf
	Else
		RemoveNPC(e\room\NPC[0])
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room1_Storage%(e.Events)
	If PlayerRoom = e\room
		Local Temp#
		
		; ~ Clock disappearance timer
		e\EventState = e\EventState + fps\Factor[0]
		If e\EventState < 70.0
			Temp = 1.0
		ElseIf e\EventState < 140.0
			Temp = 0.0
		Else
			e\EventState = 0.0
			Temp = 1.0
		EndIf
		e\EventState3 = CurveValue(Temp, e\EventState3, 10.0)
		EntityAlpha(e\room\Objects[0], e\EventState3) 
		; ~ Interact with Ordinary Duck
		If InteractObject(e\room\Objects[1], 0.8)
			If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4
				CreateMsg(GetLocalString("msg", "duck"))
				PlaySound_Strict(LoadTempSound("SFX\SCP\Joke\Quack.ogg"))
			Else
				CreateMsg(GetLocalString("msg", "flamingo"))
			EndIf
		EndIf
		
		; ~ Interact with Penny
		If e\EventState2 = 0.0
			If InteractObject(e\room\Objects[2], 0.8)
				CreateMsg(GetLocalString("msg", "coinflip_1"))
				PlaySound_Strict(LoadTempSound("SFX\Interact\PennyFlip" + Rand(0, 1) + ".ogg"))
				e\EventState2 = Rand(1, 2)
			EndIf
		Else
			Local Scale# = (e\EventState2 - 1.0) * 40.0
			
			AnimateEx(e\room\Objects[2], AnimTime(e\room\Objects[2]), 1.0 + Scale, 40.0 + Scale, 0.8, False)
			If AnimTime(e\room\Objects[2]) >= 40.0 + Scale
				SetAnimTime(e\room\Objects[2], 1.0)
				CreateMsg(GetLocalString("msg", "coinflip_2"))
				e\EventState2 = 0.0
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_005%(e.Events)
	Local de.Decals, n.NPCs
	
	If I_005\ChanceToSpawn = 1
		If PlayerRoom = e\room
			If (Not n_I\Curr106\Contained)
				If e\EventState = 0.0
					If e\room\Objects[0] = 0
						TFormPoint(0.0, 0.0, 238.0, e\room\OBJ, 0)
						e\room\Objects[0] = CreatePivot()
						PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ())
						EntityParent(e\room\Objects[0], e\room\OBJ)
					Else
						If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
							If DistanceSquared(EntityX(me\Collider, True), EntityX(e\room\Objects[0], True), EntityZ(me\Collider, True), EntityZ(e\room\Objects[0], True)) < 1.69
								PlaySound_Strict(snd_I\HorrorSFX[10])
								
								TFormPoint(0.0, 188.0, 459.0, e\room\OBJ, 0)
								de.Decals = CreateDecal(DECAL_CORROSIVE_1, TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle, Rnd(360.0), 0.1, 0.01)
								de\SizeChange = 0.003 : de\AlphaChange = 0.005 : de\Timer = 90000.0
								
								de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\RoomDoors[0]\FrameOBJ, True), EntityY(e\room\RoomDoors[0]\FrameOBJ, True) + 0.005, EntityZ(e\room\RoomDoors[0]\FrameOBJ, True), 90.0, e\room\Angle + 360.0, Rnd(360.0), 0.1, 0.01)
								de\SizeChange = 0.003 : de\AlphaChange = 0.005 : de\Timer = 90000.0
								
								TFormPoint(0.0, 0.0, 585.0, e\room\OBJ, 0)
								PositionEntity(n_I\Curr106\Collider, TFormedX(), 0.0, TFormedZ(), True)
								ResetEntity(n_I\Curr106\Collider)
								n_I\Curr106\State = 3.0
								n_I\Curr106\State2 = Rnd(3000.0, 3500.0)
								
								FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
								e\EventState = 1.0
							EndIf
						EndIf
					EndIf
				Else
					Local Dist# = DistanceSquared(EntityX(me\Collider), EntityX(e\room\RoomDoors[0]\FrameOBJ), EntityZ(me\Collider), EntityZ(e\room\RoomDoors[0]\FrameOBJ))
					
					If Dist < 0.16 And e\room\RoomDoors[0]\OpenState > 0.0 And (Not chs\NoTarget)
						If e\EventState2 = 0.0 Then PlaySound_Strict(LoadTempSound("SFX\Room\SinkholeFall.ogg"))
						
						MakeMeUnplayable()
						PositionEntity(me\Collider, CurveValue(EntityX(e\room\RoomDoors[0]\FrameOBJ), EntityX(me\Collider), 10.0), CurveValue(EntityY(e\room\RoomDoors[0]\FrameOBJ) - e\EventState2, EntityY(me\Collider), 25.0), CurveValue(EntityZ(e\room\RoomDoors[0]\FrameOBJ), EntityZ(me\Collider), 10.0), True)
						
						me\DropSpeed = 0.0
						
						ResetEntity(me\Collider)
						
						e\EventState2 = Min(e\EventState2 + fps\Factor[0] / 200.0, 2.0)
						
						me\LightBlink = Min(e\EventState2 * 5.0, 10.0)
						If e\EventState2 >= 0.2
							me\BlinkTimer = -10.0
							If n_I\Curr106\State = 3.0 Then n_I\Curr106\CurrSpeed = 0.0
						EndIf
						me\BlurTimer = e\EventState2 * 500.0
						
						If e\EventState2 = 2.0 Then MoveToPocketDimension()
					Else
						If chs\NoClip Then me\Playable = 2
					EndIf
				EndIf
			Else
				RemoveEvent(e)
			EndIf
		Else
			e\EventState2 = 0.0
		EndIf
	Else
		If e\room\Dist < 7.0
			e\room\RoomTemplate\DisableDecals = 1
			If I_005\ChanceToSpawn = 3
				TFormPoint(375.0, 71.0, -875.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
				n\State = 8.0 : n\IsDead = True
				SetNPCFrame(n, 287.0)
				RotateEntity(n\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
				
				TFormPoint(382.0, 150.0, -875.0, e\room\OBJ, 0)
				de.Decals = CreateDecal(DECAL_BLOOD_2, TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle + 270.0, 0.0, 0.3)
			Else
				TFormPoint(-296.0, 71.0, -240.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
				n\State = 8.0 : n\IsDead = True
				SetNPCFrame(n, 288.0)
				RotateEntity(n\Collider, 0.0, e\room\Angle + 125.0, 0.0, True)
			EndIf
			RemoveEvent(e)
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_173%(e.Events)
	If e\EventState = 0.0
		If PlayerRoom = e\room
			e\room\RoomDoors[1]\Open = True
			
			If SelectedDifficulty\SaveType = DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
				CreateHintMsg(Format(GetLocalString("save", "save"), key\Name[key\SAVE]), 6.0, True)
			ElseIf SelectedDifficulty\SaveType = DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS
				CreateHintMsg(GetLocalString("save", "failed.screen"), 6.0, True)
			EndIf
			
			n_I\Curr173\Idle = 1
			
			e\room\RoomDoors[0]\Open = True
			If e\room\NPC[0] <> Null
				e\room\NPC[0]\State = 8.0
				SetNPCFrame(e\room\NPC[0], 74.0)
			EndIf
			
			If e\room\NPC[1] <> Null
				PositionEntity(e\room\NPC[1]\Collider, e\room\x, e\room\y + 0.3, e\room\z - 1.0 + 2048.0 * RoomScale, True)
				ResetEntity(e\room\NPC[1]\Collider)
			Else
				e\room\NPC[1] = CreateNPC(NPCTypeD, e\room\x, e\room\y + 0.3, e\room\z - 1.0 + 2048.0 * RoomScale)
				ChangeNPCTextureID(e\room\NPC[1], NPC_CLASS_D_FRANKLIN_TEXTURE)
			EndIf
			
			If e\room\NPC[7] <> Null
				PositionEntity(e\room\NPC[7]\Collider, e\room\x + 32.0 * RoomScale, e\room\y + 0.2, e\room\z + 3116.0 * RoomScale)
				ResetEntity(e\room\NPC[7]\Collider)
			Else
				e\room\NPC[7] = CreateNPC(NPCTypeD, e\room\x + 32.0 * RoomScale, e\room\y + 0.2, e\room\z + 3116.0 * RoomScale)
				ChangeNPCTextureID(e\room\NPC[7], NPC_CLASS_D_SCIENTIST_TEXTURE)
			EndIf
			RotateEntity(e\room\NPC[7]\Collider, 0.0, e\room\Angle + 180.0, 0.0)
			
			SetNPCFrame(e\room\NPC[1], 210.0)
			; ~ Preload this sound cause of huge file size
			e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\Intro\WhatThe0a.ogg")
			
			If e\room\NPC[2] <> Null
				PositionEntity(e\room\NPC[2]\Collider, e\room\x, e\room\y + 0.3, e\room\z + 2576.0 * RoomScale, True)
				ResetEntity(e\room\NPC[2]\Collider)
			Else
				e\room\NPC[2] = CreateNPC(NPCTypeGuard, e\room\x, e\room\y + 0.3, e\room\z + 2576.0 * RoomScale)
			EndIf
			e\room\NPC[2]\State = 7.0
			; ~ Preload this sound cause of huge file size
			e\room\NPC[2]\Sound = LoadSound_Strict("SFX\Room\Intro\WhatThe0b.ogg")
			
			PointEntity(e\room\NPC[2]\Collider, e\room\NPC[1]\Collider)
			PointEntity(e\room\NPC[1]\Collider, e\room\NPC[2]\Collider)
			
			If e\room\NPC[0] = Null
				TFormPoint(3512.0, 842.0, 4682.0, e\room\OBJ, 0)
				e\room\NPC[3] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[3]\State = 8.0 : e\room\NPC[3]\IsDead = True
				SetNPCFrame(e\room\NPC[3], 286.0)
				RotateEntity(e\room\NPC[3]\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
				
				TFormPoint(4712.0, 435.8, 4026.0, e\room\OBJ, 0)
				e\room\NPC[4] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[4]\State3 = -1.0 : e\room\NPC[4]\IsDead = True
				SetNPCFrame(e\room\NPC[4], 677.0)
				RotateEntity(e\room\NPC[4]\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
				
				TFormPoint(4340.0, 435.8, 3680.0, e\room\OBJ, 0)
				e\room\NPC[5] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[5]\State3 = -1.0 : e\room\NPC[5]\IsDead = True
				ChangeNPCTextureID(e\room\NPC[5], NPC_CLASS_D_CLASS_D_TEXTURE)
				SetNPCFrame(e\room\NPC[5], 779.0)
				RotateEntity(e\room\NPC[5]\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
				
				PlaySound_Strict(LoadTempSound("SFX\Room\Intro\173Vent.ogg"))
				me\BlinkTimer = -10.0
				me\LightBlink = 1.0
				me\BigCameraShake = 3.0
				
				PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
			EndIf
			me\CurrSpeed = 0.0 : me\Playable = 2
			e\EventState = 1.0
		EndIf
	Else
		Local i%
		
		If EntityY(e\room\Objects[2], True) > e\room\y + 384.0 * RoomScale
			e\EventState4 = CurveValue(e\EventState4 + fps\Factor[0] * 2.0, e\EventState4, 40.0)
			PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[2], True), Max(e\room\y + 384.0 * RoomScale, (e\room\y + 1152.0 * RoomScale) - e\EventState4), EntityZ(e\room\Objects[2], True), True)
			RotateEntity(e\room\Objects[2], EntityPitch(e\room\Objects[2], True), CurveValue(30.0, EntityYaw(e\room\Objects[2], True), 200.0), EntityRoll(e\room\Objects[2], True), True)
			
			If EntityDistanceSquared(e\room\Objects[2], me\Collider) < 0.36
				If EntityY(e\room\Objects[2], True) < e\room\y + 640.0 * RoomScale
					If (Not chs\GodMode) And (Not me\Terminated)
						PlaySound_Strict(LoadTempSound("SFX\Character\BodyFall.ogg"))
						msg\DeathMsg = Format(GetLocalString("death", "vent"), SubjectName)
						Kill(True, False)
					EndIf
				EndIf
			EndIf
		EndIf
		
		If e\room\NPC[0] <> Null Then AnimateNPC(e\room\NPC[0], 249.0, 286.0, 0.4, False)
		
		If EntityX(me\Collider) < (e\room\x + 1384.0 * RoomScale) Then e\EventState = Max(e\EventState, 900.0)
		If e\EventState >= 500.0
			e\EventState = e\EventState + fps\Factor[0]
		ElseIf EntityDistanceSquared(me\Collider, e\room\RoomDoors[2]\FrameOBJ) < 5.0
			e\EventState = 500.0
		EndIf
		If e\EventState >= 500.0
			If e\EventState2 = 0.0
				If e\EventState > 900.0 And e\room\RoomDoors[3]\Open
					If e\EventState - fps\Factor[0] <= 900.0 
						TeleportEntity(n_I\Curr173\Collider, e\room\x + 32.0 * RoomScale, e\room\y + 0.32, e\room\z + 3184.0 * RoomScale, n_I\Curr173\CollRadius + 0.12, True)
						n_I\Curr173\CurrentRoom = e\room
						RotateEntity(n_I\Curr173\Collider, 0.0, 190.0, 0.0)
						e\room\NPC[7]\State3 = 2.0
						e\room\NPC[7]\IsDead = True
						PlaySoundEx(snd_I\NeckSnapSFX[0], Camera, e\room\NPC[7]\Collider, 8.0)
						
						PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
						me\LightBlink = 3.0
						me\BlinkTimer = -10.0
						PlaySoundEx(snd_I\StoneDragSFX, Camera, n_I\Curr173\Collider)
						
						e\room\NPC[1]\SoundCHN = PlaySoundEx(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider, 10.0, 1.0, True)
						e\room\NPC[1]\State = 1.0
						e\room\NPC[2]\SoundCHN = PlaySoundEx(e\room\NPC[2]\Sound, Camera, e\room\NPC[2]\Collider, 10.0, 1.0, True)
					EndIf
					e\room\NPC[1]\Speed = -0.008
					e\room\NPC[1]\CurrSpeed = CurveValue(e\room\NPC[1]\Speed, e\room\NPC[1]\CurrSpeed, 5.0)
					RotateEntity(e\room\NPC[1]\Collider, 0.0, 0.0, 0.0)
					
					If e\EventState > 900.0 + (70.0 * 2.5)
						If e\room\NPC[2]\State <> 1.0
							e\room\NPC[2]\CurrSpeed = CurveValue(-0.012, e\room\NPC[2]\CurrSpeed, 5.0)
							AnimateNPC(e\room\NPC[2], 39.0, 76.0, e\room\NPC[2]\CurrSpeed * 40.0)
							MoveEntity(e\room\NPC[2]\Collider, 0.0, 0.0, e\room\NPC[2]\CurrSpeed * fps\Factor[0])
							e\room\NPC[2]\State = 8.0
							
							If EntityZ(e\room\NPC[2]\Collider) < e\room\z + 2048.0 * RoomScale
								PointEntity(e\room\NPC[2]\OBJ, e\room\NPC[1]\Collider)
								RotateEntity(e\room\NPC[2]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[2]\OBJ) - 180.0, EntityYaw(e\room\NPC[2]\Collider), 15.0), 0.0)
							Else
								RotateEntity(e\room\NPC[2]\Collider, 0.0, 0.0, 0.0)
							EndIf
						EndIf
					EndIf
					
					If e\EventState < 900.0 + (70.0 * 4.0)
						If e\EventState > 900.0 + 70.0 And e\EventState < 900.0 + (70.0 * 2.5)
							AnimateNPC(e\room\NPC[2], 1539.0, 1553.0, 0.2, False)
							PointEntity(e\room\NPC[2]\OBJ, n_I\Curr173\Collider)
							RotateEntity(e\room\NPC[2]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[2]\OBJ), EntityYaw(e\room\NPC[2]\Collider), 15.0), 0.0)
						EndIf
					Else
						If e\EventState - fps\Factor[0] < 900.0 + (70.0 * 4.0)
							PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
							me\LightBlink = 3.0
							me\BlinkTimer = -10.0
							PlaySoundEx(snd_I\StoneDragSFX, Camera, n_I\Curr173\Collider)
							PointEntity(n_I\Curr173\Collider, e\room\NPC[2]\Collider)
						EndIf
						PositionEntity(n_I\Curr173\Collider, e\room\x - 96.0 * RoomScale, e\room\y + 0.32, e\room\z + 2640.0 * RoomScale, True)
						ResetEntity(n_I\Curr173\Collider)
						RotateEntity(n_I\Curr173\Collider, 0.0, 190.0, 0.0)
						
						n_I\Curr173\Idle = (1 - (me\LightBlink >= 0.25))
						
						If e\room\NPC[2]\State <> 1.0 And (Not me\Terminated)
							If EntityZ(e\room\NPC[2]\Collider) < e\room\z + 898.0 * RoomScale
								e\room\RoomDoors[3]\Open = False
								me\LightBlink = 3.0
								me\BlinkTimer = -10.0
								PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
								n_I\Curr173\Idle = 0
								If EntityDistanceSquared(n_I\Curr173\Collider, me\Collider) < 6.25 And IsEqual(EntityY(me\Collider), EntityY(n_I\Curr173\Collider), 1.0)
									PositionEntity(n_I\Curr173\Collider, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
								Else
									PlaySoundEx(snd_I\StoneDragSFX, Camera, n_I\Curr173\Collider)
									CreateHintMsg(Format(GetLocalString("msg", "run"), key\Name[key\SPRINT]), 6.0, True)
									PositionEntity(n_I\Curr173\Collider, e\room\x, -500.0, e\room\z)
								EndIf
								ResetEntity(n_I\Curr173\Collider)
							EndIf
						EndIf
					EndIf
					
					; ~ If Ulgrin can see the player then start shooting at them
					If (Not (chs\NoTarget Lor I_268\InvisibilityOn)) And EntityDistanceSquared(me\Collider, e\room\NPC[2]\Collider) < 9.0 And EntityVisible(e\room\NPC[2]\Collider, me\Collider)
						e\room\NPC[2]\State = 1.0
						e\room\NPC[2]\State3 = 1.0
					ElseIf e\room\NPC[2]\State = 1.0 And (Not EntityVisible(e\room\NPC[2]\Collider, me\Collider))
						e\room\NPC[2]\State = 0.0
						e\room\NPC[2]\State3 = 0.0
					EndIf
					If e\room\NPC[2]\State = 1.0 Then e\room\RoomDoors[3]\Open = True
				Else
					If e\room\NPC[2]\State <> 1.0
						If e\room\RoomDoors[3]\OpenState = 0.0
							For i = 1 To 2
								RemoveNPC(e\room\NPC[i])
							Next
							e\EventState2 = 1.0
						EndIf
					EndIf
				EndIf
			EndIf
			
			If e\room\Objects[1] <> 0
				If EntityY(e\room\Objects[1], True) > e\room\y - 100.0 * RoomScale
					PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[1], True), -Max(e\EventState - 1800.0, 0.0) / 5000.0, EntityZ(e\room\Objects[1], True), True)
					RotateEntity(e\room\Objects[1], -Max(e\EventState - 2040.0, 0.0) / 135.0, 225.0, 90.0 - Max(e\EventState - 2040.0, 0.0) / 43.0, True)
				Else
					CreateHintMsg(Format(GetLocalString("msg", "crouch"), key\Name[key\CROUCH]), 6.0, True)
					FreeEntity(e\room\Objects[1]) : e\room\Objects[1] = 0
				EndIf
			EndIf
			If e\room\Objects[0] <> 0
				If EntityY(e\room\Objects[0], True) > e\room\y - 100.0 * RoomScale
					PositionEntity(e\room\Objects[0], EntityX(e\room\Objects[0], True), -Max(e\EventState - 1300.0, 0.0) / 4500.0, EntityZ(e\room\Objects[0], True), True)
					RotateEntity(e\room\Objects[0], 90.0 - Max(e\EventState - 1320.0, 0.0) / 130.0, 45.0, -Max(e\EventState - 1300.0, 0.0) / 40.0, True)
					
					If Rand(300) = 2
						If EntityDistanceSquared(e\room\Objects[0], me\Collider) < 6.25 Then PlaySoundEx(snd_I\DecaySFX[Rand(3)], Camera, e\room\Objects[0], 3.0)
					EndIf
				Else
					FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
				EndIf
			EndIf
		EndIf
		
		Local Reachable% = PlayerInReachableRoom(True)
		
		If e\EventState < 2000.0 And Reachable Then e\SoundCHN = LoopSoundEx(snd_I\RoomAmbience[5], e\SoundCHN, Camera, e\room\OBJ, 100.0)
		If e\EventState3 < 11.0
			If (Not ChannelPlaying(e\SoundCHN2))
				e\EventState3 = e\EventState3 + 1.0
				
				If Reachable
					LoadEventSound(e, "SFX\Alarm\Alarm1_" + Int(e\EventState3) + ".ogg", 1)
					e\SoundCHN2 = PlaySound_Strict(e\Sound2, (e\EventState3 = 1.0))
				EndIf
			Else
				If Int(e\EventState3) = 8.0 And Reachable Then me\BigCameraShake = 1.0
			EndIf
		EndIf
		If (e\EventState Mod 600.0 > 300.0) And ((e\EventState + fps\Factor[0]) Mod 600.0 < 300.0)
			i = Floor((e\EventState - 5000.0) / 600.0) + 1.0
			
			Select i
				Case 0
					;[Block]
					If Reachable Then PlaySound_Strict(LoadTempSound("SFX\Room\Intro\IA\Scripted\Scripted5.ogg"))
					CreateHintMsg(Format(GetLocalString("msg", "openinv"), key\Name[key\INVENTORY]), 6.0, True)
					;[End Block]
				Case 1
					;[Block]
					CreateHintMsg(GetLocalString("msg", "item.select"), 6.0, True)
					;[End Block]
				Case 2
					;[Block]
					CreateHintMsg(GetLocalString("msg", "item.combine.swap"), 6.0, True)
					;[End Block]
				Case 3
					;[Block]
					CreateHintMsg(GetLocalString("msg", "item.drop"), 6.0, True)
					;[End Block]
				Case 4
					;[Block]
					CreateHintMsg(GetLocalString("msg", "right.click"), 6.0, True)
					;[End Block]
			End Select
			If i > 0 And i < 26
				If (Not CommotionState[i]) ; ~ Prevents the same commotion file from playing more then once
					If Reachable
						PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Commotion\Commotion" + (i - 1) + ".ogg"))
						CommotionState[i] = True
					EndIf
				EndIf
			EndIf
			If i > 26 Then RemoveEvent(e)
		EndIf
	EndIf
End Function

; ~ Intro Room Constants
;[Block]
Const INTRO_IN_CELL% = 1
Const INTRO_CELL_REQUESTING% = 2
Const INTRO_CELL_OPENED% = 3
Const INTRO_MOVING_TO_CHAMBER% = 4
Const INTRO_GIVE_PAPER% = 5
Const INTRO_ESCORT_DONE% = 6
Const INTRO_IN_CHAMBER% = 7
;[End Block]

Function UpdateEvent_Cont1_173_Intro%(e.Events)
	; ~ e\EventState = Main state
	
	; ~ e\EventState2 = Timer before chamber location
	
	; ~ e\EventState3 = Timer in chamber location
	
	If PlayerRoom = e\room
		Local r.Rooms, p.Props, l.Lights, w.WayPoints, sc.SecurityCams, d.Doors, se.SoundEmitters, s.Screens
		Local i%, Temp%, StrTemp$, Pvt%, x#, y#, z#, Tex%, Dist#, Dist2#
		Local FPSFactorEx#
		
		LightRenderDistance = 64.0
		If e\EventState = 0.0
			For i = 0 To 1
				snd_I\IntroSFX[i] = LoadSound_Strict("SFX\Room\Intro\Ew" + i + ".ogg")
			Next
			snd_I\IntroSFX[2] = LoadSound_Strict("SFX\Room\Intro\Horror.ogg")
			snd_I\IntroSFX[3] = LoadSound_Strict("SFX\Room\Intro\See173.ogg")
			For i = 0 To 2
				snd_I\IntroSFX[i + 4] = LoadSound_Strict("SFX\Room\Intro\Bang" + i + ".ogg")
			Next
			
			n_I\Curr173\Angle = 90.0 : n_I\Curr173\Idle = 1
			TeleportEntity(n_I\Curr173\Collider, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True), n_I\Curr173\CollRadius + 0.12, True)
			n_I\Curr173\CurrentRoom = e\room
			n_I\Curr173\Angle = 90.0 : n_I\Curr173\Idle = 1
			RotateEntity(n_I\Curr173\Collider, 0.0, 0.0, 0.0, True)
			HideEntity(n_I\Curr173\OBJ)
			HideEntity(n_I\Curr173\OBJ2)
			
			TFormPoint(328.0, 480.0, 1072.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\Angle = 180.0
			HideEntity(e\room\NPC[0]\OBJ)
			
			TFormPoint(208.0, 200.0, 480.0, e\room\OBJ, 0)
			y = TFormedY()
			e\room\NPC[1] = CreateNPC(NPCTypeD, TFormedX(), y, TFormedZ())
			e\room\NPC[1]\State3 = 3.0
			PointEntity(e\room\NPC[1]\Collider, e\room\Objects[2])
			HideEntity(e\room\NPC[1]\OBJ)
			
			TFormPoint(160.0, 0.0, 320.0, e\room\OBJ, 0)
			e\room\NPC[2] = CreateNPC(NPCTypeD, TFormedX(), y, TFormedZ())
			e\room\NPC[2]\State3 = 5.0
			PointEntity(e\room\NPC[2]\Collider, e\room\Objects[2])
			ChangeNPCTextureID(e\room\NPC[2], NPC_CLASS_D_CLASS_D_TEXTURE)
			HideEntity(e\room\NPC[2]\OBJ)
			
			TFormPoint(-4205.0, 0.0, 870.0, e\room\OBJ, 0)
			e\room\NPC[3] = CreateNPC(NPCTypeGuard, TFormedX(), y, TFormedZ())
			e\room\NPC[3]\State = 7.0
			; ~ Preload this sound cause of huge file size
			Temp = Rand(0, 4)
			e\room\NPC[3]\Sound2 = LoadSound_Strict("SFX\Room\Intro\Guard\Conversation" + Temp + "a.ogg")
			RotateEntity(e\room\NPC[3]\Collider, 0.0, e\room\Angle + 180.0, 0.0)
			HideEntity(e\room\NPC[3]\OBJ)
			
			TFormPoint(-3985.0, 0.0, 786.0, e\room\OBJ, 0)
			e\room\NPC[4] = CreateNPC(NPCTypeGuard, TFormedX(), y, TFormedZ())
			e\room\NPC[4]\State = 7.0
			; ~ Preload this sound cause of huge file size
			e\room\NPC[4]\Sound2 = LoadSound_Strict("SFX\Room\Intro\Guard\Conversation" + Temp + "b.ogg")
			RotateEntity(e\room\NPC[4]\Collider, 0.0, e\room\Angle + 135.0, 0.0)
			HideEntity(e\room\NPC[4]\OBJ)
			
			TFormPoint(-8064.0, 0.0, 1096.0, e\room\OBJ, 0)
			e\room\NPC[5] = CreateNPC(NPCTypeGuard, TFormedX(), y, TFormedZ())
			e\room\NPC[5]\State = 7.0
			e\room\NPC[5]\Sound2 = LoadSound_Strict("SFX\Room\Intro\Guard\Music" + Rand(0, 4) + ".ogg")
			RotateEntity(e\room\NPC[5]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
			HideEntity(e\room\NPC[5]\OBJ)
			
			TFormPoint(-3424.0, -100.0, -2208.0, e\room\OBJ, 0)
			e\room\NPC[6] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			ChangeNPCTextureID(e\room\NPC[6], NPC_CLASS_D_FRANKLIN_TEXTURE)
			HideEntity(e\room\NPC[6]\OBJ)
			
			TFormPoint(-3073.0, -315.0, -2165.0, e\room\OBJ, 0)
			e\room\NPC[7] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[7]\State = 3.0
			; ~ Preload this sound cause of huge file size
			e\room\NPC[7]\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Conversation.ogg")
			ChangeNPCTextureID(e\room\NPC[7], NPC_CLASS_D_SECURITY_TEXTURE)
			HideEntity(e\room\NPC[7]\OBJ)
			
			TFormPoint(-3800.0, 250.0, -4088.0, e\room\OBJ, 0)
			y = TFormedY() : z = TFormedZ()
			e\room\NPC[8] = CreateNPC(NPCTypeGuard, TFormedX(), y, z)
			e\room\NPC[8]\State = 7.0
			HideEntity(e\room\NPC[8]\OBJ)
			
			TFormPoint(-4200.0, 0.0, 0.0, e\room\OBJ, 0)
			e\room\NPC[9] = CreateNPC(NPCTypeGuard, TFormedX(), y, z)
			e\room\NPC[9]\State = 7.0
			HideEntity(e\room\NPC[9]\OBJ)
			
			TFormPoint(-4000.0, 0.0, 0.0, e\room\OBJ, 0)
			e\room\NPC[10] = CreateNPC(NPCTypeD, TFormedX(), y, z)
			e\room\NPC[10]\State2 = 1.0
			ChangeNPCTextureID(e\room\NPC[10], NPC_CLASS_D_D9341_TEXTURE)
			HideEntity(e\room\NPC[10]\OBJ)
			
			TFormPoint(-7208.0, -600.0, -3104.0, e\room\OBJ, 0)
			e\room\NPC[11] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[11]\State = 15.0
			CreateNPCAsset(e\room\NPC[11])
			HideEntity(e\room\NPC[11]\OBJ)
			HideEntity(e\room\NPC[11]\OBJ2)
			
			TFormPoint(-5675.0, -1020.0, -3717.0, e\room\OBJ, 0)
			e\room\NPC[12] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[12]\State = -1.0
			RotateEntity(e\room\NPC[12]\Collider, 0.0, 154.0, 0.0)
			SetNPCFrame(e\room\NPC[12], 357.0)
			ChangeNPCTextureID(e\room\NPC[12], NPC_CLASS_D_JANITOR_TEXTURE)
			HideEntity(e\room\NPC[12]\OBJ)
			
			For i = 8 To 11
				If i < 11
					RotateEntity(e\room\NPC[i]\Collider, 0.0, 90.0, 0.0)
				Else
					RotateEntity(e\room\NPC[i]\Collider, 0.0, -90.0, 0.0)
				EndIf
			Next
			
			TFormPoint(-894.0, 500.0, 130.0, e\room\OBJ, 0)
			e\room\NPC[13] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\NPC[13]\Collider, 0.0, e\room\Angle + 290.0, 0.0)
			ChangeNPCTextureID(e\room\NPC[13], NPC_CLASS_D_SCIENTIST_TEXTURE)
			HideEntity(e\room\NPC[13]\OBJ)
			
			TFormPoint(-3180.0, -315.0, -687.0, e\room\OBJ, 0)
			e\room\NPC[14] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\NPC[14]\Collider, 0.0, e\room\Angle + 270.0, 0.0)
			e\room\NPC[14]\State = 3.0
			Tex = LoadTexture_Strict("GFX\NPCs\scientist(2).png")
			EntityTexture(e\room\NPC[14]\OBJ, Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			HideEntity(e\room\NPC[14]\OBJ)
			
			; ~ TODO: FIX ANIMATION!
;			TFormPoint(-7875.0, 56.0, -1775.0, e\room\OBJ, 0)
;			e\room\NPC[15] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
;			RotateEntity(e\room\NPC[15]\Collider, 0.0, e\room\Angle, 0.0)
;			e\room\NPC[15]\State = 3.0
;			Tex = LoadTexture_Strict("GFX\NPCs\security(3).png")
;			EntityTexture(e\room\NPC[15]\OBJ, Tex)
;			DeleteSingleTextureEntryFromCache(Tex)
;			HideEntity(e\room\NPC[15]\OBJ)
			
			HideEntity(e\room\RoomDoors[6]\OBJ)
			HideEntity(e\room\RoomDoors[6]\OBJ2)
			HideEntity(e\room\RoomDoors[6]\FrameOBJ)
			
			x = e\room\x - 4248.0 * RoomScale
			y = e\room\y + 136.0 * RoomScale
			z = e\room\z + 8.0 * RoomScale
			
			PositionEntity(Camera, x, y, z)
			HideEntity(me\Collider)
			PositionEntity(me\Collider, x, e\room\y + 0.302, z)
			RotateEntity(Camera, -70.0, 0.0, 0.0)
			
			PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
			me\BlurTimer = 1600.0
			me\LightFlash = 1.0
			MakeMeUnplayable(False)
			
			CreateConsoleMsg("")
			CreateConsoleMsg(GetLocalString("misc", "warning2"), 255, 0, 0)
			CreateConsoleMsg("")
			
			e\EventState = INTRO_IN_CELL
		Else
			For r.Rooms = Each Rooms
				HideRoomsNoColl(r)
			Next
			ShowRoomsNoColl(e\room)
			ShowRoomsColl(e\room)
			
			CanSave = 1
			
			If e\EventState < INTRO_IN_CHAMBER
				ShouldPlay = 13
			Else
				ShouldPlay = 0
			EndIf
			Select e\EventState
				Case INTRO_IN_CELL
					;[Block]
					LightRenderDistance = 25.0
					
					FPSFactorEx = fps\Factor[0] / 30.0
					
					If e\EventState2 < 3.0
						FPSFactorEx = FPSFactorEx / 1.8
						e\EventState2 = e\EventState2 + FPSFactorEx
					ElseIf e\EventState2 > 9.0 And e\EventState2 < 11.0
						FPSFactorEx = FPSFactorEx / 1.8
						e\EventState2 = e\EventState2 + FPSFactorEx
					ElseIf e\EventState2 < 15.0 Lor e\EventState2 >= 50.0
						e\EventState2 = Min(e\EventState2 + FPSFactorEx, 150.0)
					EndIf
					
					If e\EventState2 < 15.0
						x = e\room\x - 4248.0 * RoomScale
						y = e\room\y + 136.0 * RoomScale
						z = e\room\z - 60.0 * RoomScale
						
						If e\EventState2 < 14.0
							StopMouseMovement()
							
							If e\EventState2 - FPSFactorEx < 1.0 And e\EventState2 >= 1.0
								me\BlinkTimer = -10.0
							ElseIf e\EventState2 - FPSFactorEx < 1.3 And e\EventState2 >= 1.3
								me\BlinkTimer = -10.0
							ElseIf e\EventState2 - FPSFactorEx < 2.8 And e\EventState2 >= 2.8
								PlaySound_Strict(snd_I\IntroSFX[0], True)
							ElseIf e\EventState2 - FPSFactorEx < 10.8 And e\EventState2 >= 10.8
								PlaySound_Strict(snd_I\IntroSFX[1], True)
							ElseIf e\EventState2 - FPSFactorEx < 13.0 And e\EventState2 >= 13.0
								PlaySoundEx(StepSFX(0, 0, 0), Camera, me\Collider, 8.0, 0.5)
							EndIf
							
							Dist = Clamp(e\EventState2 / 5.0, 0.0, 1.0)
							Dist = PowTwo(Dist) * (3.0 - 2.0 * Dist)
							
							Dist2 = Max((e\EventState2 - 10.0) / 4.0, 0.0)
							Dist2 = PowTwo(Dist2) * (3.0 - 2.0 * Dist2)
							
							x = x + (e\room\x - 4130.0 * RoomScale - x) * Dist2
							If e\EventState2 < 10.0
								y = y + (0.2 * Dist)
							Else
								y = (y + 0.2) + (e\room\y + 0.302 + 0.6 - (y + 0.2)) * Dist2
							EndIf
							z = z + (e\room\z + (72.0 * RoomScale) - z) * Dist
							
							RotateEntity(Camera, (-70.0) + 70.0 * Dist + Sin(e\EventState2 * 12.857) * 5.0, (-90.0) * Dist2, Sin(e\EventState2 * 25.7) * 8.0)
							PositionEntity(Camera, x, y, z)
							If (Not EntityHidden(me\Collider)) Then HideEntity(me\Collider)
							PositionEntity(me\Collider, x, e\room\y + 0.302, z)
							me\DropSpeed = 0.0
						Else
							PositionEntity(me\Collider, EntityX(me\Collider), e\room\y + 0.302, EntityZ(me\Collider))
							ResetEntity(me\Collider)
							ShowEntity(me\Collider)
							me\DropSpeed = 0.0
							me\Playable = 2
							
							For i = 0 To 1
								FreeSound_Strict(snd_I\IntroSFX[i]) : snd_I\IntroSFX[i] = 0
							Next
							e\EventState2 = 15.0
						EndIf
						CameraPitch = 0.0
						RotateEntity(me\Collider, 0.0, EntityYaw(Camera), 0.0)
					ElseIf e\EventState2 < 40.0
						If Inventory(0) <> Null
							SelectedItem = Inventory(0)
							e\EventState2 = 40.0
						Else
							CreateHintMsg(GetLocalString("msg", "paper"))
						EndIf
					ElseIf e\EventState2 < 50.0
						CreateHintMsg(GetLocalString("msg", "doc.read"), 8.0)
						e\EventState2 = 50.0
					Else
						If SelectedItem = Null Then e\EventState2 = e\EventState2 + (fps\Factor[0] / 3.0)
						If e\EventState2 >= 150.0
							e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\BeforeDoorOpen.ogg")
							e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
							
							If SelectedItem <> Null Then CreateHintMsg(GetLocalString("msg", "item.deselect"), 8.0)
							
							e\EventState = INTRO_CELL_REQUESTING
						EndIf
					EndIf
					;[End Block]
				Case INTRO_CELL_REQUESTING
					;[Block]
					LightRenderDistance = 25.0
					If (Not ChannelPlaying(e\room\NPC[3]\SoundCHN))
						LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\ExitCell.ogg")
						e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
						
						For i = 3 To 5
							e\room\NPC[i]\State = 9.0
							ShowEntity(e\room\NPC[i]\OBJ)
						Next
						
						OpenCloseDoor(e\room\RoomDoors[4])
						
						e\EventState = INTRO_CELL_OPENED
					EndIf
					;[End Block]
				Case INTRO_CELL_OPENED
					;[Block]
					; ~ Outside the cell
					If DistanceSquared(EntityX(me\Collider), e\room\x - 4096.0 * RoomScale, EntityZ(me\Collider), e\room\z + 192.0 * RoomScale) > 2.4025
						If e\room\RoomDoors[4]\Open
							StopChannel(e\room\NPC[3]\SoundCHN) : e\room\NPC[3]\SoundCHN = 0
							
							LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\Escort" + Rand(0, 1) + ".ogg")
							e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
							
							OpenCloseDoor(e\room\RoomDoors[4])
						EndIf
						If (Not ChannelPlaying(e\room\NPC[3]\SoundCHN))
							FreeEntity(e\room\Objects[3]) : e\room\Objects[3] = 0
							For i = 6 To 12
								ShowEntity(e\room\NPC[i]\OBJ)
							Next
							ShowEntity(e\room\NPC[14]\OBJ)
;							ShowEntity(e\room\NPC[15]\OBJ)
							ShowEntity(e\room\NPC[11]\OBJ2)
							ShowEntity(e\room\Objects[4])
							
							e\EventState = INTRO_MOVING_TO_CHAMBER
						EndIf
					Else ; ~ Inside the cell
						LightRenderDistance = 25.0
						
						FPSFactorEx = fps\Factor[0] / 4.0
						e\EventState2 = Min(e\EventState2 + FPSFactorEx, 630.0)
						
						If e\EventState2 - FPSFactorEx < 300.0 And e\EventState2 >= 300.0
							LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\ExitCellRefuse" + Rand(0, 1) + ".ogg")
							e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
						ElseIf e\EventState2 - FPSFactorEx < 500.0 And e\EventState2 >= 500.0 
							LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\CellGas" + Rand(0, 1) + ".ogg")
							e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
						ElseIf e\EventState2 >= 630.0
							PositionEntity(me\Collider, EntityX(me\Collider), EntityY(me\Collider), Min(EntityZ(me\Collider), e\room\z + 490.0 * RoomScale))
							If e\room\RoomDoors[4]\Open Then OpenCloseDoor(e\room\RoomDoors[4])
							If e\room\RoomEmitters[0] = Null	
								e\room\RoomEmitters.Emitter[0] = SetEmitter(e\room, e\room\x - 4191.0 * RoomScale, e\room\y + 373.0 * RoomScale, e\room\z + 159.0 * RoomScale, 5)
								e\room\RoomEmitters[0]\State = 1
							EndIf
							If e\room\RoomEmitters[1] = Null
								e\room\RoomEmitters.Emitter[1] = SetEmitter(e\room, e\room\x - 4000.0 * RoomScale, e\room\y + 373.0 * RoomScale, e\room\z + 159.0 * RoomScale, 5)
								e\room\RoomEmitters[1]\State = 1
							EndIf
							me\EyeIrritation = Max(me\EyeIrritation + (fps\Factor[0] * 4.0), 1.0)
							If Rand(1000) = 1 Then Kill()
						EndIf
					EndIf
					;[End Block]
				Case INTRO_MOVING_TO_CHAMBER
					;[Block]
					; ~ Slow the player down to match his speed to the guards
					me\CurrSpeed = Min(me\CurrSpeed - (me\CurrSpeed * (0.008 / EntityDistance(e\room\NPC[3]\Collider, me\Collider)) * fps\Factor[0]), me\CurrSpeed)
					; ~ Speed up the second guard to match his speed to the player
					If e\room\NPC[4]\State = 3.0 Then e\room\NPC[4]\CurrSpeed = Min(e\room\NPC[4]\CurrSpeed + (e\room\NPC[4]\CurrSpeed * (0.006 * EntityDistance(e\room\NPC[4]\Collider, me\Collider)) * fps\Factor[0]), e\room\NPC[4]\Speed)
					
					Dist = DistanceSquared(EntityX(me\Collider), EntityX(e\room\NPC[3]\Collider), EntityZ(me\Collider), EntityZ(e\room\NPC[3]\Collider))
					
					If e\room\NPC[3]\State <> 11.0
						If Dist < 9.0
							e\room\NPC[3]\EnemyX = e\room\x - 1674.0 * RoomScale
							e\room\NPC[3]\EnemyY = e\room\y + 0.3
							e\room\NPC[3]\EnemyZ = e\room\z - 979.0 * RoomScale
							e\room\NPC[3]\State = 3.0
						Else
							If Dist > 30.25
								If e\room\NPC[3]\State2 = 0.0
									For i = 3 To 4
										StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
									Next
									
									LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\EscortRun.ogg")
									e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
									e\room\NPC[3]\State2 = 1.0
								EndIf
								
								e\room\NPC[3]\EnemyX = EntityX(me\Collider)
								e\room\NPC[3]\EnemyY = EntityY(me\Collider)
								e\room\NPC[3]\EnemyZ = EntityZ(me\Collider)
								e\room\NPC[3]\Angle = 0.0
								e\room\NPC[3]\State = 5.0
							Else
								e\room\NPC[3]\CurrSpeed = 0.0
								e\room\NPC[3]\State = 9.0
							EndIf
						EndIf
						
						If DistanceSquared(EntityX(me\Collider), EntityX(e\room\NPC[4]\Collider), EntityZ(me\Collider), EntityZ(e\room\NPC[4]\Collider)) > 2.25 And Dist < DistanceSquared(EntityX(e\room\NPC[3]\Collider), EntityX(e\room\NPC[4]\Collider), EntityZ(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[4]\Collider))
							e\room\NPC[4]\EnemyX = e\room\x + 280.0 * RoomScale
							e\room\NPC[4]\EnemyY = e\room\y + 0.3
							e\room\NPC[4]\EnemyZ = e\room\z - 713.0 * RoomScale
							e\room\NPC[4]\State = 3.0
						Else
							e\room\NPC[4]\EnemyX = EntityX(me\Collider)
							e\room\NPC[4]\EnemyY = EntityY(me\Collider)
							e\room\NPC[4]\EnemyZ = EntityZ(me\Collider)
							e\room\NPC[4]\Angle = 0.0
							e\room\NPC[4]\State = 5.0
						EndIf
					EndIf
					
					e\room\NPC[5]\SoundCHN2 = LoopSoundEx(e\room\NPC[5]\Sound2, e\room\NPC[5]\SoundCHN2, Camera, e\room\NPC[5]\OBJ, 2.0, 0.5)
					
					If EntityX(me\Collider) < e\room\x - 5376.0 * RoomScale And e\EventStr = ""
						If Rand(3) = 1
							e\EventStr = "Scripted\Scripted" + Rand(0, 4) + ".ogg|Off.ogg|"
						Else
							; ~ GENERATE THE IA...
							; ~ ATTENTION...
							e\EventStr = "1\Attention" + Rand(0, 1) + ".ogg"
							Select Rand(3)
								Case 1
									;[Block]
									StrTemp = "Crew"
									e\EventStr = e\EventStr + "|2\Crew" + Rand(0, 5) + ".ogg"
									;[End Block]
								Case 2
									;[Block]
									StrTemp = "Scientist"
									e\EventStr = e\EventStr + "|2\Scientist" + Rand(0, 17) + ".ogg"
									;[End Block]
								Case 3
									;[Block]
									StrTemp = "Security"
									e\EventStr = e\EventStr + "|2\Security" + Rand(0, 5) + ".ogg"
									;[End Block]
							End Select
							
							If Rand(2) = 1 And StrTemp = "Scientist"
								; ~ CALL ON LINE...
								e\EventStr = e\EventStr + "|3\CallOnLine.ogg"
								
								e\EventStr = e\EventStr + "|Numbers\" + Rand(9) + ".ogg"
								If Rand(2) = 1 Then e\EventStr = e\EventStr + "|Numbers\" + Rand(9) + ".ogg"
							Else
								; ~ REPORT TO...
								e\EventStr = e\EventStr + "|3\Report" + Rand(0, 1) + ".ogg"
								
								Select StrTemp
									Case "Crew"
										;[Block]
										e\EventStr = e\EventStr + "|4\Crew" + Rand(0, 6) + ".ogg"
										If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Crew" + Rand(0, 6) + ".ogg"
										;[End Block]
									Case "Scientist"
										;[Block]
										e\EventStr = e\EventStr + "|4\Scientist" + Rand(0, 7) + ".ogg"
										If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Scientist0.ogg"
										;[End Block]
									Case "Security"
										;[Block]
										e\EventStr = e\EventStr + "|4\Security" + Rand(0, 5) + ".ogg"
										If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Security" + Rand(0, 2) + ".ogg"
										;[End Block]
								End Select
							EndIf
							e\EventStr = e\EventStr + "|Off.ogg|"
						EndIf
					EndIf
					
					If e\EventStr <> "" And e\EventStr <> "Done"
						If e\SoundCHN = 0 Then e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\Intro\IA\On.ogg"), True)
						If (Not ChannelPlaying(e\SoundCHN))
							StrTemp = Left(e\EventStr, Instr(e\EventStr, "|", 1) - 1)
							LoadEventSound(e, "SFX\Room\Intro\IA\" + StrTemp)
							e\SoundCHN = PlaySound_Strict(e\Sound, True)
							e\EventStr = Right(e\EventStr, Len(e\EventStr) - Len(StrTemp) - 1)
							If e\EventStr = ""
								If (Not ChannelPlaying(e\room\NPC[3]\SoundCHN))
									For i = 3 To 4
										e\room\NPC[i]\SoundCHN = PlaySoundEx(e\room\NPC[i]\Sound2, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
									Next
								Else
									For i = 3 To 4
										FreeSound_Strict(e\room\NPC[i]\Sound2) : e\room\NPC[i]\Sound2 = 0
									Next
								EndIf
								
								e\EventStr = "Done"
							EndIf
						EndIf
					EndIf
					
					If e\room\NPC[6]\State = 0.0
						If e\room\RoomDoors[5]\Open
							If DistanceSquared(EntityX(me\Collider), e\room\x - 3328.0 * RoomScale, EntityZ(me\Collider), e\room\z - 1232.0 * RoomScale) < 25.0
								If e\EventStr = "Done"
									LoadEventSound(e, "SFX\Room\Intro\IA\Scripted\Announcement" + Rand(0, 6) + ".ogg")
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
								e\room\NPC[6]\State = 1.0
							EndIf
						EndIf
					Else
						If (Not ChannelPlaying(e\room\NPC[7]\SoundCHN)) Then e\room\NPC[7]\SoundCHN = PlaySoundEx(e\room\NPC[7]\Sound, Camera, e\room\NPC[7]\Collider, 7.0, 1.0, True)
						If EntityZ(e\room\NPC[6]\Collider) > e\room\z - 64.0 * RoomScale
							RotateEntity(e\room\NPC[6]\Collider, 0.0, CurveAngle(-90.0, EntityYaw(e\room\NPC[6]\Collider), 15.0), 0.0)
							If e\room\RoomDoors[5]\Open Then OpenCloseDoor(e\room\RoomDoors[5])
							If e\room\RoomDoors[5]\OpenState < 1.0
								e\room\NPC[6]\State = -1.0 : e\room\NPC[6]\CurrSpeed = 0.0
								HideEntity(e\room\NPC[6]\OBJ)
							EndIf
						EndIf
					EndIf
					
					; ~ Randomly rotate the scientist sitting on chair
					;e\room\NPC[7]\GravityMult = 0.0
					RotateEntity(e\room\NPC[7]\Collider, 0.0, 180.0 + Sin(MilliSec / 20.0) * 3.0, 0.0, True)
					UpdateSoundOrigin(e\room\NPC[7]\SoundCHN, Camera, e\room\NPC[7]\Collider, 7.0, 1.0, True)
					
					If e\room\NPC[8] <> Null
						If e\room\NPC[8]\State = 7.0
							If DistanceSquared(EntityX(me\Collider), e\room\x - 6688.0 * RoomScale, EntityZ(me\Collider), e\room\z - 1252.0 * RoomScale) < 6.25
								e\room\NPC[8]\State = 10.0
								e\room\NPC[9]\State = 10.0
								e\room\NPC[10]\State = 1.0
							EndIf
						Else
							If EntityX(e\room\NPC[8]\Collider) < e\room\x - 7100.0 * RoomScale
								For i = 8 To 10
									RemoveNPC(e\room\NPC[i])
								Next
							EndIf
						EndIf
					EndIf
					If e\room\NPC[11] <> Null
						If e\room\NPC[11]\State = 15.0
							If DistanceSquared(EntityX(me\Collider), e\room\x - 6688.0 * RoomScale, EntityZ(me\Collider), e\room\z - 1252.0 * RoomScale) < 6.25 Then e\room\NPC[11]\State = 16.0
						Else
							If EntityX(e\room\NPC[11]\Collider) > e\room\x - 2000.0 * RoomScale
								RemoveNPC(e\room\NPC[11])
								For i = SOUND_NPC_VEHICLE_IDLE To SOUND_NPC_VEHICLE_IDLE
									If NPCSound[i] <> 0 Then FreeSound_Strict(NPCSound[i]) : NPCSound[i] = 0
								Next
							EndIf
						EndIf
					EndIf
					AnimateNPC(e\room\NPC[12], 357.0, 381.0, 0.05)
					
					If e\room\NPC[3]\State <> 11.0
						If DistanceSquared(EntityX(e\room\NPC[3]\Collider), EntityX(e\room\RoomDoors[2]\FrameOBJ, True), EntityZ(e\room\NPC[3]\Collider), EntityZ(e\room\RoomDoors[2]\FrameOBJ, True)) < 20.25
							e\room\NPC[3]\State = 9.0
							If DistanceSquared(EntityX(me\Collider), EntityX(e\room\RoomDoors[2]\FrameOBJ, True), EntityZ(me\Collider), EntityZ(e\room\RoomDoors[2]\FrameOBJ, True)) < 20.25
								RemoveNPC(e\room\NPC[5])
								RemoveNPC(e\room\NPC[7])
								RemoveNPC(e\room\NPC[12])
								
								For i = 3 To 4
									If e\room\NPC[i]\Sound <> 0 Then FreeSound_Strict(e\room\NPC[i]\Sound) : e\room\NPC[i]\Sound = 0
									If e\room\NPC[i]\Sound2 <> 0 Then FreeSound_Strict(e\room\NPC[i]\Sound2) : e\room\NPC[i]\Sound2 = 0
									StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
									StopChannel(e\room\NPC[i]\SoundCHN2) : e\room\NPC[i]\SoundCHN2 = 0
								Next
								e\room\NPC[3]\State3 = 0.0
								LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\EscortDone" + Rand(0, 4) + ".ogg")
								e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
								
								e\room\NPC[4]\State = 9.0
								
								OpenCloseDoor(e\room\RoomDoors[3])
								
								FreeEntity(e\room\Objects[4]) : e\room\Objects[4] = 0
								
								For i = 0 To 2
									ShowEntity(e\room\NPC[i]\OBJ)
								Next
								PointEntity(e\room\NPC[6]\Collider, e\room\OBJ)
								RotateEntity(e\room\NPC[6]\Collider, 0.0, EntityYaw(e\room\NPC[6]\Collider), 0.0)
								TFormPoint(-877.0, 500.0, 465.0, e\room\OBJ, 0)
								PositionEntity(e\room\NPC[6]\Collider, TFormedX(), TFormedY(), TFormedZ())
								ResetEntity(e\room\NPC[6]\Collider)
								
								e\EventState = INTRO_GIVE_PAPER
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case INTRO_GIVE_PAPER
					;[Block]
					If (Not ChannelPlaying(e\room\NPC[3]\SoundCHN)) And e\room\NPC[3]\Frame < 358.0
						e\room\NPC[3]\State = 8.0
						LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\OhAndByTheWay.ogg")
						e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
						SetNPCFrame(e\room\NPC[3], 358.0)
					ElseIf e\room\NPC[3]\Frame >= 358.0 And e\room\NPC[3]\Frame < 608.0
						PointEntity(e\room\NPC[3]\Collider, me\Collider)
						RotateEntity(e\room\NPC[3]\Collider, 0.0, EntityYaw(e\room\NPC[3]\Collider), 0.0)
						
						If e\room\NPC[3]\Frame < 482.0
							AnimateNPC(e\room\NPC[3], 358.0, 482.0, 0.4, False)
						Else
							AnimateNPC(e\room\NPC[3], 483.0, 607.0, 0.2)
							If EntityDistanceSquared(me\Collider, e\room\NPC[3]\Collider) < 2.25
								If EntityInView(e\room\NPC[3]\OBJ, Camera)
									HandEntity = e\room\NPC[3]\OBJ
									If mo\MouseHit1
										SelectedItem = CreateItem("Testing Brief", it_paper, 0.0, 0.0, 0.0)
										PickItem(SelectedItem)
										
										FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										SetNPCFrame(e\room\NPC[3], 608.0)
									EndIf
								EndIf
							EndIf
						EndIf
					ElseIf e\room\NPC[3]\Sound = 0
						If e\room\NPC[3]\Frame < 621.0 And e\room\NPC[3]\State = 8.0
							AnimateNPC(e\room\NPC[3], 608.0, 621.0, 0.4, False)
						Else
							e\room\NPC[3]\Angle = EntityYaw(e\room\NPC[3]\Collider)
							e\room\NPC[3]\State = 9.0
							
							OpenCloseDoor(e\room\RoomDoors[2])
							
							CreateHintMsg(GetLocalString("msg", "enterchmbr"))
							
							e\EventState = INTRO_ESCORT_DONE
						EndIf
					EndIf
					;[End Block]
				Case INTRO_ESCORT_DONE
					;[Block]
					If e\room\RoomDoors[2]\Open And e\room\NPC[3]\State <> 11.0
						If IsEqual(EntityX(me\Collider, True), EntityX(e\room\OBJ, True), 2.0)
							For i = 1 To 2
								OpenCloseDoor(e\room\RoomDoors[i])
							Next
							
							ShowEntity(n_I\Curr173\OBJ)
							ShowEntity(n_I\Curr173\OBJ2)
							ShowEntity(e\room\NPC[6]\OBJ)
							ShowEntity(e\room\NPC[13]\OBJ)
							ShowEntity(e\room\RoomDoors[6]\OBJ)
							ShowEntity(e\room\RoomDoors[6]\OBJ2)
							ShowEntity(e\room\RoomDoors[6]\FrameOBJ)
							
							e\EventState2 = 0.0
							e\EventState3 = 0.0
							e\EventState = INTRO_IN_CHAMBER
						EndIf
					EndIf
					;[End Block]
				Case INTRO_IN_CHAMBER
					;[Block]
					If snd_I\IntroSFX[3] <> 0 And e\EventState2 <> 1.0
						If EntityVisible(Camera, n_I\Curr173\OBJ) And EntityInView(n_I\Curr173\OBJ, Camera)
							CreateHintMsg(Format(GetLocalString("msg", "blink"), key\Name[key\BLINK]))
							PlaySound_Strict(snd_I\IntroSFX[3])
							e\EventState2 = 1.0
						EndIf
					EndIf
					
					e\room\NPC[6]\GravityMult = 0.0
					If e\EventState3 < 14000.0
						If ChannelPlaying(e\SoundCHN)
							If AnimTime(e\room\NPC[6]\OBJ) >= 325.0
								AnimateNPC(e\room\NPC[6], 326.0, 328.0, 0.02, False)
							Else
								AnimateNPC(e\room\NPC[6], 320.0, 328.0, 0.05, False)
							EndIf
						Else
							AnimateNPC(e\room\NPC[6], 328.0, 320.0, -0.02, False)
						EndIf
					EndIf
					
					For i = 0 To 2
						UpdateSoundOrigin(e\room\NPC[i]\SoundCHN, Camera, e\room\NPC[i]\Collider, 10.0 + ((i = 0) * 5.0), 1.0, True)
					Next
					
					If e\EventState3 < 10000.0
						If e\room\NPC[3] <> Null
							If e\room\RoomDoors[2]\OpenState < 1.0
								For d.Doors = Each Doors
									If d\room = e\room
										If EntityDistanceSquared(d\FrameOBJ, e\room\OBJ) > 81.0 Then RemoveDoor(d)
									EndIf
								Next
								For p.Props = Each Props
									If p\room = e\room
										If EntityDistanceSquared(p\OBJ, e\room\OBJ) > 81.0 Then RemoveProp(p)
									EndIf
								Next
								For sc.SecurityCams = Each SecurityCams
									If sc\room = e\room Then RemoveSecurityCam(sc)
								Next
								For l.Lights = Each Lights
									If l\room = e\room
										If EntityDistanceSquared(l\OBJ, e\room\OBJ) > 81.0 Then RemoveLight(l)
									EndIf
								Next
								For w.WayPoints = Each WayPoints
									If w\room = e\room Then RemoveWaypoint(w)
								Next
								For s.Screens = Each Screens
									If s\room = e\room Then RemoveScreen(s)
								Next
								
								Local n.NPCs
								
								TFormPoint(1571.0, 0.0, 846.0, e\room\OBJ, 0)
								n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), e\room\y + 0.05, TFormedZ())
								RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
								
								TFormPoint(1850.0, 0.0, 715.0, e\room\OBJ, 0)
								n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), e\room\y + 0.05, TFormedZ())
								RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
								
								TFormPoint(958.0, 0.0, -33.0, e\room\OBJ, 0)
								n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), e\room\y + 0.05, TFormedZ())
								RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
								
								RemoveNPC(e\room\NPC[4])
								RemoveNPC(e\room\NPC[14])
;								RemoveNPC(e\room\NPC[15])
								RemoveNPC(e\room\NPC[3])
							EndIf
						EndIf
						
						FPSFactorEx = fps\Factor[0] / 3.0
						e\EventState3 = Min(e\EventState3 + FPSFactorEx, 5000.0)
						If e\EventState3 >= 130.0 And e\EventState3 - FPSFactorEx < 130.0
							LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\EnterChamber.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound, True)
						ElseIf e\EventState3 > 230.0
							Temp = True
							For i = 1 To 2
								If DistanceSquared(EntityX(e\room\NPC[i]\Collider), EntityX(e\room\Objects[i - 1], True), EntityZ(e\room\NPC[i]\Collider), EntityZ(e\room\Objects[i - 1], True)) > 0.09
									PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[i - 1])
									RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
									If e\EventState3 > (200.0 + (i * 30.0)) Then e\room\NPC[i]\State = 1.0
									Temp = False
								Else
									e\room\NPC[i]\State = 0.0
									
									PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[2])
									RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
								EndIf
							Next
							
							If EntityX(me\Collider) < e\room\x + 696.0 * RoomScale
								If e\EventState3 >= 450.0 And e\EventState3 - FPSFactorEx < 450.0
									LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\Refuse0.ogg")
									e\SoundCHN = PlaySound_Strict(e\Sound, True)
								ElseIf e\EventState3 >= 650.0 And e\EventState3 - FPSFactorEx < 650.0
									LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\Refuse1.ogg")
									e\SoundCHN = PlaySound_Strict(e\Sound, True)
								ElseIf e\EventState3 >= 850.0 And e\EventState3 - FPSFactorEx < 850.0
									LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\Refuse2.ogg")
									e\SoundCHN = PlaySound_Strict(e\Sound, True)
									
									OpenCloseDoor(e\room\RoomDoors[1])
								ElseIf e\EventState3 > 1000.0
									e\room\NPC[0]\State = 1.0 : e\room\NPC[0]\State2 = 10.0 : e\room\NPC[0]\State3 = 1.0
								EndIf
								If e\EventState3 > 850.0 Then PositionEntity(me\Collider, Min(EntityX(me\Collider), e\room\x + 640.0 * RoomScale), EntityY(me\Collider), EntityZ(me\Collider))
							ElseIf Temp = True
								OpenCloseDoor(e\room\RoomDoors[1])
								e\EventState3 = 10000.0
							EndIf
						EndIf
					ElseIf e\EventState3 < 14000.0 ; ~ Player is inside the room
						e\EventState3 = Min(e\EventState3 + fps\Factor[0], 13000.0)
						
						For i = 1 To 2
							PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[2])
							RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
						Next
						; ~ Do not let the player leave the chamber
						If e\EventState3 < 10300.0 Then PositionEntity(me\Collider, Max(EntityX(me\Collider), e\room\x + 640.0 * RoomScale), EntityY(me\Collider), EntityZ(me\Collider))
						
						If e\EventState3 >= 10300.0 And e\EventState3 - fps\Factor[0] < 10300.0
							LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\Approach173.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound, True)
						ElseIf e\EventState3 >= 10440.0 And e\EventState3 - fps\Factor[0] < 10440.0
							OpenCloseDoor(e\room\RoomDoors[1])
							PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[1]\FrameOBJ, 7.0)
						ElseIf e\EventState3 >= 10740.0 And e\EventState3 - fps\Factor[0] < 10740.0
							LoadEventSound(e, "SFX\Room\Intro\Scientist\Franklin\Problem.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound, True)
						ElseIf e\EventState3 >= 11145.0 And e\EventState3 - fps\Factor[0] < 11145.0
							PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
							e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\Intro\ClassD\DontLikeThis.ogg")
							e\room\NPC[1]\SoundCHN = PlaySoundEx(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider, 10.0, 1.0, True)
						ElseIf e\EventState3 >= 11561.0 And e\EventState3 - fps\Factor[0] < 11561.0
							PlaySound_Strict(snd_I\IntroSFX[2])
							
							e\room\NPC[2]\Sound = LoadSound_Strict("SFX\Room\Intro\ClassD\Breen.ogg")
							e\room\NPC[2]\SoundCHN = PlaySoundEx(e\room\NPC[2]\Sound, Camera, e\room\NPC[2]\Collider, 10.0, 1.0, True)
							
							SetNPCFrame(e\room\NPC[6], 357.0)
							e\EventState3 = 14000.0
						EndIf
						
						If e\EventState3 >= 10440.0 And e\EventState3 - fps\Factor[0] < 11561.0
							If EntityX(me\Collider) < EntityX(e\room\RoomDoors[1]\FrameOBJ, True)
								If e\room\NPC[0]\State <> 12.0
									e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Balcony\Alert" + Rand(0, 1) + ".ogg")
									e\room\NPC[0]\SoundCHN = PlaySoundEx(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 15.0, 1.0, True)
									e\room\NPC[0]\State2 = 1.0
									e\room\NPC[0]\State = 12.0
								EndIf
							EndIf
						EndIf
						
						If e\EventState3 > 10300.0
							If e\EventState3 > 10560.0
								If e\EventState3 < 10750.0
									e\room\NPC[1]\State = 1.0 : e\room\NPC[1]\CurrSpeed = 0.005
								Else
									e\room\NPC[1]\State = 0.0
									e\room\NPC[1]\CurrSpeed = CurveValue(0.0, e\room\NPC[1]\CurrSpeed, 10.0)
								EndIf
							EndIf
						EndIf
					ElseIf e\EventState3 < 20000.0
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(Camera), EntityY(n_I\Curr173\Collider, True) - 0.05, EntityZ(Camera))
						PointEntity(Pvt, n_I\Curr173\Collider)
						RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), 40.0), 0.0)
						
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, 40.0)
						CameraPitch = CameraPitch - 90.0
						FreeEntity(Pvt) : Pvt = 0
						
						AnimateNPC(e\room\NPC[6], 357.0, 381.0, 0.05)
						For i = 13 To 14
							If e\room\NPC[i] <> Null
								PointEntity(e\room\NPC[i]\Collider, n_I\Curr173\Collider)
								RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
							EndIf
						Next
						e\EventState3 = Min(e\EventState3 + fps\Factor[0], 19000.0)
						If e\EventState3 < 14100.0
							If e\EventState3 < 14060.0
								me\BlinkTimer = Max((14000.0 - e\EventState3) / 2.0 - Rnd(0.0, 1.0), -10.0)
								If me\BlinkTimer = -10.0
									PointEntity(n_I\Curr173\Collider, e\room\NPC[1]\OBJ)
									RotateEntity(n_I\Curr173\Collider, 0.0, EntityYaw(n_I\Curr173\Collider), 0)
									MoveEntity(n_I\Curr173\Collider, 0.0, 0.0, n_I\Curr173\Speed * 0.6 * fps\Factor[0])
									
									n_I\Curr173\SoundCHN = LoopSoundEx(snd_I\StoneDragSFX, n_I\Curr173\SoundCHN, Camera, n_I\Curr173\Collider, 10.0, n_I\Curr173\State)
									n_I\Curr173\State = CurveValue(1.0, n_I\Curr173\State, 3.0)
								Else
									n_I\Curr173\State = Max(0.0, n_I\Curr173\State - fps\Factor[0] / 20.0)
								EndIf
							ElseIf e\EventState3 < 14065.0
								me\BlinkTimer = -10.0
								
								OpenCloseDoor(e\room\RoomDoors[6])
								
								PlaySoundEx(snd_I\NeckSnapSFX[Rand(0, 2)], Camera, n_I\Curr173\Collider)
								PlaySound_Strict(snd_I\IntroSFX[4])
								me\BigCameraShake = 3.0
								e\room\NPC[1]\IsDead = True
								PositionEntity(n_I\Curr173\Collider, EntityX(e\room\NPC[1]\OBJ), EntityY(n_I\Curr173\Collider), EntityZ(e\room\NPC[1]\OBJ))
								ResetEntity(n_I\Curr173\Collider)
								PointEntity(n_I\Curr173\Collider, e\room\NPC[2]\Collider)
								
								e\room\NPC[2]\State = -1.0
								AnimateNPC(e\room\NPC[2], 406.0, 382.0, -0.015)
								MoveEntity(e\room\NPC[2]\Collider, 0.0, 0.0, (-0.01) * fps\Factor[0])
								
								StopChannel(e\room\NPC[0]\SoundCHN) : e\room\NPC[0]\SoundCHN = 0
								LoadNPCSound(e\room\NPC[0], "SFX\Room\Intro\Guard\Balcony\WTF" + Rand(0, 1) + ".ogg")
								e\room\NPC[0]\SoundCHN = PlaySoundEx(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0, 1.0, True)
								e\room\NPC[0]\State = 12.0 : e\room\NPC[0]\State2 = 0.0 : e\room\NPC[0]\Angle = 180.0
								e\EventState3 = 14065.0
							Else
								If e\room\NPC[2]\Sound = 0
									LoadNPCSound(e\room\NPC[2], "SFX\Room\Intro\ClassD\Gasp.ogg")
									PlaySoundEx(e\room\NPC[2]\Sound, Camera, e\room\NPC[2]\Collider, 8.0, 1.0, True)
								EndIf
							EndIf
							
							If e\EventState3 > 14080.0 And e\EventState3 - fps\Factor[0] < 14080.0 Then PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
							me\BigCameraShake = 3.0
						ElseIf e\EventState3 < 14200.0
							e\room\NPC[0]\State = 8.0
							If e\EventState3 > 14115.0
								If (Not e\room\NPC[2]\IsDead)
									FreeSound_Strict(e\room\NPC[2]\Sound) : e\room\NPC[2]\Sound = 0
									PlaySoundEx(snd_I\NeckSnapSFX[1], Camera, e\room\NPC[2]\Collider, 8.0)
									e\room\NPC[2]\IsDead = True
								EndIf
								PositionEntity(n_I\Curr173\Collider, EntityX(e\room\NPC[2]\OBJ), EntityY(n_I\Curr173\Collider), EntityZ(e\room\NPC[2]\OBJ))
								ResetEntity(n_I\Curr173\Collider)
								PointEntity(n_I\Curr173\Collider, me\Collider)
							EndIf
							If e\EventState3 < 14130.0
								SetNPCFrame(e\room\NPC[2], 50.0)
								me\BlinkTimer = -10.0 : me\LightBlink = 1.0
							Else
								n_I\Curr173\Idle = 0
							EndIf
							If e\EventState3 >= 14100.0 And e\EventState3 - fps\Factor[0] < 14100.0 Then PlaySound_Strict(snd_I\IntroSFX[5])
							If e\EventState3 < 14150.0
								me\BigCameraShake = 5.0
								If EntityX(me\Collider) >= e\room\x + 696.0 * RoomScale Then CreateHintMsg(GetLocalString("msg", "leavechmbr"), 1.0)
							EndIf
						Else
							If e\EventState3 > 14300.0
								If e\EventState3 > 14600.0 And e\EventState3 < 14700.0
									me\BlinkTimer = -10.0
									me\LightBlink = 1.0
								EndIf
								If EntityX(me\Collider) >= e\room\x + 696.0 * RoomScale
									CreateHintMsg(GetLocalString("msg", "leavechmbr"), 1.0)
								Else
									e\EventState3 = 20000.0
								EndIf
							EndIf
						EndIf
					ElseIf e\EventState3 < 30000.0
						e\EventState3 = Min(e\EventState3 + fps\Factor[0], 30000.0)
						If e\EventState3 < 20100.0
							me\BigCameraShake = 2.0
						Else
							If e\EventState3 < 20200.0
								If e\EventState3 > 20105.0 And e\EventState3 - fps\Factor[0] < 20105.0
									PlaySound_Strict(snd_I\IntroSFX[6])
									TFormPoint(128.0, 480.0, 1280.0, e\room\OBJ, 0)
									PositionEntity(e\room\NPC[0]\Collider, TFormedX(), TFormedY(), TFormedZ())
									ResetEntity(e\room\NPC[0]\Collider)
									
									StopChannel(e\room\NPC[0]\SoundCHN) : e\room\NPC[0]\SoundCHN = 0
									LoadNPCSound(e\room\NPC[0], "SFX\Room\Intro\Guard\Balcony\OhShit.ogg")
									e\room\NPC[0]\SoundCHN = PlaySoundEx(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0, 1.0, True)
									
									n_I\Curr173\Angle = 0.0 : n_I\Curr173\Idle = 1
									PointEntity(e\room\NPC[0]\Collider, n_I\Curr173\OBJ)
									TFormPoint(-320.0, 480.0, 1312.0, e\room\OBJ, 0)
									PositionEntity(n_I\Curr173\Collider, TFormedX(), TFormedY(), TFormedZ())
									ResetEntity(n_I\Curr173\Collider)
									PointEntity(n_I\Curr173\Collider, e\room\NPC[0]\Collider)
								EndIf
								me\BlinkTimer = -10.0 : me\LightBlink = 1.0 : me\BigCameraShake = 3.0
							ElseIf e\EventState3 < 20300.0
								PointEntity(e\room\NPC[0]\Collider, n_I\Curr173\Collider)
								e\room\NPC[0]\State = 2.0
								If e\EventState3 > 20260.0 And e\EventState3 - fps\Factor[0] < 20260.0 Then PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
							Else
								If e\EventState3 - fps\Factor[0] < 20300.0
									me\BlinkTimer = -10.0
									me\LightBlink = 1.0
									me\BigCameraShake = 3.0
									me\CurrSpeed = 0.0
									me\Playable = 0
									
									PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
									
									If e\room\NPC[0]\Sound <> 0 Then FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
									e\room\NPC[0]\SoundCHN = PlaySoundEx(snd_I\NeckSnapSFX[1], Camera, e\room\NPC[0]\Collider, 8.0)
									
									n_I\Curr173\Idle = 0
									
									PlaySound_Strict(LoadSound_Strict("SFX\Room\Intro\173Vent.ogg"))
									
									PositionEntity(n_I\Curr173\Collider, 0.0, -500.0, 0.0)
									ResetEntity(n_I\Curr173\Collider)
									
									For r.Rooms = Each Rooms
										If r\RoomTemplate\RoomID = r_cont1_173
											x = r\x + 3712.0 * RoomScale
											y = r\y + 384.0 * RoomScale
											z = r\z + 3360.0 * RoomScale
											
											TeleportEntity(me\Collider, x + (EntityX(me\Collider) - e\room\x - 288.0 * RoomScale), y + (EntityY(me\Collider) - e\room\y + 0.4), z + (EntityZ(me\Collider) - e\room\z + 32.0 * RoomScale))
											TeleportToRoom(r)
											
											fog\FarDist = 6.0
											
											For i = 0 To 2
												PositionEntity(e\room\NPC[i]\Collider, x + (EntityX(e\room\NPC[i]\Collider) - e\room\x - 288.0 * RoomScale), y + EntityY(e\room\NPC[i]\Collider) - e\room\y, z + (EntityZ(e\room\NPC[i]\Collider) - e\room\z + 32.0 * RoomScale))
												ResetEntity(e\room\NPC[i]\Collider)
											Next
											
											FreeSound_Strict(snd_I\IntroSFX[2]) : snd_I\IntroSFX[2] = 0
											For i = 4 To 6
												FreeSound_Strict(snd_I\IntroSFX[i]) : snd_I\IntroSFX[i] = 0
											Next
											
											r\NPC[0] = e\room\NPC[0]
											r\NPC[0]\State = 8.0
											
											e\room\NPC[6]\GravityMult = 1.0
											r\NPC[1] = e\room\NPC[6]
											
											r\NPC[7] = e\room\NPC[13]
											
											For d.Doors = Each Doors
												If d\room = e\room Then RemoveDoor(d)
											Next
											
											For l.Lights = Each Lights
												If l\room = e\room Then RemoveLight(l)
											Next
											
											For p.Props = Each Props
												If p\room = e\room Then RemoveProp(p)
											Next
											
											For se.SoundEmitters = Each SoundEmitters
												If se\room = e\room Then RemoveSoundEmitter(se)
											Next
											
											For sc.SecurityCams = Each SecurityCams
												If sc\room = e\room Then RemoveSecurityCam(sc)
											Next
											
											For w.WayPoints = Each WayPoints
												If w\room = e\room Then RemoveWaypoint(w)
											Next
											
											RemoveRoom(e\room)
											
											ClearConsole()
											
											ClearFogColor()
											InitializeIntroMovie = False
											
											RemoveEvent(e)
											Return
											Exit
										EndIf
									Next
								EndIf
							EndIf
						EndIf
					EndIf
					;[End Block]
			End Select
			
			If e\EventState < INTRO_IN_CHAMBER
				If (Not me\Terminated)
					Dist = DistanceSquared(EntityX(me\Collider), EntityX(e\room\NPC[3]\Collider), EntityZ(me\Collider), EntityZ(e\room\NPC[3]\Collider))
					
					If e\room\NPC[5] <> Null
						If e\room\NPC[5]\State <> 11.0
							If DistanceSquared(EntityX(me\Collider), EntityX(e\room\NPC[5]\Collider), EntityZ(me\Collider), EntityZ(e\room\NPC[5]\Collider)) < 25.0 And Dist > 25.0 And DistanceSquared(EntityX(me\Collider), EntityX(e\room\NPC[4]\Collider), EntityZ(me\Collider), EntityZ(e\room\NPC[4]\Collider)) > 25.0
								If EntityVisible(me\Collider, e\room\NPC[5]\Collider)
									For i = 3 To 5
										If i < 5
											StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
											If e\room\NPC[i]\Sound <> 0 Then FreeSound_Strict(e\room\NPC[i]\Sound) : e\room\NPC[i]\Sound = 0
										EndIf
										e\room\NPC[i]\State = 11.0 : e\room\NPC[i]\State3 = 1.0 : e\room\NPC[i]\Reload = 70.0 * 3.0
									Next
									e\room\NPC[5]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\Room\Intro\Guard\PlayerEscape.ogg"), Camera, e\room\NPC[5]\Collider, 10.0, 1.0, True)
								EndIf
							EndIf
						EndIf
					EndIf
					If e\room\NPC[3]\State <> 11.0
						If e\room\NPC[3]\State = 9.0 Lor e\room\NPC[3]\State = 5.0
							If e\EventState = INTRO_MOVING_TO_CHAMBER
								FPSFactorEx = fps\Factor[0]
							ElseIf e\EventState = INTRO_ESCORT_DONE
								Temp = 1.5
								If SelectedItem <> Null And SelectedItem\ItemTemplate\Img <> 0 Then Temp = 3.0
								FPSFactorEx = fps\Factor[0] / Temp
							Else
								FPSFactorEx = 0.0
							EndIf
							e\room\NPC[3]\State3 = Max(e\room\NPC[3]\State3 + FPSFactorEx, 50.0)
							
							If e\room\NPC[3]\State3 >= 70.0 * 8.0 And e\room\NPC[3]\State3 - FPSFactorEx < 70.0 * 8.0
								If e\room\NPC[3]\State2 < 2.0
									For i = 3 To 4
										StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
									Next
									LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\EscortRefuse" + Rand(0, 1) + ".ogg")
									e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
									e\room\NPC[3]\State2 = 3.0 : e\room\NPC[3]\State3 = 50.0
								ElseIf e\room\NPC[3]\State2 = 3.0
									For i = 3 To 4
										StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
									Next
									LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\EscortPissedOff" + Rand(0, 1) + ".ogg")
									e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
									e\room\NPC[3]\State2 = 4.0 : e\room\NPC[3]\State3 = 50.0
								ElseIf e\room\NPC[3]\State2 = 4.0
									For i = 3 To 4
										StopChannel(e\room\NPC[i]\SoundCHN) : e\room\NPC[i]\SoundCHN = 0
									Next
									LoadNPCSound(e\room\NPC[3], "SFX\Room\Intro\Guard\Ulgrin\EscortKill" + Rand(0, 1) + ".ogg")
									e\room\NPC[3]\SoundCHN = PlaySoundEx(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider, 10.0, 1.0, True)
									e\room\NPC[3]\State2 = 5.0 : e\room\NPC[3]\State3 = 50.0 + (70.0 * 2.5)
								ElseIf e\room\NPC[3]\State2 = 5.0
									For i = 3 To 4 + (e\room\NPC[5] <> Null)
										e\room\NPC[i]\State = 11.0 : e\room\NPC[i]\State3 = 1.0
									Next
								EndIf
							EndIf
						Else
							e\room\NPC[3]\State3 = Clamp(e\room\NPC[3]\State3 - FPSFactorEx, 0.0, 50.0)
						EndIf
					Else
						If e\EventState = INTRO_ESCORT_DONE
							If e\room\NPC[0]\State <> 1.0
								LoadNPCSound(e\room\NPC[0], "SFX\Room\Intro\Guard\Balcony\WTF" + Rand(0, 1) + ".ogg")
								e\room\NPC[0]\SoundCHN = PlaySoundEx(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0, 1.0, True)
								e\room\NPC[0]\State = 1.0 : e\room\NPC[0]\State2 = 10.0 : e\room\NPC[0]\State3 = 1.0 : e\room\NPC[0]\Reload = 70.0 * 3.0
							EndIf
							For i = 1 To 2
								PointEntity(e\room\NPC[i]\OBJ, me\Collider)
								RotateEntity(e\room\NPC[i]\Collider, 0.0, EntityYaw(e\room\NPC[i]\OBJ), 0.0)
							Next
						EndIf
					EndIf
					For i = 3 To 4 + (e\room\NPC[5] <> Null)
						UpdateSoundOrigin(e\room\NPC[i]\SoundCHN, Camera, e\room\NPC[i]\Collider, 10.0, 1.0, True)
					Next
				Else
					ShouldPlay = 66
					PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Guard\Ulgrin\EscortTerminated.ogg"))
					RemoveEvent(e)
					Return
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_205%(e.Events)
	Local i%
	
	If PlayerRoom = e\room
		Local n.NPCs
		
		If e\EventState = 0.0 Lor e\EventStr <> "LoadDone"
			If e\EventStr = "" And QuickLoadPercent = -1
				QuickLoadPercent = 0
				QuickLoad_CurrEvent = e
				e\EventStr = "Load0"
			EndIf
			
			If e\room\RoomDoors[1]\Open Lor e\room\RoomDoors[2]\Open
				GiveAchievement("205")
				
				TFormPoint(-1055.0, -74.8, 650.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeClerk, TFormedX(), TFormedY(), TFormedZ())
				n\State3 = -1.0 : n\IsDead = True
				ChangeNPCTextureID(n, NPC_CLERK_VICTIM_205_TEXTURE)
				SetNPCFrame(n, 40.0)
				RotateEntity(n\Collider, 0.0, e\room\Angle - 180.0, 0.0, True)
				
				e\EventState = 1.0
			EndIf
		Else
			ShouldPlay = 15
			
			Local Temp% = UpdateLever(e\room\RoomLevers[0]\OBJ, e\EventState > 7.0 Lor (e\room\RoomDoors[0]\OpenState > 0.0 And e\room\RoomDoors[0]\OpenState < 180.0))
			
			If e\EventState > 7.0 And RemoteDoorOn
				RotateEntity(e\room\RoomLevers[0]\OBJ, CurveAngle(80.0 - (e\room\RoomDoors[0]\Open * 160.0), EntityPitch(e\room\RoomLevers[0]\OBJ), 10.0), EntityYaw(e\room\RoomLevers[0]\OBJ), 0.0)
				If Rand(150 + (250 * e\room\RoomDoors[0]\Open)) = 1
					If e\room\RoomDoors[0]\Open
						PlaySoundEx(snd_I\DoorClose079, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
					Else
						PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
					EndIf
					OpenCloseDoor(e\room\RoomDoors[0])
				EndIf
			Else
				If Temp
					If (Not e\room\RoomDoors[0]\Open) Then OpenCloseDoor(e\room\RoomDoors[0])
				Else
					If e\room\RoomDoors[0]\Open Then OpenCloseDoor(e\room\RoomDoors[0])
				EndIf
			EndIf
			If e\EventState < 65.0
				If DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[1], True), EntityZ(me\Collider), EntityZ(e\room\Objects[1], True)) < 4.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					PlaySound_Strict(LoadTempSound("SFX\SCP\205\Enter.ogg"))
					
					e\EventState = Max(e\EventState, 65.0)
					
					For i = 2 To 4
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Next
					If (Not EntityHidden(e\room\Objects[5])) Then HideEntity(e\room\Objects[5])
					
					SetAnimTime(e\room\Objects[2], 492.0)
					SetAnimTime(e\room\Objects[3], 434.0)
					SetAnimTime(e\room\Objects[4], 434.0)
				EndIf
				
				e\EventState2 = e\EventState2 + fps\Factor[0]
			EndIf
			
			Select e\EventState
				Case 1.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 4
						If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
					Next
					If EntityHidden(e\room\Objects[5]) Then ShowEntity(e\room\Objects[5])
					; ~ Sitting
					AnimateEx(e\room\Objects[5], AnimTime(e\room\Objects[5]), 526.0, 530.0, 0.2)
					If e\EventState2 > 70.0 * 20.0 Then e\EventState = e\EventState + 1.0
					;[End Block]
				Case 3.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 4
						If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
					Next
					If EntityHidden(e\room\Objects[5]) Then ShowEntity(e\room\Objects[5])
					; ~ Laying down
					AnimateEx(e\room\Objects[5], AnimTime(e\room\Objects[5]), 377.0, 525.0, 0.2)
					If e\EventState2 > 70.0 * 30.0 Then e\EventState = e\EventState + 1.0
					;[End Block]
				Case 5.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 4
						If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
					Next
					If EntityHidden(e\room\Objects[5]) Then ShowEntity(e\room\Objects[5])
					; ~ Standing
					AnimateEx(e\room\Objects[5], AnimTime(e\room\Objects[5]), 228.0, 376.0, 0.2)
					If e\EventState2 > 70.0 * 40.0 
						PlaySoundEx(LoadTempSound("SFX\SCP\205\Horror.ogg"), Camera, e\room\Objects[5], 10.0, 0.3)
						e\EventState = e\EventState + 1.0
					EndIf
					;[End Block]
				Case 7.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 5
						If i < 4
							If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
						Else
							If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
						EndIf
					Next
					; ~ Sexy demon pose
					AnimateEx(e\room\Objects[4], AnimTime(e\room\Objects[4]), 500.0, 648.0, 0.2)
					If e\EventState2 > 70.0 * 60.0
						PlaySoundEx(LoadTempSound("SFX\SCP\205\Horror.ogg"), Camera, e\room\Objects[5], 10.0, 0.5)
						e\EventState = e\EventState + 1.0
					EndIf
					;[End Block]
				Case 9.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					If (Not EntityHidden(e\room\Objects[2])) Then HideEntity(e\room\Objects[2])
					For i = 3 To 5
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Next
					; ~ Idle
					AnimateEx(e\room\Objects[3], AnimTime(e\room\Objects[3]), 2.0, 200.0, 0.2)
					AnimateEx(e\room\Objects[4], AnimTime(e\room\Objects[4]), 2.0, 122.0, 0.2)
					
					If e\EventState2 > 70.0 * 80.0
						PlaySound_Strict(LoadTempSound("SFX\SCP\205\Horror.ogg"))
						e\EventState = e\EventState + 1.0
					EndIf
					;[End Block]
				Case 11.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 4
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Next
					If EntityHidden(e\room\Objects[5]) Then ShowEntity(e\room\Objects[5])
					
					; ~ Idle
					AnimateEx(e\room\Objects[2], AnimTime(e\room\Objects[2]), 2.0, 226.0, 0.2)
					AnimateEx(e\room\Objects[3], AnimTime(e\room\Objects[3]), 2.0, 200.0, 0.2)
					AnimateEx(e\room\Objects[4], AnimTime(e\room\Objects[4]), 2.0, 122.0, 0.2)
					
					If e\EventState2 > 70.0 * 85.0 Then e\EventState = e\EventState + 1.0
					;[End Block]
				Case 13.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					For i = 2 To 4
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Next
					If EntityHidden(e\room\Objects[5]) Then ShowEntity(e\room\Objects[5])
					If AnimTime(e\room\Objects[5]) <> 227.0 Then SetAnimTime(e\room\Objects[5], 227.0)
					
					AnimateEx(e\room\Objects[2], AnimTime(e\room\Objects[2]), 2.0, 491.0, 0.05)
					AnimateEx(e\room\Objects[3], AnimTime(e\room\Objects[3]), 197.0, 433.0, 0.05)
					AnimateEx(e\room\Objects[4], AnimTime(e\room\Objects[4]), 2.0, 433.0, 0.05)
					;[End Block]
				Case 66.0
					;[Block]
					If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					AnimateEx(e\room\Objects[2], AnimTime(e\room\Objects[2]), 492.0, 534.0, 0.1, False)
					AnimateEx(e\room\Objects[3], AnimTime(e\room\Objects[3]), 434.0, 466.0, 0.1, False)
					AnimateEx(e\room\Objects[4], AnimTime(e\room\Objects[4]), 434.0, 494.0, 0.1, False)
					
					If AnimTime(e\room\Objects[2]) > 533.0
						If (Not EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ)) Then HideEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
						e\EventState = 67.0
						e\EventState2 = 0.0
						e\EventState3 = 0.0
					EndIf
					;[End Block]
				Case 67.0
					;[Block]
					If DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[1], True), EntityZ(me\Collider), EntityZ(e\room\Objects[1], True)) < 7.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
						If Rand(100) = 1
							InjurePlayer(Rnd(0.3, 0.6), 0.0, 300.0, 0.12, 0.06)
							PlaySound_Strict(snd_I\DamageSFX[Rand(2, 3)])
							me\CameraShake = 0.5 * (I_1025\FineState[4] = 0.0)
							
							e\EventState2 = Rnd(-0.1, 0.1)
							e\EventState3 = Rnd(-0.1, 0.1)
							
							If me\Injuries > 5.0
								msg\DeathMsg = Format(GetLocalString("death", "205"), SubjectName)
								Kill(True)
							EndIf
						EndIf
					EndIf
					
					TranslateEntity(me\Collider, e\EventState2, 0.0, e\EventState3)
					e\EventState2 = CurveValue(e\EventState2, 0.0, 10.0)
					e\EventState3 = CurveValue(e\EventState3, 0.0, 10.0)
					;[End Block]
				Default
					;[Block]
					If Rand(3) = 1
						If (Not EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ)) Then HideEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					Else
						If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
					EndIf
					
					e\EventState3 = e\EventState3 + fps\Factor[0]
					If e\EventState3 > 50.0
						If EntityHidden(e\room\RoomSecurityCams[0]\ScrOBJ) Then ShowEntity(e\room\RoomSecurityCams[0]\ScrOBJ)
						e\EventState = e\EventState + 1.0
						e\EventState3 = 0.0
					EndIf
					;[End Block]
			End Select
		EndIf
	ElseIf e\room\Objects[2] <> 0
		; ~ Skip
	Else
		e\EventState = 0.0
		e\EventStr = ""
	EndIf
End Function

Function UpdateEvent_Cont1_914%(e.Events)
	If PlayerRoom = e\room
		Local it.Items
		Local i%, Angle#
		
		UpdateLever(e\room\RoomLevers[0]\OBJ)
		UpdateLever(e\room\RoomLevers[1]\OBJ, True)
		
		If e\room\RoomDoors[2]\Open Lor EntityPitch(e\room\RoomLevers[0]\OBJ) < 0.0
			GiveAchievement("914")
			e\EventState2 = 1.0
		EndIf
		
		If e\room\RoomDoors[2]\Locked = 1 Then e\room\RoomDoors[2]\Locked = (Not UpdateLever(e\room\RoomLevers[1]\OBJ))
		
		If e\EventState2 = 1.0 Then ShouldPlay = 21
		
		If EntityDistanceSquared(me\Collider, e\room\Objects[0]) < 0.64
			EntityPick(Camera, 0.8)
			For i = 0 To 1
				If PickedEntity() = e\room\Objects[i]
					HandEntity = e\room\Objects[i]
					If mo\MouseHit1 Lor mo\MouseDown1 Then GrabbedEntity = e\room\Objects[i]
					Exit
				EndIf
			Next
			
			If GrabbedEntity = e\room\Objects[0]
				If e\EventState = 0.0
					HandEntity = e\room\Objects[0]
					TurnEntity(GrabbedEntity, 0.0, 0.0, -mo\Mouse_X_Speed_1 * 2.5)
					
					Angle = WrapAngle(EntityRoll(e\room\Objects[0]))
					DrawArrowIcon[3] = True
					DrawArrowIcon[1] = (Angle > 181.0)
					
					If Angle < 90.0
						RotateEntity(GrabbedEntity, 0.0, 0.0, 361.0)
					ElseIf Angle < 180.0
						RotateEntity(GrabbedEntity, 0.0, 0.0, 180.0)
					EndIf
					
					If Angle < 181.0 And Angle > 90.0
						For it.Items = Each Items
							If it\Collider <> 0 And (Not it\Picked)
								If IsEqual(EntityX(it\Collider), e\room\x - 712.0 * RoomScale, 200.0) And IsEqual(EntityY(it\Collider), e\room\y + 648.0 * RoomScale, 104.0)
									e\SoundCHN = PlaySoundEx(snd_I\MachineSFX, Camera, e\room\Objects[1])
									e\room\RoomDoors[1]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\914\DoorClose.ogg"), Camera, e\room\RoomDoors[1]\OBJ)
									SetAnimTime(e\room\Objects[7], 1.0)
									e\EventState = 1.0
									Exit
								EndIf
							EndIf
						Next
					EndIf
				EndIf
			ElseIf GrabbedEntity = e\room\Objects[1]
				If e\EventState = 0.0
					HandEntity = e\room\Objects[1]
					TurnEntity(GrabbedEntity, 0.0, 0.0, -mo\Mouse_X_Speed_1 * 2.5)
					
					Angle = WrapAngle(EntityRoll(e\room\Objects[1]))
					DrawArrowIcon[1] = (Angle < 88.0 Lor Angle > 268.0)
					DrawArrowIcon[3] = (Angle < 92.0 Lor Angle > 272.0)
					
					If Angle > 90.0
						If Angle < 180.0
							RotateEntity(GrabbedEntity, 0.0, 0.0, 90.0)
						ElseIf Angle < 270.0
							RotateEntity(GrabbedEntity, 0.0, 0.0, 270.0)
						EndIf
					EndIf
					
					Local Rotation# = Floor(EntityRoll(e\room\Objects[1]))
					
					e\SoundCHN2 = 0
					If (Rotation > -94.0 And Rotation < -86.0) Lor (Rotation > -44.0 And Rotation < -36.0) Lor (Rotation > -4.0 And Rotation < 4.0) Lor (Rotation > 36.0 And Rotation < 44.0) Lor (Rotation > 86.0 And Rotation < 94.0)
						If e\SoundCHN = 0 Then e\SoundCHN = PlaySoundEx(snd_I\KnobSFX[Rand(0, 1)], Camera, e\room\Objects[1], 2.0, 0.5)
					Else
						e\SoundCHN = 0
					EndIf
				EndIf
			EndIf
			For i = 0 To 1
				If GrabbedEntity = e\room\Objects[i]
					If (Not EntityInView(e\room\Objects[i], Camera)) Then GrabbedEntity = 0
					Exit
				EndIf
			Next
		Else
			For i = 0 To 1
				If GrabbedEntity = e\room\Objects[i]
					GrabbedEntity = 0
					Exit
				EndIf
			Next
		EndIf
		
		Local Setting%
		
		If GrabbedEntity <> e\room\Objects[1]
			Angle = WrapAngle(EntityRoll(e\room\Objects[1]))
			If Angle < 22.5
				Angle = 0.0
				Setting = SETTING_ONETOONE
			ElseIf Angle < 67.5
				Angle = 40.0
				Setting = SETTING_COARSE
			ElseIf Angle < 180.0
				Angle = 90.0
				Setting = SETTING_ROUGH
			ElseIf Angle > 337.5
				Angle = -1.0
				Setting = SETTING_ONETOONE
			ElseIf Angle > 292.5
				Angle = -40.0
				Setting = SETTING_FINE
			Else
				Angle = -90.0
				Setting = SETTING_VERY_FINE
			EndIf
			RotateEntity(e\room\Objects[1], 0.0, 0.0, CurveValue(Angle, EntityRoll(e\room\Objects[1]), 20.0))
			If Angle = -90.0 Lor Angle = -40.0 Lor Angle = -1.0 Lor Angle = 0.0 Lor Angle = 40.0 Lor Angle = 90.0
				If e\SoundCHN2 = 0 Then e\SoundCHN2 = PlaySoundEx(snd_I\KnobSFX[Rand(0, 1)], Camera, e\room\Objects[1], 2.0, 0.5)
			EndIf
		EndIf
		
		If e\EventState > 0.0
			Local x# = 318.7
			Local y# = 300.0
			Local z# = 126.9
			
			e\EventState = e\EventState + fps\Factor[0]
			CanSave = 0
			If (Not n_I\Curr106\Contained)
				If n_I\Curr106\State = 1.0 Then n_I\Curr106\State2 = n_I\Curr106\State2 - (fps\Factor[0] * (1.0 + SelectedDifficulty\AggressiveNPCs))
			EndIf
			e\room\RoomDoors[1]\Open = False
			If e\EventState > 70.0 * 2.0
				If e\room\RoomDoors[0]\Open Then e\room\RoomDoors[0]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\914\DoorClose.ogg"), Camera, e\room\RoomDoors[0]\OBJ)
				e\room\RoomDoors[0]\Open = False
			EndIf
			
			TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, e\room\OBJ)
			
			Local IsPlayerInside% = IsEqual(TFormedX(), EntityX(e\room\Objects[2]), x) And IsEqual(TFormedY(), EntityY(e\room\Objects[2]), y) And IsEqual(TFormedZ(), EntityZ(e\room\Objects[2]), z) 
			
			If IsPlayerInside
				If Setting = SETTING_ROUGH Lor Setting = SETTING_COARSE
					If e\EventState > 70.0 * 2.6 And e\EventState - fps\Factor[1] < 70.0 * 2.6 Then PlaySound_Strict(LoadTempSound("SFX\SCP\914\PlayerDeath.ogg"))
				EndIf
				
				If e\EventState > 70.0 * 3.0
					Select Setting
						Case SETTING_ROUGH
							;[Block]
							Kill(False, False)
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
							msg\DeathMsg = Format(GetLocalString("death", "914"), SubjectName)
							;[End Block]
						Case SETTING_COARSE, SETTING_ONETOONE, SETTING_FINE, SETTING_VERY_FINE
							;[Block]
							If e\EventState - fps\Factor[1] < 70.0 * 3.0 Then PlaySound_Strict(LoadTempSound("SFX\SCP\914\PlayerUse.ogg"))
							;[End Block]
					End Select
					me\BlinkTimer = -10.0
				EndIf
			EndIf
			
			If e\EventState > 70.0 * 6.0
				RotateEntity(e\room\Objects[0], EntityPitch(e\room\Objects[0]), EntityYaw(e\room\Objects[0]), CurveAngle(0.0, EntityRoll(e\room\Objects[0]), 10.0))
			Else
				RotateEntity(e\room\Objects[0], EntityPitch(e\room\Objects[0]), EntityYaw(e\room\Objects[0]), 180.0)
			EndIf
			
			If e\EventState > 70.0 * 12.0
				For it.Items = Each Items
					If it\Collider <> 0 And (Not it\Picked)
						TFormPoint(EntityX(it\Collider), EntityY(it\Collider), EntityZ(it\Collider), 0, e\room\OBJ)
						If IsEqual(TFormedX(), EntityX(e\room\Objects[2]), x) And IsEqual(TFormedY(), EntityY(e\room\Objects[2]), y) And IsEqual(TFormedZ(), EntityZ(e\room\Objects[2]), z) Then Use914(it, Setting, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True))
					EndIf
				Next
				
				If IsPlayerInside
					Select Setting
						Case SETTING_COARSE
							;[Block]
							me\Injuries = 4.0
							CreateMsg(GetLocalString("msg", "914"))
							;[End Block]
						Case SETTING_ONETOONE
							;[Block]
							opt\InvertMouseX = (Not opt\InvertMouseX)
							opt\InvertMouseY = (Not opt\InvertMouseY)
							;[End Block]
						Case SETTING_FINE
							;[Block]
							chs\SuperMan = True
							;[End Block]
						Case SETTING_VERY_FINE
							;[Block]
							If I_427\Timer < 70.0 * 360.0 Then I_427\Timer = 70.0 * 360.0
							;[End Block]
					End Select
					me\BlurTimer = 1000.0
					PositionEntity(me\Collider, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True) + 1.0, EntityZ(e\room\Objects[3], True))
					ResetEntity(me\Collider)
					me\DropSpeed = 0.0
				EndIf
				e\room\RoomDoors[0]\Open = True
				e\room\RoomDoors[1]\Open = True
				RotateEntity(e\room\Objects[0], 0.0, 0.0, 0.0)
				
				Local OpenSFX914% = LoadTempSound("SFX\SCP\914\DoorOpen.ogg")
				
				e\room\RoomDoors[0]\SoundCHN = PlaySoundEx(OpenSFX914, Camera, e\room\RoomDoors[0]\OBJ)
				e\room\RoomDoors[1]\SoundCHN = PlaySoundEx(OpenSFX914, Camera, e\room\RoomDoors[1]\OBJ)
				
				me\Playable = 2
				
				e\EventState = 0.0
			EndIf
		EndIf
		UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[1])
		
		If ChannelPlaying(e\SoundCHN) Then AnimateEx(e\room\Objects[7], AnimTime(e\room\Objects[7]), 1.0, 74.0, 0.3, e\EventState <> 0.0)
	EndIf
	If e\room\Dist < 12.0
		If EntityPitch(e\room\RoomLevers[0]\OBJ) < 0.0
			e\EventState3 = Min(e\EventState3 + fps\Factor[0], 212.0)
		Else
			e\EventState3 = Max(e\EventState3 - fps\Factor[0], 0.0)
		EndIf
		If e\EventState3 > 0.0 And e\EventState3 < 212.0
			e\BlindsCHN = LoopSoundEx(snd_I\BlindsSFX, e\BlindsCHN, Camera, e\room\Objects[6], 2.0)
		Else
			StopChannel(e\BlindsCHN) : e\BlindsCHN = 0
		EndIf
		
		PositionEntity(e\room\Objects[5], 0.0, e\EventState3, 0.0)
		
		UpdateSoundOrigin(e\BlindsCHN, Camera, e\room\Objects[6])
	EndIf
End Function

Function UpdateEvent_Room2_2_LCZ_Fan%(e.Events)
	; ~ EventState: Timer for turning the fan on / off
	
	; ~ EventState2: Fan on / off
	
	; ~ EventState3: The speed of the fan
	
	If PlayerRoom = e\room
		TurnEntity(e\room\Objects[0], e\EventState3 * fps\Factor[0], 0.0, 0.0)
		If e\EventState3 > 0.01 Then e\SoundCHN = LoopSoundEx(snd_I\RoomAmbience[8], e\SoundCHN, Camera, e\room\Objects[0], 5.0, (e\EventState3 / 4.0))
		e\EventState3 = CurveValue(e\EventState2 * 5.0, e\EventState3, 150.0)
	EndIf
	
	If e\room\Dist < 16.0
		If e\EventState < 0.0
			Local Temp# = e\EventState2
			
			e\EventState2 = Rand(0.0, 1.0)
			If PlayerRoom <> e\room
				e\EventState3 = e\EventState2 * 5.0
			Else
				If Temp = 0.0 And e\EventState2 = 1.0 ; ~ Turn on the fan
					e\SoundCHN2 = PlaySoundEx(LoadTempSound("SFX\Ambient\Room ambience\FanOn.ogg"), Camera, e\room\Objects[0], 8.0)
				ElseIf Temp = 1.0 And e\EventState2 = 0.0 ; ~ Turn off the fan
					e\SoundCHN2 = PlaySoundEx(LoadTempSound("SFX\Ambient\Room ambience\FanOff.ogg"), Camera, e\room\Objects[0], 8.0)
				EndIf
			EndIf
			e\EventState = 70.0 * Rnd(15.0, 30.0)
		Else
			e\EventState = e\EventState - fps\Factor[0]
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Closets%(e.Events)
	Local it.Items, it2.Items
	Local i%
	
	If e\EventState = 0.0
		If PlayerRoom = e\room And n_I\Curr173\Idle < 2
			TFormPoint(-1180.0, -256.0, 896.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State3 = 2.0
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_LOGISTICS_TEXTURE)
			
			e\room\NPC[0]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\Character\Maintenance\EscapeFromClosets.ogg"), Camera, e\room\NPC[0]\Collider, 10.0, 1.0, True)
			
			TFormPoint(-1292.0, -256.0, -160.0, e\room\OBJ, 0)
			e\room\NPC[1] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[1]\State3 = 3.0
			ChangeNPCTextureID(e\room\NPC[1], NPC_CLASS_D_SECURITY_TEXTURE)
			
			PointEntity(e\room\NPC[0]\Collider, e\room\NPC[1]\Collider)
			PointEntity(e\room\NPC[1]\Collider, e\room\NPC[0]\Collider)
			
			e\EventState = 1.0
		EndIf
	Else
		UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 8.0, 1.0, True)
		UpdateSoundOrigin(e\room\NPC[1]\SoundCHN, Camera, e\room\NPC[1]\Collider, 8.0, 1.0, True)
		
		e\EventState = e\EventState + fps\Factor[0]
		If e\EventState < 70.0 * 3.0
			RotateEntity(e\room\NPC[1]\Collider, 0.0, CurveAngle(e\room\Angle + 90.0, EntityYaw(e\room\NPC[1]\Collider), 100.0), 0.0, True)
			e\room\NPC[0]\State = 1.0
			If e\EventState > 70.0 * 2.9 And e\EventState - fps\Factor[0] <= 70.0 * 2.9 Then PlaySoundEx(LoadTempSound("SFX\Room\Intro\173Vent.ogg"), Camera, e\room\OBJ, 15.0)
		ElseIf e\EventState < 70.0 * 6.5
			If e\EventState - fps\Factor[0] < 70.0 * 3.0
				e\room\NPC[0]\State = 0.0
				e\room\NPC[1]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\Character\Scientist\EscapeFromClosets.ogg"), Camera, e\room\NPC[1]\Collider, 10.0, 1.0, True)
			EndIf
			
			If e\EventState > 70.0 * 4.5
				PointEntity(e\room\NPC[0]\OBJ, e\room\OBJ)
				RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 30.0), 0.0, True)
			EndIf
			PointEntity(e\room\NPC[1]\OBJ, e\room\OBJ)
			TurnEntity(e\room\NPC[1]\OBJ, 0.0, Sin(e\EventState) * 25.0, 0.0)
			RotateEntity(e\room\NPC[1]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[1]\OBJ), EntityYaw(e\room\NPC[1]\Collider), 30.0), 0.0, True)
		Else
			If e\EventState - fps\Factor[0] < 70.0 * 6.5
				PlaySound_Strict(snd_I\HorrorSFX[0])
				PlaySound_Strict(snd_I\LightOffSFX)
				me\LightBlink = 9.0
			EndIf
			
			If e\EventState > 70.0 * 7.5 And e\EventState - fps\Factor[0] <= 70.0 * 7.5
				e\room\NPC[0]\IsDead = True
				If wi\NightVision > 0 Lor wi\SCRAMBLE > 0 Then me\BlinkTimer = -10.0
				
				PlaySoundEx(snd_I\NeckSnapSFX[0], Camera, e\room\NPC[0]\Collider, 8.0)
				
				TeleportEntity(n_I\Curr173\Collider, EntityX(e\room\NPC[0]\Collider, True) + 0.35, EntityY(e\room\NPC[0]\Collider, True) + 0.1, EntityZ(e\room\NPC[0]\Collider, True), n_I\Curr173\CollRadius + 0.12, True)
				n_I\Curr173\CurrentRoom = e\room
				PointEntity(n_I\Curr173\Collider, e\room\NPC[0]\Collider)
				n_I\Curr173\Idle = 1
			ElseIf e\EventState > 70.0 * 8.0 And e\EventState - fps\Factor[0] <= 70.0 * 8.0
				e\room\NPC[1]\IsDead = True
				If wi\NightVision > 0 Lor wi\SCRAMBLE > 0 Then me\BlinkTimer = -10.0
				
				PlaySoundEx(snd_I\NeckSnapSFX[1], Camera, e\room\NPC[1]\Collider, 8.0)
				
				PositionEntity(n_I\Curr173\Collider, EntityX(e\room\NPC[1]\Collider, True) + 0.35, EntityY(e\room\NPC[1]\Collider, True) + 0.15, EntityZ(e\room\NPC[1]\Collider, True))
				PointEntity(n_I\Curr173\Collider, e\room\NPC[1]\Collider)
				ResetEntity(n_I\Curr173\Collider)
				n_I\Curr173\Idle = 0
			ElseIf e\EventState > 70.0 * 9.0 And e\EventState - fps\Factor[0] <= 70.0 * 9.0
				TFormPoint(-1065.0, -380.0, 50.0, e\room\OBJ, 0)
				it.Items = CreateItem("Wallet", it_wallet, TFormedX(), TFormedY(), TFormedZ())
				For i = 0 To 1
					it2.Items = CreateItem("Quarter", it_25ct, 0.0, 0.0, 0.0)
					it2\Picked = True : it2\Dropped = -1
					it\SecondInv[i] = it2
					HideEntity(it2\Collider)
				Next
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Elevator%(e.Events)
	If e\EventState = 0.0
		If e\room\Dist < 8.0 And e\room\Dist > 0.0
			e\room\NPC[0] = CreateNPC(NPCTypeGuard, e\room\x, 0.5, e\room\z)
			PointEntity(e\room\NPC[0]\Collider, me\Collider)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
			
			e\room\RoomDoors[0]\IsElevatorDoor = 2
			
			e\EventState = 1.0
		EndIf
	Else
		If e\room\Objects[0] = 0
			TFormPoint(1024.0, 0.0, 0.0, e\room\OBJ, 0)
			e\room\Objects[0] = CreatePivot()
			PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ())
			EntityParent(e\room\Objects[0], e\room\OBJ)
		Else
			If e\EventState = 1.0
				If e\room\Dist < 5.0
					e\room\NPC[0]\EnemyX = EntityX(e\room\Objects[0], True)
					e\room\NPC[0]\EnemyY = EntityY(e\room\Objects[0], True)
					e\room\NPC[0]\EnemyZ = EntityZ(e\room\Objects[0], True)
					
					e\EventState = 2.0
				EndIf
			ElseIf e\EventState = 2.0
				If EntityDistanceSquared(e\room\NPC[0]\Collider, me\Collider) < 6.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					e\room\NPC[0]\State = 1.0 : e\room\NPC[0]\State3 = 1.0
				Else
					e\room\NPC[0]\State = 5.0 : e\room\NPC[0]\State3 = 0.0
				EndIf
				
				If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\Objects[0]) < 4.0
					OpenCloseDoor(e\room\RoomDoors[0])
					e\room\RoomDoors[0]\IsElevatorDoor = 0
					EntityTexture(e\room\RoomDoors[0]\ElevatorPanel[1], d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_DOWN])
					UpdateEntityMaterial(e\room\RoomDoors[0]\ElevatorPanel[1])
					UpdateDoorInstances(e\room\RoomDoors[0], BUTTON_YELLOW_TEXTURE)
					
					e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\ElevatorDeath.ogg"))
					
					e\EventState = 3.0
				EndIf
			Else
				e\EventState = e\EventState + fps\Factor[0]
				If PlayerInReachableRoom(True)
					If e\EventState > 70.0 * 6.7 And e\EventState < 70.0 * 7.4
						me\BigCameraShake = 7.4 - (e\EventState / 70.0)
						If Rand(2) = 1 Then SetEmitter(Null, EntityX(me\Collider), e\room\y + 769.0 * RoomScale, EntityZ(me\Collider), 26)
						TempLightVolume = 0.6
						RemoveNPC(e\room\NPC[0])
					ElseIf e\EventState > 70.0 * 8.6 And e\EventState < 70.0 * 10.6
						me\BigCameraShake = 10.6 - (e\EventState / 70.0)
						If Rand(2) = 1 Then SetEmitter(Null, EntityX(me\Collider), e\room\y + 769.0 * RoomScale, EntityZ(me\Collider), 26)
						TempLightVolume = 0.6
					ElseIf e\EventState >= 70.0 * 13.0 And (Not ChannelPlaying(e\SoundCHN))
						EntityTexture(e\room\RoomDoors[0]\ElevatorPanel[1], d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_IDLE])
						UpdateEntityMaterial(e\room\RoomDoors[0]\ElevatorPanel[1])
						FreeEntity(e\room\RoomDoors[0]\Buttons[1]) : e\room\RoomDoors[0]\Buttons[1] = 0
						FreeEntity(e\room\RoomDoors[0]\ElevatorPanel[0]) : e\room\RoomDoors[0]\ElevatorPanel[0] = 0
						FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
						RemoveEvent(e)
					EndIf
				Else
					If e\EventState >= 70.0 * 13.0 Then RemoveEvent(e)
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_GW_2%(e.Events)
	If e\room\Dist < 6.0
		If Rand(500) = 1
			If (Not ChannelPlaying(e\SoundCHN))
				LoadEventSound(e, "SFX\Room\SparkLong.ogg", 0)
				e\SoundCHN = PlaySoundEx(e\Sound, Camera, e\room\Objects[1], 5.0)
				SetEmitter(e\room, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True), 16)
			EndIf
		EndIf
		If e\room\NPC[0] = Null
			TFormPoint(-156.0, 51.2, 121.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_SECURITY_2_TEXTURE)
			SetNPCFrame(e\room\NPC[0], 677.0)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 225.0, 0.0, True)
		EndIf
	EndIf
	UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[1], 5.0)
	
	If PlayerRoom = e\room
		e\SoundCHN = LoopSoundEx(snd_I\AlarmSFX[1], e\SoundCHN, Camera, e\room\OBJ, 5.0)
		
		If EntityDistanceSquared(me\Collider, e\room\Objects[0]) < 9.0
			If Rand(50) = 1
				SetTemplateVelocity(ParticleEffect[19], -0.007, -0.008, -0.001, 0.0012, -0.007, 0.008)
				SetEmitter(e\room, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), 19)
				PlaySoundEx(snd_I\SparkShortSFX, Camera, e\room\Objects[0], 3.0, 0.4)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_SL%(e.Events)
	; ~ e\EventState: Determines if the player already entered the room or not (0 = No, 1 = Yes)
	
	; ~ e\EventState2: Variable used for the SCP-049 event
	
	; ~ e\EventState3: Checks if Lever is activated or not
	
	Local sc.SecurityCams
	Local Dist#, i%
	
	If PlayerRoom = e\room
		If e\EventState = 0.0
			If e\EventState2 = 0.0 Then e\EventState2 = (-70.0) * 1.5
			e\EventState = 1.0
		EndIf
	EndIf
	
	Local Skip% = (n_I\Curr049 <> Null And n_I\Curr049\State = 66.0)
	
	If (Not Skip)
		If e\EventState = 1.0
			If e\EventState2 < 0.0
				If e\EventState3 = 0.0 Then e\EventState2 = Min(e\EventState2 + fps\Factor[0], 0.0)
			ElseIf e\EventState2 = 0.0
				Local AdjDist1# = 0.0
				Local AdjDist2# = 0.0
				Local Adj1% = -1
				Local Adj2% = -1
				
				For i = 0 To MaxRoomAdjacents - 1
					If e\room\AdjDoor[i] <> Null
						If Adj1 = -1
							AdjDist1 = EntityDistanceSquared(e\room\Objects[14], e\room\AdjDoor[i]\FrameOBJ)
							Adj1 = i
						Else
							AdjDist2 = EntityDistanceSquared(e\room\Objects[14], e\room\AdjDoor[i]\FrameOBJ)
							Adj2 = i
						EndIf
					EndIf
				Next
				
				If n_I\Curr049 = Null
					If AdjDist1 > AdjDist2
						n_I\Curr049 = CreateNPC(NPCType049, EntityX(e\room\AdjDoor[Adj1]\FrameOBJ), EntityY(e\room\Objects[14], True), EntityZ(e\room\AdjDoor[Adj1]\FrameOBJ))
					Else
						n_I\Curr049 = CreateNPC(NPCType049, EntityX(e\room\AdjDoor[Adj2]\FrameOBJ), EntityY(e\room\Objects[14], True), EntityZ(e\room\AdjDoor[Adj2]\FrameOBJ))
					EndIf
					GiveAchievement("049")
				Else
					If n_I\Curr049\State <> 66.0
						If AdjDist1 > AdjDist2
							PositionEntity(n_I\Curr049\Collider, EntityX(e\room\AdjDoor[Adj1]\FrameOBJ), EntityY(e\room\Objects[14], True), EntityZ(e\room\AdjDoor[Adj1]\FrameOBJ), True)
						Else
							PositionEntity(n_I\Curr049\Collider, EntityX(e\room\AdjDoor[Adj2]\FrameOBJ), EntityY(e\room\Objects[14], True), EntityZ(e\room\AdjDoor[Adj2]\FrameOBJ), True)
						EndIf
						ResetEntity(n_I\Curr049\Collider)
					EndIf
				EndIf
				e\room\NPC[0] = n_I\Curr049
				PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
				MoveEntity(e\room\NPC[0]\Collider, 0.0, 0.0, -1.0)
				e\room\NPC[0]\HideFromNVG = False
				e\room\NPC[0]\EnemyX = EntityX(me\Collider)
				e\room\NPC[0]\EnemyZ = EntityZ(me\Collider)
				e\room\NPC[0]\State = 4.0 : e\room\NPC[0]\PrevState = 2
				
				e\EventState2 = 1.0
			ElseIf e\EventState2 = 1.0
				If e\room\NPC[0]\PathStatus <> PATH_STATUS_FOUND
					e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[15], True), EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True))
				Else
					e\EventState2 = 2.0
				EndIf
			ElseIf e\EventState2 = 2.0
				If e\room\NPC[0]\PathStatus <> PATH_STATUS_FOUND
					e\room\NPC[0]\State3 = 1.0
					e\room\NPC[0]\PathTimer = 0.0
					e\EventState2 = 3.0
				Else
					If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\RoomDoors[0]\FrameOBJ) < 25.0
						For i = 0 To 1
							e\room\RoomDoors[i]\Locked = 1
							If (Not e\room\RoomDoors[i]\Open) Then OpenCloseDoor(e\room\RoomDoors[i])
						Next
						If e\room\NPC[0]\Reload = 0.0
							PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[1]\FrameOBJ, 7.0)
							e\room\NPC[0]\DropSpeed = 0.0
							e\room\NPC[0]\Reload = 1.0
						EndIf
					EndIf
				EndIf
				
				If e\room\NPC[0]\State <> 4.0 Then e\EventState2 = 7.0
			ElseIf e\EventState2 = 3.0
				If e\room\NPC[0]\State <> 4.0 Then e\EventState2 = 7.0
				
				If e\room\NPC[0]\PathStatus <> PATH_STATUS_FOUND
					If e\room\NPC[0]\PathTimer = 0.0
						If e\room\NPC[0]\PrevState = 1
							If e\room\NPC[0]\SoundCHN2 = 0
								e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\SCP\049\Room2SL0.ogg")
								e\room\NPC[0]\SoundCHN2 = PlaySoundEx(e\room\NPC[0]\Sound2, Camera, e\room\NPC[0]\Collider, 10.0, 1.0, True)
							Else
								If (Not ChannelPlaying(e\room\NPC[0]\SoundCHN2))
									e\room\NPC[0]\PathTimer = 1.0
									e\room\NPC[0]\SoundCHN2 = 0
								EndIf
							EndIf
						ElseIf e\room\NPC[0]\PrevState = 2
							If e\room\NPC[0]\State3 = 3.0
								If e\room\NPC[0]\SoundCHN2 = 0
									e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\SCP\049\Room2SL1.ogg")
									e\room\NPC[0]\SoundCHN2 = PlaySoundEx(e\room\NPC[0]\Sound2, Camera, e\room\NPC[0]\Collider, 10.0, 1.0, True)
								Else
									If (Not ChannelPlaying(e\room\NPC[0]\SoundCHN2))
										e\room\NPC[0]\PathTimer = 1.0
										e\room\NPC[0]\SoundCHN2 = 0
									EndIf
								EndIf
							Else
								If e\room\NPC[0]\Frame >= 1118.0 Then e\room\NPC[0]\PathTimer = 1.0
							EndIf
						EndIf
					Else
						Select e\room\NPC[0]\State3
							Case 1.0
								;[Block]
								e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[16], True), EntityY(e\room\Objects[16], True), EntityZ(e\room\Objects[16], True))
								e\room\NPC[0]\PrevState = 1
								;[End Block]
							Case 2.0
								;[Block]
								e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[15], True), EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True))
								e\room\NPC[0]\PrevState = 2
								;[End Block]
							Case 3.0
								;[Block]
								e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[17], True), EntityY(e\room\Objects[17], True), EntityZ(e\room\Objects[17], True))
								e\room\NPC[0]\PrevState = 2
								;[End Block]
							Case 4.0
								;[Block]
								e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], e\room\NPC[0]\EnemyX, 0.1, e\room\NPC[0]\EnemyZ)
								e\room\NPC[0]\PrevState = 2
								;[End Block]
							Case 5.0
								;[Block]
								e\EventState2 = 5.0
								;[End Block]
						End Select
						e\room\NPC[0]\PathTimer = 0.0
						e\room\NPC[0]\State3 = e\room\NPC[0]\State3 + 1.0
					EndIf
				EndIf
			ElseIf e\EventState2 = 4.0
				If e\room\NPC[0]\State <> 4.0
					e\EventState2 = 7.0
					e\room\NPC[0]\State3 = 6.0
				EndIf
			ElseIf e\EventState2 = 5.0
				Local r.Rooms
				
				e\room\NPC[0]\State = 2.0
				For r.Rooms = Each Rooms
					If r <> PlayerRoom
						If EntityDistanceSquared(r\OBJ, e\room\NPC[0]\Collider) < 1156.0 And EntityDistanceSquared(r\OBJ, e\room\NPC[0]\Collider) > 289.0
							e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(r\OBJ), EntityY(r\OBJ), EntityZ(r\OBJ))
							e\room\NPC[0]\PathTimer = 0.0
							If e\room\NPC[0]\PathStatus = PATH_STATUS_FOUND Then e\EventState2 = 6.0
							Exit
						EndIf
					EndIf
				Next
			ElseIf e\EventState2 = 6.0
				If NPCSeesPlayer(e\room\NPC[0], 8.0 - me\CrouchState) = 1 Lor e\room\NPC[0]\State2 > 0.0 Lor e\room\NPC[0]\LastSeen > 0.0
					e\EventState2 = 7.0
				Else
					; ~ Still playing the Music for SCP-049 (in the real, SCP-049's State will be set to 2, causing it to stop playing the chasing track)
					If PlayerRoom = e\room Then ShouldPlay = 19
					If e\room\NPC[0]\CurrentRoom <> PlayerRoom Lor EntityY(e\room\NPC[0]\Collider) < 0.0
						e\room\NPC[0]\Idle = 70.0 * 60.0 ; ~ Making SCP-049 idle for one minute (twice as fast for AggressiveNPCs = True)
						PositionEntity(e\room\NPC[0]\Collider, 0.0, -500.0, 0.0)
						ResetEntity(e\room\NPC[0]\Collider)
						e\EventState2 = 7.0
					EndIf
				EndIf
			EndIf
			
			If e\room\NPC[0] <> Null
				If PlayerRoom = e\room
					If e\EventState2 < 7.0
						If e\EventState2 > 2.0
							If (Not IsEqual(EntityY(e\room\RoomDoors[0]\FrameOBJ), EntityY(e\room\NPC[0]\Collider), 1.0))
								If IsEqual(EntityY(e\room\RoomDoors[0]\FrameOBJ), EntityY(me\Collider, True), 1.0)
									If e\room\RoomDoors[0]\Open
										If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
											If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
											e\SoundCHN = StreamSound_Strict("SFX\SCP\079\Stairs.ogg", opt\VoiceVolume * opt\MasterVolume)
											e\SoundCHN_IsStream = True
										EndIf
										PlaySoundEx(snd_I\DoorClose079, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
										e\room\NPC[0]\State3 = 4.0
										e\room\RoomDoors[0]\FastOpen = True
										OpenCloseDoor(e\room\RoomDoors[0])
									EndIf
								EndIf
							Else
								If (Not e\room\RoomDoors[0]\Open)
									PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
									e\room\RoomDoors[0]\FastOpen = False
									OpenCloseDoor(e\room\RoomDoors[0])
								EndIf
							EndIf
						EndIf
					Else
						If (Not e\room\RoomDoors[0]\Open)
							e\room\RoomDoors[0]\FastOpen = False
							OpenCloseDoor(e\room\RoomDoors[0])
							PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	If PlayerRoom = e\room
		; ~ Lever for checkpoint locking (might have a function in the future for the case if the checkpoint needs to be locked again)
		e\EventState3 = UpdateLever(e\room\RoomLevers[0]\OBJ, (Not e\EventState3))
		If e\EventState3 = 0.0
			TurnCheckpointMonitorsOff()
			EntityTexture(e\room\Objects[18], e\room\Textures[0], 0)
		Else
			UpdateCheckpointMonitors()
			EntityTexture(e\room\Objects[18], e\room\Textures[0], 1 + (mon_I\MonitorTimer[0] < 50.0))
		EndIf
		
		; ~ Checking if the monitors and such should be rendered or not
		If (Not IsEqual(EntityY(e\room\RoomDoors[0]\FrameOBJ), EntityY(me\Collider, True), 1.0))
			For i = 0 To 13
				If e\room\Objects[i] <> 0
					If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
				EndIf
			Next
			For sc.SecurityCams = Each SecurityCams
				If sc\room = e\room
					If EntityHidden(sc\MonitorOBJ) Then ShowEntity(sc\MonitorOBJ)
					Exit
				EndIf
			Next
			
			UpdateRedLight(e\room\RoomLights[0], 1500, 800)
			
			Local e2.Events
			
			If (MilliSec Mod 1500) < 10 Then e\EventState4 = (e\EventState4 + 1.0) Mod 6
			For i = 20 To 22
				EntityTexture(e\room\Objects[i], e\room\Textures[2], e\EventState4)
			Next
			
			For e2.Events = Each Events
				If e2\EventID = e_cont2_008
					If e2\EventState = 2.0
						mon_I\UpdateCheckpoint[1] = False
						EntityTexture(e\room\Objects[19], e\room\Textures[2], e\EventState4)
					Else
						mon_I\UpdateCheckpoint[1] = True ; ~ Used to update the timer only
						EntityTexture(e\room\Objects[19], e\room\Textures[1], 6 + (mon_I\MonitorTimer[1] < 50.0))
					EndIf
					Exit
				EndIf
			Next
		Else
			For i = 0 To 13
				If e\room\Objects[i] <> 0
					If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
				EndIf
			Next
			For sc.SecurityCams = Each SecurityCams
				If sc\room = e\room
					If (Not EntityHidden(sc\MonitorOBJ)) Then HideEntity(sc\MonitorOBJ)
					Exit
				EndIf
			Next
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Storage%(e.Events)
	Local x#, y#, z#
	
	If PlayerRoom = e\room
		Local n.NPCs, it.Items, it2.Items, de.Decals
		Local i%, j%, k%
		
		If e\EventState2 <= 0.0
			e\room\RoomDoors[1]\Locked = 0
			e\room\RoomDoors[4]\Locked = 0
			
			If EntityDistanceSquared(e\room\OBJ, n_I\Curr173\Collider) < 64.0 Lor EntityDistanceSquared(e\room\OBJ, n_I\Curr106\Collider) < 64.0
				e\room\RoomDoors[1]\Locked = 1
				e\room\RoomDoors[1]\Open = False
				e\room\RoomDoors[4]\Locked = 1
				e\room\RoomDoors[4]\Open = False
			EndIf
			For n.NPCs = Each NPCs
				If n\NPCType = NPCTypeMTF
					If EntityDistanceSquared(n\Collider, e\room\OBJ) < 36.0
						e\room\RoomDoors[1]\Locked = 1
						e\room\RoomDoors[1]\Open = False
						e\room\RoomDoors[4]\Locked = 1
						e\room\RoomDoors[4]\Open = False
						Exit
					EndIf
				EndIf
			Next
			e\EventState2 = 70.0 * 2.5
		Else
			e\EventState2 = e\EventState2 - fps\Factor[0]
		EndIf
		
		TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, e\room\OBJ)
		
		If e\room\RoomDoors[1]\OpenState = 0.0
			If (Not EntityHidden(e\room\Objects[0])) Then HideEntity(e\room\Objects[0])
			HideEntity(e\room\RoomDoors[0]\OBJ)
			HideEntity(e\room\RoomDoors[0]\Buttons[1])
			HideEntity(e\room\RoomDoors[0]\FrameOBJ)
		Else
			If EntityHidden(e\room\Objects[0]) Then ShowEntity(e\room\Objects[0])
			ShowEntity(e\room\RoomDoors[0]\OBJ)
			ShowEntity(e\room\RoomDoors[0]\Buttons[1])
			ShowEntity(e\room\RoomDoors[0]\FrameOBJ)
		EndIf
		If e\room\RoomDoors[4]\OpenState = 0.0
			If (Not EntityHidden(e\room\Objects[1])) Then HideEntity(e\room\Objects[1])
			HideEntity(e\room\RoomDoors[5]\OBJ)
			HideEntity(e\room\RoomDoors[5]\Buttons[1])
			HideEntity(e\room\RoomDoors[5]\FrameOBJ)
		Else
			If EntityHidden(e\room\Objects[1]) Then ShowEntity(e\room\Objects[1])
			ShowEntity(e\room\RoomDoors[5]\OBJ)
			ShowEntity(e\room\RoomDoors[5]\Buttons[1])
			ShowEntity(e\room\RoomDoors[5]\FrameOBJ)
		EndIf
		
		If e\EventState < 8.0
			If (Not EntityHidden(e\room\Objects[2])) Then HideEntity(e\room\Objects[2])
		Else
			If EntityHidden(e\room\Objects[2]) Then ShowEntity(e\room\Objects[2])
		EndIf
		
		Local src.Doors, dest.Doors
		Local Temp% = False
		
		If TFormedX() > 730.0
			GiveAchievement("970")
			
			me\BlinkTimer = -10.0
			UpdateWorld()
			TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, e\room\OBJ)
			
			For i = 1 To 2
				src.Doors = e\room\RoomDoors[i + 2]
				dest.Doors = e\room\RoomDoors[i]
				
				dest\Open = src\Open
				dest\OpenState = src\OpenState
				EntityParent(dest\OBJ, dest\FrameOBJ) : EntityParent(src\OBJ, src\FrameOBJ)
				EntityParent(dest\OBJ2, dest\FrameOBJ) : EntityParent(src\OBJ2, src\FrameOBJ)
				
				PositionEntity(dest\OBJ, EntityX(src\OBJ), EntityY(src\OBJ), EntityZ(src\OBJ))
				PositionEntity(dest\OBJ2, EntityX(src\OBJ2), EntityY(src\OBJ2), EntityZ(src\OBJ2))
				
				EntityParent(dest\OBJ, 0) : EntityParent(src\OBJ, 0)
				EntityParent(dest\OBJ2, 0) : EntityParent(src\OBJ2, 0)
				
				src\Open = False
				src\OpenState = 0.0
			Next
			
			TFormPoint(TFormedX() - 1024.0, TFormedY(), TFormedZ(), e\room\OBJ, 0)
			PositionEntity(me\Collider, TFormedX(), EntityY(me\Collider), TFormedZ(), True)
			ResetEntity(me\Collider)
			
			ResetRender()
			
			Temp = True
		ElseIf TFormedX() < -730.0
			GiveAchievement("970")
			
			me\BlinkTimer = -10.0
			UpdateWorld()
			TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, e\room\OBJ)
			
			For i = 1 To 2
				src.Doors = e\room\RoomDoors[i]
				dest.Doors = e\room\RoomDoors[i + 2]
				
				dest\Open = src\Open
				dest\OpenState = src\OpenState
				EntityParent(dest\OBJ, dest\FrameOBJ) : EntityParent(src\OBJ, src\FrameOBJ)
				EntityParent(dest\OBJ2, dest\FrameOBJ) : EntityParent(src\OBJ2, src\FrameOBJ)
				
				PositionEntity(dest\OBJ, EntityX(src\OBJ), EntityY(src\OBJ), EntityZ(src\OBJ))
				PositionEntity(dest\OBJ2, EntityX(src\OBJ2), EntityY(src\OBJ2), EntityZ(src\OBJ2))
				
				EntityParent(dest\OBJ, 0) : EntityParent(src\OBJ, 0)
				EntityParent(dest\OBJ2, 0) : EntityParent(src\OBJ2, 0)
				
				src\Open = False
				src\OpenState = 0.0
			Next
			
			TFormPoint(TFormedX() + 1024.0, TFormedY(), TFormedZ(), e\room\OBJ, 0)
			PositionEntity(me\Collider, TFormedX(), EntityY(me\Collider), TFormedZ(), True)
			ResetEntity(me\Collider)
			
			ResetRender()
			
			Temp = True
		EndIf
		
		If Temp
			e\EventState = e\EventState + 1.0
			For it.Items = Each Items
				If EntityDistanceSquared(it\Collider, me\Collider) < 25.0
					TFormPoint(EntityX(it\Collider), EntityY(it\Collider), EntityZ(it\Collider), 0, e\room\OBJ)
					x = TFormedX() : y = TFormedY() : z = TFormedZ()
					If z >= -290.0 And z <= 560.0
						If x >= 264.0 And x <= 760.0
							TFormPoint(x - 1024.0, y, z, e\room\OBJ, 0)
							PositionEntity(it\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
							ResetEntity(it\Collider)
						ElseIf x <= -264.0 And x >= -760.0
							TFormPoint(x + 1024.0, y, z, e\room\OBJ, 0)
							PositionEntity(it\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
							ResetEntity(it\Collider)
						EndIf
					EndIf
				EndIf
			Next
			
			Select e\EventState
				Case 1.0
					;[Block]
					If KEY2_SPAWNRATE = 5
						TFormPoint(-354.0,  220.0, 516.0, e\room\OBJ, 0)
						CreateItem("White Key", it_key_white, TFormedX(), TFormedY(), TFormedZ())
					EndIf
					;[End Block]
				Case 2.0
					;[Block]
					i = Rand(0, MaxItemAmount - 1)
					If Inventory(i) <> Null
						CreateMsg(GetLocalString("msg", "970.item.missing"))
						RemoveWearableItems(Inventory(i))
						RemoveItem(Inventory(i))
					EndIf
					;[End Block]
				Case 5.0
					;[Block]
					me\Injuries = me\Injuries + 0.3
					CreateMsg(GetLocalString("msg", "970.sharp"))
					;[End Block]
				Case 10.0
					;[Block]
					CreateDecal(DECAL_BLOOD_2, e\room\x + Cos(e\room\Angle - 90.0) * 760.0 * RoomScale, e\room\y + 0.005, e\room\z + Sin(e\room\Angle - 90.0) * 760.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
					;[End Block]
				Case 14.0
					;[Block]
					k = False
					For i = 0 To MaxItemAmount - 1
						If Inventory(i) <> Null
							If Inventory(i)\ItemTemplate\ID = it_paper
								RemoveItem(Inventory(i))
								k = True
								Exit
							EndIf
						EndIf
					Next
					If k
						it.Items = CreateItem(GetRandDocument(), it_paper, 0.0, 0.0, 0.0)
						PickItem(it, False)
					EndIf
					;[End Block]
				Case 18.0
					;[Block]
					TFormPoint(-344.0, 176.0, 272.0, e\room\OBJ, 0)
					CreateItem("Strange Note", it_paper, TFormedX(), TFormedY(), TFormedZ())
					;[End Block]
				Case 25.0
					;[Block]
					e\room\NPC[0] = CreateNPC(NPCTypeD, e\room\x + Cos(e\room\Angle - 90.0) * 760.0 * RoomScale, e\room\y + 0.2, e\room\z + Sin(e\room\Angle - 90.0) * 760.0 * RoomScale)
					e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
					RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle - 200.0, 0.0, True)
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_BENJAMIN_TEXTURE)
					SetNPCFrame(e\room\NPC[0], 80.0)
					;[End Block]
				Case 30.0
					;[Block]
					k = False
					For i = 0 To MaxItemAmount - 1
						If Inventory(i) <> Null
							RemoveWearableItems(Inventory(i))
							RemoveItem(Inventory(i))
							k = True
							Exit
						EndIf
					Next
					If k
						it.Items = CreateItem("Strange Note", it_paper, 0.0, 0.0, 0.0)
						PickItem(it, False)
					EndIf
					;[End Block]
				Case 33.0, 34.0, 35.0
					;[Block]
					de.Decals = CreateDecal(DECAL_BLOOD_6, e\room\x + Rnd(-2.0, 2.0), e\room\y + 700.0 * RoomScale, e\room\z + Rnd(-2.0, 2.0), 270.0, Rnd(360.0), 0.0, 0.05)
					de\SizeChange = 0.0005
					;[End Block]
				Case 40.0
					;[Block]
					e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin3.ogg"), True)
					;[End Block]
				Case 50.0
					;[Block]
					e\room\NPC[1] = CreateNPC(NPCTypeGuard, e\room\x + Cos(e\room\Angle + 90.0) * 600.0 * RoomScale, 0.35, e\room\z + Sin(e\room\Angle + 90.0) * 600.0 * RoomScale)
					e\room\NPC[1]\State = 7.0
					;[End Block]
				Case 52.0
					;[Block]
					RemoveNPC(e\room\NPC[1])
					;[End block]
				Case 60.0
					;[Block]
					If (Not n_I\IsHalloween)
						Local Tex% = LoadTexture_Strict("GFX\NPCs\scp_173_H.png")
						
						EntityTexture(n_I\Curr173\OBJ, Tex)
						EntityTexture(n_I\Curr173\OBJ2, Tex)
						DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
						n_I\IsHalloween = True
					EndIf
					;[End Block]
			End Select
			
			If Rand(10) = 1
				i = Rand(0, 2)
				PlaySound_Strict(AmbientSFX(i, Rand(0, AmbientSFXAmount[i] - 1)))
			EndIf
		Else
			If e\room\NPC[0] <> Null
				If EntityDistanceSquared(me\Collider, e\room\NPC[0]\Collider) < 9.0
					If EntityInView(e\room\NPC[0]\OBJ, Camera)
						me\CurrCameraZoom = (Sin(Float(MilliSec) / 20.0) + 1.0) * 15.0
						me\HeartBeatVolume = Max(CurveValue(0.3, me\HeartBeatVolume, 2.0), me\HeartBeatVolume)
						me\HeartBeatRate = Max(me\HeartBeatRate, 120.0)
					EndIf
				EndIf
			EndIf
			
			If e\room\NPC[1] <> Null
				PointEntity(e\room\NPC[1]\OBJ, me\Collider)
				RotateEntity(e\room\NPC[1]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[1]\OBJ), EntityYaw(e\room\NPC[1]\Collider), 35.0), 0.0)
			EndIf
			
			For it.Items = Each Items
				If it\Dropped <> 0
					TFormPoint(EntityX(it\Collider), EntityY(it\Collider), EntityZ(it\Collider), 0, e\room\OBJ)
					x = TFormedX() : y = TFormedY() : z = TFormedZ()
					
					If it\Dropped = 1 And Abs(x) < 264.0
						For i = -1 To 1 Step 2
							TFormPoint(x + (1024.0 * i), y, z, e\room\OBJ, 0)
							it2.Items = CreateItem(it\ItemTemplate\Name, it\ItemTemplate\ID, TFormedX(), EntityY(it\Collider), TFormedZ(), it\R, it\G, it\B, it\Alpha)
							it2\Name = it\Name
							it2\State = it\State : it2\State2 = it\State2 : it2\State3 = it\State3
							If it\InvSlots > 0
								it2\InvSlots = it\InvSlots
								For j = 0 To it\InvSlots - 1
									it2\SecondInv[j] = it\SecondInv[j]
								Next
								SetAnimTime(it2\OBJ, AnimTime(it\OBJ))
								it2\InvImg = it\InvImg
							EndIf
							RotateEntity(it2\Collider, EntityPitch(it\Collider), EntityYaw(it\Collider), 0.0)
						Next
					ElseIf it\Dropped = -1
						For it2.Items = Each Items
							If it2 <> it And it2\Dist < 225.0 And it2\ItemTemplate = it\ItemTemplate
								TFormPoint(EntityX(it2\Collider), EntityY(it2\Collider), EntityZ(it2\Collider), 0, e\room\OBJ)
								If TFormedZ() = z Then RemoveItem(it2)
							EndIf
						Next
					EndIf
					Exit
				EndIf
			Next
		EndIf
	Else
		If (Not EntityHidden(e\room\Objects[0])) Then HideEntity(e\room\Objects[0])
		If (Not EntityHidden(e\room\Objects[1])) Then HideEntity(e\room\Objects[1])
		If (Not EntityHidden(e\room\Objects[2])) Then HideEntity(e\room\Objects[2])
	EndIf
	
	If e\EventState > 25.0
		If e\room\Dist < 8.0
			If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\SCP\970\Corpse.ogg")
			e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\OBJ)
			If e\EventState > 60.0
				AnimateNPC(e\room\NPC[0], 80.0, 61.0, -0.02, False)
				e\room\NPC[0]\DropSpeed = 0.0
				y = CurveValue(1.5 + Sin(Float(MilliSec) / 20.0) * 0.1, EntityY(e\room\NPC[0]\Collider), 50.0)
				PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\NPC[0]\Collider), y, EntityZ(e\room\NPC[0]\Collider))
				TurnEntity(e\room\NPC[0]\Collider, 0.0, fps\Factor[0] * 0.1, 0.0)
				If e\room\NPC[0]\Frame < 61.1
					If e\room\RoomEmitters[0] = Null
						e\room\RoomEmitters[0] = SetEmitter(Null, EntityX(e\room\NPC[0]\Collider), EntityY(e\room\NPC[0]\Collider) + 0.3, EntityZ(e\room\NPC[0]\Collider), 27)
						EntityParent(e\room\RoomEmitters[0]\Owner, e\room\NPC[0]\Collider)
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Test_LCZ_173%(e.Events)
	If PlayerRoom = e\room
		Local de.Decals
		
		If n_I\Curr173\Idle = 0
			If e\EventState = 0.0
				If e\room\RoomDoors[0]\Open
					TeleportEntity(n_I\Curr173\Collider, EntityX(e\room\Objects[0], True), e\room\y + 0.3, EntityZ(e\room\Objects[0], True), n_I\Curr173\CollRadius + 0.12, True)
					n_I\Curr173\CurrentRoom = e\room
					e\EventState = 1.0
				EndIf
			Else
				If EntityHidden(e\room\Objects[2]) Then ShowEntity(e\room\Objects[2])
				; ~ Start a timer for SCP-173 breaking through the window
				e\EventState = e\EventState + fps\Factor[0]
				
				; ~ If close, allow SCP-173 to break glass now
				If EntityDistanceSquared(me\Collider, e\room\Objects[1]) < 0.64 Then e\EventState = Max(e\EventState, 70.0 * 12.0)
				
				If e\EventState > 70.0 * 12.0
					; ~ If the player moves a bit further and blinks, SCP-173 attacks
					If (Not PlayerSees173(n_I\Curr173)) And ((Not EntityInView(e\room\Objects[2], Camera)) Lor (me\BlinkTimer < -6.0 And me\BlinkTimer > -16.0))
						If EntityDistanceSquared(n_I\Curr173\Collider, e\room\Objects[0]) > 25.0
							; ~ Remove event, if SCP-173 is far away from the room (perhaps because the player left and SCP-173 moved to some other room?) 
							RemoveEvent(e)
						Else
							TFormPoint(-801.0, 240.0, -206.95, e\room\OBJ, 0)
							CreateDecal(DECAL_CRACKED_GLASS, TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle + 180.0, 0.0, 0.2, 0.5)
							
							PlaySoundEx(LoadTempSound("SFX\Room\GlassBreak.ogg"), Camera, n_I\Curr173\OBJ) 
							HideEntity(e\room\Objects[2])
							TFormPoint(-669.0, 128.0, -64.0, e\room\OBJ, 0)
							PositionEntity(n_I\Curr173\Collider, TFormedX(), TFormedY(), TFormedZ())
							ResetEntity(n_I\Curr173\Collider)
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont2_012%(e.Events)
	If PlayerRoom = e\room
		e\room\RoomDoors[0]\Locked = RemoteDoorOn
		If EntityY(me\Collider) < 0.0
			If (Not me\Terminated)
				If e\EventState = 0.0
					If RemoteDoorOn
						If EntityDistanceSquared(me\Collider, e\room\RoomDoors[0]\OBJ) < 6.25
							If me\FallTimer >= 0.0
								PlaySound_Strict(snd_I\HorrorSFX[7])
								
								If (Not e\room\RoomDoors[0]\Open) Then OpenCloseDoor(e\room\RoomDoors[0])
								
								Local n.NPCs
								Local i%
								
								TFormPoint(-784.0, -768.0, 640.0, e\room\OBJ, 0)
								
								Local x# = TFormedX(), y# = TFormedY(), z# = TFormedZ()
								
								For i = 0 To 1
									n.NPCs = CreateNPC(NPCTypeCockroach, x, y + 0.05, z)
									RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
								Next
								e\EventState = 1.0
							EndIf
						EndIf
					Else
						e\EventState = 1.01
					EndIf
				ElseIf e\EventState = 1.0
					RotateEntity(e\room\RoomLevers[0]\OBJ, CurveAngle(-80.0, EntityPitch(e\room\RoomLevers[0]\OBJ), 10.0), EntityYaw(e\room\RoomLevers[0]\OBJ), 0.0)
					If EntityPitch(e\room\RoomLevers[0]\OBJ) < -79.0
						PlaySoundEx(snd_I\LeverSFX, Camera, e\room\RoomLevers[0]\OBJ, 2.0)
						e\EventState4 = 1.0
						e\EventState = 1.01
					EndIf
				Else
					Local NotProtected% = (I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4)
					
					If e\EventState4 = 1.0
						GiveAchievement("012")
						e\EventState4 = UpdateLever(e\room\RoomLevers[0]\OBJ, NotProtected)
						e\EventState = CurveValue(90.0, e\EventState, 500.0)
						
						UpdateRedLight(e\room\RoomLights[0], 1500, 800)
						
						If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Music\012Golgotha.ogg")
						e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\Objects[0])
						
						If NotProtected
							If me\Sanity < -800.0 And e\EventState3 = 0.0
								TFormPoint(-360.0, -700.0, 330.0, e\room\OBJ, 0)
								PositionEntity(me\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
								RotateEntity(me\Collider, 0.0, e\room\Angle, 0.0, True)
								ResetEntity(me\Collider)
								me\LightBlink = 5.0
								me\BlinkTimer = -10.0
								e\EventState3 = 1.0
								If n_I\Curr106\State = 3.0 Then n_I\Curr106\State = 0.0
							EndIf
							
							Local Pvt%, Angle#
							
							If EntityVisible(e\room\Objects[0], Camera)
								CanSave = 0
								
								me\RestoreSanity = False
								
								ShouldPlay = 32
								
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(Camera), EntityY(e\room\Objects[0], True) - 0.05, EntityZ(Camera))
								PointEntity(Pvt, e\room\Objects[0])
								RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), 80.0 - (e\EventState2 / 200.0)), 0.0)
								
								TurnEntity(Pvt, 90.0, 0.0, 0.0)
								CameraPitch = CurveAngle(EntityPitch(Pvt) + 25.0, CameraPitch + 90.0, 80.0 - (e\EventState2 / 200.0))
								CameraPitch = CameraPitch - 90.0
								
								Local Dist# = DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[0], True), EntityZ(me\Collider), EntityZ(e\room\Objects[0], True))
								Local SqrValue# = Sqr(Dist)
								
								me\HeartBeatRate = 150.0
								me\HeartBeatVolume = Max(3.0 - SqrValue, 0.0) / 3.0
								
								Local SinValue# = Sin(Float(MilliSec) / 20.0) + 1.0
								Local de.Decals
								
								me\BlurVolume = Max((2.0 - SqrValue) * (e\EventState2 / 800.0) * SinValue, me\BlurVolume)
								me\CurrCameraZoom = Max(me\CurrCameraZoom, SinValue * 8.0 * Max((3.0 - SqrValue), 0.0))
								
								StopBreathSound()
								
								If Dist < 0.36
									Local Tex%
									
									If me\Sanity < -800.0 Then MakeMeUnplayable(False)
									me\Sanity = Max(me\Sanity - (fps\Factor[0] * (1.0 + (0.2 * SelectedDifficulty\OtherFactors)) / (1.0 + I_714\Using)), -1000.0)
									If e\EventState3 = 1.0 Then e\EventState2 = Min(e\EventState2 + fps\Factor[0], 70.0 * 86.0)
									If e\EventState2 > 70.0 And e\EventState2 - fps\Factor[0] <= 70.0
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech0.ogg"), True)
									ElseIf e\EventState2 > 70.0 * 13.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 13.0
										CreateMsg(GetLocalString("msg", "012_1"), 8.0)
										me\Injuries = me\Injuries + 0.5
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech1.ogg"), True)
									ElseIf e\EventState2 > 70.0 * 31.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 31.0
										Tex = LoadTexture_Strict("GFX\Map\Textures\scp_012(2).png")
										EntityTexture(e\room\Objects[2], Tex)
										DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
										
										CreateMsg(GetLocalString("msg", "012_2"), 8.0)
										me\Injuries = Max(me\Injuries, 1.5)
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech" + Rand(2, 3) + ".ogg"), True)
									ElseIf e\EventState2 > 70.0 * 49.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 49.0
										CreateMsg(GetLocalString("msg", "012_3"), 8.0)
										me\Injuries = me\Injuries + 0.3
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech4.ogg"), True)
									ElseIf e\EventState2 > 70.0 * 63.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 63.0
										Tex = LoadTexture_Strict("GFX\Map\Textures\scp_012(3).png")
										EntityTexture(e\room\Objects[2], Tex)
										DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
										
										me\Injuries = me\Injuries + 0.5
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech5.ogg"), True)
									ElseIf e\EventState2 > 70.0 * 74.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 74.0
										Tex = LoadTexture_Strict("GFX\Map\Textures\scp_012(4).png")
										EntityTexture(e\room\Objects[2], Tex)
										DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
										
										CreateMsg(GetLocalString("msg", "012_4"), 8.0)
										me\CameraShake = 2.0 * (I_1025\FineState[4] = 0.0)
										me\Injuries = me\Injuries + 0.8
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech6.ogg"), True)
										PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\BodyHorrorExchange" + Rand(0, 3) + ".ogg"))
										If (Not me\Crouch) Then SetCrouch(True)
										
										de.Decals = CreateDecal(DECAL_BLOOD_6, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, 0.1)
										de\MaxSize = 0.45 : de\SizeChange = 0.0002
									ElseIf e\EventState2 > 70.0 * 85.0 And e\EventState2 - fps\Factor[0] <= 70.0 * 85.0
										msg\DeathMsg = Format(GetLocalString("death", "012"), SubjectName)
										Kill(True)
									EndIf
									
									RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(me\Collider) + Sin(e\EventState2 * (e\EventState2 / 2000.0)) * (e\EventState2 / 300.0), EntityYaw(me\Collider), 80.0), 0.0)
								Else
									me\Sanity = Max(me\Sanity - (fps\Factor[0] * (0.6 + (0.12 * SelectedDifficulty\OtherFactors)) / (1.0 + I_714\Using)), -1000.0)
									Angle = WrapAngle(EntityYaw(Pvt) - EntityYaw(me\Collider))
									If Angle < 40.0
										me\ForceMove = (40.0 - Angle) * 0.02
									ElseIf Angle > 310.0
										me\ForceMove = (40.0 - Abs(360.0 - Angle)) * 0.02
									EndIf
								EndIf
								
								FreeEntity(Pvt) : Pvt = 0
							ElseIf DistanceSquared(EntityX(me\Collider), EntityX(e\room\RoomDoors[0]\FrameOBJ), EntityZ(me\Collider), EntityZ(e\room\RoomDoors[0]\FrameOBJ)) < 25.0 And EntityY(me\Collider) < -2.5
								If e\room\RoomDoors[0]\Open
									CanSave = 0
									
									me\Sanity = Max(me\Sanity - (fps\Factor[0] * (0.4 + (0.08 * SelectedDifficulty\OtherFactors)) / (1.0 + I_714\Using)), -1000.0)
									me\RestoreSanity = False
									
									Pvt = CreatePivot()
									PositionEntity(Pvt, EntityX(Camera), EntityY(me\Collider), EntityZ(Camera))
									PointEntity(Pvt, e\room\RoomDoors[0]\FrameOBJ)
									CameraPitch = CurveAngle(90.0, CameraPitch + 90.0, 100.0)
									CameraPitch = CameraPitch - 90.0
									RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), 150.0), 0.0)
									
									Angle = WrapAngle(EntityYaw(Pvt) - EntityYaw(me\Collider))
									If Angle < 40.0
										me\ForceMove = (40.0 - Angle) * 0.008
									ElseIf Angle > 310.0
										me\ForceMove = (40.0 - Abs(360.0 - Angle)) * 0.008
									EndIf
									FreeEntity(Pvt) : Pvt = 0
								EndIf
							EndIf
						EndIf
					Else
						e\EventState4 = UpdateLever(e\room\RoomLevers[0]\OBJ)
						If (Not EntityHidden(e\room\RoomLights[0]\OBJ)) Then HideEntity(e\room\RoomLights[0]\OBJ)
						e\EventState = CurveValue(-90.0, e\EventState, 500.0)
					EndIf
					PositionEntity(e\room\Objects[0], EntityX(e\room\Objects[0], True), e\room\y + Min(((-570.0 * Sin(e\EventState))) * RoomScale, -180.0 * RoomScale), EntityZ(e\room\Objects[0], True), True)
				EndIf
			EndIf
		Else
			If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
			If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
			If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont2_500_1499%(e.Events)
	If e\EventState = 0.0
		If IsRoomAdjacent(PlayerRoom, e\room)
			If (Not n_I\Curr106\Contained)
				If n_I\Curr106\State < 2.0
					e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Character\Scientist\EmilyScream.ogg"), True)
					
					CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\Objects[0], True), e\room\y + 0.005, EntityZ(e\room\Objects[0], True), 90.0, Rnd(360.0), 0.0, 0.8, 0.8)
					
					AffectDecayDoor(e\room\RoomDoors[0])
					
					e\EventState = 1.0
				EndIf
			Else
				e\room\RoomDoors[0]\Locked = 0
				RemoveEvent(e)
				Return
			EndIf
		EndIf
	ElseIf e\EventState = 1.0
		If (Not ChannelPlaying(e\SoundCHN))
			e\room\RoomDoors[0]\Locked = 0
			RemoveEvent(e)
		Else
			UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 30.0, 1.0, True)
		EndIf
	EndIf
End Function

Global skull_event_leaflet.Items = Null

Function UpdateEvent_Cont2_1123%(e.Events)
	If PlayerRoom = e\room
		; ~ The event is started when the player picks up SCP-1123 (in Items.bb/UpdateItems())
		If e\EventState > 0.0
			CanSave = 0
			If I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4
				me\BlurTimer = 800.0 - (200.0 * (I_714\Using = 1))
				me\CameraShake = 1.0 - (0.5 * (I_714\Using = 1))
				I_1123\SoundCHN = LoopSoundLocal(I_1123\Sound, I_1123\SoundCHN)
			EndIf
		EndIf
		
		Local de.Decals, d.Doors
		Local i%, j%, y#
		
		Select e\EventState
			Case 1.0
				;[Block]
				; ~ Saving me\Injuries and me\Bloodloss, so that the player won't be healed automatically
				me\PrevInjuries = me\Injuries
				me\PrevBloodloss = me\Bloodloss
				PrevIsBlackOut = IsBlackOut : IsBlackOut = False
				
				y = e\room\y + 4865.0 * RoomScale
				e\room\RoomDoors[0]\Open = False
				ChangeDoorYPositon(e\room\RoomDoors[0], y)
				ChangeDoorYPositon(e\room\RoomDoors[1], y)
				ChangeDoorYPositon(e\room\RoomDoors[2], y)
				e\room\RoomDoors[3]\Open = False
				e\room\RoomDoors[4]\Open = False
				e\room\RoomDoors[4]\Locked = 0
				e\room\RoomDoors[5]\Open = False
				e\room\RoomDoors[5]\Locked = 0
				e\room\RoomDoors[6]\Open = False
				e\room\RoomDoors[7]\Open = False
				
				e\room\Objects[7] = LoadRMesh(RoomPartsPath + "cont2_1123_cell.rmesh", Null)
				ScaleEntity(e\room\Objects[7], RoomScale, RoomScale, RoomScale)
				PositionEntity(e\room\Objects[7], e\room\x, e\room\y, e\room\z)
				RotateEntity(e\room\Objects[7], 0.0, e\room\Angle, 0.0)
				EntityParent(e\room\Objects[7], e\room\OBJ)
				
				TFormPoint(-139.0, 5006.0, 655.0, e\room\OBJ, 0)
				e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[0]\State = -1.0
				FreeEntity(e\room\NPC[0]\OBJ) : e\room\NPC[0]\OBJ = 0
				e\room\NPC[0]\OBJ = LoadAnimMesh_Strict("GFX\NPCs\nazi_officer.b3d")
				
				Local Scale# = 0.51 / MeshWidth(e\room\NPC[0]\OBJ)
				
				ScaleEntity(e\room\NPC[0]\OBJ, Scale, Scale, Scale)
				
				PositionEntity(me\Collider, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), True)
				ResetEntity(me\Collider)
				
				For i = 0 To MaxItemAmount - 1
					If Inventory(i) <> Null Then RemoveWearableItems(Inventory(i))
				Next
				ChangePlayerBodyTexture(PLAYER_BODY_PRISONER_TEX)
				
				TFormPoint(-756.0, 5016.0, 521.0, e\room\OBJ, 0)
				skull_event_leaflet = CreateItem("Leaflet", it_paper, TFormedX(), TFormedY(), TFormedZ())
				
				me\CameraShake = 1.0
				me\BlurTimer = 1200.0
				me\Injuries = 1.0
				
				e\EventState = 2.0
				;[End Block]
			Case 2.0
				;[Block]
				e\EventState2 = e\EventState2 + fps\Factor[0]
				
				If ClosestItem = skull_event_leaflet And mo\MouseHit1
					PickItem(skull_event_leaflet)
					SelectedItem = skull_event_leaflet
				EndIf
				
				PointEntity(e\room\NPC[0]\Collider, me\Collider)
				me\BlurTimer = Max(me\BlurTimer, 100.0)
				
				ShouldPlay = 29
				
				If e\EventState2 > 1000.0
					If (Not e\room\RoomDoors[3]\Open) Then OpenCloseDoor(e\room\RoomDoors[3])
					
					If e\EventState2 >= 1040.0 And e\EventState2 - fps\Factor[0] < 1040.0 
						e\room\NPC[0]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\1123\Officer0.ogg"), Camera, e\room\NPC[0]\OBJ, 10.0, 1.0, True)
					ElseIf e\EventState2 >= 1400.0 And e\EventState2 - fps\Factor[0] < 1400.0
						e\room\NPC[0]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\1123\Officer1.ogg"), Camera, e\room\NPC[0]\OBJ, 10.0, 1.0, True)
					EndIf
					AnimateNPC(e\room\NPC[0], 3.0, 26.0, 0.2)
					If EntityDistanceSquared(me\Collider, e\room\Objects[0]) > PowTwo(392.0 * RoomScale)
						me\BlinkTimer = -10.0
						me\LightBlink = 2.0
						me\BlurTimer = 500.0
						TFormPoint(828.0, 4946.0, 592.0, e\room\OBJ, 0)
						PositionEntity(me\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
						RotateEntity(me\Collider, 0.0, EntityYaw(e\room\OBJ, True), 0.0)
						ResetEntity(me\Collider)
						
						ChangePlayerBodyTexture(PLAYER_BODY_NORMAL_TEX)
						
						RemoveItem(skull_event_leaflet) : skull_event_leaflet = Null
						
						e\EventState = 3.0
					EndIf
				EndIf
				;[End Block]
			Case 3.0
				;[Block]
				If Rand(150) = 1 Then OpenCloseDoor(e\room\RoomDoors[1])
				If e\room\RoomDoors[0]\Open
					TFormPoint(-706.0, 5006.0, -845.0, e\room\OBJ, 0)
					PositionEntity(e\room\NPC[0]\Collider, TFormedX(), TFormedY(), TFormedZ())
					ResetEntity(e\room\NPC[0]\Collider)
					
					me\BlinkTimer = -10.0
					me\LightBlink = 2.0
					PlaySound_Strict(snd_I\HorrorSFX[11])
					For i = 0 To MaxItemAmount - 1
						If Inventory(i) <> Null Then RemoveWearableItems(Inventory(i))
					Next
					ChangePlayerBodyTexture(PLAYER_BODY_PRISONER_TEX)
					TFormPoint(268.0, 0.0, -640.0, e\room\OBJ, 0)
					PositionEntity(me\Collider, TFormedX(), EntityY(me\Collider) + 0.1, TFormedZ(), True)
					ResetEntity(me\Collider)
					ResetInput()
					
					For i = 0 To 2
						ChangeDoorYPositon(e\room\RoomDoors[i], e\room\y)
					Next
					TFormPoint(337.0, 0.0, -640.0, e\room\OBJ, 0)
					d.Doors = CreateDoor(Null, TFormedX(), e\room\y + 4865.0 * RoomScale, TFormedZ(), e\room\Angle + 90.0, False, WOODEN_DOOR)
					d\Locked = 2 : d\MTFClose = False
					e\room\RoomDoors.Doors[8] = d
					
					e\room\Objects[8] = LoadRMesh(RoomPartsPath + "cont2_1123_door_frame.rmesh", Null)
					PositionEntity(e\room\Objects[8], e\room\x, e\room\y, e\room\z)
					RotateEntity(e\room\Objects[8], 0.0, e\room\Angle, 0.0)
					ScaleEntity(e\room\Objects[8], RoomScale, RoomScale, RoomScale)
					e\EventState = 4.0
				EndIf
				;[End Block]
			Case 4.0
				;[Block]
				If e\room\RoomDoors[5]\Open
					PointEntity(e\room\NPC[0]\Collider, me\Collider)
					AnimateNPC(e\room\NPC[0], 27.0, 54.0, 0.5, False)
					If e\room\NPC[0]\Frame >= 53.9
						e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Horror.ogg"))
						
						me\BlurTimer = 500.0
						me\Injuries = 1.5
						me\Bloodloss = 70.0
						me\BlinkTimer = -10.0
						me\LightBlink = 2.0
						
						ChangePlayerBodyTexture(PLAYER_BODY_NORMAL_TEX)
						
						PositionEntity(me\Collider, EntityX(e\room\OBJ, True), 0.3, EntityZ(e\room\OBJ, True), True)
						ResetEntity(me\Collider)
						
						e\EventState2 = 0.0
						e\EventState = 5.0
					EndIf
				EndIf
				;[End Block]
			Case 5.0
				;[Block]
				e\EventState2 = e\EventState2 + fps\Factor[0]
				If e\EventState2 > 500.0
					For i = 4 To 5
						e\room\RoomDoors[i]\Open = False
						e\room\RoomDoors[i]\Locked = 2
					Next
					
					TFormPoint(-575.0, 5006.0, -402.0, e\room\OBJ, 0)
					PositionEntity(e\room\NPC[0]\Collider, TFormedX(), TFormedY(), TFormedZ())
					ResetEntity(e\room\NPC[0]\Collider)
					
					me\Injuries = 1.5
					me\Bloodloss = 70.0
					me\BlinkTimer = -10.0
					me\BlurTimer = 500.0
					me\BlinkTimer = -10.0
					me\LightBlink = 2.0
					
					TFormPoint(-468.0, 4946.0, -273.0, e\room\OBJ, 0)
					PositionEntity(me\Collider, TFormedX(), TFormedY(), TFormedZ())
					ResetEntity(me\Collider)
					For i = 0 To MaxItemAmount - 1
						If Inventory(i) <> Null Then RemoveWearableItems(Inventory(i))
					Next
					ChangePlayerBodyTexture(PLAYER_BODY_PRISONER_TEX)
					
					CreateDecal(DECAL_BLOOD_2, EntityX(me\Collider, True), e\room\y + 4865.0 * RoomScale + 0.005, EntityZ(me\Collider, True), 90.0, Rnd(360.0), 0.0, 0.5)
					
					PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Officer2.ogg"), True)
					
					e\EventState = 6.0
				EndIf
				;[End Block]
			Case 6.0
				;[Block]
				PointEntity(e\room\NPC[0]\Collider, me\Collider)
				AnimateNPC(e\room\NPC[0], 75.0, 128.0, 0.04)
				If e\room\NPC[0]\Frame >= 127.9
					PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Gunshot.ogg"))
					e\EventState = 7.0
				EndIf
				;[End Block]
			Case 7.0
				;[Block]
				PositionEntity(me\Collider, EntityX(e\room\OBJ, True), 0.3, EntityZ(e\room\OBJ, True), True)
				ResetEntity(me\Collider)
				me\LightFlash = 6.0 : me\BlurTimer = 500.0
				me\Injuries = me\PrevInjuries : me\PrevInjuries = 0.0
				me\Bloodloss = me\PrevBloodloss : me\PrevBloodloss = 0.0
				IsBlackOut = PrevIsBlackOut : PrevIsBlackOut = True
				If (Not me\Crouch) Then SetCrouch(True)
				For i = 0 To MaxItemAmount - 1
					If Inventory(i) <> Null
						If Inventory(i)\ItemTemplate\Name = "Leaflet"
							RemoveItem(Inventory(i))
							Exit
						EndIf
						If Inventory(i)\ItemTemplate\ID = it_clipboard
							Local Temp% = False
							
							For j = 0 To Inventory(i)\InvSlots - 1
								If Inventory(i)\SecondInv[j] <> Null
									If Inventory(i)\SecondInv[j]\ItemTemplate\Name = "Leaflet"
										RemoveItem(Inventory(i)\SecondInv[j])
										Temp = True
										Exit
									EndIf
								EndIf
							Next
							If Temp Then Exit
						EndIf
					EndIf
				Next
				
				GiveAchievement("1123")
				ChangePlayerBodyTexture(PLAYER_BODY_NORMAL_TEX)
				RemoveDoor(e\room\RoomDoors[8])
				RemoveNPC(e\room\NPC[0])
				For i = 7 To 8
					FreeEntity(e\room\Objects[i]) : e\room\Objects[i] = 0
				Next
				RemoveEvent(e)
				skull_event = Null
				;[End Block]
		End Select
	EndIf
End Function

Function UpdateEvent_Room2C_GW_LCZ%(e.Events)
	If e\room\Dist < 8.0
		; ~ Spawn SCP-173 with 1/3 chance
		;[Block]
		If e\EventState = 0.0
			e\EventState = Rand(3)
		Else
			If e\EventState < 4.0
				If n_I\Curr173\Idle > 1 Lor e\EventState <> 3.0
					e\EventState = 4.0
				Else
					If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) > 16.0 And (Not PlayerSees173(n_I\Curr173)) And PlayerRoom <> e\room
						TFormPoint(-656.0, 120.0, 448.0, e\room\OBJ, 0)
						
						Local x# = TFormedX(), y# = TFormedY(), z# = TFormedZ()
						
						PlaySoundEx(LoadTempSound("SFX\Room\Room2Nuke\Vent" + Rand(0, 2) + ".ogg"), Camera, e\room\OBJ, 12.0, 1.5)
						
						PositionEntity(e\room\Objects[0], x, e\room\y + 4.0 * RoomScale, z, True)
						RotateEntity(e\room\Objects[0], 0.0, Rnd(360.0), 0.0, True)
						EntityType(e\room\Objects[0], HIT_MAP)
						
						TeleportEntity(n_I\Curr173\Collider, x, y, z, n_I\Curr173\CollRadius + 0.12, True)
						n_I\Curr173\CurrentRoom = e\room
						
						e\EventState = 4.0
					EndIf
				EndIf
			EndIf
		EndIf
		;[End Block]
		
		; ~ Blinds
		;[Block]
		; ~ Use e\SoundCHN and e\SoundCHN2 channels for blinds
		UpdateLever(e\room\RoomLevers[1]\OBJ)
		If EntityPitch(e\room\RoomLevers[1]\OBJ) < 0.0
			e\EventState3 = Min(e\EventState3 + fps\Factor[0], 152.0)
		Else
			e\EventState3 = Max(e\EventState3 - fps\Factor[0], 0.0)
		EndIf
		If e\EventState3 > 0.0 And e\EventState3 < 152.0
			e\SoundCHN = LoopSoundEx(snd_I\BlindsSFX, e\SoundCHN, Camera, e\room\Objects[2], 2.0)
		Else
			StopChannel(e\SoundCHN) : e\SoundCHN = 0
		EndIf
		
		PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[1], True), e\EventState3 * RoomScale, EntityZ(e\room\Objects[1], True), True)
		UpdateSoundOrigin(e\BlindsCHN, Camera, e\room\Objects[2])
		
		UpdateLever(e\room\RoomLevers[3]\OBJ)
		If EntityPitch(e\room\RoomLevers[3]\OBJ) < 0.0
			e\EventState4 = Min(e\EventState4 + fps\Factor[0], 152.0)
		Else
			e\EventState4 = Max(e\EventState4 - fps\Factor[0], 0.0)
		EndIf
		If e\EventState4 > 0.0 And e\EventState4 < 152.0
			e\SoundCHN2 = LoopSoundEx(snd_I\BlindsSFX, e\SoundCHN2, Camera, e\room\Objects[4], 2.0)
		Else
			StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
		EndIf
		
		PositionEntity(e\room\Objects[3], EntityX(e\room\Objects[3], True), e\EventState4 * RoomScale, EntityZ(e\room\Objects[3], True), True)
		UpdateSoundOrigin(e\BlindsCHN, Camera, e\room\Objects[4])
		;[End Block]
	EndIf
	
	If PlayerRoom = e\room
		; ~ Gas valves
		;[Block]
		Local GasLever1% = UpdateLever(e\room\RoomLevers[0]\OBJ)
		Local GasLever2% = UpdateLever(e\room\RoomLevers[2]\OBJ)
		
		If GasLever1
			If e\room\RoomEmitters[0] = Null
				TFormPoint(-177.0, 340.0, 655.0, e\room\OBJ, 0)
				e\room\RoomEmitters[0] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
				e\room\RoomEmitters[0]\State = 1
			EndIf
		Else
			If e\room\RoomEmitters[0] <> Null Then FreeEmitter(e\room\RoomEmitters[0])
		EndIf
		
		If GasLever2
			If e\room\RoomEmitters[1] = Null
				TFormPoint(-655.0, 340.0, 240.0, e\room\OBJ, 0)
				e\room\RoomEmitters[1] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
				e\room\RoomEmitters[1]\State = 1
			EndIf
		Else
			If e\room\RoomEmitters[1] <> Null Then FreeEmitter(e\room\RoomEmitters[1])
		EndIf
		;[End Block]
		
		; ~ Door control
		;[Block]
		If (Not GasLever1) And (Not GasLever2)
			Local i%
			
			For i = 0 To 1
				e\room\RoomDoors[i]\Timer = 0.0
				e\room\RoomDoors[i]\TimerState = 0.0
				e\room\RoomDoors[i]\LinkedDoor = Null
			Next
			e\EventState2 = 0.0
		Else
			If e\EventState2 = 0.0
				If e\room\RoomDoors[0]\Open Then OpenCloseDoor(e\room\RoomDoors[0])
				If e\room\RoomDoors[1]\Open Then OpenCloseDoor(e\room\RoomDoors[1])
				
				e\room\RoomDoors[0]\Timer = 70.0 * 5.0
				e\room\RoomDoors[0]\LinkedDoor = e\room\RoomDoors[1]
				e\room\RoomDoors[1]\Timer = 70.0 * 5.0
				e\room\RoomDoors[1]\LinkedDoor = e\room\RoomDoors[0]
				
				e\EventState2 = 1.0
			EndIf
		EndIf
		;[End Block]
	EndIf
End Function

Function UpdateEvent_Cont2C_066_1162_ARC%(e.Events)
	; ~ e\EventState: A variable to determine the "nostalgia" items
	; ~ 0.0 = No nostalgia item
	; ~ 1.0 = Lost key
	; ~ 2.0 = Disciplinary Hearing DH-S-4137-17092
	; ~ 3.0 = Coin
	; ~ 4.0 = Movie Ticket
	; ~ 5.0 = Old Badge
	
	; ~ e\EventState2: Defining which slot from the Inventory should be picked
	
	; ~ e\EventState3: A check for if a item should be removed
	; ~ 0.0 = no item "trade" will happen
	; ~ 1.0 = item "trade" will happen
	; ~ 2.0 = the player doesn't have any items in the Inventory, giving him heavy injuries and giving him a random item
	; ~ 3.0 = player got a memorial item (to explain a bit D-9341's background)
	; ~ 3.1 = player got a memorial item + injuries (because he didn't have any item in his inventory before)
	
	; ~ e\EventState4: A check for SCP-066 to spawn in the area.
	
	If e\EventState4 = 0.0
		If e\room\Dist < 8.0
			If n_I\Curr066 = Null
				n_I\Curr066 = CreateNPC(NPCType066, EntityX(e\room\OBJ), e\room\y + 0.3, EntityZ(e\room\OBJ))
			Else
				If n_I\Curr066\State <> 66.0
					TeleportEntity(n_I\Curr066\Collider, EntityX(e\room\OBJ), e\room\y + 0.3, EntityZ(e\room\OBJ), n_I\Curr066\CollRadius, True)
					n_I\Curr066\CurrentRoom = e\room
				EndIf
			EndIf
			e\EventState4 = 1.0
		EndIf
	EndIf
	
	If PlayerRoom = e\room
		e\EventState = 0.0
		
		Local it.Items, itt.ItemTemplates
		Local Pick1162ARC% = True
		Local pp% = CreatePivot(e\room\OBJ)
		Local i%, Pvt%
		
		PositionEntity(pp, 976.0, 128.0, -640.0)
		
		For it.Items = Each Items
			If (Not it\Picked)
				If EntityDistanceSquared(it\Collider, e\room\Objects[0]) < 0.5625 Then Pick1162ARC = False
			EndIf
		Next
		
		If Pick1162ARC And InteractObject(e\room\Objects[0], 0.75)
			e\EventState2 = Rand(0, MaxItemAmount - 1)
			If Inventory(e\EventState2) <> Null
				; ~ Randomly picked item slot has an item in it, using this slot
				e\EventState3 = 1.0
			Else
				; ~ Randomly picked item slot is empty, getting the first available slot
				For i = 0 To MaxItemAmount - 1
					Local IsSlotEmpty% = (Inventory((i + e\EventState2) Mod MaxItemAmount) = Null)
					
					; ~ Successful
					If (Not IsSlotEmpty) Then e\EventState2 = (i + e\EventState2) Mod MaxItemAmount
					
					If Rand(8) = 1
						e\EventState3 = 3.0 + (0.1 * IsSlotEmpty)
						e\EventState = Rand(5)
						
						; ~ Checking if the selected nostalgia item already exists or not
						Local ItemName$ = ""
						
						Select e\EventState
							Case 1.0
								;[Block]
								ItemName = "Lost Key"
								;[End Block]
							Case 2.0
								;[Block]
								ItemName = "Disciplinary Hearing DH-S-4137-17092"
								;[End Block]
							Case 3.0
								;[Block]
								ItemName = "Coin"
								;[End Block]
							Case 4.0
								;[Block]
								ItemName = "Movie Ticket"
								;[End Block]
							Case 5.0
								;[Block]
								ItemName = "Old Badge"
								;[End Block]
						End Select
						
						For it.Items = Each Items
							If it\Name = ItemName
								e\EventState3 = 1.0
								e\EventState = 0.0
								Exit
							EndIf
						Next
						If (Not IsSlotEmpty) Then Exit
					Else
						If IsSlotEmpty
							e\EventState3 = 2.0
						Else
							e\EventState3 = 1.0
							Exit
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		; ~ Trade successful
		If e\EventState3 = 1.0
			Local ShouldCreateItem% = False
			
			For itt.ItemTemplates = Each ItemTemplates
				If IsItemGoodFor1162ARC(itt)
					Select Inventory(e\EventState2)\ItemTemplate\ID
						Case it_lostkey
							;[Block]
							If (itt\ID = it_key0 Lor itt\ID = it_key1) And Rand(2) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_paper, it_oldpaper
							;[Block]
							If itt\ID = it_paper And Rand(12) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_gasmask, it_finegasmask, it_veryfinegasmask, it_gasmask148, it_hazmatsuit, it_finehazmatsuit, it_veryfinehazmatsuit, it_hazmatsuit148
							;[Block]
							If (itt\ID = it_gasmask Lor itt\ID = it_finegasmask Lor itt\ID = it_veryfinegasmask Lor itt\ID = it_gasmask148 Lor itt\ID = it_hazmatsuit Lor itt\ID = it_finehazmatsuit Lor itt\ID = it_veryfinehazmatsuit Lor itt\ID = it_hazmatsuit148) And Rand(2) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_key0, it_key1, it_key2, it_key3
							;[Block]
							If (itt\ID = it_key0 Lor itt\ID = it_key1 Lor itt\ID = it_key2 Lor itt\ID = it_key3) And Rand(12) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_mastercard, it_playcard, it_origami, it_electronics
							;[Block]
							If (itt\ID = it_mastercard Lor itt\ID = it_playcard Lor itt\ID = it_origami Lor itt\ID = it_electronics) And Rand(5) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_vest, it_finevest
							;[Block]
							If (itt\ID = it_vest Lor itt\ID = it_finevest) And Rand(2) = 1 Then ShouldCreateItem = True
							;[End Block]
						Case it_eyedrops, it_fineeyedrops, it_veryfineeyedrops, it_syringe, it_finesyringe, it_veryfinesyringe, it_scp420j, it_joint, it_cigarette
							;[Block]
							If (itt\ID = it_eyedrops Lor itt\ID = it_fineeyedrops Lor itt\ID = it_veryfineeyedrops Lor itt\ID = it_syringe Lor itt\ID = it_finesyringe Lor itt\ID = it_veryfinesyringe Lor itt\ID = it_cigarette Lor itt\ID = it_joint Lor itt\ID = it_scp420j) And Rand(4) = 1 Then ShouldCreateItem = True
							;[End Block]
						Default
							;[Block]
							If (itt\ID = it_mastercard Lor itt\ID = it_playcard) And Rand(6) = 1 Then ShouldCreateItem = True
							;[End Block]
					End Select
				EndIf
				
				If ShouldCreateItem
					RemoveWearableItems(Inventory(e\EventState2))
					RemoveItem(Inventory(e\EventState2))
					
					it.Items = CreateItem(itt\Name, itt\ID, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					
					PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\Exchange" + Rand(0, 4) + ".ogg"))
					e\EventState3 = 0.0
					
					GiveAchievement("1162arc")
					mo\MouseHit1 = False
					Exit
				EndIf
			Next
			; ~ Trade not sucessful (player got in return to injuries a new item)
		ElseIf e\EventState3 = 2.0
			me\Injuries = me\Injuries + 5.0
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(me\Collider), EntityY(me\Collider) - 0.05, EntityZ(me\Collider))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			CreateDecal(DECAL_BLOOD_2, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, 0.75)
			FreeEntity(Pvt) : Pvt = 0
			For itt.ItemTemplates = Each ItemTemplates
				If IsItemGoodFor1162ARC(itt) And Rand(6) = 1
					it.Items = CreateItem(itt\Name, itt\ID, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					
					GiveAchievement("1162arc")
					mo\MouseHit1 = False
					PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\BodyHorrorExchange" + Rand(0, 3) + ".ogg"))
					me\LightFlash = 5.0
					If me\Injuries > 7.0
						msg\DeathMsg = GetLocalString("death", "1162")
						Kill(True)
					Else
						CreateMsg(GetLocalString("msg", "1162_1"))
					EndIf
					e\EventState3 = 0.0
					Exit
				EndIf
			Next
			; ~ Trade with nostalgia item
		ElseIf e\EventState3 >= 3.0
			If e\EventState3 < 3.1
				PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\Exchange" + Rand(0, 4) + ".ogg"))
				RemoveWearableItems(Inventory(e\EventState2))
				RemoveItem(Inventory(e\EventState2))
			Else
				me\Injuries = me\Injuries + 5.0
				Pvt = CreatePivot()
				PositionEntity(Pvt, EntityX(me\Collider), EntityY(me\Collider) - 0.05, EntityZ(me\Collider))
				TurnEntity(Pvt, 90.0, 0.0, 0.0)
				EntityPick(Pvt, 0.3)
				CreateDecal(DECAL_BLOOD_2, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, 0.75)
				FreeEntity(Pvt) : Pvt = 0
				PlaySound_Strict(LoadTempSound("SFX\SCP\1162_ARC\BodyHorrorExchange" + Rand(0, 3) + ".ogg"))
				me\LightFlash = 5.0
				If me\Injuries > 7.0
					msg\DeathMsg = GetLocalString("death", "1162")
					Kill(True)
				Else
					CreateMsg(GetLocalString("msg", "1162_2"))
				EndIf
				e\EventState2 = 0.0
			EndIf
			
			Select e\EventState
				Case 1.0
					;[Block]
					it.Items = CreateItem("Lost Key", it_lostkey, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					;[End Block]
				Case 2.0
					;[Block]
					it.Items = CreateItem("Disciplinary Hearing DH-S-4137-17092", it_oldpaper, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					;[End Block]
				Case 3.0
					;[Block]
					it.Items = CreateItem("Coin", it_coin, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					;[End Block]
				Case 4.0
					;[Block]
					it.Items = CreateItem("Movie Ticket", it_ticket, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					;[End Block]
				Case 5.0
					;[Block]
					it.Items = CreateItem("Old Badge", it_badge2, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
					;[End Block]
			End Select
			
			GiveAchievement("1162arc")
			mo\MouseHit1 = False
			e\EventState3 = 0.0
		EndIf
		FreeEntity(pp) : pp = 0
	EndIf
End Function

Function UpdateEvent_Room3_Storage%(e.Events)
	If PlayerRoom = e\room
		Local x1# = EntityX(me\Collider, True)
		Local y1# = EntityY(me\Collider, True)
		Local z1# = EntityZ(me\Collider, True)
		Local emit.Emitter, n.NPCs
		Local i%, x2#, y2#, z2#
		
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[2]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[3]))
		ToElevatorFloor = LowerFloor
		e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
		e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
		
		If EntityY(me\Collider) < (-4600.0) * RoomScale
			me\Zone = 0
			e\room\RoomTemplate\DisableDecals = 1
			
			If wi\GasMask = 0 And wi\HazmatSuit = 0
				me\BlurTimer = Min(me\BlurTimer + (fps\Factor[0] * 1.05), 1500.0)
				If me\BlurTimer >= 500.0 Then UpdateCough(1000)
				If me\BlurTimer >= 1500.0 And me\FallTimer = 0.0 And (Not me\Terminated)
					msg\DeathMsg = Format(GetLocalString("death", "939.gas"), SubjectName)
					Kill()
				EndIf
			EndIf
			
			ShouldPlay = 7
			
			If e\room\NPC[3] = Null
				TFormPoint(3372.0, -5578.8, 6294.0, e\room\OBJ, 0)
				e\room\NPC[4] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[4]\State3 = -1.0 : e\room\NPC[4]\IsDead = True
				ChangeNPCTextureID(e\room\NPC[4], NPC_CLASS_D_VICTIM_939_1_TEXTURE)
				SetNPCFrame(e\room\NPC[4], 40.0)
				RotateEntity(e\room\NPC[4]\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
				
				TFormPoint(1083.0, -5578.0, 989.0, e\room\OBJ, 0)
				e\room\NPC[5] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[5]\State3 = -1.0 : e\room\NPC[5]\IsDead = True
				ChangeNPCTextureID(e\room\NPC[5], NPC_CLASS_D_VICTIM_939_2_TEXTURE)
				SetNPCFrame(e\room\NPC[5], 19.0)
				RotateEntity(e\room\NPC[5]\Collider, 0.0, e\room\Angle, 0.0, True)
				
				TFormPoint(3194.0, -5632.0, 6386.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), TFormedY() + 0.05, TFormedZ())
				n\State = 2.0
				RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
				
				TFormPoint(3063.0, -5632.0, 1354.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), TFormedY() + 0.05, TFormedZ())
				n\State = 2.0
				RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
				
				TFormPoint(1188.0, -5632.0, 1114.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), TFormedY() + 0.05, TFormedZ())
				n\State = 2.0
				RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
				
				For i = 0 To 3
					e\room\NPC[i] = CreateNPC(NPCType939, 0.0, 0.0, 0.0)
				Next
			Else
				If e\EventState = 0.0
					; ~ Instance # 1
					PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True) + 0.2, EntityZ(e\room\Objects[4], True))
					ResetEntity(e\room\NPC[0]\Collider)
					e\room\NPC[0]\State = 2.0
					e\room\NPC[0]\State2 = 5.0
					e\room\NPC[0]\PrevState = 7
					; ~ Instance # 2
					PositionEntity(e\room\NPC[1]\Collider, EntityX(e\room\Objects[8], True), EntityY(e\room\Objects[8], True) + 0.2, EntityZ(e\room\Objects[8], True))
					ResetEntity(e\room\NPC[1]\Collider)
					e\room\NPC[1]\State = 2.0
					e\room\NPC[1]\State2 = 9.0
					e\room\NPC[1]\PrevState = 11
					; ~ Instance # 3
					PositionEntity(e\room\NPC[2]\Collider, EntityX(e\room\Objects[12], True), EntityY(e\room\Objects[12], True) + 0.2, EntityZ(e\room\Objects[12], True))
					ResetEntity(e\room\NPC[2]\Collider)
					e\room\NPC[2]\State = 2.0
					e\room\NPC[2]\State2 = 13.0
					e\room\NPC[2]\PrevState = 15
					; ~ Instance # 4
					PositionEntity(e\room\NPC[3]\Collider, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True) + 0.2, EntityZ(e\room\Objects[6], True)) 
					ResetEntity(e\room\NPC[3]\Collider)
					e\room\NPC[3]\State = 2.0
					e\room\NPC[3]\State2 = 7.0
					e\room\NPC[3]\PrevState = 7
					
					e\EventState = 1.0
				EndIf
				
				If (Not e\room\RoomDoors[4]\Open)
					For i = 0 To 1
						If UpdateLever(e\room\RoomLevers[i]\OBJ)
							e\room\RoomDoors[4]\Open = True
							LoadEventSound(e, "SFX\Door\Door2OpenDistanced.ogg", 1)
							e\SoundCHN2 = PlaySoundEx(e\Sound2, Camera, e\room\RoomDoors[4]\OBJ, 400.0)
							Exit
						EndIf
					Next
				Else
					For i = 0 To 1
						UpdateLever(e\room\RoomLevers[i]\OBJ, True)
						If EntityPitch(e\room\RoomLevers[i]\OBJ, True) >= 0.0 Then RotateEntity(e\room\RoomLevers[i]\OBJ, -80.0, EntityYaw(e\room\RoomLevers[i]\OBJ), 0.0)
					Next
				EndIf
				
				;e\room\NPC[0]\IgnorePlayer = False
				;e\room\NPC[2]\IgnorePlayer = False
				;e\room\NPC[3]\IgnorePlayer = False
				
				;CurrTrigger = CheckTriggers()
				
				;Select CurrTrigger
				;	Case "939-1_fix"
				;		;[Block]
				;		e\room\NPC[0]\IgnorePlayer = True
				;		e\room\NPC[3]\IgnorePlayer = True
				;		;[End Block]
				;	Case "939-3_fix"
				;		;[Block]
				;		e\room\NPC[2]\IgnorePlayer = True
				;		;[End Block]
				;End Select
				
				UpdateSoundOrigin(e\SoundCHN2, Camera, e\room\RoomDoors[4]\OBJ, 400.0)
				
				PlayerFallingPickDistance = 0.0
				
				If UpdateButton(Null, e\room\Objects[18]) And mo\MouseHit1
					SetAnimTime(e\room\Objects[18], 1.0)
					CreateMsg(GetLocalString("msg", "elev.broken"))
					PlaySound_Strict(ButtonSFX[1])
					mo\MouseHit1 = False
				EndIf
				RotateEntity(e\room\Objects[17], Sin(Float(MilliSec * 0.05)), 0.0, -Sin(Float(MilliSec * 0.03)))
				If AmbientSFX(3, 9) = 0
					AmbientSFX(3, 9) = LoadSound_Strict("SFX\Ambient\General\Ambient9.ogg")
				Else
					e\SoundCHN = LoopSoundEx(AmbientSFX(3, 9), e\SoundCHN, Camera, e\room\Objects[17], 6.0, 1.5)
				EndIf
				If EntityY(me\Collider) < -6400.0 * RoomScale
					If (Not chs\GodMode) And (Not me\Terminated) And me\FallTimer >= 0.0
						msg\DeathMsg = Format(GetLocalString("death", "939.shaft"), SubjectName)
						PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Impact.ogg"))
						me\BlurTimer = 3000.0
						Kill(False, False)
					EndIf
				EndIf
			EndIf
		Else
			e\room\RoomTemplate\DisableDecals = 0
			e\EventState = 0.0
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont3_372%(e.Events)
	If PlayerRoom = e\room
		If e\room\RoomDoors[0]\Open
			PlaySound_Strict(snd_I\RustleSFX[Rand(0, 5)])
			CreateNPC(NPCType372, 0.0, 0.0, 0.0)
			RemoveEvent(e)
		EndIf
	EndIf
End Function

Function UpdateEvent_Room4_IC%(e.Events)
	If e\room\Dist < 12.0
		Local e2.Events
		
		For e2.Events = Each Events
			If e2\EventID = e_room2_sl
				If e2\EventState3 = 0.0
					TurnCheckpointMonitorsOff()
				Else
					UpdateCheckpointMonitors()
				EndIf
				Exit
			EndIf
		Next
		If e\room\NPC[0] = Null
			TFormPoint(-603.0, 51.2, 256.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\IsDead = True : e\room\NPC[0]\State3 = -1.0
			SetNPCFrame(e\room\NPC[0], 40.0)
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_1048_A_TEXTURE)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
		EndIf
	EndIf
End Function

Dim PlacedIn.Rooms(0)

Function UpdateEvent_Cont1_035%(e.Events)
	If PlayerRoom = e\room
		; ~ EventState2: has SCP-035 told the code to the storage room (True / False)
		
		; ~ EventState3: has the player opened the gas valves (0 = no, 0 < x < 70.0 * 35.0 yes, x > 70.0 * 35.0 the host has died)
		
		If e\EventState = 0.0
			e\room\NPC[0] = CreateNPC(NPCTypeD, EntityX(e\room\Objects[1], True), 0.5, EntityZ(e\room\Objects[1], True))
			e\room\NPC[0]\State = 6.0
			CreateNPCAsset(e\room\NPC[0], 0)
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_035_TEXTURE)
			SetNPCFrame(e\room\NPC[0], 501.0)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
			
			e\EventState = 1.0
		ElseIf e\EventState > 0.0
			Local Temp%, i%
			Local Visible% = (Not (chs\NoTarget Lor I_268\InvisibilityOn))
			
			ShouldPlay = 26
			
			If ChannelPlaying(e\room\NPC[0]\SoundCHN) Then e\room\NPC[0]\SoundCHN = LoopSoundEx(e\room\NPC[0]\Sound, e\room\NPC[0]\SoundCHN, Camera, e\room\OBJ, 6.0)
			
			If e\EventState = 1.0
				If EntityDistanceSquared(me\Collider, e\room\Objects[0]) < 1.44
					If EntityInView(e\room\NPC[0]\OBJ, Camera)
						GiveAchievement("035")
						PlaySound_Strict(LoadTempSound("SFX\SCP\035\GetUp.ogg"))
						e\EventState = 1.5
					EndIf
				EndIf
			Else
				If e\room\RoomDoors[3]\Open Then e\EventState2 = Max(e\EventState2, 1.0)
				; ~ The door is closed
				If (Not UpdateLever(e\room\RoomLevers[0]\OBJ, (e\EventState2 = 20.0)))
					; ~ The gas valves are open
					Temp = UpdateLever(e\room\RoomLevers[1]\OBJ)
					If Temp Lor (e\EventState3 > 70.0 * 25.0 And e\EventState3 < 70.0 * 50.0)
						If Temp
							For i = 0 To 1
								If e\room\RoomEmitters[i] = Null
									TFormPoint(-269.0, 400.0, 135.0 + (i * 489.0), e\room\OBJ, 0)
									e\room\RoomEmitters.Emitter[i] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
									e\room\RoomEmitters[i]\State = 1
								EndIf
							Next
						Else
							For i = 0 To 1
								If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
							Next
						EndIf
						
						If e\EventState3 > (-70.0) * 30.0
							e\EventState3 = Abs(e\EventState3) + fps\Factor[0]
							If e\EventState3 > 1.0 And e\EventState3 - fps\Factor[0] <= 1.0
								e\room\NPC[0]\State = 0.0
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Gased0.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
							ElseIf e\EventState3 > 70.0 * 15.0 And e\EventState3 < 70.0 * 25.0
								If e\EventState3 - fps\Factor[0] <= 70.0 * 15.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Gased1.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
									SetNPCFrame(e\room\NPC[0], 553.0)
								EndIf
								e\room\NPC[0]\State = 6.0
								
								AnimateNPC(e\room\NPC[0], 553.0, 529.0, -0.12, False)
							ElseIf e\EventState3 > 70.0 * 25.0 And e\EventState3 < 70.0 * 35.0
								e\room\NPC[0]\State = 6.0
								AnimateNPC(e\room\NPC[0], 529.0, 524.0, -0.08, False)
							ElseIf e\EventState3 > 70.0 * 35.0
								PointEntity(e\room\NPC[0]\OBJ, me\Collider)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
								
								If e\room\NPC[0]\State = 6.0
									me\Sanity = (-150.0) * Sin(AnimTime(e\room\NPC[0]\OBJ) - 524.0) * 9.0
									AnimateNPC(e\room\NPC[0], 524.0, 553.0, 0.08, False)
									If e\room\NPC[0]\Frame > 552.9 Then e\room\NPC[0]\State = 0.0
								EndIf
								
								If e\EventState3 - fps\Factor[0] <= 70.0 * 35.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedKilled0.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
									PlaySound_Strict(LoadTempSound("SFX\SCP\035\KilledGetUp.ogg"))
									
									I_035\Sad = True
									
									Update035Label(e\room\Objects[4])
									CreateNPCAsset(e\room\NPC[0])
									
									e\EventState = 70.0 * 60.0
								EndIf
							EndIf
						EndIf
					Else ; ~ Gas valves closed
						If e\room\NPC[0]\State = 6.0
							If e\room\NPC[0]\Frame >= 501.0 And e\room\NPC[0]\Frame <= 523.0
								AnimateNPC(e\room\NPC[0], 501.0, 523.0, 0.08, False)
								If e\room\NPC[0]\Frame > 522.9 Then e\room\NPC[0]\State = 0.0
							ElseIf e\room\NPC[0]\Frame >= 524.0 And e\room\NPC[0]\Frame <= 553.0
								AnimateNPC(e\room\NPC[0], 524.0, 553.0, 0.08, False)
								If e\room\NPC[0]\Frame > 552.9 Then e\room\NPC[0]\State = 0.0
							EndIf
						EndIf
						
						For i = 0 To 1
							If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
						Next
						
						If e\room\NPC[0]\State = 0.0
							PointEntity(e\room\NPC[0]\OBJ, me\Collider)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
							
							If Rand(500) = 1
								e\room\NPC[0]\State2 = (EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\Objects[1]) > 4.0)
								e\room\NPC[0]\State = 1.0
							EndIf
						ElseIf e\room\NPC[0]\State = 1.0
							If e\room\NPC[0]\State2 = 1.0
								PointEntity(e\room\NPC[0]\OBJ, e\room\Objects[1])
								If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\Objects[1]) < 0.09 Then e\room\NPC[0]\State = 0.0
							Else
								RotateEntity(e\room\NPC[0]\OBJ, 0.0, e\room\Angle - 180.0, 0.0, True)
								If EntityDistanceSquared(e\room\NPC[0]\Collider, e\room\Objects[1]) > 4.0 Then e\room\NPC[0]\State = 0.0
							EndIf
							RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
						EndIf
						
						If e\EventState3 > 0.0
							e\EventState3 = -e\EventState3
							If e\EventState3 < (-70.0) * 35.0 ; ~ The host is dead
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedKilled1.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
								e\EventState = 70.0 * 60.0
							Else
								If e\EventState3 < (-70.0) * 20.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedStop1.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
								Else
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedStop0.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
									e\EventState3 = (-70.0) * 21.0
								EndIf
								e\EventState = 70.0 * 61.0
							EndIf
						Else
							e\EventState = e\EventState + fps\Factor[0]
							If e\EventState > 70.0 * 4.0 And e\EventState - fps\Factor[0] <= 70.0 * 4.0
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Help0.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
								e\EventState = 70.0 * 10.0
							ElseIf e\EventState > 70.0 * 20.0 And e\EventState - fps\Factor[0] <= 70.0 * 20.0
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Help1.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
							ElseIf e\EventState > 70.0 * 40.0 And e\EventState - fps\Factor[0] <= 70.0 * 40.0
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle0.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
							ElseIf e\EventState > 70.0 * 50.0 And e\EventState - fps\Factor[0] <= 70.0 * 50.0
								If Visible
									LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle1.ogg")
									e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
								EndIf
							ElseIf e\EventState > 70.0 * 80.0 And e\EventState - fps\Factor[0] <= 70.0 * 80.0
								If e\EventState2 ; ~ Skip the closet part if player has already opened it
									e\EventState = 70.0 * 130.0
								Else
									If e\EventState3 < (-70.0) * 30.0 ; ~ The host is dead
										If Visible
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedCloset.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
									ElseIf e\EventState3 = 0.0 ; ~ The gas valves haven't been opened
										If Visible
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Closet0.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
									Else ; ~ Gas valves have been opened but 035 isn't dead
										If Visible
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedCloset.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
									EndIf
								EndIf
							ElseIf e\EventState > 70.0 * 80.0
								If e\EventState2 Then e\EventState = Max(e\EventState, 70.0 * 100.0)
								If e\EventState > 70.0 * 110.0 And e\EventState - fps\Factor[0] <= 70.0 * 110.0
									If e\EventState2
										If Visible
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Closet1.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
										e\EventState = 70.0 * 130.0
									Else
										If Visible
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle2.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
									EndIf
								ElseIf e\EventState > 70.0 * 125.0 And e\EventState - fps\Factor[0] <= 70.0 * 125.0
									If Visible
										If e\EventState2
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Closet0.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										Else
											LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle3.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
										EndIf
									EndIf
								ElseIf e\EventState > 70.0 * 150.0 And e\EventState - fps\Factor[0] <= 70.0 * 150.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle4.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
								ElseIf e\EventState > 70.0 * 200.0 And e\EventState - fps\Factor[0] <= 70.0 * 200.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle5.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
								ElseIf e\EventState > 70.0 * 250.0 And e\EventState - fps\Factor[0] <= 70.0 * 250.0
									If Visible
										LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Idle6.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				Else ; ~ The player has opened the door
					If e\EventState2 < 10.0
						e\room\RoomDoors[2]\Open = False
						e\room\RoomDoors[2]\Locked = 1
						
						For i = 0 To 1
							If (Not e\room\RoomDoors[i]\Open) Then OpenCloseDoor(e\room\RoomDoors[i])
						Next
						
						If e\EventState3 = 0.0
							If Visible
								LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\Escape.ogg")
								e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
							EndIf
						ElseIf Abs(e\EventState3) > 70.0 * 35.0
							If Visible
								LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\KilledEscape.ogg")
								e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
							EndIf
						Else
							If Visible
								LoadNPCSound(e\room\NPC[0], "SFX\SCP\035\GasedEscape.ogg")
								e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound, True)
							EndIf
						EndIf
						e\EventState2 = 20.0
					EndIf
					
					If e\EventState2 = 20.0
						Local d.Doors
						Local Dist# = DistanceSquared(EntityX(e\room\RoomDoors[0]\FrameOBJ, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(e\room\RoomDoors[0]\FrameOBJ, True), EntityZ(e\room\NPC[0]\Collider, True))
						
						e\room\NPC[0]\State = 1.0
						If Dist > 6.25
							PointEntity(e\room\NPC[0]\OBJ, e\room\RoomDoors[1]\FrameOBJ)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0)
						ElseIf Dist > 0.49
							If ChannelPlaying(e\room\NPC[0]\SoundCHN)
								e\room\NPC[0]\State = 0.0
								PointEntity(e\room\NPC[0]\OBJ, me\Collider)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
							Else
								PointEntity(e\room\NPC[0]\OBJ, e\room\RoomDoors[0]\FrameOBJ)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
							EndIf
						Else
							RemoveNPC(e\room\NPC[0])
							For i = 0 To 2
								e\room\RoomDoors[i]\Locked = 0
							Next
							
							Local r.Rooms
							Local Attempts% = 0
							Local MaxAttempts% = 1000
							
							i = 0
							Dim PlacedIn.Rooms(5)
							While i < 5 And Attempts < MaxAttempts
								Attempts = Attempts + 1
								For r.Rooms = Each Rooms
									If r\RoomTemplate\Commonness > 0 And r\Zone = 3 And Rand(5) = 1
										Local AlreadyPlaced% = False
										Local j%
										
										For j = 0 To i - 1
											If PlacedIn(j) = r
												AlreadyPlaced = True
												Exit
											EndIf
										Next
										
										If (Not AlreadyPlaced)
											Local x#, y#, z#
											
											If r\RoomCenter <> 0
												x = EntityX(r\RoomCenter) + Rnd(-0.2, 0.2) : y = r\y + 0.22 : z = EntityZ(r\RoomCenter) + Rnd(-0.2, 0.2)
											Else
												x = r\x + Rnd(-0.2, 0.2) : y = r\y + 0.22 : z = r\z + Rnd(-0.2, 0.2)
											EndIf
											CreateNPC(NPCType035_Tentacle, x, y, z)
											CreateDecal(DECAL_CORROSIVE_1, x, r\y + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.4, 10.0, 0, 1, 180, 20, 20)
											PlacedIn(i) = r
											i = i + 1
											Exit
										EndIf
									EndIf
								Next
							Wend
							Dim PlacedIn.Rooms(0)
							
							OpenCloseDoor(e\room\RoomDoors[1])
							For d.Doors = Each Doors
								If d\DoorType = HEAVY_DOOR
									If DistanceSquared(EntityX(e\room\OBJ), EntityX(d\FrameOBJ, True), EntityZ(e\room\OBJ), EntityZ(d\FrameOBJ, True)) < 20.25
										OpenCloseDoor(d)
										Exit
									EndIf
								EndIf
							Next
							e\EventState2 = 0.0
							e\EventState3 = 0.0
							e\EventState = -1.0
						EndIf
					EndIf
				EndIf
			EndIf
		Else ; ~ SCP-035 has left
			If UpdateLever(e\room\RoomLevers[1]\OBJ)
				For i = 0 To 1
					If e\room\RoomEmitters[i] = Null
						TFormPoint(-269.0, 400.0, 135.0 + (i * 489.0), e\room\OBJ, 0)
						e\room\RoomEmitters.Emitter[i] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
						e\room\RoomEmitters[i]\State = 1
					EndIf
				Next
			Else
				For i = 0 To 1
					If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
				Next
			EndIf
			
			ShouldPlay = 1
			
			Temp = False
			
			; ~ Player is inside the containment chamber
			If EntityX(me\Collider) > Min(EntityX(e\room\Objects[2], True), EntityX(e\room\Objects[3], True))
				If EntityX(me\Collider) < Max(EntityX(e\room\Objects[2], True), EntityX(e\room\Objects[3], True))
					If EntityZ(me\Collider) > Min(EntityZ(e\room\Objects[2], True), EntityZ(e\room\Objects[3], True))
						If EntityZ(me\Collider) < Max(EntityZ(e\room\Objects[2], True), EntityZ(e\room\Objects[3], True))
							If e\room\NPC[1] = Null
								e\room\NPC[1] = CreateNPC(NPCType035_Tentacle, EntityX(e\room\Objects[1], True), e\room\y + 0.22, EntityZ(e\room\Objects[1], True))
							Else
								If e\room\NPC[1]\State > 0.0 And e\room\NPC[2] = Null
									TFormPoint(-72.0, 0.0, 132.0, e\room\OBJ, 0)
									e\room\NPC[2] = CreateNPC(NPCType035_Tentacle, TFormedX(), e\room\y + 0.22, TFormedZ())
								EndIf
							EndIf
							me\Stamina = CurveValue(Min(60.0, me\Stamina), me\Stamina, 20.0)
							
							Temp = True
							
							If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Room\035Chamber\Whispers0.ogg")
							If e\Sound2 = 0 Then e\Sound2 = LoadSound_Strict("SFX\Room\035Chamber\Whispers1.ogg")
							
							e\EventState2 = Min(e\EventState2 + (fps\Factor[0] / 6000.0), 1.0)
							e\EventState3 = CurveValue(e\EventState2, e\EventState3, 50.0)
							
							If I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4
								me\Sanity = me\Sanity - (fps\Factor[0] * 1.1)
								me\BlurTimer = Sin(MilliSec / 10.0) * Abs(me\Sanity)
							EndIf
							
							me\Injuries = me\Injuries + (fps\Factor[0] / (5000.0 * (1.0 + (wi\HazmatSuit > 0))))
							
							If me\Terminated And me\Bloodloss >= 100.0 Then msg\DeathMsg = Format(GetLocalString("death", "035"), SubjectName)
						EndIf
					EndIf
				EndIf
			EndIf
			
			If (Not Temp)
				e\EventState2 = Max(e\EventState2 - (fps\Factor[0] / 2000.0), 0.0)
				e\EventState3 = Max(e\EventState3 - (fps\Factor[0] / 100.0), 0.0)
			EndIf
			
			If e\EventState3 > 0.0 And I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4 
				e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\OBJ, 10.0, e\EventState3)
				e\SoundCHN2 = LoopSoundEx(e\Sound2, e\SoundCHN2, Camera, e\room\OBJ, 10.0, (e\EventState3 - 0.5) * 2.0)
			EndIf
		EndIf
		
		If e\room\NPC[0] <> Null Then UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\OBJ, 6.0, 0.8, True)
	Else
		If e\EventState = 0.0
			If e\Sound = 0 And InFacility = NullFloor
				For i = 0 To MaxRoomAdjacents - 1
					If IsRoomAdjacent(PlayerRoom\Adjacent[i], e\room)
						LoadEventSound(e, "SFX\Room\035Chamber\InProximity.ogg")
						PlaySound_Strict(e\Sound, True)
						Exit
					EndIf
				Next
			EndIf
		Else
			If e\room\NPC[0] <> Null Then UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\OBJ, 6.0, 0.8, True)
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_079%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs, it.Items
		
		If EntityY(me\Collider) < (-3868.0) * RoomScale
			If e\EventState = 0.0
				TFormPoint(-2260.0, -4985.0, 1000.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
				n\State = 8.0 : n\IsDead = True
				SetNPCFrame(n, 288.0)
				RotateEntity(n\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
				
				TFormPoint(-2220.0, -5056.0, 1000.0, e\room\OBJ, 0)
				CreateDecal(DECAL_BLOOD_2, TFormedX(), TFormedY() + 0.005, TFormedZ(), 90.0, Rnd(360.0), 0.0, 0.5)
				
				TFormPoint(-897.0, -4902.0, 783.0, e\room\OBJ, 0)
				it.Items = CreateItem("Document SCP-079", it_paper, TFormedX(), TFormedY(), TFormedZ())
				RotateEntity(it\Collider, 0.0, e\room\Angle, 0.0)
				
				e\EventState = 1.0
			EndIf
			If EntityHidden(e\room\Objects[0]) Then ShowEntity(e\room\Objects[0])
			
			ShouldPlay = 4
			
			If RemoteDoorOn
				e\room\RoomDoors[0]\Locked = 2
			Else
				e\room\RoomDoors[0]\Locked = 0
				If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					If e\EventState = 1.0
						If EntityDistanceSquared(e\room\Objects[0], me\Collider) < 9.0
							GiveAchievement("079")
							
							If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
							e\SoundCHN = StreamSound_Strict("SFX\SCP\079\Speech.ogg", opt\VoiceVolume * opt\MasterVolume)
							e\SoundCHN_IsStream = True
							
							e\EventState = 2.0
							e\EventState2 = 1.0
						EndIf
					ElseIf e\EventState >= 2.0
						e\EventState = e\EventState + fps\Factor[0]
						If e\EventState > 5000.0
							If EntityDistanceSquared(e\room\Objects[0], me\Collider) < 6.25
								If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
								e\SoundCHN = StreamSound_Strict("SFX\SCP\079\Refuse.ogg", opt\VoiceVolume * opt\MasterVolume)
								e\SoundCHN_IsStream = True
								
								e\EventState = 1.5
							EndIf
						EndIf
					EndIf
				EndIf
				
				If e\SoundCHN <> 0
					If IsStreamPlaying_Strict(e\SoundCHN)
						If Rand(4) = 1
							e\EventState3 = Int((e\EventState3 + 1.0) Mod 6)
							EntityTexture(e\room\Objects[1], mon_I\MonitorOverlayID[MONITOR_079_OVERLAYS_2], e\EventState3)
							If EntityHidden(e\room\Objects[1]) Then ShowEntity(e\room\Objects[1])
						ElseIf Rand(10) = 1
							If (Not EntityHidden(e\room\Objects[1])) Then HideEntity(e\room\Objects[1])
						EndIf
					Else
						StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
					EndIf
				ElseIf e\EventState > 1.0
					If EntityHidden(e\room\Objects[1]) Then ShowEntity(e\room\Objects[1])
					If Rand(4) = 1 Then e\EventState3 = Int((e\EventState3 + 1.0) Mod 12)
					EntityTexture(e\room\Objects[1], mon_I\MonitorOverlayID[MONITOR_079_OVERLAYS_1], e\EventState3)
				EndIf
			EndIf
		Else
			If (Not EntityHidden(e\room\Objects[0])) Then HideEntity(e\room\Objects[0])
		EndIf
		
		Local x# = EntityX(me\Collider, True)
		Local y# = EntityY(me\Collider, True)
		Local z# = EntityZ(me\Collider, True)
		
		me\InsideElevator = (IsInsideElevator(x, y, z, e\room\Objects[2]) Lor IsInsideElevator(x, y, z, e\room\Objects[3]))
		ToElevatorFloor = LowerFloor
		e\EventState4 = UpdateElevators(e\EventState4, e\room\RoomDoors[1], e\room\RoomDoors[2], e\room\Objects[2], e\room\Objects[3], e)
	EndIf
	
	If e\EventState2 = 1.0
		If RemoteDoorOn
			If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
			e\SoundCHN = StreamSound_Strict("SFX\SCP\079\GateB.ogg", opt\VoiceVolume * opt\MasterVolume)
			e\SoundCHN_IsStream = True
			
			Local e2.Events
			
			For e2.Events = Each Events
				If e2\EventID = e_gate_b_entrance Lor e2\EventID = e_gate_a_entrance Then e2\EventState3 = 1.0
				If e2\EventID = e_room2_office_3 Then e2\EventState = 1.0
			Next
			
			e\EventState2 = 2.0
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_106%(e.Events)
	; ~ EventState2: Are the magnets on
	
	If SoundTransmission Then e\SoundCHN2 = LoopSoundLocal(snd_I\RadioStatic, e\SoundCHN2)
	If (SoundTransmission Lor (e\EventState3 + fps\Factor[0] >= 2500.0)) And e\EventState = 1.0 Then e\EventState3 = Min(e\EventState3 + fps\Factor[0], 4000.0)
	
	If PlayerRoom = e\room
		Local de.Decals
		
		If e\room\NPC[0] <> Null
			If EntityY(me\Collider) < (-6400.0) * RoomScale
				ShouldPlay = 25
				me\Zone = 1
				
				Local Temp# = e\EventState2
				Local LeverState# = UpdateLever(e\room\RoomLevers[0]\OBJ, ((EntityY(e\room\Objects[1], True) < -8318.0 * RoomScale) And (EntityY(e\room\Objects[1], True) > -8603.0 * RoomScale)))
				
				If GrabbedEntity = e\room\RoomLevers[0]\OBJ And HandEntity <> 0 Then e\EventState2 = LeverState
				
				If e\EventState2 <> Temp
					If e\EventState2 = 0.0
						PlaySound_Strict(LoadTempSound("SFX\Room\106Chamber\MagnetDown.ogg"))
					Else
						PlaySound_Strict(LoadTempSound("SFX\Room\106Chamber\MagnetUp.ogg"))
					EndIf
				EndIf
				
				Temp = UpdateLever(e\room\RoomLevers[1]\OBJ)
				
				If SoundTransmission <> Temp
					If SoundTransmission
						If ChannelPlaying(e\SoundCHN) 
							ChannelVolume(e\SoundCHN, 0.0)
						Else
							e\SoundCHN = 0
						EndIf
						If ChannelPlaying(e\SoundCHN2) 
							ChannelVolume(e\SoundCHN2, 0.0)
						Else
							e\SoundCHN2 = 0
						EndIf
					Else
						ChannelVolumeEx(e\SoundCHN, opt\VoiceVolume * opt\MasterVolume)
						ChannelVolumeEx(e\SoundCHN2, opt\VoiceVolume * opt\MasterVolume)
					EndIf
					SoundTransmission = Temp
				EndIf
				
				Local x2# = EntityX(e\room\Objects[1], True)
				Local y2# = EntityY(e\room\Objects[1], True)
				Local z2# = EntityZ(e\room\Objects[1], True)
				
				If e\room\NPC[1] <> Null
					PositionEntity(e\room\NPC[1]\Collider, x2, y2, z2, True)
					ResetEntity(e\room\NPC[1]\Collider)
				EndIf
				
				If e\EventState = 0.0
					If SoundTransmission And Rand(100) = 1
						If (Not ChannelPlaying(e\SoundCHN))
							LoadEventSound(e, "SFX\Character\LureSubject\Idle" + Rand(0, 5) + ".ogg", 1)
							e\SoundCHN = PlaySound_Strict(e\Sound2, True)
						EndIf
					EndIf
					
					If SoundTransmission
						If UpdateButton(Null, e\room\Objects[0]) And mo\MouseHit1
							SetAnimTime(e\room\Objects[0], 1.0)
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
							snd_I\FemurBreakerSFX = LoadSound_Strict("SFX\Room\106Chamber\FemurBreaker.ogg")
							e\SoundCHN = PlaySound_Strict(snd_I\FemurBreakerSFX, True)
							
							e\EventState = 1.0 ; ~ Start the femur breaker
						EndIf
					EndIf
				ElseIf e\EventState = 1.0 ; ~ Bone was broken
					If SoundTransmission And e\EventState3 < 2000.0 
						If (Not ChannelPlaying(e\SoundCHN))
							LoadEventSound(e, "SFX\Character\LureSubject\Sniffling.ogg", 1)
							e\SoundCHN = PlaySound_Strict(e\Sound2, True)
						EndIf
					EndIf
					
					If e\EventState3 > 0.0 And e\EventState3 < 400.0
						AnimateEx(e\room\Objects[5], AnimTime(e\room\Objects[5]), 1.0, 120.0, 0.35, False)
					ElseIf e\EventState3 >= 2500.0
						If e\EventState2 = 1.0 And e\EventState3 - fps\Factor[0] < 2500.0
							PositionEntity(n_I\Curr106\Collider, x2, y2, z2)
							ResetEntity(n_I\Curr106\Collider)
							ShowEntity(n_I\Curr106\OBJ)
							n_I\Curr106\Contained = False
							n_I\Curr106\Idle = 0
							n_I\Curr106\State = 3.0
							n_I\Curr106\State2 = Rnd(3000.0, 3500.0)
							
							e\EventState = 2.0
							Return
						EndIf
						
						ShouldPlay = 10
						
						PositionEntity(n_I\Curr106\Collider, x2, CurveValue(-8308.0 * RoomScale + Sin(Float(MilliSec) * 0.04) * 0.07, y2, 200.0), z2, True)
						ResetEntity(n_I\Curr106\Collider)
						n_I\Curr106\Idle = 1
						
						If e\EventState3 - fps\Factor[0] < 2500.0 
							de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\NPC[0]\OBJ, True), e\room\y - 6392.0 * RoomScale, EntityZ(e\room\NPC[0]\OBJ, True), 90.0, 0.0, Rnd(360.0), 0.1, 0.01) 
							de\Timer = 90000.0 : de\AlphaChange = 0.005 : de\SizeChange = 0.003
							
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
							LoadEventSound(e, "SFX\Character\LureSubject\106Bait.ogg", 1)
							e\SoundCHN = PlaySound_Strict(e\Sound2, True)
						ElseIf e\EventState3 - fps\Factor[0] < 2900.0 And e\EventState3 >= 2900.0
							If snd_I\FemurBreakerSFX <> 0 Then FreeSound_Strict(snd_I\FemurBreakerSFX) : snd_I\FemurBreakerSFX = 0
							e\room\RoomSecurityCams[0]\PlayerState = Min(e\room\RoomSecurityCams[0]\PlayerState, 700.0)
							
							de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\RoomSecurityCams[0]\CameraOBJ, True), EntityY(e\room\RoomSecurityCams[0]\CameraOBJ, True), EntityZ(e\room\RoomSecurityCams[0]\CameraOBJ, True), 0.0, 0.0, 0.0, 0.05, 0.01) 
							de\Timer = 90000.0 : de\AlphaChange = 0.005 : de\SizeChange = 0.002
							RotateEntity(de\OBJ, EntityPitch(e\room\RoomSecurityCams[0]\CameraOBJ, True), EntityYaw(e\room\RoomSecurityCams[0]\CameraOBJ, True) + 30.0, EntityRoll(de\OBJ))
							MoveEntity(de\OBJ, 0.0, 0.05, 0.2) 
						ElseIf e\EventState3 > 3200.0
							RemoveNPC(e\room\NPC[1])
							If e\EventState2 = 1.0
								n_I\Curr106\State = 1.0
								n_I\Curr106\State2 = Rnd(22000.0, 27000.0)
								n_I\Curr106\Contained = True
							Else
								PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True))
								ResetEntity(n_I\Curr106\Collider)
								ShowEntity(n_I\Curr106\OBJ)
								n_I\Curr106\Contained = False
								n_I\Curr106\Idle = 0
								n_I\Curr106\State = 3.0
								n_I\Curr106\State2 = Rnd(3000.0, 3500.0)
								e\EventState = 2.0
								Return
							EndIf
						EndIf
					EndIf
				EndIf
				
				If e\EventState2
					PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[1], True), CurveValue(-8308.0 * RoomScale + Sin(Float(MilliSec) * 0.04) * 0.07, EntityY(e\room\Objects[1], True), 200.0), EntityZ(e\room\Objects[1], True), True)
					RotateEntity(e\room\Objects[1], Sin(Float(MilliSec) * 0.03), EntityYaw(e\room\Objects[1], True), -Sin(Float(MilliSec) * 0.025), True)
				Else
					PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[1], True), CurveValue(-8608.0 * RoomScale, EntityY(e\room\Objects[1], True), 200.0), EntityZ(e\room\Objects[1], True), True)
					RotateEntity(e\room\Objects[1], 0.0, EntityYaw(e\room\Objects[1], True), 0.0, True)
				EndIf
			EndIf
		Else
			TFormPoint(1088.0, -6222.0, 1824.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True : e\room\NPC[0]\HideFromNVG = True
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
			SetNPCFrame(e\room\NPC[0], 17.0)
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_FEMUR_BREAKER_TEXTURE)
			
			; ~ Fake Class-D inside the box
			e\room\NPC[1] = CreateNPC(NPCTypeD, 0.0, 0.0, 0.0)
			e\room\NPC[1]\State3 = -1.0 : e\room\NPC[1]\IsDead = True
			HideEntity(e\room\NPC[1]\Collider)
			HideEntity(e\room\NPC[1]\OBJ)
		EndIf
		
		Local x1# = EntityX(me\Collider, True)
		Local y1# = EntityY(me\Collider, True)
		Local z1# = EntityZ(me\Collider, True)
		
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[2]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[3]))
		ToElevatorFloor = LowerFloor
		e\EventState4 = UpdateElevators(e\EventState4, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[2], e\room\Objects[3], e)
	Else
		If (Not PlayerInReachableRoom(True))
			If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
			If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont1_895%(e.Events)
	If e\EventState < MilliSecs()
		Local sc.SecurityCams
		
		; ~ SCP-079 starts broadcasting SCP-895's camera feed on monitors after leaving the first room
		If PlayerRoom\RoomTemplate\RoomID <> r_cont1_173_intro And PlayerRoom\RoomTemplate\RoomID <> r_cont1_173
			If EntityPitch(e\room\RoomLevers[0]\OBJ, True) < 0.0 ; ~ Camera feed on
				For sc.SecurityCams = Each SecurityCams
					If sc\CoffinEffect = 0 Then sc\CoffinEffect = 2
				Next
			Else ; ~ Camera feed off
				For sc.SecurityCams = Each SecurityCams
					If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
				Next
			EndIf
		EndIf
		e\EventState = MilliSecs() + 3000
	EndIf
	
	If PlayerRoom = e\room
		Local i%, fDir#
		
		If e\room\NPC[0] = Null
			TFormPoint(321.0, -1482.8, 2416.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\IsDead = True : e\room\NPC[0]\State3 = -1.0
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_895_TEXTURE)
			SetNPCFrame(e\room\NPC[0], 40.0)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
		EndIf
		me\Zone = 1
		CoffinDistance = EntityDistance(me\Collider, e\room\Objects[0])
		If CoffinDistance < 2.0
			If e\EventState2 = 0.0
				GiveAchievement("895")
				If e\Sound = 0
					e\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardIdle" + Rand(0, 2) + ".ogg")
					e\SoundCHN = PlaySoundEx(e\Sound, Camera, e\room\OBJ)
				EndIf
				If (Not n_I\Curr106\Contained) And n_I\Curr106\State < 2.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					If e\EventState2 = 0.0
						TFormPoint(0.0, -1531.0, 2356.0, e\room\OBJ, 0)
						
						n_I\Curr106\EnemyX = TFormedX() : n_I\Curr106\EnemyY = TFormedY() : n_I\Curr106\EnemyZ = TFormedZ()
						n_I\Curr106\State = 2.0
					EndIf
				EndIf
				e\EventState2 = 1.0
			EndIf
		ElseIf CoffinDistance > 6.0 And e\EventState2 = 1.0
			If e\room\NPC[1] = Null
				FreeSound_Strict(e\Sound) : e\Sound = 0
				StopChannel(e\SoundCHN) : e\SoundCHN = 0
				
				e\room\NPC[1] = CreateNPC(NPCTypeGuard, e\room\x, e\room\y, e\room\z)
				e\room\NPC[1]\State = 8.0 : e\room\NPC[1]\PrevState = 1 : e\room\NPC[1]\GravityMult = 0.25
				LoadNPCSound(e\room\NPC[1], "SFX\Room\895Chamber\GuardScream" + Rand(0, 2) + ".ogg")
				e\room\NPC[1]\SoundCHN = PlaySoundEx(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider, 100.0, 1.0, True)
				RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\Angle + 90.0, 0.0)
				SetNPCFrame(e\room\NPC[1], 270.0)
				
				e\room\RoomDoors[0]\Open = True
				e\room\RoomDoors[1]\Open = True
			EndIf
		EndIf
		
		If e\room\NPC[1] <> Null
			If (Not e\room\NPC[1]\IsDead) Then UpdateSoundOrigin(e\room\NPC[1]\SoundCHN, Camera, e\room\NPC[1]\Collider, 100.0, 1.0, True)
			If e\room\NPC[1]\PrevState = 1
				If EntityY(e\room\NPC[1]\Collider) > ((-1531.0) * RoomScale) + e\room\NPC[1]\CollRadius
					EntityType(e\room\NPC[1]\Collider, HIT_PLAYER)
					
					Local Dist# = EntityDistanceSquared(me\Collider, e\room\NPC[1]\Collider)
					
					If Dist < 0.64 ; ~ Get the player out of the way
						Local SqrValue# = PowTwo(Sqr(Dist) - 0.8)
						
						fDir = PointDirection(EntityX(me\Collider, True), EntityZ(me\Collider, True), EntityX(e\room\NPC[1]\Collider, True), EntityZ(e\room\NPC[1]\Collider, True))
						TranslateEntity(me\Collider, Cos(-fDir + 90.0) * SqrValue, 0.0, Sin(-fDir + 90.0) * SqrValue)
					EndIf
					If EntityY(e\room\NPC[1]\Collider) > 0.6 Then EntityType(e\room\NPC[1]\Collider, 0)
				Else
					fDir = e\room\NPC[1]\Frame
					AnimateNPC(e\room\NPC[1], 270.0, 286.0, 0.5, False)
					If fDir < 275.0 And e\room\NPC[1]\Frame >= 275.0
						PlaySoundEx(LoadTempSound("SFX\Character\BodyFall.ogg"), Camera, e\room\NPC[1]\Collider)
						
						Local it.Items
						
						CreateDecal(DECAL_BLOOD_2, e\room\x, e\room\y - 1531.0 * RoomScale, e\room\z, 90.0, Rnd(360.0), 0.0, 0.4)
						
						CreateItem("Unknown Note", it_paper, e\room\x, e\room\y - 1516.0 * RoomScale, e\room\z)
						
						CreateItem("Bloody Level 3 Key Card", it_key3, e\room\x, e\room\y - 1504.0 * RoomScale, e\room\z)
						
						LoadNPCSound(e\room\NPC[1], "SFX\Room\895Chamber\GuardRadio.ogg")
					ElseIf e\room\NPC[1]\Frame > 285.9
						e\room\NPC[1]\GravityMult = 1.0
						e\room\NPC[1]\IsDead = True
						e\room\NPC[1]\PrevState = 2
					EndIf
				EndIf
			ElseIf e\room\NPC[1]\PrevState = 2
				If e\room\NPC[1]\Sound = 0 Then e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardRadio.ogg")
				e\room\NPC[1]\SoundCHN = LoopSoundEx(e\room\NPC[1]\Sound, e\room\NPC[1]\SoundCHN, Camera, e\room\NPC[1]\Collider, 5.0)
			EndIf
		EndIf
		
		If wi\NightVision > 0 Lor wi\SCRAMBLE > 0
			If wi\NVGPower > 0 And I_714\Using <> 2
				If CoffinDistance < 6.0
					TurnEntity(me\Collider, 0.0, AngleDist(PointDirection(EntityX(me\Collider, True), EntityZ(me\Collider, True), EntityX(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True)) + 90.0 + Sin(WrapAngle(e\EventState3 / 10.0)), EntityYaw(me\Collider)) / 4.0, 0.0, True)
					CameraPitch = (CameraPitch * 0.8) + (((-60.0) * Clamp((2.0 - Distance(EntityX(me\Collider, True), EntityX(e\room\Objects[0], True), EntityZ(me\Collider, True), EntityZ(e\room\Objects[0], True))) / 2.0, 0.0, 1.0)) * 0.2)
				
					me\Sanity = me\Sanity - ((fps\Factor[0] * (1.2 + (0.24 * SelectedDifficulty\OtherFactors)) / (wi\NightVision + wi\SCRAMBLE)) / (1.0 + I_714\Using))
				Else
					me\Sanity = me\Sanity - ((fps\Factor[0] * (0.4 + (0.08 * SelectedDifficulty\OtherFactors)) / (wi\NightVision + wi\SCRAMBLE)) / (1.0 + I_714\Using))
				EndIf
				me\RestoreSanity = False
				me\BlurTimer = Sin(MilliSec / 10) * Abs(me\Sanity)
				
				If me\Sanity < -1000.0
					Local StrTemp$ = "895.nvg"
					
					If wi\NightVision > 1
						StrTemp = "895.nvg.914"
					ElseIf wi\SCRAMBLE > 0
						StrTemp = "895.nvg.096"
					EndIf
					msg\DeathMsg = GetLocalString("death", StrTemp)
					EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
					If me\VomitTimer < -10.0 Then Kill()
				ElseIf me\Sanity < -800.0
					If Rand(3) = 1 Then EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
					If Rand(6) < 5
						EntityTexture(t\OverlayID[OVERLAY_NVG], mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_11)])
						For i = 0 To MaxItemAmount - 1
							If Inventory(i) <> Null
								If (wi\NightVision > 0 And (Inventory(i)\ItemTemplate\ID = it_nvg Lor Inventory(i)\ItemTemplate\ID = it_veryfinenvg Lor Inventory(i)\ItemTemplate\ID = it_finenvg)) Lor ((wi\SCRAMBLE = 1 And Inventory(i)\ItemTemplate\ID = it_scramble) Lor (wi\SCRAMBLE = 2 And Inventory(i)\ItemTemplate\ID = it_finescramble))
									If Inventory(i)\State2 = 1.0 Then PlaySound_Strict(snd_I\HorrorSFX[1])
									Inventory(i)\State2 = 2.0
									Exit
								EndIf
							EndIf
						Next
					EndIf
					me\BlurTimer = 1000.0
					If me\VomitTimer = 0.0 Then me\VomitTimer = 1.0
				ElseIf me\Sanity < -500.0
					If Rand(7) = 1 Then EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
					If Rand(50) = 1
						EntityTexture(t\OverlayID[OVERLAY_NVG], mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_11)])
						For i = 0 To MaxItemAmount - 1
							If Inventory(i) <> Null
								If (wi\NightVision > 0 And (Inventory(i)\ItemTemplate\ID = it_nvg Lor Inventory(i)\ItemTemplate\ID = it_veryfinenvg Lor Inventory(i)\ItemTemplate\ID = it_finenvg)) Lor ((wi\SCRAMBLE = 1 And Inventory(i)\ItemTemplate\ID = it_scramble) Lor (wi\SCRAMBLE = 2 And Inventory(i)\ItemTemplate\ID = it_finescramble))
									If Inventory(i)\State2 = 0.0 Then PlaySound_Strict(snd_I\HorrorSFX[0])
									Inventory(i)\State2 = 1.0
									Exit
								EndIf
							EndIf
						Next
					EndIf
				Else
					EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
					For i = 0 To MaxItemAmount - 1
						If Inventory(i) <> Null
							If (wi\NightVision > 0 And (Inventory(i)\ItemTemplate\ID = it_nvg Lor Inventory(i)\ItemTemplate\ID = it_veryfinenvg Lor Inventory(i)\ItemTemplate\ID = it_finenvg)) Lor ((wi\SCRAMBLE = 1 And Inventory(i)\ItemTemplate\ID = it_scramble) Lor (wi\SCRAMBLE = 2 And Inventory(i)\ItemTemplate\ID = it_finescramble))
								Inventory(i)\State2 = 0.0
								Exit
							EndIf
						EndIf
					Next
				EndIf
				If me\Sanity < -800.0 Then me\Sanity = -1010.0
			EndIf
		EndIf
		
		If e\EventState3 > 0.0 Then e\EventState3 = Max(e\EventState3 - fps\Factor[0], 0.0)
		If e\EventState3 = 0.0
			EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
			e\EventState3 = -1.0
		EndIf
		
		ShouldPlay = 66
		
		TurnOffSecurityCam(e\room, (Not UpdateLever(e\room\RoomLevers[0]\OBJ)))
	Else
		CoffinDistance = e\room\Dist
		If wi\NightVision > 0 Lor wi\SCRAMBLE > 0
			If CoffinDistance < 20.0 And wi\NVGPower > 0 And I_714\Using <> 2
				If me\Sanity > -600.0 me\Sanity = me\Sanity - ((fps\Factor[0] * (0.3 + (0.06 * SelectedDifficulty\OtherFactors)) / (wi\NightVision + wi\SCRAMBLE)) / (1.0 + I_714\Using))
				me\RestoreSanity = False
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_2_HCZ_106%(e.Events)
	If (Not n_I\Curr106\Contained)
		If e\EventState = 0.0
			If PlayerRoom = e\room And (Not (chs\NoTarget Lor I_268\InvisibilityOn)) Then e\EventState = 1.0
		Else
			Local FPSFactorEx# = fps\Factor[0] * 0.7
			Local de.Decals
			
			e\EventState = (e\EventState + FPSFactorEx)
			
			If (e\EventState / 250.0) > 0.3 And ((e\EventState - FPSFactorEx) / 250.0) <= 0.3
				e\SoundCHN = PlaySound_Strict(snd_I\HorrorSFX[6])
				me\BlurTimer = 800.0
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True), 0.0, e\room\Angle - 90.0, Rnd(360.0), 0.1, 0.01)
				de\SizeChange = 0.003 : de\AlphaChange = 0.005 : de\Timer = 90000.0
			EndIf
			
			If (e\EventState / 250.0) > 0.65 And ((e\EventState - FPSFactorEx) / 250.0) <= 0.65
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), 0.0, e\room\Angle + 90.0, Rnd(360.0), 0.1, 0.01)
				de\SizeChange = 0.003 : de\AlphaChange = 0.005 : de\Timer = 90000.0
			EndIf
			If e\EventState < 50.0
				n_I\Curr106\Idle = 1
				PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[0], True), 0.0, EntityZ(e\room\Objects[0], True))
				PointEntity(n_I\Curr106\Collider, e\room\Objects[1])
				MoveEntity(n_I\Curr106\Collider, 0.0, 0.0, EntityDistance(e\room\Objects[0], e\room\Objects[1]) * 0.5 * (e\EventState / 50.0))
				AnimateNPC(n_I\Curr106, 284.0, 333.0, 0.7)
			ElseIf e\EventState < 200.0
				n_I\Curr106\Idle = 1
				AnimateNPC(n_I\Curr106, 334.0, 494.0, 0.2)
				
				PositionEntity(n_I\Curr106\Collider, (EntityX(e\room\Objects[0], True) + EntityX(e\room\Objects[1], True)) / 2.0, 0.0, (EntityZ(e\room\Objects[0], True) + EntityZ(e\room\Objects[1], True)) / 2.0)
				RotateEntity(n_I\Curr106\Collider, 0.0, CurveValue(e\EventState, EntityYaw(n_I\Curr106\Collider), 30.0), 0.0, True)
				If EntityDistanceSquared(n_I\Curr106\Collider, me\Collider) < 16.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					n_I\Curr106\Idle = 0
					n_I\Curr106\State = 3.0
					n_I\Curr106\State2 = Rnd(3000.0, 3500.0)
					PlaySound_Strict(snd_I\HorrorSFX[10])
					
					RemoveEvent(e)
					Return
				EndIf
			ElseIf e\EventState < 250.0
				n_I\Curr106\Idle = 1
				PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[0], True), 0.0, EntityZ(e\room\Objects[0], True))
				PointEntity(n_I\Curr106\Collider, e\room\Objects[1])
				MoveEntity(n_I\Curr106\Collider, 0.0, 0.0, EntityDistance(e\room\Objects[0], e\room\Objects[1]) * ((e\EventState - 150.0) / 100.0))
				AnimateNPC(n_I\Curr106, 284.0, 333.0, 0.7)
			EndIf
			ResetEntity(n_I\Curr106\Collider)
			
			n_I\Curr106\SoundCHN = LoopSoundEx(snd_I\SCP106SFX[4], n_I\Curr106\SoundCHN, Camera, n_I\Curr106\Collider, 8.0, 0.8, True)
			PositionEntity(n_I\Curr106\OBJ, EntityX(n_I\Curr106\Collider), EntityY(n_I\Curr106\Collider), EntityZ(n_I\Curr106\Collider))
			RotateEntity(n_I\Curr106\OBJ, 0.0, EntityYaw(n_I\Curr106\Collider), 0.0)
			
			If e\EventState > 250.0
				PlaySoundEx(snd_I\SCP106SFX[3], Camera, n_I\Curr106\Collider, 10.0, 1.0, True)
				PlaySoundEx(snd_I\SCP106SFX[Rand(5, 7)], Camera, n_I\Curr106\Collider)
				n_I\Curr106\Idle = 0
				n_I\Curr106\State = 0.0
				n_I\Curr106\State2 = Rnd(22000.0, 27000.0)
				StopChannel(n_I\Curr106\SoundCHN) : n_I\Curr106\SoundCHN = 0
				RemoveEvent(e)
			EndIf
		EndIf
	Else
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_4_HCZ%(e.Events)
	If e\room\Dist < 6.0
		; ~ Spawn SCP-106 or SCP-173
		;[Block]
		If e\EventState <> 11.0
			If e\EventState = 10.0
				If (Not n_I\Curr106\Contained)
					If n_I\Curr106\State > 0.0
						If e\EventState2 = 0.0
							If PlayerRoom = e\room And (Not (chs\NoTarget Lor I_268\InvisibilityOn)) Then e\EventState2 = 1.0
						ElseIf e\EventState2 = 1.0
							If n_I\Curr106\State < 2.0
								TFormPoint(-864.0, -447.0, -632.0, e\room\OBJ, 0)
								n_I\Curr106\EnemyX = TFormedX() : n_I\Curr106\EnemyY = TFormedY() : n_I\Curr106\EnemyZ = TFormedZ()
								n_I\Curr106\State = 2.0
							EndIf
							e\EventState = 11.0
						EndIf
					EndIf
				Else
					e\EventState = 11.0
				EndIf
			Else
				If n_I\Curr173\Idle > 1
					e\EventState = 11.0
				Else
					If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) > 16.0 And (Not PlayerSees173(n_I\Curr173)) And PlayerRoom <> e\room
						TFormPoint(640.0, 120.0, -896.0, e\room\OBJ, 0)
						TeleportEntity(n_I\Curr173\Collider, TFormedX(), TFormedY(), TFormedZ(), n_I\Curr173\CollRadius + 0.12, True)
						n_I\Curr173\CurrentRoom = e\room
						e\EventState = 11.0
					EndIf
				EndIf
			EndIf
		EndIf
		;[End Block]
	EndIf
	
	If PlayerRoom = e\room
		Local i%, xTemp%, zTemp%
		
		; ~ Gas valves
		;[Block]
		If UpdateLever(e\room\RoomLevers[0]\OBJ)
			For i = 0 To 2
				If e\room\RoomEmitters[i] = Null
					Select i
						Case 0
							;[Block]
							xTemp = -202.0
							zTemp = -256.0
							;[End Block]
						Case 1
							;[Block]
							xTemp = -202.0
							zTemp = 0.0
							;[End Block]
						Case 2
							;[Block]
							xTemp = -202.0
							zTemp = 256.0
							;[End Block]
					End Select
					TFormPoint(xTemp, 0.0, zTemp, e\room\OBJ, 0)
					e\room\RoomEmitters[i] = SetEmitter(e\room, TFormedX(), e\room\y + 8.0 * RoomScale, TFormedZ(), 3)
					e\room\RoomEmitters[i]\State = 1
				EndIf
			Next
		Else
			For i = 0 To 2
				If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
			Next
		EndIf
		;[End Block]
	EndIf
End Function

Function UpdateEvent_Room2_5_HCZ_106%(e.Events)
	If (Not n_I\Curr106\Contained)
		Local de.Decals
		
		If e\EventState = 0.0
			If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
				If e\room\Dist < 3.0 And e\room\Dist > 0.0
					If n_I\Curr106\State < 2.0
						e\EventState = 1.0
					Else
						If n_I\Curr106\State = 3.0 And EntityDistanceSquared(n_I\Curr106\Collider, me\Collider) > 25.0 And (Not EntityInView(n_I\Curr106\OBJ, Camera))
							e\EventState2 = 1.0
							e\EventState = 1.0
						EndIf
					EndIf
				EndIf
			EndIf
		ElseIf e\EventState = 1.0
			CreateDecal(DECAL_CORROSIVE_1, EntityX(e\room\OBJ), e\room\y + 445.0 * RoomScale, EntityZ(e\room\OBJ), -90.0, Rnd(360.0), 0.0, Rnd(0.5, 0.7), Rnd(0.7, 0.85))
			
			PlaySound_Strict(snd_I\HorrorSFX[10])
			
			e\EventState = 2.0
		Else
			If e\EventState2 = 1.0 Then ShouldPlay = 10
			e\EventState = e\EventState + fps\Factor[0]
			If e\EventState <= 180.0
				n_I\Curr106\Idle = 1 : n_I\Curr106\DropSpeed = 0.0
				PositionEntity(n_I\Curr106\Collider, EntityX(e\room\OBJ, True), EntityY(me\Collider) + 1.0 - Min(Sin(e\EventState) * 1.5, 1.1), EntityZ(e\room\OBJ, True), True)
				PointEntity(n_I\Curr106\Collider, Camera)
				AnimateNPC(n_I\Curr106, 55.0, 104.0, 0.1)
				ResetEntity(n_I\Curr106\Collider)
				PositionEntity(n_I\Curr106\OBJ, EntityX(n_I\Curr106\Collider), EntityY(n_I\Curr106\Collider), EntityZ(n_I\Curr106\Collider))
				RotateEntity(n_I\Curr106\OBJ, 0.0, EntityYaw(n_I\Curr106\Collider), 0.0)
			ElseIf e\EventState > 180.0 And e\EventState < 300.0
				n_I\Curr106\State = 3.0 : n_I\Curr106\Idle = 0 : n_I\Curr106\PathTimer = 70.0 * 10.0 : n_I\Curr106\PathStatus = PATH_STATUS_NO_SEARCH : n_I\Curr106\PathLocation = 0
				PositionEntity(n_I\Curr106\Collider, EntityX(e\room\OBJ, True), -3.0, EntityZ(e\room\OBJ, True), True)
				ResetEntity(n_I\Curr106\Collider)
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, e\room\x, e\room\y + 0.005, e\room\z, 90.0, Rnd(360.0), 0.0, 0.05, 0.8)
				de\SizeChange = 0.01
				
				e\EventState = 300.0
			ElseIf e\EventState < 800.0
				If EntityY(n_I\Curr106\Collider) >= EntityY(me\Collider) - 0.05 Lor (chs\NoTarget Lor I_268\InvisibilityOn)
					RemoveEvent(e)
				Else
					TranslateEntity(n_I\Curr106\Collider, 0.0, ((EntityY(me\Collider, True) - 0.3) - EntityY(n_I\Curr106\Collider)) / 50.0, 0.0)
					If EntityY(n_I\Curr106\Collider) < -0.1 Then n_I\Curr106\CurrSpeed = 0.0
				EndIf
			Else
				RemoveEvent(e)
			EndIf
		EndIf
	Else
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_6_HCZ_173%(e.Events)
	If PlayerRoom = e\room
		If n_I\Curr173\Idle > 1
			RemoveEvent(e)
		Else
			If e\EventState = 0.0
				PlaySound_Strict(snd_I\LightOffSFX)
				
				me\LightBlink = 5.0
				e\EventState = 1.0
			EndIf
		EndIf
	EndIf
	
	If e\EventState > 0.0 And e\EventState < 200.0
		e\EventState = e\EventState + fps\Factor[0]
		
		If e\EventState > 30.0 And e\EventState - fps\Factor[0] <= 30.0
			PlaySound_Strict(LoadTempSound("SFX\Ambient\General\Ambient2.ogg"))
			EntityType(e\room\Objects[0], HIT_MAP)
		EndIf
		If e\EventState - fps\Factor[0] <= 100.0 And e\EventState > 100.0
			PlaySound_Strict(LoadTempSound("SFX\Ambient\General\Ambient5.ogg"))
			TeleportEntity(n_I\Curr173\Collider, EntityX(e\room\OBJ), e\room\y + 0.3, EntityZ(e\room\OBJ), n_I\Curr173\CollRadius + 0.12, True)
			n_I\Curr173\CurrentRoom = e\room
			n_I\Curr173\Idle = 1
			If wi\NightVision > 0 Lor wi\SCRAMBLE > 0 Then me\BlinkTimer = -10.0
		EndIf
		If e\EventState > 30.0
			If EntityY(e\room\Objects[0], True) > e\room\y + 4.0 * RoomScale
				PositionEntity(e\room\Objects[0], EntityX(e\room\Objects[0], True), Max(e\room\y + 4.0 * RoomScale, (e\room\y + 440.0 * RoomScale) - e\EventState / 30.0), EntityZ(e\room\Objects[0], True), True)
				RotateEntity(e\room\Objects[0], EntityPitch(e\room\Objects[0], True), CurveValue(30.0, EntityYaw(e\room\Objects[0], True), 200.0), EntityRoll(e\room\Objects[0], True), True)
			EndIf
		EndIf
	ElseIf e\EventState > 0.0 And me\LightBlink < 0.25
		n_I\Curr173\Idle = 0
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_6_HCZ_Smoke%(e.Events)
	If PlayerRoom = e\room
		Local emit.Emitter
		
		PlaySoundEx(snd_I\BurstSFX, Camera, e\room\OBJ)
		
		TFormPoint(0.0, 460.0, 512.0, e\room\OBJ, 0)
		emit.Emitter = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
		emit\State = 1
		
		TFormPoint(0.0, 460.0, -512.0, e\room\OBJ, 0)
		emit.Emitter = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 0)
		emit\State = 1
		
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_MT%(e.Events)
	If (e\room\Dist < 10.0 Lor PlayerRoom = e\room) And e\EventState = 0.0
		e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\457Chamber\Ambient1.ogg"))
		e\EventState = 1.0
	EndIf
	
	If PlayerRoom = e\room
		If EntityY(me\Collider, True) < -11000.0 * RoomScale
			Local n.NPCs
			Local FanSpeed# = fps\Factor[0] * 4.0
			Local i%
			
			me\Zone = 1
			ShouldPlay = 34
			
			TurnEntity(e\room\Objects[4], FanSpeed, 0.0, 0.0)
			TurnEntity(e\room\Objects[5], FanSpeed, 0.0, 0.0)
			TurnEntity(e\room\Objects[6], FanSpeed, 0.0, 0.0)
			TurnEntity(e\room\Objects[7], FanSpeed, 0.0, 0.0)
			TurnEntity(e\room\Objects[8], FanSpeed / 4.0, 0.0, 0.0)
			TurnEntity(e\room\Objects[9], FanSpeed / 4.0, 0.0, 0.0)
			
			UpdateLever(e\room\RoomLevers[0]\OBJ)
			
			Select e\EventState
				Case 1.0
					;[Block]
					For i = 0 To 1
						Select i
							Case 0
								;[Block]
								TFormPoint(941.0, -12700.0, -553.0, e\room\OBJ, 0)
								;[End Block]
							Case 1
								;[Block]
								TFormPoint(3277.0, -12700.0, 2447.0, e\room\OBJ, 0)
								;[End Block]
						End Select
						CreateNPC(NPCType966, TFormedX(), TFormedY(), TFormedZ())
					Next
					
					TFormPoint(6806.0, -12650.0, -247.0, e\room\OBJ, 0)
					n.NPCs = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
					RotateEntity(n\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
					ChangeNPCTextureID(n, NPC_CLASS_D_VICTIM_457_2_TEXTURE)
					CreateNPCAsset(n, 1)
					e\room\NPC[0] = n
					
					TFormPoint(7993.0, -12700.0, 1637.0, e\room\OBJ, 0)
					n.NPCs = CreateNPC(NPCType457, TFormedX(), TFormedY(), TFormedZ())
					e\room\NPC[1] = n
					
					TFormPoint(9259.0, -12702.0, 1729.0, e\room\OBJ, 0)
					n.NPCs = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
					n\IsDead = True : n\State3 = -1.0
					RotateEntity(n\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
					ChangeNPCTextureID(n, NPC_CLASS_D_VICTIM_457_1_TEXTURE)
					SetNPCFrame(n, 40.0)
					e\room\NPC[2] = n
					
					TFormPoint(8029.0, -12700.0, 1416.0, e\room\OBJ, 0)
					n.NPCs = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
					n\IsDead = True : n\State3 = -1.0
					RotateEntity(n\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
					ChangeNPCTextureID(n, NPC_CLASS_D_BURTON_TEXTURE)
					SetNPCFrame(n, 677.0)
					e\room\NPC[3] = n
					
					e\EventState = 2.0
					;[End Block]
				Case 2.0
					;[Block]
					If DistanceSquared(EntityX(me\Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(me\Collider, True), EntityZ(e\room\NPC[0]\Collider, True)) < 625.0
						e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\457Chamber\Ambient0.ogg"))
						e\EventState = 3.0
					EndIf
					;[End Block]
				Case 3.0
					;[Block]
					If DistanceSquared(EntityX(me\Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(me\Collider, True), EntityZ(e\room\NPC[0]\Collider, True)) < 160.0 + (125.0 * (wi\NightVision <> 0))
						e\room\NPC[0]\State = 2.0
						
						e\SoundCHN = 0
						LoadEventSound(e, "SFX\Room\457Chamber\Scream.ogg")
						
						e\EventState = 4.0
					EndIf
					;[End Block]
				Case 4.0
					;[Block]
					Local Dist# = DistanceSquared(EntityX(me\Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(me\Collider, True), EntityZ(e\room\NPC[0]\Collider, True))
					
					e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\Collider, 12.0, 1.6, True)
					If e\Sound2 = 0
						If Dist < PowTwo(fog\HideDistance) And (EntityVisible(me\Collider, e\room\NPC[0]\Collider) And EntityInView(e\room\NPC[0]\Collider, Camera))
							e\Sound2 = LoadSound_Strict("SFX\Room\457Chamber\Horror.ogg")
							e\SoundCHN2 = PlaySound_Strict(e\Sound2)
						EndIf
					EndIf
					
					TFormPoint(EntityX(e\room\NPC[0]\Collider), EntityY(e\room\NPC[0]\Collider), EntityZ(e\room\NPC[0]\Collider), 0, e\room\OBJ)
					If (Dist < 9.0) Lor (TFormedX() < 5402.0)
						SetNPCFrame(e\room\NPC[0], 41.0)
						e\room\NPC[0]\State = -1.0 : e\room\NPC[0]\State3 = 1.0
						e\room\NPC[0]\IsDead = True
						
						CreateNPCAsset(e\room\NPC[0], 1)
						CreateNPCAsset(e\room\NPC[2], 1)
						CreateNPCAsset(e\room\NPC[3], 2)
						
						PlaySoundEx(snd_I\DamageSFX[0], Camera, e\room\NPC[0]\Collider, 5.0, 0.8)
						
						e\room\NPC[1]\State = 2.0
						
						e\EventState = 5.0
					EndIf
					;[End Block]
				Case 5.0
					;[Block]
					If (Not ChannelPlaying(e\SoundCHN2))
						FreeSound_Strict(e\Sound) : e\Sound = 0
						e\SoundCHN = 0
						FreeSound_Strict(e\Sound2) : e\Sound2 = 0
						e\SoundCHN2 = 0
						
						e\EventState = 6.0
					EndIf
					;[End Block]
			End Select
		EndIf
		
		Local x1# = EntityX(me\Collider, True), y1# = EntityY(me\Collider, True), z1# = EntityZ(me\Collider, True)
		
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[2]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[3]))
		ToElevatorFloor = LowerFloor
		e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[2], e\room\Objects[0], e\room\Objects[2], e)
		e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[1], e\room\RoomDoors[3], e\room\Objects[1], e\room\Objects[3], e)
	EndIf
End Function

Function UpdateEvent_Room2_Nuke%(e.Events)
	Local i%
	
	If e\room\Dist < 6.0
		If e\room\NPC[0] = Null
			Local de.Decals
			
			TFormPoint(477.0, 51.2, 208.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_BODY_1_TEXTURE)
			SetNPCFrame(e\room\NPC[0], 40.0)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
			
			For i = 0 To 4
				TFormPoint(543.5, 120.0 + Rnd(-50.0, 50.0), 208.0 + Rnd(-50.0, 50.0), e\room\OBJ, 0)
				CreateDecal(Rand(DECAL_BULLET_HOLE_1, DECAL_BULLET_HOLE_2), TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle + 270.0, 0.0, Rnd(0.028, 0.034), 0.8)
			Next
		EndIf
	EndIf
	
	If PlayerRoom = e\room
		Local x1#, y1#, z1#
		
		If EntityY(me\Collider, True) < (-9604.0) * RoomScale
			e\EventState = UpdateLever(e\room\RoomLevers[0]\OBJ)
			UpdateLever(e\room\RoomLevers[1]\OBJ)
			
			If e\EventState = 1.0
				For i = 0 To 7
					If e\room\RoomEmitters[i] = Null
						Select i
							Case 0
								;[Block]
								x1 = 813.0
								z1 = -152.0
								;[End Block]
							Case 1
								;[Block]
								x1 = 818.0
								z1 = 147.0
								;[End Block]
							Case 2
								;[Block]
								x1 = 621.0
								z1 = 341.0
								;[End Block]
							Case 3
								;[Block]
								x1 = 327.0
								z1 = 349.0
								;[End Block]
							Case 4
								;[Block]
								x1 = 137.0
								z1 = 143.0
								;[End Block]
							Case 5
								;[Block]
								x1 = 137.0
								z1 = -155.0
								;[End Block]
							Case 6
								;[Block]
								x1 = 330.0
								z1 = -346.0
								;[End Block]
							Case 7
								;[Block]
								x1 = 635.0
								z1 = -346.0
								;[End Block]
						End Select
						y1 = -11086.0
						TFormPoint(x1, y1, z1, e\room\OBJ, 0)
						e\room\RoomEmitters[i] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 10)
					EndIf
				Next
			Else
				For i = 0 To 7
					If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
				Next
			EndIf
		Else
			If e\EventState3 = 0.0
				If e\room\RoomDoors[2]\Open Then e\EventState3 = Rand(2.0, 3.0)
			Else
				e\EventState4 = Min(e\EventState4 + fps\Factor[0], 70.0 * 9.6)
				
				If e\EventState4 > 70.0 * 2.5 And e\EventState4 - fps\Factor[0] =< 70.0 * 2.5
					TFormPoint(889.0, 120.0, 379.0 - (758.0 * (e\EventState3 = 3.0)), e\room\OBJ, 0)
					e\room\NPC[1] = CreateNPC(NPCType1048_A, TFormedX(), TFormedY(), TFormedZ())
					e\room\NPC[1]\State = -1.0
					RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\Angle + 90.0 + (180.0 * (e\EventState3 = 3.0)), 0.0, True)
					
					me\BigCameraShake = Max(1.0, me\BigCameraShake)
					PlaySoundEx(LoadTempSound("SFX\Room\Room2Nuke\Vent" + Rand(0, 2) + ".ogg"), Camera, e\room\Objects[e\EventState3], 5.0, 2.0)
					SetEmitter(Null, EntityX(e\room\Objects[e\EventState3], True), EntityY(e\room\Objects[e\EventState3], True), EntityZ(e\room\Objects[e\EventState3], True), 35)
				ElseIf e\EventState4 > 70.0 * 6.5 And e\EventState4 - fps\Factor[0] =< 70.0 * 6.5
					me\BigCameraShake = Max(1.0, me\BigCameraShake)
					PlaySoundEx(LoadTempSound("SFX\Room\Room2Nuke\Vent" + Rand(0, 2) + ".ogg"), Camera, e\room\Objects[e\EventState3], 5.0, 2.0)
					SetEmitter(Null, EntityX(e\room\Objects[e\EventState3], True), EntityY(e\room\Objects[e\EventState3], True), EntityZ(e\room\Objects[e\EventState3], True), 35)
				ElseIf e\EventState4 > 70.0 * 9.5 And e\EventState4 - fps\Factor[0] =< 70.0 * 9.5
					e\room\NPC[1]\State = 1.0 : e\room\NPC[1]\State2 = 70.0 * 2.0
					
					me\BigCameraShake = Max(1.5, me\BigCameraShake)
					PlaySoundEx(LoadTempSound("SFX\Door\DoorOpenFast.ogg"), Camera, e\room\Objects[e\EventState3], 5.0, 1.0)
					SetEmitter(Null, EntityX(e\room\Objects[e\EventState3], True), EntityY(e\room\Objects[e\EventState3], True), EntityZ(e\room\Objects[e\EventState3], True), 35)
				ElseIf e\EventState4 >= 70.0 * 9.6
					RotateEntity(e\room\Objects[e\EventState3], CurveAngle(-15.0, EntityPitch(e\room\Objects[e\EventState3], True), 15.0), e\room\Angle + (180.0 * (e\EventState3 = 3.0)), 0.0, True)
				EndIf
			EndIf
			
			Local SoundRange# = 2.0 + ((e\EventState3 = 2.0) * (e\EventState4 >= 70.0 * 9.6))
			Local SoundVol# = 0.8 + ((e\EventState3 = 2.0) * (e\EventState4 >= 70.0 * 9.6))
			
			e\SoundCHN = LoopSoundEx(snd_I\RoomAmbience[3], e\SoundCHN, Camera, e\room\Objects[2], SoundRange, SoundVol)
			e\SoundCHN2 = LoopSoundEx(snd_I\RoomAmbience[3], e\SoundCHN2, Camera, e\room\Objects[3], SoundRange, SoundVol)
		EndIf
		x1 = EntityX(me\Collider, True) : y1 = EntityY(me\Collider, True) : z1 = EntityZ(me\Collider, True)
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]))
		ToElevatorFloor = LowerFloor
		e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
	EndIf
End Function

Function UpdateEvent_Room2_Servers_HCZ%(e.Events)
	Local i%
	
	If e\EventState = 0.0
		If e\room\Dist > 0.0 And e\room\Dist < 5.0
			For i = 0 To 1
				OpenCloseDoor(e\room\RoomDoors[i])
				PlaySoundEx(snd_I\DoorClose079, Camera, e\room\RoomDoors[i]\FrameOBJ, 3.5)
				e\room\RoomDoors[i]\Locked = 1
			Next
			
			TFormPoint(-352.0, 128.0, 0.0, e\room\OBJ, 0)
			If n_I\Curr096 <> Null
				TeleportEntity(n_I\Curr096\Collider, TFormedX(), TFormedY(), TFormedZ(), n_I\Curr096\CollRadius, True)
				n_I\Curr096\CurrentRoom = e\room
			Else
				n_I\Curr096 = CreateNPC(NPCType096, TFormedX(), TFormedY(), TFormedZ())
				GiveAchievement("096")
			EndIf
			n_I\Curr096\State = 6.0 : n_I\Curr096\State2 = 70.0 * 10.0
			RotateEntity(n_I\Curr096\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
			
			LoadEventSound(e, "SFX\Character\Guard\096ServerRoom0.ogg")
			e\SoundCHN = PlaySoundEx(e\Sound, Camera, n_I\Curr096\OBJ, 12.0, 1.0, True)
			
			TFormPoint(-1328.0, 128.0, 528.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			
			GiveAchievement("096")
			
			e\EventState = 1.0
		EndIf
	ElseIf e\EventState < 70.0 * 45.0
		If Rand(50) = 1 And PlayerRoom = e\room
			me\LightBlink = Rnd(1.0, 2.0)
			If Rand(5) = 1 Then PlaySoundEx(snd_I\LightSFX[Rand(0, 2)], Camera, e\room\OBJ, 8.0, Rnd(0.1, 0.3))
		EndIf
		
		e\EventState = Min(e\EventState + fps\Factor[0], 70.0 * 40.0)
		
		If e\room\NPC[0] <> Null
			n_I\Curr096\Target = e\room\NPC[0]
			
			If e\EventState < 70.0 * 8.0
				AnimateNPC(n_I\Curr096, 472.0, 520.0, 0.25)
				PointEntity(e\room\NPC[0]\Collider, n_I\Curr096\Collider)
			ElseIf e\EventState >= 70.0 * 8.0 And e\EventState < 70.0 * 10.0
				; ~ Checking at which side the player is
				If EntityDistanceSquared(me\Collider, e\room\RoomDoors[0]\FrameOBJ) < EntityDistanceSquared(me\Collider, e\room\RoomDoors[1]\FrameOBJ)
					AnimateNPC(n_I\Curr096, 521.0, 555.0, 0.25, False)
					If n_I\Curr096\Frame >= 554.5
						SetNPCFrame(n_I\Curr096, 677.0)
						n_I\Curr096\State = 2.0
						TurnEntity(n_I\Curr096\Collider, 0.0, 180.0, 0.0)
						MoveEntity(n_I\Curr096\Collider, 0.0, 0.0, 0.3)
						e\EventState = 70.0 * 10.0
					EndIf
				Else
					AnimateNPC(n_I\Curr096, 556.0, 590.0, 0.25, False)
					If n_I\Curr096\Frame >= 589.5
						SetNPCFrame(n_I\Curr096, 677.0)
						n_I\Curr096\State = 2.0
						TurnEntity(n_I\Curr096\Collider, 0.0, 180.0, 0.0)
						MoveEntity(n_I\Curr096\Collider, 0.0, 0.0, 0.3)
						e\EventState = 70.0 * 10.0
					EndIf
				EndIf
				PointEntity(e\room\NPC[0]\Collider, n_I\Curr096\Collider)
			ElseIf e\EventState >= 70.0 * 10.0 And e\EventState < 70.0 * 20.0
				n_I\Curr096\State = Clamp(n_I\Curr096\State, 2.0, 4.0)
				n_I\Curr096\State2 = Max(n_I\Curr096\State2, 70.0 * 12.0)
				If e\EventState - fps\Factor[0] <= 70.0 * 15.0 ; ~ Walk to the doorway
					If e\EventState > 70.0 * 15.0
						e\room\NPC[0]\State = 14.0
						e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(n_I\Curr096\Collider, True), 0.4, EntityZ(n_I\Curr096\Collider, True))
						e\room\NPC[0]\PathTimer = 300.0
					Else
						PointEntity(e\room\NPC[0]\Collider, n_I\Curr096\Collider)
					EndIf
				EndIf
				If EntityVisible(e\room\NPC[0]\Collider, n_I\Curr096\Collider)
					e\room\NPC[0]\State = 13.0
					PointEntity(e\room\NPC[0]\OBJ, n_I\Curr096\Collider)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 30.0), 0.0)
				EndIf
			Else
				If n_I\Curr096\State = 5.0 ; ~ Shoot at SCP-096 when it starts attacking
					n_I\Curr096\LastSeen = 1.0
					e\room\NPC[0]\State = 2.0
					PointEntity(e\room\NPC[0]\OBJ, n_I\Curr096\Collider)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 30.0), 0.0)
					If PlayerRoom = e\room Then me\LightBlink = (e\room\NPC[0]\Reload) + Rnd(0.5, 2.0)
					n_I\Curr096\Target = e\room\NPC[0]
				Else
					If e\EventState > 70.0 * 22.0 Then n_I\Curr096\State = 5.0
					If e\room\NPC[0]\State = 13.0
						e\room\NPC[0]\State = 14.0
						e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\OBJ, True), 0.4, EntityZ(e\room\OBJ, True))
						e\room\NPC[0]\PathTimer = 300.0
						e\room\NPC[0]\Speed = e\room\NPC[0]\Speed * 1.5 ; ~ Making the guard walking a bit faster
					EndIf
				EndIf
			EndIf
			
			If n_I\Curr096\Frame > 25.0 And n_I\Curr096\Frame < 150.0
				LoadEventSound(e, "SFX\Character\Guard\096ServerRoom1.ogg")
				e\SoundCHN = PlaySoundEx(e\Sound, Camera, n_I\Curr096\OBJ, 12.0, 1.0, True)
				
				ChangeNPCTextureID(n_I\Curr096, NPC_096_BLOODY_TEXTURE)
				
				n_I\Curr096\CurrSpeed = 0.0
				
				Local CosValue# = Cos(e\room\Angle) * RoomScale
				Local SinValue# = Sin(e\room\Angle) * RoomScale
				Local de.Decals, it.Items
				Local Temp2#
				
				For i = 0 To 6
					If e\room\Angle = 0.0 Lor e\room\Angle = 180.0
						Temp2 = Rnd(197.0, 199.0) * CosValue
						de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), e\room\x - Temp2, e\room\y + 1.0, e\room\z + (140.0 * (i - 3)) * RoomScale, 0.0, e\room\Angle + 90.0, Rnd(360.0), Rnd(0.8, 0.85))
						de\SizeChange = 0.001
						
						de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), e\room\x - Temp2, e\room\y + 1.0, e\room\z + (140.0 * (i - 3)) * RoomScale, 0.0, e\room\Angle - 90.0, Rnd(360.0), Rnd(0.8, 0.85))
						de\SizeChange = 0.001
						
					Else
						Temp2 = Rnd(197.0, 199.0) * SinValue - Rnd(0.001, 0.003)
						de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), e\room\x + (140.0 * (i - 3)) * RoomScale, e\room\y + 1.0, e\room\z - Temp2, 0.0, e\room\Angle + 90.0, Rnd(360.0), Rnd(0.8, 0.85))
						de\SizeChange = 0.001
						
						de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), e\room\x + (140.0 * (i - 3)) * RoomScale, e\room\y + 1.0, e\room\z - Temp2, 0.0, e\room\Angle - 90.0, Rnd(360.0), Rnd(0.8, 0.85))
						de\SizeChange = 0.001
					EndIf
					CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), EntityX(e\room\NPC[0]\Collider) + Rnd(-2.0, 2.0), e\room\y + 0.005, EntityZ(e\room\NPC[0]\Collider) + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0)
				Next
				CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), EntityX(e\room\NPC[0]\Collider), e\room\y + 0.005, EntityZ(e\room\NPC[0]\Collider), 90.0, Rnd(360.0), 0.0)
				
				StopStream_Strict(n_I\Curr096\SoundCHN) : n_I\Curr096\SoundCHN = 0 : n_I\Curr096\SoundCHN_IsStream = False
				
				ShowEntity(e\room\Objects[0])
				
				CreateItem("Bloody Level 3 Key Card", it_key3, EntityX(e\room\NPC[0]\Collider), EntityY(e\room\NPC[0]\Collider) + 0.1, EntityZ(e\room\NPC[0]\Collider))
				
				RemoveNPC(e\room\NPC[0]) : e\room\NPC[0] = Null
				
				n_I\Curr096\State = 1.0
			EndIf
		Else
			If e\EventState >= 70.0 * 35.0 And e\EventState - fps\Factor[0] < 70.0 * 35.0 ; ~ Open them again to let the player in
				For i = 0 To 1
					OpenCloseDoor(e\room\RoomDoors[i])
					PlaySoundEx(snd_I\DoorOpen079, Camera, e\room\RoomDoors[i]\FrameOBJ, 3.5)
				Next
				If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
				Local s.Screens
				
				For s.Screens = Each Screens
					If s\room\RoomTemplate\RoomID <> r_cont1_079 Then s\Display096 = True
				Next
			EndIf
			
			
			If PlayerRoom = e\room
				If ChannelPlaying(e\SoundCHN)
					me\LightBlink = Rnd(0.5, 6.0)
					If Rand(50) = 1 Then PlaySoundEx(snd_I\LightSFX[Rand(0, 2)], Camera, e\room\OBJ, 8.0, Rnd(0.1, 0.3))
				EndIf
				
				If e\room\Angle = 0.0 Lor e\room\Angle = 180.0 ; ~ Lock the player inside
					If (Not IsEqual(EntityX(me\Collider, True), EntityX(e\room\OBJ, True), 1.12))
						If EntityDistanceSquared(me\Collider, e\room\RoomDoors[0]\FrameOBJ) > 2.0 And EntityDistanceSquared(me\Collider, e\room\RoomDoors[1]\FrameOBJ) > 2.0
							If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
							e\EventState = 70.0 * 45.0
						EndIf
					EndIf
				Else
					If (Not IsEqual(EntityZ(me\Collider, True), EntityZ(e\room\OBJ, True), 1.12))
						If EntityDistanceSquared(me\Collider, e\room\RoomDoors[0]\FrameOBJ) > 2.0 And EntityDistanceSquared(me\Collider, e\room\RoomDoors[1]\FrameOBJ) > 2.0
							If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
							e\EventState = 70.0 * 45.0
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		UpdateSoundOrigin(e\SoundCHN, Camera, n_I\Curr096\OBJ, 12.0, 1.0, True)
	ElseIf PlayerRoom = e\room
		Local x% = UpdateLever(e\room\RoomLevers[1]\OBJ) ; ~ Fuel pump
		Local y% = UpdateLever(e\room\RoomLevers[0]\OBJ) ; ~ Power switch
		Local z% = UpdateLever(e\room\RoomLevers[2]\OBJ) ; ~ Generator
		
		; ~ Fuel pump on
		If x
			e\EventState2 = Min(1.0, e\EventState2 + fps\Factor[0] / 350.0)
			
			; ~ Generator on
			If z
				If e\Sound2 = 0 Then e\Sound2 = LoadSound_Strict("SFX\Room\GeneratorOn.ogg")
				e\EventState3 = Min(1.0, e\EventState3 + fps\Factor[0] / 450.0)
			Else
				e\EventState3 = Min(0.0, e\EventState3 - fps\Factor[0] / 450.0)
			EndIf
		Else
			e\EventState2 = Max(0.0, e\EventState2 - fps\Factor[0] / 350.0)
			e\EventState3 = Max(0.0, e\EventState3 - fps\Factor[0] / 450.0)
		EndIf
		
		If e\EventState2 > 0.0 Then e\SoundCHN = LoopSoundEx(snd_I\RoomAmbience[7], e\SoundCHN, Camera, e\room\RoomLevers[2]\BaseOBJ, 5.0, e\EventState2 * 0.8)
		If e\EventState3 > 0.0 Then e\SoundCHN2 = LoopSoundEx(e\Sound2, e\SoundCHN2, Camera, e\room\RoomLevers[1]\BaseOBJ, 6.0, e\EventState3)
		
		If y And x And z
			For i = 0 To 1
				e\room\RoomDoors[i]\Locked = 0
			Next
		Else
			If Rand(50) = 1 Then me\LightBlink = Rnd(0.5, 1.0)
			
			For i = 0 To 1
				If e\room\RoomDoors[i]\Open
					OpenCloseDoor(e\room\RoomDoors[i])
					e\room\RoomDoors[i]\Locked = 1
				EndIf
			Next
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Shaft%(e.Events)
	If PlayerRoom = e\room
		Local i%
		
		If UpdateButton(Null, e\room\Objects[1]) And mo\MouseHit1
			SetAnimTime(e\room\Objects[1], 1.0)
			CreateMsg(GetLocalString("msg", "elev.broken"))
			PlaySound_Strict(ButtonSFX[1])
			mo\MouseHit1 = False
		EndIf
		
		Local ElevatorRotation# = Sin(Float(MilliSec * 0.05)) * 3.0
		
		RotateEntity(e\room\Objects[2], 0.0, e\room\Angle + 90.0 + ElevatorRotation, 0.0, True)
		For i = 3 To 5
			RotateEntity(e\room\Objects[i], 0.0, e\room\Angle + ElevatorRotation * 10.0, 0.0)
		Next
		For i = 6 To 8
			RotateEntity(e\room\Objects[i], 0.0, e\room\Angle + ElevatorRotation * 10.0, 0.0)
		Next
		If AmbientSFX(3, 9) = 0
			AmbientSFX(3, 9) = LoadSound_Strict("SFX\Ambient\General\Ambient9.ogg")
		Else
			e\SoundCHN = LoopSoundEx(AmbientSFX(3, 9), e\SoundCHN, Camera, e\room\Objects[2], 10.0, 1.5)
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Test_HCZ%(e.Events)
	If e\EventState = 0.0
		If PlayerRoom = e\room
			Local Temp%
			
			TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0.0, e\room\OBJ)
			If TFormedZ() = 0.0
				Temp = -1
			Else
				Temp = -Sgn(TFormedZ())
			EndIf
			TFormPoint(-720.0, 57.0, 816.0 * Temp, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCType1048, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State = 1.0
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle - 90.0, 0.0)
			
			e\EventState = 1.0
		EndIf
	ElseIf e\EventState = 1.0
		e\EventState2 = e\EventState2 + fps\Factor[0]
		If e\EventState2 > 70.0 * 10.5 Lor e\room\RoomDoors[0]\Open
			RemoveNPC(e\room\NPC[0])
			e\EventState = 2.0
		EndIf
	EndIf
	
	If PlayerRoom = e\room
		If e\EventState = 2.0
			If EntityDistanceSquared(me\Collider, e\room\Objects[6]) < 6.25 And e\EventState > 0.0
				Local emit.Emitter
				Local i%
				
				PlaySound_Strict(LoadTempSound("SFX\SCP\079\TestroomWarning.ogg"), True)
				For i = 0 To 5
					emit.Emitter = SetEmitter(e\room, EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True), 4)
					emit\State = 1
				Next
				If e\room\RoomDoors[1]\Open
					PlaySoundEx(snd_I\DoorClose079, Camera, e\room\RoomDoors[1]\FrameOBJ, 7.0)
					OpenCloseDoor(e\room\RoomDoors[1])
				EndIf
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont2_008%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs
		Local i%
		
		If EntityY(me\Collider) < (-8900.0) * RoomScale
			ShouldPlay = 30
			me\Zone = 1
			
			GiveAchievement("008")
			If e\EventState = 0.0
				If n_I\Curr173\Idle = 0 And EntityDistanceSquared(n_I\Curr173\Collider, me\Collider) > 36.0 ; ~ Just making sure that SCP-173 is far away enough to spawn him to this room
					TeleportEntity(n_I\Curr173\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True), n_I\Curr173\CollRadius + 0.12, True)
					n_I\Curr173\CurrentRoom = e\room
				EndIf
				e\EventState = 1.0
			ElseIf e\EventState = 1.0
				If e\EventState2 = 0.0
					For i = 2 To 3
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Next
				EndIf
				e\SoundCHN = LoopSoundEx(snd_I\RoomAmbience[5], e\SoundCHN, Camera, e\room\Objects[0], 5.0)
				
				UpdateRedLight(e\room\RoomLights[0], 1500, 800)
				
				If EntityDistanceSquared(me\Collider, e\room\Objects[0]) < 5.0
					For i = 0 To 1
						e\room\RoomDoors[i]\Locked = 1
					Next
					
					If e\EventState2 = 0.0
						If EntityDistanceSquared(n_I\Curr173\Collider, e\room\Objects[4]) < 9.0
							If (Not PlayerSees173(n_I\Curr173)) And ((Not EntityInView(e\room\Objects[2], Camera)) Lor (me\BlinkTimer < -6.0 And me\BlinkTimer > -16.0))
								; ~ SCP-173's attack point
								TFormPoint(-448.0, -9337.0, 752.0, e\room\OBJ, 0)
								PositionEntity(n_I\Curr173\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
								ResetEntity(n_I\Curr173\Collider)
								
								For i = 2 To 3
									HideEntity(e\room\Objects[i])
								Next
								
								If wi\HazmatSuit = 0
									PlaySound_Strict(LoadTempSound("SFX\SCP\008\IamInfected.ogg"))
									InjurePlayer(0.3, 0.001, 500.0)
									CreateMsg(GetLocalString("msg", "008.173"))
								EndIf
								e\SoundCHN2 = PlaySoundEx(LoadTempSound("SFX\Room\GlassBreak.ogg"), Camera, e\room\Objects[0]) 
								
								e\EventState2 = 1.0
							EndIf
						EndIf
					EndIf
					
					If InteractObject(e\room\Objects[1], 0.8, 1)
						DrawArrowIcon[2] = True
						RotateEntity(e\room\Objects[1], Clamp(EntityPitch(e\room\Objects[1], True) + Clamp(-mo\Mouse_Y_Speed_1, -10.0, 10.0), 35.0, 89.0), EntityYaw(e\room\Objects[1], True), 0.0, True)
					Else
						RotateEntity(e\room\Objects[1], EntityPitch(e\room\Objects[1], True) + Min(Cos(MilliSec) * 0.1, 89.0), EntityYaw(e\room\Objects[1], True), 0.0, True)
					EndIf
					If I_008\Timer = 0.0
						If me\Bloodloss > 0.0 Then InjurePlayer(0.0, 0.001)
					EndIf
					If wi\GasMask = 0 And wi\HazmatSuit = 0 Then me\EyeIrritation = Max(70.0, me\EyeIrritation)
				EndIf
				
				If EntityPitch(e\room\Objects[1], True) < 40.0
					PlaySoundEx(snd_I\LeverSFX, Camera, e\room\Objects[1], 2.0)
					FreeEmitter(e\room\RoomEmitters[0])
					e\EventState = 2.0
				Else
					If e\room\RoomEmitters[0] = Null
						e\room\RoomEmitters[0] = SetEmitter(e\room, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), 9)
						e\room\RoomEmitters[0]\State = 2
					EndIf
				EndIf
			Else
				If (Not EntityHidden(e\room\RoomLights[0]\OBJ)) Then HideEntity(e\room\RoomLights[0]\OBJ)
				If e\EventState2 < 2.0
					For i = 0 To 3
						e\room\RoomDoors[i]\Locked = 0
					Next
					OpenCloseDoor(e\room\RoomDoors[2])
					
					TFormPoint(-240.0, -9405.0, -635.0, e\room\OBJ, 0)
					n.NPCs = CreateNPC(NPCType008_1, TFormedX(), TFormedY(), TFormedZ())
					n\State = 3.0
					
					n.NPCs = CreateNPC(NPCType008_1, e\room\x, -250.0, e\room\z)
					ChangeNPCTextureID(n, NPC_008_1_TEXTURE)
					n\State = 3.0 : n\IdleTimer = 70.0 * -30.0
					
					n.NPCs = CreateNPC(NPCType008_1, e\room\x, -250.0, e\room\z)
					ChangeNPCTextureID(n, NPC_008_1_TEXTURE_2)
					n\State = 3.0 : n\IdleTimer = 70.0 * -60.0
					
					e\EventState2 = 2.0
				EndIf
				RotateEntity(e\room\Objects[1], CurveAngle(0.0, EntityPitch(e\room\Objects[1], True), 15.0), EntityYaw(e\room\Objects[1], True), 0.0, True)
			EndIf
		ElseIf e\EventState = 1.0
			e\EventState = 0.0
		EndIf
		
		Local x1# = EntityX(me\Collider, True), y1# = EntityY(me\Collider, True), z1# = EntityZ(me\Collider, True)
		
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[5]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[6]))
		ToElevatorFloor = LowerFloor
		e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[4], e\room\RoomDoors[5], e\room\Objects[5], e\room\Objects[6], e)
	EndIf
End Function

Function UpdateEvent_Cont2_049%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs, it.Items
		Local x1#, y1#, z1#
		
		
		If EntityY(me\Collider) > (-2848.0) * RoomScale
			x1 = EntityX(me\Collider, True) : y1 = EntityY(me\Collider, True) : z1 = EntityZ(me\Collider, True)
			me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[2]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[3]))
			ToElevatorFloor = LowerFloor
			e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
			e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
		Else
			ShouldPlay = 24
			me\Zone = 1
			
			If e\EventState = 0.0
				TFormPoint(528.0, -3440.0, 96.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCType049_2, TFormedX(), TFormedY(), TFormedZ())
				PointEntity(n\Collider, e\room\OBJ)
				TurnEntity(n\Collider, 0.0, e\room\Angle, 0.0)
				
				TFormPoint(64.0, -3440.0, -1000.0, e\room\OBJ, 0)
				n.NPCs = CreateNPC(NPCType049_2, TFormedX(), TFormedY(), TFormedZ())
				PointEntity(n\Collider, e\room\OBJ)
				TurnEntity(n\Collider, 0.0, e\room\Angle + 60.0, 0.0)
				
				TFormPoint(528.0, -2672.0, 96.0, e\room\OBJ, 0)
				If n_I\Curr049 <> Null
					If n_I\Curr049\State <> 66.0
						e\room\NPC[0] = n_I\Curr049
						e\room\NPC[0]\State = 2.0 : e\room\NPC[0]\Idle = 1 : e\room\NPC[0]\HideFromNVG = True
						TeleportEntity(e\room\NPC[0]\Collider, TFormedX(), TFormedY(), TFormedZ(), e\room\NPC[0]\CollRadius, True)
						e\room\NPC[0]\CurrentRoom = e\room
						PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
					EndIf
				Else
					n_I\Curr049 = CreateNPC(NPCType049, TFormedX(), TFormedY(), TFormedZ())
					n_I\Curr049\State = 2.0 : n_I\Curr049\Idle = 1 : n_I\Curr049\HideFromNVG = True
					PointEntity(n_I\Curr049\Collider, e\room\OBJ)
					e\room\NPC[0] = n_I\Curr049
				EndIf
				
				e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\Blackout.ogg"))
				If EntityDistanceSquared(e\room\Objects[1], me\Collider) < EntityDistanceSquared(e\room\Objects[3], me\Collider)
					TFormPoint(2720.0, -3516.0, 1824.0, e\room\OBJ, 0)
					CreateItem("Research Sector-02 Scheme", it_paper, TFormedX(), TFormedY(), TFormedZ())
				Else
					TFormPoint(-2720.0, -3516.0, -1824.0, e\room\OBJ, 0)
					CreateItem("Research Sector-02 Scheme", it_paper, TFormedX(), TFormedY(), TFormedZ())
				EndIf
				
				e\EventState = 1.0
			ElseIf e\EventState > 0.0
				Local PrevGenLever% = (EntityPitch(e\room\RoomLevers[1]\OBJ, True) < 0.0)
				Local z2% = UpdateLever(e\room\RoomLevers[0]\OBJ) ; ~ Power feed
				Local x2% = UpdateLever(e\room\RoomLevers[1]\OBJ) ; ~ Generator
				Local i%
				
				e\room\RoomDoors[1]\Locked = 1
				e\room\RoomDoors[3]\Locked = 1
				e\room\RoomDoors[1]\IsElevatorDoor = 0
				e\room\RoomDoors[3]\IsElevatorDoor = 0
				
				If PrevGenLever <> x2
					If x2
						PlaySound_Strict(snd_I\TeslaPowerUpSFX)
					Else
						PlaySound_Strict(snd_I\LightOffSFX)
					EndIf
				EndIf
				
				UpdateButton(Null, e\room\Objects[5])
				
				If e\EventState4 > 0.0
					e\EventState4 = e\EventState4 + fps\Factor[0]
					EntityTexture(e\room\Objects[5], d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE])
					If e\EventState4 > 350.0
						If e\room\RoomEmitters[0] <> Null Then FreeEmitter(e\room\RoomEmitters[0])
						EntityTexture(e\room\Objects[5], d_I\ButtonTextureID[BUTTON_GREEN_TEXTURE])
						e\EventState4 = 0.0
					EndIf
					UpdateEntityMaterial(e\room\Objects[5])
				ElseIf d_I\ClosestButton = e\room\Objects[5] And mo\MouseHit1 And x2
					SetAnimTime(e\room\Objects[5], 1.0)
					PlaySound_Strict(ButtonSFX[0])
					mo\MouseHit1 = False
					If e\EventState4 = 0.0
						If opt\ParticleAmount > 0
							e\room\RoomEmitters[0] = SetEmitter(e\room, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True), 18)
							e\room\RoomEmitters[0]\State = 3
						EndIf
						e\EventState4 = 0.01
					EndIf
				EndIf
				
				If e\EventState >= 70.0
					fog\FarDist = 6.0
					If x2
						ShouldPlay = 8
						IsBlackOut = False
						e\SoundCHN2 = LoopSoundEx(snd_I\RoomAmbience[7], e\SoundCHN2, Camera, e\room\Objects[4], 6.0)
						For i = 4 To 7
							e\room\RoomDoors[i]\Locked = 0
						Next
						e\EventState = Max(e\EventState, 70.0 * 180.0)
					Else
						IsBlackOut = True
						If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
						For i = 4 To 7
							e\room\RoomDoors[i]\Locked = 1
						Next
					EndIf
				Else
					e\EventState = Min(e\EventState + fps\Factor[0], 70.0)
				EndIf
				
				If z2 And x2
					e\room\RoomDoors[1]\Locked = 0
					e\room\RoomDoors[3]\Locked = 0
					x1 = EntityX(me\Collider, True) : y1 = EntityY(me\Collider, True) : z1 = EntityZ(me\Collider, True)
					me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[2]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[3]))
					ToElevatorFloor = LowerFloor
					e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
					e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
					
					If e\room\NPC[0] <> Null
						If e\room\NPC[0]\HideFromNVG
							i = 0
							If EntityDistanceSquared(me\Collider, e\room\RoomDoors[1]\FrameOBJ) < 9.0
								i = 1
							ElseIf EntityDistanceSquared(me\Collider, e\room\RoomDoors[3]\FrameOBJ) < 9.0
								i = 3
							EndIf
							If i > 0
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True))
								ResetEntity(e\room\NPC[0]\Collider)
								GiveAchievement("049")
								PlaySoundEx(snd_I\ElevatorBeepSFX, Camera, e\room\Objects[i], 4.0)
								e\room\RoomDoors[i]\Locked = 0
								OpenCloseDoor(e\room\RoomDoors[i])
								e\room\RoomDoors[i - 1]\Open = False
								e\room\RoomDoors[i]\Open = True
								e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
								LoadNPCSound(e\room\NPC[0], "SFX\SCP\049\DetectedInChamber.ogg", 1)
								e\room\NPC[0]\SoundCHN2 = LoopSoundEx(e\room\NPC[0]\Sound2, e\room\NPC[0]\SoundCHN2, Camera, e\room\NPC[0]\OBJ, 10.0, 1.0, True)
								e\room\NPC[0]\Idle = 0 : e\room\NPC[0]\HideFromNVG = False : e\room\NPC[0]\PrevState = 2 : e\room\NPC[0]\State = 2.0
							EndIf
						EndIf
					EndIf
				EndIf
				
				If e\EventState < 70.0 * 190.0
					If e\EventState >= 70.0 * 180.0
						For i = 0 To 2 Step 2
							e\room\RoomDoors[i]\Open = True
							e\room\RoomDoors[i + 1]\Open = False
						Next
						e\EventState = 70.0 * 190.0
					EndIf
				ElseIf e\EventState < 70.0 * 240.0
					For n.NPCs = Each NPCs ; ~ Awake the zombies
						If n\NPCType = NPCType049_2 And n\State = 0.0
							SetNPCFrame(n, 155.0)
							n\State = 1.0
						EndIf
					Next
					e\EventState = 70.0 * 241.0
				EndIf
			ElseIf e\room\NPC[0] <> Null
				If e\EventState > (-70.0) * 4.0
					If me\FallTimer < -230.0
						me\BlinkTimer = -10.0 : me\FallTimer = -231.0
						
						e\EventState = e\EventState - fps\Factor[0]
						
						If e\EventState <= (-70.0) * 4.0
							HideEntity(me\Head)
							ShowEntity(me\Collider)
							
							I_268\Using = 0 : wi\GasMask = 0 : wi\BallisticHelmet = 0
							wi\NightVision = 0 : wi\SCRAMBLE = 0
							
							me\Zombie = True
							
							msg\DeathMsg = GetLocalString("death", "0492")
							
							me\DropSpeed = 0.0 : me\BlinkTimer = -10.0 : I_008\Timer = 0.0 : I_409\Timer = 0.0 : me\FallTimer = 0.0
							
							PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), True)
							ResetEntity(e\room\NPC[0]\Collider)
							
							For n.NPCs = Each NPCs
								If n\NPCType = NPCType049_2 Then n\HP = 0
							Next
							
							TFormPoint(64.0, -3388.8, 1000.0, e\room\OBJ, 0)
							n.NPCs = CreateNPC(NPCTypeMTF, TFormedX(), TFormedY(), TFormedZ())
							n\State = MTF_SHOOTING_AT_PLAYER : n\Reload = 70.0 * 6.0
							e\room\NPC[1] = n
							
							PositionEntity(me\Collider, EntityX(e\room\OBJ, True), e\room\y - (3388.8 * RoomScale), EntityZ(e\room\OBJ, True))
							ResetEntity(me\Collider)
							PointEntity(me\Collider, e\room\NPC[1]\Collider)
							
							PlaySound_Strict(LoadTempSound("SFX\Character\MTF\049_2\Spotted.ogg"), True)
							
							LoadEventSound(e, "SFX\SCP\049_2\Breath.ogg")
						EndIf
					EndIf
				Else
					me\BlurTimer = 800.0 : me\ForceMove = 0.5 : me\Injuries = Max(2.0, me\Injuries) : me\Bloodloss = 0.0
					
					Local Pvt% = CreatePivot()
					
					PositionEntity(Pvt, EntityX(e\room\NPC[1]\Collider), EntityY(e\room\NPC[1]\Collider) + 0.2, EntityZ(e\room\NPC[1]\Collider))
					PointEntity(me\Collider, Pvt)
					PointEntity(Camera, Pvt, EntityRoll(Camera))
					FreeEntity(Pvt) : Pvt = 0
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont2_409%(e.Events)
	If PlayerRoom = e\room
		If EntityY(me\Collider) < (-1774.0) * RoomScale
			Local it.Items
			Local i%
			
			ShouldPlay = 27
			me\Zone = 1
			
			If e\EventState = 0.0
				; ~ Spawn some stuff
				;[Block]
				TFormPoint(-2251.8, -2455.8, 3513.0, e\room\OBJ, 0)
				
				Local x2# = TFormedX(), y2# = TFormedY(), z2# = TFormedZ()
				
				e\room\NPC[0] = CreateNPC(NPCTypeD, x2, y2, z2)
				e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
				ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_409_TEXTURE)
				SetNPCFrame(e\room\NPC[0], 19.0)
				RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle, 0.0, True)
				EntityShininess(e\room\NPC[0]\OBJ, 1.0, 0.5)
				
				CreateDecal(DECAL_409, x2, y2 - (56.2 * RoomScale) + 0.005, z2, 90.0, Rnd(360.0), 0.0, 0.85, 0.8, 1)
				
				If I_005\ChanceToSpawn = 2
					TFormPoint(-2408.0, -2351.0, 3304.0, e\room\OBJ, 0)
					CreateItem("Crystallized SCP-005", it_crystal005, TFormedX(), TFormedY(), TFormedZ())
				EndIf
				
				TFormPoint(-1483.0, -2351.0, 3984.0, e\room\OBJ, 0)
				it.Items = CreateItem("Document SCP-409", it_paper, TFormedX(), TFormedY(), TFormedZ())
				RotateEntity(it\Collider, 0.0, 0.0, 0.0)
				
				e\EventState = 1.0
				;[End Block]
			Else
				; ~ Getting SCP-409 crystallization
				;[Block]
				If I_409\Timer = 0.0
					If EntityDistanceSquared(me\Collider, e\room\NPC[0]\Collider) < 0.81
						GiveAchievement("409")
						If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0
							me\BlurTimer = 1000.0
							I_409\Timer = 0.001
						EndIf
					EndIf
					
					; ~ Touching SCP-409
					If InteractObject(e\room\Objects[2], 0.8)
						GiveAchievement("409")
						If (Not I_427\Using) And I_427\Timer < 70.0 * 360.0
							CreateMsg(GetLocalString("msg", "409"))
							me\BlurTimer = 1000.0
							I_409\Timer = 0.001
						Else
							CreateMsg(GetLocalString("msg", "409.nothappened"))
						EndIf
					EndIf
				EndIf
				;[End Block]
				
				; ~ Update spark particles
				;[Block]
				If EntityDistanceSquared(me\Collider, e\room\Objects[3]) < 25.0
					If Rand(50) = 1
						SetTemplateVelocity(ParticleEffect[19], -0.007, 0.008, -0.001, 0.0012, -0.007, -0.008)
						SetEmitter(e\room, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), 19)
						PlaySoundEx(snd_I\SparkShortSFX, Camera, e\room\Objects[3], 3.0, 0.4)
					EndIf
				EndIf
				;[End Block]
				
				; ~ Update shine particles
				;[Block]
				If EntityDistanceSquared(me\Collider, e\room\Objects[2]) < 25.0
					If Rand(8) = 1
						For i = 0 To 1
							Select i
								Case 0
									;[Block]
									TFormPoint(-2206.0 + Rnd(-60.0, 60.0), -2426.0, 3061.0 + Rnd(-60.0, 60.0), e\room\OBJ, 0)
									;[End Block]
								Case 1
									;[Block]
									TFormPoint(-2256.0 + Rnd(-60.0, 60.0), -2426.0, 2919.0 + Rnd(-60.0, 60.0), e\room\OBJ, 0)
									;[End Block]
							End Select
							If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -2.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY(), PickedZ(), 32)
						Next
					EndIf
				EndIf
				;[End Block]
				
				; ~ Update incinerator
				;[Block]
				If UpdateLever(e\room\RoomLevers[0]\OBJ)
					If e\EventState4 = 0.0
						If e\EventState3 > 70.0 * 2.0 And e\EventState3 < 70.0 * 6.99
							If e\Sound = 0
								e\Sound = LoadSound_Strict("SFX\Room\Laser.ogg")
							Else
								e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\Objects[5], 2.0)
								e\SoundCHN2 = LoopSoundEx(e\Sound, e\SoundCHN2, Camera, e\room\Objects[6], 2.0)
							EndIf
						EndIf
						
						If e\EventState3 = 0.0
							If EntityDistanceSquared(me\Collider, e\room\Objects[4]) < 0.64 And (Not chs\NoTarget)
								For i = 2 To 3
									e\room\RoomDoors[i]\FastOpen = True
									OpenCloseDoor(e\room\RoomDoors[i])
								Next
								PlaySound_Strict(snd_I\AlarmSFX[2])
								PlaySound_Strict(LoadTempSound("SFX\Alarm\ScanInProgress.ogg"))
								e\EventState3 = 0.001
							EndIf
						ElseIf e\EventState3 < 70.0 * 4.0
							e\EventState3 = Min(e\EventState3 + fps\Factor[0], 70.0 * 4.0)
							If e\EventState3 > 70.0 * 2.0 And e\EventState3 - fps\Factor[0] =< 70.0 * 2.0
								PlaySound_Strict(LoadTempSound("SFX\Interact\NVGOn.ogg"))
								
								EntityAlpha(e\room\Objects[5], 1.0)
								EntityAlpha(e\room\Objects[6], 1.0)
								
								ShowEntity(e\room\Objects[7])
								ShowEntity(e\room\Objects[8])
							EndIf
						ElseIf e\EventState3 = 70.0 * 4.0 Lor e\EventState3 = 70.0 * 5.0
							If e\EventState < 6.0
								Local x#, y#
								
								TFormPoint(EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True), 0, e\room\OBJ)
								y = TFormedY()
								
								TFormPoint(EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True), EntityZ(e\room\Objects[5], True), 0, e\room\OBJ)
								x = TFormedX()
								
								If e\EventState3 = 70.0 * 4.0
									If y < -1867.0 Then MoveEntity(e\room\Objects[6], 0.0, 20.0, 0.0)
									If x < -1537.0 Then MoveEntity(e\room\Objects[5], 20.0, 0.0, 0.0)
									
									If y >= -1867.0 And x >= -1537.0 Then e\EventState3 = 70.0 * 5.0
								Else
									If y > -2269.0 Then MoveEntity(e\room\Objects[6], 0.0, -20.0, 0.0)
									If x > -1980.0 Then MoveEntity(e\room\Objects[5], -20.0, 0.0, 0.0)
									
									If y <= -2269.0 And x <= -1980.0
										e\EventState3 = 70.0 * 4.0
										e\EventState = e\EventState + 1.0
									EndIf
								EndIf
							Else
								e\EventState = 1.0
								e\EventState3 = 70.0 * 5.001
							EndIf
						Else
							e\EventState3 = e\EventState3 + fps\Factor[0]
							If e\EventState3 > 70.0 * 7.0 And e\EventState3 - fps\Factor[0] =< 70.0 * 7.0
								PlaySound_Strict(LoadTempSound("SFX\Interact\NVGOff.ogg"))
								If I_409\Timer = 0.0
									PlaySound_Strict(LoadTempSound("SFX\Alarm\ScanCompleted.ogg"))
								Else
									PlaySound_Strict(LoadTempSound("SFX\Alarm\ScanDetected.ogg"))
								EndIf
								
								EntityAlpha(e\room\Objects[5], 0.0)
								EntityAlpha(e\room\Objects[6], 0.0)
								
								HideEntity(e\room\Objects[7])
								HideEntity(e\room\Objects[8])
								
								StopChannel(e\SoundCHN) : e\SoundCHN = 0
								StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
								
								FreeSound_Strict(e\Sound) : e\Sound = 0
							EndIf
							If e\EventState3 > 70.0 * 11.0
								If I_409\Timer > 0.0
									If e\room\RoomEmitters[0] = Null
										TFormPoint(1760.0, -1761.0, 2368.0, e\room\OBJ, 0)
										e\room\RoomEmitters[0] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 45)
										e\room\RoomEmitters[0]\State = 6
									EndIf
									If e\EventState3 > 70.0 * 12.0 And e\EventState3 - fps\Factor[0] =< 70.0 * 12.0
										Kill()
										msg\DeathMsg = Format(GetLocalString("death", "incinerate"), SubjectName)
									EndIf
									If e\EventState3 > 70.0 * 18.0
										For i = 2 To 3
											e\room\RoomDoors[i]\FastOpen = False
											OpenCloseDoor(e\room\RoomDoors[i])
										Next
										If e\room\RoomEmitters[0] <> Null Then FreeEmitter(e\room\RoomEmitters[0])
										e\EventState3 = 0.0
										e\EventState4 = 1.0
									EndIf
								Else
									For i = 2 To 3
										e\room\RoomDoors[i]\FastOpen = False
										OpenCloseDoor(e\room\RoomDoors[i])
									Next
									e\EventState3 = 0.0
									e\EventState4 = 1.0
								EndIf
							EndIf
						EndIf
					ElseIf EntityDistanceSquared(me\Collider, e\room\Objects[4]) > 1.96
						e\EventState4 = 0.0
					EndIf
				EndIf
				If e\EventState3 > 0.0 Then CanSave = 0
				;[End Block]
			EndIf
		EndIf
		
		; ~ Update elevators
		;[Block]
		Local x1# = EntityX(me\Collider, True), y1# = EntityY(me\Collider, True), z1# = EntityZ(me\Collider, True)
		
		me\InsideElevator = (IsInsideElevator(x1, y1, z1, e\room\Objects[0]) Lor IsInsideElevator(x1, y1, z1, e\room\Objects[1]))
		ToElevatorFloor = LowerFloor
		e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
		;[End Block]
	EndIf
End Function

Function UpdateEvent_Room3_HCZ_1048%(e.Events)
	If e\room\Dist < 5.0
		If e\EventState = 0.0
			TFormPoint(704.0, 166.0, -416.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCType1048, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State = 3.0
			PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
			SetNPCFrame(e\room\NPC[0], 326.0)
			
			Local itt.ItemTemplates
			Local DrawingName$ = "drawing_1048(" + Rand(26) + ").png"
			Local Tex% = GetRescaledTexture(False, ItemHUDTexturePath + DrawingName, 1, DeleteMapTextures, 145, 204)
			
			For itt.ItemTemplates = Each ItemTemplates
				If itt\Name = "Drawing"
					If itt\Img <> 0 Then FreeImage(itt\Img) : itt\Img = 0
					itt\ImgPath = ItemHUDTexturePath + DrawingName
					itt\TexPath = itt\ImgPath
					
					EntityTexture(itt\OBJ, Tex)
					UpdateEntityMaterial(itt\OBJ, DEFERRED_ADDITIVE)
					Exit
				EndIf
			Next
			
			Local SurfCount% = CountSurfaces(e\room\NPC[0]\OBJ)
			Local i%, SF%, b%, BT%
			
			For i = 1 To SurfCount
				SF = GetSurface(e\room\NPC[0]\OBJ, i)
				b = GetSurfaceBrush(SF)
				If b <> 0
					BT = GetBrushTexture(b, 0)
					If BT <> 0
						If Lower(StripPath(TextureName(BT))) <> "scp_1048.png"
							BrushTexture(b, Tex)
							SetDeferredBrush(b)
							PaintSurface(SF, b)
							
							FreeTexture(BT) : BT = 0
							FreeBrush(b) : b = 0
							Exit
						EndIf
						FreeTexture(BT) : BT = 0
					EndIf
					FreeBrush(b) : b = 0
				EndIf
			Next
			DeleteSingleTextureEntryFromCache(Tex)	: Tex = 0
			
			e\EventState = 1.0
		Else
			If e\room\NPC[0]\Frame > 474.0
				If InteractObject(e\room\NPC[0]\OBJ, 1.5)
					If ItemAmount < MaxItemAmount
						SelectedItem = CreateItem("Drawing", it_paper, 0.0, 0.0, 0.0)
						PickItem(SelectedItem)
						
						HandEntity = 0
						RemoveNPC(e\room\NPC[0])
						RemoveEvent(e)
						Return
					Else
						CreateMsg(GetLocalString("msg", "cantcarry"))
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room3_HCZ_Duck%(e.Events)
	If PlayerRoom = e\room
		If e\room\Objects[0] = 0
			TFormPoint(704.0, 112.0, -416.0, e\room\OBJ, 0)
			e\room\Objects[0] =	CopyEntity(n_I\NPCModelID[NPC_DUCK_MODEL])
			ScaleEntity(e\room\Objects[0], 0.07, 0.07, 0.07)
			PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\Objects[0], 0.0, Rnd(360.0), 0.0)
			
			Local Tex% = LoadTexture_Strict("GFX\NPCs\duck(3).png")
			
			EntityTexture(e\room\Objects[0], Tex)
			DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EntityParent(e\room\Objects[0], e\room\OBJ)
		Else
			If (Not EntityInView(e\room\Objects[0], Camera))
				e\EventState = e\EventState + fps\Factor[0]
				If Rand(200) = 1 And e\EventState > 300.0
					e\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\Joke\Saxophone.ogg"), Camera, e\room\Objects[0], 6.0)
					e\EventState = 0.0
				EndIf
			Else
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room3_2_HCZ_Guard%(e.Events)
	If e\room\Dist < 8.0
		Local n.NPCs
		
		TFormPoint(-190.0, 75.0, 190.0, e\room\OBJ, 0)
		n.NPCs = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
		n\State = 8.0 : n\IsDead = True
		SetNPCFrame(n, 288.0)
		RotateEntity(n\Collider, 0.0, e\room\Angle + Rnd(160.0, 180.0), 0.0, True)
		
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Cont3_009%(e.Events)
	Local it.Items
	Local i%
	
	If e\room\Dist < 20.0
		If e\EventState = 0.0
			e\SoundCHN2 = LoopSoundEx(snd_I\RoomAmbience[5], e\SoundCHN2, Camera, e\room\OBJ)
		Else
			e\SoundCHN = LoopSoundEx(snd_I\HissSFX[1], e\SoundCHN, Camera, e\room\OBJ, 5.0, 0.7)
			
			If (Not EntityHidden(e\room\RoomLights[0]\OBJ)) Then HideEntity(e\room\RoomLights[0]\OBJ)
			
			If e\EventState > 70.0
				e\EventState = Max(e\EventState - fps\Factor[0], 70.0)
			ElseIf e\EventState = 70.0
				For i = 0 To 4
					e\room\RoomDoors[i]\Locked = 0
				Next
				EntityPickMode(e\room\Objects[1], 0)
				EntityType(e\room\Objects[1], 0)
				If e\room\Objects[3] <> 0
					it.Items = CreateItem("Level 4 Key Card", it_key4, EntityX(e\room\Objects[3]), EntityY(e\room\Objects[3]) + 0.015, EntityZ(e\room\Objects[3]))
					RotateEntity(it\Collider, 0.0, EntityYaw(e\room\Objects[3]), 0.0)
					FreeEntity(e\room\Objects[3]) : e\room\Objects[3] = 0
				EndIf
				e\EventState = 66.0
			EndIf
			EntityAlpha(e\room\Objects[1], Max(e\EventState / 7875.0, 0.3))
		EndIf
	EndIf
	If PlayerRoom = e\room
		If e\room\NPC[0] = Null
			TFormPoint(382.0, -463.0, -263.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\IsDead = True : e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\State = 66.0 : e\room\NPC[0]\IceTimer = 70.0 * 30.0
			SetNPCFrame(e\room\NPC[0], 510.0)
			ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_009_TEXTURE)
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 40.0, 0.0)
			EntityColor(e\room\NPC[0]\OBJ, 255.0, 100.0, 100.0)
			EntityShininess(e\room\NPC[0]\OBJ, 1.0, 0.5)
		EndIf
		
		If e\EventState = 0.0
			UpdateRedLight(e\room\RoomLights[0], 1500, 800)
			If UpdateLever(e\room\RoomLevers[0]\OBJ)
				For i = 0 To 4
					If e\room\RoomDoors[i]\Open Then OpenCloseDoor(e\room\RoomDoors[i])
					e\room\RoomDoors[i]\Locked = 1
				Next
				SetTemplateVelocity(ParticleEffect[19], -0.007, -0.008, -0.001, 0.0012, -0.007, 0.008)
				SetEmitter(e\room, EntityX(e\room\RoomLevers[0]\OBJ, True), EntityY(e\room\RoomLevers[0]\OBJ, True), EntityZ(e\room\RoomLevers[0]\OBJ, True), 19)
				PlaySoundEx(snd_I\SparkShortSFX, Camera, e\room\RoomLevers[0]\OBJ, 3.0, 0.4)
				PlaySound_Strict(snd_I\AlarmSFX[2])
				me\LightBlink = 1.8
				e\EventState = 70.0 * 90.0
			EndIf
		EndIf
		
		If e\EventState <> 66.0
			If e\room\Objects[3] = 0
				Local itt.ItemTemplates
				
				For itt.ItemTemplates = Each ItemTemplates
					If itt\ID = it_key4
						e\room\Objects[3] = CopyInstanced(itt\OBJ)
						TFormPoint(384.0, -510.0, -391.0, e\room\OBJ, 0)
						PositionEntity(e\room\Objects[3], TFormedX(), TFormedY(), TFormedZ())
						RotateEntity(e\room\Objects[3], 0.0, 45.0, 0.0)
						Exit
					EndIf
				Next
			EndIf
		ElseIf e\EventState = 66.0
			UpdateBreathSteam()
			
			Local n.NPCs
			Local IceTriggerY# = e\room\y - 1.6
			
			For n.NPCs = Each NPCs
				If n\IceTimer = 0.0 And n\CurrentRoom = e\room
					Select n\NPCType
						Case NPCType513_1, NPCType372, NPCType106, NPCType173, NPCTypeD, NPCTypeGuard, NPCType457
							;[Block]
							; ~ Skip
							;[End Block]
						Default
							;[Block]
							If EntityY(n\Collider, True) < IceTriggerY Then n\IceTimer = 0.001
							;[End Block]
					End Select
				EndIf
			Next
			
			If EntityY(me\Collider, True) < IceTriggerY
				DecalStep = 2
				If I_009\Timer = 0.0
					GiveAchievement("009")
					If wi\HazmatSuit = 0 Then I_009\Timer = 0.001
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont3_966%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs, emit.Emitter
		Local i%, x#, y#, z#
		
		Select e\EventState
			Case 0.0
				;[Block]
				If e\room\RoomDoors[0]\Open Lor e\room\RoomDoors[1]\Open
					Local it.Items
					
					For i = 0 To 1
						CreateNPC(NPCType966, EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True))
					Next
					
					TFormPoint(0.0, 50.0, -297.0, e\room\OBJ, 0)
					e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
					e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
					SetNPCFrame(e\room\NPC[0], 502.0)
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_HARN_TEXTURE)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle, 0.0)
					
					TFormPoint(0.0, 50.0, -337.0, e\room\OBJ, 0)
					emit.Emitter = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 30)
					emit\State = 5
					
					TFormPoint(0.0, 0.0, -418.0, e\room\OBJ, 0)
					CreateDecal(DECAL_BLOOD_2, TFormedX(), e\room\y + 0.005, TFormedZ(), 90.0, e\room\Angle + 90.0, 0.0, 0.4)
					
					TFormPoint(-68.0, 40.0, -396.0, e\room\OBJ, 0)
					CreateItem("Asav Harn's Badge", it_badge, TFormedX(), TFormedY(), TFormedZ())
					
					e\EventState = 1.0
				EndIf
				;[End Block]
			Case 1.0
				;[Block]
				If e\room\RoomDoors[1]\Open
					x = EntityX(e\room\RoomDoors[1]\FrameOBJ) : y = e\room\y + 0.05 : z = EntityZ(e\room\RoomDoors[1]\FrameOBJ)
					For i = 0 To 4
						n.NPCs = CreateNPC(NPCTypeCockroach, x, y, z)
						n\State2 = Rand(0.0, 1.0) : n\State = 1.0
						RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
					Next
					e\room\RoomDoors[1]\Locked = 1
					e\EventState = 2.0
				EndIf
				;[End Block]
			Case 2.0
				;[Block]
				AnimateNPC(e\room\NPC[0], 502.0, 494.0, -0.03 - (0.1 * (e\room\RoomDoors[1]\OpenState > 100.0)), False)
				If e\room\NPC[0]\Frame < 494.1 Then RemoveEvent(e)
				;[End Block]
		End Select
	EndIf
End Function

Function UpdateEvent_Room4_2_HCZ_D%(e.Events)
	If e\room\Dist < 8.0
		Local n.NPCs
		
		TFormPoint(256.0, 55.2, 256.0, e\room\OBJ, 0)
		n.NPCs = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
		n\State3 = -1.0 : n\IsDead = True
		ChangeNPCTextureID(n, NPC_CLASS_D_BODY_2_TEXTURE)
		SetNPCFrame(n, 19.0)
		RotateEntity(n\Collider, 0.0, e\room\Angle - 30.0, 0.0, True)
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room1_Dead_End_EZ_Guard%(e.Events)
	If e\EventState = 0.0
		If PlayerRoom = e\room
			TFormPoint(-944.0, 448.0, 20.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle, 0.0, True)
			
			e\room\RoomDoors[0]\Open = True
			
			e\EventState = 1.0
		EndIf
	Else
		If e\EventState = 1.0
			If e\room\Dist < 2.5
				Local Pvt% = CreatePivot()
				
				TFormPoint(-944.0, 320.0, 1460.0, e\room\OBJ, 0)
				PositionEntity(Pvt, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[0]\State = 5.0 : e\room\NPC[0]\State3 = 0.0
				e\room\NPC[0]\EnemyX = EntityX(Pvt, True)
				e\room\NPC[0]\EnemyY = EntityY(Pvt, True)
				e\room\NPC[0]\EnemyZ = EntityZ(Pvt, True)
				FreeEntity(Pvt) : Pvt = 0
				
				e\EventState = 2.0
			EndIf
		Else
			e\EventState = e\EventState + fps\Factor[0]
			If e\EventState > 70.0 * 3.6 And e\room\RoomDoors[0]\Open Then OpenCloseDoor(e\room\RoomDoors[0])
			If e\room\RoomDoors[0]\OpenState = 0.0
				FreeEntity(e\room\RoomDoors[0]\OBJ2) : e\room\RoomDoors[0]\OBJ2 = 0
				RemoveNPC(e\room\NPC[0]) : e\room\NPC[0] = Null
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Gate_A_Entrance%(e.Events)
	If PlayerRoom = e\room
		Local r.Rooms
		Local RoomExists% = False
		
		For r.Rooms = Each Rooms
			If r\RoomTemplate\RoomID = r_room2c_ec
				RoomExists = True
				Exit
			EndIf
		Next
		If (Not RoomExists) Then e\EventState3 = 1.0
		
		If RemoteDoorOn And e\EventState3 = 0.0
			e\room\RoomDoors[1]\Locked = 2
		Else
			e\room\RoomDoors[1]\Locked = 0
			
			Local gatea.Rooms = Null
			
			For r.Rooms = Each Rooms
				If r\RoomTemplate\RoomID = r_gate_a
					gatea = r 
					Exit
				EndIf
			Next
			
			Local x# = EntityX(me\Collider, True)
			Local y# = EntityY(me\Collider, True)
			Local z# = EntityZ(me\Collider, True)
			
			me\InsideElevator = (IsInsideElevator(x, y, z, e\room\Objects[0]) Lor IsInsideElevator(x, y, z, e\room\Objects[1]))
			ToElevatorFloor = UpperFloor
			e\EventState = UpdateElevators(e\EventState, e\room\RoomDoors[0], gatea\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
			If (Not n_I\Curr106\Contained)
				If e\EventState < -1.5 And e\EventState + fps\Factor[0] >= -1.5 Then PlaySound_Strict(snd_I\SCP106SFX[3], True)
			EndIf
			
			If EntityDistanceSquared(me\Collider, e\room\Objects[1]) < 16.0
				gatea\RoomDoors[1]\Locked = 1
				PlayerRoom = gatea
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Gate_A%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs
		Local i%, TargetX#, TargetY#, TargetZ#
		
		If e\EventState = 0.0
			RenderLoading(0, GetLocalString("loading", "ending"))
			
			For n.NPCs = Each NPCs
				If n <> n_I\Curr106 And n <> n_I\Curr173 Then RemoveNPC(n)
			Next
			n_I\Curr066 = Null
			n_I\Curr049 = Null
			n_I\Curr096 = Null
			n_I\Curr513_1 = Null
			
			Local du.Dummy1499_1
			
			For du.Dummy1499_1 = Each Dummy1499_1
				RemoveDummy1499_1(du)
			Next
			
			TFormPoint(1784.0, 2124.0, 4512.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[0]\State = 0.0
			
			TFormPoint(-5048.0, 1912.0, 4656.0, e\room\OBJ, 0)
			e\room\NPC[1] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[1]\State = 0.0
			
			TFormPoint(-5615.0, 1901.0, 6904.0, e\room\OBJ, 0)
			n.NPCs = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
			ChangeNPCTextureID(n, NPC_CLASS_D_FRANKLIN_TEXTURE)
			n\State = -1.0
			SetNPCFrame(n, 326.0)
			RotateEntity(n\Collider, 0.0, 270.0, 0.0)
			
			For i = 2 To 4
				e\room\NPC[i] = CreateNPC(NPCTypeApache, e\room\x, e\room\y + 11.0, e\room\z)
				e\room\NPC[i]\State = (Not n_I\Curr106\Contained)
			Next
			
			TFormPoint(1824.0, 128.0, 7056.0, e\room\OBJ, 0)
			TargetX = TFormedX() : TargetY = TFormedY() : TargetZ = TFormedZ()
			e\room\NPC[5] = CreateNPC(NPCTypeMTF, TargetX, TargetY, TargetZ)
			e\room\NPC[6] = CreateNPC(NPCTypeMTF, TargetX + 0.8, TargetY, TargetZ + 0.8)
			
			TFormPoint(-1824.0, 128.0, 7056.0, e\room\OBJ, 0)
			TargetX = TFormedX() : TargetY = TFormedY() : TargetZ = TFormedZ()
			e\room\NPC[7] = CreateNPC(NPCTypeMTF, TargetX, TargetY, TargetZ)
			e\room\NPC[8] = CreateNPC(NPCTypeMTF, TargetX + 0.8, TargetY, TargetZ + 0.8)
			
			For i = 5 To 8
				e\room\NPC[i]\EnemyX = EntityX(e\room\OBJ, True)
				e\room\NPC[i]\EnemyY = EntityY(e\room\OBJ, True)
				e\room\NPC[i]\EnemyZ = EntityZ(e\room\OBJ, True)
				e\room\NPC[i]\State = MTF_LOOKING_AT_SOME_TARGET
			Next
			
			If n_I\Curr106\Contained
				PositionEntity(e\room\NPC[5]\Collider, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True), True)
				ResetEntity(e\room\NPC[5]\Collider)
				TFormPoint(-1473.0, 128.0, 4251.0, e\room\OBJ, 0)
				PositionEntity(e\room\NPC[7]\Collider, TFormedX(), TFormedY(), TFormedZ(), True)
				ResetEntity(e\room\NPC[7]\Collider)
			EndIf
			
			RenderLoading(30, GetLocalString("loading", "ending"))
			
			Sky = CreateSky("GFX\Map\Textures\sky")
			RotateEntity(Sky, 0.0, e\room\Angle, 0.0)
			
			RenderLoading(60, GetLocalString("loading", "ending"))
			
			e\room\Objects[0] = LoadRMesh(RoomPartsPath + "gate_a_tunnel.rmesh", Null)
			PositionEntity(e\room\Objects[0], EntityX(e\room\OBJ, True), EntityY(e\room\OBJ, True), EntityZ(e\room\OBJ, True))
			ScaleEntity(e\room\Objects[0], RoomScale, RoomScale, RoomScale)
			EntityType(e\room\Objects[0], HIT_MAP)
			EntityPickMode(e\room\Objects[0], 2)
			EntityParent(e\room\Objects[0], e\room\OBJ)
			
			TFormPoint(-4308.0, -1045.0, 544.0, e\room\OBJ, 0)
			e\room\Objects[4] = LoadMesh_Strict(RoomPartsPath + "gateawall1.b3d", e\room\OBJ)
			PositionEntity(e\room\Objects[4], TFormedX(), TFormedY(), TFormedZ(), True)
			EntityColor(e\room\Objects[4], 25.0, 25.0, 25.0)
			EntityType(e\room\Objects[4], HIT_MAP)
			
			TFormPoint(-3820.0, -1045.0, 544.0, e\room\OBJ, 0)
			e\room\Objects[5] = LoadMesh_Strict(RoomPartsPath + "gateawall2.b3d", e\room\OBJ)
			PositionEntity(e\room\Objects[5], TFormedX(), TFormedY(), TFormedZ(), True)
			EntityColor(e\room\Objects[5], 25.0, 25.0, 25.5)
			EntityType(e\room\Objects[5], HIT_MAP)
			
			TFormPoint(2850.0, 1008.0, 6157.0, e\room\OBJ, 0)
			e\room\Objects[7] = LoadMesh_Strict("GFX\Map\Props\lightgunbase.b3d")
			PositionEntity(e\room\Objects[7], TFormedX(), TFormedY(), TFormedZ(), True)
			ScaleEntity(e\room\Objects[7], RoomScale, RoomScale, RoomScale)
			
			TFormPoint(2850.0, 1296.0, 5981.0, e\room\OBJ, 0)
			e\room\Objects[8] = LoadMesh_Strict("GFX\Map\Props\lightgun.b3d")
			PositionEntity(e\room\Objects[8], TFormedX(), TFormedY(), TFormedZ(), True)
			ScaleEntity(e\room\Objects[8], RoomScale, RoomScale, RoomScale)
			EntityParent(e\room\Objects[8], e\room\Objects[7])
			RotateEntity(e\room\Objects[7], 0.0, 48.0, 0.0)
			RotateEntity(e\room\Objects[8], 40.0, 0.0, 0.0)
			
			RenderLoading(90, GetLocalString("loading", "ending"))
			
			ResetEntity(me\Collider)
			
			If (Not n_I\Curr106\Contained) Then PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\106Escape.ogg"))
			
			IsBlackOut = False
			
			CreateConsoleMsg("")
			CreateConsoleMsg(GetLocalString("misc", "warning2"), 255, 0, 0)
			CreateConsoleMsg("")
			
			e\EventState = 1.0
			
			RenderLoading(100)
		Else
			Local r.Rooms, p.Particles
			Local Dist#, Pvt%, SqrValue#, SinValue#
			
			UpdateSky(Sky)
			UpdateBreathSteam()
			
			CanSave = 1
			
			For r.Rooms = Each Rooms
				HideRoomsNoColl(r)
			Next
			ShowRoomsNoColl(e\room)
			ShowRoomsColl(e\room)
			
			ShouldPlay = 16
			
			e\EventState = e\EventState + fps\Factor[0]
			
			For i = 2 To 4
				If e\room\NPC[i] <> Null
					If e\room\NPC[i]\State < 2.0
						Dist = e\EventState / 10.0 + (120.0 * i)
						PositionEntity(e\room\NPC[i]\Collider, EntityX(e\room\Objects[1], True) + Cos(Dist) * 6000.0 * RoomScale, e\room\y + 11.0, EntityZ(e\room\Objects[1], True) + Sin(Dist) * 6000.0 * RoomScale)
						RotateEntity(e\room\NPC[i]\Collider, 7.0, Dist, 20.0)
					EndIf
				EndIf
			Next
			
			If e\EventState >= 350.0
				If (Not n_I\Curr106\Contained)
					If e\EventState - fps\Factor[0] < 350.0
						n_I\Curr106\EnemyX = EntityX(e\room\Objects[1], True) : n_I\Curr106\EnemyY = EntityY(e\room\Objects[1], True) : n_I\Curr106\EnemyZ = EntityZ(e\room\Objects[1], True)
						n_I\Curr106\State = 2.0
						RotateEntity(n_I\Curr106\Collider, 0.0, e\room\Angle + 90.0, 0.0)
						
						PlaySound_Strict(snd_I\HorrorSFX[5])
					ElseIf n_I\Curr106\State > 2.0
						If n_I\Curr106\State = 3.0
							n_I\Curr106\PathTimer = 70.0 * 100.0
							
							If n_I\Curr106\PathStatus <> PATH_STATUS_FOUND
								n_I\Curr106\PathStatus = FindPath(n_I\Curr106, EntityX(e\room\NPC[5]\Collider, True), EntityY(e\room\NPC[5]\Collider, True), EntityZ(e\room\NPC[5]\Collider, True))
								n_I\Curr106\PathLocation = 1
							EndIf
							For i = 2 To 4 ; ~ Helicopters start attacking SCP-106
								e\room\NPC[i]\EnemyX = EntityX(n_I\Curr106\OBJ, True)
								e\room\NPC[i]\EnemyY = EntityY(n_I\Curr106\OBJ, True) + 5.0
								e\room\NPC[i]\EnemyZ = EntityZ(n_I\Curr106\OBJ, True)
								e\room\NPC[i]\State = 3.0
							Next
							
							For i = 0 To 1
								PointEntity(e\room\NPC[i]\Collider, n_I\Curr106\Collider)
							Next
							For i = 5 To 8
								If NPCSeesPlayer(e\room\NPC[i], 4.0 - me\CrouchState) = 1
									e\room\NPC[i]\State = MTF_SHOOTING_AT_PLAYER
								Else
									e\room\NPC[i]\EnemyX = EntityX(n_I\Curr106\OBJ, True)
									e\room\NPC[i]\EnemyY = EntityY(n_I\Curr106\OBJ, True) + 0.4
									e\room\NPC[i]\EnemyZ = EntityZ(n_I\Curr106\OBJ, True)
									e\room\NPC[i]\State = MTF_LOOKING_AT_SOME_TARGET
								EndIf
							Next
							
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(e\room\Objects[8], True), EntityY(e\room\Objects[8], True), EntityZ(e\room\Objects[8], True))
							PointEntity(Pvt, n_I\Curr106\Collider)
							RotateEntity(e\room\Objects[7], 0.0, CurveAngle(EntityYaw(Pvt), EntityYaw(e\room\Objects[7], True), 150.0), 0.0, True)
							RotateEntity(e\room\Objects[8], CurveAngle(EntityPitch(Pvt), EntityPitch(e\room\Objects[8], True), 200.0), EntityYaw(e\room\Objects[7], True), 0.0, True)
							FreeEntity(Pvt) : Pvt = 0
						EndIf
						
						Dist = DistanceSquared(EntityX(n_I\Curr106\Collider), EntityX(e\room\Objects[2], True), EntityZ(n_I\Curr106\Collider), EntityZ(e\room\Objects[2], True))
						
						n_I\Curr106\CurrSpeed = CurveValue(0.0, n_I\Curr106\CurrSpeed, Max(5.0 * Sqr(Dist), 2.0))
						If Dist < 225.0
							If e\SoundCHN2 = 0 Then e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Franklin.ogg"), True)
							If Dist < 0.16
								n_I\Curr106\State = 4.0
								
								If n_I\Curr106\Frame =< 151.0
									e\EventState2 = 0.0
								EndIf
							Else
								If Dist < 72.25
									If e\EventState2 = 0.0
										e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\HIDTurret.ogg"))
										e\EventState2 = 1.0
									ElseIf e\EventState2 > 0.0
										e\EventState2 = e\EventState2 + fps\Factor[0]
										If e\EventState2 >= 70.0 * 7.5
											If e\EventState2 - fps\Factor[0] < 70.0 * 7.5
												p.Particles = CreateParticle(PARTICLE_SUN, EntityX(n_I\Curr106\OBJ, True), EntityY(n_I\Curr106\OBJ, True) + 0.4, EntityZ(n_I\Curr106\OBJ, True), 7.0, 0.0, 470.0)
												p\Speed = 0.0 : p\Alpha = 1.0
												EntityParent(p\Pvt, n_I\Curr106\Collider, True)
												
												p.Particles = CreateParticle(PARTICLE_SUN, EntityX(e\room\Objects[8], True), EntityY(e\room\Objects[8], True), EntityZ(e\room\Objects[8], True), 2.0, 0.0, 470.0)
												p\Speed = 0.0 : p\Alpha = 1.0
												RotateEntity(p\Pvt, EntityPitch(e\room\Objects[8], True), EntityYaw(e\room\Objects[8], True), 0.0, True)
												MoveEntity(p\Pvt, 0.0, 92.0 * RoomScale, 512.0 * RoomScale)
												EntityParent(p\Pvt, e\room\Objects[8], True)
											ElseIf e\EventState2 < 70.0 * 14.8
												me\CameraShake = 0.5
												me\LightFlash = 0.3 + EntityInView(e\room\Objects[8], Camera) * 0.5
												LightVolume = TempLightVolume * Rnd(1.0, 2.0)
												
												If (Not (me\Terminated Lor chs\GodMode))
													If EntityDistanceSquared(me\Collider, n_I\Curr106\Collider) < 1.44
														PlaySound_Strict(LoadTempSound("SFX\SCP\294\Burn.ogg"))
														Kill(True)
													EndIf
												EndIf
											EndIf
										EndIf
									EndIf
									
									If opt\ParticleAmount > 0
										For i = 0 To Rand(2, 2 + (6 * (opt\ParticleAmount - 1))) - Int(Sqr(Dist))
											p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, EntityX(n_I\Curr106\OBJ, True), EntityY(n_I\Curr106\OBJ, True) + Rnd(0.4, 0.9), EntityZ(n_I\Curr106\OBJ), 0.006, -0.002, 40.0)
											p\Speed = 0.005 : p\Alpha = 0.8 : p\AlphaChange = -0.01
											RotateEntity(p\Pvt, -Rnd(70.0, 110.0), Rnd(360.0), 0.0)
										Next
									EndIf
								EndIf
							EndIf
						EndIf
					ElseIf n_I\Curr106\State = 1.0
						n_I\Curr106\Contained = True
					EndIf
				Else
					If e\EventState2 = 0.0
						For i = 2 To 4
							e\room\NPC[i]\State = 0.0
						Next
						
						For i = 5 To 8
							e\room\NPC[i]\EnemyX = EntityX(me\Collider)
							e\room\NPC[i]\EnemyY = EntityY(me\Collider)
							e\room\NPC[i]\EnemyZ = EntityZ(me\Collider)
							e\room\NPC[i]\State = MTF_FOLLOW_PATH
						Next
						e\EventState2 = 1.0
					Else
						For i = 5 To 8
							e\room\NPC[i]\EnemyX = EntityX(me\Collider)
							e\room\NPC[i]\EnemyY = EntityY(me\Collider)
							e\room\NPC[i]\EnemyZ = EntityZ(me\Collider)
						Next
						If e\EventState2 = 1.0
							Local Temp% = False
							
							For i = 5 To 8
								If NPCSeesPlayer(e\room\NPC[i], 8.0 - me\CrouchState) = 1
									Temp = True
									Exit
								EndIf
							Next
							If Temp
								For i = 5 To 8
									e\room\NPC[i]\State = MTF_LOOKING_AT_SOME_TARGET
								Next
								e\EventState2 = 2.0
							EndIf
						ElseIf e\EventState2 = 2.0
							If (S2IMapSize(UnlockedAchievements) - S2IMapContains(UnlockedAchievements, "apollyon") - S2IMapContains(UnlockedAchievements, "keter")) => (S2IMapSize(AchievementsIndex) - 4)
								MakeMeUnplayable()
								e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\STOPRIGHTTHERE.ogg"), True)
								e\EventState2 = 3.0
							Else
								For i = 5 To 8
									If e\room\NPC[i]\State <> MTF_STATE_STUNNED
										If NPCSeesPlayer(e\room\NPC[i], 8.0 - me\CrouchState) = 1
											e\room\NPC[i]\State = MTF_SHOOTING_AT_PLAYER
										Else
											e\room\NPC[i]\State = MTF_FOLLOW_PATH
										EndIf
									EndIf
								Next
							EndIf
						ElseIf e\EventState2 = 3.0
							ShouldPlay = 0
							me\CurrSpeed = 0.0
							If (Not ChannelPlaying(e\SoundCHN))
								ClearCheats()
								
								me\SelectedEnding = Ending_A2
								PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Bang2.ogg"))
								msg\DeathMsg = ""
								
								For n.NPCs = Each NPCs
									RemoveNPC(n)
								Next
								
								me\LightFlash = 1.0
								me\Terminated = True
								me\BlinkTimer = -10.0
								
								RemoveEvent(e)
								Return
							EndIf
						EndIf
					EndIf
				EndIf
				If e\EventState3 = 0.0
					If IsEqual(EntityY(me\Collider, True), EntityY(e\room\Objects[3], True), 1.0)
						If DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[3], True), EntityZ(me\Collider), EntityZ(e\room\Objects[3], True)) < 144.0
							n_I\Curr106\State = 0.0
							n_I\Curr106\State2 = Rnd(22000.0, 27000.0)
							If (Not EntityHidden(n_I\Curr106\OBJ)) Then HideEntity(n_I\Curr106\OBJ)
							
							; ~ MTF spawns at the tunnel entrance
							For i = 5 To 8
								PositionEntity(e\room\NPC[i]\Collider, EntityX(e\room\Objects[6], True) + (i - 6) * 0.3, EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True) + (i - 6) * 0.3, True)
								ResetEntity(e\room\NPC[i]\Collider)
								
								e\room\NPC[i]\EnemyX = EntityX(me\Collider)
								e\room\NPC[i]\EnemyY = EntityY(me\Collider)
								e\room\NPC[i]\EnemyZ = EntityZ(me\Collider)
								e\room\NPC[i]\PathTimer = 0.0
								e\room\NPC[i]\State = MTF_FOLLOW_PATH
							Next
							e\room\NPC[5]\Sound = LoadSound_Strict("SFX\Character\MTF\ThereHeIs0.ogg")
							PlaySoundEx(e\room\NPC[5]\Sound, Camera, e\room\NPC[5]\Collider, 25.0, 1.0, True)
							
							e\room\RoomDoors[0]\Open = True
							
							For i = 2 To 4
								e\room\NPC[i]\State = 0.0
							Next
							
							e\EventState3 = 1.0
						EndIf
					EndIf
				ElseIf e\EventState3 = 1.0
					For i = 5 To 8
						If EntityDistanceSquared(e\room\NPC[i]\Collider, me\Collider) > 16.0
							e\room\NPC[i]\State = MTF_FOLLOW_PATH
						Else
							e\room\NPC[i]\State = MTF_SHOOTING_AT_PLAYER
						EndIf
					Next
					
					If IsEqual(EntityY(me\Collider, True), EntityY(e\room\Objects[3], True), 1.0)
						If DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[3], True), EntityZ(me\Collider), EntityZ(e\room\Objects[3], True)) < 49.0
							For i = 5 To 8
								e\room\NPC[i]\State = MTF_LOOKING_AT_SOME_TARGET
							Next
							
							e\room\Objects[9] = LoadAnimMesh_Strict("GFX\NPCs\CI.b3d")
							
							Local Scale# = 0.55 / MeshWidth(e\room\Objects[9])
							
							ScaleEntity(e\room\Objects[9], Scale, Scale, Scale)
							PositionEntity(e\room\Objects[9], EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True))
							
							e\room\Objects[10] = CopyEntity(e\room\Objects[9])
							PositionEntity(e\room\Objects[10], e\room\x - 3968.0 * RoomScale, EntityY(e\room\Objects[3], True), e\room\z - 1920.0 * RoomScale)
							
							e\room\Objects[11] = CopyEntity(e\room\Objects[9])
							PositionEntity(e\room\Objects[11], e\room\x - 4160.0 * RoomScale, EntityY(e\room\Objects[3], True), e\room\z - 1920.0 * RoomScale)
							EntityParent(e\room\Objects[11], e\room\Objects[10])
							
							e\room\Objects[12] = CopyEntity(e\room\Objects[9])
							PositionEntity(e\room\Objects[12], e\room\x - 4064.0 * RoomScale, EntityY(e\room\Objects[3], True), e\room\z - 2112.0 * RoomScale)
							EntityParent(e\room\Objects[12], e\room\Objects[10])
							
							e\SoundCHN = PlaySoundEx(LoadTempSound("SFX\Ending\GateA\Bell0.ogg"), Camera, e\room\Objects[9])
							
							p.Particles = CreateParticle(PARTICLE_SUN, EntityX(e\room\Objects[3], True), EntityY(Camera, True), EntityZ(e\room\Objects[3], True), 8.0, 0.0, 50.0)
							p\Speed = 0.15 : p\Alpha = 0.5
							p.Particles = CreateParticle(PARTICLE_SUN, EntityX(e\room\Objects[3], True), EntityY(Camera, True), EntityZ(e\room\Objects[3], True), 8.0, 0.0, 50.0)
							p\Speed = 0.25 : p\Alpha = 0.5
							PointEntity(p\Pvt, me\Collider)
							
							me\CameraShake = 1.0
							me\LightFlash = 1.0
							
							e\EventState3 = 2.0
						EndIf
					EndIf
				Else
					e\EventState3 = e\EventState3 + fps\Factor[0]
					PointEntity(e\room\Objects[9], me\Collider)
					RotateEntity(e\room\Objects[9], 0.0, EntityYaw(e\room\Objects[9]), 0.0)
					
					me\Stamina = -5.0
					
					me\BlurTimer = Sin(e\EventState3 * 0.7) * 1000.0
					
					If (Not me\Terminated)
						CameraZoom(Camera, 1.0 + Sin(e\EventState3 * 0.8) * 0.2)
						
						Dist = EntityDistanceSquared(me\Collider, e\room\Objects[3])
						If Dist < 42.25
							SqrValue = Sqr(Dist) * 80.0
							PositionEntity(me\Collider, CurveValue(EntityX(e\room\Objects[3], True), EntityX(me\Collider), Dist), EntityY(me\Collider), CurveValue(EntityZ(e\room\Objects[0], True), EntityZ(me\Collider), Dist))
						EndIf
					EndIf
					PositionEntity(me\Collider, EntityX(me\Collider), EntityY(me\Collider), Min(e\room\z + 450.0 * RoomScale, EntityZ(me\Collider)))
					If e\EventState3 > 50.0 And e\EventState3 < 230.0
						SinValue = Sin(e\EventState3 - 50.0)
						me\CameraShake = SinValue * 3.0
						TurnEntity(e\room\Objects[4], 0.0, (SinValue * (-0.85)) * fps\Factor[0], 0.0, True)
						TurnEntity(e\room\Objects[5], 0.0, (SinValue * 0.85) * fps\Factor[0], 0.0, True)
					EndIf
					
					If e\EventState3 >= 230.0
						If e\EventState3 - fps\Factor[0] < 230.0
							e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\CI.ogg"), True)
							ResetSelectedStuff()
							me\SelectedEnding = Ending_A1
						EndIf
						
						If e\EventState3 >= 480.0
							AnimateEx(e\room\Objects[9], AnimTime(e\room\Objects[9]), 176.0, 210.0, 0.2)
							MoveEntity(e\room\Objects[9], 0.0, 0.0, 0.01 * fps\Factor[0])
						EndIf
						
						If (Not ChannelPlaying(e\SoundCHN))
							ClearCheats()
							
							PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Bell1.ogg"))
							
							For n.NPCs = Each NPCs
								RemoveNPC(n)
							Next
							
							p.Particles = CreateParticle(PARTICLE_SUN, EntityX(e\room\Objects[3], True), EntityY(Camera, True), EntityZ(e\room\Objects[3], True), 8.0, 0.0, 50.0)
							p\Speed = 0.15 : p\Alpha = 0.5
							p.Particles = CreateParticle(PARTICLE_SUN, EntityX(e\room\Objects[3], True), EntityY(Camera, True), EntityZ(e\room\Objects[3], True), 8.0, 0.0, 50.0)
							p\Speed = 0.25 : p\Alpha = 0.5
							
							me\CameraShake = CurveValue(2.0, me\CameraShake, 10.0)
							me\LightFlash = CurveValue(2.0, me\LightFlash, 8.0)
							me\Terminated = True
							msg\DeathMsg = ""
							
							RemoveEvent(e)
							Return
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		HideRoomsNoColl(e\room)
	EndIf
End Function

Function UpdateEvent_Gate_B_Entrance%(e.Events)
	If PlayerRoom = e\room
		Local r.Rooms
		Local RoomExists% = False
		
		For r.Rooms = Each Rooms
			If r\RoomTemplate\RoomID = r_room2c_ec
				RoomExists = True
				Exit
			EndIf
		Next
		If (Not RoomExists) Then e\EventState3 = 1.0
		
		If (Not RemoteDoorOn) Lor (RemoteDoorOn And e\EventState3 = 0.0)
			e\room\RoomDoors[1]\Locked = 1
		Else
			e\room\RoomDoors[1]\Locked = 0
			
			Local gateb.Rooms = Null
			
			For r.Rooms = Each Rooms
				If r\RoomTemplate\RoomID = r_gate_b
					gateb = r 
					Exit
				EndIf
			Next
			
			Local x# = EntityX(me\Collider, True)
			Local y# = EntityY(me\Collider, True)
			Local z# = EntityZ(me\Collider, True)
			
			me\InsideElevator = (IsInsideElevator(x, y, z, e\room\Objects[0]) Lor IsInsideElevator(x, y, z, e\room\Objects[1]))
			ToElevatorFloor = UpperFloor
			e\EventState = UpdateElevators(e\EventState, e\room\RoomDoors[0], gateb\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e, False, True)
			
			If EntityDistanceSquared(me\Collider, e\room\Objects[1]) < 16.0
				gateb\RoomDoors[1]\Locked = 1
				PlayerRoom = gateb
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Gate_B%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs
		
		If e\EventState = 0.0
			RenderLoading(0, GetLocalString("loading", "ending"))
			
			For n.NPCs = Each NPCs
				If n <> n_I\Curr106 And n <> n_I\Curr173 Then RemoveNPC(n)
			Next
			n_I\Curr066 = Null
			n_I\Curr049 = Null
			n_I\Curr096 = Null
			n_I\Curr513_1 = Null
			
			Local du.Dummy1499_1
			
			For du.Dummy1499_1 = Each Dummy1499_1
				RemoveDummy1499_1(du)
			Next
			
			e\room\NPC[0] = CreateNPC(NPCTypeApache, e\room\x, e\room\y + 10.0, e\room\z)
			e\room\NPC[0]\State = 1.0
			
			TFormPoint(5203.0, 1444.0, -1739.0, e\room\OBJ, 0)
			e\room\NPC[1] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			e\room\NPC[1]\State = 0.0 : e\room\NPC[1]\State2 = 10.0
			
			TFormPoint(6234.0, 200.0, -8451.0, e\room\OBJ, 0)
			e\room\NPC[6] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\NPC[6]\Collider, 0.0, e\room\Angle + 90.0, 0.0)
			CreateNPCAsset(e\room\NPC[6])
			e\room\NPC[6]\State = 15.0
			
			e\room\Objects[0] = LoadMesh_Strict(RoomPartsPath + "exit1terrain.b3d", e\room\OBJ)
			ScaleEntity(e\room\Objects[0], RoomScale, RoomScale, RoomScale, True)
			RotateEntity(e\room\Objects[0], 0.0, e\room\Angle, 0.0, True)
			PositionEntity(e\room\Objects[0], e\room\x + 4356.0 * RoomScale, e\room\y - 1017.0 * RoomScale, e\room\z + 2588.0 * RoomScale, True)
			EntityPickMode(e\room\Objects[0], 2)
			
			RenderLoading(60, GetLocalString("loading", "ending"))
			
			Sky = CreateSky("GFX\Map\Textures\sky")
			RotateEntity(Sky, 0.0, e\room\Angle - 90.0, 0.0)
			
			ResetEntity(me\Collider)
			
			RenderLoading(90, GetLocalString("loading", "ending"))
			
			IsBlackOut = False
			
			CreateConsoleMsg("")
			CreateConsoleMsg(GetLocalString("misc", "warning2"), 255, 0, 0)
			CreateConsoleMsg("")
			
			e\EventState = 1.0
			
			RenderLoading(100)
		Else
			UpdateSky(Sky)
			UpdateBreathSteam()
			
			CanSave = 1
			
			Local r.Rooms, e2.Events
			Local i%, TargetX#, TargetY#, TargetZ#, Temp#, Pvt%
			
			For r.Rooms = Each Rooms
				HideRoomsNoColl(r)
			Next
			ShowRoomsNoColl(e\room)
			ShowRoomsColl(e\room)
			
			If e\room\NPC[1] = Null
				TFormPoint(Rnd(2500.0, 3905.0), 1400.0, Rnd(1224.0, 2423.0), e\room\OBJ, 0)
				If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -15.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY() + 0.1, PickedZ(), 32)
				
				TFormPoint(Rnd(1561.0, 2235.0), 1400.0, Rnd(3850.0, 2423.0), e\room\OBJ, 0)
				If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -15.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY() + 0.1, PickedZ(), 32)
				
				TFormPoint(Rnd(2235.0, 2709.0), 1400.0, Rnd(2423.0, 2690.0), e\room\OBJ, 0)
				If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -15.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY() + 0.1, PickedZ(), 32)
				
				TFormPoint(Rnd(4844.0, 5813.0), 1400.0, Rnd(1000.0, 2756.0), e\room\OBJ, 0)
				If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -15.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY() + 0.1, PickedZ(), 32)
				
				TFormPoint(Rnd(3794.0, 5419.0), 1400.0, Rnd(3345.0, 4108.0), e\room\OBJ, 0)
				If LinePick(TFormedX(), TFormedY(), TFormedZ(), 0.0, -15.0, 0.0) Then SetEmitter(Null, PickedX(), PickedY() + 0.1, PickedZ(), 32)
			EndIf
			
			If e\room\NPC[6] <> Null
				If EntityX(me\Collider, True) > e\room\x - 250.0 * RoomScale Then e\room\NPC[6]\State = 16.0
				If EntityX(e\room\NPC[6]\Collider, True) < e\room\x + 2000.0 * RoomScale
					RemoveNPC(e\room\NPC[6])
					For i = SOUND_NPC_VEHICLE_IDLE To SOUND_NPC_VEHICLE_IDLE
						If NPCSound[i] <> 0 Then FreeSound_Strict(NPCSound[i]) : NPCSound[i] = 0
					Next
				EndIf
			EndIf
			If e\EventState < 2.0 And me\SelectedEnding = -1 
				If e\room\NPC[0]\State = 2.0
					ShouldPlay = 6
				Else
					e\EventState2 = ((e\EventState2 + fps\Factor[0]) Mod 3600.0)
					PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\OBJ, True) + Cos(e\EventState2 / 10.0) * 6000.0 * RoomScale, e\room\y + 3216.0 * RoomScale, EntityZ(e\room\OBJ, True) + Sin(e\EventState2 / 10.0) * 6000.0 * RoomScale)
					RotateEntity(e\room\NPC[0]\Collider, 7.0, (e\EventState2 / 10.0), 20.0)
					ShouldPlay = 5
				EndIf
				
				If EntityDistanceSquared(me\Collider, e\room\Objects[3]) < PowTwo(320.0 * RoomScale)
					For i = 2 To 3
						e\room\RoomDoors[i]\Open = False
						e\room\RoomDoors[i]\Locked = 1
					Next
					
					TFormPoint(-5424.0, 200.0, -1068.0, e\room\OBJ, 0)
					e\room\NPC[2] = CreateNPC(NPCTypeApache, TFormedX(), TFormedY(), TFormedZ())
					e\room\NPC[2]\State = 3.0
					
					e\room\NPC[3] = CreateNPC(NPCTypeApache, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True) - 2.0, EntityZ(e\room\Objects[2], True))
					e\room\NPC[3]\State = 3.0
					
					e\room\NPC[0]\State = 3.0
					
					RemoveNPC(e\room\NPC[1])
					e\SoundCHN = StreamSound_Strict("SFX\Ending\GateB\682Battle.ogg", opt\VoiceVolume * opt\MasterVolume)
					e\SoundCHN_IsStream = True
					
					e\EventState = 2.0
				EndIf
			Else
				ShouldPlay = 6
				
				e\EventState = e\EventState + fps\Factor[0]
				
				If e\EventState < 70.0 * 40.0
					TargetX = EntityX(e\room\Objects[4], True)
					TargetY = EntityY(e\room\Objects[4], True)
					TargetZ = EntityZ(e\room\Objects[4], True)
					
					e\room\NPC[0]\EnemyX = TargetX + Sin(MilliSec / 25.0) * 3.0
					e\room\NPC[0]\EnemyY = TargetY + Cos(MilliSec / 85.0) + 9.0
					e\room\NPC[0]\EnemyZ = TargetZ + Cos(MilliSec / 25.0) * 3.0
					
					e\room\NPC[2]\EnemyX = TargetX + Sin(MilliSec / 23.0) * 3.0
					e\room\NPC[2]\EnemyY = TargetY + Cos(MilliSec / 83.0) + 5.0
					e\room\NPC[2]\EnemyZ = TargetZ + Cos(MilliSec / 23.0) * 3.0
					
					If e\room\NPC[3]\State = 3.0 
						e\room\NPC[3]\EnemyX = TargetX + Sin(MilliSec / 20.0) * 3.0
						e\room\NPC[3]\EnemyY = TargetY + Cos(MilliSec / 80.0) + 3.5
						e\room\NPC[3]\EnemyZ = TargetZ + Cos(MilliSec / 20.0) * 3.0
					EndIf
				EndIf
			EndIf
			
			If e\EventState > 70.0 * 0.6 And e\EventState < 70.0 * 42.2
				If e\EventState < 70.0 * 0.7
					me\CameraShake = 0.5
				ElseIf e\EventState > 70.0 * 3.2 And e\EventState < 70.0 * 3.3
					me\CameraShake = 0.5
				ElseIf e\EventState > 70.0 * 6.1 And e\EventState < 70.0 * 6.2
					me\CameraShake = 0.5
				ElseIf e\EventState < 70.0 * 10.8 And e\EventState < 70.0 * 10.9
					me\CameraShake = 0.5
				ElseIf e\EventState > 70.0 * 12.1 And e\EventState < 70.0 * 12.3
					me\CameraShake = 1.0
				ElseIf e\EventState > 70.0 * 13.3 And e\EventState < 70.0 * 13.5
					me\CameraShake = 1.5
				ElseIf e\EventState > 70.0 * 16.5 And e\EventState < 70.0 * 18.5
					me\CameraShake = 3.0
				ElseIf e\EventState > 70.0 * 21.5 And e\EventState < 70.0 * 24.0
					me\CameraShake = 2.0
				ElseIf e\EventState > 70.0 * 25.5 And e\EventState < 70.0 * 27.0
					me\CameraShake = 2.0
				ElseIf e\EventState > 70.0 * 31.0 And e\EventState < 70.0 * 31.5
					me\CameraShake = 0.5
				ElseIf e\EventState > 70.0 * 35.0 And e\EventState < 70.0 * 36.5
					me\CameraShake = 1.5
					If e\EventState - fps\Factor[0] <= 70.0 * 35.0
						e\SoundCHN = StreamSound_Strict("SFX\Ending\GateB\DetonatingAlphaWarheads.ogg", opt\VoiceVolume * opt\MasterVolume)
						e\SoundCHN_IsStream = True
					EndIf
				ElseIf e\EventState > 70.0 * 39.5 And e\EventState < 70.0 * 39.8
					me\CameraShake = 1.0
				ElseIf e\EventState > 70.0 * 42.0
					me\CameraShake = 0.5
					
					; ~ Helicopters leave
					TargetX = EntityX(e\room\Objects[5], True)
					TargetY = EntityY(e\room\Objects[5], True)
					TargetZ = EntityZ(e\room\Objects[5], True)
					
					e\room\NPC[0]\EnemyX = TargetX + 4.0
					e\room\NPC[0]\EnemyY = TargetY + 4.0
					e\room\NPC[0]\EnemyZ = TargetZ + 4.0
					
					e\room\NPC[2]\EnemyX = TargetX
					e\room\NPC[2]\EnemyY = TargetY
					e\room\NPC[2]\EnemyZ = TargetZ
				EndIf
			EndIf
			
			If e\EventState >= 70.0 * 45.0
				If e\EventState < 70.0 * 75.0
					If e\SoundCHN2 = 0
						e\SoundCHN2 = StreamSound_Strict("SFX\Ending\GateB\Siren.ogg", opt\SFXVolume * opt\MasterVolume, ModeLoop)
						e\SoundCHN2_IsStream = True
					EndIf
				Else
					If me\SelectedEnding = -1
						If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
						If e\SoundCHN2 <> 0 Then StopStream_Strict(e\SoundCHN2) : e\SoundCHN2 = 0 : e\SoundCHN2_IsStream = False
						
						Temp = 1.0
						For e2.Events = Each Events
							If e2\EventID = e_room2_nuke
								Temp = e2\EventState
								Exit
							EndIf
						Next
						
						If Temp = 1.0 ; ~ Explode
							ShouldPlay = 66
							me\ExplosionTimer = Max(me\ExplosionTimer, 0.1)
							ResetSelectedStuff()
							me\SelectedEnding = Ending_B2
						Else
							e\SoundCHN2 = StreamSound_Strict("SFX\Ending\GateB\AlphaWarheadsFail.ogg", opt\VoiceVolume * opt\MasterVolume)
							e\SoundCHN2_IsStream = True
							
							TFormPoint(3600.0, -830.0, 6623.0, e\room\OBJ, 0)
							n.NPCs = CreateNPC(NPCTypeMTF, TFormedX(), TFormedY(), TFormedZ())
							e\room\NPC[4] = n
							TFormPoint(4352.0, 200.0, 255.0, e\room\OBJ, 0)
							n.NPCs = CreateNPC(NPCTypeMTF, TFormedX(), TFormedY(), TFormedZ())
							e\room\NPC[5] = n
							
							For i = 4 To 5
								e\room\NPC[i]\EnemyX = EntityX(me\Collider)
								e\room\NPC[i]\EnemyY = EntityY(me\Collider)
								e\room\NPC[i]\EnemyZ = EntityZ(me\Collider)
								e\room\NPC[i]\State = MTF_FOLLOW_PATH
							Next
							
							me\SelectedEnding = Ending_B1
							e\EventState = 70.0 * 85.0
						EndIf
					Else
						If me\SelectedEnding = Ending_B1
							e\room\RoomDoors[4]\Open = True : e\room\RoomDoors[4]\Locked = 0
							
							TargetX = EntityX(e\room\Objects[4], True)
							TargetY = EntityY(e\room\Objects[4], True)
							TargetZ = EntityZ(e\room\Objects[4], True)
							
							e\room\NPC[0]\EnemyX = TargetX + Sin(MilliSec / 25.0) * 3.0
							e\room\NPC[0]\EnemyY = TargetY + Cos(MilliSec / 85.0) + 9.0
							e\room\NPC[0]\EnemyZ = TargetZ + Cos(MilliSec / 25.0) * 3.0
							
							e\room\NPC[2]\EnemyX = TargetX + Sin(MilliSec / 23.0) * 3.0
							e\room\NPC[2]\EnemyY = TargetY + Cos(MilliSec / 83.0) + 5.0
							e\room\NPC[2]\EnemyZ = TargetZ + Cos(MilliSec / 23.0) * 3.0
							
							For i = 4 To 5
								e\room\NPC[i]\EnemyX = EntityX(me\Collider)
								e\room\NPC[i]\EnemyY = EntityY(me\Collider)
								e\room\NPC[i]\EnemyZ = EntityZ(me\Collider)
							Next
							
							If (e\room\NPC[4]\State <> MTF_LOOKING_AT_SOME_TARGET And NPCSeesPlayer(e\room\NPC[4], 4.0 - me\CrouchState) = 1) Lor (e\room\NPC[5]\State <> MTF_LOOKING_AT_SOME_TARGET And NPCSeesPlayer(e\room\NPC[5], 4.0 - me\CrouchState) = 1)
								For i = 4 To 5
									e\room\NPC[i]\State = MTF_LOOKING_AT_SOME_TARGET
								Next
								ShouldPlay = 0
								MakeMeUnplayable()
								e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Ending\GateB\THEREHEIS.ogg"), True)
							EndIf
							
							If e\room\NPC[4]\State = MTF_LOOKING_AT_SOME_TARGET And (Not ChannelPlaying(e\SoundCHN))
								ClearCheats()
								
								PlaySound_Strict(LoadTempSound("SFX\Ending\GateB\Gunshot.ogg"))
								
								me\LightFlash = 1.0
								me\Terminated = True
								msg\DeathMsg = ""
								me\BlinkTimer = -10.0
								
								For n.NPCs = Each NPCs
									If n\NPCType = NPCTypeMTF Then RemoveNPC(n)
								Next
								
								RemoveEvent(e)
								Return
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			
			If e\EventState > 70.0 * 26.5
				If e\EventState3 = 0.0
					e\room\Objects[6] = LoadMesh_Strict("GFX\NPCs\scp_682_arm.b3d")
					ScaleEntity(e\room\Objects[6], 0.15, 0.15, 0.15)
					Temp = (Min(((EntityDistance(e\room\NPC[3]\Collider, me\Collider) / RoomScale) - 3000.0) / 4.0, 1000.0) + 1408.0) * RoomScale
					PositionEntity(e\room\Objects[6], EntityX(e\room\NPC[3]\Collider), e\room\y + 1408.0 * RoomScale, EntityZ(e\room\NPC[3]\Collider))
					RotateEntity(e\room\Objects[6], 0.0, e\room\Angle + Rnd(-10.0, 10.0), 0.0, True)
					TurnEntity(e\room\Objects[6], 0.0, 0.0, 180.0)
					e\EventState3 = 1.0
				Else
					If e\room\Objects[6] <> 0
						If WrapAngle(EntityRoll(e\room\Objects[6])) < 340.0 
							Local Angle# = WrapAngle(EntityRoll(e\room\Objects[6]))
							
							TurnEntity(e\room\Objects[6], 0.0, 0.0, (5.0 + Abs(Sin(Angle)) * 2.0) * fps\Factor[0])
							If Angle < 270.0 And WrapAngle(EntityRoll(e\room\Objects[6])) >= 270.0
								PlaySound_Strict(LoadTempSound("SFX\Character\Apache\Crash0.ogg"))
								e\room\NPC[3]\State = 4.0 : e\room\NPC[3]\State2 = 1.0
								e\room\NPC[3]\EnemyX = EntityX(e\room\Objects[2], True)
								e\room\NPC[3]\EnemyY = EntityY(e\room\Objects[2], True) - 2.5
								e\room\NPC[3]\EnemyZ = EntityZ(e\room\Objects[2], True)
								
								Local emit.Emitter
								
								emit.Emitter = SetEmitter(Null, EntityX(e\room\NPC[3]\Collider), EntityY(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider), 8)
								EntityParent(emit\Owner, e\room\NPC[3]\Collider)
							EndIf
						Else
							FreeEntity(e\room\Objects[6]) : e\room\Objects[6] = 0
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		If e\room\NPC[1] <> Null
			; ~ Helicopter spots or player is within range --> Start shooting
			If EntityDistanceSquared(e\room\NPC[1]\Collider, me\Collider) < 225.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
				e\room\NPC[1]\State = 1.0
				e\room\NPC[1]\State3 = 1.0
			Else
				e\room\NPC[1]\State = 0.0
				e\room\NPC[1]\State3 = 0.0
			EndIf
			
			; ~ Below roof or inside catwalk --> Stop shooting
			If EntityDistanceSquared(e\room\NPC[1]\Collider, me\Collider) < 79.21 Lor EntityDistanceSquared(e\room\Objects[1], me\Collider) < 285.61 Lor chs\NoTarget Lor I_268\InvisibilityOn
				e\room\NPC[1]\State3 = 0.0
			Else
				e\room\NPC[1]\State3 = 1.0
			EndIf
		EndIf
	Else
		HideRoomsNoColl(e\room)
	EndIf
End Function

Function UpdateEvent_Room2_EZ_035%(e.Events)
	Local e2.Events, n.NPCs
	Local Is035Released% = False
	
	For e2.Events = Each Events
		If e2 <> e And e2\EventID = e_cont1_035
			If e2\EventState < 0.0
				Is035Released = True
				Exit
			EndIf
		EndIf
	Next
	
	If Is035Released
		If e\room\Dist < 8.0
			If e\EventState = 0.0
				n.NPCs = CreateNPC(NPCTypeD, e\room\x, e\room\y + 52.0 * RoomScale, e\room\z)
				n\State3 = -1.0 : n\IsDead = True
				SetNPCFrame(n, 19.0)
				RotateEntity(n\Collider, 0.0, e\room\Angle + 180.0, 0.0)
				MoveEntity(n\Collider, 0.0, 0.0, -0.5)
				ChangeNPCTextureID(n, NPC_CLASS_D_VICTIM_035_CORPSE_TEXTURE)
				
				n.NPCs = CreateNPC(NPCType035_Tentacle, e\room\x, e\room\y + 0.22, e\room\z)
				RotateEntity(n\Collider, 0.0, e\room\Angle, 0.0)
				MoveEntity(n\Collider, 0.0, 0.0, 0.6)
				
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function 

Function UpdateEvent_Room2_2_EZ_Duck%(e.Events)
	If PlayerRoom = e\room
		If e\room\Objects[0] = 0
			e\room\Objects[0] = CopyEntity(n_I\NPCModelID[NPC_DUCK_MODEL])
			ScaleEntity(e\room\Objects[0], 0.07, 0.07, 0.07)
			PositionEntity(e\room\Objects[0], 0.0, -500.0, 0.0)
			EntityParent(e\room\Objects[0], e\room\OBJ)
		Else
			Select Rand(4)
				Case 1
					;[Block]
					TFormPoint(-808.0, -72.0, -40.0, e\room\OBJ, 0)
					;[End Block]
				Case 2
					;[Block]
					TFormPoint(-488.0, 160.0, 700, e\room\OBJ, 0)
					;[End Block]
				Case 3
					;[Block]
					TFormPoint(-488.0, 160.0, -700.0, e\room\OBJ, 0)
					;[End Block]
				Case 4
					;[Block]
					TFormPoint(-600.0, 340.0, 0.0, e\room\OBJ, 0)
					;[End Block]
			End Select
			
			If (me\BlinkTimer < -8.0 And me\BlinkTimer > -12.0) Lor wi\IsNVGBlinking
				PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ(), True)
				RotateEntity(e\room\Objects[0], 0.0, Rnd(360.0), 0.0)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Toilets_789_J%(e.Events)
	Local it.Items
	
	Select e\EventState
		Case 0.0
			;[Block]
			TFormPoint(502.0, 128.0, 83.0, e\room\OBJ, 0)
			CreateItem("Document SCP-789-J", it_paper, TFormedX(), TFormedY(), TFormedZ())
			
			e\EventState = 1.0
			;[End Block]
		Case 1.0
			;[Block]
			If PlayerRoom = e\room
				If EntityDistanceSquared(me\Collider, e\room\Objects[0]) < 0.36 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
					GiveAchievement("789j")
					e\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\Joke\789J.ogg"), Camera, e\room\Objects[0], 10.0, 1.0, True)
					
					e\EventState = 2.0
				EndIf
			EndIf
			;[End Block]
		Case 2.0
			;[Block]
			If AnimTime(e\room\Objects[0]) < 30.0
				AnimateEx(e\room\Objects[0], AnimTime(e\room\Objects[0]), 1.0, 30.0, 1.0, False)
			Else
				AnimateEx(e\room\Objects[0], AnimTime(e\room\Objects[0]), 31.0, 240.0, 0.52, False)
			EndIf
			UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 10.0, 1.5)
			If (Not ChannelPlaying(e\SoundCHN)) And (Not EntityInView(e\room\Objects[0], Camera))
				SetAnimTime(e\room\Objects[0], 1.0)
				RemoveEvent(e)
			EndIf
			;[End Block]
	End Select
End Function

Function UpdateEvent_Room2_6_EZ_Guard%(e.Events)
	Select e\EventState
		Case 0.0
			;[Block]
			If e\room\Dist < 7.0 And e\room\Dist > 0.0
				TFormPoint(685.0, 71.0, 980.0, e\room\OBJ, 0)
				e\room\NPC[0] = CreateNPC(NPCTypeGuard, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[0]\State = 8.0 : e\room\NPC[0]\IsDead = True
				SetNPCFrame(e\room\NPC[0], 287.0)
				RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
				e\EventState = 1.0
			EndIf
			;[End Block]
		Case 1.0
			;[Block]
			If e\room\NPC[0]\Sound = 0 Then e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Character\Guard\SuicideGuard0.ogg")
			If e\room\Dist < 6.5
				e\room\NPC[0]\SoundCHN = LoopSoundEx(e\room\NPC[0]\Sound, e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 12.0, 1.0, True)
				If e\room\Dist < 5.7 And (me\SndVolume > 1.0 Lor chs\NoTarget Lor I_268\InvisibilityOn) Then e\EventState = 2.0
			EndIf
			;[End Block]
		Case 2.0
			;[Block]
			TFormPoint(685.0, 150.0, 988.0, e\room\OBJ, 0)
			CreateDecal(DECAL_BLOOD_2, TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle, 0.0, 0.3)
			
			StopChannel(e\room\NPC[0]\SoundCHN) : e\room\NPC[0]\SoundCHN = 0
			FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
			e\room\NPC[0]\SoundCHN = PlaySoundEx(LoadTempSound("SFX\Character\Guard\SuicideGuard1.ogg"), Camera, e\room\NPC[0]\Collider, 12.0, 1.0, True)
			e\EventState = 3.0
			;[End Block]
		Case 3.0
			;[Block]
			UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 15.0)
			If (Not ChannelPlaying(e\room\NPC[0]\SoundCHN)) Then RemoveEvent(e)
			;[End Block]
	End Select
End Function

Function UpdateEvent_Room2_Cafeteria%(e.Events)
	If PlayerRoom = e\room
		Local it.Items
		
		If InteractObject(e\room\Objects[0], 1.0)
			Local Temp% = True
			
			For it.Items = Each Items
				If (Not it\Picked)
					If DistanceSquared(EntityX(it\Collider), EntityX(e\room\Objects[2], True), EntityZ(it\Collider), EntityZ(e\room\Objects[2], True)) = 0.0
						Temp = False
						Exit
					EndIf
				EndIf
			Next
			
			Local Inserted% = False
			
			If e\EventState2 < 2.0
				If SelectedItem <> Null
					If SelectedItem\ItemTemplate\ID = it_25ct Lor SelectedItem\ItemTemplate\ID = it_coin
						RemoveItem(SelectedItem)
						PlaySound_Strict(LoadTempSound("SFX\SCP\294\CoinDrop.ogg"))
						Inserted = True
						
						e\EventState2 = e\EventState2 + 1.0
					ElseIf SelectedItem\ItemTemplate\ID = it_mastercard Lor SelectedItem\ItemTemplate\ID = it_mastercard_golden
						me\UsedMastercard = 1 + (SelectedItem\ItemTemplate\ID = it_mastercard_golden)
						If SelectedItem\State > 0
							SelectedItem\State = SelectedItem\State - 1
							me\CurrFunds = SelectedItem\State
							
							PlaySound_Strict(LoadTempSound("SFX\SCP\294\InsertMasterCard.ogg"))
							
							Inserted = True
							RemoveItem(SelectedItem)
							
							e\EventState2 = 2.0
						EndIf
					EndIf
				EndIf
			EndIf
			If Temp
				If e\EventState2 = 2.0
					GiveAchievement("294")
					
					I_294\Using = (Temp And SecondaryLightOn > 0.1)
					If I_294\Using
						SelectedItem = Null
						mo\MouseHit1 = False
					EndIf
				ElseIf e\EventState2 = 1.0 And (Not Inserted) And me\UsedMastercard = 0
					I_294\Using = False
					CreateMsg(GetLocalString("msg", "294.another"))
				ElseIf (Not Inserted) And me\UsedMastercard = 0
					I_294\Using = False
					CreateMsg(GetLocalString("msg", "294.two"))
				ElseIf me\UsedMastercard > 0
					CreateMsg(GetLocalString("msg", "294.funds"))
					me\UsedMastercard = 0
				EndIf
			EndIf
		EndIf
		
		If InteractObject(e\room\Objects[1], 0.8)
			If ItemAmount < MaxItemAmount
				GiveAchievement("458")
				CreateMsg(GetLocalString("msg", "458"))
				it.Items = CreateItem("Pizza Slice", it_pizza, 0.0, 0.0, 0.0)
				PickItem(it)
			Else
				CreateMsg(GetLocalString("msg", "cantcarry"))
			EndIf
		EndIf
	EndIf
	
	If e\room\Dist < 8.0
		If e\EventState = 0.0
			If n_I\Curr066 = Null
				n_I\Curr066 = CreateNPC(NPCType066, EntityX(e\room\OBJ), e\room\y + 0.2, EntityZ(e\room\OBJ))
			Else
				If n_I\Curr066\State <> 66.0
					TeleportEntity(n_I\Curr066\Collider, EntityX(e\room\OBJ), e\room\y + 0.2, EntityZ(e\room\OBJ), n_I\Curr066\CollRadius, True)
					n_I\Curr066\CurrentRoom = e\room
				EndIf
			EndIf
			
			Local n.NPCs
			Local i%
			
			TFormPoint(-437.0, -384.0, 36.0, e\room\OBJ, 0)
			n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), TFormedY() + 0.05, TFormedZ())
			RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
			
			TFormPoint(-1118.0, -384.0, -606.0, e\room\OBJ, 0)
			For i = 0 To 1
				n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX() + Rnd(-0.8, 0.8), TFormedY() + 0.05, TFormedZ() + Rnd(-0.8, 0.8))
				RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
			Next
			
			TFormPoint(1330.0, -384.0, 432.0, e\room\OBJ, 0)
			n.NPCs = CreateNPC(NPCTypeCockroach, TFormedX(), TFormedY() + 0.05, TFormedZ())
			RotateEntity(n\Collider, EntityPitch(n\Collider), Rnd(360.0), EntityRoll(n\Collider))
			
			e\EventState = 1.0
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_IC%(e.Events)
	If PlayerRoom = e\room
		Local n.NPCs
		
		TFormPoint(-1200.0, 51.2, 0.0, e\room\OBJ, 0)
		
		Local x1# = TFormedX(), y1# = TFormedY(), z1# = TFormedZ()
		
		CreateDecal(DECAL_BLOOD_2, x1, y1 - (51.2 * RoomScale) + 0.005, z1, 90.0, Rnd(360.0), 0.0)
		
		n.NPCs = CreateNPC(NPCTypeD, x1, y1, z1)
		n\State3 = -1.0 : n\IsDead = True
		ChangeNPCTextureID(n, NPC_CLASS_D_GONZALES_TEXTURE)
		SetNPCFrame(n, 19.0)
		RotateEntity(n\Collider, 0.0, EntityYaw(e\room\OBJ) - 80.0, 0.0, True)
		
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_Medibay%(e.Events)
	If PlayerRoom = e\room
		If e\EventState = 0.0
			TFormPoint(-820.0, 200.0, -464.0, e\room\OBJ, 0)
			e\room\NPC[0] = CreateNPC(NPCType008_1_Surgeon, TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
			
			e\EventState = 1.0
		ElseIf e\EventState = 1.0
			If EntityDistanceSquared(e\room\NPC[0]\Collider, me\Collider) < 1.96
				me\LightBlink = 9.0
				PlaySound_Strict(snd_I\LightOffSFX)
				e\room\NPC[0]\State = 1.0
				e\EventState = 2.0
			EndIf
		EndIf
		If e\room\Objects[1] = 0
			; ~ Orange duck
			TFormPoint(-910.0, 144.0, -778.0, e\room\OBJ, 0)
			e\room\Objects[1] = CopyEntity(n_I\NPCModelID[NPC_DUCK_MODEL])
			ScaleEntity(e\room\Objects[1], 0.07, 0.07, 0.07)
			PositionEntity(e\room\Objects[1], TFormedX(), TFormedY(), TFormedZ())
			RotateEntity(e\room\Objects[1], 6.0, e\room\Angle + 180.0, 0.0)
			EntityPickMode(e\room\Objects[1], 1)
			EntityRadius(e\room\Objects[1], 0.3)
			
			Local Tex% = LoadTexture_Strict("GFX\NPCs\duck(4).png")
			
			EntityTexture(e\room\Objects[1], Tex)
			DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EntityParent(e\room\Objects[1], e\room\OBJ)
		Else
			If InteractObject(e\room\Objects[1], 0.8)
				CreateMsg(GetLocalString("msg", "breeze"))
				me\Injuries = Max(0.0, me\Injuries - Rnd(0.3))
				me\Bloodloss = 0.0
				PlaySound_Strict(LoadTempSound("SFX\SCP\Joke\Quack.ogg"))
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Office%(e.Events)
	If e\room\Dist < 6.0
		TFormPoint(820.0, -288.0, 0.0, e\room\OBJ, 0)
		n_I\Curr999 = CreateNPC(NPCType999, TFormedX(), TFormedY(), TFormedZ())
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Room2_Office_3%(e.Events)
	If PlayerRoom = e\room
		If e\EventState = 1.0 And RemoteDoorOn
			If EntityDistanceSquared(me\Collider, e\room\RoomDoors[1]\OBJ) < 4.0
				If (Not e\room\RoomDoors[0]\Open) Then OpenCloseDoor(e\room\RoomDoors[0])
				If (Not e\room\RoomDoors[1]\Open)
					OpenCloseDoor(e\room\RoomDoors[1])
				Else
					If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
					e\SoundCHN = StreamSound_Strict("SFX\SCP\079\Reward.ogg", opt\VoiceVolume * opt\MasterVolume)
					e\SoundCHN_IsStream = True
					e\EventState = 2.0
				EndIf
			EndIf
		ElseIf e\EventState = 0.0 And e\room\RoomDoors[1]\Open
			RemoveEvent(e)
		ElseIf e\EventState = 2.0 And (e\SoundCHN = 0 Lor (Not IsStreamPlaying_Strict(e\SoundCHN)))
			RemoveEvent(e)
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2_Scientists_2%(e.Events)
	If PlayerRoom = e\room
		If e\EventState = 0.0
			If e\room\RoomDoors[0]\Open
				If e\room\RoomDoors[0]\OpenState = 180.0
					PlaySound_Strict(snd_I\HorrorSFX[5])
					e\EventState = 1.0
				EndIf
			Else
				If EntityDistanceSquared(me\Collider, e\room\RoomDoors[0]\OBJ) < 2.25 And RemoteDoorOn
					PlaySoundEx(snd_I\DoorOpenHorror, Camera, e\room\RoomDoors[0]\FrameOBJ, 7.0)
					OpenCloseDoor(e\room\RoomDoors[0])
				EndIf
			EndIf
		Else
			If e\room\Objects[0] = 0
				TFormPoint(-808.0, 150.0, -72.0, e\room\OBJ, 0)
				e\room\Objects[0] = CreatePivot()
				PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ())
				EntityParent(e\room\Objects[0], e\room\OBJ)
			Else
				If EntityDistanceSquared(e\room\Objects[0], me\Collider) < 4.0
					me\HeartBeatVolume = CurveValue(0.5, me\HeartBeatVolume, 5.0)
					me\HeartBeatRate = CurveValue(120.0, me\HeartBeatRate, 150.0) 
					e\SoundCHN = LoopSoundEx(snd_I\SCP106SFX[4], e\SoundCHN, Camera, e\room\OBJ, 5.0, 0.3, True)
					If (Not n_I\Curr106\Contained)
						If n_I\Curr106\State = 1.0 Then n_I\Curr106\State2 = n_I\Curr106\State2 - (fps\Factor[0] * 3.0)
					EndIf
					me\Injuries = me\Injuries + (fps\Factor[0] * 0.0001)
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Cont2_860_1%(e.Events)
	; ~ e\EventState: Is the player in the forest
	
	; ~ e\EventState2: Which side of the door did the player enter from
	
	; ~ e\EventState3: SCP-860-2 spawn timer
	
	; ~ e\EventState4: Red key or blue key
	
	Local fr.Forest = e\room\fr
	Local i%
	
	If PlayerRoom = e\room And fr <> Null
		If (Not EntityHidden(fr\Forest_Pivot)) Then HideEntity(fr\Forest_Pivot)
		; ~ Reset the doors after leaving the forest
		For i = 0 To 1
			fr\ForestDoors[i]\Open = False
			fr\ForestDoors[i]\Locked = 2
		Next
		
		If e\room\RoomDoors[0]\Open
			If EntityHidden(fr\Forest_Pivot) Then ShowEntity(fr\Forest_Pivot)
			
			GiveAchievement("860")
			
			me\BlinkTimer = -10.0
			
			; ~ Reset monster to the (hidden) idle state
			If e\room\NPC[0] <> Null Then e\room\NPC[0]\State = 0.0
			
			; ~ Reset the door leading to the forest
			e\room\RoomDoors[0]\Open = False
			e\room\RoomDoors[0]\Locked = 1
			
			PrevIsBlackOut = IsBlackOut : IsBlackOut = False
			
			Local Pvt% = CreatePivot()
			
			PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
			PointEntity(Pvt, e\room\OBJ)
			
			Local Angle# = WrapAngle(EntityYaw(Pvt) - EntityYaw(e\room\OBJ, True))
			
			i = (Angle <= 90.0 Lor Angle >= 270.0)
			
			PositionEntity(me\Collider, EntityX(fr\ForestDoors[i]\FrameOBJ, True), EntityY(fr\ForestDoors[i]\FrameOBJ, True) + EntityY(me\Collider, True) + 0.5, EntityZ(fr\ForestDoors[i]\FrameOBJ, True), True)
			RotateEntity(me\Collider, 0.0, EntityYaw(fr\ForestDoors[i]\FrameOBJ, True) - 180.0, 0.0, True)
			MoveEntity(me\Collider, -0.5, 0.0, 0.5)
			
			; ~ Determine the locked door
			fr\ForestDoors[0]\Locked = 2 - i
			fr\ForestDoors[1]\Locked = 1 + i
			
			e\EventState2 = (1.0 - i)
			
			FreeEntity(Pvt) : Pvt = 0
			ResetEntity(me\Collider)
			
			; ~ Reset monster spawn timer
			e\EventState3 = 0.0
			; ~ The player has entered the forest
			e\EventState = 1.0
		EndIf
	Else
		If fr <> Null
			If (Not EntityHidden(fr\Forest_Pivot)) Then HideEntity(fr\Forest_Pivot)
		Else
			RemoveEvent(e)
		EndIf
	EndIf
End Function

Function UpdateEvent_Room2C_EC%(e.Events)
	If PlayerRoom = e\room
		; ~ Primary Lighting
		UpdateLever(e\room\RoomLevers[0]\OBJ)
		
		; ~ Secondary Lighting
		Local PrevState2# = e\EventState2
		
		e\EventState2 = UpdateLever(e\room\RoomLevers[1]\OBJ)
		If PrevState2 <> e\EventState2 And e\EventState > 0.0 Then PlaySoundEx(snd_I\LightOffSFX, Camera, e\room\RoomLevers[1]\OBJ)
		IsBlackOut = (e\EventState2 = 0.0)
		fog\FarDist = 6.0
		
		If e\EventState = 0.0
			If Rand(200) = 1 Lor EntityY(me\Collider, True) > 2.0
				PlaySound_Strict(snd_I\HorrorSFX[7])
				e\EventState = 1.0
			EndIf
		EndIf
		
		If e\EventState = 1.0
			RotateEntity(e\room\RoomLevers[1]\OBJ, CurveValue(80.0, EntityPitch(e\room\RoomLevers[1]\OBJ), 10.0), EntityYaw(e\room\RoomLevers[1]\OBJ), 0.0)
			If EntityPitch(e\room\RoomLevers[1]\OBJ) > 79.0 Then e\EventState = 2.0
		EndIf
		
		; ~ Remote Door Control
		RemoteDoorOn = UpdateLever(e\room\RoomLevers[2]\OBJ)
		
		If (Not RemoteDoorOn) And e\EventState3 = 0.0
			If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
			e\SoundCHN = StreamSound_Strict("SFX\SCP\079\Angry.ogg", opt\VoiceVolume * opt\MasterVolume)
			e\SoundCHN_IsStream = True
			e\EventState3 = 1.0
		EndIf
	EndIf
End Function

Function UpdateEvent_Room3_2_EZ_Duck%(e.Events)
	If PlayerRoom = e\room
		If e\room\Objects[3] = 0
			TFormPoint(928.0, -640.0, 704.0, e\room\OBJ, 0) 
			e\room\Objects[3] = CopyEntity(n_I\NPCModelID[NPC_DUCK_MODEL])
			ScaleEntity(e\room\Objects[3], 0.07, 0.07, 0.07)
			PositionEntity(e\room\Objects[3], TFormedX(), TFormedY(), TFormedZ())
			
			Local Tex% = LoadTexture_Strict("GFX\NPCs\duck(2).png")
			
			EntityTexture(e\room\Objects[3], Tex)
			DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EntityParent(e\room\Objects[3], e\room\OBJ)
		Else
			If (me\BlinkTimer > -12.0 And me\BlinkTimer < -8.0) Lor wi\IsNVGBlinking
				PointEntity(e\room\Objects[3], me\Collider)
				RotateEntity(e\room\Objects[3], 0.0, EntityYaw(e\room\Objects[3], True), 0.0, True)
			EndIf
			If e\EventState2 = 0.0
				e\EventState = CurveValue(0.0, e\EventState, 15.0)
				If Rand(800) = 1 Then e\EventState2 = 1.0
			Else
				e\EventState = e\EventState + (fps\Factor[0] * 0.5)
				If e\EventState > 360.0 Then e\EventState = 0.0
				
				If Rand(1200) = 1 Then e\EventState2 = 0.0
			EndIf
			PositionEntity(e\room\Objects[3], EntityX(e\room\Objects[3], True), (-608.0 * RoomScale) + 0.05 + Sin(e\EventState + 270.0) * 0.05, EntityZ(e\room\Objects[3], True), True)
		EndIf
	EndIf
End Function

; ~ Pocket Dimension Constants
;[Block]
Const PD_StartRoom% = 0
Const PD_FourWayRoom% = 1
Const PD_ThroneRoom% = 2
Const PD_TrenchesRoom% = 3
Const PD_ExitRoom% = 4
Const PD_FakeTunnelRoom% = 5
Const PD_TowerRoom% = 6
Const PD_Labyrinth% = 7
;[End Block]

Function UpdateEvent_Dimension_106%(e.Events)
	; ~ e\EventState: A timer for scaling the tunnels in the starting room
	
	; ~ e\EventState2: Detects where the player is
	
	; ~ e\EventState3: A timer for objects
	
	If PlayerRoom = e\room
		Local r.Rooms, e2.Events, de.Decals, p.Particles, d.Doors
		Local i%, Angle#, Dist#, Pvt%, SinValue#, CosValue#, SqrValue#, x#, y#, z#
		
		For r.Rooms = Each Rooms
			HideRoomsNoColl(r)
		Next
		ShowRoomsNoColl(e\room)
		ShowRoomsColl(e\room)
		
		PlayerFallingPickDistance = 0.0
		PrevIsBlackOut = IsBlackOut : IsBlackOut = False
		; ~ SCP-106 attacks if close enough to player
		If EntityDistanceSquared(me\Collider, n_I\Curr106\Collider) < 0.09 And n_I\Curr106\State < 3.0
			n_I\Curr106\Idle = 0
			n_I\Curr106\State = 3.0
			n_I\Curr106\State2 = 100000.0
		EndIf
		CanSave = 1
		If e\EventState2 <> PD_FakeTunnelRoom
			ShouldPlay = 3
			
			If wi\HazmatSuit = 0
				me\Injuries = me\Injuries + (fps\Factor[0] * 0.0001 * (1.0 + (wi\NightVision > 0 Lor wi\SCRAMBLE > 0)))
			Else
				me\Injuries = me\Injuries + (fps\Factor[0] * 0.00005)
			EndIf
			
			fog\FarDist = 6.0
			
			e\EventState = e\EventState + fps\Factor[0]
			
			SinValue = e\EventState / 14.0
			ScaleEntity(e\room\OBJ, RoomScale, RoomScale * (1.0 + Sin(SinValue) * 0.2), RoomScale)
			For i = 9 To 10
				ScaleEntity(e\room\Objects[i], RoomScale * (1.5 + Abs(Sin(e\EventState / 21.0 + i * 45.0) * 0.1)), RoomScale * (1.0 + Sin(SinValue + i * 20.0) * 0.1), RoomScale, True)
			Next
		ElseIf SelectedDifficulty\SaveType < DIFFICULTY_SAVE_TYPE_SAVE_ON_QUIT
			If KeyHit(key\SAVE)
				Select SelectedDifficulty\SaveType
					Case DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE
						;[Block]
						PlaySound_Strict(LoadTempSound("SFX\General\Save0.ogg"))
						CreateHintMsg(GetLocalString("save", "saved"))
						;[End Block]
					Case DIFFICULTY_SAVE_TYPE_SAVE_ON_SCREENS
						;[Block]
						CreateHintMsg(GetLocalString("save", "failed.screen"))
						;[End Block]
				End Select
			EndIf
		EndIf
		
		Local RoomExist%
		Local Teleport% = False, Random% = Rand(39)
		
		If e\EventState2 <> PD_TrenchesRoom And Sky106 <> 0 Then FreeEntity(Sky106) : Sky106 = 0
		Select e\EventState2
			Case PD_StartRoom
				;[Block]
				For i = 0 To 7
					ScaleEntity(e\room\Objects[i], RoomScale * (1.0 + Abs(Sin(e\EventState / 21.0 + i * 45.0) * 0.1)), RoomScale * (1.0 + Sin(SinValue + i * 20.0) * 0.1), RoomScale, True)
				Next
				
				If n_I\Curr106\State < 3.0 ; ~ SCP-106 circles around the starting room
					Angle = (e\EventState / 10.0 Mod 360.0)
					PositionEntity(n_I\Curr106\Collider, e\room\x, e\room\y + 0.55 + Sin(SinValue * 20.0) * 0.4, e\room\z)
					RotateEntity(n_I\Curr106\Collider, 0.0, Angle, 0.0)
					MoveEntity(n_I\Curr106\Collider, 0.0, 0.0, 6.0 - Sin(e\EventState / 10.0))
					AnimateNPC(n_I\Curr106, 55.0, 104.0, 0.5)
					RotateEntity(n_I\Curr106\Collider, 0.0, Angle + 90.0, 0.0)
					If EntityHidden(n_I\Curr106\OBJ) Lor EntityHidden(n_I\Curr106\Collider)
						ShowEntity(n_I\Curr106\OBJ)
						ShowEntity(n_I\Curr106\Collider)
					EndIf
					ResetEntity(n_I\Curr106\Collider)
					n_I\Curr106\Idle = 1 : n_I\Curr106\GravityMult = 0.0 : n_I\Curr106\DropSpeed = 0.0
					PositionEntity(n_I\Curr106\OBJ, EntityX(n_I\Curr106\Collider), EntityY(n_I\Curr106\Collider), EntityZ(n_I\Curr106\Collider))
					RotateEntity(n_I\Curr106\OBJ, 0.0, EntityYaw(n_I\Curr106\Collider), 0.0)
				EndIf
				If e\EventState > 70.0 * 65.0
					If Rand(800) = 1
						PlaySound_Strict(snd_I\HorrorSFX[8])
						n_I\Curr106\Idle = 0
						n_I\Curr106\State = 3.0
						n_I\Curr106\State2 = 100000.0
						e\EventState = 601.0
					EndIf
				EndIf
				If EntityDistanceSquared(me\Collider, e\room\OBJ) > PowTwo(-1200.0 * RoomScale)
					For e2.Events = Each Events
						If e2\EventID = e_cont1_005
							If e2\EventState = 1.0 Then Random = 40
							Exit
						EndIf
					Next
					Teleport = True
				EndIf
				;[End Block]
			Case PD_FourWayRoom
				;[Block]
				SinValue = Sin(e\EventState * 1.6) * 4.0
				CosValue = Cos(e\EventState * 0.8) * 5.0
				
				PositionEntity(e\room\Objects[9], EntityX(e\room\Objects[8], True) + 3384.0 * RoomScale, e\room\y, EntityZ(e\room\Objects[8], True))
				
				TranslateEntity(e\room\Objects[9], CosValue, 0.0, SinValue, True)
				RotateEntity(e\room\Objects[9], 0.0, e\EventState * 2.0, 0.0)
				
				PositionEntity(e\room\Objects[10], EntityX(e\room\Objects[8], True), e\room\y, EntityZ(e\room\Objects[8], True) + 3384.0 * RoomScale)
				
				TranslateEntity(e\room\Objects[10], SinValue, 0.0, CosValue, True)
				RotateEntity(e\room\Objects[10], 0.0, e\EventState * 2.0, 0.0)
				
				If (Not chs\GodMode)
					For i = 9 To 10
						Dist = DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[i], True), EntityZ(me\Collider), EntityZ(e\room\Objects[i], True))
						If Dist < 36.0
							If Dist < PowTwo(100.0 * RoomScale)
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(e\room\Objects[i], True), EntityY(me\Collider), EntityZ(e\room\Objects[i], True))
								PointEntity(Pvt, me\Collider)
								RotateEntity(Pvt, 0.0, Int(EntityYaw(Pvt) / 90.0) * 90.0, 0.0, True)
								MoveEntity(Pvt, 0.0, 0.0, 100.0 * RoomScale)
								PositionEntity(me\Collider, EntityX(Pvt), EntityY(me\Collider), EntityZ(Pvt))
								FreeEntity(Pvt) : Pvt = 0
								
								If (Not chs\GodMode) And (Not me\Terminated)
									msg\DeathMsg = GetLocalString("death", "106_1")
									PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Impact.ogg"))
									me\BlurTimer = 3000.0
									Kill(True, False)
								EndIf
							EndIf
							e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\Objects[i], 6.0)
						EndIf
					Next
				EndIf
				
				Pvt = CreatePivot()
				PositionEntity(Pvt, EntityX(e\room\Objects[8], True) - 1536.0 * RoomScale, e\room\y + 500.0 * RoomScale, EntityZ(e\room\Objects[8], True) + 608.0 * RoomScale)
				If EntityDistanceSquared(Pvt, me\Collider) < 25.0 Then e\SoundCHN2 = LoopSoundEx(e\Sound2, e\SoundCHN2, Camera, Pvt, 3.0)
				FreeEntity(Pvt) : Pvt = 0
				
				If EntityY(me\Collider) < (-1600.0) * RoomScale
					If EntityDistanceSquared(me\Collider, e\room\Objects[8]) > PowTwo(4750.0 * RoomScale) And (Not me\Terminated)
						Random = Rand(14, 30)
						Teleport = True
					Else ; ~ The player is not at the exit, must've fallen down
						If (Not chs\GodMode) And (Not me\Terminated)
							PlaySound_Strict(snd_I\HorrorSFX[8])
							msg\DeathMsg = GetLocalString("death", "106_2")
							me\BlurTimer = 3000.0
							Kill(False, False)
						EndIf
					EndIf
				EndIf
				
				If EntityDistanceSquared(me\Collider, e\room\Objects[17]) < PowTwo(2000.0 * RoomScale)
					LoadEventSound(e, "SFX\Room\PocketDimension\Screech.ogg")
					LoadEventSound(e, "SFX\Room\PocketDimension\Kneel.ogg", 1)
					SetNPCFrame(n_I\Curr106, 605.0)
					n_I\Curr106\ModelScale = 0.2136
					ScaleEntity(n_I\Curr106\OBJ, n_I\Curr106\ModelScale, n_I\Curr106\ModelScale, n_I\Curr106\ModelScale)
					n_I\Curr106\State = 5.0
					e\EventState2 = PD_ThroneRoom
				EndIf
				;[End Block]
			Case PD_ThroneRoom
				;[Block]
				Dist = EntityDistanceSquared(me\Collider, e\room\Objects[17])
				SqrValue = Sqr(Dist)
				
				If Dist >= PowTwo(2000.0 * RoomScale)
					LoadEventSound(e, "SFX\Room\PocketDimension\Rumble.ogg")
					LoadEventSound(e, "SFX\Room\PocketDimension\PrisonVoices.ogg", 1)
					e\EventState2 = PD_FourWayRoom
				EndIf
				
				If EntityHidden(e\room\Objects[18]) Then ShowEntity(e\room\Objects[18])
				
				me\Injuries = me\Injuries + (fps\Factor[0] * 0.0004 - (0.0002 * (wi\HazmatSuit > 0)))
				
				me\Sanity = Max(me\Sanity - fps\Factor[0] / SqrValue / 8.0, -1000.0)
				
				me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * 15.0 * Max((6.0 - SqrValue) / 6.0, 0.0))
				
				Pvt = CreatePivot()
				PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
				PointEntity(Pvt, e\room\Objects[17])
				TurnEntity(Pvt, 90.0, 0.0, 0.0)
				Dist = Clamp(15000.0 / (-me\Sanity), 15.0, 500.0)
				CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Dist)
				CameraPitch = CameraPitch - 90.0
				RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Dist), 0.0)
				FreeEntity(Pvt) : Pvt = 0
				
				n_I\Curr106\Idle = 0 : n_I\Curr106\GravityMult = 0.0 : n_I\Curr106\DropSpeed = 0.0
				PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[17], True), EntityY(e\room\Objects[17], True) / (1.0 + Sin(SinValue) * 0.2), EntityZ(e\room\Objects[17], True), True)
				RotateEntity(n_I\Curr106\Collider, 0.0, e\room\Angle, 0.0)
				
				; ~ Teleport the player to the trenches
				If me\Crouch
					me\BlinkTimer = -10.0 : me\Crouch = False
					PositionEntity(me\Collider, EntityX(e\room\Objects[8], True) - 1344.0 * RoomScale, e\room\y + 16.0 + 2944.0 * RoomScale, EntityZ(e\room\Objects[8], True) - 1184.0 * RoomScale)
					ResetEntity(me\Collider)
					
					n_I\Curr106\ModelScale = 0.1136
					ScaleEntity(n_I\Curr106\OBJ, n_I\Curr106\ModelScale, n_I\Curr106\ModelScale, n_I\Curr106\ModelScale)
					
					LoadEventSound(e, "SFX\Room\PocketDimension\Explosion.ogg")
					LoadEventSound(e, "SFX\Room\PocketDimension\TrenchPlane.ogg", 1)
					
					e\EventState2 = PD_TrenchesRoom
				EndIf
				;[End Block]
			Case PD_TrenchesRoom
				;[Block]
				ShouldPlay = 14
				DecalStep = 1
				If Sky106 = 0 Then Sky106 = CreateSky("GFX\Map\Textures\106sky")
				UpdateSky(Sky106, True)
				
				For i = 17 To 20
					If i > 18
						If EntityHidden(e\room\Objects[i]) Then ShowEntity(e\room\Objects[i])
					Else
						If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
					EndIf
				Next
				
				If EntityX(e\room\Objects[19], True) < EntityX(e\room\Objects[8], True) - 4000.0 * RoomScale
					e\SoundCHN2 = PlaySound_Strict(e\Sound2)
					
					PositionEntity(e\room\Objects[19], EntityX(me\Collider, True) + 4000.0 * RoomScale, e\room\y + 28.0, EntityZ(me\Collider, True))
				EndIf
				
				MoveEntity(me\Collider, 0.0, Min((28.0 - EntityY(me\Collider)), 0.0) * fps\Factor[0], 0.0)
				
				x = (-fps\Factor[0]) * RoomScale * 4.0
				y = (33.0 - Abs(EntityX(me\Collider) - EntityX(e\room\Objects[19])) * 0.5) - EntityY(e\room\Objects[19])
				z = EntityZ(me\Collider, True) - EntityZ(e\room\Objects[19])
				TranslateEntity(e\room\Objects[19], x, y, z, True)
				RotateEntity(e\room\Objects[19], -90.0 - (EntityX(me\Collider) - EntityX(e\room\Objects[19])) * 1.5, -90.0, 0.0, True)
				
				; ~ Check if the plane can see the player
				Local Safe% = False
				
				For i = 0 To 2
					Select i
						Case 0
							;[Block]
							x = (-1452.0) * RoomScale
							z = (-37.0) * RoomScale
							;[End Block]
						Case 1
							;[Block]
							x = (-121.0) * RoomScale
							z = 188.0 * RoomScale
							;[End Block]
						Case 2
							;[Block]
							x = 1223.0 * RoomScale
							z = (-196.0) * RoomScale
							;[End Block]
					End Select
					
					x = x + EntityX(e\room\Objects[8], True)
					z = z + EntityZ(e\room\Objects[8], True)
					
					If DistanceSquared(EntityX(me\Collider), x, EntityZ(me\Collider), z) < PowTwo(200.0 * RoomScale)
						Safe = True
						Exit
					EndIf
				Next
				
				Dist = EntityDistanceSquared(me\Collider, e\room\Objects[19])
				SqrValue = Sqr(Dist)
				
				e\SoundCHN2 = LoopSoundEx(e\Sound2, e\SoundCHN2, Camera, Camera, 10.0, 0.3 + (Not Safe) * 0.7)
				
				If Safe Lor chs\NoTarget Lor I_268\InvisibilityOn
					EntityTexture(e\room\Objects[19], e\room\Textures[0])
				ElseIf Dist < 64.0 And I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4
					e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\Objects[19], 8.0)
					EntityTexture(e\room\Objects[19], e\room\Textures[1])
					me\Injuries = me\Injuries + ((8.0 - SqrValue) * (fps\Factor[0] * (0.0006 / (1.0 + (I_714\Using = 1)))))
					
					If Dist < 49.0
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
						PointEntity(Pvt, e\room\Objects[19])
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, 10.0)
						CameraPitch = CameraPitch - 90.0
						RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), 10.0), 0.0)
						FreeEntity(Pvt) : Pvt = 0
					EndIf
				ElseIf Dist < 64.0
					EntityTexture(e\room\Objects[19], e\room\Textures[0])
					me\Injuries = me\Injuries + ((8.0 - SqrValue) * (fps\Factor[0] * 0.0002))
				EndIf
				
				If I_714\Using = 2 Lor chs\NoTarget Lor I_268\InvisibilityOn
					me\CameraShake = Max(3.0 + ((Not Safe) * 3.0) - SqrValue, 0.0)
				Else                        
					me\BigCameraShake = Max(4.0 + ((Not Safe) * 4.0) - SqrValue, 0.0)
				EndIf
				
				; ~ Check if player is at the sinkhole (the exit from the trench room)
				If EntityY(me\Collider) < 24.5
					StopChannel(e\SoundCHN) : e\SoundCHN = 0
					StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
					Random = 18
					Teleport = True
				EndIf
				;[End Block]
			Case PD_ExitRoom
				;[Block]
				Dist = DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[8], True) + 1024.0 * RoomScale, EntityZ(me\Collider), EntityZ(e\room\Objects[8], True))
				If Dist < PowTwo(640.0 * RoomScale)
					SqrValue = Sqr(Dist)
					me\BlurTimer = ((640.0 * RoomScale) - SqrValue) * 3000.0
					
					e\SoundCHN2 = LoopSoundEx(snd_I\DecaySFX[Rand(3)], e\SoundCHN2, Camera, me\Collider, 2.0, (640.0 * RoomScale - SqrValue) * Abs(me\CurrSpeed) * 100.0)
					me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, SqrValue * 10.0)
					
					If Dist < PowTwo(130.0 * RoomScale)
						RoomExist = False
						For r.Rooms = Each Rooms
							If r\RoomTemplate\RoomID = r_room2_shaft
								RoomExist = True
								
								GiveAchievement("pocketdimension")
								
								IsBlackOut = PrevIsBlackOut : PrevIsBlackOut = True
								
								me\BlinkTimer = -10.0 : me\BlurTimer = 1500.0
								
								If me\Playable = 2
									e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Exit.ogg"))
									MakeMeUnplayable()
								EndIf
								If (Not ChannelPlaying(e\SoundCHN))
									TeleportEntity(me\Collider, EntityX(r\Objects[0], True), 0.6, EntityZ(r\Objects[0], True), 0.3, True)
									TeleportToRoom(r)
									
									n_I\Curr106\Idle = 0
									n_I\Curr106\State = 0.0
									n_I\Curr106\State2 = Rnd(10000.0, 12000.0)
									
									de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(r\Objects[0], True), EntityY(r\Objects[0], True), EntityZ(r\Objects[0], True), 270.0, Rnd(360.0), 0.0)
									TeleportEntity(de\OBJ, EntityX(r\Objects[0], True), EntityY(r\Objects[0], True) + 0.6, EntityZ(r\Objects[0], True), 0.0, True, 4.0, True)
									
									For e2.Events = Each Events
										If e2\EventID = e_room2_sl
											e2\EventState2 = 7.0
											e2\EventState3 = 0.0
											UpdateLever(e2\room\RoomLevers[0]\OBJ)
											RotateEntity(e2\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e2\room\RoomLevers[0]\OBJ), 0.0)
											Exit
										EndIf
									Next
									
									fog\FarDist = 6.0
									me\Playable = 2
									
									If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
									If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
									
									e\EventState = 0.0
									e\EventState3 = 0.0
									e\EventState2 = PD_StartRoom
								EndIf
								Exit
							EndIf
						Next
						If (Not RoomExist)
							Random = 19
							Teleport = True
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case PD_FakeTunnelRoom
				;[Block]
				ShouldPlay = 1
				
				GiveAchievement("pocketdimension")
				
				LightRenderDistance = 64.0
				UpdateDoors()
				
				ScaleEntity(e\room\OBJ, RoomScale, RoomScale, RoomScale)
				If e\EventState3 = 0.0 And (e\room\RoomDoors[0]\OpenState > 150.0 Lor e\room\RoomDoors[1]\OpenState > 150.0)
					PlaySound_Strict(snd_I\HorrorSFX[13])
					me\BlurTimer = 800.0
					e\EventState3 = 1.0
				EndIf
				If EntityY(me\Collider) < 5.0
					PositionEntity(me\Collider, EntityX(me\Collider, True), EntityY(me\Collider, True) - 500.0 * RoomScale, EntityZ(me\Collider, True))
					ResetEntity(me\Collider)
					
					For i = 0 To 1
						e\room\RoomDoors[i]\Open = False
						e\room\RoomDoors[i]\OpenState = 0.0
					Next
					
					S2IMapErase(UnlockedAchievements, "pocketdimension")
					
					LoadEventSound(e, "SFX\Room\PocketDimension\Rumble.ogg")
					LoadEventSound(e, "SFX\Room\PocketDimension\PrisonVoices.ogg", 1)
					
					e\EventState = 0.0
					e\EventState3 = 0.0
					e\EventState2 = PD_FourWayRoom
				EndIf
				;[End Block]
			Case PD_TowerRoom
				;[Block]
				If opt\ParticleAmount > 0
					If Rand(800) = 1
						Angle = EntityYaw(Camera, True) + Rnd(150.0, 210.0)
						p.Particles = CreateParticle(PARTICLE_SHADOW, EntityX(me\Collider) + Cos(Angle) * 7.5, 0.0, EntityZ(me\Collider) + Sin(Angle) * 7.5, 4.0, 0.0, 2500.0)
						p\Speed = 0.01 : p\SizeChange = 0.0
						EntityBlend(p\OBJ, 2)
						PointEntity(p\Pvt, Camera)
						TurnEntity(p\Pvt, 0.0, 145.0, 0.0, True)
						TurnEntity(p\Pvt, Rnd(10.0 , 20.0), 0.0, 0.0, True)
					EndIf
				EndIf
				
				If e\EventState3 > 12.0
					n_I\Curr106\Idle = 1
					PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[e\EventState3], True), e\room\y + 0.27, EntityZ(e\room\Objects[e\EventState3], True))
					
					PointEntity(n_I\Curr106\Collider, Camera)
					TurnEntity(n_I\Curr106\Collider, 0.0, Sin(MilliSec / 20.0) * 6.0, 0.0, True)
					MoveEntity(n_I\Curr106\Collider, 0.0, 0.0, Sin(MilliSec / 15.0) * 0.06)
					
					n_I\Curr106\GravityMult = 0.0 : n_I\Curr106\DropSpeed = 0.0
					PositionEntity(n_I\Curr106\OBJ, EntityX(n_I\Curr106\Collider), EntityY(n_I\Curr106\Collider), EntityZ(n_I\Curr106\Collider))
					ResetEntity(n_I\Curr106\Collider)
					RotateEntity(n_I\Curr106\OBJ, 0.0, EntityYaw(n_I\Curr106\Collider), 0.0)
					
					If Rand(750) = 1 And e\EventState3 > 12.0
						me\BlinkTimer = -10.0
						PlaySound_Strict(snd_I\HorrorSFX[8])
						e\EventState3 = e\EventState3 - 1.0
					EndIf
					
					If e\EventState3 = 12.0
						me\CameraShake = 1.0
						n_I\Curr106\Idle = 0
						n_I\Curr106\State = 3.0
						n_I\Curr106\State2 = 100000.0
						PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[e\EventState3], True), e\room\y - 1.0, EntityZ(e\room\Objects[e\EventState3], True))
						ResetEntity(n_I\Curr106\Collider)
					EndIf
				Else
					n_I\Curr106\Idle = 0
					n_I\Curr106\State = 3.0
					n_I\Curr106\State2 = 100000.0
				EndIf
				
				If EntityY(me\Collider) < (-1600.0) * RoomScale
					; ~ Player is at the exit
					If DistanceSquared(EntityX(e\room\Objects[16], True), EntityX(me\Collider), EntityZ(e\room\Objects[16], True), EntityZ(me\Collider)) < PowTwo(144.0 * RoomScale)
						n_I\Curr106\State = 0.0
						n_I\Curr106\State2 = Rnd(22000.0, 27000.0)
						Random = Rand(12, 28)
						Teleport = True
					Else ; ~ Somewhere else, must've fallen down
						If (Not chs\GodMode) And (Not me\Terminated)
							PlaySound_Strict(snd_I\HorrorSFX[8])
							msg\DeathMsg = GetLocalString("death", "106_2")
							me\BlurTimer = 3000.0
							Kill(False, False)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case PD_Labyrinth
				;[Block]
				fog\FarDist = 3.5
				
				UpdateDoors()
				
				me\Injuries = me\Injuries + (fps\Factor[0] * 0.0002 - (0.0001 * (wi\HazmatSuit > 0)))
				
				If DistanceSquared(EntityX(me\Collider), EntityX(e\room\Objects[8], True) + 7202.0 * RoomScale, EntityZ(me\Collider), EntityZ(e\room\Objects[8], True) + 1502.0 * RoomScale) > PowTwo(3678.0 * RoomScale)
					n_I\Curr106\Speed = n_I\Curr106\Speed * 2.8
					For d.Doors = Each Doors
						If d\room = e\room
							d\Open = False
							d\OpenState = 0.0
						EndIf
					Next
					Random = 41
					Teleport = True
				EndIf
				;[End Block]
		End Select
		
		If Teleport
			me\BlinkTimer = -10.0 : me\BlurTimer = 1150.0
			n_I\Curr106\Idle = 1
			
			Select Random
				Case 1, 2, 3, 4, 5, 31, 32 ; ~ Rotate the player and close by the wall
					;[Block]
					PlaySound_Strict(snd_I\SCP106SFX[3], True)
					
					Pvt = CreatePivot()
					PositionEntity(Pvt, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
					PointEntity(Pvt, e\room\OBJ)
					MoveEntity(Pvt, 0.0, 0.0, EntityDistance(me\Collider, e\room\OBJ) * 1.9)
					PositionEntity(me\Collider, EntityX(Pvt), EntityY(me\Collider) + 0.1, EntityZ(Pvt))
					ResetEntity(me\Collider)
					
					MoveEntity(Pvt, 0.0, 0.0, 0.8)
					PositionEntity(e\room\Objects[10], EntityX(Pvt), 0.1, EntityZ(Pvt))
					RotateEntity(e\room\Objects[10], 0.0, EntityYaw(Pvt), 0.0, True)
					
					FreeEntity(Pvt) : Pvt = 0
					e\EventState2 = PD_StartRoom
					;[End Block]
				Case 6, 7, 8, 9, 10, 11, 12, 13, 33, 34, 35, 36 ; ~ The 4-way room
					;[Block]
					PlaySound_Strict(snd_I\SCP106SFX[3], True)
					
					PositionEntity(me\Collider, EntityX(e\room\Objects[8], True), 0.6, EntityZ(e\room\Objects[8], True))
					ResetEntity(me\Collider)
					
					LoadEventSound(e, "SFX\Room\PocketDimension\Rumble.ogg")
					LoadEventSound(e, "SFX\Room\PocketDimension\PrisonVoices.ogg", 1)
					
					e\EventState3 = 0.0
					e\EventState2 = PD_FourWayRoom
					;[End Block]
				Case 14, 15, 16, 17, 37, 38 ; ~ Middle of the large starting room
					;[Block]
					PositionEntity(me\Collider, EntityX(e\room\OBJ), 0.6, EntityZ(e\room\OBJ))
					ResetEntity(me\Collider)
					
					e\EventState3 = 0.0
					e\EventState2 = PD_StartRoom
					;[End Block]
				Case 18 ; ~ The exit room
					;[Block]
					PositionEntity(me\Collider, EntityX(e\room\Objects[8], True) - 400.0 * RoomScale, e\room\y + 16.0 - 304.0 * RoomScale, EntityZ(e\room\Objects[8], True))
					ResetEntity(me\Collider)
					
					me\LightBlink = 5.0
					
					StopChannel(e\SoundCHN) : e\SoundCHN = 0
					StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
					
					e\EventState3 = 0.0
					e\EventState2 = PD_ExitRoom
					;[End Block]
				Case 19, 20, 21, 22, 23 ; ~ Random exiting
					;[Block]
					Local RoomID% = -1
					Local LCZ% = False
					
					Select Rand(7)
						Case 1
							;[Block]
							RoomID = r_room4_ic
							LCZ = True
							;[End Block]
						Case 2
							;[Block]
							RoomID = r_room2_2_lcz
							LCZ = True
							;[End Block]
						Case 3
							;[Block]
							RoomID = r_room2_5_lcz
							LCZ = True
							;[End Block]
						Case 4
							;[Block]
							RoomID = r_room2_3_hcz
							;[End Block]
						Case 5
							;[Block]
							RoomID = r_room2_5_hcz
							;[End Block]
						Case 6
							;[Block]
							RoomID = r_room3_2_hcz
							;[End Block]
						Case 7
							;[Block]
							RoomID = r_room4_hcz
							;[End Block]
					End Select
					
					RoomExist = False
					For r.Rooms = Each Rooms
						If r\RoomTemplate\RoomID = RoomID
							RoomExist = True
							
							GiveAchievement("pocketdimension")
							
							IsBlackOut = PrevIsBlackOut : PrevIsBlackOut = True
							
							me\BlinkTimer = -10.0
							
							If r\RoomCenter <> 0
								TeleportEntity(me\Collider, EntityX(r\RoomCenter, True), EntityY(r\OBJ) + 0.4, EntityZ(r\RoomCenter, True), 0.3, True)
							Else
								TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.4, EntityZ(r\OBJ), 0.3, True)
							EndIf
							TeleportToRoom(r)
							
							n_I\Curr106\Idle = 0
							n_I\Curr106\State = 0.0
							n_I\Curr106\State2 = Rnd(10000.0, 12000.0)
							
							If (Not LCZ)
								For e2.Events = Each Events
									If e2\EventID = e_room2_sl
										e2\EventState2 = 7.0
										e2\EventState3 = 0.0
										UpdateLever(e2\room\RoomLevers[0]\OBJ)
										RotateEntity(e2\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e2\room\RoomLevers[0]\OBJ), 0.0)
										Exit
									EndIf
								Next
							EndIf
							
							fog\FarDist = 6.0
							
							If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
							If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
							
							e\EventState = 0.0
							e\EventState3 = 0.0
							Exit
						EndIf
					Next
					If (Not RoomExist)
						PositionEntity(me\Collider, EntityX(e\room\OBJ), 0.6, EntityZ(e\room\OBJ))
						ResetEntity(me\Collider)
					EndIf
					e\EventState2 = PD_StartRoom
					;[End Block]
				Case 24, 25, 26 ; ~ The fake HCZ tunnel
					;[Block]
					PlaySound_Strict(snd_I\SCP106SFX[3], True)
					
					PositionEntity(me\Collider, EntityX(e\room\Objects[8], True), e\room\y + 2288.0 * RoomScale, EntityZ(e\room\Objects[8], True))
					ResetEntity(me\Collider)
					
					e\EventState3 = 0.0
					e\EventState2 = PD_FakeTunnelRoom
					;[End Block]
				Case 27, 28, 29, 30, 39 ; ~ The tower room
					;[Block]
					PositionEntity(me\Collider, EntityX(e\room\Objects[12], True), 0.6, EntityZ(e\room\Objects[12], True))
					ResetEntity(me\Collider)
					
					e\EventState3 = 15.0
					e\EventState2 = PD_TowerRoom
					;[End Block]
				Case 40 ; ~ Labyrinth
					;[Block]
					PlaySound_Strict(snd_I\SCP106SFX[3], True)
					
					Local Temp% = Rand(21, 24)
					
					PositionEntity(me\Collider, EntityX(e\room\Objects[Temp], True), EntityY(e\room\Objects[Temp], True), EntityZ(e\room\Objects[Temp], True))
					ResetEntity(me\Collider)
					
					n_I\Curr106\Speed = n_I\Curr106\Speed / 2.8
					n_I\Curr106\Idle = 0
					n_I\Curr106\State = 3.0
					n_I\Curr106\State2 = 100000.0
					PositionEntity(n_I\Curr106\Collider, EntityX(e\room\Objects[25], True), EntityY(e\room\Objects[25], True), EntityZ(e\room\Objects[25], True))
					ResetEntity(n_I\Curr106\Collider)
					
					e\EventState3 = 0.0
					e\EventState2 = PD_Labyrinth
					;[End Block]
				Case 41 ; ~ Exit back to SCP-005
					;[Block]
					RoomExist = False
					For r.Rooms = Each Rooms
						If r\RoomTemplate\RoomID = r_cont1_005
							RoomExist = True
							
							GiveAchievement("pocketdimension")
							
							IsBlackOut = PrevIsBlackOut : PrevIsBlackOut = True
							
							me\BlinkTimer = -10.0 : me\BlurTimer = 1500.0
							
							If me\Playable = 2
								e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Exit.ogg"))
								MakeMeUnplayable()
							EndIf
							If (Not ChannelPlaying(e\SoundCHN))
								TeleportEntity(me\Collider, EntityX(r\RoomCenter, True), 0.5, EntityZ(r\RoomCenter, True), 0.3, True)
								TeleportToRoom(r)
								
								n_I\Curr106\Idle = 0
								n_I\Curr106\State = 0.0
								n_I\Curr106\State2 = 15000.0
								
								For e2.Events = Each Events
									If e2\EventID = e_cont1_005
										RemoveEvent(e2)
										Exit
									EndIf
								Next
								
								fog\FarDist = 6.0
								me\Playable = 2
								
								If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
								If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
								
								e\EventState = 0.0
								e\EventState3 = 0.0
								e\EventState2 = PD_StartRoom
							EndIf
							Exit
						EndIf
					Next
					;[End Block]
			End Select
			ResetRender()
		EndIf
	Else
		HideRoomsNoColl(e\room)
		e\EventState = 0.0
		e\EventState3 = 0.0
		If Sky106 <> 0 Then FreeEntity(Sky106) : Sky106 = 0
		e\EventState2 = PD_StartRoom
	EndIf
End Function

Function UpdateEvent2_Dimension_1499%(e.Events)
	If PlayerRoom <> e\room
		Local i%
		
		For i = 1 To 15
			If e\room\Objects[i] <> 0
				If (Not EntityHidden(e\room\Objects[i])) Then HideEntity(e\room\Objects[i])
			EndIf
		Next
		If EntityY(me\Collider) > EntityY(e\room\OBJ) - 0.5 Then PlayerRoom = e\room
	EndIf
	If e\EventState = 2.0
		If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
		StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
		HideEntity(I_1499\Sky)
		HideChunks()
		HideRoomsNoColl(e\room)
		
		Local n.NPCs, du.Dummy1499_1
		
		For n.NPCs = Each NPCs
			If n\NPCType = NPCType1499_1 Then RemoveNPC(n)
		Next
		For du.Dummy1499_1 = Each Dummy1499_1
			RemoveDummy1499_1(du)
		Next
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		If e\EventState3 < 70.0 * 30.0 Then e\EventState3 = 0.0
		e\EventState = 1.0
	EndIf
End Function

Type Dummy1499_1
	Field Anim%
	Field OBJ%
End Type

Function RemoveDummy1499_1%(du.Dummy1499_1)
	If du\OBJ <> 0 Then FreeEntity(du\OBJ) : du\OBJ = 0
	Delete(du)
End Function

Function UpdateEvent_Dimension_1499%(e.Events)
	; ~ e\EventState: If player entered dimension (will be resetted after the player leaves it)
	; ~ 0: The player never entered SCP-1499's dimension
	; ~ 1: The player had already entered the dimension at least once
	; ~ 2: The player is in dimension
	
	; ~ e\EventState2: Used to count the amount of times the player has entered the SCP-1499's dimension (for a little spawning event)
	
	; ~ e\EventState3: Variable used for the SCP-1499's church event
	
	If PlayerRoom = e\room
		Local n.NPCs, du.Dummy1499_1, r.Rooms, it.Items
		Local i%, j%, Scale#
		
		PrevIsBlackOut = IsBlackOut : IsBlackOut = False
		
		If e\EventState < 2.0
			; ~ SCP-1499's random generator
			If e\EventState = 0.0
				If e\EventStr = "" And QuickLoadPercent = -1
					QuickLoadPercent = 0
					QuickLoad_CurrEvent = e
					e\EventStr = "Load0"
				EndIf
			Else
				e\EventState = 2.0
			EndIf
			
			Local Value% = Rand(2, 3)
			
			If e\EventState2 = Value Lor e\EventState2 = 4.0
				For i = -1 To 1
					For j = -1 To 1
						If i <> 0 And j <> 0
							n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + (0.75 * i), EntityY(me\Collider) + 0.05, EntityZ(me\Collider) + (0.75 * j))
							n\State = 2.0
							PointEntity(n\Collider, me\Collider)
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
						ElseIf i <> 0 Lor j <> 0
							n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + i, EntityY(me\Collider) + 0.05, EntityZ(me\Collider) + j)
							n\State = 2.0
							PointEntity(n\Collider, me\Collider)
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
						EndIf
					Next
				Next
				e\EventState2 = 5.0
			EndIf
			If e\EventState3 < 70.0 * 30.0
				; ~ The King
				e\room\NPC.NPCs[0] = CreateNPC(NPCType1499_1, e\room\x - 1917.0 * RoomScale, e\room\y + 1904.0 * RoomScale, e\room\z + 2308.0 * RoomScale)
				e\room\NPC[0]\PrevState = 2 : e\room\NPC[0]\Angle = 270.0
				e\room\NPC[0]\LastSeen = 1.0
				EntityColor(e\room\NPC[0]\OBJ, 255.0, 204.0, 140.0)
				RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\NPC[0]\Angle, 0.0)
				; ~ Guard next to king
				e\room\NPC.NPCs[1] = CreateNPC(NPCType1499_1, e\room\x - 1917.0 * RoomScale, e\room\y + 1904.0 * RoomScale, e\room\z + 2052.0 * RoomScale)
				e\room\NPC[1]\PrevState = 1 : e\room\NPC[1]\Angle = 270.0
				RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\NPC[1]\Angle, 0.0)
				; ~ Guards at the entrance to church
				e\room\NPC.NPCs[2] = CreateNPC(NPCType1499_1, e\room\x + 4055.0 * RoomScale, e\room\y + 128.0 * RoomScale, e\room\z + 1884.0 * RoomScale)
				e\room\NPC[2]\PrevState = 3 : e\room\NPC[2]\Angle = 270.0
				RotateEntity(e\room\NPC[2]\Collider, 0.0, e\room\NPC[2]\Angle, 0.0)
				e\room\NPC.NPCs[3] = CreateNPC(NPCType1499_1, e\room\x + 4055.0 * RoomScale, e\room\y + 128.0 * RoomScale, e\room\z + 2876.0 * RoomScale)
				e\room\NPC[3]\PrevState = 3 : e\room\NPC[3]\Angle = 270.0
				RotateEntity(e\room\NPC[3]\Collider, 0.0, e\room\NPC[3]\Angle, 0.0)
				e\room\NPC[2]\Target = e\room\NPC[3]
				e\room\NPC[3]\Target = e\room\NPC[2]
				; ~ More guards
				e\room\NPC.NPCs[4] = CreateNPC(NPCType1499_1, e\room\x - 1877.0 * RoomScale, e\room\y + 192.0 * RoomScale, e\room\z + 1071.0 * RoomScale)
				e\room\NPC[4]\PrevState = 3 : e\room\NPC[4]\Angle = 270.0
				RotateEntity(e\room\NPC[4]\Collider, 0.0, e\room\NPC[4]\Angle, 0.0)
				e\room\NPC.NPCs[5] = CreateNPC(NPCType1499_1, e\room\x - 1877.0 * RoomScale, e\room\y + 192.0 * RoomScale, e\room\z + 3503.0 * RoomScale)
				e\room\NPC[5]\PrevState = 3 : e\room\NPC[5]\Angle = 270.0
				RotateEntity(e\room\NPC[5]\Collider, 0.0, e\room\NPC[5]\Angle, 0.0)
				e\room\NPC[4]\Target = e\room\NPC[5]
				e\room\NPC[5]\Target = e\room\NPC[4]
				; ~ Guard at stairs
				n.NPCs = CreateNPC(NPCType1499_1, e\room\x - 2761.0 * RoomScale, e\room\y + 128.0 * RoomScale, e\room\z + 3204.0 * RoomScale)
				n\PrevState = 1 : n\Angle = 180.0 : n\Speed = 0.0
				RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
				; ~ SCP-1499-1 instances praying in church
				; ~ Zone 1
				For i = 0 To 4
					For j = 0 To 2
						du.Dummy1499_1 = New Dummy1499_1
						For n.NPCs = Each NPCs
							If n\NPCType = NPCType1499_1 And n\PrevState <> 2
								du\OBJ = CopyEntity(n\OBJ)
								Exit
							EndIf
						Next
						Scale = 0.02 * Rnd(0.8, 1.0)
						ScaleEntity(du\OBJ, Scale, Scale, Scale)
						EntityFX(du\OBJ, 1)
						du\Anim = Rand(False, True)
						PositionEntity(du\OBJ, Clamp((e\room\x + (1887.0 - ((2560.0 / 7.0) * i)) * RoomScale) + Rnd(-0.5, 0.5), e\room\x - 873.0 * RoomScale, e\room\x + 1887.0 * RoomScale), e\room\y, Clamp((e\room\z + (1796.0 - (384.0 * j)) * RoomScale) + Rnd(-0.5, 0.5), e\room\z + 1028.0 * RoomScale, e\room\z + 1796.0 * RoomScale))
						RotateEntity(du\OBJ, 0.0, 270.0, 0.0)
						EntityAutoFade(du\OBJ, 25.0, 39.0)
					Next
				Next
				; ~ Zone 2
				For i = 0 To 3
					For j = 0 To 2
						du.Dummy1499_1 = New Dummy1499_1
						For n.NPCs = Each NPCs
							If n\NPCType = NPCType1499_1 And n\PrevState <> 2
								du\OBJ = CopyEntity(n\OBJ)
								Exit
							EndIf
						Next
						Scale = 0.02 * Rnd(0.8, 1.0)
						ScaleEntity(du\OBJ, Scale, Scale, Scale)
						EntityFX(du\OBJ, 1)
						du\Anim = Rand(False, True)
						PositionEntity(du\OBJ, Clamp((e\room\x + (1375.0 - ((2048.0 / 6.0) * i)) * RoomScale) + Rnd(-0.5, 0.5), e\room\x - 873.0 * RoomScale, e\room\x + 1375.0 * RoomScale), e\room\y, Clamp((e\room\z + (3588.0 - (384.0 * j)) * RoomScale) + Rnd(-0.5, 0.5), e\room\z + 2820.0 * RoomScale, e\room\z + 3588.0 * RoomScale))
						RotateEntity(du\OBJ, 0.0, 270.0, 0.0)
						EntityAutoFade(du\OBJ, 25.0, 39.0)
					Next
				Next
			Else
				If (Not EntityHidden(e\room\Objects[17])) Then HideEntity(e\room\Objects[17])
			EndIf
			
			For i = 0 To 14
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-20.0, 20.0), EntityY(me\Collider) + 0.1, EntityZ(me\Collider) + Rnd(-20.0, 20.0))
				n\Angle = Rnd(360.0) : n\State2 = 0.0
				If EntityDistanceSquared(n\Collider, me\Collider) < 100.0 Then n\State = 2.0
			Next
		EndIf
		
		For r.Rooms = Each Rooms
			HideRoomsNoColl(r)
		Next
		ShowRoomsNoColl(e\room)
		ShowRoomsColl(e\room)
		
		If QuickLoadPercent = 100 Lor QuickLoadPercent = -1
			UpdateChunks(15)
			If EntityHidden(I_1499\Sky) Then ShowEntity(I_1499\Sky)
			UpdateSky(I_1499\Sky)
			ShouldPlay = 17
			If EntityY(me\Collider) < 800.0
				PositionEntity(me\Collider, EntityX(me\Collider), 800.5, EntityZ(me\Collider), True)
				ResetEntity(me\Collider)
			EndIf
			; ~ A hacky fix to make items not fall that are in SCP-1499's dimension
			For it.Items = Each Items
				If EntityY(it\Collider) > 750.0
					If EntityY(it\Collider) < 800.0
						PositionEntity(it\Collider, EntityX(it\Collider), 800.5, EntityZ(it\Collider))
						ResetEntity(it\Collider)
					EndIf
				EndIf
			Next
			
			Local AnimShift#
			
			For du.Dummy1499_1 = Each Dummy1499_1
				If e\EventState3 < 70.0 * 30.0
					AnimShift = ((Not du\Anim) * 92.0)
					If AnimTime(du\OBJ) <= 360.5 + AnimShift
						AnimateEx(du\OBJ, AnimTime(du\OBJ), 321.0 + AnimShift, 361.0 + AnimShift, 0.2, False)
					ElseIf AnimTime(du\OBJ) > 361.5 + AnimShift And AnimTime(du\OBJ) <= 401.5 + AnimShift
						AnimateEx(du\OBJ, AnimTime(du\OBJ), 362.0 + AnimShift, 402.0 + AnimShift, 0.2, False)
					Else
						i = Rand(False, True)
						SetAnimTime(du\OBJ, 321.0 + (41.0 * i) + AnimShift)
					EndIf
				Else
					AnimShift = ((Not du\Anim) * 96.0)
					If AnimTime(du\OBJ) <= 411.5 + AnimShift And AnimTime(du\OBJ) > 320.5
						AnimateEx(du\OBJ, AnimTime(du\OBJ), 403.0 + AnimShift, 412.0 + AnimShift, 0.2, False)
					Else
						AnimateEx(du\OBJ, AnimTime(du\OBJ), 296.0, 320.0, 0.2)
					EndIf
					
					Local Pvt% = CreatePivot()
					
					PositionEntity(Pvt, EntityX(du\OBJ), EntityY(du\OBJ), EntityZ(du\OBJ), True)
					PointEntity(Pvt, me\Collider)
					RotateEntity(du\OBJ, 0.0, CurveAngle(EntityYaw(Pvt), EntityYaw(du\OBJ) - 180.0, 10.0) + 180.0, 0.0)
					FreeEntity(Pvt) : Pvt = 0
				EndIf
			Next
			; ~ Player is inside the church
			If e\EventState3 < 70.0 * 10.0
				If IsEqual(EntityX(me\Collider, True), e\room\x - 56.0 * RoomScale, 2160.0 * RoomScale) And IsEqual(EntityZ(me\Collider, True), e\room\z + 2287.0 * RoomScale, 1408.0 * RoomScale) Then e\EventState3 = e\EventState3 + fps\Factor[0]
			ElseIf e\EventState3 >= 70.0 * 10.0 And e\EventState3 < 70.0 * 20.0
				For i = 0 To 1
					e\room\NPC[i]\Reload = 1.0
				Next
				e\EventState3 = 70.0 * 20.0
			ElseIf e\EventState3 = 70.0 * 20.0
				If e\room\NPC[0]\Frame > 854.5
					For i = 2 To 5
						If i = 2
							LoadNPCSound(e\room\NPC[i], "SFX\SCP\1499\Triggered.ogg")
							e\room\NPC[i]\SoundCHN = PlaySoundEx(e\room\NPC[i]\Sound, Camera, e\room\NPC[i]\Collider, 50.0)
						EndIf
						e\room\NPC[i]\State = 1.0
						SetNPCFrame(e\room\NPC[i], 203.0)
					Next
					e\EventState3 = 70.0 * 30.0
				EndIf
			EndIf
			
			If e\room\NPC[0] <> Null
				If EntityHidden(e\room\Objects[17]) Then ShowEntity(e\room\Objects[17])
				If e\EventState3 < 70.0 * 30.0
					ShouldPlay = 66
					If NowPlaying = 66
						If e\SoundCHN = 0
							e\Sound2 = LoadSound_Strict("SFX\Music\HaveMercyOnMe(Choir).ogg")
							e\SoundCHN = StreamSound_Strict("SFX\Music\HaveMercyOnMe(NoChoir).ogg", opt\MusicVolume * opt\MasterVolume, ModeLoop)
							e\SoundCHN_IsStream = True
						EndIf
					EndIf
					If e\Sound2 <> 0 Then e\SoundCHN2 = LoopSoundEx(e\Sound2, e\SoundCHN2, Camera, e\room\Objects[16], 10.0, opt\MusicVolume * opt\MasterVolume)
				Else
					ShouldPlay = 18
					If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
					If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
					If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
				EndIf
			EndIf
			
			If EntityDistanceSquared(me\Collider, e\room\OBJ) > 1600.0
				For du.Dummy1499_1 = Each Dummy1499_1
					If (Not EntityHidden(du\OBJ)) Then HideEntity(du\OBJ)
				Next
			Else
				For du.Dummy1499_1 = Each Dummy1499_1
					If EntityHidden(du\OBJ) Then ShowEntity(du\OBJ)
				Next
			EndIf
		Else
			me\DropSpeed = 0.0
		EndIf
		PlayerFallingPickDistance = 0.0
	Else
		If e\EventState = 2.0
			If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN = 0 : e\SoundCHN_IsStream = False
			StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
			HideEntity(I_1499\Sky)
			HideChunks()
			HideRoomsNoColl(e\room)
			For n.NPCs = Each NPCs
				If n\NPCType = NPCType1499_1 Then RemoveNPC(n)
			Next
			For du.Dummy1499_1 = Each Dummy1499_1
				RemoveDummy1499_1(du)
			Next
			If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
			If e\EventState3 < 70.0 * 30.0 Then e\EventState3 = 0.0
			e\EventState = 1.0
		EndIf
	EndIf
End Function

Function UpdateEvent_096_Spawn%(e.Events)
	Local Place%
	
	If e\room\Dist < 17.0
		; ~ Checking some statements in order to determine if SCP-096 can spawn in this room
		If e\EventState <> 2.0
			Local e2.Events, r.Rooms
			
			If PlayerRoom = e\room Then e\EventState = 2.0
			
			If n_I\Curr096 <> Null
				If n_I\Curr096\State <> 1.0 Lor EntityDistanceSquared(n_I\Curr096\Collider, me\Collider) < 1600.0 Lor EntityDistanceSquared(n_I\Curr096\Collider, e\room\OBJ) > EntityDistanceSquared(n_I\Curr096\Collider, me\Collider) Then e\EventState = 2.0
				
				For e2.Events = Each Events
					If e2\EventID = e_room2_servers_hcz
						If e2\EventState > 0.0 And e2\room\NPC[0] <> Null
							e\EventState = 2.0
							Exit
						EndIf
					EndIf
				Next
				
				For r.Rooms = Each Rooms
					If r\RoomTemplate\RoomID = r_room2_checkpoint_lcz_hcz
						If r\Dist < 10.0
							e\EventState = 2.0
							Exit
						EndIf
					EndIf
				Next
			EndIf
			
			For e2.Events = Each Events
				If e2\EventID = e_room2_servers_hcz
					If e2\EventState = 0.0
						e\EventState = 2.0
						Exit
					EndIf
				EndIf
			Next
		EndIf
		
		If e\EventState = 0.0
			Local RID% = e\room\RoomTemplate\RoomID
			Local x#, z#
			
			Select RID
				Case r_room3_hcz, r_room3_2_hcz, r_room3_3_hcz, r_room4_hcz, r_room4_2_hcz
					;[Block]
					If RID = r_room4_hcz Lor RID = r_room4_2_hcz
						Place = Rand(0, 3)
					Else
						Place = Rand(0, 2)
					EndIf
					
					Select Place
						Case 0
							;[Block]
							x = -108.0
							z = 0.0
							;[End Block]
						Case 1
							;[Block]
							x = 0.0
							z = -108.0
							;[End Block]
						Case 2
							;[Block]
							x = 108.0
							z = 0.0
							;[End Block]
						Case 3
							;[Block]
							x = 0.0
							z = 108.0
							;[End Block]
					End Select
					;[End Block]
				Default
					;[Block]
					x = 0.0
					z = 0.0
					;[End Block]
			End Select
			
			TFormPoint(x, 200.0, z, e\room\OBJ, 0)
			If n_I\Curr096 <> Null
				TeleportEntity(n_I\Curr096\Collider, TFormedX(), TFormedY(), TFormedZ(), n_I\Curr096\CollRadius, True)
				n_I\Curr096\CurrentRoom = e\room
			Else
				n_I\Curr096 = CreateNPC(NPCType096, TFormedX(), TFormedY(), TFormedZ())
			EndIf
			n_I\Curr096\State = 1.0
			PointEntity(n_I\Curr096\Collider, me\Collider)
			RotateEntity(n_I\Curr096\Collider, 0.0, EntityYaw(n_I\Curr096\Collider) + 180.0, 0.0)
			
			e\EventState = 1.0
		ElseIf e\EventState = 1.0
			PointEntity(n_I\Curr096\Collider, me\Collider)
			RotateEntity(n_I\Curr096\Collider, 0.0, EntityYaw(n_I\Curr096\Collider) + 180.0, 0.0)
			
			If EntityDistanceSquared(n_I\Curr096\Collider, me\Collider) < 56.25
				If EntityVisible(n_I\Curr096\Collider, Camera)
					PointEntity(n_I\Curr096\Collider, me\Collider)
					RotateEntity(n_I\Curr096\Collider, 0.0, EntityYaw(n_I\Curr096\Collider) + Rnd(170.0, 190.0), 0.0)
					e\EventState = 2.0
				EndIf
			EndIf
		ElseIf e\EventState = 3.0
			e\EventState = 2.0
		EndIf
	Else
		If e\EventState = 2.0 Then e\EventState = 3.0 - (3.0 * (Rand(-1, 1 + (2 * SelectedDifficulty\AggressiveNPCs)) > 0))
	EndIf
End Function

Function UpdateEvent_106_Sinkhole%(e.Events)
	If e\EventState = 0.0
		CreateDecal(DECAL_CORROSIVE_1, e\room\x + Rnd(-0.5, 0.5), e\room\y + 0.005, e\room\z + Rnd(-0.5, 0.5), 90.0, Rnd(360.0), 0.0, 2.5)
		
		e\EventState = 1.0
	ElseIf PlayerRoom = e\room
		If snd_I\SinkHoleSFX = 0 Then snd_I\SinkHoleSFX = LoadSound_Strict("SFX\Room\Sinkhole.ogg")
		e\SoundCHN = LoopSoundEx(snd_I\SinkHoleSFX, e\SoundCHN, Camera, e\room\OBJ, 4.5, 1.5)
		
		If e\room\Dist < 0.8 And (Not chs\NoTarget)
			If e\EventState2 = 0.0 Then PlaySound_Strict(LoadTempSound("SFX\Room\SinkholeFall.ogg"))
			
			MakeMeUnplayable()
			PositionEntity(me\Collider, CurveValue(EntityX(e\room\OBJ), EntityX(me\Collider), 30.0), CurveValue(EntityY(e\room\OBJ) - e\EventState2, EntityY(me\Collider), 25.0), CurveValue(EntityZ(e\room\OBJ), EntityZ(me\Collider), 30.0), True)
			
			me\DropSpeed = 0.0
			
			ResetEntity(me\Collider)
			
			e\EventState2 = Min(e\EventState2 + fps\Factor[0] / 200.0, 2.0)
			
			me\LightBlink = Min(e\EventState2 * 5.0, 10.0)
			me\BlurTimer = e\EventState2 * 500.0
			
			If e\EventState2 >= 0.2
				me\BlinkTimer = -10.0
				If n_I\Curr106\State = 3.0 Then n_I\Curr106\CurrSpeed = 0.0
			EndIf
			
			If e\EventState2 = 2.0 Then MoveToPocketDimension()
		Else
			If chs\NoClip Then me\Playable = 2
		EndIf
	Else
		e\EventState2 = 0.0
	EndIf
End Function

Function UpdateEvent_106_Victim%(e.Events)
	If (Not n_I\Curr106\Contained)
		Local de.Decals
		
		If PlayerRoom = e\room
			If e\EventState = 0.0
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, e\room\x, e\room\y + 798.0 * RoomScale, e\room\z, -90.0, Rnd(360.0), 0.0, 0.5, 0.8)
				de\SizeChange = 0.0015
				
				PlaySound_Strict(snd_I\DecaySFX[3])
				e\EventState = 1.0
			EndIf
		EndIf
		
		If e\EventState > 0.0
			e\EventState = e\EventState + fps\Factor[0]
			If e\EventState > 200.0
				If e\room\NPC[0] = Null
					e\room\NPC[0] = CreateNPC(NPCTypeD, e\room\x, e\room\y + 900.0 * RoomScale, e\room\z)
					e\room\NPC[0]\State = -1.0 : e\room\NPC[0]\State3 = -1.0
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_MAYNARD_TEXTURE)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, Rnd(360.0), 0.0, True)
					SetNPCFrame(e\room\NPC[0], 1.0)
					
					PlaySound_Strict(snd_I\HorrorSFX[0])
					PlaySound_Strict(snd_I\DecaySFX[2])
				EndIf
				
				UpdateSoundOrigin(e\SoundCHN, Camera, e\room\OBJ)
				
				e\room\NPC[0]\FallingPickDistance = 0.0
				If EntityY(e\room\NPC[0]\Collider) > 0.35
					EntityType(e\room\NPC[0]\Collider, HIT_PLAYER)
					AnimateNPC(e\room\NPC[0], 1.0, 10.0, 0.12, False)
					
					Local Dist# = EntityDistanceSquared(me\Collider, e\room\NPC[0]\Collider)
					
					If Dist < 0.64 ; ~ Get the player out of the way
						Local SqrValue# = PowTwo(Sqr(Dist) - 0.8)
						Local fDir# = PointDirection(EntityX(me\Collider, True), EntityZ(me\Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(e\room\NPC[0]\Collider, True))
						
						TranslateEntity(me\Collider, Cos(-fDir + 90.0) * SqrValue, 0.0, Sin(-fDir + 90.0) * SqrValue)
					EndIf
					If EntityY(e\room\NPC[0]\Collider) > 0.6 Then EntityType(e\room\NPC[0]\Collider, 0)
				Else
					AnimateNPC(e\room\NPC[0], 11.0, 19.0, 0.25, False)
					If e\Sound = 0
						LoadEventSound(e, "SFX\Character\BodyFall.ogg")
						PlaySoundEx(e\Sound, Camera, e\room\NPC[0]\Collider)
						
						CreateDecal(DECAL_CORROSIVE_1, e\room\x, e\room\y + 0.005, e\room\z, 90.0, Rnd(360.0), 0.0, 0.4, 0.8)
					EndIf
					If e\room\NPC[0]\Frame >= 18.9
						e\room\NPC[0]\IsDead = True
						RemoveEvent(e)
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_106_Victim_Wall%(e.Events)
	Local de.Decals
	
	If PlayerRoom = e\room
		If e\EventState = 0.0
			If Rand(200) = 1 Then e\EventState = 1.0
		EndIf
	EndIf
	If e\EventState > 0.0
		While e\EventState < 5.0
			If Rand(120) = 1
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, e\room\x + Rnd(-2.0, 2.0), e\room\y + 0.005, e\room\z + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0, 0.05, 0.8)
				de\SizeChange = 0.0015
				
				PlaySoundEx(snd_I\DecaySFX[0], Camera, de\OBJ, 2.0, 0.5)
				
				e\EventState = e\EventState + 1.0
			EndIf
		Wend
	EndIf
	If e\EventState = 5.0
		If e\room\NPC[0] = Null
			If Rand(200) = 1
				TFormPoint(-245.0, 56.0, 0.0, e\room\OBJ, 0)
				e\room\NPC[0] = CreateNPC(NPCTypeD, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[0]\State = -1.0 : e\room\NPC[0]\State3 = -1.0 : e\room\NPC[0]\IsDead = True
				RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 270.0, 0.0)
				SetNPCFrame(e\room\NPC[0], 41.0)
				If n_I\Curr106\Contained
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_106_FEMUR_BREAKER_TEXTURE)
				Else
					ChangeNPCTextureID(e\room\NPC[0], NPC_CLASS_D_VICTIM_106_TEXTURE)
				EndIf
				
				TFormPoint(-223.95, 135.0, 0.0, e\room\OBJ, 0)
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, TFormedX(), TFormedY(), TFormedZ(), 0.0, e\room\Angle + 90.0, 0.0, 0.5, 0.0)
				de\SizeChange = 0.003 : de\AlphaChange = 0.002 : de\Timer = 90000.0
				
				PlaySound_Strict(snd_I\HorrorSFX[13])
			EndIf
		EndIf
	EndIf
	If e\room\NPC[0] <> Null
		If PlayerRoom = e\room
			LightVolume = CurveValue(0.4, LightVolume, 10.0)
			me\CurrSpeed = Min(me\CurrSpeed - (me\CurrSpeed * (0.15 / EntityDistance(e\room\NPC[0]\Collider, me\Collider)) * fps\Factor[0]), me\CurrSpeed)
		EndIf
		
		Local PrevFrame# = e\room\NPC[0]\Frame
		
		AnimateNPC(e\room\NPC[0], 41.0, 60.0, 0.02 + (0.25 * (AnimTime(e\room\NPC[0]\OBJ) > 48.0)), False)
		If PrevFrame <= 52.0 And e\room\NPC[0]\Frame > 52.0 Then PlaySoundEx(snd_I\DamageSFX[0], Camera, e\room\NPC[0]\Collider, 4.0, 0.8)
		If e\room\NPC[0]\Frame > 59.9 Then RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_173_Spawn%(e.Events)
	If e\room\Dist < 6.0
		If n_I\Curr173\Idle > 1
			RemoveEvent(e)
		Else
			If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) > 16.0 And (Not PlayerSees173(n_I\Curr173)) And PlayerRoom <> e\room
				Local x# = 0.0, y# = 0.0, z# = 0.0
				Local Pvt%
				
				Select e\room\RoomTemplate\RoomID
					Case r_room2_4_lcz
						;[Block]
						TFormPoint(640.0, 120.0, -896.0, e\room\OBJ, 0)
						x = TFormedX() : y = TFormedY() : z = TFormedZ()
						;[End Block]
					Case r_room2_6_lcz
						;[Block]
						TFormPoint(-812.0, 120.0, 0.0, e\room\OBJ, 0)
						x = TFormedX() : y = TFormedY() : z = TFormedZ()
						
						Pvt = CreatePivot()
						PositionEntity(Pvt, x, y, z, True)
						PlaySoundEx(LoadTempSound("SFX\Room\Room2Nuke\Vent" + Rand(0, 2) + ".ogg"), Camera, Pvt, 12.0, 1.5)
						FreeEntity(Pvt) : Pvt = 0
						
						PositionEntity(e\room\Objects[0], x, e\room\y + 4.0 * RoomScale, z, True)
						RotateEntity(e\room\Objects[0], 0.0, Rnd(360.0), 0.0, True)
						EntityType(e\room\Objects[0], HIT_MAP)
						;[End Block]
					Case r_room3_2_ez, r_room3_3_ez
						;[Block]
						Select Rand(3)
							Case 1
								;[Block]
								TFormPoint(576.0, -512.0, 256.0, e\room\OBJ, 0)
								;[End Block]
							Case 2
								;[Block]
								TFormPoint(-512.0, -512.0, 256.0, e\room\OBJ, 0)
								;[End Block]
							Case 3
								;[Block]
								TFormPoint(-920.0, -512.0, 480.0, e\room\OBJ, 0)
								;[End Block]
						End Select
						x = TFormedX() : y = TFormedY() : z = TFormedZ()
						;[End Block]
					Case r_room2_7_ez
						;[Block]
						Select Rand(3)
							Case 1
								;[Block]
								TFormPoint(905.0, -512.0, 507.0, e\room\OBJ, 0)
								;[End Block]
							Case 2
								;[Block]
								TFormPoint(-34.0, -512.0, 594.0, e\room\OBJ, 0)
								;[End Block]
							Case 3
								;[Block]
								TFormPoint(256.0, -512.0, -911.0, e\room\OBJ, 0)
								;[End Block]
						End Select
						x = TFormedX() : y = TFormedY() : z = TFormedZ()
						;[End Block]
				End Select
				TeleportEntity(n_I\Curr173\Collider, x, y, z, n_I\Curr173\CollRadius + 0.12, True)
				n_I\Curr173\CurrentRoom = e\room
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_682_Roar%(e.Events)
	If e\EventState = 0.0
		If PlayerRoom = e\room Then e\EventState = 70.0 * Rnd(50.0, 100.0)
	Else
		e\EventState = e\EventState - fps\Factor[0]
		If PlayerInReachableRoom(True)
			If e\EventState < 70.0 * 17.0
				If e\EventState + fps\Factor[0] >= 70.0 * 17.0 Then e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\682\Roar.ogg"))
				If e\EventState > 70.0 * 14.0 Then me\BigCameraShake = 0.5
				If e\EventState > 70.0 * 6.0 And e\EventState < 70.0 * 9.5 Then me\BigCameraShake = 2.0
			EndIf
		EndIf
		If e\EventState < 70.0 Then RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_1048_A%(e.Events)
	If e\room\Dist < 8.0
		CreateNPC(NPCType1048_A, e\room\x, e\room\y + 0.3, e\room\z)
		RemoveEvent(e)
	EndIf
End Function

Function UpdateEvent_Brownout%(e.Events)
	Local Temp% = False
	
	If e\EventState3 = 0.0
		If Rand(5) <> 1
			e\EventState3 = 1.0
		Else
			Local p.Props
			
			For p.Props = Each Props
				If p\room = e\room
					If p\Name = "tank2.b3d"
						Local Tex% = LoadTexture_Strict("GFX\Map\Textures\tank2_damaged.png")
						
						EntityInstance(p\OBJ, 0)
						EntityTexture(p\OBJ, Tex)
						UpdateEntityMaterial(p\OBJ)
						DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
						Exit
					EndIf
				EndIf
			Next
			e\EventState3 = 2.0
		EndIf
	EndIf
	
	If Rand(70) = 1
		If (IsRoomAdjacent(PlayerRoom, e\room) Lor PlayerRoom = e\room) And InFacility = NullFloor And e\EventState3 = 2.0 And PlayerRoom\RoomTemplate\RoomID <> r_cont1_173
			If e\EventState < 0.5 Then me\LightBlink = Max(1.0, me\LightBlink)
			Temp = True
		EndIf
	EndIf
	If PlayerRoom = e\room
		If e\room\Objects[0] = 0
			If e\room\RoomTemplate\RoomID = r_room2_7_lcz
				TFormPoint(906.0, 106.0, 876.0, e\room\OBJ, 0)
			Else
				TFormPoint(-876.0, 106.0, 906.0, e\room\OBJ, 0)
			EndIf
			e\room\Objects[0] = CreatePivot()
			PositionEntity(e\room\Objects[0], TFormedX(), TFormedY(), TFormedZ(), True)
		Else
			If Temp
				Local i%
				
				If e\room\RoomEmitters[0] = Null Then e\room\RoomEmitters[0] = SetEmitter(e\room, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), 37)
				SetTemplateVelocity(ParticleEffect[19], -0.007, -0.008, -0.001, 0.0012, -0.007, 0.008)
				For i = 0 To 1
					SetEmitter(e\room, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), 19)
				Next
				PlaySoundEx(snd_I\SparkShortSFX, Camera, e\room\Objects[0], 8.0, 0.6)
			EndIf
			
			Local x% = UpdateLever(e\room\RoomLevers[1]\OBJ) ; ~ Fuel pump
			Local y% = UpdateLever(e\room\RoomLevers[0]\OBJ) ; ~ Generator
			
			; ~ Fuel pump on
			If x
				e\EventState2 = Min(1.0, e\EventState2 + fps\Factor[0] / 350.0)
				
				; ~ Generator on
				If y
					If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Room\GeneratorOn.ogg")
					e\EventState = Min(1.0, e\EventState + fps\Factor[0] / 450.0)
				Else
					e\EventState = Min(0.0, e\EventState - fps\Factor[0] / 450.0)
				EndIf
			Else
				e\EventState2 = Max(0.0, e\EventState2 - fps\Factor[0] / 350.0)
				e\EventState = Max(0.0, e\EventState - fps\Factor[0] / 450.0)
			EndIf
			
			If e\EventState > 0.0 Then e\SoundCHN = LoopSoundEx(e\Sound, e\SoundCHN, Camera, e\room\RoomLevers[0]\BaseOBJ, 3.0, e\EventState)
			If e\EventState2 > 0.0 Then e\SoundCHN2 = LoopSoundEx(snd_I\RoomAmbience[7], e\SoundCHN2, Camera, e\room\RoomLevers[1]\BaseOBJ, 2.0, e\EventState2 * 0.8)
		EndIf
	EndIf
End Function

Function UpdateEvent_Checkpoint%(e.Events)
	Local e2.Events
	Local i%
	
	If PlayerRoom = e\room
		; ~ Play a sound clip when the player passes through the gate
		If e\EventState2 = 0.0
			If EntityZ(me\Collider) < e\room\z
				PlaySound_Strict(LoadTempSound("SFX\Ambient\ToZone" + (3 - (me\Zone = 1)) + ".ogg"))
				e\EventState2 = 1.0
			EndIf
		EndIf
		
		If e\EventState3 = 0.0
			If Rand(2) = 1
				TFormPoint(877.0, 146.6, 333.0, e\room\OBJ, 0)
				e\room\NPC[0] = CreateNPC(NPCType1048, TFormedX(), TFormedY(), TFormedZ())
				e\room\NPC[0]\State = 2.0
			EndIf
			e\EventState3 = 1.0
		EndIf
	EndIf
	
	If e\room\Dist < 12.0
		If e\room\NPC[0] <> Null
			If e\room\NPC[0]\State2 = 3.0 Then RemoveNPC(e\room\NPC[0])
		EndIf
		
		For e2.Events = Each Events
			If e\room\RoomTemplate\RoomID = r_room2_checkpoint_hcz_ez
				If e2\EventID = e_cont2_008
					If e2\EventState = 2.0
						TurnCheckpointMonitorsOff(False)
						For i = 0 To 1
							e\room\RoomDoors[i]\Locked = 0
						Next
					Else
						UpdateCheckpointMonitors(False)
						For i = 0 To 1
							e\room\RoomDoors[i]\Locked = 1
						Next
					EndIf
					Exit
				EndIf
			Else
				If e2\EventID = e_room2_sl
					If e2\EventState3 = 0.0
						TurnCheckpointMonitorsOff()
						For i = 0 To 1
							e\room\RoomDoors[i]\Locked = 0
						Next
					Else
						UpdateCheckpointMonitors()
						For i = 0 To 1
							e\room\RoomDoors[i]\Locked = 1
						Next
					EndIf
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	If e\room\RoomDoors[0]\Open <> e\EventState
		Local Sound% = LoadTempSound("SFX\Door\DoorCheckpoint.ogg")
		
		e\SoundCHN = PlaySoundEx(Sound, Camera, e\room\RoomDoors[0]\OBJ)
		e\SoundCHN2 = PlaySoundEx(Sound, Camera, e\room\RoomDoors[1]\OBJ)
	EndIf
	e\EventState = e\room\RoomDoors[0]\Open
	
	UpdateSoundOrigin(e\SoundCHN, Camera, e\room\RoomDoors[0]\OBJ)
	UpdateSoundOrigin(e\SoundCHN2, Camera, e\room\RoomDoors[1]\OBJ)
End Function

Function UpdateEvent_Door_Closing%(e.Events)
	If PlayerRoom = e\room
		If EntityDistanceSquared(e\room\OBJ, me\Collider) < 6.25
			Local d.Doors
			
			For d.Doors = Each Doors
				If DistanceSquared(EntityX(d\FrameOBJ, True), EntityX(me\Collider), EntityZ(d\FrameOBJ, True), EntityZ(me\Collider)) < 4.0
					If (Not EntityInView(d\FrameOBJ, Camera))
						If d\Open
							d\Open = False
							d\OpenState = 0.0
							me\BlurTimer = 100.0
							me\BigCameraShake = 3.0
						EndIf
					EndIf
					Exit
				EndIf
			Next
			RemoveEvent(e)
		EndIf
	EndIf
End Function

Function UpdateEvent_Gateway%(e.Events)
	; ~ e\EventState: Determines if the airlock is in operation or not
	
	; ~ e\EventState2: The timer for the airlocks
	
	; ~ e\EventState3: Checks if the player had left the airlock or not
	
	; ~ e\EventState4: Checks if airlock is turned on
	
	Local i%
	Local DistMult# = (2.3 * (e\room\RoomTemplate\RoomID = r_room4_gw))
	
	If PlayerRoom = e\room
		If snd_I\AirlockSFX = 0 Then snd_I\AirlockSFX = LoadSound_Strict("SFX\Room\Airlock.ogg")
		
		e\EventState3 = UpdateLever(e\room\RoomLevers[0]\OBJ)
		If e\EventState = 0.0
			If EntityDistanceSquared(e\room\Objects[0], me\Collider) < (0.64 + DistMult) And e\EventState2 = 0.0 And e\EventState3 = 1.0 And (Not chs\NoTarget)
				StopChannel(e\SoundCHN) : e\SoundCHN = 0
				For i = 0 To 1 + (2 * (e\room\RoomTemplate\RoomID = r_room4_gw))
					e\room\RoomDoors[i]\FastOpen = True
					OpenCloseDoor(e\room\RoomDoors[i])
				Next
				PlaySound_Strict(snd_I\AlarmSFX[2])
				PlaySound_Strict(LoadTempSound("SFX\Alarm\DeconInProgress.ogg"))
				e\EventState = 0.01
			ElseIf EntityDistanceSquared(e\room\Objects[0], me\Collider) > (5.29 + DistMult)
				e\EventState2 = 0.0
			EndIf
		Else
			If e\EventState < 70.0 * 9.0
				e\EventState = e\EventState + fps\Factor[0]
				For i = 0 To 1 + (2 * (e\room\RoomTemplate\RoomID = r_room4_gw))
					If e\room\RoomDoors[i]\Locked = 0 Then e\room\RoomDoors[i]\Open = False
				Next
				If e\EventState > 70.0 * 3.0 And e\EventState < 70.0 * 7.0
					If EntityDistanceSquared(e\room\Objects[0], me\Collider) < (4.0 + DistMult)
						If wi\GasMask = 0 And wi\HazmatSuit = 0 Then me\EyeIrritation = Max(70.0, me\EyeIrritation)
					EndIf
					For i = 0 To 1 + (4 * (e\room\RoomTemplate\RoomID = r_room4_gw))
						If e\room\RoomEmitters[i] = Null
							Select e\room\RoomTemplate\RoomID
								Case r_room2_gw
									;[Block]
									TFormPoint(320.0, 360.0, 220.0 * i - 110.0, e\room\OBJ, 0)
									;[End Block]
								Case r_room3_gw
									;[Block]
									TFormPoint(220.0 * i - 110.0, 360.0, 320.0, e\room\OBJ, 0)
									;[End Block]
								Case r_room4_gw
									;[Block]
									TFormPoint(-266.0 + ((i - (3 * (i > 2))) * 266.0), 476.0, 116.0 - ((i > 2) * 232.0), e\room\OBJ, 0)
									;[End Block]
							End Select
							e\room\RoomEmitters[i] = SetEmitter(e\room, TFormedX(), TFormedY(), TFormedZ(), 2)
						EndIf
					Next
					If (Not ChannelPlaying(e\SoundCHN)) Then e\SoundCHN = PlaySoundEx(snd_I\AirlockSFX, Camera, e\room\Objects[0], 5.0)
				EndIf
				If e\EventState > 70.0 * 8.9 And e\EventState - fps\Factor[0] =< 70.0 * 8.9 Then e\SoundCHN2 = PlaySoundEx(LoadTempSound("SFX\Alarm\DeconCompleted.ogg"), Camera, e\room\Objects[0], 5.0, 1.0, True)
			Else
				If (Not ChannelPlaying(e\SoundCHN2))
					For i = 0 To 1 + (4 * (e\room\RoomTemplate\RoomID = r_room4_gw))
						If e\room\RoomEmitters[i] <> Null Then FreeEmitter(e\room\RoomEmitters[i])
						If i < 4
							If (Not e\room\RoomDoors[i]\Open) Then OpenCloseDoor(e\room\RoomDoors[i])
							e\room\RoomDoors[i]\FastOpen = False
						EndIf
					Next
					e\EventState = 0.0
					e\EventState2 = 1.0
				EndIf
			EndIf
		EndIf
		
		UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 5.0)
	Else
		e\EventState2 = 0.0
	EndIf
End Function

Function UpdateEvent_Tesla%(e.Events)
	If e\EventState3 = 0.0
		If Rand(8) = 1
			RotateEntity(e\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e\room\RoomLevers[0]\OBJ), 0.0)
			RotateEntity(e\room\RoomLevers[1]\OBJ, 80.0, EntityYaw(e\room\RoomLevers[1]\OBJ), 0.0)
			e\EventState3 = 1.0
			e\EventState4 = -196.0
		Else
			e\EventState3 = 2.0
		EndIf
	EndIf
	
	If (Not EntityHidden(e\room\Objects[4])) Then HideEntity(e\room\Objects[4])
	If e\room\Dist < 16.0
		Local n.NPCs, e2.Events
		Local PrevLever% = (EntityPitch(e\room\RoomLevers[0]\OBJ, True) < 0.0)
		Local Temp%, i%, x1#, y1#, z1#, x2#, y2#, z2#
		
		Temp = UpdateLever(e\room\RoomLevers[0]\OBJ)
		If Temp
			e\room\RoomTemplate\DisableDecals = 1
			If PrevLever <> Temp
				If Temp
					PlaySound_Strict(snd_I\TeslaPowerUpSFX)
					e\EventState = 0.0
				EndIf
			EndIf
		Else
			e\room\RoomTemplate\DisableDecals = 0
			StopChannel(e\SoundCHN) : e\SoundCHN = 0
			e\EventState = 3.0
			e\EventState2 = (-70.0) * 90.0
		EndIf
		
		If PlayerRoom = e\room
			UpdateLever(e\room\RoomLevers[1]\OBJ)
			
			Temp = True
			For e2.Events = Each Events
				If e2\EventID = e\EventID And e2 <> e
					If e2\room\NPC[0] <> Null Lor e\EventState3 = 1.0
						Temp = False
						Exit
					EndIf
				EndIf
			Next
			
			If e\room\NPC[0] = Null And Temp
				e\room\RoomDoors[0]\Locked = 1
				If (e\room\Angle Mod 180) = 90.0
					i = (Abs(EntityX(me\Collider, True) < EntityX(e\room\OBJ, True)))
					x1 = i * 800.0 + (Not i) * (-800.0)
					z1 = 0.0
				Else
					i = (Abs(EntityZ(me\Collider, True)) < EntityZ(e\room\OBJ, True))
					x1 = 0.0
					z1 = i * 800.0 + (Not i) * (-800.0)
				EndIf
				e\room\NPC[0] = CreateNPC(NPCTypeClerk, EntityX(e\room\OBJ, True) + x1 * RoomScale, e\room\y + 0.3, EntityZ(e\room\OBJ, True) + z1 * RoomScale)
				e\room\NPC[0]\State = 2.0 : e\room\NPC[0]\Speed = 0.026
				PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
				e\EventState = 0.0
			EndIf
		EndIf
		
		Select e\EventState
			Case 0.0 ; ~ Idle state
				;[Block]
				If (MilliSec Mod 1500) < 800
					If EntityHidden(e\room\Objects[3]) Then ShowEntity(e\room\Objects[3])
				ElseIf (Not EntityHidden(e\room\Objects[3]))
					HideEntity(e\room\Objects[3])
				EndIf
				HideEntity(e\room\Objects[0])
				e\SoundCHN = LoopSoundEx(snd_I\TeslaIdleSFX, e\SoundCHN, Camera, e\room\Objects[0], 4.0, 0.5)
				e\EventState2 = 0.0
				x2 = EntityX(e\room\OBJ, True) : z2 = EntityZ(e\room\OBJ, True) : y2 = EntityY(e\room\OBJ, True)
				If IsEqual(EntityX(me\Collider, True), x2, 1.0) And IsEqual(EntityZ(me\Collider, True), z2, 1.0) And IsEqual(EntityY(me\Collider, True), y2, 1.3)
					If (Not me\Terminated) And (Not chs\NoTarget)
						If e\room\NPC[0] = Null Lor e\room\NPC[0]\IsDead
							If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
							e\SoundCHN = PlaySoundEx(snd_I\TeslaActivateSFX, Camera, e\room\Objects[0], 4.0, 0.5)
							e\EventState = 1.0
						EndIf
					EndIf
				EndIf
				For n.NPCs = Each NPCs
					If n\NPCType <> NPCType513_1 And (Not n\IsDead)
						x1 = EntityX(n\Collider, True) : z1 = EntityZ(n\Collider, True) : y1 = EntityY(n\Collider, True)
						If n\NPCType = NPCTypeMTF And e\room\NPC[1] = Null
							If IsEqual(x1, x2, 2.0) And IsEqual(z1, z2, 2.0) And IsEqual(y1, y2, 1.3)
								n\PrevState = 1
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\State3 = 70.0 * 10.0
								n\State = MTF_DISABLING_TESLA
								e\room\NPC[1] = n
							EndIf
						Else
							If IsEqual(x1, x2, 0.81) And IsEqual(z1, z2, 0.81) And IsEqual(y1, y2, 1.3)
								If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
								e\SoundCHN = PlaySoundEx(snd_I\TeslaActivateSFX, Camera, e\room\Objects[0], 4.0, 0.5)
								e\EventState = 1.0
							EndIf
						EndIf
					EndIf
				Next
				If e\room\NPC[1] <> Null
					If e\room\NPC[1]\State3 <= 0.0
						StopChannel(e\SoundCHN) : e\SoundCHN = 0
						PlayAnnouncement("SFX\Character\MTF\AnnouncTeslaDisabled" + Rand(0, 2) + ".ogg")
						RotateEntity(e\room\RoomLevers[0]\OBJ, 80.0, EntityYaw(e\room\RoomLevers[0]\OBJ), 0.0)
						e\room\NPC[1] = Null
					EndIf
				EndIf
				;[End Block]
			Case 1.0 ; ~ Charge state
				;[Block]
				If (MilliSec Mod 100) < 50
					If EntityHidden(e\room\Objects[3]) Then ShowEntity(e\room\Objects[3])
				ElseIf (Not EntityHidden(e\room\Objects[3]))
					HideEntity(e\room\Objects[3])
				EndIf
				e\EventState2 = e\EventState2 + fps\Factor[0]
				If e\EventState2 >= 35.0
					StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
					e\SoundCHN2 = PlaySoundEx(snd_I\TeslaShockSFX, Camera, e\room\Objects[0])
					e\EventState = 2.0
				EndIf
				;[End Block]
			Case 2.0 ; ~ Zap state
				;[Block]
				UpdateTeslaGate(e)
				;[End Block]
			Case 3.0 ; ~ Recharge state
				;[Block]
				e\EventState2 = e\EventState2 + fps\Factor[0]
				If (Not EntityHidden(e\room\Objects[0])) Then HideEntity(e\room\Objects[0])
				If (Not EntityHidden(e\room\Objects[3])) Then HideEntity(e\room\Objects[3])
				If e\EventState2 >= 0.0 Then e\EventState = 0.0
				;[End Block]
		End Select
		If EntityPitch(e\room\RoomLevers[1]\OBJ) < 0.0
			e\EventState4 = Min(e\EventState4 + fps\Factor[0], 0.0)
		Else
			e\EventState4 = Max(e\EventState4 - fps\Factor[0], -196.0)
		EndIf
		If e\EventState4 < 0.0 And e\EventState4 > -196.0
			e\BlindsCHN = LoopSoundEx(snd_I\BlindsSFX, e\BlindsCHN, Camera, e\room\Objects[2], 2.0)
		Else
			StopChannel(e\BlindsCHN) : e\BlindsCHN = 0
		EndIf
		
		PositionEntity(e\room\Objects[1], 0.0, e\EventState4, 0.0)
		UpdateSoundOrigin(e\BlindsCHN, Camera, e\room\Objects[2])
	EndIf
End Function

Function UpdateEvent_Broken_Tesla%(e.Events)
	If (Not EntityHidden(e\room\Objects[4])) Then HideEntity(e\room\Objects[4])
	If e\room\Dist < 16.0
		Local n.NPCs, e2.Events
		Local i%, x1#, y1#, z1#, x2#, y2#, z2#
		
		If PlayerRoom = e\room
			UpdateLever(e\room\RoomLevers[1]\OBJ)
			
			e\SoundCHN = LoopSoundEx(snd_I\AlarmSFX[1], e\SoundCHN, Camera, e\room\Objects[3], 3.0)
			
			If EntityDistanceSquared(me\Collider, e\room\Objects[3]) < 9.0
				If Rand(50) = 1
					SetTemplateVelocity(ParticleEffect[19], -0.007, -0.008, -0.001, 0.0012, -0.007, 0.008)
					SetEmitter(e\room, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), 19)
					PlaySoundEx(snd_I\SparkShortSFX, Camera, e\room\Objects[3], 3.0, 0.4)
				EndIf
			EndIf
		EndIf
		
		Select e\EventState
			Case 0.0 ; ~ Idle state
				;[Block]
				HideEntity(e\room\Objects[0])
				e\EventState2 = 0.0
				x2 = EntityX(e\room\OBJ, True) : z2 = EntityZ(e\room\OBJ, True) : y2 = EntityY(e\room\OBJ, True)
				For n.NPCs = Each NPCs
					If (Not n\IsDead)
						x1 = EntityX(n\Collider, True) : z1 = EntityZ(n\Collider, True) : y1 = EntityY(n\Collider, True)
						If n\NPCType = NPCTypeMTF And e\room\NPC[1] = Null
							If IsEqual(x1, x2, 2.0) And IsEqual(z1, z2, 2.0) And IsEqual(y1, y2, 1.3)
								n\PrevState = 1
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\State3 = 70.0 * 10.0
								
								n\State = MTF_DISABLING_TESLA
								e\room\NPC[1] = n
								Exit
							EndIf
						EndIf
					EndIf
				Next
				If e\room\NPC[1] <> Null
					If e\room\NPC[1]\State3 <= 0.0
						StopChannel(e\SoundCHN) : e\SoundCHN = 0
						PlayAnnouncement("SFX\Character\MTF\AnnouncTeslaDisabled" + Rand(0, 2) + ".ogg")
						e\room\NPC[1] = Null
						e\EventState2 = -70.0 * 90.0
						e\EventState = 3.0
						Return
					EndIf
				EndIf
				If ChannelPlaying(e\SoundCHN) Then StopChannel(e\SoundCHN) : e\SoundCHN = 0
				e\SoundCHN = PlaySoundEx(snd_I\TeslaActivateSFX, Camera, e\room\Objects[0], 4.0, 0.5)
				If Rand(4) = 1
					e\EventState2 = -70.0 * 5.0
					e\EventState = 3.0
				Else
					e\EventState = 1.0
				EndIf
				;[End Block]
			Case 1.0 ; ~ Charge state
				;[Block]
				If (MilliSec Mod 100) < 50
					If EntityHidden(e\room\Objects[3]) Then ShowEntity(e\room\Objects[3])
				ElseIf (Not EntityHidden(e\room\Objects[3]))
					HideEntity(e\room\Objects[3])
				EndIf
				e\EventState2 = e\EventState2 + fps\Factor[0]
				If e\EventState2 >= 35.0
					StopChannel(e\SoundCHN2) : e\SoundCHN2 = 0
					e\SoundCHN2 = PlaySoundEx(snd_I\TeslaShockSFX, Camera, e\room\Objects[0])
					e\EventState = 2.0
				EndIf
				;[End Block]
			Case 2.0 ; ~ Zap state
				;[Block]
				UpdateTeslaGate(e)
				;[End Block]
			Case 3.0 ; ~ Recharge state
				;[Block]
				e\EventState2 = e\EventState2 + fps\Factor[0]
				If (Not EntityHidden(e\room\Objects[0])) Then HideEntity(e\room\Objects[0])
				If (Not EntityHidden(e\room\Objects[3])) Then HideEntity(e\room\Objects[3])
				If e\EventState2 >= 0.0 Then e\EventState = 0.0
				;[End Block]
		End Select
		If EntityPitch(e\room\RoomLevers[1]\OBJ) < 0.0
			e\EventState4 = Min(e\EventState4 + fps\Factor[0], 0.0)
		Else
			e\EventState4 = Max(e\EventState4 - fps\Factor[0], -196.0)
		EndIf
		If e\EventState4 < 0.0 And e\EventState4 > -196.0
			e\BlindsCHN = LoopSoundEx(snd_I\BlindsSFX, e\BlindsCHN, Camera, e\room\Objects[2], 2.0)
		Else
			StopChannel(e\BlindsCHN) : e\BlindsCHN = 0
		EndIf
		
		PositionEntity(e\room\Objects[1], 0.0, e\EventState4, 0.0)
		UpdateSoundOrigin(e\BlindsCHN, Camera, e\room\Objects[2])
	EndIf
End Function

Function UpdateEvent_Trick%(e.Events)
	If PlayerRoom = e\room
		If e\room\Dist < 2.0
			If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) < 36.0 Lor EntityDistanceSquared(me\Collider, n_I\Curr106\Collider) < 36.0
				RemoveEvent(e)
			Else
				Local Pvt% = CreatePivot()
				
				PositionEntity(Pvt, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
				PointEntity(Pvt, e\room\OBJ)
				RotateEntity(Pvt, 0.0, EntityYaw(Pvt), 0.0, True)
				MoveEntity(Pvt, 0.0, 0.0, EntityDistance(Pvt, e\room\OBJ) * 2.0)
				
				me\BlinkTimer = -10.0
				
				PlaySound_Strict(snd_I\HorrorSFX[11])
				
				UpdateWorld()
				
				PositionEntity(me\Collider, EntityX(Pvt), EntityY(Pvt) + 0.05, EntityZ(Pvt))
				TurnEntity(me\Collider, 0.0, 180.0, 0.0)
				
				ResetRender()
				
				FreeEntity(Pvt) : Pvt = 0
				RemoveEvent(e)
			EndIf
		EndIf
	EndIf
End Function

Function UpdateEvent_Trick_Item%(e.Events)
	If e\room\Dist < 8.0
		If EntityDistanceSquared(me\Collider, n_I\Curr173\Collider) < 16.0
			If e\room\Objects[MaxRoomObjects - 1] <> 0
				CreateDecal(DECAL_CORROSIVE_2, EntityX(e\room\Objects[MaxRoomObjects - 1], True), e\room\y + 0.005, EntityZ(e\room\Objects[MaxRoomObjects - 1], True), 90.0, Rnd(360.0), 0.0, 0.2, Rnd(0.7, 0.8))
				
				FreeEntity(e\room\Objects[MaxRoomObjects - 1]) : e\room\Objects[MaxRoomObjects - 1] = 0
			EndIf
			RemoveEvent(e)
		Else
			If e\room\Objects[MaxRoomObjects - 1] = 0 ; ~ Use the latest object ID to prevent overlapping
				Local itt.ItemTemplates
				Local Temp%
				
				Select Rand(3)
					Case 1
						;[Block]
						Temp = it_scp500pill
						;[End Block]
					Case 2
						;[Block]
						Temp = it_key4
						;[End Block]
					Case 3
						;[Block]
						Temp = it_veryfinenvg
						;[End Block]
				End Select
				
				For itt.ItemTemplates = Each ItemTemplates
					If itt\ID = Temp
						e\room\Objects[MaxRoomObjects - 1] = CopyInstanced(itt\OBJ)
						If e\room\RoomCenter <> 0
							PositionEntity(e\room\Objects[MaxRoomObjects - 1], EntityX(e\room\RoomCenter), e\room\y + 0.1, EntityZ(e\room\RoomCenter))
						Else
							PositionEntity(e\room\Objects[MaxRoomObjects - 1], e\room\x, e\room\y, e\room\z)
						EndIf
						RotateEntity(e\room\Objects[MaxRoomObjects - 1], 0.0, Rnd(360.0), 0.0)
						EntityParent(e\room\Objects[MaxRoomObjects - 1], e\room\OBJ)
						Exit
					EndIf
				Next
			Else
				If EntityDistanceSquared(me\Collider, e\room\Objects[MaxRoomObjects - 1]) < 4.0
					me\BlurTimer = 500.0
					me\BlinkTimer = -10.0
					
					PlaySound_Strict(snd_I\HorrorSFX[11])
					
					CreateDecal(DECAL_CORROSIVE_2, EntityX(e\room\Objects[MaxRoomObjects - 1], True), e\room\y + 0.005, EntityZ(e\room\Objects[MaxRoomObjects - 1], True), 90.0, Rnd(360.0), 0.0, 0.2, Rnd(0.7, 0.8))
					
					FreeEntity(e\room\Objects[MaxRoomObjects - 1]) : e\room\Objects[MaxRoomObjects - 1] = 0
					RemoveEvent(e)
				EndIf
			EndIf
		EndIf
	EndIf
End Function


;~C#Blitz3D TSS

;~IDEal Editor Parameters:
;~C#Blitz3D TSS