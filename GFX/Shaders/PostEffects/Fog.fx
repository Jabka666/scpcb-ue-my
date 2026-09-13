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

DeclareSampler(ColorMap, 0, BLITZ_FILTER_POINT, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(AlbedoMap, 1, BLITZ_FILTER_POINT, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);

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
    float3 finalColor = lerp(sceneColor, FogColor, fogFactor);

    #ifdef D3D11
        float noise = InterleavedGradientNoise(input.Pos.xy);
    #else
        float noise = InterleavedGradientNoise(input.TexCoord * ViewportSize.xy);
    #endif

    float ditherStrength = (0.5 / 255.0) * fogFactor;
    finalColor += (noise - 0.5) * ditherStrength;

    return float4(finalColor, 1.0);
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