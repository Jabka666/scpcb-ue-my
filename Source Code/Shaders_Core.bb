Include "Source Code\Effects_Core.bb"

Const DEFERRED_PATH$ = "GFX\Shaders\Deferred\"
Const POSTEFFECTS_PATH$ = "GFX\Shaders\PostEffects\"

Global PostEffectQuad%

Global BloomEffect%
Global BloomTex%, BloomBlur%

Global ColorCorrectionEffect%
Global PresentEffect%

Global SSAOEffect%, SSAOBlur%
Global NoiseTexture%

Global FXAAEffect%

Global EyeAdaptationEffect%
Global Luma%
Global Luma64%
Global Luma16%
Global Luma4%
Global Luma1%
Global AdaptedLum%
Global PrevAdaptedLum%

Global MotionBlurEffect%
Global ClearEffect%

Global BlurEffect%
Global GammaEffect%

Global EffectsBits% = -1

Function InitShaders%()
	Local Width% = opt\GraphicWidth / 4
	Local Height% = opt\GraphicHeight / 4
	
	PostEffectQuad = CreateFullscreenQuad(QuadCamera)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	EntityOrder(PostEffectQuad, 10000000)
	EntityFX(PostEffectQuad, 8)
	
	ClearEffect = LoadEffectEx(POSTEFFECTS_PATH + "Clear.fx")
	BloomEffect = LoadEffectEx(POSTEFFECTS_PATH + "Bloom.fx")
	ColorCorrectionEffect = LoadEffectEx(POSTEFFECTS_PATH + "ColorCorrection.fx")
	PresentEffect = LoadEffectEx(POSTEFFECTS_PATH + "Present.fx")
	SSAOEffect = LoadEffectEx(POSTEFFECTS_PATH + "SSAO.fx")
	FXAAEffect = LoadEffectEx(POSTEFFECTS_PATH + "FXAA.fx")
	MotionBlurEffect = LoadEffectEx(POSTEFFECTS_PATH + "MotionBlur.fx")
	GammaEffect = LoadEffectEx(POSTEFFECTS_PATH + "Gamma.fx")
	
	DebugLog(GetEffectError())
	
	BloomTex = CreateTexture(Width, Height, 1 + 256 + 1024)
	BloomBlur = CreateTexture(Width, Height, 1 + 256 + 1024)
	
	SSAOBlur = CreateTexture(Width, Height, 1 + 256 + 1024)
	
	NoiseTexture = LoadTexture("GFX\Other\ssao.png", 1 + 32768)
	
	Luma = CreateTexture(128, 128, 1 + 1024)
	Luma64 = CreateTexture(64, 64, 2048)
	Luma16 = CreateTexture(16, 16, 2048)
	Luma4 = CreateTexture(4, 4, 2048)
	Luma1 = CreateTexture(1, 1, 2048)
	AdaptedLum = CreateTexture(1, 1, 2048)
	PrevAdaptedLum = CreateTexture(1, 1, 2048)
End Function

Function GetPostEffectQuad%()
	Return(PostEffectQuad)
End Function

; ==================================== POST EFFECTS

Function ProcessBloom%(Threshold# = 0.4)
	If BloomEffect = 0 Lor (Not opt\Bloom) Then Return
	
	EffectFloat(BloomEffect, "BloomThreshold", Threshold)
	EntityEffect(PostEffectQuad, BloomEffect)
	EntityTexture(PostEffectQuad, BloomTex, 0, 1)
	EntityTexture(PostEffectQuad, BloomBlur, 0, 2)
	
	EntityBlend(PostEffectQuad, 0)
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
	
	EntityBlend(PostEffectQuad, 3)
	EffectTechnique(BloomEffect, "Combine")
	SetBuffer(TextureBuffer(MRTColor))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function ProcessColorCorrection%()
	If ColorCorrectionEffect = 0 Lor (Not opt\ColorCorrection) Then Return
	
	EntityEffect(PostEffectQuad, ColorCorrectionEffect)
	EntityBlend(PostEffectQuad, 0)
	ShowEntity(PostEffectQuad)
	EffectTechnique(ColorCorrectionEffect, "Main")
	SetBuffer(TextureBuffer(TempColorTexture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function ProcessSSAO%(Cam%, Strength#, Radius#, BloomThreshold#, Tween# = 1.0)
	If SSAOEffect = 0 Lor (Not opt\AmbientOcclusion) Lor IsInsideForest Then Return
	
	EffectFloat(SSAOEffect, "SSAOStrength", Strength)
	EffectFloat(SSAOEffect, "SSAORadius", Radius)
	EffectFloat(SSAOEffect, "BloomThreshold", BloomThreshold)
	EffectMatrix(SSAOEffect, "InvViewProj", CameraMatrix(Cam, 3, Tween))
	EffectVector(SSAOEffect, "CameraPosition", EntityX(Cam, True), EntityY(Cam, True), EntityZ(Cam, True))
	EffectFloat(SSAOEffect, "FarClip", GetCameraRangeFar(Cam) / 1.25)
	EffectTechnique(SSAOEffect, "SSAO")
	
	EntityBlend(PostEffectQuad, 0)
	EntityEffect(PostEffectQuad, SSAOEffect)
	EntityTexture(PostEffectQuad, MRTNormal, 0, 1)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 2)
	EntityTexture(PostEffectQuad, MRTAlbedo, 0, 3)
	EntityTexture(PostEffectQuad, NoiseTexture, 0, 4)
	EntityTexture(PostEffectQuad, TempColorTexture, 0, 5)
	ShowEntity(PostEffectQuad)
	
	If opt\AmbientOcclusion = 2
		SetBuffer(TextureBuffer(TempColorTexture))
		RenderEntity(QuadCamera, PostEffectQuad)
		
		EffectTechnique(SSAOEffect, "Blur")
		EffectVector(SSAOEffect, "BlurInvSize", 1.0 / TextureWidth(TempColorTexture), 0) ; ~ Horizontal
		SetBuffer(TextureBuffer(SSAOBlur))
		RenderEntity(QuadCamera, PostEffectQuad)
		
		EntityTexture(PostEffectQuad, SSAOBlur, 0, 5)
		EffectVector(SSAOEffect, "BlurInvSize", 0, 1.0 / TextureHeight(SSAOBlur)) ; ~ Vertical
		
		EntityBlend(PostEffectQuad, 2)
		SetBuffer(TextureBuffer(MRTColor))
		RenderEntity(QuadCamera, PostEffectQuad)
	Else
		EntityBlend(PostEffectQuad, 2)
		SetBuffer(TextureBuffer(MRTColor))
		RenderEntity(QuadCamera, PostEffectQuad)
	EndIf
	HideEntity(PostEffectQuad)
End Function

Function ProcessFXAA%()
	If FXAAEffect = 0 Lor (Not opt\AntiAliasing) Then Return
	
	EntityEffect(PostEffectQuad, FXAAEffect)
	EntityBlend(PostEffectQuad, 0)
	ShowEntity(PostEffectQuad)
	EffectTechnique(FXAAEffect, "Main")
	SetBuffer(TextureBuffer(TempColorTexture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function ProcessMotionBlur%(Cam%, Strength#, Tween#)
	If MotionBlurEffect = 0 Lor (Not opt\MotionBlur) Then Return
	
	EffectFloat(MotionBlurEffect, "Strength", Strength)
	EffectMatrix(MotionBlurEffect, "InvViewProj", CameraMatrix(Cam, 3, Tween))
	EffectFloat(MotionBlurEffect, "Timestep", Min(Float(fps\ElapsedMilliSecs) / 1000.0, 1.0))
	
	EntityEffect(PostEffectQuad, MotionBlurEffect)
	EntityTexture(PostEffectQuad, MRTDepth, 0, 1)
	
	EntityBlend(PostEffectQuad, 0)
	ShowEntity(PostEffectQuad)
	EffectTechnique(MotionBlurEffect, "Main")
	SetBuffer(TextureBuffer(TempColorTexture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
	
	EffectMatrix(MotionBlurEffect, "PrevViewProj", CameraMatrix(Cam, 2, Tween))
End Function

Function ProcessEyeAdaptation%()
	If EyeAdaptationEffect = 0 Then Return
	
	Local Width# = 0.5 / GraphicsWidth()
	Local Height# = 0.5 / GraphicsHeight()
	
	EntityEffect(PostEffectQuad, EyeAdaptationEffect)
	
	EntityBlend(PostEffectQuad, 0)
	ShowEntity(PostEffectQuad)
	EffectVector(EyeAdaptationEffect, "LumaOffset", Width, Height)
	SetBuffer(TextureBuffer(Luma))
	EffectTechnique(EyeAdaptationEffect, "Present")
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0.5 / TextureWidth(Luma), 0.5 / TextureHeight(Luma))
	SetBuffer(TextureBuffer(Luma64))
	EffectTechnique(EyeAdaptationEffect, "LUM64")
	EntityTexture(PostEffectQuad, Luma, 0, 1)
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0.5 / TextureWidth(Luma64), 0.5 / TextureHeight(Luma64))
	SetBuffer(TextureBuffer(Luma16))
	EffectTechnique(EyeAdaptationEffect, "LUM16")
	EntityTexture(PostEffectQuad, Luma64, 0, 1)
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0.5 / TextureWidth(Luma16), 0.5 / TextureHeight(Luma16))
	SetBuffer(TextureBuffer(Luma4))
	EffectTechnique(EyeAdaptationEffect, "LUM4")
	EntityTexture(PostEffectQuad, Luma16, 0, 1)
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0.5 / TextureWidth(Luma4), 0.5 / TextureHeight(Luma4))
	SetBuffer(TextureBuffer(Luma1))
	EffectTechnique(EyeAdaptationEffect, "LUM1")
	EntityTexture(PostEffectQuad, Luma4, 0, 1)
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EntityTexture(PostEffectQuad, AdaptedLum, 0, 0)
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0, 0)
	SetBuffer(TextureBuffer(PrevAdaptedLum))
	EffectTechnique(EyeAdaptationEffect, "Present")
	RenderEntity(QuadCamera, PostEffectQuad)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", 0, 0)
	SetBuffer(TextureBuffer(AdaptedLum))
	EffectTechnique(EyeAdaptationEffect, "Adaptation")
	EntityTexture(PostEffectQuad, PrevAdaptedLum, 0, 1)
	EntityTexture(PostEffectQuad, Luma1, 0, 2)
	RenderEntity(QuadCamera, PostEffectQuad)
	
	EffectVector(EyeAdaptationEffect, "LumaOffset", Width, Height)
	SetBuffer(TextureBuffer(MRTColor))
	EffectTechnique(EyeAdaptationEffect, "Exposure")
	EntityTexture(PostEffectQuad, AdaptedLum, 0, 1)
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
End Function

Function ProcessGamma%(Gamma#)
	If GammaEffect = 0 Then Return
	
	EntityEffect(PostEffectQuad, GammaEffect)
	EntityBlend(PostEffectQuad, 0)
	ShowEntity(PostEffectQuad)
	EffectFloat(GammaEffect, "Gamma", Gamma)
	EffectTechnique(GammaEffect, "Main")
	SetBuffer(TextureBuffer(TempColorTexture))
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	PresentGBuffer(TempColorTexture, TextureBuffer(MRTColor))
End Function

Function BlurGBuffer%(Texture%, Force# = 1.0)
	If Force <= 0.0 Lor BlurEffect = 0 Then Return
	
	Local OldBuffer% = GraphicsBuffer()
	
	SetBuffer(TextureBuffer(TempColorTexture))
	EffectFloat(BlurEffect, "BlurStrength", Force)
	EntityEffect(PostEffectQuad, BlurEffect)
	EntityTexture(PostEffectQuad, Texture, 0, 0)
	ShowEntity(PostEffectQuad)

	EntityBlend(PostEffectQuad, 0)
	EffectTechnique(BlurEffect, "Blur")
	RenderEntity(QuadCamera, PostEffectQuad)
	PresentGBuffer(TempColorTexture, TextureBuffer(Texture))
	
	HideEntity(PostEffectQuad)
	SetBuffer(OldBuffer)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
End Function

Function PresentGBuffer%(Texture%, Dest% = 0)
	Local OldBuffer% = GraphicsBuffer()
	
	EntityBlend(PostEffectQuad, 0)
	EntityEffect(PostEffectQuad, PresentEffect)
	EntityTexture(PostEffectQuad, Texture, 0, 0)
	ShowEntity(PostEffectQuad)
	SetBuffer(Dest)
	RenderEntity(QuadCamera, PostEffectQuad)
	HideEntity(PostEffectQuad)
	EntityTexture(PostEffectQuad, MRTColor, 0, 0)
	SetBuffer(OldBuffer)
End Function

Function ClearBuffer%(Buffer%, R%, G%, B%, Alpha%)
	Local PrevBuffer% = GraphicsBuffer()
	
	SetBuffer(Buffer)
	ClsColor(R, G, B, Alpha)
	Cls
	SetBuffer(PrevBuffer)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS