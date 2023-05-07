Type Materials
	Field Name$
	Field Diff%
	Field Bump%
	Field IsDiffuseAlpha%
	Field UseMask%
End Type

Function LoadMaterials%(File$)
	CatchErrors("Uncaught (LoadMaterials")
	
	Local TemporaryString$
	Local mat.Materials = Null
	Local StrTemp$ = ""
	
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			mat.Materials = New Materials
			
			mat\Name = Lower(TemporaryString)
			mat\IsDiffuseAlpha = IniGetInt(File, TemporaryString, "transparent")
			mat\UseMask = IniGetInt(File, TemporaryString, "masked")
		EndIf
	Wend
	
	CloseFile(f)
	
	CatchErrors("LoadMaterials")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS