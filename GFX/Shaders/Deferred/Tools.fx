//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

float2 ScreenSize 		: SCREEN_SIZE;

#ifdef D3D11
#define Sample2D(tex, uv) t##tex.Sample(tex, uv)
#define Sample2DProj(tex, uv) t##tex.Sample(tex, uv.xy / uv.w)
#define Sample2DShadow(tex, uv) t##tex.SampleCmpLevelZero(tex, uv.xy, uv.z)
#define Sample2DGrad(tex, uv, dx, dy) t##tex.SampleGrad(tex, uv, dx, dy)
#define SampleCube(tex, uv) t##tex.Sample(tex, uv)
#define Sample2DLod0(tex, uv) t##tex.SampleLevel(tex, uv, 0.0)
#define Sample2DProjLod0(tex, uv) Sample2DLod0(tex, uv.xy / uv.w)
#define Sample2DLod(tex, uv, level) t##tex.SampleLevel(tex, uv, level)
#define SampleCubeLOD(tex, uv) t##tex.SampleLevel(tex, uv.xyz, uv.w)
#define default_sampler_state sampler_state{Filter=ANISOTROPIC;AddressU = Wrap;AddressV = Wrap;MaxAnisotropy=Anisotropy; MipLODBias = -0.2;}
#define technique technique11
#define Vertex(VS) VertexShader = compile vs_5_0 VS()
#define Pixel(PS) PixelShader = compile ps_5_0 PS()
static const float2 halfPixel = float2(0.0, 0.0);
#else
#define Sample2D(t, uv) tex2D(t, uv)
#define Sample2DProj(t, uv) tex2Dproj(t, uv)
#define Sample2DShadow(t, uv) tex2Dproj(t, uv)
#define Sample2DGrad(t, uv, dx, dy) tex2Dgrad(t, uv, dx, dy)
#define SampleCube(t, uv) texCUBE(t, uv)
#define Sample2DLod0(t, uv) tex2Dlod(t, float4(uv, 0.0, 0.0))
#define Sample2DProjLod0(t, uv) tex2Dlod(t, float4(uv.xy / uv.w, 0.0, 0.0))
#define SampleCubeLOD(t, uv) texCUBElod(t, uv)
static const float2 halfPixel = float2(0.5 / ScreenSize.x, 0.5 / ScreenSize.y);
#define Vertex(VS) VertexShader = compile vs_3_0 VS()
#define Pixel(PS) PixelShader = compile ps_3_0 PS()
#endif

float4x3 World 			: MATRIX_WORLD; 
float4x4 WorldViewProj 	: MATRIX_WORLDVIEWPROJ;
float4x4 ViewProj 		: MATRIX_VIEWPROJ; 
int Anisotropy			: ANISOTROPY;

struct VS_INPUT
{ 
	float4 Pos : POSITION; 
	float3 Normal : NORMAL; 
	float4 Color : COLOR0; 
	float2 TexCoords : TEXCOORD0;
};

inline float4 GetWorldPositionW(float2 Coords, float Depth, in float4x4 ivpmat)
{
	float4 WorldPos = float4(Coords.x * 2.0f - 1.0f,  -(Coords.y * 2.0f - 1.0f), Depth, 1.0f);
	WorldPos 		= mul(WorldPos, ivpmat);
	WorldPos.xyz 	/= WorldPos.w;
	return WorldPos;
}

inline float3 GetWorldPosition(float2 Coords, float Depth, in float4x4 ivpmat)
{
	return GetWorldPositionW(Coords, Depth, ivpmat).xyz;
}

inline float2 GetScreenTexCoords(float4 ScreenCoords)
{
	ScreenCoords.xy /= ScreenCoords.w;
	return 0.5f * (float2(ScreenCoords.x, -ScreenCoords.y) + 1.0f);
}

inline float GetSpecular(float3 normal, float3 eyevec, float3 lightDir, float spec)
{
    float3 V = normalize(eyevec);
    float3 H = normalize(V + lightDir);
    float NdotV = saturate(dot(normal, V));
    float NdotL = saturate(dot(normal, lightDir));

    float specular = saturate(pow(saturate(dot(normal, H)), spec));
    float rim = saturate(pow(1.0 - NdotV, 3.0) * NdotL);
    
    return specular + rim * 2;
}

inline float3 ApplyDithering(float3 color, float2 screenPos)
{
	float noise = frac(sin(dot(screenPos, float2(41.512, 73.713))) * 59758.5453);
    return color + saturate((noise - 0.5) / 255.0);
}

inline float3 ACESFilm(float3 x)
{
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return saturate((x * (a * x + b)) / (x * (c * x + d) + e));
}

inline float3 Tonemap(float3 x)
{
	return x / (x + 1.0);
}

inline float4 ShadeDither(in float4 result, in float4 ScreenPosition)
{
	result.rgb = ApplyDithering(result.rgb, GetScreenTexCoords(ScreenPosition));
	return result;
}

inline float GetScattering(float3 start, float3 dir, float3 lightPos)
{
	float d = length(dir);
	float3 q = start - lightPos;
	float b = dot(dir / d, q);
	float c = dot(q, q);
	float s = 1.0f / sqrt(c - b*b);
	float l = s * (atan( (d + b) * s)  - atan( b*s ));
	return l;
}

inline float GetIntensity(float3 color)
{
    return dot(color, float3(0.2126, 0.7152, 0.0722));
}

inline float GetFade(float val, float near, float far)
{
	return min(1.0 - (val - near) / (far - near), 1.0);
}

float InterleavedGradientNoise(float2 screenPos)
{
    float3 magic = float3(0.06711056f, 0.00583715f, 52.9829189f);
    return frac(magic.z * frac(dot(screenPos, magic.xy)));
}

inline float3 GetBloomLuma(float3 color, float sensitivity)
{
	float luma = GetIntensity(color);

	color    = pow(abs(color), sensitivity);
	color /= max(luma, 0.001);
	luma = max(0.0, luma - 0.5f);
	color *= luma;

	color  = lerp(luma, color, 5);

	return saturate(color);
}

#ifdef D3D11
inline float2 ParallaxOcclusionMapping(Texture2D tHeightMap, SamplerState HeightMap, float2 texCoords, float3 viewDir, float VdotN)
#else
inline float2 ParallaxOcclusionMapping(sampler HeightMap, float2 texCoords, float3 viewDir, float VdotN)
#endif
{
	const float parallaxScale = 0.03;
    float2 parallaxDir = normalize(viewDir.xy);
    float parallaxLen = length(viewDir.xy) / abs(viewDir.z) * parallaxScale;
    
    int steps = (int)lerp(32, 8, abs(VdotN));
    float stepSize = 1.0 / steps;
    float2 texStep = parallaxDir * parallaxLen * stepSize;
    float2 currentTex = texCoords;
    float currBound = 1.0;
    
    float prevHeight = 1.0;
    float2 pt1 = 0, pt2 = 0;
    
	float height = 1.0;

    [loop]
    for(int i = 0; i < steps; i++)
    {
        height = Sample2DLod0(HeightMap, currentTex).r;

        if(height > currBound) break;
        
        prevHeight = height;
        currBound -= stepSize;
        currentTex -= texStep;
    }

    
    float nextH = height - currBound;
    float prevH = prevHeight - (currBound + stepSize);
    float weight = nextH / (nextH - prevH);

    return lerp(currentTex, currentTex + texStep, weight);
}