//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"
#include "Transform.fx"

float4 EntityColor 		: ENTITY_COLOR;
float3 AmbientColor 	: AMBIENT_COLOR;
float3 FogColor			: FOG_COLOR;
float FogNear			: FOG_NEAR;
float FogFar			: FOG_FAR;
float2 Specular			: ENTITY_SPECULAR;
float3 EyePos			: EYE_POSITION;

float3x2 TextureMatrix : MATRIX_TEXTURE0;

#ifdef D3D11
	texture2D tDiffuseMap : register(t0);
	sampler DiffuseMap = default_sampler_state;
		
	#ifdef NORMALMAP
		texture2D tNormalMap : register(t1);
		sampler NormalMap = default_sampler_state;
	#endif

	#ifdef ROUGHMAP
		texture2D tRoughnessMap : register(t2);
		sampler RoughnessMap = default_sampler_state;
	#endif

	#ifdef EMISSIVEMAP
		texture2D tEmissiveMap : register(t3);
		sampler EmissiveMap = default_sampler_state;
	#endif

	#if defined(SPHEREMAP) || defined(REFLECTIONMAP)
		texture2D tEnvMap : register(t4);
		sampler EnvMap = default_sampler_state;
	#endif

	#ifdef HEIGHTMAP
		texture2D tHeightMap : register(t5);
		sampler HeightMap = default_sampler_state;
	#endif
#else
	sampler DiffuseMap : register(s0);

	#ifdef NORMALMAP
		sampler NormalMap : register(s1);
	#endif

	#ifdef ROUGHMAP
		sampler RoughnessMap : register(s2);
	#endif

	#ifdef EMISSIVEMAP
		sampler EmissiveMap : register(s3);
	#endif

	#if defined(SPHEREMAP) || defined(REFLECTIONMAP)
		sampler EnvMap : register(s4);
	#endif

	#ifdef HEIGHTMAP
		sampler HeightMap : register(s5);
	#endif
#endif
#ifdef MUL
	uniform float EmissiveMultiply;
#endif
	
uniform float DepthMultiply;

// ==================================================== GBUFFER

struct VS_INPUT_GBUFFER
{ 
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	#if defined(NORMALMAP) || defined(HEIGHTMAP)
		float3 Tangent : TEXCOORD2;
		float3 Binormal : TEXCOORD3;
	#endif
	float4 BlendWeights : BLENDWEIGHT;
	uint4 BlendIndices : BLENDINDICES;
	float4 VertexColor : COLOR0;
	#ifdef INSTANTIATED
		float4 IM1 : TEXCOORD4;
		float4 IM2 	: TEXCOORD5;
		float4 IM3 	: TEXCOORD6;
		float4 Color : COLOR1;
	#endif
};

struct PS_INPUT_GBUFFER
{
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	float3 WorldPos : TEXCOORD1;
	float2 Depth : TEXCOORD2;
	float4 ScreenPos : TEXCOORD3;
	#if defined(NORMALMAP) || defined(HEIGHTMAP)
		float3 Tangent : TEXCOORD4;
		float3 Binormal : TEXCOORD5;
	#endif

	float4 Color : COLOR;
};

struct PixelOutput
{
	float4 Color 	: COLOR0;
	#ifndef TRANSPARENT
		float4 Albedo	: COLOR1;
		float4 Normal 	: COLOR2;
		float4 Depth 	: COLOR3;
	#endif
};

PS_INPUT_GBUFFER GBufferVertex(VS_INPUT_GBUFFER input)
{ 
	PS_INPUT_GBUFFER output;
	
	#ifndef INSTANTIATED
		const float4x3 WorldTransform = GetWorldTransform(input.BlendIndices, input.BlendWeights);
		output.Color = input.VertexColor * EntityColor;
	#else
		const float4x3 WorldTransform = GetInstanceTransform(input.IM1, input.IM2, input.IM3);
		output.Color = input.VertexColor * input.Color * EntityColor;
	#endif
	
	output.WorldPos = mul(input.Pos, WorldTransform);
	output.Pos = mul(float4(mul(input.Pos, WorldTransform), 1), ViewProj);
	
	output.TexCoords = mul(input.TexCoords, TextureMatrix);

	output.Normal = normalize(mul(input.Normal, WorldTransform));
    #if defined(NORMALMAP) || defined(HEIGHTMAP)
		output.Tangent = normalize(mul(input.Tangent, WorldTransform));
		output.Binormal = normalize(mul(input.Binormal, WorldTransform));
	#endif

	output.Depth = output.Pos.zw;
	output.ScreenPos = output.Pos;
	return output; 
}

PixelOutput GBufferPixel(PS_INPUT_GBUFFER input)
{
	PixelOutput output;
	
	float2 texCoords = input.TexCoords;

	float2 dx = ddx(texCoords);
	float2 dy = ddy(texCoords);

	#ifdef HEIGHTMAP
		const float3x3 TBN = float3x3(input.Tangent, input.Binormal, input.Normal);
		const float3 viewDirP = (EyePos - input.WorldPos);
		const float VdotN = dot(normalize(viewDirP), input.Normal);
        const float3 viewDirM = mul(TBN, viewDirP);
		
		#ifdef D3D11
			texCoords = ParallaxOcclusionMapping(tHeightMap, HeightMap, texCoords, viewDirM, VdotN);
		#else
			texCoords = ParallaxOcclusionMapping(HeightMap, texCoords, viewDirM, VdotN);
		#endif
	#endif
	
	float4 diffuse = Sample2DGrad(DiffuseMap, texCoords, dx, dy) * input.Color;
	#ifdef D3D11 // D3D9 has auto alpha test
		#ifdef MASKED
			clip(diffuse.a - 0.5f);
		#endif
	#endif
	
	#ifndef TRANSPARENT

		#ifdef NORMALMAP
			const float3 bump = Sample2DGrad(NormalMap, texCoords, dx, dy).rgb * 2.0 - 1.0;
			const float3 normal = normalize((bump.x * input.Tangent) + (bump.y * input.Binormal) + (bump.z * input.Normal));
		#else
			const float3 normal = input.Normal;
		#endif
	#else
		const float3 normal = input.Normal;
	#endif
	
	#ifdef ROUGHMAP
		const float roughness = Sample2DGrad(RoughnessMap, texCoords, dx, dy).r * 2.0;
		const float specIntensity = lerp(Specular.r, 0.0, roughness);
		const float specPower = Specular.g;
	#else
		const float specIntensity = Specular.r;
		const float specPower = Specular.g;
	#endif
	
	#ifndef FULLBRIGHT
		const float3 ambient = AmbientColor;
	#else
		const float3 ambient = float3(1,1,1);
	#endif
	
	#if defined(SPHEREMAP) || defined(REFLECTIONMAP)
		const float3 viewDir = normalize(EyePos - input.WorldPos);
		float3 r = reflect(viewDir, normalize(normal));
		
		#ifdef SPHEREMAP
			r.xy = r.xy * (r.z + 1.0f) / 2.0f;
			r.xy = (r.xy + 1.0f) / 2.0f;
		#else
			r.xy = float2(r.x * 0.5 + 0.5, r.y * 0.5 + 0.5);
		#endif
		
		#ifdef ENVMAPADD
			diffuse.rgb += Sample2DGrad(EnvMap, r.xy, dx, dy).rgb * saturate(specIntensity);
		#else
			diffuse.rgb = lerp(diffuse.rgb, Sample2DGrad(EnvMap, r.xy, dx, dy).rgb, saturate(specIntensity));
		#endif
	#endif
	
	#if defined(EMISSIVEMAP)
		#ifndef MUL
			const float3 emissive = Sample2DGrad(EmissiveMap, texCoords, dx, dy).rgb;
		#else
			const float3 emissive = Sample2DGrad(EmissiveMap, texCoords, dx, dy).rgb * EmissiveMultiply;
		#endif
		
		output.Color = float4(diffuse.rgb * ambient, diffuse.a) + float4(emissive, 0.0);
		
		#ifdef DISABLEFOG
			const float fogFactor = 0.0f;
		#else
			const float fogFactor = saturate((distance(EyePos, input.WorldPos) - lerp(FogNear, FogFar, GetIntensity(emissive) * 0.8)) / FogFar);
		#endif
	#else
		#ifndef MUL
			output.Color = float4(diffuse.rgb * ambient, diffuse.a);
		#else
			output.Color = float4(diffuse.rgb * ambient * EmissiveMultiply, diffuse.a);
		#endif
		
		#ifdef DISABLEFOG
			const float fogFactor = 0.0f;
		#else
			const float fogFactor = saturate((distance(EyePos, input.WorldPos) - FogNear) / FogFar);
		#endif
	#endif
	
	#if defined(TRANSPARENT)
		output.Color.rgb = lerp(output.Color.rgb, FogColor, fogFactor);
	#elif defined(SKYBOX)
		output.Albedo = float4(diffuse.rgb, 0);
		output.Normal = 0;
		#ifdef REVERSEDZ
			output.Depth = float4(0, 1, 1, 1);
		#else
			output.Depth = float4(1, 1, 1, 1);
		#endif
	#else
		output.Albedo = (1.0f - fogFactor) * float4(diffuse.rgb, specIntensity);
		output.Normal = float4(normal * 0.5 + 0.5, specPower / 32.0);
		output.Depth = float4(input.Depth.x / input.Depth.y, 1, 1, 1);
		output.Color.rgb = lerp(output.Color.rgb, FogColor, fogFactor);
	#endif

	return output;
}

float4 GBufferDepth(PS_INPUT_GBUFFER input) : COLOR
{
	#ifdef REVERSEDZ
		return float4(input.Depth.x / input.Depth.y / DepthMultiply, 1, 1, 1);
	#else
		return float4(input.Depth.x / input.Depth.y * DepthMultiply, 1, 1, 1);
	#endif
}

technique DepthHack
{
	pass DPass
	{
		#ifdef D3D11
			VertexShader = compile vs_5_0 GBufferVertex();
			PixelShader = compile ps_5_0 GBufferDepth();
		#else
			VertexShader = compile vs_3_0 GBufferVertex();
			PixelShader = compile ps_3_0 GBufferDepth();
			Lighting = false;
			ZFunc = Always;
		#endif
	}
}

technique GBuffer
{
	pass Input
	{
		#ifdef D3D11
			VertexShader = compile vs_5_0 GBufferVertex();
			PixelShader = compile ps_5_0 GBufferPixel();
		#else
			VertexShader = compile vs_3_0 GBufferVertex();
			PixelShader = compile ps_3_0 GBufferPixel();
			Lighting = false;
		#endif
	}
}

// ==================================================== Shadow Map Depth

struct VS_INPUT_DEPTH
{ 
	float4 Pos : POSITION;
	float2 TexCoord  : TEXCOORD0;
	float4 BlendWeights : BLENDWEIGHT;
	uint4 BlendIndices : BLENDINDICES;
	#ifdef INSTANTIATED
		float4 IM1 : TEXCOORD4;
		float4 IM2 	: TEXCOORD5;
		float4 IM3 	: TEXCOORD6;
	#endif
};

float4 DepthVertex(VS_INPUT_DEPTH input, out float2 texcoord : TEXCOORD0) : POSITION
{
	#ifndef INSTANTIATED
		const float4x3 WorldTransform = GetWorldTransform(input.BlendIndices, input.BlendWeights);
	#else
		const float4x3 WorldTransform = GetInstanceTransform(input.IM1, input.IM2, input.IM3);
	#endif
	
	texcoord = input.TexCoord;
	return mul(float4(mul(input.Pos, WorldTransform), 1), ViewProj);
}

float4 DepthPixel(float4 Pos : POSITION, float2 TexCoord : TEXCOORD0) : COLOR
{
	return Sample2D(DiffuseMap, TexCoord);
}

technique ShadowMap
{
	pass FirstPass
	{
		#ifdef D3D11
			VertexShader = compile vs_5_0 DepthVertex();
			PixelShader = compile ps_5_0 DepthPixel();
		#else
			VertexShader = compile vs_3_0 DepthVertex();
			PixelShader = compile ps_3_0 DepthPixel();
			Lighting = false;
		#endif
	}
}