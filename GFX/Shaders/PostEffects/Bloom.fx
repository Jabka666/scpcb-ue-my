//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

const float BloomThreshold = 0.6f;
const float BloomIntensity = 1.5f;

static const float2 BlurInvSize = 1.0 / (ScreenSize / 4.0);

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
	MipFilter = None;
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

#define NUM_WEIGHTS 5

static const float offsets[NUM_WEIGHTS] = 
{
    0.0,
    1.0,
    2.0,
    3.0,
    4.0
};

static const float weights[NUM_WEIGHTS] = 
{ 
    0.227027,
    0.1945946,
    0.1216216,
    0.0540541,
    0.0162162
};

struct PS_INPUT
{
    float4 Pos       : POSITION0;
    float2 TexCoord  : TEXCOORD0;
	float2 BlurCoord : TEXCOORD1;
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
	float3 color = Sample2D(ColorMap, input.TexCoord).rgb;
    return float4(color * GetBloomLuma(color, BloomThreshold), 1.0);
}

float4 ProcessH(PS_INPUT input) : COLOR
{
    float3 color = Sample2D(BloomMap, input.TexCoord).rgb * weights[0];

    for (int i = 1; i < NUM_WEIGHTS; i++)
    {
        float2 offset = float2(offsets[i], 0.0) * BlurInvSize;
        color += Sample2D(BloomMap, input.TexCoord + offset).rgb * weights[i];
        color += Sample2D(BloomMap, input.TexCoord - offset).rgb * weights[i];
    }
    
    return float4(color, 1.0);
}

float4 ProcessV(PS_INPUT input) : COLOR
{
    float3 color = Sample2D(BloomBlur, input.TexCoord).rgb * weights[0];
    
    for (int i = 1; i < NUM_WEIGHTS; i++)
    {
        float2 offset = float2(0.0, offsets[i]) * BlurInvSize;
        color += Sample2D(BloomBlur, input.TexCoord + offset).rgb * weights[i];
        color += Sample2D(BloomBlur, input.TexCoord - offset).rgb * weights[i];
    }
    
    return float4(color, 1.0);
}

float4 ProcessCombine(PS_INPUT input) : COLOR
{
    return Sample2D(BloomMap, input.BlurCoord) * BloomIntensity;
}

technique Downsample
{
    pass p0
    {
        VertexShader = compile vs_3_0 VertexProcess();
        PixelShader = compile ps_3_0 ProcessDownsample();
        ZWriteEnable = false;
        Lighting = false;
        AlphaBlendEnable = false;
    }
}

technique BlurH
{
    pass p0
    {
        VertexShader = compile vs_3_0 VertexProcess();
        PixelShader = compile ps_3_0 ProcessH();
        ZWriteEnable = false;
        Lighting = false;
        AlphaBlendEnable = false;
    }
}

technique BlurV
{
    pass p0
    {
        VertexShader = compile vs_3_0 VertexProcess();
        PixelShader = compile ps_3_0 ProcessV();
        ZWriteEnable = false;
        Lighting = false;
        AlphaBlendEnable = false;
    }
}

technique Combine
{
    pass p0
    {
        VertexShader = compile vs_3_0 VertexProcess();
        PixelShader = compile ps_3_0 ProcessCombine();
        ZWriteEnable = false;
        Lighting = false;
    }
}