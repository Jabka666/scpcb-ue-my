Function SaveGame(File$)
	CatchErrors("Uncaught (SaveGame)")
	
	If (Not Playable) Then Return ; ~ Don't save if the player can't move at all
	
	If DropSpeed > 0.02 * fpst\FPSFactor[0] Or DropSpeed < (-0.02) * fpst\FPSFactor[0] Then Return
	
	If KillTimer < 0.0 Then Return
	
	GameSaved = True
	
	Local x%, y%, i%, Temp%
	Local n.NPCs, r.Rooms, do.Doors
	
	CreateDir(File)
	
	Local f% = WriteFile(File + "Save.txt")
	
	WriteString(f, CurrentTime())
	WriteString(f, CurrentDate())
	
	WriteInt(f, PlayTime)
	WriteFloat(f, EntityX(Collider))
	WriteFloat(f, EntityY(Collider))
	WriteFloat(f, EntityZ(Collider))
	
	WriteFloat(f, EntityX(Head))
	WriteFloat(f, EntityY(Head))
	WriteFloat(f, EntityZ(Head))
	
	WriteString(f, Str(AccessCode))
	
	WriteFloat(f, EntityPitch(Collider))
	WriteFloat(f, EntityYaw(Collider))
	
	WriteString(f, VersionNumber)
	
	WriteFloat(f, BlinkTimer)
	WriteFloat(f, BlinkEffect)
	WriteFloat(f, BlinkEffectTimer)
	
	WriteInt(f, DeathTimer)
	WriteInt(f, BlurTimer)
	WriteFloat(f, HealTimer)
	
	WriteByte(f, Crouch)
	
	WriteByte(f, ChanceToSpawn005)
	
	WriteFloat(f, Stamina)
	WriteFloat(f, StaminaEffect)
	WriteFloat(f, StaminaEffectTimer)
	
	WriteFloat(f, EyeStuck)
	WriteFloat(f, EyeIrritation)
	
	WriteFloat(f, Injuries)
	WriteFloat(f, Bloodloss)
	
	WriteFloat(f, PrevInjuries)
	WriteFloat(f, PrevBloodloss)
	
	WriteString(f, msg\DeathMsg)
	
	For i = 0 To 5
		WriteFloat(f, SCP1025State[i])
	Next
	
	WriteFloat(f, VomitTimer)
	WriteByte(f, Vomit)
	WriteFloat(f, CameraShakeTimer)
	WriteFloat(f, I_008\Timer)
	WriteFloat(f, I_409\Timer)
	
	For i = 0 To CUSTOM
		If SelectedDifficulty = difficulties(i) Then
			WriteByte(f, i)
			
			If (i = CUSTOM) Then
				WriteByte(f, SelectedDifficulty\AggressiveNPCs)
				WriteByte(f, SelectedDifficulty\PermaDeath)
				WriteByte(f, SelectedDifficulty\SaveType)
				WriteByte(f, SelectedDifficulty\OtherFactors)
			EndIf
		EndIf
	Next
	
	WriteFloat(f, MonitorTimer)
	
	WriteFloat(f, Sanity)
	
	WriteByte(f, WearingGasMask)
	WriteByte(f, WearingVest)
	WriteByte(f, WearingHelmet)
	WriteByte(f, WearingHazmat)
	
	WriteByte(f, WearingNightVision)
	WriteByte(f, I_1499\Using)
	WriteFloat(f, I_1499\PrevX)
	WriteFloat(f, I_1499\PrevY)
	WriteFloat(f, I_1499\PrevZ)
	WriteFloat(f, I_1499\x)
	WriteFloat(f, I_1499\y)
	WriteFloat(f, I_1499\z)
	If I_1499\PrevRoom <> Null
		WriteFloat(f, I_1499\PrevRoom\x)
		WriteFloat(f, I_1499\PrevRoom\z)
	Else
		WriteFloat(f, 0.0)
		WriteFloat(f, 0.0)
	EndIf
	
	WriteByte(f, SuperMan)
	WriteFloat(f, SuperManTimer)
	WriteByte(f, LightsOn)
	
	WriteString(f, RandomSeed)
	
	WriteFloat(f, SecondaryLightOn)
	WriteFloat(f, PrevSecondaryLightOn)
	WriteByte(f, RemoteDoorOn)
	WriteByte(f, SoundTransmission)
	
	For i = 0 To MAXACHIEVEMENTS - 1
		WriteByte(f, Achievements(i))
	Next
	WriteInt(f, RefinedItems)
	
	WriteInt(f, MapWidth)
	WriteInt(f, MapHeight)
	For Lvl = 0 To 0
		For x = 0 To MapWidth
			For y = 0 To MapHeight
				WriteInt(f, MapTemp(x, y))
				WriteByte(f, MapFound(x, y))
			Next
		Next
	Next
	
	WriteInt(f, 113)
	
	Temp = 0
	For  n.NPCs = Each NPCs
		Temp = Temp + 1
	Next	
	
	WriteInt(f, Temp)
	For n.NPCs = Each NPCs
		WriteByte(f, n\NPCtype)
		WriteFloat(f, EntityX(n\Collider, True))
		WriteFloat(f, EntityY(n\Collider, True))
		WriteFloat(f, EntityZ(n\Collider, True))
		
		WriteFloat(f, EntityPitch(n\Collider))
		WriteFloat(f, EntityYaw(n\Collider))
		WriteFloat(f, EntityRoll(n\Collider))
		
		WriteFloat(f, n\State)
		WriteFloat(f, n\State2)
		WriteFloat(f, n\State3)
		WriteInt(f, n\PrevState)
		
		WriteByte(f, n\Idle)
		WriteFloat(f, n\LastDist)
		WriteInt(f, n\LastSeen)
		
		WriteInt(f, n\CurrSpeed)
		
		WriteFloat(f, n\Angle)
		
		WriteFloat(f, n\Reload)
		
		WriteInt(f, n\ID)
		If n\Target <> Null Then
			WriteInt(f, n\Target\ID)		
		Else
			WriteInt(f, 0)
		EndIf
		
		WriteFloat(f, n\EnemyX)
		WriteFloat(f, n\EnemyY)
		WriteFloat(f, n\EnemyZ)
		
		WriteString(f, n\Texture)
		
		WriteFloat(f, AnimTime(n\OBJ))
		
		WriteByte(f, n\UseHeadphones)
		WriteByte(f, n\Contained)
		WriteInt(f, n\IsDead)
		WriteFloat(f, n\PathX)
		WriteFloat(f, n\PathZ)
		WriteInt(f, n\HP)
		WriteString(f, n\Model)
		WriteFloat(f, n\ModelScaleX)
		WriteFloat(f, n\ModelScaleY)
		WriteFloat(f, n\ModelScaleZ)
		WriteInt(f, n\TextureID)
	Next
	
	WriteFloat(f, MTFTimer)
	
	WriteInt(f, 632)
	
	WriteInt(f, room2gw_BrokenDoor)
	WriteFloat(f, room2gw_x)
	WriteFloat(f, room2gw_z)
	
	WriteByte(f, I_Zone\Transition[0])
	WriteByte(f, I_Zone\Transition[1])
	WriteByte(f, I_Zone\HasCustomForest)
	WriteByte(f, I_Zone\HasCustomMT)
	
	Temp = 0
	For r.Rooms = Each Rooms
		Temp = Temp + 1
	Next	
	WriteInt(f, Temp)	
	For r.Rooms = Each Rooms
		WriteInt(f, r\RoomTemplate\ID)
		WriteInt(f, r\Angle)
		WriteFloat(f, r\x)
		WriteFloat(f, r\y)
		WriteFloat(f, r\z)
		
		WriteByte(f, r\Found)
		
		WriteInt(f, r\Zone)
		
		If PlayerRoom = r Then 
			WriteByte(f, 1)
		Else 
			WriteByte(f, 0)
		EndIf
		
		For i = 0 To 11
			If r\NPC[i] = Null Then
				WriteInt(f, 0)
			Else
				WriteInt(f, r\NPC[i]\ID)
			EndIf
		Next
		
		For i = 0 To 10
			If r\Levers[i] <> 0 Then
				If EntityPitch(r\Levers[i], True) > 0 Then
					WriteByte(f, 1)
				Else
					WriteByte(f, 0)
				EndIf	
			EndIf
		Next
		WriteByte(f, 2)
		
		If r\grid = Null Then ; ~ This room doesn't have a grid
			WriteByte(f, 0)
		Else ; ~ This room has a grid
			WriteByte(f, 1)
			For y = 0 To GridSZ - 1
				For x = 0 To GridSZ - 1
					WriteByte(f, r\grid\Grid[x + (y * GridSZ)])
					WriteByte(f, r\grid\Angles[x + (y * GridSZ)])
				Next
			Next
		EndIf
		
		If r\fr = Null Then ; ~ This room doesn't have a forest
			WriteByte(f, 0)
		Else ; ~ This room has a forest
			If (Not I_Zone\HasCustomForest) Then
				WriteByte(f, 1)
			Else
				WriteByte(f, 2)
			EndIf
			For y = 0 To GridSize - 1
				For x = 0 To GridSize - 1
					WriteByte(f, r\fr\Grid[x + (y * GridSize)])
				Next
			Next
			WriteFloat(f, EntityX(r\fr\Forest_Pivot, True))
			WriteFloat(f, EntityY(r\fr\Forest_Pivot, True))
			WriteFloat(f, EntityZ(r\fr\Forest_Pivot, True))
		EndIf
	Next
	
	WriteInt(f, 954)
	
	Temp = 0
	For do.Doors = Each Doors
		Temp = Temp + 1	
	Next	
	WriteInt(f, Temp)	
	For do.Doors = Each Doors
		WriteFloat(f, EntityX(do\FrameOBJ, True))
		WriteFloat(f, EntityY(do\FrameOBJ, True))
		WriteFloat(f, EntityZ(do\FrameOBJ, True))
		WriteByte(f, do\Open)
		WriteFloat(f, do\OpenState)
		WriteByte(f, do\Locked)
		WriteByte(f, do\AutoClose)
		
		WriteFloat(f, EntityX(do\OBJ, True))
		WriteFloat(f, EntityZ(do\OBJ, True))
		
		If do\OBJ2 <> 0 Then
			WriteFloat(f, EntityX(do\OBJ2, True))
			WriteFloat(f, EntityZ(do\OBJ2, True))
		Else
			WriteFloat(f, 0.0)
			WriteFloat(f, 0.0)
		End If
		
		WriteFloat(f, do\Timer)
		WriteFloat(f, do\TimerState)
		
		WriteByte(f, do\IsElevatorDoor)
		WriteByte(f, do\MTFClose)
	Next
	
	WriteInt(f, 1845)
	
	Local d.Decals
	
	Temp = 0
	For d.Decals = Each Decals
		Temp = Temp + 1
	Next	
	WriteInt(f, Temp)
	For d.Decals = Each Decals
		WriteInt(f, d\ID)
		
		WriteFloat(f, EntityX(d\OBJ, True))
		WriteFloat(f, EntityY(d\OBJ, True))
		WriteFloat(f, EntityZ(d\OBJ, True))
		
		WriteFloat(f, EntityPitch(d\OBJ, True))
		WriteFloat(f, EntityYaw(d\OBJ, True))
		WriteFloat(f, EntityRoll(d\OBJ, True))
		
		WriteByte(f, d\BlendMode)
		WriteInt(f, d\FX)
		
		WriteFloat(f, d\Size)
		WriteFloat(f, d\Alpha)
		WriteFloat(f, d\AlphaChange)
		WriteFloat(f, d\Timer)
		WriteFloat(f, d\LifeTime)
	Next
	
	Local e.Events
	
	Temp = 0
	For e.Events = Each Events
		Temp = Temp + 1
	Next	
	WriteInt(f, Temp)
	For e.Events = Each Events
		WriteString(f, e\EventName)
		WriteFloat(f, e\EventState)
		WriteFloat(f, e\EventState2)	
		WriteFloat(f, e\EventState3)
		WriteFloat(f, e\EventState4)
		WriteFloat(f, EntityX(e\room\OBJ))
		WriteFloat(f, EntityZ(e\room\OBJ))
		WriteString(f, e\EventStr)
	Next
	
	Temp = 0
	For it.Items = Each Items	
		Temp = Temp + 1
	Next
	WriteInt(f, Temp)
	For it.Items = Each Items
		WriteString(f, it\ItemTemplate\Name)
		WriteString(f, it\ItemTemplate\TempName)
		
		WriteString(f, it\Name)
		
		WriteFloat(f, EntityX(it\Collider, True))
		WriteFloat(f, EntityY(it\Collider, True))
		WriteFloat(f, EntityZ(it\Collider, True))
		
		WriteByte(f, it\R)
		WriteByte(f, it\G)
		WriteByte(f, it\B)
		WriteFloat(f, it\A)
		
		WriteFloat(f, EntityPitch(it\Collider))
		WriteFloat(f, EntityYaw(it\Collider))
		
		WriteFloat(f, it\State)
		WriteByte(f, it\Picked)
		
		If SelectedItem = it Then 
			WriteByte(f, 1) 
		Else 
			WriteByte(f, 0)
		EndIf
		
		Local ItemFound% = False
		
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) = it Then ItemFound = True : Exit
		Next
		If ItemFound Then 
			WriteByte(f, i) 
		Else 
			WriteByte(f, 66)
		EndIf
		
		If it\ItemTemplate\IsAnim <> 0 Then
			WriteFloat(f, AnimTime(it\Model))
		EndIf
		WriteByte(f, it\InvSlots)
		WriteInt(f, it\ID)
		If it\ItemTemplate\InvImg = it\InvImg Then 
			WriteByte(f, 0) 
		Else 
			WriteByte(f, 1)
		EndIf
	Next
	
	Temp = 0
	For it.Items = Each Items
		If it\InvSlots > 0 Then Temp = Temp + 1
	Next
	
	WriteInt(f, Temp)
	
	For it.Items = Each Items
		If it\InvSlots > 0 Then
			WriteInt(f, it\ID)
			For i = 0 To it\InvSlots - 1
				If it\SecondInv[i] <> Null Then
					WriteInt(f, it\SecondInv[i]\ID)
				Else
					WriteInt(f, -1)
				EndIf
			Next
		EndIf
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		WriteByte(f, itt\Found)
	Next
	
	If UsedConsole Then
		WriteInt(f, 100)
	Else
		WriteInt(f, 994)
	EndIf
	WriteFloat(f, CameraFogFar)
	WriteFloat(f, StoredCameraFogFar)
	WriteByte(f, I_427\Using)
	WriteFloat(f, I_427\Timer)
	
	WriteByte(f, I_714\Using)
	
	CloseFile(f)
	
	If Not MenuOpen Then
		If SelectedDifficulty\SaveType = SAVEONSCREENS Then
			PlaySound_Strict(LoadTempSound("SFX\General\Save2.ogg"))
		Else
			PlaySound_Strict(LoadTempSound("SFX\General\Save1.ogg"))
		EndIf
		
		msg\Msg = "Game progress saved."
		msg\Timer = 70.0 * 4.0
	EndIf
	
	CatchErrors("SaveGame")
End Function

Function LoadGame(File$)
	CatchErrors("Uncaught (LoadGame)")
	
	DropSpeed = 0.0
	
	DebugHUD = False
	
	GameSaved = True
	
	Local x#, y#, z#, i%, Temp%, StrTemp$, r.Rooms, ID%, n.NPCs, do.Doors
	Local f% = ReadFile(File + "Save.txt")
	
	StrTemp = ReadString(f)
	StrTemp = ReadString(f)
	
	PlayTime = ReadInt(f)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y + 0.05, z)
	ResetEntity(Collider)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y + 0.05, z)
	ResetEntity(Head)
	
	AccessCode = Int(ReadString(f))
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0.0, 0.0)
	
	StrTemp = ReadString(f)
	If (StrTemp <> VersionNumber) Then RuntimeError("The save files of v" + StrTemp + " aren't compatible with SCP - Containment Breach Ultimate Edition v" + VersionNumber + ".")
	
	BlinkTimer = ReadFloat(f)
	BlinkEffect = ReadFloat(f)	
	BlinkEffectTimer = ReadFloat(f)
	
	DeathTimer = ReadInt(f)	
	BlurTimer = ReadInt(f)	
	HealTimer = ReadFloat(f)
	
	Crouch = ReadByte(f)
	
	ChanceToSpawn005 = ReadByte(f)
	
	Stamina = ReadFloat(f)
	StaminaEffect = ReadFloat(f)	
	StaminaEffectTimer = ReadFloat(f)	
	
	EyeStuck = ReadFloat(f)
	EyeIrritation = ReadFloat(f)
	
	Injuries = ReadFloat(f)
	Bloodloss = ReadFloat(f)
	
	PrevInjuries = ReadFloat(f)
	PrevBloodloss = ReadFloat(f)
	
	msg\DeathMsg = ReadString(f)
	
	For i = 0 To 5
		SCP1025State[i] = ReadFloat(f)
	Next
	
	VomitTimer = ReadFloat(f)
	Vomit = ReadByte(f)
	CameraShakeTimer = ReadFloat(f)
	I_008\Timer = ReadFloat(f)
	I_409\Timer = ReadFloat(f)
	
	Local DifficultyIndex = ReadByte(f)
	
	SelectedDifficulty = difficulties(DifficultyIndex)
	If (DifficultyIndex = CUSTOM) Then
		SelectedDifficulty\AggressiveNPCs = ReadByte(f)
		SelectedDifficulty\PermaDeath = ReadByte(f)
		SelectedDifficulty\SaveType	= ReadByte(f)
		SelectedDifficulty\OtherFactors = ReadByte(f)
	EndIf
	
	MonitorTimer = ReadFloat(f)
	
	Sanity = ReadFloat(f)
	
	WearingGasMask = ReadByte(f)
	WearingVest = ReadByte(f)
	WearingHelmet = ReadByte(f)
	WearingHazmat = ReadByte(f)
	
	WearingNightVision = ReadByte(f)
	I_1499\Using = ReadByte(f)
	I_1499\PrevX = ReadFloat(f)
	I_1499\PrevY = ReadFloat(f)
	I_1499\PrevZ = ReadFloat(f)
	I_1499\x = ReadFloat(f)
	I_1499\y = ReadFloat(f)
	I_1499\z = ReadFloat(f)
	
	Local r1499_x# = ReadFloat(f)
	Local r1499_z# = ReadFloat(f)
	
	SuperMan = ReadByte(f)
	SuperManTimer = ReadFloat(f)
	LightsOn = ReadByte(f)
	
	RandomSeed = ReadString(f)
	
	SecondaryLightOn = ReadFloat(f)
	PrevSecondaryLightOn = ReadFloat(f)
	RemoteDoorOn = ReadByte(f)
	SoundTransmission = ReadByte(f)	
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Achievements(i) = ReadByte(f)
	Next
	RefinedItems = ReadInt(f)
	
	MapWidth = ReadInt(f)
	MapHeight = ReadInt(f)
	For x = 0 To MapWidth 
		For y = 0 To MapHeight
			MapTemp(x, y) = ReadInt(f)
			MapFound(x, y) = ReadByte(f)
		Next
	Next
	
	If ReadInt(f) <> 113 Then RuntimeError("Couldn't load the game, save file corrupted (error 2.5)")
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local NPCtype% = ReadByte(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				;[Block]
				Curr173 = n
				;[End Block]
			Case NPCtype106
				;[Block]
				Curr106 = n
				;[End Block]
			Case NPCtype096
				;[Block]
				Curr096 = n
				;[End Block]
			Case NPCtype513_1
				;[Block]
				Curr513_1 = n
				;[End Block]
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\Texture = ReadString(f)
		If n\Texture <> "" Then
			Tex = LoadTexture_Strict(n\Texture)
			EntityTexture(n\OBJ, Tex)
		EndIf
		
		Local Frame# = ReadFloat(f)
		
		Select NPCtype
			Case NPCtype106, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtype049_2, NPCtypeClerk, NPCtype008_1
				;[Block]
				SetAnimTime(n\OBJ, Frame)
				;[End Block]
		End Select
		
		n\Frame = Frame
		n\UseHeadphones = ReadByte(f)
		n\Contained = ReadByte(f)
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX = ReadFloat(f)
		n\ModelScaleY = ReadFloat(f)
		n\ModelScaleZ = ReadFloat(f)
		If n\Model <> ""
			FreeEntity(n\OBJ)
			n\OBJ = LoadAnimMesh_Strict(n\Model)
			ScaleEntity(n\OBJ, n\ModelScaleX, n\ModelScaleY, n\ModelScaleZ)
			SetAnimTime(n\OBJ, Frame)
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs, n\TextureID - 1)
			SetAnimTime(n\OBJ, Frame)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.NPCs = Each NPCs
				If n2 <> n Then
					If n2\ID = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	MTFTimer = ReadFloat(f)
	
	If ReadInt(f) <> 632 Then RuntimeError("Couldn't load the game, save file corrupted (error 1)")
	
	room2gw_BrokenDoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	I_Zone\Transition[0] = ReadByte(f)
	I_Zone\Transition[1] = ReadByte(f)
	I_Zone\HasCustomForest = ReadByte(f)
	I_Zone\HasCustomMT = ReadByte(f)
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local RoomTemplateID% = ReadInt(f)
		Local Angle% = ReadInt(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Found = ReadByte(f)
		
		Level = ReadInt(f)
		
		Temp2 = ReadByte(f)		
		
		Angle = WrapAngle(Angle)
		
		For rt.RoomTemplates = Each RoomTemplates
			If rt\ID = RoomTemplateID Then
				r.Rooms = CreateRoom(Level, rt\Shape, x, y, z, rt\Name)
				TurnEntity(r\OBJ, 0.0, Angle, 0.0)
				r\Angle = Angle
				r\Found = Found
				Exit
			End If
		Next
		
		If Temp2 = 1 Then PlayerRoom = r.Rooms
		
		For x = 0 To 11
			ID = ReadInt(f)
			If ID > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = ID Then r\NPC[x] = n : Exit
				Next
			EndIf
		Next
		
		For x = 0 To 11
			ID = ReadByte(f)
			If ID = 2 Then
				Exit
			Else If ID = 1 Then
				RotateEntity(r\Levers[x], 78.0, EntityYaw(r\Levers[x]), 0.0)
			Else
				RotateEntity(r\Levers[x], -78.0, EntityYaw(r\Levers[x]), 0.0)
			EndIf
		Next
		
		If ReadByte(f) = 1 Then ; ~ This room has a grid
			If r\grid <> Null Then ; ~ Remove the old grid content
				For x = 0 To GridSZ - 1
					For y = 0 To GridSZ - 1
						If r\grid\Entities[x + (y * GridSZ)] <> 0 Then
							FreeEntity(r\grid\Entities[x + (y * GridSZ)])
							r\grid\Entities[x + (y * GridSZ)] = 0
						EndIf
						If r\grid\waypoints[x + (y * Gridsz)] <> Null Then
							RemoveWaypoint(r\grid\waypoints[x + (y * GridSZ)])
							r\grid\waypoints[x + (y * GridSZ)] = Null
						EndIf
					Next
				Next
				For x = 0 To 5
					If r\grid\Meshes[x] <> 0 Then
						FreeEntity(r\grid\Meshes[x])
						r\grid\Meshes[x] = 0
					EndIf
				Next
				Delete(r\grid)
			EndIf
			r\grid = New Grids
			For y = 0 To GridSZ - 1
				For x = 0 To GridSZ - 1
					r\grid\Grid[x + (y * GridSZ)] = ReadByte(f)
					r\grid\Angles[x + (y * GridSZ)] = ReadByte(f)
					; ~ Get only the necessary data, make the event handle the meshes and waypoints separately
				Next
			Next
		EndIf
		
		Local HasForest = ReadByte(f)
		
		If HasForest > 0 Then ; ~ This room has a forest
			If r\fr <> Null Then ; ~ Remove the old forest
				DestroyForest(r\fr)
			Else
				r\fr = New Forest
			EndIf
			For y = 0 To GridSize - 1
				Local sssss$ = ""
				
				For x = 0 To GridSize - 1
					r\fr\Grid[x + (y * GridSize)] = ReadByte(f)
					sssss = sssss + Str(r\fr\Grid[x + (y * GridSize)])
				Next
			Next
			lx# = ReadFloat(f)
			ly# = ReadFloat(f)
			lz# = ReadFloat(f)
			If HasForest = 1 Then
				PlaceForest(r\fr, lx, ly, lz, r)
			Else
				PlaceForest_MapCreator(r\fr, lx, ly, lz, r)
			EndIf
		ElseIf r\fr <> Null Then ; ~ Remove the old forest
			DestroyForest(r\fr)
			Delete(r\fr)
		EndIf
	Next
	
	For r.Rooms = Each Rooms
		If r\x = r1499_x And r\z = r1499_z
			I_1499\PrevRoom = r
			Exit
		EndIf
	Next
	
	If ReadInt(f) <> 954 Then RuntimeError("Couldn't load the game, save file may be corrupted (error 2)")
	
	Local Spacing# = 8.0
	Local Zone%, ShouldSpawnDoor%
	For y = MapHeight To 0 Step -1
		If y < I_Zone\Transition[1] - (SelectedMap = "") Then
			Zone = 3
		ElseIf y >= I_Zone\Transition[1] - (SelectedMap = "") And y < I_Zone\Transition[0] - (SelectedMap = "") Then
			Zone = 2
		Else
			Zone = 1
		EndIf
		
		For x = MapWidth To 0 Step -1
			If MapTemp(x, y) > 0 Then
				If Zone = 2 Then Temp = 2 Else Temp = 0
                
                For r.Rooms = Each Rooms
					r\Angle = WrapAngle(r\Angle)
					If Int(r\x / 8.0) = x And Int(r\z / 8.0) = y Then
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 90 Or r\Angle = 270
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 0 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 0 Or r\Angle = 180 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If (x + 1) < (MapWidth + 1)
								If MapTemp(x + 1, y) > 0 Then
									do.Doors = CreateDoor(r\Zone, Float(x) * Spacing + Spacing / 2.0, 0.0, Float(y) * Spacing, 90.0, r, Max(Rand(-3, 1), 0), Temp)
									r\AdjDoor[0] = do
								EndIf
							EndIf
						EndIf
						
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 180
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 0 Or r\Angle = 180
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 180 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 180 Or r\Angle = 90 Or r\Angle = 270
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If (y + 1) < (MapHeight + 1)
								If MapTemp(x, y + 1) > 0 Then
									do.Doors = CreateDoor(r\Zone, Float(x) * Spacing, 0.0, Float(y) * Spacing + Spacing / 2.0, 0.0, r, Max(Rand(-3, 1), 0), Temp)
									r\AdjDoor[3] = do
								EndIf
							EndIf
						EndIf
						Exit
					EndIf
                Next
			End If
		Next
	Next
	
	Temp = ReadInt(f)
	
	For i = 1 To Temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local Open% = ReadByte(f)
		Local OpenState# = ReadFloat(f)
		Local Locked% = ReadByte(f)
		Local AutoClose% = ReadByte(f)
		
		Local OBJX# = ReadFloat(f)
		Local OBJZ# = ReadFloat(f)
		
		Local OBJ2X# = ReadFloat(f)
		Local OBJ2Z# = ReadFloat(f)
		
		Local Timer% = ReadFloat(f)
		Local TimerState# = ReadFloat(f)
		
		Local IsElevDoor = ReadByte(f)
		Local MTFClose = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\FrameOBJ, True) = x And EntityY(do\FrameOBJ, True) = y And EntityZ(do\FrameOBJ, True) = z Then
				do\Open = Open
				do\OpenState = OpenState
				do\Locked = Locked
				do\AutoClose = AutoClose
				do\Timer = Timer
				do\TimerState = TimerState
				do\IsElevatorDoor = IsElevDoor
				do\MTFClose = MTFClose
				
				PositionEntity(do\OBJ, OBJX, y, OBJZ, True)
				If do\OBJ2 <> 0 Then PositionEntity(do\OBJ2, OBJ2X, y, OBJ2Z, True)
				Exit
			End If
		Next		
	Next
	
	InitWayPoints()
	
	If ReadInt(f) <> 1845 Then RuntimeError("Couldn't load the game, save file corrupted (error 3)")
	
	Local d.Decals
	
	For d.Decals = Each Decals
		FreeEntity(d\OBJ)
		Delete(d)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		ID = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local Pitch# = ReadFloat(f)
		Local Yaw# = ReadFloat(f)
		Local Roll# = ReadFloat(f)
		
		d.Decals = CreateDecal(ID, x, y, z, Pitch, Yaw, Roll)
		d\BlendMode = ReadByte (f)
		d\FX = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\LifeTime = ReadFloat(f)
		
		ScaleSprite(d\OBJ, d\Size, d\Size)
		EntityBlend(d\OBJ, d\BlendMode)
		EntityFX(d\OBJ, d\FX)
	Next
	UpdateDecals()
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local e.Events = New Events
		
		e\EventName = ReadString(f)
		
		e\EventState = ReadFloat(f)
		e\EventState2 = ReadFloat(f)		
		e\EventState3 = ReadFloat(f)
		e\EventState4 = ReadFloat(f)
		x = ReadFloat(f)
		z = ReadFloat(f)
		For  r.Rooms = Each Rooms
			If EntityX(r\OBJ) = x And EntityZ(r\OBJ) = z Then
				e\room = r
				Exit
			EndIf
		Next
		e\EventStr = ReadString(f)
	Next
	
	For e.Events = Each Events
		; ~ Reset for the monitor loading and stuff for room2sl
		If e\EventName = "room2sl"
			e\EventState = 0.0
			e\EventStr = ""
		; ~ Reset dimension1499
		ElseIf e\EventName = "dimension1499"
			If e\EventState > 0.0
				e\EventState = 0.0
				e\EventStr = ""
				HideChunks()
				DeleteChunks()
				For n.NPCs = Each NPCs
					If n\NPCtype = NPCtype1499_1
						If n\InFacility = 0
							RemoveNPC(n)
						EndIf
					EndIf
				Next
				
				Local du.Dummy1499_1
				
				For du.Dummy1499_1 = Each Dummy1499_1
					Delete(du)
				Next
			EndIf
		; ~ Reset the forest event to make it loading properly
		ElseIf e\EventName = "room860"
			e\EventStr = ""
		ElseIf e\EventName = "room205"
			e\EventStr = ""
		ElseIf e\EventName = "room106"
			If e\EventState2 = False Then
				PositionEntity(e\room\Objects[6], EntityX(e\room\Objects[6], True), -1280.0 * RoomScale, EntityZ(e\room\Objects[6], True), True)
			EndIf
		EndIf
	Next
	
	Local it.Items
	
	For it.Items = Each Items
		RemoveItem(it)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local IttName$ = ReadString(f)
		Local TempName$ = ReadString(f)
		Local Name$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Red = ReadByte(f)
		Green = ReadByte(f)
		Blue = ReadByte(f)		
		A = ReadFloat(f)
		
		it.Items = CreateItem(IttName, TempName, x, y, z, Red, Green, Blue, A)
		it\Name = Name
		
		EntityType(it\Collider, HIT_ITEM)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\Collider, x, y, 0.0)
		
		it\State = ReadFloat(f)
		it\Picked = ReadByte(f)
		If it\Picked Then HideEntity(it\Collider)
		
		nt = ReadByte(f)
		If nt = True Then SelectedItem = it
		
		nt = ReadByte(f)
		If nt < 66
			Inventory(nt) = it
			ItemAmount = ItemAmount + 1
		EndIf
		
		For itt.ItemTemplates = Each ItemTemplates
			If (itt\TempName = TempName) And (itt\Name = IttName) Then
				If itt\IsAnim <> 0 Then SetAnimTime(it\Model, ReadFloat(f)) : Exit
			EndIf
		Next
		it\InvSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID > LastItemID Then LastItemID = it\ID
		
		If ReadByte(f) = 0 Then
			it\InvImg = it\ItemTemplate\InvImg
		Else
			it\InvImg = it\ItemTemplate\InvImg2
		EndIf
	Next	
	
	Local o_i%
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		o_i = ReadInt(f)
		
		For ij.Items = Each Items
			If ij\ID = o_i Then it.Items = ij : Exit
		Next
		For j% = 0 To it\InvSlots - 1
			o_i = ReadInt(f)
			If o_i <> -1 Then
				For ij.Items = Each Items
					If ij\ID = o_i Then
						it\SecondInv[j] = ij
						Exit
					EndIf
				Next
			EndIf
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\Found = ReadByte(f)
	Next
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			Dist# = 20.0
			
			Local closestroom.Rooms
			
			For r.Rooms = Each Rooms
				Dist2# = EntityDistance(r\OBJ, do\OBJ)
				If Dist2 < Dist Then
					Dist = Dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	If ReadInt(f) <> 994
		UsedConsole = True
	EndIf
	
	CameraFogFar = ReadFloat(f)
    StoredCameraFogFar = ReadFloat(f)
	If CameraFogFar = 0.0 Then
		CameraFogFar = 6.0
	EndIf
	I_427\Using = ReadByte(f)
	I_427\Timer = ReadFloat(f)
	
	I_714\Using = ReadByte(f)
	
	CloseFile(f)
	
	For r.Rooms = Each Rooms
		r\Adjacent[0] = Null
		r\Adjacent[1] = Null
		r\Adjacent[2] = Null
		r\Adjacent[3] = Null
		For r2.Rooms = Each Rooms
			If r <> r2 Then
				If r2\z = r\z Then
					If (r2\x) = (r\x + 8.0) Then
						r\Adjacent[0] = r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf (r2\x) = (r\x - 8.0)
						r\Adjacent[2] = r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x = r\x Then
					If (r2\z) = (r\z - 8.0) Then
						r\Adjacent[1] = r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf (r2\z) = (r\z + 8.0)
						r\Adjacent[3] = r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0] <> Null) And (r\Adjacent[1] <> Null) And (r\Adjacent[2] <> Null) And (r\Adjacent[3] <> Null) Then Exit
		Next
		
		For do.Doors = Each Doors
			If (do\KeyCard = 0) And (do\Code = "") Then
				If EntityZ(do\FrameOBJ, True) = r\z Then
					If EntityX(do\FrameOBJ, True) = r\x + 4.0 Then
						r\AdjDoor[0] = do
					ElseIf EntityX(do\FrameOBJ, True) = r\x - 4.0 Then
						r\AdjDoor[2] = do
					EndIf
				ElseIf EntityX(do\FrameOBJ, True) = r\x Then
					If EntityZ(do\FrameOBJ, True) = r\z + 4.0 Then
						r\AdjDoor[3] = do
					ElseIf EntityZ(do\FrameOBJ, True) = r\z - 4.0 Then
						r\AdjDoor[1] = do
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	If PlayerRoom\RoomTemplate\Name = "dimension1499"
		BlinkTimer = -1.0
		ShouldEntitiesFall = False
		PlayerRoom = I_1499\PrevRoom
		UpdateDoors()
		UpdateRooms()
		For it.Items = Each Items
			it\DistTimer = 0.0
		Next
	EndIf
	
	If Collider <> 0 Then
		If PlayerRoom <> Null Then
			ShowEntity(PlayerRoom\OBJ)
		EndIf
		ShowEntity(Collider)
		TeleportEntity(Collider, EntityX(Collider), EntityY(Collider) + 0.5, EntityZ(Collider), 0.3, True)
		If PlayerRoom <> Null Then
			HideEntity(PlayerRoom\OBJ)
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0.0
	
	CatchErrors("LoadGame")
