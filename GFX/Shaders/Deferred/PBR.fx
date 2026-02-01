const float PI = 3.14159265358979323846;
const float Epsilon = 0.0001;

float3 GetSpecularDominantDir(float3 normal, float3 reflection, float roughness)
{
	const float smoothness = 1.0 - roughness;
	const float lerpFactor = smoothness * (sqrt(smoothness) + roughness);
	return lerp(normal, reflection, lerpFactor);
}

float3 Fresnel(float3 specular, float VdotH, float LdotH)
{
	float ior = 0.25;
	float airIor = 1.000277;
	float f0 = (ior - airIor) / (ior + airIor);
	const float max_ior = 2.5;
	f0 = clamp(f0 * f0, 0.0, (max_ior - airIor) / (max_ior + airIor));
	return specular * (f0   + (1 - f0) * pow(2, (-5.55473 * LdotH - 6.98316) * LdotH));
}

float Visibility(float NdotL, float NdotV, float roughness)
{
	return NdotL * NdotV / max(1e-7, max(NdotL, NdotV));
}

float Distribution(float NdotH, float roughness)
{
	roughness *= roughness;
	float tmp =  (NdotH * roughness - NdotH) * NdotH + 1;
	return roughness / (tmp * tmp);
}

float3 Diffuse(float3 diffuseColor, float roughness, float NdotV, float NdotL, float VdotH)
{
	return diffuseColor * (1.0 / PI) * pow(NdotV, 0.5 + 0.3 * roughness);
}

float3 GetPBRLight(float rad, float3 worldPos, float3 lightVec, float3 normal, float3 toCamera, float roughness, float3 specColor, float3 diffColor, out float ndl)
{
	float specEnergy = 1.0f;

	float radius = rad / 100;
	float roughSqr = max(roughness, 0.08);
	roughSqr *= roughSqr;

	float radius2           = radius * radius;
	float distToLightSqrd   = dot(lightVec,lightVec);
	float invDistToLight    = rsqrt(distToLightSqrd);
	float sinAlphaSqr       = saturate(radius2 / distToLightSqrd);
	float sinAlpha          = sqrt(sinAlphaSqr);

	ndl       = dot(normal, (lightVec * invDistToLight));

	if(ndl < sinAlpha)
	{
		ndl = max(ndl, -sinAlpha);
		ndl = ((sinAlpha + ndl) * (sinAlpha + ndl)) / (4 * sinAlpha);
	}

	float sphereAngle = saturate(radius * invDistToLight);
						
	specEnergy = roughSqr / (roughSqr + 0.5f * sphereAngle);
	specEnergy *= specEnergy;                           

	float3 R = 2 * dot(toCamera, normal) * normal - toCamera;
	R = GetSpecularDominantDir(normal, R, roughness);

	// Find closest point on sphere to ray
	float3 closestPointOnRay = dot(lightVec, R) * R;
	float3 centerToRay = closestPointOnRay - lightVec;
	float invDistToRay = rsqrt(dot(centerToRay, centerToRay));
	float3 closestPointOnSphere = lightVec + centerToRay * saturate(radius * invDistToRay);

	lightVec = closestPointOnSphere;
	float3 L = normalize(lightVec);

	float3 h = normalize(toCamera + L);
	float hdn = saturate(dot(h, normal));
	float hdv = dot(h, toCamera);
	float ndv = saturate(dot(normal, toCamera));
	float hdl = saturate(dot(h, L));
	ndl = max(ndl, 0.0);

	const float3 diffuseFactor = Diffuse(diffColor, roughness, ndv, ndl, hdv)  * ndl;
	const float3 fresnelTerm = Fresnel(specColor, hdv, hdl) ;
	const float distTerm = Distribution(hdn, roughness);
	const float visTerm = Visibility(ndl, ndv, roughness);
	float3 specularFactor = distTerm * visTerm * fresnelTerm * ndl / PI;
	return diffuseFactor + specularFactor;
}

float3 GetBRDF(float radius, float3 worldPos, float3 lightVec, float3 toCamera, float3 normal, float roughness, float3 diffColor, float3 specColor)
{
	float ndl = clamp((dot(normal, lightVec)), Epsilon, 1.0);
	return GetPBRLight(radius, worldPos, lightVec, normal, toCamera, roughness, specColor, diffColor, ndl);
}

float3 EnvBRDFApprox (float3 specColor, float roughness, float ndv)
{
	const float4 c0 = float4(-1, -0.0275, -0.572, 0.022 );
	const float4 c1 = float4(1, 0.0425, 1.0, -0.04 );
	float4 r = roughness * c0 + c1;
	float a004 = min( r.x * r.x, exp2( -9.28 * ndv ) ) * r.x + r.y;
	float2 AB = float2( -1.04, 1.04 ) * a004 + r.zw;
	return specColor * AB.x + AB.y;
}

float GetMipFromRoughness(float roughness)
{
	return (roughness * 12.0 - pow(roughness, 6.0) * 1.5);
}

#ifdef D3D11
float3 GetIBL(TextureCube tcubeMap, SamplerState cubeMap, TextureCube tblendcubeMap, SamplerState blendcubeMap, float blendfactor, float3 reflectVec, float3 normal, float3 viewDir, float3 diffColor, float3 specColor, float roughness, float3 ambient)
#else
float3 GetIBL(samplerCUBE cubeMap, samplerCUBE blendcubeMap, float blendfactor, float3 reflectVec, float3 normal, float3 viewDir, float3 diffColor, float3 specColor, float roughness, float3 ambient)
#endif
{
	roughness = max(roughness, 0.001);
	reflectVec = GetSpecularDominantDir(normal, reflectVec, roughness);
	float ndv = saturate(dot(-viewDir, normal));
	float mipSelect = GetMipFromRoughness(roughness) * 4.0;
	
	float3 src = SampleCubeLOD(blendcubeMap, float4(reflectVec, mipSelect)).rgb;
	float3 srcD = SampleCubeLOD(blendcubeMap, float4(normal, 9.0)).rgb;
	
	float3 dest = SampleCubeLOD(cubeMap, float4(reflectVec, mipSelect)).rgb;
	float3 destD = SampleCubeLOD(cubeMap, float4(normal, 9.0)).rgb;
	
	float3 cube = lerp(src, dest, blendfactor);
	float3 cubeD = lerp(srcD, destD, blendfactor);
	

	float3 brightness = clamp(ambient, 0.0, 1.0);
	float3 darknessCutoff = clamp((ambient - 1.0) * 0.1, 0.0, 0.25);

	float hdrMaxBrightness = 5.0;
	float3 hdrCube = pow(cube + darknessCutoff, max(1.0, ambient));
	hdrCube += max(0.0, hdrCube - 1.0) * hdrMaxBrightness;

	float3 hdrCubeD = pow(cubeD + darknessCutoff, max(1.0, ambient));
	hdrCubeD += max(0.0, hdrCubeD - 1.0) * hdrMaxBrightness;

	const float3 environmentSpecular = EnvBRDFApprox(specColor, roughness, ndv);
	const float3 environmentDiffuse = EnvBRDFApprox(diffColor, 1.0, ndv);

	return pow((hdrCube * environmentSpecular + hdrCubeD * environmentDiffuse) * brightness, 0.707);
}