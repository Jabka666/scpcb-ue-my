Const ClrR% = 50, ClrG% = 50, ClrB% = 50

Global MouseDown1%, MouseHit1%, MouseDown2%, MouseSpeedX#, MouseSpeedY#, MouseSpeedZ#
Global SelectedTextBox% = 0
Global PrevSelectedTextBox% = 0

Const AspectRatio# = 16.0 / 9.0

Global ResWidth% = 895
Global ResHeight% = 560
Global ResFactor# = ResHeight / 768.0

Graphics3D(ResWidth, ResHeight, 0, 2)
HHWND = api_GetActiveWindow() ; ~ User32.dll
api_ShowWindow(HHWND, 0) ; ~ User32.dll
SetBuffer(BackBuffer())

AppTitle("MapCreator 3D View")

Const OptionFileMC$ = "..\Data\options_MC.ini"
Const OptionFile$ = "..\Data\options.ini"

Global Camera% = CreateCamera()

Global CamColorR% = GetINIInt(OptionFileMC, "3D Scene", "BG Color R")
Global CamColorG% = GetINIInt(OptionFileMC, "3D Scene", "BG Color G")
Global CamColorB% = GetINIInt(OptionFileMC, "3D Scene", "BG Color B")
Global CursorColorR% = GetINIInt(OptionFileMC, "3D Scene", "Cursor Color R")
Global CursorColorG% = GetINIInt(OptionFileMC, "3D Scene", "Cursor Color G")
Global CursorColorB% = GetINIInt(OptionFileMC, "3D Scene", "Cursor Color B")

CameraClsColor(Camera, CamColorR, CamColorG, CamColorB)

Global CamRange# = GetINIFloat(OptionFileMC, "3D Scene", "Camera Range")

CameraRange(Camera, 0.05, CamRange)
PositionEntity(Camera, 0.0, 1.0, 0.0)

Global AmbientLightRoomTex% = CreateTexture(2, 2, 257)

TextureBlend(AmbientLightRoomTex, 5)
SetBuffer(TextureBuffer(AmbientLightRoomTex))
ClsColor(0, 0, 0)
Cls()
SetBuffer(BackBuffer())

; ~ Loading door-relevant meshes (for adjacent doors)
Global Door_LCZ% = LoadMesh("..\GFX\map\Door01.x")
HideEntity(Door_LCZ)

Global Door_HCZ_1% = LoadMesh("..\GFX\map\heavydoor1.x")
HideEntity(Door_HCZ_1)

Global Door_HCZ_2% = LoadMesh("..\GFX\map\heavydoor2.x")
HideEntity(Door_HCZ_2)

Global Door_Frame% = LoadMesh("..\GFX\map\DoorFrame.x")
HideEntity(Door_Frame)

Global Door_Button% = LoadMesh("..\GFX\map\Button.b3d")
HideEntity(Door_Button)

Global MenuOpen% = True

Const ROOM1% = 1, ROOM2% = 2, ROOM2C% = 3, ROOM3% = 4, ROOM4% = 5

Global Font1% = LoadFont("..\GFX\font\cour\Courier New.ttf", 16)

Global RoomTempID%

Const RoomsFile$ = "..\Data\rooms.ini"

LoadRoomTemplates(RoomsFile)

Const MaterialsFile$ = "..\Data\materials.ini"

LoadMaterials(MaterialsFile)

Const RoomScale# = 8.0 / 2048.0

Global MapWidth% = GetINIInt(OptionFile, "Global", "Map Size"), MapHeight% = GetINIInt(OptionFile, "Global", "Map Size")

Dim MapTemp%(MapWidth + 1, MapHeight + 1)
Dim MapFound%(MapWidth, MapHeight)

Dim MapName$(MapWidth, MapHeight)
Dim MapRoomID%(ROOM4 + 1)
Dim MapRoom$(ROOM4 + 1, 0)

Global ZoneTransValue1% = 13, ZoneTransValue2% = 7

Global MT_GridSize% = 19
Dim MTName$(MT_GridSize, MT_GridSize)

Global ForestGridSize% = 10
Dim ForestName$(ForestGridSize, ForestGridSize)
Global ForestMeshWidth#

Global CurrMapGrid% = 0
; ~ 0: Facility
; ~ 1: Forest (SCP-860-1)
; ~ 2: Maintenance Tunnels

Global PickedRoom.Rooms
Global CurrSelectedRoom.Rooms

ChangeDir("..")

LoadRoomTemplateMeshes()

FreeTextureCache()

ChangeDir("Map Creator")

Global ShowFPS% = GetINIInt(OptionFileMC, "3D Scene", "Show FPS")
Global CheckFPS%, ElapsedLoops%, FPS%
Global VSync% = GetINIInt(OptionFileMC, "3D Scene", "VSync")
Global AdjDoorPlace% = GetINIInt(OptionFileMC, "3D Scene", "Adjdoors_Place")

Global MXS# = 0.0, MYS# = 0.0

MoveMouse(GraphicsWidth() / 2, GraphicsHeight() / 2)

Global Mouse_Left_Limit% = 250, Mouse_Right_Limit% = GraphicsWidth () - 250
Global Mouse_Top_Limit% = 150, Mouse_Bottom_Limit% = GraphicsHeight () - 150

Global Faster% = False
Global Slower% = False
Global IsRemote% = True

PositionEntity(Camera, (MapWidth / 2.0) * 8.0, 1.0, MapHeight * 8.0)
RotateEntity(Camera, 0.0, 180.0, 0.0)
MXS = 180.0

Const GameUPS% = 60 ; ~ Updates per second

Global Period# = 1000.0 / GameUPS

Global PrevTime% = MilliSecs()
Global ElapsedTime#

Repeat
	Cls()
	
	If ShowFPS
		If CheckFPS < MilliSecs() Then
			FPS = ElapsedLoops
			ElapsedLoops = 0
			CheckFPS = MilliSecs() + 1000
		EndIf
		ElapsedLoops = ElapsedLoops + 1
	EndIf
	
	ElapsedTime = ElapsedTime + Float(MilliSecs() - PrevTime) / Float(Period)
	PrevTime = MilliSecs()
	
	Local f%, i%
	Local PrevAdjDoorPlace = AdjDoorPlace
	
	If FileType("CONFIG_OPTINIT.SI") = 1 Then
		f = ReadFile("CONFIG_OPTINIT.SI")
		
		CamColorR = ReadInt(f)
		CamColorG = ReadInt(f)
		CamColorB = ReadInt(f)
		CursorColorR = ReadInt(f)
		CursorColorG = ReadInt(f)
		CursorColorB = ReadInt(f)
		CamRange = ReadInt(f)
		VSync = ReadByte(f)
		ShowFPS = ReadByte(f)
		AdjDoorPlace = ReadByte(f)
		
		CamRange = Max(CamRange, 20.0)
		
		CameraClsColor(Camera, CamColorR, CamColorG, CamColorB)
		CameraRange(Camera, 0.05, CamRange * 2.0)
		
		CloseFile(f)
		DeleteFile("CONFIG_OPTINIT.SI")
	EndIf
	
	Local d.Doors
	
	If PrevAdjDoorPlace <> AdjDoorPlace Then
		If AdjDoorPlace Then
			PlaceAdjacentDoors()
		Else
			For d.Doors = Each Doors
				FreeEntity(d\FrameOBJ)
				FreeEntity(d\Buttons[0])
				FreeEntity(d\Buttons[1])
				FreeEntity(d\OBJ)
				FreeEntity(d\OBJ2)
				Delete(d)
			Next
		EndIf
	EndIf
	
	Local x%, y%
	Local r.Rooms, rt.RoomTemplates
	Local Name$, Angle%
	Local eName$, eProb#
	
	If FileType("CONFIG_MAPINIT.SI") = 1 Then
		ClearTextureCache()
		For r.Rooms = Each Rooms
			FreeEntity(r\OBJ)
			FreeTexture(r\OverlayTex)
			Delete(r)
		Next
		For d.Doors = Each Doors
			FreeEntity(d\FrameOBJ)
			FreeEntity(d\Buttons[0])
			FreeEntity(d\Buttons[1])
			FreeEntity(d\OBJ)
			FreeEntity(d\OBJ2)
			Delete(d)
		Next
		For x = 0 To MapWidth
			For y = 0 To MapHeight
				MapTemp(x, y) = 0
			Next
		Next
		
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
							r = PlaceRoom(Name, MapWidth - x, y, GetZone(y), rt\Shape, eName, eProb)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90 And r\Angle <> 270 Then
								r\Angle = r\Angle + 180
							EndIf
							r\Angle = WrapAngle(r\Angle)
							
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							
							MapTemp(MapWidth - x, y) = 1
							
							Exit
						EndIf
					Next
					
					Local IsSelRoom% = ReadByte(f)
					
					If IsSelRoom Then
						PositionEntity(Camera, (MapWidth - x) * 8.0, 1.0, y * 8.0)
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
					
					Angle = ReadByte(f) * 90.0
					
					For rt.RoomTemplates = Each RoomTemplates
						If Lower(rt\Name) = Name Then
							r = PlaceRoom(Name, ForestGridSize - x, y, GetZone(y), rt\Shape, "", 0.0, 1)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90.0 And r\Angle <> 270.0 Then
								r\Angle = r\Angle + 180.0
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
					
					Angle = ReadByte(f) * 90.0
					
					For rt.RoomTemplates = Each RoomTemplates
						If Lower(rt\Name) = Name Then
							r = PlaceRoom(Name, MT_GridSize - x, y, GetZone(y), rt\Shape, "", 0.0, 2)
							r\GridX = x
							r\GridZ = y
							
							r\Angle = Angle
							If r\Angle <> 90.0 And r\Angle <> 270.0 Then
								r\Angle = r\Angle + 180.0
							EndIf
							If rt\Shape = ROOM2C Lor rt\Shape = ROOM3 Then
								r\Angle = r\Angle - 90.0
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
		
		FreeTextureCache()
		
		CloseFile(f)
		
		If AdjDoorPlace And CurrMapGrid = 0 Then
			PlaceAdjacentDoors()
		EndIf
		
		DeleteFile("CONFIG_MAPINIT.SI")
	EndIf
	
	While ElapsedTime > 0.0
		MouseHit1 = MouseHit(1)
		
		If MouseHit(2) Then
			IsRemote = (Not IsRemote)
			MoveMouse(GraphicsWidth() / 2, GraphicsHeight() / 2)
		EndIf
		
		For r = Each Rooms
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
				If EntityDistance(Camera, r\OBJ) > CamRange Lor (Not EntityInView(GetChild(r\OBJ, 2), Camera))
					HideEntity(r\OBJ)
				Else
					ShowEntity(r\OBJ)
				EndIf
			Else
				If EntityDistance(Camera, r\OBJ) > CamRange Lor (Not EntityInView(r\OBJ, Camera))
					HideEntity(r\OBJ)
				Else
					ShowEntity(r\OBJ)
				EndIf
			EndIf
		Next
		
		For d.Doors = Each Doors
			If EntityDistance(Camera, d\FrameOBJ) > CamRange Lor (Not EntityInView(d\FrameOBJ, Camera))
				HideEntity(d\FrameOBJ)
				HideEntity(d\OBJ)
				HideEntity(d\OBJ2)
				HideEntity(d\Buttons[0])
				HideEntity(d\Buttons[1])
			Else
				ShowEntity(d\FrameOBJ)
				ShowEntity(d\OBJ)
				ShowEntity(d\OBJ2)
				ShowEntity(d\Buttons[0])
				ShowEntity(d\Buttons[1])
			EndIf
		Next
		
		If (Not IsRemote) Then
			HidePointer()
			
			If (MouseX() > Mouse_Right_Limit) Lor (MouseX() < Mouse_Left_Limit) Lor (MouseY() > Mouse_Bottom_Limit) Lor (MouseY() < Mouse_Top_Limit)
				MoveMouse(GraphicsWidth() / 2, GraphicsHeight() / 2)
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
			
			Local Picker% = EntityPick(Camera, CamRange / (2.5 - (CurrMapGrid = 1)))
			
			If Picker <> 0 Then
				For r = Each Rooms
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
		SetFont(Font1)
		If ShowFPS Then
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
			Rect(2, 32, StringWidth("Room name: " + rName), StringHeight("Room name: " + rName))
			Rect(2, 52, StringWidth("Room X: " + rX), StringHeight("Room X: " + rX))
			Rect(2, 72, StringWidth("Room Z: " + rZ), StringHeight("Room Z: " + rZ))
			
			Color(255, 255, 255)
			Text(2, 32, "Room name: " + rName)
			Text(2, 52, "Room X: " + rX)
			Text(2, 72, "Room Z: " + rZ)
			
			If PickedRoom\Event <> "" Then
				eName$ = PickedRoom\Event
				
				Local eChance# = PickedRoom\EventChance
				
				Color(0, 0, 0)
				Rect(2, 92, StringWidth("Room event: " + eName), StringHeight("Room event: " + eName))
				Rect(2, 112, StringWidth("Room event chance: " + Int(eChance * 100) + "%"), StringHeight("Room event chance: " + Int(eChance * 100) + "%"))
				
				Color(255, 255, 255)
				Text(2, 92, "Room event: " + eName)
				Text(2, 112, "Room event chance: " + Int(eChance * 100) + "%")
			EndIf
		EndIf
		
		If CurrSelectedRoom <> Null Then
			rName = CurrSelectedRoom\RoomTemplate\Name
			
			Color(0, 0, 0)
			Rect(ResWidth - 2) - StringWidth("Selected room: " + rName), 2, StringWidth("Selected room: " + rName), StringHeight("Selected room: " + rName)
			
			Color(255, 255, 255)
			Text((ResWidth - 2) - StringWidth("Selected room: " + rName), 2, "Selected room: " + rName)
		EndIf
		
		Color(CursorColorR, CursorColorG, CursorColorB)
		Rect((ResWidth / 2) - 25, (ResHeight / 2) - 2.5, 20, 5, True)
		Rect((ResWidth / 2) + 5, (ResHeight / 2) - 2.5, 20, 5, True)
		Rect((ResWidth / 2) - 2.5, (ResHeight / 2) - 25, 5, 20, True)
		Rect((ResWidth / 2) - 2.5, (ResHeight / 2) + 5, 5, 20, True)
	EndIf
	
	If VSync Then
		Flip(1)
	Else
		Flip(0)
	EndIf
Until api_FindWindow("BlitzMax_Window_Class", "SCP-CB Ultimate Edition Map Creator") = 0
End

Type RoomTemplates
	Field OBJ%, ID%
	Field OBJPath$
	Field Zone%[5]
	Field Shape%, Name$
	Field Commonness%, Large%
	Field DisableDecals%
End Type

Function CreateRoomTemplate.RoomTemplates(MeshPath$)
	Local rt.RoomTemplates = New RoomTemplates
	
	rt\OBJPath = MeshPath
	
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates(File$)
	Local TemporaryString$, i%
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			StrTemp = GetINIString(File, TemporaryString, "Mesh Path")
			
			rt = CreateRoomTemplate(StrTemp)
			rt\Name = Lower(TemporaryString)
			
			StrTemp = Lower(GetINIString(File, TemporaryString, "Shape"))
			
			Select StrTemp
				Case "room1", "1"
					;[Block]
					rt\Shape = ROOM1
					;[End Block]
				Case "room2", "2"
					;[Block]
					rt\Shape = ROOM2
					;[End Block]
				Case "room2c", "2c"
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
				rt\Zone[i] = GetINIInt(File, TemporaryString, "Zone" + (i + 1))
			Next
			
			rt\Commonness = Max(Min(GetINIInt(File, TemporaryString, "Commonness"), 100.0), 0.0)
			rt\Large = GetINIInt(File, TemporaryString, "Large")
			rt\DisableDecals = GetINIInt(File, TemporaryString, "Disabledecals")
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function LoadRoomTemplateMeshes()
	Local rt.RoomTemplates, i%
	Local MT_Prefix$ = "maintenance tunnel "
	
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt1.rmesh"
	rt\Name = MT_Prefix + "endroom"
	rt\Shape = ROOM1
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt2.rmesh"
	rt\Name = MT_Prefix + "corridor"
	rt\Shape = ROOM2
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt2c.rmesh"
	rt\Name = MT_Prefix + "corner"
	rt\Shape = ROOM2C
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt3.rmesh"
	rt\Name = MT_Prefix + "t-shaped room"
	rt\Shape = ROOM3
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt4.rmesh"
	rt\Name = MT_Prefix + "4-way room"
	rt\Shape = ROOM4
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt_elevator.rmesh"
	rt\Name = MT_Prefix + "elevator"
	rt\Shape = ROOM2
	rt = New RoomTemplates
	rt\OBJPath = "GFX\map\mt_generator.rmesh"
	rt\Name = MT_Prefix + "generator room"
	rt\Shape = ROOM1
	
	For rt = Each RoomTemplates
		If rt\OBJPath <> "" Then
			LoadRoomMesh(rt)
		EndIf
	Next
	
	Local hMap%[ROOM4], Mask%[ROOM4]
	Local GroundTexture% = LoadTexture("GFX\map\forest\forestfloor.jpg")
	Local PathTexture% = LoadTexture("GFX\map\forest\forestpath.jpg")
	
	hMap[ROOM1] = LoadImage("GFX\map\forest\forest1h.png")
	Mask[ROOM1] = LoadTexture("GFX\map\forest\forest1h_mask.png", 1 + 2)
	hMap[ROOM2] = LoadImage("GFX\map\forest\forest2h.png")
	Mask[ROOM2] = LoadTexture("GFX\map\forest\forest2h_mask.png", 1 + 2)
	hMap[ROOM2C] = LoadImage("GFX\map\forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture("GFX\map\forest\forest2Ch_mask.png", 1 + 2)
	hMap[ROOM3] = LoadImage("GFX\map\forest\forest3h.png")
	Mask[ROOM3] = LoadTexture("GFX\map\forest\forest3h_mask.png", 1 + 2)
	hMap[ROOM4] = LoadImage("GFX\map\forest\forest4h.png")
	Mask[ROOM4] = LoadTexture("GFX\map\forest\forest4h_mask.png", 1 + 2)
	
	Local FR_Prefix$ = "scp-860-1 "
	
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2], 0.03, GroundTexture, PathTexture, Mask[ROOM2])
	rt\Name = FR_Prefix + "door"
	rt\Shape = ROOM2
	ForestMeshWidth = MeshWidth(rt\OBJ)
	HideEntity(rt\OBJ)
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM1], 0.03, GroundTexture, PathTexture, Mask[ROOM1])
	rt\Name = FR_Prefix + "endroom"
	rt\Shape = ROOM1
	HideEntity(rt\OBJ)
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2], 0.03, GroundTexture, PathTexture, Mask[ROOM2])
	rt\Name = FR_Prefix + "path"
	rt\Shape = ROOM2
	HideEntity(rt\OBJ)
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM2C], 0.03, GroundTexture, PathTexture, Mask[ROOM2C])
	rt\Name = FR_Prefix + "corner"
	rt\Shape = ROOM2C
	HideEntity(rt\OBJ)
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM3], 0.03, GroundTexture, PathTexture, Mask[ROOM3])
	rt\Name = FR_Prefix + "t-shaped path"
	rt\Shape = ROOM3
	HideEntity(rt\OBJ)
	rt = New RoomTemplates
	rt\OBJ = LoadTerrain(hMap[ROOM4], 0.03, GroundTexture, PathTexture, Mask[ROOM4])
	rt\Name = FR_Prefix + "4-way path"
	rt\Shape = ROOM4
	HideEntity(rt\OBJ)
	
	FreeTexture(GroundTexture)
	FreeTexture(PathTexture)
	For i = ROOM1 To ROOM4
		FreeImage(hMap[i])
		FreeTexture(Mask[i])
	Next
End Function

Function PlaceRoom.Rooms(Name$, x%, z%, Zone%, Shape%, Event$ = "", EventChance# = 1.0, MapGrid% = 0)
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
	
	For rt = Each RoomTemplates
		If rt\Name = Name
			r = CreateRoom(Zone, Shape, x * Spacing, 0.0, z * Spacing, Name)
			Exit
		EndIf
	Next
	
	r\Event = Event
	If r\Event <> "" Then r\EventChance = EventChance
	
	Return(r)
End Function

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#, Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#, Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

Function GetMeshExtents2(Mesh%)
	Local xMax# = -1000000
	Local xMin# = 1000000
	Local yMax# = -1000000
	Local yMin# = 1000000
	Local zMax# = -1000000
	Local zMin# = 1000000
	Local su%, s%, i%, x#, y#, z#
	
	For su = 1 To CountSurfaces(Mesh)
		s = GetSurface(Mesh, su)
		For i = 0 To CountVertices(s) - 1
			x = VertexX(s, i)
			y = VertexY(s, i)
			z = VertexZ(s, i)
			TFormPoint(x, y, z, Mesh, 0)
			x = TFormedX()
			y = TFormedY()
			z = TFormedZ()
			If x > xMax Then xMax = x
			If x < xMin Then xMin = x
			If y > yMax Then yMax = y
			If y < yMin Then yMin = y
			If z > zMax Then zMax = z
			If z < zMin Then zMin = z
		Next
	Next
	
	Mesh_MinX = xMin
	Mesh_MinY = yMin
	Mesh_MinZ = zMin
	Mesh_MaxX = xMax
	Mesh_MaxY = yMax
	Mesh_MaxZ = zMax
	Mesh_MagX = xMax - xMin
	Mesh_MagY = yMax - yMin
	Mesh_MagZ = zMax - zMin
End Function

Function SetRoom(Room_Name$, Room_Type%, Pos%, Min_Pos%, Max_Pos%) ; ~ Place a room without overwriting others
	Local Looped%, Can_Place%
	
	Looped = False
	Can_Place = True
	While MapRoom(Room_Type, Pos) <> ""
		Pos = Pos + 1
		If Pos > Max_Pos Then
			If Looped = False Then
				Pos = Min_Pos + 1 : Looped = True
			Else
				Can_Place = False
				Exit
			EndIf
		EndIf
	Wend
	
	If Can_Place = True Then
		MapRoom(Room_Type, Pos) = Room_Name
		Return(True)
	Else
		Return(False)
	EndIf
End Function

Function GetZone(y%)
	Local ZoneAmount% = 3
	
	Return(Min(Floor((Float(MapWidth - y) / MapWidth * ZoneAmount)), ZoneAmount - 1))
End Function

Type Props
	Field File$
	Field OBJ%
End Type

Function CreatePropObj(File$)
	Local p.Props
	
	For p.Props = Each Props
		If p\File = File Then
			Return(CopyEntity(p\OBJ))
		EndIf
	Next
	
	p.Props = New Props
	p\File = File
	p\OBJ = LoadMesh(File)
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
	Field Adjacent.Rooms[4]
	Field AdjDoor.Doors[4]
	Field GridX%, GridZ%
	Field ForestWallOBJ%
End Type 

Function CreateRoom.Rooms(Zone%, RoomShape%, x#, y#, z#, Name$ = "")
	Local r.Rooms = New Rooms
	Local rt.RoomTemplates
	Local Tempf1#, Tempf2#, Tempf3#
	
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
					r\ForestWallOBJ = LoadRMesh("..\GFX\map\forest\wall_opt.rmesh", Null)
					ScaleEntity(r\ForestWallOBJ, RoomScale, RoomScale, RoomScale)
					PositionEntity(r\ForestWallOBJ, x, y, z, True)
					EntityParent(r\ForestWallOBJ, r\OBJ)
					RotateEntity(r\ForestWallOBJ, 0.0, 180.0, 0.0)
					MoveEntity(r\ForestWallOBJ, 0.0, 0.0, -(14.0 + Tempf1))
				EndIf
				
				If CurrMapGrid <> 1 Then
					r\OverlayTex = CreateTexture(1, 1)
					SetBuffer(TextureBuffer(r\OverlayTex))
					ClsColor(0, 0, 0)
					Cls()
					SetBuffer(BackBuffer())
					EntityTexture(GetChild(r\OBJ, 2), r\OverlayTex, 0, 0)
				Else
					r\OverlayTex = CreateTexture(1, 1)
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
End Function

Function CreateOverLapBox(r.Rooms)
	Local s%
	Local SizeAdd# = 0.02
	
	If r\RoomTemplate\Name = "gateb" Then Return
	If r\RoomTemplate\Name = "gatea" Then Return
	If r\RoomTemplate\Name = "room049" Then Return
	If r\RoomTemplate\Name = "room008" Then Return
	If r\RoomTemplate\Name = "room3storage" Then Return
	If r\RoomTemplate\Name = "room966" Then Return
	If r\RoomTemplate\Name = "room106" Then Return
	If r\RoomTemplate\Name = "room079" Then Return
	If r\RoomTemplate\Name = "gateaentrance" Then Return
	If r\RoomTemplate\Name = "room1office" Then Return
	
	r\OverlapCheckBox = CreateMesh()
	GetMeshExtents2(GetChild(r\OBJ, 2))
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

Function LoadRoomMesh(rt.RoomTemplates)
	If Instr(rt\OBJPath, ".rmesh") <> 0 Then ; ~ File is RoomMesh
		rt\OBJ = LoadRMesh(rt\OBJPath, rt)
	EndIf
	
	If (Not rt\OBJ) Then RuntimeError("Failed To load map file " + Chr(34) + rt\OBJPath + Chr(34) + ".")
	
	HideEntity(rt\OBJ)
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

Type Materials
	Field Name$
	Field Diff%
	Field Bump%
	Field StepSound%
End Type

Function LoadMaterials(File$)
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
			
			mat\StepSound = (GetINIInt(File, TemporaryString, "stepsound") + 1)
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function ApplyBumpMap(Texture%)
	TextureBlend(Texture, 6)
	TextureBumpEnvMat(Texture, 0, 0, -0.012)
	TextureBumpEnvMat(Texture, 0, 1, -0.012)
	TextureBumpEnvMat(Texture, 1, 0, 0.012)
	TextureBumpEnvMat(Texture, 1, 1, 0.012)
	TextureBumpEnvOffset(Texture, 0.5)
	TextureBumpEnvScale(Texture, 1.0)
End Function

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
			Local Temp$ = GetINIString("Data\materials.ini", tc\Name, "bump")
			
			If Temp <> "" Then
				tc\Bump = LoadTexture(Temp)
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
		If tc\Diff <> 0 Then FreeTexture(tc\Diff)
		If tc\Bump <> 0 Then FreeTexture(tc\Bump)
		tc\Diff = 0 : tc\Bump = 0
	Next
End Function

Function LoadRMesh(File$, rt.RoomTemplates)
	; ~ Generate a texture made of white
	Local BlankTexture%
	
	BlankTexture = CreateTexture(4, 4, 1, 1)
	ClsColor(255, 255, 255)
	SetBuffer(TextureBuffer(BlankTexture))
	Cls()
	SetBuffer(BackBuffer())
	
	Local PinkTexture%
	
	PinkTexture = CreateTexture(4, 4, 1, 1)
	ClsColor(255, 255, 255)
	SetBuffer(TextureBuffer(PinkTexture))
	Cls()
	SetBuffer(BackBuffer())
	
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
	If f = 0 Then RuntimeError("Error reading file " + Chr(34) + File + Chr(34))
	
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
		HasTriggerBox = True
	Else
		RuntimeError(Chr(34) + File + Chr(34) + " is Not RMESH (" + IsRMesh + ")")
	EndIf
	
	File = StripFilename(File)
	
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
				Tex[j] = GetTextureFromCache(Temp1s)
				If Tex[j] = 0 Then ; ~ Texture is not in cache
					Select True
						Case Temp1i < 3
							;[Block]
							Tex[j] = LoadTexture(File + Temp1s, 1)
							;[End Block]
						Default
							;[Block]
							Tex[j] = LoadTexture(File + Temp1s, 3)
							;[End Block]
					End Select
					
					If Tex[j] <> 0 Then
						If Temp1i = 1 Then TextureBlend(Tex[j], 5)
						If Instr(Lower(Temp1s), "_lm") <> 0 Then
							TextureBlend(Tex[j], 3)
						EndIf
						AddTextureToCache(Tex[j])
					EndIf
				EndIf
				If Tex[j] <> 0 Then
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
				BrushTexture(Brush, BlankTexture, 0, 1)
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
						BrushTexture(Brush, BlankTexture, 0, j + 1)
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
		
		Count2 = ReadInt(f) ; ~ Polygons
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
		Next
		
		If IsAlpha = 1 Then
			AddMesh(ChildMesh, Alpha)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
		Else
			AddMesh(ChildMesh,Opaque)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
			
			; ~ Make collision double-sided
			Local FlipChild% = CopyMesh(ChildMesh)
			
			FlipMesh(FlipChild)
			AddMesh(FlipChild, ChildMesh)
			FreeEntity(FlipChild)	
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
		
		Count2 = ReadInt(f) ; ~ Polygons
		For j = 1 To Count2
			Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
			AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
			AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
		Next
	Next
	
	; ~ Trigger boxes
	If HasTriggerBox Then
		Local Numb%, tb%
		
		Numb = ReadInt(f)
		For tb = 0 To Numb-1
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
				If File <> "" Then
					Local Model% = CreatePropObj("GFX\Map\Props\" + File)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					PositionEntity(Model, Temp1, Temp2, Temp3)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					RotateEntity(Model, Temp1, Temp2, Temp3)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					ScaleEntity(Model, Temp1, Temp2, Temp3)
					
					EntityParent(Model, Opaque)
					EntityType(Model, HIT_MAP)
				Else
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				EndIf
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
	
	FreeTexture(BlankTexture)
	
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
	If p < 1 Then
		a = s
	Else
		a = Left(s, p - 1)
	EndIf
	Return(a)
End Function

Function KeyValue$(Entity%, Key$, DefaultValue$ = "")
	Local Properties$, p%, TestKey$, Value$, Test$
	
	Properties = EntityName(Entity)
	Properties = Replace(Properties$, Chr(13), "")
	Key = Lower(Key)
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

Function GetINIString$(File$, Section$, Parameter$)
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
	; ~ Returns: True (Success) or False (Failed)
	INI_sSection = "[" + Trim(INI_sSection) + "]"
	INI_sUpperSection$ = Upper(INI_sSection)
	INI_sKey = Trim(INI_sKey)
	INI_sValue = Trim(INI_sValue)
	INI_sFilename$ = CurrentDir() + "\" + INI_sAppName
	
	; ~ Retrieve the INI data (if it exists)
	
	INI_sContents$ = INI_FileToString(INI_sFilename)
	
	; ~ (Re)Create the INI file updating / adding the SECTION, KEY and VALUE
	
	INI_bWrittenKey% = False
	INI_bSectionFound% = False
	INI_sCurrentSection$ = ""
	
	INI_lFileHandle = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return(False) ; ~ Create file failed!
	
	INI_lOldPos% = 1
	INI_lPos% = Instr(INI_sContents, Chr(0))
	
	While INI_lPos <> 0
		INI_sTemp$ = Trim(Mid(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos)))
		
		If INI_sTemp <> "" Then
			If Left(INI_sTemp, 1) = "[" And Right(INI_sTemp, 1) = "]" Then
				; ~ Process SECTION
				If INI_sCurrentSection = INI_sUpperSection And INI_bWrittenKey = False Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If INI_sCurrentSection = INI_sUpperSection Then INI_bSectionFound = True
			Else
				lEqualsPos% = Instr(INI_sTemp, "=")
				If lEqualsPos <> 0 Then
					If (INI_sCurrentSection = INI_sUpperSection) And (Upper(Trim(Left(INI_sTemp, (lEqualsPos - 1)))) = Upper(INI_sKey)) Then
						If (INI_sValue <> "") Then INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
						INI_bWrittenKey = True
					Else
						WriteLine(INI_lFileHandle, INI_sTemp)
					EndIf
				EndIf
			EndIf
		EndIf
		
		; ~ Move through the INI file...
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr(0), INI_lOldPos)
	Wend
	
	; ~ KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY = VALUE line
	If INI_bWrittenKey = False Then
		If INI_bSectionFound = False Then INI_CreateSection(INI_lFileHandle, INI_sSection)
		INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
	EndIf
	
	CloseFile(INI_lFileHandle)
	
	Return(True) ; ~ Success
End Function

Function INI_FileToString$(INI_sFilename$)
	INI_sString$ = ""
	INI_lFileHandle% = ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While (Not(Eof(INI_lFileHandle)))
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

Function Button%(x%, y%, Width%, Height%, Txt$, Disabled% = False)
	Local Pushed% = False
	
	Color(ClrR, ClrG, ClrB)
	If (Not Disabled) Then 
		If MouseX() > x * ResFactor And MouseX() < (x + Width) * ResFactor Then
			If MouseY() > y * ResFactor And MouseY() < (y + Height) * ResFactor Then
				If MouseDown1 Then
					Pushed = True
					Color(ClrR * 0.6, ClrG * 0.6, ClrB * 0.6)
				Else
					Color(Min(ClrR * 1.2, 255.0), Min(ClrR * 1.2, 255.0), Min(ClrR * 1.2, 255.0))
				EndIf
			EndIf
		EndIf
	EndIf
	
	If Pushed Then 
		Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor)
		Color(133, 130, 125)
		Rect((x + 1) * ResFactor, (y + 1) * ResFactor, (Width - 1) * ResFactor, (Height - 1) * ResFactor, False)	
		Color(10, 10, 10)
		Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor, False)
		Color(250, 250, 250)
		Line(x * ResFactor, (y + Height - 1) * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor)
		Line((x + Width - 1) * ResFactor, y * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor)
	Else
		Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor)
		Color(133, 130, 125)
		Rect(x * ResFactor, y * ResFactor, (Width - 1) * ResFactor, (Height - 1) * ResFactor, False)	
		Color(250, 250, 250)
		Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor, False)
		Color(10, 10, 10)
		Line(x * ResFactor, (y + Height - 1) * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor)
		Line((x + Width - 1) * ResFactor, y * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor	)	
	EndIf
	
	Color(255, 255, 255)
	If Disabled Then Color(70, 70, 70)
	SetFont(Font1)
	Text((x + Width / 2) * ResFactor, (y + Height / 2 - 1) * ResFactor, Txt, True, True)
	
	Color(0, 0, 0)
	
	If Pushed And MouseHit1 Then PlaySound(ButtonSFX) : Return(True)
End Function

Function InputBox$(x%, y%, Width%, Height%, Txt$, ID% = 0)
	TextBox(x, y, Width, Height, Txt$)
	
	Local MouseOnBox% = False
	
	If MouseX() > x * ResFactor And MouseX() < (x + Width) * ResFactor Then
		If MouseY() > y * ResFactor And MouseY() < (y + Height) * ResFactor Then
			MouseOnBox = True
			If MouseHit1 Then SelectedTextBox = ID : FlushKeys()
		EndIf
	EndIf	
	
	If MouseOnBox = False And MouseHit1 And SelectedTextBox = ID Then SelectedTextBox = 0
	
	If SelectedTextBox = ID Then
		Txt = rInput(Txt)
		Color(0, 0, 0)
		If (MilliSecs() Mod 800) < 400 Then  Rect(((x + Width / 2 + StringWidth(Txt) / 2 + 2)) * ResFactor, (y + Height / 2 - 5) * ResFactor, 2 * ResFactor, 12 * ResFactor)
	EndIf
	
	Return(Txt)
End Function

Function TextBox(x%, y%, Width%, Height%, Txt$)
	Color(255, 255, 255)
	Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor)
	
	Color(128, 128, 128)
	Rect(x * ResFactor, y * ResFactor, Width * ResFactor, Height * ResFactor, False)
	Color(64, 64, 64)
	Rect((x + 1) * ResFactor, (y + 1) * ResFactor, (Width - 2) * ResFactor, (Height - 2) * ResFactor, False)	
	Color(255, 255, 255)
	Line((x + Width - 1) * ResFactor, y * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor)
	Line(x * ResFactor, (y + Height - 1) * ResFactor, (x + Width - 1) * ResFactor, (y + Height - 1) * ResFactor)
	Color(212, 208, 199)
	Line((x + Width - 2) * ResFactor, (y + 1) * ResFactor, (x + Width - 2) * ResFactor, (y + Height - 2) * ResFactor)
	Line((x + 1) * ResFactor, (y + Height - 2) * ResFactor, (x + Width - 2) * ResFactor, (y + Height - 2) * ResFactor)
	
	Color(0, 0, 0)
	SetFont(Font1)
	Text((x + Width / 2) * ResFactor, (y + Height / 2) * ResFactor, Txt, True, True)
End Function

Function rInput$(aString$)
	Local Value%, Length%
	
	Value = GetKey()
	Length = Len(aString)
	If Value = 8 Then Value = 0 : If Length > 0 Then aString = Left(aString, Length - 1)
	If Value = 13 Then Goto ende
	If Value = 0 Then Goto ende
	If Value > 0 And Value < 7 Lor Value > 26 And Value < 32 Lor Value = 9 Then Goto ende
	aString = aString + Chr(Value)
	.ende
	Return(aString)
End Function

Function WrapAngle#(Angle#)
	While Angle < 0.0
		Angle = Angle + 360.0
	Wend 
	While Angle >= 360.0
		Angle = Angle - 360.0
	Wend
	Return(Angle)
End Function

Type Doors
	Field OBJ%, OBJ2%, FrameOBJ%, Buttons%[2]
	Field Dir%
	Field Angle%
End Type

Function CreateDoor.Doors(x#, y#, z#, Angle#, room.Rooms, Big% = False)
	Local d.Doors, i%
	Local Parent%
	
	If room <> Null Then Parent = room\OBJ
	
	d.Doors = New Doors
	If Big = 2 Then 
		d\OBJ = CopyEntity(Door_HCZ_1)
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(Door_HCZ_2)
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(Door_Frame)
	Else
		d\OBJ = CopyEntity(Door_LCZ)
		ScaleEntity(d\OBJ, (204.0 * RoomScale) / MeshWidth(d\OBJ), 313.0 * RoomScale / MeshHeight(d\OBJ), 16.0 * RoomScale / MeshDepth(d\OBJ))
		
		d\FrameOBJ = CopyEntity(Door_Frame)
		d\OBJ2 = CopyEntity(Door_LCZ)
		
		ScaleEntity(d\OBJ2, (204.0 * RoomScale) / MeshWidth(d\OBJ2), 313.0 * RoomScale / MeshHeight(d\OBJ2), 16.0 * RoomScale / MeshDepth(d\OBJ2))
	EndIf
	
	PositionEntity(d\FrameOBJ, x, y, z)
	ScaleEntity(d\FrameOBJ, RoomScale, RoomScale, RoomScale)
	
	For i = 0 To 1
		d\Buttons[i] = CopyEntity(Door_Button)
		ScaleEntity(d\Buttons[i], 0.03, 0.03, 0.03)
		
		PositionEntity(d\Buttons[i], x + 0.6 + (i * (-1.2)), y + 0.7, z - 0.1 + (i * 0.2))
		RotateEntity(d\Buttons[i], 0.0, (i * 180.0), 0.0)
		EntityParent(d\Buttons[i], d\FrameOBJ)
	Next
	
	PositionEntity(d\OBJ, x, y, z)
	
	RotateEntity(d\OBJ, 0.0, Angle, 0.0)
	RotateEntity(d\FrameOBJ, 0.0, Angle, 0.0)
	
	If d\OBJ2 <> 0 Then
		PositionEntity(d\OBJ2, x, y, z)
		RotateEntity(d\OBJ2, 0.0, Angle + 180.0, 0.0)
	EndIf
	
	d\Angle = Angle
	
	d\Dir = Big
	
	Return(d)
End Function

Function PlaceAdjacentDoors()
	Local Temp% = 0, Zone%
	Local Spacing# = 8.0
	Local ShouldSpawnDoor% = False
	Local x%, y%
	Local r.Rooms, d.Doors
	
	For y = MapHeight To 0 Step -1
		If y < ZoneTransValue2 Then
			Zone = 3
		ElseIf y >= ZoneTransValue2 And y < ZoneTransValue1 Then
			Zone = 2
		Else
			Zone = 1
		EndIf
		
		For x = MapWidth To 0 Step -1
			If MapTemp(x, y) > 0 Then
				If Zone = 2 Then Temp = 2 Else Temp = 0
				
				For r.Rooms = Each Rooms
					If Int(r\x / 8.0) = x And Int(r\z / 8.0) = y Then
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 90.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 90.0 Lor r\Angle = 270.0 Then 
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 90.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 180.0 Lor r\Angle = 90.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor Then
							If x + 1 < MapWidth + 1
								If MapTemp(x + 1, y) > 0 Then
									d.Doors = CreateDoor(Float(x) * Spacing + Spacing / 2.0, 0.0, Float(y) * Spacing, 90.0, r, Temp)
									r\AdjDoor[0] = d
								EndIf
							EndIf
						EndIf
						
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 180.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 180.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 180.0 Lor r\Angle = 90.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 180.0 Lor r\Angle = 90.0 Lor r\Angle = 270.0 Then
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						
						If ShouldSpawnDoor Then
							If y + 1 < MapHeight + 1 Then
								If MapTemp(x, y + 1) > 0 Then
									d.Doors = CreateDoor(Float(x) * Spacing, 0.0, Float(y) * Spacing + Spacing / 2.0, 0.0, r, Temp)
									r\AdjDoor[3] = d
								EndIf
							EndIf
						EndIf
						Exit
					EndIf
				Next
			End If
		Next
	Next
	
	For d.Doors = Each Doors
		EntityParent(d\OBJ, 0)
		If d\OBJ2 <> 0 Then EntityParent(d\OBJ2, 0)
		If d\FrameOBJ <> 0 Then EntityParent(d\FrameOBJ, 0)
		If d\Buttons[0] <> 0 Then EntityParent(d\Buttons[0], 0)
		If d\Buttons[1] <> 0 Then EntityParent(d\Buttons[1], 0)
		
		If d\OBJ2 <> 0 And d\Dir = 0 Then
			MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
			MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
		EndIf	
	Next
End Function

Function LoadTerrain(hMap%, yScale# = 0.7, t1%, t2%, Mask%)
	; ~ Load the heightmap
	If hMap = 0 Then RuntimeError("Heightmap image " + hMap + " does not exist.")
	
	; ~ Store heightmap dimensions
	Local x% = ImageWidth(hMap) - 1, y% = ImageHeight(hMap) - 1
	Local lX%, lY%, Index%
	
	; ~ Load texture and lightmaps
	If t1 = 0 Then RuntimeError("LoadTerrain error: invalid texture 1")
	If t2 = 0 Then RuntimeError("LoadTerrain error: invalid texture 2")
	If Mask = 0 Then RuntimeError("LoadTerrain error: invalid texture mask")
	
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
	LockBuffer(ImageBuffer(hMap))
	LockBuffer(TextureBuffer(Mask))
	
	For lX = 0 To x
		For lY = 0 To y
			; ~ Ising vertex alpha and two meshes instead of FE_ALPHAWHATEVER
			; ~ It doesn't look perfect but it does the job
			; ~ You might get better results by downscaling the mask to the same size as the heightmap
			Local MaskX# = Min(lX * Float(TextureWidth(Mask)) / Float(ImageWidth(hMap)), TextureWidth(Mask) - 1.0)
			Local MaskY# = TextureHeight(Mask) - Min(lY * Float(TextureHeight(Mask)) / Float(ImageHeight(hMap)), TextureHeight(Mask) - 1.0)
			Local RGB%, RED%
			
			RGB = ReadPixelFast(Min(lX, x - 1.0), y - Min(lY, y - 1.0), ImageBuffer(hMap))
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
	UnlockBuffer(ImageBuffer(hMap))
	
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
;~C#Blitz3D