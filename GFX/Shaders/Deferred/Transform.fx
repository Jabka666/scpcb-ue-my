//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

bool Skinned						: SKINNED;
float4x3 BonesMatrices[MAX_BONES] 	: BONE_MATRICES;

inline float4x3 GetSkinTransform(uint4 indices, float4 weights)
{
	int	Matrices[4]        	= {indices.x,indices.y,indices.z,indices.w};
    float BlendWeights[4] 	= (float[4])weights;

	if(Matrices[0] == 255) return World;
	
	float4x3 Mat = 0;
	
	for(int i = 0; i < 4; i++)
	{
		const int ind = Matrices[i];
		if(ind == 255) break;
		Mat += BonesMatrices[ind] * BlendWeights[i];
	}
	
	return Mat;
}

inline float4x3 GetInstanceTransform(float4 M1, float4 M2, float4 M3)
{
	float4x3 Mat;
	Mat[0] = float3(M1.x, M2.x, M3.x);
	Mat[1] = float3(M1.y, M2.y, M3.y);
	Mat[2] = float3(M1.z, M2.z, M3.z);
	Mat[3] = float3(M1.w, M2.w, M3.w);
	return Mat;
}