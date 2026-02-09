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
static const float NoiseSize = ScreenSize / 512.0;
static const float2 LowResTexelSize = 1.0 / (ScreenSize * 0.5);
static const float2 InvScreenSize = 1.0 / ScreenSize;

uniform float SSAOStrength = 1.f;
uniform float SSAORadius = 0.15f;
uniform float SSAOBias = 0.1f;
uniform float BloomThreshold;
uniform float4x4 InvViewProj;
uniform float3 CameraPosition;
uniform float FarClip;
uniform float2 BlurInvSize;
static const float FarClipSqr = FarClip * FarClip;

static const float2 SSAOSamples[NUM_SAMPLES] =
{
	float2(1,0),
	float2(-1,0), 
	float2(0,1),
	float2(0,-1)
};

static const int MAX_WEIGHTS = 9;
static const float offsets[MAX_WEIGHTS] = {
    4.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0, -4.0
};

static const float weights[MAX_WEIGHTS] = {
    0.052, 0.092, 0.122, 0.152, 0.162, 0.152, 0.122, 0.092, 0.052
};
	
static const float DEPTH_FALLOFF = 2.f;

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
	float3 nd = diff / scale;
	return saturate(dot(normal, nd) - SSAOBias) * (1.0 / (1.0 + scale)) * SSAOStrength;
}

float4 SSAOProcess(PS_INPUT input) : COLOR
{ 
	float3 position = GetPosition(input.TexCoord); 
	float len = GetLength(CameraPosition, position);
	if(len > FarClipSqr || GetIntensity(GetBloomLuma(max(Sample2D(ColorMap, input.TexCoord).rgb - Sample2D(AlbedoMap, input.TexCoord).rgb, 0.0), BloomThreshold)) > 0.0) return 1.0;

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

float4 BilateralProcess(PS_INPUT input) : COLOR
{
	float depth = GetLength(CameraPosition, GetPosition(input.TexCoord));
	return float4(depth, 0, 0, 1);
}

float4 NormalProcess(PS_INPUT input) : COLOR
{
	return normalize(Sample2D(NormalMap, input.TexCoord));
}

float4 BlurProcess(PS_INPUT input) : COLOR
{
    float centerDepth = Sample2D(DepthMap, input.TexCoord).r;
	float3 centerNormal = normalize(Sample2D(NormalMap, input.TexCoord).xyz);

    float3 accColor = 0.0f;
    float totalWeight = 1e-6f;

    [unroll]
    for (int i = 0; i < MAX_WEIGHTS; i++)
    {
        float2 tex = input.TexCoord + BlurInvSize * offsets[i];

		float neighborDepth = Sample2D(DepthMap, tex).r;
		float3 neighborNormal = normalize(Sample2D(NormalMap, tex).xyz);
		
        float depthDiff = abs(centerDepth - neighborDepth);
        float rangeWeight = saturate(1.0f - depthDiff * DEPTH_FALLOFF);
        float normalWeight = saturate(dot(centerNormal, neighborNormal)) + 0.00001;
        
        float weight = weights[i] * rangeWeight * normalWeight;
        accColor += weight * Sample2D(SSAOMap, tex).r;
        totalWeight += weight;
    }

    return float4((accColor / totalWeight).xxx, 1.0);
}

float4 FinalProcess(PS_INPUT input) : COLOR
{
    float fullResDepth = Sample2D(DepthMap, input.TexCoord).r;
	if(fullResDepth >= FarClipSqr) return 1.0;
	
    float3 fullResNormal = normalize(Sample2D(NormalMap, input.TexCoord).xyz);
    
    float2 lowResUV = input.TexCoord; 
    float2 base_uv = floor(lowResUV / LowResTexelSize - 0.5) * LowResTexelSize + 0.5 * LowResTexelSize;

    float totalAO = 0.0;
    float totalWeight = 0.00001;

    [unroll]
    for(int i = 0; i < 4; i++)
    {
        float2 offset = float2(i % 2, i / 2) * LowResTexelSize;
        float2 sampleUV = base_uv + offset;

        float aoLow = Sample2D(SSAOMap, sampleUV).r;
        float depthLow = Sample2D(DepthMapLow, sampleUV).r; 
        float3 normalLow = normalize(Sample2D(NormalMapLow, sampleUV).xyz);

        float depthDiff = abs(fullResDepth - depthLow);
        float normalDiff = saturate(dot(fullResNormal, normalLow));

        float weight = pow(normalDiff, 16.0) / (0.0001 + depthDiff);

        totalAO += aoLow * weight;
        totalWeight += weight;
    }

    float finalAO = totalAO / totalWeight;
    return float4(finalAO.xxx, 1.0);
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

technique Normal
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(NormalProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}

technique Bilateral
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(BilateralProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}

technique Blur
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(BlurProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}

technique Final
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(FinalProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}