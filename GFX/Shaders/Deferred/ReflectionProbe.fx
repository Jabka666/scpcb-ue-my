#include "Tools.fx"
#include "PBR.fx"

float3 EyePos			: EYE_POSITION;
float4x3 InvWorld		: MATRIX_INVWORLD;

uniform float4x4 InvViewProj;
uniform float3 ProbeColor = float3(1,1,1);
uniform float2 ProbeDelta = 0;
uniform float ProbeMip = 8.0;
uniform float ProbeBlend = 1.0;

static const float3 cProbeColor = SRGBToLinear(ProbeColor);

DeclareSampler(AlbedoMap, 0, BLITZ_FILTER_POINT, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(NormalMap, 1, BLITZ_FILTER_POINT, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(DepthMap, 2, BLITZ_FILTER_POINT, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareCubeSampler(EnvMap, 3, BLITZ_FILTER_ANISOTROPY, BLITZ_ADDR_WRAP, BLITZ_ADDR_WRAP, -0.4, Anisotropy);
DeclareCubeSampler(PrevEnvMap, 4, BLITZ_FILTER_ANISOTROPY, BLITZ_ADDR_WRAP, BLITZ_ADDR_WRAP, -0.4, Anisotropy);

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

	float3 IBL;
	#ifdef D3D11
	IBL = GetBlendedIBL(tEnvMap, EnvMap, tPrevEnvMap, PrevEnvMap, ProbeBlend, finalReflection, normal, viewDir, diffuse * (1.0 - metallic), F0, roughness, cProbeColor, ProbeMip);
	#else
	IBL = GetBlendedIBL(EnvMap, PrevEnvMap, ProbeBlend, finalReflection, normal, viewDir, diffuse * (1.0 - metallic), F0, roughness, cProbeColor, ProbeMip);
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