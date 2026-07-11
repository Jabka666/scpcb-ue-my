float gammaValue = 1.0;

texture SceneTex;

sampler SceneSampler = sampler_state
{
    Texture = <SceneTex>;
    MinFilter = POINT;
    MagFilter = POINT;
    MipFilter = NONE;
    AddressU  = CLAMP;
    AddressV  = CLAMP;
};

float4x4 WorldViewProj;

struct VS_OUTPUT
{
    float4 Position : POSITION;
    float2 TexCoord : TEXCOORD0;
};

VS_OUTPUT VS_Gamma(float4 Pos : POSITION, float2 Tex : TEXCOORD0)
{
    VS_OUTPUT Out;
    Out.Position = mul(Pos, WorldViewProj);
    Out.TexCoord = Tex;
    return Out;
}

float4 PS_Gamma(VS_OUTPUT In) : COLOR0
{
    float3 color = tex2D(SceneSampler, In.TexCoord).rgb;
    color = pow(saturate(color), 1.0 / max(gammaValue, 0.0001));
    return float4(color, 1.0);
}

technique Gamma
{
    pass P0
    {
        VertexShader = compile vs_2_0 VS_Gamma();
        PixelShader  = compile ps_2_0 PS_Gamma();
    }
}
