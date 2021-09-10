; ~ Texture Cache Constants
;[Block]
Const MapTexturesFolder$ = "GFX\map\textures\"
Const DeleteMapTextures% = 0
Const DeleteAllTextures% = 1
;[End Block]

Type TextureInCache
	Field Tex%
	Field TexName$
	Field TexDeleteType%
End Type

Function LoadTextureCheckingIfInCache(TexName$, TexFlags% = 1, DeleteType% = DeleteMapTextures)
	Local tic.TextureInCache
	Local CurrPath$
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexName <> "CreateTexture" Then
			If StripPath(TexName) = tic\TexName Then
				If tic\TexDeleteType < DeleteType Then
					tic\TexDeleteType = DeleteType
				EndIf
				Return(tic\Tex)
			EndIf
		EndIf
	Next
	
	CurrPath = TexName
	tic.TextureInCache = New TextureInCache
	tic\TexName = StripPath(TexName)
	tic\TexDeleteType = DeleteType
	If (Not tic\Tex) Then
		tic\Tex = LoadTexture(CurrPath, TexFlags)
	EndIf
	Return(tic\Tex)
End Function

Function LoadAnimTextureCheckingIfInCache(TexName$, TexFlags% = 1, Width%, Height%, FirstFrame%, Count%, DeleteType% = DeleteMapTextures)
	Local tic.TextureInCache
	Local CurrPath$
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexName <> "CreateTexture" Then
			If StripPath(TexName) = tic\TexName Then
				If tic\TexDeleteType < DeleteType Then
					tic\TexDeleteType = DeleteType
				EndIf
				Return(tic\Tex)
			EndIf
		EndIf
	Next
	
	CurrPath = TexName
	tic.TextureInCache = New TextureInCache
	tic\TexName = StripPath(TexName)
	tic\TexDeleteType = DeleteType
	If (Not tic\Tex) Then
		tic\Tex = LoadAnimTexture(CurrPath, TexFlags, Width, Height, FirstFrame, Count)
	EndIf
	Return(tic\Tex)
End Function

Function DeleteTextureEntriesFromCache(DeleteType%)
	Local tic.TextureInCache, mat.Materials
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexDeleteType =< DeleteType
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
	For mat.Materials = Each Materials
		mat\Diff = 0
		mat\Bump = 0
	Next
End Function

Function DeleteSingleTextureEntryFromCache(Texture%)
	Local tic.TextureInCache
	
	For tic.TextureInCache = Each TextureInCache
		If tic\Tex = Texture
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
End Function

Function CreateTextureUsingCacheSystem(Width%, Height%, TexFlags% = 1, Frames% = 1, DeleteType% = DeleteAllTextures)
	Local tic.TextureInCache
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = "CreateTexture"
	tic\TexDeleteType = DeleteType
	tic\Tex = CreateTexture(Width, Height, TexFlags + (256 * (opt\SaveTexturesInVRAM <> 0)), Frames)
	Return(tic\Tex)
End Function

Function IsTexAlpha%(Tex%, Name$ = "") ; ~ Detect transparency in textures
	Local mat.Materials
	Local Temp1s$
	Local Temp%, Temp2%
	
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

Function CheckForTexture%(Tex%, TexFlags% = 1)
	Local Name$ = ""
	Local Texture%
	
	If FileType(TextureName(Tex)) = 1 Then ; ~ Check if texture is existing in original path
		Name = TextureName(Tex)
	ElseIf FileType(MapTexturesFolder + StripPath(TextureName(Tex))) = 1 ; ~ If not, check the MapTexturesFolder
		Name = MapTexturesFolder + StripPath(TextureName(Tex))
	EndIf
	Texture = LoadTextureCheckingIfInCache(Name, TexFlags, DeleteMapTextures)
	If Texture <> 0 Then
		If ((TexFlags Shr 1) Mod 2) = 0 Then
			TextureBlend(Texture, 2 + (3 * opt\Atmosphere))
		Else
			TextureBlend(Texture, 1)
		EndIf
	EndIf
	Return(Texture)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D