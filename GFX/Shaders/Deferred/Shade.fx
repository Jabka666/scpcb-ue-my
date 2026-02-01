//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"

float3 EyePos			: EYE_POSITION;

uniform float4 LightPos;
uniform float3 LightColor;
uniform float3 LightDirection;
uniform float LightScattering;
uniform float ShadowIntensity;
uniform float NormalOffset = 0.05;
uniform float2 ShadowMapSize;
uniform int ShadowMapAddress = 3;
static const float2 InvShadowMapSize = 1.0 / ShadowMapSize;

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

	texture2D tRampMap : register(t5);
	sampler RampMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Border; AddressV = Border; BorderColor = float4(0.0, 0.0, 0.0, 0.0); };

	texture2D tMetallicMap : register(t6);
	sampler MetallicMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT; };
	
	Texture2D tShadowMap;
	SamplerComparisonState ShadowMap = sampler_state { Filter = COMPARISON_MIN_MAG_LINEAR_MIP_POINT; AddressU = ShadowMapAddress; AddressV = ShadowMapAddress; BorderColor = float4(1.0, 1.0, 1.0, 1.0); ComparisonFunc = LESS_EQUAL; };
#else
	sampler AlbedoMap : register(s0) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler NormalMap : register(s1) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler DepthMap : register(s2) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	samplerCUBE FaceSelectCubeMap : register(s3) = sampler_state { AddressU = Clamp; AddressV = Clamp; AddressW = Clamp; MinFilter = Point; MagFilter = Point; MipFilter = Point;  };

	sampler SpotMap : register(s4) = sampler_state { MinFilter = Linear; MagFilter = Linear; MipFilter = None; AddressU = Border; AddressV = Border; BorderColor = 0; };

	sampler RampMap : register(s5) = sampler_state { MinFilter = Linear; MagFilter = Linear; MipFilter = None; AddressU = Border; AddressV = Border; BorderColor = 0; };

	texture tShadowMap;
	sampler ShadowMap = sampler_state { Texture = <tShadowMap>; 
	MinFilter = Point; MagFilter = Linear; 
	MipFilter = Point; AddressU = ShadowMapAddress; AddressV = ShadowMapAddress; AddressW = ShadowMapAddress; BorderColor = 0xFFFFFFFF; };
#endif


struct PS_INPUT
{ 
	float4 Pos 				: POSITION; 
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

inline float GetShadow(float4 ProjCoord, int face = 0)
{
	#ifdef D3D11
		ProjCoord.xyz /= ProjCoord.w;
		float smoothCoeff = clamp(ProjCoord.z * 1.3, 1.0, 3.0);
		float2 offsets = InvShadowMapSize * smoothCoeff;
	#else
		float smoothCoeff = clamp(ProjCoord.z / ProjCoord.w * 1.3, 1.0, 3.0);
		float2 offsets = (InvShadowMapSize * ProjCoord.w) * smoothCoeff;
	#endif

    float sum = 0.0;

    [unroll]for (int x = -2; x <= 2; ++x)
    {
        [unroll]for (int y = -2; y <= 2; ++y)
        {
            float2 offset = float2(x, y) * offsets;
			float4 proj = float4(ProjCoord.xy + offset, ProjCoord.zw);
			#if !defined(DIRLIGHT) && !defined(SPOTLIGHT)
				proj = ClampShadows(proj, face);
			#endif
            sum += Sample2DShadow(ShadowMap, proj).r;
        }
    }

    float shadowFactor = pow(sum / 25.0, 0.707);
    return lerp(shadowFactor, 1.0, ShadowIntensity);
}

inline float GetPointShadow(float3 worldPos)
{
	#ifdef SHADOWS
        int face = 255 * SampleCubeLOD(FaceSelectCubeMap, float4(worldPos - LightPos.xyz, 0.0)).r;
		float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
		ProjCoord.x = ((ProjCoord.x / ProjCoord.w + face) / 6.0) * ProjCoord.w;
		return GetShadow(ProjCoord, face);
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

inline void GetGBuffer(float4 ScreenPosition, out float3 worldPos, out float3 diffuse, out float3 normal, out float roughness, out float metallic)
{
	float2 TexCoords = GetScreenTexCoords(ScreenPosition) + halfPixel;
	float4 Albedo = Sample2DLod0(AlbedoMap, TexCoords);
	float4 Normals = Sample2DLod0(NormalMap, TexCoords);
	worldPos = GetWorldPosition(TexCoords, Sample2DLod0(DepthMap, TexCoords).r, InvViewProj);
	
	metallic = Normals.a;
	roughness = length(Normals.xyz);
	normal = normalize(Normals.xyz);
	diffuse = Albedo.rgb;
}

inline void GetLighting(float3 worldPos, float3 normal, out float light, out float3 lightDir, out float3 worldPosN)
{
	#ifndef DIRLIGHT
		float3 lightVec = (LightPos.xyz - worldPos) * LightPos.w;
		float len = length(lightVec);
		lightDir = lightVec / len;
		light = saturate(dot(normal, lightDir)) * Sample2DLod0(RampMap, float2(len, 0.0)).r;
	#else
		lightDir = LightDirection;
		light = saturate(dot(normal, lightDir));
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

inline float4 CalculateScattering(float3 vworldPos, float3 worldPos, float3 normal)
{
	#ifdef SCATTERING
		const float3 PosCam	= normalize(vworldPos-EyePos);
		const float3 dir 	=  worldPos - EyePos;
		
		const float AttenPow = 1-pow(1.0f-saturate(dot(PosCam,normal)),1);
		return float4(LightColor, 1) * saturate(GetScattering(EyePos, dir, LightPos.xyz) * LightScattering * AttenPow);
	#else
		return float4(0.0f, 0.0f, 0.0f, 0.0f);
	#endif
}

// ================================================================================== LIGHTING
float4 ProcessLight(PS_INPUT input) : COLOR
{
	float3 diffuse, normal;
	float3 worldPos, lightDir, worldPosN, color;
	float diff, roughness, metallic;
	
	GetGBuffer(input.ScreenPosition, worldPos, diffuse, normal, roughness, metallic);
	GetLighting(worldPos, normal, diff, lightDir, worldPosN);
	
	#if defined(DIRLIGHT)
		diff *= GetSpotShadow(worldPosN);
		color = LightColor;
	#elif defined(SPOTLIGHT)
		float4 spotPos = mul(float4(worldPos, 1.0), SpotMatrix);
		color = spotPos.w > 0.0 ? LightColor * Sample2DProjLod0(SpotMap, spotPos).rgb * GetSpotShadow(worldPosN) : 0.0;
	#else
		diff *= GetPointShadow(worldPosN);
		color = LightColor;
	#endif
	
	diff *= 12.0;

	const float3 eyeVector = normalize(EyePos - worldPos);
	const float3 lightVec = normalize(lightDir); 
	const float3 F0 = lerp(0.07, diffuse / max(1.0 - metallic, 0.00001), metallic);

	float3 BRDF = GetBRDF(0.5, worldPos, lightVec, eyeVector, normal, roughness, diffuse, F0);

	return float4(BRDF * color * diff / PI + CalculateScattering(input.WorldPos, worldPos, input.Normal), 1.0f);
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

	BlendState BlendingState
	{
		BlendEnable[0] = true;
		SrcBlend[0] = One;
		DestBlend[0] = One;
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
			SetRasterizerState(RasterState);
			SetBlendState(BlendingState, float4(1, 1, 1, 1), -1);
		#else
			CullMode = CW;
			SrcBlend = One;
			DestBlend = One;
			ZWriteEnable = false;
			Lighting = false;
		#endif
	}
}