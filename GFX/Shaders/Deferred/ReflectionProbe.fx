#include "Tools.fx"
#include "PBR.fx"

float3 EyePos			: EYE_POSITION;
float4x3 InvWorld		: MATRIX_INVWORLD;

uniform float4x4 InvViewProj;
uniform float3 ProbeColor = float3(1,1,1);
uniform float2 ProbeDelta = 0;
uniform float ProbeMip = 8.0;

static const float3 cProbeColor = SRGBToLinear(ProbeColor);

#ifdef D3D11
	texture2D tAlbedoMap : register(t0);
	sampler AlbedoMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT;  };
	
	texture2D tNormalMap : register(t1);
	sampler NormalMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT; };

	texture2D tDepthMap : register(t2);
	sampler DepthMap = sampler_state { AddressU = Clamp; AddressV = Clamp; Filter = MIN_MAG_MIP_POINT; };
	
	TextureCube tEnvMap : register(t3);
	SamplerState EnvMap = default_sampler_state;
#else
	sampler AlbedoMap : register(s0) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler NormalMap : register(s1) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };

	sampler DepthMap : register(s2) = sampler_state { AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };
	
	samplerCUBE EnvMap : register(s3);
#endif

struct PS_INPUT
{ 
	float4 Pos 				: OUT_POSITION; 
	float4 ScreenPosition 	: TEXCOORD0;
	float3 WorldPos			: TEXCOORD1;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, WorldViewProj); 
	output.ScreenPosition 	= output.Pos;
	
	output.WorldPos = mul(input.Pos, World).xyz;
	return output;
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
	
	if(dot(Normals.xyz, Normals.xyz) < 0.0001) discard;
}

// ================================================================================== REFLECTION PROBES
float4 ProcessReflectionProbe(PS_INPUT input) : OUTPUT(0)
{
	float3 diffuse, normal;
	float3 worldPos;
	float roughness, metallic;
	GetGBuffer(input.ScreenPosition, worldPos, diffuse, normal, roughness, metallic);

	float3 localPos = mul(float4(worldPos, 1.0), InvWorld).xyz;
	
	float3 fadeDistance = float3(0.12, 0.1, 0.12);
	float3 distFromEdge = 0.5 - abs(localPos); 
	float3 blendEdge = saturate(distFromEdge / fadeDistance); 

	float weight = min(min(blendEdge.x, blendEdge.y), blendEdge.z);
	weight = smoothstep(0, 1, weight);

	float3 viewDir = normalize(worldPos - EyePos);
	float3 reflection = normalize(reflect(viewDir, normal));
	reflection = BoxProject(reflection, worldPos, InvWorld, World);
	
	float3 finalReflection = reflection;
	finalReflection.x = reflection.x * ProbeDelta.y - reflection.z * ProbeDelta.x;
	finalReflection.z = reflection.x * ProbeDelta.x + reflection.z * ProbeDelta.y;

	float3 minReflectance = 0.04; 
	float3 F0 = lerp(minReflectance, max(minReflectance, diffuse), metallic);

	#ifdef D3D11
	float3 IBL = GetIBL(tEnvMap, EnvMap, finalReflection, normal, viewDir, diffuse * (1.0 - metallic), F0, roughness, cProbeColor, ProbeMip);
	#else
	float3 IBL = GetIBL(EnvMap, finalReflection, normal, viewDir, diffuse * (1.0 - metallic), F0, roughness, cProbeColor, ProbeMip);
	#endif

	return float4(IBL * weight, weight);
}
// ================================================================================== FINAL

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
#endif

technique Main
{
	pass Environment
	{
		Vertex(VertexProcess);
		Pixel(ProcessReflectionProbe);
			
		#ifdef D3D11
			SetDepthStencilState(DepthState, 0);
			SetRasterizerState(RasterState);
		#else
			CullMode = CW;
			ZWriteEnable = false;
			Lighting = false;
		#endif
	}
}