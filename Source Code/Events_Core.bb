Type Events
	Field EventID%
	Field room.Rooms
	Field EventState#, EventState2#, EventState3#, EventState4#
	Field SoundCHN%, SoundCHN2%
	Field Sound%, Sound2%
	Field BlindsCHN%
	Field SoundCHN_IsStream%, SoundCHN2_IsStream%
	Field EventStr$
	Field Img%, Img2%
End Type

Global forest_event.Events
Global skull_event.Events
Global PD_event.Events
Global scribe_event.Events

; ~ Events ID Constants
;[Block]
; ~ LCZ
Const e_room1_dead_end_lcz_106% = 0
Const e_room1_storage% = 1
Const e_cont1_005% = 2
Const e_cont1_173% = 3, e_cont1_173_intro% = 4
Const e_cont1_205% = 5
Const e_cont1_914% = 6
Const e_room2_2_lcz_fan% = 7
Const e_room2_closets% = 8
Const e_room2_elevator% = 9
Const e_room2_gw_2% = 10
Const e_room2_storage% = 11
Const e_room2_sl% = 12
Const e_room2_test_lcz_173% = 13
Const e_cont2_012% = 14
Const e_cont2_500_1499% = 15
Const e_cont2_1123% = 16
Const e_cont2c_066_1162_arc% = 17
Const e_room3_storage% = 18
Const e_cont3_372% = 19
Const e_room4_ic% = 20
; ~ HCZ
Const e_cont1_035% = 21
Const e_cont1_079% = 22
Const e_cont1_106% = 23
Const e_cont1_895% = 24
Const e_room2_2_hcz_106% = 25
Const e_room2_4_hcz% = 26
Const e_room2_5_hcz_106% = 27
Const e_room2_6_hcz_smoke% = 28, e_room2_6_hcz_173% = 29
Const e_room2_mt% = 30
Const e_room2_nuke% = 31
Const e_room2_servers_hcz% = 32
Const e_room2_shaft% = 33
Const e_room2_test_hcz% = 34
Const e_cont2_008% = 35
Const e_cont2_049% = 36
Const e_cont2_409% = 37
Const e_room3_hcz_1048% = 38, e_room3_hcz_duck% = 39
Const e_room3_2_hcz_guard% = 40
Const e_cont3_009% = 41
Const e_cont3_966% = 42
Const e_room4_2_hcz_d% = 43
; ~ EZ
Const e_gate_a_entrance% = 44, e_gate_a% = 45
Const e_gate_b_entrance% = 46, e_gate_b% = 47
Const e_room1_dead_end_ez_guard% = 48
Const e_room2_ez_035% = 49
Const e_room2_2_ez_duck% = 50
Const e_room2_6_ez_guard% = 51
Const e_room2_office% = 52
Const e_room2_office_3% = 53
Const e_room2_cafeteria% = 54
Const e_room2_ic% = 55
Const e_room2_medibay% = 56
Const e_room2_scientists_2% = 57
Const e_cont2_860_1% = 58
Const e_room2c_ec% = 59
Const e_room3_2_ez_duck% = 60
; ~ OTHERS
Const e_096_spawn% = 61
Const e_106_sinkhole% = 62
Const e_106_victim% = 63, e_106_victim_wall% = 64
Const e_173_spawn% = 65
Const e_682_roar% = 66
Const e_toilets_789_j% = 67
Const e_1048_a% = 68
Const e_brownout% = 69
Const e_checkpoint% = 70
Const e_door_closing% = 71
Const e_gateway% = 72
Const e_tesla% = 73, e_broken_tesla% = 74
Const e_trick% = 75, e_trick_item% = 76
Const e_dimension_106% = 77, e_dimension_1499% = 78
;[End Block]

; ~ For Map Creator
Function FindEventID%(EventName$)
	Select Lower(EventName)
		Case "room1_dead_end_106"
			;[Block]
			Return(e_room1_dead_end_lcz_106)
			;[End Block]
		Case "room1_storage"
			;[Block]
			Return(e_room1_storage)
			;[End Block]
		Case "cont1_005"
			;[Block]
			Return(e_cont1_005)
			;[End Block]
		Case "cont1_173"
			;[Block]
			Return(e_cont1_173)
			;[End Block]
		Case "cont1_173_intro"
			;[Block]
			Return(e_cont1_173_intro)
			;[End Block]
		Case "cont1_205"
			;[Block]
			Return(e_cont1_205)
			;[End Block]
		Case "cont1_914"
			;[Block]
			Return(e_cont1_914)
			;[End Block]
		Case "room2_2_lcz_fan"
			;[Block]
			Return(e_room2_2_lcz_fan)
			;[End Block]
		Case "room2_gw_2"
			;[Block]
			Return(e_room2_gw_2)
			;[End Block]
		Case "room2_closets"
			;[Block]
			Return(e_room2_closets)
			;[End Block]
		Case "room2_elevator"
			;[Block]
			Return(e_room2_elevator)
			;[End Block]
		Case "room2_sl"
			;[Block]
			Return(e_room2_sl)
			;[End Block]
		Case "room2_storage"
			;[Block]
			Return(e_room2_storage)
			;[End Block]
		Case "room2_test_lcz_173"
			;[Block]
			Return(e_room2_test_lcz_173)
			;[End Block]
		Case "cont2_012"
			;[Block]
			Return(e_cont2_012)
			;[End Block]
		Case "cont2_500_1499"
			;[Block]
			Return(e_cont2_500_1499)
			;[End Block]
		Case "cont2_1123"
			;[Block]
			Return(e_cont2_1123)
			;[End Block]
		Case "cont2c_066_1162_arc"
			;[Block]
			Return(e_cont2c_066_1162_arc)
			;[End Block]
		Case "room3_storage"
			;[Block]
			Return(e_room3_storage)
			;[End Block]
		Case "cont3_372"
			;[Block]
			Return(e_cont3_372)
			;[End Block]
		Case "room4_ic"
			;[Block]
			Return(e_room4_ic)
			;[End Block]
		Case "cont1_035"
			;[Block]
			Return(e_cont1_035)
			;[End Block]
		Case "cont1_079"
			;[Block]
			Return(e_cont1_079)
			;[End Block]
		Case "cont1_106"
			;[Block]
			Return(e_cont1_106)
			;[End Block]
		Case "cont1_895"
			;[Block]
			Return(e_cont1_895)
			;[End Block]
		Case "room2_2_hcz_106"
			;[Block]
			Return(e_room2_2_hcz_106)
			;[End Block]
		Case "room2_4_hcz"
			;[Block]
			Return(e_room2_4_hcz)
			;[End Block]
		Case "room2_5_hcz_106"
			;[Block]
			Return(e_room2_5_hcz_106)
			;[End Block]
		Case "room2_6_hcz_smoke"
			;[Block]
			Return(e_room2_6_hcz_smoke)
			;[End Block]
		Case "room2_6_hcz_173"
			;[Block]
			Return(e_room2_6_hcz_173)
			;[End Block]
		Case "room2_mt"
			;[Block]
			Return(e_room2_mt)
			;[End Block]
		Case "room2_nuke"
			;[Block]
			Return(e_room2_nuke)
			;[End Block]
		Case "room2_shaft"
			;[Block]
			Return(e_room2_shaft)
			;[End Block]
		Case "room2_servers_hcz"
			;[Block]
			Return(e_room2_servers_hcz)
			;[End Block]
		Case "room2_test_hcz"
			;[Block]
			Return(e_room2_test_hcz)
			;[End Block]
		Case "cont2_008"
			;[Block]
			Return(e_cont2_008)
			;[End Block]
		Case "cont2_049"
			;[Block]
			Return(e_cont2_049)
			;[End Block]
		Case "cont2_409"
			;[Block]
			Return(e_cont2_409)
			;[End Block]
		Case "room3_hcz_duck"
			;[Block]
			Return(e_room3_hcz_duck)
			;[End Block]
		Case "room3_hcz_1048"
			;[Block]
			Return(e_room3_hcz_1048)
			;[End Block]
		Case "room3_2_hcz_guard"
			;[Block]
			Return(e_room3_2_hcz_guard)
			;[End Block]
		Case "cont3_009"
			;[Block]
			Return(e_cont3_009)
			;[End Block]
		Case "cont3_966"
			;[Block]
			Return(e_cont3_966)
			;[End Block]
		Case "room4_2_hcz_d"
			;[Block]
			Return(e_room4_2_hcz_d)
			;[End Block]
		Case "room1_dead_end_guard"
			;[Block]
			Return(e_room1_dead_end_ez_guard)
			;[End Block]
		Case "gate_a_entrance"
			;[Block]
			Return(e_gate_a_entrance)
			;[End Block]
		Case "gate_a"
			;[Block]
			Return(e_gate_a)
			;[End Block]
		Case "gate_b_entrance"
			;[Block]
			Return(e_gate_b_entrance)
			;[End Block]
		Case "gate_b"
			;[Block]
			Return(e_gate_b)
			;[End Block]
		Case "room2_6_ez_guard"
			;[Block]
			Return(e_room2_6_ez_guard)
			;[End Block]
		Case "room2_office"
			;[Block]
			Return(e_room2_office)
			;[End Block]
		Case "room2_office_3"
			;[Block]
			Return(e_room2_office_3)
			;[End Block]
		Case "room2_cafeteria"
			;[Block]
			Return(e_room2_cafeteria)
			;[End Block]
		Case "room2c_ec"
			;[Block]
			Return(e_room2c_ec)
			;[End Block]
		Case "room2_ic"
			;[Block]
			Return(e_room2_ic)
			;[End Block]
		Case "room2_ez_035"
			;[Block]
			Return(e_room2_ez_035)
			;[End Block]
		Case "room2_2_ez_duck"
			;[Block]
			Return(e_room2_2_ez_duck)
			;[End Block]
		Case "room2_medibay"
			;[Block]
			Return(e_room2_medibay)
			;[End Block]
		Case "room2_scientists_2"
			;[Block]
			Return(e_room2_scientists_2)
			;[End Block]
		Case "cont2_860_1"
			;[Block]
			Return(e_cont2_860_1)
			;[End Block]
		Case "room3_2_ez_duck"
			;[Block]
			Return(e_room3_2_ez_duck)
			;[End Block]
		Case "096_spawn"
			;[Block]
			Return(e_096_spawn)
			;[End Block]
		Case "106_victim"
			;[Block]
			Return(e_106_victim)
			;[End Block]
		Case "106_victim_wall"
			;[Block]
			Return(e_106_victim_wall)
			;[End Block]
		Case "106_sinkhole"
			;[Block]
			Return(e_106_sinkhole)
			;[End Block]
		Case "173_spawn"
			;[Block]
			Return(e_173_spawn)
			;[End Block]
		Case "682_roar"
			;[Block]
			Return(e_682_roar)
			;[End Block]
		Case "toilets_789_j"
			;[Block]
			Return(e_toilets_789_j)
			;[End Block]
		Case "1048_a"
			;[Block]
			Return(e_1048_a)
			;[End Block]
		Case "brownout"
			;[Block]
			Return(e_brownout)
			;[End Block]
		Case "checkpoint"
			;[Block]
			Return(e_checkpoint)
			;[End Block]
		Case "door_closing"
			;[Block]
			Return(e_door_closing)
			;[End Block]
		Case "gateway"
			;[Block]
			Return(e_gateway)
			;[End Block]
		Case "tesla"
			;[Block]
			Return(e_tesla)
			;[End Block]
		Case "broken_tesla"
			;[Block]
			Return(e_broken_tesla)
			;[End Block]
		Case "trick"
			;[Block]
			Return(e_trick)
			;[End Block]
		Case "trick_item"
			;[Block]
			Return(e_trick_item)
			;[End Block]
		Case "dimension_106"
			;[Block]
			Return(e_dimension_106)
			;[End Block]
		Case "dimension_1499"
			;[Block]
			Return(e_dimension_1499)
			;[End Block]
		Default
			;[Block]
			Return(-1)
			;[End Block]
	End Select
End Function

Function FindEventVariable%(e.Events)
	Select e\EventID
		Case e_cont2_012
			;[Block]
			scribe_event = e
			;[End Block]
		Case e_cont2_1123
			;[Block]
			skull_event = e
			;[End Block]
		Case e_cont2_860_1
			;[Block]
			forest_event = e
			;[End Block]
		Case e_dimension_106
			;[Block]
			PD_event = e
			;[End Block]
	End Select
End Function

Function CreateEvent.Events(EventID%, RoomID%, ID%, Prob# = 0.0)
	; ~ RoomName = the name of the room(s) you want the event to be assigned to
	
	; ~ The ID-variable determines which of the rooms the event is assigned to,
	; ~ 0 will assign it to the first generated room, 1 to the second, etc.
	
	; ~ The prob-variable can be used to randomly assign events into some rooms
	; ~ 0.5 means that there's a 50% chance that event is assigned to the rooms
	; ~ 1.0 means that the event is assigned to every room
	; ~ The ID-variable is ignored if prob <> 0.0
	
	Local e.Events, e2.Events, r.Rooms
	Local i% = 0, Temp%
	
	If Prob = 0.0
		For r.Rooms = Each Rooms
			If RoomID = r\RoomTemplate\RoomID
				Temp = False
				For e2.Events = Each Events
					If e2\room = r
						Temp = True
						Exit
					EndIf
				Next
				
				i = i + 1
				If i >= ID And (Not Temp)
					e.Events = New Events
					e\EventID = EventID
					FindEventVariable(e)
					e\room = r
					Return(e)
				EndIf
			EndIf
		Next
	Else
		For r.Rooms = Each Rooms
			If RoomID = r\RoomTemplate\RoomID
				Temp = False
				For e2.Events = Each Events
					If e2\room = r
						Temp = True
						Exit
					EndIf
				Next
				
				If Rnd(0.0, 1.0) <= Prob And (Not Temp)
					e.Events = New Events
					e\EventID = EventID
					FindEventVariable(e)
					e\room = r
				EndIf
			EndIf
		Next
	EndIf
	Return(Null)
End Function

Global QuickLoadPercent% 
Global QuickLoadPercent_DisplayTimer#
Global QuickLoad_CurrEvent.Events

Function UpdateQuickLoading%()
	If QuickLoadPercent > -1
		If QuickLoadPercent > 99
			If QuickLoadPercent_DisplayTimer < 70.0
				QuickLoadPercent_DisplayTimer = Min(QuickLoadPercent_DisplayTimer + fps\Factor[0], 70.0)
			Else
				QuickLoadPercent = -1
			EndIf
		EndIf
		QuickLoadEvents()
	Else
		QuickLoadPercent = -1
		QuickLoadPercent_DisplayTimer = 0.0
		QuickLoad_CurrEvent = Null
	EndIf
End Function

Function RenderQuickLoading%()
	If QuickLoadPercent > -1 And opt\HUDEnabled
		Local CoordEx% = 90 * MenuScale
		
		MidHandle(t\IconID[9])
		DrawImage(t\IconID[9], opt\GraphicWidth - CoordEx, opt\GraphicHeight - (150 * MenuScale))
		Color(255, 255, 255)
		SetFontEx(fo\FontID[Font_Default])
		TextEx(opt\GraphicWidth - (100 * MenuScale), opt\GraphicHeight - CoordEx, Format(GetLocalString("loading", "loading"), QuickLoadPercent), True)
	EndIf
End Function

Include "Source Code\Event_Scripts_Core.bb"

Function QuickLoadEvents%() ; ~ Get rid of this shit - Jabka
	If QuickLoad_CurrEvent = Null
		QuickLoadPercent = -1
		Return
	EndIf
	
	CatchErrors("QuickLoadEvents()")
	
	Local e.Events = QuickLoad_CurrEvent
	Local r.Rooms, sc.SecurityCams, sc2.SecurityCams, n.NPCs
	Local i%, x#, y#, z#
	
	; ~ Might be a good idea to use QuickLoadPercent to determine the "steps" of the loading process 
	; ~ Instead of magic values in e\EventState and e\EventStr
	
	Select e\EventID
		Case e_cont1_205
			;[Block]
			If e\EventState = 0.0 Lor e\EventStr <> "LoadDone"
				Select e\EventStr
					Case "Load0"
						;[Block]
						e\room\Objects[2] = CopyEntity(n_I\NPCModelID[NPC_205_DEMON_1_MODEL])
						e\room\ScriptedObject[2] = True
						QuickLoadPercent = 15
						e\EventStr = "Load1"
						;[End Block]
					Case "Load1"
						;[Block]
						e\room\Objects[3] = CopyEntity(n_I\NPCModelID[NPC_205_DEMON_2_MODEL])
						e\room\ScriptedObject[3] = True
						QuickLoadPercent = 30
						e\EventStr = "Load2"
						;[End Block]
					Case "Load2"
						;[Block]
						e\room\Objects[4] = CopyEntity(n_I\NPCModelID[NPC_205_DEMON_3_MODEL])
						e\room\ScriptedObject[4] = True
						QuickLoadPercent = 45
						e\EventStr = "Load3"
						;[End Block]
					Case "Load3"
						;[Block]
						e\room\Objects[5] = CopyEntity(n_I\NPCModelID[NPC_205_WOMAN_MODEL])
						e\room\ScriptedObject[5] = True
						QuickLoadPercent = 60
						e\EventStr = "Load4"
						;[End Block]
					Case "Load4"
						;[Block]
						For i = 2 To 5
							PositionEntity(e\room\Objects[i], EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), True)
							RotateEntity(e\room\Objects[i], -90.0, EntityYaw(e\room\Objects[0], True), 0.0, True)
							ScaleEntity(e\room\Objects[i], 0.05, 0.05, 0.05, True)
						Next
						QuickLoadPercent = 75
						e\EventStr = "Load5"
						;[End Block]
					Case "Load5"
						;[Block]
						For i = 2 To 5
							HideEntity(e\room\Objects[i])
						Next
						QuickLoadPercent = 100
						e\EventStr = "LoadDone"
						;[End Block]
				End Select
			EndIf
			;[End Block]
		Case e_dimension_1499
			;[Block]
			If e\EventState = 0.0
				If e\EventStr = "Load0"
					QuickLoadPercent = 10
					e\room\Objects[0] = LoadMesh_Strict("GFX\Map\dimension1499\1499plane.b3d")
					HideEntity(e\room\Objects[0])
					e\room\ScriptedObject[0] = True
					
					CreateConsoleMsg("")
					CreateConsoleMsg(GetLocalString("misc", "warning2"), 255, 0, 0)
					CreateConsoleMsg("")
					
					e\EventStr = "Load1"
				ElseIf e\EventStr = "Load1"
					QuickLoadPercent = 30
					I_1499\Sky = CreateSky("GFX\Map\Textures\1499sky")
					e\EventStr = 1
				Else
					Local EventStrInt% = Int(e\EventStr)
					
					If EventStrInt < 16
						QuickLoadPercent = QuickLoadPercent + 2
						e\room\Objects[EventStrInt] = LoadRMesh("GFX\Map\dimension1499\dimension_1499_object(" + (EventStrInt) + ").rmesh", Null, False)
						ScaleEntity(e\room\Objects[EventStrInt], RoomScale, RoomScale, RoomScale)
						HideEntity(e\room\Objects[EventStrInt])
						e\EventStr = EventStrInt + 1
					ElseIf EventStrInt = 16
						QuickLoadPercent = 90
						CreateChunkParts(e\room)
						e\EventStr = 17
					ElseIf EventStrInt = 17
						QuickLoadPercent = 100
						
						x = EntityX(e\room\OBJ)
						y = EntityY(e\room\OBJ)
						z = EntityZ(e\room\OBJ)
						
						Local ch.Chunk
						
						For i = -2 To 0 Step 2
							ch.Chunk = CreateChunk(-1, x * (i * 2.5), y, z, True)
							ch.Chunk = CreateChunk(-1, x * (i * 2.5), y, z - 40.0, True)
						Next
						
						e\EventState = 2.0
						e\EventStr = 18
					EndIf
				EndIf
			EndIf
			;[End Block]
	End Select
	
	CatchErrors("Uncaught: QuickLoadEvents(Event Name: ID: " + e\EventID + ")")
End Function

Function UpdateEvents%()
	CatchErrors("UpdateEvents()")
	
	Local e.Events
	
	For e.Events = Each Events
		Select e\EventID
			Case e_room1_dead_end_lcz_106
				;[Block]
				UpdateEvent_Room1_Dead_End_LCZ_106(e)
				;[End Block]
			Case e_room1_storage
				;[Block]
				UpdateEvent_Room1_Storage(e)
				;[End Block]
			Case e_cont1_005
				;[Block]
				UpdateEvent_Cont1_005(e)
				;[End Block]
			Case e_cont1_173
				;[Block]
				UpdateEvent_Cont1_173(e)
				;[End Block]
			Case e_cont1_205
				;[Block]
				UpdateEvent_Cont1_205(e)
				;[End block]
			Case e_cont1_914
				;[Block]
				UpdateEvent_Cont1_914(e)
				;[End Block]
			Case e_room2_2_lcz_fan
				;[Block]
				UpdateEvent_Room2_2_LCZ_Fan(e)
				;[End Block]
			Case e_room2_closets
				;[Block]
				UpdateEvent_Room2_Closets(e)
				;[End Block]
			Case e_room2_elevator
				;[Block]
				UpdateEvent_Room2_Elevator(e)
				;[End Block]
			Case e_room2_gw_2
				;[Block]
				UpdateEvent_Room2_GW_2(e)
				;[End Block]
			Case e_room2_sl
				;[Block]
				UpdateEvent_Room2_SL(e)
				;[End Block]
			Case e_room2_storage
				;[Block]
				UpdateEvent_Room2_Storage(e)
				;[End Block]
			Case e_room2_test_lcz_173
				;[Block]
				UpdateEvent_Room2_Test_LCZ_173(e)
				;[End Block]
			Case e_cont2_012
				;[Block]
				UpdateEvent_Cont2_012(e)
				;[End Blck]
			Case e_cont2_500_1499
				;[Block]
				UpdateEvent_Cont2_500_1499(e)
				;[End Block]
			Case e_cont2_1123
				;[Block]
				UpdateEvent_Cont2_1123(e)
				;[End Block]
			Case e_cont2c_066_1162_arc
				;[Block]
				UpdateEvent_Cont2C_066_1162_ARC%(e)
				;[End Block]
			Case e_room3_storage
				;[Block]
				UpdateEvent_Room3_Storage(e)
				;[End Block]
			Case e_cont3_372
				;[Block]
				UpdateEvent_Cont3_372(e)
				;[End Block]
			Case e_room4_ic
				;[Block]
				UpdateEvent_Room4_IC(e)
				;[End Block]
			Case e_cont1_035
				;[Block]
				UpdateEvent_Cont1_035(e)
				;[End Block]
			Case e_cont1_079
				;[Block]
				UpdateEvent_Cont1_079(e)
				;[End Block]
			Case e_cont1_106
				;[Block]
				UpdateEvent_Cont1_106(e)
				;[End Block]
			Case e_cont1_895
				;[Block]
				UpdateEvent_Cont1_895(e)
				;[End Block]
			Case e_room2_2_hcz_106
				;[Block]
				UpdateEvent_Room2_2_HCZ_106(e)
				;[End Block]
			Case e_room2_4_hcz
				;[Block]
				UpdateEvent_Room2_4_HCZ(e)
				;[End Block]
			Case e_room2_5_hcz_106
				;[Block]
				UpdateEvent_Room2_5_HCZ_106(e)
				;[End Block]
			Case e_room2_6_hcz_173
				;[Block]
				UpdateEvent_Room2_6_HCZ_173(e)
				;[End Block]
			Case e_room2_6_hcz_smoke
				;[Block]
				UpdateEvent_Room2_6_HCZ_Smoke(e)
				;[End Block]
			Case e_room2_mt
				;[Block]
				UpdateEvent_Room2_MT(e)
				;[End Block]
			Case e_room2_nuke
				;[Block]
				UpdateEvent_Room2_Nuke(e)
				;[End Block]
			Case e_room2_servers_hcz
				;[Block]
				UpdateEvent_Room2_Servers_HCZ(e)
				;[End Block]
			Case e_room2_shaft
				;[Block]
				UpdateEvent_Room2_Shaft(e)
				;[End Block]
			Case e_room2_test_hcz
				;[Block]
				UpdateEvent_Room2_Test_HCZ(e)
				;[End Block]
			Case e_cont2_008
				;[Block]
				UpdateEvent_Cont2_008(e)
				;[End Block]
			Case e_cont2_049
				;[Block]
				UpdateEvent_Cont2_049(e)
				;[End Block]
			Case e_cont2_409
				;[Block]
				UpdateEvent_Cont2_409(e)
				;[End Block]
			Case e_room3_hcz_1048
				;[Block]
				UpdateEvent_Room3_HCZ_1048(e)
				;[End Block]
			Case e_room3_hcz_duck
				;[Block]
				UpdateEvent_Room3_HCZ_Duck(e)
				;[End Block]
			Case e_room3_2_hcz_guard
				;[Block]
				UpdateEvent_Room3_2_HCZ_Guard(e)
				;[End Block]
			Case e_cont3_009
				;[Block]
				UpdateEvent_Cont3_009(e)
				;[End Block]
			Case e_cont3_966
				;[Block]
				UpdateEvent_Cont3_966(e)
				;[End Block]
			Case e_room4_2_hcz_d
				;[Block]
				UpdateEvent_Room4_2_HCZ_D(e)
				;[End Block]
			Case e_room1_dead_end_ez_guard
				;[Block]
				UpdateEvent_Room1_Dead_End_EZ_Guard(e)
				;[End Block]
			Case e_gate_a_entrance
				;[Block]
				UpdateEvent_Gate_A_Entrance(e)
				;[End Block]
			Case e_gate_b_entrance
				;[Block]
				UpdateEvent_Gate_B_Entrance(e)
				;[End Block]
			Case e_room2_ez_035
				;[Block]
				UpdateEvent_Room2_EZ_035(e)
				;[End Block]
			Case e_toilets_789_j
				;[Block]
				UpdateEvent_Toilets_789_J(e)
				;[End Block]
			Case e_room2_2_ez_duck
				;[Block]
				UpdateEvent_Room2_2_EZ_Duck(e)
				;[End Block]
			Case e_room2_6_ez_guard
				;[Block]
				UpdateEvent_Room2_6_EZ_Guard(e)
				;[End Block]
			Case e_room2_cafeteria
				;[Block]
				UpdateEvent_Room2_Cafeteria(e)
				;[End Block]
			Case e_room2_ic
				;[Block]
				UpdateEvent_Room2_IC(e)
				;[End Block]
			Case e_room2_medibay
				;[Block]
				UpdateEvent_Room2_Medibay(e)
				;[End Block]
			Case e_room2_office
				;[Block]
				UpdateEvent_Room2_Office(e)
				;[End Block]
			Case e_room2_office_3
				;[Block]
				UpdateEvent_Room2_Office_3(e)
				;[End Block]
			Case e_room2_scientists_2
				;[Block]
				UpdateEvent_Room2_Scientists_2(e)
				;[End Block]
			Case e_cont2_860_1
				;[Block]
				UpdateEvent_Cont2_860_1(e)
				;[End Block]
			Case e_room2c_ec
				;[Block]
				UpdateEvent_Room2C_EC(e)
				;[End Block]
			Case e_room3_2_ez_duck
				;[Block]
				UpdateEvent_Room3_2_EZ_Duck(e)
				;[End Block]
			Case e_dimension_1499
				;[Block]
				UpdateEvent2_Dimension_1499(e)
				;[End Block]
			Case e_096_spawn
				;[Block]
				UpdateEvent_096_Spawn(e)
				;[End Block]
			Case e_106_sinkhole
				;[Block]
				UpdateEvent_106_Sinkhole(e)
				;[End Block]
			Case e_106_victim
				;[Block]
				UpdateEvent_106_Victim(e)
				;[End Block]
			Case e_106_victim_wall
				;[Block]
				UpdateEvent_106_Victim_Wall(e)
				;[End Block]
			Case e_173_spawn
				;[Block]
				UpdateEvent_173_Spawn(e)
				;[End Block]
			Case e_682_roar
				;[Block]
				UpdateEvent_682_Roar(e)
				;[End Block]
			Case e_1048_a
				;[Block]
				UpdateEvent_1048_A(e)
				;[End Block]
			Case e_brownout
				;[Block]
				UpdateEvent_Brownout(e)
				;[End Block]
			Case e_checkpoint
				;[Block]
				UpdateEvent_Checkpoint(e)
				;[End Block]
			Case e_door_closing
				;[Block]
				UpdateEvent_Door_Closing(e)
				;[End Block]
			Case e_gateway
				;[Block]
				UpdateEvent_Gateway(e)
				;[End Block]
			Case e_tesla
				;[Block]
				UpdateEvent_Tesla(e)
				;[End Block]
			Case e_broken_tesla
				;[Block]
				UpdateEvent_Broken_Tesla(e)
				;[End Block]
			Case e_trick
				;[Block]
				UpdateEvent_Trick(e)
				;[End Block]
			Case e_trick_item
				;[Block]
				UpdateEvent_Trick_Item(e)
				;[End Block]
		End Select
		
		If e <> Null
			CatchErrors("Uncaught: UpdateEvents(Event ID: " + e\EventID + ")")
		Else
			CatchErrors("Uncaught: UpdateEvents(Event doesn't exist anymore!)")
		EndIf
	Next
	
	UpdateExplosion()
End Function

Function UpdateIntro%()
	Local e.Events
	
	For e.Events = Each Events
		If e\EventID = e_cont1_173_intro
			UpdateEvent_Cont1_173_Intro(e)
			Exit
		EndIf
	Next
End Function

Function UpdateEndings%()
	Local e.Events
	
	For e.Events = Each Events
		If e\EventID = e_gate_a
			UpdateEvent_Gate_A(e)
		ElseIf e\EventID = e_gate_b
			UpdateEvent_Gate_B(e)
		ElseIf e\EventID = e_dimension_1499
			UpdateEvent2_Dimension_1499(e)
		EndIf
	Next
	
	UpdateExplosion()
End Function

Function UpdateDimension106%()
	Local e.Events
	
	For e.Events = Each Events
		If e\EventID = e_dimension_106
			UpdateEvent_Dimension_106(e)
		ElseIf e\EventID = e_dimension_1499
			UpdateEvent2_Dimension_1499(e)
		EndIf
	Next
End Function

Function UpdateDimension1499%()
	Local e.Events
	
	For e.Events = Each Events
		If e\EventID = e_dimension_1499
			UpdateEvent_Dimension_1499(e)
			Exit
		EndIf
	Next
End Function

Function RemoveEvent%(e.Events)
	If e\SoundCHN_IsStream
		If e\SoundCHN <> 0 Then StopStream_Strict(e\SoundCHN) : e\SoundCHN_IsStream = False
	Else
		StopChannel(e\SoundCHN)
	EndIf
	e\SoundCHN = 0
	If e\SoundCHN2_IsStream
		If e\SoundCHN2 <> 0 Then StopStream_Strict(e\SoundCHN2) : e\SoundCHN2_IsStream = False
	Else
		StopChannel(e\SoundCHN2)
	EndIf
	e\SoundCHN2 = 0
	If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
	If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
	
	If e\Img <> 0 Then FreeImage(e\Img) : e\Img = 0
	If e\Img2 <> 0 Then FreeImage(e\Img2) : e\Img2 = 0
	
	Delete(e)
End Function

Function Update035Label%(OBJ%)
	Local itt.ItemTemplates, it.Items
	Local Tex%, i%, LabelPath$, CurrTex$
	Local SF%, b%, Brush%, t1%, TexName$
	Local SurfCount% = CountSurfaces(OBJ)
	
	If I_035\Sad
		CurrTex = "035_sad"
	Else
		CurrTex = "035_smile"
	EndIf
	LabelPath = "GFX\Map\Textures\label" + CurrTex + ".png"
	
	Tex = LoadTexture_Strict(LabelPath)
	EntityTexture(OBJ, Tex)
	DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	
	For itt.ItemTemplates = Each ItemTemplates
		If itt\Name = "Document SCP-035"
			If itt\Img <> 0 Then FreeImage(itt\Img) : itt\Img = 0
			itt\ImgPath = ItemHUDTexturePath + "doc_" + CurrTex + ".png"
			itt\Img = ResizeImageEx(LoadImage_Strict(itt\ImgPath), MenuScale, MenuScale)
			itt\ImgWidth = ImageWidth(itt\Img) / 2
			itt\ImgHeight = ImageHeight(itt\Img) / 2
			itt\TexPath = itt\ImgPath
			
			For it.Items = Each Items
				If it\ItemTemplate\Name = itt\Name
					Tex = GetRescaledTexture(False, itt\TexPath, 1, DeleteMapTextures, 145, 204)
					EntityTexture(it\OBJ, Tex)
					DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
					Exit
				EndIf
			Next
		EndIf
	Next
	
	FreeImage(S2IMapGet(AchievementsImages, "035"))
	S2IMapSet(AchievementsImages, "035", ResizeImageEx(LoadImage_Strict("GFX\Menu\achievements\Achv" + CurrTex + ".png"), MenuScale, MenuScale))
End Function

Function UpdateForest%()
	Local r.Rooms
	
	For r.Rooms = Each Rooms
		HideRoomsNoColl(r)
	Next
	ShowRoomsNoColl(forest_event\room)
	ShowRoomsColl(forest_event\room)
	
	Local tX%, tY%
	Local HideDist# = PowTwo(fog\HideDistance * 2.0)
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)] <> 0
				If DistanceSquared(EntityX(me\Collider, True), EntityX(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)], True), EntityZ(me\Collider, True), EntityZ(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)], True)) < HideDist
					If EntityHidden(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)]) Then ShowEntity(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)])
				Else
					If (Not EntityHidden(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)])) Then HideEntity(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	If Rand(10 - (7 * (me\BigCameraShake > 0.0))) = 1 Then SetEmitter(Null, EntityX(me\Collider), EntityY(me\Collider), EntityZ(me\Collider), 24)
	
	If forest_event\room\NPC[0] = Null And forest_event\EventState4 = 0.0 Then forest_event\room\NPC[0] = CreateNPC(NPCType860_2, 0.0, 0.0, 0.0)
	me\CurrCameraZoom = Max(me\CurrCameraZoom, (Sin(Float(MilliSec) / 20.0) + 1.0) * 5.0)
	If forest_event\EventState4 = 1.0
		ShouldPlay = 33
		If Rand(100) = 1
			me\CameraShake = 0.7
			me\HeartBeatVolume = 0.7
			me\HeartBeatRate = Rnd(60, 70)
		EndIf
	Else
		ShouldPlay = 9
	EndIf
	If forest_event\room\NPC[0] <> Null
		If (forest_event\room\NPC[0]\State2 = 1.0 And forest_event\room\NPC[0]\State > 1.0) Lor forest_event\room\NPC[0]\State > 2.0 Then ShouldPlay = 12 ; ~ The monster is chasing the player
	EndIf
	
	; ~ The player fell
	If (Not chs\NoClip)
		If EntityY(me\Collider) <= 28.5 
			me\BlinkTimer = -10.0
			Kill() 
		ElseIf EntityY(me\Collider) > EntityY(forest_event\room\fr\Forest_Pivot, True) + 0.5
			MoveEntity(me\Collider, 0.0, ((EntityY(forest_event\room\fr\Forest_Pivot, True) + 0.5) - EntityY(me\Collider)) * fps\Factor[0], 0.0)
		EndIf
	EndIf
	
	If forest_event\room\NPC[0] <> Null 
		If forest_event\room\NPC[0]\State = 0.0 Lor EntityDistanceSquared(me\Collider, forest_event\room\NPC[0]\Collider) > HideDist
			forest_event\EventState3 = forest_event\EventState3 + fps\Factor[0] * (0.75 + (3.75 * (me\CurrSpeed > 0.02)))
			If forest_event\EventState3 > 7000.0 - (2000.0 * SelectedDifficulty\AggressiveNPCs)
				forest_event\room\NPC[0]\State = 2.0
				PositionEntity(forest_event\room\NPC[0]\Collider, 0.0, -110.0, 0.0)
			ElseIf Rnd(40000 + (3000.0 * SelectedDifficulty\AggressiveNPCs)) < forest_event\EventState3 And (forest_event\EventState3 Mod 500.0) > 490.0
				forest_event\room\NPC[0]\State = 1.0
				PositionEntity(forest_event\room\NPC[0]\Collider, 0.0, -110.0, 0.0)
			EndIf
		EndIf
	EndIf
	
	If forest_event\room\fr\ForestDoors[0]\Open Lor forest_event\room\fr\ForestDoors[1]\Open
		me\BlinkTimer = -10.0
		
		PositionEntity(me\Collider, EntityX(forest_event\room\RoomDoors[0]\FrameOBJ, True), 0.5, EntityZ(forest_event\room\RoomDoors[0]\FrameOBJ, True))
		
		RotateEntity(me\Collider, 0.0, EntityYaw(forest_event\room\OBJ, True) + forest_event\EventState2 * 180.0, 0.0)
		MoveEntity(me\Collider, 0.0, 0.0, 1.5)
		
		ResetEntity(me\Collider)
		
		ResetRender()
		
		IsBlackOut = PrevIsBlackOut
		If wi\NightVision > 0
			fog\FarDist = 15.0
		ElseIf wi\SCRAMBLE > 0
			fog\FarDist = 9.0
		Else
			fog\FarDist = 6.0
		EndIf
		
		ClearFogColor()
		
		For tX = 0 To ForestGridSize - 1
			For tY = 0 To ForestGridSize - 1
				If forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)] <> 0 Then HideEntity(forest_event\room\fr\TileEntities[tX + (tY * ForestGridSize)])
			Next
		Next
		
		forest_event\EventState = 0.0
		forest_event\EventState3 = 0.0
	EndIf
End Function

Function UpdateTeslaGate%(e.Events)
	Local emit.Emitter, n.NPCs
	Local x#, y#, z#
	
	x = EntityX(e\room\OBJ, True) : z = EntityZ(e\room\OBJ, True) : y = EntityY(e\room\OBJ, True)
	If IsEqual(EntityX(me\Collider, True), x, 0.75) And IsEqual(EntityZ(me\Collider, True), z, 0.75) And IsEqual(EntityY(me\Collider, True), y, 1.3)
		If (Not me\Terminated)
			If opt\ParticleAmount > 0
				emit.Emitter = SetEmitter(Null, EntityX(me\Collider, True), EntityY(me\Collider, True), EntityZ(me\Collider, True), 14)
				EntityParent(emit\Owner, me\Collider)
			EndIf
			me\LightFlash = 0.4
			me\CameraShake = 1.0
			msg\DeathMsg = Format(GetLocalString("death", "tesla"), SubjectName)
			Kill()
		EndIf
	EndIf
	For n.NPCs = Each NPCs
		If n\NPCType <> NPCType513_1 And n\NPCType <> NPCType457 And (Not n\IsDead)
			If n\NPCType = NPCTypeClerk
				e\room\RoomDoors[0]\Locked = 0
				SetNPCFrame(n, 41.0)
				n\IsDead = True
				n\State3 = 1.0
			EndIf
			If IsEqual(EntityX(n\Collider, True), x, 0.6) And IsEqual(EntityZ(n\Collider, True), z, 0.6) And IsEqual(EntityY(n\Collider, True), y, 1.3)
				n\CurrSpeed = 0.0
				n\HP = 0
				If n\NPCType <> NPCType106
					n\TeslaHit = True
					EntityColor(n\OBJ, 40.0, 40.0, 40.0)
					If n\NPCType = NPCType173 Then EntityColor(n\OBJ2, 40.0, 40.0, 40.0)
				EndIf
				If opt\ParticleAmount > 0 And n\NPCType <> NPCType1048_A And n\NPCType <> NPCTypeCockroach
					emit.Emitter = SetEmitter(Null, EntityX(n\OBJ, True), EntityY(n\OBJ, True), EntityZ(n\OBJ, True), 14)
					EntityParent(emit\Owner, n\OBJ)
				EndIf
				Select n\NPCType
					Case NPCType106
						;[Block]
						GiveAchievement("tesla")
						n\State = 4.0
						;[End Block]
					Case NPCType049
						;[Block]
						If n\State <> 3.0 Then n\State = 5.0
						;[End Block]
					Case NPCType966
						;[Block]
						ShowEntity(n\OBJ)
						;[End Block]
					Case NPCType999
						;[Block]
						n\EnemyX = 0.0
						n\EnemyY = 0.0
						n\EnemyZ = 0.0
						n\State = 4.0
						;[End Block]
				End Select
			EndIf
		EndIf
	Next
	If Rand(5) < 5
		PositionTexture(t\OverlayTextureID[3], 0.0, Rnd(0.0, 1.0))
		If EntityHidden(e\room\Objects[0]) Then ShowEntity(e\room\Objects[0])
		If e\room\Dist < 6.0
			LightVolume = TempLightVolume * Rnd(1.0, 2.0)
			ShowEntity(e\room\Objects[4])
		EndIf
	Else
		HideEntity(e\room\Objects[4])
	EndIf
	e\EventState2 = e\EventState2 - (fps\Factor[0] * 1.5)
	If e\EventState2 <= 0.0
		StopChannel(e\SoundCHN) : e\SoundCHN = 0
		e\SoundCHN = PlaySoundEx(snd_I\TeslaPowerUpSFX, Camera, e\room\Objects[0], 4.0, 0.5)
		e\EventState = 3.0
		e\EventState2 = -70.0 - (70.0 * (e\EventID = e_broken_tesla) * 4.0)
	EndIf
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS