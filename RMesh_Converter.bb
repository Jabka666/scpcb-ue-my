; ~ Converter Universal for SCP - Containment Breach Ultimate Edition Reborn v1.3.2
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/n7KdW4u
;----------------------------------------------------------------------------------------------------------------------------------------------------

Function IniWriteBuffer%(File$, ClearPrevious% = True)
	IniWriteBuffer_(File, ClearPrevious)
End Function

Function IniGetBufferString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Return(IniGetBufferString_(File, Section, Parameter, DefaultValue))
End Function

Function IniWriteString%(File$, Section$, Parameter$, Value$, UpdateBuffer% = True)
	IniWriteString_(File, Section, Parameter, Value, UpdateBuffer)
End Function

Function IniGetString$(File$, Section$, Parameter$, DefaultValue$ = "", AllowBuffer% = True)
	Return(IniGetString_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function GetFileLocalString$(File$, Name$, Parameter$, DefaultValue$ = "", CheckRootFile% = True)
	Local DefaultValue1$
	
	If CheckRootFile
		DefaultValue1 = IniGetBufferString(File, Name, Parameter, DefaultValue)
	Else
		DefaultValue1 = DefaultValue
	EndIf
	
	Return(IniGetBufferString(LanguagePath + File, Name, Parameter, DefaultValue1))
End Function

Function GetLocalString$(Section$, Parameter$)
	Return(GetFileLocalString(LanguageFile, Section, Parameter, Section + "," + Parameter))
End Function

Function Format$(String_$, Parameter$, Replace_$ = "%s")
	Return(Replace(String_, Replace_, Parameter))
End Function

Global Language$ = IniGetString(GetEnv("AppData") + "\scpcb-ue\Data\options.ini", "Global", "Language")
Global LanguagePath$

Const LanguageFile$ = "Data\local.ini"

IniWriteBuffer(LanguageFile)

Function SetLanguage%()
	If Language = "en"
		LanguagePath = ""
	Else
		LanguagePath = "Localization\" + Language + "\"
		IniWriteBuffer(LanguagePath + LanguageFile)
	EndIf
End Function

SetLanguage()

Graphics3D(640, 480, 0, 2)

AppTitle(GetLocalString("converter", "title"))

Global ConsoleFont% = LoadFont("GFX\Fonts\Andale Mono.ttf")

SetFont(ConsoleFont)

Const HIT_MAP% = 1
Const HIT_PLAYER% = 2

Collisions(HIT_PLAYER, HIT_MAP, 2, 2)

Function StripFileName$(File$)
	Local LastSlash% = 0
	Local FileLen% = Len(File)
	Local i%
	
	If FileLen = 0 Then Return("")
	
	For i = FileLen To 1 Step -1
		Local Middle$ = Mid(File, i, 1)
		
		If Middle = "\" Lor Middle = "/" ; ~ Detect a delimiter
			LastSlash = i
			Exit
		EndIf
	Next
	Return(Left(File, LastSlash))
End Function

Function StripPath$(File$)
	Local LastSlash% = 0
	Local FileLen% = Len(File)
	Local i%
	
	If FileLen = 0 Then Return("")
	
	For i = FileLen To 1 Step -1
		Local Middle$ = Mid(File, i, 1)
		
		If Middle = "\" Lor Middle = "/" ; ~ Detect a delimiter
			LastSlash = i
			Exit
		EndIf
	Next
	Return(Right(File, FileLen - LastSlash))
End Function

Function Piece$(s$, Entry%, Char$ = " ")
	Local n%, p%, a$
	
	While Instr(s, Char + Char)
		s = Replace(s, Char + Char, Char)
	Wend
	For n = 1 To Entry - 1
		p = Instr(s, Char)
		s = Mid(s, p + 1)
	Next
	p = Instr(s, Char)
	If p < 1
		a = s
	Else
		a = Left(s, p - 1)
	EndIf
	Return(a)
End Function

Function KeyValue$(Entity%, Key$, DefaultValue$ = "")
	Local p%, Value$, Properties$, TestKey$, Test$
	
	Properties = EntityName(Entity)
	Properties = Replace(Properties, Chr(13), "")
	Key = Lower(Key)
	Repeat
		p = Instr(Properties, Chr(10))
		If p
			Test = (Left(Properties, p - 1))
		Else
			Test = Properties
		EndIf
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

Function IsAlpha%(Tex%) ; ~ Detect transparency in textures
	Local Temp1s$ = StripPath(TextureName(Tex))
	Local Temp1i%
	Local x%, y%
	
	If Instr(Temp1s, ".png") <> 0 Lor Instr(Temp1s, ".tga") <> 0 Lor Instr(Temp1s, ".tpic") <> 0 ; ~ Texture is PNG or TARGA
		LockBuffer(TextureBuffer(Tex))
		For x = 0 To TextureWidth(Tex) - 1
			For y = 0 To TextureHeight(Tex) - 1
				Temp1i = ReadPixelFast(x, y, TextureBuffer(Tex))
				Temp1i = Temp1i Shr 24
				If Temp1i < 255
					UnlockBuffer(TextureBuffer(Tex))
					Return(3) ; ~ Texture has transparency
				EndIf
			Next
		Next
		UnlockBuffer(TextureBuffer(Tex))
		Return(1) ; ~ Texture is opaque
	ElseIf Instr(Temp1s, "_lm") <> 0 ; ~ Texture is a lightmap
		Return(2)
	EndIf
	Return(1) ; ~ Texture is opaque
End Function

Function SaveRoomMesh(BaseMesh%, FileName$) ; ~ Base mesh should be a 3D World Studio mesh
	If BaseMesh = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), FileName))
	
	Local Node%, ClassName$, c%, i%, j%, z%
	Local Surf%, Brush%, Tex%, TexName$
	Local Temp1i%
	Local TempMesh% = BaseMesh
	Local f% = WriteFile(FileName)
	Local DrawnMesh% = CreateMesh()
	Local HiddenMesh% = CreateMesh()
	;Local TriggerBoxAmount% = 0
	;Local TriggerBox%[128]
	;Local TriggerBoxName$[128]
	
	For c = 1 To CountChildren(TempMesh)
		Node = GetChild(TempMesh, c)	
		ClassName = Lower(KeyValue(Node, "classname"))
		
		Select ClassName
			Case "mesh"
				;[Block]
				ScaleMesh(Node, EntityScaleX(Node), EntityScaleY(Node), EntityScaleZ(Node))
				RotateMesh(Node, EntityPitch(Node), EntityYaw(Node), EntityRoll(Node))
				PositionMesh(Node, EntityX(Node), EntityY(Node), EntityZ(Node))
				AddMesh(Node, DrawnMesh)
				;[End Block]
			Case "brush"
				;[Block]
				RotateMesh(Node, EntityPitch(Node), EntityYaw(Node), EntityRoll(Node))
				PositionMesh(Node, EntityX(Node), EntityY(Node), EntityZ(Node))
				AddMesh(Node, DrawnMesh)
				;[End Block]
			Case "field_hit"
				;[Block]
				RotateMesh(Node, EntityPitch(Node), EntityYaw(Node), EntityRoll(Node))
				PositionMesh(Node, EntityX(Node), EntityY(Node), EntityZ(Node))
				AddMesh(Node, HiddenMesh)
				;[End Block]
;			Case "trigger"
;				;[Block]
;				TriggerBox[TriggerBoxAmount] = CreateMesh()
;				RotateMesh(Node, EntityPitch(Node), EntityYaw(Node), EntityRoll(Node))
;				PositionMesh(Node, EntityX(Node), EntityY(Node), EntityZ(Node))
;				AddMesh(Node, TriggerBox[TriggerBoxAmount])
;				TriggerBoxName[TriggerBoxAmount] = String(KeyValue(Node, "event", "event"), 1)
;				TriggerBoxAmount = TriggerBoxAmount + 1
;				;[End Block]
		End Select
	Next
	
;	If TriggerBoxAmount = 0
	WriteString(f, "RoomMesh")
;	Else
;		WriteString(f, "RoomMesh.HasTriggerBox")
;	EndIf
	
	WriteInt(f, CountSurfaces(DrawnMesh))
	For i = 1 To CountSurfaces(DrawnMesh)
		Surf = GetSurface(DrawnMesh, i)
		Brush = GetSurfaceBrush(Surf)
		Tex = 0
		Tex = GetBrushTexture(Brush, 0)
		If Tex <> 0
			WriteByte(f, IsAlpha(Tex))
			TexName = TextureName(Tex)
			WriteString(f, StripPath(TexName))
			FreeTexture(Tex) : Tex = 0
		Else
			WriteByte(f, 0)
		EndIf
		
		Tex = 0
		Tex = GetBrushTexture(Brush, 1)
		If Tex <> 0
			WriteByte(f, IsAlpha(Tex))
			TexName = TextureName(Tex)
			WriteString(f, StripPath(TexName))
			FreeTexture(Tex) : Tex = 0
		Else
			WriteByte(f, 0)
		EndIf
		
		FreeBrush(Brush) : Brush = 0
		
		WriteInt(f, CountVertices(Surf))
		For j = 0 To CountVertices(Surf) - 1
			; ~ World coordinates
			WriteFloat(f, VertexX(Surf, j))
			WriteFloat(f, VertexY(Surf, j))
			WriteFloat(f, VertexZ(Surf, j))
			
			; ~ Texture coordinates
			WriteFloat(f, VertexU(Surf, j, 0))
			WriteFloat(f, VertexV(Surf, j, 0))
			
			WriteFloat(f, VertexU(Surf, j, 1))
			WriteFloat(f, VertexV(Surf, j, 1))
			
			; ~ Colors
			WriteByte(f, VertexRed(Surf, j))
			WriteByte(f, VertexGreen(Surf, j))
			WriteByte(f, VertexBlue(Surf, j))
		Next
		
		WriteInt(f, CountTriangles(Surf))
		For j = 0 To CountTriangles(Surf) - 1
			WriteInt(f, TriangleVertex(Surf, j, 0))
			WriteInt(f, TriangleVertex(Surf, j, 1))
			WriteInt(f, TriangleVertex(Surf, j, 2))
		Next
	Next
	
	WriteInt(f, CountSurfaces(HiddenMesh))
	For i = 1 To CountSurfaces(HiddenMesh)
		Surf = GetSurface(HiddenMesh, i)
		WriteInt(f, CountVertices(Surf))
		For j = 0 To CountVertices(Surf) - 1
			; ~ World coords
			WriteFloat(f, VertexX(Surf, j))
			WriteFloat(f, VertexY(Surf, j))
			WriteFloat(f, VertexZ(Surf, j))
		Next
		
		WriteInt(f, CountTriangles(Surf))
		For j = 0 To CountTriangles(Surf) - 1
			WriteInt(f, TriangleVertex(Surf, j, 0))
			WriteInt(f, TriangleVertex(Surf, j, 1))
			WriteInt(f, TriangleVertex(Surf, j, 2))
		Next
	Next
	
;	If TriggerBoxAmount > 0
;		WriteInt(f, TriggerBoxAmount)
;		For z = 0 To TriggerBoxAmount - 1
;			WriteInt(f, CountSurfaces(TriggerBox[z]))
;			For i = 1 To CountSurfaces(TriggerBox[z])
;				Surf = GetSurface(TriggerBox[z], i)
;				WriteInt(f, CountVertices(Surf))
;				For j = 0 To CountVertices(Surf) - 1
;					; ~ World coords
;					WriteFloat(f, VertexX(Surf, j))
;					WriteFloat(f, VertexY(Surf, j))
;					WriteFloat(f, VertexZ(Surf, j))
;				Next
;				
;				WriteInt(f, CountTriangles(Surf))
;				For j = 0 To CountTriangles(Surf) - 1
;					WriteInt(f, TriangleVertex(Surf, j, 0))
;					WriteInt(f, TriangleVertex(Surf, j, 1))
;					WriteInt(f, TriangleVertex(Surf, j, 2))
;				Next
;			Next
;			WriteString(f, TriggerBoxName[z])
;		Next
;	EndIf
	
	Temp1i = 0
	
	For c = 1 To CountChildren(TempMesh)
		Node = GetChild(TempMesh, c)	
		ClassName = Lower(KeyValue(Node, "classname"))
		
		Select ClassName
			Case "screen", "waypoint", "light", "light_fix", "spotlight", "soundemitter", "model", "mesh"
				;[Block]
				Temp1i = Temp1i + 1
				;[End Block]
		End Select
	Next
	
	WriteInt(f, Temp1i)
	
	For c = 1 To CountChildren(TempMesh)
		Node = GetChild(TempMesh, c)	
		ClassName = Lower(KeyValue(Node, "classname"))
		
		Select ClassName
			Case "screen"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteString(f, KeyValue(Node, "imgpath", ""))
				;[End Block]
			Case "waypoint"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				;[End Block]
			Case "light"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteFloat(f, Float(KeyValue(Node, "range", "1")))
				WriteString(f, KeyValue(Node, "color", "255 255 255"))
				WriteFloat(f, Float(KeyValue(Node, "intensity", "1.0")))
				;[End Block]
			Case "light_fix"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteString(f, KeyValue(Node, "color", "255 255 255"))
				WriteFloat(f, Float(KeyValue(Node, "intensity", "1.0")))
				WriteFloat(f, Float(KeyValue(Node, "range", "1")))
				;[End Block]
			Case "spotlight"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteFloat(f, Float(KeyValue(Node, "range", "1")))
				WriteString(f, KeyValue(Node, "color", "255 255 255"))
				WriteFloat(f, Float(KeyValue(Node, "intensity", "1.0")))
				WriteString(f, KeyValue(Node, "angles", "0 0 0"))
				
				WriteInt(f, Int(KeyValue(Node, "innerconeangle", "")))
				WriteInt(f, Int(KeyValue(Node, "outerconeangle", "")))
				;[End Block]
			Case "soundemitter"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteInt(f, Int(KeyValue(Node, "sound", "0")))
				WriteFloat(f, Float(KeyValue(Node, "range", "1")))
				;[End Block]
			Case "model"
				;[Block]
				RuntimeError(Format(Format(GetLocalString("runerr", "model.support"), FileName, "{0}"), KeyValue(Node, "file"), "{1}"))
				;[End Block]
			Case "mesh"
				;[Block]
				WriteString(f, ClassName)
				
				WriteFloat(f, EntityX(Node))
				WriteFloat(f, EntityY(Node))
				WriteFloat(f, EntityZ(Node))
				
				WriteString(f, KeyValue(Node, "file"))
				
				WriteFloat(f, EntityPitch(Node))
				WriteFloat(f, EntityYaw(Node))
				WriteFloat(f, EntityRoll(Node))
				
				WriteFloat(f, EntityScaleX(Node))
				WriteFloat(f, EntityScaleY(Node))
				WriteFloat(f, EntityScaleZ(Node))
				
				Local Coll$ = KeyValue(Node, "hascollision", "yes")
				
				WriteByte(f, (Coll = "yes"))
				WriteInt(f, Int(KeyValue(Node, "meshfx", "0")))
				WriteString(f, KeyValue(Node, "textureoverwrite"))
				;[End Block]
		End Select
	Next
	
	WriteString(f, "EOF")
	
	CloseFile(f)
	
	FreeEntity(DrawnMesh) : DrawnMesh = 0
	FreeEntity(HiddenMesh) : HiddenMesh = 0
End Function

; ~ Free Image Constants
;[Block]
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
Const FIF_FAXG3% = 27
Const FIF_SGI% = 28
Const FIF_EXR% = 29
Const FIF_J2K% = 30
Const FIF_JP2% = 31
Const FIF_PFM% = 32
Const FIF_PICT% = 33
Const FIF_RAW% = 34
;[End Block]

Type Converted
	Field Name$
End Type

Function LoadRMesh(File$)
	; ~ Read the file
	Local f% = ReadFile(File)
	
	If f = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), File))
	
	Local fw% = WriteFile(Replace(File, ".rmesh", "_opt.rmesh"))
	Local i%, j%, k%, x#, y#, z#, Yaw#
	Local Vertex%, r.Converted
	Local Temp1i%, Temp2i%, Temp3i%
	Local Temp1#, Temp2#, Temp3#
	Local Temp1s$, Temp2s$
	Local Done%, LoadTex%
	Local Success%
	
	Temp2s = ReadString(f)
	WriteString(fw, Temp2s)
	
	File = StripFileName(File)
	
	Local Count%, Count2%
	
	; ~ Draw meshes
	Count = ReadInt(f)
	WriteInt(fw, Count)
	
	Local u#, v#
	
	For i = 1 To Count ; ~ Drawn mesh
		For j = 0 To 1
			Temp1i = ReadByte(f)
			WriteByte(fw, Temp1i)
			If Temp1i <> 0
				Temp1s = ReadString(f)
				If Instr(Temp1s, ".bmp") <> 0
					Done = 0
					For r.Converted = Each Converted
						If r\Name = Temp1s
							Done = 1
							Exit
						EndIf
					Next
					If (Not Done)
						r.Converted = New Converted
						r\Name = Temp1s
						LoadTex = FI_Load(FIF_BMP, File + Temp1s, 0)
					EndIf
					Temp1s = Replace(Temp1s, ".bmp", ".png")
					If (Not Done)
						Success = FI_Save(FIF_PNG, LoadTex, File + Temp1s, 0)
						If (Not Success) Then RuntimeError(FI_GetLastMessage())
						FI_Unload(LoadTex) : LoadTex = 0
					EndIf
				EndIf
				WriteString(fw, Temp1s)
			EndIf
		Next
		
		Count2 = ReadInt(f) ; ~ Vertices
		WriteInt(fw, Count2)
		
		For j = 1 To Count2
			; ~ World coordinates
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			WriteFloat(fw, x)
			WriteFloat(fw, y)
			WriteFloat(fw, z)
			
			; ~ Texture coordinates
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

SetBuffer(BackBuffer())
ClsColor(0, 0, 0)
Color(255, 255, 255)

Local Stri$, Mesh%

Stri = Input(GetLocalString("converter", "load"))
Mesh = LoadAnimMesh(Stri)
SaveRoomMesh(Mesh, Replace(Stri, ".b3d", ".rmesh"))
LoadRMesh(Replace(Stri, ".b3d", ".rmesh"))
Cls()
Text(5, 5, Format(GetLocalString("converter", "complete"), Stri))
Flip()
Delay(1000)
End()

;~IDEal Editor Parameters:
;~F#5#9#D#11#15#21#25#30#48#5A#6C#7F#9B#B4#C8#CF#D5#158#166#170
;~F#178#184#190#1A0#1AB#1AF#1D2#1FA#1FE
;~C#Blitz3D_TSS