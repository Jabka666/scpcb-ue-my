//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW = Clamp;
};
sampler BloomMap : register(s1) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW  = Clamp;
};
sampler BloomBlur : register(s2) = sampler_state
{
    MinFilter = Linear;
    MagFilter = Linear;
	MipFilter = Linear;
	AddressU = Clamp;
	AddressV = Clamp;
	AddressW  = Clamp;
};

const float BloomThreshold = 0.4;
const float2 BloomMix = float2(1, 1);
static const float2 BlurInvSize = 1.0 / (ScreenSize / 4.0);

static const float offsets[5] = {
    2.0,
    1.0,
    0.0,
    -1.0,
    -2.0,
};

static const float weights[5] = {
    0.1,
    0.25,
    0.3,
    0.25,
    0.1
};

struct PS_INPUT
{ 
	float4 Pos 				: POSITION0; 
	float2 TexCoord 		: TEXCOORD0;
	float2 BlurCoord		: TEXCOORD1;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	
	float2 ScreenCoord = GetScreenTexCoords(output.Pos);
	output.TexCoord = ScreenCoord + halfPixel;
	output.BlurCoord = ScreenCoord + BlurInvSize;
	return output;
}

float4 ProcessDownsample(PS_INPUT input) : COLOR
{
    float3 rgb = Sample2D(ColorMap, input.TexCoord).rgb;
	return float4((rgb - BloomThreshold) / (1.0 - BloomThreshold), 1.0);
}

float4 ProcessH(PS_INPUT input) : COLOR
{
	float3 color = 0.0;
	for (int i = 0; i < 5; ++i) color += Sample2D(BloomMap, input.TexCoord + (float2(offsets[i], 0.0)) * BlurInvSize).rgb * weights[i];
	return float4(color, 1.0);
}

float4 ProcessV(PS_INPUT input) : COLOR
{
	float3 color = 0.0;
	for (int i = 0; i < 5; ++i) color += Sample2D(BloomBlur, input.TexCoord + (float2(0.0, offsets[i])) * BlurInvSize).rgb * weights[i];
	return float4(color, 1.0);
}

float4 ProcessCombine(PS_INPUT input) : COLOR
{
    float3 diff = Sample2D(ColorMap, input.TexCoord).rgb * BloomMix.x;
    float3 bloom = Sample2D(BloomMap, input.BlurCoord).rgb * BloomMix.y;
	return float4((diff * saturate(1.0 - bloom)) + bloom, 1.0);
}

technique Downsample
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 ProcessDownsample();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}

technique BlurH
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 ProcessH();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}

technique BlurV
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 ProcessV();
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
		PixelShader = compile ps_3_0 ProcessCombine();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}