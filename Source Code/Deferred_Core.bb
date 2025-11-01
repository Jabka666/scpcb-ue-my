Const DEFERRED_LIGHT_DIRECTIONAL% = 1
Const DEFERRED_LIGHT_POINT% = 2
Const DEFERRED_LIGHT_SPOT% = 3

Const DEFERRED_DIFF% = 0
Const DEFERRED_DIFFALPHA% = 1
Const DEFERRED_DIFFNORMAL% = 2
Const DEFERRED_DIFFROUGH% = 4
Const DEFERRED_DIFFEMISSIVE% = 8
Const DEFERRED_DIFFNOLIT% = 16
Const DEFERRED_DIFFEMISSIVEMUL% = 32
Const DEFERRED_NONE% = 64

Const DEFERRED_SHADE_SHADOWS% = 1
Const DEFERRED_SHADE_SCATTERING% = 2

Const MAX_DEFERRED_VARIATIONS% = 256
Const MAX_DEFERRED_SHADE_VARIATIONS% = 32

Const DIRECTIONAL_LIGHT_TIME% = 0
Const DIRECTIONAL_LIGHT_RANGE# = 0.01
Const DIRECTIONAL_LIGHT_EXTRUSION# = 20.0
Global SHADOW_BIAS# = 0.00044

Const SHADOW_MAP_MIPMAPS% = 3 ; ~ Don't change this
Const SHADOW_MAP_SIZE% = 256
Const DIRLIGHT_SHADOW_MAP_SIZE% = 1024

Global MRTColor%
Global MRTAlbedo%
Global MRTDepth%
Global MRTNormal%

Type InputEffect
	Field Effect%
	Field Bit%
End Type

Type ShadeEffect
	Field Effect%
	Field Bit%
End Type

Type DummyTexture
	Field Tex%
End Type

Global DeferredInputEffect.InputEffect[MAX_DEFERRED_VARIATIONS]
Global DeferredShadeEffect.ShadeEffect[MAX_DEFERRED_SHADE_VARIATIONS]

Global DeferredShadowMapCube%[SHADOW_MAP_MIPMAPS + 1]
Global DeferredShadowMap%[SHADOW_MAP_MIPMAPS + 1]
Global TextureDummies.DummyTexture

Global DeferredCamera%, QuadCamera%
Global DeferredSphere%, DeferredCone%, DeferredQuad%
Global DirectionalLightUpdate%
Global ShadowsDistance#
Global GBufferBlur#
Global TempColorTexture%

Global CubeRotateX#[6]
Global CubeRotateY#[6]

CubeRotateX[0] = 0 : CubeRotateY[0] = 90
CubeRotateX[1] = 0 : CubeRotateY[1] = 0
CubeRotateX[2] = 0 : CubeRotateY[2] = -90
CubeRotateX[3] = 0 : CubeRotateY[3] = 180
CubeRotateX[4] = -90 : CubeRotateY[4] = 0
CubeRotateX[5] = 90 : CubeRotateY[5] = 0

Function InitDeferred%()
	Local se.ShadeEffect
	Local i%
	
	ClearDeferred()
	LoadInputEffect(DEFERRED_NONE, "")
	LoadInputEffect(DEFERRED_DIFF, "Source Code\Deferred\Input.fx", "REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNOLIT, "Source Code\Deferred\Input.fx", "NOLIT REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFALPHA, "Source Code\Deferred\Input.fx", "TRANSPARENT REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNORMAL, "Source Code\Deferred\Input.fx", "NORMALMAP REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFROUGH, "Source Code\Deferred\Input.fx", "ROUGHMAP REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFEMISSIVE, "Source Code\Deferred\Input.fx", "EMISSIVEMAP REVERSEDZ")
	
	LoadInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH, "Source Code\Deferred\Input.fx", "NORMALMAP ROUGHMAP REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFEMISSIVE, "Source Code\Deferred\Input.fx", "NORMALMAP EMISSIVEMAP REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE, "Source Code\Deferred\Input.fx", "ROUGHMAP EMISSIVEMAP REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE, "Source Code\Deferred\Input.fx", "NORMALMAP ROUGHMAP EMISSIVEMAP REVERSEDZ")
	
	LoadInputEffect(DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL, "Source Code\Deferred\Input.fx", "EMISSIVEMAP MUL REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL, "Source Code\Deferred\Input.fx", "NORMALMAP EMISSIVEMAP MUL REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL, "Source Code\Deferred\Input.fx", "ROUGHMAP EMISSIVEMAP MUL REVERSEDZ")
	LoadInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL, "Source Code\Deferred\Input.fx", "NORMALMAP ROUGHMAP EMISSIVEMAP MUL REVERSEDZ")
	
	LoadShadeEffect(0, "Source Code\Deferred\Shade.fx")
	LoadShadeEffect(DEFERRED_SHADE_SHADOWS, "Source Code\Deferred\Shade.fx", "SHADOWS")
	LoadShadeEffect(DEFERRED_SHADE_SCATTERING, "Source Code\Deferred\Shade.fx", "SCATTERING")
	LoadShadeEffect(DEFERRED_SHADE_SHADOWS Or DEFERRED_SHADE_SCATTERING, "Source Code\Deferred\Shade.fx", "SHADOWS SCATTERING")
	
	MRTColor = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 1 + 2 + 256 + 16384)
	MRTAlbedo = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 1 + 2 + 256 + 16384)
	MRTDepth = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 131072)
	MRTNormal = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 1 + 2 + 256 + 16384)
	
	For i = 1 To SHADOW_MAP_MIPMAPS
		Local iRounded% = RoundTwo(i)
		
		DeferredShadowMapCube[i - 1] = CreateShadowMap(SHADOW_MAP_SIZE * 6 / iRounded, SHADOW_MAP_SIZE / iRounded)
		DeferredShadowMap[i - 1] = CreateShadowMap(SHADOW_MAP_SIZE / iRounded, SHADOW_MAP_SIZE / iRounded)
		
		CreateDummyTexture(SHADOW_MAP_SIZE * 6 / iRounded, SHADOW_MAP_SIZE / iRounded)
		CreateDummyTexture(SHADOW_MAP_SIZE / iRounded, SHADOW_MAP_SIZE / iRounded)
	Next
	
	DeferredShadowMap[SHADOW_MAP_MIPMAPS] = CreateShadowMap(DIRLIGHT_SHADOW_MAP_SIZE, DIRLIGHT_SHADOW_MAP_SIZE)
	
	Local FaceSelectCubeMap% = CreateTexture(1, 1, 1 + 2 + 128 + 512)
	
	For i = 0 To 5
		SetCubeFace(FaceSelectCubeMap, i)
		LockBuffer(TextureBuffer(FaceSelectCubeMap))
		WritePixelFast(0, 0, i Shl 16, TextureBuffer(FaceSelectCubeMap))
		UnlockBuffer(TextureBuffer(FaceSelectCubeMap))
	Next
	
	QuadCamera = CreateCamera()
	CameraClsMode(QuadCamera, 0, 0)
	HideEntity(QuadCamera)
	
	DeferredCamera = CreateCamera()
	CameraClsMode(DeferredCamera, 0, 1)
	CameraColorWrite(DeferredCamera, False)
	HideEntity(DeferredCamera)
	
	Local AdjustMatrix% = CreateBank(64)
	
	PokeFloat(AdjustMatrix, 0, 0.5)
	PokeFloat(AdjustMatrix, 20, -0.5)
	PokeFloat(AdjustMatrix, 40, 1.0)
	PokeFloat(AdjustMatrix, 48, 0.5)
	PokeFloat(AdjustMatrix, 52, 0.5)
	PokeFloat(AdjustMatrix, 60, 1.0)
	
	For se.ShadeEffect = Each ShadeEffect
		EffectMatrix(se\Effect, "ShadowsAdjust", BankPointer(AdjustMatrix))
	Next
	
	FreeBank(AdjustMatrix) : AdjustMatrix = 0
	
	Local SpotTexture% = LoadTexture("GFX\Other\spot.png")
	Local RampTexture% = LoadTexture("GFX\Other\ramp.png")
	
	DeferredSphere = CreateLightVolume(DEFERRED_LIGHT_POINT)
	DeferredCone = CreateLightVolume(DEFERRED_LIGHT_SPOT)
	DeferredQuad = CreateLightVolume(DEFERRED_LIGHT_DIRECTIONAL)
	
	EntityTexture(DeferredSphere, MRTAlbedo, 0, 0)
	EntityTexture(DeferredSphere, MRTNormal, 0, 1)
	EntityTexture(DeferredSphere, MRTDepth, 0, 2)
	EntityTexture(DeferredSphere, FaceSelectCubeMap, 0, 3)
	EntityTexture(DeferredSphere, SpotTexture, 0, 4)
	EntityTexture(DeferredSphere, RampTexture, 0, 5)
	EntityOrder(DeferredSphere, 10000000)
	EntityBlend(DeferredSphere, 3)
	EntityFX(DeferredSphere, 8)
	
	EntityTexture(DeferredCone, MRTAlbedo, 0, 0)
	EntityTexture(DeferredCone, MRTNormal, 0, 1)
	EntityTexture(DeferredCone, MRTDepth, 0, 2)
	EntityTexture(DeferredCone, FaceSelectCubeMap, 0, 3)
	EntityTexture(DeferredCone, SpotTexture, 0, 4)
	EntityTexture(DeferredCone, RampTexture, 0, 5)
	EntityOrder(DeferredCone, 10000000)
	EntityBlend(DeferredCone, 3)
	EntityFX(DeferredCone, 8)
	
	EntityTexture(DeferredQuad, MRTAlbedo, 0, 0)
	EntityTexture(DeferredQuad, MRTNormal, 0, 1)
	EntityTexture(DeferredQuad, MRTDepth, 0, 2)
	EntityTexture(DeferredQuad, FaceSelectCubeMap, 0, 3)
	EntityTexture(DeferredQuad, SpotTexture, 0, 4)
	EntityTexture(DeferredQuad, RampTexture, 0, 5)
	EntityOrder(DeferredQuad, 10000000)
	EntityBlend(DeferredQuad, 3)
	EntityFX(DeferredQuad, 8)
	
	FreeTexture(SpotTexture) : SpotTexture = 0
	FreeTexture(RampTexture) : RampTexture = 0
	
	HideEntity(DeferredSphere)
	HideEntity(DeferredCone)
	HideEntity(DeferredQuad)
	
	; ~ Volumes mask
	MaskEntity(DeferredSphere, 4)
	MaskEntity(DeferredCone, 4)
	MaskEntity(DeferredQuad, 4)
	
	SetShadowsDistance(3.0)
	SetShadowsBias(0.0002)
	DirectionalLightUpdate = 0
	
	SetEmissiveMultiply(1.0)
	SetGBufferBlur(0.0)
	
	TempColorTexture = CreateTexture(opt\GraphicWidth, opt\GraphicHeight, 1 + 256 + 16384)
End Function

Function ClearDeferred%()
	Delete Each DummyTexture
	Delete Each DynamicLight
	Delete Each InputEffect
	Delete Each ShadeEffect
End Function

Function SetDeferredParticle%(Entity%, Enable% = True)
	SetShadowsCasting(Entity, False) ;Enable)
End Function

Function SetShadowsCasting%(Entity%, Enable%)
	MaskEntity(Entity, 1 + (15 * Enable))
End Function

Function SetDeferredEntity%(Entity%, CastShadows% = False, State% = -1)
	Local SurfCount%
	
	If State <> -1
		Local i%, SF%, b%
		
		EntityEffect(Entity, GetInputEffect(State))
		If State = DEFERRED_NONE And EntityClass(Entity) = "Mesh"
			SurfCount = CountSurfaces(Entity)
			For i = 1 To SurfCount
				SF = GetSurface(Entity, i)
				b = GetSurfaceBrush(SF)
				If b <> 0
					SetDeferredBrush(b, DEFERRED_NONE)
					PaintSurface(SF, b)
					FreeBrush(b) : b = 0
				EndIf
			Next
		EndIf
	Else
		If EntityClass(Entity) = "Mesh"
			SurfCount = CountSurfaces(Entity)
			For i = 1 To SurfCount
				SF = GetSurface(Entity, i)
				b = GetSurfaceBrush(SF)
				If b <> 0
					SetDeferredBrush(b)
					PaintSurface(SF, b)
					FreeBrush(b) : b = 0
				EndIf
			Next
		Else
			UpdateEntityMaterial(Entity, State)
		EndIf
	EndIf
	
	SetShadowsCasting(Entity, CastShadows)
