; ~ Texture Cache Constants
;[Block]
Const MapTexturesFolder$ = "GFX\Map\Textures\"

Const DeleteMapTextures% = 0
Const DeleteAllTextures% = 1
;[End Block]

Type TextureInCache
	Field Tex%
	Field TexName$
	Field TexDeleteType%
End Type

Function LoadTextureCheckingIfInCache%(TexName$, TexFlags% = 1, DeleteType% = DeleteMapTextures, Scale# = 1.0)
	If TexName = "" Then Return(0)
	
	Local tic.TextureInCache
	Local StrippedName$ = StripPath(TexName)
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexName <> "CTUCS"
			If StrippedName = tic\TexName
				If tic\TexDeleteType < DeleteType Then tic\TexDeleteType = DeleteType
				Return(tic\Tex)
			EndIf
		EndIf
	Next
	
	Local CurrPath$ = StripAbsolutePath(TexName, "gfx\")
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = StrippedName
	tic\TexDeleteType = DeleteType
	If FileType(lang\LanguagePath + CurrPath) = 1 Then CurrPath = lang\LanguagePath + CurrPath
	tic\Tex = LoadTexture(CurrPath, TexFlags)
	If tic\Tex = 0
		tic\Tex = CreateTexture(1, 1, 1 + 256)
		TextureBlend(tic\Tex, 3)
		SetBuffer(TextureBuffer(tic\Tex))
		ClsColor(255, 0, 255)
		Cls()
		SetBuffer(BackBuffer())
;	Else
;		If Scale <> 1.0 Then tic\Tex = RescaleTexture(tic\Tex, Scale, Scale, TexFlags)
;		If opt\DisplayMode = 0 And TextureBuffer(tic\Tex) <> 0 Then BufferDirty(TextureBuffer(tic\Tex))
	EndIf
	Return(tic\Tex)
End Function

Function LoadAnimTextureCheckingIfInCache%(TexName$, TexFlags% = 1, Width%, Height%, FirstFrame%, Count%, DeleteType% = DeleteMapTextures)
	If TexName = "" Then Return(0)
	
	Local tic.TextureInCache
	Local StrippedName$ = StripPath(TexName)
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexName <> "CTUCS"
			If StrippedName = tic\TexName
				If tic\TexDeleteType < DeleteType Then tic\TexDeleteType = DeleteType
				Return(tic\Tex)
			EndIf
		EndIf
	Next
	
	Local CurrPath$ = StripAbsolutePath(TexName, "gfx\")
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = StrippedName
	tic\TexDeleteType = DeleteType
	If FileType(lang\LanguagePath + CurrPath) = 1 Then CurrPath = lang\LanguagePath + CurrPath
	tic\Tex = LoadAnimTexture(CurrPath, TexFlags, Width, Height, FirstFrame, Count)
	If tic\Tex = 0
		tic\Tex = CreateTexture(1, 1, 1 + 256)
		TextureBlend(tic\Tex, 3)
		SetBuffer(TextureBuffer(tic\Tex))
		ClsColor(255, 0, 255)
		Cls()
		SetBuffer(BackBuffer())
;	Else
;		If opt\DisplayMode = 0 And tic\Tex <> 0 And TextureBuffer(tic\Tex) <> 0 Then BufferDirty(TextureBuffer(tic\Tex))
	EndIf
	Return(tic\Tex)
End Function

Function DeleteTextureEntriesFromCache%(DeleteType%)
	Local tic.TextureInCache, mat.Materials
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexDeleteType <= DeleteType
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
End Function

Function DeleteSingleTextureEntryFromCache%(Texture%, DeleteType% = DeleteMapTextures)
	Local tic.TextureInCache
	
	For tic.TextureInCache = Each TextureInCache
		If tic\Tex = Texture And tic\TexDeleteType <= DeleteType
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
End Function

Function CreateTextureUsingCacheSystem%(Width%, Height%, TexFlags% = 1, Frames% = 1, DeleteType% = DeleteAllTextures)
	Local tic.TextureInCache
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = "CTUCS"
	tic\TexDeleteType = DeleteType
	tic\Tex = CreateTexture(Width, Height, TexFlags, Frames)
	Return(tic\Tex)
End Function

Function IsTexAlpha%(Tex%, Name$ = "") ; ~ Detect transparency in textures
	Local mat.Materials
	Local Temp1s$
	Local Temp%, Temp2%
	
	If Name = ""
		Temp1s = StripPath(TextureName(Tex))
	Else
		Temp1s = Name
	EndIf
	
	; ~ Texture is a lightmap
	If Instr(Temp1s, "_lm") <> 0 Then Return(2)
	
	For mat.Materials = Each Materials
		If mat\Name = Temp1s
			Temp = mat\IsDiffuseAlpha
			Temp2 = mat\UseMask
			Exit
		EndIf
	Next
	Return(1 + (2 * (Temp <> 0)) + (4 * (Temp2 <> 0)))
End Function

; ~ This is supposed to be the only texture that will be outside the TextureCache system
Global MissingTexture%

Function LoadMissingTexture%()
	MissingTexture = CreateTexture(1, 1, 1 + 256)
	TextureBlend(MissingTexture, 3)
	SetBuffer(TextureBuffer(MissingTexture))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
End Function

Function CheckForTexture%(Tex%, TexFlags% = 1)
	Local Name$ = ""
	Local TexName$ = TextureName(Tex)
	
	If FileType(TexName) = 1 ; ~ Check if texture is existing in original path
		Name = TexName
	ElseIf FileType(MapTexturesFolder + StripPath(TexName)) = 1 ; ~ If not, check the MapTexturesFolder
		Name = MapTexturesFolder + StripPath(TexName)
	EndIf
	
	Local Texture% = LoadTextureCheckingIfInCache(Name, TexFlags)
	
	If Texture <> 0 Then TextureBlend(Texture, 1 + 4 * (((TexFlags Shr 1) Mod 2) = 0))
	Return(Texture)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS