; ~ NPC ID Constants
;[Block]
Const NPCType008_1% = 0, NPCType035_Tentacle% = 1, NPCType049% = 2, NPCType049_2% = 3, NPCType066% = 4, NPCType096% = 5
Const NPCType106% = 6, NPCType173% = 7, NPCType372% = 8, NPCType513_1% = 9, NPCType860_2% = 10, NPCType939% = 11
Const NPCType966% = 12, NPCType1048% = 13, NPCType1048_A% = 14, NPCType1499_1% = 15

Const NPCTypeApache% = 16, NPCTypeClerk% = 17, NPCTypeD% = 18, NPCTypeGuard% = 19, NPCTypeMTF% = 20
;[End Block]

Const MaxPathLocations% = 21
Const PathLocationDist# = 0.04 ; ~ 0.2 ^ 2

Type NPCs
	Field OBJ%, OBJ2%, OBJ3%, Collider%
	Field NPCType%, ID%
	Field CollRadius#
	Field DropSpeed#, Gravity%, FallingPickDistance#
	Field State#, State2#, State3#, PrevState%
	Field Frame#, Angle#, AnimTimer#
	Field Sound%, SoundCHN%
	Field Sound2%, SoundCHN2%
	Field SoundCHN_IsStream%, SoundCHN2_IsStream%
	Field Speed#, CurrSpeed#
	Field Texture$
	Field Idle#, IdleTimer#
	Field Reload#
	Field LastSeen%, LastDist#
	Field PrevX#, PrevY#, PrevZ#
	Field Target.NPCs, TargetID%
	Field EnemyX#, EnemyY#, EnemyZ#
	Field Path.WayPoints[MaxPathLocations], PathStatus%, PathTimer#, PathLocation%
	Field HideFromNVG%
	Field NVGX#, NVGY#, NVGZ#, NVGName$
	Field GravityMult#
	Field MaxGravity#
	Field IsDead%
	Field BlinkTimer# = 1.0
	;Field IgnorePlayer%
	Field ManipulateBone%, ManipulationType%
	Field BoneToManipulate$
	Field BonePitch#, BoneYaw#, BoneRoll#
	Field NPCNameInSection$
	Field InFacility%
	Field HP%
	Field Model$
	Field ModelScaleX#, ModelScaleY#, ModelScaleZ#
	Field TextureID% = -1
	Field HasAsset% = False
	Field HasAnim%
	Field Contained% = False
	Field TeslaHit% = False
	Field IsOpt% = False
End Type

Const NPCsFile$ = "Data\NPCs.ini"

Global ForestNPC%, ForestNPCTex%, ForestNPCData#[3]

Function CreateNPC.NPCs(NPCType%, x#, y#, z#)
	CatchErrors("CreateNPC(" + NPCType + ", " + x + ", " + y + ", " + z)
	
	Local n.NPCs, n2.NPCs
	Local Temp#, i%, Tex%, TexFestive%
	Local SF%, b%, t1%
	
	n.NPCs = New NPCs
	n\NPCType = NPCType
	n\GravityMult = 1.0
	n\MaxGravity = 0.2
	n\CollRadius = 0.2
	n\FallingPickDistance = 10.0
	n\HasAnim = True
	Select NPCType
		Case NPCType173
			;[Block]
			n\NVGName = "SCP-173"
			n\HasAnim = False
			n\Speed = IniGetFloat(NPCsFile, "SCP-173", "Speed") / 100.0
			n\Gravity = True
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.32
			EntityRadius(n\Collider, n\CollRadius - 0.09, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_173_MODEL])
			n\OBJ2 = CopyEntity(n_I\NPCModelID[NPC_173_HEAD_MODEL])
			; ~ Set a fastive texture
			Select Left(CurrentDate(), 7)
				Case "31 Oct "
					;[Block]
					n_I\IsHalloween = True
					TexFestive = LoadTexture_Strict("GFX\NPCs\scp_173_H.png")
					;[End Block]
				Case "01 Jan "
					;[Block]
					n_I\IsNewYear = True
					TexFestive = LoadTexture_Strict("GFX\NPCs\scp_173_NY.png")
					;[End Block]
				Case "01 Apr "
					;[Block]
					n_I\IsAprilFools = True
					TexFestive = LoadTexture_Strict("GFX\NPCs\scp_173_J.png")
					;[End Block]
			End Select
			If TexFestive <> 0
				EntityTexture(n\OBJ, TexFestive)
				EntityTexture(n\OBJ2, TexFestive)
				DeleteSingleTextureEntryFromCache(TexFestive)
			EndIf
			Temp = IniGetFloat(NPCsFile, "SCP-173", "Scale") / MeshDepth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp)
			
			n\OBJ3 = CopyEntity(n_I\NPCModelID[NPC_173_BOX_MODEL])
			ScaleEntity(n\OBJ3, RoomScale, RoomScale, RoomScale)
			HideEntity(n\OBJ3)
			;[End Block]
		Case NPCType106
			;[Block]
			n\NVGName = "SCP-106"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\Speed = IniGetFloat(NPCsFile, "SCP-106", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_106_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-106", "Scale") / 2.2
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\OBJ2 = CreateSprite()
			ScaleSprite(n\OBJ2, 0.03, 0.03)
			Tex = LoadTexture_Strict("GFX\NPCs\scp_106_eyes.png", 1, DeleteAllTextures, False)
			EntityTexture(n\OBJ2, Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			EntityBlend(n\OBJ2, 3)
			EntityFX(n\OBJ2, 1 + 8)
			SpriteViewMode(n\OBJ2, 2)
			HideEntity(n\OBJ2)
			;[End Block]
		Case NPCTypeGuard
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "Guard", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_GUARD_MODEL])
			Temp = IniGetFloat(NPCsFile, "Guard", "Scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
			;[End Block]
		Case NPCTypeMTF
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "MTF", "Speed") / 100.0
			n\HP = 100
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_MTF_MODEL])
			Temp = IniGetFloat(NPCsFile, "MTF", "Scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0) 
			
			If NPCSound[SOUND_NPC_MTF_BEEP] = 0 Then NPCSound[SOUND_NPC_MTF_BEEP] = LoadSound_Strict("SFX\Character\MTF\Beep.ogg")
			If NPCSound[SOUND_NPC_MTF_BREATH] = 0 Then NPCSound[SOUND_NPC_MTF_BREATH] = LoadSound_Strict("SFX\Character\MTF\Breath.ogg")
			;[End Block]
		Case NPCTypeD
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "Class D", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_CLASS_D_MODEL])
			Temp = IniGetFloat(NPCsFile, "Class D", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
			;[End Block]
		Case NPCType372
			;[Block]
			n\NVGName = "SCP-372"
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_372_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-372", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			HideEntity(n\OBJ)
			;[End Block]
		Case NPCType513_1
			;[Block]
			n\NVGName = "SCP-513-1"
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_513_1_MODEL])
			HideEntity(n\OBJ)
			n\OBJ2 = CopyEntity(n\OBJ)
			EntityAlpha(n\OBJ2, 0.6)
			HideEntity(n\OBJ2)
			
			Temp = IniGetFloat(NPCsFile, "SCP-513-1", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp)
			;[End Block]
		Case NPCType096
			;[Block]
			n\NVGName = "SCP-096"
			n\Speed = IniGetFloat(NPCsFile, "SCP-096", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.23
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_096_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-096", "Scale") / 3.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, (-MeshWidth(n\OBJ)) * 2.0, (-MeshHeight(n\OBJ)) * 2.0, (-MeshDepth(n\OBJ)) * 2.0, MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 4.0, MeshDepth(n\OBJ) * 4.0)
			
			n\OBJ2 = CreateSprite(FindChild(n\OBJ, "Reyelid"))
			ScaleSprite(n\OBJ2, 0.07, 0.08)
			EntityOrder(n\OBJ2, -5)
			EntityTexture(n\OBJ2, t\OverlayTextureID[2])
			HideEntity(n\OBJ2)
			;[End Block]
		Case NPCType049
			;[Block]
			n\NVGName = "SCP-049"
			n\Speed = IniGetFloat(NPCsFile, "SCP-049", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_049_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-049", "Scale")
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			If NPCSound[SOUND_NPC_049_BREATH] = 0 Then NPCSound[SOUND_NPC_049_BREATH] = LoadSound_Strict("SFX\SCP\049\Breath.ogg")
			;[End Block]
		Case NPCType049_2
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "SCP-049-2", "Speed") / 100.0
			n\HP = 150
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_049_2_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-049-2", "Scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
			
			If NPCSound[SOUND_NPC_049_2_BREATH] = 0 Then NPCSound[SOUND_NPC_049_2_BREATH] = LoadSound_Strict("SFX\SCP\049_2\Breath.ogg")
			If NPCSound[SOUND_NPC_049_2_RESTING] = 0 Then NPCSound[SOUND_NPC_049_2_RESTING] = LoadSound_Strict("SFX\SCP\049_2\Resting.ogg")
			;[End Block]
		Case NPCTypeApache
			;[Block]
			n\NVGName = GetLocalString("npc", "apache")
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			
			n\Collider = CreatePivot()
			n\CollRadius = 3.0
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_APACHE)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_APACHE_MODEL])
			
			n\OBJ2 = CopyEntity(n_I\NPCModelID[NPC_APACHE_ROTOR_1_MODEL])
			EntityParent(n\OBJ2, n\OBJ)
			
			For i = -1 To 1 Step 2
				Local Rotor2% = CopyEntity(n\OBJ2, n\OBJ2)
				
				RotateEntity(Rotor2, 0.0, 4.0 * i, 0.0)
				EntityAlpha(Rotor2, 0.5)
			Next
			
			n\OBJ3 = CopyEntity(n_I\NPCModelID[NPC_APACHE_ROTOR_2_MODEL])
			EntityParent(n\OBJ3, n\OBJ)
			PositionEntity(n\OBJ3, 0.0, 2.15, -5.48)
			
			For i = -1 To 1 Step 2
				Local Light1% = CreateLight(2, n\OBJ)
				
				LightRange(Light1, 2.0)
				LightColor(Light1, 255, 255, 255)
				PositionEntity(Light1, 1.65 * i, 1.17, -0.25)
				
				Local LightSprite% = CreateSprite(n\OBJ)
				
				PositionEntity(LightSprite, 1.65 * i, 1.17, 0.0, -0.25)
				ScaleSprite(LightSprite, 0.13, 0.13)
				EntityTexture(LightSprite, misc_I\LightSpriteID[LIGHT_SPRITE_DEFAULT])
				EntityBlend(LightSprite, 3)
				EntityFX(LightSprite, 1 + 8)
			Next
			ScaleEntity(n\OBJ, 0.7, 0.7, 0.7)
			;[End Block]
		Case NPCType035_Tentacle
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\HP = 500
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_035_TENTACLE_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-035's Tentacle", "Scale") / 10.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			SetNPCFrame(n, 282.0)
			
			If NPCSound[SOUND_NPC_035_TENTACLE_IDLE] = 0 Then NPCSound[SOUND_NPC_035_TENTACLE_IDLE] = LoadSound_Strict("SFX\SCP\035_Tentacle\TentacleIdle.ogg")
			;[End Block]
		Case NPCType860_2
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
			n\Speed = IniGetFloat(NPCsFile, "SCP-860-2", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.25
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_860_2_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-860-2", "Scale") / 20.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, (-MeshWidth(n\OBJ)) * 2.0, (-MeshHeight(n\OBJ)) * 2.0, (-MeshDepth(n\OBJ)) * 2.0, MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 4.0, MeshDepth(n\OBJ) * 4.0)
			EntityFX(n\OBJ, 1)
			
			n\OBJ2 = CreateSprite()
			ScaleSprite(n\OBJ2, 0.1, 0.1)
			Tex = LoadTexture_Strict("GFX\NPCs\scp_860_2_eyes.png", 1 + 2, DeleteAllTextures, False)
			EntityTexture(n\OBJ2, Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			EntityFX(n\OBJ2, 1 + 8)
			EntityBlend(n\OBJ2, 3)
			SpriteViewMode(n\OBJ2, 2)
			HideEntity(n\OBJ2)
			;[End Block]
		Case NPCType939
			;[Block]
			i = 1
			For n2.NPCs = Each NPCs
				If n <> n2
					If n\NPCType = n2\NPCType Then i = i + 1
				EndIf
			Next
			n\NVGName = "SCP-939-" + i
			n\Speed = IniGetFloat(NPCsFile, "SCP-939", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.32
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_939_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-939", "Scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			;[End Block]
		Case NPCType066
			;[Block]
			n\NVGName = "SCP-066"
			n\Speed = IniGetFloat(NPCsFile, "SCP-066", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_066_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-066", "Scale") / 2.5
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			;[End Block]
		Case NPCType966
			;[Block]
			i = 1
			For n2.NPCs = Each NPCs
				If n <> n2
					If n\NPCType = n2\NPCType Then i = i + 1
				EndIf
			Next
			n\NVGName = "SCP-966-" + i
			n\Speed = (IniGetFloat(NPCsFile, "SCP-966", "Speed") / 100.0)
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_966_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-966", "Scale") / 40.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			SetNPCFrame(n, 15.0)
			;[End Block]
		Case NPCType1499_1
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
			n\Speed = IniGetFloat(NPCsFile, "SCP-1499-1", "Speed") / 100.0 * Rnd(0.9, 1.1)
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_1499_1_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-1499-1", "Scale") / 4.0 * Rnd(0.8, 1.0)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			EntityFX(n\OBJ, 1)
			EntityAutoFade(n\OBJ, HideDistance * 2.5, HideDistance * 2.95)
			;[End Block]
		Case NPCType008_1
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "SCP-008-1", "Speed") / 100.0
			n\HP = 100
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_008_1_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-008-1", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
			SetNPCFrame(n, 11.0)
			
			If NPCSound[SOUND_NPC_008_1_BREATH] = 0 Then NPCSound[SOUND_NPC_008_1_BREATH] = LoadSound_Strict("SFX\SCP\008_1\Breath.ogg")
			;[End Block]
		Case NPCTypeClerk
			;[Block]
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, "Clerk", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_CLERK_MODEL])
			Temp = IniGetFloat(NPCsFile, "Clerk", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
			;[End Block]
		Case NPCType1048
			;[Block]
			n\NVGName = "SCP-1048"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_1048_MODEL])
			EntityPickMode(n\OBJ, 2) ; ~ We can use that because SCP-1048 is a fully scripted NPC
			Temp = IniGetFloat(NPCsFile, "SCP-1048", "Scale") / 10.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			;[End Block]
		Case NPCType1048_A
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
			n\Speed = IniGetFloat(NPCsFile, "SCP-1048", "Speed")
			n\HP = 60
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.1
			EntityRadius(n\Collider, n\CollRadius, 0.15)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_1048_A_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-1048", "Scale") / 10.0
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			;[End Block]
	End Select
	
	PositionEntity(n\Collider, x, y, z, True)
	PositionEntity(n\OBJ, x, y, z, True)
	
	ResetEntity(n\Collider)
	
	n\ID = 0
	n\ID = FindFreeNPCID()
	
	NPCSpeedChange(n)
	
	CatchErrors("Uncaught: CreateNPC(" + NPCType + ", " + x + ", " + y + ", " + z)
	
	Return(n)
End Function

Function CreateNPCAsset%(n.NPCs)
	Local Temp#, Tex%
	
	Select n\NPCType
		Case NPCTypeGuard
			;[Block]
			n\OBJ2 = CopyEntity(n_I\NPCModelID[NPC_VEHICLE_MODEL])
			Temp = IniGetFloat(NPCsFile, "Guard", "Scale") / 2.5
			Temp = (Temp + 1.68) / MeshWidth(n\OBJ2)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp)
			HideEntity(n\OBJ2)
			;[End Block]
		Case NPCTypeD
			;[Block]
			If n\OBJ2 <> 0
				EntityParent(n\OBJ2, 0)
				
				FreeEntity(n\OBJ2) : n\OBJ2 = 0
			EndIf
			
			; ~ Save model parameters
			Local PrevYaw# = EntityYaw(n\OBJ)
			Local PrevX# = EntityX(n\OBJ)
			Local PrevY# = EntityY(n\OBJ)
			Local PrevZ# = EntityZ(n\OBJ)
			Local PrevFrame# = AnimTime(n\OBJ)
			
			; ~ Reset parameters
			RotateEntity(n\OBJ, 0.0, 0.0, 0.0)
			PositionEntity(n\OBJ, 0.0, 0.0, 0.0)
			SetNPCFrame(n, 1.0)
			
			; ~ Load the mask and apply to model
			If I_035\Sad
				n\OBJ2 = LoadMesh_Strict("GFX\NPCs\scp_035_sad.b3d")
			Else
				n\OBJ2 = LoadMesh_Strict("GFX\NPCs\scp_035_smile.b3d")
			EndIf
			Temp = IniGetFloat(NPCsFile, "Class D", "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp, True)
			PositionEntity(n\OBJ2, 0.0, 0.86, -0.1, True)
			RotateEntity(n\OBJ2, 0.0, EntityYaw(n\OBJ, True), 0.0, True)
			EntityParent(n\OBJ2, FindChild(n\OBJ, "Bip01_Head"))
			
			; ~ Bring back the model
			RotateEntity(n\OBJ, 0.0, PrevYaw, 0.0)
			PositionEntity(n\OBJ, PrevX, PrevY, PrevZ)
			SetNPCFrame(n, PrevFrame)
			;[End Block]
	End Select
	
	n\HasAsset = True
End Function

Function RemoveNPC%(n.NPCs)
	If n = Null Then Return
	
	CatchErrors("RemoveNPC()")
	
	n\Target = Null
	If n\SoundCHN_IsStream
		If n\SoundCHN <> 0 Then StopStream_Strict(n\SoundCHN) : n\SoundCHN_IsStream = False
	Else
		StopChannel(n\SoundCHN)
	EndIf
	n\SoundCHN = 0
	If n\SoundCHN2_IsStream
		If n\SoundCHN2 <> 0 Then StopStream_Strict(n\SoundCHN2) : n\SoundCHN2_IsStream = False
	Else
		StopChannel(n\SoundCHN2)
	EndIf
	n\SoundCHN2 = 0
	If n\Sound <> 0 Then FreeSound_Strict(n\Sound) : n\Sound = 0
	If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2) : n\Sound2 = 0
	
	If n\OBJ2 <> 0 Then FreeEntity(n\OBJ2) : n\OBJ2 = 0
	If n\OBJ3 <> 0 Then FreeEntity(n\OBJ3) : n\OBJ3 = 0
	FreeEntity(n\Collider) : n\Collider = 0
	FreeEntity(n\OBJ) : n\OBJ = 0
	
	Delete(n)
	
	CatchErrors("Uncaught: RemoveNPC()")
End Function

Global RemoveHazmatTimer#, Remove714Timer#

Function UpdateNPCs%()
	CatchErrors("UpdateNPCs()")
	
	Local n.NPCs, n2.NPCs, d.Doors, de.Decals, r.Rooms, e.Events, w.WayPoints, p.Particles, wp.WayPoints, wayPointCloseToPlayer.WayPoints
	Local i%, j%, Dist#, Dist2#, Angle#, x#, x2#, y#, z#, z2#, PrevFrame#, PlayerSeeAble%, Visible%
	Local Target%, Pvt%, Pick%, PrevDist#, NewDist#, Attack%
	Local SinValue#, SqrValue#
	Local DifficultyDMGMult#
	
	Select SelectedDifficulty\OtherFactors
		Case EASY
			;[Block]
			DifficultyDMGMult = 1.0
			;[End Block]
		Case NORMAL
			;[Block]
			DifficultyDMGMult = 1.15
			;[End Block]
		Case HARD
			;[Block]
			DifficultyDMGMult = 1.3
			;[End Block]
		Case EXTREME
			;[Block]
			DifficultyDMGMult = 1.45
			;[End Block]
	End Select
	
	For n.NPCs = Each NPCs
		; ~ A variable to determine if the NPC is in the facility or not
		n\InFacility = IsInFacility(EntityY(n\Collider))
		
		Select n\NPCType
			Case NPCType173
				;[Block]
				If n\Idle <> 3 And (Not IsPlayerOutsideFacility())
					Dist = EntityDistanceSquared(n\Collider, me\Collider)
					
					n\State3 = 1.0
					
					If n\Idle < 2
						If n\IdleTimer > 0.1
							n\Idle = 1
							n\IdleTimer = Max(n\IdleTimer - fps\Factor[0], 0.1)
						ElseIf n\IdleTimer = 0.1
							n\Idle = 0
							n\IdleTimer = 0.0
						EndIf
						
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						
						PositionEntity(n\OBJ2, EntityX(n\Collider), EntityY(n\Collider) - 0.32, EntityZ(n\Collider))
						RotateEntity(n\OBJ2, 0.0, (EntityYaw(n\Collider) - 180.0) + n\Angle, 0.0)
						
						If n\Idle = 0
							Local Temp% = False
							Local Move% = True
							
							If Dist < 225.0
								If Dist < 100.0
									If EntityVisible(n\Collider, me\Collider)
										Temp = True
										n\EnemyX = EntityX(me\Collider, True)
										n\EnemyY = EntityY(me\Collider, True)
										n\EnemyZ = EntityZ(me\Collider, True)
									EndIf
								EndIf
								
								Local SoundVol# = Max(Min((Distance(EntityX(n\Collider), n\PrevX, EntityZ(n\Collider), n\PrevZ) * 2.5), 1.0), 0.0)
								
								n\SoundCHN = LoopSound2(snd_I\StoneDragSFX, n\SoundCHN, Camera, n\Collider, 10.0, n\State)
								
								n\PrevX = EntityX(n\Collider)
								n\PrevZ = EntityZ(n\Collider)
								
								If PlayerSees173(n) Then Move = False
							EndIf
							
							If chs\NoTarget Then Move = True
							
							; ~ Doesn't move
							If (Not Move)
								SqrValue = Sqr(Dist)
								me\BlurVolume = Max(Max(Min((4.0 - SqrValue) / 6.0, 0.9), 0.1), me\BlurVolume)
								me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * 15.0 * Max((3.5 - SqrValue) / 3.5, 0.0))
								
								If Dist < 12.25 And MilliSecs() - n\LastSeen > 60000 And Temp
									PlaySound_Strict(snd_I\HorrorSFX[Rand(3, 4)])
									
									n\LastSeen = MilliSecs()
								EndIf
								
								If Dist < 2.25 And Rand(700) = 1 Then PlaySound2(snd_I\SCP173SFX[Rand(0, 2)], Camera, n\OBJ)
								
								If Dist < 2.25 And n\LastDist > 2.0 And Temp
									me\CurrCameraZoom = 40.0
									me\HeartBeatRate = Max(me\HeartBeatRate, 140.0)
									me\HeartBeatVolume = 0.5
									
									Select Rand(5)
										Case 1
											;[Block]
											PlaySound_Strict(snd_I\HorrorSFX[1])
											;[End Block]
										Case 2
											;[Block]
											PlaySound_Strict(snd_I\HorrorSFX[2])
											;[End Block]
										Case 3
											;[Block]
											PlaySound_Strict(snd_I\HorrorSFX[9])
											;[End Block]
										Case 4
											;[Block]
											PlaySound_Strict(snd_I\HorrorSFX[10])
											;[End Block]
										Case 5
											;[Block]
											PlaySound_Strict(snd_I\HorrorSFX[12])
											;[End Block]
									End Select
								EndIf
								
								n\LastDist = Sqr(Dist)
								
								n\State = Max(0.0, n\State - fps\Factor[0] / 20.0)
							Else
								; ~ Teleport to a room closer to the player
								If Dist > 2500.0
									If Rand(70) = 1
										If (Not IsPlayerOutsideFacility()) And PlayerRoom\RoomTemplate\RoomID <> r_dimension_106
											For w.WayPoints = Each WayPoints
												If w\door = Null And Rand(5) = 1
													If DistanceSquared(EntityX(w\OBJ, True), EntityX(me\Collider), EntityZ(w\OBJ, True), EntityZ(me\Collider)) < 625.0
														If DistanceSquared(EntityX(w\OBJ, True), EntityX(me\Collider), EntityZ(w\OBJ, True), EntityZ(me\Collider)) > 225.0
															PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 200.0 * RoomScale, EntityZ(w\OBJ, True))
															ResetEntity(n\Collider)
															Exit
														EndIf
													EndIf
												EndIf
											Next
										EndIf
									EndIf
								ElseIf Dist > PowTwo(HideDistance * 0.8) ; ~ Move randomly from waypoint to another
									If Rand(70) = 1 Then TeleportCloser(n)
								Else ; ~ Actively move towards the player
									n\State = CurveValue(SoundVol, n\State, 3.0)
									
									; ~ Tries to open doors
									If Rand(20 - (10 * SelectedDifficulty\AggressiveNPCs)) = 1
										For d.Doors = Each Doors
											If d\Locked = 0 And (Not d\Open) And d\Code = 0 And d\KeyCard = 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR
												For i = 0 To 1
													If d\Buttons[i] <> 0
														If Abs(EntityX(n\Collider) - EntityX(d\Buttons[i])) < 0.5
															If Abs(EntityZ(n\Collider) - EntityZ(d\Buttons[i])) < 0.5
																If (d\OpenState >= 180.0 Lor d\OpenState <= 0.0)
																	Pvt = CreatePivot()
																	PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.5, EntityZ(n\Collider))
																	PointEntity(Pvt, d\Buttons[i])
																	MoveEntity(Pvt, 0.0, 0.0, n\Speed * 0.6)
																	
																	If EntityPick(Pvt, 0.5) = d\Buttons[i]
																		PlaySound_Strict(LoadTempSound("SFX\Door\DoorOpen173.ogg"))
																		OpenCloseDoor(d, True)
																		FreeEntity(Pvt) : Pvt = 0
																		Exit
																	EndIf
																	FreeEntity(Pvt) : Pvt = 0
																EndIf
															EndIf
														EndIf
													EndIf
												Next
											EndIf
										Next
									EndIf
									
									If chs\NoTarget
										Temp = False
										n\EnemyX = 0.0
										n\EnemyY = 0.0
										n\EnemyZ = 0.0
									EndIf
									
									; ~ Attacks
									If Temp
										If (Not I_268\InvisibilityOn) Then n\Angle = DeltaYaw(n\Collider, Camera)
										If Dist < 0.4225
											If (Not me\Terminated) And (Not chs\GodMode)
												Select PlayerRoom\RoomTemplate\RoomID
													Case r_room2c_gw_lcz, r_room2_closets, r_cont1_895
														;[Block]
														msg\DeathMsg = Format(GetLocalString("death", "173.gw"), SubjectName)
														;[End Block]
													Case r_cont1_173_intro
														;[Block]
														msg\DeathMsg = Format(GetLocalString("death", "173.intro"), SubjectName)
														;[End block]
													Case r_room2_6_lcz
														;[Block]
														msg\DeathMsg = GetLocalString("death", "173.6")
														;[End Block]
													Default 
														;[Block]
														msg\DeathMsg = Format(GetLocalString("death", "173.default"), SubjectName)
														;[End Block]
												End Select
												
												If (Not chs\GodMode) Then n\Idle = 1
												
												PlaySound_Strict(snd_I\NeckSnapSFX[Rand(0, 2)])
												If Rand(2) = 1
													TurnEntity(Camera, 0.0, Rnd(80.0, 100.0), 0.0)
												Else
													TurnEntity(Camera, 0.0, Rnd(-100.0, -80.0), 0.0)
												EndIf
												Kill()
											EndIf
										Else
											If (Not I_268\InvisibilityOn)
												PointEntity(n\Collider, me\Collider)
												RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), EntityRoll(n\Collider))
												TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider) + 90.0) * n\Speed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider) + 90.0) * n\Speed * fps\Factor[0])
											EndIf
										EndIf
									Else ; ~ Move to the location where he was last seen
										If n\EnemyX <> 0.0
											n\Angle = DeltaYaw(n\Collider, Camera)
											If DistanceSquared(EntityX(n\Collider), n\EnemyX, EntityZ(n\Collider), n\EnemyZ) > 0.25 And (Not I_268\InvisibilityOn)
												AlignToVector(n\Collider, n\EnemyX - EntityX(n\Collider), 0.0, n\EnemyZ - EntityZ(n\Collider), 3)
												MoveEntity(n\Collider, 0.0, 0.0, n\Speed * fps\Factor[0])
												If Rand(500) = 1 Then n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
											Else
												n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
											EndIf
										Else
											If Rand(400) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 10.0)
											TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider) + 90.0) * n\Speed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider) + 90.0) * n\Speed * fps\Factor[0])
											If (Not chs\NoTarget)
												n\Angle = Rnd(-120.0, 120.0)
											Else
												n\Angle = 0.0
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						PositionEntity(n\Collider, EntityX(n\Collider), Min(EntityY(n\Collider), 0.35), EntityZ(n\Collider))
					Else ; ~ SCP-173 was captured by MTF
						If n_I\MTFLeader <> Null
							Local Tmp% = False
							
							Dist = EntityDistanceSquared(n\Collider, n_I\MTFLeader\Collider)
							If Dist > PowTwo(HideDistance / 2.0)
								If (Not EntityInView(n\OBJ, Camera)) And (Not EntityVisible(n\Collider, n_I\MTFLeader\Collider)) Then Tmp = True
							EndIf
							If (Not Tmp)
								PointEntity(n\OBJ, n_I\MTFLeader\Collider)
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0, True)
								MoveEntity(n\Collider, 0.0, 0.0, 0.016 * fps\Factor[0] * Max(Min(((Sqr(Dist) * 2.0) - 1.0) * 0.5, 1.0), -0.5))
								n\GravityMult = 1.0
							Else
								PositionEntity(n\Collider, EntityX(n_I\MTFLeader\Collider), EntityY(n_I\MTFLeader\Collider) + 0.3, EntityZ(n_I\MTFLeader\Collider))
								ResetEntity(n\Collider)
								n\DropSpeed = 0.0
								n\GravityMult = 0.0
							EndIf
						EndIf
						
						SinValue = 0.05 + (Sin(MilliSec * 0.08) * 0.02)
						
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) + SinValue, EntityZ(n\Collider))
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						
						PositionEntity(n\OBJ2, EntityX(n\Collider), EntityY(n\Collider) + SinValue, EntityZ(n\Collider))
						RotateEntity(n\OBJ2, 0.0, (EntityYaw(n\Collider) - 180.0) + n\Angle, 0.0)
						
						If EntityHidden(n\OBJ3) Then ShowEntity(n\OBJ3)
						
						PositionEntity(n\OBJ3, EntityX(n\Collider), EntityY(n\Collider) - SinValue, EntityZ(n\Collider))
						RotateEntity(n\OBJ3, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
					EndIf
				EndIf
				;[End Block]
			Case NPCType106
				;[Block]
				If n\Contained Lor PlayerRoom\RoomTemplate\RoomID = r_gate_b
					If (Not EntityHidden(n\OBJ))
						n\Idle = 1
						HideEntity(n\OBJ)
						HideEntity(n\OBJ2)
						PositionEntity(n\Collider, 0.0, -500.0, 0.0)
						ResetEntity(n\Collider)
					EndIf
				Else
					Dist = EntityDistanceSquared(n\Collider, me\Collider)
					
					Local Spawn106% = True
					
					; ~ Checking if SCP-106 is allowed to spawn
					If PlayerRoom\RoomTemplate\RoomID = r_dimension_1499 Lor (PlayerRoom\RoomTemplate\RoomID = r_cont2_049 And EntityY(me\Collider) <= -2848.0 * RoomScale) Then Spawn106 = False
					If forest_event <> Null
						If PlayerRoom = forest_event\room
							If forest_event\EventState = 1.0 Then Spawn106 = False
						EndIf
					EndIf
					; ~ Gate A event has been triggered. Don't make SCP-106 disappear!
					; ~ The reason why this is a seperate for loop is because we need to make sure that cont2_860_1 would not be able to overwrite the "Spawn106" variable
					For e.Events = Each Events
						If e\EventID = e_gate_a
							If e\EventState <> 0.0
								Spawn106 = True
								n\Idle = (PlayerRoom\RoomTemplate\RoomID = r_dimension_1499)
							EndIf
							Exit
						EndIf
					Next
					
					If (Not Spawn106) And n\State <= 0.0
						PositionEntity(n\Collider, 0.0, -500.0, 0.0)
						ResetEntity(n\Collider)
						n\State = Rnd(22000.0, 27000.0)
					EndIf
					
					If n\Idle = 0 And Spawn106
						If n\State <= 0.0
							If EntityY(n\Collider) < EntityY(me\Collider) - 20.0 - 0.55
								If (Not PlayerRoom\RoomTemplate\DisableDecals)
									de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(me\Collider), 0.01, EntityZ(me\Collider), 90.0, Rnd(360.0), 0.0, 0.05, 0.8)
									de\SizeChange = 0.001
									EntityParent(de\OBJ, PlayerRoom\OBJ)
								EndIf
								
								n\PrevY = EntityY(me\Collider)
								
								SetNPCFrame(n, 110.0)
								
								If PlayerRoom\RoomTemplate\RoomID <> r_cont1_895 Then PositionEntity(n\Collider, EntityX(me\Collider), EntityY(me\Collider) - 15.0, EntityZ(me\Collider))
								
								PlaySound_Strict(snd_I\DecaySFX[0])
							EndIf
							
							If Rand(500) = 1 Then PlaySound2(snd_I\SCP106SFX[Rand(0, 2)], Camera, n\Collider)
							n\SoundCHN = LoopSound2(snd_I\SCP106SFX[4], n\SoundCHN, Camera, n\Collider, 8.0, 0.8, True)
							
							If n\State > -10.0
								ShouldPlay = 66
								If n\Frame < 259.0
									PositionEntity(n\Collider, EntityX(n\Collider), n\PrevY - 0.15, EntityZ(n\Collider))
									If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
										PointEntity(n\OBJ, me\Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 100.0), 0.0, True)
									EndIf
									
									AnimateNPC(n, 110.0, 259.0, 0.15, False)
								Else
									n\State = -10.0
								EndIf
							Else
								If PlayerRoom\RoomTemplate\RoomID <> r_gate_a And PlayerRoom\RoomTemplate\RoomID <> r_dimension_106 Then ShouldPlay = 10
								
								Visible = False
								
								If Dist < 64.0
									If (Not (chs\NoTarget Lor I_268\InvisibilityOn)) Then Visible = EntityVisible(n\Collider, me\Collider)
								EndIf
								
								If Visible
									If PlayerRoom\RoomTemplate\RoomID <> r_gate_a Then n\PathTimer = 0.0
									If EntityInView(n\Collider, Camera)
										GiveAchievement(Achv106)
										
										SqrValue = (4.0 - Sqr(Dist))
										
										me\BlurVolume = Max(Max(Min(SqrValue / 6.0, 0.9), 0.1), me\BlurVolume)
										me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * 20.0 * Max(SqrValue / 4.0, 0.0))
										
										If MilliSecs() - n\LastSeen > 60000
											me\CurrCameraZoom = 40.0
											PlaySound_Strict(snd_I\HorrorSFX[6])
											n\LastSeen = MilliSecs()
										EndIf
									EndIf
								Else
									n\State = n\State - fps\Factor[0]
								EndIf
								
								If Dist > 0.64
									If ((Dist > 625.0 Lor PlayerRoom\RoomTemplate\RoomID = r_dimension_106 Lor Visible Lor (n\PathStatus <> PATH_STATUS_FOUND) And (Not (chs\NoTarget Lor I_268\InvisibilityOn)))) And PlayerRoom\RoomTemplate\RoomID <> r_gate_a
										If (Dist > 1600.0 Lor PlayerRoom\RoomTemplate\RoomID = r_dimension_106) Then TranslateEntity(n\Collider, 0.0, ((EntityY(me\Collider) - 0.14) - EntityY(n\Collider)) / 50.0, 0.0)
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										
										PointEntity(n\OBJ, me\Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0 - SelectedDifficulty\OtherFactors), 0.0)
										
										If (Not me\Terminated)
											PrevFrame = n\Frame
											AnimateNPC(n, 284.0, 333.0, n\CurrSpeed * 43.0)
											
											If (PrevFrame <= 286.0 And n\Frame > 286.0) Lor (PrevFrame <= 311.0 And n\Frame > 311.0) Then PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))
										Else
											n\CurrSpeed = 0.0
										EndIf
										
										n\PathTimer = Max(n\PathTimer - fps\Factor[0], 0.0)
										If n\PathTimer <= 0.0
											n\PathStatus = FindPath(n, EntityX(me\Collider, True), EntityY(me\Collider, True), EntityZ(me\Collider, True))
											n\PathTimer = 70.0 * 10.0
										EndIf
									Else
										If n\PathTimer > 0.0
											n\PathTimer = Max(n\PathTimer - fps\Factor[0], 0.0)
											
											If n\PathStatus = PATH_STATUS_FOUND
												While n\Path[n\PathLocation] = Null
													If n\PathLocation > MaxPathLocations - 1
														n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
														Exit
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Wend
												
												If n\Path[n\PathLocation] <> Null
													TranslateEntity(n\Collider, 0.0, ((EntityY(n\Path[n\PathLocation]\OBJ, True) - 0.11) - EntityY(n\Collider)) / 50.0, 0.0)
													
													PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
													
													Dist2 = EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ)
													
													RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), Min(20.0, Sqr(Dist2) * 10.0)), 0.0)
													
													n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
													
													PrevFrame = AnimTime(n\OBJ)
													AnimateNPC(n, 284.0, 333.0, n\CurrSpeed * 43.0)
													If (PrevFrame <= 286.0 And n\Frame > 286.0) Lor (PrevFrame <= 311.0 And n\Frame > 311.0) Then PlaySound2(StepSFX(2, 0, Rand(0, 2)), Camera, n\Collider, 6.0, Rnd(0.8, 1.0))
													
													If Dist2 < PathLocationDist Then n\PathLocation = n\PathLocation + 1
												EndIf
											Else
												n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
												If n\State3 = 0.0 Then AnimateNPC(n, 334.0, 494.0, 0.3)
											EndIf
										Else
											n\PathStatus = FindPath(n, EntityX(me\Collider, True), EntityY(me\Collider, True), EntityZ(me\Collider, True))
											n\PathTimer = 70.0 * 10.0
											n\CurrSpeed = 0.0
										EndIf
									EndIf
								ElseIf (Not chs\NoTarget)
									If Dist > 0.25
										n\CurrSpeed = CurveValue(n\Speed * 2.5, n\CurrSpeed, 10.0)
									Else
										n\CurrSpeed = 0.0
									EndIf
									AnimateNPC(n, 105.0, 110.0, 0.15, False)
									
									If (Not me\Terminated) And me\FallTimer >= 0.0
										PointEntity(n\OBJ, me\Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0 - SelectedDifficulty\OtherFactors), 0.0)
										
										If Ceil(n\Frame) = 110.0 And (Not chs\GodMode)
											PlaySound_Strict(snd_I\DamageSFX[1])
											PlaySound_Strict(snd_I\HorrorSFX[5])
											If PlayerRoom\RoomTemplate\RoomID = r_dimension_106
												msg\DeathMsg = Format(GetLocalString("death", "106.dimension"), SubjectName)
												Kill(True)
											ElseIf PlayerRoom\RoomTemplate\RoomID = r_gate_a
												msg\DeathMsg = Format(GetLocalString("death", "106.gatea"), SubjectName)
												Kill(True)
											Else
												PlaySound_Strict(snd_I\SCP106SFX[3], True)
												ShowEntity(me\Head)
												PositionEntity(me\Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
												ResetEntity(me\Head)
												RotateEntity(me\Head, 0.0, EntityYaw(Camera) + Rnd(-45.0, 45.0), 0.0)
												me\FallTimer = Min(-1.0, me\FallTimer)
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							
							If n\State < Rnd(-3500.0, -3000.0)
								If (Not EntityInView(n\OBJ, Camera)) And Dist > 25.0
									PositionEntity(n\Collider, 0.0, -500.0, 0.0)
									ResetEntity(n\Collider)
									n\State = Rnd(22000.0, 27000.0)
								EndIf
							EndIf
							
							If me\FallTimer < -250.0 Then MoveToPocketDimension()
							
							If n\Reload = 0.0
								If Dist > 100.0 And (Not IsPlayerOutsideFacility()) And PlayerRoom\RoomTemplate\RoomID <> r_dimension_106 And n\State < -5.0 ; ~ Timer idea -- Juanjpro
									If (Not EntityInView(n\OBJ, Camera))
										TurnEntity(me\Collider, 0.0, 180.0, 0.0)
										If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
											Pick = EntityPick(me\Collider, 5.0)
										Else
											Pick = 0
										EndIf
										TurnEntity(me\Collider, 0.0, 180.0, 0.0)
										If Pick <> 0
											TeleportEntity(n\Collider, PickedX(), PickedY(), PickedZ(), n\CollRadius)
											PointEntity(n\Collider, me\Collider)
											RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
											MoveEntity(n\Collider, 0.0, 0.0, -2.0)
											PlaySound2(snd_I\SCP106SFX[3], Camera, n\Collider, 10.0, 1.0, True)
											n\SoundCHN2 = PlaySound2(snd_I\SCP106SFX[Rand(6, 8)], Camera, n\Collider)
											n\PathTimer = 0.0
											n\Reload = (70.0 * 10.0) / (SelectedDifficulty\OtherFactors + 1.0)
										EndIf
									EndIf
								EndIf
							EndIf
							n\Reload = Max(0.0, n\Reload - fps\Factor[0])
							
							UpdateSoundOrigin(n\SoundCHN2, Camera, n\Collider)
						Else ; ~ Idling outside the map
							n\CurrSpeed = 0.0
							MoveEntity(n\Collider, 0.0, ((EntityY(me\Collider) - 30.0) - EntityY(n\Collider)) / 200.0, 0.0)
							n\DropSpeed = 0.0
							SetNPCFrame(n, 110.0)
							
							If (Not PlayerRoom\RoomTemplate\DisableDecals)
								If PlayerRoom\RoomTemplate\RoomID <> r_gate_a Then n\State = n\State - (fps\Factor[0] * (1.0 + (SelectedDifficulty\AggressiveNPCs)))
							EndIf
						EndIf
						
						ResetEntity(n\Collider)
						n\DropSpeed = 0.0
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.15, EntityZ(n\Collider))
						
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider), 0.0)
						
						PositionEntity(n\OBJ2, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
						RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
						MoveEntity(n\OBJ2, 0.0, 0.946, -0.165)
						
						If PlayerRoom\RoomTemplate\RoomID = r_dimension_106 Lor IsPlayerOutsideFacility()
							If (Not EntityHidden(n\OBJ2)) Then HideEntity(n\OBJ2)
						Else
							If Dist < PowTwo(opt\CameraFogFar * LightVolume * 0.6)
								If (Not EntityHidden(n\OBJ2)) Then HideEntity(n\OBJ2)
							Else
								If n\State <= -10.0
									If EntityHidden(n\OBJ2) Then ShowEntity(n\OBJ2)
									EntityAlpha(n\OBJ2, Min(Sqr(Dist) - opt\CameraFogFar * LightVolume * 0.6, 1.0))
								EndIf
							EndIf
						EndIf
					Else
						If (Not EntityHidden(n\OBJ2)) Then HideEntity(n\OBJ2)
					EndIf
				EndIf
				;[End Block]
			Case NPCType096
				;[Block]
				Dist = EntityDistanceSquared(me\Collider, n\Collider)
				Angle = WrapAngle(DeltaYaw(n\Collider, me\Collider))
				
				If wi\SCRAMBLE > 0 And Dist < PowTwo(opt\CameraFogFar * LightVolume) And (Angle < 135.0 Lor Angle > 225.0) And EntityVisible(Camera, n\OBJ2)
					If EntityHidden(n\OBJ2) Then ShowEntity(n\OBJ2)
					ScaleSprite(n\OBJ2, Rnd(0.06, 0.08), Rnd(0.07, 0.09))
					PositionEntity(n\OBJ2, Rnd(0.1) - 0.05, Rnd(0.1) - 0.05, Rnd(0.1) - 0.05)
				Else
					If (Not EntityHidden(n\OBJ2)) Then HideEntity(n\OBJ2)
				EndIf
				If Dist < 16.0 Then GiveAchievement(Achv096)
				
				Select n\State
					Case 0.0 ; ~ Sitting
						;[Block]
						If Dist < 64.0
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\Music\096.ogg", 0)
								n\SoundCHN_IsStream = True
							EndIf
							
							If n\State3 = -1.0
								AnimateNPC(n, 936.0, 1263.0, 0.1, False)
								If n\Frame >= 1262.9
									SetNPCFrame(n, 312.0)
									n\State3 = 0.0
									n\State = 1.0
								EndIf
							Else
								AnimateNPC(n, 936.0, 1263.0, 0.1)
								If n\State3 < 70.0 * 6.0
									n\State3 = n\State3 + fps\Factor[0]
								Else
									If Rand(5) = 1
										n\State3 = -1.0
									Else
										n\State3 = 70.0 * Rand(0, 3)
									EndIf
								EndIf
							EndIf
							
							If (Not chs\NoTarget)
								If Dist < PowTwo(opt\CameraFogFar * LightVolume)
									If wi\SCRAMBLE = 0 And (Angle < 135.0 Lor Angle > 225.0) And (EntityVisible(Camera, n\OBJ2) And EntityInView(n\OBJ2, Camera))
										If me\BlinkTimer < -16.0 Lor me\BlinkTimer > -6.0
											PlaySound_Strict(LoadTempSound("SFX\SCP\096\Triggered.ogg"), True)
											
											me\CurrCameraZoom = 10.0
											
											SetNPCFrame(n, 194.0)
											
											StopStream_Strict(n\SoundCHN) : n\SoundCHN = 0 : n\SoundCHN_IsStream = False
											n\Sound = 0
											
											n\State3 = 0.0
											n\State = 2.0
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						UpdateStreamSoundOrigin(n\SoundCHN, Camera, n\Collider, 8.0, 1.0, True)
						;[End Block]
					Case 1.0 ; ~ Walking around
						;[Block]
						If Dist < 256.0
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\Music\096.ogg", 0)
								n\SoundCHN_IsStream = True
							EndIf
							
							PrevFrame = n\Frame
							
							If n\Frame >= 422.0
								n\State2 = n\State2 + fps\Factor[0]
								If n\State2 > 1000.0
									If n\State2 > 1600.0 Then n\State2 = Rnd(0.0, 500.0)
									
									If n\Frame < 1382.0
										n\CurrSpeed = CurveValue(n\Speed * 0.1, n\CurrSpeed, 5.0)
										AnimateNPC(n, 1369.0, 1382.0, n\CurrSpeed * 45.0, False)
									Else
										n\CurrSpeed = CurveValue(n\Speed * 0.1, n\CurrSpeed, 5.0)
										AnimateNPC(n, 1383.0, 1456.0, n\CurrSpeed * 45.0)
									EndIf
									
									If MilliSecs() > n\State3
										n\LastSeen = 0
										If EntityVisible(me\Collider, n\Collider)
											n\LastSeen = 1
										Else
											If (Not EntityHidden(n\Collider)) Then HideEntity(n\Collider)
											EntityPick(n\Collider, 1.5)
											If PickedEntity() <> 0 Then n\Angle = EntityYaw(n\Collider) + Rnd(80.0, 110.0)
											If EntityHidden(n\Collider) Then ShowEntity(n\Collider)
										EndIf
										n\State3 = MilliSecs() + 2000
									EndIf
									
									If n\LastSeen
										PointEntity(n\OBJ, me\Collider)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 130.0), 0.0)
										If Dist < 2.25 Then n\State2 = 0.0
									Else
										RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle, EntityYaw(n\Collider), 50.0), 0.0)
									EndIf
								Else
									If n\Frame > 472.0 ; ~ Walk to idle
										n\CurrSpeed = CurveValue(n\Speed * 0.05, n\CurrSpeed, 8.0)
										AnimateNPC(n, 1383.0, 1469.0, n\CurrSpeed * 45.0, False)
										If n\Frame >= 1468.9 Then SetNPCFrame(n, 423.0)
									Else
										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 4.0)
										AnimateNPC(n, 423.0, 471.0, 0.2)
									EndIf
								EndIf
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							Else
								AnimateNPC(n, 312.0, 422.0, 0.3, False)
							EndIf
							
							If (Not chs\NoTarget)
								If Dist < PowTwo(opt\CameraFogFar * LightVolume)
									If wi\SCRAMBLE = 0 And (Angle < 135.0 Lor Angle > 225.0) And (EntityVisible(Camera, n\OBJ2) And EntityInView(n\OBJ2, Camera))
										If me\BlinkTimer < -16.0 Lor me\BlinkTimer > -6.0
											PlaySound_Strict(LoadTempSound("SFX\SCP\096\Triggered.ogg"), True)
											
											me\CurrCameraZoom = 10.0
											
											If n\Frame >= 422.0 Then SetNPCFrame(n, 677.0)
											
											StopStream_Strict(n\SoundCHN) : n\SoundCHN = 0 : n\SoundCHN_IsStream = False
											n\Sound = 0
											
											n\State = 3.0
										EndIf
									EndIf
								EndIf
							EndIf
							
							If n\CurrSpeed > 0.001
								If (PrevFrame < 1383.0 And n\Frame >= 1383.0) Lor (PrevFrame < 1420.0 And n\Frame >= 1420.0) Lor (PrevFrame < 1466.0 And n\Frame >= 1466.0) Then PlaySound2(snd_I\Step2SFX[Rand(10, 12)], Camera, n\Collider, 8.0, Rnd(0.8, 1.0))
							EndIf
						EndIf
						UpdateStreamSoundOrigin(n\SoundCHN, Camera, n\Collider, 14.0, 1.0, True)
						;[End Block]
					Case 2.0, 3.0, 4.0 ; ~ Triggered
						;[Block]
						If n\SoundCHN = 0
							n\SoundCHN = StreamSound_Strict("SFX\Music\096Angered.ogg", 0)
							n\SoundCHN_IsStream = True
						EndIf
						UpdateStreamSoundOrigin(n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True)
						
						If n\State = 2.0 ; ~ Get up
							If n\Frame < 312.0
								AnimateNPC(n, 193.0, 311.0, 0.3, False)
								If n\Frame > 310.9
									SetNPCFrame(n, 737.0)
									n\State = 3.0
								EndIf
							ElseIf n\Frame >= 312.0 And n\Frame <= 422.0
								AnimateNPC(n, 312.0, 422.0, 0.3, False)
								If n\Frame > 421.9 Then SetNPCFrame(n, 677.0)
							Else
								AnimateNPC(n, 677.0, 736.0, 0.3, False)
								If n\Frame > 735.9
									SetNPCFrame(n, 737.0)
									n\State = 3.0
								EndIf
							EndIf
						ElseIf n\State = 3.0
							AnimateNPC(n, 677.0, 737.0, 0.3, False)
							If n\Frame >= 737.0 Then n\State2 = 0.0 : n\State = 4.0
						ElseIf n\State = 4.0
							n\State2 = n\State2 + fps\Factor[0]
							If n\State2 > 70.0 * 26.0
								AnimateNPC(n, 823.0, 847.0, n\Speed * 8.0, False)
								If n\Frame > 846.9
									StopStream_Strict(n\SoundCHN) : n\SoundCHN = 0 : n\SoundCHN_IsStream = False
									n\State = 5.0
								EndIf
							Else
								AnimateNPC(n, 1471.0, 1556.0, 0.4)
							EndIf
						EndIf
						;[End Block]
					Case 5.0 ; ~ Chasing player
						;[Block]
						me\CurrCameraZoom = CurveValue(Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * 10.0), me\CurrCameraZoom, 8.0)
						
						If n\Target = Null
							If n\SoundCHN = 0
								n\SoundCHN = StreamSound_Strict("SFX\SCP\096\Scream.ogg", 0)
								n\SoundCHN_IsStream = True
							EndIf
							UpdateStreamSoundOrigin(n\SoundCHN, Camera, n\Collider, 7.5, 1.0, True)
							
							If n\SoundCHN2 = 0
								n\SoundCHN2 = StreamSound_Strict("SFX\Music\096Chase.ogg", 0)
								n\SoundCHN2_IsStream = True
							Else
								SetStreamVolume_Strict(n\SoundCHN2, Min(Max(8.0 - Sqr(Dist), 0.6), 1.0) * opt\VoiceVolume * opt\MasterVolume)
							EndIf
						EndIf
						
						If (Not me\Terminated)
							If MilliSecs() > n\State3
								n\LastSeen = 0
								If n\Target = Null
									If EntityVisible(me\Collider, n\Collider) Then n\LastSeen = 1
								Else
									If EntityVisible(n\Target\Collider, n\Collider) Then n\LastSeen = 1
								EndIf
								n\State3 = MilliSecs() + 2000
							EndIf
							
							If chs\NoTarget And n\Target = Null Then n\LastSeen = 0
							
							If n\LastSeen = 1
								n\PathTimer = Max(70.0 * 3.0, n\PathTimer)
								n\PathStatus = PATH_STATUS_NO_SEARCH
								
								If n\Target <> Null Then Dist = EntityDistanceSquared(n\Target\Collider, n\Collider)
								
								If Dist < 7.84 Lor n\Frame < 150.0
									If n\Frame > 193.0 Then n\Frame = 2.0 ; ~ Go to the start of the jump animation
									
									AnimateNPC(n, 2.0, 193.0, 0.7)
									
									If Dist > 1.0
										n\CurrSpeed = CurveValue(n\Speed * 2.0, n\CurrSpeed, 15.0)
									Else
										n\CurrSpeed = 0.0
										
										If n\Target = Null
											If (Not chs\GodMode)
												PlaySound_Strict(snd_I\DamageSFX[4])
												
												Pvt = CreatePivot()
												me\CameraShake = 30.0
												me\BlurTimer = 2000.0
												msg\DeathMsg = Format(GetLocalString("death", "096"), SubjectName)
												For i = 0 To 6
													PositionEntity(Pvt, EntityX(me\Collider) + Rnd(-0.1, 0.1), EntityY(me\Collider) - 0.05, EntityZ(me\Collider) + Rnd(-0.1, 0.1))
													TurnEntity(Pvt, 90.0, 0.0, 0.0)
													EntityPick(Pvt, 0.3)
													
													de.Decals = CreateDecal(Rand(DECAL_BLOOD_DROP_1, DECAL_BLOOD_DROP_2), PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0, Rnd(0.2, 0.6))
													EntityParent(de\OBJ, PlayerRoom\OBJ)
												Next
												FreeEntity(Pvt) : Pvt = 0
												Kill(True) : me\KillAnim = 1
											EndIf
										EndIf
									EndIf
									
									If n\Target = Null
										PointEntity(n\Collider, me\Collider)
									Else
										PointEntity(n\Collider, n\Target\Collider)
									EndIf
								Else
									If n\Target = Null
										PointEntity(n\OBJ, me\Collider)
									Else
										PointEntity(n\OBJ, n\Target\Collider)
									EndIf
									
									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 5.0), 0.0)
									
									If n\Frame > 847.0 Then n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									
									If n\Frame < 906.0
										AnimateNPC(n, 737.0, 906.0, n\Speed * 8.0, False)
									Else
										AnimateNPC(n, 907.0, 935.0, n\CurrSpeed * 8.0)
									EndIf
								EndIf
								
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							Else
								If n\PathTimer <= 0.0
									If n\Target <> Null
										n\PathStatus = FindPath(n, EntityX(n\Target\Collider), EntityY(n\Target\Collider) + 0.2, EntityZ(n\Target\Collider))
									Else
										n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
									EndIf
									n\PathTimer = 70.0 * 5.0
								Else
									If n\PathStatus = PATH_STATUS_FOUND And (Not chs\NoTarget)
										If n\Path[n\PathLocation] = Null
											If n\PathLocation > MaxPathLocations - 1
												n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Else
											PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
											
											RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 5.0), 0.0)
											
											If n\Frame > 847.0 Then n\CurrSpeed = CurveValue(n\Speed * 1.5, n\CurrSpeed, 15.0)
											MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
											
											If n\Frame < 906.0
												AnimateNPC(n, 737.0, 906.0, n\Speed * 8.0, False)
											Else
												AnimateNPC(n, 907.0, 935.0, n\CurrSpeed * 8.0)
											EndIf
											
											Dist2 = EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ)
											If Dist2 < 0.64
												If n\Path[n\PathLocation]\door <> Null
													If (Not n\Path[n\PathLocation]\door\Open)
														n\Path[n\PathLocation]\door\Open = True
														n\Path[n\PathLocation]\door\FastOpen = True
														PlaySound2(snd_I\OpenDoorFastSFX, Camera, n\Path[n\PathLocation]\door\FrameOBJ)
													EndIf
												EndIf
												If Dist2 < PathLocationDist Then n\PathLocation = n\PathLocation + 1
											EndIf
										EndIf
										n\PathTimer = Max(0.0, n\PathTimer - fps\Factor[0])
									Else
										n\CurrSpeed = 0.0
										AnimateNPC(n, 1471.0, 1556.0, 0.1)
										n\PathTimer = Max(0.0, n\PathTimer - fps\Factor[0] * 2.0)
									EndIf
								EndIf
							EndIf
							
							If Rand(50) = 1
								If Dist > 400.0 Then TeleportCloser(n)
							EndIf
						Else
							AnimateNPC(n, Min(27.0, AnimTime(n\OBJ)), 193.0, 0.5)
						EndIf
						;[End Block]
				End Select
				
				If n\State > 1.0
					For e.Events = Each Events
						If e\EventID = e_room2_servers_hcz
							If e\EventState = 0.0
								For i = 0 To 1
									e\room\RoomDoors[i]\Locked = 0
								Next
								e\EventState = 70.0 * 45.0
								RemoveEvent(e)
							EndIf
							Exit
						EndIf
					Next
					If n\Target = Null Then CanSave = 2
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				
				RotateEntity(n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0)
				;[End Block]
			Case NPCType049
				;[Block]
				; ~ n\State: The "main state" of the NPC
				
				; ~ n\State2: Attacks the player when the value is above 0.0
				
				; ~ n\State3: Timer for updating the path again
				
				PrevFrame = n\Frame
				
				Dist = EntityDistanceSquared(me\Collider, n\Collider)
				
				UpdateNPCBlinking(n)
				
				If Dist >= 0.25
					Remove714Timer = Min(Remove714Timer + fps\Factor[0], 500.0)
					RemoveHazmatTimer = Min(RemoveHazmatTimer + fps\Factor[0], 500.0)
				ElseIf (Not chs\NoTarget)
					If EntityVisible(me\Collider, n\Collider)
						If n\State > 1 And n\State <> 3
							If wi\HazmatSuit > 0
								RemoveHazmatTimer = RemoveHazmatTimer - (fps\Factor[0] * 1.5)
								If RemoveHazmatTimer < 200.0 And RemoveHazmatTimer + fps\Factor[0] * 1.5 >= 200.0 And (Not ChannelPlaying(n\SoundCHN2))
									n\SoundCHN2 = PlaySound2(LoadTempSound("SFX\SCP\049\TakeOffHazmat.ogg"), Camera, n\Collider, 10.0, 1.0, True)
								ElseIf RemoveHazmatTimer =< 0.0
									For i = 0 To 2
										If RemoveHazmatTimer < -(i * (250.0 * (wi\HazmatSuit = 4))) And RemoveHazmatTimer + fps\Factor[0] * 1.5 >= -(i * (250.0 * (wi\HazmatSuit = 4)))
											me\CameraShake = 2.0
											If i = 2
												For i = 0 To MaxItemAmount - 1
													If Inventory(i) <> Null
														If Inventory(i)\ItemTemplate\ID >= it_hazmatsuit And Inventory(i)\ItemTemplate\ID =< it_veryfinehazmatsuit
															CreateMsg(GetLocalString("msg", "suit.destroyed"))
															wi\HazmatSuit = 0
															RemoveItem(Inventory(i))
															Exit
														EndIf
													EndIf
												Next
											EndIf
										EndIf
									Next
								EndIf
							ElseIf I_714\Using > 0
								me\BlurTimer = me\BlurTimer + (fps\Factor[0] * 2.5)
								If I_268\InvisibilityOn
									Remove714Timer = Min(Remove714Timer, 499.0)
								Else
									Remove714Timer = Remove714Timer - (fps\Factor[0] * (3.0 / I_714\Using))
									If Remove714Timer < 200.0 And Remove714Timer + fps\Factor[0] * 1.5 >= 200.0 And (Not ChannelPlaying(n\SoundCHN2))
										If I_714\Using = 2 Then n\SoundCHN2 = PlaySound2(LoadTempSound("SFX\SCP\049\714Equipped.ogg"), Camera, n\Collider, 10.0, 1.0, True)
									ElseIf Remove714Timer =< 0.0
										For i = 0 To MaxItemAmount - 1
											If Inventory(i) <> Null
												If Inventory(i)\ItemTemplate\ID = it_scp714 Lor Inventory(i)\ItemTemplate\ID = it_coarse714
													CreateMsg(GetLocalString("msg", "714.forceremoved"))
													I_714\Using = 0 : DropItem(Inventory(i))
													Exit
												EndIf
											EndIf
										Next
									EndIf
								EndIf
							Else
								me\CurrCameraZoom = 20.0
								me\BlurTimer = 500.0
								
								If (Not chs\GodMode)
									If PlayerRoom\RoomTemplate\RoomID = r_cont2_049
										For e.Events = Each Events
											If e\EventID = e_cont2_049
												e\EventState = -1.0
												Exit
											EndIf
										Next
										If me\FallTimer >= 0.0
											ShowEntity(me\Head)
											PositionEntity(me\Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
											ResetEntity(me\Head)
											RotateEntity(me\Head, 0.0, EntityYaw(Camera) + Rnd(-45.0, 45.0), 0.0)
											me\FallTimer = Min(-1.0, me\FallTimer)
										EndIf
									Else
										msg\DeathMsg = GetLocalString("death", "049")
										Kill() : me\KillAnim = 0
									EndIf
									PlaySound_Strict(LoadTempSound("SFX\SCP\049\Horror.ogg"))
									LoadNPCSound(n, "SFX\SCP\049\Kidnap" + Rand(0, 1) + ".ogg", 1)
									n\SoundCHN2 = PlaySound2(n\Sound2, Camera, n\OBJ, 10.0, 1.0, True)
									n\State = 3.0
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				If Dist < 16.0
					If EntityInView(n\Collider, Camera) Then GiveAchievement(Achv049)
				EndIf
				
				If n\Idle > 0.1
					If PlayerRoom\RoomTemplate\RoomID <> r_cont2_049
						n\Idle = Max(n\Idle - (1 + SelectedDifficulty\AggressiveNPCs) * fps\Factor[0], 0.1)
					EndIf
					n\DropSpeed = 0.0
					If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN) : n\SoundCHN = 0
					If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2) : n\SoundCHN2 = 0
					PositionEntity(n\Collider, 0.0, -500.0, 0.0)
					ResetEntity(n\Collider)
				Else
					n\SoundCHN = LoopSound2(NPCSound[SOUND_NPC_049_BREATH], n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True) ; ~ Breath channel
					If n\Idle = 0.1
						If PlayerInReachableRoom()
							For i = 0 To MaxRoomAdjacents - 1
								If PlayerRoom\Adjacent[i] <> Null
									For j = 0 To MaxRoomAdjacents - 1
										If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
											If PlayerRoom\Adjacent[i]\Adjacent[j] <> PlayerRoom
												If PlayerRoom\Adjacent[i]\Adjacent[j]\RoomCenter <> 0
													TeleportEntity(n\Collider, EntityX(PlayerRoom\Adjacent[i]\Adjacent[j]\RoomCenter, True), 0.5, EntityZ(PlayerRoom\Adjacent[i]\Adjacent[j]\RoomCenter, True), n\CollRadius, True)
												Else
													TeleportEntity(n\Collider, PlayerRoom\Adjacent[i]\Adjacent[j]\x, 0.5, PlayerRoom\Adjacent[i]\Adjacent[j]\z, n\CollRadius, True)
												EndIf
												Exit
											EndIf
										EndIf
									Next
									Exit
								EndIf
							Next
							n\Idle = 0.0
						EndIf
					EndIf
					
					Select n\State
						Case 0.0 ; ~ Nothing (used for events)
							;[Block]
							;[End Block]
						Case 1.0 ; ~ Looking around before getting active
							;[Block]
							If n\Frame >= 538.0
								AnimateNPC(n, 659.0, 538.0, -0.45, False)
								If n\Frame > 537.9 Then SetNPCFrame(n, 37.0)
							Else
								AnimateNPC(n, 37.0, 269.0, 0.7, False)
								If n\Frame > 268.9 Then n\State = 2.0
							EndIf
							;[End Block]
						Case 2.0 ; ~ Being active
							;[Block]
							If (Dist < PowTwo(HideDistance * 2.0)) And n\Idle = 0 And PlayerInReachableRoom(True)
								n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
								PlayerSeeAble = NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume)
								If n\State2 > 0.0
									If PlayerSeeAble = 1 Then n\State2 = 70.0 * 2.0
									; ~ Playing a sound after detecting the player
									If n\PrevState <= 1 And (Not ChannelPlaying(n\SoundCHN2))
										LoadNPCSound(n, "SFX\SCP\049\Spotted" + Rand(0, 6) + ".ogg", 1)
										n\SoundCHN2 = PlaySound2(n\Sound2, Camera, n\OBJ, 10.0, 1.0, True)
										n\PrevState = 2
									EndIf
									n\PathStatus = PATH_STATUS_NO_SEARCH
									n\PathTimer = 0.0
									n\PathLocation = 0
									
									PointEntity(n\Collider, me\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									
									If Dist >= 0.25
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										
										If Dist < 9.0
											AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 428.0), 387.0), 463.0, n\CurrSpeed * 38.0) ; ~ WALK CYCLE 8 + WALK CYCLE 7
										Else
											If n\Frame > 428.0
												AnimateNPC(n, Min(AnimTime(n\OBJ), 463.0), 498.0, n\CurrSpeed * 38.0, False)  ; ~ WALK CYCLE 7 + WALK CYCLE 6
												If n\Frame > 497.9 Then SetNPCFrame(n, 358.0)
											Else
												AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 358.0), 346.0), 393.0, n\CurrSpeed * 38.0) ; IDLE TO WALK + WALK CYCLE 3
											EndIf
										EndIf
									EndIf
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0 - (SelectedDifficulty\OtherFactors * 2.0))
								Else ; ~ Finding a path to the player
									If PlayerSeeAble = 1 Then n\State2 = 70.0 * 2.0
									If n\PathStatus = PATH_STATUS_FOUND ; ~ Path to player found
										While n\Path[n\PathLocation] = Null
											If n\PathLocation > MaxPathLocations - 1
												n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
												Exit
											Else
												n\PathLocation = n\PathLocation + 1
											EndIf
										Wend
										If n\Path[n\PathLocation] <> Null
											n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
											PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
											RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
											MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
											
											AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 358.0), 346.0), 393.0, n\CurrSpeed * 38.0)
											n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0 - (SelectedDifficulty\OtherFactors * 2.0))
											
											; ~ Playing a sound if he hears the player
											If n\PrevState = 0 And (Not ChannelPlaying(n\SoundCHN2))
												If Rand(30) = 1
													LoadNPCSound(n, "SFX\SCP\049\Searching6.ogg", 1)
												Else
													LoadNPCSound(n, "SFX\SCP\049\Searching" + Rand(0, 5) + ".ogg", 1)
												EndIf
												n\SoundCHN2 = PlaySound2(n\Sound2, Camera, n\Collider, 10.0, 1.0, True)
												n\PrevState = 1
											EndIf
											
											UseDoorNPC(n)
											
											; ~ Resetting the "PrevState" value randomly, to make SCP-049 talking randomly 
											If Rand(600) = 1 And (Not ChannelPlaying(n\SoundCHN2)) Then n\PrevState = 0
											
											If n\PrevState > 1 Then n\PrevState = 1
										EndIf
									Else ; ~ Stands still and tries to find a path
										n\PathTimer = n\PathTimer + fps\Factor[0]
										n\CurrSpeed = 0.0
										If n\PathTimer > 70.0 * (5.0 - (2.0 * SelectedDifficulty\AggressiveNPCs))
											n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
											n\PathTimer = 0.0
											n\State3 = 0.0
											
											; ~ Attempt to find a room (the PlayerRoom or one of it's adjacent rooms) for SCP-049 to go to but select the one closest to him
											If n\PathStatus <> PATH_STATUS_FOUND
												Local ClosestDist# = EntityDistanceSquared(PlayerRoom\OBJ, n\Collider)
												Local ClosestRoom.Rooms = PlayerRoom
												Local CurrDist# = 0.0
												
												For i = 0 To MaxRoomAdjacents - 1
													If PlayerRoom\Adjacent[i] <> Null
														CurrDist = EntityDistanceSquared(PlayerRoom\Adjacent[i]\OBJ, n\Collider)
														If CurrDist < ClosestDist
															ClosestDist = CurrDist
															ClosestRoom = PlayerRoom\Adjacent[i]
														EndIf
													EndIf
												Next
												n\PathStatus = FindPath(n, EntityX(ClosestRoom\OBJ), 0.5, EntityZ(ClosestRoom\OBJ))
											EndIf
											
											; ~ Making 3 attempts at finding a path
											While Int(n\State3) < 3.0
												; ~ Breaking up the path if no "real" path has been found (only 1 waypoint and it is too close)
												If n\PathStatus = PATH_STATUS_FOUND
													If n\Path[1] <> Null
														If n\Path[2] = Null And EntityDistanceSquared(n\Path[1]\OBJ, n\Collider) < 0.16
															n\PathLocation = 0
															n\PathStatus = PATH_STATUS_NO_SEARCH
														EndIf
													EndIf
													If n\Path[0] <> Null And n\Path[1] = Null
														n\PathLocation = 0
														n\PathStatus = PATH_STATUS_NO_SEARCH
													EndIf
												EndIf
												
												; ~ No path could still be found, just make SCP-049 go to a room (further away than the very first attempt)
												If n\PathStatus <> PATH_STATUS_FOUND
													ClosestDist = 10000.0 ; ~ Prevent the PlayerRoom to be considered the closest, so SCP-049 wouldn't try to find a path there
													ClosestRoom.Rooms = PlayerRoom
													CurrDist = 0.0
													For i = 0 To MaxRoomAdjacents - 1
														If PlayerRoom\Adjacent[i] <> Null
															CurrDist = EntityDistanceSquared(PlayerRoom\Adjacent[i]\OBJ, n\Collider)
															If CurrDist < ClosestDist
																ClosestDist = CurrDist
																For j = 0 To MaxRoomAdjacents - 1
																	If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
																		If PlayerRoom\Adjacent[i]\Adjacent[j] <> PlayerRoom
																			ClosestRoom = PlayerRoom\Adjacent[i]\Adjacent[j]
																			Exit
																		EndIf
																	EndIf
																Next
															EndIf
														EndIf
													Next
													n\PathStatus = FindPath(n, EntityX(ClosestRoom\OBJ), 0.5, EntityZ(ClosestRoom\OBJ))
												EndIf
												
												; ~ Making SCP-049 skip waypoints for doors he can't interact with, but only if the actual path is behind him
												If n\PathStatus = PATH_STATUS_FOUND
													If n\Path[1] <> Null
														If n\Path[1]\door <> Null
															If (n\Path[1]\door\Locked > 0 Lor n\Path[1]\door\KeyCard <> 0 Lor n\Path[1]\door\Code <> 0) And (Not n\Path[1]\door\Open)
																Repeat
																	If n\PathLocation > MaxPathLocations - 1
																		n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
																		Exit
																	Else
																		n\PathLocation = n\PathLocation + 1
																	EndIf
																	If n\Path[n\PathLocation] <> Null
																		If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > (45.0 - Abs(DeltaYaw(n\Collider, n\Path[1]\OBJ)))
																			n\State3 = 3.0
																			Exit
																		EndIf
																	EndIf
																Forever
															Else
																n\State3 = 3.0
															EndIf
														Else
															n\State3 = 3.0
														EndIf
													EndIf
												EndIf
												n\State3 = n\State3 + 1.0
											Wend
										EndIf
										AnimateNPC(n, 269.0, 345.0, 0.2)
									EndIf
								EndIf
								
								If n\CurrSpeed > 0.005
									If (PrevFrame < 361.0 And n\Frame >= 361.0) Lor (PrevFrame < 377.0 And n\Frame >= 377.0) Lor (PrevFrame < 431.0 And n\Frame >= 431.0) Lor (PrevFrame < 447.0 And n\Frame >= 447.0) Then PlaySound2(snd_I\Step2SFX[Rand(7, 9)], Camera, n\Collider, 8.0, Rnd(0.8, 1.0))
								EndIf
								
								UpdateSoundOrigin(n\SoundCHN2, Camera, n\OBJ, 10.0, 1.0, True)
							ElseIf n\Idle = 0
								If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN) : n\SoundCHN = 0
								If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2) : n\SoundCHN2 = 0
								If PlayerInReachableRoom(True) And InFacility = NullFloor ; ~ Player is in a room where SCP-049 can teleport to
									If Rand(4 - (2 * SelectedDifficulty\AggressiveNPCs)) = 1
										TeleportCloser(n)
									Else
										n\Idle = 70.0 * 60.0
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 3.0 ; ~ The player was killed by SCP-049
							;[Block]
							AnimateNPC(n, 537.0, 660.0, 0.7, False)
							
							PositionEntity(n\Collider, CurveValue(EntityX(me\Collider), EntityX(n\Collider), 20.0), EntityY(n\Collider), CurveValue(EntityZ(me\Collider), EntityZ(n\Collider), 20.0))
							RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(me\Collider) - 180.0, EntityYaw(n\Collider), 40.0), 0.0)
							;[End Block]
						Case 4.0 ; ~ Standing on catwalk
							;[Block]
							If Dist < 64.0
								PointEntity(n\Collider, me\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								AnimateNPC(n, 18.0, 19.0, 0.05)
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								
								n\State3 = 1.0
							ElseIf Dist > PowTwo(HideDistance * 0.8) And n\State3 > 0.0
								For r.Rooms = Each Rooms
									If EntityDistanceSquared(r\OBJ, n\Collider) < 16.0
										If r\RoomCenter <> 0
											TeleportEntity(n\Collider, EntityX(r\RoomCenter, True), EntityY(r\OBJ) + 0.5, EntityZ(r\RoomCenter, True), n\CollRadius, True)
										Else
											TeleportEntity(n\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.5, EntityZ(r\OBJ), n\CollRadius, True)
										EndIf
										Exit
									EndIf
								Next
								n\State3 = 0.0
								n\State = 2.0
							EndIf
							;[End Block]
						Case 5.0 ; ~ Going to surveillance room
							;[Block]
							PlayerSeeAble = NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume, 60.0, True)
							If PlayerSeeAble = 1
								PlaySound_Strict(LoadTempSound("SFX\SCP\049\Room2SLSpawn.ogg"))
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\PathLocation = 0
								n\PathTimer = 0.0
								n\PrevState = 0
								n\State3 = 0.0
								n\State2 = 70.0 * 2.0
								n\State = 2.0
							ElseIf PlayerSeeAble = 2 And n\State3 > 0.0
								n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
							Else
								If n\State3 = 6.0
									If EntityDistanceSquared(n\Collider, me\Collider) > PowTwo(HideDistance)
										n\PathStatus = PATH_STATUS_NO_SEARCH
										n\PathLocation = 0
										n\PathTimer = 0.0
										n\PrevState = 0
										n\State3 = 0.0
										n\State = 2.0
									Else
										If n\PathStatus <> PATH_STATUS_FOUND Then n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
									EndIf
								EndIf
								
								If n\PathStatus = PATH_STATUS_FOUND
									If n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 10.0 - SelectedDifficulty\OtherFactors)
										
										UseDoorNPC(n)
										
										AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 358.0), 346.0), 393.0, n\CurrSpeed * 38.0)
									EndIf
								Else
									Select n\PrevState
										Case 0
											;[Block]
											AnimateNPC(n, 269.0, 345.0, 0.2)
											;[End Block]
										Case 1
											;[Block]
											AnimateNPC(n, 661.0, 891.0, 0.4, False)
											;[End Block]
										Case 2
											;[Block]
											AnimateNPC(n, 892.0, 1119.0, 0.4, False)
											;[End Block]
									End Select
								EndIf
							EndIf
							
							If PlayerRoom\RoomTemplate\RoomID = r_room2_sl Then ShouldPlay = 19
							
							If n\CurrSpeed > 0.005
								If (PrevFrame < 361.0 And n\Frame >= 361.0) Lor (PrevFrame < 377.0 And n\Frame >= 377.0) Lor (PrevFrame < 431.0 And n\Frame >= 431.0) Lor (PrevFrame < 447.0 And n\Frame >= 447.0) Then PlaySound2(snd_I\Step2SFX[Rand(7, 9)], Camera, n\Collider, 8.0, Rnd(0.8, 1.0))
							EndIf
							
							UpdateSoundOrigin(n\SoundCHN2, Camera, n\OBJ, 10.0, 1.0, True)
							;[End Block]
					End Select
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.22, EntityZ(n\Collider, True), True)
				RotateEntity(n\OBJ, 0.0, n\Angle, 0.0, True)
				
				n\LastSeen = Max(n\LastSeen - fps\Factor[0], 0.0)
				;[End Block]
			Case NPCType049_2
				;[Block]
				; ~ n\State: Main State
				
				; ~ n\State2: A timer used for the player detection
				
				If (Not n\IsDead)
					PrevFrame = n\Frame
					
					UpdateNPCBlinking(n)
					
					Select n\State
						Case 0.0 ; ~ Just lies
							;[Block]
							n\SoundCHN = LoopSound2(NPCSound[SOUND_NPC_049_2_RESTING], n\SoundCHN, Camera, n\Collider, 4.0, 1.0, True)
							If Rand(2000) = 1
								If EntityDistanceSquared(n\Collider, me\Collider) < 9.0 - (me\Crouch * 4.5) Then n\State = 1.0
							EndIf
							;[End Block]
						Case 1.0 ; ~ Stands up
							;[Block]
							AnimateNPC(n, 1.0, 556.0, 1.0, False)
							If (PrevFrame < 288.0 And n\Frame >= 288.0) Lor (PrevFrame < 350.0 And n\Frame >= 350.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							If n\Frame >= 555.9 Then n\State = 2.0
							;[End Block]
						Case 2.0 ; ~ Player/NPC is visible, tries to kill
							;[Block]
							n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
							If n\State2 > 0.0
								If n\Target = Null
									If NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume) = 1 Then n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
									PointEntity(n\Collider, me\Collider)
									Dist = EntityDistanceSquared(n\Collider, me\Collider)
								Else
									If NPCSeesNPC(n\Target, n) Then n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
									PointEntity(n\Collider, n\Target\Collider)
									Dist = EntityDistanceSquared(n\Collider, n\Target\Collider)
								EndIf
								
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								
								AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 714.0), 705.0), 794.0, n\CurrSpeed * 38.0)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								
								If Dist < 0.49
									SetNPCFrame(n, 795.0)
									n\State = 4.0
								EndIf
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\PathLocation = 0
								
								If (PrevFrame < 733.0 And n\Frame >= 733.0) Lor (PrevFrame < 773.0 And n\Frame >= 773.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							Else
								n\Target = Null
								n\State = 3.0
							EndIf
							;[End Block]
						Case 3.0 ; ~ Player/NPC isn't visible, tries to find
							;[Block]
							If n\PathTimer <= 0.0 ; ~ Update path
								n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider) + 0.1, EntityZ(me\Collider))
								If n\PathStatus = PATH_STATUS_FOUND
									While n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_FOUND
											Exit
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Wend
									If n\PathLocation < MaxPathLocations - 1
										If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
											If n\Path[n\PathLocation]\door = Null
												If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
											EndIf
										EndIf
									EndIf
									UseDoorNPC(n)
								EndIf
								n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
							Else
								; ~ Still attack if the player is too close
								If (Not chs\NoTarget)
									If EntityDistanceSquared(n\Collider, me\Collider) < 0.49 And EntityVisible(me\Collider, n\Collider) Then n\State = 4.0
								EndIf
								
								If n\PathStatus = PATH_STATUS_FOUND
									If n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
										RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
										
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
										
										UseDoorNPC(n)
									EndIf
									n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
								Else
									n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
									n\PathTimer = n\PathTimer - (fps\Factor[0] * 2.0) ; ~ Timer goes down fast
								EndIf
							EndIf
							If n\CurrSpeed < 0.005
								If n\Frame > 713.0
									AnimateNPC(n, 795.0, 813.0, 0.7, False)
									If n\Frame > 812.9 Then SetNPCFrame(n, 557.0)
								Else
									AnimateNPC(n, 557.0, 704.0, 0.2)
								EndIf
							Else
								AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 713.0), 705.0), 794.0, n\CurrSpeed * 38.0)
								If (PrevFrame < 733.0 And n\Frame >= 733.0) Lor (PrevFrame < 773.0 And n\Frame >= 773.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
							
							If n\Target = Null
								If NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume) = 1
									n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
									n\State = 2.0
								EndIf
							EndIf
							
							For n2.NPCs = Each NPCs
								If n2\NPCType = NPCTypeMTF And (Not n2\IsDead)
									If NPCSeesNPC(n2, n) = 1
										n\Target = n2
										n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
										n\State = 2.0
										Exit
									EndIf
								EndIf
							Next
							;[End Block]
						Case 4.0 ; ~ Attacks
							;[Block]
							If (Not me\Terminated)
								n\CurrSpeed = 0.0
								If n\Target = Null
									PointEntity(n\Collider, me\Collider)
								Else
									PointEntity(n\Collider, n\Target\Collider)
								EndIf
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								If n\Frame <= 813.0
									AnimateNPC(n, 795.0, 813.0, 0.7, False)
									If n\Frame > 812.9
										If Rand(2) = 1
											SetNPCFrame(n, 814.0)
										Else
											SetNPCFrame(n, 879.0)
										EndIf
									EndIf
								Else
									If n\Frame < 879.0
										AnimateNPC(n, 814.0, 878.0, 0.4, False)
										Attack = (n\Frame >= 839.0 And PrevFrame < 839.0)
										If n\Frame > 877.9
											SetNPCFrame(n, 705.0)
											n\State = 2.0
										EndIf
									Else
										AnimateNPC(n, 879.0, 943.0, 0.4, False)
										Attack = (n\Frame >= 900.0 And PrevFrame < 900.0)
										If n\Frame > 942.9
											SetNPCFrame(n, 705.0)
											n\State = 2.0
										EndIf
									EndIf
									If Attack
										If n\Target = Null
											If EntityDistanceSquared(n\Collider, me\Collider) < 0.5625
												PlaySound_Strict(snd_I\DamageSFX[Rand(5, 8)])
												InjurePlayer(Rnd(0.55, 0.85) * DifficultyDMGMult, 0.0, 0.0, Rnd(0.25, 0.3) * DifficultyDMGMult, 0.2)
												me\CameraShake = 2.5
												
												If me\Injuries > 3.0
													msg\DeathMsg = Format(GetLocalString("death", "0492killed"), SubjectName)
													Kill(True)
												EndIf
											Else
												PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
											EndIf
										Else
											If EntityDistanceSquared(n\Collider, n\Target\Collider) < 0.5625
												PlaySound2(snd_I\DamageSFX[Rand(5, 8)], Camera, n\Target\OBJ)
												If n\Target\HP > 0
													n\Target\HP = Max(n\Target\HP - Rnd(30.0, 50.0), 0.0)
												Else
													n\Target\IsDead = True
													n\Target = Null
													n\State = 3.0
													Return
												EndIf
											Else
												PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
											EndIf
										EndIf
									EndIf
								EndIf
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							EndIf
							If n\Target <> Null
								If n\Target\IsDead
									n\State = 3.0
									n\Target = Null
								EndIf
							EndIf
							;[End Block]
					End Select
					
					; ~ Loop the breath sound
					If n\State > 1.0 Then n\SoundCHN = LoopSound2(NPCSound[SOUND_NPC_049_2_BREATH], n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True)
				Else
					AnimateNPC(n, 944.0, 982.0, 0.2, False)
				EndIf
				PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.2, EntityZ(n\Collider, True), True)
				RotateEntity(n\OBJ, -90.0, n\Angle, 0.0, True)
				;[End Block]
			Case NPCTypeGuard ; ~ TODO: WRITE A NEW AI
				;[Block]
				PrevFrame = n\Frame
				
				n\BoneToManipulate = ""
				n\ManipulateBone = False
				n\ManipulationType = 0
				n\NPCNameInSection = "Guard"
				
				Select n\State
					Case 1.0 ; ~ Aims and shoots at the player
						;[Block]
						If n\Frame < 39.0 Lor (n\Frame > 76.0 And n\Frame < 245.0) Lor (n\Frame > 248.0 And n\Frame < 302.0) Lor n\Frame > 344.0
							AnimateNPC(n, 345.0, 357.0, 0.2, False)
							If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						EndIf
						
						If (Not me\Terminated)
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							
							Local ShootAccuracy# = 0.4 + 0.5 * SelectedDifficulty\AggressiveNPCs
							Local DetectDistance# = 121.0
							
							; ~ If at Gate B increase his distance so that he can shoot the player from a distance after they are spotted.
							If PlayerRoom\RoomTemplate\RoomID = r_gate_b
								DetectDistance = 484.0
								
								; ~ Increase accuracy if the player is going slow
								ShootAccuracy = 0.51 - (10.0 * me\CurrSpeed)
							EndIf
							
							If Dist < PowTwo(DetectDistance)
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(Pvt, me\Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
								
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
								PointEntity(Pvt, me\Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
								
								If n\Reload = 0.0
									If n\State3 = 1.0
										Local InstaKillPlayer% = False
										
										msg\DeathMsg = Format(GetLocalString("death", "guard.default"), SubjectName)
										If PlayerRoom\RoomTemplate\RoomID = r_cont1_173 
											msg\DeathMsg = Format(GetLocalString("death", "guard.173"), SubjectName)
											InstaKillPlayer = True
										ElseIf PlayerRoom\RoomTemplate\RoomID = r_gate_b
											msg\DeathMsg = GetLocalString("death", "guard.gateb")
										EndIf
										
										PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 35.0)
										
										RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
										PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
										MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
										
										PointEntity(Pvt, me\Collider)
										Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ShootAccuracy, True, InstaKillPlayer)
										n\Reload = 8.0
									Else
										n\CurrSpeed = n\Speed
									EndIf
								EndIf
								
								If n\Reload > 0.0 And n\Reload <= 8.0
									AnimateNPC(n, 245, 248, 0.35)
								Else
									If n\Frame < 302.0 Then AnimateNPC(n, 302.0, 344.0, 0.35)
								EndIf
								
								FreeEntity(Pvt) : Pvt = 0
							Else
								AnimateNPC(n, 302.0, 344.0, 0.35)
							EndIf
							
							n\ManipulateBone = True
							n\BoneToManipulate = "Chest"
							
							If n\State2 = 10.0 ; ~ Hacky way of applying spine pitch to specific guards.
								n\ManipulationType = 3
							Else
								n\ManipulationType = 0
							EndIf
						Else
							n\State = 0.0
						EndIf
						;[End Block]
					Case 2.0 ; ~ Shoots
						;[Block]
						AnimateNPC(n, 245.0, 248.0, 0.35)
						If n\Reload = 0.0
							PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 15.0)
							Pvt = CreatePivot()
							RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
							PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
							
							SetEmitter(Null, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 13)
							n\Reload = 8.0
						EndIf
						;[End Block]
					Case 3.0 ; ~ Follows a path
						;[Block]
						If n\PathStatus = PATH_STATUS_FOUND
							If n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
								
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
								
								PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
								
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
								
								If EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ) < PathLocationDist Then n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							If n\PathTimer = 0.0 Then n\PathStatus = FindPath(n, n\EnemyX, n\EnemyY + 0.5, n\EnemyZ)
							
							wayPointCloseToPlayer = Null
							
							Pvt = CreatePivot()
							PositionEntity(Pvt, n\EnemyX, n\EnemyY, n\EnemyZ)
							For wp.WayPoints = Each WayPoints
								If EntityDistanceSquared(wp\OBJ, Pvt) < 4.0
									wayPointCloseToPlayer = wp
									Exit
								EndIf
							Next
							FreeEntity(Pvt) : Pvt = 0
							
							If wayPointCloseToPlayer <> Null
								n\PathTimer = 1.0
								If EntityVisible(wayPointCloseToPlayer\OBJ, n\Collider)
									If Abs(DeltaYaw(n\Collider, wayPointCloseToPlayer\OBJ)) > 0.0
										PointEntity(n\OBJ, wayPointCloseToPlayer\OBJ)
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
									EndIf
								EndIf
							Else
								n\PathTimer = 0.0
							EndIf
							
							If n\PathTimer = 1.0
								AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
								
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							EndIf
						EndIf
						;[End Block]
					Case 4.0 ; ~ Standing and looking around randomly
						;[Block]
						AnimateNPC(n, 77.0, 201.0, 0.2)
						
						If Rand(400) = 1 Then n\Angle = Rnd(-180.0, 180.0)
						
						RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + Sin(MilliSec / 50) * 2.0, EntityYaw(n\Collider), 150.0), 0.0, True)
						
						If EntityDistanceSquared(n\Collider, me\Collider) < 225.0
							If WrapAngle(EntityYaw(n\Collider) - DeltaYaw(n\Collider, me\Collider)) < 90.0 And EntityVisible(me\Collider, n\Collider) Then n\State = 1.0
						EndIf
						;[End Block]
					Case 5.0 ; ~ Following a target
						;[Block]
						RotateEntity(n\Collider, 0.0, CurveAngle(VectorYaw(n\EnemyX - EntityX(n\Collider), 0.0, n\EnemyZ - EntityZ(n\Collider)) + n\Angle, EntityYaw(n\Collider), 20.0), 0.0)
						
						Dist = DistanceSquared(EntityX(n\Collider), n\EnemyX, EntityZ(n\Collider), n\EnemyZ)
						
						AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
						
						If Dist > 4.0 Lor Dist < 1.0
							n\CurrSpeed = CurveValue(n\Speed * Sgn(Sqr(Dist) - 1.5) * 0.75, n\CurrSpeed, 10.0)
						Else
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
						EndIf
						
						MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
						;[End Block]
					Case 7.0 ; ~ Idle
						;[Block]
						AnimateNPC(n, 77.0, 201.0, 0.2)
						;[End Block]
					Case 8.0 ; ~ None
						;[Block]
						;[End Block]
					Case 9.0 ; ~ Looks at the player
						;[Block]
						AnimateNPC(n, 77.0, 201.0, 0.2)
						
						n\BoneToManipulate = "head"
						n\ManipulateBone = True
						n\ManipulationType = 0
						n\Angle = EntityYaw(n\Collider)
						;[End Block]
					Case 10.0 ; ~ Just walking
						;[Block]
						AnimateNPC(n, 1.0, 38.0, n\CurrSpeed * 40.0)
						
						n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
						
						MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
						;[End Block]
					Case 11.0 ; ~ Trying to find the player and kill
						;[Block]
						If n\Frame < 39.0 Lor (n\Frame > 76.0 And n\Frame < 245.0) Lor (n\Frame > 248.0 And n\Frame < 302.0) Lor n\Frame > 344.0
							AnimateNPC(n, 345.0, 357.0, 0.2, False)
							If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						EndIf
						
						If (Not me\Terminated)
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							
							Local SearchPlayer%
							
							SearchPlayer = ((Not (chs\NoTarget Lor I_268\InvisibilityOn)) And Dist < 121.0 And EntityVisible(n\Collider, me\Collider))
							If SearchPlayer
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
								PointEntity(Pvt, me\Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
								
								RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
								
								PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
								PointEntity(Pvt, me\Collider)
								RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
								
								If n\Reload = 0.0
									If n\State3 = 1.0
										InstaKillPlayer = False
										
										msg\DeathMsg = ""
										
										PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 15.0)
										
										RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
										PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
										MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
										PointEntity(Pvt, me\Collider)
										
										SqrValue = Sqr(Dist)
										
										Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ((25.0 / SqrValue) * (1.0 / SqrValue)), True, InstaKillPlayer)
										n\Reload = 8.0
									Else
										n\CurrSpeed = n\Speed
									EndIf
								EndIf
								If n\Reload > 0.0 And n\Reload <= 8.0
									AnimateNPC(n, 245.0, 248.0, 0.35)
								Else
									If n\Frame < 302.0 Then AnimateNPC(n, 302.0, 344.0, 0.35)
								EndIf
								FreeEntity(Pvt) : Pvt = 0
							Else
								If n\PathStatus = PATH_STATUS_FOUND
									If n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40.0)
										
										n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										
										PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
										
										RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
										
										If EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ) < PathLocationDist Then n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									If n\PathTimer = 0.0 Then n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider) + 0.5, EntityZ(me\Collider))
									
									wayPointCloseToPlayer = Null
									
									For wp.WayPoints = Each WayPoints
										If EntityDistanceSquared(wp\OBJ, me\Collider) < 4.0
											wayPointCloseToPlayer = wp
											Exit
										EndIf
									Next
									
									If wayPointCloseToPlayer <> Null
										n\PathTimer = 1.0
										If EntityVisible(wayPointCloseToPlayer\OBJ, n\Collider)
											If Abs(DeltaYaw(n\Collider, wayPointCloseToPlayer\OBJ)) > 0.0
												PointEntity(n\OBJ, wayPointCloseToPlayer\OBJ)
												RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
											EndIf
										EndIf
									Else
										n\PathTimer = 0.0
									EndIf
									
									If n\PathTimer = 1.0
										AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40.0)
										
										n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
									EndIf
								EndIf
								
								If (PrevFrame < 43.0 And n\Frame >= 43.0) Lor (PrevFrame < 61.0 And n\Frame >= 61.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
							EndIf
						Else
							n\State = 0.0
						EndIf
						;[End Block]
					Case 12.0
						;[Block]
						If n\Frame < 39.0 Lor (n\Frame > 76.0 And n\Frame < 245.0) Lor (n\Frame > 248.0 And n\Frame < 302.0) Lor n\Frame > 344.0
							AnimateNPC(n, 345.0, 357.0, 0.2, False)
							If n\Frame >= 356.0 Then SetNPCFrame(n, 302.0)
						EndIf
						If n\Frame < 345.0 Then AnimateNPC(n, 302.0, 344.0, 0.35)
						
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(Pvt, me\Collider)
						Else
							RotateEntity(Pvt, 0.0, n\Angle, 0.0)
						EndIf
						RotateEntity(Pvt, Min(EntityPitch(Pvt), 20.0), EntityYaw(Pvt), 0.0)
						
						RotateEntity(n\Collider, CurveAngle(EntityPitch(Pvt), EntityPitch(n\Collider), 10.0), CurveAngle(EntityYaw(Pvt), EntityYaw(n\Collider), 10.0), 0.0, True)
						
						PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider) + 0.8, EntityZ(n\Collider))
						If n\State2 = 1.0
							PointEntity(Pvt, me\Collider)
							n\ManipulateBone = True
							n\BoneToManipulate = "Chest"
							n\ManipulationType = 0
						Else
							RotateEntity(Pvt, 0.0, n\Angle, 0.0)
						EndIf
						RotateEntity(Pvt, Min(EntityPitch(Pvt), 40.0), EntityYaw(n\Collider), 0.0)
						
						FreeEntity(Pvt) : Pvt = 0
						;[End Block]
					Case 13.0
						;[Block]
						AnimateNPC(n, 202.0, 244.0, 0.35)
						;[End Block]
					Case 14.0
						;[Block]
						If n\PathStatus = PATH_STATUS_NOT_FOUND
							n\State = 13.0
							n\CurrSpeed = 0.0
						ElseIf n\PathStatus = PATH_STATUS_FOUND
							If n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\OBJ, n\Path[n\PathLocation]\OBJ)
								
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
								
								AnimateNPC(n, 39.0, 76.0, n\CurrSpeed * 40.0)
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
								
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
								
								If EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ) < PathLocationDist Then n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							n\CurrSpeed = 0.0
							n\State = 13.0
						EndIf
						
						If (PrevFrame < 43.0 And n\Frame >= 43.0) Lor (PrevFrame < 61.0 And n\Frame >= 61.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
						;[End Block]
					Case 15.0 ; ~ Inside vehicle (idle)
						;[Block]
						If n\OBJ2 <> 0
							If EntityHidden(n\OBJ2) Then ShowEntity(n\OBJ2)
							
							AnimateNPC(n, 623.0, 642.0, 0.3)
							
							If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2) : n\SoundCHN2 = 0
							n\SoundCHN = LoopSound2(snd_I\VehicleSFX[0], n\SoundCHN, Camera, n\OBJ2, 10.0, 1.0)
							
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						Else
							OpenConsoleOnError(Format(GetLocalString("runerr", "guard.state"), "15.0"))
						EndIf
						;[End Block]
					Case 16.0 ; ~ Inside vehicle (driving)
						;[Block]
						If n\OBJ2 <> 0
							If EntityHidden(n\OBJ2) Then ShowEntity(n\OBJ2)
							
							AnimateNPC(n, 623.0, 642.0, 0.3)
							
							If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN) : n\SoundCHN = 0
							n\SoundCHN2 = LoopSound2(snd_I\VehicleSFX[1], n\SoundCHN2, Camera, n\OBJ2, 12.0, 1.0)
							
							n\CurrSpeed = CurveValue(n\Speed * 0.9, n\CurrSpeed, 20.0)
							Animate2(n\OBJ2, AnimTime(n\OBJ2), 1.0, 20.0, n\CurrSpeed * 5.0)
							
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
						Else
							OpenConsoleOnError(Format(GetLocalString("runerr", "guard.state"), "16.0"))
						EndIf
						;[End Block]
					Default
						;[Block]
						If Rand(400) = 1 Then n\PrevState = Rand(-30, 30)
						n\PathStatus = PATH_STATUS_NO_SEARCH
						AnimateNPC(n, 77.0, 201.0, 0.2)
						
						RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + n\PrevState + Sin(MilliSec / 50) * 2.0, EntityYaw(n\Collider), 50.0), 0.0, True)
						;[End Block]
				End Select
				
				If n\CurrSpeed > 0.01
					If (PrevFrame < 5.0 And n\Frame >= 5.0) Lor (PrevFrame < 23.0 And n\Frame >= 23.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
				ElseIf n\CurrSpeed < -0.01
					If (PrevFrame >= 23.0 And n\Frame < 23.0) Lor (PrevFrame >= 5.0 And n\Frame < 5.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
				EndIf
				
				n\Reload = Max(0.0, n\Reload - fps\Factor[0])
				
				If n\OBJ2 <> 0
					PositionEntity(n\OBJ2, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
					RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider), 0.0)
					
					PositionEntity(n\OBJ, EntityX(n\OBJ2) + 1.75, EntityY(n\OBJ2) + 0.33, EntityZ(n\OBJ2) + 0.42)
					RotateEntity(n\OBJ, 0.0, EntityYaw(n\OBJ2) + 180.0, 0.0)
				Else
					PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
					RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) + 180.0, 0.0)
				EndIf
				;[End Block]
			Case NPCTypeMTF
				;[Block]
				UpdateMTFUnit(n)
				;[End Block]
			Case NPCTypeD, NPCTypeClerk
				;[Block]
				If (Not n\IsDead)
					RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
					
					PrevFrame = AnimTime(n\OBJ)
					Select n\State
						Case -1.0 ; ~ Nothing
							;[Block]
							;[End Block]
						Case 0.0 ; ~ Idles
							;[Block]
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
							Animate2(n\OBJ, AnimTime(n\OBJ), 210.0, 235.0, 0.1)
							;[End Block]
						Case 1.0 ; ~ Walking
							;[Block]
							If n\State2 = 1.0
								n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
							Else
								n\CurrSpeed = CurveValue(0.015, n\CurrSpeed, 5.0)
							EndIf
							Animate2(n\OBJ, AnimTime(n\OBJ), 236.0, 260.0, n\CurrSpeed * 18.0)
							;[End Block]
						Case 2.0 ; ~ Running
							;[Block]
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 5.0)
							Animate2(n\OBJ, AnimTime(n\OBJ), 301.0, 319.0, n\CurrSpeed * 18.0)
							;[End Block]
					End Select
					
					If n\State2 <> 2.0
						If n\State = 1.0
							If n\CurrSpeed > 0.005
								If (PrevFrame < 244.0 And AnimTime(n\OBJ) >= 244.0) Lor (PrevFrame < 256.0 And AnimTime(n\OBJ) >= 256.0) Then PlaySound2(StepSFX(GetStepSound(n\Collider), 0, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
						ElseIf n\State = 2.0
							If n\CurrSpeed > 0.005
								If (PrevFrame < 309.0 And AnimTime(n\OBJ) >= 309.0) Lor (PrevFrame <= 319.0 And AnimTime(n\OBJ) <= 301.0) Then PlaySound2(StepSFX(GetStepSound(n\Collider), 1, Rand(0, 2)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
						EndIf
					EndIf
					MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
				Else
					Select n\State3
						Case -1.0 ; ~ Don't animate
							;[Block]
							;[End Block]
						Case 0.0 ; ~ Fall backward
							;[Block]
							AnimateNPC(n, 1.0, 40.0, 0.5, False)
							;[End Block]
						Case 1.0 ; ~ Fall forward
							;[Block]
							AnimateNPC(n, 41.0, 60.0, 0.5, False)
							;[End Block]
						Case 2.0 ; ~ Snap #1
							;[Block]
							AnimateNPC(n, 555.0, 629.0, 0.5, False)
							;[End Block]
						Case 3.0 ; ~ Snap #2
							;[Block]
							AnimateNPC(n, 630.0, 677.0, 0.5, False)
							;[End Block]
						Case 4.0 ; ~ Snap #3
							;[Block]
							AnimateNPC(n, 678.0, 711.0, 0.5, False)
							;[End Block]
						Case 5.0 ; ~ Snap #4
							;[Block]
							AnimateNPC(n, 712.0, 779.0, 0.5, False)
							;[End Block]
					End Select
				EndIf
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				RotateEntity(n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider) - 180.0, 0.0)
				;[End Block]
			Case NPCType1048
				;[Block]
				n\Speed = 0.3
				Visible = (EntityDistanceSquared(me\Collider, n\Collider) < 4.0 And EntityInView(n\OBJ, Camera))
				
				If Visible Then GiveAchievement(Achv1048)
				
				Select n\State
					Case 0.0 ; ~ Idle
						;[Block]
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						
						AnimateNPC(n, 2.0, 151.0, n\Speed)
						;[End Block]
					Case 1.0 ; ~ Walks
						;[Block]
						If n\Frame < 262.0
							AnimateNPC(n, 249.0, 262.0, n\Speed, False)
						Else
							n\Speed = 0.008
							
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							
							AnimateNPC(n, 263.0, 290.0, n\CurrSpeed * 37.5)
						EndIf
						;[End Block]
					Case 2.0 ; ~ Happy :]
						;[End Block]
						PlayerSeeAble = (Not (chs\NoTarget Lor I_268\InvisibilityOn))
						If Visible And PlayerSeeAble
							PointEntity(n\Collider, me\Collider)
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
						
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						If n\State2 = 0.0
							If Visible And PlayerSeeAble Then n\State2 = 1.0
						ElseIf n\State2 = 1.0
							AnimateNPC(n, 305.0, 325.0, n\Speed, False)
							If n\Frame > 324.9 Then n\State2 = 2.0
						ElseIf n\State2 = 2.0
							AnimateNPC(n, 325.0, 305.0, (-n\Speed) / 1.5, False)
							If n\Frame < 305.1 And (Not EntityInView(n\OBJ, Camera)) Then n\State2 = 3.0
						EndIf
						;[End Block]
					Case 3.0 ; ~ Gives a paper
						;[Block]
						If Visible And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
							PointEntity(n\Collider, me\Collider)
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
						
						n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
						If n\Frame < 474.0
							AnimateNPC(n, 326.0, 474.0, n\Speed, False)
						Else
							AnimateNPC(n, 475.0, 623.0, n\Speed)
						EndIf
						;[End Block]
				End Select
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.08, EntityZ(n\Collider))
				RotateEntity(n\OBJ, -90.0, n\Angle, 0.0)
				;[End Block]
			Case NPCType1048_A
				;[Block]
				If (Not n\IsDead)
					n\Speed = 1.0
					Dist = EntityDistanceSquared(n\Collider, me\Collider)
					Visible = (Dist < 6.0 And EntityVisible(n\Collider, me\Collider))
					
					If Visible Then GiveAchievement(Achv1048)
					
					Select n\State
						Case 0.0 ; ~ Idle
							;[Block]
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
							
							AnimateNPC(n, 2.0, 399.0, n\Speed)
							
							If Rand(300) = 1
								If (Not ChannelPlaying(n\SoundCHN)) Then n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\1048A\Random" + Rand(0, 4) + ".ogg"), Camera, n\Collider, 8.0, 1.0, True)
							EndIf
							If Visible And (Not (chs\NoTarget Lor I_268\InvisibilityOn)) Then n\State = 2.0
							;[End Block]
						Case 1.0 ; ~ Walks
							;[Block]
							n\Speed = 0.008
							
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							
							AnimateNPC(n, 649.0, 677.0, n\CurrSpeed * 125.0)
							;[End Block]
						Case 2.0 ; ~ Scream
							;[Block]
							PrevFrame = n\Frame
							
							AnimateNPC(n, Max(PrevFrame, 2.0), 647.0, 1.0, False)
							
							If PrevFrame <= 400.0 And n\Frame > 400.0 Then n\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\1048A\Shriek.ogg"))
							
							Local Volume# = Max(1.0 - Abs(PrevFrame - 600.0) / 100.0, 0.0)
							
							me\BlurTimer = Volume * 1000.0 / Max(Dist / 8.0, 1.0)
							me\CameraShake = Volume * 10.0 / Max(Dist / 4.0, 1.0)
							
							If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
								PointEntity(n\Collider, me\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
							EndIf
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							
							If PrevFrame > 646.0
								If Dist < 16.0 And I_1048A\EarGrowTimer = 0.0
									I_1048A\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\1048A\Growth.ogg"), True)
									me\BlurTimer = 1000.0
									me\CameraShake = 2.0
									I_1048A\EarGrowTimer = 0.01
								EndIf
								n\State = 0.0
							EndIf
							;[End Block]
					End Select
					UpdateSoundOrigin(n\SoundCHN, Camera, n\Collider, 8.0, 1.0, True)
					
					PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.15, EntityZ(n\Collider))
					RotateEntity(n\OBJ, -90.0, n\Angle, 0.0)
				EndIf
				;[End Block]
			Case NPCType513_1
				;[Block]
				If PlayerRoom\RoomTemplate\RoomID <> r_dimension_106
					If n\Idle = 1
						If (Not EntityHidden(n\OBJ))
							HideEntity(n\OBJ)
							HideEntity(n\OBJ2)
						EndIf
						If Rand(200) = 1
							For w.WayPoints = Each WayPoints
								If w\room <> PlayerRoom
									x = Abs(EntityX(me\Collider) - EntityX(w\OBJ, True))
									If x > 3.0 And x < 9.0
										z = Abs(EntityZ(me\Collider) - EntityZ(w\OBJ, True))
										If z > 3.0 And z < 9.0
											PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 20.0 * RoomScale, EntityZ(w\OBJ, True))
											ResetEntity(n\Collider)
											
											n\LastSeen = 0
											
											n\Path[0] = w
											
											n\Idle = 0
											n\State2 = 70.0 * Rnd(15.0, 20.0)
											n\State = Max(Rand(-1.0, 2.0), 0.0)
											n\PrevState = Rand(0, 1)
											Exit
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					EndIf
					
					If n\Idle = 0
						If EntityHidden(n\OBJ)
							ShowEntity(n\OBJ)
							ShowEntity(n\OBJ2)
						EndIf
						
						Dist = EntityDistanceSquared(me\Collider, n\Collider)
						
						; ~ Use the prev-values to do a "twitching" effect
						n\PrevX = CurveValue(0.0, n\PrevX, 10.0)
						n\PrevZ = CurveValue(0.0, n\PrevZ, 10.0)
						
						If Rand(100) = 1
							If Rand(5) = 1
								n\PrevX = (EntityX(me\Collider) - EntityX(n\Collider)) * 0.9
								n\PrevZ = (EntityZ(me\Collider) - EntityZ(n\Collider)) * 0.9
							Else
								n\PrevX = Rnd(0.1, 0.5)
								n\PrevZ = Rnd(0.1, 0.5)
							EndIf
						EndIf
						
						Temp = Rnd(-1.0, 1.0)
						PositionEntity(n\OBJ2, EntityX(n\Collider) + n\PrevX * Temp, EntityY(n\Collider) - 0.2 + Sin((MilliSec / 8 - 45) Mod 360) * 0.05, EntityZ(n\Collider) + n\PrevZ * Temp)
						RotateEntity(n\OBJ2, 0.0, EntityYaw(n\OBJ), 0.0)
						If Floor(AnimTime(n\OBJ2)) <> Floor(n\Frame) Then SetAnimTime(n\OBJ2, n\Frame)
						
						If n\State = 0.0
							If n\PrevState = 0
								AnimateNPC(n, 2.0, 74.0, 0.2)
							Else
								AnimateNPC(n, 75.0, 124.0, 0.2)
							EndIf
							
							If n\LastSeen
								PointEntity(n\OBJ2, me\Collider)
								RotateEntity(n\OBJ, 0.0, CurveAngle(EntityYaw(n\OBJ2), EntityYaw(n\OBJ), 40.0), 0.0)
								If Dist < 16.0 Then n\State = Rand(1.0, 2.0)
							Else
								If Dist < 36.0 And Rand(5) = 1
									If EntityInView(n\Collider, Camera) And EntityVisible(me\Collider, n\Collider)
										n\LastSeen = 1
										PlaySound_Strict(LoadTempSound("SFX\SCP\513_1\Bell" + Rand(0, 2) + ".ogg"))
									EndIf
								EndIf
							EndIf
						Else
							If n\Path[0] = Null
								; ~ Move towards a waypoint that is:
								; ~ 1: Max 8 units away from SCP-513-1
								; ~ 2: Further away from the player than SCP-513-1's current position 
								For w.WayPoints = Each WayPoints
									x = Abs(EntityX(n\Collider, True) - EntityX(w\OBJ, True))
									If x < 8.0 And x > 1.0
										z = Abs(EntityZ(n\Collider, True) - EntityZ(w\OBJ, True))
										If z < 8.0 And z > 1.0
											If EntityDistanceSquared(me\Collider, w\OBJ) > Dist
												n\Path[0] = w
												Exit
											EndIf
										EndIf
									EndIf
								Next
								
								; ~ SCP-513-1 simply disappears
								If n\Path[0] = Null
									n\Idle = 1
									n\State2 = 0.0
								EndIf
							Else
								If EntityDistanceSquared(n\Collider, n\Path[0]\OBJ) > 1.0
									PointEntity(n\OBJ, n\Path[0]\OBJ)
									RotateEntity(n\Collider, CurveAngle(EntityPitch(n\OBJ), EntityPitch(n\Collider), 15.0), CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 15.0), 0.0, True)
									n\CurrSpeed = CurveValue(0.05 * Max((7.0 - Sqr(Dist)) / 7.0, 0.0), n\CurrSpeed, 15.0)
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
									If Rand(200) = 1 Then MoveEntity(n\Collider, 0.0, 0.0, 0.5)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
								Else
									For i = 0 To 4
										If n\Path[0]\connected[i] <> Null
											If EntityDistanceSquared(me\Collider, n\Path[0]\connected[i]\OBJ) > Dist
												If n\LastSeen = 0
													If EntityInView(n\Collider, Camera) And EntityVisible(me\Collider, n\Collider)
														n\LastSeen = 1
														PlaySound_Strict(LoadTempSound("SFX\SCP\513_1\Bell" + Rand(0, 2) + ".ogg"))
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
						PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2 + Sin((MilliSec / 8) Mod 360) * 0.1, EntityZ(n\Collider))
						
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
						
						If n\State2 > 0.0
							If Dist < 16.0 Then n\State2 = n\State2 - fps\Factor[0] * 4.0
							n\State2 = n\State2 - fps\Factor[0]
						Else
							n\Path[0] = Null
							n\Idle = 1
							n\State2 = 0.0
						EndIf
					EndIf
				EndIf
				
				n\DropSpeed = 0.0
				ResetEntity(n\Collider)
				;[End Block]
			Case NPCType372
				;[Block]
				If PlayerInReachableRoom(True)
					If n\Idle = 1
						If (Not EntityHidden(n\OBJ)) Then HideEntity(n\OBJ)
						If Rand(50) = 1 And (me\BlinkTimer < -5.0 And me\BlinkTimer > -15.0)
							Angle = EntityYaw(me\Collider) + Rnd(-90.0, 90.0)
							
							Dist = Rnd(1.5, 2.0)
							PositionEntity(n\Collider, EntityX(me\Collider) + Sin(Angle) * Dist, EntityY(me\Collider) + 0.2, EntityZ(me\Collider) + Cos(Angle) * Dist)
							n\Idle = 0
							n\State = Rnd(20.0, 60.0)
							
							If Rand(300) = 1 Then PlaySound2(snd_I\RustleSFX[Rand(0, 5)], Camera, n\Collider, 8.0, Rnd(0.0, 0.2))
						EndIf
					EndIf
					
					If n\Idle = 0
						If EntityHidden(n\OBJ) Then ShowEntity(n\OBJ)
						PositionEntity(n\OBJ, EntityX(n\Collider) + Rnd(-0.005, 0.005), EntityY(n\Collider) + 0.3 + 0.1 * Sin(MilliSec / 2.0), EntityZ(n\Collider) + Rnd(-0.005, 0.005))
						RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider), ((MilliSec / 5.0) Mod 360.0))
						
						AnimateNPC(n, 1.0, 300.0, Rnd(0.8, 2.5))
						
						If EntityInView(n\OBJ, Camera) And (me\BlinkTimer < -16.0 Lor me\BlinkTimer > -6.0)
							GiveAchievement(Achv372)
							
							If Rand(30) = 1
								If EntityVisible(Camera, n\OBJ)
									If (Not ChannelPlaying(n\SoundCHN)) Then n\SoundCHN = PlaySound2(snd_I\RustleSFX[Rand(0, 5)], Camera, n\OBJ, 8.0, 0.3)
								EndIf
							EndIf
							
							Temp = CreatePivot()
							PositionEntity(Temp, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
							PointEntity(Temp, n\Collider)
							
							Angle = WrapAngle(EntityYaw(me\Collider) - EntityYaw(Temp))
							If Angle < 180.0
								RotateEntity(n\Collider, 0.0, EntityYaw(me\Collider) - 80.0, 0.0)
							Else
								RotateEntity(n\Collider, 0.0, EntityYaw(me\Collider) + 80.0, 0.0)
							EndIf
							FreeEntity(Temp) : Temp = 0
							
							MoveEntity(n\Collider, 0.0, 0.0, 0.03 * fps\Factor[0])
						EndIf
						n\State = n\State - (fps\Factor[0] * 0.8)
						If n\State <= 0.0
							n\Idle = 1
							PositionEntity(n\Collider, 0.0, -500.0, 0.0)
						EndIf
					EndIf
					n\DropSpeed = 0.0
					ResetEntity(n\Collider)
				EndIf
				;[End Block]
			Case NPCTypeApache
				;[Block]
				Dist = EntityDistanceSquared(me\Collider, n\Collider)
				If Dist < 3600.0
					If PlayerRoom\RoomTemplate\RoomID = r_gate_b
						Dist2 = Max(Min(EntityDistance(n\Collider, PlayerRoom\Objects[10]) / (8000.0 * RoomScale), 1.0), 0.0)
					Else
						Dist2 = 1.0
					EndIf
					n\SoundCHN = LoopSound2(snd_I\ApacheSFX, n\SoundCHN, Camera, n\Collider, 25.0, Dist2)
				EndIf
				
				n\DropSpeed = 0.0
				
				Select n\State
					Case 0.0, 1.0
						;[Block]
						TurnEntity(n\OBJ2, 0.0, 20.0 * fps\Factor[0], 0.0)
						TurnEntity(n\OBJ3, 20.0 * fps\Factor[0], 0.0, 0.0)
						
						If n\State = 1.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
							If Abs(EntityX(me\Collider) - EntityX(n\Collider)) < 30.0
								If Abs(EntityZ(me\Collider) - EntityZ(n\Collider)) < 30.0
									If Abs(EntityY(me\Collider) - EntityY(n\Collider)) < 20.0
										If Rand(20) = 1
											If EntityVisible(me\Collider, n\Collider)
												PlaySound2(snd_I\AlarmSFX[1], Camera, n\Collider, 50.0, 1.0)
												n\State = 2.0
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case 2.0, 3.0 ; ~ Attacks
						;[Block]
						If n\State = 2.0
							Target = me\Collider
						ElseIf n\State = 3.0
							Target = CreatePivot()
							PositionEntity(Target, n\EnemyX, n\EnemyY, n\EnemyZ, True)
						EndIf
						
						TurnEntity(n\OBJ2, 0.0, 20.0 * fps\Factor[0], 0.0)
						TurnEntity(n\OBJ3, 20.0 * fps\Factor[0], 0.0, 0.0)
						
						If Abs(EntityX(Target) - EntityX(n\Collider)) < 55.0
							If Abs(EntityZ(Target) - EntityZ(n\Collider)) < 55.0
								If Abs(EntityY(Target) - EntityY(n\Collider)) < 20.0
									PointEntity(n\OBJ, Target)
									RotateEntity(n\Collider, CurveAngle(Min(WrapAngle(EntityPitch(n\OBJ)), 40.0), EntityPitch(n\Collider), 40.0), CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 90.0), EntityRoll(n\Collider), True)
									PositionEntity(n\Collider, EntityX(n\Collider), CurveValue(EntityY(Target) + 8.0, EntityY(n\Collider), 70.0), EntityZ(n\Collider))
									
									Dist = DistanceSquared(EntityX(Target), EntityX(n\Collider), EntityZ(Target), EntityZ(n\Collider))
									
									n\CurrSpeed = CurveValue(Min(Sqr(Dist) - 6.5, 6.5) * 0.008, n\CurrSpeed, 50.0)
									
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
									
									Visible = False
									If n\PathTimer = 0.0
										Visible = EntityVisible(n\Collider, Target)
										n\PathTimer = Rnd(100.0, 200.0)
									Else
										n\PathTimer = Min(n\PathTimer - fps\Factor[0], 0.0)
									EndIf
									If chs\NoTarget Lor I_268\InvisibilityOn And n\State = 2.0 Then Visible = False
									
									If Visible ; ~ Player visible
										RotateEntity(n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(0.0, EntityRoll(n\Collider), 40.0), True)
										If n\Reload <= 0.0
											If Dist < 400.0
												Pvt = CreatePivot()
												PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
												RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), EntityRoll(n\Collider))
												MoveEntity(Pvt, 0.0, 0.023, 0.188)
												PointEntity(Pvt, Target)
												
												If WrapAngle(EntityYaw(Pvt) - EntityYaw(n\Collider)) < 10.0
													PlaySound2(snd_I\GunshotSFX[1], Camera, n\Collider, 20.0)
													
													SqrValue = Sqr(Dist)
													
													Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ((10.0 / SqrValue) * (1.0 / SqrValue)) * (n\State = 2.0), (n\State = 2.0))
													
													If me\Terminated And n\State <> 3
														If PlayerRoom\RoomTemplate\RoomID = r_gate_b
															msg\DeathMsg = GetLocalString("death", "apache.gateb")
														Else
															msg\DeathMsg = GetLocalString("death", "apache.gatea")
														EndIf
													EndIf
												EndIf
												FreeEntity(Pvt) : Pvt = 0
											EndIf
											n\Reload = 5.0
										EndIf
									Else
										RotateEntity(n\Collider, EntityPitch(n\Collider), EntityYaw(n\Collider), CurveAngle(-20.0, EntityRoll(n\Collider), 40.0), True)
									EndIf
									MoveEntity(n\Collider, (-EntityRoll(n\Collider)) * 0.002, 0.0, 0.0)
									
									n\Reload = n\Reload - fps\Factor[0]
								EndIf
							EndIf
						EndIf
						If n\State = 3 Then FreeEntity(Target) : Target = 0
						;[End Block]
					Case 4.0 ; ~ Crashes
						;[Block]
						If n\State2 < 300.0
							TurnEntity(n\OBJ2, 0.0, 20.0 * fps\Factor[0], 0.0)
							TurnEntity(n\OBJ3, 20.0 * fps\Factor[0], 0.0, 0.0)
							
							TurnEntity(n\Collider, 0.0, (-fps\Factor[0]) * 7.0, 0.0)
							n\State2 = n\State2 + fps\Factor[0] * 0.3
							
							Target = CreatePivot()
							PositionEntity(Target, n\EnemyX, n\EnemyY, n\EnemyZ, True)
							
							PointEntity(n\OBJ, Target)
							MoveEntity(n\OBJ, 0.0, 0.0, fps\Factor[0] * 0.001 * n\State2)
							PositionEntity(n\Collider, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							
							If EntityDistanceSquared(n\OBJ, Target) < 0.09
								me\CameraShake = Max(me\CameraShake, 3.0)
								PlaySound_Strict(LoadTempSound("SFX\Character\Apache\Crash" + Rand(0, 1) + ".ogg"))
								n\State = 5.0
							EndIf
							FreeEntity(Target) : Target = 0
						EndIf
						;[End Block]
				End Select
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity(n\OBJ, EntityPitch(n\Collider), EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				;[End Block]
			Case NPCType035_Tentacle
				;[Block]
				If (Not n\IsDead)
					Dist = EntityDistanceSquared(n\Collider, me\Collider)
					If Dist < PowTwo(HideDistance)
						PrevFrame = n\Frame
						Select n\State 
							Case 0.0 ; ~ Spawns
								;[Block]
								If n\Frame = 282.0
									PlaySound2(LoadTempSound("SFX\SCP\035_Tentacle\TentacleSpawn.ogg"), Camera, n\Collider, 5.0)
									SetNPCFrame(n, 283.0)
								Else
									me\HeartBeatVolume = Max(CurveValue(1.0, me\HeartBeatVolume, 50.0), me\HeartBeatVolume)
									me\HeartBeatRate = Max(CurveValue(130.0, me\HeartBeatRate, 100.0), me\HeartBeatRate)
									
									PointEntity(n\OBJ, me\Collider)
									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 25.0), 0.0)
									
									AnimateNPC(n, 283.0, 389.0, 0.3, False)
									
									If n\Frame > 388.9 Then n\State = 1.0
								EndIf
								;[End Block]
							Case 1.0 ; ~ Idles
								;[Block]
								AnimateNPC(n, 33.0, 174.0, 0.3)
								
								If Dist < 3.24 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
									n\State = 2.0
								Else
									; ~ Randomly rotates
									If Rand(400) = 1 Then n\Angle = Rnd(360.0)
									
									RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle + Sin(MilliSec / 50) * 2.0, EntityYaw(n\Collider), 150.0), 0.0, True)
								EndIf
								;[End Block]
							Case 2.0 ; ~ Attacks
								;[Block]
								; ~ Finish the idle animation before playing the attack animation
								If n\Frame > 32.0 And n\Frame < 174.0
									AnimateNPC(n, 33.0, 174.0, 2.0, False)
									If n\Frame > 173.9 Then SetNPCFrame(n, 2.0)
								Else
									PointEntity(n\OBJ, me\Collider)
									RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 10.0), 0.0)
									
									AnimateNPC(n, 2.0, 32.0, 0.3, False)
									
									If n\Frame > 5.0 And PrevFrame <= 5.0
										If Dist < 3.24
											If Abs(DeltaYaw(n\Collider, me\Collider)) < 20.0
												If wi\HazmatSuit > 0
													PlaySound_Strict(LoadTempSound("SFX\Character\BodyFall.ogg"))
													InjurePlayer(Rnd(0.5))
												Else
													PlaySound_Strict(snd_I\DamageSFX[Rand(9, 10)])
													InjurePlayer(Rnd(0.75, 1.15) * DifficultyDMGMult, 0.0, 100.0, Rnd(0.35, 0.4) * DifficultyDMGMult, 0.2)
													
													If me\Injuries > 3.0
														If PlayerRoom\RoomTemplate\RoomID = r_room2_ez
															msg\DeathMsg = GetLocalString("death", "035.offices")
														Else
															msg\DeathMsg = GetLocalString("death", "035.default")
														EndIf
														Kill(True)
													EndIf
												EndIf
												me\CameraShake = 2.0
											Else
												PlaySound2(snd_I\MissSFX, Camera, n\Collider)
											EndIf
										Else
											PlaySound2(snd_I\MissSFX, Camera, n\Collider, 3.0)
										EndIf
									ElseIf n\Frame > 31.9
										SetNPCFrame(n, 173.0)
										n\State = 1.0
									EndIf
								EndIf
								;[End Block]
						End Select
						If n\State <> 0.0 Then n\SoundCHN = LoopSound2(NPCSound[SOUND_NPC_035_TENTACLE_IDLE], n\SoundCHN, Camera, n\Collider)
					EndIf
				Else
					AnimateNPC(n, 515.0, 551.0, 0.15, False)
				EndIf
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
				RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider) - 180.0, EntityRoll(n\Collider), True)
				;[End Block]
			Case NPCType860_2
				;[Block]
				If forest_event <> Null
					If PlayerRoom = forest_event\room
						If forest_event\EventState = 1.0
							Local fr.Forest = PlayerRoom\fr
							
							Dist = EntityDistanceSquared(me\Collider, n\Collider)
							If ForestNPC <> 0
								If ForestNPCData[2] = 1.0
									If EntityHidden(ForestNPC) Then ShowEntity(ForestNPC)
									If ForestNPCData[1] = 0.0
										If Rand(200) = 1
											ForestNPCData[1] = fps\Factor[0]
											EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0] + 1.0)
										EndIf
									ElseIf ForestNPCData[1] > 0.0 And ForestNPCData[1] < 5.0
										ForestNPCData[1] = Min(ForestNPCData[1] + fps\Factor[0], 5.0)
									Else
										ForestNPCData[1] = 0.0
										EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
									EndIf
									If n\State <> 1.0
										If (me\BlinkTimer < -8.0 And me\BlinkTimer > -12.0) Lor (Not EntityInView(ForestNPC, Camera))
											ForestNPCData[2] = 0.0
											If (Not EntityHidden(ForestNPC)) Then HideEntity(ForestNPC)
										EndIf
									EndIf
								Else
									If (Not EntityHidden(ForestNPC)) Then HideEntity(ForestNPC)
								EndIf
							EndIf
							
							Select n\State
								Case 0.0 ; ~ Idles (hidden)
									;[Block]
									If (Not EntityHidden(n\Collider))
										HideEntity(n\Collider)
										HideEntity(n\OBJ)
										HideEntity(n\OBJ2)
										PositionEntity(n\Collider, 0.0, -100.0, 0.0)
										n\State2 = 0.0
									EndIf
									;[End Block]
								Case 1.0 ; ~ Appears briefly behind the trees
									;[Block]
									n\DropSpeed = 0.0
									
									If EntityY(n\Collider) <= -100.0
										; ~ Transform the position of the player to the local coordinates of the forest
										TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, fr\Forest_Pivot)
										
										; ~ Calculate the indices of the forest cell the player is in
										x = Floor((TFormedX() + 6.0) / 12.0)
										z = Floor((TFormedZ() + 6.0) / 12.0)
										
										; ~ Step through nearby cells
										For x2 = Max(x - 1, 1.0) To Min(x + 1, ForestGridSize) Step 2
											For z2 = Max(z - 1, 1.0) To Min(z + 1, ForestGridSize) Step 2
												; ~ Choose an empty cell (not on the path)
												If fr\Grid[(z2 * ForestGridSize) + x2] = 0
													; ~ Spawn the monster between the empty cell and the cell the player is in
													TFormPoint(((x2 + x) / 2.0) * 12.0, 0.0, ((z2 + z) / 2.0) * 12.0, fr\Forest_Pivot, 0)
													
													; ~ Keep searching for a more suitable cell
													PositionEntity(n\Collider, TFormedX(), EntityY(fr\Forest_Pivot, True) + 2.3, TFormedZ())
													If EntityInView(n\Collider, Camera)
														PositionEntity(n\Collider, 0.0, -110.0, 0.0)
													Else
														x2 = ForestGridSize
														Exit
													EndIf
												EndIf
											Next
										Next
										
										If EntityY(n\Collider) > -100.0
											PlaySound2(StepSFX(4, 0, Rand(0, 2)), Camera, n\Collider, 15.0, 0.5)
											
											If ForestNPCData[2] <> 1.0 Then ForestNPCData[2] = 0.0
											
											Select Rand(3)
												Case 1
													;[Block]
													PointEntity(n\Collider, me\Collider)
													SetNPCFrame(n, 2.0)
													;[End Block]
												Case 2
													;[Block]
													PointEntity(n\Collider, me\Collider)
													SetNPCFrame(n, 201.0)
													;[End Block]
												Case 3
													;[Block]
													PointEntity(n\Collider, me\Collider)
													TurnEntity(n\Collider, 0.0, 90.0, 0.0)
													SetNPCFrame(n, 299.0)
													;[End Block]
											End Select
											n\State2 = 0.0
										EndIf
									Else
										If EntityHidden(n\OBJ)
											ShowEntity(n\OBJ)
											ShowEntity(n\Collider)
										EndIf
										
										PositionEntity(n\Collider, EntityX(n\Collider), EntityY(fr\Forest_Pivot, True) + 2.3, EntityZ(n\Collider))
										
										If ForestNPC <> 0
											If ForestNPCData[2] = 0.0
												Local DocChance% = 0
												Local DocAmount% = 0
												
												For i = 0 To MaxItemAmount - 1
													If Inventory(i) <> Null
														Local DocName$ = Inventory(i)\ItemTemplate\Name
														
														If DocName = "Log #1" Lor DocName = "Log #2" Lor DocName = "Log #3"
															DocAmount = DocAmount + 1
															DocChance = DocChance + 10 * DocAmount
														EndIf
													EndIf
												Next
												
												If Rand(860 - DocChance) = 1
													If EntityHidden(ForestNPC) Then ShowEntity(ForestNPC)
													ForestNPCData[2] = 1.0
													If Rand(2) = 1
														ForestNPCData[0] = 0.0
													Else
														ForestNPCData[0] = 2.0
													EndIf
													ForestNPCData[1] = 0.0
													PositionEntity(ForestNPC, EntityX(n\Collider), EntityY(n\Collider) + 0.5, EntityZ(n\Collider))
													RotateEntity(ForestNPC, 0.0, EntityYaw(n\Collider), 0.0)
													MoveEntity(ForestNPC, 0.75, 0.0, 0.0)
													RotateEntity(ForestNPC, 0.0, 0.0, 0.0)
													EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
												Else
													ForestNPCData[2] = 2.0
												EndIf
											EndIf
										EndIf
										
										If n\State2 = 0.0 ; ~ Don't start moving until the player is looking
											If EntityInView(n\Collider, Camera)
												If Rand(8) = 1 Then PlaySound2(LoadTempSound("SFX\SCP\860_2\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 20.0, 1.0, True)
												n\State2 = 1.0
											EndIf
										Else
											If n\Frame <= 199.0
												AnimateNPC(n, 2.0, 199.0, 0.5, False)
												If n\Frame = 199.0
													PlaySound2(StepSFX(4, 0, Rand(0, 2)), Camera, n\Collider, 15.0)
													SetNPCFrame(n, 298.0)
												EndIf
											ElseIf n\Frame <= 297.0
												PointEntity(n\Collider, me\Collider)
												
												AnimateNPC(n, 200.0, 297.0, 0.5, False)
												If n\Frame = 297.0
													PlaySound2(StepSFX(4, 0, Rand(0, 2)), Camera, n\Collider, 15.0)
													SetNPCFrame(n, 298.0)
												EndIf
											Else
												Angle = CurveAngle(PointDirection(EntityX(n\Collider), EntityZ(n\Collider), EntityX(me\Collider), EntityZ(me\Collider)), EntityYaw(n\Collider) + 90.0, 20.0)
												
												RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
												
												AnimateNPC(n, 298.0, 316.0, n\CurrSpeed * 10.0)
												
												n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
												MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
												
												If Dist > 225.0 Then n\State = 0.0
											EndIf
										EndIf
									EndIf
									
									ResetEntity(n\Collider)
									;[End Block]
								Case 2.0 ; ~ Appears on the path and starts to walk towards the player
									;[Block]
									If EntityHidden(n\OBJ)
										ShowEntity(n\OBJ)
										ShowEntity(n\Collider)
									EndIf
									
									PrevFrame = n\Frame
									
									If EntityY(n\Collider) <= -100.0
										; ~ Transform the position of the player to the local coordinates of the forest
										TFormPoint(EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 0, fr\Forest_Pivot)
										
										; ~ Calculate the indices of the forest cell the player is in
										x = Floor((TFormedX() + 6.0) / 12.0)
										z = Floor((TFormedZ() + 6.0) / 12.0)
										
										For x2 = Max(x - 1, 1) To Min(x + 1, ForestGridSize)
											For z2 = Max(z - 1, 1) To Min(z + 1, ForestGridSize)
												; ~ Find a nearby cell that's on the path and not the cell the player is in
												If fr\Grid[(z2 * ForestGridSize) + x2] > 0 And (x2 <> x Lor z2 <> z) And (x2 = x Lor z2 = z)
													; ~ Transform the position of the cell back to world coordinates
													TFormPoint(x2 * 12.0, 0.0, z2 * 12.0, fr\Forest_Pivot, 0)
													
													PositionEntity(n\Collider, TFormedX(), EntityY(fr\Forest_Pivot, True) + 1.0, TFormedZ())
													If EntityInView(n\Collider, Camera)
														me\BlinkTimer = -10.0
													Else
														x2 = ForestGridSize
														Exit
													EndIf
												EndIf
											Next
										Next
									Else
										Angle = CurveAngle(Find860Angle(n, fr), EntityYaw(n\Collider) + 90.0, 80.0)
										
										RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
										
										n\CurrSpeed = CurveValue(n\Speed * 0.3, n\CurrSpeed, 50.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										
										AnimateNPC(n, 494.0, 569.0, n\CurrSpeed * 25.0)
										
										If n\State2 = 0.0
											If Dist < 64.0
												If EntityInView(n\Collider, Camera)
													If Rand(8) = 1
														PlaySound_Strict(LoadTempSound("SFX\SCP\860_2\Chase" + Rand(0, 2) + ".ogg"))
														
														PlaySound2(LoadTempSound("SFX\SCP\860_2\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
													EndIf
													n\State2 = 1.0
												EndIf
											EndIf
										EndIf
										
										If me\CurrSpeed > 0.03 ; ~ The player is running
											n\State3 = n\State3 + fps\Factor[0]
											If Rnd(5000.0) < n\State3
												Temp = True
												If ChannelPlaying(n\SoundCHN) Then Temp = False
												If Temp Then n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\860_2\Cancer" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
											EndIf
										Else
											n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
										EndIf
										
										If Dist < 20.25 Lor n\State3 > Rnd(200.0, 250.0)
											If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
												n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\860_2\Cancer" + Rand(3, 5) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
												n\State = 3.0
											Else
												If (PrevFrame < 492.0 And n\Frame >= 492.0) Lor (PrevFrame < 568.0 And n\Frame >= 568.0)
													SetNPCFrame(n, 2.0)
													n\State = 4.0
												EndIf
											EndIf
										EndIf
										If Dist > 400.0 Then n\State = 0.0
									EndIf
									If (PrevFrame < 533.0 And n\Frame >= 533.0) Lor (PrevFrame < 568.0 And n\Frame >= 568.0) Then PlaySound2(StepSFX(4, 0, Rand(0, 2)), Camera, n\Collider, 15.0, 0.6)
									;[End Block]
								Case 3.0 ; ~ Runs towards the player and attacks
									;[Block]
									If EntityHidden(n\OBJ)
										ShowEntity(n\OBJ)
										ShowEntity(n\Collider)
									EndIf
									
									PrevFrame = n\Frame
									
									Angle = CurveAngle(Find860Angle(n, fr), EntityYaw(n\Collider) + 90.0, 40.0)
									
									RotateEntity(n\Collider, 0.0, Angle - 90.0, 0.0, True)
									
									; ~ If close enough to attack or already attacking, play the attack anim
									If (Dist < 1.0 Lor (n\Frame > 451.0 And n\Frame < 493.0) Lor me\Terminated)
										msg\DeathMsg = Format(GetLocalString("death", "860"), SubjectName)
										
										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
										
										AnimateNPC(n, 451.0, 493.0, 0.5, False)
										
										If (PrevFrame < 461.0 And n\Frame >= 461.0)
											PlaySound_Strict(snd_I\DamageSFX[11])
											me\CameraShake = 2.0
											Kill(True)
										EndIf
										If (PrevFrame < 476.0 And n\Frame >= 476.0) Lor (PrevFrame < 486.0 And n\Frame >= 486.0) Then PlaySound_Strict(snd_I\DamageSFX[12])
									Else
										n\CurrSpeed = CurveValue(n\Speed * 0.8, n\CurrSpeed, 10.0)
										
										AnimateNPC(n, 298.0, 316.0, n\CurrSpeed * 10.0)
										
										If (PrevFrame < 307.0 And n\Frame >= 307.0) Then PlaySound2(StepSFX(4, 0, Rand(0, 2)), Camera, n\Collider, 10.0)
									EndIf
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
									
									If chs\NoTarget Lor I_268\InvisibilityOn
										If (PrevFrame < 315.0 And n\Frame >= 315.0) Lor (PrevFrame < 492.0 And n\Frame >= 492.0)
											SetNPCFrame(n, 2.0)
											n\State = 4.0
										EndIf
									EndIf
									;[End Block]
								Case 4.0 ; ~ Idles (not hidden)
									;[Block]
									n\CurrSpeed = 0.0
									
									AnimateNPC(n, 2.0, 199.0, 0.5)
									
									If (Not (chs\NoTarget Lor I_268\InvisibilityOn)) Then n\State = 3.0
									;[End Block]
							End Select
							
							If n\State <> 0.0
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
								
								PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.23, EntityZ(n\Collider))
								RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
								
								If Dist > 64.0
									If EntityHidden(n\OBJ2) Then ShowEntity(n\OBJ2)
									EntityAlpha(n\OBJ2, Min(Sqr(Dist) - 8.0, 1.0))
									
									PositionEntity(n\OBJ2, EntityX(n\OBJ), EntityY(n\OBJ) , EntityZ(n\OBJ))
									RotateEntity(n\OBJ2, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
									MoveEntity(n\OBJ2, 0.0, 0.75, -0.825)
									
									; ~ Render distance is set to 8.5 inside the forest,
									; ~ So we need to cheat a bit to make the eyes visible if they're further than that
									Pvt = CreatePivot()
									PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
									PointEntity(Pvt, n\OBJ2)
									MoveEntity(Pvt, 0.0, 0.0, 8.0)
									PositionEntity(n\OBJ2, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt))
									FreeEntity(Pvt) : Pvt = 0
								Else
									If (Not EntityHidden(n\OBJ2)) Then HideEntity(n\OBJ2)
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case NPCType939
				;[Block]
				; ~ State is set to 66 in the room3_storage-event if player isn't inside the room
				If PlayerRoom\RoomTemplate\RoomID <> r_room3_storage Then n\State = 66.0
				If n\State < 66.0
					Select n\State
						Case 0.0 ; ~ Idles
							;[Block]
							AnimateNPC(n, 290.0, 405.0, 0.1)
							;[End Block]
						Case 1.0
							;[Block]
							If n\Frame >= 644.0 And n\Frame < 683.0
								n\CurrSpeed = CurveValue(n\Speed * 0.05, n\CurrSpeed, 10.0)
								AnimateNPC(n, 644.0, 683.0, 28.0 * n\CurrSpeed * 4.0, False)
								If n\Frame >= 682.0 Then SetNPCFrame(n, 175.0)
							Else
								n\CurrSpeed = CurveValue(0, n\CurrSpeed, 5.0)
								AnimateNPC(n, 175.0, 297.0, 0.22, False)
								If n\Frame >= 296.0 Then n\State = 2.0
							EndIf
							
							n\LastSeen = 0
							
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							;[End Block]
						Case 2.0 ; ~ Walking
							;[Block]
							n\State2 = Max(n\State2, n\PrevState - 3)
							
							Dist = EntityDistanceSquared(n\Collider, PlayerRoom\Objects[n\State2])
							
							n\CurrSpeed = CurveValue(n\Speed * 0.3 * Min(Sqr(Dist), 1.0), n\CurrSpeed, 10.0)
							MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
							
							PrevFrame = n\Frame
							AnimateNPC(n, 644.0, 683.0, 28.0 * n\CurrSpeed)
							
							If (PrevFrame < 664.0 And n\Frame >= 664.0) Lor (PrevFrame > 673.0 And n\Frame < 654.0)
								PlaySound2(snd_I\Step2SFX[Rand(3, 6)], Camera, n\Collider, 12.0)
								If Rand(10) = 1
									Temp = (n\SoundCHN = 0 Lor (Not ChannelPlaying(n\SoundCHN)))
									If Temp
										LoadNPCSound(n, "SFX\SCP\939\" + (n\ID Mod 3) + "Lure" + Rand(0, 9) + ".ogg")
										n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 10.0, 1.0, True)
									EndIf
								EndIf
							EndIf
							
							PointEntity(n\OBJ, PlayerRoom\Objects[n\State2])
							RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0 - (SelectedDifficulty\OtherFactors * 2.0)), 0.0)
							
							If Dist < 0.16
								n\State2 = n\State2 + 1.0
								If n\State2 > n\PrevState Then n\State2 = (n\PrevState - 3)
								n\State = 1.0
							EndIf
							;[End Block]
						Case 3.0 ; ~ Attack
							;[Block]
							If EntityVisible(me\Collider, n\Collider)
								n\EnemyX = EntityX(me\Collider)
								n\EnemyZ = EntityZ(me\Collider)
								n\LastSeen = 70.0
							EndIf
							
							If n\LastSeen > 0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
								PrevFrame = n\Frame
								
								If n\Frame >= 18.0 And n\Frame < 68.0
									n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
									AnimateNPC(n, 18.0, 68.0, 0.5, True)
									
									Temp = ((PrevFrame < 24.0 And n\Frame >= 24.0) Lor (PrevFrame < 57.0 And n\Frame >= 57.0))
									If Temp
										If DistanceSquared(n\EnemyX, EntityX(n\Collider), n\EnemyZ, EntityZ(n\Collider)) < 2.25
											PlaySound_Strict(snd_I\DamageSFX[11])
											InjurePlayer(Rnd(1.5, 2.5), 0.0, 500.0, Rnd(0.2, 0.75))
											me\CameraShake = 2.0
										Else
											SetNPCFrame(n, 449.0)
										EndIf
									EndIf
									
									If me\Injuries > 4.0
										If (Not chs\GodMode) Then n\State = 5.0
										msg\DeathMsg = GetLocalString("death", "939")
										Kill(True)
									EndIf
								Else
									If n\LastSeen = 70.0
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
										
										AnimateNPC(n, 449.0, 464.0, n\CurrSpeed * 6.0)
										
										If (PrevFrame < 452.0 And n\Frame >= 452.0) Lor (PrevFrame < 459.0 And n\Frame >= 459.0) Then PlaySound2(StepSFX(1, 1, Rand(0, 7)), Camera, n\Collider, 12.0)
										
										; ~ Player is visible
										If DistanceSquared(n\EnemyX, EntityX(n\Collider), n\EnemyZ, EntityZ(n\Collider)) < 1.0 Then SetNPCFrame(n, 18.0)
									Else
										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
										AnimateNPC(n, 175.0, 297.0, n\CurrSpeed * 5.0)
									EndIf
								EndIf
								
								Angle = VectorYaw(n\EnemyX - EntityX(n\Collider), 0.0, n\EnemyZ - EntityZ(n\Collider))
								RotateEntity(n\Collider, 0.0, CurveAngle(Angle, EntityYaw(n\Collider), 15.0 - (SelectedDifficulty\OtherFactors * 1.5)), 0.0)
								
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
								
								n\LastSeen = n\LastSeen - fps\Factor[0]
							Else
								n\State = 2.0
							EndIf
							;[End Block]
						Case 5.0 ; ~ Finishes attack and goes to idle
							;[Block]
							If n\Frame < 68.0
								AnimateNPC(n, 18.0, 68.0, 0.5, False)
							Else
								AnimateNPC(n, 464.0, 473.0, 0.5, False)
							EndIf
							;[End Block]
					End Select
					
					If n\State < 3.0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn)); And (Not n\IgnorePlayer)
						Dist = EntityDistanceSquared(n\Collider, me\Collider) - (EntityVisible(me\Collider, n\Collider) * 1.4641)
						If PowTwo(me\SndVolume * 1.2) > Dist Lor Dist < 2.25
							If n\State3 = 0.0
								LoadNPCSound(n, "SFX\SCP\939\" + (n\ID Mod 3) + "Attack" + Rand(0, 2) + ".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 10.0, 1.0, True)
								
								PlaySound_Strict(LoadTempSound("SFX\SCP\939\Horror.ogg"))
								n\State3 = 1.0
							EndIf
							n\State = 3.0
						ElseIf PowTwo(me\SndVolume * 1.6) > Dist
							If n\State <> 1 And n\Reload <= 0.0
								LoadNPCSound(n, "SFX\SCP\939\" + (n\ID Mod 3) + "Alert" + Rand(0, 2) + ".ogg")
								n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 10.0, 1.0, True)
								
								SetNPCFrame(n, 175.0)
								
								n\Reload = 70.0 * 3.0
							EndIf
							n\State = 1.0
						EndIf
						n\Reload = n\Reload - fps\Factor[0]
					EndIf
					
					RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0, True)
					
					PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.28, EntityZ(n\Collider))
					RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider), EntityRoll(n\Collider), True)
				EndIf
				;[End Block]
			Case NPCType066
				;[Block]
				Dist = EntityDistanceSquared(n\Collider, me\Collider)
				
				Select n\State
					Case 0.0
						;[Block]
						; ~ Idles: moves around randomly from waypoint to another if the player is far enough
						; ~ Starts staring at the player when the player is close enough
						
						If Dist > 400.0
							AnimateNPC(n, 451.0, 612.0, 0.2)
							
							If n\State2 < MilliSecs()
								For w.WayPoints = Each WayPoints
									If w\door = Null
										If DistanceSquared(EntityX(w\OBJ, True), EntityX(n\Collider), EntityZ(w\OBJ, True), EntityZ(n\Collider)) < 16.0
											PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 200.0 * RoomScale, EntityZ(w\OBJ, True))
											ResetEntity(n\Collider)
											Exit
										EndIf
									EndIf
								Next
								n\State2 = MilliSecs() + 5000
							EndIf
						ElseIf Dist < 64.0
							n\LastDist = Rnd(1.0, 2.5)
							n\State = 1.0
						EndIf
						;[End Block]
					Case 1.0 ; ~ Staring at the player
						;[Block]
						If n\Frame < 451.0
							Angle = WrapAngle(CurveAngle(DeltaYaw(n\Collider, me\Collider) - 180.0, (AnimTime(n\OBJ) - 2.0) / 1.2445, 15.0))
							
							SetNPCFrame(n, (Angle * 1.2445) + 2.0)
						Else
							AnimateNPC(n, 636.0, 646.0, 0.4, False)
							If n\Frame = 646.0 Then SetNPCFrame(n, 2.0)
						EndIf
						
						If Rand(700) = 1 Then PlaySound2(LoadTempSound("SFX\SCP\066\Eric" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 8.0, 1.0, True)
						
						If Dist < 1.0 + PowTwo(n\LastDist)
							If EntityVisible(me\Collider, n\Collider)
								GiveAchievement(Achv066)
								n\State = Rand(2.0, 3.0)
							EndIf
						EndIf
						;[End Block]
					Case 2.0 ; ~ Rolls towards the player and make a sound, and then escape	
						;[Block]
						If n\Frame < 647.0
							Angle = CurveAngle(0.0, (AnimTime(n\OBJ) - 2.0) / 1.2445, 5.0)
							
							If Angle < 5.0 Lor Angle > 355.0
								SetNPCFrame(n, 647.0)
							Else
								SetNPCFrame(n, Angle * 1.2445 + 2.0)
							EndIf
						Else
							If n\Frame = 683.0
								If n\State2 = 0.0
									If Rand(2) = 1
										PlaySound2(LoadTempSound("SFX\SCP\066\Eric" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 8.0, 1.0, True)
									Else
										PlaySound2(LoadTempSound("SFX\SCP\066\Notes" + Rand(0, 5) + ".ogg"), Camera, n\Collider, 8.0)
									EndIf
									
									If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
										Select Rand(6)
											Case 1
												;[Block]
												PlaySound_Strict(LoadTempSound("SFX\SCP\066\Beethoven.ogg"))
												me\DeafTimer = 70.0 * (45.0 + (15.0 * SelectedDifficulty\AggressiveNPCs))
												me\Deaf = True
												me\BigCameraShake = 10.0
												;[End Block]
											Case 2
												;[Block]
												n\State3 = Rand(700.0, 1400.0)
												;[End Block]
											Case 3
												;[Block]
												For d.Doors = Each Doors
													If d\Locked = 0 And d\KeyCard = 0 And d\Code = 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR
														If EntityDistanceSquared(d\FrameOBJ, n\Collider) < 256.0 Then OpenCloseDoor(d)
													EndIf
												Next
												;[End Block]
											Case 4
												;[Block]
												If (Not PlayerRoom\RoomTemplate\DisableDecals)
													de.Decals = CreateDecal(DECAL_CORROSIVE_2, EntityX(n\Collider), 0.005, EntityZ(n\Collider), 90.0, Rnd(360.0), 0.0, 0.3)
													EntityParent(de\OBJ, PlayerRoom\OBJ)
												EndIf
												me\BigCameraShake = 5.0
												PlaySound_Strict(LoadTempSound("SFX\Character\BodyFall.ogg"))
												If DistanceSquared(EntityX(me\Collider), EntityX(n\Collider), EntityZ(me\Collider), EntityZ(n\Collider)) < 0.64 Then InjurePlayer(Rnd(0.3, 0.5), 0.0, 200.0)
												;[End Block]
											Case 5, 6 ; ~ No effect
												;[Block]
												;[End Block]
										End Select
									EndIf
								EndIf
								
								n\State2 = n\State2 + fps\Factor[0]
								If n\State2 > 70.0
									n\State2 = 0.0
									n\State = 3.0
								EndIf
							Else
								n\CurrSpeed = CurveValue(n\Speed * 1.5, n\CurrSpeed, 10.0)
								PointEntity(n\OBJ, me\Collider)
								
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ) - 180.0, EntityYaw(n\Collider), 10.0), 0.0)
								
								AnimateNPC(n, 647.0, 683.0, n\CurrSpeed * 25.0, False)
								
								MoveEntity(n\Collider, 0.0, 0.0, (-n\CurrSpeed) * fps\Factor[0])
							EndIf
						EndIf
						;[End Block]
					Case 3.0
						;[Block]
						PointEntity(n\OBJ, me\Collider)
						Angle = CurveAngle(EntityYaw(n\OBJ) + n\Angle - 180.0, EntityYaw(n\Collider), 10.0)
						RotateEntity(n\Collider, 0.0, Angle, 0.0)
						
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
						MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
						
						If Rand(100) = 1 Then n\Angle = Rnd(-20.0, 20.0)
						
						n\State2 = n\State2 + fps\Factor[0]
						If n\State2 > 250.0
							AnimateNPC(n, 684.0, 647.0, (-n\CurrSpeed) * 25.0, False)
							If n\Frame = 647.0
								n\State2 = 0.0
								n\State = 0.0
							EndIf
						Else
							AnimateNPC(n, 684.0, 647.0, (-n\CurrSpeed) * 25.0)
						EndIf
						;[End Block]
				End Select
				
				If n\State > 1.0
					If n\Sound = 0 Then n\Sound = LoadSound_Strict("SFX\SCP\066\Rolling.ogg")
					If (Not ChannelPlaying(n\SoundCHN)) Then n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
				EndIf
				
				If n\State3 > 0.0
					n\State3 = n\State3 - fps\Factor[0]
					LightVolume = TempLightVolume - TempLightVolume * Min(Max(n\State3 / 500.0, 0.01), 0.6)
					me\HeartBeatRate = Max(me\HeartBeatRate, 130.0)
					me\HeartBeatVolume = Max(me\HeartBeatVolume, Min(n\State3 / 1000.0, 1.0))
				EndIf
				
				UpdateSoundOrigin(n\SoundCHN2, Camera, n\Collider, 20.0, 1.0, False)
				If ChannelPlaying(n\SoundCHN2) Then me\BlurTimer = Max((5.0 - (Sqr(Dist)) * 300.0), 0.0)
				
				PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
				
				RotateEntity(n\OBJ, EntityPitch(n\Collider) - 90.0, EntityYaw(n\Collider), 0.0)
				;[End Block]
			Case NPCType966
				;[Block]
				Dist = EntityDistanceSquared(n\Collider, me\Collider)
				
				If n\State > -1.0
					If Dist < PowTwo(HideDistance)
						; ~ n\State: The "general" state (Idles / Wanders off/ Attacks / Echo and etc.)
						
						; ~ n\State2: Timer for doing raycasts
						
						; ~ n\State3: Timer for making all instances aggressive to player
						
						UpdateNPCBlinking(n)
						
						PrevFrame = n\Frame
						
						If wi\NightVision = 0
							If (Not EntityHidden(n\OBJ)) Then HideEntity(n\OBJ)
							If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
								If Dist < 1.0 And n\Reload <= 0.0
									Select Rand(6)
										Case 1
											;[Block]
											CreateMsg(GetLocalString("msg", "966_1"))
											;[End Block]
										Case 2
											;[Block]
											CreateMsg(GetLocalString("msg", "966_2"))
											;[End Block]
										Case 3
											;[Block]
											CreateMsg(GetLocalString("msg", "966_3"))
											;[End Block]
										Case 4
											;[Block]
											CreateMsg(GetLocalString("msg", "966_4"))
											;[End Block]
										Case 5
											;[Block]
											CreateMsg(GetLocalString("msg", "966_5"))
											;[End Block]
										Case 6
											;[Block]
											CreateMsg(GetLocalString("msg", "966_6"))
											;[End Block]
									End Select
									n\Reload = 70.0 * 30.0
								EndIf
							EndIf
						Else
							If EntityVisible(n\Collider, me\Collider) Then GiveAchievement(Achv966)
							If EntityHidden(n\OBJ) Then ShowEntity(n\OBJ)
						EndIf
						n\Reload = n\Reload - fps\Factor[0]
						
						If n\State3 > 70.0 * 5.0
							If n\State3 < 1000.0
								For n2.NPCs = Each NPCs
									If n2\NPCType = n\NPCType Then n2\State3 = 1000.0 
								Next
								n\State3 = 1000.0
							EndIf
							n\State = Max(n\State, 8.0)
						EndIf
						
						If me\Stamina < 10.0
							n\State3 = n\State3 + fps\Factor[0]
						ElseIf n\State3 < 900.0
							n\State3 = Max(n\State3 - fps\Factor[0] * 0.2, 0.0)
						EndIf
						
						Select n\State
							Case 0.0 ; ~ Idles
								;[Block]
								If n\Frame > 557.0
									AnimateNPC(n, 628.0, 652.0, 0.25, False)
									If n\Frame > 651.9 Then SetNPCFrame(n, 2.0)
								Else
									AnimateNPC(n, 2.0, 214.0, 0.25, False)
									
									; ~ Echo / Stares off / Walking around periodically
									If n\Frame > 213.9
										If Rand(3) = 1 And Dist < 12.0
											n\State = Rand(1.0, 4.0)
										Else
											n\State = Rand(5.0, 6.0)
										EndIf
									EndIf
									; ~ Echo if player gets close
									If NPCSeesPlayer(n, 4.0 - me\CrouchState + me\SndVolume) = 1 Then n\State = Rand(4)
								EndIf
								;[End Block]
							Case 1.0, 2.0 ; ~ Echo
								;[Block]
								AnimateNPC(n, 214.0, 257.0, 0.25, False)
								If n\Frame > 256.9 Then n\State = 0.0
								
								If NPCSeesPlayer(n, 4.0 - me\CrouchState + me\SndVolume) = 1
									PointEntity(n\Collider, me\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									
									If n\State3 < 900.0
										me\BlurTimer = Float(((Sin(MilliSec / 50.0) + 1.0) * 200.0) / Sqr(Dist))
										
										If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4
											If me\StaminaEffect < 1.5
												Select Rand(4)
													Case 1
														;[Block]
														CreateMsg(GetLocalString("msg", "966.sleep_1"))
														;[End Block]
													Case 2
														;[Block]
														CreateMsg(GetLocalString("msg", "966.sleep_2"))
														;[End Block]
													Case 3
														;[Block]
														CreateMsg(GetLocalString("msg", "966.sleep_3"))
														;[End Block]
													Case 4
														;[Block]
														CreateMsg(GetLocalString("msg", "966.sleep_4"))
														;[End Block]
												End Select
											EndIf
											me\BlinkEffect = 1.5 - (0.25 * I_714\Using)
											me\BlinkEffectTimer = 1000.0 - (500.0 * I_714\Using)
											
											me\StaminaEffect = 2.0 - (0.5 * I_714\Using)
											me\StaminaEffectTimer = 1000.0 - (500.0 * I_714\Using)
										EndIf
									EndIf
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								EndIf
								
								If n\Frame > 228.0 And PrevFrame <= 228.0
									If (Not ChannelPlaying(n\SoundCHN)) Then n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\966\Echo" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
								EndIf
								UpdateSoundOrigin(n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True)
								;[End Block]
							Case 3.0, 4.0 ; ~ Stare off at player
								;[Block]
								If n\State = 3.0
									AnimateNPC(n, 258.0, 332.0, 0.25, False)
									If n\Frame > 331.9 Then n\State = 0.0
								Else
									AnimateNPC(n, 333.0, 457.0, 0.25, False)
									If n\Frame > 456.9 Then n\State = 0.0
								EndIf
								
								If NPCSeesPlayer(n, 4.0 - me\CrouchState + me\SndVolume) = 1
									PointEntity(n\Collider, me\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								EndIf
								
								If (n\Frame > 271.0 And PrevFrame <= 271.0) Lor (n\Frame > 301.0 And PrevFrame <= 301.0) Lor (n\Frame > 314.0 And PrevFrame <= 314.0)
									If (Not ChannelPlaying(n\SoundCHN)) Then n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\966\Idle" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
								EndIf
								UpdateSoundOrigin(n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True)
								;[End Block]
							Case 5.0, 6.0, 8.0 ; ~ Walking or chasing
								;[Block]
								If n\State = 8.0
									If NPCSeesPlayer(n, 6.0 - me\CrouchState + me\SndVolume) = 1 ; ~ Chasing the player
										PointEntity(n\Collider, me\Collider)
										RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										
										n\PathTimer = 0.0
										n\PathStatus = PATH_STATUS_NO_SEARCH
										n\PathLocation = 0
										
										If Dist < 0.7225 Then n\State = 9.0
									Else ; ~ Trying to find the player
										If n\PathTimer <= 0.0 ; ~ Update the path
											n\PathStatus = FindPath(n, EntityX(me\Collider), EntityY(me\Collider) + 0.1, EntityZ(me\Collider))
											If n\PathStatus = PATH_STATUS_FOUND
												While n\Path[n\PathLocation] = Null
													If n\PathLocation > MaxPathLocations - 1
														n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
														Exit
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Wend
												If n\PathLocation < MaxPathLocations - 1
													If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
														If n\Path[n\PathLocation]\door = Null
															If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
														EndIf
													EndIf
												EndIf
												UseDoorNPC(n)
											EndIf
											n\PathTimer = 70.0 * 10.0 ; ~ Search again after 10 seconds
										Else
											; ~ Still attack if the player is too close
											If Dist < 0.81 And (Not chs\NoTarget)
												If EntityVisible(n\Collider, me\Collider) Then n\State = 9.0
											EndIf
											
											If n\PathStatus = PATH_STATUS_FOUND
												If n\Path[n\PathLocation] = Null
													If n\PathLocation > MaxPathLocations - 1
														n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
													Else
														n\PathLocation = n\PathLocation + 1
													EndIf
												Else
													PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
													RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
													n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
													MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
													
													UseDoorNPC(n)
												EndIf
												n\PathTimer = Max(n\PathTimer - fps\Factor[0], 0.0) ; ~ Timer goes down fast
											Else
												n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
												n\PathTimer = Max(n\PathTimer - (fps\Factor[0] * 2.0), 0.0) ; ~ Timer goes down fast
											EndIf
										EndIf
									EndIf
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								Else ; ~ Wandering around
									Temp = False
									If MilliSecs() > n\State2
										If (Not EntityHidden(n\Collider)) Then HideEntity(n\Collider) 
										EntityPick(n\Collider, 1.5)
										If PickedEntity() <> 0 Then Temp = True
										If EntityHidden(n\Collider) Then ShowEntity(n\Collider)
										
										n\State2 = MilliSecs() + 1000
										
										If Rand(5) = 1 Then n\State = 0.0
									EndIf
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True) + (Temp * Rnd(80.0, 110.0)), 0.0, True)
									n\CurrSpeed = CurveValue(n\Speed * 0.6, n\CurrSpeed, 10.0)
									MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								EndIf
								
								If n\CurrSpeed < 0.005
									If n\Frame > 557.0
										AnimateNPC(n, 628.0, 652.0, 0.25, False)
										If n\Frame > 651.9 Then SetNPCFrame(n, 2.0)
									Else
										AnimateNPC(n, 2.0, 214.0, 0.25)
									EndIf
									If (PrevFrame < 650.0 And n\Frame >= 650.0) Then PlaySound2(snd_I\Step2SFX[Rand(3, 6)], Camera, n\Collider, 7.0, Rnd(0.5, 0.7))
								Else
									AnimateNPC(n, Max(Min(AnimTime(n\OBJ), 580.0), 557.0), 628.0, n\CurrSpeed * 25.0)
									If (PrevFrame < 581.0 And n\Frame >= 581.0) Lor (PrevFrame < 607.0 And n\Frame >= 607.0) Then PlaySound2(snd_I\Step2SFX[Rand(3, 6)], Camera, n\Collider, 7.0, Rnd(0.5, 0.7))
								EndIf
								;[End Block]
							Case 9.0 ; ~ Attacks
								;[Block]
								If (Not me\Terminated)
									n\CurrSpeed = 0.0
									PointEntity(n\Collider, me\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
									
									If n\LastSeen = 0
										n\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\966\Echo" + Rand(0, 2) + ".ogg"), Camera, n\Collider, 10.0, 1.0, True)
										n\LastSeen = 1
									EndIf
									
									If n\Frame > 557.0
										AnimateNPC(n, 629.0, 652.0, 0.25, False)
										If (PrevFrame < 650.0 And n\Frame >= 650.0) Then PlaySound2(snd_I\Step2SFX[Rand(3, 6)], Camera, n\Collider, 7.0, Rnd(0.5, 0.7))
										If n\Frame > 651.9
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
										If n\Frame <= 487.0
											AnimateNPC(n, 458.0, 487.0, 0.3, False)
											If n\Frame > 486.9
												n\LastSeen = 0
												n\State = 8.0
											EndIf
										ElseIf n\Frame <= 517.0
											AnimateNPC(n, 488.0, 517.0, 0.3, False)
											If n\Frame > 516.9
												n\LastSeen = 0
												n\State = 8.0
											EndIf
										ElseIf n\Frame <= 556.0
											AnimateNPC(n, 518.0, 556.0, 0.3, False)
											If n\Frame > 555.9
												n\LastSeen = 0
												n\State = 8.0
											EndIf
										EndIf
									EndIf
									
									If (n\Frame > 470.0 And PrevFrame <= 470.0) Lor (n\Frame > 500.0 And PrevFrame <= 500.0) Lor (n\Frame > 527.0 And PrevFrame <= 527.0)
										If Dist < 0.81
											PlaySound2(snd_I\DamageSFX[Rand(11, 12)], Camera, n\Collider)
											InjurePlayer(Rnd(0.45, 0.75) * DifficultyDMGMult, 0.0, 500.0, Rnd(0.2, 0.25) * DifficultyDMGMult)
											me\CameraShake = 1.8
											If me\Injuries > 10.0
												msg\DeathMsg = Format(GetLocalString("death", "966"), SubjectName)
												Kill(True)
											EndIf
										Else
											PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
										EndIf
									EndIf
								EndIf
								;[End Block]
						End Select
						PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.2, EntityZ(n\Collider, True), True)
						RotateEntity(n\OBJ, -90.0, n\Angle, 0.0, True)
					Else
						If (Not EntityHidden(n\OBJ)) Then HideEntity(n\OBJ)
						If Rand(850 - (250 * SelectedDifficulty\AggressiveNPCs)) = 1 Then TeleportCloser(n)
					EndIf
				EndIf
				;[End Block]
			Case NPCType1499_1
				;[Block]
				; ~ n\State: Current State of the NPC
				
				; ~ n\State2: A second state variable (dependend on the current NPC's n\State)
				
				; ~ n\State3: Determines if the NPC will always be aggressive against the player
				
				; ~ n\PrevState: Determines the type / behaviour of the NPC
				; ~ 0: Normal / Citizen
				; ~ 1: Stair guard / Guard next to king
				; ~ 2: King
				; ~ 3: Front guard
				
				PrevFrame = n\Frame
				
				If n\Idle = 0 And EntityDistanceSquared(n\Collider, me\Collider) < PowTwo(HideDistance * 3.0)
					If n\PrevState = 0
						If n\State = 0.0 Lor n\State = 2.0
							For n2.NPCs = Each NPCs
								If n2\NPCType = n\NPCType And n2 <> n
									If n2\State <> 0.0 And n2\State <> 2.0
										If n2\PrevState = 0
											n\State = 1.0
											n\State2 = 0.0
											Exit
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					EndIf
					
					Select n\State
						Case 0.0
							;[Block]
							If n\PrevState = 0
								If n\CurrSpeed = 0.0
									If n\Reload = 0.0
										If n\State2 < 500.0 * Rnd(1.0, 3.0)
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + fps\Factor[0]
										Else
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									Else
										If n\State2 < 1500.0
											n\CurrSpeed = 0.0
											n\State2 = n\State2 + fps\Factor[0]
										Else
											If n\Target <> Null
												If n\Target\Target <> Null Then n\Target\Target = Null
												n\Target\Reload = 0.0
												n\Target\Angle = n\Target\Angle + Rnd(-45.0, 45.0)
												n\Target = Null
												n\Reload = 0.0
												n\Angle = n\Angle + Rnd(-45.0, 45.0)
											EndIf
											If n\CurrSpeed = 0.0 Then n\CurrSpeed = n\CurrSpeed + 0.0001
										EndIf
									EndIf
								Else
									If n\Target <> Null Then n\State2 = 0.0
									
									If n\State2 < 10000.0 * Rnd(1.0, 3.0)
										n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 10.0)
										n\State2 = n\State2 + fps\Factor[0]
									Else
										n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 50.0)
									EndIf
									
									RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle, EntityYaw(n\Collider), 10.0), 0.0)
									
									If n\Target = Null
										If Rand(200) = 1 Then n\Angle = n\Angle + Rnd(-45.0, 45.0)
										
										If (Not EntityHidden(n\Collider)) Then HideEntity(n\Collider)
										EntityPick(n\Collider, 1.5)
										If PickedEntity() <> 0 Then n\Angle = EntityYaw(n\Collider) + Rnd(80.0, 110.0)
										If EntityHidden(n\Collider) Then ShowEntity(n\Collider)
									Else
										n\Angle = EntityYaw(n\Collider) + DeltaYaw(n\Collider, n\Target\Collider)
										If EntityDistanceSquared(n\Collider, n\Target\Collider) < 2.25
											n\CurrSpeed = 0.0
											n\Target\CurrSpeed = 0.0
											n\Reload = 1.0
											n\Target\Reload = 1.0
											Temp = Rand(0, 2)
											If Temp = 0
												SetNPCFrame(n, 296.0)
											ElseIf Temp = 1
												SetNPCFrame(n, 856.0)
											Else
												SetNPCFrame(n, 905.0)
											EndIf
											Temp = Rand(0, 2)
											If Temp = 0
												SetNPCFrame(n\Target, 296.0)
											ElseIf Temp = 1
												SetNPCFrame(n\Target, 856.0)
											Else
												SetNPCFrame(n\Target, 905.0)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle, EntityYaw(n\Collider), 10.0), 0.0)
							EndIf
							
							If n\CurrSpeed = 0.0
								If n\Reload = 0.0 And n\PrevState <> 2
									AnimateNPC(n, 296.0, 320.0, 0.2)
								ElseIf n\Reload = 0.0 And n\PrevState = 2
									If n\Frame <= 532.5
										AnimateNPC(n, 509.0, 533.0, 0.2, False)
									ElseIf n\Frame > 533.5 And n\Frame <= 600.5
										AnimateNPC(n, 534.0, 601.0, 0.2, False)
									Else
										Temp = Rand(False, True)
										If (Not Temp)
											SetNPCFrame(n, 509.0)
										Else
											SetNPCFrame(n, 534.0)
										EndIf
									EndIf
								Else
									If n\PrevState = 2
										AnimateNPC(n, 713.0, 855.0, 0.2, False)
										If n\Frame > 833.5
											PointEntity(n\OBJ, me\Collider)
											RotateEntity(n\Collider, 0.0, CurveAngle(n\Angle, EntityYaw(n\Collider), 10.0), 0.0)
										EndIf
									ElseIf n\PrevState = 1
										AnimateNPC(n, 602.0, 712.0, 0.2, False)
										If n\Frame > 711.5 Then n\Reload = 0.0
									Else
										If n\Frame <= 319.5
											AnimateNPC(n, 296.0, 320.0, 0.2, False)
										ElseIf n\Frame > 320.5 And n\Frame < 903.5
											AnimateNPC(n, 856.0, 904.0, 0.2, False)
										ElseIf n\Frame > 904.5 And n\Frame < 952.5
											AnimateNPC(n, 905.0, 953.0, 0.2, False)
										Else
											Temp = Rand(0, 2)
											If Temp = 0
												SetNPCFrame(n, 296.0)
											ElseIf Temp = 1
												SetNPCFrame(n, 856.0)
											Else
												SetNPCFrame(n, 905.0)
											EndIf
										EndIf
									EndIf
								EndIf
							Else
								If (n\ID Mod 2) = 0
									AnimateNPC(n, 1.0, 62.0, n\CurrSpeed * 28.0)
								Else
									AnimateNPC(n, 100.0, 167.0, n\CurrSpeed * 28.0)
								EndIf
								If n\PrevState = 0
									If n\Target = Null
										If Rand(1200) = 1
											For n2.NPCs = Each NPCs
												If n2 <> n
													If n2\NPCType = n\NPCType
														If n2\Target = Null
															If n2\PrevState = 0
																If EntityDistanceSquared(n\Collider, n2\Collider) < 400.0
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
							
							; ~ Randomly play the "screaming animation" and revert back to State = 0.0
							If n\Target = Null And n\PrevState = 0
								If Rand(5000) = 1
									If (Not ChannelPlaying(n\SoundCHN))
										Dist = EntityDistanceSquared(n\Collider, me\Collider)
										If Dist < 400.0
											LoadNPCSound(n, "SFX\SCP\1499\Idle" + Rand(0, 3) + ".ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0, 1.0, True)
										EndIf
									EndIf
									n\State2 = 0.0
									n\State = 2.0
								EndIf
								
								If (n\ID Mod 2) = 0 And (Not (chs\NoTarget Lor I_268\InvisibilityOn))
									Dist = EntityDistanceSquared(n\Collider, me\Collider)
									If Dist < 100.0
										If EntityVisible(n\Collider, me\Collider)
											; ~ Play the "screaming animation"
											If Dist < 25.0
												LoadNPCSound(n, "SFX\SCP\1499\Triggered.ogg")
												n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0, 1.0, True)
												
												n\State2 = 1.0 ; ~ If player is too close, switch to attack after screaming
												
												For n2.NPCs = Each NPCs
													If n2\NPCType = n\NPCType And n2 <> n
														If n2\PrevState = 0
															n2\State = 1.0
															n2\State2 = 0.0
														EndIf
													EndIf
												Next
											Else
												n\State2 = 0.0 ; ~ Otherwise keep idling
											EndIf
											SetNPCFrame(n, 203.0)
											n\State = 2.0
										EndIf
									EndIf
								EndIf
							ElseIf n\PrevState = 1
								Dist = EntityDistanceSquared(n\Collider, me\Collider)
								If (Not (chs\NoTarget Lor I_268\InvisibilityOn))
									If Dist < 16.0
										If EntityVisible(n\Collider, me\Collider)
											LoadNPCSound(n, "SFX\SCP\1499\Triggered.ogg")
											n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0, 1.0, True)
											
											SetNPCFrame(n, 203.0)
											n\State = 1.0
										EndIf
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 1.0 ; ~ Attacking the player
							;[Block]
							If chs\NoTarget Lor I_268\InvisibilityOn Then n\State = 0.0
							
							If PlayerRoom\RoomTemplate\RoomID = r_dimension_1499 And n\PrevState = 0 Then ShouldPlay = 18
							
							PointEntity(n\OBJ, me\Collider)
							RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
							
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							
							If n\State2 = 0.0
								If n\PrevState = 1
									n\CurrSpeed = CurveValue(n\Speed * 2.0, n\CurrSpeed, 10.0)
									If n\Target <> Null Then n\Target\State = 1.0
								Else
									n\CurrSpeed = CurveValue(n\Speed * 1.75, n\CurrSpeed, 10.0)
								EndIf
								
								If (n\ID Mod 2) = 0
									AnimateNPC(n, 1.0, 62.0, n\CurrSpeed * 28.0)
								Else
									AnimateNPC(n, 100.0, 167.0, n\CurrSpeed * 28.0)
								EndIf
							EndIf
							
							If Dist < 0.5625
								If (n\ID Mod 2) = 0 Lor n\State3 = 1.0 Lor n\PrevState = 1 Lor n\PrevState = 3 Lor n\PrevState = 4
									If n\State2 = 1.0
										SetNPCFrame(n, 63.0)
									Else
										SetNPCFrame(n, 168.0)
									EndIf
									n\State2 = Rand(1.0, 2.0)
									n\State = 3.0
								Else
									n\State = 4.0
								EndIf
							EndIf
							;[End Block]
						Case 2.0 ; ~ Play the "screaming animation" and switch to n\State2 after it's finished
							;[Block]
							n\CurrSpeed = 0.0
							AnimateNPC(n, 203.0, 295.0, 0.1, False)
							
							If n\Frame > 294.0 Then n\State = n\State2
							;[End Block]
						Case 3.0 ; ~ Slashing at the player
							;[Block]
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							If n\State2 = 1.0
								AnimateNPC(n, 63.0, 100.0, 0.6, False)
								Attack = (PrevFrame < 89.0 And n\Frame >= 89.0)
								If n\Frame >= 99.0
									n\State2 = 0.0
									n\State = 1.0
								EndIf
							Else
								AnimateNPC(n, 168.0, 202.0, 0.6, False)
								Attack = (PrevFrame < 189.0 And n\Frame >= 189.0)
								If n\Frame >= 201.0
									n\State2 = 0.0
									n\State = 1.0
								EndIf
							EndIf
							
							If Attack
								If Dist > 0.64 Lor Abs(DeltaYaw(n\Collider, me\Collider)) > 60.0
									PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
								Else
									PlaySound2(snd_I\DamageSFX[Rand(11, 12)], Camera, n\Collider)
									InjurePlayer(Rnd(0.65, 1.1) * DifficultyDMGMult, 0.0, 500.0, Rnd(0.3, 0.35) * DifficultyDMGMult, 0.2)
									me\CameraShake = 2.5
									
									If me\Injuries > 10.0
										If PlayerRoom\RoomTemplate\RoomID = r_dimension_1499
											msg\DeathMsg = GetLocalString("death", "1499.dimension")
										Else
											msg\DeathMsg = GetLocalString("death", "1499")
										EndIf
										Kill(True)
									EndIf
								EndIf
							EndIf
							;[End Block]
						Case 4.0 ; ~ Standing in front of the player
							;[Block]
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 5.0)
							AnimateNPC(n, 296.0, 320.0, 0.2)
							
							PointEntity(n\OBJ, me\Collider)
							RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
							
							If Dist > 0.64 Then n\State = 1.0
							;[End Block]
					End Select
					
					UpdateSoundOrigin(n\SoundCHN, Camera, n\Collider, 20.0, 1.0, True)
					
					MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
					
					RotateEntity(n\OBJ, 0.0, EntityYaw(n\Collider) - 180.0, 0.0)
					PositionEntity(n\OBJ, EntityX(n\Collider), EntityY(n\Collider) - 0.2, EntityZ(n\Collider))
					
					If EntityHidden(n\OBJ) Then ShowEntity(n\OBJ)
				Else
					If (Not EntityHidden(n\OBJ)) Then HideEntity(n\OBJ)
				EndIf
				;[End Block]
			Case NPCType008_1
				;[Block]
				; ~ n\State: Main State
				
				; ~ n\State2: A timer used for the player detection
				
				; ~ n\State3: A timer for making the NPC idle (if the player escapes during that time)
				
				If (Not n\IsDead)
					PrevFrame = n\Frame
					
					UpdateNPCBlinking(n)
					
					Select n\State
						Case 0.0 ; ~ Lying next to the wall
							;[Block]
							SetNPCFrame(n, 11.0)
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							;[End Block]
						Case 1.0 ; ~ Stands up
							;[Block]
							AnimateNPC(n, 11.0, 29.0, 0.1, False)
							If n\Frame > 28.9 Then n\State = 3.0
							;[End Block]
						Case 2.0 ; ~ Player/NPC is visible, tries to kill
							;[Block]
							n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
							If n\State2 > 0.0
								If n\Target = Null
									If NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume) = 1 Then n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
									PointEntity(n\Collider, me\Collider)
									Dist = EntityDistanceSquared(n\Collider, me\Collider)
								Else
									If NPCSeesNPC(n\Target, n) = 1 Then n\State2 = 70.0 * 2.0
									PointEntity(n\Collider, n\Target\Collider)
									Dist = EntityDistanceSquared(n\Collider, n\Target\Collider)
								EndIf
								
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								
								If Dist < 9.0
									AnimateNPC(n, Min(AnimTime(n\OBJ), 95.0), 125.0, n\CurrSpeed * 30.0)
								Else
									AnimateNPC(n, Min(AnimTime(n\OBJ), 64.0), 93.0, n\CurrSpeed * 30.0)
								EndIf
								MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
								
								If Dist < 0.49
									SetNPCFrame(n, 126.0)
									n\State = 4.0
								EndIf
								
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\PathLocation = 0
								
								If (PrevFrame < 65.0 And n\Frame >= 65.0) Lor (PrevFrame < 80.0 And n\Frame >= 80.0) Lor (PrevFrame < 95.0 And n\Frame >= 95.0) Lor (PrevFrame < 113.0 And n\Frame >= 113.0) Then PlaySound2(StepSFX(GetStepSound(n\Collider), 0, Rand(0, 7)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							Else
								n\Target = Null
								n\State = 3.0
							EndIf
							;[End Block]
						Case 3.0 ; ~ Player/NPC isn't visible, tries to find
							;[Block]
							Dist = EntityDistanceSquared(n\Collider, me\Collider)
							If n\PathTimer <= 0.0 ; ~ Update path
								x = 0.0
								y = 0.0
								z = 0.0
								
								; ~ Tries to find a room that is close
								For r.Rooms = Each Rooms
									If EntityDistanceSquared(r\OBJ, n\Collider) < 196.0 And EntityDistanceSquared(r\OBJ, n\Collider) > 36.0
										x = EntityX(r\OBJ)
										y = 0.1
										z = EntityZ(r\OBJ)
										Exit
									EndIf
								Next
								
								If x <> 0.0 And y <> 0.0 And z <> 0.0 Then n\PathStatus = FindPath(n, x, y, z)
								
								If n\PathStatus = PATH_STATUS_FOUND
									While n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
											Exit
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Wend
									If n\PathLocation < MaxPathLocations - 1
										If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
											If n\Path[n\PathLocation]\door = Null
												If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
											EndIf
										EndIf
									EndIf
									UseDoorNPC(n)
								EndIf
								n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
							Else
								; ~ Still attack if the player is too close
								If Dist < 0.49 And (Not chs\NoTarget)
									If EntityVisible(me\Collider, n\Collider) Then n\State = 4.0
								EndIf
								
								If n\PathStatus = PATH_STATUS_FOUND
									If n\Path[n\PathLocation] = Null
										If n\PathLocation > MaxPathLocations - 1
											n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
										Else
											n\PathLocation = n\PathLocation + 1
										EndIf
									Else
										PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
										RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
										
										n\CurrSpeed = CurveValue(n\Speed * 0.7, n\CurrSpeed, 20.0)
										MoveEntity(n\Collider, 0.0, 0.0, n\CurrSpeed * fps\Factor[0])
										n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
										
										UseDoorNPC(n)
									EndIf
									n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
								Else
									n\CurrSpeed = CurveValue(0.0, n\CurrSpeed, 10.0)
									n\PathTimer = n\PathTimer - (fps\Factor[0] * 2.0) ; ~ Timer goes down fast
								EndIf
							EndIf
							
							If n\CurrSpeed < 0.005
								AnimateNPC(n, 323.0, 344.0, 0.2)
							Else
								AnimateNPC(n, 64.0, 93.0, n\CurrSpeed * 30.0)
								If (PrevFrame < 65.0 And n\Frame >= 65.0) Lor (PrevFrame < 80.0 And n\Frame >= 80.0) Then PlaySound2(StepSFX(GetStepSound(n\Collider), 0, Rand(0, 7)), Camera, n\Collider, 8.0, Rnd(0.3, 0.5))
							EndIf
							
							If Dist > PowTwo(HideDistance)
								If n\State3 < 70.0 * (15.0 + (10.0 * SelectedDifficulty\AggressiveNPCs))
									n\State3 = n\State3 + fps\Factor[0]
								Else
									n\State3 = 70.0 * 360.0
									n\State = 5.0
								EndIf
							EndIf
							
							If n\Target = Null
								If NPCSeesPlayer(n, 8.0 - me\CrouchState + me\SndVolume) = 1
									n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
									n\State = 2.0
								EndIf
							EndIf
							
							For n2.NPCs = Each NPCs
								If n2\NPCType = NPCTypeMTF And (Not n2\IsDead)
									If NPCSeesNPC(n2, n) = 1
										n\Target = n2
										n\State2 = 70.0 * 2.0 ; ~ Give up after 2 seconds
										n\State = 2.0
										Exit
									EndIf
								EndIf
							Next
							;[End Block]
						Case 4.0 ; ~ Attacks
							;[Block]
							If (Not me\Terminated)
								n\CurrSpeed = 0.0
								If n\Target = Null
									PointEntity(n\Collider, me\Collider)
								Else
									PointEntity(n\Collider, n\Target\Collider)
								EndIf
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								AnimateNPC(n, 126.0, 165.0, 0.6, False)
								If n\Frame >= 146.0 And PrevFrame < 146.0
									If n\Target = Null
										If EntityDistanceSquared(n\Collider, me\Collider) < 0.5625
											PlaySound_Strict(snd_I\DamageSFX[Rand(5, 8)])
											InjurePlayer(Rnd(0.4, 0.7) * DifficultyDMGMult, 1.0 + SelectedDifficulty\AggressiveNPCs, 0.0, Rnd(0.175, 0.225) * DifficultyDMGMult, 0.2)
											me\CameraShake = 2.5
											
											If me\Injuries > 3.0
												msg\DeathMsg = Format(GetLocalString("death", "008"), SubjectName)
												Kill(True)
											EndIf
										Else
											PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
										EndIf
									Else
										If EntityDistanceSquared(n\Collider, n\Target\Collider) < 0.5625
											PlaySound2(snd_I\DamageSFX[Rand(5, 8)], Camera, n\Target\OBJ)
											If n\Target\HP > 0
												n\Target\HP = Max(n\Target\HP - Rnd(10.0, 20.0), 0.0)
											Else
												n\Target\IsDead = True
												n\Target = Null
												n\State = 3.0
												Return
											EndIf
										Else
											PlaySound2(snd_I\MissSFX, Camera, n\Collider, 2.5)
										EndIf
									EndIf
								ElseIf n\Frame >= 164.0
									If EntityDistanceSquared(n\Collider, me\Collider) < 0.49
										SetNPCFrame(n, 126.0)
									Else
										n\State = 2.0
									EndIf
								EndIf
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							Else
								n\State = 3.0
							EndIf
							
							If n\Target <> Null
								If n\Target\IsDead
									n\Target = Null
									n\State = 3.0
								EndIf
							EndIf
							;[End Block]
						Case 5.0 ; ~ Idling
							;[Block]
							If (Not EntityHidden(n\OBJ))
								HideEntity(n\OBJ)
								HideEntity(n\Collider)
								n\DropSpeed = 0.0
								PositionEntity(n\Collider, 0.0, -500.0, 0.0, True)
								ResetEntity(n\Collider)
							EndIf
							If n\Idle > 0
								n\Idle = Max(n\Idle - (1 + (1 * SelectedDifficulty\AggressiveNPCs)) * fps\Factor[0], 0.0)
							Else
								If PlayerInReachableRoom(True) ; ~ Player is in a room where SCP-008-1 can teleport to
									If Rand(50 - (20 * SelectedDifficulty\AggressiveNPCs)) = 1
										If EntityHidden(n\OBJ)
											ShowEntity(n\OBJ)
											ShowEntity(n\Collider)
											For w.WayPoints = Each WayPoints
												If w\door = Null And w\room\Dist < HideDistance And Rand(3) = 1
													If EntityDistanceSquared(w\room\OBJ, n\Collider) < EntityDistanceSquared(me\Collider, n\Collider)
														If DistanceSquared(EntityX(w\OBJ, True), EntityX(n\Collider), EntityZ(w\OBJ, True), EntityZ(n\Collider)) < 144.0
															If DistanceSquared(EntityX(w\OBJ, True), EntityX(n\Collider), EntityZ(w\OBJ, True), EntityZ(n\Collider)) > 16.0
																If w\room\Dist > 4.0
																	PositionEntity(n\Collider, EntityX(w\OBJ, True), EntityY(w\OBJ, True) + 200.0 * RoomScale, EntityZ(w\OBJ, True), True)
																	ResetEntity(n\Collider)
																	n\PathStatus = PATH_STATUS_NO_SEARCH
																	n\PathTimer = 0.0
																	n\PathLocation = 0
																	Exit
																EndIf
															EndIf
														EndIf
													EndIf
												EndIf
											Next
											n\State3 = 0.0
											n\State = 3.0
										EndIf
									EndIf
								EndIf
							EndIf
							;[End Block]
					End Select
					
					; ~ Loop the breath sound
					If n\State > 1.0 And n\State < 5.0 Then n\SoundCHN = LoopSound2(NPCSound[SOUND_NPC_008_1_BREATH], n\SoundCHN, Camera, n\Collider, 10.0, 1.0, True)
				Else
					AnimateNPC(n, 344.0, 363.0, 0.5, False)
				EndIf
				PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.2, EntityZ(n\Collider, True), True)
				RotateEntity(n\OBJ, 0.0, n\Angle - 180.0, 0.0, True)
				;[End Block]
		End Select
		
		Local GravityDist# = DistanceSquared(EntityX(me\Collider), EntityX(n\Collider), EntityZ(me\Collider), EntityZ(n\Collider))
		
		If GravityDist < PowTwo(HideDistance) Lor n\NPCType = NPCType1499_1
			If (Not n\IsOpt)
				EntityAlpha(n\OBJ, 1.0)
				n\IsOpt = True
			EndIf
		Else
			If n\IsOpt
				EntityAlpha(n\OBJ, 0.0)
				n\IsOpt = False
			EndIf
		EndIf
		
		If n\IsDead
			If n\GravityMult = 1.0
				EntityType(n\Collider, HIT_DEAD)
				
				Local RemoveSound% = False
				
				Select n\NPCType
					Case NPCType035_Tentacle
						;[Block]
						If n\Frame > 550.9
							HideEntity(n\Collider)
							HideEntity(n\OBJ)
							RemoveSound = True
							n\GravityMult = 0.0
						EndIf
						;[End Block]
					Case NPCType1048_A
						;[Block]
						HideEntity(n\Collider)
						HideEntity(n\OBJ)
						
						RemoveSound = True
						
						PlaySound2(LoadTempSound("SFX\SCP\1048A\Explode.ogg"), Camera, n\Collider, 8.0)
						p.Particles = CreateParticle(PARTICLE_BLOOD, EntityX(n\Collider), EntityY(n\Collider) + 0.2, EntityZ(n\Collider), 0.25, 0.0)
						EntityColor(p\OBJ, 100.0, 100.0, 100.0)
						RotateEntity(p\Pvt, 0.0, 0.0, Rnd(360.0))
						p\AlphaChange = -Rnd(0.02, 0.03)
						For i = 0 To 1
							p.Particles = CreateParticle(PARTICLE_BLOOD, EntityX(n\Collider) + Rnd(-0.2, 0.2), EntityY(n\Collider) + 0.25, EntityZ(n\Collider) + Rnd(-0.2, 0.2), 0.15, 0.0)
							EntityColor(p\OBJ, 100.0, 100.0, 100.0)
							RotateEntity(p\Pvt, 0.0, 0.0, Rnd(360.0))
							p\AlphaChange = -Rnd(0.02, 0.03)
						Next
						n\GravityMult = 0.0
						;[End Block]
					Case NPCTypeGuard
						;[Block]
						n\OBJ3 = CreatePivot(FindChild(n\OBJ, "Thumb01.R.001"))
						EntityRadius(n\OBJ3, 0.3)
						EntityPickMode(n\OBJ3, 1, False)
						
						RemoveSound = True
						n\Target = Null
						n\BlinkTimer = -1.0
						n\GravityMult = 0.0
						;[End Block]
					Default
						;[Block]
						RemoveSound = True
						n\Target = Null
						n\BlinkTimer = -1.0
						n\GravityMult = 0.0
						;[End Block]
				End Select
				If RemoveSound
					If ChannelPlaying(n\SoundCHN) Then StopChannel(n\SoundCHN) : n\SoundCHN = 0
					If n\Sound <> 0 Then FreeSound_Strict(n\Sound) : n\Sound = 0
					If ChannelPlaying(n\SoundCHN2) Then StopChannel(n\SoundCHN2) : n\SoundCHN2 = 0
					If n\Sound2 <> 0 Then FreeSound_Strict(n\Sound2) : n\Sound2 = 0
				EndIf
			EndIf
			If n\NPCType = NPCTypeGuard
				If n\OBJ3 <> 0
					If EntityDistanceSquared(n\OBJ3, me\Collider) < 1.0
						If EntityPick(Camera, 1.0) = n\OBJ3
							HandEntity = n\OBJ3
							If mo\MouseHit1
								Local RandomChance% = Rand(5)
								
								; ~ Special message for suicide guy
								If PlayerRoom\RoomTemplate\ID = r_room2_6_ez Then RandomChance = 6
								
								CreateMsg(GetLocalString("msg", "pickup.wpn_" + RandomChance))
								; ~ Remove the pivot for optimization. Do not allow the player pick up this weapon again. Can be restored by reloading the game, it's normal
								HandEntity = 0
								FreeEntity(n\OBJ3) : n\OBJ3 = 0
								;Exit
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Else
			If GravityDist < PowTwo(HideDistance * 0.6) Lor n\NPCType = NPCType1499_1
				If n\InFacility = InFacility
					TranslateEntity(n\Collider, 0.0, n\DropSpeed, 0.0)
					
					Local CollidedFloor% = False
					
					For i = 1 To CountCollisions(n\Collider)
						If CollisionY(n\Collider, i) < EntityY(n\Collider) - 0.01
							CollidedFloor = True
							Exit
						EndIf
					Next
					
					If CollidedFloor
						n\DropSpeed = 0.0
					Else
						If ShouldEntitiesFall
							Local UpdateGravity% = False
							Local MaxX#, MinX#, MaxZ#, MinZ#
							
							If n\InFacility = NullFloor
								If PlayerRoom\RoomTemplate\RoomID <> r_cont1_173_intro
									If forest_event <> Null
										If PlayerRoom = forest_event\room
											If forest_event\EventState = 1.0 Then UpdateGravity = True
										EndIf
									EndIf
								Else
									UpdateGravity = True
								EndIf
								If (Not UpdateGravity)
									For r.Rooms = Each Rooms
										If r\MaxX <> 0.0 Lor r\MinX <> 0.0 Lor r\MaxZ <> 0.0 Lor r\MinZ <> 0.0
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
										If Abs(EntityX(n\Collider) - EntityX(r\OBJ)) <= Abs(MaxX - MinX)
											If Abs(EntityZ(n\Collider) - EntityZ(r\OBJ)) <= Abs(MaxZ - MinZ)
												If r = PlayerRoom
													UpdateGravity = True
													Exit
												EndIf
												If IsRoomAdjacent(PlayerRoom, r)
													UpdateGravity = True
													Exit
												EndIf
												For i = 0 To MaxRoomAdjacents - 1
													If IsRoomAdjacent(PlayerRoom\Adjacent[i], r)
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
								n\DropSpeed = Max(n\DropSpeed - 0.005 * fps\Factor[0] * n\GravityMult, -n\MaxGravity)
							Else
								If n\FallingPickDistance > 0.0
									n\DropSpeed = 0.0
								Else
									n\DropSpeed = Max(n\DropSpeed - 0.005 * fps\Factor[0] * n\GravityMult, -n\MaxGravity)
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
		EndIf
		CatchErrors("Uncaught: UpdateNPCs(NPC Name: " + Chr(34) + n\NVGName + Chr(34) + ", ID: " + n\NPCType + ")")
	Next
	
	UpdateCameraCheck()
End Function

; ~ MTF AI constants
;[Block]
Const MTF_WANDERING_AROUND% = 0
Const MTF_SEARCHING_PLAYER% = 1
Const MTF_173_SPOTTED% = 2
Const MTF_FOLLOW_PATH% = 3
Const MTF_049_066_106_SPOTTED% = 4
Const MTF_LOOKING_AT_SOME_TARGET% = 5
Const MTF_SHOOTING_AT_PLAYER% = 6
Const MTF_096_SPOTTED% = 8
Const MTF_ZOMBIES_SPOTTED% = 9
Const MTF_DISABLING_TESLA% = 11
;[End Block]

Function UpdateMTFUnit%(n.NPCs)
	Local r.Rooms, p.Particles, n2.NPCs, w.WayPoints, de.Decals, e.Events
	
	If n\IsDead
		AnimateNPC(n, 1050.0, 1174.0, 0.7, False)
		If n = n_I\MTFLeader Then n_I\MTFLeader = Null
	Else
		If n_I\MTFLeader = Null Then n_I\MTFLeader = n
		
		n\MaxGravity = 0.03
		
		UpdateNPCBlinking(n)
		
		n\Reload = Max(n\Reload - fps\Factor[0], 0.0)
		
		If Int(n\State) <> MTF_SEARCHING_PLAYER And Int(n\State) <> MTF_DISABLING_TESLA Then n\PrevState = 0
		
		n\SoundCHN2 = LoopSound2(NPCSound[SOUND_NPC_MTF_BREATH], n\SoundCHN2, Camera, n\Collider, 10.0, 1.0, True) ; ~ Breath channel
		
		Local Dist#, FoundChamber%, Pvt%
		Local PrevFrame# = n\Frame
		Local x# = 0.0, y# = 0.0, z# = 0.0
		Local SqrValue#, PlayerSeeAble%
		Local FPSFactorEx# = fps\Factor[0] * 2.0
		
		Select Int(n\State) ; ~ What is this MTF doing
			Case MTF_WANDERING_AROUND
				;[Block]
				n\Speed = 0.015
				; ~ Set a timer to step back
				If n <> n_I\MTFLeader
					Dist = EntityDistanceSquared(n\Collider, n_I\MTFLeader\Collider)
					If Dist < 0.64
						n\State3 = 70.0
						For n2.NPCs = Each NPCs
							If n2\NPCType = NPCTypeMTF And n2 <> n
								If EntityDistanceSquared(n2\Collider, n\Collider) < 0.64 And n2\State = MTF_WANDERING_AROUND Then n2\State3 = 70.0
							EndIf
						Next
					EndIf
					If n\State3 > 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\PathLocation = 0
						n\PathTimer = 1.0
						n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
					EndIf
				EndIf
				
				If n\PathTimer <= 0.0 ; ~ Update path
					If n <> n_I\MTFLeader ; ~ I'll follow the leader
						n\PathStatus = FindPath(n, EntityX(n_I\MTFLeader\Collider, True), EntityY(n_I\MTFLeader\Collider, True) + 0.1, EntityZ(n_I\MTFLeader\Collider, True)) ; ~ Whatever you say boss
					Else ; ~ I am the leader
						If n_I\Curr173\Idle = 2
							For r.Rooms = Each Rooms
								If r\RoomTemplate\RoomID = r_cont1_173
									FoundChamber = False
									Pvt = CreatePivot()
									
									PositionEntity(Pvt, r\x + 4736.0 * RoomScale, r\y + 420.0 * RoomScale, r\x + 3774.0 * RoomScale, True)
									
									If DistanceSquared(EntityX(Pvt), EntityX(n\Collider), EntityZ(Pvt), EntityZ(n\Collider)) < 12.25 Then FoundChamber = True
									
									FreeEntity(Pvt) : Pvt = 0
									
									If DistanceSquared(EntityX(n\Collider), r\x + 4736.0 * RoomScale, EntityZ(n\Collider), r\z + 3774.0 * RoomScale) > 2.56 And (Not FoundChamber)
										x = r\x + 4736.0 * RoomScale
										y = r\y + 420.0 * RoomScale
										z = r\z + 3774.0 * RoomScale
										Exit
									ElseIf DistanceSquared(EntityX(n\Collider), r\x + 4736.0 * RoomScale, EntityZ(n\Collider), r\z + 3774.0 * RoomScale) > 2.56 And FoundChamber
										n\EnemyX = r\x + 4736.0 * RoomScale
										n\EnemyY = r\y + 420.0 * RoomScale
										n\EnemyZ = r\z + 3774.0 * RoomScale
										Exit
									Else
										LoadNPCSound(n, "SFX\Character\MTF\173\Cont" + Rand(0, 3) + ".ogg")
										PlayMTFSound(n\Sound, n)
										PlayAnnouncement("SFX\Character\MTF\Announc173Contain.ogg")
										If r\RoomDoors[0]\Open Then OpenCloseDoor(r\RoomDoors[0])
										n_I\Curr173\Idle = 3
										Exit
									EndIf
								EndIf
							Next
;						ElseIf (Not n_I\Curr106\Contained) And n_I\Curr173\Idle = 3 And PlayerRoom\RoomTemplate\RoomID <> r_cont1_106
;							For r.Rooms = Each Rooms
;								If r\RoomTemplate\RoomID = r_cont1_106
;									If n\IdleTimer = 0.0
;										FoundChamber = False
;										Pvt = CreatePivot()
;										
;										PositionEntity(Pvt, EntityX(r\RoomDoors[0]\FrameOBJ), r\y + 0.1, EntityZ(r\RoomDoors[0]\FrameOBJ), True)
;										
;										If DistanceSquared(EntityX(Pvt), EntityX(n\Collider), EntityZ(Pvt), EntityZ(n\Collider)) < 12.25 Then FoundChamber = True
;										
;										FreeEntity(Pvt) : Pvt = 0
;										
;										If DistanceSquared(EntityX(n\Collider), EntityX(r\RoomDoors[0]\FrameOBJ), EntityZ(n\Collider), EntityZ(r\RoomDoors[0]\FrameOBJ)) > 2.56 And (Not FoundChamber)
;											x = EntityX(r\RoomDoors[0]\FrameOBJ)
;											y = 0.1
;											z = EntityZ(r\RoomDoors[0]\FrameOBJ)
;											Exit
;										ElseIf DistanceSquared(EntityX(n\Collider), EntityX(r\RoomDoors[0]\FrameOBJ), EntityZ(n\Collider), EntityZ(r\RoomDoors[0]\FrameOBJ)) > 2.56 And FoundChamber
;											n\EnemyX = EntityX(r\RoomDoors[0]\FrameOBJ)
;											n\EnemyY = 0.1
;											n\EnemyZ = EntityZ(r\RoomDoors[0]\FrameOBJ)
;											Exit
;										Else
;											LoadNPCSound(n, "SFX\Character\MTF\106\FoundChamber.ogg")
;											PlayMTFSound(n\Sound, n)
;											PositionEntity(n\Collider, 0.0, -500.0, 0.0, True)
;											ResetEntity(n\Collider)
;											n\IdleTimer = 3.0
;											Exit
;										EndIf
;									Else
;										If n\IdleTimer > 0.0
;											If n\IdleTimer = 2.0
;												LoadNPCSound(n, "SFX\Character\MTF\106\Protocol.ogg")
;												PlayMTFSound(n\Sound, n)
;											EndIf
;											n\IdleTimer = n\IdleTimer - 1.0
;											Exit
;										Else
;											LoadNPCSound(n, "SFX\Character\MTF\106\Cont.ogg")
;											PlayMTFSound(n\Sound, n)
;											PlayAnnouncement("SFX\Character\MTF\Announc106Contain.ogg")
;											
;											UpdateLever(r\RoomLevers[0]\OBJ)
;											RotateEntity(r\RoomLevers[0]\OBJ, -80.0, EntityYaw(r\RoomLevers[0]\OBJ), 0.0)
;											
;											de.Decals = CreateDecal(DECAL_CORROSIVE_1, EntityX(r\RoomSecurityCams[0]\CameraOBJ, True), EntityY(r\RoomSecurityCams[0]\CameraOBJ, True), EntityZ(r\RoomSecurityCams[0]\CameraOBJ, True), 0.0, 0.0, 0.0, 0.05, 0.01) 
;											de\Timer = 90000.0 : de\AlphaChange = 0.005 : de\SizeChange = 0.002
;											RotateEntity(de\OBJ, EntityPitch(r\RoomSecurityCams[0]\CameraOBJ, True) + Rnd(10.0, 20.0), EntityYaw(r\RoomSecurityCams[0]\CameraOBJ, True) + 30.0, EntityRoll(de\OBJ))
;											MoveEntity(de\OBJ, 0.0, 0.05, 0.2) 
;											RotateEntity(de\OBJ, EntityPitch(r\RoomSecurityCams[0]\CameraOBJ, True), EntityYaw(r\RoomSecurityCams[0]\CameraOBJ, True), EntityRoll(de\OBJ))
;											EntityParent(de\OBJ, r\RoomSecurityCams[0]\CameraOBJ)
;											
;											For e.Events = Each Events
;												If r = e\room
;													e\EventState = 1.0
;													e\EventState2 = 1.0
;													e\EventState3 = 4000.0
;													Exit
;												EndIf
;											Next
;											PositionEntity(n\Collider, EntityX(r\RoomCenter, True), r\y + 0.3, EntityZ(r\RoomCenter, True), True)
;											ResetEntity(n\Collider)
;											n_I\Curr106\Contained = True
;											Exit
;										EndIf
;									EndIf
;								EndIf
;							Next
						Else
							For r.Rooms = Each Rooms
								If ((Abs(r\x - EntityX(n\Collider, True)) > 12.0) Lor (Abs(r\z - EntityZ(n\Collider, True)) > 12.0)) And (Rand(Max(4 - Int(Abs(r\z - EntityZ(n\Collider, True) / 8.0)), 2.0)) = 1)
									x = r\x
									y = 0.1
									z = r\z
									Exit
								EndIf
							Next
						EndIf
						If n\EnemyX = 0.0 And n\EnemyZ = 0.0
							n\PathStatus = FindPath(n, x, y, z) ; ~ We're going to this room for no particular reason
						Else
							n\State = MTF_FOLLOW_PATH
						EndIf
					EndIf
					If n\PathStatus = PATH_STATUS_FOUND
						While n\Path[n\PathLocation] = Null
							If n\PathLocation > MaxPathLocations - 1
								n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Exit
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Wend
						If n\PathLocation < MaxPathLocations - 1
							If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
								If n\Path[n\PathLocation]\door = Null
									If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
								EndIf
							EndIf
						EndIf
						UseDoorNPC(n)
					EndIf
					n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
				ElseIf n\PathTimer <= 70.0 * 2.5 And n = n_I\MTFLeader
					n\CurrSpeed = 0.0
					If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
					FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
					n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
					n\PathTimer = n\PathTimer - fps\Factor[0]  ; ~ Timer goes down slow
				Else
					If n\PathStatus = PATH_STATUS_NOT_FOUND
						n\CurrSpeed = 0.0
						If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
						FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
						n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
					ElseIf n\PathStatus = PATH_STATUS_FOUND
						If n\Path[n\PathLocation] = Null
							If n\PathLocation > MaxPathLocations - 1
								n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
							Else
								n\PathLocation = n\PathLocation + 1
							EndIf
						Else
							PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
							RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
							TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
							AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
							
							UseDoorNPC(n, True, True)
						EndIf
						n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
					Else
						If n = n_I\MTFLeader
							n\CurrSpeed = 0.0
							If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
							FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
						Else
							If Dist >= 1.0 And n\State3 =< 0.0
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								PointEntity(n\Collider, n_I\MTFLeader\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
							ElseIf n\State3 =< 0.0
								n\CurrSpeed = 0.0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
							Else
								n\CurrSpeed = CurveValue(-n\Speed, n\CurrSpeed, 20.0)
								PointEntity(n\Collider, n_I\MTFLeader\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 522.0, 488.0, n\CurrSpeed * 26.0)
							EndIf
						EndIf
						n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
					EndIf
					n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
				EndIf
				
				If n\Target = Null
					PlayerSeeAble = NPCSeesPlayer(n, 4.0 - me\CrouchState + me\SndVolume)
					If PlayerSeeAble > 0
						If n\LastSeen > 0 And n\LastSeen < 70.0 * 15.0
							If PlayerSeeAble < 2
								LoadNPCSound(n, "SFX\Character\MTF\ThereHeIs" + Rand(0, 5) + ".ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						Else
							If PlayerSeeAble = 1
								LoadNPCSound(n, "SFX\Character\MTF\Stop" + Rand(0, 5) + ".ogg")
								PlayMTFSound(n\Sound, n)
							ElseIf PlayerSeeAble = 2
								LoadNPCSound(n, "SFX\Character\MTF\ClassD" + Rand(0, 3) + ".ogg")
								PlayMTFSound(n\Sound, n)
							EndIf
						EndIf
						
						n\EnemyX = EntityX(me\Collider, True)
						n\EnemyY = EntityY(me\Collider, True)
						n\EnemyZ = EntityZ(me\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\LastSeen = 70.0 * Rnd(30.0, 40.0)
						n\Reload = 70.0 * (3.0 - SelectedDifficulty\AggressiveNPCs)
						n\State2 = 70.0 * (15.0 * PlayerSeeAble) ; ~ Give up after 15 seconds (30 seconds if detected by loud noise, over camera: 45)
						n\State = MTF_SEARCHING_PLAYER
					EndIf
				EndIf
				
				; ~ B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
				If n_I\Curr173\Idle < 2
					If NPCSeesNPC(n_I\Curr173, n) > 0
						If n = n_I\MTFLeader
							If n\State2 > 0.0
								LoadNPCSound(n, "SFX\Character\MTF\173\Spotted3.ogg")
							Else
								LoadNPCSound(n, "SFX\Character\MTF\173\Spotted" + Rand(0, 1) + ".ogg")
							EndIf
							PlayMTFSound(n\Sound, n)
						EndIf
						
						For n2.NPCs = Each NPCs
							If n2\NPCType = NPCTypeMTF
								n2\EnemyX = EntityX(n_I\Curr173\Collider, True)
								n2\EnemyY = EntityY(n_I\Curr173\Collider, True)
								n2\EnemyZ = EntityZ(n_I\Curr173\Collider, True)
								n2\PathTimer = 0.0
								n2\PathStatus = PATH_STATUS_NO_SEARCH
								n2\Target = n_I\Curr173
								n2\State2 = 70.0 * 30.0 ; ~ Give up after 30 seconds
								n2\State3 = 0.0
								n2\State = MTF_173_SPOTTED
							EndIf
						Next
					EndIf
				EndIf
				
				If n_I\Curr106\State <= 0.0
					If NPCSeesNPC(n_I\Curr106, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\106\Spotted" + Rand(0, 2) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						
						n\EnemyX = EntityX(n_I\Curr106\Collider, True)
						n\EnemyY = EntityY(n_I\Curr106\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr106\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr106
						n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr096 <> Null
					If NPCSeesNPC(n_I\Curr096, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\096\Spotted" + Rand(0, 1) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						PlaySound2(snd_I\NVGSFX[0], Camera, n\Collider, 5.0)
						
						n\EnemyX = EntityX(n_I\Curr096\Collider, True)
						n\EnemyY = EntityY(n_I\Curr096\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr096\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr096
						n\State2 = 70.0 * 10.0 ; ~ Give up after 10 seconds
						n\State3 = 0.0
						n\State = MTF_096_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr049 <> Null
					If NPCSeesNPC(n_I\Curr049, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\049\Spotted" + Rand(0, 4) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						
						n\EnemyX = EntityX(n_I\Curr049\Collider, True)
						n\EnemyY = EntityY(n_I\Curr049\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr049\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr049
						n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr066 <> Null
					If NPCSeesNPC(n_I\Curr066, n) = 1
						n\EnemyX = EntityX(n_I\Curr066\Collider, True)
						n\EnemyY = EntityY(n_I\Curr066\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr066\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr066
						n\State2 = 70.0 * 10.0 ; ~ Give up after 10 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If (Not n2\IsDead)
						If n2\NPCType = NPCType049_2
							If NPCSeesNPC(n2, n) = 1
								If n = n_I\MTFLeader
									LoadNPCSound(n, "SFX\Character\MTF\049_2\Spotted.ogg")
									PlayMTFSound(n\Sound, n)
								EndIf
								
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 4.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType008_1
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType035_Tentacle
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType1048_A
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						EndIf
					EndIf
				Next
				;[End Block]
			Case MTF_SEARCHING_PLAYER
				;[Block]
				n\Speed = 0.015
				n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
				If n\State2 > 0.0
					PlayerSeeAble = NPCSeesPlayer(n, 4.0 - me\CrouchState + me\SndVolume)
					If PlayerSeeAble > 0 Then n\State2 = 70.0 * 15.0
					If PlayerSeeAble = 1
						n\EnemyX = EntityX(me\Collider, True)
						n\EnemyY = EntityY(me\Collider, True)
						n\EnemyZ = EntityZ(me\Collider, True)
						
						PointEntity(n\Collider, me\Collider)
						
						If n\Reload <= 0.0 And (Not me\Terminated)
							Local PrevTerminated# = me\Terminated
							
							PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 15.0)
							
							Pvt = CreatePivot()
							RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
							PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
							
							SqrValue = Sqr(Dist)
							
							Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ((25.0 / SqrValue) * (1.0 / SqrValue)), True)
							
							FreeEntity(Pvt) : Pvt = 0
							
							msg\DeathMsg = Format(GetLocalString("death", "ntf.blood"), SubjectName)
							
							If (Not PrevTerminated) And me\Terminated
								msg\DeathMsg = Format(GetLocalString("death", "ntf.terminated"), SubjectName)
								PlayMTFSound(LoadTempSound("SFX\Character\MTF\TargetTerminated" + Rand(0, 3) + ".ogg"), n)
							EndIf
							n\Reload = 8.0
						EndIf
						
						Dist = EntityDistanceSquared(me\Collider, n\Collider)
						; ~ If close enough, start shooting at the player
						If Dist < 9.0 + ((PlayerRoom\RoomTemplate\RoomID = r_gate_a) * 16.0) 
							For n2.NPCs = Each NPCs
								If n2\NPCType = NPCTypeMTF And n2 <> n
									If n2\State = MTF_WANDERING_AROUND
										If EntityDistanceSquared(n\Collider, n2\Collider) < 36.0
											n\PrevState = 1
											n2\LastSeen = 70.0 * Rnd(30.0, 40.0)
											n2\EnemyX = EntityX(me\Collider, True)
											n2\EnemyY = EntityY(me\Collider, True)
											n2\EnemyZ = EntityZ(me\Collider, True)
											n2\State2 = n\State2
											n2\PathTimer = 0.0
											n2\PathStatus = PATH_STATUS_NO_SEARCH
											n2\Reload = 70.0 * (4.0 - SelectedDifficulty\AggressiveNPCs)
											n2\PrevState = 0
											n2\State = MTF_SEARCHING_PLAYER
										EndIf
									EndIf
								EndIf
							Next
							
							If n\PrevState = 1
								SetNPCFrame(n, 423.0)
								n\PrevState = 2
							ElseIf n\PrevState = 2
								n\CurrSpeed = 0.0
								If n\Frame > 200.0
									AnimateNPC(n, 424.0, 463.0, 0.5, False)
									If n\Frame > 462.9 Then n\Frame = 78.0
								Else
									AnimateNPC(n, 78.0, 193.0, 0.2, False)
								EndIf
							EndIf
						Else
							PositionEntity(n\OBJ, n\EnemyX, n\EnemyY, n\EnemyZ, True)
							PointEntity(n\Collider, n\OBJ)
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
							TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
							AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
						EndIf
						RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 10.0)
					Else
						n\LastSeen = n\LastSeen - fps\Factor[0]
						
						If n\Reload =< 8.0 Then n\Reload = 8.0
						
						If n\PathTimer <= 0.0 ; ~ Update path
							n\PathStatus = FindPath(n, n\EnemyX, n\EnemyY + 0.1, n\EnemyZ)
							n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
							If n = n_I\MTFLeader
								If Rand(10) = 1
									For n2.NPCs = Each NPCs
										If n2\NPCType = NPCTypeMTF And n2 <> n
											If EntityDistanceSquared(n\Collider, n2\Collider) < 36.0 Then n\PrevState = 3
										EndIf
									Next
								EndIf
							EndIf
						ElseIf n\PathTimer <= 70.0 * 2.5
							n\CurrSpeed = 0.0
							If n\PrevState = 0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
							ElseIf n\PrevState = 3
								SetNPCFrame(n, 350.0)
								n\PrevState = 4
							ElseIf n\PrevState = 4
								AnimateNPC(n, 350.0, 423.0, 0.5, False)
								If n\Frame > 422.9 Then n\PrevState = 0
							EndIf
							n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
						Else
							If n\PathStatus = PATH_STATUS_NOT_FOUND
								n\CurrSpeed = 0.0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
							ElseIf n\PathStatus = PATH_STATUS_FOUND
								If n\Path[n\PathLocation] = Null
									If n\PathLocation > MaxPathLocations - 1
										n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
									
									UseDoorNPC(n, True, True)
								EndIf
								n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
							Else
								PositionEntity(n\OBJ, n\EnemyX, n\EnemyY, n\EnemyZ, True)
								If DistanceSquared(EntityX(n\Collider, True), n\EnemyX, EntityZ(n\Collider, True), n\EnemyZ) < PathLocationDist Lor (Not EntityVisible(n\OBJ, n\Collider))
									If Rand(35) = 1 Then
										RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
										
										For w.WayPoints = Each WayPoints
											If Rand(3) = 1
												If EntityDistanceSquared(w\OBJ, n\Collider) < 36.0
													n\EnemyX = EntityX(w\OBJ, True)
													n\EnemyY = EntityY(w\OBJ, True)
													n\EnemyZ = EntityZ(w\OBJ, True)
													n\PathTimer = 0.0
													Exit
												EndIf
											EndIf
										Next
									EndIf
									FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
									n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
								Else
									PointEntity(n\Collider, n\OBJ)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
								EndIf
							EndIf
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
						EndIf
						
						If n = n_I\MTFLeader And n\LastSeen < 70.0 * 30.0 And n\LastSeen + fps\Factor[0] >= 70.0 * 30.0
							If Rand(2) = 1 Then PlayMTFSound(LoadTempSound("SFX\Character\MTF\Searching" + Rand(0, 5) + ".ogg"), n)
						EndIf
					EndIf
				Else
					If n = n_I\MTFLeader
						PlayMTFSound(LoadTempSound("SFX\Character\MTF\TargetLost" + Rand(0, 2) + ".ogg"), n)
						If MTFCameraCheckTimer = 0.0
							If Rand(15 - (7 * SelectedDifficulty\AggressiveNPCs)) = 1 ; ~ Maybe change this to another chance -- ENDSHN
								PlayAnnouncement("SFX\Character\MTF\AnnouncCameraCheck.ogg")
								MTFCameraCheckTimer = fps\Factor[0]
							EndIf
						EndIf
					EndIf
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State = MTF_WANDERING_AROUND
				EndIf
				
				; ~ B3D doesn't do short-circuit evaluation, so this retarded nesting is an optimization
				If n_I\Curr173\Idle < 2
					If NPCSeesNPC(n_I\Curr173, n) > 0
						If n = n_I\MTFLeader
							If n\State2 > 0.0
								LoadNPCSound(n, "SFX\Character\MTF\173\Spotted3.ogg")
							Else
								LoadNPCSound(n, "SFX\Character\MTF\173\Spotted" + Rand(0, 1) + ".ogg")
							EndIf
							PlayMTFSound(n\Sound, n)
						EndIf
						
						For n2.NPCs = Each NPCs
							If n2\NPCType = NPCTypeMTF
								n2\EnemyX = EntityX(n_I\Curr173\Collider, True)
								n2\EnemyY = EntityY(n_I\Curr173\Collider, True)
								n2\EnemyZ = EntityZ(n_I\Curr173\Collider, True)
								n2\PathTimer = 0.0
								n2\PathStatus = PATH_STATUS_NO_SEARCH
								n2\Target = n_I\Curr173
								n2\State2 = 70.0 * 30.0 ; ~ Give up after 30 seconds
								n2\State3 = 0.0
								n2\State = MTF_173_SPOTTED
							EndIf
						Next
					EndIf
				EndIf
				
				If n_I\Curr106\State <= 0.0
					If NPCSeesNPC(n_I\Curr106, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\106\Spotted" + Rand(0, 2) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						
						n\EnemyX = EntityX(n_I\Curr106\Collider, True)
						n\EnemyY = EntityY(n_I\Curr106\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr106\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr106
						n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr096 <> Null
					If NPCSeesNPC(n_I\Curr096, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\096\Spotted" + Rand(0, 1) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						PlaySound2(snd_I\NVGSFX[0], Camera, n\Collider, 5.0)
						
						n\EnemyX = EntityX(n_I\Curr096\Collider, True)
						n\EnemyY = EntityY(n_I\Curr096\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr096\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr096
						n\State2 = 70.0 * 10.0 ; ~ Give up after 10 seconds
						n\State3 = 0.0
						n\State = MTF_096_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr049 <> Null
					If NPCSeesNPC(n_I\Curr049, n) = 1
						If n = n_I\MTFLeader
							LoadNPCSound(n, "SFX\Character\MTF\049\Spotted" + Rand(0, 4) + ".ogg")
							PlayMTFSound(n\Sound, n)
						EndIf
						
						n\EnemyX = EntityX(n_I\Curr049\Collider, True)
						n\EnemyY = EntityY(n_I\Curr049\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr049\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr049
						n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				If n_I\Curr066 <> Null
					If NPCSeesNPC(n_I\Curr066, n) = 1
						n\EnemyX = EntityX(n_I\Curr066\Collider, True)
						n\EnemyY = EntityY(n_I\Curr066\Collider, True)
						n\EnemyZ = EntityZ(n_I\Curr066\Collider, True)
						n\PathTimer = 0.0
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\Target = n_I\Curr066
						n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
						n\State3 = 0.0
						n\State = MTF_049_066_106_SPOTTED
					EndIf
				EndIf
				
				For n2.NPCs = Each NPCs
					If (Not n2\IsDead)
						If n2\NPCType = NPCType049_2
							If NPCSeesNPC(n2, n) = 1
								LoadNPCSound(n, "SFX\Character\MTF\049_2\Spotted.ogg")
								PlayMTFSound(n\Sound, n)
								
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 4.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType008_1
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType035_Tentacle
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						ElseIf n2\NPCType = NPCType1048_A And (Not n2\IsDead)
							If NPCSeesNPC(n2, n) = 1
								n\EnemyX = EntityX(n2\Collider, True)
								n\EnemyY = EntityY(n2\Collider, True)
								n\EnemyZ = EntityZ(n2\Collider, True)
								n\PathTimer = 0.0
								n\PathStatus = PATH_STATUS_NO_SEARCH
								n\Target = n2
								n\Reload = 70.0 * 3.0
								n\State2 = 70.0 * 15.0 ; ~ Give up after 15 seconds
								n\State3 = 0.0
								n\State = MTF_ZOMBIES_SPOTTED
								Exit
							EndIf
						EndIf
					EndIf
				Next
				;[End Block]
			Case MTF_FOLLOW_PATH
				;[Block]
				Pvt = CreatePivot()
				PositionEntity(Pvt, n\EnemyX, n\EnemyY, n\EnemyZ, True)
				
				If DistanceSquared(EntityX(n\Collider), EntityX(Pvt), EntityX(n\Collider), EntityX(Pvt)) < 0.25
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					FreeEntity(Pvt) : Pvt = 0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				FreeEntity(Pvt) : Pvt = 0
				
				If n\PathStatus = PATH_STATUS_FOUND
					If n\Path[n\PathLocation] = Null
						If n\PathLocation > MaxPathLocations - 1
							n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
						Else
							n\PathLocation = n\PathLocation + 1
						EndIf
					Else
						PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
						RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
						TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
						AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
						
						UseDoorNPC(n, True, True)
					EndIf
				Else
					If n\PathTimer = 0.0 Then n\PathStatus = FindPath(n, n\EnemyX, n\EnemyY + 0.2, n\EnemyZ)
					
					Local ClosestWaypoint.WayPoints = Null
					
					Pvt = CreatePivot()
					PositionEntity(Pvt, n\EnemyX, n\EnemyY, n\EnemyZ)
					For w.WayPoints = Each WayPoints
						If EntityDistanceSquared(w\OBJ, Pvt) < 4.0
							ClosestWaypoint = w
							Exit
						EndIf
					Next
					FreeEntity(Pvt) : Pvt = 0
					
					If ClosestWaypoint <> Null
						n\PathTimer = 1.0
						If EntityVisible(ClosestWaypoint\OBJ, n\Collider)
							If Abs(DeltaYaw(n\Collider, ClosestWaypoint\OBJ)) > 0.0
								PointEntity(n\OBJ, ClosestWaypoint\OBJ)
								RotateEntity(n\Collider, 0.0, CurveAngle(EntityYaw(n\OBJ), EntityYaw(n\Collider), 20.0), 0.0)
							EndIf
						EndIf
					Else
						n\PathTimer = 0.0
					EndIf
					
					If n\PathTimer = 1.0
						AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
						TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
					EndIf
				EndIf
				n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
				;[End Block]
			Case MTF_LOOKING_AT_SOME_TARGET
				;[Block]
				Pvt = CreatePivot()
				PositionEntity(Pvt, n\EnemyX, n\EnemyY, n\EnemyZ, True)
				PointEntity(n\Collider, Pvt)
				FreeEntity(Pvt) : Pvt = 0
				RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
				n\CurrSpeed = 0.0
				AnimateNPC(n, 78.0, 194.0, 0.2)
				n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
				;[End Block]
			Case MTF_SHOOTING_AT_PLAYER
				;[Block]
				PointEntity(n\Collider, me\Collider)
				RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
				n\CurrSpeed = 0.0
				AnimateNPC(n, 346.0, 351.0, 0.2)
				n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 10.0)
				
				Dist = EntityDistanceSquared(me\Collider, n\Collider)
				If n\Reload <= 0.0 And (Not me\Terminated)
					PrevTerminated = me\Terminated
					
					PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 15.0)
					
					Pvt = CreatePivot()
					
					RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
					PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
					MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
					
					SqrValue = Sqr(Dist)
					
					Shoot(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), ((25.0 / SqrValue) * (1.0 / SqrValue)), True)
					
					FreeEntity(Pvt) : Pvt = 0
					
					msg\DeathMsg = Format(GetLocalString("death", "ntf.blood"), SubjectName)
					
					If (Not PrevTerminated) And me\Terminated
						If PlayerRoom\RoomTemplate\RoomID = r_cont2_049
							msg\DeathMsg = GetLocalString("death", "0492")
							PlayMTFSound(LoadTempSound("SFX\Character\MTF\049_2\TargetTerminated.ogg"), n)
						Else
							msg\DeathMsg = Format(GetLocalString("death", "ntf.gatea"), SubjectName)
							PlayMTFSound(LoadTempSound("SFX\Character\MTF\Targetterminated" + Rand(0, 3) + ".ogg"), n)
						EndIf
					EndIf
					n\Reload = 8.0
				EndIf
				;[End Block]
			Case MTF_DISABLING_TESLA
				;[Block]
				For n2.NPCs = Each NPCs
					If n2\NPCType = NPCTypeMTF And n2 <> n
						If EntityDistanceSquared(n\Collider, n2\Collider) < 4.0 And n2\State <> MTF_LOOKING_AT_SOME_TARGET
							n2\PrevState = 0
							n2\PathTimer = 0.0
							n2\PathLocation = 0
							n2\PathStatus = PATH_STATUS_NO_SEARCH
							n2\EnemyX = EntityX(n\Collider)
							n2\EnemyY = EntityY(n\Collider)
							n2\EnemyZ = EntityZ(n\Collider)
							n2\State = MTF_LOOKING_AT_SOME_TARGET
						EndIf
					EndIf
				Next
				
				If n\PrevState = 1
					SetNPCFrame(n, 423.0)
					n\PrevState = 2
				ElseIf n\PrevState = 2
					n\CurrSpeed = 0.0
					If n\Frame < 1175.0
						AnimateNPC(n, 423.0, 463.0, 0.5, False)
						If n\Frame > 462.9
							LoadNPCSound(n, "SFX\Character\MTF\TeslaRequest.ogg")
							PlayMTFSound(n\Sound, n)
							SetNPCFrame(n, 1175.0)
						EndIf
					Else
						AnimateNPC(n, 1175.0, 1290.0, 0.18, False)
					EndIf
				EndIf
				
				n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
				If n\State3 =< 0.0 Lor n\IsDead
					For n2.NPCs = Each NPCs
						If n2\NPCType = NPCTypeMTF And n2 <> n
							If EntityDistanceSquared(n\Collider, n2\Collider) < 6.0
								n2\EnemyX = 0.0 : n2\EnemyY = 0.0 : n2\EnemyZ = 0.0
								n2\State = MTF_WANDERING_AROUND
							EndIf
						EndIf
					Next
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				;[End Block]
			Case MTF_173_SPOTTED
				;[Block]
				n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
				If n\BlinkTimer <= 0.0
					If NPCSound[SOUND_NPC_MTF_BLINKING] = 0 Then NPCSound[SOUND_NPC_MTF_BLINKING] = LoadSound_Strict("SFX\Character\MTF\173\BLINKING.ogg")
					PlayMTFSound(NPCSound[SOUND_NPC_MTF_BLINKING], n)
				EndIf
				If NPCSeesNPC(n\Target, n) = 1
					n\EnemyX = EntityX(n\Target\Collider, True)
					n\EnemyY = EntityY(n\Target\Collider, True)
					n\EnemyZ = EntityZ(n\Target\Collider, True)
					
					n\State2 = 70.0 * 30.0
					n\PathLocation = 0
					n\PathTimer = 0.0
					n\PathStatus = PATH_STATUS_NO_SEARCH
					
					If n\Target\Idle <> 2 Then n\Target\Idle = 1
					
					Local Curr173Dist# = DistanceSquared(EntityX(n\Collider, True), EntityX(n\Target\Collider, True), EntityZ(n\Collider, True), EntityZ(n\Target\Collider, True))
					
					If Curr173Dist < 25.0
						Local TempDist# = 1.0
						
						If n <> n_I\MTFLeader Then TempDist = 4.0
						
						PointEntity(n\Collider, n\Target\Collider)
						
						If Curr173Dist < TempDist
							n\State3 = n\State3 + fps\Factor[0]
							If n\State3 >= 70.0 * 15.0
								LoadNPCSound(n_I\MTFLeader, "SFX\Character\MTF\173\Box" + Rand(0, 2) + ".ogg")
								PlayMTFSound(n_I\MTFLeader\Sound, n_I\MTFLeader)
								; ~ Always attach to leader
								n\Target\Idle = 2
							EndIf
							
							n\CurrSpeed = 0.0
							FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
						Else
							n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
							TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
							AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
						EndIf
					Else
						PositionEntity(n\OBJ, n\EnemyX, n\EnemyY, n\EnemyZ, True)
						PointEntity(n\Collider, n\OBJ)
						n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
						TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
						AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
					EndIf
					RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
					n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
				Else
					If n\PathTimer <= 0.0 ; ~ Update path
						n\PathStatus = FindPath(n, n\EnemyX, n\EnemyY + 0.1, n\EnemyZ)
						If n\PathStatus = PATH_STATUS_FOUND
							While n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
									Exit
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Wend
							If n\PathLocation < MaxPathLocations - 1
								If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
									If n\Path[n\PathLocation]\door = Null
										If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
									EndIf
								EndIf
							EndIf
							UseDoorNPC(n)
						EndIf
						n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
					ElseIf n\PathTimer <= 70.0 * 2.5
						n\PathTimer = n\PathTimer - fps\Factor[0]
						n\CurrSpeed = 0.0
						If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
						FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
					Else
						If n\PathStatus = PATH_STATUS_FOUND
							If n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
								
								UseDoorNPC(n, True, True)
							EndIf
							n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
						Else
							PositionEntity(n\OBJ, n\EnemyX, n\EnemyY, n\EnemyZ, True)
							If DistanceSquared(EntityX(n\Collider, True), n\EnemyX, EntityZ(n\Collider, True), n\EnemyZ) < 0.25 Lor (Not EntityVisible(n\OBJ, n\Collider))
								If Rand(35) = 1 Then
									RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
									
									For w.WayPoints = Each WayPoints
										If Rand(3) = 1
											If EntityDistanceSquared(w\OBJ, n\Collider) < 36.0
												n\EnemyX = EntityX(w\OBJ, True)
												n\EnemyY = EntityY(w\OBJ, True)
												n\EnemyZ = EntityZ(w\OBJ, True)
												n\PathTimer = 0.0
												Exit
											EndIf
										EndIf
									Next
								EndIf
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
							Else
								PointEntity(n\Collider, n\OBJ)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
							EndIf
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
					EndIf
				EndIf
				If n\State2 =< 0.0 Lor n\Target\Idle = 2
					n\Target = Null
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State2 = 0.0
					n\State3 = 0.0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				;[End Block]
			Case MTF_049_066_106_SPOTTED
				;[Block]
				n\Speed = 0.03
				n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
				If n\State2 > 0.0
					Dist = EntityDistanceSquared(n\Collider, n\Target\Collider)
					If NPCSeesNPC(n\Target, n) = 1 Then n\State2 = 70.0 * 15.0
					If n\State2 > 70.0 And Dist > PowTwo(HideDistance) Then n\State2 = 70.0
					
					; ~ Set a timer to step back
					If Dist < (9.0 - (n\Target = n_I\Curr066) * 8.0) Then n\State3 = 70.0 * 3.0
					If n\State3 > 0.0
						n\Speed = 0.02
						n\PathStatus = PATH_STATUS_NO_SEARCH
						n\PathLocation = 0
						n\PathTimer = 1.0
						
						n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
					EndIf
					
					If n\PathTimer <= 0.0 ; ~ Update path
						If n <> n_I\MTFLeader ; ~ I'll follow the leader
							n\PathStatus = FindPath(n, EntityX(n_I\MTFLeader\Collider, True), EntityY(n_I\MTFLeader\Collider, True) + 0.1, EntityZ(n_I\MTFLeader\Collider, True))
						Else ; ~ I am the leader
							For r.Rooms = Each Rooms
								If ((Abs(r\x - EntityX(n\Collider, True)) > 12.0) Lor (Abs(r\z - EntityZ(n\Collider, True)) > 12.0)) And (Rand(Max(4 - Int(Abs(r\z - EntityZ(n\Collider, True) / 8.0)), 2)) = 1)
									If EntityDistanceSquared(r\OBJ, n\Target\Collider) > 36.0
										x = r\x
										y = 0.1
										z = r\z
										Exit
									EndIf
								EndIf
							Next
							n\PathStatus = FindPath(n, x, y, z) ; ~ We're going to this room
						EndIf
						If n\PathStatus = PATH_STATUS_FOUND
							While n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
									Exit
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Wend
							If n\PathLocation < MaxPathLocations - 1
								If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
									If n\Path[n\PathLocation]\door = Null
										If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
									EndIf
								EndIf
							EndIf
							UseDoorNPC(n)
						EndIf
						n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
					Else
						If n\PathStatus = PATH_STATUS_NOT_FOUND
							n\CurrSpeed = 0.0
							If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
							FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
							n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
						ElseIf n\PathStatus = PATH_STATUS_FOUND
							If n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0) ; ~ Placeholder (until running animation has been implemented)
								
								UseDoorNPC(n, True, True)
							EndIf
							n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
						Else
							If Dist >= 1.0 And n\State3 =< 0.0
								PointEntity(n\Collider, n\Target\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
							ElseIf n\State3 =< 0.0
								n\CurrSpeed = 0.0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
							Else
								PointEntity(n\Collider, n\Target\Collider)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(-n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 522.0, 488.0, n\CurrSpeed * 26.0)
							EndIf
							n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
					EndIf
				Else
					n\Target = Null
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State2 = 0.0
					n\State3 = 0.0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				;[End Block]
			Case MTF_096_SPOTTED
				;[Block]
				n\Speed = 0.0175
				n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
				n\SoundCHN = LoopSound2(snd_I\SCRAMBLESFX, n\SoundCHN, Camera, n\Collider, 5.0)
				
				If n\State2 > 0.0
					If NPCSeesNPC(n\Target, n) = 1 Then n\State2 = 70.0 * 10.0
					; ~ Set a timer to step back
					If n <> n_I\MTFLeader
						Dist = EntityDistanceSquared(n\Collider, n_I\MTFLeader\Collider)
						If Dist < 0.64
							n\State3 = 70.0
							For n2.NPCs = Each NPCs
								If n2\NPCType = NPCTypeMTF And n2 <> n
									If EntityDistanceSquared(n2\Collider, n\Collider) < 0.64 And n2\State = MTF_096_SPOTTED Then n2\State3 = 70.0
								EndIf
							Next
						EndIf
						If n\State3 > 0.0
							n\PathStatus = PATH_STATUS_NO_SEARCH
							n\PathLocation = 0
							n\PathTimer = 1.0
							n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
						EndIf
					EndIf
					
					If n\PathTimer <= 0.0 ; ~ Update path
						If n <> n_I\MTFLeader ; ~ I'll follow the leader
							n\PathStatus = FindPath(n, EntityX(n_I\MTFLeader\Collider, True), EntityY(n_I\MTFLeader\Collider, True) + 0.1, EntityZ(n_I\MTFLeader\Collider, True)) ; ~ Whatever you say boss
						Else ; ~ I am the leader
							For r.Rooms = Each Rooms
								If ((Abs(r\x - EntityX(n\Collider, True)) > 12.0) Lor (Abs(r\z - EntityZ(n\Collider, True)) > 12.0)) And (Rand(Max(4 - Int(Abs(r\z - EntityZ(n\Collider, True) / 8.0)), 2)) = 1)
									x = r\x
									y = 0.1
									z = r\z
									Exit
								EndIf
							Next
							n\PathStatus = FindPath(n, x, y, z) ; ~ We're going to this room for no particular reason
						EndIf
						If n\PathStatus = PATH_STATUS_FOUND
							While n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
									Exit
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Wend
							If n\PathLocation < MaxPathLocations - 1
								If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
									If n\Path[n\PathLocation]\door = Null
										If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
									EndIf
								EndIf
							EndIf
							UseDoorNPC(n)
						EndIf
						n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
					ElseIf n\PathTimer <= 70.0 * 2.5 And n = n_I\MTFLeader
						n\CurrSpeed = 0.0
						FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
						n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
					Else
						If n\PathStatus = PATH_STATUS_NOT_FOUND
							n\CurrSpeed = 0.0
							FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
							n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
						ElseIf n\PathStatus = PATH_STATUS_FOUND
							If n\Path[n\PathLocation] = Null
								If n\PathLocation > MaxPathLocations - 1
									n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
								Else
									n\PathLocation = n\PathLocation + 1
								EndIf
							Else
								PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
								RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
								n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
								TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
								AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
								
								UseDoorNPC(n, True, True)
							EndIf
							n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
						Else
							If n = n_I\MTFLeader
								n\CurrSpeed = 0.0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
							Else
								If Dist >= 1.0 And n\State3 =< 0.0
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									PointEntity(n\Collider, n_I\MTFLeader\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
								ElseIf n\State3 =< 0.0
									n\CurrSpeed = 0.0
									If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
									FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								Else
									n\CurrSpeed = CurveValue(-n\Speed, n\CurrSpeed, 20.0)
									PointEntity(n\Collider, n_I\MTFLeader\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 522.0, 488.0, n\CurrSpeed * 26.0)
								EndIf
							EndIf
							n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
					EndIf
				Else
					StopChannel(n\SoundCHN) : n\SoundCHN = 0
					PlaySound2(snd_I\NVGSFX[1], Camera, n\Collider, 5.0)
					n\Target = Null
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State2 = 0.0
					n\State3 = 0.0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				;[End Block]
			Case MTF_ZOMBIES_SPOTTED
				;[Block]
				n\State2 = Max(n\State2 - fps\Factor[0], 0.0)
				If n\State2 > 0.0 And (Not n\Target\IsDead)
					Dist = EntityDistanceSquared(n\Collider, n\Target\Collider)
					If NPCSeesNPC(n\Target, n) = 1
						n\State2 = 70.0 * 15.0
						PointEntity(n\Collider, n\Target\Collider)
						RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
						
						If Dist < 4.0 Then n\State3 = 70.0 * 2.0
						If n\State3 > 0.0
							n\Speed = 0.02
							n\PathStatus = PATH_STATUS_NO_SEARCH
							n\PathLocation = 0
							n\PathTimer = 1.0
							n\CurrSpeed = CurveValue(-n\Speed, n\CurrSpeed, 20.0)
							TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
							AnimateNPC(n, 522.0, 488.0, n\CurrSpeed * 26.0)
							
							n\State3 = Max(n\State3 - fps\Factor[0], 0.0)
						EndIf
						n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 10.0)
						
						If n\Reload <= 0.0
							PlaySound2(snd_I\GunshotSFX[0], Camera, n\Collider, 15.0)
							
							Pvt = CreatePivot()
							RotateEntity(Pvt, EntityPitch(n\Collider), EntityYaw(n\Collider), 0.0, True)
							PositionEntity(Pvt, EntityX(n\OBJ), EntityY(n\OBJ), EntityZ(n\OBJ))
							MoveEntity(Pvt, 0.0622, 0.83925, 0.5351)
							
							If EntityDistanceSquared(me\Collider, n\Collider) < PowTwo(opt\CameraFogFar) Then LightVolume = TempLightVolume * 1.2
							
							SetEmitter(Null, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 13)
							SetEmitter(Null, EntityX(n\Target\Collider), EntityY(n\Target\Collider), EntityZ(n\Target\Collider), 15)
							
							FreeEntity(Pvt) : Pvt = 0
							
							PlaySound2(snd_I\BulletHitSFX, Camera, n\Target\Collider, 5.0)
							If n\Target\HP > 0
								n\Target\HP = Max(n\Target\HP - Rand(5, 10), 0.0)
							Else
								If (Not n\Target\IsDead)
									If n\Target\NPCType = NPCType049_2
										If n = n_I\MTFLeader
											LoadNPCSound(n, "SFX\Character\MTF\049_2\TargetTerminated.ogg")
											PlayMTFSound(n\Sound, n)
										EndIf
									EndIf
								EndIf
								n\Target\IsDead = True
								n\Target = Null
								n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
								n\State2 = 0.0
								n\State3 = 0.0
								n\State = MTF_WANDERING_AROUND
								Return
							EndIf
							n\Reload = 8.0
						EndIf
						n\PathStatus = PATH_STATUS_NO_SEARCH
					Else
						If n\PathTimer <= 0.0 ; ~ Update path
							n\PathStatus = FindPath(n, EntityX(n\Target\Collider), EntityY(n\Target\Collider), EntityZ(n\Target\Collider))
							If n\PathStatus = PATH_STATUS_FOUND
								While n\Path[n\PathLocation] = Null
									If n\PathLocation > MaxPathLocations - 1
										n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
										Exit
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Wend
								If n\PathLocation < MaxPathLocations - 1
									If n\Path[n\PathLocation] <> Null And n\Path[n\PathLocation + 1] <> Null
										If n\Path[n\PathLocation]\door = Null
											If Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation]\OBJ)) > Abs(DeltaYaw(n\Collider, n\Path[n\PathLocation + 1]\OBJ)) Then n\PathLocation = n\PathLocation + 1
										EndIf
									EndIf
								EndIf
								UseDoorNPC(n)
							EndIf
							n\PathTimer = 70.0 * Rnd(6.0, 10.0) ; ~ Search again after 6-10 seconds
						Else
							If n\PathStatus = PATH_STATUS_NOT_FOUND
								n\CurrSpeed = 0.0
								If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
								FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
							ElseIf n\PathStatus = PATH_STATUS_FOUND
								If n\Path[n\PathLocation] = Null
									If n\PathLocation > MaxPathLocations - 1
										n\PathLocation = 0 : n\PathStatus = PATH_STATUS_NO_SEARCH
									Else
										n\PathLocation = n\PathLocation + 1
									EndIf
								Else
									PointEntity(n\Collider, n\Path[n\PathLocation]\OBJ)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
									
									UseDoorNPC(n, True, True)
								EndIf
								n\PathTimer = n\PathTimer - fps\Factor[0] ; ~ Timer goes down slow
							Else
								If Dist >= 1.0 And n\State3 =< 0.0
									PointEntity(n\Collider, n\Target\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\CurrSpeed = CurveValue(n\Speed, n\CurrSpeed, 20.0)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 488.0, 522.0, n\CurrSpeed * 26.0)
								ElseIf n\State3 =< 0.0
									n\CurrSpeed = 0.0
									If Rand(35) = 1 Then RotateEntity(n\Collider, 0.0, Rnd(360.0), 0.0, True)
									FinishWalking(n, 488.0, 522.0, n\Speed * 26.0)
								Else
									PointEntity(n\Collider, n\Target\Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider, True), 0.0, True)
									n\CurrSpeed = CurveValue(-n\Speed, n\CurrSpeed, 20.0)
									TranslateEntity(n\Collider, Cos(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], 0.0, Sin(EntityYaw(n\Collider, True) + 90.0) * n\CurrSpeed * fps\Factor[0], True)
									AnimateNPC(n, 522.0, 488.0, n\CurrSpeed * 26.0)
								EndIf
								n\PathTimer = n\PathTimer - FPSFactorEx ; ~ Timer goes down fast
							EndIf
							n\Angle = CurveAngle(EntityYaw(n\Collider, True), n\Angle, 20.0)
						EndIf
					EndIf
				Else
					n\Target = Null
					n\EnemyX = 0.0 : n\EnemyY = 0.0 : n\EnemyZ = 0.0
					n\State2 = 0.0
					n\State3 = 0.0
					n\State = MTF_WANDERING_AROUND
					Return
				EndIf
				;[End Block]
		End Select
		
		If n\CurrSpeed > 0.01
			If (PrevFrame < 505.0 And n\Frame >= 505.0) Lor (PrevFrame < 521.0 And n\Frame >= 521.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
		ElseIf n\CurrSpeed < -0.01
			If (PrevFrame >= 521.0 And n\Frame < 521.0) Lor (PrevFrame >= 505.0 And n\Frame < 505.0) Then PlaySound2(snd_I\Step2SFX[Rand(0, 2)], Camera, n\Collider, 8.0, Rnd(0.5, 0.7))
		EndIf
		
		; ~ Teleport companions close to the leader if they get stuck
		If PlayerRoom\RoomTemplate\RoomID <> r_gate_a
			If Rand(100) = 1
				If n <> n_I\MTFLeader
					If n\State = MTF_WANDERING_AROUND Lor n\State = MTF_096_SPOTTED
						If EntityDistanceSquared(n\Collider, n_I\MTFLeader\Collider) > 256.0
							If (Not EntityInView(n\Collider, Camera)) And (Not EntityInView(n_I\MTFLeader\Collider, Camera)) Then TeleportEntity(n\Collider, EntityX(n_I\MTFLeader\Collider, True), EntityY(n_I\MTFLeader\Collider, True) + 0.5, EntityZ(n_I\MTFLeader\Collider, True), n\CollRadius, True)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		; ~ Teleport back to the facility if fell through the floor
		If PlayerRoom\RoomTemplate\RoomID <> r_cont2_049 And n\InFacility = LowerFloor Then TeleportCloser(n)
	EndIf
	If n_I\MTFLeader = Null And MTFTimer > 0.0 And MTFTimer < 35000.0
		PlayAnnouncement("SFX\Character\MTF\AnnouncLost.ogg")
		MTFTimer = 35000.0
	EndIf
	PositionEntity(n\OBJ, EntityX(n\Collider, True), EntityY(n\Collider, True) - 0.2, EntityZ(n\Collider, True), True)
	RotateEntity(n\OBJ, -90.0, n\Angle, 0.0, True)
End Function

Function TeleportCloser%(n.NPCs)
	Local ClosestDist# = 0.0
	Local closestWaypoint.WayPoints
	Local w.WayPoints
	Local xTemp#, zTemp#
	
	If InFacility > UpperFloor Then Return
	
	For w.WayPoints = Each WayPoints
		If w\door = Null
			xTemp = Abs(EntityX(w\OBJ, True) - EntityX(n\Collider, True))
			If xTemp > 1.0 And xTemp < 10.0
				zTemp = Abs(EntityZ(w\OBJ, True) - EntityZ(n\Collider, True))
				If zTemp > 1.0 And zTemp < 10.0
					If EntityDistanceSquared(me\Collider, w\OBJ) > PowTwo(16.0 - (8.0 * SelectedDifficulty\AggressiveNPCs))
						; ~ Teleports to the nearby waypoint that takes it closest to the player
						Local NewDist# = EntityDistanceSquared(me\Collider, w\OBJ)
						
						If NewDist < ClosestDist Lor closestWaypoint = Null
							ClosestDist = NewDist
							closestWaypoint = w
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	Local ShouldTeleport% = False
	
	If closestWaypoint <> Null
		If n\InFacility <> NullFloor Lor SelectedDifficulty\AggressiveNPCs Then
			ShouldTeleport = True
		ElseIf EntityY(closestWaypoint\OBJ, True) <= 6.5 And EntityY(closestWaypoint\OBJ, True) >= -6.5
			ShouldTeleport = True
		EndIf
		If ShouldTeleport
			TeleportEntity(n\Collider, EntityX(closestWaypoint\OBJ, True), EntityY(closestWaypoint\OBJ, True) + 60.0 * RoomScale, EntityZ(closestWaypoint\OBJ, True), n\Collider, True)
			n\CurrSpeed = 0.0
			n\PathStatus = PATH_STATUS_NO_SEARCH
			n\PathTimer = 0.0
			n\PathLocation = 0
		EndIf
	EndIf
End Function

; ~ Path status constants
;[Block]
Const PATH_STATUS_NO_SEARCH% = 0
Const PATH_STATUS_FOUND% = 1
Const PATH_STATUS_NOT_FOUND% = 2
;[End Block]

Function FindPath%(n.NPCs, x#, y#, z#)
	Local Dist#, Dist2#
	Local w.WayPoints, StartPoint.WayPoints, EndPoint.WayPoints, Smallest.WayPoints
	Local i%, gTemp#
	
	; ~ PathStatus = PATH_STATUS_NO_SEARCH, route hasn't been searched for yet
	; ~ PathStatus = PATH_STATUS_FOUND, route found
	; ~ PathStatus = PATH_STATUS_NOT_FOUND, route not found (target unreachable)
	
	For w.WayPoints = Each WayPoints
		w\State = 0
		w\Fcost = 0
		w\Gcost = 0
		w\Hcost = 0
	Next
	
	n\PathStatus = PATH_STATUS_NO_SEARCH
	n\PathLocation = 0
	For i = 0 To MaxPathLocations - 1
		n\Path[i] = Null
	Next
	
	Local Pvt% = CreatePivot()
	
	PositionEntity(Pvt, x, y, z, True)
	
	Local Temp% = CreatePivot()
	
	PositionEntity(Temp, EntityX(n\Collider, True), EntityY(n\Collider, True) + 0.15, EntityZ(n\Collider, True))
	
	Dist = 350.0
	For w.WayPoints = Each WayPoints
		Dist2 = EntityDistanceSquared(w\OBJ, Temp)
		If Dist2 < Dist
			; ~ Prefer waypoints that are visible
			If (Not EntityVisible(w\OBJ, Temp)) Then Dist2 = Dist2 * 3.0
			If Dist2 < Dist
				Dist = Dist2
				StartPoint = w
			EndIf
		EndIf
	Next
	FreeEntity(Temp) : Temp = 0
	
	If StartPoint = Null
		FreeEntity(Pvt) : Pvt = 0
		Return(PATH_STATUS_NOT_FOUND)
	EndIf
	StartPoint\State = 1
	
	EndPoint = Null
	Dist = 400.0
	For w.WayPoints = Each WayPoints
		Dist2 = EntityDistanceSquared(Pvt, w\OBJ)
		If Dist2 < Dist
			Dist = Dist2
			EndPoint = w
		EndIf
	Next
	FreeEntity(Pvt) : Pvt = 0
	
	If EndPoint = StartPoint
		If Dist < 0.4
			Return(PATH_STATUS_NO_SEARCH)
		Else
			n\Path[0] = EndPoint
			Return(PATH_STATUS_FOUND)
		EndIf
	EndIf
	If EndPoint = Null Then Return(PATH_STATUS_NOT_FOUND)
	
	Local Temp2%
	
	Repeat
		Temp2 = False
		Smallest.WayPoints = Null
		Dist = 10000.0
		For w.WayPoints = Each WayPoints
			If w\State = 1
				Temp2 = True
				If w\Fcost < Dist
					Dist = w\Fcost
					Smallest = w
				EndIf
			EndIf
		Next
		
		If Smallest <> Null
			w = Smallest
			w\State = 2
			
			For i = 0 To 4
				If w\connected[i] <> Null
					If w\connected[i]\State < 2
						If w\connected[i]\State = 1
							gTemp = w\Gcost + w\Dist[i]
							If n\NPCType = NPCTypeMTF
								If w\connected[i]\door = Null Then gTemp = gTemp + 0.5
							EndIf
							If gTemp < w\connected[i]\Gcost
								w\connected[i]\Gcost = gTemp
								w\connected[i]\Fcost = w\connected[i]\Gcost + w\connected[i]\Hcost
								w\connected[i]\parent = w
							EndIf
						Else
							w\connected[i]\Hcost = Abs(EntityX(w\connected[i]\OBJ, True) - EntityX(EndPoint\OBJ, True)) + Abs(EntityZ(w\connected[i]\OBJ, True) - EntityZ(EndPoint\OBJ, True))
							gTemp = w\Gcost + w\Dist[i]
							If n\NPCType = NPCTypeMTF
								If w\connected[i]\door = Null Then gTemp = gTemp + 0.5
							EndIf
							w\connected[i]\Gcost = gTemp
							w\connected[i]\Fcost = w\Gcost + w\Hcost
							w\connected[i]\parent = w
							w\connected[i]\State = 1
						EndIf
					EndIf
				EndIf
			Next
		Else
			If EndPoint\State > 0
				StartPoint\parent = Null
				EndPoint\State = 2
				Exit
			EndIf
		EndIf
		
		If EndPoint\State > 0
			StartPoint\parent = Null
			EndPoint\State = 2
			Exit
		EndIf
	Until (Not Temp2)
	
	If EndPoint\State > 0
		Local CurrPoint.WayPoints = EndPoint
		Local TwentiethPoint.WayPoints = EndPoint
		Local Length% = 0
		
		Repeat
			Length = Length + 1
			CurrPoint = CurrPoint\parent
			If Length > MaxPathLocations - 1 Then TwentiethPoint = TwentiethPoint\parent
		Until CurrPoint = Null
		
		CurrPoint.WayPoints = EndPoint
		While TwentiethPoint <> Null
			Length = Min(Length - 1, MaxPathLocations - 1)
			TwentiethPoint = TwentiethPoint\parent
			n\Path[Length] = TwentiethPoint
		Wend
		
		Return(PATH_STATUS_FOUND)
	Else
		Return(PATH_STATUS_NOT_FOUND)
	EndIf
End Function

Function NPCSeesNPC%(n.NPCs, n2.NPCs, Dist# = 36.0)
	If EntityDistanceSquared(n\Collider, n2\Collider) < Dist
		If EntityVisible(n\Collider, n2\Collider) Then Return(1)
		If n = n_I\Curr173
			Local SoundVol173# = Max(Min((Distance(EntityX(n_I\Curr173\Collider), n_I\Curr173\PrevX, EntityZ(n_I\Curr173\Collider), n_I\Curr173\PrevZ) * 2.5), 1.0), 0.0)
			
			If SoundVol173 > 0.0 Then Return(2)
		EndIf
	EndIf
	Return(0)
End Function

Function NPCSeesPlayer%(n.NPCs, Dist#, Angle# = 60.0, DisableSoundOnCrouch% = False)
	; ~ Return values:
	; ~ 0: Player is not detected anyhow
	; ~ 1: Player is detected by vision
	; ~ 2: Player is detected by emitting a sound
	; ~ 3: Player is detected by a camera (only for MTF Units!)
	
	Local Dist2# = EntityDistanceSquared(me\Collider, n\Collider)
	
	If n\BlinkTimer <= 0.0 Then Return(0)
	If I_268\InvisibilityOn Lor chs\NoTarget Then n\State2 = 0.0 : Return(0)
	
	If n\NPCType <> NPCTypeMTF
		If Dist2 > PowTwo(Dist)
			Return(0)
		Else
			Local Visible% = EntityVisible(n\Collider, me\Collider)
			Local DeltaYawVal# = Abs(DeltaYaw(n\Collider, me\Collider))
			
			; ~ Spots the player if he's either in view or making a loud sound
			If me\SndVolume > 1.0
				If (DeltaYawVal > Angle) And Visible
					Return(1)
				ElseIf (Not Visible)
					If DisableSoundOnCrouch And me\Crouch
						Return(0)
					Else
						Return(2)
					EndIf
				EndIf
			ElseIf DeltaYawVal > 60.0 
				Return(0)
			EndIf
			Return(Visible)
		EndIf
	Else
		Local ReturnState% = 0 + (3 * me\Detected)
		
		If Dist2 < PowTwo(Dist + ((PlayerRoom\RoomTemplate\RoomID = r_gate_a) * 4.0))
			If me\SndVolume > Rnd(1.0, 1.5) Then ReturnState = 2
			If EntityVisible(n\Collider, me\Collider) And Abs(DeltaYaw(n\Collider, me\Collider)) < Angle Then ReturnState = 1
		EndIf
		Return(ReturnState)
	EndIf
End Function

Function PlayerSees173%(n.NPCs)
	If (Not chs\NoTarget) And (wi\IsNVGBlinking Lor (Not (EntityInView(n\OBJ, Camera) Lor EntityInView(n\OBJ2, Camera))) Lor (me\BlinkTimer > -16.0 And me\BlinkTimer < -6.0))
		Return(False)
	Else
		Return(True)
	EndIf
End Function

Function UpdateNPCBlinking%(n.NPCs)
	If n\BlinkTimer =< 0.0 Then n\BlinkTimer = 70.0 * Rnd(5.0, 10.0)
	n\BlinkTimer = n\BlinkTimer - fps\Factor[0]
End Function

Function Shoot%(x#, y#, z#, HitProb# = 1.0, Particles% = True, InstaKill% = False)
	Local p.Particles, de.Decals, n.NPCs, emit.Emitter
	Local Pvt%, ShotMessageUpdate$, i%
	Local DifficultyDMGMult#
	
	SetEmitter(Null, x, y, z, 13)
	
	LightVolume = TempLightVolume * 1.2
	
	If InstaKill
		PlaySound_Strict(snd_I\BulletHitSFX)
		Kill(True)
		Return
	EndIf
	
	If Rnd(1.0) <= HitProb
		Select SelectedDifficulty\OtherFactors
			Case EASY
				;[Block]
				DifficultyDMGMult = 1.0
				;[End Block]
			Case NORMAL
				;[Block]
				DifficultyDMGMult = 1.15
				;[End Block]
			Case HARD
				;[Block]
				DifficultyDMGMult = 1.3
				;[End Block]
			Case EXTREME
				;[Block]
				DifficultyDMGMult = 1.45
				;[End Block]
		End Select
		
		Local MsgRand% = Rand(17)
		
		TurnEntity(Camera, Rnd(-3.0, 3.0), Rnd(-3.0, 3.0), 0.0)
		Select MsgRand
			Case 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ; ~ Vest
				;[Block]
				If wi\BallisticVest <> 2 Then me\Stamina = me\Stamina - Rnd(5.0)
				InjurePlayer(Rnd(0.61, 0.72) * DifficultyDMGMult, 0.0, Rnd(200.0, 300.0), 0.43 * DifficultyDMGMult)
				If wi\BallisticVest > 0
					ShotMessageUpdate = GetLocalString("msg", "bullet.vest")
				Else
					ShotMessageUpdate = GetLocalString("msg", "bullet.body")
				EndIf
				;[End Block]
			Case 11 ; ~ Left Leg
				;[Block]
				me\Stamina = me\Stamina - Rnd(10.0)
				InjurePlayer(Rnd(0.44, 0.54) * DifficultyDMGMult, 0.0, Rnd(400.0, 500.0))
				ShotMessageUpdate = GetLocalString("msg", "bullet.leg.left")
				;[End Block]
			Case 12 ; ~ Right Leg
				;[Block]
				me\Stamina = me\Stamina - Rnd(10.0)
				InjurePlayer(Rnd(0.44, 0.54) * DifficultyDMGMult, 0.0, Rnd(400.0, 500.0))
				ShotMessageUpdate = GetLocalString("msg", "bullet.leg.right")
				;[End Block]
			Case 13 ; ~ Left Arm
				;[Block]
				InjurePlayer(Rnd(0.44, 0.54) * DifficultyDMGMult, 0.0, Rnd(400.0, 500.0))
				ShotMessageUpdate = GetLocalString("msg", "bullet.arm.left")
				;[End Block]
			Case 14 ; ~ Right Arm
				;[Block]
				InjurePlayer(Rnd(0.44, 0.54) * DifficultyDMGMult, 0.0, Rnd(400.0, 500.0))
				ShotMessageUpdate = GetLocalString("msg", "bullet.arm.right")
				;[End Block]
			Case 15 ; ~ Neck
				;[Block]
				InjurePlayer(Rnd(0.75, 0.9) * DifficultyDMGMult, 0.0, 700.0)
				ShotMessageUpdate = GetLocalString("msg", "bullet.neck")
				;[End Block]
			Case 16, 17 ; ~ Helmet, Face or Head
				;[Block]
				If wi\BallisticHelmet
					InjurePlayer(0.1)
				Else
					For n.NPCs = Each NPCs
						If n\NPCType = NPCTypeMTF Lor n\NPCType = NPCTypeApache Lor n\NPCType = NPCTypeGuard
							If EntityInView(n\OBJ, Camera)
								ShotMessageUpdate = GetLocalString("msg", "bullet.face")
							Else
								ShotMessageUpdate = GetLocalString("msg", "bullet.head")
							EndIf
							Kill(True)
						EndIf
					Next
				EndIf
				;[End Block]
		End Select
		If msg\Timer < 70.0 * 5.0 Then CreateMsg(ShotMessageUpdate)
		If me\Injuries >= 6.0 Then Kill(True)
		
		If MsgRand > 10 And MsgRand < 16 Then emit.Emitter = SetEmitter(Null, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 15)
		
		PlaySound_Strict(snd_I\BulletHitSFX)
	ElseIf Particles And opt\ParticleAmount > 0
		Pvt = CreatePivot()
		PositionEntity(Pvt, EntityX(me\Collider), (EntityY(me\Collider) + EntityY(Camera)) / 2.0, EntityZ(me\Collider))
		If emit <> Null Then PointEntity(Pvt, emit\Owner)
		TurnEntity(Pvt, 0.0, 180.0, 0.0)
		EntityPick(Pvt, 2.5)
		
		If PickedEntity() <> 0
			PlaySound2(snd_I\BulletMissSFX, Camera, Pvt, 0.4, Rnd(0.8, 1.0))
			
			If Particles
				p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, PickedX(), PickedY(), PickedZ(), 0.03, 0.0, 80.0)
				p\Speed = 0.001 : p\SizeChange = 0.003 : p\Alpha = 0.8 : p\AlphaChange = -0.01
				RotateEntity(p\Pvt, EntityPitch(Pvt) - 180.0, EntityYaw(Pvt), 0)
				
				For i = 0 To Rand(2, 3)
					p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, PickedX(), PickedY(), PickedZ(), 0.006, 0.003, 80.0)
					p\Speed = 0.02 : p\Alpha = 0.8 : p\AlphaChange = -0.01
					RotateEntity(p\Pvt, EntityPitch(Pvt) + Rnd(170.0, 190.0), EntityYaw(Pvt) + Rnd(-10.0, 10.0), 0.0)
				Next
				
				de.Decals = CreateDecal(Rand(DECAL_BULLET_HOLE_1, DECAL_BULLET_HOLE_2), PickedX(), PickedY() + Rnd(-0.05, 0.05), PickedZ(), Rnd(-4.0, 4.0), Rnd(-4.0, 4.0), Rnd(-4.0, 4.0), Rnd(0.028, 0.034), 1.0, 1, 2)
				de\LifeTime = 70.0 * 20.0
				AlignToVector(de\OBJ, -PickedNX(), -PickedNY(), -PickedNZ(), 3)
				MoveEntity(de\OBJ, 0.0, 0.0, -0.001)
			EndIf
		EndIf
		FreeEntity(Pvt) : Pvt = 0
	EndIf
End Function

Function MoveToPocketDimension%()
	Local r.Rooms, e.Events
	
	n_I\Curr106\State = 250.0 ; ~ Make SCP-106 idle for a while
	For r.Rooms = Each Rooms
		If r\RoomTemplate\RoomID = r_dimension_106
			me\BlinkTimer = -10.0 : me\FallTimer = 0.0 : me\DropSpeed = 0.0 : me\Playable = True
			InjurePlayer(0.5, 0.0, 1500.0)
			HideEntity(me\Head)
			ShowEntity(me\Collider)
			PlaySound_Strict(snd_I\Use914SFX)
			PlaySound_Strict(snd_I\SCP106SFX[5])
			
			TeleportEntity(me\Collider, EntityX(r\OBJ), EntityY(r\OBJ) + 0.5, EntityZ(r\OBJ))
			TeleportToRoom(r)
			
			For e.Events = Each Events
				If r = e\room
					e\EventState2 = PD_StartRoom
					Exit
				EndIf
			Next
			Return
			Exit
		EndIf
	Next
End Function

Function FindFreeNPCID%()
	Local n2.NPCs
	Local ID% = 1
	
	While True
		Local Taken% = False
		
		For n2.NPCs = Each NPCs
			If n2\ID = ID
				Taken = True
				Exit
			EndIf
		Next
		If (Not Taken) Then Return(ID)
		ID = ID + 1
	Wend
End Function

Function ForceSetNPCID%(n.NPCs, NewID%)
	Local n2.NPCs
	
	n\ID = NewID
	
	For n2.NPCs = Each NPCs
		If n2 <> n
			If n2\ID = NewID Then n2\ID = FindFreeNPCID()
		EndIf
	Next
End Function

Function ConsoleSpawnNPC%(Name$, NPCState$ = "")
	Local n.NPCs
	Local ConsoleMsg$
	
	Select Name 
		Case "008", "008zombie", "008-1", "infectedhuman", "humaninfected", "scp008-1", "scp-008-1", "scp0081", "0081", "scp-0081", "008_1", "scp_008_1"
			;[Block]
			n.NPCs = CreateNPC(NPCType008_1, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n\State = 1.0
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "008"))
			;[End Block]
		Case "049", "scp049", "scp-049", "plaguedoctor"
			;[Block]
			n.NPCs = CreateNPC(NPCType049, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n\State = 1.0
			n_I\Curr049 = n
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-049")
			;[End Block]
		Case "049-2", "0492", "scp-049-2", "scp049-2", "049zombie", "curedhuman", "scp0492", "scp-0492", "049_2", "scp_049_2"
			;[Block]
			n.NPCs = CreateNPC(NPCType049_2, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n\State = 1.0
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-049-2")
			;[End Block]
		Case "066", "scp066", "scp-066", "eric"
			;[Block]
			n.NPCs = CreateNPC(NPCType066, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n_I\Curr066 = n
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-066")
			;[End Block]
		Case "096", "scp096", "scp-096", "shyguy"
			;[Block]
			n.NPCs = CreateNPC(NPCType096, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n\State = 1.0
			n_I\Curr096 = n
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-096")
			;[End Block]
		Case "106", "scp106", "scp-106", "larry", "oldman"
			;[Block]
			n.NPCs = CreateNPC(NPCType106, EntityX(me\Collider), EntityY(me\Collider) - 0.5, EntityZ(me\Collider))
			n\State = -1.0
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-106")
			;[End Block]
		Case "173", "scp173", "scp-173", "statue", "sculpture"
			;[Block]
			n.NPCs = CreateNPC(NPCType173, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n_I\Curr173 = n
			If n_I\Curr173\Idle = 3 Then n_I\Curr173\Idle = 0
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-173")
			;[End Block]
		Case "372", "scp372", "scp-372", "pj", "jumper"
			;[Block]
			n.NPCs = CreateNPC(NPCType372, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-372")
			;[End Block]
		Case "513-1", "5131", "scp513-1", "scp-513-1", "bll", "scp-5131", "scp5131"
			;[Block]
			n.NPCs = CreateNPC(NPCType513_1, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-513-1")
			;[End Block]
		Case "860-2", "8602", "scp860-2", "scp-860-2", "forestmonster", "scp8602"
			;[Block]
			CreateConsoleMsg(Format(GetLocalString("console", "spawn.nope"), "SCP-860-2"), 255, 0, 0)
			;[End Block]
		Case "939", "scp939", "scp-939"
			CreateConsoleMsg(Format(GetLocalString("console", "spawn.nope"), GetLocalString("npc", "939")), 255, 0, 0)
			;[End Block]
		Case "966", "scp966", "scp-966", "sleepkiller"
			;[Block]
			n.NPCs = CreateNPC(NPCType966, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "966"))
			;[End Block]
		Case "1048a", "1048-a", "scp1048-a", "scp-1048-a", "scp1048a", "scp-1048a", "earbear"
			;[Block]
			n.NPCs = CreateNPC(NPCType1048_A, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-1048-A")
			;[End Block]
		Case "1048", "scp1048", "scp-1048", "scp-1048", "bear", "builderbear"
			;[Block]
			n.NPCs = CreateNPC(NPCType1048, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-1048")
			;[End Block]
		Case "1499-1", "14991", "scp-1499-1", "scp1499-1", "scp-14991", "scp14991"
			n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-1499-1")
			;[End Block]
		Case "class-d", "classd", "d"
			;[Block]
			n.NPCs = CreateNPC(NPCTypeD, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "dclass"))
			;[End Block]
		Case "guard"
			;[Block]
			n.NPCs = CreateNPC(NPCTypeGuard, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "guard"))
			;[End Block]
		Case "mtf", "ntf"
			;[Block]
			n.NPCs = CreateNPC(NPCTypeMTF, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			n_I\MTFLeader = n
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "mtf"))
			;[End Block]
		Case "apache", "helicopter"
			;[Block]
			n.NPCs = CreateNPC(NPCTypeApache, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "apache"))
			;[End Block]
		Case "tentacle", "scp035tentacle", "scp-035tentacle", "scp-035-tentacle", "scp035-tentacle"
			;[Block]
			n.NPCs = CreateNPC(NPCType035_Tentacle, EntityX(me\Collider), EntityY(me\Collider) - 0.12, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "tentacle"))
			;[End Block]
		Case "clerk", "woman"
			;[Block]
			n.NPCs = CreateNPC(NPCTypeClerk, EntityX(me\Collider), EntityY(me\Collider) + 0.2, EntityZ(me\Collider))
			ConsoleMsg = Format(GetLocalString("console", "spawn"), GetLocalString("npc", "clerk"))
			;[End Block]
		Default 
			;[Block]
			CreateConsoleMsg(GetLocalString("console", "spawn.notfound"), 255, 0, 0)
			Return
			;[End Block]
	End Select
	
	If n <> Null
		If NPCState <> "" Then
			n\State = Float(NPCState)
			ConsoleMsg = ConsoleMsg + " (State = " + n\State + ")"
		EndIf
	EndIf
	
	CreateConsoleMsg(ConsoleMsg)
End Function

Function ManipulateNPCBones%()
	Local n.NPCs
	Local MaxValue#, MinValue#, Offset#, Smooth#
	Local i%, Bone%, Pvt%, BoneName$
	Local ToValue#
	
	For n.NPCs = Each NPCs
		If n\ManipulateBone
			BoneName = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "bonename", 0)
			If BoneName <> ""
				Pvt = CreatePivot()
				Bone = FindChild(n\OBJ, BoneName)
				If Bone = 0 Then RuntimeError(Format(GetLocalString("runerr", "spawn.bone.notexist"), BoneName))
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
									ToValue = DeltaPitch(Bone, Camera) + Offset
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
								MaxValue = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_max", 2)
								MinValue = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controlleraxis" + i + "_min", 2)
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
				FreeEntity(Pvt) : Pvt = 0
			EndIf
		EndIf
	Next
End Function

Function NPCSpeedChange%(n.NPCs)
	Select n\NPCType
		Case NPCType173, NPCType106, NPCType096, NPCType049, NPCType939
			Select SelectedDifficulty\OtherFactors
				Case NORMAL
					;[Block]
					n\Speed = n\Speed * 1.1
					;[End Block]
				Case HARD
					;[Block]
					n\Speed = n\Speed * 1.2
					;[End Block]
				Case EXTREME
					;[Block]
					n\Speed = n\Speed * 1.3
					;[End Block]
			End Select
	End Select
End Function

Function IsPlayerOutsideFacility%()
	If PlayerRoom\RoomTemplate\RoomID = r_gate_a Lor PlayerRoom\RoomTemplate\RoomID = r_gate_b Then Return(True)
	Return(False)
End Function

Function PlayerInReachableRoom%(CanSpawnIn049Chamber% = False, Intro% = False)
	Local e.Events
	
	; ~ Player is in these rooms, returning false
	If PlayerRoom\RoomTemplate\RoomID = r_dimension_106 Lor PlayerRoom\RoomTemplate\RoomID = r_dimension_1499 Lor (PlayerRoom\RoomTemplate\RoomID = r_cont1_173_intro And (Not Intro)) Lor IsPlayerOutsideFacility() Then Return(False)
	; ~ Player is in SCP-860-1, returning false
	If forest_event <> Null
		If PlayerRoom = forest_event\room
			If forest_event\EventState = 1.0 Then Return(False)
		EndIf
	EndIf
	; ~ Player is inside the fake world, returning false
	If skull_event <> Null
		If skull_event\EventState > 0.0 Then Return(False)
	EndIf
	
	If (Not CanSpawnIn049Chamber)
		If (Not SelectedDifficulty\AggressiveNPCs)
			If PlayerRoom\RoomTemplate\RoomID = r_cont2_049 And EntityY(me\Collider) <= (-2848.0) * RoomScale Then Return(False)
		EndIf
	EndIf
	; ~ Return true, this means player is in reachable room
	Return(True)
End Function

Function UseDoorNPC%(n.NPCs, PlaySFX% = True, PlayCautionSFX% = False)
	Local Dist# = EntityDistanceSquared(n\Collider, n\Path[n\PathLocation]\OBJ)
	
	If n\NPCType = NPCTypeMTF
		If Dist < 1.0
			If n\Path[n\PathLocation]\door <> Null
				If (Not n\Path[n\PathLocation]\door\Open) And n\Path[n\PathLocation]\door\DoorType <> OFFICE_DOOR And n\Path[n\PathLocation]\door\DoorType <> ELEVATOR_DOOR
					OpenCloseDoor(n\Path[n\PathLocation]\door, PlaySFX, PlayCautionSFX)
					If PlaySFX Then PlaySound2(NPCSound[SOUND_NPC_MTF_BEEP], Camera, n\OBJ, 8.0)
					If n\Path[n\PathLocation]\door\MTFClose Then n\Path[n\PathLocation]\door\TimerState = 70.0 * 5.0
				EndIf
			EndIf
			If Dist < PathLocationDist Then n\PathLocation = n\PathLocation + 1
		EndIf
	Else
		If Dist < 0.49
			Local Temp% = True
			
			If n\Path[n\PathLocation]\door <> Null
				If (Not n\Path[n\PathLocation]\door\Open) And (n\Path[n\PathLocation]\door\DoorType = ELEVATOR_DOOR Lor n\Path[n\PathLocation]\door\Locked > 0 Lor n\Path[n\PathLocation]\door\KeyCard <> 0 Lor n\Path[n\PathLocation]\door\Code <> 0 Lor (Not n\Path[n\PathLocation]\door\Buttons[0]) Lor (Not n\Path[n\PathLocation]\door\Buttons[1]) Lor ((Not n\Path[n\PathLocation]\door\Open) And n\Path[n\PathLocation]\door\DoorType = OFFICE_DOOR))
					Temp = False
				Else
					If (Not n\Path[n\PathLocation]\door\Open)
						OpenCloseDoor(n\Path[n\PathLocation]\door, PlaySFX, PlayCautionSFX)
						If n\NPCType = NPCType049 Then n\Path[n\PathLocation]\door\TimerState = 70.0 * 3.0
					EndIf
				EndIf
			EndIf
			If Dist < PathLocationDist And Temp
				n\PathLocation = n\PathLocation + 1
			ElseIf Dist < 0.25 And (Not Temp)
				; ~ Breaking up the path when the door in front of NPC cannot be operated by himself
				n\PathStatus = PATH_STATUS_NO_SEARCH
				n\PathTimer = 70.0 * 6.0
				n\PathLocation = 0
			EndIf
		EndIf
	EndIf
End Function

Function UpdateNPCNearTesla%()
	Local n.NPCs
	
	For n.NPCs = Each NPCs
		If n\TeslaHit
			If opt\ParticleAmount > 0 Then SetEmitter(Null, EntityX(n\OBJ, True), EntityY(n\OBJ, True), EntityZ(n\OBJ, True), 14)
			
			If n\NPCType = NPCType106
				If n\State3 = 1.0
					AnimateNPC(n, 259.0, 110.0, -0.15, False)
					
					If n\Frame <= 170.0
						PositionEntity(n\Collider, 0.0, -500.0, 0.0)
						ResetEntity(n\Collider)
						
						n\Idle = 0 : n\State = 70.0 * 60.0 * Rnd(10.0, 13.0) : n\TeslaHit = False : n\State3 = 0.0
					EndIf
				EndIf
			Else
				n\TeslaHit = False
			EndIf
		EndIf
	Next
End Function

Function SetNPCFrame%(n.NPCs, Frame#)
	If Abs(n\Frame - Frame) < 0.001 Then Return
	
	If EntityDistanceSquared(n\Collider, me\Collider) >= PowTwo(HideDistance)
		If n\AnimTimer <= 0.0
			SetAnimTime(n\OBJ, Frame)
			n\AnimTimer = fps\Factor[0] * 4.0
		Else
			n\AnimTimer = n\AnimTimer - fps\Factor[0]
		EndIf
	Else
		SetAnimTime(n\OBJ, Frame)
	EndIf
	
	n\Frame = Frame
End Function

Function AnimateNPC%(n.NPCs, FirstFrame#, LastFrame#, Speed#, Loop% = True)
	Local NewTime#, Temp%
	
	If Speed > 0.0
		NewTime = Max(Min(n\Frame + Speed * fps\Factor[0], LastFrame), FirstFrame)
		
		If Loop And NewTime >= LastFrame Then NewTime = FirstFrame
	Else
		If FirstFrame < LastFrame
			Temp = FirstFrame
			FirstFrame = LastFrame
			LastFrame = Temp
		EndIf
		
		If Loop
			NewTime = n\Frame + Speed * fps\Factor[0]
			
			If NewTime < LastFrame
				NewTime = FirstFrame
			ElseIf NewTime > FirstFrame
				NewTime = LastFrame
			EndIf
		Else
			NewTime = Max(Min(n\Frame + Speed * fps\Factor[0], FirstFrame), LastFrame)
		EndIf
	EndIf
	SetNPCFrame(n, NewTime)
End Function

Function Animate2#(Entity%, Curr#, FirstFrame%, LastFrame%, Speed#, Loop% = True)
	Local NewTime#, Temp%
	
	If Speed > 0.0
		NewTime = Max(Min(Curr + Speed * fps\Factor[0], LastFrame), FirstFrame)
		
		If Loop
			If NewTime >= LastFrame Then NewTime = FirstFrame
		EndIf
	Else
		If FirstFrame < LastFrame
			Temp = FirstFrame
			FirstFrame = LastFrame
			LastFrame = Temp
		EndIf
		
		If Loop
			NewTime = Curr + Speed * fps\Factor[0]
			
			If NewTime < LastFrame Then NewTime = FirstFrame
			If NewTime > FirstFrame Then NewTime = LastFrame
		Else
			NewTime = Max(Min(Curr + Speed * fps\Factor[0], FirstFrame), LastFrame)
		EndIf
	EndIf
	
	SetAnimTime(Entity, NewTime)
	
	Return(NewTime)
End Function

Function FinishWalking%(n.NPCs, StartFrame#, EndFrame#, Speed#)
	Local CenterFrame#
	
	If n <> Null
		CenterFrame = (EndFrame - StartFrame) / 2.0
		If n\Frame >= CenterFrame
			AnimateNPC(n, StartFrame, EndFrame, Speed, False)
		Else
			AnimateNPC(n, EndFrame, StartFrame, -Speed, False)
		EndIf
	EndIf
End Function

Function ChangeNPCTextureID%(n.NPCs, TextureID%)
	Local Temp#
	
	If n = Null
		OpenConsoleOnError(GetLocalString("msg", "spawn.invaildtex"))
		Return
	EndIf
	
	n\TextureID = TextureID + 1
	EntityTexture(n\OBJ, n_I\NPCTextureID[TextureID])
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS