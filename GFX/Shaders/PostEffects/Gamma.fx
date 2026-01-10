//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"


#ifdef D3D11
	texture2D tColorMap : register(t0);
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s0) = sampler_state
	{
		MinFilter = Linear;
		MagFilter = Linear;
		MipFilter = Linear;
		AddressU = Clamp;
		AddressV = Clamp;
		AddressW = Clamp;
	};
#endif

struct PS_INPUT
{
    float4 Pos      : POSITION0;
    float2 TexCoord : TEXCOORD0;
};

const float Gamma = 0.5f;

PS_INPUT VertexProcess(VS_INPUT input)
{
    PS_INPUT o;
    o.Pos = mul(input.Pos, ViewProj);
    o.TexCoord = GetScreenTexCoords(o.Pos) + halfPixel;
    return o;
}

static const float correction = 1.0 / Gamma;

float4 ProcessGamma(PS_INPUT input) : COLOR
{
	return pow(Sample2DLod0(ColorMap, input.TexCoord), correction);
}

technique Main
{
	pass p0
	{
		#ifdef D3D11
			VertexShader = compile vs_5_0 VertexProcess();
			PixelShader = compile ps_5_0 ProcessGamma();
		#else
			VertexShader = compile vs_3_0 VertexProcess();
			PixelShader = compile ps_3_0 ProcessGamma();
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		#endif
	}
}