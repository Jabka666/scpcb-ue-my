Include "Source Code\INI_Core.bb"
Include "Source Code\Math_Core.bb"

Const ResWidth% = 910
Const ResHeight% = 660

Local LoadingWindow% = CreateWindow("", GraphicsWidth() / 2 - 160, GraphicsHeight() / 2 - 120, 320, 260, 0, 8)
Local PanelLoading% = CreatePanel(0, 0, 320, 260, LoadingWindow, 0)

SetPanelImage(PanelLoading, "Assets\map_logo.png")

; ~ Create a window to put the toolbar in
Local WinHandle% = CreateWindow("SCP-CB Ultimate Edition Map Creator", GraphicsWidth() / 2 - ResWidth / 2, GraphicsHeight() / 2 - ResHeight / 2, ResWidth, ResHeight, 0, 13) 

Global MainHwnd% = GetActiveWindow() ; ~ User32.dll

HideGadget(WinHandle)

Const RoomsFile$ = "..\Data\rooms.ini"

LoadRoomTemplates(RoomsFile)

Global ListBox% = CreateListBox(5, 60, ResWidth / 4, ResHeight / 2 - 20, WinHandle)

Local rt.RoomTemplates

For rt.RoomTemplates = Each RoomTemplates
	If rt\MapGrid = 0 Then
		AddGadgetItem(ListBox, rt\Name)
	EndIf
Next
SetGadgetLayout(ListBox, 3, 3, 2, 2)

Const EventsFile$ = "..\Data\events_MC.ini"

InitEvents(EventsFile)
AddEvents()

Global Room_Desc% = CreateLabel("Room description:", 5, 40 + ResHeight / 2, ResWidth / 4, ResHeight / 11.8, WinHandle, 3)

SetGadgetLayout(Room_Desc, 3, 3, 2, 2)

Global Grid_Room_Info% = CreateLabel("", 5, 200 + ResHeight / 2, ResWidth / 4, ResHeight / 11.6, WinHandle, 3)

SetGadgetLayout(Grid_Room_Info, 3, 3, 2, 2)

Global ChangeGridGadget% = False
Global GridGadgetText$ = ""

Global Event_Desc% = CreateLabel("", 5, 117 + ResHeight / 2, ResWidth / 4, ResHeight / 12.0, WinHandle, 3)

SetGadgetLayout(Event_Desc , 3, 3, 2, 2)

Global Event_Prob% = CreateSlider(6, 185 + ResHeight / 2, ResWidth / 4 - 2, ResHeight / 38.0, WinHandle, 1)

SetGadgetLayout(Event_Prob, 3, 3, 2, 2)
SetSliderRange(Event_Prob, 0, 100)
DisableGadget(Event_Prob)

Global Event_Prob_Label% = CreateLabel("", 5, 170 + ResHeight / 2, ResWidth / 4, ResHeight / 38.0, WinHandle, 3)

SetGadgetLayout(Event_Prob_Label, 3, 3, 2, 2)

Local Menu% = WindowMenu(WinHandle)

Global ComboBox% = CreateComboBox(5, 95 + ResHeight / 2, ResWidth / 4, ResHeight - ResHeight / 1.39, WinHandle)

SetGadgetLayout(ComboBox, 3, 3, 2, 2)
DisableGadget(ComboBox)

Local TxtBox% = CreateTextField(5, 40, 150, 20, WinHandle) ; ~ Create TextField in that window

SetGadgetText(TxtBox, "") ; ~ Set Text in that TextField for info

Local OK% = CreateButton("Search", 155, 40, 50, 20, WinHandle) ; ~ Create OK button
Local Clean_Txt% = CreateButton("X", 210, 40, 20, 20, WinHandle) ; ~ Create EXIT button

Global ShowGrid% = True

Local Map_2D% = CreateCanvas(300, 25, 551, 551, WinHandle)

Dim MapIcons%(5, 4)

MapIcons(ROOM1, 0) = LoadImage("Assets\room1.png")
MapIcons(ROOM2, 0) = LoadImage("Assets\room2.png")
MapIcons(ROOM2C, 0) = LoadImage("Assets\room2C.png")
MapIcons(ROOM3, 0) = LoadImage("Assets\room3.png")
MapIcons(ROOM4, 0) = LoadImage("Assets\room4.png")

Local i%, n%

For i = ROOM1 To ROOM4
	MaskImage(MapIcons(i, 0), 255, 255, 255)
	MidHandle(MapIcons(i, 0))
	For n = 1 To 3
		MapIcons(i, n) = CopyImage(MapIcons(i, 0))
		MaskImage(MapIcons(i, n), 255, 255, 255)
		RotateImage(MapIcons(i, n), 90 * n)
		MidHandle(MapIcons(i, n))
	Next
Next

Dim ForestIcons%(5, 4)

ForestIcons(ROOM1, 0) = LoadImage("Assets\forest1.png")
ForestIcons(ROOM2, 0) = LoadImage("Assets\forest2.png")
ForestIcons(ROOM2C, 0) = LoadImage("Assets\forest2C.png")
ForestIcons(ROOM3, 0) = LoadImage("Assets\forest3.png")
ForestIcons(ROOM4, 0) = LoadImage("Assets\forest4.png")
For i = ROOM1 To ROOM4
	MaskImage(ForestIcons(i, 0), 255, 255, 255)
	MidHandle(ForestIcons(i, 0))
	For n = 1 To 3
		ForestIcons(i, n) = CopyImage(ForestIcons(i, 0))
		MaskImage(ForestIcons(i, n), 255, 255, 255)
		RotateImage(ForestIcons(i, n), 90 * n)
		MidHandle(ForestIcons(i, n))
	Next
Next

Global Grid_SelectedX# = -1.0, Grid_SelectedY# = -1.0
Global CurrMapGrid% = 0

Dim SpecialIcons%(2, 4)

SpecialIcons(1, 0) = LoadImage("Assets\forest_exit.png")
SpecialIcons(2, 0) = LoadImage("Assets\room2elev.png")
For i = 1 To 2
	MaskImage(SpecialIcons(i, 0), 255, 255, 255)
	MidHandle(SpecialIcons(i, 0))
	For n = 1 To 3
		SpecialIcons(i, n) = CopyImage(SpecialIcons(i, 0))
		MaskImage(SpecialIcons(i, n), 255, 255, 255)
		RotateImage(SpecialIcons(i, n), 90 * n)
		MidHandle(SpecialIcons(i, n))
	Next
Next

Const MapSize% = 18

Dim Map.RoomTemplates(MapSize, MapSize)
Dim MapAngle%(MapSize, MapSize)
Dim MapEvent$(MapSize, MapSize)
Dim MapEventProb#(MapSize, MapSize)

Const ForestGridSize% = 9
Dim ForestPlace.RoomTemplates(ForestGridSize, ForestGridSize)
Dim ForestPlaceAngle%(ForestGridSize, ForestGridSize)

Const MT_GridSize% = 18
Dim MTRoom.RoomTemplates(MT_GridSize, MT_GridSize)
Dim MTRoomAngle%(MT_GridSize, MT_GridSize)

Global Arrows%[4]

Arrows[0] = LoadImage("Assets\arrows.png")
HandleImage(Arrows[0], ImageWidth(Arrows[0]) / 2, ImageHeight(Arrows[0]) / 2)
For i = 1 To 3
	Arrows[i] = CopyImage(Arrows[0])
	HandleImage(Arrows[i], ImageWidth(Arrows[i]) / 2, ImageHeight(Arrows[i]) / 2)
	RotateImage(Arrows[i], i * 90.0)
Next

Global PlusIcon%

PlusIcon = LoadImage("Assets\plus.png")
MaskImage(PlusIcon, 255, 255, 255)
MidHandle(PlusIcon)

SetGadgetLayout(TxtBox, 3, 3, 3, 3)
SetGadgetLayout(OK, 3, 3, 3, 3)
SetGadgetLayout(Clean_Txt, 3, 3, 3, 3)

Local Tab% = CreateTabber(0, 5, ResWidth / 4 + 20, ResHeight - 60, WinHandle)

InsertGadgetItem(Tab, 0, "2-D/Map Creator")
InsertGadgetItem(Tab, 1, "3-D/Map Viewer")
SetGadgetLayout(Tab, 3, 3, 2, 2)

Local Tab2% = CreateTabber(300, 5, ResWidth / 4 + 20, ResHeight - 100, WinHandle)

InsertGadgetItem(Tab2, 0, "Facility")
InsertGadgetItem(Tab2, 1, "Forest")
InsertGadgetItem(Tab2, 2, "Maintenance Tunnels")
SetGadgetLayout(Tab2, 3, 3, 2, 2)

SetStatusText(LoadingWindow, "Starting up")
; ~ Now create a whole bunch of menus and sub-items

Local File% = CreateMenu("File", 0, Menu) ; ~ Main menu

CreateMenu("New", 0, File) ; ~ Child menu 
CreateMenu("Open", 1, File) ; ~ Child menu 
CreateMenu("", 1000, File) ; ~ Use an empty string to generate separator bars
CreateMenu("Save", 2, File) ; ~ Child menu 
CreateMenu("Save as...", 3, File) ; ~ Child menu 
CreateMenu("", 1000, File) ; ~ Use an empty string to generate separator bars
CreateMenu("Quit", 10001, File) ; ~ Another child menu

Local Options% = CreateMenu("Options", 0, Menu)
Local Event_Default% = CreateMenu("Set the event for the rooms by default", 15, Options)

CreateMenu("", 1000, Options)

Local Zone_Trans% = CreateMenu("Map Settings", 18, Options)
Local Author_Descr% = CreateMenu("Edit Author and Description", 19, Options)

CreateMenu("", 1000, Options)
CreateMenu("Edit Camera", 17, Options)

LoadOptionsINI()

If (Not opt\Events) Then
	UncheckMenu(Event_Default)
Else
	CheckMenu(Event_Default)
EndIf

; ~ Now the Edit Menu
Local Edit% = CreateMenu("&Help", 0, Menu) ; ~ Main menu with Alt Shortcut - Use & to specify the shortcut key

CreateMenu("Manual" + Chr(8) + "F1", 6, Edit) ; ~ Another Child menu with Alt Shortcut
CreateMenu("About" + Chr(8) + "F12", 40, Edit) ; ~ Child menu with Alt Shortcut

HotKeyEvent(59, 0, $1001, 6)
HotKeyEvent(88, 0, $1001, 40)

; ~ Finally, once all menus are set up / updated, we call UpdateWindowMenu to tell the OS about the menu
UpdateWindowMenu(WinHandle)

SetStatusText(LoadingWindow, "Creating 2D scene...")

Local OptionWin% = CreateWindow("Edit Camera", GraphicsWidth() / 2 - 160, GraphicsHeight() / 2 - 120, 300, 280, WinHandle, 1)

HideGadget(OptionWin)

Local LabelColor% = CreateLabel("", 5, 5, 285, 60, OptionWin, 1)
Local LabelColor2% = CreateLabel("", 5, 70, 285, 60, OptionWin, 1)
Local LabelRange% = CreateLabel("", 5, 135, 285, 60, OptionWin, 1)
Local Color_Button% = CreateButton("Change CameraFog Color", 25, 20, 150, 30, OptionWin)
Local Color_Button2% = CreateButton("Change Cursor Color", 25, 85, 150, 30, OptionWin)
Local LabelFogR% = CreateLabel("R " + opt\FogR, 225, 15, 40, 15, OptionWin)
Local LabelFogG% = CreateLabel("G " + opt\FogG, 225, 30, 40, 15, OptionWin)
Local LabelFogB% = CreateLabel("B " + opt\FogB, 225, 45, 40, 15, OptionWin)
Local LabelCursorR% = CreateLabel("R " + opt\CursorR, 225, 75, 40, 15, OptionWin)
Local LabelCursorG% = CreateLabel("G " + opt\CursorG, 225, 90, 40, 15, OptionWin)
Local LabelCursorB% = CreateLabel("B " + opt\CursorG, 225, 105, 40, 15, OptionWin)

LabelRange = CreateLabel("Culling Range", 10, 170, 80, 20, OptionWin)

Global CameraRangeOpt% = CreateTextField(25, 150, 40, 20, OptionWin)

SetGadgetText(CameraRangeOpt, opt\CamRange)

Global VSync% = CreateButton("Vsync", 123, 145, 50, 30, OptionWin, 2)

SetButtonState(VSync, opt\VSync)

Global ShowFPS% = CreateButton("Show FPS", 210, 145, 70, 30, OptionWin, 2)

SetButtonState(ShowFPS, opt\ShowFPS)

Local CancelOpt_Button% = CreateButton("Cancel", 10, 210, 100,30, OptionWin)
Local SaveOpt_Button% = CreateButton("Save", 185, 210, 100, 30, OptionWin)

Local Map_Settings% = CreateWindow("Map Settings", GraphicsWidth() / 2 - 120, GraphicsHeight() / 2 - 80, 240, 160, WinHandle, 1)

HideGadget(Map_Settings)

Local ZoneText% = CreateLabel("Zone transition settings:", 10, 10, 200, 20, Map_Settings)
Local LabelZoneTrans1% = CreateLabel("LCZ to HCZ transition", 10, 60, 120, 20, Map_Settings)

Global ZoneTrans1% = CreateTextField(20, 40, 80, 20, Map_Settings)

SetGadgetText(ZoneTrans1, 5)

Local LabelZoneTrans2% = CreateLabel("HCZ to EZ transition", 120, 60, 120, 20, Map_Settings)

Global ZoneTrans2% = CreateTextField(130, 40, 80, 20, Map_Settings)

SetGadgetText(ZoneTrans2, 11)

Global ZoneTransValue1% = 13, ZoneTransValue2% = 7

Local ResetZoneTrans% = CreateButton("Reset", 10, 90, 100, 30, Map_Settings)
Local ApplyZoneTrans% = CreateButton("Apply", 120, 90, 100, 30, Map_Settings)

Local AuthorDescr_Settings% = CreateWindow("Edit Author and Description", GraphicsWidth() / 2 - 200, GraphicsHeight() / 2 - 80, 400, 200, WinHandle, 1)

HideGadget(AuthorDescr_Settings)

Global MapAuthor$ = "", MapDescription$ = ""
Global Map_Author_Text% = CreateTextField(120, 30, 140, 20, AuthorDescr_Settings)

Local Map_Author_Label% = CreateLabel("Map author:", 140, 10, 160, 20, AuthorDescr_Settings)

Global Descr_Text% = CreateTextArea(20, 80, 350, 80, AuthorDescr_Settings, 1)

Local Descr_Label% = CreateLabel("Description:", 140, 60, 160, 20, AuthorDescr_Settings)

SetStatusText(LoadingWindow, "Executing 3-D viewer...")
ExecFile("Window3D.exe")

Repeat
	Local VWPRT% = FindWindow("Blitz Runtime Class", "MapCreator 3-D View") ; ~ User32.dll
	
	ShowGadget(LoadingWindow)
Until VWPRT <> 0
SetStatusText(LoadingWindow, "Creating 3-D scene...")

SetParent(VWPRT, MainHwnd) ; ~ User32.dll				
api_SetWindowPos(VWPRT, 0, 5, 30, 895, 560, 1) ; ~ User32.dll
ShowWindow(VWPRT, 0) ; ~ User32.dll

HideGadget(LoadingWindow)
ShowGadget(WinHandle)

SetBuffer(CanvasBuffer(Map_2D))

Global MouseHit1%, MouseHit2%, MouseDown1%, MouseDown2%, MouseHit3%

Repeat
	Local x%, y%, x2%, y2%
	Local c%, e%, FileName$, Name$
	
	MouseHit1 = MouseHit(1)
	MouseHit2 = MouseHit(2)
	MouseDown1 = MouseDown(1)
	MouseDown2 = MouseDown(2)
	MouseHit3 = MouseHit(3)
	
	SetGadgetText(Map_Author_Text, Left(TextFieldText(Map_Author_Text), 15))
	SetGadgetText(Map_Author_Label, ("Map author (" + (Len(TextFieldText(Map_Author_Text))) + "/15) :"))
	
	If Len(TextAreaText(Descr_Text)) > 200 Then
		SetGadgetText(Descr_Text, Left(TextAreaText(Descr_Text), 200))
	EndIf
	SetGadgetText(Descr_Label, ("Description (" + (Len(TextAreaText(Descr_Text))) + "/200) :"))
	
	If FileType("CONFIG_TO2D.SI") = 1 Then
		Local f% = ReadFile("CONFIG_TO2D.SI")
		Local ev.Event
		
		Grid_SelectedX = ReadInt(f)
		Grid_SelectedY = ReadInt(f)
		
		ChangeGridGadget = True
		GridGadgetText = ""
		SelectGadgetItem(ListBox, -1)
		HideGadget(ListBox)
		ShowGadget(ListBox)
		ClearGadgetItems(ComboBox)
		
		If CurrMapGrid = 0 Then
			Local HasEvent% = False
			Local CurrEventDescr$ = ""
			
			For rt.RoomTemplates = Each RoomTemplates
				If rt = Map(Grid_SelectedX, Grid_SelectedY)
					For i = 0 To 5
						If rt\Events[i] <> "" Then
							InsertGadgetItem(ComboBox, 0, "[none]")
							HasEvent = True
							Exit
						EndIf
					Next
					For i = 0 To 5
						If rt\Events[i] <> "" Then
							InsertGadgetItem(ComboBox, i + 1, rt\Events[i])
						EndIf
					Next
					SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
					Exit
				EndIf
			Next 
			
			If (Not HasEvent) Then
				DisableGadget(ComboBox)
				SetGadgetText(Event_Desc, "")
				SetGadgetText(Event_Prob_Label, "")
				SetSliderValue(Event_Prob, 99)
				DisableGadget(Event_Prob)
				GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°"
			Else
				EnableGadget(ComboBox)
				If MapEvent(Grid_SelectedX, Grid_SelectedY) <> "" And MapEvent(Grid_SelectedX, Grid_SelectedY) <> "[none]"
					For ev.Event = Each Event
						If ev\Name = MapEvent(Grid_SelectedX, Grid_SelectedY)
							SetGadgetText(Event_Desc, "Event description:" + Chr(13) + ev\Description)
							Exit
						EndIf
					Next
				Else
					SetGadgetText(Event_Desc, "")
				EndIf
				If MapEvent(Grid_SelectedX, Grid_SelectedY) <> "" And MapEvent(Grid_SelectedX, Grid_SelectedY) <> "[none]"
					SetGadgetText(Event_Prob_Label, "Event chance: " + Int(MapEventProb(Grid_SelectedX, Grid_SelectedY) * 100) + "%")
					SetSliderValue(Event_Prob, Int(MapEventProb(Grid_SelectedX, Grid_SelectedY) * 100) - 1)
					EnableGadget(Event_Prob)
					GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°" + Chr(13) + "Event: " + MapEvent(Grid_SelectedX, Grid_SelectedY) + Chr(13) + "Event Chance: " + Int(MapEventProb(Grid_SelectedX, Grid_SelectedY) * 100) + "%"
				Else
					SetGadgetText(Event_Prob_Label, "")
					SetSliderValue(Event_Prob, 99)
					DisableGadget(Event_Prob)
					GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°"
				EndIf
			EndIf
			
			c = CountGadgetItems(ComboBox)
			If c >= 0 Then
				For e = 0 To c - 1
					If GadgetItemText(ComboBox, e) = MapEvent(Grid_SelectedX, Grid_SelectedY)
						SelectGadgetItem(ComboBox, e)
					EndIf
				Next
			EndIf
		ElseIf CurrMapGrid = 1
			For rt.RoomTemplates = Each RoomTemplates
				If rt = ForestPlace(Grid_SelectedX, Grid_SelectedY)
					SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
					Exit
				EndIf
			Next 
			
			DisableGadget(ComboBox)
			SetGadgetText(Event_Desc, "")
			SetGadgetText(Event_Prob_Label, "")
			SetSliderValue(Event_Prob, 99)
			DisableGadget(Event_Prob)
			GridGadgetText = "Name: " + ForestPlace(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) + "°"
		Else
			For rt.RoomTemplates = Each RoomTemplates
				If rt = MTRoom(Grid_SelectedX, Grid_SelectedY)
					SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
					Exit
				EndIf
			Next 
			
			DisableGadget(ComboBox)
			SetGadgetText(Event_Desc, "")
			SetGadgetText(Event_Prob_Label, "")
			SetSliderValue(Event_Prob, 99)
			DisableGadget(Event_Prob)
			GridGadgetText = "Name: " + MTRoom(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MTRoomAngle(Grid_SelectedX, Grid_SelectedY) + "°"
		EndIf
		
		CloseFile(f)
		DeleteFile("CONFIG_TO2D.SI")
	EndIf
	
	If ShowGrid Then
		Cls()
		
		Local Width# = GadgetWidth(Map_2D)
		Local Height# = GadgetHeight(Map_2D)
		
		; ~ Facility grid
		If CurrMapGrid = 0 Then
			For x = 0 To MapSize
				For y = 0 To MapSize
					If y < ZoneTransValue2 Then
						Color(255, 255, 125)
					ElseIf y = ZoneTransValue2
						Color(255, 200, 125)
					ElseIf y > ZoneTransValue2 And y < ZoneTransValue1
						Color(255, 125, 125)
					ElseIf y = ZoneTransValue1
						Color(255, 200, 200)
					Else
						Color(255, 255, 255)
					EndIf
					
					Rect(Float(Width) / Float(MapSize + 1) * x, Float(Height) / Float(MapSize + 1) * y, (Float(Width) / Float(MapSize + 1)), (Float(Height) / Float(MapSize + 1)), True)
					
					Local PrevSelectedX% = Grid_SelectedX, PrevSelectedY% = Grid_SelectedY
					
					If (MouseX() - GadgetX(Map_2D)) > (Float(Width) / Float(MapSize + 1) * x + GadgetX(WinHandle)) And (MouseX() - GadgetX(Map_2D)) < ((Float(Width) / Float(MapSize + 1) * x) + (Float(Width) / Float(MapSize + 1)) + GadgetX(WinHandle))
						Local Offset% = 45
						
						If (MouseY() - GadgetY(Map_2D)) > (Float(Height) / Float(MapSize + 1) * y + GadgetY(WinHandle) + Offset) And (MouseY() - GadgetY(Map_2D)) < ((Float(Height) / Float(MapSize + 1) * y) + (Float(Height) / Float(MapSize + 1)) + GadgetY(WinHandle) + Offset)
							Color(200, 200, 200)
							Rect(Float(Width) / Float(MapSize + 1) * x, Float(Height) / Float(MapSize + 1) * y, (Float(Width) / Float(MapSize + 1)), (Float(Height) / Float(MapSize + 1)), True)
							If Map(x, y) = Null And SelectedGadgetItem(ListBox) > -1
								x2 = Float(Width) / Float(MapSize + 1)
								y2 = Float(Height) / Float(MapSize + 1)
								DrawImage(PlusIcon, (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
							EndIf
							If MouseHit1 Then
								If Grid_SelectedX <> x Or Grid_SelectedY <> y Then
									Local Item% = SelectedGadgetItem(ListBox)
									
									If Map(x, y) <> Null
										Grid_SelectedX = x
										Grid_SelectedY = y
										ChangeGridGadget = True
										GridGadgetText = ""
										SelectGadgetItem(ListBox, -1)
										HideGadget(ListBox)
										ShowGadget(ListBox)
										
										ClearGadgetItems(ComboBox)
										
										HasEvent = False
										CurrEventDescr = ""
										For rt.RoomTemplates = Each RoomTemplates
											If rt = Map(x, y) Then
												For i = 0 To 5
													If rt\Events[i] <> "" Then
														InsertGadgetItem(ComboBox, 0, "[none]")
														HasEvent = True
														Exit
													EndIf
												Next
												For i = 0 To 5
													If rt\Events[i] <> "" Then
														InsertGadgetItem(ComboBox, i + 1, rt\Events[i])
													EndIf
												Next
												SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
												Exit
											EndIf
										Next 
										
										If (Not HasEvent) Then
											DisableGadget(ComboBox)
											SetGadgetText(Event_Desc, "")
											SetGadgetText(Event_Prob_Label, "")
											SetSliderValue(Event_Prob, 99)
											DisableGadget(Event_Prob)
										Else
											EnableGadget(ComboBox)
											If MapEvent(x, y) <> "" And MapEvent(x, y) <> "[none]" Then
												For ev.Event = Each Event
													If ev\Name = MapEvent(x, y)
														SetGadgetText(Event_Desc, "Event description:" + Chr(13) + ev\Description)
														Exit
													EndIf
												Next
											Else
												SetGadgetText(Event_Desc, "")
											EndIf
											If MapEvent(x, y) <> "" And MapEvent(x, y) <> "[none]" Then
												SetGadgetText(Event_Prob_Label, "Event chance: 100%")
												SetSliderValue(Event_Prob, 99)
												EnableGadget(Event_Prob)
											Else
												SetGadgetText(Event_Prob_Label, "")
												SetSliderValue(Event_Prob, 99)
												DisableGadget(Event_Prob)
											EndIf
										EndIf
										
										c = CountGadgetItems(ComboBox)
										If c >= 0 Then
											For e = 0 To c - 1
												If GadgetItemText(ComboBox, e) = MapEvent(x, y) Then
													SelectGadgetItem(ComboBox, e)
												EndIf
											Next
										EndIf
									EndIf
									If Item >= 0 Then
										If Map(x, y) = Null Then
											Local Room_Name$ = GadgetItemText(ListBox, Item)
											
											For rt.RoomTemplates = Each RoomTemplates
												If rt\Name = Room_Name
													Map(x, y) = rt
													Exit
												EndIf
											Next
											If Map(x, y)\Name = "cont1_173" Or Map(x, y)\Name = "room2_checkpoint_lcz_hcz" Or Map(x, y)\Name = "room2_checkpoint_hcz_ez" Then MapAngle(x, y) = 180
											
											Local Item2% = SelectedGadgetItem(ComboBox)
											
											If Item2 >= 0 Then
												Local Event_Name$ = GadgetItemText(ComboBox, Item2)
												
												If Event_Name <> "" And Event_Name <> "[none]" Then
													MapEvent(x, y) = Event_Name
													MapEventProb(x, y) = Float((SliderValue(Event_Prob) + 1) / 100.0)
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
							If MouseDown2 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
								If Map(x, y) <> Null
									Map(x, y) = Null
									MapAngle(x, y) = 0
									MapEvent(x, y) = ""
									MapEventProb(x, y) = 0.0
								EndIf
							EndIf
							If MouseHit3 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
							EndIf
						EndIf
					EndIf
						
					If Grid_SelectedX = x And Grid_SelectedY = y Then
						Color(150, 150, 150)
						Rect(Float(Width) / Float(MapSize + 1) * x, Float(Height) / Float(MapSize + 1) * y, (Float(Width) / Float(MapSize + 1)), (Float(Height) / Float(MapSize + 1)), True)
					EndIf
					
					If Map(x, y) = Null Then
						Color(90, 90, 90)
						Rect(Float(Width) / Float(MapSize + 1) * x + 1, Float(Height) / Float(MapSize + 1) * y + 1, (Float(Width) / Float(MapSize + 1)) - 1, (Float(Height) / Float(MapSize + 1)) - 1, False)
					Else
						x2 = Float(Width) / Float(MapSize + 1)
						y2 = Float(Height) / Float(MapSize + 1)
						DrawImage(MapIcons(Map(x, y)\Shape, Floor(MapAngle(x, y) / 90)), (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
						
						If Grid_SelectedX = x And Grid_SelectedY = y Then
							If PrevSelectedX <> Grid_SelectedX Or PrevSelectedY <> Grid_SelectedY Then
								ChangeGridGadget = True
								If MapEvent(x, y) <> "" And MapEvent(x, y) <> "[none]"
									GridGadgetText = "Name: " + Map(x, y)\Name + Chr(13) + "Angle: " + MapAngle(x, y) + "°" + Chr(13) + "Event: " + MapEvent(x, y) + Chr(13) + "Event Chance: " + Int(MapEventProb(x, y) * 100) + "%"
									SetSliderValue(Event_Prob, Int(MapEventProb(x, y) * 100) - 1)
								Else
									GridGadgetText = "Name: " + Map(x, y)\Name + Chr(13) + "Angle: " + MapAngle(x, y) + "°"
								EndIf
								If GadgetText(Event_Prob_Label) <> "" Then
									SetGadgetText(Event_Prob_Label, "Event chance: " + (SliderValue(Event_Prob) + 1) + "%")
								EndIf
							EndIf
						EndIf
					EndIf
				Next
			Next
			
			If MouseDown1 Then
				If Grid_SelectedX > -1 And Grid_SelectedY > -1 Then
					If MouseX() > (GadgetX(Map_2D) + GadgetX(WinHandle)) And MouseX() < ((Width) + GadgetX(Map_2D) + GadgetX(WinHandle))
						Offset = 45
						If MouseY() > (GadgetY(Map_2D) + GadgetY(WinHandle) + Offset) And MouseY() < ((Height) + GadgetY(Map_2D) + GadgetY(WinHandle) + Offset)
							If Map(Grid_SelectedX, Grid_SelectedY)\Name <> "cont1_173" And Map(Grid_SelectedX, Grid_SelectedY)\Name <> "room2_checkpoint_lcz_hcz" And Map(Grid_SelectedX, Grid_SelectedY)\Name <> "room2_checkpoint_hcz_ez" Then
								Local PrevAngle% = MapAngle(Grid_SelectedX, Grid_SelectedY)
								
								; ~ Left
								If (MouseX() - GadgetX(Map_2D)) < (Float(Width) / Float(MapSize + 1) * Grid_SelectedX + GadgetX(WinHandle))
									MapAngle(Grid_SelectedX, Grid_SelectedY) = 90
								EndIf
								; ~ Right
								If (MouseX() - GadgetX(Map_2D)) > ((Float(Width) / Float(MapSize + 1) * Grid_SelectedX) + (Float(Width) / Float(MapSize + 1)) + GadgetX(WinHandle))
									MapAngle(Grid_SelectedX, Grid_SelectedY) = 270
								EndIf
								; ~ Up
								Offset = 45
								If (MouseY() - GadgetY(Map_2D)) < (Float(Height) / Float(MapSize + 1) * Grid_SelectedY + GadgetY(WinHandle) + Offset)
									MapAngle(Grid_SelectedX, Grid_SelectedY) = 180
								EndIf
								; ~ Down
								If (MouseY() - GadgetY(Map_2D)) > ((Float(Height) / Float(MapSize + 1) * Grid_SelectedY) + (Float(Height) / Float(MapSize + 1)) + GadgetY(WinHandle) + Offset)
									MapAngle(Grid_SelectedX, Grid_SelectedY) = 0
								EndIf
								
								Local Width2% = Float(Width) / Float(MapSize + 1) / 2.0
								Local Height2% = Float(Height) / Float(MapSize + 1) / 2.0
								
								DrawImage(Arrows[Floor(MapAngle(Grid_SelectedX, Grid_SelectedY) / 90)], Float(Width) / Float(MapSize + 1) * Grid_SelectedX + Width2, Float(Height) / Float(MapSize + 1) * Grid_SelectedY + Height2)
								If PrevAngle <> MapAngle(Grid_SelectedX, Grid_SelectedY) Then
									ChangeGridGadget = True
									If MapEvent(Grid_SelectedX, Grid_SelectedY) <> "" And MapEvent(Grid_SelectedX, Grid_SelectedY) <> "[none]" Then
										GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°" + Chr(13) + "Event: " + MapEvent(Grid_SelectedX, Grid_SelectedY) + Chr(13) + "Event Chance: " + Int(MapEventProb(Grid_SelectedX, Grid_SelectedY) * 100) + "%"
									Else
										GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°"
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		ElseIf CurrMapGrid = 1 ; ~ Forest grid
			For x = 0 To ForestGridSize
				For y = 0 To ForestGridSize
					Color(125, 255, 255)
					If x = ForestGridSize Or y = ForestGridSize Then
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSize + 1)) + 1,(Float(Height - 1) / Float(ForestGridSize + 1)) + 1, True)
					Else
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSize + 1)), (Float(Height - 1) / Float(ForestGridSize + 1)), True)
					EndIf
					
					If ForestPlace(x, y) = Null Then
						Color(90, 90, 90)
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x + 1, Float(Height - 1) / Float(ForestGridSize + 1) * y + 1, (Float(Width - 1) / Float(ForestGridSize + 1)) - 1, (Float(Height - 1) / Float(ForestGridSize + 1)) - 1, False)
					EndIf
				Next
			Next
			
			For x = 0 To ForestGridSize
				For y = 0 To ForestGridSize
					Color(255, 255, 255)
					If x = ForestGridSize Or y = ForestGridSize Then
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSize + 1)) + 1, (Float(Height - 1) / Float(ForestGridSize + 1)) + 1, True)
					Else
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSize + 1)), (Float(Height - 1) / Float(ForestGridSize + 1)), True)
					EndIf
					
					PrevSelectedX = Grid_SelectedX
					PrevSelectedY = Grid_SelectedY
					If (MouseX() - GadgetX(Map_2D)) > (Float(Width - 1) / Float(ForestGridSize + 1) * x + GadgetX(WinHandle)) And (MouseX() - GadgetX(Map_2D)) < ((Float(Width - 1) / Float(ForestGridSize + 1) * x) + (Float(Width - 1) / Float(ForestGridSize + 1)) + GadgetX(WinHandle))
						Offset = 45
						If (MouseY() - GadgetY(Map_2D)) > (Float(Height - 1) / Float(ForestGridSize + 1) * y + GadgetY(WinHandle) + Offset) And (MouseY() - GadgetY(Map_2D)) < ((Float(Height - 1) / Float(ForestGridSize + 1) * y) + (Float(Height - 1) / Float(ForestGridSize + 1)) + GadgetY(WinHandle) + Offset)
							Color(200, 200, 200)
							Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSizee + 1)), (Float(Height - 1) / Float(ForestGridSize + 1)), True)
							If ForestPlace(x, y) = Null And SelectedGadgetItem(ListBox) > -1 Then
								x2 = Float(Width) / Float(ForestGridSize + 1)
								y2 = Float(Height) / Float(ForestGridSize + 1)
								DrawImage(PlusIcon, (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
							EndIf
							If MouseHit1 Then
								If Grid_SelectedX <> x Or Grid_SelectedY <> y Then
									Item = SelectedGadgetItem(ListBox)
									If ForestPlace(x, y) <> Null
										Grid_SelectedX = x
										Grid_SelectedY = y
										ChangeGridGadget = True
										GridGadgetText = ""
										SelectGadgetItem(ListBox, -1)
										HideGadget(ListBox)
										ShowGadget(ListBox)
										
										ClearGadgetItems(ComboBox)
										
										For rt.RoomTemplates = Each RoomTemplates
											If rt = ForestPlace(x, y)
												SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
												Exit
											EndIf
										Next 
										
										DisableGadget(ComboBox)
										SetGadgetText(Event_Desc, "")
										SetGadgetText(Event_Prob_Label, "")
										SetSliderValue(Event_Prob, 99)
										DisableGadget(Event_Prob)
									EndIf
									
									If Item >= 0 Then
										If ForestPlace(x, y) = Null Then
											Room_Name$ = GadgetItemText(ListBox, Item)
											For rt.RoomTemplates = Each RoomTemplates
												If rt\Name = Room_Name
													ForestPlace(x, y) = rt
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							If MouseDown2 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
								If ForestPlace(x, y) <> Null Then
									ForestPlace(x, y) = Null
									ForestPlaceAngle(x, y) = 0
								EndIf
							EndIf
							If MouseHit3 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
							EndIf
						EndIf
					EndIf
					
					If Grid_SelectedX = x And Grid_SelectedY = y Then
						Color(150, 150, 150)
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x, Float(Height - 1) / Float(ForestGridSize + 1) * y, (Float(Width - 1) / Float(ForestGridSize + 1)), (Float(Height - 1) / Float(ForestGridSize + 1)), True)
					EndIf
					
					If ForestPlace(x, y) = Null Then
						Color(90, 90, 90)
						Rect(Float(Width - 1) / Float(ForestGridSize + 1) * x + 1, Float(Height - 1) / Float(ForestGridSize + 1) * y + 1, (Float(Width - 1) / Float(ForestGridSize + 1)) - 1, (Float(Height - 1) / Float(ForestGridSize + 1)) - 1, False)
					Else
						x2 = Float(Width - 1) / Float(ForestGridSize + 1)
						y2 = Float(Height - 1) / Float(ForestGridSize +1)
						If ForestPlace(x, y)\Name = "SCP-860-1 door" Then
							DrawImage(SpecialIcons(1, Floor(ForestPlaceAngle(x, y) / 90.0)), (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
						Else
							DrawImage(ForestIcons(ForestPlace(x, y)\Shape, Floor(ForestPlaceAngle(x, y) / 90.0)), (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
						EndIf
						
						If Grid_SelectedX = x And Grid_SelectedY = y Then
							If PrevSelectedX <> Grid_SelectedX Or PrevSelectedY <> Grid_SelectedY Then
								ChangeGridGadget = True
								GridGadgetText = "Name: " + ForestPlace(x, y)\Name + Chr(13) + "Angle: " + ForestPlaceAngle(x, y) + "°"
							EndIf
						EndIf
					EndIf
				Next
			Next
			If MouseDown1 Then
				If Grid_SelectedX > -1 And Grid_SelectedY > -1 Then
					If MouseX() > (GadgetX(Map_2D) + GadgetX(WinHandle)) And MouseX() < ((Width) + GadgetX(Map_2D) + GadgetX(WinHandle))
						Offset = 45
						If MouseY() > (GadgetY(Map_2D) + GadgetY(WinHandle) + Offset) And MouseY() < ((Height) + GadgetY(Map_2D) + GadgetY(WinHandle) + Offset)
							PrevAngle = ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY)
							; ~ Left
							If (MouseX() - GadgetX(Map_2D)) < (Float(Width - 1) / Float(ForestGridSize + 1) * Grid_SelectedX + GadgetX(WinHandle))
								ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) = 90.0
							EndIf
							; ~ Right
							If (MouseX() - GadgetX(Map_2D)) > ((Float(Width - 1) / Float(ForestGridSize + 1) * Grid_SelectedX) + (Float(Width - 1) / Float(ForestGridSize + 1)) + GadgetX(WinHandle))
								ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) = 270.0
							EndIf
							; ~ Up
							Offset = 45
							If (MouseY() - GadgetY(Map_2D)) < (Float(Height - 1) / Float(ForestGridSize + 1) * Grid_SelectedY + GadgetY(WinHandle) + Offset)
								ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) = 180.0
							EndIf
							; ~ Down
							If (MouseY() - GadgetY(Map_2D)) > ((Float(Height - 1) / Float(ForestGridSize + 1) * Grid_SelectedY) + (Float(Height - 1) / Float(ForestGridSize + 1)) + GadgetY(WinHandle) + Offset)
								ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) = 0.0
							EndIf
							Width2 = Float(Width - 1) / Float(ForestGridSize + 1) / 2.0
							Height2 = Float(Height - 1) / Float(ForestGridSize + 1) / 2.0
							DrawImage(Arrows[Floor(ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) / 90)], Float(Width - 1) / Float(ForestGridSize + 1) * Grid_SelectedX + Width2, Float(Height - 1) / Float(ForestGridSize + 1) * Grid_SelectedY + Height2)
							If PrevAngle <> ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY)
								ChangeGridGadget = True
								GridGadgetText = "Name: " + ForestPlace(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + ForestPlaceAngle(Grid_SelectedX, Grid_SelectedY) + "°"
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Else ; ~ Maintenance tunnel grid
			For x = 0 To MT_GridSize
				For y = 0 To MT_GridSize
					Color(255, 255, 255)
					Rect(Float(Width) / Float(MT_GridSize + 1) * x, Float(Height) / Float(MT_GridSize + 1) * y, (Float(Width) / Float(MT_GridSize + 1)), (Float(Height) / Float(MT_GridSize + 1)), True)
					
					PrevSelectedX = Grid_SelectedX
					PrevSelectedY = Grid_SelectedY
					If (MouseX() - GadgetX(Map_2D)) > (Float(Width) / Float(MT_GridSize + 1) * x + GadgetX(WinHandle)) And (MouseX() - GadgetX(Map_2D)) < ((Float(Width) / Float(MT_GridSize + 1) * x) + (Float(Width) / Float(MT_GridSize + 1)) + GadgetX(WinHandle))
						Offset = 45
						If (MouseY() - GadgetY(Map_2D)) > (Float(Height) / Float(MT_GridSize + 1) * y + GadgetY(WinHandle) + Offset) And (MouseY() - GadgetY(Map_2D)) < ((Float(Height) / Float(MT_GridSize + 1) * y) + (Float(Height) / Float(MT_GridSize + 1)) + GadgetY(WinHandle) + Offset)
							Color(200, 200, 200)
							Rect(Float(Width) / Float(MT_GridSize + 1) * x, Float(Height) / Float(MT_GridSize + 1) * y, (Float(Width) / Float(MT_GridSize + 1)), (Float(Height) / Float(MT_GridSize + 1)), True)
							If MTRoom(x, y) = Null And SelectedGadgetItem(ListBox) > -1 Then
								x2 = Float(Width) / Float(MT_GridSize + 1)
								y2 = Float(Height) / Float(MT_GridSize + 1)
								DrawImage(PlusIcon, (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
							EndIf
							If MouseHit1 Then
								If Grid_SelectedX <> x Or Grid_SelectedY <> y Then
									Item = SelectedGadgetItem(ListBox)
									If MTRoom(x, y) <> Null
										Grid_SelectedX = x
										Grid_SelectedY = y
										ChangeGridGadget = True
										GridGadgetText = ""
										SelectGadgetItem(ListBox, -1)
										HideGadget(ListBox)
										ShowGadget(ListBox)
										
										ClearGadgetItems(ComboBox)
										
										For rt.RoomTemplates = Each RoomTemplates
											If rt = MTRoom(x, y)
												SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
												Exit
											EndIf
										Next 
										
										DisableGadget(ComboBox)
										SetGadgetText(Event_Desc, "")
										SetGadgetText(Event_Prob_Label, "")
										SetSliderValue(Event_Prob, 99)
										DisableGadget(Event_Prob)
									EndIf
									If Item >= 0 Then
										If MTRoom(x, y) = Null
											Room_Name = GadgetItemText(ListBox, Item)
											For rt.RoomTemplates = Each RoomTemplates
												If rt\Name = Room_Name
													MTRoom(x, y) = rt
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf
							If MouseDown2 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
								If MTRoom(x, y) <> Null Then
									MTRoom(x, y) = Null
									MTRoomAngle(x, y) = 0
								EndIf
							EndIf
							If MouseHit3 Then
								Grid_SelectedX = -1
								Grid_SelectedY = -1
								ChangeGridGadget = True
								GridGadgetText = ""
								SetSliderValue(Event_Prob, 99)
								SetGadgetText(Event_Prob_Label, "")
								DisableGadget(Event_Prob)
								SetGadgetText(Event_Desc, "")
								DisableGadget(ComboBox)
								ClearGadgetItems(ComboBox)
							EndIf
						EndIf
					EndIf
					
					If Grid_SelectedX = x And Grid_SelectedY = y Then
						Color(150, 150, 150)
						Rect(Float(Width) / Float(MT_GridSize + 1) * x, Float(Height) / Float(MT_GridSize + 1) * y, (Float(Width) / Float(MT_GridSize + 1)), (Float(Height) / Float(MT_GridSize + 1)), True)
					EndIf
					
					If MTRoom(x, y) = Null Then
						Color(90, 90, 90)
						Rect(Float(Width) / Float(MT_GridSize + 1) * x + 1, Float(Height) / Float(MT_GridSize + 1) * y + 1, (Float(Width) / Float(MT_GridSize + 1)) - 1, (Float(Height) / Float(MT_GridSize + 1)) - 1, False)
					Else
						x2 = Float(Width) / Float(MT_GridSize + 1)
						y2 = Float(Height) / Float(MT_GridSize + 1)
						If MTRoom(x, y)\Name = "Maintenance tunnel elevator"
							DrawImage(SpecialIcons(2, Floor(MTRoomAngle(x, y) / 90.0)), (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
						Else
							DrawImage(MapIcons(MTRoom(x, y)\Shape, Floor(MTRoomAngle(x, y) / 90.0)), (x2 * x) + (x2 / 2.0) + 0.5, (y2 * y) + (y2 / 2.0) + 0.5)
						EndIf
						
						If Grid_SelectedX = x And Grid_SelectedY = y Then
							If PrevSelectedX <> Grid_SelectedX Or PrevSelectedY <> Grid_SelectedY Then
								ChangeGridGadget = True
								GridGadgetText = "Name: " + MTRoom(x, y)\Name + Chr(13) + "Angle: " + MTRoomAngle(x, y) + "°"
							EndIf
						EndIf
					EndIf
				Next
			Next
			If MouseDown1 Then
				If Grid_SelectedX > -1 And Grid_SelectedY > -1 Then
					If MouseX() > (GadgetX(Map_2D) + GadgetX(WinHandle)) And MouseX() < ((Width) + GadgetX(Map_2D) + GadgetX(WinHandle))
						Offset = 45
						If MouseY()>(GadgetY(Map_2D) + GadgetY(WinHandle) + Offset) And MouseY() < ((Height) + GadgetY(Map_2D) + GadgetY(WinHandle) + Offset)
							PrevAngle = MTRoomAngle(Grid_SelectedX, Grid_SelectedY)
							; ~ Left
							If (MouseX() - GadgetX(Map_2D)) < (Float(Width) / Float(MT_GridSize + 1) * Grid_SelectedX + GadgetX(WinHandle))
								MTRoomAngle(Grid_SelectedX, Grid_SelectedY) = 90.0
							EndIf
							; ~ Right
							If (MouseX() - GadgetX(Map_2D)) > ((Float(Width) / Float(MT_GridSize + 1) * Grid_SelectedX) + (Float(Width) / Float(MT_GridSize + 1)) + GadgetX(WinHandle))
								MTRoomAngle(Grid_SelectedX, Grid_SelectedY) = 270.0
							EndIf
							; ~ Up
							Offset = 45
							If (MouseY() - GadgetY(Map_2D)) < (Float(Height) / Float(MT_GridSize + 1) * Grid_SelectedY + GadgetY(WinHandle) + Offset)
								MTRoomAngle(Grid_SelectedX, Grid_SelectedY) = 180.0
							EndIf
							; ~ Down
							If (MouseY() - GadgetY(Map_2D)) > ((Float(Height) / Float(MT_GridSize + 1) * Grid_SelectedY) + (Float(Height) / Float(MT_GridSize + 1)) + GadgetY(WinHandle) + Offset)
								MTRoomAngle(Grid_SelectedX, Grid_SelectedY) = 0.0
							EndIf
							Width2 = Float(Width) / Float(MT_GridSize + 1) / 2.0
							Height2 = Float(Height) / Float(MT_GridSize + 1) / 2.0
							DrawImage(Arrows[Floor(MTRoomAngle(Grid_SelectedX, Grid_SelectedY) / 90)], Float(Width) / Float(MT_GridSize + 1) * Grid_SelectedX + Width2, Float(Height) / Float(MT_GridSize + 1) * Grid_SelectedY + Height2)
							If PrevAngle <> MTRoomAngle(Grid_SelectedX, Grid_SelectedY)
								ChangeGridGadget = True
								GridGadgetText = "Name: " + MTRoom(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MTRoomAngle(Grid_SelectedX, Grid_SelectedY) + "°"
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		FlipCanvas(Map_2D)
	EndIf
	
	If Grid_SelectedX <> -1 And Grid_SelectedY <> -1 Then
		Local PrevEvent% = MapEvent(Grid_SelectedX, Grid_SelectedY)
		
		Item2 = SelectedGadgetItem(ComboBox)
		If Item2 >= 0 Then
			Event_Name = GadgetItemText(ComboBox, Item2)
			If Event_Name <> PrevEvent
				If Event_Name <> "" And Event_Name <> "[none]"
					MapEvent(Grid_SelectedX, Grid_SelectedY) = Event_Name
					MapEventProb(Grid_SelectedX, Grid_SelectedY) = Float((SliderValue(Event_Prob) + 1) / 100.0)
					GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°" + Chr(13) + "Event: " + MapEvent(Grid_SelectedX, Grid_SelectedY) + Chr(13) + "Event Chance: " + Int(MapEventProb(Grid_SelectedX, Grid_SelectedY) * 100) + "%"
					ChangeGridGadget = True
				Else
					MapEvent(Grid_SelectedX, Grid_SelectedY) = Event_Name
					MapEventProb(Grid_SelectedX, Grid_SelectedY) = 0.0
					GridGadgetText = "Name: " + Map(Grid_SelectedX, Grid_SelectedY)\Name + Chr(13) + "Angle: " + MapAngle(Grid_SelectedX, Grid_SelectedY) + "°"
					ChangeGridGadget = True
				EndIf
			EndIf
		EndIf
	EndIf
	
	If ChangeGridGadget Then
		SetGadgetText(Grid_Room_Info, GridGadgetText)
		ChangeGridGadget = False
	EndIf
	
	Local ID% = WaitEvent()
	
	If ID = $803 And EventSource() = WinHandle Then Exit ; ~ Handle the close gadget on the window being hit
	If ID = $803 And EventSource() = OptionWin Then HideGadget(OptionWin)
	If ID = $803 And EventSource() = Map_Settings Then HideGadget(Map_Settings)
	If ID = $803 And EventSource() = AuthorDescr_Settings Then HideGadget(AuthorDescr_Settings)
	If ID = $1001 Then ; ~ Handle any menu item hit events
		; ~ Extract the EventData as this will contain our unique id for the menu item
		Local EID% = EventData() 
		
	    If EID = 0 Then 
			Local Result% = Proceed("Save current map?", True)
			
			If Result = 1 Then
				SetStatusText(WinHandle, "Created new map and saving prev. map")
				If FileType(FileName) <> 1 Then
  			   		FileName = RequestFile("Open map", "cbmap", True, "")
				EndIf
				If FileName <> "" Then
					SaveMap(FileName)
				EndIf
				EraseMap()
				If ShowGrid = False
					SaveMap("CONFIG_MAPINIT.SI", True)
				EndIf
				FileName = ""
			ElseIf Result = 0 Then 
				SetStatusText(WinHandle, "Created new map without saving prev. map")
				EraseMap()
				If ShowGrid = False
					SaveMap("CONFIG_MAPINIT.SI", True)
				EndIf
				FileName = ""
			ElseIf Result = -1 Then
				SetStatusText(WinHandle, "Creating new map has been cancelled")
			EndIf
		EndIf
		If EID = 1 Then
			FileName = RequestFile("Open Map","*cbmap2;*cbmap,*cbmap2,*cbmap", False, "") 
			If FileName <> "" Then
				LoadMap(FileName)
			EndIf
		EndIf
		If EID = 2 Then
			If FileType(FileName) <> 1 Then
				FileName = RequestFile("Save Map", "cbmap2,cbmap", True, "")
			EndIf
			If FileName <> "" Then
				If Right(FileName, 5) = "cbmap" Then
					Local Value% = Confirm("cbmap is an outdated file format. Some data can be lost if you save your map to this file format." + Chr(13) + "Are you sure you want to proceed?", 0)
					
					If Value = 1 Then
						SaveMap(FileName, False, 1)
					EndIf
				Else
					SaveMap(FileName$)
				EndIf
			EndIf
		EndIf	
		If EID = 3 Then
			.back
			FileName = RequestFile("Save Map", "cbmap2, cbmap", True, "")
			If FileName <> "" Then
				If Right(FileName, 5) = "cbmap" Then
					Value = Confirm("cbmap is an outdated file format. Some data can be lost if you save your map to this file format." + Chr(13) + "Are you sure you want to proceed?", 0)
					If Value = 0 Then
						Goto back
					EndIf
					SaveMap(FileName, False, 1)
				Else
					SaveMap(FileName)
				EndIf
			EndIf
		EndIf
		
		If EID = 6 Then ExecFile("Manual.pdf")
		If EID = 40 Then Notify("SCP Containment Breach Ultimate Edition Map Creator" + Chr(13))
		If EID = 17 Then 
			ShowGadget(OptionWin)
		EndIf
		If EID = 15 Then
			Value = MenuChecked(Event_Default)
			If Value = 0 Then CheckMenu(Event_Default)
			If Value = 1 Then UncheckMenu(Event_Default)
			UpdateWindowMenu(WinHandle)
			PutINIValue(OptionFileMC, "General", "Events_Default", (Not Value))
		EndIf
		If EID = 18 Then
			ShowGadget(Map_Settings)
		EndIf
		If EID = 19 Then
			ShowGadget(AuthorDescr_Settings)
		EndIf
		If EID = 10001 Then End
	EndIf
	
	If ID = $401 Then ; ~ Button action event.  EventData contains the toolbar button hit
		If EventSource() = Tab Then
			Select EventData()
				Case 0
					;[Block]
                 	ShowWindow(VWPRT ,0) ; ~ User32.dll
                  	ShowGadget(ListBox)
					ShowGadget(Event_Desc) 
					ShowGadget(TxtBox)
					ShowGadget(OK)
					ShowGadget(Clean_Txt)
					ShowGadget(ComboBox)
					ShowGadget(Map_2D)
					ShowGadget(Room_Desc)
					ShowGadget(Event_Prob)
					ShowGadget(Event_Prob_Label)
					ShowGadget(Grid_Room_Info)
					ShowGadget(Tab2)
					SetGadgetShape(Tab, 0, 5, ResWidth / 4 + 20, ResHeight - 60)
					ShowGrid = True
					;[End Block]
				Case 1
					;[Block]
					ShowWindow(VWPRT, 1) ; ~ User32.dll
              		HideGadget(ListBox) 
					HideGadget(Event_Desc) 
					HideGadget(TxtBox) 
					HideGadget(OK)
					HideGadget(Clean_Txt)
					HideGadget(ComboBox)
					HideGadget(Map_2D)
					HideGadget(Room_Desc)
					HideGadget(Event_Prob)
					HideGadget(Event_Prob_Label)
					HideGadget(Grid_Room_Info)
					HideGadget(Tab2)
					SetGadgetShape(Tab, 0, 5, ResWidth, ResHeight - 60)
					ShowGrid = False
					SaveMap("CONFIG_MAPINIT.SI", True)
					;[End Block]
			End Select
		EndIf
		
		If EventSource() = Tab2 Then
			CurrMapGrid = EventData()
			ClearGadgetItems ListBox
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					AddGadgetItem(ListBox, rt\Name)
				EndIf
			Next
			ClearGadgetItems(ComboBox)
			DisableGadget(ComboBox)
			SetGadgetText(Event_Desc, "")
			SetGadgetText(Event_Prob_Label, "")
			SetSliderValue(Event_Prob, 99)
			DisableGadget(Event_Prob)
			SetGadgetText(Room_Desc, "Room description:")
			Grid_SelectedX = -1
			Grid_SelectedY = -1
		EndIf
		
		If EventSource() = Color_Button Then 
			If RequestColor(opt\FogR, opt\FogG, opt\FogB) = 1 Then
				opt\FogR = RequestedRed()
				opt\FogG = RequestedGreen()
				opt\FogB = RequestedBlue()
				SetGadgetText(LabelFogR, "R " + opt\FogR)
				SetGadgetText(LabelFogG, "G " + opt\FogG)
				SetGadgetText(LabelFogB, "B " + opt\FogB)
			EndIf	
		EndIf
		If EventSource() = Color_Button2 Then
			If RequestColor(opt\CursorR, opt\CursorG, opt\CursorB) = 1 Then
				opt\CursorR = RequestedRed()
				opt\CursorG = RequestedGreen()
				opt\CursorB = RequestedBlue()
				SetGadgetText(LabelCursorR, "R " + opt\CursorR)
				SetGadgetText(LabelCursorG, "G " + opt\CursorG)
				SetGadgetText(LabelCursorB, "B " + opt\CursorB)
			EndIf
		EndIf
		If EventSource() = CancelOpt_Button Then
			SetGadgetText(LabelFogR, "R " + opt\FogR)
			SetGadgetText(LabelFogG, "G " + opt\FogG)
			SetGadgetText(LabelFogB, "B " + opt\FogB)
			SetGadgetText(LabelCursorR, "R " + opt\CursorR)
			SetGadgetText(LabelCursorG, "G " + opt\CursorG)
			SetGadgetText(LabelCursorB, "B " + opt\CursorB)
			SetGadgetText(CameraRangeOpt, opt\CamRange)
			SetButtonState(VSync, opt\VSync)
			SetButtonState(ShowFPS, opt\ShowFPS)
			HideGadget(OptionWin)
		EndIf	
		If EventSource() = SaveOpt_Button Then
			HideGadget(OptionWin)
			SetStatusText(WinHandle, "New settings are saved")
			PutINIValue(OptionFileMC, "3-D Scene", "BG Color R", opt\FogR)
			PutINIValue(OptionFileMC, "3-D Scene", "BG Color G", opt\FogG)
			PutINIValue(OptionFileMC, "3-D Scene", "BG Color B", opt\FogB)
			PutINIValue(OptionFileMC, "3-D Scene", "Cursor Color R", opt\CursorR)
			PutINIValue(OptionFileMC, "3-D Scene", "Cursor Color G", opt\CursorG)
			PutINIValue(OptionFileMC, "3-D Scene", "Cursor Color B", opt\CursorB)
			PutINIValue(OptionFileMC, "3-D Scene", "Camera Range", TextFieldText(CameraRangeOpt))
			PutINIValue(OptionFileMC, "3-D Scene", "VSync", ButtonState(VSync))
			PutINIValue(OptionFileMC, "3-D Scene", "Show FPS", ButtonState(ShowFPS))
			WriteOptions()
		EndIf
		If EventSource() = ResetZoneTrans Then
			SetGadgetText(ZoneTrans1, 5)
			SetGadgetText(ZoneTrans2, 11)
			ZoneTransValue1 = (MapSize) - Int(TextFieldText(ZoneTrans1))
			ZoneTransValue2 = (MapSize) - Int(TextFieldText(ZoneTrans2))
		EndIf
		If EventSource() = ZoneTrans1 Then
			SetGadgetText(ZoneTrans1, Int(TextFieldText(ZoneTrans1)))
		EndIf
		If EventSource() = ZoneTrans2 Then
			SetGadgetText(ZoneTrans2, Int(TextFieldText(ZoneTrans2)))
		EndIf
		If EventSource() = ApplyZoneTrans Then
			SetGadgetText(ZoneTrans2, Int(Min(Max(Int(TextFieldText(ZoneTrans2)), Int(TextFieldText(ZoneTrans1)) + 2), MapSize - 1)))
			SetGadgetText(ZoneTrans1, Int(Min(Max(Int(TextFieldText(ZoneTrans1)), 1), Int(TextFieldText(ZoneTrans2)) - 2)))
			ZoneTransValue1 = MapSize - Int(TextFieldText(ZoneTrans1))
			ZoneTransValue2 = MapSize - Int(TextFieldText(ZoneTrans2))
		EndIf
		If EventSource() = OK Then ; ~ When "OK" is pressed
			ClearGadgetItems(ListBox)
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					If Instr(rt\Name, TextFieldText(TxtBox))
						AddGadgetItem(ListBox, rt\Name)
					EndIf
				EndIf
			Next
		EndIf
		If EventSource() = Clean_Txt Then
			SetGadgetText(TxtBox, "")
			ClearGadgetItems(ListBox)
			For rt.RoomTemplates = Each RoomTemplates
				If rt\MapGrid = CurrMapGrid
					AddGadgetItem(ListBox, rt\Name)
				EndIf
			Next
		EndIf
		If EventSource() = ComboBox Then
			Item = SelectedGadgetItem(ComboBox)
			If Item > -1 Then
				Name = GadgetItemText(ComboBox, Item)
				
				If Item > 0 Then
					For ev.Event = Each Event
						If ev\Name = Name Then
							SetGadgetText(Event_Desc, "Event description:" + Chr(13) + ev\Description)
							Exit
						EndIf
					Next
					SetGadgetText(Event_Prob_Label, "Event chance: " + (SliderValue(Event_Prob) + 1) + "%")
					EnableGadget(Event_Prob)
					SetSliderValue(Event_Prob, 99)
				Else
					SetGadgetText(Event_Desc, "")
					SetGadgetText(Event_Prob_Label, "")
					SetSliderValue(Event_Prob, 99)
					DisableGadget(Event_Prob)
				EndIf
			EndIf
		EndIf
		If EventSource() = ListBox Then 
			Item = SelectedGadgetItem(ListBox)
            
			Grid_SelectedX = -1
			Grid_SelectedY = -1
			ChangeGridGadget = True
			GridGadgetText = ""
            
            If Item > -1 Then
				Name = GadgetItemText(ListBox, Item)
				
				ClearGadgetItems(ComboBox)
				
				HasEvent = False
				
				Local CurrRT.RoomTemplates = Null
				
				For rt.RoomTemplates = Each RoomTemplates
					If rt\Name = Name
						For i = 0 To 5
							If rt\Events[i] <> "" Then
								InsertGadgetItem(ComboBox, 0, "[none]")
								HasEvent = True
								Exit
							EndIf
						Next
						For i = 0 To 5
							If rt\Events[i] <> "" Then
								InsertGadgetItem(ComboBox, i + 1, rt\Events[i])
							EndIf
						Next
						SetGadgetText(Room_Desc, "Room description:" + Chr(13) + rt\Description)
						CurrRT = rt
						Exit
					EndIf
				Next 
				
				If CountGadgetItems(ComboBox) > 0 Then
					If MenuChecked(Event_Default) Then
						SelectGadgetItem(ComboBox, 1)
					Else
						SelectGadgetItem(ComboBox, 0)
					EndIf 
				EndIf
				
				If (Not HasEvent) Then
					DisableGadget(ComboBox)
					SetGadgetText(Event_Desc, "")
					SetGadgetText(Event_Prob_Label, "")
					SetSliderValue(Event_Prob, 99)
					DisableGadget(Event_Prob)
				Else
					EnableGadget(ComboBox)
					If SelectedGadgetItem(ComboBox) <> 0 Then
						For ev.Event = Each Event
							If ev\Name = CurrRT\Events[0]
								SetGadgetText(Event_Desc, "Event description:" + Chr(13) + ev\Description)
								Exit
							EndIf
						Next
						SetGadgetText(Event_Prob_Label, "Event chance: 100%")
						SetSliderValue(Event_Prob, 99)
						EnableGadget(Event_Prob)
					Else
						SetGadgetText(Event_Prob_Label, "")
						SetSliderValue(Event_Prob, 99)
						DisableGadget(Event_Prob)
					EndIf
				EndIf
				Grid_SelectedX = -1
				Grid_SelectedY = -1
				ChangeGridGadget = True
				GridGadgetText = ""
			EndIf
		EndIf
		If EventSource() = Event_Prob Then
			SetGadgetText(Event_Prob_Label, "Event chance: " + (SliderValue(Event_Prob) + 1) + "%")
			If Grid_SelectedX <> -1 And Grid_SelectedY <> -1 Then
				x = Grid_SelectedX
				y = Grid_SelectedY
				MapEventProb(x, y) = Float((SliderValue(Event_Prob) + 1) / 100.0)
				If MapEvent(x, y) <> "" Then
					GridGadgetText = "Name: " + Map(x, y)\Name + Chr(13) + "Angle: " + MapAngle(x, y) + "°" + Chr(13) + "Event: " + MapEvent(x, y) + Chr(13) + "Event Chance: " + Int(MapEventProb(x, y) * 100) + "%"
				EndIf
				SetGadgetText(Grid_Room_Info, GridGadgetText)
			EndIf
		EndIf
	EndIf
Forever
End()

Const ROOM1% = 0, ROOM2% = 1, ROOM2C% = 2, ROOM3% = 3, ROOM4% = 4

Global RoomTempID%

Type RoomTemplates
	Field Shape%, Name$
	Field Description$
	Field ID%
	Field Events$[5]
	Field MapGrid% = 0
End Type 

Function CreateRoomTemplate.RoomTemplates()
	Local rt.RoomTemplates
	
	rt.RoomTemplates = New RoomTemplates
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates(File$)
	Local TemporaryString$
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			Local AddRoom% = True
			
			Select TemporaryString
				Case "room ambience", "cont1_173_intro", "dimension_106", "dimension_1499", "gate_b", "gate_a"
					;[Block]
					AddRoom = False
					;[End Block]
			End Select
			
			If AddRoom Then
				rt.RoomTemplates = CreateRoomTemplate()
				rt\Name = TemporaryString
				
				StrTemp = GetINIString(File, TemporaryString, "Shape")
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
				
				rt\Description = GetINIString(File, TemporaryString, "Descr")
				
				rt\MapGrid = 0
			EndIf
		EndIf
	Wend
	
	; ~ Forest pieces
	Local Fr_Prefix$ = "SCP-860-1 "
	
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "door"
	rt\Shape = ROOM1
	rt\Description = "FRDOOR"
	rt\MapGrid = 1
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "endroom"
	rt\Shape = ROOM1
	rt\Description = "FRENDROOM"
	rt\MapGrid = 1
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "path"
	rt\Shape = ROOM2
	rt\Description = "FRPATH"
	rt\MapGrid = 1
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "corner"
	rt\Shape = ROOM2C
	rt\Description = "FRCORNER"
	rt\MapGrid = 1
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "t-shaped path"
	rt\Shape = ROOM3
	rt\Description = "FRTSHAPE"
	rt\MapGrid = 1
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = Fr_Prefix + "4-way path"
	rt\Shape = ROOM4
	rt\Description = "FR4WAY"
	rt\MapGrid = 1
	
	; ~ Maintenance tunnel rooms
	Local MT_Prefix$ = "Maintenance tunnel "
	
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "endroom"
	rt\Shape = ROOM1
	rt\Description = "MTENDROOM"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "corridor"
	rt\Shape = ROOM2
	rt\Description = "MTCORRIDOR"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "corner"
	rt\Shape = ROOM2C
	rt\Description = "MTCORNER"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "t-shaped room"
	rt\Shape = ROOM3
	rt\Description = "MTTSHAPE"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "4-way room"
	rt\Shape = ROOM4
	rt\Description = "MT4WAY"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "elevator"
	rt\Shape = ROOM2
	rt\Description = "MTELEVATOR"
	rt\MapGrid = 2
	rt.RoomTemplates = CreateRoomTemplate()
	rt\Name = MT_Prefix + "generator room"
	rt\Shape = ROOM1
	rt\Description = "MTGENERATOR"
	rt\MapGrid = 2
	
	CloseFile(f)
End Function

Const MaxEvents% = 9

Type Event
	Field Name$
	Field Description$
	Field Room$[MaxEvents]
End Type

Function InitEvents(File$)
	Local TemporaryString$
	Local e.Event = Null
	Local StrTemp$ = "", i%
	Local f% = OpenFile(File)
	
	While (Not Eof(f))
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString, 1) = "["
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			e.Event = New Event
			e\Name = TemporaryString
			
			e\Description = GetINIString(File, TemporaryString, "Descr")
			
			For i = 0 To MaxEvents - 1
				e\Room[i] = GetINIString(File, TemporaryString, "Room" + i)
			Next
		EndIf
	Wend
	
	CloseFile(f)
End Function

Function AddEvents()
	Local rt.RoomTemplates, e.Event
	Local i%
	
	For rt.RoomTemplates = Each RoomTemplates
		For e.Event = Each Event
			For i = 0 To MaxEvents - 1
				If rt\Name = e\Room[i]
					AssignEventToRoomTemplate(rt, e)
				EndIf
			Next
		Next
	Next
End Function

Function AssignEventToRoomTemplate(rt.RoomTemplates, e.Event)
	Local i%
	
	For i = 0 To 5
		If rt\Events[i] = "" Then
			rt\Events[i] = e\Name
			Exit
		EndIf
	Next
End Function

Function EraseMap()
	Local Item%, Name$, i%
	Local x%, y%
	Local HasEvent% = False
	Local rt.RoomTemplates
	
	Grid_SelectedX = -1
	Grid_SelectedY = -1
	ChangeGridGadget = True
	GridGadgetText = ""
	
	Item = SelectedGadgetItem(ListBox)
    If Item > -1 Then
    	Name = GadgetItemText(ListBox, Item)
		For rt.RoomTemplates = Each RoomTemplates
			If rt\Name = Name Then
				For i = 0 To 5
					If rt\Events[i] <> "" Then
						HasEvent = True
					EndIf
				Next
				Exit
			EndIf
		Next 
	EndIf
	
	If (Not HasEvent) Then
		DisableGadget(ComboBox)
		SetGadgetText(Event_Desc, "")
		SetGadgetText(Event_Prob_Label, "")
		SetSliderValue(Event_Prob, 99)
		DisableGadget(Event_Prob)
	Else
		SetSliderValue(Event_Prob, 99)
		SetGadgetText(Event_Prob_Label, "Event chance: " + (SliderValue(Event_Prob) + 1) + "%")
	EndIf
	
	For x = 0 To MapSize
		For y = 0 To MapSize
			Map(x, y) = Null
			MapAngle(x, y) = 0
			MapEvent(x, y) = ""
			MapEventProb(x, y) = 0.0
		Next
	Next
	
	For x = 0 To ForestGridSize
		For y = 0 To ForestGridSize
			ForestPlace(x, y) = Null
			ForestPlaceAngle(x, y) = 0
		Next
	Next
	
	For x = 0 To MT_GridSize
		For y = 0 To MT_GridSize
			MTRoom(x, y) = Null
			MTRoomAngle(x, y) = 0
		Next
	Next
	
	ZoneTransValue1 = 13
	ZoneTransValue2 = 7
	SetGadgetText(ZoneTrans1, 5)
	SetGadgetText(ZoneTrans2, 11)
	MapAuthor = ""
	MapDescription = ""
	SetGadgetText(Map_Author_Text, "")
End Function

Function LoadMap(File$)
	EraseMap()
	
	Local f% = ReadFile(File)
	Local i%, Name$
	Local x%, y%
	Local rt.RoomTemplates
	
	If Right(File, 6) = "cbmap2" Then
		MapAuthor = ReadLine(f)
		MapDescription = ReadLine(f)
		If MapAuthor = "[Unknown]" Then MapAuthor = ""
		If MapDescription = "[No description]" Then MapDescription = ""
		SetGadgetText(Map_Author_Text, MapAuthor)
		SetGadgetText(Descr_Text, MapDescription)
		ZoneTransValue1 = ReadByte(f)
		ZoneTransValue2 = ReadByte(f)
		SetGadgetText(ZoneTrans1, (MapSize) - ZoneTransValue1)
		SetGadgetText(ZoneTrans2, (MapSize) - ZoneTransValue2)
		
		Local RoomAmount% = ReadInt(f) ; ~ Amount of rooms
		Local ForestAmount% = ReadInt(f) ; ~ Amount of forest pieces
		Local MTRoomAmount% = ReadInt(f) ; ~ Amount of maintenance tunnel rooms
		
		; ~ Facility rooms
		For i = 0 To RoomAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = ReadString(f)
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					Map(x, y) = rt
					Exit
				EndIf
			Next
			
			MapAngle(x, y) = ReadByte(f) * 90
			MapEvent(x, y) = ReadString(f)
			If MapEvent(x, y) = "" Then MapEvent(x, y) = "[none]"
			MapEventProb(x, y) = ReadFloat(f)
			If MapEventProb(x, y) = 0.0 Then MapEventProb(x, y) = 1.0
		Next
		; ~ Forest pieces
		For i = 0 To ForestAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = ReadString(f)
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					ForestPlace(x, y) = rt
					Exit
				EndIf
			Next
			ForestPlaceAngle(x, y) = ReadByte(f) * 90
		Next
		; ~ Maintenance tunnel pieces
		For i = 0 To MTRoomAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = ReadString(f)
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					MTRoom(x, y) = rt
					Exit
				EndIf
			Next
			MTRoomAngle(x, y) = ReadByte(f) * 90
		Next
	Else
		While (Not Eof(f))
			x = ReadByte(f)
			y = ReadByte(f)
			Name = ReadString(f)
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					Map(x, y) = rt
					Exit
				EndIf
			Next
			MapAngle(x, y) = ReadByte(f) * 90
			MapEvent(x, y) = ReadString(f)
			If MapEvent(x, y) = "" Then MapEvent(x, y) = "[none]"
			MapEventProb(x, y) = ReadFloat(f)
			If MapEventProb(x, y) = 0.0 Then MapEventProb(x, y) = 1.0
		Wend
	EndIf
	
	If ShowGrid = False Then
		SaveMap("CONFIG_MAPINIT.SI", True)
	EndIf
	
	CloseFile(f)
End Function

Function SaveMap(File$, StreamTopRgm% = False, Old% = 0)
	Local f% = WriteFile(File)
	Local Temp%, x%, y%
	
	If Old = 0 Then
		MapAuthor = TextFieldText(Map_Author_Text)
		If Trim(MapAuthor) = "" Then
			WriteLine(f, "[Unknown]")
		Else
			WriteLine(f, MapAuthor)
		EndIf
		MapDescription = TextAreaText(Descr_Text)
		If Trim(MapDescription) = "" Then
			WriteLine(f, "[No description]")
		Else
			WriteLine(f, MapDescription)
		EndIf
		WriteByte(f, ZoneTransValue1)
		WriteByte(f, ZoneTransValue2)
		; ~ Facility room amount
		Temp = 0
		For x = 0 To MapSize
			For y = 0 To MapSize
				If Map(x, y) <> Null Then
					Temp = Temp + 1
				EndIf
			Next
		Next
		WriteInt(f, Temp)
		; ~ Forest room amount
		Temp = 0
		For x = 0 To ForestGridSize
			For y = 0 To ForestGridSize
				If ForestPlace(x, y) <> Null Then
					Temp = Temp + 1
				EndIf
			Next
		Next
		WriteInt(f, Temp)
		; ~ Maintenance Tunnels room amount
		Temp = 0
		For x = 0 To MT_GridSize
			For y = 0 To MT_GridSize
				If MTRoom(x, y) <> Null Then
					Temp = Temp + 1
				EndIf
			Next
		Next
		WriteInt(f, Temp)
	EndIf
	
	If StreamTopRgm Then
		WriteInt(f, CurrMapGrid)
	EndIf
	
	For x = 0 To MapSize
		For y = 0 To MapSize
			If Map(x, y) <> Null
				WriteByte(f, x)
				WriteByte(f, y)
				WriteString(f, Lower(Map(x, y)\Name))
				WriteByte(f, Floor(MapAngle(x, y) / 90))
				If MapEvent(x, y) <> "[none]" Then
					WriteString(f, MapEvent(x, y))
				Else
					WriteString(f, "")
				EndIf
				WriteFloat(f, MapEventProb(x, y))
				
				If StreamTopRgm Then
					If Grid_SelectedX = x And Grid_SelectedY = y Then
						WriteByte(f, 1)
					Else
						WriteByte(f, 0)
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	If Old = 0 Then
		For x = 0 To ForestGridSize
			For y = 0 To ForestGridSize
				If ForestPlace(x, y) <> Null Then
					WriteByte(f, x)
					WriteByte(f, y)
					WriteString(f, Lower(ForestPlace(x, y)\Name))
					WriteByte(f, Floor(ForestPlaceAngle(x, y) / 90.0))
					
					If StreamTopRgm Then
						If Grid_SelectedX = x And Grid_SelectedY = y Then
							WriteByte(f, 1)
						Else
							WriteByte(f, 0)
						EndIf
					EndIf
				EndIf
			Next
		Next
		
		For x = 0 To MT_GridSize
			For y = 0 To MT_GridSize
				If MTRoom(x, y) <> Null Then
					WriteByte(f, x)
					WriteByte(f, y)
					WriteString(f, Lower(MTRoom(x, y)\Name))
					WriteByte(f, Floor(MTRoomAngle(x, y) / 90.0))
					
					If StreamTopRgm Then
						If Grid_SelectedX = x And Grid_SelectedY = y Then
							WriteByte(f, 1)
						Else
							WriteByte(f, 0)
						EndIf
					EndIf
				EndIf
			Next
		Next
	EndIf
	
	CloseFile(f)
End Function

Function WriteOptions()
	Local f% = WriteFile("CONFIG_OPTINIT.SI")
	
	WriteInt(f, opt\FogR) : WriteInt(f, opt\FogG) : WriteInt(f, opt\FogG)
	WriteInt(f, opt\CursorR) : WriteInt(f, opt\CursorG) : WriteInt(f, opt\CursorG)
	WriteInt(f, TextFieldText(CameraRangeOpt))
	WriteByte(f, ButtonState(VSync))
	WriteByte(f, ButtonState(ShowFPS))
	
	CloseFile(f)
End Function

;~IDEal Editor Parameters:
;~C#BlitzPlus