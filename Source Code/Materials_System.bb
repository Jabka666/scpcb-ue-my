Type Materials
	Field Name$
	Field Diff%
	Field Bump%
	Field StepSound%
End Type

Function ApplyBumpMap(Texture%)
	TextureBlend(Texture, 6)
	TextureBumpEnvMat(Texture, 0, 0, -0.012)
	TextureBumpEnvMat(Texture, 0, 1, -0.012)
	TextureBumpEnvMat(Texture, 1, 0, 0.012)
	TextureBumpEnvMat(Texture, 1, 1, 0.012)
	TextureBumpEnvOffset(Texture, 0.5)
	TextureBumpEnvScale(Texture, 1.0)
End Function

Function LoadMaterials(File$)
	CatchErrors("Uncaught (LoadMaterials)")
	
	Local TemporaryString$
	Local mat.Materials = Null
	Local StrTemp$ = ""
	Local f = OpenFile(File)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			mat.Materials = New Materials
			
			mat\Name = Lower(TemporaryString)
			If BumpEnabled Then
				StrTemp = GetINIString(File, TemporaryString, "bump")
				If StrTemp <> "" Then 
					mat\Bump =  LoadTexture_Strict(StrTemp)
					ApplyBumpMap(mat\Bump)					
				EndIf
			EndIf
			
			mat\StepSound = (GetINIInt(File, TemporaryString, "stepsound") + 1)
		EndIf
	Wend
	
	CloseFile(f)
	
	CatchErrors("LoadMaterials")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D