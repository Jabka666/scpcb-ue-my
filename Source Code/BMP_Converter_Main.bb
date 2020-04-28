Graphics3D(640, 480, 0, 2)

AppTitle("Optimize lightmaps (convert bmp to png)")

Const FIF_UNKNOWN% = -1
Const FIF_BMP% = 0
Const FIF_ICO% = 1
Const FIF_JPEG% = 2
Const FIF_JNG% = 3
Const FIF_KOALA% = 4
Const FIF_LBM% = 5
Const FIF_IFF% = FIF_LBM
Const FIF_MNG% = 6
Const FIF_PBM% = 7
Const FIF_PBMRAW% = 8
Const FIF_PCD% = 9
Const FIF_PCX% = 10
Const FIF_PGM% = 11
Const FIF_PGMRAW% = 12
Const FIF_PNG% = 13
Const FIF_PPM% = 14
Const FIF_PPMRAW% = 15
Const FIF_RAS% = 16
Const FIF_TARGA% = 17
Const FIF_TIFF% = 18
Const FIF_WBMP% = 19
Const FIF_PSD% = 20
Const FIF_CUT% = 21
Const FIF_XBM% = 22
Const FIF_XPM% = 23
Const FIF_DDS% = 24
Const FIF_GIF% = 25
Const FIF_HDR% = 26
Const FIF_FAXG3%	= 27
Const FIF_SGI% = 28
Const FIF_EXR% = 29
Const FIF_J2K% = 30
Const FIF_JP2% = 31
Const FIF_PFM% = 32
Const FIF_PICT% = 33
Const FIF_RAW% = 34

Function GetINIString$(File$, Section$, Parameter$)
	Local TemporaryString$ = ""
	Local f% = ReadFile(File)
	
	While Not Eof(f)
		If ReadLine(f) = "[" + Section + "]" Then
			Repeat 
				TemporaryString = ReadLine(f)
				If Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0))) = Parameter Then
					CloseFile(f)
					Return(Trim(Right(TemporaryString, Len(TemporaryString) - Instr(TemporaryString, "="))))
				EndIf
			Until Left(TemporaryString,1) = "[" Or Eof(f)
			CloseFile(f)
			Return("")
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function GetINIInt%(File$, Section$, Parameter$)
	Local StrTemp$ = Lower(GetINIString(File, Section, Parameter))
	
	Select StrTemp
		Case "true"
			;[Block]
			Return(1)
			;[End Block]
		Case "false"
			;[Block]
			Return(0)
			;[End Block]
		Default
			;[Block]
			Return(Int(StrTemp))
			;[End Block]
	End Select
	Return 
End Function

Function GetINIFloat#(File$, Section$, Parameter$)
	Return(GetINIString(File, Section, Parameter))
End Function

Function PutINIValue%(INI_sAppName$, INI_sSection$, INI_sKey$, INI_sValue$)
	;  ~ Returns: True (Success) or False (Failed)
	INI_sSection = "[" + Trim(INI_sSection) + "]"
	INI_sUpperSection$ = Upper(INI_sSection)
	INI_sKey = Trim(INI_sKey)
	INI_sValue = Trim(INI_sValue)
	INI_sFilename$ = CurrentDir() + "\"  + INI_sAppName
	; ~ Retrieve the INI data (if it exists)
	
	INI_sContents$ = INI_FileToString(INI_sFilename)
	
    ; ~ (Re)Create the INI file updating / adding the SECTION, KEY and VALUE
	
	INI_bWrittenKey% = False
	INI_bSectionFound% = False
	INI_sCurrentSection$ = ""
	
	INI_lFileHandle = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; ~ Create file failed!
	
	INI_lOldPos% = 1
	INI_lPos% = Instr(INI_sContents, Chr(0))
	
	While (INI_lPos <> 0)
		INI_sTemp$ = Trim(Mid(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos)))
		If (INI_sTemp <> "") Then
			If Left(INI_sTemp, 1) = "[" And Right(INI_sTemp, 1) = "]" Then
				; ~ Process SECTION
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
			Else
				; ~ KEY = VALUE
				lEqualsPos% = Instr(INI_sTemp, "=")
				If (lEqualsPos <> 0) Then
					If (INI_sCurrentSection = INI_sUpperSection) And Upper(Trim(Left(INI_sTemp, (lEqualsPos - 1)))) = Upper(INI_sKey) Then
						If (INI_sValue <> "") Then INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
						INI_bWrittenKey = True
					Else
						WriteLine(INI_lFileHandle, INI_sTemp)
					End If
				End If
			End If
		End If
		
		; ~ Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr(0), INI_lOldPos)
	Wend
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY=VALUE line
	If INI_bWrittenKey = False Then
		If (INI_bSectionFound = False) Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	End If
	
	CloseFile(INI_lFileHandle)
	
	Return(True) ; ~ Success
End Function


Function INI_FileToString$(INI_sFilename$)
	INI_sString$ = ""
	INI_lFileHandle% = ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine(INI_lFileHandle) + Chr(0)
		Wend
		CloseFile(INI_lFileHandle)
	End If
	Return(INI_sString)
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine(INI_lFileHandle, "") ; ~ Blank line between sections
	WriteLine(INI_lFileHandle, INI_sNewSection)
	Return(INI_sNewSection)
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	WriteLine(INI_lFileHandle, INI_sKey + "=" + INI_sValue)
	Return(True)
End Function

Function Min#(a#, b#)
	If a < b Then 
		Return(a)
	Else 
		Return(b)
	EndIf
End Function

Function Max#(a#, b#)
	If a > b Then 
		Return(a)
	Else 
		Return(b)
	EndIf
End Function

Function StripPath$(File$) 
	Local mi$ = "", i%, Name$ = ""
	
	If Len(File) > 0 
		For i = Len(File) To 1 Step -1 
			mi = Mid$(File, i, 1) 
			If mi = "\" Or mi = "/" Then Return(Name) Else Name = mi + Name 
		Next 
	EndIf 
	Return(Name) 
End Function 

Function StripFilename$(File$)
	Local mi$ = "", i%, LastSlash% = 0
	
	If Len(File) > 0
		For i = 1 To Len(File)
			mi = Mid(File$, i, 1)
			If mi = "\" Or mi = "/" Then
				LastSlash = i
			EndIf
		Next
	EndIf
	
	Return(Left(File, LastSlash))
End Function

Function EntityScaleX#(Entity%, Globl% = False) 
	If Globl Then 
		TFormVector(1.0, 0.0, 0.0, Entity, 0) 
	Else 
		TFormVector(1.0, 0.0, 0.0, Entity, GetParent(Entity))
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ()))
End Function 

Function EntityScaleY#(Entity%, Globl% = False)
	If Globl Then 
		TFormVector(0.0, 1.0, 0.0, Entity, 0) 
	Else 
		TFormVector(0.0, 1.0, 0.0, Entity, GetParent(Entity))  
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ())) 
End Function 

Function EntityScaleZ#(Entity%, Globl% = False)
	If Globl Then 
		TFormVector(0.0, 0.0, 1.0, Entity, 0)
	Else 
		TFormVector(0.0, 0.0, 1.0, Entity, GetParent(Entity))
	EndIf
	Return(Sqr(TFormedX() * TFormedX() + TFormedY() * TFormedY() + TFormedZ() * TFormedZ())) 
End Function

Function Piece$(s$, Entry%, Char$ = " ")
	Local n%, p%, a$
	
	While Instr(s, Char + Char)
		s = Replace(s, Char + Char, Char)
	Wend
	For n = 1 To Entry - 1
		p = Instr(s, Char)
		s = Right(s, Len(s) - p)
	Next
	p = Instr(s, Char)
	If p < 1 
		a = s
	Else
		a = Left(s, p - 1)
	EndIf
	Return a
End Function

Function KeyValue$(Entity%, Key$, DefaultValue$ = "")
	Local Properties$, TestKey$, Value$, Test$, p%
	
	Properties = EntityName(Entity)
	Properties = Replace(Properties$, Chr(13), "")
	Key$ = Lower(Key)
	Repeat
		p = Instr(Properties, Chr(10))
		If p Then Test = (Left(Properties, p - 1)) Else Test = Properties
		TestKey = Piece(Test, 1, "=")
		TestKey = Trim(TestKey)
		TestKey = Replace(TestKey, Chr(34), "")
		TestKey = Lower(TestKey)
		If TestKey = Key Then
			Value = Piece(Test, 2, "=")
			Value = Trim(Value)
			Value = Replace(Value, Chr(34), "")
			Return(Value)
		EndIf
		If (Not p) Then Return(DefaultValue)
		Properties = Right(Properties, Len(Properties) - p)
	Forever 
End Function

Type Converted
	Field Name$
End Type

Function LoadRMesh(File$)
	; ~ Read the file
	Local f% = ReadFile(File)
	Local fw% = WriteFile(Replace(File, ".rmesh", "_opt.rmesh"))
	Local i%, j%, k%, x#, y#, z#, Yaw#
	Local Vertex%
	Local Temp1i%, Temp2i%, Temp3i%
	Local Temp1#, Temp2#, Temp3#
	Local Temp1s$, Temp2s$
	
	Temp2s = ReadString(f)
	WriteString(fw, Temp2s)
	
	File = StripFilename(File)
	
	If FileType(File + "bmp_lm") <> 2 Then CreateDir(File + "bmp_lm")
	
	Local Count%, Count2%
	
	Count = ReadInt(f)
	WriteInt(fw, Count)
	
	Local u#, v#
	
	For i = 1 To Count ; ~ Drawn mesh
		For j = 0 To 1
			Temp1i = ReadByte(f)
			WriteByte(fw, Temp1i)
			If Temp1i <> 0 Then
				Temp1s = ReadString(f)
				
				If Instr(Temp1s, ".bmp") <> 0 Then
					Done% = 0
					For r.Converted = Each Converted
						If r\name = Temp1s Then Done = 1 : Exit
					Next
					If (Not done) Then
						r.Converted = New Converted
						r\Name = Temp1s
						LoadTex% = FI_Load(FIF_BMP, File + Temp1s, 0)
					EndIf
					CopyFile(File + Temp1s, File + "bmp_lm\" + Temp1s)
					DeleteFile(File + Temp1s)
					Temp1s = Replace(Temp1s, ".bmp", ".png")
					If (Not Done) Then
						FI_Save(FIF_PNG, LoadTex, File + Temp1s, 0)
						FI_Unload(LoadTex)
					EndIf
				EndIf
				WriteString(fw, Temp1s)
			EndIf
		Next
		
		Count2 = ReadInt(f) ; ~ Vertices
		WriteInt(fw, Count2)
		
		For j = 1 To Count2
			; ~ World coords
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			WriteFloat(fw, x)
			WriteFloat(fw, y)
			WriteFloat(fw, z)
			
			; ~ Texture coords
			For k = 0 To 1
				u = ReadFloat(f) : v = ReadFloat(f)
				WriteFloat(fw, u)
				WriteFloat(fw, v)
			Next
			
			; ~ Colors
			Temp1i = ReadByte(f)
			Temp2i = ReadByte(f)
			Temp3i = ReadByte(f)
			WriteByte(fw, Temp1i)
			WriteByte(fw, Temp2i)
			WriteByte(fw, Temp3i)
		Next
		
		Count2 = ReadInt(f) ; ~ Polys
		WriteInt(fw, Count2)
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			WriteInt(fw, Temp1i)
			WriteInt(fw, Temp2i)
			WriteInt(fw, Temp3i)
		Next
	Next
	
	While (Not Eof(f))
		Temp1i = ReadByte(f)
		WriteByte(fw, Temp1i)
	Wend
End Function

Local State% = 0

SetBuffer(BackBuffer())
ClsColor(0, 0, 0)
Cls
Color(255, 255, 255)
Text(5, 5, "Press a key:")
Text(5, 25, "1 - Convert BMPs to PNGs, and modify rmeshes to use them")
Text(5, 45, "2 - Convert BMPs to PNGs for a specific room and modify it to use them")
Text(5, 65, "3 - Revert options.ini to use the old rmeshes")
Text(5, 85, "ESC - Close without doing anything")
Flip

While (Not KeyHit(1))
	If KeyHit(2) Or KeyHit(79) Then State = 1 : Exit
	If KeyHit(3) Or KeyHit(80) Then State = 2 : Exit
Wend

Local Stri$, TemporaryString$, f%

Type INIConvert
	Field File$
	Field Section$
	Field Key$
	Field Value$
End Type

Local ic.INIConvert

If State = 1 Then
	If FileSize("Data\rooms_opt.ini") = 0 Then
		CopyFile "Data\rooms.ini", "Data\rooms_bmp.ini"
	EndIf
	
	f = ReadFile("Data\rooms.ini")
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			If TemporaryString <> "room ambience" Then
				Stri = GetINIString("Data\rooms.ini", TemporaryString, "mesh path")
				
				LoadRMesh(Stri)
				
				Cls
				Text(5, 5, "Converted " + Chr(34) + Stri + Chr(34))
				Flip
				
				ic.INIConvert = New INIConvert
				ic\File = "Data\rooms.ini"
				ic\Section = TemporaryString
				ic\Key = "mesh path"
				ic\Value = Replace(Stri, ".rmesh", ".rmesh")
			EndIf
		EndIf
	Wend
	
	For ic.INIConvert = Each INIConvert
		PutINIValue(ic\File, ic\Section, ic\Key, ic\Value)
	Next
	
	Cls
	Text(5, 5, "Conversion complete")
	Flip
	Delay(1000)
	
	CloseFile(f)
ElseIf State = 2
	Cls
	Flip
	FlushKeys()
	Stri = Input("Path for the room to be converted: ")
	LoadRMesh(Stri)
	Cls
	Text(5, 5, "Conversion of " + Stri + " complete")
	Flip
	Delay(1000)
ElseIf State = 3
	f = ReadFile("Data\rooms.ini")
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			If TemporaryString <> "room ambience" Then
				Stri = GetINIString("Data\rooms.ini", TemporaryString, "mesh path")
				
				ic.INIConvert = New INIConvert
				ic\File = "Data\rooms.ini"
				ic\Section = TemporaryString
				ic\Key = "mesh path"
				ic\Value = Replace(Stri, "_opt.rmesh", ".rmesh")
			EndIf
		EndIf
	Wend
	
	For ic.INIConvert = Each INIConvert
		PutINIValue(ic\File, ic\Section, ic\Key, ic\Value)
	Next
	
	Cls
	Text(5, 5, "Reset complete, you need to restore the bmp lightmaps manually")
	Flip
	Delay(1000)
	
	CloseFile(f)
EndIf

;~IDEal Editor Parameters:
;~C#Blitz3D