Const MAX_BRUSH_TEXTURES% = 8
Const MATERIAL_NORMAL% = 1
Const MATERIAL_ROUGHNESS% = 2
Const MATERIAL_EMISSIVE% = 3
Const MATERIAL_ENVMAP% = 4
Const MATERIAL_HEIGHTMAP% = 5

Type Materials
	Field Name$
	Field IsDiffuseAlpha%
	Field UseMask%
	Field StepSound%
	
	Field IsAnimated%, TexWidth%, TexHeight%, FirstFrame%, Count%
	
	Field TextureFile$[MAX_BRUSH_TEXTURES]
	Field Texture%[MAX_BRUSH_TEXTURES]
	Field Loaded%
	
	Field ReactBlackout%
	Field Roughness#, Metallic#, RMSpecified%
	Field FakeCurve%
End Type

Function LoadMaterial%(File$, Loc$)
	Local StrTemp$
	
	If (Not IniSectionExist(File, Loc)) Then Loc = Lower(Loc)
	
	If IniSectionExist(File, Loc)
		Local LowerLoc$ = Lower(Loc)
		Local mat.Materials = Null
		Local m.Materials
		
		For m.Materials = Each Materials
			If m\Name = LowerLoc
				mat = m
				Exit
			EndIf
		Next
		
		If mat = Null
			mat.Materials = New Materials
			mat\Name = LowerLoc
		EndIf
		
		Local IsAnimated$ = IniGetString(File, Loc, "animated")
		
		If IsAnimated <> ""
			mat\IsAnimated = True
			mat\TexWidth = Int(Piece(IsAnimated, 1, "|"))
			mat\TexHeight = Int(Piece(IsAnimated, 2, "|"))
			mat\FirstFrame = Int(Piece(IsAnimated, 3, "|"))
			mat\Count = Int(Piece(IsAnimated, 4, "|"))
		EndIf
		
		mat\TextureFile[MATERIAL_NORMAL] = IniGetString(File, Loc, "normal")
		mat\TextureFile[MATERIAL_ROUGHNESS] = IniGetString(File, Loc, "roughmetalmap")
		mat\TextureFile[MATERIAL_EMISSIVE] = IniGetString(File, Loc, "emissive")
		mat\TextureFile[MATERIAL_ENVMAP] = IniGetString(File, Loc, "envmap")
		mat\TextureFile[MATERIAL_HEIGHTMAP] = IniGetString(File, Loc, "heightmap")
		
		mat\RMSpecified = (IniKeyExist(File, Loc, "roughness") Lor IniKeyExist(File, Loc, "metallic"))
		mat\Roughness = IniGetFloat(File, Loc, "roughness")
		mat\Metallic = IniGetFloat(File, Loc, "metallic")
		mat\ReactBlackout = IniGetInt(File, Loc, "reactblackout") <> 0
		mat\StepSound = IniGetInt(File, Loc, "stepsound")
		mat\IsDiffuseAlpha = IniGetInt(File, Loc, "transparent") <> 0
		mat\UseMask = IniGetInt(File, Loc, "masked") <> 0
		mat\FakeCurve = IniGetInt(File, Loc, "fakenormals") <> 0
	EndIf
End Function

Function LoadMaterialTextures%(mat.Materials)
	If mat\Loaded Then Return
	
	Local i%
	
	For i = MATERIAL_NORMAL To MATERIAL_HEIGHTMAP ; ~ Active textures
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
			If mat\TextureFile[Index] = "" Then mat\Texture[Index] = GlobalEnvironmentMap
			;[End Block]
	End Select
	
	If mat\Texture[Index] = 0 And mat\TextureFile[Index] <> ""
		If mat\IsAnimated And Index <> MATERIAL_ENVMAP
			mat\Texture[Index] = LoadAnimTexture_Strict(mat\TextureFile[Index], 1, mat\TexWidth, mat\TexHeight, mat\FirstFrame, mat\Count, DeleteAllTextures)
		Else
			mat\Texture[Index] = LoadTexture_Strict(mat\TextureFile[Index], 1 + (128 * (Index = MATERIAL_ENVMAP)), DeleteAllTextures)
		EndIf
	EndIf
	
	If mat\Texture[Index] <> 0 Then TextureBlend(mat\Texture[Index], 0)
End Function

Function GetMaterial.Materials(Texture%)
	Local mat.Materials
	Local Temp1s$ = Lower(StripPath(TextureName(Texture)))
	
	For mat.Materials = Each Materials
		If mat\Name = Temp1s Then Return(mat)
	Next
	Return(Null)
End Function

Const MaterialsFile$ = "Data\materials.ini"

;~IDEal Editor Parameters:
;~C#Blitz3D TSS