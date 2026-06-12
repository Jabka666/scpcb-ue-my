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

float3 cFogColor		: FOG_COLOR;
float4 cEntityColor 	: ENTITY_COLOR;
float3 cAmbientColor 	: AMBIENT_COLOR;
float2 FogPlane			: FOG_PLANE;
float2 ClipPlane		: CLIP_PLANE;
float4 Material			: ENTITY_MATERIAL;
float3 EyePos			: EYE_POSITION;

static const float4 EntityColor = float4(SRGBToLinear(cEntityColor.rgb), cEntityColor.a);
static const float3 FogColor = SRGBToLinear(cFogColor);
static const float3 AmbientColor = SRGBToLinear(cAmbientColor);

float3x2 TextureMatrix : MATRIX_TEXTURE0;

#ifdef LOCALTRANSFORM
float4x4 View			: MATRIX_VIEW;
float4x4 Proj			: MATRIX_PROJECTION;
#endif

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
	
	#ifdef HEIGHTMAP
		texture2D tHeightMap : register(t5);
		sampler HeightMap = default_sampler_state;
	#endif
	
	#ifdef TRANSPARENT
		Texture2D tMRTDepth;
		SamplerState MRTDepth = sampler_state{Filter=MIN_MAG_MIP_POINT;AddressU = Clamp;AddressV = Clamp;};
		
		Texture2D tLighting;
		SamplerState Lighting = sampler_state{Filter=MIN_MAG_MIP_LINEAR;AddressU = Clamp;AddressV = Clamp;};
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
	
	#ifdef HEIGHTMAP
		sampler HeightMap : register(s5);
	#endif
	
	#ifdef TRANSPARENT
		texture tMRTDepth;
		sampler MRTDepth = sampler_state { Texture = <tMRTDepth>; AddressU = Clamp; AddressV = Clamp; MinFilter = None; MagFilter = None; MipFilter = None; };
		
		texture tLighting;
		sampler Lighting = sampler_state { Texture = <tLighting>; AddressU = Clamp; AddressV = Clamp; MinFilter = Linear; MagFilter = Linear; MipFilter = Linear; };
	#endif
#endif

#ifdef FORWARD
uniform float3 LightDirection;
uniform float3 LightColor;
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

	float4 IM1 : TEXCOORD4;
	float4 IM2 	: TEXCOORD5;
	float4 IM3 	: TEXCOORD6;
	float4 Color : COLOR1;
};

struct VS_OUTPUT_DEFERRED
{
	float4 Pos : OUT_POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	float3 WorldPos : TEXCOORD1;
	float2 Depth : TEXCOORD2;
	#if defined(NORMALMAP) || defined(HEIGHTMAP)
		float3 Tangent : TEXCOORD3;
		float3 Binormal : TEXCOORD4;
	#endif
	float4 Color : COLOR0;
	
	#ifdef EMISSIVECOLOR
	float4 EmissiveColor : COLOR1;
	#endif
	
	float4 ScreenPosition : TEXCOORD5;
};

struct DeferredOutput
{
	float4 Color 	: OUTPUT(0);
	#if !defined(TRANSPARENT) && !defined(FORWARD)
		float4 Albedo	: OUTPUT(1);
		float4 Normal 	: OUTPUT(2);
		float4 Depth 	: OUTPUT(3);
	#endif
};

// ===================================================================================== TECHNIQUES

inline void GetVertexData(in VS_INPUT_GBUFFER input, inout VS_OUTPUT_DEFERRED output, float4x3 WorldTransform)
{
	#ifdef LOCALTRANSFORM
        float3 localPos = mul(input.Pos, WorldTransform);
        output.WorldPos = localPos + EyePos; 
        float4x4 matViewStatic = View;
        matViewStatic[3][0] = 0;
        matViewStatic[3][1] = 0;
        matViewStatic[3][2] = 0;
        output.Pos = mul(float4(localPos, 1), mul(matViewStatic, Proj));
	#else
        output.WorldPos = mul(input.Pos, WorldTransform);
        output.Pos = mul(float4(output.WorldPos, 1), ViewProj);
    #endif
	
	output.TexCoords = mul(float3(input.TexCoords, 1.0), TextureMatrix);

	output.Normal = normalize(mul(input.Normal, WorldTransform));
	#if defined(NORMALMAP) || defined(HEIGHTMAP)
		output.Tangent = normalize(mul(input.Tangent, WorldTransform));
		output.Binormal = normalize(mul(input.Binormal, WorldTransform));
	#endif

	output.ScreenPosition = output.Pos;
	output.Depth = output.Pos.zw;
}

