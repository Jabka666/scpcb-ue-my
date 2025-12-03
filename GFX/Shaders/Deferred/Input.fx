//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "Tools.fx"
#include "Transform.fx"

float3 AmbientColor 	: AMBIENT_COLOR;
float4 EntityColor 		: ENTITY_COLOR;
float3 FogColor			: FOG_COLOR;
float FogNear			: FOG_NEAR;
float FogFar			: FOG_FAR;
float2 Specular			: ENTITY_SPECULAR;
float3 EyePos			: EYE_POSITION;

float3x2 TextureMatrix : MATRIX_TEXTURE0;
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

#ifdef MUL
	const float EmissiveMultiply;
#endif

#ifdef INNERGLOW
	const float InnerGlow;
#endif
	
const float DepthMultiply;

// ==================================================== GBUFFER

struct VS_INPUT_GBUFFER
{ 
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	float3 Tangent : TEXCOORD2;
	float3 Binormal : TEXCOORD3;
	float4 BlendWeights : BLENDWEIGHT;
	float4 BlendIndices : BLENDINDICES;
	
};

struct PS_INPUT_GBUFFER
{
	float4 Pos : POSITION; 
	float3 Normal : NORMAL;
	float2 TexCoords : TEXCOORD0;
	float3 WorldPos : TEXCOORD1;
	float3 Tangent : TEXCOORD2;
	float3 Binormal : TEXCOORD3;
	float2 Depth : TEXCOORD4;
	float4 ScreenPos : TEXCOORD5;
	#ifdef INNERGLOW
		float3 View      : TEXCOORD6;
	#endif
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
	
	const float4x3 WorldTransform = Skinned ? GetSkinTransform(input.BlendIndices, input.BlendWeights) : World;

	output.WorldPos = mul(input.Pos, WorldTransform);
	output.Pos = mul(float4(mul(input.Pos, WorldTransform), 1), ViewProj);
	
	output.TexCoords = mul(input.TexCoords, TextureMatrix);
    
	output.Normal = normalize(mul(input.Normal, WorldTransform));
    output.Tangent = normalize(mul(input.Tangent, WorldTransform));
	output.Binormal = normalize(mul(input.Binormal, WorldTransform));
	output.Depth = output.Pos.zw;
	output.ScreenPos = output.Pos;
	
	#ifdef INNERGLOW
		output.View = normalize(EyePos - output.WorldPos);
	#endif
	return output; 
}

PixelOutput GBufferPixel(PS_INPUT_GBUFFER input)
{
	PixelOutput output;
	float4 diffuse = Sample2D(DiffuseMap, input.TexCoords) * EntityColor;

	#ifndef TRANSPARENT
		#ifdef NORMALMAP
			const float3 bump = Sample2D(NormalMap, input.TexCoords).rgb * 2.0 - 1.0;
			const float3 normal = normalize((bump.x * input.Tangent) + (bump.y * input.Binormal) + (bump.z * input.Normal));
		#else
			const float3 normal = input.Normal;
		#endif

		#ifdef ROUGHMAP
			const float roughness = Sample2D(RoughnessMap, input.TexCoords).r * 2.0;
			const float specIntensity = lerp(Specular.r, 0.0, roughness);
			const float specPower = Specular.g;
		#else
			const float specIntensity = Specular.r;
			const float specPower = Specular.g;
		#endif
	#endif
	
	#ifndef FULLBRIGHT
		const float3 ambient = AmbientColor;
	#else
		const float3 ambient = float3(1,1,1);
	#endif
	
	#if defined(EMISSIVEMAP)
		#ifndef MUL
			float3 emissive = Sample2D(EmissiveMap, input.TexCoords).rgb;
		#else
			float3 emissive = Sample2D(EmissiveMap, input.TexCoords).rgb * EmissiveMultiply;
		#endif
		
		output.Color = float4(diffuse.rgb * ambient, diffuse.a) + float4(emissive, 0.0);
		float fogFactor = saturate((distance(EyePos, input.WorldPos) - lerp(FogNear, FogFar, GetIntensity(emissive) * 0.8)) / FogFar);
	#else
		#ifndef MUL
			output.Color = float4(diffuse.rgb * ambient, diffuse.a);
		#else
			output.Color = float4(diffuse.rgb * ambient * EmissiveMultiply, diffuse.a);
		#endif
		float fogFactor = saturate((distance(EyePos, input.WorldPos) - FogNear) / FogFar);
	#endif
	
	#ifdef INNERGLOW
		float NdotV = dot(normalize(input.Normal), normalize(input.View));
		float fresnel = pow(1.0f - NdotV, InnerGlow);
		const float glowFactor = saturate(fresnel);
		output.Color.rgb += glowFactor;
	#endif
	
	// Fog dither
	fogFactor = 1.0 - ShadeDither(float4(1.0 - fogFactor, 0, 0, 0), input.ScreenPos).r;
	
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
		output.Normal = float4(normal * 0.5 + 0.5, specPower / 10.0);
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
		VertexShader = compile vs_3_0 GBufferVertex();
		PixelShader = compile ps_3_0 GBufferDepth();
		Lighting = false;
		ZFunc = Always;
	}
}

technique GBuffer
{
	pass Input
	{
		VertexShader = compile vs_3_0 GBufferVertex();
		PixelShader = compile ps_3_0 GBufferPixel();
		Lighting = false;
	}
}

// ==================================================== Shadow Map Depth

struct VS_INPUT_DEPTH
{ 
	float4 Pos : POSITION;
	float4 BlendWeights : BLENDWEIGHT;
	float4 BlendIndices : BLENDINDICES;
};

float4 DepthVertex(VS_INPUT_DEPTH input) : POSITION
{
	const float4x3 WorldTransform = Skinned ? GetSkinTransform(input.BlendIndices, input.BlendWeights) : World;
	
	return mul(float4(mul(input.Pos, WorldTransform), 1), ViewProj);
}

float4 DepthPixel(float4 Pos : POSITION) : COLOR
{
	return 0;
}

technique ShadowMap
{
	pass FirstPass
	{
		VertexShader = compile vs_3_0 DepthVertex();
		PixelShader = compile ps_3_0 DepthPixel();
		Lighting = false;
	}
}