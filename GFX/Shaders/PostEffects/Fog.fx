//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

float3 cFogColor			: FOG_COLOR;
static const float3 FogColor = pow(cFogColor, 2.2);

#ifdef D3D11
	texture2D tColorMap : register(t0);
	sampler ColorMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
	
	texture2D tAlbedoMap : register(t1);
	sampler AlbedoMap = sampler_state { Filter = MIN_MAG_MIP_POINT; AddressU = Clamp; AddressV = Clamp; };
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
	
	sampler AlbedoMap : register(s1) = sampler_state
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

float4 FogProcess(PS_INPUT input) : OUTPUT(0)
{
    float3 sceneColor = Sample2D(ColorMap, input.TexCoord).rgb;
    float fogFactor = Sample2D(AlbedoMap, input.TexCoord).a;
    return float4(lerp(sceneColor, FogColor, fogFactor), 1.0);
}

technique Main
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(FogProcess);
		
		#ifndef D3D11
			ZWriteEnable = false;
			ClipPlaneEnable = false;
			Lighting = false;
		#endif
	}
}