; ~ This is the include file for the "Devil Particle System" by "bytecode77"
; ~ The link to the system's BlitzBasic.com page: "http://www.blitzbasic.com/toolbox/toolbox.php?tool=174"
; ~ The link to the original page: "https://bytecode77.com/coding/devilengines/devilparticlesystem"
; ~ All rights for this file go to "bytecode77"
;
; ~ This file also has been modified a bit to suit better for SCP:CB


Type Template
	Field EmitterMaxTime%											; ~ Emitter life time
	Field EmitterBlend%												; ~ Blendmode of emitter entity
	Field Interval%, ParticlesPerInterval%							; ~ Particle interval
	Field MaxParticles%												; ~ Max particles
	Field MinTime%, MaxTime%										; ~ Particle life time
	Field Tex%, AnimTex%, TexFrame#, MaxTexFrames%, TexSpeed#		; ~ Texture
	Field MinOX#, MaxOX#, MinOY#, MaxOY#, MinOZ#, MaxOZ#			; ~ Offset
	Field MinXV#, MaxXV#, MinYV#, MaxYV#, MinZV#, MaxZV#			; ~ Velocity
	Field RotVel1#, RotVel2#, AlignToFall%, AlignToFallOffset%		; ~ Rotation
	Field Gravity#													; ~ Gravity
	Field Alpha#, AlphaVel%											; ~ Alpha
	Field sX#, sY#, SizeMultiplicator1#, SizeMultiplicator2#		; ~ Size
	Field SizeAdd#, SizeMult#										; ~ Size velocity
	Field R1%, G1%, B1%, R2%, G2%, B2%								; ~ Colors
	Field Brightness%												; ~ Brightness
	Field FloorY#, FloorBounce#, FloorUp%							; ~ Floor
	Field PitchFix%, YawFix%										; ~ Fix angles
End Type

Type Emitter
	Field LoopAmount#, Age#, MaxTime#
	Field tmp.Template
	Field Owner%, Ent%, Surf%
	Field Del%
	Field room.Rooms
	Field State%
	Field SoundCHN%
End Type

Type Particle
	Field emitter.Emitter
	Field Age%, MaxTime%	; ~ Life time
	Field x#, y#, z#		; ~ Position
	Field XV#, YV#, ZV#		; ~ Velocity
	Field Rot#, RotVel#		; ~ Rotation
	Field sX#, sY#			; ~ Size
End Type

Global ParticleCam%
Global ParticlePiv%
Global ParticleEffect%[16]
Global UpdateDevilParticlesTimer# = 0.0

Function CreateTemplate()
	Local tmp.Template
	Local Template%
	
	tmp.Template = New Template
	
	Template = Handle(tmp)
	SetTemplateEmitterBlend(Template, 3)
	SetTemplateInterval(Template, 1)
	SetTemplateParticlesPerInterval(Template, 1)
	SetTemplateMaxParticles(Template, -1)
	SetTemplateEmitterLifeTime(Template, 100)
	SetTemplateParticleLifeTime(Template, 0, 20)
	SetTemplateAlpha(Template, 1)
	SetTemplateSize(Template, 1, 1)
	SetTemplateSizeVel(Template, 0, 1)
	SetTemplateColors(Template, $FFFFFF, $FFFFFF)
	SetTemplateBrightness(Template, 1)
	SetTemplateFloor(Template, -1000000)
	SetTemplateFixAngles(Template, -1, -1)
	Return(Handle(tmp))
End Function

Function FreeTemplate%(Template%)
	Local tmp.Template
	Local i%
	
	tmp.Template = Object.Template(Template)
	Delete(tmp)
End Function

