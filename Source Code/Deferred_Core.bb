Const DEFERRED_LIGHT_DIRECTIONAL% = 1
Const DEFERRED_LIGHT_POINT% = 2
Const DEFERRED_LIGHT_SPOT% = 3

Const DEFERRED_DIFF% = 0
Const DEFERRED_DIFFALPHA% = 1
Const DEFERRED_DIFFNORMAL% = 2
Const DEFERRED_DIFFROUGH% = 4
Const DEFERRED_DIFFEMISSION% = 8
Const DEFERRED_DIFFNOLIT% = 16
Const DEFERRED_NONE% = 32

Const MAX_DEFERRED_VARIATIONS% = 128
Const MAX_DEFERRED_INPUTS% = 16

Const DIRECTIONAL_LIGHT_TIME% = 0
Const DIRECTIONAL_LIGHT_RANGE# = 0.01
Const DIRECTIONAL_LIGHT_EXTRUSION# = 20.0
Global SHADOW_BIAS# = 0.0002

Const SHADOW_MAP_MIPMAPS% = 1 ; ~ Don't change this
Const SHADOW_MAP_SIZE% = 1024
Const DIRLIGHT_SHADOW_MAP_SIZE% = 1024

Global MRTColor%
Global MRTAlbedo%
Global MRTDepth%
Global MRTNormal%

Global DeferredInputEffect%[MAX_DEFERRED_VARIATIONS]

Global DeferredInput%[MAX_DEFERRED_INPUTS], DeferredShade%

Global DeferredShadowMapCube%[SHADOW_MAP_MIPMAPS]
Global DeferredShadowMap%[SHADOW_MAP_MIPMAPS + 1]
Global DeferredShadowMapDummy%[4]

Global DeferredCamera%, QuadCamera%
Global DeferredSphere%, DeferredCone%, DeferredQuad%
Global DirectionalLightUpdate%
Global ShadowsDistance#

Global CubeRotateX#[6]
Global CubeRotateY#[6]

Global DEFERRED_INPUTS% = 0

CubeRotateX[0] = 0 : CubeRotateY[0] = 90
CubeRotateX[1] = 0 : CubeRotateY[1] = 0
CubeRotateX[2] = 0 : CubeRotateY[2] = -90
CubeRotateX[3] = 0 : CubeRotateY[3] = 180
CubeRotateX[4] = -90 : CubeRotateY[4] = 0
CubeRotateX[5] = 90 : CubeRotateY[5] = 0

Global PostEffectQuad%

Global BloomEffect%
Global BloomTex%, BloomBlur%

Global ColorCorrectionEffect%
Global PresentEffect%

Global SSAOEffect%
Global NoiseTexture%

Global FXAAEffect%

Function InitDeferred%()
	Local i%
	
	DeferredInputEffect[DEFERRED_DIFF] = LoadEffect("Source Code\Deffered\Input.fx")
	DeferredInputEffect[DEFERRED_DIFFNOLIT] = LoadEffectEx("Source Code\Deffered\Input.fx", "NOLIT")
	DeferredInputEffect[DEFERRED_DIFFALPHA] = LoadEffectEx("Source Code\Deffered\Input.fx", "TRANSPARENT")
	DeferredInputEffect[DEFERRED_DIFFNORMAL] = LoadEffectEx("Source Code\Deffered\Input.fx", "NORMALMAP")
	DeferredInputEffect[DEFERRED_DIFFROUGH] = LoadEffectEx("Source Code\Deffered\Input.fx", "ROUGHMAP")
	DeferredInputEffect[DEFERRED_DIFFEMISSION] = LoadEffectEx("Source Code\Deffered\Input.fx", "EMISSIVEMAP")
	DeferredInputEffect[DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH] = LoadEffectEx("Source Code\Deffered\Input.fx", "NORMALMAP ROUGHMAP")
	DeferredInputEffect[DEFERRED_DIFFNORMAL Or DEFERRED_DIFFEMISSION] = LoadEffectEx("Source Code\Deffered\Input.fx", "NORMALMAP EMISSIVEMAP")
	DeferredInputEffect[DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSION] = LoadEffectEx("Source Code\Deffered\Input.fx", "ROUGHMAP EMISSIVEMAP")
	DeferredInputEffect[DEFERRED_DIFFNORMAL Or DEFERRED_DIFFROUGH Or DEFERRED_DIFFEMISSION] = LoadEffectEx("Source Code\Deffered\Input.fx", "NORMALMAP ROUGHMAP EMISSIVEMAP")
	BloomEffect = LoadEffect("Source Code\Shaders\Bloom.fx")
	ColorCorrectionEffect = LoadEffect("Source Code\Shaders\ColorCorrection.fx")
	PresentEffect = LoadEffect("Source Code\Shaders\Present.fx")
	SSAOEffect = LoadEffect("Source Code\Shaders\SSAO.fx")
	FXAAEffect = LoadEffect("Source Code\Shaders\FXAA.fx")
	
	DEFERRED_INPUTS = 0
	For i = 0 To MAX_DEFERRED_VARIATIONS - 1
		If DeferredInputEffect[i] <> 0
			DeferredInput[DEFERRED_INPUTS] = DeferredInputEffect[i]
			DEFERRED_INPUTS = DEFERRED_INPUTS + 1
		EndIf
	Next
	
	DeferredShade = LoadEffect("Source Code\Deffered\Shade.fx")
	
	Local Width% = GraphicsWidth(), Height% = GraphicsHeight()
	
	MRTColor = CreateTexture(Width, Height, 1 + 2 + 256 + 16384)
	MRTAlbedo = CreateTexture(Width, Height, 1 + 2 + 256 + 16384)
	MRTDepth = CreateTexture(Width, Height, 131072)
	MRTNormal = CreateTexture(Width, Height, 1 + 2 + 256 + 16384)
	
	For i = 1 To SHADOW_MAP_MIPMAPS
		Local iRounded% = RoundTwo(i)
		
		DeferredShadowMapCube[i - 1] = CreateShadowMap(SHADOW_MAP_SIZE * 6 / iRounded, SHADOW_MAP_SIZE / iRounded)
		DeferredShadowMap[i - 1] = CreateShadowMap(SHADOW_MAP_SIZE / iRounded, SHADOW_MAP_SIZE / iRounded)
	Next
	
	DeferredShadowMap[SHADOW_MAP_MIPMAPS] = CreateShadowMap(DIRLIGHT_SHADOW_MAP_SIZE, DIRLIGHT_SHADOW_MAP_SIZE)
	
	DeferredShadowMapDummy[DEFERRED_LIGHT_POINT] = CreateTexture(SHADOW_MAP_SIZE * 6, SHADOW_MAP_SIZE, 1 + 256 + 16384)
	DeferredShadowMapDummy[DEFERRED_LIGHT_SPOT] = CreateTexture(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE, 1 + 256 + 16384)
	DeferredShadowMapDummy[DEFERRED_LIGHT_DIRECTIONAL] = CreateTexture(DIRLIGHT_SHADOW_MAP_SIZE, DIRLIGHT_SHADOW_MAP_SIZE, 1 + 256 + 16384)
	
	Local FaceSelectCubeMap% = CreateTexture(1, 1, 1 + 2 + 128 + 512)
	
	For i = 0 To 5
		SetCubeFace(FaceSelectCubeMap, i)
		LockBuffer(TextureBuffer(FaceSelectCubeMap))
		WritePixelFast(0, 0, i Shl 16, TextureBuffer(FaceSelectCubeMap))
		UnlockBuffer(TextureBuffer(FaceSelectCubeMap))
	Next
	
	DeferredCamera = CreateCamera()
	CameraClsMode(DeferredCamera, 0, 1)
	CameraColorWrite(DeferredCamera, False)
	HideEntity(DeferredCamera)
	
	QuadCamera = CreateCamera()
	CameraClsMode(QuadCamera, 0, 0)
	HideEntity(QuadCamera)
	
	Local AdjustMatrix% = CreateBank(64)
	
	PokeFloat(AdjustMatrix, 0, 0.5)
	PokeFloat(AdjustMatrix, 20, -0.5)
	PokeFloat(AdjustMatrix, 40, 1.0)
	PokeFloat(AdjustMatrix, 48, 0.5)
	PokeFloat(AdjustMatrix, 52, 0.5)
	PokeFloat(AdjustMatrix, 60, 1.0)
	EffectMatrix(DeferredShade, "ShadowsAdjust", BankPointer(AdjustMatrix))
	FreeBank(AdjustMatrix) : AdjustMatrix = 0
	
	Local SpotTexture% = LoadTexture("GFX\Other\spot.png")
	
	DeferredSphere = CreateLightVolume(DEFERRED_LIGHT_POINT)
	DeferredCone = CreateLightVolume(DEFERRED_LIGHT_SPOT)
	DeferredQuad = CreateLightVolume(DEFERRED_LIGHT_DIRECTIONAL)
	
	EntityEffect(DeferredSphere, DeferredShade)
	EntityTexture(DeferredSphere, MRTAlbedo, 0, 0)
	EntityTexture(DeferredSphere, MRTNormal, 0, 1)
	EntityTexture(DeferredSphere, MRTDepth, 0, 2)
	EntityTexture(DeferredSphere, FaceSelectCubeMap, 0, 3)
	EntityTexture(DeferredSphere, SpotTexture, 0, 4)
	EntityOrder(DeferredSphere, 10000000)
	EntityBlend(DeferredSphere, 3)
	EntityFX(DeferredSphere, 8)
	
	EntityEffect(DeferredCone, DeferredShade)
	EntityTexture(DeferredCone, MRTAlbedo, 0, 0)
	EntityTexture(DeferredCone, MRTNormal, 0, 1)
	EntityTexture(DeferredCone, MRTDepth, 0, 2)
	EntityTexture(DeferredCone, FaceSelectCubeMap, 0, 3)
	EntityTexture(DeferredCone, SpotTexture, 0, 4)
	EntityOrder(DeferredCone, 10000000)
	EntityBlend(DeferredCone, 3)
	EntityFX(DeferredCone, 8)
	
	EntityEffect(DeferredQuad, DeferredShade)
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
	
	; ~ Volumes mask
	MaskEntity(DeferredSphere, 4)
	MaskEntity(DeferredCone, 4)
	MaskEntity(DeferredQuad, 4)
	
	SetShadowsDistance(4.0)
	DirectionalLightUpdate = 0
	
	BloomTex = CreateTexture(Width / 4, Height / 4, 1 + 256 + 16384)
	BloomBlur = CreateTexture(Width / 4, Height / 4, 1 + 256 + 16384)
	
	PostEffectQuad = CreateFullscreenQuad(QuadCamera)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	EntityOrder(PostEffectQuad, 10000000)
	EntityFX(PostEffectQuad, 8)
	
	NoiseTexture = LoadTexture("GFX\Other\ssao.png")
	
	Delete Each DynamicLight
End Function

Function SetDeferredParticle%(Entity%, Enable% = True)
	SetShadowsCasting(Entity, False) ;Enable)
End Function

Function SetShadowsCasting%(Entity%, Enable%)
	MaskEntity(Entity, 1 + 15 * Enable)
End Function

Function SetDeferredEntity%(Entity%, Shadows% = False, State% = -1)
	If DeferredShade = 0 Then Return
	If State <> -1
		Local i%, SF%, b%, SurfCount%
		
		EntityEffect(Entity, DeferredInputEffect[State])
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
	
	SetShadowsCasting(Entity, Shadows)
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
					If mat\Emissive <> 0 Then State = State Or DEFERRED_DIFFEMISSION
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
	
	BrushEffect(Brush, DeferredInputEffect[State])
End Function

Function UpdateEntityMaterial%(Ent%, State% = -1, Frame% = 0)
	Local Brush% = GetEntityBrush(Ent)
	
	SetDeferredBrush(Brush, State, Frame)
	PaintEntity(Ent, Brush)
	FreeBrush(Brush) : Brush = 0
End Function

Function ProcessDeferred%(Cam%, Tween#)
	If DeferredShade <> 0 And DeferredInputEffect[DEFERRED_DIFF] <> 0
		SetBuffer(TextureBuffer(MRTColor))
		SetBuffer(TextureBuffer(MRTAlbedo), 1)
		SetBuffer(TextureBuffer(MRTNormal), 2)
		SetBuffer(TextureBuffer(MRTDepth), 3)
		RenderWorld(Tween)
		ProcessSSAO(Cam, 1.5, 0.2)
		EffectMatrix(DeferredShade, "InvViewProj", CameraMatrix(Cam, 3, Tween))
		ProcessAllLights(Cam, Tween)
		
		ProcessFXAA()
		ProcessBloom()
		ProcessColorCorrection()
		PresentGBuffer(MRTColor, BackBuffer())
	Else
		RenderWorld(Tween)
	EndIf
End Function

Function ProcessAllLights%(Cam%, Tween#)
	Local i%
	
	For i = 0 To DEFERRED_INPUTS - 1
		EffectTechnique(DeferredInput[i], "Depth")
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
		If (Not EntityHidden(l\OBJ)) Then ProcessLight(Cam, EntityX(l\OBJ, True), EntityY(l\OBJ, True), EntityZ(l\OBJ, True), EntityPitch(l\OBJ, True), EntityYaw(l\OBJ, True), l\Range, l\R, l\G, l\B, l\Fade * SecondaryLightOn, l\lType, l\FOV, l\CastShadows, Tween)
	Next
	
	For dl.DynamicLight = Each DynamicLight
		If (Not EntityHidden(dl\OBJ)) And (GetParent(dl\OBJ) = 0 Lor (Not EntityHidden(GetParent(dl\OBJ)))) Then ProcessLight(Cam, EntityX(dl\OBJ, True), EntityY(dl\OBJ, True), EntityZ(dl\OBJ, True), EntityPitch(dl\OBJ, True), EntityYaw(dl\OBJ, True), dl\Range, dl\R, dl\G, dl\B, dl\Fade, dl\lType, dl\FOV, dl\CastShadows, Tween)
	Next
	
	If KeyDown(34) Then ProcessLight(Cam, EntityX(Cam), EntityY(Cam), EntityZ(Cam), EntityPitch(Cam), EntityYaw(Cam), 25.0, 200, 200, 200, 1.0, DEFERRED_LIGHT_SPOT, 35.0, False, Tween)
	
	EndRender()
	
	HideEntity(DeferredCone)
	HideEntity(DeferredSphere)
	HideEntity(DeferredQuad)
	
	CameraClsMode(Cam, 1, 1)
	CameraRange(Cam, Near, Far)
	
	If DirectionalLightUpdate < MilliSecs() Then DirectionalLightUpdate = MilliSecs() + DIRECTIONAL_LIGHT_TIME
	
	For i = 0 To DEFERRED_INPUTS - 1
		EffectTechnique(DeferredInput[i], "GBuffer")
	Next
End Function

Function ProcessLight%(Cam%, x#, y#, z#, Pitch#, Yaw#, Range#, R%, G%, B%, Intensity#, LightType%, FOV# = 90.0, Shadows% = True, Tween# = 1.0)
	Local VolumeScale# = Range * 1.25
	Local DistToLight# = 1.0
	Local Volume%, TanValue#
	
	EffectBool(DeferredShade, "Shadowed", False)
	
	Select LightType
		Case DEFERRED_LIGHT_POINT
			;[Block]
			Volume = DeferredSphere
			PositionEntity(Volume, x, y, z)
			ScaleEntity(Volume, VolumeScale, VolumeScale, VolumeScale)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			
			DistToLight = EntityDistance(Cam, Volume)
			If Shadows Then RenderShadowMap(Cam, DeferredShadowMapCube[GetShadowMapMip(Range, DistToLight)], LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			
			EffectTechnique(DeferredShade, "PointLight")
			CameraRange(Cam, 0.01, DistToLight + (Range * 2.0))
			;[End Block]
		Case DEFERRED_LIGHT_SPOT
			;[Block]
			Volume = DeferredCone
			TanValue = Tan(FOV * 0.5)
			VolumeScale = TanValue * Range
			
			PositionEntity(Volume, x, y, z)
			RotateEntity(Volume, Pitch, Yaw, 0.0)
			ScaleEntity(Volume, VolumeScale, VolumeScale, Range)
			
			If (Not EntityInView(Volume, Cam)) Then Return
			DistToLight = EntityDistance(Cam, Volume)
			
			Local Shadowmap% = DeferredShadowMap[GetShadowMapMip(Range, DistToLight)]
			
			PositionEntity(DeferredCamera, x, y, z)
			RotateEntity(DeferredCamera, Pitch, Yaw, 0.0)
			CameraRange(DeferredCamera, 0.01, Range)
			CameraProjMode(DeferredCamera, 1)
			CameraZoom(DeferredCamera, 1.0 / TanValue)
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(Shadowmap), TextureHeight(Shadowmap))
			CameraDepthBias(DeferredCamera, SHADOW_BIAS, 0.5)
			
			If Shadows Then RenderShadowMap(Cam, Shadowmap, LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			
			EffectTechnique(DeferredShade, "SpotLight")
			EffectMatrix(DeferredShade, "LightViewProj", CameraMatrix(DeferredCamera, 2, Tween))
			EffectVector(DeferredShade, "LightDirection", Sin(-Yaw), Tan(-Pitch), Cos(-Yaw))
			CameraRange(Cam, 0.01, DistToLight + ((VolumeScale + Range) * 2.0))
			;[End Block]
		Case DEFERRED_LIGHT_DIRECTIONAL
			;[Block]
			Volume = DeferredQuad
			
			If Shadows Then RenderShadowMap(Cam, DeferredShadowMap[0], LightType, x, y, z, Pitch, Yaw, Range, FOV, Tween)
			
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
	
	RenderEntity(Cam, Volume, Tween)
End Function

Global DEFERRED_LIGHT_POINT_CULLING_SCALE# = Tan(90.0 * 0.8)

Function RenderShadowMap%(MainCam%, ShadowMap%, LightType%, x#, y#, z#, Pitch#, Yaw#, Range#, FOV#, Tween# = 1.0)
	Local i%
	
	SetBuffer(TextureBuffer(DeferredShadowMapDummy[LightType]))
	
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
			CameraViewport(DeferredCamera, 0, 0, TextureWidth(ShadowMap), TextureHeight(ShadowMap))
			
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
			
			Local Width% = TextureWidth(ShadowMap) / 6
			Local Height% = TextureHeight(ShadowMap)
			Local CullingScale# = DEFERRED_LIGHT_POINT_CULLING_SCALE * Range
			
			PositionEntity(DeferredCone, x, y, z)
			
			For i = 0 To 5
				RotateEntity(DeferredCone, CubeRotateX[i], CubeRotateY[i], 0.0)
				ScaleEntity(DeferredCone, CullingScale, CullingScale, Range)
				
				If EntityInView(DeferredCone, MainCam)
					RotateEntity(DeferredCamera, CubeRotateX[i], CubeRotateY[i], 0.0)
					CameraViewport(DeferredCamera, i * Width, 0, Width, Height)
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
	
	EffectTexture(DeferredShade, "tShadowMap", ShadowMap)
	EffectBool(DeferredShade, "Shadowed", True)
	
	SetBuffer(TextureBuffer(MRTColor))
End Function

Function CreateShadowMap%(Width%, Height%)
	Return(CreateTexture(Width, Height, 524288))
End Function

Function CreateLightVolume(LightType%)
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

Function SetShadowsDistance%(Dist#)
	ShadowsDistance = Dist
End Function

Function GetShadowMapMip%(Range#, Dist#)
	Local MipLevel% = Floor((Dist / (Range + ShadowsDistance)) * (SHADOW_MAP_MIPMAPS - 1))
	
	Return(Min(Max(MipLevel, 0), (SHADOW_MAP_MIPMAPS - 1)))
End Function

; ==================================== POST EFFECTS

Function ProcessBloom%(Threshold# = 0.4)
	EffectFloat(BloomEffect, "BloomThreshold", Threshold)
	EntityEffect(PostEffectQuad, BloomEffect)
	EntityTexture(PostEffectQuad, BloomTex, 0, 1)
	EntityTexture(PostEffectQuad, BloomBlur, 0, 2)
	
	ShowEntity(PostEffectQuad)
	SetBuffer(TextureBuffer(BloomTex))
	EffectTechnique(BloomEffect, "Downsample")
	RenderEntity(QuadCamera, PostEffectQuad)
	
	SetBuffer(TextureBuffer(BloomBlur))
	EffectTechnique(BloomEffect, "BlurH")
	RenderEntity(QuadCamera, PostEffectQuad)
	
	SetBuffer(TextureBuffer(BloomTex))
	EffectTechnique(BloomEffect, "BlurV")
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectTechnique(BloomEffect, "Combine")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function ProcessColorCorrection%()
	EntityEffect(PostEffectQuad, ColorCorrectionEffect)
	ShowEntity(PostEffectQuad)
	EffectTechnique(ColorCorrectionEffect, "Main")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function ProcessSSAO%(Cam%, Strength#, Radius#)
	EffectFloat(SSAOEffect, "SSAOStrength", Strength)
	EffectFloat(SSAOEffect, "SSAORadius", Radius)
	EffectMatrix(SSAOEffect, "InvViewProj", CameraMatrix(Cam, 3))
	EffectVector(SSAOEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	
	EntityBlend(PostEffectQuad, 2)
	EntityEffect(PostEffectQuad, SSAOEffect)
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	EntityTexture(PostEffectQuad, NoiseTexture, 0, 4)
	
	ShowEntity(PostEffectQuad)
	EffectTechnique(SSAOEffect, "Main")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	EntityBlend(PostEffectQuad, 0)
End Function

Function ProcessFXAA%()
	EntityEffect(PostEffectQuad, FXAAEffect)
	
	ShowEntity(PostEffectQuad)
	EffectTechnique(FXAAEffect, "Main")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function PresentGBuffer%(Tex%, Dest% = 0)
	EntityEffect(PostEffectQuad, PresentEffect)
	EntityTexture(PostEffectQuad, Tex, 0, 0)
	ShowEntity(PostEffectQuad)
	SetBuffer(Dest)
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
End Function

; ==================================== DYNAMIC LIGHTS

Type DynamicLight
	Field OBJ%
	Field lType%
	Field R%, G%, B%
	Field Range#
	Field Fade#
	Field FOV#
	Field CastShadows%
End Type

Function FindDynamicLight.DynamicLight(OBJ%)
	Local dl.DynamicLight
	
	For dl.DynamicLight = Each DynamicLight
		If dl\OBJ = OBJ Then Return(dl)
	Next
End Function

Function CreateLight(lType%, Parent% = 0)
	Local dl.DynamicLight = New DynamicLight
	
	dl\OBJ = CreatePivot(Parent)
	dl\lType = lType
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

Function LightFOV%(Entity%, Range#)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\FOV = Range
End Function

Function LightShadows(Entity%, CastShadows%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Then dl\CastShadows = CastShadows
End Function

Function OnLightDestruct(Entity%)
	Local dl.DynamicLight = FindDynamicLight(Entity)
	
	If dl <> Null Delete(dl)
End Function

; ====================================

Function LoadEffectEx%(File$, Defines$ = "")
	Local f% = ReadFile(File)
	
	If f = 0 Then Return
	
	Local i%
	Local Export$ = StripFileName(File) + "TEMP_EFFECT_FILE.fx"
	Local c% = WriteFile(Export)
	
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