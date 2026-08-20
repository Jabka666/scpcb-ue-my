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

Const RENDER_OFFSCREEN% = 1
Const RENDER_ENVCAPTURE% = 2

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
Const DIRECTIONAL_LIGHT_EXTRUSION# = 200.0
Global SHADOW_BIAS# = 0.00044
Global NORMAL_OFFSET# = 1.0
Global SLOPE_BIAS# = 2.0

Const SHADOW_MAP_MIPMAPS% = 1 ; ~ Don't change this
Const DIRLIGHT_SHADOW_CASCADES% = 3

Global SHADOW_MAP_SIZE% = 1024
Const DIRLIGHT_SHADOW_SPLIT_LAMBDA# = 0.75

Global ShadowManagerSlotPoint% = ShadowManagerCreateSlot()
Global ShadowManagerSlotSpot% = ShadowManagerCreateSlot()
Global ShadowManagerSlotDir% = ShadowManagerCreateSlot()

Global MRTColor%
Global MRTAlbedo%
Global MRTDepth%
Global MRTNormal%
Global MRTLighting%
Global MRTVolume%
Global RSDepth%

Global EnvMRTColor%, EnvMRTAlbedo%, EnvMRTDepth%, EnvMRTNormal%, EnvMRTLighting%, EnvMRTVolume%, EnvTempColorTexture%, EnvRSDepth%
Global EnvRenderActive% = False
Global SavedMRTColor%, SavedMRTAlbedo%, SavedMRTDepth%, SavedMRTNormal%, SavedMRTLighting%, SavedMRTVolume%, SavedTempColorTexture%, SavedRSDepth%

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
	Field Cube%
End Type

Type EnvMap
	Field Name$
	Field Texture%
End Type

Global CurrentEnvMap.EnvMap

Global DeferredInputEffect.InputEffect[MAX_DEFERRED_VARIATIONS]
Global DeferredShadeEffect.ShadeEffect[MAX_DEFERRED_SHADE_VARIATIONS]

Global DeferredShadowMapCube%[SHADOW_MAP_MIPMAPS + 1]
Global DeferredShadowMap%[SHADOW_MAP_MIPMAPS + 1]
Global TextureDummies.DummyTexture

Global DeferredCamera%, QuadCamera%
Global DeferredSphere%, DeferredCone%, DeferredQuad%, DeferredBox%
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

Global EmissiveMultiply#

Const LIGHTING_DEFERRED% = 0
Const LIGHTING_PREPASS% = 1

Global LIGHTING_TYPE% = LIGHTING_PREPASS

Global FaceSelectCubeMap%
Global SpotTexture%

Global ResolutionScaleX# = -999.0
Global ResolutionScaleY# = -999.0

Global CurrentTween#

Global CurrentCameraNear#
Global CurrentCameraFar#

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
	CreateInputVariation(DEFERRED_EMISSIVECOLOR, "EMISSIVECOLOR")
	CreateInputVariation(DEFERRED_LOCALTRANSFORM, "LOCALTRANSFORM")
	
	CreateShadeVariation(DEFERRED_SHADE_DIRLIGHT, "DIRLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_POINTLIGHT, "POINTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SPOTLIGHT, "SPOTLIGHT")
	CreateShadeVariation(DEFERRED_SHADE_SHADOWS, "SHADOWS")
	CreateShadeVariation(DEFERRED_SHADE_TUBE, "TUBE")
	CreateShadeVariation(DEFERRED_SHADE_SCATTERING, "SCATTERING")
	CreateShadeVariation(DEFERRED_SHADE_VOLUMETRIC, "VOLUMETRIC")
	CreateShadeVariation(DEFERRED_SHADE_VOLUMETRIC_HQ, "VOLUMETRIC_HQ")
	
	UpdateShadowsQuality()
	
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
	
	SetEmissiveMultiply(1.0)
	
	ResolutionScaleX = -999.0
	ResolutionScaleY = -999.0
	SetRenderParameters(1.0, 1.0, opt\HDRRender)
	
	If (Not opt\ParallaxOcclusion)
		ProhibitInputEffect(GetProhibitedInputEffect() Or DEFERRED_DIFFHEIGHTMAP)
	Else
		ProhibitInputEffect(GetProhibitedInputEffect() And (GetProhibitedInputEffect() Xor DEFERRED_DIFFHEIGHTMAP))
	EndIf
End Function

Function UpdateShadowsQuality%()
	Local NewSize%, i%
	
	Select opt\ShadowsQuality
		Case 0
			;[Block]
			NewSize = 512
			;[End Block]
		Case 1
			;[Block]
			NewSize = 1024
			;[End Block]
		Default
			;[Block]
			NewSize = 2048
			;[End Block]
	End Select
	
	If NewSize = SHADOW_MAP_SIZE And DeferredShadowMap[SHADOW_MAP_MIPMAPS] <> 0 Then Return
	
	For i = 0 To SHADOW_MAP_MIPMAPS - 1
		If DeferredShadowMapCube[i] <> 0 Then FreeTexture(DeferredShadowMapCube[i])
		If DeferredShadowMap[i] <> 0 Then FreeTexture(DeferredShadowMap[i])
	Next
	If DeferredShadowMap[SHADOW_MAP_MIPMAPS] <> 0 Then FreeTexture(DeferredShadowMap[SHADOW_MAP_MIPMAPS])
	
	SHADOW_MAP_SIZE = NewSize
	
	Local ShadowMapSize% = Min(SHADOW_MAP_SIZE, 1024)
	
	For i = 0 To SHADOW_MAP_MIPMAPS - 1
		If opt\DXLevel >= 100
			DeferredShadowMapCube[i] = CreateCubeShadowMap(ShadowMapSize Shr i)
			CreateCubeDummyTexture(ShadowMapSize Shr i)
		Else
			DeferredShadowMapCube[i] = CreateShadowMap((ShadowMapSize * 6) Shr i, ShadowMapSize Shr i)
			CreateDummyTexture((ShadowMapSize * 6) Shr i, ShadowMapSize Shr i)
		EndIf
		DeferredShadowMap[i] = CreateShadowMap(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
		
		CreateDummyTexture(SHADOW_MAP_SIZE Shr i, SHADOW_MAP_SIZE Shr i)
	Next
	
	CreateDummyTexture(SHADOW_MAP_SIZE * DIRLIGHT_SHADOW_CASCADES, SHADOW_MAP_SIZE)
	DeferredShadowMap[SHADOW_MAP_MIPMAPS] = CreateShadowMap(SHADOW_MAP_SIZE * DIRLIGHT_SHADOW_CASCADES, SHADOW_MAP_SIZE)
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
	
	; ~ Preload inputs
	GetInputEffect(DEFERRED_DIFF)
	GetInputEffect(DEFERRED_DIFFSKYBOX)
	GetInputEffect(DEFERRED_DIFFSKYBOX Or DEFERRED_FULLBRIGHT)
	GetInputEffect(DEFERRED_FULLBRIGHT)
	GetInputEffect(DEFERRED_FULLBRIGHT Or DEFERRED_DISABLEFOG)
	GetInputEffect(DEFERRED_TRANSPARENT)
	GetInputEffect(DEFERRED_TRANSPARENT Or DEFERRED_FULLBRIGHT)
	GetInputEffect(DEFERRED_TRANSPARENT Or DEFERRED_FULLBRIGHT Or DEFERRED_DISABLEFOG)
	GetInputEffect(DEFERRED_FORWARD)
	GetInputEffect(DEFERRED_LOCALTRANSFORM)
	GetInputEffect(DEFERRED_LOCALTRANSFORM Or DEFERRED_DIFFEMISSIVE)
	
	GetInputEffect(DEFERRED_DIFFNORMAL)
	GetInputEffect(DEFERRED_DIFFROUGH)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFENVMAP)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFORM)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL Or DEFERRED_DIFFENVMAP)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_MASKED)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFHEIGHTMAP)
	GetInputEffect(DEFERRED_DIFFEMISSIVE)
	GetInputEffect(DEFERRED_DIFFEMISSIVE Or DEFERRED_DIFFEMISSIVEMUL)
	GetInputEffect(DEFERRED_DIFFENVMAP)
	GetInputEffect(DEFERRED_DIFFORM)
	GetInputEffect(DEFERRED_MASKED)
	GetInputEffect(DEFERRED_DIFFHEIGHTMAP)
	GetInputEffect(DEFERRED_EMISSIVECOLOR)
	GetInputEffect(DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_EMISSIVECOLOR)
	
	GetInputEffect(DEFERRED_TRANSPARENT Or DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH)
	GetInputEffect(DEFERRED_TRANSPARENT Or DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSIVE)
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
	
	FreeBank(AdjustMatrix) : AdjustMatrix = 0
End Function

Function ClearDeferred%()
	PostEffectQuad = 0
	MRTColor = 0
	MRTAlbedo = 0
	MRTDepth = 0
	MRTNormal = 0
	RSDepth = 0
	
	FreeEnvBuffers()
	EnvRenderActive = False
	
	DeferredShadowMap[0] = 0
	DeferredShadowMap[SHADOW_MAP_MIPMAPS] = 0
	DeferredShadowMapCube[0] = 0
	
	Delete Each DummyTexture
	Delete Each DynamicLight
	Delete Each EnvMap
	CurrentEnvMap = Null
End Function

Function FreeEnvBuffers%()
	If EnvMRTColor <> 0
		FreeTexture(EnvMRTColor)
		FreeTexture(EnvMRTAlbedo)
		FreeTexture(EnvMRTDepth)
		FreeTexture(EnvMRTNormal)
		FreeTexture(EnvMRTLighting)
		FreeTexture(EnvMRTVolume)
		FreeTexture(EnvTempColorTexture)
		FreeTexture(EnvRSDepth)
	EndIf
	EnvMRTColor = 0
	EnvMRTAlbedo = 0
	EnvMRTDepth = 0
	EnvMRTNormal = 0
	EnvMRTLighting = 0
	EnvMRTVolume = 0
	EnvTempColorTexture = 0
	EnvRSDepth = 0
End Function

Function CreateEnvBuffers%(Width%, Height%)
	EnvMRTColor = CreateTexture(Width, Height, 1024 Or 131072)
	EnvMRTAlbedo = CreateTexture(Width, Height, 1 Or 2 Or 1024)
	EnvMRTDepth = CreateTexture(Width, Height, 1024 Or 2048)
	EnvMRTNormal = CreateTexture(Width, Height, 1024 Or 4096)
	EnvMRTLighting = CreateTexture(Width, Height, 1024 Or 131072)
	EnvMRTVolume = CreateTexture(Width, Height, 1024 Or 131072)
	EnvTempColorTexture = CreateTexture(Width, Height, 1024 Or 131072)
	EnvRSDepth = CreateTexture(Width, Height, 524288)
End Function

Function BindLightVolumeTextures%(Albedo%, Normal%, Depth%)
	EntityTexture(DeferredSphere, Albedo, 0, 0)
	EntityTexture(DeferredSphere, Normal, 0, 1)
	EntityTexture(DeferredSphere, Depth, 0, 2)
	
	EntityTexture(DeferredCone, Albedo, 0, 0)
	EntityTexture(DeferredCone, Normal, 0, 1)
	EntityTexture(DeferredCone, Depth, 0, 2)
	
	EntityTexture(DeferredQuad, Albedo, 0, 0)
	EntityTexture(DeferredQuad, Normal, 0, 1)
	EntityTexture(DeferredQuad, Depth, 0, 2)
	
	EntityTexture(DeferredBox, Albedo, 0, 0)
	EntityTexture(DeferredBox, Normal, 0, 1)
	EntityTexture(DeferredBox, Depth, 0, 2)
End Function

Function BeginEnvironment%(Width%, Height%)
	If EnvMRTColor = 0 Lor TextureWidth(EnvMRTColor) <> Width Lor TextureHeight(EnvMRTColor) <> Height
		If EnvMRTColor <> 0 Then FreeEnvBuffers()
		CreateEnvBuffers(Width, Height)
	EndIf
	
	SavedMRTColor = MRTColor
	SavedMRTAlbedo = MRTAlbedo
	SavedMRTDepth = MRTDepth
	SavedMRTNormal = MRTNormal
	SavedMRTLighting = MRTLighting
	SavedMRTVolume = MRTVolume
	SavedTempColorTexture = TempColorTexture
	SavedRSDepth = RSDepth
	
	MRTColor = EnvMRTColor
	MRTAlbedo = EnvMRTAlbedo
	MRTDepth = EnvMRTDepth
	MRTNormal = EnvMRTNormal
	MRTLighting = EnvMRTLighting
	MRTVolume = EnvMRTVolume
	TempColorTexture = EnvTempColorTexture
	RSDepth = EnvRSDepth
	
	BindLightVolumeTextures(EnvMRTAlbedo, EnvMRTNormal, EnvMRTDepth)
	UpdateShaders()
	
	EnvRenderActive = True
End Function

Function EndEnvironment%()
	If (Not EnvRenderActive) Then Return
	
	MRTColor = SavedMRTColor
	MRTAlbedo = SavedMRTAlbedo
	MRTDepth = SavedMRTDepth
	MRTNormal = SavedMRTNormal
	MRTLighting = SavedMRTLighting
	MRTVolume = SavedMRTVolume
	TempColorTexture = SavedTempColorTexture
	RSDepth = SavedRSDepth
	
	BindLightVolumeTextures(SavedMRTAlbedo, SavedMRTNormal, SavedMRTDepth)
	UpdateShaders()
	
	EnvRenderActive = False
End Function

Function SetShadowsCasting%(Entity%, Enable%)
	If Enable
		MaskEntity(Entity, EntityMask(Entity) Or 16)
	Else
		MaskEntity(Entity, (EntityMask(Entity) And (EntityMask(Entity) Xor 16)) Or 1)
	EndIf
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
				If mat\EmissiveColor Then State = State Or DEFERRED_EMISSIVECOLOR
				
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
			If (TextureFlags(t1) And 4) <> 0 Then State = State Or DEFERRED_MASKED
			
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

Function RenderDeferred%(Cam%, Tween# = 1.0, Flags% = 0, Destination% = 0)
	CurrentTween = Tween
	If GetInputEffect(DEFERRED_DIFF) <> 0
		Local ef.InputEffect, se.ShadeEffect
		Local Environment% = (Flags And RENDER_OFFSCREEN) <> 0
		Local EnvCapture% = (Flags And RENDER_ENVCAPTURE) <> 0
		Local RenderMask% = -1
		
		If EnvCapture Then RenderMask = 256
		
		If Destination = 0 Then Destination = BackBuffer()
		CurrTrisAmount = 0
		BatchesAmount = 0
		For ef.InputEffect = Each InputEffect
			If ef\Effect <> 0 Then EffectTechnique(ef\Effect, "Deferred")
		Next
		
		If EnvCapture
			BeginEnvironment(BufferWidth(Destination), BufferHeight(Destination))
		ElseIf (Not Environment)
			SetRenderParameters(-1, -1, opt\HDRRender)
		EndIf
		
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
		RenderWorld(CurrentTween, Cam, RenderMask, 1) ; ~ Render only opacity
		Count3D()
		
		CameraClsMode(Cam, 0, 1)
		
		If (Not Environment) Then ProcessLinearDepth(Cam)
		
		Local InvViewProjection% = CameraMatrix(Cam, 3, CurrentTween)
		
		For se.ShadeEffect = Each ShadeEffect
			EffectMatrix(se\Effect, "InvViewProj", InvViewProjection)
		Next
		
		EffectMatrix(ReflectionProbesEffect, "InvViewProj", InvViewProjection)
		
		WireFrame(False)
		ProcessGraphics(Cam, Environment)
		
		CameraClsMode(Cam, 0, 0)
		
		If (Not Environment)
			ProcessFog(fog\R, fog\G, fog\B)
			ProcessBloomAndSSAO(Cam, 1.0, 1.9, 0.047)
		EndIf
		
		SetBuffer(TextureBuffer(MRTColor), GetResolutionDepth())
		
		CameraClsMode(Cam, 0, 0)
		WireFrame(WireFrameState)
		RenderWorld(CurrentTween, Cam, RenderMask, 2)
		Count3D()
		
		CameraClsMode(Cam, 1, 1)
		WireFrame(False)
		
		If (Not Environment)
			;ProcessSSR(Cam)
			If opt\VolumetricLights Then ProcessBilateralBlur(Cam, MRTVolume, TempColorTexture, LinearDepth, MRTNormal, MRTColor, 3) ; ~ Use TempColorTexture texture to avoid creating additional textures
			ProcessMotionBlur(Cam, 1.0)
			PresentGBuffer(MRTColor, TextureBuffer(MRTAlbedo), GetResolutionDepth(), 2 - ((TextureFlags(MRTColor) And 4096) <> 0))
			If (Not ProcessFXAA(MRTAlbedo, Destination)) Then PresentGBuffer(MRTAlbedo, Destination)
		Else
			If EnvCapture
				PresentGBuffer(MRTColor, Destination, GetResolutionDepth(), True)
				EndEnvironment()
			Else
				PresentGBuffer(MRTColor, TextureBuffer(MRTAlbedo), GetResolutionDepth(), True)
			EndIf
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
	
	CameraClsMode(Cam, 0, 0)
	
	Local lp.LightPool, dl.DynamicLight
	
	CurrentCameraNear = GetCameraRangeNear(Cam)
	CurrentCameraFar = GetCameraRangeFar(Cam)
	
	ShowEntity(DeferredCone)
	ShowEntity(DeferredSphere)
	ShowEntity(DeferredQuad)
	ShowEntity(DeferredBox)
	
	BeginRender(CurrentTween, 4 Or 16) ; ~ Begin render light/environment volumes and shadowmaps
	
	Local SecondaryLight# = Min(SecondaryLightOn, 1.0)
	
	For lp.LightPool = Each LightPool
		If (Not EntityHidden(lp\l\OBJ)) Then RenderLight(Cam, lp\l\OBJ, lp\l\Range, lp\l\Length, lp\l\R, lp\l\G, lp\l\B, Max(lp\l\Fade * SecondaryLight, Environment), lp\l\LType, lp\l\FOV, lp\l\FOVTan, lp\l\CastShadows And DrawShadows, lp\l\Scattering * 0.15)
	Next
	
	For dl.DynamicLight = Each DynamicLight
		If (Not EntityHidden(dl\OBJ)) And (GetParent(dl\OBJ) = 0 Lor (Not EntityHidden(GetParent(dl\OBJ)))) Then RenderLight(Cam, dl\OBJ, dl\Range, dl\Length, dl\R, dl\G, dl\B, dl\Fade, dl\LType, dl\FOV, dl\FOV, dl\CastShadows And DrawShadows, dl\Scattering * 0.15)
	Next
	
	If (wi\NVGPower > 0 Lor wi\NightVision = 3) And wi\NightVision > 0 Then RenderLight(Cam, GetDummyPivot(EntityX(Cam, True, CurrentTween), EntityY(Cam, True, CurrentTween), EntityZ(Cam, True, CurrentTween)), 2500.0 * RoomScale, 0.0, 200, 200, 200, 2.5, DEFERRED_LIGHT_POINT, 90.0, 1.0, False, 0.0)
	
	If KeyDown(34) And opt\DebugMode = 1 Then RenderLight(Cam, GetDummyPivot(EntityX(Cam, True, CurrentTween), EntityY(Cam, True, CurrentTween), EntityZ(Cam, True, CurrentTween)), 25.0, 0.0, 200, 200, 200, 1.0, DEFERRED_LIGHT_SPOT, 60.0, 0.57, False, 0.0)
	
	EndRender()
	
	PresentGBuffer(MRTLighting, TextureBuffer(MRTColor), GetResolutionDepth(), False, 3)
	
	; ~ Render reflection probes
	If opt\Reflections
		Local AR# = fog\CurrAmbientR, AG# = fog\CurrAmbientG, AB# = fog\CurrAmbientB
		
		PrepareReflectionProbes(TempColorTexture)
		For rp.ReflectionProbe = Each ReflectionProbe
			If rp\RT\EnvironmentMap <> 0 And rp\RT\EnvironmentR > 0.0 And rp\RT\EnvironmentG > 0.0 And rp\RT\EnvironmentB > 0.0 Then RenderReflectionProbe(Cam, 255.0 * (AR / rp\RT\EnvironmentR), 255.0 * (AG / rp\RT\EnvironmentG), 255.0 * (AB / rp\RT\EnvironmentB), rp\RT\EnvironmentMap, rp\Bounds, rp\Delta, rp\RT\PrevEnvironmentMap, rp\RT\EnvironmentBlend)
		Next
	EndIf
	
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, CurrentCameraNear, CurrentCameraFar)
	
	HideEntity(DeferredCone)
	HideEntity(DeferredSphere)
	HideEntity(DeferredQuad)
	HideEntity(DeferredBox)
	
	If opt\Reflections Then BlendReflectionProbes(TempColorTexture)
End Function

Function RenderLight%(Cam%, OBJ%, Range#, Length#, R%, G%, B%, Intensity#, LType%, FOV# = 90.0, FOVTan# = 1.0, CastShadows% = True, Scattering# = 1.0)
	If Intensity <= 0.0 Then Return
	
	Local DistToLight# = EntityDistance(Cam, OBJ)
	
	If LType <> DEFERRED_LIGHT_DIRECTIONAL And DistToLight - Range > GetCameraRangeFar(Cam) Then Return
	
	Local Volume%
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
	If Scattering > 0.0 And LType <> DEFERRED_LIGHT_DIRECTIONAL Then EffectBits = EffectBits Or DEFERRED_SHADE_VOLUME_QUALITY[opt\VolumetricLights]
	If Length > 0.0 Then EffectBits = EffectBits Or DEFERRED_SHADE_TUBE
	
	Local ShadeEffect% = GetShadeEffect(EffectBits)
	
	If (Not CastShadows) Then EffectFloat(ShadeEffect, "NormalOffset", 0.0)
	
	EffectVector(ShadeEffect, "EyePos", EntityX(Cam, True, CurrentTween), EntityY(Cam, True, CurrentTween), EntityZ(Cam, True, CurrentTween))
	
	FOV = Clamp(FOV, 0.0, 170.0)
	
	Select LType
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, DeferredShadowMapCube[GetShadowMapMip(Range, DistToLight)], LType, OBJ, Range, FOV, FOVTan)
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0) + (DistToLight * Range))
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
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
			CameraZoom(DeferredCamera, 1.0 / FOVTan)
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(Shadowmap), TextureHeight(Shadowmap))
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, SLOPE_BIAS)
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, Shadowmap, LType, OBJ, Range, FOV, FOVTan)
			
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
			CameraRange(Cam, CurrentCameraNear, CurrentCameraFar)
			
			If CastShadows Then RenderShadowMap(ShadeEffect, Cam, DeferredShadowMap[SHADOW_MAP_MIPMAPS], LType, OBJ, Range, FOV, FOVTan)
			
			Cam = QuadCamera
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

Function RenderShadowMap%(ShadeEffect%, MainCam%, ShadowMap%, LType%, OBJ%, Range#, FOV#, FOVTan# = 1.0)
	Local ShadowMapWidth% = TextureWidth(ShadowMap)
	Local ShadowMapHeight% = TextureHeight(ShadowMap)
	Local CubeShadow% = (LType = DEFERRED_LIGHT_POINT And opt\DXLevel >= 100)
	Local DummyTexture% = FindDummyTexture(ShadowMapWidth, ShadowMapHeight, CubeShadow)
	Local ScaledNormalOffset#
	
	If DummyTexture = 0 Then Return
	
	Select LType
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			ShadowManagerSetParams(SHADOW_BIAS * 0.01, SLOPE_BIAS, 16, CurrentTween)
			ShadowManagerRenderDir(ShadowManagerSlotDir, TextureBuffer(DummyTexture), TextureBuffer(ShadowMap), ShadeEffect, MainCam, EntityX(OBJ, True, 0.0), EntityY(OBJ, True, 0.0), EntityZ(OBJ, True, 0.0), EntityPitch(OBJ, True, 0.0), EntityYaw(OBJ, True, 0.0), EntityX(OBJ, True, 1.0), EntityY(OBJ, True, 1.0), EntityZ(OBJ, True, 1.0), EntityPitch(OBJ, True, 1.0), EntityYaw(OBJ, True, 1.0), DIRECTIONAL_LIGHT_EXTRUSION, DIRLIGHT_SHADOW_CASCADES, DIRLIGHT_SHADOW_SPLIT_LAMBDA, CurrentCameraFar)
			EffectInt(ShadeEffect, "ShadowMapAddress", 4)
			EffectMatrix(ShadeEffect, "CascadeView", CameraMatrix(MainCam, 0, CurrentTween))
			EffectFloat(ShadeEffect, "NormalOffset", 0)
			;[End Block]
		Case DEFERRED_LIGHT_POINT
			;[Block]
			ShadowManagerSetParams(SHADOW_BIAS, SLOPE_BIAS, 16, CurrentTween)
			ShadowManagerRenderPoint(ShadowManagerSlotPoint, TextureBuffer(DummyTexture), TextureBuffer(ShadowMap), ShadeEffect, MainCam, EntityX(OBJ, True, 0.0), EntityY(OBJ, True, 0.0), EntityZ(OBJ, True, 0.0), EntityX(OBJ, True, 1.0), EntityY(OBJ, True, 1.0), EntityZ(OBJ, True, 1.0), Range)
			ScaledNormalOffset = 2.0 * DEFERRED_LIGHT_POINT_CULLING_SCALE_TAN * Range
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			EffectFloat(ShadeEffect, "NormalOffset", ScaledNormalOffset)
			EffectInt(ShadeEffect, "ShadowMapAddress", 3)
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			ShadowManagerSetParams(SHADOW_BIAS, SLOPE_BIAS, 16, CurrentTween)
			ShadowManagerRenderSpot(ShadowManagerSlotSpot, TextureBuffer(DummyTexture), TextureBuffer(ShadowMap), ShadeEffect, MainCam, EntityX(OBJ, True, 0.0), EntityY(OBJ, True, 0.0), EntityZ(OBJ, True, 0.0), EntityPitch(OBJ, True, 0.0), EntityYaw(OBJ, True, 0.0), EntityX(OBJ, True, 1.0), EntityY(OBJ, True, 1.0), EntityZ(OBJ, True, 1.0), EntityPitch(OBJ, True, 1.0), EntityYaw(OBJ, True, 1.0), Range, FOV)
			ScaledNormalOffset = 2.0 * FOVTan * Range
			ScaledNormalOffset = ScaledNormalOffset * NORMAL_OFFSET
			EffectFloat(ShadeEffect, "NormalOffset", ScaledNormalOffset)
			EffectInt(ShadeEffect, "ShadowMapAddress", 3)
			;[End Block]
	End Select
	
	EffectVector(ShadeEffect, "ShadowMapSize", ShadowMapWidth, ShadowMapHeight)
	If CubeShadow
		EffectTexture(ShadeEffect, "tShadowCubeMap", ShadowMap)
	Else
		EffectTexture(ShadeEffect, "tShadowMap", ShadowMap)
	EndIf
End Function

Function CreateShadowMap%(Width%, Height%)
	Return(CreateTexture(Width, Height, 8192))
End Function

Function CreateCubeShadowMap%(Size%)
	Return(CreateTexture(Size, Size, 8192 Or 128))
End Function

Function CreateDummyTexture%(Width%, Height%, Cube% = False)
	If FindDummyTexture(Width, Height, Cube) <> 0 Then Return
	
	Local t.DummyTexture
	
	t.DummyTexture = New DummyTexture
	t\Cube = Cube
	If Cube
		t\Tex = CreateTexture(Width, Height, 1 + 256 + 1024 + 128)
	Else
		t\Tex = CreateTexture(Width, Height, 1 + 256 + 1024)
	EndIf
End Function

Function CreateCubeDummyTexture%(Size%)
	CreateDummyTexture(Size, Size, True)
End Function

Function FindDummyTexture%(Width%, Height%, Cube% = False)
	Local dt.DummyTexture
	
	For dt.DummyTexture = Each DummyTexture
		If TextureWidth(dt\Tex) = Width And TextureHeight(dt\Tex) = Height And dt\Cube = Cube Then Return(dt\Tex)
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

Function SetEmissiveMultiply%(Value#, Force% = False)
	If EmissiveMultiply <> Value Lor Force
		Local ef.InputEffect
		
		EmissiveMultiply = Value
		For ef.InputEffect = Each InputEffect
			If (ef\Bit And DEFERRED_DIFFEMISSIVEMUL) Then EffectFloat(ef\Effect, "EmissiveMultiply", EmissiveMultiply)
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
	Field FOV#, FOVTan#
	Field Scattering#
	Field Length#
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
	dl\FOVTan = Tan(dl\FOV)
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

Function LightIntensity%(Entity%, Intensity#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\Fade = Intensity
End Function

Function LightFOV%(Entity%, FOV#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null
		dl\FOV = FOV
		dl\FOVTan = Tan(dl\FOV * 0.5)
	EndIf
End Function

Function LightCastShadows%(Entity%, CastShadows%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\CastShadows = CastShadows
End Function

Function LightScattering%(Entity%, Scattering#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\Scattering = Scattering
End Function

Function LightLength%(Entity%, Length#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\Length = Length
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
		
		If (Not LoadShadeEffect(Bit, "Shade.fx", Defines + "REVERSEDZ")) Then Return(0)
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
	Return ; ~ TODO (for transparency)
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
	
	fog\R = 0
	fog\G = 0
	fog\B = 0
	
	For i = 0 To 5
		SetCubeFace(CubeTexture, i)
		RotateEntity(Camera, CubeRotateX[i], CubeRotateY[i], 0.0)
		RenderDeferred(Camera, 1.0, RENDER_OFFSCREEN Or RENDER_ENVCAPTURE, TextureBuffer(CubeTexture))
	Next
	
	opt\MotionBlur = PrevBlur
	CameraProjMode(Camera, 0)
	Return(CubeTexture)
End Function

Const Log2# = 0.6931472

Function RenderReflectionProbe%(Cam%, R%, G%, B%, Texture%, Box%, Delta% = 0, PrevTexture% = 0, Blend# = 1.0)
	If Texture = 0 Lor Box = 0 Then Return
	
	PositionEntity(DeferredBox, EntityX(Box, True), EntityY(Box, True), EntityZ(Box, True))
	RotateEntity(DeferredBox, EntityPitch(Box, True), EntityYaw(Box, True), EntityRoll(Box, True))
	ScaleEntity(DeferredBox, EntityScaleX(Box, True), EntityScaleY(Box, True), EntityScaleZ(Box, True))
	
	EntityTexture(DeferredBox, Texture, 0, 3)
	If PrevTexture <> 0 Then EntityTexture(DeferredBox, PrevTexture, 0, 4)
	EntityEffect(DeferredBox, ReflectionProbesEffect)
	
	EffectVector(ReflectionProbesEffect, "ProbeColor", R / 255.0, G / 255.0, B / 255.0)
	EffectVector(ReflectionProbesEffect, "ProbeDelta", Sin(Delta), Cos(Delta))
	EffectFloat(ReflectionProbesEffect, "ProbeMip", Log(TextureWidth(Texture)) / Log2)
	EffectFloat(ReflectionProbesEffect, "ProbeBlend", Blend)
	
	CameraRange(Cam, 0.1, 500000.0)
	
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
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS