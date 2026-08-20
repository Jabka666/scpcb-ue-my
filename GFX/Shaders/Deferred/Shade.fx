//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"

#ifdef VOLUMETRIC_HQ
	#define RAYMARCH_STEPS 4
#else
	#define RAYMARCH_STEPS 2
#endif

uniform int Time;
uniform float4 LightPos;
uniform float3 LightColor;
uniform float3 LightDirection;
uniform float LightScattering;
uniform float ShadowIntensity;
uniform float NormalOffset = 0.05;
uniform float2 ShadowMapSize;
uniform float ShadowMapRefSize = 2048.0;
uniform int ShadowMapAddress = 3;
uniform float LightLength;
uniform float3 EyePos;
static const float2 InvShadowMapSize = 1.0 / ShadowMapSize;
static const float3 pLightColor = SRGBToLinear(LightColor);

uniform float4x4 LightViewProj;
uniform float4x4 LightViewProj0;
uniform float4x4 LightViewProj1;
uniform float4x4 LightViewProj2;
uniform float4x4 LightViewProj3;
uniform float4x4 LightViewProj4;
uniform float4x4 LightViewProj5;
uniform float4x4 InvViewProj;
uniform float4x4 ShadowsAdjust;
uniform int CascadeCount = 4;
uniform float4 CascadeSplits;
uniform float4x4 CascadeView;
static const float4x4 SpotMatrix = mul(LightViewProj, ShadowsAdjust);
static const float4x4 LightMatrix[6] =
{
	mul(LightViewProj0, ShadowsAdjust),
	mul(LightViewProj1, ShadowsAdjust),
	mul(LightViewProj2, ShadowsAdjust),
	mul(LightViewProj3, ShadowsAdjust),
	mul(LightViewProj4, ShadowsAdjust),
	mul(LightViewProj5, ShadowsAdjust)
};

#include "PBR.fx"

#ifdef D3D11

	texture2D tAlbedoMap : register(t0);
	sampler AlbedoMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT;  };

	texture2D tNormalMap : register(t1);
	sampler NormalMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT; };

	texture2D tDepthMap : register(t2);
	sampler DepthMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT; };

	textureCUBE tFaceSelectCubeMap : register(t3);
	sampler FaceSelectCubeMap = sampler_state { AddressU = Clamp; AddressV = Clamp; AddressW = Clamp; Filter = MIN_MAG_MIP_POINT;  };

	texture2D tSpotMap : register(t4);
	sampler SpotMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Border; AddressV = Border; BorderColor = float4(0.0, 0.0, 0.0, 0.0); };

	Texture2D tShadowMap;
	SamplerComparisonState ShadowMap = sampler_state { Filter = COMPARISON_MIN_MAG_LINEAR_MIP_POINT; AddressU = ShadowMapAddress; AddressV = ShadowMapAddress; BorderColor = float4(1.0, 1.0, 1.0, 1.0); ComparisonFunc = LESS_EQUAL; };

	TextureCube tShadowCubeMap;
	SamplerComparisonState ShadowCubeMap = sampler_state { Filter = COMPARISON_MIN_MAG_LINEAR_MIP_POINT; AddressU = Clamp; AddressV = Clamp; AddressW = Clamp; BorderColor = float4(1.0, 1.0, 1.0, 1.0); ComparisonFunc = LESS_EQUAL; };
#else
	sampler AlbedoMap : register(s0) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler NormalMap : register(s1) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler DepthMap : register(s2) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	samplerCUBE FaceSelectCubeMap : register(s3) = sampler_state { AddressU = Clamp; AddressV = Clamp; AddressW = Clamp; MinFilter = Point; MagFilter = Point; MipFilter = Point;  };

	sampler SpotMap : register(s4) = sampler_state { MinFilter = Linear; MagFilter = Linear; MipFilter = None; AddressU = Border; AddressV = Border; BorderColor = 0; };

	texture tShadowMap;
	sampler ShadowMap = sampler_state { Texture = <tShadowMap>; 
	MinFilter = Point; MagFilter = Linear; 
	MipFilter = Point; AddressU = ShadowMapAddress; AddressV = ShadowMapAddress; AddressW = ShadowMapAddress; BorderColor = 0xFFFFFFFF; };
#endif


struct PS_INPUT
{ 
	float4 Pos 				: OUT_POSITION; 
	float4 ScreenPosition 	: TEXCOORD0;
	float3 WorldPos			: TEXCOORD1;
	float3 Normal			: TEXCOORD2;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, WorldViewProj); 
	output.ScreenPosition 	= output.Pos;
	
	output.WorldPos = mul(input.Pos, World).xyz;
	output.Normal = normalize(mul(input.Normal, World).xyz);
	return output;
}

inline float4 ClampShadows(float4 ProjCoord, int face, int divisions)
{
    float padding = InvShadowMapSize.x; 
    float minX = (float)face / divisions + padding;
    float maxX = ((float)face + 1.0) / divisions - padding;
	#ifdef D3D11
	ProjCoord.x = clamp(ProjCoord.x, minX, maxX);
	#else
    ProjCoord.x = clamp(ProjCoord.x, minX * ProjCoord.w, maxX * ProjCoord.w);
	#endif
    return ProjCoord;
}

inline float2 GetShadowFilterSize(float smoothCoeff)
{
	#ifdef DIRLIGHT
		float faces = (float)CascadeCount;
	#elif defined(POINTLIGHT)
		float faces = 6.0;
	#else
		float faces = 1.0;
	#endif
	return smoothCoeff / float2(ShadowMapRefSize * faces, ShadowMapRefSize);
}

inline float GetShadow(float4 ProjCoord, int face = 0, float smooth = 1.0, int divisions = 6)
{
	#ifdef D3D11
		ProjCoord.xyz /= ProjCoord.w;
		float smoothCoeff = clamp(ProjCoord.z * smooth, 1.0, 3.0);
		float2 offsets = GetShadowFilterSize(smoothCoeff);
	#else
		float smoothCoeff = clamp(ProjCoord.z / ProjCoord.w * smooth, 1.0, 3.0);
		float2 offsets = GetShadowFilterSize(smoothCoeff) * ProjCoord.w;
	#endif

    float sum = 0.0;

    for (int x = -1; x <= 1; ++x)
    {
        for (int y = -1; y <= 1; ++y)
        {
            float2 offset = float2(x, y) * offsets;
			float4 proj = float4(ProjCoord.xy + offset, ProjCoord.zw);
			#if !defined(SPOTLIGHT)
				proj = ClampShadows(proj, face, divisions);
			#endif
            sum += Sample2DShadow(ShadowMap, proj).r;
        }
    }

    return lerp(sum / 9.0, 1.0, ShadowIntensity);
}
inline float GetPointShadow(float3 worldPos)
{
	#ifdef SHADOWS
		#ifdef D3D11
			float3 d = worldPos - LightPos.xyz;
			float dist = length(d);
			float3 ndir = d / max(dist, 1e-6);
			float depth = dist * max(abs(ndir.x), max(abs(ndir.y), abs(ndir.z)));

			float range = 1.0 / max(LightPos.w, 1e-6);
			float nearZ = max(0.001, 0.005 * range);
			float farZ = range;
			float Qscale = farZ / max(farZ - nearZ, 1e-6);

			float3 helper = abs(ndir.y) < 0.99 ? float3(0, 1, 0) : float3(1, 0, 0);
			float3 t1 = normalize(cross(helper, ndir));
			float3 t2 = cross(ndir, t1);

			float texelAng = (3.14159265 * 0.5) / max(ShadowMapRefSize, 1.0);
			float sum = 0.0;
			for (int x = -1; x <= 1; ++x)
			{
				for (int y = -1; y <= 1; ++y)
				{
					float3 tap = normalize(ndir + (float(x) * t1 + float(y) * t2) * texelAng);
					float tapDepth = dist * max(abs(tap.x), max(abs(tap.y), abs(tap.z)));
					float ref = Qscale - Qscale * nearZ / max(tapDepth, nearZ);
					sum += SampleCubeShadow(ShadowCubeMap, tap, ref);
				}
			}
			return lerp(sum / 9.0, 1.0, ShadowIntensity);
		#else
			int face = clamp((int)(255.0f * SampleCubeLOD(FaceSelectCubeMap, float4(worldPos - LightPos.xyz, 0.0)).r), 0, 5);
			float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
			ProjCoord.x = ((ProjCoord.x / ProjCoord.w + face) / 6.0) * ProjCoord.w;
			return GetShadow(ProjCoord, face);
		#endif
	#else
		return 1.0;
	#endif
}

inline float GetDirShadow(float3 worldPos)
{
	#ifdef SHADOWS
		float4 v = mul(float4(worldPos, 1.0), CascadeView);
		float viewDepth = v.z / max(v.w, 1e-6);

		int ci = CascadeCount - 1;
		for (int k = 0; k < 3; ++k)
		{
			if (k < CascadeCount - 1 && viewDepth <= CascadeSplits[k]) { ci = k; break; }
		}

		float blend = 0.0;
		if (ci + 1 < CascadeCount)
		{
			float boundary = CascadeSplits[ci];
			float halfBand = max(CascadeSplits[ci + 1] - boundary, 0.001) * 0.25;
			blend = saturate(((viewDepth - boundary) + halfBand) / (2.0 * halfBand));
		}

		float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[ci]);
		ProjCoord.x = (ProjCoord.x / ProjCoord.w + ci) / CascadeCount * ProjCoord.w;
		float shadow = GetShadow(ProjCoord, ci, 0.0, CascadeCount);

		if (blend > 0.0 && ci + 1 < CascadeCount)
		{
			int ci2 = ci + 1;
			float4 ProjCoord2 = mul(float4(worldPos, 1.0), LightMatrix[ci2]);
			ProjCoord2.x = (ProjCoord2.x / ProjCoord2.w + ci2) / CascadeCount * ProjCoord2.w;
			float shadow2 = GetShadow(ProjCoord2, ci2, 0.0, CascadeCount);
			shadow = lerp(shadow, shadow2, blend);
		}

		return shadow;
	#else
		return 1.0;
	#endif
}

inline float GetSpotShadow(float3 worldPos)
{
	#ifdef SHADOWS
		return GetShadow(mul(float4(worldPos, 1.0), SpotMatrix));
	#else
		return 1.0;
	#endif
}

inline float GetShadowRay(float4 ProjCoord)
{
	#ifdef D3D11
		ProjCoord.xyz /= ProjCoord.w;
	#endif

    return Sample2DShadow(ShadowMap, ProjCoord).r;
}


inline float GetPointShadowRay(float3 worldPos)
{
	#ifdef SHADOWS
		#ifdef D3D11
			float3 d = worldPos - LightPos.xyz;
			float dist = length(d);
			float3 ndir = d / max(dist, 1e-6);

			float range = 1.0 / max(LightPos.w, 1e-6);
			float nearZ = max(0.001, 0.005 * range);
			float farZ = range;
			float Qscale = farZ / max(farZ - nearZ, 1e-6);

			float depth = dist * max(abs(ndir.x), max(abs(ndir.y), abs(ndir.z)));
			float ref = Qscale - Qscale * nearZ / max(depth, nearZ);
			return SampleCubeShadow(ShadowCubeMap, ndir, ref);
		#else
			int face = 255 * SampleCubeLOD(FaceSelectCubeMap, float4(worldPos - LightPos.xyz, 0.0)).r;
			float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
			ProjCoord.x = ((ProjCoord.x / ProjCoord.w + face) / 6.0) * ProjCoord.w;
			return GetShadowRay(ProjCoord);
		#endif
	#else
		return 1.0;
	#endif
}

inline void GetGBuffer(float4 ScreenPosition, out float2 TexCoords, out float3 worldPos, out float3 diffuse, out float3 normal, out float roughness, out float metallic)
{
	TexCoords = GetScreenTexCoords(ScreenPosition) + halfPixel;
	float4 Albedo = Sample2DLod0(AlbedoMap, TexCoords);
	float4 Normals = Sample2DLod0(NormalMap, TexCoords);
	worldPos = GetWorldPosition(TexCoords, Sample2DLod0(DepthMap, TexCoords).r, InvViewProj);
	
	metallic = Normals.a;
	roughness = length(Normals.xyz);
	normal = normalize(Normals.xyz);
	diffuse = Albedo.rgb;
}

inline float CalculateAttenuation(float3 lightVec, out float3 lightDir)
{
	float distSqr = dot(lightVec, lightVec);
	float len = sqrt(distSqr);
	lightDir = lightVec / len;

	float radiusSqr = 1.0 / max(LightPos.w * LightPos.w, 0.0001); 
	float attenuation = 1.0 / (distSqr + 1.0);
	float windowing = pow(saturate(1.0 - pow(distSqr / radiusSqr, 2)), 2);

	return attenuation * windowing;
}

inline void GetLighting(float3 worldPos, float3 normal, out float light, inout float3 lightVec, out float3 worldPosN)
{
	#ifndef DIRLIGHT
		float3 lightDir;
		lightVec = LightPos.xyz - worldPos;
        light = CalculateAttenuation(lightVec, lightDir);
	#else
		lightVec = -LightDirection;
		float3 lightDir = -LightDirection;
		light = 1.0;
	#endif

	#ifdef SHADOWS
		#ifndef DIRLIGHT
			float cosAngle = saturate(1.0 - dot(normal, normalize(LightPos.xyz - worldPos)));
		#else
			float cosAngle = saturate(1.0 - dot(normal, lightDir));
		#endif
		worldPosN = worldPos + (cosAngle * NormalOffset * normal);
	#else
		worldPosN = 0.0f;
	#endif
}

inline float3 CalculateScattering(float3 volumePos, float3 volumeNormal, float3 worldPos, float3 eyeDir)
{
	#ifdef DIRLIGHT
	return float3(0.0, 0.0, 0.0);
	#else
	const float3 volumeDir	= normalize(volumePos - EyePos);
	const float AttenPow = 1-pow(1.0f-saturate(dot(volumeDir, volumeNormal)),1);
	return float4(pLightColor, 1) * saturate(GetScattering(EyePos, eyeDir, LightPos.xyz) * LightScattering * 0.001 * AttenPow);
	#endif
}

inline float3 RaymarchLight(float3 volumePos, float3 volumeNormal, float3 worldPos, float2 iScreenPos)
{
	#ifdef DIRLIGHT
		return float3(0.0, 0.0, 0.0);
	#endif

	float3 rayVec = worldPos - EyePos;
	
	#ifdef VOLUMETRIC
		float3 accumulated = 0.0f;

		#ifdef SHADOWS
			float rayLength = length(rayVec);
			float3 rayDirection = rayVec / rayLength;

			float stepSize = rayLength / RAYMARCH_STEPS;
			float3 step = rayDirection * stepSize;

			float3 currentposition = EyePos + step * DITHER_PATTERN[clamp(int(iScreenPos.x * ScreenSize.x) % 4, 0, 3)][clamp(int(iScreenPos.y * ScreenSize.y) % 4, 0, 3)];

			#if defined(SPOTLIGHT)
				float4 projCoord = mul(float4(currentposition, 1.0), SpotMatrix);
				float4 projStep = mul(float4(step, 0.0), SpotMatrix);
			#endif
			
			for (int i = 0; i < RAYMARCH_STEPS; ++i)
			{
				float3 lightDir;
				float3 intensity;
				float shadow;

				#if defined(SPOTLIGHT)
					float3 spotColor = projCoord.w > 0.0 ? Sample2DProjLod0(SpotMap, projCoord).rgb * GetShadowRay(projCoord) : 0.0;
					intensity = CalculateAttenuation(LightPos.xyz - currentposition, lightDir) * spotColor;
					
					shadow = 1;
					
					float scatteringDot = dot(rayDirection, lightDir);
					float scattering = max(ComputeScattering(0.8f, 7.0f, scatteringDot), 0.25) * 0.3;
				#else
					intensity = CalculateAttenuation(LightPos.xyz - currentposition, lightDir);
					shadow = GetPointShadowRay(currentposition);
					
					float scatteringDot = dot(rayDirection, lightDir);
					float scattering = max(ComputeScattering(0.8f, 7.0f, scatteringDot), 0.25) * 0.3;
				#endif

				float dust = DustNoise(currentposition * 3.0, Time * 0.00035);
				dust = dust * dust * dust * 2.5;
				accumulated += intensity * shadow * scattering * (1.0 + dust);
				
				currentposition += step;
				
				#if defined(SPOTLIGHT)
					projCoord += projStep;
				#endif
			}

			accumulated *= stepSize * pLightColor * LightScattering * 0.1;
		#endif
		
		return lerp(accumulated, CalculateScattering(volumePos, volumeNormal, worldPos, rayVec), ShadowIntensity);
	#else
		return CalculateScattering(volumePos, volumeNormal, worldPos, rayVec);
	#endif
}

inline float3 GetVolumetricWorldPos(float3 sceneWorldPos, float3 volumeWorldPos)
{
    float3 rayDir = sceneWorldPos - EyePos;
    float sceneDist = length(rayDir);
    float volumeDist = length(volumeWorldPos - EyePos);
    return EyePos + rayDir * saturate(volumeDist / (sceneDist + 0.0001));
}

// ================================================================================== GOD RAYS DIR LIGHT

#ifdef DIRLIGHT

uniform float SunDiscSize       = 0.025;    // sun disc radius (fraction of screen height)
uniform float SunDiscIntensity  = 5.5;      // HDR core, feeds the bloom
uniform float SunGlowSize       = 0.022;    // corona/halo radius (fraction of max screen dimension)
uniform float SunGlowIntensity  = 0.40;
uniform float SunAuraSize       = 0.045;    // wide soft atmosphere radius
uniform float SunAuraIntensity  = 0.015;
uniform float SunShaftIntensity = 0.3;
uniform float SunShaftRadius    = 0.3;     // max god ray reach (fraction of max screen dimension)
uniform int   SunShaftSteps     = 48;       // max ray march samples

inline float2 GetSunScreenPosition(float3 worldPos, out float sunCos)
{
	float3 R = InvViewProj[0].xyz;
	float3 U = InvViewProj[1].xyz;
	float3 F = normalize(cross(R, U));

	if (dot(F, normalize(worldPos - EyePos)) < 0.0) F = -F;

	float3 sunDir = normalize(-LightDirection);
	sunCos = dot(sunDir, F);

	float2 ndc = float2(0.0, 0.0);
	if (sunCos > 0.0001)
	{
		float2 invFocalSq = float2(1.0 / dot(R, R), 1.0 / dot(U, U));
		ndc = float2(dot(sunDir, R) * invFocalSq.x, dot(sunDir, U) * invFocalSq.y) / sunCos;
	}
	return 0.5 * (float2(ndc.x, -ndc.y) + 1.0);
}

inline float GetSunSkyMask(float2 uv, float2 screenSizePx)
{
	float2 stepUV = 1.0 / screenSizePx;

	float d00 = Sample2DLod0(DepthMap, uv + float2(-stepUV.x, -stepUV.y)).r;
	float d10 = Sample2DLod0(DepthMap, uv + float2( stepUV.x, -stepUV.y)).r;
	float d01 = Sample2DLod0(DepthMap, uv + float2(-stepUV.x,  stepUV.y)).r;
	float d11 = Sample2DLod0(DepthMap, uv + float2( stepUV.x,  stepUV.y)).r;
	float depth = (d00 + d10 + d01 + d11) * 0.25;

	#ifdef REVERSEDZ
		depth = 1.0 - depth;
	#endif
	return smoothstep(0.99995, 0.99998, depth);
}

inline float GetSunSkyMaskPoint(float2 uv)
{
	float depth = Sample2DLod0(DepthMap, uv).r;
	#ifdef REVERSEDZ
		depth = 1.0 - depth;
	#endif
	return smoothstep(0.99995, 0.99998, depth);
}

inline float3 GetSunShafts(float2 texCoords, float2 sunUV, float sunCos, float3 sunColor, float2 screenSizePx)
{
	float3 shafts = float3(0.0, 0.0, 0.0);

	float maxDim = max(screenSizePx.x, screenSizePx.y);
	float2 delta = texCoords - sunUV;
	float distPx = length(delta * screenSizePx);
	float rayRadiusPx = SunShaftRadius * maxDim;

	if (distPx > 1.0 && distPx < rayRadiusPx)
	{
		int steps = clamp(int(distPx * 0.20), 10, SunShaftSteps);
		float2 stepVec = delta / (float)steps;

		float2 sampleUV = texCoords - stepVec * (InterleavedGradientNoise(texCoords * screenSizePx + Time * 0.0003) * 0.8);
		float acc = 0.0;
		
		float sourceR = SunGlowSize * maxDim;
		float invSourceRSq = 1.0 / (sourceR * sourceR);

		[loop]
		for (int i = 0; i < steps; ++i)
		{
			sampleUV -= stepVec;

			float2 dUVPx = (sampleUV - sunUV) * screenSizePx;
			float dSSq = dot(dUVPx, dUVPx) * invSourceRSq;
			float glow = exp(-2.0 * dSSq);

			acc += GetSunSkyMaskPoint(sampleUV) * glow;
		}

		float fade = pow(saturate(1.0 - distPx / rayRadiusPx), 2.0);
		float height = saturate(sunCos * 2.0);
		shafts = sunColor * (acc / (float)steps) * fade * height * SunShaftIntensity * GetSunSkyMaskPoint(texCoords);
	}
	return shafts;
}

inline float3 GetSunContribution(float2 texCoords, float3 worldPos)
{
	float3 contribution = float3(0.0, 0.0, 0.0);

	float sunCos;
	float2 sunUV = GetSunScreenPosition(worldPos, sunCos);
	if (sunCos <= 0.0001) return contribution;

	float2 screenSizePx = ScreenSize; 
	
	float2 deltaPx = (texCoords - sunUV) * screenSizePx;
	float distPx = length(deltaPx);

	float3 sunDir = normalize(-LightDirection);
	float3 sunUpRaw = float3(0.0, 1.0, 0.0) - sunDir * dot(sunDir, float3(0.0, 1.0, 0.0));
	float3 sunUp = (dot(sunUpRaw, sunUpRaw) > 1e-6) ? normalize(sunUpRaw) : float3(0.0, 0.0, 1.0);

	float3 rayDir = normalize(worldPos - EyePos);
	float rayDot = max(dot(rayDir, sunDir), 1e-6);
	float pxU = dot(rayDir, cross(sunDir, sunUp)) / rayDot;
	float pxV = dot(rayDir, sunUp) / rayDot;

	float3 camR = InvViewProj[0].xyz;
	float3 camU = InvViewProj[1].xyz;
	float2 invFocalSq = float2(1.0 / dot(camR, camR), 1.0 / dot(camU, camU));
	float2 upOnScreen = normalize(float2(dot(sunUp, camR) * invFocalSq.x * screenSizePx.x, -dot(sunUp, camU) * invFocalSq.y * screenSizePx.y));
	float pxUp = dot(deltaPx, upOnScreen);

	float edge = min(min(sunUV.x, sunUV.y), min(1.0 - sunUV.x, 1.0 - sunUV.y));
	float sunRadiusUV = (SunAuraSize + 0.5 * SunDiscSize) * 1.2;
	float screenFade = smoothstep(-sunRadiusUV, 0.02, edge);

	if (screenFade <= 0.0) return contribution;

	float skyMask = GetSunSkyMaskPoint(texCoords);
	float sunVis = GetSunSkyMaskPoint(sunUV);

	float airmass = min(1.0 / max(sunCos, 0.04), 25.0);
	float3 sunColor = pLightColor * exp(-airmass * float3(0.10, 0.14, 0.20));

	float discR = SunDiscSize * screenSizePx.y;
	float glowR = SunGlowSize * max(screenSizePx.x, screenSizePx.y);
	float auraR = SunAuraSize * max(screenSizePx.x, screenSizePx.y);

	float sunset = 1.0 - saturate(sunCos * 5.0);
	float glowScale = lerp(1.0, 1.35, sunset);

	float lowerLimb = saturate(0.5 - pxUp / max(discR, 1.0));
	sunColor *= lerp(float3(1.0, 1.0, 1.0), float3(1.0, 0.78, 0.50), lowerLimb * sunset);

	float tDisc = distPx / discR;
	float tGlow = distPx / (glowR * glowScale);
	float tAura = distPx / (auraR * glowScale);

	float core = exp(-tDisc * tDisc * 2.5);
	float glow = exp(-tGlow * tGlow * 3.0);
	float aura = exp(-tAura * tAura * 3.5);

	float3 discTint   = lerp(float3(1.0, 0.985, 0.94), float3(1.0, 0.74, 0.38), saturate(tDisc * tDisc));
	float3 coronaTint = float3(1.0, 0.82, 0.52);
	float3 auraTint   = float3(1.0, 0.93, 0.82);

	float ang = atan2(pxV, pxU);
	float raysT = Time * 0.00012;
	float2 dirP = float2(cos(ang), sin(ang));

	float fil1 = DustNoise(float3(dirP * 1.4, raysT * 0.6), raysT);
	float fil2 = DustNoise(float3(dirP * 4.2, raysT * 1.4), raysT * 0.9);
	float filaments = fil1 * 0.65 + fil2 * 0.35;
	
	float rayOut = smoothstep(discR * 0.9, discR * 1.2, distPx);
	
	float falloffDist = distPx / (glowR * glowScale * 2.6);
	float rayFalloff = exp(-(falloffDist * falloffDist));

	float rayHorizonFade = smoothstep(0.05, 0.35, normalize(-LightDirection).y);
	float3 coronaRays = sunColor * coronaTint * (filaments * 2.0 - 0.75) * rayOut * rayFalloff * 0.22 * sunVis * rayHorizonFade;

	float turb = DustNoise(float3(texCoords * screenSizePx * 0.005, Time * 0.00025), Time * 0.0004);
	float breakup = 1.0 + (turb - 0.5) * 0.20 * smoothstep(0.30, 0.95, tDisc);
	float shimmer = 1.0 + (turb - 0.5) * 0.08;

	float3 sun = (sunColor * discTint * core * SunDiscIntensity * shimmer
	           + sunColor * coronaTint * glow * SunGlowIntensity * breakup
	           + sunColor * auraTint * aura * SunAuraIntensity * breakup
	           + coronaRays) * skyMask * screenFade;
	           
	float3 shafts = GetSunShafts(texCoords, sunUV, sunCos, sunColor, screenSizePx) * screenFade;

	return sun + shafts;
}

#endif

// ================================================================================== LIGHTING

struct LightOutput
{
	float4 Color : OUTPUT(0);
	#ifdef SCATTERING
		float4 Volume : OUTPUT(1);
	#endif
};

LightOutput ProcessLight(PS_INPUT input)
{
	float3 diffuse, normal;
	float3 worldPos, lightVec, worldPosN, color;
	float diff, roughness, metallic;
	float2 texCoords;
	
	GetGBuffer(input.ScreenPosition, texCoords, worldPos, diffuse, normal, roughness, metallic);
	GetLighting(worldPos, normal, diff, lightVec, worldPosN);
	
	#if defined(DIRLIGHT)
		diff *= GetDirShadow(worldPosN);
		color = pLightColor;
	
	#elif defined(SPOTLIGHT)
		float4 spotPos = mul(float4(worldPos, 1.0), SpotMatrix);
		color = spotPos.w > 0.0 ? pLightColor * Sample2DProjLod0(SpotMap, spotPos).rgb * GetSpotShadow(worldPosN) : 0.0;
	#else
		diff *= GetPointShadow(worldPosN);
		color = pLightColor;
	#endif

	const float3 eyeVector = normalize(EyePos - worldPos);

	float3 minReflectance = 0.04; 
	float3 F0 = lerp(minReflectance, max(minReflectance, diffuse), metallic);
	
	float3 light = CalculatePBRLight(lightVec, color, eyeVector, normal, diffuse * (1.0 - metallic), F0, roughness, LightLength, LightDirection);
	
	LightOutput lighting;
	lighting.Color = float4(light * diff, 1.0f);

	#ifdef DIRLIGHT
		lighting.Color.rgb += GetSunContribution(texCoords, worldPos);
		#ifdef SCATTERING
			lighting.Volume = 0;
		#endif
	#else
		#ifdef SCATTERING
			float3 raymarchWorldPos = GetVolumetricWorldPos(worldPos, input.WorldPos);
			lighting.Volume = float4(RaymarchLight(input.WorldPos, input.Normal, raymarchWorldPos, texCoords), 1.0f);
		#endif
	#endif
	
	return lighting;
}

#ifdef D3D11
	DepthStencilState DepthState
	{
		DepthEnable = FALSE;
		DepthWriteMask = ZERO;
		DepthFunc = LESS_EQUAL;
	};

	RasterizerState RasterState
	{
		CullMode = FRONT;
	};
#endif

technique Main
{
	pass Light
	{
		Vertex(VertexProcess);
		Pixel(ProcessLight);
			
		#ifdef D3D11
			SetDepthStencilState(DepthState, 0);
			#ifndef DIRLIGHT
				SetRasterizerState(RasterState);
			#endif
		#else
			#ifdef DIRLIGHT
				CullMode = None;
			#else
				CullMode = CW;
			#endif
			ZWriteEnable = false;
			Lighting = false;
		#endif
	}
}