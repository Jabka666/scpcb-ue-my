//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

const float4 Value;

struct PS_INPUT
{ 
	float4 Pos 				: POSITION0; 
}; 

PS_INPUT VertexProcess(VS_INPUT input)
{ 
	PS_INPUT output; 
	output.Pos = mul(input.Pos, ViewProj); 
	return output;
}

float4 Clear(PS_INPUT input) : COLOR
{
    return Value;
}

technique Main
{
	pass p0
	{
		VertexShader = compile vs_3_0 VertexProcess();
		PixelShader = compile ps_3_0 Clear();
		ZWriteEnable = false;
		ClipPlaneEnable = false;
		Lighting = false;
	}
}