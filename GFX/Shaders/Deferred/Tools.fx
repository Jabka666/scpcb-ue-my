//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

#define Sample2D(t, uv) tex2D(t, uv)
#define Sample2DProj(t, uv) tex2Dproj(t, uv)
#define SampleCube(t, uv) texCUBE(t, uv)
#define Sample2DLod0(t, uv) tex2Dlod(t, float4(uv, 0.0, 0.0))

#define MAX_BONES 79

float4x3 World 			: MATRIX_WORLD; 
float4x4 WorldViewProj 	: MATRIX_WORLDVIEWPROJ;
float4x4 ViewProj 		: MATRIX_VIEWPROJ; 
float2 ScreenSize 		: SCREEN_SIZE;

static const float2 halfPixel = float2(0.5 / ScreenSize.x, 0.5 / ScreenSize.y);

struct VS_INPUT
{ 
	float4 Pos : POSITION; 
	float3 Normal : NORMAL; 
	float4 Color : COLOR; 
	float2 TexCoords : TEXCOORD0;
};

inline float3 GetWorldPosition(float2 Coords, float Depth, in float4x4 ivpmat)
{
	float4 WorldPos = float4(Coords.x * 2.0f - 1.0f,  -(Coords.y * 2.0f - 1.0f), Depth, 1.0f);
	WorldPos 	= mul(WorldPos, ivpmat);
	WorldPos 	/= WorldPos.w;
	return WorldPos.xyz;
}

inline float2 GetScreenTexCoords(float4 ScreenCoords)
{
	ScreenCoords.xy /= ScreenCoords.w;
	return 0.5f * (float2(ScreenCoords.x, -ScreenCoords.y) + 1.0f);
}

inline float GetSpecular(float3 normal, float3 eyevec, float3 lightDir, float specularPower)
{
	const float spec = specularPower * 10.0;
	float3 V = normalize(eyevec);
    float3 halfVec = normalize(V + lightDir);
    float specular = saturate(pow(dot(normal, halfVec), spec));
    
    // Fresnel
    float NdotV = max(0.0, dot(normal, V));
	const float fresnelIntensity = 0.017;
    float fresnel = pow(1.0 - NdotV, 4.0) * fresnelIntensity;
    fresnel *= (spec + 0.25);
    return specular + fresnel;
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

inline float4 ACESFilm(float4 x)
{
    float a = 2.51;
    float b = 0.03;
    float c = 2.43;
    float d = 0.59;
    float e = 0.14;
    return saturate((x * (a * x + b)) / (x * (c * x + d) + e));
}

inline float4 ShadeDither(in float4 result, in float4 ScreenPosition)
{
	result.rgb = ApplyDithering(result, GetScreenTexCoords(ScreenPosition));
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
    return dot(color, float3(0.299, 0.587, 0.114));
}

inline float GetFade(float val, float near, float far)
{
	return min(1.0 - (val - near) / (far - near), 1.0);
}

inline float GetBloomLuma(float3 color, float threshold)
{
	float luma = GetIntensity(color);
	// Soft Threshold
	const float softThreshold = 0.5f;
	float knee = threshold * softThreshold;
	float soft = luma - threshold + knee;
	soft = clamp(soft, 0.0, 2.0 * knee);
	soft = soft * soft / (4.0 * knee + 0.00001);

	float contribution = max(soft, luma - threshold);
	contribution /= max(luma, 0.00001);
	return contribution;
}