VS_OUTPUT_DEFERRED VS_Base(VS_INPUT_GBUFFER input)
{
	VS_OUTPUT_DEFERRED output;
	
	#ifndef EMISSIVECOLOR
		output.Color = input.VertexColor * EntityColor;
	#else
		output.Color = input.VertexColor;
		output.EmissiveColor = EntityColor;
	#endif
	
	GetVertexData(input, output, World);
	return output;
}

VS_OUTPUT_DEFERRED VS_Instanced(VS_INPUT_GBUFFER input)
{
	VS_OUTPUT_DEFERRED output;
	#ifndef EMISSIVECOLOR
		output.Color = input.VertexColor * input.Color;
	#else
		output.Color = input.VertexColor;
		output.EmissiveColor = input.Color;
	#endif
	
	GetVertexData(input, output, GetInstanceTransform(input.IM1, input.IM2, input.IM3));
	return output;
}

VS_OUTPUT_DEFERRED VS_Skinned(VS_INPUT_GBUFFER input)
{ 
	VS_OUTPUT_DEFERRED output;
	
	#ifndef EMISSIVECOLOR
		output.Color = input.VertexColor * EntityColor;
	#else
		output.Color = input.VertexColor;
		output.EmissiveColor = EntityColor;
	#endif
	
	GetVertexData(input, output, GetSkinTransform(input.BlendIndices, input.BlendWeights));
	return output;
}

inline void GetMaterial(in VS_OUTPUT_DEFERRED input, out float4 color, out float4 diffuse, out float3 normal, out float2 material, out float fogFactor)
{
	float2 texCoords = input.TexCoords;
	#ifdef HEIGHTMAP
		float2 dx = ddx(texCoords);
		float2 dy = ddy(texCoords);
		
		#define SampleTexture(tex, uv) Sample2DGrad(tex, uv, dx, dy)

		float3 N = normalize(input.Normal);
		float3 T = normalize(input.Tangent);
		float3 B = normalize(input.Binormal);

		T = normalize(T - dot(T, N) * N);
		B = normalize(B - dot(B, N) * N - dot(B, T) * T);
		
		float3x3 TBN = float3x3(T, B, N);

		float3 viewDirP = normalize(EyePos - input.WorldPos);
		float VdotN = dot(viewDirP, N);
		float3 viewDirM = mul(TBN, viewDirP);

		#ifdef D3D11
			texCoords = ParallaxOcclusionMapping(tHeightMap, HeightMap, texCoords, viewDirM, VdotN, dx, dy);
		#else
			texCoords = ParallaxOcclusionMapping(HeightMap, texCoords, viewDirM, VdotN, dx, dy);
		#endif
	#else
		#define SampleTexture(tex, uv) Sample2D(tex, uv)
	#endif
	
	diffuse = SRGBToLinear(SampleTexture(DiffuseMap, texCoords));
	
	#ifdef ROUGHMAP
		#ifdef ORM
			// Newest Standard ORM: R - AO, G - Roughness, B - Metalness
			float4 MaterialData = SampleTexture(MaterialMap, texCoords).gbra;
			diffuse.rgb *= MaterialData.b;
		#else
			// Deprecated RM: R - Roughness, B - Metalness
			float4 MaterialData = SampleTexture(MaterialMap, texCoords);
		#endif
		
		// Out: R - Roughness, G - Metalness, B - AO
	#endif

	#ifdef D3D11 // D3D9 has auto alpha test
		#ifdef MASKED
			clip(diffuse.a - 0.5f);
		#endif
	#endif
	diffuse *= input.Color;

	#ifdef NORMALMAP
		float3 bump = SampleTexture(NormalMap, texCoords).rgb * 2.0 - 1.0;
		bump.xy *= 1.8f;
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

	material.x = clamp(material.x, 0.02, 1.0);
	material.y = clamp(material.y, 0.0, 1.0);
	
	float3 minReflectance = 0.04; 
	float3 F0 = lerp(minReflectance, max(minReflectance, diffuse.rgb), material.y);

	#ifndef FULLBRIGHT
		float3 ambient = AmbientColor;
	#else
		float3 ambient = float3(1,1,1);
	#endif
		
	#if defined(EMISSIVEMAP)
		float3 emissive = SampleTexture(EmissiveMap, texCoords).rgb;
		
		#ifdef EMISSIVECOLOR
		emissive *= input.EmissiveColor;
		#endif
		
		emissive = SRGBToLinear(emissive);
		
		#ifdef MUL
			emissive *= EmissiveMultiply;
		#endif

		color = float4(diffuse.rgb * ambient * (1.0 - material.y), diffuse.a) + float4(emissive, 0.0);

		#ifdef DISABLEFOG
			fogFactor = 0.0f;
		#else
			float near = lerp(FogPlane.x, FogPlane.y, GetIntensity(emissive) * 0.95);
			fogFactor = saturate((distance(EyePos, input.WorldPos) - near) / (FogPlane.y - near));
		#endif
	#else
		color = float4(diffuse.rgb * ambient * (1.0 - material.y), diffuse.a);
		
		#ifdef MUL
			color.rgb *= EmissiveMultiply;
		#endif
		
		#ifdef DISABLEFOG
			fogFactor = 0.0f;
		#else
			fogFactor = saturate((distance(EyePos, input.WorldPos) - FogPlane.x) / (FogPlane.y - FogPlane.x));
		#endif
	#endif
	
	#ifdef FORWARD
		const float3 eyeVector = normalize(EyePos - input.WorldPos);
		const float3 lightVec = normalize(LightDirection);
		color.rgb += CalculatePBRLight(lightVec, LightColor, eyeVector, normal, diffuse.rgb * (1.0 - material.y), F0, material.x);
		color.rgb = LinearToSRGB(pow(max(ACESFilm(color.rgb), 0.0), 0.707));
	#else
		#if defined(TRANSPARENT) && !defined(FULLBRIGHT)
		float2 screenUV = GetScreenTexCoords(input.ScreenPosition) + halfPixel;
		float3 sceneLight = Sample2DLod0(Lighting, screenUV).rgb;
		float sceneDepth = Sample2DLod0(MRTDepth, screenUV).r;
		float meshDepth = input.Depth.x / input.Depth.y;
		
		float n = ClipPlane.x;
		float f = ClipPlane.y;
		
		#ifdef REVERSEDZ
		float linearScene = (n * f) / (sceneDepth * (f - n) + n);
		float linearMesh  = (n * f) / (meshDepth * (f - n) + n);
		#else
		float linearScene = (n * f) / (f - sceneDepth * (f - n));
		float linearMesh  = (n * f) / (f - meshDepth * (f - n));
		#endif
		
		float distanceFactor = saturate(1.0 - abs(linearScene - linearMesh) / 2.0);
		color.rgb += diffuse.rgb * sceneLight * distanceFactor;
		#endif
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
	
	#if defined(TRANSPARENT) || defined(FORWARD)
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
		output.Normal = float4(normal * material.x, material.y);
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

float4 PS_DepthHack(VS_OUTPUT_DEFERRED input) : OUTPUT(0)
{
	#ifdef REVERSEDZ
		return float4(input.Depth.x / input.Depth.y / DepthMultiply, 1, 1, 1);
	#else
		return float4(input.Depth.x / input.Depth.y * DepthMultiply, 1, 1, 1);
	#endif
}

#ifdef D3D11
DepthStencilState AlwaysDepth
{
    DepthEnable = TRUE;
    DepthWriteMask = ALL; 
    DepthFunc = Always; 
};
#endif

technique DepthHack
{
	pass DPass
	{
		Vertex(VS_Base);
		Pixel(PS_DepthHack);
		
		#ifndef D3D11
			Lighting = false;
			ZFunc = Always;
		#else
			SetDepthStencilState(AlwaysDepth, 0);
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
		#else
			SetDepthStencilState(AlwaysDepth, 0);
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
		#else
			SetDepthStencilState(AlwaysDepth, 0);
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
	float4 Pos : OUT_POSITION;
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

float4 PS_Depth(VS_OUTPUT_DEPTH input) : OUTPUT(0)
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