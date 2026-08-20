Type Particles
	Field OBJ%, Pvt%
	Field Alpha#, Size#
	Field Speed#, ySpeed#, Gravity#
	Field RChange#, GChange#, BChange#, AlphaChange#
	Field SizeChange#
	Field LifeTime#
	Field Dist#
End Type

Function CreateParticle.Particles(ID%, x#, y#, z#, Size#, Gravity# = 1.0, LifeTime# = 200.0)
	If ID < 0 Lor ID >= MaxParticleTextureIDAmount Lor p_I\ParticleTextureID[ID] = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "particle"), ID))
	
	Local p.Particles
	
	p.Particles = New Particles
	p\OBJ = CreateSprite()
	PositionEntity(p\OBJ, x, y, z, True)
	EntityTexture(p\OBJ, p_I\ParticleTextureID[ID])
	RotateEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
	SpriteViewMode(p\OBJ, 3)
	
	Local FX% = 0
	
	Select ID
		Case PARTICLE_BLACK_SMOKE, PARTICLE_WHITE_SMOKE, PARTICLE_DUST, PARTICLE_BLOOD
			;[Block]
			FX = 1
			EntityFX(p\OBJ, FX)
			EntityBlend(p\OBJ, 1)
			;[End Block]
		Case PARTICLE_FLASH, PARTICLE_SUN, PARTICLE_SPARK
			;[Block]
			FX = 1 + 8
			EntityFX(p\OBJ, FX)
			EntityBlend(p\OBJ, 3)
			;[End Block]
	End Select
	
	p\Pvt = CreatePivot()
	PositionEntity(p\Pvt, x, y, z, True)
	
	p\LifeTime = LifeTime
	p\Gravity = Gravity * 0.004
	p\Alpha = 1.0
	p\Size = Size
	ScaleSprite(p\OBJ, p\Size, p\Size)
	
	Local State% = DEFERRED_TRANSPARENT
	
	If FX And 1 Then State = State Or DEFERRED_FULLBRIGHT
	If FX And 8 Then State = State Or DEFERRED_DISABLEFOG
	
	SetDeferredEntity(p\OBJ, False, DEFERRED_ADDITIVE Or State)
	
	EntityDestructor(p\OBJ, @ParticleDestructor)
	EntityDestructor(p\Pvt, @ParticleDestructor)
	
	Return(p)
End Function

Function UpdateParticles%()
	CatchErrors("UpdateParticles()")
	
	Local p.Particles
	Local HideDist# = PowTwo(GetCameraRangeFar(Camera) * LightVolume)
	
	For p.Particles = Each Particles
		If EntityDistanceSquared(p\OBJ, me\Collider) <= HideDist
			MoveEntity(p\Pvt, 0.0, 0.0, (p\Speed * fps\Factor[0]))
			If p\Gravity <> 0.0 Then p\ySpeed = p\ySpeed - (p\Gravity * fps\Factor[0])
			TranslateEntity(p\Pvt, 0.0, (p\ySpeed * fps\Factor[0]), 0.0, True)
			
			PositionEntity(p\OBJ, EntityX(p\Pvt, True), EntityY(p\Pvt, True), EntityZ(p\Pvt, True), True)
			
			If p\AlphaChange <> 0.0
				p\Alpha = Clamp(p\Alpha + (p\AlphaChange * fps\Factor[0]), 0.0, 1.0)
				EntityAlpha(p\OBJ, p\Alpha)
			EndIf
			
			If p\SizeChange <> 0.0
				p\Size = p\Size + (p\SizeChange * fps\Factor[0])
				ScaleSprite(p\OBJ, p\Size, p\Size)
			EndIf
			
			p\LifeTime = p\LifeTime - fps\Factor[0]
			If (p\LifeTime <= 0.0) Lor (p\Size < 0.00001) Lor (p\Alpha <= 0.0) Then RemoveParticle(p)
		Else
			RemoveParticle(p)
		EndIf
	Next
	
	CatchErrors("Uncaught: UpdateParticles()")
End Function

Function ParticleDestructor%(Entity%)
	Local p.Particles
	
	For p.Particles = Each Particles
		If p\Pvt = Entity
			EntityDestructor(p\OBJ, 0)
			FreeEntity(p\OBJ)
			Delete(p)
			Exit
		ElseIf p\OBJ = Entity
			EntityDestructor(p\Pvt, 0)
			FreeEntity(p\Pvt)
			Delete(p)
			Exit
		EndIf
	Next
End Function

Function RemoveParticle%(p.Particles)
	If p = Null Then Return
	FreeEntity(p\Pvt)
End Function

Global DustParticleChance%
Global SnowUpdateTimer#

Function UpdateSnow%()
	SnowUpdateTimer = SnowUpdateTimer - fps\Factor[0]
	If SnowUpdateTimer <= 0.0
		Local SpawnX#, SpawnZ#, SkyY#, GroundY#
		Local Attempts%
		Local ValidPointFound% = False
		Local PlayerPosX# = EntityX(me\Collider)
		Local PlayerPosZ# = EntityZ(me\Collider)
		
		GroundY = EntityY(me\Collider)
		SkyY = GroundY + 10.0
		
		For Attempts = 1 To 5
			SpawnX = PlayerPosX + Rnd(-5.0, 5.0)
			SpawnZ = PlayerPosZ + Rnd(-5.0, 5.0)
			
			Local Hit% = LinePick(SpawnX, SkyY, SpawnZ, 0.0, -10.0, 0.0, 0.5)
			
			If Hit = 0 Or PickedY() <= (GroundY + 1.0)
				ValidPointFound = True
				Exit
			EndIf
		Next
		
		If ValidPointFound Then SetEmitter(Null, SpawnX, SkyY, SpawnZ, 48)
		
		SnowUpdateTimer = 25.0
	EndIf
End Function

Function UpdateDust%()
	If IsPlayerOutsideFacility() Then Return
	
	Local emit.Emitter
	
	; ~ Create a single dust particle
	DustParticleChance = Max(35 + (25 * (opt\ParticleAmount = 1)) - (me\BigCameraShake > 0.0) * 35, 1)
	If Rand(DustParticleChance) = 1 Then SetEmitter(Null, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 12)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS