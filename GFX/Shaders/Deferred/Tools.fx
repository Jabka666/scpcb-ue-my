//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

float4 ViewportSize		: VIEWPORT_SIZE;
static const float2 ScreenSize = ViewportSize.zw;

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
#define OUTPUT(n) SV_Target##n
#define OUT_POSITION SV_Position
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
static const float2 halfPixel = 0.5 / ScreenSize;
#define Vertex(VS) VertexShader = compile vs_3_0 VS()
#define Pixel(PS) PixelShader = compile ps_3_0 PS()
#define OUTPUT(n) COLOR##n
#define OUT_POSITION POSITION
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

inline float3 LinearToSRGB(float3 color)
{
	color = saturate(color);
	
    float3 sq1 = sqrt(color);
    float3 sq2 = sqrt(sq1);
    float3 sq3 = sqrt(sq2);

    return 0.662002687f * sq1 + 0.684122060f * sq2 - 0.323583601f * sq3 - 0.0225411470f * color;
}

inline float4 LinearToSRGB(float4 value)
{
    return float4(LinearToSRGB(value.rgb), value.a);
}

inline float3 SRGBToLinear(float3 value)
{
    return value * (value * (value * 0.305306011f + 0.682171111f) + 0.012522878f);
}

inline float4 SRGBToLinear(float4 value)
{
    return float4(SRGBToLinear(value.rgb), value.a);
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
	float luma = GetIntensity(LinearToSRGB(Tonemap(color)));

	color    = pow(abs(color), sensitivity);
	color /= max(luma, 0.001);
	luma = max(0.0, luma - 0.5f);
	color *= luma;

	color  = lerp(luma, color, 5);

	return saturate(color);
}

#ifdef D3D11
inline float2 ParallaxOcclusionMapping(Texture2D tHeightMap, SamplerState HeightMap, float2 texCoords, float3 viewDir, float VdotN, float2 dx, float2 dy)
#else
inline float2 ParallaxOcclusionMapping(sampler HeightMap, float2 texCoords, float3 viewDir, float VdotN, float2 dx, float2 dy)
#endif
{
	const float parallaxScale = 0.025;
	
	const float minZ = 0.05; 
    float2 parallaxDir = (viewDir.xy / max(abs(viewDir.z), minZ)) * parallaxScale;

	int steps = (int)lerp(48, 8, abs(VdotN));
	float stepSize = 1.0 / (float)steps;
	float2 texStep = parallaxDir * stepSize;

	float2 currentTex = texCoords;
	float currBound = 1.0;
	float prevHeight = 1.0;
	float height = 1.0;

	[loop]
	for(int i = 0; i < steps; i++)
	{
		height = Sample2DGrad(HeightMap, currentTex, dx, dy).r;

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

inline float3 BoxProject(float3 RayDir, float3 WorldPos, float4x3 InvWorldMatrix, in float4x3 WorldMatrix)
{
    float3 localDir = mul(RayDir, (float3x3)InvWorldMatrix);
    float3 localPos = mul(float4(WorldPos, 1.0), InvWorldMatrix).xyz;

    float3 firstPlane = (0.5 - localPos) / localDir;
    float3 secondPlane = (-0.5 - localPos) / localDir;
    float3 furthestPlane = max(firstPlane, secondPlane);
    float dist = min(min(furthestPlane.x, furthestPlane.y), furthestPlane.z);

    float3 localIntersect = localPos + localDir * dist;

    return mul(localIntersect, (float3x3)WorldMatrix);
}

static const float4x4 DITHER_PATTERN = float4x4
(float4(0.0f, 0.5f, 0.125f, 0.625f),
 float4(0.75f, 0.22f, 0.875f, 0.375f),
 float4(0.1875f, 0.6875f, 0.0625f, 0.5625f),
 float4(0.9375f, 0.4375f, 0.8125f, 0.3125f));

inline float ComputeScattering(float mie, float force, float lightDotView)
{
    const float PI = 3.14159265358979323846;
    float g2 = mie * mie;
    float x = 1.0f + g2 - (2.0f * mie) * lightDotView;
    return (1.0f - g2) / (force * PI * x * sqrt(max(x, 0.00001f)));
}

inline float4 Hash4(float4 x, float4 y, float4 z)
{
    float4 p_x = frac(x * 0.1031);
    float4 p_y = frac(y * 0.1031);
    float4 p_z = frac(z * 0.1031);

    float4 dot_val = p_x * (p_y + 33.33) + p_y * (p_z + 33.33) + p_z * (p_x + 33.33);
    
    p_x += dot_val;
    p_y += dot_val;
    p_z += dot_val;
    
    return frac((p_x + p_y) * p_z);
}

float DustNoise(float3 p, float time)
{
    p += time + float3(time * 0.1, time * 0.12, time * 0.1);

    float3 i = floor(p);
    float3 f = frac(p);
    f = f * f * (3.0 - 2.0 * f);

    float4 ix = i.x + float4(0.0, 1.0, 0.0, 1.0);
    float4 iy = i.y + float4(0.0, 0.0, 1.0, 1.0);

    float4 h0 = Hash4(ix, iy, i.z);
    float4 h1 = Hash4(ix, iy, i.z + 1.0);

    float2 lerpX_0 = lerp(float2(h0.x, h0.z), float2(h0.y, h0.w), f.x);
    float2 lerpX_1 = lerp(float2(h1.x, h1.z), float2(h1.y, h1.w), f.x);

    float lerpY_0 = lerp(lerpX_0.x, lerpX_0.y, f.y);
    float lerpY_1 = lerp(lerpX_1.x, lerpX_1.y, f.y);

    return lerp(lerpY_0, lerpY_1, f.z);
}