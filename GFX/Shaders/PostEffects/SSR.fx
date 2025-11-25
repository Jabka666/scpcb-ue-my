//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 8
#define SAMPLE_COUNT 4

const float4x4 InvViewProj;
const float4x4 CamViewProj;
const float3 CameraPosition;
const float FarClip;
const float DepthCheckBias = 0.1;

static const float2 RAND_SAMPLES[SAMPLE_COUNT] = {
    float2(0.127, 0.654),
    float2(0.321, 0.879),
    float2(0.456, 0.123),
    float2(0.789, 0.234)
};

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
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

sampler ReflectionMap : register(s4) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
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

float3 GetPosition(in float2 uv, float depth)
{
	return GetWorldPosition(uv, depth, InvViewProj);
}

float3 GetUVFromPosition(in float3 pos)
{
	float4 proj = mul(float4(pos, 1.0), CamViewProj);
	proj.xyz /= proj.w;
	float2 uv = proj.xy * 0.5 + 0.5;
	uv.y = 1.0 - uv.y;
	return float3(uv, proj.z);
}

float GetDepth(float2 uv)
{
	return Sample2DLod0(DepthMap, uv).r;
}

struct RayTraceOutput
{
	bool Hit;
	float2 UV;
};

RayTraceOutput TraceRay(float2 texCoord)
{
	RayTraceOutput output;
	// Now get the position
	float3 reflPosition = GetPosition(texCoord, GetDepth(texCoord));
	//tranform normal back into [-1,1] range
	float3 reflNormal = normalize(Sample2D(NormalMap, texCoord).xyz * 2.0 - 1.0f);
	
	// First, Get the View Direction
	float3 vDir = normalize(reflPosition - CameraPosition);
	float3 reflectDir = normalize(reflect(vDir, reflNormal));
	
	// The Current Position in 3D
	float3 curPos = 0;

	// The Current UV
	float3 curUV = 0;

	// The Current Length
	float curLength = 1;

	// Now loop
    for (int a = 0; a < NUM_SAMPLES; a++)
    {
		// Update the Current Position of the Ray
		curPos = reflPosition + reflectDir * curLength;
		// Get the UV Coordinates of the current Ray
		curUV = GetUVFromPosition(curPos);
		// The Depth of the Current Pixel
		float curDepth = GetDepth(curUV.xy);
		for (int i = 0; i < SAMPLE_COUNT; i++)
		{
			if (abs(curUV.z - curDepth) < DepthCheckBias)
			{
				// If it's hit something, then return the UV position
				output.Hit = true;
				output.UV = curUV.xy;
				return output;
			}
			curDepth = GetDepth(curUV.xy + (RAND_SAMPLES[i].xy * halfPixel * 2));
		}

		// Get the New Position and Vector
		float3 newPos = GetPosition(curUV.xy, curDepth);
		curLength = length(reflPosition - newPos);
    }
    return output;
}

float4 SSRProcess(PS_INPUT input) : COLOR
{ 
	RayTraceOutput ray = TraceRay(input.TexCoord);
	
	float amount = Sample2D(NormalMap, input.TexCoord).a * 10.0;
	if(amount <= 0.0) return 0;
	
	const float EdgeCutOff = 0.5;
	if (ray.Hit == true)
	{
		// Fade at edges
		if (ray.UV.y < EdgeCutOff * 2)
			amount *= (ray.UV.y / EdgeCutOff / 2);

		return float4(ray.UV.xy, amount, 1.0);
	}
	return 0;
}

float4 CombineProcess(PS_INPUT input) : COLOR
{ 
	float3 reflectionData = Sample2D(ReflectionMap, input.TexCoord).xyz;
	
	return Sample2D(ColorMap, input.TexCoord) + (Sample2D(ColorMap, reflectionData.xy) * reflectionData.z);
}

technique SSR
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 SSRProcess();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}

technique Combine
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 CombineProcess();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}