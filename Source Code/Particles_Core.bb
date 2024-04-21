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
	
	If p_I\ParticleTextureID[ID] = 0 Then RuntimeError(Format(GetLocalString("runerr", "particle"), ID))
	
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

Type DevilEmitters
	Field OBJ%
	Field x#, y#, z#
	Field ParticleID%
	Field room.Rooms
	Field Timer# = 0.0
	Field SoundCHN%
	Field State%
End Type

Function CreateDevilEmitter.DevilEmitters(room.Rooms, x#, y#, z#, ParticleID%)
	Local dem.DevilEmitters
	
	dem.DevilEmitters = New DevilEmitters
	dem\OBJ = CreatePivot()
	PositionEntity(dem\OBJ, x, y, z, True)
	If room <> Null Then EntityParent(dem\OBJ, room\OBJ)
	
	dem\room = room
	dem\x = x
	dem\y = y
	dem\z = z
	dem\ParticleID = ParticleID
	
	Return(dem)
End Function

Function UpdateDevilEmitters()
	Local dem.DevilEmitters
	Local InSmoke% = False
	
	For dem.DevilEmitters = Each DevilEmitters
		If fps\Factor[0] > 0.0 And (dem\room = Null Lor (PlayerRoom = dem\room Lor dem\room\Dist < 8.0))
			If dem\Timer = 0.0
				SetEmitter(dem\OBJ, ParticleEffect[dem\ParticleID])
				dem\Timer = 1.0
			Else
				Select dem\State
					Case 1
						;[Block]
						dem\SoundCHN = LoopSound2(HissSFX[0], dem\SoundCHN, Camera, dem\OBJ)
						If (Not InSmoke)
							If wi\GasMask = 0 And wi\HazmatSuit = 0
								If DistanceSquared(EntityX(Camera, True), EntityX(dem\OBJ, True), EntityZ(Camera, True), EntityZ(dem\OBJ, True)) < 0.64
									If Abs(EntityY(Camera, True) - EntityY(dem\OBJ, True)) < 5.0 Then InSmoke = True
								EndIf
							EndIf
						EndIf
						;[End Block]
					Case 2
						;[Block]
						dem\SoundCHN = LoopSound2(HissSFX[1], dem\SoundCHN, Camera, dem\OBJ, 5.0)
						;[End Block]
				End Select
			EndIf
		EndIf
	Next
	
	If InSmoke
		If me\EyeIrritation > 70.0 * 6.0 Then me\BlurVolume = Max(me\BlurVolume, (me\EyeIrritation - (70.0 * 6.0)) / (70.0 * 24.0))
		If me\EyeIrritation > 70.0 * 24.0
			msg\DeathMsg = Format(GetLocalString("death", "smoke"), SubjectName)
			Kill()
		EndIf
		
		UpdateCough(150)
		me\EyeIrritation = me\EyeIrritation + (fps\Factor[0] * 4.0)
	EndIf
End Function

Function RemoveDevilEmitter%(dem.DevilEmitters, RemoveParticles% = False)
	FreeEmitter(dem\OBJ, RemoveParticles)
	FreeEntity(dem\OBJ) : dem\OBJ = 0
	Delete(dem)
End Function

Function UpdateDust%()
	Local p.Particles, dem.DevilEmitters
	Local i%, Pvt%
	
	; ~ Create a single dust particle
	If Rand(45 + (25 * (opt\ParticleAmount = 1))) = 1 Then dem.DevilEmitters = CreateDevilEmitter(Null, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 12)
	
	; ~ Create extra dust particles while the camera is shaking
	If me\BigCameraShake > 0.0
		dem.DevilEmitters = CreateDevilEmitter(Null, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 12)
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS