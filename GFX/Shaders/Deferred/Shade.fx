//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"

float3 EyePos			: EYE_POSITION;

const float3 LightPos;
const float LightRange;
const float3 LightColor;
const float3 LightDirection;
const float LightScattering;
const float ShadowIntensity;
const float NormalOffset = 0.05;
const float2 ShadowMapSize;
const int ShadowMapAddress = 3;
static const float2 InvShadowMapSize = 1.0 / ShadowMapSize;

const float4x4 LightViewProj;
const float4x4 LightViewProj0;
const float4x4 LightViewProj1;
const float4x4 LightViewProj2;
const float4x4 LightViewProj3;
const float4x4 LightViewProj4;
const float4x4 LightViewProj5;
const float4x4 InvViewProj;
const float4x4 ShadowsAdjust;
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

sampler AlbedoMap : register(s0) = sampler_state
{
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
    MinFilter = None;
    MagFilter = None;
	MipFilter = None;
};

sampler NormalMap : register(s1) = sampler_state
{
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
    MinFilter = None;
    MagFilter = None;
	MipFilter = None;
};

sampler DepthMap : register(s2) = sampler_state
{
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
    MinFilter = None;
    MagFilter = None;
	MipFilter = None;
};

samplerCUBE FaceSelectCubeMap : register(s3) = sampler_state
{
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
    MinFilter = Point;
    MagFilter = Point;
	MipFilter = None;
};

sampler SpotMap : register(s4) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = None;
	AddressU = Border;
	AddressV = Border;
	AddressW = Border;
	BorderColor = 0;
};

sampler RampMap : register(s5) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = None;
	AddressU = Border;
	AddressV = Border;
	AddressW = Border;
	BorderColor = 0;
};

texture tShadowMap;
sampler ShadowMap = sampler_state
{
	Texture = <tShadowMap>;
    MinFilter = Point;
    MagFilter = Linear;
	MipFilter = Point;
	AddressU = ShadowMapAddress;
	AddressV = ShadowMapAddress;
	AddressW = ShadowMapAddress;
	BorderColor = 0xFFFFFFFF;
};


struct PS_INPUT
{ 
	float4 Pos 				: POSITION0; 
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

inline float GetShadow(float4 ProjCoord)
{
	float2 offsets = (InvShadowMapSize * ProjCoord.w);
	float4 ProjCoord2 = float4(ProjCoord.x + offsets.x, ProjCoord.yzw);
	float4 ProjCoord3 = float4(ProjCoord.x, ProjCoord.y + offsets.y, ProjCoord.zw);
	float4 ProjCoord4 = float4(ProjCoord.xy + offsets.xy, ProjCoord.zw);

	float4 inLight = float4(
		Sample2DProj(ShadowMap, ProjCoord).r,
		Sample2DProj(ShadowMap, ProjCoord2).r,
		Sample2DProj(ShadowMap, ProjCoord3).r,
		Sample2DProj(ShadowMap, ProjCoord4).r
	);

	return lerp(dot(inLight, 0.25), 1.0, ShadowIntensity);
}

inline float GetPointShadow(float3 worldPos)
{
	#ifdef SHADOWS
		int face = 255 * SampleCube(FaceSelectCubeMap, worldPos - LightPos).r;
		float4 ProjCoord = mul(float4(worldPos, 1.0), LightMatrix[face]);
		ProjCoord.x = lerp(ProjCoord.x / ProjCoord.w, 0.5, InvShadowMapSize.x * 16); // Fix shadows bleeding
		ProjCoord.x = ((ProjCoord.x + face) / 6.0) * ProjCoord.w;
		return GetShadow(ProjCoord);
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

inline void GetGBuffer(float4 ScreenPosition, out float4 Albedo, out float4 Normal, out float3 worldPos, out float3 normalVec)
{
	float2 TexCoords = GetScreenTexCoords(ScreenPosition) + halfPixel;
	#ifndef LOD0
		Albedo 	= Sample2D(AlbedoMap, TexCoords);
		Normal 	= Sample2D(NormalMap, TexCoords);
		worldPos = GetWorldPosition(TexCoords, Sample2D(DepthMap, TexCoords).r, InvViewProj);
	#else
		Albedo 	= Sample2DLod0(AlbedoMap, TexCoords);
		Normal 	= Sample2DLod0(NormalMap, TexCoords);
		worldPos = GetWorldPosition(TexCoords, Sample2DLod0(DepthMap, TexCoords).r, InvViewProj);		
	#endif
	normalVec = normalize(Normal.xyz * 2.0 - 1.0);
}

inline void GetLighting(float3 worldPos, float3 normalVec, out float light, out float3 NdotL, out float3 worldPosN)
{
	#ifndef DIRLIGHT
		NdotL = normalize(LightPos - worldPos);
		float length = distance(worldPos, LightPos) / LightRange;
		light = saturate(dot(NdotL, normalVec)) * Sample2D(RampMap, float2(length, 0.0)).r;
		#ifdef SHADOWS
			float cosAngle = saturate(1.0 - dot(normalVec, NdotL));
			worldPosN = worldPos + cosAngle * NormalOffset * normalVec;
		#else
			worldPosN = 0.0f;
		#endif
	#else
		NdotL = normalize(-LightDirection);
		light = saturate(dot(NdotL, normalVec));
		#ifdef SHADOWS
			float cosAngle = saturate(1.0 - dot(normalVec, -LightDirection));
			worldPosN = worldPos + (cosAngle * NormalOffset * normalVec);
		#else
			worldPosN = 0.0f;
		#endif
	#endif
}

inline float4 CalculateScattering(float3 vworldPos, float3 worldPos, float3 normal)
{
	#ifdef SCATTERING
		const float3 PosCam	= normalize(vworldPos-EyePos);
		const float3 dir 	=  worldPos - EyePos;
		const float AttenPow = 1-pow(1.0f-saturate(dot(PosCam,normal)),1);
		return float4(LightColor, 1) * saturate(GetScattering(EyePos, dir, LightPos) * LightScattering * AttenPow);
	#else
		return 0.0;
	#endif
}

// ================================================================================== SPOTLIGHT
float4 ProcessLight(PS_INPUT input) : COLOR
{
	float4 Albedo, Normal;
	float3 worldPos, normalVec, NdotL, worldPosN, color;
	float diff;
	
	GetGBuffer(input.ScreenPosition, Albedo, Normal, worldPos, normalVec);
	GetLighting(worldPos, normalVec, diff, NdotL, worldPosN);

	#if defined(DIRLIGHT)
		diff *= GetSpotShadow(worldPosN);
		color = LightColor;
	#elif defined(SPOTLIGHT)
		float4 spotPos = mul(float4(worldPos, 1.0), SpotMatrix);
		color = spotPos.w > 0.0 ? LightColor * Sample2DProj(SpotMap, spotPos).rgb * GetSpotShadow(worldPosN) : 0.0;
	#else
		diff *= GetPointShadow(worldPosN);
		color = LightColor;
	#endif

	#ifdef SPECULAR
		float spec = GetSpecular(normalVec, EyePos - worldPos, NdotL, Normal.a * 255.0);
		return ShadeDither(ACESFilm(diff * float4(color * (Albedo.rgb + spec * Albedo.a), 0.0) + CalculateScattering(input.WorldPos, worldPos, input.Normal)), input.ScreenPosition);
	#else
		return ShadeDither(ACESFilm(diff * float4(color * Albedo.rgb, 0.0) + CalculateScattering(input.WorldPos, worldPos, input.Normal)), input.ScreenPosition);
	#endif
}

technique Main
{
	pass Light
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 ProcessLight();
		CullMode = CW;
		SrcBlend = One;
		DestBlend = One;
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}