End Function

Function SetDeferredBrush%(Brush%, State% = -1, Frame% = 0)
	If State = -1
		State = DEFERRED_DIFF
		
		Local t1% = GetBrushTexture(Brush, 0)
		Local mat.Materials
		
		If t1 <> 0
			mat.Materials = GetMaterial(t1)
			If mat <> Null
				State = 0
				If mat\IsDiffuseAlpha
					State = DEFERRED_DIFFALPHA
				Else
					If mat\Normal <> 0 Then State = State Or DEFERRED_DIFFNORMAL
					If mat\Roughness <> 0 Then State = State Or DEFERRED_DIFFROUGH
					If mat\Emissive <> 0 Then State = State Or DEFERRED_DIFFEMISSIVE
					If mat\ReactBlackout <> 0 Then State = State Or DEFERRED_DIFFEMISSIVEMUL
					
					BrushTexture(Brush, MissingTexture, 0, 1)
					BrushTexture(Brush, MissingTexture, 0, 2)
					BrushTexture(Brush, MissingTexture, 0, 3)
					If mat\Normal <> 0 Then BrushTexture(Brush, mat\Normal, Frame, 1)
					If mat\Roughness <> 0 Then BrushTexture(Brush, mat\Roughness, Frame, 2)
					If mat\Emissive <> 0 Then BrushTexture(Brush, mat\Emissive, Frame, 3)
				EndIf
			EndIf
			FreeTexture(t1) : t1 = 0
		EndIf
	EndIf
	
	BrushEffect(Brush, GetInputEffect(State))
End Function

Function UpdateEntityMaterial%(Entity%, State% = -1, Frame% = 0)
	Local Brush% = GetEntityBrush(Entity)
	
	SetDeferredBrush(Brush, State, Frame)
	PaintEntity(Entity, Brush)
	FreeBrush(Brush) : Brush = 0
End Function

Function ProcessDeferred%(Cam%, Tween# = 1.0)
	If GetShadeEffect(0) <> 0 And GetInputEffect(DEFERRED_DIFF) <> 0
		Local ef.InputEffect, se.ShadeEffect
		
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "GBuffer")
		Next
		ClearBuffer(TextureBuffer(MRTColor), fog\ClsR / 255.0, fog\ClsG / 255.0, fog\ClsB / 255.0, 1)
		ClearBuffer(TextureBuffer(MRTAlbedo), 0, 0, 0, 1)
		ClearBuffer(TextureBuffer(MRTNormal), 0, 0, 0, 1)
		ClearBuffer(TextureBuffer(MRTDepth), 0, 0, 0, 1)
		SetBuffer(TextureBuffer(MRTColor))
		SetBuffer(TextureBuffer(MRTAlbedo), 1)
		SetBuffer(TextureBuffer(MRTNormal), 2)
		SetBuffer(TextureBuffer(MRTDepth), 3)
		CameraClsMode(Cam, 0, 1)
		AmbientLight(fog\CurrAmbientR, fog\CurrAmbientG, fog\CurrAmbientB)
		; ~ Render opacity
		RenderWorld(Tween, Cam, -1 Xor 32, 1) ; ~ Render only opacity
		ProcessSSAO(Cam, 1.5, 0.2, Tween) ; ~ Process SSAO for opacity
		
		InvViewProjection% = CameraMatrix(Cam, 3, Tween)
		For se.ShadeEffect = Each ShadeEffect
			EffectMatrix(se\Effect, "InvViewProj", InvViewProjection)
		Next
		ProcessAllLights(Cam, Tween)
		
		CameraClsMode(Cam, 0, 0)
		
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "GBuffer")
		Next
		
		BeginRender(Tween, -1) ; ~ We can't use transparency rendering twice without begin render
		; ~ Render decals
		RenderWorld(Tween, Cam, 32)
		; ~ Render transparency
		AmbientLight(Min(fog\CurrAmbientR * 4.0, 255.0), Min(fog\CurrAmbientG * 4.0, 255.0), Min(fog\CurrAmbientB * 4, 255.0))
		RenderWorld(Tween, Cam, -1 Xor 32, 2)
		CameraClsMode(Cam, 1, 1)
		EndRender()
		
		ProcessFXAA()
		ProcessBloom()
		ProcessColorCorrection()
		;ProcessEyeAdaptation()
		ProcessMotionBlur(Cam, 1.0, Tween)
		ProcessGamma(Lerp(opt\ScreenGamma, 1.0, 0.5))
		PresentGBuffer(MRTColor, BackBuffer())
		SetBuffer(BackBuffer())
	Else
		RenderWorld(Tween)
	EndIf
