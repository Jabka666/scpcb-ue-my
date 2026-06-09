const float PI = 3.14159265358979323846;
const float Epsilon = 0.0001;
const float tubeWorldRadius = 0.05;

float3 GetSpecularDominantDir(float3 normal, float3 reflection, float roughness)
{
	const float smoothness = 1.0 - roughness;
	const float lerpFactor = smoothness * (sqrt(smoothness) + roughness);
	return lerp(normal, reflection, lerpFactor);
}

float D_GGX(float NdotH, float roughness)
{
    float a = roughness * roughness;
    float a2 = a * a;
    float d = (NdotH * a2 - NdotH) * NdotH + 1.0;
    return a2 / (PI * d * d);
}

float3 F_Schlick(float VdotH, float3 F0)
{
    float fc = pow(clamp(1.0 - VdotH, 0.0, 1.0), 5.0);
    return saturate(50.0 * F0.g) * fc + (1 - fc) * F0;
}

float3 Fd_Lambert(float3 diffuseColor)
{
    return diffuseColor / PI;
}

float V_SmithJointApprox(float NdotL, float NdotV, float roughness)
{
	float a = roughness * roughness;
    float visSmithV = NdotL * (NdotV * (1.0 - a) + a);
    float visSmithL = NdotV * (NdotL * (1.0 - a) + a);
    return 0.5 / (visSmithV + visSmithL + 1e-5);
}

float3 CalculatePBRLight(float3 lightVec, float3 lightColor, float3 viewDir, float3 normal, float3 diffuseColor, float3 F0, float roughness, float tubeLength = 0.0, float3 tubeDir = float3(0, 0, 0))
{
	float3 lightDir = normalize(lightVec);

	float NdotL = saturate(dot(normal, lightDir));
	float NdotV = saturate(dot(normal, viewDir));

	#ifdef TUBE
		float3 R = reflect(-viewDir, normal);

		float RdotTube = dot(R, tubeDir);
		float LdotTube = dot(lightVec, tubeDir);
		float RdotL    = dot(R, lightVec);

		float denom = 1.0 - RdotTube * RdotTube + 1e-5;
		float t = clamp((RdotTube * RdotL - LdotTube) / denom, -tubeLength, tubeLength);

		float3 specLightDir = normalize(lightVec + tubeDir * t);
		float3 H = normalize(viewDir + specLightDir);

		float lightDist = length(lightVec);
		float energyNorm = 1.0 / (1.0 + (tubeLength * 2.0) / (lightDist + 1e-5));

		float modifiedRoughness = saturate(roughness + tubeWorldRadius / (lightDist + 1e-5));

		float specNdotL = saturate(dot(normal, specLightDir));
	#else
		float3 H = normalize(viewDir + lightDir);
		float energyNorm = 1.0;
		float modifiedRoughness = roughness;
		float specNdotL = NdotL;
	#endif

	float NdotH = saturate(dot(normal, H));
	float VdotH = saturate(dot(viewDir, H));

	float D = D_GGX(NdotH, modifiedRoughness);
	float V = V_SmithJointApprox(specNdotL, NdotV, modifiedRoughness);
	float3 F = F_Schlick(VdotH, F0);

	float3 specular = D * V * F * energyNorm;

	float3 kD = 1.0 - F;
	float3 diffuse = kD * Fd_Lambert(diffuseColor);

	return (diffuse * NdotL + specular * specNdotL) * lightColor;
}

float3 EnvBRDFApprox (float3 specColor, float roughness, float ndv)
{
	const float4 c0 = float4(-1, -0.0275, -0.572, 0.022 );
	const float4 c1 = float4(1, 0.0425, 1.0, -0.04 );
	float4 r = roughness * c0 + c1;
	float a004 = min( r.x * r.x, exp2( -9.28 * ndv ) ) * r.x + r.y;
	float2 AB = float2( -1.04, 1.04 ) * a004 + r.zw;
	return max(specColor * AB.x + AB.y, 0);
}

float GetMipFromRoughness(float roughness)
{
	return (roughness * 12.0 - pow(roughness, 6.0) * 1.5);
}

#ifdef D3D11
float3 GetIBL(TextureCube tcubeMap, SamplerState cubeMap, float3 reflectVec, float3 normal, float3 viewDir, float3 diffColor, float3 specColor, float roughness, float3 ambient, float maxMip)
#else
float3 GetIBL(samplerCUBE cubeMap, float3 reflectVec, float3 normal, float3 viewDir, float3 diffColor, float3 specColor, float roughness, float3 ambient, float maxMip)
#endif
{
	float ndv = saturate(dot(-viewDir, normal))+ 1e-5f;
	
	const float3 environmentSpecular = EnvBRDFApprox(specColor, roughness, ndv);
	const float3 environmentDiffuse = EnvBRDFApprox(diffColor, 1.0, ndv);
	
	roughness = roughness * roughness;
	reflectVec = GetSpecularDominantDir(normal, reflectVec, roughness);
	float mipSelect = min(GetMipFromRoughness(roughness) * 4.0, maxMip);

	float3 cube = SRGBToLinear(SampleCubeLOD(cubeMap, float4(reflectVec, mipSelect)).rgb) * ambient;
	float3 cubeD = SRGBToLinear(SampleCubeLOD(cubeMap, float4(normal, maxMip)).rgb) * ambient;

	return cube * environmentSpecular + cubeD * environmentDiffuse;
}