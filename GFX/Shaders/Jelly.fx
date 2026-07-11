float4x4 WorldViewProj : WORLDVIEWPROJ;
float time : TIME;

texture tex0;

sampler2D sam0 = sampler_state
{
    Texture = <tex0>;

    MinFilter = Linear;
    MagFilter = Linear;
    MipFilter = Point;

    AddressU = Wrap;
    AddressV = Wrap;
};

struct VS_INPUT
{
    float4 pos : POSITION;
    float2 tex : TEXCOORD0;
};

struct VS_OUTPUT
{
    float4 pos : POSITION;
    float2 tex : TEXCOORD0;
};

VS_OUTPUT VS(VS_INPUT input)
{
    VS_OUTPUT output = (VS_OUTPUT)0;

    float3 p = input.pos.xyz;

    float distortionScale = 0.005;

    float wave1 = sin(p.x * 0.8 + time * 0.5);
    float wave2 = cos(p.z * 0.7 + time * 0.4);
    float wave3 = sin((p.x + p.z) * 0.5 + time * 0.35);

    p.y += distortionScale * (wave1 + wave2 * 0.5);
    p.x += distortionScale * wave3;
    p.z += distortionScale * cos(p.y * 0.6 + time * 0.45);

    output.pos = mul(float4(p, 1.0), WorldViewProj);
    output.tex = input.tex;

    return output;
}

float4 PS(VS_OUTPUT input) : COLOR0
{
    float4 color = tex2D(sam0, input.tex);
    return color;
}

technique Jelly
{
    pass P0
    {
        VertexShader = compile vs_2_0 VS();
        PixelShader = compile ps_2_0 PS();
    }
}