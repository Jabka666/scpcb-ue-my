Const MaxDecalTextureIDAmount% = 24

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
Const DECAL_999% = 20

Const DECAL_WATER% = 21

Const DECAL_CRACKED_GLASS% = 22

Const DECAL_FOAM% = 23
;[End Block]

Function LoadDecals%()
	Local i%
	
	de_I.DecalInstance = New DecalInstance
	
	For i = DECAL_CORROSIVE_1 To DECAL_CORROSIVE_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\corrosive_decal(" + i + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	For i = DECAL_BLOOD_1 To DECAL_BLOOD_6
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\blood_decal(" + (i - DECAL_BLOOD_1) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	For i = DECAL_PD_1 To DECAL_PD_6
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\pd_decal(" + (i - DECAL_PD_1) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	For i = DECAL_BULLET_HOLE_1 To DECAL_BULLET_HOLE_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\bullet_hole_decal(" + (i - DECAL_BULLET_HOLE_1) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	For i = DECAL_BLOOD_DROP_1 To DECAL_BLOOD_DROP_2
		de_I\DecalTextureID[i] = LoadTexture_Strict("GFX\Decals\blood_drop_decal(" + (i - DECAL_BLOOD_DROP_1) + ").png", 1 + 2, DeleteAllTextures)
	Next
	
	de_I\DecalTextureID[DECAL_409] = LoadTexture_Strict("GFX\Decals\scp_409_decal.png", 1 + 2, DeleteAllTextures)
	
	de_I\DecalTextureID[DECAL_427] = LoadTexture_Strict("GFX\Decals\scp_427_decal.png", 1 + 2, DeleteAllTextures)
	
	de_I\DecalTextureID[DECAL_999] = LoadTexture_Strict("GFX\Decals\scp_999_decal.png", 1 + 2, DeleteAllTextures)
	
	de_I\DecalTextureID[DECAL_WATER] = LoadTexture_Strict("GFX\Decals\water_decal.png", 1 + 2, DeleteAllTextures)
	
	de_I\DecalTextureID[DECAL_CRACKED_GLASS] = LoadTexture_Strict("GFX\Decals\cracked_glass_decal.png", 1 + 2, DeleteAllTextures)
	
	de_I\DecalTextureID[DECAL_FOAM] = LoadTexture_Strict("GFX\Decals\foam_decal.png", 1 + 2, DeleteAllTextures)
End Function

Function RemoveDecalInstances%()
	Local i%
	
	For i = 0 To MaxDecalTextureIDAmount - 1
		de_I\DecalTextureID[i] = 0
	Next
	Delete Each DecalBase
	Delete(de_I) : de_I = Null
End Function

Const MaxParticleTextureIDAmount% = 16

Type ParticleInstance
	Field ParticleTextureID%[MaxParticleTextureIDAmount]
End Type

Global p_I.ParticleInstance

; ~ Particles ID Constants
;[Block]
Const PARTICLE_BLACK_SMOKE% = 0
Const PARTICLE_WHITE_SMOKE% = 1
Const PARTICLE_GREY_SMOKE% = 2

Const PARTICLE_FLASH% = 3

Const PARTICLE_DUST% = 4

Const PARTICLE_SHADOW% = 5

Const PARTICLE_SUN% = 6

Const PARTICLE_BLOOD% = 7

Const PARTICLE_SPARK% = 8

Const PARTICLE_WATER_DROP% = 9
Const PARTICLE_WATER_RING% = 10

Const PARTICLE_LEAF% = 11

Const PARTICLE_CONCRETE% = 12

Const PARTICLE_FLY% = 13

Const PARTICLE_FIRE% = 14

Const PARTICLE_SNOW_SHINE% = 15
;[End Block]

Const MaxParticleEffects% = 41

Global ParticleEffect%[MaxParticleEffects]

Function LoadParticles%()
	p_I.ParticleInstance = New ParticleInstance
	
	p_I\ParticleTextureID[PARTICLE_BLACK_SMOKE] = LoadTexture_Strict("GFX\Particles\smoke(0).png", 1 + 2, DeleteAllTextures)
	p_I\ParticleTextureID[PARTICLE_WHITE_SMOKE] = LoadTexture_Strict("GFX\Particles\smoke(1).png", 1 + 2, DeleteAllTextures)
	p_I\ParticleTextureID[PARTICLE_GREY_SMOKE] = LoadTexture_Strict("GFX\Particles\smoke(2).png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_FLASH] = LoadTexture_Strict("GFX\Particles\flash.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_DUST] = LoadTexture_Strict("GFX\Particles\dust.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_SHADOW] = LoadTexture_Strict("GFX\NPCs\hg.pt", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_SUN] = LoadTexture_Strict("GFX\Map\Textures\sun.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_BLOOD] = LoadTexture_Strict("GFX\Particles\blood.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_SPARK] = LoadTexture_Strict("GFX\Particles\spark.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_WATER_DROP] = LoadTexture_Strict("GFX\Particles\water_drop.png", 1 + 2, DeleteAllTextures)
	p_I\ParticleTextureID[PARTICLE_WATER_RING] = LoadTexture_Strict("GFX\Particles\water_ring.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_LEAF] = LoadTexture_Strict("GFX\Particles\leaf.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_CONCRETE] = LoadTexture_Strict("GFX\Particles\concrete.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_FLY] = LoadTexture_Strict("GFX\Particles\fly.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_FIRE] = LoadTexture_Strict("GFX\Particles\fire.png", 1 + 2, DeleteAllTextures)
	
	p_I\ParticleTextureID[PARTICLE_SNOW_SHINE] = LoadTexture_Strict("GFX\Particles\snow_shine.png", 1 + 2, DeleteAllTextures)
	
	; ~ Black smoke in "room2c_gw_lcz"/"room2_6_hcz"/"cont1_035"
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[0], -1)
	SetTemplateParticleLifeTime(ParticleEffect[0], 45, 50)
	SetTemplateTexture(ParticleEffect[0], PARTICLE_BLACK_SMOKE)
	SetTemplateOffset(ParticleEffect[0], 0.0, 0.0, 0.05, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[0], -0.025, 0.025, -0.05, -0.04, -0.025, 0.025)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	SetTemplateSize(ParticleEffect[0], 0.02, 0.02, 1.5, 1.8)
	SetTemplateSizeVel(ParticleEffect[0], 0.025, 1.013)
	SetTemplateGravity(ParticleEffect[0], -0.001)
	
	; ~ White smoke in "room2_gw_2"
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], -1)
	SetTemplateParticleLifeTime(ParticleEffect[1], 4, 7)
	SetTemplateTexture(ParticleEffect[1], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[1], -0.025, 0.025, 0.0, 0.0, -0.025, 0.025)
	SetTemplateVelocity(ParticleEffect[1], -0.015, 0.015, 0.01, 0.03, -0.015, 0.015)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	SetTemplateSize(ParticleEffect[1], 0.1, 0.1, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], 0.008, 1.01)
	
	; ~ White smoke in "room2_gw/room3_gw/room4_gw"
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 70.0 * 4.0)
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
	SetTemplateParticleLifeTime(ParticleEffect[7], 15, 20)
	SetTemplateTexture(ParticleEffect[7], PARTICLE_WHITE_SMOKE)
	SetTemplateOffset(ParticleEffect[7], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[7], 0.0, 0.0, 0.008, 0.008, -0.03, -0.02)
	SetTemplateAlphaVel(ParticleEffect[7], True)
	SetTemplateSize(ParticleEffect[7], 0.35, 0.35, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[7], 0.018, 1.01)
	SetTemplateGravity(ParticleEffect[7], -0.0005)
	
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
	SetTemplateSize(ParticleEffect[11], 0.005, 0.005, 1.0, 1.2)
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
	SetTemplateSize(ParticleEffect[12], 0.0016, 0.0016, 1.0, 1.25)
	SetTemplateAlphaVel(ParticleEffect[12], True)
	
	; ~ A simple flash particle
	ParticleEffect[13] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[13], 1)
	SetTemplateParticleLifeTime(ParticleEffect[13], 2, 2)
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
	
	; ~ Sparks from light emitter
	ParticleEffect[20] = CreateTemplate()
	SetTemplateFX(ParticleEffect[20], 64)
	SetTemplateParticlesPerInterval(ParticleEffect[20], 20)
	SetTemplateEmitterLifeTime(ParticleEffect[20], 2)
	SetTemplateParticleLifeTime(ParticleEffect[20], 60, 70)
	SetTemplateTexture(ParticleEffect[20], PARTICLE_SPARK)
	SetTemplateOffset(ParticleEffect[20], -0.05, 0.05, 0.0, 0.0, -0.05, 0.05)
	SetTemplateVelocity(ParticleEffect[20], -0.0175, 0.0175, -0.015, 0.015, -0.0175, 0.0175)
	SetTemplateAlignToFall(ParticleEffect[20], True, 45)
	SetTemplateGravity(ParticleEffect[20], 0.001)
	SetTemplateSize(ParticleEffect[20], 0.006, 0.01)
	
	; ~ Blood sprite
	ParticleEffect[21] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[21], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[21], 1)
	SetTemplateParticleLifeTime(ParticleEffect[21], 15, 20)
	SetTemplateTexture(ParticleEffect[21], PARTICLE_BLOOD)
	SetTemplateOffset(ParticleEffect[21], -0.2, 0.2, 0.1, 0.1, -0.2, 0.2)
	SetTemplateSize(ParticleEffect[21], 0.2, 0.2, 1.0, 1.2)
	SetTemplateAlphaVel(ParticleEffect[21], True)
	
	; ~ Water drop particle in "room2_js"
	ParticleEffect[22] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[22], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[22], -1)
	SetTemplateInterval(ParticleEffect[22], 15)
	SetTemplateParticleLifeTime(ParticleEffect[22], 140, 140)
	SetTemplateTexture(ParticleEffect[22], PARTICLE_WATER_DROP)
	SetTemplateOffset(ParticleEffect[22], -0.3, 0.3, 0.0, 0.0, -0.3, 0.3)
	SetTemplateVelocity(ParticleEffect[22], 0.0, 0.0, -0.042, -0.04, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[22], 0.016, 0.016, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[22], True)
	SetTemplateFloor(ParticleEffect[22], 0.0, 0.0, 0)
	
	; ~ Water rings in "room2_js"
	ParticleEffect[23] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[23], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[23], -1)
	SetTemplateInterval(ParticleEffect[23], 15)
	SetTemplateParticleLifeTime(ParticleEffect[23], 30, 40)
	SetTemplateTexture(ParticleEffect[23], PARTICLE_WATER_RING)
	SetTemplateOffset(ParticleEffect[23], -0.3, 0.3, 0.0, 0.0, -0.3, 0.3)
	SetTemplateSize(ParticleEffect[23], 0.005, 0.005, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[23], 0.001, 1.001)
	SetTemplateFixAngles(ParticleEffect[23], 90, 0)
	SetTemplateAlphaVel(ParticleEffect[23], True)
	
	; ~ Leafs in the forest
	ParticleEffect[24] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[24], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[24], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[24], 5)
	SetTemplateParticleLifeTime(ParticleEffect[24], 360, 380)
	SetTemplateTexture(ParticleEffect[24], PARTICLE_LEAF)
	SetTemplateOffset(ParticleEffect[24], -4.0, 4.0, 2.5, 2.5, -4.0, 4.0)
	SetTemplateVelocity(ParticleEffect[24], 0.03, 0.01, -0.012, -0.01, 0.001, 0.001)
	SetTemplateSize(ParticleEffect[24], 0.02, 0.02, 0.9, 1.1)
	
	; ~ White smoke in "cont2_1123"
	ParticleEffect[25] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[25], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[25], -1)
	SetTemplateParticleLifeTime(ParticleEffect[25], 15, 20)
	SetTemplateTexture(ParticleEffect[25], PARTICLE_WHITE_SMOKE)
	SetTemplateVelocity(ParticleEffect[25], 0.0, 0.0, -0.008, -0.008, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[25], True)
	SetTemplateSize(ParticleEffect[25], 0.35, 0.35, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[25], 0.018, 1.01)
	
	; ~ Concrete pieces
	ParticleEffect[26] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[26], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[26], 1)
	SetTemplateParticleLifeTime(ParticleEffect[26], 140, 140)
	SetTemplateTexture(ParticleEffect[26], PARTICLE_CONCRETE)
	SetTemplateOffset(ParticleEffect[26], -4.0, 4.0, 0.0, 0.0, -4.0, 4.0)
	SetTemplateVelocity(ParticleEffect[26], 0.0, 0.0, -0.042, -0.04, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[26], 0.02, 0.02, 0.8, 1.3)
	SetTemplateAlphaVel(ParticleEffect[26], True)
	SetTemplateFloor(ParticleEffect[26], 0.0, 0.0, 0)
	
	; ~ Blood drip particle in "room2_storage"
	ParticleEffect[27] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[27], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[27], -1)
	SetTemplateInterval(ParticleEffect[27], 13)
	SetTemplateParticleLifeTime(ParticleEffect[27], 120, 120)
	SetTemplateTexture(ParticleEffect[27], PARTICLE_WATER_DROP)
	SetTemplateOffset(ParticleEffect[27], -0.15, 0.15, 0.0, 0.0, -0.15, 0.15)
	SetTemplateVelocity(ParticleEffect[27], 0.0, 0.0, -0.042, -0.04, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[27], 0.016, 0.016, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[27], True)
	SetTemplateFloor(ParticleEffect[27], 0.0, 0.0, 0)
	SetTemplateColors(ParticleEffect[27], $800000, $800000)
	
	; ~ Breath steam
	ParticleEffect[28] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[28], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[28], 1)
	SetTemplateParticleLifeTime(ParticleEffect[28], 20, 30)
	SetTemplateTexture(ParticleEffect[28], PARTICLE_WHITE_SMOKE)
	SetTemplateVelocity(ParticleEffect[28], 0.0, 0.0, 0.0015, 0.002, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[28], True)
	SetTemplateSize(ParticleEffect[28], 0.14, 0.14, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[28], 0.011, 1.01)
	
	; ~ Fine SCP-513 ring
	ParticleEffect[29] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[29], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[29], 1)
	SetTemplateParticleLifeTime(ParticleEffect[29], 60, 70)
	SetTemplateTexture(ParticleEffect[29], PARTICLE_WATER_RING)
	SetTemplateOffset(ParticleEffect[29], 0.0, 0.0, 0.3, 0.3, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[29], 0.05, 0.05, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[29], 0.001, 1.7)
	SetTemplateFixAngles(ParticleEffect[29], 90, 0)
	SetTemplateAlphaVel(ParticleEffect[29], True)
	
	; ~ Flies
	ParticleEffect[30] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[30], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[30], -1)
	SetTemplateParticleLifeTime(ParticleEffect[30], 30, 40)
	SetTemplateTexture(ParticleEffect[30], PARTICLE_FLY)
	SetTemplateOffset(ParticleEffect[30], -0.1, 0.1, 0.0, 0.3, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[30], -0.01, 0.01, -0.01, 0.01, -0.01, 0.01)
	SetTemplateSize(ParticleEffect[30], 0.008, 0.008, 0.9, 1.1)
	
	; ~ Fire in "room2_tesla_2_hcz"
	ParticleEffect[31] = CreateTemplate()
	SetTemplateFX(ParticleEffect[31], 1 + 2 + 8 + 32)
	SetTemplateEmitterLifeTime(ParticleEffect[31], -1)
	SetTemplateInterval(ParticleEffect[31], 4)
	SetTemplateParticlesPerInterval(ParticleEffect[31], 3)
	SetTemplateParticleLifeTime(ParticleEffect[31], 40, 45)
	SetTemplateTexture(ParticleEffect[31], PARTICLE_FIRE)
	SetTemplateOffset(ParticleEffect[31], -0.005, 0.01, -0.005, 0.01, -0.005, 0.01)
	SetTemplateVelocity(ParticleEffect[31], -0.007, 0.007, -0.005, 0.02, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[31], True)
	SetTemplateSize(ParticleEffect[31], 0.04, 0.04, 0.5, 1.0)
	SetTemplateSizeVel(ParticleEffect[31], 0.01, 1.01)
	
	; ~ SCP-409/Snow shining
	ParticleEffect[32] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[32], 1)
	SetTemplateParticleLifeTime(ParticleEffect[32], 6, 10)
	SetTemplateTexture(ParticleEffect[32], PARTICLE_SNOW_SHINE)
	SetTemplateAlphaVel(ParticleEffect[32], True)
	SetTemplateSize(ParticleEffect[32], 0.05, 0.05, 0.5, 1.0)
	
	; ~ Explosion
	ParticleEffect[33] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[33], 1)
	SetTemplateParticleLifeTime(ParticleEffect[33], 1200, 1200)
	SetTemplateTexture(ParticleEffect[33], PARTICLE_WHITE_SMOKE)
	SetTemplateAlphaVel(ParticleEffect[33], True)
	SetTemplateSize(ParticleEffect[33], 1.0, 1.0, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[33], 0.01, 1.08)
	
	; ~ Light wave
	ParticleEffect[34] = CreateTemplate()
	SetTemplateEmitterLifeTime(ParticleEffect[34], 1)
	SetTemplateParticleLifeTime(ParticleEffect[34], 60, 60)
	SetTemplateTexture(ParticleEffect[34], PARTICLE_WHITE_SMOKE)
	SetTemplateAlphaVel(ParticleEffect[34], True)
	SetTemplateSize(ParticleEffect[34], 1.0, 1.0, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[34], 0.01, 2.5)
	
	; ~ Dust particles from vent
	ParticleEffect[35] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[35], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[35], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[35], 10)
	SetTemplateParticleLifeTime(ParticleEffect[35], 70, 80)
	SetTemplateTexture(ParticleEffect[35], PARTICLE_DUST)
	SetTemplateOffset(ParticleEffect[35], -0.2, 0.2, -0.05, 0.05, -0.2, 0.2)
	SetTemplateVelocity(ParticleEffect[35], -0.004, 0.004, -0.0001, 0.0001, -0.004, 0.004)
	SetTemplateSize(ParticleEffect[35], 0.005, 0.005, 0.9, 1.1)
	SetTemplateAlphaVel(ParticleEffect[35], True)
	
	; ~ SCP-457 flame particle
	ParticleEffect[36] = CreateTemplate()
	SetTemplateFX(ParticleEffect[36], 1 + 2 + 8 + 32)
	SetTemplateEmitterLifeTime(ParticleEffect[36], -1)
	SetTemplateParticlesPerInterval(ParticleEffect[36], 6)
	SetTemplateParticleLifeTime(ParticleEffect[36], 25, 30)
	SetTemplateTexture(ParticleEffect[36], PARTICLE_FIRE)
	SetTemplateOffset(ParticleEffect[36], -0.003, 0.003, -0.03, 0.03, -0.003, 0.003)
	SetTemplateVelocity(ParticleEffect[36], -0.004, 0.004, -0.0015, 0.015, -0.004, 0.004)
	SetTemplateAlphaVel(ParticleEffect[36], True)
	SetTemplateSize(ParticleEffect[36], 0.03, 0.03, 0.9, 1.1)
	SetTemplateSizeVel(ParticleEffect[36], 0.0013, 1.0013)
	
	; ~ Grey smoke particles
	ParticleEffect[37] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[37], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[37], -1)
	SetTemplateInterval(ParticleEffect[37], 25)
	SetTemplateParticleLifeTime(ParticleEffect[37], 40, 50)
	SetTemplateTexture(ParticleEffect[37], PARTICLE_GREY_SMOKE)
	SetTemplateOffset(ParticleEffect[37], 0.0, 0.0, -0.1, 0.1, 0.0, 0.0)
	SetTemplateVelocity(ParticleEffect[37], -0.006, 0.006, 0.01, 0.012, -0.006, 0.006)
	SetTemplateAlphaVel(ParticleEffect[37], True)
	SetTemplateSize(ParticleEffect[37], 0.01, 0.01, 1.0, 1.2)
	SetTemplateSizeVel(ParticleEffect[37], 0.008, 1.01)
	SetTemplateFloor(ParticleEffect[37], 1.3, 0.12, True)
	SetTemplateColors(ParticleEffect[37], $808080, $808080)
	
	; ~ Fire in "room2_mt"
	ParticleEffect[38] = CreateTemplate()
	SetTemplateFX(ParticleEffect[38], 1 + 2 + 8 + 32)
	SetTemplateEmitterLifeTime(ParticleEffect[38], -1)
	SetTemplateInterval(ParticleEffect[38], 4)
	SetTemplateParticlesPerInterval(ParticleEffect[38], 2)
	SetTemplateParticleLifeTime(ParticleEffect[38], 60, 70)
	SetTemplateTexture(ParticleEffect[38], PARTICLE_FIRE)
	SetTemplateOffset(ParticleEffect[38], -0.04, 0.04, -0.04, 0.04, -0.04, 0.04)
	SetTemplateVelocity(ParticleEffect[38], -0.007, 0.007, -0.005, 0.02, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[38], True)
	SetTemplateSize(ParticleEffect[38], 0.08, 0.08, 0.5, 1.0)
	SetTemplateSizeVel(ParticleEffect[38], 0.01, 1.01)
	
	; ~ A single water drop particle in "room2_mt"
	ParticleEffect[39] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[39], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[39], -1)
	SetTemplateInterval(ParticleEffect[39], 45)
	SetTemplateParticleLifeTime(ParticleEffect[39], 140, 140)
	SetTemplateTexture(ParticleEffect[39], PARTICLE_WATER_DROP)
	SetTemplateOffset(ParticleEffect[39], -0.01, 0.01, 0.0, 0.0, -0.01, 0.01)
	SetTemplateVelocity(ParticleEffect[39], 0.0, 0.0, -0.042, -0.04, 0.0, 0.0)
	SetTemplateSize(ParticleEffect[39], 0.016, 0.016, 1.0, 1.5)
	SetTemplateAlphaVel(ParticleEffect[39], True)
	
	; ~ A single water ring in "room2_mt"
	ParticleEffect[40] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[40], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[40], -1)
	SetTemplateInterval(ParticleEffect[40], 60) ; ~ TODO: Sync interval with water drop
	SetTemplateParticleLifeTime(ParticleEffect[40], 30, 40)
	SetTemplateTexture(ParticleEffect[40], PARTICLE_WATER_RING)
	SetTemplateOffset(ParticleEffect[40], -0.01, 0.01, 0.0, 0.0, -0.01, 0.01)
	SetTemplateSize(ParticleEffect[40], 0.005, 0.005, 1.0, 1.0)
	SetTemplateSizeVel(ParticleEffect[40], 0.001, 1.002)
	SetTemplateFixAngles(ParticleEffect[40], 90, 0)
	SetTemplateAlphaVel(ParticleEffect[40], True)
End Function

Function RemoveParticleInstances%()
	Local i%
	
	For i = 0 To MaxParticleTextureIDAmount - 1
		p_I\ParticleTextureID[i] = 0
	Next
	Delete(p_I) : p_I = Null
End Function

Const MaxDoorModelIDAmount% = 10
Const MaxDoorFrameModelIDAmount% = 4
Const MaxButtonModelIDAmount% = 7
Const MaxButtonTextureIDAmount% = 4
Const MaxElevatorPanelTextureIDAmount% = 3

Type DoorInstance
	Field DoorModelID%[MaxDoorModelIDAmount]
	Field DoorFrameModelID%[MaxDoorFrameModelIDAmount]
	Field ButtonModelID%[MaxButtonModelIDAmount]
	Field ButtonTextureID%[MaxButtonTextureIDAmount]
	Field ElevatorPanelModel%
	Field ElevatorPanelTextureID%[MaxElevatorPanelTextureIDAmount%]
	Field SelectedDoor.Doors, ClosestDoor.Doors, AnimDoor.Doors
	Field ClosestButton%, AnimButton%
	Field DoorColl%, BigDoorColl%
	Field DoorGroup%[MaxDoorModelIDAmount]
	Field ButtonGroup%[MaxDoorModelIDAmount]
	Field FrameGroup%[MaxDoorFrameModelIDAmount]
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
Const DOOR_FENCE_MODEL% = 8
Const DOOR_ONE_SIDED_MODEL% = 9
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
Const BUTTON_DEFAULT_MODEL_SEPARATED% = 5
Const BUTTON_ELEVATOR_MODEL_SEPARATED% = 6
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
	
	Local DECAY_TEX%[2]
	
	DECAY_TEX[0] = LoadTexture_Strict("GFX\Map\Textures\Door01_Corrosive.png")
	DECAY_TEX[1] = LoadTexture_Strict("GFX\Map\Textures\containment_doors_Corrosive.png")
	
	For i = 0 To MaxDoorModelIDAmount - 1
		d_I\DoorGroup[i] = CreatePivot()
	Next
	
	For i = 0 To MaxButtonModelIDAmount - 1
		d_I\ButtonGroup[i] = CreatePivot()
	Next
	
	For i = 0 To MaxDoorFrameModelIDAmount - 1
		d_I\FrameGroup[i] = CreatePivot()
	Next
	
	d_I\DoorModelID[DOOR_DEFAULT_MODEL] = LoadMesh_Strict("GFX\Map\Props\Door01.b3d")
	d_I\DoorModelID[DOOR_ELEVATOR_MODEL] = LoadMesh_Strict("GFX\Map\Props\ElevatorDoor.b3d")
	d_I\DoorModelID[DOOR_HEAVY_MODEL_1] = LoadMesh_Strict("GFX\Map\Props\HeavyDoor1.b3d")
	d_I\DoorModelID[DOOR_HEAVY_MODEL_2] = LoadMesh_Strict("GFX\Map\Props\HeavyDoor2.b3d")
	d_I\DoorModelID[DOOR_BIG_MODEL_1] = LoadMesh_Strict("GFX\Map\Props\contdoorleft.b3d")
	d_I\DoorModelID[DOOR_BIG_MODEL_2] = LoadMesh_Strict("GFX\Map\Props\contdoorright.b3d")
	d_I\DoorModelID[DOOR_OFFICE_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\officedoor.b3d")
	d_I\DoorModelID[DOOR_WOODEN_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorWooden.b3d")
	d_I\DoorModelID[DOOR_FENCE_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\FenceDoor.b3d")
	d_I\DoorModelID[DOOR_ONE_SIDED_MODEL] = LoadMesh_Strict("GFX\Map\Props\Door02.b3d")
	
	For i = 0 To MaxDoorModelIDAmount - 1
		CreateInstanceHider(d_I\DoorModelID[i])
		SetDeferredEntity(d_I\DoorModelID[i], True, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
		ShowEntity(d_I\DoorModelID[i])
	Next
	
	d_I\DoorFrameModelID[DOOR_DEFAULT_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorFrame.b3d")
	d_I\DoorFrameModelID[DOOR_BIG_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\ContDoorFrame.b3d")
	d_I\DoorFrameModelID[DOOR_OFFICE_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\officedoorframe.b3d")
	d_I\DoorFrameModelID[DOOR_WOODEN_FRAME_MODEL] = LoadMesh_Strict("GFX\Map\Props\DoorWoodenFrame.b3d")
	
	For i = 0 To MaxDoorFrameModelIDAmount - 1
		CreateInstanceHider(d_I\DoorFrameModelID[i])
		SetDeferredEntity(d_I\DoorFrameModelID[i], True, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
		ShowEntity(d_I\DoorFrameModelID[i])
	Next
	
	d_I\DoorColl = LoadMesh_Strict("GFX\Map\Props\DoorColl.b3d")
	HideEntity(d_I\DoorColl)
	
	d_I\BigDoorColl = LoadMesh_Strict("GFX\Map\Props\BigDoorColl.b3d")
	HideEntity(d_I\BigDoorColl)
	
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_DOWN] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_down.png", 1, DeleteAllTextures)
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_UP] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_up.png", 1, DeleteAllTextures)
	d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_IDLE] = LoadTexture_Strict("GFX\Map\Textures\elevator_panel_idle.png", 1, DeleteAllTextures)
	
	d_I\ElevatorPanelModel = LoadMesh_Strict("GFX\Map\Props\elevator_panel.b3d")
	HideEntity(d_I\ElevatorPanelModel)
	
	d_I\ButtonTextureID[BUTTON_GREEN_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad.jpg", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_using.png", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_RED_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_locked.png", 1, DeleteAllTextures)
	d_I\ButtonTextureID[BUTTON_106_TEXTURE] = LoadTexture_Strict("GFX\Map\Textures\keypad_106.png", 1, DeleteAllTextures)
	
	d_I\ButtonModelID[BUTTON_DEFAULT_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\Button.b3d")
	
	Local BUTTON%
	
	d_I\ButtonModelID[BUTTON_DEFAULT_MODEL_SEPARATED] = LoadMesh_Strict("GFX\Map\Props\Button_base.b3d")
	BUTTON = LoadMesh_Strict("GFX\Map\Props\Button_button.b3d", d_I\ButtonModelID[BUTTON_DEFAULT_MODEL_SEPARATED])
	NameEntity(BUTTON, "Button0")
	SetDeferredEntity(BUTTON, False, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
	
	d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL_SEPARATED] = LoadMesh_Strict("GFX\Map\Props\ButtonElevator_Base.b3d")
	BUTTON = LoadMesh_Strict("GFX\Map\Props\ButtonElevator_Up.b3d", d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL_SEPARATED])
	NameEntity(BUTTON, "Button0")
	SetDeferredEntity(BUTTON, False, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
	
	BUTTON = LoadMesh_Strict("GFX\Map\Props\ButtonElevator_Down.b3d", d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL_SEPARATED])
	NameEntity(BUTTON, "Button1")
	SetDeferredEntity(BUTTON, False, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
	
	d_I\ButtonModelID[BUTTON_KEYCARD_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonKeycard.b3d")
	
	d_I\ButtonModelID[BUTTON_KEYPAD_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonCode.b3d")
	
	d_I\ButtonModelID[BUTTON_SCANNER_MODEL] = LoadMesh_Strict("GFX\Map\Props\ButtonScanner.b3d")
	
	d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\ButtonElevator.b3d")
	
	For i = 0 To MaxButtonModelIDAmount - 1
		CreateInstanceHider(d_I\ButtonModelID[i])
		SetDeferredEntity(d_I\ButtonModelID[i], False, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
		ShowEntity(d_I\ButtonModelID[i])
	Next
	
	; ================================= Groups
	
	; ~ Doors
	For i = 0 To MaxDoorModelIDAmount - 1
		CopyEntity(d_I\DoorModelID[i], d_I\DoorGroup[i])
		CopyEntity(d_I\DoorModelID[i], d_I\DoorGroup[i])
	Next
	; ~ Frames
	For i = 0 To MaxDoorFrameModelIDAmount - 1
		CopyEntity(d_I\DoorFrameModelID[i], d_I\FrameGroup[i])
		CopyEntity(d_I\DoorFrameModelID[i], d_I\FrameGroup[i])
	Next
	
	; ~ Buttons
	For i = 0 To 3
		CopyEntity(d_I\ButtonModelID[BUTTON_DEFAULT_MODEL_SEPARATED], d_I\ButtonGroup[BUTTON_DEFAULT_MODEL])
		CopyEntity(d_I\ButtonModelID[BUTTON_ELEVATOR_MODEL_SEPARATED], d_I\ButtonGroup[BUTTON_ELEVATOR_MODEL])
		CopyEntity(d_I\ButtonModelID[BUTTON_KEYCARD_MODEL], d_I\ButtonGroup[BUTTON_KEYCARD_MODEL])
		CopyEntity(d_I\ButtonModelID[BUTTON_SCANNER_MODEL], d_I\ButtonGroup[BUTTON_SCANNER_MODEL])
		CopyEntity(d_I\ButtonModelID[BUTTON_KEYPAD_MODEL], d_I\ButtonGroup[BUTTON_KEYPAD_MODEL])
	Next
	
	; ~ Set textures for group children
	Local g%, c%, Child%, ChildChild%
	
	For g = 0 To MaxDoorModelIDAmount - 1
		For i = 1 To CountChildren(d_I\DoorGroup[g])
			Child = GetChild(d_I\DoorGroup[g], i)
			EntityTexture(Child, DECAY_TEX[i - 1])
			UpdateEntityMaterial(Child, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
			CreateInstanceHider(Child)
		Next
	Next
	
	For g = 0 To MaxDoorFrameModelIDAmount - 1
		For i = 1 To CountChildren(d_I\FrameGroup[g])
			Child = GetChild(d_I\FrameGroup[g], i)
			EntityTexture(Child, DECAY_TEX[i - 1])
			UpdateEntityMaterial(Child, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
			CreateInstanceHider(Child)
		Next
	Next
	
	For g = 0 To MaxButtonModelIDAmount - 1
		For i = 1 To CountChildren(d_I\ButtonGroup[g])
			Child = GetChild(d_I\ButtonGroup[g], i)
			EntityTexture(Child, d_I\ButtonTextureID[i - 1])
			UpdateEntityMaterial(Child, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
			CreateInstanceHider(Child)
			For c = 1 To CountChildren(Child)
				ChildChild = GetChild(Child, c)
				EntityTexture(ChildChild, d_I\ButtonTextureID[i - 1])
				UpdateEntityMaterial(ChildChild, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
				CreateInstanceHider(ChildChild)
			Next
		Next
	Next
End Function

Function RemoveDoorInstances%()
	Local i%
	
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
	
	For i = CAM_HEAD_DEFAULT_TEXTURE To CAM_HEAD_RED_LIGHT_TEXTURE
		sc_I\CamTextureID[i] = LoadTexture_Strict("GFX\Map\Textures\camera(" + (i + 1) + ").png", 1, DeleteAllTextures)
	Next
	
	sc_I\CamModelID[CAM_BASE_MODEL] = LoadMesh_Strict("GFX\Map\Props\CamBase.b3d")
	sc_I\CamModelID[CAM_HEAD_MODEL] = LoadMesh_Strict("GFX\Map\Props\CamHead.b3d")
	
	For i = 0 To MaxCamModelIDAmount - 1
		HideEntity(sc_I\CamModelID[i])
	Next
	
	sc_I\ScreenTex = CreateTextureUsingCacheSystem(512, 512, 1 + 256 + 1024)
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
Const MONITOR_079_OVERLAYS_1% = 3
Const MONITOR_079_OVERLAYS_2% = 4
Const MONITOR_079_OVERLAYS_3% = 5
Const MONITOR_895_OVERLAY_1% = 6
Const MONITOR_895_OVERLAY_2% = 7
Const MONITOR_895_OVERLAY_3% = 8
Const MONITOR_895_OVERLAY_4% = 9
Const MONITOR_895_OVERLAY_5% = 10
Const MONITOR_895_OVERLAY_6% = 11
Const MONITOR_895_OVERLAY_7% = 12
Const MONITOR_895_OVERLAY_8% = 13
Const MONITOR_895_OVERLAY_9% = 14
Const MONITOR_895_OVERLAY_10% = 15
Const MONITOR_895_OVERLAY_11% = 16
Const MONITOR_096_OVERLAY% = 17
;[End Block]

Function LoadMonitors%()
	Local i%
	
	mon_I.MonitorInstance = New MonitorInstance
	
	mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY] = LoadTexture_Strict("GFX\Overlays\monitor_overlay.png", 1, DeleteAllTextures)
	mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_1_OVERLAY] = LoadAnimTexture_Strict("GFX\Map\Screens\screen_checkpoint_lockdown.png", 1, 1024, 768, 0, 3, DeleteAllTextures)
	mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_2_OVERLAY] = CreateTextureUsingCacheSystem(1, 1)
	
	mon_I\MonitorOverlayID[MONITOR_079_OVERLAYS_1] = LoadAnimTexture_Strict("GFX\Overlays\scp_079_overlays_X.png", 1, 256, 256, 0, 12, DeleteAllTextures)
	mon_I\MonitorOverlayID[MONITOR_079_OVERLAYS_2] = LoadAnimTexture_Strict("GFX\Overlays\scp_079_overlays_ASCII.png", 1, 256, 256, 0, 6, DeleteAllTextures)
	mon_I\MonitorOverlayID[MONITOR_079_OVERLAYS_3] = LoadAnimTexture_Strict("GFX\Overlays\scp_079_overlays.png", 1, 256, 256, 0, 6, DeleteAllTextures)
	
	For i = MONITOR_895_OVERLAY_1 To MONITOR_895_OVERLAY_11
		mon_I\MonitorOverlayID[i] = LoadTexture_Strict("GFX\Overlays\scp_895_overlay(" + (i - MONITOR_895_OVERLAY_1) + ").png", 1, DeleteAllTextures)
	Next
	
	mon_I\MonitorOverlayID[MONITOR_096_OVERLAY] = LoadTexture_Strict("GFX\Map\Screens\screen_096.png", 1, DeleteAllTextures)
	
	mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL] = LoadMesh_Strict("GFX\Map\Props\monitor2.b3d")
	mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL] = LoadMesh_Strict("GFX\Map\Props\monitor_checkpoint.b3d")
	
	For i = 0 To MaxMonitorModelIDAmount - 1
		HideEntity(mon_I\MonitorModelID[i])
	Next
End Function

Function RemoveMonitorInstances%()
	Local i%
	
	For i = 0 To MaxMonitorModelIDAmount - 1
		FreeEntity(mon_I\MonitorModelID[i]) : mon_I\MonitorModelID[i] = 0
	Next
	For i = MONITOR_DEFAULT_OVERLAY To MONITOR_096_OVERLAY
		mon_I\MonitorOverlayID[i] = 0
	Next
	Delete(mon_I) : mon_I = Null
End Function

Const MaxNPCModelIDAmount% = 34
Const MaxNPCTextureID% = 35

Type NPCInstance
	Field NPCModelID%[MaxNPCModelIDAmount]
	Field NPCTextureName$[MaxNPCTextureID]
	Field Curr173.NPCs
	Field Curr106.NPCs
	Field Curr096.NPCs
	Field Curr049.NPCs
	Field Curr066.NPCs
	Field Curr457.NPCs
	Field Curr999.NPCs
	Field Curr513_1.NPCs
	Field MTFLeader.NPCs, MTFCoLeader.NPCs
	Field IsHalloween%, IsNewYear%, IsAprilFools%
End Type

Global n_I.NPCInstance
; ~ NPC Model ID Constants
;[Block]
Const NPC_008_1_MODEL% = 0
Const NPC_008_1_SURGEON_MODEL% = 1
Const NPC_035_TENTACLE_MODEL% = 2
Const NPC_049_MODEL% = 3
Const NPC_049_2_MODEL% = 4
Const NPC_066_MODEL% = 5
Const NPC_096_MODEL% = 6
Const NPC_106_MODEL% = 7
Const NPC_173_MODEL% = 8
Const NPC_173_HEAD_MODEL% = 9
Const NPC_173_BOX_MODEL% = 10
Const NPC_205_DEMON_1_MODEL% = 11
Const NPC_205_DEMON_2_MODEL% = 12
Const NPC_205_DEMON_3_MODEL% = 13
Const NPC_205_WOMAN_MODEL% = 14
Const NPC_372_MODEL% = 15
Const NPC_513_1_MODEL% = 16
Const NPC_860_2_MODEL% = 17
Const NPC_939_MODEL% = 18
Const NPC_966_MODEL% = 19
Const NPC_999_MODEL% = 20
Const NPC_1048_MODEL% = 21
Const NPC_1048_A_MODEL% = 22
Const NPC_1499_1_MODEL% = 23
Const NPC_APACHE_MODEL% = 24
Const NPC_APACHE_ROTOR_1_MODEL% = 25
Const NPC_APACHE_ROTOR_2_MODEL% = 26
Const NPC_CLERK_MODEL% = 27
Const NPC_CLASS_D_MODEL% = 28
Const NPC_COCKROACH_MODEL% = 29
Const NPC_DUCK_MODEL% = 30
Const NPC_GUARD_MODEL% = 31
Const NPC_MTF_MODEL% = 32
Const NPC_VEHICLE_MODEL% = 33
;[End Block]

; ~ NPC Texture ID Constants
;[Block]
Const NPC_CLASS_D_GONZALES_TEXTURE% = 0
Const NPC_CLASS_D_BENJAMIN_TEXTURE% = 1
Const NPC_CLASS_D_SECURITY_TEXTURE% = 2
Const NPC_CLASS_D_SECURITY_2_TEXTURE% = 3
Const NPC_CLASS_D_SCIENTIST_TEXTURE% = 4
Const NPC_CLASS_D_BURTON_TEXTURE% = 5
Const NPC_CLASS_D_FRANKLIN_TEXTURE% = 6
Const NPC_CLASS_D_MAYNARD_TEXTURE% = 7
Const NPC_CLASS_D_CLASS_D_TEXTURE% = 8
Const NPC_CLASS_D_D9341_TEXTURE% = 9
Const NPC_CLASS_D_JANITOR_TEXTURE% = 10
Const NPC_CLASS_D_MAINTENANCE_TEXTURE% = 11
Const NPC_CLASS_D_LOGISTICS_TEXTURE% = 12
Const NPC_CLASS_D_HARN_TEXTURE% = 13
Const NPC_CLASS_D_RUFINO_TEXTURE% = 14

Const NPC_MTF_LEADER_TEXTURE% = 15

Const NPC_096_BLOODY_TEXTURE% = 16
Const NPC_008_1_TEXTURE% = 17
Const NPC_008_1_TEXTURE_2% = 18

Const NPC_CLASS_D_BODY_1_TEXTURE% = 19
Const NPC_CLASS_D_BODY_2_TEXTURE% = 20
Const NPC_CLASS_D_VICTIM_009_TEXTURE% = 21
Const NPC_CLASS_D_VICTIM_035_TEXTURE% = 22
Const NPC_CLASS_D_VICTIM_035_CORPSE_TEXTURE% = 23
Const NPC_CLASS_D_VICTIM_106_TEXTURE% = 24
Const NPC_CLASS_D_VICTIM_106_FEMUR_BREAKER_TEXTURE% = 25
Const NPC_CLASS_D_VICTIM_409_TEXTURE% = 26
Const NPC_CLASS_D_VICTIM_457_1_TEXTURE% = 27
Const NPC_CLASS_D_VICTIM_457_2_TEXTURE% = 28
Const NPC_CLASS_D_VICTIM_895_TEXTURE% = 29
Const NPC_CLASS_D_VICTIM_939_1_TEXTURE% = 30
Const NPC_CLASS_D_VICTIM_939_2_TEXTURE% = 31
Const NPC_CLASS_D_VICTIM_FEMUR_BREAKER_TEXTURE% = 32

Const NPC_CLERK_VICTIM_205_TEXTURE% = 33

Const NPC_CLASS_D_VICTIM_1048_A_TEXTURE% = 34
;[End Block]

Function LoadNPCs%()
	Local i%
	
	n_I.NPCInstance = New NPCInstance
	
	n_I\NPCTextureName[NPC_CLASS_D_GONZALES_TEXTURE] = "Gonzales"
	n_I\NPCTextureName[NPC_CLASS_D_BENJAMIN_TEXTURE] = "D_9341(2)"
	n_I\NPCTextureName[NPC_CLASS_D_SECURITY_TEXTURE] = "security"
	n_I\NPCTextureName[NPC_CLASS_D_SECURITY_2_TEXTURE] = "security(2)"
	n_I\NPCTextureName[NPC_CLASS_D_SCIENTIST_TEXTURE] = "scientist"
	n_I\NPCTextureName[NPC_CLASS_D_BURTON_TEXTURE] = "Burton"
	n_I\NPCTextureName[NPC_CLASS_D_FRANKLIN_TEXTURE] = "Franklin"
	n_I\NPCTextureName[NPC_CLASS_D_MAYNARD_TEXTURE] = "Maynard"
	n_I\NPCTextureName[NPC_CLASS_D_CLASS_D_TEXTURE] = "class_d(2)"
	n_I\NPCTextureName[NPC_CLASS_D_D9341_TEXTURE] = "D_9341"
	n_I\NPCTextureName[NPC_CLASS_D_JANITOR_TEXTURE] = "janitor"
	n_I\NPCTextureName[NPC_CLASS_D_MAINTENANCE_TEXTURE] = "maintenance"
	n_I\NPCTextureName[NPC_CLASS_D_LOGISTICS_TEXTURE] = "logistics"
	n_I\NPCTextureName[NPC_CLASS_D_HARN_TEXTURE] = "Harn"
	n_I\NPCTextureName[NPC_CLASS_D_RUFINO_TEXTURE] = "Rufino"
	
	n_I\NPCTextureName[NPC_MTF_LEADER_TEXTURE] = "MTF(2)"
	
	n_I\NPCTextureName[NPC_096_BLOODY_TEXTURE] = "scp_096_bloody"
	n_I\NPCTextureName[NPC_008_1_TEXTURE] = "scp_008_1(2)"
	n_I\NPCTextureName[NPC_008_1_TEXTURE_2] = "scp_008_1(3)"
	
	n_I\NPCTextureName[NPC_CLASS_D_BODY_1_TEXTURE] = "body"
	n_I\NPCTextureName[NPC_CLASS_D_BODY_2_TEXTURE] = "body(2)"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_009_TEXTURE] = "scp_009_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_035_TEXTURE] = "scp_035_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_035_CORPSE_TEXTURE] = "scp_035_victim_corpse"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_106_TEXTURE] = "scp_106_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_106_FEMUR_BREAKER_TEXTURE] = "scp_106_victim_femur_breaker"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_409_TEXTURE] = "scp_409_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_457_1_TEXTURE] = "scp_457_victim(0)"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_457_2_TEXTURE] = "scp_457_victim(1)"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_895_TEXTURE] ="scp_895_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_939_1_TEXTURE] = "scp_939_victim"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_939_2_TEXTURE] ="scp_939_victim(2)"
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_FEMUR_BREAKER_TEXTURE] = "femur_breaker_victim"
	n_I\NPCTextureName[NPC_CLERK_VICTIM_205_TEXTURE] = "clerk(2)"
	
	n_I\NPCTextureName[NPC_CLASS_D_VICTIM_1048_A_TEXTURE] = "scp_1048_a_victim"
	
	n_I\NPCModelID[NPC_008_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_008_1.b3d")
	
	n_I\NPCModelID[NPC_008_1_SURGEON_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_008_1_surgeon.b3d")
	
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
	
	n_I\NPCModelID[NPC_999_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_999.b3d")
	
	n_I\NPCModelID[NPC_1048_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1048.b3d")
	n_I\NPCModelID[NPC_1048_A_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1048_a.b3d")
	
	n_I\NPCModelID[NPC_1499_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\scp_1499_1.b3d")
	
	n_I\NPCModelID[NPC_APACHE_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache.b3d")
	n_I\NPCModelID[NPC_APACHE_ROTOR_1_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache_rotor.b3d")
	n_I\NPCModelID[NPC_APACHE_ROTOR_2_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\apache_rotor(2).b3d")
	
	n_I\NPCModelID[NPC_CLERK_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\clerk.b3d")
	
	n_I\NPCModelID[NPC_CLASS_D_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\class_d.b3d")
	
	n_I\NPCModelID[NPC_COCKROACH_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\cockroach.b3d")
	
	n_I\NPCModelID[NPC_DUCK_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\duck.b3d")
	
	n_I\NPCModelID[NPC_GUARD_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\guard.b3d")
	
	n_I\NPCModelID[NPC_MTF_MODEL] = LoadAnimMesh_Strict("GFX\NPCs\MTF.b3d")
	
	n_I\NPCModelID[NPC_VEHICLE_MODEL] = LoadAnimMesh_Strict("GFX\Map\Props\vehicle.b3d")
	
	For i = 0 To MaxNPCModelIDAmount - 1
		HideEntity(n_I\NPCModelID[i])
	Next
End Function

Function RemoveNPCInstances%()
	Local i%
	
	For i = 0 To MaxNPCModelIDAmount - 1
		FreeEntity(n_I\NPCModelID[i]) : n_I\NPCModelID[i] = 0
	Next
	Delete(n_I) : n_I = Null
End Function

Const MaxMTModelIDAmount% = 1
Const MaxLightSpriteIDAmount% = 3

Type MiscInstance
	Field MTModelID%[MaxMTModelIDAmount]
	Field CupLiquid%
	Field LightSpriteID[MaxLightSpriteIDAmount]
	Field AdvancedLightSprite%
	Field SaveScreen%
	Field LightConeModel%
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
	
	misc_I\MTModelID[0] = LoadRMesh("GFX\Map\mt1_generator.rmesh", Null)
	
	HideEntity(misc_I\MTModelID[0])
	
	misc_I\CupLiquid = LoadMesh_Strict("GFX\Items\cup_liquid.b3d")
	HideEntity(misc_I\CupLiquid)
	
	misc_I\SaveScreen = LoadMesh_Strict("GFX\Map\Props\save_screen.b3d")
	HideEntity(misc_I\SaveScreen)
	
	For i = LIGHT_SPRITE_DEFAULT To LIGHT_SPRITE_RED
		misc_I\LightSpriteID[i] = LoadTexture_Strict("GFX\Particles\light(" + i + ").png", 1, DeleteAllTextures)
	Next
	misc_I\AdvancedLightSprite = LoadTexture_Strict("GFX\Particles\advanced_light.png", 1, DeleteAllTextures)
	
	misc_I\LightConeModel = LoadMesh_Strict("GFX\Map\Props\lightcone.b3d")
	HideEntity(misc_I\LightConeModel)
End Function

Function RemoveMiscInstances%()
	Local i%
	
	FreeEntity(misc_I\MTModelID[0]) : misc_I\MTModelID[0] = 0
	
	FreeEntity(misc_I\CupLiquid) : misc_I\CupLiquid = 0
	FreeEntity(misc_I\SaveScreen) : misc_I\SaveScreen = 0
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
			LoadMaterial(File, Loc)
		EndIf
	Wend
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadMaterials(" + File + ")")
End Function

Function InitLoadingScreens%(File$)
	If LoadingScreensDoc <> 0 Then JsonFreeDocument(LoadingScreensDoc) : LoadingScreensDoc = 0
	LoadingScreensDoc = JsonParseFromFile(lang\LanguagePath + File)
	
	If JsonIsArray(LoadingScreensDoc) ; ~ Has localized loading screens -> Use localized only
		LoadingScreens = JsonGetArray(LoadingScreensDoc)
	Else
		JsonFreeDocument(LoadingScreensDoc) : LoadingScreensDoc = 0
		LoadingScreensDoc = JsonParseFromFile(File)
		LoadingScreens = JsonGetArray(LoadingScreensDoc)
	EndIf
End Function

Const ItemsPath$ = "GFX\Items\"
Const ItemHUDTexturePath$ = "GFX\Items\HUD Textures\"
Const ItemINVIconPath$ = "GFX\Items\Inventory Icons\"

Function LoadItems%()
	Local it.ItemTemplates, it2.ItemTemplates
	Local Tex%
	
	; ~ [PAPER]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "doc005"), "Document SCP-005", it_paper, "paper.b3d", "INV_paper.png", "doc_005.png", 0.003, 0, "doc_005.png")
	CreateItemTemplate(GetLocalString("items", "doc008"), "Document SCP-008", it_paper, "paper.b3d", "INV_paper.png", "doc_008.png", 0.003, 0, "doc_008.png")
	CreateItemTemplate(GetLocalString("items", "doc009"), "Document SCP-009", it_paper, "paper.b3d", "INV_paper.png", "doc_009.png", 0.003, 0, "doc_009.png")
	CreateItemTemplate(GetLocalString("items", "doc012"), "Document SCP-012", it_paper, "paper.b3d", "INV_paper.png", "doc_012.png", 0.003, 0, "doc_012.png")
	CreateItemTemplate(GetLocalString("items", "doc035"), "Document SCP-035", it_paper, "paper.b3d", "INV_paper.png", "doc_035_smile.png", 0.003, 0, "doc_035_smile.png")
	CreateItemTemplate(GetLocalString("items", "doc049"), "Document SCP-049", it_paper, "paper.b3d", "INV_paper.png", "doc_049.png", 0.003, 0, "doc_049.png")
	CreateItemTemplate(GetLocalString("items", "doc066"), "Document SCP-066", it_paper, "paper.b3d", "INV_paper.png", "doc_066.png", 0.003, 0, "doc_066.png")
	CreateItemTemplate(GetLocalString("items", "doc079"), "Document SCP-079", it_paper, "paper.b3d", "INV_paper.png", "doc_079.png", 0.003, 0, "doc_079.png")
	CreateItemTemplate(GetLocalString("items", "doc085"), "Document SCP-085", it_paper, "paper.b3d", "INV_paper.png", "doc_085.png", 0.003, 0, "doc_085.png")
	CreateItemTemplate(GetLocalString("items", "doc096"), "Document SCP-096", it_paper, "paper.b3d", "INV_paper.png", "doc_096.png", 0.003, 0, "doc_096.png")
	CreateItemTemplate(GetLocalString("items", "doc106"), "Document SCP-106", it_paper, "paper.b3d", "INV_paper.png", "doc_106.png", 0.003, 0, "doc_106.png")
	CreateItemTemplate(GetLocalString("items", "doc173"), "Document SCP-173", it_paper, "paper.b3d", "INV_paper.png", "doc_173.png", 0.003, 0, "doc_173.png")
	CreateItemTemplate(GetLocalString("items", "doc205"), "Document SCP-205", it_paper, "paper.b3d", "INV_paper.png", "doc_205.png", 0.003, 0, "doc_205.png")
	CreateItemTemplate(GetLocalString("items", "doc294"), "Document SCP-294", it_paper, "paper.b3d", "INV_paper.png", "doc_294.png", 0.003, 0, "doc_294.png")
	CreateItemTemplate(GetLocalString("items", "doc372"), "Document SCP-372", it_paper, "paper.b3d", "INV_paper.png", "doc_372.png", 0.003, 0, "doc_372.png")
	CreateItemTemplate(GetLocalString("items", "doc409"), "Document SCP-409", it_paper, "paper.b3d", "INV_paper.png", "doc_409.png", 0.003, 0, "doc_409.png")
	CreateItemTemplate(GetLocalString("items", "doc427"), "Document SCP-427", it_paper, "paper.b3d", "INV_paper_bloody.png", "doc_427.png", 0.003, 0, "doc_427.png")
	CreateItemTemplate(GetLocalString("items", "doc457"), "Document SCP-457", it_paper, "paper.b3d", "INV_paper.png", "doc_457.png", 0.003, 0, "doc_457.png")
	CreateItemTemplate(GetLocalString("items", "doc458"), "Document SCP-458", it_paper, "paper.b3d", "INV_paper.png", "doc_458.png", 0.003, 0, "doc_458.png")
	CreateItemTemplate(GetLocalString("items", "doc500"), "Document SCP-500", it_paper, "paper.b3d", "INV_paper.png", "doc_500.png", 0.003, 0, "doc_500.png")
	CreateItemTemplate(GetLocalString("items", "doc513"), "Document SCP-513", it_paper, "paper.b3d", "INV_paper.png", "doc_513.png", 0.003, 0, "doc_513.png")
	CreateItemTemplate(GetLocalString("items", "doc682"), "Document SCP-682", it_paper, "paper.b3d", "INV_paper.png", "doc_682.png", 0.003, 0, "doc_682.png")
	CreateItemTemplate(GetLocalString("items", "doc714"), "Document SCP-714", it_paper, "paper.b3d", "INV_paper.png", "doc_714.png", 0.003, 0, "doc_714.png")
	CreateItemTemplate(GetLocalString("items", "doc789j"), "Document SCP-789-J", it_paper, "paper.b3d", "INV_paper.png", "doc_789_j.png", 0.003, 0, "doc_789_j.png")
	CreateItemTemplate(GetLocalString("items", "doc860"), "Document SCP-860", it_paper, "paper.b3d", "INV_paper.png", "doc_860.png", 0.003, 0, "doc_860.png")
	CreateItemTemplate(GetLocalString("items", "doc8601"), "Document SCP-860-1", it_paper, "paper.b3d", "INV_paper.png", "doc_860_1.png", 0.003, 0, "doc_860_1.png")
	CreateItemTemplate(GetLocalString("items", "doc895"), "Document SCP-895", it_paper, "paper.b3d", "INV_paper.png", "doc_895.png", 0.003, 0, "doc_895.png")
	CreateItemTemplate(GetLocalString("items", "doc914"), "Document SCP-914", it_paper, "paper.b3d", "INV_paper.png", "doc_914.png", 0.003, 0, "doc_914.png")
	CreateItemTemplate(GetLocalString("items", "doc939"), "Document SCP-939", it_paper, "paper.b3d", "INV_paper.png", "doc_939.png", 0.003, 0, "doc_939.png")
	CreateItemTemplate(GetLocalString("items", "doc966"), "Document SCP-966", it_paper, "paper.b3d", "INV_paper.png", "doc_966.png", 0.003, 0, "doc_966.png")
	CreateItemTemplate(GetLocalString("items", "doc970"), "Document SCP-970", it_paper, "paper.b3d", "INV_paper.png", "doc_970.png", 0.003, 0, "doc_970.png")
	CreateItemTemplate(GetLocalString("items", "doc999"), "Document SCP-999", it_paper, "paper.b3d", "INV_paper.png", "doc_999.png", 0.003, 0, "doc_999.png")
	CreateItemTemplate(GetLocalString("items", "doc1025"), "Document SCP-1025", it_paper, "paper.b3d", "INV_paper.png", "doc_1025.png", 0.003, 0,  "doc_1025.png")
	CreateItemTemplate(GetLocalString("items", "doc1048"), "Document SCP-1048", it_paper, "paper.b3d", "INV_paper.png", "doc_1048.png", 0.003, 0,  "doc_1048.png")
	CreateItemTemplate(GetLocalString("items", "doc1123"), "Document SCP-1123", it_paper, "paper.b3d", "INV_paper.png", "doc_1123.png", 0.003, 0, "doc_1123.png")
	CreateItemTemplate(GetLocalString("items", "doc1162"), "Document SCP-1162-ARC", it_paper, "paper.b3d", "INV_paper.png", "doc_1162_ARC.png", 0.003, 0, "doc_1162_ARC.png")
	CreateItemTemplate(GetLocalString("items", "doc1499"), "Document SCP-1499", it_paper, "paper.b3d", "INV_paper.png", "doc_1499.png", 0.003, 0, "doc_1499.png")
	CreateItemTemplate(GetLocalString("items", "doc2022"), "Document SCP-2022", it_paper, "paper.b3d", "INV_paper.png", "doc_2022.png", 0.003, 0, "doc_2022.png")
	
	CreateItemTemplate(GetLocalString("items", "doc1048a"), "Incident Report SCP-1048-A", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_1048_a.png", 0.003, 0, "doc_IR_1048_a.png")
	CreateItemTemplate(GetLocalString("items", "doc035a"), "SCP-035 Addendum", it_paper, "paper.b3d", "INV_paper.png", "doc_035_ad.png", 0.003, 0, "doc_035_ad.png")
	CreateItemTemplate(GetLocalString("items", "doc093"), "SCP-093 Recovered Materials", it_paper, "paper.b3d", "INV_paper.png", "doc_093_rm.png", 0.003, 0, "doc_093_rm.png")
	CreateItemTemplate(GetLocalString("items", "doc914log"), "SCP-914 Test Logs #1", it_paper, "paper.b3d", "INV_paper.png", "doc_RAND(2).png", 0.003, 0, "doc_RAND(2).png")
	CreateItemTemplate(GetLocalString("items", "doctestlog"), "SCP-914 Test Logs #2", it_paper, "paper.b3d", "INV_paper.png", "doc_914_Test.png", 0.003, 0, "doc_914_Test.png")
	CreateItemTemplate(GetLocalString("items", "doccdol"), "Class D Orientation Leaflet", it_paper, "paper.b3d", "INV_paper.png", "doc_OL.png", 0.003, 0, "doc_OL.png")
	CreateItemTemplate(GetLocalString("items", "doc"), "Document", it_paper, "paper.b3d", "INV_paper.png", "doc_RAND(3).png", 0.003, 0, "doc_RAND(3).png")
	CreateItemTemplate(GetLocalString("items", "doco5"), "Field Agent Log #235-001-CO5", it_paper, "paper.b3d", "INV_paper.png", "doc_FAL.png", 0.003, 0, "doc_FAL.png")
	CreateItemTemplate(GetLocalString("items", "doco52"), "Groups of Interest Log", it_paper, "paper.b3d", "INV_paper.png", "doc_GOI.png", 0.003, 0, "doc_GOI.png")
	CreateItemTemplate(GetLocalString("items", "docir066"), "Incident Report SCP-066-2", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_066.png", 0.003, 0, "doc_IR_066.png")
	CreateItemTemplate(GetLocalString("items", "docir106"), "Incident Report SCP-106-0204", it_paper, "paper.b3d", "INV_paper.png", "doc_IR_106.png", 0.003, 0, "doc_IR_106.png")
	CreateItemTemplate(GetLocalString("items", "doc_148_response"), "Response to Request #148-1435", it_paper, "paper.b3d", "INV_paper.png", "doc_148_response.png", 0.003, 0, "doc_148_response.png")
	CreateItemTemplate(GetLocalString("items", "docmtf"), "Mobile Task Forces", it_paper, "paper.b3d", "INV_paper.png", "doc_MTF.png", 0.003, 0, "doc_MTF.png")
	CreateItemTemplate(GetLocalString("items", "docgears"), "Note from Gears", it_paper, "paper.b3d", "INV_paper.png", "note_Gears.png", 0.0025, 0, "note_Gears.png")
	CreateItemTemplate(GetLocalString("items", "docdaniel"), "Note from Daniel", it_paper, "note.b3d", "INV_note(2).png", "note_Daniel.png", 0.0025, 0, "note_Daniel.png")
	CreateItemTemplate(GetLocalString("items", "docbryan"), "Note from Bryan", it_paper, "note.b3d", "INV_note(2).png", "note_Bryan.png", 0.0025, 0, "note_Bryan.png")
	CreateItemTemplate(GetLocalString("items", "docndp"), "Nuclear Device Document", it_paper, "paper.b3d", "INV_paper.png", "doc_NDP.png", 0.003, 0, "doc_NDP.png")
	CreateItemTemplate(GetLocalString("items", "dococ"), "Object Classes", it_paper, "paper.b3d", "INV_paper.png", "doc_OBJC.png", 0.003, 0, "doc_OBJC.png")
	CreateItemTemplate(GetLocalString("items", "doclai"), "Log of Anomalous Items", it_paper, "paper.b3d", "INV_paper.png", "doc_LAI.png", 0.003, 0, "doc_LAI.png")
	CreateItemTemplate(GetLocalString("items", "docrp"), "Recall Protocol RP-106-N", it_paper, "paper.b3d", "INV_paper.png", "doc_RP.png", 0.0025, 0, "doc_RP.png")
	CreateItemTemplate(GetLocalString("items", "docrs"), "Research Sector-02 Scheme", it_paper, "paper.b3d", "INV_paper.png", "doc_RS.png", 0.003, 0, "doc_RS.png")
	CreateItemTemplate(GetLocalString("items", "docacs"), "Anomaly Classification System", it_paper, "paper.b3d", "INV_paper.png", "doc_ACS.png", 0.003, 0, "doc_ACS.png")
	CreateItemTemplate(GetLocalString("items", "docdc"), "Disruption Classes", it_paper, "paper.b3d", "INV_paper.png", "doc_DC.png", 0.003, 0, "doc_DC.png")
	CreateItemTemplate(GetLocalString("items", "docrc"), "Risk Classes", it_paper, "paper.b3d", "INV_paper.png", "doc_RC.png", 0.003, 0, "doc_RC.png")
	CreateItemTemplate(GetLocalString("items", "docscl"), "Security Clearance Levels", it_paper, "paper.b3d", "INV_paper.png", "doc_SCL.png", 0.003, 0, "doc_SCL.png")
	CreateItemTemplate(GetLocalString("items", "docst"), "Storage Transfers", it_paper, "paper.b3d", "INV_paper.png", "doc_storagetransfers.png", 0.003, 0, "doc_storagetransfers.png")
	CreateItemTemplate(GetLocalString("items", "docwarn_1"), "Warning Labels #1", it_paper, "paper.b3d", "INV_paper.png", "doc_WARN.png", 0.003, 0, "doc_WARN.png")
	CreateItemTemplate(GetLocalString("items", "docwarn_2"), "Warning Labels #2", it_paper, "paper.b3d", "INV_paper.png", "doc_WARN(2).png", 0.003, 0, "doc_WARN(2).png")
	CreateItemTemplate(GetLocalString("items", "docwarn_3"), "Warning Labels #3", it_paper, "paper.b3d", "INV_paper.png", "doc_WARN(3).png", 0.003, 0, "doc_WARN(3).png")
	CreateItemTemplate(GetLocalString("items", "doccz"), "Containment Zones", it_paper, "paper.b3d", "INV_paper.png", "doc_CZ.png", 0.003, 0, "doc_CZ.png")
	CreateItemTemplate(GetLocalString("items", "docsn"), "Sticky Note", it_paper, "note.b3d", "INV_note(2).png", "note_682.png", 0.0025, 0, "note_682.png")
	CreateItemTemplate(GetLocalString("items", "docmsp"), "The Modular Site Project", it_paper, "paper.b3d", "INV_paper.png", "doc_MSP.png", 0.003, 0, "doc_MSP.png")
	CreateItemTemplate(GetLocalString("items", "docees"), "Emergency Evacuation Shelters", it_paper, "paper.b3d", "INV_paper.png", "doc_EES.png", 0.003, 0, "doc_EES.png")
	CreateItemTemplate(GetLocalString("items", "docsrm"), "Security Room Modules", it_paper, "paper.b3d", "INV_paper.png", "doc_SRM.png", 0.003, 0, "doc_SRM.png")
	CreateItemTemplate(GetLocalString("items", "docdd"), "Device Document", it_paper, "paper.b3d", "INV_paper.png", "doc_DD.png", 0.003, 0, "doc_DD.png")
	CreateItemTemplate(GetLocalString("items", "docblank"), "Blank Paper", it_paper, "paper.b3d", "INV_paper_blank.png", "doc_blank.png", 0.003, 0, "doc_blank.png")
	CreateItemTemplate(GetLocalString("items", "docl_1"), "Blood-stained Note", it_paper, "note.b3d", "INV_note_bloody.png", "note_L(3).png", 0.0025, 0, "note_L(3).png")
	CreateItemTemplate(GetLocalString("items", "docmaynard"), "Burnt Note", it_paper, "paper.b3d", "INV_burnt_note.png", "note_Maynard.png", 0.003, 0, "note_Maynard.png")
	CreateItemTemplate(GetLocalString("items", "docdr"), "Data Report", it_paper, "paper.b3d", "INV_paper_bloody.png", "doc_data.png", 0.003, 0, "doc_data.png")
	CreateItemTemplate(GetLocalString("items", "docdrawing"), "Drawing", it_paper, "paper.b3d", "INV_note.png", "drawing_1048(1).png", 0.003, 0, "drawing_1048(1).png")
	CreateItemTemplate(GetLocalString("items", "docallok"), "Dr. Allok's Note", it_paper, "note.b3d", "INV_note.png", "note_Allok.png", 0.004, 0, "note_Allok.png")
	CreateItemTemplate(GetLocalString("items", "docl_2"), "Dr. L's Note #1", it_paper, "paper.b3d", "INV_note.png", "note_L.png", 0.0025, 0, "note_L.png")
	CreateItemTemplate(GetLocalString("items", "docl_3"), "Dr. L's Note #2", it_paper, "paper.b3d", "INV_note.png", "note_L(2).png", 0.0025, 0, "note_L(2).png")
	CreateItemTemplate(GetLocalString("items", "docl_4"), "Dr. L's Burnt Note #1", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(4).png", 0.0025, 0, "note_L(4).png")
	CreateItemTemplate(GetLocalString("items", "docl_5"), "Dr. L's Burnt Note #2", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(5).png", 0.0025, 0, "note_L(5).png")
	CreateItemTemplate(GetLocalString("items", "papn_1"), "Note From Nobody #1", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(0).png", 0.0025, 0, "note_Nobody(0).png")
	CreateItemTemplate(GetLocalString("items", "papn_2"), "Note From Nobody #2", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(1).png", 0.0025, 0, "note_Nobody(1).png")
	CreateItemTemplate(GetLocalString("items", "papn_3"), "Note From Nobody #3", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(2).png", 0.0025, 0, "note_Nobody(2).png")
	CreateItemTemplate(GetLocalString("items", "papn_4"), "Note From Nobody #4", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(3).png", 0.0025, 0, "note_Nobody(3).png")
	CreateItemTemplate(GetLocalString("items", "papn_5"), "Note From Nobody #5", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(4).png", 0.0025, 0, "note_Nobody(4).png")
	CreateItemTemplate(GetLocalString("items", "papn_6"), "Note From Nobody #6", it_paper, "paper.b3d", "INV_paper(2).png", "note_Nobody(5).png", 0.0025, 0, "note_Nobody(5).png")
	CreateItemTemplate(GetLocalString("items", "docjournal"), "Journal Page", it_paper, "paper.b3d", "INV_note.png", "note_Gonzales.png", 0.0025, 0, "note_Gonzales.png")
	CreateItemTemplate(GetLocalString("items", "docleaflet"), "Leaflet", it_paper, "paper.b3d", "INV_note.png", "leaflet.png", 0.003, 0, "leaflet.png")
	CreateItemTemplate(GetLocalString("items", "doclog_1"), "Log #1", it_paper, "paper.b3d", "INV_paper(2).png", "note_forest(0).png", 0.002, 0, "note_forest(0).png")
	CreateItemTemplate(GetLocalString("items", "doclog_2"), "Log #2", it_paper, "paper.b3d", "INV_paper(2).png", "note_forest(1).png", 0.002, 0, "note_forest(1).png")
	CreateItemTemplate(GetLocalString("items", "doclog_3"), "Log #3", it_paper, "paper.b3d", "INV_paper(2).png", "note_forest(2).png", 0.002, 0, "note_forest(2).png")
	CreateItemTemplate(GetLocalString("items", "docmn"), "Mysterious Note", it_paper, "paper.b3d", "INV_note.png", "note_mysterious.png", 0.003, 0, "note_mysterious.png")
	CreateItemTemplate(GetLocalString("items", "docnotemaynard"), "Note from Maynard", it_paper, "note.b3d", "INV_note.png", "note_Maynard(2).png", 0.0025, 0, "note_Maynard(2).png")
	CreateItemTemplate(GetLocalString("items", "docrand"), "Notification", it_paper, "paper.b3d", "INV_note.png", "doc_RAND.png", 0.003, 0, "doc_RAND.png")
	CreateItemTemplate(GetLocalString("items", "docl_6"), "Scorched Note", it_paper, "paper.b3d", "INV_burnt_note.png", "note_L(6).png", 0.0025, 0, "note_L(6).png")
	CreateItemTemplate(GetLocalString("items", "docsnm"), "Strange Note", it_paper, "paper.b3d", "INV_note.png", "note_strange.png", 0.0025, 0, "note_strange.png")
	CreateItemTemplate(GetLocalString("items", "docun"), "Unknown Note", it_paper, "note.b3d", "INV_note_bloody.png", "note_unknown.png", 0.003, 0, "note_unknown.png")
	CreateItemTemplate(GetLocalString("items", "doctb"), "Testing Brief", it_paper, "paper.b3d", "INV_paper.png", "doc_TB.png", 0.0025, 0, "doc_TB.png")
	CreateItemTemplate("SCP-085", "SCP-085", it_paper, "note.b3d", "INV_note.png", "note_085(0).png", 0.0033, 0, "note_085(0).png")
	CreateItemTemplate(GetLocalString("items", "docdh"), "Disciplinary Hearing DH-S-4137-17092", it_oldpaper, "paper.b3d", "INV_paper.png", "doc_DH.png", 0.003, 0, "doc_DH.png")
	
	CreateItemTemplate(GetLocalString("items", "origami"), "Origami", it_origami, "origami.b3d", "INV_origami.png", "", 0.003, 0)
	
	CreateItemTemplate(GetLocalString("items", "badge"), "Emily Ross' Badge", it_badge, "badge.b3d", "INV_Emily_badge.png", "Emily_badge.png", 0.0001, 1, "Emily_badge.png")
	CreateItemTemplate(GetLocalString("items", "burntbadge"), "George Maynard's Badge", it_badge, "badge.b3d", "INV_Maynard_badge.png", "Maynard_badge.png", 0.0001, 1, "Maynard_badge.png")
	CreateItemTemplate(GetLocalString("items", "harnbadge"), "Asav Harn's Badge", it_badge, "badge.b3d", "INV_harn_badge.png", "harn_badge.png", 0.0001, 1, "harn_badge.png")
	CreateItemTemplate(GetLocalString("items", "gonzalesbadge"), "Jim Gonzales' Badge", it_badge, "badge.b3d", "INV_Gonzales_badge.png", "Gonzales_badge.png", 0.0001, 1, "Gonzales_badge.png")
	CreateItemTemplate(GetLocalString("items", "skinnerbadge"), "Brian Skinner's Badge", it_badge, "badge.b3d", "INV_Skinner_badge.png", "Skinner_badge.png", 0.0001, 1, "Skinner_badge.png")
	CreateItemTemplate(GetLocalString("items", "rosewoodbadge"), "Victor Rosewood's Badge", it_badge2, "badge.b3d", "INV_Rosewood_badge.png", "Rosewood_badge.png", 0.0001, 1, "Rosewood_badge.png", "", False, 1 + 2 + 8)
	CreateItemTemplate(GetLocalString("items", "oldbadge"), "Old Badge", it_badge2, "badge.b3d", "INV_D_9341_badge.png", "D_9341_badge.png", 0.0001, 1, "D_9341_badge.png", "", False, 1 + 2 + 8)
	
	CreateItemTemplate(GetLocalString("items", "ticket"), "Movie Ticket", it_ticket, "badge.b3d", "INV_ticket.png", "ticket.png", 0.0001, 0, "ticket.png", "", False, 1 + 2 + 8)
	;[End Block]
	
	; ~ [SCPs AND VARIATIONS]
	;[Block]
	CreateItemTemplate("SCP-005", "SCP-005", it_scp005, "scp_005.b3d", "INV_scp_005.png", "", 0.005, 3)
	CreateItemTemplate("SCP-005", "Coarse SCP-005", it_coarse005, "scp_005.b3d", "INV_scp_005.png", "", 0.005, 3)
	CreateItemTemplate("SCP-005", "Crystallized SCP-005", it_crystal005, "scp_005.b3d", "INV_scp_005_crystal.png", "", 0.005, 3, "scp_005_crystal.png")
	
	CreateItemTemplate(GetLocalString("items", "148"), "SCP-148 Ingot", it_scp148ingot, "scp_148.b3d", "INV_scp_148.png", "", RoomScale, 2)
	CreateItemTemplate(GetLocalString("items", "metalpanel"), "Metal Panel", it_scp148, "metal_panel.b3d", "INV_metal_panel.png", "", RoomScale, 2)
	
	CreateItemTemplate("SCP-268", "SCP-268", it_scp268, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	CreateItemTemplate("SCP-268", "Fine SCP-268", it_fine268, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	CreateItemTemplate(GetLocalString("items", "cap"), "Newsboy Cap", it_cap, "scp_268.b3d", "INV_scp_268.png", "", 0.09, 2)
	
	CreateItemTemplate(GetLocalString("items", "420j"), "Some SCP-420-J", it_scp420j, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.00055, 0)
	CreateItemTemplate(GetLocalString("items", "cigarette"), "Cigarette", it_cigarette, "cigarette.b3d", "INV_cigarette.png", "", 0.0032, 0)
	CreateItemTemplate(GetLocalString("items", "joint"), "Joint", it_joint, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.00045, 0)
	CreateItemTemplate(GetLocalString("items", "smellyjoint"), "Smelly Joint", it_joint_smelly, "scp_420_j.b3d", "INV_scp_420_j.png", "", 0.00045, 0)
	
	CreateItemTemplate("SCP-427", "SCP-427", it_scp427, "scp_427.b3d", "INV_scp_427.png", "", 0.001, 3, "", "INV_scp_427_opened.png")
	CreateItemTemplate("SCP-500", "SCP-500", it_scp500, "scp_500.b3d", "INV_scp_500.png", "", 0.03, 2, "", "", True)
	it.ItemTemplates = CreateItemTemplate("SCP-500-01", "SCP-500-01", it_scp500pill, "pill.b3d", "INV_scp_500_pill.png", "", 0.0003, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "500death"), "Upgraded Pill", it_scp500pilldeath, "pill.b3d", "INV_scp_500_pill.png", "", 0.0003, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "pill"), "Pill", it_pill, "pill.b3d", "INV_pill.png", "", 0.0003, 2)
	EntityColor(it\OBJ, 255.0, 255.0, 255.0)
	
	CreateItemTemplate("SCP-513", "SCP-513", it_scp513, "scp_513.b3d", "INV_scp_513.png", "", 0.1, 2)
	CreateItemTemplate("SCP-513", "Fine SCP-513", it_fine513, "scp_513.b3d", "INV_scp_513_no_rust.png", "", 0.1, 2, "scp_513_no_rust.png")
	
	it.ItemTemplates = CreateItemTemplate("SCP-714", "SCP-714", it_scp714, "scp_714.b3d", "INV_scp_714.png", "", 0.2, 3)
	EntityColor(it\OBJ, 125.0, 200.0, 125.0)
	it.ItemTemplates = CreateItemTemplate("SCP-714", "Coarse SCP-714", it_coarse714, "scp_714.b3d", "INV_scp_714_grey.png", "", 0.2, 3)
	EntityColor(it\OBJ, 150.0, 150.0, 150.0)
	it.ItemTemplates = CreateItemTemplate("SCP-714", "Fine SCP-714", it_fine714, "scp_714.b3d", "INV_scp_714_blue.png", "", 0.2, 3)
	EntityColor(it\OBJ, 140.0, 200.0, 200.0)
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "ring"), "Green Jade Ring", it_ring, "scp_714.b3d", "INV_scp_714_small.png", "", 0.15, 3)
	EntityColor(it\OBJ, 125.0, 200.0, 125.0)
	
	it.ItemTemplates = CreateItemTemplate("SCP-860", "SCP-860", it_scp860, "scp_860.b3d", "INV_scp_860.png", "", 0.003, 3)
	EntityColor(it\OBJ, 60.0, 60.0, 130.0)
	it.ItemTemplates = CreateItemTemplate("SCP-860", "Fine SCP-860", it_fine860, "scp_860.b3d", "INV_scp_860_red.png", "", 0.003, 3)
	EntityColor(it\OBJ, 130.0, 60.0, 60.0)
	
	CreateItemTemplate("SCP-1025", "SCP-1025", it_scp1025, "scp_1025.b3d", "INV_scp_1025.png", "", 0.1, 0)
	CreateItemTemplate("SCP-1025", "Fine SCP-1025", it_fine1025, "scp_1025.b3d", "INV_scp_1025_blue.png", "", 0.1, 0, "scp_1025_blue.png")
	CreateItemTemplate(GetLocalString("items", "book"), "Book", it_book, "scp_1025.b3d", "INV_book.png", "", 0.07, 0, "book.png")
	
	CreateItemTemplate("SCP-1123", "SCP-1123", it_scp1123, "scp_1123.b3d", "INV_scp_1123.png", "", 0.015, 2)
	
	CreateItemTemplate("SCP-1499", "SCP-1499", it_scp1499, "scp_1499.b3d", "INV_scp_1499.png", "", 0.022, 2)
	CreateItemTemplate("SCP-1499", "Fine SCP-1499", it_fine1499, "scp_1499.b3d", "INV_scp_1499.png", "", 0.022, 2)
	
	CreateItemTemplate("SCP-2022", "SCP-2022", it_scp2022, "scp_2022.b3d", "INV_scp_2022.png", "", 0.03, 1)
	CreateItemTemplate("SCP-2022-01", "SCP-2022-01", it_scp2022pill, "pill.b3d", "INV_scp_2022_pill.png", "", 0.0003, 2, "scp_2022_01.png")
	;[End Block]
	
	; ~ [MISC ITEMS]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "helmet"), "Ballistic Helmet", it_helmet, "ballistic_helmet.b3d", "INV_ballistic_helmet.png", "", 0.018, 2)
	
	CreateItemTemplate(GetLocalString("items", "vest"), "Ballistic Vest", it_vest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "corrvest"), "Corrosive Ballistic Vest", it_corrvest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.02, 2, "ballistic_vest_corrosive.png")
	CreateItemTemplate(GetLocalString("items", "finevest"), "Heavy Ballistic Vest", it_finevest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.022, 2)
	CreateItemTemplate(GetLocalString("items", "veryfinevest"), "Bulky Ballistic Vest", it_veryfinevest, "ballistic_vest.b3d", "INV_ballistic_vest.png", "", 0.025, 2)
	
	CreateItemTemplate(GetLocalString("items", "cup"), "Cup", it_cup, "cup.b3d", "INV_cup_filled.png", "", 0.04, 2)
	CreateItemTemplate(GetLocalString("items", "emptycup"), "Empty Cup", it_emptycup, "cup.b3d", "INV_cup_empty.png", "", 0.04, 2)
	
	CreateItemTemplate(GetLocalString("items", "clipboard"), "Clipboard", it_clipboard, "clipboard.b3d", "INV_clipboard_filled.png", "", 0.003, 1, "", "INV_clipboard_empty.png", True)
	CreateItemTemplate(GetLocalString("items", "wallet"), "Wallet", it_wallet, "wallet.b3d", "INV_wallet_filled.png", "", 0.055, 2, "", "INV_wallet_empty.png", True)
	
	CreateItemTemplate(GetLocalString("items", "electronics"), "Electronical Components", it_electronics, "circuits.b3d", "INV_circuits.png", "", 0.0011, 1)
	
	CreateItemTemplate(GetLocalString("items", "eyedrops"), "ReVision Eyedrops", it_eyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0011, 1)
	CreateItemTemplate(GetLocalString("items", "eyedrops.red"), "RedVision Eyedrops", it_eyedrops2, "eye_drops.b3d", "INV_eye_drops_red.png", "", 0.0011, 1, "eye_drops_red.png")
	CreateItemTemplate(GetLocalString("items", "eyedrops_2"), "Fine Eyedrops", it_fineeyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate(GetLocalString("items", "eyedrops_2"), "Very Fine Eyedrops", it_veryfineeyedrops, "eye_drops.b3d", "INV_eye_drops.png", "", 0.0013, 1)
	
	CreateItemTemplate(GetLocalString("items", "fak"), "First Aid Kit", it_firstaid, "first_aid_kit.b3d", "INV_first_aid_kit.png", "", 0.05, 1)
	CreateItemTemplate(GetLocalString("items", "bfak"), "Blue First Aid Kit", it_firstaid2, "first_aid_kit.b3d", "INV_first_aid_kit_blue.png", "", 0.03, 1, "first_aid_kit(2).png")
	CreateItemTemplate(GetLocalString("items", "cfak"), "Compact First Aid Kit", it_finefirstaid, "first_aid_kit.b3d", "INV_first_aid_kit_compact.png", "", 0.03, 1)
	CreateItemTemplate(GetLocalString("items", "sb"), "Strange Bottle", it_veryfinefirstaid, "eye_drops.b3d", "INV_strange_bottle.png", "", 0.002, 1, "strange_bottle.png")
	
	CreateItemTemplate(GetLocalString("items", "mask"), "Gas Mask", it_gasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.019, 2)
	CreateItemTemplate(GetLocalString("items", "mask"), "Fine Gas Mask", it_finegasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.019, 2)
	CreateItemTemplate(GetLocalString("items", "mask"), "Very Fine Gas Mask", it_veryfinegasmask, "gas_mask.b3d", "INV_gas_mask.png", "", 0.02, 2)
	CreateItemTemplate(GetLocalString("items", "mask148"), "Heavy Gas Mask", it_gasmask148, "gas_mask.b3d", "INV_gas_mask_heavy.png", "", 0.02, 2, "gas_mask_heavy.png")
	
	CreateItemTemplate(GetLocalString("items", "headphones"), "Headphones", it_headphones, "headphones.b3d", "INV_headphones.png", "", 0.155, 2)
	
	CreateItemTemplate(GetLocalString("items", "suit"), "Hazmat Suit", it_hazmatsuit, "hazmat_suit.b3d", "INV_hazmat_suit.png", "", 0.013, 2, "", "", True)
	CreateItemTemplate(GetLocalString("items", "suitfire"), "Fire Suit", it_finehazmatsuit, "hazmat_suit.b3d", "INV_fire_suit.png", "", 0.013, 2, "fire_suit.png", "", True)
	CreateItemTemplate(GetLocalString("items", "suit"), "Very Fine Hazmat Suit", it_veryfinehazmatsuit, "hazmat_suit.b3d", "INV_hazmat_suit.png", "", 0.013, 2, "", "", True)
	CreateItemTemplate(GetLocalString("items", "suit148"), "Heavy Hazmat Suit", it_hazmatsuit148, "hazmat_suit.b3d", "INV_hazmat_suit_heavy.png", "", 0.013, 2, "hazmat_suit_heavy.png", "", True)
	
	CreateItemTemplate(GetLocalString("items", "nvg"), "Night Vision Goggles", it_nvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_off.png", "", 0.02, 2, "", "INV_night_vision_goggles_on_green.png")
	CreateItemTemplate(GetLocalString("items", "nvg"), "Fine Night Vision Goggles", it_finenvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_off.png", "", 0.02, 2, "", "INV_night_vision_goggles_on_red.png")
	CreateItemTemplate(GetLocalString("items", "nvg"), "Very Fine Night Vision Goggles", it_veryfinenvg, "night_vision_goggles.b3d", "INV_night_vision_goggles_off.png", "", 0.02, 2, "", "INV_night_vision_goggles_on_blue.png")
	CreateItemTemplate(GetLocalString("items", "scramble"), "SCRAMBLE Gear", it_scramble, "SCRAMBLE_gear.b3d", "INV_SCRAMBLE_gear_off.png", "", 0.02, 2, "", "INV_SCRAMBLE_gear_on.png")
	CreateItemTemplate(GetLocalString("items", "scramble"), "Fine SCRAMBLE Gear", it_finescramble, "SCRAMBLE_gear.b3d", "INV_SCRAMBLE_gear_off.png", "", 0.02, 2, "", "INV_SCRAMBLE_gear_on.png")
	
	; ~ HUD texture is defined in "UpdateGUI"
	;[Block]
	CreateItemTemplate(GetLocalString("items", "radio"), "Radio Transceiver", it_radio, "radio.b3d", "INV_radio.png", "", 0.9, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "18V Radio Transceiver", it_18vradio, "radio.b3d", "INV_radio.png", "", 0.92, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "Fine Radio Transceiver", it_fineradio, "radio.b3d", "INV_radio.png", "", 0.9, 1)
	CreateItemTemplate(GetLocalString("items", "radio"), "Very Fine Radio Transceiver", it_veryfineradio, "radio.b3d", "INV_radio.png", "", 0.9, 1)
	
	CreateItemTemplate(GetLocalString("items", "nav"), "S-NAV Navigator", it_nav, "navigator.b3d", "INV_navigator.png", "", 0.00072, 1)
	CreateItemTemplate(GetLocalString("items", "nav300"), "S-NAV 300 Navigator", it_nav300, "navigator.b3d", "INV_navigator.png", "", 0.00074, 1)
	CreateItemTemplate(GetLocalString("items", "nav310"), "S-NAV 310 Navigator", it_nav310, "navigator.b3d", "INV_navigator.png", "", 0.00072, 1)
	CreateItemTemplate(GetLocalString("items", "navulti"), "S-NAV Navigator Ultimate", it_navulti, "navigator.b3d", "INV_navigator.png", "", 0.00072, 1)
	
	CreateItemTemplate(GetLocalString("items", "e.reader"), "E-Reader", it_e_reader, "e_reader.b3d", "INV_e_reader.png", "", 0.0012, 1)
	CreateItemTemplate(GetLocalString("items", "e.reader20"), "E-Reader 20", it_e_reader20, "e_reader.b3d", "INV_e_reader.png", "", 0.0012, 1)
	CreateItemTemplate(GetLocalString("items", "e.readerulti"), "E-Reader Ultimate", it_e_readerulti, "e_reader.b3d", "INV_e_reader.png", "", 0.0012, 1)
	;[End Block]
	
	CreateItemTemplate(GetLocalString("items", "45bat"), "4.5V Battery", it_coarsebat, "battery.b3d", "INV_battery_4.5v.png", "", 0.0065, 1)
	CreateItemTemplate(GetLocalString("items", "bat"), "9V Battery", it_bat, "battery.b3d", "INV_battery_9v.png", "", 0.0065, 1, "battery_9V.png")
	CreateItemTemplate(GetLocalString("items", "18bat"), "18V Battery", it_finebat, "battery.b3d", "INV_battery_18v.png", "", 0.0075, 1, "battery_18V.png")
	CreateItemTemplate(GetLocalString("items", "999bat"), "999V Battery", it_veryfinebat, "battery.b3d", "INV_battery_999v.png", "", 0.007, 1, "battery_999V.png")
	CreateItemTemplate(GetLocalString("items", "killbat"), "Strange Battery", it_killbat, "battery.b3d", "INV_strange_battery.png", "", 0.007, 1, "strange_battery.png")
	
	CreateItemTemplate(GetLocalString("items", "syringe"), "Syringe", it_syringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Fine Syringe", it_finesyringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Very Fine Syringe", it_veryfinesyringe, "syringe.b3d", "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate(GetLocalString("items", "syringe"), "Infected Syringe", it_syringeinf, "syringe.b3d", "INV_syringe_infect.png", "", 0.005, 2, "syringe_infect.png")
	;[End Block]
	
	; ~ [KEYCARDS, HANDS, KEYS, CARDS, COINS]
	;[Block]
	CreateItemTemplate(GetLocalString("items", "key0"), "Level 0 Key Card", it_key0, "key_card.b3d", "INV_key_card_lvl_0.png", "", 0.00037, 1)
	CreateItemTemplate(GetLocalString("items", "key0"), "Bloody Level 0 Key Card", it_key0, "key_card.b3d", "INV_key_card_lvl_0_bloody.png", "", 0.00037, 1, "key_card_lvl_0_bloody.png")
	CreateItemTemplate(GetLocalString("items", "key1"), "Level 1 key Card", it_key1, "key_card.b3d", "INV_key_card_lvl_1.png", "", 0.00037, 1, "key_card_lvl_1.png")
	CreateItemTemplate(GetLocalString("items", "key1"), "Bloody Level 1 key Card", it_key1, "key_card.b3d", "INV_key_card_lvl_1_bloody.png", "", 0.00037, 1, "key_card_lvl_1_bloody.png")
	CreateItemTemplate(GetLocalString("items", "key2"), "Level 2 key Card", it_key2, "key_card.b3d", "INV_key_card_lvl_2.png", "", 0.00037, 1, "key_card_lvl_2.png")
	CreateItemTemplate(GetLocalString("items", "key3"), "Level 3 key Card", it_key3, "key_card.b3d", "INV_key_card_lvl_3.png", "", 0.00037, 1, "key_card_lvl_3.png")
	CreateItemTemplate(GetLocalString("items", "key3"), "Bloody Level 3 key Card", it_key3, "key_card.b3d", "INV_key_card_lvl_3_bloody.png", "", 0.00037, 1, "key_card_lvl_3_bloody.png")
	CreateItemTemplate(GetLocalString("items", "key4"), "Level 4 key Card", it_key4, "key_card.b3d", "INV_key_card_lvl_4.png", "", 0.00037, 1, "key_card_lvl_4.png")
	CreateItemTemplate(GetLocalString("items", "key5"), "Level 5 key Card", it_key5, "key_card.b3d", "INV_key_card_lvl_5.png", "", 0.00037, 1, "key_card_lvl_5.png")
	CreateItemTemplate(GetLocalString("items", "key6"), "Level 6 Key Card", it_key6, "key_card.b3d", "INV_key_card_lvl_6.png", "", 0.00037, 1, "key_card_lvl_6.png")
	CreateItemTemplate(GetLocalString("items", "keyomni"), "Key Card Omni", it_keyomni, "key_card.b3d", "INV_key_card_lvl_omni.png", "", 0.00037, 1, "key_card_lvl_omni.png")
	
	CreateItemTemplate(GetLocalString("items", "mastercard"), "Mastercard", it_mastercard, "key_card.b3d", "INV_master_card.png", "", 0.00037, 1, "master_card.png")
	CreateItemTemplate(GetLocalString("items", "mastercard"), "Mastercard", it_mastercard_golden, "key_card.b3d", "INV_master_card_golden.png", "", 0.00037, 1, "master_card_golden.png")
	CreateItemTemplate(GetLocalString("items", "playcard"), "Playing Card", it_playcard, "key_card.b3d", "INV_playing_card.png", "", 0.00037, 1, "playing_card.png")
	
	CreateItemTemplate(GetLocalString("items", "hand"), "White Severed Hand", it_hand, "severed_hand.b3d", "INV_severed_hand_white.png", "", 0.033, 2)
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "hand"), "Black Severed Hand", it_hand2, "severed_hand.b3d", "INV_severed_hand_black.png", "", 0.033, 2)
	EntityColor(it\OBJ, 96.0, 67.0, 46.0)
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "hand"), "Yellow Severed Hand", it_hand3, "severed_hand.b3d", "INV_severed_hand_yellow.png", "", 0.033, 2)
	EntityColor(it\OBJ, 200.0, 176.0, 146.0)
	
	CreateItemTemplate(GetLocalString("items", "key.simple"), "White Key", it_key_white, "key.b3d", "INV_key.png", "", 0.0027, 3)
	CreateItemTemplate(GetLocalString("items", "key"), "Lost Key", it_lostkey, "key.b3d", "INV_lost_key.png", "", 0.0027, 3, "lost_key.png")
	it.ItemTemplates = CreateItemTemplate(GetLocalString("items", "key.simple"), "Yellow Key", it_key_yellow, "key.b3d", "INV_key(2).png", "", 0.0027, 3)
	EntityColor(it\OBJ, 180.0, 150.0, 110.0)
	
	CreateItemTemplate(GetLocalString("items", "25ct"), "Quarter", it_25ct, "coin.b3d", "INV_coin.png", "", 0.0004, 3)
	CreateItemTemplate(GetLocalString("items", "coin"), "Coin", it_coin, "coin.b3d", "INV_coin_rusty.png", "", 0.0004, 3, "coin_rusty.png")
	
	CreateItemTemplate(GetLocalString("items", "pizza"), "Pizza Slice", it_pizza, "Pizza_Slice.b3d", "INV_Pizza_Slice.png", "", 0.05, 2)
	;[End Block]
	
	For it.ItemTemplates = Each ItemTemplates
		If it\Tex <> 0
			If it\TexPath <> ""
				For it2.ItemTemplates = Each ItemTemplates
					If it2 <> it And it2\Tex = it\Tex Then DeleteSingleTextureEntryFromCache(it2\Tex) : it2\Tex = 0
				Next
			EndIf
			DeleteSingleTextureEntryFromCache(it\Tex) : it\Tex = 0
		EndIf
	Next
End Function

Global SoundTransmission%
Global SoundEmitter%

Const MaxTempSounds% = 10

Global TempSounds%[MaxTempSounds]
Global TempSoundsName$[MaxTempSounds]
Global TempSoundIndex% = 0

; ~ The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Global Music$[35]

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
Music[9] = "860_1_Blue"
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
Music[33] = "860_1_Red"
Music[34] = "457Chamber"
;[End Block]

Global MusicCHN%
MusicCHN = StreamSound_Strict("SFX\Music\" + Music[2] + ".ogg", opt\MusicVolume, ModeLoop)

Global NowPlaying% = 2, ShouldPlay% = 11
Global CurrMusic% = True

Dim OpenDoorSFX%(7, 3), CloseDoorSFX%(7, 3)

Type SoundInstance
	Field RoomAmbience%[13]
	Field CloseDecayDoorSFX%[4], OpenDecayDoorSFX%[4]
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
	Field FemurBreakerSFX%
	Field CrouchSFX%
	Field DecaySFX%[5]
	Field BurstSFX%
	Field HissSFX%[2]
	Field RustleSFX%[6]
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
	Field LowBatterySFX%[2]
	Field ElevatorBeepSFX%, ElevatorMoveSFX%
	Field PickSFX%[4]
	Field SCP106SFX%[8]
	Field SCP173SFX%[3]
	Field HorrorSFX%[14]
	Field MissSFX%
	Field IntroSFX%[7]
	Field AlarmSFX%[3]
	Field DamageSFX%[14]
	Field HeartBeatSFX%
	Field NeckSnapSFX%[3]
	Field VomitSFX%
	Field BreathGasRelaxedSFX%
	Field Step2SFX%[13]
	Field MachineSFX%
	Field BlindsSFX%
	Field SparkShortSFX%
	Field SinkHoleSFX%
	Field WatchesSFX%
	Field FireSFX%
	Field BuzzingSFX%
	Field AirlockSFX%
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
Const SOUND_NPC_457_FIRE% = 8
Const SOUND_NPC_457_SIGHTING% = 9
Const SOUND_NPC_VEHICLE_IDLE% = 10
Const SOUND_NPC_VEHICLE_MOVING% = 11
Const SOUND_NPC_APACHE_PROPELLER% = 12
;[End Block]
Const MaxNPCSounds% = 13
Global NPCSound%[MaxNPCSounds]

Function LoadSounds%()
	Local i%
	
	RenderLoading(45, GetLocalString("loading", "sounds"))
	
	snd_I.SoundInstance = New SoundInstance
	
	snd_I\RoomAmbience[0] = LoadSound_Strict("SFX\Ambient\Room ambience\rumble.ogg")
	snd_I\RoomAmbience[1] = LoadSound_Strict("SFX\Ambient\Room ambience\lowdrone.ogg")
	snd_I\RoomAmbience[2] = LoadSound_Strict("SFX\Ambient\Room ambience\pulsing.ogg")
	snd_I\RoomAmbience[3] = LoadSound_Strict("SFX\Ambient\Room ambience\ventilation.ogg")
	snd_I\RoomAmbience[4] = LoadSound_Strict("SFX\Ambient\Room ambience\drip.ogg")
	snd_I\RoomAmbience[5] = LoadSound_Strict("SFX\Alarm\Alarm0.ogg")
	snd_I\RoomAmbience[6] = LoadSound_Strict("SFX\Ambient\Room ambience\895.ogg")
	snd_I\RoomAmbience[7] = LoadSound_Strict("SFX\Ambient\Room ambience\fuelpump.ogg")
	snd_I\RoomAmbience[8] = LoadSound_Strict("SFX\Ambient\Room ambience\Fan.ogg")
	snd_I\RoomAmbience[9] = LoadSound_Strict("SFX\Ambient\Room ambience\servers1.ogg")
	snd_I\RoomAmbience[10] = LoadSound_Strict("SFX\Ambient\Room ambience\173chamber.ogg")
	snd_I\RoomAmbience[11] = LoadSound_Strict("SFX\Ambient\Room ambience\372Cell.ogg")
	snd_I\RoomAmbience[12] = LoadSound_Strict("SFX\Ambient\Room ambience\water_pipe.ogg")
	
	snd_I\OpenDecayDoorSFX[DEFAULT_DOOR] = LoadSound_Strict("SFX\Door\DoorDecayOpen.ogg") ; ~ Also one-sided door
	snd_I\CloseDecayDoorSFX[DEFAULT_DOOR] = LoadSound_Strict("SFX\Door\DoorDecayClose.ogg") ; ~ Also one-sided door
	snd_I\OpenDecayDoorSFX[ELEVATOR_DOOR] = LoadSound_Strict("SFX\Door\ElevatorDecayOpen.ogg")
	snd_I\CloseDecayDoorSFX[ELEVATOR_DOOR] = LoadSound_Strict("SFX\Door\ElevatorDecayClose.ogg")
	snd_I\OpenDecayDoorSFX[HEAVY_DOOR] = LoadSound_Strict("SFX\Door\Door2DecayOpen.ogg")
	snd_I\CloseDecayDoorSFX[HEAVY_DOOR] = LoadSound_Strict("SFX\Door\Door2DecayClose.ogg")
	snd_I\OpenDecayDoorSFX[BIG_DOOR] = LoadSound_Strict("SFX\Door\BigDoorDecayOpen.ogg")
	snd_I\CloseDecayDoorSFX[BIG_DOOR] = LoadSound_Strict("SFX\Door\BigDoorDecayClose.ogg")
	
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
			OpenDoorSFX(FENCE_DOOR, i) = LoadSound_Strict("SFX\Door\FenceDoorOpen" + i + ".ogg")
			snd_I\BigDoorErrorSFX[i] = LoadSound_Strict("SFX\Door\BigDoorError" + i + ".ogg")
			
			snd_I\NeckSnapSFX[i] = LoadSound_Strict("SFX\SCP\173\NeckSnap" + i + ".ogg")
			CoughSFX(0, i) = LoadSound_Strict("SFX\Character\D9341\Cough" + i + ".ogg")
			CoughSFX(1, i) = LoadSound_Strict("SFX\Character\D9341\Cough" + i + "Gas.ogg")
			
			snd_I\SCP106SFX[i] = LoadSound_Strict("SFX\SCP\106\Corrosion" + i + ".ogg")
			snd_I\SCP106SFX[i + 5] = LoadSound_Strict("SFX\SCP\106\WallDecay" + i + ".ogg")
			
			snd_I\SCP173SFX[i] = LoadSound_Strict("SFX\SCP\173\Rattle" + i + ".ogg")
			
			snd_I\LightSFX[i] = LoadSound_Strict("SFX\Room\Light" + i + ".ogg")
			
			StepSFX(2, 0, i) = LoadSound_Strict("SFX\Step\StepPD" + i + ".ogg")
			StepSFX(3, 0, i) = LoadSound_Strict("SFX\Step\StepCloth" + i + ".ogg")
			StepSFX(4, 0, i) = LoadSound_Strict("SFX\Step\StepForest" + i + ".ogg")
			
			snd_I\AlarmSFX[i] = LoadSound_Strict("SFX\Alarm\Alarm" + (i + 1) + ".ogg")
		EndIf
		If i < 4
			snd_I\DecaySFX[i] = LoadSound_Strict("SFX\SCP\106\Decay" + i + ".ogg")
			
			snd_I\DripSFX[i] = LoadSound_Strict("SFX\Character\D9341\BloodDrip" + i + ".ogg")
			
			snd_I\PickSFX[i] = LoadSound_Strict("SFX\Interact\PickItem" + i + ".ogg")
			
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
	
	snd_I\BurstSFX = LoadSound_Strict("SFX\Room\TunnelBurst.ogg")
	
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
	
	snd_I\HeartBeatSFX = LoadSound_Strict("SFX\Character\D9341\HeartBeat.ogg")
	
	snd_I\MissSFX = LoadSound_Strict("SFX\Character\Miss.ogg")
	
	snd_I\BreathGasRelaxedSFX = LoadSound_Strict("SFX\Character\D9341\BreathGasRelaxed.ogg")
	
	snd_I\CrouchSFX = LoadSound_Strict("SFX\Character\D9341\Crouch.ogg")
	
	snd_I\SCRAMBLESFX = LoadSound_Strict("SFX\Interact\SCRAMBLE.ogg")
	
	snd_I\BlindsSFX = LoadSound_Strict("SFX\Room\Blinds.ogg")
	
	snd_I\SparkShortSFX = LoadSound_Strict("SFX\Room\SparkShort.ogg")
	
	snd_I\WatchesSFX = LoadSound_Strict("SFX\Room\Watches.ogg")
	
	I_1123\Sound = LoadSound_Strict("SFX\SCP\1123\Ambient.ogg")
	
	snd_I\FireSFX = LoadSound_Strict("SFX\Room\Fire.ogg")
	
	snd_I\BuzzingSFX = LoadSound_Strict("SFX\Room\Buzzing.ogg")
End Function

Function RemoveSoundInstances%()
	Local i%
	
	For i = 0 To 13
		If i < 2
			RadioSFX(0, i) = 0
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
			OpenDoorSFX(FENCE_DOOR, i) = 0
			snd_I\BigDoorErrorSFX[i] = 0
			snd_I\SCP173SFX[i] = 0
			snd_I\NeckSnapSFX[i] = 0
			CoughSFX(0, i) = 0
			CoughSFX(1, i) = 0
			snd_I\LightSFX[i] = 0
			snd_I\AlarmSFX[i] = 0
		EndIf
		If i < 4
			snd_I\DecaySFX[i] = 0
			snd_I\PickSFX[i] = 0
			snd_I\DripSFX[i] = 0
			snd_I\OpenDecayDoorSFX[i] = 0
			snd_I\CloseDecayDoorSFX[i] = 0
		EndIf
		If i < 5
			BreathSFX(0, i) = 0
			BreathSFX(1, i) = 0
		EndIf
		If i < 6
			AmbientSFXAmount[i] = 0
			snd_I\RustleSFX[i] = 0
		EndIf
		If i < 7 Then snd_I\IntroSFX[i] = 0
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
			snd_I\SCP106SFX[i] = 0
		EndIf
		If i < 9
			RadioSFX(1, i) = 0
		EndIf
		If i < 11
			NPCSound[i] = 0
		EndIf
		If i < 13
			snd_I\Step2SFX[i] = 0
			snd_I\RoomAmbience[i] = 0
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
	
	snd_I\BurstSFX = 0
	
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
	
	snd_I\MissSFX = 0
	
	snd_I\BreathGasRelaxedSFX = 0
	
	snd_I\CrouchSFX = 0
	
	snd_I\SCRAMBLESFX = 0
	
	snd_I\FemurBreakerSFX = 0
	
	snd_I\VomitSFX = 0
	
	snd_I\BlindsSFX = 0
	
	snd_I\SparkShortSFX = 0
	
	snd_I\SinkHoleSFX = 0
	
	snd_I\WatchesSFX = 0
	
	snd_I\FireSFX = 0
	
	snd_I\BuzzingSFX = 0
	
	snd_I\AirlockSFX = 0
	
	Delete(snd_I) : snd_I = Null
End Function

Function LoadEvents%()
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	If opt\IntroEnabled Then CreateEvent(e_cont1_173_intro, r_cont1_173_intro, 0)
	CreateEvent(e_cont1_173, r_cont1_173, 0)
	
	CreateEvent(e_dimension_106, r_dimension_106, 0)
	
	; ~ There's a 7% chance that SCP-106 appears in the rooms named r_room2_5_hcz
	CreateEvent(e_room2_5_hcz_106, r_room2_5_hcz, 0, 0.07 + (0.1 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The chance for SCP-173 appearing in the first r_room2c_gw_lcz is about 66%
	; ~ There's a 30% chance that it appears in the later r_room2c_gw_lcz
	If Rand(3) < 3 Then CreateEvent(e_173_spawn, r_room2c_gw_lcz, 0)
	CreateEvent(e_173_spawn, r_room2c_gw_lcz, 1, 0.3 + (0.5 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_trick, r_room2_lcz, 0, 0.15)
	CreateEvent(e_trick, r_room2_3_lcz, 0, 0.15)
	
	CreateEvent(e_room2_ez_035, r_room2_ez, 0)
	
	CreateEvent(e_trick_item, r_room2_lcz, 0, 0.4)
	CreateEvent(e_trick_item, r_room2c_lcz, 0, 0.15)
	CreateEvent(e_trick_item, r_room2c_2_ez, 0, 0.15)
	CreateEvent(e_trick_item, r_room4_2_ez, 1, 0.15)
	CreateEvent(e_trick_item, r_room2_4_ez, 0, 0.2)
	CreateEvent(e_106_victim_wall, r_room2_4_ez, 1)
	
	CreateEvent(e_1048_a, r_room2_lcz, 1, 0.7)
	CreateEvent(e_1048_a, r_room2_3_lcz, 1, 0.3 + (0.3 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent(e_1048_a, r_room2_5_lcz, 0, 0.2 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent(e_brownout, r_room2c_2_lcz, 0, 1.0)
	CreateEvent(e_brownout, r_room2_7_lcz, 0, 1.0)
	
	CreateEvent(e_room2_storage, r_room2_storage, 0)
	
	CreateEvent(e_room1_dead_end_lcz_106, r_room1_dead_end_lcz, Rand(0, 1))
	CreateEvent(e_room1_dead_end_ez_guard, r_room1_dead_end_ez, Rand(0, 1))
	
	CreateEvent(e_room2_scientists_2, r_room2_scientists_2, 0)
	
	CreateEvent(e_room2_2_lcz_fan, r_room2_2_lcz, 0, 1.0)
	
	CreateEvent(e_room2_elevator, r_room2_elevator, Rand(0, 1))
	
	CreateEvent(e_room3_storage, r_room3_storage, 0)
	
	CreateEvent(e_room2_6_hcz_smoke, r_room2_6_hcz, 0, 0.2)
	CreateEvent(e_room2_6_hcz_173, r_room2_6_hcz, 0, 0.3 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ SCP-173 appears in half of the r_room2_6_lcz-rooms
	CreateEvent(e_173_spawn, r_room2_6_lcz, 0, 0.5 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The anomalous duck in r_room2_2_ez-rooms
	CreateEvent(e_room2_2_ez_duck, r_room2_2_ez, 0, 0.7)
	
	CreateEvent(e_room2_closets, r_room2_closets, 0)
	
	CreateEvent(e_room1_storage, r_room1_storage, 0)
	
	CreateEvent(e_room2_cafeteria, r_room2_cafeteria, 0)
	
	CreateEvent(e_room3_hcz_duck, r_room3_hcz, 0)
	CreateEvent(e_room3_hcz_1048,r_room3_hcz, 1)
	
	CreateEvent(e_room2_servers_hcz, r_room2_servers_hcz, 0)
	
	CreateEvent(e_173_spawn, r_room3_2_ez, 0, 0.8)
	CreateEvent(e_room3_2_ez_duck, r_room3_2_ez, 1)
	CreateEvent(e_173_spawn, r_room3_3_ez, 0)
	
	CreateEvent(e_173_spawn, r_room2_7_ez, 0, 0.6)
	
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
	
	CreateEvent(e_door_closing, r_room3_2_hcz, 0, 0.1)
	
	CreateEvent(e_106_victim, r_room3_2_lcz, Rand(2))
	CreateEvent(e_106_sinkhole, r_room4_lcz, Rand(2))
	
	CreateEvent(e_brownout, r_room3_lcz, 0, 1.0)
	
	CreateEvent(e_cont1_079, r_cont1_079, 0)
	
	CreateEvent(e_cont2_049, r_cont2_049, 0)
	
	CreateEvent(e_cont2_012, r_cont2_012, 0)
	
	CreateEvent(e_cont1_035, r_cont1_035, 0)
	
	CreateEvent(e_cont2_008, r_cont2_008, 0)
	
	CreateEvent(e_cont1_106, r_cont1_106, 0)
	
	CreateEvent(e_cont3_372, r_cont3_372, 0)
	
	CreateEvent(e_106_sinkhole, r_cont3_513, 0)
	
	CreateEvent(e_cont1_914, r_cont1_914, 0)
	
	CreateEvent(e_toilets_789_j, r_room2_6_ez, 0)
	CreateEvent(e_room2_6_ez_guard, r_room2_7_hcz, 1)
	
	CreateEvent(e_room2_2_hcz_106, r_room2_2_hcz, Rand(0, 3))
	
	CreateEvent(e_173_spawn, r_room2_4_hcz, 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
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
	
	CreateEvent(e_cont3_009, r_cont3_009, 0)
	
	CreateEvent(e_cont3_966, r_cont3_966, 0)
	
	CreateEvent(e_cont2_1123, r_cont2_1123, 0)
	
	CreateEvent(e_tesla, r_room2_tesla_lcz, 0, 1.0)
	CreateEvent(e_tesla, r_room2_tesla_hcz, 0, 1.0)
	CreateEvent(e_broken_tesla, r_room2_tesla_2_hcz, 0, 1.0)
	CreateEvent(e_tesla, r_room2_tesla_ez, 0, 1.0)
	
	CreateEvent(e_room4_2_hcz_d, r_room4_2_hcz, 0)
	
	CreateEvent(e_room2_gw_2, r_room2_gw_2, 0)
	CreateEvent(e_gateway, r_room2_gw, 0, 1.0)
	CreateEvent(e_gateway, r_room3_gw, 0, 1.0)
	CreateEvent(e_gateway, r_room4_gw, 0, 1.0)
	
	CreateEvent(e_dimension_1499, r_dimension_1499, 0)
	
	CreateEvent(e_cont2c_066_1162_arc, r_cont2c_066_1162_arc, 0)
	
	CreateEvent(e_cont2_500_1499, r_cont2_500_1499, 0)
	
	CreateEvent(e_room2_sl, r_room2_sl, 0)
	
	CreateEvent(e_room2_medibay, r_room2_medibay, 0)
	
	CreateEvent(e_room2_office, r_room2_office, 0)
	
	CreateEvent(e_room2_office_3, r_room2_office_3, 0)
	
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
	
	CreateEvent(e_173_spawn, r_room2_4_lcz, 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
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
		Local DoorX# = EntityX(d\FrameOBJ, True)
		Local DoorY# = EntityY(d\FrameOBJ, True)
		Local DoorZ# = EntityZ(d\FrameOBJ, True)
		
		If (Not d\DisableWaypoint)
			If d\DoorType = BIG_DOOR
				d\DoorColl = CopyEntity(d_I\BigDoorColl)
			Else
				d\DoorColl = CopyEntity(d_I\DoorColl)
			EndIf
			ScaleEntity(d\DoorColl, RoomScale, RoomScale, RoomScale)
			PositionEntity(d\DoorColl, DoorX, DoorY, DoorZ, True)
			RotateEntity(d\DoorColl, 0.0, EntityYaw(d\FrameOBJ, True), 0.0)
			EntityPickMode(d\DoorColl, 2)
		EndIf
		
		HideEntity(d\OBJ)
		If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
		HideEntity(d\FrameOBJ)
		
		If d\room = Null
			ClosestRoom.Rooms = Null
			Dist = 30.0
			For r.Rooms = Each Rooms
				x = Abs(EntityX(r\OBJ, True) - DoorX)
				If x < 20.0
					z = Abs(EntityZ(r\OBJ, True) - DoorZ)
					If z < 20.0
						Dist2 = PowTwo(x) + PowTwo(z)
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
		If (Not d\DisableWaypoint) And d\DoorType <> WOODEN_DOOR Then CreateWaypoint(d, ClosestRoom, DoorX, DoorY + 0.18, DoorZ)
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
		If Iter = 5
			RenderLoading(LoadingStart + Floor((35.0 / Amount) * Number), GetLocalString("loading", "waypoints"))
			Iter = 0
		EndIf
		
		w2.WayPoints = After(w)
		
		While w2 <> Null
			If w\room = w2\room Lor w\door <> Null Lor w2\door <> Null
				Dist = EntityDistance(w\OBJ, w2\OBJ)
				
				Local CanCreateWayPoint% = False
				
				If w\room\MaxWayPointY = 0.0 Lor w2\room\MaxWayPointY = 0.0
					CanCreateWayPoint = True
				ElseIf IsEqual(EntityY(w\OBJ), EntityY(w2\OBJ), w\room\MaxWayPointY)
					CanCreateWayPoint = True
				EndIf
				
				If Dist < 7.0
					If CanCreateWayPoint
						If EntityVisible(w\OBJ, w2\OBJ)
							For i = 0 To MaxConnectedWaypoints - 1
								If w\connected[i] = Null
									w\connected[i] = w2.WayPoints
									w\Dist[i] = Dist
									Exit
								EndIf
							Next
							
							For n = 0 To MaxConnectedWaypoints - 1
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
		If (Not d\DisableWaypoint) Then FreeEntity(d\DoorColl) : d\DoorColl = 0
		ShowEntity(d\OBJ)
		If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
		ShowEntity(d\FrameOBJ)
	Next
	; ~ We don't need this anymore
	FreeEntity(d_I\DoorColl) : d_I\DoorColl = 0
	FreeEntity(d_I\BigDoorColl) : d_I\BigDoorColl = 0
	
	For w.WayPoints = Each WayPoints
		EntityRadius(w\OBJ, 0.0)
		EntityPickMode(w\OBJ, 0, False)
		
		If opt\DebugMode
			For i = 0 To MaxConnectedWaypoints - 1
				If w\connected[i] <> Null
					Local tLine% = CreateLine(EntityX(w\OBJ, True), EntityY(w\OBJ, True), EntityZ(w\OBJ, True), EntityX(w\connected[i]\OBJ, True), EntityY(w\connected[i]\OBJ, True), EntityZ(w\connected[i]\OBJ, True))
					
					EntityFX(tLine, 1)
					EntityColor(tLine, 0.0, 200.0, 0.0)
					EntityParent(tLine, w\OBJ)
				EndIf
			Next
		EndIf
	Next
End Function

; ~ Textures Constants
;[Block]
Const MaxOverlayTextureIDAmount% = 4
Const MaxOverlayIDAmount% = 13
Const MaxIconIDAmount% = 13
Const MaxImageIDAmount% = 7
;[End Block]

Type Textures
	Field IconID%[MaxIconIDAmount]
	Field ImageID%[MaxImageIDAmount]
	Field OverlayTextureID%[MaxOverlayTextureIDAmount]
	Field OverlayID%[MaxOverlayIDAmount]
	Field NAVRenderTarget%
End Type

; ~ Overlay ID Constants
;[Block]
Const OVERLAY_VIGNETTE% = 0
Const OVERLAY_GAS_MASK% = 1
Const OVERLAY_HAZMAT_SUIT% = 2
Const OVERLAY_SCP_008% = 3
Const OVERLAY_NVG% = 4
Const OVERLAY_DARK% = 5
Const OVERLAY_LIGHT_FLASH% = 6
Const OVERLAY_SCP_409% = 7
Const OVERLAY_HELMET% = 8
Const OVERLAY_GAS_MASK_FOG% = 9
Const OVERLAY_SCP_009% = 10
Const OVERLAY_BURN% = 11
Const OVERLAY_BLOODY% = 12
;[End Block]

Global OverlayBurnAlpha#

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
Global InFacility%, IsInsideForest%
Global PlayerFallingPickDistance#

Global ShouldEntitiesFall%
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
	Field Crouch%, CrouchState#
	Field SndVolume#
	Field SelectedEnding%, EndingScreen%, EndingTimer#
	Field CreditsScreen%, CreditsTimer#
	Field BlurVolume#, BlurTimer#
	Field LightBlink#, LightFlash#
	Field CurrCameraZoom#
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
	Field PickTimer#, LastPicked%
	Field PickedCooler.Props
End Type

Global me.Player

Function LoadData%()
	Local TempStr$
	
	SubFile = JsonParseFromFile(SubtitlesFile)
	LocalSubFile = JsonParseFromFile(lang\LanguagePath + SubtitlesFile)
	SubColors = JsonGetValue(SubFile, "colors")
	LocalSubColors = JsonGetValue(LocalSubFile, "colors")
	SubtitlesInit = True
	
	If SCP1499ChunksDoc <> 0 Then JsonFreeDocument(SCP1499ChunksDoc) : SCP1499ChunksDoc = 0
	SCP1499ChunksDoc = JsonParseFromFile(SCP1499ChunksFile)
	SCP1499Chunks = JsonGetArray(SCP1499ChunksDoc)
	
	SubjectName = GetLocalString("misc", "subject")
	PlayerFallingPickDistance = 10.0
	
	CameraZoomValue = Tan((2.0 * ATan(Tan((opt\FOV) / 2.0) * (GraphicWidthFloat / GraphicHeightFloat))) / 2.0)
	
	Collisions(HIT_PLAYER, HIT_MAP, 2, 2)
	Collisions(HIT_PLAYER, HIT_PLAYER, 1, 3)
	Collisions(HIT_ITEM, HIT_MAP, 2, 2)
	Collisions(HIT_APACHE, HIT_APACHE, 1, 2)
	Collisions(HIT_DEAD, HIT_MAP, 2, 2)
	
	LoadRoomTemplates("Data\rooms.ini")
	
	Select SelectedDifficulty\OtherFactors
		Case DIFFICULTY_FACTOR_EASY
			;[Block]
			DifficultyDMGMult = 1.0
			;[End Block]
		Case DIFFICULTY_FACTOR_NORMAL
			;[Block]
			DifficultyDMGMult = 1.15
			;[End Block]
		Case DIFFICULTY_FACTOR_HARD
			;[Block]
			DifficultyDMGMult = 1.3
			;[End Block]
		Case DIFFICULTY_FACTOR_EXTREME
			;[Block]
			DifficultyDMGMult = 1.45
			;[End Block]
	End Select
	
	ShouldEntitiesFall = True
	CoffinDistance = 100.0
	
	QuickLoadPercent = -1
	
	EscapeSecondsTimer = 70.0
	
	opttimer.OptimizationTimer = New OptimizationTimer
	chs.Cheats = New Cheats
	me.Player = New Player
	pm.PlayerModel = New PlayerModel
	wi.WearableItems = New WearableItems
	fog.FogAmbient = New FogAmbient
	
	I_009.SCP009 = New SCP009
	I_005.SCP005 = New SCP005
	I_008.SCP008 = New SCP008
	I_035.SCP035 = New SCP035
	I_268.SCP268 = New SCP268
	I_294.SCP294 = New SCP294
	Init294Drinks()
	I_409.SCP409 = New SCP409
	I_427.SCP427 = New SCP427
	I_714.SCP714 = New SCP714
	I_1025.SCP1025 = New SCP1025
	I_1499.SCP1499 = New SCP1499
	I_966.SCP966 = New SCP966
	I_1048A.SCP1048A = New SCP1048A
	I_1123.SCP1123 = New SCP1123
	I_2022.SCP2022 = New SCP2022
	
	as.AutoSave = New AutoSave
	
	msg.Messages = New Messages
	
	I_Zone.MapZones = New MapZones
	
	bk.BrokenDoor = New BrokenDoor
	
	InitAchievements()
	LoadAchievementsFile()
	igm.InGameMenu = New InGameMenu
	
	t.Textures = New Textures
End Function

Global Camera%

Const MaxBodyTextures% = 6
; ~ Player's body texture constants
;[Block]
Const PLAYER_BODY_NORMAL_TEX% = 0
Const PLAYER_BODY_HAZMAT_SUIT_TEX% = 1
Const PLAYER_BODY_FIRE_SUIT_TEX% = 2
Const PLAYER_BODY_HAZMAT_SUIT_HEAVY_TEX% = 3
Const PLAYER_BODY_VEST_TEX% = 4
Const PLAYER_BODY_PRISONER_TEX% = 5
;[End Block]

; ~ Player body animation constants
;[Block]
Const MaxPlayerAnimations% = 21

Const PLAYER_ANIM_IDLE% = 1
Const PLAYER_ANIM_CROUCH_IDLE% = 2

Const PLAYER_ANIM_WALK% = 3
Const PLAYER_ANIM_RUN% = 4
Const PLAYER_ANIM_CROUCH_WALK% = 5

Const PLAYER_ANIM_WALK_STRAFE_RIGHT% = 6
Const PLAYER_ANIM_WALK_STRAFE_LEFT% = 7

Const PLAYER_ANIM_RUN_STRAFE_RIGHT% = 8
Const PLAYER_ANIM_RUN_STRAFE_LEFT% = 9

Const PLAYER_ANIM_CROUCH_WALK_STRAFE_RIGHT% = 10
Const PLAYER_ANIM_CROUCH_WALK_STRAFE_LEFT% = 11

Const PLAYER_ANIM_NOCLIP% = 12

Const PLAYER_ANIM_LEFT_INTERACT% = 13
Const PLAYER_ANIM_CROUCH_LEFT_INTERACT% = 14
Const PLAYER_ANIM_LEFT_PICK_UP% = 15
Const PLAYER_ANIM_CROUCH_LEFT_PICK_UP% = 16
Const PLAYER_ANIM_RIGHT_INTERACT% = 17
Const PLAYER_ANIM_CROUCH_RIGHT_INTERACT% = 18
Const PLAYER_ANIM_RIGHT_PICK_UP% = 19
Const PLAYER_ANIM_CROUCH_RIGHT_PICK_UP% = 20
;[End Block]

Type PlayerModel
	Field Pivot%, OBJ%
	Field AnimationSpeed#[MaxPlayerAnimations]
	Field AnimationTransition#[MaxPlayerAnimations]
	Field AnimationMode%[MaxPlayerAnimations]
	Field AnimID%
	Field BodyTextureName$[MaxBodyTextures]
End Type

Global pm.PlayerModel

Function LoadEntities%()
	CatchErrors("LoadEntities()")
	
	Local i%, j%, k%, Tex%
	Local Name$, Test%, File$, Scale#
	
	ApplyGraphicOptions()
	
	DeInitMainMenuAssets()
	
	RenderLoading(0, GetLocalString("loading", "data"))
	
	LoadData()
	
	LoadMissingTexture() ; ~ Create this texture before loading a mesh or texture
	
	InitSubtitlesAssets()
	
	RenderLoading(3, GetLocalString("loading", "player"))
	
	SoundEmitter = CreatePivot()
	
	me\Collider = CreatePivot()
	EntityRadius(me\Collider, 0.15, 0.3)
	EntityPickMode(me\Collider, 1)
	EntityType(me\Collider, HIT_PLAYER)
	
	me\Head = CreatePivot()
	EntityRadius(me\Head, 0.15)
	EntityType(me\Head, HIT_PLAYER)
	
	Camera = CreateCamera()
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	CameraFogMode(Camera, 1)
	CameraFogRange(Camera, 0.1, fog\FarDist)
	CameraFogColor(Camera, 30.0, 30.0, 30.0)
	CameraRange(Camera, 0.01, fog\FarDist * CameraRangeScale)
	CameraClsColor(Camera, 80.0, 80.0, 80.0)
	CameraReverseZ(Camera, True)
	AmbientLight(80.0, 80.0, 80.0)
	SetShadowsBias(0.00044, 1.0)
	fog\HideDistance = fog\FarDist * CameraRangeScale
	
	pm\Pivot = CreatePivot()
	pm\OBJ = LoadAnimMesh_Strict("GFX\NPCs\player_body.b3d", pm\Pivot)
	Scale = 0.51
	i = MeshWidth(pm\OBJ) : j = MeshHeight(pm\OBJ) : k = MeshDepth(pm\OBJ)
	ScaleEntity(pm\OBJ, Scale / i, Scale / i, Scale / i)
	MeshCullBox(pm\OBJ, -i, -j, -k, i * 2.0, j * 2.0, k * 2.0)
	EntityType(pm\OBJ, 0)
	HideEntity(pm\OBJ)
	SetDeferredEntity(pm\OBJ, True)
	
	Local StartFrame#, EndFrame#
	
	For i = PLAYER_ANIM_IDLE To PLAYER_ANIM_CROUCH_RIGHT_PICK_UP
		Select i
			Case PLAYER_ANIM_IDLE
				;[Block]
				StartFrame = 1.0
				EndFrame = 19.0
				pm\AnimationSpeed[i] = 0.1
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_CROUCH_IDLE
				;[Block]
				StartFrame = 157.0
				EndFrame = 181.0
				pm\AnimationSpeed[i] = 0.05
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_WALK
				;[Block]
				StartFrame = 20.0
				EndFrame = 44.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_RUN
				;[Block]
				StartFrame = 95.0
				EndFrame = 112.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_CROUCH_WALK
				;[Block]
				StartFrame = 189.0
				EndFrame = 213.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_WALK_STRAFE_RIGHT
				;[Block]
				StartFrame = 45.0
				EndFrame = 69.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_WALK_STRAFE_LEFT
				;[Block]
				StartFrame = 70.0
				EndFrame = 94.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_RUN_STRAFE_RIGHT
				;[Block]
				StartFrame = 113.0
				EndFrame = 130.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_RUN_STRAFE_LEFT
				;[Block]
				StartFrame = 131.0
				EndFrame = 148.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_CROUCH_WALK_STRAFE_RIGHT
				;[Block]
				StartFrame = 214.0
				EndFrame = 238.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_CROUCH_WALK_STRAFE_LEFT
				;[Block]
				StartFrame = 239.0
				EndFrame = 263.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_NOCLIP
				;[Block]
				StartFrame = 264.0
				EndFrame = 284.0
				pm\AnimationSpeed[i] = 0.245
				pm\AnimationTransition[i] = 15.0
				pm\AnimationMode[i] = 1
				;[End Block]
			Case PLAYER_ANIM_LEFT_INTERACT
				;[Block]
				StartFrame = 285.0
				EndFrame = 325.0
				pm\AnimationSpeed[i] = 1.5
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_CROUCH_LEFT_INTERACT
				;[Block]
				StartFrame = 367.0
				EndFrame = 408.0
				pm\AnimationSpeed[i] = 1.5
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_LEFT_PICK_UP
				;[Block]
				StartFrame = 326.0
				EndFrame = 366.0
				pm\AnimationSpeed[i] = 1.2
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_CROUCH_LEFT_PICK_UP
				;[Block]
				StartFrame = 409.0
				EndFrame = 448.0
				pm\AnimationSpeed[i] = 1.2
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_RIGHT_INTERACT
				;[Block]
				StartFrame = 449.0
				EndFrame = 489.0
				pm\AnimationSpeed[i] = 1.5
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_CROUCH_RIGHT_INTERACT
				;[Block]
				StartFrame = 531.0
				EndFrame = 572.0
				pm\AnimationSpeed[i] = 1.5
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_RIGHT_PICK_UP
				;[Block]
				StartFrame = 490.0
				EndFrame = 530.0
				pm\AnimationSpeed[i] = 1.2
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
			Case PLAYER_ANIM_CROUCH_RIGHT_PICK_UP
				;[Block]
				StartFrame = 573.0
				EndFrame = 612.0
				pm\AnimationSpeed[i] = 1.2
				pm\AnimationTransition[i] = 5.0
				pm\AnimationMode[i] = 3
				;[End Block]
		End Select
		ExtractAnimSeq(pm\OBJ, StartFrame, EndFrame)
	Next
	SetPlayerModelAnimation(PLAYER_ANIM_IDLE)
	pm\BodyTextureName[PLAYER_BODY_NORMAL_TEX] = ""
	pm\BodyTextureName[PLAYER_BODY_HAZMAT_SUIT_TEX] = "_hazmat_suit"
	pm\BodyTextureName[PLAYER_BODY_FIRE_SUIT_TEX] = "_fire_suit"
	pm\BodyTextureName[PLAYER_BODY_HAZMAT_SUIT_HEAVY_TEX] = "_hazmat_suit_heavy"
	pm\BodyTextureName[PLAYER_BODY_VEST_TEX] = "_vest"
	pm\BodyTextureName[PLAYER_BODY_PRISONER_TEX] = "_flashback"
	
	ParticleCam = Camera
	ParticlePiv = CreatePivot()
	
	RenderLoading(5, GetLocalString("loading", "icons"))
	
	t\IconID[0] = ResizeImageEx(LoadImage_Strict("GFX\HUD\walk_icon.png"), MenuScale, MenuScale)
	t\IconID[1] = ResizeImageEx(LoadImage_Strict("GFX\HUD\sprint_icon.png"), MenuScale, MenuScale)
	t\IconID[2] = ResizeImageEx(LoadImage_Strict("GFX\HUD\crouch_icon.png"), MenuScale, MenuScale)
	For i = 3 To 4
		t\IconID[i] = LoadImage_Strict("GFX\HUD\blink_icon(" + (i - 2) + ").png")
		t\IconID[i] = ResizeImageEx(t\IconID[i], MenuScale, MenuScale)
	Next
	For i = 5 To 6
		t\IconID[i] = ResizeImageEx(LoadImage_Strict("GFX\HUD\hand_symbol(" + (i - 4) + ").png"), MenuScale, MenuScale)
	Next
	t\IconID[7] = ResizeImageEx(LoadImage_Strict("GFX\HUD\shield_icon.png"), MenuScale, MenuScale)
	
	t\IconID[8] = ResizeImageEx(LoadImage_Strict("GFX\HUD\scp_268_icon.png"), MenuScale, MenuScale)
	
	t\IconID[9] = ResizeImageEx(LoadImage_Strict("GFX\Menu\QuickLoading.png"), MenuScale, MenuScale)
	
	For i = 0 To 3
		t\IconID[i + 10] = ResizeImageEx(LoadImage_Strict("GFX\HUD\arrow_symbol.png"), MenuScale, MenuScale)
		RotateImage(t\IconID[i + 10], i * 90.0)
		HandleImage(t\IconID[i + 10], 0, 0)
	Next
	
	t\ImageID[0] = ResizeImageEx(LoadImage_Strict("GFX\Menu\pause_menu.png"), MenuScale, MenuScale)
	
	t\ImageID[1] = ResizeImageEx(LoadImage_Strict("GFX\HUD\blink_meter(2).png"), MenuScale, MenuScale)
	
	For i = 2 To 3
		t\ImageID[i] = ResizeImageEx(LoadImage_Strict("GFX\HUD\stamina_meter(" + (i - 1) + ").png"), MenuScale, MenuScale)
	Next
	
	t\ImageID[4] = ResizeImageEx(LoadImage_Strict("GFX\HUD\keypad_HUD.png"), MenuScale, MenuScale)
	
	t\ImageID[5] = ResizeImageEx(LoadImage_Strict("GFX\Overlays\scp_294_overlay.png"), MenuScale, MenuScale)
	
	t\ImageID[6] = ScaleImageEx(LoadAnimImage_Strict("GFX\HUD\NVG_batteries.png", 64, 64, 0, 3), MenuScale, MenuScale)
	
	t\NAVRenderTarget = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 1 + 1024)
	
	RenderLoading(10, GetLocalString("loading", "textures"))
	
	AmbientLightRoomTex = CreateTextureUsingCacheSystem(1, 1, 1 + 256)
	TextureBlend(AmbientLightRoomTex, 2)
	
	CreateBlurImage()
	
	; ~ Overlays
	Local OverlayScale# = 0.001 + (GraphicHeightFloat / GraphicWidthFloat)
	
	t\OverlayTextureID[0] = LoadTexture_Strict("GFX\Overlays\vignette_overlay.png", 1, DeleteAllTextures)
	t\OverlayID[OVERLAY_VIGNETTE] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_VIGNETTE], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_VIGNETTE], t\OverlayTextureID[0])
	EntityBlend(t\OverlayID[OVERLAY_VIGNETTE], 2)
	EntityOrder(t\OverlayID[OVERLAY_VIGNETTE], -1000)
	MoveEntity(t\OverlayID[OVERLAY_VIGNETTE], 0.0, 0.0, 1.0)
	
	Tex = LoadTexture_Strict("GFX\Overlays\gas_mask_overlay.png")
	t\OverlayID[OVERLAY_GAS_MASK] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_GAS_MASK], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_GAS_MASK], Tex)
	EntityBlend(t\OverlayID[OVERLAY_GAS_MASK], 2)
	EntityOrder(t\OverlayID[OVERLAY_GAS_MASK], -1003)
	MoveEntity(t\OverlayID[OVERLAY_GAS_MASK], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	HideEntity(t\OverlayID[OVERLAY_GAS_MASK])
	
	Tex = LoadTexture_Strict("GFX\Overlays\hazmat_suit_overlay.png")
	t\OverlayID[OVERLAY_HAZMAT_SUIT] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_HAZMAT_SUIT], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_HAZMAT_SUIT], Tex)
	EntityBlend(t\OverlayID[OVERLAY_HAZMAT_SUIT], 2)
	EntityOrder(t\OverlayID[OVERLAY_HAZMAT_SUIT], -1003)
	MoveEntity(t\OverlayID[OVERLAY_HAZMAT_SUIT], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	HideEntity(t\OverlayID[OVERLAY_HAZMAT_SUIT])
	
	Tex = LoadTexture_Strict("GFX\Overlays\scp_008_overlay.png")
	t\OverlayID[OVERLAY_SCP_008] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_SCP_008], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_SCP_008], Tex)
	EntityBlend(t\OverlayID[OVERLAY_SCP_008], 3)
	EntityOrder(t\OverlayID[OVERLAY_SCP_008], -1003)
	EntityAlpha(t\OverlayID[OVERLAY_SCP_008], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_SCP_008], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	t\OverlayTextureID[1] = LoadTexture_Strict("GFX\Overlays\night_vision_goggles_overlay.png", 1, DeleteAllTextures)
	t\OverlayID[OVERLAY_NVG] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_NVG], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_NVG], t\OverlayTextureID[1])
	EntityBlend(t\OverlayID[OVERLAY_NVG], 2)
	EntityOrder(t\OverlayID[OVERLAY_NVG], -1003)
	MoveEntity(t\OverlayID[OVERLAY_NVG], 0.0, 0.0, 1.0)
	HideEntity(t\OverlayID[OVERLAY_NVG])
	
	t\OverlayTextureID[2] = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO_HALF, SMALLEST_POWER_TWO_HALF, 1 + 2)
	SetBuffer(TextureBuffer(t\OverlayTextureID[2]))
	ClsColor(0, 0, 0)
	Cls()
	t\OverlayID[OVERLAY_DARK] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_DARK], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_DARK], t\OverlayTextureID[2])
	EntityBlend(t\OverlayID[OVERLAY_DARK], 1)
	EntityOrder(t\OverlayID[OVERLAY_DARK], -1002)
	EntityAlpha(t\OverlayID[OVERLAY_DARK], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_DARK], 0.0, 0.0, 1.0)
	
	Tex = CreateTextureUsingCacheSystem(SMALLEST_POWER_TWO_HALF, SMALLEST_POWER_TWO_HALF, 1 + 2, 1, DeleteMapTextures)
	SetBuffer(TextureBuffer(Tex))
	ClsColor(255, 255, 255)
	Cls()
	ClsColor(0, 0, 0)
	SetBuffer(BackBuffer())
	t\OverlayID[OVERLAY_LIGHT_FLASH] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_LIGHT_FLASH], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_LIGHT_FLASH], Tex)
	EntityBlend(t\OverlayID[OVERLAY_LIGHT_FLASH], 1)
	EntityOrder(t\OverlayID[OVERLAY_LIGHT_FLASH], -1002)
	EntityAlpha(t\OverlayID[OVERLAY_LIGHT_FLASH], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_LIGHT_FLASH], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\scp_409_overlay.png")
	t\OverlayID[OVERLAY_SCP_409] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_SCP_409], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_SCP_409], Tex)
	EntityBlend(t\OverlayID[OVERLAY_SCP_409], 3)
	EntityOrder(t\OverlayID[OVERLAY_SCP_409], -1003)
	EntityAlpha(t\OverlayID[OVERLAY_SCP_409], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_SCP_409], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0	
	
	Tex = LoadTexture_Strict("GFX\Overlays\helmet_overlay.png")
	t\OverlayID[OVERLAY_HELMET] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_HELMET], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_HELMET], Tex)
	EntityBlend(t\OverlayID[OVERLAY_HELMET], 2)
	EntityOrder(t\OverlayID[OVERLAY_HELMET], -1003)
	MoveEntity(t\OverlayID[OVERLAY_HELMET], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	HideEntity(t\OverlayID[OVERLAY_HELMET])
	
	Tex = LoadTexture_Strict("GFX\Overlays\fog_gas_mask_overlay.png")
	t\OverlayID[OVERLAY_GAS_MASK_FOG] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_GAS_MASK_FOG], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_GAS_MASK_FOG], Tex)
	EntityBlend(t\OverlayID[OVERLAY_GAS_MASK_FOG], 3)
	EntityOrder(t\OverlayID[OVERLAY_GAS_MASK_FOG], -1002)
	EntityAlpha(t\OverlayID[OVERLAY_GAS_MASK_FOG], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_GAS_MASK_FOG], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Map\Textures\scp_009.png")
	t\OverlayID[OVERLAY_SCP_009] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_SCP_009], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_SCP_009], Tex)
	EntityBlend(t\OverlayID[OVERLAY_SCP_009], 3)
	EntityOrder(t\OverlayID[OVERLAY_SCP_009], -1001)
	EntityFX(t\OverlayID[OVERLAY_SCP_009], 1)
	EntityAlpha(t\OverlayID[OVERLAY_SCP_009], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_SCP_009], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	Tex = LoadTexture_Strict("GFX\Overlays\fire_overlay.png")
	t\OverlayID[OVERLAY_BURN] = CreateSprite(ArkBlurCam)
	ScaleSprite(t\OverlayID[OVERLAY_BURN], 1.001, OverlayScale)
	EntityTexture(t\OverlayID[OVERLAY_BURN], Tex)
	EntityBlend(t\OverlayID[OVERLAY_BURN], 3)
	EntityOrder(t\OverlayID[OVERLAY_BURN], -1003)
	EntityAlpha(t\OverlayID[OVERLAY_BURN], 0.0)
	MoveEntity(t\OverlayID[OVERLAY_BURN], 0.0, 0.0, 1.0)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	t\OverlayTextureID[3] = LoadTexture_Strict("GFX\Overlays\tesla_overlay.png", 1 + 2, DeleteAllTextures)
	
	wi\SCRAMBLESpriteScreen = CreateSprite()
	PositionEntity(wi\SCRAMBLESpriteScreen, 0.0, -500.0, 0.0)
	ScaleSprite(wi\SCRAMBLESpriteScreen, 0.07, 0.08)
	EntityOrder(wi\SCRAMBLESpriteScreen, -5)
	EntityTexture(wi\SCRAMBLESpriteScreen, t\OverlayTextureID[2])
	HideEntity(wi\SCRAMBLESpriteScreen)
	
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
	If AchievementsArray <> 0 Then JsonFreeDocument(AchievementsArray) : AchievementsArray = 0
	If LocalAchievementsArray <> 0 Then JsonFreeDocument(LocalAchievementsArray) : LocalAchievementsArray = 0
	
	For i = 0 To MaxIconIDAmount - 1
		FreeImage(t\IconID[i]) : t\IconID[i] = 0
	Next
	For i = 0 To MaxImageIDAmount - 1
		FreeImage(t\ImageID[i]) : t\ImageID[i] = 0
	Next
	For i = 0 To MaxOverlayTextureIDAmount - 1
		t\OverlayTextureID[i] = 0
	Next
	FreeTexture(t\NAVRenderTarget) : t\NAVRenderTarget = 0
	For i = 0 To MaxOverlayIDAmount - 1
		If t\OverlayID[i] <> 0 Then FreeEntity(t\OverlayID[i]) : t\OverlayID[i] = 0
	Next
	Delete(t) : t = Null
End Function

Function Init294Drinks%()
	If I_294\DrinksDoc <> 0 Then JsonFreeDocument(I_294\DrinksDoc) : I_294\DrinksDoc = 0
	I_294\DrinksDoc = JsonParseFromFile(lang\LanguagePath + SCP294File)
	
	Local i%, j%
	
	If JsonIsArray(I_294\DrinksDoc) ; ~ Has localized SCP-294 drinks -> Use localized only
		I_294\Drinks = JsonGetArray(I_294\DrinksDoc)
	Else
		JsonFreeDocument(I_294\DrinksDoc) : I_294\DrinksDoc = 0
		I_294\DrinksDoc = JsonParseFromFile(SCP294File)
		I_294\Drinks = JsonGetArray(I_294\DrinksDoc)
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
	
	fog\FarDist = 6.0
	
	IsBlackOut = False : PrevIsBlackOut = False
	RemoteDoorOn = True
	
	InitOtherStuff()
	
	MaxItemAmount = SelectedDifficulty\InventorySlots
	Dim Inventory.Items(MaxItemAmount + 2) ; ~ Create two extra slots for polydactyly
	
	RenderLoading(50, GetLocalString("loading", "stuff"))
	
	me\BlinkTimer = -10.0 : me\BlinkEffect = 1.0 : me\Stamina = 100.0 : me\StaminaEffect = 1.0 : me\HeartBeatRate = 70.0
	
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	I_005\ChanceToSpawn = Rand(3)
	KEY2_SPAWNRATE = Rand(6)
	
	Remove714Timer = 500.0
	RemoveHazmatTimer = 500.0
	
	CODE_DR_MAYNARD = 0
	For i = 0 To 3
		CODE_DR_MAYNARD = CODE_DR_MAYNARD + (Rand(9) * (10 ^ i))
	Next
	If CODE_DR_MAYNARD = CODE_DR_HARP Lor CODE_DR_MAYNARD = CODE_CONT1_035 Lor CODE_DR_MAYNARD = CODE_DR_L Then CODE_DR_MAYNARD = CODE_DR_MAYNARD + 1
	
	CODE_CMR = ((CODE_DR_MAYNARD * 2) Mod 10000)
	If CODE_CMR < 1000 Then CODE_CMR = CODE_CMR + 1000
	
	CODE_MAINTENANCE_TUNNELS = ((CODE_DR_MAYNARD * 3) Mod 10000)
	If CODE_MAINTENANCE_TUNNELS < 1000 Then CODE_MAINTENANCE_TUNNELS = CODE_MAINTENANCE_TUNNELS + 1000
	
	CODE_DR_GEARS = ((CODE_DR_MAYNARD * 4) Mod 10000)
	If CODE_DR_GEARS < 1000 Then CODE_DR_GEARS = CODE_DR_GEARS + 1000
	
	RenderLoading(55, GetLocalString("loading", "rooms"))
	
	For it.Items = Each Items
		EntityType(it\Collider, 0)
	Next
	
	If SelectedCustomMap = Null
		CreateMap()
	Else
		LoadMap(CustomMapsPath + SelectedCustomMap\Name)
	EndIf
	
	LoadWayPoints()
	
	n_I\Curr173 = CreateNPC(NPCType173, 0.0, -500.0, 0.0)
	n_I\Curr106 = CreateNPC(NPCType106, 0.0, -500.0, 0.0)
	n_I\Curr106\State2 = 70.0 * 60.0 * Rnd(12.0, 16.0)
	
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
			Case FENCE_DOOR
				;[Block]
				MoveEntity(d\OBJ, 114.0 * RoomScale, 0.0, 0.0)
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
	
	For r.Rooms = Each Rooms
		If r\RoomTemplate\DisableDecals < 2
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
			TFormPoint(3584.0, 580.0, 3096.0, r\OBJ, 0)
			PositionEntity(me\Collider, TFormedX(), TFormedY(), TFormedZ())
			PlayerRoom = r
			it.Items = CreateItem("Class D Orientation Leaflet", it_paper, 0.0, 0.0, 0.0)
			PickItem(it, False)
		ElseIf r\RoomTemplate\RoomID = r_cont1_173_intro And opt\IntroEnabled
			InitializeIntroMovie = True
			TFormPoint(-4096.0, 0.0, 0.0, r\OBJ, 0)
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
	
	RaycastItems()
	fps\Factor[0] = 0.0
	fps\PrevTime = MilliSecs()
	ShouldDeleteGadgets = True
	
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
				
				I_1499\Sky = CreateSky("GFX\Map\Textures\1499sky")
				
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
	ShouldDeleteGadgets = True
	
	ResetInput()
	
	CatchErrors("Uncaught: InitLoadGame()")
