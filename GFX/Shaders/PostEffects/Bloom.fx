//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#include "..\Deferred\Tools.fx"

#define BLOOM_SAMPLES 8

uniform float BloomIntensity = 0.2f;
uniform float BloomSensitivity = 1.0f;
uniform float BloomCurve = 3.23f;
uniform float BloomSpread = 0.5f;
uniform float BloomExposure = 0.5f;
uniform float BloomSaturation = 0.7f;

uniform float BlurSize;
uniform float2 BlurInvSize;
uniform float2 HighestSize;

static const float2 BufferSize = 1.0 / HighestSize;

DeclareSampler(ColorMap, 0, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(BloomMap, 1, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(sBloomV_A, 2, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(sBloomH_B, 3, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(sBloomV_B, 4, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(sBloomH_C, 5, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);
DeclareSampler(sBloomV_C, 6, BLITZ_FILTER_LINEAR, BLITZ_ADDR_CLAMP, BLITZ_ADDR_CLAMP, 0.0, 1);

struct PS_INPUT
{
    float4 Pos       : OUT_POSITION;
    float2 TexCoord  : TEXCOORD0;
};

PS_INPUT VertexProcess(VS_INPUT input)
{
    PS_INPUT output;

    output.Pos = mul(input.Pos, ViewProj);
	#ifdef D3D11
		output.TexCoord = GetScreenTexCoords(output.Pos);
	#else
		output.TexCoord = GetScreenTexCoords(output.Pos) + BlurInvSize;
	#endif
    return output;
}

#ifdef D3D11
float4 SampleBlur(texture2D ttex, sampler tex, float2 texcoords, float2 pixelsize)
#else
float4 SampleBlur(sampler tex, float2 texcoords, float2 pixelsize)
#endif
{
    return (Sample2D(tex, texcoords + float2(pixelsize.x, pixelsize.y)) +
		Sample2D(tex, texcoords + float2(-pixelsize.x, pixelsize.y)) +
		Sample2D(tex, texcoords + float2(pixelsize.x, -pixelsize.y)) +
		Sample2D(tex, texcoords + float2(-pixelsize.x, -pixelsize.y))) * 0.25f;
}

float4 CalculateBloom(float2 texcoord, float size, float2 dir)
{
    const float2 pixelSize = BufferSize * size;
    float offset = BloomSpread * 0.5;
	float4 blur = 0.0f;
	float totalWeight = 0.00001f;
	
    for (int i = 1; i < BLOOM_SAMPLES; i++)
    {
		float2 d = float2(offset * pixelSize.x, offset * pixelSize.y) * dir;
        float weight = pow(abs(BLOOM_SAMPLES - i), BloomCurve);
        blur	+= Sample2DLod0(BloomMap, texcoord + d) * weight;
        blur 	+= Sample2DLod0(BloomMap, texcoord - d) * weight;
        offset 	+= BloomSpread;
        totalWeight += weight;
    }

    return blur / (totalWeight * 2);
}

float3 ApplySaturation(float3 color, float saturation)
{
    float luma = dot(color, float3(0.299, 0.587, 0.114));
    return lerp(float3(luma, luma, luma), color, saturation);
}

float4 PS_Luma(PS_INPUT input) : OUTPUT(0)
{
    return float4(GetBloomLuma(Sample2DLod0(ColorMap, input.TexCoord).rgb, BloomSensitivity), 1.0f);
}

float4 PS_BloomH(PS_INPUT input) : OUTPUT(0)
{
    return CalculateBloom(input.TexCoord, BlurSize, float2(1, 0));
}

float4 PS_BloomV(PS_INPUT input) : OUTPUT(0)
{
    return CalculateBloom(input.TexCoord, BlurSize, float2(0, 1));
}

float4 PS_BlurBloom(PS_INPUT input) : OUTPUT(0)
{
	float3 LP = Sample2D(ColorMap, input.TexCoord).rgb;
	#ifdef D3D11
	    float4 Bloom  = SampleBlur(tBloomMap, BloomMap, input.TexCoord, BufferSize * 2);
           Bloom += SampleBlur(tsBloomV_A, sBloomV_A, input.TexCoord, BufferSize * 2);
           Bloom += SampleBlur(tsBloomH_B, sBloomH_B, input.TexCoord, BufferSize * 4);
           Bloom += SampleBlur(tsBloomV_B, sBloomV_B, input.TexCoord, BufferSize * 4);
           Bloom += SampleBlur(tsBloomH_C, sBloomH_C, input.TexCoord, BufferSize * 8);
           Bloom += SampleBlur(tsBloomV_C, sBloomV_C, input.TexCoord, BufferSize * 8);
	#else
    float4 Bloom  = SampleBlur(BloomMap, input.TexCoord, BufferSize * 2);
           Bloom += SampleBlur(sBloomV_A, input.TexCoord, BufferSize * 2);
           Bloom += SampleBlur(sBloomH_B, input.TexCoord, BufferSize * 4);
           Bloom += SampleBlur(sBloomV_B, input.TexCoord, BufferSize * 4);
           Bloom += SampleBlur(sBloomH_C, input.TexCoord, BufferSize * 8);
           Bloom += SampleBlur(sBloomV_C, input.TexCoord, BufferSize * 8);
	#endif
           Bloom *= lerp(0, 10, BloomIntensity) / 6;
    return float4(lerp(Bloom.rgb, max(Bloom.rgb - LP, 0.0), 0),0);
}

float4 PS_FinalBloom(PS_INPUT input) : OUTPUT(0)
{
	#ifdef D3D11
		float4 Bloom = SampleBlur(tBloomMap, BloomMap, input.TexCoord, BufferSize);
    #else
		float4 Bloom = SampleBlur(BloomMap, input.TexCoord, BufferSize);
	#endif
	
    Bloom.rgb = ApplySaturation(Bloom.rgb, BloomSaturation);
    
	return float4(Bloom.rgb * BloomExposure, 1.0f);
}

technique Luma
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PS_Luma);

		#ifndef D3D11
		ZWriteEnable = false;
		Lighting = false;
		#endif
	}
}

technique BloomH
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PS_BloomH);

		#ifndef D3D11
		ZWriteEnable = false;
		Lighting = false;
		#endif
	}
}

technique BloomV
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PS_BloomV);

		#ifndef D3D11
		ZWriteEnable = false;
		Lighting = false;
		#endif
	}
}

technique Blur
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PS_BlurBloom);

		#ifndef D3D11
		ZWriteEnable = false;
		Lighting = false;
		#endif
	}
}

technique Final
{
	pass p0
	{
		Vertex(VertexProcess);
		Pixel(PS_FinalBloom);

		#ifndef D3D11
		ZWriteEnable = false;
		Lighting = false;
		#endif
	}
}