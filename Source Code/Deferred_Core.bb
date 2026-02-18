Const DEFERRED_LIGHT_DIRECTIONAL% = 1
Const DEFERRED_LIGHT_POINT% = 2
Const DEFERRED_LIGHT_SPOT% = 3

Const DEFERRED_DIFF% = 0
Const DEFERRED_DIFFSKYBOX% = $0001
Const DEFERRED_DIFFNORMAL% = $0002
Const DEFERRED_DIFFROUGH% = $0004
Const DEFERRED_DIFFEMISSIVE% = $0008
Const DEFERRED_DIFFEMISSIVEMUL% = $0010
Const DEFERRED_FULLBRIGHT% = $0020
Const DEFERRED_TRANSPARENT% = $0040
Const DEFERRED_DIFFENVMAP% = $0080
Const DEFERRED_DIFFHEIGHTMAP% = $0100
Const DEFERRED_MASKED% = $0200
Const DEFERRED_DISABLEFOG% = $0400
Const DEFERRED_FAKECURVE% = $0800	
Const DEFERRED_LOCALTRANSFORM% = $1000

Const DEFERRED_ADDITIVE% = $8000
Const DEFERRED_NOMATERIAL% = $10000

Const MAX_DEFERRED_VARIATIONS% = DEFERRED_LOCALTRANSFORM Shl 1

Const DEFERRED_SHADE_DIRLIGHT% = $0001
Const DEFERRED_SHADE_POINTLIGHT% = $0002
Const DEFERRED_SHADE_SPOTLIGHT% = $0004
Const DEFERRED_SHADE_SHADOWS% = $0008
Const DEFERRED_SHADE_SCATTERING% = $0010

Const MAX_DEFERRED_SHADE_VARIATIONS% = DEFERRED_SHADE_SCATTERING Shl 1

Const DIRECTIONAL_LIGHT_TIME% = 0
Const DIRECTIONAL_LIGHT_RANGE# = 0.01
Const DIRECTIONAL_LIGHT_EXTRUSION# = 20.0
Global SHADOW_BIAS# = 0.00044
Global NORMAL_OFFSET# = 1.0
Global SLOPE_BIAS# = 2.0

Const SHADOW_MAP_MIPMAPS% = 1 ; ~ Don't change this
Const SHADOW_MAP_SIZE% = 512
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

Type InputEffectVariation
	Field Define$
	Field Bit%
End Type

Type ShadeEffectVariation
	Field Define$
	Field Bit%
End Type

Type DummyTexture
	Field Tex%
End Type

Type EnvMap
	Field Name$
	Field Texture%
End Type

Global CurrentEnvMap.EnvMap
Global PreviousEnvMap.EnvMap

Global DeferredInputEffect.InputEffect[MAX_DEFERRED_VARIATIONS]
Global DeferredShadeEffect.ShadeEffect[MAX_DEFERRED_SHADE_VARIATIONS]

Global DeferredShadowMapCube%[SHADOW_MAP_MIPMAPS + 1]
Global DeferredShadowMap%[SHADOW_MAP_MIPMAPS + 1]
Global TextureDummies.DummyTexture

Global DeferredCamera%, QuadCamera%
Global DeferredSphere%, DeferredCone%, DeferredQuad%
Global DirectionalLightUpdate%
Global ShadowsDistance#, ShadowsMipDistance#, ShadowsFade#
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

Global EmissiveMultiply#, EnvBlendFactor#

Const LIGHTING_DEFERRED% = 0
Const LIGHTING_PREPASS% = 1

Global LIGHTING_TYPE% = LIGHTING_PREPASS

Global GlobalEnvironmentMap%, BlendEnvironmentMap%

