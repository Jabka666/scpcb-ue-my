Include "Source Code\Strict_Functions_Core_3D.bb"
Include "Source Code\INI_Core_3D.bb"
Include "Source Code\Math_Core_3D.bb"
Include "Source Code\Materials_Core_3D.bb"
Include "Source Code\Texture_Cache_Core_3D.bb"

Global Language$ = IniGetString(GetEnv("AppData") + "\scpcb-ue\Data\options.ini", "Global", "Language")
Global LanguagePath$

Const LanguageFile$ = "Data\local.ini"

IniWriteBuffer("..\" + LanguageFile)

Function SetLanguage%()
	If Language = "en" Then
		LanguagePath = ""
	Else
		LanguagePath = "Localization\" + Language + "\"
		IniWriteBuffer("..\" + LanguagePath + LanguageFile)
	EndIf
End Function

SetLanguage()

Const ClrR% = 50, ClrG% = 50, ClrB% = 50

Global MouseHit1%

Const ResWidth% = 895
Const ResHeight% = 560

Graphics3D(ResWidth, ResHeight, 0, 4)

Local HHWND% = api_GetActiveWindow() ; ~ User32.dll

api_ShowWindow(HHWND, 0) ; ~ User32.dll
SetBuffer(BackBuffer())

AppTitle("MapCreator 3-D View") ; ~ Do not localize this

Global Camera% = CreateCamera()

LoadOptionsINI()

CameraClsColor(Camera, opt\FogR, opt\FogG, opt\FogB)

CameraRange(Camera, 0.05, opt\CamRange)
PositionEntity(Camera, 0.0, 1.0, 0.0)

Global AmbientLightRoomTex%

AmbientLightRoomTex = CreateTextureUsingCacheSystem(2, 2, 1 + 256)
TextureBlend(AmbientLightRoomTex, 5)
SetBuffer(TextureBuffer(AmbientLightRoomTex))
ClsColor(0, 0, 0)
Cls()
SetBuffer(BackBuffer())

LoadMissingTexture()

Global MenuOpen% = True

Const ROOM1% = 0, ROOM2% = 1, ROOM2C% = 2, ROOM3% = 3, ROOM4% = 4

Global RoomTempID%

LoadRoomTemplates("..\Data\rooms.ini")

LoadMaterials("..\Data\materials.ini")

Const RoomScale# = 8.0 / 2048.0

Const MapGridSize% = 18

Type MapGrid
	Field Grid%[(MapGridSize + 1) ^ 2]
End Type

Global CurrGrid.MapGrid

Global ZoneTransValue1% = 13, ZoneTransValue2% = 7

Const MT_GridSize% = 19

Const ForestGridSize% = 10
Global ForestMeshWidth#

Global CurrMapGrid% = 0
; ~ 0: Facility
; ~ 1: Forest (SCP-860-1)
; ~ 2: Maintenance Tunnels

Global PickedRoom.Rooms
Global CurrSelectedRoom.Rooms

; ~ Objects Constants
;[Block]
Const MaxButtonModelIDAmount% = 2
Const MaxDoorModelIDAmount% = 5
Const MaxLeverModelIDAmount% = 2
Const MaxMiscModelIDAmount% = 1
;[End Block]

Type Objects
	Field ButtonModelID[MaxButtonModelIDAmount]
	Field DoorModelID[MaxDoorModelIDAmount]
	Field LeverModelID[MaxLeverModelIDAmount]
	Field MiscModelID%[MaxMiscModelIDAmount]
End Type

Global o.Objects = New Objects

ChangeDir("..")

Const FontsPath$ = "Data\fonts.ini"

Global ConsoleFont% = LoadFont_Strict("GFX\fonts\" + IniGetString(LanguagePath + FontsPath, "Console", "File", IniGetString(FontsPath, "Console", "File")), IniGetString(LanguagePath + FontsPath, "Console", "Size", IniGetString(FontsPath, "Console", "Size")))

Function LoadEntities%()
	Local i%

	; ~ [BUTTONS]
	
	o\ButtonModelID[0] = LoadMesh_Strict("GFX\map\Props\Button.b3d") ; ~ Button
	
	o\ButtonModelID[1] = LoadMesh_Strict("GFX\map\Props\ButtonKeycard.b3d") ; ~ Keycard Button
	
	For i = 0 To MaxButtonModelIDAmount - 1
		HideEntity(o\ButtonModelID[i])
	Next
	
	; ~ [DOORS]
	
	o\DoorModelID[0] = LoadMesh_Strict("GFX\map\Props\Door01.x") ; ~ Default Door
	
	o\DoorModelID[1] = LoadMesh_Strict("GFX\map\Props\ContDoorLeft.x") ; ~ Big Door Left
	
	o\DoorModelID[2] = LoadMesh_Strict("GFX\map\Props\ContDoorRight.x") ; ~ Big Door Right
	
	o\DoorModelID[3] = LoadMesh_Strict("GFX\map\Props\DoorFrame.b3d") ; ~ Door Frame
	
	o\DoorModelID[4] = LoadMesh_Strict("GFX\map\Props\ContDoorFrame.b3d") ; ~ Big Door Frame
	
	For i = 0 To MaxDoorModelIDAmount - 1
		HideEntity(o\DoorModelID[i])
	Next
	
	; ~ [LEVERS]
	
	o\LeverModelID[0] = LoadMesh_Strict("GFX\map\Props\LeverBase.b3d") ; ~ Lever Base
	
	o\LeverModelID[1] = LoadMesh_Strict("GFX\map\Props\LeverHandle.b3d") ; ~ Lever Handle
	
	For i = 0 To MaxLeverModelIDAmount - 1
		HideEntity(o\LeverModelID[i])
	Next
	
	; ~ [MISC]
	
	o\MiscModelID[0] = LoadRMesh("GFX\map\scp_860_1_wall.rmesh", Null)
	HideEntity(o\MiscModelID[0])
End Function

LoadEntities()
LoadRoomTemplateMeshes()

DeleteTextureEntriesFromCache(DeleteMapTextures)

ChangeDir("Map Creator")

Global CheckFPS%, ElapsedLoops%, FPS%

Global MXS# = 0.0, MYS# = 0.0

Type Mouse
	Field Mouse_Left_Limit%
	Field Mouse_Right_Limit%
	Field Mouse_Top_Limit%
	Field Mouse_Bottom_Limit%
	Field Viewport_Center_X%
	Field Viewport_Center_Y%
End Type

Global mo.Mouse = New Mouse

mo\Mouse_Left_Limit% = 250
mo\Mouse_Right_Limit% = GraphicsWidth() - 250
mo\Mouse_Top_Limit% = 150
mo\Mouse_Bottom_Limit% = GraphicsHeight() - 150

; ~ Viewport
mo\Viewport_Center_X = GraphicsWidth() / 2
mo\Viewport_Center_Y = GraphicsHeight() / 2

MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)

Global Faster% = False
Global Slower% = False
Global IsRemote% = True

PositionEntity(Camera, (MapGridSize / 2.0) * 8.0, 1.0, MapGridSize * 8.0)
RotateEntity(Camera, 0.0, 180.0, 0.0)
MXS = 180.0

Const Period# = 1000.0 / 60.0

Global PrevTime% = MilliSecs()
Global ElapsedTime#

InitErrorMsgs(9, True)
SetErrorMsg(0, Format(GetLocalString("error", "title.mc"), SystemProperty("blitzversion")) + Chr(10))
SetErrorMsg(1, Format(Format(GetLocalString("error", "date"), CurrentDate(), "{0}"), CurrentTime(), "{1}"))
SetErrorMsg(2, Format(Format(Format(GetLocalString("error", "cpu"), Trim(SystemProperty("cpuname")), "{0}"), SystemProperty("cpuarch"), "{1}"), GetEnv("NUMBER_OF_PROCESSORS"), "{2}"))
SetErrorMsg(3, Format(Format(Format(GetLocalString("error", "gpu"), GfxDriverName(CountGfxDrivers()), "{0}"), ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)), "{1}"), (TotalVidMem() / 1024), "{2}"))

SetErrorMsg(7, Format(GetLocalString("error", "ex"), "_CaughtError_") + Chr(10))
SetErrorMsg(8, GetLocalString("error", "shot")) 

Function CatchErrors%(Location$)
	SetErrorMsg(6, Format(GetLocalString("error", "error"), Location))
End Function

Repeat
	SetErrorMsg(4, Format(Format(Format(GetLocalString("error", "gpu"), GfxDriverName(CountGfxDrivers()), "{0}"), ((TotalVidMem() / 1024) - (AvailVidMem() / 1024)), "{1}"), (TotalVidMem() / 1024), "{2}"))
	SetErrorMsg(5, Format(Format(GetLocalString("error", "status"), ((TotalPhys() / 1024) - (AvailPhys() / 1024)), "{0}"), (TotalPhys() / 1024), "{1}"))
	
	Cls()
	
	If opt\ShowFPS Then
		If CheckFPS < MilliSecs() Then
			FPS = ElapsedLoops
			ElapsedLoops = 0
			CheckFPS = MilliSecs() + 1000
		EndIf
		ElapsedLoops = ElapsedLoops + 1
	EndIf
	
	ElapsedTime = ElapsedTime + Float(MilliSecs() - PrevTime) / Float(Period)
	PrevTime = MilliSecs()
	
	Local f%
	
	If FileType("CONFIG_OPTINIT.SI") = 1 Then
		f = ReadFile("CONFIG_OPTINIT.SI")
		
		opt\FogR = ReadInt(f) : opt\FogG = ReadInt(f) : opt\FogB = ReadInt(f)
		opt\CursorR = ReadInt(f) : opt\CursorG = ReadInt(f) : opt\CursorB = ReadInt(f)
		opt\VSync = ReadByte(f)
		opt\ShowFPS = ReadByte(f)
		opt\CamRange = ReadFloat(f)
		
		opt\CamRange = Max(opt\CamRange, 20.0)
		
		CameraClsColor(Camera, opt\FogR, opt\FogG, opt\FogB)
		CameraRange(Camera, 0.01, opt\CamRange * 2.0)
		
		CloseFile(f)
		DeleteFile("CONFIG_OPTINIT.SI")
	EndIf
	
	Local x%, y%, i%
	Local r.Rooms, rt.RoomTemplates
	Local Name$, Angle%
	Local eName$, eProb#
	
	If FileType("CONFIG_MAPINIT.SI") = 1 Then
		DeleteTextureEntriesFromCache(DeleteAllTextures)
		For r.Rooms = Each Rooms
			FreeEntity(r\OBJ)
			DeleteSingleTextureEntryFromCache(r\OverlayTex)
			Delete(r)
		Next
		If CurrGrid <> Null Then
			Delete(CurrGrid) : CurrGrid = Null
		EndIf
		CurrGrid = New MapGrid
		
		f = ReadFile("CONFIG_MAPINIT.SI")
		
		ReadLine(f) ; ~ Author
		ReadLine(f) ; ~ Description
		ZoneTransValue1 = ReadByte(f)
		ZoneTransValue2 = ReadByte(f)
		
		Local RoomAmount% = ReadInt(f) ; ~ Amount of rooms
		Local ForestAmount% = ReadInt(f) ; ~ Amount of forest grid parts
		Local MTRoomAmount% = ReadInt(f) ; ~ Amount of maintenance tunnel rooms
		
		CurrMapGrid = ReadInt(f)
		
		Select CurrMapGrid
			Case 0
				;[Block]
				; ~ Room data
				For i = 0 To RoomAmount - 1
					x = ReadByte(f)
					y = ReadByte(f)
					Name = Lower(ReadString(f))
					
					Angle = ReadByte(f) * 90
					
					eName = ReadString(f)
					eProb = ReadFloat(f)
					
					For rt.RoomTemplates = Each RoomTemplates
						If Lower(rt\Name) = Name Then
							r = PlaceRoom(Name, MapGridSize - x, y, GetZone(y), rt\Shape, eName, eProb)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90 And r\Angle <> 270 Then
								r\Angle = r\Angle + 180
							EndIf
							r\Angle = WrapAngle(r\Angle)
							
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							
							CurrGrid\Grid[(MapGridSize - x) + (y * MapGridSize)] = 1
							
							Exit
						EndIf
					Next
					
					Local IsSelRoom% = ReadByte(f)
					
					If IsSelRoom Then
						PositionEntity(Camera, (MapGridSize - x) * 8.0, 1.0, y * 8.0)
						RotateEntity(Camera, 0.0, Angle, 0.0)
						MXS = -Angle
						MYS = 0.0
					EndIf
				Next
				;[End Block]
			Case 1
				;[Block]
				; ~ Skip room data
				For i = 0 To RoomAmount - 1
					ReadByte(f)
					ReadByte(f)
					ReadString(f)
					ReadByte(f)
					ReadString(f)
					ReadFloat(f)
					ReadByte(f)
				Next
				; ~ Forest data
				For i = 0 To ForestAmount - 1
					x = ReadByte(f)
					y = ReadByte(f)
					Name = Lower(ReadString(f))
					
					Angle = ReadByte(f) * 90
					
					For rt.RoomTemplates = Each RoomTemplates
						If Lower(rt\Name) = Name Then
							r = PlaceRoom(Name, ForestGridSize - x, y, GetZone(y), rt\Shape, "", 0.0, 1)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90 And r\Angle <> 270 Then
								r\Angle = r\Angle + 180
							EndIf
							
							r\Angle = WrapAngle(r\Angle)
							
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							
							Exit
						EndIf
					Next
					
					IsSelRoom = ReadByte(f)
					If IsSelRoom Then
						PositionEntity(Camera, (ForestGridSize - x) * 12.0, 1.0, y * 12.0)
						RotateEntity(Camera, 0.0, Angle, 0.0)
						MXS = -Angle
						MYS = 0.0
					EndIf
				Next
				;[End Block]
			Case 2
				;[Block]
				; ~ Skip room data
				For i = 0 To RoomAmount - 1
					ReadByte(f)
					ReadByte(f)
					ReadString(f)
					ReadByte(f)
					ReadString(f)
					ReadFloat(f)
					ReadByte(f)
				Next
				; ~ Skip forest data
				For i = 0 To ForestAmount - 1
					ReadByte(f)
					ReadByte(f)
					ReadString(f)
					ReadByte(f)
					ReadByte(f)
				Next
				; ~ Maintenance tunnel data
				For i = 0 To MTRoomAmount - 1
					x = ReadByte(f)
					y = ReadByte(f)
					Name = Lower(ReadString(f))
					
					Angle = ReadByte(f) * 90
					
					For rt.RoomTemplates = Each RoomTemplates
						If Lower(rt\Name) = Name Then
							r = PlaceRoom(Name, MT_GridSize - x, y, GetZone(y), rt\Shape, "", 0.0, 2)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90 And r\Angle <> 270 Then
								r\Angle = r\Angle + 180
							EndIf
							If rt\Shape = ROOM2C Lor rt\Shape = ROOM3 Then
								r\Angle = r\Angle - 90
							EndIf
							r\Angle = WrapAngle(r\Angle)
							
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							
							Exit
						EndIf
					Next
					
					IsSelRoom = ReadByte(f)
					If IsSelRoom Then
						PositionEntity(Camera, (MT_GridSize - x) * 2.0, 1.0, y * 2.0)
						RotateEntity(Camera, 0.0, Angle, 0.0)
						MXS = -Angle
						MYS = 0.0
					EndIf
				Next
				;[End Block]
		End Select
		
		DeleteTextureEntriesFromCache(DeleteMapTextures)
		
		CloseFile(f)
		
		DeleteFile("CONFIG_MAPINIT.SI")
	EndIf
	
	While ElapsedTime > 0.0
		MouseHit1 = MouseHit(1)
		
		If MouseHit(2) Then
			IsRemote = (Not IsRemote)
			MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
		EndIf
		
		For r.Rooms = Each Rooms
			If r\ResetOverlayTex And r <> PickedRoom
				SetBuffer(TextureBuffer(r\OverlayTex))
				If CurrMapGrid <> 1 Then
					ClsColor(0, 0, 0)
				Else
					ClsColor(255, 255, 255)
				EndIf
				Cls()
				SetBuffer(BackBuffer())
				r\ResetOverlayTex = False
			EndIf
			PickedRoom = Null
			If CurrMapGrid <> 1 Then
				If EntityDistanceSquared(Camera, r\OBJ) > PowTwo(opt\CamRange) Lor (Not EntityInView(GetChild(r\OBJ, 2), Camera))
					If (Not EntityHidden(r\OBJ)) Then HideEntity(r\OBJ)
				Else
					If EntityHidden(r\OBJ) Then ShowEntity(r\OBJ)
				EndIf
			Else
				If EntityDistanceSquared(Camera, r\OBJ) > PowTwo(opt\CamRange) Lor (Not EntityInView(r\OBJ, Camera))
					If (Not EntityHidden(r\OBJ)) Then HideEntity(r\OBJ)
				Else
					If EntityHidden(r\OBJ) Then ShowEntity(r\OBJ)
				EndIf
			EndIf
		Next
		
		If (Not IsRemote) Then
			HidePointer()
			
			If (MouseX() > mo\Mouse_Right_Limit) Lor (MouseX() < mo\Mouse_Left_Limit) Lor (MouseY() > mo\Mouse_Bottom_Limit) Lor (MouseY() < mo\Mouse_Top_Limit)
				MoveMouse(mo\Viewport_Center_X, mo\Viewport_Center_Y)
			EndIf
			
			MXS = MXS + MouseXSpeed() * 0.25
			MYS = MYS + MouseYSpeed() * 0.25
			
			MYS = Max(Min(MYS, 85.0), -85.0)
			
			RotateEntity(Camera, MYS, -MXS, 0.0)
			
			Faster = 0
			If KeyDown(42) Then Faster = 1
			Slower = 0
			If KeyDown(29) Then Slower = 1
			
			If KeyDown(17) Then MoveEntity(Camera, 0.0, 0.0, (0.05 + (0.05 * Faster)) / (1.0 + Slower))
			If KeyDown(30) Then MoveEntity(Camera, (-0.05 - (0.05 * Faster)) / (1.0 + Slower), 0.0, 0.0)
			If KeyDown(31) Then MoveEntity(Camera, 0.0, 0.0, (-0.05 - (0.05 * Faster)) / (1.0 + Slower))
			If KeyDown(32) Then MoveEntity(Camera, (0.05 + (0.05 * Faster)) / (1.0 + Slower), 0.0, 0.0)
			
			Local Picker% = EntityPick(Camera, opt\CamRange / (2.5 - (CurrMapGrid = 1)))
			
			If Picker <> 0 Then
				For r.Rooms = Each Rooms
					If CurrMapGrid <> 1 Then
						If PickedEntity() = GetChild(r\OBJ, 2)
							SetBuffer(TextureBuffer(r\OverlayTex))
							ClsColor(70, 70, 20 + (Float(Sin(MilliSecs() / 4.0)) * 20))
							Cls()
							SetBuffer(BackBuffer())
							PickedRoom = r
							r\ResetOverlayTex = True
							Exit
						EndIf
					Else
						If PickedEntity() = r\OBJ
							SetBuffer(TextureBuffer(r\OverlayTex))
							ClsColor(60, 60, 50 - (Float(Sin(MilliSecs() / 4.0)) * 50))
							Cls()
							SetBuffer(BackBuffer())
							PickedRoom = r
							r\ResetOverlayTex = True
							Exit
						EndIf
					EndIf
				Next
			EndIf
			If MouseHit1 Then
				If PickedRoom <> Null Then
					CurrSelectedRoom = PickedRoom
					f = WriteFile("CONFIG_TO2D.SI")
					WriteInt(f, CurrSelectedRoom\GridX)
					WriteInt(f, CurrSelectedRoom\GridZ)
					CloseFile(f)
				Else
					CurrSelectedRoom = Null
				EndIf
			EndIf
		Else
			ShowPointer()
		EndIf
		
		ElapsedTime = ElapsedTime - 1.0 ; ~ Indicate that a frame has been processed
		
		CaptureWorld() ; ~ Capture this game state, tweening will make it look smooth
	Wend
	RenderWorld(1.0 - Max(Min(ElapsedTime, 0.0), -1.0))
	
	If (Not IsRemote) Then
		SetFont(ConsoleFont)
		If opt\ShowFPS Then
			Color(0, 0, 0)
			Rect(2, 2, StringWidth("FPS: " + FPS), StringHeight("FPS: " + FPS))
			
			Color(255, 255, 255)
			Text(2, 2, "FPS: " + FPS)
		EndIf
		
		If PickedRoom <> Null Then
			Local rName$ = PickedRoom\RoomTemplate\Name
			Local rX% = Int(PickedRoom\x)
			Local rZ% = Int(PickedRoom\z)
			Local rAngle% = Int(PickedRoom\Angle)
			
			Color(0, 0, 0)
			Rect(2, 32, StringWidth(Format(GetLocalString("mc", "room.name"), rName)), StringHeight(Format(GetLocalString("mc", "room.name"), rName)))
			Rect(2, 52, StringWidth(Format(GetLocalString("mc", "room.x"), rX)), StringHeight(Format(GetLocalString("mc", "room.x"), rX)))
			Rect(2, 72, StringWidth(Format(GetLocalString("mc", "room.z"), rZ)), StringHeight(Format(GetLocalString("mc", "room.z"), rZ)))
			
			Color(255, 255, 255)
			Text(2, 32, Format(GetLocalString("mc", "room.name"), rName))
			Text(2, 52, Format(GetLocalString("mc", "room.x"), rX))
			Text(2, 72, Format(GetLocalString("mc", "room.z"), rZ))
			
			If PickedRoom\Event <> "" Then
				eName$ = PickedRoom\Event
				
				Local eChance# = PickedRoom\EventChance
				
				Color(0, 0, 0)
				Rect(2, 92, StringWidth(Format(GetLocalString("mc", "room.event"),  eName)), StringHeight(Format(GetLocalString("mc", "room.event"),  eName)))
				Rect(2, 112, StringWidth(Format(GetLocalString("mc", "room.event.chance"), Int(eChance * 100))), StringHeight(Format(GetLocalString("mc", "room.event.chance"), Int(eChance * 100))))
				
				Color(255, 255, 255)
				Text(2, 92, Format(GetLocalString("mc", "room.event"),  eName))
				Text(2, 112, Format(GetLocalString("mc", "room.event.chance"), Int(eChance * 100)))
			EndIf
		EndIf
		
		If CurrSelectedRoom <> Null Then
			rName = CurrSelectedRoom\RoomTemplate\Name
			
			Color(0, 0, 0)
			Rect(ResWidth - 2) - StringWidth(Format(GetLocalString("mc", "room.selected"), rName)), 2, StringWidth(Format(GetLocalString("mc", "room.selected"), rName)), StringHeight(Format(GetLocalString("mc", "room.selected"), rName))
			
			Color(255, 255, 255)
			Text((ResWidth - 2) - StringWidth(Format(GetLocalString("mc", "room.selected"), rName)), 2, Format(GetLocalString("mc", "room.selected"), rName))
		EndIf
		
		Color(opt\CursorR, opt\CursorG, opt\CursorB)
		Rect((ResWidth / 2) - 25, (ResHeight / 2) - 2.5, 20, 5, True)
		Rect((ResWidth / 2) + 5, (ResHeight / 2) - 2.5, 20, 5, True)
		Rect((ResWidth / 2) - 2.5, (ResHeight / 2) - 25, 5, 20, True)
		Rect((ResWidth / 2) - 2.5, (ResHeight / 2) + 5, 5, 20, True)
	EndIf
	
	If opt\VSync Then
		Flip(True)
	Else
		Flip(False)
	EndIf
Until (Not api_FindWindow("BlitzMax_Window_Class", "SCP-CB Ultimate Edition Reborn Map Creator"))
End()

Type RoomTemplates
	Field OBJ%, ID%
	Field OBJPath$
	Field Zone%[5]
	Field Shape%, Name$
End Type

Function CreateRoomTemplate.RoomTemplates(MeshPath$)
	Local rt.RoomTemplates
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\" + MeshPath
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates%(File$)
	CatchErrors("Uncaught (LoadRoomTemplates)")
	
	Local TemporaryString$, i%
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			If TemporaryString <> "room ambience" Then
				StrTemp = IniGetString(File, TemporaryString, "Mesh Path")
				
				rt.RoomTemplates = CreateRoomTemplate(StrTemp)
				rt\Name = Lower(TemporaryString)
				
				StrTemp = IniGetString(File, TemporaryString, "Shape")
				
				Select StrTemp
					Case "room1", "1"
						;[Block]
						rt\Shape = ROOM1
						;[End Block]
					Case "room2", "2"
						;[Block]
						rt\Shape = ROOM2
						;[End Block]
					Case "room2C", "2C"
						;[Block]
						rt\Shape = ROOM2C
						;[End Block]
					Case "room3", "3"
						;[Block]
						rt\Shape = ROOM3
						;[End Block]
					Case "room4", "4"
						;[Block]
						rt\Shape = ROOM4
						;[End Block]
				End Select
				
				For i = 0 To 4
					rt\Zone[i] = IniGetInt(File, TemporaryString, "Zone" + (i + 1))
				Next
			EndIf
		EndIf
	Wend
	
	CloseFile(f)
	
	CatchErrors("LoadRoomTemplates")
End Function

Function LoadRoomTemplateMeshes%()
	CatchErrors("Uncaught (LoadRoomTemplatesMeshes)")
	
	Local rt.RoomTemplates, i%
	Local MT_Prefix$ = "maintenance tunnel "
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt1.rmesh"
	rt\Name = MT_Prefix + "endroom"
	rt\Shape = ROOM1
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt2.rmesh"
	rt\Name = MT_Prefix + "corridor"
	rt\Shape = ROOM2
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt2c.rmesh"
	rt\Name = MT_Prefix + "corner"
	rt\Shape = ROOM2C
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt3.rmesh"
	rt\Name = MT_Prefix + "t-shaped room"
	rt\Shape = ROOM3
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt4.rmesh"
	rt\Name = MT_Prefix + "4-way room"
	rt\Shape = ROOM4
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt2_elevator.rmesh"
	rt\Name = MT_Prefix + "elevator"
	rt\Shape = ROOM2
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\mt1_generator.rmesh"
	rt\Name = MT_Prefix + "generator room"
	rt\Shape = ROOM1
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\OBJPath <> "" Then
			LoadRoomMesh(rt)
		EndIf
	Next
	
	Local hMap%[ROOM4 + 1], Mask%[ROOM4 + 1]
	Local GroundTexture% = LoadTexture_Strict("GFX\map\textures\forestfloor.jpg")
	Local PathTexture% = LoadTexture_Strict("GFX\map\textures\forestpath.jpg")
	
	hMap[ROOM1] = LoadImage_Strict("GFX\map\forest\forest1h.png")
	Mask[ROOM1] = LoadTexture_Strict("GFX\map\forest\forest1h_mask.png", 1 + 2)
	hMap[ROOM2] = LoadImage_Strict("GFX\map\forest\forest2h.png")
	Mask[ROOM2] = LoadTexture_Strict("GFX\map\forest\forest2h_mask.png", 1 + 2)
	hMap[ROOM2C] = LoadImage_Strict("GFX\map\forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture_Strict("GFX\map\forest\forest2Ch_mask.png", 1 + 2)
	hMap[ROOM3] = LoadImage_Strict("GFX\map\forest\forest3h.png")
	Mask[ROOM3] = LoadTexture_Strict("GFX\map\forest\forest3h_mask.png", 1 + 2)
	hMap[ROOM4] = LoadImage_Strict("GFX\map\forest\forest4h.png")
	Mask[ROOM4] = LoadTexture_Strict("GFX\map\forest\forest4h_mask.png", 1 + 2)
	
	Local FR_Prefix$ = "scp-860-1 "
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2], 0.03, GroundTexture, PathTexture, Mask[ROOM2])
	rt\Name = FR_Prefix + "door"
	rt\Shape = ROOM2
	ForestMeshWidth = MeshWidth(rt\OBJ)
	HideEntity(rt\OBJ)
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM1], 0.03, GroundTexture, PathTexture, Mask[ROOM1])
	rt\Name = FR_Prefix + "endroom"
	rt\Shape = ROOM1
	HideEntity(rt\OBJ)
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2], 0.03, GroundTexture, PathTexture, Mask[ROOM2])
	rt\Name = FR_Prefix + "path"
	rt\Shape = ROOM2
	HideEntity(rt\OBJ)
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2C], 0.03, GroundTexture, PathTexture, Mask[ROOM2C])
	rt\Name = FR_Prefix + "corner"
	rt\Shape = ROOM2C
	HideEntity(rt\OBJ)
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM3], 0.03, GroundTexture, PathTexture, Mask[ROOM3])
	rt\Name = FR_Prefix + "t-shaped path"
	rt\Shape = ROOM3
	HideEntity(rt\OBJ)
	rt.RoomTemplates = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM4], 0.03, GroundTexture, PathTexture, Mask[ROOM4])
	rt\Name = FR_Prefix + "4-way path"
	rt\Shape = ROOM4
	HideEntity(rt\OBJ)
	
	DeleteSingleTextureEntryFromCache(GroundTexture)
	DeleteSingleTextureEntryFromCache(PathTexture)
	For i = ROOM1 To ROOM4
		FreeImage(hMap[i])
		DeleteSingleTextureEntryFromCache(Mask[i])
	Next
	
	CatchErrors("LoadRoomTemplatesMeshes")
End Function

Function PlaceRoom.Rooms(Name$, x%, z%, Zone%, Shape%, Event$ = "", EventChance# = 1.0, MapGrid% = 0)
	CatchErrors("Uncaught (PlaceRoom)")
	
	Local rt.RoomTemplates, r.Rooms
	Local Spacing#
	
	Select MapGrid
		Case 0
			;[Block]
			Spacing = 8.0
			;[End Block]
		Case 1
			;[Block]
			Spacing = 12.0
			;[End Block]
		Case 2
			;[Block]
			Spacing = 2.0
			;[End Block]
	End Select
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\Name = Name
			r.Rooms = CreateRoom(Zone, Shape, x * Spacing, 0.0, z * Spacing, Name)
			Exit
		EndIf
	Next
	
	r\Event = Event
	If r\Event <> "" Then r\EventChance = EventChance
	
	Return(r)
	
	CatchErrors("PlaceRoom")
End Function

Type Props
	Field File$
	Field OBJ%
End Type

Function CheckForPropModel%(File$)
	Local Path$ = "GFX\map\Props\"
	
	Select File
		Case Path + "button.b3d"
			;[Block]
			Return(CopyEntity(o\ButtonModelID[0]))
			;[End Block]
		Case Path + "buttonkeycard.b3d"
			;[Block]
			Return(CopyEntity(o\ButtonModelID[1]))
			;[End Block]
		Case Path + "door01.b3d"
			;[Block]
			Return(CopyEntity(o\DoorModelID[0]))
			;[End Block]
		Case Path + "contdoorleft.b3d"
			;[Block]
			Return(CopyEntity(o\DoorModelID[1]))
			;[End Block]
		Case Path + "contdoorright.b3d"
			;[Block]
			Return(CopyEntity(o\DoorModelID[2]))
			;[End Block]
		Case Path + "doorframe.b3d"
			;[Block]
			Return(CopyEntity(o\DoorModelID[3]))
			;[End Block]
		Case Path + "contdoorframe.b3d"
			;[Block]
			Return(CopyEntity(o\DoorModelID[4]))
			;[End Block]
		Case Path + "leverbase.b3d"
			;[Block]
			Return(CopyEntity(o\LeverModelID[0]))
			;[End Block]
		Case Path + "leverhandle.b3d"
			;[Block]
			Return(CopyEntity(o\LeverModelID[1]))
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
	If Right(File, 2) = ".x" Then
		File = Left(File, Len(File) - 2)
	ElseIf Right(File, 4) = ".b3d"
		File = Left(File, Len(File) - 4)
	EndIf
	File = File + ".b3d"
	
	For p.Props = Each Props
		If p\File = File Then
			Return(CopyEntity(p\OBJ))
		EndIf
	Next
	
	p.Props = New Props
	p\File = File
	; ~ A hacky optimization (just copy models that loaded as variable). Also fixes wrong models folder if the CBRE was used
	p\OBJ = CheckForPropModel(File)
	Return(p\OBJ)
End Function

Type Rooms
	Field Zone%
	Field Found%
	Field OBJ%
	Field x#, y#, z#
	Field Angle%
	Field RoomTemplate.RoomTemplates
	Field Dist#
	Field OverlapCheckBox%
	Field RoomType%
	Field OverlayTex%
	Field ResetOverlayTex%
	Field Event$
	Field EventChance#
	Field GridX%, GridZ%
	Field ForestWallOBJ%
End Type 

Function CreateRoom.Rooms(Zone%, RoomShape%, x#, y#, z#, Name$ = "")
	CatchErrors("Uncaught (CreateRoom)")
	
	Local r.Rooms
	Local rt.RoomTemplates
	Local Tempf1#, Tempf2#, Tempf3#
	
	r.Rooms = New Rooms
	r\Zone = Zone
	r\RoomType = RoomShape
	r\x = x : r\y = y : r\z = z
	
	If Name <> "" Then
		Name = Lower(Name)
		For rt.RoomTemplates = Each RoomTemplates
			If rt\Name = Name Then
				r\RoomTemplate = rt
				
				r\OBJ = CopyEntity(rt\OBJ)
				If CurrMapGrid <> 1 Then
					ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
				Else
					Tempf3 = ForestMeshWidth
					Tempf1 = 12.0 / Tempf3
					ScaleEntity(r\OBJ, Tempf1, Tempf1, Tempf1)
				EndIf
				
				PositionEntity(r\OBJ, x, y, z)
				
				If Name = "scp-860-1 door" Then
					r\ForestWallOBJ = CopyEntity(o\MiscModelID[0])
					ScaleEntity(r\ForestWallOBJ, RoomScale, RoomScale, RoomScale)
					PositionEntity(r\ForestWallOBJ, x, y, z, True)
					EntityParent(r\ForestWallOBJ, r\OBJ)
					RotateEntity(r\ForestWallOBJ, 0.0, 180.0, 0.0)
					MoveEntity(r\ForestWallOBJ, 0.0, 0.0, -(14.0 + Tempf1))
				EndIf
				
				If CurrMapGrid <> 1 Then
					r\OverlayTex = CreateTextureUsingCacheSystem(1, 1)
					SetBuffer(TextureBuffer(r\OverlayTex))
					ClsColor(0, 0, 0)
					Cls()
					SetBuffer(BackBuffer())
					EntityTexture(GetChild(r\OBJ, 2), r\OverlayTex, 0, 0)
				Else
					r\OverlayTex = CreateTextureUsingCacheSystem(1, 1)
					TextureBlend(r\OverlayTex, 5)
					SetBuffer(TextureBuffer(r\OverlayTex))
					ClsColor(255, 255, 255)
					Cls()
					SetBuffer(BackBuffer())
					EntityTexture(r\OBJ, r\OverlayTex, 0, 0)
				EndIf
				
				Return(r)
			EndIf
		Next
	EndIf
	
	CatchErrors("CreateRoom")
End Function

Function CreateOverLapBox%(r.Rooms)
	Local s%
	Local SizeAdd# = 0.02
	
	If r\RoomTemplate\Name = "gate_b" Then Return
	If r\RoomTemplate\Name = "gate_a" Then Return
	If r\RoomTemplate\Name = "cont2_049" Then Return
	If r\RoomTemplate\Name = "cont2_008" Then Return
	If r\RoomTemplate\Name = "cont2_409" Then Return
	If r\RoomTemplate\Name = "room3_storage" Then Return
	If r\RoomTemplate\Name = "cont1_106" Then Return
	If r\RoomTemplate\Name = "cont1_079" Then Return
	If r\RoomTemplate\Name = "gate_b_entrance" Then Return
	If r\RoomTemplate\Name = "gate_a_entrance" Then Return
	
	r\OverlapCheckBox = CreateMesh()
	GetMeshExtents(GetChild(r\OBJ, 2))
	s = CreateSurface(r\OverlapCheckBox)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MinY + SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MinY + SizeAdd, Mesh_MinZ + SizeAdd)
	AddTriangle(s, 0, 1, 2)
	AddTriangle(s, 0, 2, 3)
	s = CreateSurface(r\OverlapCheckBox)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MinY + SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MinY + SizeAdd, Mesh_MinZ + SizeAdd)
	AddTriangle(s, 0, 1, 2)
	AddTriangle(s, 0, 2, 3)
	s = CreateSurface(r\OverlapCheckBox)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MinY + SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MaxX - SizeAdd, Mesh_MinY + SizeAdd, Mesh_MaxZ - SizeAdd)
	AddTriangle(s, 0, 1, 2)
	AddTriangle(s, 0, 2, 3)
	s = CreateSurface(r\OverlapCheckBox)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MaxZ - SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MaxY - SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MinY + SizeAdd, Mesh_MinZ + SizeAdd)
	AddVertex(s, Mesh_MinX + SizeAdd, Mesh_MinY + SizeAdd, Mesh_MaxZ - SizeAdd)
	AddTriangle(s, 0, 1, 2)
	AddTriangle(s, 0, 2, 3)
	EntityAlpha(r\OverlapCheckBox, 0.5)
End Function

Function LoadRoomMesh%(rt.RoomTemplates)
	If Instr(rt\OBJPath, ".rmesh") <> 0 Then ; ~ File is RoomMesh
		rt\OBJ = LoadRMesh(rt\OBJPath, rt)
	ElseIf Instr(rt\OBJPath, ".b3d") <> 0 ; ~ File is .b3d
		RuntimeError(Format(GetLocalString("runerr", "b3d"), rt\OBJPath))
	Else ; ~ File not found
		RuntimeError(Format(GetLocalString("runerr", "notfound"), rt\OBJPath))
	EndIf
	
	If (Not rt\OBJ) Then RuntimeError(Format(GetLocalString("runerr", "failedload"), rt\OBJPath))
	
	HideEntity(rt\OBJ)
End Function

Function LoadRMesh%(File$, rt.RoomTemplates)
	CatchErrors("Uncaught (LoadRMesh)")
	
	ClsColor(0, 0, 0)
	
	; ~ Read the file
	Local f% = ReadFile(File)
	Local i%, j%, k%, x#, y#, z#, Yaw#
	Local Vertex%
	Local Temp1i%, Temp2i%, Temp3i%
	Local Temp1#, Temp2#, Temp3#
	Local Temp1s$, Temp2s$
	Local CollisionMeshes% = CreatePivot()
	Local HasTriggerBox% = False
	
	For i = 0 To 3 ; ~ Reattempt up to 3 times
		If f = 0 Then
			f = ReadFile(File)
		Else
			Exit
		EndIf
	Next
	If f = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), File))
	
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
		HasTriggerBox = True
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
			If Temp1i <> 0 Then
				Temp1s = ReadString(f)
				If FileType(File + Temp1s) = 1 ; ~ Check if texture is existing in original path
					If Temp1i < 3 Then
						Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s)
					Else
						Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s, 3)
					EndIf
				ElseIf FileType(MapTexturesFolder + Temp1s) = 1 ; ~ If not, check the MapTexturesFolder
					If Temp1i < 3 Then
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s)
					Else
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 3)
					EndIf
				EndIf
				If Tex[j] <> 0 Then
					If Temp1i = 1 Then TextureBlend(Tex[j], 5)
					If Instr(Lower(Temp1s), "_lm") <> 0 Then
						TextureBlend(Tex[j], 3)
					EndIf
					IsAlpha = 2
					If Temp1i = 3 Then IsAlpha = 1
					TextureCoords(Tex[j], 1 - j)
				EndIf
			EndIf
		Next
		
		If IsAlpha = 1 Then
			If Tex[1] <> 0 Then
				TextureBlend(Tex[1], 2)
				BrushTexture(Brush, Tex[1], 0, 1)
			Else
				BrushTexture(Brush, MissingTexture, 0, 1)
			EndIf
		Else
			If Tex[0] <> 0 And Tex[1] <> 0 Then
				For j = 0 To 1
					BrushTexture(Brush, Tex[j], 0, j + 2)
				Next
				BrushTexture(Brush, AmbientLightRoomTex, 1)
			Else
				For j = 0 To 1
					If Tex[j] <> 0 Then
						BrushTexture(Brush, Tex[j], 0, j + 1)
					Else
						BrushTexture(Brush, MissingTexture, 0, j + 1)
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
		
		If IsAlpha = 1 Then
			AddMesh(ChildMesh, Alpha)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
		Else
			AddMesh(ChildMesh, Opaque)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
		EndIf
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
	If HasTriggerBox Then
		Local Numb%, TB%
		
		Numb = ReadInt(f)
		For TB = 0 To Numb - 1
			Count = ReadInt(f)
			For i = 1 To Count
				Count2 = ReadInt(f)
				For j = 1 To Count2
					ReadFloat(f) : ReadFloat(f) : ReadFloat(f)
				Next
				Count2 = ReadInt(f)
				For j = 1 To Count2
					ReadInt(f) : ReadInt(f) : ReadInt(f)
				Next
			Next
			ReadString(f)
		Next
	EndIf
	
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
			Case "playerstart"
				;[Block]
				ReadFloat(f) : ReadFloat(f) : ReadFloat(f)
				ReadString(f)
				;[End Block]
			Case "model"
				;[Block]
				File = ReadString(f)
				
				Model = CreateProp("GFX\map\Props\" + File)
				
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				PositionEntity(Model, Temp1, Temp2, Temp3)
				
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				RotateEntity(Model, Temp1, Temp2, Temp3)
				
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				ScaleEntity(Model, Temp1, Temp2, Temp3)
				
				EntityParent(Model, Opaque)
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
				
				EntityParent(Model, Opaque)
				ReadByte(f)
				ReadInt(f)
				ReadString(f)
				;[End Block]
		End Select
	Next
	
	Local OBJ%
	
	Temp1i = CopyMesh(Alpha)
	FlipMesh(Temp1i)
	AddMesh(Temp1i, Alpha)
	FreeEntity(Temp1i)
	
	If Brush <> 0 Then FreeBrush(Brush)
	
	AddMesh(Alpha, Opaque)
	FreeEntity(Alpha)
	
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
	
	CatchErrors("LoadRMesh")
End Function

Function LoadTerrain%(HeightMap%, yScale# = 0.7, t1%, t2%, Mask%)
	; ~ Load the HeightMap
	If (Not HeightMap) Then RuntimeError(Format(GetLocalString("runerr", "heightmap"), HeightMap))
	
	; ~ Store heightmap dimensions
	Local x% = ImageWidth(HeightMap) - 1, y% = ImageHeight(HeightMap) - 1
	Local lX%, lY%, Index%
	
	; ~ Load texture and lightmaps
	If (Not t1) Then RuntimeError(Format(GetLocalString("runerr", "tex_1"), t1))
	If (Not t2) Then RuntimeError(Format(GetLocalString("runerr", "tex_2"), t2))
	If (Not Mask) Then RuntimeError(Format(GetLocalString("runerr", "mask"), Mask))
	
	; ~ Auto scale the textures to the right size
	If t1 Then ScaleTexture(t1, x / 4.0, y / 4.0)
	If t2 Then ScaleTexture(t2, x / 4.0, y / 4.0)
	If Mask Then ScaleTexture(Mask, x, y)
	
	; ~ Start building the terrain
	Local Mesh% = CreateMesh()
	Local Surf% = CreateSurface(Mesh)
	
	; ~ Create some verts for the terrain
	For lY = 0 To y
		For lX = 0 To x
			AddVertex(Surf, lX, 0.0, lY, 1.0 / lX, 1.0 / lY)
		Next
	Next
	RenderWorld()
	
	; ~ Connect the verts with faces
	For lY = 0 To y - 1
		For lX = 0 To x - 1
			AddTriangle(Surf, lX + ((x + 1) * lY), lX + ((x + 1) * lY) + (x + 1), (lX + 1) + ((x + 1) * lY))
			AddTriangle(Surf, (lX + 1) + ((x + 1) * lY), lX + ((x + 1) * lY) + (x + 1), (lX + 1) + ((x + 1) * lY) + (x + 1))
		Next
	Next
	
	; ~ Position the terrain to center
	Local Mesh2% = CopyMesh(Mesh, Mesh)
	Local Surf2% = GetSurface(Mesh2, 1)
	
	PositionMesh(Mesh, (-x) / 2.0, 0.0, (-y) / 2.0)
	PositionMesh(Mesh2, (-x) / 2.0, 0.01, (-y) / 2.0)
	
	; ~ Alter vertice height to match the heightmap red channel
	LockBuffer(ImageBuffer(HeightMap))
	LockBuffer(TextureBuffer(Mask))
	
	For lX = 0 To x
		For lY = 0 To y
			; ~ Ising vertex alpha and two meshes instead of FE_ALPHAWHATEVER
			; ~ It doesn't look perfect but it does the job
			; ~ You might get better results by downscaling the mask to the same size as the heightmap
			Local MaskX# = Min(lX * Float(TextureWidth(Mask)) / Float(ImageWidth(HeightMap)), TextureWidth(Mask) - 1.0)
			Local MaskY# = TextureHeight(Mask) - Min(lY * Float(TextureHeight(Mask)) / Float(ImageHeight(HeightMap)), TextureHeight(Mask) - 1.0)
			Local RGB%, RED%
			
			RGB = ReadPixelFast(Min(lX, x - 1.0), y - Min(lY, y - 1.0), ImageBuffer(HeightMap))
			RED = (RGB And $FF0000) Shr 16 ; ~ Separate out the red
			
			Local Alpha# = (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Max(MaskY - 5.0, 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, TextureWidth(Mask) - 5.0), Min(MaskY + 5.0, TextureHeight(Mask) - 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Min(MaskY + 5.0, TextureHeight(Mask) - 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, TextureWidth(Mask) - 5.0), Max(MaskY - 5.0, 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha * 0.25
			Alpha = Sqr(Alpha)
			
			Index = lX + ((x + 1) * lY)
			VertexCoords(Surf, Index , VertexX(Surf, Index), RED * yScale, VertexZ(Surf, Index))
			VertexCoords(Surf2, Index , VertexX(Surf2, Index), RED * yScale, VertexZ(Surf2, Index))
			VertexColor(Surf2, Index, 255.0, 255.0, 255.0, Alpha)
			; ~ Set the terrain texture coordinates
			VertexTexCoords(Surf, Index, lX, -lY )
			VertexTexCoords(Surf2, Index, lX, -lY) 
		Next
	Next
	UnlockBuffer(TextureBuffer(Mask))
	UnlockBuffer(ImageBuffer(HeightMap))
	
	UpdateNormals(Mesh)
	UpdateNormals(Mesh2)
	
	EntityTexture(Mesh, t1, 0, 1)
	EntityTexture(Mesh2, t2, 0, 1)
	
	EntityFX(Mesh, 1)
	EntityFX(Mesh2, 1 + 2 + 32)
	
	EntityPickMode(Mesh, 2)
	
	Return(Mesh)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS