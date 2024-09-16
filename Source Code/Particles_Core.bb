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
	CatchErrors("CreateParticle(" + ID + ", " + x + ", " + y + ", " + z + ", " + Size + ", " + Gravity + ", " + LifeTime + ")")
	
	Local p.Particles
	
	p.Particles = New Particles
	p\OBJ = CreateSprite()
	PositionEntity(p\OBJ, x, y, z, True)
	If ID = PARTICLE_WHITE_SMOKE
		EntityTexture(p\OBJ, p_I\ParticleTextureID[ID], Rand(0, 3))
	Else
		EntityTexture(p\OBJ, p_I\ParticleTextureID[ID])
	EndIf
	RotateEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
	SpriteViewMode(p\OBJ, 3)
	
	Select ID
		Case PARTICLE_BLACK_SMOKE, PARTICLE_WHITE_SMOKE, PARTICLE_DUST, PARTICLE_BLOOD
			;[Block]
			EntityFX(p\OBJ, 1)
			EntityBlend(p\OBJ, 1)
			;[End Block]
		Case PARTICLE_FLASH, PARTICLE_SHADOW, PARTICLE_SUN, PARTICLE_SPARK
			;[Block]
			EntityFX(p\OBJ, 1 + 8)
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
	
	If p_I\ParticleTextureID[ID] = 0 Then RuntimeErrorEx(Format(GetLocalString("runerr", "particle"), ID))
	
	CatchErrors("Uncaught: CreateParticle(" + ID + ", " + x + ", " + y + ", " + z + ", " + Size + ", " + Gravity + ", " + LifeTime + ")")
	
	Return(p)
End Function
	
Function UpdateParticles%()
	CatchErrors("UpdateParticles()")
	
	Local p.Particles
	
	For p.Particles = Each Particles
		If EntityDistanceSquared(p\OBJ, me\Collider) <= PowTwo(HideDistance)
			MoveEntity(p\Pvt, 0.0, 0.0, (p\Speed * fps\Factor[0]))
			If p\Gravity <> 0.0 Then p\ySpeed = p\ySpeed - (p\Gravity * fps\Factor[0])
			TranslateEntity(p\Pvt, 0.0, (p\ySpeed * fps\Factor[0]), 0.0, True)
			
			PositionEntity(p\OBJ, EntityX(p\Pvt, True), EntityY(p\Pvt, True), EntityZ(p\Pvt, True), True)
			
			If p\AlphaChange <> 0.0
				p\Alpha = Min(Max(p\Alpha + (p\AlphaChange * fps\Factor[0]), 0.0), 1.0)
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
	
Function RemoveParticle%(p.Particles)
	CatchErrors("RemoveParticle()")
	
	FreeEntity(p\OBJ) : p\OBJ = 0
	FreeEntity(p\Pvt) : p\Pvt = 0
	Delete(p)
	
	CatchErrors("Uncaught: RemoveParticles()")
End Function

Global DustParticleChance%

Function UpdateDust%()
	Local emit.Emitter
	
	; ~ Create a single dust particle
	DustParticleChance = Max(35 + (25 * (opt\ParticleAmount = 1)) - (me\BigCameraShake > 0.0) * 35, 1)
	If Rand(DustParticleChance) = 1 Then SetEmitter(Null, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 12)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS