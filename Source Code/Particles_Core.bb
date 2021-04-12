Type Particles
	Field OBJ%, Pvt%
	Field Alpha#, Size#
	Field Speed#, ySpeed#, Gravity#
	Field RChange#, GChange#, BChange#, AlphaChange#
	Field SizeChange#
	Field LifeTime#
End Type 

Function CreateParticle.Particles(ID%, x#, y#, z#, Size#, Gravity# = 1.0, LifeTime# = 200.0)
	Local p.Particles
	
	p.Particles = New Particles
	p\OBJ = CreateSprite()
	PositionEntity(p\OBJ, x, y, z, True)
	EntityTexture(p\OBJ, t\ParticleTextureID[ID])
	RotateEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
	EntityFX(p\OBJ, 1 + 8)
	SpriteViewMode(p\OBJ, 3)
	
	Select ID
		Case 0, 1, 6
			;[Block]
			EntityBlend(p\OBJ, 1)
			;[End Block]
		Case 2, 3, 4, 5, 7
			;[Block]
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
	
	If (Not t\ParticleTextureID[ID]) Then
		CreateConsoleMsg("Particle Texture ID: " + ID + " not found.")
		If opt\ConsoleOpening And opt\CanOpenConsole Then
			ConsoleOpen = True
		EndIf
		Return(Null)
	EndIf
	Return(p)
End Function
	
Function UpdateParticles()
	Local p.Particles
	
	For p.Particles = Each Particles
		MoveEntity(p\Pvt, 0.0, 0.0, (p\Speed * fps\Factor[0]))
		If p\Gravity <> 0.0 Then p\ySpeed = p\ySpeed - (p\Gravity * fps\Factor[0])
		TranslateEntity(p\Pvt, 0.0, (p\ySpeed * fps\Factor[0]), 0.0, True)
		
		PositionEntity(p\OBJ, EntityX(p\Pvt, True), EntityY(p\Pvt, True), EntityZ(p\Pvt, True), True)
		
		If p\AlphaChange <> 0.0 Then
			p\Alpha = Min(Max(p\Alpha + (p\AlphaChange * fps\Factor[0]), 0.0), 1.0)
			EntityAlpha(p\OBJ, p\Alpha)		
		EndIf
		
		If p\SizeChange <> 0.0 Then 
			p\Size = p\Size + (p\SizeChange * fps\Factor[0])
			ScaleSprite(p\OBJ, p\Size, p\Size)
		EndIf
		
		p\LifeTime = p\LifeTime - fps\Factor[0]
		If p\LifeTime =< 0.0 Lor p\Size < 0.00001 Lor p\Alpha =< 0.0 Then
			RemoveParticle(p)
		EndIf
	Next
End Function
	
Function RemoveParticle(p.Particles)
	FreeEntity(p\OBJ) : p\OBJ = 0
	FreeEntity(p\Pvt) : p\Pvt = 0
	Delete(p)
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
	Local e.Emitters
	Local r.Rooms
	
	e.Emitters = New Emitters
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
			e\AlphaChange = -0.008
			;[End Block]
		Case 1
			;[Block]
			e\Size = 0.03
			e\Gravity = -0.2
			e\LifeTime = 200.0
			e\SizeChange = 0.008
			e\Speed = 0.004
			e\RandAngle = 40
			e\AlphaChange = -0.01
			;[End Block]
	End Select
	
	For r.Rooms = Each Rooms
		If Abs(EntityX(e\OBJ) - EntityX(r\OBJ)) < 4.0 And Abs(EntityZ(e\OBJ) - EntityZ(r\OBJ)) < 4.0 Then
			e\room = r
		EndIf
	Next
	
	Return(e)
End Function

Function UpdateEmitters()
	Local e.Emitters, p.Particles
	Local InSmoke% = False
	
	For e.Emitters = Each Emitters
		If fps\Factor[0] > 0.0 And (PlayerRoom = e\room Lor e\room\Dist < 8.0) Then
			p.Particles = CreateParticle(0, EntityX(e\OBJ, True), EntityY(e\OBJ, True), EntityZ(e\OBJ, True), e\Size, e\Gravity, e\LifeTime)
			p\Speed = e\Speed
			RotateEntity(p\Pvt, EntityPitch(e\OBJ, True), EntityYaw(e\OBJ, True), EntityRoll(e\OBJ, True), True)
			TurnEntity(p\Pvt, Rnd(-e\RandAngle, e\RandAngle), Rnd(-e\RandAngle, e\RandAngle), 0)
			
			TurnEntity(p\OBJ, 0.0, 0.0, Rnd(360.0))
			
			p\SizeChange = e\SizeChange
			
			p\AlphaChange = e\AlphaChange
			
			e\SoundCHN = LoopSound2(HissSFX, e\SoundCHN, Camera, e\OBJ)
			
			If (Not InSmoke) Then
				If wi\GasMask = 0 And wi\HazmatSuit = 0 Then
					If DistanceSquared(EntityX(Camera, True), EntityX(e\OBJ, True), EntityZ(Camera, True), EntityZ(e\OBJ, True)) < 0.64 Then
						If Abs(EntityY(Camera, True) - EntityY(e\OBJ, True)) < 5.0 Then InSmoke = True
					EndIf
				EndIf					
			EndIf
		EndIf
	Next
	
	If InSmoke Then
		If me\EyeIrritation > 70.0 * 6.0 Then me\BlurVolume = Max(me\BlurVolume, (me\EyeIrritation - (70.0 * 6.0)) / (70.0 * 24.0))
		If me\EyeIrritation > 70.0 * 24.0 Then 
			msg\DeathMsg = SubjectName + " found dead in [DATA REDACTED]. Cause of death: Suffocation due to decontamination gas."
			Kill()
		EndIf
		
		If me\KillTimer >= 0.0 Then 
			If Rand(150) = 1 Then
				If (Not CoughCHN) Then
					CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
				Else
					If (Not ChannelPlaying(CoughCHN)) Then CoughCHN = PlaySound_Strict(CoughSFX[Rand(0, 2)])
				EndIf
			EndIf
		EndIf
		me\EyeIrritation = me\EyeIrritation + (fps\Factor[0] * 4.0)
	EndIf	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D