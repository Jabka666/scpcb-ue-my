//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

uniform float3 CameraPosition;
uniform float2 BlurInvSize;
uniform float FarClip;
uniform float4x4 InvViewProj;
uniform float2 LowResTexelSize;

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
	texture2D tDepthMapLow : register(t3);
	texture2D tNormalMapLow : register(t4);
	
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMapLow = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMapLow = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s0) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap : register(s1) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap : register(s2) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMapLow : register(s3) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMapLow : register(s4) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
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
    return length(src - dest);
}

float4 BilateralProcess(PS_INPUT input) : OUTPUT(0)
{
	float depth = GetLength(CameraPosition, GetPosition(input.TexCoord));
	return float4(depth, 0, 0, 1);
}

float4 NormalProcess(PS_INPUT input) : OUTPUT(0)
{
	return normalize(Sample2D(NormalMap, input.TexCoord));
}

float4 BlurProcess(PS_INPUT input) : OUTPUT(0)
{
    float centerDepth = Sample2D(DepthMap, input.TexCoord).r;
    float3 centerNormal = normalize(Sample2D(NormalMap, input.TexCoord).xyz);

    float3 accColor = Sample2D(ColorMap, input.TexCoord).rgb * 0.0001;
    float totalWeight = 0.0001;
	
	float far = step(FarClip * 0.9, centerDepth);

    [unroll]
    for (int i = 0; i < MAX_WEIGHTS; i++)
    {
        float2 tex = input.TexCoord + BlurInvSize * offsets[i];

        float neighborDepth = Sample2D(DepthMap, tex).r;
        float3 neighborNormal = normalize(Sample2D(NormalMap, tex).xyz);
		
        float depthDiff = abs(centerDepth - neighborDepth);
        float normalizedDiff = depthDiff / (centerDepth + 0.0001);
        float rangeWeight = saturate(1.0f - normalizedDiff * DEPTH_FALLOFF);
        float normalWeight = saturate(dot(centerNormal, neighborNormal)) + 0.00001;
		float geoWeight = rangeWeight * normalWeight;
		float neighborfar = step(FarClip * 0.9, neighborDepth);
        float weight = weights[i] * lerp(geoWeight, neighborfar, far);
		
        accColor += weight * Sample2D(ColorMap, tex).rgb;
        totalWeight += weight;
    }

    return float4(accColor / totalWeight, 1.0);
}

float4 FinalProcess(PS_INPUT input) : OUTPUT(0)
{
    float fullResDepth = Sample2D(DepthMap, input.TexCoord).r;
	float3 centerColor = Sample2D(ColorMap, input.TexCoord).rgb;
	
    float3 fullResNormal = normalize(Sample2D(NormalMap, input.TexCoord).xyz);
    
    float2 lowResUV = input.TexCoord; 
    float2 base_uv = floor(lowResUV / LowResTexelSize - 0.5) * LowResTexelSize + 0.5 * LowResTexelSize;

	float3 totalColor = centerColor * 0.0001;
    float totalWeight = 0.0001;

    [unroll]
    for(int i = 0; i < 4; i++)
    {
        float2 offset = float2(i % 2, i / 2) * LowResTexelSize;
        float2 sampleUV = base_uv + offset;

        float3 low = Sample2D(ColorMap, sampleUV).rgb;
        float depthLow = Sample2D(DepthMapLow, sampleUV).r; 
        float3 normalLow = normalize(Sample2D(NormalMapLow, sampleUV).xyz);

        float depthDiff = abs(fullResDepth - depthLow);
        float normalDiff = saturate(dot(fullResNormal, normalLow));

        float weight = pow(normalDiff, 8.0) * exp(-depthDiff * DEPTH_FALLOFF);

        totalColor += low * weight;
        totalWeight += weight;
    }

	float far = step(fullResDepth, FarClip - 0.01);
    float3 finalColor = totalColor / totalWeight;
    return float4(lerp(centerColor, finalColor, far), 1.0);
}

float4 FinalSimpleProcess(PS_INPUT input) : OUTPUT(0)
{
    return float4(Sample2D(ColorMap, input.TexCoord).rgb, 1.0);
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

technique FinalSimple
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(FinalSimpleProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}
