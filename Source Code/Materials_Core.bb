Type Materials
	Field Name$
	Field Bump%
	Field IsDiffuseAlpha%
	Field UseMask%
	Field StepSound%
End Type

Function ApplyBumpMap%(Texture%)
	TextureBlend(Texture, 6)
	TextureBumpEnvMat(Texture, 0, 0, -0.012)
	TextureBumpEnvMat(Texture, 0, 1, -0.012)
	TextureBumpEnvMat(Texture, 1, 0, 0.012)
	TextureBumpEnvMat(Texture, 1, 1, 0.012)
	TextureBumpEnvOffset(Texture, 0.5)
	TextureBumpEnvScale(Texture, 1.0)
End Function

Const MaterialsFile$ = "Data\materials.ini"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS