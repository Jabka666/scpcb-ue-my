Function GetTextureFromCache%(Name$)
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc\Diff)
	Next
	Return(0)
End Function

Function GetBumpFromCache%(Name$)
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc\Bump)
	Next
	Return(0)
End Function

Function GetCache.Materials(Name$)
	For tc.Materials = Each Materials
		If tc\Name = Name Then Return(tc)
	Next
	Return(Null)
End Function

Function AddTextureToCache(Texture%)
	Local tc.Materials = GetCache(StripPath(TextureName(Texture)))
	
	If tc.Materials = Null Then
		tc.Materials = New Materials
		tc\Name = StripPath(TextureName(Texture))
		If BumpEnabled Then
			Local Temp$ = GetINIString(MaterialsFile, tc\Name, "bump")
			
			If Temp <> "" Then
				tc\Bump = LoadTexture_Strict(Temp)
				ApplyBumpMap(tc\Bump)
			Else
				tc\Bump = 0
			EndIf
		EndIf
		tc\Diff = 0
	EndIf
	If tc\Diff = 0 Then tc\Diff = Texture
End Function

Function ClearTextureCache()
	For tc.Materials = Each Materials
		If tc\Diff <> 0 Then FreeTexture(tc\Diff)
		If tc\Bump <> 0 Then FreeTexture(tc\Bump)
		Delete(tc)
	Next
End Function

Function FreeTextureCache()
	For tc.Materials = Each Materials
		If tc\Diff <> 0 Then FreeTexture(tc\Diff) : tc\Diff = 0
		If tc\Bump <> 0 Then FreeTexture(tc\Bump) : tc\Bump = 0
	Next
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D