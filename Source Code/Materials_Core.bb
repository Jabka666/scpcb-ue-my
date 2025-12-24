Const MAX_BRUSH_TEXTURES% = 8
Const MATERIAL_NORMAL% = 1
Const MATERIAL_ROUGHNESS% = 2
Const MATERIAL_EMISSIVE% = 3
Const MATERIAL_ENVMAP% = 4

Type Materials
	Field IsAnimated%, TexWidth%, TexHeight%, FirstFrame%, Count%
	Field TextureFile$[MAX_BRUSH_TEXTURES]
	Field Texture%[MAX_BRUSH_TEXTURES]
	Field Loaded%
	Field EnvMapType%, EnvMapAdditive%, EnvMapGlobal%
	Field SpecIntensity#, SpecPower#
	Field ReactBlackout%
	Field Name$
	Field IsDiffuseAlpha%
	Field UseMask%
	Field StepSound%
End Type

Function LoadMaterial%(File$, Loc$)
	Local StrTemp$
	
	If (Not IniSectionExist(File, Loc)) Then Loc = Lower(Loc)
	
	If IniSectionExist(File, Loc)
		Local mat.Materials
		
		mat.Materials = New Materials
		mat\Name = Lower(Loc)
		
		Local IsAnimated$ = IniGetString(File, Loc, "animated")
		
		If IsAnimated <> ""
			mat\IsAnimated = True
			mat\TexWidth = Int(Piece(IsAnimated, 1, "|"))
			mat\TexHeight = Int(Piece(IsAnimated, 2, "|"))
			mat\FirstFrame = Int(Piece(IsAnimated, 3, "|"))
			mat\Count = Int(Piece(IsAnimated, 4, "|"))
		EndIf
		
		mat\TextureFile[MATERIAL_NORMAL] = IniGetString(File, Loc, "normal")
		mat\TextureFile[MATERIAL_ROUGHNESS] = IniGetString(File, Loc, "roughness")
		mat\TextureFile[MATERIAL_EMISSIVE] = IniGetString(File, Loc, "emissive")
		mat\TextureFile[MATERIAL_ENVMAP] = IniGetString(File, Loc, "envmap")
		
		mat\EnvMapType = IniGetInt(File, Loc, "envmaptype", 0)
		mat\EnvMapAdditive = IniGetInt(File, Loc, "envmapadd", 0)
		mat\EnvMapGlobal = IniGetInt(File, Loc, "envmapglobal", 0)
		mat\SpecIntensity = IniGetFloat(File, Loc, "specintensity")
		mat\SpecPower = IniGetFloat(File, Loc, "specpower")
		mat\ReactBlackout = IniGetInt(File, Loc, "reactblackout")
		mat\StepSound = IniGetInt(File, Loc, "stepsound")
		mat\IsDiffuseAlpha = IniGetInt(File, Loc, "transparent")
		mat\UseMask = IniGetInt(File, Loc, "masked")
	EndIf
End Function

Function LoadMaterialTextures%(mat.Materials)
	If mat\Loaded Then Return
	
	Local i%
	
	For i = MATERIAL_NORMAL To MATERIAL_ENVMAP ; ~ Active textures
		LoadMaterialTexture(mat, i)
	Next
	mat\Loaded = True
End Function

Function HasMaterialTexture%(mat.Materials, Index%)
	Return(mat\Texture[Index] <> 0)
End Function

Function GetMaterialTexture%(mat.Materials, Index%)
	If mat\Texture[Index] = 0 Then Return(MissingTexture)
	Return(mat\Texture[Index])
End Function

Function LoadMaterialTexture%(mat.Materials, Index%)
	; ~ Customized texture
	Select Index
		Case MATERIAL_ENVMAP
			;[Block]
			If mat\EnvMapGlobal Then mat\Texture[Index] = GetGlobalReflections()
			;[End Block]
	End Select
	
	If mat\Texture[Index] = 0 And mat\TextureFile[Index] <> ""
		If mat\IsAnimated
			mat\Texture[Index] = LoadAnimTexture_Strict(mat\TextureFile[Index], 1, mat\TexWidth, mat\TexHeight, mat\FirstFrame, mat\Count, DeleteAllTextures)
		Else
			mat\Texture[Index] = LoadTexture_Strict(mat\TextureFile[Index], 1, DeleteAllTextures)
		EndIf
	EndIf
	
	If mat\Texture[Index] <> 0 Then TextureBlend(mat\Texture[Index], 0)
End Function

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