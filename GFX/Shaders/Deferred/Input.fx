//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"
#include "Transform.fx"
#include "PBR.fx"

float4 EntityColor 		: ENTITY_COLOR;
float3 AmbientColor 	: AMBIENT_COLOR;
float3 FogColor			: FOG_COLOR;
float FogNear			: FOG_NEAR;
float FogFar			: FOG_FAR;
float4 Material			: ENTITY_MATERIAL;
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
		texture2D tMaterialMap : register(t2);
		sampler MaterialMap = default_sampler_state;
	#endif

	#ifdef EMISSIVEMAP
		texture2D tEmissiveMap : register(t3);
		sampler EmissiveMap = default_sampler_state;
	#endif

	#ifdef ENVMAP
		TextureCube tEnvMap : register(t4);
		SamplerState EnvMap = default_sampler_state;
		
		TextureCube tBlendEnvMap;
		SamplerState BlendEnvMap = default_sampler_state;
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
		sampler MaterialMap : register(s2);
	#endif

	#ifdef EMISSIVEMAP
		sampler EmissiveMap : register(s3);
	#endif

	#ifdef ENVMAP
		samplerCUBE EnvMap : register(s4);
		
		textureCUBE tBlendEnvMap;
		samplerCUBE BlendEnvMap = sampler_state {Texture = <tBlendEnvMap>;};
	#endif
	
	#ifdef HEIGHTMAP
		sampler HeightMap : register(s5);
	#endif
#endif

#ifdef MUL
uniform float EmissiveMultiply;
#endif

#ifdef ENVMAP
uniform float EnvBlendFactor = 1.0f;
#endif
	
uniform float DepthMultiply;

// ==================================================== GBUFFER

struct VS_INPUT_GBUFFER
{ 
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	
	#if defined(NORMALMAP) || defined(HEIGHTMAP) || defined(FAKECURVE)
		float3 Tangent : TEXCOORD2;
		float3 Binormal : TEXCOORD3;
	#endif
	
	float4 BlendWeights : BLENDWEIGHT;
	uint4 BlendIndices : BLENDINDICES;
	float4 VertexColor : COLOR0;

	float4 IM1 : TEXCOORD4;
	float4 IM2 	: TEXCOORD5;
	float4 IM3 	: TEXCOORD6;
	float4 Color : COLOR1;
};

struct VS_OUTPUT_DEFERRED
{
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	float3 WorldPos : TEXCOORD1;
	float2 Depth : TEXCOORD2;
	#if defined(NORMALMAP) || defined(HEIGHTMAP) || defined(FAKECURVE)
		float3 Tangent : TEXCOORD3;
		float3 Binormal : TEXCOORD4;
	#endif
	float4 Color : COLOR;
};

struct DeferredOutput
{
	float4 Color 	: COLOR0;
	#ifndef TRANSPARENT
		float4 Albedo	: COLOR1;
		float4 Normal 	: COLOR2;
		float4 Depth 	: COLOR3;
	#endif
};

// ===================================================================================== TECHNIQUES

inline void GetVertexData(in VS_INPUT_GBUFFER input, inout VS_OUTPUT_DEFERRED output, float4x3 WorldTransform)
{
	output.WorldPos = mul(input.Pos, WorldTransform);
	output.Pos = mul(float4(output.WorldPos, 1), ViewProj);
	
	output.TexCoords = mul(float3(input.TexCoords, 1.0), TextureMatrix);

	output.Normal = normalize(mul(input.Normal, WorldTransform));
	#if defined(NORMALMAP) || defined(HEIGHTMAP) || defined(FAKECURVE)
		output.Tangent = normalize(mul(input.Tangent, WorldTransform));
		output.Binormal = normalize(mul(input.Binormal, WorldTransform));
	#endif

	output.Depth = output.Pos.zw;
}

VS_OUTPUT_DEFERRED VS_Base(VS_INPUT_GBUFFER input)
{
	VS_OUTPUT_DEFERRED output;
	output.Color = input.VertexColor * EntityColor;
	GetVertexData(input, output, World);
	return output;
}

VS_OUTPUT_DEFERRED VS_Instanced(VS_INPUT_GBUFFER input)
{
	VS_OUTPUT_DEFERRED output;
	output.Color = input.VertexColor * input.Color * EntityColor;
	GetVertexData(input, output, GetInstanceTransform(input.IM1, input.IM2, input.IM3));
	return output;
}

VS_OUTPUT_DEFERRED VS_Skinned(VS_INPUT_GBUFFER input)
{ 
	VS_OUTPUT_DEFERRED output;
	output.Color = input.VertexColor * EntityColor;
	GetVertexData(input, output, GetSkinTransform(input.BlendIndices, input.BlendWeights));
	return output;
}

inline void GetMaterial(in VS_OUTPUT_DEFERRED input, out float4 color, out float4 diffuse, out float3 normal, out float2 material, out float fogFactor)
{
	float2 texCoords = input.TexCoords;

	float2 dx = ddx(texCoords);
	float2 dy = ddy(texCoords);

	#ifdef HEIGHTMAP
		float3x3 TBN = float3x3(input.Tangent, input.Binormal, input.Normal);
		float3 viewDirP = (EyePos - input.WorldPos);
		float VdotN = dot(normalize(viewDirP), input.Normal);
        float3 viewDirM = mul(TBN, viewDirP);
		
		#ifdef D3D11
			texCoords = ParallaxOcclusionMapping(tHeightMap, HeightMap, texCoords, viewDirM, VdotN);
		#else
			texCoords = ParallaxOcclusionMapping(HeightMap, texCoords, viewDirM, VdotN);
		#endif
	#endif
	
	#ifdef ROUGHMAP
		float4 MaterialData = Sample2DGrad(MaterialMap, texCoords, dx, dy);
	#endif
	
	diffuse = Sample2DGrad(DiffuseMap, texCoords, dx, dy);
	#ifdef D3D11 // D3D9 has auto alpha test
		#ifdef MASKED
			clip(diffuse.a - 0.5f);
		#endif
	#endif
	diffuse *= input.Color;

	#ifdef NORMALMAP
		float3 bump = Sample2DGrad(NormalMap, texCoords, dx, dy).rgb * 2.0 - 1.0;
		normal = normalize((bump.x * input.Tangent) + (bump.y * input.Binormal) + (bump.z * input.Normal));
	#else
		normal = normalize(input.Normal);
	#endif

	#ifdef ROUGHMAP
		material.x = MaterialData.r + Material.b;
		material.y = MaterialData.g + Material.a;
	#else
		material.x = Material.b;
		material.y = Material.a;
	#endif

	material.x *= material.x;
	material.x = clamp(material.x, 0.004, 1.0);
	material.y = clamp(material.y, 0.03, 0.95);
	
	float3 F0 = lerp(0.07, diffuse.rgb, material.y);
	
	diffuse.rgb *= 1.0 - material.y;

	#ifndef FULLBRIGHT
		float3 ambient = AmbientColor;
		#ifdef TRANSPARENT
			ambient *= 2.f;
		#endif
	#else
		float3 ambient = float3(1,1,1);
	#endif
		
	#if defined(EMISSIVEMAP)
		float3 emissive = Sample2DGrad(EmissiveMap, texCoords, dx, dy).rgb;
		
		#ifdef MUL
			emissive *= EmissiveMultiply;
		#endif

		color = float4(diffuse.rgb * ambient, diffuse.a) + float4(emissive, 0.0);

		#ifdef DISABLEFOG
			fogFactor = 0.0f;
		#else
			float near = lerp(FogNear, FogFar, GetIntensity(emissive) * 0.95);
			fogFactor = saturate((distance(EyePos, input.WorldPos) - near) / (FogFar - near));
		#endif
	#else
		color = float4(diffuse.rgb * ambient, diffuse.a);
		
		#ifdef MUL
			color.rgb *= EmissiveMultiply;
		#endif
		
		#ifdef DISABLEFOG
			fogFactor = 0.0f;
		#else
			fogFactor = saturate((distance(EyePos, input.WorldPos) - FogNear) / (FogFar - FogNear));
		#endif
	#endif
	
	#ifdef ENVMAP
		float3 viewDir = normalize(input.WorldPos - EyePos);
		float3 reflectN;

		#ifdef FAKECURVE // Fix for flat surfaces
			#ifndef NORMALMAP
				float3 reflectBump = float3(0, 0, 1);
			#else
				float3 reflectBump = bump;
			#endif
			float2 fakeCurve = (texCoords - 0.5) * 2.0; 
			reflectBump.xy += fakeCurve * 0.3;
			reflectBump = normalize(reflectBump);
			reflectN = normalize((reflectBump.x * input.Tangent) + (reflectBump.y * input.Binormal) + (reflectBump.z * input.Normal));
		#else
			reflectN = normal;
		#endif
		
		float3 r = normalize(reflect(viewDir, reflectN));

		#ifdef D3D11
			float3 IBL = GetIBL(tEnvMap, EnvMap, tBlendEnvMap, BlendEnvMap, EnvBlendFactor, r, reflectN, viewDir, diffuse.rgb, F0, material.x, ambient);
		#else
			float3 IBL = GetIBL(EnvMap, BlendEnvMap, EnvBlendFactor, r, reflectN, viewDir, diffuse.rgb, F0, material.x, ambient);
		#endif
		
		color.rgb += IBL;
	#endif
}

DeferredOutput PS_Deferred(VS_OUTPUT_DEFERRED input)
{
	DeferredOutput output;
	float4 diffuse;
	float fogFactor;
	float3 normal;
	float2 material;
	GetMaterial(input, output.Color, diffuse, normal, material, fogFactor);
	
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
		output.Albedo = float4(diffuse.rgb, fogFactor);
		output.Normal = float4(normalize(normal) * material.x, material.y);
		output.Depth = float4(input.Depth.x / input.Depth.y, 1, 1, 1);
	#endif
	
	return output;
}

technique Deferred
{
	pass Input
	{
		Vertex(VS_Base);
		Pixel(PS_Deferred);
		
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}

technique Deferred::Instanced
{
	pass Input
	{
		Vertex(VS_Instanced);
		Pixel(PS_Deferred);
		
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}

technique Deferred::Skinned
{
	pass Input
	{
		Vertex(VS_Skinned);
		Pixel(PS_Deferred);
		
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}

// =====================================================================================

float4 PS_DepthHack(VS_OUTPUT_DEFERRED input) : COLOR
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
		Vertex(VS_Base);
		Pixel(PS_DepthHack);
		
		#ifndef D3D11
			Lighting = false;
			ZFunc = Always;
		#endif
	}
}

technique DepthHack::Skinned
{
	pass DPass
	{
		Vertex(VS_Skinned);
		Pixel(PS_DepthHack);
		
		#ifndef D3D11
			Lighting = false;
			ZFunc = Always;
		#endif
	}
}

technique DepthHack::Instanced
{
	pass DPass
	{
		Vertex(VS_Instanced);
		Pixel(PS_DepthHack);
		
		#ifndef D3D11
			Lighting = false;
			ZFunc = Always;
		#endif
	}
}

// ===================================================================================== Shadow Map Depth

struct VS_INPUT_DEPTH
{ 
	float4 Pos : POSITION;
	#ifdef MASKED
		float2 TexCoord  : TEXCOORD0;
	#endif
	float4 BlendWeights : BLENDWEIGHT;
	uint4 BlendIndices : BLENDINDICES;

	float4 IM1 : TEXCOORD4;
	float4 IM2 	: TEXCOORD5;
	float4 IM3 	: TEXCOORD6;
};

struct VS_OUTPUT_DEPTH
{
	float4 Pos : POSITION;
	#ifdef D3D11
		#ifdef MASKED
			float2 TexCoord : TEXCOORD0;
		#endif
	#endif
};

void GetVertexData_Depth(in VS_INPUT_DEPTH input, inout VS_OUTPUT_DEPTH output, float4x3 WorldTransform)
{
	output.Pos = mul(float4(mul(input.Pos, WorldTransform), 1), ViewProj);
	
	#ifdef D3D11
		#ifdef MASKED
			output.TexCoord = mul(input.TexCoord, TextureMatrix);
		#endif
	#endif
}

VS_OUTPUT_DEPTH VS_DepthBase(VS_INPUT_DEPTH input)
{
	VS_OUTPUT_DEPTH output;
	GetVertexData_Depth(input, output, World);
	return output;
}

VS_OUTPUT_DEPTH VS_DepthInstanced(VS_INPUT_DEPTH input)
{
	VS_OUTPUT_DEPTH output;
	GetVertexData_Depth(input, output, GetInstanceTransform(input.IM1, input.IM2, input.IM3));
	return output;
}

VS_OUTPUT_DEPTH VS_DepthSkinned(VS_INPUT_DEPTH input)
{
	VS_OUTPUT_DEPTH output;
	GetVertexData_Depth(input, output, GetSkinTransform(input.BlendIndices, input.BlendWeights));
	return output;
}

float4 PS_Depth(VS_OUTPUT_DEPTH input) : COLOR
{
	#ifdef D3D11 // D3D9 has auto alpha test
		#ifdef MASKED
			clip(Sample2D(DiffuseMap, input.TexCoord).a - 0.5f);
		#endif
	#endif
	
	return 0;
}

technique ShadowMap
{
	pass FirstPass
	{
		Vertex(VS_DepthBase);
		Pixel(PS_Depth);
			
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}

technique ShadowMap::Instanced
{
	pass FirstPass
	{
		Vertex(VS_DepthInstanced);
		Pixel(PS_Depth);
		
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}

technique ShadowMap::Skinned
{
	pass FirstPass
	{
		Vertex(VS_DepthSkinned);
		Pixel(PS_Depth);
		
		#ifndef D3D11
			Lighting = false;
		#endif
	}
}