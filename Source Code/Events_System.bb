Type Events
	Field EventName$
	Field room.Rooms
	Field EventState#, EventState2#, EventState3#, EventState4#
	Field SoundCHN%, SoundCHN2%, SoundCHN3%
	Field Sound%, Sound2%, Sound3%
	Field SoundCHN_IsStream%, SoundCHN2_IsStream%, SoundCHN3_IsStream%
	Field EventStr$
	Field Img%
End Type 

Function CreateEvent.Events(Eventname$, RoomName$, ID%, Prob# = 0.0)
	; ~ RoomName = the name of the room(s) you want the event to be assigned to
	
	; ~ The ID-variable determines which of the rooms the event is assigned to,
	; ~ 0 will assign it to the first generated room, 1 to the second, etc.
	
	; ~ The prob-variable can be used to randomly assign events into some rooms
	; ~ 0.5 means that there's a 50% chance that event is assigned to the rooms
	; ~ 1.0 means that the event is assigned to every room
	; ~ The Id-variable is ignored if prob <> 0.0
	
	Local i% = 0, Temp%, e.Events, e2.Events, r.Rooms
	
	If Prob = 0.0 Then
		For r.Rooms = Each Rooms
			If (RoomName = "" Or RoomName = r\RoomTemplate\Name) Then
				Temp = False
				For e2.Events = Each Events
					If e2\room = r Then Temp = True : Exit
				Next
				
				i = i + 1
				If i >= ID And Temp = False Then
					e.Events = New Events
					e\EventName = Eventname					
					e\room = r
					Return(e)
				End If
			EndIf
		Next
	Else
		For r.Rooms = Each Rooms
			If (RoomName = "" Or RoomName = r\RoomTemplate\Name) Then
				Temp = False
				For e2.Events = Each Events
					If e2\room = r Then Temp = True : Exit
				Next
				
				If Rnd(0.0, 1.0) < Prob And Temp = False Then
					e.Events = New Events
					e\EventName = Eventname					
					e\room = r
				End If
			EndIf
		Next		
	EndIf
	Return(Null)
End Function

Function InitEvents()
	Local e.Events
	
	CreateEvent("room173intro", "room173intro", 0)
	CreateEvent("room173", "room173", 0)
	
	CreateEvent("pocketdimension", "pocketdimension", 0)	
	
	; ~ There's a 7% chance that SCP-106 appears in the rooms named "tunnel"
	CreateEvent("tunnel106", "tunnel", 0, 0.07 + (0.1 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The chance for SCP-173 appearing in the first room2clockroom is about 66%
	; ~ There's a 30% chance that it appears in the later room2clockrooms
	If Rand(3) < 3 Then CreateEvent("room2clockroom173", "room2clockroom", 0)
	CreateEvent("room2clockroom173", "room2clockroom", 0, 0.3 + (0.5 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent("room2trick", "room2", 0, 0.15)	
	
	CreateEvent("1048a", "room2", 0, 1.0)	
	
	CreateEvent("room2storage", "room2storage", 0)	
	
	; ~ SCP-096 spawns in the first (and last) "room2clockroom3"
	CreateEvent("room2clockroom096", "room2clockroom3", 0)
	
	CreateEvent("room1endroom106", "room1endroom", Rand(0, 1))
	
	CreateEvent("room2poffices2", "room2poffices2", 0)
	
	CreateEvent("room2fan", "room2_2", 0, 1.0)
	
	CreateEvent("room2elevator2", "room2elevator", 0)
	CreateEvent("room2elevator", "room2elevator", Rand(1, 2))
	
	CreateEvent("room3storage", "room3storage", 0, 0.0)
	
	CreateEvent("tunnel2smoke", "tunnel2", 0, 0.2)
	CreateEvent("tunnel2", "tunnel2", Rand(0, 2), 0.0)
	CreateEvent("tunnel2", "tunnel2", 0, (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ SCP-173 appears in half of the "room2doors"-rooms
	CreateEvent("room2doors173", "room2doors", 0, 0.5 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	; ~ The anomalous duck in "room2offices2"-rooms
	CreateEvent("room2offices2", "room2offices2", 0, 0.7)
	
	CreateEvent("room2closets", "room2closets", 0)	
	
	CreateEvent("room2cafeteria", "room2cafeteria", 0)	
	
	CreateEvent("room3pitduck", "room3pit", 0)
	CreateEvent("room3pit1048", "room3pit", 1)
	
	; ~ The event that causes the door to open by itself in "room2offices3"
	CreateEvent("room2offices3", "room2offices3", 0, 1.0)	
	
	CreateEvent("room2servers", "room2servers", 0)	
	
	CreateEvent("room3servers", "room3servers", 0)	
	CreateEvent("room3servers", "room3servers2", 0)
	
	; ~ The dead guard
	CreateEvent("room3tunnel", "room3tunnel", 0, 0.08)
	
	CreateEvent("room4", "room4", 0)
	
	If Rand(5) < 5 Then 
		Select Rand(3)
			Case 1
				;[Block]
				CreateEvent("682roar", "tunnel", Rand(0, 2), 0.0)
				;[End Block]
			Case 2
				;[Block]
				CreateEvent("682roar", "room3pit", Rand(0, 2), 0.0)	
				;[End Block]
			Case 3
				;[Block]
				CreateEvent("682roar", "room2z3", 0, 0.0)
				;[End Block]
		End Select 
	EndIf 
	
	CreateEvent("room2testroom173", "room2testroom2", 0, 1.0)	
	
	CreateEvent("room2tesla", "room2tesla", 0, 0.9)
	
	CreateEvent("room2nuke", "room2nuke", 0, 0)
	
	If Rand(5) < 5 Then 
		CreateEvent("room895_106", "room895", 0, 0)
	Else
		CreateEvent("room895", "room895", 0, 0)
	EndIf 
	
	CreateEvent("checkpoint", "checkpoint1", 0, 1.0)
	CreateEvent("checkpoint", "checkpoint2", 0, 1.0)
	
	CreateEvent("room3door", "room3", 0, 0.1)
	CreateEvent("room3door", "room3tunnel", 0, 0.1)	
	
	If Rand(2) = 1 Then
		CreateEvent("106victim", "room3", Rand(1, 2))
		CreateEvent("106sinkhole", "room3_2", Rand(2, 3))
	Else
		CreateEvent("106victim", "room3_2", Rand(1, 2))
		CreateEvent("106sinkhole", "room3", Rand(2, 3))
	EndIf
	CreateEvent("106sinkhole", "room4", Rand(1, 2))
	
	CreateEvent("room079", "room079", 0, 0.0)	
	
	CreateEvent("room049", "room049", 0, 0.0)
	
	CreateEvent("room012", "room012", 0, 0.0)
	
	CreateEvent("room035", "room035", 0, 0.0)
	
	CreateEvent("room008", "room008", 0, 0.0)
	
	CreateEvent("room106", "room106", 0, 0.0)	
	
	CreateEvent("room372", "room372", 0, 0.0)
	
	CreateEvent("room914", "room914", 0, 0.0)
	
	CreateEvent("buttghost", "room2toilets", 0, 0.0)
	CreateEvent("toiletguard", "room2toilets", 1, 0.0)
	
	CreateEvent("room2pipes106", "room2pipes", Rand(0, 3)) 
	
	CreateEvent("room2pit", "room2pit", 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent("room2testroom", "room2testroom", 0)
	
	CreateEvent("room2tunnel", "room2tunnel", 0)
	
	CreateEvent("room2ccont", "room2ccont", 0)
	
	CreateEvent("gateaentrance", "gateaentrance", 0)
	CreateEvent("gatea", "gatea", 0)	
	CreateEvent("gateb", "gateb", 0)
	
	CreateEvent("room205", "room205", 0)
	
	CreateEvent("room860","room860", 0)
	
	CreateEvent("room966","room966", 0)
	
	CreateEvent("room1123", "room1123", 0, 0.0)
	
	CreateEvent("room2tesla", "room2tesla_lcz", 0, 0.9)
	CreateEvent("room2tesla", "room2tesla_hcz", 0, 0.9)
	
	CreateEvent("room4tunnels", "room4tunnels", 0)
	
	CreateEvent("room2gw_b", "room2gw_b", Rand(0, 1))
	CreateEvent("room2gw", "room2gw", 0, 1.0)
	
	CreateEvent("dimension1499", "dimension1499", 0)
	
	CreateEvent("room1162", "room1162", 0)
	
	CreateEvent("room2scps2", "room2scps2", 0)
	
	CreateEvent("room2sl", "room2sl", 0)
	
	CreateEvent("room2medibay2", "room2medibay2", 0)
	
	CreateEvent("room2shaft", "room2shaft", 0)
	
	CreateEvent("room1lifts", "room1lifts", 0)
	
	CreateEvent("096spawn", "room4pit", 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room3pit", 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room2pipes", 0, 0.4 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room2pit", 0, 0.5 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room3tunnel", 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room4tunnels", 0, 0.7 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "tunnel", 0, 0.6 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "tunnel2", 0, 0.4 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	CreateEvent("096spawn", "room3z2", 0, 0.7 + (0.2 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent("room2pit", "room2_4", 0, 0.4 + (0.4 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent("room2offices035", "room2offices", 0)
	
	CreateEvent("room2pit106", "room2pit", 0, 0.07 + (0.1 * SelectedDifficulty\AggressiveNPCs))
	
	CreateEvent("room4info", "room4info", 0)
	
	CreateEvent("room2bio", "room2bio", 0)
End Function

Function QuickLoadEvents()
	CatchErrors("Uncaught (QuickLoadEvents)")
	
	If QuickLoad_CurrEvent = Null Then
		QuickLoadPercent = -1
		Return
	EndIf
	
	Local e.Events = QuickLoad_CurrEvent
	Local r.Rooms, sc.SecurityCams, sc2.SecurityCams, Scale#, Pvt%, n.NPCs, Tex%, i%, x#, z#
	Local o.Objects = First Objects
	
	; ~ Might be a good idea to use QuickLoadPercent to determine the "steps" of the loading process 
	; ~ Instead of magic values in e\eventState and e\eventStr
	
	Select e\EventName
		Case "room2sl"
			;[Block]
			If e\EventState = 0 And e\EventStr <> ""
				If e\EventStr <> "" And Left(e\EventStr, 4) <> "Load"
					QuickLoadPercent = QuickLoadPercent + 5
					If Int(e\EventStr) > 9
						e\EventStr = "Load2"
					Else
						e\EventStr = Int(e\EventStr) + 1
					EndIf
				ElseIf e\EventStr = "Load2"
					Local Skip% = False
					
					If e\room\NPC[0] = Null Then
						For n.NPCs = Each NPCs
							If n\NPCtype = NPCtype049
								Skip = True
								Exit
							EndIf
						Next
						
						If (Not Skip) Then
							e\room\NPC[0] = CreateNPC(NPCtype049, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True) + 5.0, EntityZ(e\room\Objects[7], True))
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True) + 5.0, EntityZ(e\room\Objects[7], True))
							ResetEntity(e\room\NPC[0]\Collider)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0)
							e\room\NPC[0]\State = 0.0
							e\room\NPC[0]\PrevState = 2
						EndIf
					EndIf
					QuickLoadPercent = 80
					e\EventStr = "Load3"
				ElseIf e\EventStr = "Load3"
					e\EventState = 1.0
					If e\EventState2 = 0.0 Then e\EventState2 = (-70) * 5.0
					
					QuickLoadPercent = 100
				EndIf
			EndIf
			;[End Block]
		Case "room2closets"
			;[Block]
			If e\EventState = 0.0
				If e\EventStr = "Load0"
					QuickLoadPercent = 10
					If e\room\NPC[0] = Null Then
						e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
					EndIf
					ChangeNPCTextureID(e\room\NPC[0], 11)
					e\EventStr = "Load1"
				ElseIf e\EventStr = "Load1"
					QuickLoadPercent = 20
					e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\Storeroom\Escape1.ogg")
					e\EventStr = "Load2"
				ElseIf e\EventStr = "Load2"
					QuickLoadPercent = 35
					e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 12.0)
					e\EventStr = "Load3"
				ElseIf e\EventStr = "Load3"
					QuickLoadPercent = 55
					If e\room\NPC[1] = Null Then
						e\room\NPC[1] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True))
					EndIf
					ChangeNPCTextureID(e\room\NPC[1], 2)
					e\EventStr = "Load4"
				ElseIf e\EventStr = "Load4"
					QuickLoadPercent = 80
					e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\Storeroom\Escape2.ogg")
					e\EventStr = "Load5"
				ElseIf e\EventStr = "Load5"
					QuickLoadPercent = 100
					PointEntity(e\room\NPC[0]\Collider, e\room\NPC[1]\Collider)
					PointEntity(e\room\NPC[1]\Collider, e\room\NPC[0]\Collider)
					
					e\EventState = 1.0
				EndIf
			EndIf
			;[End Block]
		Case "room3storage"
			;[Block]
			If e\room\NPC[0] = Null Then
				e\room\NPC[0] = CreateNPC(NPCtype939, 0, 0, 0)
				QuickLoadPercent = 25
			ElseIf e\room\NPC[1] = Null Then
				e\room\NPC[1] = CreateNPC(NPCtype939, 0, 0, 0)
				QuickLoadPercent = 50
			ElseIf e\room\NPC[2] = Null Then
				e\room\NPC[2] = CreateNPC(NPCtype939, 0, 0, 0)
				QuickLoadPercent = 100
			Else
				If QuickLoadPercent > -1 Then QuickLoadPercent = 100
			EndIf
			;[End Block]
		Case "room049"
			;[Block]
			If e\EventState = 0.0 Then
				If e\EventStr = "Load0"
					n.NPCs = CreateNPC(NPCtype049_2, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True))
					PointEntity(n\Collider, e\room\OBJ)
					TurnEntity(n\Collider, 0.0, 190.0, 0.0)
					QuickLoadPercent = 20
					e\EventStr = "Load1"
				ElseIf e\EventStr = "Load1"
					n.NPCs = CreateNPC(NPCtype049_2, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True), EntityZ(e\room\Objects[5], True))
					PointEntity(n\Collider, e\room\OBJ)
					TurnEntity(n\Collider, 0.0, 20.0, 0.0)
					QuickLoadPercent = 60
					e\EventStr = "Load2"
				ElseIf e\EventStr = "Load2"
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype049
							e\room\NPC[0] = n
							e\room\NPC[0]\State = 2.0
							e\room\NPC[0]\Idle = 1
							e\room\NPC[0]\HideFromNVG = True
							PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True) + 3, EntityZ(e\room\Objects[4], True))
							ResetEntity(e\room\NPC[0]\Collider)
							Exit
						EndIf
					Next
					If e\room\NPC[0] = Null
						n.NPCs = CreateNPC(NPCtype049, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True) + 3, EntityZ(e\room\Objects[4], True))
						PointEntity(n\Collider, e\room\OBJ)
						n\State = 2.0
						n\Idle = 1
						n\HideFromNVG = True
						e\room\NPC[0] = n
					EndIf
					QuickLoadPercent = 100
					e\EventState = 1.0
				EndIf
			EndIf
			;[End Block]
		Case "room205"
			;[Block]
			If e\EventState = 0 Or e\EventStr <> "LoadDone" Then
				If e\EventStr = "Load0"
					e\room\Objects[3] = CopyEntity(o\NPCModelID[30])
					QuickLoadPercent = 10
					e\EventStr = "Load1"
				ElseIf e\EventStr = "Load1"
					e\room\Objects[4] = CopyEntity(o\NPCModelID[31])
					QuickLoadPercent = 20
					e\EventStr = "Load2"
				ElseIf e\EventStr = "Load2"
					e\room\Objects[5] = CopyEntity(o\NPCModelID[32])
					QuickLoadPercent = 30
					e\EventStr = "Load3"
				ElseIf e\EventStr = "Load3"
					e\room\Objects[6] = CopyEntity(o\NPCModelID[33])
					QuickLoadPercent = 40
					e\EventStr = "Load4"
				ElseIf e\EventStr = "Load4"
					QuickLoadPercent = 50
					e\EventStr = "Load5"
				ElseIf e\EventStr = "Load5"
					For i = 3 To 6
						PositionEntity(e\room\Objects[i], EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), True)
						RotateEntity(e\room\Objects[i], -90.0, EntityYaw(e\room\Objects[0], True), 0.0, True)
						ScaleEntity(e\room\Objects[i], 0.05, 0.05, 0.05, True)
					Next
					QuickLoadPercent = 70
					e\EventStr = "Load6"
				ElseIf e\EventStr = "Load6"
					HideEntity(e\room\Objects[3])
					HideEntity(e\room\Objects[4])
					HideEntity(e\room\Objects[5])
					QuickLoadPercent = 100
					e\EventStr = "LoadDone"
				EndIf
			EndIf
			;[End Block]
		Case "room860"
			;[Block]
			If e\EventStr = "Load0"
				QuickLoadPercent = 15
				ForestNPC = CreateSprite()
				ScaleSprite(ForestNPC, 0.75 * (140.0 / 410.0), 0.75)
				SpriteViewMode(ForestNPC, 4)
				EntityFX(ForestNPC, 1 + 8)
				ForestNPCTex = LoadAnimTexture("GFX\npcs\AgentIJ.AIJ", 1 + 2, 140, 410, 0, 4)
				ForestNPCData[0] = 0.0
				EntityTexture(ForestNPC, ForestNPCTex, ForestNPCData[0])
				ForestNPCData[1] = 0.0
				ForestNPCData[2] = 0.0
				HideEntity(ForestNPC)
				e\EventStr = "Load1"
			ElseIf e\EventStr = "Load1"
				QuickLoadPercent = 40
				e\EventStr = "Load2"
			ElseIf e\EventStr = "Load2"
				QuickLoadPercent = 100
				If e\room\NPC[0] = Null Then e\room\NPC[0] = CreateNPC(NPCtype860_2, 0.0, 0.0, 0.0)
				e\EventStr = "LoadDone"
			EndIf
			;[End Block]
		Case "room966"
			;[Block]
			If e\EventState = 1.0
				e\EventState2 = e\EventState2 + FPSfactor
				If e\EventState2 > 30.0 Then
					If e\EventStr = ""
						CreateNPC(NPCtype966, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
						QuickLoadPercent = 50
						e\EventStr = "Load0"
					ElseIf e\EventStr = "Load0"
						CreateNPC(NPCtype966, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True))
						QuickLoadPercent = 100
						e\EventState = 2.0
					EndIf
				Else
					QuickLoadPercent = Int(e\EventState2)
				EndIf
			EndIf
			;[End Block]
		Case "dimension1499"
			;[Block]
			If e\EventState = 0.0
				If e\EventStr = "Load0"
					QuickLoadPercent = 10
					e\room\Objects[0] = LoadMesh_Strict("GFX\map\dimension1499\1499plane.b3d")
					HideEntity(e\room\Objects[0])
					e\EventStr = "Load1"
				ElseIf e\EventStr = "Load1"
					QuickLoadPercent = 30
					NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
					e\EventStr = 1
				Else
					If Int(e\EventStr) < 16
						QuickLoadPercent = QuickLoadPercent + 2
						e\room\Objects[Int(e\EventStr)] = LoadMesh_Strict("GFX\map\dimension1499\1499object" + (Int(e\EventStr)) + ".b3d")
						HideEntity(e\room\Objects[Int(e\EventStr)])
						e\EventStr = Int(e\EventStr) + 1
					ElseIf Int(e\EventStr) = 16
						QuickLoadPercent = 90
						CreateChunkParts(e\room)
						e\EventStr = 17
					ElseIf Int(e\EventStr) = 17
						QuickLoadPercent = 100
						x = EntityX(e\room\OBJ)
						z = EntityZ(e\room\OBJ)
						
						Local ch.Chunk
						
						For i = -2 To 0 Step 2
							ch = CreateChunk(-1, x * (i * 2.5), EntityY(e\room\OBJ), z, True)
						Next
						For i = -2 To 0 Step 2
							ch = CreateChunk(-1, x * (i * 2.5), EntityY(e\room\OBJ), z - 40.0, True)
						Next
						e\EventState = 2.0
						e\EventStr = 18
					EndIf
				EndIf
			EndIf
			;[End Block]
	End Select
	
	CatchErrors("QuickLoadEvents " + e\EventName)
End Function

Function UpdateEvents()
	CatchErrors("Uncaught (UpdateEvents)")
	
	Local Dist#, i%, Temp%, Pvt%, StrTemp$, j%, k%
	Local p.Particles, n.NPCs, r.Rooms, e.Events, e2.Events, it.Items, it2.Items, em.Emitters, sc.SecurityCams, sc2.SecurityCams
	Local CurrTrigger$ = ""
	Local x#, y#, z#
	Local Angle#
	Local o.Objects = First Objects
	Local ov.Overlays = First Overlays
	Local tt.TempTextures = First TempTextures
	
	CurrStepSFX = 0
	
	UpdateRooms()
	
	For e.Events = Each Events
		Select e\EventName
			Case "gateb"
				;[Block]
				If RemoteDoorOn = False Then
					e\room\RoomDoors[4]\Locked = True
				ElseIf RemoteDoorOn And e\EventState3 = 0.0
					e\room\RoomDoors[4]\Locked = 2
				Else
					e\room\RoomDoors[4]\Locked = False
					
					If Curr096 <> Null Then
						If Curr096\State = 0.0 Or Curr096\State = 5.0 Then
							e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[8], e\room\Objects[9], e)
						Else
							e\EventState2 = Update096ElevatorEvent(e, e\EventState2, e\room\RoomDoors[0], e\room\Objects[8])
						EndIf
					Else
						e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[8], e\room\Objects[9], e)
					EndIf
					EntityAlpha(ov\OverlayID[0], 1.0)						
				EndIf
				;[End Block]
			Case "room173"
				;[Block]
				If e\room\RoomDoors[5] = Null Then
					For i = 0 To 3
						If e\room\AdjDoor[i] <> Null Then
							e\room\RoomDoors[5] = e\room\AdjDoor[i]
							e\room\RoomDoors[5]\Open = True
							Exit
						EndIf
					Next
				EndIf
				If e\EventState = 0.0 Then
					If PlayerRoom = e\room Then
						e\room\RoomDoors[2]\Open = True
						
						ShowEntity(ov\OverlayID[0])
						AmbientLight(Brightness, Brightness, Brightness)
						CameraFogRange(Camera, CameraFogNear, CameraFogFar)
						CameraFogMode(Camera, 1)
						
						If SelectedDifficulty\SaveType = SAVEANYWHERE Then
							Msg = "Press " + KeyName(KEY_SAVE) + " to save."
							MsgTimer = 70 * 4.0
						ElseIf SelectedDifficulty\SaveType = SAVEONSCREENS Then
							Msg = "Saving is only permitted on clickable monitors scattered throughout the facility."
							MsgTimer = 70 * 8.0
						EndIf
						
						Curr173\Idle = False
						
						While e\room\RoomDoors[1]\OpenState < 180.0
							e\room\RoomDoors[1]\OpenState = Min(180.0, e\room\RoomDoors[1]\OpenState + 0.8)
							MoveEntity(e\room\RoomDoors[1]\OBJ, Sin(e\room\RoomDoors[1]\OpenState) / 180.0, 0.0, 0.0)
							MoveEntity(e\room\RoomDoors[1]\OBJ2, -Sin(e\room\RoomDoors[1]\OpenState) / 180.0, 0.0, 0.0)
						Wend
						
						If e\room\NPC[0] <> Null Then SetNPCFrame(e\room\NPC[0], 74.0) : e\room\NPC[0]\State = 8.0
						
						If e\room\NPC[1] = Null Then
							e\room\NPC[1] = CreateNPC(NPCtypeD, 0.0, 0.0, 0.0)
							ChangeNPCTextureID(e\room\NPC[1], 3)
						EndIf
						PositionEntity(e\room\NPC[1]\Collider, e\room\x, 0.5, e\room\z - 1.0, True)
						ResetEntity(e\room\NPC[1]\Collider)
						SetNPCFrame(e\room\NPC[1], 210.0)
						
						If e\room\NPC[2] = Null Then
							e\room\NPC[2] = CreateNPC(NPCtypeGuard, 0.0, 0.0, 0.0)
						EndIf
						PositionEntity(e\room\NPC[2]\Collider, e\room\x, 0.5, e\room\z + 528.0 * RoomScale, True)
						ResetEntity(e\room\NPC[2]\Collider)
						e\room\NPC[2]\State = 7.0
						PointEntity(e\room\NPC[2]\Collider, e\room\NPC[1]\Collider)
						
						If e\room\NPC[0] = Null
							e\room\NPC[3] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True))
							RotateEntity(e\room\NPC[3]\Collider, 0.0, 90.0, 0.0)
							SetNPCFrame(e\room\NPC[3], 286.0)
							e\room\NPC[3]\State = 8.0
							MoveEntity(e\room\NPC[3]\Collider, 1.0, 0.0, 0.0)
							
							e\room\NPC[4] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[3], True), 0.5, EntityZ(e\room\Objects[3], True))
							SetNPCFrame(e\room\NPC[4], 19.0)
							e\room\NPC[4]\State = 3.0
							RotateEntity(e\room\NPC[4]\Collider, 0.0, 270.0, 0.0)
							MoveEntity(e\room\NPC[4]\Collider, 0.0, 0.0, 2.65)
							
							e\room\NPC[5] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[4], True), 0.5, EntityZ(e\room\Objects[4], True))
							ChangeNPCTextureID(e\room\NPC[5], 6)
							SetNPCFrame(e\room\NPC[5], 19.0)
							e\room\NPC[5]\State = 3.0
							RotateEntity(e\room\NPC[5]\Collider, 0.0, 270.0, 0.0)
							MoveEntity(e\room\NPC[5]\Collider, 0.25, 0.0, 3.0)
							RotateEntity(e\room\NPC[5]\Collider, 0.0, 0.0, 0.0)
							
							x = EntityX(e\room\OBJ, True) + 3712.0 * RoomScale
							y = 384.0 * RoomScale
							z = EntityZ(e\room\OBJ, True) + 1312.0 * RoomScale
							
							For i = 3 To 5
								PositionEntity(e\room\NPC[i]\Collider, x + (EntityX(e\room\NPC[i]\Collider) - EntityX(e\room\OBJ)), y + EntityY(e\room\NPC[i]\Collider) + 0.4, z + (EntityZ(e\room\NPC[i]\Collider) - EntityZ(e\room\OBJ)))
								ResetEntity(e\room\NPC[i]\Collider)
							Next
						EndIf
						e\EventState = 1.0
					EndIf
				Else
					If e\room\NPC[0] <> Null Then AnimateNPC(e\room\NPC[0], 249.0, 286.0, 0.4, False)
					
					CurrTrigger = CheckTriggers()
					
					If (CurrTrigger = "173scene_timer") Then
						e\EventState = e\EventState + FPSfactor
					Else If (CurrTrigger = "173scene_activated")
						e\EventState = Max(e\EventState, 500.0)
					EndIf
					
					If e\EventState < 850.0
						PositionEntity(Curr173\Collider, e\room\x + 32.0 * RoomScale, 0.31, e\room\z + 1072.0 * RoomScale, True)
						HideEntity(Curr173\OBJ)
					EndIf
					
					If e\EventState >= 500.0 Then
						e\EventState = e\EventState + FPSfactor
						
						If e\EventState2 = 0.0 Then
							ShowEntity(Curr173\OBJ)
							If e\EventState > 900.0 And e\room\RoomDoors[5]\open Then
								If e\EventState - FPSfactor =< 900.0 Then 
									e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\Intro\WhatThe.ogg")
									e\room\NPC[1]\SoundCHN = PlaySound2(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider)
								EndIf
								e\room\NPC[1]\State = 3.0
								e\room\NPC[1]\CurrSpeed = CurveValue(-0.008, e\room\NPC[1]\CurrSpeed, 5.0)
								AnimateNPC(e\room\NPC[1], 260.0, 236.0, e\room\NPC[1]\CurrSpeed * 18.0)
								RotateEntity(e\room\NPC[1]\Collider, 0.0, 0.0, 0.0)
								
								If e\EventState > 900.0 + 2.5 * 70.0 Then
									If e\room\NPC[2]\State <> 1.0
										e\room\NPC[2]\CurrSpeed = CurveValue(-0.012, e\room\NPC[2]\CurrSpeed, 5.0)
										AnimateNPC(e\room\NPC[2], 39.0, 76.0, e\room\NPC[2]\CurrSpeed * 40.0)
										MoveEntity(e\room\NPC[2]\Collider, 0.0, 0.0, e\room\NPC[2]\CurrSpeed * FPSfactor)
										e\room\NPC[2]\State = 8.0
										
										If EntityZ(e\room\NPC[2]\Collider) < e\room\z Then
											PointEntity(e\room\NPC[2]\obj, e\room\NPC[1]\Collider)
											RotateEntity(e\room\NPC[2]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[2]\OBJ) - 180.0, EntityYaw(e\room\NPC[2]\Collider), 15.0), 0.0)
										Else
											RotateEntity(e\room\NPC[2]\Collider, 0.0, 0.0, 0.0)
										EndIf
									EndIf
								EndIf
								
								If e\EventState < 900.0 + 4.0 * 70.0 Then
									PositionEntity(Curr173\Collider, e\room\x + 32.0 * RoomScale, 0.31, e\room\z + 1072.0 * RoomScale, True)
									RotateEntity(Curr173\Collider, 0.0, 190.0, 0.0)
									
									If e\EventState > 900.0 + 70.0 And e\EventState < 900.0 + 2.5 * 70.0 Then
										AnimateNPC(e\room\NPC[2], 1539.0, 1553.0, 0.2, False)
										PointEntity(e\room\NPC[2]\OBJ, Curr173\Collider)
										RotateEntity(e\room\NPC[2]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[2]\OBJ), EntityYaw(e\room\NPC[2]\Collider), 15.0), 0.0)
									EndIf
								Else
									If e\EventState - FPSfactor < 900.0 + 4.0 * 70.0 Then 
										PlaySound_Strict(IntroSFX(Rand(8, 10)))
										LightBlink = 3.0
										PlaySound2(StoneDragSFX, Camera, Curr173\Collider)
										PointEntity(Curr173\Collider, e\room\NPC[2]\Collider)
										If EntityY(Collider) < 320.0 * RoomScale Then BlinkTimer = -10.0
									EndIf
									PositionEntity(Curr173\Collider, e\room\x - 96.0 * RoomScale, 0.31, e\room\z + 592.0 * RoomScale, True)
									RotateEntity(Curr173\Collider, 0.0, 190.0, 0.0)
									
									If e\room\NPC[2]\State <> 1 And KillTimer >= 0.0
										If EntityZ(e\room\NPC[2]\Collider) < e\room\z - 1150.0 * RoomScale Then
											e\room\RoomDoors[5]\Open = False
											LightBlink = 3.0
											PlaySound_Strict(IntroSFX(Rand(8, 10)))
											BlinkTimer = -10.0
											PlaySound2(StoneDragSFX, Camera, Curr173\Collider)
											If EntityDistance(Curr173\Collider, Collider) < 2.5 And Abs(EntityY(Collider) - EntityY(Curr173\Collider)) < 1.0 Then
                                                PositionEntity(Curr173\Collider, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
                                            Else
                                                PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
                                            EndIf
											ResetEntity(Curr173\Collider)
											Msg = "Hold " + KeyName(KEY_SPRINT) + " to run."
											MsgTimer = 70 * 8.0
										EndIf
									EndIf
								EndIf
								
								; ~ If Ulgrin can see the player then start shooting at them.
								If (CurrTrigger = "173scene_end") And EntityVisible(e\room\NPC[2]\Collider, Collider) And (Not chs\NoTarget) Then
									e\room\NPC[2]\State = 1.0
									e\room\NPC[2]\State3 = 1.0
								ElseIf e\room\NPC[2]\State = 1.0 And (Not EntityVisible(e\room\NPC[2]\Collider, Collider))
									e\room\NPC[2]\State = 0.0
									e\room\NPC[2]\State3 = 0.0
								EndIf
								If e\room\NPC[2]\State = 1.0 Then e\room\RoomDoors[5]\Open = True
							Else
								CanSave = True
								
								If e\room\NPC[2]\State <> 1.0
									If EntityX(Collider) < (e\room\x + 1384.0 * RoomScale) Then e\EventState = Max(e\EventState, 900.0)
									
									If e\room\RoomDoors[5]\OpenState = 0.0 Then 
										If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
										If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
										
										e\EventState2 = 1.0
									EndIf
								EndIf
							EndIf
						EndIf
						
						PositionEntity(e\room\Objects[0], EntityX(e\room\Objects[0], True), -Max(e\EventState - 1300.0, 0.0) / 4500.0, EntityZ(e\room\Objects[0], True), True)
						RotateEntity(e\room\Objects[0], -Max(e\EventState - 1320.0, 0.0) / 130.0, 0.0, -Max(e\EventState - 1300.0, 0.0) / 40.0, True)
						
						PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[1], True), -Max(e\EventState - 1800.0, 0.0) / 5000.0, EntityZ(e\room\Objects[1], True), True)
						RotateEntity(e\room\Objects[1], -Max(e\EventState - 2040.0, 0.0) / 135.0, 0.0, -Max(e\EventState - 2040.0,0) / 43.0, True)
						
						If EntityDistance(e\room\Objects[0], Collider) < 2.5 Then
							If Rand(300) = 2 Then PlaySound2(DecaySFX(Rand(1, 3)),Camera, e\room\Objects[0], 3.0)
						EndIf
					EndIf
					
					If (e\EventState < 2000.0) Then
						If e\SoundCHN = 0 Then
							e\SoundCHN = PlaySound_Strict(AlarmSFX(0))
						Else
							If ChannelPlaying(e\SoundCHN) = False Then e\SoundCHN = PlaySound_Strict(AlarmSFX(0))
						End If
					EndIf
					
					If (e\EventState3 < 11.0) Then
						If ChannelPlaying(e\SoundCHN2) = False Then
							e\EventState3 = e\EventState3 + 1.0
							
							If e\Sound2 <> 0 Then
								FreeSound_Strict(e\Sound2) : e\Sound2 = 0
							EndIf
							e\Sound2 = LoadSound_Strict("SFX\Alarm\Alarm2_" + Int(e\EventState3) + ".ogg")
							e\SoundCHN2 = PlaySound_Strict(e\Sound2)
						Else
							If Int(e\EventState3) = 8.0 Then CameraShake = 1.0
						EndIf
					EndIf
					If ((e\EventState Mod 600.0 > 300.0) And ((e\EventState + FPSfactor) Mod 600.0 < 300.0)) Then
						i = Floor((e\EventState - 5000.0) / 600.0) + 1.0
						
						If i = 0 Then PlaySound_Strict(LoadTempSound("SFX\Room\Intro\PA\Scripted\Scripted6.ogg"))
						
						If (i > 0 And i < 26) Then
							If (Not CommotionState(i)) Then ; ~ Prevents the same commotion file from playing more then once.
								PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Commotion\Commotion" + i + ".ogg"))
								CommotionState(i) = True
							EndIf
						EndIf
						
						If (i > 26) Then
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							
							FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							FreeEntity(e\room\Objects[1]) : e\room\Objects[1] = 0
							
							RemoveEvent(e)							
						EndIf
					EndIf					
				End If
				;[End Block]
			Case "room173intro"
				;[Block]
				If KillTimer >= 0.0 And e\EventState2 = 0.0 Then
					PlayerZone = 0
					
					If e\EventState3 > 0 Then
						ShouldPlay = 13
						
						; ~ Slow the player down to match his speed to the guards
						CurrSpeed = Min(CurrSpeed - (CurrSpeed * (0.008 / EntityDistance(e\room\NPC[3]\Collider, Collider)) * FPSfactor), CurrSpeed)
						
						If e\EventState3 < 170.0 Then
							If e\EventState3 = 1.0 Then
								PositionEntity(Camera, x, y, z)
								HideEntity(Collider)
								PositionEntity(Collider, x, 0.302, z)
								RotateEntity(Camera, -70.0, 0.0, 0.0)								
								
								CurrMusicVolume = MusicVolume
								
								StopStream_Strict(MusicCHN)
								MusicCHN = StreamSound_Strict("SFX\Music\" + Music(13) + ".ogg",CurrMusicVolume, Mode)
								NowPlaying = ShouldPlay
								
								PlaySound_Strict(IntroSFX(Rand(8, 10)))
								BlurTimer = 500.0
								ShowEntity(ov\OverlayID[7])
								EntityAlpha(ov\OverlayID[7], 0.5)
							EndIf
							
							If e\EventState3 < 3.0 Then
								e\EventState3 = e\EventState3 + FPSfactor / 100.0
							ElseIf e\EventState3 < 15.0 Or e\EventState3 >= 50.0 Then
								e\EventState3 = e\EventState3 + FPSfactor / 30.0
							EndIf
							
							If e\EventState3 < 15.0 Then
								x = EntityX(e\room\OBJ) - (3224.0 + 1024.0) * RoomScale
								y = 136.0 * RoomScale
								z = EntityZ(e\room\OBJ) + 8.0 * RoomScale	
								
								If e\EventState3 - FPSfactor / 30.0 < 3.7 And e\EventState3 > 3.7 Then PlaySound_Strict(IntroSFX(0))
								If e\EventState3 - FPSfactor / 30.0 < 9.3 And e\EventState3 > 9.3 Then PlaySound_Strict(IntroSFX(1))
								
								If e\EventState3 < 14.0 Then
									Mouse_X_Speed_1 = 0.0
									Mouse_Y_Speed_1 = 0.0
									
									If e\EventState3 - FPSfactor / 30.0 < 12.0 And e\EventState3 > 12.0 Then PlaySound2(StepSFX(0, 0, 0), Camera, Collider, 8.0, 0.3)
									
									ShowEntity(ov\OverlayID[7])
									EntityAlpha(ov\OverlayID[7], 0.9 - (e\EventState3 / 2.0))
									
									x = x + (EntityX(e\room\OBJ) - (3048.0 + 1024.0) * RoomScale - x) * Max((e\EventState3 - 10.0) / 4.0, 0.0) 
									
									If e\EventState3 < 10.0 Then
										y = y + (0.2) * Min(Max((e\EventState3 - 3.0) / 5.0, 0.0), 1.0)
									Else
										y = (y + 0.2) + (0.302 + 0.6 - (y + 0.2)) * Max((e\EventState3 - 10.0) / 4.0, 0.0) 
									EndIf
									
									z = z + (EntityZ(e\room\OBJ) + 104.0 * RoomScale - z) * Min(Max((e\EventState3 - 3) / 5.0, 0.0), 1.0)
									
									; ~ I'm sorry you have to see this
									RotateEntity(Camera, -70.0 + 70.0 * Min(Max((e\EventState3 - 3.0) / 5.0, 0.0), 1.0) + Sin(e\EventState3 * 12.857) * 5.0, (-60.0) * Max((e\EventState3 - 10.0) / 4.0, 0.0), Sin(e\EventState3 * 25.7) * 8.0)
									
									PositionEntity(Camera, x, y, z)
									HideEntity(Collider)
									PositionEntity(Collider, x, 0.302, z)	
									DropSpeed = 0.0
									UnableToMove = True
								Else
									HideEntity(ov\OverlayID[7])
									
									PositionEntity(Collider, EntityX(Collider), 0.302, EntityZ(Collider))
									ResetEntity(Collider)
									ShowEntity(Collider)
									DropSpeed = 0.0
									UnableToMove = False
									
									Msg = "Pick up the paper on the desk."
									MsgTimer = 70 * 7.0
									
									e\EventState3 = 15.0
								EndIf
								
								User_Camera_Pitch = 0.0
								RotateEntity(Collider, 0.0, EntityYaw(Camera), 0.0)
							ElseIf e\EventState3 < 40.0
								If Inventory(0) <> Null Then
									Msg = "Press " + KeyName(KEY_INV) + " to open the inventory."
									MsgTimer = 70 * 7.0
									e\EventState3 = 40.0
									Exit
								EndIf
							EndIf
							
							If SelectedItem <> Null Then
								e\EventState3 = e\EventState3 + FPSfactor / 5.0
							EndIf							
							
						ElseIf e\EventState3 >= 150.0 And e\EventState3 < 700.0
							If e\room\NPC[3]\State = 7.0 Then
								If e\room\NPC[3]\Sound2 = 0
									e\room\NPC[3]\Sound2 = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\BeforeDoorOpen.ogg")
									e\room\NPC[3]\SoundCHN2 = PlaySound2(e\room\NPC[3]\Sound2, Camera, e\room\NPC[3]\Collider)
								EndIf
								UpdateSoundOrigin(e\room\NPC[3]\SoundCHN2, Camera, e\room\NPC[3]\Collider)
								
								If ChannelPlaying(e\room\NPC[3]\SoundCHN2) = False Then
									e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\ExitCell.ogg")
									e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
									
									e\room\NPC[3]\State = 9.0
									e\room\NPC[4]\State = 9.0
									e\room\NPC[5]\State = 9.0
									
									e\room\RoomDoors[6]\Locked = False		
									UseDoor(e\room\RoomDoors[6], False)
									e\room\RoomDoors[6]\Locked = True
								EndIf
							Else
								If e\room\NPC[3]\Sound2 <> 0 Then
									FreeSound_Strict(e\room\NPC[3]\Sound2) : e\room\NPC[3]\Sound2 = 0
								EndIf
								
								e\EventState3 = Min(e\EventState3 + FPSfactor / 4.0, 699.0)
								
								; ~ Outside the cell
								If Distance(EntityX(Collider), EntityZ(Collider), PlayerRoom\x - (3072.0 + 1024.0) * RoomScale, PlayerRoom\z + 192.0 * RoomScale) > 1.5 Then
									If e\EventState3 > 250.0 Then
										If e\room\NPC[3]\SoundCHN <> 0 Then
											If ChannelPlaying(e\room\NPC[3]\SoundCHN) Then StopChannel(e\room\NPC[3]\SoundCHN)
										EndIf
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\Escort" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
										
										e\room\NPC[3]\PathStatus = FindPath(e\room\NPC[3], PlayerRoom\x - 320.0 * RoomScale, 0.3, PlayerRoom\z - 704.0 * RoomScale)
										e\room\NPC[4]\PathStatus = FindPath(e\room\NPC[4], PlayerRoom\x - 320.0 * RoomScale, 0.3, PlayerRoom\z - 704.0 * RoomScale)
										
										e\room\RoomDoors[6]\Locked = False		
									    UseDoor(e\room\RoomDoors[6], False)
									    e\room\RoomDoors[6]\Locked = True
										
										e\EventState3 = 710.0
									EndIf
								Else ; ~ Inside the cell
									e\room\NPC[3]\State = 9.0
									
									If e\EventState3 - (FPSfactor / 4.0) < 350.0 And e\EventState3 >= 350.0 Then
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\ExitCellRefuse" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
									ElseIf e\EventState3 - (FPSfactor / 4.0) < 550.0 And e\EventState3 >= 550.0 
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\CellGas" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
									ElseIf e\EventState3 > 630.0
										PositionEntity(Collider, EntityX(Collider), EntityY(Collider), Min(EntityZ(Collider), EntityZ(e\room\OBJ, True) + 490.0 * RoomScale))
										If e\room\RoomDoors[6]\Open = True Then 
											e\room\RoomDoors[6]\Locked = False		
											UseDoor(e\room\RoomDoors[6], False)
											e\room\RoomDoors[6]\Locked = True
											
											em.Emitters = CreateEmitter(PlayerRoom\x - (2976.0 + 1024.0) * RoomScale, 373.0 * RoomScale, PlayerRoom\z + 204.0 * RoomScale, 0)
											TurnEntity(em\OBJ, 90.0, 0.0, 0.0, True)
											em\RandAngle = 7.0
											em\Speed = 0.03
											em\SizeChange = 0.003
											em\Room = PlayerRoom
											
											em.Emitters = CreateEmitter(PlayerRoom\x - (3168.0 + 1024.0) * RoomScale, 373.0 * RoomScale, PlayerRoom\z + 204.0 * RoomScale, 0)
											TurnEntity(em\OBJ, 90.0, 0.0, 0.0, True)
											em\RandAngle = 7.0
											em\Speed = 0.03
											em\SizeChange = 0.003
											em\Room = PlayerRoom
										EndIf
										EyeIrritation = Max(EyeIrritation + FPSfactor * 4.0, 1.0)
									EndIf
								EndIf
							EndIf
						ElseIf e\EventState3 < 800.0
							e\EventState3 = e\EventState3 + FPSfactor / 4.0
							If e\room\NPC[5]\State <> 11.0
								If EntityDistance(e\room\NPC[3]\Collider, e\room\NPC[5]\Collider) > 5.0 And EntityDistance(e\room\NPC[4]\Collider, e\room\NPC[5]\Collider)
									If EntityDistance(e\room\NPC[5]\Collider, Collider) < 3.5
										e\room\NPC[3]\State = 11.0
										e\room\NPC[4]\State = 11.0
										e\room\NPC[5]\State = 11.0
										e\room\NPC[3]\State3 = 1.0
										e\room\NPC[4]\State3 = 1.0
										e\room\NPC[5]\State3 = 1.0
										e\room\NPC[3]\Reload = 70 * 3.0
										e\room\NPC[4]\Reload = 70 * 3.0
										e\room\NPC[5]\Reload = 70 * 3.0
										
										For i = 3 To 4
											If ChannelPlaying(e\room\NPC[i]\SoundCHN) Then StopChannel(e\room\NPC[i]\SoundCHN)
											If e\room\NPC[i]\Sound <> 0 Then
												FreeSound_Strict(e\room\NPC[i]\Sound) : e\room\NPC[i]\Sound = 0
											EndIf
										Next
										e\room\NPC[5]\SoundCHN2 = PlaySound2(e\room\NPC[5]\Sound2, Camera, e\room\NPC[5]\Collider)
									EndIf
								EndIf
							EndIf
						ElseIf e\EventState3 < 900.0
							e\room\NPC[4]\Angle = 0.0
							
							If EntityX(Collider) < EntityX(e\room\OBJ, True) - 5376.0 * RoomScale And e\EventStr = "" Then
								If Rand(3) = 1 Then
									e\EventStr = "Scripted\Scripted" + Rand(1, 5) + ".ogg|Off.ogg|"
								Else
									; ~ Generate the PA message
									e\EventStr = "1\Attention" + Rand(1, 2) + ".ogg"
									Select Rand(3)
										Case 1
											StrTemp = "Crew"
											e\EventStr = e\EventStr + "|2\Crew" + Rand(0, 5) + ".ogg"
										Case 2
											StrTemp = "Scientist"
											e\EventStr = e\EventStr + "|2\Scientist" + Rand(0, 19) + ".ogg"
										Case 3
											StrTemp = "Security"	
											e\EventStr = e\EventStr + "|2\security" + Rand(0, 5) + ".ogg"
									End Select
									If Rand(2) = 1 And StrTemp = "Scientist" Then ;Call on line...
										e\EventStr = e\EventStr + "|3\CallOnLine.ogg"
										
										e\EventStr = e\EventStr + "|Numbers\" + Rand(1, 9) + ".ogg"
										If Rand(2) = 1 Then e\EventStr = e\EventStr + "|Numbers\" + Rand(1, 9) + ".ogg"
									Else
										e\EventStr = e\EventStr + "|3\Report" + Rand(0, 1) + ".ogg"
										
										Select StrTemp
											Case "Crew"
												e\EventStr = e\EventStr + "|4\Crew" + Rand(0, 6) + ".ogg"
												If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Crew" + Rand(0, 6) + ".ogg"
											Case "Scientist"
												e\EventStr = e\EventStr + "|4\Scientist" + Rand(0, 7) + ".ogg"
												If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Scientist0.ogg"
											Case "Security"
												e\EventStr = e\EventStr + "|4\Security" + Rand(0, 5) + ".ogg"
												If Rand(2) = 1 Then e\EventStr = e\EventStr + "|5\Security" + Rand(1, 2) + ".ogg"
										End Select
									EndIf
									e\EventStr = e\EventStr + "|Off.ogg|"
								EndIf
							EndIf
							
							If e\room\NPC[6] <> Null Then
								If e\room\NPC[6]\State = 0.0 Then 
									If e\room\RoomDoors[7]\Open Then 
										If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\OBJ, True) - 3328.0 * RoomScale, EntityZ(e\room\OBJ, True) - 1232.0 * RoomScale) < 5.0 Then
											e\room\NPC[6]\State = 1.0
											If e\EventStr = "Done" Then 
												If e\Sound <> 0 Then
													FreeSound_Strict(e\Sound) : e\Sound = 0
												EndIf
												
												e\Sound = LoadSound_Strict("SFX\Room\Intro\PA\scripted\announcement" + Rand(1, 7) + ".ogg")
												e\SoundCHN = PlaySound_Strict(e\Sound)
											EndIf
										EndIf
									EndIf
								Else
									If EntityZ(e\room\NPC[6]\Collider) > EntityZ(e\room\OBJ, True) - 64.0 * RoomScale Then
										RotateEntity(e\room\NPC[6]\Collider, 0.0, CurveAngle(90.0, EntityYaw(e\room\NPC[6]\Collider), 15.0), 0.0)
										If e\room\RoomDoors[7]\Open Then 
											e\room\RoomDoors[7]\Locked = False
											UseDoor(e\room\RoomDoors[7], False)
											e\room\RoomDoors[7]\Locked = True
										EndIf
										If e\room\RoomDoors[7]\OpenState < 1.0 Then e\room\NPC[6]\State = 0.0
									EndIf
								EndIf
							EndIf
							
							If e\room\NPC[8] <> Null Then
								If e\room\NPC[8]\State = 7.0 Then
									If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\OBJ, True) - 6688.0 * RoomScale, EntityZ(e\room\OBJ, True) - 1252.0 * RoomScale) < 2.5 Then
										e\room\NPC[8]\State = 10.0
										e\room\NPC[9]\State = 1.0
										e\room\NPC[10]\State = 10.0
									EndIf
								Else
									If EntityX(e\room\NPC[8]\Collider) < EntityX(e\room\obj, True) - 7100.0 * RoomScale Then
										For i = 8 To 10
											If e\room\NPC[i] <> Null Then RemoveNPC(e\room\NPC[i])
										Next
									EndIf
								EndIf
							EndIf
							
							e\room\NPC[5]\SoundCHN = LoopSound2(e\room\NPC[5]\Sound, e\room\NPC[5]\SoundCHN, Camera, e\room\NPC[5]\OBJ, 2.0, 0.5)
							
							If e\EventStr <> "" And e\EventStr <> "Done" Then
								If e\SoundCHN = 0 Then 
									e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Room\Intro\PA\On.ogg"))
								EndIf
								If ChannelPlaying(e\SoundCHN) = False Then
									StrTemp = Left(e\EventStr, Instr(e\EventStr, "|", 1) - 1)
									e\Sound = LoadSound_Strict("SFX\Room\Intro\PA\" + StrTemp)
									e\SoundCHN = PlaySound_Strict(e\Sound)
									e\EventStr = Right(e\EventStr, Len(e\EventStr) - Len(StrTemp) - 1)
									If e\EventStr = "" Then 
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										Temp = Rand(1, 5)
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Conversation" + Temp + "a.ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
										e\room\NPC[4]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Conversation" + Temp + "b.ogg")
										e\room\NPC[4]\SoundCHN = PlaySound2(e\room\NPC[4]\Sound, Camera, e\room\NPC[4]\Collider)
										e\EventStr = "Done"
									EndIf
								EndIf
							EndIf
							
							Dist = Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider))
							
							If Dist < 3.0 Then
								e\room\NPC[3]\State3 = Min(Max(e\room\NPC[3]\State3 - FPSfactor, 0.0), 50.0)
							Else
								e\room\NPC[3]\State3 = Max(e\room\NPC[3]\State3 + FPSfactor, 50.0)
								If e\room\NPC[3]\State3 >= 70 * 8.0 And e\room\NPC[3]\State3 - FPSfactor < 70 * 8.0 And e\room\NPC[3]\State = 7.0 Then
									If e\room\NPC[4]\SoundCHN <> 0 Then
										If ChannelPlaying(e\room\NPC[4]\SoundCHN) Then StopChannel(e\room\NPC[4]\SoundCHN)
									EndIf
									
									If e\room\NPC[3]\State2 < 2.0 Then
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\EscortRefuse" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
										e\room\NPC[3]\State3 = 50.0
										e\room\NPC[3]\State2 = 3.0
									ElseIf e\room\NPC[3]\State2 = 3.0
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\EscortPissedOff" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
										e\room\NPC[3]\State3 = 50.0
										e\room\NPC[3]\State2 = 4.0
									ElseIf e\room\NPC[3]\State2 = 4.0
										If e\room\NPC[3]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
										EndIf
										
										e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\EscortKill" + Rand(1, 2) + ".ogg")
										e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
										e\room\NPC[3]\State3 = 50.0 + 70.0 * 2.5
										e\room\NPC[3]\State2 = 5.0
									ElseIf e\room\NPC[3]\State2 = 5.0
										e\room\NPC[3]\State = 11.0
										e\room\NPC[4]\State = 11.0
										e\room\NPC[5]\State = 11.0
										e\room\NPC[3]\State3 = 1.0
										e\room\NPC[4]\State3 = 1.0
										e\room\NPC[5]\State3 = 1.0
									EndIf
								EndIf
								If e\room\NPC[5]\State <> 11.0
									If EntityDistance(e\room\NPC[3]\Collider, e\room\NPC[5]\Collider) > 5.0 And EntityDistance(e\room\NPC[4]\Collider, e\room\NPC[5]\Collider)
										If EntityDistance(e\room\NPC[5]\Collider, Collider) < 3.5
											e\room\NPC[3]\State = 11.0
											e\room\NPC[4]\State = 11.0
											e\room\NPC[5]\State = 11.0
											e\room\NPC[3]\State3 = 1.0
											e\room\NPC[4]\State3 = 1.0
											e\room\NPC[5]\State3 = 1.0
											e\room\NPC[3]\Reload = 70 * 3.0
											e\room\NPC[4]\Reload = 70 * 3.0
											e\room\NPC[5]\Reload = 70 * 3.0
											
											For i = 3 To 4
												If ChannelPlaying(e\room\NPC[i]\SoundCHN) Then StopChannel(e\room\NPC[i]\SoundCHN)
												If e\room\NPC[i]\Sound <> 0 Then
													FreeSound_Strict(e\room\NPC[i]\Sound) : e\room\NPC[i]\Sound = 0
												EndIf
											Next
											e\room\NPC[5]\SoundCHN2 = PlaySound2(e\room\NPC[5]\Sound2, Camera, e\room\NPC[5]\Collider)
										EndIf
									EndIf
								EndIf
							EndIf
							
							If e\room\NPC[5]\State = 11.0
								UpdateSoundOrigin(e\room\NPC[5]\SoundCHN2, Camera, e\room\NPC[5]\Collider)
							EndIf
							
							If e\room\NPC[3]\State <> 11.0 Then
								If Dist < Min(Max(4.0 - e\room\NPC[3]\State3 * 0.05, 1.5), 4.0) Then
									If e\room\NPC[3]\PathStatus <> 1 Then
										e\room\NPC[3]\State = 7.0
										PointEntity(e\room\NPC[3]\OBJ, Collider)
										RotateEntity(e\room\NPC[3]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[3]\OBJ), EntityYaw(e\room\NPC[3]\Collider), 20.0), 0.0, True)
										
										If e\room\NPC[3]\PathStatus = 2 Then
											e\room\NPC[3]\PathStatus = FindPath(e\room\NPC[3], PlayerRoom\x - 320.0 * RoomScale, 0.3, PlayerRoom\z - 704.0 * RoomScale)
											e\room\NPC[4]\PathStatus = FindPath(e\room\NPC[4], PlayerRoom\x - 320.0 * RoomScale, 0.3, PlayerRoom\z - 704.0 * RoomScale)
											e\room\NPC[3]\State = 3.0
										EndIf
									Else
										e\room\NPC[3]\State = 3.0
									EndIf
								Else
									e\room\NPC[3]\State = 7.0
									PointEntity(e\room\NPC[3]\OBJ, Collider)
									RotateEntity(e\room\NPC[3]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[3]\OBJ), EntityYaw(e\room\NPC[3]\Collider), 20.0), 0.0, True)	
									
									If Dist > 5.5 Then
										e\room\NPC[3]\PathStatus = 2
										If e\room\NPC[3]\State2 = 0.0 Then
											If e\room\NPC[3]\Sound <> 0 Then
												FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
											EndIf
											
											e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\EscortRun.ogg")
											e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
											PlaySound2(e\Sound, Camera, e\room\NPC[3]\Collider)
											
											e\room\NPC[3]\State2 = 1.0
										EndIf
										
										e\room\NPC[3]\State = 5.0
										e\room\NPC[3]\EnemyX = EntityX(Collider)
										e\room\NPC[3]\EnemyY = EntityY(Collider)
										e\room\NPC[3]\EnemyZ = EntityZ(Collider)
									EndIf
								EndIf	
								
								Dist = EntityDistance(Collider, e\room\NPC[4]\Collider)
								
								If Dist > 1.5 And EntityDistance(e\room\NPC[3]\Collider, Collider) < EntityDistance(e\room\NPC[3]\Collider, e\room\NPC[4]\Collider) Then
									e\room\NPC[4]\State = 3.0
								Else
									e\room\NPC[4]\State = 5.0
									e\room\NPC[4]\EnemyX = EntityX(Collider)
									e\room\NPC[4]\EnemyY = EntityY(Collider)
									e\room\NPC[4]\EnemyZ = EntityZ(Collider)
								EndIf
							EndIf
							
							Dist = Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\RoomDoors[2]\FrameOBJ, True), EntityZ(e\room\RoomDoors[2]\FrameOBJ, True))
							
							If Distance(EntityX(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider), EntityX(e\room\RoomDoors[2]\frameobj,True), EntityZ(e\room\RoomDoors[2]\FrameOBJ, True)) < 4.5 And Dist < 5.0 Then
								e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
								e\room\NPC[0]\Angle = 180.0
								
								If ChannelPlaying(e\room\NPC[7]\SoundCHN) Then StopChannel(e\room\NPC[7]\SoundCHN)
								If e\room\NPC[7]\Sound <> 0 Then
									FreeSound_Strict(e\room\NPC[7]\Sound) : e\room\NPC[7]\Sound = 0	
								EndIf
								
								FreeEntity(e\room\Objects[9]) : e\room\Objects[9] = 0
								FreeEntity(e\room\Objects[10]) : e\room\Objects[10] = 0
								
								If e\room\NPC[5] <> Null Then
									RemoveNPC(e\room\NPC[5])
								EndIf
								If e\room\NPC[7] <> Null Then
									RemoveNPC(e\room\NPC[7])
								EndIf
								
								If e\room\NPC[3]\Sound <> 0 Then
									FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
								EndIf
									
								e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\EscortDone" + Rand(1, 5) + ".ogg")
								e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
								
								PositionEntity(e\room\NPC[6]\Collider, EntityX(e\room\OBJ, True) - 1190.0 * RoomScale, 450.0 * RoomScale, EntityZ(e\room\OBJ, True) + 456.0 * RoomScale, True)
								ResetEntity(e\room\NPC[6]\Collider)
								PointEntity(e\room\NPC[6]\Collider, e\room\OBJ)
								e\room\NPC[6]\CurrSpeed = 0.0
								e\room\NPC[6]\State = 0.0
								
								e\EventState3 = 905.0
								
								e\room\RoomDoors[3]\Locked = False
								UseDoor(e\room\RoomDoors[3], False)
								e\room\RoomDoors[3]\Locked = True
								
								e\room\NPC[1] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[1], True), 0.5, EntityZ(e\room\Objects[1], True))
								PointEntity(e\room\NPC[1]\Collider, e\room\Objects[5])
								
								e\room\NPC[2] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[2], True), 0.5, EntityZ(e\room\Objects[2], True))
								PointEntity(e\room\NPC[2]\Collider, e\room\Objects[5])
								ChangeNPCTextureID(e\room\NPC[2], 6)
								
								PositionEntity(Curr173\Collider, EntityX(e\room\Objects[5], True), 0.5, EntityZ(e\room\Objects[5], True))
								ResetEntity(Curr173\Collider)
								
								e\room\NPC[3]\State = 9.0	
								e\room\NPC[4]\State = 9.0
							EndIf
						ElseIf e\EventState3 =< 905.0
							If ChannelPlaying(e\room\NPC[3]\SoundCHN) = False And e\room\NPC[3]\Frame < 358.0 Then
								e\room\NPC[3]\State = 8.0
								If e\room\NPC[3]\Sound <> 0 Then
									FreeSound_Strict(e\room\NPC[3]\Sound) : e\room\NPC[3]\Sound = 0
								EndIf
								
								e\room\NPC[3]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Ulgrin\OhAndByTheWay.ogg")
								e\room\NPC[3]\SoundCHN = PlaySound2(e\room\NPC[3]\Sound, Camera, e\room\NPC[3]\Collider)
								SetNPCFrame(e\room\NPC[3], 358.0)
							ElseIf e\room\NPC[3]\Frame >= 358.0 Then
								PointEntity(e\room\NPC[3]\Collider, Collider)
								RotateEntity(e\room\NPC[3]\Collider, 0.0, EntityYaw(e\room\NPC[3]\Collider), 0.0)
								
								If e\room\NPC[3]\Frame =< 481.5 Then
									Local PrevAnimFrame# = e\room\NPC[3]\Frame
									
									AnimateNPC(e\room\NPC[3], 358.0, 482.0, 0.4, False)
								Else
									AnimateNPC(e\room\NPC[3], 483.0, 607.0, 0.2, True)
									If (EntityDistance(Collider, e\room\NPC[3]\Collider) < 1.5) Then
										If EntityInView(e\room\NPC[3]\OBJ, Camera) Then
											DrawHandIcon = True
											
											If MouseHit1 Then
												SelectedItem = CreateItem("Document SCP-173", "paper", 0.0, 0.0, 0.0)
												EntityType(SelectedItem\collider, HIT_ITEM)
												
												PickItem(SelectedItem)
												
												e\room\RoomDoors[2]\Locked = False
												UseDoor(e\room\RoomDoors[2], False)
												e\room\RoomDoors[2]\Locked = True
												e\EventState3 = 910.0
												SetNPCFrame(e\room\NPC[3], 608.0)
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						Else
							If e\room\NPC[3]\Frame =< 620.5 And e\room\NPC[3]\State = 8.0 Then
								AnimateNPC(e\room\NPC[3], 608.0, 621.0, 0.4, False)
							Else
								e\room\NPC[3]\Angle = EntityYaw(e\room\NPC[3]\Collider)
								e\room\NPC[3]\State = 9.0
								e\room\NPC[4]\State = 9.0
								If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\OBJ), EntityZ(e\room\OBJ)) < 4.0 Then
									e\room\RoomDoors[2]\Locked = False
									UseDoor(e\room\RoomDoors[2], False)
									e\room\RoomDoors[2]\Locked = True
									e\EventState3 = 0.0
									e\room\NPC[3]\State = 0.0
									e\room\NPC[4]\State = 0.0
									
									UseDoor(e\room\RoomDoors[1], False)
								EndIf
							EndIf
						EndIf
						
						If e\room\NPC[7] <> Null Then
							RotateEntity(e\room\NPC[7]\Collider, 0.0, 180.0 + Sin(MilliSecs2() / 20.0) * 3.0, 0.0, True)
							PositionEntity(e\room\NPC[7]\Collider, EntityX(e\room\OBJ, True) - 3361.0 * RoomScale, (-315.0) * RoomScale, EntityZ(e\room\OBJ, True) - 2165.0 * RoomScale)
							ResetEntity(e\room\NPC[7]\Collider)
							
							e\room\NPC[7]\State = 6.0
							SetNPCFrame(e\room\NPC[7], 182.0)
							
							If e\room\NPC[6]\State = 1.0 And e\room\NPC[7]\Sound <> 0 Then 
								If ChannelPlaying(e\room\NPC[7]\SoundCHN) = False Then StopChannel(e\room\NPC[7]\SoundCHN)
								If e\room\NPC[7]\Sound <> 0 Then 
									FreeSound_Strict(e\room\NPC[7]\Sound) : e\room\NPC[7]\Sound = 0	
								EndIf
								
								If e\room\NPC[7]\Sound <> 0 Then e\room\NPC[7]\SoundCHN = LoopSound2(e\room\NPC[7]\Sound, e\room\NPC[7]\SoundCHN, Camera, e\room\NPC[7]\Collider, 7.0)
							EndIf
						EndIf
					Else
						If IntroSFX(4) <> 0 Then e\SoundCHN2 = LoopSound2(IntroSFX(4), e\SoundCHN2, Camera, e\room\Objects[4], 6.0)
						
						If e\EventState = 0.0 Then
							If PlayerRoom = e\room Then
								For i = 0 To 1
									IntroSFX(i) = LoadSound_Strict("SFX\Room\Intro\Ew" + (i + 1) + ".ogg")
								Next
								IntroSFX(2) = LoadSound_Strict("SFX\Room\Intro\Horror.ogg")
								IntroSFX(3) = LoadSound_Strict("SFX\Room\Intro\See173.ogg")
								IntroSFX(4) = LoadSound_Strict("SFX\Room\Intro\173Chamber.ogg")
								
								Curr173\Idle = True
								
								e\room\NPC[3] = CreateNPC(NPCtypeGuard, e\room\x - 4096.0 * RoomScale + Rnd(-0.3, 0.3), 0.3, e\room\z + Rnd(860.0, 896.0) * RoomScale)
								RotateEntity(e\room\NPC[3]\Collider, 0.0, e\room\Angle + 180.0, 0.0)
								e\room\NPC[3]\State = 7.0
								
								e\room\NPC[4] = CreateNPC(NPCtypeGuard, e\room\x - 3840.0 * RoomScale, 0.3, e\room\z + 768.0 * RoomScale)
								RotateEntity(e\room\NPC[4]\Collider, 0.0, e\room\Angle + 135.0, 0.0)
								e\room\NPC[4]\State = 7.0
								
								e\room\NPC[5] = CreateNPC(NPCtypeGuard, e\room\x - 8288.0 * RoomScale, 0.3, e\room\z + 1096.0 * RoomScale)
								e\room\NPC[5]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Music" + Rand(1, 5) + ".ogg")
								RotateEntity(e\room\NPC[5]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
								e\room\NPC[5]\State = 7.0
								e\room\NPC[5]\Sound2 = LoadSound_Strict("SFX\Room\Intro\Guard\PlayerEscape.ogg")
								e\room\NPC[5]\UseHeadphones = True
								
								e\room\NPC[6] = CreateNPC(NPCtypeD, e\room\x - 3712.0 * RoomScale, -0.3, e\room\z - 2208.0 * RoomScale)
								ChangeNPCTextureID(e\room\NPC[6], 3)
								
								e\room\NPC[7] = CreateNPC(NPCtypeD, e\room\x - 3712.0 * RoomScale, -0.3, e\room\z - 2208.0 * RoomScale)
								e\room\NPC[7]\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Conversation.ogg")
								ChangeNPCTextureID(e\room\NPC[7], 2)
								PositionEntity(Collider, PlayerRoom\x - (3072.0 + 1024.0) * RoomScale, 0.3, PlayerRoom\z + 192.0 * RoomScale)
								ResetEntity(Collider)
								
								Pvt = CreatePivot()
								RotateEntity(Pvt, 90.0, 0.0, 0.0)
								
								e\room\NPC[8] = CreateNPC(NPCtypeGuard, e\room\x - 3800.0 * RoomScale, 1.0, e\room\z - 3900.0 * RoomScale)
								e\room\NPC[8]\State = 7.0
								
								e\room\NPC[9] = CreateNPC(NPCtypeD, e\room\x - 4000.0 * RoomScale, 1.1, e\room\z - 3900.0 * RoomScale)
								e\room\NPC[9]\State2 = 1.0
								ChangeNPCTextureID(e\room\NPC[9], 8)
								
								e\room\NPC[10] = CreateNPC(NPCtypeGuard, e\room\x - 4200.0 * RoomScale, 1.0, e\room\z - 3900.0 * RoomScale)
								e\room\NPC[10]\State = 7.0
									
								For i = 8 To 10
									PositionEntity(Pvt, EntityX(e\room\NPC[i]\Collider), EntityY(e\room\NPC[i]\Collider), EntityZ(e\room\NPC[i]\Collider))
									EntityPick(Pvt, 20.0)
									If PickedEntity() <> 0
										PositionEntity(e\room\NPC[i]\Collider, PickedX(), PickedY(), PickedZ(), True)
										AlignToVector(e\room\NPC[i]\Collider, -PickedNX(), -PickedNY(), -PickedNZ(), 3.0)
										RotateEntity(e\room\NPC[i]\Collider, 0.0, 90.0, 0.0)
									EndIf
								Next
								FreeEntity(Pvt)
								
								e\EventState = 1.0
								e\EventState3 = 1.0
							EndIf
						ElseIf e\EventState < 10000.0
							If e\room\NPC[6]\SoundCHN <> 0 Then 
								If ChannelPlaying(e\room\NPC[6]\SoundCHN) = True Then
									e\room\NPC[6]\State = 6.0
									If AnimTime(e\room\NPC[6]\OBJ) >= 325.0 Then
										Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 326.0, 328.0, 0.02, False)
									Else
										Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 320.0, 328.0, 0.05, False)
									EndIf
								Else
									Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 328.0, 320.0, -0.02, False)
								EndIf
							EndIf
							
							If IntroSFX(3) <> 0 Then
								If EntityVisible(Curr173\Collider, Collider) Then
									If EntityInView(Curr173\OBJ, Camera) Then
										Msg = "Press " + KeyName(KEY_BLINK) + " to blink."
										MsgTimer = 70 * 4.0
										PlaySound_Strict(IntroSFX(3))
										IntroSFX(3) = 0
									EndIf
								EndIf
							EndIf
							
							e\EventState = Min(e\EventState + (FPSfactor / 3.0), 5000.0)
							If e\EventState >= 130.0 And e\EventState - (FPSfactor / 3.0) < 130.0 Then
								If e\Sound <> 0 Then
									FreeSound_Strict(e\Sound) : e\Sound = 0
								EndIf
								
								e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\EnterChamber.ogg")
								e\SoundCHN = PlaySound_Strict(e\Sound)
								
								For i = 3 To 4
									If e\room\NPC[i] <> Null Then
										RemoveNPC(e\room\NPC[i])
									EndIf
								Next
							ElseIf e\EventState > 230.0
								Temp = True
								For i = 1 To 2
									If Distance(EntityX(e\room\NPC[i]\Collider), EntityZ(e\room\NPC[i]\Collider), EntityX(e\room\Objects[i + 2], True), EntityZ(e\room\Objects[i + 2], True)) > 0.3 Then
										PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[i + 2])
										RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\obj), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
										If e\EventState > (200.0 + i * 30.0) Then e\room\NPC[i]\State = 1.0
										Temp = False
									Else
										e\room\NPC[i]\State = 0.0
										
										PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[5])
										RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
									EndIf
								Next
								
								If EntityX(Collider) < (EntityX(e\room\OBJ)) + 408.0 * RoomScale Then
									If e\EventState >= 450.0 And e\EventState - (FPSfactor / 3.0) < 450.0 Then
										If e\Sound <> 0 Then
											FreeSound_Strict(e\Sound) : e\Sound = 0
										EndIf
										
										e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\Refuse1.ogg")
										e\SoundCHN = PlaySound_Strict(e\Sound)
									ElseIf e\EventState >= 650.0 And e\EventState - (FPSfactor / 3.0) < 650.0
										If e\Sound <> 0 Then
											FreeSound_Strict(e\Sound) : e\Sound = 0
										EndIf
										
										e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\Refuse2.ogg")
										e\SoundCHN = PlaySound_Strict(e\Sound)
									ElseIf e\EventState >= 850.0 And e\EventState - (FPSfactor / 3.0) < 850.0
										If e\Sound <> 0 Then
											FreeSound_Strict(e\Sound) : e\Sound = 0
										EndIf
										
										e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\Refuse3.ogg")
										e\SoundCHN = PlaySound_Strict(e\Sound)
										
										UseDoor(e\room\RoomDoors[1], False)
									ElseIf e\EventState > 1000.0
										e\room\NPC[0]\State = 1.0
										e\room\NPC[0]\State2 = 10.0
										e\room\NPC[0]\State3 = 1.0
										e\room\NPC[3]\State = 11.0
										e\room\RoomDoors[2]\Locked = False
										UseDoor(e\room\RoomDoors[2], False)
										e\room\RoomDoors[2]\Locked = True
										e\EventState2 = 1.0
										Exit
									EndIf
									
									If e\EventState > 850.0 Then
										PositionEntity(Collider, Min(EntityX(Collider), EntityX(e\room\OBJ) + 352.0 * RoomScale), EntityY(Collider), EntityZ(Collider))
									End If
								ElseIf Temp = True
									e\EventState = 10000.0
									UseDoor(e\room\RoomDoors[1], False)
								End If
							End If
							
							e\room\NPC[6]\State = 7.0
							PointEntity(e\room\NPC[6]\OBJ, Collider)
							RotateEntity(e\room\NPC[6]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[6]\OBJ), EntityYaw(e\room\NPC[6]\Collider), 20.0), 0.0, True)	
							
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[5], True), EntityY(Curr173\Collider), EntityZ(e\room\Objects[5], True))
							RotateEntity(Curr173\Collider, 0.0, 0.0, 0.0, True)
							ResetEntity(Curr173\Collider)
						ElseIf e\EventState < 14000.0 ; ~ Player is inside the room
							e\EventState = Min(e\EventState + FPSfactor, 13000.0)
							
							For i = 1 To 2
								If Distance(EntityX(e\room\NPC[i]\Collider), EntityZ(e\room\NPC[i]\Collider), EntityX(e\room\Objects[i + 2], True), EntityZ(e\room\Objects[i + 2], True)) < 0.3 Then
									PointEntity(e\room\NPC[i]\OBJ, e\room\Objects[5])
									RotateEntity(e\room\NPC[i]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[i]\OBJ), EntityYaw(e\room\NPC[i]\Collider), 15.0), 0.0)
								EndIf
							Next
							
							If e\EventState < 10300.0 Then
								PositionEntity(Collider, Max(EntityX(Collider), EntityX(e\room\OBJ) + 352.0 * RoomScale), EntityY(Collider), EntityZ(Collider))
							End If
							
							e\room\NPC[6]\State = 6.0
							PointEntity(e\room\NPC[6]\OBJ, Curr173\Collider)
							RotateEntity(e\room\NPC[6]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[6]\OBJ), EntityYaw(e\room\NPC[6]\Collider), 50.0), 0.0, True)	
							
							If e\EventState >= 10300.0 And e\EventState - FPSfactor < 10300.0 Then
								If e\Sound <> 0 Then
									FreeSound_Strict(e\Sound) : e\Sound = 0
								EndIf
								
								e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\Approach173.ogg")
								e\SoundCHN = PlaySound_Strict(e\Sound)
								
								PositionEntity(Collider, Max(EntityX(Collider), EntityX(e\room\OBJ) + 352.0 * RoomScale), EntityY(Collider), EntityZ(Collider))
							ElseIf e\EventState >= 10440.0 And e\EventState - FPSfactor < 10440.0
								UseDoor(e\room\RoomDoors[1], False)
								PlaySound_Strict(IntroSFX(3))
							ElseIf e\EventState >= 10740.0 And e\EventState - FPSfactor < 10740.0
								If e\Sound <> 0 Then
									FreeSound_Strict(e\Sound) : e\Sound = 0
								EndIf
								
								e\Sound = LoadSound_Strict("SFX\Room\Intro\Scientist\Franklin\Problem.ogg")
								e\SoundCHN = PlaySound_Strict(e\Sound)
							ElseIf e\EventState >= 11145.0 And e\EventState - FPSfactor < 11145.0
								PlaySound_Strict(IntroSFX(Rand(8, 10)))
								e\room\NPC[1]\Sound = LoadSound_Strict("SFX\Room\Intro\ClassD\DontLikeThis.ogg")
								e\room\NPC[1]\SoundCHN = PlaySound2(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider)
							ElseIf e\EventState >= 11561.0 And e\EventState - FPSfactor < 11561.0
								e\EventState = 14000.0
								PlaySound_Strict(IntroSFX(2))
								
								e\room\NPC[2]\Sound = LoadSound_Strict("SFX\Room\Intro\ClassD\Breen.ogg")
								e\room\NPC[2]\SoundCHN = PlaySound2(e\room\NPC[2]\Sound, Camera, e\room\NPC[2]\Collider)
							End If
							
							If e\EventState >= 10440.0 And e\EventState - FPSfactor < 11561.0 Then
								If EntityX(Collider) < EntityX(e\room\RoomDoors[1]\FrameOBJ, True)
									If e\room\NPC[0]\State <> 12.0
										e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Balcony\Alert" + Rand(1,2 ) + ".ogg")
										e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0)
										e\room\NPC[0]\State = 12.0
										e\room\NPC[0]\State2 = 1.0
									EndIf
								EndIf
							EndIf
							
							If e\EventState > 10300.0 Then 
								If e\EventState > 10560.0 Then
									If e\EventState < 10750.0 Then
										e\room\NPC[1]\State = 1.0
										e\room\NPC[1]\CurrSpeed = 0.005										
									Else
										e\room\NPC[1]\State = 0.0
										e\room\NPC[1]\CurrSpeed = CurveValue(0.0, e\room\NPC[1]\CurrSpeed, 10.0)	
									EndIf
								EndIf
								
								If AnimTime(e\room\NPC[6]\OBJ) >= 325.0 Then
									Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 326.0, 328.0, 0.02, False)
								Else
									Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 320.0, 328.0, 0.05, False)
								EndIf
							EndIf
							
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[5], True), EntityY(Curr173\Collider), EntityZ(e\room\Objects[5], True))
							RotateEntity(Curr173\Collider, 0.0, 0.0, 0.0, True)
							ResetEntity(Curr173\Collider)
						ElseIf e\EventState < 20000.0
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(Camera), EntityY(Curr173\Collider, True) - 0.05, EntityZ(Camera))
							PointEntity(Pvt, Curr173\Collider)
							RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(Collider), 40.0), 0.0)
							
							TurnEntity(Pvt, 90.0, 0.0, 0.0)
							User_Camera_Pitch = CurveAngle(EntityPitch(Pvt), User_Camera_Pitch + 90.0, 40.0)
							User_Camera_Pitch = User_Camera_Pitch - 90.0
							FreeEntity(Pvt)
							
							e\room\NPC[6]\State = 6.0
							PointEntity(e\room\NPC[6]\OBJ, Curr173\Collider)
							RotateEntity(e\room\NPC[6]\Collider, 0.0, CurveValue(EntityYaw(e\room\NPC[6]\OBJ), EntityYaw(e\room\NPC[6]\Collider), 20.0), 0.0, True)	
							Animate2(e\room\NPC[6]\OBJ, AnimTime(e\room\NPC[6]\OBJ), 357.0, 381.0, 0.05)
							
							e\EventState = Min(e\EventState + FPSfactor, 19000.0)
							If e\EventState < 14100.0 Then
								If e\EventState < 14060.0 Then
									BlinkTimer = Max((14000.0 - e\EventState) / 2.0 - Rnd(0, 1.0), -10.0)
									If BlinkTimer = -10.0 Then
										PointEntity(Curr173\Collider, e\room\NPC[1]\OBJ)
										RotateEntity(Curr173\Collider, 0.0, EntityYaw(Curr173\Collider), 0)
										MoveEntity(Curr173\Collider, 0.0, 0.0, Curr173\Speed * 0.6 * FPSfactor)
										
										Curr173\SoundCHN = LoopSound2(StoneDragSFX, Curr173\SoundChn, Camera, Curr173\Collider, 10.0, Curr173\State)
										
										Curr173\State = CurveValue(1.0, Curr173\State, 3.0)
									Else
										Curr173\State = Max(0.0, Curr173\State - FPSfactor / 20.0)
									EndIf
								ElseIf e\EventState < 14065.0
									BlinkTimer = -10.0
									If e\room\NPC[1]\State = 0.0 Then PlaySound2(NeckSnapSFX(Rand(0, 2)), Camera, Curr173\Collider)
									
									SetAnimTime(e\room\NPC[1]\OBJ, 0.0)
									e\room\NPC[1]\State = 6.0
									PositionEntity(Curr173\Collider, EntityX(e\room\NPC[1]\OBJ), EntityY(Curr173\Collider), EntityZ(e\room\NPC[1]\OBJ))
									ResetEntity(Curr173\Collider)
									PointEntity(Curr173\Collider, e\room\NPC[2]\Collider)
									
									e\room\NPC[2]\State = 3.0
									RotateEntity(e\room\NPC[2]\Collider, 0.0, EntityYaw(e\room\NPC[2]\Collider), 0.0)
									Animate2(e\room\NPC[2]\OBJ, AnimTime(e\room\NPC[2]\OBJ), 406.0, 382.0, (-0.01) * 15.0)
									MoveEntity(e\room\NPC[2]\Collider, 0.0, 0.0, (-0.01) * FPSfactor)
									
									e\room\NPC[0]\State = 12.0
									
									If ChannelPlaying(e\room\NPC[0]\SoundCHN) = True Then StopChannel(e\room\NPC[0]\SoundCHN)
									If e\room\NPC[0]\Sound <> 0
										FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
									EndIf
									
									e\room\NPC[0]\Angle = 180.0
									e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Balcony\WTF" + Rand(1, 2) + ".ogg")
									e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0)
									e\room\NPC[0]\State2 = 0.0
								Else
									Animate2(e\room\NPC[1]\OBJ, AnimTime(e\room\NPC[1]\OBJ), 0.0, 19.0, 0.2, False)
									
									If e\room\NPC[2]\Sound <> 0 Then
										FreeSound_Strict(e\room\NPC[2]\Sound) : e\room\NPC[2]\Sound = 0
									EndIf
									
									If e\room\NPC[2]\Sound = 0 Then 
										e\room\NPC[2]\Sound = LoadSound_Strict("SFX\Room\Intro\ClassD\Gasp.ogg")
										PlaySound2(e\room\NPC[2]\Sound, Camera, e\room\NPC[2]\Collider, 8.0)	
									EndIf									
								EndIf
								
								If e\EventState > 14080.0 And e\EventState - FPSfactor < 14080.0 Then PlaySound_Strict(IntroSFX(Rand(8, 10)))
								CameraShake = 3.0
							ElseIf e\EventState < 14200.0
								Animate2(e\room\NPC[1]\OBJ, AnimTime(e\room\NPC[1]\OBJ), 0.0, 19.0, 0.2, False)
								
								e\room\NPC[0]\State = 8.0
								If e\EventState > 14105.0 Then
									If e\room\NPC[2]\Sound <> 0 Then
										FreeSound_Strict(e\room\NPC[2]\Sound) : e\room\NPC[2]\Sound = 0
									EndIf
									
									If e\room\NPC[2]\State <> 6.0 Then PlaySound2(NeckSnapSFX(1), Camera, e\room\NPC[2]\Collider, 8.0)
									e\room\NPC[2]\State = 6.0
									PositionEntity(Curr173\Collider, EntityX(e\room\NPC[2]\OBJ), EntityY(Curr173\Collider), EntityZ(e\room\NPC[2]\OBJ))
									ResetEntity(Curr173\Collider)
									PointEntity(Curr173\Collider, Collider)
								EndIf
								If e\EventState < 14130.0 Then 
									SetAnimTime(e\room\NPC[2]\OBJ, 50.0)
									BlinkTimer = -10.0
									LightBlink = 1.0
								Else 
									Animate2(e\room\NPC[2]\OBJ, AnimTime(e\room\NPC[2]\OBJ), 50.0, 60.0, 0.2, False)
									Curr173\Idle = False
								EndIf
								If e\EventState > 14100.0 And e\EventState - FPSfactor < 14100.0 Then PlaySound_Strict(IntroSFX(6))
								If e\EventState < 14150.0 Then CameraShake = 5.0
							Else
								Animate2(e\room\NPC[2]\OBJ, AnimTime(e\room\NPC[2]\OBJ), 45.0, 60.0, 0.2, False)
								If e\EventState > 14300.0 Then 
									If e\EventState > 14600.0 And e\EventState < 14700.0 Then 
										BlinkTimer = -10.0
										LightBlink = 1.0
									EndIf
									If EntityX(Collider) < (EntityX(e\room\OBJ)) + 448.0 * RoomScale Then e\EventState = 20000.0
								EndIf
							End If
						ElseIf e\EventState < 30000.0
							e\EventState = Min(e\EventState + FPSfactor, 30000.0)
							If e\EventState < 20100.0 Then
								CameraShake = 2.0
							Else
								If e\EventState < 20200.0 Then
									If e\EventState > 20105.0 And e\EventState - FPSfactor < 20105.0 Then 
										PlaySound_Strict(IntroSFX(7))
										PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\OBJ) - 160.0 * RoomScale, EntityY(e\room\NPC[0]\Collider) + 0.1, EntityZ(e\room\OBJ) + 1280.0 * RoomScale)
										ResetEntity(e\room\NPC[0]\Collider)										
										
										If ChannelPlaying(e\room\NPC[0]\SoundCHN) = True Then StopChannel(e\room\NPC[0]\SoundCHN)
										If e\room\NPC[0]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
										EndIf
										
										e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\Intro\Guard\Balcony\OhSh.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 20.0)
									EndIf
									If e\EventState > 20105.0 Then
										Curr173\Idle = True
										PointEntity(e\room\NPC[0]\Collider, Curr173\OBJ)
										PositionEntity(Curr173\Collider, EntityX(e\room\OBJ) - 608.0 * RoomScale, EntityY(e\room\OBJ) + 480.0 * RoomScale, EntityZ(e\room\OBJ) + 1312.0 * RoomScale)
										ResetEntity(Curr173\Collider)
										PointEntity(Curr173\Collider, e\room\NPC[0]\Collider)
									EndIf
									BlinkTimer = -10.0
									LightBlink = 1.0
									CameraShake = 3.0
								ElseIf e\EventState < 20300.0
									PointEntity(e\room\NPC[0]\Collider, Curr173\Collider)
									e\room\NPC[0]\State = 2.0
									UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 20.0)
									If e\EventState > 20260.0 And e\EventState - FPSfactor < 20260.0 Then PlaySound_Strict(IntroSFX(Rand(8, 10)))
								Else
									If e\EventState - FPSfactor < 20300.0 Then
										BlinkTimer = -10.0
										LightBlink = 1.0
										CameraShake = 3.0
										PlaySound_Strict(IntroSFX(Rand(8, 10)))
										
										If e\room\NPC[0]\Sound <> 0 Then
											FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
										EndIf
										
										e\room\NPC[0]\SoundCHN = PlaySound2(NeckSnapSFX(1), Camera, e\room\NPC[0]\Collider, 8.0)
										
										Curr173\Idle = False
										
										PlaySound_Strict(IntroSFX(11))
										
										PositionEntity(Curr173\Collider, EntityX(PlayerRoom\OBJ) - 400.0 * RoomScale, 100.0, EntityZ(PlayerRoom\OBJ) + 1072.0 * RoomScale)
										ResetEntity(Curr173\Collider)
										
										For r.Rooms = Each Rooms
											If r\RoomTemplate\Name = "room173" Then
												PlayerRoom = r
												
												x# = EntityX(r\OBJ, True) + 3712.0 * RoomScale
												y# = 384.0 * RoomScale
												z# = EntityZ(r\OBJ, True) + 1312.0 * RoomScale
												
												PositionEntity(Collider, x  + (EntityX(Collider) - EntityX(e\room\OBJ)), y + EntityY(Collider) + 0.4, z + (EntityZ(Collider) - EntityZ(e\room\OBJ)))
												DropSpeed = 0.0
												ResetEntity(Collider)
												
												For i = 0 To 2
													PositionEntity(e\room\NPC[i]\Collider, x + (EntityX(e\room\NPC[i]\Collider) - EntityX(e\room\OBJ)), y + EntityY(e\room\NPC[i]\Collider) + 0.4, z + (EntityZ(e\room\NPC[i]\Collider) - EntityZ(e\room\OBJ)))
													ResetEntity(e\room\NPC[i]\Collider)
												Next
												
												ShouldPlay = 0
												
												For i = 0 To 4
													FreeSound_Strict(IntroSFX(i))
												Next
												
												r\NPC[0] = e\room\NPC[0]
												r\NPC[0]\State = 8
												
												For do.Doors = Each Doors
													If do\room = e\room Then
														RemoveDoor(do)
													EndIf
												Next
												
												For w.Waypoints = Each WayPoints
													If w\room = e\room Then 
														FreeEntity(w\OBJ)
														Delete(w)
													EndIf
												Next
												
												For i = 3 To 4
													RemoveNPC(e\room\NPC[i])
												Next
												r\NPC[1] = e\room\NPC[6]
												
												FreeEntity(e\room\OBJ)
												Delete(e\room)
												
												For sc.SecurityCams = Each SecurityCams
													If sc\room = e\room Then Delete(sc)
												Next
												
												ShowEntity(ov\OverlayID[0])
												AmbientLight(Brightness, Brightness, Brightness)
												CameraFogRange(Camera, CameraFogNear, CameraFogFar)
												CameraFogMode(Camera, 1)
												
												e\EventState2 = 1.0
												
												Exit
											EndIf
										Next
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				Else
					If KillTimer < 0.0 Then
						If e\room\NPC[3] <> Null Then
							If e\room\NPC[3]\State = 1.0 Then 
								LoadEventSound(e, "SFX\Room\Intro\Guard\Ulgrin\EscortTerminated.ogg")
								PlaySound_Strict(e\Sound)
							EndIf
						EndIf
					EndIf
					
					For i = 0 To 4
						If IntroSFX(i) <> 0 Then FreeSound_Strict(IntroSFX(i)) : IntroSFX(i) = 0
					Next
					FreeSound_Strict(IntroSFX(2)) : IntroSFX(2) = 0
					
					e\EventState2 = 1.0
				EndIf
				
				If PlayerRoom = e\room Then
					If e\EventState >= 10.0 Then
						CameraRange(Camera, 0.05, 15.0)
					Else															
						CameraRange(Camera, 0.05, 40.0)
					EndIf	
					CameraFogMode(Camera, 0)
	 	            AmbientLight(140.0, 140.0, 140.0)
	   				HideEntity(ov\OverlayID[0])
					
					LightVolume = 4.0
					TempLightVolume = 4.0			
				Else
					RemoveEvent(e)		
				EndIf	
				;[End Block]
			Case "buttghost"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityDistance(Collider, e\room\Objects[0]) < 1.8 Then
						If e\EventState = 0.0
							GiveAchievement(Achv789J)
							e\SoundCHN = PlaySound2(ButtGhostSFX, Camera, e\room\Objects[0])
							e\EventState = 1.0
						Else
							If ChannelPlaying(e\SoundCHN) = False Then
								RemoveEvent(e)
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "checkpoint"
				;[Block]
				If PlayerRoom = e\room Then
					; ~ Play a sound clip when the player passes through the gate
					If e\EventState2 = 0.0 Then
						If EntityZ(Collider) < e\room\z Then
							If PlayerZone = 1 Then
								PlaySound_Strict(LoadTempSound("SFX\Ambient\ToZone2.ogg"))
							Else
								PlaySound_Strict(LoadTempSound("SFX\Ambient\ToZone3.ogg"))
							EndIf
							e\EventState2 = 1.0
						EndIf
					EndIf
					
					If e\EventState3 = 0.0 Then
						If Rand(2) = 1 Then
							GiveAchievement(Achv1048)
							e\room\Objects[1] = CopyEntity(o\NPCModelID[23])
							ScaleEntity(e\room\Objects[1], 0.05, 0.05, 0.05)
							PositionEntity(e\room\Objects[1], EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
							SetAnimTime(e\room\Objects[1], 267.0)	
						EndIf
						
						e\EventState3 = 1.0
					ElseIf e\room\Objects[1] <> 0
						If e\EventState3 = 1.0 Then
							PointEntity(e\room\Objects[1], Collider)
							RotateEntity(e\room\Objects[1], -90.0, EntityYaw(e\room\Objects[1]), 0.0)
							Angle = WrapAngle(DeltaYaw(Collider, e\room\Objects[1]))
							If Angle < 40.0 Or Angle > 320.0 Then e\EventState3 = 2.0
						ElseIf e\EventState3 = 2.0
							PointEntity(e\room\Objects[1], Collider)
							RotateEntity(e\room\Objects[1], -90.0, EntityYaw(e\room\Objects[1]), 0.0)
							Animate2(e\room\Objects[1], AnimTime(e\room\Objects[1]), 267.0, 283.0, 0.3, False)
							If AnimTime(e\room\Objects[1]) = 283.0 Then e\EventState3 = 3.0
						ElseIf e\EventState3 = 3.0
							Animate2(e\room\Objects[1], AnimTime(e\room\Objects[1]), 283.0, 267.0, -0.2, False)
							If AnimTime(e\room\Objects[1]) = 267.0 Then e\EventState3 = 4.0
						ElseIf e\EventState3 = 4.0
							Angle = WrapAngle(DeltaYaw(Collider, e\room\Objects[1]))
							If Angle > 90 And Angle < 270.0 Then 
								FreeEntity(e\room\Objects[1])
								e\room\Objects[1] = 0
								e\EventState3 = 5.0
							EndIf
						EndIf
					EndIf
				EndIf
				
				If e\room\RoomTemplate\Name = "checkpoint2" Then
					For e2.Events = Each Events
						If e2\EventName = "room008"
							If e2\EventState = 2.0 Then
								If e\room\RoomDoors[0]\Locked Then
									TurnCheckpointMonitorsOff(1)
									e\room\RoomDoors[0]\Locked = False
									e\room\RoomDoors[1]\Locked = False
								EndIf
							Else
								If e\room\Dist < 12.0 Then
									UpdateCheckpointMonitors(1)
									e\room\RoomDoors[0]\Locked = True
									e\room\RoomDoors[1]\Locked = True
								EndIf
							EndIf
						EndIf
					Next
				Else
					For e2.Events = Each Events
						If e2\EventName = "room2sl" Then
							If e2\EventState3 = 0.0 Then
								If e\room\Dist < 12.0 Then
									TurnCheckpointMonitorsOff(0)
									e\room\RoomDoors[0]\Locked = False
									e\room\RoomDoors[1]\Locked = False
								EndIf
							Else
								If e\room\Dist < 12.0 Then
									UpdateCheckpointMonitors(0)
									e\room\RoomDoors[0]\Locked = True
									e\room\RoomDoors[1]\Locked = True
								EndIf
							EndIf
						EndIf
					Next
				EndIf
				
				If e\room\RoomDoors[0]\Open <> e\EventState Then
					If e\Sound = 0 Then LoadEventSound(e, "SFX\Door\DoorCheckpoint.ogg")
					e\SoundCHN = PlaySound2(e\Sound, Camera, e\room\RoomDoors[0]\OBJ)
					e\SoundCHN2 = PlaySound2(e\Sound, Camera, e\room\RoomDoors[1]\OBJ)
				EndIf
				e\EventState = e\room\RoomDoors[0]\Open
				
				If ChannelPlaying(e\SoundCHN)
					UpdateSoundOrigin(e\SoundCHN, Camera, e\room\RoomDoors[0]\OBJ)
				EndIf
				If ChannelPlaying(e\SoundCHN2)
					UpdateSoundOrigin(e\SoundCHN2, Camera, e\room\RoomDoors[1]\OBJ)
				EndIf
				;[End Block]
			Case "room895", "room895_106"
				;[Block]
				If e\EventState < MilliSecs2() Then
					; ~ SCP-079 starts broadcasting 895 camera feed on monitors after leaving the first zone
					If PlayerZone > 0 Then 
						If EntityPitch(e\room\Levers[0], True) > 0.0 Then ; ~ Camera feed on
							For sc.SecurityCams = Each SecurityCams
								If sc\CoffinEffect = 0 And sc\room\RoomTemplate\Name <> "room106" And sc\room\RoomTemplate\Name <> "room205" Then sc\CoffinEffect = 2
								If sc\room = e\room Then sc\Screen = True
							Next
						Else ; ~ Camera feed off
							For sc.SecurityCams = Each SecurityCams
								If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
								If sc\room = e\room Then sc\Screen = False
							Next
						EndIf						
					EndIf
					e\EventState = MilliSecs2() + 3000
				EndIf
				
				If PlayerRoom = e\room Then
					CoffinDistance = EntityDistance(Collider, e\room\Objects[1])
					If CoffinDistance < 1.5 Then 
						GiveAchievement(Achv895)
						If (Not Contained106) And e\EventName = "room895_106" And e\EventState2 = 0.0 Then
							de.Decals = CreateDecal(0, EntityX(e\room\Objects[1], True), -1531.0 * RoomScale, EntityZ(e\room\Objects[1], True), 90.0, Rand(360.0), 0.0)
							de\Size = 0.05 : de\SizeChange = 0.001 : UpdateDecals()
							EntityAlpha(de\OBJ, 0.8)
							
							If Curr106\State > 0.0 Then
								PositionEntity(Curr106\Collider, EntityX(e\room\Objects[1], True), -10240.0 * RoomScale, EntityZ(e\room\Objects[1], True))
								Curr106\State = -0.1
								ShowEntity(Curr106\OBJ)
								e\EventState2 = 1.0
							EndIf
						EndIf
					ElseIf CoffinDistance < 3.0 Then
						If e\room\NPC[0] = Null Then
							e\room\NPC[0] = CreateNPC(NPCtypeGuard, e\room\x, e\room\y, e\room\z)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\angle + 90.0, 0.0)
							e\room\NPC[0]\State = 8.0
							SetNPCFrame(e\room\NPC[0], 270.0)
							e\room\NPC[0]\GravityMult = 0.0
							e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardIdle" + Rand(1, 3) + ".ogg")
							e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider)
							e\room\NPC[0]\IsDead = True
							e\room\NPC[0]\FallingPickDistance = 0.0
							
							If e\room\RoomDoors[0]\Open = False Then e\room\RoomDoors[0]\Open = True
						EndIf
					ElseIf CoffinDistance > 5.0 Then
						If e\room\NPC[0] <> Null Then
							If e\room\NPC[0]\PrevState = 0 Then
								If ChannelPlaying(e\room\NPC[0]\SoundCHN) Then StopChannel(e\room\NPC[0]\SoundCHN)
								If e\room\NPC[0]\Sound <> 0 Then
									FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
								EndIf
								e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Room\895Chamber\GuardScream" + Rand(1, 3) + ".ogg")
								e\room\NPC[0]\SoundCHN = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 100.0)
								e\room\NPC[0]\PrevState = 1
								e\room\NPC[0]\State2 = 0.0
							EndIf
						EndIf
					EndIf
					
					If e\room\NPC[0] <> Null Then
						UpdateSoundOrigin(e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 100.0)
						If e\room\NPC[0]\PrevState = 0 Then
							e\room\NPC[0]\GravityMult = 0.0
						ElseIf e\room\NPC[0]\PrevState = 1 Then
							If e\room\NPC[0]\State2 < 70 * 1.0 Then
								e\room\NPC[0]\State2 = e\room\NPC[0]\State2 + FPSfactor
								e\room\NPC[0]\GravityMult = 0.0
							Else
								e\room\NPC[0]\GravityMult = 1.0
							EndIf
							If EntityY(e\room\NPC[0]\Collider) > (-1531.0 * RoomScale) + 0.35 Then
								Dist = EntityDistance(Collider, e\room\NPC[0]\Collider)
								If Dist < 0.8 Then ; ~ Get the player out of the way
									fdir# = point_direction(EntityX(Collider, True), EntityZ(Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(e\room\NPC[0]\Collider, True))
									TranslateEntity(Collider, Cos(-fdir + 90.0) * (Dist - 0.8) * (Dist - 0.8), 0, Sin(-fdir + 90.0) * (Dist - 0.8) * (Dist - 0.8))
								EndIf
								If EntityY(e\room\NPC[0]\Collider) > 0.6 Then EntityType(e\room\NPC[0]\Collider, 0)
							Else
								e\EventState = e\EventState + FPSfactor
								AnimateNPC(e\room\NPC[0], 270.0, 286.0, 0.4, False)
								If e\Sound = 0 Then
									LoadEventSound(e,"SFX\General\BodyFall.ogg")
									e\SoundCHN = PlaySound_Strict(e\Sound)
									
									de.Decals = CreateDecal(3, EntityX(e\room\OBJ), -1534.0 * RoomScale, EntityZ(e\room\OBJ), 90.0, Rand(360.0), 0.0)
									de\Size = 0.4 : UpdateDecals()
									ScaleSprite(de\obj, de\Size, de\Size)
									
									it = CreateItem("Unknown Note", "paper", EntityX(e\room\NPC[0]\Collider, True), EntityY(e\room\NPC[0]\Collider, True) + 0.04, EntityZ(e\room\NPC[0]\Collider))
									EntityType(it\Collider, HIT_ITEM)
								EndIf
								If e\room\NPC[0]\Frame = 286.0 Then
									e\room\NPC[0]\PrevState = 2
								EndIf
							EndIf
							If e\room\NPC[0]\SoundCHN2 = 0 Then
								e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\Room\895Chamber\GuardRadio.ogg")
								e\room\NPC[0]\SoundCHN2 = LoopSound2(e\room\NPC[0]\Sound2, e\room\NPC[0]\SoundCHN2, Camera, e\room\NPC[0]\Collider, 5)
							EndIf
						ElseIf e\room\NPC[0]\PrevState = 2 Then
							If ChannelPlaying(e\SoundCHN) = False And e\Sound <> 0 Then
								FreeSound_Strict(e\Sound) : e\Sound = 0
								e\SoundCHN = 0
							EndIf
							If ChannelPlaying(e\room\NPC[0]\SoundCHN) = False And e\room\NPC[0]\Sound <> 0 Then
								FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
								e\room\NPC[0]\SoundCHN = 0
							EndIf
							If e\room\NPC[0]\Sound2 = 0 Then
								e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\Room\895Chamber\GuardRadio.ogg")
							EndIf
							e\room\NPC[0]\SoundChn2 = LoopSound2(e\room\NPC[0]\Sound2, e\room\NPC[0]\SoundCHN2, Camera, e\room\NPC[0]\Collider, 5.0)
						EndIf
					EndIf
					
					If WearingNightVision > 0 Then
						Local HasBatteryFor895% = 0
						
						For i = 0 To MaxItemAmount - 1
							If (Inventory(i) <> Null) Then
								If (WearingNightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernv") Or (WearingNightVision = 3 And Inventory(i)\ItemTemplate\TempName = "finenvgoggles") Then
									If Inventory(i)\state > 0.0 Or WearingNightVision = 3 Then
										HasBatteryFor895 = 1
										Exit
									EndIf
								EndIf
							EndIf
						Next
						If (CoffinDistance < 4.0) And (HasBatteryFor895) And Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then
							TempF# = point_direction(EntityX(Collider, True), EntityZ(Collider, True), EntityX(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True))
							TempF2# = EntityYaw(Collider)
							TempF3# = angleDist(TempF + 90.0 + Sin(WrapAngle(e\EventState3 / 10.0)), TempF2)
							TurnEntity(Collider, 0.0, TempF3 / 4.0, 0.0, True)
							TempF# = Abs(point_distance(EntityX(Collider, True), EntityZ(Collider, True), EntityX(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True)))
							TempF2# = (-60.0) * Min(Max((2.0 - TempF) / 2.0, 0.0), 1.0)
							User_Camera_Pitch = (User_Camera_Pitch * 0.8) + (TempF2 * 0.2)
							
							Sanity = Sanity - (FPSfactor * 1.1 / WearingNightVision)
							RestoreSanity = False
							BlurTimer = Sin(MilliSecs2() / 10) * Abs(Sanity)
							
							If VomitTimer < 0.0 Then
								RestoreSanity = False
								Sanity = -1010.0
							EndIf
							
							If Sanity < -1000.0 Then
								If WearingNightVision > 0
									DeathMSG = Chr(34) + "Class D viewed SCP-895 through a pair of digital night vision goggles, presumably enhanced by SCP-914. It might be possible that the subject "
									DeathMSG = DeathMSG + "was able to resist the memetic effects partially through these goggles. The goggles have been stored for further study." + Chr(34)
								Else
									DeathMSG = Chr(34) + "Class D viewed SCP-895 through a pair of digital night vision goggles, killing him." + Chr(34)
								EndIf
								EntityTexture(ov\OverlayID[4], ov\OverlayTextureID[4])
								If VomitTimer < -10.0 Then
									Kill()
								EndIf
							ElseIf Sanity < -800.0 Then
								If Rand(3) = 1 Then EntityTexture(ov\OverlayID[4], ov\OverlayTextureID[4])
								If Rand(6) < 5 Then
									EntityTexture(ov\OverlayID[4], tt\MiscTextureID[Rand(7, 12)])
									For i = 0 To MaxItemAmount - 1
										If (Inventory(i) <> Null) Then
											If (WearingNightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernv") Or (WearingNightVision = 3 And Inventory(i)\ItemTemplate\TempName = "finenvgoggles") Then
												If Inventory(i)\State2 = 1.0 Then PlaySound_Strict(HorrorSFX(1))
												Inventory(i)\State2 = 2.0
												Exit
											EndIf
										EndIf
									Next
								EndIf
								BlurTimer = 1000.0
								If VomitTimer = 0.0 Then
									VomitTimer = 1.0
								EndIf
							ElseIf Sanity < -500.0 Then
								If Rand(7) = 1 Then EntityTexture(ov\OverlayID[4], ov\OverlayTextureID[4])
								If Rand(50) = 1 Then
									EntityTexture(ov\OverlayID[4], tt\MiscTextureID[Rand(7, 12)])
									For i = 0 To MaxItemAmount - 1
										If (Inventory(i) <> Null) Then
											If (WearingNightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernv") Or (WearingNightVision = 3 And Inventory(i)\ItemTemplate\TempName = "finenvgoggles") Then
												If Inventory(i)\State2 = 0.0 Then PlaySound_Strict(HorrorSFX(0))
												Inventory(i)\State2 = 1.0
												Exit
											EndIf
										EndIf
									Next
								EndIf
							Else
								EntityTexture(ov\OverlayID[4], ov\OverlayTextureID[4])
								For i = 0 To MaxItemAmount - 1
									If (Inventory(i) <> Null) Then
										If (WearingNightVision = 1 And Inventory(i)\ItemTemplate\TempName = "nvgoggles") Or (WearingNightVision = 2 And Inventory(i)\ItemTemplate\TempName = "supernv") Or (WearingNightVision = 3 And Inventory(i)\ItemTemplate\TempName = "finenvgoggles") Then
											Inventory(i)\State2 = 0.0
										EndIf
									EndIf
								Next
							EndIf
						EndIf
					EndIf
					
					If e\EventState3 > 0.0 Then e\EventState3 = Max(e\EventState3 - FPSfactor, 0.0)
					If e\EventState3 = 0.0 Then
						e\EventState3 = -1.0
						EntityTexture(ov\OverlayID[4], ov\OverlayTextureID[4])
						If WearingNightVision = 1 Then
							EntityColor(ov\OverlayID[4], 0.0, 255.0, 0.0)
						ElseIf WearingNightVision = 2 Then
							EntityColor(ov\OverlayID[4], 0.0, 100.0, 255.0)
						EndIf
					EndIf
					
					ShouldPlay = 66
					
					If UpdateLever(e\room\Levers[0]) Then
						For sc.SecurityCams = Each SecurityCams
							If sc\CoffinEffect = 0 And sc\room\RoomTemplate\Name <> "room106" Then sc\CoffinEffect = 2
							If sc\CoffinEffect = 1 Then EntityBlend(sc\ScrOverlay, 3)
							If sc\room = e\room Then sc\Screen = True
						Next
					Else
						For sc.SecurityCams = Each SecurityCams
							If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
							If sc\CoffinEffect = 1 Then EntityBlend(sc\ScrOverlay, 0)
							If sc\room = e\room Then sc\Screen = False
						Next
					EndIf
				Else
					CoffinDistance = e\room\Dist
				EndIf
				;[End Block]
			Case "room1endroom106"
				;[Block]
				If (Not Contained106) Then
					If e\EventState = 0.0 Then
						If e\room\Dist < 8.0 And e\room\Dist > 0.0 Then
							If Curr106\State < 0 Then 
								RemoveEvent(e)
							Else
								e\room\RoomDoors[0]\Open = True
								
								e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\RoomDoors[0]\OBJ, True), 0.5, EntityZ(e\room\RoomDoors[0]\OBJ, True))
								ChangeNPCTextureID(e\room\NPC[0], 4)
								PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0, True)
								MoveEntity(e\room\NPC[0]\Collider, 0.0, 0.0, 0.5) 
								
								e\room\RoomDoors[0]\Open = False
								PlaySound2(LoadTempSound("SFX\Door\EndroomDoor.ogg"), Camera, e\room\OBJ, 15.0)
								
								e\EventState = 1.0							
							EndIf
						EndIf
					ElseIf e\EventState = 1.0
						If PlayerRoom = e\room Then
							e\room\NPC[0]\State = 1.0
							e\EventState = 2.0
							
							e\Sound = LoadSound_Strict("SFX\Character\Janitor\106Abduct.ogg")
							PlaySound_Strict(e\Sound)		
							
							If e\SoundCHN <> 0 Then StopChannel(e\SoundCHN)
						ElseIf e\room\Dist < 8.0
							If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Character\Janitor\Idle.ogg")
							e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\OBJ, 15.0)
						EndIf
					ElseIf e\EventState = 2.0
						Dist = EntityDistance(e\room\NPC[0]\Collider, e\room\OBJ)
						If Dist < 1.5 Then
							de.Decals = CreateDecal(0, EntityX(e\room\OBJ), 0.01, EntityZ(e\room\OBJ), 90.0, Rnd(360.0), 0.0)
							de\Size = 0.05 : de\SizeChange = 0.008 : de\Timer = 10000.0 : UpdateDecals()
							e\EventState = 3.0
						EndIf					
					Else
						Dist = Distance(EntityX(e\room\NPC[0]\Collider), EntityZ(e\room\NPC[0]\Collider), EntityX(e\room\OBJ), EntityZ(e\room\OBJ))
						PositionEntity(Curr106\OBJ, EntityX(e\room\OBJ, True), 0.0, EntityZ(e\room\OBJ, True))
						PointEntity(Curr106\OBJ, e\room\NPC[0]\Collider)
						RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\OBJ), 0.0, True)
						
						Curr106\Idle = True
						
						If Dist < 0.4 Then
							If e\room\NPC[0]\State = 1 Then 
								SetNPCFrame(e\room\NPC[0], 41)
							EndIf
							e\EventState = e\EventState + FPSfactor / 2.0
							e\room\NPC[0]\State = 6.0
							e\room\NPC[0]\CurrSpeed = CurveValue(0.0, e\room\NPC[0]\CurrSpeed, 25.0)
							PositionEntity(e\room\NPC[0]\Collider, CurveValue(EntityX(e\room\OBJ, True), EntityX(e\room\NPC[0]\Collider), 25.0), 0.3 - e\EventState / 70.0, CurveValue(EntityZ(e\room\OBJ, True), EntityZ(e\room\NPC[0]\Collider), 25.0))
							ResetEntity(e\room\NPC[0]\Collider)
							
							AnimateNPC(e\room\NPC[0], 41.0, 58.0, 0.1, False)
							
							AnimateNPC(Curr106, 206.0, 112.0, -1.0, False)
						Else
							AnimateNPC(Curr106, 112.0, 206.0, 1.5, False)
						EndIf
						CurrSpeed = Min(CurrSpeed - (CurrSpeed * (0.15 / EntityDistance(e\room\NPC[0]\Collider, Collider)) * FPSfactor), CurrSpeed)
						If e\EventState > 100.0 Then
							PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), -100.0, EntityZ(Curr106\Collider), True)
							PositionEntity(Curr106\Collider, EntityX(Curr106\Collider), -100.0, EntityZ(Curr106\Collider), True)
							
							Curr106\Idle = False
							If EntityDistance(Collider, e\room\OBJ) < 2.5 Then Curr106\State = -0.1
							
							RemoveNPC(e\room\NPC[0])
							
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "gateaentrance"
				;[Block]
				If PlayerRoom = e\room Then 
					If RemoteDoorOn = False Then
						e\room\RoomDoors[1]\Locked = True
					ElseIf RemoteDoorOn And e\EventState3 = 0.0
						e\room\RoomDoors[1]\Locked = 2
					Else
						e\room\RoomDoors[1]\Locked = False
						
						Local gatea.Rooms = Null
						
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name = "gatea" Then
								gatea = r 
								Exit
							EndIf
						Next
						
						If Curr096 <> Null Then
							If Curr096\State = 0.0 Or Curr096\State = 5.0 Then
								e\EventState = UpdateElevators(e\EventState, e\room\RoomDoors[0], gatea\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
							Else
								e\EventState = Update096ElevatorEvent(e, e\EventState, e\room\RoomDoors[0], e\room\Objects[0])
							EndIf
						Else
							e\EventState = UpdateElevators(e\EventState, e\room\RoomDoors[0], gatea\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
						EndIf
						If Contained106 = False Then 
							If e\EventState < -1.5 And e\EventState + FPSfactor >= -1.5 Then
								PlaySound_Strict(OldManSFX(3))
							EndIf
						EndIf
						
						If EntityDistance(Collider, e\room\Objects[1]) < 4.0 Then
							gatea\RoomDoors[1]\Locked = True
							PlayerRoom = gatea
							RemoveEvent(e)
						EndIf						
					EndIf
				EndIf
				;[End Block]
			Case "room2clockroom173"
				;[Block]
				If e\room\Dist < 6.0  And e\room\Dist > 0.0 Then
					If Curr173\Idle > 1 Then
						RemoveEvent(e)
					Else
						If (Not EntityInView(Curr173\Collider, Camera)) Or EntityDistance(Curr173\Collider, Collider) > 15.0 Then 
							PositionEntity(Curr173\Collider, e\room\x + Cos(225.0 - 90.0 + e\room\Angle) * 2.0, 0.6, e\room\z + Sin(225.0 - 90.0 + e\room\Angle) * 2.0)
							ResetEntity(Curr173\Collider)
							RemoveEvent(e)
						EndIf						
					EndIf
				EndIf
				;[End Block]
			Case "room2clockroom096"
				;[Block]
				If PlayerRoom = e\room Then
					If Curr096 = Null Then
						Curr096 = CreateNPC(NPCtype096, EntityX(e\room\OBJ, True), 0.3, EntityZ(e\room\OBJ, True))
						RotateEntity(Curr096\Collider, 0.0, e\room\Angle + 45.0, 0.0, True)
					EndIf
					RemoveEvent(e)
				End If
				;[End Block]
			Case "room372"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState = 0.0 Then
						If e\room\RoomDoors[0]\Open Then
							PlaySound_Strict(RustleSFX(Rand(0, 2)))
							CreateNPC(NPCtype372, 0.0, 0.0, 0.0)
							e\EventState = 1.0
							
							RemoveEvent(e)
						EndIf					
					EndIf
				EndIf
				;[End Block]
			Case "pocketdimension"
				;[Block]
				; ~ [EventState]: a timer for scaling the tunnels in the starting room
				
				; ~ [EventState2]:
				
				; ~ 0 if the player is in the starting room
				; ~ 1 if in the room with the throne, moving pillars, plane etc
				; ~ 12-15 if player is in the room with the tall pillars 
				; ~ (goes down from 15 to 12 and SCP-106 teleports from pillar to another, pillars being room\Objects[12 to 15])
				
				; ~ [EventState3]:
				; ~ 1 when appearing in the tunnel that looks like the tunnels in HCZ
				; ~ 2 after opening the door in the tunnel
				; ~ otherwise 0
				
				If PlayerRoom = e\room Then
					ShowEntity(e\room\OBJ)
					
					PlayerFallingPickDistance = 0.0
					
					Injuries = Injuries + FPSfactor * 0.00005
					PrevSecondaryLightOn = SecondaryLightOn : SecondaryLightOn = True
					
					If (EntityY(Collider) < 2000.0 * RoomScale Or EntityY(Collider) > 2608.0 * RoomScale) Then CurrStepSFX = 1
					
					If e\Sound = 0 Then LoadEventSound(e, "SFX\Room\PocketDimension\Rumble.ogg")
					If e\Sound2 = 0 Then e\Sound2 = LoadEventSound(e, "SFX\Room\PocketDimension\PrisonVoices.ogg", 1)
					
					If e\EventState = 0.0 Then
						CameraFogColor(Camera, 0.0, 0.0, 0.0)
						CameraClsColor(Camera, 0.0, 0.0, 0.0)
						e\EventState = 0.1
					EndIf
					
					If EntityY(Collider) < 2000.0 * RoomScale Or e\EventState3 = 0.0 Or EntityY(Collider) > 2608.0 * RoomScale Then 
						ShouldPlay = 3
					Else 
						ShouldPlay = 0
					EndIf
					
					ScaleEntity(e\room\OBJ, RoomScale, RoomScale * (1.0 + Sin(e\EventState / 14.0) * 0.2), RoomScale)
					For i = 0 To 7
						ScaleEntity(e\room\Objects[i], RoomScale * (1.0 + Abs(Sin(e\EventState / 21.0 + i * 45.0) * 0.1)), RoomScale * (1.0 + Sin(e\EventState / 14.0 + i * 20.0) * 0.1), RoomScale, True)
					Next
					ScaleEntity(e\room\Objects[9], RoomScale * (1.5 + Abs(Sin(e\EventState / 21.0 + i * 45.0) * 0.1)), RoomScale * (1.0 + Sin(e\EventState / 14.0 + i * 20.0) * 0.1), RoomScale, True)
					
					e\EventState = e\EventState + FPSfactor
					
					If e\EventState2 = 0.0 Then 
						e\room\RoomDoors[0]\Open = False
						e\room\RoomDoors[1]\Open = False
						
						If Curr106\State > 0.0 ; ~ SCP-106 circles around the starting room
							Angle = (e\EventState / 10.0 Mod 360.0)
							PositionEntity(Curr106\Collider, EntityX(e\room\OBJ), 0.2 + 0.35 + Sin(e\EventState / 14.0 + i * 20.0) * 0.4, EntityX(e\room\OBJ))
							RotateEntity(Curr106\Collider, 0.0, Angle, 0.0)
							MoveEntity(Curr106\Collider, 0.0, 0.0, 6.0 - Sin(e\EventState / 10.0))
							AnimateNPC(Curr106, 55.0, 104.0, 0.5)
							RotateEntity(Curr106\Collider, 0.0, Angle + 90.0, 0.0)
							Curr106\Idle = True
							ShowEntity(Curr106\OBJ)
							ShowEntity(Curr106\Collider)
							ResetEntity(Curr106\Collider)
							Curr106\GravityMult = 0.0
							Curr106\DropSpeed = 0
							PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), EntityY(Curr106\Collider) - 0.15, EntityZ(Curr106\Collider))
							RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\Collider), 0.0)
							
							If e\EventState > 70 * 65.0 Then
								If Rand(800) = 1 Then	
									PlaySound_Strict HorrorSFX(8)
									Curr106\State = -0.1
									Curr106\Idle = False
									e\EventState = 601.0
								EndIf
							EndIf
						EndIf
					EndIf 
					
					If EntityDistance(Collider, Curr106\Collider) < 0.3 Then ; ~ SCP-106 attacks if close enough to player
						Curr106\Idle = False
						Curr106\State = -10.0
					EndIf
					
					If e\EventState2 = 1 Then ; ~ In the second room
						PositionEntity(e\room\Objects[9], EntityX(e\room\Objects[8], True) + 3384.0 * RoomScale, 0.0, EntityZ(e\room\Objects[8], True))
						
						TranslateEntity(e\room\Objects[9], Cos(e\EventState * 0.8) * 5.0, 0.0, Sin(e\EventState * 1.6) * 4.0, True)
						RotateEntity(e\room\Objects[9], 0.0, e\EventState * 2.0, 0.0)
						
						PositionEntity(e\room\Objects[10], EntityX(e\room\Objects[8], True), 0.0, EntityZ(e\room\Objects[8], True) + 3384.0 * RoomScale)
						
						TranslateEntity(e\room\Objects[10], Sin(e\EventState * 1.6) * 4.0, 0.0, Cos(e\EventState * 0.8) * 5.0, True)
						RotateEntity(e\room\Objects[10], 0.0, e\EventState * 2.0, 0.0)
						
						If e\EventState3 = 1.0 Or e\EventState3 = 2.0 Then ;the "trick room"
							If e\EventState3 = 1.0 And (e\room\RoomDoors[0]\OpenState > 150.0 Or e\room\RoomDoors[1]\OpenState > 150.0) Then
								PlaySound_Strict(LoadTempSound("SFX\Horror\Horror16.ogg"))
								BlurTimer = 800.0
								e\EventState3 = 2.0
							EndIf
							If EntityY(Collider) < 5.0 Then e\EventState3 = 0
						Else
							; ~ The trenches
							If EntityY(Collider) > 6.0 Then
								ShouldPlay = 15
								
								CameraFogColor(Camera, 38.0, 55.0, 47.0)
								CameraClsColor(Camera, 38.0, 55.0, 47.0)
								
								If EntityX(e\room\Objects[20], True) < EntityX(e\room\Objects[8], True) - 4000.0 * RoomScale Then
									e\SoundCHN2 = PlaySound_Strict(e\Sound2)
									
									PositionEntity e\room\Objects[20], EntityX(Collider, True) + 4000.0 * RoomScale, 12.0, EntityZ(Collider, True)
								EndIf
								
								MoveEntity(Collider, 0.0, Min((12.0 - EntityY(Collider)), 0.0) * FPSfactor, 0.0)
								
								x = -FPSfactor * RoomScale * 4.0
								y = (17.0 - Abs(EntityX(Collider) - EntityX(e\room\Objects[20])) * 0.5) - EntityY(e\room\Objects[20])
								z = EntityZ(Collider, True) - EntityZ(e\room\Objects[20])
								TranslateEntity(e\room\Objects[20], x, y, z, True)
								RotateEntity(e\room\Objects[20], -90.0 - (EntityX(Collider) - EntityX(e\room\Objects[20])) * 1.5, -90.0, 0.0, True)
								
								; ~ Check if the plane can see the player
								Local Safe% = False
								
								For i = 0 To 2
									Select i
										Case 0
											;[Block]
											x = -1452.0 * RoomScale
											z = -37.0 * RoomScale
											;[End Block]
										Case 1
											;[Block]
											x = -121.0 * RoomScale
											z = 188.0 * RoomScale
											;[End Block]
										Case 2
											;[Block]
											x = 1223.0 * RoomScale
											z = -196.0 * RoomScale
											;[End Block]
									End Select
									
									x = x + EntityX(e\room\Objects[8], True)
									z = z + EntityZ(e\room\Objects[8], True)
									
									If Distance(EntityX(Collider), EntityZ(Collider), x, z) < 200.0 * RoomScale Then Safe = True : Exit
								Next
								
								Dist = EntityDistance(Collider, e\room\Objects[20])
								
								If e\SoundCHN2 <> 0 And ChannelPlaying(e\SoundCHN2)
									e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, Camera, 10.0, 0.3 + (Not Safe) * 0.6)
								EndIf	
								
								If Safe Then
									EntityTexture(e\room\Objects[20], e\room\Objects[18])
								ElseIf Dist < 8.0
									e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\Objects[20], 8.0)
									EntityTexture(e\room\Objects[20], e\room\Objects[19])
									Injuries = Injuries + (8.0 - Dist) * FPSfactor * 0.0003
									
									If Dist < 7.0 Then 
										Pvt = CreatePivot()
										PositionEntity Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
										PointEntity(Pvt, e\room\Objects[20])
										TurnEntity(Pvt, 90.0, 0.0, 0.0)
										User_Camera_Pitch = CurveAngle(EntityPitch(Pvt), User_Camera_Pitch + 90.0, 10.0)
										User_Camera_Pitch = User_Camera_Pitch - 90.0
										RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(Collider), 10.0), 0.0)
										FreeEntity(Pvt)
									EndIf
								EndIf
								
								CameraShake = Max(4.0 + ((Not Safe) * 4.0) - Dist, 0.0)
								
								; ~ Check if player is at the sinkhole (the exit from the trench room)
								If EntityY(Collider) < 8.5 Then
									LoadEventSound(e, "SFX\Room\PocketDimension\Rumble.ogg")
									LoadEventSound(e, "SFX\Room\PocketDimension\PrisonVoices.ogg", 1)
									
									; ~ Move to the "exit room"
									BlurTimer = 1500.0
									e\EventState2 = 1.0
									BlinkTimer = -10.0
									
									PositionEntity(Collider, EntityX(e\room\Objects[8], True) - 400.0 * RoomScale, -304.0 * RoomScale, EntityZ(e\room\Objects[8], True))
									ResetEntity(Collider)
									
									CameraFogColor(Camera, 0.0, 0.0, 0.0)
									CameraClsColor(Camera, 0.0, 0.0, 0.0)
								EndIf
							Else
								e\EventState3 = 0.0
								
								For i = 9 To 10
									Dist = Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True))
									If Dist < 6.0 Then 
										If Dist < 100.0 * RoomScale Then
											Pvt = CreatePivot()
											PositionEntity(Pvt, EntityX(e\room\Objects[i], True), EntityY(Collider), EntityZ(e\room\Objects[i], True))
											PointEntity(Pvt, Collider)
											RotateEntity(Pvt, 0, Int(EntityYaw(Pvt) / 90) * 90.0, 0.0, True)
											MoveEntity(Pvt, 0.0, 0.0, 100.0 * RoomScale)
											PositionEntity(Collider, EntityX(Pvt), EntityY(Collider), EntityZ(Pvt))
											FreeEntity(Pvt)
											
											If KillTimer = 0.0 Then
												DeathMSG = "In addition to the decomposed appearance typical of SCP-106's victims, the body exhibits injuries that have not been observed before: "
												DeathMSG = DeathMSG + "massive skull fracture, three broken ribs, fractured shoulder and multiple heavy lacerations."
												
												PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Impact.ogg"))
												KillTimer = -1.0
											EndIf
										EndIf
										If Float(e\EventStr) < 1000.0 Then
											e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\Objects[i], 6.0)
										EndIf
									EndIf
								Next
								
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(e\room\Objects[8], True) - 1536.0 * RoomScale, 500.0 * RoomScale, EntityZ(e\room\Objects[8], True) + 608.0 * RoomScale)
								If EntityDistance(Pvt, Collider) < 5.0 Then 
									e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, Pvt, 3.0)
								EndIf
								FreeEntity(Pvt)
								
								; ~ SCP-106's eyes
								ShowEntity(e\room\Objects[17])
								PositionEntity(e\room\Objects[17], EntityX(e\room\Objects[8], True), 1376.0 * RoomScale, EntityZ(e\room\Objects[8], True) - 2848.0 * RoomScale)
								PointEntity(e\room\Objects[17], Collider)
								TurnEntity(e\room\Objects[17], 0.0, 180.0, 0.0)
								
								Temp = EntityDistance(Collider, e\room\Objects[17])
								If Temp < 2000.0 * RoomScale Then
									Injuries = Injuries + (FPSfactor / 4000.0)
									e\EventStr = Float(e\EventStr) + (FPSfactor / 1000.0)
									
									If Float(e\EventStr) > 1.0 And Float(e\EventStr) < 1000.0 Then
										PlaySound_Strict LoadTempSound("SFX\Room\PocketDimension\Kneel.ogg")
										LoadEventSound(e, "SFX\Room\PocketDimension\Screech.ogg")
										e\EventStr = Float(1000.0)
									EndIf
									
									Sanity = Max(Sanity - FPSfactor / Temp / 8.0, -1000.0)
									
									CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * 15.0 * Max((6.0 - Temp) / 6.0, 0.0))
									
									Pvt = CreatePivot()
									PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
									PointEntity(Pvt, e\room\Objects[17])
									TurnEntity(Pvt, 90.0, 0.0, 0.0)
									User_Camera_Pitch = CurveAngle(EntityPitch(Pvt), User_Camera_Pitch + 90.0, Min(Max(15000.0 / (-Sanity), 15.0), 500.0))
									User_Camera_Pitch = User_Camera_Pitch - 90.0
									RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(Collider), Min(Max(15000.0 / (-Sanity), 15.0), 500.0)), 0)
									FreeEntity(Pvt)
									
									; ~ Teleport the player to the trenches
									If Crouch Then
										BlinkTimer = -10.0
										PositionEntity(Collider, EntityX(e\room\Objects[8], True) - 1344.0 * RoomScale, 2944.0 * RoomScale, EntityZ(e\room\Objects[8], True) - 1184.0 * RoomScale)
										ResetEntity(Collider)
										Crouch = False
										
										LoadEventSound(e, "SFX\Room\PocketDimension\Explosion.ogg")
										LoadEventSound(e, "SFX\Room\PocketDimension\TrenchPlane.ogg", 1)
										PositionEntity(e\room\Objects[20], EntityX(e\room\Objects[8], True) - 1000, 0, 0, True)
										
										e\EventStr = Float(0.0)
									EndIf
								ElseIf EntityY(Collider) < -180.0 * RoomScale ; ~ The "exit room"
									Temp = Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[8], True) + 1024.0 * RoomScale, EntityZ(e\room\Objects[8], True))
									If Temp < 640.0 * RoomScale
										BlurTimer = (640.0 * RoomScale - Temp) * 3000.0
										
										e\SoundCHN2 = LoopSound2(DecaySFX(Rand(1, 3)), e\SoundCHN2, Camera, Collider, 2.0, (640.0 * RoomScale - Temp) * Abs(CurrSpeed) * 100)
										CurrSpeed = CurveValue(0.0, CurrSpeed, Temp * 10.0)
										
										If Temp < 130.0 * RoomScale Then
											
											For r.Rooms = Each Rooms
												If r\RoomTemplate\Name = "room2shaft" Then
													GiveAchievement(AchvPD)
													e\EventState = 0.0
													e\EventState2 = 0.0
													
													SecondaryLightOn = PrevSecondaryLightOn
													PrevSecondaryLightOn = 0.0
													
													BlinkTimer = -10.0
													LightBlink = 5.0
													
													BlurTimer = 1500.0
													
													PlayerRoom = r
													
													PlaySound_Strict(LoadTempSound("SFX\Room\PocketDimension\Exit.ogg"))
													
													TeleportEntity(Collider, EntityX(r\Objects[0], True), 0.4, EntityZ(r\Objects[0], True), 0.3, True)
													
													UpdateRooms()
													UpdateDoors()
													Curr106\State = 10000.0
													Curr106\Idle = False
													
													de.Decals = CreateDecal(0, EntityX(r\Objects[0], True), EntityY(r\Objects[0], True), EntityZ(r\Objects[0], True), 270.0, Rand(360.0), 0.0)
													TeleportEntity(de\OBJ, EntityX(r\Objects[0], True), EntityY(r\Objects[0], True) + 0.6, EntityZ(r\Objects[0], True), 0.0, True, 4.0, 1)
													
													For e2.Events = Each Events
														If e2\EventName = "room2sl"
															e2\EventState3 = 0
															UpdateLever(e2\room\Levers[0])
															RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 0.0)
															TurnCheckpointMonitorsOff(0)
															Exit
														EndIf
													Next
													Exit
													Return
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							EndIf	
						EndIf
						
						If EntityY(Collider) < -1600.0 * RoomScale Then
							If EntityDistance(Collider, e\room\Objects[8]) > 4750.0 * RoomScale Then
								CameraFogColor(Camera, 0.0, 0.0, 0.0)
								CameraClsColor(Camera, 0.0, 0.0, 0.0)
								
								DropSpeed = 0.0
								BlurTimer = 500.0
								BlurTimer = 1500.0
								PositionEntity(Collider, EntityX(e\room\OBJ,True), 0.4, EntityX(e\room\OBJ, True))
								For r.Rooms = Each Rooms
									If r\RoomTemplate\Name = "room106" Then
										e\EventState = 0.0
										e\EventState2 = 0.0
										
										TeleportEntity(Collider, EntityX(r\Objects[10], True), 0.4, EntityZ(r\Objects[10], True), 0.3, True)
										
										GiveAchievement(AchvPD)
										SecondaryLightOn = PrevSecondaryLightOn
										PrevSecondaryLightOn = 0.0
										
										Curr106\State = 10000.0
										Curr106\Idle = False
										
										For e2.Events = Each Events
											If e2\EventName = "room2sl"
												e2\EventState3 = 0.0
												UpdateLever(e2\room\Levers[0])
												RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 0.0)
												TurnCheckpointMonitorsOff(0)
												Exit
											EndIf
										Next
										Exit
										Return
									EndIf
								Next
								ResetEntity(Collider)
								
								e\EventState2 = 0.0
								UpdateDoorsTimer = 0.0
								UpdateDoors()
								UpdateRooms()
							Else ; ~ The player is not at the exit, must've fallen down
								If KillTimer >= 0 Then 
									PlaySound_Strict HorrorSFX(8)
									DeathMSG = "In addition to the decomposed appearance typical of the victims of SCP-106, the subject seems to have suffered multiple heavy fractures to both of his legs."
								EndIf
								KillTimer = Min(-1.0, KillTimer)	
								BlurTimer = 3000.0
							EndIf
						EndIf
						
						UpdateDoorsTimer = 0.0
						UpdateDoors()
						UpdateRooms()
					ElseIf e\EventState2 = 0.0
						Dist# = EntityDistance(Collider, e\room\OBJ)	
						
						If Dist > 1700.0 * RoomScale Then
							BlinkTimer = -10.0
							
							Select Rand(25)
								Case 1, 2, 3, 4
									;[Block]
									PlaySound_Strict(OldManSFX(3))
									
									Pvt = CreatePivot()
									PositionEntity(Pvt, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
									PointEntity(Pvt, e\room\OBJ)
									MoveEntity(Pvt, 0.0, 0.0, Dist * 1.9)
									PositionEntity(Collider, EntityX(Pvt), EntityY(Collider), EntityZ(Pvt))
									ResetEntity(Collider)
									
									MoveEntity(Pvt, 0.0, 0.0, 0.8)
									PositionEntity(e\room\Objects[10], EntityX(Pvt), 0.0, EntityZ(Pvt))
									RotateEntity(e\room\Objects[10], 0.0, EntityYaw(Pvt), 0.0, True)	
									
									FreeEntity(Pvt)
									;[End Block]
								Case 5, 6, 7, 8, 9, 10 
									;[Block]
									e\EventState2 = 1.0
									BlinkTimer = -10.0
									PlaySound_Strict(OldManSFX(3))
									
									PositionEntity(Collider, EntityX(e\room\Objects[8], True), 0.5, EntityZ(e\room\Objects[8], True))
									ResetEntity(Collider)
									;[End Block]
								Case 11, 12 ;middle of the large starting room
									;[Block]
									BlurTimer = 500.0
									PositionEntity(Collider, EntityX(e\room\OBJ), 0.5, EntityZ(e\room\OBJ))
									;[End Block]
								Case 13, 14, 15 ; ~ The exit room"
									;[Block]
									BlurTimer = 1500.0
									e\EventState2 = 1.0
									BlinkTimer = -10.0
									
									PositionEntity(Collider, EntityX(e\room\Objects[8], True) - 400.0 * RoomScale, -304.0 * RoomScale, EntityZ(e\room\Objects[8], True))
									ResetEntity Collider
									;[End Block]
								Case 16, 17, 18, 19
									;[Block]
									BlurTimer = 1500.0
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "tunnel" Then
											GiveAchievement(AchvPD)
											e\EventState = 0.0
											e\EventState2 = 0.0
											
											SecondaryLightOn = PrevSecondaryLightOn
											PrevSecondaryLightOn = 0.0
											TeleportEntity(Collider, EntityX(r\OBJ, True), 0.4, EntityZ(r\OBJ, True), 0.3, True)
											Curr106\State = 250.0
											Curr106\Idle = False
											
											For e2.Events = Each Events
												If e2\EventName = "room2sl"
													e2\EventState3 = 0.0
													UpdateLever(e2\room\Levers[0])
													RotateEntity(e2\room\Levers[0], 0.0, EntityYaw(e2\room\Levers[0]), 0.0)
													TurnCheckpointMonitorsOff(0)
													Exit
												EndIf
											Next
											Exit
											Return
										EndIf
									Next
									;[End Block]
								Case 20, 21, 22 ; ~ The tower room
									;[Block]
									BlinkTimer = -10.0
									PositionEntity(Collider, EntityX(e\room\Objects[12], True), 0.6, EntityZ(e\room\Objects[12], True))
									ResetEntity(Collider)
									e\EventState2 = 15.0
									;[End Block]
								Case 23, 24, 25
									;[Block]
									BlurTimer = 1500.0
									e\EventState2 = 1.0
									e\EventState3 = 1.0
									BlinkTimer = -10.0
									
									PlaySound_Strict(OldManSFX(3))
									
									PositionEntity(Collider, EntityX(e\room\Objects[8], True), 2288.0 * RoomScale, EntityZ(e\room\Objects[8], True))
									ResetEntity(Collider)
									;[End Block]
							End Select 
							
							UpdateDoorsTimer = 0.0
							UpdateDoors()
							UpdateRooms()
						EndIf					
					Else ; ~ Pillar room
						CameraFogColor(Camera, 38.0 * 0.5, 55.0 * 0.5, 47.0 * 0.5)
						CameraClsColor(Camera, 38.0 * 0.5, 55.0 * 0.5, 47.0 * 0.5)
						
						If ParticleAmount > 0
							If Rand(800) = 1 Then 
								Angle = EntityYaw(Camera, True) + Rnd(150, 210)
								p.Particles = CreateParticle(EntityX(Collider) + Cos(Angle) * 7.5, 0.0, EntityZ(Collider) + Sin(Angle) * 7.5, 3, 4.0, 0.0, 2500.0)
								p\Speed = 0.01 : p\SizeChange = 0.0
								EntityBlend(p\OBJ, 2)
								PointEntity(p\Pvt, Camera)
								TurnEntity(p\Pvt, 0.0, 145.0, 0.0, True)
								TurnEntity(p\Pvt, Rnd(10.0 , 20.0), 0.0, 0.0, True)
							EndIf
						EndIf
						
						If e\EventState2 > 12.0 Then 
							Curr106\Idle = True
							PositionEntity(Curr106\Collider, EntityX(e\room\Objects[e\EventState2], True), 0.27, EntityZ(e\room\Objects[e\EventState2], True))
							
							PointEntity(Curr106\Collider, Camera)
							TurnEntity(Curr106\Collider, 0, Sin(MilliSecs2() / 20) * 6.0, 0, True)
							MoveEntity(Curr106\Collider, 0, 0, Sin(MilliSecs2() / 15) * 0.06)
							
							ShowEntity(Curr106\OBJ)
							ShowEntity(Curr106\Collider)
							ResetEntity(Curr106\Collider)
							Curr106\GravityMult = 0.0
							Curr106\DropSpeed = 0.0
							PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), EntityY(Curr106\Collider) - 0.15, EntityZ(Curr106\Collider))
							RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\Collider), 0.0)
							
							If Rand(750) = 1 And e\EventState2 > 12.0 Then
								BlinkTimer = -10.0
								e\EventState2 = e\EventState2 - 1.0
								PlaySound_Strict(HorrorSFX(8))
							EndIf
							
							If e\EventState2 = 12.0 Then
								CameraShake = 1.0
								PositionEntity(Curr106\Collider, EntityX(e\room\Objects[e\EventState2], True), -1.0, EntityZ(e\room\Objects[e\EventState2], True))
								Curr106\State = -10.0
								ResetEntity(Curr106\Collider)
							EndIf
						Else 
							Curr106\State = -10.0
							Curr106\Idle = False
						EndIf
						
						If EntityY(Collider) < -1600.0 * RoomScale Then
							; ~ Player is at the exit
							If Distance(EntityX(e\room\Objects[16], True), EntityZ(e\room\Objects[16], True), EntityX(Collider), EntityZ(Collider)) < 144.0 * RoomScale Then
								CameraFogColor(Camera, 0.0, 0.0, 0.0)
								CameraClsColor(Camera, 0.0, 0.0, 0.0)
								
								DropSpeed = 0.0
								BlurTimer = 500.0
								PositionEntity(Collider, EntityX(e\room\OBJ), 0.5, EntityZ(e\room\OBJ))
								ResetEntity(Collider)
								e\EventState2 = 0.0
								UpdateDoorsTimer = 0.0
								UpdateDoors()
								UpdateRooms()
							Else ; ~ Somewhere else, must've fallen down
								If KillTimer >= 0.0 Then PlaySound_Strict(HorrorSFX(8))
								KillTimer = Min(-1.0, KillTimer)	
								BlurTimer = 3000.0
							EndIf
						EndIf 
					EndIf
				Else
					HideEntity(e\room\OBJ)
					e\EventState = 0.0
					e\EventState2 = 0.0
					e\EventState3 = 0.0
					e\EventStr = Float(0.0)
				EndIf
				;[End Block]
			Case "room2cafeteria"
				;[Block]
				If PlayerRoom = e\room Then
					If (Not Using294) Then
						If EntityDistance(e\room\Objects[0], Collider) < 1.5 Then
							GiveAchievement(Achv294)
							If EntityInView(e\room\Objects[0], Camera) Then
								DrawHandIcon = True
								If MouseHit1 Then
									Temp = True
									For it.Items = Each Items
										If it\Picked = False Then
											If EntityX(it\Collider) - EntityX(e\room\Objects[1], True) = 0 Then
												If EntityZ(it\Collider) - EntityZ(e\room\Objects[1], True) = 0 Then
													Temp = False
													Exit
												EndIf
											EndIf
										EndIf
									Next
									
									Local Inserted% = False
									
									If e\EventState2 < 2.0 Then
										If SelectedItem <> Null Then
											If SelectedItem\ItemTemplate\TempName = "25ct" Or SelectedItem\ItemTemplate\TempName = "coin" Then
												RemoveItem(SelectedItem)
												SelectedItem = Null
												e\EventState2 = e\EventState2 + 1.0
												PlaySound_Strict(LoadTempSound("SFX\SCP\294\coin_drop.ogg"))
												Inserted = True
											EndIf
										EndIf
									EndIf
									If e\EventState2 = 2.0 Then
										Using294 = Temp
										If Using294 Then MouseHit1 = False
									ElseIf e\EventState2 = 1.0 And (Not Inserted) Then
										Using294 = False
										Msg = "You need to insert another Quarter in order to use this machine."
										MsgTimer = 70 * 5.0
									ElseIf (Not Inserted) Then
										Using294 = False
										Msg = "You need to insert two Quarters in order to use this machine."
										MsgTimer = 70 * 5.0
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf		
				EndIf
				
				If e\EventState = 0.0 Then
					CreateNPC(NPCtype066, EntityX(e\room\OBJ), 0.5, EntityZ(e\room\obj))
					e\EventState = 1.0
				EndIf
				;[End Block]
			Case "room2ccont"
				;[Block]
				If PlayerRoom = e\room Then
					EntityPick(Camera, 1.5)
					
					If PickedEntity() = e\room\Objects[3]
						If e\EventState = 0.0
							e\EventState = Max(e\EventState, 1.0)
							PlaySound_Strict(HorrorSFX(7))
							PlaySound_Strict(LeverSFX)
						EndIf 
					EndIf
					
					; ~ Primary Lighting
					UpdateLever(e\room\Objects[1])
					
					; ~ Secondary Lighting
					Local PrevState2# = e\EventState2
					
					e\EventState2 = UpdateLever(e\room\Objects[3])
					If (PrevState2 <> e\EventState2) And e\EventState > 0 Then PlaySound2(LightSFX, Camera, e\room\Objects[3])
					If e\EventState2
						SecondaryLightOn = CurveValue(1.0, SecondaryLightOn, 10.0)
					Else
						SecondaryLightOn = CurveValue(0.0, SecondaryLightOn, 10.0)
					EndIf
					
					; ~ Remote Door Control
					RemoteDoorOn = UpdateLever(e\room\Objects[5])
					
					If e\EventState = 1.0 Then
						RotateEntity(e\room\Objects[3], 0.0, EntityYaw(e\room\Objects[3]), 0.0)
						e\EventState = 2.0
					EndIf
				EndIf
				;[End Block]
			Case "room2closets"
				;[Block]
				If e\EventState = 0 Then
					If PlayerRoom = e\room And Curr173\Idle<2 Then
						If e\EventStr = "" And QuickLoadPercent = -1
							QuickLoadPercent = 0
							QuickLoad_CurrEvent = e
							e\EventStr = "Load0"
						EndIf
					EndIf
				Else
					e\EventState=e\EventState+FPSfactor
					If e\EventState < 70*3.5 Then
						RotateEntity(e\room\NPC[1]\Collider,0,CurveAngle(e\room\angle+90,EntityYaw(e\room\NPC[1]\Collider),100.0),0,True)
						
						e\room\NPC[0]\State=1
						If e\EventState > 70*3.2 And e\EventState-FPSfactor =< 70*3.2 Then PlaySound2(IntroSFX(11),Camera,e\room\obj,15.0)
					ElseIf e\EventState < 70*6.5
						If e\EventState-FPSfactor < 70*3.5 Then
							e\room\NPC[0]\State=0
							e\room\NPC[1]\SoundChn = PlaySound2(e\room\NPC[1]\Sound, Camera, e\room\NPC[1]\Collider,12.0)
						EndIf
						
						If e\EventState > 70*4.5 Then
							PointEntity e\room\NPC[0]\obj, e\room\obj
							RotateEntity(e\room\NPC[0]\Collider,0,CurveAngle(EntityYaw(e\room\NPC[0]\obj),EntityYaw(e\room\NPC[0]\Collider),30.0),0,True)
						EndIf
						PointEntity e\room\NPC[1]\obj, e\room\obj
						TurnEntity e\room\NPC[1]\obj, 0, Sin(e\EventState)*25, 0
						RotateEntity(e\room\NPC[1]\Collider,0,CurveAngle(EntityYaw(e\room\NPC[1]\obj),EntityYaw(e\room\NPC[1]\Collider),30.0),0,True)
					Else
						If e\EventState-FPSfactor < 70*6.5 Then 
							PlaySound_Strict (HorrorSFX(0))
							PlaySound_Strict (LightSFX)
						EndIf
						BlinkTimer = Max((70*6.5-e\EventState)/5.0 - Rnd(0.0,2.0),-10)
						If BlinkTimer =-10 Then
							If e\EventState > 70*7.5 And e\EventState-FPSfactor =< 70*7.5 Then
								PlaySound2(NeckSnapSFX(0),Camera,e\room\NPC[0]\Collider,8.0)
								;Wallet spawning (with 3 coins)
								it.Items = CreateItem("Wallet","wallet",EntityX(e\room\NPC[0]\Collider,True),EntityY(e\room\NPC[0]\Collider,True),EntityZ(e\room\NPC[0]\Collider,True))
								EntityType(it\collider, HIT_ITEM)
								PointEntity it\collider,e\room\NPC[1]\Collider
								MoveEntity it\collider,-0.4,0,-0.2
								TeleportEntity(it\collider,EntityX(it\collider),EntityY(it\collider),EntityZ(it\collider),-0.02,True,10)
								For i = 0 To 1
									it2.Items = CreateItem("Quarter","25ct",1,1,1)
									it2\Picked = True
									it2\Dropped = -1
									it2\itemtemplate\found=True
									it\SecondInv[i] = it2
									HideEntity(it2\collider)
									EntityType(it2\collider, HIT_ITEM)
								Next
							EndIf
							If e\EventState > 70*8.0 And e\EventState-FPSfactor =< 70*8.0 Then
								PlaySound2(NeckSnapSFX(1),Camera,e\room\NPC[1]\Collider,8.0)
							EndIf
							SetNPCFrame e\room\NPC[0], 60
							e\room\NPC[0]\State=8
							
							SetNPCFrame e\room\NPC[1], 19
							e\room\NPC[1]\State = 6
						EndIf
						
						If e\EventState > 70*8.5 Then
							PositionEntity Curr173\Collider, (EntityX(e\room\Objects[0],True)+EntityX(e\room\Objects[1],True))/2,EntityY(e\room\Objects[0],True),(EntityZ(e\room\Objects[0],True)+EntityZ(e\room\Objects[1],True))/2
							PointEntity Curr173\Collider, Collider
							ResetEntity Curr173\Collider
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2doors173"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState = 0.0 And Curr173\Idle = 0 Then
						If (Not EntityInView(Curr173\OBJ, Camera)) Then
							e\EventState = 1.0
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[0], True), 0.5, EntityZ(e\room\Objects[0], True))
							ResetEntity(Curr173\Collider)
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2elevator"
				;[Block]
				If e\EventState = 0.0 Then
					If e\room\Dist < 5.0 And e\room\Dist > 0.0 Then
						e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\OBJ, True), 0.5, EntityZ(e\room\OBJ, True))
						e\EventState = 1.0
					EndIf
				Else
					If e\room\NPC[0] <> Null Then
						; ~ Attack the player, if he too close
						If EntityDistance(e\room\NPC[0]\Collider, Collider) < 2.0 And (Not chs\NoTarget) Then
							e\room\NPC[0]\State = 1.0
							e\room\NPC[0]\State3 = 1.0
						Else
							If e\EventState = 1.0 Then
								PointEntity(e\room\NPC[0]\Collider, e\room\Objects[1])
								RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0, True)	
								e\room\NPC[0]\State = 10.0
								
								If EntityDistance(e\room\NPC[0]\Collider, e\room\Objects[1]) =< 2.5 Then
									e\room\RoomDoors[0]\Locked = False
									UseDoor(e\room\RoomDoors[0]) 
									e\room\RoomDoors[0]\Locked = True
									
									PlaySound_Strict(LoadTempSound("SFX\Room\Room2ElevatorDeath.ogg"))
									
									e\EventState = 1.5
								EndIf
							Else
								If e\room\RoomDoors[0]\OpenState =< 0.0 Then 
									RemoveNPC(e\room\NPC[0]) : e\room\NPC[0] = Null
									e\EventState = 2.0
								EndIf
							EndIf
						EndIf
					EndIf
					
					If e\EventState >= 2.0 And e\EventState < 70 * 13.0 Then
						e\EventState = e\EventState + FPSfactor
						If e\EventState > 70 * 6.7 And e\EventState < 70 * 7.4 Then
							CameraShake = 7.4 - (e\EventState / 70)
						ElseIf e\EventState > 70 * 8.6 And e\EventState < 70 * 10.6 
							CameraShake = 10.6 - (e\EventState / 70)
						ElseIf e\EventState > 70 * 12.6
							CameraShake = 0.0
							If e\EventState - FPSfactor < 70 * 12.6 Then
								de.Decals = CreateDecal(3, EntityX(e\room\Objects[0], True), 0.0005, EntityZ(e\room\Objects[0], True), 90.0, Rnd(360.0), 0.0)
								
								de.Decals = CreateDecal(17, EntityX(e\room\Objects[0], True), 0.002, EntityZ(e\room\Objects[0], True), 90.0, Rnd(360.0), 0.0)
								de\Size = 0.5
								
								de.Decals = CreateDecal(3, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True), 0.0, e\room\Angle + 270.0, 0.0)
								de\Size = 0.9
								
								e\room\RoomDoors[0]\Locked = False
								
								e\EventState2 = 1.0
							EndIf
						EndIf
					EndIf
					
					If e\EventState2 = 1.0 Then
						If e\room\RoomDoors[0]\Open Then 
							e\room\RoomDoors[0]\Locked = True
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2elevator2"
				;[Block]
				If e\room\Dist < 8.0 And e\room\Dist > 0.0 Then
					de.Decals = CreateDecal(3, EntityX(e\room\Objects[0], True), 0.0005, EntityZ(e\room\Objects[0], True), 90.0, Rnd(360.0), 0.0)
					
					e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[0], True), 0.5, EntityZ(e\room\Objects[0], True))
					ChangeNPCTextureID(e\room\NPC[0], 0)
					
					RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\OBJ) - 80.0, 0.0, True)	
					
					SetNPCFrame(e\room\NPC[0], 19.0)
					e\room\NPC[0]\State = 8.0
					
					RemoveEvent(e)
				EndIf
				;[End Block]
			Case "room2fan"
				;[Block]
				; ~ EventState1 = Timer for turning the fan on / off
				
				; ~ EventState2 = Fan on / off
				
				; ~ EventState3 = The speed of the fan
				
				If PlayerRoom = e\room Then
					TurnEntity(e\room\Objects[0], e\EventState3 * FPSfactor, 0.0, 0.0)
					If e\EventState3 > 0.01 Then
						e\room\SoundCHN = LoopSound2(RoomAmbience[9], e\room\SoundCHN, Camera, e\room\Objects[0], 5.0, (e\EventState3 / 4.0))
					EndIf
					e\EventState3 = CurveValue(e\EventState2 * 5, e\EventState3, 150.0)			
				EndIf
				
				If e\room\Dist < 16.0 Then 
					If e\EventState < 0.0 Then
						e\EventState = 70 * Rand(15, 30)
						Temp = e\EventState2
						e\EventState2 = Rand(0.0, 1.0)
						If PlayerRoom <> e\room Then
							e\EventState3 = e\EventState2 * 5.0
						Else
							If Temp = 0 And e\EventState2 = 1.0 Then ; ~ Turn on the fan
								PlaySound2 (LoadTempSound("SFX\Ambient\Room ambience\FanOn.ogg"), Camera, e\room\Objects[0], 8.0)
							ElseIf Temp = 1 And e\EventState2 = 0.0 ; ~ Turn off the fan
								PlaySound2 (LoadTempSound("SFX\Ambient\Room ambience\FanOff.ogg"), Camera, e\room\Objects[0], 8.0)
							EndIf
						EndIf
					Else
						e\EventState = e\EventState - FPSfactor
					EndIf					
				EndIf
				;[End Block]
			Case "room2nuke"
				;[Block]
				If PlayerRoom = e\room Then
					e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[4], e\room\Objects[5], e)
					
					e\EventState = UpdateLever(e\room\Objects[1])
					UpdateLever(e\room\Objects[3])
					
					If e\EventState3 = 0.0 Then
						n.NPCs = CreateNPC(NPCtypeD, EntityX(e\room\Objects[6], True), 0.55, EntityZ(e\room\Objects[6], True))
						RotateEntity(n\Collider, 0.0, e\room\Angle + 90.0, 0.0)
						n\State = 3.0 : n\IsDead = True
						ChangeNPCTextureID(n, 9)
						SetNPCFrame(n, 40.0)
						e\EventState3 = 1.0
					EndIf
				EndIf
				;[End Block]
			Case "room2offices2"
				;[Block]
				If PlayerRoom = e\room Then
					If BlinkTimer < -8.0 And BlinkTimer > -12.0 Then
						Temp = Rand(1, 4)
						PositionEntity(e\room\Objects[0], EntityX(e\room\Objects[Temp], True), EntityY(e\room\Objects[Temp], True), EntityZ(e\room\Objects[Temp], True), True)
						RotateEntity(e\room\Objects[0], 0.0, Rnd(360.0), 0.0)
					EndIf
				EndIf
				;[End Block]
			Case "room2offices3"
				;[Block]
				If PlayerRoom = e\room Then
					e\EventState = e\EventState + FPSfactor
					If e\EventState > 700.0 Then
						If EntityDistance(e\room\RoomDoors[0]\OBJ, Collider) > 0.5 Then 
							If EntityInView(e\room\RoomDoors[0]\OBJ, Camera) = False Then
								e\room\RoomDoors[0]\Open = False
								RemoveEvent(e)
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2tesla"
				;[Block]
				Temp = True
				If e\EventState2 > 70 * 3.5 And e\EventState2 < 70 * 90.0 Then Temp = False
				If Temp And EntityY(Collider, True) > EntityY(e\room\OBJ, True) And EntityY(Collider, True) < 4.0 Then
					If e\EventState = 0.0 Then
						If e\room\Dist < 8.0 Then
							HideEntity(e\room\Objects[3])
							If (MilliSecs2() Mod 1500) < 800 Then
								ShowEntity(e\room\Objects[4])
							Else
								HideEntity(e\room\Objects[4])
							EndIf			
							
							If e\SoundCHN = 0 Then ; ~ Humming when the player isn't close
								e\SoundCHN = PlaySound2(TeslaIdleSFX, Camera, e\room\Objects[3], 4.0, 0.5)
							Else
								If ChannelPlaying(e\SoundCHN) = False Then e\SoundCHN = PlaySound2(TeslaIdleSFX, Camera, e\room\Objects[3], 4.0, 0.5)
							EndIf
							
							For i = 0 To 2
								If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True)) < 300.0 * RoomScale Then
									; ~ Play the activation sound
									If KillTimer >= 0.0 Then 
										PlayerSoundVolume = Max(8.0, PlayerSoundVolume)
										StopChannel(e\SoundCHN)
										e\SoundCHN = PlaySound2(TeslaActivateSFX, Camera, e\room\Objects[3], 4.0, 0.5)
										e\EventState = 1.0
										Exit
									EndIf
								EndIf
							Next
							
							Local Temp2% = True
							
							For e2.Events = Each Events
								If e2\EventName = e\EventName And e2 <> e
									If e2\EventStr <> ""
										Temp2 = False
										e\EventStr = "Done"
										Exit
									EndIf
								EndIf
							Next
							
							Local Temp3% = 0
							
							If Temp2
								If e\EventStr = "" And PlayerRoom = e\room
									If EntityDistance(e\room\Objects[5], Collider) < EntityDistance(e\room\Objects[6], Collider)
										Temp3 = 6
									Else
										Temp3 = 5
									EndIf
									
									e\room\NPC[0] = CreateNPC(NPCtypeClerk, EntityX(e\room\Objects[Temp3], True), 0.5, EntityZ(e\room\Objects[Temp3], True))
									PointEntity(e\room\NPC[0]\Collider, e\room\Objects[2])
									e\room\NPC[0]\State = 2.0
									e\EventStr = "Step1"
									e\EventState = 0.0
									e\EventState2 = 0.0
									e\EventState3 = 0.0
								EndIf
							EndIf
						Else
							HideEntity(e\room\Objects[4])
						EndIf
						
						If Curr106\State < -10.0 And e\EventState = 0.0 Then 
							For i = 0 To 2
								If Distance(EntityX(Curr106\Collider), EntityZ(Curr106\Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True)) < 300.0 * RoomScale Then
									; ~ Play the activation sound
									If KillTimer >= 0.0 Then 
										StopChannel(e\SoundCHN)
										e\SoundCHN = PlaySound2(TeslaActivateSFX, Camera, e\room\Objects[3], 4.0, 0.5)
										HideEntity(e\room\Objects[4])
										e\EventState = 1.0
										Curr106\State = 70 * 60.0 * Rnd(10.0, 13.0)
										GiveAchievement(AchvTesla)
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						e\EventState = e\EventState + FPSfactor
						If e\EventState =< 40.0 Then
							HideEntity(e\room\Objects[3])
							If (MilliSecs2() Mod 100) < 50 Then
								ShowEntity(e\room\Objects[4])
							Else
								HideEntity(e\room\Objects[4])
							EndIf
						Else
							If e\room\Dist < 2.0
								If e\EventState - FPSfactor =< 40.0 Then PlaySound_Strict(TeslaShockSFX)	
							Else
								If e\EventState - FPSfactor =< 40.0 Then PlaySound2(TeslaShockSFX, Camera, e\room\Objects[2])
							EndIf
							If e\EventState < 70.0 Then 
								If KillTimer >= 0.0 Then 
									For i = 0 To 2
										If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True)) < 250.0 * RoomScale Then
											ShowEntity(ov\OverlayID[7])
											LightFlash = 0.4
											CameraShake = 1.0
											Kill()
											DeathMSG = "Subject D-9341 killed by the Tesla gate at [REDACTED]."
										EndIf
									Next
								EndIf
								
								If e\EventStr = "Step1"
									e\room\NPC[0]\State = 3.0
								EndIf
								
								If Curr106\State < -10.0 Then
									For i = 0 To 2
										If Distance(EntityX(Curr106\Collider), EntityZ(Curr106\Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True)) < 250.0 * RoomScale Then
											ShowEntity(ov\OverlayID[7])
											LightFlash = 0.3
											If ParticleAmount > 0
												For i = 0 To 5 + (5 * (ParticleAmount - 1))
													p.Particles = CreateParticle(EntityX(Curr106\Collider, True), EntityY(Curr106\Collider, True), EntityZ(Curr106\Collider, True), 0, 0.015, -0.2, 250.0)
													p\Size = 0.03 : p\Gravity = -0.2 : p\LifeTime = 200 : p\SizeChange = 0.005 : p\Speed = 0.001
													RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0, True)
												Next
											EndIf
											Curr106\State = -20000.0
											TranslateEntity(Curr106\Collider, 0.0, -50.0, 0.0, True)
										EndIf
									Next								
								EndIf
								
								HideEntity(e\room\Objects[3])
								HideEntity(e\room\Objects[4])
								
								If Rand(5) < 5 Then 
									PositionTexture(TeslaTexture, 0.0, Rnd(0.0, 1.0))
									ShowEntity(e\room\Objects[3])			
								EndIf
							Else 
								If e\EventState - FPSfactor < 70 * 1.0 Then 
									StopChannel(e\SoundCHN)	
									e\SoundCHN = PlaySound2(TeslaPowerUpSFX, Camera, e\room\Objects[3], 4.0, 0.5)
								EndIf 
								HideEntity(e\room\Objects[3])
								
								If e\EventState > 150.0 Then e\EventState = 0.0
							EndIf
						EndIf
					EndIf
				Else
					HideEntity(e\room\Objects[4])
				EndIf
				
				If e\room\NPC[0] <> Null
					If e\EventStr = "Step1" And e\room\NPC[0]\State <> 3.0
						If e\EventState = 0.0
							For i = 0 To 2
								If Distance(EntityX(e\room\NPC[0]\Collider), EntityZ(e\room\NPC[0]\Collider), EntityX(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True)) < 400.0 * RoomScale
									StopChannel(e\SoundCHN)
									e\SoundCHN = PlaySound2(TeslaActivateSFX, Camera, e\room\Objects[3], 4.0, 0.5)
									HideEntity(e\room\Objects[4])
									e\EventState = 1.0
									Exit
								EndIf
							Next
						EndIf
					ElseIf e\EventStr = "Step1" And e\room\NPC[0]\State = 3.0
						e\room\NPC[0]\CurrSpeed = 0.0
						AnimateNPC(e\room\NPC[0], 41.0, 60.0, 0.5, False)
						If e\room\NPC[0]\Frame = 60.0
							e\room\NPC[0]\IsDead = True
							e\EventStr = "Step2"
							SetNPCFrame(e\room\NPC[0], 57)
						EndIf
					ElseIf e\EventStr = "Step2"
						AnimateNPC(e\room\NPC[0], 57.0, 60.0, 0.5, False)
						If e\room\NPC[0]\Frame = 60.0
							e\EventStr = "0"
						EndIf
					ElseIf e\EventStr <> "" And e\EventStr <> "Step1" And e\EventStr <> "Done"
						If Float(e\EventStr) < 70 * 10.0 Then
							If ParticleAmount > 0 Then
								If Rand(20 - (10 * (ParticleAmount - 1))) = 1 Then
									p.Particles = CreateParticle(EntityX(e\room\NPC[0]\Collider), EntityY(e\room\NPC[0]\OBJ) + 0.05, EntityZ(e\room\NPC[0]\Collider), 0, 0.05, 0.0, 60.0)
									p\Speed = 0.002
									RotateEntity(p\Pvt, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0)
									MoveEntity(p\Pvt,Rnd(-0.1, 0.1), 0.0, 0.1 + Rnd(0.0, 0.5))
									RotateEntity(p\Pvt, -90.0, EntityYaw(e\room\NPC[0]\Collider), 0.0)
									p\Achange = -0.02
								EndIf
							EndIf
							e\EventStr = Float(e\EventStr) + FPSfactor
						Else
							e\EventStr = "Done"
						EndIf
					EndIf
				EndIf
				
				If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "room860" Then
					If e\EventState2 = 0 Then
						If e\EventState3 =< 0 Then 
							Temp = False
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtypeMTF Then
									If Abs(EntityX(n\Collider) - EntityX(e\room\OBJ, True)) < 4.0 Then
										If Abs(EntityZ(n\Collider) - EntityZ(e\room\OBJ, True)) < 4.0 Then
											Temp = True
											If e\EventState2 = 0.0 Then
												n\Sound = LoadSound_Strict("SFX\Character\MTF\Tesla0.ogg")
												PlayMTFSound(n\Sound, n)
												n\Idle = 70 * 10.0
												e\EventState2 = 70 * 100.0
											EndIf
										EndIf
									EndIf
								EndIf
							Next
							If Temp = False Then e\EventState2 = 70 * 3.5
							e\EventState3 = e\EventState3 + 140.0
						Else
							e\EventState3 = e\EventState3 - FPSfactor
						EndIf
					Else
						If e\EventState2 >= 70 * 92.0 And e\EventState2 - FPSfactor < 70 * 92.0
							PlayAnnouncement("SFX\Character\MTF\Tesla" + Rand(1, 3) + ".ogg")
						EndIf
						
						e\EventState2 = Max(e\EventState2 - FPSfactor, 0.0)
					EndIf					
				EndIf
				;[End Block]
			Case "room2trick"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityDistance(e\room\OBJ, Collider) < 2.0 Then
						If EntityDistance(Collider, Curr173\OBJ) < 6.0 Or EntityDistance(Collider, Curr106\OBJ) < 6.0 Then
							RemoveEvent(e)
						Else
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(Collider), EntityY(Collider), EntityZ(Collider))
							PointEntity(Pvt, e\room\OBJ)
							RotateEntity(Pvt, 0.0, EntityYaw(Pvt), 0.0, True)
							MoveEntity(Pvt, 0.0, 0.0, EntityDistance(Pvt,e\room\OBJ) * 2.0)
							
							BlinkTimer = -10.0
							
							PlaySound_Strict(HorrorSFX(11))
							
							PositionEntity(Collider, EntityX(Pvt), EntityY(Pvt) + 0.05, EntityZ(Pvt))
							UpdateWorld()
							
							TurnEntity(Collider, 0.0, 180.0, 0.0)
							
							FreeEntity(Pvt)
							RemoveEvent(e)							
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2tunnel"	
				;[Block]
				If EntityY(Collider, True) >= 8.0 And EntityY(Collider, True) =< 12.0 Then
					If (EntityX(Collider, True) >= e\room\x - 6.0) And (EntityX(Collider, True) =< (e\room\x + 2.0 * GridSZ + 6.0)) Then
						If (EntityZ(Collider, True) >= e\room\z - 6.0) And (EntityZ(Collider, True) =< (e\room\z + 2.0 * GridSZ + 6.0)) Then
							PlayerRoom = e\room
						EndIf
					EndIf
				EndIf
				
				If PlayerRoom = e\room Then
					Local Meshes%[7]
					Local TempStr$
					
					Local ia%, ib%, ic%, id%
					Local dr.Doors
					
					Local TempInt%, TempInt2%
					Local iX%, iY%
					
					If I_Zone\HasCustomMT Then
						If e\room\grid\Meshes[0] = 0 Then
							PlaceGrid_MapCreator(e\room)
						EndIf
					EndIf
					
					If e\room\grid = Null Then
						e\room\grid = New Grids
						
						OldSeed% = RndSeed()
						SeedRnd(GenerateSeedNumber(RandomSeed))
						
						Local Dir%
						
						Dir = Rand(0, 1) Shl 1
						; ~ 0 = right
						; ~ 1 = up
						; ~ 2 = left
						; ~ 3 = down
						
						iX = GridSZ / 2 + Rand(-2, 2)
						iY = GridSZ / 2 + Rand(-2, 2)
						
						e\room\grid\Grid[iX + (iY * GridSZ)] = 1
						
						If Dir = 2 Then e\room\grid\Grid[(iX + 1) + (iY * GridSZ)] = 1 Else e\room\grid\Grid[(iX - 1) + (iY * GridSZ)] = 1
						
						Local Count% = 2
						
						While Count < 100
							TempInt = Rand(1, 5) Shl Rand(1, 2)
							For i = 1 To TempInt
								TempInt2 = True
								
								Select Dir
									Case 0
										;[Block]
										If iX < GridSZ - 2 - (i Mod 2) Then iX = iX + 1 Else TempInt2 = False
										;[End Block]
									Case 1
										;[Block]
										If iY < GridSZ - 2 - (i Mod 2) Then iY = iY + 1 Else TempInt2 = False
										;[End Block]
									Case 2
										;[Block]
										If iX > 1 + (i Mod 2) Then iX = iX - 1 Else TempInt2 = False
										;[End Block]
									Case 3
										;[Block]
										If iY > 1 + (i Mod 2) Then iY = iY - 1 Else TempInt2 = False
										;[End Block]
								End Select
								
								If TempInt2 Then
									If e\room\grid\Grid[iX + (iY * GridSZ)] = 0 Then
										e\room\grid\Grid[iX + (iY * GridSZ)] = 1
										Count = Count + 1
									EndIf
								Else
									Exit
								EndIf
							Next
							Dir = Dir + ((Rand(0, 1) Shl 1) - 1)
							While Dir < 0
								Dir = Dir + 4
							Wend
							While Dir > 3
								Dir = Dir - 4
							Wend
						Wend
						
						; ~ Generate the tunnels
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Grid[iX + (iY * GridSZ)] > 0 Then
									e\room\grid\Grid[iX + (iY * GridSZ)] = (e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)] > 0) + (e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)] > 0) + (e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)] > 0) + (e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)] > 0)
								EndIf
							Next
						Next
						
						Local MaxX% = GridSZ - 1
						Local CanRetry% = 0
						
						For iX = 0 To MaxX
							For iY = 0 To GridSZ - 1
								If e\room\grid\Grid[iX + 1 + (iY * GridSZ)] > 0 Then
									MaxX = iX
									If (e\room\grid\Grid[iX + 1 + ((iY + 1) * GridSZ)] < 3) And (e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 3) Then
										CanRetry = 1
										If Rand(0, 1) = 1 Then
											e\room\grid\Grid[iX + 1 + ((iY) * GridSZ)] = e\room\grid\Grid[iX + 1 + ((iY) * GridSZ)] + 1
											e\room\grid\Grid[iX + ((iY) * GridSZ)] = 7 ; ~ Generator room
											CanRetry = 0
											Exit
										EndIf
									EndIf
								EndIf
							Next
							If CanRetry Then iX = iX - 1
						Next
						
						Local FirstX%, LastX%
						Local FirstY%, LastY%
						
						FirstX = -1
						LastY = -1
						FirstX = -1
						LastY = -1
						
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Grid[iX + (iY * GridSZ)] = 2 Then
									If e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)] > 0 And e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)] > 0 Then ; ~ Horizontal
										If FirstX = -1 Or FirstY = -1 Then
											If e\room\grid\Grid[iX - 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY - 1) * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY + 1) * GridSZ)] < 3 Then
												If e\room\grid\Grid[iX - 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX - 1 + ((iY + 1) * GridSZ)] < 1 Then
													FirstX = iX : FirstY = iY
												EndIf
											EndIf
										EndIf
										If e\room\grid\Grid[iX - 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY - 1) * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY + 1) * GridSZ)] < 3 Then
											If e\room\grid\Grid[iX - 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX - 1 + ((iY + 1) * GridSZ)] < 1 Then
												LastX = iX : LastY = iY
											EndIf
										EndIf
									ElseIf e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)] > 0 And e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)] > 0 Then ; ~ Vertical
										If FirstX = -1 Or FirstY = -1 Then
											If e\room\grid\Grid[iX - 1 + (iY * Gridsz)] < 3 And e\room\grid\grid[iX + 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY - 1) * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY + 1) * GridSZ)] < 3 Then
												If e\room\grid\Grid[iX - 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX - 1 + ((iY + 1) * GridSZ)] < 1 Then
													FirstX = iX : FirstY = iY
												EndIf
											EndIf
										EndIf
										If e\room\grid\Grid[iX - 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + 1 + (iY * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY - 1) * GridSZ)] < 3 And e\room\grid\Grid[iX + ((iY + 1) * GridSZ)] < 3 Then
											If e\room\grid\Grid[iX - 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX + 1 + ((iY - 1) * GridSZ)] < 1 And e\room\grid\Grid[iX - 1 + ((iY + 1) * GridSZ)] < 1 Then
												LastX = iX : LastY = iY
											EndIf
										EndIf
									EndIf
								EndIf
							Next
						Next
						
						If LastX = FirstX And LastY = FirstY Then
							RuntimeError("The maintenance tunnels could not be generated properly!")
						EndIf
						
						; ~ Place the tunnels
						For i = 0 To 6
							Meshes[i] = CopyEntity(o\MTModelID[i])
							HideEntity(Meshes[i])
						Next
						FreeTextureCache
						
						TempInt = 0
						
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Grid[iX + (iY * GridSZ)] > 0 Then
									Select e\room\grid\Grid[iX + (iY * GridSZ)]
										Case 1, 7
											;[Block]
											TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)] - 1])
											
											If e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)] > 0 Then
												RotateEntity(TempInt, 0.0, 90.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 1
											ElseIf e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)] > 0 Then
												RotateEntity(TempInt, 0.0, 270.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 3
											ElseIf e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)] > 0 Then
												RotateEntity(TempInt, 0.0, 180.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 2
											Else
												RotateEntity(TempInt, 0.0, 0.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 0
											EndIf
											;[End Block]
										Case 2
											;[Block]
											If (iX = FirstX And iY = FirstY) Or (iX = LastX And iY = LastY) Then
												e\room\grid\Grid[iX + (iY * GridSZ)] = 6
											EndIf
											
											If e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)] > 0 And e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)] > 0 Then ; ~ Horizontal
												TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)] - 1])
												
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
												
												TempInt2 = Rand(0, 1)
												RotateEntity(TempInt,0.0, TempInt2 * 180.0 + 90.0, 0.0)
												
												e\room\grid\Angles[iX + (iY * GridSZ)] = (TempInt2 * 2) + 1
											ElseIf e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)] > 0 And e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)] > 0 Then ; ~ Vertical
												TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)] - 1])
												
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
												
												TempInt2 = Rand(0, 1)
												RotateEntity(TempInt, 0.0, TempInt2 * 180.0, 0.0)
												e\room\grid\Angles[iX + (iY * Gridsz)] = (TempInt2 * 2)
											Else
												TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)]])
												
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (412.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
												
												ia = e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)]
												ib = e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)]
												ic = e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)]
												id = e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)]
												
												If ia > 0 And ic > 0 Then
													RotateEntity(TempInt, 0.0, 0.0, 0.0)
													e\room\grid\Angles[iX + (iY * GridSZ)] = 0
												ElseIf ia > 0 And id > 0 Then
													RotateEntity(TempInt, 0.0, 90.0, 0.0)
													e\room\grid\Angles[iX + (iY * GridSZ)] = 1
												ElseIf ib > 0 And ic > 0 Then
													RotateEntity(TempInt, 0.0, 270.0, 0.0)
													e\room\grid\Angles[iX + (iY * GridSZ)] = 3
												Else
													RotateEntity(TempInt, 0.0, 180.0, 0.0)
													e\room\grid\Angles[iX + (iY * GridSZ)] = 2
												EndIf
											EndIf
											
											If (iX = FirstX And iY = FirstY) Then
												e\room\grid\Grid[iX + (iY * GridSZ)] = 5
											EndIf
											;[End Block]
										Case 3
											;[Block]
											TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)]])
											
											ia = e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)]
											ib = e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)]
											ic = e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)]
											id = e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)]
											If ia > 0 And ic > 0 And id > 0 Then
												RotateEntity(TempInt, 0.0, 90.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 1
											ElseIf ib > 0 And ic > 0 And id > 0 Then
												RotateEntity(TempInt, 0.0, 270.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 3
											ElseIf ic > 0 And ia > 0 And ib > 0 Then
												RotateEntity(TempInt, 0.0, 0.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 0
											Else
												RotateEntity(TempInt, 0.0, 180.0, 0.0)
												e\room\grid\Angles[iX + (iY * GridSZ)] = 2
											EndIf
											;[End Block]
										Case 4
											;[Block]
											TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)]])
											
											TempInt2 = Rand(0, 3)
											RotateEntity(TempInt, 0.0, TempInt2 * 90.0, 0.0)
											
											e\room\grid\Angles[iX + (iY * GridSZ)] = TempInt2
											;[End Block]
									End Select
									
									ScaleEntity(TempInt, RoomScale, RoomScale, RoomScale, True)
									PositionEntity(TempInt, e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
									
									Select e\room\grid\Grid[iX + (iY * GridSZ)]
										Case 1
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											;[End Block]
										Case 3, 4
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0, 8.0 + (412.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											;[End Block]
										Case 7
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0 - (Sin(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Cos(EntityYaw(TempInt, True)) * 16.0 * RoomScale), 8.0 + (396.0 * RoomScale), e\room\z + iY * 2.0 + (Cos(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Sin(EntityYaw(TempInt, True)) * 16.0 * RoomScale), 2, 500.0 * RoomScale, 255, 200, 200)
											it = CreateItem("SCP-500-01", "scp500", e\room\x + iX * 2.0 + (Cos(EntityYaw(TempInt, True)) * (-208.0) * RoomScale) - (Sin(EntityYaw(TempInt, True)) * 1226.0 * RoomScale), 8.0 + (80.0 * RoomScale), e\room\z + iY * 2.0 + (Sin(EntityYaw(TempInt, True)) * (-208.0) * RoomScale) + (Cos(EntityYaw(TempInt, True)) * 1226.0 * RoomScale))
											EntityType(it\Collider, HIT_ITEM)
											
											it = CreateItem("Night Vision Goggles", "nvgoggles", e\room\x + iX * 2.0 - (Sin(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Cos(EntityYaw(TempInt, True)) * 16.0 * RoomScale), 8.0 + (80.0 * RoomScale), e\room\z + iY * 2.0 + (Cos(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Sin(EntityYaw(TempInt, True)) * 16.0 * RoomScale))
											EntityType(it\Collider, HIT_ITEM)
											;[End Block]
									End Select
									
									If e\room\grid\Grid[iX + (iY * GridSZ)] = 6 Or e\room\grid\Grid[iX + (iY * GridSZ)] = 5 Then
										dr = CreateDoor(e\room\Zone, e\room\x + (iX * 2.0) + (Cos(EntityYaw(TempInt, True)) * 240.0 * RoomScale), 8.0, e\room\z + (iY * 2.0) + (Sin(EntityYaw(TempInt, True)) * 240.0 * RoomScale), EntityYaw(TempInt, True) + 90.0, Null, False, 3)
										PositionEntity(dr\Buttons[0], EntityX(dr\Buttons[0], True) + (Cos(EntityYaw(TempInt, True)) * 0.05), EntityY(dr\Buttons[0], True) + 0.0, EntityZ(dr\Buttons[0], True) + (Sin(EntityYaw(TempInt, True)) * 0.05), True)
										
										AddLight(Null, e\room\x + iX * 2.0 + (Cos(EntityYaw(TempInt, True)) * 555.0 * RoomScale), 8.0 + (469.0 * RoomScale), e\room\z + iY * 2.0 + (Sin(EntityYaw(TempInt, True)) * 555.0 * RoomScale), 2, 600.0 * RoomScale, 255, 255, 255)
										
										TempInt2 = CreatePivot()
										RotateEntity(TempInt2, 0.0, EntityYaw(TempInt, True) + 180.0, 0, True)
										PositionEntity(TempInt2, e\room\x + (iX * 2.0) + (Cos(EntityYaw(TempInt, True)) * 552.0 * RoomScale), 8.0 + (240.0 * RoomScale), e\room\z + (iY * 2.0) + (Sin(EntityYaw(TempInt, True)) * 552.0 * RoomScale))
										If e\room\grid\Grid[iX + (iY * GridSZ)] = 6 Then
											If e\room\RoomDoors[1] = Null Then
												dr\Open = (Not e\room\RoomDoors[0]\Open)
												e\room\RoomDoors[1] = dr
											Else
												RemoveDoor(dr)
											EndIf
											If e\room\Objects[3] = 0 Then
												e\room\Objects[3] = TempInt2
												PositionEntity(e\room\Objects[1], e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
											Else
												FreeEntity(TempInt2)
											EndIf
										Else
											If e\room\RoomDoors[3] = Null Then
												dr\Open = (Not e\room\RoomDoors[2]\Open)
												e\room\RoomDoors[3] = dr
											Else
												RemoveDoor(dr)
											EndIf
											If e\room\Objects[5] = 0 Then
												e\room\Objects[5] = TempInt2
												PositionEntity(e\room\Objects[0], e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
											Else
												FreeEntity(TempInt2)
											EndIf
										EndIf
									EndIf
									
									e\room\grid\Entities[iX + (iY * GridSZ)] = TempInt
									
									wayp.WayPoints = CreateWaypoint(e\room\x + (iX * 2.0), 8.2, e\room\z + (iY * 2.0), Null, e\room)
									
									e\room\grid\waypoints[iX + (iY * GridSZ)] = wayp
									
									If iY < GridSZ - 1 Then
										If e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iY > 0 Then
										If e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iX > 0 Then
										If e\room\grid\waypoints[iX - 1 + (iY * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX - 1 + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY*GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iX < GridSZ - 1 Then
										If e\room\grid\waypoints[iX + 1 + (iY * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + 1 + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							Next
						Next
						
						For i = 0 To 6
							e\room\grid\Meshes[i] = Meshes[i]
						Next
						
						PositionEntity(e\room\Objects[0], e\room\x + FirstX * 2.0, 8.0, e\room\z + FirstY * 2.0, True)
						PositionEntity(e\room\Objects[1], e\room\x + LastX * 2.0, 8.0, e\room\z + LastY * 2.0, True)
					Else If e\room\grid\Meshes[0] = 0 Then
						; ~ Place the tunnels
						For i = 0 To 6
							Meshes[i] = CopyEntity(o\MTModelID[i])
							HideEntity(Meshes[i])
						Next
						
						FreeTextureCache
						
						TempInt = 0
						
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Grid[iX + (iY * GridSZ)] > 0 Then
									Select e\room\grid\Grid[iX + (iY * GridSZ)]
										Case 1, 7
											;[Block]
											TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)] - 1])
										Case 2
											If e\room\grid\Grid[(iX + 1) + ((iY) * GridSZ)] > 0 And e\room\grid\Grid[(iX - 1) + ((iY) * GridSZ)] > 0 Then ; ~ Horizontal
												TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)] - 1])
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											ElseIf e\room\grid\Grid[(iX) + ((iY + 1) * GridSZ)] > 0 And e\room\grid\Grid[(iX) + ((iY - 1) * GridSZ)] > 0 Then ; ~ Vertical
												TempInt = CopyEntity(Meshes[e\room\grid\grid[iX + (iY * GridSZ)] - 1])
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											Else
												TempInt = CopyEntity(Meshes[e\room\grid\grid[iX + (iY * GridSZ)]])
												AddLight(Null, e\room\x + iX * 2.0, 8.0 + (412.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											EndIf
											;[End Block]
										Case 3, 4
											;[Block]
											TempInt = CopyEntity(Meshes[e\room\grid\Grid[iX + (iY * GridSZ)]])
											;[End Block]
										Case 5, 6
											;[Block]
											TempInt = CopyEntity(Meshes[5])
											;[End Block]
									End Select
									
									ScaleEntity(TempInt, RoomScale, RoomScale, RoomScale, True)
									
									RotateEntity(TempInt, 0.0, e\room\grid\Angles[iX + (iY * GridSZ)] * 90.0, 0.0)
									PositionEntity(TempInt, e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
									
									Select e\room\grid\Grid[iX + (iY * GridSZ)]
										Case 1, 5, 6
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0, 8.0 + (368.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											;[End Block]
										Case 3, 4
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0, 8.0 + (412.0 * RoomScale), e\room\z + iY * 2.0, 2, 500.0 * RoomScale, 255, 255, 255)
											;[End Block]
										Case 7
											;[Block]
											AddLight(Null, e\room\x + iX * 2.0 - (Sin(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Cos(EntityYaw(TempInt, True)) * 16.0 * RoomScale), 8.0 + (396.0 * RoomScale), e\room\z + iY * 2.0 + (Cos(EntityYaw(TempInt, True)) * 504.0 * RoomScale) + (Sin(EntityYaw(TempInt, True)) * 16.0 * RoomScale), 2, 500.0 * RoomScale, 255, 200, 200)
											;[End Block]
									End Select
									
									If e\room\grid\Grid[iX + (iY * GridSZ)] = 6 Or e\room\grid\Grid[iX + (iY * GridSZ)] = 5 Then
										dr = CreateDoor(e\room\Zone, e\room\x + (iX * 2.0) + (Cos(EntityYaw(TempInt, True)) * 240.0 * RoomScale), 8.0, e\room\z + (iY * 2.0) + (Sin(EntityYaw(TempInt, True)) * 240.0 * RoomScale), EntityYaw(TempInt, True) + 90.0, Null, False, 3)
										PositionEntity(dr\Buttons[0], EntityX(dr\Buttons[0], True) + (Cos(EntityYaw(TempInt, True)) * 0.05), EntityY(dr\Buttons[0], True) + 0.0, EntityZ(dr\Buttons[0], True) + (Sin(EntityYaw(TempInt, True)) * 0.05), True)
										
										AddLight(Null, e\room\x + iX * 2.0 + (Cos(EntityYaw(TempInt, True)) * 555.0 * RoomScale), 8.0 + (469.0 * RoomScale), e\room\z + iY * 2.0 + (Sin(EntityYaw(TempInt, True)) * 555.0 * RoomScale), 2, 600.0 * RoomScale, 255, 255, 255)
										
										TempInt2 = CreatePivot()
										RotateEntity(TempInt2, 0.0, EntityYaw(TempInt, True) + 180.0, 0.0, True)
										PositionEntity(TempInt2, e\room\x + (iX * 2.0) + (Cos(EntityYaw(TempInt, True)) * 552.0 * RoomScale), 8.0 + (240.0 * RoomScale), e\room\z + (iY * 2.0) + (Sin(EntityYaw(TempInt, True)) * 552.0 * RoomScale))
										If e\room\grid\Grid[iX + (iY * GridSZ)] = 6 Then
											If e\room\RoomDoors[1] = Null Then
												dr\Open = (Not e\room\RoomDoors[0]\Open)
												e\room\RoomDoors[1] = dr
											Else
												RemoveDoor(dr)
											EndIf
											If e\room\Objects[3] = 0 Then
												e\room\Objects[3] = TempInt2
												PositionEntity(e\room\Objects[1], e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
											Else
												FreeEntity(TempInt2)
											EndIf
										Else
											If e\room\RoomDoors[3] = Null Then
												dr\Open = (Not e\room\RoomDoors[2]\Open)
												e\room\RoomDoors[3] = dr
											Else
												RemoveDoor(dr)
											EndIf
											If e\room\Objects[5] = 0 Then
												e\room\Objects[5] = TempInt2
												PositionEntity(e\room\Objects[0], e\room\x + iX * 2.0, 8.0, e\room\z + iY * 2.0, True)
											Else
												FreeEntity(TempInt2)
											EndIf
										EndIf
									EndIf
									
									e\room\grid\Entities[iX + (iY * GridSZ)] = TempInt
									
									wayp.WayPoints = CreateWaypoint(e\room\x + (iX * 2.0), 8.2, e\room\z + (iY * 2.0), Null, e\room)
									
									e\room\grid\waypoints[iX + (iY * GridSZ)] = wayp
									
									If iY < GridSZ - 1 Then
										If e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + ((iY + 1) * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iY > 0 Then
										If e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + ((iY - 1) * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iX > 0 Then
										If e\room\grid\waypoints[iX - 1 + (iY * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX - 1 + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX - 1 + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
									If iX < GridSZ - 1 Then
										If e\room\grid\waypoints[iX + 1 + (iY * GridSZ)] <> Null Then
											Dist = EntityDistance(e\room\grid\waypoints[iX + (iY * GridSZ)]\OBJ, e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\OBJ)
											For i = 0 To 3
												If e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + 1 + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]
													e\room\grid\waypoints[iX + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
											For i = 0 To 3
												If e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)] Then
													Exit
												ElseIf e\room\grid\waypoints[iX + (iY * GridSZ)]\connected[i] = Null Then
													e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\connected[i] = e\room\grid\waypoints[iX + (iY * GridSZ)]
													e\room\grid\waypoints[iX + 1 + (iY * GridSZ)]\Dist[i] = Dist
													Exit
												EndIf
											Next
										EndIf
									EndIf
								EndIf
							Next
						Next
						
						For i = 0 To 6
							e\room\grid\Meshes[i] = Meshes[i]
						Next
						
						SeedRnd(OldSeed)
						
						For it.Items = Each Items
							If (EntityY(it\Collider, True) >= 8.0) And (EntityY(it\Collider, True) =< 12.0) And (EntityX(it\Collider, True) >= e\room\x - 6.0) And (EntityX(it\Collider, True) =< (e\room\x + (2.0 * GridSZ) + 6.0)) And (EntityZ(it\Collider, True) >= e\room\z - 6.0) And (EntityZ(it\Collider, True) =< (e\room\z + (2.0 * GridSZ) + 6.0)) Then
								TranslateEntity(it\Collider, 0.0, 0.3, 0.0, True)
								ResetEntity(it\Collider)
							EndIf
						Next
					EndIf
					
					If EntityY(Collider, True) > 4.0 Then
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Entities[iX + (iY * GridSZ)] <> 0
									ShowEntity(e\room\grid\Entities[iX + (iY * GridSZ)])
								EndIf
							Next
						Next
						
						For r.Rooms = Each Rooms
							If r <> e\room
								HideEntity(r\OBJ)
							EndIf
						Next
						EntityAlpha(GetChild(e\room\OBJ, 2), 0.0)
						
						ShouldPlay = 7
						
						If e\EventState = 0 Then
							If EntityDistance(Collider, e\room\Objects[0]) < EntityDistance(Collider, e\room\Objects[1]) Then
								Temp = 0
							Else
								Temp = 1
							EndIf
							e\EventState = 2.0
							
							If (Not Contained106) Then 	
								de.Decals = CreateDecal(0, EntityX(e\room\Objects[Temp], True), EntityY(e\room\Objects[Temp], True) + 0.05, EntityZ(e\room\Objects[Temp], True), 90.0, Rand(360.0), 0.0)
								de\Size = 0.05 : de\SizeChange = 0.001 : EntityAlpha(de\OBJ, 0.8) : UpdateDecals()
								
								PositionEntity(Curr106\Collider, EntityX(e\room\Objects[Temp], True), EntityY(Collider, True) - 3.0, EntityZ(e\room\Objects[Temp], True))
								SetAnimTime(Curr106\OBJ, 110.0)
								Curr106\State = -0.1	
								Curr106\PrevY = EntityY(Collider)
							EndIf
							
							For i = 0 To 1
								Local spawnPoint.WayPoints = Null
								
								For x = i * ((GridSZ * GridSZ) / 5.0) To (GridSZ * GridSZ - 1)
									If Rand(2) = 1 And e\room\grid\waypoints[x] <> Null Then 
										spawnPoint = e\room	\grid\waypoints[x]
										x = GridSZ * GridSZ
									EndIf
								Next 
								If (spawnPoint <> Null) Then
									e\room\NPC[i] = CreateNPC(NPCtype966, EntityX(spawnPoint\OBJ, True), EntityY(spawnPoint\OBJ, True), EntityZ(spawnPoint\OBJ, True))
								EndIf
							Next
						EndIf
					Else
						For iY = 0 To GridSZ - 1
							For iX = 0 To GridSZ - 1
								If e\room\grid\Entities[iX + (iY * GridSZ)] <> 0
									HideEntity e\room\grid\Entities[iX + (iY * GridSZ)]
								EndIf
							Next
						Next
					EndIf
					
					e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[2], e\room\Objects[3], e, False)
					e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[4], e\room\Objects[5], e, False)
				Else
					If e\room\grid <> Null Then
						If e\room\grid\Meshes[0] <> 0 Then
							For iY = 0 To GridSZ - 1
								For iX = 0 To GridSZ - 1
									If e\room\grid\Entities[iX + (iY * GridSZ)] <> 0
										HideEntity(e\room\grid\Entities[iX + (iY * GridSZ)])
									EndIf
								Next
							Next
						EndIf
					EndIf
				EndIf 
				;[End Block]
			Case "room2pipes106"
				;[Block]
				If (Not Contained106) Then 
					If e\EventState = 0.0 Then
						If PlayerRoom = e\room Then e\EventState = 1.0
					Else
						e\EventState = (e\EventState + FPSfactor * 0.7)
						If e\EventState < 50.0 Then
							Curr106\Idle = 1
							PositionEntity(Curr106\Collider, EntityX(e\room\Objects[0], True), EntityY(Collider) - 0.15, EntityZ(e\room\Objects[0], True))
							PointEntity(Curr106\Collider, e\room\Objects[1])
							MoveEntity(Curr106\Collider, 0.0, 0.0, EntityDistance(e\room\Objects[0], e\room\Objects[1]) * 0.5 * (e\EventState / 50.0))
							AnimateNPC(Curr106, 284.0, 333.0, 0.02 * 35.0)
						ElseIf e\EventState < 200
							Curr106\Idle = True
							AnimateNPC(Curr106, 334.0, 494.0, 0.2)
							
							PositionEntity(Curr106\Collider, (EntityX(e\room\Objects[0], True) + EntityX(e\room\Objects[1], True)) / 2.0, EntityY(Collider) - 0.15, (EntityZ(e\room\Objects[0], True) + EntityZ(e\room\Objects[1], True)) / 2.0)
							RotateEntity(Curr106\Collider, 0.0, CurveValue(e\EventState, EntityYaw(Curr106\Collider), 30.0), 0.0, True)
							If EntityDistance(Curr106\Collider, Collider) < 4.0 Then
								Pvt = CreatePivot()
								PositionEntity(Pvt, EntityX(Curr106\Collider), EntityY(Curr106\Collider), EntityZ(Curr106\Collider))
								PointEntity(Pvt, Collider)
								If WrapAngle(EntityYaw(Pvt) - EntityYaw(Curr106\Collider)) < 80 Then
									Curr106\State = -11.0
									Curr106\Idle = 0
									PlaySound_Strict(HorrorSFX(10))
									e\EventState = 260.0
								EndIf
								FreeEntity(Pvt)
							EndIf
						ElseIf e\EventState < 250.0
							Curr106\Idle = True
							PositionEntity(Curr106\Collider, EntityX(e\room\Objects[0], True), EntityY(Collider) - 0.15, EntityZ(e\room\Objects[0], True))
							PointEntity(Curr106\Collider, e\room\Objects[1])
							MoveEntity(Curr106\Collider, 0.0, 0.0, EntityDistance(e\room\Objects[0], e\room\Objects[1]) * ((e\EventState - 150.0) / 100.0))
							AnimateNPC(Curr106, 284.0, 333.0, 0.02 * 35.0)
						EndIf
						ResetEntity(Curr106\Collider)
						
						PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), EntityY(Curr106\Collider) - 0.15, EntityZ(Curr106\Collider))
						RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\Collider), 0.0)
						
						If (e\EventState / 250.0) > 0.3 And ((e\EventState - FPSfactor * 0.7) / 250.0) =< 0.3 Then
							e\SoundCHN = PlaySound_Strict(HorrorSFX(6))
							BlurTimer = 800.0
							d.Decals = CreateDecal(0, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True), 0.0, e\room\Angle - 90.0, Rnd(360.0))
							d\Timer = 90000.0 : d\Alpha = 0.01 : d\AlphaChange = 0.005 : d\Size = 0.1 : d\SizeChange = 0.003
						EndIf
						
						If (e\EventState / 250.0) > 0.65 And ((e\EventState - FPSfactor * 0.7) / 250.0) =< 0.65 Then
							d.Decals = CreateDecal(0, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), 0.0, e\room\Angle + 90.0, Rnd(360.0))
							d\Timer = 90000.0 : d\Alpha = 0.01 : d\AlphaChange = 0.005 : d\Size = 0.1 : d\SizeChange = 0.003
						EndIf						
						If e\EventState > 250.0 Then Curr106\Idle = 0 : RemoveEvent(e)
					End If
				EndIf
				;[End Block]
			Case "room2pit106"
                ;[Block]
                If (Not Contained106) And Curr106\State > 0.0 Then 
                    If e\EventState = 0.0 Then
                        If PlayerRoom = e\room Then e\EventState = 1
                    Else
                        e\EventState = e\EventState + 1.0
                        PositionEntity(Curr106\Collider, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True), EntityZ(e\room\Objects[7], True))
                        ResetEntity(Curr106\Collider)
                        
                        PointEntity(Curr106\Collider, Camera)
                        TurnEntity(Curr106\Collider, 0.0, Sin(MilliSecs2() / 20) * 6.0, 0, True)
                        MoveEntity(Curr106\Collider, 0.0, 0.0, Sin(MilliSecs2() / 15) * 0.06)
                        PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), EntityY(Curr106\Collider) - 0.15, EntityZ(Curr106\Collider))
                        
                        RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\Collider), 0.0)
                        Curr106\Idle = True
                        AnimateNPC(Curr106, 334.0, 49.04, 0.3)
                        If e\EventState > 800.0 Then
                            If BlinkTimer < -5.0 Then Curr106\Idle = False : RemoveEvent(e)
                        EndIf
                    EndIf
                End If
                ;[End Block]
			Case "room2pit"
				;[Block]
				If Curr173\Idle = 0 Then 
					If e\room\Dist < 8.0  And e\room\Dist > 0.0 Then			
						If (Not EntityVisible(Curr173\Collider, Camera)) And (Not EntityVisible(e\room\Objects[6], Camera)) Then 
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[6], True), 0.5, EntityZ(e\room\Objects[6], True))
							ResetEntity(Curr173\Collider)
							RemoveEvent(e)
						EndIf
					End If
				EndIf
				;[End Block]
			Case "room3pitduck"
				;[Block]
				If PlayerRoom = e\room Then
					If e\room\Objects[2] = 0 Then
						e\room\Objects[2] =	CopyEntity(o\NPCModelID[25])
						ScaleEntity(e\room\Objects[2], 0.07, 0.07, 0.07)
						Tex = LoadTexture_Strict("GFX\npcs\duck(3).png")
						EntityTexture(e\room\Objects[2], Tex)
						FreeTexture(Tex)
						PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
						PointEntity(e\room\Objects[2], e\room\OBJ)
						RotateEntity(e\room\Objects[2], 0.0, EntityYaw(e\room\Objects[2], True), 0.0, True)
						
						LoadEventSound(e, "SFX\SCP\Joke\Saxophone.ogg")
					Else
						If EntityInView(e\room\Objects[2], Camera) = False Then
							e\EventState = e\EventState + FPSfactor
							If Rand(200) = 1 And e\EventState > 300.0 Then
								e\EventState = 0.0
								e\SoundCHN = PlaySound2(e\Sound, Camera, e\room\Objects[2], 6.0)
							EndIf
						Else
							If e\SoundCHN <> 0 Then
								If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
							EndIf
						EndIf						
					EndIf
				EndIf
				;[End Block]
			Case "room3pit1048"
				;[Block]
				If PlayerRoom = e\room Then
					If e\room\Objects[2] = 0 Then
						e\room\Objects[2] =	CopyEntity(o\NPCModelID[24])
						ScaleEntity(e\room\Objects[2], 0.05, 0.05, 0.05)
						SetAnimTime(e\room\Objects[2], 414.0)
						
						Local ImgPath$ = ItemsPath + "1048\1048_" + Rand(1, 26) + ".png"
						Local itt.ItemTemplates
						
						For itt.ItemTemplates = Each ItemTemplates
							If itt\Name = "Drawing" Then
								If itt\Img <> 0 Then FreeImage(itt\Img)	
								itt\Img = LoadImage_Strict(ImgPath)
								MaskImage(itt\Img, 255, 0, 255)
								itt\ImgPath = ImgPath
								Exit
							EndIf
						Next
						
						Tex% = LoadTexture_Strict(ImgPath)
						
						Local Brush% = LoadBrush_Strict(ImgPath, 1)
						
						For i = 1 To CountSurfaces(e\room\Objects[2])
							SF% = GetSurface(e\room\Objects[2], i)
							b% = GetSurfaceBrush(SF)
							t% = GetBrushTexture(b, 0)
							TexName$ = StripPath(TextureName(t))
							
							If Lower(TexName) = "1048_1.png" Then
								PaintSurface(SF, Brush)
							EndIf
							FreeBrush(b)
						Next
						FreeTexture(Tex)
						FreeBrush(Brush)
						
						PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
					Else
						PointEntity(e\room\Objects[2], Collider)
						RotateEntity(e\room\Objects[2], -90.0, EntityYaw(e\room\Objects[2], True), 0.0, True)
						
						If e\EventState = 0.0 Then
							If EntityDistance(Collider, e\room\Objects[2]) < 3.0 Then
								If EntityInView(e\room\Objects[2], Camera) Then 
									e\EventState = 1.0
									GiveAchievement(Achv1048)
								EndIf
							EndIf
						Else If e\EventState = 1.0
							Animate2(e\room\Objects[2], AnimTime(e\room\Objects[2]), 1.0, 205.0, 0.5, False)
							If AnimTime(e\room\Objects[2]) = 205.0 Then e\EventState = 2.0
						Else If e\EventState = 2.0
							Animate2(e\room\Objects[2], AnimTime(e\room\Objects[2]), 205.0, 353.0, 1.0)	
							If (EntityDistance(Collider, e\room\Objects[2]) < 1.5) Then
								DrawHandIcon = True
								If MouseHit1 Then
									If ItemAmount >= MaxItemAmount Then
										Msg = "You cannot carry any more items."
										MsgTimer = 70 * 5.0
									Else
										SelectedItem = CreateItem("Drawing", "paper", 0.0, 0.0, 0.0)
										EntityType(SelectedItem\Collider, HIT_ITEM)
										
										PickItem(SelectedItem)
										
										FreeEntity(e\room\Objects[2])
										e\room\Objects[2] = 0
										
										e\EventState = 3.0
										RemoveEvent(e)
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2poffices2"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState = 0.0 Then
						If e\room\RoomDoors[0]\Open = True Then 
							If e\room\RoomDoors[0]\Openstate = 180.0 Then 
								e\EventState = 1.0
								PlaySound_Strict(HorrorSFX(5))
							EndIf
						Else
							If EntityDistance(Collider, e\room\RoomDoors[0]\OBJ) < 1.5 And RemoteDoorOn Then
								e\room\RoomDoors[0]\Open = True
							EndIf
						EndIf
					Else
						If EntityDistance(e\room\Objects[0], Collider) < 2.0 Then
							HeartBeatVolume = CurveValue(0.5, HeartBeatVolume, 5.0)
							HeartBeatRate = CurveValue(120.0, HeartBeatRate, 150.0) 
							e\SoundCHN = LoopSound2(OldManSFX(4), e\SoundCHN, Camera, e\room\OBJ, 5.0, 0.3)
							Curr106\State = Curr106\State - FPSfactor * 3
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2servers"
				;[Block]
				If e\EventState = 0 Then
					If PlayerRoom = e\room Then
						; ~ Close the doors when the player enters the room
						UseDoor(e\room\RoomDoors[0], False)
						e\room\RoomDoors[0]\Locked = True
						UseDoor(e\room\RoomDoors[1], False)
						e\room\RoomDoors[1]\Locked = True
						
						If Curr096 = Null Then
							Curr096 = CreateNPC(NPCtype096, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True) + 0.1, EntityZ(e\room\Objects[6], True))
						Else
							PositionEntity(Curr096\Collider, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True) + 0.1, EntityZ(e\room\Objects[6], True), True)
						EndIf
						
						RotateEntity(Curr096\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
						ResetEntity(Curr096\Collider)
						Curr096\State = 6.0
						Curr096\State2 = 70 * 10.0
						
						LoadEventSound(e, "SFX\Character\Guard\096ServerRoom1.ogg")
						e\SoundCHN = PlaySound_Strict(e\Sound)
						
						e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True), EntityZ(e\room\Objects[7], True))
						
						GiveAchievement(Achv096)
						
						e\EventState = 1.0
					EndIf
				ElseIf e\EventState < 70 * 45.0
					If	Rand(200) < 5 And PlayerRoom = e\room Then 
						LightBlink = Rnd(1.0, 2.0)
						If Rand(5) = 1 Then PlaySound2(IntroSFX(Rand(8, 10)), Camera, e\room\OBJ, 8.0, Rnd(0.1, 0.3))
					EndIf
					
					e\EventState = Min(e\EventState + FPSfactor, 70 * 43.0)
					
					If e\room\NPC[0] <> Null Then
						Curr096\Target = e\room\NPC[0]
						
						If e\EventState < 70 * 8.0 Then
							AnimateNPC(Curr096, 472.0, 520.0, 0.25)
							PointEntity(e\room\NPC[0]\Collider, Curr096\Collider)
						ElseIf e\EventState >= 70 * 8.0 And e\EventState < 70 * 10.0
							; ~ Checking at which side the player is
							If EntityDistance(Collider, e\room\RoomDoors[0]\FrameOBJ) < EntityDistance(Collider, e\room\RoomDoors[1]\FrameOBJ)
								AnimateNPC(Curr096, 521.0, 555.0, 0.25, False)
								If Curr096\Frame >= 554.5
									e\EventState = 70 * 10.0
									Curr096\Frame = 677.0
									SetNPCFrame(Curr096, Curr096\Frame)
									Curr096\State = 1.0
									TurnEntity(Curr096\Collider, 0.0, 180.0, 0.0)
									MoveEntity(Curr096\Collider, 0.0, 0.0, 0.3)
								EndIf
							Else
								AnimateNPC(Curr096, 556.0, 590.0, 0.25, False)
								If Curr096\Frame >= 589.5
									e\EventState = 70 * 10.0
									Curr096\Frame = 677.0
									SetNPCFrame(Curr096, Curr096\Frame)
									Curr096\State = 1.0
									TurnEntity(Curr096\Collider, 0.0, 180.0, 0.0)
									MoveEntity(Curr096\Collider, 0.0, 0.0, 0.3)
								EndIf
							EndIf
							PointEntity(e\room\NPC[0]\Collider, Curr096\Collider)
						ElseIf e\EventState >= 70 * 10.0 And e\EventState < 70 * 20.0
							Curr096\State = Min(Max(1.0, Curr096\State), 3.0)
							Curr096\State2 = Max(Curr096\State2, 70 * 12.0)
							If e\EventState - FPSfactor =< 70 * 15.0 Then ; ~ Walk to the doorway
								If e\EventState > 70 * 15.0 Then
									e\room\NPC[0]\State = 14.0
									e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(Curr096\Collider, True), 0.4, EntityZ(Curr096\Collider, True))
									e\room\NPC[0]\PathTimer = 300.0
								Else
									PointEntity(e\room\NPC[0]\Collider, Curr096\Collider)
								EndIf
							EndIf
							If EntityVisible(e\room\NPC[0]\Collider, Curr096\Collider)
								e\room\RoomDoors[2]\Open = False
								e\room\NPC[0]\State = 13.0
								PointEntity(e\room\NPC[0]\OBJ, Curr096\Collider)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 30.0), 0.0)
							EndIf
						Else
							If Curr096\State = 4.0 Then ; ~ Shoot at SCP-096 when it starts attacking
								Curr096\LastSeen = 1.0
								e\room\NPC[0]\State = 2.0
								PointEntity(e\room\NPC[0]\OBJ, Curr096\Collider)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 30.0), 0.0)
								If PlayerRoom = e\room Then LightBlink = (e\room\NPC[0]\Reload) + Rnd(0.5, 2.0)
								Curr096\Target = e\room\NPC[0]
							Else
								If e\EventState > 70 * 22.0 Then Curr096\State = 4.0
								If e\room\NPC[0]\State = 13 Then
									e\room\NPC[0]\State = 14.0
									e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\OBJ, True), 0.4, EntityZ(e\room\OBJ, True))
									e\room\NPC[0]\PathTimer = 300.0
									e\room\NPC[0]\Speed = e\room\NPC[0]\Speed * 1.8 ; ~ Making the guard walking a bit faster
								EndIf
							EndIf
						EndIf
						
						If AnimTime(Curr096\OBJ) > 25 And AnimTime(Curr096\OBJ) < 150.0 Then
							If e\Sound <> 0 Then
								FreeSound_Strict(e\Sound) : e\Sound = 0
							EndIf
							e\Sound = LoadSound_Strict("SFX\Character\Guard\096ServerRoom2.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound)
							
							Curr096\CurrSpeed = 0
							
							For i = 0 To 6
								If e\room\Angle = 0 Or e\room\Angle = 180.0 Then
									de.Decals = CreateDecal(Rand(2, 3), e\room\x - Rnd(197.0, 199.0) * Cos(e\room\Angle) * RoomScale, 1.0, e\room\z + (140.0 * (i - 3)) * RoomScale, 0.0, e\room\Angle + 90.0, Rnd(360.0))
									de\Size = Rnd(0.8, 0.85) : de\SizeChange = 0.001
									de.Decals = CreateDecal(Rand(2, 3), e\room\x - Rnd(197.0, 199.0) * Cos(e\room\Angle) * RoomScale, 1.0, e\room\z + (140.0 * (i - 3)) * RoomScale, 0.0, e\room\Angle - 90.0, Rnd(360.0))
									de\Size = Rnd(0.8, 0.85) : de\SizeChange = 0.001
								Else
									de.Decals = CreateDecal(Rand(2, 3), e\room\x + (140.0 * (i - 3)) * RoomScale, 1.0, e\room\z - Rnd(197.0, 199.0) * Sin(e\room\Angle) * RoomScale - Rnd(0.001, 0.003), 0.0, e\room\Angle + 90.0, Rnd(360.0))
									de\Size = Rnd(0.8, 0.85) : de\SizeChange = 0.001
									de.Decals = CreateDecal(Rand(2, 3), e\room\x + (140.0 * (i - 3)) * RoomScale, 1.0, e\room\z - Rnd(197.0, 199.0) * Sin(e\room\Angle) * RoomScale - Rnd(0.001, 0.003), 0.0, e\room\Angle - 90.0, Rnd(360.0))
									de\Size = Rnd(0.8, 0.85) : de\SizeChange = 0.001
								EndIf
								de.Decals = CreateDecal(Rand(2, 3), EntityX(e\room\NPC[0]\Collider) + Rnd(-2.0, 2.0), Rnd(0.001, 0.003), EntityZ(e\room\NPC[0]\Collider) + Rnd(-2.0, 2.0), 90.0, Rnd(360.0), 0.0)
							Next
							de\Size = Rnd(0.5, 0.7)
							ScaleSprite(de\OBJ, de\Size, de\Size)
							
							Curr096\State = 5.0
							StopStream_Strict(Curr096\SoundCHN)
							Curr096\SoundCHN = 0
							
							RemoveNPC(e\room\NPC[0])
							e\room\NPC[0] = Null
						EndIf
					Else
						If e\EventState >= 70 * 40 And e\EventState - FPSfactor < 70 * 40 Then ; ~ Open them again to let the player in
							e\room\RoomDoors[0]\Locked = False
							e\room\RoomDoors[1]\Locked = False
							UseDoor(e\room\RoomDoors[0], False)
							UseDoor(e\room\RoomDoors[1], False)
							If e\Sound <> 0 Then
								FreeSound_Strict(e\Sound) : e\Sound = 0
							EndIf
							e\room\RoomDoors[0]\Locked = True
							e\room\RoomDoors[1]\Locked = True
						EndIf
						
						If PlayerRoom = e\room Then
							If e\SoundCHN <> 0 Then
								If ChannelPlaying(e\SoundCHN) = True Then 
									LightBlink = Rnd(0.5, 6.0)
									If Rand(50) = 1 Then PlaySound2(IntroSFX(Rand(8, 10)), Camera, e\room\OBJ, 8.0, Rnd(0.1, 0.3))
								EndIf
							EndIf						
							
							If e\room\Angle = 0 Or e\room\Angle = 180 Then ; ~ Lock the player inside
								If Abs(EntityX(Collider) - EntityX(e\room\OBJ, True)) > 1.3 Then 
									e\EventState = 70 * 50.0
									e\Sound = 0
								EndIf
							Else
								If Abs(EntityZ(Collider) - EntityZ(e\room\OBJ, True)) > 1.3 Then 
									e\EventState = 70 * 50.0
									e\Sound = 0
								EndIf
							EndIf	
						EndIf
					EndIf
					If ChannelPlaying(e\SoundCHN) = True Then
						UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[10], 10.0, 20.0)
					EndIf
				ElseIf PlayerRoom = e\room
					Temp = UpdateLever(e\room\Objects[1]) ; ~ Power switch
					x = UpdateLever(e\room\Objects[3]) ; ~ Fuel pump
					z = UpdateLever(e\room\Objects[5]) ; ~ Generator
					
					; ~ Fuel pump on
					If x Then
						e\EventState2 = Min(1.0, e\EventState2 + FPSfactor / 350.0)
						
						; ~ Generator on
						If z Then
							If e\Sound2 = 0 Then LoadEventSound(e, "SFX\General\GeneratorOn.ogg", 1)
							e\EventState3 = Min(1.0, e\EventState3 + FPSfactor / 450.0)
						Else
							e\EventState3 = Min(0.0, e\EventState3 - FPSfactor / 450.0)
						EndIf
					Else
						e\EventState2 = Max(0.0, e\EventState2 - FPSfactor / 350.0)
						e\EventState3 = Max(0.0, e\EventState3 - FPSfactor / 450.0)
					EndIf
					
					If e\EventState2 > 0.0 Then e\SoundCHN = LoopSound2(RoomAmbience[8], e\SoundCHN, Camera, e\room\Objects[3], 5.0, e\EventState2 * 0.8)
					If e\EventState3 > 0.0 Then e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\Objects[5], 6.0, e\EventState3)
					
					If Temp = 0 And x And z Then
						e\room\RoomDoors[0]\Locked = False
						e\room\RoomDoors[1]\Locked = False
					Else
						If Rand(200) < 5 Then LightBlink = Rnd(0.5, 1.0)
						
						If e\room\RoomDoors[0]\Open Then 
							e\room\RoomDoors[0]\Locked = False
							UseDoor(e\room\RoomDoors[0], False) 
						EndIf
						If e\room\RoomDoors[1]\Open Then 
							e\room\RoomDoors[1]\Locked = False
							UseDoor(e\room\RoomDoors[1], False)
						EndIf
						e\room\RoomDoors[0]\Locked = True
						e\room\RoomDoors[1]\Locked = True							
					EndIf 
				EndIf
				;[End Block]
			Case "room2storage"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState2 =< 0.0 Then
						e\room\RoomDoors[1]\Locked = False
						e\room\RoomDoors[4]\Locked = False
						
						If EntityDistance(Collider, Curr173\OBJ) < 8.0 Or EntityDistance(Collider, Curr106\OBJ) < 8.0 Then
							e\room\RoomDoors[1]\Locked = True
							e\room\RoomDoors[4]\Locked = True
						Else
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtypeMTF Then 
									If EntityDistance(Collider, Curr173\OBJ) < 8.0 Then 
										e\room\RoomDoors[1]\Locked = True
										e\room\RoomDoors[4]\Locked = True
										Exit
									EndIf
								EndIf
							Next
						EndIf
						e\EventState2 = 70 * 5.0
					Else
						e\EventState2 = e\EventState2 - FPSfactor
					EndIf
					
					LightVolume = TempLightVolume * 0.5
					
					TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0.0, e\room\OBJ)
					
					Temp = 0
					If TFormedX() > 730.0 Then
						GiveAchievement(Achv970)
						
						UpdateWorld()
						TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0.0, e\room\OBJ)
						
						For i = 1 To 2
							e\room\RoomDoors[i]\Open = e\room\RoomDoors[i + 2]\Open
							e\room\RoomDoors[i]\OpenState = e\room\RoomDoors[i + 2]\OpenState
							PositionEntity(e\room\RoomDoors[i]\OBJ, EntityX(e\room\RoomDoors[i + 2]\OBJ), EntityY(e\room\RoomDoors[i + 2]\OBJ), EntityZ(e\room\RoomDoors[i + 2]\OBJ))
							PositionEntity(e\room\RoomDoors[i]\OBJ2, EntityX(e\room\RoomDoors[i + 2]\OBJ2), EntityY(e\room\RoomDoors[i + 2]\OBJ2), EntityZ(e\room\RoomDoors[i + 2]\OBJ2))							
							
							e\room\RoomDoors[i + 2]\Open = False
							e\room\RoomDoors[i + 2]\OpenState = 0.0
							PositionEntity(e\room\RoomDoors[i + 2]\OBJ, EntityX(e\room\RoomDoors[0]\OBJ), EntityY(e\room\RoomDoors[0]\OBJ), EntityZ(e\room\RoomDoors[0]\OBJ))
							PositionEntity(e\room\RoomDoors[i + 2]\OBJ2, EntityX(e\room\RoomDoors[0]\OBJ2), EntityY(e\room\RoomDoors[0]\OBJ2), EntityZ(e\room\RoomDoors[0]\OBJ2))							
						Next	
						
						TFormPoint(TFormedX() - 1024.0, TFormedY(), TFormedZ(), e\room\OBJ, 0)
						HideEntity(Collider)
						PositionEntity(Collider, TFormedX(), EntityY(Collider), TFormedZ(), True)
						ShowEntity(Collider)
						
						Temp = True
					ElseIf TFormedX() < -730.0
						GiveAchievement(Achv970)
						
						UpdateWorld()
						TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0, e\room\OBJ)
						
						For i = 1 To 2
							e\room\RoomDoors[i + 2]\Open = e\room\RoomDoors[i]\Open
							e\room\RoomDoors[i + 2]\OpenState = e\room\RoomDoors[i]\OpenState
							PositionEntity(e\room\RoomDoors[i + 2]\OBJ, EntityX(e\room\RoomDoors[i]\OBJ), EntityY(e\room\RoomDoors[i]\OBJ), EntityZ(e\room\RoomDoors[i]\OBJ))
							PositionEntity(e\room\RoomDoors[i + 2]\OBJ2, EntityX(e\room\RoomDoors[i]\OBJ2), EntityY(e\room\RoomDoors[i]\OBJ2), EntityZ(e\room\RoomDoors[i]\OBJ2))							
							
							e\room\RoomDoors[i]\Open = False
							e\room\RoomDoors[i]\OpenState = 0.0
							PositionEntity(e\room\RoomDoors[i]\OBJ, EntityX(e\room\RoomDoors[0]\OBJ), EntityY(e\room\RoomDoors[0]\OBJ), EntityZ(e\room\RoomDoors[0]\OBJ))
							PositionEntity(e\room\RoomDoors[i]\OBJ2, EntityX(e\room\RoomDoors[0]\OBJ2), EntityY(e\room\RoomDoors[0]\OBJ2), EntityZ(e\room\RoomDoors[0]\OBJ2))							
						Next
						
						TFormPoint(TFormedX() + 1024.0, TFormedY(), TFormedZ(), e\room\OBJ, 0)
						HideEntity(Collider)
						PositionEntity(Collider, TFormedX(), EntityY(Collider), TFormedZ(), True)
						ShowEntity(Collider)
						
						Temp = True
					EndIf
					
					If Temp = True Then 
						e\EventState = e\EventState + 1
						For it.Items = Each Items
							If EntityDistance(it\Collider, Collider) < 5.0 Then
								TFormPoint(EntityX(it\Collider), EntityY(it\Collider), EntityZ(it\Collider), 0, e\room\OBJ)
								x = TFormedX() : y = TFormedY() : z = TFormedZ()
								If TFormedX() > 264.0 Then
									TFormPoint(x - 1024.0, y, z, e\room\OBJ, 0)
									PositionEntity(it\Collider, TFormedX(), TFormedY(), TFormedZ())
									ResetEntity(it\Collider)
								ElseIf TFormedX() < -264.0
									TFormPoint(x + 1024.0, y, z, e\room\OBJ, 0)
									PositionEntity(it\Collider, TFormedX(), TFormedY(), TFormedZ())
									ResetEntity(it\Collider)
								EndIf
							EndIf
						Next
						
						Select e\EventState 
							Case 2.0
								;[Block]
								i = Rand(MaxItemAmount)
								If Inventory(i) <> Null Then RemoveItem(Inventory(i))		
								;[End Block]
							Case 5.0
								;[Block]
								Injuries = Injuries + 0.3
								;[End Block]
							Case 10.0
								;[Block]
								de.Decals = CreateDecal(3, EntityX(e\room\OBJ) + Cos(e\room\Angle - 90.0) * 760.0 * RoomScale, 0.0005, EntityZ(e\room\OBJ) + Sin(e\room\Angle - 90.0) * 760.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
								;[End Block]
							Case 14.0
								;[Block]
								For i = 0 To MaxItemAmount - 1
									If Inventory(i) <> Null Then
										If Inventory(i)\ItemTemplate\TempName = "paper" Then
											RemoveItem(Inventory(i))
											For itt.ItemTemplates = Each ItemTemplates
												If itt\TempName = "paper" And Rand(6) = 1 Then
													Inventory(i) = CreateItem(itt\Name, itt\TempName, 1.0, 1.0, 1.0)
													HideEntity(Inventory(i)\Collider)
													Inventory(i)\Picked = True
													Exit
												EndIf
											Next
											Exit
										EndIf
									EndIf
								Next
								;[End Block]
							Case 18.0
								;[Block]
								TFormPoint(-344.0, 176.0, 272.0, e\room\OBJ, 0)
								it.Items = CreateItem("Strange Note", "paper", TFormedX(), TFormedY(), TFormedZ())
								EntityType(it\Collider, HIT_ITEM)
								;[End Block]
							Case 25.0
								;[Block]
								e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\OBJ) + Cos(e\room\Angle - 90.0) * 760.0 * RoomScale, 0.35, EntityZ(e\room\OBJ) + Sin(e\room\Angle - 90.0) * 760.0 * RoomScale)
								RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle - 200.0, 0.0, True)
								ChangeNPCTextureID(e\room\NPC[0], 1)
								SetAnimTime(e\room\NPC[0]\OBJ, 80.0)
								e\room\NPC[0]\State = 10.0
								;[End Block]
							Case 30.0
								;[Block]
								i = Rand(0, MaxItemAmount - 1)
								If Inventory(i) <> Null Then RemoveItem(Inventory(i))
								Inventory(i) = CreateItem("Strange Note", "paper", 1.0, 1.0, 1.0)
								HideEntity(Inventory(i)\Collider)
								Inventory(i)\Picked = True
								;[End Block]
							Case 35.0
								;[Block]
								For i = 0 To 3
									de.Decals = CreateDecal(17, e\room\x + Rnd(-2.0, 2.0), 700.0 * RoomScale, e\room\z + Rnd(-2.0, 2.0), 270.0, Rnd(360.0), 0.0)
									de\Size = 0.05 : de\SizeChange = 0.0005  : UpdateDecals()
									EntityAlpha(de\obj, 0.8)
								Next
								;[End Block]
							Case 40.0
								;[Block]
								PlaySound_Strict(LoadTempSound("SFX\Radio\Franklin4.ogg"))
								;[End Block]
							Case 50.0
								;[Block]
								e\room\NPC[1] = CreateNPC(NPCtypeGuard, EntityX(e\room\OBJ) + Cos(e\room\Angle + 90.0) * 600.0 * RoomScale, 0.35, EntityZ(e\room\OBJ) + Sin(e\room\Angle + 90.0) * 600.0 * roomScale)
								e\room\NPC[1]\State = 7.0
								;[End Block]
							Case 52.0
								;[Block]
								If e\room\NPC[1] <> Null Then
									RemoveNPC(e\room\NPC[1])
									e\room\NPC[1] = Null
								EndIf
								;[Block]
							Case 60.0
								;[Block]
								If (Not HalloweenTex) Then
									Local Tex970% = LoadTexture_Strict("GFX\npcs\scp_173_h.pt", 1)
									
									EntityTexture(Curr173\OBJ, Tex970, 0, 0)
									FreeTexture(Tex970)
								EndIf
								;[End Block]
						End Select
						
						If Rand(10) = 1 Then
							Temp = Rand(0, 2)
							PlaySound_Strict(AmbientSFX(Temp, Rand(0, AmbientSFXAmount(Temp) - 1)))
						EndIf
					Else
						If e\room\NPC[0] <> Null Then
							If EntityDistance(Collider, e\room\NPC[0]\Collider) < 3.0 Then
								If EntityInView(e\room\NPC[0]\OBJ, Camera) Then
									CurrCameraZoom = (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * 15.0
									HeartBeatVolume = Max(CurveValue(0.3, HeartBeatVolume, 2.0), HeartBeatVolume)
									HeartBeatRate = Max(HeartBeatRate, 120.0)
								EndIf
							EndIf
						EndIf
						
						If e\room\NPC[1] <> Null Then
							PointEntity(e\room\NPC[1]\OBJ, Collider)
							RotateEntity(e\room\NPC[1]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[1]\OBJ), EntityYaw(e\room\NPC[1]\Collider), 35.0), 0.0)
						EndIf
						
						For it.Items = Each Items
							If (it\Dropped = 1 And Abs(TFormedX()) < 264.0) Or it\Dropped = -1 Then
								TFormPoint(EntityX(it\Collider), EntityY(it\Collider), EntityZ(it\Collider), 0, e\room\OBJ)
								x = TFormedX() : y = TFormedY() : z = TFormedZ()
								
								If it\Dropped = 1 Then
									For i = - 1 To 1 Step 2
										TFormPoint(x + 1024.0 * i, y, z, e\room\OBJ, 0)
										it2.Items = CreateItem(it\Name, it\ItemTemplate\TempName, TFormedX(), EntityY(it\Collider), TFormedZ())
										RotateEntity(it2\Collider, EntityPitch(it\Collider), EntityYaw(it\Collider), 0)
										EntityType(it2\Collider, HIT_ITEM)
									Next
								Else
									For it2.Items = Each Items
										If it2 <> it And it2\Dist < 15.0 Then
											TFormPoint(EntityX(it2\Collider), EntityY(it2\Collider), EntityZ(it2\Collider), 0.0, e\room\OBJ)
										EndIf
									Next
								EndIf
								Exit
							EndIf
						Next
					EndIf
				EndIf
				
				If e\EventState > 26.0 Then
					If Abs(EntityX(Collider) - e\room\x) < 8.0 Then
						If Abs(EntityZ(Collider) - e\room\z) < 8.0 Then
							If e\Sound = 0 Then
								e\Sound = LoadSound_Strict("SFX\SCP\970\Corpse.ogg")
							EndIf
							e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\OBJ)
							If e\EventState < 30.0 Then
								LightVolume = TempLightVolume * 0.4
							ElseIf e\EventState > 60.0
								AnimateNPC(e\room\NPC[0], 80.0, 61.0, -0.02, False)
								e\room\NPC[0]\DropSpeed = 0.0
								y = CurveValue(1.5 + Sin(Float(MilliSecs2()) / 20.0) * 0.1, EntityY(e\room\NPC[0]\Collider), 50.0)
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\NPC[0]\Collider), y, EntityZ(e\room\NPC[0]\Collider))
								TurnEntity(e\room\NPC[0]\Collider, 0.0, 0.1 * FPSfactor, 0.0)
							EndIf 								
						EndIf
					EndIf
				EndIf					
				;[End Block]
			Case "room3door"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityDistance(e\room\OBJ, Collider) < 2.5 Then
						For do.Doors = Each Doors
							If Abs(EntityX(do\OBJ, True) - EntityX(Collider)) < 2.0 Then
								If Abs(EntityZ(do\OBJ, True) - EntityZ(Collider)) < 2.0 Then
									If (Not EntityInView(do\OBJ, Camera)) Then
										If do\Open Then
											do\Open = False
											do\OpenState = 0.0
											BlurTimer = 100.0
											CameraShake = 3.0											
										EndIf
									EndIf
									Exit
								EndIf
							EndIf
						Next
						RemoveEvent(e)
					EndIf
				EndIf
				;[End Block]
			Case "room3servers"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState3 = 0.0 And Curr173\Idle = 0 Then
						If BlinkTimer < -10.0 Then 
							Temp = Rand(0, 2)
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[Temp], True), EntityY(e\room\Objects[Temp], True), EntityZ(e\room\Objects[Temp], True))
							ResetEntity(Curr173\Collider)
							e\EventState3 = 1.0
						EndIf
					EndIf
					
					If e\room\Objects[3] > 0 Then 
						If BlinkTimer < -8.0 And BlinkTimer > -12.0 Then
							PointEntity(e\room\Objects[3], Camera)
							RotateEntity(e\room\Objects[3], 0.0, EntityYaw(e\room\Objects[3], True), 0.0, True)
						EndIf
						If e\EventState2 = 0.0 Then 
							e\EventState = CurveValue(0.0, e\EventState, 15.0)
							If Rand(800) = 1 Then e\EventState2 = 1.0
						Else
							e\EventState = e\EventState + (FPSfactor * 0.5)
							If e\EventState > 360.0 Then e\EventState = 0.0	
							
							If Rand(1200) = 1 Then e\EventState2 = 0.0
						EndIf
						PositionEntity(e\room\Objects[3], EntityX(e\room\Objects[3], True), (-608.0 * RoomScale) + 0.05 + Sin(e\EventState + 270.0) * 0.05, EntityZ(e\room\Objects[3], True), True)
					EndIf
				EndIf
				;[End Block]
			Case "room3storage"
				;[Block]
				If PlayerRoom = e\room Then
					e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
					e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
					
					If EntityY(Collider) < -4600.0 * RoomScale Then
						GiveAchievement(Achv939)
						
						ShouldPlay = 7
						
						If e\room\NPC[0] = Null Or e\room\NPC[1] = Null Or e\room\NPC[2] = Null Then
							If QuickLoadPercent = -1 Then
								QuickLoadPercent = 0
								QuickLoad_CurrEvent = e
							EndIf
						Else
							If e\EventState = 0.0 Then
								;Instance # 1
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True) + 0.2, EntityZ(e\room\Objects[4], True))
								ResetEntity(e\room\NPC[0]\Collider)
								e\room\NPC[0]\State = 2.0
								e\room\NPC[0]\State2 = 5.0
								e\room\NPC[0]\PrevState = 7
								;Instance # 2
								PositionEntity(e\room\NPC[1]\Collider, EntityX(e\room\Objects[9], True), EntityY(e\room\Objects[9], True) + 0.2, EntityZ(e\room\Objects[9], True))
								ResetEntity(e\room\NPC[1]\Collider)
								e\room\NPC[1]\State = 2.0
								e\room\NPC[1]\State2 = 10.0
								e\room\NPC[1]\PrevState = 12
								;Instance # 3
								PositionEntity(e\room\NPC[2]\Collider, EntityX(e\room\Objects[13], True), EntityY(e\room\Objects[13], True) + 0.2, EntityZ(e\room\Objects[13], True))
								ResetEntity(e\room\NPC[2]\Collider)
								e\room\NPC[2]\State = 2.0
								e\room\NPC[2]\State2 = 14.0
								e\room\NPC[2]\PrevState = 16
								; ~ Other
								e\EventState = 1.0
							EndIf
							
							If e\room\RoomDoors[4]\Open = False
								If UpdateLever(e\room\Levers[0])
									e\room\RoomDoors[4]\Open = True
									If e\Sound2 <> 0 Then 
										FreeSound_Strict(e\Sound2) : e\Sound2 = 0
									EndIf
									e\Sound2 = LoadSound_Strict("SFX\Door\Door2Open1_dist.ogg")
									e\SoundCHN2 = PlaySound2(e\Sound2, Camera, e\room\RoomDoors[4]\OBJ, 400.0)
								EndIf
								If UpdateLever(e\room\Levers[1])
									e\room\RoomDoors[4]\Open = True
									If e\Sound2 <> 0 Then 
										FreeSound_Strict(e\Sound2) : e\Sound2 = 0
									EndIf
									e\Sound2 = LoadSound_Strict("SFX\Door\Door2Open1_dist.ogg")
									e\SoundCHN2 = PlaySound2(e\Sound2, Camera, e\room\RoomDoors[4]\OBJ, 400.0)
								EndIf
							EndIf
							
							UpdateLever(e\room\Levers[0], e\room\RoomDoors[4]\Open)
							UpdateLever(e\room\Levers[1], e\room\RoomDoors[4]\Open)
							
							e\room\NPC[0]\IgnorePlayer = False
							e\room\NPC[2]\IgnorePlayer = False
							
							CurrTrigger = CheckTriggers()
							
							Select CurrTrigger
								Case "939-1_fix"
									;[Block]
									e\room\NPC[0]\IgnorePlayer = True
									;[End Block]
								Case "939-3_fix"
									;[Block]
									e\room\NPC[2]\IgnorePlayer = True
									;[End Block]
							End Select
							
							If ChannelPlaying(e\SoundCHN2) = True Then
								UpdateSoundOrigin(e\SoundCHN2, Camera, e\room\RoomDoors[4]\OBJ, 400.0)
							EndIf
							
							PlayerFallingPickDistance = 0.0
							
							If EntityY(Collider) < -6400.0 * RoomScale And KillTimer >= 0.0 And FallTimer >= 0.0 Then
								PlaySound_Strict LoadTempSound("SFX\Room\PocketDimension\Impact.ogg")
								KillTimer = -1.0
							EndIf
						EndIf
					Else
						e\EventState = 0.0
						If e\room\NPC[0] <> Null Then e\room\NPC[0]\State = 66
						If e\room\NPC[1] <> Null Then e\room\NPC[1]\State = 66
						If e\room\NPC[2] <> Null Then e\room\NPC[2]\State = 66
					EndIf
				Else
					If e\room\NPC[0] <> Null Then e\room\NPC[0]\State = 66
					If e\room\NPC[1] <> Null Then e\room\NPC[1]\State = 66
					If e\room\NPC[2] <> Null Then e\room\NPC[2]\State = 66
				EndIf 
				;[End Block]
			Case "room3tunnel"
				;[Block]
				If e\EventState = 0.0 Then
					e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True) + 0.5, EntityZ(e\room\Objects[0], True))
					PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider) + Rnd(-20.0, 20.0), 0.0, True)
					SetNPCFrame(e\room\NPC[0], 288.0)
					e\room\NPC[0]\State = 8.0
					
					e\EventState = 1.0
					RemoveEvent(e)
				EndIf
				;[End Block]
			Case "room4"
				;[Block]
				If e\EventState < MilliSecs2() Then
					If PlayerRoom <> e\room Then
						If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\OBJ), EntityZ(e\room\OBJ)) < 16.0 Then
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtype049 Then
									If n\State = 2.0 And EntityDistance(Collider, n\Collider) > 16.0 Then
										TFormVector(368.0, 528.0, 176.0, e\room\OBJ, 0)
										PositionEntity(n\Collider, EntityX(e\room\OBJ) + TFormedX(), TFormedY(), EntityZ(e\room\OBJ) + TFormedZ())
										ResetEntity(n\Collider)
										n\PathStatus = 0
										n\State = 4.0
										n\State2 = 0.0
										n\State3 = 0.0
										RemoveEvent(e)
									EndIf
									Exit
								EndIf
							Next
						EndIf
					EndIf
					If e <> Null Then e\EventState = MilliSecs2() + 5000
				EndIf
				;[End Block]
			Case "room012"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityY(Collider) < 0.0 Then
						If e\EventState = 0.0 Then
							If EntityDistance(Collider, e\room\RoomDoors[0]\OBJ) < 2.5 And RemoteDoorOn Then
								GiveAchievement(Achv012)
								
								PlaySound_Strict(HorrorSFX(7))
								PlaySound2(LeverSFX, Camera, e\room\RoomDoors[0]\OBJ)
								
								e\room\RoomDoors[0]\Locked = False
								UseDoor(e\room\RoomDoors[0], False)
								e\room\RoomDoors[0]\Locked = True
								
								e\EventState = 1.0
							EndIf
						Else
							If e\Sound = 0 Then LoadEventSound(e, "SFX\Music\012Golgotha.ogg")
							e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\Objects[3], 5.0)
							
							If e\Sound2 = 0 Then LoadEventSound(e, "SFX\Music\012.ogg", 1)
							
							If e\EventState < 90.0 Then e\EventState = CurveValue(90.0, e\EventState, 500)
							PositionEntity(e\room\Objects[2], EntityX(e\room\Objects[2], True), (-130 - 448 * Sin(e\EventState)) * RoomScale, EntityZ(e\room\Objects[2], True), True)
							
							If e\EventState2 > 0.0 And e\EventState2 < 200.0 Then
								e\EventState2 = e\EventState2 + FPSfactor
								RotateEntity(e\room\Objects[1], CurveValue(85.0, EntityPitch(e\room\Objects[1]), 5), EntityYaw(e\room\Objects[1]), 0.0)
							Else
								e\EventState2 = e\EventState2 + FPSfactor
								If e\EventState2 < 250.0 Then
									ShowEntity(e\room\Objects[3])
								Else
									HideEntity(e\room\Objects[3]) 
									If e\EventState2 > 300.0 Then e\EventState2 = 200.0
								EndIf
							EndIf
							
							If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then
								If EntityVisible(e\room\Objects[2], Camera) Then 							
									e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\Objects[3], 10.0, e\EventState3 / (86.0 * 70.0))
									
									Pvt = CreatePivot()
									PositionEntity(Pvt, EntityX(Camera), EntityY(e\room\Objects[2], True) - 0.05, EntityZ(Camera))
									PointEntity(Pvt, e\room\Objects[2])
									RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(Collider), 80.0 - (e\EventState3 / 200.0)), 0.0)
									
									TurnEntity(Pvt, 90.0, 0.0, 0.0)
									User_Camera_Pitch = CurveAngle(EntityPitch(Pvt) + 25.0, User_Camera_Pitch + 90.0, 80.0 - (e\EventState3 / 200.0))
									User_Camera_Pitch = User_Camera_Pitch - 90
									
									Dist = Distance(EntityX(Collider),EntityZ(Collider),EntityX(e\room\Objects[2],True),EntityZ(e\room\Objects[2],True))
									
									HeartBeatRate = 150.0
									HeartBeatVolume = Max(3.0 - Dist, 0.0) / 3.0
									BlurVolume = Max((2.0 - Dist) * (e\EventState3 / 800.0)*(Sin(Float(MilliSecs2()) / 20.0 + 1.0)), BlurVolume)
									CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs2()) / 20.0) + 1.0) * 8.0 * Max((3.0 - Dist), 0.0))
									
									If BreathCHN <> 0 Then
										If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
									EndIf
									
									If Dist < 0.6 Then
										e\EventState3 = Min(e\EventState3 + FPSfactor, 70 * 86.0)
										If e\EventState3 > 70 * 1.0 And e\EventState3 - FPSfactor =< 70 * 1.0 Then
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech1.ogg"))
										ElseIf e\EventState3 > 70 * 13.0 And e\EventState3 - FPSfactor =< 70 * 13.0
											Msg="You start pushing your nails into your wrist, drawing blood."
											MsgTimer = 70 * 7.0
											Injuries = Injuries + 0.5
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech2.ogg"))
										ElseIf e\EventState3 > 70 * 31.0 And e\EventState3 - FPSfactor =< 70 * 31.0
											Tex = LoadTexture_Strict("GFX\map\scp-012_1.jpg")
											EntityTexture(e\room\Objects[4], Tex, 0, 1)
											FreeTexture(Tex)
											
											Msg="You tear open your left wrist and start writing on the composition with your blood."
											MsgTimer = 70 * 7.0
											Injuries = Max(Injuries, 1.5)
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech" + Rand(3, 4) + ".ogg"))
										ElseIf e\EventState3 > 70 * 49.0 And e\EventState3 - FPSfactor =< 70 * 49.0
											Msg="You push your fingers deeper into the wound."
											MsgTimer = 70 * 8.0
											Injuries = Injuries + 0.3
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech5.ogg"))
										ElseIf e\EventState3 > 70 * 63.0 And e\EventState3 - FPSfactor =< 70 * 63.0
											Tex = LoadTexture_Strict("GFX\map\scp-012_2.jpg")
											EntityTexture(e\room\Objects[4], Tex, 0, 1)	
											FreeTexture(Tex)
											
											Injuries = Injuries + 0.5
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech6.ogg"))
										ElseIf e\EventState3 > 70 * 74 And e\EventState3 - FPSfactor =< 70 * 74.0
											Tex = LoadTexture_Strict("GFX\map\scp-012_3.jpg")
											EntityTexture(e\room\Objects[4], Tex, 0, 1)
											FreeTexture(Tex)
											
											Msg = "You rip the wound wide open. Grabbing scoops of blood pouring out."
											MsgTimer = 70 * 7.0
											Injuries = Injuries + 0.8
											PlaySound_Strict(LoadTempSound("SFX\SCP\012\Speech7.ogg"))
											Crouch = True
											
											de.Decals = CreateDecal(17,  EntityX(Collider), (-768.0) * RoomScale + 0.01, EntityZ(Collider), 90.0, Rnd(360.0), 0.0)
											de\Size = 0.1 : de\MaxSize = 0.45 : de\SizeChange = 0.0002 : UpdateDecals()
										ElseIf e\EventState3 > 70 * 85.0 And e\EventState3-FPSfactor =< 70 * 85.0	
											DeathMSG = "Subject D-9341 found in a pool of blood next to SCP-012. Subject seems to have ripped open his wrists and written three extra "
											DeathMSG = DeathMSG + "lines to the composition before dying of blood loss."
											Kill()
										EndIf
										
										RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Collider) + Sin(e\EventState3 * (e\EventState3 / 2000.0)) * (e\EventState3 / 300.0), EntityYaw(Collider), 80.0), 0.0)
									Else
										Angle = WrapAngle(EntityYaw(Pvt)-EntityYaw(Collider))
										If Angle < 40.0 Then
											ForceMove = (40.0 - Angle) * 0.02
										ElseIf Angle > 310.0
											ForceMove = (40.0 - Abs(360.0 - Angle)) * 0.02
										EndIf
									EndIf								
									
									FreeEntity(Pvt)							
								Else
									If (Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\RoomDoors[0]\frameobj), EntityZ(e\room\RoomDoors[0]\frameobj))<4.5) And  EntityY(Collider)<-2.5 Then
										Pvt = CreatePivot()
										PositionEntity Pvt, EntityX(Camera), EntityY(Collider), EntityZ(Camera)
										PointEntity(Pvt, e\room\RoomDoors[0]\FrameOBJ)
										User_Camera_Pitch = CurveAngle(90.0, User_Camera_Pitch + 90.0, 100.0)
										User_Camera_Pitch = User_Camera_Pitch - 90.0
										RotateEntity(Collider, EntityPitch(Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(Collider), 150.0), 0.0)
										
										Angle = WrapAngle(EntityYaw(Pvt) - EntityYaw(Collider))
										If Angle < 40.0 Then
											ForceMove = (40.0 - Angle) * 0.008
										ElseIf Angle > 310.0
											ForceMove = (40.0 - Abs(360.0 - Angle)) * 0.008
										EndIf
										FreeEntity(Pvt)
									EndIf
								EndIf
							EndIf
						EndIf
					Else
						If e\Sound <> 0 Then
							If ChannelPlaying(e\SoundCHN) = True Then StopChannel(e\SoundCHN)
							FreeSound_Strict(e\Sound) : e\Sound = 0
						EndIf
						If e\Sound2 <> 0 Then
							If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
							FreeSound_Strict(e\Sound2) : e\Sound2 = 0
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room035"
				;[Block]
				If PlayerRoom = e\room Then
					; ~ EventState2 = has 035 told the code to the storage room (true / false)
					
					; ~ EventState3 = has the player opened the gas valves (0 = no, 0 < x < 70 * 35 yes, x > 70 * 35 the host has died)
					
					ShouldPlay = 27
					
					If e\EventState = 0.0 Then
						If EntityDistance(Collider, e\room\Objects[3]) < 2 Then
							n.NPCs = CreateNPC(NPCtypeD, EntityX(e\room\Objects[4], True), 0.5,EntityZ(e\room\Objects[4], True))
							n\Texture = "GFX\NPCs\scp_035_victim.png"
							n\Model = "GFX\NPCs\035.b3d"
							HideEntity(n\OBJ)
							
							SetAnimTime(n\OBJ, 501.0)
							n\Frame = 501.0
							
							n\State = 6.0
							
							e\EventState = 1.0
						EndIf
					ElseIf e\EventState > 0.0
						If e\room\NPC[0] = Null Then
							For n.NPCs = Each NPCs
								If n\Texture = "GFX\NPCs\scp_035_victim.png" Then
									e\room\NPC[0] = n
									
									Temp = e\room\NPC[0]\Frame
									
									FreeEntity(e\room\NPC[0]\OBJ)
									e\room\NPC[0]\OBJ = CopyEntity(o\NPCModelID[27])
									x = 0.5 / MeshWidth(e\room\NPC[0]\OBJ)
									e\room\NPC[0]\ModelScaleX = x
									e\room\NPC[0]\ModelScaleY = x
									e\room\NPC[0]\ModelScaleZ = x
									ScaleEntity(e\room\NPC[0]\OBJ, x, x, x)
									SetAnimTime(e\room\NPC[0]\OBJ, Temp)
									ShowEntity(e\room\NPC[0]\OBJ)
									
									RotateEntity(n\Collider, 0.0, e\room\Angle + 270.0, 0.0, True)
									Exit
								EndIf
							Next
						EndIf
						
						If e\room\NPC[0]\SoundCHN <> 0 Then
							If ChannelPlaying(e\room\NPC[0]\SoundCHN) = True Then
								e\room\NPC[0]\SoundChn = LoopSound2(e\room\NPC[0]\Sound, e\room\NPC[0]\SoundCHN, Camera, e\room\OBJ, 6.0)
							EndIf
						EndIf
						
						If e\EventState = 1.0 Then
							If EntityDistance(Collider, e\room\Objects[3]) < 1.2 
								If EntityInView(e\room\NPC[0]\OBJ, Camera) Then
									GiveAchievement(Achv035)
									PlaySound_Strict(LoadTempSound("SFX\SCP\035\GetUp.ogg"))
									e\EventState = 1.5
								EndIf
							EndIf
						Else
							If e\room\RoomDoors[3]\Open Then e\EventState2 = Max(e\EventState2, 1.0)
							; ~ The door is closed
							If UpdateLever(e\room\Levers[0], (e\EventState2 = 20.0)) = 0 Then
								; ~ The gas valves are open
								Temp = UpdateLever(e\room\Levers[1], False)
								If Temp Or (e\EventState3 > 70 * 25.0 And e\EventState3 < 70 * 50.0) Then 
									If Temp Then 
										PositionEntity(e\room\Objects[5], EntityX(e\room\Objects[5], True), 424.0 * RoomScale, EntityZ(e\room\Objects[5], True), True)
										PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), 424.0 * RoomScale, EntityZ(e\room\Objects[6], True), True)
									Else
										PositionEntity(e\room\Objects[5], EntityX(e\room\Objects[5], True), 10.0, EntityZ(e\room\Objects[5], True), True)
										PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), 10.0, EntityZ(e\room\Objects[6], True), True)
									EndIf
									
									If e\EventState3 > (-70) * 30.0 Then 
										e\EventState3 = Abs(e\EventState3) + FPSfactor
										If e\EventState3 > 1.0 And e\EventState3 - FPSfactor =< 1.0 Then
											e\room\NPC[0]\State = 0.0
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Gased1.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
										ElseIf e\EventState3 > 70 * 15.0 And e\EventState3 < 70 * 25.0
											If e\EventState3 - FPSfactor =< 70 * 15.0 Then
												If e\room\NPC[0]\Sound <> 0 Then 
													FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
												EndIf
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Gased2.ogg")
												e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												SetNPCFrame(e\room\NPC[0], 553.0)
											EndIf
											e\room\NPC[0]\State = 6.0
											
											AnimateNPC(e\room\NPC[0], 553.0, 529.0, -0.12, False)
										ElseIf e\EventState3 > 70 * 25.0 And e\EventState3 < 70 * 35.0
											e\room\NPC[0]\State = 6.0
											AnimateNPC(e\room\NPC[0], 529.0, 524.0, -0.08, False)
										ElseIf e\EventState3 > 70 * 35.0
											If e\room\NPC[0]\State = 6.0 Then
												Sanity = -150.0 * Sin(AnimTime(e\room\NPC[0]\OBJ) - 524.0) * 9.0
												AnimateNPC(e\room\NPC[0], 524.0, 553.0, 0.08, False)
												If e\room\NPC[0]\Frame = 553.0 Then e\room\NPC[0]\State = 0.0
											EndIf
											
											If e\EventState3 - FPSfactor =< 70 * 35.0 Then 
												If e\room\NPC[0]\Sound <> 0 Then 
													FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
												EndIf
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedKilled1.ogg")
												e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												PlaySound_Strict(LoadTempSound("SFX\SCP\035\KilledGetUp.ogg"))
												e\EventState = 70 * 60.0
											EndIf
										EndIf
									EndIf
								Else ; ~ Gas valves closed
									If e\room\NPC[0]\State = 6.0 Then
										If e\room\NPC[0]\Frame >= 501.0 And e\room\NPC[0]\Frame =< 523.0 Then
											e\room\NPC[0]\Frame = Animate2(e\room\NPC[0]\OBJ, AnimTime(e\room\NPC[0]\OBJ), 501.0, 523.0, 0.08, False)
											If e\room\NPC[0]\Frame = 523.0 Then e\room\NPC[0]\State = 0.0
										EndIf	
										
										If e\room\NPC[0]\Frame >= 524.0 And e\room\NPC[0]\Frame =< 553.0 Then
											e\room\NPC[0]\Frame = Animate2(e\room\NPC[0]\OBJ, AnimTime(e\room\NPC[0]\OBJ), 524.0, 553.0, 0.08, False)
											If e\room\NPC[0]\Frame = 553.0 Then e\room\NPC[0]\State = 0.0
										EndIf	
									EndIf
									
									PositionEntity(e\room\Objects[5], EntityX(e\room\Objects[5], True), 10.0, EntityZ(e\room\Objects[5], True), True)
									PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), 10.0, EntityZ(e\room\Objects[6], True), True)
									
									If e\room\NPC[0]\State = 0.0 Then
										PointEntity(e\room\NPC[0]\OBJ, Collider)
										RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
										
										If Rand(500) = 1 Then
											If EntityDistance(e\room\NPC[0]\Collider, e\room\Objects[4]) > 2.0 Then
												e\room\NPC[0]\State2 = 1.0
											Else
												e\room\NPC[0]\State2 = 0.0
											EndIf
											e\room\NPC[0]\State = 1.0
										EndIf
									ElseIf e\room\NPC[0]\State = 1.0
										If e\room\NPC[0]\State2 = 1.0 Then
											PointEntity(e\room\NPC[0]\OBJ, e\room\Objects[4])
											If EntityDistance(e\room\NPC[0]\Collider, e\room\Objects[4]) < 0.2 Then e\room\NPC[0]\State = 0.0
										Else
											RotateEntity(e\room\NPC[0]\OBJ, 0.0, e\room\Angle - 180.0, 0.0, True)
											If EntityDistance(e\room\NPC[0]\Collider, e\room\Objects[4]) > 2.0 Then e\room\NPC[0]\State = 0.0
										EndIf
										RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
									EndIf
									
									If e\EventState3 > 0.0 Then
										e\EventState3 = -e\EventState3
										If e\EventState3 < (-70) * 35.0 Then ; ~ The host is dead
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedKilled2.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											e\EventState = 70 * 60.0
										Else 
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											If e\EventState3 < (-70) * 20.0 Then
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedStop2.ogg")
											Else
												e\EventState3 = (-70) * 21.0
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedStop1.ogg")
											EndIf
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											e\EventState = 70 * 61.0
										EndIf
									Else
										
										e\EventState = e\EventState + FPSfactor
										If e\EventState > 70 * 4.0 And e\EventState - FPSfactor =< 70 * 4.0 Then
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Help1.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											e\EventState = 70 * 10.0
										ElseIf e\EventState > 70 * 20.0 And e\EventState - FPSfactor =< 70 * 20.0
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Help2.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
										ElseIf e\EventState > 70 * 40.0 And e\EventState - FPSfactor =< 70 * 40.0
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle1.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
										ElseIf e\EventState > 70 * 50.0 And e\EventState - FPSfactor =< 70 * 50.0
											If e\room\NPC[0]\Sound <> 0 Then 
												FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
											EndIf
											e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle2.ogg")
											e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
										ElseIf e\EventState > 70 * 80.0 And e\EventState - FPSfactor =< 70 * 80.0
											If e\EventState2 Then ; ~ Skip the closet part if player has already opened it
												e\EventState = 70 * 130.0
											Else
												If e\EventState3 < (-70) * 30.0 Then ; ~ The host is dead
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedCloset.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												ElseIf e\EventState3 = 0 ; ~ The gas valves haven't been opened
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Closet1.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												Else ; ~ Gas valves have been opened but 035 isn't dead
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedCloset.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												EndIf												
											EndIf
										ElseIf e\EventState > 70 * 80.0
											If e\EventState2 Then e\EventState = Max(e\EventState, 70 * 100.0)
											If e\EventState > 70 * 110.0 And e\EventState - FPSfactor =< 70 * 110.0 Then
												If e\EventState2 Then
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Closet2.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
													e\EventState = 70 * 130.0
												Else
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle3.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												EndIf
											ElseIf e\EventState > 70 * 125.0 And e\EventState - FPSfactor =< 70 * 125.0
												If e\EventState2 Then
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Closet2.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												Else
													If e\room\NPC[0]\Sound <> 0 Then 
														FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
													EndIf
													e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle4.ogg")
													e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
												EndIf
											ElseIf e\EventState > 70 * 150.0 And e\EventState - FPSfactor =< 70 * 150.0
												If e\room\NPC[0]\Sound <> 0 Then 
													FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
												EndIf
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle5.ogg")
												e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											ElseIf e\EventState > 70 * 200.0 And e\EventState - FPSfactor =< 70 * 200.0
												If e\room\NPC[0]\Sound <> 0 Then 
													FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
												EndIf
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle6.ogg")
												e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											ElseIf e\EventState > 70 * 250.0 And e\EventState - FPSfactor =< 70 * 2500
												If e\room\NPC[0]\Sound <> 0 Then 
													FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
												EndIf
												e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Idle7.ogg")
												e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
											EndIf
										EndIf
									EndIf
								EndIf								
							Else ; ~ The player has opened the door
								If e\EventState2 < 10.0 Then
									e\room\RoomDoors[2]\Open = False
									e\room\RoomDoors[2]\Locked = True
									
									If e\room\RoomDoors[1]\Open = False Then 
										e\room\RoomDoors[0]\Locked = False
										e\room\RoomDoors[1]\Locked = False
										UseDoor(e\room\RoomDoors[1])
										e\room\RoomDoors[0]\Locked = True
										e\room\RoomDoors[1]\Locked = True
									EndIf
									
									If e\EventState3 = 0.0 Then
										If e\room\NPC[0]\Sound <> 0 Then 
											FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
										EndIf
										e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\Escape.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
									ElseIf Abs(e\EventState3) > 70 * 35.0
										If e\room\NPC[0]\Sound <> 0 Then 
											FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
										EndIf
										e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\KilledEscape.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
									Else
										If e\room\NPC[0]\Sound <> 0 Then 
											FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0
										EndIf
										e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\035\GasedEscape.ogg")
										e\room\NPC[0]\SoundCHN = PlaySound_Strict(e\room\NPC[0]\Sound)
									EndIf
									e\EventState2 = 20.0
								EndIf
								
								If e\EventState2 = 20.0 Then
									Dist = EntityDistance(e\room\RoomDoors[0]\FrameOBJ, e\room\NPC[0]\Collider)
									e\room\NPC[0]\State = 1.0
									If Dist > 2.5 Then
										PointEntity(e\room\NPC[0]\OBJ, e\room\RoomDoors[1]\FrameOBJ)
										RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0)
									ElseIf Dist > 0.7
										If ChannelPlaying(e\room\NPC[0]\SoundCHN) = True Then
											e\room\NPC[0]\State = 0.0
											PointEntity(e\room\NPC[0]\OBJ, Collider)
											RotateEntity(e\room\NPC[0]\Collider, 0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0.0)
										Else
											PointEntity(e\room\NPC[0]\OBJ, e\room\RoomDoors[0]\FrameOBJ)
											RotateEntity(e\room\NPC[0]\Collider, 0.0, CurveAngle(EntityYaw(e\room\NPC[0]\OBJ), EntityYaw(e\room\NPC[0]\Collider), 15.0), 0)
										EndIf
									Else
										RemoveNPC(e\room\NPC[0]) : e\room\NPC[0] = Null
										e\EventState = -1.0
										e\EventState2 = 0.0
										e\EventState3 = 0.0
										e\room\RoomDoors[0]\Locked = False										
										e\room\RoomDoors[1]\Locked = False
										e\room\RoomDoors[2]\Locked = False
										UseDoor(e\room\RoomDoors[1], False)
										For do.Doors = Each Doors
											If do\Dir = 2 Then
												If Abs(EntityX(e\room\OBJ) - EntityX(do\FrameOBJ,True)) < 4.5 Then 
													If Abs(EntityZ(e\room\OBJ) - EntityZ(do\FrameOBJ, True)) < 4.5 Then 
														UseDoor(do, False)
														Exit
													EndIf
												EndIf
											EndIf
										Next
									EndIf
								EndIf
							EndIf
						EndIf
					Else ; ~ SCP-035 has left
						If UpdateLever(e\room\Levers[1], False) Then 
							PositionEntity(e\room\Objects[5], EntityX(e\room\Objects[5], True), 424.0 * RoomScale, EntityZ(e\room\Objects[5], True), True)
							PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), 424.0 * RoomScale, EntityZ(e\room\Objects[6], True), True)
						Else
							PositionEntity(e\room\Objects[5], EntityX(e\room\Objects[5], True), 10.0, EntityZ(e\room\Objects[5], True), True)
							PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), 10.0, EntityZ(e\room\Objects[6], True), True)
						EndIf
						
						Temp = False
						
						; ~ Player is inside the containment chamber
						If EntityX(Collider) > Min(EntityX(e\room\Objects[7], True), EntityX(e\room\Objects[8], True)) Then
							If EntityX(Collider) < Max(EntityX(e\room\Objects[7], True), EntityX(e\room\Objects[8], True)) Then
								If EntityZ(Collider) > Min(EntityZ(e\room\Objects[7], True), EntityZ(e\room\Objects[8], True)) Then
									If EntityZ(Collider) < Max(EntityZ(e\room\Objects[7], True), EntityZ(e\room\Objects[8], True)) Then
										ShouldPlay = 0
										
										If e\room\NPC[0] = Null Then
											If e\room\NPC[0] = Null Then e\room\NPC[0] = CreateNPC(NPCtype035_Tentacle, 0.0, 0.0, 0.0)
										EndIf
										
										PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[4], True), 0.0, EntityZ(e\room\Objects[4], True))
										
										If e\room\NPC[0]\State > 0.0 Then 
											If e\room\NPC[1] = Null Then
												If e\room\NPC[1] = Null Then e\room\NPC[1] = CreateNPC(NPCtype035_Tentacle, 0.0, 0.0, 0.0)
											EndIf
										EndIf
										
										Stamina = CurveValue(Min(60.0, Stamina), Stamina, 20.0)
										
										Temp = True
										
										If e\Sound = 0 Then LoadEventSound(e, "SFX\Room\035Chamber\Whispers1.ogg")
										If e\Sound2 = 0 Then LoadEventSound(e, "SFX\Room\035Chamber\Whispers2.ogg", 1)
										
										e\EventState2 = Min(e\EventState2 + (FPSfactor / 6000.0), 1.0)
										e\EventState3 = CurveValue(e\EventState2, e\EventState3, 50.0)
										
										If Wearing714 = 0 And WearingHazmat < 3 And WearingGasMask < 3 Then
											Sanity = Sanity - FPSfactor * 1.1
											BlurTimer = Sin(MilliSecs2() / 10.0) * Abs(Sanity)
										EndIf
										
										If WearingHazmat = 0 Then
											Injuries = Injuries + (FPSfactor / 5000.0)
										Else
											Injuries = Injuries + (FPSfactor / 10000.0)
										EndIf
										
										If KillTimer < 0.0 And Bloodloss >= 100.0 Then
											DeathMSG = "Class D Subject D-9341 found dead inside SCP-035's containment chamber. "
											DeathMSG = DeathMSG + "The subject exhibits heavy hemorrhaging of blood vessels around the eyes and inside the mouth and nose. "
											DeathMSG = DeathMSG + "Sent for autopsy."
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
						
						If e\room\NPC[1] <> Null Then 
							PositionEntity e\room\NPC[1]\Collider, EntityX(e\room\OBJ, True), 0.0, EntityZ(e\room\OBJ, True)
							Angle = WrapAngle(EntityYaw(e\room\NPC[1]\Collider) - e\room\Angle)
							
							If Angle > 90.0 Then 
								If Angle < 225.0 Then 
									RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\Angle - 89.0 - 180.0, 0.0)
								Else
									RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\Angle - 1.0, 0.0)
								EndIf
							EndIf
						EndIf
						
						If Temp = False Then 
							e\EventState2 = Max(e\EventState2 - (FPSfactor / 2000.0), 0.0)
							e\EventState3 = Max(e\EventState3 - (FPSfactor / 100.0), 0.0)
						EndIf
						
						If e\EventState3 > 0.0 And Wearing714 = 0 And WearingHazmat < 3 And WearingGasMask < 3 Then 
							e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\OBJ, 10.0, e\EventState3)
							e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\OBJ, 10.0, (e\EventState3 - 0.5) * 2.0)
						EndIf
					EndIf
				Else	
					If e\EventState = 0.0 Then	
						If e\Sound = 0 Then
							If EntityDistance(Collider, e\room\OBJ) < 20.0 Then
								LoadEventSound(e, "SFX\Room\035Chamber\InProximity.ogg")
								PlaySound_Strict(e\Sound)
							EndIf
						EndIf
					ElseIf e\EventState < 0.0
						For i = 0 To 1
							If e\room\NPC[i] <> Null Then 
								RemoveNPC(e\room\NPC[i]) : e\room\NPC[i] = Null
							EndIf						
						Next						
					EndIf
				EndIf
				;[End Block]
			Case "room049"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityY(Collider) > -2848.0 * RoomScale Then
						e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
						e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
					Else
						ShouldPlay = 25
						
						If e\EventState = 0.0 Then
							If e\EventStr = "" And QuickLoadPercent = -1
								QuickLoadPercent = 0
								QuickLoad_CurrEvent = e
								e\EventStr = "Load0"
							EndIf
							PlaySound_Strict(LoadTempSound("SFX\Room\Blackout.ogg"))
							If EntityDistance(e\room\Objects[11], Collider) < EntityDistance(e\room\Objects[12], Collider) Then
								it = CreateItem("Research Sector-02 Scheme", "paper", EntityX(e\room\Objects[11], True), EntityY(e\room\Objects[11], True), EntityZ(e\room\Objects[11], True))
								EntityType(it\Collider, HIT_ITEM)
							Else
								it = CreateItem("Research Sector-02 Scheme", "paper", EntityX(e\room\Objects[12], True), EntityY(e\room\Objects[12], True), EntityZ(e\room\Objects[12], True))
								EntityType(it\Collider, HIT_ITEM)
							EndIf
						ElseIf e\EventState > 0.0
							Local PrevGenLever%
							
							If EntityPitch(e\room\Objects[9], True) > 0.0 Then
								PrevGenLever = True
							Else
								PrevGenLever = False
							EndIf
							Temp = (Not UpdateLever(e\room\Objects[7])) ; ~ Power feed
							x = UpdateLever(e\room\Objects[9]) ; ~ Generator
							
							e\room\RoomDoors[1]\Locked = True
							e\room\RoomDoors[3]\Locked = True
							e\room\RoomDoors[1]\IsElevatorDoor = 0
							e\room\RoomDoors[3]\IsElevatorDoor = 0
							
							If (PrevGenLever <> x) Then
								If x = False Then
									PlaySound_Strict(LightSFX)
								Else
									PlaySound_Strict(TeslaPowerUpSFX)
								EndIf
							EndIf
							
							If e\EventState >= 70.0 Then
								If x Then
									ShouldPlay = 8
									e\EventState = Max(e\EventState, 70 * 180.0)
									SecondaryLightOn = CurveValue(1.0, SecondaryLightOn, 10.0)
									If e\Sound2 = 0 Then LoadEventSound(e, "SFX\Ambient\Room Ambience\FuelPump.ogg", 1)
									e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\Objects[10], 6.0)
									For i = 4 To 6
										e\room\RoomDoors[i]\Locked = False
									Next
								Else
									SecondaryLightOn = CurveValue(0.0, SecondaryLightOn, 10.0)
									If ChannelPlaying(e\SoundCHN2) Then StopChannel(e\SoundCHN2)
									For i = 4 To 6
										e\room\RoomDoors[i]\Locked = True
									Next
								EndIf
							Else
								e\EventState = Min(e\EventState + FPSfactor, 70.0)
							EndIf
							
							If Temp And x Then
								e\room\RoomDoors[1]\Locked = False
								e\room\RoomDoors[3]\Locked = False
								e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
								e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
								
								If e\room\NPC[0]\Idle > 0
									i = 0
									If EntityDistance(Collider, e\room\RoomDoors[1]\FrameOBJ) < 3.0
										i = 1
									ElseIf EntityDistance(Collider,e\room\RoomDoors[3]\FrameOBJ) < 3.0
										i = 3
									EndIf
									If i > 0
										PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True))
										ResetEntity(e\room\NPC[0]\Collider)
										PlaySound2(ElevatorBeepSFX, Camera, e\room\Objects[i], 4.0)
										e\room\RoomDoors[i]\Locked = False
										UseDoor(e\room\RoomDoors[i], False, True)
										e\room\RoomDoors[i - 1]\Open = False
										e\room\RoomDoors[i]\Open = True
										e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(Collider), EntityY(Collider), EntityZ(Collider))
										If e\room\NPC[0]\Sound2 <> 0 Then FreeSound_Strict(e\room\NPC[0]\Sound2)
										e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\SCP\049\DetectedInChamber.ogg")
										e\room\NPC[0]\SoundCHN2 = LoopSound2(e\room\NPC[0]\Sound2, e\room\NPC[0]\SoundCHN2, Camera, e\room\NPC[0]\OBJ)
										e\room\NPC[0]\Idle = 0
										e\room\NPC[0]\HideFromNVG = False
										e\room\NPC[0]\PrevState = 2
										e\room\NPC[0]\State = 2.0
									EndIf
								EndIf
							EndIf
							
							If e\EventState < 70 * 190.0 Then
								If e\EventState >= 70 * 180.0 Then
									e\room\RoomDoors[1]\Open = False
									e\room\RoomDoors[3]\Open = False
									e\room\RoomDoors[0]\Open = True
									e\room\RoomDoors[2]\Open = True
									
									e\EventState = 70 * 190.0
								EndIf
							ElseIf e\EventState < 70 * 240.0
								For n.NPCs = Each NPCs ; ~ Awake the zombies
									If n\NPCtype = NPCtype049_2 And n\State = 0.0 Then
										n\State = 1.0
										SetNPCFrame(n, 155.0)
									EndIf
								Next
								e\EventState = 70 * 241.0
							EndIf
						EndIf
					EndIf
				Else
					e\EventState2 = UpdateElevators(e\EventState2, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[0], e\room\Objects[1], e)
					e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[2], e\room\RoomDoors[3], e\room\Objects[2], e\room\Objects[3], e)
				EndIf 
				
				If e\EventState < 0.0 Then
					If e\EventState > (-70) * 4.0 Then
						I_008\Timer = 0.0
						If FallTimer >= 0.0 Then 
							FallTimer = Min(-1.0, FallTimer)
							PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
							ResetEntity(Head)
							RotateEntity(Head, 0.0, EntityYaw(Camera) + Rnd(-45.0, 45.0), 0.0)
						ElseIf FallTimer < -230.0
							FallTimer = -231.0
							BlinkTimer = 0.0
							e\EventState = e\EventState - FPSfactor
							
							If e\EventState =< (-70) * 4.0 Then 
								UpdateDoorsTimer = 0.0
								UpdateDoors()
								UpdateRooms()
								ShowEntity(Collider)
								DropSpeed = 0.0
								BlinkTimer = -10.0
								FallTimer = 0.0
								PositionEntity(Collider, EntityX(e\room\OBJ, True), EntityY(e\room\Objects[5], True) + 0.2, EntityZ(e\room\OBJ, True))
								ResetEntity(Collider)										
								
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), True)
								ResetEntity(e\room\NPC[0]\Collider)
								
								For n.NPCs = Each NPCs
									If n\NPCtype = NPCtype049_2 Then
										PositionEntity(n\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True), True)
										ResetEntity(n\Collider)
										n\State = 4.0
									EndIf
								Next
								
								n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True) + 0.2, EntityZ(e\room\Objects[5], True))
								n\State = 6.0
								n\Reload = 70 * 6.0
								PointEntity(n\Collider, Collider)
								e\room\NPC[1] = n
								
								n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True) + 0.2, EntityZ(e\room\Objects[5], True))
								n\State = 6.0
								n\Reload = 70 * 6.0 + Rnd(15.0, 30.0)
								RotateEntity(n\Collider, 0.0, EntityYaw(e\room\NPC[1]\Collider), 0.0)
								MoveEntity(n\Collider, 0.5, 0.0, 0.0)
								PointEntity(n\Collider, Collider)
								
								n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True) + 0.2, EntityZ(e\room\Objects[5], True))
								n\State = 6.0
								n\Reload = 70 * 6.0 + Rnd(15.0, 30.0)
								RotateEntity(n\Collider, 0.0, EntityYaw(e\room\NPC[1]\Collider), 0.0)
								n\State2 = EntityYaw(n\Collider)
								MoveEntity(n\Collider, -0.65, 0.0, 0.0)
								
								MoveEntity(e\room\NPC[1]\Collider, 0.0, 0.0, 0.1)
								PointEntity(Collider, e\room\NPC[1]\Collider)
								
								PlaySound_Strict(LoadTempSound("SFX\Character\MTF\049\Player0492_1.ogg"))
								
								LoadEventSound(e, "SFX\SCP\049_2\Breath.ogg")
								
								IsZombie = True
							EndIf
						EndIf
					Else
						BlurTimer = 800.0
						ForceMove = 0.5
						Injuries = Max(2.0, Injuries)
						Bloodloss = 0.0
						I_008\Timer = 0.0
						
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(e\room\NPC[1]\Collider), EntityY(e\room\NPC[1]\Collider) + 0.2, EntityZ(e\room\NPC[1]\Collider))
						PointEntity(Collider, e\room\NPC[1]\Collider)
						PointEntity(Camera, Pvt, EntityRoll(Camera))
						FreeEntity(Pvt)
						
						If KillTimer < 0.0 Then
							PlaySound_Strict(LoadTempSound("SFX\Character\MTF\049\Player0492_2.ogg"))
							RemoveEvent(e)
						Else
							If e\SoundCHN = 0 Then
								e\SoundCHN = PlaySound_Strict (e\Sound)
							Else
								If ChannelPlaying(e\SoundCHN) = False Then e\SoundCHN = PlaySound_Strict(e\Sound)
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room079"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityY(Collider) < -9500.0 * RoomScale Then
						If e\EventState = 0.0 Then
							e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[2], True), EntityY(e\room\Objects[2], True) + 0.5, EntityZ(e\room\Objects[2], True))
							PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0, True)
							SetNPCFrame(e\room\NPC[0], 288.0)
							e\room\NPC[0]\State = 8.0
							
							e\EventState = 1.0
						EndIf
						ShouldPlay = 4
						
						If RemoteDoorOn Then 
							e\room\RoomDoors[0]\Locked = 2
						ElseIf e\EventState < 10000.0
							e\room\RoomDoors[0]\Locked = False
							If e\EventState = 1.0 Then 
								e\EventState = 2.0
							ElseIf e\EventState = 2.0
								If EntityDistance(e\room\Objects[0], Collider) < 3.0 Then 
									GiveAchievement(Achv079)
									e\EventState = 3.0
									e\EventState2 = 1.0
									e\SoundCHN3 = StreamSound_Strict("SFX\SCP\079\Speech.ogg", SFXVolume, 0.0)
									e\SoundCHN3_IsStream = True
								EndIf							
							ElseIf e\EventState < 2000.0 Then
								If IsStreamPlaying_Strict(e\SoundCHN3) Then
									If Rand(4) = 1 Then
										EntityTexture(e\room\Objects[1], tt\MiscTextureID[Rand(1, 6)])
										ShowEntity(e\room\Objects[1])
									ElseIf Rand(10) = 1 
										HideEntity(e\room\Objects[1])							
									EndIf							
								Else
									If e\SoundCHN3 <> 0 Then
										StopStream_Strict(e\SoundCHN3) : e\SoundCHN3 = 0
									EndIf
									EntityTexture(e\room\Objects[1], tt\MiscTextureID[0])
									ShowEntity(e\room\Objects[1])
									e\EventState = e\EventState + FPSfactor
								EndIf
							Else
								If EntityDistance(e\room\Objects[0], Collider) < 2.5 Then 
									e\EventState = 10001.0
									If e\SoundCHN3 <> 0 Then
										StopStream_Strict(e\SoundCHN3) : e\SoundCHN3 = 0 
									EndIf
									e\SoundCHN3 = StreamSound_Strict("SFX\SCP\079\Refuse.ogg", SFXVolume, 0.0)
								EndIf
							EndIf
						Else
							If e\SoundCHN3 <> 0 Then
								If (Not IsStreamPlaying_Strict(e\SoundCHN3)) Then
									e\SoundCHN3 = 0
									EntityTexture(e\room\Objects[1], tt\MiscTextureID[0])
									ShowEntity(e\room\Objects[1])
								Else
									If Rand(4) = 1 Then
										EntityTexture(e\room\Objects[1], tt\MiscTextureID[Rand(1, 6)])
										ShowEntity(e\room\Objects[1])
									ElseIf Rand(10) = 1 
										HideEntity(e\room\Objects[1])							
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
					e\EventState4 = UpdateElevators(e\EventState4, e\room\RoomDoors[1], e\room\RoomDoors[2], e\room\Objects[3], e\room\Objects[4], e)	
				EndIf
				
				If e\EventState2 = 1.0 Then
					If RemoteDoorOn Then 	
						If e\SoundCHN3 <> 0
							StopStream_Strict(e\SoundCHN3) : e\SoundCHN3 = 0
						EndIf
						e\SoundCHN3 = StreamSound_Strict("SFX\SCP\079\GateB.ogg", SFXVolume, 0.0)
						e\SoundCHN3_IsStream = True
						e\EventState2 = 2.0
						
						For e2.Events = Each Events
							If e2\EventName = "gateb" Or e2\EventName = "gateaentrance" Then
								e2\EventState3 = 1.0
							EndIf
						Next
					EndIf	
				EndIf
				;[End Block]
			Case "room106"
				;[Block]
				; ~ EventState2 = Are the magnets on
				
				If SoundTransmission Then 
					If e\EventState = 1.0 Then
						e\EventState3 = Min(e\EventState3 + FPSfactor, 4000.0)
    				EndIf
					If ChannelPlaying(e\SoundCHN3) = False Then e\SoundCHN3 = PlaySound_Strict(RadioStatic)   
				EndIf
				
				If e\room\NPC[0] = Null Then
					TFormPoint(1088.0, -5900.0, 1728.0, e\room\OBJ, 0)
					e\room\NPC[0] = CreateNPC(NPCtypeD, TFormedX(), TFormedY(), TFormedZ())
					TurnEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 90.0, 0.0, True)
					e\room\NPC[0]\HideFromNVG = True
				EndIf
				
				If PlayerRoom = e\room Then 
				    If e\room\NPC[0] <> Null Then
					    If EntityY(Collider) < -6900.0 * RoomScale
					        ShouldPlay = 26
							
						    e\room\NPC[0]\State = 6.0
						    If e\room\NPC[0]\Idle = 0 Then
							    AnimateNPC(e\room\NPC[0], 17.0, 19.0, 0.01, False)
							    If e\room\NPC[0]\Frame = 19.0 Then e\room\NPC[0]\Idle = 1
						    Else
							    AnimateNPC(e\room\NPC[0], 19.0, 17.0, -0.01, False)	
							    If e\room\NPC[0]\Frame = 17.0 Then e\room\NPC[0]\Idle = 0
						    EndIf
							
						    PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True) + 0.1, EntityZ(e\room\Objects[5], True), True)
						    RotateEntity(e\room\NPC[0]\Collider, EntityPitch(e\room\Objects[5], True), EntityYaw(e\room\Objects[5], True), 0.0, True)
						    ResetEntity(e\room\NPC[0]\Collider)
							
						    Temp = e\EventState2
							
						    Local LeverState# = UpdateLever(e\room\Objects[1], ((EntityY(e\room\Objects[6], True) < -8318.0 * RoomScale) And (EntityY(e\room\Objects[6], True) > -8603.0 * RoomScale)))
							
						    If GrabbedEntity = e\room\Objects[1] And DrawHandIcon = True Then e\EventState2 = LeverState
							
						    If e\EventState2 <> Temp Then 
							    If e\EventState2 = False Then
								    PlaySound_Strict(MagnetDownSFX)
							    Else
								    PlaySound_Strict(MagnetUpSFX)	
							    EndIf
						    EndIf
							
						    If ((e\EventState3 > 3200.0) Or (e\EventState3 < 2500.0)) Or (e\EventState <> 1.0) Then
							    SoundTransmission = UpdateLever(e\room\Objects[3])
						    EndIf
						    If (Not SoundTransmission) Then
							    If e\SoundCHN2 <> 0 Then
							    	If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
							    EndIf
							    If e\SoundCHN3 <> 0 Then
							        If ChannelPlaying(e\SoundCHN3) = True Then StopChannel(e\SoundCHN3)
		               	        EndIf
						    EndIf
							
						    If e\EventState = 0.0 Then 
							    If SoundTransmission And Rand(100) = 1 Then
							    	If e\SoundCHN2 = 0 Then
									    LoadEventSound(e, "SFX\Character\LureSubject\Idle" + Rand(1, 6) + ".ogg", 1)
									    e\SoundCHN2 = PlaySound_Strict(e\Sound2)								
								    EndIf
								    If ChannelPlaying(e\SoundCHN2) = False Then
									    LoadEventSound(e, "SFX\Character\LureSubject\Idle" + Rand(1, 6) + ".ogg", 1)
									    e\SoundCHN2 = PlaySound_Strict(e\Sound2)
								    EndIf
							    EndIf
								
							    If SoundTransmission Then
							    	UpdateButton(e\room\Objects[4])
								    If ClosestButton = e\room\Objects[4] And MouseHit1 Then
									    e\EventState = 1.0 ; ~ Start the femur breaker
									    If SoundTransmission = True Then ; ~ Only play sounds if transmission is on
										    If e\SoundCHN2 <> 0 Then
											    If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
										    EndIf 
										    FemurBreakerSFX = LoadSound_Strict("SFX\Room\106Chamber\FemurBreaker.ogg")
										    e\SoundCHN2 = PlaySound_Strict(FemurBreakerSFX)
									    EndIf
								    EndIf
							    EndIf
						    ElseIf e\EventState = 1.0 ; ~ Bone was broken
							    If SoundTransmission And e\EventState3 < 2000.0 Then 
								    If e\SoundCHN2 = 0 Then 
									    LoadEventSound(e, "SFX\Character\LureSubject\Sniffling.ogg", 1)
									    e\SoundCHN2 = PlaySound_Strict(e\Sound2)								
								    EndIf
								    If ChannelPlaying(e\SoundCHN2) = False Then
									    LoadEventSound(e, "SFX\Character\LureSubject\Sniffling.ogg", 1)
									    e\SoundCHN2 = PlaySound_Strict(e\Sound2)
								    EndIf
							    EndIf
								
							    If e\EventState3 >= 2500.0 Then
							    	If e\EventState2 = 1.0 And e\EventState3 - FPSfactor < 2500.0 Then
									    PositionEntity(Curr106\Collider, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True))
									    Contained106 = False
									    ShowEntity(Curr106\OBJ)
									    Curr106\Idle = False
									    Curr106\State = -11.0
									    e\EventState = 2.0
									    Exit
								    EndIf
									
								    ShouldPlay = 10
									
								    PositionEntity(Curr106\Collider, EntityX(e\room\Objects[5], True), ((-6628.0) + 108.0 * (Min(e\EventState3 - 2500.0, 800.0) / 320.0)) * RoomScale, EntityZ(e\room\Objects[5], True))
								    HideEntity(Curr106\OBJ2)
									
								    RotateEntity(Curr106\Collider, 0.0, EntityYaw(e\room\Objects[5], True) + 180.0, 0.0, True)
								    Curr106\State = -11.0
								    AnimateNPC(Curr106, 206.0, 250.0, 0.1)
								    Curr106\Idle = True	
									
								    If e\EventState3 - FPSfactor < 2500.0 Then 
									    d.Decals = CreateDecal(0, EntityX(e\room\Objects[5], True), -6392.0 * RoomScale, EntityZ(e\room\Objects[5], True), 90.0, 0.0, Rnd(360.0)) 
									    d\Timer = 90000.0 : d\Alpha = 0.01 : d\AlphaChange = 0.005 : d\Size = 0.1 : d\SizeChange = 0.003
										
										If e\SoundCHN2 <> 0 Then
									    	If ChannelPlaying(e\SoundCHN2) Then StopChannel e\SoundCHN2
				    				    EndIf 
									    LoadEventSound(e, "SFX\Character\LureSubject\106Bait.ogg", 1)
									    e\SoundCHN2 = PlaySound_Strict(e\Sound2)
								    ElseIf e\EventState3 - FPSfactor < 2900.0 And e\EventState3 >= 2900.0 Then
									    If FemurBreakerSFX <> 0 Then 
											FreeSound_Strict(FemurBreakerSFX) : FemurBreakerSFX = 0
										EndIf
										
									    d.Decals = CreateDecal(0, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True), EntityZ(e\room\Objects[7], True), 0.0, 0.0, 0.0) 
										d\Timer = 90000.0 : d\Alpha = 0.01 : d\AlphaChange = 0.005 : d\Size = 0.05 : d\SizeChange = 0.002
									    RotateEntity(d\OBJ, EntityPitch(e\room\Objects[7], True) + Rnd(10.0, 20.0), EntityYaw(e\room\Objects[7], True) + 30.0, EntityRoll(d\OBJ))
									    MoveEntity(d\OBJ, 0,0.05, 0.2) 
									    RotateEntity(d\OBJ, EntityPitch(e\room\Objects[7], True), EntityYaw(e\room\Objects[7], True), EntityRoll(d\OBJ))
										EntityParent(d\OBJ, e\room\Objects[7])
									ElseIf e\EventState3 > 3200.0 Then
										If e\EventState2 = True Then
										    Contained106 = True
									    Else
										    PositionEntity(Curr106\Collider, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True))
											
										    Contained106 = False
										    ShowEntity(Curr106\OBJ)
										    Curr106\Idle = False
										    Curr106\State = -11.0
											
										    e\EventState = 2.0
										    Exit
									    EndIf
								    EndIf	
							    EndIf 	
						    EndIf
							
						    If e\EventState2 Then
						        PositionEntity (e\room\Objects[6], EntityX(e\room\Objects[6], True), CurveValue(-8308.0 * RoomScale + Sin(Float(MilliSecs2()) * 0.04) * 0.07, EntityY(e\room\Objects[6], True), 200.0), EntityZ(e\room\Objects[6], True), True)
						        RotateEntity(e\room\Objects[6], Sin(Float(MilliSecs2()) * 0.03), EntityYaw(e\room\Objects[6], True), -Sin(Float(MilliSecs2()) * 0.025), True)
					        Else
						        PositionEntity (e\room\Objects[6], EntityX(e\room\Objects[6], True), CurveValue(-8608.0 * RoomScale, EntityY(e\room\Objects[6], True), 200.0), EntityZ(e\room\Objects[6], True), True)
						        RotateEntity(e\room\Objects[6], 0, EntityYaw(e\room\Objects[6], True), 0, True)
					        EndIf
					    EndIf
					EndIf
					e\EventState4 = UpdateElevators(e\EventState4, e\room\RoomDoors[0], e\room\RoomDoors[1], e\room\Objects[11], e\room\Objects[12], e)
				Else
					If PlayerRoom\RoomTemplate\Name = "pocketdimension" Or PlayerRoom\RoomTemplate\Name = "dimension1499" Then
						If e\SoundCHN2 <> 0 Then
							If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
						EndIf
						If e\SoundCHN3 <> 0 Then
							If ChannelPlaying(e\SoundCHN3) = True Then StopChannel(e\SoundCHN3)
						EndIf
			        ElseIf PlayerRoom\RoomTemplate\Name = "room860" Then
						For e2.Events = Each Events
							If e2\EventName = "room860" Then
								If e2\EventState = 1.0 Then
									If e\SoundCHN2 <> 0 Then
										If ChannelPlaying(e\SoundCHN2) = True Then StopChannel(e\SoundCHN2)
									EndIf
									If e\SoundCHN3 <> 0 Then
										If ChannelPlaying(e\SoundCHN3) = True Then StopChannel(e\SoundCHN3)
									EndIf
								EndIf
								Exit
							EndIf
						Next
					EndIf
				EndIf
				;[End Block]
			Case "room205"
				;[Block]
				If PlayerRoom = e\room Then
					If e\EventState = 0 Or e\EventStr <> "LoadDone" Then
						If e\EventStr = "" And QuickLoadPercent = -1
							QuickLoadPercent = 0
							QuickLoad_CurrEvent = e
							e\EventStr = "Load0"
						EndIf
						
						If e\room\Objects[3] <> 0
							HideEntity(e\room\Objects[3])
							HideEntity(e\room\Objects[4])
							HideEntity(e\room\Objects[5])
							HideEntity(e\room\Objects[6])
						EndIf
						
						If e\room\RoomDoors[1]\Open = True
							e\EventState = 1.0
							GiveAchievement(Achv205)
						EndIf
					Else
						ShouldPlay = 16
						If (e\EventState < 65) Then
							If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True)) < 2.0 Then
								PlaySound_Strict(LoadTempSound("SFX\SCP\205\Enter.ogg"))
								
								e\EventState = Max(e\EventState, 65.0)
								
								ShowEntity(e\room\Objects[3])
								ShowEntity(e\room\Objects[4])
								ShowEntity(e\room\Objects[5])
								HideEntity(e\room\Objects[6])
								
								SetAnimTime(e\room\Objects[3], 492)
								SetAnimTime(e\room\Objects[4], 434)
								SetAnimTime(e\room\Objects[5], 434)
								
								e\room\RoomDoors[0]\Open = False
							EndIf
							
							If e\EventState > 7.0 Then
								If (Rand(0, 300) = 1) Then
									e\room\RoomDoors[0]\Open = (Not e\room\RoomDoors[0]\Open)
								EndIf
							EndIf 
							e\EventState2 = e\EventState2 + FPSfactor							
						EndIf
						
						Select e\EventState
							Case 1.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								HideEntity(e\room\Objects[5])
								HideEntity(e\room\Objects[4])
								HideEntity(e\room\Objects[3])
								; ~ Sitting
								ShowEntity(e\room\Objects[6])
								Animate2(e\room\Objects[6], AnimTime(e\room\Objects[6]), 526.0, 530.0, 0.2)
								If e\EventState2 > 70 * 20.0 Then e\EventState = e\EventState + 1.0
								;[End Block]
							Case 3.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								HideEntity(e\room\Objects[5])
								HideEntity(e\room\Objects[4])
								HideEntity(e\room\Objects[3])
								; ~ Laying down
								ShowEntity(e\room\Objects[6])
								Animate2(e\room\Objects[6], AnimTime(e\room\Objects[6]), 377.0, 525.0, 0.2)
								If e\EventState2 > 70 * 30.0 Then e\EventState = e\EventState + 1.0
								;[End Block]
							Case 5.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								HideEntity(e\room\Objects[5])
								HideEntity(e\room\Objects[4])
								HideEntity(e\room\Objects[3])
								; ~ Standing
								ShowEntity(e\room\Objects[6])
								Animate2(e\room\Objects[6], AnimTime(e\room\Objects[6]), 228.0, 376.0, 0.2)
								If e\EventState2 > 70 * 40.0 Then 
									e\EventState = e\EventState + 1.0
									PlaySound2(LoadTempSound("SFX\SCP\205\Horror.ogg"), Camera, e\room\Objects[6], 10.0, 0.3)
								EndIf	
								;[End Block]
							Case 7.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								ShowEntity(e\room\Objects[6])
								HideEntity(e\room\Objects[4])
								HideEntity(e\room\Objects[3])
								; ~ First demon appears
								ShowEntity(e\room\Objects[5])
								; ~ Sexy demon pose
								Animate2(e\room\Objects[5], AnimTime(e\room\Objects[5]), 500.0, 648.0, 0.2)
								If e\EventState2 > 70 * 60.0 Then 
									e\EventState = e\EventState + 1.0
									PlaySound2(LoadTempSound("SFX\SCP\205\Horror.ogg"), Camera, e\room\Objects[6], 10.0, 0.5)
								EndIf
								;[End Block]
							Case 9.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								ShowEntity(e\room\Objects[6])
								ShowEntity(e\room\Objects[5])
								HideEntity(e\room\Objects[3])
								; ~ Second demon appears
								ShowEntity(e\room\Objects[4])
								; ~ Idle
								Animate2(e\room\Objects[4], AnimTime(e\room\Objects[4]), 2.0, 200.0, 0.2)
								Animate2(e\room\Objects[5], AnimTime(e\room\Objects[5]), 4.0, 125.0, 0.2)
								
								If e\EventState2 > 70 * 80.0 Then 
									e\EventState = e\EventState + 1.0
									PlaySound_Strict(LoadTempSound("SFX\SCP\205\Horror.ogg"))
								EndIf
								;[End Block]
							Case 11.0
								;[Block[
								ShowEntity(e\room\Objects[1])
								ShowEntity(e\room\Objects[6])
								ShowEntity(e\room\Objects[5])
								ShowEntity(e\room\Objects[4])
								; ~ Third demon
								ShowEntity(e\room\Objects[3])
								; ~ Idle
								Animate2(e\room\Objects[3], AnimTime(e\room\Objects[3]), 2.0, 226.0, 0.2)
								Animate2(e\room\Objects[4], AnimTime(e\room\Objects[4]), 2.0, 200.0, 0.2)
								Animate2(e\room\Objects[5], AnimTime(e\room\Objects[5]), 4.0, 125.0, 0.2)
								
								If e\EventState2 > 70 * 85.0 Then e\EventState = e\EventState + 1.0
								;[End Block]
							Case 13.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								ShowEntity(e\room\Objects[6])
								ShowEntity(e\room\Objects[5])
								ShowEntity(e\room\Objects[4])
								ShowEntity(e\room\Objects[3])
								If AnimTime(e\room\Objects[6]) <> 227.0 Then SetAnimTime(e\room\Objects[6], 227.0)
								
								Animate2(e\room\Objects[3], AnimTime(e\room\Objects[3]), 2.0, 491.0, 0.05)
								Animate2(e\room\Objects[4], AnimTime(e\room\Objects[4]), 197.0, 433.0, 0.05)
								Animate2(e\room\Objects[5], AnimTime(e\room\Objects[5]), 2.0, 433.0, 0.05)
								;[End Block]
							Case 66.0
								;[Block]
								ShowEntity(e\room\Objects[1])
								Animate2(e\room\Objects[3], AnimTime(e\room\Objects[3]), 492.0, 534.0, 0.1, False)
								Animate2(e\room\Objects[4], AnimTime(e\room\Objects[4]), 434.0, 466.0, 0.1, False)
								Animate2(e\room\Objects[5], AnimTime(e\room\Objects[5]), 434.0, 494.0, 0.1, False)
								
								If AnimTime(e\room\Objects[3]) > 515.0 Then
									If AnimTime(e\room\Objects[3]) > 533.0 Then 
										e\EventState = 67.0
										e\EventState2 = 0.0									
										e\EventState3 = 0.0
										HideEntity(e\room\Objects[1])
									EndIf
								EndIf
								;[End Block]
							Case 67.0
								;[Block]
								If (Not chs\NoTarget) Then
									If Rand(150) = 1 Then
										DeathMSG = "The SCP-205 cycle seems to have resumed its normal course after the anomalies observed during "
										DeathMSG = DeathMSG + "[REDACTED]. The body of subject D-9341 was discovered inside the chamber. "
										DeathMSG = DeathMSG + "The subject exhibits signs of blunt force trauma typical for personnel who have "
										DeathMSG = DeathMSG + "entered the chamber when the lights are off."
										
										Injuries = Injuries + Rnd(0.4, 0.8)
										PlaySound_Strict(DamageSFX(Rand(2, 3)))
										CameraShake = 0.5
										
										e\EventState2 = Rnd(-0.1, 0.1)
										e\EventState3 = Rnd(-0.1, 0.1)
										
										If (Injuries > 5.0) Then Kill()
									EndIf
								EndIf
								
								TranslateEntity(Collider, e\EventState2, 0.0, e\EventState3)
								e\EventState2 = CurveValue(e\EventState2, 0.0, 10.0)								
								e\EventState3 = CurveValue(e\EventState3, 0.0, 10.0)
								;[End Block]
							Default
								;[Block]
								If Rand(3) = 1 Then
									HideEntity(e\room\Objects[1])
								Else
									ShowEntity(e\room\Objects[1])
								EndIf
								
								e\EventState3 = e\EventState3 + FPSfactor
								If (e\EventState3 > 50.0) Then
									ShowEntity(e\room\Objects[1])
									e\EventState = e\EventState + 1.0
									e\EventState3 = 0.0
								EndIf
								;[End Block]
						End Select
					EndIf
				Else If e\room\Objects[3] <> 0
					HideEntity(e\room\Objects[3])
					HideEntity(e\room\Objects[4])
					HideEntity(e\room\Objects[5])
					HideEntity(e\room\Objects[6])
				Else
					e\EventState = 0.0
					e\EventStr = ""
				EndIf
				;[End block]
			Case "room860"
				;[Block]
				; ~ e\EventState = Is the player in the forest
				
				; ~ e\EventState2 = Which side of the door did the player enter from
				
				; ~ e\EventState3 = Monster spawn timer
				
				Local fr.Forest = e\room\fr
				
				If PlayerRoom = e\room And fr <> Null Then 
					If e\EventState = 1.0 Then ; ~ The player is in the forest
						CurrStepSFX = 2
						
						Curr106\Idle = True
						
						UpdateForest(fr, Collider)
						
						If e\EventStr = "" And QuickLoadPercent = -1
							QuickLoadPercent = 0
							QuickLoad_CurrEvent = e
							e\EventStr = "Load0"
						EndIf
						
						If e\room\NPC[0] <> Null Then
							If (e\room\NPC[0]\State2 = 1.0 And e\room\NPC[0]\State > 1.0) Or e\room\NPC[0]\State > 2.0 ; ~ The monster is chasing the player
								ShouldPlay = 12
							Else
								ShouldPlay = 9
							EndIf
						EndIf
						
						; ~ The player fell
						If (Not NoClip)
							If EntityY(Collider) =< 28.5 Then 
								Kill() 
								BlinkTimer = -2.0
							ElseIf EntityY(Collider) > EntityY(fr\Forest_Pivot, True) + 0.5
								MoveEntity(Collider, 0.0, ((EntityY(fr\Forest_Pivot, True) + 0.5) - EntityY(Collider)) * FPSfactor, 0.0)
							EndIf
						EndIf
						
						If e\room\NPC[0] <> Null
							If e\room\NPC[0]\State = 0.0 Or EntityDistance(Collider, e\room\NPC[0]\Collider) > 20.0 Then
								e\EventState3 = e\EventState3 + (1.0 + CurrSpeed) * FPSfactor
								If (e\EventState3 Mod 500.0) < 10.0 And ((e\EventState3 - FPSfactor) Mod 500.0) > 490.0 Then
									If e\EventState3 > 3000.0 - (500.0 * SelectedDifficulty\aggressiveNPCs) And Rnd(10000 + (500.0 * SelectedDifficulty\aggressiveNPCs)) < e\EventState3
										e\room\NPC[0]\State = 2.0
										PositionEntity(e\room\NPC[0]\Collider, 0.0, -110.0, 0.0)
										e\EventState3 = e\EventState3 - Rnd(1000.0, 2000.0 - (500.0 * SelectedDifficulty\aggressiveNPCs))
									Else
										e\room\NPC[0]\State = 1.0
										PositionEntity(e\room\NPC[0]\Collider, 0.0, -110.0, 0.0)
									EndIf
								EndIf
							EndIf
						EndIf
						
						For i = 0 To 1
							If EntityDistance(fr\Door[i], Collider) < 0.5 Then
								If EntityInView(fr\Door[i], Camera) Then
									DrawHandIcon = True
									If MouseHit1 Then
										If i = e\EventState2 Then
											BlinkTimer = -10.0
											
											PlaySound_Strict(LoadTempSound("SFX\Door\WoodenDoorOpen.ogg"))
											
											RotateEntity(e\room\Objects[3], 0.0, 0.0, 0.0)
											RotateEntity(e\room\Objects[4], 0.0, 180.0, 0.0)
											
											PositionEntity(Collider, EntityX(e\room\Objects[2], True), 0.5, EntityZ(e\room\Objects[2], True))
											
											RotateEntity(Collider, 0.0, EntityYaw(e\room\OBJ, True) + e\EventState2 * 180.0, 0.0)
											MoveEntity(Collider, 0.0, 0.0, 1.5)
											
											ResetEntity(Collider)
											
											UpdateDoorsTimer = 0.0
											UpdateDoors()
											
											SecondaryLightOn = PrevSecondaryLightOn
											
											e\EventState = 0.0
											e\EventState3 = 0.0
										Else
											PlaySound_Strict(LoadTempSound("SFX\Door\WoodenDoorBudge.ogg"))
											Msg = "The door will not budge."
											MsgTimer = 70 * 5.0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
						
						If e\room\NPC[0] <> Null
							x = Max(1.0 - (e\room\NPC[0]\State3 / 300.0), 0.1)
						Else
							x = 2.0
						EndIf
						
						If (Not DebugHUD)
							CameraClsColor(Camera, x * 98.0, x * 133.0, x * 162.0)
							CameraRange(Camera, RoomScale, 8.5)
							CameraFogRange(Camera, 0.5, 8.0)
							CameraFogColor(Camera, x * 98.0, x * 133.0, x * 162.0)
						EndIf
					Else
						If (Not Contained106) Then Curr106\Idle = False
						If EntityYaw(e\room\Objects[3]) = 0.0 Then
							HideEntity(fr.Forest\Forest_Pivot)
							If Abs(Distance(EntityX(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), EntityX(Collider, True), EntityZ(Collider, True))) < 1.0 Then
								DrawHandIcon = True
								
								If SelectedItem = Null Then
									If MouseHit1 Then
										PlaySound_Strict(LoadTempSound("SFX\Door\WoodenDoorBudge.ogg"))
										Msg = "The door will not budge."
										MsgTimer = 70 * 5.0
									EndIf
								ElseIf SelectedItem\itemtemplate\tempname = "scp860" 
									If MouseHit1 Then
										PlaySound_Strict(LoadTempSound("SFX\Door\WoodenDoorOpen.ogg"))
										ShowEntity(fr.Forest\Forest_Pivot)
										SelectedItem = Null
										
										BlinkTimer = -10.0
										
										e\EventState = 1.0
										
										; ~ Reset monster spawn timer
										e\EventState3 = 0.0
										
										If e\room\NPC[0] <> Null Then
											; ~ Reset monster to the (hidden) idle state
											e\room\NPC[0]\State = 0.0
										EndIf
										
										PrevSecondaryLightOn = SecondaryLightOn
										SecondaryLightOn = True
										
										Pvt = CreatePivot()
										PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
										PointEntity(Pvt, e\room\OBJ)
										Ang# = WrapAngle(EntityYaw(Pvt) - EntityYaw(e\room\OBJ, True))
										If Ang > 90.0 And Ang < 270.0 Then
											PositionEntity(Collider, EntityX(fr\Door[0], True), EntityY(fr\Door[0], True) + EntityY(Collider, True) + 0.5, EntityZ(fr\Door[0], True), True)
											RotateEntity(Collider, 0.0, EntityYaw(fr\Door[0], True) - 180.0, 0.0, True)
											MoveEntity(Collider, -0.5, 0.0, 0.5)
											e\EventState2 = 1.0
										Else
											PositionEntity(Collider, EntityX(fr\Door[1], True), EntityY(fr\Door[1], True) + EntityY(Collider, True) + 0.5, EntityZ(fr\Door[1], True), True)
											RotateEntity(Collider, 0.0, EntityYaw(fr\Door[1], True) - 180.0, 0.0, True)
											MoveEntity(Collider, -0.5, 0.0, 0.5)
											e\EventState2 = 0.0
										EndIf
										FreeEntity(Pvt)
										ResetEntity(Collider)
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				Else
					If (fr = Null) Then
						RemoveEvent(e)
					Else
						If fr\Forest_Pivot <> 0 Then HideEntity(fr\Forest_Pivot)
					EndIf
				EndIf
				;[End Block]
			Case "room966"
				;[Block]
				If PlayerRoom = e\room Then
					Select e\EventState
						Case 0.0
							;[Block]
							; ~ A dirty workaround to hide the pause when loading 966 model
							If QuickLoadPercent = -1
								e\EventState = 1.0
								QuickLoadPercent = 0
								QuickLoad_CurrEvent = e
							EndIf
							;[End Block]
						Case 2.0
							;[Block]
							e\EventState = 2.0
							RemoveEvent(e)
							;[End Block]
					End Select
				EndIf
				;[End Block]
			Case "room1123"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityDistance(Collider, e\room\Objects[3]) < 0.9 Or (e\EventState > 0.0 And e\EventState < 7.0) Then
					    If Wearing714 = 0 Or WearingHazmat < 3 Or WearingGasMask < 3 Then
			                If e\EventState = 0.0 Then BlurTimer = 1000.0
					        CameraShake = 1.0
							If e\Sound3 = 0 Then e\Sound3 = LoadSound_Strict("SFX\SCP\1123\Ambient.ogg")
							e\SoundCHN3 = LoopSound2(e\Sound3, e\SoundCHN3, Camera, Collider, 4.0, 4.0)
					    EndIf
				    EndIf
					; ~ The event is started when the player picks up SCP-1123 (in Items.bb/UpdateItems())
					If e\EventState > 0.0 And e\EventState < 7.0 Then
						CanSave = False
					EndIf
					If e\EventState = 1.0 Then
						; ~ Saving Injuries and Bloodloss, so that the player won't be healed automatically
						PrevInjuries = Injuries
						PrevBloodloss = Bloodloss
						PrevSecondaryLightOn = SecondaryLightOn
						SecondaryLightOn = True
						
						e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\Objects[6], True), EntityY(e\room\Objects[6], True), EntityZ(e\room\Objects[6], True))
						
						Nazi% = CopyEntity(o\NPCModelID[22])
						Scale# = 0.5 / MeshWidth(Nazi)
						
						FreeEntity(e\room\NPC[0]\OBJ)
						e\room\NPC[0]\OBJ = CopyEntity(Nazi)
						ScaleEntity(e\room\NPC[0]\OBJ, Scale, Scale, Scale)
						
						FreeEntity(Nazi)
						PositionEntity(Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True), True)
						ResetEntity(Collider)
						
						CameraShake = 1.0
						BlurTimer = 1200
						Injuries = 1.0
						e\EventState = 2.0
					ElseIf e\EventState = 2.0
						e\EventState2 = e\EventState2 + FPSfactor
						
						PointEntity(e\room\NPC[0]\Collider, Collider)
						BlurTimer = Max(BlurTimer, 100.0)
						
						If e\EventState2 > 200.0 And e\EventState2 - FPSfactor =< 200.0 Then 							
							e\Sound = LoadSound_Strict("SFX\Music\Room1123.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound)
						EndIf
						
						If e\EventState2 > 1000.0 Then
							If e\Sound2 = 0 Then
								e\Sound2 = LoadSound_Strict("SFX\Door\1123DoorOpen.ogg")
								e\SoundCHN2 = PlaySound_Strict(e\Sound2)
							EndIf
							RotateEntity(e\room\Objects[11], 0.0, CurveAngle(10.0, EntityYaw(e\room\Objects[11], 0), 40.0), 0.0, False)
							If e\EventState2 >= 1040.0 And e\EventState2 - FPSfactor < 1040.0 Then 
								PlaySound2(LoadTempSound("SFX\SCP\1123\Officer1.ogg"), Camera, e\room\NPC[0]\OBJ)
							ElseIf e\EventState2 >= 1400.0 And e\EventState2 - FPSfactor < 1400.0 Then 
								PlaySound2(LoadTempSound("SFX\SCP\1123\Officer2.ogg"), Camera, e\room\NPC[0]\OBJ)
							EndIf
							e\room\NPC[0]\State = 3.0
							AnimateNPC(e\room\NPC[0], 3.0, 26.0, 0.2, True)
							If EntityDistance(Collider, e\room\Objects[4]) > 392.0 * RoomScale Then
								BlinkTimer = -10.0
								BlurTimer = 500.0
								PositionEntity(Collider, EntityX(e\room\Objects[5], True), EntityY(e\room\Objects[5], True), EntityZ(e\room\Objects[5], True), True)
								RotateEntity(Collider, 0.0, EntityYaw(e\room\OBJ, True) + 180.0, 0.0)
								ResetEntity(Collider)
								e\EventState = 3.0
							EndIf
						EndIf
					ElseIf e\EventState = 3.0
						If e\room\RoomDoors[0]\OpenState > 160.0 Then
							If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Music\1123.ogg")
							e\SoundCHN = PlaySound_Strict(e\Sound)
							
							PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True), EntityZ(e\room\Objects[7], True))
							ResetEntity(e\room\NPC[0]\Collider)
							
							e\EventState = 4.0
						EndIf
					ElseIf e\EventState = 4.0
						TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0.0, e\room\OBJ)
						
						If TFormedX() < 256.0 And TFormedZ() > -480.0 Then
							e\room\RoomDoors[0]\Open = False
						EndIf
						
						If EntityYaw(e\room\Objects[13], False) = 0 Then
							
							If EntityDistance(Collider, e\room\Objects[12]) < 1.0 Then
								DrawHandIcon = True
								If MouseHit1 Then
									RotateEntity(e\room\Objects[13], 0.0, 1.0, 0.0, False)
									RotateEntity(e\room\Objects[11], 0.0, 90.0, 0.0, False)
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Horror.ogg"))
								EndIf
							EndIf							
						Else
							RotateEntity(e\room\Objects[13], 0.0, CurveAngle(90.0, EntityYaw(e\room\Objects[13], False), 40.0), 0.0)
							If EntityYaw(e\room\Objects[13], False) > 30.0 Then
								e\room\NPC[0]\State = 3.0
								PointEntity(e\room\NPC[0]\Collider, Collider)
								AnimateNPC(e\room\NPC[0], 27.0, 54.0, 0.5, False)
								If e\room\NPC[0]\Frame >= 54.0 Then
									e\EventState = 5.0
									e\EventState2 = 0.0
									PositionEntity(Collider, EntityX(e\room\OBJ, True), 0.3, EntityZ(e\room\OBJ, True), True)
									ResetEntity(Collider)									
									BlinkTimer = -10.0
									BlurTimer = 500.0
									Injuries = 1.5
									Bloodloss = 70.0
								EndIf								
							EndIf
						EndIf
					ElseIf e\EventState = 5.0
						e\EventState2 = e\EventState2 + FPSfactor
						If e\EventState2 > 500.0 Then 
							RotateEntity(e\room\Objects[9], 0.0, 90.0, 0.0, False)
							RotateEntity(e\room\Objects[13], 0.0, 0.0, 0.0, False)
							
							x = (EntityX(e\room\Objects[8], True) + EntityX(e\room\Objects[12], True)) / 2.0
							y = EntityY(e\room\Objects[5], True)
							z = (EntityZ(e\room\Objects[8], True) + EntityZ(e\room\Objects[12], True)) / 2.0
							PositionEntity(Collider, x, y, z, True)
							ResetEntity(Collider)
							
							x = (EntityX(Collider, True) + EntityX(e\room\Objects[12], True)) / 2.0
							z = (EntityZ(Collider, True) + EntityZ(e\room\Objects[12], True)) / 2.0
							
							PositionEntity(e\room\NPC[0]\Collider, x, y + 0.2, z)
							ResetEntity(e\room\NPC[0]\Collider)
							
							Injuries = 1.5
							Bloodloss = 70.0
							BlinkTimer = -10.0
							
							de.Decals = CreateDecal(3, EntityX(Collider), 512.0 * RoomScale + 0.0005, EntityZ(Collider), 90.0, Rnd(360.0), 0.0)
							de\Size = 0.5 : ScaleSprite(de\OBJ, de\Size, de\Size)
							
							e\room\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\1123\Officer3.ogg")
							
							e\EventState = 6.0
						EndIf
					ElseIf e\EventState = 6.0
						PointEntity(e\room\NPC[0]\Collider, Collider)
						AnimateNPC(e\room\NPC[0], 75.0, 128.0, 0.04, True)	
						If e\room\NPC[0]\Sound <> 0 Then 
							If e\room\NPC[0]\SoundCHN <> 0 Then
								If ChannelPlaying(e\room\NPC[0]\SoundChn) = False Then 
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Gunshot.ogg"))
									e\EventState = 7.0
									FreeSound_Strict(e\room\NPC[0]\Sound) : e\room\NPC[0]\Sound = 0	
								EndIf
							EndIf
							If e\room\NPC[0]\Sound <> 0 Then e\room\NPC[0]\SoundCHN = LoopSound2(e\room\NPC[0]\Sound, e\room\NPC[0]\SoundCHN, Camera, e\room\NPC[0]\Collider, 7.0)
						EndIf
					ElseIf e\EventState = 7.0
						PositionEntity(Collider, EntityX(e\room\OBJ, True), 0.3, EntityZ(e\room\OBJ, True), True)
						ResetEntity(Collider)
						ShowEntity(ov\OverlayID[7])
						LightFlash = 6.0
						BlurTimer = 500.0
						Injuries = PrevInjuries
						Bloodloss = PrevBloodloss
						SecondaryLightOn = PrevSecondaryLightOn
						RotateEntity(e\room\Objects[9], 0.0, 0.0, 0.0, False)
						PrevInjuries = 0.0
						PrevBloodloss = 0.0
						PrevSecondaryLightOn = 0.0
						Crouch = False
						CanSave = True
						For i = 0 To MaxItemAmount - 1
							If Inventory(i) <> Null Then
								If Inventory(i)\ItemTemplate\Name = "Leaflet"
									RemoveItem(Inventory(i))
									Exit
								EndIf
							EndIf
						Next
						GiveAchievement(Achv1123)
						
						If e\SoundCHN3 <> 0 Then
							If ChannelPlaying(e\SoundCHN3) = True Then StopChannel(e\SoundCHN3)
							If e\Sound3 <> 0 Then
								FreeSound_Strict(e\Sound3) : e\Sound3 = 0
							EndIf
						EndIf
						
						RemoveNPC(e\room\NPC[0])
						RemoveEvent(e)						
					End If
				EndIf
				;[End Block]
			Case "room2testroom"
			    ;[Block]
                If e\EventState = 0.0
                    If PlayerRoom = e\room Then
                        e\room\Objects[7] = CopyEntity(o\NPCModelID[23])
					    ScaleEntity(e\room\Objects[7], 0.05, 0.05, 0.05)
						
						TFormPoint(EntityX(Collider), EntityY(Collider), EntityZ(Collider), 0.0, e\room\OBJ)
						If TFormedZ() = 0.0 Then Temp = -1 Else Temp = -Sgn(TFormedZ())
						TFormPoint(-720.0, 0.0, 816.0 * Temp, e\room\OBJ, 0)
						PositionEntity(e\room\Objects[7], TFormedX(), 0.0, TFormedZ())
						
						RotateEntity(e\room\Objects[7], -90.0, e\room\Angle - 90.0, 0.0)
						SetAnimTime(e\room\Objects[7], 297.0)
						e\EventState = 1.0
                    EndIf
                ElseIf e\EventState = 1.0
                    If e\room\Objects[7] <> 0 Then
						Animate2(e\room\Objects[7], AnimTime(e\room\Objects[7]), 284.0, 295.0, 0.3)
						MoveEntity(e\room\Objects[7], 0.0, (-0.008) * FPSfactor, 0.0)
						TFormPoint(EntityX(e\room\Objects[7]), EntityY(e\room\Objects[7]), EntityZ(e\room\Objects[7]), 0.0, e\room\OBJ)
						
						If Abs(TFormedX()) > 725 Or e\room\RoomDoors[0]\Open = True Then
							FreeEntity(e\room\Objects[7]) : e\room\Objects[7] = 0
							e\EventState = 2.0
						EndIf
					EndIf
				EndIf
				
				If PlayerRoom = e\room Then
                    If e\EventState = 2.0 Then
                        If EntityDistance(Collider, e\room\Objects[6]) < 2.5 And e\EventState > 0.0 Then
							PlaySound_Strict(LoadTempSound("SFX\SCP\079\TestroomWarning.ogg"))
							For i = 0 To 5
								em.Emitters = CreateEmitter(EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True), 0)
								em\RandAngle = 5 : em\Speed = 0.042 : em\SizeChange = 0.0025	
								TurnEntity(em\OBJ, 90.0, 0.0, 0.0, True)
							Next
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "tunnel2smoke"
				;[Block]
				If PlayerRoom = e\room Then
					If e\room\Dist < 3.5 Then
						PlaySound2(BurstSFX, Camera, e\room\OBJ) 
						For i = 0 To 1
							em.Emitters = CreateEmitter(EntityX(e\room\Objects[i], True), EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True), 0)
							TurnEntity(em\OBJ, 90.0, 0.0, 0.0, True)
							EntityParent(em\OBJ, e\room\OBJ)
							em\Size = 0.05 : em\RandAngle = 10 : em\Speed = 0.06 : em\SizeChange = 0.007
							For z = 0 To Ceil(3.3333 * (ParticleAmount + 1))
								p.Particles = CreateParticle(EntityX(em\OBJ, True), 448.0 * RoomScale, EntityZ(em\OBJ, True), Rand(em\MinImage, em\MaxImage), em\Size, em\Gravity, em\LifeTime)
								p\Speed = em\Speed : p\size = 0.05 : p\SizeChange = 0.008
								RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0, True)
							Next
						Next
						RemoveEvent(e)
					End If					
				EndIf
				;[End Block]
			Case "tunnel2"
				;[Block]
				If PlayerRoom = e\room Then
					If Curr173\Idle > 1 Then
						RemoveEvent(e)
						Exit
					Else		
						If e\EventState = 0.0 Then
							If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\OBJ), EntityZ(e\room\OBJ)) < 3.5 Then
								PlaySound_Strict(LightSFX)
								
								LightBlink = Rnd(0.0, 1.0) * (e\EventState / 200.0)
								e\EventState = 1.0
							End If
						End If	
					EndIf
				EndIf
				
				If e\EventState > 0.0 And e\EventState < 200.0 Then
					BlinkTimer = -10.0
					If e\EventState > 30.0 Then 
						LightBlink = 1.0 
						If e\EventState - FPSfactor =< 30.0 Then 
							PlaySound_Strict(LoadTempSound("SFX\Ambient\General\Ambient3.ogg"))
						EndIf
					EndIf
					If e\EventState - FPSfactor =< 100.0 And e\EventState > 100.0 Then
						PlaySound_Strict(LoadTempSound("SFX\Ambient\General\Ambient6.ogg"))
						PositionEntity(Curr173\Collider, EntityX(e\room\OBJ), 0.6, EntityZ(e\room\OBJ))
						ResetEntity(Curr173\Collider)					
						Curr173\Idle = True		
					EndIf
					LightBlink = 1.0
					e\EventState = e\EventState + FPSfactor
				ElseIf e\EventState <> 0.0 Then
					BlinkTimer = BLINKFREQ
					
					Curr173\Idle = False
					RemoveEvent(e)
				EndIf
				;[End Block]
			Case "tunnel106"
				;[Block]
				If e\EventState = 0.0 Then
					If e\room\Dist < 5.0 And e\room\Dist > 0.0 Then
						If Curr106\State >= 0.0 Then
							e\EventState = 1.0
						Else
							If Curr106\State =< -10.0 And EntityDistance(Curr106\Collider, Collider) > 5 And (Not EntityInView(Curr106\OBJ, Camera)) Then
								e\EventState = 1.0
								e\EventState2 = 1.0
							EndIf
						EndIf
					ElseIf Contained106
						RemoveEvent(e)
					EndIf
				ElseIf e\EventState = 1.0
					If e\room\Dist < 3.0 Or Rand(7000) = 1 Then
						e\EventState = 2.0
						d.Decals = CreateDecal(0, EntityX(e\room\OBJ), 445.0 * RoomScale, EntityZ(e\room\OBJ), -90.0, Rand(360.0), 0.0)
						d\Size = Rnd(0.5, 0.7) : EntityAlpha(d\OBJ, 0.7) : d\ID = 1 : ScaleSprite(d\OBJ, d\Size, d\Size)
						EntityAlpha(d\OBJ, Rnd(0.7, 0.85))
						
						PlaySound_Strict(HorrorSFX(10))
					ElseIf e\room\Dist > 8.0
						If Rand(5) = 1 Then
							Curr106\Idle = False
							RemoveEvent(e)
						Else
							Curr106\Idle = False
							Curr106\State = -10000.0
							RemoveEvent(e)
						End If
					EndIf
				Else
					If e\EventState2 = 1.0 Then
						ShouldPlay = 10
					EndIf
					e\EventState = e\EventState + FPSfactor
					If e\EventState =< 180.0 Then
						PositionEntity(Curr106\Collider, EntityX(e\room\OBJ, True), EntityY(Collider) + 1.0 - Min(Sin(e\EventState) * 1.5, 1.1), EntityZ(e\room\OBJ, True), True)
						PointEntity(Curr106\Collider, Camera)
						AnimateNPC(Curr106, 55.0, 104.0, 0.1)
						Curr106\Idle = True
						Curr106\State = 1.0
						ResetEntity(Curr106\Collider)
						Curr106\DropSpeed = 0.0
						PositionEntity(Curr106\OBJ, EntityX(Curr106\Collider), EntityY(Curr106\Collider) - 0.15, EntityZ(Curr106\Collider))
						RotateEntity(Curr106\OBJ, 0.0, EntityYaw(Curr106\Collider), 0.0)
						ShowEntity(Curr106\OBJ)
					ElseIf e\EventState > 180.0 And e\EventState < 300.0 Then
						Curr106\Idle = False
						Curr106\State = -10.0
						PositionEntity(Curr106\Collider, EntityX(e\room\OBJ, True), -3.0, EntityZ(e\room\OBJ, True), True)
						Curr106\PathTimer = 70 * 10.0
						Curr106\PathStatus = 0
						Curr106\PathLocation = 0
						de.Decals = CreateDecal(0, EntityX(e\room\OBJ, True), 0.01, EntityZ(e\room\OBJ, True), 90.0, Rand(360.0), 0.0)
						de\Size = 0.05 : de\SizeChange = 0.01 : EntityAlpha(de\OBJ, 0.8) : UpdateDecals()
						e\EventState = 300.0
					ElseIf e\EventState < 800.0
						If EntityY(Curr106\Collider) >= EntityY(Collider) - 0.05 Then
							RemoveEvent(e)
						Else
							TranslateEntity(Curr106\Collider, 0.0, ((EntityY(Collider, True) - 0.11) - EntityY(Curr106\Collider)) / 50.0, 0.0)
							If EntityY(Curr106\Collider) < -0.1 Then
								Curr106\CurrSpeed = 0.0
							EndIf
						EndIf
					Else
						RemoveEvent(e)
					EndIf
				EndIf
				;[End Block]
			Case "room2testroom173"
				;[Block]
				If PlayerRoom = e\room	
					If Curr173\Idle = 0 Then 
						If e\EventState = 0.0 Then
							If e\room\RoomDoors[0]\Open = True
							PositionEntity(Curr173\Collider, EntityX(e\room\Objects[0], True), 0.5, EntityZ(e\room\Objects[0], True))
							ResetEntity(Curr173\Collider)
							e\EventState = 1.0
							EndIf
						Else
							If e\room\Objects[2] = 0
								Local GlassTex% = LoadTexture_Strict("GFX\map\glass.png", 1 + 2)
								
								e\room\Objects[2] = CreateSprite()
								EntityTexture(e\room\Objects[2], GlassTex)
								SpriteViewMode(e\room\Objects[2], 2)
								ScaleSprite(e\room\Objects[2], 182.0 * RoomScale * 0.5, 192.0 * RoomScale * 0.5)
								Pvt = CreatePivot(e\room\OBJ)
								PositionEntity(Pvt, -632.0, 224.0, -208.0, False)
								PositionEntity(e\room\Objects[2], EntityX(Pvt, True), EntityY(Pvt, True), EntityZ(Pvt, True))
								FreeEntity(Pvt)
								RotateEntity(e\room\Objects[2], 0.0, e\room\Angle, 0.0)
								TurnEntity(e\room\Objects[2], 0.0, 180.0, 0.0)
								EntityParent(e\room\Objects[2], e\room\OBJ)
								FreeTexture(GlassTex)
							EndIf
							
							ShowEntity(e\room\Objects[2])
							; ~ Start a timer for SCP-173 breaking through the window
							e\EventState = e\EventState + 1.0
							Dist = EntityDistance(Collider, e\room\Objects[1])
							If Dist < 1.0 Then
								; ~ If close, increase the timer so that SCP-173 is ready to attack
								e\EventState = Max(e\EventState, 70 * 12.0)
							ElseIf Dist > 1.4
								; ~ If the player moves a bit further and blinks, SCP-173 attacks
								If e\EventState > 70 * 12.0 And (BlinkTimer =< -10.0 Or (Not EntityInView(Curr173\OBJ, Camera))) Then
									If EntityDistance(Curr173\Collider, e\room\Objects[0]) > 5.0 Then
										; ~ Remove event, if SCP-173 is far away from the room (perhaps because the player left and SCP-173 moved to some other room?) 
										RemoveEvent(e)
									Else
										PlaySound2(LoadTempSound("SFX\General\GlassBreak.ogg"), Camera, Curr173\OBJ) 
										FreeEntity(e\room\Objects[2])
										e\room\Objects[2] = 0
										PositionEntity(Curr173\Collider, EntityX(e\room\Objects[1], True), 0.5, EntityZ(e\room\Objects[1], True))
										ResetEntity(Curr173\Collider)
										RemoveEvent(e)
									EndIf
								EndIf	
							EndIf
						End If
					EndIf
				End If	
				;[End Block]
			Case "toiletguard"
				;[Block]
				If e\EventState = 0 Then
					If e\room\dist < 8.0  And e\room\dist > 0 Then e\EventState = 1
				ElseIf e\EventState = 1
					e\room\NPC[0]=CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[1],True), EntityY(e\room\Objects[1],True)+0.5, EntityZ(e\room\Objects[1],True))
					PointEntity e\room\NPC[0]\Collider, e\room\obj
					RotateEntity e\room\NPC[0]\Collider, 0, EntityYaw(e\room\NPC[0]\Collider)-20,0, True
					
					SetNPCFrame (e\room\NPC[0], 287)
					e\room\NPC[0]\State = 8
					
					e\EventState = 2	
				Else
					If e\Sound = 0 Then e\Sound = LoadSound_Strict("SFX\Character\Guard\SuicideGuard1.ogg")
					If e\room\dist < 15.0 And e\room\dist >= 4.0 Then 
						e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\NPC[0]\Collider, 15.0)
						
					ElseIf e\room\dist<4.0 And PlayerSoundVolume > 1.0
						If e\EventState2=0
							;Y=0.01
							de.Decals = CreateDecal(3,  EntityX(e\room\Objects[2],True), EntityY(e\room\Objects[2],True), EntityZ(e\room\Objects[2],True),0,e\room\angle+270,0)
							de\Size = 0.3 : ScaleSprite (de\obj, de\size, de\size)
							
							;de.Decals = CreateDecal(17,  EntityX(e\room\Objects[2],True), 0.01, EntityZ(e\room\Objects[2],True),90,Rnd(360),0)
							;de\Size = 0.1 : de\maxsize = 0.45 : de\sizechange = 0.0002 : UpdateDecals()
							
							e\EventState2 = 1
						EndIf
						If e\SoundCHN2 = 0
							StopChannel(e\SoundCHN)
							FreeSound_Strict(e\Sound)
							e\room\NPC[0]\Sound = LoadSound_Strict("SFX\Character\Guard\SuicideGuard2.ogg")
							e\SoundCHN2 = PlaySound2(e\room\NPC[0]\Sound, Camera, e\room\NPC[0]\Collider, 15.0)
						EndIf
						UpdateSoundOrigin(e\SoundCHN2,Camera,e\room\NPC[0]\Collider,15.0)
						If (Not ChannelPlaying(e\SoundCHN2)) Then RemoveEvent(e)
					EndIf
				EndIf
				;[End Block]
			Case "room008"
				;[Block]
				If PlayerRoom = e\room Then	
				    If EntityY(Collider) < - 4496.0 * RoomScale Then
					    GiveAchievement(Achv008)
					    If e\EventState = 0.0 Then					
						    If Curr173\Idle < 2 And EntityDistance(Curr173\Collider, Collider) > HideDistance ; ~ Just making sure that SCP-173 is far away enough to spawn him to this room
							    PositionEntity(Curr173\Collider, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True), True)
							    ResetEntity(Curr173\Collider)
						    EndIf											
						    e\EventState = 1.0
					    ElseIf e\EventState = 1.0
					        If EntityDistance(e\room\Objects[1], Collider) < 3.1 Then
						        e\SoundCHN = LoopSound2(AlarmSFX(0), e\SoundCHN, Camera, e\room\Objects[0], 5.0)
						    EndIf
							
						    If (MilliSecs2() Mod 1000) < 500 Then
						    	ShowEntity(e\room\Objects[5]) 
						    Else
							    HideEntity(e\room\Objects[5])
						    EndIf
						    
						    Dist = EntityDistance(Collider, e\room\Objects[0])
						    If Dist < 2.0 Then 
							    e\room\RoomDoors[0]\Locked = True
							    e\room\RoomDoors[1]\Locked = True
							    
							    If e\EventState2 = 0.0 Then
								    ShowEntity(e\room\Objects[2])
								    If EntityDistance(Curr173\Collider, e\room\Objects[4]) < 3.0
									    If BlinkTimer < -10.0 Or (Not EntityInView(Curr173\OBJ, Camera)) And Curr173\Idle = 0 Then
										    PositionEntity(Curr173\Collider, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True), True)
										    ResetEntity(Curr173\Collider)
											
										    HideEntity(e\room\Objects[2])
											
										    If WearingHazmat = 0 Then
												Injuries = Injuries + 0.1
												Msg = "The window shattered and a piece of glass cut your arm."
												MsgTimer = 70 * 8
												
											    If I_008\Timer = 0.0 Then I_008\Timer = 1.0
											EndIf
											PlaySound2(LoadTempSound("SFX\General\GlassBreak.ogg"), Camera, e\room\Objects[0]) 
											
											e\EventState2 = 1.0
									    EndIf
								    EndIf
							    EndIf
								
							    If Dist < 1.0 Then
								    If EntityInView(e\room\Objects[0], Camera) Then
									    DrawHandIcon = True
										If MouseDown1 Then
										    DrawArrowIcon(2) = True
										    RotateEntity(e\room\Levers[0], Max(Min(EntityPitch(e\room\Levers[0]) + Max(Min(-Mouse_Y_Speed_1, 10.0), -10.0), 89.0), 35.0), EntityYaw(e\room\Levers[0]), 0.0)
									    EndIf
								    EndIf
							    EndIf
								
							    If WearingHazmat = 0 And Bloodloss > 0.0
								    If I_008\Timer = 0.0
									    I_008\Timer = 1.0
								    EndIf
							    EndIf
						    EndIf
							
						    If EntityPitch(e\room\Levers[0], True) < 40.0 Then 
							    e\EventState = 2.0
							    PlaySound_Strict(LeverSFX)
						    Else
							    p.Particles = CreateParticle(EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0],True), 6, 0.02, -0.12)
								p\SizeChange = 0.012 :  p\Achange = -0.015
							    RotateEntity(p\Pvt, -90.0, 0.0, 0.0, True)
							    TurnEntity(p\Pvt, Rnd(-26.0, 26.0), Rnd(-26.0, 26.0), Rnd(360.0))
							EndIf		
					    Else
						    HideEntity(e\room\Objects[5])
						    e\room\RoomDoors[0]\Locked = False
						    e\room\RoomDoors[1]\Locked = False
						    e\room\RoomDoors[2]\Locked = False
							
						    RotateEntity(e\room\Levers[0], CurveAngle(1.0, EntityPitch(e\room\Levers[0], True), 15.0), EntityYaw(e\room\Levers[0], True), 0.0, True)
					    EndIf
				    EndIf
				    e\EventState3 = UpdateElevators(e\EventState3, e\room\RoomDoors[3], e\room\RoomDoors[4], e\room\Objects[8], e\room\Objects[9], e)
				EndIf
				;[End Block]
			Case "106victim"
				;[Block]
				If (Not Contained106) Then
					If PlayerRoom = e\room Then
						If e\EventState = 0.0 Then
							de.Decals = CreateDecal(0, EntityX(e\room\OBJ), 799.0 * RoomScale, EntityZ(e\room\OBJ), -90.0, Rnd(360.0), 0.0)
							de\Size = 0.05 : de\SizeChange = 0.0015 : UpdateDecals()
							EntityAlpha(de\OBJ, 0.8)
							PlaySound_Strict(DecaySFX(3))
							e\EventState = 1.0
						EndIf
					EndIf
					
					If e\EventState > 0.0 Then 
						If e\room\NPC[0] = Null Then
							e\EventState = e\EventState + FPSfactor
						EndIf
						If e\EventState > 200.0 Then
							If e\room\NPC[0] = Null Then
								e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\OBJ), 900.0 * RoomScale, EntityZ(e\room\OBJ))
								RotateEntity(e\room\NPC[0]\Collider, 0.0, Rnd(360.0), 0.0, True)
								ChangeNPCTextureID(e\room\NPC[0], 5)
								e\room\NPC[0]\State = 6.0
								
								PlaySound_Strict(HorrorSFX(0))
								PlaySound_Strict(DecaySFX(2))
							EndIf
							
							If ChannelPlaying(e\SoundCHN) = True Then
								UpdateSoundOrigin(e\SoundCHN, Camera, e\room\OBJ)
							EndIf
							
							e\room\NPC[0]\FallingPickDistance = 0.0
							EntityType(e\room\NPC[0]\Collider, HIT_PLAYER)
							If EntityY(e\room\NPC[0]\Collider) > 0.35 Then
								AnimateNPC(e\room\NPC[0], 1.0, 10.0, 0.12, False)
								Dist# = EntityDistance(Collider, e\room\NPC[0]\Collider)
								If Dist < 0.8 Then ; ~ Get the player out of the way
									fdir# = point_direction(EntityX(Collider, True), EntityZ(Collider, True), EntityX(e\room\NPC[0]\Collider, True), EntityZ(e\room\NPC[0]\Collider, True))
									TranslateEntity(Collider, Cos(-fdir + 90.0) * (Dist - 0.8) * (Dist - 0.8), 0.0, Sin(-fdir + 90.0) * (Dist - 0.8) * (Dist - 0.8))
								EndIf
								If EntityY(e\room\NPC[0]\Collider) > 0.6 Then EntityType(e\room\NPC[0]\Collider, 0)
							Else
								e\EventState = e\EventState + FPSfactor
								AnimateNPC(e\room\NPC[0], 11.0, 19.0, 0.25, False)
								If e\Sound = 0 Then 
									LoadEventSound(e, "SFX\General\BodyFall.ogg")
									PlaySound2(e\Sound, Camera, e\room\NPC[0]\Collider)
									
									de.Decals = CreateDecal(0, EntityX(e\room\OBJ), 0.001, EntityZ(e\room\OBJ), 90.0, Rand(360.0), 0.0)
									de\Size = 0.4 : UpdateDecals()	
									EntityAlpha(de\OBJ, 0.8)
								EndIf
								
								If e\EventState > 400.0 Then
									If e\Sound <> 0 Then 
										FreeSound_Strict(e\Sound) : e\Sound = 0
									EndIf
									RemoveEvent(e)
								EndIf								
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "106sinkhole"
				;[Block]
				If e\EventState = 0.0 Then
					de.Decals = CreateDecal(0, EntityX(e\room\OBJ) + Rnd(-0.5, 0.5), 0.01, EntityZ(e\room\OBJ) + Rnd(-0.5, 0.5), 90.0, Rnd(360.0), 0.0)
					de\Size = 2.5
					ScaleSprite(de\OBJ, de\Size, de\Size)
					
					e\EventState = 1.0
				ElseIf PlayerRoom = e\room
					If e\Sound = 0 Then
						e\Sound = LoadSound_Strict("SFX\Room\Sinkhole.ogg")
					Else
						e\SoundCHN = LoopSound2(e\Sound, e\SoundCHN, Camera, e\room\OBJ, 4.5, 1.5)
					EndIf
					Dist = Distance(EntityX(Collider), EntityZ(Collider),EntityX(e\room\OBJ), EntityZ(e\room\OBJ))
					If Dist < 2.0 Then
						CurrStepSFX = 1
						CurrSpeed = CurveValue(0.0, CurrSpeed, Max(Dist * 50.0, 1.0))	
						CrouchState = (2.0 - Dist) / 2.0
						
						If Dist < 0.5 Then
							If e\EventState2 = 0.0 Then
								PlaySound_Strict(LoadTempSound("SFX\Room\SinkholeFall.ogg"))
							EndIf
							
							CurrSpeed = CurveValue(0.0, CurrSpeed, Max(Dist  * 50.0,1.0))
							
							x = CurveValue(EntityX(e\room\OBJ), EntityX(Collider), 10.0)
							y = CurveValue(EntityY(e\room\OBJ) - e\EventState2, EntityY(Collider), 25.0)
							z = CurveValue(EntityZ(e\room\OBJ), EntityZ(Collider), 10.0)
							PositionEntity(Collider, x, y, z, True)
							
							DropSpeed = 0.0
							
							ResetEntity(Collider)
							
							e\EventState2 = Min(e\EventState2 + FPSfactor / 200.0, 2.0)
							
							If e\EventState2 >= 0.2 Then BlinkTimer = -10.0
							
							BlurTimer = e\EventState2 * 500.0
							
							If e\EventState2 = 2.0 Then MoveToPocketDimension()
						EndIf
					EndIf
				Else 
					e\EventState2 = 0.0
				EndIf
				;[End Block]
			Case "682roar"
				;[Block]
				If e\EventState = 0.0 Then
					If PlayerRoom = e\room Then e\EventState = 70 * Rnd(300.0, 1000.0)
				ElseIf PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "room860" And PlayerRoom\RoomTemplate\Name <> "room1123" And PlayerRoom\RoomTemplate\Name <> "dimension1499" 
					e\EventState = e\EventState - FPSfactor
					
					If e\EventState < 70 * 17.0 Then
						If	e\EventState + FPSfactor >= 70 * 17.0 Then LoadEventSound(e, "SFX\SCP\682\Roar.ogg") : e\SoundCHN = PlaySound_Strict(e\Sound)
						If e\EventState > (70 * 17.0) - (70 * 3.0) Then CameraShake = 0.5
						If e\EventState < (70 * 17.0) - (70 * 7.5) And e\EventState > (70 * 17.0) - (70 * 11.0) Then CameraShake = 2.0				
						If e\EventState < 70 * 1.0 Then 
							If e\Sound <> 0 Then FreeSound_Strict(e\Sound) 
							RemoveEvent(e)
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room914"
				;[Block]
				If PlayerRoom = e\room Then
					If e\room\RoomDoors[2]\Open
						GiveAchievement(Achv914)
						e\EventState2 = 1.0
					EndIf
					
					If e\EventState2 = 1.0
						ShouldPlay = 22
					EndIf
					
					EntityPick(Camera, 1.0)
					If PickedEntity() = e\room\Objects[0] Then
						DrawHandIcon = True
						If MouseHit1 Then GrabbedEntity = e\room\Objects[0]
					ElseIf PickedEntity() = e\room\Objects[1]
						DrawHandIcon = True
						If MouseHit1 Then GrabbedEntity = e\room\Objects[1]
					EndIf
					
					If MouseDown1 Or MouseHit1 Then
						If GrabbedEntity <> 0 Then ; ~ Avain
							If GrabbedEntity = e\room\Objects[0] Then
								If e\EventState = 0.0 Then
									DrawHandIcon = True
									TurnEntity(GrabbedEntity, 0.0, 0.0, -Mouse_X_Speed_1 * 2.5)
									
									Angle = WrapAngle(EntityRoll(e\room\Objects[0]))
									If Angle > 181.0 Then DrawArrowIcon(3) = True
									DrawArrowIcon(1) = True
									
									If Angle < 90.0 Then
										RotateEntity(GrabbedEntity, 0.0, 0.0, 361.0)
									ElseIf Angle < 180.0
										RotateEntity(GrabbedEntity, 0.0, 0.0, 180)
									EndIf
									
									If Angle < 181.0 And Angle > 90.0 Then
										For it.Items = Each Items
											If it\Collider <> 0 And it\Picked = False Then
												If Abs(EntityX(it\Collider) - (e\room\x - 712.0 * RoomScale)) < 200.0 Then
													If Abs(EntityY(it\Collider) - (e\room\y + 648.0 * RoomScale)) < 104.0 Then
														e\EventState = 1.0
														e\SoundCHN = PlaySound2(MachineSFX, Camera, e\room\Objects[1])
														e\room\RoomDoors[1]\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\914\DoorClose.ogg"), Camera, e\room\RoomDoors[1]\OBJ)
														Exit
													EndIf
												End If
											End If
										Next
									EndIf
								End If
							ElseIf GrabbedEntity = e\room\Objects[1]
								If e\EventState = 0.0 Then
									DrawHandIcon = True
									TurnEntity(GrabbedEntity, 0.0, 0.0, -Mouse_X_Speed_1 * 2.5)
									
									Angle = WrapAngle(EntityRoll(e\room\Objects[1]))
									DrawArrowIcon(3) = True
									DrawArrowIcon(1) = True
									
									If Angle > 90.0 Then
										If Angle < 180.0 Then
											RotateEntity(GrabbedEntity, 0.0, 0.0, 90.0)
										ElseIf Angle < 270.0
											RotateEntity(GrabbedEntity, 0.0, 0.0, 270.0)
										EndIf
									EndIf
								End If
							End If
						End If
					Else
						GrabbedEntity = 0
					End If
					
					Local Setting$ = ""
					
					If GrabbedEntity <> e\room\Objects[1] Then
						Angle = WrapAngle(EntityRoll(e\room\Objects[1]))
						If Angle < 22.5 Then
							Angle = 0.0
							Setting = "1:1"
						ElseIf Angle < 67.5
							Angle = 40.0
							Setting = "Coarse"
						ElseIf Angle < 180.0
							Angle = 90.0
							Setting = "Rough"
						ElseIf Angle > 337.5
							Angle = 359.0 - 360.0
							Setting = "1:1"
						ElseIf Angle > 292.5
							Angle = 320.0 - 360.0
							Setting = "Fine"
						Else
							Angle = 270.0 - 360.0
							Setting = "Very Fine"
						End If
						RotateEntity(e\room\Objects[1], 0.0, 0.0, CurveValue(Angle, EntityRoll(e\room\Objects[1]), 20.0))
					EndIf
					
					For i = 0 To 1
						If GrabbedEntity = e\room\Objects[i] Then
							If (Not EntityInView(e\room\Objects[i], Camera)) Then
								GrabbedEntity = 0
							ElseIf EntityDistance(e\room\Objects[i], Camera) > 1.0
								GrabbedEntity = 0
							End If
						End If
					Next
					
					If e\EventState > 0.0 Then
						e\EventState = e\EventState + FPSfactor
						
						e\room\RoomDoors[1]\Open = False
						If e\EventState > 70 * 2.0 Then
							If e\room\RoomDoors[0]\Open = True Then
								e\room\RoomDoors[0]\SoundCHN = PlaySound2(LoadTempSound("SFX\SCP\914\DoorClose.ogg"), Camera, e\room\RoomDoors[0]\OBJ)
							EndIf
							e\room\RoomDoors[0]\Open = False
						EndIf
						
						If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True)) < (170.0 * RoomScale) Then
							If Setting = "Rough" Or Setting = "Coarse" Then
								If e\EventState > 70 * 2.6 And e\EventState - FPSfactor2 < 70 * 2.6 Then PlaySound_Strict(Death914SFX)
							EndIf
							
							If e\EventState > 70 * 3.0 Then
								Select Setting
									Case "Rough"
										;[Block]
										KillTimer = Min(-1.0, KillTimer)
										BlinkTimer = -10.0
										If e\SoundCHN <> 0 Then StopChannel e\SoundCHN
										DeathMSG = Chr(34) + "A heavily mutilated corpse found inside the output booth of SCP-914. DNA testing identified the corpse as Class D Subject D-9341. "
										DeathMSG = DeathMSG + "The subject had obviously been " + Chr(34) + "refined" + Chr(34) + " by SCP-914 on the " + Chr(34) + "Rough" + Chr(34) + " setting, but we are still confused as to how he "
										DeathMSG = DeathMSG + "ended up inside the intake booth and who or what wound the key." + Chr(34)
										;[End Block]
									Case "Coarse"
										;[Block]
										BlinkTimer = -10.0
										If e\EventState - FPSfactor2 < 70 * 3.0 Then PlaySound_Strict(Use914SFX)
										;[End Block]
									Case "1:1"
										;[Block]
										BlinkTimer = -10.0
										If e\EventState - FPSfactor2 < 70 * 3.0 Then PlaySound_Strict(Use914SFX)
										;[End Block]
									Case "Fine", "Very Fine"
										;[Block]
										BlinkTimer = -10.0
										If e\EventState - FPSfactor2 < 70 * 3.0 Then PlaySound_Strict(Use914SFX)
										;[End Block]
								End Select
							End If
						EndIf
						
						If e\EventState > 70 * 6.0 Then	
							RotateEntity(e\room\Objects[0], EntityPitch(e\room\Objects[0]), EntityYaw(e\room\Objects[0]), CurveAngle(0.0, EntityRoll(e\room\Objects[0]), 10.0))
						Else
							RotateEntity(e\room\Objects[0], EntityPitch(e\room\Objects[0]), EntityYaw(e\room\Objects[0]), 180.0)
						EndIf
						
						If e\EventState > 70 * 12.0 Then							
							For it.Items = Each Items
								If it\Collider <> 0 And it\Picked = False Then
									If Distance(EntityX(it\Collider), EntityZ(it\Collider), EntityX(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True)) < 180.0 * RoomScale Then
										Use914(it, Setting, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True), EntityZ(e\room\Objects[3], True))
									End If
								End If
							Next
							
							If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[2], True), EntityZ(e\room\Objects[2], True)) < 160.0 * RoomScale Then
								Select Setting
									Case "Coarse"
										;[Block]
										Injuries = 4.0
										Msg = "You notice countless small incisions all around your body. They are bleeding heavily."
										MsgTimer = 70 * 8.0
										;[End Block]
									Case "1:1"
										;[Block]
										InvertMouse = (Not InvertMouse)
										;[End Block]
									Case "Fine", "Very Fine"
										;[Block]
										SuperMan = True
										;[End Block]
								End Select
								BlurTimer = 1000.0
								PositionEntity(Collider, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True) + 1.0, EntityZ(e\room\Objects[3], True))
								ResetEntity(Collider)
								DropSpeed = 0.0
							EndIf								
							e\room\RoomDoors[0]\Open = True
							e\room\RoomDoors[1]\Open = True
							RotateEntity(e\room\Objects[0], 0.0, 0.0, 0.0)
							e\EventState = 0.0
							
							Local OpenSFX914% = LoadTempSound("SFX\SCP\914\DoorOpen.ogg")
							
							e\room\RoomDoors[0]\SoundCHN = PlaySound2(OpenSFX914, Camera, e\room\RoomDoors[0]\OBJ)
							e\room\RoomDoors[1]\SoundCHN = PlaySound2(OpenSFX914, Camera, e\room\RoomDoors[1]\OBJ)
						End If
					End If
				EndIf
				UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[1])
				;[End Block]
			Case "1048a"
				;[Block]
				If PlayerRoom = e\room Then 
					If chs\NoTarget Then e\EventState = 1.0
				EndIf
				
				If e\room\Objects[0] = 0 Then
					If PlayerRoom <> e\room Then
						Dist = Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\obj), EntityZ(e\room\obj))
						If Dist < 16.0 Then
							For e2.Events = Each Events
								If e2\EventName = e\EventName Then
									If e2\room <> e\room Then
										If e2\room\Objects[0] <> 0 Then
											e\room\Objects[0] = CopyEntity(e2\room\Objects[0])
											Exit
										EndIf
									EndIf
								EndIf
							Next
							If e\room\Objects[0] = 0 Then
								e\room\Objects[0] =	CopyEntity(o\NPCModelID[17])
							EndIf
							ScaleEntity(e\room\Objects[0], 0.05, 0.05, 0.05)
							SetAnimTime(e\room\Objects[0], 2)
							PositionEntity(e\room\Objects[0], EntityX(e\room\OBJ), 0.0, EntityZ(e\room\OBJ))
							
							RotateEntity(e\room\Objects[0], -90.0, Rnd(0.0, 360.0), 0.0)
							
							e\Sound = LoadSound_Strict("SFX\SCP\1048A\Shriek.ogg")
							e\Sound2 = LoadSound_Strict("SFX\SCP\1048A\Growth.ogg")
							
							e\EventState = 1.0
						EndIf
					EndIf
				Else
					e\EventState3 = 1.0
					Select e\EventState
						Case 1.0
							;[Block]
							Animate2(e\room\Objects[0], AnimTime(e\room\Objects[0]), 2.0, 395.0, 1.0)
							
							If (EntityDistance(Collider, e\room\Objects[0]) < 2.5) Then e\EventState = 2.0
							;[End Block]
						Case 2.0
							;[Block]
							Local PrevFrame# = AnimTime(e\room\Objects[0])
							
							Animate2(e\room\Objects[0], PrevFrame, 2.0, 647.0, 1.0, False)
							
							If (PrevFrame =< 400.0 And AnimTime(e\room\Objects[0]) > 400.0) Then
								e\SoundCHN = PlaySound_Strict(e\Sound)
							EndIf
							
							Local Volume# = Max(1.0 - Abs(PrevFrame - 600.0) / 100.0, 0.0)
							
							BlurTimer = Volume * 1000.0
							CameraShake = Volume * 10.0
							
							PointEntity(e\room\Objects[0], Collider)
							RotateEntity(e\room\Objects[0], -90.0, EntityYaw(e\room\Objects[0]), 0.0)
							
							If PrevFrame > 646.0 Then
								If PlayerRoom = e\room Then
									e\EventState = 3.0	
									PlaySound_Strict(e\Sound2)
									
									Msg = "Something is growing all around your body."
									MsgTimer = 70.0 * 3.0
								Else
									e\EventState3 = 70 * 30.0
								EndIf
							EndIf
							;[End Block]
						Case 3.0
							;[Block]
							If I_427\Using = 0 And I_427\Timer < 70 * 360.0 Then
								e\EventState2 = e\EventState2 + FPSfactor
							EndIf
							
							BlurTimer = e\EventState2 * 2.0
							
							If I_427\Using = 0 And I_427\Timer < 70 * 360.0 Then
								If e\EventState2 > 250.0 And e\EventState2 - FPSfactor =< 250.0 Then
									Select Rand(3)
										Case 1
											;[Block]
											Msg = "Ears are growing all over your body."
											;[End Block]
										Case 2
											;[Block]
											Msg = "Ear-like organs are growing all over your body."
											;[End Block]
										Case 3
											;[Block]
											Msg = "Ears are growing all over your body. They are crawling on your skin."
											;[End Block]
									End Select
									MsgTimer = 70.0 * 3.0
								Else If e\EventState2 > 600.0 And e\EventState2 - FPSfactor =< 600.0
									Select Rand(4)
										Case 1
											;[Block]
											Msg = "It is becoming difficult to breathe."
											;[End Block]
										Case 2
											;[Block]
											Msg = "You have excellent hearing now. Also, you are dying."
											;[End Block]
										Case 3
											;[Block]
											Msg = "The ears are growing inside your body."
											;[End Block]
										Case 4
											;[Block]
											Msg = Chr(34) + "Can't... Breathe..." + Chr(34)
											;[End Block]
									End Select
									MsgTimer = 70.0 * 5.0
								EndIf
							EndIf
							
							If e\EventState2 > 70 * 15.0 Then
								DeathMSG = "A dead body covered in ears was found in [REDACTED]. Subject was presumably attacked by an instance of SCP-1048-A and suffocated to death by the ears. "
								DeathMSG = DeathMSG + "Body was sent for autopsy."
								Kill()
								RemoveEvent(e)
							EndIf
							
							; ~ Remove event when player was cured by SCP-427
							If e\EventState2 = 0.0 And I_427\Using > 0 Then
								If PlayerRoom = e\room Then BlinkTimer = -10.0
								If e\room\Objects[0] <> 0 Then
									FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
								EndIf
								RemoveEvent(e)
							EndIf
					End Select 
					
					If e <> Null Then
						If ChannelPlaying(e\SoundCHN) = True Then
							UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 6.0)
						EndIf
						If PlayerRoom <> e\room Then
							If e\EventState3 > 0.0 Then
								e\EventState3 = e\EventState3 + FPSfactor
								If e\EventState3 > 70 * 25.0 Then
									FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
									RemoveEvent(e)
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room4tunnels"
				;[Block]
				If e\room\Dist < 10.0 And e\room\Dist > 0.0 Then
					e\room\NPC[0] = CreateNPC(NPCtypeD, EntityX(e\room\OBJ, True) + 1.0, 0.5, EntityZ(e\room\OBJ, True) + 1.0)
					ChangeNPCTextureID(e\room\NPC[0], 10)
					RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\OBJ) - Rand(20.0, 60.0), 0.0, True)	
					SetNPCFrame(e\room\NPC[0], 19.0)
					e\room\NPC[0]\State = 8.0
					
					RemoveEvent(e)
				EndIf
				;[End Block]
			Case "room2gw_b"
				;[Block]
				If e\room\Dist < 8.0
					If e\EventState = 0.0 Then
						e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True))
						PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
						RotateEntity(e\room\NPC[0]\Collider, 0.0, EntityYaw(e\room\NPC[0]\Collider), 0.0, True)
						SetNPCFrame(e\room\NPC[0], 288.0)
						e\room\NPC[0]\State = 8.0
						
						e\EventState = 1.0
					EndIf
					
					p.Particles = CreateParticle(EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True), 6, 0.2, 0.0, 10.0)
					p\Speed = 0.01 : p\Achange = -0.02
					RotateEntity(p\Pvt, -60.0, e\room\Angle - 90.0, 0.0)
					
					e\SoundCHN = LoopSound2(AlarmSFX(3), e\SoundCHN, Camera, e\room\Objects[1], 5.0)
				EndIf
				;[End Block]
			Case "room2scps2"
				;[Block]
				If e\room\Dist < 15.0
					If Contained106 Then e\EventState = 2.0
					If Curr106\State < 0.0 Then e\EventState = 2.0
					
					If e\EventState < 2.0
						If e\EventState = 0.0
							LoadEventSound(e, "SFX\Character\Scientist\EmilyScream.ogg")
							e\SoundCHN = PlaySound2(e\Sound, Camera, e\room\Objects[0], 100.0, 1.0)
							de.Decals = CreateDecal(0, EntityX(e\room\Objects[0], True), e\room\y + 2.0 * RoomScale, EntityZ(e\room\Objects[0], True), 90.0, Rand(360.0), 0.0)
							de\Size = 0.5 : EntityAlpha(de\OBJ, 0.8)
							EntityFX(de\OBJ, 1)
							e\EventState = 1.0
						ElseIf e\EventState = 1.0
							If ChannelPlaying(e\SoundCHN) = False
								e\EventState = 2.0
								e\room\RoomDoors[0]\Locked = False
							Else
								UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 100.0, 1.0)
							EndIf
						EndIf
					Else
						e\room\RoomDoors[0]\Locked = False
						RemoveEvent(e)
					EndIf
				EndIf
				;[End Block]
			Case "room1162"
				;[Block]
				; ~ e\EventState = A variable to determine the "nostalgia" items
				; ~ 0.0 = No nostalgia item
				; ~  1.0 = Lost key
				; ~  2.0 = Disciplinary Hearing DH-S-4137-17092
				; ~  3.0 = Coin
				; ~  4.0 = Movie Ticket
				; ~  5.0 = Old Badge
				
				; ~ e\EventState2 = Defining which slot from the Inventory should be picked
				
				; ~ e\EventState3 = A check for if a item should be removed
				; ~ 0.0 = no item "trade" will happen
				; ~ 1.0 = item "trade" will happen
				; ~ 2.0 = the player doesn't has any items in the Inventory, giving him heavily Injuries and giving him a random item
				; ~ 3.0 = player got a memorial item (to explain a bit D-9341's background)
				; ~ 3.1 = player got a memorial item + injuries (because he didn't had any item in his inventory before)
				If PlayerRoom = e\room
					GrabbedEntity = 0
					
					e\EventState = 0.0
					
					Local Pick1162% = True
					Local pp% = CreatePivot(e\room\OBJ)
					
					PositionEntity(pp, 976.0, 128.0, -640.0)
					
					For it.Items = Each Items
						If (Not it\Picked) Then
							If EntityDistance(it\Collider, e\room\Objects[0]) < 0.75
								Pick1162 = False
							EndIf
						EndIf
					Next
					
					If EntityDistance(e\room\Objects[0], Collider) < 0.75 And Pick1162
						DrawHandIcon = True
						If MouseHit1 Then GrabbedEntity = e\room\Objects[0]
					EndIf
					
					If GrabbedEntity <> 0
						e\EventState2 = Rand(0, MaxItemAmount - 1)
						If Inventory(e\EventState2) <> Null
							; ~ Randomly picked item slot has an item in it, using this slot
							e\EventState3 = 1.0
						Else
							; ~ Randomly picked item slot is empty, getting the first available slot
							For i = 0 To MaxItemAmount - 1
								Local IsSlotEmpty% = (Inventory((i + e\EventState2) Mod MaxItemAmount) = Null)
								
								If (Not IsSlotEmpty) Then
									; ~ Successful
									e\EventState2 = (i + e\EventState2) Mod MaxItemAmount
								EndIf
								
								If Rand(8) = 1 Then
									If IsSlotEmpty Then
										e\EventState3 = 3.1
									Else
										e\EventState3 = 3.0
									EndIf
									
									e\EventState = Rand(1, 5)
									
									; ~ Checking if the selected nostalgia item already exists or not
									Local ItemName$ = ""
									
									Select e\EventState
										Case 1.0
											;[Block]
											ItemName = "Lost Key"
											;[End Block]
										Case 2.0
											;[Block]
											ItemName = "Disciplinary Hearing DH-S-4137-17092"
											;[End Block]
										Case 3.0
											;[Block]
											ItemName = "Coin"
											;[End Block]
										Case 4.0
											;[Block[
											ItemName = "Movie Ticket"
											;[End Block]
										Case 5.0
											;[Block]
											ItemName = "Old Badge"
											;[End Block]
									End Select
									
									Local ItemExists% = False
									
									For it.Items = Each Items
										If it\Name = ItemName Then
											ItemExists = True
											e\EventState3 = 1.0
											e\EventState = 0.0
											Exit
										EndIf
									Next
									
									If (Not ItemExists) And (Not IsSlotEmpty) Then Exit
								Else
									If IsSlotEmpty Then
										e\EventState3 = 2.0
									Else
										e\EventState3 = 1.0
										Exit
									EndIf
								EndIf
							Next
						EndIf
					EndIf
					
					; ~ Trade successful
					If e\EventState3 = 1.0
						Local ShouldCreateItem% = False
						
						For itt.ItemTemplates = Each ItemTemplates
							If (IsItemGoodFor1162(itt)) Then
								Select Inventory(e\EventState2)\ItemTemplate\TempName
									Case "key"
										;[Block]
										If itt\TempName = "key1" Or itt\TempName = "key2" And Rand(2) = 1
											ShouldCreateItem = True
										EndIf
										;[End Block]
									Case "paper", "oldpaper"
										;[Block]
										If itt\TempName = "paper" And Rand(12) = 1 Then
											ShouldCreateItem = True
										EndIf
										;[End Block]
									Case "gasmask", "gasmask3", "supergasmask", "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
										;[Block]
										If itt\TempName = "gasmask" Or itt\TempName = "gasmask3" Or itt\TempName = "supergasmask" Or itt\TempName = "hazmatsuit" Or itt\TempName = "hazmatsuit2" Or itt\TempName = "hazmatsuit3" And Rand(2) = 1
											ShouldCreateItem = True
										EndIf
										;[End Block]
									Case "key1", "key2", "key3"
										;[Block]
										If itt\TempName = "key1" Or itt\TempName = "key2" Or itt\TempName = "key3" Or itt\TempName = "misc" And Rand(6) = 1
											ShouldCreateItem = True
										EndIf
										;[End Block]
									Case "vest", "finevest"
										;[Block]
										If itt\TempName = "vest" Or itt\TempName = "finevest" And Rand(1) = 1
											ShouldCreateItem = True
										EndIf
										;[End Block]
									Default
										;[Block]
										If itt\TempName = "misc" And Rand(6) = 1
											ShouldCreateItem = True
										EndIf
										;[End Block]
								End Select
							EndIf
							
							If ShouldCreateItem Then
								RemoveItem(Inventory(e\EventState2))
								it = CreateItem(itt\Name, itt\TempName, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								EntityType(it\Collider, HIT_ITEM)
								PlaySound_Strict(LoadTempSound("SFX\SCP\1162\Exchange" + Rand(0, 4) + ".ogg"))
								e\EventState3 = 0.0
								
								GiveAchievement(Achv1162)
								MouseHit1 = False
								Exit
							EndIf
						Next
					; ~ Trade not sucessful (player got in return to injuries a new item)
					ElseIf e\EventState3 = 2.0
						Injuries = Injuries + 5.0
						Pvt = CreatePivot()
						PositionEntity(Pvt, EntityX(Collider), EntityY(Collider) - 0.05, EntityZ(Collider))
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						EntityPick(Pvt, 0.3)
						de.Decals = CreateDecal(3, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0)
						de\Size = 0.75
						ScaleSprite(de\OBJ, de\Size, de\Size)
						FreeEntity(Pvt)
						For itt.ItemTemplates = Each ItemTemplates
							If IsItemGoodFor1162(itt) And Rand(6) = 1
								it = CreateItem(itt\Name, itt\TempName, EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								EntityType(it\Collider, HIT_ITEM)
								GiveAchievement(Achv1162)
								MouseHit1 = False
								e\EventState3 = 0.0
								If Injuries > 15.0
									DeathMSG = "A dead Class D subject was discovered within the containment chamber of SCP-1162."
									DeathMSG = DeathMSG + " An autopsy revealed that his right lung was missing, which suggests"
									DeathMSG = DeathMSG + " interaction with SCP-1162."
									PlaySound_Strict(LoadTempSound("SFX\SCP\1162\BodyHorrorExchange" + Rand(1, 4) + ".ogg"))
									LightFlash = 5.0
									Kill()
								Else
									PlaySound_Strict(LoadTempSound("SFX\SCP\1162\BodyHorrorExchange" + Rand(1, 4) + ".ogg"))
									LightFlash = 5.0
									Msg = "You feel a sudden overwhelming pain in your chest."
									MsgTimer = 70 * 5.0
								EndIf
								Exit
							EndIf
						Next
					; ~ Trade with nostalgia item
					ElseIf e\EventState3 >= 3.0
						If e\EventState3 < 3.1
							PlaySound_Strict(LoadTempSound("SFX\SCP\1162\Exchange" + Rand(0, 4) + ".ogg"))
							RemoveItem(Inventory(e\EventState2))
						Else
							Injuries = Injuries + 5.0
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(Collider), EntityY(Collider) - 0.05, EntityZ(Collider))
							TurnEntity(Pvt, 90.0, 0.0, 0.0)
							EntityPick(Pvt, 0.3)
							de.Decals = CreateDecal(3, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, Rnd(360.0), 0.0)
							de\Size = 0.75 
							ScaleSprite(de\OBJ, de\Size, de\Size)
							FreeEntity(Pvt)
							If Injuries > 15.0
								DeathMSG = "A dead Class D subject was discovered within the containment chamber of SCP-1162."
								DeathMSG = DeathMSG + " An autopsy revealed that his right lung was missing, which suggests"
								DeathMSG = DeathMSG + " interaction with SCP-1162."
								PlaySound_Strict(LoadTempSound("SFX\SCP\1162\BodyHorrorExchange" + Rand(1, 4) + ".ogg"))
								LightFlash = 5.0
								Kill()
							Else
								PlaySound_Strict(LoadTempSound("SFX\SCP\1162\BodyHorrorExchange" + Rand(1, 4) + ".ogg"))
								LightFlash = 5.0
								Msg = "You notice something moving in your pockets and a sudden pain in your chest."
								MsgTimer = 70 * 5.0
							EndIf
							e\EventState2 = 0.0
						EndIf
						Select e\EventState
							Case 1.0
								;[Block]
								it = CreateItem("Lost Key", "key", EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								;[End Block]
							Case 2.0
								;[Block]
								it = CreateItem("Disciplinary Hearing DH-S-4137-17092", "oldpaper", EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								;[End Block]
							Case 3.0
								;[Block]
								it = CreateItem("Coin", "coin", EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								;[End Block]
							Case 4.0
								;[Block]
								it = CreateItem("Movie Ticket", "ticket", EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								;[End Block]
							Case 5.0
								;[Block]
								it = CreateItem("Old Badge", "badge", EntityX(pp, True), EntityY(pp, True), EntityZ(pp, True))
								;[End Block]
						End Select
						EntityType(it\Collider, HIT_ITEM)
						GiveAchievement(Achv1162)
						MouseHit1 = False
						e\EventState3 = 0.0
					EndIf
					FreeEntity(pp)
				EndIf
				;[End Block]
			Case "room2gw"
				;[Block]
				; ~ e\EventState: Determines if the airlock is in operation or not
				
				; ~ e\EventState2: The timer for the airlocks
				
				; ~ e\EventState3: Checks if the player had left the airlock or not
				
				e\room\RoomDoors[0]\Locked = True
				e\room\RoomDoors[1]\Locked = True
				
				Local BrokenDoor% = False
				
				If e\room\Objects[1] <> 0 Then BrokenDoor = True
				
				If PlayerRoom = e\room
					If e\EventState = 0.0
						If EntityDistance(e\room\Objects[0], Collider) < 1.4 And e\EventState3 = 0.0
							e\EventState = 1.0
							If BrokenDoor Then
								If e\Sound2 <> 0 Then 
									FreeSound_Strict(e\Sound2) : e\Sound2 = 0
								EndIf
								e\Sound2 = LoadSound_Strict("SFX\Door\DoorSparks.ogg")
								e\SoundCHN2 = PlaySound2(e\Sound2, Camera, e\room\Objects[1], 5.0)
							EndIf
							StopChannel(e\SoundCHN)
							e\SoundCHN = 0
							If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
							e\Sound = LoadSound_Strict("SFX\Door\Airlock.ogg")
							e\room\RoomDoors[0]\Locked = False
							e\room\RoomDoors[1]\Locked = False
							UseDoor(e\room\RoomDoors[0])
							UseDoor(e\room\RoomDoors[1])
							PlaySound_Strict(AlarmSFX(4))
						ElseIf EntityDistance(e\room\Objects[0], Collider) > 2.4
							e\EventState3 = 0.0
						EndIf
					Else
						If e\EventState2 < 70 * 7.0
							e\EventState2 = e\EventState2 + FPSfactor
							e\room\RoomDoors[0]\Open = False
							e\room\RoomDoors[1]\Open = False
							If e\EventState2 < 70 * 1.0
								
								If BrokenDoor Then
									Pvt = CreatePivot()
									
									Local d_Ent% = e\room\Objects[1]
									
									PositionEntity(Pvt, EntityX(d_Ent, True), EntityY(d_Ent, True) + Rnd(0.0, 0.05), EntityZ(d_Ent, True))
									RotateEntity(Pvt, 0.0, EntityYaw(d_Ent, True) + 90.0, 0.0)
									MoveEntity(Pvt, 0.0, 0.0, 0.2)
									
									If ParticleAmount > 0
										For i = 0 To (1 + (2 * (ParticleAmount - 1)))
											p.Particles = CreateParticle(EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 7, 0.002, 0.0, 25.0)
											p\Speed = Rnd(0.01,0.05) : p\Size = 0.0075 : p\Achange = -0.05
											RotateEntity(p\Pvt, Rnd(-45.0, 0.0), EntityYaw(Pvt) + Rnd(-10.0,10.0), 0)
											ScaleSprite(p\OBJ, p\Size, p\Size)
										Next
									EndIf
									FreeEntity(Pvt)
								EndIf
							ElseIf e\EventState2 > 70 * 3.0 And e\EventState < 70 * 5.5
								Pvt = CreatePivot(e\room\OBJ)								
								For i = 0 To 1
									If i = 0
										PositionEntity(Pvt, 312.0, 416.0, -128.0, False)
									Else
										PositionEntity(Pvt, 312.0, 416.0, 224.0, False)
									EndIf
									p.Particles = CreateParticle(EntityX(Pvt, True), EntityY(Pvt, True), EntityZ(Pvt, True), 6, 0.8, 0.0, 50.0)
									p\Speed = 0.025 : p\Achange = -0.02
									RotateEntity(p\Pvt, 90.0, 0.0, 0.0)
								Next
								FreeEntity(Pvt)
								If e\SoundCHN = 0 Then e\SoundCHN = PlaySound2(e\Sound, Camera, e\room\Objects[0], 5.0)
							EndIf
						Else
							e\EventState = 0.0
							e\EventState2 = 0.0
							e\EventState3 = 1.0
							If e\room\RoomDoors[0]\Open = False
								e\room\RoomDoors[0]\Locked = False
								e\room\RoomDoors[1]\Locked = False
								UseDoor(e\room\RoomDoors[0])
								UseDoor(e\room\RoomDoors[1])
							EndIf
						EndIf
					EndIf
					
					If BrokenDoor Then
						If ChannelPlaying(e\SoundCHN2)
							UpdateSoundOrigin(e\SoundCHN2, Camera, e\room\Objects[1], 5.0)
						EndIf
					EndIf
					If ChannelPlaying(e\SoundCHN)
						UpdateSoundOrigin(e\SoundCHN, Camera, e\room\Objects[0], 5.0)
					EndIf
				Else
					e\EventState3 = 0.0
				EndIf
				;[End Block]
			Case "room2sl"
				;[Block]
				; ~ e\EventState: Determines if the player already entered the room or not (0 = No, 1 = Yes)
				
				; ~ e\EventState2: Variable used for the SCP-049 event
				
				; ~ e\EventState3: Checks if Lever is activated or not
				
				If PlayerRoom = e\room
					If e\EventStr = "" And QuickLoadPercent = -1
						QuickLoadPercent = 0
						QuickLoad_CurrEvent = e
						e\EventStr = 0
					EndIf
				EndIf
				
				If e\EventState = 1.0
					If e\EventState2 < 0.0
						If e\EventState2 = (-70) * 5.0
							For sc.SecurityCams = Each SecurityCams
								If sc\room = e\room
									If EntityDistance(sc\ScrOBJ, Camera) < 5.0
										If EntityVisible(sc\ScrOBJ, Camera)
											e\EventState2 = Min(e\EventState2 + FPSfactor, 0.0)
											Exit
										EndIf
									EndIf
								EndIf
							Next
						Else
							e\EventState2 = Min(e\EventState2 + FPSfactor, 0.0)
						EndIf
					ElseIf e\EventState2 = 0.0
						If e\room\NPC[0] <> Null
							Local AdjDist1# = 0.0
							Local AdjDist2# = 0.0
							Local Adj1% = -1
							Local Adj2% = -1
							
							For i = 0 To 3
								If e\room\AdjDoor[i] <> Null
									If Adj1 = -1
										AdjDist1 = EntityDistance(e\room\Objects[7], e\room\AdjDoor[i]\FrameOBJ)
										Adj1 = i
									Else
										AdjDist2 = EntityDistance(e\room\Objects[7], e\room\AdjDoor[i]\FrameOBJ)
										Adj2 = i
									EndIf
								EndIf
							Next
							If AdjDist1 > AdjDist2
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\AdjDoor[Adj1]\FrameOBJ), EntityY(e\room\Objects[7], True), EntityZ(e\room\AdjDoor[Adj1]\FrameOBJ))
							Else
								PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\AdjDoor[Adj2]\FrameOBJ), EntityY(e\room\Objects[7], True), EntityZ(e\room\AdjDoor[Adj2]\FrameOBJ))
							EndIf
							PointEntity(e\room\NPC[0]\Collider, e\room\OBJ)
							MoveEntity(e\room\NPC[0]\Collider, 0.0, 0.0, -1.0)
							ResetEntity(e\room\NPC[0]\Collider)
							e\room\NPC[0]\HideFromNVG = False
							e\room\NPC[0]\PathX = EntityX(e\room\NPC[0]\Collider)
							e\room\NPC[0]\PathZ = EntityZ(e\room\NPC[0]\Collider)
							e\room\NPC[0]\State = 5.0
							
							e\EventState2 = 1.0
						EndIf
					ElseIf e\EventState2 = 1.0
						If e\room\NPC[0]\PathStatus <> 1
							e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[15], True), EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True))
						Else
							e\EventState2 = 2.0
						EndIf
					ElseIf e\EventState2 = 2.0
						If e\room\NPC[0]\PathStatus <> 1
							e\room\NPC[0]\State3 = 1.0
							e\EventState2 = 3.0
							e\room\NPC[0]\PathTimer = 0.0
						Else
							If EntityDistance(e\room\NPC[0]\Collider, e\room\RoomDoors[0]\FrameOBJ) < 5.0
								e\room\RoomDoors[0]\Locked = True
								e\room\RoomDoors[1]\Locked = True
								If e\room\NPC[0]\Reload = 0.0
									If (Not e\room\RoomDoors[0]\Open) Then PlaySound_Strict LoadTempSound("SFX\Door\DoorOpen079.ogg")
									e\room\NPC[0]\Reload = 1.0
								EndIf
								If (Not e\room\RoomDoors[0]\Open)
									e\room\RoomDoors[0]\Open = True
									Sound = Rand(0, 2)
									PlaySound2(OpenDoorSFX(0, Sound), Camera, e\room\RoomDoors[0]\OBJ)
								EndIf
								If (Not e\room\RoomDoors[1]\Open)
									e\room\RoomDoors[1]\Open = True
									Sound = Rand(0, 2)
									PlaySound2(OpenDoorSFX(0, Sound), Camera, e\room\RoomDoors[1]\OBJ)
								EndIf
							EndIf
							If e\room\NPC[0]\Reload = 1.0
								e\room\NPC[0]\DropSpeed = 0.0
							EndIf
						EndIf
						
						If e\room\NPC[0]\State <> 5.0
							e\EventState2 = 7.0
						EndIf
					ElseIf e\EventState2 = 3.0
						If e\room\NPC[0]\State <> 5.0
							e\EventState2 = 7.0
						EndIf
						
						If MeNPCSeesPlayer(e\room\NPC[0], True) = 2
							e\EventState2 = 4.0
						EndIf
						
						If e\room\NPC[0]\PathStatus <> 1
							If e\room\NPC[0]\PathTimer = 0.0
								If e\room\NPC[0]\PrevState = 1 Then
									If e\room\NPC[0]\SoundCHN2 = 0 Then
										e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\SCP\049\Room2SL1.ogg")
										e\room\NPC[0]\SoundCHN2 = PlaySound2(e\room\NPC[0]\Sound2, Camera, e\room\NPC[0]\Collider)
									Else
										If ChannelPlaying(e\room\NPC[0]\SoundCHN2) = False
											e\room\NPC[0]\PathTimer = 1.0
											e\room\NPC[0]\SoundCHN2 = 0
										EndIf
									EndIf
								ElseIf e\room\NPC[0]\PrevState = 2
									If e\room\NPC[0]\State3 = 3.0 Then
										If (e\room\NPC[0]\SoundCHN2 = 0) Then
											e\room\NPC[0]\Sound2 = LoadSound_Strict("SFX\SCP\049\Room2SL2.ogg")
											e\room\NPC[0]\SoundCHN2 = PlaySound2(e\room\NPC[0]\Sound2, Camera, e\room\NPC[0]\Collider)
										Else
											If ChannelPlaying(e\room\NPC[0]\SoundCHN2) = False
												e\room\NPC[0]\PathTimer = 1.0
												e\room\NPC[0]\SoundCHN2 = 0
											EndIf
										EndIf
									Else
										If e\room\NPC[0]\Frame >= 1118.0
											e\room\NPC[0]\PathTimer = 1.0
										EndIf
									EndIf
								EndIf
							Else
								Select e\room\NPC[0]\State3
									Case 1.0
										;[Block]
										e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[16], True), EntityY(e\room\Objects[16], True), EntityZ(e\room\Objects[16], True))
										e\room\NPC[0]\PrevState = 1
										;[End Block]
									Case 2.0
										;[Block]
										e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[15], True), EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True))
										e\room\NPC[0]\PrevState = 2
										;[End Block]
									Case 3.0
										;[Block]
										e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(e\room\Objects[17], True), EntityY(e\room\Objects[17], True), EntityZ(e\room\Objects[17], True))
										e\room\NPC[0]\PrevState = 2
										;[End Block]
									Case 4.0
										;[Block]
										e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], e\room\NPC[0]\PathX, 0.1, e\room\NPC[0]\PathZ)
										e\room\NPC[0]\PrevState = 2
										;[End Block]
									Case 5.0
										e\EventState2 = 5.0
								End Select
								e\room\NPC[0]\PathTimer = 0.0
								e\room\NPC[0]\State3 = e\room\NPC[0]\State3 + 1.0
							EndIf
						EndIf
					ElseIf e\EventState2 = 4.0
						If e\room\NPC[0]\State <> 5.0
							e\EventState2 = 7.0
							e\room\NPC[0]\State3 = 6.0
						EndIf
					ElseIf e\EventState2 = 5.0
						e\room\NPC[0]\State = 2.0
						For r.Rooms = Each Rooms
							If r <> PlayerRoom
								If (EntityDistance(r\OBJ, e\room\NPC[0]\Collider) < HideDistance * 2 And EntityDistance(r\OBJ, e\room\NPC[0]\Collider) > HideDistance)
									e\room\NPC[0]\PathStatus = FindPath(e\room\NPC[0], EntityX(r\OBJ), EntityY(r\OBJ), EntityZ(r\OBJ))
									e\room\NPC[0]\PathTimer = 0.0
									If e\room\NPC[0]\PathStatus = 1 Then e\EventState2 = 6.0
									Exit
								EndIf
							EndIf
						Next
					ElseIf e\EventState2 = 6.0
						If MeNPCSeesPlayer(e\room\NPC[0], True) Or e\room\NPC[0]\State2 > 0.0 Or e\room\NPC[0]\LastSeen > 0.0 Then
							e\EventState2 = 7.0
						Else
							; ~ Still playing the Music for SCP-049 (in the real, SCP-049's State will be set to 2, causing it to stop playing the chasing track)
							If PlayerRoom = e\room Then
								ShouldPlay = 20
							EndIf
							If e\room\NPC[0]\PathStatus <> 1
								e\room\NPC[0]\Idle = 70 * 60.0 ; ~ Making SCP-049 idle for one minute (twice as fast for AggressiveNPCs = True)
								PositionEntity(e\room\NPC[0]\Collider, 0.0, 500.0, 0.0)
								ResetEntity(e\room\NPC[0]\Collider)
								
								e\EventState2 = 7.0
							EndIf
						EndIf
					EndIf
					
					If e\room\NPC[0] <> Null
						If e\EventState2 < 7.0
							If e\EventState2 > 2.0
								If Abs(EntityY(e\room\RoomDoors[0]\FrameOBJ) - EntityY(e\room\NPC[0]\Collider)) > 1.0
									If Abs(EntityY(e\room\RoomDoors[0]\FrameOBJ) - EntityY(Collider)) < 1.0
										If e\room\RoomDoors[0]\Open
											e\room\RoomDoors[0]\Open = False
											e\room\RoomDoors[0]\FastOpen = 1
											PlaySound_Strict(LoadTempSound("SFX\Door\DoorClose079.ogg"))
										EndIf
									EndIf
								Else
									If e\room\RoomDoors[0]\Open = False
										e\room\RoomDoors[0]\FastOpen = 0
										e\room\RoomDoors[0]\Open = True
										Sound = Rand(0, 2)
										PlaySound2(OpenDoorSFX(0, Sound), Camera, e\room\RoomDoors[0]\OBJ)
										PlaySound_Strict(LoadTempSound("SFX\Door\DoorOpen079.ogg"))
									EndIf
								EndIf
							EndIf
						Else
							If e\room\RoomDoors[0]\Open = False
								e\room\RoomDoors[0]\FastOpen = 0
								e\room\RoomDoors[0]\Open = True
								Sound = Rand(0, 2)
								PlaySound2(OpenDoorSFX(0, Sound), Camera, e\room\RoomDoors[0]\OBJ)
								PlaySound_Strict(LoadTempSound("SFX\Door\DoorOpen079.ogg"))
							EndIf
						EndIf
					EndIf
				EndIf
				
				If PlayerRoom = e\room Then
					; ~ Lever for checkpoint locking (might have a function in the future for the case if the checkpoint needs to be locked again)
					e\EventState3 = UpdateLever(e\room\Levers[0])
					If e\EventState3 = 1.0 Then
						UpdateCheckpointMonitors(0)
						If MonitorTimer < 50.0 Then
							EntityTexture(e\room\Objects[20], e\room\Textures[0], 1)
						Else
							EntityTexture(e\room\Objects[20], e\room\Textures[0], 2)
						EndIf
					Else
						TurnCheckpointMonitorsOff(0)
						EntityTexture(e\room\Objects[20], e\room\Textures[0], 0)
					EndIf
					
					; ~ Checking if the monitors and such should be rendered or not
					If Abs(EntityY(e\room\RoomDoors[0]\FrameOBJ) - EntityY(Collider)) > 1.0 Then
						For i = 0 To 14
							If e\room\Objects[i] <> 0 And i <> 7 Then
								ShowEntity(e\room\Objects[i])
							EndIf
						Next
						For sc.SecurityCams = Each SecurityCams
							If sc\room = e\room Then
								If sc\ScrObj <> 0
									ShowEntity(sc\ScrOBJ)
								EndIf
								If sc\ScrOverlay <> 0
									ShowEntity(sc\ScrOverlay)
								EndIf
								Exit
							EndIf
						Next
						For i = 0 To 3
							If PlayerRoom\Adjacent[i] <> Null Then
								EntityAlpha(GetChild(PlayerRoom\Adjacent[i]\OBJ, 2), 0)
							EndIf
						Next
					Else
						For i = 0 To 14
							If e\room\Objects[i] <> 0 And i <> 7 Then
								HideEntity(e\room\Objects[i])
							EndIf
						Next
						For sc.SecurityCams = Each SecurityCams
							If sc\room = e\room Then
								If sc\ScrObj <> 0
									HideEntity(sc\ScrOBJ)
								EndIf
								If sc\ScrOverlay <> 0
									HideEntity(sc\ScrOverlay)
								EndIf
								Exit
							EndIf
						Next
					EndIf
				EndIf
				
				For e2.Events = Each Events
					If e2\EventName = "room008"
						If e2\EventState = 2.0
							EntityTexture(e\room\Objects[21], e\room\Textures[0], 3)
						Else
							EntityTexture(e\room\Objects[21], e\room\Textures[1], 6)
						EndIf
					EndIf
				Next
				;[End Block]
			Case "096spawn"
				;[Block]
				Local xSpawn#, zSpawn#, Place%
				
				If e\room\Dist < HideDistance
					; ~ Checking some statements in order to determine if SCP-096 can spawn in this room
					If e\EventState <> 2.0
						If Curr096 <> Null
							If EntityDistance(Curr096\Collider, Collider) < 40.0 Then
								e\EventState = 2.0
							EndIf
							
							For e2.Events = Each Events
								If e2\EventName = "room2servers"
									If e2\EventState > 0.0 And e2\room\NPC[0] <> Null
										e\EventState = 2.0
										Exit
									EndIf
								EndIf
							Next
							
							For r.Rooms = Each Rooms
   								If r\RoomTemplate\Name = "checkpoint1"
									If r\Dist < 10.0
										e\EventState = 2.0
										Exit
									EndIf
								EndIf
							Next
							
							If Curr096\State <> 5.0
								e\EventState = 2.0
							EndIf
							
							If EntityDistance(Curr096\Collider, e\room\OBJ) > EntityDistance(Curr096\Collider, Collider)
								e\EventState = 2.0
							EndIf
						EndIf
						
						For e2.Events = Each Events
							If e2\EventName = "room2servers"
								If e2\EventState = 0.0
									e\EventState = 2.0
									Exit
								EndIf
							EndIf
						Next
						If PlayerRoom = e\room Then e\EventState = 2.0
					EndIf
					
					If e\EventState = 0.0
						Select e\room\RoomTemplate\Name
							Case "room4pit", "room3pit", "room3z2", "room4tunnels", "room3tunnel"
								;[Block]
								If e\room\RoomTemplate\Name = "room4pit" Or e\room\RoomTemplate\Name = "room4tunnels"
									Place = Rand(0, 3)
								Else
									Place = Rand(0, 2)
								EndIf
								
								If Place = 0
									xSpawn = -608.0
									zSpawn = 0.0
								ElseIf Place = 1
									xSpawn = 0.0
									zSpawn = -608.0
								ElseIf Place = 2
									xSpawn = 608.0
									zSpawn = 0.0
								Else
									xSpawn = 0.0
									zSpawn = 608.0
								EndIf
								;[End Block]
							Default
								;[Block]
								xSpawn = Rnd(-100.0, 100.0)
								zSpawn = Rnd(-100.0, 100.0)
								;[End Block]
						End Select
						Pvt = CreatePivot(e\room\OBJ)
						PositionEntity(Pvt, xSpawn, 0.0, zSpawn)
						If Curr096 = Null
							Curr096 = CreateNPC(NPCtype096, EntityX(Pvt, True), e\room\y + 0.5, EntityZ(Pvt, True))
						Else
							PositionEntity(Curr096\Collider, EntityX(Pvt, True), e\room\y + 0.5, EntityZ(Pvt, True))
							ResetEntity(Curr096\Collider)
						EndIf
						PointEntity(Curr096\Collider, Collider)
						RotateEntity(Curr096\Collider, 0.0, EntityYaw(Curr096\Collider) + 180.0, 0.0)
						FreeEntity(Pvt)
						Curr096\State = 5.0
						
						e\EventState = 1.0
					ElseIf e\EventState = 1.0
						PointEntity(Curr096\Collider, Collider)
						RotateEntity(Curr096\Collider, 0.0, EntityYaw(Curr096\Collider) + 180.0, 0.0)
						
						If EntityDistance(Curr096\Collider, Collider) < HideDistance * 0.5
							If EntityVisible(Curr096\Collider, Camera)
								PointEntity(Curr096\Collider, Collider)
								RotateEntity(Curr096\Collider, 0.0, EntityYaw(Curr096\Collider) + Rnd(170.0, 190.0), 0.0)
								e\EventState = 2.0
							EndIf
						EndIf
					ElseIf e\EventState = 3.0
						e\EventState = 2.0
					EndIf
				Else
					If e\EventState = 2.0
						If Rand(-1, 1 + (2 * SelectedDifficulty\AggressiveNPCs)) > 0 Then
							e\EventState = 0.0
						Else
							e\EventState = 3.0
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "room2medibay2"
				;[Block]
				; ~ e\EventState: Determines if the player has entered the room or not
				; ~ 0 : Not entered
				; ~ 1 : Has entered
				
				; ~ Hiding / Showing the props in this room
				If PlayerRoom <> e\room Then
					HideEntity(e\room\Objects[0])
				Else
					ShowEntity(e\room\Objects[0])
					If e\EventState = 0.0 Then
						e\room\NPC[0] = CreateNPC(NPCtype008_1, EntityX(e\room\Objects[3], True), 0.5, EntityZ(e\room\Objects[3], True))
						RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle - 90.0, 0.0)
						e\EventState = 1.0
					EndIf
					
					If EntityDistance(e\room\NPC[0]\Collider, Collider) < 1.2 Then
						If e\EventState2 = 0.0 Then
							LightBlink = 12.0
							PlaySound_Strict(LightSFX)
							e\room\NPC[0]\State = 1.0
							e\EventState2 = 1.0
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "dimension1499"
				;[Block]
				If PlayerRoom <> e\room Then
					If e\room\Objects[0] <> 0 Then
						For i = 1 To 15
							HideEntity(e\room\Objects[i])
						Next
					EndIf
					If EntityY(Collider) > EntityY(e\room\OBJ) - 0.5 Then
						PlayerRoom = e\room
					EndIf
				EndIf
				If e\EventState = 2.0 Then
					If e\SoundCHN <> 0 Then
						StopStream_Strict(e\SoundCHN)
						StopChannel(e\SoundCHN2)
						e\SoundCHN = 0
						e\SoundCHN2 = 0
					EndIf
					HideEntity(NTF_1499Sky)
					HideChunks()
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype1499_1 Then
							RemoveNPC(n)
						EndIf
					Next
					For du.Dummy1499_1 = Each Dummy1499_1
						FreeEntity(du\OBJ)
						Delete(du)
					Next
					If e\EventState3 < 70 * 30.0 Then
						e\EventState3 = 0.0
					EndIf
					e\EventState = 1.0
					If e\Sound2 <> 0 Then
						FreeSound_Strict(e\Sound2) : e\Sound2 = 0
					EndIf
				EndIf
				;[End Block]
			Case "room2offices035"
				;[Block]
				Local Is035Released% = False
				
				For e2.Events = Each Events
					If e2 <> e And e2\EventName = "room035" Then
						If e2\EventState < 0.0 Then
							Is035Released = True
							Exit
						EndIf
					EndIf
				Next
				
				If Is035Released Then
					If e\room\Dist < 8.0 Then
						If e\room\NPC[0] = Null Then
							e\room\NPC[0] = CreateNPC(NPCtypeD, e\room\x, 0.5, e\room\z)
							RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0)
							MoveEntity(e\room\NPC[0]\Collider, 0.0, 0.0, -0.5)
							e\room\NPC[0]\State = 3.0
							ChangeNPCTextureID(e\room\NPC[0], 7)
							SetNPCFrame(e\room\NPC[0], 19.0)
						EndIf
						If e\room\NPC[1] = Null Then
							If EntityDistance(e\room\NPC[0]\Collider, Collider) < 2.5 Then
								e\room\NPC[1] = CreateNPC(NPCtype035_Tentacle, EntityX(e\room\NPC[0]\Collider), 0.13, EntityZ(e\room\NPC[0]\Collider))
								RotateEntity(e\room\NPC[1]\Collider, 0.0, e\room\Angle, 0.0)
								MoveEntity(e\room\NPC[1]\Collider, 0.0, 0.0, 0.6)
							EndIf
						EndIf
					Else
						If e\room\Dist > HideDistance
							If e\room\NPC[1] <> Null
								RemoveNPC(e\room\NPC[1])
								e\room\NPC[1] = Null
							EndIf
						EndIf
					EndIf
				EndIf
				
				If e\room\NPC[1] <> Null Then
					If e\room\NPC[1]\IsDead = True Then RemoveEvent(e)
				EndIf
				;[End Block]
			Case "room2shaft"
                ;[Block]
                If e\EventState = 0.0 Then
                    e\room\NPC[0] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True) + 0.5, EntityZ(e\room\Objects[1], True))
                    RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 180.0, 0.0, True)
					SetNPCFrame(e\room\NPC[0], 286.0)
                    e\room\NPC[0]\State = 8.0
                    
                    e\EventState = 1.0
                EndIf
				
				If PlayerRoom = e\room Then
					UpdateButton(e\room\Objects[2])
					If ClosestButton = e\room\Objects[2] And MouseHit1 Then
						Msg = "The elevator appears to be broken."
						PlaySound2(ButtonSFX2, Camera, e\room\Objects[2])
						MsgTimer = 70 * 5.0
						MouseHit1 = 0
					EndIf
				EndIf
                ;[End Block]
			Case "room1lifts"
				;[Block]
				If PlayerRoom = e\room Then
					For i = 0 To 1
						UpdateButton(e\room\Objects[i])
						If ClosestButton = e\room\Objects[i] And MouseHit1 Then
							Msg = "The elevator appears to be broken."
							PlaySound2(ButtonSFX2, Camera, e\room\Objects[i])
							MsgTimer = 70 * 5.0
							MouseHit1 = 0
						EndIf
					Next
				EndIf
				;[End Block]
			Case "room4info"
				;[Block]
				If e\EventState = 0.0 Then
					e\room\NPC[0] = CreateNPC(NPCtypeClerk, EntityX(e\room\Objects[1], True), EntityY(e\room\Objects[1], True), EntityZ(e\room\Objects[1], True))
					RotateEntity(e\room\NPC[0]\Collider, 0.0, e\room\Angle + 270.0, 0.0)
					e\room\NPC[0]\State = 3.0
					SetNPCFrame(e\room\NPC[0], 40.0)
					e\room\NPC[0]\IsDead = True
					Tex = LoadTexture_Strict("GFX\npcs\body_c.png")
					EntityTexture(e\room\NPC[0]\OBJ, Tex)
					FreeTexture(Tex)
					e\EventState = 1.0
				Else
					e\SoundCHN = LoopSound2(ScientistRadioSFX(Rand(0, 1)), e\SoundCHN, Camera, e\room\Objects[2], 4.0, 0.5)
			    EndIf
				
				For e2.Events = Each Events
					If e2\EventName = "room2sl" Then
						If e2\EventState3 = 0.0 Then
							If e\room\Dist < 12.0 Then
								TurnCheckpointMonitorsOff(0)
							EndIf
						Else
							If e\room\Dist < 12.0 Then
								UpdateCheckpointMonitors(0)
							EndIf
						EndIf
					EndIf
				Next
				;[End Block]
			Case "room2bio"
				;[Block]
				If e\room\Dist < HideDistance Then
					ShowEntity(e\room\Objects[0])
				Else
					HideEntity(e\room\Objects[0])
				EndIf
				;[End Block]
		End Select
		
		If e <> Null Then
			CatchErrors(Chr(34) + e\EventName + Chr(34) + " event")
		Else
			CatchErrors("Deleted event")
		EndIf
	Next
	
	; ~ This here is necessary because the SCP-294's drinks with explosion effect didn't worked anymore -- ENDSHN
	If ExplosionTimer > 0.0 Then
		ExplosionTimer = ExplosionTimer + FPSfactor
		If ExplosionTimer < 140.0 Then
			If ExplosionTimer - FPSfactor < 5.0 Then
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke1.ogg")
				PlaySound_Strict(ExplosionSFX)
				CameraShake = 10.0
				ExplosionTimer = 5.0
			EndIf
			CameraShake = CurveValue(ExplosionTimer / 60.0, CameraShake, 50.0)
		Else
			CameraShake = Min((ExplosionTimer / 20.0), 20.0)
			If ExplosionTimer - FPSfactor < 140.0 Then
				BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke2.ogg")
				PlaySound_Strict(ExplosionSFX)				
				For i = 0 To (10 + (10 * (ParticleAmount + 1)))
					p.Particles = CreateParticle(EntityX(Collider) + Rnd(-0.5, 0.5), EntityY(Collider) - Rnd(0.2, 1.5), EntityZ(Collider) + Rnd(-0.5, 0.5), 0, Rnd(0.2, 0.6), 0.0, 350.0)	
					RotateEntity(p\Pvt, -90.0, 0.0, 0.0, True)
					p\Speed = Rnd(0.05, 0.07)
				Next
			EndIf
			LightFlash = Min((ExplosionTimer - 140.0) / 10.0, 5.0)
			
			If ExplosionTimer > 160.0 Then KillTimer = Min(KillTimer, -0.1)
			If ExplosionTimer > 500.0 Then ExplosionTimer = 0.0
			
			; ~ A dirty workaround to prevent the collider from falling down into the facility once the nuke goes off, causing the UpdateEvents() function to be called again and crashing the game
			PositionEntity(Collider, EntityX(Collider), 200.0, EntityZ(Collider))
		EndIf
	EndIf
	
End Function

Function UpdateDimension1499()
	Local e.Events,n.NPCs, n2.NPCs, r.Rooms, it.Items, i%, j%, du.Dummy1499_1, Temp%, Scale#, x%, y%
	
	For e.Events = Each Events
		If e\EventName = "dimension1499"
			; ~ e\EventState: If player entered dimension (will be resetted after the player leaves it)
			; ~ 0: The player never entered SCP-1499
			; ~ 1: The player had already entered the dimension at least once
			; ~ 2: The player is in dimension
			
			; ~ e\EventState2: Used to count the amount of times the player has entered the SCP-1499's dimension (for a little spawning event)
			
			; ~ e\EventState3: Variable used for the SCP-1499's church event
			
			If PlayerRoom = e\room Then
				If e\EventState < 2.0
					; ~ SCP-1499's random generator
					If e\EventState = 0.0
						If e\EventStr = "" And QuickLoadPercent = -1
							QuickLoadPercent = 0
							QuickLoad_CurrEvent = e
							e\EventStr = "Load0"
						EndIf
					Else
						e\EventState = 2.0
					EndIf
					
					Local Value% = Rand(2, 3)
					
					If e\EventState2 = Value Or e\EventState2 = 4.0 Then
						For i = -1 To 1
							For j = -1 To 1
								If i <> 0 And j <> 0 Then
									n.NPCs = CreateNPC(NPCtype1499_1, EntityX(Collider) + (0.75 * i), EntityY(Collider) + 0.05, EntityZ(Collider) + (0.75 * j))
									PointEntity(n\Collider, Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
									n\State = 2.0
								ElseIf i <> 0 Or j <> 0 Then
									n.NPCs = CreateNPC(NPCtype1499_1, EntityX(Collider) + i, EntityY(Collider) + 0.05, EntityZ(Collider) + j)
									PointEntity(n\Collider, Collider)
									RotateEntity(n\Collider, 0.0, EntityYaw(n\Collider), 0.0)
									n\State = 2.0
								EndIf
							Next
						Next
						e\EventState2 = 5.0
					EndIf
					If e\EventState3 < 70 * 30.0 Then
						; ~ Guards at the entrance to church
						n.NPCs = CreateNPC(NPCtype1499_1, e\room\x + 4055.0 * RoomScale, e\room\y + 240.0 * RoomScale, e\room\z + 1884.0 * RoomScale)
						n\PrevState = 3
						n\Angle = 270.0
						RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
						n2.NPCs = CreateNPC(NPCtype1499_1, e\room\x + 4055.0 * RoomScale, e\room\y + 240.0 * RoomScale, e\room\z + 2876.0 * RoomScale)
						n2\PrevState = 3
						n2\Angle = 270.0
						RotateEntity(n2\Collider, 0.0, n2\Angle, 0.0)
						n\Target = n2
						n2\Target = n
						e\room\NPC[2] = n
						e\room\NPC[3] = n2
						; ~ More guards
						n.NPCs = CreateNPC(NPCtype1499_1, e\room\x - 1877.0 * RoomScale, e\room\y + 192.0 * RoomScale, e\room\z + 1071.0 * RoomScale)
						n\PrevState = 3
						n\Angle = 270.0
						RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
						n2.NPCs = CreateNPC(NPCtype1499_1, e\room\x - 1877.0 * RoomScale, e\room\y + 192.0 * RoomScale, e\room\z + 3503.0 * RoomScale)
						n2\PrevState = 3
						n2\Angle = 270.0
						RotateEntity(n2\Collider, 0.0, n2\Angle, 0.0)
						n\Target = n2
						n2\Target = n
						e\room\NPC[4] = n
						e\room\NPC[5] = n2
						; ~ Guard at stairs
						n.NPCs = CreateNPC(NPCtype1499_1, e\room\x - 2761.0 * RoomScale, e\room\y + 240.0 * RoomScale, e\room\z + 3204.0 * RoomScale)
						n\PrevState = 1
						n\Angle = 180.0
						RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
						n\Speed = 0.0
						; ~ King
						n.NPCs = CreateNPC(NPCtype1499_1, e\room\x - 1917.0 * RoomScale, e\room\y + 1904.0 * RoomScale, e\room\z + 2308.0 * RoomScale)
						n\PrevState = 2
						n\Angle = 270.0
						RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
						Tex = LoadTexture_Strict("GFX\npcs\scp_1499_1_king.png")
						EntityTexture(n\OBJ, Tex)
						FreeTexture(Tex)
						e\room\NPC[0] = n
						; ~ Guard next to king
						n.NPCs = CreateNPC(NPCtype1499_1, e\room\x - 1917.0 * RoomScale, e\room\y + 1904.0 * RoomScale, e\room\z + 2052.0 * RoomScale)
						n\PrevState = 1
						n\Angle = 270.0
						RotateEntity(n\Collider, 0.0, n\Angle, 0.0)
						e\room\NPC[1] = n
						; ~ SCP-1499-1 instances praying in church
						; ~ Zone 1
						For x = 0 To 7
							For y = 0 To 2
								du = New Dummy1499_1
								For n.NPCs = Each NPCs
									If n\NPCtype = NPCtype1499_1 And n\PrevState <> 2 Then
										du\OBJ = CopyEntity(n\OBJ)
										Exit
									EndIf
								Next
								Scale = (GetINIFloat("Data\NPCs.ini", "SCP-1499-1", "scale") / 4.0) * Rnd(0.8, 1.0)
								ScaleEntity(du\OBJ, Scale, Scale, Scale)
								EntityFX(du\OBJ, 1)
								du\Anim = Rand(0, 1)
								PositionEntity(du\OBJ, Max(Min((e\room\x + (1887.0 - ((2560.0 / 7.0) * x)) * RoomScale) + Rnd(-0.5, 0.5), e\room\x + 1887.0 * RoomScale), e\room\x - 873.0 * RoomScale), e\room\y, Max(Min((e\room\z + (1796.0 - (384.0 * y)) * RoomScale) + Rnd(-0.5, 0.5), e\room\z + 1796.0 * RoomScale), e\room\z + 1028.0 * RoomScale))
								RotateEntity(du\OBJ, 0.0, 270.0, 0.0)
								EntityAutoFade(du\OBJ, 25.0, 39.0)
							Next
						Next
						; ~ Zone 2
						For x = 0 To 6
							For y = 0 To 2
								du = New Dummy1499_1
								For n.NPCs = Each NPCs
									If n\NPCtype = NPCtype1499_1 And n\PrevState <> 2 Then
										du\OBJ = CopyEntity(n\OBJ)
										Exit
									EndIf
								Next
								Scale = (GetINIFloat("Data\NPCs.ini", "SCP-1499-1", "scale") / 4.0) * Rnd(0.8, 1.0)
								ScaleEntity(du\OBJ, Scale, Scale, Scale)
								EntityFX(du\OBJ, 1)
								du\Anim = Rand(0, 1)
								PositionEntity(du\OBJ, Max(Min((e\room\x + (1375.0 - ((2048.0 / 6.0) * x)) * RoomScale) + Rnd(-0.5, 0.5), e\room\x + 1375.0 * RoomScale), e\room\x - 873.0 * RoomScale), e\room\y, Max(Min((e\room\z + (3588.0 - (384.0 * y)) * RoomScale) + Rnd(-0.5, 0.5), e\room\z + 3588.0 * RoomScale), e\room\z + 2820.0 * RoomScale))
								RotateEntity(du\OBJ, 0.0, 270.0, 0.0)
								EntityAutoFade(du\OBJ, 25.0, 39.0)
							Next
						Next
					Else
						HideEntity(e\room\Levers[1])
					EndIf
					
					For i = 0 To 14
						n.NPCs = CreateNPC(NPCtype1499_1, EntityX(Collider) + Rnd(-20.0, 20.0), EntityY(Collider) + 0.1, EntityZ(Collider) + Rnd(-20.0, 20.0))
						If Rand(2) = 1 Then n\State2 = 1500.0
						n\Angle = Rnd(360.0)
						n\State2 = 0.0
						If EntityDistance(n\Collider, Collider) < 10.0 Then
							n\State = 2.0
						EndIf
					Next
				EndIf
				
				If (Not DebugHUD) Then
					CameraFogRange(Camera, 40.0, 80.0)
					CameraFogColor(Camera, 96.0, 97.0, 104.0)
					CameraClsColor(Camera, 96.0, 97.0, 104.0)
					CameraRange(Camera, 0.05, 90.0)
				Else
					CameraFogRange(Camera, 120.0, 120.0)
					CameraFogColor(Camera, 96.0, 97.0, 104.0)
					CameraClsColor(Camera, 96.0, 97.0 ,104.0)
					CameraRange(Camera, 0.05, 120.0)
				EndIf
				
				For r.Rooms = Each Rooms
					HideEntity(r\OBJ)
				Next
				ShowEntity(e\room\OBJ)
				If QuickLoadPercent = 100 Or QuickLoadPercent = -1
					UpdateChunks(e\room, 15)
					ShowEntity(NTF_1499Sky)
					Update1499Sky()
					ShouldPlay = 18
					If EntityY(Collider) < 800.0
						PositionEntity(Collider, EntityX(Collider), 800.5, EntityZ(Collider), True)
						ResetEntity(Collider)
					EndIf
					; ~ A hacky fix to make items not fall that are in SCP-1499's dimension
					For it.Items = Each Items
						If EntityY(it\Collider) > 750.0
							If EntityY(it\Collider) < 800.0
								PositionEntity(it\Collider, EntityX(it\Collider), 800.5, EntityZ(it\Collider))
								ResetEntity(it\Collider)
							EndIf
						EndIf
					Next
					For du = Each Dummy1499_1
						If e\EventState3 < 70 * 30.0 Then
							If du\Anim = 0 Then
								If AnimTime(du\OBJ) =< 360.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ),321.0, 361.0, 0.2, False)
								ElseIf AnimTime(du\OBJ) > 361.5 And AnimTime(du\OBJ) =< 401.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ),362.0, 402.0, 0.2, False)
								Else
									Temp = Rand(0, 1)
									If Temp = 0 Then
										SetAnimTime(du\OBJ, 321.0)
									Else
										SetAnimTime(du\OBJ, 362.0)
									EndIf
								EndIf
							Else
								If AnimTime(du\OBJ) =< 452.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ), 413.0, 453.0, 0.2, False)
								ElseIf AnimTime(du\OBJ) > 453.5 And AnimTime(du\OBJ) =< 497.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ), 454.0, 498.0, 0.2, False)
								Else
									Temp = Rand(0, 1)
									If Temp = 0 Then
										SetAnimTime(du\OBJ, 413.0)
									Else
										SetAnimTime(du\OBJ, 454.0)
									EndIf
								EndIf
							EndIf
						Else
							If du\Anim = 0 Then
								If AnimTime(du\OBJ) =< 411.5 And AnimTime(du\OBJ) > 320.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ), 403.0, 412.0, 0.2, False)
								Else
									Animate2(du\OBJ, AnimTime(du\OBJ), 296.0, 320.0, 0.2, True)
								EndIf
							Else
								If AnimTime(du\OBJ) =< 507.5 And AnimTime(du\OBJ) > 320.5 Then
									Animate2(du\OBJ, AnimTime(du\OBJ), 499.0, 508.0, 0.2, False)
								Else
									Animate2(du\OBJ, AnimTime(du\OBJ), 296.0, 320.0, 0.2, True)
								EndIf
							EndIf
							
							Local Pvt% = CreatePivot()
							
							PositionEntity(Pvt,EntityX(du\OBJ), EntityY(du\OBJ), EntityZ(du\OBJ), True)
							PointEntity(Pvt, Collider)
							RotateEntity(du\OBJ, 0.0, CurveAngle(EntityYaw(Pvt), EntityYaw(du\OBJ) - 180.0, 10.0) + 180.0, 0.0)
							FreeEntity(Pvt)
						EndIf
					Next
					; ~ Player is inside the church
					If e\EventState3 < 70 * 10.0 Then
						If Abs(EntityX(Collider) - (e\room\x - 56.0 * RoomScale)) < 2160.0 * RoomScale Then
							If Abs(EntityZ(Collider) - (e\room\z + 2287.0 * RoomScale)) < 1408.0 * RoomScale Then
								e\EventState3 = e\EventState3 + FPSfactor
							EndIf
						EndIf
					ElseIf e\EventState3 >= 70 * 10.0 And e\EventState3 < 70 * 20.0 Then
						For i = 0 To 1
							e\room\NPC[i]\Reload = 1.0
						Next
						e\EventState3 = 70 * 20.0
					ElseIf e\EventState3 = 70 * 20.0
						If e\room\NPC[0]\Frame > 854.5 Then
							For i = 2 To 5
								If i = 2
									If e\room\NPC[i]\Sound <> 0 Then 
										FreeSound_Strict(e\room\NPC[i]\Sound) : e\room\NPC[i]\Sound = 0
									EndIf
									e\room\NPC[i]\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
									e\room\NPC[i]\SoundCHN = PlaySound2(e\room\NPC[i]\Sound, Camera, e\room\NPC[i]\Collider, 50.0)
								EndIf
								e\room\NPC[i]\State = 1.0
								e\room\NPC[i]\Frame = 203.0
							Next
							e\EventState3 = 70 * 30.0
						EndIf
					EndIf
					
					If e\room\NPC[0] <> Null Then
						ShowEntity(e\room\Levers[1])
						If e\EventState3 < 70 * 30.0 Then
							ShouldPlay = 66
							If NowPlaying = 66 Then
								If e\SoundCHN = 0 Then
									e\Sound2 = LoadSound_Strict("SFX\Music\HaveMercyOnMe(Choir).ogg")
									e\SoundCHN = StreamSound_Strict("SFX\Music\HaveMercyOnMe(NoChoir).ogg", MusicVolume)
									e\SoundCHN_IsStream = True
								EndIf
							EndIf
							If e\Sound2 <> 0 Then
								e\SoundCHN2 = LoopSound2(e\Sound2, e\SoundCHN2, Camera, e\room\Levers[0], 10.0, MusicVolume)
							EndIf
						Else
							ShouldPlay = 19
							If e\SoundCHN <> 0 Then
								StopStream_Strict(e\SoundCHN)
								StopChannel(e\SoundCHN2)
								e\SoundCHN = 0
								e\SoundCHN2 = 0
							EndIf
							If e\Sound2 <> 0 Then
								FreeSound_Strict(e\Sound2) : e\Sound2 = 0
							EndIf
						EndIf
					EndIf
					
					If EntityDistance(Collider, e\room\OBJ) > 40.0
						For du.Dummy1499_1 = Each Dummy1499_1
							HideEntity(du\OBJ)
						Next
					Else
						For du.Dummy1499_1 = Each Dummy1499_1
							ShowEntity(du\OBJ)
						Next
					EndIf
				Else
					DropSpeed = 0.0
				EndIf
				CurrStepSFX = 3
				PlayerFallingPickDistance = 0.0
			Else
				If e\EventState = 2.0
					If e\SoundCHN <> 0 Then
						StopStream_Strict(e\SoundCHN)
						StopChannel(e\SoundCHN2)
						e\SoundCHN = 0
						e\SoundCHN2 = 0
					EndIf
					HideEntity(NTF_1499Sky)
					HideChunks()
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype1499_1
							RemoveNPC(n)
						EndIf
					Next
					For du.Dummy1499_1 = Each Dummy1499_1
						FreeEntity(du\OBJ)
						Delete(du)
					Next
					If e\EventState3 < 70 * 30.0 Then
						e\EventState3 = 0.0
					EndIf
					e\EventState = 1.0
					If e\Sound2 <> 0 Then
						FreeSound_Strict(e\Sound2) : e\Sound2 = 0
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function

Function UpdateEndings()
	Local e.Events, n.NPCs, r.Rooms, i%, Pvt%, p.Particles
	Local Dist#
	Local o.Objects = First Objects
	Local ov.Overlays = First Overlays
	
	For e.Events = Each Events
		Select e\EventName
			Case "gateb"
				;[Block]
				If PlayerRoom = e\room Then
					If EntityY(Collider) > 1040.0 * RoomScale Then
						
						For r.Rooms = Each Rooms
							HideEntity(r\OBJ)
						Next					
						ShowEntity(e\room\OBJ)
						
						If e\EventState = 0.0 Then
							DrawLoading(0, True)
							
							For i = 0 To MaxRoomLights - 1
								If e\room\LightSprites[i] <> 0 Then 
									EntityFX(e\room\LightSprites[i], 1 + 8)
								EndIf
							Next
							
							For n.NPCs = Each NPCs
								RemoveNPC(n)
							Next
							Curr173 = Null
							Curr106 = Null
							Curr096 = Null
							Curr513_1 = Null
							
							CameraFogMode(Camera, 0)
							SecondaryLightOn = True
							
							DrawLoading(60, True)
							DrawLoading(90, True)
							
							e\room\NPC[0] = CreateNPC(NPCtypeApache, e\room\x, 100.0, e\room\z)
							e\room\NPC[0]\State = 1.0
							
							e\room\NPC[1] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[4], True), EntityY(e\room\Objects[4], True) + 0.2, EntityZ(e\room\Objects[4], True))
							e\room\NPC[1]\State = 0.0
							e\room\NPC[1]\State2 = 10.0
							
							
							Pvt = CreatePivot()
							PositionEntity(Pvt, EntityX(e\room\Objects[0], True), EntityY(e\room\Objects[0], True), EntityZ(e\room\Objects[0], True))
							
							e\room\Objects[0] = LoadMesh_Strict("GFX\map\exit1terrain.b3d", e\room\OBJ)
							ScaleEntity(e\room\Objects[0], RoomScale, RoomScale, RoomScale,True)
							RotateEntity(e\room\Objects[0], 0.0, e\room\Angle, 0.0, True)
							PositionEntity(e\room\Objects[0], EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), True)
							FreeEntity(Pvt)
							
							Delay(100)
							
							CreateConsoleMsg("")
							CreateConsoleMsg("WARNING! Teleporting away from this area may cause bugs or crashing.", 255, 0, 0)
							CreateConsoleMsg("")
							
							Sky = sky_CreateSky("GFX\map\sky\sky")
							RotateEntity(Sky, 0.0, e\room\Angle - 90.0, 0.0)
							
							e\EventState = 1.0
							
							For n.NPCs = Each NPCs
								If n\NPCtype = NPCtypeMTF
									RemoveNPC(n)
								EndIf
							Next
							
							DrawLoading(100, True)
						Else
							UpdateSky()
							
							If e\EventState < 2.0 And SelectedEnding = "" Then 
								If e\room\NPC[0]\State = 2.0 Then
									ShouldPlay = 6
								Else
									e\EventState2 = (e\EventState2 + FPSfactor) Mod 3600.0
									PositionEntity(e\room\NPC[0]\Collider, EntityX(e\room\OBJ, True) + Cos(e\EventState2 / 10.0) * 6000.0 * RoomScale, 14000.0 * RoomScale, EntityZ(e\room\OBJ, True) + Sin(e\EventState2 / 10.0) * 6000.0 * RoomScale)
									RotateEntity(e\room\NPC[0]\Collider, 7.0, (e\EventState2 / 10.0), 20.0)										
									ShouldPlay = 5
								EndIf
								
								If EntityDistance(Collider, e\room\Objects[10]) < 320.0 * RoomScale Then
									e\EventState = 2.0
									e\room\RoomDoors[2]\Open = False
									e\room\RoomDoors[2]\Locked = 6
									e\room\RoomDoors[3]\Open = False
									e\room\RoomDoors[3]\Locked = 6
									
									e\room\NPC[2] = CreateNPC(NPCtypeApache, EntityX(e\room\Objects[9], True), EntityY(e\room\Objects[9], True) + 0.5, EntityZ(e\room\Objects[9], True))
									e\room\NPC[2]\State = 3.0
									
									e\room\NPC[3] = CreateNPC(NPCtypeApache, EntityX(e\room\Objects[7], True), EntityY(e\room\Objects[7], True) - 2.0, EntityZ(e\room\Objects[7], True))
									e\room\NPC[3]\State = 3.0
									
									e\room\NPC[0]\State = 3.0
									
									PlayAnnouncement("SFX\Ending\GateB\682Battle.ogg")
								EndIf								
							Else
								ShouldPlay = 6
								e\EventState = e\EventState  +FPSfactor
								
								If e\EventState < 70 * 40.0 Then 	
									e\room\NPC[0]\EnemyX = EntityX(e\room\Objects[11], True) + Sin(MilliSecs2() / 25.0) * 3.0
									e\room\NPC[0]\EnemyY = EntityY(e\room\Objects[11], True) + Cos(MilliSecs() / 85.0) + 9.0
									e\room\NPC[0]\EnemyZ = EntityZ(e\room\Objects[11], True) + Cos(MilliSecs() / 25.0) * 3.0
									
									e\room\NPC[2]\EnemyX = EntityX(e\room\Objects[11], True) + Sin(MilliSecs2() / 23.0) * 3.0
									e\room\NPC[2]\EnemyY = EntityY(e\room\Objects[11], True) + Cos(MilliSecs() / 83.0) + 5.0
									e\room\NPC[2]\EnemyZ = EntityZ(e\room\Objects[11], True) + Cos(MilliSecs() / 23.0) * 3.0
									
									If e\room\NPC[3]\State = 3.0 Then 
										e\room\NPC[3]\EnemyX = EntityX(e\room\Objects[11], True) + Sin(MilliSecs2() / 20.0) * 3.0
										e\room\NPC[3]\EnemyY = EntityY(e\room\Objects[11], True) + Cos(MilliSecs() / 80.0) + 3.5
										e\room\NPC[3]\EnemyZ = EntityZ(e\room\Objects[11], True) + Cos(MilliSecs() / 20.0) * 3.0
									EndIf
								EndIf
							EndIf
							
							If e\EventState > 70 * 0.6 And e\EventState < 70 * 42.2 Then 
								If e\EventState < 70 * 0.7 Then
									CameraShake = 0.5
								ElseIf e\EventState > 70 * 3.2 And e\EventState < 70 * 3.3
									CameraShake = 0.5
								ElseIf e\EventState > 70 * 6.1 And e\EventState < 70 * 6.2	
									CameraShake = 0.5
								ElseIf e\EventState < 70 * 10.8 And e\EventState < 70 * 10.9	
									CameraShake = 0.5
								ElseIf e\EventState > 70 * 12.1 And e\EventState < 70 * 12.3
									CameraShake = 1.0
								ElseIf e\EventState > 70 * 13.3 And e\EventState < 70 * 13.5
									CameraShake = 1.5
								ElseIf e\EventState > 70 * 16.5 And e\EventState < 70 * 18.5
									CameraShake = 3.0
								ElseIf e\EventState > 70 * 21.5 And e\EventState < 70 * 24.0	
									CameraShake = 2.0
								ElseIf e\EventState > 70 * 25.5 And e\EventState < 70 * 27.0	
									CameraShake = 2.0	
								ElseIf e\EventState > 70 * 31.0 And e\EventState < 70 * 31.5	
									CameraShake = 0.5	
								ElseIf e\EventState > 70 * 35.0 And e\EventState < 70 * 36.5	
									CameraShake = 1.5		
									If e\EventState - FPSfactor =< 70 * 35.0 Then
										e\SoundCHN = StreamSound_Strict("SFX\Ending\GateB\DetonatingAlphaWarheads.ogg", SFXVolume, 0.0)
										e\SoundCHN_IsStream = True
									EndIf									
								ElseIf e\EventState > 70 * 39.5 And e\EventState < 70 * 39.8		
									CameraShake = 1.0
								ElseIf e\EventState > 70 * 42.0
									CameraShake = 0.5
									
									; ~ Helicopters leave
									e\room\NPC[0]\EnemyX = EntityX(e\room\Objects[19], True) + 4.0
									e\room\NPC[0]\EnemyY = EntityY(e\room\Objects[19], True) + 4.0
									e\room\NPC[0]\EnemyZ = EntityZ(e\room\Objects[19], True) + 4.0
									
									e\room\NPC[2]\EnemyX = EntityX(e\room\Objects[19], True)
									e\room\NPC[2]\EnemyY = EntityY(e\room\Objects[19], True)
									e\room\NPC[2]\EnemyZ = EntityZ(e\room\Objects[19], True)
								EndIf
							EndIf
							
							If e\EventState >= 70 * 45.0 Then
								If e\EventState < 70 * 75.0 Then
									If e\SoundCHN2 = 0
										e\SoundCHN2 = StreamSound_Strict("SFX\Ending\GateB\Siren.ogg", SFXVolume, Mode)
										e\SoundCHN2_IsStream = True
									EndIf
								Else
									If SelectedEnding = "" Then
									    ShouldPlay = 66
										
										StopStream_Strict(e\SoundCHN)
										StopStream_Strict(e\SoundCHN2)
										
										Temp = True
										For e2.Events = Each Events
											If e2\EventName = "room2nuke" Then
												temp = e2\EventState
												Exit
											EndIf
										Next
										
										If Temp = 1 Then ; ~ Explode
											ExplosionTimer = Max(ExplosionTimer, 0.1)
											SelectedEnding = "B2"
										Else
											PlayAnnouncement("SFX\Ending\GateB\AlphaWarheadsFail.ogg")
											
											For i = 0 To 1
												n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[18], True) + (i * 0.4), EntityY(e\room\Objects[18], True) + 0.29, EntityZ(e\room\Objects[18], True) + (i * 0.4))
											Next
											
											n.NPCs = CreateNPC(NPCtypeMTF, EntityX(e\room\RoomDoors[2]\OBJ, True),EntityY(e\room\RoomDoors[2]\OBJ, True) + 0.29, (EntityZ(e\room\RoomDoors[2]\OBJ, True) + EntityZ(e\room\RoomDoors[3]\OBJ, True)) / 2.0)
											
											For n.NPCs = Each NPCs
												If n\NPCtype = NPCtypeMTF Then
													n\LastSeen = 70 * Rnd(30.0, 35.0)
													n\State = 3.0
													n\State2 = 10.0
													n\EnemyX = EntityX(Collider)
													n\EnemyY = EntityY(Collider)
													n\EnemyZ = EntityZ(Collider)
												EndIf
											Next
											
											e\EventState = 70 * 85.0
											
											SelectedEnding = "B3"
										EndIf
									Else
										If SelectedEnding = "B3" Then
											e\room\NPC[0]\EnemyX = EntityX(e\room\Objects[11], True) + Sin(MilliSecs2() / 25.0) * 3.0
											e\room\NPC[0]\EnemyY = EntityY(e\room\Objects[11], True) + Cos(MilliSecs() / 85.0) + 9.0
											e\room\NPC[0]\EnemyZ = EntityZ(e\room\Objects[11], True) + Cos(MilliSecs() / 25.0) * 3.0
											
											e\room\NPC[2]\EnemyX = EntityX(e\room\Objects[11], True) + Sin(MilliSecs2() / 23.0) * 3.0
											e\room\NPC[2]\EnemyY = EntityY(e\room\Objects[11], True) + Cos(MilliSecs() / 83.0) + 5.0
											e\room\NPC[2]\EnemyZ = EntityZ(e\room\Objects[11], True) + Cos(MilliSecs() / 23.0) * 3.0
											
											e\room\RoomDoors[5]\Open = True
											
											If e\EventState3 > 0.0 And e\EventState3 =< 500.0
												e\EventState3 = e\EventState3 + FPSfactor
												UnableToMove = True
												For n.NPCs = Each NPCs
													If n\NPCtype = NPCtypeMTF Then
														n\LastSeen = (70 * Rnd(30.0, 35.0))
														n\State = 3.0
														n\State2 = 10.0
														n\EnemyX = EntityX(Collider)
														n\EnemyY = EntityY(Collider)
														n\EnemyZ = EntityZ(Collider)
														PointEntity(n\Collider, Collider)
													EndIf
												Next
											ElseIf e\EventState3 > 500.0
												ShouldPlay = 0
												CurrSpeed = 0.0
												PlaySound_Strict LoadTempSound("SFX\Ending\GateB\Gunshot.ogg")
												chs\GodMode = False
												chs\NoClip = False
												chs\InfiniteStamina = False
												chs\NoBlink = False
												chs\Cheats = False
												LightFlash = 20.0
												KillTimer = -0.1
												DeathMsg = ""
												Kill()
												BlinkTimer = -10.0
												For n.NPCs = Each NPCs
													If n\NPCtype = NPCtypeMTF
														RemoveNPC(n)
													EndIf
												Next
												RemoveEvent(e)
												Exit
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
							
							If e\EventState > 70 * 26.5 Then
								If e\room\Objects[12] = 0 Then
									e\room\Objects[12] = CopyEntity(o\NPCModelID[28])
									ScaleEntity(e\room\Objects[12], 0.15, 0.15, 0.15)
									Temp = (Min(((EntityDistance(e\room\NPC[3]\Collider, Collider) / RoomScale) - 3000.0) / 4.0, 1000.0) + 12192.0) * RoomScale
									PositionEntity(e\room\Objects[12], EntityX(e\room\NPC[3]\Collider), 12192.0 * RoomScale, EntityZ(e\room\NPC[3]\Collider))
									RotateEntity(e\room\Objects[12], 0.0, e\room\Angle + Rnd(-10.0, 10.0), 0.0, True)
									TurnEntity(e\room\Objects[12], 0.0, 0.0, 180.0)
								Else
									If WrapAngle(EntityRoll(e\room\Objects[12])) < 340.0 Then 
										Angle# = WrapAngle(EntityRoll(e\room\Objects[12]))
										TurnEntity(e\room\Objects[12], 0.0, 0.0, (5.0 + Abs(Sin(Angle)) * 2.0) * FPSfactor)
										If Angle < 270.0 And WrapAngle(EntityRoll(e\room\Objects[12])) >= 270.0 Then
											PlaySound_Strict(LoadTempSound("SFX\Character\Apache\Crash1.ogg"))
											e\room\NPC[3]\State = 4.0
											e\room\NPC[3]\State2 = 1.0
											e\room\NPC[3]\EnemyX = EntityX(e\room\Objects[7], True)
											e\room\NPC[3]\EnemyY = EntityY(e\room\Objects[7], True) - 2.5
											e\room\NPC[3]\EnemyZ = EntityZ(e\room\Objects[7], True)
											
											em.Emitters = CreateEmitter(EntityX(e\room\NPC[3]\Collider), EntityY(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider), 0)
											em\Room = PlayerRoom : em\RandAngle = 45.0 : em\Gravity = -0.18 : em\LifeTime = 400.0 : em\SizeChange = Rnd(0.005, 0.007) : em\Achange = -0.004
											TurnEntity(em\OBJ, (-80) + 20.0 * i, 0.0, 0.0)
											EntityParent(em\OBJ, e\room\NPC[3]\Collider)
											
											If ParticleAmount > 0
												For i = 0 To (3 + (4 * (ParticleAmount - 1)))
													p.Particles = CreateParticle(EntityX(e\room\NPC[3]\Collider), EntityY(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider), 0, Rnd(0.5, 1.0), -0.1, 200.0)
													p\Speed = 0.01 : p\SizeChange = 0.01 : p\A = 1.0 : p\Achange = -0.005
													RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
													MoveEntity(p\Pvt, 0.0, 0.0, 0.3)
												Next
												
												For i = 0 To (6 + (6 * (ParticleAmount - 1)))
													p.Particles = CreateParticle(EntityX(e\room\NPC[3]\Collider), EntityY(e\room\NPC[3]\Collider), EntityZ(e\room\NPC[3]\Collider), 0, 0.02, 0.003, 200.0)
													p\Speed = 0.04 : p\A = 1.0 : p\Achange = -0.005
													RotateEntity(p\Pvt, Rnd(360.0), Rnd(360.0), 0.0)
												Next
											EndIf
										EndIf
									Else
										HideEntity e\room\Objects[12]
									EndIf
								EndIf
							EndIf
						EndIf
						HideEntity(ov\OverlayID[0])
						CameraFogRange(Camera, 5.0, 45.0)
						
						Angle = Max(Sin(EntityYaw(Collider)), 0.0)
						CameraFogColor(Camera, 200.0 + (Angle * 50.0), 200.0 + (Angle * 30.0), 200.0)
						CameraClsColor(Camera, 200.0 + (Angle * 50.0), 200.0 + (Angle * 30.0), 200.0)					
						CameraRange(Camera, 0.05, 60.0)
						AmbientLight(140.0, 140.0, 140.0)
						
						If ParticleAmount > 0
							If Rand(3) = 1 Then
								p.Particles = CreateParticle(EntityX(Camera) + Rnd(-2.0, 2.0), EntityY(Camera) + Rnd(0.9, 2.0), EntityZ(Camera) + Rnd(-2.0, 2.0), 2, 0.006, 0.0, 300.0)
								p\Speed = Rnd(0.002,0.003) : p\SizeChange = -0.00001
								RotateEntity(p\Pvt, Rnd(-20.0, 20.0), e\room\Angle - 90.0 + Rnd(-15.0, 15.0), 0.0, 0.0)
							EndIf
						EndIf
						
						; ~ Helicopter spots or player is within range. --> Start shooting.
						If e\room\NPC[1]\State <> 1 Then
							If (EntityDistance(e\room\NPC[1]\Collider, Collider) < 15.0) Or EntityVisible(e\room\NPC[0]\Collider, Collider) And (Not chs\Notarget) Then
								e\room\NPC[1]\State = 1.0
								e\room\NPC[1]\State3 = 1.0
							Else
								e\room\NPC[1]\State = 0.0
								e\room\NPC[1]\State3 = 0.0
							EndIf
						EndIf
						
						; ~ Below roof or inside catwalk. --> Stop shooting.
						If (EntityDistance(e\room\NPC[1]\Collider, Collider) < 8.9) Or (EntityDistance(e\room\Objects[5], Collider) < 16.9) Or chs\Notarget Then
							e\room\NPC[1]\State = 0.0
							e\room\NPC[1]\State3 = 0.0
						Else
							e\room\NPC[1]\State = 1.0
							e\room\NPC[1]\State3 = 1.0
						EndIf
					EndIf
				EndIf
				;[End Block]
			Case "gatea"
				;[Block]
				If PlayerRoom = e\room Then 
					For r.Rooms = Each Rooms
						HideEntity(r\OBJ)
					Next					
					ShowEntity(e\room\OBJ)
					
					If e\EventState = 0.0 Then
						DrawLoading(0)
						
						e\room\Objects[0] = LoadMesh_Strict("GFX\map\gateatunnel.b3d")
						PositionEntity(e\room\Objects[0], EntityX(e\room\OBJ, True), EntityY(e\room\OBJ, True), EntityZ(e\room\OBJ, True))
						ScaleEntity(e\room\Objects[0],RoomScale, RoomScale, RoomScale)
						EntityType(e\room\Objects[0], HIT_MAP)
						EntityPickMode(e\room\Objects[0], 3)
						EntityParent(e\room\Objects[0], e\room\OBJ)
						
						DrawLoading(30)
						
						For i = 0 To e\room\MaxLights
							If e\room\LightSprites[i] <> 0 Then 
								EntityFX(e\room\LightSprites[i], 1 + 8)
							EndIf
						Next
						
						For n.NPCs = Each NPCs
							If n <> Curr106 And n <> Curr173 Then  
								RemoveNPC(n)
							EndIf
						Next
						Curr173\Idle = 1
						Curr096 = Null
						Curr513_1 = Null
						
						CameraFogMode(Camera, 0)
						SecondaryLightOn = True
						
						HideDistance = 35.0
						
						For i = 2 To 4
							e\room\NPC[i] = CreateNPC(NPCtypeApache, e\room\x, e\room\y + 11.0, e\room\z)
							e\room\NPC[i]\State = (Not Contained106)
						Next
						
						CreateConsoleMsg("")
						CreateConsoleMsg("WARNING! Teleporting away from this area may cause bugs or crashing.", 255, 0, 0)
						CreateConsoleMsg("")
						
						Sky = sky_CreateSky("GFX\map\sky\sky")
						RotateEntity(Sky, 0.0, e\room\Angle, 0.0)
						
						DrawLoading(60)
						
						For i = 0 To 1
							e\room\NPC[i] = CreateNPC(NPCtypeGuard, EntityX(e\room\Objects[i + 5], True), EntityY(e\room\Objects[i + 5], True), EntityZ(e\room\Objects[i + 5], True))
							e\room\NPC[i]\State = 0.0
							PointEntity(e\room\NPC[i]\Collider, e\room\Objects[3])
						Next
						
						For i = 7 To 8
							e\room\NPC[i] = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[i], True) + 0.8, EntityY(e\room\Objects[i], True), EntityZ(e\room\Objects[i], True) + 0.8)
							e\room\NPC[i]\State = 5.0
							e\room\NPC[i]\PrevState = 1
							PointEntity(e\room\NPC[i]\Collider, e\room\Objects[3])
						Next	
						
						For i = 5 To 6
							e\room\NPC[i] = CreateNPC(NPCtypeMTF, EntityX(e\room\Objects[i + 2], True), EntityY(e\room\Objects[i + 2], True), EntityZ(e\room\Objects[i + 2], True))
							e\room\NPC[i]\State = 5.0
							e\room\NPC[i]\PrevState = 1	
							PointEntity(e\room\NPC[i]\Collider, e\room\Objects[3])
						Next		
						
						If Contained106 Then
							e\room\RoomDoors[2]\Locked = True
							
							PositionEntity(e\room\NPC[5]\Collider, EntityX(e\room\Objects[15], True) + (i - 6) * 0.2, EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True) + (i - 6) * 0.2, True)
							ResetEntity(e\room\NPC[5]\Collider)
						EndIf
						
						xTemp# = EntityX(e\room\Objects[9], True)
						zTemp# = EntityZ(e\room\Objects[9], True)
						FreeEntity(e\room\Objects[9])
						
						e\room\Objects[9] = LoadMesh_Strict("GFX\map\lightgunbase.b3d")
						ScaleEntity(e\room\Objects[9], RoomScale, RoomScale, RoomScale)
						EntityFX(e\room\Objects[9], 0)
						PositionEntity(e\room\Objects[9], xTemp, e\room\y + 992.0 * RoomScale, zTemp)
						e\room\Objects[10] = LoadMesh_Strict("GFX\map\lightgun.b3d")
						EntityFX(e\room\Objects[10], 0)
						ScaleEntity(e\room\Objects[10], RoomScale, RoomScale, RoomScale)
						PositionEntity(e\room\Objects[10], xTemp, e\room\y + (992.0 + 288.0) * RoomScale, zTemp - 176.0 * RoomScale, True)
						EntityParent(e\room\Objects[10], e\room\Objects[9])
						RotateEntity(e\room\Objects[9], 0.0, 48.0, 0.0)
						RotateEntity(e\room\Objects[10], 40.0, 0.0, 0.0)
						
						For Temp = 0 To 20
							For i = 0 To 1
								TranslateEntity(e\room\NPC[i]\Collider, 0.0, -0.04, 0.0)
							Next							
							For i = 5 To 8
								TranslateEntity(e\room\NPC[i]\Collider, 0.0, -0.04, 0.0)
							Next
						Next
						
						ResetEntity(Collider)
						e\EventState = 1.0
						
						RotateEntity(Collider, 0.0, EntityYaw(Collider) + (e\room\Angle + 180.0), 0.0)
						
						If (Not Contained106) Then PlaySound_Strict LoadTempSound("SFX\Ending\GateA\106Escape.ogg") 
						
						DrawLoading(100)
					Else
						ShouldPlay = 17
						
						e\EventState = e\EventState + FPSfactor
						HideEntity(ov\OverlayID[0])
						CameraFogRange(Camera, 5.0, 30.0)
						
						Angle = Max(Sin(EntityYaw(Collider) + 90.0), 0.0)
						CameraFogColor(Camera, 200.0 + (Angle * 40.0), 200.0 + (Angle * 20), 200)
						CameraClsColor(Camera, 200.0 + (Angle * 40.0), 200.0 + (Angle * 20), 200)		
						CameraRange(Camera, 0.05, 30.0)
						AmbientLight(140.0, 140.0, 140.0)
						
						For i = 2 To 4
							If e\room\NPC[i] <> Null Then 
								If e\room\NPC[i]\State < 2.0 Then 
									PositionEntity(e\room\NPC[i]\Collider, EntityX(e\room\Objects[3], True) + Cos(e\EventState / 10.0 + (120.0 * i)) * 6000.0 * RoomScale, e\room\y + 11.0, EntityZ(e\room\Objects[3], True) + Sin(e\EventState / 10.0 + (120.0 * i)) * 6000.0 * RoomScale)
									RotateEntity(e\room\NPC[i]\Collider, 7.0, (e\EventState / 10.0 + (120.0 * i)), 20.0)
								EndIf
							EndIf
						Next
						
						UpdateSky()
						
						If e\EventState >= 350.0 Then
							If Contained106 = False Then
								If e\EventState - FPSfactor < 350.0
									Curr106\State = -0.1
									SetNPCFrame(Curr106, 110.0)
									PositionEntity(Curr106\Collider, EntityX(e\room\Objects[3], True), EntityY(Collider) - 50.0, EntityZ(e\room\Objects[3], True), True)
									PositionEntity(Curr106\OBJ, EntityX(e\room\Objects[3], True), EntityY(Collider) - 50.0, EntityZ(e\room\Objects[3], True), True)
									
									de.Decals = CreateDecal(0, EntityX(e\room\Objects[3], True), EntityY(e\room\Objects[3], True) + 0.01, EntityZ(e\room\Objects[3], True), 90.0, Rand(360.0), 0.0)
									de\Size = 0.05 : de\SizeChange = 0.001 : EntityAlpha(de\OBJ, 0.8) : UpdateDecals() 
									
									PlaySound_Strict(HorrorSFX(5))
									PlaySound_Strict(DecaySFX(0))
								ElseIf Curr106\State < 0.0
									HideEntity(Curr106\OBJ2)
									Curr106\PathTimer = 70 * 100.0
									
									If Curr106\State3 = 0.0 Then
										If Curr106\PathStatus <> 1 Then
											PositionEntity(Curr106\Collider, EntityX(e\room\Objects[3], True), EntityY(Curr106\Collider), EntityZ(e\room\Objects[3], True), True)
											If Curr106\State =< -10.0 Then
												Dist = EntityY(Curr106\Collider)
												PositionEntity(Curr106\Collider, EntityX(Curr106\Collider), EntityY(e\room\Objects[3], True), EntityZ(Curr106\Collider), True)
												Curr106\PathStatus = FindPath(Curr106, EntityX(e\room\NPC[5]\Collider, True), EntityY(e\room\NPC[5]\Collider, True), EntityZ(e\room\NPC[5]\Collider,True))
												Curr106\PathTimer = 70 * 200.0
												PositionEntity(Curr106\Collider, EntityX(Curr106\Collider), Dist, EntityZ(Curr106\Collider), True)
												ResetEntity(Curr106\Collider)
												Curr106\PathLocation = 1
											EndIf
										Else
											Curr106\PathTimer = 70 * 200.0
											For i = 2 To 4 ; ~ Helicopters start attacking SCP-106
												e\room\NPC[i]\State = 3.0 
												e\room\NPC[i]\EnemyX = EntityX(Curr106\OBJ, True)
												e\room\NPC[i]\EnemyY = EntityY(Curr106\OBJ, True) + 5.0
												e\room\NPC[i]\EnemyZ = EntityZ(Curr106\OBJ, True)
											Next
											
											For i = 5 To 8
												e\room\NPC[i]\State = 5.0
												e\room\NPC[i]\EnemyX = EntityX(Curr106\OBJ, True)
												e\room\NPC[i]\EnemyY = EntityY(Curr106\OBJ, True) + 0.4
												e\room\NPC[i]\EnemyZ = EntityZ(Curr106\OBJ, True)											
											Next
											
											Pvt = CreatePivot()
											PositionEntity(Pvt, EntityX(e\room\Objects[10], True), EntityY(e\room\Objects[10], True), EntityZ(e\room\Objects[10], True))
											PointEntity(Pvt, Curr106\Collider)
											RotateEntity(e\room\Objects[9], 0.0, CurveAngle(EntityYaw(Pvt), EntityYaw(e\room\Objects[9], True), 150.0), 0.0, True)
											RotateEntity(e\room\Objects[10], CurveAngle(EntityPitch(Pvt), EntityPitch(e\room\Objects[10], True), 200.0), EntityYaw(e\room\Objects[9], True), 0.0, True)
											FreeEntity(Pvt)
											
											If FPSfactor > 0.0 Then ; ~ Decals under 106
												If ((e\EventState - FPSfactor) Mod 100.0) =< 50.0 And (e\EventState Mod 100.0) > 50.0 Then
													de.Decals = CreateDecal(0, EntityX(Curr106\Collider, True),EntityY(e\room\Objects[3], True) + 0.01, EntityZ(Curr106\Collider, True), 90.0, Rnd(360.0), 0.0)
													de\Size = 0.2 : de\SizeChange = 0.004 : de\Timer = 90000.0 : UpdateDecals() 
													EntityAlpha(de\OBJ, 0.8)
												EndIf
											EndIf
										EndIf
									EndIf
									
									Dist = Distance(EntityX(Curr106\Collider), EntityZ(Curr106\Collider), EntityX(e\room\Objects[4], True), EntityZ(e\room\Objects[4], True))
									
									Curr106\CurrSpeed = CurveValue(0.0, Curr106\CurrSpeed, Max(5.0 * Dist, 2.0))
									If Dist < 15.0 Then
										If e\SoundCHN2 = 0 Then
											e\SoundCHN2 = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Franklin.ogg"))
										EndIf
										
										If Dist < 0.4 Then
											Curr106\PathStatus = 0
											Curr106\PathTimer = 70 * 200.0
											If Curr106\State3 = 0.0 Then 
												SetNPCFrame(Curr106, 259.0)
												If e\Sound <> 0 Then 
													FreeSound_Strict(e\Sound) : e\Sound = 0
												EndIf
												LoadEventSound(e, "SFX\Ending\GateA\106Retreat.ogg")
												e\SoundCHN = PlaySound2(e\Sound, Camera, Curr106\Collider, 35.0)
											EndIf
											
											If FPSfactor > 0.0 Then
												If ((e\EventState - FPSfactor) Mod 160.0) =< 50.0 And (e\EventState Mod 160.0) > 50.0 Then
													de.Decals = CreateDecal(0, EntityX(Curr106\Collider, True), EntityY(e\room\Objects[3], True) + 0.01, EntityZ(Curr106\Collider, True), 90.0, Rnd(360.0), 0.0)
													de\Size = 0.05 : de\SizeChange = 0.004 : de\Timer = 90000.0 : UpdateDecals() 	
													EntityAlpha(de\OBJ, 0.8)
												EndIf
											EndIf
											
											AnimateNPC(Curr106, 259.0, 110.0, -0.1, False)
											
											Curr106\State3 = Curr106\State3 + FPSfactor
											PositionEntity(Curr106\Collider, EntityX(Curr106\Collider, True), CurveValue(EntityY(e\room\Objects[3], True) - (Curr106\State3 / 4500.0), EntityY(Curr106\Collider, True), 100.0), EntityZ(Curr106\Collider, True))
											If Curr106\State3 > 700.0 Then
												Curr106\State = 100000.0
												e\EventState2 = 0.0
												For i = 5 To 8
													e\room\NPC[i]\State = 1.0
												Next
												For i = 2 To 4 ; ~ Helicopters attack the player
													e\room\NPC[i]\State = 2.0
												Next
												HideEntity(Curr106\OBJ)
											EndIf
										Else
											If Dist < 8.5 Then 
												If e\EventState2 = 0
													e\SoundCHN2 = PlaySound_Strict (LoadTempSound("SFX\Ending\GateA\HIDTurret.ogg"))
													e\EventState2 = 1.0
												ElseIf e\EventState2 > 0.0
													e\EventState2 = e\EventState2 + FPSfactor
													If e\EventState2 >= 70 * 7.5 Then
														If e\EventState2 - FPSfactor < 70 * 7.5 Then
															p.Particles = CreateParticle(EntityX(Curr106\OBJ, True), EntityY(Curr106\OBJ, True) + 0.4, EntityZ(Curr106\OBJ, True), 4, 7.0, 0.0, (70 * 6.7))
															p\Speed = 0.0 : p\A = 1.0
															EntityParent(p\pvt, Curr106\Collider, True)
															
															p.Particles = CreateParticle(EntityX(e\room\Objects[10], True),EntityY(e\room\Objects[10], True), EntityZ(e\room\Objects[10], True), 4, 2.0, 0.0, (70 * 6.7))
															p\Speed = 0.0 : p\A = 1.0
															RotateEntity(p\Pvt, EntityPitch(e\room\Objects[10], True), EntityYaw(e\room\Objects[10], True), 0.0, True)
															MoveEntity(p\Pvt, 0.0, 92.0 * RoomScale, 512.0 * RoomScale)
															EntityParent(p\Pvt, e\room\Objects[10], True)
														ElseIf e\EventState2 < 70 * 14.3
															CameraShake = 0.5
															LightFlash = 0.3 + EntityInView(e\room\Objects[10], Camera) * 0.5
														EndIf
													EndIf
												EndIf
												
												If ParticleAmount > 0
													For i = 0 To Rand(2, 2 + (6 * (ParticleAmount - 1))) - Int(Dist)
														p.Particles = CreateParticle(EntityX(Curr106\OBJ, True), EntityY(Curr106\OBJ, True) + Rnd(0.4, 0.9), EntityZ(Curr106\OBJ), 0, 0.006, -0.002, 40.0)
														p\Speed = 0.005 : p\A = 0.8 : p\Achange = -0.01
														RotateEntity(p\Pvt, -Rnd(70.0, 110.0), Rnd(360.0), 0.0) 	
													Next										
												EndIf
											EndIf
										EndIf
									EndIf
								EndIf
								
								If e\EventState3 = 0.0 Then 
									If Abs(EntityY(Collider) - EntityY(e\room\Objects[11], True)) < 1.0 Then
										If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[11], True), EntityZ(e\room\Objects[11], True)) < 12.0 Then
											Curr106\State = 100000.0
											HideEntity(Curr106\OBJ)
											
											; ~ MTF spawns at the tunnel entrance
											For i = 5 To 8
												e\room\NPC[i]\State = 3.0
												PositionEntity(e\room\NPC[i]\Collider, EntityX(e\room\Objects[15], True) + (i - 6) * 0.3, EntityY(e\room\Objects[15], True), EntityZ(e\room\Objects[15], True) + (i - 6) * 0.3, True)
												ResetEntity(e\room\NPC[i]\Collider)
												
												e\room\NPC[i]\PathStatus = FindPath(e\room\NPC[i], EntityX(Collider), EntityY(Collider) + 0.2, EntityZ(Collider))
												e\room\NPC[i]\PathTimer = 70 * 2.0
												e\room\NPC[i]\LastSeen = 70 * 100.0
											Next
											e\room\NPC[5]\Sound = LoadSound_Strict("SFX\Character\MTF\ThereHeIs1.ogg")
											PlaySound2(e\room\NPC[5]\Sound, Camera, e\room\NPC[5]\Collider, 25.0)
											
											e\room\RoomDoors[2]\Open = True
											
											For i = 2 To 4
												RemoveNPC(e\room\NPC[i])
												e\room\NPC[i] = Null
											Next
											
											e\EventState3 = 1.0
										EndIf
									EndIf
								ElseIf e\EventState3 = 1.0
									For i = 5 To 8
										If EntityDistance(e\room\NPC[i]\Collider, Collider) > 4.0 Then
											e\room\NPC[i]\State = 3.0
										Else
											e\room\NPC[i]\State = 1.0
										EndIf
									Next
									
									If Abs(EntityY(Collider) - EntityY(e\room\Objects[11], True)) < 1.0 Then
										If Distance(EntityX(Collider), EntityZ(Collider), EntityX(e\room\Objects[11], True), EntityZ(e\room\Objects[11], True)) < 7.0 Then
											e\room\Objects[12] = CopyEntity(o\NPCModelID[26])
											
											Local Temp2# = 0.55 / MeshWidth(e\room\Objects[12])
											
											ScaleEntity(e\room\Objects[12], Temp2, Temp2, Temp2)
											PositionEntity(e\room\Objects[12], EntityX(e\room\Objects[11], True), EntityY(e\room\Objects[11], True), EntityZ(e\room\Objects[11], True))
											
											e\room\Objects[17] = CopyEntity(e\room\Objects[12])
											PositionEntity(e\room\Objects[17], EntityX(e\room\OBJ, True) - 3968.0 * RoomScale, EntityY(e\room\Objects[11], True), EntityZ(e\room\OBJ, True) - 1920.0 * RoomScale)
											
											OBJ = CopyEntity(e\room\Objects[12])
											PositionEntity(OBJ, EntityX(e\room\OBJ, True) - 4160.0 * RoomScale, EntityY(e\room\Objects[11], True), EntityZ(e\room\OBJ, True) - 1920.0 * RoomScale)
											EntityParent(OBJ, e\room\Objects[17])
											
											OBJ = CopyEntity(e\room\Objects[12])
											PositionEntity(OBJ, EntityX(e\room\OBJ, True) - 4064.0 * RoomScale, EntityY(e\room\Objects[11], True), EntityZ(e\room\OBJ, True) - 2112.0 * RoomScale)
											EntityParent(OBJ,e\room\Objects[17])
											
											e\SoundCHN = PlaySound2(LoadTempSound("SFX\Ending\GateA\Bell1.ogg"), Camera, e\room\Objects[12])
											
											p.Particles = CreateParticle(EntityX(e\room\Objects[11], True),EntityY(Camera,True), EntityZ(e\room\Objects[11],True), 4, 8.0, 0, 50)
											p\Speed = 0.15 : p\A = 0.5
											p.Particles = CreateParticle(EntityX(e\room\Objects[11], True),EntityY(Camera,True), EntityZ(e\room\Objects[11],True), 4, 8.0, 0, 50)
											p\Speed = 0.25 : p\A = 0.5
											PointEntity(p\Pvt, Collider)
											
											CameraShake = 1.0
											LightFlash = 1.0
											
											e\EventState3 = 2.0
										EndIf
									EndIf
								Else
									e\EventState3 = e\EventState3 + FPSfactor
									PointEntity(e\room\Objects[12], Collider)
									RotateEntity(e\room\Objects[12], 0.0, EntityYaw(e\room\Objects[12]), 0.0)
									
									Stamina = -5.0
									
									BlurTimer = Sin(e\EventState3 * 0.7) * 1000.0
									
									If KillTimer = 0.0 Then 
										CameraZoom(Camera, 1.0 + Sin(e\EventState3 * 0.8) * 0.2)
										
										Dist = EntityDistance(Collider, e\room\Objects[11])
										If Dist < 6.5 Then
											PositionEntity(Collider, CurveValue(EntityX(e\room\Objects[11], True), EntityX(Collider), Dist * 80.0), EntityY(Collider), CurveValue(EntityZ(e\room\Objects[0], True), EntityZ(Collider), Dist * 80.0))
										EndIf
									EndIf
									
									If e\EventState3 > 50.0 And e\EventState3 < 230.0 Then
										CameraShake = Sin(e\EventState3 - 50.0) * 3.0
										TurnEntity(e\room\Objects[13], 0.0, (Sin(e\EventState3 - 50.0) * (-0.85)) * FPSfactor, 0.0, True)
										TurnEntity(e\room\Objects[14], 0.0, (Sin(e\EventState3 - 50.0) * 0.85) * FPSfactor, 0.0, True)
										
										For i = 5 To 8
											PositionEntity(e\room\NPC[i]\Collider, CurveValue(EntityX(e\room\RoomDoors[2]\FrameOBJ, True), EntityX(e\room\NPC[i]\Collider, True), 50.0), EntityY(e\room\NPC[i]\Collider, True), CurveValue(EntityZ(e\room\RoomDoors[2]\FrameOBJ, True), EntityZ(e\room\NPC[i]\Collider, True), 50.0), True)
											ResetEntity(e\room\NPC[i]\Collider)
										Next
									EndIf
									
									If e\EventState3 >= 230.0 Then
										If e\EventState3 - FPSfactor < 230.0 Then
											e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\CI.ogg"))
										EndIf
										
										If ChannelPlaying(e\SoundCHN) = False And SelectedEnding = "" Then
											PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\Bell2.ogg"))
											
											p.Particles = CreateParticle(EntityX(e\room\Objects[11], True), EntityY(Camera, True), EntityZ(e\room\Objects[11], True), 4, 8.0, 0.0, 50.0)
											p\Speed = 0.15 : p\A = 0.5
											p.Particles = CreateParticle(EntityX(e\room\Objects[11], True), EntityY(Camera, True), EntityZ(e\room\Objects[11], True), 4, 8.0, 0.0, 50.0)
											p\Speed = 0.25 : p\A = 0.5
											
											SelectedEnding = "A1"
											chs\GodMode = 0
											chs\NoClip = 0
											chs\NoBlink = 0
											chs\InfiniteStamina = 0
											chs\Cheats = 0
											KillTimer = -0.1
											DeathMsg = ""
											Kill()
										EndIf
										
										If SelectedEnding <> "" Then
											CameraShake = CurveValue(2.0, CameraShake, 10.0)
											LightFlash = CurveValue(2.0, LightFlash, 8.0)
										EndIf
									EndIf
								EndIf
							Else 
								If e\EventState2 = 0.0 Then
									e\EventState2 = 1.0
									
									For i = 5 To 8
										e\room\NPC[i]\State = 3.0
										e\room\NPC[i]\PathStatus = FindPath(e\room\NPC[i], EntityX(e\room\OBJ) - 1.0 + 2.0 * (i Mod 2), EntityY(Collider) + 0.2, EntityZ(e\room\OBJ) - 2.0 * (i Mod 2))
										e\room\NPC[i]\PathTimer = 70 * Rnd(15.0, 20.0)
										e\room\NPC[i]\LastSeen = 70 * 300.0
									Next
								Else
									For i = 5 To 8
										If e\room\NPC[i]\State = 5.0
											e\room\NPC[i]\EnemyX = EntityX(Collider)
											e\room\NPC[i]\EnemyY = EntityY(Collider)
											e\room\NPC[i]\EnemyZ = EntityZ(Collider)
										Else
											If EntityDistance(e\room\NPC[i]\Collider, Collider) < 6.0
												e\room\NPC[i]\State = 5.0
												e\room\NPC[i]\CurrSpeed = 0.0
											EndIf
										EndIf
									Next
									
									If e\EventState2 =< 1.0 Then
										For i = 5 To 8
											If e\room\NPC[i]\State = 5.0 Then
												For Temp = 5 To 8
													e\room\NPC[Temp]\State = 5.0
													e\room\NPC[Temp]\EnemyX = EntityX(Collider)
													e\room\NPC[Temp]\EnemyY = EntityY(Collider)
													e\room\NPC[Temp]\EnemyZ = EntityZ(Collider)
													e\room\NPC[Temp]\PathTimer = 70 * Rnd(7.0, 10.0)
													e\room\NPC[Temp]\Reload = 2000.0
													UnableToMove = True
												Next
												
												If e\EventState2 = 1.0 Then
													e\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\Ending\GateA\STOPRIGHTTHERE.ogg"))
													e\EventState2 = 2.0			
												EndIf
											Else
												e\room\NPC[i]\LastSeen = 70 * 300.0
												e\room\NPC[i]\Reload = 2000.0
												e\room\NPC[i]\State3 = 70 * 145.0											
											EndIf
										Next										
									Else
										ShouldPlay = 0.0
										CurrSpeed = 0.0
										If ChannelPlaying(e\SoundCHN) = False Then
											PlaySound_Strict(IntroSFX(7))
											SelectedEnding = "A2"
											chs\GodMode = 0
											chs\NoClip = 0
											chs\NoBlink = 0
											chs\InfiniteStamina = 0
											chs\Cheats = 0
											LightFlash = 20.0
											KillTimer = -0.1
											DeathMsg = ""
											Kill()
											BlinkTimer = -10.0
											RemoveEvent(e)
											Exit
										EndIf
									EndIf									
								EndIf
							EndIf
						EndIf
					EndIf
				Else
					HideEntity(e\room\OBJ)
				EndIf
				;[End Block]
		End Select
	Next
	
	If ExplosionTimer > 0.0 Then
		ExplosionTimer = ExplosionTimer + FPSfactor
		If ExplosionTimer < 140.0 Then
			If ExplosionTimer - FPSfactor < 5.0 Then
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke1.ogg")
				PlaySound_Strict(ExplosionSFX)
				CameraShake = 10.0
				ExplosionTimer = 5.0
			EndIf
			CameraShake = CurveValue(ExplosionTimer / 60.0, CameraShake, 50.0)
		Else
			CameraShake = Min((ExplosionTimer / 20.0),20.0)
			If ExplosionTimer - FPSfactor < 140.0 Then
				BlinkTimer = 1.0
				ExplosionSFX = LoadSound_Strict("SFX\Ending\GateB\Nuke2.ogg")
				PlaySound_Strict(ExplosionSFX)			
				For i = 0 To (10 + (10 * (ParticleAmount + 1)))
					p.Particles = CreateParticle(EntityX(Collider) + Rnd(-0.5, 0.5), EntityY(Collider) - Rnd(0.2, 1.5), EntityZ(Collider) + Rnd(-0.5, 0.5), 0, Rnd(0.2, 0.6), 0.0, 350.0)
					p\Speed = Rnd(0.05, 0.07)
					RotateEntity(p\Pvt, -90.0, 0.0, 0.0, True)
				Next
			EndIf
			LightFlash = Min((ExplosionTimer - 140.0) / 10.0, 5.0)
			
			If ExplosionTimer > 160.0 Then KillTimer = Min(KillTimer, -0.1)
			If ExplosionTimer > 500.0 Then ExplosionTimer = 0.0
			
			; ~ A dirty workaround to prevent the collider from falling down into the facility once the nuke goes off,
			; ~ Causing the UpdateEvent function to be called again and crashing the game
			PositionEntity(Collider, EntityX(Collider), 200.0, EntityZ(Collider))
		EndIf
	EndIf
End Function

Function LoadEventSound(e.Events, File$, Number% = 0)
	If Number = 0 Then
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		e\Sound = LoadSound_Strict(File)
		Return(e\Sound)
	ElseIf Number = 1 Then
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		e\Sound2 = LoadSound_Strict(File)
		Return(e\Sound2)
	ElseIf Number = 2 Then
		If e\Sound3 <> 0 Then FreeSound_Strict(e\Sound3) : e\Sound3 = 0
		e\Sound3 = LoadSound_Strict(File)
		Return(e\Sound3)
	EndIf
End Function

Function RemoveEvent(e.Events)
	If e\Sound <> 0 Then FreeSound_Strict(e\Sound)
	If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2)
	If e\Sound3 <> 0 Then FreeSound_Strict(e\Sound3)
	If e\Img <> 0 Then FreeImage(e\Img)
	
	Delete(e)
End Function

Function IsItemGoodFor1162(itt.ItemTemplates)
	Local IN$ = itt\TempName
	
	Select itt\TempName
		Case "key1", "key2", "key3"
			;[Block]
			Return(True)
			;[End Block]
		Case "misc", "scp420j", "cigarette"
			;[Block]
			Return(True)
			;[End Block]
		Case "vest", "finevest","gasmask"
			;[Block]
			Return(True)
			;[End Block]
		Case "radio", "18vradio"
			;[Block]
			Return(True)
			;[End Block]
		Case "clipboard", "eyedrops", "nvgoggles"
			;[Block]
			Return(True)
			;[End Block]
		Case "drawing"
			;[Block]
			If itt\Img <> 0 Then FreeImage(itt\Img)	
			itt\Img = LoadImage_Strict(ItemsPath + "1048\1048_" + Rand(1, 26) + ".png") ; ~ Gives a random drawing.
			Return(True)
			;[End Block]
		Default
			;[Block]
			If itt\TempName <> "paper" Then
				Return(False)
			Else If Instr(itt\Name, "Leaflet")
				Return(False)
			Else
				; ~ If the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				; ~ (Because those are items created recently, which D-9341 has most likely never seen)
				Return(((Not Instr(itt\Name, "Note")) And (Not Instr(itt\Name, "Log"))))
			EndIf
			;[End Block]
	End Select
End Function

Function Update096ElevatorEvent#(e.Events, EventState#, d.Doors, ElevatorOBJ%)
	Local PrevEventState# = EventState
	
	If EventState < 0.0 Then
		EventState = 0.0
		PrevEventState = 0.0
	EndIf
	
	If d\OpenState = 0.0 And d\Open = False Then
		If Abs(EntityX(Collider) - EntityX(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
			If Abs(EntityZ(Collider) - EntityZ(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
				If Abs(EntityY(Collider) - EntityY(ElevatorOBJ, True)) =< 280.0 * RoomScale + (0.015 * FPSfactor) Then
					d\Locked = True
					If EventState = 0.0 Then
						TeleportEntity(Curr096\Collider, EntityX(d\FrameOBJ), EntityY(d\FrameOBJ) + 1.0, EntityZ(d\FrameOBJ), Curr096\CollRadius)
						PointEntity(Curr096\Collider, ElevatorOBJ)
						RotateEntity(Curr096\Collider, 0.0, EntityYaw(Curr096\Collider), 0.0)
						MoveEntity(Curr096\Collider, 0.0, 0.0, -0.5)
						ResetEntity(Curr096\Collider)
						Curr096\State = 6.0
						SetNPCFrame(Curr096, 0.0)
						e\Sound = LoadSound_Strict("SFX\SCP\096\ElevatorSlam.ogg")
						EventState = EventState + FPSfactor * 1.4
					EndIf
				EndIf
			EndIf
		EndIf
	EndIf
	
	If EventState > 0.0 Then
		If PrevEventState = 0.0 Then
			e\SoundCHN = PlaySound_Strict(e\Sound)
		EndIf
		
		If EventState > 70.0 * 1.9 And EventState < 70.0 * 2 + FPSfactor
			CameraShake = 7.0
		ElseIf EventState > 70 * 4.2 And EventState < 70.0 * 4.25 + FPSfactor
			CameraShake = 1.0
		ElseIf EventState > 70 * 5.9 And EventState < 70.0 * 5.95 + FPSfactor
			CameraShake = 1.0
		ElseIf EventState > 70 * 7.25 And EventState < 70.0 * 7.3 + FPSfactor
			CameraShake = 1.0
			d\FastOpen = True
			d\Open = True
			Curr096\State = 4.0
			Curr096\LastSeen = 1.0
		ElseIf EventState > 70 * 8.1 And EventState < 70.0 * 8.15 + FPSfactor
			CameraShake = 1.0
		EndIf
		
		If EventState =< 70 * 8.1 Then
			d\OpenState = Min(d\OpenState, 20.0)
		EndIf
		EventState = EventState + FPSfactor * 1.4
	EndIf
	Return(EventState)
End Function

;~IDEal Editor Parameters:
;~B#1218#1E51
;~C#Blitz3D