End Function

Function LoadGameQuick(File$)
	CatchErrors("Uncaught (LoadGameQuick)")
	
	DebugHUD = False
	GameSaved = True
	IsZombie = False
	DeafPlayer = False
	DeafTimer = 0.0
	UnableToMove = False
	msg\Msg = ""
	SelectedEnding = ""
	
	PositionEntity(Collider, 0.0, 1000.0, 0.0, True)
	ResetEntity(Collider)
	
	Local x#, y#, z#, i%, Temp%, StrTemp$, ID%
	Local Player_X#, Player_Y#, Player_Z#, r.Rooms, n.NPCs, do.Doors
	Local f% = ReadFile(File + "Save.txt")
	
	StrTemp = ReadString(f)
	StrTemp = ReadString(f)
	
	DropSpeed = -0.1
	HeadDropSpeed = 0.0
	Shake = 0.0
	CurrSpeed = 0.0
	
	HeartBeatVolume = 0.0
	
	CameraShake = 0.0
	Shake = 0.0
	LightFlash = 0.0
	BlurTimer = 0.0
	
	KillTimer = 0.0
	FallTimer = 0.0
	MenuOpen = False
	
	chs\Cheats = 0
	chs\GodMode = 0
	chs\InfiniteStamina = 0
	chs\NoBlink = 0
	chs\NoClip = 0
	chs\NoTarget = 0
	WireFrame(0)
	
	PlayTime = ReadInt(f)
	
	HideEntity(Collider)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y + 0.05, z)
	
	ShowEntity(Collider)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y + 0.05, z)
	ResetEntity(Head)
	
	AccessCode = Int(ReadString(f))
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0.0, 0.0)
	
	StrTemp = ReadString(f)
	If (StrTemp <> VersionNumber) Then RuntimeError("The save files of v" + StrTemp + " aren't compatible with SCP - Containment Breach Ultimate Edition v" + VersionNumber + ".")
	
	BlinkTimer = ReadFloat(f)
	BlinkEffect = ReadFloat(f)	
	BlinkEffectTimer = ReadFloat(f)	
	
	DeathTimer = ReadInt(f)	
	BlurTimer = ReadInt(f)	
	HealTimer = ReadFloat(f)
	
	Crouch = ReadByte(f)
	
	ChanceToSpawn005 = ReadByte(f)
	
	Stamina = ReadFloat(f)
	StaminaEffect = ReadFloat(f)	
	StaminaEffectTimer = ReadFloat(f)	
	
	EyeStuck = ReadFloat(f)
	EyeIrritation = ReadFloat(f)
	
	Injuries = ReadFloat(f)
	Bloodloss = ReadFloat(f)
	
	PrevInjuries = ReadFloat(f)
	PrevBloodloss = ReadFloat(f)
	
	msg\DeathMsg = ReadString(f)
	
	For i = 0 To 5
		SCP1025State[i] = ReadFloat(f)
	Next
	
	VomitTimer = ReadFloat(f)
	Vomit = ReadByte(f)
	CameraShakeTimer = ReadFloat(f)
	I_008\Timer = ReadFloat(f)
	I_409\Timer = ReadFloat(f)
	
	Local DifficultyIndex = ReadByte(f)
	
	SelectedDifficulty = difficulties(DifficultyIndex)
	If (DifficultyIndex = CUSTOM) Then
		SelectedDifficulty\AggressiveNPCs = ReadByte(f)
		SelectedDifficulty\PermaDeath = ReadByte(f)
		SelectedDifficulty\SaveType	= ReadByte(f)
		SelectedDifficulty\OtherFactors = ReadByte(f)
	EndIf
	
	MonitorTimer = ReadFloat(f)
	
	Sanity = ReadFloat(f)
	
	WearingGasMask = ReadByte(f)
	WearingVest = ReadByte(f)
	WearingHelmet = ReadByte(f)
	WearingHazmat = ReadByte(f)
	
	WearingNightVision = ReadByte(f)
	I_1499\Using = ReadByte(f)
	I_1499\PrevX = ReadFloat(f)
	I_1499\PrevY = ReadFloat(f)
	I_1499\PrevZ = ReadFloat(f)
	I_1499\x = ReadFloat(f)
	I_1499\y = ReadFloat(f)
	I_1499\z = ReadFloat(f)
	
	Local r1499_x# = ReadFloat(f)
	Local r1499_z# = ReadFloat(f)
	
	SuperMan = ReadByte(f)
	SuperManTimer = ReadFloat(f)
	LightsOn = ReadByte(f)
	
	RandomSeed = ReadString(f)
	
	SecondaryLightOn = ReadFloat(f)
	PrevSecondaryLightOn = ReadFloat(f)
	RemoteDoorOn = ReadByte(f)
	SoundTransmission = ReadByte(f)	
	
	For i = 0 To MAXACHIEVEMENTS - 1
		Achievements(i) = ReadByte(f)
	Next
	RefinedItems = ReadInt(f)
	
	MapWidth = ReadInt(f)
	MapHeight = ReadInt(f)
	For x = 0 To MapWidth
		For y = 0 To MapHeight
			MapTemp(x, y) = ReadInt(f)
			MapFound(x, y) = ReadByte(f)
		Next
	Next
	
	If ReadInt(f) <> 113 Then RuntimeError("Couldn't load the game, save file corrupted (error 2.5)")
	
	For n.NPCs = Each NPCs
		RemoveNPC(n)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local NPCtype% = ReadByte(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				;[Block]
				Curr173 = n
				;[End Block]
			Case NPCtype106
				;[Block]
				Curr106 = n
				;[End Block]
			Case NPCtype096
				;[Block]
				Curr096 = n
				;[End Block]
			Case NPCtype513_1
				;[Block]
				Curr513_1 = n
				;[End Block]
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\Texture = ReadString(f)
		If n\Texture <> "" Then
			Tex = LoadTexture_Strict(n\Texture)
			EntityTexture(n\OBJ, Tex)
		EndIf
		
		Local Frame# = ReadFloat(f)
		
		Select NPCtype
			Case NPCtype106, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtype049_2, NPCtypeClerk, NPCtype008_1
				;[Block]
				SetAnimTime(n\OBJ, Frame)
				;[End Block]
		End Select		
		
		n\Frame = Frame
		n\UseHeadphones = ReadByte(f)
		n\Contained = ReadByte(f)
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX = ReadFloat(f)
		n\ModelScaleY = ReadFloat(f)
		n\ModelScaleZ = ReadFloat(f)
		If n\Model <> ""
			FreeEntity(n\OBJ)
			n\OBJ = LoadAnimMesh_Strict(n\Model)
			ScaleEntity(n\OBJ, n\ModelScaleX, n\ModelScaleY, n\ModelScaleZ)
			SetAnimTime(n\OBJ, Frame)
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs, n\TextureID - 1)
			SetAnimTime(n\OBJ, Frame)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.NPCs = Each NPCs
				If n2 <> n Then
					If n2\ID = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	MTFTimer = ReadFloat(f)
	
	If ReadInt(f) <> 632 Then RuntimeError("Couldn't load the game, save file corrupted (error 1)")
	
	room2gw_BrokenDoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	I_Zone\Transition[0] = ReadByte(f)
	I_Zone\Transition[1] = ReadByte(f)
	I_Zone\HasCustomForest = ReadByte(f)
	I_Zone\HasCustomMT = ReadByte(f)
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local RoomTemplateID% = ReadInt(f)
		Local Angle% = ReadInt(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Found = ReadByte(f)
		
		Level = ReadInt(f)
		
		Temp2 = ReadByte(f)	
		
		If Angle >= 360
            Angle = Angle - 360
        EndIf
		
		For r.Rooms = Each Rooms
			If r\x = x And r\z = z Then
				Exit
			EndIf
		Next
		
		For x = 0 To 11
			ID = ReadInt(f)
			If ID > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = ID Then r\NPC[x] = n : Exit
				Next
			EndIf
		Next
		
		For x = 0 To 11
			ID = ReadByte(f)
			If ID = 2 Then
				Exit
			Else If ID = 1 Then
				RotateEntity(r\Levers[x], 78.0, EntityYaw(r\Levers[x]), 0.0)
			Else
				RotateEntity(r\Levers[x], -78.0, EntityYaw(r\Levers[x]), 0.0)
			EndIf
		Next
		
		If ReadByte(f) = 1 Then ; ~ This room has a grid
			For y = 0 To GridSZ - 1
				For x = 0 To GridSZ - 1
					ReadByte(f) : ReadByte(f)
				Next
			Next
		Else ; ~ This grid doesn't exist in the save
			If r\grid <> Null Then
				For x = 0 To GridSZ - 1
					For y = 0 To GridSZ - 1
						If r\grid\Entities[x + (y * GridSZ)] <> 0 Then
							FreeEntity(r\grid\Entities[x + (y * GridSZ)])
							r\grid\Entities[x + (y * GridSZ)] = 0
						EndIf
						If r\grid\waypoints[x + (y * GridSZ)] <> Null Then
							RemoveWaypoint(r\grid\waypoints[x + (y * GridSZ)])
							r\grid\waypoints[x + (y * GridSZ)] = Null
						EndIf
					Next
				Next
				For x = 0 To 5
					If r\grid\Meshes[x] <> 0 Then
						FreeEntity(r\grid\Meshes[x])
						r\grid\Meshes[x] = 0
					EndIf
				Next
				Delete(r\grid)
				r\grid = Null
			EndIf
		EndIf
		
		If ReadByte(f) > 0 Then ; ~ This room has a forest
			For y = 0 To GridSize - 1
				For x = 0 To GridSize - 1
					ReadByte(f)
				Next
			Next
			lx# = ReadFloat(f)
			ly# = ReadFloat(f)
			lz# = ReadFloat(f)
		ElseIf r\fr <> Null Then ; ~ Remove the old forest
			DestroyForest(r\fr)
			Delete(r\fr)
		EndIf
		
		If Temp2 = 1 Then PlayerRoom = r.Rooms
	Next
	
	For r.Rooms = Each Rooms
		If r\x = r1499_x And r\z = r1499_z
			I_1499\PrevRoom = r
			Exit
		EndIf
	Next
	
	If ReadInt(f) <> 954 Then RuntimeError("Couldn't load the game, save file may be corrupted (error 2)")
	
	Temp = ReadInt (f)
	
	For i = 1 To Temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local Open% = ReadByte(f)
		Local OpenState# = ReadFloat(f)
		Local Locked% = ReadByte(f)
		Local AutoClose% = ReadByte(f)
		
		Local OBJX# = ReadFloat(f)
		Local OBJZ# = ReadFloat(f)
		
		Local OBJ2X# = ReadFloat(f)
		Local OBJ2Z# = ReadFloat(f)
		
		Local Timer% = ReadFloat(f)
		Local TimerState# = ReadFloat(f)
		
		Local IsElevDoor% = ReadByte(f)
		Local MTFClose% = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\FrameOBJ, True) = x Then 
				If EntityZ(do\FrameOBJ, True) = z Then	
					If EntityY(do\FrameOBJ, True) = y 
						do\Open = Open
						do\OpenState = OpenState
						do\Locked = Locked
						do\AutoClose = AutoClose
						do\Timer = Timer
						do\TimerState = TimerState
						do\IsElevatorDoor = IsElevDoor
						do\MTFClose = MTFClose
						
						PositionEntity(do\OBJ, OBJX, EntityY(do\OBJ), OBJZ, True)
						If do\OBJ2 <> 0 Then PositionEntity(do\OBJ2, OBJ2X, EntityY(do\OBJ2), OBJ2Z, True)
						
						Exit
					EndIf
				EndIf
			End If
		Next		
	Next
	
	If ReadInt(f) <> 1845 Then RuntimeError("Couldn't load the game, save file corrupted (error 3)")
	
	Local d.Decals
	
	For d.Decals = Each Decals
		FreeEntity(d\OBJ)
		Delete(d)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		ID = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local Pitch# = ReadFloat(f)
		Local Yaw# = ReadFloat(f)
		Local Roll# = ReadFloat(f)
		
		d.Decals = CreateDecal(ID, x, y, z, Pitch, Yaw, Roll)
		d\BlendMode = ReadByte (f)
		d\FX = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\LifeTime = ReadFloat(f)
		
		ScaleSprite(d\OBJ, d\Size, d\Size)
		EntityBlend(d\OBJ, d\BlendMode)
		EntityFX(d\OBJ, d\FX)
	Next
	UpdateDecals()
	
	Local e.Events
	
	For e.Events = Each Events
		If e\Sound <> 0 Then FreeSound_Strict(e\Sound) : e\Sound = 0
		If e\Sound2 <> 0 Then FreeSound_Strict(e\Sound2) : e\Sound2 = 0
		If e\Sound3 <> 0 Then FreeSound_Strict(e\Sound3) : e\Sound3 = 0
		Delete(e)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		e.Events = New Events
		e\EventName = ReadString(f)
		
		e\EventState = ReadFloat(f)
		e\EventState2 = ReadFloat(f)
		e\EventState3 = ReadFloat(f)	
		e\EventState4 = ReadFloat(f)
		x = ReadFloat(f)
		z = ReadFloat(f)
		For r.Rooms = Each Rooms
			If EntityX(r\OBJ) = x And EntityZ(r\OBJ) = z Then
				e\room = r
				Exit
			EndIf
		Next	
		e\EventStr = ReadString(f)
		If e\EventName = "room173"
			; ~ A hacky fix for the case that the intro objects aren't loaded when they should
			; ~ Altough I'm too lazy to add those objects there because at the time where you can save, those objects are already in the ground anyway -- ENDSHN
			If e\room\Objects[0] = 0 Then
				e\room\Objects[0] = CreatePivot()
				e\room\Objects[1] = CreatePivot()
			EndIf
		ElseIf e\EventName = "room860" Then
			If e\EventState = 1.0 Then
				ShowEntity(e\room\fr\Forest_Pivot)
			EndIf
		EndIf
	Next
	
	Local it.Items
	
	For it.Items = Each Items
		RemoveItem(it)
	Next
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		Local IttName$ = ReadString(f)
		Local TempName$ = ReadString(f)
		Local Name$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Red = ReadByte(f)
		Green = ReadByte(f)
		Blue = ReadByte(f)		
		A = ReadFloat(f)
		
		it.Items = CreateItem(IttName, TempName, x, y, z, Red, Green, Blue, A)
		it\Name = Name
		
		EntityType(it\Collider, HIT_ITEM)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\Collider, x, y, 0.0)
		
		it\State = ReadFloat(f)
		it\Picked = ReadByte(f)
		If it\Picked Then HideEntity(it\Collider)
		
		nt = ReadByte(f)
		If nt = True Then SelectedItem = it
		
		nt = ReadByte(f)
		If nt < 66
			Inventory(nt) = it
			ItemAmount = ItemAmount + 1
		EndIf
		
		For itt.ItemTemplates = Each ItemTemplates
			If itt\TempName = TempName Then
				If itt\IsAnim <> 0 Then SetAnimTime(it\Model, ReadFloat(f)) : Exit
			EndIf
		Next
		it\InvSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID > LastItemID Then LastItemID = it\ID
		
		If ReadByte(f) = 0 Then
			it\InvImg = it\ItemTemplate\InvImg
		Else
			it\InvImg = it\ItemTemplate\InvImg2
		EndIf
	Next	
	
	Local o_i%
	
	Temp = ReadInt(f)
	For i = 1 To Temp
		o_i = ReadInt(f)
		
		For ij.Items = Each Items
			If ij\ID = o_i Then it.Items = ij : Exit
		Next
		For j% = 0 To it\InvSlots - 1
			o_i = ReadInt(f)
			If o_i <> -1 Then
				For ij.Items = Each Items
					If ij\ID = o_i Then
						it\SecondInv[j] = ij
						Exit
					EndIf
				Next
			EndIf
		Next
	Next
	For itt.ItemTemplates = Each ItemTemplates
		itt\Found = ReadByte(f)
	Next
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			Dist# = 20.0
			
			Local closestroom.Rooms
			
			For r.Rooms = Each Rooms
				Dist2# = EntityDistance(r\OBJ, do\OBJ)
				If Dist2 < Dist Then
					Dist = Dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	If ReadInt(f) <> 994
		UsedConsole = True
	EndIf
	
	If 0 Then 
		closestroom = Null
		Dist = 30.0
		For r.Rooms = Each Rooms
			Dist2# = EntityDistance(r\OBJ, Collider)
			If Dist2 < Dist Then
				Dist = Dist2
				closestroom = r
			EndIf
		Next
		
		If closestroom <> Null Then PlayerRoom = closestroom
	EndIf
	
	; ~ This will hopefully fix the SCP-895 crash bug after the player died by it's sanity effect and then quickloaded the game -- ENDSHN
	For sc.SecurityCams = Each SecurityCams
		sc\PlayerState = 0
	Next
	EntityTexture(tt\OverlayID[4], tt\OverlayTextureID[4])
	RestoreSanity = True
	
	CameraFogFar = ReadFloat(f)
    StoredCameraFogFar = ReadFloat(f)
	If CameraFogFar = 0.0 Then
		CameraFogFar = 6.0
	EndIf
	I_427\Using = ReadByte(f)
	I_427\Timer = ReadFloat(f)
	
	I_714\Using = ReadByte(f)
	
	CloseFile(f)
	
	If Collider <> 0 Then
		If PlayerRoom <> Null Then
			ShowEntity(PlayerRoom\OBJ)
		EndIf
		ShowEntity(Collider)
		TeleportEntity(Collider, EntityX(Collider), EntityY(Collider) + 0.5, EntityZ(Collider), 0.3, True)
		If PlayerRoom <> Null Then
			HideEntity(PlayerRoom\OBJ)
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0.0
	
	; ~ Free some entities that could potentially cause memory leaks (for the endings)
	; ~ This is only required for the LoadGameQuick function, as the other one is from the menu where everything is already deleted anyways
	Local xTemp#, zTemp#
	
	If Sky <> 0 Then
		FreeEntity(Sky)
		Sky = 0
	EndIf
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "gatea" Then
			If r\Objects[0] <> 0 Then
				FreeEntity(r\Objects[0]) : r\Objects[0] = 0
				
				xTemp = EntityX(r\Objects[9], True)
				zTemp = EntityZ(r\Objects[9], True)
				FreeEntity(r\Objects[9]) : r\Objects[9] = 0
				
				r\Objects[10] = 0 ; ~ r\Objects[10] is already deleted because it is a parent object to r\Objects[9] which is already deleted a line before
				
				; ~ Readding this object, as it is originally inside the "FillRoom" function but gets deleted when it loads GateA
				r\Objects[9] = CreatePivot()
				PositionEntity(r\Objects[9], xTemp, r\y + 992.0 * RoomScale, zTemp, True)
				EntityParent(r\Objects[9], r\OBJ)
				
				; ~ The GateA wall pieces
				xTemp = EntityX(r\Objects[13], True)
				zTemp = EntityZ(r\Objects[13], True)
				FreeEntity(r\Objects[13])
				r\Objects[13] = LoadMesh_Strict("GFX\map\gateawall1.b3d", r\OBJ)
				PositionEntity(r\Objects[13], xTemp, r\y - 1045.0 * RoomScale, zTemp, True)
				EntityColor(r\Objects[13], 25.0, 25.0, 25.0)
				EntityType(r\Objects[13], HIT_MAP)
				
				xTemp = EntityX(r\Objects[14], True)
				zTemp = EntityZ(r\Objects[14], True)
				FreeEntity(r\Objects[14])
				r\Objects[14] = LoadMesh_Strict("GFX\map\gateawall2.b3d", r\OBJ)
				PositionEntity(r\Objects[14], xTemp, r\y - 1045.0 * RoomScale, zTemp, True)	
				EntityColor(r\Objects[14], 25.0, 25.0, 25.0)
				EntityType(r\Objects[14], HIT_MAP)
			EndIf
			If r\Objects[12] <> 0 Then
				FreeEntity(r\Objects[12]) : r\Objects[12] = 0
				FreeEntity(r\Objects[17]) : r\Objects[17] = 0
			EndIf
		ElseIf r\RoomTemplate\Name = "gateb" Then
			If r\Objects[0] <> 0 Then
				xTemp = EntityX(r\Objects[0], True)
				zTemp = EntityZ(r\Objects[0], True)
				FreeEntity(r\Objects[0]) : r\Objects[0] = 0
				r\Objects[0] = CreatePivot(r\OBJ)
				PositionEntity(r\Objects[0], xTemp, 9767.0 * RoomScale, zTemp, True)
			EndIf
		EndIf
	Next
	; ~ Resetting some stuff (those get changed when going to the endings)
	CameraFogMode(Camera, 1)
	HideDistance = 15.0
	
	CatchErrors("LoadGameQuick")
End Function

Function LoadSaveGames()
	CatchErrors("Uncaught (LoadSaveGames)")
	
	Local i%, myDir%, File$, j%
	
	SaveGameAmount = 0
	If FileType(SavePath) = 1 Then RuntimeError("Can't create dir " + Chr(34) + SavePath + Chr(34))
	If FileType(SavePath) = 0 Then CreateDir(SavePath)
	myDir = ReadDir(SavePath) 
	Repeat 
		File = NextFile(myDir) 
		If File = "" Then Exit 
		If FileType(SavePath + "\" + File) = 2 Then 
			If File <> "." And File <> ".." Then 
				If (FileType(SavePath + File + "\Save.txt") > 0) Then
					SaveGameAmount = SaveGameAmount + 1
				EndIf
			EndIf
		End If 
	Forever 
	CloseDir(myDir)
	
	Dim SaveGames$(SaveGameAmount + 1) 
	
	myDir = ReadDir(SavePath) 
	i = 0
	Repeat 
		File = NextFile(myDir) 
		If File = "" Then Exit 
		If FileType(SavePath + "\" + File) = 2 Then 
			If File <> "." And File <> ".." Then 
				If (FileType(SavePath + File + "\Save.txt") > 0) Then
					SaveGames(i) = File
					i = i + 1
				EndIf
			EndIf
		End If 
	Forever 
	CloseDir(myDir)
	
	Dim SaveGameTime$(SaveGameAmount + 1)
	Dim SaveGameDate$(SaveGameAmount + 1)
	Dim SaveGameVersion$(SaveGameAmount + 1)
	For i = 1 To SaveGameAmount
		Local f% = ReadFile(SavePath + SaveGames(i - 1) + "\Save.txt")
		
		SaveGameTime(i - 1) = ReadString(f)
		SaveGameDate(i - 1) = ReadString(f)
		; ~ Skip all data until the VersionVersion number
		ReadInt(f)
		For j = 0 To 5
			ReadFloat(f)
		Next
		ReadString(f)
		ReadFloat(f)
		ReadFloat(f)
		; ~ End Skip
		SaveGameVersion(i - 1) = ReadString(f)
		
		CloseFile(f)
	Next
	
	CatchErrors("LoadSaveGames")
End Function

Function LoadSavedMaps()
	CatchErrors("Uncaught (LoadSavedMaps)")
	
	Local i%, Dir, File$
	
	For i = 0 To SavedMapsAmount
		SavedMaps(i) = ""
		SavedMapsAuthor(i) = ""
	Next
	SavedMapsAmount = 0
	
	Dir = ReadDir("Map Creator\Maps")
	Repeat
		File = NextFile(Dir)
		
		If File = "" Then Exit
		If FileType(CurrentDir() + "Map Creator\Maps\" + File) = 1 Then 
			If File <> "." And File <> ".." Then
				If Right(File, 6) = "cbmap2" Or Right(File, 5) = "cbmap" Then
					SavedMapsAmount = SavedMapsAmount + 1
				EndIf
			EndIf
		EndIf 
	Forever 
	CloseDir(Dir)
	
	Dim SavedMaps(SavedMapsAmount + 1)
	Dim SavedMapsAuthor$(SavedMapsAmount + 1)
	
	i = 0
	Dir = ReadDir("Map Creator\Maps") 
	Repeat
		File = NextFile(Dir)
		
		If File = "" Then Exit
		If FileType(CurrentDir() + "Map Creator\Maps\" + File) = 1 Then 
			If File <> "." And File <> ".." Then
				If Right(File, 6) = "cbmap2" Or Right(File, 5) = "cbmap" Then
					SavedMaps(i) = File
					If Right(File, 6) = "cbmap2" Then
						Local f% = ReadFile("Map Creator\Maps\" + File)
						
						SavedMapsAuthor$(i) = ReadLine(f)
						CloseFile(f)
					Else
						SavedMapsAuthor$(i) = "[Unknown]"
					EndIf
					i = i + 1
				EndIf
			EndIf
		EndIf 
	Forever 
	CloseDir(Dir)
	CatchErrors("LoadSavedMaps")
End Function

Function LoadMap(File$)
	CatchErrors("Uncaught (LoadMap)")
	Local f%, x%, y%, Name$, Angle%, Prob#
	Local r.Rooms, rt.RoomTemplates, e.Events
	Local RoomAmount%, ForestPieceAmount%, MTPieceAmount%, i%
	
	f = ReadFile(File)
	
	Dim MapTemp%(MapWidth + 1, MapHeight + 1)
	Dim MapFound%(MapWidth + 1, MapHeight + 1)
	CoffinDistance = 100.0
	
	For x = 0 To MapWidth + 1
		For y = 0 To MapHeight + 1
			MapTemp(x, y) = False
			MapFound(x, y) = False
		Next
	Next
	
	If Right(File, 6) = "cbmap2" Then
		ReadLine(f)
		ReadLine(f)
		I_Zone\Transition[0] = ReadByte(f)
		I_Zone\Transition[1] = ReadByte(f)
		RoomAmount = ReadInt(f)
		ForestPieceAmount = ReadInt(f)
		MTPieceAmount = ReadInt(f)
		
		If ForestPieceAmount > 0 Then
			I_Zone\HasCustomForest = True
		EndIf
		If MTPieceAmount > 0 Then
			I_Zone\HasCustomMT = True
		EndIf
		
		; ~ Facility rooms
		For i = 0 To RoomAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = Lower(ReadString(f))
			
			Angle = ReadByte(f) * 90.0
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					r.Rooms = CreateRoom(0, rt\Shape, (MapWidth - x) * 8.0, 0.0, y * 8.0, Name)
					
					r\Angle = Angle
					If r\Angle <> 90 And r\Angle <> 270
						r\Angle = r\Angle + 180
					EndIf
					r\Angle = WrapAngle(r\Angle)
					
					TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
					
					MapTemp(MapWidth - x, y) = True
					
					Exit
				EndIf
			Next
			
			Name = ReadString(f)
			Prob = ReadFloat(f)
			
			If r <> Null Then
				If Prob > 0.0 Then
					If Rnd(0.0, 1.0) =< Prob Then
						e.Events = New Events
						e\EventName = Name
						e\room = r   
					EndIf
				ElseIf Prob = 0.0 And Name <> "" Then
					e.Events = New Events
					e\EventName = Name
					e\room = r  
				EndIf
			EndIf
		Next
		
		Local ForestRoom.Rooms
		
		For r.Rooms = Each Rooms
			If r\RoomTemplate\Name = "room860" Then
				ForestRoom = r
				Exit
			EndIf
		Next
		
		If ForestRoom <> Null Then
			Local fr.Forest = New Forest
		EndIf
		
		; ~ Forest rooms
		For i = 0 To ForestPieceAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = Lower(ReadString(f))
			
			Angle = ReadByte(f)
			
			If Angle <> 0 And Angle <> 2 Then
				Angle = Angle + 2
			EndIf
			Angle = Angle + 1
			If Angle > 3 Then
				Angle = (Angle Mod 4)
			EndIf
			
			x = (GridSize - 1) - x
			
			If fr <> Null Then
				Select Name
					; ~ 1, 2, 3, 4 = ROOM1
					; ~ 5, 6, 7, 8 = ROOM2
					; ~ 9, 10, 11, 12 = ROOM2C
					; ~ 13, 14, 15, 16 = ROOM3
					; ~ 17, 18, 19, 20 = ROOM4
					; ~ 21, 22, 23, 24 = DOORROOM
					Case "scp-860-1 endroom"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 1
						;[End Block]
					Case "scp-860-1 path"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 5
						;[End Block]
					Case "scp-860-1 corner"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 9
						;[End Block]
					Case "scp-860-1 t-shaped path"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 13
						;[End Block]
					Case "scp-860-1 4-way path"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 17
						;[End Block]
					Case "scp-860-1 door"
						;[Block]
						fr\grid[(y * GridSize) + x] = Angle + 21
						;[End Block]
				End Select
			EndIf
		Next
		
		If fr <> Null Then
			ForestRoom\fr = fr
			PlaceForest_MapCreator(ForestRoom\fr, ForestRoom\x, ForestRoom\y + 30.0, ForestRoom\z, ForestRoom)
		EndIf
		
		Local MTRoom.Rooms
		
		For r.Rooms = Each Rooms
			If r\RoomTemplate\Name = "room2tunnel" Then
				MTRoom = r
				Exit
			EndIf
		Next
		
		If MTRoom <> Null Then
			MTRoom\grid = New Grids
		EndIf
		
		; ~ Maintenance tunnels rooms
		For i = 0 To MTPieceAmount - 1
			x = ReadByte(f)
			y = ReadByte(f)
			Name = Lower(ReadString(f))
			
			Angle = ReadByte(f)
			
			If Angle <> 1 And Angle <> 3 Then
				Angle = Angle + 2
			EndIf
			If Name = "maintenance tunnel corner" Or Name = "maintenance tunnel t-shaped room" Then
				Angle = Angle + 3
			EndIf
			If Angle > 3 Then
				Angle = (Angle Mod 4)
			EndIf
			
			x = (GridSZ - 1) - x
			
			If MTRoom <> Null Then
				Select Name
					Case "maintenance tunnel endroom"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM1
						;[End Block]
					Case "maintenance tunnel corridor"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM2
						;[End Block]
					Case "maintenance tunnel corner"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM2C
						;[End Block]
					Case "maintenance tunnel t-shaped room"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM3
						;[End Block]
					Case "maintenance tunnel 4-way room"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM4
						;[End Block]
					Case "maintenance tunnel elevator"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM4 + 1
						;[End Block]
					Case "maintenance tunnel generator room"
						;[Block]
						MTRoom\grid\Grid[x + (y * GridSZ)] = ROOM4 + 2
						;[End Block]
				End Select
				MTRoom\grid\Angles[x + (y * GridSZ)] = Angle
			EndIf
		Next
	Else
		I_Zone\Transition[0] = 13
		I_Zone\Transition[1] = 7
		I_Zone\HasCustomForest = False
		I_Zone\HasCustomMT = False
		While Not Eof(f)
			x = ReadByte(f)
			y = ReadByte(f)
			Name$ = Lower(ReadString(f))
			
			Angle = ReadByte(f) * 90
			
			For rt.RoomTemplates = Each RoomTemplates
				If Lower(rt\Name) = Name Then
					r.Rooms = CreateRoom(0, rt\Shape, (MapWidth - x) * 8.0, 0.0, y * 8.0, Name)
					
					r\Angle = Angle
					If r\Angle <> 90 And r\Angle <> 270
						r\Angle = r\Angle + 180
					EndIf
					r\Angle = WrapAngle(r\Angle)
					
					TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
					
					MapTemp(MapWidth - x, y) = True
					
					Exit
				EndIf
			Next
			
			Name = ReadString(f)
			Prob = ReadFloat(f)
			
			If r <> Null Then
				If Prob > 0.0 Then
					If Rnd(0.0, 1.0) =< Prob Then
						e.Events = New Events
						e\EventName = Name
						e\room = r   
					EndIf
				ElseIf Prob = 0.0 And Name <> "" Then
					e.Events = New Events
					e\EventName = Name
					e\room = r
				EndIf
			EndIf
		Wend
	EndIf
	
	CloseFile(f)
	
	Local Temp% = 0, Zone%
	Local Spacing# = 8.0
	Local ShouldSpawnDoor% = False
	Local d.Doors
	For y = MapHeight To 0 Step -1
		If y < I_Zone\Transition[1] Then
			Zone = 3
		ElseIf y >= I_Zone\Transition[1] And y < I_Zone\Transition[0] Then
			Zone = 2
		Else
			Zone = 1
		EndIf
		
		For x = MapWidth To 0 Step -1
			If MapTemp(x, y) > 0 Then
				If Zone = 2 Then Temp = 2 Else Temp = 0
                
                For r.Rooms = Each Rooms
					r\Angle = WrapAngle(r\Angle)
					If Int(r\x / 8.0) = x And Int(r\z / 8.0) = y Then
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 90 Or r\Angle = 270
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 0 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								If r\Angle = 0 Or r\Angle = 180 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If (x + 1) < (MapWidth + 1)
								If MapTemp(x + 1, y) > 0 Then
									d.Doors = CreateDoor(r\zone, Float(x) * Spacing + Spacing / 2.0, 0.0, Float(y) * Spacing, 90.0, r, Max(Rand(-3, 1), 0), Temp)
									r\AdjDoor[0] = d
								EndIf
							EndIf
						EndIf
						
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 180
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 0 Or r\Angle = 180
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 180 Or r\Angle = 90
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 180 Or r\Angle = 90 Or r\Angle = 270
									ShouldSpawnDoor = True
								EndIf
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If (y + 1) < (MapHeight + 1)
								If MapTemp(x, y + 1) > 0 Then
									d.Doors = CreateDoor(r\zone, Float(x) * Spacing, 0.0, Float(y) * Spacing + Spacing / 2.0, 0.0, r, Max(Rand(-3, 1), 0), Temp)
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
	
	If IntroEnabled Then r = CreateRoom(0, ROOM1, 8.0, 0.0, (MapHeight + 2) * 8.0, "room173intro")
	r = CreateRoom(0, ROOM1, (MapWidth + 2) * 8.0, 0.0, (MapHeight + 2) * 8.0, "pocketdimension")
	r = CreateRoom(0, ROOM1, 0.0, 500.0, -16.0, "gatea")
	r = CreateRoom(0, ROOM1, -16.0, 800.0, 0.0, "dimension1499")
	
	CreateEvent("room173intro", "room173intro", 0)
	CreateEvent("pocketdimension", "pocketdimension", 0)   
	CreateEvent("gatea", "gatea", 0)
	CreateEvent("dimension1499", "dimension1499", 0)
	
	For r.Rooms = Each Rooms
		r\Adjacent[0] = Null
		r\Adjacent[1] = Null
		r\Adjacent[2] = Null
		r\Adjacent[3] = Null
		For r2.Rooms = Each Rooms
			If r <> r2 Then
				If r2\z = r\z Then
					If (r2\x) = (r\x + 8.0) Then
						r\Adjacent[0] = r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf (r2\x) = (r\x - 8.0)
						r\Adjacent[2] = r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x = r\x Then
					If (r2\z) = (r\z - 8.0) Then
						r\Adjacent[1] = r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf (r2\z) = (r\z + 8.0)
						r\Adjacent[3] = r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0] <> Null) And (r\Adjacent[1] <> Null) And (r\Adjacent[2] <> Null) And (r\Adjacent[3] <> Null) Then Exit
		Next
	Next
	
	CatchErrors("LoadMap")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D