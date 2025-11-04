//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 4

const float SSAOStrength = 1.f;
const float SSAORadius = 0.15f;
const float SSAOBias = 0.1f;
const float4x4 InvViewProj;
const float3 CameraPosition;

static const float2 SSAOSamples[NUM_SAMPLES] =
{
	float2(1,0),
	float2(-1,0), 
	float2(0,1),
	float2(0,-1)
};

sampler NormalMap : register(s1) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
};

sampler DepthMap : register(s2) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
};

sampler AlbedoMap : register(s3) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
};

sampler NoiseMap : register(s4) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Mirror;
	AddressV = Mirror;
	AddressW = Mirror;
};

struct PS_INPUT
{ 
	float4 Pos 				: POSITION0; 
	float2 TexCoord 		: TEXCOORD0;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
	return output;
}

float3 GetPosition(in float2 uv)
{
	return GetWorldPosition(uv, Sample2DLod0(DepthMap, uv).r, InvViewProj);
}

float CalculateAO(in float2 centerUV, in float2 uv, in float3 position, in float3 normal) 
{
	float3 diff = GetPosition(centerUV + uv) - position; 
	const float3 nd = normalize(diff); 
	const float scale = length(diff); 
	return max(0.0, dot(normal, nd) - SSAOBias) * (1.0 / (1.0 + scale)) * SSAOStrength;
}

float4 SSAOProcess(PS_INPUT input) : COLOR
{ 
	float3 position = GetPosition(input.TexCoord); 
	float3 normal = normalize(Sample2DLod0(NormalMap, input.TexCoord).xyz * 2.0 - 1.0f);
	float2 randomNormal = normalize(Sample2DLod0(NoiseMap, ScreenSize / 256.0 * input.TexCoord).xy * 2.0 - 1.0f);
	float radius = SSAORadius / length(CameraPosition - position); 
	
	float ao = 0.0f; 
	for (int j = 0; j < NUM_SAMPLES; ++j) 
	{
		float2 coord1 = reflect(SSAOSamples[j], randomNormal) * radius; 
		float2 coord2 = float2(coord1.x * 0.7 - coord1.y * 0.7, coord1.x * 0.7 + coord1.y * 0.7); 

		ao += CalculateAO(input.TexCoord, coord1 * 0.25, position, normal); 
		ao += CalculateAO(input.TexCoord, coord2 * 0.5, position, normal); 
		ao += CalculateAO(input.TexCoord, coord1 * 0.75, position, normal); 
		ao += CalculateAO(input.TexCoord, coord2, position, normal); 
	}

	return 1.0 - (ao / (NUM_SAMPLES * 4));
}

technique Main
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 SSAOProcess();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}