Function SetTemplateEmitterBlend%(Template%, EmitterBlend%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\EmitterBlend = EmitterBlend
End Function

Function SetTemplateInterval%(Template%, Interval%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\Interval = Interval
End Function

Function SetTemplateParticlesPerInterval%(Template%, ParticlesPerInterval%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\ParticlesPerInterval = ParticlesPerInterval
End Function

Function SetTemplateMaxParticles%(Template%, MaxParticles%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\MaxParticles = MaxParticles
End Function

Function SetTemplateParticleLifeTime%(Template%, MinTime%, MaxTime%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\MinTime = MinTime
	tmp\MaxTime = MaxTime
End Function

Function SetTemplateEmitterLifeTime%(Template%, EmitterMaxTime%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\EmitterMaxTime = EmitterMaxTime
End Function

Function SetTemplateTexture%(Template%, ID%, Anim% = False, MaxFrames% = 1, Speed# = 1.0)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\Tex = p_I\ParticleTextureID[ID]
	tmp\AnimTex = Anim
	If Anim
		tmp\MaxTexFrames = MaxFrames
		tmp\TexSpeed = Speed
	EndIf
End Function

Function SetTemplateOffset%(Template%, MinOX#, MaxOX#, MinOY#, MaxOY#, MinOZ#, MaxOZ#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\MinOX = MinOX
	tmp\MaxOX = MaxOX
	tmp\MinOY = MinOY
	tmp\MaxOY = MaxOY
	tmp\MinOZ = MinOZ
	tmp\MaxOZ = MaxOZ
End Function

Function SetTemplateVelocity%(Template%, MinXV#, MaxXV#, MinYV#, MaxYV#, MinZV#, MaxZV#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\MinXV = MinXV
	tmp\MaxXV = MaxXV
	tmp\MinYV = MinYV
	tmp\MaxYV = MaxYV
	tmp\MinZV = MinZV
	tmp\MaxZV = MaxZV
End Function

Function SetTemplateRotation%(Template%, RotVel1#, RotVel2#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\RotVel1 = RotVel1
	tmp\RotVel2 = RotVel2
End Function

Function SetTemplateAlignToFall%(Template%, AlignToFall%, AlignToFallOffset% = 0)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\AlignToFall = AlignToFall
	tmp\AlignToFallOffset = AlignToFallOffset
End Function

Function SetTemplateGravity%(Template%, Gravity#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\Gravity = Gravity
End Function

Function SetTemplateSize%(Template%, sX#, sY#, SizeMultiplicator1# = 1.0, SizeMultiplicator2# = 1.0)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\sX = sX
	tmp\sY = sY
	tmp\SizeMultiplicator1 = SizeMultiplicator1
	tmp\SizeMultiplicator2 = SizeMultiplicator2
End Function

Function SetTemplateSizeVel%(Template%, SizeAdd#, SizeMult#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\SizeAdd = SizeAdd
	tmp\SizeMult = SizeMult
End Function

Function SetTemplateAlpha%(Template%, Alpha#)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\Alpha = Alpha
End Function

Function SetTemplateAlphaVel%(Template%, AlphaVel%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\AlphaVel = AlphaVel
End Function

Function SetTemplateColors%(Template%, Col1%, Col2%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\R1 = (Col1 And $FF0000) / $10000
	tmp\G1 = (Col1 And $FF00) / $100
	tmp\B1 = Col1 And $FF
	tmp\R2 = (Col2 And $FF0000) / $10000
	tmp\G2 = (Col2 And $FF00) / $100
	tmp\B2 = Col2 And $FF
End Function

Function SetTemplateBrightness%(Template%, Brightness%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\Brightness = Brightness
End Function

Function SetTemplateFloor%(Template%, FloorY#, FloorBounce# = 0.5, FloorUp% = -1)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\FloorY = FloorY
	tmp\FloorBounce = FloorBounce
	tmp\FloorUp = FloorUp
End Function

Function SetTemplateFixAngles%(Template%, PitchFix%, YawFix%)
	Local tmp.Template
	
	tmp.Template = Object.Template(Template)
	tmp\PitchFix = PitchFix
	tmp\YawFix = YawFix
End Function

; ~ TODO: Find a way to remove and create emitters during the save loading without loosing the parent object
Function SetEmitter.Emitter(room.Rooms, x#, y#, z#, ID%)
	Local emit.Emitter
	Local i%
	
	emit.Emitter = New Emitter
	emit\room = room
	
	emit\Owner = CreatePivot()
	PositionEntity(emit\Owner, x, y, z)
	If room <> Null Then EntityParent(emit\Owner, room\OBJ)
	
	emit\Ent = CreateMesh()
	emit\Surf = CreateSurface(emit\Ent)
	
	emit\tmp = Object.Template(ParticleEffect[ID])
	emit\MaxTime = emit\tmp\EmitterMaxTime
	EntityBlend(emit\Ent, emit\tmp\EmitterBlend)
	EntityFX(emit\Ent, 1 + 2 + 32)
	If emit\tmp\Tex <> 0 Then EntityTexture(emit\Ent, emit\tmp\Tex)
	
	Return(emit)
End Function

Function FreeEmitter%(emit.Emitter, DeleteParticles% = False)
	Local p.Particle
	
	If DeleteParticles
		For p.Particle = Each Particle
			If p\emitter = emit Then Delete(p)
		Next
		FreeEntity(emit\Ent) : emit\Ent = 0
		emit\Surf = 0
		FreeEntity(emit\Owner) : emit\Owner = 0
		Delete(emit)
	Else
		emit\Del = True
	EndIf
End Function

Function UpdateParticles_Devil()
	Local emit.Emitter, p.Particle
	Local i%
	
	For emit.Emitter = Each Emitter
		ClearSurface(emit\Surf)
		If emit\MaxTime > -1
			If emit\Age > emit\MaxTime
				emit\Del = True
			Else
				emit\Age = emit\Age + 1
			EndIf
		EndIf
		If fps\Factor[0] > 0.0 And (emit\room = Null Lor (PlayerRoom = emit\room Lor emit\room\Dist < 8.0))
			If emit\tmp\MaxParticles > -1
				Local ParticlesAmount% = 0
				
				For p.Particle = Each Particle
					If p\emitter = emit Then ParticlesAmount = ParticlesAmount + 1
				Next
			EndIf
			emit\LoopAmount = (emit\LoopAmount + 1) Mod emit\tmp\Interval
			If emit\LoopAmount = 0 And (Not emit\Del)
				For i = 1 To emit\tmp\ParticlesPerInterval
					If (emit\tmp\MaxParticles > -1 And ParticlesAmount < emit\tmp\MaxParticles) Lor emit\tmp\MaxParticles = -1
						p.Particle = New Particle
						p\emitter = emit
						p\MaxTime = Rand(emit\tmp\MinTime, emit\tmp\MaxTime)
						p\x = EntityX(emit\Owner, True) + Rnd(emit\tmp\MinOX, emit\tmp\MaxOX)
						p\y = EntityY(emit\Owner, True) + Rnd(emit\tmp\MinOY, emit\tmp\MaxOY)
						p\z = EntityZ(emit\Owner, True) + Rnd(emit\tmp\MinOZ, emit\tmp\MaxOZ)
						p\XV = Rnd(emit\tmp\MinXV, emit\tmp\MaxXV)
						p\YV = Rnd(emit\tmp\MinYV, emit\tmp\MaxYV)
						p\ZV = Rnd(emit\tmp\MinZV, emit\tmp\MaxZV)
						p\RotVel = Rnd(emit\tmp\RotVel1, emit\tmp\RotVel2)
						
						Local SM# = Rnd(emit\tmp\SizeMultiplicator1, emit\tmp\SizeMultiplicator2)
						
						p\sX = p\emitter\tmp\sX * SM
						p\sY = p\emitter\tmp\sY * SM
					EndIf
				Next
			EndIf
			If emit\tmp\AnimTex
				emit\tmp\TexFrame = emit\tmp\TexFrame + emit\tmp\TexSpeed
				If emit\tmp\TexFrame > emit\tmp\MaxTexFrames - 1 Then emit\tmp\TexFrame = 0
				EntityTexture(emit\Ent, emit\tmp\Tex, emit\tmp\TexFrame)
			EndIf
			
			Local InSmoke% = False
			
			Select emit\State
				Case 1
					;[Block]
					emit\SoundCHN = LoopSound2(snd_I\HissSFX[0], emit\SoundCHN, Camera, emit\Owner)
					If (Not InSmoke)
						If wi\GasMask = 0 And wi\HazmatSuit = 0
							If DistanceSquared(EntityX(Camera, True), EntityX(emit\Owner, True), EntityZ(Camera, True), EntityZ(emit\Owner, True)) < 0.64
								If Abs(EntityY(Camera, True) - EntityY(emit\Owner, True)) < 5.0 Then InSmoke = True
							EndIf
						EndIf
					EndIf
					;[End Block]
				Case 2
					;[Block]
					emit\SoundCHN = LoopSound2(snd_I\HissSFX[1], emit\SoundCHN, Camera, emit\Owner, 5.0)
					;[End Block]
			End Select
			
			If InSmoke
				If me\EyeIrritation > 70.0 * 6.0 Then me\BlurVolume = Max(me\BlurVolume, (me\EyeIrritation - (70.0 * 6.0)) / (70.0 * 24.0))
				If me\EyeIrritation > 70.0 * 24.0
					msg\DeathMsg = Format(GetLocalString("death", "smoke"), SubjectName)
					Kill()
				EndIf
				
				UpdateCough(150)
				me\EyeIrritation = me\EyeIrritation + (fps\Factor[0] * 4.0)
			EndIf
		EndIf
		
		If emit\Del
			Local Del% = True
			
			For p.Particle = Each Particle
				If p\emitter = emit Then Del = False
			Next
			If Del
				FreeEntity(emit\Ent) : emit\Ent = 0
				emit\Surf = 0
				FreeEntity(emit\Owner) : emit\Owner = 0
				Delete(emit)
			EndIf
		EndIf
	Next
	PositionEntity(ParticlePiv, EntityX(ParticleCam, True), EntityY(ParticleCam, True), EntityZ(ParticleCam, True))
	
	Local CamPitch# = EntityPitch(ParticleCam, True)
	Local CamYaw# = EntityYaw(ParticleCam, True)
	Local CamRoll# = EntityRoll(ParticleCam, True)
	
	For p.Particle = Each Particle
		If EntityDistanceSquared(p\emitter\Owner, me\Collider) > PowTwo(HideDistance) Lor p\Age > p\MaxTime
			Delete(p)
		Else
			p\Age = p\Age + 1
			If p\emitter\tmp\AlignToFall
				p\Rot = p\emitter\tmp\AlignToFallOffset - ATan2(p\XV, p\YV)
			Else
				p\Rot = p\Rot + p\RotVel
			EndIf
			p\YV = p\YV - p\emitter\tmp\Gravity
			p\x = p\x + p\XV
			p\y = p\y + p\YV
			p\z = p\z + p\ZV
			
			Local Bounce% = False
			
			If p\emitter\tmp\FloorUp = 1
				If p\y >= p\emitter\tmp\FloorY Then Bounce = True
			ElseIf p\emitter\tmp\FloorUp = 0
				If p\y < p\emitter\tmp\FloorY Then Bounce = True
			EndIf
			If Bounce Then p\YV = p\YV * (-p\emitter\tmp\FloorBounce)
			p\sX = (p\sX + p\emitter\tmp\SizeAdd) * p\emitter\tmp\SizeMult
			p\sY = (p\sY + p\emitter\tmp\SizeAdd) * p\emitter\tmp\SizeMult
			
			RotateEntity(ParticlePiv, CamPitch, CamYaw, CamRoll + (p\Rot + p\emitter\tmp\AlignToFallOffset))
			If p\emitter\tmp\PitchFix > -1 Then RotateEntity(ParticlePiv, p\emitter\tmp\PitchFix, EntityYaw(ParticlePiv), EntityRoll(ParticlePiv))
			If p\emitter\tmp\YawFix > -1 Then RotateEntity(ParticlePiv, EntityPitch(ParticlePiv), p\emitter\tmp\YawFix, EntityRoll(ParticlePiv))
			
			Local x# = EntityX(p\emitter\Ent) + p\x
			Local y# = EntityY(p\emitter\Ent) + p\y
			Local z# = EntityZ(p\emitter\Ent) + p\z
			Local sX# = p\sX
			Local sY# = p\sY
			
			TFormVector(sX, -sY, 0.0, ParticlePiv, 0)
			
			Local v1x# = TFormedX() + x
			Local v1y# = TFormedY() + y
			Local v1z# = TFormedZ() + z
			
			TFormVector(-sX, -sY, 0.0, ParticlePiv, 0)
			
			Local v2x# = TFormedX() + x
			Local v2y# = TFormedY() + y
			Local v2z# = TFormedZ() + z
			
			TFormVector(sX, sY, 0.0, ParticlePiv, 0)
			
			Local v3x# = TFormedX() + x
			Local v3y# = TFormedY() + y
			Local v3z# = TFormedZ() + z
			
			TFormVector(-sX, sY, 0.0, ParticlePiv, 0)
			
			Local v4x# = TFormedX() + x
			Local v4y# = TFormedY() + y
			Local v4z# = TFormedZ() + z
			Local v1% = AddVertex(p\emitter\Surf, v1x, v1y, v1z, 0.0, 0.0)
			Local v2% = AddVertex(p\emitter\Surf, v2x, v2y, v2z, 1.0, 0.0)
			Local v3% = AddVertex(p\emitter\Surf, v3x, v3y, v3z, 0.0, 1.0)
			Local v4% = AddVertex(p\emitter\Surf, v4x, v4y, v4z, 1.0, 1.0)
			Local R% = p\emitter\tmp\R1 + (p\emitter\tmp\R2 - p\emitter\tmp\R1) * Float(p\Age) / Float(p\MaxTime)
			Local G% = p\emitter\tmp\G1 + (p\emitter\tmp\G2 - p\emitter\tmp\G1) * Float(p\Age) / Float(p\MaxTime)
			Local B% = p\emitter\tmp\B1 + (p\emitter\tmp\B2 - p\emitter\tmp\B1) * Float(p\Age) / Float(p\MaxTime)
			Local CurrAlpha#
			
			If p\emitter\tmp\AlphaVel
				CurrAlpha = (1 - Float(p\Age) / Float(p\MaxTime)) * p\emitter\tmp\Alpha
			Else
				CurrAlpha = p\emitter\tmp\Alpha
			EndIf
			
			VertexColor(p\emitter\Surf, v1, R, G, B, CurrAlpha)
			VertexColor(p\emitter\Surf, v2, R, G, B, CurrAlpha)
			VertexColor(p\emitter\Surf, v3, R, G, B, CurrAlpha)
			VertexColor(p\emitter\Surf, v4, R, G, B, CurrAlpha)
			For i = 1 To p\emitter\tmp\Brightness
				AddTriangle(p\emitter\Surf, v1, v2, v3)
				AddTriangle(p\emitter\Surf, v3, v2, v4)
			Next
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS