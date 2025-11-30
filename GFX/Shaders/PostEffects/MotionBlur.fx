//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define NUM_SAMPLES 7

const float4x4 InvViewProj;
const float4x4 PrevViewProj;
const float Strength = 10.0f;
const float Timestep;

sampler ColorMap : register(s0) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

sampler DepthMap : register(s1) = sampler_state
{
    MinFilter = None;
    MagFilter = None;
    MipFilter = None;
    AddressU = Clamp;
    AddressV = Clamp;
    AddressW = Clamp;
};

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

float4 PS_MotionBlur(PS_INPUT input) : COLOR
{
	float4 H = float4(input.TexCoord.x * 2.0f - 1.0f,  -(input.TexCoord.y * 2.0f - 1.0f), Sample2D(DepthMap, input.TexCoord).r, 1);
	float4 D = mul(H, InvViewProj);
	float4 worldPos = D / D.w;

	float4 currentPos = H;
	float4 previousPos = mul(float4(worldPos.xyz, 1.0), PrevViewProj);
	previousPos /= previousPos.w;

	float2 velocity = (currentPos.xy - previousPos.xy) / 200.f * Strength;
	velocity /= Timestep * 20.0;
	
	// Get the initial color at this pixel.
	float4 color = Sample2DLod0(ColorMap, input.TexCoord);
	input.TexCoord += velocity;
	for (int i = 1; i < NUM_SAMPLES; ++i, input.TexCoord += velocity)
	{
	  // Sample the color buffer along the velocity vector.
	  float4 currentColor = Sample2DLod0(ColorMap, input.TexCoord);
	  // Add the current color to our color sum.
	  color += currentColor;
	} // Average all of the samples to get the final blur color.
	float4 finalColor = color / NUM_SAMPLES;

    return finalColor;
}

technique Main
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 PS_MotionBlur();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}