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
	While ((FirstByte) Or ((ReadByte_ <> 13) And (ReadByte_ <> 10))) And (Offset < ini\Size)
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
				Until (Left(TemporaryString, 1) = "[") Or (ini\BankOffset >= ini\Size)
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

Function PutINIValue%(File$, INI_sSection$, INI_sKey$, INI_sValue$)
	; ~ Returns: True (Success) or False (Failed)
	INI_sSection = "[" + Trim(INI_sSection) + "]"
	
	Local INI_sUpperSection$ = Upper(INI_sSection)
	
	INI_sKey = Trim(INI_sKey)
	INI_sValue = Trim(INI_sValue)
	
	Local INI_sFilename$ = File
	; ~ Retrieve the INI data (if it exists)
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	; ~ (Re)Create the INI file updating / adding the SECTION, KEY and VALUE
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	
	If INI_lFileHandle = 0 Then Return(False) ; ~ Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr(0))
	
	While INI_lPos <> 0
		Local INI_sTemp$ = Mid(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If INI_sTemp <> "" Then
			If Left(INI_sTemp, 1) = "[" And Right(INI_sTemp, 1) = "]" Then
				; ~ Process SECTION
				If INI_sCurrentSection = INI_sUpperSection And (Not INI_bWrittenKey) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				EndIf
				INI_sCurrentSection = Upper(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If INI_sCurrentSection = INI_sUpperSection Then INI_bSectionFound = True
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine(INI_lFileHandle, INI_sTemp)
				Else
					; ~ KEY = VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					
					If lEqualsPos <> 0 Then
						If INI_sCurrentSection = INI_sUpperSection And Upper(Trim(Left(INI_sTemp, (lEqualsPos - 1)))) = Upper(INI_sKey) Then
							If INI_sValue <> "" Then INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
							INI_bWrittenKey = True
						Else
							WriteLine(INI_lFileHandle, INI_sTemp)
						EndIf
					EndIf
				EndIf
			EndIf	
		EndIf
		; ~ Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos = Instr(INI_sContents, Chr(0), INI_lOldPos)
	Wend
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY = VALUE line
	If (Not INI_bWrittenKey) Then
		If (Not INI_bSectionFound) Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	EndIf
	CloseFile(INI_lFileHandle)
	Return(True) ; ~ Success
End Function

Global OptionFileMC$ = GetEnv("AppData") + "\scpcb-ue\Data\options_MC.ini"

Type Options
	Field FogR%, FogG%, FogB%
	Field CursorR%, CursorG%, CursorB%
	Field VSync%
	Field ShowFPS%
	Field CamRange#
	Field Events%
End Type

Global opt.Options = New Options

Function LoadOptionsINI()
	; ~ [3-D SCENE]

	opt\FogR = GetINIInt(OptionFileMC, "3-D Scene", "BG Color R", 0)
	
	opt\FogG = GetINIInt(OptionFileMC, "3-D Scene", "BG Color G", 0)
	
	opt\FogB = GetINIInt(OptionFileMC, "3-D Scene", "BG Color B", 0)
	
	opt\CursorR% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color R", 255)
	
	opt\CursorG% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color G", 0)
	
	opt\CursorB% = GetINIInt(OptionFileMC, "3-D Scene", "Cursor Color B", 0)
	
	opt\VSync = GetINIInt(OptionFileMC, "3-D Scene", "VSync", True)
	
	opt\ShowFPS = GetINIInt(OptionFileMC, "3-D Scene", "Show FPS", False)
	
	opt\CamRange = GetINIFloat(OptionFileMC, "3-D Scene", "Camera Range", 50.0)
	
	opt\Events% = GetINIInt(OptionFileMC, "General", "Events_Default", True)
End Function

;~IDEal Editor Parameters:
;~C#BlitzPlus