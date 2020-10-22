Function StripPath$(File$) 
	Local Name$ = "", i%, mi$ = ""
	
	If Len(File) > 0 Then 
		For i = Len(File) To 1 Step -1 
			mi = Mid(File, i, 1) 
			If mi = "\" Lor mi = "/" Then Return(Name)
			
			Name = mi + Name
		Next 
	EndIf 
	
	Return(Name) 
End Function

Function StripFilename$(File$)
	Local mi$ = ""
	Local LastSlash% = 0
	Local i%
	
	If Len(File) > 0 Then
		For i = 1 To Len(File)
			mi = Mid(File, i, 1)
			If mi = "\" Lor mi = "/" Then
				LastSlash = i
			EndIf
		Next
	EndIf
	
	Return(Left(File, LastSlash))
End Function

Function GetINIString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Local TemporaryString$ = ""
	Local f% = ReadFile(File)
	
	While (Not Eof(f))
		If ReadLine(f) = "[" + Section + "]" Then
			Repeat 
				TemporaryString = ReadLine(f)
				If Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1.0, 0.0)) ) = Parameter Then
					CloseFile(f)
					Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
			CloseFile(f)
			Return(DefaultValue)
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function GetINIInt%(File$, Section$, Parameter$, DefaultValue% = 0)
	Local Txt$ = Lower(GetINIString(File, Section, Parameter, DefaultValue))
	
	If Lower(Txt) = "true" Then
		Return(1)
	ElseIf Lower(Txt) = "false"
		Return(0)
	Else
		Return(Int(Txt))
	EndIf
End Function

Function GetINIFloat#(File$, Section$, Parameter$, DefaultValue# = 0.0)
	Return(GetINIString(File, Section, Parameter, DefaultValue))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D