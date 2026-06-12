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
	sampler ColorMap = sampler_state 
	{ 
		Filter = MIN_MAG_MIP_LINEAR; 
		AddressU = Clamp; 
		AddressV = Clamp;
	};
#else
	sampler ColorMap : register(s0) = sampler_state
	{
		MinFilter = LINEAR;
		MagFilter = LINEAR;
		MipFilter = LINEAR;
		AddressU = Clamp;
		AddressV = Clamp;
		AddressW = Clamp;
	};
#endif

struct PS_INPUT
{ 
	float4 Pos 				: OUT_POSITION; 
	float2 TexCoord 		: TEXCOORD0;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
	return output;
}

float4 Present(PS_INPUT input) : OUTPUT(0)
{
    return float4(Sample2DLod0(ColorMap, input.TexCoord).rgb, 1.0);
}

float4 PresentACES(PS_INPUT input) : OUTPUT(0)
{
    float3 color = Sample2DLod0(ColorMap, input.TexCoord).rgb;
    color = pow(max(ACESFilm(color), 0.0), 0.707);
    return float4(LinearToSRGB(color), 1.0);
}

float4 PresentPow(PS_INPUT input) : OUTPUT(0)
{
    float3 color = Sample2DLod0(ColorMap, input.TexCoord).rgb;
    color = pow(color, 0.85);
    return float4(LinearToSRGB(color), 1.0);
}

technique Main
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(Present);
		
		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}

technique ACES
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PresentACES);
		
		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}

technique Pow
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PresentPow);
		
		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}