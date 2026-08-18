//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"
#include "..\Deferred\PBR.fx"

uniform float4x4 InvViewProj;
uniform float4x4 CameraViewProj;
uniform float3 CameraPosition;
uniform float FarClip;
uniform float SSRMaxDistance = 40.0;
uniform float SSRStep = 0.1;
uniform float SSRBias = 0.02;
uniform float SSRStrength = 1.0;

static const int SSRMaxSteps = 64;

#ifdef D3D11
	texture2D tColorMap : register(t0);
	texture2D tDepthMap : register(t1);
	texture2D tNormalMap : register(t2);
	texture2D tLinearDepthMap : register(t3);

	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	sampler LinearDepthMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s0) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler DepthMap : register(s1) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler NormalMap : register(s2) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
	sampler LinearDepthMap : register(s3) = sampler_state { MinFilter = None; MagFilter = None; MipFilter = None; AddressU = Clamp; AddressV = Clamp; };
#endif

struct PS_INPUT
{
	float4 Pos : OUT_POSITION;
	float2 TexCoord : TEXCOORD0;
};

PS_INPUT VertexProcess(VS_INPUT input)
{
	PS_INPUT output;
	output.Pos = mul(input.Pos, ViewProj);
	output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
	return output;
}

float4 SSRProcess(PS_INPUT input) : OUTPUT(0)
{
	const float2 uv = input.TexCoord;

	float centerDepth = Sample2DLod0(LinearDepthMap, uv).r;
	if (centerDepth > FarClip * 0.98) return 0.0;

	float4 normalRaw = Sample2DLod0(NormalMap, uv);
	float roughness = length(normalRaw.xyz);

	float roughFade = 1.0 - smoothstep(0.2, 0.8, roughness);
	if (roughFade <= 0.001) return 0.0;

	float3 worldPos = GetWorldPosition(uv, Sample2DLod0(DepthMap, uv).r, InvViewProj);
	float metallic = normalRaw.a;
	float3 normal = normalize(normalRaw.xyz);
	
	float3 viewDelta = worldPos - CameraPosition;
	float3 viewDir = normalize(viewDelta);
	float3 R = reflect(viewDir, normal);

	float initialDepth = length(viewDelta);
	float localSSRStep = clamp(initialDepth * 0.02, 0.005, SSRStep);
	float t = localSSRStep;
	
	float ndv = saturate(dot(-viewDir, normal)) + 1e-5f;

	float3 minReflectance = 0.04;
	float3 sceneColor = Sample2DLod0(ColorMap, uv).rgb;
	float3 F0 = lerp(minReflectance, max(minReflectance, sceneColor), metallic);

	float3 environmentSpecular = EnvBRDFApprox(F0, roughness, ndv);

	float2 hitUV = uv;
	float hitFound = 0.0;
	float hitT = 0.0;

	float4 startClip = mul(float4(worldPos, 1.0), CameraViewProj);
	float4 rayClip = mul(float4(R, 0.0), CameraViewProj);

	[loop]for (int i = 0; i < SSRMaxSteps; ++i)
	{
		if (t > SSRMaxDistance) break;

		float4 clip = startClip + rayClip * t;
		float2 puv = GetScreenTexCoords(clip);

		if (puv.x < 0.0 || puv.x > 1.0 || puv.y < 0.0 || puv.y > 1.0) break;

		float surfaceDepth = Sample2DLod0(LinearDepthMap, puv).r;
		float currentStep = max(localSSRStep, t * 0.06);

		if (surfaceDepth > FarClip * 0.98) 
		{
			t += currentStep;
			continue;
		}

		float rayDepth = length(viewDelta + R * t);
		float thickness = max(0.1, currentStep * 2.0);

		if (rayDepth >= surfaceDepth + SSRBias && rayDepth < surfaceDepth + thickness)
		{
			float3 hitNormalRaw = Sample2DLod0(NormalMap, puv).xyz;
			float3 hitNormal = normalize(hitNormalRaw);

			if (dot(R, hitNormal) < -0.1)
			{
				float t0 = t - currentStep;
				float t1 = t;

				[unroll]for (int j = 0; j < 4; ++j)
				{
					float tMid = (t0 + t1) * 0.5;
					
					float4 clipMid = startClip + rayClip * tMid;
					float2 puvMid = GetScreenTexCoords(clipMid);
					
					float depthMid = Sample2DLod0(LinearDepthMap, puvMid).r;
					float rayDepthMid = length(viewDelta + R * tMid);
					
					if (rayDepthMid >= depthMid + SSRBias)
						t1 = tMid;
					else
						t0 = tMid;
				}

				t = t1;
				float4 clipFinal = startClip + rayClip * t;
				
				hitUV = GetScreenTexCoords(clipFinal);
				hitFound = 1.0;
				hitT = t;
				break;
			}
		}

		t += currentStep;
	}

	float distFade = 1.0 - smoothstep(SSRMaxDistance * 0.5, SSRMaxDistance, hitT);
	float edgeFade = smoothstep(0.0, 0.15, min(min(hitUV.x, 1.0 - hitUV.x), min(hitUV.y, 1.0 - hitUV.y)));

	float fade = distFade * edgeFade * roughFade;
	float3 reflectedColor = Sample2DLod0(ColorMap, hitUV).rgb;
	float3 finalColor = reflectedColor * environmentSpecular * SSRStrength * fade * hitFound;

	return float4(finalColor, 1.0);
}

technique SSR
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(SSRProcess);

		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}