//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 3
static const float INV_SAMPLES = 1.0 / (NUM_SAMPLES * 4);
static const float NoiseSize = ScreenSize / 512.0;

const float SSAOStrength = 1.f;
const float SSAORadius = 0.15f;
const float SSAOBias = 0.1f;
const float BloomThreshold;
const float4x4 InvViewProj;
const float3 CameraPosition;
const float FarClip;
const float2 BlurInvSize;

static const float FarClipSqr = FarClip * FarClip;

static const float2 SSAOSamples[NUM_SAMPLES] =
{
    float2(1, 0),
    float2(-0.5, 0.866),
    float2(-0.5, -0.866)
};

static const int MAX_WEIGHTS = 9;
static const float offsets[MAX_WEIGHTS] = {
    4.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0, -4.0
};

static const float weights[MAX_WEIGHTS] = {
    0.052, 0.092, 0.122, 0.152, 0.162, 0.152, 0.122, 0.092, 0.052
};
	
static const float DEPTH_FALLOFF = 20.0f;

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler NormalMap : register(s1) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler DepthMap : register(s2) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler AlbedoMap : register(s3) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler NoiseMap : register(s4) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
    MipFilter = Linear;
    AddressU = Wrap;
    AddressV = Wrap;
    AddressW = Wrap;
};

sampler SSAOMap : register(s5) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
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

inline float3 GetPosition(in float2 uv)
{
	return GetWorldPosition(uv, Sample2D(DepthMap, uv).r, InvViewProj);
}

inline float GetPositionLength(float3 position)
{
    const float3 diff = CameraPosition - position;
    return dot(diff, diff);
}

inline float CalculateAO(in float2 centerUV, in float2 uv, in float3 position, in float3 normal) 
{
	const float3 diff = GetPosition(centerUV + uv) - position; 
	const float scale = length(diff);
	const float3 nd = diff / scale;
	return max(0.0, dot(normal, nd) - SSAOBias) * (1.0 / (1.0 + scale)) * SSAOStrength;
}

float4 SSAOProcess(PS_INPUT input) : COLOR
{ 
	const float3 position = GetPosition(input.TexCoord); 
	const float len = GetPositionLength(position);
	if(len > FarClipSqr || GetBloomLuma(Sample2D(ColorMap, input.TexCoord).rgb, BloomThreshold) > 0.0) return 1.0;

	const float3 normal = normalize(Sample2D(NormalMap, input.TexCoord).xyz * 2.0 - 1.0f);
	const float2 randomNormal = normalize(Sample2D(NoiseMap, input.TexCoord * NoiseSize).xy * 2.0 - 1.0f);
	const float radius = SSAORadius / sqrt(len);
	
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

float4 BlurProcess(PS_INPUT input) : COLOR
{
    const float centerDepth = GetPositionLength(GetPosition(input.TexCoord));

    if(centerDepth > FarClipSqr) return 1.0;

    float accColor = 0.0f;
    float totalWeight = 1e-6f;

    [unroll]for (int i = 0; i < MAX_WEIGHTS; i++)
    {
        const float2 tex = input.TexCoord + BlurInvSize * offsets[i];
        const float neighborDepth = GetPositionLength(GetPosition(tex));
        const float depthDiff = abs(centerDepth - neighborDepth);
        const float rangeWeight = saturate(1.0f - depthDiff * DEPTH_FALLOFF);
        const float weight = weights[i] * rangeWeight;
        accColor += weight * Sample2D(SSAOMap, tex).r;
        totalWeight += weight;
    }

    return float4((accColor / totalWeight).xxx, 1.0);
}

technique SSAO
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

technique Blur
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 BlurProcess();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}