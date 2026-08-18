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

Function LoadTextureCheckingIfInCache%(TexName$, TexFlags% = 1, DeleteType% = DeleteMapTextures)
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
	If TexFlags And 128
		tic\Tex = LoadCubeTexture(CurrPath, TexFlags)
	Else
		tic\Tex = LoadTexture(CurrPath, TexFlags)
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
	
	If Texture <> 0 Then TextureBlend(Texture, 1 + (((TexFlags Shr 1) Mod 2) = 0))
	Return(Texture)
End Function

Function LoadCubeTexture%(Tex$, Flags%)
	Local CubeMap% = LoadTexture(Tex, 1 + 16384)
	
	If CubeMap = 0 Then Return(0)
	
	Local CubeTexture% = 0
	Local Width% = TextureWidth(CubeMap)
	Local Divisor% = 1
	Local i%
	
	Select opt\TextureQuality
		Case 0
			;[Block]
			Divisor = 8
			;[End Block]
		Case 1
			;[Block]
			Divisor = 4
			;[End Block]
		Case 2
			;[Block]
			Divisor = 2
			;[End Block]
		Case 3
			;[Block]
			Divisor = 1
			;[End Block]
	End Select
	
	If TextureHeight(CubeMap) >= Width / 4 ; ~ Base cubemap
		Width = Width / 4
		CubeTexture = CreateTexture(Width / Divisor, Width / Divisor, Flags Or 8)
		SetCubeFace(CubeTexture, 0)
		CopyRectStretch(Width * 0, Width, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 1)
		CopyRectStretch(Width * 1, Width, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 2)
		CopyRectStretch(Width * 2, Width, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 3)
		CopyRectStretch(Width * 3, Width, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 4)
		CopyRectStretch(Width * 1, Width * 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 5)
		CopyRectStretch(Width * 1, Width * 2, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
	ElseIf TextureHeight(CubeMap) = Width / 6 ; ~ Cubemap in line
		Width = Width / 6
		CubeTexture = CreateTexture(Width / Divisor, Width / Divisor, Flags Or 8)
		SetCubeFace(CubeTexture, 0)
		CopyRectStretch(Width * 0, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 3)
		CopyRectStretch(Width * 1, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 2)
		CopyRectStretch(Width * 2, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 1)
		CopyRectStretch(Width * 3, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 4)
		CopyRectStretch(Width * 4, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		SetCubeFace(CubeTexture, 5)
		CopyRectStretch(Width * 5, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
	Else ; ~ 2D Texture, just copy same to all faces
		CubeTexture = CreateTexture(Width / Divisor, Width / Divisor, Flags Or 8)
		For i = 0 To 5
			SetCubeFace(CubeTexture, i)
			CopyRectStretch(0, 0, Width, Width, 0, 0, Width / Divisor, Width / Divisor, TextureBuffer(CubeMap), TextureBuffer(CubeTexture))
		Next
	EndIf
	
	FreeTexture(CubeMap) : CubeMap = 0
	Return(CubeTexture)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS