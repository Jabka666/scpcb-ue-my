//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

const float PresentMultiply = 1.0f;

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
	float4 Pos 				: POSITION0; 
	float2 TexCoord 		: TEXCOORD0;
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	output.TexCoord = GetScreenTexCoords(output.Pos) + halfPixel;
	return output;
}

float4 Present(PS_INPUT input) : COLOR
{
    return float4(Sample2DLod0(ColorMap, input.TexCoord).rgb, 1.0);
}

float4 PresentMul(PS_INPUT input) : COLOR
{
    return float4(Sample2DLod0(ColorMap, input.TexCoord).rgb * PresentMultiply, 1.0);
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

technique Mul
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PresentMul);
		
		#ifndef D3D11
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
		#endif
	}
}