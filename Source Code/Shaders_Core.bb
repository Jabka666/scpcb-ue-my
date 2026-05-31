Function LoadEffectEx%(File$, Defines$ = "", Necessary% = True)
	Local Effect% = LoadEffect(File, Defines)
	
	If Necessary And GetEffectError() <> "" Then RuntimeErrorEx(Format(Format(GetLocalString("runerr", "effect.failed.load"), File, "{0}"), GetEffectError(), "{1}"))
	UpdateLoadingContinuous()
	Return(Effect)
End Function

Const DEFERRED_PATH$ = "GFX\Shaders\Deferred\"
Const POSTEFFECTS_PATH$ = "GFX\Shaders\PostEffects\"

Global PostEffectQuad%

Global BloomEffect%
Global BloomTex%, BloomH_A%, BloomV_A%, BloomH_B%, BloomV_B%, BloomH_C%, BloomV_C%

Global ColorCorrectionEffect%
Global PresentEffect%

Global SSAOEffect%, SSAOBlurH%, SSAOBlurV%, SSAODepth%, SSAODepthLow%, SSAONormalLow%

Global LinearDepth%

Global NoiseTexture%

Global FXAAEffect%

Global MotionBlurEffect%

Global GammaEffect%

Global FogEffect%

Global ReflectionProbesEffect%, BlendProbesEffect%

Global BilateralBlurEffect%

Global EffectsBits% = -1

Global PostEffect%

Function InitShaders%()
	Local Width% = opt\GraphicWidth
	Local Height% = opt\GraphicHeight
	
	ReloadPostEffects()
	
	If BloomEffect = 0 Then BloomEffect = LoadEffectEx(POSTEFFECTS_PATH + "Bloom.fx")
	;If ColorCorrectionEffect = 0 Then ColorCorrectionEffect = LoadEffectEx(POSTEFFECTS_PATH + "ColorCorrection.fx")
	If PresentEffect = 0 Then PresentEffect = LoadEffectEx(POSTEFFECTS_PATH + "Present.fx")
	If SSAOEffect = 0 Then SSAOEffect = LoadEffectEx(POSTEFFECTS_PATH + "SSAO.fx")
	If FXAAEffect = 0 Then FXAAEffect = LoadEffectEx(POSTEFFECTS_PATH + "FXAA.fx")
	If MotionBlurEffect = 0 Then MotionBlurEffect = LoadEffectEx(POSTEFFECTS_PATH + "MotionBlur.fx")
	;If GammaEffect = 0 Then GammaEffect = LoadEffectEx(POSTEFFECTS_PATH + "Gamma.fx", "", False)
	If FogEffect = 0 Then FogEffect = LoadEffectEx(POSTEFFECTS_PATH + "Fog.fx", "", True)
	If ReflectionProbesEffect = 0 Then ReflectionProbesEffect = LoadEffectEx(DEFERRED_PATH + "ReflectionProbe.fx", "", True)
	If BlendProbesEffect = 0 Then BlendProbesEffect = LoadEffectEx(POSTEFFECTS_PATH + "BlendProbes.fx", "", True)
	If BilateralBlurEffect = 0 Then BilateralBlurEffect = LoadEffectEx(POSTEFFECTS_PATH + "BilateralBlur.fx", "", True)
	PostEffect = 0
End Function

Function ReloadPostEffects%()
	Local Width% = TextureWidth(MRTColor)
	Local Height% = TextureHeight(MRTColor)
	
	PostEffect = 0
	If PostEffectQuad <> 0
		FreeEntity(PostEffectQuad)
		
		FreeTexture(BloomTex)
		FreeTexture(BloomH_A)
		FreeTexture(BloomV_A)
		FreeTexture(BloomH_B)
		FreeTexture(BloomV_B)
		FreeTexture(BloomH_C)
		FreeTexture(BloomV_C)
		
		FreeTexture(SSAOBlurV)
		FreeTexture(SSAOBlurH)
		FreeTexture(SSAODepth)
		FreeTexture(SSAODepthLow)
		FreeTexture(SSAONormalLow)
		
		FreeTexture(LinearDepth)
	EndIf
	
	PostEffectQuad = CreateFullscreenQuad(TextureWidth(MRTColor), TextureHeight(MRTColor), QuadCamera)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	EntityOrder(PostEffectQuad, 10000000)
	EntityFX(PostEffectQuad, 8)
	HideEntity(PostEffectQuad)
	
	BloomTex = CreateTexture(Width / 2, Height / 2, 1024 + 4096)
	BloomH_A = CreateTexture(Width / 2, Height / 2, 1024 + 4096)
	BloomV_A = CreateTexture(Width / 2, Height / 2, 1024 + 4096)
	
	BloomH_B = CreateTexture(Width / 4, Height / 4, 1024 + 4096)
	BloomV_B = CreateTexture(Width / 4, Height / 4, 1024 + 4096)
	
	BloomH_C = CreateTexture(Width / 8, Height / 8, 1024 + 4096)
	BloomV_C = CreateTexture(Width / 8, Height / 8, 1024 + 4096)
	
	SSAOBlurV = CreateTexture(Width / 2, Height / 2, 1024 + 131072)
	SSAOBlurH = CreateTexture(Width / 2, Height / 2, 1024 + 131072)
	SSAODepth = CreateTexture(Width, Height, 1024 + 2048)
	SSAODepthLow = CreateTexture(Width / 2, Height / 2, 1024 + 2048)
	SSAONormalLow = CreateTexture(Width / 2, Height / 2, 1024 + 4096)
	
	LinearDepth = CreateTexture(Width, Height, 1024 + 2048)
End Function

Function GetPostEffectQuad%()
	Return(PostEffectQuad)
End Function

Function ProcessBloomAndSSAO%(Cam%, BloomThreshold#, Strength#, Radius#) ; ~ Process SSAO with Bloom to prevent conflicts
	If BloomEffect = 0 Lor (Not opt\Bloom)
		ProcessSSAO(Cam, Strength, Radius)
		Return
	EndIf
	
	EffectFloat(BloomEffect, "BloomSensitivity", BloomThreshold)
	
	Local BloomTexWidth% = TextureWidth(BloomTex)
	Local BloomTexHeight% = TextureHeight(BloomTex)
	Local Aspect# = Float(TextureWidth(MRTColor)) / Float(TextureHeight(MRTColor))
	
	EffectVector(BloomEffect, "HighestSize", 1080.0 * Aspect, 1080.0)
	
	EffectVector(BloomEffect, "BlurInvSize", 0.5 / BloomTexWidth * 2, 0.5 / BloomTexWidth * 2)
	RenderEffectQuad(BloomEffect, BloomTex, "Luma")
	
	EffectVector(BloomEffect, "BlurInvSize", 0.5 / BloomTexWidth, 0.5 / BloomTexHeight)
	EffectFloat(BloomEffect, "BlurSize", 2.0)
	EntityTexture(PostEffectQuad, BloomTex, 0, 1)
	RenderEffectQuad(BloomEffect, BloomH_A, "BloomH")
	
	EntityTexture(PostEffectQuad, BloomH_A, 0, 1)
	RenderEffectQuad(BloomEffect, BloomV_A, "BloomV")
	
	EffectVector(BloomEffect, "BlurInvSize", 1.0 / TextureWidth(BloomV_A), 1.0 / TextureHeight(BloomV_A))
	EffectFloat(BloomEffect, "BlurSize", 8.0)
	EntityTexture(PostEffectQuad, BloomV_A, 0, 1)
	RenderEffectQuad(BloomEffect, BloomH_B, "BloomH")
	
	EntityTexture(PostEffectQuad, BloomH_B, 0, 1)
	RenderEffectQuad(BloomEffect, BloomV_B, "BloomV")
	
	EffectFloat(BloomEffect, "BlurSize", 16.0)
	EffectVector(BloomEffect, "BlurInvSize", 1.0 / TextureWidth(BloomV_B), 1.0 / TextureHeight(BloomV_B))
	
	EntityTexture(PostEffectQuad, BloomV_B, 0, 1)
	RenderEffectQuad(BloomEffect, BloomH_C, "BloomH")
	
	EntityTexture(PostEffectQuad, BloomH_C, 0, 1)
	RenderEffectQuad(BloomEffect, BloomV_C, "BloomV")
	
	EntityTexture(PostEffectQuad, BloomH_A, 0, 1)
	EntityTexture(PostEffectQuad, BloomV_A, 0, 2)
	EntityTexture(PostEffectQuad, BloomH_B, 0, 3)
	EntityTexture(PostEffectQuad, BloomV_B, 0, 4)
	EntityTexture(PostEffectQuad, BloomH_C, 0, 5)
	EntityTexture(PostEffectQuad, BloomV_C, 0, 6)
	
	EffectVector(BloomEffect, "BlurInvSize", 0.0, 0.0)
	RenderEffectQuad(BloomEffect, BloomTex, "Blur")
	
	ProcessSSAO(Cam, Strength, Radius)
	
	EffectVector(BloomEffect, "BlurInvSize", 0.5 / BloomTexWidth, 0.5 / BloomTexHeight)
	EntityTexture(PostEffectQuad, BloomTex, 0, 1)
	RenderEffectQuad(BloomEffect, MRTColor, "Final", 3)
End Function

Function ProcessColorCorrection%()
	If ColorCorrectionEffect = 0 Then Return
	
	RenderEffectQuad(ColorCorrectionEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function ProcessFog%(R%, G%, B%)
	If FogEffect = 0 Then Return
	
	CameraFogColor(QuadCamera, R, G, B)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 1)
	RenderEffectQuad(FogEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function ProcessSSAO%(Cam%, Strength#, Radius#)
	If SSAOEffect = 0 Lor (Not opt\AmbientOcclusion) Lor IsInsideForest Then Return
	
	EffectFloat(SSAOEffect, "SSAOStrength", Strength)
	EffectFloat(SSAOEffect, "SSAORadius", Radius)
	EffectMatrix(SSAOEffect, "InvViewProj", CameraMatrix(Cam, 3, CurrentTween))
	EffectMatrix(SSAOEffect, "InvProj", CameraMatrix(Cam, 5, CurrentTween))
	EffectMatrix(SSAOEffect, "ViewMat", CameraMatrix(Cam, 0, CurrentTween))
	EffectVector(SSAOEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(SSAOEffect, "FarClip", GetCameraRangeFar(Cam))
	
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	RenderEffectQuad(SSAOEffect, SSAOBlurH, "SSAO", 0)
	
	ProcessBilateralBlur(Cam, SSAOBlurH, SSAOBlurV, SSAODepthLow, SSAONormalLow, MRTColor, 2)
End Function

Function ProcessLinearDepth%(Cam%)
	If (SSAOEffect = 0 Lor (Not opt\AmbientOcclusion)) And (Not opt\VolumetricLights) Then Return
	
	EffectVector(BilateralBlurEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(BilateralBlurEffect, "FarClip", GetCameraRangeFar(Cam))
	EffectMatrix(BilateralBlurEffect, "InvViewProj", CameraMatrix(Cam, 3, CurrentTween))
	
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	RenderEffectQuad(BilateralBlurEffect, LinearDepth, "Bilateral", 0)
End Function

Function ProcessBilateralBlur%(Cam%, BlurH%, BlurV%, LowDepth%, NormalLow%, Output%, OutputBlend%)
	If TextureWidth(BlurH) <> TextureWidth(BlurV) Lor TextureHeight(BlurH) <> TextureHeight(BlurV) Then Return
	
	EffectVector(BilateralBlurEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(BilateralBlurEffect, "FarClip", GetCameraRangeFar(Cam))
	EffectVector(BilateralBlurEffect, "LowResTexelSize", 1.0 / TextureWidth(BlurH), 1.0 / TextureHeight(BlurH))
	EffectMatrix(BilateralBlurEffect, "InvViewProj", CameraMatrix(Cam, 3, CurrentTween))
	
	Local FinalTechnique$ = "Final"
	
	If TextureWidth(LowDepth) <> TextureWidth(LinearDepth)
		RenderEffectQuad(BilateralBlurEffect, LowDepth, "Bilateral", 0)
		RenderEffectQuad(BilateralBlurEffect, NormalLow, "Normal", 0)
	Else
		FinalTechnique = "FinalSimple"
	EndIf
	
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, LinearDepth, 0, 2)
	EntityTexture(PostEffectQuad, LowDepth, 0, 3)
	EntityTexture(PostEffectQuad, NormalLow, 0, 4)
	
	EntityTexture(PostEffectQuad, BlurH, 0, 0)
	EffectVector(BilateralBlurEffect, "BlurInvSize", 1.0 / TextureWidth(BlurH), 0) ; ~ Horizontal
	RenderEffectQuad(BilateralBlurEffect, BlurV, "Blur")
	
	EntityTexture(PostEffectQuad, BlurV, 0, 0)
	EffectVector(BilateralBlurEffect, "BlurInvSize", 0.0, 1.0 / TextureHeight(BlurV)) ; ~ Vertical
	RenderEffectQuad(BilateralBlurEffect, BlurH, "Blur")
	
	EntityTexture(PostEffectQuad, BlurH, 0, 0)
	RenderEffectQuad(BilateralBlurEffect, Output, FinalTechnique, OutputBlend)
	
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
End Function

Function ProcessFXAA%(Inpu%, Output%)
	If FXAAEffect = 0 Lor (Not opt\AntiAliasing) Then Return(False)
	
	EntityTexture(PostEffectQuad, Inpu, 0, 0)
	RenderEffectQuad(FXAAEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, Output)
	Return(True)
End Function

Function ProcessMotionBlur%(Cam%, Strength#)
	If MotionBlurEffect = 0 Lor (Not opt\MotionBlur) Then Return
	
	EffectFloat(MotionBlurEffect, "Strength", Strength)
	EffectMatrix(MotionBlurEffect, "InvViewProj", CameraMatrix(Cam, 3, CurrentTween))
	EffectFloat(MotionBlurEffect, "Timestep", Min(Float(fps\ElapsedMilliSecs) / 1000.0, 1.0))
	
	EntityTexture(PostEffectQuad, MRTDepth, 0, 1)
	RenderEffectQuad(MotionBlurEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
	
	EffectMatrix(MotionBlurEffect, "PrevViewProj", CameraMatrix(Cam, 2, CurrentTween))
End Function

Function ProcessGamma%(Src%, Dest%, Gamma#)
	If GammaEffect = 0 Then Return
	
	EffectFloat(GammaEffect, "Gamma", Gamma)
	RenderEffectQuad(GammaEffect, Src, "Main")
	PresentGBuffer(Src, TextureBuffer(Dest))
End Function

Function PresentGBuffer%(Texture%, Dest% = 0, Depth% = 0, Pow% = 0, Blend% = 0)
	Local OldBuffer% = GraphicsBuffer()
	
	EntityBlend(PostEffectQuad, Blend)
	SetQuadEffect(PresentEffect)
	EntityTexture(PostEffectQuad, Texture, 0, 0)
	If Pow = 1
		EffectTechnique(PresentEffect, "ACES")
	ElseIf Pow = 2
		EffectTechnique(PresentEffect, "Pow")
	Else
		EffectTechnique(PresentEffect, "Main")
	EndIf
	ShowEntity(PostEffectQuad)
	SetBuffer(Dest, Depth)
	CameraViewport(QuadCamera, 0, 0, BufferWidth(Dest), BufferHeight(Dest))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	SetBuffer(OldBuffer, GetResolutionDepth())
End Function

Function ClearBuffer%(Buffer%, R%, G%, B%, Alpha%)
	Local PrevBuffer% = GraphicsBuffer()
	
	SetBuffer(Buffer)
	ClsColor(R, G, B, Alpha)
	Cls()
	SetBuffer(PrevBuffer)
End Function

Function RenderEffectQuad%(Effect%, Texture%, Technique$, Blend% = 0)
	SetQuadEffect(Effect)
	ShowEntity(PostEffectQuad)
	EntityBlend(PostEffectQuad, Blend)
	SetBuffer(TextureBuffer(Texture), GetResolutionDepth())
	EffectTechnique(Effect, Technique)
	CameraViewport(QuadCamera, 0, 0, TextureWidth(Texture), TextureHeight(Texture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function SetQuadEffect%(Effect%)
	If PostEffect = Effect Then Return
	
	EntityEffect(PostEffectQuad, Effect)
	PostEffect = Effect
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS