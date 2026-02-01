//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

float3 FogColor			: FOG_COLOR;

#ifdef D3D11
	texture2D tColorMap : register(t1);
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
#else
	sampler ColorMap : register(s1) = sampler_state
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

float4 FogProcess(PS_INPUT input) : COLOR
{
    return 1.0 - Sample2D(ColorMap, input.TexCoord).a;
}

float4 FinalProcess(PS_INPUT input) : COLOR
{
	float fog = Sample2D(ColorMap, input.TexCoord).a;
    return float4(FogColor * fog, 1.0);
}

#ifdef D3D11
	BlendState Multiply
	{
		BlendEnable[0] = true;
		SrcBlend[0] = ZERO;
		DestBlend[0] = SRC_COLOR;
	};
	
	BlendState Additive
	{
		BlendEnable[0] = true;
		SrcBlend[0] = One;
		DestBlend[0] = One;
	};
#endif

technique Main
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(FogProcess);
		
		#ifdef D3D11
			SetBlendState(Multiply, float4(1, 1, 1, 1), -1);
		#else
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
			AlphaBlendEnable = true;
			SrcBlend = Zero;
			DestBlend = SrcColor;
		#endif
	}
	
	pass p1
	{
		Vertex(VertexProcess);
		Pixel(FinalProcess);
		
		#ifdef D3D11
			SetBlendState(Additive, float4(1, 1, 1, 1), -1);
		#else
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
			AlphaBlendEnable = true;
			SrcBlend = One;
			DestBlend = One;
		#endif
	}
}