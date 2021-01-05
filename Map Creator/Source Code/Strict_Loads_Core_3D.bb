; ~ Safe loads for MAV trapping media issues

; ~ Basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
; ~ More informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
; ~ Likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
; ~ Added zero checks since blitz load functions return zero sometimes even if the filetype exists.

Function LoadMesh_Strict(File$, Parent% = 0)
	Local Tmp%, i%, SF%, b%, t1%, t2%, Texture%
	Local TexAlpha% = 0
	
	If Tmp = 0 Then
		If FileType(File) <> 1 Then RuntimeError("3D Mesh " + File + " not found.")
		Tmp = LoadMesh(File, Parent)
		If Tmp = 0 Then RuntimeError("Failed to load 3D Mesh: " + File)
	EndIf
	
	For i = 1 To CountSurfaces(Tmp)
		SF = GetSurface(Tmp, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			Texture = 0
			t1 = 0 : t2 = 0
			t1 = GetBrushTexture(b, 0) ; ~ Diffuse or Lightmap
			If t1 <> 0 Then
				TexAlpha = IsTexAlpha(t1)
				If TexAlpha <> 2 Then
					Texture = CheckForTexture(t1, TexAlpha)
					If Texture <> 0 Then 
						BrushTexture(b, Texture, 0, 0)
					Else
						; ~ Sometimes that error is intentional - such as if the mesh doesn't has a texture applied or an invalid one which gets fixed by something like EntityTexture
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
				Else
					t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if lightmap is existing)
					Texture = CheckForTexture(t1, TexAlpha)
					If Texture <> 0 Then
						TextureCoords(Texture, 1)
						BrushTexture(b, Texture, 0, 0)
					Else
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
					
					Texture = CheckForTexture(t2, TexAlpha)
					If Texture <> 0 Then
						TextureCoords(Texture, 0)
						BrushTexture(b, Texture, 0, 1)
					Else
						BrushTexture(b, MissingTexture, 0, 1)
					EndIf
					FreeTexture(t2)
				EndIf
				PaintSurface(SF, b)
				FreeTexture(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
	Return(Tmp)
End Function

; ~ Don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$, Flags% = 1, TexDeleteType% = DeleteMapTextures)
	Local Tmp%
	
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError("Texture " + File + " not found.")
		Tmp = LoadTextureCheckingIfInCache(File, Flags, TexDeleteType)
		If (Not Tmp) Then RuntimeError("Failed to load Texture: " + File)
	EndIf
	Return(Tmp) 
End Function

Function LoadFont_Strict(File$ = "Tahoma", Height% = 13)
	Local Tmp%
	
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError("Font " + File + " not found.")
		Tmp = LoadFont(File, Height)
		If (Not Tmp) Then RuntimeError("Failed to load Font: " + File)
	EndIf
	Return(Tmp)
End Function

Function LoadImage_Strict(File$)
	Local Tmp%
	
	If (Not Tmp) Then
		If FileType(File) <> 1 Then RuntimeError("Image " + Chr(34) + File + Chr(34) + " not found. ")
		Tmp = LoadImage(File)
		If (Not Tmp) Then RuntimeError("Failed to load image: " + File)
	EndIf
	Return(Tmp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D