; ~ This is the include file for the "Devil Particle System" by "bytecode77"
; ~ The link to the system's BlitzBasic.com page: "http://www.blitzbasic.com/toolbox/toolbox.php?tool=174"
; ~ The link to the original page: "https://bytecode77.com/coding/devilengines/devilparticlesystem"
; ~ All rights for this file go to "bytecode77"
; ~ This file also has been modified a bit to suit better for SCP:CB

Type Template
	Field Sub_Template.Template[7] ; ~ Sub templates
	Field Emitter_Blend% ; ~ BlendMode of emitter entity
	Field Interval%, Particles_Per_Interval% ; ~ Particle interval
	Field Max_Particles% ; ~ Max particles
	Field Emitter_Max_Time# ; ~ Emitter life time
	Field Min_Time#, Max_Time# ; ~ Particle life time
	Field Tex%, AnimTex%, TexFrame#, MaxTexFrames%, TexSpeed# ; ~ Texture
	Field Min_ox#, Max_ox#, Min_oy#, Max_oy#, Min_oz#, Max_oz# ; ~ Offset
	Field Min_xv#, Max_xv#, Min_yv#, Max_yv#, Min_zv#, Max_zv# ; ~ Velocity
	Field Rot_vel1#, Rot_vel2#, Align_To_Fall%, Align_To_Fall_Offset% ; ~ Rotation
	Field Gravity# ; ~ Gravity
	Field Alpha#, Alpha_Vel# ; ~ Alpha
	Field sx#, sy#, Size_Multiplicator1#, Size_Multiplicator2# ; ~ Size
	Field Size_add#, Size_Mult# ; ~ Size velocity
	Field R1%, G1%, B1%, R2%, G2%, B2% ; ~ Colors
	Field Brightness# ; ~ Brightness
	Field Floor_Y#, Floor_Bounce# ; ~ Floor
	Field Pitch_Fix#, Yaw_Fix# ; ~ Fix angles
	Field Yaw#
End Type

Type Emitter
	Field Fixed%
	Field Cnt_Loop#, Age#, Max_Time#
	Field tmp.Template
	Field Owner%, Ent%, Surf%
	Field Del%, Frozen%
End Type

Type Particle
	Field Emitter.Emitter
	Field Age%, Max_Time# ; ~ Life time
	Field x#, y#, z# ; ~ Position
	Field xv#, yv#, zv# ; ~ Velocity
	Field Rot#, Rot_Vel# ; ~ Rotation
	Field sx#, sy# ; ~ Size
End Type

Global ParticleCam, ParticlePiv

Function InitParticles(Cam%)
	ParticleCam = Cam
	ParticlePiv = CreatePivot()
	SeedRnd(MilliSecs())
End Function

Function FreeParticles()
	For tmp.Template = Each Template
		FreeTemplate(Handle(tmp))
	Next
	For e.Emitter = Each Emitter
		FreeEmitter(e\Ent)
	Next
	Delete Each Template
	Delete Each Emitter
	Delete Each Particle
	If ParticlePiv Then FreeEntity ParticlePiv
End Function

Function CreateTemplate()
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

Function FreeTemplate(Template%)
	tmp.Template = Object.Template(Template)
	If tmp\Tex Then FreeTexture(tmp\Tex)
	For i = 0 To 7
		If tmp\Sub_Template[i] <> Null Then FreeTemplate(Handle(tmp\Sub_Template[i]))
	Next
	Delete(tmp)
End Function

Function SetTemplateEmitterBlend(Template%, Emitter_Blend%)
	tmp.Template = Object.Template(Template)
	tmp\Emitter_Blend = Emitter_Blend
End Function

Function SetTemplateInterval(Template%, Interval#)
	tmp.Template = Object.Template(Template)
	tmp\Interval = Interval
End Function

Function SetTemplateParticlesPerInterval(Template%, Particles_Per_Interval#)
	tmp.Template = Object.Template(Template)
	tmp\Particles_Per_Interval = Particles_Per_Interval
End Function

Function SetTemplateMaxParticles(Template%, Max_Particles%)
	tmp.Template = Object.Template(Template)
	tmp\Max_Particles = Max_Particles
End Function

Function SetTemplateParticleLifeTime(Template%, Min_Time#, Max_Time#)
	tmp.Template = Object.Template(Template)
	tmp\Min_Time = Min_Time
	tmp\Max_Time = Max_Time
End Function

Function SetTemplateEmitterLifeTime(Template%, Emitter_Max_Time#)
	tmp.Template = Object.Template(Template)
	tmp\Emitter_Max_Time = Emitter_Max_Time
End Function

Function SetTemplateTexture(Template%, Path$, Mode% = 0, Blend% = 1)
	tmp.Template = Object.Template(Template)
	tmp\Tex = LoadTexture(Path$, Mode)
	TextureBlend(tmp\Tex, Blend)
End Function

Function SetTemplateAnimTexture(Template%, Path$, Mode%, Blend%, Width%, Height%, MaxFrames%, Speed# = 1)
	tmp.Template = Object.Template(Template)
	tmp\AnimTex = True
	tmp\MaxTexFrames = MaxFrames
	tmp\TexSpeed# = Speed#
	tmp\Tex = LoadAnimTexture(Path$, Mode, With, Height, 0, tmp\MaxTexFrames)
	TextureBlend(tmp\tex, Blend)
End Function

Function SetTemplateOffset(Template%, Min_ox#, Max_ox#, Min_oy#, Max_oy#, Min_oz#, Max_oz#)
tmp.Template = Object.Template(Template)
tmp\Min_ox# = Min_ox#
tmp\Max_ox# = Max_ox#
tmp\Min_oy# = Min_oy#
tmp\Max_oy# = Max_oy#
tmp\Min_oz# = Min_oz#
tmp\Max_oz# = Max_oz#
End Function

Function SetTemplateVelocity(Template%, Min_xv#, Max_xv#, Min_yv#, Max_yv#, Min_zv#, Max_zv#)
tmp.Template = Object.Template(Template)
tmp\Min_xv# = Min_xv#
tmp\Max_xv# = Max_xv#
tmp\Min_yv# = Min_yv#
tmp\Max_yv# = Max_yv#
tmp\Min_zv# = Min_zv#
tmp\Max_zv# = Max_zv#
End Function

Function SetTemplateRotation(Template%, Rot_Vel1#, Rot_Vel2#)
	tmp.Template = Object.Template(Template)
	tmp\Rot_Vel1# = Rot_Vel1#
	tmp\Rot_Vel2# = Rot_Vel2#
End Function

Function SetTemplateAlignToFall(Template%, Align_To_Fall%, Align_To_Fall_Offset% = 0)
	tmp.Template = Object.Template(Template)
	tmp\Align_To_Fall = Align_To_Fall
	tmp\Align_To_Fall_Offset = Align_To_Fall_Offset
End Function

Function SetTemplateGravity(Template%, Gravity#)
	tmp.Template = Object.Template(Template)
	tmp\gravity# = Gravity#
End Function

Function SetTemplateSize(Template%, sx#, sy#, Size_Multiplicator1# = 1, Size_Multiplicator2# = 1)
	tmp.Template = Object.Template(Template)
	tmp\sx# = sx#
	tmp\sy# = sy#
	tmp\Size_Multiplicator1# = Size_Multiplicator1#
	tmp\Size_Multiplicator2# = Size_Multiplicator2#
End Function

Function SetTemplateSizeVel(Template%, Size_Add#, Size_Mult#)
	tmp.Template = Object.Template(Template)
	tmp\Size_Add# = Size_Add#
	tmp\Size_Mult# = Size_Mult#
End Function

Function SetTemplateAlpha(Template%, Alpha#)
	tmp.Template = Object.Template(Template)
	tmp\Alpha# = Alpha#
End Function

Function SetTemplateAlphaVel(Template%, Alpha_vel#)
	tmp.Template = Object.Template(Template)
	tmp\Alpha_Vel = Alpha_vel
End Function

Function SetTemplateColors(Template%, Col1%, Col2%)
	tmp.Template = Object.Template(Template)
	tmp\R1 = (Col1 And $FF0000) / $10000
	tmp\G1 = (Col1 And $FF00) / $100
	tmp\B1 = Col1 And $FF
	tmp\R2 = (Col2 And $FF0000) / $10000
	tmp\G2 = (Col2 And $FF00) / $100
	tmp\B2 = Col2 And $FF
End Function

Function SetTemplateBrightness(Template%, Brightness#)
	tmp.Template = Object.Template(Template)
	tmp\brightness = Brightness
End Function

Function SetTemplateFloor(Template%, Floor_Y#, Floor_Bounce# = .5)
	tmp.Template = Object.Template(Template)
	tmp\Floor_Y# = Floor_Y#
	tmp\Floor_Bounce# = Floor_Bounce#
End Function

Function SetTemplateFixAngles(Template%, Pitch_Fix#, Yaw_Fix#)
	tmp.Template = Object.Template(Template)
	tmp\Pitch_Fix = Pitch_Fix
	tmp\Yaw_Fix = Yaw_Fix
End Function

Function SetTemplateSubTemplate(Template%, Sub_Template%, For_Each_Particle% = False)
	tmp.Template = Object.Template(Template)
	For i = 0 To 7
		If tmp\sub_template[i] = Null Then
			tmp\sub_template[i] = Object.Template(Sub_Template)
			Exit
		EndIf
	Next
End Function

Function SetEmitter(Owner%, Template%, Fixed% = False)
	e.Emitter = New Emitter
	If Fixed Then
		e\Owner = CreatePivot()
		PositionEntity(e\Owner, EntityX(Owner), EntityY(Owner), EntityZ(Owner))
		e\Fixed = True
	Else
		e\Owner = Owner
	EndIf
	e\Ent = CreateMesh()
	NameEntity(e\Ent,"Emitter3")
	e\Surf = CreateSurface(e\ent)
	e\tmp = Object.Template(Template)
	e\Max_Time = e\tmp\Emitter_Max_Time
	EntityBlend(e\Ent, e\tmp\Emitter_Blend)
	EntityFX(e\Ent, 34)
	If e\tmp\Tex Then EntityTexture(e\Ent, e\tmp\Tex)
	For i = 0 To 7
		If e\tmp\Sub_Template[i] <> Null Then
			If e\tmp\Sub_Template[i]\Tex Then SetEmitter(Owner, Handle(e\tmp\Sub_Template[i]), Fixed)
		EndIf
	Next
	Return(e\Ent)
End Function

Function FreeEmitter(Ent%, Delete_Particles% = True)
	For e.Emitter = Each Emitter
		If e\Owner = Ent Then
			If Delete_Particles Then
				For p.Particle = Each Particle
					If p\Emitter = e Then Delete(p)
				Next
				FreeEntity(e\Ent)
				If e\Fixed And e\Owner Then FreeEntity(e\Owner)
				Delete(e)
			Else
				e\Del = True
			EndIf
		EndIf
	Next
End Function

Function FreezeEmitter(Ent%)
	For e.Emitter = Each Emitter
		If e\Owner = Ent Then e\Frozen = True
	Next
End Function

Function UnfreezeEmitter(Ent%)
	For e.Emitter = Each Emitter
		If e\Owner = Ent Then e\Frozen = False
	Next
End Function

Function SetTemplateYaw(Template%, Yaw#)
	tmp.template = Object.Template(Template)
	tmp\Yaw = Yaw#
End Function

Function UpdateParticles_Devil()
	Local e.Emitter, p.Particle
	
	For e.Emitter = Each Emitter
		If e\tmp\Max_Particles > -1 Then
			Cnt_Particles = 0
			For p.Particle = Each Particle
				If p\Emitter = e Then Cnt_Particles = Cnt_Particles + 1
			Next
		EndIf
		ClearSurface(e\Surf)
		If e\Max_Time > -1 Then
			If e\Age > e\Max_Time Then e\Del = True Else e\Age = e\Age + 1
		EndIf
		If e\Frozen = False Then
			e\Cnt_Loop = (e\Cnt_Loop + 1) Mod e\tmp\Interval
			If e\Cnt_Loop = 0 And e\Del = False Then
				For i = 1 To e\tmp\Particles_Per_Interval
					If (e\tmp\Max_Particles > -1 And Cnt_Particles < e\tmp\Max_Particles) Or e\tmp\Max_Particles = -1 Then
						p.Particle = New Particle
						p\Emitter = e
						p\Max_Time = Rand(e\tmp\Min_time, e\tmp\Max_Time)
						p\x# = EntityX(e\Owner, True) + Rnd(e\tmp\Min_ox#, e\tmp\Max_ox#)
						p\y# = EntityY(e\Owner, True) + Rnd(e\tmp\Min_oy#, e\tmp\Max_oy#)
						p\z# = EntityZ(e\Owner, True) + Rnd(e\tmp\Min_oz#, e\tmp\Max_oz#)
						p\xv# = Rnd(e\tmp\Min_xv#, e\tmp\Max_xv#)
						p\yv# = Rnd(e\tmp\Min_yv#, e\tmp\Max_yv#)
						p\zv# = Rnd(e\tmp\Min_zv#, e\tmp\Max_zv#)
						p\Rot_Vel# = Rnd(e\tmp\Rot_vel1#, e\tmp\Rot_vel2#)
						sm# = Rnd(e\tmp\Size_Multiplicator1#, e\tmp\Size_Multiplicator2#)
						p\sx# = p\Emitter\tmp\sx# * sm#
						p\sy# = p\Emitter\tmp\sy# * sm#
					EndIf
				Next
			EndIf
		EndIf
		If e\tmp\AnimTex Then
			e\tmp\TexFrame# = e\tmp\TexFrame# + e\tmp\TexSpeed#
			If e\tmp\TexFrame# > e\tmp\MaxTexFrames - 1 Then e\tmp\TexFrame# = 0
			EntityTexture(e\Ent, e\tmp\Tex, e\tmp\TexFrame#)
		EndIf
		Frame = Frame + TexSpeed#
		If e\Del Then
			Del = True
			For p.Particle = Each Particle
				If p\Emitter = e Then Del = False
			Next
			If Del Then
				FreeEntity(e\Ent)
				If e\Fixed And e\Owner Then FreeEntity(e\Owner)
				Delete e
			EndIf
		EndIf
	Next
	PositionEntity (ParticlePiv, EntityX(ParticleCam, True), EntityY(ParticleCam, True), EntityZ(ParticleCam, True))
	
	Local Cam_Pitch# = EntityPitch(ParticleCam, True)
	Local Cam_Yaw# = EntityYaw(ParticleCam, True)
	Local Cam_Roll# = EntityRoll(ParticleCam, True)
	
	For p.Particle = Each Particle
		If p\Age > p\Max_Time Then
			Delete(p)
		Else
			If p\Emitter\Frozen = False Then
				p\Age = p\Age + 1
				If p\Emitter\tmp\Align_To_Fall Then p\Rot# = (p\Emitter\tmp\Align_To_Fall_Offset - ATan2(p\xv#, p\yv#)) Else p\Rot# = (p\Rot# + p\Rot_Vel#)
				p\yv# = p\yv# - p\Emitter\tmp\Gravity#
				p\x# = p\x# + p\xv#
				p\y# = p\y# + p\yv#
				p\z# = p\z# + p\zv#
				If p\y# < p\Emitter\tmp\Floor_Y# Then p\yv# = p\yv# * (-p\Emitter\tmp\Floor_Bounce#)
				p\sx# = (p\sx# + p\Emitter\tmp\Size_add#) * p\Emitter\tmp\Size_Mult#
				p\sy# = (p\sy# + p\Emitter\tmp\Size_add#) * p\Emitter\tmp\Size_Mult#
			EndIf
			RotateEntity(ParticlePiv, Cam_Pitch#, Cam_Yaw#, Cam_Roll# + (p\Rot# + p\Emitter\tmp\Align_To_Fall_Offset))
			If p\Emitter\tmp\Pitch_Fix > -1 Then RotateEntity ParticlePiv, p\Emitter\tmp\Pitch_Fix, EntityYaw(ParticlePiv), EntityRoll(ParticlePiv)
			If p\Emitter\tmp\Yaw_Fix > -1 Then RotateEntity ParticlePiv, EntityPitch(ParticlePiv), p\Emitter\tmp\Yaw_Fix, EntityRoll(ParticlePiv)
			x# = EntityX(p\Emitter\Ent) + p\x#
			y# = EntityY(p\Emitter\Ent) + p\y#
			z# = EntityZ(p\Emitter\Ent) + p\z#
			sx# = p\sx#
			sy# = p\sy#
			TFormVector(sx#, -sy#, 0, ParticlePiv, 0)
			v1x# = TFormedX() + x#
			v1y# = TFormedY() + y#
			v1z# = TFormedZ() + z#
			TFormVector(-sx#, -sy#, 0, ParticlePiv, 0)
			v2x# = TFormedX() + x#
			v2y# = TFormedY() + y#
			v2z# = TFormedZ() + z#
			TFormVector(sx#, sy#, 0, ParticlePiv, 0)
			v3x# = TFormedX() + x#
			v3y# = TFormedY() + y#
			v3z# = TFormedZ() + z#
			TFormVector(-sx#, sy#, 0, ParticlePiv, 0)
			v4x# = TFormedX() + x#
			v4y# = TFormedY() + y#
			v4z# = TFormedZ() + z#
			v1 = AddVertex(p\Emitter\Surf, v1x#, v1y#, v1z#, 0, 0)
			v2 = AddVertex(p\Emitter\Surf, v2x#, v2y#, v2z#, 1, 0)
			v3 = AddVertex(p\Emitter\Surf, v3x#, v3y#, v3z#, 0, 1)
			v4 = AddVertex(p\Emitter\Surf, v4x#, v4y#, v4z#, 1, 1)
			r = p\Emitter\tmp\R1 + (p\Emitter\tmp\R2 - p\Emitter\tmp\R1) * Float(p\Age) / Float(p\Max_Time)
			g = p\Emitter\tmp\G1 + (p\Emitter\tmp\G2 - p\Emitter\tmp\G1) * Float(p\Age) / Float(p\Max_Time)
			b = p\Emitter\tmp\B1 + (p\Emitter\tmp\B2 - p\Emitter\tmp\B1) * Float(p\Age) / Float(p\Max_Time)
			If p\Emitter\tmp\Alpha_Vel Then a# = (1 - Float(p\Age) / Float(p\Max_Time)) * p\Emitter\tmp\Alpha# Else a# = p\Emitter\tmp\Alpha#
			VertexColor(p\Emitter\Surf, v1, r, g, b, a#)
			VertexColor(p\Emitter\Surf, v2, r, g, b, a#)
			VertexColor(p\Emitter\Surf, v3, r, g, b, a#)
			VertexColor(p\Emitter\Surf, v4, r, g, b, a#)
			For i = 1 To p\Emitter\tmp\Brightness
				AddTriangle(p\Emitter\Surf, v1, v2, v3)
				AddTriangle(p\Emitter\Surf, v3, v2, v4)
			Next
		EndIf
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D