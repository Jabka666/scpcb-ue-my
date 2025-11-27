//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 12 

const float4x4 InvViewProj;
const float4x4 PrevViewProj;

const float Strength = 1.0f; 
const float MaxVelocity = 0.05f; 

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler DepthMap : register(s1) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

struct PS_INPUT
{
    float4 Pos      : POSITION0;
    float2 TexCoord : TEXCOORD0;
};

PS_INPUT VertexProcess(VS_INPUT input)
{
    PS_INPUT output;
    output.Pos = mul(input.Pos, ViewProj);
    output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
    return output;
}

float InterleavedGradientNoise(float2 uv)
{
    float3 noise = float3(0.06711056, 0.00583715, 52.9829189);
    return frac(noise.z * frac(dot(uv, noise.xy)));
}

float4 ProcessMotionBlur(PS_INPUT input) : COLOR
{
    // 1. Получаем глубину
    float depth = Sample2DLod0(DepthMap, input.TexCoord).r;
    float4 H = float4(input.TexCoord.x * 2.0f - 1.0f, (1.0f - input.TexCoord.y) * 2.0f - 1.0f, depth, 1.0f);
    float4 D = mul(H, InvViewProj);
    float4 worldPos = D / D.w;
    float4 previousPos = mul(worldPos, PrevViewProj);
    previousPos /= previousPos.w;

    float2 velocity = (H.xy - previousPos.xy);
    velocity.x *= 0.5f;
    velocity.y *= -0.5f;
	
    velocity *= Strength;

    float velLen = length(velocity);
    if (velLen > MaxVelocity) velocity = (velocity / velLen) * MaxVelocity;
    if (velLen < 0.0001f) return Sample2DLod0(ColorMap, input.TexCoord);

    float noise = InterleavedGradientNoise(input.TexCoord * ScreenSize); 

    float2 uv = input.TexCoord;

    float2 step = velocity / float(NUM_SAMPLES);

    uv += step * (noise - 0.5f);

    float4 colorAccum = 0.0f;
    float weightAccum = 0.0f;

    [unroll]for (int i = 0; i < NUM_SAMPLES; i++)
    {
        float4 currentColor = Sample2DLod0(ColorMap, uv);

        colorAccum += currentColor;
        weightAccum += 1.0f;
        uv -= step; 
    }

    return colorAccum / weightAccum;
}

technique Main
{
    pass p0
    {
        VertexShader = compile vs_3_0 VertexProcess();
        PixelShader = compile ps_3_0 ProcessMotionBlur();
        ZWriteEnable = false;
        ClipPlaneEnable = false;
        Lighting = false;
        AlphaBlendEnable = false;
    }
}