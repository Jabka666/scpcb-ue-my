//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#ifdef D3D11
#define FXAA_HLSL_4 1
#else
#define FXAA_HLSL_3 1
#endif
#define FXAA_QUALITY__PRESET 9
#include "FXAATool.fx"

//   1.00 - upper limit (softer)
//   0.75 - default amount of filtering
//   0.50 - lower limit (sharper, less sub-pixel aliasing removal)
//   0.25 - almost off
//   0.00 - completely off
float fxaaQualitySubpix = 0.75; 

//   0.333 - too little (faster)
//   0.250 - low quality
//   0.166 - default
//   0.125 - high quality 
//   0.063 - overkill (slower)
float fxaaQualityEdgeThreshold = 0.125; 

//   0.0833 - upper limit (default, the start of visible unfiltered edges)
//   0.0625 - high quality (faster)
//   0.0312 - visible limit (slower)
float fxaaQualityEdgeThresholdMin = 0.0625; 

static const float BUFFER_RCP_WIDTH = 1.0 / ScreenSize.x;
static const float BUFFER_RCP_HEIGHT = 1.0 / ScreenSize.y;

#ifdef D3D11
texture2D tScreenTex : register(t0);
sampler ScreenTex = sampler_state { Filter = MIN_MAG_MIP_LINEAR; AddressU = Clamp; AddressV = Clamp; };
#else
sampler ScreenTex : register(s0) = sampler_state
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

float4 FXAA(PS_INPUT input) : OUTPUT(0)
{
	#ifdef D3D11
		FxaaTex fxaatex = {ScreenTex, tScreenTex};
	#endif
	
	float4 c0 = FxaaPixelShader(
		input.TexCoord,
		#ifdef D3D11
			fxaatex,
		#else
			ScreenTex,
		#endif
		float2(BUFFER_RCP_WIDTH, BUFFER_RCP_HEIGHT),
		float4(-2.0*BUFFER_RCP_WIDTH,-2.0*BUFFER_RCP_HEIGHT,2.0*BUFFER_RCP_WIDTH,2.0*BUFFER_RCP_HEIGHT),
		fxaaQualitySubpix,
		fxaaQualityEdgeThreshold,
		fxaaQualityEdgeThresholdMin
	);
	
    return c0;
}

technique Main
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(FXAA);
	}
}