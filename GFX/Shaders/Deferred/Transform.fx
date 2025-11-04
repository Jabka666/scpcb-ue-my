//--------------------------------------------------------------------------
//	THIS FILE IS A PRIVATE PROPERTY OF EUCLID LABS STUDIO.
//	THIS SHADER MAY NOT BE USED IN ANY PROJECTS
//	WITHOUT THE EXPLICIT PERMISSION OF THE RIGHTS HOLDER. ANY USE OF THESE FILES
//	IN YOUR PROJECTS REQUIRES PRIOR AGREEMENT WITH THE RIGHTS HOLDER.
//	YOU CAN CONTACT US BY MAILING US ON EUCLIDLABSSTUDIO@GMAIL.COM.
//--------------------------------------------------------------------------

bool Skinned			: SKINNED;
float4x3 BonesMatrices[MAX_BONES] : BONE_MATRICES;

float4x3 GetSkinTransform(float4 indices, float4 weights)
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