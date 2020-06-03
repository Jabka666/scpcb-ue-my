Type Particles
	Field OBJ%, Pvt%
	Field Image%
	Field R#, G#, B#, A#, Size#
	Field Speed#, ySpeed#, Gravity#
	Field RChange#, GChange#, BChange#, AChange#
	Field SizeChange#
	Field LifeTime#
End Type 
	
Function CreateParticle.Particles(x#, y#, z#, Image%, Size#, Gravity# = 1.0, LifeTime# = 200.0)
	Local p.Particles = New Particles
	Local tt.TextureTemplate = First TextureTemplate
	
	p\LifeTime = LifeTime
	
	p\OBJ = CreateSprite()
	PositionEntity(p\OBJ, x, y, z, True)
	EntityTexture(p\OBJ, tt\ParticleTextureID[Image])
	RotateEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
	EntityFX(p\OBJ, 1 + 8)
	
	SpriteViewMode (p\OBJ, 3)
	
	Select Image
		Case 0, 5, 6
			;[Block]
			EntityBlend(p\OBJ, 1)
			;[End Block]
		Case 1, 2, 3, 4, 7
			;[Block]
			EntityBlend(p\OBJ, BLEND_ADD)
			;[End Block]
	End Select
	
	p\Pvt = CreatePivot()
	PositionEntity(p\Pvt, x, y, z, True)
	
	p\Image = Image
	p\Gravity = Gravity * 0.004
	p\R = 255.0 : p\G = 255.0 : p\B = 255.0 : p\A = 1.0
	p\Size = Size
	ScaleSprite(p\OBJ, p\Size, p\Size)
	Return(p)
End Function
	
Function UpdateParticles()
	Local p.Particles
	
	For p.Particles = Each Particles
		MoveEntity(p\Pvt, 0.0, 0.0, p\Speed * fpst\FPSFactor[0])
		If p\Gravity <> 0 Then p\ySpeed = p\ySpeed - p\Gravity * fpst\FPSFactor[0]
		TranslateEntity(p\Pvt, 0.0, p\ySpeed * fpst\FPSFactor[0], 0.0, True)
		
		PositionEntity(p\OBJ, EntityX(p\Pvt, True), EntityY(p\Pvt, True), EntityZ(p\Pvt, True), True)
		
		If p\AChange <> 0.0 Then
			p\A = Min(Max(p\A + p\AChange * fpst\FPSFactor[0], 0.0), 1.0)
			EntityAlpha(p\OBJ, p\A)		
		EndIf
		
		If p\SizeChange <> 0.0 Then 
			p\Size = p\Size + p\SizeChange * fpst\FPSFactor[0]
			ScaleSprite(p\OBJ, p\Size, p\Size)
		EndIf
		
		p\LifeTime = p\LifeTime - fpst\FPSFactor[0]
		If p\LifeTime =< 0.0 Or p\Size < 0.00001 Or p\A =< 0.0 Then
			RemoveParticle(p)
		End If
	Next
End Function
	
Function RemoveParticle(p.Particles)
	FreeEntity(p\OBJ)
	FreeEntity(p\Pvt)	
	Delete(p)
End Function

Global InSmoke%
Global HissSFX% = LoadSound_Strict("SFX\General\Hiss.ogg")
Global SmokeDelay# = 0.0

Type Emitters
	Field OBJ%
	Field Size#
	Field MinImage%, MaxImage%
	Field Gravity#
	Field LifeTime#
	Field Disable%
	Field Room.Rooms
	Field SoundCHN%
	Field Speed#, RandAngle#
	Field SizeChange#, AChange#
End Type 

Function CreateEmitter.Emitters(x#, y#, z#, EmitterType%) 
	Local e.Emitters = New Emitters
	
	e\OBJ = CreatePivot()
	NameEntity(e\OBJ, "Emitter1")
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
			e\AChange = -0.008
			;[End Block]
		Case 1
			;[Block]
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200.0
			e\SizeChange = 0.008
			e\Speed = 0.004
			e\RandAngle = 40
			e\AChange = -0.01
			e\MinImage = 6 : e\MaxImage = 6
			;[End Block]
	End Select
	
	For r.Rooms = Each Rooms
		If Abs(EntityX(e\OBJ) - EntityX(r\OBJ)) < 4.0 And Abs(EntityZ(e\OBJ) - EntityZ(r\OBJ)) < 4.0 Then
			e\Room = r
		EndIf
	Next
	
	Return(e)
End Function

Function UpdateEmitters()
	InSmoke = False
	For e.Emitters = Each Emitters
		If fpst\FPSFactor[0] > 0.0 And (PlayerRoom = e\room Or e\room\Dist < 8.0) Then
			Local p.Particles = CreateParticle(EntityX(e\OBJ, True), EntityY(e\OBJ, True), EntityZ(e\OBJ, True), Rand(e\MinImage, e\MaxImage), e\Size, e\Gravity, e\LifeTime)
			
			p\Speed = e\speed
			RotateEntity(p\Pvt, EntityPitch(e\OBJ, True), EntityYaw(e\OBJ, True), EntityRoll(e\OBJ, True), True)
			TurnEntity(p\Pvt, Rnd(-e\RandAngle, e\RandAngle), Rnd(-e\RandAngle, e\RandAngle), 0)
			
			TurnEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
			
			p\SizeChange = e\SizeChange
			
			p\AChange = e\AChange
			
			e\SoundCHN = LoopSound2(HissSFX, e\SoundCHN, Camera, e\OBJ)
			
			If InSmoke = False Then
				If WearingGasMask = 0 And WearingHazmat = 0 Then
					Local Dist# = Distance(EntityX(Camera, True), EntityZ(Camera, True), EntityX(e\OBJ, True), EntityZ(e\OBJ, True))
					
					If Dist < 0.8 Then
						If Abs(EntityY(Camera, True) - EntityY(e\OBJ, True)) < 5.0 Then InSmoke = True
					EndIf
				EndIf					
			EndIf
		EndIf
	Next
	
	If InSmoke Then
		If EyeIrritation > (70.0 * 6.0) Then BlurVolume = Max(BlurVolume, (EyeIrritation - (70.0 * 6.0)) / (70.0 * 24.0))
		If EyeIrritation > (70.0 * 24.0) Then 
			msg\DeathMsg = SubjectName + " found dead in [DATA REDACTED]. Cause of death: Suffocation due to decontamination gas."
			Kill()
		EndIf
		
		If KillTimer >= 0.0 Then 
			If Rand(150) = 1 Then
				If CoughCHN = 0 Then
					CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				Else
					If ChannelPlaying(CoughCHN) = False Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
				End If
			EndIf
		EndIf
		EyeIrritation = EyeIrritation + fpst\FPSFactor[0] * 4.0
	EndIf	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D