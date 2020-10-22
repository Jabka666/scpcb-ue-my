Const MapTexturesFolder$ = "GFX\map\textures\"

Function GetTextureFromCache%(Name$)
	Local tc.Materials
	
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc\Diff)
	Next
	Return(0)
End Function

Function GetBumpFromCache%(Name$)
	Local tc.Materials
	
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc\Bump)
	Next
	Return(0)
End Function

Function GetCache.Materials(Name$)
	Local tc.Materials
	
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc)
	Next
	Return(Null)
End Function

Function AddTextureToCache(Texture%)
	Local tc.Materials = GetCache(StripPath(TextureName(Texture)))
	
	If tc.Materials = Null Then
		tc.Materials = New Materials
		If FileType(TextureName(Texture)) = 1 Then
			tc\Name = StripPath(TextureName(Texture))
		ElseIf FileType(MapTexturesFolder + StripPath(TextureName(Texture))) = 1
			tc\Name = MapTexturesFolder + StripPath(TextureName(Texture))
		EndIf
		tc\Diff = 0
	EndIf
	If tc\Diff = 0 Then tc\Diff = Texture
End Function

Function ClearTextureCache()
	Local tc.Materials
	
	For tc.Materials = Each Materials
		If tc\Diff <> 0 Then FreeTexture(tc\Diff)
		If tc\Bump <> 0 Then FreeTexture(tc\Bump)
		Delete(tc)
	Next
End Function

Function FreeTextureCache()
	Local tc.Materials
	
	For tc.Materials = Each Materials
		If tc\Diff <> 0 Then FreeTexture(tc\Diff)
		If tc\Bump <> 0 Then FreeTexture(tc\Bump)
		tc\Diff = 0 : tc\Bump = 0
	Next
End Function

Function IsTexAlpha%(Tex%, Name$ = "") ; ~ Detect transparency in textures
	Local Temp1s$
	Local Temp%, Temp2%
	Local mat.Materials
	
	If Name = "" Then
		Temp1s = StripPath(TextureName(Tex))
	Else
		Temp1s = Name
	EndIf
	
	If Instr(Temp1s, "_lm") <> 0 Then ; ~ Texture is a lightmap
		Return(2)
	EndIf
	
	For mat.Materials = Each Materials
		If mat\Name = Temp1s Then
			Temp = mat\IsDiffuseAlpha
			Temp2 = mat\UseMask
			Exit
		EndIf
	Next
	Return(1 + (2 * (Temp <> 0)) + (4 * (Temp2 <> 0)))
End Function

; ~ This is supposed to be the only texture that will be outside the TextureCache system
Global MissingTexture%

Function LoadMissingTexture()
	MissingTexture = CreateTexture(2, 2, 1)
	TextureBlend(MissingTexture, 3)
	SetBuffer(TextureBuffer(MissingTexture))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
End Function

Function CheckForTexture(Tex%, TexFlags% = 1)
	Local Name$ = ""
	Local Texture%
	
	If FileType(TextureName(Tex)) = 1 Then ; ~ Check if texture is existing in original path
		Name = TextureName(Tex)
	ElseIf FileType(MapTexturesFolder + StripPath(TextureName(Tex))) = 1 ; ~ If not, check the MapTexturesFolder
		Name = MapTexturesFolder + StripPath(TextureName(Tex))
	EndIf
	Texture = LoadTexture(Name, TexFlags)
	If Texture <> 0 Then
		If ((TexFlags Shr 1) Mod 2) = 0 Then
			TextureBlend(Texture, 5)
		Else
			TextureBlend(Texture, 1)
		EndIf
	EndIf
	Return(Texture)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D