Function InitDeferred%()
	Local Width% = opt\GraphicWidth
	Local Height% = opt\GraphicHeight
	Local i%
	
	ClearDeferred()

	CreateInputVariation(DEFERRED_DIFFSKYBOX, "SKYBOX")
	CreateInputVariation(DEFERRED_DIFFNORMAL, "NORMALMAP")
	CreateInputVariation(DEFERRED_DIFFROUGH, "ROUGHMAP")
	CreateInputVariation(DEFERRED_DIFFEMISSIVE, "EMISSIVEMAP")
	CreateInputVariation(DEFERRED_DIFFEMISSIVEMUL, "MUL")
	CreateInputVariation(DEFERRED_DIFFENVMAP, "ENVMAP")
	CreateInputVariation(DEFERRED_DIFFHEIGHTMAP, "HEIGHTMAP")
	CreateInputVariation(DEFERRED_FULLBRIGHT, "FULLBRIGHT")
	CreateInputVariation(DEFERRED_TRANSPARENT, "TRANSPARENT")
	CreateInputVariation(DEFERRED_MASKED, "MASKED")
	CreateInputVariation(DEFERRED_DISABLEFOG, "DISABLEFOG")
	CreateInputVariation(DEFERRED_FAKECURVE, "FAKECURVE")
	CreateInputVariation(DEFERRED_LOCALTRANSFORM, "LOCALTRANSFORM")
	
	CreateShadeVariation(DEFERRED_SHADE_DIRLIGHT, "DIRLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_POINTLIGHT, "POINTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SPOTLIGHT, "SPOTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SHADOWS, "SHADOWS")
	CreateShadeVariation(DEFERRED_SHADE_SCATTERING, "SCATTERING")
	
	MRTColor = CreateTexture(Width, Height, 131072)
	MRTAlbedo = CreateTexture(Width, Height, 1 + 2 + 1024)
	MRTDepth = CreateTexture(Width, Height, 2048)
	MRTNormal = CreateTexture(Width, Height, 4096)
	
	For i = 0 To SHADOW_MAP_MIPMAPS - 1
		DeferredShadowMapCube[i] = CreateShadowMap((SHADOW_MAP_SIZE * 6) Shr i, SHADOW_MAP_SIZE Shr i)
		DeferredShadowMap[i] = CreateShadowMap(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
		
		CreateDummyTexture((SHADOW_MAP_SIZE * 6) Shr i, SHADOW_MAP_SIZE Shr i)
		CreateDummyTexture(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
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
	CameraClsMode(DeferredCamera, 0, 0)
	CameraColorWrite(DeferredCamera, False)
	CameraReverseZ(DeferredCamera, False)
	HideEntity(DeferredCamera)
	
	Local SpotTexture% = LoadTexture("GFX\Shaders\spot.png")
	Local RampTexture% = LoadTexture("GFX\Shaders\ramp.png")
	
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
	FreeTexture(FaceSelectCubeMap) : FaceSelectCubeMap = 0
	
	HideEntity(DeferredSphere)
	HideEntity(DeferredCone)
	HideEntity(DeferredQuad)
	
	; ~ Volumes mask
	MaskEntity(DeferredSphere, 4)
	MaskEntity(DeferredCone, 4)
	MaskEntity(DeferredQuad, 4)
	
	SetShadowsMipDistance(3.0)
	SetShadowsDistance(6.0, 0.3)
	SetShadowsBias(0.0001, 0.0)
	
	DirectionalLightUpdate = 0
	SetEmissiveMultiply(1.0)
	
	TempColorTexture = CreateTexture(Width, Height, 131072)
	GlobalEnvironmentMap = CreateTexture(1024, 1024, 1 + 8 + 128)
	BlendEnvironmentMap = CreateTexture(1024, 1024, 1 + 8 + 128)
End Function

Function PreloadShaders%()
	; ~ Preload shading
	GetShadeEffect(DEFERRED_SHADE_DIRLIGHT)
	GetShadeEffect(DEFERRED_SHADE_DIRLIGHT Or DEFERRED_SHADE_SCATTERING)
	GetShadeEffect(DEFERRED_SHADE_DIRLIGHT Or DEFERRED_SHADE_SHADOWS)
	GetShadeEffect(DEFERRED_SHADE_DIRLIGHT Or DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_SHADOWS)
	
	GetShadeEffect(DEFERRED_SHADE_POINTLIGHT)
	GetShadeEffect(DEFERRED_SHADE_POINTLIGHT Or DEFERRED_SHADE_SCATTERING)
	GetShadeEffect(DEFERRED_SHADE_POINTLIGHT Or DEFERRED_SHADE_SHADOWS)
	GetShadeEffect(DEFERRED_SHADE_POINTLIGHT Or DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_SHADOWS)
	
	GetShadeEffect(DEFERRED_SHADE_SPOTLIGHT)
	GetShadeEffect(DEFERRED_SHADE_SPOTLIGHT Or DEFERRED_SHADE_SCATTERING)
	GetShadeEffect(DEFERRED_SHADE_SPOTLIGHT Or DEFERRED_SHADE_SHADOWS)
	GetShadeEffect(DEFERRED_SHADE_SPOTLIGHT Or DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_SHADOWS)
End Function

Function UpdateShaders%()
	Local se.ShadeEffect, ef.InputEffect
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
	
	For ef.InputEffect = Each InputEffect
		If ef\Effect <> 0 Then EffectTexture(ef\Effect, "tBlendEnvMap", BlendEnvironmentMap)
	Next
	
	FreeBank(AdjustMatrix) : AdjustMatrix = 0
End Function

Function ClearDeferred%()
	Delete Each EffectHash
	Delete Each DummyTexture
	Delete Each DynamicLight
	Delete Each EnvMap
	CurrentEnvMap = Null
	PreviousEnvMap = Null
End Function

Function SetDeferredParticle%(Entity%, Enable% = True)
	SetDeferredEntity(Entity, False, DEFERRED_TRANSPARENT) ;Enable, DEFERRED_TRANSPARENT)
End Function

Function SetShadowsCasting%(Entity%, Enable%)
	MaskEntity(Entity, 1 + (15 * Enable))
End Function

Function SetDeferredEntity%(Entity%, CastShadows% = False, State% = -1)
	
	If EntityClass(Entity) = "Mesh"
		Local SurfCount% = CountSurfaces(Entity)
		Local i%, SF%, b%
		
		For i = 1 To SurfCount
			SF = GetSurface(Entity, i)
			b = GetSurfaceBrush(SF)
			If b <> 0
				SetDeferredBrush(b, State)
				PaintSurface(SF, b)
				FreeBrush(b) : b = 0
			EndIf
		Next
	Else
		UpdateEntityMaterial(Entity, State)
	EndIf
	
	SetShadowsCasting(Entity, CastShadows)
End Function

Function SetDeferredBrush%(Brush%, State = -1, Frame% = 0)
	Local Customized% = ((State And (DEFERRED_NOMATERIAL Or DEFERRED_ADDITIVE)) <> 0) And State <> -1
	
	If State = -1 Lor Customized
		If (Not Customized) Then State = DEFERRED_DIFF
		
		Local t1% = GetBrushTexture(Brush, 0)
		Local mat.Materials
		
		If t1 <> 0
			Local TexName$ = TextureName(t1)
			
			mat.Materials = GetMaterial(t1)
			If mat <> Null
				LoadMaterialTextures(mat)
				
				If HasMaterialTexture(mat, MATERIAL_NORMAL) Then State = State Or DEFERRED_DIFFNORMAL
				If HasMaterialTexture(mat, MATERIAL_ROUGHNESS) Then State = State Or DEFERRED_DIFFROUGH
				If HasMaterialTexture(mat, MATERIAL_EMISSIVE) Then State = State Or DEFERRED_DIFFEMISSIVE
				If HasMaterialTexture(mat, MATERIAL_ENVMAP) Then State = State Or DEFERRED_DIFFENVMAP
				If HasMaterialTexture(mat, MATERIAL_HEIGHTMAP) Then State = State Or DEFERRED_DIFFHEIGHTMAP
				
				If mat\ReactBlackout <> 0 Then State = State Or DEFERRED_DIFFEMISSIVEMUL
				If mat\IsDiffuseAlpha Then State = State Or DEFERRED_TRANSPARENT
				If mat\UseMask Then State = State Or DEFERRED_MASKED
				If mat\FakeCurve Then State = State Or DEFERRED_FAKECURVE
				
				BrushTexture(Brush, GetMaterialTexture(mat, MATERIAL_NORMAL), 0, MATERIAL_NORMAL)
				BrushTexture(Brush, GetMaterialTexture(mat, MATERIAL_ROUGHNESS), 0, MATERIAL_ROUGHNESS)
				BrushTexture(Brush, GetMaterialTexture(mat, MATERIAL_EMISSIVE), Frame, MATERIAL_EMISSIVE)
				BrushTexture(Brush, GetMaterialTexture(mat, MATERIAL_ENVMAP), 0, MATERIAL_ENVMAP)
				BrushTexture(Brush, GetMaterialTexture(mat, MATERIAL_HEIGHTMAP), 0, MATERIAL_HEIGHTMAP)
				BrushShininess(Brush, 0, 0)
				
				If mat\RMSpecified
					BrushMaterial(Brush, mat\Roughness, mat\Metallic)
				ElseIf HasMaterialTexture(mat, MATERIAL_ROUGHNESS)
					BrushMaterial(Brush, 0.0, 0.0)
				EndIf
			ElseIf (State And DEFERRED_NOMATERIAL) = 0
				BrushMaterial(Brush, 1.0, 0.0)
			EndIf
			FreeTexture(t1) : t1 = 0
			
			If mat = Null And (State And DEFERRED_NOMATERIAL) <> 0 And TexName = "" Then Return ; ~ Don't set effect if can't find material
		EndIf
	EndIf
	
	State = State And (State Xor (DEFERRED_ADDITIVE Or DEFERRED_NOMATERIAL)) ; ~ Remove customized
	BrushEffect(Brush, GetInputEffect(State))
End Function

Function UpdateEntityMaterial%(Entity%, State% = -1, Frame% = 0)
	If EntityClass(Entity) = "Pivot" Then Return
	
	Local Brush% = GetEntityBrush(Entity)
	
	SetDeferredBrush(Brush, State, Frame)
	PaintEntity(Entity, Brush)
	FreeBrush(Brush) : Brush = 0
End Function

Function ProcessDeferred%(Cam%, Tween# = 1.0)
	If GetInputEffect(DEFERRED_DIFF) <> 0
		Local ef.InputEffect, se.ShadeEffect
		
		CurrTrisAmount = 0
		BatchesAmount = 0
		
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "Deferred")
		Next
		
		ClearBuffer(TextureBuffer(MRTColor), fog\R, fog\G, fog\B, 255)
		ClearBuffer(TextureBuffer(MRTAlbedo), 0.0, 0.0, 0.0, 255.0)
		ClearBuffer(TextureBuffer(MRTNormal), 0.0, 0.0, 0.0, 0.0)
		ClearBuffer(TextureBuffer(MRTDepth), 0.0, 0.0, 0.0, 0.0)
		SetBuffer(TextureBuffer(MRTColor))
		SetBuffer(TextureBuffer(MRTAlbedo), 0, 1)
		SetBuffer(TextureBuffer(MRTNormal), 0, 2)
		SetBuffer(TextureBuffer(MRTDepth), 0, 3)
		CameraClsMode(Cam, 0, 1)
		
		Local Brightness# = Lerp(opt\ScreenGamma, 1.0, 0.5) * 0.5
		
		AmbientLight(Min(fog\CurrAmbientR * Brightness, 255.0), Min(fog\CurrAmbientG * Brightness, 255.0), Min(fog\CurrAmbientB * Brightness, 255.0))
		; ~ Render opacity
		WireFrame(WireFrameState)
		RenderWorld(Tween, Cam, -1, 1) ; ~ Render only opacity
		Count3D()
		ProcessSSAO(Cam, 2.0, 0.05, 1.0, Tween) ; ~ Process SSAO for opacity
		
		Local InvViewProjection% = CameraMatrix(Cam, 3, Tween)
		
		For se.ShadeEffect = Each ShadeEffect
			EffectMatrix(se\Effect, "InvViewProj", InvViewProjection)
		Next
		
		WireFrame(0)
		ProcessAllLights(Cam, Tween)
		
		CameraClsMode(Cam, 0, 0)
		
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "Deferred")
		Next
		
		ProcessFog(fog\R, fog\G, fog\B)
		
		WireFrame(WireFrameState)
		RenderWorld(Tween, Cam, -1, 2)
		Count3D()
		
		CameraClsMode(Cam, 1, 1)
		WireFrame(0)
		
		ProcessBloom(1.0)
		ProcessFXAA()
		;ProcessSSGI(Cam, 0.5, 1.5, Tween) ; ~ Unstable
		ProcessColorCorrection()
		ProcessMotionBlur(Cam, 1.0, Tween)
		;ProcessGamma(Lerp(opt\ScreenGamma, 1.0, 0.5))
		PresentGBuffer(MRTColor, BackBuffer())
		SetBuffer(BackBuffer())
	Else
		RenderWorld(Tween)
		Count3D()
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
		If (Not EntityHidden(l\OBJ)) Then ProcessLight(Cam, EntityX(l\OBJ, True, Tween), EntityY(l\OBJ, True, Tween), EntityZ(l\OBJ, True, Tween), EntityPitch(l\OBJ, True, Tween), EntityYaw(l\OBJ, True, Tween), l\Range, l\R, l\G, l\B, l\Fade * Min(SecondaryLightOn, 1.0) * 1.25, l\LightType, l\FOV, (l\CastShadows And (opt\LightingQuality > 1)), 0.005 * l\Scattering * opt\VolumetricLights, Tween)
	Next
	
	For dl.DynamicLight = Each DynamicLight
		If (Not EntityHidden(dl\OBJ)) And (GetParent(dl\OBJ) = 0 Lor (Not EntityHidden(GetParent(dl\OBJ)))) Then ProcessLight(Cam, EntityX(dl\OBJ, True, Tween), EntityY(dl\OBJ, True, Tween), EntityZ(dl\OBJ, True, Tween), EntityPitch(dl\OBJ, True, Tween), EntityYaw(dl\OBJ, True, Tween), dl\Range, dl\R, dl\G, dl\B, dl\Fade, dl\LightType, dl\FOV, (dl\CastShadows And (opt\LightingQuality > 0)), 0.005 * dl\Scattering * opt\VolumetricLights, Tween)
	Next
	
	If (wi\NVGPower > 0 Lor wi\NightVision = 3) And wi\NightVision > 0 Then ProcessLight(Cam, EntityX(Cam, True, Tween), EntityY(Cam, True, Tween), EntityZ(Cam, True, Tween), 0, 0, 2500.0 * LightRangeScale, 200, 200, 200, 1.5, DEFERRED_LIGHT_POINT, 90.0, False, 0.0, Tween)

	If KeyDown(34) And opt\DebugMode = 1 Then ProcessLight(Cam, EntityX(Cam, True, Tween), EntityY(Cam, True, Tween), EntityZ(Cam, True, Tween), EntityPitch(Cam, True, Tween), EntityYaw(Cam, True, Tween), 25.0, 200, 200, 200, 1.0, DEFERRED_LIGHT_SPOT, 60, False, 0.0, Tween)
	
	EndRender()
	
	HideEntity(DeferredCone)
	HideEntity(DeferredSphere)
	HideEntity(DeferredQuad)
	
	CameraClsMode(Cam, 1, 1)
	CameraRange(Cam, Near, Far)
	
	If DirectionalLightUpdate < MilliSecs() Then DirectionalLightUpdate = MilliSecs() + DIRECTIONAL_LIGHT_TIME
End Function

Function ProcessLight%(Cam%, x#, y#, z#, Pitch#, Yaw#, Range#, R%, G%, B%, Intensity#, LightType%, FOV# = 90.0, CastShadows% = True, Scattering# = 0.01, Tween# = 1.0)
	If Intensity <= 0.0 Then Return
	
	Local VolumeScale# = Range * 1.25
	Local Volume%, TanValue#, ShadowIntensity# = 1.0
	Local DistToLight# = Distance(EntityX(Cam, True), x, EntityY(Cam, True), y, EntityZ(Cam, True), z)
	
	If DistToLight - Range > GetCameraRangeFar(Cam) Then Return
	
	Local EffectBits% = GetShadeLight(LightType)

	If CastShadows And LightType <> DEFERRED_LIGHT_DIRECTIONAL
		ShadowIntensity = GetFade(Max(DistToLight - Range, 0), ShadowsDistance * ShadowsFade, ShadowsDistance)
		If ShadowIntensity <= 0.0 Then CastShadows = False
	EndIf
	
	If CastShadows Then EffectBits = EffectBits Or DEFERRED_SHADE_SHADOWS
	If Scattering > 0.0 Then EffectBits = EffectBits Or DEFERRED_SHADE_SCATTERING
	
	Local DeferredShade% = GetShadeEffect(EffectBits)

	If (Not CastShadows) Then EffectFloat(DeferredShade, "NormalOffset", 0.0)
	
	FOV = Clamp(FOV, 0.0, 170.0)
	
	Select LightType
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, DeferredShadowMapCube[GetShadowMapMip(Range, DistToLight)], LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			TanValue = Tan(FOV * 0.5)
			
			If 1
				Volume = DeferredSphere
				
				PositionEntity(Volume, x, y, z)
				ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			Else
				VolumeScale = TanValue * Range
				Volume = DeferredCone
				PositionEntity(Volume, x, y, z)
				RotateEntity(Volume, Pitch, Yaw, 0.0)
				ScaleEntity(Volume, VolumeScale, VolumeScale, Range)
			EndIf
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			Local Shadowmap% = DeferredShadowMap[GetShadowMapMip(Range, DistToLight)]
			
			PositionEntity(DeferredCamera, x, y, z)
			RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
			CameraRange(DeferredCamera, 0.005 * Range, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1.0 / TanValue)
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(Shadowmap), TextureHeight(Shadowmap))
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, SLOPE_BIAS)
			
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, Shadowmap, LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			
			EffectMatrix(DeferredShade, "LightViewProj", CameraMatrix(DeferredCamera, 2, Tween))
			EffectVector(DeferredShade, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
			If Scattering > 0.0
				CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			Else
				CameraRange(Cam, 0.01, 1000000)
			EndIf
			;[End Block]
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = DeferredQuad
			
			If CastShadows Then RenderShadowMap(DeferredShade, Cam, DeferredShadowMap[SHADOW_MAP_MIPMAPS], LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			
			Tween = 1.0
			Cam = QuadCamera
			
			EffectMatrix(DeferredShade, "LightViewProj", CameraMatrix(DeferredCamera, 2, Tween))
			EffectVector(DeferredShade, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
			;[End Block]
	End Select
	
	Intensity = Intensity * Lerp(opt\ScreenGamma, 1.0, 0.8)
	
	EffectVector(DeferredShade, "LightPos", x, y, z, 1.0 / Max(Range, 0.0001))
	EffectVector(DeferredShade, "LightColor", R / 255.0 * Intensity, G / 255.0 * Intensity, B / 255.0 * Intensity)
	EffectFloat(DeferredShade, "LightScattering", Scattering)
	EffectFloat(DeferredShade, "ShadowIntensity", 1.0 - ShadowIntensity)
	
	EntityEffect(Volume, DeferredShade)
	RenderEntity(Cam, Volume, Tween)
	Count3D()
End Function

Global DEFERRED_LIGHT_POINT_CULLING_SCALE_TAN# = Tan(90.0 * 0.5)

Function RenderShadowMap%(DeferredShade%, MainCam%, ShadowMap%, LightType%, x#, y#, z#, Pitch#, Yaw#, Range#, FOV#, Tween# = 1.0)
	Local ShadowMapWidth% = TextureWidth(ShadowMap)
	Local ShadowMapHeight% = TextureHeight(ShadowMap)
	Local DummyTexture% = FindDummyTexture(ShadowMapWidth, ShadowMapHeight)
	Local i%, ScaledNormalOffset#
	
	If DummyTexture = 0
		DebugLog("Unknown texture error: " + ShadowMapWidth + " " + ShadowMapHeight)
		Return
	EndIf
	
	SetBuffer(TextureBuffer(DummyTexture))
	
	Select LightType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			If DirectionalLightUpdate < MilliSecs() Lor DirectionalLightUpdate = 0
				PositionEntity(DeferredCamera, x, y, z)
				RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
				MoveEntity(DeferredCamera, 0.0, 0.0, -DIRECTIONAL_LIGHT_EXTRUSION)
			EndIf
			
			CameraDepthBias(DeferredCamera, SHADOW_BIAS * 18, SLOPE_BIAS)
			CameraRange(DeferredCamera, 0.1, DIRECTIONAL_LIGHT_EXTRUSION + 15.0)
			CameraProjMode(DeferredCamera, 2)
			CameraZoom(DeferredCamera, DIRECTIONAL_LIGHT_RANGE)
			CameraViewport(DeferredCamera, 0, 0, ShadowMapWidth, ShadowMapHeight)
			
			; ~ Clear depth
			SetBuffer(TextureBuffer(ShadowMap))
			ClsColor(255, 0, 0)
			Cls()
			ClsColor(0, 0, 0)
			; ~ Set dummy texture with depth
			SetBuffer(TextureBuffer(DummyTexture), TextureBuffer(ShadowMap))
			
			RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
			Count3D()
			EffectInt(DeferredShade, "ShadowMapAddress", 4)
			EffectFloat(DeferredShade, "NormalOffset", NORMAL_OFFSET)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
			PositionEntity(DeferredCamera, x, y, z)
			RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
			CameraRange(DeferredCamera, 0.005 * Range, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1)
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, SLOPE_BIAS)
			
			; ~ Clear depth
			SetBuffer(TextureBuffer(ShadowMap))
			ClsColor(255, 0, 0)
			Cls()
			ClsColor(0, 0, 0)
			; ~ Set dummy texture with depth
			SetBuffer(TextureBuffer(DummyTexture), TextureBuffer(ShadowMap))
			
			Local Width% = ShadowMapWidth / 6
			Local Height% = ShadowMapHeight
			Local CullingScale# = DEFERRED_LIGHT_POINT_CULLING_SCALE_TAN * Range
			
			PositionEntity(DeferredCone, x, y, z)
			
			For i = 0 To 5
				RotateEntity(DeferredCone, CubeRotateX[i], CubeRotateY[i], 0.0)
				ScaleEntity(DeferredCone, CullingScale, CullingScale, Range)
				
				If EntityInView(DeferredCone, MainCam)
					RotateEntity(DeferredCamera, CubeRotateX[i], CubeRotateY[i], 0.0)
					CameraViewport(DeferredCamera, i * Width, 0, Width, Height)
					RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
					Count3D()
					EffectMatrix(DeferredShade, "LightViewProj" + i, CameraMatrix(DeferredCamera, 2, Tween)) ; ~ Push matrix for each face
				EndIf
			Next
			
			ScaledNormalOffset = 2.0 * CullingScale
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			
			EffectFloat(DeferredShade, "NormalOffset", ScaledNormalOffset)
			EffectInt(DeferredShade, "ShadowMapAddress", 3)
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			; ~ Clear depth
			SetBuffer(TextureBuffer(ShadowMap))
			ClsColor(255, 0, 0)
			Cls()
			ClsColor(0, 0, 0)
			; ~ Set dummy texture with depth
			SetBuffer(TextureBuffer(DummyTexture), TextureBuffer(ShadowMap))
			RenderWorld(Tween, DeferredCamera, 16) ; ~ Render only 16 mask
			Count3D()
			
			ScaledNormalOffset = 2.0 * Tan(FOV * 0.5) * Range
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			
			EffectFloat(DeferredShade, "NormalOffset", ScaledNormalOffset)
			EffectInt(DeferredShade, "ShadowMapAddress", 3)
			;[End Block]
	End Select
	
	EffectVector(DeferredShade, "ShadowMapSize", ShadowMapWidth, ShadowMapHeight)
	EffectTexture(DeferredShade, "tShadowMap", ShadowMap)
	
	SetBuffer(TextureBuffer(MRTColor))
End Function

Function CreateShadowMap%(Width%, Height%)
	Return(CreateTexture(Width, Height, 8192))
End Function

Function CreateDummyTexture%(Width%, Height%)
	If FindDummyTexture(Width, Height) <> 0 Then Return
	
	Local t.DummyTexture = New DummyTexture
	
	t\Tex = CreateTexture(Width, Height, 1 + 256 + 1024)
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

Function SetShadowsMipDistance%(Dist#)
	ShadowsMipDistance = Dist
End Function

Function SetShadowsDistance%(Dist#, Fade#)
	ShadowsDistance = Dist
	ShadowsFade = 1.0 - Fade
End Function

Function SetShadowsBias%(Bias#, Normal#)
	SHADOW_BIAS = Bias
	NORMAL_OFFSET = Normal
End Function

Function GetEmissiveMultiply#()
	Return(EmissiveMultiply)
End Function

Function GetEnvBlendFactor#()
	Return(EnvBlendFactor)
End Function

Function SetEmissiveMultiply%(Value#)
	If EmissiveMultiply <> Value
		Local ef.InputEffect
		
		EmissiveMultiply = Value
		For ef.InputEffect = Each InputEffect
			If (ef\Bit And DEFERRED_DIFFEMISSIVEMUL) Then EffectFloat(ef\Effect, "EmissiveMultiply", EmissiveMultiply)
		Next
	EndIf
End Function

Function SetEnvBlendFactor(Value#)
	If EnvBlendFactor <> Value
		Local ef.InputEffect
		
		EnvBlendFactor = Value
		For ef.InputEffect = Each InputEffect
			If (ef\Bit And DEFERRED_DIFFENVMAP) Then EffectFloat(ef\Effect, "EnvBlendFactor", EnvBlendFactor)
		Next
	EndIf
End Function

Function GetShadowMapMip%(Range#, Dist#)
	Local MipLevel% = Floor((Dist / (Range + ShadowsMipDistance)) * (SHADOW_MAP_MIPMAPS - 1))
	
	Return(Min(Max(MipLevel, 0), (SHADOW_MAP_MIPMAPS - 1)))
End Function

; ==================================== DYNAMIC LIGHTS

Type DynamicLight
	Field OBJ%
	Field LightType%
	Field R%, G%, B%
	Field Range#
	Field Fade#
	Field FOV#
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
	If DeferredInputEffect[Bit] <> Null Then Return(True)
	
	DeferredInputEffect[Bit] = New InputEffect
	DeferredInputEffect[Bit]\Effect = LoadEffectEx(DEFERRED_PATH + File, Defines)
	DeferredInputEffect[Bit]\Bit = Bit
	UpdateShaders()
	Return(True)
End Function

Function LoadShadeEffect%(Bit%, File$, Defines$ = "")
	If DeferredShadeEffect[Bit] <> Null Then Return(True)
	
	DeferredShadeEffect[Bit] = New ShadeEffect
	DeferredShadeEffect[Bit]\Effect = LoadEffectEx(DEFERRED_PATH + File, Defines)
	DeferredShadeEffect[Bit]\Bit = Bit
	UpdateShaders()
	Return(True)
End Function

Function GetInputEffect%(Bit%)
	If DeferredInputEffect[Bit] = Null
		Local v.InputEffectVariation
		Local Defines$ = "", FoundBits% = 0
		
		For v.InputEffectVariation = Each InputEffectVariation
			If (v\Bit And Bit) <> 0
				Defines = Defines + v\Define + " "
				FoundBits = FoundBits Or v\Bit
			EndIf
		Next
		
		If FoundBits <> Bit Then Return(0)
		
		If (Not LoadInputEffect(Bit, "Input.fx", Defines + "REVERSEDZ")) Then Return(0)
	EndIf
	
	Return(DeferredInputEffect[Bit]\Effect)
End Function

Function GetShadeEffect%(Bit%)
	If DeferredShadeEffect[Bit] = Null
		Local v.ShadeEffectVariation
		Local Defines$ = "", FoundBits% = 0
		
		For v.ShadeEffectVariation = Each ShadeEffectVariation
			If (v\Bit And Bit) <> 0
				Defines = Defines + v\Define + " "
				FoundBits = FoundBits Or v\Bit
			EndIf
		Next
		
		If FoundBits <> Bit Then Return(0)
		
		If (Not LoadShadeEffect(Bit, "Shade.fx", Defines)) Then Return(0)
	EndIf
	
	Return(DeferredShadeEffect[Bit]\Effect)
End Function

Function CreateInputVariation%(Bit%, Define$)
	Local iv.InputEffectVariation
	
	For iv.InputEffectVariation = Each InputEffectVariation
		If iv\Bit = Bit
			iv\Define = Define
			Return
		EndIf
	Next
	iv.InputEffectVariation = New InputEffectVariation
	iv\Bit = Bit
	iv\Define = Define
End Function

Function CreateShadeVariation%(Bit%, Define$)
	Local iv.ShadeEffectVariation 
	
	For iv.ShadeEffectVariation = Each ShadeEffectVariation
		If iv\Bit = Bit
			iv\Define = Define
			Return
		EndIf
	Next
	iv.ShadeEffectVariation = New ShadeEffectVariation
	iv\Bit = Bit
	iv\Define = Define
End Function

Function GetShadeLight%(LightType%)
	Select LightType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Return(DEFERRED_SHADE_DIRLIGHT)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Return(DEFERRED_SHADE_POINTLIGHT)
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			Return(DEFERRED_SHADE_SPOTLIGHT)
			;[End Block]
	End Select
End Function

Function SetGlobalEnvironment%(Texture$)
	Texture = Lower(Texture)
	If CurrentEnvMap = Null Lor CurrentEnvMap\Name <> Texture
		Local env.EnvMap
		Local i%
		
		If CurrentEnvMap <> Null ; ~ Save previous env
			If IsEqual(fog\EnvBlendFactor, 1.0, 0.05) Lor PreviousEnvMap <> CurrentEnvMap
				For i = 0 To 5
					SetCubeFace(BlendEnvironmentMap, i)
					SetCubeFace(CurrentEnvMap\Texture, i)
					CopyRectStretch(0, 0, TextureWidth(CurrentEnvMap\Texture), TextureHeight(CurrentEnvMap\Texture), 0, 0, TextureWidth(BlendEnvironmentMap), TextureHeight(BlendEnvironmentMap), TextureBuffer(CurrentEnvMap\Texture), TextureBuffer(BlendEnvironmentMap))
				Next
				fog\EnvBlendFactor = 0.0
				PreviousEnvMap = CurrentEnvMap
			Else
				fog\EnvBlendFactor = 1.0 - fog\EnvBlendFactor
			EndIf
		EndIf
		
		CurrentEnvMap = Null
		For env.EnvMap = Each EnvMap
			If env\Name = Texture 
				CurrentEnvMap = env
				Exit
			EndIf
		Next
		
		If CurrentEnvMap = Null
			CurrentEnvMap = New EnvMap
			CurrentEnvMap\Name = Texture
			CurrentEnvMap\Texture = LoadTexture_Strict(Texture, 1 + 128, DeleteAllTextures)
		EndIf
		
		If CurrentEnvMap <> Null
			For i = 0 To 5
				SetCubeFace(GlobalEnvironmentMap, i)
				SetCubeFace(CurrentEnvMap\Texture, i)
				CopyRectStretch(0, 0, TextureWidth(CurrentEnvMap\Texture), TextureHeight(CurrentEnvMap\Texture), 0, 0, TextureWidth(GlobalEnvironmentMap), TextureHeight(GlobalEnvironmentMap), TextureBuffer(CurrentEnvMap\Texture), TextureBuffer(GlobalEnvironmentMap))
			Next
		EndIf
	EndIf
End Function

Function Count3D%()
	CurrTrisAmount = CurrTrisAmount + TrisRendered()
	BatchesAmount = BatchesAmount + Batches()
	Return(Batches())
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS