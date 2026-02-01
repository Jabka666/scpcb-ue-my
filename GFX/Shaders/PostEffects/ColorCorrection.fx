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
		MinFilter = None;
		MagFilter = None;
		MipFilter = None;
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

PS_INPUT VertexProcess(VS_INPUT input)
{
    PS_INPUT o;
    o.Pos = mul(input.Pos, ViewProj);
    o.TexCoord = GetScreenTexCoords(o.Pos) + halfPixel;
    return o;
}

// -------------------------- Parameters ------------------------------

const float ExposureStops = 0.0;
const float Offset        = -0.0090;
const float Hue           = 0.0;
const float Saturation    = 1.4;

static const float3x3 HueMatrix =
{
	0.213 + cos(Hue)*0.787 - sin(Hue)*0.213, 0.715 - cos(Hue)*0.715 - sin(Hue)*0.715, 0.072 - cos(Hue)*0.072 + sin(Hue)*0.928,
	0.213 - cos(Hue)*0.213 + sin(Hue)*0.143, 0.715 + cos(Hue)*0.285 + sin(Hue)*0.140, 0.072 - cos(Hue)*0.072 - sin(Hue)*0.283,
	0.213 - cos(Hue)*0.213 - sin(Hue)*0.787, 0.715 - cos(Hue)*0.715 + sin(Hue)*0.715, 0.072 + cos(Hue)*0.928 + sin(Hue)*0.072
};

// ------------------------ Hue ---------------------------

float3 ApplyHue(float3 c, float a)
{
    return mul(c, HueMatrix);
}

// ------------------------ Exposure ---------------------------


float3 ApplyExposureOffsetGamma(float3 c, float E_stops, float offset)
{
    return saturate(c * exp2(E_stops) + offset);
}

float4 ProcessColorCorrection(PS_INPUT input) : COLOR
{
    float4 col = saturate(Sample2DLod0(ColorMap, input.TexCoord));

    col.rgb = ApplyExposureOffsetGamma(col.rgb, ExposureStops, Offset);

    col.rgb = (col.rgb - 0.5) + 0.5;

    col.rgb = ApplyHue(col.rgb, Hue);

    float luma = dot(col.rgb, float3(0.299, 0.587, 0.114));
    col.rgb = lerp(luma.xxx, col.rgb, Saturation);

    return saturate(col);
}

technique Main
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(ProcessColorCorrection);
			
		#ifndef D3D11
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		#endif
	}
}