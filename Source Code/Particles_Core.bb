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
	
	If (Not p_I\ParticleTextureID[ID]) Then RuntimeError(Format(GetLocalString("runerr", "particle"), ID))
	
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

Type Emitters
	Field OBJ%
	Field Size#
	Field Gravity#
	Field LifeTime#
	Field room.Rooms
	Field SoundCHN%
	Field Speed#, RandAngle#
	Field SizeChange#, AlphaChange#
End Type

Function CreateEmitter.Emitters(x#, y#, z#, EmitterType%)
	CatchErrors("CreateEmitter(" + x + ", " + y + ", " + z + ", " + EmitterType + ")")
	
	Local e.Emitters
	Local r.Rooms
	
	e.Emitters = New Emitters
	e\OBJ = CreatePivot()
	PositionEntity(e\OBJ, x, y, z, True)
	
	Select EmitterType
		Case 0
			;[Block]
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200.0
			e\SizeChange = 0.005
			e\Speed = 0.004
			e\RandAngle = 20.0
			e\AlphaChange = -0.008
			;[End Block]
		Case 1
			;[Block]
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200.0
			e\SizeChange = 0.008
			e\Speed = 0.004
			e\RandAngle = 40.0
			e\AlphaChange = -0.01
			;[End Block]
	End Select
	
	For r.Rooms = Each Rooms
		If Abs(EntityX(e\OBJ) - EntityX(r\OBJ)) < 4.0 And Abs(EntityZ(e\OBJ) - EntityZ(r\OBJ)) < 4.0 Then e\room = r
	Next
	
	CatchErrors("Uncaught: CreateEmitter(" + x + ", " + y + ", " + z + ", " + EmitterType + ")")
	
	Return(e)
End Function

Function UpdateEmitters%()
	CatchErrors("UpdateEmitters()")
	
	Local e.Emitters, p.Particles
	Local InSmoke% = False
	
	For e.Emitters = Each Emitters
		If fps\Factor[0] > 0.0 And (PlayerRoom = e\room Lor e\room\Dist < 8.0)
			p.Particles = CreateParticle(PARTICLE_BLACK_SMOKE, EntityX(e\OBJ, True), EntityY(e\OBJ, True), EntityZ(e\OBJ, True), e\Size, e\Gravity, e\LifeTime)
			p\Speed = e\Speed
			RotateEntity(p\Pvt, EntityPitch(e\OBJ, True), EntityYaw(e\OBJ, True), EntityRoll(e\OBJ, True), True)
			TurnEntity(p\Pvt, Rnd(-e\RandAngle, e\RandAngle), Rnd(-e\RandAngle, e\RandAngle), 0.0)
			
			TurnEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
			
			p\SizeChange = e\SizeChange
			
			p\AlphaChange = e\AlphaChange
			
			e\SoundCHN = LoopSound2(HissSFX, e\SoundCHN, Camera, e\OBJ)
			
			If (Not InSmoke)
				If wi\GasMask = 0 And wi\HazmatSuit = 0
					If DistanceSquared(EntityX(Camera, True), EntityX(e\OBJ, True), EntityZ(Camera, True), EntityZ(e\OBJ, True)) < 0.64
						If Abs(EntityY(Camera, True) - EntityY(e\OBJ, True)) < 5.0 Then InSmoke = True
					EndIf
				EndIf
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
	
	CatchErrors("Uncaught: UpdateEmitters()")
End Function

Function UpdateDust%()
	Local p.Particles
	Local i%, Pvt%
	
	If opt\ParticleAmount > 0
		; ~ Create a single dust particle
		If Rand(35 + (35 * (opt\ParticleAmount = 1))) = 1
			Pvt = CreatePivot()
			
			PositionEntity(Pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
			If Rand(2) = 1
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			EndIf
			
			p.Particles = CreateParticle(PARTICLE_DUST, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 0.002, 0.0, 300.0)
			p\Speed = 0.001 : p\SizeChange = -0.00001
			RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
			FreeEntity(Pvt) : Pvt = 0
		EndIf
		
		; ~ Create extra dust particles while the camera is shaking
		If me\BigCameraShake > 0.0
			For i = 0 To 5 + (2 * (opt\ParticleAmount - 1))
				Pvt = CreatePivot()
				PositionEntity(Pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
				RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
				If Rand(2) = 1
					MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
				Else
					MoveEntity(Pvt, 0.0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
				EndIf
				
				p.Particles = CreateParticle(PARTICLE_DUST, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 0.002, 0.0, 300.0)
				p\Speed = 0.001 : p\SizeChange = -0.00001
				RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
				FreeEntity(Pvt) : Pvt = 0
			Next
		EndIf
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS