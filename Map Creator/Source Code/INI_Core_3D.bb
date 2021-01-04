Function StripFileName$(File$)
	Local mi$ = "", LastSlash% = 0, i%
	
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

Function StripPath$(File$) 
	Local Name$ = "", i%, mi$
	
	If Len(File) > 0 Then
		For i = Len(File) To 1 Step -1 
			mi = Mid(File, i, 1) 
			If mi = "\" Lor mi = "/" Then Return(Name)
			Name = mi + Name 
		Next 
	EndIf 
	Return(Name) 
End Function

Type INIFile
	Field Name$
	Field Bank%
	Field BankOffset% = 0
	Field Size%
End Type

Function ReadINILine$(ini.INIFile)
	Local ReadByte_%
	Local FirstByte% = True
	Local Offset% = ini\BankOffset
	Local Bank% = ini\Bank
	Local RetStr$ = ""
	
	ReadByte_ = PeekByte(Bank, Offset)
	While ((FirstByte) Lor ((ReadByte_ <> 13) And (ReadByte_ <> 10))) And (Offset < ini\Size)
		ReadByte_ = PeekByte(Bank, Offset)
		If ReadByte_ <> 13 And ReadByte_ <> 10 Then
			FirstByte = False
			RetStr = RetStr + Chr(ReadByte_)
		EndIf
		Offset = Offset + 1
	Wend
	ini\BankOffset = Offset
	
	Return(RetStr)
End Function

Function UpdateINIFile$(File$)
	Local ini.INIFile = Null
	Local k.INIFile
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(File) Then
			ini = k
			Exit
		EndIf
	Next
	
	If ini = Null Then Return
	If ini\Bank <> 0 Then FreeBank(ini\Bank)
	
	Local f% = ReadFile(ini\Name)
	Local FileSize_% = 1
	
	While FileSize_ < FileSize(ini\Name)
		FileSize_ = FileSize_ * 2
	Wend
	ini\Bank = CreateBank(FileSize_)
	ini\Size = 0
	While (Not Eof(f))
		PokeByte(ini\Bank, ini\Size, ReadByte(f))
		ini\Size = ini\Size + 1
	Wend
	CloseFile(f)
End Function

Function GetINIString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Local TemporaryString$ = ""
	Local ini.INIFile = Null
	Local k.INIFile
	
	For k.INIFile = Each INIFile
		If k\Name = Lower(File) Then
			ini = k
			Exit
		EndIf
	Next
	
	If ini = Null Then
		ini.INIFile = New INIFile
		ini\Name = Lower(File)
		ini\Bank = 0
		UpdateINIFile(ini\Name)
	EndIf
	
	ini\BankOffset = 0
	
	Section = Lower(Section)
	
	While ini\BankOffset < ini\Size
		Local StrTemp$ = ReadINILine(ini)
		
		If Left(StrTemp, 1) = "[" Then
			StrTemp = Lower(StrTemp)
			If Mid(StrTemp, 2, Len(StrTemp) - 2) = Section Then
				Repeat
					TemporaryString = ReadINILine(ini)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(Parameter) Then
						Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
					EndIf
				Until (Left(TemporaryString, 1) = "[") Lor (ini\BankOffset >= ini\Size)
				Return(DefaultValue)
			EndIf
		EndIf
	Wend
	Return(DefaultValue)
End Function

Function GetINIInt%(File$, Section$, Parameter$, DefaultValue% = 0)
	Local StrTemp$ = GetINIString(File, Section, Parameter, DefaultValue)
	
	Select StrTemp
		Case "True"
			;[Block]
			Return(True)
			;[End Block]
		Case "False"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(StrTemp))
			;[End Block]
	End Select
End Function

Function GetINIFloat#(File$, Section$, Parameter$, DefaultValue# = 0.0)
	Return(Float(GetINIString(File, Section, Parameter, DefaultValue)))
End Function

Function INI_FileToString$(INI_sFilename$)
	Local INI_sString$ = ""
	Local INI_lFileHandle% = ReadFile(INI_sFilename)
	
	If INI_lFileHandle <> 0 Then
		While (Not Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine(INI_lFileHandle) + Chr(0)
		Wend
		CloseFile(INI_lFileHandle)
	EndIf
	Return(INI_sString)
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine(INI_lFileHandle, "") ; ~ Blank line between sections
	WriteLine(INI_lFileHandle, INI_sNewSection)
	Return(INI_sNewSection)
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine(INI_lFileHandle, INI_sKey + " = " + INI_sValue)
	Return(True)
End Function

Global OptionFileMC$ = GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini"

Type Options
	Field FogR%, FogG%, FogB%
	Field CursorR%, CursorB%, CursorG%
	Field VSync%
	Field ShowFPS%
	Field CamRange#
End Type

Global opt.Options = New Options

Function LoadOptionsINI()
	; ~ [3-D SCENE]
	
	; ~ TODO: Make colored fog
	opt\FogR = GetINIInt(OptionFileMC, "3-D Scene", "BG Color R", 0)
	
	opt\FogG = GetINIInt(OptionFileMC, "3-D Scene", "BG Color G", 0)
	
	opt\FogB = GetINIInt(OptionFileMC, "3-D Scene", "BG Color B", 0)
	
	opt\CursorR% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color R", 255)
	
	opt\CursorG% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color G", 0)
	
	opt\CursorB% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color B", 0)
	
	opt\VSync% = GetINIInt(OptionFileMC, "3-D Scene", "VSync", True)
	
	opt\ShowFPS% = GetINIInt(OptionFileMC, "3-D Scene", "Show FPS", False)
	
	opt\CamRange# = GetINIFloat(OptionFileMC, "3-D Scene", "Camera Range", 50.0)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D