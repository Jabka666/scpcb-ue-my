; ~ Safe loads for MAV trapping media issues

; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists.

Function LoadMesh_Strict%(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, t2%, Texture%
	Local TexAlpha% = 0
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "mesh.notfound"), File))
		Tmp = LoadMesh(File, Parent)
		If Tmp = 0 Then RuntimeError(Format(GetLocalString("runerr", "mesh.failed.load"), File))
	EndIf
	
	Local SurfCount% = CountSurfaces(Tmp)
	
	For i = 1 To SurfCount
		SF = GetSurface(Tmp, i)
		b = GetSurfaceBrush(SF)
		If b <> 0
			Texture = 0
			t1 = 0 : t2 = 0
			t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
			If t1 <> 0
				TexAlpha = IsTexAlpha(t1)
				If TexAlpha <> 2
					Texture = CheckForTexture(t1, TexAlpha)
					If Texture <> 0
						BrushTexture(b, Texture, 0, 0)
						DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
					Else
						; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
				Else
					Texture = CheckForTexture(t1, 1)
					If Texture <> 0
						TextureCoords(Texture, 0)
						BrushTexture(b, Texture, 0, 1)
						DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
					Else
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
					
					t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if Lightmap is existing)
					If t2 <> 0
						Texture = CheckForTexture(t2, TexAlpha)
						If Texture <> 0
							TextureCoords(Texture, 1)
							BrushTexture(b, Texture, 0, 0)
							DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
						Else
							BrushTexture(b, MissingTexture, 0, 1)
						EndIf
						FreeTexture(t2) : t2 = 0
					EndIf
				EndIf
				PaintSurface(SF, b)
				FreeTexture(t1) : t1 = 0
			EndIf
			FreeBrush(b) : b = 0
		EndIf
	Next
	Return(Tmp)
End Function

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict%(File$, Flags% = 1, TexDeleteType% = DeleteMapTextures)
	Local Tmp%
	
	If FileType(LanguagePath + File) = 1 Then File = LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "texture.notfound"), File))
		Tmp = LoadTextureCheckingIfInCache(File, Flags, TexDeleteType)
		If Tmp = 0 Then RuntimeError(Format(GetLocalString("runerr", "texture.failed.load"), File))
	EndIf
	Return(Tmp) 
End Function

Function LoadFont_Strict%(File$, Height% = 13)
	Local Tmp%
	
	If FileType(LanguagePath + File) = 1 Then File = LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "font.notfound"), File))
		Tmp = LoadFont(File, Height)
		If Tmp = 0 Then RuntimeError(Format(GetLocalString("runerr", "font.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Function LoadImage_Strict%(File$)
	Local Tmp%
	
	If FileType(LanguagePath + File) = 1 Then File = LanguagePath + File
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "image.notfound"), File))
		Tmp = LoadImage(File)
		If Tmp = 0 Then RuntimeError(Format(GetLocalString("runerr", "image.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS