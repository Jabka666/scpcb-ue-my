Include "Source Code\Effects_Core.bb"

Const DEFERRED_PATH$ = "GFX\Shaders\Deferred\"
Const POSTEFFECTS_PATH$ = "GFX\Shaders\PostEffects\"

Global PostEffectQuad%

Global BloomEffect%
Global BloomTex%, BloomH_A%, BloomV_A%, BloomH_B%, BloomV_B%, BloomH_C%, BloomV_C%

Global ColorCorrectionEffect%
Global PresentEffect%

Global SSAOEffect%, SSAOBlurH%, SSAOBlurV%, SSAODepth%, SSAODepthLow%, SSAONormalLow%
Global SSGIEffect%, SSGIBlurH%, SSGIBlurV%, SSGIDepth%, SSGIDepthLow%, SSGIColor%

Global NoiseTexture%

Global FXAAEffect%

Global MotionBlurEffect%

Global GammaEffect%

Global FogEffect%

Global EffectsBits% = -1

Global PostEffect%

Function InitShaders%()
	Local Width% = opt\GraphicWidth
	Local Height% = opt\GraphicHeight
	
	PostEffectQuad = CreateFullscreenQuad(QuadCamera)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	EntityOrder(PostEffectQuad, 10000000)
	EntityFX(PostEffectQuad, 8)
	HideEntity(PostEffectQuad)
	
	BloomEffect = LoadEffectEx(POSTEFFECTS_PATH + "Bloom.fx")
	ColorCorrectionEffect = LoadEffectEx(POSTEFFECTS_PATH + "ColorCorrection.fx")
	PresentEffect = LoadEffectEx(POSTEFFECTS_PATH + "Present.fx")
	SSAOEffect = LoadEffectEx(POSTEFFECTS_PATH + "SSAO.fx")
	FXAAEffect = LoadEffectEx(POSTEFFECTS_PATH + "FXAA.fx")
	MotionBlurEffect = LoadEffectEx(POSTEFFECTS_PATH + "MotionBlur.fx")
	GammaEffect = LoadEffectEx(POSTEFFECTS_PATH + "Gamma.fx", "", False)
	SSGIEffect = LoadEffectEx(POSTEFFECTS_PATH + "SSGI.fx", "", False)
	FogEffect = LoadEffectEx(POSTEFFECTS_PATH + "Fog.fx", "", True)
	
	BloomTex = CreateTexture(Width / 2, Height / 2, 4096)
	BloomH_A = CreateTexture(Width / 2, Height / 2, 4096)
	BloomV_A = CreateTexture(Width / 2, Height / 2, 4096)
	
	BloomH_B = CreateTexture(Width / 4, Height / 4, 4096)
	BloomV_B = CreateTexture(Width / 4, Height / 4, 4096)
	
	BloomH_C = CreateTexture(Width / 8, Height / 8, 4096)
	BloomV_C = CreateTexture(Width / 8, Height / 8, 4096)
	
	SSAOBlurV = CreateTexture(Width / 2, Height / 2, 131072)
	SSAOBlurH = CreateTexture(Width / 2, Height / 2, 131072)
	SSAODepth = CreateTexture(Width, Height, 2048)
	SSAODepthLow = CreateTexture(Width / 2, Height / 2, 2048)
	SSAONormalLow = CreateTexture(Width / 2, Height / 2, 4096)
	
	SSGIColor = CreateTexture(Width / 2, Height / 2, 131072)
	SSGIBlurV = CreateTexture(Width / 2, Height / 2, 131072)
	SSGIBlurH = CreateTexture(Width / 2, Height / 2, 131072)
	SSGIDepth = CreateTexture(Width, Height, 2048)
	SSGIDepthLow = CreateTexture(Width / 2, Height / 2, 2048)
	
	NoiseTexture = LoadTexture("GFX\Other\ssgi.png", 1 + 32768)
End Function

Function GetPostEffectQuad%()
	Return(PostEffectQuad)
End Function

; ==================================== POST EFFECTS

Function ProcessBloom%(Threshold# = 1.0)
	If BloomEffect = 0 Lor (Not opt\Bloom) Then Return
	
	Local BloomTexWidth% = TextureWidth(BloomTex)
	Local BloomTexHeight% = TextureHeight(BloomTex)
	
	EffectFloat(BloomEffect, "BloomSensitivity", Threshold)
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
	
	EntityBlend(PostEffectQuad, 3)
	EffectTechnique(BloomEffect, "Combine")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	
	EffectVector(BloomEffect, "BlurInvSize", 0.0, 0.0)
	RenderEffectQuad(BloomEffect, BloomTex, "Blur")
	
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
	RenderEffectQuad(FogEffect, MRTColor, "Main")
End Function

Function ProcessSSAO%(Cam%, Strength#, Radius#, BloomThreshold#, Tween# = 1.0)
	If SSAOEffect = 0 Lor (Not opt\AmbientOcclusion) Lor IsInsideForest Then Return
	
	Local i%
	
	EffectFloat(SSAOEffect, "SSAOStrength", Strength)
	EffectFloat(SSAOEffect, "SSAORadius", Radius)
	EffectFloat(SSAOEffect, "BloomThreshold", BloomThreshold)
	EffectMatrix(SSAOEffect, "InvViewProj", CameraMatrix(Cam, 3, Tween))
	EffectVector(SSAOEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(SSAOEffect, "FarClip", GetCameraRangeFar(Cam) / 1.25)
	
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	EntityTexture(PostEffectQuad, TempColorTexture, 0, 4)
	EntityTexture(PostEffectQuad, TempColorTexture, 0, 5)
	
	RenderEffectQuad(SSAOEffect, SSAOBlurH, "SSAO", 0)
	RenderEffectQuad(SSAOEffect, SSAODepth, "Bilateral", 0)
	RenderEffectQuad(SSAOEffect, SSAODepthLow, "Bilateral", 0)
	RenderEffectQuad(SSAOEffect, SSAONormalLow, "Normal", 0)
	EntityTexture(PostEffectQuad, SSAODepth, 0, 2)
	EntityTexture(PostEffectQuad, SSAODepthLow, 0, 6)
	EntityTexture(PostEffectQuad, SSAONormalLow, 0, 7)
	
	EntityTexture(PostEffectQuad, SSAOBlurH, 0, 5)
	EffectVector(SSAOEffect, "BlurInvSize", 1.0 / TextureWidth(SSAOBlurH), 0) ; ~ Horizontal
	RenderEffectQuad(SSAOEffect, SSAOBlurV, "Blur")
	
	EntityTexture(PostEffectQuad, SSAOBlurV, 0, 5)
	EffectVector(SSAOEffect, "BlurInvSize", 0.0, 1.0 / TextureHeight(SSAOBlurV)) ; ~ Vertical
	RenderEffectQuad(SSAOEffect, SSAOBlurH, "Blur")
	
	EntityTexture(PostEffectQuad, SSAOBlurH, 0, 5)
	RenderEffectQuad(SSAOEffect, MRTColor, "Final", 2)
End Function

Function ProcessSSGI%(Cam%, Strength#, Radius#, Tween# = 1.0)
	If SSGIEffect = 0 Then Return
	
	Local i%
	
	EffectFloat(SSGIEffect, "SSGIIntensity", Strength)
	EffectFloat(SSGIEffect, "SSGIRadius", Radius)
	EffectMatrix(SSGIEffect, "ViewProjc", CameraMatrix(Cam, 2, Tween))
	EffectMatrix(SSGIEffect, "InvViewProj", CameraMatrix(Cam, 3, Tween))
	EffectVector(SSGIEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(SSGIEffect, "FarClip", GetCameraRangeFar(Cam))
	
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	EntityTexture(PostEffectQuad, MRTColor, 0, 4)
	
	EntityTexture(PostEffectQuad, NoiseTexture, 0, 5)
	
	RenderEffectQuad(SSGIEffect, SSGIColor, "Downsample", 0)
	EntityTexture(PostEffectQuad, SSGIColor, 0, 0)
	RenderEffectQuad(SSGIEffect, SSGIBlurH, "SSGI", 0)
	
	RenderEffectQuad(SSGIEffect, SSGIDepth, "Bilateral", 0)
	RenderEffectQuad(SSGIEffect, SSGIDepthLow, "Bilateral", 0)
	EntityTexture(PostEffectQuad, SSGIDepth, 0, 2)
	EntityTexture(PostEffectQuad, SSGIDepthLow, 0, 6)
	
	For i = 0 To 6
		EffectVector(SSGIEffect, "BlurInvSize", 1.0 / TextureWidth(SSGIBlurH), 0) ; ~ Horizontal
		EntityTexture(PostEffectQuad, SSGIBlurH, 0, 4)
		RenderEffectQuad(SSGIEffect, SSGIBlurV, "Blur")
		
		EffectVector(SSGIEffect, "BlurInvSize", 0.0, 1.0 / TextureHeight(SSGIBlurV)) ; ~ Vertical
		EntityTexture(PostEffectQuad, SSGIBlurV, 0, 4)
		RenderEffectQuad(SSGIEffect, SSGIBlurH, "Blur")
	Next
	
	EntityTexture(PostEffectQuad, SSGIBlurH, 0, 4)
	RenderEffectQuad(SSGIEffect, MRTColor, "Final", 3)
	
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
End Function

Function ProcessFXAA%()
	If FXAAEffect = 0 Lor (Not opt\AntiAliasing) Then Return
	
	RenderEffectQuad(FXAAEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function ProcessMotionBlur%(Cam%, Strength#, Tween#)
	If MotionBlurEffect = 0 Lor (Not opt\MotionBlur) Then Return
	
	EffectFloat(MotionBlurEffect, "Strength", Strength)
	EffectMatrix(MotionBlurEffect, "InvViewProj", CameraMatrix(Cam, 3, Tween))
	EffectFloat(MotionBlurEffect, "Timestep", Min(Float(fps\ElapsedMilliSecs) / 1000.0, 1.0))
	
	EntityTexture(PostEffectQuad, MRTDepth, 0, 1)
	
	RenderEffectQuad(MotionBlurEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
	
	EffectMatrix(MotionBlurEffect, "PrevViewProj", CameraMatrix(Cam, 2, Tween))
End Function

Function ProcessGamma%(Gamma#)
	If GammaEffect = 0 Then Return
	
	EffectFloat(GammaEffect, "Gamma", Gamma)
	RenderEffectQuad(GammaEffect, TempColorTexture, "Main")
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function PresentGBuffer%(Texture%, Dest% = 0, Multiply# = 1.0)
	Local OldBuffer% = GraphicsBuffer()
	
	EntityBlend(PostEffectQuad, 0)
	SetQuadEffect(PresentEffect)
	EntityTexture(PostEffectQuad, Texture, 0, 0)
	If Multiply <> 1.0
		EffectTechnique(PresentEffect, "Mul")
		EffectFloat(PresentEffect, "PresentMultiply", Multiply)
	Else
		EffectTechnique(PresentEffect, "Main")
	EndIf
	ShowEntity(PostEffectQuad)
	SetBuffer(Dest)
	CameraViewport(QuadCamera, 0, 0, BufferWidth(Dest), BufferHeight(Dest))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	SetBuffer(OldBuffer)
End Function

Function ClearBuffer%(Buffer%, R%, G%, B%, Alpha%)
	Local PrevBuffer% = GraphicsBuffer()
	
	SetBuffer(Buffer)
	ClsColor(R, G, B, Alpha)
	Cls()
	SetBuffer(PrevBuffer)
End Function

Function RenderEffectQuad(Effect%, Texture%, Technique$, Blend% = 0)
	SetQuadEffect(Effect)
	ShowEntity(PostEffectQuad)
	EntityBlend(PostEffectQuad, Blend)
	SetBuffer(TextureBuffer(Texture))
	EffectTechnique(Effect, Technique)
	CameraViewport(QuadCamera, 0, 0, TextureWidth(Texture), TextureHeight(Texture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function SetQuadEffect%(Effect%)
	If PostEffect = Effect Then Return
	
	EntityEffect PostEffectQuad, Effect
	PostEffect = Effect
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS