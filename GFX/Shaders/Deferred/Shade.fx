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

float3 EyePos			: EYE_POSITION;
uniform int Time;
uniform float4 LightPos;
uniform float3 LightColor;
uniform float3 LightDirection;
uniform float LightScattering;
uniform float ShadowIntensity;
uniform float NormalOffset = 0.05;
uniform float2 ShadowMapSize;
uniform int ShadowMapAddress = 3;
uniform float LightLength;
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

inline float4 ClampShadows(float4 ProjCoord, int face)
{
    float padding = InvShadowMapSize.x; 
    float minX = (float)face / 6.0 + padding;
    float maxX = ((float)face + 1.0) / 6.0 - padding;
	#ifdef D3D11
	ProjCoord.x = clamp(ProjCoord.x, minX, maxX);
	#else
    ProjCoord.x = clamp(ProjCoord.x, minX * ProjCoord.w, maxX * ProjCoord.w);
	#endif
    return ProjCoord;
}

inline float GetShadow(float4 ProjCoord, int face = 0, float smooth = 1.0)
{
	#ifdef D3D11
		ProjCoord.xyz /= ProjCoord.w;
		float smoothCoeff = clamp(ProjCoord.z * smooth, 1.0, 3.0);
		float2 offsets = InvShadowMapSize * smoothCoeff;
	#else
		float smoothCoeff = clamp(ProjCoord.z / ProjCoord.w * smooth, 1.0, 3.0);
		float2 offsets = (InvShadowMapSize * ProjCoord.w) * smoothCoeff;
	#endif

    float sum = 0.0;

    for (int x = -1; x <= 1; ++x)
    {
        for (int y = -1; y <= 1; ++y)
        {
            float2 offset = float2(x, y) * offsets;
			float4 proj = float4(ProjCoord.xy + offset, ProjCoord.zw);
			#if !defined(DIRLIGHT) && !defined(SPOTLIGHT)
				proj = ClampShadows(proj, face);
			#endif
            sum += Sample2DShadow(ShadowMap, proj).r;
        }
    }

    return lerp(sum / 9.0, 1.0, ShadowIntensity);
}
inline float GetPointShadow(float3 worldPos)
{
	#ifdef SHADOWS
        int face = clamp((int)(255.0f * SampleCubeLOD(FaceSelectCubeMap, float4(worldPos - LightPos.xyz, 0.0)).r), 0, 5);
		float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
		ProjCoord.x = ((ProjCoord.x / ProjCoord.w + face) / 6.0) * ProjCoord.w;
		return GetShadow(ProjCoord, face);
	#else
		return 1.0;
	#endif
}

inline float GetDirShadow(float3 worldPos)
{
	#ifdef SHADOWS
		return GetShadow(mul(float4(worldPos, 1.0), SpotMatrix), 0, 0.0f);
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
        int face = 255 * SampleCubeLOD(FaceSelectCubeMap, float4(worldPos - LightPos.xyz, 0.0)).r;
		float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
		ProjCoord.x = ((ProjCoord.x / ProjCoord.w + face) / 6.0) * ProjCoord.w;
		return GetShadowRay(ProjCoord);
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
		lightVec = LightDirection;
		float3 lightDir = LightDirection;
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
	const float3 volumeDir	= normalize(volumePos - EyePos);
	const float AttenPow = 1-pow(1.0f-saturate(dot(volumeDir, volumeNormal)),1);
	return float4(pLightColor, 1) * saturate(GetScattering(EyePos, eyeDir, LightPos.xyz) * LightScattering * 0.001 * AttenPow);
}

inline float3 RaymarchLight(float3 volumePos, float3 volumeNormal, float3 worldPos, float2 iScreenPos)
{
	float3 rayVec = worldPos - EyePos;
	
	#ifdef VOLUMETRIC
		float3 accumulated = 0.0f;

		#ifdef SHADOWS
			float rayLength = length(rayVec);
			float3 rayDirection = rayVec / rayLength;

			float stepSize = rayLength / RAYMARCH_STEPS;
			float3 step = rayDirection * stepSize;

			float3 currentposition = EyePos + step * DITHER_PATTERN[clamp(int(iScreenPos.x * ScreenSize.x) % 4, 0, 3)][clamp(int(iScreenPos.y * ScreenSize.y) % 4, 0, 3)];

			#if defined(DIRLIGHT) || defined(SPOTLIGHT)
				float4 projCoord = mul(float4(currentposition, 1.0), SpotMatrix);
				float4 projStep = mul(float4(step, 0.0), SpotMatrix);
			#endif
			
			for (int i = 0; i < RAYMARCH_STEPS; ++i)
			{
				float3 lightDir;
				float3 intensity;
				float shadow;

				#if defined(DIRLIGHT)
					lightDir = LightDirection;
					shadow = GetShadowRay(projCoord);
					intensity = 1.0f;
				#elif defined(SPOTLIGHT)
					float3 spotColor = projCoord.w > 0.0 ? Sample2DProjLod0(SpotMap, projCoord).rgb * GetShadowRay(projCoord) : 0.0;
					intensity = CalculateAttenuation(LightPos.xyz - currentposition, lightDir) * spotColor;
					
					shadow = 1;
				#else
					intensity = CalculateAttenuation(LightPos.xyz - currentposition, lightDir);
					shadow = GetPointShadowRay(currentposition);
				#endif

				float dust = DustNoise(currentposition * 3.0, Time * 0.00035);
				dust = dust * dust * dust * 2.5;
				
				float scatteringDot = dot(rayDirection, lightDir);
				float scattering = max(ComputeScattering(0.8f, 7.0f, scatteringDot), 0.25) * 0.3;
				accumulated += intensity * shadow * (1.0 + dust) * scattering;
				
				currentposition += step;
				
				#if defined(DIRLIGHT) || defined(SPOTLIGHT)
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
	#ifdef SCATTERING
		float3 raymarchWorldPos = GetVolumetricWorldPos(worldPos, input.WorldPos); // Fix raymarch on non rendered
		lighting.Volume = float4(RaymarchLight(input.WorldPos, input.Normal, raymarchWorldPos, texCoords), 1.0f);
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
			CullMode = CW;
			ZWriteEnable = false;
			Lighting = false;
		#endif
	}
}