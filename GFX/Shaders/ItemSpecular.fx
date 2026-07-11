float4x4 World;
float4x4 View;
float4x4 Projection;
float4x4 WorldViewProj;

float3 CameraPos;
float3 LightDir;
float3 LightColor = float3(1.0, 1.0, 1.0);

float SpecStrength = 0.35;
static const float SpecPower   = 24.0;
static const float RimStrength = 0.15;
static const float RimPower    = 3.0;

texture DiffuseTexture;
sampler DiffuseSampler = sampler_state
{
    Texture = <DiffuseTexture>;
    MinFilter = LINEAR;
    MagFilter = LINEAR;
    MipFilter = LINEAR;
    AddressU = WRAP;
    AddressV = WRAP;
};

struct VS_INPUT
{
    float3 Position : POSITION;
    float3 Normal   : NORMAL;
    float4 Color    : COLOR0;
    float2 UV       : TEXCOORD0;
    float2 UV2      : TEXCOORD1;
};

struct VS_OUTPUT
{
    float4 Position   : POSITION;
    float2 UV         : TEXCOORD0;
    float3 WorldNormal: TEXCOORD1;
    float3 WorldPos   : TEXCOORD2;
};

VS_OUTPUT VS_Main(VS_INPUT IN)
{
    VS_OUTPUT OUT;

    float4 worldPos = mul(float4(IN.Position, 1.0), World);
    OUT.WorldPos    = worldPos.xyz;
    OUT.Position    = mul(float4(IN.Position, 1.0), WorldViewProj);
    OUT.WorldNormal = normalize(mul(IN.Normal, (float3x3)World));
    OUT.UV          = IN.UV;

    return OUT;
}

float4 PS_Main(VS_OUTPUT IN) : COLOR0
{
    float3 N = normalize(IN.WorldNormal);
    float3 V = normalize(CameraPos - IN.WorldPos);
    float3 L = normalize(LightDir);

    float4 baseColor = tex2D(DiffuseSampler, IN.UV);
    float NdotL = saturate(dot(N, L));
    float3 diffuse = baseColor.rgb * (0.35 + 0.65 * NdotL);

    float3 H = normalize(L + V);
    float NdotH = saturate(dot(N, H));
    float spec = pow(NdotH, SpecPower) * SpecStrength;

    float fresnel = pow(1.0 - saturate(dot(N, V)), RimPower) * RimStrength;

    float3 fakeSpecular = (spec + fresnel) * LightColor;
    float3 finalColor = diffuse + fakeSpecular;
	
    return float4(finalColor, baseColor.a);
}

technique ItemFakeSpecular
{
    pass P0
    {
        VertexShader = compile vs_2_0 VS_Main();
        PixelShader  = compile ps_2_0 PS_Main();
    }
}