End Function

Function ProcessAllLights%(Cam%, Tween#)
	Local ef.InputEffect
	Local i%
	
	For ef.InputEffect = Each InputEffect
		If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "ShadowMap")
	Next
	CameraClsMode(Cam, 0, 0)
	
	Local l.Lights, dl.DynamicLight
	Local Near# = GetCameraRangeNear(Cam)
	Local Far# = GetCameraRangeFar(Cam)
	
	ShowEntity(DeferredCone)
	ShowEntity(DeferredSphere)
	ShowEntity(DeferredQuad)
	
	BeginRender(Tween, 4 Or 16) ; ~ Begin render light volumes and shadowmaps
	
	For l.Lights = Each Lights
		If (Not EntityHidden(l\OBJ)) Then ProcessLight(Cam, EntityX(l\OBJ, True), EntityY(l\OBJ, True), EntityZ(l\OBJ, True), EntityPitch(l\OBJ, True), EntityYaw(l\OBJ, True), l\Range, l\R, l\G, l\B, l\Fade * SecondaryLightOn, l\LightType, l\FOV, l\TanFOV, l\CastShadows, 0.008 * l\Scattering, Tween)
	Next
	
	For dl.DynamicLight = Each DynamicLight
		If (Not EntityHidden(dl\OBJ)) And (GetParent(dl\OBJ) = 0 Lor (Not EntityHidden(GetParent(dl\OBJ)))) Then ProcessLight(Cam, EntityX(dl\OBJ, True), EntityY(dl\OBJ, True), EntityZ(dl\OBJ, True), EntityPitch(dl\OBJ, True), EntityYaw(dl\OBJ, True), dl\Range, dl\R, dl\G, dl\B, dl\Fade, dl\LightType, dl\FOV, dl\TanFOV, dl\CastShadows, 0.008 * dl\Scattering, Tween)
	Next
	
	If KeyDown(34) Then ProcessLight(Cam, EntityX(Cam), EntityY(Cam), EntityZ(Cam), EntityPitch(Cam), EntityYaw(Cam), 25.0, 200, 200, 200, 1.0, DEFERRED_LIGHT_SPOT, 90.0, DEFERRED_LIGHT_POINT_CULLING_SCALE, False, 0.0, Tween)
	
	EndRender()
	
	HideEntity(DeferredCone)
	HideEntity(DeferredSphere)
	HideEntity(DeferredQuad)
	
	CameraClsMode(Cam, 1, 1)
	CameraRange(Cam, Near, Far)
	
	If DirectionalLightUpdate < MilliSecs() Then DirectionalLightUpdate = MilliSecs() + DIRECTIONAL_LIGHT_TIME
End Function

Function ProcessLight%(Cam%, x#, y#, z#, Pitch#, Yaw#, Range#, R%, G%, B%, Intensity#, LightType%, FOV# = 90.0, TanFOV# = 1.0, CastShadows% = True, Scattering# = 0.01, Tween# = 1.0)
	Local VolumeScale# = Range * 1.25
	Local Volume%
	Local DistToLight# = 1.0
	Local EffectBits = 0
	
	If CastShadows Then EffectBits = EffectBits Or DEFERRED_SHADE_SHADOWS
	If Scattering > 0.0 Then EffectBits = EffectBits Or DEFERRED_SHADE_SCATTERING
	
	Local DeferredShade% = GetShadeEffect(EffectBits)
	
	Select LightType
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			DistToLight = EntityDistance(Cam, Volume)
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, DeferredShadowMapCube[GetShadowMapMip(Range, DistToLight)], LightType, x, y, z, Pitch, Yaw, Range, FOV, TanFOV, Tween)
			
			EffectTechnique(DeferredShade, "PointLight")
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			If Scattering > 0.0
				Volume = DeferredSphere
				
				PositionEntity(Volume, x, y, z)
				ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			Else
				VolumeScale = TanFOV * Range
				Volume = DeferredCone
				PositionEntity(Volume, x, y, z)
				RotateEntity(Volume, Pitch, Yaw, 0.0)
				ScaleEntity(Volume, VolumeScale, VolumeScale, Range)
			EndIf
			
			If (Not EntityInView(Volume, Cam)) Then Return
			DistToLight = EntityDistance(Cam, Volume)
			
			Local Shadowmap% = DeferredShadowMap[GetShadowMapMip(Range, DistToLight)]
			
			PositionEntity(DeferredCamera, x, y, z)
			RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
			CameraRange(DeferredCamera, 0.01, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1.0 / TanFOV)
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(Shadowmap), TextureHeight(Shadowmap))
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, 0.5)
			
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, Shadowmap, LightType, x, y, z, Pitch, Yaw, Range, FOV, TanFOV, Tween)
			
			EffectTechnique(DeferredShade, "SpotLight")
			EffectMatrix(DeferredShade, "LightViewProj", CameraMatrix(DeferredCamera, 2, Tween))
			EffectVector(DeferredShade, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range) + VolumeScale)
			;[End Block]
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = DeferredQuad
			
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, DeferredShadowMap[0], LightType, x, y, z, Pitch, Yaw, Range, FOV, TanFOV, Tween)
			
			Tween = 1.0
			Cam = QuadCamera
			
			EffectTechnique(DeferredShade, "DirLight")
			EffectMatrix(DeferredShade, "LightViewProj", CameraMatrix(DeferredCamera, 2, Tween))
			EffectVector(DeferredShade, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
			;[End Block]
	End Select
	
	EffectVector(DeferredShade, "LightPos", x, y, z)
	EffectVector(DeferredShade, "LightColor", R / 255.0 * Intensity, G / 255.0 * Intensity, B / 255.0 * Intensity)
	EffectFloat(DeferredShade, "LightRange", Range)
	EffectFloat(DeferredShade, "LightScattering", Scattering)
	EntityEffect(Volume, DeferredShade)
	
	RenderEntity(Cam, Volume, Tween)
End Function

Global DEFERRED_LIGHT_POINT_CULLING_SCALE# = Tan(90.0 * 0.5)

Function RenderShadowMap%(DeferredShade%, MainCam%, ShadowMap%, LightType%, x#, y#, z#, Pitch#, Yaw#, Range#, FOV#, TanFOV# = 1.0, Tween# = 1.0)
	Local ShadowMapWidth% = TextureWidth(ShadowMap)
	Local ShadowMapHeight% = TextureHeight(ShadowMap)
	Local DummyTexture% = FindDummyTexture(ShadowMapWidth, ShadowMapHeight)
	Local i%
	
	If DummyTexture = 0 Then DebugLog("Unknown texture error" + ShadowMapWidth + " " + ShadowMapHeight)
	
	SetBuffer(TextureBuffer(DummyTexture))
	
	Select LightType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			If DirectionalLightUpdate < MilliSecs() Lor DirectionalLightUpdate = 0
				PositionEntity(DeferredCamera, x, y, z)
				RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
				MoveEntity(DeferredCamera, 0.0, 0.0, -DIRECTIONAL_LIGHT_EXTRUSION)
			EndIf
			
			CameraDepthBias(DeferredCamera, SHADOW_BIAS * 18, 0.5)
			CameraRange(DeferredCamera, 0.1, DIRECTIONAL_LIGHT_EXTRUSION + 15.0)
			CameraProjMode(DeferredCamera, 2)
			CameraZoom(DeferredCamera, DIRECTIONAL_LIGHT_RANGE)
			CameraViewport(DeferredCamera, 0, 0, ShadowMapWidth, ShadowMapHeight)
			
			SetBuffer(TextureBuffer(ShadowMap))
			RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
			EffectInt(DeferredShade, "ShadowMapAddress", 4)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
			PositionEntity(DeferredCamera, x, y, z)
			RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
			CameraRange(DeferredCamera, 0.01, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1)
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, 0.5)
			
			SetBuffer(TextureBuffer(ShadowMap))
			
			Local CullingScale# = DEFERRED_LIGHT_POINT_CULLING_SCALE * Range
			
			PositionEntity(DeferredCone, x, y, z)
			
			For i = 0 To 5
				RotateEntity(DeferredCone, CubeRotateX[i], CubeRotateY[i], 0.0)
				ScaleEntity(DeferredCone, CullingScale, CullingScale, Range)
				
				If EntityInView(DeferredCone, MainCam)
					RotateEntity(DeferredCamera, CubeRotateX[i], CubeRotateY[i], 0.0)
					CameraViewport(DeferredCamera, i * ShadowMapWidth / 6, 0, ShadowMapWidth / 6, ShadowMapHeight)
					RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
					EffectMatrix(DeferredShade, "LightViewProj" + i, CameraMatrix(DeferredCamera, 2, Tween)) ; ~ Push matrix for each face
				EndIf
			Next
			EffectInt(DeferredShade, "ShadowMapAddress", 3)
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			SetBuffer(TextureBuffer(ShadowMap))
			RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
			EffectInt(DeferredShade, "ShadowMapAddress", 3)
			;[End Block]
	End Select
	
	EffectVector(DeferredShade, "ShadowMapSize", ShadowMapWidth, ShadowMapHeight)
	EffectTexture(DeferredShade, "tShadowMap", ShadowMap)
	
	SetBuffer(TextureBuffer(MRTColor))
End Function

Function CreateShadowMap%(Width%, Height%)
	Return(CreateTexture(Width, Height, 524288))
End Function

Function CreateDummyTexture%(Width%, Height%)
	If FindDummyTexture(Width, Height) <> 0 Then Return
	
	Local t.DummyTexture = New DummyTexture
	
	t\Tex = CreateTexture(Width, Height, 1 + 256 + 16384)
End Function

Function FindDummyTexture%(Width%, Height%)
	Local dt.DummyTexture
	
	For dt.DummyTexture = Each DummyTexture
		If TextureWidth(dt\Tex) = Width And TextureHeight(dt\Tex) = Height Then Return(dt\Tex)
	Next
	Return(0)
End Function

Function CreateLightVolume%(LightType%)
	Local Volume%, SF%
	
	Select LightType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = CreateFullscreenQuad(QuadCamera)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = CreateSphere(5)
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			Volume = CreateMesh()
			SF = CreateSurface(Volume)
			
			AddVertex(SF, 0.00001, 0.00001, 0.00001)
			AddVertex(SF, 0.00001, -0.00001, 0.00001)
			AddVertex(SF, -0.00001, -0.00001, 0.00001)
			AddVertex(SF, -0.00001, 0.00001, 0.00001)
			AddVertex(SF, 1.00000, 1.00000, 0.99999)
			AddVertex(SF, 1.00000, -1.00000, 0.99999)
			AddVertex(SF, -1.00000, -1.00000, 0.99999)
			AddVertex(SF, -1.00000, 1.00000, 0.99999)
			
			AddTriangle(SF, 3, 0, 1)
			AddTriangle(SF, 3, 1, 2)
			AddTriangle(SF, 0, 4, 5)
			AddTriangle(SF, 0, 5, 1)
			AddTriangle(SF, 3, 7, 4)
			AddTriangle(SF, 3, 4, 0)
			AddTriangle(SF, 7, 3, 2)
			AddTriangle(SF, 7, 2, 6)
			AddTriangle(SF, 6, 2, 1)
			AddTriangle(SF, 6, 1, 5)
			AddTriangle(SF, 7, 5, 4)
			AddTriangle(SF, 7, 6, 5)
			;[End Block]
	End Select
	Return(Volume)
End Function

Function SetGBufferBlur%(Blur#)
	GBufferBlur = Blur
End Function

Function SetShadowsDistance%(Dist#)
	ShadowsDistance = Dist
End Function

Function SetShadowsBias%(Bias#)
	SHADOW_BIAS = Bias
End Function

Function SetEmissiveMultiply%(em#)
	If EmissiveMultiply <> em
		Local ef.InputEffect
		
		For ef.InputEffect = Each InputEffect
			If (ef\Bit And DEFERRED_DIFFEMISSIVEMUL) Then EffectFloat(ef\Effect, "EmissiveMultiply", em)
		Next
		EmissiveMultiply = em
	EndIf
End Function

Function GetShadowMapMip%(Range#, Dist#)
	Local MipLevel% = Floor((Dist / (Range + ShadowsDistance)) * (SHADOW_MAP_MIPMAPS - 1))
	
	Return(Min(Max(MipLevel, 0), (SHADOW_MAP_MIPMAPS - 1)))
End Function

; ==================================== DYNAMIC LIGHTS

Type DynamicLight
	Field OBJ%
	Field LightType%
	Field R%, G%, B%
	Field Range#
	Field Fade#
	Field FOV#, TanFOV#
	Field Scattering#
	Field CastShadows%
End Type

Function FindDynamicLight.DynamicLight(OBJ%)
	Local dl.DynamicLight
	
	For dl.DynamicLight = Each DynamicLight
		If dl\OBJ = OBJ Then Return(dl)
	Next
End Function

Function CreateLight%(LightType%, Parent% = 0)
	Local dl.DynamicLight = New DynamicLight
	
	dl\OBJ = CreatePivot(Parent)
	dl\LightType = LightType
	dl\Fade = 1.0
	dl\R = 255
	dl\G = 255
	dl\B = 255
	dl\Range = 10.0
	dl\FOV = 90.0
	dl\TanFOV = (LightType <> 3) + ((LightType = 3) * Tan(dl\FOV * 0.5))
	EntityDestructor(dl\OBJ, @OnLightDestruct)
	
	Return(dl\OBJ)
End Function

Function LightRange%(Entity%, Range#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\Range = Range
End Function

Function LightColor%(Entity%, R%, G%, B%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null
		dl\R = R
		dl\G = G
		dl\B = B
	EndIf
End Function

Function LightFOV%(Entity%, FOV#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\FOV = FOV
End Function

Function LightCastShadows%(Entity%, CastShadows%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\CastShadows = CastShadows
End Function

Function LightScattering%(Entity%, Scattering#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\Scattering = Scattering
End Function

Function OnLightDestruct%(Entity%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Delete(dl)
End Function

; ====================================

Function LoadInputEffect%(Bit%, File$, Defines$ = "")
	DeferredInputEffect[Bit] = New InputEffect
	DeferredInputEffect[Bit]\Effect = LoadEffectEx(File, Defines)
	DeferredInputEffect[Bit]\Bit = Bit
End Function

Function LoadShadeEffect%(Bit%, File$, Defines$ = "")
	DeferredShadeEffect[Bit] = New ShadeEffect
	DeferredShadeEffect[Bit]\Effect = LoadEffectEx(File, Defines)
	DeferredShadeEffect[Bit]\Bit = Bit
End Function

Function GetInputEffect%(Bit%)
	If DeferredInputEffect[Bit] = Null Then Return(0)
	Return(DeferredInputEffect[Bit]\Effect)
End Function

Function GetShadeEffect%(Bit%)
	If DeferredShadeEffect[Bit] = Null Then Return(0)
	Return(DeferredShadeEffect[Bit]\Effect)
End Function

Function LoadEffectEx%(File$, Defines$ = "")
	Local f% = ReadFile(File)
	
	If f = 0 Then Return
	
	If Defines = "" Then Return(LoadEffect(File))
	
	Local Export$ = StripFileName(File) + "TEMP_EFFECT_FILE.fx"
	Local c% = WriteFile(Export)
	Local i%
	
	If c <> 0
		Local StringsAmount% = CountSplitString(Defines, " ")
		
		For i = 0 To StringsAmount - 1
			WriteLine(c, "#define " + SplitString(Defines, " ", i))
		Next
		While (Not Eof(f))
			WriteLine(c, ReadLine(f))
		Wend
		CloseFile(c)
	EndIf
	
	Local Effect% = LoadEffect(Export)
	
	CloseFile(f)
	DeleteFile(Export)
	
	Return(Effect)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS