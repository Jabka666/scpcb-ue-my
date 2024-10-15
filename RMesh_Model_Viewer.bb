; ~ RMESH Model Viewer for SCP - Containment Breach Ultimate Edition Reborn v1.3.3
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Contact us: https://discord.gg/n7KdW4u
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ IniController - A part of BlitzToolBox
; ~ Write & Read ini file.
; ~ v1.08 2024.9.16
; ~ https://github.com/ZiYueCommentary/BlitzToolbox

Function IniWriteBuffer%(File$, ClearPrevious% = True)
	IniWriteBuffer_(File, ClearPrevious)
End Function

Function IniGetBufferString$(File$, Section$, Parameter$, DefaultValue$ = "")
	Return(IniGetBufferString_(File, Section, Parameter, DefaultValue))
End Function

Function IniGetString$(File$, Section$, Parameter$, DefaultValue$ = "", AllowBuffer% = True)
	Return(IniGetString_(File, Section, Parameter, DefaultValue, AllowBuffer))
End Function

Function IniGetInt%(File$, Section$, Parameter$, DefaultValue% = 0, AllowBuffer% = True)
	Local Result$ = IniGetString(File, Section, Parameter, DefaultValue, AllowBuffer)
	
	Select Result
		Case "True", "true", "1"
			;[Block]
			Return(True)
			;[End Block]
		Case "False", "false", "0"
			;[Block]
			Return(False)
			;[End Block]
		Default
			;[Block]
			Return(Int(Result))
			;[End Block]
	End Select
End Function

Function GetFileLocalString$(File$, Name$, Parameter$, DefaultValue$ = "", CheckRootFile% = True)
	Local DefaultValue1$
	
	If CheckRootFile
		DefaultValue1 = IniGetBufferString(File, Name, Parameter, DefaultValue)
	Else
		DefaultValue1 = DefaultValue
	EndIf
	
	Return(IniGetBufferString(File, Name, Parameter, DefaultValue1))
End Function

Function GetLocalString$(Section$, Parameter$, CheckRootFile% = True)
	Local DefaultValue$
	
	If CheckRootFile
		DefaultValue = IniGetBufferString(LanguageFile, Section, Parameter, Section + "," + Parameter)
	Else 
		DefaultValue = Section + "," + Parameter
	EndIf
	Return(IniGetBufferString(LanguagePath + LanguageFile, Section, Parameter, DefaultValue))
End Function

Function Format$(String_$, Parameter$, Replace_$ = "%s")
	Return(Replace(String_, Replace_, Parameter))
End Function

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
						TextureCoords(Texture, 1)
						BrushTexture(b, Texture, 0, 0)
						DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
					Else
						BrushTexture(b, MissingTexture, 0, 0)
					EndIf
					
					t2 = GetBrushTexture(b, 1) ; ~ Diffuse (if Lightmap is existing)
					If t2 <> 0
						Texture = CheckForTexture(t2, TexAlpha)
						If Texture <> 0
							TextureCoords(Texture, 0)
							BrushTexture(b, Texture, 0, 1)
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
	
	If Tmp = 0
		If FileType(File) <> 1 Then RuntimeError(Format(GetLocalString("runerr", "texture.notfound"), File))
		Tmp = LoadTextureCheckingIfInCache(File, Flags, TexDeleteType)
		If Tmp = 0 Then RuntimeError(Format(GetLocalString("runerr", "texture.failed.load"), File))
	EndIf
	Return(Tmp)
End Function

Type Materials
	Field Name$
	Field IsDiffuseAlpha%
	Field UseMask%
End Type

Function LoadMaterials%(File$)
	Local Loc$
	Local mat.Materials = Null
	Local StrTemp$ = ""
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		Loc = Trim(ReadLine(f))
		If Left(Loc, 1) = "["
			Loc = Mid(Loc, 2, Len(Loc) - 2)
			mat.Materials = New Materials
			mat\Name = Lower(Loc)
			mat\IsDiffuseAlpha = IniGetInt(File, Loc, "transparent")
			mat\UseMask = IniGetInt(File, Loc, "masked")
		EndIf
	Wend
	
	CloseFile(f)
End Function

; ~ Texture Cache Constants
;[Block]
Const MapTexturesFolder$ = "GFX\map\textures\"

Const DeleteMapTextures% = 0
Const DeleteAllTextures% = 1
;[End Block]

Type TextureInCache
	Field Tex%
	Field TexName$
	Field TexDeleteType%
End Type

Function LoadTextureCheckingIfInCache%(TexName$, TexFlags% = 1, DeleteType% = DeleteMapTextures)
	If TexName = "" Then Return(0)
	
	Local tic.TextureInCache
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexName <> "CreateTexture"
			If StripPath(TexName) = tic\TexName
				If tic\TexDeleteType < DeleteType Then tic\TexDeleteType = DeleteType
				Return(tic\Tex)
			EndIf
		EndIf
	Next
	
	Local CurrPath$ = TexName
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = StripPath(TexName)
	tic\TexDeleteType = DeleteType
	If FileType(LanguagePath + CurrPath) = 1 Then CurrPath = LanguagePath + CurrPath
	If tic\Tex = 0 Then tic\Tex = LoadTexture(CurrPath, TexFlags)
	Return(tic\Tex)
End Function

Function DeleteTextureEntriesFromCache%(DeleteType%)
	Local tic.TextureInCache
	
	For tic.TextureInCache = Each TextureInCache
		If tic\TexDeleteType =< DeleteType
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
End Function

Function DeleteSingleTextureEntryFromCache%(Texture%)
	Local tic.TextureInCache
	
	For tic.TextureInCache = Each TextureInCache
		If tic\Tex = Texture
			If tic\Tex <> 0 Then FreeTexture(tic\Tex) : tic\Tex = 0
			Delete(tic)
		EndIf
	Next
End Function

Function CreateTextureUsingCacheSystem%(Width%, Height%, TexFlags% = 1, Frames% = 1, DeleteType% = DeleteAllTextures)
	Local tic.TextureInCache
	
	tic.TextureInCache = New TextureInCache
	tic\TexName = "CreateTexture"
	tic\TexDeleteType = DeleteType
	tic\Tex = CreateTexture(Width, Height, TexFlags + 256, Frames)
	Return(tic\Tex)
End Function

Function IsTexAlpha%(Tex%, Name$ = "") ; ~ Detect transparency in textures
	Local mat.Materials
	Local Temp1s$
	Local Temp%, Temp2%
	
	If Name = ""
		Temp1s = StripPath(TextureName(Tex))
	Else
		Temp1s = Name
	EndIf
	
	; ~ Texture is a lightmap
	If Instr(Temp1s, "_lm") <> 0 Then Return(2)
	
	For mat.Materials = Each Materials
		If mat\Name = Temp1s
			Temp = mat\IsDiffuseAlpha
			Temp2 = mat\UseMask
			Exit
		EndIf
	Next
	Return(1 + (2 * (Temp <> 0)) + (4 * (Temp2 <> 0)))
End Function

; ~ This is supposed to be the only texture that will be outside the TextureCache system
Global MissingTexture%

Function LoadMissingTexture%()
	MissingTexture = CreateTexture(2, 2, 1 + 256)
	TextureBlend(MissingTexture, 3)
	SetBuffer(TextureBuffer(MissingTexture))
	ClsColor(0, 0, 0)
	Cls()
	SetBuffer(BackBuffer())
End Function

Function CheckForTexture%(Tex%, TexFlags% = 1)
	Local Name$ = ""
	
	If FileType(TextureName(Tex)) = 1 ; ~ Check if texture is existing in original path
		Name = TextureName(Tex)
	ElseIf FileType(MapTexturesFolder + StripPath(TextureName(Tex))) = 1 ; ~ If not, check the MapTexturesFolder
		Name = MapTexturesFolder + StripPath(TextureName(Tex))
	EndIf
	
	Local Texture% = LoadTextureCheckingIfInCache(Name, TexFlags)
	
	If Texture <> 0
		If ((TexFlags Shr 1) Mod 2) = 0
			TextureBlend(Texture, 5)
		Else
			TextureBlend(Texture, 1)
		EndIf
	EndIf
	Return(Texture)
End Function

Type Props
	Field File$
	Field OBJ%
End Type

Function CheckForPropModel%(File$)
	Local Path$ = "GFX\map\Props\"
	
	Select File
		Case Path + "door01.b3d"
			;[Block]
			Return(CopyEntity(DoorModelID[0]))
			;[End Block]
		Case Path + "contdoorleft.b3d"
			;[Block]
			Return(CopyEntity(DoorModelID[1]))
			;[End Block]
		Case Path + "contdoorright.b3d"
			;[Block]
			Return(CopyEntity(DoorModelID[2]))
			;[End Block]
		Default
			;[Block]
			Return(LoadMesh_Strict(File))
			;[End Block]
	End Select
End Function

Function CreateProp%(File$)
	Local p.Props
	
	; ~ A hacky way to use .b3d format
	If Right(File, 2) = ".x"
		File = Left(File, Len(File) - 2)
	ElseIf Right(File, 4) = ".b3d"
		File = Left(File, Len(File) - 4)
	EndIf
	File = File + ".b3d"
	
	For p.Props = Each Props
		If p\File = File Then Return(CopyEntity(p\OBJ))
	Next
	
	p.Props = New Props
	p\File = File
	; ~ A hacky optimization (just copy models that loaded as variable). Also fixes wrong models folder if the CBRE was used
	p\OBJ = CheckForPropModel(File)
	Return(p\OBJ)
End Function

Function LoadRMesh%(File$)
	ClsColor(0, 0, 0)
	
	; ~ Read the file
	Local f% = ReadFile(File)
	
	If f = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), File))
	
	Local i%, j%, k%, x#, y#, z#, Yaw#
	Local Vertex%
	Local Temp1i%, Temp2i%, Temp3i%
	Local Temp1#, Temp2#, Temp3#
	Local Temp1s$, Temp2s$
	;Local HasTriggerBox% = False
	Local CollisionMeshes% = CreatePivot()
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	;ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
	;	HasTriggerBox = True
	Else
		RuntimeError(Format(Format(GetLocalString("runerr", "notrmesh"), File, "{0}"), IsRMesh, "{1}"))
	EndIf
	
	File = StripFileName(File)
	
	Local Count%, Count2%
	
	; ~ Drawn meshes
	Local Opaque%, Alpha%
	
	Opaque = CreateMesh()
	Alpha = CreateMesh()
	
	Count = ReadInt(f)
	
	Local ChildMesh%
	Local Surf%, Tex%[2], Brush%
	Local IsAlpha%
	Local u#, v#
	
	For i = 1 To Count ; ~ Drawn mesh
		ChildMesh = CreateMesh()
		
		Surf = CreateSurface(ChildMesh)
		
		Brush = CreateBrush()
		
		Tex[0] = 0 : Tex[1] = 0
		
		IsAlpha = 0
		
		For j = 0 To 1
			Temp1i = ReadByte(f)
			If Temp1i <> 0
				Temp1s = ReadString(f)
				If FileType(File + Temp1s) = 1 ; ~ Check if texture is existing in original path
					If Temp1i < 3
						If Instr(Temp1s, "_lm") <> 0
							Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s, 1 + 256)
						Else
							Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s)
						EndIf
					Else
						Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s, 3)
					EndIf
				ElseIf FileType(MapTexturesFolder + Temp1s) = 1 ; ~ If not, check the MapTexturesFolder
					If Temp1i < 3
						If Instr(Temp1s, "_lm") <> 0
							Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 1 + 256)
						Else
							Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s)
						EndIf
					Else
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 3)
					EndIf
				EndIf
				If Tex[j] <> 0
					If Temp1i = 1 Then TextureBlend(Tex[j], 5)
					If Instr(Lower(Temp1s), "_lm") <> 0 Then TextureBlend(Tex[j], 3)
					IsAlpha = 2
					If Temp1i = 3 Then IsAlpha = 1
					TextureCoords(Tex[j], 1 - j)
				EndIf
			EndIf
		Next
		
		If IsAlpha = 1
			If Tex[1] <> 0
				TextureBlend(Tex[1], 2)
				BrushTexture(Brush, Tex[1], 0, 1)
			Else
				BrushTexture(Brush, MissingTexture, 0, 1)
			EndIf
		Else
			If Tex[0] <> 0 And Tex[1] <> 0
				Local Temp$ = StripPath(TextureName(Tex[1]))
				
				For j = 0 To 1
					BrushTexture(Brush, Tex[j], 0, j + 1)
				Next
				BrushTexture(Brush, AmbientLightRoomTex, 0)
			Else
				For j = 0 To 1
					If Tex[j] <> 0
						BrushTexture(Brush, Tex[j], 0, j)
					Else
						BrushTexture(Brush, MissingTexture, 0, j)
					EndIf
				Next
			EndIf
		EndIf
		
		Surf = CreateSurface(ChildMesh)
		
		If IsAlpha > 0 Then PaintSurface(Surf, Brush)
		
		FreeBrush(Brush) : Brush = 0
		
		Count2 = ReadInt(f) ; ~ Vertices
		
		For j = 1 To Count2
			; ~ World coords
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			Vertex = AddVertex(Surf, x, y, z)
			
			; ~ Texture coords
			For k = 0 To 1
				u = ReadFloat(f) : v = ReadFloat(f)
				VertexTexCoords(Surf, Vertex, u, v, 0.0, k)
			Next
			
			; ~ Colors
			Temp1i = ReadByte(f)
			Temp2i = ReadByte(f)
			Temp3i = ReadByte(f)
			VertexColor(Surf, Vertex, Temp1i, Temp2i, Temp3i, 1.0)
		Next
		
		Count2 = ReadInt(f) ; ~ Polys
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
		Next
		
		If IsAlpha = 1
			AddMesh(ChildMesh, Alpha)
		Else
			AddMesh(ChildMesh, Opaque)
		EndIf
		EntityAlpha(ChildMesh, 0.0)
		EntityParent(ChildMesh, CollisionMeshes)
		HideEntity(ChildMesh)
	Next
	
	Local HiddenMesh%
	
	HiddenMesh = CreateMesh()
	
	Count = ReadInt(f) ; ~ Invisible collision mesh
	For i = 1 To Count
		Surf = CreateSurface(HiddenMesh)
		Count2 = ReadInt(f) ; ~ Vertices
		For j = 1 To Count2
			; ~ World coords
			x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
			Vertex = AddVertex(Surf, x, y, z)
		Next
		
		Count2 = ReadInt(f) ; ~ Polys
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
			AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
		Next
	Next
	
	; ~ Trigger boxes
;	If HasTriggerBox
;		Local Numb%, TB%
;		
;		Numb = ReadInt(f)
;		For TB = 0 To Numb - 1
;			Count = ReadInt(f)
;			For i = 1 To Count
;				Count2 = ReadInt(f)
;				For j = 1 To Count2
;					ReadFloat(f) : ReadFloat(f) : ReadFloat(f)
;				Next
;				Count2 = ReadInt(f)
;				For j = 1 To Count2
;					ReadInt(f) : ReadInt(f) : ReadInt(f)
;				Next
;			Next
;			ReadString(f)
;		Next
;	EndIf
	
	Local Model%
	
	Count = ReadInt(f) ; ~ Point entities
	For i = 1 To Count
		Temp1s = ReadString(f)
		Select Temp1s
			Case "screen"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadString(f)
				;[End Block]
			Case "waypoint"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				;[End Block]
			Case "light"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f) : ReadString(f) : ReadFloat(f)
				;[End Block]
			Case "light_fix"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadString(f) : ReadFloat(f) : ReadFloat(f)
				;[End Block]
			Case "spotlight"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f) : ReadString(f) : ReadFloat(f) : ReadString(f) : ReadInt(f) : ReadInt(f)
				;[End Block]
			Case "soundemitter"
				;[Block]
				ReadFloat(f)
				ReadFloat(f)
				ReadFloat(f)
				ReadInt(f)
				ReadFloat(f)
				;[End Block]
			Case "model"
				;[Block]
				Temp2s = ReadString(f)
				RuntimeError(Format(Format(GetLocalString("runerr", "model.support"), File, "{0}"), "GFX\Map\Props\" + Temp2s, "{1}"))
				;[End Block]
			Case "mesh"
				;[Block]
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				
				File = ReadString(f)
				Model = CreateProp("GFX\map\Props\" + File)
				
				PositionEntity(Model, Temp1, Temp2, Temp3)
				
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				RotateEntity(Model, Temp1, Temp2, Temp3)
				
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				ScaleEntity(Model, Temp1, Temp2, Temp3)
				
				EntityPickMode(Model, 2)
				EntityParent(Model, Opaque)
				ReadByte(f)
				EntityFX(Model, ReadInt(f))
				
				Temp2s = ReadString(f)
				If Temp2s <> ""
					Local Texture% = LoadTextureCheckingIfInCache(Temp2s)
					
					EntityTexture(Model, Texture)
					DeleteSingleTextureEntryFromCache(Texture) : Texture = 0
				EndIf
				;[End Block]
		End Select
	Next
	
	Local OBJ%
	
	Temp1i = CopyMesh(Alpha)
	FlipMesh(Temp1i)
	AddMesh(Temp1i, Alpha)
	FreeEntity(Temp1i) : Temp1i = 0
	
	If Brush <> 0 Then FreeBrush(Brush)
	
	AddMesh(Alpha, Opaque)
	FreeEntity(Alpha) : Alpha = 0
	
	EntityFX(Opaque, 3)
	
	EntityAlpha(HiddenMesh, 0.0)
	EntityAlpha(Opaque, 1.0)
	
	OBJ = CreatePivot()
	CreatePivot(OBJ) ; ~ Skip "meshes" object
	EntityParent(Opaque, OBJ)
	EntityPickMode(Opaque, 2)
	EntityParent(HiddenMesh, OBJ)
	CreatePivot(OBJ) ; ~ Skip "pointentites" object
	CreatePivot(OBJ) ; ~ Skip "solidentites" object
	EntityParent(CollisionMeshes, OBJ)
	
	CloseFile(f)
	
	Return(OBJ)
End Function

;Print("Hey Mark,")
;Print("stop losing this ffs")
;Print("Thanks for your time.")
;Print("")

Global ConsoleFont% = LoadFont("GFX\Fonts\Andale Mono.ttf")

SetFont(ConsoleFont)

Const LanguageFile$ = "Data\local.ini"

Global Language$ = IniGetString(GetEnv("AppData") + "\scpcb-ue\Data\options.ini", "Global", "Language")
Global LanguagePath$

IniWriteBuffer(LanguageFile)

Function SetLanguage%()
	If Language = "en"
		LanguagePath = ""
	Else
		LanguagePath = "Localization\" + Language + "\"
		IniWriteBuffer(LanguagePath + LanguageFile)
	EndIf
End Function

Global DoorModelID%[3]

Global CamPitch# = 0.0
Global CamYaw# = 0.0

.start

Local FileName$ = Input(GetLocalString("rmeshview", "load"))

Graphics3D(1280, 720, 32, 2)

Global GraphicWidthHalf% = GraphicsWidth() / 2
Global GraphicHeightHalf% = GraphicsHeight() / 2

SetLanguage()

DoorModelID[0] = LoadMesh_Strict("GFX\map\Props\Door01.x") ; ~ Default Door

DoorModelID[1] = LoadMesh_Strict("GFX\map\Props\ContDoorLeft.x") ; ~ Big Door Left

DoorModelID[2] = LoadMesh_Strict("GFX\map\Props\ContDoorRight.x") ; ~ Big Door Right

Local i%

For i = 0 To 2
	HideEntity(DoorModelID[i])
Next

Global AmbientLightRoomTex% = CreateTextureUsingCacheSystem(2, 2, 1 + 256)

TextureBlend(AmbientLightRoomTex, 2)
SetBuffer(TextureBuffer(AmbientLightRoomTex))
ClsColor(0, 0, 0)
Cls()
SetBuffer(BackBuffer())

LoadMissingTexture()

LoadMaterials("Data\materials.ini")

Global RoomMesh% = LoadRMesh(FileName)

DeleteTextureEntriesFromCache(DeleteMapTextures)

ScaleEntity(RoomMesh, 0.1, 0.1, 0.1)
EntityPickMode(RoomMesh, 2)
ShowEntity(RoomMesh)

Global CursorSphere% = CreateSphere()
ScaleEntity(CursorSphere, 5.0, 5.0, 5.0)
EntityColor(CursorSphere, 255.0, 0.0, 0.0)
EntityFX(CursorSphere, 1)

Global Camera% = CreateCamera()

TranslateEntity(Camera, 0.0, 5.0, 0.0)

HidePointer()

While (Not KeyHit(1))
	; ~ Mouselook
	
	Local DiffX% = MouseX() - GraphicWidthHalf
	Local DiffY% = MouseY() - GraphicHeightHalf
	
	MoveMouse(GraphicWidthHalf, GraphicHeightHalf)
	
	CamYaw = CamYaw - Float(DiffX) / 30.0
	While CamYaw >= 360.0
		CamYaw = CamYaw - 360.0
	Wend
	While CamYaw < 0.0
		CamYaw = CamYaw + 360.0
	Wend
	CamPitch = CamPitch + Float(DiffY) / 30.0
	CamPitch = Clamp(CamPitch, -90.0, 90.0)
	RotateEntity(Camera, CamPitch, CamYaw, 0.0, True)
	
	Local IsKeyDown42% = KeyDown(42)
	
	If KeyDown(17) Then MoveEntity(Camera, 0.0, 0.0, 1.0 + IsKeyDown42 * 2.0)
	
	If KeyDown(30) Then MoveEntity(Camera, -1.0 - IsKeyDown42 * 2.0, 0.0, 0.0)
	If KeyDown(32) Then MoveEntity(Camera, 1.0 + IsKeyDown42 * 2.0, 0.0, 0.0)
	
	If KeyDown(31) Then MoveEntity(Camera, 0.0, 0.0, -1.0 - IsKeyDown42 * 2.0)
	
	If KeyDown(57) Then TranslateEntity(Camera, 0.0, 4.0 + IsKeyDown42 * 8.0, 0.0)
	
	SetBuffer(BackBuffer())
	
	CameraPick(Camera, GraphicWidthHalf, GraphicHeightHalf)
	
	Local PX# = PickedX()
	Local PY# = PickedY()
	Local PZ# = PickedZ()
	
	PositionEntity(CursorSphere, PX, PY, PZ, True)
	
	Local Dist# = 2.5
	
	ScaleEntity(CursorSphere, Dist, Dist, Dist)
	
	RenderWorld()
	
	Color(0, 255, 0)
	Text(5, 5, GetLocalString("rmeshview", "exit"))
	Text(5, 25, GetLocalString("rmeshview", "load.new"))
	Text(5, 45, Format(GetLocalString("rmeshview", "pick.x"), Str(PX * 10.0)))
	Text(5, 65, Format(GetLocalString("rmeshview", "pick.y"), Str(PY * 10.0)))
	Text(5, 85, Format(GetLocalString("rmeshview", "pick.z"), Str(PZ * 10.0)))
	
	AmbientLight(30.0, 30.0, 30.0)
	
	If KeyHit(18)
		DeleteTextureEntriesFromCache(DeleteAllTextures)
		FreeEntity(RoomMesh) : RoomMesh = 0
		For i = 0 To 2
			FreeEntity(DoorModelID[i]) : DoorModelID[i] = 0
		Next
		AmbientLightRoomTex = 0
		MissingTexture = 0
		FreeEntity(Camera) : Camera = 0
		FreeEntity(CursorSphere) : CursorSphere = 0
		GraphicWidthHalf = 0.0 : GraphicHeightHalf = 0.0
		
		Local p.Props
		
		For p.Props = Each Props
			Delete(p)
		Next
		ClearWorld()
		EndGraphics()
		Goto start
	EndIf
	Flip()
Wend
;~IDEal Editor Parameters:
;~F#9#D#11#15#1A#1E#22#28#34#3F#43#55#67#7A#B9#C4#CA#DF#E6#EC
;~F#104#10F#11A#124#13F#148#15D#162#167#16B#16F#173#179#18F#25D#264#26A#271#278#27F
;~F#287#28C#2D9
;~C#Blitz3D_TSS