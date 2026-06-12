//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 4
static const float INV_SAMPLES = 1.0 / (NUM_SAMPLES * 4);

uniform float SSAOStrength = 1.f;
uniform float SSAORadius = 0.15f;
uniform float SSAOBias = 0.1f;
uniform float SSAOMaxDistance = 1.5f;
uniform float BloomThreshold;
uniform float4x4 InvViewProj;
uniform float3 CameraPosition;
uniform float FarClip;
static const float FarClipSqr = FarClip * FarClip;

static const float2 SSAOSamples[NUM_SAMPLES] =
{
	float2(1,0),
	float2(-1,0), 
	float2(0,1),
	float2(0,-1)
};

#ifdef D3D11
	texture2D tColorMap : register(t0);
	texture2D tNormalMap : register(t1);
	texture2D tDepthMap : register(t2);
	texture2D tAlbedoMap : register(t3);
	texture2D tNoiseMap : register(t4);
	texture2D tSSAOMap : register(t5);
	texture2D tDepthMapLow : register(t6);
	texture2D tNormalMapLow : register(t7);
	
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler AlbedoMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler NoiseMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Wrap; AddressV = Wrap; };
	sampler SSAOMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMapLow = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMapLow = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s0) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap : register(s1) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap : register(s2) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler AlbedoMap : register(s3) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NoiseMap : register(s4) = sampler_state { MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; AddressU = Wrap; AddressV = Wrap; };
	sampler SSAOMap : register(s5) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMapLow : register(s6) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMapLow : register(s7) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
#endif

struct PS_INPUT
{ 
	float4 Pos 				: OUT_POSITION; 
	float2 TexCoord 		: TEXCOORD0;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
	return output;
}

inline float3 GetPosition(in float2 uv)
{
	return GetWorldPosition(uv, Sample2D(DepthMap, uv).r, InvViewProj);
}

inline float GetLength(float3 src, float3 dest)
{
	const float3 diff = src - dest;
    return dot(diff, diff); 
}

inline float CalculateAO(in float2 centerUV, in float2 uv, in float3 position, in float3 normal) 
{
	float3 diff = GetPosition(centerUV + uv) - position; 
	float scale = length(diff);
	float falloff = smoothstep(SSAOMaxDistance, 0.0, scale);
	float3 nd = diff / scale;
	return saturate(dot(normal, nd) - SSAOBias) * falloff * SSAOStrength;
}

float4 SSAOProcess(PS_INPUT input) : OUTPUT(0)
{ 
	float3 position = GetPosition(input.TexCoord); 
	float len = GetLength(CameraPosition, position);
	if(len > FarClipSqr) return 1.0;

	float3 normal = normalize(Sample2D(NormalMap, input.TexCoord).xyz);
	float noise = InterleavedGradientNoise(input.TexCoord * (ScreenSize / 8));
	float2 randomNormal;
	sincos(noise * 6.283185, randomNormal.y, randomNormal.x);

	float radius = max(SSAORadius / sqrt(len), 8 / ScreenSize);
	
	float ao = 0.0f; 

	[unroll]for (int j = 0; j < NUM_SAMPLES; ++j) 
	{
		float2 coord1 = reflect(SSAOSamples[j], randomNormal) * radius; 
		float2 coord2 = float2(coord1.x * 0.7 - coord1.y * 0.7, coord1.x * 0.7 + coord1.y * 0.7); 

		ao += CalculateAO(input.TexCoord, coord1 * 0.25, position, normal); 
		ao += CalculateAO(input.TexCoord, coord2 * 0.5, position, normal); 
		ao += CalculateAO(input.TexCoord, coord1 * 0.75, position, normal); 
		ao += CalculateAO(input.TexCoord, coord2, position, normal);
	}
	
	return lerp(1.0 - ao * INV_SAMPLES, 1.0, 1.0 - GetFade(len, FarClipSqr * 0.8, FarClipSqr));
}

technique SSAO
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(SSAOProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}