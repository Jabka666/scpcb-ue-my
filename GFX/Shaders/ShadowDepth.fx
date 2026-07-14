float4x4 World;
float4x4 WorldView;
float4x4 WorldViewProj;

float FarPlane = 1000.0;

struct VS_INPUT {
	float3 Pos : POSITION;
};

struct VS_OUTPUT {
	float4 Pos   : POSITION;
	float  ViewZ : TEXCOORD0;
};

VS_OUTPUT VS(VS_INPUT IN) {
	VS_OUTPUT OUT;
	OUT.Pos = mul(float4(IN.Pos, 1.0), WorldViewProj);
	OUT.ViewZ = mul(float4(IN.Pos, 1.0), WorldView).z;
	return OUT;
}

float4 PS(VS_OUTPUT IN) : COLOR {
	float d = saturate(IN.ViewZ / max(FarPlane, 0.0001));
	return float4(d, d, d, 1.0);
}

technique ShadowDepth {
	pass P0 {
		VertexShader = compile vs_2_0 VS();
		PixelShader  = compile ps_2_0 PS();
	}
}
