Type Materials
	Field Normal%
	Field Roughness%
	Field Emissive%
	Field ReactBlackout%
	Field Name$
	Field IsDiffuseAlpha%
	Field UseMask%
	Field StepSound%
End Type

Function GetMaterial.Materials(Texture%)
	Local mat.Materials
	Local Temp1s$
	
	Temp1s = StripPath(TextureName(Texture))
	
	For mat.Materials = Each Materials
		If mat\Name = Temp1s Then Return(mat)
	Next
	Return(Null)
End Function

Const MaterialsFile$ = "Data\materials.ini"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS