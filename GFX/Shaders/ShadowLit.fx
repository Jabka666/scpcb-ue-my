float4x4 World;
float4x4 WorldViewProj;

float4x4 LightView;
float4x4 LightProj;
float3   LightColor;
float3   LightDir;
float3   LightPos;
float    LightIsSpot;
float    LightRange = 1000.0;
float    CosPhi;
float    CosTheta;
float    FarPlane = 1000.0;
float    ShadowTexelSize = 1.0 / 1024.0;
float    ShadowBias = 0.0015;

texture  DiffuseTex;
float    HasDiffuseTex;

sampler2D DiffuseSampler = sampler_state {
	Texture = <DiffuseTex>;
	MinFilter = LINEAR; MagFilter = LINEAR; MipFilter = LINEAR;
	AddressU = WRAP; AddressV = WRAP;
};

texture  ShadowMap;
sampler2D ShadowSampler = sampler_state {
	Texture = <ShadowMap>;
	MinFilter = POINT; MagFilter = POINT; MipFilter = NONE;
	AddressU = CLAMP; AddressV = CLAMP;
};

struct VS_INPUT {
	float3 Pos    : POSITION;
	float3 Normal : NORMAL;
	float2 UV     : TEXCOORD0;
};

struct VS_OUTPUT {
	float4 Pos      : POSITION;
	float3 WorldPos : TEXCOORD0;
	float3 WorldN   : TEXCOORD1;
	float2 UV       : TEXCOORD2;
};

VS_OUTPUT VS(VS_INPUT IN) {
	VS_OUTPUT OUT;
	float4 worldPos = mul(float4(IN.Pos, 1.0), World);
	OUT.Pos = mul(float4(IN.Pos, 1.0), WorldViewProj);
	OUT.WorldPos = worldPos.xyz;
	OUT.WorldN = normalize(mul(float4(IN.Normal, 0.0), World).xyz);
	OUT.UV = IN.UV;
	return OUT;
}

float SampleShadow(float2 uv, float refDepth) {
	if(uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) return 1.0; //outside the map assume lit

	float2 offs[4] = {
		float2(-0.5, -0.5), float2(0.5, -0.5),
		float2(-0.5,  0.5), float2(0.5,  0.5)
	};
	float shadow = 0.0;
	for(int i = 0; i < 4; i++) {
		float stored = tex2D(ShadowSampler, uv + offs[i] * ShadowTexelSize).r;
		shadow += (refDepth <= stored + ShadowBias) ? 1.0 : 0.0;
	}
	return shadow * 0.25;
}

float4 PS(VS_OUTPUT IN) : COLOR {
	float3 N = normalize(IN.WorldN);

	float3 L;
	float atten = 1.0;
	if(LightIsSpot > 0.5) {
		float3 toLight = LightPos - IN.WorldPos;
		float dist = length(toLight);
		L = toLight / max(dist, 0.0001);

		float spotCos = dot(-L, normalize(LightDir));
		float spotAtten = saturate((spotCos - CosPhi) / max(CosTheta - CosPhi, 0.0001));
		float distAtten = saturate(1.0 - dist / max(LightRange, 0.0001));
		atten = spotAtten * distAtten;
	}
	else {
		L = normalize(-LightDir);
	}

	float NdotL = saturate(dot(N, L));
	if(NdotL <= 0.0 || atten <= 0.0) return float4(0, 0, 0, 0);

	float4 lightViewPos = mul(float4(IN.WorldPos, 1.0), LightView);
	float4 lightClipPos = mul(lightViewPos, LightProj);
	float2 shadowUV = (lightClipPos.xy / lightClipPos.w) * float2(0.5, -0.5) + 0.5;
	float refDepth = lightViewPos.z / max(FarPlane, 0.0001);
	float shadow = SampleShadow(shadowUV, refDepth);
	if(shadow <= 0.0) return float4(0, 0, 0, 0);

	float3 albedo = HasDiffuseTex > 0.5 ? tex2D(DiffuseSampler, IN.UV).rgb : float3(1, 1, 1);
	float3 result = albedo * LightColor * NdotL * atten * shadow;
	return float4(result, 1.0);
}

technique ShadowLit {
	pass P0 {
		VertexShader = compile vs_2_0 VS();
		PixelShader  = compile ps_2_0 PS();
	}
}