End Function

Function InitOtherStuff%()
	Local TempStr$
	
	me\Playable = 2 : me\SelectedEnding = -1
	
	opt\MasterVolume = opt\PrevMasterVolume
	
	chs\NoClipSpeed = 2.0
	If opt\DebugMode Then InitCheats()
	
	as\Timer = 70.0 * 70.0
	If SelectedDifficulty\SaveType <> DIFFICULTY_SAVE_TYPE_SAVE_ANYWHERE Then opt\AutoSaveEnabled = False
	
	Local HideX# = -400 * MenuScale
	
	ProtectHUDX = HideX
	CapHUDX = HideX
	
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

Function NullGame%(PlayButtonSFX% = True)
	CatchErrors("NullGame()")
	
	Local ach.AchievementMsg, c.ConsoleMsg, e.Events, itt.ItemTemplates, it.Items, de.Decals, p.Particles, d.Doors, lvr.Levers, sc.SecurityCams
	Local du.Dummy1499_1, n.NPCs, s.Screens, w.WayPoints, pr.Props, l.Lights, rt.RoomTemplates, r.Rooms, m.Materials, snd.Sound, fr.Forest
	Local ch.Chunk, chp.ChunkPart, sv.Save, cm.CustomMaps, se.SoundEmitters, tmp.Template, emit.Emitter, al.AlarmLamp
	
	Local i%
	
	DeleteTextureEntriesFromCache(DeleteAllTextures)
	
	StopMouseMovement()
	KillSounds(False)
	opt\MasterVolume = opt\PrevMasterVolume
	If PlayButtonSFX Then PlaySound_Strict(ButtonSFX[0])
	
	RandomSeed = ""
	
	Delete(opttimer) : opttimer = Null
	
	DifficultyDMGMult = 0.0
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
	CoffinDistance = 0.0
	CameraZoomValue = 0.0
	
	SecondaryLightOn = 0.0
	IsBlackOut = False : PrevIsBlackOut = False
	LightRenderDistance = 0.0
	
	RemoteDoorOn = False
	
	GameSaved = False
	CanSave = 0
	
	MTFTimer = 0.0
	MTFCameraCheckTimer = 0.0
	MTFCameraCheckDetected = False
	
	SNAVUnlocked = False
	EReaderUnlocked = False
	
	CODE_DR_MAYNARD = 0
	CODE_DR_GEARS = 0
	CODE_MAINTENANCE_TUNNELS = 0
	CODE_CMR = 0
	
	ShouldPlay = 66
	FreeEntity(SoundEmitter) : SoundEmitter = 0
	SoundTransmission = False
	
	TempLightVolume = 0.0
	LightVolume = 0.0
	
	GrabbedEntity = 0
	CameraPitch = 0.0
	
	For i = 0 To MaxHandIcons - 1
		Delete(HandIcon[i])
	Next
	HandEntity = 0
	For i = 0 To 3
		DrawArrowIcon[i] = False
	Next
	
	BatMsgTimer = 0.0
	
	EscapeSecondsTimer = 0.0
	EscapeTimer = 0
	BreachTime = 0
	
	If Camera <> 0 Then FreeEntity(Camera) : Camera = 0
	If Sky <> 0 Then FreeEntity(Sky) : Sky = 0
	If Sky106 <> 0 Then FreeEntity(Sky106) : Sky106 = 0
	
	CurrTrisAmount = 0
	
	CurrAchvMSGID = 0
	For ach.AchievementMsg = Each AchievementMsg
		Delete(ach)
	Next
	
	If SubFile <> 0 Then JsonFreeDocument(SubFile) : SubFile = 0
	If LocalSubFile <> 0 Then JsonFreeDocument(LocalSubFile) : LocalSubFile = 0
	SubColors = 0
	LocalSubColors = 0
	SubtitlesInit = False
	ClearSubtitles()
	DeInitSubtitlesAssets()
	Delete(msg) : msg = Null
	Delete(as) : as = Null
	
	FreeEntity(wi\SCRAMBLESpriteScreen) : wi\SCRAMBLESpriteScreen = 0
	Delete(wi) : wi = Null
	Delete(fog) : fog = Null
	
	Delete(I_009) : I_009 = Null
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
	Delete(I_714) : I_714 = Null
	Delete(I_1025) : I_1025 = Null
	If I_1499\Sky <> 0 Then FreeEntity(I_1499\Sky) : I_1499\Sky = 0
	Delete(I_1499) : I_1499 = Null
	SCP1499Chunks = 0
	Delete(I_1048A) : I_1048A = Null
	FreeSound_Strict(I_1123\Sound) : I_1123\Sound = 0
	Delete(I_1123) : I_1123 = Null
	Delete(I_966) : I_966 = Null
	Delete(I_2022) : I_2022 = Null
	
	QuickLoadPercent = 0
	QuickLoadPercent_DisplayTimer = 0.0
	For e.Events = Each Events
		RemoveEvent(e)
	Next
	skull_event = Null
	PD_event = Null
	forest_event = Null
	
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
	skull_event_leaflet = Null
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
	ButtonDirection = False
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
	For al.AlarmLamp = Each AlarmLamp
		RemoveAlarmLamp(al)
	Next
	For se.SoundEmitters = Each SoundEmitters
		RemoveSoundEmitter(se)
	Next
	For fr.Forest = Each Forest
		If fr <> Null Then DestroyForest(fr)
		Delete(fr)
	Next
	For i = 0 To MaxChunkData - 1
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
	KEY2_SPAWNRATE = 0
	For r.Rooms = Each Rooms
		RemoveRoom(r)
	Next
	For rt.RoomTemplates = Each RoomTemplates
		RemoveRoomTemplate(rt)
	Next
	
	FreeEntity(me\Collider) : me\Collider = 0
	FreeEntity(me\Head) : me\Head = 0
	Delete(me) : me = Null
	FreeEntity(pm\OBJ) : pm\OBJ = 0
	FreeEntity(pm\Pivot) : pm\Pivot = 0
	Delete(pm) : pm = Null
	
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
	Mesh_MidX = 0.0 : Mesh_MidY = 0.0 : Mesh_MidZ = 0.0
	Mesh_MaxX = 0.0 : Mesh_MaxY = 0.0 : Mesh_MaxZ = 0.0
	Mesh_MagX = 0.0 : Mesh_MagY = 0.0 : Mesh_MagZ = 0.0
	
	InitializeIntroMovie = False
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
	
	DestructInstanceCore()
	
	FreeBlur()
	
	RenderTween = 0.0
	ShouldDisableHUD = False
	
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