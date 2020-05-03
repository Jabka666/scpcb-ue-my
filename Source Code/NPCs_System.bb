Global Curr173.NPCs, Curr106.NPCs, Curr096.NPCs, Curr5131.NPCs
Global Contained106% = False

Type NPCs
	Field OBJ%, OBJ2%, OBJ3%, OBJ4%, Collider%
	Field NPCtype%, ID%
	Field DropSpeed#, Gravity%
	Field State#, State2#, State3#, PrevState%
	Field MakingNoise%
	Field Frame#
	Field Angle#
	Field Sound%, SoundCHN%, SoundTimer#
	Field Sound2%, SoundCHN2%
	Field Speed#, CurrSpeed#
	Field Texture$
	Field Idle#
	Field Reload#
	Field LastSeen%, LastDist#
	Field PrevX#, PrevY#, PrevZ#
	Field Target.NPCs, TargetID%
	Field EnemyX#, EnemyY#, EnemyZ#
	Field Path.WayPoints[20], PathStatus%, PathTimer#, PathLocation%
	Field NVX#, NVY#, NVZ#, NVName$
	Field GravityMult# = 1.0
	Field MaxGravity# = 0.2
	Field MTFVariant%
	Field MTFLeader.NPCs
	Field IsDead%
	Field BlinkTimer# = 1.0
	Field IgnorePlayer%
	Field ManipulateBone%
	Field ManipulationType%
	Field BoneToManipulate$
	Field BonePitch#
	Field BoneYaw#
	Field BoneRoll#
	Field NPCNameInSection$
	Field InFacility% = True
	Field CanUseElevator% = False
	Field HP%
	Field PathX#, PathZ#
	Field Model$
	Field ModelScaleX#, ModelScaleY#, ModelScaleZ#
	Field HideFromNVG
	Field TextureID% = -1
	Field CollRadius#
	Field IdleTimer#
	Field SoundCHN_IsStream%, SoundCHN2_IsStream%
	Field FallingPickDistance#
	Field UseHeadphones% = False
End Type

Function CreateNPC.NPCs(NPCtype%, x#, y#, z#)
	Local n.NPCs = New NPCs, n2.NPCs
	Local Temp#, i%, Tex%
	Local SF%, b%, t1%
	Local o.Objects = First Objects
	
	n\NPCtype = NPCtype
	n\GravityMult = 1.0
	n\MaxGravity = 0.2
	n\CollRadius = 0.2
	n\FallingPickDistance = 10
	Select NPCtype
		Case NPCtype173
			;[Block]
			n\NVName = "SCP-173"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.23, 0.32)
			EntityType(n\Collider, HIT_PLAYER)
			n\Gravity = True
			
			n\OBJ = CopyEntity(o\NPCModelID[0])
			
			; ~ On Halloween set Jack-o'-lantern texture.
			If (Left(CurrentDate(), 7) = "31 Oct ") Then
				HalloweenTex = True
				
				Local TexFestive% = LoadTexture_Strict("GFX\npcs\scp_173_h.pt", 1)
				
				EntityTexture(n\OBJ, TexFestive, 0, 0)
				FreeTexture(TexFestive)
			EndIf
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-173", "scale") / MeshDepth(n\OBJ)			
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-173", "speed") / 100.0
			
			n\OBJ2 = CopyEntity(o\NPCModelID[29])
			ScaleEntity(n\OBJ2, RoomScale, 1.025 * RoomScale, RoomScale)
			HideEntity(n\OBJ2)
			
			n\CollRadius = 0.32
			;[End Block]
		Case NPCtype106
			;[Block]
			n\NVName = "SCP-106"
			n\Collider = CreatePivot()
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[1])
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-106", "scale") / 2.2
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			Local OldManEyes% = LoadTexture_Strict("GFX\npcs\scp_106_eyes.png")
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-106", "speed") / 100.0
			
			n\OBJ2 = CreateSprite()
			ScaleSprite(n\OBJ2, 0.03, 0.03)
			EntityTexture(n\OBJ2, OldManEyes)
			EntityBlend(n\OBJ2, 3)
			EntityFX(n\OBJ2, 1 + 8)
			SpriteViewMode(n\OBJ2, 2)
			
			FreeTexture(OldManEyes)
			;[End Block]
		Case NPCtypeGuard
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[2])
			
			Temp = GetINIFloat("Data\NPCs.ini", "Guard", "scale") / 2.5
			
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "Guard", "speed") / 100.0
			;[End Block]
		Case NPCtypeMTF
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[7])
			
			Temp = GetINIFloat("Data\NPCs.ini", "MTF", "scale") / 2.5
			
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2) 
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "MTF", "speed") / 100.0
			
			If MTFSFX(0) = 0 Then
				MTFSFX(0) = LoadSound_Strict("SFX\Character\MTF\ClassD1.ogg")
				MTFSFX(1) = LoadSound_Strict("SFX\Character\MTF\ClassD2.ogg")
				MTFSFX(2) = LoadSound_Strict("SFX\Character\MTF\ClassD3.ogg")			
				MTFSFX(3) = LoadSound_Strict("SFX\Character\MTF\ClassD4.ogg")
				MTFSFX(5) = LoadSound_Strict("SFX\Character\MTF\Beep.ogg")
				MTFSFX(6) = LoadSound_Strict("SFX\Character\MTF\Breath.ogg")
			EndIf
			
			If MTFrooms[6] = Null Then 
				For r.Rooms = Each Rooms
					Select Lower(r\RoomTemplate\Name)
						Case "room106"
							;[Block]
							MTFrooms[0] = r
							;[End Block]
						Case "room372"
							;[Block]
							MTFrooms[1] = r
							;[End Block]
						Case "room079"
							;[Block]
							MTFrooms[2] = r
							;[End Block]
						Case "room2poffices"
							;[Block]
							MTFrooms[3] = r
							;[End Block]
						Case "room914"
							;[Block]
							MTFrooms[4] = r
							;[End Block]
						Case "room895"
							;[Block]
							MTFrooms[5] = r
							;[End Block]
						Case "start"
							;[Block]
							MTFrooms[6] = r
							;[End Block]
					End Select
				Next			
			EndIf
			;[End Block]
		Case NPCtypeD
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.32)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[3])
			
			Temp = 0.52 / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
			
			n\Speed = 2.0 / 100
			
			n\CollRadius = 0.32
			;[End Block]
		Case NPCtype372
			;[Block]
			n\NVName = "SCP-372"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			
			n\OBJ = CopyEntity(o\NPCModelID[4])
			
			Temp = 0.35 / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			;[End Block]
		Case NPCtype513_1
			;[Block]
			n\NVName = "SCP-513-1"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			n\OBJ = CopyEntity(o\NPCModelID[11])
			
			n\OBJ2 = CopyEntity(n\OBJ)
			EntityAlpha(n\OBJ2, 0.6)
			
			Temp = 1.8 / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp)
			;[End Block]
		Case NPCtype096
			;[Block]
			n\NVName = "SCP-096"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.26)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[8])
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-096", "speed") / 100.0
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-096", "scale") / 3.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)	
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ) * 2, -MeshHeight(n\OBJ) * 2, -MeshDepth(n\OBJ) * 2, MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 4, MeshDepth(n\OBJ) * 4)
			
			n\CollRadius = 0.26
			;[End Block]
		Case NPCtype049
			;[Block]
			n\NVName = "SCP-049"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[9])
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-049", "speed") / 100.0
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-049", "scale")
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\Sound = LoadSound_Strict("SFX\Horror\Horror12.ogg")
			
			If HorrorSFX(13) = 0 Then HorrorSFX(13) = LoadSound_Strict("SFX\Horror\Horror13.ogg")
			
			n\CanUseElevator = True
			;[End Block]
		Case NPCtype049_2
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[10])
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-049-2", "scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
			
			SetAnimTime(n\OBJ, 107)
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-049-2", "speed") / 100.0)
			
			n\Sound = LoadSound_Strict("SFX\SCP\049\0492Breath.ogg")
			
			n\HP = 100
			;[End Block]
		Case NPCtypeApache
			;[Block]
			n\NVName = "Human"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\Collider = CreatePivot()
			EntityRadius n\Collider, 0.2
			
			n\OBJ = CopyEntity(o\NPCModelID[5])
			
			Temp = 0.6
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\OBJ2 = CopyEntity(o\NPCModelID[6])
			EntityParent(n\OBJ2, n\OBJ)
			
			For i = -1 To 1 Step 2
				Local Rotor2% = CopyEntity(n\OBJ2, n\OBJ2)
				
				RotateEntity(Rotor2, 0.0, 4.0 * i, 0.0)
				EntityAlpha(Rotor2, 0.5)
			Next
			
			n\OBJ3 = CopyEntity(o\NPCModelID[21], n\OBJ)
			PositionEntity(n\OBJ3, 0.0, 2.15, -5.48)
			
			EntityType(n\Collider, HIT_APACHE)
			EntityRadius(n\Collider, 3.0)
			
			For i = -1 To 1 Step 2
				Local Light1% = CreateLight(2, n\OBJ)
				
				LightRange(Light1, 2.0)
				LightColor(Light1, 255, 255, 255)
				PositionEntity(Light1, 1.65 * i, 1.17, -0.25)
				
				Local LightSprite% = CreateSprite(n\OBJ)
				
				PositionEntity(LightSprite, 1.65 * i, 1.17, 0, -0.25)
				ScaleSprite(LightSprite, 0.13, 0.13)
				EntityTexture(LightSprite, LightSpriteTex(0))
				EntityBlend(LightSprite, 3)
				EntityFX(LightSprite, 1 + 8)				
			Next
			;[End Block]
		Case NPCtype035_Tentacle
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			
			n\OBJ = CopyEntity(o\NPCModelID[12])
			ScaleEntity(n\OBJ, 0.065, 0.065, 0.065)
			SetAnimTime(n\OBJ, 283)
			
			n\HP = 500
			;[End Block]
		Case NPCtype860
			;[Block]
			n\NVName = "Unidentified"
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.25)
			EntityType(n\Collider, HIT_PLAYER)
			n\OBJ = CopyEntity(o\NPCModelID[13])
			
			EntityFX(n\OBJ, 1)
			
			Tex = LoadTexture_Strict("GFX\npcs\scp_860_2_eyes.png", 1 + 2)
			
			n\OBJ2 = CreateSprite()
			ScaleSprite(n\OBJ2, 0.1, 0.1)
			EntityTexture(n\OBJ2, Tex)
			FreeTexture(Tex)
			
			EntityFX(n\OBJ2, 1 + 8)
			EntityBlend(n\OBJ2, BLEND_ADD)
			SpriteViewMode(n\OBJ2, 2)
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "forestmonster", "speed") / 100.0
			
			Temp = GetINIFloat("Data\NPCs.ini", "forestmonster", "scale") / 20.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)	
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ) * 2, -MeshHeight(n\OBJ) * 2, -MeshDepth(n\OBJ) * 2, MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 4, MeshDepth(n\OBJ) * 4)
			
			n\CollRadius = 0.25
			;[End Block]
		Case NPCtype939
			;[Block]
			Local Amount939% = 0
			
			For n2.NPCs = Each NPCs
				If n\NPCtype = n2\NPCtype And n <> n2
					Amount939% = Amount939 + 1
				EndIf
			Next
			
			If Amount939% = 0 Then i = 53
			If Amount939% = 1 Then i = 89
			If Amount939% = 2 Then i = 96
			n\NVName = "SCP-939-" + i
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.3)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[14])
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-939", "scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)	
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-939", "speed") / 100.0
			
			n\CollRadius = 0.3
			;[End Block]
		Case NPCtype066
			;[Block]
			n\NVName = "SCP-066"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[15])
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-066", "scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-066", "speed") / 100.0
			;[End Block]
		Case NPCtype966
			;[Block]
			i = 1
			For n2.NPCs = Each NPCs
				If n\NPCtype = n2\NPCtype And n <> n2 Then i = i + 1
			Next
			n\NVName = "SCP-966-" + i
			
			n\Collider = CreatePivot()
			EntityRadius n\Collider,0.2
			
			n\OBJ = CopyEntity(o\NPCModelID[16])
			EntityFX(n\OBJ, 1)
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-966", "scale") / 40.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)	
			
			SetAnimTime(n\OBJ, 15.0)
			
			EntityType(n\Collider, HIT_PLAYER)
			
			n\Speed = (GetINIFloat("Data\NPCs.ini", "SCP-966", "speed") / 100.0)
			;[End Block]
		Case NPCtype1499
			;[Block]
			n\NVName = "Unidentified"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[18])
			
			Temp = GetINIFloat("Data\NPCs.ini", "SCP-1499-1", "scale") / 4.0 * Rnd(0.8, 1.0)
			
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			EntityFX(n\OBJ, 1)
			
			EntityAutoFade(n\OBJ, HideDistance * 2.5, HideDistance * 2.95)
			
			n\Speed = GetINIFloat("Data\NPCs.ini", "SCP-1499-1", "speed") / 100.0 * Rnd(0.9, 1.1)
			;[End Block]
		Case NPCtype008_1
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.2)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[19])
			
			Temp = 0.5 / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
			
			SetNPCFrame(n, 11)
			
			n\Speed = 2.0 / 100
			
			n\Sound = LoadSound_Strict("SFX\SCP\008_1\Breath.ogg")
			
			n\HP = 120
			;[End Block]
		Case NPCtypeClerk
			;[Block]
			n\NVName = "Human"
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, 0.32)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(o\NPCModelID[20])
			
			Temp = 0.5 / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\Speed = 2.0 / 100
			
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
			
			n\CollRadius = 0.32
			;[End Block]
	End Select
	
	PositionEntity(n\Collider, x, y, z, True)
	PositionEntity(n\OBJ, x, y, z, True)
	
	ResetEntity(n\Collider)
	
	n\ID = 0
	n\ID = FindFreeNPCID()
	
	NPCSpeedChange(n)
	
	Return(n)
End Function

Function RemoveNPC(n.NPCs)
	
	If n = Null Then Return
	
	If n\OBJ2 <> 0 Then 
		FreeEntity(n\OBJ2) : n\OBJ2 = 0
	EndIf
	If n\OBJ3 <> 0 Then 
		FreeEntity(n\OBJ3) : n\OBJ3 = 0
	EndIf
	If n\OBJ4 <> 0 Then 
		FreeEntity(n\OBJ4) : n\OBJ4 = 0
	EndIf
	
	If (Not n\SoundCHN_IsStream)
		If n\SoundCHN <> 0 And ChannelPlaying(n\SoundCHN) Then
			StopChannel(n\SoundCHN)
		EndIf
	Else
		If (n\SoundCHN <> 0)
			StopStream_Strict(n\SoundCHN)
		EndIf
	EndIf
	
	If (Not n\SoundCHN2_IsStream)
		If (n\SoundCHN2 <> 0 And ChannelPlaying(n\SoundCHN2)) Then
			StopChannel(n\SoundCHN2)
		EndIf
	Else
		If (n\SoundCHN2 <> 0)
			StopStream_Strict(n\SoundCHN2)
		EndIf
	EndIf
	
	If n\Sound <> 0 Then FreeSound_Strict(n\Sound) : n\Sound = 0
	If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2) : n\Sound2 = 0
	
	FreeEntity(n\OBJ) : n\OBJ = 0
	FreeEntity(n\Collider) : n\Collider = 0	
	
	Delete(n)
End Function

Function UpdateNPCs()
	CatchErrors("Uncaught (UpdateNPCs)")
	Local n.NPCs, n2.NPCs, d.Doors, de.Decals, r.Rooms
	Local i%, Dist#, Dist2#, Angle#, x#, y#, z#, PrevFrame#, PlayerSeeAble%, RN$
	Local Target%, Pvt%, Pick%
	
	For n.NPCs = Each NPCs
		; ~ A variable to determine if the NPC is in the facility or not
		n\InFacility = CheckForNPCInFacility(n)
		
		Select n\NPCtype
			Case NPCtype173
				;[Block]
				If Curr173\Idle <> 3 Then
					Dist = EntityDistance(n\Collider, Collider)		
					
					n\State3 = 1.0
					
					If n\Idle < 2 Then
						If n\IdleTimer > 0.1
							n\Idle = 1
							n\IdleTimer = Max(n\IdleTimer - FPSfactor, 0.1)
						ElseIf n\IdleTimer = 0.1
							n\Idle = 0
							n\IdleTimer = 0.0
						EndIf
						
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						
						If n\Idle = False Then
							Local Temp% = False
							Local Move% = True
							
							If Dist < 15.0 Then
								If Dist < 10.0 Then 
									If EntityVisible(n\Collider, Collider) Then
										Temp = True
										n\EnemyX = EntityX(Collider, True)
										n\EnemyY = EntityY(Collider, True)
										n\EnemyZ = EntityZ(Collider, True)
									EndIf
								EndIf										
								
								Local SoundVol# = Max(Min((Distance(EntityX(n\Collider), EntityZ(n\Collider), n\PrevX, n\PrevZ) * 2.5), 1.0), 0.0)
								
								n\SoundCHN = LoopSound2(StoneDragSFX, n\SoundCHN, Camera, n\Collider, 10.0, n\State)
								
								n\PrevX = EntityX(n\Collider)
								n\PrevZ = EntityZ(n\Collider)				
								
								If (BlinkTimer < -16.0 Or BlinkTimer > -6.0) And (IsNVGBlinking = False) Then
									If EntityInView(n\OBJ, Camera) Then Move = False
								EndIf
							EndIf
							
							If chs\NoTarget Then Move = True
							
							; ~ Doesn't move
							If Move = False Then
								BlurVolume = Max(Max(Min((4.0 - Dist) / 6.0, 0.9), 0.1), BlurVolume)
								CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * 15.0 * Max((3.5 - Dist) / 3.5, 0.0))								
								
								If Dist < 3.5 And MilliSecs2() - n\LastSeen > 60000 And Temp Then
									PlaySound_Strict(HorrorSFX(Rand(3, 4)))
									
									n\LastSeen = MilliSecs2()
								EndIf
								
								If Dist < 1.5 And Rand(700) = 1 Then PlaySound2(Scp173SFX(Rand(0, 2)), Camera, n\OBJ)
								
								If Dist < 1.5 And n\LastDist > 2.0 And Temp Then
									CurrCameraZoom = 40.0
									HeartBeatRate = Max(HeartBeatRate, 140.0)
									HeartBeatVolume = 0.5
									
									Select Rand(5)
										Case 1
											;[Block]
											PlaySound_Strict(HorrorSFX(1))
											;[End Block]
										Case 2
											;[Block]
											PlaySound_Strict(HorrorSFX(2))
											;[End Block]
										Case 3
											;[Block]
											PlaySound_Strict(HorrorSFX(9))
											;[End Block]
										Case 4
											;[Block]
											PlaySound_Strict(HorrorSFX(10))
											;[End Block]
										Case 5
											;[Block]
											PlaySound_Strict(HorrorSFX(14))
											;[End Block]
									End Select
								EndIf									
								
								n\LastDist = Dist
								
								n\State = Max(0.0, n\State - FPSfactor / 20.0)
							Else 
								; ~ Teleport to a room closer to the player
								If Dist > 50.0 Then
									If Rand(70) = 1 Then
										If PlayerRoom\RoomTemplate\Name <> "exit1" And PlayerRoom\RoomTemplate\Name <> "gatea" And PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then
											For w.Waypoints = Each WayPoints
												If w\door = Null And Rand(5) = 1 Then
													x = Abs(EntityX(Collider) - EntityX(w\OBJ, True))
													If x < 25.0 And x > 15.0 Then
														z = Abs(EntityZ(Collider) - EntityZ(w\OBJ, True))
														If z < 25.0 And z > 15.0 Then
															PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 0.25, EntityZ(w\OBJ, True))
															ResetEntity(n\Collider)
															Exit
														EndIf
													EndIf
												EndIf
											Next
										EndIf
									EndIf
								ElseIf Dist > HideDistance * 0.8 ; ~ Move randomly from waypoint to another
									If Rand(70) = 1 Then TeleportCloser(n)
								Else ; ~ Actively move towards the player
									n\State = CurveValue(SoundVol, n\State, 3.0)
									
									; ~ Try to open doors
									If Rand(20) = 1 Then
										For d.Doors = Each Doors
											If (Not d\Locked) And d\Open = False And d\Code = "" And d\KeyCard = 0 Then
												For i = 0 To 1
													If d\Buttons[i] <> 0 Then
														If Abs(EntityX(n\Collider) - EntityX(d\Buttons[i])) < 0.5 Then
															If Abs(EntityZ(n\Collider) - EntityZ(d\Buttons[i])) < 0.5 Then
																If (d\OpenState >= 180.0 Or d\OpenState =< 0.0) Then
																	Pvt = CreatePivot()
																	PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.5, EntityZ(n\Collider))
																	PointEntity(Pvt, d\Buttons[i])
																	MoveEntity(Pvt, 0.0, 0.0, n\Speed * 0.6)
																	
																	If EntityPick(Pvt, 0.5) = d\Buttons[i] Then 
																		PlaySound_Strict(LoadTempSound("SFX\Door\DoorOpen173.ogg"))
																		UseDoor(d, False)
																	EndIf
																	FreeEntity(Pvt)
																EndIf
															EndIf
														EndIf
													EndIf
												Next
											EndIf
										Next
									EndIf
									
									If chs\NoTarget Then
										Temp = False
										n\EnemyX = 0.0
										n\EnemyY = 0.0
										n\EnemyZ = 0.0
									EndIf
									
									; ~ Attacks
									If Temp Then 				
										If Dist < 0.65 Then
											If KillTimer >= 0.0 And (Not chs\GodMode) Then
												Select PlayerRoom\RoomTemplate\Name
													Case "lockroom", "room2closets", "room895"
														;[Block]
														DeathMSG = "Subject D-9341. Cause of death: Fatal cervical fracture. The surveillance tapes confirm that the subject was killed by SCP-173."	
														;[End Block]
													Case "173"
														;[Block]
														DeathMSG = "Subject D-9341. Cause of death: Fatal cervical fracture. According to Security Chief Franklin who was present at SCP-173's containment "
														DeathMSG = DeathMSG + "chamber during the breach, the subject was killed by SCP-173 as soon as the disruptions in the electrical network started."
														;[End block]
													Case "room2doors"
														;[Block]
														DeathMSG = Chr(34) + "If I'm not mistaken, one of the main purposes of these rooms was to stop SCP-173 from moving further in the event of a containment breach. "
														DeathMSG = DeathMSG + "So, who's brilliant idea was it to put A GODDAMN MAN-SIZED VENTILATION DUCT in there?" + Chr(34)
														;[End Block]
													Default 
														;[Block]
														DeathMSG = "Subject D-9341. Cause of death: Fatal cervical fracture. Assumed to be attacked by SCP-173."
														;[End Block]
												End Select
												
												If (Not chs\GodMode) Then n\Idle = True
												
												PlaySound_Strict(NeckSnapSFX(Rand(0, 2)))
												If Rand(2) = 1 Then 
													TurnEntity(Camera, 0.0, Rnd(80.0, 100.0), 0.0)
												Else
													TurnEntity(Camera, 0.0, Rnd(-100.0, -80.0), 0.0)
												EndIf
												Kill()
											EndIf
										Else
											PointEntity(n\Collider, Collider)
											RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), EntityRoll(n\Collider))
											TranslateEntity(n\Collider,Cos(EntityYaw(n\Collider) + 90.0) * n\Speed * FPSfactor, 0.0, Sin(EntityYaw(n\Collider) + 90.0) * n\Speed * FPSfactor)
										EndIf
									Else ; ~ Move To the location where he was Last seen							
										If n\EnemyX <> 0.0 Then						
											If Distance(EntityX(n\Collider), EntityZ(n\Collider), n\EnemyX, n\EnemyZ) > 0.5 Then
												AlignToVector(n\Collider, n\EnemyX - EntityX(n\Collider), 0.0, n\EnemyZ - EntityZ(n\Collider), 3)
												MoveEntity(n\Collider, 0.0, 0.0, n\Speed * FPSfactor)
												If Rand(500) = 1 Then n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
											Else
												n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
											End If
										Else
											If Rand(400) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 10.0)
											TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider) + 90.0) * n\Speed * FPSfactor, 0.0, Sin(EntityYaw(n\Collider) + 90.0) * n\Speed * FPSfactor)
										End If
									EndIf
								EndIf
							EndIf
						EndIf
						PositionEntity(n\Collider, EntityX(n\Collider), Min(EntityY(n\Collider), 0.35), EntityZ(n\Collider))
					Else ; ~ SCP-173 was captured by MTFs
						If n\Target <> Null Then
							Local Tmp% = False
							
							If Dist > HideDistance * 0.7 Then
								If EntityVisible(n\OBJ, Collider) = False
									Tmp = True
								EndIf
							EndIf
							If (Not Tmp) Then
								PointEntity(n\OBJ, n\Target\Collider)
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0, True)								
								Dist = EntityDistance(n\Collider, n\Target\Collider)
								MoveEntity(n\Collider, 0.0, 0.0, 0.016 * FPSfactor * Max(Min((Dist * 2.0 - 1.0) * 0.5, 1.0), -0.5))
								n\GravityMult = 1.0
							Else
								PositionEntity(n\Collider, EntityX(n\Target\Collider), EntityY(n\Target\Collider) + 0.3, EntityZ(n\Target\Collider))
								ResetEntity(n\Collider)
								n\DropSpeed = 0.0
								n\GravityMult = 0.0
							EndIf
						EndIf
						
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) + 0.05 + Sin(MilliSecs2() * 0.08) * 0.02, EntityZ(n\Collider))
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						
						ShowEntity(n\OBJ2)
						
						PositionEntity(n\OBJ2, EntityX(n\Collider), EntityY(n\Collider) - 0.05 + Sin(MilliSecs2() * 0.08) * 0.02, EntityZ(n\Collider))
						RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
					EndIf
				EndIf
				;[End block]
			Case NPCtype106
				;[Block]
				If Contained106 Then
					n\Idle = True
					HideEntity(n\OBJ)
					HideEntity(n\OBJ2)
					PositionEntity(n\OBJ, 0.0, 500.0, 0.0, True)
				Else
					Dist = EntityDistance(n\Collider, Collider)
					
					Local Spawn106% = True
					
					; ~ Checking if SCP-106 is allowed to spawn
					If PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then Spawn106 = False
					For e.Events = Each Events
						If e\EventName = "room860"
							If e\EventState = 1.0
								Spawn106 = False
							EndIf
							Exit
						EndIf
					Next
					If PlayerRoom\RoomTemplate\Name$ = "room049" And EntityY(Collider) =< -2848.0 * RoomScale Then
						Spawn106 = False
					EndIf
					; ~ GateA event has been triggered - don't make 106 disapper!
					; ~ The reason why this is a seperate For loop is because we need to make sure that room860 would not be able to overwrite the "spawn106%" variable
					For e.Events = Each Events
						If e\EventName = "gatea"
							If e\EventState <> 0.0
								Spawn106 = True
								If PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
									n\Idle = True
								Else
									n\Idle = False
								EndIf
							EndIf
							Exit
						EndIf
					Next
					If (Not Spawn106) And n\State =< 0.0 Then
						n\State = Rnd(22000.0, 27000.0)
						PositionEntity(n\Collider, 0.0, 500.0, 0.0)
					EndIf
					
					If (Not n\Idle) And Spawn106%
						If n\State =< 0.0 Then ; ~ Attacking	
							If EntityY(n\Collider) < EntityY(Collider) - 20.0 - 0.55 Then
								If Not PlayerRoom\RoomTemplate\DisableDecals Then
									de.Decals = CreateDecal(0, EntityX(Collider), 0.01, EntityZ(Collider), 90.0, Rnd(360.0), 0.0)
									de\Size = 0.05 : de\SizeChange = 0.001 : EntityAlpha(de\OBJ, 0.8) : UpdateDecals()
								EndIf
								
								n\PrevY = EntityY(Collider)
								
								SetAnimTime(n\OBJ, 110.0)
								
								If PlayerRoom\RoomTemplate\Name <> "room895"
									PositionEntity(n\Collider, EntityX(Collider), EntityY(Collider) - 15.0, EntityZ(Collider))
								EndIf
								
								PlaySound_Strict(DecaySFX(0))
							End If
							
							If Rand(500) = 1 Then PlaySound2(OldManSFX(Rand(0, 2)), Camera, n\Collider)
							n\SoundCHN = LoopSound2(OldManSFX(4), n\SoundCHN, Camera, n\Collider, 8.0, 0.8)
							
							If n\State > -10.0 Then
								ShouldPlay = 66
								If n\Frame < 259.0 Then
									PositionEntity(n\Collider, EntityX(n\Collider), n\PrevY - 0.15, EntityZ(n\Collider))
									PointEntity(n\OBJ, Collider)
									RotateEntity(n\Collider, 0.0, CurveValue(EntityYaw(n\OBJ),EntityYaw(n\Collider), 100.0), 0.0, True)
									
									AnimateNPC(n, 110.0, 259.0, 0.15, False)
								Else
									n\State = -10.0
								EndIf
							Else
								If PlayerRoom\RoomTemplate\Name <> "gatea" Then ShouldPlay = 10
								
								Local Visible% = False
								
								If Dist < 8.0 Then
									Visible = EntityVisible(n\Collider, Collider)
								EndIf
								
								If chs\NoTarget Then Visible = False
								
								If Visible Then
									If PlayerRoom\RoomTemplate\Name <> "gatea" Then n\PathTimer = 0.0
									If EntityInView(n\Collider, Camera) Then
										GiveAchievement(Achv106)
										
										BlurVolume = Max(Max(Min((4.0 - Dist) / 6.0, 0.9), 0.1), BlurVolume)
										CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * 20.0 * Max((4.0 - Dist) / 4.0, 0.0))
										
										If MilliSecs2() - n\LastSeen > 60000.0 Then 
											CurrCameraZoom = 40.0
											PlaySound_Strict(HorrorSFX(6))
											n\LastSeen = MilliSecs2()
										EndIf
									EndIf
								Else
									n\State = n\State - FPSfactor
								End If
								
								If Dist > 0.8 Then
									If (Dist > 25.0 Or PlayerRoom\RoomTemplate\Name = "pocketdimension" Or Visible Or n\PathStatus <> 1) And PlayerRoom\RoomTemplate\Name <> "gatea" Then 
										If (Dist > 40.0 Or PlayerRoom\RoomTemplate\Name = "pocketdimension") Then
											TranslateEntity(n\Collider, 0.0, ((EntityY(Collider) - 0.14) - EntityY(n\Collider)) / 50.0, 0.0)
										EndIf
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										
										PointEntity(n\OBJ, Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0)
										
										If KillTimer >= 0.0 Then
											PrevFrame = n\Frame
											AnimateNPC(n, 284.0, 333.0, n\CurrSpeed * 43.0)
											
											If PrevFrame =< 286.0 And n\Frame > 286.0 Then
												PlaySound2(Step2SFX(Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))	
											ElseIf PrevFrame =< 311.0 And n\Frame > 311.0 
												PlaySound2(Step2SFX(Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))
											EndIf
										Else 
											n\CurrSpeed = 0.0
										EndIf
										
										n\PathTimer = Max(n\PathTimer - FPSfactor, 0.0)
										If n\PathTimer =< 0.0 Then
											n\PathStatus = FindPath(n, EntityX(Collider, True), EntityY(Collider, True), EntityZ(Collider, True))
											n\PathTimer = 70 * 10.0
										EndIf
									Else 
										If n\PathTimer =< 0.0 Then
											n\PathStatus = FindPath(n, EntityX(Collider, True), EntityY(Collider, True), EntityZ(Collider, True))
											n\PathTimer = 70 * 10.0
											n\CurrSpeed = 0.0
										Else
											n\PathTimer = Max(n\PathTimer - FPSfactor, 0.0)
											
											If n\PathStatus = 2 Then
												n\CurrSpeed = 0.0
											ElseIf n\PathStatus = 1
												While n\Path[n\PathLocation] = Null
													If n\PathLocation > 19 Then 
														n\PathLocation = 0 : n\PathStatus = 0
														Exit
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Wend
												
												If n\Path[n\PathLocation] <> Null Then 
													TranslateEntity(n\Collider, 0.0, ((EntityY(n\Path[n\PathLocation]\OBJ, True) - 0.11) - EntityY(n\Collider)) / 50.0, 0.0)
													
													PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
													
													Dist2 = EntityDistance(n\Collider, n\Path[n\PathLocation]\OBJ)
													
													RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), Min(20.0, Dist2 * 10.0)), 0.0)
													n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
													
													PrevFrame# = AnimTime(n\OBJ)
													AnimateNPC(n, 284.0, 333.0, n\CurrSpeed * 43.0)
													If PrevFrame =< 286.0 And n\Frame > 286.0 Then
														PlaySound2(Step2SFX(Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))	
													ElseIf PrevFrame =< 311.0 And n\Frame > 311.0 
														PlaySound2(Step2SFX(Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))
													EndIf
													
													If Dist2 < 0.2 Then n\PathLocation = n\PathLocation + 1
												EndIf
											ElseIf n\PathStatus = 0
												If n\State3 = 0.0 Then AnimateNPC(n, 334.0, 494.0, 0.3)
												n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
											EndIf
										EndIf
									EndIf
								ElseIf PlayerRoom\RoomTemplate\Name <> "gatea" And (Not chs\NoTarget) Then
									If Dist > 0.5 Then 
										n\CurrSpeed = CurveValue(n\Speed * 2.5, n\CurrSpeed, 10.0)
									Else
										n\CurrSpeed = 0.0
									EndIf
									AnimateNPC(n, 105.0, 110.0, 0.15, False)
									
									If KillTimer >= 0.0 And FallTimer >= 0.0 Then
										PointEntity(n\OBJ, Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0)									
										
										If Ceil(n\Frame) = 110.0 And (Not chs\GodMode) Then
											PlaySound_Strict(DamageSFX(1))
											PlaySound_Strict(HorrorSFX(5))											
											If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
												DeathMSG = "Subject D-9341. Body partially decomposed by what is assumed to be SCP-106's " + Chr(34) + "corrosion" + Chr(34) + " effect. Body disposed of via incineration."
												Kill()
											Else
												PlaySound_Strict(OldManSFX(3))
												FallTimer = Min(-1.0, FallTimer)
												PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
												ResetEntity(Head)
												RotateEntity(Head, 0.0, EntityYaw(Camera) + Rnd(-45.0, 45.0), 0.0)
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf 
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
							
							If n\State =< Rnd(-3500.0, -3000.0) Then 
								If (Not EntityInView(n\OBJ, Camera)) And Dist > 5.0 Then
									n\State = Rnd(22000.0, 27000.0)
									PositionEntity(n\Collider, 0.0, 500.0, 0.0)
								EndIf
							EndIf
							
							If FallTimer < -250.0 Then
								MoveToPocketDimension()
								n\State = 250.0 ; ~ Make SCP-106 idle for a while
							EndIf
							
							If n\Reload = 0.0 Then
                                If Dist > 10.0 And PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And n\State < -5.0 Then ; ~ Timer idea -- Juanjpro
                                    If (Not EntityInView(n\OBJ, Camera)) Then
                                        TurnEntity(Collider, 0.0, 180.0, 0.0)
                                        Pick = EntityPick(Collider, 5.0)
                                        TurnEntity(Collider, 0.0, 180.0, 0.0)
                                        If Pick <> 0 Then
											TeleportEntity(n\Collider, PickedX(), PickedY(), PickedZ(), n\CollRadius)
                                            PointEntity(n\Collider, Collider)
                                            RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
                                            MoveEntity(n\Collider, 0.0, 0.0, -2.0)
                                            PlaySound2(OldManSFX(3), Camera, n\Collider)
											n\SoundCHN2 = PlaySound2(OldManSFX(6 + Rand(0, 2)), Camera, n\Collider)
                                            n\PathTimer = 0.0
                                            n\Reload = (70 * 10.0) / (SelectedDifficulty\OtherFactors + 1.0)
										EndIf
                                    EndIf
                                EndIf
                            EndIf
                            n\Reload = Max(0, n\Reload - FPSfactor)
							
							UpdateSoundOrigin(n\SoundCHN2,Camera,n\Collider)
						Else ; ~ Idling outside the map
							n\CurrSpeed = 0
							MoveEntity n\Collider, 0, ((EntityY(Collider) - 30) - EntityY(n\Collider)) / 200.0, 0
							n\DropSpeed = 0
							n\Frame = 110
							
							If (Not PlayerRoom\RoomTemplate\DisableDecals) Then
								If PlayerRoom\RoomTemplate\Name <> "gatea"
									If (SelectedDifficulty\AggressiveNPCs) Then
										n\State = n\State - (FPSfactor * 2)
									Else
										n\State = n\State - FPSfactor
									EndIf
								EndIf
							EndIf
						End If
						
						ResetEntity(n\Collider)
						n\DropSpeed = 0.0
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.15, EntityZ(n\Collider))
						
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider), 0.0)
						
						PositionEntity(n\OBJ2, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
						RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						MoveEntity(n\OBJ2, 0.0, 8.6 * 0.11, -1.5 * 0.11)
						
						If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "gatea" Then
							HideEntity(n\OBJ2)
						Else
							If Dist < CameraFogFar * LightVolume * 0.6 Then
								HideEntity(n\OBJ2)
							Else
								If n\State =< -10.0 Then
									ShowEntity(n\OBJ2)
									EntityAlpha(n\OBJ2, Min(Dist - CameraFogFar * LightVolume * 0.6, 1.0))
								EndIf
							EndIf
						EndIf						
					Else
						HideEntity(n\OBJ2)
					EndIf
				EndIf
				;[End Block]
			Case NPCtype096
				;[Block]
				Dist = EntityDistance(Collider, n\Collider)
				
				Select n\State
					Case 0
						;[Block]
						If Dist<8.0 Then
							GiveAchievement(Achv096)
							;If n\Sound = 0 Then
							;	n\Sound = LoadSound_Strict("SFX\Music\096.ogg")
							;Else
							;	n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 8.0, 1.0)
							;EndIf
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\Music\096.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,8.0,1.0)
							EndIf
							
							If n\State3 = -1
								AnimateNPC(n,936,1263,0.1,False)
								If n\Frame=>1262.9
									n\State = 5
									n\State3 = 0
									n\Frame = 312
								EndIf
							Else
								AnimateNPC(n,936,1263,0.1)
								If n\State3 < 70*6
									n\State3=n\State3+FPSfactor
								Else
									If Rand(1,5)=1
										n\State3 = -1
									Else
										n\State3=70*(Rand(0,3))
									EndIf
								EndIf
							EndIf
							;AnimateNPC(n, 1085,1412, 0.1) ;sitting
							
							Angle = WrapAngle(DeltaYaw(n\Collider, Collider));-EntityYaw(n\Collider,True))
							
							If (Not chs\NoTarget)
								If Angle<90 Or Angle>270 Then
									CameraProject Camera,EntityX(n\Collider), EntityY(n\Collider)+0.25, EntityZ(n\Collider)
									
									If ProjectedX()>0 And ProjectedX()<GraphicWidth Then
										If ProjectedY()>0 And ProjectedY()<GraphicHeight Then
											If EntityVisible(Collider, n\Collider) Then
												If (BlinkTimer < - 16 Or BlinkTimer > - 6)
													PlaySound_Strict LoadTempSound("SFX\SCP\096\Triggered.ogg")
													
													CurrCameraZoom = 10
													
													n\Frame = 194
													;n\Frame = 307
													StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
													n\Sound = 0
													n\State = 1
													n\State3 = 0
												EndIf
											EndIf									
										EndIf
									EndIf								
									
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case 4
						;[Block]
						CurrCameraZoom = CurveValue(Max(CurrCameraZoom, (Sin(Float(MilliSecs2())/20.0)+1.0) * 10.0),CurrCameraZoom,8.0)
						
						If n\Target = Null Then 
							;If n\Sound = 0 Then
							;	n\Sound = LoadSound_Strict("SFX\SCP\096\Scream.ogg")
							;Else
							;	n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 7.5, 1.0)
							;EndIf
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\SCP\096\Scream.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,7.5,1.0)
							EndIf
							
							;If n\Sound2 = 0 Then
							;	n\Sound2 = LoadSound_Strict("SFX\Music\096Chase.ogg")
							;Else
							;	If n\SoundChn2 = 0 Then
							;		n\SoundChn2 = PlaySound_Strict (n\Sound2)
							;	Else
							;		If (Not ChannelPlaying(n\SoundChn2)) Then n\SoundChn2 = PlaySound_Strict(n\Sound2)
							;		ChannelVolume(n\SoundChn2, Min(Max(8.0-dist,0.6),1.0)*SFXVolume#)
							;	EndIf
							;EndIf
							If n\SoundCHN2 = 0
								n\SoundCHN2 = StreamSound_Strict("SFX\Music\096Chase.ogg",0)
								n\SoundCHN2_IsStream = 2
							Else
								SetStreamVolume_Strict(n\SoundCHN2,Min(Max(8.0-Dist,0.6),1.0)*SFXVolume#)
							EndIf
						EndIf
						
						If chs\NoTarget And n\Target = Null Then n\State = 5
						
						If KillTimer =>0 Then
							
							If MilliSecs2() > n\State3 Then
								n\LastSeen=0
								If n\Target=Null Then
									If EntityVisible(Collider, n\Collider) Then n\LastSeen=1
								Else
									If EntityVisible(n\Target\Collider, n\Collider) Then n\LastSeen=1
								EndIf
								n\State3=MilliSecs2()+3000
							EndIf
							
							If n\LastSeen=1 Then
								n\PathTimer=Max(70*3, n\PathTimer)
								n\PathStatus=0
								
								If n\Target<> Null Then Dist = EntityDistance(n\Target\Collider, n\Collider)
								
								If Dist < 2.8 Or n\Frame<150 Then 
									If n\Frame>193 Then n\Frame = 2.0 ;go to the start of the jump animation
									
									AnimateNPC(n, 2, 193, 0.7)
									
									If Dist > 1.0 Then 
										n\CurrSpeed = CurveValue(n\Speed*2.0,n\CurrSpeed,15.0)
									Else
										n\CurrSpeed = 0
										
										If n\Target=Null Then
											If (Not chs\GodMode) Then 
												PlaySound_Strict DamageSFX(4)
												
												Pvt = CreatePivot()
												CameraShake = 30
												BlurTimer = 2000
												DeathMSG = "A large amount of blood found in [DATA REDACTED]. DNA indentified as Subject D-9341. Most likely [DATA REDACTED] by SCP-096."
												Kill()
												KillAnim = 1
												For i = 0 To 6
													PositionEntity Pvt, EntityX(Collider)+Rnd(-0.1,0.1),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.1,0.1)
													TurnEntity Pvt, 90, 0, 0
													EntityPick(Pvt,0.3)
													
													de.Decals = CreateDecal(Rand(15,16), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
													de\Size = Rnd(0.2,0.6) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
												Next
												FreeEntity Pvt
											EndIf
										EndIf				
									EndIf
									
									If n\Target=Null Then
										PointEntity n\Collider, Collider
									Else
										PointEntity n\Collider, n\Target\Collider
									EndIf
									
								Else
									If n\Target=Null Then 
										PointEntity n\OBJ, Collider
									Else
										PointEntity n\OBJ, n\Target\Collider
									EndIf
									
									RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 5.0), 0
									
									;1000
									If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
									
									If n\Frame<906 Then ;1058
										AnimateNPC(n,737,906,n\Speed*8,False)
										;AnimateNPC(n, 892,1058, n\Speed*8, False)
									Else
										AnimateNPC(n,907,935,n\CurrSpeed*8)
										;AnimateNPC(n, 1059,1084, n\CurrSpeed*8)	
									EndIf
								EndIf
								
								RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True
								MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
								
							Else
								If n\PathStatus = 1 Then
									
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity n\OBJ, n\Path[n\PathLocation]\obj
										
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 5.0), 0
										
										;1000
										If n\Frame>847 Then n\CurrSpeed = CurveValue(n\Speed*1.5,n\CurrSpeed,15.0)
										MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
										
										If n\Frame<906 Then ;1058
											AnimateNPC(n,737,906,n\Speed*8,False)
											;AnimateNPC(n, 892,1058, n\Speed*8, False)
										Else
											AnimateNPC(n,907,935,n\CurrSpeed*8)
											;AnimateNPC(n, 1059,1084, n\CurrSpeed*8)	
										EndIf
										
										Dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If Dist2 < 0.8 Then ;0.4
											If n\Path[n\PathLocation]\door <> Null Then
												If n\Path[n\PathLocation]\door\open = False Then
													n\Path[n\PathLocation]\door\open = True
													n\Path[n\PathLocation]\door\fastopen = 1
													PlaySound2(OpenDoorFastSFX, Camera, n\Path[n\PathLocation]\door\obj)
												EndIf
											EndIf							
											If Dist2 < 0.7 Then n\PathLocation = n\PathLocation + 1 ;0.2
										EndIf 
									EndIf
									
								Else
									;AnimateNPC(n, 892,972, 0.2)
									AnimateNPC(n,737,822,0.2)
									
									n\PathTimer = Max(0, n\PathTimer-FPSfactor)
									If n\PathTimer=<0 Then
										If n\Target<>Null Then
											n\PathStatus = FindPath(n, EntityX(n\Target\Collider),EntityY(n\Target\Collider)+0.2,EntityZ(n\Target\Collider))	
										Else
											n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider))	
										EndIf
										n\PathTimer = 70*5
									EndIf
								EndIf
							EndIf
							
							If Dist > 32.0 Or EntityY(n\Collider)<-50 Then
								If Rand(50)=1 Then TeleportCloser(n)
							EndIf
						Else ;play the eating animation if killtimer < 0 
							AnimateNPC(n, Min(27,AnimTime(n\OBJ)), 193, 0.5)
							
							;Animate2(n\obj, AnimTime(n\obj), Min(27,AnimTime(n\obj)), 193, 0.5)
						EndIf
						
						
						;[End Block]
					Case 1,2,3
						;[Block]
						;If n\Sound = 0 Then
						;	n\Sound = LoadSound_Strict("SFX\Music\096Angered.ogg")
						;Else
						;	n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 10.0, 1.0)
						;EndIf
						If n\SoundCHN = 0
							n\SoundCHN = StreamSound_Strict("SFX\Music\096Angered.ogg",0)
							n\SoundCHN_IsStream = True
						Else
							UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,10.0,1.0)
						EndIf
						
						If n\State=1 Then ; get up
							If n\Frame<312
								AnimateNPC(n,193,311,0.3,False)
								If n\Frame > 310.9 Then n\State = 2 : n\Frame = 737
							ElseIf n\Frame>=312 And n\Frame<=422
								AnimateNPC(n,312,422,0.3,False)
								If n\Frame > 421.9 Then n\Frame = 677
							Else
								AnimateNPC(n,677,736,0.3,False)
								If n\Frame > 735.9 Then n\State = 2 : n\Frame = 737
							EndIf
							;If n\Frame>1085 Then
							;	AnimateNPC(n, 1085, 1412, 0.3,False)
							;	If n\Frame> 1411.9 Then n\Frame = 307
							;Else
							;	AnimateNPC(n, 307, 424, 0.3, False)
							;	If n\Frame > 423.9 Then n\State = 2 : n\Frame = 892
							;EndIf
						ElseIf n\State=2
							AnimateNPC(n,737,822,0.3,False)
							If n\Frame=>822 Then n\State=3 : n\State2=0
							;AnimateNPC(n, 833, 972, 0.3, False)
							;If n\Frame=>972 Then n\State = 3 : n\State2=0
						ElseIf n\State=3
							n\State2 = n\State2+FPSfactor
							If n\State2 > 70*18 Then
								AnimateNPC(n,823,847,n\Speed*8,False)
								;AnimateNPC(n, 973, 1001, 0.5, False)
								If n\Frame>846.9 Then ;1000.9 
									n\State = 4
									StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
								EndIf
							Else
								AnimateNPC(n,737,822,0.3)
								;AnimateNPC(n, 892,978, 0.3)
							EndIf
						EndIf
						;[End Block]
					Case 5
						;[Block]
						If Dist < 16.0 Then 
							
							If Dist < 4.0 Then
								GiveAchievement(Achv096)
							EndIf
							
							;If n\Sound = 0 Then
							;	n\Sound = LoadSound_Strict("SFX\Music\096.ogg")
							;Else
							;	n\SoundChn = LoopSound2(n\Sound, n\SoundChn, Camera, n\Collider, 14.0, 1.0)
							;EndIf
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\Music\096.ogg",0)
								n\SoundCHN_IsStream = True
							Else
								UpdateStreamSoundOrigin(n\SoundCHN,Camera,n\Collider,14.0,1.0)
							EndIf
							
							If n\Frame>=422
								n\State2=n\State2+FPSfactor
								If n\State2>1000 Then ;walking around
									If n\State2>1600 Then n\State2=Rand(0,500) ;: n\Frame = 1457 ;1652
									
									;1652
									If n\Frame<1382 Then ;idle to walk
										n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
										AnimateNPC(n,1369,1382,n\CurrSpeed*45,False)
										;AnimateNPC(n, 1638,1652, n\CurrSpeed*45,False)
									Else
										n\CurrSpeed = CurveValue(n\Speed*0.1,n\CurrSpeed,5.0)
										AnimateNPC(n,1383,1456,n\CurrSpeed*45)
										;AnimateNPC(n, 1653,1724, n\CurrSpeed*45) ;walk
									EndIf
									
									If MilliSecs2() > n\State3 Then
										n\LastSeen=0
										If EntityVisible(Collider, n\Collider) Then 
											n\LastSeen=1
										Else
											HideEntity n\Collider
											EntityPick(n\Collider, 1.5)
											If PickedEntity() <> 0 Then
												n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
											EndIf
											ShowEntity n\Collider
										EndIf
										n\State3=MilliSecs2()+3000
									EndIf
									
									If n\LastSeen Then 
										PointEntity n\OBJ, Collider
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),130.0),0
										If Dist < 1.5 Then n\State2=0
									Else
										RotateEntity n\Collider, 0, CurveAngle(n\Angle,EntityYaw(n\Collider),50.0),0
									EndIf
								Else
									;1638
									If n\Frame>472 Then ;walk to idle
										n\CurrSpeed = CurveValue(n\Speed*0.05,n\CurrSpeed,8.0)
										AnimateNPC(n,1383,1469,n\CurrSpeed*45,False)
										If n\Frame=>1468.9 Then n\Frame=423
										;AnimateNPC(n, 1652, 1638, -n\CurrSpeed*45,False)
									Else ;idle
										n\CurrSpeed = CurveValue(0,n\CurrSpeed,4.0)	
										AnimateNPC(n,423,471,0.2)
										;AnimateNPC(n, 585, 633, 0.2) ;idle
									EndIf
								EndIf
								
								MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
							Else
								AnimateNPC(n,312,422,0.3,False)
							EndIf
							
							Angle = WrapAngle(DeltaYaw(n\Collider, Camera));-EntityYaw(n\Collider))
							If (Not chs\NoTarget)
								If Angle<55 Or Angle>360-55 Then
									CameraProject Camera,EntityX(n\Collider), EntityY(Collider)+5.8*0.2-0.25, EntityZ(n\Collider)
									
									If ProjectedX()>0 And ProjectedX()<GraphicWidth Then
										If ProjectedY()>0 And ProjectedY()<GraphicHeight Then
											If EntityVisible(Collider, n\Collider) Then
												If (BlinkTimer < - 16 Or BlinkTimer > - 6)
													PlaySound_Strict LoadTempSound("SFX\SCP\096\Triggered.ogg")
													
													CurrCameraZoom = 10
													
													If n\Frame >= 422
														n\Frame = 677 ;833
													EndIf
													StopStream_Strict(n\SoundCHN) : n\SoundCHN=0
													n\Sound = 0
													n\State = 2
												EndIf
											EndIf									
										EndIf
									EndIf
									
								EndIf
							EndIf
						EndIf
						;[End Block]
				End Select
				
				;ResetEntity(n\Collider)
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider)-0.03, EntityZ(n\Collider)) ;-0.07
				
				RotateEntity n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider), 0
				;[End Block]
			Case NPCtype049
				;[Block]
				;n\state = the "main state" of the NPC
				;n\state2 = attacks the player when the value is above 0.0
				;n\state3 = timer for updating the path again
				
				PrevFrame# = n\Frame
				
				Dist  = EntityDistance(Collider, n\Collider)
				
				n\BlinkTimer# = 1.0
				
				If n\Idle > 0.1
					If PlayerRoom\RoomTemplate\Name$ <> "room049"
						n\Idle = Max(n\Idle-(1+SelectedDifficulty\aggressiveNPCs)*FPSfactor,0.1)
					EndIf
					n\DropSpeed = 0
					If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN)
					If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2)
					PositionEntity n\Collider,0,-500,0
					PositionEntity n\OBJ,0,-500,0
				Else
					If n\Idle = 0.1 Then
						If PlayerInReachableRoom() Then
							For i = 0 To 3
								If PlayerRoom\Adjacent[i]<>Null Then
									For j = 0 To 3
										If PlayerRoom\Adjacent[i]\Adjacent[j]<>Null Then
											TeleportEntity(n\Collider,PlayerRoom\Adjacent[i]\Adjacent[j]\x,0.5,PlayerRoom\Adjacent[i]\Adjacent[j]\z,n\CollRadius,True)
											Exit
										EndIf
									Next
									Exit
								EndIf
							Next
							n\Idle = 0.0
							DebugLog "SCP-049 not idle"
						EndIf
					EndIf
					
					Select n\State
						Case 0 ;nothing (used for events)
						Case 1 ;looking around before getting active
							;[Block]
							If n\Frame=>538 Then
								AnimateNPC(n, 659, 538, -0.45, False)
								If n\Frame > 537.9 Then n\Frame = 37
								
								;Animate2(n\obj, AnimTime(n\obj), 659, 538, -0.45, False)
								;If AnimTime(n\obj)=538 Then SetAnimTime(n\obj, 37)
							Else
								AnimateNPC(n, 37, 269, 0.7, False)
								If n\Frame>268.9 Then n\State = 2
								
								;Animate2(n\obj, AnimTime(n\obj), 37, 269, 0.7, False)
								;If AnimTime(n\obj)=269 Then n\State = 2
							EndIf
							;[End Block]
						Case 2 ;being active
							;[Block]
							If (Dist < HideDistance*2) And (Not n\Idle) And PlayerInReachableRoom(True) Then
								n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
								PlayerSeeAble% = MeNPCSeesPlayer(n)
								If PlayerSeeAble%=True Or n\State2>0 Then ;Player is visible for 049's sight - attacking
									GiveAchievement(Achv049)
									
									;Playing a sound after detecting the player
									If n\PrevState <= 1 And ChannelPlaying(n\SoundCHN2)=False
										If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
										n\Sound2 = LoadSound_Strict("SFX\SCP\049\Spotted"+Rand(1,7)+".ogg")
										n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\OBJ)
										n\PrevState = 2
									EndIf
									n\PathStatus = 0
									n\PathTimer# = 0.0
									n\PathLocation = 0
									If PlayerSeeAble%=True Then n\State2 = 70*2
									
									PointEntity n\OBJ,Collider
									RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),10.0),0
									
									If Dist < 0.5 Then
										If WearingHazmat>0 Then
											BlurTimer = BlurTimer+FPSfactor*2.5
											If BlurTimer>250 And BlurTimer-FPSfactor*2.5 <= 250 And n\PrevState<>3 Then
												If n\SoundCHN2 <> 0 Then StopChannel(n\SoundCHN2)
												n\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\049\TakeOffHazmat.ogg"))
												n\PrevState=3
											ElseIf BlurTimer => 500
												For i = 0 To MaxItemAmount-1
													If Inventory(i)<>Null Then
														If Instr(Inventory(i)\itemtemplate\tempname,"hazmatsuit") And WearingHazmat<3 Then
															If Inventory(i)\state2 < 3 Then
																Inventory(i)\state2 = Inventory(i)\state2 + 1
																BlurTimer = 260.0
																CameraShake = 2.0
															Else
																RemoveItem(Inventory(i))
																WearingHazmat = False
															EndIf
															Exit
														EndIf
													EndIf
												Next
											EndIf
										ElseIf Wearing714 Then
											BlurTimer = BlurTimer+FPSfactor*2.5
											If BlurTimer>250 And BlurTimer-FPSfactor*2.5 <= 250 And n\PrevState<>3 Then
												If n\SoundCHN2 <> 0 Then StopChannel(n\SoundCHN2)
												n\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\SCP\049\714Equipped.ogg"))
												n\PrevState=3
											ElseIf BlurTimer => 500
												Wearing714=False
											EndIf
										Else
											CurrCameraZoom = 20.0
											BlurTimer = 500.0
											
											If (Not chs\GodMode) Then
												If PlayerRoom\RoomTemplate\Name$ = "room049"
													DeathMSG = "Three (3) active instances of SCP-049-2 discovered in the tunnel outside SCP-049's containment chamber. Terminated by Nine-Tailed Fox."
													For e.events = Each Events
														If e\EventName = "room049" Then e\EventState=-1 : Exit
													Next
												Else
													DeathMSG = "An active instance of SCP-049-2 was discovered in [REDACTED]. Terminated by Nine-Tailed Fox."
													Kill()
												EndIf
												PlaySound_Strict HorrorSFX(13)
												If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
												n\Sound2 = LoadSound_Strict("SFX\SCP\049\Kidnap"+Rand(1,2)+".ogg")
												n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\OBJ)
												n\State = 3
											EndIf										
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor	
										
										If n\PrevState = 3 Then n\PrevState = 2
										
										If Dist < 3.0 Then
											AnimateNPC(n, Max(Min(AnimTime(n\OBJ),428.0),387), 463.0, n\CurrSpeed*38)
										Else
											If n\Frame>428.0 Then
												AnimateNPC(n, Min(AnimTime(n\OBJ),463.0), 498.0, n\CurrSpeed*38,False)
												If n\Frame>497.9 Then n\Frame = 358
											Else
												AnimateNPC(n, Max(Min(AnimTime(n\OBJ),358.0),346), 393.0, n\CurrSpeed*38)
											EndIf
										EndIf
									EndIf
								Else ;Finding a path to the player
									If n\PathStatus = 1 ;Path to player found
										While n\Path[n\PathLocation]=Null
											If n\PathLocation > 19
												n\PathLocation = 0 : n\PathStatus = 0 : Exit
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Wend
										If n\Path[n\PathLocation]<>Null Then
											;closes doors behind him
											If n\PathLocation>0 Then
												If n\Path[n\PathLocation-1] <> Null Then
													If n\Path[n\PathLocation-1]\door <> Null Then
														If (Not n\Path[n\PathLocation-1]\door\IsElevatorDoor) Then
															If EntityDistance(n\Path[n\PathLocation-1]\obj,n\Collider)>0.3 Then
																If (n\Path[n\PathLocation-1]\door\MTFClose) And (n\Path[n\PathLocation-1]\door\open) And (n\Path[n\PathLocation-1]\door\buttons[0]<>0 Or n\Path[n\PathLocation-1]\door\buttons[1]<>0) Then
																	UseDoor(n\Path[n\PathLocation-1]\door, False)
																EndIf
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
											
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											PointEntity n\OBJ,n\Path[n\PathLocation]\obj
											RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),10.0),0
											MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
											
											;opens doors in front of him
											Dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
											If Dist2 < 0.6 Then
												Temp = True
												If n\Path[n\PathLocation]\door <> Null Then
													If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
														If (n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard<>0 Or n\Path[n\PathLocation]\door\Code<>"") And (Not n\Path[n\PathLocation]\door\open) Then
															Temp = False
														Else
															If n\Path[n\PathLocation]\door\open = False And (n\Path[n\PathLocation]\door\buttons[0]<>0 Or n\Path[n\PathLocation]\door\buttons[1]<>0) Then
																UseDoor(n\Path[n\PathLocation]\door, False)
															EndIf
														EndIf
													EndIf
												EndIf
												If Dist2#<0.2 And Temp
													n\PathLocation = n\PathLocation + 1
												ElseIf Dist2#<0.5 And (Not Temp)
													;Breaking up the path when the door in front of 049 cannot be operated by himself
													n\PathStatus = 0
													n\PathTimer# = 0.0
												EndIf
											EndIf
											
											AnimateNPC(n, Max(Min(AnimTime(n\OBJ),358.0),346), 393.0, n\CurrSpeed*38)
											
											;Playing a sound if he hears the player
											If n\PrevState = 0 And ChannelPlaying(n\SoundCHN2)=False
												If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2)
												If Rand(30)=1
													n\Sound2 = LoadSound_Strict("SFX\SCP\049\Searching7.ogg")
												Else
													n\Sound2 = LoadSound_Strict("SFX\SCP\049\Searching"+Rand(1,6)+".ogg")
												EndIf
												n\SoundCHN2 = LoopSound2(n\Sound2,n\SoundCHN2,Camera,n\OBJ)
												n\PrevState = 1
											EndIf
											
											;Resetting the "PrevState" value randomly, to make 049 talking randomly 
											If Rand(600)=1 And ChannelPlaying(n\SoundCHN2)=False Then n\PrevState = 0
											
											If n\PrevState > 1 Then n\PrevState = 1
										EndIf
									Else ;No Path to the player found - stands still and tries to find a path
										;[Block]
										n\PathTimer# = n\PathTimer# + FPSfactor
										If n\PathTimer# > 70*(5-(2*SelectedDifficulty\aggressiveNPCs)) Then
											n\PathStatus = FindPath(n, EntityX(Collider),EntityY(Collider),EntityZ(Collider))
											n\PathTimer# = 0.0
											n\State3 = 0
											
											;Attempt to find a room (the Playerroom or one of it's adjacent rooms) for 049 to go to but select the one closest to him
											If n\PathStatus <> 1 Then
												Local closestdist# = EntityDistance(PlayerRoom\obj,n\Collider)
												Local closestRoom.Rooms = PlayerRoom
												Local currdist# = 0.0
												For i = 0 To 3
													If PlayerRoom\Adjacent[i]<>Null Then
														currdist = EntityDistance(PlayerRoom\Adjacent[i]\obj,n\Collider)
														If currdist < closestdist Then
															closestdist = currdist
															closestRoom = PlayerRoom\Adjacent[i]
														EndIf
													EndIf
												Next
												n\PathStatus = FindPath(n,EntityX(closestRoom\obj),0.5,EntityZ(closestRoom\obj))
												DebugLog "Find path for 049 in another room (pathstatus: "+n\PathStatus+")"
											EndIf
											
											;Making 3 attempts at finding a path
											While Int(n\State3) < 3
												;Breaking up the path if no "real" path has been found (only 1 waypoint and it is too close)
												If n\PathStatus = 1 Then
													If n\Path[1]<>Null Then
														If n\Path[2]=Null And EntityDistance(n\Path[1]\obj,n\Collider)<0.4 Then
															n\PathLocation = 0
															n\PathStatus = 0
															DebugLog "Breaking up path for 049 because no waypoint number 2 has been found and waypoint number 1 is too close."
														EndIf
													EndIf
													If n\Path[0]<>Null And n\Path[1]=Null Then
														n\PathLocation = 0
														n\PathStatus = 0
														DebugLog "Breaking up path for 049 because no waypoint number 1 has been found."
													EndIf
												EndIf
												
												;No path could still be found, just make 049 go to a room (further away than the very first attempt)
												If n\PathStatus <> 1 Then
													closestdist# = 100.0 ;Prevent the PlayerRoom to be considered the closest, so 049 wouldn't try to find a path there
													closestRoom.Rooms = PlayerRoom
													currdist# = 0.0
													For i = 0 To 3
														If PlayerRoom\Adjacent[i]<>Null Then
															currdist = EntityDistance(PlayerRoom\Adjacent[i]\obj,n\Collider)
															If currdist < closestdist Then
																closestdist = currdist
																For j = 0 To 3
																	If PlayerRoom\Adjacent[i]\Adjacent[j]<>Null Then
																		If PlayerRoom\Adjacent[i]\Adjacent[j]<>PlayerRoom Then
																			closestRoom = PlayerRoom\Adjacent[i]\Adjacent[j]
																			Exit
																		EndIf
																	EndIf
																Next
															EndIf
														EndIf
													Next
													n\PathStatus = FindPath(n,EntityX(closestRoom\obj),0.5,EntityZ(closestRoom\obj))
													DebugLog "Find path for 049 in another further away room (pathstatus: "+n\PathStatus+")"
												EndIf
												
												;Making 049 skip waypoints for doors he can't interact with, but only if the actual path is behind him
												If n\PathStatus = 1 Then
													If n\Path[1]<>Null Then
														If n\Path[1]\door<>Null Then
															If (n\Path[1]\door\locked Or n\Path[1]\door\KeyCard<>0 Or n\Path[1]\door\Code<>"") And (Not n\Path[1]\door\open) Then
																Repeat
																	If n\PathLocation > 19
																		n\PathLocation = 0 : n\PathStatus = 0 : Exit
																	Else
																		n\PathLocation = n\PathLocation + 1
																	EndIf
																	If n\Path[n\PathLocation]<>Null Then
																		If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>(45.0-Abs(DeltaYaw(n\Collider,n\Path[1]\obj))) Then
																			DebugLog "Skip until waypoint number "+n\PathLocation
																			n\State3 = 3
																			Exit
																		EndIf
																	EndIf
																Forever
															Else
																n\State3 = 3
															EndIf
														Else
															n\State3 = 3
														EndIf
													EndIf
												EndIf
												n\State3 = n\State3 + 1
											Wend
										EndIf
										AnimateNPC(n, 269, 345, 0.2)
										;[End Block]
									EndIf
								EndIf
								
								If n\CurrSpeed > 0.005 Then
									If (PrevFrame < 361 And n\Frame=>361) Or (PrevFrame < 377 And n\Frame=>377) Then
										PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
									ElseIf (PrevFrame < 431 And n\Frame=>431) Or (PrevFrame < 447 And n\Frame=>447) Then
										PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
									EndIf
								EndIf
								
								If ChannelPlaying(n\SoundCHN2)
									UpdateSoundOrigin(n\SoundCHN2,Camera,n\OBJ)
								EndIf
							ElseIf (Not n\Idle)
								If ChannelPlaying(n\SoundCHN) Then
									StopChannel(n\SoundCHN)
								EndIf
								If PlayerInReachableRoom(True) And InFacility=1 Then ;Player is in a room where SCP-049 can teleport to
									If Rand(1,3-SelectedDifficulty\otherFactors)=1 Then
										TeleportCloser(n)
										DebugLog "SCP-049 teleported closer due to distance"
									Else
										n\Idle = 60*70
										DebugLog "SCP-049 is now idle"
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 3 ;The player was killed by SCP-049
							;[Block]
							AnimateNPC(n, 537, 660, 0.7, False)
							
							;Animate2(n\obj, AnimTime(n\obj), 537, 660, 0.7, False)
							PositionEntity n\Collider, CurveValue(EntityX(Collider),EntityX(n\Collider),20.0),EntityY(n\Collider),CurveValue(EntityZ(Collider),EntityZ(n\Collider),20.0)
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(Collider)-180.0,EntityYaw(n\Collider),40), 0
							;[End Block]
						Case 4 ;Standing on catwalk in room4
							;[Block]
							If Dist < 8.0 Then
								AnimateNPC(n, 18, 19, 0.05)
								
								;Animate2(n\obj, AnimTime(n\obj), 18, 19, 0.05)
								PointEntity n\OBJ, Collider	
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 45.0), 0
								
								n\State3 = 1
							ElseIf Dist > HideDistance*0.8 And n\State3 > 0 Then
								n\State = 2
								n\State3 = 0
								For r.Rooms = Each Rooms
									If EntityDistance(r\obj,n\Collider)<4.0 Then
										TeleportEntity(n\Collider,EntityX(r\obj),0.1,EntityZ(r\obj),n\CollRadius,True)
										Exit
									EndIf
								Next
							EndIf
							;[End Block]
						Case 5 ;used for "room2sl"
							;[Block]
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
							PlayerSeeAble% = MeNPCSeesPlayer(n,True)
							If PlayerSeeAble% = True
								n\State = 2
								n\PathStatus = 0
								n\PathLocation = 0
								n\PathTimer = 0
								n\State3 = 0
								n\State2 = 70*2
								n\PrevState = 0
								PlaySound_Strict LoadTempSound("SFX\Room\Room2SL049Spawn.ogg")
							ElseIf PlayerSeeAble% = 2 And n\State3 > 0.0
								n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
							Else
								If n\State3 = 6.0
									If EntityDistance(n\Collider,Collider)>HideDistance
										n\State = 2
										n\PathStatus = 0
										n\PathLocation = 0
										n\PathTimer = 0
										n\State3 = 0
										n\PrevState = 0
									Else
										If n\PathStatus <> 1 Then n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
									EndIf
								EndIf
								
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null
										If n\PathLocation > 19 Then
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										PointEntity n\OBJ,n\Path[n\PathLocation]\obj
										RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),10.0),0
										MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
										
										;closes doors behind him
										If n\PathLocation>0 Then
											If n\Path[n\PathLocation-1] <> Null
												If n\Path[n\PathLocation-1]\door <> Null Then
													If n\Path[n\PathLocation-1]\door\KeyCard=0
														If EntityDistance(n\Path[n\PathLocation-1]\obj,n\Collider)>0.3
															If n\Path[n\PathLocation-1]\door\open Then UseDoor(n\Path[n\PathLocation-1]\door, False)
														EndIf
													EndIf
												EndIf
											EndIf
										EndIf
										
										;opens doors in front of him
										Dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If Dist2 < 0.6 Then
											If n\Path[n\PathLocation]\door <> Null Then
												If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
											EndIf
										EndIf
										
										If Dist2#<0.2
											n\PathLocation = n\PathLocation + 1
										EndIf
										
										AnimateNPC(n, Max(Min(AnimTime(n\OBJ),358.0),346), 393.0, n\CurrSpeed*38)
									EndIf
								Else
									Select n\PrevState
										Case 0
											AnimateNPC(n, 269, 345, 0.2)
										Case 1
											AnimateNPC(n, 661, 891, 0.4, False)
										Case 2
											AnimateNPC(n, 892, 1119, 0.4, False)
									End Select
								EndIf
							EndIf
							
							If PlayerRoom\RoomTemplate\Name = "room2sl" Then
								ShouldPlay = 20
							EndIf
							
							If n\CurrSpeed > 0.005 Then
								If (PrevFrame < 361 And n\Frame=>361) Or (PrevFrame < 377 And n\Frame=>377) Then
									PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))						
								ElseIf (PrevFrame < 431 And n\Frame=>431) Or (PrevFrame < 447 And n\Frame=>447)
									PlaySound2(StepSFX(3,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.8,1.0))
								EndIf
							EndIf
							
							If ChannelPlaying(n\SoundCHN2)
								UpdateSoundOrigin(n\SoundCHN2,Camera,n\OBJ)
							EndIf
							;[End Block]
					End Select
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider)-0.22, EntityZ(n\Collider))
				
				RotateEntity n\OBJ, 0, EntityYaw(n\Collider), 0
				
				n\LastSeen = Max(n\LastSeen-FPSfactor,0)
				
				n\State2 = Max(n\State2-FPSfactor,0)
				
				;[End Block]
			Case NPCtype049_2
				;[Block]
				; ~ n\State: Main State
				
				; ~ n\State2: A timer used for the player detection
				
				; ~ n\State3: A timer for making the NPC idle (if the player escapes during that time)
				
				If (Not n\IsDead) Then
					Dist = EntityDistance(n\Collider, Collider)
					
					PrevFrame = n\Frame
					
					n\BlinkTimer = 1.0
					
					PlayerSeeAble = MeNPCSeesPlayer(n)
					
					Select n\State
						Case 0.0 ; ~ Just lies
							;[Block]
							AnimateNPC(n, 719.0, 777.0, 0.2, False)
							
							If n\Frame = 777.0 Then
								If Rand(700) = 1 Then
									If EntityDistance(Collider, n\Collider) < 5.0 Then
										n\Frame = 719.0
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 1.0 ; ~ Stands up
							;[Block]
							If n\Frame >= 682.0 Then 
								AnimateNPC(n, 926.0, 935.0, 0.3, False)
								If n\Frame = 935.0 Then n\State = 2.0
							Else
								AnimateNPC(n, 155.0, 682.0, 1.1, False)
							EndIf
							;[End Block]
						Case 2.0 ; ~ Player is visible, tries to kill
							;[Block]
							If PlayerSeeAble = True Or n\State2 > 0.0 And (Not chs\Notarget) Then
								If PlayerSeeAble = True
									n\State2 = 70 * 2.0
								Else
									n\State2 = Max(n\State2 - FPSfactor, 0.0)
								EndIf
								PointEntity(n\OBJ, Collider)
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
								
								AnimateNPC(n, 936.0, 1017.0, n\CurrSpeed * 60.0)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								
								If Dist < 0.7
									If Abs(DeltaYaw(n\Collider, Collider)) =< 60.0
										n\State = 4.0
										If Rand(2) = 1 Then
											SetNPCFrame(n, 2.0)
										Else
											SetNPCFrame(n, 66.0)
										EndIf
									EndIf
								EndIf
								
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\PathLocation = 0
								n\State3 = 0.0
							Else
								n\State = 3.0
							EndIf
							
							; ~ Loop the walk sound
							If n\CurrSpeed > 0.005 Then
								If (PrevFrame < 970.0 And n\Frame >= 970.0) Or (PrevFrame < 1012.0 And n\Frame >= 1012.0) Then
									PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
								EndIf
							EndIf
							;[End Block]
						Case 3.0 ; ~ Player isn't visible, tries to find
							;[Block]
							If PlayerSeeAble = True And (Not chs\Notarget) Then
								n\State = 2.0
							EndIf
							
							If n\PathStatus = 1
								If n\Path[n\PathLocation] = Null Then 
									If n\PathLocation > 19 Then 
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
									
									AnimateNPC(n, 936.0, 1017.0, n\CurrSpeed * 60.0)
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
									
									; ~ Opens doors in front of him
									Dist2 = EntityDistance(n\Collider, n\Path[n\PathLocation]\OBJ)
									If Dist2 < 0.6 Then
										Temp = True
										If n\Path[n\PathLocation]\door <> Null
											If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
												If ((n\Path[n\PathLocation]\door\Locked Or n\Path[n\PathLocation]\door\KeyCard > 0 Or n\Path[n\PathLocation]\door\Code <> "") And n\Path[n\PathLocation]\door\Open = False) Then
													Temp = False
												Else
													If n\Path[n\PathLocation]\door\Open = False Then UseDoor(n\Path[n\PathLocation]\Door, False)
												EndIf
											EndIf
											If n\Path[n\PathLocation]\door\OpenState >= 180.0
												n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											Else
												n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
											EndIf
										Else
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										EndIf
										If Dist2 < 0.2 And Temp Then
											n\PathLocation = n\PathLocation + 1
										ElseIf Dist2 < 0.5 And (Not Temp)
											n\PathStatus = 0
											n\PathTimer = 0.0
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									EndIf
								EndIf
							Else
								; ~ No path, stands still
								If n\CurrSpeed =< 0.001 Then
									AnimateNPC(n, 778.0, 926.0, 0.1)
									n\CurrSpeed = 0.0
									
									n\PathTimer = n\PathTimer - FPSfactor
									If n\PathTimer < 70 * 5.0
										n\PathTimer = n\PathTimer + Rnd(1.0, 2.0 + (2.0 * SelectedDifficulty\AggressiveNPCs)) * FPSfactor
									Else
										n\EnemyX = 0.0
										n\EnemyY = 0.0
										n\EnemyZ = 0.0
										
										; ~ When stands still, tries to find a room that is close
										For r.Rooms = Each Rooms
											If EntityDistance(r\OBJ, n\Collider) < 14.0 And EntityDistance(r\OBJ, n\Collider) > 6.0 Then
												n\EnemyX = EntityX(r\OBJ)
												n\EnemyY = EntityY(r\OBJ)
												n\EnemyZ = EntityZ(r\OBJ)
												Exit
											EndIf
										Next
										
										; ~ The room wasn't found, tries to get the coordinates from a random waypoint
										If n\EnemyX = 0.0 And n\EnemyY = 0.0 And n\EnemyZ = 0.0 Then
											For w.WayPoints = Each WayPoints
												If EntityDistance(w\OBJ, n\Collider) < 10.0 And EntityDistance(w\OBJ, n\Collider) > 4.0 Then
													n\EnemyX = EntityX(w\OBJ)
													n\EnemyY = EntityY(w\OBJ)
													n\EnemyZ = EntityZ(w\OBJ)
												EndIf
											Next
										EndIf
										
										; ~ Just tries to find a path, if possible
										If n\EnemyX <> 0.0 Or n\EnemyY <> 0.0 Or n\EnemyZ <> 0.0
											n\PathStatus = FindPath(n, n\EnemyX, n\EnemyY, n\EnemyZ)
										EndIf
										n\PathTimer = 0.0
									EndIf
								Else
									n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 20.0)
									AnimateNPC(n, 936.0, 1017.0, n\CurrSpeed * 60.0)
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								EndIf
							EndIf
							
							If Dist > HideDistance Then
								If n\State3 < 70 * (15.0 + (10.0 * SelectedDifficulty\AggressiveNPCs))
									n\State3 = n\State3 + FPSfactor
								Else
									n\State3 = 70 * (6.0 * 60.0)
									n\State = 5.0
								EndIf
							EndIf
							
							; ~ Loop the walk sound
							If n\CurrSpeed > 0.005 Then
								If (PrevFrame < 970.0 And n\Frame >= 970.0) Or (PrevFrame < 1012.0 And n\Frame >= 1012.0) Then
									PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
								EndIf
							EndIf
							;[End Block]
						Case 4.0 ; ~ Attacks
							;[Block]
							If n\Frame < 66.0 Then
								AnimateNPC(n, 2.0, 65.0, 0.7, False)
								If n\Frame >= 23.0 And PrevFrame < 23.0 Then
									If Dist < 1.1 Then
										If Abs(DeltaYaw(n\Collider, Collider)) =< 60.0
											PlaySound2(DamageSFX(Rand(5, 8)), Camera, n\Collider)
											Injuries = Injuries + Rnd(0.4, 1.0)
											
											If Injuries > 3.0
												DeathMsg = "Subject D-9341. Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
												Kill()
											EndIf
										Else
											PlaySound2(MissSFX, Camera, n\Collider)
										EndIf
									Else
										PlaySound2(MissSFX, Camera, n\Collider, 2.35)
									EndIf
								ElseIf n\Frame >= 64.0
									n\State = 2.0
								EndIf
							Else
								AnimateNPC(n, 66.0, 132.0, 0.7, False)
								If n\Frame >= 90.0 And PrevFrame < 90.0 Then
									If Dist < 1.1 Then
										If Abs(DeltaYaw(n\Collider, Collider)) =< 60.0
											PlaySound2(DamageSFX(Rand(5, 8)), Camera, n\Collider)
											Injuries = Injuries + Rnd(0.4, 1.0)
											
											If Injuries > 3.0
												DeathMsg = "Subject D-9341. Cause of death: multiple lacerations and severe blunt force trauma caused by an instance of SCP-049-2."
												Kill()
											EndIf
										Else
											PlaySound2(MissSFX, Camera, n\Collider)
										EndIf
									Else
										PlaySound2(MissSFX, Camera, n\Collider, 2.35)
									EndIf
								ElseIf n\Frame >= 131.0
									n\State = 2.0
								EndIf
							EndIf
							;[End Block]
						Case 5.0 ; ~ Idles, if player escaped from the NPC
							;[Block]
							HideEntity(n\OBJ)
							HideEntity(n\Collider)
							n\DropSpeed = 0.0
							PositionEntity(n\Collider, 0.0, 500.0, 0.0)
							ResetEntity(n\Collider)
							If n\Idle > 0.0
								n\Idle = Max(n\Idle - (1 + (1 * SelectedDifficulty\AggressiveNPCs)) * FPSfactor, 0.0)
							Else
								If PlayerInReachableRoom() ; ~ Player is in a room where NPC can teleport to
									If Rand(50 - (20 * SelectedDifficulty\AggressiveNPCs)) = 1
										ShowEntity(n\Collider)
										ShowEntity(n\OBJ)
										For w.waypoints = Each WayPoints
											If w\door = Null And w\room\Dist < HideDistance And Rand(3) = 1 Then
												If EntityDistance(w\room\OBJ, n\Collider) < EntityDistance(Collider, n\Collider)
													x = Abs(EntityX(n\Collider) - EntityX(w\OBJ, True))
													If x < 12.0 And x > 4.0 Then
														z = Abs(EntityZ(n\Collider) - EntityZ(w\OBJ, True))
														If z < 12.0 And z > 4.0 Then
															If w\room\Dist > 4.0
																PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 0.25, EntityZ(w\OBJ, True))
																ResetEntity(n\Collider)
																n\PathStatus = 0
																n\PathTimer# = 0.0
																n\PathLocation = 0
																Exit
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
										Next
										n\State = 2.0
										n\State3 = 0.0
									EndIf
								EndIf
							EndIf
							;[End Block]
					End Select
				Else
					; ~ The NPC was killed
					If n\SoundCHN <> 0 Then
						If ChannelPlaying(n\SoundCHN) = True Then StopChannel(n\SoundCHN)
						If n\Sound <> 0 Then
							FreeSound_Strict(n\Sound) : n\Sound = 0
						EndIf
					EndIf
					AnimateNPC(n, 1035.0, 1072.0, 0.3, False)
				EndIf
				
				; ~ Loop the breath sound
				If n\State > 1.0 And n\State < 5.0 Then
					n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				RotateEntity(n\OBJ, -90.0, EntityYaw(n\Collider), 0.0)
				;[End Block]
			Case NPCtypeGuard
				;[Block]
				PrevFrame = n\Frame
				
				n\BoneToManipulate = ""
				n\ManipulateBone = False
				n\ManipulationType = 0
				n\NPCNameInSection = "Guard"
				
				Select n\State
					Case 1.0 ; ~ Aims and shoots at the player
						;[Block]
						If n\Frame < 39.0 Or (n\Frame > 76.0 And n\Frame < 245.0) Or (n\Frame > 248.0 And n\Frame < 302.0) Or n\Frame > 344.0 Then
							AnimateNPC(n, 345.0, 357.0, 0.2, False)
							If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						EndIf
						
						If KillTimer >= 0.0 Then
							Dist = EntityDistance(n\Collider, Collider)
							
							Local ShootAccuracy# = 0.4 + 0.5 * SelectedDifficulty\AggressiveNPCs
							Local DetectDistance# = 11.0
							
							; ~ If at Gate B increase his distance so that he can shoot the player from a distance after they are spotted.
							If PlayerRoom\RoomTemplate\Name = "exit1" Then
								DetectDistance = 21.0
								ShootAccuracy = 0.0
								If Rand(1, 8 - SelectedDifficulty\AggressiveNPCs * 4) < 2 Then ShootAccuracy = 0.03
								
								; ~ Increase accuracy if the player is going slow
								ShootAccuracy = ShootAccuracy + (0.5 - CurrSpeed * 20.0)
							EndIf
							
							If Dist < DetectDistance Then
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(Pvt, Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
								
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
								PointEntity(Pvt, Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
								
								If n\Reload = 0.0
									EntityPick(Pvt, Dist)
									If PickedEntity() = Collider Or n\State3 = 1 Then
										Local InstaKillPlayer% = False
										
										If PlayerRoom\RoomTemplate\Name = "start" Then 
											DeathMSG = "Subject D-9341. Cause of death: Gunshot wound to the head. The surveillance tapes confirm that the subject was terminated by Agent Ulgrin shortly after the site lockdown was initiated."
											InstaKillPlayer = True
										ElseIf PlayerRoom\RoomTemplate\Name = "exit1" Then
											DeathMSG = Chr(34) + "Agent G. to control. Eliminated a Class D escapee in Gate B's courtyard." + Chr(34)
										Else
											DeathMSG = ""
										EndIf
										
										PlaySound2(GunshotSFX, Camera, n\Collider, 35.0)
										
										RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
										PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
										MoveEntity (Pvt,0.8 * 0.079, 10.75 * 0.079, 6.9 * 0.079)
										
										PointEntity(Pvt, Collider)
										Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ShootAccuracy, False, InstaKillPlayer)
										n\Reload = 7.0
									Else
										n\CurrSpeed = n\Speed
									End If
								EndIf
								
								If n\Reload > 0.0 And n\Reload =< 7.0
									AnimateNPC(n, 245, 248, 0.35, True)
								Else
									If n\Frame < 302.0
										AnimateNPC(n, 302.0, 344.0, 0.35, True)
									EndIf
								EndIf
								
								FreeEntity(Pvt)
							Else
								AnimateNPC(n, 302.0, 344.0, 0.35, True)
							EndIf
							
							n\ManipulateBone = True
							
							If n\State2 = 10.0 Then ; ~ Hacky way of applying spine pitch to specific guards.
								n\BoneToManipulate = "Chest"
								n\ManipulationType = 3
							Else
								n\BoneToManipulate = "Chest"
								n\ManipulationType = 0
							EndIf
						Else
							n\State = 0.0
						EndIf
						;[End Block]
					Case 2.0 ; ~ Shoots
						;[Block]
						AnimateNPC(n, 245.0, 248.0, 0.35, True)
						If n\Reload = 0.0
							PlaySound2(GunshotSFX, Camera, n\Collider, 20.0)
							p.Particles = CreateParticle(EntityX(n\OBJ, True), EntityY(n\OBJ, True), EntityZ(n\OBJ, True), 1, 0.2, 0.0, 5.0)
							PositionEntity(p\Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							RotateEntity(p\Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
							MoveEntity(p\Pvt, 0.8 * 0.079, 10.75 * 0.079, 6.9 * 0.079)
							n\Reload = 7.0
						End If
						;[End Block]
					Case 3.0 ; ~ Follows a path
						;[Block]
						If n\PathStatus = 2 Then
							n\State = 0.0
							n\CurrSpeed = 0.0
						ElseIf n\PathStatus = 1
							If n\Path[n\PathLocation] = Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
								
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
								
								AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
								
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
								
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\OBJ) < 0.2 Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						Else
							n\CurrSpeed = 0
							n\State = 4
						EndIf
						;[End Block]
					Case 4.0
						;[Block]
						AnimateNPC(n, 77.0, 201.0, 0.2)
						
						If Rand(400) = 1 Then n\Angle = Rnd(-180.0, 180.0)
						
						RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + Sin(MilliSecs2() / 50) * 2.0, EntityYaw(n\Collider), 150.0), 0.0, True)
						
						Dist = EntityDistance(n\Collider, Collider)
						If Dist < 15.0 Then
							If WrapAngle(EntityYaw(n\Collider) - DeltaYaw(n\Collider, Collider)) < 90.0 Then
								If EntityVisible(Pvt, Collider) Then n\State = 1.0
							EndIf
						EndIf
						;[End Block]
					Case 5.0 ; ~ Following a target
						;[Block]
						RotateEntity n\Collider, 0, CurveAngle(VectorYaw(n\EnemyX-EntityX(n\Collider), 0, n\EnemyZ-EntityZ(n\Collider))+n\Angle, EntityYaw(n\Collider), 20.0), 0
						
						Dist# = Distance(EntityX(n\Collider),EntityZ(n\Collider),n\EnemyX,n\EnemyZ)
						
						AnimateNPC(n,1,38,n\CurrSpeed*40)
						
						If Dist > 2.0 Or Dist < 1.0  Then
							n\CurrSpeed = CurveValue(n\Speed*Sgn(Dist-1.5)*0.75, n\CurrSpeed, 10.0)
						Else
							n\CurrSpeed = CurveValue(0, n\CurrSpeed, 10.0)
						EndIf
						
						MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
						;[End Block]
					Case 7.0 ; ~ Just walking
						;[Block]
						If n\UseHeadphones = True Then
						    AnimateNPC(n, 623.0, 747.0, 0.2)
						Else
						    AnimateNPC(n, 77.0, 201.0, 0.2)
						EndIf
						;[End Block]
					Case 8.0 ; ~ Idles
						;[Block]
						;[End Block]
					Case 9.0 ; ~ Looks at the player
						;[Block]
						If n\UseHeadphones = True Then
						    AnimateNPC(n, 623.0, 747.0, 0.2)
						Else
						    AnimateNPC(n, 77.0, 201.0, 0.2)
						EndIf
						n\BoneToManipulate = "head"
						n\ManipulateBone = True
						n\ManipulationType = 0
						n\Angle = EntityYaw(n\Collider)
						;[End Block]
					Case 10.0
						;[Block]
						AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
						
						n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
						
						MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
						;[End Block]
					Case 11.0 ; ~ Trying to find the player and kill
						;[Block]
						If n\UseHeadphones = True Then
							If n\Frame < 787.0 Or (n\Frame > 824.0 And n\Frame < 867.0) Or (n\Frame > 870.0 And n\Frame < 884.0) Or n\Frame > 939.0
							    AnimateNPC(n, 927.0, 939.0, 0.2, False)
							    If n\Frame >= 883.0 Then SetNPCFrame(n, 884.0)
						    EndIf
						Else
						    If n\Frame < 39.0 Or (n\Frame > 76.0 And n\Frame < 245.0) Or (n\Frame > 248.0 And n\Frame < 302.0) Or n\Frame > 344.0
							    AnimateNPC(n, 345.0, 357.0, 0.2, False)
							    If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						    EndIf
						EndIf
						
						If KillTimer >= 0.0 Then
							Dist = EntityDistance(n\Collider, Collider)
							
							Local SearchPlayer% = False
							
							If Dist < 11.0
								If EntityVisible(n\Collider,Collider)
									SearchPlayer = True
								EndIf
							EndIf
							
							If SearchPlayer
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(Pvt, Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
								
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
								PointEntity(Pvt, Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
								
								If n\Reload = 0.0
									EntityPick(Pvt, Dist)
									If PickedEntity() = Collider Or n\State3 = 1.0 Then
										InstaKillPlayer% = False
										
										DeathMSG = ""
										
										PlaySound2(GunshotSFX, Camera, n\Collider, 35.0)
										
										RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
										PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
										MoveEntity(Pvt, 0.8 * 0.079, 10.75 * 0.079, 6.9 * 0.079)
										
										PointEntity(Pvt, Collider)
										Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 1.0, False, InstaKillPlayer)
										n\Reload = 7
									Else
										n\CurrSpeed = n\Speed
									End If
								EndIf
								
								If n\Reload > 0.0 And n\Reload =< 7.0
								    If n\UseHeadphones = True Then
								        AnimateNPC(n, 867.0, 870.0, 0.35, True)
								    Else
									    AnimateNPC(n, 245.0, 248.0, 0.35, True)
									EndIf
								Else
								    If n\UseHeadphones = True Then
								        If n\Frame < 884.0
										    AnimateNPC(n, 884.0, 926.0, 0.35, True)
									    EndIf
									Else
									    If n\Frame < 302.0
									        AnimateNPC(n, 302.0, 344.0, 0.35, True)
									    EndIf
									EndIf
								EndIf
								
								FreeEntity(Pvt)
							Else
								If n\PathStatus = 1
									If n\Path[n\PathLocation] = Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										If n\UseHeadphones = True Then
									        AnimateNPC(n, 787.0, 823.0, n\CurrSpeed * 40.0)
									    Else
										    AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40.0)
										EndIf
										n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
										
										PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
										
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
										
										If EntityDistance(n\Collider,n\Path[n\PathLocation]\OBJ) < 0.2 Then
											n\PathLocation = n\PathLocation + 1
										EndIf
									EndIf
								Else
									If n\PathTimer = 0.0 Then n\PathStatus = FindPath(n, EntityX(Collider), EntityY(Collider) + 0.5, EntityZ(Collider))
									
									Local wayPointCloseToPlayer.WayPoints
									
									wayPointCloseToPlayer = Null
									
									For wp.WayPoints = Each WayPoints
										If EntityDistance(wp\OBJ, Collider) < 2.0
											wayPointCloseToPlayer = wp
											Exit
										EndIf
									Next
									
									If wayPointCloseToPlayer <> Null
										n\PathTimer = 1.0
										If EntityVisible(wayPointCloseToPlayer\OBJ, n\Collider)
											If Abs(DeltaYaw(n\Collider,wayPointCloseToPlayer\OBJ)) > 0
												PointEntity(n\OBJ, wayPointCloseToPlayer\OBJ)
												RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
											EndIf
										EndIf
									Else
										n\PathTimer = 0.0
									EndIf
									
									If n\PathTimer = 1.0
										If n\UseHeadphones = True Then
									        AnimateNPC(n, 787.0, 823.0, n\CurrSpeed * 40.0)
									    Else
										    AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40.0)
										EndIf
										n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
									EndIf
								EndIf
								
								If PrevFrame < 43.0 And n\Frame >= 43.0 Then
									PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))						
								ElseIf PrevFrame < 61.0 And n\Frame >= 61.0
									PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
								EndIf
							EndIf
						Else
							n\State = 0.0
						EndIf
						;[End Block]
					Case 12.0
						;[Block]
						If n\Frame < 39.0 Or (n\Frame > 76.0 And n\Frame < 245.0) Or (n\Frame > 248.0 And n\Frame < 302.0) Or n\Frame > 344.0 Then
							AnimateNPC(n, 345.0, 357.0, 0.2, False)
							If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						EndIf
						If n\Frame < 345.0
							AnimateNPC(n, 302.0, 344.0, 0.35, True)
						EndIf
						
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(Pvt, Collider)
						Else
							RotateEntity(Pvt, 0.0, n\Angle, 0.0)
						EndIf
						RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
						
						RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
						
						PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(Pvt, Collider)
							n\ManipulateBone = True
							n\BoneToManipulate = "Chest"
							n\ManipulationType = 0
						Else
							RotateEntity(Pvt, 0.0, n\Angle, 0.0)
						EndIf
						RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
						
						FreeEntity(Pvt)
						
						UpdateSoundOrigin(n\SoundCHN, Camera, n\Collider, 20.0)
						;[End Block]
					Case 13.0
						;[Block]
						AnimateNPC(n, 202, 244, 0.35, True)
						;[End Block]
					Case 14.0
						;[Block]
						If n\PathStatus = 2 Then
							n\State = 13.0
							n\CurrSpeed = 0.0
						ElseIf n\PathStatus = 1
							If n\Path[n\PathLocation] = Null Then 
								If n\PathLocation > 19 Then 
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
								
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
								
								AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40)
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
								
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\OBJ) < 0.2 Then
									n\PathLocation = n\PathLocation + 1
								EndIf 
							EndIf
						Else
							n\CurrSpeed = 0.0
							n\State = 13.0
						EndIf
						
						If PrevFrame < 43.0 And n\Frame >= 43.0 Then
							PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))						
						ElseIf PrevFrame < 61.0 And n\Frame >= 61.0
							PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
						EndIf
						;[End Block]
					Default
						;[Block]
						If Rand(400) = 1 Then n\PrevState = Rand(-30, 30)
						n\PathStatus = 0
						AnimateNPC(n, 77.0, 201.0, 0.2)
						
						RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + n\PrevState + Sin(MilliSecs2() / 50) * 2.0, EntityYaw(n\Collider), 50.0), 0.0, True)
						;[End Block]
				End Select
				
				If n\CurrSpeed > 0.01 Then
					If PrevFrame < 5.0 And n\Frame >= 5.0
						PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))						
					ElseIf PrevFrame < 23.0 And n\Frame >= 23.0
						PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.5, 0.7))						
					EndIf
				EndIf
				
				If n\Frame > 286.5 And n\Frame < 288.5
					n\IsDead = True
				EndIf
				
				n\Reload = Max(0.0, n\Reload - FPSfactor)
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) + 180.0, 0.0)
				;[End Block]
			Case NPCtypeMTF
				;[Block]
				UpdateMTFUnit(n)
				;[End Block]
			Case NPCtypeD, NPCtypeClerk
				;[Block]
				RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				
				PrevFrame = AnimTime(n\OBJ)
				
				Select n\State
					Case 0.0 ; ~ Idles
						;[Block]
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						Animate2(n\OBJ, AnimTime(n\OBJ), 210.0, 235.0, 0.1)
						;[End Block]
					Case 1.0 ; ~ Walking
						If n\State2 = 1.0
							n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
						Else
							n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
						EndIf
						Animate2(n\OBJ, AnimTime(n\OBJ), 236.0, 260.0, n\CurrSpeed * 18.0)
						;[End Block]
					Case 2 ; ~ Running
						;[Block]
						n\CurrSpeed = CurveValue(0.03, n\CurrSpeed, 5.0)
						Animate2(n\OBJ, AnimTime(n\OBJ), 301.0, 319.0, n\CurrSpeed * 18.0)
						;[End Block]
				End Select
				
				If n\State2 <> 2.0
					If n\State = 1.0
						If n\CurrSpeed > 0.01 Then
							If PrevFrame < 244.0 And AnimTime(n\OBJ) >= 244.0 Then
								PlaySound2(StepSFX(GetStepSound(n\Collider), 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))						
							ElseIf PrevFrame < 256.0 And AnimTime(n\OBJ) >= 256.0
								PlaySound2(StepSFX(GetStepSound(n\Collider), 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
						EndIf
					ElseIf n\State = 2.0
						If n\CurrSpeed > 0.01 Then
							If PrevFrame < 309.0 And AnimTime(n\OBJ) >= 309.0
								PlaySound2(StepSFX(GetStepSound(n\Collider), 1, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							ElseIf PrevFrame =< 319.0 And AnimTime(n\OBJ) =< 301.0
								PlaySound2(StepSFX(GetStepSound(n\Collider), 1, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
						EndIf
					EndIf
				EndIf
				
				If n\Frame = 19.0 Or n\Frame = 60.0
					n\IsDead = True
				EndIf
				If AnimTime(n\OBJ) = 19.0 Or AnimTime(n\OBJ) = 60.0
					n\IsDead = True
				EndIf
				
				MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
				
				RotateEntity(n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider) - 180.0, 0.0)
				;[End Block]
			Case NPCtype513_1
				;[Block]
				If PlayerRoom\RoomTemplate\Name <> "pocketdimension" Then 
					If n\Idle Then
						HideEntity(n\OBJ)
						HideEntity(n\OBJ2)
						If Rand(200) = 1 Then
							For w.WayPoints = Each WayPoints
								If w\room <> PlayerRoom Then
									x = Abs(EntityX(Collider) - EntityX(w\OBJ, True))
									If x > 3.0 And x < 9.0 Then
										z = Abs(EntityZ(Collider) - EntityZ(w\OBJ, True))
										If z > 3.0 And z < 9.0 Then
											PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True), EntityZ(w\OBJ, True))
											PositionEntity(n\OBJ, EntityX(w\OBJ, True), EntityY(w\OBJ, True), EntityZ(w\OBJ, True))
											ResetEntity(n\Collider)
											ShowEntity(n\OBJ)
											ShowEntity(n\OBJ2)
											
											n\LastSeen = 0.0
											
											n\Path[0] = w
											
											n\Idle = False
											n\State2 = 70 * Rnd(15.0, 20.0)
											n\State = Max(Rand(-1.0, 2.0), 0.0)
											n\PrevState = Rand(0, 1)
											Exit
										EndIf
									EndIf
								EndIf
							Next
						End If
					Else
						Dist = EntityDistance(Collider, n\Collider)
						
						; ~ Use the prev-values to do a "twitching" effect
						n\PrevX = CurveValue(0.0, n\PrevX, 10.0)
						n\PrevZ = CurveValue(0.0, n\PrevZ, 10.0)
						
						If Rand(100) = 1 Then
							If Rand(5) = 1 Then
								n\PrevX = (EntityX(Collider) - EntityX(n\Collider)) * 0.9
								n\PrevZ = (EntityZ(Collider) - EntityZ(n\Collider)) * 0.9
							Else
								n\PrevX = Rnd(0.1, 0.5)
								n\PrevZ = Rnd(0.1, 0.5)						
							EndIf
						EndIf
						
						Temp = Rnd(-1.0, 1.0)
						PositionEntity(n\OBJ2, EntityX(n\Collider) + n\PrevX * Temp, EntityY(n\Collider) - 0.2 + Sin((MilliSecs2() / 8 - 45) Mod 360) * 0.05, EntityZ(n\Collider) + n\PrevZ * Temp)
						RotateEntity(n\OBJ2, 0.0, EntityYaw(n\OBJ), 0.0)
						If Floor(AnimTime(n\OBJ2)) <> Floor(n\Frame) Then SetAnimTime(n\OBJ2, n\Frame)
						
						If n\State = 0.0 Then
							If n\PrevState = 0
								AnimateNPC(n, 2.0, 74.0, 0.2)
							Else
								AnimateNPC(n, 75.0, 124.0, 0.2)
							EndIf
							
							If n\LastSeen Then 	
								PointEntity(n\OBJ2, Collider)
								RotateEntity(n\OBJ, 0.0, CurveAngle(EntityYaw(n\OBJ2), EntityYaw(n\OBJ), 40.0), 0.0)
								If Dist < 4.0 Then n\State = Rand(1.0, 2.0)
							Else
								If Dist < 6.0 And Rand(5) = 1 Then
									If EntityInView(n\Collider, Camera) Then
										If EntityVisible(Collider, n\Collider) Then
											n\LastSeen = 1.0
											PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell" + Rand(2, 3) + ".ogg"))
										EndIf
									EndIf
								EndIf								
							EndIf
						Else
							If n\Path[0] = Null Then
								; ~ Move towards a waypoint that is:
								; ~ 1. max 8 units away from SCP-513-1
								; ~ 2. further away from the player than SCP-513-1's current position 
								For w.WayPoints = Each WayPoints
									x = Abs(EntityX(n\Collider, True) - EntityX(w\OBJ, True))
									If x < 8.0 And x > 1.0 Then
										z = Abs(EntityZ(n\Collider, True) - EntityZ(w\OBJ, True))
										If z < 8.0 And z > 1.0 Then
											If EntityDistance(Collider, w\OBJ) > Dist Then
												n\Path[0] = w
												Exit
											EndIf
										EndIf
									EndIf
								Next
								
								; ~ SCP-513-1 simply disappears
								If n\Path[0] = Null Then
									n\Idle = True
									n\State2 = 0.0
								EndIf
							Else
								If EntityDistance(n\Collider, n\Path[0]\OBJ) > 1.0 Then
									PointEntity(n\OBJ, n\Path[0]\OBJ)
									RotateEntity(n\Collider, CurveAngle(EntityPitch(n\OBJ), EntityPitch(n\Collider), 15.0), CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 15.0), 0.0, True)
									n\CurrSpeed = CurveValue(0.05 * Max((7.0 - Dist) / 7.0, 0.0), n\CurrSpeed, 15.0)
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
									If Rand(200) = 1 Then MoveEntity(n\Collider, 0.0, 0.0, 0.5)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
								Else
									For i = 0 To 4
										If n\Path[0]\connected[i] <> Null Then
											If EntityDistance(Collider, n\Path[0]\connected[i]\OBJ) > Dist Then
												
												If n\LastSeen = 0 Then 
													If EntityInView(n\Collider, Camera) Then
														If EntityVisible(Collider, n\Collider) Then
															n\LastSeen = 1.0
															PlaySound_Strict(LoadTempSound("SFX\SCP\513\Bell" + Rand(2, 3) + ".ogg"))
														EndIf
													EndIf
												EndIf
												n\Path[0] = n\Path[0]\connected[i]
												Exit
											EndIf
										EndIf
									Next
									If n\Path[0] = Null Then n\State2 = 0.0
								EndIf
							EndIf
						EndIf
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2 + Sin((MilliSecs2() / 8) Mod 360) * 0.1, EntityZ(n\Collider))
						
						Select n\State 
							Case 1.0
								;[Block]
								If n\PrevState = 0
									AnimateNPC(n, 125.0, 194.0, n\CurrSpeed * 20.0)
								Else
									AnimateNPC(n, 195.0, 264.0, n\CurrSpeed * 20.0)
								EndIf
								RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider), 0.0) 
								;[End Block]
							Case 2.0
								;[Block]
								If n\PrevState = 0
									AnimateNPC(n, 2.0, 74.0, 0.2)
								Else
									AnimateNPC(n, 75.0, 124.0, 0.2)
								EndIf
								RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider), 0.0)
								;[End Block]
						End Select
						
						If n\State2 > 0.0 Then
							If Dist < 4.0 Then n\State2 = n\State2 - FPSfactor * 4.0
							n\State2 = n\State2 - FPSfactor
						Else
							n\Path[0] = Null
							n\Idle = True
							n\State2 = 0.0
						EndIf
					End If
				EndIf
				
				n\DropSpeed = 0.0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtype372 ;------------------------------------------------------------------------------------------------------------------
				;[Block]
				RN$ = PlayerRoom\RoomTemplate\Name
				If RN$ <> "pocketdimension" And RN$ <> "dimension1499" Then 
					If n\Idle Then
						HideEntity(n\OBJ)
						If Rand(50) = 1 And (BlinkTimer < -5 And BlinkTimer > -15) Then
							ShowEntity(n\OBJ)
							Angle# = EntityYaw(Collider)+Rnd(-90,90)
							
							Dist = Rnd(1.5, 2.0)
							PositionEntity(n\Collider, EntityX(Collider) + Sin(Angle) * Dist, EntityY(Collider)+0.2, EntityZ(Collider) + Cos(Angle) * Dist)
							n\Idle = False
							n\State = Rand(20, 60)
							
							If Rand(300)=1 Then PlaySound2(RustleSFX(Rand(0,2)),Camera, n\OBJ, 8, Rnd(0.0,0.2))
						End If
					Else
						PositionEntity(n\OBJ, EntityX(n\Collider) + Rnd(-0.005, 0.005), EntityY(n\Collider)+0.3+0.1*Sin(MilliSecs2()/2), EntityZ(n\Collider) + Rnd(-0.005, 0.005))
						RotateEntity n\OBJ, 0, EntityYaw(n\Collider), ((MilliSecs2()/5) Mod 360)
						
						AnimateNPC(n, 32, 113, 0.4)
						;Animate2(n\obj, AnimTime(n\obj), 32, 113, 0.4)
						
						If EntityInView(n\OBJ, Camera) Then
							GiveAchievement(Achv372)
							
							If Rand(30)=1 Then 
								If (Not ChannelPlaying(n\SoundCHN)) Then
									If EntityVisible(Camera, n\OBJ) Then 
										n\SoundCHN = PlaySound2(RustleSFX(Rand(0,2)),Camera, n\OBJ, 8, 0.3)
									EndIf
								EndIf
							EndIf
							
							Temp = CreatePivot()
							PositionEntity Temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
							PointEntity Temp, n\Collider
							
							Angle =  WrapAngle(EntityYaw(Collider)-EntityYaw(Temp))
							If Angle < 180 Then
								RotateEntity n\Collider, 0, EntityYaw(Collider)-80, 0		
							Else
								RotateEntity n\Collider, 0, EntityYaw(Collider)+80, 0
							EndIf
							FreeEntity Temp
							
							MoveEntity n\Collider, 0, 0, 0.03*FPSfactor
							
							n\State = n\State-FPSfactor
						EndIf
						n\State=n\State-(FPSfactor/80.0)
						If n\State <= 0 Then n\Idle = True	
					End If
					
				EndIf
				
				n\DropSpeed = 0
				ResetEntity(n\Collider)						
				;[End Block]
			Case NPCtypeApache ;------------------------------------------------------------------------------------------------------------------
				;[Block]
				Dist = EntityDistance(Collider, n\Collider)
				If Dist<60.0 Then 
					If PlayerRoom\RoomTemplate\Name = "exit1" Then 
						Dist2 = Max(Min(EntityDistance(n\Collider, PlayerRoom\Objects[3])/(8000.0*RoomScale),1.0),0.0)
					Else 
						Dist2 = 1.0
					EndIf
					
					n\SoundCHN = LoopSound2(ApacheSFX, n\SoundCHN, Camera, n\Collider, 25.0, Dist2)
				EndIf
				
				n\DropSpeed = 0
				
				Select n\State
					Case 0,1
						TurnEntity(n\OBJ2,0,20.0*FPSfactor,0)
						TurnEntity(n\OBJ3,20.0*FPSfactor,0,0)
						
						If n\State=1 And (Not chs\NoTarget) Then
							If Abs(EntityX(Collider)-EntityX(n\Collider))< 30.0 Then
								If Abs(EntityZ(Collider)-EntityZ(n\Collider))<30.0 Then
									If Abs(EntityY(Collider)-EntityY(n\Collider))<20.0 Then
										If Rand(20)=1 Then 
											If EntityVisible(Collider, n\Collider) Then
												n\State = 2
												PlaySound2(AlarmSFX(2), Camera, n\Collider, 50, 1.0)
											EndIf
										EndIf									
									EndIf
								EndIf
							EndIf							
						EndIf
					Case 2,3 ;player located -> attack
						
						If n\State = 2 Then 
							Target = Collider
						ElseIf n\State = 3
							Target=CreatePivot()
							PositionEntity Target, n\EnemyX, n\EnemyY, n\EnemyZ, True
						EndIf
						
						If chs\NoTarget And n\State = 2 Then n\State = 1
						
						TurnEntity(n\OBJ2,0,20.0*FPSfactor,0)
						TurnEntity(n\OBJ3,20.0*FPSfactor,0,0)
						
						If Abs(EntityX(Target)-EntityX(n\Collider)) < 55.0 Then
							If Abs(EntityZ(Target)-EntityZ(n\Collider)) < 55.0 Then
								If Abs(EntityY(Target)-EntityY(n\Collider))< 20.0 Then
									PointEntity n\OBJ, Target
									RotateEntity n\Collider, CurveAngle(Min(WrapAngle(EntityPitch(n\OBJ)),40.0),EntityPitch(n\Collider),40.0), CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),90.0), EntityRoll(n\Collider), True
									PositionEntity(n\Collider, EntityX(n\Collider), CurveValue(EntityY(Target)+8.0,EntityY(n\Collider),70.0), EntityZ(n\Collider))
									
									Dist# = Distance(EntityX(Target),EntityZ(Target),EntityX(n\Collider),EntityZ(n\Collider))
									
									n\CurrSpeed = CurveValue(Min(Dist-6.5,6.5)*0.008, n\CurrSpeed, 50.0)
									
									;If Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\collider),EntityZ(n\collider)) > 6.5 Then
									;	n\currspeed = CurveValue(0.08,n\currspeed,50.0)
									;Else
									;	n\currspeed = CurveValue(0.0,n\currspeed,30.0)
									;EndIf
									MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
									
									
									If n\PathTimer = 0 Then
										n\PathStatus = EntityVisible(n\Collider,Target)
										n\PathTimer = Rand(100,200)
									Else
										n\PathTimer = Min(n\PathTimer-FPSfactor,0.0)
									EndIf
									
									If n\PathStatus = 1 Then ;player visible
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(0, EntityRoll(n\Collider),40), True
										
										If n\Reload =< 0 Then
											If Dist<20.0 Then
												Pvt = CreatePivot()
												
												PositionEntity Pvt, EntityX(n\Collider),EntityY(n\Collider), EntityZ(n\Collider)
												RotateEntity Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),EntityRoll(n\Collider)
												MoveEntity Pvt, 0, 8.87*(0.21/9.0), 8.87*(1.7/9.0) ;2.3
												PointEntity Pvt, Target
												
												If WrapAngle(EntityYaw(Pvt)-EntityYaw(n\Collider))<10 Then
													PlaySound2(Gunshot2SFX, Camera, n\Collider, 20)
													
													If PlayerRoom\RoomTemplate\Name = "exit1" Then
														DeathMSG = Chr(34)+"CH-2 to control. Shot down a runaway Class D at Gate B."+Chr(34)
													Else
														DeathMSG = Chr(34)+"CH-2 to control. Shot down a runaway Class D at Gate A."+Chr(34)
													EndIf
													
													Shoot( EntityX(Pvt),EntityY(Pvt), EntityZ(Pvt),((10/Dist)*(1/Dist))*(n\State=2),(n\State=2))
													
													n\Reload = 5
												EndIf
												
												FreeEntity Pvt
											EndIf
										EndIf
									Else 
										RotateEntity n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(-20, EntityRoll(n\Collider),40), True
									EndIf
									MoveEntity n\Collider, -EntityRoll(n\Collider)*0.002,0,0
									
									n\Reload=n\Reload-FPSfactor
									
									
								EndIf
							EndIf
						EndIf		
						
						If n\State = 3 Then FreeEntity Target
					Case 4 ;crash
						If n\State2 < 300 Then
							
							TurnEntity(n\OBJ2,0,20.0*FPSfactor,0)
							TurnEntity(n\OBJ3,20.0*FPSfactor,0,0)
							
							TurnEntity n\Collider,0,-FPSfactor*7,0;Sin(MilliSecs2()/40)*FPSfactor
							n\State2=n\State2+FPSfactor*0.3
							
							Target=CreatePivot()
							PositionEntity Target, n\EnemyX, n\EnemyY, n\EnemyZ, True
							
							PointEntity n\OBJ, Target
							MoveEntity n\OBJ, 0,0,FPSfactor*0.001*n\State2
							PositionEntity(n\Collider, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							
							If EntityDistance(n\OBJ, Target) <0.3 Then
								;If TempSound2 <> 0 Then FreeSound_Strict TempSound2 : TempSound2 = 0
								;TempSound2 = LoadSound_Strict("SFX\Character\Apache\Crash"+Rand(1,2)+".ogg")
								CameraShake = Max(CameraShake, 3.0)
								;PlaySound_Strict TempSound2
								PlaySound_Strict LoadTempSound("SFX\Character\Apache\Crash"+Rand(1,2)+".ogg")
								n\State = 5
							EndIf
							
							FreeEntity Target
						EndIf
				End Select
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider), EntityRoll(n\Collider), True
				;[End Block]
			Case NPCtype035_Tentacle
				;[Block]
				Dist = EntityDistance(n\Collider, Collider)
				
				If Dist < HideDistance
					If (Not n\IsDead) Then
					    Select n\State 
						    Case 0.0 ; ~ Spawns
								;[Block]
							    If n\Frame > 283.0 Then
								    HeartBeatVolume = Max(CurveValue(1.0, HeartBeatVolume, 50.0), HeartBeatVolume)
								    HeartBeatRate = Max(CurveValue(130.0, HeartBeatRate, 100.0), HeartBeatRate)
									
								    PointEntity(n\OBJ, Collider)
								    RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 25.0), 0.0)
									
								    AnimateNPC(n, 283.0, 389.0, 0.3, False)
									
								    If n\Frame > 388.0 Then
									    n\State = 1.0
										FreeSound_Strict(n\Sound) : n\Sound = 0
									EndIf
							    Else
								    If Dist < 2.5 Then 
									    SetNPCFrame(n, 284.0)
									    n\Sound = LoadSound_Strict("SFX\SCP\035_Tentacle\TentacleSpawn.ogg")
									    PlaySound_Strict(n\Sound)
								    EndIf
							    EndIf
								;[End Block]
						    Case 1.0 ; ~ Idles
								;[Block]
							    If n\Sound = 0 Then
								    FreeSound_Strict(n\Sound) : n\Sound = 0
								    n\Sound = LoadSound_Strict("SFX\SCP\035_Tentacle\TentacleIdle.ogg")
							    EndIf
							    n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider)
								
							    If Dist < 1.8 And (Not chs\NoTarget) Then
								    If Abs(DeltaYaw(n\Collider, Collider)) < 20.0 Then 
									    n\State = 2.0
									    If n\Sound <> 0 Then 
											FreeSound_Strict(n\Sound) : n\Sound = 0 
										EndIf
								    EndIf
									
									PointEntity(n\OBJ, Collider)
									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 25.0), 0.0)
								Else
									; ~ Randomly rotates
									If Rand(400) = 1 Then n\Angle = Rnd(360.0)
									
									RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + Sin(MilliSecs2() / 50) * 2.0, EntityYaw(n\Collider), 150.0), 0.0, True)
							    EndIf
								
							    AnimateNPC(n, 33.0, 174.0, 0.3)
								;[End Block]
						    Case 2.0 ; ~ Attacks
								;[Block]
							    ; ~ Finish the idle animation before playing the attack animation
							    If n\Frame > 33.0 And n\Frame < 174.0 Then
								    AnimateNPC(n, 33.0, 174.0, 2.0, False)
							    Else
								    PointEntity(n\OBJ, Collider)
								    RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0)							
									
								    If n\Frame > 33.0 Then n\Frame = 2.0
									
								    AnimateNPC(n, 2.0, 32.0, 0.3, False)
									
								    If n\Frame >= 5.0 And n\Frame < 6.0 Then
									    If Dist < 1.8 Then
										    If Abs(DeltaYaw(n\Collider, Collider)) < 20.0 Then 
											    If WearingHazmat > 0 Then
											        PlaySound_Strict(LoadTempSound("SFX\General\BodyFall.ogg"))
											        BlurTimer = 100.0
													Injuries = Injuries + Rnd(0.5)
												Else
												    BlurTimer = 100.0
												    PlaySound_Strict(DamageSFX(Rand(9, 10)))
											        Injuries = Injuries + Rnd(1.0, 1.5)
													
													If Injuries > 3.0 Then
													    If PlayerRoom\RoomTemplate\Name = "room2offices" Then
														    DeathMsg = Chr(34) + "One large and highly active tentacle-like appendage seems "
														    DeathMsg = DeathMsg + "to have grown outside the dead body of a scientist within office area [DATA REDACTED]. It's level of aggression is "
														    DeathMsg = DeathMsg + "unlike anything we've seen before - it looks like it has "
														    DeathMsg = DeathMsg + "beaten some unfortunate Class D to death at some point during the breach." + Chr(34)
													    Else
														    DeathMsg = Chr(34) + "We will need more than the regular cleaning team to take care of this. "
														    DeathMsg = DeathMsg + "Two large and highly active tentacle-like appendages seem "
														    DeathMsg = DeathMsg + "to have formed inside the chamber. Their level of aggression is "
														    DeathMsg = DeathMsg + "unlike anything we've seen before - it looks like they have "
														    DeathMsg = DeathMsg + "beaten some unfortunate Class D to death at some point during the breach." + Chr(34)
													    EndIf
													    Kill()
												    EndIf
											    EndIf
											Else
												PlaySound2(MissSFX, Camera, n\Collider)
										    EndIf
										Else
											PlaySound2(MissSFX, Camera, n\Collider, 3.0)
									    EndIf
									    n\Frame = 6.0
								    ElseIf n\Frame = 32.0
									    n\State = 1.0
									    n\Frame = 173.0
								    EndIf
							    EndIf
								;[End Block]
					    End Select
					Else
						; ~ The NPC was killed
					    AnimateNPC(n, 515.0, 551.0, 0.15, False)
					    If n\Frame >= 550.0 Then
							If n\SoundCHN <> 0 Then
								If ChannelPlaying(n\SoundCHN) = True Then StopChannel(n\SoundCHN)
								If n\Sound <> 0 Then
									FreeSound_Strict(n\Sound) : n\Sound = 0
								EndIf
							EndIf
				            HideEntity(n\OBJ)
							HideEntity(n\Collider)
				        EndIf
					EndIf
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider) - 180.0, EntityRoll(n\Collider), True)
				
				n\DropSpeed = 0.0
				
				ResetEntity(n\Collider)
				;[End Block]
			Case NPCtype860
				;[Block]
				If PlayerRoom\RoomTemplate\Name = "room860" Then
					Local fr.Forest = PlayerRoom\fr
					
					Dist = EntityDistance(Collider, n\Collider)
					
					If ForestNPC <> 0
						If ForestNPCData[2] = 1.0
							ShowEntity(ForestNPC)
							If n\State <> 1.0
								If (BlinkTimer < -8.0 And BlinkTimer > -12.0) Or (Not EntityInView(ForestNPC, Camera))
									ForestNPCData[2] = 0.0
									HideEntity(ForestNPC)
								EndIf
							EndIf
						Else
							HideEntity(ForestNPC)
						EndIf
					EndIf
					
					Select n\State
						Case 0.0 ; ~ Idles (hidden)
							;[Block]
							HideEntity(n\Collider)
							HideEntity(n\OBJ)
							HideEntity(n\OBJ2)
							
							n\State2 = 0.0
							PositionEntity(n\Collider, 0.0, -100.0, 0.0)
							;[End Block]
						Case 1.0 ; ~ Appears briefly behind the trees
							;[Block]
							n\DropSpeed = 0.0
							
							If EntityY(n\Collider) =< -100.0 Then
								; ~ Transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0, fr\Forest_Pivot)
								
								; ~ Calculate the indices of the forest cell the player is in
								x = Floor((TFormedX() + 6.0) / 12.0)
								z = Floor((TFormedZ() + 6.0) / 12.0)
								
								; ~ Step through nearby cells
								For x2 = Max(x - 1, 1) To Min(x + 1, GridSize) Step 2
									For z2 = Max(z - 1, 1) To Min(z + 1, GridSize) Step 2
										; ~ Choose an empty cell (not on the path)
										If fr\grid[(z2 * GridSize) + x2] = 0 Then
											; ~ Spawn the monster between the empty cell and the cell the player is in
											TFormPoint(((x2 + x) / 2) * 12.0, 0, ((z2 + z) / 2) * 12.0, fr\Forest_Pivot, 0)
											
											; ~ Keep searching for a more suitable cell
											If EntityInView(n\Collider, Camera) Then
												PositionEntity(n\Collider, 0.0, -110.0, 0.0)
											Else
												PositionEntity(n\Collider, TFormedX(), EntityY(fr\Forest_Pivot, True) + 2.3, TFormedZ())
												x2 = GridSize
												Exit												
											EndIf
										EndIf
									Next
								Next
								
								If EntityY(n\Collider) > -100.0 Then
									PlaySound2(Step2SFX(Rand(3, 5)), Camera, n\Collider, 15.0, 0.5)
									
									If ForestNPCData[2] <> 1.0 Then ForestNPCData[2] = 0.0
									
									Select Rand(3)
										Case 1
											;[Block]
											PointEntity(n\Collider, Collider)
											SetNPCFrame(n, 2.0)
										Case 2
											;[Block]
											PointEntity(n\Collider, Collider)
											SetNPCFrame(n, 201.0)
											;[End Block]
										Case 3
											;[Block]
											PointEntity(n\Collider, Collider)
											TurnEntity(n\Collider, 0.0, 90.0, 0.0)
											SetNPCFrame(n, 299.0)
											;[End Block]
									End Select
									n\State2 = 0.0
								EndIf
							Else
								ShowEntity(n\OBJ)
								ShowEntity(n\Collider)
								
								PositionEntity(n\Collider, EntityX(n\Collider), EntityY(fr\Forest_Pivot, True) + 2.3, EntityZ(n\Collider))
								
								If ForestNPC <> 0
									If ForestNPCData[2] = 0.0 Then
										Local DocChance% = 0
										Local DocAmount% = 0
										
										For i = 0 To MaxItemAmount - 1
											If Inventory(i) <> Null Then
												Local DocName$ = Inventory(i)\ItemTemplate\Name
												
												If DocName = "Log #1" Or DocName = "Log #2" Or DocName = "Log #3" Then
													DocAmount = DocAmount + 1
													DocChance = DocChance + 10 * DocAmount
												EndIf
											EndIf
										Next
										
										If Rand(1, 860 - DocChance) = 1 Then
											ShowEntity(ForestNPC)
											ForestNPCData[2] = 1.0
											If Rand(2) = 1
												ForestNPCData[0] = 0.0
											Else
												ForestNPCData[0] = 2.0
											EndIf
											ForestNPCData[1] = 0.0
											PositionEntity(ForestNPC, EntityX(n\Collider),EntityY(n\Collider) + 0.5, EntityZ(n\Collider))
											RotateEntity(ForestNPC, 0.0, EntityYaw(n\Collider), 0.0)
											MoveEntity(ForestNPC, 0.75, 0.0, 0.0)
											RotateEntity(ForestNPC, 0.0, 0.0, 0.0)
											EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
										Else
											ForestNPCData[2] = 2.0
										EndIf
									ElseIf ForestNPCData[2] = 1.0
										If ForestNPCData[1] = 0.0
											If Rand(200) = 1 Then
												ForestNPCData[1] = FPSfactor
												EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0] + 1.0)
											EndIf
										ElseIf ForestNPCData[1] > 0.0 And ForestNPCData[1] < 5.0
											ForestNPCData[1] = Min(ForestNPCData[1] + FPSfactor, 5.0)
										Else
											ForestNPCData[1] = 0
											EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
										EndIf
									EndIf
								EndIf
								
								If n\State2 = 0 Then ; ~ Don't start moving until the player is looking
									If EntityInView(n\Collider, Camera) Then 
										n\State2 = 1.0
										If Rand(8) = 1 Then
											PlaySound2(LoadTempSound("SFX\SCP\860\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 20.0)
										EndIf										
									EndIf
								Else
									If n\Frame =< 199.0 Then
										AnimateNPC(n, 2.0, 199.0, 0.5, False)
										If n\Frame = 199.0 Then n\Frame = 298.0 : PlaySound2(Step2SFX(Rand(3, 5)), Camera, n\Collider, 15.0)
									ElseIf n\Frame =< 297.0
										PointEntity(n\Collider, Collider)
										
										AnimateNPC(n, 200.0, 297.0, 0.5, False)
										If n\Frame = 297.0 Then n\Frame = 298.0 : PlaySound2(Step2SFX(Rand(3, 5)), Camera, n\Collider, 15.0)
									Else
										Angle = CurveAngle(point_direction(EntityX(n\Collider),EntityZ(n\Collider),EntityX(Collider),EntityZ(Collider)),EntityYaw(n\Collider)+90,20.0)
										
										RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
										
										AnimateNPC(n, 298.0, 316.0, n\CurrSpeed * 10.0)
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
										
										If Dist > 15.0 Then
											PositionEntity(n\Collider, 0.0, -110.0, 0.0)
											n\State = 0.0
											n\State2 = 0.0
										EndIf
									EndIf									
								EndIf
							EndIf
							
							ResetEntity(n\Collider)
							;[End Block]
						Case 2.0 ; ~ Appears on the path and starts to walk towards the player
							;[Block]
							ShowEntity(n\OBJ)
							ShowEntity(n\Collider)
							
							PrevFrame = n\Frame
							
							If EntityY(n\Collider) =< -100.0 Then
								; ~ Transform the position of the player to the local coordinates of the forest
								TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0, fr\Forest_Pivot)
								
								; ~ Calculate the indices of the forest cell the player is in
								x = Floor((TFormedX() + 6.0) / 12.0)
								z = Floor((TFormedZ() + 6.0) / 12.0)
								
								For x2 = Max(x - 1, 1) To Min(x + 1, GridSize)
									For z2 = Max(z - 1, 1) To Min(z + 1, GridSize)
										; ~ Find a nearby cell that's on the path and not the cell the player is in
										If fr\grid[(z2 * GridSize) + x2] > 0 And (x2<>x Or z2 <> z) And (x2 = x Or z2 = z) Then
											; ~ Transform the position of the cell back to world coordinates
											TFormPoint(x2 * 12.0, 0.0, z2 * 12.0, fr\Forest_Pivot, 0)
											
											PositionEntity(n\Collider, TFormedX(), EntityY(fr\Forest_Pivot, True) + 1.0, TFormedZ())
											
											If EntityInView(n\Collider, Camera) Then
												BlinkTimer = -10.0
											Else
												x2 = GridSize
												Exit
											EndIf
										EndIf
									Next
								Next
							Else
								Angle = CurveAngle(Find860Angle(n, fr), EntityYaw(n\Collider) + 90.0, 80.0)
								
								RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
								
								n\CurrSpeed = CurveValue(n\Speed * 0.3, n\CurrSpeed, 50.0)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								
								AnimateNPC(n, 494.0, 569.0, n\CurrSpeed * 25.0)
								
								If n\State2 = 0.0 Then
									If Dist < 8.0 Then
										If EntityInView(n\Collider, Camera) Then
											PlaySound_Strict(LoadTempSound("SFX\SCP\860\Chase" + Rand(1, 2) + ".ogg"))
											
											PlaySound2(LoadTempSound("SFX\SCP\860\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider)	
											n\State2 = 1.0
										EndIf										
									EndIf
								EndIf
								
								If CurrSpeed > 0.03 Then ; ~ The player is running
									n\State3 = n\State3 + FPSfactor
									If Rnd(5000.0) < n\State3 Then
										Temp = True
										If n\SoundCHN <> 0 Then
											If ChannelPlaying(n\SoundCHN) = True Then Temp = False
										EndIf
										If Temp Then
											n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\860\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider)
										EndIf
									EndIf
								Else
									n\State3 = Max(n\State3 - FPSfactor, 0.0)
								EndIf
								
								If Dist < 4.5 Or n\State3 > Rnd(200.0, 250.0) Then
									n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\860\Cancer" + Rand(3, 5) + ".ogg"), Camera, n\Collider)
									If (Not chs\Notarget) Then
										n\State = 3.0
									Else
										If (PrevFrame < 492.0 And n\Frame >= 492.0) Or (PrevFrame < 568.0 And n\Frame >= 568.0) Then
											SetNPCFrame(n, 2.0)
											n\State = 4.0
										EndIf
									EndIf
								EndIf
								
								If Dist > 20.0 Then
									n\State = 0.0
									n\State2 = 0.0
									PositionEntity(n\Collider, 0.0, -110.0, 0.0)
								EndIf
							EndIf
							
							If (PrevFrame < 533.0 And n\Frame >= 533.0) Or (PrevFrame > 568.0 And n\Frame < 2.0) Then
								PlaySound2(Step2SFX(Rand(3, 5)), Camera, n\Collider, 15.0, 0.6)
							EndIf
							;[End Block]
						Case 3.0 ; ~ Runs towards the player and attacks
							;[Block]
							ShowEntity(n\OBJ)
							ShowEntity(n\Collider)
							
							PrevFrame = n\Frame
							
							Angle = CurveAngle(Find860Angle(n, fr), EntityYaw(n\Collider) + 90.0, 40.0)
							
							RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
							
							; ~ If close enough to attack or already attacking, play the attack anim
							If (Dist < 1.1 Or (n\Frame > 451.0 And n\Frame < 493.0) Or KillTimer < 0.0) Then
								DeathMSG = ""
								
								n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
								
								AnimateNPC(n, 451.0, 493.0, 0.5, False)
								
								If (PrevFrame < 461.0 And n\Frame >= 461.0) Then 
									If KillTimer >= 0.0 Then Kill() : KillAnim = 0
									PlaySound_Strict(DamageSFX(11))
								EndIf
								If (PrevFrame < 476.0 And n\Frame >= 476.0) Then PlaySound_Strict(DamageSFX(12))
								If (PrevFrame < 486.0 And n\Frame >= 486.0) Then PlaySound_Strict(DamageSFX(12))
							Else
								n\CurrSpeed = CurveValue(n\Speed * 0.8, n\CurrSpeed, 10.0)
								
								AnimateNPC(n, 298.0, 316.0, n\CurrSpeed * 10.0)
								
								If (PrevFrame < 307.0 And n\Frame >= 307.0) Then
									PlaySound2(Step2SFX(Rand(3, 5)), Camera, n\Collider, 10.0)
								EndIf
							EndIf
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
							
							If chs\Notarget Then
								If (PrevFrame < 315.0 And n\Frame >= 315.0) Or (PrevFrame < 492.0 And n\Frame >= 492.0) Then
									SetNPCFrame(n, 2.0)
									n\State = 4.0
								EndIf
							EndIf
							;[End Block]
						Case 4.0 ; ~ Idles (not hidden)
							;[Block]
							n\CurrSpeed = 0.0
							
							AnimateNPC(n, 2.0, 199.0, 0.5)
							
							If (Not chs\Notarget) Then n\State = 3.0
							;[End Block]
					End Select
					
					If n\State <> 0.0 Then
						RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)	
						
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.23, EntityZ(n\Collider))
						RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
						
						If Dist > 8.0 Then
							ShowEntity(n\OBJ2)
							EntityAlpha(n\OBJ2, Min(Dist - 8.0, 1.0))
							
							PositionEntity(n\OBJ2, EntityX(n\OBJ), EntityY(n\OBJ) , EntityZ(n\OBJ))
							RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
							MoveEntity(n\OBJ2, 0.0, 30.0 * 0.025, (-33.0) * 0.025)
							
							; ~ Render distance is set to 8.5 inside the forest,
							; ~ So we need to cheat a bit to make the eyes visible if they're further than that
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
							PointEntity(Pvt, n\OBJ2)
							MoveEntity(Pvt, 0.0, 0.0, 8.0)
							PositionEntity(n\OBJ2, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
							FreeEntity(Pvt)
						Else
							HideEntity(n\OBJ2)
						EndIf
					EndIf
				EndIf
				;[End Block] 
			Case NPCtype939
				;[Block]
				
				If PlayerRoom\RoomTemplate\Name <> "room3storage"
					n\State = 66
				EndIf
				
				;state is set to 66 in the room3storage-event if player isn't inside the room
				If n\State < 66 Then 
					Select n\State
						Case 0
							AnimateNPC(n, 290,405,0.1)
							
							;Animate2(n\obj,AnimTime(n\obj),290,405,0.1)
						Case 1
							
							If n\Frame=>644 And n\Frame<683 Then ;finish the walking animation
								;n\CurrSpeed = CurveValue(n\Speed*0.2, n\CurrSpeed, 10.0)
								n\CurrSpeed = CurveValue(n\Speed*0.05, n\CurrSpeed, 10.0)
								AnimateNPC(n, 644,683,28*n\CurrSpeed*4,False)
								If n\Frame=>682 Then n\Frame =175
								
								;Animate2(n\obj,AnimTime(n\obj),644,683,28*n\CurrSpeed,False)
								;If AnimTime(n\obj)=683 Then SetAnimTime(n\obj,175)
							Else
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
								AnimateNPC(n, 175,297,0.22,False)
								If n\Frame=>296 Then n\State = 2
								
								;Animate2(n\obj,AnimTime(n\obj),175,297,0.22,False)
								;If AnimTime(n\obj)=297 Then n\State = 2
							EndIf
							
							n\LastSeen = 0
							
							MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor						
							
						Case 2
							n\State2 = Max(n\State2, (n\PrevState-3))
							
							Dist = EntityDistance(n\Collider, PlayerRoom\Objects[n\State2])
							
							n\CurrSpeed = CurveValue(n\Speed*0.3*Min(Dist,1.0), n\CurrSpeed, 10.0)
							MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor 
							
							PrevFrame = n\Frame
							AnimateNPC(n, 644,683,28*n\CurrSpeed) ;walk
							
							;prevFrame = AnimTime(n\obj)
							;Animate2(n\obj,AnimTime(n\obj),644,683,28*n\CurrSpeed) ;walk
							
							If (PrevFrame<664 And n\Frame=>664) Or (PrevFrame>673 And n\Frame<654) Then
								PlaySound2(StepSFX(4, 0, Rand(0,3)), Camera, n\Collider, 12.0)
								If Rand(10)=1 Then
									Temp = False
									If n\SoundCHN = 0 Then 
										Temp = True
									ElseIf Not ChannelPlaying(n\SoundCHN)
										Temp = True
									EndIf
									If Temp Then
										If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
										n\Sound = LoadSound_Strict("SFX\SCP\939\"+(n\ID Mod 3)+"Lure"+Rand(1,10)+".ogg")
										n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)
									EndIf
								EndIf
							EndIf
							
							PointEntity n\OBJ, PlayerRoom\Objects[n\State2]
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),20.0), 0
							
							If Dist<0.4 Then
								n\State2 = n\State2 + 1
								If n\State2 > n\PrevState Then n\State2 = (n\PrevState-3)
								n\State = 1
							EndIf
							
						Case 3
							If EntityVisible(Collider, n\Collider) Then
								n\EnemyX = EntityX(Collider)
								n\EnemyZ = EntityZ(Collider)
								n\LastSeen = 10*7
							EndIf
							
							If n\LastSeen > 0 And (Not chs\NoTarget) Then
								PrevFrame = n\Frame
								
								If (n\Frame=>18.0 And n\Frame<68.0) Then
									n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
									AnimateNPC(n, 18,68,0.5,True)
									;Animate2(n\obj,AnimTime(n\obj),18,68,0.5,True)
									
									;hasn't hit
									Temp = False
									
									If PrevFrame < 24 And n\Frame>=24 Then
										Temp = True
									ElseIf PrevFrame < 57 And n\Frame>=57
										Temp = True
									EndIf
									
									If Temp Then
										If Distance(n\EnemyX, n\EnemyZ, EntityX(n\Collider), EntityZ(n\Collider))<1.5 Then
											PlaySound_Strict(DamageSFX(11))
											Injuries = Injuries + Rnd(1.5, 2.5)-WearingVest*0.5
											BlurTimer = 500		
										Else
											n\Frame	 = 449
											;SetAnimTime(n\obj, 449)
										EndIf
									EndIf
									
									If Injuries>4.0 Then 
										DeathMSG=Chr(34)+"All four (4) escaped SCP-939 specimens have been captured and recontained successfully. "
										DeathMSG=DeathMSG+"Three (3) of them made quite a mess at Storage Area 6. A cleaning team has been dispatched."+Chr(34)
										Kill()
										If (Not chs\GodMode) Then n\State = 5
									EndIf								
								Else
									If n\LastSeen = 10*7 Then 
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										
										AnimateNPC(n, 449,464,6*n\CurrSpeed) ;run
										;Animate2(n\obj,AnimTime(n\obj),449,464,6*n\CurrSpeed) ;run
										
										If (PrevFrame<452 And n\Frame=>452) Or (PrevFrame<459 And n\Frame=>459) Then
											PlaySound2(StepSFX(1, 1, Rand(0,7)), Camera, n\Collider, 12.0)
										EndIf										
										
										If Distance(n\EnemyX, n\EnemyZ, EntityX(n\Collider), EntityZ(n\Collider))<1.1 Then ;player is visible
											n\Frame = 18
											;SetAnimTime(n\obj, 18.0)
										EndIf
									Else
										n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
										AnimateNPC(n, 175,297,5*n\CurrSpeed,True)	
										;Animate2(n\obj,AnimTime(n\obj),175,297,5*n\CurrSpeed,True)
									EndIf
									
								EndIf
								
								Angle = VectorYaw(n\EnemyX-EntityX(n\Collider), 0.0, n\EnemyZ-EntityZ(n\Collider))
								RotateEntity n\Collider, 0, CurveAngle(Angle,EntityYaw(n\Collider),15.0), 0									
								
								MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor							
								
								n\LastSeen = n\LastSeen - FPSfactor
							Else
								n\State = 2
							EndIf
							
						;Animate2(n\obj,AnimTime(n\obj),406,437,0.1) ;leap
						Case 5
							If n\Frame<68 Then
								AnimateNPC(n, 18,68,0.5,False) ;finish the attack animation
								
								;Animate2(n\obj,AnimTime(n\obj),18,68,0.5,False) ;finish the attack animation
							Else
								AnimateNPC(n, 464,473,0.5,False) ;attack to idle
								
								;Animate2(n\obj,AnimTime(n\obj),464,473,0.5,False) ;attack to idle
							EndIf
							
					End Select
					
					If n\State < 3 And (Not chs\NoTarget) And (Not n\IgnorePlayer) Then
						Dist = EntityDistance(n\Collider, Collider)
						
						If Dist < 4.0 Then Dist = Dist - EntityVisible(Collider, n\Collider)
						If PlayerSoundVolume*1.2>Dist Or Dist < 1.5 Then
							If n\State3 = 0 Then
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\SCP\939\"+(n\ID Mod 3)+"Attack"+Rand(1,3)+".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)										
								
								PlaySound_Strict(LoadTempSound("SFX\SCP\939\attack.ogg"))
								n\State3 = 1
							EndIf
							
							n\State = 3
						ElseIf PlayerSoundVolume*1.6>Dist
							If n\State<>1 And n\Reload <= 0 Then
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\SCP\939\"+(n\ID Mod 3)+"Alert"+Rand(1,3)+".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider)	
								
								n\Frame = 175
								n\Reload = 70 * 3
								;SetAnimTime(n\obj, 175)	
							EndIf
							
							n\State = 1
							
						EndIf
						
						n\Reload = n\Reload - FPSfactor
						
					EndIf				
					
					RotateEntity n\Collider, 0, EntityYaw(n\Collider), 0, True	
					
					PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider)-0.28, EntityZ(n\Collider))
					RotateEntity n\OBJ, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), EntityRoll(n\Collider), True					
				EndIf
				;[End Block]
			Case NPCtype066
				;[Block]
				Dist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))
				
				Select n\State
					Case 0 
						;idle: moves around randomly from waypoint to another if the player is far enough
						;starts staring at the player when the player is close enough
						
						If Dist > 20.0 Then
							AnimateNPC(n, 451, 612, 0.2, True)
							;Animate2(n\obj, AnimTime(n\obj), 451, 612, 0.2, True)
							
							If n\State2 < MilliSecs2() Then
								For w.waypoints = Each WayPoints
									If w\door = Null Then
										If Abs(EntityX(w\obj,True)-EntityX(n\Collider))<4.0 Then
											If Abs(EntityZ(w\obj,True)-EntityZ(n\Collider))<4.0 Then
												PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.3, EntityZ(w\obj,True)
												ResetEntity n\Collider
												Exit
											EndIf
										EndIf
									EndIf
								Next
								n\State2 = MilliSecs2()+5000
							EndIf
						ElseIf Dist < 8.0
							n\LastDist = Rnd(1.0, 2.5)
							n\State = 1
						EndIf
					Case 1 ;staring at the player
						
						If n\Frame<451 Then
							Angle = WrapAngle(CurveAngle(DeltaYaw(n\Collider, Collider)-180, (AnimTime(n\OBJ)-2.0)/1.2445, 15.0))
							;0->360 = 2->450
							SetNPCFrame(n,Angle*1.2445+2.0)
							
							;SetAnimTime(n\obj, angle*1.2445+2.0)							
						Else
							AnimateNPC(n, 636, 646, 0.4, False)
							If n\Frame = 646 Then SetNPCFrame(n,2)
							;Animate2(n\obj, AnimTime(n\obj), 636, 646, 0.4, False)
							;If AnimTime(n\obj)=646 Then SetAnimTime (n\obj, 2)
						EndIf
						Dist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))
						
						If Rand(700)=1 Then PlaySound2(LoadTempSound("SFX\SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
						
						If Dist < 1.0+n\LastDist Then n\State = Rand(2,3)
					Case 2 ;roll towards the player and make a sound, and then escape	
						If n\Frame < 647 Then 
							Angle = CurveAngle(0, (AnimTime(n\OBJ)-2.0)/1.2445, 5.0)
							
							If Angle < 5 Or Angle > 355 Then 
								SetNPCFrame(n,647)
							Else
								SetNPCFrame(n,Angle*1.2445+2.0)
							EndIf
							;SetAnimTime(n\obj, angle*1.2445+2.0)
							;If angle < 5 Or angle > 355 Then SetAnimTime(n\obj, 647)
						Else
							If n\Frame=683 Then 
								If n\State2 = 0 Then
									If Rand(2)=1 Then
										PlaySound2(LoadTempSound("SFX\SCP\066\Eric"+Rand(1,3)+".ogg"),Camera, n\Collider, 8.0)
									Else
										PlaySound2(LoadTempSound("SFX\SCP\066\Notes"+Rand(1,6)+".ogg"), Camera, n\Collider, 8.0)
									EndIf									
									
									Select Rand(1,6)
										Case 1
											If n\Sound2=0 Then n\Sound2=LoadSound_Strict("SFX\SCP\066\Beethoven.ogg")
											n\SoundCHN2 = PlaySound2(n\Sound2, Camera, n\Collider)
											DeafTimer# = 70*(45+(15*SelectedDifficulty\aggressiveNPCs))
											DeafPlayer = True
											CameraShake = 10.0
										Case 2
											n\State3 = Rand(700,1400)
										Case 3
											For d.Doors = Each Doors
												If d\locked = False And d\KeyCard = 0 And d\Code = "" Then
													If Abs(EntityX(d\frameobj)-EntityX(n\Collider))<16.0 Then
														If Abs(EntityZ(d\frameobj)-EntityZ(n\Collider))<16.0 Then
															UseDoor(d, False)
														EndIf
													EndIf
												EndIf
											Next
										Case 4
											If PlayerRoom\RoomTemplate\DisableDecals = False Then
												CameraShake = 5.0
												de.Decals = CreateDecal(1, EntityX(n\Collider), 0.01, EntityZ(n\Collider), 90, Rand(360), 0)
												de\Size = 0.3 : UpdateDecals
												PlaySound_Strict(LoadTempSound("SFX\General\BodyFall.ogg"))
												If Distance(EntityX(Collider),EntityZ(Collider),EntityX(n\Collider),EntityZ(n\Collider))<0.8 Then
													Injuries = Injuries + Rnd(0.3,0.5)
												EndIf
											EndIf
									End Select
								EndIf
								
								n\State2 = n\State2+FPSfactor
								If n\State2>70 Then 
									n\State = 3
									n\State2 = 0
								EndIf
							Else
								n\CurrSpeed = CurveValue(n\Speed*1.5, n\CurrSpeed, 10.0)
								PointEntity n\OBJ, Collider
								;angle = CurveAngle(EntityYaw(n\obj), EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ)-180, EntityYaw(n\Collider), 10), 0
								
								AnimateNPC(n, 647, 683, n\CurrSpeed*25, False)
								;Animate2(n\obj, AnimTime(n\obj), 647, 683, n\CurrSpeed*25, False)
								
								MoveEntity n\Collider, 0,0,-n\CurrSpeed*FPSfactor
								
							EndIf
						EndIf
					Case 3
						PointEntity n\OBJ, Collider
						Angle = CurveAngle(EntityYaw(n\OBJ)+n\Angle-180, EntityYaw(n\Collider), 10);1.0/Max(n\CurrSpeed,0.0001))
						RotateEntity n\Collider, 0, Angle, 0
						
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
						MoveEntity n\Collider, 0,0,n\CurrSpeed*FPSfactor
						
						;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						
						If Rand(100)=1 Then n\Angle = Rnd(-20,20)
						
						n\State2 = n\State2 + FPSfactor
						If n\State2>250 Then 
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25, False)
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25, False)
							If n\Frame=647 Then 
								n\State = 0
								n\State2=0
							EndIf
						Else
							AnimateNPC(n, 684, 647, -n\CurrSpeed*25)
							
							;Animate2(n\obj, AnimTime(n\obj), 684, 647, -n\CurrSpeed*25)
						EndIf
						
				End Select
				
				If n\State > 1 Then
					If n\Sound = 0 Then n\Sound = LoadSound_Strict("SFX\SCP\066\Rolling.ogg")
					If n\SoundCHN<>0 Then
						If ChannelPlaying(n\SoundCHN) Then
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, n\Collider, 20)
						EndIf
					Else
						n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20)
					EndIf					
				EndIf
				
				;If n\SoundChn2<>0 Then
				;	If ChannelPlaying(n\SoundChn2) Then
				;		n\SoundChn2 = LoopSound2(n\Sound2, n\SoundChn2, Camera, n\Collider, 20)
				;		BlurTimer = Max((5.0-dist)*300,0)
				;	EndIf
				;EndIf
				
				
				If n\State3 > 0 Then
					n\State3 = n\State3-FPSfactor
					LightVolume = TempLightVolume-TempLightVolume*Min(Max(n\State3/500,0.01),0.6)
					HeartBeatRate = Max(HeartBeatRate, 130)
					HeartBeatVolume = Max(HeartBeatVolume,Min(n\State3/1000,1.0))
				EndIf
				
				If ChannelPlaying(n\SoundCHN2)
					UpdateSoundOrigin2(n\SoundCHN2,Camera,n\Collider,20)
					BlurTimer = Max((5.0-Dist)*300,0)
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				RotateEntity n\OBJ, EntityPitch(n\Collider)-90, EntityYaw(n\Collider), 0
				;[End Block]
			Case NPCtype966
				;[Block]
				Dist = EntityDistance(n\Collider, Collider)
				
				If n\State > -1.0 Then
					If (Dist < HideDistance) Then
						; ~ n\State = The "general" state (Idles / Wanders off/ Attacks / Echo and etc.)
						
						; ~ n\State2 = Timer for doing raycasts
						
						If EntityVisible(n\Collider, Camera) Then
							If WearingNightVision > 0 Then GiveAchievement(Achv966)
						EndIf
						
						PrevFrame = n\Frame
						
						If n\Sound > 0 Then
							Temp = 0.5
							; ~ The ambient sound gets louder when the npcs are attacking
							If n\State > 0.0 Then Temp = 1.0	
							
							n\SoundCHN = LoopSound2(n\Sound, n\SoundCHN, Camera, Camera, 10.0, Temp)
						EndIf
						
						Temp = Rnd(-1.0, 1.0)
						PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.2, EntityZ(n\Collider, True))
						RotateEntity(n\OBJ, -90.0, EntityYaw(n\Collider), 0.0)
						
						If WearingNightVision = 0 Then
							HideEntity(n\OBJ)
							If (Not chs\Notarget) Then
								If Dist < 1.0 And n\Reload =< 0.0 And MsgTimer =< 0.0 Then
									Select Rand(6)
										Case 1
											;[Block]
											Msg = "You feel something breathing right next to you."
											;[End Block]
										Case 2
											;[Block]
											Msg = Chr(34) + "It feels like something's in this room with me." + Chr(34)
											;[End Block]
										Case 3
											;[Block]
											Msg = "You feel like something is here with you, but you do not see anything."
											;[End Block]
										Case 4
											;[Block]
											Msg = Chr(34) + "Is my mind playing tricks on me or is there someone else here?" + Chr(34)
											;[End Block]
										Case 5
											;[Block]
											Msg = "You feel like something is following you."
											;[End Block]
										Case 6
											;[Block]
											Msg = "You can feel something near you, but you are unable to see it. Perhaps its time is now."
											;[End Block]
									End Select
									n\Reload = 70 * 20.0
									MsgTimer = 70 * 8.0
								EndIf
								n\Reload = n\Reload - FPSfactor
							EndIf
						Else
							ShowEntity(n\OBJ)
						EndIf
						
						If n\State3 > 70 * 5.0 Then
							If n\State3 < 1000.0 Then
								For n2.NPCs = Each NPCs	
									If n2\NPCtype = n\NPCtype Then n2\State3 = 1000.0 
								Next
							EndIf
							n\State = Max(n\State, 8.0)
							n\State3 = 1000.0					
						EndIf
						
						If Stamina < 10.0 Then 
							n\State3 = n\State3 + FPSfactor
						Else If n\State3 < 900.0
							n\State3 = Max(n\State3 - FPSfactor * 0.2, 0.0)
						EndIf
						
						If n\State <> 10.0
							n\LastSeen = 0.0
						EndIf
						
						Select n\State
							Case 0.0 ; ~ Idle, standing
								;[Block]
								If n\Frame > 556.0
									AnimateNPC(n, 628.0, 652.0, 0.25, False)
									If n\Frame > 651.0 Then SetNPCFrame(n, 2.0)
								Else
									AnimateNPC(n, 2.0, 214.0, 0.25, False)
									
									; ~ Echo / Stares off / Walking around periodically
									If n\Frame > 213.0
										If Rand(3) = 1 And Dist < 4.0 Then
											n\State = Rand(1.0, 4.0)
										Else
											n\State = Rand(5.0, 6.0)								
										EndIf
									EndIf
									
									; ~ Echo if player gets close
									If Dist < 2.0 Then 
										n\State = Rand(1, 4)
									EndIf 							
								EndIf
								
								n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
								
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed)
								;[End Block]
							Case 1.0, 2.0 ; ~ Ccho
								;[Block]
								AnimateNPC(n, 214.0, 257.0, 0.25, False)
								If n\Frame > 256.0 Then n\State = 0.0
								
								If n\Frame > 228.0 And PrevFrame =< 228.0
									PlaySound2(LoadTempSound("SFX\SCP\966\Echo" + Rand(1, 3) + ".ogg"), Camera, n\Collider)
								EndIf
								
								If (Not chs\Notarget) Then
									Angle = VectorYaw(EntityX(Collider) - EntityX(n\Collider), 0.0, EntityZ(Collider) - EntityZ(n\Collider))
									RotateEntity(n\Collider, 0.0, CurveAngle(Angle, EntityYaw(n\Collider), 20.0), 0.0)
									
									If n\State3 < 900.0 Then
										BlurTimer = ((Sin(MilliSecs2() / 50) + 1.0) * 200) / Dist
										
										If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 And Dist < 16.0 Then
											BlinkEffect = Max(BlinkEffect, 1.5)
											BlinkEffectTimer = 1000.0
											
											StaminaEffect = 2.0
											StaminaEffectTimer = 1000.0
											
											If MsgTimer =< 0.0 And StaminaEffect < 1.5 Then
												Select Rand(4)
													Case 1
														;[Block]
														Msg = "You feel exhausted."
														;[End Block]
													Case 2
														;[Block]
														Msg = Chr(34) + "Could really go for a nap now..." + Chr(34)
														;[End Block]
													Case 3
														;[Block]
														Msg = Chr(34) + "If I wasn't in this situation I would take a nap somewhere." + Chr(34)
														;[End Block]
													Case 4
														;[Block]
														Msg = "You feel restless."
														;[End Block]
												End Select
												MsgTimer = 70 * 7.0
											EndIf
										EndIf							
									EndIf
								EndIf
								;[End Block]
							Case 3.0, 4.0 ; ~ Stare off at player
								;[Block]
								If n\State = 3.0 Then
									AnimateNPC(n, 257.0, 332.0, 0.25, False)
									If n\Frame > 331.0 Then n\State = 0.0
								Else
									AnimateNPC(n, 332.0, 457.0, 0.25, False)
									If n\Frame > 456.0 Then n\State = 0.0
								EndIf
								
								If n\Frame > 271.0 And PrevFrame =< 271.0 Or n\Frame > 354 Or n\Frame > 314.0 And PrevFrame =< 314.0 Or n\Frame > 301.0 And PrevFrame =< 301.0 Then
									PlaySound2(LoadTempSound("SFX\SCP\966\Idle" + Rand(1, 3) + ".ogg"), Camera, n\Collider)
								EndIf
								
								If (Not chs\Notarget) Then
									Angle = VectorYaw(EntityX(Collider) - EntityX(n\Collider), 0.0, EntityZ(Collider) - EntityZ(n\Collider))
									RotateEntity(n\Collider, 0.0, CurveAngle(Angle, EntityYaw(n\Collider), 20.0), 0.0)
								EndIf
								;[End Block]
							Case 5.0, 6.0, 8.0 ; ~ Walking or chasing
								;[Block]
								If n\Frame < 580.0 And n\Frame > 214.0
									AnimateNPC(n, 556.0, 580.0, 0.25, False)
								Else
									If n\CurrSpeed >= 0.0 Then
										AnimateNPC(n, 580.0, 628.0, n\CurrSpeed * 25.0)
									Else
										AnimateNPC(n, 2.0, 214.0, 0.25)
									EndIf
									
									; ~ Chasing the player
									If n\State = 8.0 And Dist < 32.0 Then
										If n\PathTimer =< 0.0 Then
											n\PathStatus = FindPath(n, EntityX(Collider, True), EntityY(Collider, True), EntityZ(Collider, True))
											n\PathTimer = 40 * 10.0
											n\CurrSpeed = 0.0
										EndIf
										n\PathTimer = Max(n\PathTimer - FPSfactor, 0.0)
										
										If (Not EntityVisible(n\Collider, Collider)) Then
											If n\PathStatus = 2 Then
												n\CurrSpeed = 0.0
												SetNPCFrame(n, 2.0)
											ElseIf n\PathStatus = 1
												If n\Path[n\PathLocation] = Null Then 
													If n\PathLocation > 19 Then 
														n\PathLocation = 0 : n\PathStatus = 0
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Else
													n\Angle = VectorYaw(EntityX(n\Path[n\PathLocation]\OBJ, True) - EntityX(n\Collider), 0.0, EntityZ(n\Path[n\PathLocation]\OBJ, True) - EntityZ(n\Collider))
													
													Dist2 = EntityDistance(n\Collider, n\Path[n\PathLocation]\OBJ)
													
													Temp = True
													If Dist2 < 0.8 Then 
														If n\Path[n\PathLocation]\door <> Null Then
															If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
																If (n\Path[n\PathLocation]\door\Locked Or n\Path[n\PathLocation]\door\KeyCard <> 0 Or n\Path[n\PathLocation]\door\Code <> "") And (Not n\Path[n\PathLocation]\door\Open) Then
																	Temp = False
																Else
																	If n\Path[n\PathLocation]\door\Open = False And (n\Path[n\PathLocation]\door\Buttons[0] <> 0 Or n\Path[n\PathLocation]\door\Buttons[1] <> 0) Then
																		UseDoor(n\Path[n\PathLocation]\door, False)
																	EndIf
																EndIf
															EndIf
														EndIf
														If Dist2 < 0.3 Then n\PathLocation = n\PathLocation + 1
													EndIf
													
													If Temp = False Then
														n\PathStatus = 0
														n\PathLocation = 0
														n\PathTimer = 40 * 10.0
													EndIf
												EndIf
												
												n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
											ElseIf n\PathStatus = 0
												n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
											EndIf
										Else
											n\Angle = VectorYaw(EntityX(Collider) - EntityX(n\Collider), 0.0, EntityZ(Collider) - EntityZ(n\Collider))
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
											
											If Dist < 1.0 Then n\State = 10.0
										EndIf
									Else
										If MilliSecs2() > n\State2 And Dist < 16.0 Then
											HideEntity(n\Collider) 
											EntityPick(n\Collider, 1.5)
											If PickedEntity() <> 0 Then
												n\Angle = EntityYaw(n\Collider) + Rnd(80.0, 110.0)
											EndIf
											ShowEntity(n\Collider)
											
											n\State2 = MilliSecs2() + 1000
											
											If Rand(5) = 1 Then n\State = 0.0
										EndIf	
										
										n\CurrSpeed = CurveValue(n\Speed * 0.5, n\CurrSpeed, 20.0)
									EndIf
									
									If (PrevFrame < 604.0 And n\Frame >= 604.0) Or (PrevFrame < 627.0 And n\Frame >= 627.0) Then
										PlaySound2(StepSFX(4, 0, Rand(0, 3)), Camera, n\Collider, 7.0, Rnd(0.5, 0.7))
									EndIf
									
									RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle,EntityYaw(n\Collider), 10.0), 0.0)
									
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * FPSfactor)
								EndIf
								;[End Block]
							Case 10 ; ~ Attacks
								;[Block]
								If chs\Notarget Then n\State = 0.0
								
								If n\LastSeen = 0.0 Then
									PlaySound2(LoadTempSound("SFX\SCP\966\Echo" + Rand(1, 3) + ".ogg"), Camera, n\Collider)
									n\LastSeen = 1.0
								EndIf
								
								If n\Frame > 557.0
									AnimateNPC(n, 628, 652, 0.25, False)
									If n\Frame > 651.0
										Select Rand(3)
											Case 1
												;[Block]
												SetNPCFrame(n, 458.0)
												;[End Block]
											Case 2
												;[Block]
												SetNPCFrame(n, 488.0)
												;[End Block]
											Case 3
												;[Block]
												SetNPCFrame(n, 518.0)
												;[End Block]
										End Select
									EndIf
								Else
									If n\Frame =< 487.0 Then
										AnimateNPC(n, 458.0, 487.0, 0.3, False)
										If n\Frame > 486.0 Then n\State = 8.0
									ElseIf n\Frame =< 517.0
										AnimateNPC(n, 488.0, 517.0, 0.3, False)
										If n\Frame > 516.0 Then n\State = 8.0
									ElseIf n\Frame =< 557.0
										AnimateNPC(n, 518.0, 557.0, 0.3, False)
										If n\Frame > 556.0 Then n\State = 8.0
									EndIf
								EndIf
								
								If n\Frame > 470.0 And PrevFrame =< 470.0 Or n\Frame > 500.0 And PrevFrame =< 500.0 Or n\Frame > 527.0 And PrevFrame =< 527.0 Then
									If Dist < 1.0 Then
										If (Abs(DeltaYaw(n\Collider, Collider)) =< 60.0) Then
											PlaySound2(DamageSFX(Rand(11, 12)), Camera, n\Collider)
											Injuries = Injuries + Rnd(0.5, 1.0)			
										EndIf
									Else
										PlaySound2(MissSFX, Camera, n\Collider, 2.35)
									EndIf	
								EndIf
								
								n\Angle = VectorYaw(EntityX(Collider) - EntityX(n\Collider), 0.0, EntityZ(Collider) - EntityZ(n\Collider))
								RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle, EntityYaw(n\Collider), 30.0), 0.0)
								;[End Block]
						End Select
					Else
						HideEntity(n\OBJ)
						If Rand(600) = 1 Then
							TeleportCloser(n)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case NPCtype1499
				;[Block]
				;n\State: Current State of the NPC
				;n\State2: A second state variable (dependend on the current NPC's n\State)
				;n\State3: Determines if the NPC will always be aggressive against the player
				;n\PrevState: Determines the type/behaviour of the NPC
				;	0 = Normal / Citizen
				;	1 = Stair guard / Guard next to king
				;	2 = King
				;	3 = Front guard
				
				PrevFrame# = n\Frame
				
				If (Not n\Idle) And EntityDistance(n\Collider,Collider)<HideDistance*3 Then
					If n\PrevState = 0 Then
						If n\State = 0 Or n\State = 2 Then
							For n2.NPCs = Each NPCs
								If n2\NPCtype = n\NPCtype And n2 <> n Then
									If n2\State <> 0 And n2\State <> 2 Then
										If n2\PrevState = 0 Then
											n\State = 1
											n\State2 = 0
											Exit
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					EndIf
					
					Select n\State
						Case 0
							;[Block]
							If n\PrevState=0 Then
								If n\CurrSpeed = 0.0 Then
									If n\Reload=0 Then
										If n\State2 < 500.0*Rnd(1,3) Then
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + FPSfactor
										Else
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									Else
										If n\State2 < 1500 Then
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + FPSfactor
										Else
											If n\Target <> Null Then
												If n\Target\Target <> Null Then
													n\Target\Target = Null
												EndIf
												n\Target\Reload = 0
												n\Target\Angle = n\Target\Angle+Rnd(-45,45)
												n\Target = Null
												n\Reload = 0
												n\Angle = n\Angle+Rnd(-45,45)
											EndIf
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									EndIf
								Else
									If n\Target<>Null Then
										n\State2 = 0.0
									EndIf
									
									If n\State2 < 10000.0*Rnd(1,3)
										n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,10.0)
										n\State2 = n\State2 + FPSfactor
									Else
										n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,50.0)
									EndIf
									
									RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
									
									If n\Target=Null Then
										If Rand(200) = 1 Then n\Angle = n\Angle + Rnd(-45,45)
										
										HideEntity n\Collider
										EntityPick(n\Collider, 1.5)
										If PickedEntity() <> 0 Then
											n\Angle = EntityYaw(n\Collider)+Rnd(80,110)
										EndIf
										ShowEntity n\Collider
									Else
										n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\Collider,n\Target\Collider)
										If EntityDistance(n\Collider,n\Target\Collider)<1.5 Then
											n\CurrSpeed = 0.0
											n\Target\CurrSpeed = 0.0
											n\Reload = 1
											n\Target\Reload = 1
											Temp = Rand(0,2)
											If Temp=0 Then
												SetNPCFrame(n,296)
											ElseIf Temp=1 Then
												SetNPCFrame(n,856)
											Else
												SetNPCFrame(n,905)
											EndIf
											Temp = Rand(0,2)
											If Temp=0 Then
												SetNPCFrame(n\Target,296)
											ElseIf Temp=1 Then
												SetNPCFrame(n\Target,856)
											Else
												SetNPCFrame(n\Target,905)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
							EndIf
							
							If n\CurrSpeed = 0.0
								If n\Reload = 0 And n\PrevState<>2 Then
									AnimateNPC(n,296,320,0.2)
								ElseIf n\Reload = 0 And n\PrevState=2 Then
									;509-533
									;534-601
									If n\Frame <= 532.5 Then
										AnimateNPC(n,509,533,0.2,False)
									ElseIf n\Frame > 533.5 And n\Frame <= 600.5 Then
										AnimateNPC(n,534,601,0.2,False)
									Else
										Temp = Rand(0,1)
										If Temp=0 Then
											SetNPCFrame(n,509)
										Else
											SetNPCFrame(n,534)
										EndIf
									EndIf
									;SetNPCFrame(n,855)
								Else
									If n\PrevState=2 Then
										AnimateNPC(n,713,855,0.2,False)
										If n\Frame > 833.5 Then
											PointEntity n\OBJ,Collider
											RotateEntity n\Collider,0,CurveAngle(n\Angle,EntityYaw(n\Collider),10.0),0
										EndIf
									ElseIf n\PrevState=1 Then
										AnimateNPC(n,602,712,0.2,False)
										If n\Frame > 711.5 Then
											n\Reload = 0
										EndIf
									Else
										If n\Frame <= 319.5 Then
											AnimateNPC(n,296,320,0.2,False)
										;856-904
										ElseIf n\Frame > 320.5 And  n\Frame < 903.5 Then
											AnimateNPC(n,856,904,0.2,False)
										;905-953
										ElseIf n\Frame > 904.5 And n\Frame < 952.5 Then
											AnimateNPC(n,905,953,0.2,False)
										Else
											Temp = Rand(0,2)
											If Temp=0 Then
												SetNPCFrame(n,296)
											ElseIf Temp=1 Then
												SetNPCFrame(n,856)
											Else
												SetNPCFrame(n,905)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
								If n\PrevState = 0 Then
									If n\Target = Null Then
										If Rand(1,1200)=1 Then
											For n2.NPCs = Each NPCs
												If n2<>n Then
													If n2\NPCtype=n\NPCtype Then
														If n2\Target = Null Then
															If n2\PrevState=0 Then
																If EntityDistance(n\Collider,n2\Collider)<20.0 Then
																	n\Target = n2
																	n2\Target = n
																	n\Target\CurrSpeed = n\Target\CurrSpeed + 0.0001
																	Exit
																EndIf
															EndIf
														EndIf
													EndIf
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							
							;randomly play the "screaming animation" and revert back to state 0
							If n\Target=Null And n\PrevState=0 Then
								If (Rand(5000)=1) Then
									n\State = 2
									n\State2 = 0
									
									If Not ChannelPlaying(n\SoundCHN) Then
										Dist = EntityDistance(n\Collider,Collider)
										If (Dist < 20.0) Then
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict("SFX\SCP\1499\Idle"+Rand(1,4)+".ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
										EndIf
									EndIf
								EndIf
								
								If (n\ID Mod 2 = 0) And (Not chs\NoTarget) Then
									Dist = EntityDistance(n\Collider,Collider)
									If Dist < 10.0 Then
										If EntityVisible(n\Collider,Collider) Then
											;play the "screaming animation"
											n\State = 2
											If Dist < 5.0 Then
												If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
												n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
												n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider,20.0)
												
												n\State2 = 1 ;if player is too close, switch to attack after screaming
												
												For n2.NPCs = Each NPCs
													;If n2\NPCtype = n\NPCtype And n2 <> n And (n\ID Mod 2 = 0) Then
													If n2\NPCtype = n\NPCtype And n2 <> n
														If n2\PrevState = 0 Then
															n2\State = 1
															n2\State2 = 0
														EndIf
													EndIf
												Next
											Else
												n\State2 = 0 ;otherwise keep idling
											EndIf
											
											n\Frame = 203
										EndIf
									EndIf
								EndIf
							ElseIf n\PrevState=1 Then
								Dist = EntityDistance(n\Collider,Collider)
								If (Not chs\NoTarget) Then
									If Dist < 4.0 Then
										If EntityVisible(n\Collider,Collider) Then
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider,20.0)
											
											n\State = 1
											
											n\Frame = 203
										EndIf
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 1 ;attacking the player
							;[Block]
							If chs\NoTarget Then n\State = 0
							
							If PlayerRoom\RoomTemplate\Name = "dimension1499" And n\PrevState=0 Then
								ShouldPlay = 19
							EndIf
							
							PointEntity n\OBJ,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),20.0),0
							
							Dist = EntityDistance(n\Collider,Collider)
							
							If n\State2 = 0.0
								If n\PrevState=1 Then
									n\CurrSpeed = CurveValue(n\Speed*2.0,n\CurrSpeed,10.0)
									If n\Target<>Null Then
										n\Target\State = 1
									EndIf
								Else
									n\CurrSpeed = CurveValue(n\Speed*1.75,n\CurrSpeed,10.0)
								EndIf
								
								If (n\ID Mod 2 = 0) Then
									AnimateNPC(n,1,62,(n\CurrSpeed*28))
								Else
									AnimateNPC(n,100,167,(n\CurrSpeed*28))
								EndIf
							EndIf
							
							If Dist < 0.75
								If (n\ID Mod 2 = 0) Or n\State3 = 1 Or n\PrevState=1 Or n\PrevState=3 Or n\PrevState=4 Then
									n\State2 = Rand(1,2)
									n\State = 3
									If n\State2 = 1
										SetNPCFrame(n,63)
									Else
										SetNPCFrame(n,168)
									EndIf
								Else
									n\State = 4
								EndIf
							EndIf
							;[End Block]
						Case 2 ;play the "screaming animation" and switch to n\state2 after it's finished
							;[Block]
							n\CurrSpeed = 0.0
							AnimateNPC(n,203,295,0.1,False)
							
							If n\Frame > 294.0 Then
								n\State = n\State2
							EndIf
							;[End Block]
						Case 3 ;slashing at the player
							;[Block]
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							Dist = EntityDistance(n\Collider,Collider)
							If n\State2 = 1
								AnimateNPC(n,63,100,0.6,False)
								If PrevFrame < 89 And n\Frame=>89
									If Dist > 0.85 Or Abs(DeltaYaw(n\Collider,Collider))>60.0
										;Miss
									Else
										Injuries = Injuries + Rnd(0.75,1.5)
										PlaySound2(LoadTempSound("SFX\General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If Injuries > 10.0
											Kill()
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class-B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [REDACTED]."
											Else
												DeathMSG = "An unidentified male and a deceased Class D subject were discovered in [REDACTED] by the Nine-Tailed Fox. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 99
									n\State2 = 0.0
									n\State = 1
								EndIf
							Else
								AnimateNPC(n,168,202,0.6,False)
								If PrevFrame < 189 And n\Frame=>189
									If Dist > 0.85 Or Abs(DeltaYaw(n\Collider,Collider))>60.0
										;Miss
									Else
										Injuries = Injuries + Rnd(0.75,1.5)
										PlaySound2(LoadTempSound("SFX\General\Slash"+Rand(1,2)+".ogg"), Camera, n\Collider)
										If Injuries > 10.0
											Kill()
											If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
												DeathMSG = "All personnel situated within Evacuation Shelter LC-2 during the breach have been administered "
												DeathMSG = DeathMSG + "Class-B amnestics due to Incident 1499-E. The Class D subject involved in the event "
												DeathMSG = DeathMSG + "died shortly after being shot by Agent [REDACTED]."
											Else
												DeathMSG = "An unidentified male and a deceased Class D subject were discovered in [REDACTED] by the Nine-Tailed Fox. "
												DeathMSG = DeathMSG + "The man was described as highly agitated and seemed to only speak Russian. "
												DeathMSG = DeathMSG + "He's been taken into a temporary holding area at [REDACTED] while waiting for a translator to arrive."
											EndIf
										EndIf
									EndIf
								ElseIf n\Frame => 201
									n\State2 = 0.0
									n\State = 1
								EndIf
							EndIf
							;[End Block]
						Case 4 ;standing in front of the player
							;[Block]
							Dist = EntityDistance(n\Collider,Collider)
							n\CurrSpeed = CurveValue(0.0,n\CurrSpeed,5.0)
							AnimateNPC(n,296,320,0.2)
							
							PointEntity n\OBJ,Collider
							RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),20.0),0
							
							If Dist > 0.85
								n\State = 1
							EndIf
							;[End Block]
					End Select
					
					If n\SoundCHN <> 0 And ChannelPlaying(n\SoundCHN) Then
						UpdateSoundOrigin(n\SoundCHN,Camera,n\Collider,20.0)
					EndIf
					
					MoveEntity n\Collider,0,0,n\CurrSpeed*FPSfactor
					
					RotateEntity n\OBJ,0,EntityYaw(n\Collider)-180,0
					PositionEntity n\OBJ,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
					
					ShowEntity n\OBJ
				Else
					HideEntity n\OBJ
				EndIf
				
				;[End Block]
			Case NPCtype008_1
				;[Block]
				;n\State: Main State
				;n\State2: A timer used for the player detection
				;n\State3: A timer for making the NPC idle (if the player escapes during that time)
				
				If (Not n\IsDead)
					If n\State = 0
						EntityType n\Collider,HIT_DEAD
					Else
						EntityType n\Collider,HIT_PLAYER
					EndIf
					
					PrevFrame = n\Frame
					
					n\BlinkTimer = 1
					
					Select n\State
						Case 0 ;Lying next to the wall
							SetNPCFrame(n,11)
						Case 1 ;Standing up
							AnimateNPC(n,11,32,0.1,False)
							If n\Frame => 29
								n\State = 2
							EndIf
						Case 2 ;Being active
							PlayerSeeAble = MeNPCSeesPlayer(n)
							If PlayerSeeAble=1 Or n\State2 > 0.0
								If PlayerSeeAble=1
									n\State2 = 70*2
								Else
									n\State2 = Max(n\State2-FPSfactor,0)
								EndIf
								PointEntity n\OBJ, Collider
								RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0
								
								AnimateNPC(n, 64, 93, n\CurrSpeed*30)
								n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
								
								If EntityDistance(n\Collider,Collider)<1.0
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										n\State = 3
									EndIf
								EndIf
								
								n\PathTimer = 0
								n\PathStatus = 0
								n\PathLocation = 0
								n\State3 = 0
							Else
								If n\PathStatus = 1
									If n\Path[n\PathLocation]=Null Then 
										If n\PathLocation > 19 Then 
											n\PathLocation = 0 : n\PathStatus = 0
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity n\OBJ, n\Path[n\PathLocation]\obj
										RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0
										
										AnimateNPC(n, 64, 93, n\CurrSpeed*30)
										n\CurrSpeed = CurveValue(n\Speed*0.7, n\CurrSpeed, 20.0)
										MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
										
										;opens doors in front of him
										Dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
										If Dist2 < 0.6 Then
											Temp = True
											If n\Path[n\PathLocation]\door <> Null Then
												If (Not n\Path[n\PathLocation]\door\IsElevatorDoor)
													If n\Path[n\PathLocation]\door\locked Or n\Path[n\PathLocation]\door\KeyCard>0 Or n\Path[n\PathLocation]\door\Code<>"" Then
														Temp = False
													Else
														If n\Path[n\PathLocation]\door\open = False Then UseDoor(n\Path[n\PathLocation]\door, False)
													EndIf
												EndIf
											EndIf
											If Dist2#<0.2 And Temp
												n\PathLocation = n\PathLocation + 1
											ElseIf Dist2#<0.5 And (Not Temp)
												n\PathStatus = 0
												n\PathTimer# = 0.0
											EndIf
										EndIf
										
										;If Rand(100)=3
										;	n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										;EndIf
									EndIf
								Else
									AnimateNPC(n, 323, 344, 0.2, True)
									n\CurrSpeed = 0
									If n\PathTimer < 70*5
										n\PathTimer = n\PathTimer + Rnd(1,2+(2*SelectedDifficulty\aggressiveNPCs))*FPSfactor
									Else
										n\PathStatus = FindPath(n,EntityX(Collider),EntityY(Collider),EntityZ(Collider))
										n\PathTimer = 0
									EndIf
								EndIf
								
								If EntityDistance(n\Collider,Collider)>HideDistance
									If n\State3 < 70*(15+(10*SelectedDifficulty\aggressiveNPCs))
										n\State3 = n\State3+FPSfactor
									Else
										DebugLog "SCP-008-1 IDLE"
										n\State3 = 70*(6*60)
										n\State = 4
									EndIf
								EndIf
							EndIf
							
							If n\CurrSpeed > 0.005 Then
								If (PrevFrame < 80 And n\Frame=>80) Or (PrevFrame > 92 And n\Frame<65)
									PlaySound2(StepSFX(0,0,Rand(0,7)),Camera, n\Collider, 8.0, Rnd(0.3,0.5))
								EndIf
							EndIf
							
							n\SoundCHN = LoopSound2(n\Sound,n\SoundCHN,Camera,n\Collider)
						Case 3 ;Attacking
							AnimateNPC(n, 126, 165, 0.4, False)
							If (n\Frame => 146 And PrevFrame < 146)
								If EntityDistance(n\Collider,Collider)<1.1
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										PlaySound_Strict DamageSFX(Rand(5,8))
										Injuries = Injuries+Rnd(0.4,1.0)
										Infect = Infect + (1+(1*SelectedDifficulty\aggressiveNPCs))
										DeathMSG = "Subject D-9341. Cause of death: multiple lacerations and severe blunt force trauma caused by [DATA EXPUNGED], who was infected with SCP-008. Said subject was located by Nine-Tailed Fox and terminated."
									EndIf
								EndIf
							ElseIf n\Frame => 164
								If EntityDistance(n\Collider,Collider)<1.1
									If (Abs(DeltaYaw(n\Collider,Collider))<=60.0)
										SetNPCFrame(n,126)
									Else
										n\State = 2
									EndIf
								Else
									n\State = 2
								EndIf
							EndIf
						Case 4 ;Idling
							HideEntity n\OBJ
							HideEntity n\Collider
							n\DropSpeed = 0
							PositionEntity n\Collider,0,500,0
							ResetEntity n\Collider
							If n\Idle > 0
								n\Idle = Max(n\Idle-(1+(1*SelectedDifficulty\aggressiveNPCs))*FPSfactor,0)
							Else
								If PlayerInReachableRoom() ;Player is in a room where SCP-008-1 can teleport to
									If Rand(50-(20*SelectedDifficulty\aggressiveNPCs))=1
										ShowEntity n\Collider
										ShowEntity n\OBJ
										For w.waypoints = Each WayPoints
											If w\door=Null And w\room\dist < HideDistance And Rand(3)=1 Then
												If EntityDistance(w\room\obj,n\Collider)<EntityDistance(Collider,n\Collider)
													x = Abs(EntityX(n\Collider)-EntityX(w\obj,True))
													If x < 12.0 And x > 4.0 Then
														z = Abs(EntityZ(n\Collider)-EntityZ(w\obj,True))
														If z < 12 And z > 4.0 Then
															If w\room\dist > 4
																DebugLog "MOVING 008-1 TO "+w\room\roomtemplate\name
																PositionEntity n\Collider, EntityX(w\obj,True), EntityY(w\obj,True)+0.25,EntityZ(w\obj,True)
																ResetEntity n\Collider
																n\PathStatus = 0
																n\PathTimer# = 0.0
																n\PathLocation = 0
																Exit
															EndIf
														EndIf
													EndIf
												EndIf
											EndIf
										Next
										n\State = 2
										n\State3 = 0
									EndIf
								EndIf
							EndIf
					End Select
				Else
					If n\SoundCHN <> 0
						StopChannel n\SoundCHN
						n\SoundCHN = 0
						FreeSound_Strict n\Sound
						n\Sound = 0
					EndIf
					AnimateNPC(n, 344, 363, 0.5, False)
				EndIf
				
				RotateEntity n\OBJ,0,EntityYaw(n\Collider)-180,0
				PositionEntity n\OBJ,EntityX(n\Collider),EntityY(n\Collider)-0.2,EntityZ(n\Collider)
				;[End Block]
		End Select
		
		If n\IsDead
			EntityType(n\Collider, HIT_DEAD)
		EndIf
		
		Local GravityDist# = Distance(EntityX(Collider), EntityZ(Collider), EntityX(n\Collider), EntityZ(n\Collider))
		
		If GravityDist < HideDistance * 0.7 Or n\NPCtype = NPCtype1499 Then
			If n\InFacility = InFacility
				TranslateEntity(n\Collider, 0.0, n\DropSpeed, 0.0)
				
				Local CollidedFloor% = False
				
				For i = 1 To CountCollisions(n\Collider)
					If CollisionY(n\Collider, i) < EntityY(n\Collider) - 0.01 Then CollidedFloor = True : Exit
				Next
				
				If CollidedFloor = True Then
					n\DropSpeed = 0.0
				Else
					If ShouldEntitiesFall Then
						Local UpdateGravity% = False
						Local MaxX#, MinX#, MaxZ#, MinZ#
						
						If n\InFacility = 1 Then
							If PlayerRoom\RoomTemplate\Name <> "173"
								For e.Events = Each Events
									If e\EventName = "room860"
										If e\EventState = 1.0
											UpdateGravity = True
											Exit
										EndIf
									EndIf
								Next
							Else
								UpdateGravity = True
							EndIf
							If (Not UpdateGravity) Then
								For r.Rooms = Each Rooms
									If r\MaxX <> 0.0 Or r\MinX <> 0.0 Or r\MaxZ <> 0.0 Or r\MinZ <> 0.0
										MaxX = r\MaxX
										MinX = r\MinX
										MaxZ = r\MaxZ
										MinZ = r\MinZ
									Else
										MaxX = 4.0
										MinX = 0.0
										MaxZ = 4.0
										MinZ = 0.0
									EndIf
									If Abs(EntityX(n\Collider) - EntityX(r\OBJ)) =< Abs(MaxX - MinX)
										If Abs(EntityZ(n\Collider) - EntityZ(r\OBJ)) =< Abs(MaxZ - MinZ)
											If r = PlayerRoom Then
												UpdateGravity = True
												Exit
											EndIf
											If IsRoomAdjacent(PlayerRoom, r) Then
												UpdateGravity = True
												Exit
											EndIf
											For i = 0 To 3
												If IsRoomAdjacent(PlayerRoom\Adjacent[i], r) Then
													UpdateGravity = True
													Exit
												EndIf
											Next
										EndIf
									EndIf
								Next
							EndIf
						Else
							UpdateGravity = True
						EndIf
						If UpdateGravity
							n\DropSpeed = Max(n\DropSpeed - 0.005 * FPSfactor * n\GravityMult, -n\MaxGravity)
						Else
							If n\FallingPickDistance > 0.0
								n\DropSpeed = 0.0
							Else
								n\DropSpeed = Max(n\DropSpeed - 0.005 * FPSfactor * n\GravityMult, -n\MaxGravity)
							EndIf
						EndIf
					Else
						n\DropSpeed = 0.0
					EndIf
				EndIf
			Else
				n\DropSpeed = 0.0
			EndIf
		Else
			n\DropSpeed = 0.0
		EndIf
		CatchErrors(Chr(34) + n\NVName + Chr(34) + " NPC")
	Next
	
	If MTF_CameraCheckTimer > 0.0 And MTF_CameraCheckTimer < 70 * 90.0 Then
		MTF_CameraCheckTimer = MTF_CameraCheckTimer + FPSfactor
	ElseIf MTF_CameraCheckTimer >= 70 * 90.0
		MTF_CameraCheckTimer = 0.0
		If (Not PlayerDetected) Then
			If MTF_CameraCheckDetected Then
				PlayAnnouncement("SFX\Character\MTF\AnnouncCameraFound" + Rand(1, 2) + ".ogg")
				PlayerDetected = True
				MTF_CameraCheckTimer = 70 * 60.0
			Else
				PlayAnnouncement("SFX\Character\MTF\AnnouncCameraNoFound.ogg")
			EndIf
		EndIf
		MTF_CameraCheckDetected = False
		If MTF_CameraCheckTimer = 0.0 Then
			PlayerDetected = False
		EndIf
	EndIf
End Function

Function TeleportCloser(n.NPCs)
	Local ClosestDist# = 0.0
	Local closestWaypoint.WayPoints
	Local w.WayPoints
	
	Local xTemp#, zTemp#
	
	For w.WayPoints = Each WayPoints
		If w\door = Null Then
			xTemp = Abs(EntityX(w\OBJ, True) - EntityX(n\Collider, True))
			If xTemp < 10.0 And xTemp > 1.0 Then 
				zTemp = Abs(EntityZ(w\OBJ, True) - EntityZ(n\Collider, True))
				If zTemp < 10.0 And zTemp > 1.0 Then
					If EntityDistance(Collider, w\OBJ) > 16.0 - (8.0 * SelectedDifficulty\AggressiveNPCs) Then
						; ~ Teleports to the nearby waypoint that takes it closest to the player
						Local NewDist# = EntityDistance(Collider, w\OBJ)
						
						If (NewDist < ClosestDist Or closestWaypoint = Null) Then
							ClosestDist = NewDist	
							closestWaypoint = w
						EndIf						
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	Local ShouldTeleport% = False
	
	If (closestWaypoint <> Null) Then
		If n\InFacility <> 1 Or SelectedDifficulty\AggressiveNPCs Then
			ShouldTeleport = True
		ElseIf EntityY(closestWaypoint\OBJ, True) =< 7.0 And EntityY(closestWaypoint\OBJ, True) >= -10.0 Then
			ShouldTeleport = True
		EndIf
		
		If ShouldTeleport Then
			PositionEntity(n\Collider, EntityX(closestWaypoint\OBJ, True), EntityY(closestWaypoint\OBJ, True) + 0.15, EntityZ(closestWaypoint\OBJ, True), True)
			ResetEntity(n\Collider)
			n\PathStatus = 0
			n\PathTimer = 0.0
			n\PathLocation = 0
		EndIf
	EndIf
End Function

Function OtherNPCSeesMeNPC%(me.NPCs, other.NPCs)
	If other\BlinkTimer =< 0.0 Then Return(False)
	
	If EntityDistance(other\Collider, me\Collider) < 6.0 Then
		If Abs(DeltaYaw(other\Collider, me\Collider)) < 60.0 Then
			Return(True)
		EndIf
	EndIf
	Return(False)
End Function

Function MeNPCSeesPlayer%(me.NPCs, DisableSoundOnCrouch% = False)
	; ~ Return values:
	; ~ False (= 0): Player is Not detected anyhow
	; ~ True (= 1): Player is detected by vision
	; ~ 2: Player is detected by emitting a sound
	; ~ 3: Player is detected by a camera (only for MTF Units!)
	; ~ 4: Player is detected through glass
	
	If chs\NoTarget Then Return(False)
	
	If (Not PlayerDetected) Or me\NPCtype <> NPCtypeMTF
		If me\BlinkTimer =< 0.0 Then Return(False)
		If EntityDistance(Collider, me\Collider) > (8.0 - CrouchState + PlayerSoundVolume) Then Return(False)
		
		; ~ Spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume > 1.0
			If (Abs(DeltaYaw(me\Collider, Collider)) > 60.0) And EntityVisible(me\Collider, Collider)
				Return(1)
			ElseIf (Not EntityVisible(me\Collider, Collider))
				If DisableSoundOnCrouch And Crouch Then
					Return(False)
				Else
					Return(2)
				EndIf
			EndIf
		Else
			If Abs(DeltaYaw(me\Collider, Collider)) > 60.0 Then Return(False)
		EndIf
		Return(EntityVisible(me\Collider, Collider))
	Else
		If EntityDistance(Collider, me\Collider) > (8.0 - CrouchState + PlayerSoundVolume) Then Return(3)
		If EntityVisible(me\Collider, Camera) Then Return(True)
		
		; ~ Spots the player if he's either in view or making a loud sound
		If PlayerSoundVolume > 1.0 Then Return(2)
		Return(3)
	EndIf
End Function

Function TeleportMTFGroup(n.NPCs)
	Local n2.NPCs
	
	If n\MTFLeader <> Null Then Return
	
	TeleportCloser(n)
	
	For n2 = Each NPCs
		If n2\NPCtype = NPCtypeMTF
			If n2\MTFLeader <> Null
				PositionEntity(n2\Collider, EntityX(n2\MTFLeader\Collider), EntityY(n2\MTFLeader\Collider) + 0.1, EntityZ(n2\MTFLeader\Collider))
			EndIf
		EndIf
	Next
End Function

Function UpdateMTFUnit(n.NPCs)
	;[Block]
	
	If n\NPCtype<>NPCtypeMTF Then
		Local realType$ = ""
		Select n\NPCtype
			Case NPCtype173
                realType = "173"
			Case NPCtype106
                realType = "106"
			Case NPCtypeGuard
                realType = "guard"
			Case NPCtypeD
                realType = "d"
			Case NPCtype372
                realType = "372"
			Case NPCtypeApache
                realType = "apache"
			Case NPCtype096
                realType = "096"
			Case NPCtype049
                realType = "049"
			Case NPCtype049_2
                realType = "049-2"
			Case NPCtype513_1
                realType = "513-1"
			Case NPCtype035_Tentacle
                realType = "035-tentacle"
			Case NPCtype860
                realType = "860"
			Case NPCtype939
                realType = "939"
			Case NPCtype066
                realType = "066"
			Case NPCtype966
                realType = "966"
			Case NPCtype1499
				realType = "1499-1"
		End Select
		RuntimeError "Called UpdateMTFUnit on "+realType
	EndIf
	;[End Block]
	
	Local x#,y#,z#
	Local r.Rooms
	Local prevDist#,newDist#
	Local n2.NPCs
	
	Local p.Particles, target, dist#, dist2#
	
	If n\IsDead Then
		n\BlinkTimer = -1.0
		SetNPCFrame(n, 532.0)
		If ChannelPlaying(n\SoundCHN2) Then
			StopChannel(n\SoundCHN2)
		EndIf
		Return
	EndIf
	
	n\MaxGravity = 0.03
	
	n\BlinkTimer = n\BlinkTimer - FPSfactor
	If n\BlinkTimer<=-5.0 Then 
		;only play the "blinking" sound clip if searching/containing 173
		If n\State = 2
			If OtherNPCSeesMeNPC(Curr173,n)
				PlayMTFSound(LoadTempSound("SFX\Character\MTF\173\BLINKING.ogg"),n)
			EndIf
		EndIf
		n\BlinkTimer = 70.0*Rnd(10.0,15.0)
	EndIf	
	
	n\Reload = n\Reload - FPSfactor
	
	Local prevFrame# = n\Frame
	
	n\BoneToManipulate = ""
	;n\BoneToManipulate2 = ""
	n\ManipulateBone = False
	n\ManipulationType = 0
	n\NPCNameInSection = "MTF"
	
	If Int(n\State) <> 1 Then n\PrevState = 0
	
	n\SoundCHN2 = LoopSound2(MTFSFX(6),n\SoundCHN2,Camera,n\Collider)
	
	If n\Idle>0.0 Then
		FinishWalking(n,488,522,0.015*26)
		n\Idle=n\Idle-FPSfactor
		If n\Idle<=0.0 Then n\Idle = 0.0
	Else
		Select Int(n\State) ;what is this MTF doing
			Case 0 ;wandering around
                ;[Block]
                n\Speed = 0.015
                If n\PathTimer<=0.0 Then ;update path
					If n\MTFLeader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						If Curr173\Idle<>2
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									x = r\x
									y = 0.1
									z = r\z
									DebugLog r\RoomTemplate\Name
									Exit
								EndIf
							Next
						Else
							Local tmp = False
							If EntityDistance(n\Collider,Curr173\Collider)>4.0
								If (Not EntityVisible(n\Collider,Curr173\Collider))
									tmp = True
								EndIf
							EndIf
							
							If (Not tmp)
								For r = Each Rooms
									If r\RoomTemplate\Name$ = "start"
										Local foundChamber% = False
										Local pvt% = CreatePivot()
										PositionEntity pvt%,EntityX(r\obj,True)+4736*RoomScale,0.5,EntityZ(r\obj,True)+1692*RoomScale
										
										If Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider))<3.5
											foundChamber% = True
											DebugLog Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider))
										EndIf
										
										If Curr173\Idle = 3 And Distance(EntityX(pvt%),EntityZ(pvt%),EntityX(n\Collider),EntityZ(n\Collider)) > 4.0
											If r\RoomDoors[1]\open = True Then UseDoor(r\RoomDoors[1],False)
										EndIf
										
										FreeEntity pvt%
										
										If Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And (Not foundChamber)
											x = EntityX(r\obj,True)+4736*RoomScale
											y = 0.1
											z = EntityZ(r\obj,True)+1692*RoomScale
											DebugLog "Move to 173's chamber"
											Exit
										ElseIf Distance(EntityX(n\Collider),EntityZ(n\Collider),EntityX(r\obj,True)+4736*RoomScale,EntityZ(r\obj,True)+1692*RoomScale)>1.6 And foundChamber
											n\PathX = EntityX(r\obj,True)+4736*RoomScale
											n\PathZ = EntityZ(r\obj,True)+1692*RoomScale
											DebugLog "Move inside 173's chamber"
											Exit
										Else
											Curr173\Idle = 3
											Curr173\Target = Null
											Curr173\IsDead = True
											If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
											n\Sound = LoadSound_Strict("SFX\Character\MTF\173\Cont"+Rand(1,4)+".ogg")
											PlayMTFSound(n\Sound, n)
											PlayAnnouncement("SFX\Character\MTF\Announc173Contain.ogg")
											DebugLog "173 contained"
											Exit
										EndIf
									EndIf
								Next
							Else
								x = EntityX(Curr173\Collider)
								y = 0.1
								z = EntityZ(Curr173\Collider)
								DebugLog "Going back to 173's cage"
							EndIf
						EndIf
						If n\PathX=0 Then n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTFLeader=Null) Then
					n\PathTimer=n\PathTimer-FPSfactor
					n\CurrSpeed = 0.0
					If Rand(1,35)=1 Then
						RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					EndIf
					FinishWalking(n,488,522,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
							
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									Local sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTFSFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-FPSfactor ;timer goes down slow
					ElseIf n\PathX#<>0.0
						pvt = CreatePivot()
						PositionEntity pvt,n\PathX#,0.5,n\PathZ#
						
						PointEntity n\Collider,pvt
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
						AnimateNPC(n,488, 522, n\CurrSpeed*26)
						
						If Distance(EntityX(n\Collider),EntityZ(n\Collider),n\PathX#,n\PathZ#)<0.2
							n\PathX# = 0.0
							n\PathZ# = 0.0
							n\PathTimer = 70.0 * Rnd(6.0,10.0)
						EndIf
						
						FreeEntity pvt
					Else
						n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
						If n\MTFLeader = Null Then
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTFLeader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTFLeader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						Else
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
                
				Local temp = MeNPCSeesPlayer(n)
				
				If chs\NoTarget Then temp = False
				
                If temp>False Then
					If n\LastSeen > 0 And n\LastSeen < 70*15 Then
						If temp < 2
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict("SFX\Character\MTF\ThereHeIs"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					Else
						If temp = True
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict("SFX\Character\MTF\Stop"+Rand(1,6)+".ogg")
							PlayMTFSound(n\Sound, n)
						ElseIf temp = 2
							;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							;n\Sound = MTFSFX(Rand(0,3))
							;PlayMTFSound(n\Sound, n)
							PlayMTFSound(MTFSFX(Rand(0,3)),n)
						EndIf
					EndIf
					
					n\LastSeen = (70*Rnd(30,40))
					n\LastDist = 1
					
					n\State = 1
					n\EnemyX = EntityX(Collider,True)
					n\EnemyY = EntityY(Collider,True)
					n\EnemyZ = EntityZ(Collider,True)
					n\State2 = 70.0*(15.0*temp) ;give up after 15 seconds (30 seconds if detected by loud noise, over camera: 45)
					DebugLog "player spotted :"+n\State2
					n\PathTimer=0.0
					n\PathStatus=0
					n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					
					;If EntityDistance(n\Collider,Collider)>HideDistance*0.7
					;	TeleportMTFGroup(n)
					;EndIf
                EndIf
				
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					Local SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then							
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
							DebugLog "173 spotted :"+n\State2
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict("SFX\Character\MTF\173\Spotted"+Rand(1,2)+".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
					EndIf
				EndIf
				
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							DebugLog "106 spotted :"+n\State2
							;If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\106\Spotted"+Rand(1,3)+".ogg")
								PlayMTFSound(n\Sound, n)
							;EndIf
						EndIf
					EndIf
				EndIf
				
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							DebugLog "096 spotted :"+n\State2
							;If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\096\Spotted"+Rand(1,2)+".ogg")
								PlayMTFSound(n\Sound, n)
							;EndIf
						EndIf
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								DebugLog "049 spotted :"+n\State2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Spotted"+Rand(1,5)+".ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								DebugLog "049-2 spotted :"+n\State2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Player0492_1.ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype008_1 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								DebugLog "008 spotted :"+n\State2
								;If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								;n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Player0492_1.ogg")
								;PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2, n) Then
							If EntityVisible(n\Collider, n2\Collider)
								n\State = 9.0
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70 * 5.0
								Exit
							EndIf
						EndIf
					EndIf
				Next
                ;[End Block]
			Case 1 ;searching for player
                ;[Block]
                n\Speed = 0.015
                n\State2=n\State2-FPSfactor
                If MeNPCSeesPlayer(n) = True Then
					
					;if close enough, start shooting at the player
					If playerDist < 4.0 Then
						
						Local angle# = VectorYaw(EntityX(Collider)-EntityX(n\Collider),0,EntityZ(Collider)-EntityZ(n\Collider))
						
						RotateEntity(n\Collider, 0, CurveAngle(angle, EntityYaw(n\Collider), 10.0), 0, True)
						n\Angle = EntityYaw(n\Collider)
						
						If n\Reload =< 0 And KillTimer = 0 Then
							If EntityVisible(n\Collider, Camera) Then
								angle# = WrapAngle(angle - EntityYaw(n\Collider))
								If angle < 5 Or angle > 355 Then 
									prev% = KillTimer
									
									PlaySound2(GunshotSFX, Camera, n\Collider, 15)
									
									pvt% = CreatePivot()
									
									RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
									PositionEntity(pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
									MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
									
									Shoot(EntityX(pvt),EntityY(pvt),EntityZ(pvt),5.0/playerDist, False)
									n\Reload = 7
									
									FreeEntity(pvt)
									
									DeathMSG="Subject D-9341. Died of blood loss after being shot by Nine-Tailed Fox."
									
									;player killed -> "target terminated"
									If prev => 0 And KillTimer < 0 Then
										DeathMSG="Subject D-9341. Terminated by Nine-Tailed Fox."
										PlayMTFSound(LoadTempSound("SFX\Character\MTF\Targetterminated"+Rand(1,4)+".ogg"),n)
									EndIf
								EndIf	
							EndIf
						EndIf
						
						For n2.NPCs = Each NPCs
							If n2\NPCtype = NPCtypeMTF And n2 <> n
								If n2\State = 0
									If EntityDistance(n\Collider,n2\Collider)<6.0
										n\PrevState = 1
										n2\LastSeen = (70*Rnd(30,40))
										n2\LastDist = 1
										
										n2\State = 1
										n2\EnemyX = EntityX(Collider,True)
										n2\EnemyY = EntityY(Collider,True)
										n2\EnemyZ = EntityZ(Collider,True)
										n2\State2 = n\State2
										n2\PathTimer=0.0
										n2\PathStatus=0
										n2\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
										n2\PrevState = 0
									EndIf
								EndIf
							EndIf
						Next
						
						If n\PrevState = 1
							SetNPCFrame(n,423)
							n\PrevState = 2
						ElseIf n\PrevState=2
							If n\Frame>200
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								AnimateNPC(n, 423, 463, 0.4, False)
								If n\Frame>462.9 Then n\Frame = 78
							Else
								AnimateNPC(n, 78, 193, 0.2, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						Else
							If n\Frame>958 Then
								AnimateNPC(n, 1374, 1383, 0.3, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
								If n\Frame>1382.9 Then n\Frame = 78
							Else
								AnimateNPC(n, 78, 193, 0.2, False)
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
							EndIf
						EndIf
					Else
						PositionEntity n\OBJ,n\EnemyX,n\EnemyY,n\EnemyZ,True
						PointEntity n\Collider,n\OBJ
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						
						n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
						AnimateNPC(n,488, 522, n\CurrSpeed*26)
					EndIf
                Else
					n\LastSeen = n\LastSeen - FPSfactor
					
					;n\Reload = 200-(100*SelectedDifficulty\aggressiveNPCs)
					If n\Reload <= 7
						n\Reload = 7
					EndIf
					
					If n\PathTimer<=0.0 Then ;update path
						n\PathStatus = FindPath(n,n\EnemyX,n\EnemyY+0.1,n\EnemyZ)
						n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
					ElseIf n\PathTimer<=70.0 * 2.5 Then
						n\PathTimer=n\PathTimer-FPSfactor
						n\CurrSpeed = 0.0
						If Rand(1,35)=1 Then
							RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
					Else
						If n\PathStatus=2 Then
							n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						ElseIf n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									;open the door and make it automatically close after 5 seconds
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-FPSfactor ;timer goes down slow
						Else
							PositionEntity n\OBJ,n\EnemyX,n\EnemyY,n\EnemyZ,True
							If (Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),n\EnemyX,n\EnemyZ)<0.2) Or (Not EntityVisible(n\OBJ,n\Collider)) Then
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								If Rand(1,35)=1 Then
									For wp.Waypoints = Each WayPoints
										If (Rand(1,3)=1) Then
											If (EntityDistance(wp\obj,n\Collider)<6.0) Then
												n\EnemyX = EntityX(wp\obj,True)
												n\EnemyY = EntityY(wp\obj,True)
												n\EnemyZ = EntityZ(wp\obj,True)
												n\PathTimer = 0.0
												Exit
											EndIf											
										EndIf
									Next
								EndIf
								n\PathTimer=n\PathTimer-FPSfactor ;timer goes down slow
							Else
								PointEntity n\Collider,n\OBJ
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
							EndIf
						EndIf
					EndIf
					
					If n\MTFLeader=Null And n\LastSeen<70*30 And n\LastSeen+FPSfactor=>70*30 Then
						If Rand(2)=1 Then 
							PlayMTFSound(LoadTempSound("SFX\Character\MTF\Searching"+Rand(1,6)+".ogg"),n)
						EndIf
					EndIf
					
					;If EntityDistance(n\Collider,Collider)>HideDistance*0.7
					;	TeleportMTFGroup(n)
					;EndIf
                EndIf
                
                If n\State2<=0.0 And n\State2+FPSfactor >0.0 Then
					If n\MTFLeader = Null Then
						DebugLog "targetlost: "+n\State2
						PlayMTFSound(LoadTempSound("SFX\Character\MTF\Targetlost"+Rand(1,3)+".ogg"),n)
						If MTF_CameraCheckTimer=0.0
							If Rand(15-(7*SelectedDifficulty\aggressiveNPCs))=1 ;Maybe change this to another chance - ENDSHN
								PlayAnnouncement("SFX\Character\MTF\AnnouncCameraCheck.ogg")
								MTF_CameraCheckTimer = FPSfactor
							EndIf
						EndIf
					EndIf
					n\State = 0
                EndIf
                
				;B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
                If Curr173\Idle<2 Then
					SoundVol173# = Max(Min((Distance(EntityX(Curr173\Collider), EntityZ(Curr173\Collider), Curr173\PrevX, Curr173\PrevZ) * 2.5), 1.0), 0.0)
					If OtherNPCSeesMeNPC(Curr173,n) Or (SoundVol173#>0.0 And EntityDistance(n\Collider,Curr173\Collider)<6.0) Then
						If EntityVisible(n\Collider,Curr173\Collider) Or SoundVol173#>0.0 Then	
							n\State = 2
							n\EnemyX = EntityX(Curr173\Collider,True)
							n\EnemyY = EntityY(Curr173\Collider,True)
							n\EnemyZ = EntityZ(Curr173\Collider,True)
							n\State2 = 70.0*15.0 ;give up after 15 seconds
							DebugLog "173 spotted :"+n\State2
							If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
							n\Sound = LoadSound_Strict("SFX\Character\MTF\173\Spotted3.ogg")
							PlayMTFSound(n\Sound, n)
							n\State3 = 0.0
							n\PathTimer=0.0
							n\PathStatus=0
						EndIf
					EndIf
				EndIf
				
				If Curr106\State <= 0
					If OtherNPCSeesMeNPC(Curr106,n) Or EntityDistance(n\Collider,Curr106\Collider)<3.0 Then
						If EntityVisible(n\Collider,Curr106\Collider) Then
							n\State = 4
							n\EnemyX = EntityX(Curr106\Collider,True)
							n\EnemyY = EntityY(Curr106\Collider,True)
							n\EnemyZ = EntityZ(Curr106\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							n\Target = Curr106
							DebugLog "106 spotted :"+n\State2
							If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\106\Spotted4.ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				
				If Curr096 <> Null
					If OtherNPCSeesMeNPC(Curr096,n) Then
						If EntityVisible(n\Collider,Curr096\Collider) Then
							n\State = 8
							n\EnemyX = EntityX(Curr096\Collider,True)
							n\EnemyY = EntityY(Curr096\Collider,True)
							n\EnemyZ = EntityZ(Curr096\Collider,True)
							n\State2 = 70*15.0
							n\State3 = 0.0
							n\PathTimer = 0.0
							n\PathStatus = 0
							DebugLog "096 spotted :"+n\State2
							If n\MTFLeader=Null
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\096\Spotted"+Rand(1,2)+".ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If n2\NPCtype = NPCtype049
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 4
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								DebugLog "049 spotted :"+n\State2
								If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
								n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Spotted"+Rand(1,5)+".ogg")
								PlayMTFSound(n\Sound, n)
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype049_2 And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2,n) Then
							If EntityVisible(n\Collider,n2\Collider)
								n\State = 9
								n\EnemyX = EntityX(n2\Collider,True)
								n\EnemyY = EntityY(n2\Collider,True)
								n\EnemyZ = EntityZ(n2\Collider,True)
								n\State2 = 70*15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70*5
								DebugLog "049-2 spotted :"+n\State2
								;If n\MTFLeader=Null
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Player0492_1.ogg")
									PlayMTFSound(n\Sound, n)
								;EndIf
								Exit
							EndIf
						EndIf
					ElseIf n2\NPCtype = NPCtype035_Tentacle And n2\IsDead = False
						If OtherNPCSeesMeNPC(n2, n) Then
							If EntityVisible(n\Collider, n2\Collider)
								n\State = 9.0
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\State2 = 70 * 15.0
								n\State3 = 0.0
								n\PathTimer = 0.0
								n\PathStatus = 0
								n\Target = n2
								n\Reload = 70 * 5.0
								Exit
							EndIf
						EndIf
					EndIf
				Next
				
                ;DebugLog Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),n\EnemyX,n\EnemyZ)
                
                ;[End Block]
			Case 2 ;searching for/looking at 173
                ;[Block]
                If Curr173\Idle = 2 Then
					n\State = 0
                Else
					For n2.NPCs = Each NPCs
						If n2<>n Then
							If n2\NPCtype = NPCtypeMTF Then
								n2\State = 2
							EndIf
						EndIf
					Next
					
					Local curr173Dist# = Distance(EntityX(n\Collider,True),EntityZ(n\Collider,True),EntityX(Curr173\Collider,True),EntityZ(Curr173\Collider,True))
					
					If curr173Dist<5.0 Then
						If Curr173\Idle <> 2 Then Curr173\Idle = True
						n\State2 = 70.0*15.0
						n\PathTimer = 0.0
						Local tempDist# = 1.0
						If n\MTFLeader<>Null Then tempDist = 2.0
						If curr173Dist<tempDist Then
							If n\MTFLeader = Null Then
								n\State3=n\State3+FPSfactor
								DebugLog "CONTAINING 173: "+n\State3
								;If n\State3>=70.0*10.0 Then
								If n\State3>=70.0*15.0 Then
									Curr173\Idle = 2
									If n\MTFLeader = Null Then Curr173\Target = n
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									n\Sound = LoadSound_Strict("SFX\Character\MTF\173\Box"+Rand(1,3)+".ogg")
									PlayMTFSound(n\Sound, n)
								EndIf
							EndIf
							PositionEntity n\OBJ,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\OBJ
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							FinishWalking(n,488,522,n\Speed*26)
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						Else
							PositionEntity n\OBJ,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True),EntityZ(Curr173\Collider,True),True
							PointEntity n\Collider,n\OBJ
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						EndIf
					Else
						If Curr173\Idle <> 2 Then Curr173\Idle = False
						If n\PathTimer<=0.0 Then ;update path
							n\PathStatus = FindPath(n,EntityX(Curr173\Collider,True),EntityY(Curr173\Collider,True)+0.1,EntityZ(Curr173\Collider,True))
							n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6 seconds
						ElseIf n\PathTimer<=70.0 * 2.5 Then
							n\PathTimer=n\PathTimer-FPSfactor
							n\CurrSpeed = 0.0
							If Rand(1,35)=1 Then
								RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						Else
							If n\PathStatus=2 Then
								n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
							ElseIf n\PathStatus=1 Then
								If n\Path[n\PathLocation]=Null Then
									If n\PathLocation > 19 Then
										n\PathLocation = 0 : n\PathStatus = 0
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									PointEntity n\Collider,n\Path[n\PathLocation]\obj
									RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
									n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
									RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
									
									n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
									
									TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
									AnimateNPC(n,488, 522, n\CurrSpeed*26)
									
									newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
									
									If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
										;open the door and make it automatically close after 5 seconds
										If (Not n\Path[n\PathLocation]\door\open)
											sound = 0
											If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
											PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
											PlayMTFSound(MTFSFX(5),n)
										EndIf
										n\Path[n\PathLocation]\door\open = True
										If n\Path[n\PathLocation]\door\MTFClose
											n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
										EndIf
									EndIf
									
									If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
								n\PathTimer=n\PathTimer-FPSfactor ;timer goes down slow
							Else
								n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
								n\CurrSpeed = 0.0
								If Rand(1,35)=1 Then
									RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
								EndIf
								FinishWalking(n,488,522,n\Speed*26)
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
							EndIf
						EndIf
					EndIf
                EndIf
                ;[End Block]
			Case 3 ;following a path
				;[Block]
				
				n\Angle = CurveValue(0,n\Angle,40.0)
				
				If n\PathStatus = 2 Then
					n\State = 5
					n\CurrSpeed = 0
				ElseIf n\PathStatus = 1
					If n\Path[n\PathLocation]=Null Then 
						If n\PathLocation > 19 Then 
							n\PathLocation = 0
							n\PathStatus = 0
							If n\LastSeen > 0 Then n\State = 5
						Else
							n\PathLocation = n\PathLocation + 1
						EndIf
					Else
						If n\Path[n\PathLocation]\door <> Null Then
							If n\Path[n\PathLocation]\door\open = False Then
								n\Path[n\PathLocation]\door\open = True
								n\Path[n\PathLocation]\door\timerstate = 8.0*70.0
								PlayMTFSound(MTFSFX(5),n)
							EndIf
						EndIf
						
						If dist < HideDistance*0.7 Then 
							dist2# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj) 
							
							;If Rand(5)=1 Then 
							;	For n2.NPCs = Each NPCs
							;		If n2\NPCtype = n\NPCtype And n2<>n Then
							;			If EntityDistance(n\Collider, n2\Collider)<2 Then
							;				n\Idle = 150
							;			EndIf
							;		EndIf
							;	Next
							;EndIf
							
							PointEntity n\OBJ, n\Path[n\PathLocation]\obj
							
							RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0
							If n\Idle = 0 Then
								n\CurrSpeed = CurveValue(n\Speed*Max(Min(dist2,1.0),0.1), n\CurrSpeed, 20.0)
								MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
								
								;If dist2 < (0.25+((n\Path[Min(n\PathLocation+1,19)]=Null)*0.3 * (n\ID Mod 3))) Then
								If EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)<0.5
									n\PathLocation = n\PathLocation + 1
								EndIf
							EndIf
						Else
							If Rand(20)=1 Then 
								PositionEntity n\Collider, EntityX(n\Path[n\PathLocation]\obj,True),EntityY(n\Path[n\PathLocation]\obj,True)+0.25,EntityZ(n\Path[n\PathLocation]\obj,True),True
								n\PathLocation = n\PathLocation + 1
								ResetEntity n\Collider
							EndIf
						EndIf
						
					EndIf
				Else
					n\CurrSpeed = 0
					n\State = 5
				EndIf
				
				
				If n\Idle = 0 And n\PathStatus = 1 Then
					If dist < HideDistance Then
						If n\Frame>959 Then
							AnimateNPC(n, 1376, 1383, 0.2, False)
							If n\Frame >1382.9 Then n\Frame = 488
						Else
							AnimateNPC(n, 488, 522, n\CurrSpeed*30)
						EndIf
					EndIf
				Else
					If dist < HideDistance Then
						If n\LastSeen > 0 Then 
							AnimateNPC(n, 78, 312, 0.2, True)
						Else
							If n\Frame<962 Then
								If n\Frame>487 Then n\Frame = 463
								AnimateNPC(n, 463, 487, 0.3, False)
								If n\Frame>486.9 Then n\Frame = 962
							Else
								AnimateNPC(n, 962, 1259, 0.3)
							EndIf
						EndIf
					EndIf
					
					n\CurrSpeed = CurveValue(0, n\CurrSpeed, 20.0)
				EndIf
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 4 ;SCP-106/049 detected
				;[Block]
				n\Speed = 0.03
                n\State2=n\State2-FPSfactor
				If n\State2 > 0.0
					If OtherNPCSeesMeNPC(n\Target,n)
						n\State2 = 70*15
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)>HideDistance
						If n\State2 > 70
							n\State2 = 70
						EndIf
					EndIf
					
					If EntityDistance(n\Target\Collider,n\Collider)<3.0 And n\State3 >= 0.0
						n\State3 = 70*5
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						PointEntity n\Collider,n\Target\Collider
						RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
						AnimateNPC(n,522, 488, n\CurrSpeed*26)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-FPSfactor,0)
						
						HideEntity n\Collider
						TurnEntity n\Collider,0,180,0
						EntityPick(n\Collider, 1.0)
						If PickedEntity() <> 0 Then
							n\State3 = -70*2
						EndIf
						ShowEntity n\Collider
						TurnEntity n\Collider,0,180,0
					ElseIf n\State3 < 0.0
						n\State3 = Min(n\State3+FPSfactor,0)
					EndIf
					
					If n\PathTimer<=0.0 Then
						If n\MTFLeader<>Null Then
							n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True))
						Else
							For r = Each Rooms
								If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
									If EntityDistance(r\obj,n\Target\Collider)>6.0
										x = r\x
										y = 0.1
										z = r\z
										DebugLog r\RoomTemplate\Name
										Exit
									EndIf
								EndIf
							Next
							n\PathStatus = FindPath(n,x,y,z)
						EndIf
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
								AnimateNPC(n,488, 522, n\CurrSpeed*26) ;Placeholder (until running animation has been implemented)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<2.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-FPSfactor
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				Else
					n\State = 0
				EndIf
				;[End Block]
			Case 5 ;looking at some other target than the player
				;[Block]
				target=CreatePivot()
				PositionEntity target, n\EnemyX, n\EnemyY, n\EnemyZ, True
				
				If dist<HideDistance Then
					AnimateNPC(n, 346, 351, 0.2, False)
				EndIf
				
				If Abs(EntityX(target)-EntityX(n\Collider)) < 55.0 And Abs(EntityZ(target)-EntityZ(n\Collider)) < 55.0 And Abs(EntityY(target)-EntityY(n\Collider))< 20.0 Then
					
					PointEntity n\OBJ, target
					RotateEntity n\Collider, 0, CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),30.0), 0, True
					
					If n\PathTimer = 0 Then
						n\PathStatus = EntityVisible(n\Collider,target)
						n\PathTimer = Rand(100,200)
					Else
						n\PathTimer = Min(n\PathTimer-FPSfactor,0.0)
					EndIf
					
					If n\PathStatus = 1 And n\Reload =< 0 Then
						dist# = Distance(EntityX(target),EntityZ(target),EntityX(n\Collider),EntityZ(n\Collider))
						
						;If dist<20.0 Then
						;	pvt = CreatePivot()
						;	
						;	PositionEntity pvt, EntityX(n\obj),EntityY(n\obj), EntityZ(n\obj)
						;	RotateEntity pvt, EntityPitch(n\Collider), EntityYaw(n\Collider),0
						;	MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
						;	
						;	If WrapAngle(EntityYaw(pvt)-EntityYaw(n\Collider))<5 Then
						;		PlaySound2(GunshotSFX, Camera, n\Collider, 20)
						;		p.Particles = CreateParticle(EntityX(n\obj, True), EntityY(n\obj, True), EntityZ(n\obj, True), 1, 0.2, 0.0, 5)
						;		PositionEntity(p\pvt, EntityX(pvt), EntityY(pvt), EntityZ(pvt))
						;		
						;		n\Reload = 7
						;	EndIf
						;	
						;	FreeEntity pvt
						;EndIf
					EndIf
				EndIf		
				
				FreeEntity target
				
				n\Angle = EntityYaw(n\Collider)
				;[End Block]
			Case 6 ;seeing the player as a 049-2 instance
				;[Block]
				
				PointEntity n\OBJ,Collider
				RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),20.0),0
				n\Angle = EntityYaw(n\Collider)
				
				AnimateNPC(n, 346, 351, 0.2, False)
				
				If n\Reload =< 0 And KillTimer = 0 Then
					If EntityVisible(n\Collider, Collider) Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(GunshotSFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							Shoot(EntityX(pvt),EntityY(pvt),EntityZ(pvt),0.9, False)
							n\Reload = 7
							
							FreeEntity(pvt)
							
							;If prev => 0 And KillTimer < 0 Then
								;DeathMSG="Subject D-9341. Terminated by Nine-Tailed Fox."
								;If n\MTFLeader = Null Then PlayMTFSound(LoadTempSound("SFX\Character\MTF\049\Player0492_2.ogg"),n)
							;EndIf
						EndIf	
					EndIf
				EndIf
				
				;[End Block]
			Case 7 ;just shooting
				;[Block]
				AnimateNPC(n, 346, 351, 0.2, False)
				
				RotateEntity n\Collider,0,CurveAngle(n\State2,EntityYaw(n\Collider),20),0
				n\Angle = EntityYaw(n\Collider)
				
				If n\Reload =< 0 
					LightVolume = TempLightVolume*1.2
					PlaySound2(GunshotSFX, Camera, n\Collider, 20)
					
					pvt% = CreatePivot()
					
					RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
					PositionEntity(pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
					MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
					
					p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
					TurnEntity p\obj, 0,0,Rnd(360)
					p\Achange = -0.15
					
					FreeEntity(pvt)
					n\Reload = 7
				End If
				;[End Block]
			Case 8 ;SCP-096 spotted
				;[Block]
				n\Speed = 0.015
				n\BoneToManipulate = "head"
				n\ManipulateBone = True
				n\ManipulationType = 2
                If n\PathTimer<=0.0 Then ;update path
					If n\MTFLeader<>Null Then ;i'll follow the leader
						n\PathStatus = FindPath(n,EntityX(n\MTFLeader\Collider,True),EntityY(n\MTFLeader\Collider,True)+0.1,EntityZ(n\MTFLeader\Collider,True)) ;whatever you say boss
					Else ;i am the leader
						For r = Each Rooms
							If ((Abs(r\x-EntityX(n\Collider,True))>12.0) Or (Abs(r\z-EntityZ(n\Collider,True))>12.0)) And (Rand(1,Max(4-Int(Abs(r\z-EntityZ(n\Collider,True)/8.0)),2))=1) Then
								x = r\x
								y = 0.1
								z = r\z
								DebugLog r\RoomTemplate\Name
								Exit
							EndIf
						Next
						n\PathStatus = FindPath(n,x,y,z) ;we're going to this room for no particular reason
					EndIf
					If n\PathStatus = 1 Then
						While n\Path[n\PathLocation]=Null
							If n\PathLocation>19 Then Exit
							n\PathLocation=n\PathLocation+1
						Wend
						If n\PathLocation<19 Then
							If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
								If (n\Path[n\PathLocation]\door=Null) Then
									If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
										n\PathLocation=n\PathLocation+1
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0,10.0) ;search again after 6-10 seconds
                ElseIf (n\PathTimer<=70.0 * 2.5) And (n\MTFLeader=Null) Then
					n\PathTimer=n\PathTimer-FPSfactor
					n\CurrSpeed = 0.0
					;If Rand(1,35)=1 Then
					;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
					;EndIf
					FinishWalking(n,488,522,n\Speed*26)
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
                Else
					If n\PathStatus=2 Then
						n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
						n\CurrSpeed = 0.0
						;If Rand(1,35)=1 Then
						;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
						;EndIf
						FinishWalking(n,488,522,n\Speed*26)
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
					ElseIf n\PathStatus=1 Then
						If n\Path[n\PathLocation]=Null Then
							If n\PathLocation > 19 Then
								n\PathLocation = 0 : n\PathStatus = 0
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							PointEntity n\Collider,n\Path[n\PathLocation]\obj
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
							RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							;MoveEntity n\Collider, 0, 0, n\CurrSpeed * FPSfactor
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
							
							newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
							
							If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
								;open the door and make it automatically close after 5 seconds
								If (Not n\Path[n\PathLocation]\door\open)
									sound = 0
									If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
									PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
									PlayMTFSound(MTFSFX(5),n)
								EndIf
								n\Path[n\PathLocation]\door\open = True
								If n\Path[n\PathLocation]\door\MTFClose
									n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
								EndIf
							EndIf
                            
							If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
								n\PathLocation=n\PathLocation+1
							EndIf
						EndIf
						n\PathTimer=n\PathTimer-FPSfactor ;timer goes down slow
					Else
						n\PathTimer=n\PathTimer-(FPSfactor*2.0) ;timer goes down fast
						If n\MTFLeader = Null Then
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						ElseIf EntityDistance(n\Collider,n\MTFLeader\Collider)>1.0 Then
							PointEntity n\Collider,n\MTFLeader\Collider
							RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
							
							n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
							TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
							AnimateNPC(n,488, 522, n\CurrSpeed*26)
						Else
							;If Rand(1,35)=1 Then
							;	RotateEntity n\Collider,0.0,Rnd(360.0),0.0,True
							;EndIf
							FinishWalking(n,488,522,n\Speed*26)
							n\CurrSpeed = 0.0
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
						RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
					EndIf
                EndIf
				
				If (Not EntityVisible(n\Collider,Curr096\Collider)) Or EntityDistance(n\Collider,Curr096\Collider)>6.0
					n\State = 0
				EndIf
				;[End Block]
			Case 9 ;SCP-049-2/008 spotted
				;[Block]
				If EntityVisible(n\Collider, n\Target\Collider) Then
					PointEntity n\OBJ,n\Target\Collider
					RotateEntity n\Collider,0,CurveAngle(EntityYaw(n\OBJ),EntityYaw(n\Collider),20.0),0
					n\Angle = EntityYaw(n\Collider)
					
					If EntityDistance(n\Target\Collider,n\Collider)<1.3
						n\State3 = 70*2
					EndIf
					
					If n\State3 > 0.0
						n\PathStatus = 0
						n\PathLocation = 0
						n\Speed = 0.02
						n\CurrSpeed = CurveValue(-n\Speed,n\CurrSpeed,20.0)
						TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
						AnimateNPC(n,522, 488, n\CurrSpeed*26)
						
						n\PathTimer = 1.0
						
						n\State3=Max(n\State3-FPSfactor,0)
					Else
						n\State3 = 0
						AnimateNPC(n, 346, 351, 0.2, False)
					EndIf
					If n\Reload =< 0 And n\Target\IsDead = False Then
						;angle# = WrapAngle(angle - EntityYaw(n\Collider))
						;If angle < 5 Or angle > 355 Then
						If (Abs(DeltaYaw(n\Collider,n\Target\Collider))<50.0)
							;prev% = KillTimer
							
							PlaySound2(GunshotSFX, Camera, n\Collider, 15)
							
							pvt% = CreatePivot()
							
							RotateEntity(pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0, True)
							PositionEntity(pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							MoveEntity (pvt,0.8*0.079, 10.75*0.079, 6.9*0.079)
							
							p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 1, Rnd(0.08,0.1), 0.0, 5)
							TurnEntity p\obj, 0,0,Rnd(360)
							p\Achange = -0.15
							If n\Target\HP > 0
								n\Target\HP = Max(n\Target\HP-Rand(5,10),0)
							Else
								If (Not n\Target\IsDead)
									If n\Sound <> 0 Then FreeSound_Strict n\Sound : n\Sound = 0
									If n\NPCtype = NPCtype049_2
										n\Sound = LoadSound_Strict("SFX\Character\MTF\049\Player0492_2.ogg")
										PlayMTFSound(n\Sound, n)
									Else
										;Still needs to be added! (for 008)
									EndIf
								EndIf
								SetNPCFrame(n\Target,133)
								n\Target\IsDead = True
								n\Target = Null
								n\State = 0
								Return
							EndIf
							n\Reload = 7
							
							FreeEntity(pvt)
						EndIf	
					EndIf
					n\PathStatus = 0
				Else
					If n\PathTimer<=0.0 Then
						n\PathStatus = FindPath(n,EntityX(n\Target\Collider),EntityY(n\Target\Collider),EntityZ(n\Target\Collider))
						If n\PathStatus = 1 Then
							While n\Path[n\PathLocation]=Null
								If n\PathLocation>19 Then Exit
								n\PathLocation=n\PathLocation+1
							Wend
							If n\PathLocation<19 Then
								If (n\Path[n\PathLocation]<>Null) And (n\Path[n\PathLocation+1]<>Null) Then
									If (n\Path[n\PathLocation]\door=Null) Then
										If Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation]\obj))>Abs(DeltaYaw(n\Collider,n\Path[n\PathLocation+1]\obj)) Then
											n\PathLocation=n\PathLocation+1
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						n\PathTimer = 70*10
					Else
						If n\PathStatus=1 Then
							If n\Path[n\PathLocation]=Null Then
								If n\PathLocation > 19 Then
									n\PathLocation = 0 : n\PathStatus = 0
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								prevDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								PointEntity n\Collider,n\Path[n\PathLocation]\obj
								RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
								n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
								RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
								
								n\CurrSpeed = CurveValue(n\Speed,n\CurrSpeed,20.0)
								TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90.0)*n\CurrSpeed * FPSfactor, True
								AnimateNPC(n,488, 522, n\CurrSpeed*26)
								
								newDist# = EntityDistance(n\Collider,n\Path[n\PathLocation]\obj)
								
								If (newDist<1.0 And n\Path[n\PathLocation]\door<>Null) Then
									If (Not n\Path[n\PathLocation]\door\open)
										sound = 0
										If n\Path[n\PathLocation]\door\dir = 1 Then sound = 0 Else sound=Rand(0, 2)
										PlaySound2(OpenDoorSFX(n\Path[n\PathLocation]\door\dir,sound),Camera,n\Path[n\PathLocation]\door\obj)
										PlayMTFSound(MTFSFX(5),n)
									EndIf
									n\Path[n\PathLocation]\door\open = True
									If n\Path[n\PathLocation]\door\MTFClose
										n\Path[n\PathLocation]\door\timerstate = 70.0*5.0
									EndIf
								EndIf
								
								If (newDist<0.2) Or ((prevDist<newDist) And (prevDist<1.0)) Then
									n\PathLocation=n\PathLocation+1
								EndIf
							EndIf
							n\PathTimer=n\PathTimer-FPSfactor
						Else
							n\PathTimer=0.0
						EndIf
					EndIf
				EndIf
				
				If n\Target\IsDead = True Then
					n\Target = Null
					n\State = 0
				EndIf
				
				;[End Block]
		End Select
		
		If n\CurrSpeed > 0.01 Then
			If prevFrame > 500 And n\Frame<495
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
			ElseIf prevFrame < 505 And n\Frame=>505
				PlaySound2(StepSFX(2,0,Rand(0,2)),Camera, n\Collider, 8.0, Rnd(0.5,0.7))
			EndIf
		EndIf
		
		If chs\NoTarget And n\State = 1 Then n\State = 0
		
		If n\State <> 3 And n\State <> 5 And n\State <> 6 And n\State <> 7
			If n\MTFLeader<>Null Then
				If EntityDistance(n\Collider,n\MTFLeader\Collider)<0.7 Then
					PointEntity n\Collider,n\MTFLeader\Collider
					RotateEntity n\Collider,0.0,EntityYaw(n\Collider,True),0.0,True
					n\Angle = CurveAngle(EntityYaw(n\Collider,True),n\Angle,20.0)
					
					TranslateEntity n\Collider, Cos(EntityYaw(n\Collider,True)-45)* 0.01 * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)-45)* 0.01 * FPSfactor, True
				EndIf
			Else
				For n2.NPCs = Each NPCs
					If n2<>n And n2\IsDead=False Then
						If Abs(DeltaYaw(n\Collider,n2\Collider))<80.0 Then
							If EntityDistance(n\Collider,n2\Collider)<0.7 Then							
								TranslateEntity n2\Collider, Cos(EntityYaw(n\Collider,True)+90)* 0.01 * FPSfactor, 0, Sin(EntityYaw(n\Collider,True)+90)* 0.01 * FPSfactor, True
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		;teleport back to the facility if fell through the floor
		If n\State <> 6 And n\State <> 7
			If (EntityY(n\Collider) < -10.0) Then
				TeleportCloser(n)
			EndIf
		EndIf
		
		RotateEntity n\OBJ,-90.0,n\Angle,0.0,True
		
		PositionEntity n\OBJ,EntityX(n\Collider,True),EntityY(n\Collider,True)-0.15,EntityZ(n\Collider,True),True
		
	EndIf
End Function

Function Shoot(x#, y#, z#, hitProb# = 1.0, particles% = True, instaKill% = False)
	
	;muzzle flash
	Local p.particles = CreateParticle(x,y,z, 1, Rnd(0.08,0.1), 0.0, 5)
	TurnEntity p\obj, 0,0,Rnd(360)
	p\Achange = -0.15
	
	LightVolume = TempLightVolume*1.2
	
	
	If instaKill Then Kill() : PlaySound_Strict BullethitSFX : Return
	
	If Rnd(1.0) =< hitProb Then
		TurnEntity Camera, Rnd(-3,3), Rnd(-3,3), 0
		
		Local ShotMessageUpdate$
		If WearingVest>0 Then
			If WearingVest = 1 Then
				Select Rand(8)
					Case 1,2,3,4,5
						BlurTimer = 500
						Stamina = 0
						ShotMessageUpdate = "A bullet penetrated your vest, making you gasp."
						Injuries = Injuries + Rnd(0.1,0.5)
					Case 6
						BlurTimer = 500
						ShotMessageUpdate = "A bullet hit your left leg."
						Injuries = Injuries + Rnd(0.8,1.2)
					Case 7
						BlurTimer = 500
						ShotMessageUpdate = "A bullet hit your right leg."
						Injuries = Injuries + Rnd(0.8,1.2)
					Case 8
						BlurTimer = 500
						Stamina = 0
						ShotMessageUpdate = "A bullet struck your neck, making you gasp."
						Injuries = Injuries + Rnd(1.2,1.6)
				End Select	
			Else
				If Rand(10)=1 Then
					BlurTimer = 500
					Stamina = Stamina - 1
					ShotMessageUpdate = "A bullet hit your chest. The vest absorbed some of the damage."
					Injuries = Injuries + Rnd(0.8,1.1)
				Else
					ShotMessageUpdate = "A bullet hit your chest. The vest absorbed most of the damage."
					Injuries = Injuries + Rnd(0.1,0.5)
				EndIf
			EndIf
			
			If Injuries >= 5
				If Rand(3) = 1 Then Kill()
			EndIf
		Else
			Select Rand(6)
				Case 1
					Kill()
				Case 2
					BlurTimer = 500
					ShotMessageUpdate = "A bullet hit your left leg."
					Injuries = Injuries + Rnd(0.8,1.2)
				Case 3
					BlurTimer = 500
					ShotMessageUpdate = "A bullet hit your right leg."
					Injuries = Injuries + Rnd(0.8,1.2)
				Case 4
					BlurTimer = 500
					ShotMessageUpdate = "A bullet hit your right shoulder."
					Injuries = Injuries + Rnd(0.8,1.2)	
				Case 5
					BlurTimer = 500
					ShotMessageUpdate = "A bullet hit your left shoulder."
					Injuries = Injuries + Rnd(0.8,1.2)	
				Case 6
					BlurTimer = 500
					ShotMessageUpdate = "A bullet hit your right shoulder."
					Injuries = Injuries + Rnd(2.5,4.0)
			End Select
		EndIf
		
		;Only updates the message if it's been more than two seconds.
		If (MsgTimer < 64*4) Then
			Msg = ShotMessageUpdate
			MsgTimer = 70*6
		EndIf

		Injuries = Min(Injuries, 4.0)
		
		;Kill()
		PlaySound_Strict BullethitSFX
	ElseIf particles And ParticleAmount>0
		pvt = CreatePivot()
		PositionEntity pvt, EntityX(Collider),(EntityY(Collider)+EntityY(Camera))/2,EntityZ(Collider)
		PointEntity pvt, p\obj
		TurnEntity pvt, 0, 180, 0
		
		EntityPick(pvt, 2.5)
		
		If PickedEntity() <> 0 Then 
			PlaySound2(Gunshot3SFX, Camera, pvt, 0.4, Rnd(0.8,1.0))
			
			If particles Then 
				;dust/smoke particles
				p.particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.03, 0, 80)
				p\speed = 0.001
				p\SizeChange = 0.003
				p\A = 0.8
				p\Achange = -0.01
				RotateEntity p\pvt, EntityPitch(pvt)-180, EntityYaw(pvt),0
				
				For i = 0 To Rand(2,3)
					p.particles = CreateParticle(PickedX(),PickedY(),PickedZ(), 0, 0.006, 0.003, 80)
					p\speed = 0.02
					p\A = 0.8
					p\Achange = -0.01
					RotateEntity p\pvt, EntityPitch(pvt)+Rnd(170,190), EntityYaw(pvt)+Rnd(-10,10),0	
				Next
				
				;bullet hole decal
				Local de.Decals = CreateDecal(Rand(13,14), PickedX(),PickedY(),PickedZ(), 0,0,0)
				AlignToVector de\obj,-PickedNX(),-PickedNY(),-PickedNZ(),3
				MoveEntity de\obj, 0,0,-0.001
				EntityFX de\obj, 1
				de\lifetime = 70*20
				EntityBlend de\obj, 2
				de\Size = Rnd(0.028,0.034)
				ScaleSprite de\obj, de\Size, de\Size
			EndIf				
		EndIf
		FreeEntity pvt
	EndIf
End Function

Function PlayMTFSound(Sound%, n.NPCs)
	If n <> Null Then
		n\SoundCHN = PlaySound2(Sound, Camera, n\Collider, 8.0)	
	EndIf
	
	If SelectedItem <> Null Then
		If SelectedItem\State2 = 3.0 And SelectedItem\State > 0.0 Then 
			Select SelectedItem\ItemTemplate\TempName 
				Case "radio", "fineradio", "18vradio"
					;[Block]
					If Sound <> MTFSFX(5) Or ChannelPlaying(RadioCHN(3)) = False Then
						If RadioCHN(3) <> 0 Then StopChannel(RadioCHN(3))
						RadioCHN(3) = PlaySound_Strict(Sound)
					EndIf
					;[End Block]
			End Select
		EndIf
	EndIf 
End Function

Function MoveToPocketDimension()
	Local r.Rooms
	
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "pocketdimension" Then
			FallTimer = 0.0
			UpdateDoors()
			UpdateRooms()
			ShowEntity(Collider)
			PlaySound_Strict(Use914SFX)
			PlaySound_Strict(OldManSFX(5))
			PositionEntity(Collider, EntityX(r\OBJ), 0.8, EntityZ(r\OBJ))
			DropSpeed = 0.0
			ResetEntity(Collider)
			
			BlinkTimer = -10.0
			
			Injuries = Injuries + 0.5
			
			PlayerRoom = r
			
			Return
		EndIf
	Next		
End Function

Function FindFreeNPCID%()
	Local ID% = 1
	
	While True
		Local Taken% = False
		
		For n2.NPCs = Each NPCs
			If n2\ID = ID Then
				Taken = True
				Exit
			EndIf
		Next
		If (Not Taken) Then
			Return(ID)
		EndIf
		ID = ID + 1
	Wend
End Function

Function ForceSetNPCID(n.NPCs, NewID%)
	n\ID = NewID
	
	For n2.NPCs = Each NPCs
		If n2 <> n And n2\ID = NewID Then
			n2\ID = FindFreeNPCID()
		EndIf
	Next
End Function

Function Find860Angle(n.NPCs, fr.Forest)
	TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0, fr\Forest_Pivot)
	
	Local PlayerX# = Floor((TFormedX() + 6.0) / 12.0)
	Local PlayerZ# = Floor((TFormedZ() + 6.0) / 12.0)
	
	TFormPoint(EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider), 0, fr\Forest_Pivot)
	
	Local x# = (TFormedX() + 6.0) / 12.0
	Local z# = (TFormedZ() + 6.0) / 12.0
	
	Local xt% = Floor(x), zt% = Floor(z)
	
	Local x2%, z2%
	
	If xt <> PlayerX Or zt <> PlayerZ Then ; ~ The monster is not on the same tile as the player
		For x2 = Max(xt - 1, 0) To Min(xt + 1, GridSize - 1)
			For z2 = Max(zt - 1, 0) To Min(zt + 1, GridSize - 1)
				If fr\grid[(z2 * GridSize) + x2] > 0 And (x2 <> xt Or z2 <> zt) And (x2 = xt Or z2 = zt) Then
					
					; ~ Tile (x2, z2) is closer to the player than the monsters current tile
					If (Abs(PlayerX - x2) + Abs(PlayerZ - z2)) < (Abs(PlayerX - xt) + Abs(PlayerZ - zt)) Then
						; ~ Calculate the position of the tile in world coordinates
						TFormPoint(x2 * 12.0, 0.0, z2 * 12.0, fr\Forest_Pivot, 0)
						Return(point_direction(EntityX(n\Collider), EntityZ(n\Collider), TFormedX(), TFormedZ()) + 180)
					EndIf
				EndIf
			Next
		Next
	Else
		Return(point_direction(EntityX(n\Collider), EntityZ(n\Collider), EntityX(Collider), EntityZ(Collider)) + 180)
	EndIf		
End Function

Function Console_SpawnNPC(C_Input$, C_State$ = "")
	Local n.NPCs
	Local ConsoleMsg$
	
	Select C_Input$ 
		Case "008", "008zombie", "008-1", "infectedhuman", "humaninfected", "scp008-1", "scp-008-1", "scp0081", "0081", "scp-0081"
			;[Block]
			n.NPCs = CreateNPC(NPCtype008_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1.0
			ConsoleMsg = "SCP-008 infected human spawned."
			;[End Block]
		Case "049", "scp049", "scp-049", "plaguedoctor"
			;[Block]
			n.NPCs = CreateNPC(NPCtype049, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1.0
			ConsoleMsg = "SCP-049 spawned."
			;[End Block]
		Case "049-2", "0492", "scp-049-2", "scp049-2", "049zombie", "curedhuman", "scp0492", "scp-0492"
			;[Block]
			n.NPCs = CreateNPC(NPCtype049_2, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 1.0
			ConsoleMsg = "SCP-049-2 spawned."
			;[End Block]
		Case "066", "scp066", "scp-066", "eric"
			;[Block]
			n.NPCs = CreateNPC(NPCtype066, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "SCP-066 spawned."
			;[End Block]
		Case "096", "scp096", "scp-096", "shyguy"
			;[Block]
			n.NPCs = CreateNPC(NPCtype096, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			n\State = 5.0
			If (Curr096 = Null) Then Curr096 = n
			ConsoleMsg = "SCP-096 spawned."
			;[End Block]
		Case "106", "scp106", "scp-106", "larry", "oldman"
			;[Block]
			n.NPCs = CreateNPC(NPCtype106, EntityX(Collider), EntityY(Collider) - 0.5, EntityZ(Collider))
			n\State = -1.0
			ConsoleMsg = "SCP-106 spawned."
			;[End Block]
		Case "173", "scp173", "scp-173", "statue", "sculpture"
			;[Block]
			n.NPCs = CreateNPC(NPCtype173, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			Curr173 = n
			If Curr173\Idle = 3 Then Curr173\Idle = False
			ConsoleMsg = "SCP-173 spawned."
			;[End Block]
		Case "372", "scp372", "scp-372", "pj", "jumper"
			;[Block]
			n.NPCs = CreateNPC(NPCtype372, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "SCP-372 spawned."
			;[End Block]
		Case "513-1", "5131", "scp513-1", "scp-513-1", "bll", "scp-5131", "scp5131"
			;[Block]
			n.NPCs = CreateNPC(NPCtype513_1, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "SCP-513-1 spawned."
			;[End Block]
		Case "860-2", "8602", "scp860-2", "scp-860-2", "forestmonster", "scp8602"
			;[Block]
			CreateConsoleMsg("SCP-860-2 cannot be spawned with the console. Sorry!", 255, 0, 0)
			;[End Block]
		Case "939", "scp939", "scp-939"
			CreateConsoleMsg("SCP-939 instances cannot be spawned with the console. Sorry!", 255, 0, 0)
			;[End Block]
		Case "966", "scp966", "scp-966", "sleepkiller"
			;[Block]
			n.NPCs = CreateNPC(NPCtype966, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "SCP-966 instance spawned."
			;[End Block]
		Case "1048-a", "scp1048-a", "scp-1048-a", "scp1048a", "scp-1048a", "earbear"
			;[Block]
			CreateConsoleMsg("SCP-1048-A cannot be spawned with the console. Sorry!", 255, 0, 0)
			;[End Block]
		Case "1048", "scp1048", "scp-1048", "scp-1048", "bear", "builderbear"
			;[Block]
			CreateConsoleMsg("SCP-1048 cannot be spawned with the console. Sorry!", 255, 0, 0)
			;[End Block]
		Case "1499-1", "14991", "scp-1499-1", "scp1499-1", "scp-14991", "scp14991"
			n.NPCs = CreateNPC(NPCtype1499, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "SCP-1499-1 instance spawned."
			;[End Block]
		Case "class-d", "classd", "d"
			;[Block]
			n.NPCs = CreateNPC(NPCtypeD, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "D-Class spawned."
			;[End Block]
		Case "guard"
			;[Block]
			n.NPCs = CreateNPC(NPCtypeGuard, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "Guard spawned."
			;[End Block]
		Case "mtf", "ntf"
			;[Block]
			n.NPCs = CreateNPC(NPCtypeMTF, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "MTF unit spawned."
			;[End Block]
		Case "apache", "helicopter"
			;[Block]
			n.NPCs = CreateNPC(NPCtypeApache, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "Apache spawned."
			;[End Block]
		Case "tentacle", "scp035tentacle", "scp-035tentacle", "scp-035-tentacle", "scp035-tentacle"
			;[Block]
			n.NPCs = CreateNPC(NPCtype035_Tentacle, EntityX(Collider), EntityY(Collider) - 0.12, EntityZ(Collider))
			ConsoleMsg = "SCP-035 tentacle spawned."
			;[End Block]
		Case "clerk", "woman"
			;[Block]
			n.NPCs = CreateNPC(NPCtypeClerk, EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
			ConsoleMsg = "Clerk spawned."
			;[End Block]
		Default 
			;[Block]
			CreateConsoleMsg("NPC type not found.", 255, 0, 0) : Return
			;[End Block]
	End Select
	
	If n <> Null
		If C_State <> "" Then n\State = Float(C_State) : ConsoleMsg = ConsoleMsg + " (State = " + n\State + ")"
	EndIf
	
	CreateConsoleMsg(ConsoleMsg)
End Function

Function ManipulateNPCBones()
	Local n.NPCs, Bone%, Pvt%, BoneName$
	Local MaxValue#, MinValue#, Offset#, Smooth#
	Local i%
	Local ToValue#
	
	For n = Each NPCs
		If n\ManipulateBone
			BoneName = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "bonename", 0)
			If BoneName <> ""
				Pvt = CreatePivot()
				Bone = FindChild(n\OBJ,BoneName$)
				If Bone = 0 Then RuntimeError("ERROR: NPC bone " + Chr(34) + BoneName + Chr(34) + " does not exist.")
				PositionEntity(Pvt, EntityX(Bone, True), EntityY(Bone, True), EntityZ(Bone, True))
				Select n\ManipulationType
					Case 0 ; ~ Looking at player
						;[Block]
						For i = 1 To GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controller_max", 1)
							If GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i, 0) = "pitch"
								MaxValue = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_max", 2)
								MinValue = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_min", 2)
								Offset = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_offset", 2)
								If GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_inverse", 3)
									ToValue = (-DeltaPitch(Bone, Camera)) + Offset
								Else
									ToValue = DeltaPitch(Bone,Camera) + Offset
								EndIf
								Smooth = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_smoothing", 2)
								If Smooth > 0.0
									n\BonePitch = CurveAngle(ToValue, n\BonePitch, Smooth)
								Else
									n\BonePitch = ToValue
								EndIf
								n\BonePitch = ChangeAngleValueForCorrectBoneAssigning(n\BonePitch)
								n\BonePitch = Max(Min(n\BonePitch, MaxValue), MinValue)
							ElseIf GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis1", 0) = "yaw"
								MaxValue = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate, "controlleraxis" + i + "_max", 2)
								MinValue = GetNPCManipulationValue(n\NPCNameInSection,n\BoneToManipulate, "controlleraxis" + i + "_min", 2)
								Offset = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_offset", 2)
								If GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_inverse", 3)
									ToValue = (-DeltaYaw(Bone, Camera)) + Offset
								Else
									ToValue = DeltaYaw(Bone, Camera) + Offset
								EndIf
								Smooth = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_smoothing", 2)
								If Smooth > 0.0
									n\BoneYaw = CurveAngle(ToValue, n\BoneYaw, Smooth)
								Else
									n\BoneYaw = ToValue
								EndIf
								n\BoneYaw = ChangeAngleValueForCorrectBoneAssigning(n\BoneYaw)
								n\BoneYaw = Max(Min(n\BoneYaw, MaxValue), MinValue)
							EndIf
						Next
						RotateEntity(Bone, EntityPitch(Bone) + n\BonePitch, EntityYaw(Bone) + n\BoneYaw, EntityRoll(Bone) + n\BoneRoll)
						;[End Block]
				End Select
				FreeEntity(Pvt)
			EndIf
		EndIf
	Next
End Function

Function GetNPCManipulationValue$(NPC$, Bone$, Section$, ValueType% = 0)
	; ~ Valuetype determines what type of variable should the Output be returned
	; ~ 0 - String
	; ~ 1 - Int
	; ~ 2 - Float
	; ~ 3 - Boolean
	
	Local Value$ = GetINIString("Data\NPCBones.ini", NPC, Bone + "_" + Section)
	
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
			If Value = "true" Or Value = "1"
				Return(True)
			Else
				Return(False)
			EndIf
			;[End Block]
	End Select
End Function

Function ChangeAngleValueForCorrectBoneAssigning(Value#)
	Local Numb#
	
	If Value =< 180.0
		Numb = Value
	Else
		Numb = -360.0 + Value
	EndIf
	
	Return(Numb)
End Function

Function NPCSpeedChange(n.NPCs)
	Select n\NPCtype
		Case NPCtype173, NPCtype106, NPCtype096, NPCtype049, NPCtype939, NPCtypeMTF
			Select SelectedDifficulty\OtherFactors
				Case NORMAL
					;[Block]
					n\Speed = n\Speed * 1.1
					;[End Block]
				Case HARD
					;[Block]
					n\Speed = n\Speed * 1.2
					;[End Block]
			End Select
	End Select
End Function

Function PlayerInReachableRoom(CanSpawnIn049Chamber% = False)
	Local RN$ = PlayerRoom\RoomTemplate\Name$
	Local e.Events, Temp%
	
	; ~ Player is in these rooms, returning false
	If RN = "pocketdimension" Or RN = "gatea" Or RN = "dimension1499" Or RN = "173" Then
		Return(False)
	EndIf
	; ~ Player is at Gate B and is at the surface, returning false
	If RN = "exit1" And EntityY(Collider) > 1040.0 * RoomScale Then
		Return(False)
	EndIf
	; ~ Player is in SCP-860's test room and inside the forest, returning false
	Temp = False
	For e = Each Events
		If e\EventName = "room860" And e\EventState = 1.0 Then
			Temp = True
			Exit
		EndIf
	Next
	If RN = "room860" And Temp Then
		Return(False)
	EndIf
	If (Not CanSpawnIn049Chamber) Then
		If SelectedDifficulty\AggressiveNPCs = False Then
			If RN = "room049" And EntityY(Collider) =< -2848.0 * RoomScale Then
				Return(False)
			EndIf
		EndIf
	EndIf
	; ~ Return true, this means player is in reachable room
	Return(True)
End Function

Function CheckForNPCInFacility(n.NPCs)
	; ~ False (= 0): NPC is not in facility (mostly meant for "dimension1499")
	; ~ True (= 1): NPC is in facility
	; ~ 2: NPC is in tunnels (maintenance tunnels / 049 tunnels / 939 storage room, etc...)
	
	If EntityY(n\Collider) > 100.0
		Return(False)
	EndIf
	If EntityY(n\Collider) < -10.0
		Return(2)
	EndIf
	If EntityY(n\Collider) > 7.0 And EntityY(n\Collider) =< 100.0
		Return(2)
	EndIf
	
	Return(True)
End Function

Function FinishWalking(n.NPCs, StartFrame#, EndFrame#, Speed#)
	Local CenterFrame#
	
	If n <> Null
		CenterFrame = (EndFrame - StartFrame) / 2
		If n\Frame >= CenterFrame
			AnimateNPC(n, StartFrame, EndFrame, Speed, False)
		Else
			AnimateNPC(n, EndFrame, StartFrame, -Speed, False)
		EndIf
	EndIf
End Function

Function ChangeNPCTextureID(n.NPCs, TextureID%)
	Local Temp#
	
	If (n = Null) Then
		CreateConsoleMsg("Tried to change the texture of an invalid NPC")
		If ConsoleOpening Then
			ConsoleOpen = True
		EndIf
		Return
	EndIf
	
	n\TextureID = TextureID + 1
	FreeEntity(n\OBJ)
	n\OBJ = CopyEntity(DTextures[TextureID + 1])
	
	Temp = 0.5 / MeshWidth(n\OBJ)
	ScaleEntity(n\OBJ, Temp, Temp, Temp)
	MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2, MeshHeight(n\OBJ) * 2, MeshDepth(n\OBJ) * 2)
	
	SetNPCFrame(n, n\Frame)
End Function

Function AnimateNPC(n.NPCs, Start#, Quit#, Speed#, Loop% = True)
	Local NewTime#, Temp%
	
	If Speed > 0.0 Then 
		NewTime = Max(Min(n\Frame + Speed * FPSfactor, Quit), Start)
		
		If Loop And NewTime >= Quit Then
			NewTime = Start
		EndIf
	Else
		If Start < Quit Then
			Temp = Start
			Start = Quit
			Quit = Temp
		EndIf
		
		If Loop Then
			NewTime = n\Frame + Speed * FPSfactor
			
			If NewTime < Quit Then 
				NewTime = Start
			Else If NewTime > Start 
				NewTime = Quit
			EndIf
		Else
			NewTime = Max(Min(n\Frame + Speed * FPSfactor, Start), Quit)
		EndIf
	EndIf
	SetNPCFrame(n, NewTime)
End Function

Function SetNPCFrame(n.NPCs, Frame#)
	If Abs(n\Frame - Frame) < 0.001 Then Return
	
	SetAnimTime(n\OBJ, Frame)
	
	n\Frame = Frame
End Function

Function Animate2#(Entity%, Curr#, Start%, Quit%, Speed#, Loop% = True)
	Local NewTime#, Temp%
	
	If Speed > 0.0 Then 
		NewTime = Max(Min(Curr + Speed * FPSfactor, Quit), Start)
		
		If Loop Then
			If NewTime >= Quit Then 
				NewTime = Start
			EndIf
		EndIf
	Else
		If Start < Quit Then
			Temp = Start
			Start = Quit
			Quit = Temp
		EndIf
		
		If Loop Then
			NewTime = Curr + Speed * FPSfactor
			
			If NewTime < Quit Then NewTime = Start
			If NewTime > Start Then NewTime = Quit
		Else
			NewTime = Max(Min(Curr + Speed * FPSfactor, Start), Quit)
		EndIf
	EndIf
	
	SetAnimTime(Entity, NewTime)
	
	Return(NewTime)
End Function 

;~IDEal Editor Parameters:
;~B#189#12CD#1367#13F8#15A2#16BD#188E#18EA
;~C#Blitz3D