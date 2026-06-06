Const DEFERRED_LIGHT_DIRECTIONAL% = 1
Const DEFERRED_LIGHT_POINT% = 2
Const DEFERRED_LIGHT_SPOT% = 3

Const DEFERRED_DIFF% = 0
Const DEFERRED_DIFFSKYBOX% = 1
Const DEFERRED_DIFFNORMAL% = 1 Shl 1
Const DEFERRED_DIFFROUGH% = 1 Shl 2
Const DEFERRED_DIFFEMISSIVE% = 1 Shl 3
Const DEFERRED_DIFFEMISSIVEMUL% = 1 Shl 4
Const DEFERRED_FULLBRIGHT% = 1 Shl 5
Const DEFERRED_TRANSPARENT% = 1 Shl 6
Const DEFERRED_DIFFENVMAP% = 1 Shl 7
Const DEFERRED_DIFFHEIGHTMAP% = 1 Shl 8
Const DEFERRED_MASKED% = 1 Shl 9
Const DEFERRED_DISABLEFOG% = 1 Shl 10
Const DEFERRED_DIFFORM% = 1 Shl 11
Const DEFERRED_FORWARD% = 1 Shl 12
Const DEFERRED_EMISSIVECOLOR% = 1 Shl 13
Const DEFERRED_LOCALTRANSFORM% = 1 Shl 14

Const DEFERRED_ADDITIVE% = 1 Shl 16
Const DEFERRED_NOMATERIAL% = 1 Shl 17

Const MAX_DEFERRED_VARIATIONS% = DEFERRED_LOCALTRANSFORM Shl 1

Const DEFERRED_SHADE_DIRLIGHT% = 1
Const DEFERRED_SHADE_POINTLIGHT% = 1 Shl 1
Const DEFERRED_SHADE_SPOTLIGHT% = 1 Shl 2
Const DEFERRED_SHADE_SHADOWS% = 1 Shl 3
Const DEFERRED_SHADE_TUBE% = 1 Shl 4
Const DEFERRED_SHADE_SCATTERING% = 1 Shl 5
Const DEFERRED_SHADE_VOLUMETRIC% = 1 Shl 6
Const DEFERRED_SHADE_VOLUMETRIC_HQ% = 1 Shl 7

Const MAX_DEFERRED_SHADE_VARIATIONS% = DEFERRED_SHADE_VOLUMETRIC_HQ Shl 1

Global DEFERRED_SHADE_VOLUME_QUALITY%[4]
DEFERRED_SHADE_VOLUME_QUALITY[0] = 0
DEFERRED_SHADE_VOLUME_QUALITY[1] = DEFERRED_SHADE_SCATTERING
DEFERRED_SHADE_VOLUME_QUALITY[2] = DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_VOLUMETRIC
DEFERRED_SHADE_VOLUME_QUALITY[3] = DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_VOLUMETRIC Or DEFERRED_SHADE_VOLUMETRIC_HQ

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
Global MRTLighting%
Global MRTVolume%
Global RSDepth%

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

Global ProhibitedInputVariations%

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
Global DeferredSphere%, DeferredCone%, DeferredQuad%, DeferredBox%
Global DirectionalLightUpdate%
Global ShadowsDistance#, ShadowsMipDistance#, ShadowsFade#
Global GBufferBlur#
Global TempColorTexture%

Global CubeRotateX#[6]
Global CubeRotateY#[6]

CubeRotateX[0] = 0.0 : CubeRotateY[0] = 90.0
CubeRotateX[1] = 0.0 : CubeRotateY[1] = 0.0
CubeRotateX[2] = 0.0 : CubeRotateY[2] = -90.0
CubeRotateX[3] = 0.0 : CubeRotateY[3] = 180.0
CubeRotateX[4] = -90.0 : CubeRotateY[4] = 0.0
CubeRotateX[5] = 90.0 : CubeRotateY[5] = 0.0

Global EmissiveMultiply#, EnvBlendFactor#

Const LIGHTING_DEFERRED% = 0
Const LIGHTING_PREPASS% = 1

Global LIGHTING_TYPE% = LIGHTING_PREPASS

Global GlobalEnvironmentMap%, BlendEnvironmentMap%
Global FaceSelectCubeMap%
Global SpotTexture%

Global ResolutionScaleX# = -999.0
Global ResolutionScaleY# = -999.0

Global CurrentTween#

Function InitDeferred%()
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
	CreateInputVariation(DEFERRED_DIFFORM, "ORM")
	CreateInputVariation(DEFERRED_FORWARD, "FORWARD")
	CreateInputVariation(DEFERRED_LOCALTRANSFORM, "LOCALTRANSFORM")
	
	CreateShadeVariation(DEFERRED_SHADE_DIRLIGHT, "DIRLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_POINTLIGHT, "POINTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SPOTLIGHT, "SPOTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SHADOWS, "SHADOWS")
	CreateShadeVariation(DEFERRED_SHADE_TUBE, "TUBE")
	CreateShadeVariation(DEFERRED_SHADE_SCATTERING, "SCATTERING")
	
	For i = 0 To SHADOW_MAP_MIPMAPS - 1
		DeferredShadowMapCube[i] = CreateShadowMap((SHADOW_MAP_SIZE * 6) Shr i, SHADOW_MAP_SIZE Shr i)
		DeferredShadowMap[i] = CreateShadowMap(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
		
		CreateDummyTexture((SHADOW_MAP_SIZE * 6) Shr i, SHADOW_MAP_SIZE Shr i)
		CreateDummyTexture(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
	Next
	
	CreateDummyTexture(DIRLIGHT_SHADOW_MAP_SIZE, DIRLIGHT_SHADOW_MAP_SIZE)
	DeferredShadowMap[SHADOW_MAP_MIPMAPS] = CreateShadowMap(DIRLIGHT_SHADOW_MAP_SIZE, DIRLIGHT_SHADOW_MAP_SIZE)
	
	FaceSelectCubeMap = CreateTexture(1, 1, 1 + 2 + 128 + 512)
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
	
	SpotTexture = LoadTexture("GFX\Shaders\spot.png", 1 + 32768)
	
	SetShadowsMipDistance(3.0)
	SetShadowsDistance(6.0, 0.3)
	SetShadowsBias(0.0001, 0.0)
	
	DirectionalLightUpdate = 0
	SetEmissiveMultiply(1.0)
	
	GlobalEnvironmentMap = CreateTexture(1024, 1024, 1 + 8 + 128)
	BlendEnvironmentMap = CreateTexture(1024, 1024, 1 + 8 + 128)
	
	ResolutionScaleX = -999.0
	ResolutionScaleY = -999.0
	SetRenderParameters(1.0, 1.0, opt\HDRRender)
	
	If (Not opt\ParallaxOcclusion)
		ProhibitInputEffect(GetProhibitedInputEffect() Or DEFERRED_DIFFHEIGHTMAP)
	Else
		ProhibitInputEffect(GetProhibitedInputEffect() And (GetProhibitedInputEffect() Xor DEFERRED_DIFFHEIGHTMAP))
	EndIf
End Function

Function GetResolutionDepth%()
	If RSDepth <> 0 Then Return(TextureBuffer(RSDepth))
	Return(0)
End Function

Function SetRenderParameters%(ScaleX#, ScaleY#, HDR%)
	If ScaleX < 0.0 Lor ScaleY < 0.0
		ScaleX = ResolutionScaleX
		ScaleY = ResolutionScaleY
	Else
		ScaleX = Max(ScaleX, 0.1)
		ScaleY = Max(ScaleY, 0.1)
	EndIf
	
	Local ShouldReload% = (ResolutionScaleX <> ScaleX Lor ResolutionScaleY <> ScaleY)
	Local IsHDR% = MRTColor <> 0 And (TextureFlags(MRTColor) And 4096) <> 0
    
    If IsHDR <> HDR Then ShouldReload = True
	
	If ShouldReload
		ResolutionScaleX = ScaleX
		ResolutionScaleY = ScaleY
		
		If MRTColor <> 0
			FreeTexture(MRTColor)
			FreeTexture(MRTAlbedo)
			FreeTexture(MRTDepth)
			FreeTexture(MRTNormal)
			FreeTexture(MRTLighting)
			FreeTexture(MRTVolume)
			FreeTexture(TempColorTexture)
			
			FreeEntity(DeferredQuad)
			FreeEntity(DeferredCone)
			FreeEntity(DeferredSphere)
			FreeEntity(DeferredBox)
		EndIf
		
		If RSDepth <> 0 Then FreeTexture(RSDepth) : RSDepth = 0
		
		Local Width% = opt\GraphicWidth * ResolutionScaleX
		Local Height% = opt\GraphicHeight * ResolutionScaleY
		
		If HDR
			MRTColor = CreateTexture(Width, Height, 1024 Or 4096)
			MRTAlbedo = CreateTexture(Width, Height, 1 Or 2 Or 1024)
			MRTDepth = CreateTexture(Width, Height, 1024 Or 2048)
			MRTNormal = CreateTexture(Width, Height, 1024 Or 4096)
			MRTLighting = CreateTexture(Width, Height, 1024 Or 4096)
			MRTVolume = CreateTexture(Width, Height, 1024 Or 4096)
			TempColorTexture = CreateTexture(Width, Height, 1024 Or 4096)
		Else
			MRTColor = CreateTexture(Width, Height, 1024 Or 131072)
			MRTAlbedo = CreateTexture(Width, Height, 1 Or 2 Or 1024)
			MRTDepth = CreateTexture(Width, Height, 1024 Or 2048)
			MRTNormal = CreateTexture(Width, Height, 1024 Or 4096)
			MRTLighting = CreateTexture(Width, Height, 1024 Or 131072)
			MRTVolume = CreateTexture(Width, Height, 1024 Or 131072)
			TempColorTexture = CreateTexture(Width, Height, 1024 Or 131072)
		EndIf
		
		If ResolutionScaleX <> 1.0 Lor ResolutionScaleY <> 1.0 Then RSDepth = CreateTexture(Width, Height, 524288)
		
		DeferredSphere = CreateLightVolume(DEFERRED_LIGHT_POINT)
		DeferredCone = CreateLightVolume(DEFERRED_LIGHT_SPOT)
		DeferredQuad = CreateLightVolume(DEFERRED_LIGHT_DIRECTIONAL)
		DeferredBox = CreateCube()
		
		EntityTexture(DeferredBox, MRTAlbedo, 0, 0)
		EntityTexture(DeferredBox, MRTNormal, 0, 1)
		EntityTexture(DeferredBox, MRTDepth, 0, 2)
		EntityOrder(DeferredBox, 10000000)
		EntityBlend(DeferredBox, 3)
		EntityFX(DeferredBox, 8)
		
		EntityTexture(DeferredSphere, MRTAlbedo, 0, 0)
		EntityTexture(DeferredSphere, MRTNormal, 0, 1)
		EntityTexture(DeferredSphere, MRTDepth, 0, 2)
		EntityTexture(DeferredSphere, FaceSelectCubeMap, 0, 3)
		EntityTexture(DeferredSphere, SpotTexture, 0, 4)
		EntityOrder(DeferredSphere, 10000000)
		EntityBlend(DeferredSphere, 3)
		EntityFX(DeferredSphere, 8)
		
		EntityTexture(DeferredCone, MRTAlbedo, 0, 0)
		EntityTexture(DeferredCone, MRTNormal, 0, 1)
		EntityTexture(DeferredCone, MRTDepth, 0, 2)
		EntityTexture(DeferredCone, FaceSelectCubeMap, 0, 3)
		EntityTexture(DeferredCone, SpotTexture, 0, 4)
		EntityOrder(DeferredCone, 10000000)
		EntityBlend(DeferredCone, 3)
		EntityFX(DeferredCone, 8)
		
		EntityTexture(DeferredQuad, MRTAlbedo, 0, 0)
		EntityTexture(DeferredQuad, MRTNormal, 0, 1)
		EntityTexture(DeferredQuad, MRTDepth, 0, 2)
		EntityTexture(DeferredQuad, FaceSelectCubeMap, 0, 3)
		EntityTexture(DeferredQuad, SpotTexture, 0, 4)
		EntityOrder(DeferredQuad, 10000000)
		EntityBlend(DeferredQuad, 3)
		EntityFX(DeferredQuad, 8)
		
		HideEntity(DeferredSphere)
		HideEntity(DeferredCone)
		HideEntity(DeferredQuad)
		HideEntity(DeferredBox)
		
		; ~ Volumes mask
		MaskEntity(DeferredSphere, 4)
		MaskEntity(DeferredCone, 4)
		MaskEntity(DeferredQuad, 4)
		MaskEntity(DeferredBox, 4)
		
		ReloadPostEffects()
		UpdateShaders()
	EndIf
End Function

Function PreloadShaders%()
	Local TypeMask% = DEFERRED_SHADE_DIRLIGHT Or DEFERRED_SHADE_POINTLIGHT Or DEFERRED_SHADE_SPOTLIGHT
	Local QualityMask% = DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_VOLUMETRIC Or DEFERRED_SHADE_VOLUMETRIC_HQ
	Local i%
	
	For i = 1 To MAX_DEFERRED_SHADE_VARIATIONS - 1
		Local CurrentMask% = i And (TypeMask)
		
		If CurrentMask <> 0
			If (CurrentMask And (CurrentMask - 1)) = 0
				
				Local CurrentQuality% = (i And QualityMask)
				Local IsValidQuality% = False
				
				Select CurrentQuality
					Case 0
						;[Block]
						IsValidQuality = True
						;[End Block]
					Case DEFERRED_SHADE_SCATTERING
						;[Block]
						IsValidQuality = True
						;[End Block]
					Case (DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_VOLUMETRIC)
						;[Block]
						IsValidQuality = True
						;[End Block]
					Case (DEFERRED_SHADE_SCATTERING Or DEFERRED_SHADE_VOLUMETRIC Or DEFERRED_SHADE_VOLUMETRIC_HQ)
						;[Block]
						IsValidQuality = True
						;[End Block]
				End Select
				If IsValidQuality Then GetShadeEffect(i)
			EndIf
		EndIf
	Next
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
		If ef\Effect <> 0
			EffectTexture(ef\Effect, "tLighting", MRTLighting)
			EffectTexture(ef\Effect, "tMRTDepth", MRTDepth)
		EndIf
	Next
	
	SetEmissiveMultiply(EmissiveMultiply, True)
	SetEnvBlendFactor(EnvBlendFactor, True)
	
	FreeBank(AdjustMatrix) : AdjustMatrix = 0
End Function

Function ClearDeferred%()
	PostEffectQuad = 0
	MRTColor = 0
	MRTAlbedo = 0
	MRTDepth = 0
	MRTNormal = 0
	RSDepth = 0
	
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
				If mat\IsDiffuseAlpha Lor GetBrushBlend(Brush) > 0 Then State = State Or DEFERRED_TRANSPARENT
				If mat\UseMask Then State = State Or DEFERRED_MASKED
				If mat\IsORM Then State = State Or DEFERRED_DIFFORM
				
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
			Else
				If GetBrushBlend(Brush) > 0 Then State = State Or DEFERRED_TRANSPARENT
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

Function ProcessDeferred%(Cam%, Tween# = 1.0, ScaleX# = 1.0, ScaleY# = 1.0, Environment% = False, Destination% = 0)
	CurrentTween = Tween
	If GetInputEffect(DEFERRED_DIFF) <> 0
		Local ef.InputEffect, se.ShadeEffect
		
		If Destination = 0 Then Destination = BackBuffer()
		
		CurrTrisAmount = 0
		BatchesAmount = 0
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "Deferred")
		Next
		
		SetRenderParameters(ScaleX, ScaleY, opt\HDRRender)
		CameraViewport(Cam, 0, 0, TextureWidth(MRTColor), TextureHeight(MRTColor))
		
		ClearBuffer(TextureBuffer(MRTColor), fog\R, fog\G, fog\B, 255)
		ClearBuffer(TextureBuffer(MRTAlbedo), 0, 0, 0, 255)
		ClearBuffer(TextureBuffer(MRTNormal), 0, 0, 0, 0)
		ClearBuffer(TextureBuffer(MRTDepth), 0, 0, 0, 0)
		ClearBuffer(TextureBuffer(MRTLighting), 0, 0, 0, 0)
		ClearBuffer(TextureBuffer(MRTVolume), 0, 0, 0, 255)
		ClearBuffer(TextureBuffer(TempColorTexture), 0, 0, 0, 0)
		SetBuffer(TextureBuffer(MRTColor), GetResolutionDepth())
		SetBuffer(TextureBuffer(MRTAlbedo), GetResolutionDepth(), 1)
		SetBuffer(TextureBuffer(MRTNormal), GetResolutionDepth(), 2)
		SetBuffer(TextureBuffer(MRTDepth), GetResolutionDepth(), 3)
		CameraClsMode(Cam, 0, 1)
		
		AmbientLight(fog\CurrAmbientR, fog\CurrAmbientG, fog\CurrAmbientB)
		
		; ~ Render opacity
		WireFrame(WireFrameState)
		RenderWorld(CurrentTween, Cam, -1, 1) ; ~ Render only opacity
		Count3D()
		
		CameraClsMode(Cam, 0, 1)
		
		ProcessLinearDepth(Cam)
		
		Local InvViewProjection% = CameraMatrix(Cam, 3, CurrentTween)
		
		For se.ShadeEffect = Each ShadeEffect
			EffectMatrix(se\Effect, "InvViewProj", InvViewProjection)
		Next
		
		EffectMatrix(ReflectionProbesEffect, "InvViewProj", InvViewProjection)
		
		WireFrame(False)
		ProcessGraphics(Cam, Environment)
		
		CameraClsMode(Cam, 0, 0)
		
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "Deferred")
		Next
		
		If (Not Environment) Then ProcessFog(fog\R, fog\G, fog\B)
		
		ProcessBloomAndSSAO(Cam, 1.0, 1.75, 0.047)
		
		SetBuffer(TextureBuffer(MRTColor), GetResolutionDepth())
		
		CameraClsMode(Cam, 0, 0)
		WireFrame(WireFrameState)
		RenderWorld(CurrentTween, Cam, -1, 2)
		Count3D()
		
		CameraClsMode(Cam, 1, 1)
		WireFrame(False)
		
		If (Not Environment)
			If opt\VolumetricLights Then ProcessBilateralBlur(Cam, MRTVolume, TempColorTexture, LinearDepth, MRTNormal, MRTColor, 3) ; ~ Use TempColorTexture texture to avoid creating additional textures
			ProcessMotionBlur(Cam, 1.0)
			PresentGBuffer(MRTColor, TextureBuffer(MRTAlbedo), GetResolutionDepth(), 2 - ((TextureFlags(MRTColor) And 4096) <> 0))
			If (Not ProcessFXAA(MRTAlbedo, Destination)) Then PresentGBuffer(MRTAlbedo, Destination)
		Else
			PresentGBuffer(MRTColor, TextureBuffer(MRTAlbedo), GetResolutionDepth(), True)
		EndIf
		SetBuffer(BackBuffer())
	Else
		RenderWorld(CurrentTween)
		Count3D()
	EndIf
End Function

Function ProcessGraphics%(Cam%, Environment% = False)
	Local ef.InputEffect, rp.ReflectionProbe
	Local DrawShadows% = (Environment Lor (opt\LightingQuality > 1))
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
	ShowEntity(DeferredBox)
	
	BeginRender(CurrentTween, 4 Or 16) ; ~ Begin render light/environment volumes and shadowmaps
	
	For l.Lights = Each Lights
		If (Not EntityHidden(l\OBJ)) Then RenderLight(Cam, l\OBJ, l\Range, l\Length, l\R, l\G, l\B, Max(l\Fade * Min(SecondaryLightOn, 1.0), Environment), l\LType, l\FOV, l\CastShadows And DrawShadows, l\Scattering * 0.15)
	Next
	
	For dl.DynamicLight = Each DynamicLight
		If (Not EntityHidden(dl\OBJ)) And (GetParent(dl\OBJ) = 0 Lor (Not EntityHidden(GetParent(dl\OBJ)))) Then RenderLight(Cam, dl\OBJ, dl\Range, 0.0, dl\R, dl\G, dl\B, dl\Fade, dl\LType, dl\FOV, dl\CastShadows And DrawShadows, dl\Scattering * 0.15)
	Next
	
	If (wi\NVGPower > 0 Lor wi\NightVision = 3) And wi\NightVision > 0 Then RenderLight(Cam, GetDummyPivot(EntityX(Cam, True, CurrentTween), EntityY(Cam, True, CurrentTween), EntityZ(Cam, True, CurrentTween)), 2500.0 * RoomScale, 0.0, 200, 200, 200, 2.5, DEFERRED_LIGHT_POINT, 90.0, False, 0.0)
	
	If KeyDown(34) And opt\DebugMode = 1 Then RenderLight(Cam, GetDummyPivot(EntityX(Cam, True, CurrentTween), EntityY(Cam, True, CurrentTween), EntityZ(Cam, True, CurrentTween)), 25.0, 0.0, 200, 200, 200, 1.0, DEFERRED_LIGHT_SPOT, 60.0, False, 0.0)
	
	EndRender()
	
	PresentGBuffer(MRTLighting, TextureBuffer(MRTColor), GetResolutionDepth(), False, 3)
	
	; ~ Render reflection probes
	If opt\Reflections > 0
		Local AR# = fog\CurrAmbientR, AG# = fog\CurrAmbientG, AB# = fog\CurrAmbientB
		
		PrepareReflectionProbes(TempColorTexture)
		For rp.ReflectionProbe = Each ReflectionProbe
			RenderReflectionProbe(Cam, 255.0 * (AR / rp\RT\EnvironmentR), 255.0 * (AG / rp\RT\EnvironmentG), 255.0 * (AB / rp\RT\EnvironmentB), rp\RT\EnvironmentMap, rp\Bounds, rp\Delta)
		Next
	EndIf
	
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, Near, Far)
	
	HideEntity(DeferredCone)
	HideEntity(DeferredSphere)
	HideEntity(DeferredQuad)
	HideEntity(DeferredBox)
	
	If DirectionalLightUpdate < MilliSecs() Then DirectionalLightUpdate = MilliSecs() + DIRECTIONAL_LIGHT_TIME
	
	If opt\Reflections > 0 Then BlendReflectionProbes(TempColorTexture)
End Function

Function RenderLight%(Cam%, OBJ%, Range#, Length#, R%, G%, B%, Intensity#, LType%, FOV# = 90.0, CastShadows% = True, Scattering# = 1.0)
	If Intensity <= 0.0 Then Return
	
	Local DistToLight# = EntityDistance(Cam, OBJ)
	
	If DistToLight - Range > GetCameraRangeFar(Cam) Then Return
	
	Local Volume%, TanValue#
	Local x# = EntityX(OBJ, True, CurrentTween)
	Local y# = EntityY(OBJ, True, CurrentTween)
	Local z# = EntityZ(OBJ, True, CurrentTween)
	Local Pitch# = EntityPitch(OBJ, True, CurrentTween)
	Local Yaw# = EntityYaw(OBJ, True, CurrentTween)
	Local VolumeScale# = Range * 1.25
	Local ShadowIntensity# = 1.0
	Local EffectBits% = GetShadeLight(LType)
	
	If CastShadows And LType <> DEFERRED_LIGHT_DIRECTIONAL
		ShadowIntensity = GetFade(Max(DistToLight - Range, 0.0), ShadowsDistance * ShadowsFade, ShadowsDistance)
		If ShadowIntensity <= 0.0 Then CastShadows = False
	EndIf
	
	If CastShadows Then EffectBits = EffectBits Or DEFERRED_SHADE_SHADOWS
	If Scattering > 0.0 Then EffectBits = EffectBits Or DEFERRED_SHADE_VOLUME_QUALITY[opt\VolumetricLights]
	If Length > 0.0 Then EffectBits = EffectBits Or DEFERRED_SHADE_TUBE
	
	Local ShadeEffect% = GetShadeEffect(EffectBits)
	
	If (Not CastShadows) Then EffectFloat(ShadeEffect, "NormalOffset", 0.0)
	
	FOV = Clamp(FOV, 0.0, 170.0)
	
	Select LType
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, DeferredShadowMapCube[GetShadowMapMip(Range, DistToLight)], LType, OBJ, Range, FOV)
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			TanValue = Tan(FOV * 0.5)
			
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			Local Shadowmap% = DeferredShadowMap[GetShadowMapMip(Range, DistToLight)]
			
			PositionEntity(DeferredCamera, EntityX(OBJ, True, 0.0), EntityY(OBJ, True, 0.0), EntityZ(OBJ, True, 0.0))
			RotateEntity(DeferredCamera, EntityPitch(OBJ, True, 0.0), EntityYaw(OBJ, True, 0.0), 0.0)
			CaptureEntity(DeferredCamera)
			
			PositionEntity(DeferredCamera, EntityX(OBJ, True), EntityY(OBJ, True), EntityZ(OBJ, True))
			RotateEntity(DeferredCamera, EntityPitch(OBJ, True), EntityYaw(OBJ, True), 0.0)
			
			CameraRange(DeferredCamera, 0.005 * Range, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1.0 / TanValue)
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(Shadowmap), TextureHeight(Shadowmap))
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, SLOPE_BIAS)
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, Shadowmap, LType, OBJ, Range, FOV)
			
			EffectMatrix(ShadeEffect, "LightViewProj", CameraMatrix(DeferredCamera, 2, CurrentTween))
			If Scattering > 0.0
				CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			Else
				CameraRange(Cam, 0.01, 1000000)
			EndIf
			;[End Block]
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = DeferredQuad
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, DeferredShadowMap[SHADOW_MAP_MIPMAPS], LType, OBJ, Range, FOV)
			
			Cam = QuadCamera
			
			EffectMatrix(ShadeEffect, "LightViewProj", CameraMatrix(DeferredCamera, 2))
			;[End Block]
	End Select
	
	Intensity = Intensity * Lerp(opt\ScreenGamma, 1.0, 0.8)
	
	EffectVector(ShadeEffect, "LightPos", x, y, z, 1.0 / Max(Range, 0.0001))
	EffectVector(ShadeEffect, "LightColor", R / 255.0 * Intensity, G / 255.0 * Intensity, B / 255.0 * Intensity)
	EffectVector(ShadeEffect, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
	EffectFloat(ShadeEffect, "ShadowIntensity", 1.0 - ShadowIntensity)
	EffectInt(ShadeEffect, "Time", MilliSecs())
	If Length > 0.0 Then EffectFloat(ShadeEffect, "LightLength", Length)
	If Scattering > 0.0 Then EffectFloat(ShadeEffect, "LightScattering", Scattering)
	EntityEffect(Volume, ShadeEffect)
	
	SetBuffer(TextureBuffer(MRTLighting), GetResolutionDepth())
	If (EffectBits And DEFERRED_SHADE_SCATTERING) <> 0 Then SetBuffer(TextureBuffer(MRTVolume), GetResolutionDepth(), 1)
	RenderEntity(Cam, Volume, CurrentTween)
	If (EffectBits And DEFERRED_SHADE_SCATTERING) <> 0 Then SetBuffer(0, 0, 1)
	
	Count3D()
End Function

Global DEFERRED_LIGHT_POINT_CULLING_SCALE_TAN# = Tan(90.0 * 0.5)

Function RenderShadowMap%(ShadeEffect%, MainCam%, ShadowMap%, LType%, OBJ%, Range#, FOV#)
	Local ShadowMapWidth% = TextureWidth(ShadowMap)
	Local ShadowMapHeight% = TextureHeight(ShadowMap)
	Local DummyTexture% = FindDummyTexture(ShadowMapWidth, ShadowMapHeight)
	Local i%, ScaledNormalOffset#
	
	If DummyTexture = 0
		DebugLog("Unknown texture error: " + ShadowMapWidth + "x" + ShadowMapHeight)
		Return
	EndIf
	
	SetBuffer(TextureBuffer(DummyTexture))
	
	Select LType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			PositionEntity(DeferredCamera, EntityX(OBJ, True, 0.0), EntityY(OBJ, True, 0.0), EntityZ(OBJ, True, 0.0))
			RotateEntity(DeferredCamera, EntityPitch(OBJ, True, 0.0), EntityYaw(OBJ, True, 0.0), 0.0)
			CaptureEntity(DeferredCamera)
			
			PositionEntity(DeferredCamera, EntityX(OBJ, True), EntityY(OBJ, True), EntityZ(OBJ, True))
			RotateEntity(DeferredCamera, EntityPitch(OBJ, True), EntityYaw(OBJ, True), 0.0)
			MoveEntity(DeferredCamera, 0.0, 0.0, -DIRECTIONAL_LIGHT_EXTRUSION)
			
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
			
			RenderWorld(CurrentTween, DeferredCamera, 16) ; ~ Render only 16 mask
			Count3D()
			EffectInt(ShadeEffect, "ShadowMapAddress", 4)
			EffectFloat(ShadeEffect, "NormalOffset", 0)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
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
			
			Local cX# = EntityX(OBJ, True, 0.0), cY# = EntityY(OBJ, True, 0.0), cZ# = EntityZ(OBJ, True, 0.0)
			Local rX# = EntityX(OBJ, True), rY# = EntityY(OBJ, True), rZ# = EntityZ(OBJ, True)
			
			PositionEntity(DeferredCone, EntityX(OBJ, True, CurrentTween), EntityY(OBJ, True, CurrentTween), EntityZ(OBJ, True, CurrentTween))
			
			For i = 0 To 5
				RotateEntity(DeferredCone, CubeRotateX[i], CubeRotateY[i], 0.0)
				ScaleEntity(DeferredCone, CullingScale, CullingScale, Range)
				
				If EntityInView(DeferredCone, MainCam)
					PositionEntity(DeferredCamera, cX, cY, cZ)
					RotateEntity(DeferredCamera, CubeRotateX[i], CubeRotateY[i], 0.0)
					CaptureEntity(DeferredCamera)
					
					PositionEntity(DeferredCamera, rX, rY, rZ)
					
					CameraViewport(DeferredCamera, i * Width, 0, Width, Height)
					RenderWorld(CurrentTween, DeferredCamera, 16) ; ~ Render only 16 mask
					Count3D()
					EffectMatrix(ShadeEffect, "LightViewProj" + i, CameraMatrix(DeferredCamera, 2, CurrentTween)) ; ~ Push matrix for each face
				EndIf
			Next
			
			ScaledNormalOffset = 2.0 * DEFERRED_LIGHT_POINT_CULLING_SCALE_TAN * Range
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			
			EffectFloat(ShadeEffect, "NormalOffset", ScaledNormalOffset)
			EffectInt(ShadeEffect, "ShadowMapAddress", 3)
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
			
			RenderWorld(CurrentTween, DeferredCamera, 16) ; ~ Render only 16 mask
			Count3D()
			
			ScaledNormalOffset = 2.0 * Tan(FOV * 0.5) * Range
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			
			EffectFloat(ShadeEffect, "NormalOffset", ScaledNormalOffset)
			EffectInt(ShadeEffect, "ShadowMapAddress", 3)
			;[End Block]
	End Select
	
	EffectVector(ShadeEffect, "ShadowMapSize", ShadowMapWidth, ShadowMapHeight)
	EffectTexture(ShadeEffect, "tShadowMap", ShadowMap)
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

Function CreateLightVolume%(LType%)
	Local Volume%, SF%
	
	Select LType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = CreateFullscreenQuad(TextureWidth(MRTColor), TextureHeight(MRTColor), QuadCamera)
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

Function SetEmissiveMultiply%(Value#, Force% = False)
	If EmissiveMultiply <> Value Lor Force
		Local ef.InputEffect
		
		EmissiveMultiply = Value
		For ef.InputEffect = Each InputEffect
			If (ef\Bit And DEFERRED_DIFFEMISSIVEMUL) Then EffectFloat(ef\Effect, "EmissiveMultiply", EmissiveMultiply)
		Next
	EndIf
End Function

Function SetEnvBlendFactor%(Value#, Force% = False)
	If EnvBlendFactor <> Value Lor Force
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

Type DynamicLight
	Field OBJ%
	Field LType%
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

Function CreateLight%(LType%, Parent% = 0)
	Local dl.DynamicLight = New DynamicLight
	
	dl\OBJ = CreatePivot(Parent)
	dl\LType = LType
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
				If (v\Bit And ProhibitedInputVariations) = 0 Then Defines = Defines + v\Define + " "
				FoundBits = FoundBits Or v\Bit
			EndIf
		Next
		
		If FoundBits <> Bit Then Return(0)
		
		If (Not LoadInputEffect(Bit, "Input.fx", Defines + "REVERSEDZ")) Then Return(0)
	EndIf
	
	Return(DeferredInputEffect[Bit]\Effect)
End Function

Function GetProhibitedInputEffect%()
	Return(ProhibitedInputVariations)
End Function

Function ProhibitInputEffect%(Bits%)
    If ProhibitedInputVariations <> Bits
		Local ef.InputEffect, v.InputEffectVariation
        Local ChangedBits% = ProhibitedInputVariations Xor Bits
		
        ProhibitedInputVariations = Bits
        
        For ef.InputEffect = Each InputEffect
            If ef\Effect <> 0
                If (ef\Bit And ChangedBits) <> 0
					Local NewDefines$ = ""
					
                    For v.InputEffectVariation = Each InputEffectVariation
                        If (v\Bit And ef\Bit) <> 0 And (v\Bit And ProhibitedInputVariations) = 0
                            NewDefines = NewDefines + v\Define + " "
                        EndIf
                    Next
					ReloadEffect(ef\Effect, DEFERRED_PATH + "Input.fx", NewDefines + "REVERSEDZ")
                EndIf
            EndIf
        Next
        UpdateShaders()
    EndIf
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

Function GetShadeLight%(LType%)
	Select LType
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

Function GenerateEnvironment%(FaceWidth%, x#, y#, z#)
	FaceWidth = Clamp(FaceWidth, 1, 4096)
	
	Local CubeTexture% = CreateTexture(FaceWidth, FaceWidth, 1 Or 8 Or 128)
	Local i%
	
	PositionEntity(Camera, x, y, z)
	CameraProjMode(Camera, 1)
	CameraViewport(Camera, 0, 0, opt\GraphicWidth, opt\GraphicHeight)
	CameraZoom(Camera, 1.0)
	CameraRange(Camera, 0.1, 100.0)
	
	Local PrevBlur%  = opt\MotionBlur
	
	opt\MotionBlur = False
	
	Local ScaleX# = Float(FaceWidth) / opt\GraphicWidth
	Local ScaleY# = Float(FaceWidth) / opt\GraphicHeight
	
	fog\R = 0
	fog\G = 0
	fog\B = 0
	
	For i = 0 To 5
		SetCubeFace(CubeTexture, i)
		RotateEntity(Camera, CubeRotateX[i], CubeRotateY[i], 0.0)
		ProcessDeferred(Camera, 1.0, ScaleX, ScaleY, True)
		CopyRectStretch(0, 0, TextureWidth(MRTAlbedo), TextureHeight(MRTAlbedo), 0, 0, FaceWidth, FaceWidth, TextureBuffer(MRTAlbedo), TextureBuffer(CubeTexture))
	Next
	
	opt\MotionBlur = PrevBlur
	CameraProjMode(Camera, 0)
	Return(CubeTexture)
End Function

Function RenderReflectionProbe(Cam%, R%, G%, B%, Texture%, Box%, Delta% = 0)
	If Texture = 0 Lor Box = 0 Then Return
	
	PositionEntity(DeferredBox, EntityX(Box, True), EntityY(Box, True), EntityZ(Box, True))
	RotateEntity(DeferredBox, EntityPitch(Box, True), EntityYaw(Box, True), EntityRoll(Box, True))
	ScaleEntity(DeferredBox, EntityScaleX(Box, True), EntityScaleY(Box, True), EntityScaleZ(Box, True))
	
	EntityTexture(DeferredBox, Texture, 0, 3)
	EntityEffect(DeferredBox, ReflectionProbesEffect)
	
	EffectVector(ReflectionProbesEffect, "ProbeColor", R / 255.0, G / 255.0, B / 255.0)
	EffectVector(ReflectionProbesEffect, "ProbeDelta", Sin(Delta), Cos(Delta))
	
	CameraRange(Cam, 0.1, 500000)
	
	RenderEntity(Cam, DeferredBox, CurrentTween)
End Function

Function PrepareReflectionProbes%(Output%)
	ClearBuffer(TextureBuffer(TempColorTexture), 0, 0, 0, 0)
	SetBuffer(TextureBuffer(TempColorTexture), GetResolutionDepth())
End Function

Function BlendReflectionProbes%(Output%)
	EntityTexture(PostEffectQuad, Output, 0, 0)
	RenderEffectQuad(BlendProbesEffect, MRTColor, "Main", 3)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	SetBuffer(TextureBuffer(MRTColor), GetResolutionDepth())
End Function

Function Count3D%()
	CurrTrisAmount = CurrTrisAmount + TrisRendered()
	BatchesAmount = BatchesAmount + Batches()
	Return(Batches())
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS