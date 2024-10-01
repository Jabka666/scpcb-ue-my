Const MaxDecalTextureIDAmount% = 23

Type DecalInstance
	Field DecalTextureID%[MaxDecalTextureIDAmount]
End Type

Global de_I.DecalInstance

; ~ Decal Texture ID Constants
;[Block]
Const DECAL_CORROSIVE_1% = 0
Const DECAL_CORROSIVE_2% = 1

Const DECAL_BLOOD_1% = 2
Const DECAL_BLOOD_2% = 3
Const DECAL_BLOOD_3% = 4
Const DECAL_BLOOD_4% = 5
Const DECAL_BLOOD_5% = 6
Const DECAL_BLOOD_6% = 7

Const DECAL_PD_1% = 8
Const DECAL_PD_2% = 9
Const DECAL_PD_3% = 10
Const DECAL_PD_4% = 11
Const DECAL_PD_5% = 12
Const DECAL_PD_6% = 13

Const DECAL_BULLET_HOLE_1% = 14
Const DECAL_BULLET_HOLE_2% = 15

Const DECAL_BLOOD_DROP_1% = 16
Const DECAL_BLOOD_DROP_2% = 17

Const DECAL_427% = 18
Const DECAL_409% = 19

Const DECAL_WATER% = 20

Const DECAL_KETER% = 21
Const DECAL_APOLLYON% = 22
;[End Block]

Function LoadDecals%()
	Local i%
	
	de_I.DecalInstance = New DecalInstance
	
	For i = DECAL_CORROSIVE_1 To DECAL_CORROSIVE_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\corrosive_decal(" + i + ").png", 1 + 2, DeleteAllTextures, False)
	Next
	
	For i = DECAL_BLOOD_1 To DECAL_BLOOD_6
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\blood_decal(" + (i - DECAL_BLOOD_1) + ").png", 1 + 2, DeleteAllTextures, False)
	Next
	
	For i = DECAL_PD_1 To DECAL_PD_6
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\pd_decal(" + (i - DECAL_PD_1) + ").png", 1 + 2, DeleteAllTextures, False)
	Next
	
	For i = DECAL_BULLET_HOLE_1 To DECAL_BULLET_HOLE_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\bullet_hole_decal(" + (i - DECAL_BULLET_HOLE_1) + ").png", 1 + 2, DeleteAllTextures, False)
	Next
	
	For i = DECAL_BLOOD_DROP_1 To DECAL_BLOOD_DROP_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\blood_drop_decal(" + (i - DECAL_BLOOD_DROP_1) + ").png", 1 + 2, DeleteAllTextures, False)
	Next
	
	de_I\DecalTextureID[DECAL_409] = LoadTexture_Strict("GFX\Decals\scp_409_decal.png", 1 + 2, DeleteAllTextures, False)
	
	de_I\DecalTextureID[DECAL_427] = LoadTexture_Strict("GFX\Decals\scp_427_decal.png", 1 + 2, DeleteAllTextures, False)
	
	de_I\DecalTextureID[DECAL_WATER] = LoadTexture_Strict("GFX\Decals\water_decal.png", 1 + 2, DeleteAllTextures, False)
	
	If S2IMapContains(UnlockedAchievements, "keter") Then de_I\DecalTextureID[DECAL_KETER] = LoadTexture_Strict("GFX\Menu\Achievements\AchvKeter.png", 1, DeleteAllTextures, False)
	If S2IMapContains(UnlockedAchievements, "apollyon") Then de_I\DecalTextureID[DECAL_APOLLYON] = LoadTexture_Strict("GFX\Menu\Achievements\AchvApollyon.png", 1 + 2, DeleteAllTextures, False)
End Function

Const MaxParticleTextureIDAmount% = 9

Function RemoveDecalInstances%()
	Local i%
	
	For i = 0 To MaxDecalTextureIDAmount - 1
		de_I\DecalTextureID[i] = 0
	Next
	Delete(de_I) : de_I = Null
End Function

Type ParticleInstance
	Field ParticleTextureID%[MaxParticleTextureIDAmount]
End Type

Global p_I.ParticleInstance

; ~ Particles ID Constants
;[Block]
Const PARTICLE_BLACK_SMOKE% = 0
Const PARTICLE_WHITE_SMOKE% = 1

Const PARTICLE_FLASH% = 2

Const PARTICLE_DUST% = 3

Const PARTICLE_SHADOW% = 4

Const PARTICLE_SUN% = 5

Const PARTICLE_BLOOD% = 6

Const PARTICLE_SPARK% = 7

Const PARTICLE_WATER_DROP% = 8
;[End Block]

Function LoadParticles%()
	p_I.ParticleInstance = New ParticleInstance
	
	p_I\ParticleTextureID[PARTICLE_BLACK_SMOKE] = LoadTexture_Strict("GFX\Particles\smoke(0).png", 1 + 2, DeleteAllTextures, False)
	p_I\ParticleTextureID[PARTICLE_WHITE_SMOKE] = LoadTexture_Strict("GFX\Particles\smoke(1).png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_FLASH] = LoadTexture_Strict("GFX\Particles\flash.png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_DUST] = LoadTexture_Strict("GFX\Particles\dust.png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_SHADOW] = LoadTexture_Strict("GFX\NPCs\hg.pt", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_SUN] = LoadTexture_Strict("GFX\Map\Textures\sun.png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_BLOOD] = LoadTexture_Strict("GFX\Particles\blood.png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_SPARK] = LoadTexture_Strict("GFX\Particles\spark.png", 1 + 2, DeleteAllTextures, False)
	
	p_I\ParticleTextureID[PARTICLE_WATER_DROP] = LoadTexture_Strict("GFX\Particles\water_drop.png", 1 + 2, DeleteAllTextures, False)
	
	; ~ Black smoke in "room2c_gw_lcz"/"room2_6_hcz"/"cont1_035"
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[0], -1)
	SetTemplateParticleLifeTime(ParticleEffect[0], 53, 56)
	SetTemplateTexture(ParticleEffect[0], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[0], 0.0, 0.0, 0.05, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[0], -0.03, 0.03, -0.05, -0.04, -0.03, 0.03)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	SetTemplateSize(ParticleEffect[0], 0.02, 0.02, 1.5, 1.8)
	SetTemplateSizeVel(ParticleEffect[0], 0.025, 1.013)
	SetTemplateGravity(ParticleEffect[0], -0.001)
	
	; ~ White smoke in "room2_gw_2"
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], -1)
	SetTemplateParticleLifeTime(ParticleEffect[1], 7, 10)
	SetTemplateTexture(ParticleEffect[1], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[1], -0.03, 0.03, 0.0, 0.0, -0.03, 0.03)
	SetTemplateVelocity(ParticleEffect[1], -0.02, 0.02, 0.027, 0.045, -0.02, 0.02)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	SetTemplateSize(ParticleEffect[1], 0.18, 0.2, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], 0.02, 1.01)
	
	; ~ White smoke in "room2_gw/room3_gw"
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 70.0 * 3.0)
	SetTemplateParticleLifeTime(ParticleEffect[2], 35, 40)
	SetTemplateTexture(ParticleEffect[2], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[2], -0.2, 0.2, -0.1, 0.1, -0.2, 0.2)
	SetTemplateVelocity(ParticleEffect[2], -0.015, 0.015, -0.04, -0.035, -0.015, 0.015)
	SetTemplateAlphaVel(ParticleEffect[2], True)
	SetTemplateSize(ParticleEffect[2], 0.5, 0.5, 1.0, 1.5)
	SetTemplateSizeVel(ParticleEffect[2], 0.01, 1.01)
	
	; ~ Black smoke in "cont2c_096"/"room3_hcz"/"room4_hcz"/"room2_4_hcz"
	ParticleEffect[3] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[3], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[3], -1)
	SetTemplateParticleLifeTime(ParticleEffect[3], 70, 80)
	SetTemplateTexture(ParticleEffect[3], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[3], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[3], -0.01, 0.01, 0.061, 0.071, -0.01, 0.01)
	SetTemplateAlphaVel(ParticleEffect[3], True)
	SetTemplateSize(ParticleEffect[3], 0.02, 0.02, 1.0, 1.2)
	SetTemplateSizeVel(ParticleEffect[3], 0.01, 1.01)
	SetTemplateFloor(ParticleEffect[3], 1.3, 0.12, True)
	
	; ~ Black smoke in "room2_test_hcz"
	ParticleEffect[4] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[4], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[4], -1)
	SetTemplateParticleLifeTime(ParticleEffect[4], 70, 74)
	SetTemplateTexture(ParticleEffect[4], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[4], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[4], -0.01, 0.01, -0.086, -0.076, -0.01, 0.01)
	SetTemplateAlphaVel(ParticleEffect[4], True)
	SetTemplateSize(ParticleEffect[4], 0.022, 0.022, 1.2, 1.4)
	SetTemplateSizeVel(ParticleEffect[4], 0.01, 1.012)
	SetTemplateFloor(ParticleEffect[4], -4.45, 0.2, False)
	
	; ~ Black smoke in "cont1_173_intro"
	ParticleEffect[5] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[5], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[5], -1)
	SetTemplateParticleLifeTime(ParticleEffect[5], 28, 30)
	SetTemplateTexture(ParticleEffect[5], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[5], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[5], -0.01, 0.01, -0.035, -0.025, -0.01, 0.01)
	SetTemplateAlphaVel(ParticleEffect[5], True)
	SetTemplateSize(ParticleEffect[5], 0.01, 0.01, 1.0, 1.3)
	SetTemplateSizeVel(ParticleEffect[5], 0.0125, 1.012)
	
	; ~ Black smoke in "room3_storage"
	ParticleEffect[6] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[6], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[6], -1)
	SetTemplateParticleLifeTime(ParticleEffect[6], 50, 60)
	SetTemplateTexture(ParticleEffect[6], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[6], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[6], 0.04, 0.05, 0.01, 0.01, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[6], True)
	SetTemplateSize(ParticleEffect[6], 0.04, 0.04, 1.0, 1.2)
	SetTemplateSizeVel(ParticleEffect[6], 0.01, 1.01)
	SetTemplateGravity(ParticleEffect[6], -0.0005)
	
	; ~ White smoke in "cont1_173"
	ParticleEffect[7] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[7], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[7], -1)
	SetTemplateParticleLifeTime(ParticleEffect[7], 30, 45)
	SetTemplateTexture(ParticleEffect[7], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[7], -0.07, 0.07, -0.1, 0.1, -0.07, 0.07)
	SetTemplateVelocity(ParticleEffect[7], 0.0, 0.0, 0.02, 0.025, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[7], True)
	SetTemplateSize(ParticleEffect[7], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[7], 0.018, 1.01)
	
	; ~ Black smoke from Apache Helicopter
	ParticleEffect[8] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[8], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[8], -1)
	SetTemplateParticleLifeTime(ParticleEffect[8], 230, 250)
	SetTemplateTexture(ParticleEffect[8], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[8], -0.2, 0.2, -0.1, 0.1, -0.2, 0.2)
	SetTemplateVelocity(ParticleEffect[8], -0.05, 0.05, 0.05, 0.07, -0.05, 0.05)
	SetTemplateAlphaVel(ParticleEffect[8], True)
	SetTemplateSize(ParticleEffect[8], 0.02, 0.02, 1.0, 1.2)
	SetTemplateSizeVel(ParticleEffect[8], 0.01, 1.01)
	
	; ~ White smoke in "cont2_008"
	ParticleEffect[9] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[9], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[9], -1)
	SetTemplateInterval(ParticleEffect[9], 2)
	SetTemplateParticleLifeTime(ParticleEffect[9], 40, 45)
	SetTemplateTexture(ParticleEffect[9], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[9], -0.01, 0.01, 0.03, 0.04, -0.01, 0.01)
	SetTemplateVelocity(ParticleEffect[9], 0.0, 0.0, 0.025, 0.03, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[9], True)
	SetTemplateSize(ParticleEffect[9], 0.07, 0.07, 0.5, 1.0)
	SetTemplateSizeVel(ParticleEffect[9], 0.02, 1.02)
	SetTemplateFloor(ParticleEffect[9], -18.2, 0.1, 1)
	
	; ~ White smoke in "room2_nuke"
	ParticleEffect[10] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[10], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[10], -1)
	SetTemplateInterval(ParticleEffect[10], 40)
	SetTemplateParticlesPerInterval(ParticleEffect[10], 2)
	SetTemplateParticleLifeTime(ParticleEffect[10], 40, 45)
	SetTemplateTexture(ParticleEffect[10], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[10], -0.03, 0.03, 0.1, 0.15, -0.03, 0.03)
	SetTemplateVelocity(ParticleEffect[10], 0.0, 0.0, 0.02, 0.03, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[10], True)
	SetTemplateSize(ParticleEffect[10], 0.02, 0.02, 0.8, 1.0)
	SetTemplateSizeVel(ParticleEffect[10], 0.016, 1.01)
	
	; ~ Dust at blast doors
	ParticleEffect[11] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[11], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[11], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[11], 30)
	SetTemplateParticleLifeTime(ParticleEffect[11], 70, 80)
	SetTemplateTexture(ParticleEffect[11], PARTICLE_DUST)
	SetTemplateOffset(ParticleEffect[11], -0.2, 0.2, 0.0, 1.2, -0.2, 0.2)
	SetTemplateVelocity(ParticleEffect[11], -0.004, 0.004, -0.0001, 0.0001, -0.004, 0.004)
	SetTemplateSize(ParticleEffect[11], 0.01, 0.01, 1.0, 1.2)
	SetTemplateAlphaVel(ParticleEffect[11], True)
	
	; ~ A simple dust particle
	ParticleEffect[12] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[12], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[12], 1)
	SetTemplateParticleLifeTime(ParticleEffect[12], 100, 130)
	SetTemplateMaxParticles(ParticleEffect[12], 100)
	SetTemplateTexture(ParticleEffect[12], PARTICLE_DUST)
	SetTemplateOffset(ParticleEffect[12], -0.55, 0.55, -0.1, 0.3, -0.55, 0.55)
	SetTemplateVelocity(ParticleEffect[12], -0.001, 0.001, -0.001, 0.001, -0.001, 0.001)
	SetTemplateSize(ParticleEffect[12], 0.0018, 0.0018, 1.0, 1.25)
	SetTemplateAlphaVel(ParticleEffect[12], True)
	
	; ~ A simple flash particle
	ParticleEffect[13] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[13], 1)
	SetTemplateParticleLifeTime(ParticleEffect[13], 2, 3)
	SetTemplateTexture(ParticleEffect[13], PARTICLE_FLASH)
	SetTemplateOffset(ParticleEffect[13], -0.01, 0.01, -0.01, 0.01, -0.01, 0.01)
	SetTemplateSize(ParticleEffect[13], 0.08, 0.08, 0.8, 1.0)
	SetTemplateRotation(ParticleEffect[13], 0.0, 360.0)
	SetTemplateAlphaVel(ParticleEffect[13], True)
	
	; ~ Black smoke in tesla rooms
	ParticleEffect[14] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[14], 1)
	SetTemplateInterval(ParticleEffect[14], 20)
	SetTemplateEmitterLifeTime(ParticleEffect[14], 70)
	SetTemplateParticleLifeTime(ParticleEffect[14], 60, 70)
	SetTemplateTexture(ParticleEffect[14], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[14], -0.2, 0.2, 0.2, 0.8, -0.2, 0.2)
	SetTemplateVelocity(ParticleEffect[14], -0.003, 0.003, 0.005, 0.008, -0.003, 0.003)
	SetTemplateSize(ParticleEffect[14], 0.04, 0.04, 1.0, 1.2)
	SetTemplateSizeVel(ParticleEffect[14], 0.001, 1.0)
	SetTemplateAlphaVel(ParticleEffect[14], True)
	
	; ~ Blood sprites fountain
	ParticleEffect[15] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[15], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[15], 1)
	SetTemplateParticleLifeTime(ParticleEffect[15], 110, 120)
	SetTemplateTexture(ParticleEffect[15], PARTICLE_BLOOD)
	SetTemplateOffset(ParticleEffect[15], -0.1, 0.1, 0.1, 0.6, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[15], -0.015, 0.015, 0.015, 0.02, -0.015, 0.015)
	SetTemplateSize(ParticleEffect[15], 0.01, 0.01, 1.0, 2.0)
	SetTemplateAlphaVel(ParticleEffect[15], True)
	SetTemplateGravity(ParticleEffect[15], 0.0022)
	
	; ~ Long sparks effect (also used for fast opened door)
	ParticleEffect[16] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[16], 15)
	SetTemplateParticlesPerInterval(ParticleEffect[16], 30)
	SetTemplateParticleLifeTime(ParticleEffect[16], 60, 70)
	SetTemplateTexture(ParticleEffect[16], PARTICLE_SPARK)
	SetTemplateOffset(ParticleEffect[16], -0.03, 0.03, 0.0, 0.05, -0.03, 0.03)
	SetTemplateVelocity(ParticleEffect[16], -0.008, 0.008, -0.008, 0.008, -0.008, 0.008)
	SetTemplateSize(ParticleEffect[16], 0.006, 0.006, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[16], True)
	
	; ~ Water drop particle
	ParticleEffect[17] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[17], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[17], -1)
	SetTemplateParticlesPerInterval(ParticleEffect[17], 1)
	SetTemplateParticleLifeTime(ParticleEffect[17], 110, 120)
	SetTemplateTexture(ParticleEffect[17], PARTICLE_WATER_DROP)
	SetTemplateVelocity(ParticleEffect[17], -0.0001, 0.0001, -0.008, -0.005, -0.0001, 0.0001)
	SetTemplateSize(ParticleEffect[17], 0.008, 0.008, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[17], True)
	SetTemplateFloor(ParticleEffect[17], 0.4, 0.02, 0)
	
	; ~ Water sprinklet particle
	ParticleEffect[18] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[18], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[18], -1)
	SetTemplateParticlesPerInterval(ParticleEffect[18], 10)
	SetTemplateParticleLifeTime(ParticleEffect[18], 110, 120)
	SetTemplateTexture(ParticleEffect[18], PARTICLE_WATER_DROP)
	SetTemplateVelocity(ParticleEffect[18], -0.0025, 0.0025, -0.008, -0.005, -0.0025, 0.0025)
	SetTemplateSize(ParticleEffect[18], 0.008, 0.008, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[18], True)
	SetTemplateFloor(ParticleEffect[18], -13.2, 0.08, 0)
	
	; ~ Short sparks effect
	ParticleEffect[19] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[19], 1)
	SetTemplateParticleLifeTime(ParticleEffect[19], 50, 60)
	SetTemplateTexture(ParticleEffect[19], PARTICLE_SPARK)
	SetTemplateOffset(ParticleEffect[19], 0.0, 0.0, -0.01, 0.01, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[19], 0.005, 0.005, 1.0, 1.5)
	SetTemplateGravity(ParticleEffect[19], 0.0005)
End Function

Function RemoveParticleInstances%()
	Local i%
	
	For i = 0 To MaxParticleTextureIDAmount - 1
		p_I\ParticleTextureID[i] = 0
	Next
	Delete(p_I) : p_I = Null
End Function

Const MaxDoorModelIDAmount% = 9
Const MaxDoorFrameModelIDAmount% = 4
Const MaxButtonModelIDAmount% = 5
Const MaxButtonTextureIDAmount% = 4
Const MaxElevatorPanelTextureIDAmount% = 3

Type DoorInstance
	Field DoorModelID%[MaxDoorModelIDAmount]
	Field DoorFrameModelID%[MaxDoorFrameModelIDAmount]
	Field ButtonModelID%[MaxButtonModelIDAmount]
	Field ButtonTextureID%[MaxButtonTextureIDAmount]
	Field ElevatorPanelModel%
	Field ElevatorPanelTextureID%[MaxElevatorPanelTextureIDAmount%]
	Field SelectedDoor.Doors, ClosestDoor.Doors
	Field ClosestButton%, AnimButton%
End Type

Global d_I.DoorInstance

; ~ Door Model ID Constants
;[Block]
Const DOOR_DEFAULT_MODEL% = 0
Const DOOR_ELEVATOR_MODEL% = 1
Const DOOR_HEAVY_MODEL_1% = 2
Const DOOR_HEAVY_MODEL_2% = 3
Const DOOR_BIG_MODEL_1% = 4
Const DOOR_BIG_MODEL_2% = 5
Const DOOR_OFFICE_MODEL% = 6
Const DOOR_WOODEN_MODEL% = 7
Const DOOR_ONE_SIDED_MODEL% = 8
;[End Block]

; ~ Door Frame Model ID Constants
;[Block]
Const DOOR_DEFAULT_FRAME_MODEL% = 0
Const DOOR_BIG_FRAME_MODEL% = 1
Const DOOR_OFFICE_FRAME_MODEL% = 2
Const DOOR_WOODEN_FRAME_MODEL% = 3
;[End Block]

; ~ Button Model ID Constants
;[Block]
Const BUTTON_DEFAULT_MODEL% = 0
Const BUTTON_KEYCARD_MODEL% = 1
Const BUTTON_KEYPAD_MODEL% = 2
Const BUTTON_SCANNER_MODEL% = 3
Const BUTTON_ELEVATOR_MODEL% = 4
;[End Block]

; ~ Button Texture ID Constants
;[Block]
Const BUTTON_GREEN_TEXTURE% = 0
Const BUTTON_YELLOW_TEXTURE% = 1
Const BUTTON_RED_TEXTURE% = 2
Const BUTTON_106_TEXTURE% = 3
;[End Block]

; ~ Elevator Panel Texture ID Constants
;[Block]
Const ELEVATOR_PANEL_UP% = 0
Const ELEVATOR_PANEL_DOWN% = 1
Const ELEVATOR_PANEL_IDLE% = 2
;[End Block]

Function LoadDoors%()
	Local i%
	
	d_I.DoorInstance = New DoorInstance
	
	d_I\DoorModelID[DOOR_DEFAULT_MODEL] = LoadMesh_Strict("GFX\Map\Props\Door01.x")
	
	d_I\DoorModelID[DOOR_ELEVATOR_MODEL] = LoadMesh_Strict("GFX\Map\Props\ElevatorDoor.b3d")
	
	d_I\DoorModelID[DOOR_HEAVY_MODEL_1] = LoadMesh_Strict("GFX\Map\Props\HeavyDoor1.x")
	d_I\DoorModelID[DOOR_HEAVY_MODEL_2] = LoadMesh_Strict("GFX\Map\Props\HeavyDoor2.x")
	
	d_I\DoorModelID[DOOR_BIG_MODEL_1] = LoadMesh_Strict("GFX\Map\Props\contdoorleft.x")
	d_I\DoorModelID[DOOR_BIG_MODEL_2] = LoadMesh_Strict("GFX\Map\Props\contdoorright.x")
	
	d_I\DoorModelID[DOOR_OFFICE_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\officedoor.b3d")
	
	d_I\DoorModelID[DOOR_WOODEN_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorWooden.b3d")
	
	d_I\DoorModelID[DOOR_ONE_SIDED_MODEL] = LoadMesh_Strict("GFX\Map\Props\Door02.x")
	
	For i = 0 To MaxDoorModelIDAmount - 1
		HideEntity(d_I\DoorModelID[i])
	Next
	
	d_I\DoorFrameModelID[DOOR_DEFAULT_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorFrame.b3d")
	
	d_I\DoorFrameModelID[DOOR_BIG_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\ContDoorFrame.b3d")
	
	d_I\DoorFrameModelID[DOOR_OFFICE_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\officedoorframe.b3d")
	
	d_I\DoorFrameModelID[DOOR_WOODEN_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorWoodenFrame.b3d")
	
	For i = 0 To MaxDoorFrameModelIDAmount - 2 Step 2
		HideEntity(d_I\DoorFrameModelID[i])
		HideEntity(d_I\DoorFrameModelID[i + 1])
	Next
	
	d_I\ElevatorPanelModel = LoadMesh_Strict("GFX\Map\Props\elevator_panel.b3d")
	HideEntity(d_I\ElevatorPanelModel)
	
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_DOWN] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_down.png", 1, DeleteAllTextures)
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_UP] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_up.png", 1, DeleteAllTextures)
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_IDLE] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_idle.png", 1, DeleteAllTextures)
	
	d_I\ButtonModelID[BUTTON_DEFAULT_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\Button.b3d")
	
	d_I\ButtonModelID[BUTTON_KEYCARD_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonKeycard.b3d")
	
	d_I\ButtonModelID[BUTTON_KEYPAD_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonCode.b3d")
	
	d_I\ButtonModelID[BUTTON_SCANNER_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonScanner.b3d")
	
	d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\ButtonElevator.b3d")
	
	For i = 0 To MaxButtonModelIDAmount - 1
		HideEntity(d_I\ButtonModelID[i])
	Next
	
	d_I\ButtonTextureID[BUTTON_GREEN_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad.jpg", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_using.png", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_RED_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_locked.png", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_106_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_106.png", 1, DeleteAllTextures)
End Function

Function RemoveDoorInstances%()
	Local i%
	
	For i = 0 To MaxDoorModelIDAmount - 1
		FreeEntity(d_I\DoorModelID[i]) : d_I\DoorModelID[i] = 0
	Next
	For i = 0 To MaxDoorFrameModelIDAmount - 1
		FreeEntity(d_I\DoorFrameModelID[i]) : d_I\DoorFrameModelID[i] = 0
	Next
	FreeEntity(d_I\ElevatorPanelModel) : d_I\ElevatorPanelModel = 0
	For i = 0 To MaxButtonModelIDAmount - 1
		FreeEntity(d_I\ButtonModelID[i]) : d_I\ButtonModelID[i] = 0
	Next
	For i = ELEVATOR_PANEL_DOWN To ELEVATOR_PANEL_IDLE
		d_I\ElevatorPanelTextureID[i] = 0
	Next
	For i = BUTTON_GREEN_TEXTURE To BUTTON_106_TEXTURE
		d_I\ButtonTextureID[i] = 0
	Next
	Delete(d_I) : d_I = Null
End Function

Const MaxLeverModelIDAmount% = 2

Type LeverInstance
	Field LeverModelID%[MaxLeverModelIDAmount]
End Type

Global lvr_I.LeverInstance

; ~ Lever Model ID Constants
;[Block]
Const LEVER_BASE_MODEL% = 0
Const LEVER_HANDLE_MODEL% = 1
;[End Block]

Function LoadLevers%()
	Local i%
	
	lvr_I.LeverInstance = New LeverInstance
	
	lvr_I\LeverModelID[LEVER_BASE_MODEL] = LoadMesh_Strict("GFX\Map\Props\LeverBase.b3d")
	
	lvr_I\LeverModelID[LEVER_HANDLE_MODEL] = LoadMesh_Strict("GFX\Map\Props\LeverHandle.b3d")
	
	For i = 0 To MaxLeverModelIDAmount - 1
		HideEntity(lvr_I\LeverModelID[i])
	Next
End Function

Function RemoveLeverInstances%()
	Local i%
	
	For i = 0 To MaxLeverModelIDAmount - 1
		FreeEntity(lvr_I\LeverModelID[i]) : lvr_I\LeverModelID[i] = 0
	Next
	Delete(lvr_I) : lvr_I = Null
End Function

Const MaxCamModelIDAmount% = 2
Const MaxCamTextureIDAmount% = 2

Type SecurityCamInstance
	Field CamModelID%[MaxCamModelIDAmount]
	Field CamTextureID%[MaxCamTextureIDAmount]
	Field ScreenTex%
	Field SelectedMonitor.SecurityCams
	Field CoffinCam.SecurityCams
End Type

Global sc_I.SecurityCamInstance

; ~ Cam Model ID Constants
;[Block]
Const CAM_BASE_MODEL% = 0
Const CAM_HEAD_MODEL% = 1
;[End Block]

; ~ Cam Texture ID Constants
;[Block]
Const CAM_HEAD_DEFAULT_TEXTURE% = 0
Const CAM_HEAD_RED_LIGHT_TEXTURE% = 1
;[End Block]

Function LoadSecurityCams%()
	Local i%
	
	sc_I.SecurityCamInstance = New SecurityCamInstance
	
	sc_I\CamModelID[CAM_BASE_MODEL] = LoadMesh_Strict("GFX\Map\Props\CamBase.b3d")
	sc_I\CamModelID[CAM_HEAD_MODEL] = LoadMesh_Strict("GFX\Map\Props\CamHead.b3d")
	
	For i = 0 To MaxCamModelIDAmount - 1
		HideEntity(sc_I\CamModelID[i])
	Next
	
	sc_I\ScreenTex = CreateTextureUsingCacheSystem(512, 512)
	
	For i = CAM_HEAD_DEFAULT_TEXTURE To CAM_HEAD_RED_LIGHT_TEXTURE
		sc_I\CamTextureID[i] = LoadTexture_Strict("GFX\Map\Textures\camera(" + (i + 1) + ").png", 1, DeleteAllTextures)
	Next
End Function

Function RemoveSecurityCamInstances%()
	Local i%
	
	For i = 0 To MaxCamModelIDAmount - 1
		FreeEntity(sc_I\CamModelID[i]) : sc_I\CamModelID[i] = 0
	Next
	sc_I\ScreenTex = 0
	For i = CAM_HEAD_DEFAULT_TEXTURE To CAM_HEAD_RED_LIGHT_TEXTURE
		sc_I\CamTextureID[i] = 0
	Next
	Delete(sc_I) : sc_I = Null
End Function

Const MaxMonitorModelIDAmount% = 2
Const MaxMonitorOverlayIDAmount% = 18

Type MonitorInstance
	Field MonitorModelID%[MaxMonitorModelIDAmount]
	Field MonitorOverlayID%[MaxMonitorOverlayIDAmount]
	Field MonitorTimer#[2]
	Field UpdateCheckpoint%[2]
End Type

Global mon_I.MonitorInstance

; ~ Monitor Model ID Constants
;[Block]
Const MONITOR_DEFAULT_MODEL% = 0
Const MONITOR_CHECKPOINT_MODEL% = 1
;[End Block]

; ~ Monitor Overlay ID Constants
;[Block]
Const MONITOR_DEFAULT_OVERLAY% = 0
Const MONITOR_LOCKDOWN_1_OVERLAY% = 1
Const MONITOR_LOCKDOWN_2_OVERLAY% = 2
Const MONITOR_LOCKDOWN_3_OVERLAY% = 3
Const MONITOR_LOCKDOWN_4_OVERLAY% = 4
Const MONITOR_079_OVERLAY_1% = 5
Const MONITOR_079_OVERLAY_2% = 6
Const MONITOR_079_OVERLAY_3% = 7
Const MONITOR_079_OVERLAY_4% = 8
Const MONITOR_079_OVERLAY_5% = 9
Const MONITOR_079_OVERLAY_6% = 10
Const MONITOR_079_OVERLAY_7% = 11
Const MONITOR_895_OVERLAY_1% = 12
Const MONITOR_895_OVERLAY_2% = 13
Const MONITOR_895_OVERLAY_3% = 14
Const MONITOR_895_OVERLAY_4% = 15
Const MONITOR_895_OVERLAY_5% = 16
;[End Block]

Function LoadMonitors%()
	Local i%
	
	mon_I.MonitorInstance = New MonitorInstance
	
	mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL] = LoadMesh_Strict("GFX\Map\Props\monitor2.b3d")
	mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL] = LoadMesh_Strict("GFX\Map\Props\monitor_checkpoint.b3d")
	
	For i = 0 To MaxMonitorModelIDAmount - 1
		HideEntity(mon_I\MonitorModelID[i])
	Next
	
	mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY] = LoadTexture_Strict("GFX\Overlays\monitor_overlay.png", 1, DeleteAllTextures, False)
	For i = MONITOR_LOCKDOWN_1_OVERLAY To MONITOR_LOCKDOWN_3_OVERLAY
		mon_I\MonitorOverlayID[i] = LoadTexture_Strict("GFX\Map\Textures\lockdown_screen(" + i + ").png", 1, DeleteAllTextures, False)
	Next
	mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_4_OVERLAY] = CreateTextureUsingCacheSystem(1, 1)
	SetBuffer(TextureBuffer(mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_4_OVERLAY]))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	
	For i = MONITOR_079_OVERLAY_1 To MONITOR_079_OVERLAY_7
		mon_I\MonitorOverlayID[i] = LoadTexture_Strict("GFX\Overlays\scp_079_overlay(" + (i - 4) + ").png", 1, DeleteAllTextures, False)
	Next
	
	For i = MONITOR_895_OVERLAY_1 To MONITOR_895_OVERLAY_5
		mon_I\MonitorOverlayID[i] = LoadTexture_Strict("GFX\Overlays\scp_895_overlay(" + (i - 11) + ").png", 1, DeleteAllTextures, False)
	Next
End Function

Function RemoveMonitorInstances%()
	Local i%
	
	For i = 0 To MaxMonitorModelIDAmount - 1
		FreeEntity(mon_I\MonitorModelID[i]) : mon_I\MonitorModelID[i] = 0
	Next
	mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY] = 0
	For i = MONITOR_LOCKDOWN_1_OVERLAY To MONITOR_LOCKDOWN_3_OVERLAY
		mon_I\MonitorOverlayID[i] = 0
	Next
	For i = MONITOR_079_OVERLAY_1 To MONITOR_079_OVERLAY_7
		mon_I\MonitorOverlayID[i] = 0
	Next
	For i = MONITOR_895_OVERLAY_1 To MONITOR_895_OVERLAY_5
		mon_I\MonitorOverlayID[i] = 0
	Next
	Delete(mon_I) : mon_I = Null
End Function

Const MaxNPCModelIDAmount% = 32
Const MaxNPCTextureID% = 19

Type NPCInstance
	Field NPCModelID%[MaxNPCModelIDAmount]
	Field NPCTextureID%[MaxNPCTextureID]
	Field Curr173.NPCs
	Field Curr106.NPCs
	Field Curr096.NPCs
	Field Curr513_1.NPCs
	Field Curr049.NPCs
	Field Curr066.NPCs
	Field MTFLeader.NPCs, MTFCoLeader.NPCs
	Field IsHalloween%, IsNewYear%, IsAprilFools%
End Type

Global n_I.NPCInstance
; ~ NPC Model ID Constants
;[Block]
Const NPC_008_1_MODEL% = 0
Const NPC_035_TENTACLE_MODEL% = 1
Const NPC_049_MODEL% = 2
Const NPC_049_2_MODEL% = 3
Const NPC_066_MODEL% = 4
Const NPC_096_MODEL% = 5
Const NPC_106_MODEL% = 6
Const NPC_173_MODEL% = 7
Const NPC_173_HEAD_MODEL% = 8
Const NPC_173_BOX_MODEL% = 9
Const NPC_205_DEMON_1_MODEL% = 10
Const NPC_205_DEMON_2_MODEL% = 11
Const NPC_205_DEMON_3_MODEL% = 12
Const NPC_205_WOMAN_MODEL% = 13
Const NPC_372_MODEL% = 14
Const NPC_513_1_MODEL% = 15
Const NPC_860_2_MODEL% = 16
Const NPC_939_MODEL% = 17
Const NPC_966_MODEL% = 18
Const NPC_1048_MODEL% = 19
Const NPC_1048_A_MODEL% = 20
Const NPC_1499_1_MODEL% = 21
Const NPC_APACHE_MODEL% = 22
Const NPC_APACHE_ROTOR_1_MODEL% = 23
Const NPC_APACHE_ROTOR_2_MODEL% = 24
Const NPC_CLERK_MODEL% = 25
Const NPC_CLASS_D_MODEL% = 26
Const NPC_DUCK_MODEL% = 27
Const NPC_GUARD_MODEL% = 28
Const NPC_MTF_MODEL% = 29
Const NPC_NAZI_MODEL% = 30
Const NPC_VEHICLE_MODEL% = 31
;[End Block]

; ~ NPC Texture ID Constants
;[Block]
Const NPC_CLASS_D_GONZALES_TEXTURE% = 0
Const NPC_CLASS_D_BENJAMIN_TEXTURE% = 1
Const NPC_CLASS_D_SCIENTIST_TEXTURE% = 2
Const NPC_CLASS_D_FRANKLIN_TEXTURE% = 3
Const NPC_CLASS_D_MAYNARD_TEXTURE% = 4
Const NPC_CLASS_D_CLASS_D_TEXTURE% = 5
Const NPC_CLASS_D_D9341_TEXTURE% = 6
Const NPC_CLASS_D_BODY_1_TEXTURE% = 7
Const NPC_CLASS_D_BODY_2_TEXTURE% = 8
Const NPC_CLASS_D_JANITOR_TEXTURE% = 9
Const NPC_CLASS_D_MAINTENANCE% = 10
Const NPC_CLASS_D_VICTIM_008_TEXTURE% = 11
Const NPC_CLASS_D_VICTIM_035_TEXTURE% = 12
Const NPC_CLASS_D_VICTIM_409_TEXTURE% = 13
Const NPC_CLASS_D_VICTIM_939_1_TEXTURE% = 14
Const NPC_CLASS_D_VICTIM_939_2_TEXTURE% = 15
Const NPC_CLERK_VICTIM_205_TEXTURE% = 16

Const NPC_096_BLOODY_TEXTURE% = 17

Const NPC_1499_1_KING_TEXTURE% = 18
;[End Block]

Function LoadNPCs%()
	Local i%
	
	n_I.NPCInstance = New NPCInstance
	
	n_I\NPCModelID[NPC_008_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_008_1.b3d")
	
	n_I\NPCModelID[NPC_035_TENTACLE_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_035_tentacle.b3d")
	
	n_I\NPCModelID[NPC_049_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_049.b3d")
	
	n_I\NPCModelID[NPC_049_2_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_049_2.b3d")
	
	n_I\NPCModelID[NPC_066_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_066.b3d")
	
	n_I\NPCModelID[NPC_096_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_096.b3d")
	
	n_I\NPCModelID[NPC_106_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_106.b3d")
	
	n_I\NPCModelID[NPC_173_MODEL] = LoadMesh_Strict("GFX\NPCs\scp_173_body.b3d")
	n_I\NPCModelID[NPC_173_HEAD_MODEL] = LoadMesh_Strict("GFX\NPCs\scp_173_head.b3d")
	n_I\NPCModelID[NPC_173_BOX_MODEL] = LoadMesh_Strict("GFX\NPCs\scp_173_box.b3d")
	
	n_I\NPCModelID[NPC_205_DEMON_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_205_demon.b3d")
	n_I\NPCModelID[NPC_205_DEMON_2_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_205_demon(2).b3d")
	n_I\NPCModelID[NPC_205_DEMON_3_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_205_demon(3).b3d")
	n_I\NPCModelID[NPC_205_WOMAN_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_205_woman.b3d")
	
	n_I\NPCModelID[NPC_372_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_372.b3d")
	
	n_I\NPCModelID[NPC_513_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_513_1.b3d")
	
	n_I\NPCModelID[NPC_860_2_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_860_2.b3d")
	
	n_I\NPCModelID[NPC_939_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_939.b3d")
	
	n_I\NPCModelID[NPC_966_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_966.b3d")
	
	n_I\NPCModelID[NPC_1048_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1048.b3d")
	n_I\NPCModelID[NPC_1048_A_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1048_a.b3d")
	
	n_I\NPCModelID[NPC_1499_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1499_1.b3d")
	
	n_I\NPCModelID[NPC_APACHE_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache.b3d")
	n_I\NPCModelID[NPC_APACHE_ROTOR_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache_rotor.b3d")
	n_I\NPCModelID[NPC_APACHE_ROTOR_2_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache_rotor(2).b3d")
	
	n_I\NPCModelID[NPC_CLERK_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\clerk.b3d")
	
	n_I\NPCModelID[NPC_CLASS_D_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\class_d.b3d")
	
	n_I\NPCModelID[NPC_DUCK_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\duck.b3d")
	
	n_I\NPCModelID[NPC_GUARD_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\guard.b3d")
	
	n_I\NPCModelID[NPC_MTF_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\MTF.b3d")
	
	n_I\NPCModelID[NPC_NAZI_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\nazi_officer.b3d")
	
	n_I\NPCModelID[NPC_VEHICLE_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\vehicle.b3d")
	
	For i = 0 To MaxNPCModelIDAmount - 2 Step 2
		HideEntity(n_I\NPCModelID[i])
		HideEntity(n_I\NPCModelID[i + 1])
	Next
	
	n_I\NPCTextureID[NPC_CLASS_D_GONZALES_TEXTURE] = LoadTexture_Strict("GFX\NPCs\Gonzales.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_BENJAMIN_TEXTURE] = LoadTexture_Strict("GFX\NPCs\D_9341(2).png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_SCIENTIST_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scientist.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_FRANKLIN_TEXTURE] = LoadTexture_Strict("GFX\NPCs\Franklin.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_MAYNARD_TEXTURE] = LoadTexture_Strict("GFX\NPCs\Maynard.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_CLASS_D_TEXTURE] = LoadTexture_Strict("GFX\NPCs\class_d(2).png", 1, DeleteAllTextures)
	If opt\IntroEnabled Then n_I\NPCTextureID[NPC_CLASS_D_D9341_TEXTURE] = LoadTexture_Strict("GFX\NPCs\D_9341.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_BODY_1_TEXTURE] = LoadTexture_Strict("GFX\NPCs\body.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_BODY_2_TEXTURE] = LoadTexture_Strict("GFX\NPCs\body(2).png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_JANITOR_TEXTURE] = LoadTexture_Strict("GFX\NPCs\janitor.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_MAINTENANCE] = LoadTexture_Strict("GFX\NPCs\maintenance.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_VICTIM_008_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_008_1_victim.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_VICTIM_035_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_035_victim.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_VICTIM_409_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_409_victim.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_VICTIM_939_1_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_939_victim.png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLASS_D_VICTIM_939_2_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_939_victim(2).png", 1, DeleteAllTextures)
	n_I\NPCTextureID[NPC_CLERK_VICTIM_205_TEXTURE] = LoadTexture_Strict("GFX\NPCs\clerk(2).png", 1, DeleteAllTextures)
	
	n_I\NPCTextureID[NPC_096_BLOODY_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_096_bloody.png", 1, DeleteAllTextures)
	
	n_I\NPCTextureID[NPC_1499_1_KING_TEXTURE] = LoadTexture_Strict("GFX\NPCs\scp_1499_1_king.png", 1, DeleteAllTextures)
End Function

Function RemoveNPCInstances%()
	Local i%
	
	For i = 0 To MaxNPCModelIDAmount - 1
		FreeEntity(n_I\NPCModelID[i]) : n_I\NPCModelID[i] = 0
	Next
	For i = NPC_CLASS_D_GONZALES_TEXTURE To NPC_096_BLOODY_TEXTURE
		n_I\NPCTextureID[i] = 0
	Next
	Delete(n_I) : n_I = Null
End Function

Const MaxMTModelIDAmount% = 7
Const MaxLightSpriteIDAmount% = 3

Type MiscInstance
	Field MTModelID%[MaxMTModelIDAmount]
	Field CupLiquid%
	Field LightSpriteID[MaxLightSpriteIDAmount]
	Field AdvancedLightSprite%
End Type

Global misc_I.MiscInstance

; ~ Light Sprite ID Constants
;[Block]
Const LIGHT_SPRITE_DEFAULT% = 0
Const LIGHT_SPRITE_RED% = 1
;[End Block]

Function LoadMisc%()
	Local i%
	
	misc_I.MiscInstance = New MiscInstance
	
	misc_I\MTModelID[0] = LoadRMesh("GFX\Map\mt1.rmesh", Null)
	misc_I\MTModelID[1] = LoadRMesh("GFX\Map\mt2.rmesh", Null)
	misc_I\MTModelID[2] = LoadRMesh("GFX\Map\mt2C.rmesh", Null)
	misc_I\MTModelID[3] = LoadRMesh("GFX\Map\mt3.rmesh", Null)
	misc_I\MTModelID[4] = LoadRMesh("GFX\Map\mt4.rmesh", Null)
	misc_I\MTModelID[5] = LoadRMesh("GFX\Map\mt2_elevator.rmesh", Null)
	misc_I\MTModelID[6] = LoadRMesh("GFX\Map\mt1_generator.rmesh", Null)
	
	For i = 0 To MaxMTModelIDAmount - 1
		HideEntity(misc_I\MTModelID[i])
	Next
	
	misc_I\CupLiquid = LoadMesh_Strict("GFX\Items\cup_liquid.b3d")
	HideEntity(misc_I\CupLiquid)
	
	For i = LIGHT_SPRITE_DEFAULT To LIGHT_SPRITE_RED
		misc_I\LightSpriteID[i] = LoadTexture_Strict("GFX\Particles\light(" + i + ").png", 1, DeleteAllTextures, False)
	Next
	misc_I\AdvancedLightSprite = LoadTexture_Strict("GFX\Particles\advanced_light.png", 1, DeleteAllTextures, False)
End Function

Function RemoveMiscInstances%()
	Local i%
	
	For i = 0 To MaxMTModelIDAmount - 1
		FreeEntity(misc_I\MTModelID[i]) : misc_I\MTModelID[i] = 0
	Next
	FreeEntity(misc_I\CupLiquid) : misc_I\CupLiquid = 0
	For i = LIGHT_SPRITE_DEFAULT To LIGHT_SPRITE_RED
		misc_I\LightSpriteID[i] = 0
	Next
	misc_I\AdvancedLightSprite = 0
	Delete(misc_I) : misc_I = Null
End Function

Function LoadMaterials%(File$)
	CatchErrors("LoadMaterials(" + File + ")")
	
	Local Loc$
	Local mat.Materials = Null
	Local StrTemp$ = ""
	Local f% = OpenFile_Strict(File)
	
	While (Not Eof(f))
		Loc = Trim(ReadLine(f))
		If Left(Loc, 1) = "["
			Loc = Mid(Loc, 2, Len(Loc) - 2)
			mat.Materials = New Materials
			mat\Name = Lower(Loc)
			If opt\BumpEnabled
				StrTemp = IniGetString(File, Loc, "bump")
				If StrTemp <> ""
					mat\Bump = LoadTexture_Strict(StrTemp, 1 + 256, DeleteMapTextures, False)
					ApplyBumpMap(mat\Bump)
				EndIf
			EndIf
			mat\StepSound = (IniGetInt(File, Loc, "stepsound") + 1)
			mat\IsDiffuseAlpha = IniGetInt(File, Loc, "transparent")
			mat\UseMask = IniGetInt(File, Loc, "masked")
		EndIf
	Wend
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadMaterials(" + File + ")")
End Function

Function InitLoadingScreens%(File$)
	Local LocalLoadingScreen% = JsonParseFromFile(lang\LanguagePath + File)
	
	If JsonIsArray(LocalLoadingScreen) ; ~ Has localized loading screens -> Use localized only
		LoadingScreens = JsonGetArray(LocalLoadingScreen)
	Else
		LoadingScreens = JsonGetArray(JsonParseFromFile(File))
	EndIf
End Function

Const ItemsPath$ = "GFX\Items\"
Const ItemHUDTexturePath$ = "GFX\Items\HUD Textures\"
Const ItemINVIconPath$ = "GFX\Items\Inventory Icons\"

Function LoadItems%()
	Local it.ItemTemplates, it2.ItemTemplates
	
	; ~ [PAPER]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "doc005"), "Document SCP-005", it_paper, "paper.b3d", "INV_paper.png", "doc_005.png", 0.003, 0, "doc_005.png")
	CreateItemTemplate(GetLocalString("items", "doc008"), "Document SCP-008", it_paper, "paper.b3d", "INV_paper.png", "doc_008.png", 0.003, 0, "doc_008.png")
	CreateItemTemplate(GetLocalString("items", "doc012"), "Document SCP-012", it_paper, "paper.b3d", "INV_paper.png", "doc_012.png", 0.003, 0, "doc_012.png")
	CreateItemTemplate(GetLocalString("items", "doc035"), "Document SCP-035", it_paper, "paper.b3d", "INV_paper.png", "doc_035_smile.png", 0.003, 0, "doc_035_smile.png")
	CreateItemTemplate(GetLocalString("items", "doc049"), "Document SCP-049", it_paper, "paper.b3d", "INV_paper.png", "doc_049.png", 0.003, 0, "doc_049.png")
	CreateItemTemplate(GetLocalString("items", "doc066"), "Document SCP-066", it_paper, "paper.b3d", "INV_paper.png", "doc_066.png", 0.003, 0, "doc_066.png")
	CreateItemTemplate(GetLocalString("items", "doc079"), "Document SCP-079", it_paper, "paper.b3d", "INV_paper.png", "doc_079.png", 0.003, 0, "doc_079.png")
	CreateItemTemplate(GetLocalString("items", "doc096"), "Document SCP-096", it_paper, "paper.b3d", "INV_paper.png", "doc_096.png", 0.003, 0, "doc_096.png")
	CreateItemTemplate(GetLocalString("items", "doc106"), "Document SCP-106", it_paper, "paper.b3d", "INV_paper.png", "doc_106.png", 0.003, 0, "doc_106.png")
	CreateItemTemplate(GetLocalString("items", "doc173"), "Document SCP-173", it_paper, "paper.b3d", "INV_paper.png", "doc_173.png", 0.003, 0, "doc_173.png")
	CreateItemTemplate(GetLocalString("items", "doc205"), "Document SCP-205", it_paper, "paper.b3d", "INV_paper.png", "doc_205.png", 0.003, 0, "doc_205.png")
	CreateItemTemplate(GetLocalString("items", "doc372"), "Document SCP-372", it_paper, "paper.b3d", "INV_paper.png", "doc_372.png", 0.003, 0, "doc_372.png")
	CreateItemTemplate(GetLocalString("items", "doc409"), "Document SCP-409", it_paper, "paper.b3d", "INV_paper.png", "doc_409.png", 0.003, 0, "doc_409.png")
	CreateItemTemplate(GetLocalString("items", "doc500"), "Document SCP-500", it_paper, "paper.b3d", "INV_paper.png", "doc_500.png", 0.003, 0, "doc_500.png")
	CreateItemTemplate(GetLocalString("items", "doc513"), "Document SCP-513", it_paper, "paper.b3d", "INV_paper.png", "doc_513.png", 0.003, 0, "doc_513.png")
	CreateItemTemplate(GetLocalString("items", "doc682"), "Document SCP-682", it_paper, "paper.b3d", "INV_paper.png", "doc_682.png", 0.003, 0, "doc_682.png")
	CreateItemTemplate(GetLocalString("items", "doc714"), "Document SCP-714", it_paper, "paper.b3d", "INV_paper.png", "doc_714.png", 0.003, 0, "doc_714.png")
	CreateItemTemplate(GetLocalString("items", "doc860"), "Document SCP-860", it_paper, "paper.b3d", "INV_paper.png", "doc_860.png", 0.003, 0, "doc_860.png")
	CreateItemTemplate(GetLocalString("items", "doc8601"), "Document SCP-860-1", it_paper, "paper.b3d", "INV_paper.png", "doc_860_1.png", 0.003, 0, "doc_860_1.png")
	CreateItemTemplate(GetLocalString("items", "doc895"), "Document SCP-895", it_paper, "paper.b3d", "INV_paper.png", "doc_895.png", 0.003, 0, "doc_895.png")
	CreateItemTemplate(GetLocalString("items", "doc939"), "Document SCP-939", it_paper, "paper.b3d", "INV_paper.png", "doc_939.png", 0.003, 0, "doc_939.png")
	CreateItemTemplate(GetLocalString("items", "doc966"), "Document SCP-966", it_paper, "paper.b3d", "INV_paper.png", "doc_966.png", 0.003, 0, "doc_966.png")
	CreateItemTemplate(GetLocalString("items", "doc970"), "Document SCP-970", it_paper, "paper.b3d", "INV_paper.png", "doc_970.png", 0.003, 0, "doc_970.png")
	CreateItemTemplate(GetLocalString("items", "doc1048"), "Document SCP-1048", it_paper, "paper.b3d", "INV_paper.png", "doc_1048.png", 0.003, 0,  "doc_1048.png")
	CreateItemTemplate(GetLocalString("items", "doc1123"), "Document SCP-1123", it_paper, "paper.b3d", "INV_paper.png", "doc_1123.png", 0.003, 0, "doc_1123.png")
	CreateItemTemplate(GetLocalString("items", "doc1162"), "Document SCP-1162-ARC", it_paper, "paper.b3d", "INV_paper.png", "doc_1162_ARC.png", 0.003, 0, "doc_1162_ARC.png")
	CreateItemTemplate(GetLocalString("items", "doc1499"), "Document SCP-1499", it_paper, "paper.b3d", "INV_paper.png", "doc_1499.png", 0.003, 0, "doc_1499.png")

	CreateItemTemplate(GetLocalString("items", "doc1048a"), "Incident Report SCP-1048-A", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_1048_a.png", 0.003, 0, "doc_IR_1048_a.png")
	
	CreateItemTemplate(GetLocalString("items", "doc035a"), "SCP-035 Addendum", it_paper, "paper.b3d", "INV_paper.png", "doc_035_ad.png", 0.003, 0, "doc_035_ad.png")
	
	CreateItemTemplate(GetLocalString("items", "doc093"), "SCP-093 Recovered Materials", it_paper, "paper.b3d", "INV_paper.png", "doc_093_rm.png", 0.003, 0, "doc_093_rm.png")
	
	CreateItemTemplate(GetLocalString("items", "doc914log"), "Addendum: 5/14 Test Log", it_paper, "paper.b3d", "INV_paper.png", "doc_RAND(2).png", 0.003, 0, "doc_RAND(2).png")
	
	CreateItemTemplate(GetLocalString("items", "doccdol"), "Class D Orientation Leaflet", it_paper, "paper.b3d", "INV_paper.png", "doc_OL.png", 0.003, 0, "doc_OL.png")
	
	CreateItemTemplate(GetLocalString("items", "doc"), "Document", it_paper, "paper.b3d", "INV_paper.png", "doc_RAND(3).png", 0.003, 0, "doc_RAND(3).png")
	
	CreateItemTemplate(GetLocalString("items", "doco5"), "Field Agent Log #235-001-CO5", it_paper, "paper.b3d", "INV_paper.png", "doc_O5.png", 0.003, 0, "doc_O5.png")
	
	CreateItemTemplate(GetLocalString("items", "doco52"), "Groups of Interest Log", it_paper, "paper.b3d", "INV_paper.png", "doc_O5(2).png", 0.003, 0, "doc_O5(2).png")
	
	CreateItemTemplate(GetLocalString("items", "docir066"), "Incident Report SCP-066-2", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_066.png", 0.003, 0, "doc_IR_066.png")
	
	CreateItemTemplate(GetLocalString("items", "docir106"), "Incident Report SCP-106-0204", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_106.png", 0.003, 0, "doc_IR_106.png")
	
	CreateItemTemplate(GetLocalString("items", "docmtf"), "Mobile Task Forces", it_paper, "paper.b3d", "INV_paper.png", "doc_MTF.png", 0.003, 0, "doc_MTF.png")
	
	CreateItemTemplate(GetLocalString("items", "docdaniel"), "Note from Daniel", it_paper, "note.b3d", "INV_note(2).png", "note_Daniel.png", 0.0025, 0, "note_Daniel.png")
	
	CreateItemTemplate(GetLocalString("items", "docndp"), "Nuclear Device Document", it_paper, "paper.b3d", "INV_paper.png", "doc_NDP.png", 0.003, 0, "doc_NDP.png")
	
	CreateItemTemplate(GetLocalString("items", "dococ"), "Object Classes", it_paper, "paper.b3d", "INV_paper.png", "doc_OBJC.png", 0.003, 0, "doc_OBJC.png")
	
	CreateItemTemplate(GetLocalString("items", "docrp"), "Recall Protocol RP-106-N", it_paper, "paper.b3d", "INV_paper.png", "doc_RP.png", 0.0025, 0, "doc_RP.png")
	
	CreateItemTemplate(GetLocalString("items", "docrs"), "Research Sector-02 Scheme", it_paper, "paper.b3d", "INV_paper.png", "doc_RS.png", 0.003, 0, "doc_RS.png")
	
	CreateItemTemplate(GetLocalString("items", "docscl"), "Security Clearance Levels", it_paper, "paper.b3d", "INV_paper.png", "doc_SCL.png", 0.003, 0, "doc_SCL.png")
	
	CreateItemTemplate(GetLocalString("items", "docst"), "Storage Transfers", it_paper, "paper.b3d", "INV_paper.png", "doc_storagetransfers.png", 0.003, 0, "doc_storagetransfers.png")
	
	CreateItemTemplate(GetLocalString("items", "docsn"), "Sticky Note", it_paper, "note.b3d", "INV_note(2).png", "note_682.png", 0.0025, 0, "note_682.png")
	
	CreateItemTemplate(GetLocalString("items", "docmsp"), "The Modular Site Project", it_paper, "paper.b3d", "INV_paper.png", "doc_MSP.png", 0.003, 0, "doc_MSP.png")
	
	CreateItemTemplate(GetLocalString("items", "docblank"), "Blank Paper", it_paper, "paper.b3d", "INV_paper_blank.png", "doc_blank.png", 0.003, 0, "doc_blank.png")
	
	CreateItemTemplate(GetLocalString("items", "docl_1"), "Blood-stained Note", it_paper, "note.b3d", "INV_note_bloody.png", "note_L(3).png", 0.0025, 0, "note_L(3).png")
	
	CreateItemTemplate(GetLocalString("items", "docmaynard"), "Burnt Note", it_paper, "paper.b3d", "INV_burnt_note.png", "note_Maynard.png", 0.003, 0, "note_Maynard.png")
	
	CreateItemTemplate(GetLocalString("items", "docdr"), "Data Report", it_paper, "paper.b3d", "INV_paper_bloody.png", "doc_data.png", 0.003, 0, "doc_data.png")
	
	CreateItemTemplate(GetLocalString("items", "doc427"), "Document SCP-427", it_paper, "paper.b3d", "INV_paper_bloody.png", "doc_427.png", 0.003, 0, "doc_427.png")
	
	CreateItemTemplate(GetLocalString("items", "docdrawing"), "Drawing", it_paper, "paper.b3d", "INV_note.png", "drawing_1048(1).png", 0.003, 0, "drawing_1048(1).png")
	
	CreateItemTemplate(GetLocalString("items", "docallok"), "Dr. Allok's Note", it_paper, "note.b3d", "INV_note.png", "note_Allok.png", 0.004, 0, "note_Allok.png")
	
	CreateItemTemplate(GetLocalString("items", "docl_2"), "Dr. L's Note #1", it_paper, "paper.b3d", "INV_note.png", "note_L.png", 0.0025, 0, "note_L.png")
	CreateItemTemplate(GetLocalString("items", "docl_3"), "Dr. L's Note #2", it_paper, "paper.b3d", "INV_note.png", "note_L(2).png", 0.0025, 0, "note_L(2).png")
	
	CreateItemTemplate(GetLocalString("items", "docl_4"), "Dr. L's Burnt Note #1", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(4).png", 0.0025, 0, "note_L(4).png")
	CreateItemTemplate(GetLocalString("items", "docl_5"), "Dr. L's Burnt Note #2", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(5).png", 0.0025, 0, "note_L(5).png")
	
	CreateItemTemplate(GetLocalString("items", "docjournal"), "Journal Page", it_paper, "paper.b3d", "INV_note.png", "note_Gonzales.png", 0.0025, 0, "note_Gonzales.png")
	
	CreateItemTemplate(GetLocalString("items", "docleaflet"), "Leaflet", it_paper, "paper.b3d", "INV_note.png", "leaflet.png", 0.003, 0, "leaflet.png")
	
	CreateItemTemplate(GetLocalString("items", "doclog_1"), "Log #1", it_paper, "paper.b3d", "INV_note.png", "note_forest.png", 0.002, 0, "note_forest.png")
	CreateItemTemplate(GetLocalString("items", "doclog_2"), "Log #2", it_paper, "paper.b3d", "INV_note.png", "note_forest(2).png", 0.002, 0, "note_forest(2).png")
	CreateItemTemplate(GetLocalString("items", "doclog_3"), "Log #3", it_paper, "paper.b3d", "INV_note.png", "note_forest(3).png", 0.002, 0, "note_forest(3).png")
	
	CreateItemTemplate(GetLocalString("items", "docmn"), "Mysterious Note", it_paper, "paper.b3d", "INV_note.png", "note_mysterious.png", 0.003, 0, "note_mysterious.png")
	
	CreateItemTemplate(GetLocalString("items", "docnotemaynard"), "Note from Maynard", it_paper, "note.b3d", "INV_note.png", "note_Maynard(2).png", 0.0025, 0, "note_Maynard(2).png")
	
	CreateItemTemplate(GetLocalString("items", "docrand"), "Notification", it_paper, "paper.b3d", "INV_note.png", "doc_RAND.png", 0.003, 0, "doc_RAND.png")
	
	CreateItemTemplate(GetLocalString("items", "docl_6"), "Scorched Note", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(6).png", 0.0025, 0, "note_L(6).png")
	
	CreateItemTemplate(GetLocalString("items", "docsnm"), "Strange Note", it_paper, "paper.b3d", "INV_note.png", "note_strange.png", 0.0025, 0, "note_strange.png")
	
	CreateItemTemplate(GetLocalString("items", "docun"), "Unknown Note", it_paper, "note.b3d", "INV_note_bloody.png", "note_unknown.png", 0.003, 0, "note_unknown.png")
	
	CreateItemTemplate(GetLocalString("items", "docdh"), "Disciplinary Hearing DH-S-4137-17092", it_oldpaper, "paper.b3d", "INV_paper.png", "doc_DH.png", 0.003, 0, "doc_DH.png")
	
	CreateItemTemplate(GetLocalString("items", "origami"), "Origami", it_origami, "origami.b3d", "INV_origami.png", "", 0.003, 0)
	
	CreateItemTemplate(GetLocalString("items", "badge"), "Emily Ross' Badge", it_badge, "badge.b3d", "INV_Emily_badge.png", "Emily_badge.png", 0.0001, 1)
	
	CreateItemTemplate(GetLocalString("items", "burntbadge"), "George Maynard's Badge", it_burntbadge, "badge.b3d", "INV_Maynard_badge.png", "Maynard_badge.png", 0.0001, 1, "Maynard_badge.png")
	
	CreateItemTemplate(GetLocalString("items", "harnbadge"), "Asav Harn's Badge", it_harnbadge, "badge.b3d", "INV_harn_badge.png", "harn_badge.png", 0.0001, 1, "harn_badge.png")
	
	CreateItemTemplate(GetLocalString("items", "oldbadge"), "Old Badge", it_oldbadge, "badge.b3d", "INV_D_9341_badge.png", "D_9341_badge.png", 0.0001, 1, "D_9341_badge.png", "", False, 1 + 2 + 8)
	
	CreateItemTemplate(GetLocalString("items", "ticket"), "Movie Ticket", it_ticket, "badge.b3d", "INV_ticket.png", "ticket.png", 0.0001, 0, "ticket.png", "", False, 1 + 2 + 8)
	;[End Block]
	
	; ~ [SCPs]
	;[Block]
	CreateItemTemplate("SCP-085", "SCP-085", it_paper, "note.b3d", "INV_note.png", "note_085.png", 0.0033, 0, "note_085.png")
	
	CreateItemTemplate("SCP-005", "SCP-005", it_scp005, "scp_005.b3d", "INV_scp_005.png", "", 0.0003, 1)
	CreateItemTemplate(GetLocalString("items", "148"), "SCP-148 Ingot", it_scp148ingot, "scp_148.b3d", "INV_scp_148.png", "", RoomScale, 2)
	CreateItemTemplate(GetLocalString("items", "metalpanel"), "Metal Panel", it_scp148, "metal_panel.b3d", "INV_metal_panel.png", "", RoomScale, 2)
	
	it.ItemTemplates = CreateItemTemplate("SCP-500-01", "SCP-500-01", it_scp500pill, "pill.b3d", "INV_scp_500_pill.png", "", 0.00007, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	CreateItemTemplate("SCP-268", "SCP-268", it_scp268, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	CreateItemTemplate("SCP-268", "SCP-268", it_fine268, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	CreateItemTemplate("SCP-427", "SCP-427", it_scp427, "scp_427.b3d", "INV_scp_427.png", "", 0.001, 3)
	CreateItemTemplate("SCP-500", "SCP-500", it_scp500, "scp_500.b3d", "INV_scp_500.png", "", 0.035, 2)
	CreateItemTemplate("SCP-513", "SCP-513", it_scp513, "scp_513.b3d", "INV_scp_513.png", "", 0.1, 2)
	CreateItemTemplate("SCP-714", "SCP-714", it_scp714, "scp_714.b3d", "INV_scp_714.png", "", 0.2, 3)
	CreateItemTemplate("SCP-714", "SCP-714", it_coarse714, "scp_714.b3d", "INV_scp_714_grey.png", "", 0.2, 3, "scp_714_grey.png")
	CreateItemTemplate("SCP-714", "SCP-714", it_fine714, "scp_714.b3d", "INV_scp_714_blue.png", "", 0.2, 3, "scp_714_blue.png")
	CreateItemTemplate("SCP-860", "SCP-860", it_scp860, "scp_860.b3d", "INV_scp_860.png", "", 0.003, 3)
	CreateItemTemplate("SCP-1025", "SCP-1025", it_scp1025, "scp_1025.b3d", "INV_scp_1025.png", "", 0.1, 0)
	CreateItemTemplate("SCP-1123", "SCP-1123", it_scp1123, "scp_1123.b3d", "INV_scp_1123.png", "", 0.015, 2)
	CreateItemTemplate("SCP-1499", "SCP-1499", it_scp1499, "scp_1499.b3d", "INV_scp_1499.png", "", 0.022, 2)
	CreateItemTemplate("SCP-1499", "SCP-1499", it_fine1499, "scp_1499.b3d", "INV_scp_1499.png", "", 0.022, 2)
	
	CreateItemTemplate(GetLocalString("items", "cap"), "Newsboy Cap", it_cap, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	
	CreateItemTemplate(GetLocalString("items", "joint"), "Joint", it_joint, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.0004, 2)
	
	CreateItemTemplate(GetLocalString("items", "420j"), "Some SCP-420-J", it_scp420j, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.0005, 2)
	
	CreateItemTemplate(GetLocalString("items", "smellyjoint"), "Smelly Joint", it_scp420s, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.0004, 2)
	
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "500death"), "Upgraded Pill", it_scp500pilldeath, "pill.b3d", "INV_scp_500_pill.png", "", 0.00007, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	;[End Block]
	
	; ~ [MISC ITEMS]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "helmet"), "Ballistic Helmet", it_helmet, "ballistic_helmet.b3d", "INV_ballistic_helmet.png", "", 0.018, 2)
	
	CreateItemTemplate(GetLocalString("items", "vest"), "Ballistic Vest", it_vest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "corrvest"), "Corrosive Ballistic Vest", it_corrvest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.02, 2, "ballistic_vest_corrosive.png")
	CreateItemTemplate(GetLocalString("items", "finevest"), "Heavy Ballistic Vest", it_finevest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.022, 2)
	CreateItemTemplate(GetLocalString("items", "veryfinevest"), "Bulky Ballistic Vest", it_veryfinevest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.025, 2)
	
	CreateItemTemplate(GetLocalString("items", "book"), "Book", it_book, "scp_1025.b3d", "INV_book.png", "", 0.07, 0, "book.png")
	
	CreateItemTemplate(GetLocalString("items", "cigarette"), "Cigarette", it_cigarette, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.0004, 2)
	
	CreateItemTemplate(GetLocalString("items", "ring"), "Green Jade Ring", it_ring, "scp_714.b3d", "INV_scp_714_small.png", "", 0.15, 3)
	
	CreateItemTemplate(GetLocalString("items", "cup"), "Cup", it_cup, "cup.b3d", "INV_cup_filled.png", "", 0.04, 2)
	
	CreateItemTemplate(GetLocalString("items", "emptycup"), "Empty Cup", it_emptycup, "cup.b3d", "INV_cup_empty.png", "", 0.04, 2)
	
	CreateItemTemplate(GetLocalString("items", "clipboard"), "Clipboard", it_clipboard, "clipboard.b3d", "INV_clipboard_filled.png", "", 0.003, 1, "", "INV_clipboard_empty.png", True)
	
	CreateItemTemplate(GetLocalString("items", "wallet"), "Wallet", it_wallet, "wallet.b3d", "INV_wallet_filled.png", "", 0.0006, 2, "", "INV_wallet_empty.png", True)
	
	CreateItemTemplate(GetLocalString("items", "electronics"), "Electronical Components", it_electronics, "circuits.b3d", "INV_circuits.png", "", 0.0011, 1)
	
	CreateItemTemplate(GetLocalString("items", "eyedrops"), "ReVision Eyedrops", it_eyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate(GetLocalString("items", "eyedrops.red"), "RedVision Eyedrops", it_eyedrops2, "eye_drops.b3d", "INV_eye_drops_red.png", "", 0.0012, 1, "eye_drops_red.png")
	CreateItemTemplate(GetLocalString("items", "eyedrops_2"), "Eyedrops", it_fineeyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate(GetLocalString("items", "eyedrops_2"), "Eyedrops", it_veryfineeyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0012, 1)
	
	CreateItemTemplate(GetLocalString("items", "fak"), "First Aid Kit", it_firstaid, "first_aid_kit.b3d", "INV_first_aid_kit.png", "", 0.05, 1)
	CreateItemTemplate(GetLocalString("items", "bfak"), "Blue First Aid Kit", it_firstaid2, "first_aid_kit.b3d", "INV_first_aid_kit_blue.png", "", 0.03, 1, "first_aid_kit(2).png")
	CreateItemTemplate(GetLocalString("items", "cfak"), "Compact First Aid Kit", it_finefirstaid, "first_aid_kit.b3d", "INV_first_aid_kit_compact.png", "", 0.03, 1)
	CreateItemTemplate(GetLocalString("items", "sb"), "Strange Bottle", it_veryfinefirstaid, "eye_drops.b3d", "INV_strange_bottle.png", "", 0.002, 1, "strange_bottle.png")
	
	CreateItemTemplate(GetLocalString("items", "mask"), "Gas Mask", it_gasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.019, 2)
	CreateItemTemplate(GetLocalString("items", "mask"), "Gas Mask", it_finegasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.019, 2)
	CreateItemTemplate(GetLocalString("items", "mask"), "Gas Mask", it_veryfinegasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "mask148"), "Heavy Gas Mask", it_gasmask148, "gas_mask.b3d", "INV_gas_mask_heavy.png", "", 0.02, 2, "gas_mask_heavy.png")
	
	CreateItemTemplate(GetLocalString("items", "suit"), "Hazmat Suit", it_hazmatsuit, "hazmat_suit.b3d", "INV_hazmat_suit.png", "", 0.013, 2, "", "", True)
	CreateItemTemplate(GetLocalString("items", "suit"), "Hazmat Suit", it_finehazmatsuit, "hazmat_suit.b3d", "INV_hazmat_suit.png", "", 0.013, 2, "", "", True)
	CreateItemTemplate(GetLocalString("items", "suit"), "Hazmat Suit", it_veryfinehazmatsuit, "hazmat_suit.b3d", "INV_hazmat_suit.png", "", 0.013, 2, "", "", True)
	CreateItemTemplate(GetLocalString("items", "suit148"), "Heavy Hazmat Suit", it_hazmatsuit148, "hazmat_suit.b3d", "INV_hazmat_suit_heavy.png", "", 0.013, 2, "hazmat_suit_heavy.png", "", True)
	
	CreateItemTemplate(GetLocalString("items", "nvg"), "Night Vision Goggles", it_nvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_green.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "nvg"), "Night Vision Goggles", it_finenvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_red.png", "", 0.02, 2, "night_vision_goggles_red.png")
	CreateItemTemplate(GetLocalString("items", "nvg"), "Night Vision Goggles", it_veryfinenvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_blue.png", "", 0.02, 2, "night_vision_goggles_blue.png")
	CreateItemTemplate(GetLocalString("items", "scramble"), "SCRAMBLE Gear", it_scramble, "SCRAMBLE_gear.b3d", "INV_SCRAMBLE_gear.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "scramble"), "SCRAMBLE Gear", it_finescramble, "SCRAMBLE_gear.b3d", "INV_SCRAMBLE_gear.png", "", 0.02, 2)
	
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "pill"), "Pill", it_pill, "pill.b3d", "INV_pill.png", "", 0.00007, 2)
	EntityColor(it\OBJ, 255.0, 255.0, 255.0)
	
	CreateItemTemplate(GetLocalString("items", "radio"), "Radio Transceiver", it_radio, "radio.b3d", "INV_radio.png", "radio.png", 1.0, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "Radio Transceiver", it_18vradio, "radio.b3d", "INV_radio.png", "radio.png", 1.02, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "Radio Transceiver", it_fineradio, "radio.b3d", "INV_radio.png", "radio.png", 1.0, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "Radio Transceiver", it_veryfineradio, "radio.b3d", "INV_radio.png", "radio.png", 1.0, 1)
	
	CreateItemTemplate(GetLocalString("items", "hand_1"), "White Severed Hand", it_hand, "severed_hand.b3d", "INV_severed_hand_white.png", "", 0.033, 2)
	CreateItemTemplate(GetLocalString("items", "hand_2"), "Black Severed Hand", it_hand2, "severed_hand.b3d", "INV_severed_hand_black.png", "", 0.033, 2, "severed_hand(2).png")
	CreateItemTemplate(GetLocalString("items", "hand_3"), "Yellow Severed Hand", it_hand3, "severed_hand.b3d", "INV_severed_hand_yellow.png", "", 0.033, 2, "severed_hand(3).png")
	
	CreateItemTemplate(GetLocalString("items", "nav"), "S-NAV Navigator", it_nav, "navigator.b3d", "INV_navigator.png", "navigator.png", 0.0008, 1)
	CreateItemTemplate(GetLocalString("items", "nav300"), "S-NAV 300 Navigator", it_nav300, "navigator.b3d", "INV_navigator.png", "navigator.png", 0.0008, 1)
	CreateItemTemplate(GetLocalString("items", "nav310"), "S-NAV 310 Navigator", it_nav310, "navigator.b3d", "INV_navigator.png", "navigator.png", 0.0008, 1)
	CreateItemTemplate(GetLocalString("items", "navulti"), "S-NAV Navigator Ultimate", it_navulti, "navigator.b3d", "INV_navigator.png", "navigator.png", 0.0008, 1)
	
	CreateItemTemplate(GetLocalString("items", "bat"), "9V Battery", it_bat, "battery.b3d", "INV_battery_9v.png", "", 0.007, 1)
	CreateItemTemplate(GetLocalString("items", "45bat"), "4.5V Battery", it_coarsebat, "battery.b3d", "INV_battery_4.5v.png", "", 0.007, 1, "battery_4.5V.png")
	CreateItemTemplate(GetLocalString("items", "18bat"), "18V Battery", it_finebat, "battery.b3d", "INV_battery_18v.png", "", 0.009, 1, "battery_18V.png")
	CreateItemTemplate(GetLocalString("items", "999bat"), "999V Battery", it_veryfinebat, "battery.b3d", "INV_battery_999v.png", "", 0.0075, 1, "battery_999V.png")
	CreateItemTemplate(GetLocalString("items", "killbat"), "Strange Battery", it_killbat, "battery.b3d", "INV_strange_battery.png", "", 0.009, 1, "strange_battery.png")
	
	CreateItemTemplate(GetLocalString("items", "syringe"), "Syringe", it_syringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Syringe", it_finesyringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Syringe", it_veryfinesyringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Syringe", it_syringeinf, "syringe.b3d", "INV_syringe_infect.png", "", 0.005, 2, "syringe_infect.png")
	;[End Block]
	
	; ~ [KEYCARDS, KEYS, CARDS, COINS]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "key0"), "Level 0 Key Card", it_key0, "key_card.b3d", "INV_key_card_lvl_0.png", "", 0.00037, 1)
	CreateItemTemplate(GetLocalString("items", "key1"), "Level 1 key Card", it_key1, "key_card.b3d", "INV_key_card_lvl_1.png", "", 0.00037, 1, "key_card_lvl_1.png")
	CreateItemTemplate(GetLocalString("items", "key2"), "Level 2 key Card", it_key2, "key_card.b3d", "INV_key_card_lvl_2.png", "", 0.00037, 1, "key_card_lvl_2.png")
	CreateItemTemplate(GetLocalString("items", "key3"), "Level 3 key Card", it_key3, "key_card.b3d", "INV_key_card_lvl_3.png", "", 0.00037, 1, "key_card_lvl_3.png")
	CreateItemTemplate(GetLocalString("items", "key3"), "Level 3 key Card", it_key3_bloody, "key_card.b3d", "INV_key_card_lvl_3_bloody.png", "", 0.00037, 1, "key_card_lvl_3_bloody.png") ; ~ Such a stupid way
	CreateItemTemplate(GetLocalString("items", "key4"), "Level 4 key Card", it_key4, "key_card.b3d", "INV_key_card_lvl_4.png", "", 0.00037, 1, "key_card_lvl_4.png")
	CreateItemTemplate(GetLocalString("items", "key5"), "Level 5 key Card", it_key5, "key_card.b3d", "INV_key_card_lvl_5.png", "", 0.00037, 1, "key_card_lvl_5.png")
	CreateItemTemplate(GetLocalString("items", "key6"), "Level 6 Key Card", it_key6, "key_card.b3d", "INV_key_card_lvl_6.png", "", 0.00037, 1, "key_card_lvl_6.png")
	CreateItemTemplate(GetLocalString("items", "keyomni"), "Key Card Omni", it_keyomni, "key_card.b3d", "INV_key_card_lvl_omni.png", "", 0.00037, 1, "key_card_lvl_omni.png")
	
	CreateItemTemplate(GetLocalString("items", "mastercard"), "Mastercard", it_mastercard, "key_card.b3d", "INV_master_card.png", "", 0.00037, 1, "master_card.png")
	
	CreateItemTemplate(GetLocalString("items", "playcard"), "Playing Card", it_playcard, "key_card.b3d", "INV_playing_card.png", "", 0.00037, 1, "playing_card.png")
	
	CreateItemTemplate(GetLocalString("items", "key"), "Lost Key", it_key, "key.b3d", "INV_key.png", "", 0.003, 3)
	
	CreateItemTemplate(GetLocalString("items", "25ct"), "Quarter", it_25ct, "coin.b3d", "INV_coin.png", "", 0.0005, 3)
	CreateItemTemplate(GetLocalString("items", "coin"), "Coin", it_coin, "coin.b3d", "INV_coin_rusty.png", "", 0.0005, 3, "coin_rusty.png")
	
	CreateItemTemplate(GetLocalString("items", "pizza"), "Pizza Slice", it_pizza, "Pizza_Slice.b3d", "INV_Pizza_Slice.png", "", 0.05, 2)
	;[End Block]
	
	For it.ItemTemplates = Each ItemTemplates
		If it\Tex <> 0
			If it\TexPath <> ""
				For it2.ItemTemplates = Each ItemTemplates
					If it2 <> it And it2\Tex = it\Tex
						DeleteSingleTextureEntryFromCache(it2\Tex) : it2\Tex = 0
					EndIf
				Next
			EndIf
			DeleteSingleTextureEntryFromCache(it\Tex) : it\Tex = 0
		EndIf
	Next
End Function

Global SoundTransmission%
Global SoundEmitter%

Global TempSounds%[10]
Global TempSoundIndex% = 0

; ~ The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Global Music$[33]

; ~ Music list
;[Block]
Music[0] = "LightContainmentZone"
Music[1] = "HeavyContainmentZone"
Music[2] = "EntranceZone"
Music[3] = "PD"
Music[4] = "079Chamber"
Music[5] = "Gate_B1"
Music[6] = "Gate_B2"
Music[7] = "Room3_storage"
Music[8] = "049Chamber"
Music[9] = "860_1Chamber"
Music[10] = "106Chase"
Music[11] = "Menu"
Music[12] = "860_2Chase"
Music[13] = "173IntroChamber"
Music[14] = "PDTrench"
Music[15] = "205Chamber"
Music[16] = "Gate_A"
Music[17] = "1499Dimension"
Music[18] = "1499_1Chase"
Music[19] = "049Chase"
Music[20] = "..\Ending\MenuBreath"
Music[21] = "914Chamber"
Music[22] = "Ending"
Music[23] = "Credits"
Music[24] = "SaveMeFrom"
Music[25] = "106Chamber"
Music[26] = "035Chamber"
Music[27] = "409Chamber"
Music[28] = "MaintenanceTunnels"
Music[29] = "1123Chamber"
Music[30] = "008Chamber"
Music[31] = "008Cutscene"
Music[32] = "012Chamber"
;[End Block]

Global MusicCHN%
MusicCHN = StreamSound_Strict("SFX\Music\" + Music[2] + ".ogg", opt\MusicVolume, Mode)

Global NowPlaying% = 2, ShouldPlay% = 11
Global CurrMusic% = True

Dim OpenDoorSFX%(6, 3), CloseDoorSFX%(6, 3)

Type SoundInstance
	Field BigDoorErrorSFX%[3]
	Field DoorClose079%, DoorOpen079%
	Field KeyCardSFX%[2]
	Field ScannerSFX%[2]
	Field DoorBudgeSFX%[2]
	Field DoorLockSFX%
	Field OpenDoorFastSFX%
	Field CautionSFX%
	Field CameraSFX%
	Field StoneDragSFX%
	Field GunshotSFX%[2]
	Field BulletMissSFX%, BulletHitSFX%
	Field TeslaIdleSFX%, TeslaActivateSFX%, TeslaPowerUpSFX%, TeslaShockSFX%
	Field MagnetUpSFX%, MagnetDownSFX%
	Field FemurBreakerSFX%
	Field CrouchSFX%
	Field DecaySFX%[5]
	Field BurstSFX%
	Field HissSFX%[2]
	Field RustleSFX%[6]
	Field Use914SFX%, Death914SFX%
	Field DripSFX%[4]
	Field KnobSFX%[2]
	Field LeverSFX%
	Field LightSFX%[3]
	Field LightOffSFX%
	Field RadioSquelch%
	Field RadioStatic%
	Field RadioStatic895%
	Field RadioBuzz%
	Field SCRAMBLESFX%
	Field NVGSFX%[2]
	Field LowBatterySFX%[2]
	Field ElevatorBeepSFX%, ElevatorMoveSFX%
	Field PickSFX%[4]
	Field SCP106SFX%[9]
	Field SCP173SFX%[3]
	Field HorrorSFX%[14]
	Field MissSFX%
	Field IntroSFX%[8]
	Field AlarmSFX%[4]
	Field DamageSFX%[14]
	Field HeartBeatSFX%
	Field NeckSnapSFX%[3]
	Field VomitSFX%
	Field BreathGasRelaxedSFX%
	Field Step2SFX%[13]
	Field VehicleSFX%[2]
	Field ExplosionSFX%
	Field MachineSFX%
	Field ApacheSFX%
	Field BlindsSFX%
	Field SparkShortSFX%
	Field SinkHoleSFX%
End Type

Global snd_I.SoundInstance

Dim RadioSFX%(2, 9)

Global EndBreathSFX%
Global EndBreathCHN%

Global SCRAMBLECHN%

Global LowBatteryCHN%[2]

Global AmbientSFXCHN%, CurrAmbientSFX%
Global AmbientSFXAmount%[6]
Dim AmbientSFX%(6, 16)

Global CommotionState%[25]

Dim BreathSFX%(2, 5)
Global BreathCHN%

Global BreathGasRelaxedCHN%

Dim CoughSFX%(2, 3) ; ~ Normal / Gas Mask, Amount
Global CoughCHN%, VomitCHN%

Global DecalStep%
; ~ 0 - Normal
; ~ 1 - Metal
; ~ 2 - PD
; ~ 3 - Cloth
; ~ 4 - Forest (Should be used in future. Currently doesn't work for player)
Dim StepSFX%(6, 2, 8) ; ~ (Ground Type, Walk / Run, ID)

Global RadioCHN%[7]
; ~ 6 is used for radio static only

Global IntercomStreamCHN%

Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Global UserTrackName$[256]

; ~ NPCs Sound Constants
;[Block]
Const SOUND_NPC_MTF_BEEP% = 0
Const SOUND_NPC_MTF_BREATH% = 1
Const SOUND_NPC_MTF_BLINKING% = 2
Const SOUND_NPC_008_1_BREATH% = 3
Const SOUND_NPC_035_TENTACLE_IDLE% = 4
Const SOUND_NPC_049_BREATH% = 5
Const SOUND_NPC_049_2_BREATH% = 6
Const SOUND_NPC_049_2_RESTING% = 7
;[End Block]
Const MaxNPCSounds% = 8
Global NPCSound%[MaxNPCSounds]

Function LoadSounds%()
	Local i%
	
	RenderLoading(45, GetLocalString("loading", "sounds"))
	
	snd_I.SoundInstance = New SoundInstance
	
	For i = 0 To 13
		If i < 2
			snd_I\KeyCardSFX[i] = LoadSound_Strict("SFX\Interact\KeyCardUse" + i + ".ogg")
			snd_I\ScannerSFX[i] = LoadSound_Strict("SFX\Interact\ScannerUse" + i + ".ogg")
			
			snd_I\DoorBudgeSFX[i] = LoadSound_Strict("SFX\Interact\DoorBudge" + i + ".ogg")
			
			snd_I\GunshotSFX[i] = LoadSound_Strict("SFX\Character\Gunshot" + i + ".ogg")
			
			snd_I\HissSFX[i] = LoadSound_Strict("SFX\Room\Hiss" + i + ".ogg")
			
			RadioSFX(0, i) = LoadSound_Strict("SFX\Radio\RadioAlarm" + i + ".ogg")
			
			snd_I\LowBatterySFX[i] = LoadSound_Strict("SFX\Interact\LowBattery" + i + ".ogg")
			snd_I\KnobSFX[i] = LoadSound_Strict("SFX\Room\914Chamber\Knob" + i + ".ogg")
			
			StepSFX(5, 0, i) = LoadSound_Strict("SFX\Step\StepFluid" + i + ".ogg")
		EndIf
		If i < 3
			OpenDoorSFX(DEFAULT_DOOR, i) = LoadSound_Strict("SFX\Door\DoorOpen" + i + ".ogg") ; ~ Also one-sided door
			CloseDoorSFX(DEFAULT_DOOR, i) = LoadSound_Strict("SFX\Door\DoorClose" + i + ".ogg") ; ~ Also one-sided door
			OpenDoorSFX(ELEVATOR_DOOR, i) = LoadSound_Strict("SFX\Door\ElevatorOpen" + i + ".ogg")
			CloseDoorSFX(ELEVATOR_DOOR, i) = LoadSound_Strict("SFX\Door\ElevatorClose" + i + ".ogg")
			OpenDoorSFX(HEAVY_DOOR, i) = LoadSound_Strict("SFX\Door\Door2Open" + i + ".ogg")
			CloseDoorSFX(HEAVY_DOOR, i) = LoadSound_Strict("SFX\Door\Door2Close" + i + ".ogg")
			OpenDoorSFX(BIG_DOOR, i) = LoadSound_Strict("SFX\Door\BigDoorOpen" + i + ".ogg")
			CloseDoorSFX(BIG_DOOR, i) = LoadSound_Strict("SFX\Door\BigDoorClose" + i + ".ogg")
			OpenDoorSFX(OFFICE_DOOR, i) = LoadSound_Strict("SFX\Door\OfficeDoorOpen" + i + ".ogg")
			OpenDoorSFX(WOODEN_DOOR, i) = LoadSound_Strict("SFX\Door\WoodenDoorOpen" + i + ".ogg")
			snd_I\BigDoorErrorSFX[i] = LoadSound_Strict("SFX\Door\BigDoorError" + i + ".ogg")
			
			snd_I\NeckSnapSFX[i] = LoadSound_Strict("SFX\SCP\173\NeckSnap" + i + ".ogg")
			CoughSFX(0, i) = LoadSound_Strict("SFX\Character\D9341\Cough" + i + ".ogg")
			CoughSFX(1, i) = LoadSound_Strict("SFX\Character\D9341\Cough" + i + "Gas.ogg")
			
			snd_I\SCP106SFX[i] = LoadSound_Strict("SFX\SCP\106\Corrosion" + i + ".ogg")
			snd_I\SCP106SFX[i + 6] = LoadSound_Strict("SFX\SCP\106\WallDecay" + i + ".ogg")
			
			snd_I\SCP173SFX[i] = LoadSound_Strict("SFX\SCP\173\Rattle" + i + ".ogg")
			
			snd_I\LightSFX[i] = LoadSound_Strict("SFX\Room\Light" + i + ".ogg")
			
			StepSFX(2, 0, i) = LoadSound_Strict("SFX\Step\StepPD" + i + ".ogg")
			StepSFX(3, 0, i) = LoadSound_Strict("SFX\Step\StepCloth" + i + ".ogg")
			StepSFX(4, 0, i) = LoadSound_Strict("SFX\Step\StepForest" + i + ".ogg")
		EndIf
		If i < 4
			snd_I\DecaySFX[i] = LoadSound_Strict("SFX\SCP\106\Decay" + i + ".ogg")
			
			snd_I\DripSFX[i] = LoadSound_Strict("SFX\Character\D9341\BloodDrip" + i + ".ogg")
			
			snd_I\PickSFX[i] = LoadSound_Strict("SFX\Interact\PickItem" + i + ".ogg")
			
			snd_I\AlarmSFX[i] = LoadSound_Strict("SFX\Alarm\Alarm" + i + ".ogg")
			
			snd_I\Step2SFX[i + 3] = LoadSound_Strict("SFX\Step\NPCs\939_966\StepMetal" + i + ".ogg")
			If i < 3
				snd_I\Step2SFX[i] = LoadSound_Strict("SFX\Step\NPCs\MTF\StepMetal" + i + ".ogg")
				snd_I\Step2SFX[i + 7] = LoadSound_Strict("SFX\Step\NPCs\049\StepMetal" + i + ".ogg")
				snd_I\Step2SFX[i + 10] = LoadSound_Strict("SFX\Step\NPCs\096\Step" + i + ".ogg")
			EndIf
		EndIf
		If i < 5
			BreathSFX(0, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + ".ogg")
			BreathSFX(1, i) = LoadSound_Strict("SFX\Character\D9341\Breath" + i + "Gas.ogg")
		EndIf
		If i < 6
			snd_I\RustleSFX[i] = LoadSound_Strict("SFX\SCP\372\Rustle" + i + ".ogg")
		EndIf
		If i < 8
			StepSFX(0, 0, i) = LoadSound_Strict("SFX\Step\Step" + i + ".ogg")
			StepSFX(0, 1, i) = LoadSound_Strict("SFX\Step\Run" + i + ".ogg")
			StepSFX(1, 0, i) = LoadSound_Strict("SFX\Step\StepMetal" + i + ".ogg")
			StepSFX(1, 1, i) = LoadSound_Strict("SFX\Step\RunMetal" + i + ".ogg")
		EndIf
		If i < 9
			RadioSFX(1, i) = LoadSound_Strict("SFX\Radio\SCPRadio" + i + ".ogg")
		EndIf
		If i < 14
			snd_I\HorrorSFX[i] = LoadSound_Strict("SFX\Horror\Horror" + i + ".ogg")
		EndIf
		snd_I\DamageSFX[i] = LoadSound_Strict("SFX\Character\D9341\Damage" + i + ".ogg")
	Next
	
	snd_I\DoorOpen079 = LoadSound_Strict("SFX\Door\DoorOpen079.ogg")
	snd_I\DoorClose079 = LoadSound_Strict("SFX\Door\DoorClose079.ogg")
	
	snd_I\DoorLockSFX = LoadSound_Strict("SFX\Interact\DoorLock.ogg")
	
	snd_I\OpenDoorFastSFX = LoadSound_Strict("SFX\Door\DoorOpenFast.ogg")
	snd_I\CautionSFX = LoadSound_Strict("SFX\Room\LockroomSiren.ogg")
	
	snd_I\CameraSFX = LoadSound_Strict("SFX\Room\Camera.ogg")
	
	snd_I\StoneDragSFX = LoadSound_Strict("SFX\SCP\173\StoneDrag.ogg")
	
	snd_I\BulletMissSFX = LoadSound_Strict("SFX\Character\BulletMiss.ogg")
	snd_I\BulletHitSFX = LoadSound_Strict("SFX\Character\BulletHit.ogg")
	
	snd_I\TeslaIdleSFX = LoadSound_Strict("SFX\Room\Tesla\Idle.ogg")
	snd_I\TeslaActivateSFX = LoadSound_Strict("SFX\Room\Tesla\WindUp.ogg")
	snd_I\TeslaPowerUpSFX = LoadSound_Strict("SFX\Room\Tesla\PowerUp.ogg")
	snd_I\TeslaShockSFX = LoadSound_Strict("SFX\Room\Tesla\Shock.ogg")
	
	snd_I\MagnetUpSFX = LoadSound_Strict("SFX\Room\106Chamber\MagnetUp.ogg") 
	snd_I\MagnetDownSFX = LoadSound_Strict("SFX\Room\106Chamber\MagnetDown.ogg")
	
	snd_I\BurstSFX = LoadSound_Strict("SFX\Room\TunnelBurst.ogg")
	
	snd_I\Death914SFX = LoadSound_Strict("SFX\SCP\914\PlayerDeath.ogg") 
	snd_I\Use914SFX = LoadSound_Strict("SFX\SCP\914\PlayerUse.ogg")
	snd_I\MachineSFX = LoadSound_Strict("SFX\SCP\914\Refining.ogg")
	
	snd_I\LeverSFX = LoadSound_Strict("SFX\Interact\LeverFlip.ogg") 
	
	snd_I\LightOffSFX = LoadSound_Strict("SFX\Room\LightSwitch.ogg")
	
	snd_I\RadioSquelch = LoadSound_Strict("SFX\Radio\Squelch.ogg")
	snd_I\RadioStatic = LoadSound_Strict("SFX\Radio\Static.ogg")
	snd_I\RadioStatic895 = LoadSound_Strict("SFX\Radio\Static895.ogg")
	snd_I\RadioBuzz = LoadSound_Strict("SFX\Radio\Buzz.ogg")
	
	snd_I\ElevatorBeepSFX = LoadSound_Strict("SFX\General\Elevator\Beep.ogg") 
	snd_I\ElevatorMoveSFX = LoadSound_Strict("SFX\General\Elevator\Moving.ogg") 
	
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
	
	snd_I\SCP106SFX[3] = LoadSound_Strict("SFX\SCP\106\Laugh.ogg")
	snd_I\SCP106SFX[4] = LoadSound_Strict("SFX\SCP\106\Breathing.ogg")
	snd_I\SCP106SFX[5] = LoadSound_Strict("SFX\Room\PocketDimension\Enter.ogg")
	
	snd_I\HeartBeatSFX = LoadSound_Strict("SFX\Character\D9341\HeartBeat.ogg")
	
	snd_I\ApacheSFX = LoadSound_Strict("SFX\Character\Apache\Propeller.ogg")
	
	snd_I\VehicleSFX[0] = LoadSound_Strict("SFX\Character\Vehicle\Idle.ogg")
	snd_I\VehicleSFX[1] = LoadSound_Strict("SFX\Character\Vehicle\Move.ogg")
	
	snd_I\MissSFX = LoadSound_Strict("SFX\Character\Miss.ogg")
	
	snd_I\BreathGasRelaxedSFX = LoadSound_Strict("SFX\Character\D9341\BreathGasRelaxed.ogg")
	
	snd_I\CrouchSFX = LoadSound_Strict("SFX\Character\D9341\Crouch.ogg")
	
	snd_I\SCRAMBLESFX = LoadSound_Strict("SFX\Interact\SCRAMBLE.ogg")
	
	snd_I\NVGSFX[0] = LoadSound_Strict("SFX\Interact\NVGOn.ogg")
	snd_I\NVGSFX[1] = LoadSound_Strict("SFX\Interact\NVGOff.ogg")
	
	snd_I\BlindsSFX = LoadSound_Strict("SFX\Room\Blinds.ogg")
	
	snd_I\SparkShortSFX = LoadSound_Strict("SFX\Room\SparkShort.ogg")
End Function

Function RemoveSoundInstances%()
	Local i%
	
	For i = 0 To 13
		If i < 2
			RadioSFX(0, i) = 0
			snd_I\VehicleSFX[i] = 0
			snd_I\NVGSFX[i] = 0
			snd_I\LowBatterySFX[i] = 0
			snd_I\KnobSFX[i] = 0
			snd_I\GunshotSFX[i] = 0
			snd_I\DoorBudgeSFX[i] = 0
			snd_I\KeyCardSFX[i] = 0
			snd_I\ScannerSFX[i] = 0
			snd_I\HissSFX[i] = 0
		EndIf
		If i < 3
			OpenDoorSFX(DEFAULT_DOOR, i) = 0
			CloseDoorSFX(DEFAULT_DOOR, i) = 0
			OpenDoorSFX(ELEVATOR_DOOR, i) = 0
			CloseDoorSFX(ELEVATOR_DOOR, i) = 0
			OpenDoorSFX(HEAVY_DOOR, i) = 0
			CloseDoorSFX(HEAVY_DOOR, i) = 0
			OpenDoorSFX(BIG_DOOR, i) = 0
			CloseDoorSFX(BIG_DOOR, i) = 0
			OpenDoorSFX(OFFICE_DOOR, i) = 0
			OpenDoorSFX(WOODEN_DOOR, i) = 0
			snd_I\BigDoorErrorSFX[i] = 0
			snd_I\SCP173SFX[i] = 0
			snd_I\NeckSnapSFX[i] = 0
			CoughSFX(0, i) = 0
			CoughSFX(1, i) = 0
			snd_I\LightSFX[i] = 0
		EndIf
		If i < 4
			snd_I\DecaySFX[i] = 0
			snd_I\PickSFX[i] = 0
			snd_I\AlarmSFX[i] = 0
			snd_I\DripSFX[i] = 0
		EndIf
		If i < 5
			BreathSFX(0, i) = 0
			BreathSFX(1, i) = 0
		EndIf
		If i < 6
			AmbientSFXAmount[i] = 0
			snd_I\RustleSFX[i] = 0
		EndIf
		If i < 8
			StepSFX(0, 0, i) = 0
			StepSFX(0, 1, i) = 0
			StepSFX(1, 0, i) = 0
			StepSFX(1, 1, i) = 0
			If i < 3
				StepSFX(2, 0, i) = 0
				StepSFX(3, 0, i) = 0
				StepSFX(4, 0, i) = 0
			EndIf
			If i < 2 Then StepSFX(5, 0, i) = 0
			snd_I\IntroSFX[i] = 0
			NPCSound[i] = 0
		EndIf
		If i < 9
			RadioSFX(1, i) = 0
			snd_I\SCP106SFX[i] = 0
		EndIf
		If i < 11
			RoomAmbience[i] = 0
		EndIf
		If i < 13
			snd_I\Step2SFX[i] = 0
		EndIf
		If i < 14
			snd_I\HorrorSFX[i] = 0
		EndIf
		snd_I\DamageSFX[i] = 0
	Next
	snd_I\DoorClose079 = 0
	snd_I\DoorOpen079 = 0
	
	snd_I\DoorLockSFX = 0
	
	snd_I\OpenDoorFastSFX = 0
	snd_I\CautionSFX = 0
	
	snd_I\CameraSFX = 0
	
	snd_I\StoneDragSFX = 0
	
	snd_I\BulletMissSFX = 0
	snd_I\BulletHitSFX = 0
	
	snd_I\TeslaIdleSFX = 0
	snd_I\TeslaActivateSFX = 0
	snd_I\TeslaPowerUpSFX = 0
	snd_I\TeslaShockSFX = 0
	
	snd_I\MagnetUpSFX = 0
	snd_I\MagnetDownSFX = 0
	
	snd_I\BurstSFX = 0
	
	snd_I\Death914SFX = 0
	snd_I\Use914SFX = 0
	snd_I\MachineSFX = 0
	
	snd_I\LeverSFX = 0
	
	snd_I\LightOffSFX = 0
	
	snd_I\RadioSquelch = 0
	snd_I\RadioStatic = 0
	snd_I\RadioStatic895 = 0
	snd_I\RadioBuzz = 0
	
	snd_I\ElevatorBeepSFX = 0
	snd_I\ElevatorMoveSFX = 0
	
	snd_I\HeartBeatSFX = 0
	
	snd_I\ApacheSFX = 0
	
	snd_I\MissSFX = 0
	
	snd_I\BreathGasRelaxedSFX = 0
	
	snd_I\CrouchSFX = 0
	
	snd_I\SCRAMBLESFX = 0
	
	snd_I\FemurBreakerSFX = 0
	
	snd_I\ExplosionSFX = 0
	
	snd_I\VomitSFX = 0
	
	snd_I\BlindsSFX = 0
	
	snd_I\SparkShortSFX = 0
	
	snd_I\SinkHoleSFX = 0
	
	Delete(snd_I) : snd_I = Null
End Function

Function LoadEvents%()
	If opt\IntroEnabled Then CreateEvent(e_cont1_173_intro, r_cont1_173_intro, 0)
	CreateEvent(e_cont1_173, r_cont1_173, 0)
	
	CreateEvent(e_dimension_106, r_dimension_106, 0)
	
	; ~ There's a 7% chance that SCP-106 appears in the rooms named r_room2_5_hcz
	CreateEvent(e_room2_5_hcz_106, r_room2_5_hcz, 0, 0.07 + (0.1 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The chance for SCP-173 appearing in the first r_room2c_gw_lcz is about 66%
	; ~ There's a 30% chance that it appears in the later r_room2c_gw_lcz
	If Rand(3) < 3 Then CreateEvent(e_173_appearing, r_room2c_gw_lcz, 0)
	CreateEvent(e_173_appearing, r_room2c_gw_lcz, 1, 0.3 + (0.5 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_trick, r_room2_lcz, 0, 0.15)
	CreateEvent(e_trick, r_room2_3_lcz, 0, 0.15)
	
	CreateEvent(e_room2_ez_035, r_room2_ez, 0)
	
	CreateEvent(e_trick_item, r_room2c_2_lcz, 0, 0.15)
	CreateEvent(e_trick_item, r_room2c_2_ez, 0, 0.15)
	CreateEvent(e_trick_item, r_room4_2_ez, 1, 0.15)
	CreateEvent(e_trick_item, r_room2_4_ez, 1, 0.2)
	
	CreateEvent(e_1048_a, r_room2_lcz, 1, 1.0)
	CreateEvent(e_1048_a, r_room2_3_lcz, 1, 0.3 + (0.3 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_1048_a, r_room2_5_lcz, 0, 0.2 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_room2_storage, r_room2_storage, 0)
	
	; ~ SCP-096 spawns in the first (and last)
	CreateEvent(e_room2c_gw_ez_096, r_room2c_gw_ez, 0)
	
	CreateEvent(e_room1_dead_end_106, r_room1_dead_end_lcz, Rand(0, 1))
	CreateEvent(e_room1_dead_end_guard, r_room1_dead_end_ez, Rand(0, 1))
	
	CreateEvent(e_room2_scientists_2, r_room2_scientists_2, 0)
	
	CreateEvent(e_room2_2_lcz_fan, r_room2_2_lcz, 0, 1.0)
	
	CreateEvent(e_room2_elevator, r_room2_elevator, Rand(0, 1))
	
	CreateEvent(e_room3_storage, r_room3_storage, 0)
	
	CreateEvent(e_room2_6_hcz_smoke, r_room2_6_hcz, 0, 0.2)
	CreateEvent(e_room2_6_hcz_173, r_room2_6_hcz, 0, 0.3 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ SCP-173 appears in half of the r_room2_6_lcz-rooms
	CreateEvent(e_173_appearing, r_room2_6_lcz, 0, 0.5 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The anomalous duck in r_room2_2_ez-rooms
	CreateEvent(e_room2_2_ez_duck, r_room2_2_ez, 0, 0.7)
	
	CreateEvent(e_room2_closets, r_room2_closets, 0)
	
	CreateEvent(e_room2_cafeteria, r_room2_cafeteria, 0)
	
	CreateEvent(e_room3_hcz_duck, r_room3_hcz, 0)
	CreateEvent(e_room3_hcz_1048,r_room3_hcz, 1)
	
	CreateEvent(e_room2_servers_hcz, r_room2_servers_hcz, 0)
	
	CreateEvent(e_173_appearing, r_room3_2_ez, 0, 0.8)
	CreateEvent(e_room3_2_ez_duck, r_room3_2_ez, 1)
	CreateEvent(e_173_appearing, r_room3_3_ez, 0)
	
	; ~ The dead guard
	CreateEvent(e_room3_2_hcz_guard, r_room3_2_hcz, 0, 0.1)
	
	If Rand(5) < 5
		Select Rand(4)
			Case 1
				;[Block]
				CreateEvent(e_682_roar, r_room2_5_hcz, Rand(0, 2))
				;[End Block]
			Case 2
				;[Block]
				CreateEvent(e_682_roar, r_room3_hcz, Rand(0, 2))
				;[End Block]
			Case 3
				;[Block]
				CreateEvent(e_682_roar, r_room2_5_ez, 0)
				;[End Block]
			Case 4
				;[Block]
				CreateEvent(e_682_roar, r_room4_ez, 0)
				;[End Block]
		End Select
	EndIf
	
	CreateEvent(e_room2_nuke, r_room2_nuke, 0)
	
	CreateEvent(e_cont1_895, r_cont1_895, 0)
	
	CreateEvent(e_checkpoint, r_room2_checkpoint_lcz_hcz, 0, 1.0)
	CreateEvent(e_checkpoint, r_room2_checkpoint_hcz_ez, 0, 1.0)
	
	CreateEvent(e_door_closing, r_room3_lcz, 0, 0.1)
	CreateEvent(e_door_closing, r_room3_2_hcz, 0, 0.1)
	
	If Rand(2) = 1
		CreateEvent(e_106_victim, r_room3_lcz, Rand(2))
		CreateEvent(e_106_sinkhole, r_room3_2_lcz, Rand(2, 3))
	Else
		CreateEvent(e_106_victim, r_room3_2_lcz, Rand(2))
		CreateEvent(e_106_sinkhole, r_room3_lcz, Rand(2, 3))
	EndIf
	CreateEvent(e_106_sinkhole, r_room4_lcz, Rand(2))
	
	CreateEvent(e_cont1_079, r_cont1_079, 0)
	
	CreateEvent(e_cont2_049, r_cont2_049, 0)
	
	CreateEvent(e_cont2_012, r_cont2_012, 0)
	
	CreateEvent(e_cont1_035, r_cont1_035, 0)
	
	CreateEvent(e_cont2_008, r_cont2_008, 0)
	
	CreateEvent(e_cont1_106, r_cont1_106, 0)
	
	CreateEvent(e_cont3_372, r_cont3_372, 0)
	
	CreateEvent(e_cont1_914, r_cont1_914, 0)
	
	CreateEvent(e_room2_6_ez_789_j, r_room2_6_ez, 0)
	CreateEvent(e_room2_6_ez_guard, r_room2_6_ez, 1)
	
	CreateEvent(e_room2_2_hcz_106, r_room2_2_hcz, Rand(0, 3))
	
	CreateEvent(e_173_appearing, r_room2_4_hcz, 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_room2_test_hcz, r_room2_test_hcz, 0)
	CreateEvent(e_room2_test_lcz_173, r_room2_test_lcz, 0)
	
	CreateEvent(e_room2_mt, r_room2_mt, 0)
	
	CreateEvent(e_room2c_ec, r_room2c_ec, 0)
	
	CreateEvent(e_gate_a_entrance, r_gate_a_entrance, 0)
	CreateEvent(e_gate_a, r_gate_a, 0)
	CreateEvent(e_gate_b_entrance, r_gate_b_entrance, 0)
	CreateEvent(e_gate_b, r_gate_b, 0)
	
	CreateEvent(e_cont1_205, r_cont1_205, 0)
	
	CreateEvent(e_cont2_860_1, r_cont2_860_1, 0)
	
	CreateEvent(e_cont3_966, r_cont3_966, 0)
	
	CreateEvent(e_cont2_1123, r_cont2_1123, 0)
	
	CreateEvent(e_tesla, r_room2_tesla_lcz, 0, 1.0)
	CreateEvent(e_tesla, r_room2_tesla_hcz, 0, 1.0)
	CreateEvent(e_tesla, r_room2_tesla_ez, 0, 1.0)
	
	CreateEvent(e_room4_2_hcz_d, r_room4_2_hcz, 0)
	
	CreateEvent(e_room2_gw_2, r_room2_gw_2, 0)
	CreateEvent(e_gateway, r_room2_gw, 0, 1.0)
	CreateEvent(e_gateway, r_room3_gw, 0, 1.0)
	
	CreateEvent(e_dimension_1499, r_dimension_1499, 0)
	
	CreateEvent(e_cont2c_066_1162_arc, r_cont2c_066_1162_arc, 0)
	
	CreateEvent(e_cont2_500_1499, r_cont2_500_1499, 0)
	
	CreateEvent(e_room2_sl, r_room2_sl, 0)
	
	CreateEvent(e_room2_medibay, r_room2_medibay, 0)
	
	CreateEvent(e_room2_shaft, r_room2_shaft, 0)
	
	CreateEvent(e_096_spawn, r_room2_3_hcz, 0, 0.4 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room2_4_hcz, 0, 0.5 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room2_5_hcz, 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room2_hcz, 0, 0.4 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room3_hcz, 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room3_2_hcz, 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room3_3_hcz, 0, 0.7 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room4_hcz, 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_096_spawn, r_room4_2_hcz, 0, 0.7 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_173_appearing, r_room2_4_lcz, 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_room2_4_hcz_106, r_room2_4_hcz, 0, 0.07 + (0.1 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_room4_ic, r_room4_ic, 0)
	
	CreateEvent(e_cont2_409, r_cont2_409, 0)
	
	CreateEvent(e_cont1_005, r_cont1_005, 0)
	
	CreateEvent(e_room2_ic, r_room2_ic, 0)
End Function

Function LoadWayPoints%(LoadingStart% = 55)
	Local d.Doors, w.WayPoints, w2.WayPoints, r.Rooms, ClosestRoom.Rooms
	Local x#, y#, z#
	Local Dist#, Dist2#
	
	For d.Doors = Each Doors
		HideEntity(d\OBJ)
		If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
		HideEntity(d\FrameOBJ)
		
		If d\room = Null
			ClosestRoom.Rooms = Null
			Dist = 30.0
			For r.Rooms = Each Rooms
				x = Abs(EntityX(r\OBJ, True) - EntityX(d\FrameOBJ, True))
				If x < 20.0
					z = Abs(EntityZ(r\OBJ, True) - EntityZ(d\FrameOBJ, True))
					If z < 20.0
						Dist2 = (x * x) + (z * z)
						If Dist2 < Dist
							ClosestRoom = r
							Dist = Dist2
						EndIf
					EndIf
				EndIf
			Next
		Else
			ClosestRoom = d\room
		EndIf
		If (Not d\DisableWaypoint) And d\DoorType <> WOODEN_DOOR Then CreateWaypoint(d, ClosestRoom, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True) + 0.18, EntityZ(d\FrameOBJ, True))
	Next
	
	Local Amount% = 0
	
	For w.WayPoints = Each WayPoints
		EntityRadius(w\OBJ, 0.2)
		EntityPickMode(w\OBJ, 1, True)
		Amount = Amount + 1
	Next
	
	Local Number% = 0
	Local Iter% = 0
	Local i%, n%
	
	For w.WayPoints = Each WayPoints
		Number = Number + 1
		Iter = Iter + 1
		If Iter = 20
			RenderLoading(LoadingStart + Floor((30.0 / Amount) * Number), GetLocalString("loading", "waypoints"))
			Iter = 0
		EndIf
		
		w2.WayPoints = After(w)
		
		Local CanCreateWayPoint% = False
		
		While w2 <> Null
			If w\room = w2\room Lor w\door <> Null Lor w2\door <> Null
				Dist = EntityDistance(w\OBJ, w2\OBJ)
				
				If w\room\MaxWayPointY = 0.0 Lor w2\room\MaxWayPointY = 0.0
					CanCreateWayPoint = True
				Else
					If Abs(EntityY(w\OBJ) - EntityY(w2\OBJ)) <= w\room\MaxWayPointY Then CanCreateWayPoint = True
				EndIf
				
				If Dist < 7.0
					If CanCreateWayPoint
						If EntityVisible(w\OBJ, w2\OBJ)
							For i = 0 To 4
								If w\connected[i] = Null
									w\connected[i] = w2.WayPoints
									w\Dist[i] = Dist
									Exit
								EndIf
							Next
							
							For n = 0 To 4
								If w2\connected[n] = Null
									w2\connected[n] = w.WayPoints
									w2\Dist[n] = Dist
									Exit
								EndIf
							Next
						EndIf
					EndIf
				EndIf
			EndIf
			w2 = After(w2)
		Wend
	Next
	
	For d.Doors = Each Doors
		ShowEntity(d\OBJ)
		If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
		ShowEntity(d\FrameOBJ)
	Next
	
	For w.WayPoints = Each WayPoints
		EntityRadius(w\OBJ, 0.0)
		EntityPickMode(w\OBJ, 0, False)
		
		If opt\DebugMode
			For i = 0 To 4
				If w\connected[i] <> Null
					Local tLine% = CreateLine(EntityX(w\OBJ, True), EntityY(w\OBJ, True), EntityZ(w\OBJ, True), EntityX(w\connected[i]\OBJ, True), EntityY(w\connected[i]\OBJ, True), EntityZ(w\connected[i]\OBJ, True))
					
					EntityColor(tLine, 255.0, 0.0, 0.0)
					EntityParent(tLine, w\OBJ)
				EndIf
			Next
		EndIf
	Next
End Function

; ~ Textures Constants
;[Block]
Const MaxOverlayTextureIDAmount% = 4
Const MaxOverlayIDAmount% = 11
Const MaxIconIDAmount% = 13
Const MaxImageIDAmount% = 8
;[End Block]

Type Textures
	Field IconID%[MaxIconIDAmount]
	Field ImageID%[MaxImageIDAmount]
	Field OverlayTextureID%[MaxOverlayTextureIDAmount]
	Field OverlayID%[MaxOverlayIDAmount]
End Type

Global t.Textures

; ~ Collisions Constants
;[Block]
Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_DEAD% = 5
;[End Block]

Global SubjectName$
Global InFacility%
Global PlayerFallingPickDistance#

Global ShouldEntitiesFall%
Global HideDistance#
Global CoffinDistance#

Global RemoteDoorOn%

Type Player
	Field Terminated# = False
	Field KillAnim%, KillAnimTimer#, FallTimer#, DeathTimer#
	Field Sanity#, RestoreSanity%
	Field ForceMove#, ForceAngle#
	Field Playable%
	Field BlinkTimer#, BLINKFREQ#, BlinkEffect#, BlinkEffectTimer#, EyeIrritation#, EyeStuck#
	Field Stamina#, StaminaEffect#, StaminaEffectTimer#, StaminaMax#
	Field CameraShakeTimer#, Shake#, CameraShake#, BigCameraShake#
	Field Vomit%, VomitTimer#, Regurgitate%
	Field HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#
	Field Injuries#, Bloodloss#, PrevInjuries#, PrevBloodloss#, HealTimer#
	Field DropSpeed#, HeadDropSpeed#, CurrSpeed#
	Field Crouch%, CrouchState#, Lean#
	Field SndVolume#
	Field SelectedEnding%, EndingScreen%, EndingTimer#
	Field CreditsScreen%, CreditsTimer#
	Field BlurVolume#, BlurTimer#
	Field LightBlink#, LightFlash#
	Field CurrCameraZoom#
	Field CameraFogDist#
	Field RefinedItems%
	Field Deaf%, DeafTimer#
	Field Zombie%
	Field Detected%
	Field ExplosionTimer#
	Field Zone%
	Field Collider%, Head%
	Field StopHidingTimer#
	Field CurrFunds%, UsedMastercard%
	Field InsideElevator%
End Type

Global me.Player

Function LoadData%()
	Local TempStr$
	
	SubFile = JsonParseFromFile(SubtitlesFile)
	LocalSubFile = JsonParseFromFile(lang\LanguagePath + SubtitlesFile)
	SubColors = JsonGetValue(SubFile, "colors")
	LocalSubColors = JsonGetValue(LocalSubFile, "colors")
	SubtitlesInit = True
	
	SCP1499Chunks = JsonGetArray(JsonParseFromFile(SCP1499ChunksFile))
	
	SubjectName = GetLocalString("misc", "subject")
	PlayerFallingPickDistance = 10.0
	
	Collisions(HIT_PLAYER, HIT_MAP, 2, 2)
	Collisions(HIT_PLAYER, HIT_PLAYER, 1, 3)
	Collisions(HIT_ITEM, HIT_MAP, 2, 2)
	Collisions(HIT_APACHE, HIT_APACHE, 1, 2)
	Collisions(HIT_DEAD, HIT_MAP, 2, 2)
	
	LoadRoomTemplates("Data\rooms.ini")
	
	ShouldEntitiesFall = True
	HideDistance = 17.0
	CoffinDistance = 100.0
	
	QuickLoadPercent = -1
	
	EscapeSecondsTimer = 70.0
	
	chs.Cheats = New Cheats
	me.Player = New Player
	wi.WearableItems = New WearableItems
	
	I_005.SCP005 = New SCP005
	I_008.SCP008 = New SCP008
	I_035.SCP035 = New SCP035
	I_268.SCP268 = New SCP268
	I_294.SCP294 = New SCP294
	Init294Drinks()
	I_409.SCP409 = New SCP409
	I_427.SCP427 = New SCP427
	I_500.SCP500 = New SCP500
	I_714.SCP714 = New SCP714
	I_1025.SCP1025 = New SCP1025
	I_1499.SCP1499 = New SCP1499
	I_966.SCP966 = New SCP966
	I_1048A.SCP1048A = New SCP1048A
	
	as.AutoSave = New AutoSave
	
	msg.Messages = New Messages
	
	I_Zone.MapZones = New MapZones
	
	bk.BrokenDoor = New BrokenDoor
	
	InitAchievements()
	LoadAchievementsFile()
	igm.InGameMenu = New InGameMenu
	
	t.Textures = New Textures
	
	If SelectedCustomMap = Null
		TempStr = Format(GetLocalString("menu", "new.seed2"), RandomSeed)
	Else
		Local Name$ = ConvertToUTF8(SelectedCustomMap\Name)
		
		If Len(Name) > 15
			TempStr = Format(GetLocalString("menu", "new.map"), Left(Name, 14) + "..")
		Else
			TempStr = Format(GetLocalString("menu", "new.map"), Name)
		EndIf
	EndIf
	SetErrorMsg(8, TempStr)
End Function

Global Camera%

Function LoadEntities%()
	CatchErrors("LoadEntities()")
	
	Local i%, Tex%
	Local Name$, Test%, File$
	
	DeInitMainMenuAssets()
	
	RenderLoading(0, GetLocalString("loading", "data"))
	
	LoadData()
	
	InitSubtitlesAssets()
	
	RenderLoading(3, GetLocalString("loading", "player"))
	
	SoundEmitter = CreatePivot()
	
	me\Collider = CreatePivot()
	EntityRadius(me\Collider, 0.15, 0.30)
	EntityPickMode(me\Collider, 1)
	EntityType(me\Collider, HIT_PLAYER)
	
	me\Head = CreatePivot()
	EntityRadius(me\Head, 0.15)
	EntityType(me\Head, HIT_PLAYER)
	
	Camera = CreateCamera()
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, 0.1, me\CameraFogDist)
	CameraFogColor(Camera, 30.0, 30.0, 30.0)
	CameraRange(Camera, 0.01, me\CameraFogDist)
	CameraClsColor(Camera, 30.0, 30.0, 30.0)
	AmbientLight(30.0, 30.0, 30.0)
	
	ParticleCam = Camera
	ParticlePiv = CreatePivot()
	
	RenderLoading(5, GetLocalString("loading", "icons"))
	
	t\IconID[0] = ScaleImageEx(LoadImage_Strict("GFX\HUD\walk_icon.png"), MenuScale, MenuScale)
	t\IconID[1] = ScaleImageEx(LoadImage_Strict("GFX\HUD\sprint_icon.png"), MenuScale, MenuScale)
	t\IconID[2] = ScaleImageEx(LoadImage_Strict("GFX\HUD\crouch_icon.png"), MenuScale, MenuScale)
	For i = 3 To 4
		t\IconID[i] = LoadImage_Strict("GFX\HUD\blink_icon(" + (i - 2) + ").png")
		t\IconID[i] = ScaleImageEx(t\IconID[i], MenuScale, MenuScale)
	Next
	For i = 5 To 6
		t\IconID[i] = ScaleImageEx(LoadImage_Strict("GFX\HUD\hand_symbol(" + (i - 4) + ").png"), MenuScale, MenuScale)
	Next
	t\IconID[7] = ScaleImageEx(LoadImage_Strict("GFX\HUD\shield_icon.png"), MenuScale, MenuScale)
	
	t\IconID[8] = ScaleImageEx(LoadImage_Strict("GFX\HUD\scp_268_icon.png"), MenuScale, MenuScale)
	
	t\IconID[9] = ScaleImageEx(LoadImage_Strict("GFX\Menu\QuickLoading.png"), MenuScale, MenuScale)
	
	For i = 0 To 3
		t\IconID[i + 10] = ScaleImageEx(LoadImage_Strict("GFX\HUD\arrow_symbol.png"), MenuScale, MenuScale)
		RotateImage(t\IconID[i + 10], i * 90.0)
		HandleImage(t\IconID[i + 10], 0, 0)
	Next
	
	t\ImageID[0] = ScaleImageEx(LoadImage_Strict("GFX\Menu\pause_menu.png"), MenuScale, MenuScale)
	
	t\ImageID[1] = ScaleImageEx(LoadImage_Strict("GFX\HUD\blink_meter(2).png"), MenuScale, MenuScale)
	
	For i = 2 To 3
		t\ImageID[i] = ScaleImageEx(LoadImage_Strict("GFX\HUD\stamina_meter(" + (i - 1) + ").png"), MenuScale, MenuScale)
	Next
	
	t\ImageID[4] = ScaleImageEx(LoadImage_Strict("GFX\HUD\keypad_HUD.png"), MenuScale, MenuScale)
	
	t\ImageID[5] = ScaleImageEx(LoadImage_Strict("GFX\Overlays\scp_294_overlay.png"), MenuScale, MenuScale)
	
	t\ImageID[6] = ScaleImageEx(LoadAnimImage_Strict("GFX\HUD\NVG_batteries.png", 64, 64, 0, 3), MenuScale, MenuScale, 3)
	MaskImage(t\ImageID[6], 255, 0, 255)
	
	t\ImageID[7] = CreateImage(opt\GraphicWidth, opt\GraphicHeight)
	
	RenderLoading(10, GetLocalString("loading", "textures"))
	
	LoadMissingTexture()
	
	AmbientLightRoomTex = CreateTextureUsingCacheSystem(2, 2, 1 + 256)
	TextureBlend(AmbientLightRoomTex, 2)
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
	
	CreateBlurImage()
	
	; ~ Overlays
	Local OverlayScale# = GraphicHeightFloat / GraphicWidthFloat
	
	t\OverlayTextureID[0] = LoadTexture_Strict("GFX\Overlays\vignette_overlay.png", 1, DeleteAllTextures, False) ; ~ VIGNETTE
	t\OverlayID[0] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[0], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[0], t\OverlayTextureID[0])
	EntityBlend(t\OverlayID[0], 2)
	EntityOrder(t\OverlayID[0], -1000)
	MoveEntity(t\OverlayID[0], 0.0, 0.0, 1.0)
	
	Tex = LoadTexture_Strict("GFX\Overlays\gas_mask_overlay.png", 1, DeleteMapTextures, False) ; ~ GAS MASK
	t\OverlayID[1] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[1], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[1], Tex)
	EntityBlend(t\OverlayID[1], 2)
	EntityFX(t\OverlayID[1], 1)
	EntityOrder(t\OverlayID[1], -1003)
	MoveEntity(t\OverlayID[1], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\hazmat_suit_overlay.png", 1, DeleteMapTextures, False) ; ~ HAZMAT SUIT
	t\OverlayID[2] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[2], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[2], Tex)
	EntityBlend(t\OverlayID[2], 2)
	EntityFX(t\OverlayID[2], 1)
	EntityOrder(t\OverlayID[2], -1003)
	MoveEntity(t\OverlayID[2], 0, 0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\scp_008_overlay.png", 1, DeleteMapTextures, False) ; ~ SCP-008
	t\OverlayID[3] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[3], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[3], Tex)
	EntityBlend(t\OverlayID[3], 3)
	EntityFX(t\OverlayID[3], 1)
	EntityOrder(t\OverlayID[3], -1003)
	MoveEntity(t\OverlayID[3], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	t\OverlayTextureID[1] = LoadTexture_Strict("GFX\Overlays\night_vision_goggles_overlay.png", 1, DeleteAllTextures, False) ; ~ NIGHT VISION GOGGLES
	t\OverlayID[4] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[4], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[4], t\OverlayTextureID[1])
	EntityBlend(t\OverlayID[4], 2)
	EntityFX(t\OverlayID[4], 1)
	EntityOrder(t\OverlayID[4], -1003)
	MoveEntity(t\OverlayID[4], 0.0, 0.0, 1.0)
	
	t\OverlayTextureID[2] = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO_HALF, SMALLEST_POWER_TWO_HALF, 1 + 2) ; ~ DARK
	SetBuffer(TextureBuffer(t\OverlayTextureID[2]))
	Cls()
	SetBuffer(BackBuffer())
	t\OverlayID[5] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[5], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[5], t\OverlayTextureID[2])
	EntityBlend(t\OverlayID[5], 1)
	EntityOrder(t\OverlayID[5], -1002)
	MoveEntity(t\OverlayID[5], 0.0, 0.0, 1.0)
	EntityAlpha(t\OverlayID[5], 0.0)
	
	Tex = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO_HALF, SMALLEST_POWER_TWO_HALF, 1 + 2) ; ~ LIGHT
	SetBuffer(TextureBuffer(Tex))
	ClsColor(255, 255, 255)
	Cls()
	ClsColor(0, 0, 0)
	SetBuffer(BackBuffer())
	t\OverlayID[6] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[6], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[6], Tex)
	EntityBlend(t\OverlayID[6], 1)
	EntityOrder(t\OverlayID[6], -1002)
	MoveEntity(t\OverlayID[6], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\scp_409_overlay.png", 1, DeleteMapTextures, False) ; ~ SCP-409
	t\OverlayID[7] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[7], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[7], Tex)
	EntityBlend(t\OverlayID[7], 3)
	EntityFX(t\OverlayID[7], 1)
	EntityOrder(t\OverlayID[7], -1003)
	MoveEntity(t\OverlayID[7], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0	
	
	Tex = LoadTexture_Strict("GFX\Overlays\helmet_overlay.png", 1, DeleteMapTextures, False) ; ~ HELMET
	t\OverlayID[8] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[8], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[8], Tex)
	EntityBlend(t\OverlayID[8], 2)
	EntityFX(t\OverlayID[8], 1)
	EntityOrder(t\OverlayID[8], -1003)
	MoveEntity(t\OverlayID[8], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\fog_gas_mask_overlay.png", 1, DeleteMapTextures, False) ; ~ FOG IN GAS MASK
	t\OverlayID[9] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[9], 1.0, OverlayScale)
	EntityTexture(t\OverlayID[9], Tex)
	EntityBlend(t\OverlayID[9], 3)
	EntityFX(t\OverlayID[9], 1)
	EntityOrder(t\OverlayID[9], -1002)
	MoveEntity(t\OverlayID[9], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	For i = 1 To 9
		HideEntity(t\OverlayID[i])
	Next
	t\OverlayTextureID[3] = LoadTexture_Strict("GFX\Overlays\tesla_overlay.png", 1 + 2, DeleteAllTextures, False)
	
	LoadDecals()
	
	LoadParticles()
	
	LoadMaterials(MaterialsFile)
	
	RenderLoading(13, GetLocalString("loading", "models"))
	
	LoadDoors()
	
	LoadNPCs()
	
	LoadLevers()
	
	LoadMonitors()
	
	LoadSecurityCams()
	
	LoadMisc()
	
	LoadItems()
	
	RenderLoading(15, GetLocalString("loading", "chunks"))
	
	SetChunkDataValues()
	
	RenderLoading(20, GetLocalString("loading", "tracks"))
	
	UserTrackMusicAmount = 0
	If opt\UserTrackMode > 0
		Local DirPath$ = "SFX\Radio\UserTracks\"
		
		If FileType(DirPath) <> 2 Then CreateDir(DirPath)
		
		Local Dir% = ReadDir(DirPath)
		
		Repeat
			File = NextFile(Dir)
			If File = "" Then Exit
			If FileType(DirPath + File) = 1
				Test = LoadSound(DirPath + File)
				If Test <> 0
					UserTrackName[UserTrackMusicAmount] = File
					UserTrackMusicAmount = UserTrackMusicAmount + 1
				EndIf
				FreeSound(Test) : Test = 0
			EndIf
		Forever
		CloseDir(Dir)
	EndIf
	
	RenderLoading(25, GetLocalString("loading", "graphic"))
	
	AntiAlias(opt\AntiAliasing)
	TextureLodBias(opt\TextureDetailsLevel)
	TextureAnisotropic(opt\AnisotropicLevel)
	
	RenderLoading(30, GetLocalString("loading", "console"))
	
	ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
	
	CreateConsoleMsg("Console commands: ")
	CreateConsoleMsg(" - help [page]")
	CreateConsoleMsg(" - teleport [room name]")
	CreateConsoleMsg(" - godmode [on / off]")
	CreateConsoleMsg(" - noclip [on / off]")
	CreateConsoleMsg(" - infinitestamina [on / off]")
	CreateConsoleMsg(" - noblink [on / off]")
	CreateConsoleMsg(" - notarget [on / off]")
	CreateConsoleMsg(" - noclipspeed [x] (default = 2.0)")
	CreateConsoleMsg(" - wireframe [on / off]")
	CreateConsoleMsg(" - debughud [category]")
	CreateConsoleMsg(" - camerafog [x]")
	CreateConsoleMsg(" - heal")
	CreateConsoleMsg(" - revive")
	CreateConsoleMsg(" - asd")
	CreateConsoleMsg(" - spawnitem [item name]")
	CreateConsoleMsg(" - 106retreat")
	CreateConsoleMsg(" - disable173 / enable173")
	CreateConsoleMsg(" - disable106 / enable106")
	CreateConsoleMsg(" - spawn [NPC type]")
	
	CatchErrors("Uncaught: LoadEntities()")
End Function

Function RemoveTextureInstances%()
	Local i%
	
	Local Achievements% = JsonGetArray(JsonGetValue(AchievementsArray, "achievements"))
	Local ArraySize% = JsonGetArraySize(Achievements)
	
	For i = 0 To ArraySize - 1
		FreeImage(S2IMapGet(AchievementsImages, JsonGetString(JsonGetValue(JsonGetArrayValue(Achievements, i), "id"))))
	Next
	FreeImage(S2IMapGet(AchievementsImages, "locked"))
	DestroyS2IMap(AchievementsIndex) : AchievementsIndex = 0
	DestroyS2IMap(AchievementsImages) : AchievementsImages = 0
	DestroyS2IMap(UnlockedAchievements) : UnlockedAchievements = 0
	AchievementsArray = 0
	LocalAchievementsArray = 0
	
	For i = 0 To MaxIconIDAmount - 1
		FreeImage(t\IconID[i]) : t\IconID[i] = 0
	Next
	For i = 0 To MaxImageIDAmount - 1
		FreeImage(t\ImageID[i]) : t\ImageID[i] = 0
	Next
	For i = 0 To MaxOverlayTextureIDAmount - 1
		t\OverlayTextureID[i] = 0
	Next
	For i = 0 To MaxOverlayIDAmount - 1
		If t\OverlayID[i] <> 0 Then FreeEntity(t\OverlayID[i]) : t\OverlayID[i] = 0
	Next
	Delete(t) : t = Null
End Function

Function Init294Drinks%()
	Local LocalDrinks% = JsonParseFromFile(lang\LanguagePath + SCP294File)
	Local i%, j%
	
	If JsonIsArray(LocalDrinks) ; ~ Has localized scp294 drinks -> Use localized only
		I_294\Drinks = JsonGetArray(LocalDrinks)
	Else
		I_294\Drinks = JsonGetArray(JsonParseFromFile(SCP294File))
	EndIf
	
	I_294\DrinksMap = CreateS2IMap()
	
	Local ArraySize% = JsonGetArraySize(I_294\Drinks)
	
	For i = 0 To ArraySize - 1
		Local DrinkNames% = JsonGetArray(JsonGetValue(JsonGetArrayValue(I_294\Drinks, i), "name"))
		Local DrinkArraySize% = JsonGetArraySize(DrinkNames)
		
		For j = 0 To DrinkArraySize - 1
			S2IMapSet(I_294\DrinksMap, Upper(JsonGetString(JsonGetArrayValue(DrinkNames, j))), i)
		Next
	Next
End Function

Function InitNewGame%()
	CatchErrors("InitNewGame()")
	
	Local de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events, rt.RoomTemplates, p.Props
	Local i%, Tex%
	
	LoadEntities()
	LoadSounds()
	
	me\CameraFogDist = 6.0
	
	IsBlackOut = False : PrevIsBlackOut = False
	RemoteDoorOn = True
	
	InitOtherStuff()
	
	MaxItemAmount = SelectedDifficulty\InventorySlots
	Dim Inventory.Items(MaxItemAmount)
	
	RenderLoading(50, GetLocalString("loading", "stuff"))
	
	me\BlinkTimer = -10.0 : me\BlinkEffect = 1.0 : me\Stamina = 100.0 : me\StaminaEffect = 1.0 : me\HeartBeatRate = 70.0
	
	I_005\ChanceToSpawn = Rand(3)
	
	Remove714Timer = 500.0
	RemoveHazmatTimer = 500.0
	
	CODE_DR_MAYNARD = 0
	For i = 0 To 3
		CODE_DR_MAYNARD = CODE_DR_MAYNARD + (Rand(9) * (10 ^ i))
	Next
	If CODE_DR_MAYNARD = CODE_DR_HARP Lor CODE_DR_MAYNARD = CODE_CONT1_035 Lor CODE_DR_MAYNARD = CODE_DR_L Then CODE_DR_MAYNARD = CODE_DR_MAYNARD + 1
	
	CODE_O5_COUNCIL = ((CODE_DR_MAYNARD * 2) Mod 10000)
	If CODE_O5_COUNCIL < 1000 Then CODE_O5_COUNCIL = CODE_O5_COUNCIL + 1000
	
	CODE_MAINTENANCE_TUNNELS = ((CODE_DR_MAYNARD * 3) Mod 10000)
	If CODE_MAINTENANCE_TUNNELS < 1000 Then CODE_MAINTENANCE_TUNNELS = CODE_MAINTENANCE_TUNNELS + 1000
	
	CODE_DR_GEARS = ((CODE_DR_MAYNARD * 4) Mod 10000)
	If CODE_DR_GEARS < 1000 Then CODE_DR_GEARS = CODE_DR_GEARS + 1000
	
	RenderLoading(55, GetLocalString("loading", "rooms"))
	
	If SelectedCustomMap = Null
		CreateMap()
	Else
		LoadMap(CustomMapsPath + SelectedCustomMap\Name)
	EndIf
	
	LoadWayPoints()
	
	n_I\Curr173 = CreateNPC(NPCType173, 0.0, -500.0, 0.0)
	n_I\Curr106 = CreateNPC(NPCType106, 0.0, -500.0, 0.0)
	n_I\Curr106\State = 70.0 * 60.0 * Rnd(12.0, 17.0)
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		Select d\DoorType
			Case DEFAULT_DOOR, ONE_SIDED_DOOR, SCP_914_DOOR
				;[Block]
				MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
				If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
				;[End Block]
			Case OFFICE_DOOR
				;[Block]
				MoveEntity(d\OBJ, 92.0 * RoomScale, 0.0, 0.0)
				;[End Block]
			Case WOODEN_DOOR
				;[Block]
				MoveEntity(d\OBJ, 68.0 * RoomScale, 0.0, 0.0)
				;[End Block]
		End Select
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		For i = 0 To 1
			If d\Buttons[i] <> 0 Then EntityParent(d\Buttons[i], 0)
			If d\ElevatorPanel[i] <> 0 Then EntityParent(d\ElevatorPanel[i], 0)
		Next
	Next
	
	For it.Items = Each Items
		EntityType(it\Collider, HIT_ITEM)
		EntityParent(it\Collider, 0)
	Next
	
	For sc.SecurityCams = Each SecurityCams
		EntityParent(sc\BaseOBJ, 0)
		If sc\MonitorOBJ <> 0 Then EntityParent(sc\MonitorOBJ, 0)
	Next
	
	For p.Props = Each Props
		If p\TexPath <> ""
			; ~ Such a stupid way, but it works
			Tex = LoadTexture_Strict(p\TexPath)
			EntityTexture(p\OBJ, Tex)
			;DeleteSingleTextureEntryFromCache(Tex) : Tex = 0 ; ~ Actually we don't need this
			p\TexPath = ""
		EndIf
	Next
	
	For r.Rooms = Each Rooms
		If (Not r\RoomTemplate\DisableDecals)
			If Rand(4) = 1
				de.Decals = CreateDecal(Rand(DECAL_BLOOD_1, DECAL_BLOOD_2), r\x + Rnd(-2.0, 2.0), r\y + 0.005, r\z + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.4), Rnd(0.85, 0.95))
				EntityParent(de\OBJ, r\OBJ)
			EndIf
			If Rand(4) = 1
				de.Decals = CreateDecal(DECAL_CORROSIVE_1, r\x + Rnd(-2.0, 2.0), r\y + 0.005, r\z + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0, Rnd(0.5, 0.7), Rnd(0.7, 0.85))
				EntityParent(de\OBJ, r\OBJ)
			EndIf
		EndIf
		
		If r\RoomTemplate\RoomID = r_cont1_173 And (Not opt\IntroEnabled)
			TFormPoint(3584.0, 640.0, 3096.0, r\OBJ, 0)
			PositionEntity(me\Collider, TFormedX(), TFormedY(), TFormedZ())
			PlayerRoom = r
			it.Items = CreateItem("Class D Orientation Leaflet", it_paper, 1.0, 1.0, 1.0)
			it\Picked = True : it\Dropped = -1 : it\ItemTemplate\Found = True
			Inventory(0) = it
			HideEntity(it\Collider)
			EntityType(it\Collider, HIT_ITEM)
			EntityParent(it\Collider, 0)
			ItemAmount = ItemAmount + 1
		ElseIf r\RoomTemplate\RoomID = r_cont1_173_intro And opt\IntroEnabled
			TFormPoint(-4096.0 * RoomScale, 0.0, 0.0, r\OBJ, 0)
			PositionEntity(me\Collider, TFormedX(), 0.0, TFormedZ())
			PlayerRoom = r
		EndIf
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	Local ts.TempScreens, twp.TempWayPoints, tl.TempLights, tp.TempProps, tse.TempSoundEmitters
	
	For ts.TempScreens = Each TempScreens
		Delete(ts)
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete(twp)
	Next
	
	For tl.TempLights = Each TempLights
		Delete(tl)
	Next
	
	For tp.TempProps = Each TempProps
		Delete(tp)
	Next
	
	For tse.TempSoundEmitters = Each TempSoundEmitters
		Delete(tse)
	Next
	
	RenderLoading(85, GetLocalString("loading", "events"))
	
	If SelectedCustomMap = Null Then LoadEvents()
	
	For e.Events = Each Events
		Select e\EventID
			Case e_room2_nuke
				;[Block]
				e\EventState = 1.0
				;[End Block]
			Case e_cont1_106
				;[Block]
				e\EventState2 = 1.0
				;[End Block]
			Case e_room2_sl
				;[Block]
				e\EventState3 = 1.0
				;[End Block]
		End Select
	Next
	
	RenderLoading(90, GetLocalString("loading", "pos"))
	
	TurnEntity(me\Collider, 0.0, 180.0, 0.0)
	
	ResetEntity(me\Collider)
	
	MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	SetFontEx(fo\FontID[Font_Default])
	
	HidePointer()
	
	me\DropSpeed = 0.0
	
	ResetRender()
	UpdateNPCs()
	UpdateWorld()
	
	DeleteTextureEntriesFromCache(DeleteMapTextures)
	
	RenderLoading(100)
	
	fps\Factor[0] = 0.0
	fps\PrevTime = MilliSecs()
	
	ResetInput()
	
	CatchErrors("Uncaught: InitNewGame()")
End Function

Function InitLoadGame%()
	CatchErrors("InitLoadGame()")
	
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events, p.Props
	Local i%, x#, y#, z#, Tex%
	
	InitOtherStuff()
	LoadWayPoints()
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		For i = 0 To 1
			If d\Buttons[i] <> 0 Then EntityParent(d\Buttons[i], 0)
			If d\ElevatorPanel[i] <> 0 Then EntityParent(d\ElevatorPanel[i], 0)
		Next
	Next
	
	For sc.SecurityCams = Each SecurityCams
		EntityParent(sc\BaseOBJ, 0)
		If sc\MonitorOBJ <> 0 Then EntityParent(sc\MonitorOBJ, 0)
	Next
	
	For p.Props = Each Props
		If p\TexPath <> ""
			; ~ Such a stupid way, but it works
			Tex = LoadTexture_Strict(p\TexPath)
			EntityTexture(p\OBJ, Tex)
			;DeleteSingleTextureEntryFromCache(Tex) : Tex = 0 ; ~ Actually we don't need this
			p\TexPath = ""
		EndIf
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity(rt\OBJ) : rt\OBJ = 0
	Next
	
	Local ts.TempScreens, twp.TempWayPoints, tl.TempLights, tp.TempProps, tse.TempSoundEmitters
	
	For ts.TempScreens = Each TempScreens
		Delete(ts)
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete(twp)
	Next
	
	For tl.TempLights = Each TempLights
		Delete(tl)
	Next
	
	For tp.TempProps = Each TempProps
		Delete(tp)
	Next
	
	For tse.TempSoundEmitters = Each TempSoundEmitters
		Delete(tse)
	Next
	
	RenderLoading(85, GetLocalString("loading", "events"))
	
	For e.Events = Each Events
		; ~ Loading the necessary stuff for dimension_1499, but this will only be done if the player is in this dimension already
		If e\EventID = e_dimension_1499
			If e\EventState = 2.0
				e\room\Objects[0] = LoadMesh_Strict("GFX\Map\dimension1499\1499plane.b3d")
				HideEntity(e\room\Objects[0])
				e\room\ScriptedObject[0] = True
				
				I_1499\Sky = CreateSky("GFX\Map\sky\1499sky")
				
				For i = 1 To 15
					e\room\Objects[i] = LoadRMesh("GFX\Map\dimension1499\dimension_1499_object(" + i + ").rmesh", Null, False)
					ScaleEntity(e\room\Objects[i], RoomScale, RoomScale, RoomScale)
					HideEntity(e\room\Objects[i])
				Next
				
				CreateChunkParts(e\room)
				
				x = EntityX(e\room\OBJ)
				y = EntityY(e\room\OBJ)
				z = EntityZ(e\room\OBJ)
				
				Local ch.Chunk
				
				For i = -2 To 0 Step 2
					ch.Chunk = CreateChunk(-1, x * (i * 2.5), y, z, True)
					ch.Chunk = CreateChunk(-1, x * (i * 2.5), y, z - 40.0, True)
				Next
			EndIf
			Exit
		EndIf
	Next
	
	RenderLoading(90, GetLocalString("loading", "pos"))
	
	ResetEntity(me\Collider)
	
	MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
	
	SetFontEx(fo\FontID[Font_Default])
	
	HidePointer()
	
	me\DropSpeed = 0.0
	
	ResetRender()
	UpdateNPCs()
	UpdateWorld()
	
	DeleteTextureEntriesFromCache(DeleteMapTextures)
	
	RenderLoading(100)
	
	fps\Factor[0] = 0.0
	fps\PrevTime = MilliSecs()
	
	ResetInput()
	
	CatchErrors("Uncaught: InitLoadGame()")
End Function

Function InitOtherStuff%()
	Local sv.Save, cm.CustomMaps
	
	me\Playable = True : me\SelectedEnding = -1
	
	opt\MasterVolume = opt\PrevMasterVolume
	
	chs\NoClipSpeed = 2.0
	If opt\DebugMode Then InitCheats()
	
	as\Timer = 70.0 * 120.0
	If SelectedDifficulty\SaveType <> SAVE_ANYWHERE Then opt\AutoSaveEnabled = False
End Function

Function NullGame%(PlayButtonSFX% = True)
	CatchErrors("NullGame()")
	
	Local ach.AchievementMsg, c.ConsoleMsg, e.Events, itt.ItemTemplates, it.Items, de.Decals, p.Particles, d.Doors, lvr.Levers, sc.SecurityCams
	Local du.Dummy1499_1, n.NPCs, s.Screens, w.WayPoints, pr.Props, l.Lights, rt.RoomTemplates, r.Rooms, m.Materials, snd.Sound, fr.Forest, mt.MTGrid
	Local ch.Chunk, chp.ChunkPart, sv.Save, cm.CustomMaps, se.SoundEmitters, tmp.Template, emit.Emitter
	
	Local i%
	
	DeleteTextureEntriesFromCache(DeleteAllTextures)
	
	StopMouseMovement()
	KillSounds(False)
	If PlayButtonSFX Then PlaySound_Strict(ButtonSFX[0])
	
	RandomSeed = ""
	
	UsedConsole = False
	Delete(chs) : chs = Null
	WireFrameState = 0
	WireFrame(0)
	ConsoleOpen = False
	ConsoleInput = ""
	ConsoleScroll = 0.0 : ConsoleScrollDragging = 0
	ConsoleMouseMem = 0
	ConsoleR = 0 : ConsoleG = 0 : ConsoleB = 0
	ConsoleInBox = 0 : ConsoleInBar = 0
	For c.ConsoleMsg = Each ConsoleMsg
		Delete(c)
	Next
	
	SubjectName = ""
	InFacility = NullFloor
	PlayerFallingPickDistance = 0.0
	ToElevatorFloor = 0
	
	ShouldEntitiesFall = False
	HideDistance = 0.0
	CoffinDistance = 0.0
	
	SecondaryLightOn = 0.0
	IsBlackOut = False : PrevIsBlackOut = False
	UpdateLightsTimer = 0.0
	LightRenderDistance = 0.0
	
	RemoteDoorOn = False
	
	GameSaved = False
	CanSave = 0
	
	MTFTimer = 0.0
	MTFCameraCheckTimer = 0.0
	MTFCameraCheckDetected = False
	
	CODE_DR_MAYNARD = 0
	CODE_DR_GEARS = 0
	CODE_MAINTENANCE_TUNNELS = 0
	CODE_O5_COUNCIL = 0
	
	ShouldPlay = 66
	FreeEntity(SoundEmitter) : SoundEmitter = 0
	SoundTransmission = False
	
	TempLightVolume = 0.0
	LightVolume = 0.0
	CurrFogColorR = 0.0 : CurrFogColorG = 0.0 : CurrFogColorB = 0.0
	CurrFogColor = ""
	CurrAmbientColorR = 0.0 : CurrAmbientColorG = 0.0 : CurrAmbientColorB = 0.0
	CurrAmbientColor = ""
	
	GrabbedEntity = 0
	CameraPitch = 0.0
	
	HandEntity = 0
	For i = 0 To 3
		DrawArrowIcon[i] = False
	Next
	
	BatMsgTimer = 0.0
	
	EscapeSecondsTimer = 0.0
	EscapeTimer = 0.0
	
	If Camera <> 0 Then FreeEntity(Camera) : Camera = 0
	If Sky <> 0 Then FreeEntity(Sky) : Sky = 0
	
	CurrTrisAmount = 0
	
	CurrAchvMSGID = 0
	For ach.AchievementMsg = Each AchievementMsg
		Delete(ach)
	Next
	
	SubFile = 0
	LocalSubFile = 0
	SubColors = 0
	LocalSubColors = 0
	SubtitlesInit = False
	ClearSubtitles()
	DeInitSubtitlesAssets()
	Delete(msg) : msg = Null
	Delete(as) : as = Null
	
	FreeEntity(me\Collider) : me\Collider = 0
	FreeEntity(me\Head) : me\Head = 0
	Delete(me) : me = Null
	Delete(wi) : wi = Null
	
	Delete(I_005) : I_005 = Null
	Delete(I_008) : I_008 = Null
	Delete(I_035) : I_035 = Null
	Delete(I_268) : I_268 = Null
	DestroyS2IMap(I_294\DrinksMap) : I_294\DrinksMap = 0
	Delete(I_294) : I_294 = Null
	Delete(I_409) : I_409 = Null
	For i = 0 To 1
		I_427\Sound[i] = 0
	Next
	Delete(I_427) : I_427 = Null
	Delete(I_500) : I_500 = Null
	Delete(I_714) : I_714 = Null
	Delete(I_1025) : I_1025 = Null
	If I_1499\Sky <> 0 Then FreeEntity(I_1499\Sky) : I_1499\Sky = 0
	Delete(I_1499) : I_1499 = Null
	SCP1499Chunks = 0
	Delete(I_1048A) : I_1048A = Null
	Delete(I_966) : I_966 = Null
	
	QuickLoadPercent = 0
	QuickLoadPercent_DisplayTimer = 0.0
	For e.Events = Each Events
		RemoveEvent(e)
	Next
	
	IsUsingRadio = False
	InvOpen = False
	For i = 0 To 9
		If i < 9
			RadioState[i] = 0.0
			RadioState2[i] = 0
		EndIf
		RadioState3[i] = 0
	Next
	
	ItemAmount = 0 : MaxItemAmount = 0
	LastItemID = 0
	For it.Items = Each Items
		RemoveItem(it)
	Next
	Dim Inventory.Items(0)
	For itt.ItemTemplates = Each ItemTemplates
		RemoveItemTemplate(itt)
	Next
	
	For de.Decals = Each Decals
		RemoveDecal(de)
	Next
	RemoveDecalInstances()
	ParticleCam = 0
	FreeEntity(ParticlePiv) : ParticlePiv = 0
	DustParticleChance = 0
	For emit.Emitter = Each Emitter
		FreeEmitter(emit, True)
	Next
	For tmp.Template = Each Template
		FreeTemplate(Handle(tmp))
	Next
	Delete Each Template
	For p.Particles = Each Particles
		RemoveParticle(p)
	Next
	RemoveParticleInstances()
	Delete(bk) : bk = Null
	For d.Doors = Each Doors
		RemoveDoor(d)
	Next
	d_I\AnimButton = 0
	RemoveDoorInstances()
	For lvr.Levers = Each Levers
		RemoveLever(lvr)
	Next
	RemoveLeverInstances()
	For sc.SecurityCams = Each SecurityCams
		RemoveSecurityCam(sc)
	Next
	RemoveSecurityCamInstances()
	RemoveMonitorInstances()
	For s.Screens = Each Screens
		RemoveScreen(s)
	Next
	For w.WayPoints = Each WayPoints
		RemoveWaypoint(w)
	Next
	For pr.Props = Each Props
		RemoveProp(pr)
	Next
	For l.Lights = Each Lights
		RemoveLight(l)
	Next
	For se.SoundEmitters = Each SoundEmitters
		RemoveSoundEmitter(se)
	Next
	For fr.Forest = Each Forest
		If fr <> Null Then DestroyForest(fr)
		Delete(fr)
	Next
	For mt.MTGrid = Each MTGrid
		If mt <> Null Then DestroyMT(mt, False)
		Delete(mt)
	Next
	For i = 0 To 4095
		CHUNKDATA[i] = 0
	Next
	For ch.Chunk = Each Chunk
		RemoveChunk(ch)
	Next
	For chp.ChunkPart = Each ChunkPart
		RemoveChunkPart(chp)
	Next
	Dim MapRoom$(0, 0)
	Dim RoomAmount%(0, 0)
	Delete(CurrMapGrid) : CurrMapGrid = Null
	Delete(I_Zone) : I_Zone = Null
	RoomTempID = 0
	For r.Rooms = Each Rooms
		RemoveRoom(r)
	Next
	For rt.RoomTemplates = Each RoomTemplates
		RemoveRoomTemplate(rt)
	Next
	
	RemoveHazmatTimer = 0.0
	Remove714Timer = 0.0
	ForestNPC = 0
	ForestNPCTex = 0
	For i = 0 To 2
		ForestNPCData[i] = 0.0
	Next
	For du.Dummy1499_1 = Each Dummy1499_1
		RemoveDummy1499_1(du)
	Next
	For n.NPCs = Each NPCs
		RemoveNPC(n)
	Next
	RemoveNPCInstances()
	
	RemoveMiscInstances()
	
	For m.Materials = Each Materials
		Delete(m)
	Next
	RemoveTextureInstances()
	Delete Each TextureInCache
	AmbientLightRoomTex = 0
	FreeTexture(MissingTexture) : MissingTexture = 0
	
	Mesh_MinX = 0.0 : Mesh_MinY = 0.0 : Mesh_MinZ = 0.0
	Mesh_MaxX = 0.0 : Mesh_MaxY = 0.0 : Mesh_MaxZ = 0.0
	Mesh_MagX = 0.0 : Mesh_MagY = 0.0 : Mesh_MagZ = 0.0
	
	For i = 0 To 24
		CommotionState[i] = False
	Next
	CurrAmbientSFX = 0
	TempSoundIndex = 0
	For snd.Sound = Each Sound
		If snd\InternalHandle <> 0 Then FreeSound(snd\InternalHandle) : snd\InternalHandle = 0
		Delete(snd)
	Next
	RemoveSoundInstances()
	
	For sv.Save = Each Save
		Delete(sv)
	Next
	For cm.CustomMaps = Each CustomMaps
		Delete(cm)
	Next
	
	FreeBlur()
	If FresizeTexture <> 0 Then FreeTexture(FresizeTexture) : FresizeTexture = 0
	If FresizeTexture2 <> 0 Then FreeTexture(FresizeTexture2) : FresizeTexture2 = 0
	If FresizeImage <> 0 Then FreeEntity(FresizeImage) : FresizeImage = 0
	If FresizeCam <> 0 Then FreeEntity(FresizeCam) : FresizeCam = 0
	
	RenderTween = 0.0
	
	ClearCollisions()
	ClearWorld()
	ResetTimingAccumulator()
	InitFastResize()
	
	; ~ Load main menu assets and open main menu
	SelectedInputBox = 0
	ShouldDeleteGadgets = True
	DeleteMenuGadgets()
	InitMainMenuAssets()
	MenuOpen = False
	Delete(igm) : igm = Null
	MainMenuOpen = True
	mm\MainMenuTab = MainMenuTab_Default
	
	CatchErrors("Uncaught: NullGame()")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS