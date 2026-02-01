//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_RAYS 8
#define NUM_STEPS 6
static const float INV_RAYS = 1.0 / NUM_RAYS;
static const float2 LowResTexelSize = 1.0 / (ScreenSize * 0.5);
static const float2 InvScreenSize = 1.0 / ScreenSize;
static const float NoiseSize = ScreenSize / 64.0;

uniform float SSGIRadius = 2.0f;
uniform float SSGIIntensity = 0.5f;
uniform float SSGIBias = 0.0f;
uniform float4x4 InvViewProj;
uniform float4x4 ViewProjc;
uniform float3 CameraPosition;
uniform float FarClip;
uniform float2 BlurInvSize;

static const float FarClipSqr = FarClip * FarClip;

static const int MAX_WEIGHTS = 9;
static const float offsets[MAX_WEIGHTS] = {
    4.0, 3.0, 2.0, 1.0, 0.0, -1.0, -2.0, -3.0, -4.0
};
static const float weights[MAX_WEIGHTS] = {
    0.052, 0.092, 0.122, 0.152, 0.162, 0.152, 0.122, 0.092, 0.052
};
	
static const float DEPTH_FALLOFF = 0.1f;

#ifdef D3D11
	texture2D tColorMap : register(t0);
	texture2D tNormalMap : register(t1);
	texture2D tDepthMap : register(t2);
	texture2D tAlbedoMap : register(t3);
	texture2D tSSGIMap : register(t4);
	texture2D tNoiseMap : register(t5);
	texture2D tDepthMapLow : register(t6);
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler AlbedoMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler NoiseMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Wrap; AddressV = Wrap; };
	sampler SSGIMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMapLow = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s0) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap : register(s1) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap : register(s2) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler AlbedoMap : register(s3) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler SSGIMap : register(s4) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NoiseMap : register(s5) = sampler_state { MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; AddressU = Wrap; AddressV = Wrap; };
	sampler DepthMapLow : register(s6) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
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
	return GetWorldPosition(uv, Sample2DLod0(DepthMap, uv).r, InvViewProj);
}

inline float GetPositionLength(float3 position)
{
    const float3 diff = CameraPosition - position;
    return dot(diff, diff);
}

float3x3 GetTangentBasis(float3 normal)
{
    float3 tangent = cross(normal, float3(0, 1, 0));
    if (length(tangent) < 0.001) tangent = cross(normal, float3(1, 0, 0));
    tangent = normalize(tangent);
    float3 bitangent = cross(normal, tangent);
    return float3x3(tangent, bitangent, normal);
}

// Проекция мировой позиции в UV координаты
float2 ProjectToScreen(float3 worldPos)
{
    float4 clipPos = mul(float4(worldPos, 1.0f), ViewProjc);
    float2 ndc = clipPos.xy / clipPos.w;
    return ndc * float2(0.5f, -0.5f) + 0.5f;
}

float4 SSGIProcess(PS_INPUT input) : COLOR
{
    const float3 worldPos = GetPosition(input.TexCoord);
    const float len = GetPositionLength(worldPos);
    if(len > FarClipSqr) return 0.0f;

    const float3 normal = normalize(Sample2DLod0(NormalMap, input.TexCoord).xyz);

    float3 randomVals = Sample2D(NoiseMap, input.TexCoord * NoiseSize).xyz;
    
    float3 giAccum = 0.0f;
    float validSamples = 0.0f;

    float3x3 tbn = GetTangentBasis(normal);

    [unroll]
    for(int i = 0; i < NUM_RAYS; ++i)
    {
        float2 hash = frac(randomVals.xy + float2(i * 0.15, i * 0.25));
        
        float phi = 2.0 * 3.1415926 * hash.x;
        float cosTheta = sqrt(1.0 - hash.y);
        float sinTheta = sqrt(hash.y);
        
        float3 sampleDirLocal = float3(sinTheta * cos(phi), sinTheta * sin(phi), cosTheta);
        float3 sampleDirWorld = mul(sampleDirLocal, tbn);

        if(dot(sampleDirWorld, normal) < 0) sampleDirWorld = -sampleDirWorld;

        float3 rayEndPos = worldPos + sampleDirWorld * SSGIRadius;
        float2 startUV = input.TexCoord;
        float2 endUV = ProjectToScreen(rayEndPos);
        
        float2 rayDelta = endUV - startUV;
        float2 rayStep = rayDelta / NUM_STEPS;

        float jitter = randomVals.z;
        float2 currentUV = startUV + rayStep * jitter;
        
        float3 accumLight = 0.0f;
        bool hit = false;

        [unroll]
        for(int s = 1; s <= NUM_STEPS; ++s)
        {
            currentUV += rayStep;

            if(any(currentUV < 0.0) || any(currentUV > 1.0)) break;

            float3 samplePos = GetPosition(currentUV);

            float3 toSample = samplePos - worldPos;
            float distToSample = length(toSample);

            float projDist = dot(toSample, sampleDirWorld);

            float depthDiff = distToSample - projDist;
            
            if(projDist > SSGIBias && projDist < SSGIRadius && abs(depthDiff) < 0.1f)
            {
                float3 colorSample = Sample2DLod0(ColorMap, currentUV).rgb;
				float roughness = min(length(Sample2DLod0(NormalMap, currentUV).xyz), 0.5);
                float attenuation = 1.0 - saturate(projDist / SSGIRadius);
				
                accumLight = colorSample * attenuation * (1.0 - roughness);
                hit = true;
                break;
            }
        }
        
        if(hit)
        {
            giAccum += accumLight;
            validSamples += 1.0;
        }
    }

    float3 finalGI = (giAccum / max(validSamples, 1.0)) * SSGIIntensity;

    return float4(finalGI, 1.0);
}

float4 DownsampleProcess(PS_INPUT input) : COLOR
{
	return Sample2DLod0(SSGIMap, input.TexCoord);
}

float4 BilateralProcess(PS_INPUT input) : COLOR
{
	float depth = GetPositionLength(GetPosition(input.TexCoord));
	return float4(depth, 0, 0, 1);
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
        float normalWeight = saturate(dot(centerNormal, neighborNormal));
        
        float weight = weights[i] * rangeWeight * normalWeight;
        accColor += weight * Sample2D(SSGIMap, tex).rgb;
        totalWeight += weight;
    }

    return float4(accColor / totalWeight, 1.0);
}

float4 FinalProcess(PS_INPUT input) : COLOR
{
	float fullResDepth = Sample2D(DepthMap, input.TexCoord).r;
    float2 lowResUV = input.TexCoord; 
    float2 base_uv = floor(lowResUV / LowResTexelSize - 0.5) * LowResTexelSize + 0.5 * LowResTexelSize;

    float3 totalAO = 0.0;
    float totalWeight = 1e-6;

    [unroll]
    for(int i = 0; i < 4; i++)
    {
        float2 offset = float2(i % 2, i / 2) * LowResTexelSize;
        float2 sampleUV = base_uv + offset;

        float3 aoLow = Sample2D(SSGIMap, sampleUV).rgb;
        float depthLow = Sample2D(DepthMapLow, sampleUV).r; 

        float depthDiff = abs(fullResDepth - depthLow);
        float weight = 1.0 / (0.0001 + depthDiff);

        totalAO += aoLow * weight;
        totalWeight += weight;
    }

    float3 final = totalAO / totalWeight;
    return float4(Sample2D(AlbedoMap, input.TexCoord).rgb * final, 1.0);
}

#ifdef D3D11
	technique SSGI
	{
		pass p0
		{
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 SSGIProcess();
		}
	}

	technique Downsample
	{
		pass p0
		{
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 DownsampleProcess();
		}
	}
	
	technique Bilateral
	{
		pass p0
		{
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 BilateralProcess();
		}
	}
	
	technique Blur
	{
		pass p0
		{
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 BlurProcess();
		}
	}
	
	technique Final
	{
		pass p0
		{
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 FinalProcess();
		}
	}
#else
	
	technique SSGI
	{
		pass p0
		{
			VertexShader = compile vs_3_0 VertexProcess();
			PixelShader = compile ps_3_0 SSGIProcess();
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		}
	}

	technique Downsample
	{
		pass p0
		{
			VertexShader = compile vs_3_0 VertexProcess();
			PixelShader = compile ps_3_0 DownsampleProcess();
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		}
	}
	
	technique Bilateral
	{
		pass p0
		{
			VertexShader = compile vs_3_0 VertexProcess();
			PixelShader = compile ps_3_0 BilateralProcess();
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
	
	technique Final
	{
		pass p0
		{
			VertexShader = compile vs_3_0 VertexProcess();
			PixelShader = compile ps_3_0 FinalProcess();
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		}
	}
#endif