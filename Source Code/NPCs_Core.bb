Include "Source Code\NPCs_AI_Core.bb"

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
	Field ModelScale#
	Field TextureID% = -1
	Field HasAsset% = False
	Field HasAnim%
	Field Contained% = False
	Field IsOpt% = False
	Field TargetUpdateTimer#
End Type

Const NPCsFile$ = "Data\NPCs.ini"

Global ForestNPC%, ForestNPCTex%, ForestNPCData#[3]

Function CreateNPC.NPCs(NPCType%, x#, y#, z#)
	CatchErrors("CreateNPC(" + NPCType + ", " + x + ", " + y + ", " + z)
	
	Local n.NPCs, n2.NPCs
	Local Temp#, i%, Tex%
	
	n.NPCs = New NPCs
	n\NPCType = NPCType
	n\GravityMult = 1.0
	n\MaxGravity = 0.2
	n\CollRadius = 0.2
	n\FallingPickDistance = 10.0
	n\HasAnim = True
	Select NPCType
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
		Case NPCType035_Tentacle
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
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
		Case NPCType106
			;[Block]
			n\NVGName = "SCP-106"
			n\GravityMult = 0.0
			n\MaxGravity = 0.0
			n\Speed = IniGetFloat(NPCsFile, "SCP-106", "Speed") / 100.0
			
			n\Collider = CreatePivot()
			n\CollRadius = 0.24
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_106_MODEL])
			Temp = IniGetFloat(NPCsFile, "SCP-106", "Scale") / 2.2
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			
			n\OBJ2 = CreateSprite()
			ScaleSprite(n\OBJ2, 0.03, 0.03)
			Tex = LoadTexture_Strict("GFX\NPCs\scp_106_eyes.png", 1, DeleteAllTextures, False)
			EntityTexture(n\OBJ2, Tex)
			DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EntityBlend(n\OBJ2, 3)
			EntityFX(n\OBJ2, 1 + 8)
			SpriteViewMode(n\OBJ2, 2)
			HideEntity(n\OBJ2)
			;[End Block]
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
					Tex = LoadTexture_Strict("GFX\NPCs\scp_173_H.png")
					;[End Block]
				Case "01 Jan "
					;[Block]
					n_I\IsNewYear = True
					Tex = LoadTexture_Strict("GFX\NPCs\scp_173_NY.png")
					;[End Block]
				Case "01 Apr "
					;[Block]
					n_I\IsAprilFools = True
					Tex = LoadTexture_Strict("GFX\NPCs\scp_173_J.png")
					;[End Block]
			End Select
			If Tex <> 0
				EntityTexture(n\OBJ, Tex)
				EntityTexture(n\OBJ2, Tex)
				DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EndIf
			Temp = IniGetFloat(NPCsFile, "SCP-173", "Scale") / MeshDepth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			ScaleEntity(n\OBJ2, Temp, Temp, Temp)
			
			n\OBJ3 = CopyEntity(n_I\NPCModelID[NPC_173_BOX_MODEL])
			ScaleEntity(n\OBJ3, RoomScale, RoomScale, RoomScale)
			HideEntity(n\OBJ3)
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
			DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
			EntityFX(n\OBJ2, 1 + 8)
			EntityBlend(n\OBJ2, 3)
			SpriteViewMode(n\OBJ2, 2)
			HideEntity(n\OBJ2)
			
			If ForestNPC = 0
				ForestNPC = CreateSprite()
				ScaleSprite(ForestNPC, 0.75 * (140.0 / 410.0), 0.75)
				SpriteViewMode(ForestNPC, 4)
				EntityFX(ForestNPC, 1 + 8)
				ForestNPCTex = LoadAnimTexture_Strict("GFX\NPCs\AgentIJ.AIJ", 1 + 2, 140, 410, 0, 4, DeleteAllTextures)
				ForestNPCData[0] = 0.0
				EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
				ForestNPCData[1] = 0.0
				ForestNPCData[2] = 0.0
				HideEntity(ForestNPC)
			EndIf
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
			n\ModelScale = IniGetFloat(NPCsFile, "SCP-1048", "Scale") / 10.0 * Rnd(0.9, 1.1)
			ScaleEntity(n\OBJ, n\ModelScale, n\ModelScale, n\ModelScale)
			;[End Block]
		Case NPCType1499_1
			;[Block]
			n\NVGName = GetLocalString("npc", "undefine")
			n\Speed = IniGetFloat(NPCsFile, "SCP-1499-1", "Speed") / 100.0 * Rnd(0.9, 1.1)
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[NPC_1499_1_MODEL])
			n\ModelScale = IniGetFloat(NPCsFile, "SCP-1499-1", "Scale") / 4.0 * Rnd(0.8, 1.0)
			ScaleEntity(n\OBJ, n\ModelScale, n\ModelScale, n\ModelScale)
			EntityFX(n\OBJ, 1)
			EntityAutoFade(n\OBJ, HideDistance * 2.5, HideDistance * 2.95)
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
			n\MaxGravity = 0.03
			
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
		Case NPCTypeD, NPCTypeClerk
			;[Block]
			Local Name$ = "Class D"
			Local ModelID% = NPC_CLASS_D_MODEL
			
			If n\NPCType = NPCTypeClerk
				Name = "Clerk"
				ModelID = NPC_CLERK_MODEL
			EndIf
			
			n\NVGName = GetLocalString("npc", "human")
			n\Speed = IniGetFloat(NPCsFile, Name, "Speed") / 100.0
			
			n\Collider = CreatePivot()
			EntityRadius(n\Collider, n\CollRadius)
			EntityType(n\Collider, HIT_PLAYER)
			
			n\OBJ = CopyEntity(n_I\NPCModelID[ModelID])
			Temp = IniGetFloat(NPCsFile, Name, "Scale") / MeshWidth(n\OBJ)
			ScaleEntity(n\OBJ, Temp, Temp, Temp)
			MeshCullBox(n\OBJ, -MeshWidth(n\OBJ), -MeshHeight(n\OBJ), -MeshDepth(n\OBJ), MeshWidth(n\OBJ) * 2.0, MeshHeight(n\OBJ) * 2.0, MeshDepth(n\OBJ) * 2.0)
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
	Local Temp#
	
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
			PositionEntity(n\OBJ2, 0.0, 0.86, -0.094, True)
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
	
	Local n.NPCs
	
	For n.NPCs = Each NPCs
		; ~ A variable to determine if the NPC is in the facility or not
		n\InFacility = IsInFacility(EntityY(n\Collider))
		
		Select n\NPCType
			Case NPCType008_1
				;[Block]
				UpdateNPCType008_1(n)
				;[End Block]
			Case NPCType035_Tentacle
				;[Block]
				UpdateNPCType035_Tentacle(n)
				;[End Block]
			Case NPCType049
				;[Block]
				UpdateNPCType049(n)
				;[End Block]
			Case NPCType049_2
				;[Block]
				UpdateNPCType049_2(n)
				;[End Block]
			Case NPCType066
				;[Block]
				UpdateNPCType066(n)
				;[End Block]
			Case NPCType096
				;[Block]
				UpdateNPCType096(n)
				;[End Block]
			Case NPCType106
				;[Block]
				UpdateNPCType106(n)
				;[End Block]
			Case NPCType173
				;[Block]
				UpdateNPCType173(n)
				;[End Block]
			Case NPCType372
				;[Block]
				UpdateNPCType372(n)
				;[End Block]
			Case NPCType513_1
				;[Block]
				UpdateNPCType513_1(n)
				;[End Block]
			Case NPCType860_2
				;[Block]
				UpdateNPCType860_2(n)
				;[End Block]
			Case NPCType939
				;[Block]
				UpdateNPCType939(n)
				;[End Block]
			Case NPCType966
				;[Block]
				UpdateNPCType966(n)
				;[End Block]
			Case NPCType1048
				;[Block]
				UpdateNPCType1048(n)
				;[End Block]
			Case NPCType1048_A
				;[Block]
				UpdateNPCType1048_A(n)
				;[End Block]
			Case NPCType1499_1
				;[Block]
				UpdateNPCType1499_1(n)
				;[End Block]
			Case NPCTypeD, NPCTypeClerk
				;[Block]
				UpdateNPCTypeD_Clerk(n)
				;[End Block]
			Case NPCTypeApache
				;[Block]
				UpdateNPCTypeApache(n)
				;[End Block]
			Case NPCTypeGuard ; ~ TODO: WRITE A NEW AI
				;[Block]
				UpdateNPCTypeGuard(n)
				;[End Block]
			Case NPCTypeMTF
				;[Block]
				UpdateNPCTypeMTF(n)
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
						If n\Frame > 548.9
							Local Pvt% = CreatePivot()
							
							PositionEntity(Pvt, EntityX(n\Collider), EntityY(n\Collider), EntityZ(n\Collider))
							TurnEntity(Pvt, 90.0, 0.0, 0.0)
							If EntityPick(Pvt, 0.5)
								Local de.Decals = CreateDecal(DECAL_CORROSIVE_2, EntityX(n\Collider), PickedY() + 0.005, EntityZ(n\Collider), 90.0, Rnd(360.0), 0.0, 0.5, 1.0)
								
								de\SizeChange = 0.0005 : de\MaxSize = 0.2
								EntityParent(de\OBJ, PlayerRoom\OBJ)
							EndIf
							FreeEntity(Pvt) : Pvt = 0
							PlaySoundEx(LoadTempSound("SFX\Room\PocketDimension\Impact.ogg"), Camera, n\Collider, 4.0, 0.8)
							
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
						
						n\HideFromNVG = True
						n\GravityMult = 0.0
						;[End Block]
					Case NPCTypeGuard
						;[Block]
						n\OBJ3 = CreatePivot(FindChild(n\OBJ, "Thumb01.R.001"))
						EntityRadius(n\OBJ3, 0.35)
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
			If n\NPCType = NPCTypeMTF
				If n_I\MTFLeader = Null Then n_I\MTFLeader = n
				If n_I\MTFCoLeader = Null And n <> n_I\MTFLeader Then n_I\MTFCoLeader = n
			EndIf
			If n_I\MTFLeader = Null And n_I\MTFCoLeader = Null And MTFTimer > 20000.0 And MTFTimer < 31000.0
				PlayAnnouncement("SFX\Character\MTF\AnnouncLost.ogg")
				MTFTimer = 31000.0
			EndIf
			If GravityDist < PowTwo(HideDistance * 0.6) Lor n\NPCType = NPCType1499_1
				If n\InFacility = InFacility
					TranslateEntity(n\Collider, 0.0, n\DropSpeed, 0.0)
					
					Local r.Rooms
					Local CollidedFloor% = False
					Local CollCount% = CountCollisions(n\Collider)
					Local i%
					
					For i = 1 To CollCount
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
									If forest_event <> Null And forest_event\room = PlayerRoom
										If forest_event\EventState = 1.0 Then UpdateGravity = True
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

Function TeleportCloser%(n.NPCs)
	Local ClosestDist# = 0.0
	Local closestWaypoint.WayPoints
	Local w.WayPoints
	Local Dist#
	
	If InFacility > UpperFloor Then Return
	
	For w.WayPoints = Each WayPoints
		If w\door = Null
			Dist = DistanceSquared(EntityX(w\OBJ, True), EntityX(n\Collider, True), EntityZ(w\OBJ, True), EntityZ(n\Collider, True))
			If Dist > 1.0 And Dist < 100.0
				If EntityDistanceSquared(me\Collider, w\OBJ) > PowTwo(16.0 - (6.0 * SelectedDifficulty\AggressiveNPCs))
					; ~ Teleports to the nearby waypoint that takes it closest to the player
					Local NewDist# = EntityDistanceSquared(me\Collider, w\OBJ)
					
					If NewDist < ClosestDist Lor closestWaypoint = Null
						ClosestDist = NewDist
						closestWaypoint = w
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	
	If closestWaypoint <> Null
		Local ShouldTeleport% = False
		
		If n\InFacility <> NullFloor Lor SelectedDifficulty\AggressiveNPCs Then
			ShouldTeleport = True
		ElseIf EntityY(closestWaypoint\OBJ, True) <= 6.5 And EntityY(closestWaypoint\OBJ, True) >= -6.5
			ShouldTeleport = True
		EndIf
		If ShouldTeleport
			TeleportEntity(n\Collider, EntityX(closestWaypoint\OBJ, True), EntityY(closestWaypoint\OBJ, True) + 0.2, EntityZ(closestWaypoint\OBJ, True), n\CollRadius, True)
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
			Local SoundVol173# = Clamp(Distance(EntityX(n_I\Curr173\Collider), n_I\Curr173\PrevX, EntityZ(n_I\Curr173\Collider), n_I\Curr173\PrevZ) * 2.5, 0.0, 1.0)
			
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
	
	If I_268\InvisibilityOn Lor chs\NoTarget
		n\State2 = 0.0
		Return(0)
	EndIf
	
	Local Dist2# = EntityDistanceSquared(me\Collider, n\Collider)
	
	If n\NPCType <> NPCTypeMTF
		If Dist2 > PowTwo(Dist) Lor n\BlinkTimer <= 0.0
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
	Return(Not ((Not chs\NoTarget) And (wi\IsNVGBlinking Lor (Not (EntityInView(n\OBJ, Camera) Lor EntityInView(n\OBJ2, Camera))) Lor (me\BlinkTimer > -16.0 And me\BlinkTimer < -6.0))))
End Function

Function UpdateNPCBlinking%(n.NPCs)
	If n\BlinkTimer =< 0.0 Then n\BlinkTimer = 70.0 * Rnd(5.0, 10.0)
	n\BlinkTimer = n\BlinkTimer - fps\Factor[0]
End Function

Function Shoot%(x#, y#, z#, Parent% = 0, HitProb# = 1.0, Particles% = True, InstaKill% = False)
	Local p.Particles, de.Decals, n.NPCs, emit.Emitter
	Local Pvt%, ShotMessageUpdate$, i%
	
	emit.Emitter = SetEmitter(Null, x, y, z, 13)
	EntityParent(emit\Owner, Parent)
	LightVolume = TempLightVolume * 1.2
	
	If InstaKill
		PlaySound_Strict(snd_I\BulletHitSFX)
		Kill(True)
		Return
	EndIf
	
	If Rnd(1.0) <= HitProb
		Local MsgRand% = Rand(17)
		
		TurnEntity(Camera, Rnd(-3.0, 3.0), Rnd(-3.0, 3.0), 0.0)
		Select MsgRand
			Case 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ; ~ Vest
				;[Block]
				If wi\BallisticVest <> 2 Then me\Stamina = me\Stamina - (Rnd(5.0) * (I_1025\FineState[3] = 0.0))
				InjurePlayer(Rnd(0.61, 0.72) * DifficultyDMGMult, 0.0, Rnd(200.0, 300.0), 0.43 * DifficultyDMGMult)
				If wi\BallisticVest > 0
					ShotMessageUpdate = GetLocalString("msg", "bullet.vest")
				Else
					ShotMessageUpdate = GetLocalString("msg", "bullet.body")
				EndIf
				;[End Block]
			Case 11 ; ~ Left Leg
				;[Block]
				me\Stamina = me\Stamina - (Rnd(10.0) * (I_1025\FineState[3] = 0.0))
				InjurePlayer(Rnd(0.44, 0.54) * DifficultyDMGMult, 0.0, Rnd(400.0, 500.0))
				ShotMessageUpdate = GetLocalString("msg", "bullet.leg.left")
				;[End Block]
			Case 12 ; ~ Right Leg
				;[Block]
				me\Stamina = me\Stamina - (Rnd(10.0) * (I_1025\FineState[3] = 0.0))
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
					me\Injuries = me\Injuries + 0.1
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
			PlaySoundEx(snd_I\BulletMissSFX, Camera, Pvt, 0.4, Rnd(0.8, 1.0))
			
			If Particles
				Local PX# = PickedX()
				Local PY# = PickedY()
				Local PZ# = PickedZ()
				
				p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, PX, PY, PZ, 0.03, 0.0, 80.0)
				p\Speed = 0.001 : p\SizeChange = 0.003 : p\Alpha = 0.8 : p\AlphaChange = -0.01
				RotateEntity(p\Pvt, EntityPitch(Pvt) - 180.0, EntityYaw(Pvt), 0)
				
				For i = 0 To Rand(2, 3)
					p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, PX, PY, PZ, 0.006, 0.003, 80.0)
					p\Speed = 0.02 : p\Alpha = 0.8 : p\AlphaChange = -0.01
					RotateEntity(p\Pvt, EntityPitch(Pvt) + Rnd(170.0, 190.0), EntityYaw(Pvt) + Rnd(-10.0, 10.0), 0.0)
				Next
				
				de.Decals = CreateDecal(Rand(DECAL_BULLET_HOLE_1, DECAL_BULLET_HOLE_2), PX, PY + Rnd(-0.05, 0.05), PZ, Rnd(-4.0, 4.0), Rnd(-4.0, 4.0), Rnd(-4.0, 4.0), Rnd(0.028, 0.034), 1.0, 1, 2)
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
	
	n_I\Curr106\Idle = 1 ; ~ Make SCP-106 idle for a while
	For r.Rooms = Each Rooms
		If r\RoomTemplate\RoomID = r_dimension_106
			me\BlinkTimer = -10.0 : me\FallTimer = 0.0 : me\DropSpeed = 0.0 : me\Playable = True
			me\Injuries = me\Injuries + 0.5
			me\BlurTimer = 1750.0
			HideEntity(me\Head)
			ShowEntity(me\Collider)
			PlaySound_Strict(snd_I\Use914SFX)
			PlaySound_Strict(snd_I\SCP106SFX[5])
			n_I\Curr106\Idle = 0
			
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
	Local PlayerPosX#, PlayerPosY#, PlayerPosZ#
	
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
			GiveAchievement("049")
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
			GiveAchievement("096")
			ConsoleMsg = Format(GetLocalString("console", "spawn"), "SCP-096")
			;[End Block]
		Case "106", "scp106", "scp-106", "larry", "oldman"
			;[Block]
			PlayerPosX = EntityX(me\Collider) : PlayerPosY = EntityY(me\Collider) : PlayerPosZ = EntityZ(me\Collider)
			n.NPCs = CreateNPC(NPCType106, PlayerPosX, PlayerPosY, PlayerPosZ)
			n\EnemyX = PlayerPosX : n\EnemyY = PlayerPosY : n\EnemyZ = PlayerPosZ
			n\State = 2.0
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
			;[Block]
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
			;[Block]
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
		Case "tentacle", "035tentacle", "scp035tentacle", "scp-035tentacle", "scp-035-tentacle", "scp035-tentacle"
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
				If Bone = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "spawn.bone.notexist"), BoneName))
				PositionEntity(Pvt, EntityX(Bone, True), EntityY(Bone, True), EntityZ(Bone, True))
				Select n\ManipulationType
					Case 0 ; ~ Looking at player
						;[Block]
						Local ArrayTo% = GetNPCManipulationValue(n\NPCNameInSection, n\BoneToManipulate, "controller_max", 1)
						
						For i = 1 To ArrayTo
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
								n\BonePitch = Clamp(n\BonePitch, MinValue, MaxValue)
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
								n\BoneYaw = Clamp(n\BoneYaw, MinValue, MaxValue)
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
	Return(PlayerRoom\RoomTemplate\RoomID = r_gate_a Lor PlayerRoom\RoomTemplate\RoomID = r_gate_b)
End Function

Function PlayerInReachableRoom%(CanSpawnIn049Chamber% = False, Intro% = False)
	Local e.Events
	
	; ~ Player is in these rooms, returning false
	If PlayerRoom\RoomTemplate\RoomID = r_dimension_106 Lor PlayerRoom\RoomTemplate\RoomID = r_dimension_1499 Lor (PlayerRoom\RoomTemplate\RoomID = r_cont1_173_intro And (Not Intro)) Lor IsPlayerOutsideFacility() Then Return(False)
	; ~ Player is in SCP-860-1, returning false
	If forest_event <> Null And forest_event\room = PlayerRoom
		If forest_event\EventState = 1.0 Then Return(False)
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
					If PlaySFX Then PlaySoundEx(NPCSound[SOUND_NPC_MTF_BEEP], Camera, n\OBJ, 8.0)
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
						If n\NPCType = NPCType049 Then n\Path[n\PathLocation]\door\TimerState = 70.0 * 2.5
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
	Local NewTime#
	
	If Speed > 0.0
		NewTime = Clamp(n\Frame + Speed * fps\Factor[0], FirstFrame, LastFrame)
		
		If Loop And NewTime >= LastFrame Then NewTime = FirstFrame
	Else
		If FirstFrame < LastFrame
			Local Temp# = FirstFrame
			
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
			NewTime = Clamp(n\Frame + Speed * fps\Factor[0], LastFrame, FirstFrame)
		EndIf
	EndIf
	SetNPCFrame(n, NewTime)
End Function

Function AnimateEx#(Entity%, Curr#, FirstFrame%, LastFrame%, Speed#, Loop% = True)
	Local NewTime#
	
	If Speed > 0.0
		NewTime = Clamp(Curr + Speed * fps\Factor[0], FirstFrame, LastFrame)
		
		If Loop And NewTime >= LastFrame Then NewTime = FirstFrame
	Else
		If FirstFrame < LastFrame
			Local Temp# = FirstFrame
			
			FirstFrame = LastFrame
			LastFrame = Temp
		EndIf
		
		If Loop
			NewTime = Curr + Speed * fps\Factor[0]
			
			If NewTime < LastFrame
				NewTime = FirstFrame
			ElseIf NewTime > FirstFrame
				NewTime = LastFrame
			EndIf
		Else
			NewTime = Clamp(Curr + Speed * fps\Factor[0], LastFrame, FirstFrame)
		EndIf
	EndIf
	
	SetAnimTime(Entity, NewTime)
	
	Return(NewTime)
End Function

Function FinishWalking%(n.NPCs, StartFrame#, EndFrame#, Speed#)
	If n <> Null
		Local CenterFrame# = (EndFrame - StartFrame) / 2.0
		
		If n\Frame >= CenterFrame
			AnimateNPC(n, StartFrame, EndFrame, Speed, False)
		Else
			AnimateNPC(n, EndFrame, StartFrame, -Speed, False)
		EndIf
	EndIf
End Function

Function ChangeNPCTextureID%(n.NPCs, TextureID%)
	If n = Null
		OpenConsoleOnError(GetLocalString("msg", "spawn.invaildtex"))
		Return
	EndIf
	
	n\TextureID = TextureID + 1
	EntityTexture(n\OBJ, n_I\NPCTextureID[TextureID])
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS