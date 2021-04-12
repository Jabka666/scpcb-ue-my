RenderLoading(45, "MATERIALS CORE")

Include "Source Code\Materials_Core.bb"

RenderLoading(50, "TEXTURE CACHE CORE")

Include "Source Code\Texture_Cache_Core.bb"

Type Props
	Field File$
	Field OBJ%
End Type

Function CheckForPropModel%(File$)
	If Instr(File, Lower("Button.")) <> 0 Then ; ~ Check for "Button"
		Return(CopyEntity(o\ButtonModelID[0]))
	ElseIf Instr(File, Lower("Door01")) <> 0 ; ~ Check for "Door01"
		Return(CopyEntity(o\DoorModelID[0]))
	ElseIf Instr(File, Lower("\DoorFrame")) <> 0 ; ~ Check for "DoorFrame"
		Return(CopyEntity(o\DoorModelID[1]))
	ElseIf Instr(File, Lower("ContDoorLeft")) <> 0 ; ~ Check for "ContDoorLeft"
		Return(CopyEntity(o\DoorModelID[5]))
	ElseIf Instr(File, Lower("ContDoorRight")) <> 0 ; ~ Check for "ContDoorRight"
		Return(CopyEntity(o\DoorModelID[6]))
	ElseIf Instr(File, Lower("LeverBase")) <> 0 ; ~ Check for "LeverBase"
		Return(CopyEntity(o\LeverModelID[0]))
	ElseIf Instr(File, Lower("LeverHandle")) <> 0 ; ~ Check for "LeverHandle"
		Return(CopyEntity(o\LeverModelID[1]))
	Else
		Return(LoadMesh_Strict(File))
	EndIf
End Function

Function CreatePropOBJ%(File$)
	Local p.Props
	
	; ~ A hacky way to use .b3d format
	If Right(File, 1) = "x" Then File = Left(File, Len(File) - 1) + "b3d"
	
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

Global LightVolume#, TempLightVolume#

Function AddLight%(room.Rooms, x#, y#, z#, lType%, Range#, R%, G%, B%)
	Local i%
	
	If room <> Null Then
		For i = 0 To MaxRoomLights - 1
			If (Not room\Lights[i]) Then
				room\Lights[i] = CreateLight(lType)
				LightRange(room\Lights[i], Range)
				LightColor(room\Lights[i], R, G, B)
				PositionEntity(room\Lights[i], x, y, z, True)
				EntityParent(room\Lights[i], room\OBJ)
				
				room\LightIntensity[i] = (R + G + B) / 255.0 / 3.0
				
				room\LightSprites[i] = CreateSprite()
				PositionEntity(room\LightSprites[i], x, y, z)
				ScaleSprite(room\LightSprites[i], 0.13 , 0.13)
				EntityTexture(room\LightSprites[i], t\LightSpriteID[0])
				EntityFX(room\LightSprites[i], 1 + 8)
				EntityBlend(room\LightSprites[i], 3)
				EntityColor(room\LightSprites[i], R, G, B)
				EntityParent(room\LightSprites[i], room\OBJ)
				
				room\LightSpritesPivot[i] = CreatePivot()
				EntityRadius(room\LightSpritesPivot[i], 0.05)
				PositionEntity(room\LightSpritesPivot[i], x, y, z)
				EntityParent(room\LightSpritesPivot[i], room\OBJ)
				
				room\LightSprites2[i] = CreateSprite()
				PositionEntity(room\LightSprites2[i], x, y, z)
				ScaleSprite(room\LightSprites2[i], 0.6, 0.6)
				EntityTexture(room\LightSprites2[i], t\LightSpriteID[2])
				EntityBlend(room\LightSprites2[i], 3)
				EntityOrder(room\LightSprites2[i], -1)
				EntityColor(room\LightSprites2[i], R, G, B)
				EntityParent(room\LightSprites2[i], room\OBJ)
				EntityFX(room\LightSprites2[i], 1 + 8)
				RotateEntity(room\LightSprites2[i], 0.0, 0.0, Rnd(360.0))
				SpriteViewMode(room\LightSprites2[i], 1)
				room\LightSpriteHidden[i] = True
				HideEntity(room\LightSprites2[i])
				room\LightFlicker[i] = Rand(1, 10)
				
				room\LightR[i] = R
				room\LightG[i] = G
				room\LightB[i] = B
				
				HideEntity(room\Lights[i])
				
				room\MaxLights = room\MaxLights + 1
				
				Return(room\Lights[i])
			EndIf
		Next
	Else
		Local Light%, Sprite%
		
		Light = CreateLight(lType)
		LightRange(Light, Range)
		LightColor(Light, R, G, B)
		PositionEntity(Light, x, y, z, True)
		
		Sprite = CreateSprite()
		PositionEntity(Sprite, x, y, z)
		ScaleSprite(Sprite, 0.13 , 0.13)
		EntityTexture(Sprite, t\LightSpriteID[0])
		EntityFX(Sprite, 1 + 8)
		EntityBlend(Sprite, 3)
		EntityColor(Sprite, R, G, B)
		
		Return(Light)
	EndIf
End Function

Type LightTemplates
	Field RoomTemplate.RoomTemplates
	Field lType%
	Field x#, y#, z#
	Field Range#
	Field R%, G%, B%
	Field Pitch#, Yaw#
	Field InnerConeAngle%, OuterConeAngle#
End Type 

Function AddTempLight.LightTemplates(rt.RoomTemplates, x#, y#, z#, lType%, Range#, R%, G%, B%)
	Local lt.LightTemplates
	
	lt.LightTemplates = New LightTemplates
	lt\RoomTemplate = rt
	lt\x = x
	lt\y = y
	lt\z = z
	lt\lType = lType
	lt\Range = Range
	lt\R = R
	lt\G = G
	lt\B = B
	
	Return(lt)
End Function

Global UpdateRoomLightsTimer# = 0.0

Function UpdateRoomLights(Cam%)
	If opt\EnableRoomLights And SecondaryLightOn > 0.5 And Cam = Camera Then
		UpdateRoomLightsTimer = UpdateRoomLightsTimer + fps\Factor[0]
		If UpdateRoomLightsTimer >= 8.0 Then
			UpdateRoomLightsTimer = 0.0
		EndIf
	EndIf
End Function

Function RenderRoomLights(Cam%)
	Local r.Rooms, i%, Random#, Alpha#, Dist#
	
	For r.Rooms = Each Rooms
		If r\Dist < HideDistance * 0.7 Lor r = PlayerRoom Then
			For i = 0 To r\MaxLights - 1
				If r\Lights[i] <> 0 Then
					If opt\EnableRoomLights And SecondaryLightOn > 0.5 And Cam = Camera Then
						EntityOrder(r\LightSprites2[i], -1)
						If UpdateRoomLightsTimer = 0.0 Then
							ShowEntity(r\LightSprites[i])
							
							If EntityDistanceSquared(Cam, r\Lights[i]) < 72.25 Then
								If (Not r\LightHidden[i]) Then
									ShowEntity(r\Lights[i])
									r\LightHidden[i] = True
								EndIf
							Else
								If r\LightHidden[i] Then
									HideEntity(r\Lights[i])
									r\LightHidden[i] = False
								EndIf
							EndIf
							
							If EntityDistanceSquared(Cam, r\LightSprites2[i]) < 72.25 Then
								If EntityVisible(Cam, r\LightSpritesPivot[i]) Then
									If r\LightSpriteHidden[i] Then
										ShowEntity(r\LightSprites2[i])
										r\LightSpriteHidden[i] = False
									EndIf
									If PlayerRoom\RoomTemplate\Name = "room173intro" Then
										Random = Rnd(0.38, 0.42)
									Else
										If r\LightFlicker[i] < 5 Then
											Random = Rnd(0.38, 0.42)
										ElseIf r\LightFlicker[i] > 4 And r\LightFlicker[i] < 10 Then
											Random = Rnd(0.35, 0.45)
										Else
											Random = Rnd(0.3, 0.5)
										EndIf
									EndIf
									ScaleSprite(r\LightSprites2[i], Random, Random)
									
									Alpha = 1.0 - Max(Min(((EntityDistance(Cam, r\LightSpritesPivot[i]) + 0.5) / 7.5), 1.0), 0.0)
									
									If Alpha > 0.0 Then
										EntityAlpha(r\LightSprites2[i], Max(3.0 * (BRIGHTNESS / 255.0) * (r\LightIntensity[i] / 2.0), 1.0) * Alpha)
									Else
										; ~ Instead of rendering the sprite invisible, just hiding it if the player is far away from it
										If (Not r\LightSpriteHidden[i]) Then
											HideEntity(r\LightSprites2[i])
											r\LightSpriteHidden[i] = True
										EndIf
									EndIf
								Else
									If (Not r\LightSpriteHidden[i]) Then
										HideEntity(r\LightSprites2[i])
										r\LightSpriteHidden[i] = True
									EndIf
								EndIf
							Else
								If (Not r\LightSpriteHidden[i]) Then
									HideEntity(r\LightSprites2[i])
									r\LightSpriteHidden[i] = True
								EndIf
							EndIf
						Else
							If EntityDistanceSquared(Cam, r\LightSprites2[i]) < 72.25 Then
								If PlayerRoom\RoomTemplate\Name = "room173intro" Then
									Random = Rnd(0.38, 0.42)
								Else
									If r\LightFlicker[i] < 5 Then
										Random = Rnd(0.38, 0.42)
									ElseIf r\LightFlicker[i] > 4 And r\LightFlicker[i] < 10 Then
										Random = Rnd(0.35, 0.45)
									Else
										Random = Rnd(0.3, 0.5)
									EndIf
								EndIf
								If (Not r\LightSpriteHidden[i]) Then
									ScaleSprite(r\LightSprites2[i], Random, Random)
								EndIf
							EndIf
						EndIf
					ElseIf Cam = Camera Then
						If SecondaryLightOn =< 0.5 Then
							HideEntity(r\LightSprites[i])
						Else
							ShowEntity(r\LightSprites[i])
						EndIf
						
						If r\LightHidden[i] Then
							HideEntity(r\Lights[i])
							r\LightHidden[i] = False
						EndIf
						If (Not r\LightSpriteHidden[i]) Then
							HideEntity(r\LightSprites2[i])
							r\LightSpriteHidden[i] = True
						EndIf
					Else
						; ~ This will make the lightsprites not glitch through the wall when they are rendered by the cameras
						EntityOrder(r\LightSprites2[i], 0)
					EndIf
				EndIf
			Next
		EndIf
	Next
End Function

Global AmbientLightRoomTex%, AmbientLightRoomVal%

Function AmbientLightRooms(Value% = 0)
	If Value = AmbientLightRoomVal Then Return
	AmbientLightRoomVal = Value
	
	Local OldBuffer% = BackBuffer() ; ~ Probably shouldn't make assumptions here but who cares, why wouldn't it use the BackBuffer()
	
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor(Value, Value, Value)
	Cls()
	ClsColor(0, 0, 0)
	SetBuffer(OldBuffer)
End Function

Const RoomScale# = 8.0 / 2048.0

Function LoadRMesh(File$, rt.RoomTemplates)
	CatchErrors("Uncaught (LoadRMesh)")
	
	Local mat.Materials
	
	ClsColor(0, 0, 0)
	
	; ~ Read the file
	Local f% = ReadFile(File)
	Local i%, j%, k%, x#, y#, z#, Yaw#, Pitch#, Roll#
	Local Vertex%
	Local Temp1i%, Temp2i%, Temp3i%
	Local Temp1#, Temp2#, Temp3#
	Local Temp1s$, Temp2s$
	Local CollisionMeshes% = CreatePivot()
	Local HasTriggerBox% = False
	
	For i = 0 To 3 ; ~ Reattempt up to 3 times
		If (Not f) Then
			f = ReadFile(File)
		Else
			Exit
		EndIf
	Next
	If (Not f) Then RuntimeError("Error reading file " + Chr(34) + File + Chr(34))
	
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
		HasTriggerBox = True
	Else
		RuntimeError(Chr(34) + File + Chr(34) + " is Not RMESH (" + IsRMesh + ")")
	EndIf
	
	File = StripFileName(File)
	
	Local Count%, Count2%
	
	; ~ Drawn meshes
	Local Opaque%, Alpha%
	
	Opaque = CreateMesh()
	Alpha = CreateMesh()
	
	Local ChildMesh%
	Local Surf%, Tex%[2], Brush%
	Local IsAlpha%
	Local u#, v#
	
	Count = ReadInt(f)
	
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
						Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s, 1)
					Else
						Tex[j] = LoadTextureCheckingIfInCache(File + Temp1s, 3)
					EndIf
				ElseIf FileType(MapTexturesFolder + Temp1s) = 1 ; ~ If not, check the MapTexturesFolder
					If Temp1i < 3 Then
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 1)
					Else
						Tex[j] = LoadTextureCheckingIfInCache(MapTexturesFolder + Temp1s, 3)
					EndIf
				EndIf
				If Tex[j] <> 0 Then
					If Temp1i = 1 Then TextureBlend(Tex[j], 5)
					If Instr(Lower(Temp1s), "_lm") <> 0 Then
						TextureBlend(Tex[j], 2)
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
				BrushTexture(Brush, Tex[1], 0, 0)
			Else
				BrushTexture(Brush, MissingTexture, 0, 0)
			EndIf
		Else
			Local BumpTex% = 0
			
			If Tex[0] <> 0 And Tex[1] <> 0 Then
				If opt\BumpEnabled Then
					Local Temp$ = StripPath(TextureName(Tex[1]))
					
					For mat.Materials = Each Materials
						If mat\Name = Temp Then
							BumpTex = mat\Bump
							Exit
						EndIf
					Next
				Else
					BumpTex = 0
				EndIf
				If (Not BumpTex) Then
					For j = 0 To 1
						BrushTexture(Brush, Tex[j], 0, j + 1)
					Next
				Else
					TextureCoords(BumpTex, 0)
					For j = 0 To 1
						BrushTexture(Brush, Tex[j], 0, j + 2)
					Next
					BrushTexture(Brush, BumpTex, 0, 1)
				EndIf
				BrushTexture(Brush, AmbientLightRoomTex, 0)
			Else
				If opt\BumpEnabled Then
					If Tex[1] <> 0 Then
						Temp = StripPath(TextureName(Tex[1]))
						For mat.Materials = Each Materials
							If mat\Name = Temp Then
								BumpTex = mat\Bump
								Exit
							EndIf
						Next
					EndIf
				Else
					BumpTex = 0
				EndIf
				If (Not BumpTex) Then
					For j = 0 To 1
						If Tex[j] <> 0 Then
							BrushTexture(Brush, Tex[j], 0, j)
						Else
							BrushTexture(Brush, MissingTexture, 0, j)
						EndIf
					Next
				Else
					TextureCoords(BumpTex, 0)
					For j = 0 To 1
						If Tex[j] <> 0 Then
							BrushTexture(Brush, Tex[j], 0, j + 1)
						Else
							BrushTexture(Brush, MissingTexture, 0, j + 1)
						EndIf
					Next
					BrushTexture(Brush, BumpTex, 0, 0)
				EndIf
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
			EntityAlpha(ChildMesh, 0.0)
		Else
			AddMesh(ChildMesh, Opaque)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
			EntityType(ChildMesh, HIT_MAP)
			EntityPickMode(ChildMesh, 2)
			
			; ~ Make collision double-sided
			Local FlipChild% = CopyMesh(ChildMesh)
			
			FlipMesh(FlipChild)
			AddMesh(FlipChild, ChildMesh)
			FreeEntity(FlipChild)
		EndIf
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
		Local TB%
		
		rt\TempTriggerBoxAmount = ReadInt(f)
		For TB = 0 To rt\TempTriggerBoxAmount - 1
			rt\TempTriggerBox[TB] = CreateMesh(rt\OBJ)
			Count = ReadInt(f)
			For i = 1 To Count
				Surf = CreateSurface(rt\TempTriggerBox[TB])
				Count2 = ReadInt(f)
				For j = 1 To Count2
					x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
					Vertex = AddVertex(Surf, x, y, z)
				Next
				Count2 = ReadInt(f)
				For j = 1 To Count2
					Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
					AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
					AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
				Next
			Next
			rt\TempTriggerBoxName[TB] = ReadString(f)
		Next
	EndIf
	
	Count = ReadInt(f) ; ~ Point entities
	
	Local Range#, lColor$, Intensity#
	Local R%, G%, B%
	Local Angles$
	
	For i = 1 To Count
		Temp1s = ReadString(f)
		Select Temp1s
			Case "screen"
				;[Block]
				Temp1 = ReadFloat(f) * RoomScale
				Temp2 = ReadFloat(f) * RoomScale
				Temp3 = ReadFloat(f) * RoomScale
				
				Temp2s = ReadString(f)
				
				If Temp1 <> 0 Lor Temp2 <> 0 Lor Temp3 <> 0 Then 
					Local ts.TempScreens = New TempScreens
					
					ts\x = Temp1
					ts\y = Temp2
					ts\z = Temp3
					ts\ImgPath = Temp2s
					ts\RoomTemplate = rt
				EndIf
				;[End Block]
			Case "waypoint"
				;[Block]
				Temp1 = ReadFloat(f) * RoomScale
				Temp2 = ReadFloat(f) * RoomScale
				Temp3 = ReadFloat(f) * RoomScale
				
				Local w.TempWayPoints = New TempWayPoints
				
				w\roomtemplate = rt
				w\x = Temp1
				w\y = Temp2
				w\z = Temp3
				;[End Block]
			Case "light"
				;[Block]
				Temp1 = ReadFloat(f) * RoomScale
				Temp2 = ReadFloat(f) * RoomScale
				Temp3 = ReadFloat(f) * RoomScale
				
				If Temp1 <> 0 Lor Temp2 <> 0 Lor Temp3 <> 0 Then 
					Range = ReadFloat(f) / 2000.0
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					R = Int(Piece(lColor, 1, " ")) * Intensity
					G = Int(Piece(lColor, 2, " ")) * Intensity
					B = Int(Piece(lColor, 3, " ")) * Intensity
					
					AddTempLight(rt, Temp1, Temp2, Temp3, 2, Range, R, G, B)
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f)
				EndIf
				;[End Block]
			Case "spotlight"
				;[Block]
				Temp1 = ReadFloat(f) * RoomScale
				Temp2 = ReadFloat(f) * RoomScale
				Temp3 = ReadFloat(f) * RoomScale
				
				If Temp1 <> 0 Lor Temp2 <> 0 Lor Temp3 <> 0 Then 
					Range = ReadFloat(f) / 2000.0
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					R = Int(Piece(lColor, 1, " ")) * Intensity
					G = Int(Piece(lColor, 2, " ")) * Intensity
					B = Int(Piece(lColor, 3, " ")) * Intensity
					
					Local lt.LightTemplates = AddTempLight(rt, Temp1, Temp2, Temp3, 2, Range, R, G, B)
					
					Angles = ReadString(f)
					Pitch = Piece(Angles, 1, " ")
					Yaw = Piece(Angles, 2, " ")
					lt\Pitch = Pitch
					lt\Yaw = Yaw
					
					lt\InnerConeAngle = ReadInt(f)
					lt\OuterConeAngle = ReadInt(f)
				Else
					ReadFloat(f) : ReadString(f) : ReadFloat(f) : ReadString(f) : ReadInt(f) : ReadInt(f)
				EndIf
				;[End Block]
			Case "soundemitter"
				;[Block]
				Temp1i = 0
				If rt <> Null Then
					For j = 0 To MaxRoomEmitters - 1
						If (Not rt\TempSoundEmitter[j]) Then
							rt\TempSoundEmitterX[j] = ReadFloat(f) * RoomScale
							rt\TempSoundEmitterY[j] = ReadFloat(f) * RoomScale
							rt\TempSoundEmitterZ[j] = ReadFloat(f) * RoomScale
							rt\TempSoundEmitter[j] = ReadInt(f)
							
							rt\TempSoundEmitterRange[j] = ReadFloat(f)
							Temp1i = 1
							Exit
						EndIf
					Next
				EndIf
				
				If Temp1i = 0 Then
					ReadFloat(f)
					ReadFloat(f)
					ReadFloat(f)
					ReadInt(f)
					ReadFloat(f)
				EndIf
				;[End Block]
			Case "playerstart"
				;[Block]
				Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
				
				Angles = ReadString(f)
				Pitch = Piece(Angles, 1, " ")
				Yaw = Piece(Angles, 2, " ")
				Roll = Piece(Angles, 3, " ")
				;[End Block]
			Case "model"
				;[Block]
				File = ReadString(f)
				If File <> "" Then
					Local Model% = CreatePropOBJ("GFX\map\Props\" + File)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					PositionEntity(Model, Temp1, Temp2, Temp3)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					RotateEntity(Model, Temp1, Temp2, Temp3)
					
					Temp1 = ReadFloat(f) : Temp2 = ReadFloat(f) : Temp3 = ReadFloat(f)
					ScaleEntity(Model, Temp1, Temp2, Temp3)
					
					EntityParent(Model, Opaque)
					EntityType(Model, HIT_MAP)
					EntityPickMode(Model, 2)
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
	EntityType(HiddenMesh, HIT_MAP)
	EntityAlpha(Opaque, 1.0)
	
	OBJ = CreatePivot()
	CreatePivot(OBJ) ; ~ Skip "meshes" object
	EntityParent(Opaque, OBJ)
	EntityParent(HiddenMesh, OBJ)
	CreatePivot(OBJ) ; ~ Skip "pointentites" object
	CreatePivot(OBJ) ; ~ Skip "solidentites" object
	EntityParent(CollisionMeshes, OBJ)
	
	CloseFile(f)
	
	Return(OBJ)
	
	CatchErrors("LoadRMesh")
End Function

Const ForestGridSize% = 10

Type Forest
	Field TileMesh%[5]
	Field DetailMesh%[5]
	Field Grid%[(ForestGridSize ^ 2) + 11]
	Field TileEntities%[(ForestGridSize ^ 2) + 1]
	Field Forest_Pivot%
	Field Door%[2]
	Field DetailEntities%[2]
	Field ID%
End Type

; ~ Forest Constants
;[Block]
Const Deviation_Chance% = 40 ; ~ Out of 100
Const Branch_Chance% = 65
Const Branch_Max_Life% = 4
Const Branch_Die_Chance% = 18
Const Max_Deviation_Distance% = 3
Const Return_Chance% = 27
Const Center% = 5
Const Cobble_Chance% = 0
;[End Block]

Function GenForestGrid(fr.Forest)
	CatchErrors("Uncaught (GenForestGrid)")
	
	Local LastForestID%
	
	fr\ID = LastForestID + 1
	LastForestID = LastForestID + 1
	
	Local Door1_Pos%, Door2_Pos%
	Local i%, j%, n%, LeftMost%, RightMost%
	
	Door1_Pos = Rand(3, 7)
	Door2_Pos = Rand(3, 7)
	
	; ~ Clear the grid
	For i = 0 To ForestGridSize - 1
		For j = 0 To ForestGridSize - 1
			fr\Grid[(j * ForestGridSize) + i] = 0
		Next
	Next
	
	; ~ Set the position of the concrete and doors
	fr\Grid[Door1_Pos] = 3
	fr\Grid[((ForestGridSize - 1) * ForestGridSize) + Door2_Pos] = 3
	
	; ~ Generate the path
	Local PathX% = Door2_Pos
	Local PathY% = 1
	Local Dir% = 1
	
	fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	
	Local Deviated%
	
	While PathY < ForestGridSize - 4
		If Dir = 1 Then ; ~ Determine whether to go forward or to the side
			If Chance(Deviation_Chance) Then
				; ~ Pick a branch direction
				Dir = 2 * Rand(0, 1)
				; ~ Make sure you have not passed max side distance
				Dir = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir)
				Deviated = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir, True)
				If Deviated Then fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
				PathX = MoveForward(Dir, PathX, PathY)
				PathY = MoveForward(Dir, PathX, PathY, True)
			EndIf
		Else
			; ~ We are going to the side, so determine whether to keep going or go forward again
			Dir = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir)
			Deviated = TurnIfDeviating(Max_Deviation_Distance, PathX, Center, Dir, True)
			If Deviated Lor Chance(Return_Chance) Then Dir = 1
			
			PathX = MoveForward(Dir, PathX, PathY)
			PathY = MoveForward(Dir, PathX, PathY, True)
			; ~ If we just started going forward go twice so as to avoid creating a potential 2x2 line
			If Dir = 1 Then
				fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
				PathX = MoveForward(Dir, PathX, PathY)
				PathY = MoveForward(Dir, PathX, PathY, True)
			EndIf
		EndIf
		;~ Add our position to the grid
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	; ~ Finally, bring the path back to the door now that we have reached the end
	Dir = 1
	While PathY < ForestGridSize - 2
		PathX = MoveForward(Dir, PathX, PathY)
		PathY = MoveForward(Dir, PathX, PathY, True)
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	
	If PathX <> Door1_Pos Then
		Dir = 0
		If Door1_Pos > PathX Then Dir = 2
		While PathX <> Door1_Pos
			PathX = MoveForward(Dir, PathX, PathY)
			PathY = MoveForward(Dir, PathX, PathY, True)
			fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
		Wend
	EndIf
	
	; ~ Attempt to create new branches
	Local New_Y%, Temp_Y%, New_X%
	Local Branch_Type%, Branch_Pos%
	
	New_Y = -3 ; ~ Used for counting off. Branches will only be considered once every 4 units so as to avoid potentially too many branches
	While New_Y < ForestGridSize - 6
		New_Y = New_Y + 4
		Temp_Y = New_Y
		New_X = 0
		If Chance(Branch_Chance) Then
			Branch_Type = -1
			If Chance(Cobble_Chance) Then
				Branch_Type = -2
			EndIf
			; ~ Create a branch at this spot
			; ~ Determine if on left or on right
			Branch_Pos = 2 * Rand(0, 1)
			; ~ Get leftmost or rightmost path in this row
			LeftMost = ForestGridSize
			RightMost = 0
			For i = 0 To ForestGridSize
				If fr\Grid[((ForestGridSize - 1 - New_Y) * ForestGridSize) + i] = 1 Then
					If i < LeftMost Then LeftMost = i
					If i > RightMost Then RightMost = i
				EndIf
			Next
			If Branch_Pos = 0 Then
				New_X = LeftMost - 1
			Else
				New_X = RightMost + 1
			EndIf
			; ~ Before creating a branch make sure there are no 1's above or below
			If (Temp_Y <> 0 And fr\Grid[((ForestGridSize - 1 - Temp_Y + 1) * ForestGridSize) + New_X] = 1) Lor fr\Grid[((ForestGridSize - 1 - Temp_Y - 1) * ForestGridSize) + New_X] = 1 Then
				Exit ; ~ Break simply to stop creating the branch
			EndIf
			fr\Grid[((ForestGridSize - 1 - Temp_Y) * ForestGridSize) + New_X] = Branch_Type ; ~ Make 4s so you don't confuse your branch for a path; will be changed later
			If Branch_Pos = 0 Then
				New_X = LeftMost - 2
			Else
				New_X = RightMost + 2
			EndIf
			fr\Grid[((ForestGridSize - 1 - Temp_Y) * ForestGridSize) + New_X] = Branch_Type ; ~ Branch out twice to avoid creating an unwanted 2x2 path with the real path
			i = 2
			While i < Branch_Max_Life
				i = i + 1
				If Chance(Branch_Die_Chance) Then
					Exit
				EndIf
				If Rand(0, 3) = 0 Then ; ~ Have a higher chance to go up to confuse the player
					If Branch_Pos = 0 Then
						New_X = New_X - 1
					Else
						New_X = New_X + 1
					EndIf
				Else
					Temp_Y = Temp_Y + 1
				EndIf
				
				; ~ Before creating a branch make sure there are no 1's above or below
				n = ((ForestGridSize - 1 - Temp_Y + 1) * ForestGridSize) + New_X
				If n < ForestGridSize - 1 Then 
					If Temp_Y <> 0 And fr\Grid[n] = 1 Then Exit
				EndIf
				n = ((ForestGridSize - 1 - Temp_Y - 1) * ForestGridSize) + New_X
				If n > 0 Then 
					If fr\Grid[n] = 1 Then Exit
				EndIf
				fr\Grid[((ForestGridSize - 1 - Temp_Y) * ForestGridSize) + New_X] = Branch_Type ; ~ Make 4s so you don't confuse your branch for a path; will be changed later
				If Temp_Y >= ForestGridSize - 2 Then Exit
			Wend
		EndIf
	Wend
	
	; ~ Change branches from 4s to 1s (they were 4s so that they didn't accidently create a 2x2 path unintentionally)
	For i = 0 To ForestGridSize - 1
		For j = 0 To ForestGridSize - 1
			If fr\Grid[(i * ForestGridSize) + j] = -1 Then
				fr\Grid[(i * ForestGridSize) + j] = 1
			ElseIf fr\Grid[(i * ForestGridSize) + j] = -2
				fr\Grid[(i * ForestGridSize) + j] = 1
			EndIf
		Next
	Next
	
	CatchErrors("GenForestGrid")
End Function

; ~ Shape IDs Constants
;[Block]
Const ROOM1% = 0
Const ROOM2% = 1
Const ROOM2C% = 2
Const ROOM3% = 3
Const ROOM4% = 4
;[End Block]

Function PlaceForest(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("Uncaught (PlaceForest)")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%
	Local Tile_Entity%, Detail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, Frame%, lX%, lY%, d%
	
	If fr\Forest_Pivot <> 0 Then FreeEntity(fr\Forest_Pivot) : fr\Forest_Pivot = 0
	For i = ROOM1 To ROOM4
		If fr\TileMesh[i] <> 0 Then FreeEntity(fr\TileMesh[i]) : fr\TileMesh[i] = 0
	Next
	For i = 0 To 4
		If fr\DetailMesh[i] <> 0 Then FreeEntity(fr\DetailMesh[i]) : fr\DetailMesh[i] = 0
	Next
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	; ~ Load assets
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
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
	Next
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\map\forest\detail\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\map\forest\detail\rock1.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\map\forest\detail\rock2.b3d")
	fr\DetailMesh[3] = LoadMesh_Strict("GFX\map\forest\detail\tree2.b3d")
	fr\DetailMesh[4] = LoadRMesh("GFX\map\forest\wall_opt.rmesh", Null)
	
	For i = ROOM1 To ROOM4
		HideEntity(fr\TileMesh[i])
	Next
	For i = 0 To 4
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\Grid[(tY * ForestGridSize) + tX] = 1 Then 
				Tile_Type = 0
				If tX + 1 < ForestGridSize Then Tile_Type = (fr\Grid[(tY * ForestGridSize) + tX + 1] > 0)
				If tX - 1 >= 0 Then Tile_Type = Tile_Type + (fr\Grid[(tY * ForestGridSize) + tX - 1] > 0)
				
				If tY + 1 < ForestGridSize Then Tile_Type = Tile_Type + (fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0)
				If tY - 1 >= 0 Then Tile_Type = Tile_Type + (fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0)
				
				Local Angle# = 0.0
				
				Select Tile_Type
					Case 1
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM1])
						
						If fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0 Then
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] > 0
							Angle = 270.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0
							Angle = 90.0
						Else
							Angle = 0.0
						EndIf
						
						Tile_Type = ROOM1 + 1
						;[End Block]
					Case 2
						;[Block]
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0 Then
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Tile_Type = ROOM2 + 1
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0 And fr\Grid[(tY * ForestGridSize) + tX - 1] > 0
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Angle = 90.0
							Tile_Type = ROOM2 + 1
						Else
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2C])
							If fr\Grid[(tY * ForestGridSize) + tX - 1] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0 Then
								Angle = 180.0
							ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0 And fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0
								Angle = 0.0
							ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] > 0 And fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0
								Angle = 270.0
							Else
								Angle = 90.0
							EndIf
							Tile_Type = ROOM2C + 1
						EndIf
						;[End Block]
					Case 3
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM3])
						
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] = 0 Then
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] = 0
							Angle = 90.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] = 0
							Angle = 270.0
						Else
							Angle = 0.0
						EndIf
						
						Tile_Type = ROOM3 + 1
						;[End Block]
					Case 4
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM4])	
						Tile_Type = ROOM4 + 1
						;[End Block]
				End Select
				
				If Tile_Type > 0 Then 
					Local ItemPlaced%[4]
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[Floor(tY / 3)]) Then
						ItemPlaced[Floor(tY / 3)] = True
						it.Items = CreateItem("Log #" + Int(Floor(tY / 3) + 1), "paper", 0.0, 0.5, 0.0)
						EntityType(it\Collider, HIT_ITEM)
						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							If ColorRed() > Rand(100, 260) Then
								Select Rand(0, 7)
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[3])
											RotateEntity(d, 0.0, 90.0 * i + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - Rnd(3.0, 3.2), lY * Tempf4 - (Tempf3 / 2.0), True)
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - 1.3, lY * Tempf4 - (Tempf3 / 2.0), True)
										EntityFX(Detail_Entity, 1)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[3])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - 1.3, lY * Tempf4 - (Tempf3 / 2.0), True)
										;[End Block]
								End Select
								If Detail_Entity <> 0 Then
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
				EndIf
			EndIf
		Next
	Next
	
	; ~ Place the wall		
	For i = 0 To 1
		tY = i * (ForestGridSize - 1)
		For tX = 0 To ForestGridSize - 1
			If fr\Grid[(tY * ForestGridSize) + tX] = 3 Then
				fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[4])
				ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
				
				fr\Door[i] = CopyEntity(r\Objects[3])
				ScaleEntity(fr\Door[i], 49.0 * RoomScale, 45.0 * RoomScale, 48.0 * RoomScale, True)
				EntityPickMode(fr\Door[i], 2)
				PositionEntity(fr\Door[i], 72.0 * RoomScale, 32.0 * RoomScale, 0.0, True)
				RotateEntity(fr\Door[i], 0.0, 180.0, 0.0)
				EntityParent(fr\Door[i], fr\DetailEntities[i])
				
				Frame = CopyEntity(r\Objects[2], fr\Door[i])
				PositionEntity(Frame, 0.0, 32.0 * RoomScale, 0.0, True)
				ScaleEntity(Frame, 48.0 * RoomScale, 45.0 * RoomScale, 48.0 * RoomScale, True)
				EntityParent(Frame, fr\DetailEntities[i])
				
				EntityType(fr\DetailEntities[i], HIT_MAP)
				EntityPickMode(fr\DetailEntities[i], 2)
				PositionEntity(fr\DetailEntities[i], x + (tX * Tile_Size), y, z + (tY * Tile_Size) + (Tile_Size / 2) - (Tile_Size * i), True)
				RotateEntity(fr\DetailEntities[i], 0.0, 180.0 * i, 0.0)
				EntityParent(fr\DetailEntities[i], fr\Forest_Pivot)
				Exit
			EndIf		
		Next		
	Next
	
	CatchErrors("PlaceForest")
End Function

Function PlaceMapCreatorForest(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("Uncaught (PlaceMapCreatorForest)")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%, Detail_Entity%
	Local Tile_Entity%, Eetail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, Frame%, lX%, lY%, d%
	
	If fr\Forest_Pivot <> 0 Then FreeEntity(fr\Forest_Pivot) : fr\Forest_Pivot = 0
	For i = ROOM1 To ROOM4
		If fr\TileMesh[i] <> 0 Then FreeEntity(fr\TileMesh[i]) : fr\TileMesh[i] = 0
	Next
	For i = 0 To 4
		If fr\DetailMesh[i] <> 0 Then FreeEntity(fr\DetailMesh[i]) : fr\DetailMesh[i] = 0
	Next
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	Local hMap%[ROOM4 + 1], Mask%[ROOM4 + 1]
	; ~ Load assets
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
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
	Next
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\map\forest\detail\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\map\forest\detail\rock1.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\map\forest\detail\rock2.b3d")
	fr\DetailMesh[3] = LoadMesh_Strict("GFX\map\forest\detail\tree2.b3d")
	fr\DetailMesh[4] = LoadRMesh("GFX\map\forest\wall_opt.rmesh", Null)
	
	For i = ROOM1 To ROOM4
		HideEntity(fr\TileMesh[i])
	Next
	For i = 0 To 4
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\Grid[(tY * ForestGridSize) + tX] > 0 Then 
				Tile_Type = 0
				
				Local Angle# = 0.0
				
				Tile_Type = Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0)
				If Tile_Type = 6 Then
					Tile_Type = 2
				EndIf
				Angle = (fr\Grid[(tY * ForestGridSize) + tX] Mod 4.0) * 90.0
				
				Tile_Entity = CopyEntity(fr\TileMesh[Tile_Type - 1])
				
				If Tile_Type > 0 Then 
					Local ItemPlaced%[4]
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[Floor(tY / 3)]) Then
						ItemPlaced[Floor(tY / 3)] = True
						it.Items = CreateItem("Log #" + Int(Floor(tY / 3) + 1), "paper", 0.0, 0.5, 0.0)
						EntityType(it\Collider, HIT_ITEM)
						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							If ColorRed() > Rand(100, 260) Then
								Detail_Entity = 0
								Select Rand(0, 7)
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[3])
											RotateEntity(d, 0.0, 90.0 * i + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - Rnd(3.0, 3.2), lY * Tempf4 - (Tempf3 / 2.0), True)
										
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - 1.3, lY * Tempf4 - (Tempf3 / 2.0), True)
										EntityFX(Detail_Entity, 1)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[3])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, lX * Tempf4 - (Tempf3 / 2.0), ColorRed() * 0.03 - 1.3, lY * Tempf4 - (Tempf3 / 2.0), True)
										;[End Block]
								End Select
								
								If Detail_Entity <> 0 Then
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
				EndIf
				
				If Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0) = 6 Then
					For i = 0 To 1
						If (Not fr\Door[i]) Then
							fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[4])
							ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
							
							fr\Door[i] = CopyEntity(r\Objects[3])
							ScaleEntity(fr\Door[i], 49.0 * RoomScale, 45.0 * RoomScale, 48.0 * RoomScale, True)
							EntityPickMode(fr\Door[i], 2)
							PositionEntity(fr\Door[i], 72.0 * RoomScale, 32.0 * RoomScale, 0.0, True)
							RotateEntity(fr\Door[i], 0.0, 180.0, 0.0)
							EntityParent(fr\Door[i], fr\DetailEntities[i])
							
							Frame = CopyEntity(r\Objects[2], fr\Door[i])
							PositionEntity(Frame, 0.0, 32.0 * RoomScale, 0.0, True)
							ScaleEntity(Frame, 48.0 * RoomScale, 45.0 * RoomScale, 48.0 * RoomScale, True)
							EntityParent(Frame, fr\DetailEntities[i])
							
							EntityType(fr\DetailEntities[i], HIT_MAP)
							EntityPickMode(fr\DetailEntities[i], 2)
							PositionEntity(fr\DetailEntities[i], x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
							RotateEntity(fr\DetailEntities[i], 0.0, Angle + 180.0, 0.0)
							MoveEntity(fr\DetailEntities[i], 0.0, 0.0, -6.0)
							EntityParent(fr\DetailEntities[i], fr\Forest_Pivot)
							Exit
						EndIf
					Next
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("PlaceMapCreatorForest")
End Function

Function DestroyForest(fr.Forest)
	CatchErrors("Uncaught (DestroyForest)")
	
	Local tX%, tY%, i%
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0 Then
				FreeEntity(fr\TileEntities[tX + (tY * ForestGridSize)]) : fr\TileEntities[tX + (tY * ForestGridSize)] = 0
				fr\Grid[tX + (tY * ForestGridSize)] = 0
			EndIf
		Next
	Next
	For i = 0 To 1
		If fr\Door[i] <> 0 Then FreeEntity(fr\Door[i]) : fr\Door[i] = 0
		If fr\DetailEntities[i] <> 0 Then FreeEntity(fr\DetailEntities[i]) : fr\DetailEntities[i] = 0
	Next
	If fr\Forest_Pivot <> 0 Then FreeEntity(fr\Forest_Pivot) : fr\Forest_Pivot = 0
	For i = ROOM1 To ROOM4
		If fr\TileMesh[i] <> 0 Then FreeEntity(fr\TileMesh[i]) : fr\TileMesh[i] = 0
	Next
	For i = 0 To 4
		If fr\DetailMesh[i] <> 0 Then FreeEntity(fr\DetailMesh[i]) : fr\DetailMesh[i] = 0
	Next
	
	CatchErrors("DestroyForest")
End Function

Function UpdateForest(fr.Forest, Ent%)
	CatchErrors("Uncaught (UpdateForest)")
	
	Local tX%, tY%
	
	If Abs(EntityY(Ent, True) - EntityY(fr\Forest_Pivot, True)) < 12.0 Then
		For tX = 0 To ForestGridSize - 1
			For tY = 0 To ForestGridSize - 1
				If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0 Then
					If Abs(EntityX(Ent, True) - EntityX(fr\TileEntities[tX + (tY * ForestGridSize)], True)) < HideDistance Then
						If Abs(EntityZ(Ent, True) - EntityZ(fr\TileEntities[tX + (tY * ForestGridSize)], True)) < HideDistance Then
							ShowEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
						Else
							HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
						EndIf
					Else
						HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
					EndIf
				EndIf
			Next
		Next
	EndIf
	
	CatchErrors("UpdateForest")
End Function

Global RoomTempID%

Type RoomTemplates
	Field OBJ%, ID%
	Field OBJPath$
	Field Zone%[5]
	Field TempSoundEmitter%[MaxRoomEmitters]
	Field TempSoundEmitterX#[MaxRoomEmitters], TempSoundEmitterY#[MaxRoomEmitters], TempSoundEmitterZ#[MaxRoomEmitters]
	Field TempSoundEmitterRange#[MaxRoomEmitters]
	Field Shape%, Name$
	Field Commonness%, Large%
	Field DisableDecals%
	Field TempTriggerBoxAmount%
	Field TempTriggerBox%[128]
	Field TempTriggerBoxName$[128]
	Field DisableOverlapCheck% = True
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
End Type 	

Function CreateRoomTemplate.RoomTemplates(MeshPath$)
	Local rt.RoomTemplates
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\map\" + MeshPath
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates(File$)
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
				StrTemp = GetINIString(File, TemporaryString, "Mesh Path")
				
				rt.RoomTemplates = CreateRoomTemplate(StrTemp)
				rt\Name = Lower(TemporaryString)
				
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
					Case "room2C", "2C", "room2c", "2c"
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
				
				rt\Commonness = Max(Min(GetINIInt(File, TemporaryString, "Commonness"), 100), 0)
				rt\Large = GetINIInt(File, TemporaryString, "Large")
				rt\DisableDecals = GetINIInt(File, TemporaryString, "Disabledecals")
				rt\DisableOverlapCheck = GetINIInt(File, TemporaryString, "DisableOverlapCheck")
			EndIf
		EndIf
	Wend
	
	i = 0
	Repeat
		StrTemp = GetINIString(File, "room ambience", "Ambience" + i)
		If StrTemp = "" Then Exit
		
		RoomAmbience[i] = LoadSound_Strict(StrTemp)
		i = i + 1
	Forever
	
	CloseFile(f)
	
	CatchErrors("LoadRoomTemplates")
End Function

Function LoadRoomMesh(rt.RoomTemplates)
	If Instr(rt\OBJPath, ".rmesh") <> 0 Then ; ~ File is .rmesh
		rt\OBJ = LoadRMesh(rt\OBJPath, rt)
	ElseIf Instr(rt\OBJPath, ".b3d") <> 0 ; ~ File is .b3d
		RuntimeError(".b3d rooms are no longer supported, please use the converter! Affected room: " + Chr(34) + rt\OBJPath + Chr(34))
	Else ; ~ File not found
		RuntimeError("File: " + Chr(34) + rt\OBJPath + Chr(34) + " not found.")
	EndIf
	
	If (Not rt\OBJ) Then RuntimeError("Failed to load map file: " + Chr(34) + rt\OBJPath + Chr(34) + ".")
	
	CalculateRoomTemplateExtents(rt)
	
	HideEntity(rt\OBJ)
End Function

LoadRoomTemplates("Data\rooms.ini")

Global RoomAmbience%[10]

Global Sky%

Global HideDistance#

Global SecondaryLightOn# = True
Global PrevSecondaryLightOn# = True
Global RemoteDoorOn% = True

Const MaxRoomLights% = 32
Const MaxRoomEmitters% = 8
Const MaxRoomObjects% = 30

Type Rooms
	Field Zone%
	Field Found%
	Field OBJ%
	Field x#, y#, z#
	Field Angle%
	Field RoomTemplate.RoomTemplates
	Field Dist#
	Field SoundCHN%
	Field fr.Forest
	Field SoundEmitter%[MaxRoomEmitters]
	Field SoundEmitterOBJ%[MaxRoomEmitters]
	Field SoundEmitterRange#[MaxRoomEmitters]
	Field SoundEmitterCHN%[MaxRoomEmitters]
	Field Lights%[MaxRoomLights]
	Field LightIntensity#[MaxRoomLights]
	Field LightSprites%[MaxRoomLights]	
	Field Objects%[MaxRoomObjects]
	Field Levers%[10]
	Field RoomDoors.Doors[7]
	Field NPC.NPCs[12]
	Field mt.MTGrid
	Field Adjacent.Rooms[4]
	Field AdjDoor.Doors[4]
	Field NonFreeAble%[10]
	Field Textures%[10]
	Field MaxLights% = 0
	Field LightSpriteHidden%[MaxRoomLights]
	Field LightSpritesPivot%[MaxRoomLights]
	Field LightSprites2%[MaxRoomLights]
	Field LightHidden%[MaxRoomLights]
	Field LightFlicker%[MaxRoomLights]
	Field TriggerBoxAmount%
	Field TriggerBoxes.TriggerBox[8]
	Field MaxWayPointY#
	Field LightR#[MaxRoomLights], LightG#[MaxRoomLights], LightB#[MaxRoomLights]
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
End Type 

Global PlayerRoom.Rooms

Const MTGridSize% = 19 ; ~ Same size as the main map itself (better for the map creator)

Type MTGrid
	Field Grid%[MTGridSize ^ 2]
	Field Angles%[MTGridSize ^ 2]
	Field Meshes%[7]
	Field Entities%[MTGridSize ^ 2]
	Field waypoints.WayPoints[MTGridSize ^ 2]
End Type

Function UpdateMT(mt.MTGrid)
	CatchErrors("Uncaught (UpdateMT)")
	
	Local tX%, tY%
	
	For tX = 0 To MTGridSize - 1
		For tY = 0 To MTGridSize - 1
			If mt\Entities[tX + (tY * MTGridSize)] <> 0 Then
				If Abs(EntityY(me\Collider, True) - EntityY(mt\Entities[tX + (tY * MTGridSize)], True)) > 4.0 Then Exit
				If Abs(EntityX(me\Collider, True) - EntityX(mt\Entities[tX + (tY * MTGridSize)], True)) < HideDistance Then
					If Abs(EntityZ(me\Collider, True) - EntityZ(mt\Entities[tX + (tY * MTGridSize)], True)) < HideDistance Then
						ShowEntity(mt\Entities[tX + (tY * MTGridSize)])
					Else
						HideEntity(mt\Entities[tX + (tY * MTGridSize)])
					EndIf
				Else
					HideEntity(mt\Entities[tX + (tY * MTGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("UpdateMT")
End Function

Function PlaceMapCreatorMT(r.Rooms)
	CatchErrors("Uncaught (PlaceMapCreatorMT)")
	
	Local dr.Doors, it.Items, wayp.WayPoints
	Local x%, y%, i%, Dist#
	Local Meshes%[7]
	
	For i = 0 To 6
		Meshes[i] = CopyEntity(o\MTModelID[i])
		HideEntity(Meshes[i])
	Next
	
	For y = 0 To MTGridSize - 1
		For x = 0 To MTGridSize - 1
			If r\mt\Grid[x + (y * MTGridSize)] > 0 Then
				Local Tile_Type% = 0
				Local Angle# = 0.0
				
				Tile_Type = r\mt\Grid[x + (y * MTGridSize)]
				Angle = r\mt\Angles[x + (y * MTGridSize)] * 90.0
				
				Local Tile_Entity% = CopyEntity(Meshes[Tile_Type - 1])
				
				RotateEntity(Tile_Entity, 0.0, Angle, 0.0)
				ScaleEntity(Tile_Entity, RoomScale, RoomScale, RoomScale, True)
				PositionEntity(Tile_Entity, r\x + (x * 2.0), r\y + 8.0, r\z + (y * 2.0), True)
				
				Select Tile_Type
					Case ROOM1 + 1, ROOM2 + 1
						;[Block]
						AddLight(Null, r\x + (x * 2.0), r\y + 8.0 + (372.0 * RoomScale), r\z + (y * 2.0), 2, 500.0 * RoomScale, 255, 255, 255)
						;[End Block]
					Case ROOM2C + 1, ROOM3 + 1, ROOM4 + 1
						;[Block]
						AddLight(Null, r\x + (x * 2.0), r\y + 8.0 + (416.0 * RoomScale), r\z + (y * 2.0), 2, 500.0 * RoomScale, 255, 255, 255)
						;[End Block]
					Case ROOM4 + 2
						;[Block]
						dr.Doors = CreateDoor(r\x + (x * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * 240.0 * RoomScale), r\y + 8.0, r\z + (y * 2.0) + (Sin(EntityYaw(Tile_Entity, True)) * 240.0 * RoomScale), EntityYaw(Tile_Entity, True) - 90.0, Null, False, Elevator_Door)
						PositionEntity(dr\Buttons[0], EntityX(dr\Buttons[0], True) + (Cos(EntityYaw(Tile_Entity, True)) * 0.05), EntityY(dr\Buttons[0], True), EntityZ(dr\Buttons[0], True) + (Sin(EntityYaw(Tile_Entity, True)) * 0.05), True)
						PositionEntity(dr\Buttons[1], EntityX(dr\Buttons[1], True) + (Cos(EntityYaw(Tile_Entity, True)) * 0.05), EntityY(dr\Buttons[1], True), EntityZ(dr\Buttons[1], True) + (Sin(EntityYaw(Tile_Entity, True)) * 0.031), True)
						
						AddLight(Null, r\x + (x * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * 555.0 * RoomScale), r\y + 8.0 + (469.0 * RoomScale), r\z + (y * 2.0) + (Sin(EntityYaw(Tile_Entity, True)) * 555.0 * RoomScale), 2, 600.0 * RoomScale, 255, 255, 255)
						
						Local TempInt2% = CreatePivot()
						
						RotateEntity(TempInt2, 0.0, EntityYaw(Tile_Entity, True) + 180.0, 0.0, True)
						PositionEntity(TempInt2, r\x + (x * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * 552.0 * RoomScale), r\y + 8.0 + (240.0 * RoomScale), r\z + (y * 2.0) + (Sin(EntityYaw(Tile_Entity, True)) * 552.0 * RoomScale))
						If r\RoomDoors[1] = Null Then
							r\RoomDoors[1] = dr
							r\Objects[3] = TempInt2
							PositionEntity(r\Objects[0], r\x + (x * 2.0), r\y + 8.0, r\z + (y * 2.0), True)
						ElseIf r\RoomDoors[1] <> Null And r\RoomDoors[3] = Null Then
							r\RoomDoors[3] = dr
							r\Objects[5] = TempInt2
							PositionEntity(r\Objects[1], r\x + (x * 2.0), r\y + 8.0, r\z + (y * 2.0), True)
						EndIf
						;[End Block]
					Case ROOM4 + 3
						;[Block]
						AddLight(Null, r\x + (x * 2.0) - (Sin(EntityYaw(Tile_Entity, True)) * 504.0 * RoomScale) + (Cos(EntityYaw(Tile_Entity, True)) * 16.0 * RoomScale), r\y + 8.0 + (396.0 * RoomScale), r\z + (y * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * 504.0 * RoomScale) + (Sin(EntityYaw(Tile_Entity, True)) * 16.0 * RoomScale), 2, 500.0 * RoomScale, 255, 200, 200)
						it.Items = CreateItem("SCP-500-01", "scp500pill", r\x + (x * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * (-208.0) * RoomScale) - (Sin(EntityYaw(Tile_Entity, True)) * 1226.0 * RoomScale), r\y + 8.0 + (90.0 * RoomScale), r\z + (y * 2.0) + (Sin(EntityYaw(Tile_Entity, True)) * (-208.0) * RoomScale) + (Cos(EntityYaw(Tile_Entity, True)) * 1226.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						
						it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + (x * 2.0) - (Sin(EntityYaw(Tile_Entity, True)) * 504.0 * RoomScale) + (Cos(EntityYaw(Tile_Entity, True)) * 16.0 * RoomScale), r\y + 8.0 + (90.0 * RoomScale),  r\z + (y * 2.0) + (Cos(EntityYaw(Tile_Entity, True)) * 504.0 * RoomScale) + (Sin(EntityYaw(Tile_Entity, True)) * 16.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						;[End Block]
				End Select
				
				r\mt\Entities[x + (y * MTGridSize)] = Tile_Entity
				wayp = CreateWaypoint(r\x + (x * 2.0), r\y + 8.2, r\z + (y * 2.0), Null, r)
				r\mt\waypoints[x + (y * MTGridSize)] = wayp
				
				If y < MTGridSize - 1 Then
					If r\mt\waypoints[x + ((y + 1) * MTGridSize)] <> Null Then
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y + 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)] Then
								Exit 
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If y > 0 Then
					If r\mt\waypoints[x + ((y - 1) * MTGridSize)] <> Null Then
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y - 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x > 0 Then
					If r\mt\waypoints[x - 1 + (y * MTGridSize)] <> Null Then
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x - 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x < MTGridSize - 1 Then
					If r\mt\waypoints[x + 1 + (y * MTGridSize)] <> Null Then
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)] Then
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null Then
								r\mt\waypoints[x + 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + 1 + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	For i = 0 To 6
		r\mt\Meshes[i] = Meshes[i]
	Next
	
	CatchErrors("PlaceMapCreatorMT")
End Function

Function CreateRoom.Rooms(Zone%, RoomShape%, x#, y#, z#, Name$ = "")
	CatchErrors("Uncaught (CreateRoom)")
	
	Local r.Rooms = New Rooms
	Local rt.RoomTemplates
	
	r\Zone = Zone
	
	r\x = x : r\y = y : r\z = z
	
	If Name <> "" Then
		Name = Lower(Name)
		For rt.RoomTemplates = Each RoomTemplates
			If rt\Name = Name Then
				r\RoomTemplate = rt
				
				If (Not rt\OBJ) Then LoadRoomMesh(rt)
				
				r\OBJ = CopyEntity(rt\OBJ)
				ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
				EntityType(r\OBJ, HIT_MAP)
				EntityPickMode(r\OBJ, 2)
				
				PositionEntity(r\OBJ, x, y, z)
				FillRoom(r)
				
				CalculateRoomExtents(r)
				Return(r)
			EndIf
		Next
	EndIf
	
	Local Temp% = 0
	
	For rt.RoomTemplates = Each RoomTemplates
		Local i%
		
		For i = 0 To 4
			If rt\Zone[i] = Zone Then 
				If rt\Shape = RoomShape Then
					Temp = Temp + rt\Commonness
					Exit
				EndIf
			EndIf
		Next
	Next
	
	Local RandomRoom% = Rand(Temp)
	
	Temp = 0
	For rt.RoomTemplates = Each RoomTemplates
		For i = 0 To 4
			If rt\Zone[i] = Zone And rt\Shape = RoomShape Then
				Temp = Temp + rt\Commonness
				If RandomRoom > Temp - rt\Commonness And RandomRoom =< Temp Then
					r\RoomTemplate = rt
					
					If (Not rt\OBJ) Then LoadRoomMesh(rt)
					
					r\OBJ = CopyEntity(rt\OBJ)
					ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
					EntityType(r\OBJ, HIT_MAP)
					EntityPickMode(r\OBJ, 2)
					
					PositionEntity(r\OBJ, x, y, z)
					FillRoom(r)
					
					CalculateRoomExtents(r)
					Return(r)	
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("CreateRoom")
End Function

Type TempWayPoints
	Field x#, y#, z#
	Field roomtemplate.RoomTemplates
End Type

Type WayPoints
	Field OBJ%
	Field door.Doors
	Field room.Rooms
	Field State%
	Field connected.WayPoints[5]
	Field Dist#[5]
	Field Fcost#, Gcost#, Hcost#
	Field parent.WayPoints
End Type

Function CreateWaypoint.WayPoints(x#, y#, z#, door.Doors, room.Rooms)
	Local w.WayPoints
	
	w.WayPoints = New WayPoints
	w\OBJ = CreatePivot()
	PositionEntity(w\OBJ, x, y, z)
	EntityParent(w\OBJ, room\OBJ)
	
	w\room = room
	w\door = door
	
	Return(w)
End Function

Function InitWayPoints(LoadingStart% = 55)
	Local d.Doors, w.WayPoints, w2.WayPoints, r.Rooms, ClosestRoom.Rooms
	Local x#, y#, z#
	Local Temper% = MilliSecs2()
	Local Dist#, Dist2#
	
	For d.Doors = Each Doors
		If d\OBJ <> 0 Then HideEntity(d\OBJ)
		If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
		If d\FrameOBJ <> 0 Then HideEntity(d\FrameOBJ)
		
		If d\room = Null Then
			ClosestRoom.Rooms = Null
			Dist = 30.0
			For r.Rooms = Each Rooms
				x = Abs(EntityX(r\OBJ, True) - EntityX(d\FrameOBJ, True))
				If x < 20.0 Then
					z = Abs(EntityZ(r\OBJ, True) - EntityZ(d\FrameOBJ, True))
					If z < 20.0 Then
						Dist2 = (x * x) + (z * z)
						If Dist2 < Dist Then
							ClosestRoom = r
							Dist = Dist2
						EndIf
					EndIf
				EndIf
			Next
		Else
			ClosestRoom = d\room
		EndIf
		If (Not d\DisableWaypoint) Then CreateWaypoint(EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True) + 0.18, EntityZ(d\FrameOBJ, True), d, ClosestRoom)
	Next
	
	Local Amount% = 0
	
	For w.WayPoints = Each WayPoints
		EntityPickMode(w\OBJ, 1, True)
		EntityRadius(w\OBJ, 0.2)
		Amount = Amount + 1
	Next
	
	Local Number% = 0
	Local Iter% = 0
	Local i%, n%
	
	For w.WayPoints = Each WayPoints
		Number = Number + 1
		Iter = Iter + 1
		If Iter = 20 Then 
			RenderLoading(LoadingStart + Floor((30.0 / Amount) * Number), "WAYPOINTS") 
			Iter = 0
		EndIf
		
		w2.WayPoints = After(w)
		
		Local CanCreateWayPoint% = False
		
		While w2 <> Null
			If w\room = w2\room Lor w\door <> Null Lor w2\door <> Null
				Dist = EntityDistance(w\OBJ, w2\OBJ)
				
				If w\room\MaxWayPointY = 0.0 Lor w2\room\MaxWayPointY = 0.0
					CanCreateWayPoint = True
				Else
					If Abs(EntityY(w\OBJ) - EntityY(w2\OBJ)) =< w\room\MaxWayPointY
						CanCreateWayPoint = True
					EndIf
				EndIf
				
				If Dist < 7.0 Then
					If CanCreateWayPoint
						If EntityVisible(w\OBJ, w2\OBJ) Then
							For i = 0 To 4
								If w\connected[i] = Null Then
									w\connected[i] = w2.WayPoints 
									w\Dist[i] = Dist
									Exit
								EndIf
							Next
							
							For n = 0 To 4
								If w2\connected[n] = Null Then 
									w2\connected[n] = w.WayPoints 
									w2\Dist[n] = Dist
									Exit
								EndIf					
							Next
						EndIf
					EndIf	
				EndIf
			EndIf
			w2 = After(w2)
		Wend
	Next
	
	For d.Doors = Each Doors
		If d\OBJ <> 0 Then ShowEntity(d\OBJ)
		If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
		If d\FrameOBJ <> 0 Then ShowEntity(d\FrameOBJ)		
	Next
	
	For w.WayPoints = Each WayPoints
		EntityPickMode(w\OBJ, 0, 0)
		EntityRadius(w\OBJ, 0)
		
		For i = 0 To 4
			If w\connected[i] <> Null Then 
				Local tLine% = CreateLine(EntityX(w\OBJ, True), EntityY(w\OBJ, True), EntityZ(w\OBJ, True), EntityX(w\connected[i]\OBJ, True), EntityY(w\connected[i]\OBJ, True), EntityZ(w\connected[i]\OBJ, True))
				
				EntityColor(tLine, 255.0, 0.0, 0.0)
				EntityParent(tLine, w\OBJ)
			EndIf
		Next
	Next
End Function

Function RemoveWaypoint(w.WayPoints)
	FreeEntity(w\OBJ) : w\OBJ = 0
	Delete(w)
End Function

Global ClosestButton%

Function CreateButton%(ButtonID%, x#, y#, z#, Pitch# = 0.0, Yaw# = 0.0, Roll# = 0.0, Parent% = 0, Locked% = False)
	Local OBJ%
	
	OBJ = CopyEntity(o\ButtonModelID[ButtonID])
	ScaleEntity(OBJ, 0.03, 0.03, 0.03)
	PositionEntity(OBJ, x, y, z)
	RotateEntity(OBJ, Pitch, Yaw, Roll)
	EntityPickMode(OBJ, 2)
	If Locked Then EntityTexture(OBJ, t\MiscTextureID[17])
	If Parent <> 0 Then EntityParent(OBJ, Parent)
	
	Return(OBJ)
End Function

Function UpdateButton(OBJ%)
	Local Dist# = EntityDistanceSquared(me\Collider, OBJ)
	
	If Dist < 0.64 Then
		Local Temp% = CreatePivot()
		
		PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
		PointEntity(Temp, OBJ)
		
		If EntityPick(Temp, 0.6) = OBJ Then
			If (Not ClosestButton) Then 
				ClosestButton = OBJ
			Else
				If Dist < EntityDistanceSquared(me\Collider, ClosestButton) Then ClosestButton = OBJ
			EndIf							
		EndIf
		FreeEntity(Temp)
	EndIf			
End Function

Global UpdateDoorsTimer#
Global DoorTempID%

Type BrokenDoor
	Field IsBroken%
	Field x#
	Field z#
End Type

Global bk.BrokenDoor = New BrokenDoor

Type Doors
	Field OBJ%, OBJ2%, FrameOBJ%, Buttons%[2]
	Field Locked%, LockedUpdated%, Open%, Angle%, OpenState#, FastOpen%
	Field DoorType%, Dist#
	Field Timer%, TimerState#
	Field KeyCard%
	Field room.Rooms
	Field DisableWaypoint%
	Field SoundCHN%
	Field Code$
	Field ID%
	Field AutoClose%
	Field LinkedDoor.Doors
	Field IsElevatorDoor% = False
	Field MTFClose% = True
End Type 

Global ClosestDoor.Doors
Global SelectedDoor.Doors

; ~ Doors IDs Constants
;[Block]
Const Default_Door% = 0
Const Big_Door% = 1
Const Heavy_Door% = 2
Const Elevator_Door% = 3
Const One_Sided_Door% = 4
Const SCP_914_Door% = 5
;[End Block]

Function CreateDoor.Doors(x#, y#, z#, Angle#, room.Rooms, Open% = False, DoorType% = Default_Door, Keycard% = 0, Code$ = "", CheckIfZeroCard% = False)
	Local d.Doors, Parent%, i%
	
	If room <> Null Then Parent = room\OBJ
	
	d.Doors = New Doors
	If DoorType = Big_Door Then
		d\OBJ = CopyEntity(o\DoorModelID[5])
		ScaleEntity(d\OBJ, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		d\OBJ2 = CopyEntity(o\DoorModelID[6])
		ScaleEntity(d\OBJ2, 55.0 * RoomScale, 55.0 * RoomScale, 55.0 * RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[4])				
		ScaleEntity(d\FrameOBJ, RoomScale, RoomScale, RoomScale)
		EntityType(d\FrameOBJ, HIT_MAP)
		EntityAlpha(d\FrameOBJ, 0.0)
	ElseIf DoorType = Heavy_Door Then
		d\OBJ = CopyEntity(o\DoorModelID[2])
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(o\DoorModelID[3])
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	ElseIf DoorType = Elevator_Door
		d\OBJ = CopyEntity(o\DoorModelID[7])
		ScaleEntity(d\OBJ, RoomScale, RoomScale, RoomScale)
		d\OBJ2 = CopyEntity(d\OBJ)
		ScaleEntity(d\OBJ2, RoomScale, RoomScale, RoomScale)
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	ElseIf DoorType = One_Sided_Door Lor DoorType = SCP_914_Door
		d\OBJ = CopyEntity(o\DoorModelID[10])
		ScaleEntity(d\OBJ, 203.0 * RoomScale / MeshWidth(d\OBJ), 313.0 * RoomScale / MeshHeight(d\OBJ), 15.0 * RoomScale / MeshDepth(d\OBJ))
		d\OBJ2 = CopyEntity(o\DoorModelID[10])
		ScaleEntity(d\OBJ2, 203.0 * RoomScale / MeshWidth(d\OBJ2), 313.0 * RoomScale / MeshHeight(d\OBJ2), 15.0 * RoomScale / MeshDepth(d\OBJ2))
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	Else
		d\OBJ = CopyEntity(o\DoorModelID[0])
		ScaleEntity(d\OBJ, 203.0 * RoomScale / MeshWidth(d\OBJ), 313.0 * RoomScale / MeshHeight(d\OBJ), 15.0 * RoomScale / MeshDepth(d\OBJ))
		d\OBJ2 = CopyEntity(o\DoorModelID[0])
		ScaleEntity(d\OBJ2, 203.0 * RoomScale / MeshWidth(d\OBJ2), 313.0 * RoomScale / MeshHeight(d\OBJ2), 15.0 * RoomScale / MeshDepth(d\OBJ2))
		
		d\FrameOBJ = CopyEntity(o\DoorModelID[1])
	EndIf
	
	PositionEntity(d\FrameOBJ, x, y, z)
	ScaleEntity(d\FrameOBJ, RoomScale, RoomScale, RoomScale)
	EntityPickMode(d\FrameOBJ, 2)
	
	PositionEntity(d\OBJ, x, y, z)
	RotateEntity(d\OBJ, 0.0, Angle, 0.0)
	EntityType(d\OBJ, HIT_MAP)
	EntityPickMode(d\OBJ, 2)
	CreateCollBox(d\OBJ)
	EntityParent(d\OBJ, Parent)
	
	If d\OBJ2 <> 0 Then
		PositionEntity(d\OBJ2, x, y, z)
		RotateEntity(d\OBJ2, 0.0, Angle + ((DoorType <> Big_Door) * 180.0), 0.0)
		EntityType(d\OBJ2, HIT_MAP)
		EntityPickMode(d\OBJ2, 2)
		CreateCollBox(d\OBJ2)
		EntityParent(d\OBJ2, Parent)
	EndIf
	
	For i = 0 To 1
		If Code <> "" Then 
			d\Buttons[i] = CreateButton(2, x + ((DoorType <> Big_Door) * (0.6 + (i * (-1.2)))) + ((DoorType = Big_Door) * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((DoorType <> Big_Door) * ((-0.1) + (i * 0.2))) + ((DoorType = Big_Door) * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((DoorType <> Big_Door) * (i * 180.0)) + ((DoorType = Big_Door) * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
		ElseIf DoorType = Elevator_Door
			d\Buttons[i] = CreateButton(i * 4, x + ((DoorType <> Big_Door) * (0.6 + (i * (-1.2)))) + ((DoorType = Big_Door) * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((DoorType <> Big_Door) * ((-0.1) + (i * 0.2))) + ((DoorType = Big_Door) * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((DoorType <> Big_Door) * (i * 180.0)) + ((DoorType = Big_Door) * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
		Else
			If Keycard > 0 Then
				d\Buttons[i] = CreateButton(1, x + ((DoorType <> Big_Door) * (0.6 + (i * (-1.2)))) + ((DoorType = Big_Door) * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((DoorType <> Big_Door) * ((-0.1) + (i * 0.2))) + ((DoorType = Big_Door) * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((DoorType <> Big_Door) * (i * 180.0)) + ((DoorType = Big_Door) * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
			ElseIf Keycard < 0
				d\Buttons[i] = CreateButton(3, x + ((DoorType <> Big_Door) * (0.6 + (i * (-1.2)))) + ((DoorType = Big_Door) * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((DoorType <> Big_Door) * ((-0.1) + (i * 0.2))) + ((DoorType = Big_Door) * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((DoorType <> Big_Door) * (i * 180.0)) + ((DoorType = Big_Door) * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
			Else
				d\Buttons[i] = CreateButton(0, x + ((DoorType <> Big_Door) * (0.6 + (i * (-1.2)))) + ((DoorType = Big_Door) * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((DoorType <> Big_Door) * ((-0.1) + (i * 0.2))) + ((DoorType = Big_Door) * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((DoorType <> Big_Door) * (i * 180.0)) + ((DoorType = Big_Door) * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
			EndIf
		EndIf
	Next
	
	RotateEntity(d\FrameOBJ, 0.0, Angle, 0.0)
	EntityParent(d\FrameOBJ, Parent)
	
	d\ID = DoorTempID
	DoorTempID = DoorTempID + 1
	
	If Keycard > 0 Then
		If CheckIfZeroCard Then
			d\KeyCard = Keycard + 1
		Else
			d\KeyCard = Keycard + 2
		EndIf
	Else
		d\KeyCard = Keycard 
	EndIf
	
	; ~ Set "d\Locked = 1" for elevator doors to fix buttons color. Anyway the door will be unlocked by "UpdateElevators" function. -- Jabka
	If DoorType = Elevator_Door Then d\Locked = 1
	
	d\Code = Code
	
	d\Angle = Angle
	d\Open = Open		
	
	If d\Open And DoorType = Default_Door And d\Locked = 0 And Rand(8) = 1 Then d\AutoClose = True
	d\DoorType = DoorType
	d\room = room
	
	d\MTFClose = True
	
	Return(d)
End Function

Function UpdateDoors()
	Local p.Particles, d.Doors
	Local x#, z#, Dist#, i%
	
	If UpdateDoorsTimer =< 0.0 Then
		For d.Doors = Each Doors
			Local xDist# = Abs(EntityX(me\Collider) - EntityX(d\OBJ, True))
			Local zDist# = Abs(EntityZ(me\Collider) - EntityZ(d\OBJ, True))
			
			d\Dist = xDist + zDist
			
			If d\Dist > HideDistance * 2.0 Then
				If d\OBJ <> 0 Then HideEntity(d\OBJ)
				If d\FrameOBJ <> 0 Then HideEntity(d\FrameOBJ)
				If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
				If d\Buttons[0] <> 0 Then HideEntity(d\Buttons[0])
				If d\Buttons[1] <> 0 Then HideEntity(d\Buttons[1])				
			Else
				If d\OBJ <> 0 Then ShowEntity(d\OBJ)
				If d\FrameOBJ <> 0 Then ShowEntity(d\FrameOBJ)
				If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
				If d\Buttons[0] <> 0 Then ShowEntity(d\Buttons[0])
				If d\Buttons[1] <> 0 Then ShowEntity(d\Buttons[1])
			EndIf
		Next
		UpdateDoorsTimer = 30.0
	Else
		UpdateDoorsTimer = Max(UpdateDoorsTimer - fps\Factor[0], 0.0)
	EndIf
	
	ClosestButton = 0
	ClosestDoor = Null
	
	For d.Doors = Each Doors
		If d\Dist < HideDistance * 2.0 Lor d\IsElevatorDoor > 0 Then ; ~ Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2tunnel -- ENDSHN
			If (d\OpenState >= 180.0 Lor d\OpenState =< 0.0) And (Not GrabbedEntity) Then
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then
						If Abs(EntityX(me\Collider) - EntityX(d\Buttons[i], True)) < 1.0 Then 
							If Abs(EntityZ(me\Collider) - EntityZ(d\Buttons[i], True)) < 1.0 Then 
								Dist = DistanceSquared(EntityX(me\Collider, True), EntityX(d\Buttons[i], True), EntityZ(me\Collider, True), EntityZ(d\Buttons[i], True))
								If Dist < 0.64 Then
									Local Temp% = CreatePivot()
									
									PositionEntity(Temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
									PointEntity(Temp, d\Buttons[i])
									
									If EntityPick(Temp, 0.6) = d\Buttons[i] Then
										If (Not ClosestButton) Then
											ClosestButton = d\Buttons[i]
											ClosestDoor = d
										Else
											If Dist < EntityDistanceSquared(me\Collider, ClosestButton) Then ClosestButton = d\Buttons[i] : ClosestDoor = d
										EndIf							
									EndIf
									FreeEntity(Temp)
								EndIf							
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
			If d\Open Then
				If d\OpenState < 180.0 Then
					Select d\DoorType
						Case Default_Door
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fps\Factor[0] / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * fps\Factor[0] / 80.0, 0.0, 0.0)	
							;[End Block]
						Case Big_Door
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 0.8))
							MoveEntity(d\OBJ, Sin(d\OpenState) * fps\Factor[0] / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, (-Sin(d\OpenState)) * fps\Factor[0] / 180.0, 0.0, 0.0)
							;[End Block]
						Case Heavy_Door
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen + 1) * fps\Factor[0] / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fps\Factor[0] / 120.0, 0.0, 0.0)
							;[End Block]
						Case Elevator_Door
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fps\Factor[0] / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState)* (d\FastOpen * 2 + 1) * fps\Factor[0] / 162.0, 0.0, 0.0)
							;[End Block]
						Case One_Sided_Door
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (d\FastOpen * 2 + 1) * fps\Factor[0] / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fps\Factor[0]) / 80.0, 0.0, 0.0)	
							;[End Block]	
						Case SCP_914_Door ; ~ Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 1.4))
							MoveEntity(d\OBJ, Sin(d\OpenState) * fps\Factor[0] / 114.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (-fps\Factor[0]) / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
				Else
					d\FastOpen = 0
					ResetEntity(d\OBJ)
					If d\OBJ2 <> 0 Then ResetEntity(d\OBJ2)
					If d\TimerState > 0.0 Then
						d\TimerState = Max(0.0, d\TimerState - fps\Factor[0])
						If d\TimerState + fps\Factor[0] > 110.0 And d\TimerState =< 110.0 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\OBJ)
						
						If d\TimerState = 0.0 Then 
							d\Open = (Not d\Open)
							If d\DoorType <> Default_Door And d\DoorType <> One_Sided_Door Then
								d\SoundCHN = PlaySound2(CloseDoorSFX(d\DoorType, Rand(0, 2)), Camera, d\OBJ)
							Else
								d\SoundCHN = PlaySound2(CloseDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
							EndIf
						EndIf
					EndIf
					If d\AutoClose And RemoteDoorOn Then
						If EntityDistanceSquared(Camera, d\OBJ) < 4.41 Then
							If (Not I_714\Using) And wi\GasMask <> 3 And wi\HazmatSuit <> 3 Then PlaySound_Strict(HorrorSFX[7])
							d\Open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\DoorType, 1), Rand(0, 2)), Camera, d\OBJ) : d\AutoClose = False
						EndIf
					EndIf				
				EndIf
			Else
				If d\OpenState > 0.0 Then
					Select d\DoorType
						Case Default_Door
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) * (d\FastOpen + 1) / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fps\Factor[0]) / 80.0, 0.0, 0.0)	
							;[End Block]
						Case Big_Door
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 0.8))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) / 180.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * fps\Factor[0] / 180.0, 0.0, 0.0)
							If d\OpenState < 15.0 And d\OpenState + fps\Factor[0] >= 15.0
								If opt\ParticleAmount = 2 Then
									For i = 0 To Rand(75, 99)
										Local Pvt% = CreatePivot()
										
										PositionEntity(Pvt, EntityX(d\FrameOBJ, True) + Rnd(-0.2, 0.2), EntityY(d\FrameOBJ, True) + Rnd(0.0, 1.2), EntityZ(d\FrameOBJ, True) + Rnd(-0.2, 0.2))
										RotateEntity(Pvt, 0.0, Rnd(360.0), 0.0)
										
										p.Particles = CreateParticle(3, EntityX(Pvt), EntityY(Pvt), EntityZ(Pvt), 0.002, 0.0, 300.0)
										p\Speed = 0.005 : p\SizeChange = -0.00001 : p\Size = 0.01 : p\Alphachange = -0.01
										RotateEntity(p\Pvt, Rnd(-20.0, 20.0), Rnd(360.0), 0.0)
										ScaleSprite(p\OBJ, p\Size, p\Size)
										EntityOrder(p\OBJ, -1)
										FreeEntity(Pvt)
									Next
								EndIf
							EndIf
							;[End Block]
						Case Heavy_Door
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) * (d\FastOpen + 1) / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fps\Factor[0]) / 120.0, 0.0, 0.0)
							;[End Block]
						Case Elevator_Door
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) * (d\FastOpen + 1) / 162.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * (-fps\Factor[0]) / 162.0, 0.0, 0.0)
							;[End Block]
						Case One_Sided_Door
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 2.0 * (d\FastOpen + 1)))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) * (d\FastOpen + 1) / 80.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * (d\FastOpen + 1) * fps\Factor[0] / 80.0, 0.0, 0.0)
							;[End Block]	
						Case SCP_914_Door ; ~ Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState - (fps\Factor[0] * 1.4))
							MoveEntity(d\OBJ, Sin(d\OpenState) * (-fps\Factor[0]) / 114.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, Sin(d\OpenState) * fps\Factor[0] / 114.0, 0.0, 0.0)
							;[End Block]
					End Select
					
					If d\Angle = 0.0 Lor d\Angle = 180.0 Then
						If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(me\Collider)) < 0.15 Then
							If Abs(EntityX(d\FrameOBJ, True) - EntityX(me\Collider)) < 0.7 * (d\DoorType * 2 + 1) Then
								z = CurveValue(EntityZ(d\FrameOBJ, True) + 0.15 * Sgn(EntityZ(me\Collider) - EntityZ(d\FrameOBJ, True)), EntityZ(me\Collider), 5)
								PositionEntity(me\Collider, EntityX(me\Collider), EntityY(me\Collider), z)
							EndIf
						EndIf
					Else
						If Abs(EntityX(d\FrameOBJ, True) - EntityX(me\Collider)) < 0.15 Then	
							If Abs(EntityZ(d\FrameOBJ, True) - EntityZ(me\Collider)) < 0.7 * (d\DoorType * 2 + 1) Then
								x = CurveValue(EntityX(d\FrameOBJ, True) + 0.15 * Sgn(EntityX(me\Collider) - EntityX(d\FrameOBJ, True)), EntityX(me\Collider), 5)
								PositionEntity(me\Collider, x, EntityY(me\Collider), EntityZ(me\Collider))
							EndIf
						EndIf
					EndIf
				Else
					d\FastOpen = 0
					PositionEntity(d\OBJ, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
					If d\DoorType = Default_Door Lor d\DoorType = One_Sided_Door Lor d\DoorType = SCP_914_Door Then
						MoveEntity(d\OBJ, 0.0, 0.0, 8.0 * RoomScale)
					EndIf
					If d\OBJ2 <> 0 Then
						PositionEntity(d\OBJ2, EntityX(d\FrameOBJ, True), EntityY(d\FrameOBJ, True), EntityZ(d\FrameOBJ, True))
						If d\DoorType = Default_Door Lor d\DoorType = One_Sided_Door Lor d\DoorType = SCP_914_Door Then
							MoveEntity(d\OBJ2, 0.0, 0.0, 8.0 * RoomScale)
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\FrameOBJ)
		
		If d\Locked <> d\LockedUpdated Then
			If d\Locked = 1 Then
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then EntityTexture(d\Buttons[i], t\MiscTextureID[17])
				Next
			Else
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then EntityTexture(d\Buttons[i], t\MiscTextureID[16])
				Next
			EndIf
			d\LockedUpdated = d\Locked
		EndIf
		
		If d\DoorType = Big_Door Then
			If d\Locked = 2 Then
				If d\OpenState > 48.0 Then
					d\Open = False
					d\OpenState = Min(d\OpenState, 48.0)
				EndIf	
			EndIf
		EndIf
	Next
End Function

Function UpdateElevators#(State#, door1.Doors, door2.Doors, FirstPivot%, SecondPivot%, event.Events, IgnoreRotation% = True)
	Local n.NPCs, it.Items, de.Decals
	Local x#, z#, Dist#, Dir#
	
	door1\IsElevatorDoor = 1
	door2\IsElevatorDoor = 1
	If door1\Open And (Not door2\Open) And door1\OpenState = 180.0 Then 
		State = -1.0
		door1\Locked = 0
		If (ClosestButton = door2\Buttons[0] Lor ClosestButton = door2\Buttons[1]) And mo\MouseHit1 Then
			UseDoor(door1, False)
		EndIf
	ElseIf door2\Open And (Not door1\Open) And door2\OpenState = 180.0 Then
		State = 1.0
		door2\Locked = 0
		If (ClosestButton = door1\Buttons[0] Lor ClosestButton = door1\Buttons[1]) And mo\MouseHit1 Then
			UseDoor(door2, False)
		EndIf
	ElseIf Abs(door1\OpenState - door2\OpenState) < 0.2 Then
		door1\IsElevatorDoor = 2
		door2\IsElevatorDoor = 2
	EndIf
	
	door1\Locked = 1
	door2\Locked = 1
	If door1\Open Then
		door1\IsElevatorDoor = 3
		If Abs(EntityX(me\Collider) - EntityX(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
			If Abs(EntityZ(me\Collider) - EntityZ(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
				If Abs(EntityY(me\Collider) - EntityY(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
					door1\Locked = 0
					door1\IsElevatorDoor = 1
				EndIf
			EndIf
		EndIf
	EndIf
	If door2\Open Then
		door2\IsElevatorDoor = 3
		If Abs(EntityX(me\Collider) - EntityX(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
			If Abs(EntityZ(me\Collider) - EntityZ(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
				If Abs(EntityY(me\Collider) - EntityY(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
					door2\Locked = 0
					door2\IsElevatorDoor = 1
				EndIf
			EndIf
		EndIf	
	EndIf
	
	Local Inside% = False
	
	If (Not door1\Open) And (Not door2\Open) Then
		door1\Locked = 1
		door2\Locked = 1
		If door1\OpenState = 0.0 And door2\OpenState = 0.0 Then
			If State < 0.0 Then
				State = State - fps\Factor[0]
				If Abs(EntityX(me\Collider) - EntityX(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
					If Abs(EntityZ(me\Collider) - EntityZ(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
						If Abs(EntityY(me\Collider) - EntityY(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
							Inside = True
							
							If (Not event\SoundCHN) Then
								event\SoundCHN = PlaySound_Strict(ElevatorMoveSFX)
							Else
								If (Not ChannelPlaying(event\SoundCHN)) Then event\SoundCHN = PlaySound_Strict(ElevatorMoveSFX)
							EndIf
							
							me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						EndIf
					EndIf
				EndIf
				
				If State < -500.0 Then
					door1\Locked = 1
					door2\Locked = 0
					State = 0.0
					If Inside Then
						If (Not IgnoreRotation) Then
							Dist = Distance(EntityX(me\Collider, True), EntityX(FirstPivot, True), EntityZ(me\Collider, True), EntityZ(FirstPivot, True))
							Dir = PointDirection(EntityX(me\Collider, True), EntityZ(me\Collider, True), EntityX(FirstPivot, True), EntityZ(FirstPivot, True))
							Dir = Dir + EntityYaw(SecondPivot, True) - EntityYaw(FirstPivot, True)
							Dir = WrapAngle(Dir)
							x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
							z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
							RotateEntity(me\Collider, EntityPitch(me\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(me\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(me\Collider, True), True)
						Else
							x = Max(Min((EntityX(me\Collider) - EntityX(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
							z = Max(Min((EntityZ(me\Collider) - EntityZ(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
						EndIf
						
						TeleportEntity(me\Collider, EntityX(SecondPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(SecondPivot, True) + (EntityY(me\Collider) - EntityY(FirstPivot, True)), EntityZ(SecondPivot, True) + z, 0.3, True)
						UpdateDoorsTimer = 0.0
						me\DropSpeed = 0.0
						UpdateDoors()
						UpdateRooms()
						
						door2\SoundCHN = PlaySound_Strict(OpenDoorSFX(3, Rand(0, 2)))
					EndIf
					
					For n.NPCs = Each NPCs
						If Abs(EntityX(n\Collider) - EntityX(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(n\Collider) - EntityZ(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(n\Collider) - EntityY(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(n\Collider, True), EntityX(FirstPivot, True), EntityZ(n\Collider, True), EntityZ(FirstPivot, True))
										Dir = PointDirection(EntityX(n\Collider, True), EntityZ(n\Collider, True), EntityX(FirstPivot, True), EntityZ(FirstPivot, True))
										Dir = Dir + EntityYaw(SecondPivot, True) - EntityYaw(FirstPivot, True)
										Dir = WrapAngle(Dir)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(n\Collider, EntityPitch(n\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(n\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(n\Collider, True), True)
									Else
										x = Max(Min((EntityX(n\Collider) - EntityX(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(n\Collider) - EntityZ(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									
									TeleportEntity(n\Collider, EntityX(SecondPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(SecondPivot, True) + (EntityY(n\Collider) - EntityY(FirstPivot, True)), EntityZ(SecondPivot, True) + z, n\CollRadius, True)
									If n = Curr173 Then
										Curr173\IdleTimer = 10.0
									EndIf
								EndIf
							EndIf
						EndIf
					Next
					
					For it.Items = Each Items
						If Abs(EntityX(it\Collider) - EntityX(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(it\Collider) - EntityZ(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(it\Collider) - EntityY(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(it\Collider, True), EntityX(FirstPivot, True), EntityZ(it\Collider, True), EntityZ(FirstPivot, True))
										Dir = PointDirection(EntityX(it\Collider, True), EntityZ(it\Collider, True), EntityX(FirstPivot, True), EntityZ(FirstPivot, True))
										Dir = Dir + EntityYaw(SecondPivot, True) - EntityYaw(FirstPivot, True)
										Dir = WrapAngle(Dir)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(it\Collider, EntityPitch(it\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(it\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(it\Collider, True), True)
									Else
										x = Max(Min((EntityX(it\Collider) - EntityX(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(it\Collider) - EntityZ(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									TeleportEntity(it\Collider, EntityX(SecondPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(SecondPivot, True) + (EntityY(it\Collider) - EntityY(FirstPivot, True)), EntityZ(SecondPivot, True) + z, 0.01, True)
								EndIf
							EndIf
						EndIf
					Next
					
					For de.Decals = Each Decals
						If Abs(EntityX(de\OBJ) - EntityX(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(de\OBJ) - EntityZ(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(de\OBJ) - EntityY(FirstPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(de\OBJ, True), EntityX(FirstPivot, True), EntityZ(de\OBJ, True), EntityZ(FirstPivot, True))
										Dir = PointDirection(EntityX(de\OBJ, True), EntityZ(de\OBJ, True), EntityX(FirstPivot, True), EntityZ(FirstPivot, True))
										Dir = Dir + EntityYaw(SecondPivot, True) - EntityYaw(FirstPivot, True)
										Dir = WrapAngle(Dir)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(de\OBJ, True), EntityYaw(FirstPivot, True)), EntityRoll(de\OBJ, True), True)
									Else
										x = Max(Min((EntityX(de\OBJ) - EntityX(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(de\OBJ) - EntityZ(FirstPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									TeleportEntity(de\OBJ, EntityX(SecondPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(SecondPivot, True) + (EntityY(de\OBJ) - EntityY(FirstPivot, True)), EntityZ(SecondPivot, True) + z, 0.01, True)
								EndIf
							EndIf
						EndIf
					Next
					UseDoor(door2, False, (Not Inside))
					door1\Open = False
					
					PlaySound2(ElevatorBeepSFX, Camera, FirstPivot, 4.0)
				EndIf
			Else
				State = State + fps\Factor[0]
				If Abs(EntityX(me\Collider) - EntityX(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
					If Abs(EntityZ(me\Collider) - EntityZ(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then	
						If Abs(EntityY(me\Collider) - EntityY(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							Inside = True
							
							If (Not event\SoundCHN) Then
								event\SoundCHN = PlaySound_Strict(ElevatorMoveSFX)
							Else
								If (Not ChannelPlaying(event\SoundCHN)) Then event\SoundCHN = PlaySound_Strict(ElevatorMoveSFX)
							EndIf
							
							me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						EndIf
					EndIf
				EndIf	
				
				If State > 500.0 Then 
					door1\Locked = 0
					door2\Locked = 1
					State = 0.0
					If Inside Then
						If (Not IgnoreRotation) Then
							Dist = Distance(EntityX(me\Collider, True), EntityX(SecondPivot, True), EntityZ(me\Collider, True), EntityZ(SecondPivot, True))
							Dir = PointDirection(EntityX(me\Collider, True), EntityZ(me\Collider, True), EntityX(SecondPivot, True), EntityZ(SecondPivot, True))
							Dir = Dir + EntityYaw(FirstPivot, True) - EntityYaw(SecondPivot, True)
							x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
							z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
							RotateEntity(me\Collider, EntityPitch(me\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(me\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(me\Collider, True), True)
						Else
							x = Max(Min((EntityX(me\Collider) - EntityX(SecondPivot, True)), (280 * RoomScale) - 0.22), ((-280) * RoomScale) + 0.22)
							z = Max(Min((EntityZ(me\Collider) - EntityZ(SecondPivot, True)), (280 * RoomScale) - 0.22), ((-280) * RoomScale) + 0.22)
						EndIf
						TeleportEntity(me\Collider, EntityX(FirstPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(FirstPivot, True) + (EntityY(me\Collider) - EntityY(SecondPivot, True)), EntityZ(FirstPivot, True) + z, 0.3, True)
						UpdateDoorsTimer = 0.0
						me\DropSpeed = 0.0
						UpdateDoors()
						UpdateRooms()
						
						door1\SoundCHN = PlaySound_Strict(OpenDoorSFX(3, Rand(0, 2)))
					EndIf
					
					For n.NPCs = Each NPCs
						If Abs(EntityX(n\Collider) - EntityX(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(n\Collider) - EntityZ(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(n\Collider) - EntityY(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(n\Collider, True), EntityX(SecondPivot, True), EntityZ(n\Collider, True), EntityZ(SecondPivot, True))
										Dir = PointDirection(EntityX(n\Collider, True), EntityZ(n\Collider, True), EntityX(SecondPivot, True), EntityZ(SecondPivot, True))
										Dir = Dir + EntityYaw(FirstPivot, True) - EntityYaw(SecondPivot, True)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(n\Collider, EntityPitch(n\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(n\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(n\Collider, True), True)
									Else
										x = Max(Min((EntityX(n\Collider) - EntityX(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(n\Collider) - EntityZ(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									TeleportEntity(n\Collider, EntityX(FirstPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(FirstPivot, True) + (EntityY(n\Collider) - EntityY(SecondPivot, True)), EntityZ(FirstPivot, True) + z, n\CollRadius, True)
									If n = Curr173 Then
										Curr173\IdleTimer = 10.0
									EndIf
								EndIf
							EndIf
						EndIf
					Next
					
					For it.Items = Each Items
						If Abs(EntityX(it\Collider) - EntityX(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(it\Collider) - EntityZ(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(it\Collider) - EntityY(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(it\Collider, True), EntityX(SecondPivot, True), EntityZ(it\Collider, True), EntityZ(SecondPivot, True))
										Dir = PointDirection(EntityX(it\Collider, True), EntityZ(it\Collider, True), EntityX(SecondPivot, True), EntityZ(SecondPivot, True))
										Dir = Dir + EntityYaw(FirstPivot, True) - EntityYaw(SecondPivot, True)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(it\Collider, EntityPitch(it\Collider, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(it\Collider, True), EntityYaw(FirstPivot, True)), EntityRoll(it\Collider, True), True)
									Else
										x = Max(Min((EntityX(it\Collider) - EntityX(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(it\Collider) - EntityZ(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									TeleportEntity(it\Collider, EntityX(FirstPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(FirstPivot, True) + (EntityY(it\Collider) - EntityY(SecondPivot, True)), EntityZ(FirstPivot, True) + z, 0.01, True)
								EndIf
							EndIf
						EndIf
					Next
					
					For de.Decals = Each Decals
						If Abs(EntityX(de\OBJ) - EntityX(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
							If Abs(EntityZ(de\OBJ) - EntityZ(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
								If Abs(EntityY(de\OBJ) - EntityY(SecondPivot, True)) < (280.0 * RoomScale) + (0.015 * fps\Factor[0]) Then
									If (Not IgnoreRotation) Then
										Dist = Distance(EntityX(de\OBJ, True), EntityX(SecondPivot, True), EntityZ(de\OBJ, True), EntityZ(SecondPivot, True))
										Dir = PointDirection(EntityX(de\OBJ, True), EntityZ(de\OBJ, True), EntityX(SecondPivot, True), EntityZ(SecondPivot, True))
										Dir = Dir + EntityYaw(FirstPivot, True) - EntityYaw(SecondPivot, True)
										x = Max(Min(Cos(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min(Sin(Dir) * Dist, (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), EntityYaw(SecondPivot, True) + AngleDist(EntityYaw(de\OBJ, True), EntityYaw(FirstPivot, True)), EntityRoll(de\OBJ, True), True)
									Else
										x = Max(Min((EntityX(de\OBJ) - EntityX(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
										z = Max(Min((EntityZ(de\OBJ) - EntityZ(SecondPivot, True)), (280.0 * RoomScale) - 0.22), ((-280.0) * RoomScale) + 0.22)
									EndIf
									TeleportEntity(de\OBJ, EntityX(FirstPivot, True) + x, (0.1 * fps\Factor[0]) + EntityY(FirstPivot, True) + (EntityY(de\OBJ) - EntityY(SecondPivot, True)), EntityZ(FirstPivot, True) + z, 0.01, True)
								EndIf
							EndIf
						EndIf
					Next
					UseDoor(door1, False, (Not Inside))
					door2\Open = False
					
					PlaySound2(ElevatorBeepSFX, Camera, SecondPivot, 4.0)
				EndIf	
			EndIf
		EndIf
	EndIf
	Return(State)
End Function

Function UseDoor(d.Doors, ShowMsg% = True, PlaySFX% = True, Scripted% = False)
	Local Temp% = 0
	
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If ShowMsg Then
				CreateMsg("A keycard is required to operate this door.", 6.0)
			EndIf
			If (Not Scripted) Then Return
		Else
			Select SelectedItem\ItemTemplate\TempName
				Case "key6"
					;[Block]
					Temp = 1
					;[End Block]
				Case "key0"
					;[Block]
					Temp = 2
					;[End Block]
				Case "key1"
					;[Block]
					Temp = 3
					;[End Block]
				Case "key2"
					;[Block]
					Temp = 4
					;[End Block]
				Case "key3"
					;[Block]
					Temp = 5
					;[End Block]
				Case "key4"
					;[Block]
					Temp = 6
					;[End Block]
				Case "key5"
					;[Block]
					Temp = 7
					;[End Block]
				Case "keyomni"
					;[Block]
					Temp = 8
					;[End Block]
				Case "scp005"
					;[Block]
					Temp = 9
					;[End Block]
				Default
					;[Block]
					Temp = -1
					;[End Block]
			End Select
			
			If Temp = -1 Then 
				If ShowMsg Then CreateMsg("A keycard is required to operate this door.", 6.0)
				If (Not Scripted) Then Return				
			ElseIf Temp >= d\KeyCard 
				SelectedItem = Null
				If d\Locked = 1 Then
					If ShowMsg Then
						PlaySound_Strict(KeyCardSFX2)
						If Temp = 1 Then 
							CreateMsg("The keycard was inserted into the slot. UNKNOWN ERROR! " + Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34), 8.0)
						Else
							If Temp = 9 Then
								CreateMsg("You hold the key close to the slot but nothing happened.", 6.0)
							Else
								CreateMsg("The keycard was inserted into the slot but nothing happened.", 6.0)
							EndIf
						EndIf
					EndIf
					If (Not Scripted) Then Return
				Else
					If ShowMsg Then
						PlaySound_Strict(KeyCardSFX1)
						If Temp = 9 Then
							CreateMsg("You hold the key close to the slot.", 6.0)
						Else
							CreateMsg("The keycard was inserted into the slot.", 6.0)
						EndIf
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If ShowMsg Then 
					PlaySound_Strict(KeyCardSFX2)					
					If d\Locked = 1 Then
						If Temp = 1 Then 
							CreateMsg("The keycard was inserted into the slot. UNKNOWN ERROR! " +  Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34), 8.0)
						Else
							If Temp = 9 Then
								CreateMsg("You hold the key close to the slot but nothing happened.", 6.0)
							Else
								CreateMsg("The keycard was inserted into the slot but nothing happened.", 6.0)
							EndIf
						EndIf
					Else
						If Temp = 1 Then 
							CreateMsg("The keycard was inserted into the slot. UNKNOWN ERROR! " + Chr(34) + "Do" + Chr(Rand(48, 122)) + "s th" + Chr(Rand(48, 122)) + " B" + Chr(Rand(48, 122)) + "ack " + Chr(Rand(48, 122)) + "oon howl? " + Chr(Rand(48, 122)) + "es. N" + Chr(Rand(48, 122)) + ". Ye" + Chr(Rand(48, 122)) + ". " + Chr(Rand(48, 122)) + "o." + Chr(34), 8.0)
						Else
							If Temp = 9 Then
								CreateMsg("You hold the key close to the slot but nothing happened.", 6.0)
							Else
								CreateMsg("A keycard with security clearance " + (d\KeyCard - 2) + " or higher is required to operate this door.", 6.0)
							EndIf
						EndIf
					EndIf
				EndIf
				If (Not Scripted) Then Return
			EndIf
		EndIf	
	ElseIf d\KeyCard < 0
		; ~ I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable -- risingstar64
		If SelectedItem <> Null Then
			If SelectedItem\ItemTemplate\TempName = "scp005" Then
				Temp = 2
			ElseIf SelectedItem\ItemTemplate\TempName = "key0" Lor SelectedItem\ItemTemplate\TempName = "key1" Lor SelectedItem\ItemTemplate\TempName = "key2" Lor SelectedItem\ItemTemplate\TempName = "key3" Lor SelectedItem\ItemTemplate\TempName = "key4" Lor SelectedItem\ItemTemplate\TempName = "key5" Lor SelectedItem\ItemTemplate\TempName = "key6" Lor SelectedItem\ItemTemplate\TempName = "keyomni"
				Temp = 3
			ElseIf SelectedItem\ItemTemplate\TempName = "key" Lor SelectedItem\ItemTemplate\TempName = "scp860"
				Temp = 4
			Else
				Temp = (SelectedItem\ItemTemplate\TempName = "hand" And d\KeyCard = -1) Lor (SelectedItem\ItemTemplate\TempName = "hand2" And d\KeyCard = -2)
			EndIf
		EndIf
		SelectedItem = Null
		If Temp <> 0 Then
			If Temp >= 3 Then
				If ShowMsg Then
					PlaySound_Strict(ButtonSFX)
					If Temp = 4 Then
						CreateMsg("There is no place to insert the key.", 6.0)
					Else
						CreateMsg("The type of this slot doesn't require keycards.", 6.0)
					EndIf
				EndIf
				If (Not Scripted) Then Return
			Else
				If ShowMsg Then
					PlaySound_Strict(ScannerSFX1)
					If Temp = 2 Then
						CreateMsg("You hold the key onto the scanner. The scanner reads: " + Chr(34) + "Unknown DNA verified. ERROR! Access granted." + Chr(34), 6.0)
					Else
						CreateMsg("You place the palm of the hand onto the scanner. The scanner reads: " + Chr(34) + "DNA verified. Access granted." + Chr(34), 6.0)
					EndIf
				EndIf
			EndIf
		Else
			If ShowMsg Then
				PlaySound_Strict(ScannerSFX2)
				CreateMsg("You placed your palm onto the scanner. The scanner reads: " + Chr(34) + "DNA doesn't match known sample. Access denied." + Chr(34), 6.0)
			EndIf
			If (Not Scripted) Then Return
		EndIf
	Else
		If d\Locked = 1 Then
			If ShowMsg Then 
				If (Not d\IsElevatorDoor > 0) Then
					PlaySound_Strict(ButtonSFX2)
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
						If d\Open Then
							CreateMsg("You pushed the button but nothing happened.", 6.0)
						Else
							CreateMsg("The door appears to be locked.", 6.0)
						EndIf    
					Else
						CreateMsg("The elevator appears to be broken.", 6.0)
					EndIf
				Else
					If d\IsElevatorDoor = 1 Then
						CreateMsg("You called the elevator.", 6.0)
					ElseIf d\IsElevatorDoor = 3 Then
						CreateMsg("The elevator is already on this floor.", 6.0)
					ElseIf msg\Txt <> "You called the elevator."
						Select Rand(10)
							Case 1
								;[Block]
								CreateMsg("Stop spamming the button.", 6.0)
								;[End Block]
							Case 2
								;[Block]
								CreateMsg("Pressing it harder doesn't make the elevator come faster.", 6.0)
								;[End Block]
							Case 3
								;[Block]
								CreateMsg("If you continue pressing this button I will generate a Memory Access Violation.", 6.0)
								;[End Block]
							Default
								;[Block]
								CreateMsg("You already called the elevator.", 6.0)
								;[End Block]
						End Select
					Else
						CreateMsg("You already called the elevator.", 6.0)
					EndIf
				EndIf
			EndIf
			If (Not Scripted) Then Return
		EndIf	
	EndIf
	
	d\Open = (Not d\Open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\Open = (Not d\LinkedDoor\Open)
	
	If d\Open Then
		If d\LinkedDoor <> Null Then d\LinkedDoor\TimerState = d\LinkedDoor\Timer
		d\TimerState = d\Timer
	EndIf
	
	If PlaySFX Then
		If d\Open Then
			If d\DoorType = Big_Door And d\Locked = 2 Then
				d\SoundCHN = PlaySound2(BigDoorErrorSFX[Rand(0, 2)], Camera, d\OBJ)
			Else
				If d\DoorType <> Default_Door And d\DoorType <> One_Sided_Door Then
					d\SoundCHN = PlaySound2(OpenDoorSFX(d\DoorType, Rand(0, 2)), Camera, d\OBJ)
				Else
					d\SoundCHN = PlaySound2(OpenDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
				EndIf
			EndIf
		Else
			If d\DoorType <> Default_Door And d\DoorType <> One_Sided_Door Then
				d\SoundCHN = PlaySound2(CloseDoorSFX(d\DoorType, Rand(0, 2)), Camera, d\OBJ)
			Else
				d\SoundCHN = PlaySound2(CloseDoorSFX(0, Rand(0, 2)), Camera, d\OBJ)
			EndIf
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\OBJ)
	EndIf
End Function

Function RemoveDoor(d.Doors)
	Local i%
	
	If d\OBJ <> 0 Then FreeEntity(d\OBJ) : d\OBJ = 0
	If d\OBJ2 <> 0 Then FreeEntity(d\OBJ2) : d\OBJ2 = 0
	For i = 0 To 1
		If d\Buttons[i] <> 0 Then FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
	Next
	If d\FrameOBJ <> 0 Then FreeEntity(d\FrameOBJ) : d\FrameOBJ = 0
	Delete(d)
End Function

Type Decals
	Field OBJ%, ID%
	Field Size#, SizeChange#, MaxSize#
	Field Alpha#, AlphaChange#
	Field BlendMode%, FX%
	Field R%, G%, B%
	Field Timer#, LifeTime#
End Type

Function CreateDecal.Decals(ID%, x#, y#, z#, Pitch#, Yaw#, Roll#, Size# = 1.0, Alpha# = 1.0, FX% = 0, BlendMode% = 1, R% = 0, G% = 0, B% = 0)
	Local d.Decals
	
	d.Decals = New Decals
	d\ID = ID
	d\Size = Size
	d\Alpha = Alpha
	d\FX = FX : d\BlendMode = BlendMode
	d\R = R : d\G = G : d\B = B
	d\MaxSize = 1.0
	
	d\OBJ = CreateSprite()
	PositionEntity(d\OBJ, x, y, z)
	ScaleSprite(d\OBJ, Size, Size)
	RotateEntity(d\OBJ, Pitch, Yaw, Roll)
	EntityTexture(d\OBJ, t\DecalTextureID[ID])
	EntityAlpha(d\OBJ, Alpha)
	EntityFX(d\OBJ, FX)
	EntityBlend(d\OBJ, BlendMode)
	SpriteViewMode(d\OBJ, 2)
	If R <> 0 Lor G <> 0 Lor B <> 0 Then EntityColor(d\OBJ, R, G, B)
	
	If (Not t\DecalTextureID[ID]) Then
		CreateConsoleMsg("Decal Texture ID: " + ID + " not found.")
		If opt\ConsoleOpening And opt\CanOpenConsole Then
			ConsoleOpen = True
		EndIf
		Return(Null)
	EndIf
	Return(d)
End Function

Function UpdateDecals()
	Local d.Decals
	
	For d.Decals = Each Decals
		If d\SizeChange <> 0.0 Then
			d\Size = d\Size + d\SizeChange * fps\Factor[0]
			ScaleSprite(d\OBJ, d\Size, d\Size)
			
			Select d\ID
				Case 0
					;[Block]
					If d\Timer =< 0.0 Then
						Local Angle# = Rnd(360.0)
						Local Temp# = Rnd(d\Size)
						Local d2.Decals
						
						d2.Decals = CreateDecal(1, EntityX(d\OBJ) + Cos(Angle) * Temp, EntityY(d\OBJ) - 0.0005, EntityZ(d\OBJ) + Sin(Angle) * Temp, EntityPitch(d\OBJ), EntityYaw(d\OBJ), EntityRoll(d\OBJ), Rnd(0.1, 0.5))
						PlaySound2(DecaySFX[Rand(1, 3)], Camera, d2\OBJ, 10.0, Rnd(0.1, 0.5))
						d\Timer = Rnd(50.0, 100.0)
					Else
						d\Timer = d\Timer - fps\Factor[0]
					EndIf
					;[End Block]
			End Select
			
			If d\Size >= d\MaxSize Then
				d\SizeChange = 0.0
				d\Size = d\MaxSize
			EndIf
		EndIf
		
		If d\AlphaChange <> 0.0 Then
			d\Alpha = Min(d\Alpha + (fps\Factor[0] * d\AlphaChange), 1.0)
			EntityAlpha(d\OBJ, d\Alpha)
		EndIf
		
		If d\LifeTime > 0.0 Then
			d\LifeTime = Max(d\LifeTime - fps\Factor[0], 5.0)
		EndIf
		
		If d\Size =< 0.0 Lor d\Alpha =< 0.0 Lor d\LifeTime = 5.0 Then
			FreeEntity(d\OBJ) : d\OBJ = 0
			Delete(d)
		EndIf
	Next
End Function

Global ScreenTexs%[2]

Type SecurityCams
	Field OBJ%, MonitorOBJ%, Pvt%
	Field BaseOBJ%, CameraOBJ%
	Field ScrOBJ%, ScrWidth#, ScrHeight#
	Field Screen%, Cam%, ScrTexture%, ScrOverlay%
	Field Angle#, Turn#, CurrAngle#
	Field State#, PlayerState%
	Field SoundCHN%
	Field InSight%
	Field RenderInterval#
	Field room.Rooms
	Field FollowPlayer%
	Field CoffinEffect%
	Field AllowSaving%
	Field MinAngle#, MaxAngle#, Dir%
End Type

Global SelectedMonitor.SecurityCams
Global CoffinCam.SecurityCams

Function CreateSecurityCam.SecurityCams(x1#, y1#, z1#, r.Rooms, Screen% = False, x2# = 0.0, y2# = 0.0, z2# = 0.0)
	Local sc.SecurityCams
	
	sc.SecurityCams = New SecurityCams
	sc\OBJ = CopyEntity(o\CamModelID[0])
	ScaleEntity(sc\OBJ, 0.0015, 0.0015, 0.0015)
	PositionEntity(sc\OBJ, x1, y1, z1)
	sc\CameraOBJ = CopyEntity(o\CamModelID[1])
	ScaleEntity(sc\CameraOBJ, 0.01, 0.01, 0.01)
	
	sc\room = r
	
	sc\Screen = Screen
	If Screen Then
		sc\AllowSaving = True
		
		sc\RenderInterval = 12.0
		
		Local Scale# = RoomScale * 4.5 * 0.4
		
		sc\ScrOBJ = CreateSprite()
		EntityFX(sc\ScrOBJ, 17)
		SpriteViewMode(sc\ScrOBJ, 2)
		sc\ScrTexture = 0
		EntityTexture(sc\ScrOBJ, ScreenTexs[sc\ScrTexture])
		ScaleSprite(sc\ScrOBJ, MeshWidth(o\MonitorModelID[0]) * Scale * 0.95 * 0.5, MeshHeight(o\MonitorModelID[0]) * Scale * 0.95 * 0.5)
		PositionEntity(sc\ScrOBJ, x2, y2, z2)
		
		sc\ScrOverlay = CreateSprite(sc\ScrOBJ)
		ScaleSprite(sc\ScrOverlay, MeshWidth(o\MonitorModelID[0]) * Scale * 0.95 * 0.5, MeshHeight(o\MonitorModelID[0]) * Scale * 0.95 * 0.5)
		MoveEntity(sc\ScrOverlay, 0.0, 0.0, -0.005)
		EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
		SpriteViewMode(sc\ScrOverlay, 2)
		EntityFX(sc\ScrOverlay, 1)
		EntityBlend(sc\ScrOverlay, 3)
		
		sc\MonitorOBJ = CopyEntity(o\MonitorModelID[0], sc\ScrOBJ)
		ScaleEntity(sc\MonitorOBJ, Scale, Scale, Scale)
		
		sc\Cam = CreateCamera()
		CameraViewport(sc\Cam, 0, 0, 512, 512)
		CameraRange(sc\Cam, 0.01, 8.0)
		CameraZoom(sc\Cam, 0.8)
		HideEntity(sc\Cam)	
	EndIf
	
	If r <> Null Then 
		EntityParent(sc\OBJ, r\OBJ)
		If Screen Then
			If sc\ScrOBJ <> 0 Then EntityParent(sc\ScrOBJ, r\OBJ)
		EndIf
	EndIf
	Return(sc)
End Function

Function UpdateSecurityCams()
	CatchErrors("Uncaught (UpdateSecurityCams)")
	
	Local sc.SecurityCams
	
	; ~ CoffinEffect = 0, not affected by SCP-895
	; ~ CoffinEffect = 1, constantly affected by SCP-895
	; ~ CoffinEffect = 2, SCP-079 can broadcast SCP-895 feed on this screen
	; ~ CoffinEffect = 3, SCP-079 broadcasting SCP-895 feed
	
	For sc.SecurityCams = Each SecurityCams
		Local Close% = False
		
		If sc\room = Null Then
			HideEntity(sc\Cam)
		Else
			If sc\room\Dist < 6.0 Lor PlayerRoom = sc\room Then 
				Close = True
			ElseIf sc\Cam <> 0
				HideEntity(sc\Cam)
			EndIf
			
			If sc\room <> Null Then
				If sc\room\RoomTemplate\Name = "room2sl" Then sc\CoffinEffect = 0
			EndIf
			
			If Close Lor sc = CoffinCam Then 
				If sc\FollowPlayer Then
					If sc <> CoffinCam Then
						If EntityVisible(sc\CameraOBJ, Camera)
							If MTFCameraCheckTimer > 0.0 Then MTFCameraCheckDetected = True
						EndIf
					EndIf
					If (Not sc\Pvt) Then
						sc\Pvt = CreatePivot(sc\OBJ)
						EntityParent(sc\Pvt, 0) ; ~ Sets position and rotation of the pivot to the cam object
					EndIf
					PointEntity(sc\Pvt, Camera)
					
					RotateEntity(sc\CameraOBJ, CurveAngle(EntityPitch(sc\Pvt), EntityPitch(sc\CameraOBJ), 75.0), CurveAngle(EntityYaw(sc\Pvt), EntityYaw(sc\CameraOBJ), 75.0), 0.0)
					
					PositionEntity(sc\CameraOBJ, EntityX(sc\OBJ, True), EntityY(sc\OBJ, True) - 0.083, EntityZ(sc\OBJ, True))
				Else
					If sc\Turn > 0.0 Then
						If (Not sc\Dir) Then
							sc\CurrAngle = sc\CurrAngle + 0.2 * fps\Factor[0]
							If sc\CurrAngle > sc\Turn * 1.3 Then sc\Dir = True
						Else
							sc\CurrAngle = sc\CurrAngle - 0.2 * fps\Factor[0]
							If sc\CurrAngle < (-sc\Turn) * 1.3 Then sc\Dir = False
						EndIf
					EndIf
					PositionEntity(sc\CameraOBJ, EntityX(sc\OBJ, True), EntityY(sc\OBJ, True) - 0.083, EntityZ(sc\OBJ, True))
					RotateEntity(sc\CameraOBJ, EntityPitch(sc\CameraOBJ), sc\room\Angle + sc\Angle + Max(Min(sc\CurrAngle, sc\Turn), -sc\Turn), 0)
					
					If EntityInView(sc\CameraOBJ, Camera) And EntityVisible(sc\CameraOBJ, Camera) Then
						If (MilliSecs2() Mod 1200) < 800 Then
							EntityTexture(sc\CameraOBJ, t\MiscTextureID[19])
						Else
							EntityTexture(sc\CameraOBJ, t\MiscTextureID[18])
						EndIf
					EndIf
					
					If sc\Cam <> 0 Then 
						PositionEntity(sc\Cam, EntityX(sc\CameraOBJ, True), EntityY(sc\CameraOBJ, True), EntityZ(sc\CameraOBJ, True))
						RotateEntity(sc\Cam, EntityPitch(sc\CameraOBJ), EntityYaw(sc\CameraOBJ), 0.0)
						MoveEntity(sc\Cam, 0.0, 0.0, 0.1)
					EndIf
					
					If sc <> CoffinCam Then
						If Abs(DeltaYaw(sc\CameraOBJ, Camera)) < 60.0
							If EntityVisible(sc\CameraOBJ, Camera)
								If MTFCameraCheckTimer > 0.0 Then MTFCameraCheckDetected = True
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			
			If Close Then
				If sc\Screen Then
					If sc\State < sc\RenderInterval Then
						sc\State = sc\State + fps\Factor[0]
					Else
						sc\State = 0.0
					EndIf
					
					If me\BlinkTimer > -5.0 And EntityInView(sc\ScrOBJ, Camera) Then
						If EntityVisible(Camera, sc\ScrOBJ) Then
							If (sc\CoffinEffect = 1 Lor sc\CoffinEffect = 3) And (Not I_714\Using) And wi\HazmatSuit <> 3 And wi\GasMask <> 3 Then
								If me\BlinkTimer > -5.0 Then
									me\Sanity = me\Sanity - fps\Factor[0]
									me\RestoreSanity = False
								EndIf
							EndIf
						EndIf
					EndIf
					
					If me\Sanity < -1000.0 Then 
						msg\DeathMsg = Chr(34) + "What we know is that he died of cardiac arrest. My guess is that it was caused by SCP-895, although it has never been observed affecting video equipment from this far before. "
						msg\DeathMsg = msg\DeathMsg + "Further testing is needed to determine whether SCP-895's " + Chr(34) + "Red Zone" + Chr(34) + " is increasing." + Chr(34)
						
						If me\VomitTimer < -10.0 Then Kill()
					EndIf
					
					If me\VomitTimer < 0.0 And me\Sanity < -800.0 Then
						me\RestoreSanity = False
						me\Sanity = -1010.0
					EndIf
					
					If me\BlinkTimer > -5.0 And EntityInView(sc\ScrOBJ, Camera) And EntityVisible(Camera, sc\ScrOBJ) Then
						sc\InSight = True
					Else
						sc\InSight = False
					EndIf
					
					If (sc\CoffinEffect = 1 Lor sc\CoffinEffect = 3) And (Not I_714\Using) And wi\HazmatSuit <> 3 And wi\GasMask <> 3 Then
						If sc\InSight Then
							Local Pvt% = CreatePivot()
							
							PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
							PointEntity(Pvt, sc\ScrOBJ)
							
							RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
							
							TurnEntity(Pvt, 90.0, 0.0, 0.0)
							CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
							CameraPitch = CameraPitch - 90.0
							
							FreeEntity(Pvt)
							If (sc\CoffinEffect = 1 Lor sc\CoffinEffect = 3) And ((Not I_714\Using) And wi\GasMask <> 3 And wi\HazmatSuit <> 3) Then
								If me\Sanity < -800.0 Then
									If Rand(3) = 1 Then EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
									If Rand(6) < 5 Then
										EntityTexture(sc\ScrOverlay, t\MiscTextureID[Rand(7, 12)])
										If sc\PlayerState = 1 Then PlaySound_Strict(HorrorSFX[1])
										sc\PlayerState = 2
										If (Not sc\SoundCHN) Then
											sc\SoundCHN = PlaySound_Strict(HorrorSFX[4])
										Else
											If (Not ChannelPlaying(sc\SoundCHN)) Then sc\SoundCHN = PlaySound_Strict(HorrorSFX[4])
										EndIf
										If sc\CoffinEffect = 3 And Rand(200) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf	
									me\BlurTimer = 1000.0
									If me\VomitTimer = 0.0 Then me\VomitTimer = 1.0
								ElseIf me\Sanity < -500.0
									If Rand(7) = 1 Then EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
									If Rand(50) = 1 Then
										EntityTexture(sc\ScrOverlay, t\MiscTextureID[Rand(7, 12)])
										If sc\PlayerState = 0 Then PlaySound_Strict(HorrorSFX[0])
										sc\PlayerState = Max(sc\PlayerState, 1)
										If sc\CoffinEffect = 3 And Rand(100) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf
								Else
									EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
								EndIf
							EndIf
						EndIf
					Else
						If sc\InSight Then
							If I_714\Using Lor wi\HazmatSuit = 3 Lor wi\GasMask = 3 Then EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
						EndIf
					EndIf
					
					If sc\InSight And sc\CoffinEffect = 0 Lor sc\CoffinEffect = 2 Then
						If sc\PlayerState = 0 Then
							sc\PlayerState = Rand(60000, 65000)
						EndIf
						
						If Rand(500) = 1 Then EntityTexture(sc\ScrOverlay, t\MiscTextureID[Rand(1, 6)])
						
						If (MilliSecs2() Mod sc\PlayerState) >= Rand(600) Then
							EntityTexture(sc\ScrOverlay, t\MonitorTextureID[0])
						Else
							If (Not sc\SoundCHN) Then
								sc\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(1, 3) + ".ogg"))
								If sc\CoffinEffect = 2 Then sc\CoffinEffect = 3 : sc\PlayerState = 0
							ElseIf (Not ChannelPlaying(sc\SoundCHN))
								sc\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(1, 3) + ".ogg"))
								If sc\CoffinEffect = 2 Then sc\CoffinEffect = 3 : sc\PlayerState = 0
							EndIf
							EntityTexture(sc\ScrOverlay, t\MiscTextureID[Rand(1, 6)])
						EndIf
					EndIf
				EndIf
				If (Not sc\InSight) Then sc\SoundCHN = LoopSound2(CameraSFX, sc\SoundCHN, Camera, sc\CameraOBJ, 4.0)
			EndIf
			
			If sc <> Null Then
				If sc\room <> Null Then
					CatchErrors("UpdateSecurityCameras (" + sc\room\RoomTemplate\Name + ")")
				Else
					CatchErrors("UpdateSecurityCameras (screen has no room)")
				EndIf
			Else
				CatchErrors("UpdateSecurityCameras (screen doesn't exist anymore)")
			EndIf
		EndIf
	Next
End Function

Function RenderSecurityCams()
	CatchErrors("Uncaught (RenderSecurityCams)")
	
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		Local Close% = False
		
		If sc\room <> Null Then
			If sc\room\Dist < 6.0 Lor PlayerRoom = sc\room Then 
				Close = True
			EndIf
			
			If Close Then
				If sc\Screen Then
					If sc\State >= sc\RenderInterval Then
						If me\BlinkTimer > -5.0 And EntityInView(sc\ScrOBJ, Camera) Then
							If EntityVisible(Camera, sc\ScrOBJ) Then
								If CoffinCam = Null Lor Rand(5) = 5 Lor sc\CoffinEffect <> 3 Then
									HideEntity(Camera)
									ShowEntity(sc\Cam)
									Cls()
									
									RenderRoomLights(sc\Cam)
									
									SetBuffer(BackBuffer())
									RenderWorld()
									CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(ScreenTexs[sc\ScrTexture]))
									
									HideEntity(sc\Cam)
									ShowEntity(Camera)										
								Else
									HideEntity(Camera)
									ShowEntity(CoffinCam\room\OBJ)
									EntityAlpha(GetChild(CoffinCam\room\OBJ, 2), 1.0)
									ShowEntity(CoffinCam\Cam)
									Cls()
									
									RenderRoomLights(CoffinCam\Cam)
									
									SetBuffer(BackBuffer())
									RenderWorld()
									CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(ScreenTexs[sc\ScrTexture]))
									
									HideEntity(CoffinCam\room\OBJ)
									HideEntity(CoffinCam\Cam)
									ShowEntity(Camera)										
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			
			If sc <> Null Then
				If sc\room <> Null Then
					CatchErrors("RenderSecurityCameras (" + sc\room\RoomTemplate\Name + ")")
				Else
					CatchErrors("RenderSecurityCameras (screen has no room)")
				EndIf
			Else
				CatchErrors("RenderSecurityCameras (screen doesn't exist anymore)")
			EndIf
		EndIf
	Next
	Cls()
End Function

Function UpdateMonitorSaving()
	Local sc.SecurityCams
	Local Close% = False
	
	If SelectedDifficulty\SaveType <> SAVEONSCREENS Then Return
	
	For sc.SecurityCams = Each SecurityCams
		If sc\AllowSaving And sc\Screen Then
			Close = False
			If sc\room\Dist < 6.0 Lor PlayerRoom = sc\room Then 
				Close = True
			EndIf
			
			If Close And (Not GrabbedEntity) And (Not ClosestButton) Then
				If EntityInView(sc\ScrOBJ, Camera) And EntityDistanceSquared(sc\ScrOBJ, Camera) < 1.21 Then
					If EntityVisible(sc\ScrOBJ, Camera) Then
						ga\DrawHandIcon = True
						If mo\MouseHit1 Then SelectedMonitor = sc
					Else
						If SelectedMonitor = sc Then SelectedMonitor = Null
					EndIf
				Else
					If SelectedMonitor = sc Then SelectedMonitor = Null
				EndIf
				
				If SelectedMonitor = sc Then
					If sc\InSight Then
						Local Pvt% = CreatePivot()
						
						PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
						PointEntity(Pvt, sc\ScrOBJ)
						RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
						CameraPitch = CameraPitch - 90.0
						FreeEntity(Pvt)
					EndIf
				EndIf
			Else
				If SelectedMonitor = sc Then SelectedMonitor = Null
			EndIf
		EndIf
	Next
End Function

Function UpdateCheckpointMonitors(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Entity%, Name$
	
	Entity = o\MonitorModelID[1]
	
	If LCZ Then
		UpdateCheckpoint1 = True
	Else
		UpdateCheckpoint2 = True
	EndIf
	
	For i = 2 To CountSurfaces(Entity)
		SF = GetSurface(Entity, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitor_overlay.png"
					If LCZ Then
						If MonitorTimer < 50.0 Then
							BrushTexture(b, t\MonitorTextureID[2], 0, 0)
						Else
							BrushTexture(b, t\MonitorTextureID[3], 0, 0)
						EndIf
					Else
						If MonitorTimer2 < 50.0 Then
							BrushTexture(b, t\MonitorTextureID[2], 0, 0)
						Else
							BrushTexture(b, t\MonitorTextureID[1], 0, 0)
						EndIf
					EndIf
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then DeleteSingleTextureEntryFromCache(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
End Function

Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0
Global UpdateCheckpoint1%, UpdateCheckpoint2%

Function TurnCheckpointMonitorsOff(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Entity%, Name$
	
	Entity = o\MonitorModelID[1]
	
	If LCZ Then
		UpdateCheckpoint1 = False
		MonitorTimer = 0.0
	Else
		UpdateCheckpoint2 = False
		MonitorTimer2 = 0.0
	EndIf
	
	For i = 2 To CountSurfaces(Entity)
		SF = GetSurface(Entity, i)
		b = GetSurfaceBrush(SF)
		If b <> 0 Then
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0 Then
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitor_overlay.png"
					BrushTexture(b, t\MonitorTextureID[4], 0, 0)
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then DeleteSingleTextureEntryFromCache(t1)
			EndIf
			FreeBrush(b)
		EndIf
	Next
End Function

Function TimeCheckpointMonitors()
	If UpdateCheckpoint1 Then
		If MonitorTimer < 100.0
			MonitorTimer = Min(MonitorTimer + fps\Factor[0], 100.0)
		Else
			MonitorTimer = 0.0
		EndIf
	EndIf
	If UpdateCheckpoint2 Then
		If MonitorTimer2 < 100.0
			MonitorTimer2 = Min(MonitorTimer2 + fps\Factor[0], 100.0)
		Else
			MonitorTimer2 = 0.0
		EndIf
	EndIf
End Function

Global SelectedScreen.Screens

Type Screens
	Field OBJ%
	Field ImgPath$
	Field Img%
	Field room.Rooms
End Type

Type TempScreens
	Field ImgPath$
	Field x#, y#, z#
	Field RoomTemplate.RoomTemplates
End Type

Function CreateScreen.Screens(x#, y#, z#, ImgPath$, r.Rooms)
	Local s.Screens = New Screens
	
	s\OBJ = CreatePivot()
	EntityPickMode(s\OBJ, 1)	
	EntityRadius(s\OBJ, 0.1)
	
	PositionEntity(s\OBJ, x, y, z)
	
	; ~ A hacky way to use .png format
	If Right(ImgPath, 3) = "jpg" Then ImgPath = Left(ImgPath, Len(ImgPath) - 3) + "png"
	
	s\ImgPath = ImgPath
	s\room = r
	EntityParent(s\OBJ, r\OBJ)
	
	Return(s)
End Function

Function UpdateScreens()
	If SelectedScreen <> Null Then Return
	If SelectedDoor <> Null Then Return
	
	Local s.Screens
	
	For s.Screens = Each Screens
		If s\room = PlayerRoom Then
			If EntityDistanceSquared(me\Collider, s\OBJ) < 1.21 Then
				EntityPick(Camera, 1.2)
				If PickedEntity() = s\OBJ And s\ImgPath <> "" Then
					ga\DrawHandIcon = True
					If mo\MouseUp1 Then 
						SelectedScreen = s
						s\Img = LoadImage_Strict("GFX\screens\" + s\ImgPath)
						s\Img = ResizeImage2(s\Img, ImageWidth(s\Img) * MenuScale, ImageHeight(s\Img) * MenuScale)
						MaskImage(s\Img, 255, 0, 255)
						PlaySound_Strict(ButtonSFX)
						mo\MouseUp1 = False
					EndIf
				EndIf
			EndIf
			Exit
		EndIf
	Next
End Function

Function UpdateLever%(OBJ%, Locked% = False)
	Local Dist# = EntityDistanceSquared(Camera, OBJ)
	Local PrevPitch# = EntityPitch(OBJ)
	
	If Dist < 0.64 And (Not Locked) And EntityInView(OBJ, Camera) Then 
		EntityPick(Camera, 0.6)
		
		If PickedEntity() = OBJ Then
			ga\DrawHandIcon = True
			If mo\MouseHit1 Then GrabbedEntity = OBJ
		EndIf
		
		If GrabbedEntity <> 0 Then
			If mo\MouseDown1 Lor mo\MouseHit1 Then
				If GrabbedEntity = OBJ Then
					ga\DrawHandIcon = True 
					RotateEntity(GrabbedEntity, Max(Min(EntityPitch(OBJ) + Max(Min(mo\Mouse_Y_Speed_1 * 8.0, 30.0), -30.0), 80.0), -80.0), EntityYaw(OBJ), 0.0)
					
					ga\DrawArrowIcon[0] = True
					ga\DrawArrowIcon[2] = True
				EndIf
			EndIf
		EndIf 
		
		; ~ Reset lever state if player doesn't click on the mouse
		If (Not mo\MouseDown1) And (Not mo\MouseHit1) Then 
			If EntityPitch(OBJ, True) > 0.0 Then
				RotateEntity(OBJ, CurveValue(80.0, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			Else
				RotateEntity(OBJ, CurveValue(-80.0, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			EndIf
			GrabbedEntity = 0
		EndIf
	Else
		; ~ Reset lever state if player is far away or doesn't look at the lever
		If EntityPitch(OBJ, True) > 0.0 Then
			RotateEntity(OBJ, CurveValue(80.0, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
		Else
			RotateEntity(OBJ, CurveValue(-80.0, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
		EndIf
		If GrabbedEntity <> 0 And GrabbedEntity = OBJ Then GrabbedEntity = 0
	EndIf
	
	If EntityPitch(OBJ, True) > 75.0 Then
		If PrevPitch =< 75.0 Then PlaySound2(LeverSFX, Camera, OBJ, 1.0)
	ElseIf EntityPitch(OBJ, True) < -75.0
		If PrevPitch >= -75.0 Then PlaySound2(LeverSFX, Camera, OBJ, 1.0)	
	EndIf	
	
	If EntityPitch(OBJ, True) > 0.0 Then
		Return(True)
	Else
		Return(False)
	EndIf	
End Function

Function FillRoom(r.Rooms)
	CatchErrors("Uncaught (FillRoom)")
	
	Local d.Doors, d2.Doors, sc.SecurityCams, de.Decals, r2.Rooms, sc2.SecurityCams, tw.TempWayPoints, fr.Forest
	Local it.Items, it2.Items, em.Emitters, w.WayPoints, w2.WayPoints, lt.LightTemplates, ts.TempScreens
	Local xTemp#, yTemp#, zTemp#, xTemp2%, yTemp2%, zTemp2%
	Local t1%, Tex%
	Local i%, k%, Temp%, Temp3%, Angle#
	Local TempStr$, TempStr2$, TempStr3$
	
	Select r\RoomTemplate\Name
		Case "room860"
			;[Block]
			; ~ Doors to observation booth
			d.Doors = CreateDoor(r\x + 928.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 0.0, r, False, Default_Door, 0, "GEAR")
			d\AutoClose = False : d\Locked = 1
			
			d.Doors = CreateDoor(r\x + 928.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 0.0, r, True, Default_Door, 0, "GEAR")
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False
			
			; ~ Doors to the room itself
			d.Doors = CreateDoor(r\x + 416.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			d.Doors = CreateDoor(r\x + 416.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			; ~ The wooden doors
			r\Objects[2] = CopyEntity(o\DoorModelID[8])
			ScaleEntity(r\Objects[2], 46.0 * RoomScale, 45.0 * RoomScale, 80.0 * RoomScale)
			PositionEntity(r\Objects[2], r\x + 184.0 * RoomScale, r\y, r\z)
			
			r\Objects[3] = CopyEntity(o\DoorModelID[9])
			ScaleEntity(r\Objects[3], 49.0 * RoomScale, 45.0 * RoomScale, 46.0 * RoomScale)
			PositionEntity(r\Objects[3], r\x + 112.0 * RoomScale, r\y, r\z + 0.05)
			EntityType(r\Objects[3], HIT_MAP)
			EntityPickMode(r\Objects[3], 2)
			
			r\Objects[4] = CopyEntity(r\Objects[3])
			ScaleEntity(r\Objects[4], 49.0 * RoomScale, 45.0 * RoomScale, 46.0 * RoomScale)
			PositionEntity(r\Objects[4], r\x + 256.0 * RoomScale, r\y, r\z - 0.05)
			RotateEntity(r\Objects[4], 0.0, 180.0, 0.0)
			
			For i = 2 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			; ~ The forest
			If (Not I_Zone\HasCustomForest) Then
				fr.Forest = New Forest
				r\fr = fr
				GenForestGrid(fr)
				PlaceForest(fr, r\x, r\y + 30.0, r\z, r)
			EndIf
			
			it.Items = CreateItem("Document SCP-860-1", "paper", r\x + 672.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 335.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle + 10.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-860", "paper", r\x + 1152.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 384.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle + 170.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2clockroom"
			;[Block]
			; ~ Doors
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 736.0 * RoomScale, r\y, r\z - 104.0 * RoomScale, 0.0, r)
			r\RoomDoors[0]\Timer = 70.0 * 5.0 : r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x - 288.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[0], True), r\z - 634.0 * RoomScale, True)
			FreeEntity(r\RoomDoors[0]\Buttons[1]) : r\RoomDoors[0]\Buttons[1] = 0
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 104.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 270.0, r)
			r\RoomDoors[1]\Timer = 70.0 * 5.0 : r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], r\x + 634.0 * RoomScale, r\y + 0.7, r\z + 288.0 * RoomScale, True)
			RotateEntity(r\RoomDoors[1]\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(r\RoomDoors[1]\Buttons[1]) : r\RoomDoors[1]\Buttons[1] = 0
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			; ~ Security camera inside
			sc.SecurityCams = CreateSecurityCam(r\x - 688.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 688.0 * RoomScale, r, True, r\x + 668.0 * RoomScale, r\y + 1.1, r\z - 96.0 * RoomScale)
			sc\Angle = 225.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 40.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 112.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 112.0 * RoomScale, r, True, r\x + 96.0 * RoomScale, r\y + 1.1, r\z - 668.0 * RoomScale)
			sc\Angle = 45.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 40.0, 0.0, 0.0)
			
			; ~ Smoke
			em.Emitters = CreateEmitter(r\x - 175.0 * RoomScale, r\y + 370.0 * RoomScale, r\z + 656.0 * RoomScale, 0)
			em\RandAngle = 20.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			
			em.Emitters = CreateEmitter(r\x - 655.0 * RoomScale, r\y + 370.0 * RoomScale, r\z + 240.0 * RoomScale, 0)
			em\RandAngle = 20.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			;[End Block]
		Case "room2clockroom3"
			;[Block]
			; ~ Security cameras inside
			sc.SecurityCams = CreateSecurityCam(r\x + 512.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 384.0 * RoomScale, r, True, r\x + 668.0 * RoomScale, r\y + 1.1, r\z - 96.0 * RoomScale)
			sc\Angle = 135.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 40.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 384.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 512.0 * RoomScale, r, True, r\x + 96.0 * RoomScale, r\y + 1.1, r\z - 668.0 * RoomScale)
			sc\Angle = 315.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 40.0, 0.0, 0.0)
			
			; ~ Create blood decals inside
			For i = 0 To 5
				de.Decals = CreateDecal(Rand(2, 3), r\x + Rnd(-392.0, 520.0) * RoomScale, r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-392.0, 520.0) * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.3, 0.6))
				EntityParent(de\OBJ, r\OBJ)
				de.Decals = CreateDecal(Rand(16, 17), r\x + Rnd(-392.0, 520.0) * RoomScale, r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-392.0, 520.0) * RoomScale, 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.6))
				EntityParent(de\OBJ, r\OBJ)
				de.Decals = CreateDecal(Rand(16, 17), r\x + Rnd(-0.5, 0.5), r\y + 3.0 * RoomScale + Rnd(0, 0.001), r\z + Rnd(-0.5, 0.5), 90.0, Rnd(360.0), 0.0, Rnd(0.1, 0.6))
				EntityParent(de\OBJ, r\OBJ)
			Next
			;[End Block]
		Case "gatea"
			;[Block]
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 4064.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z + 3952.0 * RoomScale, 0.0, r)
			r\RoomDoors[2]\AutoClose = False
			
			d.Doors = CreateDoor(r\x, r\y, r\z + 2336.0 * RoomScale, 0.0, r, True, Big_Door)
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x, r\y, r\z - 1024.0 * RoomScale, 0.0, r)
			d\AutoClose = False : d\Locked = 1
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			d.Doors = CreateDoor(r\x - 1440.0 * RoomScale, r\y - 480.0 * RoomScale, r\z + 2328.0 * RoomScale, 0.0, r, me\SelectedEnding = Ending_A2, Default_Door, 2)
			d\AutoClose = False
			If me\SelectedEnding = Ending_A2 Then 
				d\Locked = 1	
			Else
				d\Locked = 0	
			EndIf	
			PositionEntity(d\Buttons[0], r\x - 1320.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 2294.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], r\x - 1590.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 2484.0 * RoomScale, True)	
			RotateEntity(d\Buttons[1], 0.0, 90.0, 0.0, True)
			
			d.Doors = CreateDoor(r\x - 1440.0 * RoomScale, r\y - 480.0 * RoomScale, r\z + 4352.0 * RoomScale, 0.0, r, me\SelectedEnding = Ending_A2, Default_Door, 2)
			d\AutoClose = False
			If me\SelectedEnding = Ending_A2 Then 
				d\Locked = 1	
			Else
				d\Locked = 0
			EndIf
			PositionEntity(d\Buttons[0], r\x - 1320.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 4378.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, 180.0, 0.0, True)
			PositionEntity(d\Buttons[1], r\x - 1590.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 4232.0 * RoomScale, True)	
			RotateEntity(d\Buttons[1], 0.0, 90.0, 0.0, True)
			
			For r2.Rooms = Each Rooms
				If r2\RoomTemplate\Name = "gateaentrance" Then
					; ~ Elevator
					r\RoomDoors.Doors[1] = CreateDoor(r\x + 1544.0 * RoomScale, r\y, r\z - 64.0 * RoomScale, -90.0, r, False, Elevator_Door)
					r\RoomDoors[1]\AutoClose = False
					PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) - 0.22, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
					PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)
					
					r2\Objects[1] = CreatePivot()
					PositionEntity(r2\Objects[1], r\x + 1848.0 * RoomScale, r\y, r\z - 64.0 * RoomScale)
					EntityParent(r2\Objects[1], r\OBJ)
					Exit
				EndIf
			Next
			
			; ~ SCP-106's spawnpoint
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 1216.0 * RoomScale, r\y, r\z + 2112.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x, r\y + 96.0 * RoomScale, r\z + 6400.0 * RoomScale)		
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 1784.0 * RoomScale, r\y + 2124.0 * RoomScale, r\z + 4512.0 * RoomScale)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 5048.0 * RoomScale, r\y + 1912.0 * RoomScale, r\z + 4656.0 * RoomScale)	
			
			; ~ MTF spawnpoint
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 1824.0 * RoomScale, r\y + 0.22, r\z + 7056.0 * RoomScale)	
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x - 1824.0 * RoomScale, r\y + 0.22, r\z + 7056.0 * RoomScale)	
			
			For i = 3 To 8
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 4064.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z - 1696.0 * RoomScale)
			EntityParent(r\Objects[11], r\OBJ)
			
			r\Objects[13] = LoadMesh_Strict("GFX\map\gateawall1.b3d", r\OBJ)
			PositionEntity(r\Objects[13], r\x - 4308.0 * RoomScale, r\y - 1045.0 * RoomScale, r\z + 544.0 * RoomScale, True)
			EntityColor(r\Objects[13], 25.0, 25.0, 25.0)
			EntityType(r\Objects[13], HIT_MAP)
			
			r\Objects[14] = LoadMesh_Strict("GFX\map\gateawall2.b3d", r\OBJ)
			PositionEntity(r\Objects[14], r\x - 3820.0 * RoomScale, r\y - 1045.0 * RoomScale, r\z + 544.0 * RoomScale, True)	
			EntityColor(r\Objects[14], 25.0, 25.0, 25.5)
			EntityType(r\Objects[14], HIT_MAP)
			
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x - 3568.0 * RoomScale, r\y - 1089.0 * RoomScale, r\z + 4944.0 * RoomScale)
			EntityParent(r\Objects[15], r\OBJ)
			
			; ~ Hit Box
			r\Objects[16] = LoadMesh_Strict("GFX\map\gatea_hitbox1.b3d", r\OBJ)
			EntityPickMode(r\Objects[16], 2)
			EntityType(r\Objects[16], HIT_MAP)
			EntityAlpha(r\Objects[16], 0.0)
			
			w.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z + 3112.0 * RoomScale, Null, r)
			w2.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z + 2112.0 * RoomScale, Null, r)
			w\connected[0] = w2 : w\Dist[0] = EntityDistance(w\OBJ, w2\OBJ)
			w2\connected[0] = w : w2\Dist[0] = w\Dist[0]
			;[End Block]
		Case "gateaentrance"
			;[Block]
			; ~ Elevator
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 736.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.061, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.061, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x, r\y, r\z - 360.0 * RoomScale, 0.0, r, False, Big_Door, 5)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[1], r\x + 422.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[1], True), r\z - 576.0 * RoomScale, True)
			RotateEntity(r\RoomDoors[1]\Buttons[1], 0.0, r\Angle - 90.0, 0.0, True)
			PositionEntity(r\RoomDoors[1]\Buttons[0], r\x - 522.0 * RoomScale, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			RotateEntity(r\RoomDoors[1]\Buttons[0], 0.0, r\Angle - 225.0, 0.0, True)
			
			; ~ Elevator's pivot
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1048.0 * RoomScale, r\y, r\z + 512.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "gatebentrance"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 720.0 * RoomScale, r\y, r\z + 1432.0 * RoomScale, 0.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True), EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True) + 0.031, True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True), EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True) - 0.031, True)	
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x, r\y, r\z - 320.0 * RoomScale, 0.0, r, False, Big_Door, 5)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[1], r\x + 358.0 * RoomScale, EntityY(r\RoomDoors[1]\Buttons[1], True), r\z - 528.0 * RoomScale, True)
			RotateEntity(r\RoomDoors[1]\Buttons[1], 0.0, r\Angle - 90.0, 0.0, True)
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True), EntityY(r\RoomDoors[1]\Buttons[0], True), r\z - 198.0 * RoomScale, True)
			RotateEntity(r\RoomDoors[1]\Buttons[0], 0.0, r\Angle - 180.0, 0.0, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 720.0 * RoomScale, r\y, r\z + 1744.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "gateb"
			;[Block]
			For r2.Rooms = Each Rooms
				If r2\RoomTemplate\Name = "gatebentrance" Then
					; ~ Elevator
					r\RoomDoors.Doors[1] = CreateDoor(r\x - 5424.0 * RoomScale, r\y, r\z - 1380.0 * RoomScale, 0.0, r, False, Elevator_Door)
					r\RoomDoors[1]\AutoClose = False
					PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True), EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True) + 0.031, True)
					PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True), EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True) - 0.031, True)
					
					r2\Objects[1] = CreatePivot()
					PositionEntity(r2\Objects[1], r\x - 5424.0 * RoomScale, r\y, r\z - 1068.0 * RoomScale)		
					EntityParent(r2\Objects[1], r\OBJ)
					Exit
				EndIf
			Next
			
			; ~ Other doors
			r\RoomDoors.Doors[2] = CreateDoor(r\x + 4352.0 * RoomScale, r\y, r\z - 492.0 * RoomScale, 0.0, r)
			r\RoomDoors[2]\AutoClose = False	
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x + 4352.0 * RoomScale, r\y, r\z + 498.0 * RoomScale, 0.0, r)
			r\RoomDoors[3]\AutoClose = False
			
			r\RoomDoors.Doors[4] = CreateDoor(r\x + 3248.0 * RoomScale, r\y - 928.0 * RoomScale, r\z + 6400.0 * RoomScale, 0.0, r, False, One_Sided_Door, 0, "GEAR")
			r\RoomDoors[4]\AutoClose = False : r\RoomDoors[4]\Locked = 1
			FreeEntity(r\RoomDoors[4]\Buttons[1]) : r\RoomDoors[4]\Buttons[1] = 0	
			
			d.Doors = CreateDoor(r\x + 3072.0 * RoomScale, r\y - 928.0 * RoomScale, r\z + 5800.0 * RoomScale, 90.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			; ~ Guard spawnpoint
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 5203.0 * RoomScale, r\y + 1444.0 * RoomScale, r\z - 1739.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 4363.0 * RoomScale, r\y - 248.0 * RoomScale, r\z + 2766.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 5192.0 * RoomScale, r\y + 1408.0 * RoomScale, r\z - 4352.0 * RoomScale)
			
			; ~ Walkway
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 4352.0 * RoomScale, r\y, r\z + 1344.0 * RoomScale)	
			
			; ~ SCP-682's paw
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 2816.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 2816.0 * RoomScale)
			
			For i = 3 To 6
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			; ~ MTF spawnpoint
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 3600.0 * RoomScale, r\y - 888.0 * RoomScale, r\z + 6623.0 * RoomScale)
			
			; ~ "SCP-682" pivot
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 3808.0 * RoomScale, r\y + 1536.0 * RoomScale, r\z - 13568.0 * RoomScale)
			
			; ~ Apache radius
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x - 7680.0 * RoomScale, r\y + 208.0 * RoomScale, r\z - 27048.0 * RoomScale)
			
			; ~ Extra apache spawnpoint
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 5424.0 * RoomScale, r\y, r\z - 1068.0 * RoomScale)
			
			For i = 8 To 11
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room372"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z - 368.0 * RoomScale, 0.0, r, False, Big_Door, 2)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x - 496.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 278.0 * RoomScale, True) 
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.025, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True) 
			TurnEntity(d\Buttons[0], 0.0, 90.0, 0.0)
			
			; ~ Hit Box
			r\Objects[3] = LoadMesh_Strict("GFX\map\room372_hb.b3d", r\OBJ)
			EntityPickMode(r\Objects[3], 2)
			EntityType(r\Objects[3], HIT_MAP)
			EntityAlpha(r\Objects[3], 0.0)
			
			it.Items = CreateItem("Document SCP-372", "paper", r\x + 800.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 1108.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x + 800.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 944.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room079"
			;[Block]
			; ~ Doors
			d.Doors = CreateDoor(r\x - 1648.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z - 260.0 * RoomScale, 90.0, r, False, Big_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[1], r\x - 1418.0 * RoomScale, r\y - 10490.0 * RoomScale, r\z - 26.0 * RoomScale, True) 
			PositionEntity(d\Buttons[0], r\x - 1894.0 * RoomScale, r\y - 10490.0 * RoomScale, r\z - 503.0 * RoomScale, True)
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 1484.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z + 1205.0 * RoomScale, 90.0, r, False, Big_Door, 4)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[1], r\x - 1700.0 * RoomScale, r\y - 10490.0 * RoomScale, r\z + 777.5 * RoomScale, True)
			RotateEntity(r\RoomDoors[0]\Buttons[1], 0.0, 90.0, 0.0)
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x - 1216.0 * RoomScale, r\y - 10490.0 * RoomScale, r\z + 1502.0 * RoomScale, True) 
			RotateEntity(r\RoomDoors[0]\Buttons[0], 0.0, -90.0, 0.0)
			
			d.Doors = CreateDoor(r\x - 1216.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z + 888.0 * RoomScale, 0.0, r, False, Default_Door, -1)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			d.Doors = CreateDoor(r\x, r\y, r\z + 64.0 * RoomScale, 0.0, r, False, Heavy_Door, 3)
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			; ~ Elevators' doors
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 512.0 * RoomScale, r\y, r\z - 256.0 * RoomScale, -90.0, r, True, Elevator_Door)
			r\RoomDoors[1]\AutoClose = False
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x + 512.0 * RoomScale, r\y - 10240.0 * RoomScale, r\z - 256.0 * RoomScale, -90.0, r, False, Elevator_Door)
			r\RoomDoors[2]\AutoClose = False
			
			r\Objects[0] = LoadAnimMesh_Strict("GFX\map\Props\079.b3d")
			ScaleEntity(r\Objects[0], 1.3, 1.3, 1.3)
			PositionEntity(r\Objects[0], r\x + 166.0 * RoomScale, r\y - 10800.0 * RoomScale, r\z + 1606.0 * RoomScale)
			TurnEntity(r\Objects[0], 0.0, -90.0, 0.0)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = CreateSprite(r\Objects[0])
			SpriteViewMode(r\Objects[1], 2)
			PositionEntity(r\Objects[1], 0.082, 0.119, 0.010)
			ScaleSprite(r\Objects[1], 0.09, 0.0725)
			TurnEntity(r\Objects[1], 0.0, 13.0, 0.0)
			MoveEntity(r\Objects[1], 0.0, 0.0, -0.022)
			EntityTexture(r\Objects[1], t\MiscTextureID[6])
			HideEntity(r\Objects[1])
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 2260.0 * RoomScale, r\y - 10688.0 * RoomScale, r\z + 1000.0 * RoomScale)
			
			; ~ Elevators' pivots
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 816.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 256.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 816.0 * RoomScale, r\y - 10000.0 * RoomScale, r\z - 256.0 * RoomScale)
			
			For i = 2 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			de.Decals = CreateDecal(3, r\x - 2200.0 * RoomScale, r\y - 10688.0 * RoomScale + 0.01, r\z + 1000.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case "room2checkpoint"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 48.0 * RoomScale, r\y, r\z - 128.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\Timer = 70.0 * 5.0
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x - 152.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[0], True), r\z - 346.0 * RoomScale, True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], r\x - 152.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[1], True), r\z + 90.0 * RoomScale, True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x - 352.0 * RoomScale, r\y, r\z - 128.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\Timer = 70.0 * 5.0
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			If CurrMapGrid\Grid[Floor(r\x / RoomSpacing) + ((Floor(r\z / RoomSpacing) - 1) * MapGridSize)] = MapGrid_NoTile Then
				d.Doors = CreateDoor(r\x, r\y, r\z - 1026.0 * RoomScale, 0.0, r, False, Heavy_Door, 0, "GEAR")
				d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
				FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			EndIf
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 720.0 * RoomScale, r\y + 120.0 * RoomScale, r\z + 333.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Monitors at the both sides
			r\Objects[2] = CopyEntity(o\MonitorModelID[1], r\OBJ)
			PositionEntity(r\Objects[2], r\x - 152.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 124.0 * RoomScale, True)
			ScaleEntity(r\Objects[2], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[2], 0.0, 180.0, 0.0)
			EntityFX(r\Objects[2], 1)
			
			r\Objects[3] = CopyEntity(o\MonitorModelID[1], r\OBJ)
			PositionEntity(r\Objects[3], r\x - 152.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 380.0 * RoomScale, True)
			ScaleEntity(r\Objects[3], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[3], 0.0, 0.0, 0.0)
			EntityFX(r\Objects[3], 1)
			
			sc.SecurityCams = CreateSecurityCam(r\x + 192.0 * RoomScale, r\y + 704.0 * RoomScale, r\z - 960.0 * RoomScale, r)
			sc\Angle = 45.0 : sc\Turn = 0.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room2checkpoint2"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 48.0 * RoomScale, r\y, r\z + 128.0 * RoomScale, 0.0, r, False, Default_Door, 5)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\Timer = 70.0 * 5.0
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x + 152.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[0], True), r\z - 90.0 * RoomScale, True)			
			PositionEntity(r\RoomDoors[0]\Buttons[1], r\x + 152.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[1], True), r\z + 346.0 * RoomScale, True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 352.0 * RoomScale, r\y, r\z + 128.0 * RoomScale, 0.0, r, False, Default_Door, 5)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\Timer = 70.0 * 5.0
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			r\RoomDoors[0]\LinkedDoor = r\RoomDoors[1]
			r\RoomDoors[1]\LinkedDoor = r\RoomDoors[0]
			
			If CurrMapGrid\Grid[Floor(r\x / RoomSpacing) + ((Floor(r\z / RoomSpacing) - 1) * MapGridSize)] = MapGrid_NoTile Then
				d.Doors = CreateDoor(r\x, r\y, r\z - 1026.0 * RoomScale, 0.0, r, False, Default_Door, 0, "GEAR")
				d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
				FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			EndIf
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 720.0 * RoomScale, r\y + 120.0 * RoomScale, r\z + 464.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			; ~ Monitors at the both sides
			r\Objects[2] = CopyEntity(o\MonitorModelID[1], r\OBJ)
			PositionEntity(r\Objects[2], r\x + 152.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 380.0 * RoomScale, True)
			ScaleEntity(r\Objects[2], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[2], 0.0, 180.0, 0.0)
			EntityFX(r\Objects[2], 1)
			
			r\Objects[3] = CopyEntity(o\MonitorModelID[1], r\OBJ)
			PositionEntity(r\Objects[3], r\x + 152.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 124.0 * RoomScale, True)
			ScaleEntity(r\Objects[3], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[3], 0.0, 0.0, 0.0)
			EntityFX(r\Objects[3], 1)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 192.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 960.0 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 0.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room2pit"
			;[Block]
			; ~ Smoke
			i = 0
			For xTemp = -1 To 1 Step 2
				For zTemp = -1 To 1
					em.Emitters = CreateEmitter(r\x + 202.0 * RoomScale * xTemp, r\y + 8.0 * RoomScale, r\z + 256.0 * RoomScale * zTemp, 0)
					em\RandAngle = 30.0 : em\Speed = 0.0045 : em\SizeChange = 0.007 : em\AlphaChange = -0.016
					If i < 3 Then 
						TurnEntity(em\OBJ, -45.0, -90.0, 0.0) 
					Else
						TurnEntity(em\OBJ, -45.0, 90.0, 0.0)
					EndIf
					EntityParent(em\OBJ, r\OBJ)
					i = i + 1
				Next
			Next
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 640.0 * RoomScale, r\y + 8.0 * RoomScale, r\z - 896.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 864.0 * RoomScale, r\y - 400.0 * RoomScale, r\z - 632.0 * RoomScale)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room2testroom"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 240.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, r, False, Default_Door, 1)
			r\RoomDoors[0]\AutoClose = False
			
			d.Doors = CreateDoor(r\x - 512.0 * RoomScale, r\y, r\z + 384.0 * RoomScale, 0.0, r)
			d\AutoClose = False	
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.031, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)					
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 640.0 * RoomScale, r\y + 0.5, r\z - 912.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 669.0 * RoomScale, r\y + 0.5, r\z - 16.0 * RoomScale)
			
			; ~ Glass panel
			Tex = LoadTexture_Strict("GFX\map\textures\glass.png", 1 + 2)
			r\Objects[2] = CreateSprite()
			EntityTexture(r\Objects[2], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			SpriteViewMode(r\Objects[2], 2)
			ScaleSprite(r\Objects[2], 182.0 * RoomScale * 0.5, 192.0 * RoomScale * 0.5)
			PositionEntity(r\Objects[2], r\x - 632.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 208.0 * RoomScale)
			TurnEntity(r\Objects[2], 0.0, 180.0, 0.0)			
			HideEntity(r\Objects[2])
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Level 2 Key Card", "key2", r\x - 342.0 * RoomScale, r\y + 264.0 * RoomScale, r\z + 102.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 180.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x - 914.0 * RoomScale, r\y + 137.0 * RoomScale, r\z + 61.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room3tunnel"
			;[Block]
			; ~ Guard's position
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 190.0 * RoomScale, r\y + 4.0 * RoomScale, r\z + 190.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "room2toilets"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1040.0 * RoomScale, r\y + 192.0 * RoomScale, r\z)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1530.0 * RoomScale, r\y + 0.5, r\z + 512.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 1535.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + 512.0 * RoomScale)
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room2storage"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 1288.0 * RoomScale, r\y, r\z, 270.0, r)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x - 760.0 * RoomScale, r\y, r\z, 270.0, r)
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 264.0 * RoomScale, r\y, r\z, 270.0, r)
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x + 264.0 * RoomScale, r\y, r\z, 270.0, r)
			
			r\RoomDoors.Doors[4] = CreateDoor(r\x + 760.0 * RoomScale, r\y, r\z, 270.0, r)
			
			r\RoomDoors.Doors[5] = CreateDoor(r\x + 1288.0 * RoomScale, r\y, r\z, 270.0, r)
			
			For i = 0 To 5
				r\RoomDoors[i]\AutoClose = False
				MoveEntity(r\RoomDoors[i]\Buttons[0], 0.0, 0.0, -8.0)
				MoveEntity(r\RoomDoors[i]\Buttons[1], 0.0, 0.0, -8.0)
			Next
			
			it.Items = CreateItem("Document SCP-939", "paper", r\x + 352.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 256.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle + 4.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 352.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 448.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Empty Cup", "emptycup", r\x - 672.0 * RoomScale, 240.0 * RoomScale, r\z + 288.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 0 Key Card", "key0", r\x - 672.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 224.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2sroom"
			;[Block]
			d.Doors = CreateDoor(r\x + 1440.0 * RoomScale, 224.0 * RoomScale, r\z + 32.0 * RoomScale, 270.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			it.Items = CreateItem("Some SCP-420-J", "scp420j", r\x + 1776.0 * RoomScale, r\y + 400.0 * RoomScale, r\z + 427.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Some SCP-420-J", "scp420j", r\x + 1858.0 * RoomScale, r\y + 400.0 * RoomScale, r\z + 435.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 5 Key Card", "key5", r\x + 2232.0 * RoomScale, r\y + 392.0 * RoomScale, r\z + 387.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Nuclear Device Document", "paper", r\x + 2248.0 * RoomScale, r\y + 440.0 * RoomScale, r\z + 372.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x + 2240.0 * RoomScale, r\y + 320.0 * RoomScale, r\z + 128.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2shaft"
			;[Block]
			d.Doors = CreateDoor(r\x + 1552.0 * RoomScale, r\y, r\z + 552.0 * RoomScale, 0.0, r)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), r\z + 518.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), r\z + 575.0 * RoomScale, True)
			
			d.Doors = CreateDoor(r\x + 256.0 * RoomScale, r\y, r\z + 744.0 * RoomScale, 90.0, r, False, Default_Door, 2)
			d\AutoClose = False
			
			; ~ Player's position after leaving the pocket dimension
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1560.0 * RoomScale, r\y, r\z + 250.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1344.0 * RoomScale, r\y - 752.0 * RoomScale, r\z - 384.0 * RoomScale)
			
			r\Objects[2] = CreateButton(0, r\x + 1180.0 * RoomScale, r\y + 180.0 * RoomScale, r\z - 552.0 * RoomScale, 0.0, 270.0, 0.0, 0, True)
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			de.Decals = CreateDecal(3, r\x + 1334.0 * RoomScale, r\y - 796.0 * RoomScale + 0.01, r\z - 220.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.25)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Level 3 Key Card", "key3", r\x + 1119.0 * RoomScale, r\y + 233.0 * RoomScale, r\z + 494.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 1035.0 * RoomScale, r\y + 145.0 * RoomScale, r\z + 56.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 1930.0 * RoomScale, r\y + 97.0 * RoomScale, r\z + 256.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 1061.0 * RoomScale, r\y + 161.0 * RoomScale, r\z + 494.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x + 1930.0 * RoomScale, r\y + 225.0 * RoomScale, r\z + 128.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2poffices"
			;[Block]
			d.Doors = CreateDoor(r\x + 240.0 * RoomScale, r\y, r\z + 448.0 * RoomScale, 270.0, r, False, Default_Door, 0, Str(AccessCode))
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)	
			
			d.Doors = CreateDoor(r\x - 496.0 * RoomScale, r\y, r\z, 270.0, r, False, Default_Door, 0, "GEAR")
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False : d\DisableWaypoint = True
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)						
			
			d.Doors = CreateDoor(r\x + 240.0 * RoomScale, r\y, r\z - 576.0 * RoomScale, 270.0, r, False, Default_Door, 0, "7816")
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)			
			
			it.Items = CreateItem("Mysterious Note", "paper", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z + 544.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)	
			
			it.Items = CreateItem("Ballistic Vest", "vest", r\x + 608.0 * RoomScale, r\y + 112.0 * RoomScale, r\z + 32.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Incident Report SCP-106-0204", "paper", r\x + 704.0 * RoomScale, r\y + 183.0 * RoomScale, r\z - 576.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Journal Page", "paper", r\x + 912.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 160.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 912.0 * RoomScale, r\y + 112.0 * RoomScale, r\z - 336.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			If I_005\ChanceToSpawn = 2 Then 
				it.Items = CreateItem("SCP-005", "scp005",  r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z + 755.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case "room2poffices2"
			;[Block]
			d.Doors = CreateDoor(r\x + 240.0 * RoomScale, r\y, r\z + 48.0 * RoomScale, 270.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)			
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 432.0 * RoomScale, r\y, r\z, 90.0, r, False, Default_Door, 0, "1234")
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\MTFClose = False : r\RoomDoors[0]\DisableWaypoint = True
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.061, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			FreeEntity(r\RoomDoors[0]\Buttons[1]) : r\RoomDoors[0]\Buttons[1] = 0
			
			de.Decals = CreateDecal(0, r\x - 808.0 * RoomScale, r\y + 0.005, r\z - 72.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(2, r\x - 808.0 * RoomScale, r\y + 0.01, r\z - 72.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.3)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(0, r\x - 432.0 * RoomScale, r\y + 0.01, r\z, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			r\Objects[0] = CreatePivot(r\OBJ)
			PositionEntity(r\Objects[0], r\x - 808.0 * RoomScale, r\y + 1.0, r\z - 72.0 * RoomScale, True)
			
			it.Items = CreateItem("Dr. L's Burnt Note #1", "paper", r\x - 688.0 * RoomScale, r\y + 1.0, r\z - 16.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Dr. L's Burnt Note #2", "paper", r\x - 808.0 * RoomScale, r\y + 1.0, r\z - 72.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("The Modular Site Project", "paper", r\x + 622.0 * RoomScale, r\y + 125.0 * RoomScale, r\z - 73.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2elevator"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 448.0 * RoomScale, r\y, r\z, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\MTFClose = False
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 888.0 * RoomScale, r\y + 240.0 * RoomScale, r\z, True)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1024.0 * RoomScale, r\y + 120.0 * RoomScale, r\z, True)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room2cafeteria"
			;[Block]
			; ~ SCP-294
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1847.0 * RoomScale, r\y - 240.0 * RoomScale, r\z - 321.0 * RoomScale)
			
			; ~ Spawnpoint for the cups
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 1780.0 * RoomScale, r\y - 248.0 * RoomScale, r\z - 276.0 * RoomScale)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Cup", "cup", r\x - 508.0 * RoomScale, r\y - 187.0 * RoomScale, r\z + 284.0 * RoomScale, 240, 175, 70)
			EntityParent(it\Collider, r\OBJ) : it\Name = "Cup of Orange Juice"
			
			it.Items = CreateItem("Cup", "cup", r\x + 1412.0 * RoomScale, r\y - 187.0 * RoomScale, r\z - 716.0 * RoomScale, 87, 62, 45)
			EntityParent(it\Collider, r\OBJ) : it\Name = "Cup of Coffee"
			
			it.Items = CreateItem("Empty Cup", "emptycup", r\x - 540.0 * RoomScale, r\y - 187.0 * RoomScale, r\z + 124.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Quarter", "25ct", r\x - 447.0 * RoomScale, r\y - 334.0 * RoomScale, r\z + 36.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Quarter", "25ct", r\x + 1409.0 * RoomScale, r\y - 334.0 * RoomScale, r\z - 732.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2nuke"
			;[Block]
			d.Doors = CreateDoor(r\x + 576.0 * RoomScale, r\y, r\z + 152.0 * RoomScale, 90.0, r, False, One_Sided_Door, 5)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.09, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.09, True)
			
			d.Doors = CreateDoor(r\x - 549.0 * RoomScale, r\y + 3808.0 * RoomScale, r\z + 738.0 * RoomScale, 90.0, r, False, Default_Door, 5)
			d\AutoClose = False		
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), r\z + 608.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), r\z + 608.0 * RoomScale, True)
			
			; ~ Elevators
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 1192.0 * RoomScale, r\y, r\z, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) + 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 675.0 * RoomScale, r\y + 3808.0 * RoomScale, r\z, -90.0, r, False, Elevator_Door)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) + 0.031, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) - 0.031, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)
			
			; ~ Elevators' pivots
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 1496.0 * RoomScale, r\y + 240.0 * RoomScale, r\z)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 979.0 * RoomScale, r\y + 4048.0 * RoomScale, r\z)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 1110.0 * RoomScale, r\y + 36.0 * RoomScale, r\z - 208.0 * RoomScale)
			
			For i = 4 To 6
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For k = 0 To 1
				r\Objects[k * 2] = CopyEntity(o\LeverModelID[0])
				r\Objects[k * 2 + 1] = CopyEntity(o\LeverModelID[1])
				r\Levers[k] = r\Objects[k * 2 + 1]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k * 2 + i], 0.04, 0.04, 0.04)
					PositionEntity(r\Objects[k * 2 + i], r\x - 980.0 * RoomScale, r\y + 4016.0 * RoomScale, r\z - (502.0 - (132.0 * k)) * RoomScale)
					EntityParent(r\Objects[k * 2 + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k * 2], 0.0, -270.0, 0.0)
				RotateEntity(r\Objects[k * 2 + 1], 10.0, -90.0, 0.0)
				EntityPickMode(r\Objects[k * 2 + 1], 1, False)
				EntityRadius(r\Objects[k * 2 + 1], 0.1)
			Next
			
			it.Items = CreateItem("Nuclear Device Document", "paper", r\x - 940.0 * RoomScale, r\y + 3988.0 * RoomScale, r\z - 706.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Vest", "vest", r\x - 764.0 * RoomScale, r\y + 3958.0 * RoomScale, r\z - 768.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, -90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r\x + 619.0 * RoomScale, r\y + 4191.0 * RoomScale, r\z - 312.0 * RoomScale, r)
			sc\Angle = 90.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room2mt"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 264.0 * RoomScale, r\y, r\z + 656.0 * RoomScale, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)			
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 264.0 * RoomScale, r\y, r\z - 656.0 * RoomScale, 90.0, r, True, Elevator_Door)
			r\RoomDoors[2]\AutoClose = False
			PositionEntity(r\RoomDoors[2]\Buttons[0], EntityX(r\RoomDoors[2]\Buttons[0], True) + 0.031, EntityY(r\RoomDoors[2]\Buttons[0], True), EntityZ(r\RoomDoors[2]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[2]\Buttons[1], EntityX(r\RoomDoors[2]\Buttons[1], True) - 0.031, EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[1], True), True)
			
			Temp = ((Int(AccessCode) * 3) Mod 10000)
			If Temp < 1000 Then Temp = Temp + 1000
			d.Doors = CreateDoor(r\x, r\y, r\z, 0.0, r, False, Big_Door, 0, Temp)
			PositionEntity(d\Buttons[0], r\x + 230.0 * RoomScale, EntityY(d\Buttons[1], True), r\z - 384.0 * RoomScale, True)
			RotateEntity(d\Buttons[0], 0.0, -90.0, 0.0, True)
			PositionEntity(d\Buttons[1], r\x - 230.0 * RoomScale, EntityY(d\Buttons[1], True), r\z + 384.0 * RoomScale, True)		
			RotateEntity(d\Buttons[1], 0.0, 90.0, 0.0, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 2640.0 * RoomScale, r\y - 2496.0 * RoomScale, r\z + 400.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 4336.0 * RoomScale, r\y - 2496.0 * RoomScale, r\z - 2512.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			RotateEntity(r\Objects[2], 0.0, 180.0, 0.0, True)
			PositionEntity(r\Objects[2], r\x + 552.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 656.0 * RoomScale)
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 552.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 656.0 * RoomScale)
			EntityParent(r\Objects[4], r\OBJ)
			
			de.Decals = CreateDecal(0, r\x + 64.0 * RoomScale, r\y + 0.005, r\z + 144.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Scorched Note", "paper", r\x + 64.0 * RoomScale, r\y + 144.0 * RoomScale, r\z - 384.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room008"
			;[Block]
			d.Doors = CreateDoor(r\x + 296.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 672.0 * RoomScale, 180.0, r, True, One_Sided_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[1], r\x + 164.0 * RoomScale, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			r\RoomDoors[0] = d
			
			d2.Doors = CreateDoor(r\x + 296.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 144.0 * RoomScale, 0.0, r, False, One_Sided_Door)
			d2\AutoClose = False
			PositionEntity(d2\Buttons[0], r\x + 438.0 * RoomScale, EntityY(d2\Buttons[0], True), r\z - 480.0 * RoomScale, True)
			RotateEntity(d2\Buttons[0], 0.0, -90.0, 0.0, True)			
			PositionEntity(d2\Buttons[1], r\x + 164.0 * RoomScale, EntityY(d2\Buttons[0], True), r\z - 134.0 * RoomScale, True)
			r\RoomDoors[1] = d2
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			
			d.Doors = CreateDoor(r\x - 384.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 672.0 * RoomScale, 0.0, r, False, Default_Door, 4)
			d\AutoClose = False : d\Locked = 1 : r\RoomDoors[2] = d
			
			d.Doors = CreateDoor(r\x + 456.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 864.0 * RoomScale, 90.0, r)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.162, True)
			
			; ~ Elevators' doors
			r\RoomDoors.Doors[3] = CreateDoor(r\x + 448.0 * RoomScale, r\y, r\z, -90.0, r, True, Elevator_Door)
			r\RoomDoors[3]\AutoClose = False
			
			r\RoomDoors.Doors[4] = CreateDoor(r\x + 800.0 * RoomScale, r\y - 5104.0 * RoomScale, r\z - 287.0 * RoomScale, -90.0, r, False, Elevator_Door)
			r\RoomDoors[4]\AutoClose = False
			
			; ~ The container
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 292.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 516.0 * RoomScale)
			
			; ~ The lid of the container
			r\Objects[1] = LoadRMesh("GFX\map\008_2_opt.rmesh", Null)
			ScaleEntity(r\Objects[1], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[1], r\x + 292.0 * RoomScale, r\y - 4954.0 * RoomScale, r\z + 576.0 * RoomScale)
			RotateEntity(r\Objects[1], 85.0, 0.0, 0.0, True)
			
			r\Levers[0] = r\Objects[1]
			
			Tex = LoadTexture_Strict("GFX\map\textures\glass.png", 1 + 2)
			r\Objects[2] = CreateSprite()
			EntityTexture(r\Objects[2], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			SpriteViewMode(r\Objects[2], 2)
			ScaleSprite(r\Objects[2], 256.0 * RoomScale * 0.5, 194.0 * RoomScale * 0.5)
			PositionEntity(r\Objects[2], r\x - 176.0 * RoomScale, r\y - 4881.0 * RoomScale, r\z + 448.0 * RoomScale)
			TurnEntity(r\Objects[2], 0, 90, 0)			
			
			; ~ SCP-173's spawnpoint
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 445.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 544.0 * RoomScale)
			
			; ~ SCP-173's attack point
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 67.0 * RoomScale, r\y - 4985.0 * RoomScale, r\z + 464.0 * RoomScale)
			
			r\Objects[5] = CreateSprite()
			PositionEntity(r\Objects[5], r\x - 158.0 * RoomScale, r\y - 4737.0 * RoomScale, r\z + 298.0 * RoomScale)
			ScaleSprite(r\Objects[5], 0.02, 0.02)
			EntityTexture(r\Objects[5], t\LightSpriteID[1])
			EntityBlend(r\Objects[5], 3)
			HideEntity(r\Objects[5])
			
			; ~ Spawnpoint for the scientist used in the "SCP-008-1's scene"
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 160.0 * RoomScale, r\y + 670.0 * RoomScale, r\z - 384.0 * RoomScale)
			
			; ~ Spawnpoint for the player
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x, r\y + 672.0 * RoomScale, r\z + 350.0 * RoomScale)
			
			; ~ Elevators' pivots
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 752.0 * RoomScale, r\y + 240.0 * RoomScale, r\z)
			
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 1104.0 * RoomScale, r\y - 4864.0 * RoomScale, r\z - 287.0  * RoomScale)
			
			For i = 0 To 9
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Hazmat Suit", "hazmatsuit", r\x - 76.0 * RoomScale, r\y - 4895.0 * RoomScale, r\z - 396.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-008", "paper", r\x - 545.0 * RoomScale, r\y - 4895.0 * RoomScale, r\z + 368.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r\x + 666.0 * RoomScale, r\y - 4654.0 * RoomScale, r\z + 755.0 * RoomScale, r)
			sc\Angle = 135.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room035"
			;[Block]
			d.Doors = CreateDoor(r\x - 296.0 * RoomScale, r\y, r\z - 672.0 * RoomScale, 180.0, r, True, One_Sided_Door, 5)
			d\AutoClose = False : d\Locked = 1
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], r\x - 164.0 * RoomScale, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			r\RoomDoors[0] = d
			
			d2.Doors = CreateDoor(r\x - 296.0 * RoomScale, r\y, r\z - 144.0 * RoomScale, 0.0, r, False, One_Sided_Door)
			d2\AutoClose = False : d2\Locked = 1
			PositionEntity(d2\Buttons[0], r\x - 438.0 * RoomScale, EntityY(d2\Buttons[0], True), r\z - 480.0 * RoomScale, True)
			RotateEntity(d2\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(d2\Buttons[1]) : d2\Buttons[1] = 0
			r\RoomDoors[1] = d2
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			
			; ~ Door to the control room
			r\RoomDoors.Doors[2] = CreateDoor(r\x + 384.0 * RoomScale, r\y, r\z - 672.0 * RoomScale, 180.0, r, False, Default_Door, 5)
			r\RoomDoors[2]\AutoClose = False
			
			; ~ Door to the storage room
			r\RoomDoors.Doors[3] = CreateDoor(r\x + 768.0 * RoomScale, r\y, r\z + 512.0 * RoomScale, 90.0, r, False, Default_Door, 0, "5731")
			r\RoomDoors[3]\AutoClose = False			
			
			For i = 0 To 1
				r\Objects[i * 2] = CopyEntity(o\LeverModelID[0])
				r\Objects[i * 2 + 1] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[i] = r\Objects[i * 2 + 1]
				
				For k = 0 To 1
					ScaleEntity(r\Objects[i * 2 + k], 0.04, 0.04, 0.04)
					PositionEntity(r\Objects[i * 2 + k], r\x + 210.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - (208.0 - i * 76.0) * RoomScale)
					EntityParent(r\Objects[i * 2 + k], r\OBJ)
				Next
				
				RotateEntity(r\Objects[i * 2], 0, -90.0 - 180.0, 0.0)
				RotateEntity(r\Objects[i * 2 + 1], -80.0, -90.0, 0.0)
				EntityPickMode(r\Objects[i * 2 + 1], 1, False)
				EntityRadius(r\Objects[i * 2 + 1], 0.1)	
			Next
			
			; ~ The control room
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 456.0 * RoomScale, r\y + 0.5, r\z + 400.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 576.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			
			For i = 3 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			em.Emitters = CreateEmitter(r\x - 269.0 * RoomScale, r\y + 20.0, r\z + 624.0 * RoomScale, 0)
			em\RandAngle = 15.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			r\Objects[5] = em\OBJ
			
			em.Emitters = CreateEmitter(r\x - 269.0 * RoomScale, r\y + 20.0, r\z + 135.0 * RoomScale, 0)
			em\RandAngle = 15.0 : em\Speed = 0.05 : em\SizeChange = 0.007 : em\AlphaChange = -0.006 : em\Gravity = -0.24
			TurnEntity(em\OBJ, 90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			r\Objects[6] = em\OBJ
			
			; ~ The corners of the cont chamber (needed to calculate whether the player is inside the chamber)
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x - 720.0 * RoomScale, r\y + 0.5, r\z + 880.0 * RoomScale)
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 176.0 * RoomScale, r\y + 0.5, r\z - 144.0 * RoomScale)	
			
			For i = 7 To 8
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("SCP-035 Addendum", "paper", r\x + 248.0 * RoomScale, r\y + 220.0 * RoomScale, r\z + 576.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x - 544.0 * RoomScale, r\y + 0.5, r\z + 704.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-500-01", "scp500pill", r\x + 1168.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 576 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Metal Panel", "scp148", r\x - 360.0 * RoomScale, r\y + 0.5, r\z + 644.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-035", "paper", r\x + 1168.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 408.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room513"
			;[Block]
			d.Doors = CreateDoor(r\x - 704.0 * RoomScale, r\y, r\z + 304.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 312.0 * RoomScale, r\y + 414.0 * RoomScale, r\z + 656.0 * RoomScale, r)
			sc\FollowPlayer = True
			
			it.Items = CreateItem("SCP-513", "scp513", r\x - 32.0 * RoomScale, r\y + 196.0 * RoomScale, r\z + 688.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Blood-stained Note", "paper", r\x + 736.0 * RoomScale, r\y + 1.0, r\z + 48.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-513", "paper", r\x - 480.0 * RoomScale, r\y + 104.0 * RoomScale, r\z - 176.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room966"
			;[Block]
			d.Doors = CreateDoor(r\x - 400.0 * RoomScale, r\y, r\z, -90.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			d.Doors = CreateDoor(r\x, r\y, r\z - 480.0 * RoomScale, 180.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 0.5, r\z + 512.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x, r\y + 0.5, r\z)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x - 312.0 * RoomScale, r\y + 452.0 * RoomScale, r\z + 644.0 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 320.0 * RoomScale, r\y + 0.5, r\z + 704.0 * RoomScale)
			it\State = Rnd(1000.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room3storage"
			;[Block]
			; ~ Elevator Doors
			r\RoomDoors.Doors[0] = CreateDoor(r\x, r\y, r\z + 448.0 * RoomScale, 0.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 5840.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 1048.0 * RoomScale, 0.0, r, False, Elevator_Door)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True), EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True) - 0.031, True)					
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True), EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True) + 0.031, True)
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x + 608.0 * RoomScale, r\y, r\z - 313.0 * RoomScale, 180.0, r, True, Elevator_Door)
			r\RoomDoors[2]\AutoClose = False
			PositionEntity(r\RoomDoors[2]\Buttons[0], EntityX(r\RoomDoors[2]\Buttons[0], True), EntityY(r\RoomDoors[2]\Buttons[0], True), EntityZ(r\RoomDoors[2]\Buttons[0], True) + 0.03, True)					
			PositionEntity(r\RoomDoors[2]\Buttons[1], EntityX(r\RoomDoors[2]\Buttons[1], True), EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[1], True) - 0.03, True)
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x - 456.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z - 824.0 * RoomScale, 180.0, r, False, Elevator_Door)
			r\RoomDoors[3]\AutoClose = False
			PositionEntity(r\RoomDoors[3]\Buttons[0], EntityX(r\RoomDoors[3]\Buttons[0], True), EntityY(r\RoomDoors[3]\Buttons[0], True), EntityZ(r\RoomDoors[3]\Buttons[0], True) + 0.031, True)					
			PositionEntity(r\RoomDoors[3]\Buttons[1], EntityX(r\RoomDoors[3]\Buttons[1], True), EntityY(r\RoomDoors[3]\Buttons[1], True), EntityZ(r\RoomDoors[3]\Buttons[1], True) - 0.031, True)
			
			; ~ Other doors
			r\RoomDoors.Doors[4] = CreateDoor(r\x + 56.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 6344.0 * RoomScale, 90.0, r, False, Heavy_Door)
			r\RoomDoors[4]\AutoClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[4]\Buttons[i]) : r\RoomDoors[4]\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x + 1157.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 660.0 * RoomScale, 0.0, r, False, Heavy_Door)
			d\Locked = 1 : d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x + 234.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 5239.0 * RoomScale, 90.0, r, False, Heavy_Door)
			d\Locked = 1 : d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x + 3446.0 * RoomScale, r\y - 5632.0 * RoomScale, r\z + 6369.0 * RoomScale, 90.0, r, False, Heavy_Door)
			d\Locked = 1 : d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			; ~ Elevators' pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 240.0 * RoomScale, r\z + 752.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 5840.0 * RoomScale, r\y - 5392.0 * RoomScale, r\z + 1360.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 608.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 624.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 456.0 * RoomScale, r\y - 5392.0 * RoomScale, r\z - 1136 * RoomScale)
			
			; ~ Waypoints # 1
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 2128.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 2048.0 * RoomScale)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 2128.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z - 1136.0 * RoomScale)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 3824.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z - 1168.0 * RoomScale)
			
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x + 3760.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 2048.0 * RoomScale)
			
			r\Objects[8] = CreatePivot()
			PositionEntity(r\Objects[8], r\x + 4848.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 112.0 * RoomScale)
			
			; ~ Waypoints # 2
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x + 592.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 6352.0 * RoomScale)
			
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x + 2928.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 6352.0 * RoomScale)
			
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x + 2928.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 5200.0 * RoomScale)
			
			r\Objects[12] = CreatePivot()
			PositionEntity(r\Objects[12], r\x + 592.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 5200.0 * RoomScale)
			
			; ~ Waypoints # 3
			r\Objects[13] = CreatePivot()
			PositionEntity(r\Objects[13], r\x + 1136.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 2944.0 * RoomScale)
			
			r\Objects[14] = CreatePivot()
			PositionEntity(r\Objects[14], r\x + 1104.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1184.0 * RoomScale)
			
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x - 464.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 1216.0 * RoomScale)
			
			r\Objects[16] = CreatePivot()
			PositionEntity(r\Objects[16], r\x - 432.0 * RoomScale, r\y - 5550.0 * RoomScale, r\z + 2976.0 * RoomScale)
			
			; ~ Corpses
			r\Objects[17] = CreatePivot()
			PositionEntity(r\Objects[17], r\x + 2200.0 * RoomScale, r\y - 5540.0 * RoomScale, r\z + 2932.0 * RoomScale)
			
			r\Objects[18] = CreatePivot()
			PositionEntity(r\Objects[18], r\x + 1015.5 * RoomScale, r\y - 5540.0 * RoomScale, r\z + 2964.0 * RoomScale)
			
			For i = 0 To 18
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For k = 10 To 11
				r\Objects[k * 2] = CopyEntity(o\LeverModelID[0])
				r\Objects[k * 2 + 1] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[k - 10] = r\Objects[k * 2 + 1]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k * 2 + i], 0.04, 0.04, 0.04)
					If k = 10
						PositionEntity(r\Objects[k * 2 + i], r\x + 3095.5 * RoomScale, r\y - 5461.0 * RoomScale, r\z + 6568.0 * RoomScale)
					Else
						PositionEntity(r\Objects[k * 2 + i], r\x + 1215.5 * RoomScale, r\y - 5461.0 * RoomScale, r\z + 3164.0 * RoomScale)
					EndIf
					EntityParent(r\Objects[k * 2 + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k * 2], 0.0, 0.0, 0.0)
				RotateEntity(r\Objects[k * 2 + 1], -10.0, 0.0 - 180.0, 0.0)
				EntityPickMode(r\Objects[k * 2 + 1], 1, False)
				EntityRadius(r\Objects[k * 2 + 1], 0.1)
			Next
			
			em.Emitters = CreateEmitter(r\x + 5218.0 * RoomScale, r\y - 5584.0 * RoomScale, r\z - 600.0 * RoomScale, 0)
			em\RandAngle = 15.0 : em\Speed = 0.03 : em\SizeChange = 0.01 : em\AlphaChange = -0.006 : em\Gravity = -0.2 
			TurnEntity(em\OBJ, 20.0, -100.0, 0.0)
			EntityParent(em\OBJ, r\OBJ) : em\room = r
			
			Select Rand(3)
				Case 1
					;[Block]
					xTemp = 2312.0
					zTemp = -952.0
					;[End Block]
				Case 2
					;[Block]
					xTemp = 3032.0
					zTemp = 1288.0
					;[End Block]
				Case 3
					;[Block]
					xTemp = 2824.0
					zTemp = 2808.0
					;[End Block]
			End Select
			
			it.Items = CreateItem("Black Severed Hand", "hand2", r\x + xTemp * RoomScale, r\y - 5596.0 * RoomScale + 1.0, r\z + zTemp * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 1936.0 * RoomScale, r\y - 5496.0 * RoomScale, r\z - 944.0 * RoomScale)
			it\State = Rnd(1000.0)
			EntityParent(it\Collider, r\OBJ)
			
			de.Decals = CreateDecal(3, r\x + xTemp * RoomScale, r\y - 5632.0 * RoomScale + 0.01, r\z + zTemp * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(3, r\x + 2268.0 * RoomScale, r\y - 5510.0 * RoomScale, r\z + 2932.0 * RoomScale, 0.0, r\Angle + 270.0, 0.0, 0.3)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(7, r\x + 1215.5 * RoomScale, r\y - 5632.0 * RoomScale + 0.01, r\z + 2964.0 * RoomScale, 90.0, r\Angle + 180.0, 0.0, 0.4)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case "room049"
			;[Block]
			; ~ Elevator doors
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 328.0 * RoomScale, r\y, r\z + 656.0 * RoomScale, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)	
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 2908.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z + 1824.0 * RoomScale, -90.0, r, False, Elevator_Door)
			r\RoomDoors[1]\AutoClose = False	
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) + 0.018, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) - 0.018, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)	
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 672.0 * RoomScale, r\y, r\z - 408.0 * RoomScale, 0.0, r, True, Elevator_Door)
			r\RoomDoors[2]\AutoClose = False
			PositionEntity(r\RoomDoors[2]\Buttons[0], EntityX(r\RoomDoors[2]\Buttons[0], True), EntityY(r\RoomDoors[2]\Buttons[0], True), EntityZ(r\RoomDoors[2]\Buttons[0], True) - 0.031, True)
			PositionEntity(r\RoomDoors[2]\Buttons[1], EntityX(r\RoomDoors[2]\Buttons[1], True), EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[1], True) + 0.031, True)				
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x - 2766.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1592.0 * RoomScale, 0.0, r, False, Elevator_Door)
			r\RoomDoors[3]\AutoClose = False	
			PositionEntity(r\RoomDoors[3]\Buttons[0], EntityX(r\RoomDoors[3]\Buttons[0], True), EntityY(r\RoomDoors[3]\Buttons[0], True), EntityZ(r\RoomDoors[3]\Buttons[0], True) - 0.031, True)
			PositionEntity(r\RoomDoors[3]\Buttons[1], EntityX(r\RoomDoors[3]\Buttons[1], True), EntityY(r\RoomDoors[3]\Buttons[1], True), EntityZ(r\RoomDoors[3]\Buttons[1], True) + 0.031, True)
			
			; ~ Storage room doors
			r\RoomDoors.Doors[4] = CreateDoor(r\x + 272.0 * RoomScale, r\y - 3552.0 * RoomScale, r\z + 104.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[4]\AutoClose = False : r\RoomDoors[4]\Locked = 1
			
			r\RoomDoors.Doors[5] = CreateDoor(r\x + 264.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1824.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[5]\AutoClose = False : r\RoomDoors[5]\Locked = 1
			PositionEntity(r\RoomDoors[5]\Buttons[0], EntityX(r\RoomDoors[5]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[5]\Buttons[0], True), EntityZ(r\RoomDoors[5]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[5]\Buttons[1], EntityX(r\RoomDoors[5]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[5]\Buttons[1], True), EntityZ(r\RoomDoors[5]\Buttons[1], True), True)					
			
			r\RoomDoors.Doors[6] = CreateDoor(r\x - 264.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z + 1824.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[6]\AutoClose = False : r\RoomDoors[6]\Locked = 1
			PositionEntity(r\RoomDoors[6]\Buttons[0], EntityX(r\RoomDoors[6]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[6]\Buttons[0], True), EntityZ(r\RoomDoors[6]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[6]\Buttons[1], EntityX(r\RoomDoors[6]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[6]\Buttons[1], True), EntityZ(r\RoomDoors[6]\Buttons[1], True), True)
			
			; ~ DNA door
			d.Doors = CreateDoor(r\x, r\y, r\z, 0.0, r, False, Heavy_Door, -2)
			d\AutoClose = False
			
			; ~ Other doors
			d.Doors = CreateDoor(r\x - 272.0 * RoomScale, r\y - 3552.0 * RoomScale, r\z + 98.0 * RoomScale, 90.0, r, True, Big_Door)
			d\AutoClose = False : d\MTFClose = False : d\Locked = 1
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x - 2990.0 * RoomScale, r\y - 3520.0 * RoomScale, r\z - 1824.0 * RoomScale, 90.0, r, False, Heavy_Door)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d.Doors = CreateDoor(r\x - 896.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 90.0, r, False, Heavy_Door)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			; ~ Elevators' pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 640.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 656.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 3211.0 * RoomScale, r\y - 3280.0 * RoomScale, r\z + 1824.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 672.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 93.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 2766.0 * RoomScale, r\y - 3280.0 * RoomScale, r\z - 1277.0 * RoomScale)
			
			; ~ Zombie # 1
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x + 528.0 * RoomScale, r\y - 3440.0 * RoomScale, r\z + 96.0 * RoomScale)
			
			; ~ Zombie # 2
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x  + 64.0 * RoomScale, r\y - 3440.0 * RoomScale, r\z - 1000.0 * RoomScale)
			
			For i = 0 To 5
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For k = 0 To 1
				r\Objects[k * 2 + 6] = CopyEntity(o\LeverModelID[0])
				r\Objects[k * 2 + 7] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[k] = r\Objects[k * 2 + 7]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k * 2 + 6 + i], 0.03, 0.03, 0.03)
					
					Select k
						Case 0 ; ~ Power feed
							;[Block]
							PositionEntity(r\Objects[k * 2 + 6 + i], r\x + 852.0 * RoomScale, r\y - 3374.0 * RoomScale, r\z - 854.0 * RoomScale)
							;[End Block]
						Case 1 ; ~ Generator
							;[Block]
							PositionEntity(r\Objects[k * 2 + 6 + i], r\x - 834.0 * RoomScale, r\y - 3400.0 * RoomScale, r\z + 1093.0 * RoomScale)
							;[End Block]
					End Select
					EntityParent(r\Objects[k * 2 + 6 + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k * 2 + 6], 0.0, 180.0 + 90.0 * (Not k), 0.0)
				RotateEntity(r\Objects[k * 2 + 7], 81.0 - 92.0 * k, 90.0 * (Not k), 0.0)
				EntityPickMode(r\Objects[k * 2 + 7], 1, False)
				EntityRadius(r\Objects[k * 2 + 7], 0.1)
			Next
			
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x - 832.0 * RoomScale, r\y - 3484.0 * RoomScale, r\z + 1572.0 * RoomScale)
			
			; ~ Spawnpoint for the map layout document
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x + 2642.0 * RoomScale, r\y - 3516.0 * RoomScale, r\z + 1822.0 * RoomScale)
			
			r\Objects[12] = CreatePivot()
			PositionEntity(r\Objects[12], r\x - 2666.0 * RoomScale, r\y - 3516.0 * RoomScale, r\z - 1792.0 * RoomScale)
			
			For i = 10 To 12
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Document SCP-049", "paper", r\x - 608.0 * RoomScale, r\y - 3332.0 * RoomScale, r\z + 876.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 4 Key Card", "key4", r\x - 512.0 * RoomScale, r\y - 3412.0 * RoomScale, r\z + 864.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 385.0 * RoomScale, r\y - 3412.0 * RoomScale, r\z + 271.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2_2"
			;[Block]
			For r2.Rooms = Each Rooms
				If r2 <> r Then
					If r2\RoomTemplate\Name = "room2_2" Then
						r\Objects[0] = CopyEntity(r2\Objects[0]) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If (Not r\Objects[0]) Then r\Objects[0] = LoadRMesh("GFX\map\fan_opt.rmesh", Null)
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[0], r\x - 248.0 * RoomScale, r\y + 528.0 * RoomScale, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "room012"
			;[Block]
			d.Doors = CreateDoor(r\x + 264.0 * RoomScale, r\y, r\z + 672.0 * RoomScale, 270.0, r, False, Default_Door, 3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 512.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 336.0 * RoomScale, 0.0, r)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\Locked = 1
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x + 176.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 352.0 * RoomScale, True)
			FreeEntity(r\RoomDoors[0]\Buttons[1]) : r\RoomDoors[0]\Buttons[1] = 0
			
			r\Objects[0] = CopyEntity(o\LeverModelID[0])
			r\Objects[1] = CopyEntity(o\LeverModelID[1])
			r\Levers[0] = r\Objects[1]
			
			For i = 0 To 1
				ScaleEntity(r\Objects[i], 0.04, 0.04, 0.04)
				PositionEntity(r\Objects[i], r\x + 240.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 364.0 * RoomScale, True)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			RotateEntity(r\Objects[1], 10.0, -180.0, 0.0)
			EntityPickMode(r\Objects[1], 1, False)
			EntityRadius(r\Objects[1], 0.1)
			
			r\Objects[2] = LoadRMesh("GFX\map\room012_2_opt.rmesh", Null)
			ScaleEntity(r\Objects[2], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[2], r\x - 360 * RoomScale, r\y - 130.0 * RoomScale, r\z + 456.0 * RoomScale)
			
			r\Objects[3] = CreateSprite()
			PositionEntity(r\Objects[3], r\x - 43.5 * RoomScale, r\y - 574.0 * RoomScale, r\z - 362.0 * RoomScale)
			ScaleSprite(r\Objects[3], 0.015, 0.015)
			EntityTexture(r\Objects[3], t\LightSpriteID[1])
			EntityBlend(r\Objects[3], 3)
			HideEntity(r\Objects[3])
			
			For i = 2 To 3
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[4] = LoadMesh_Strict("GFX\map\Props\012.b3d")
			ScaleEntity(r\Objects[4], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[4], r\x - 360.0 * RoomScale, r\y - 130.0 * RoomScale, r\z + 456.0 * RoomScale)
			EntityParent(r\Objects[4], r\Objects[2])
			
			it.Items = CreateItem("Document SCP-012", "paper", r\x - 56.0 * RoomScale, r\y - 576.0 * RoomScale, r\z - 408.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Severed Hand", "hand", r\x - 784.0 * RoomScale, r\y - 576.0 * RoomScale + 0.3, r\z + 640.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			de.Decals = CreateDecal(3, r\x - 784.0 * RoomScale, r\y - 768.0 * RoomScale + 0.01, r\z + 640.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			;[End Block]
		Case "room2tunnel2"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 544.0 * RoomScale, r\z + 512.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x, r\y + 544.0 * RoomScale, r\z - 512.0 * RoomScale)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room2pipes"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 368.0 * RoomScale, r\y, r\z)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 368.0 * RoomScale, r\y, r\z)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 224.0 * RoomScale - 0.005, r\y + 192.0 * RoomScale, r\z)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 224.0 * RoomScale + 0.005, r\y + 192.0 * RoomScale, r\z)
			
			For i = 0 To 3
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room3pit"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 704.0 * RoomScale, r\y + 112.0 * RoomScale, r\z - 416.0 * RoomScale)
			EntityParent(r\Objects[i], r\OBJ)
			
			em.Emitters = CreateEmitter(r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			
			em.Emitters = CreateEmitter(r\x - 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			;[End Block]
		Case "room2servers"
			;[Block]
			; ~ Locked room at the room's center
			d.Doors = CreateDoor(r\x, r\y, r\z, 0.0, r, False, Heavy_Door)
			d\Locked = 1
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 208.0 * RoomScale, r\y, r\z - 736.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.061, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.061, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x - 208.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) - 0.061, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) + 0.061, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)
			
			For k = 0 To 2
				r\Objects[k * 2] = CopyEntity(o\LeverModelID[0])
				r\Objects[k * 2 + 1] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[k] = r\Objects[k * 2 + 1]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k * 2 + i], 0.03, 0.03, 0.03)
					
					Select k
						Case 0 ; ~ Power switch
							;[Block]
							PositionEntity(r\Objects[k * 2 + i], r\x - 1260.0 * RoomScale, r\y + 234.0 * RoomScale, r\z + 750.0 * RoomScale)	
							;[End Block]
						Case 1 ; ~ Generator fuel pump
							;[Block]
							PositionEntity(r\Objects[k * 2 + i], r\x - 920.0 * RoomScale, r\y + 164.0 * RoomScale, r\z + 898.0 * RoomScale)
							;[End Block]
						Case 2 ; ~ Generator On / Off
							;[Block]
							PositionEntity(r\Objects[k * 2 + i], r\x - 837.0 * RoomScale, r\y + 152.0 * RoomScale, r\z + 886.0 * RoomScale)
							;[End Block]
					End Select
					EntityParent(r\Objects[k * 2 + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k * 2 + 1], 81, -180, 0)
				EntityPickMode(r\Objects[k * 2 + 1], 1, False)
				EntityRadius(r\Objects[k * 2 + 1], 0.1)
			Next
			RotateEntity(r\Objects[2 + 1], -81.0, -180.0, 0.0)
			RotateEntity(r\Objects[4 + 1], -81.0, -180.0, 0.0)
			
			; ~ SCP-096's spawnpoint
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 320 * RoomScale, r\y + 0.5, r\z)
			
			; ~ Guard's spawnpoint
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x - 1328.0 * RoomScale, r\y + 0.5, r\z + 528.0 * RoomScale)
			
			; ~ The point where the guard walks to
			r\Objects[8] = CreatePivot() 
			PositionEntity(r\Objects[8], r\x - 1376.0 * RoomScale, r\y + 0.5, r\z + 32.0 * RoomScale)
			
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x - 848.0 * RoomScale, r\y + 0.5, r\z + 576.0 * RoomScale)
			
			For i = 6 To 9
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room3servers"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 736.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 400.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 552.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 528.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x + 736.0 * RoomScale, r\y - 512.0 * RoomScale, r\z + 272.0 * RoomScale)
			
			r\Objects[3] = CopyEntity(o\NPCModelID[NPCTypeDuck])
			ScaleEntity(r\Objects[3], 0.07, 0.07, 0.07)
			Tex = LoadTexture_Strict("GFX\npcs\duck(2).png")
			EntityTexture(r\Objects[3], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			PositionEntity(r\Objects[3], r\x + 928.0 * RoomScale, r\y - 640.0 * RoomScale, r\z + 704.0 * RoomScale)
			
			For i = 0 To 3
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("9V Battery", "bat", r\x - 132.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 648.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x - 76.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 648.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x - 196.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 648.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x + 124.0 * RoomScale, r\y - 368.0 * RoomScale, r\z - 648.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room3servers2"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 504.0 * RoomScale, r\y - 512.0 * RoomScale, r\z + 271.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 628.0 * RoomScale, r\y - 512.0 * RoomScale, r\z + 271.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 532.0 * RoomScale, r\y - 512.0 * RoomScale, r\z - 877.0 * RoomScale)	
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Document SCP-970", "paper", r\x + 960.0 * RoomScale, r\y - 448.0 * RoomScale, r\z + 251.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)	
			EntityParent(it\Collider, r\OBJ)	
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 954.0 * RoomScale, r\y - 504.0 * RoomScale, r\z + 235.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)		
			;[End Block]
		Case "room2testroom2"
			;[Block]
			; ~ DNA door
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 720.0 * RoomScale, r\y, r\z, 0.0, r, False, Heavy_Door, -1)
			r\RoomDoors[0]\AutoClose = False
			
			; ~ Door to the center
			d.Doors = CreateDoor(r\x - 624.0 * RoomScale, r\y - 1280.0 * RoomScale, r\z, 90.0, r, True)	
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)			
			
			For xTemp = 0 To 1
				For zTemp = -1 To 1
					r\Objects[xTemp * 3 + (zTemp + 1)] = CreatePivot()
					PositionEntity(r\Objects[xTemp * 3 + (zTemp + 1)], r\x + (-236.0 + 280.0 * xTemp) * RoomScale, r\y - 700.0 * RoomScale, r\z + 384.0 * zTemp * RoomScale)
					EntityParent(r\Objects[xTemp * 3 + (zTemp + 1)], r\OBJ)
				Next
			Next
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x + 754.0 * RoomScale, r\y - 1248.0 * RoomScale, r\z)
			EntityParent(r\Objects[6], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r\x + 744.0 * RoomScale, r\y - 856.0 * RoomScale, r\z + 236.0 * RoomScale, r)
			sc\FollowPlayer = True
			
			it.Items = CreateItem("Document SCP-682", "paper", r\x + 656.0 * RoomScale, r\y - 1200.0 * RoomScale, r\z - 16.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2closets"
			;[Block]
			d.Doors = CreateDoor(r\x - 240.0 * RoomScale, r\y - 0.1 * RoomScale, r\z, 90.0, r)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x - 230.0 * RoomScale, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], r\x - 250.0 * RoomScale, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 1180.0 * RoomScale, r\y - 256.0 * RoomScale, r\z + 896.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 1292.0 * RoomScale, r\y - 256.0 * RoomScale, r\z - 160.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 1065.0 * RoomScale, r\y - 380.0 * RoomScale, r\z + 50.0 * RoomScale)
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x, r\y + 704.0 * RoomScale, r\z + 863.0 * RoomScale, r)
			sc\Angle = 180.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 736.0 * RoomScale, r\y + 176.0 * RoomScale, r\z + 544.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 736.0 * RoomScale, r\y + 100.0 * RoomScale, r\z - 448.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x + 730.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 580.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x + 740.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 750.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateItem("Level 0 Key Card", "key0", r\x + 736.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 752.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Clipboard", "clipboard", r\x - 400.0 * RoomScale, r\y - 50.0 * RoomScale, r\z - 700.0 * RoomScale)
			; ~ A hacky fix for clipboard's model and icon
			it\InvImg = it\ItemTemplate\InvImg
			SetAnimTime(it\Model, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it2.Items = CreateItem("Document SCP-1048", "paper", 1.0, 1.0, 1.0)
			it2\Picked = True : it2\Dropped = -1 : it\SecondInv[0] = it2
			HideEntity(it2\Collider)
			EntityParent(it2\Collider, r\OBJ)
			
			it.Items = CreateItem("Incident Report SCP-1048-A", "paper", r\x + 736.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 480.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2offices"
			;[Block]
			w.WayPoints = CreateWaypoint(r\x - 32.0 * RoomScale, r\y + 66.0 * RoomScale, r\z + 288.0 * RoomScale, Null, r)
			w2.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z - 448.0 * RoomScale, Null, r)
			w\connected[0] = w2 : w\Dist[0] = EntityDistance(w\OBJ, w2\OBJ)
			w2\connected[0] = w : w2\Dist[0] = w\Dist[0]
			
			it.Items = CreateItem("Document SCP-106", "paper", r\x + 404.0 * RoomScale, r\y + 145.0 * RoomScale, r\z + 559.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 2 Key Card", "key2", r\x - 156.0 * RoomScale, r\y + 151.0 * RoomScale, r\z + 72.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x + 305.0 * RoomScale, r\y + 153.0 * RoomScale, r\z + 944.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Notification", "paper", r\x - 137.0 * RoomScale, r\y + 153.0 * RoomScale, r\z + 464.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2offices2"
			;[Block]
			r\Objects[0] = CopyEntity(o\NPCModelID[NPCTypeDuck])
			ScaleEntity(r\Objects[0], 0.07, 0.07, 0.07)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 808.0 * RoomScale, r\y - 72.0 * RoomScale, r\z - 40.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 488.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 700.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 488.0 * RoomScale, r\y + 160.0 * RoomScale, r\z - 668.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 572.0 * RoomScale, r\y + 350.0 * RoomScale, r\z - 4.0 * RoomScale)
			
			Temp = Rand(1, 4)
			PositionEntity(r\Objects[0], EntityX(r\Objects[Temp], True), EntityY(r\Objects[Temp], True), EntityZ(r\Objects[Temp], True), True)
			
			For i = 0 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Level 1 Key Card", "key1", r\x - 368.0 * RoomScale, r\y - 48.0 * RoomScale, r\z + 80.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-895", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z + 368.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("Document SCP-860", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 464.0 * RoomScale)
			Else
				it.Items = CreateItem("SCP-093 Recovered Materials", "paper", r\x - 800.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 464.0 * RoomScale)
			EndIf
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("S-NAV Navigator", "nav", r\x - 336.0 * RoomScale, r\y - 48.0 * RoomScale, r\z - 480.0 * RoomScale)
			it\State = Rnd(100.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2offices3"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 1056.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 290.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[0]\AutoClose = False
			
			If Rand(2) = 1 Then 
				it.Items = CreateItem("Mobile Task Forces", "paper", r\x + 744.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 944.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)	
			Else
				it.Items = CreateItem("Security Clearance Levels", "paper", r\x + 680.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 944.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)			
			EndIf
			
			it.Items = CreateItem("Object Classes", "paper", r\x + 160.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 568.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)	
			
			it.Items = CreateItem("Document", "paper", r\x - 1440.0 * RoomScale, r\y + 624.0 * RoomScale, r\z + 152.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)	
			
			it.Items = CreateItem("Radio Transceiver", "radio", r\x - 1184.0 * RoomScale, r\y + 480.0 * RoomScale, r\z - 800.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)				
			
			For i = 0 To Rand(0, 1)
				it.Items = CreateItem("ReVision Eyedrops", "eyedrops", r\x - 1529.0 * RoomScale, r\y + 563.0 * RoomScale, r\z - 572.0 * RoomScale + i * 0.05)
				EntityParent(it\Collider, r\OBJ)				
			Next
			
			it.Items = CreateItem("9V Battery", "bat", r\x - 1545.0 * RoomScale, r\y + 603.0 * RoomScale, r\z - 372.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x - 1540.0 * RoomScale, r\y + 603.0 * RoomScale, r\z - 340.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x - 1529.0 * RoomScale, r\y + 603.0 * RoomScale, r\z - 308.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			;[End Block]
		Case "room173"
			;[Block]
			; ~ The containment doors
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 4000.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 1696.0 * RoomScale, 90.0, r, True, Big_Door)
			r\RoomDoors[1]\Locked = 0 : r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x + 2704.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 624.0 * RoomScale, 90.0, r)
			r\RoomDoors[2]\AutoClose = False : r\RoomDoors[2]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[2]\Buttons[i]) : r\RoomDoors[2]\Buttons[i] = 0
			Next
			
			d.Doors = CreateDoor(r\x + 1392.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 64.0 * RoomScale, 90.0, r, True)
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False
			
			d.Doors = CreateDoor(r\x - 640.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 64.0 * RoomScale, 90.0, r)
			d\Locked = 1 : d\AutoClose = False : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d.Doors = CreateDoor(r\x + 1264.0 * RoomScale, r\y + 383.9 * RoomScale, r\z + 312.0 * RoomScale, 180.0, r, True, One_Sided_Door)
			d\Locked = 1 : d\AutoClose = False : d\MTFClose = False
			PositionEntity(d\Buttons[0], r\x + 1120.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 322.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], r\x + 1120.0 * RoomScale, EntityY(d\Buttons[1], True), r\z + 302.0 * RoomScale, True)
			
			d.Doors = CreateDoor(r\x, r\y, r\z + 1184.0 * RoomScale, 0.0, r, False, Default_Door, 6)
			d\Locked = 1 : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			r\Objects[0] = LoadRMesh("GFX\map\IntroDesk_opt.rmesh", Null)
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[0], r\x + 272.0 * RoomScale, r\y, r\z + 400.0 * RoomScale)
			
			r\Objects[1] = LoadRMesh("GFX\map\IntroDrawer_opt.rmesh", Null)
			ScaleEntity(r\Objects[1], RoomScale, RoomScale, RoomScale)
			PositionEntity(r\Objects[1], r\x + 448.0 * RoomScale, r\y, r\z + 192.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], EntityX(r\OBJ) - 400.0 * RoomScale, r\y + 440.0 * RoomScale, EntityZ(r\OBJ) + 1322.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], EntityX(r\OBJ) + 1000.0 * RoomScale, r\y + 120.0 * RoomScale, EntityZ(r\OBJ) + 666.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], EntityX(r\OBJ) + 628.0 * RoomScale, r\y + 120.0 * RoomScale, EntityZ(r\OBJ) + 320.0 * RoomScale)
			
			For i = 0 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x - 336.0 * RoomScale, r\y + 352.0 * RoomScale, r\z + 48.0 * RoomScale, r, True, r\x + 1456.0 * RoomScale, r\y + 608.0 * RoomScale, r\z + 352.0 * RoomScale)
			sc\Angle = 270.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			
			de.Decals = CreateDecal(0, r\x + 272.0 * RoomScale, r\y + 0.005, r\z + 262.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			de.Decals = CreateDecal(0, r\x + 456.0 * RoomScale, r\y + 0.005, r\z + 135.0 * RoomScale, 90.0, Rnd(360.0), 0.0)
			EntityParent(de\OBJ, r\OBJ)
			
			For i = 0 To 4
				Select i
					Case 0
						;[Block]
						xTemp = 4305
						zTemp = 1234.0
						Temp = 4
						;[End Block]
					Case 1
						;[Block]
						xTemp = 5190.0
						zTemp = 2270.0
						Temp = 4
						;[End Block]
					Case 2
						;[Block]
						xTemp = 5222.0
						zTemp = 1224.0  
						Temp = 4
						;[End Block]
					Case 3
						;[Block]
						xTemp = 4320.0 
						zTemp = 2000.0
						Temp = 4
						;[End Block]
					Case 4
						;[Block]
						xTemp = 4978.0
						zTemp = 1985.0
						Temp = 6
						;[End Block]
				End Select
				de.Decals = CreateDecal(Temp, r\x + xTemp * RoomScale, r\y + 386.0 * RoomScale, r\z + zTemp * RoomScale, 90.0, 45.0, 0.0, ((i = 0) * 0.44) + ((i = 1) * 1.2) + ((i > 1) * 0.54), Rnd(0.8, 1.0))
				EntityParent(de\OBJ, r\OBJ)
			Next
			;[End Block]
		Case "room2scps"
			;[Block]
			d.Doors = CreateDoor(r\x + 272.0 * RoomScale, r\y, r\z, 90.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x - 272.0 * RoomScale, r\y, r\z, 270.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x - 560.0 * RoomScale, r\y, r\z - 272.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			d.Doors = CreateDoor(r\x + 560.0 * RoomScale, r\y, r\z - 272.0 * RoomScale, 180.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			d.Doors = CreateDoor(r\x + 560.0 * RoomScale, r\y, r\z + 272.0 * RoomScale, 180.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			d.Doors = CreateDoor(r\x - 560.0 * RoomScale, r\y, r\z + 272.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			
			For i = 0 To 3
				Select i
					Case 0
						;[Block]
						xTemp = 560.0
						zTemp = -416.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = -560.0
						zTemp = -416.0
						;[End Block]
					Case 2
						;[Block]
						xTemp = 560.0
						zTemp = 416.0
						;[End Block]
					Case 3
						;[Block]
						xTemp = -560.0
						zTemp = 416.0
						;[End Block]
				End Select
				sc.SecurityCams = CreateSecurityCam(r\x + xTemp * RoomScale, r\y + 386.0 * RoomScale, r\z + zTemp * RoomScale, r)
				If i < 2 Then
					sc\Angle = 180.0
				Else
					sc\Angle = 0.0
				EndIf
				sc\Turn = 30.0
				TurnEntity(sc\CameraOBJ, 30.0, 0.0, 0.0)
			Next
			
			For i = 0 To 14
				Select i
					Case 0
						;[Block]
						xTemp = -64.0
						zTemp = -516.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = -96.0
						zTemp = -388.0
						;[End Block]
					Case 2
						;[Block]
						xTemp = -128.0
						zTemp = -292.0
						;[End Block]
					Case 3
						;[Block]
						xTemp = -128.0
						zTemp = -132.0
						;[End Block]
					Case 4
						;[Block]
						xTemp = -160.0
						zTemp = -36.0
						;[End Block]
					Case 5
						;[Block]
						xTemp = -192.0
						zTemp = 28.0
						;[End Block]
					Case 6
						;[Block]
						xTemp = -384.0
						zTemp = 28.0
						;[End Block]
					Case 7
						;[Block]
						xTemp = -448.0
						zTemp = 92.0
						;[End Block]
					Case 8
						;[Block]
						xTemp = -480.0
						zTemp = 124.0
						;[End Block]
					Case 9
						;[Block]
						xTemp = -512.0
						zTemp = 156.0
						;[End Block]
					Case 10
						;[Block]
						xTemp = -544.0
						zTemp = 220.0
						;[End Block]
					Case 11
						;[Block]
						xTemp = -544.0
						zTemp = 380.0
						;[End Block]
					Case 12
						;[Block]
						xTemp = -544.0
						zTemp = 476.0
						;[End Block]
					Case 13
						;[Block]
						xTemp = -544.0
						zTemp = 572.0
						;[End Block]
					Case 14
						;[Block]
						xTemp = -544.0
						zTemp = 636.0
						;[End Block]
				End Select
				de.Decals = CreateDecal(Rand(16, 17), r\x + xTemp * RoomScale, r\y + 0.005, r\z + zTemp * RoomScale, 90.0, Rnd(360.0), 0.0, ((i =< 10) * Rnd(0.2, 0.25)) + ((i > 10) * Rnd(0.1, 0.17)))
				EntityParent(de\OBJ, r\OBJ)
			Next
			
			it.Items = CreateItem("SCP-714", "scp714", r\x - 555.5 * RoomScale, r\y + 220.0 * RoomScale, r\z - 758.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-1025", "scp1025", r\x + 552.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 758.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-860", "scp860", r\x + 568.0 * RoomScale, r\y + 178.0 * RoomScale, r\z + 750.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-714", "paper", r\x - 728.0 * RoomScale, r\y + 288.0 * RoomScale, r\z - 360.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-427", "paper", r\x - 608.0 * RoomScale, r\y + 66.0 * RoomScale, r\z + 636.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room205"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 1400.0 * RoomScale, r\y - 128.0 * RoomScale, r\z - 384.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			r\RoomDoors[0]\AutoClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[0]\Buttons[i]) : r\RoomDoors[0]\Buttons[i] = 0
			Next
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 128.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, r, False, Default_Door, 3)
			r\RoomDoors[1]\AutoClose = False
			
			sc.SecurityCams = CreateSecurityCam(r\x - 1152.0 * RoomScale, r\y + 900.0 * RoomScale, r\z + 176.0 * RoomScale, r, True, r\x - 1716.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 176.0 * RoomScale)
			sc\Angle = 90.0 : sc\Turn = 0.0 : sc\AllowSaving = False : sc\RenderInterval = 0.0
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			ScaleSprite(sc\ScrOBJ, 896.0 * 0.5 * RoomScale, 896.0 * 0.5 * RoomScale)
			CameraZoom(sc\Cam, 1.5)
			HideEntity(sc\OBJ)
			HideEntity(sc\CameraOBJ)
			HideEntity(sc\ScrOverlay)
			HideEntity(sc\MonitorOBJ)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 1536.0 * RoomScale, r\y + 730.0 * RoomScale, r\z + 192.0 * RoomScale)
			RotateEntity(r\Objects[0], 0.0, -90.0, 0.0)
			EntityParent(r\Objects[0], r\OBJ)
			
			r\Objects[1] = sc\ScrOBJ
			
			it.Items = CreateItem("Document SCP-205", "paper", r\x - 357.0 * RoomScale, r\y + 120.0 * RoomScale, r\z + 50.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room1endroom", "room1endroom3"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x, r\y, r\z + 1136.0 * RoomScale, r\y, r, False, Big_Door)
			r\RoomDoors[0]\AutoClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[0]\Buttons[i]) : r\RoomDoors[0]\Buttons[i] = 0
			Next
			;[End Block]
		Case "room895"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x, r\y, r\z - 448.0 * RoomScale, 0.0, r, False, Big_Door, 2)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x - 390.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[i], True), r\z - 280.0 * RoomScale, True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.025, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True) 
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, - 1320.0 * RoomScale, r\z + 2304.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 96.0 * RoomScale, r\y - 1532.0 * RoomScale, r\z + 2016.0 * RoomScale)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[2] = CopyEntity(o\LeverModelID[0])
			r\Objects[3] = CopyEntity(o\LeverModelID[1])
			
			r\Levers[0] = r\Objects[3]
			
			For i = 0 To 1
				ScaleEntity(r\Objects[2 + i], 0.04, 0.04, 0.04)
				PositionEntity(r\Objects[2 + i], r\x - 800.0 * RoomScale, r\y + 180.0 * RoomScale, r\z - 336.0 * RoomScale)
				EntityParent(r\Objects[2 + i], r\OBJ)
			Next
			RotateEntity(r\Objects[2], 0.0, 180.0, 0.0)
			RotateEntity(r\Objects[3], 10.0, 0.0, 0.0)
			EntityPickMode(r\Objects[3], 1, False)
			EntityRadius(r\Objects[3], 0.1)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 320.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 288.0 * RoomScale, r, True, r\x - 800.0 * RoomScale, r\y + 288.0 * RoomScale, r\z - 340.0 * RoomScale)
			sc\Angle = 225.0 : sc\Turn = 45.0 : sc\CoffinEffect = 1 : CoffinCam = sc
			TurnEntity(sc\CameraOBJ, 120.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 180.0, 0.0)
			
			it.Items = CreateItem("Document SCP-895", "paper", r\x - 688.0 * RoomScale, r\y + 133.0 * RoomScale, r\z - 304.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Level 3 Key Card", "key3", r\x + 240.0 * RoomScale, r\y - 1456.0 * RoomScale, r\z + 2064.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 280.0 * RoomScale, r\y - 1456.0 * RoomScale, r\z + 2164.0 * RoomScale)
			it\State = Rnd(1000.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2tesla", "room2tesla_lcz", "room2tesla_hcz"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 114.0 * RoomScale, r\y, r\z)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x + 114.0 * RoomScale, r\y, r\z)		
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x, r\y, r\z)	
			
			r\Objects[3] = CreateSprite()
			EntityTexture(r\Objects[3], t\MiscTextureID[13])
			SpriteViewMode(r\Objects[3], 2) 
			EntityBlend(r\Objects[3], 3) 
			EntityFX(r\Objects[3], 1 + 8 + 16)
			PositionEntity(r\Objects[3], r\x, r\y + 0.8, r\z)
			HideEntity(r\Objects[3])
			
			r\Objects[4] = CreateSprite()
			PositionEntity(r\Objects[4], r\x - 32.0 * RoomScale, r\y + 568.0 * RoomScale, r\z)
			ScaleSprite(r\Objects[4], 0.03, 0.03)
			EntityTexture(r\Objects[4], t\LightSpriteID[1])
			EntityBlend(r\Objects[4], 3)
			HideEntity(r\Objects[4])
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x, r\y, r\z - 800.0 * RoomScale)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x, r\y, r\z + 800.0 * RoomScale)
			
			For i = 0 To 6
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For r2.Rooms = Each Rooms
				If r2 <> r Then
					If r2\RoomTemplate\Name = "room2tesla" Lor r2\RoomTemplate\Name = "room2tesla_lcz" Lor r2\RoomTemplate\Name = "room2tesla_hcz" Then
						r\Objects[7] = CopyEntity(r2\Objects[7], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If (Not r\Objects[7])Then r\Objects[7] = LoadMesh_Strict("GFX\map\room2tesla_caution.b3d", r\OBJ)
			
			w.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z + 292.0 * RoomScale, Null, r)
			w2.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z - 284.0 * RoomScale, Null, r)
			w\connected[0] = w2 : w\Dist[0] = EntityDistance(w\OBJ, w2\OBJ)
			w2\connected[0] = w : w2\Dist[0] = w\Dist[0]
			;[End Block]
		Case "room2doors"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z + 528.0 * RoomScale, 0.0, r)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x - 832.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 167.0 * RoomScale, True) 
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			d2.Doors = CreateDoor(r\x, r\y, r\z - 528.0 * RoomScale, 180.0, r, True)
			d2\AutoClose = False
			FreeEntity(d2\Buttons[0]) : d2\Buttons[0] = 0
			PositionEntity(d2\Buttons[1], EntityX(d2\Buttons[1], True), EntityY(d2\Buttons[1], True), EntityZ(d2\Buttons[1], True) + 0.061, True)
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 832.0 * RoomScale, r\y + 0.5, r\z)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "room914"
			;[Block]
			r\RoomDoors.Doors[2] = CreateDoor(r\x, r\y, r\z - 368.0 * RoomScale, 0.0, r, False, Big_Door, 2)
			r\RoomDoors[2]\AutoClose = False
			PositionEntity(r\RoomDoors[2]\Buttons[0], r\x - 496.0 * RoomScale, EntityY(r\RoomDoors[2]\Buttons[0], True), r\z - 278.0 * RoomScale, True)
			PositionEntity(r\RoomDoors[2]\Buttons[1], EntityX(r\RoomDoors[2]\Buttons[1], True) + 0.025, EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[1], True), True) 
			TurnEntity(r\RoomDoors[2]\Buttons[0], 0.0, 90.0, 0.0)
			
			d.Doors = CreateDoor(r\x - 1037.0 * RoomScale, r\y, r\z + 528.0 * RoomScale, 180.0, r, True, SCP_914_Door)
			d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			r\RoomDoors[0] = d : d\AutoClose = False
			
			d.Doors = CreateDoor(r\x + 404.0 * RoomScale, r\y, r\z + 528.0 * RoomScale, 180.0, r, True, SCP_914_Door)
			d\AutoClose = False
			For i = 0 To 1
				FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
			Next
			
			r\RoomDoors[1] = d : d\AutoClose = False
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x - 448.0 * RoomScale, r\y, r\z - 705.0 * RoomScale, 90.0, r, False, Default_Door, 2)
			r\RoomDoors[3]\AutoClose = False
			PositionEntity(r\RoomDoors[3]\Buttons[0], EntityX(r\RoomDoors[3]\Buttons[0], True) - 0.061, EntityY(r\RoomDoors[3]\Buttons[0], True), EntityZ(r\RoomDoors[3]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[3]\Buttons[1], EntityX(r\RoomDoors[3]\Buttons[1], True) + 0.061, EntityY(r\RoomDoors[3]\Buttons[1], True), EntityZ(r\RoomDoors[3]\Buttons[1], True), True)
			
			r\Objects[0] = LoadMesh_Strict("GFX\map\Props\914key.b3d")
			PositionEntity(r\Objects[0], r\x - 416.0 * RoomScale, r\y + 190.0 * RoomScale, r\z + 374.0 * RoomScale, True)
			
			r\Objects[1] = LoadMesh_Strict("GFX\map\Props\914knob.b3d")
			PositionEntity(r\Objects[1], r\x - 416.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 374.0 * RoomScale, True)
			
			For i = 0 To 1
				ScaleEntity(r\Objects[i], RoomScale, RoomScale, RoomScale, True)
				EntityPickMode(r\Objects[i], 2)
			Next
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 1132.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 308.0 * RoomScale, r\y + 0.5, r\z + 640.0 * RoomScale)
			
			For i = 0 To 3
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Addendum: 5/14 Test Log", "paper", r\x + 538.0 * RoomScale, r\y + 228.0 * RoomScale, r\z + 127.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			RotateEntity(it\Collider, 0.0, 0.0, 0.0)	
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 538.0 * RoomScale, r\y + 112.0 * RoomScale, r\z - 40.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			
			it.Items = CreateItem("Dr. L's Note #1", "paper", r\x - 538.0 * RoomScale, r\y + 250.0 * RoomScale, r\z - 365.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
		Case "room173intro"
			;[Block]
			r\RoomDoors.Doors[1] = CreateDoor(EntityX(r\OBJ) + 288.0 * RoomScale, r\y, EntityZ(r\OBJ) + 384.0 * RoomScale, 90.0, r, False, Big_Door)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 1008.0 * RoomScale, r\y, r\z - 688.0 * RoomScale, 90.0, r)
			r\RoomDoors[2]\AutoClose = False : r\RoomDoors[2]\Locked = 1 : r\RoomDoors[2]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[2]\Buttons[i]) : r\RoomDoors[2]\Buttons[i] = 0
			Next
			
			r\RoomDoors.Doors[3] = CreateDoor(r\x - 2324.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[3]\AutoClose = False : r\RoomDoors[3]\Locked = 1 : r\RoomDoors[3]\MTFClose = False
			PositionEntity(r\RoomDoors[3]\Buttons[0], EntityX(r\RoomDoors[3]\Buttons[0], True) - 4.0 * RoomScale, EntityY(r\RoomDoors[3]\Buttons[0], True), EntityZ(r\RoomDoors[3]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[3]\Buttons[1], EntityX(r\RoomDoors[3]\Buttons[1], True) + 4.0 * RoomScale, EntityY(r\RoomDoors[3]\Buttons[1], True), EntityZ(r\RoomDoors[3]\Buttons[1], True), True)
			
			r\RoomDoors.Doors[4] = CreateDoor(r\x - 4352.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, r, True)
			r\RoomDoors[4]\AutoClose = False : r\RoomDoors[4]\Locked = 1 : r\RoomDoors[4]\MTFClose = False	
			
			Tex = LoadTexture_Strict("GFX\map\textures\Door02.jpg")
			For zTemp = 0 To 1
				d.Doors = CreateDoor(r\x - 5760.0 * RoomScale, r\y, r\z + (320.0 + 896.0 * zTemp) * RoomScale, 0.0, r)
				d\Locked = 1 : d\DisableWaypoint = True : d\AutoClose = False
				If zTemp = 0 Then
					FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
				Else
					FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
				EndIf
				
				d.Doors = CreateDoor(r\x - 8288.0 * RoomScale, r\y, r\z + (320.0 + 896.0 * zTemp) * RoomScale, 0.0, r, zTemp = 0)
				d\Locked = 1 : d\MTFClose = False : d\AutoClose = False
				If zTemp <> 0 Then 
					d\DisableWaypoint = True
					FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
				EndIf
				
				For xTemp = 0 To 2
					d.Doors = CreateDoor(r\x - (7424.0 - 512.0 * xTemp) * RoomScale, r\y, r\z + (1008.0 - 480.0 * zTemp) * RoomScale, 180.0 * (Not zTemp), r)
					d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False : d\AutoClose = False
					EntityTexture(d\OBJ, Tex)
					FreeEntity(d\OBJ2) : d\OBJ2 = 0
					For i = 0 To 1
						FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
					Next
				Next					
				For xTemp = 0 To 4
					d.Doors = CreateDoor(r\x - (5120.0 - 512.0 * xTemp) * RoomScale, r\y, r\z + (1008.0 - 480.0 * zTemp) * RoomScale, 180.0 * (Not zTemp), r)
					d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False : d\AutoClose = False
					EntityTexture(d\OBJ, Tex)
					FreeEntity(d\OBJ2) : d\OBJ2 = 0
					For i = 0 To 1
						FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
					Next
					
					If xTemp = 2 And zTemp = 1 Then r\RoomDoors[5] = d
				Next	
			Next
			DeleteSingleTextureEntryFromCache(Tex)
			
			; ~ The door in the office below the walkway
			r\RoomDoors.Doors[6] = CreateDoor(r\x - 3712.0 * RoomScale, r\y - 385.0 * RoomScale, r\z - 128.0 * RoomScale, 0.0, r, True, Default_Door, 6)
			r\RoomDoors[6]\AutoClose = False : r\RoomDoors[6]\MTFClose = False
			FreeEntity(r\RoomDoors[6]\Buttons[1]) : r\RoomDoors[6]\Buttons[1] = 0
			
			d.Doors = CreateDoor(r\x - 3712.0 * RoomScale, r\y - 385.0 * RoomScale, r\z - 2336.0 * RoomScale, 0.0, r)
			d\AutoClose = False : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			; ~ The door from the concrete tunnel to the large hall
			d.Doors = CreateDoor(r\x - 6864.0 * RoomScale, r\y, r\z - 1248.0 * RoomScale, 90.0, r, True)
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False
			
			; ~ The door to the staircase in the office room
			d.Doors = CreateDoor(r\x - 2448.0 * RoomScale, r\y, r\z - 1000.0 * RoomScale, 0.0, r, False, One_Sided_Door)
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			PositionEntity(d\Buttons[0], r\x - 2592.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 1010.0 * RoomScale, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], EntityX(r\OBJ) + 40.0 * RoomScale, 460.0 * RoomScale, EntityZ(r\OBJ) + 1072.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], EntityX(r\OBJ) - 80.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\OBJ) + 480.0 * RoomScale)
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], EntityX(r\OBJ) - 128.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\OBJ) + 320.0 * RoomScale)
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], EntityX(r\OBJ) + 660.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\OBJ) + 526.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], EntityX(r\OBJ) + 700.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\OBJ) + 320.0 * RoomScale)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], EntityX(r\OBJ) + 1472.0 * RoomScale, 100.0 * RoomScale, EntityZ(r\OBJ) + 912.0 * RoomScale)
			
			For i = 0 To 5
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 0 To 4
				Select i
					Case 0
						;[Block]
						xTemp = 587.0
						zTemp = -70.0
						Temp = 4
						;[End Block]
					Case 1
						;[Block]
						xTemp = 1472.0
						zTemp = 912.0  
						Temp = 4
						;[End Block]
					Case 2
						;[Block]
						xTemp = 1504.0
						zTemp = -80.0
						Temp = 4
						;[End Block]
					Case 3
						;[Block]
						xTemp = 602.0 
						zTemp = 642.0
						Temp = 4
						;[End Block]
					Case 4
						;[Block]
						xTemp = 1260.0
						zTemp = 627.0
						Temp = 6
						;[End Block]
				End Select
				de.Decals = CreateDecal(Temp, r\x + xTemp * RoomScale, r\y + 2.0 * RoomScale, r\z + zTemp * RoomScale, 90.0, 45.0, 0.0, ((i = 0) * 0.44) + ((i = 1) * 1.2) + ((i > 1) * 0.54), Rnd(0.8, 1.0))
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x - 4048.0 * RoomScale, r\y - 32.0 * RoomScale, r\z - 1232.0 * RoomScale, r, True, r\x - 2256.0 * RoomScale, r\y + 224.0 * RoomScale, r\z - 928.0 * RoomScale)
			sc\Angle = 270.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			
			it.Items = CreateItem("Class D Orientation Leaflet", "paper", r\x - (2914.0 + 1024.0) * RoomScale, r\y + 170.0 * RoomScale, r\z + 40.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2ccont"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z + 368.0 * RoomScale, 0.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			d.Doors = CreateDoor(r\x - 720.0 * RoomScale, r\y + 896.0 * RoomScale, r\z + 736.0 * RoomScale, 90.0, r, False, One_Sided_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			For k = 0 To 2
				r\Objects[k * 2] = CopyEntity(o\LeverModelID[0])
				r\Objects[k * 2 + 1] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[k] = r\Objects[k * 2 + 1]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k * 2 + i], 0.04, 0.04, 0.04)
					PositionEntity(r\Objects[k * 2 + i], r\x - 240.0 * RoomScale, r\y + 1104.0 * RoomScale, r\z + (632.0 - 64.0 * k) * RoomScale)
					EntityParent(r\Objects[k * 2 + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k * 2], 0.0, -90.0, 0.0)
				RotateEntity(r\Objects[k * 2 + 1], 10.0, -90.0 - 180.0, 0.0)
				EntityPickMode(r\Objects[k * 2 + 1], 1, False)
				EntityRadius(r\Objects[k * 2 + 1], 0.1)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x - 265.0 * RoomScale, r\y + 1280.0 * RoomScale, r\z + 105.0 * RoomScale, r)
			sc\Angle = 45.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("Note from Daniel", "paper", r\x - 400.0 * RoomScale, r\y + 1040.0 * RoomScale, r\z + 115.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room106"
			;[Block]
			; ~ Elevators' doors
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 647.0 * RoomScale, r\y - 7327.9 * RoomScale, r\z - 803.0 * RoomScale, 90.0, r, False, Elevator_Door) 
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) + 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x - 708.0 * RoomScale, r\y, r\z - 704.0 * RoomScale, 90.0, r, True, Elevator_Door) 
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) + 0.018, EntityY(r\RoomDoors[1]\Buttons[0], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) - 0.018, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)
			
			; ~ Other doors
			d.Doors = CreateDoor(r\x - 968.0 * RoomScale, r\y - 8092.0 * RoomScale, r\z + 1328.0 * RoomScale, 180.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) - 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) + 0.061, True)	
			
			d.Doors = CreateDoor(r\x, r\y - 7328.0 * RoomScale, r\z - 529.0 * RoomScale, 0.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.029, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.029, True)
			
			d.Doors = CreateDoor(r\x - 624.0 * RoomScale, r\y - 8608.0 * RoomScale, r\z - 64.0 * RoomScale, 90.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x - 176.0 * RoomScale, r\y - 7328.0 * RoomScale, r\z - 1697.0 * RoomScale, 0.0, r, False, Heavy_Door) 
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			
			d.Doors = CreateDoor(r\x + 384.0 * RoomScale, r\y, r\z - 704.0 * RoomScale, 90.0, r, False, Heavy_Door) 
			d\AutoClose = False : d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)
			
			; ~ Levers
			For k = 0 To 2 Step 2
				r\Objects[k] = CopyEntity(o\LeverModelID[0])
				r\Objects[k + 1] = CopyEntity(o\LeverModelID[1])
				
				r\Levers[k / 2] = r\Objects[k + 1]
				
				For i = 0 To 1
					ScaleEntity(r\Objects[k + i], 0.04, 0.04, 0.04)
					PositionEntity(r\Objects[k + i], r\x - (555.0 - 81.0 * (k / 2.0)) * RoomScale, r\y - 7904.0 * RoomScale, r\z + 2976.0 * RoomScale, True)
					EntityParent(r\Objects[k + i], r\OBJ)
				Next
				RotateEntity(r\Objects[k], 0.0, 0.0, 0.0)
				RotateEntity(r\Objects[k + 1], 10.0, -180.0, 0.0)
				EntityPickMode(r\Objects[k + 1], 1, False)
				EntityRadius(r\Objects[k + 1], 0.1)
			Next
			RotateEntity(r\Objects[1], 81.0, -180.0, 0.0)
			RotateEntity(r\Objects[3], -81.0, -180.0, 0.0)			
			
			r\Objects[4] = CreateButton(0, r\x - 146.0 * RoomScale, r\y - 7904.0 * RoomScale, r\z + 2989.0 * RoomScale)
			
			r\Objects[5] = CreatePivot()
			TurnEntity(r\Objects[5], 0.0, 180.0, 0.0)
			PositionEntity(r\Objects[5], r\x + 1088.0 * RoomScale, r\y - 6224.0 * RoomScale, r\z + 1824.0 * RoomScale) 
			
			; ~ Chamber		
			r\Objects[6] = LoadRMesh("GFX\map\room1062_opt.rmesh", Null)
			ScaleEntity(r\Objects[6], RoomScale, RoomScale, RoomScale)
			EntityType(r\Objects[6], HIT_MAP)
			EntityPickMode(r\Objects[6], 3)
			PositionEntity(r\Objects[6], r\x + 784.0 * RoomScale, r\y - 8308.0 * RoomScale, r\z + 656.0 * RoomScale)
			
			For i = 4 To 6
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x + 768.0 * RoomScale, r\y - 5936.0 * RoomScale, r\z + 1632.0 * RoomScale, r, True, r\x - 272.0 * RoomScale, r\y - 7872.0 * RoomScale, r\z + 2956.0 * RoomScale)
			sc\Angle = 315.0 : sc\Turn = 20.0 : sc\CoffinEffect = 0
			TurnEntity(sc\CameraOBJ, 45.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, -10.0, 0.0)
			
			r\Objects[7] = sc\CameraOBJ
			r\Objects[8] = sc\OBJ
			
			sc.SecurityCams = CreateSecurityCam(r\x - 1216.0 * RoomScale, r\y - 7664.0 * RoomScale, r\z + 1404.0 * RoomScale, r)
			sc\Angle = 315.0 : sc\Turn = 30.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			r\Objects[9] = CreatePivot()
			PositionEntity(r\Objects[9], r\x - 272.0 * RoomScale, r\y - 8000.0 * RoomScale, r\z + 2672.0 * RoomScale)
			
			r\Objects[10] = CreatePivot()
			PositionEntity(r\Objects[10], r\x, r\y - 7325.0 * RoomScale, r\z - 784.0 * RoomScale)
			
			; ~ Elevators' pivots
			r\Objects[11] = CreatePivot()
			PositionEntity(r\Objects[11], r\x - 951.0 * RoomScale, r\y - 7088.0 * RoomScale, r\z - 803.0 * RoomScale)
			
			r\Objects[12] = CreatePivot()
			PositionEntity(r\Objects[12], r\x - 1012.0 * RoomScale, r\y + 240.0 * RoomScale, r\z - 704.0 * RoomScale)
			
			For i = 9 To 12
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Level 5 Key Card", "key5", r\x - 752.0 * RoomScale, r\y - 7920.0 * RoomScale, r\z + 2962.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Dr. Allok's Note", "paper", r\x - 416.0 * RoomScale, r\y - 7904.0 * RoomScale, r\z + 2428.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Recall Protocol RP-106-N", "paper", r\x + 268.0 * RoomScale, r\y - 7904.0 * RoomScale, r\z + 2529.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room1archive"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z - 528.0 * RoomScale, 0.0, r, False, Default_Door, Rand(1, 3))
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 256.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 640.0 * RoomScale, r)
			sc\Angle = 180.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			For xTemp2 = 0 To 1
				For yTemp2 = 0 To 2
					For zTemp2 = 0 To 2
						TempStr = "9V Battery" : TempStr2 = "bat"
						
						Local ItemChance% = Rand(-10, 100)
						
						Select True
							Case ItemChance < 0
								;[Block]
								Exit
								;[End Block]
							Case ItemChance < 40 ; ~ 40% chance for a document
								;[Block]
								TempStr = "Document SCP-"
								Select Rand(22)
									Case 1
										;[Block]
										TempStr = TempStr + "005"
										;[End Block]
									Case 2
										;[Block]
										TempStr = TempStr + "008"
										;[End Block]
									Case 3
										;[Block]
										TempStr = TempStr + "012"
										;[End Block]
									Case 4
										;[Block]
										TempStr = TempStr + "035"
										;[End Block]
									Case 5
										;[Block]
										TempStr = TempStr + "049"
										;[End Block]
									Case 6
										;[Block]
										TempStr = TempStr + "096"
										;[End Block]
									Case 7
										;[Block]
										TempStr = TempStr + "106"
										;[End Block]
									Case 8
										;[Block]
										TempStr = TempStr + "173"
										;[End Block]
									Case 9
										;[Block]
										TempStr = TempStr + "205"
										;[End Block]
									Case 10
										;[Block]
										TempStr = TempStr + "409"
										;[End Block]
									Case 11
										;[Block]
										TempStr = TempStr + "513"
										;[End Block]
									Case 12
										;[Block]
										TempStr = TempStr + "682"
										;[End Block]
									Case 13
										;[Block]
										TempStr = TempStr + "714"
										;[End Block]
									Case 14
										;[Block]
										TempStr = TempStr + "860"
										;[End Block]
									Case 15
										;[Block]
										TempStr = TempStr + "860-1"
										;[End Block]
									Case 16
										;[Block]
										TempStr = TempStr + "895"
										;[End Block]
									Case 17
										;[Block]
										TempStr = TempStr + "939"
										;[End Block]
									Case 18
										;[Block]
										TempStr = TempStr + "966"
										;[End Block]
									Case 19
										;[Block]
										TempStr = TempStr + "970"
										;[End Block]
									Case 20
										;[Block]
										TempStr = TempStr + "1048"
										;[End Block]
									Case 21
										;[Block]
										TempStr = TempStr + "1162"
										;[End Block]
									Case 22
										;[Block]
										TempStr = TempStr + "1499"
										;[End Block]
								End Select
								TempStr2 = "paper"
								;[End Block]
							Case ItemChance >= 40 And ItemChance < 45 ; ~ 5% chance for a key card
								;[Block]
								Temp3 = Rand(0, 2)
								TempStr = "Level " + Str(Temp3) + " Key Card"
								TempStr2 = "key" + Str(Temp3)
								;[End Block]
							Case ItemChance >= 45 And ItemChance < 50 ; ~ 5% chance for a medkit
								;[Block]
								TempStr = "First Aid Kit"
								TempStr2 = "firstaid"
								;[End Block]
							Case ItemChance >= 50 And ItemChance < 60 ; ~ 10% chance for a battery
								;[Block]
								TempStr = "9V Battery"
								TempStr2 = "bat"
								;[End Block]
							Case ItemChance >= 60 And ItemChance < 70 ; ~ 10% chance for an SNAV
								;[Block]
								TempStr = "S-NAV Navigator"
								TempStr2 = "nav"
								;[End Block]
							Case ItemChance >= 70 And ItemChance < 85 ; ~ 15% chance for a radio
								;[Block]
								TempStr = "Radio Transceiver"
								TempStr2 = "radio"
								;[End Block]
							Case ItemChance >= 85 And ItemChance < 95 ; ~ 10% chance for a clipboard
								;[Block]
								TempStr = "Clipboard"
								TempStr2 = "clipboard"
								;[End Block]
							Case ItemChance >= 95 And ItemChance =< 100 ; ~ 5% chance for misc
								;[Block]
								Temp3 = Rand(1, 3)
								Select Temp3
									Case 1 ; ~ Playing card
										;[Block]
										TempStr = "Playing Card"
										TempStr2 = "playcard"
										;[End Block]
									Case 2 ; ~ Mastercard
										;[Block]
										TempStr = "Mastercard"
										TempStr2 = Lower(TempStr)
										;[End Block]
									Case 3 ; ~ Origami
										;[Block]
										TempStr = "Origami"
										TempStr2 = Lower(TempStr)
										;[End Block]
								End Select
								;[End Block]
						End Select
						xTemp = (-672.0) + 864.0 * xTemp2
						yTemp = 96.0 + 96.0 * yTemp2
						zTemp = 480.0 - 352.0 * zTemp2 + Rnd(-96.0, 96.0)
						
						it.Items = CreateItem(TempStr, TempStr2, r\x + xTemp * RoomScale, r\y + yTemp * RoomScale, r\z + zTemp * RoomScale)
						EntityParent(it\Collider, r\OBJ)							
					Next
				Next
			Next
			;[End Block]
		Case "room1123"
			;[Block]
			; ~ Fake door to the contianment chamber itself
			d.Doors = CreateDoor(r\x + 832.0 * RoomScale, r\y + 512.0 * RoomScale, r\z + 368.0 * RoomScale, 0.0, r, True, One_Sided_Door, 3)
			d\AutoClose = False : d\Locked = 1 : d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.12, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			; ~ Door to the containment chamber itself
			d.Doors = CreateDoor(r\x + 832.0 * RoomScale, r\y, r\z + 368.0 * RoomScale, 0.0, r, False, One_Sided_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.12, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.12, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			; ~ Door to the pre-containment chamber
			d.Doors = CreateDoor(r\x + 280.0 * RoomScale, r\y, r\z - 607.0 * RoomScale, 90.0, r)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			; ~ Fake door to the pre-containment chamber
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 280.0 * RoomScale, r\y + 512.0 * RoomScale, r\z - 607.0 * RoomScale, 90.0, r)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			FreeEntity(r\RoomDoors[0]\Buttons[1]) : r\RoomDoors[0]\Buttons[1] = 0
			
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x + 832.0 * RoomScale, r\y + 166.0 * RoomScale, r\z + 784.0 * RoomScale)
			
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 648.0 * RoomScale, r\y + 592.0 * RoomScale, r\z + 692.0 * RoomScale)
			
			r\Objects[5] = CreatePivot()
			PositionEntity(r\Objects[5], r\x + 828.0 * RoomScale, r\y + 592.0 * RoomScale, r\z + 592.0 * RoomScale)
			
			r\Objects[6] = CreatePivot()
			PositionEntity(r\Objects[6], r\x - 76.0 * RoomScale, r\y + 620.0 * RoomScale, r\z + 744.0 * RoomScale)
			
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x - 640.0 * RoomScale, r\y + 620.0 * RoomScale, r\z - 864.0 * RoomScale)	
			
			r\Objects[8] = CopyEntity(o\DoorModelID[8])
			PositionEntity(r\Objects[8], r\x - 272.0 * RoomScale, r\y + 512.0 * RoomScale, r\z + 288.0 * RoomScale)
			RotateEntity(r\Objects[8], 0.0, 90.0, 0.0)
			ScaleEntity(r\Objects[8], 45.0 * RoomScale, 45.0 * RoomScale, 80.0 * RoomScale)	
			
			r\Objects[9] = CopyEntity(o\DoorModelID[9])
			ScaleEntity(r\Objects[9], 46.0 * RoomScale, 45.0 * RoomScale, 46.0 * RoomScale)
			PositionEntity(r\Objects[9], r\x - 272.0 * RoomScale, r\y + 512.0 * RoomScale, r\z + (288.0 - 70.0) * RoomScale)
			RotateEntity(r\Objects[9], 0.0, 10.0, 0.0)
			EntityType(r\Objects[9], HIT_MAP)
			EntityPickMode(r\Objects[9], 2)
			
			r\Objects[10] = CopyEntity(r\Objects[8])
			PositionEntity(r\Objects[10], r\x - 272.0 * RoomScale, r\y + 512.0 * RoomScale, r\z + 736.0 * RoomScale)
			RotateEntity(r\Objects[10], 0.0, 90.0, 0.0)
			ScaleEntity(r\Objects[10], 45.0 * RoomScale, 45.0 * RoomScale, 80.0 * RoomScale)
			
			r\Objects[11] =  CopyEntity(r\Objects[9])
			ScaleEntity(r\Objects[11], 46.0 * RoomScale, 45.0 * RoomScale, 46.0 * RoomScale)
			PositionEntity(r\Objects[11], r\x - 272.0 * RoomScale, r\y + 512.0 * RoomScale, r\z + (736.0-70) * RoomScale)
			RotateEntity(r\Objects[11], 0.0, 90.0, 0.0)
			EntityType(r\Objects[11], HIT_MAP)
			EntityPickMode(r\Objects[11], 2)
			
			r\Objects[12] = CopyEntity(r\Objects[8])
			PositionEntity(r\Objects[12], r\x - 592.0 * RoomScale, r\y + 512.0 * RoomScale, r\z - 704.0 * RoomScale)
			RotateEntity(r\Objects[12], 0.0, 0.0, 0.0)
			ScaleEntity(r\Objects[12], 45.0 * RoomScale, 45.0 * RoomScale, 80.0 * RoomScale)
			
			r\Objects[13] = CopyEntity(r\Objects[9])
			ScaleEntity(r\Objects[13], 46.0 * RoomScale, 45.0 * RoomScale, 46.0 * RoomScale)
			PositionEntity(r\Objects[13], r\x - (592.0 + 70.0) * RoomScale, r\y + 512.0 * RoomScale, r\z - 704.0 * RoomScale)
			RotateEntity(r\Objects[13], 0.0, 0.0, 0.0)
			EntityType(r\Objects[13], HIT_MAP)
			EntityPickMode(r\Objects[13], 2)
			
			For i = 3 To 13
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			it.Items = CreateItem("Document SCP-1123", "paper", r\x + 511.0 * RoomScale, r\y + 125.0 * RoomScale, r\z - 936.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCP-1123", "scp1123", r\x + 832.0 * RoomScale, r\y + 166.0 * RoomScale, r\z + 784.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Leaflet", "paper", r\x - 816.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 888.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Gas Mask", "gasmask", r\x + 457.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + 960.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "pocketdimension"
			;[Block]
			; ~ Doors inside fake tunnel
			r\RoomDoors.Doors[0] = CreateDoor(r\x, r\y + 2060.0 * RoomScale, r\z + 32.0 - 1024.0 * RoomScale, 0.0, r, False, Heavy_Door)
			r\RoomDoors[0]\AutoClose = False
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x, r\y + 2048.0 * RoomScale, r\z + 32.0 + 1024.0 * RoomScale, 180.0, r, False, Heavy_Door)
			r\RoomDoors[1]\AutoClose = False
			
			Local Entity%
			Local Hallway% = LoadRMesh("GFX\map\pocketdimension2_opt.rmesh", Null) ; ~ The tunnels in the first room
			
			r\Objects[8] = LoadRMesh("GFX\map\pocketdimension3_opt.rmesh", Null) ; ~ The room with the throne, moving pillars etc 
			
			r\Objects[9] = LoadRMesh("GFX\map\pocketdimension4_opt.rmesh", Null) ; ~ The flying pillar
			
			r\Objects[10] = CopyEntity(r\Objects[9])
			
			r\Objects[11] = LoadRMesh("GFX\map\pocketdimension5_opt.rmesh", Null) ; ~ The pillar room
			
			Local Terrain% = LoadMesh_Strict("GFX\map\pocketdimensionterrain.b3d")
			
			ScaleEntity(Terrain, RoomScale, RoomScale, RoomScale, True)
			PositionEntity(Terrain, r\x, r\y + 29440.0, r\z, True)
			
			For k = 0 To -1
				Select k
					Case 0
						;[Block]
						Entity = Hallway 
						;[End Block]
					Case 1
						;[Block]
						Entity = r\Objects[8]
						;[End Block]
					Case 2
						;[Block]
						Entity = r\Objects[9]
						;[End Block]
					Case 3
						;[Block]
						Entity = r\Objects[10]
						;[End Block]
					Case 4
						;[Block]
						Entity = r\Objects[11]
						;[End Block]
				End Select 
			Next
			
			For i = 8 To 11
				ScaleEntity(r\Objects[i], RoomScale, RoomScale, RoomScale)
				EntityType(r\Objects[i], HIT_MAP)
				EntityPickMode(r\Objects[i], 2)
				PositionEntity(r\Objects[i], r\x, r\y, r\z + 32.0, True)
			Next
			
			ScaleEntity(Terrain, RoomScale, RoomScale, RoomScale)
			EntityType(Terrain, HIT_MAP)
			EntityPickMode(Terrain, 3)
			PositionEntity(Terrain, r\x, r\y + 2944.0 * RoomScale, r\z + 32.0, True)			
			
			de.Decals = CreateDecal(13, r\x - (1536.0 * RoomScale), r\y + 0.02, r\z + 608.0 * RoomScale + 32.0, 90.0, 0.0, 0.0, 0.8, 1.0, 1 + 8, 2)
			
			ScaleEntity(r\Objects[10], RoomScale * 1.5, RoomScale * 2.0, RoomScale * 1.5, True)			
			PositionEntity(r\Objects[11], r\x, r\y, r\z + 64.0, True)	
			
			For i = 1 To 8
				r\Objects[i - 1] = CopyEntity(Hallway)
				
				Angle = (i - 1) * (360.0 / 8.0)
				
				ScaleEntity(r\Objects[i - 1], RoomScale, RoomScale, RoomScale)
				EntityType(r\Objects[i - 1], HIT_MAP)
				EntityPickMode(r\Objects[i - 1], 2)
				RotateEntity(r\Objects[i - 1], 0.0, Angle - 90.0, 0.0)
				PositionEntity(r\Objects[i - 1], r\x + Cos(Angle) * (512.0 * RoomScale), r\y, r\z + Sin(Angle) * (512.0 * RoomScale))
				EntityParent(r\Objects[i - 1], r\OBJ)
				
				If i < 6 Then 
					de.Decals = CreateDecal(i + 7, r\x + Cos(Angle) * (512.0 * RoomScale) * 3.0, r\y + 0.02, r\z + Sin(Angle) * (512.0 * RoomScale) * 3.0, 90.0, Angle - 90.0, 0.0, 0.5, 1.0, 1 + 8, 2)
				EndIf				
			Next
			
			For i = 12 To 16
				r\Objects[i] = CreatePivot(r\Objects[11])
				Select i
					Case 12
						;[Block]
						PositionEntity(r\Objects[i], r\x, r\y + 200.0 * RoomScale, r\z + 64.0, True)
						;[End Block]
					Case 13
						;[Block]
						PositionEntity(r\Objects[i], r\x + 390.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 + 272.0 * RoomScale, True)
						;[End Block]
					Case 14
						;[Block]
						PositionEntity(r\Objects[i], r\x + 838.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 - 551.0 * RoomScale, True)	
						;[End Block]
					Case 15
						;[Block]
						PositionEntity(r\Objects[i], r\x - 139.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 64.0 + 1201.0 * RoomScale, True)
						;[End Block]
					Case 16
						;[Block]
						PositionEntity(r\Objects[i], r\x - 1238.0 * RoomScale, r\y - 1664.0 * RoomScale, r\z + 64.0 + 381.0 * RoomScale, True)
						;[End Block]
				End Select 
			Next
			
			r\Textures[0] = LoadTexture_Strict("GFX\npcs\pd_plane.png", 1 + 2, DeleteAllTextures)
			
			r\Textures[1] = LoadTexture_Strict("GFX\npcs\pd_plane_eye.png", 1 + 2, DeleteAllTextures)
			
			Tex = LoadTexture_Strict("GFX\npcs\scp_106_eyes.png", 1, DeleteAllTextures)
			r\Objects[17] = CreateSprite()
			EntityTexture(r\Objects[17], Tex)
			DeleteSingleTextureEntryFromCache(Tex)
			PositionEntity(r\Objects[17], EntityX(r\Objects[8], True), r\y + 1376.0 * RoomScale, EntityZ(r\Objects[8], True) - 2848.0 * RoomScale)
			ScaleSprite(r\Objects[17], 0.03, 0.03)
			EntityBlend(r\Objects[17], 3)
			EntityFX(r\Objects[17], 1 + 8)
			SpriteViewMode(r\Objects[17], 2)
			HideEntity(r\Objects[17])
			
			r\Objects[18] = CreateSprite()
			ScaleSprite(r\Objects[18], 8.0, 8.0)
			EntityTexture(r\Objects[18], r\Textures[0])
			EntityOrder(r\Objects[18], 100)
			EntityBlend(r\Objects[18], 2)
			EntityFX(r\Objects[18], 1 + 8)
			SpriteViewMode(r\Objects[18], 2)
			HideEntity(r\Objects[18])
			
			r\Objects[19] = LoadMesh_Strict("GFX\map\throne_wall.b3d")
			PositionEntity(r\Objects[19], EntityX(r\Objects[8], True), r\y, EntityZ(r\Objects[8], True) - 864.5 * RoomScale)
			ScaleEntity(r\Objects[19], RoomScale / 2.04, RoomScale, RoomScale)
			EntityPickMode(r\Objects[19], 2)
			EntityType(r\Objects[19], HIT_MAP)
			EntityParent(r\Objects[19], r\OBJ)
			HideEntity(r\Objects[19])
			
			FreeEntity(Entity)
			FreeEntity(Hallway)
			
			it.Items = CreateItem("Burnt Note", "paper", EntityX(r\OBJ), r\y + 0.5, EntityZ(r\OBJ) + 3.5)
			;[End Block]
		Case "room3z3"
			;[Block]
			sc.SecurityCams = CreateSecurityCam(r\x - 320.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 512.25 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room2_3", "room3_3"
			;[Block]
			w.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z, Null, r)
			;[End Block]
		Case "room1lifts"
			;[Block]
			For i = 0 To 1
				r\Objects[i] = CreateButton(0, r\x + (96.0 + (i * -192.0)) * RoomScale, r\y + 160.0 * RoomScale, r\z + 71.0 * RoomScale, 0.0, 0.0, 0.0, 0, True)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x + 384.0 * RoomScale, r\y + (448.0 - 64.0) * RoomScale, r\z - 960.0 * RoomScale, r)
			sc\Angle = 45.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			w.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z, Null, r)
			;[End Block]
		Case "room2servers2"
			;[Block]
			d.Doors = CreateDoor(r\x + 264.0 * RoomScale, r\y, r\z + 672.0 * RoomScale, 270.0, r, False, Default_Door, 3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)	
			
			d.Doors = CreateDoor(r\x - 512.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 336.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)	
			
			d.Doors = CreateDoor(r\x - 509.0 * RoomScale, r\y - 768.0 * RoomScale, r\z - 1037.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\Locked = 1 : d\DisableWaypoint = True : d\MTFClose = False
			FreeEntity(d\Buttons[0]) : d\Buttons[0] = 0
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.012, True)
			
			it.Items = CreateItem("Night Vision Goggles", "nvg", r\x + 56.0154 * RoomScale, r\y - 648.0 * RoomScale, r\z + 749.638 * RoomScale)
			it\State = Rnd(1000.0)
			RotateEntity(it\Collider, 0.0, r\Angle + Rand(245), 0.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2gw", "room2gw_b"
			;[Block]
			If r\RoomTemplate\Name = "room2gw_b" Then
				r\Objects[0] = CreatePivot()
				PositionEntity(r\Objects[0], r\x + 280.0 * RoomScale, r\y + 345.0 * RoomScale, r\z - 340.0 * RoomScale)
				
				r\Objects[1] = CreatePivot()
				PositionEntity(r\Objects[1], r\x - 156.0 * RoomScale, r\y + 0.5, r\z + 121.0 * RoomScale)
				
				For i = 0 To 1
					EntityParent(r\Objects[i], r\OBJ)
				Next
			EndIf
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 336.0 * RoomScale, r\y, r\z - 382.0 * RoomScale, 0.0, r, True)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\Locked = 1 : r\RoomDoors[0]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[0]\Buttons[i]) : r\RoomDoors[0]\Buttons[i] = 0
			Next
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 336.0 * RoomScale, r\y, r\z + 462.0 * RoomScale, 180.0, r, True)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\Locked = 1 : r\RoomDoors[1]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			For r2.Rooms = Each Rooms
				If r2 <> r Then
					If r2\RoomTemplate\Name = "room2gw" Lor r2\RoomTemplate\Name = "room2gw_b" Then
						r\Objects[2] = CopyEntity(r2\Objects[2], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If (Not r\Objects[2]) Then r\Objects[2] = LoadMesh_Strict("GFX\map\room2gw_pipes.b3d", r\OBJ)
			EntityPickMode(r\Objects[2], 2)
			
			If r\RoomTemplate\Name = "room2gw" Then
				r\Objects[0] = CreatePivot()
				PositionEntity(r\Objects[0], r\x + 344.0 * RoomScale, r\y + 128.0 * RoomScale, r\z)
				EntityParent(r\Objects[0], r\OBJ)
				
				Local BD_Temp% = False
				
				If bk\IsBroken Then
					If bk\x = r\x And bk\z = r\z Then
						BD_Temp = True
					EndIf
				EndIf
				
				If ((Not bk\IsBroken) And Rand(2) = 1) Lor BD_Temp Then
					r\Objects[1] = CopyEntity(o\DoorModelID[0])
					ScaleEntity(r\Objects[1], (204.0 * RoomScale) / MeshWidth(r\Objects[1]), 313.0 * RoomScale / MeshHeight(r\Objects[1]), 16.0 * RoomScale / MeshDepth(r\Objects[1]))
					EntityType(r\Objects[1], HIT_MAP)
					PositionEntity(r\Objects[1], r\x + 336.0 * RoomScale, r\y, r\z + 462.0 * RoomScale)
					RotateEntity(r\Objects[1], 0.0, 180.0 + 180.0, 0.0)
					EntityParent(r\Objects[1], r\OBJ)
					MoveEntity(r\Objects[1], 120.0, 0.0, 5.0)
					
					bk\IsBroken = True
					bk\x = r\x
					bk\z = r\z
					
					FreeEntity(r\RoomDoors[1]\OBJ2) : r\RoomDoors[1]\OBJ2 = 0
				EndIf
			EndIf
			;[End Block]
		Case "room3gw"
			;[Block]
			d.Doors = CreateDoor(r\x - 728.0 * RoomScale, r\y, r\z - 456.5 * RoomScale, 0.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.044, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.044, True)	
			
			d.Doors = CreateDoor(r\x - 222.5 * RoomScale, r\y, r\z - 736.0 * RoomScale, -90.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.052, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.052, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			r\RoomDoors.Doors[0] = CreateDoor(r\x - 459.0 * RoomScale, r\y, r\z + 339.0 * RoomScale, 90.0, r, True, Default_Door)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\Locked = 1 : r\RoomDoors[0]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[0]\Buttons[i]) : r\RoomDoors[0]\Buttons[i] = 0
			Next
			
			r\RoomDoors[1] = CreateDoor(r\x + 385.0 * RoomScale, r\y, r\z + 339.0 * RoomScale, 270.0, r, True, Default_Door)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\Locked = 1 : r\RoomDoors[1]\MTFClose = False
			For i = 0 To 1
				FreeEntity(r\RoomDoors[1]\Buttons[i]) : r\RoomDoors[1]\Buttons[i] = 0
			Next
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x - 48.0 * RoomScale, r\y + 128.0 * RoomScale, r\z + 320.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			For r2.Rooms = Each Rooms
				If r2 <> r Then
					If r2\RoomTemplate\Name = "room3gw" Then
						r\Objects[2] = CopyEntity(r2\Objects[2], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If (Not r\Objects[2]) Then r\Objects[2] = LoadMesh_Strict("GFX\map\room3gw_pipes.b3d", r\OBJ)
			EntityPickMode(r\Objects[2], 2)
			;[End Block]
		Case "room1162"
			;[Block]
			d.Doors = CreateDoor(r\x + 248.0 * RoomScale, r\y, r\z - 736.0 * RoomScale, 90.0, r, False, Default_Door, 2)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 1012.0 * RoomScale, r\y + 128.0 * RoomScale, r\z - 640.0 * RoomScale)
			EntityPickMode(r\Objects[0], 1)
			EntityParent(r\Objects[0], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 192.0 * RoomScale, r\y + 704.0 * RoomScale, r\z + 192.0 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("Document SCP-1162", "paper", r\x + 863.227 * RoomScale, r\y + 152.0 * RoomScale, r\z - 953.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2scps2"
			;[Block]
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 288.0 * RoomScale, r\y, r\z + 576.0 * RoomScale, 90.0, r, False, Default_Door, 3)
			r\RoomDoors[0]\Locked = 1
			
			d.Doors = CreateDoor(r\x + 778.5 * RoomScale, r\y, r\z + 671.0 * RoomScale, 90.0, r, False, Default_Door, 4)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.02, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.02, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x + 556.0 * RoomScale, r\y, r\z + 296.0 * RoomScale, 0.0, r, False, Default_Door, 3)
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.031, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.031, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 576.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 632.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			
			For i = 0 To 1
				Select i
					Case 0
						;[Block]
						xTemp = 850.0
						yTemp = 352.0
						zTemp = 876.0
						;[End Block]
					Case 1
						;[Block]
						xTemp = 600.0
						yTemp = 512.0
						zTemp = 150.0
						;[End Block]
				End Select
				sc.SecurityCams = CreateSecurityCam(r\x + xTemp * RoomScale, r\y + yTemp * RoomScale, r\z + zTemp * RoomScale, r)
				If i = 0 Then
					sc\Angle = 220.0
				Else
					sc\Angle = 180.0
				EndIf
				sc\Turn = 30.0
				TurnEntity(sc\CameraOBJ, 30.0, 0.0, 0.0)
			Next
			
			it.Items = CreateItem("SCP-1499", "scp1499", r\x + 600.0 * RoomScale, r\y + 176.0 * RoomScale, r\z - 228.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, r\Angle, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-1499", "paper", r\x + 840.0 * RoomScale, r\y + 260.0 * RoomScale, r\z + 224.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Emily Ross' Badge", "badge", r\x + 364.0 * RoomScale, r\y + 5.0 * RoomScale, r\z + 716.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-500", "paper", r\x + 890.0 * RoomScale, r\y + 228.0 * RoomScale, r\z + 490.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ) : RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			
			If Rand(4) = 1 Then
				it.Items = CreateItem("SCP-500", "scp500", r\x + 1152.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 336.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ) : RotateEntity(it\Collider, 0.0, 180.0, 0.0)
			EndIf
			;[End Block]
		Case "room3offices"
			;[Block]			
			d.Doors = CreateDoor(r\x + 736.0 * RoomScale, r\y, r\z + 240.0 * RoomScale, 0.0, r, False, One_Sided_Door, 3)
			PositionEntity(d\Buttons[0], r\x + 892.0 * RoomScale, EntityY(d\Buttons[0], True), r\z + 226.0 * RoomScale, True)
			PositionEntity(d\Buttons[1], r\x + 892.0 * RoomScale, EntityY(d\Buttons[1], True), r\z + 253.0 * RoomScale, True)
			
			r\Objects[0] = LoadMesh_Strict("GFX\map\room3offices_hb.b3d", r\OBJ)
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			;[End Block]
		Case "room2offices4"
			;[Block]
			d.Doors = CreateDoor(r\x - 240.0 * RoomScale, r\y - 0.1 * RoomScale, r\z, 90.0, r)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], r\x - 230.0 * RoomScale, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], r\x - 250.0 * RoomScale, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			it.Items = CreateItem("Sticky Note", "paper", r\x - 991.0 * RoomScale, r\y - 242.0 * RoomScale, r\z + 904.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2sl"
			;[Block]
			; ~ Doors for room
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 480.0 * RoomScale, r\y, r\z - 640.0 * RoomScale, 90.0, r, False, Default_Door, 3)
			r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\MTFClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], r\x + 576.0 * RoomScale, EntityY(r\RoomDoors[0]\Buttons[0], True), r\z - 474.0 * RoomScale, True)
			RotateEntity(r\RoomDoors[0]\Buttons[0], 0.0, 270.0, 0.0)
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x + 544.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 256.0 * RoomScale, 270.0, r, False, One_Sided_Door, 3)
			r\RoomDoors[1]\AutoClose = False : r\RoomDoors[1]\MTFClose = False
			
			d.Doors = CreateDoor(r\x + 1504.0 * RoomScale, r\y + 480.0 * RoomScale, r\z + 960.0 * RoomScale, 0.0, r)
			d\AutoClose = False : d\Locked = 1 : d\MTFClose = False
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			Local Scale# = RoomScale * 4.5 * 0.4
			Local Screen%
			
			r\Textures[0] = LoadAnimTexture_Strict("GFX\SL_monitors_checkpoint.png", 1, 512, 512, 0, 4, DeleteAllTextures)
			r\Textures[1] = LoadAnimTexture_Strict("GFX\Sl_monitors.png", 1, 256, 256, 0, 10, DeleteAllTextures)
			
			; ~ Monitor Objects
			For i = 0 To 14
				If i <> 7 Then
					r\Objects[i] = CopyEntity(o\MonitorModelID[0])
					ScaleEntity(r\Objects[i], Scale, Scale, Scale)
					If i <> 4 And i <> 13 Then
						Screen = CreateSprite()
						EntityFX(Screen, 17)
						SpriteViewMode(Screen, 2)
						ScaleSprite(Screen, MeshWidth(o\MonitorModelID[0]) * Scale * 0.95 * 0.5, MeshHeight(o\MonitorModelID[0]) * Scale * 0.95 * 0.5)
						Select i
							Case 0
								;[Block]
								EntityTexture(Screen, r\Textures[1], 0)
								;[End Block]
							Case 1
								;[Block]
								EntityTexture(Screen, r\Textures[1], 9)
								;[End Block]
							Case 2
								;[Block]
								EntityTexture(Screen, r\Textures[1], 2)
								;[End Block]
							Case 3
								;[Block]
								EntityTexture(Screen, r\Textures[1], 1)
								;[End Block]
							Case 5
								;[Block]
								EntityTexture(Screen, r\Textures[1], 8)
								;[End Block]
							Case 8
								;[Block]
								EntityTexture(Screen, r\Textures[1], 4)
								;[End Block]
							Case 9
								;[Block]
								EntityTexture(Screen, r\Textures[1], 5)
								;[End Block]
							Case 10
								;[Block]
								EntityTexture(Screen, r\Textures[1], 3)
								;[End Block]
							Case 11
								;[Block]
								EntityTexture(Screen, r\Textures[1], 7)
								;[End Block]
							Default
								;[Block]
								EntityTexture(Screen, r\Textures[0], 3)
								;[End Block]
						End Select
						EntityParent(Screen, r\Objects[i])
					ElseIf i = 4 Then
						r\Objects[20] = CreateSprite()
						EntityFX(r\Objects[20], 17)
						SpriteViewMode r\Objects[20], 2
						ScaleSprite(r\Objects[20], MeshWidth(o\MonitorModelID[0]) * Scale * 0.95 * 0.5, MeshHeight(o\MonitorModelID[0]) * Scale * 0.95 * 0.5)
						EntityTexture(r\Objects[20], r\Textures[0], 2)
						EntityParent(r\Objects[20], r\Objects[i])
					Else
						r\Objects[21] = CreateSprite()
						EntityFX(r\Objects[21], 17)
						SpriteViewMode(r\Objects[21], 2)
						ScaleSprite(r\Objects[21], MeshWidth(o\MonitorModelID[0]) * Scale * 0.95 * 0.5, MeshHeight(o\MonitorModelID[0]) * Scale * 0.95 * 0.5)
						EntityTexture(r\Objects[21], r\Textures[1], 6)
						EntityParent(r\Objects[21], r\Objects[i])
					EndIf
				EndIf
			Next
			
			For i = 0 To 2
				PositionEntity(r\Objects[i], r\x - 207.94 * RoomScale, r\y + (648.0 + (112.0 * i)) * RoomScale, r\z - 60.0686 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 105.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 3 To 5
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 3))) * RoomScale, r\z + 95.7443 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 6 To 8 Step 2
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 6))) * RoomScale, r\z + 255.744 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 9 To 11
				PositionEntity(r\Objects[i], r\x - 231.489 * RoomScale, r\y + (648.0 + (112.0 * (i - 9))) * RoomScale, r\z + 415.744 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 90.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			For i = 12 To 14
				PositionEntity(r\Objects[i], r\x - 208.138 * RoomScale, r\y + (648.0 + (112.0 * (i - 12))) * RoomScale, r\z + 571.583 * RoomScale)
				RotateEntity(r\Objects[i], 0.0, 75.0 + r\Angle, 0.0)
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			; ~ Main PathPoint for SCP-049
			r\Objects[7] = CreatePivot()
			PositionEntity(r\Objects[7], r\x, r\y + 100.0 * RoomScale, r\z - 800.0 * RoomScale, True)
			EntityParent(r\Objects[7], r\OBJ)
			
			; ~ PathPoints for SCP-049
			r\Objects[15] = CreatePivot()
			PositionEntity(r\Objects[15], r\x + 700.0 * RoomScale, r\y + 700.0 * RoomScale, r\z + 256.0 * RoomScale)
			
			r\Objects[16] = CreatePivot()
			PositionEntity(r\Objects[16], r\x - 60.0 * RoomScale, r\y + 700.0 * RoomScale, r\z + 200.0 * RoomScale)
			
			r\Objects[17] = CreatePivot()
			PositionEntity(r\Objects[17], r\x - 48.0 * RoomScale, r\y + 540.0 * RoomScale, r\z + 656.0 * RoomScale)
			
			For i = 15 To 17
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			r\Objects[9 * 2] = CopyEntity(o\LeverModelID[0])
			r\Objects[9 * 2 + 1] = CopyEntity(o\LeverModelID[1])
			
			r\Levers[0] = r\Objects[9 * 2 + 1]
			
			For i = 0 To 1
				ScaleEntity(r\Objects[9 * 2 + i], 0.04, 0.04, 0.04)
				PositionEntity(r\Objects[9 * 2 + i], r\x - 49.0 * RoomScale, r\y + 689.0 * RoomScale, r\z + 912.0 * RoomScale)
				EntityParent(r\Objects[9 * 2 + i], r\OBJ)
			Next
			
			RotateEntity(r\Objects[9 * 2], 0.0, 0.0, 0.0)
			RotateEntity(r\Objects[9 * 2 + 1], 10.0, 0.0 - 180.0, 0.0)
			EntityPickMode(r\Objects[9 * 2 + 1], 1, False)
			EntityRadius(r\Objects[9 * 2 + 1], 0.1)
			
			; ~ Camera in the room itself
			sc.SecurityCams = CreateSecurityCam(r\x - 159.0 * RoomScale, r\y + 384.0 * RoomScale, r\z - 929.0 * RoomScale, r, True, r\x - 231.489 * RoomScale, r\y + 760.0 * RoomScale, r\z + 255.744 * RoomScale)
			sc\Angle = 315.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			TurnEntity(sc\ScrOBJ, 0.0, 90.0, 0.0)
			;[End Block]
		Case "room2_4"
			;[Block]
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 640.0 * RoomScale, r\y + 8.0 * RoomScale, r\z - 896.0 * RoomScale)
			EntityParent(r\Objects[0], r\OBJ)
			;[End Block]
		Case "room2clockroom2"
			;[Block]
			d.Doors = CreateDoor(r\x - 736.0 * RoomScale, r\y, r\z - 104.0 * RoomScale, 0.0, r)
			d\Timer = 70.0 * 5.0 : d\AutoClose = False : d\Locked = 1
			PositionEntity(d\Buttons[0], r\x - 288.0 * RoomScale, EntityY(d\Buttons[0], True), r\z - 634.0 * RoomScale, True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			d2.Doors = CreateDoor(r\x + 104.0 * RoomScale, r\y, r\z + 736.0 * RoomScale, 270.0, r)
			d2\Timer = 70.0 * 5.0 : d2\AutoClose = False : d2\Locked = 1
			PositionEntity(d2\Buttons[0], r\x + 634.0 * RoomScale, EntityY(d2\Buttons[0], True), r\z + 288.0 * RoomScale, True)
			RotateEntity(d2\Buttons[0], 0.0, 90.0, 0.0, True)
			FreeEntity(d2\Buttons[1]) : d2\Buttons[1] = 0
			
			d\LinkedDoor = d2
			d2\LinkedDoor = d
			
			Scale = RoomScale * 4.5 * 0.4
			
			r\Objects[0] = CopyEntity(o\MonitorModelID[0])
			ScaleEntity(r\Objects[0], Scale, Scale, Scale)
			PositionEntity(r\Objects[0], r\x + 668.0 * RoomScale, r\y + 1.1, r\z - 96.0 * RoomScale)
			RotateEntity(r\Objects[0], 0.0, 90.0, 0.0)
			
			r\Objects[1] = CopyEntity(o\MonitorModelID[0])
			ScaleEntity(r\Objects[1], Scale, Scale, Scale)
			PositionEntity(r\Objects[1], r\x + 96.0 * RoomScale, r\y + 1.1, r\z - 668.0 * RoomScale)
			
			For i = 0 To 1
				EntityParent(r\Objects[i], r\OBJ)
			Next
			;[End Block]
		Case "room2cpit"
			;[Block]
			d.Doors = CreateDoor(r\x - 256.0 * RoomScale, r\y, r\z - 752.0 * RoomScale, 90.0, r, False, Heavy_Door, 3)
			d\Locked = 1 : d\AutoClose = False : d\MTFClose = False : d\DisableWaypoint = True
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			FreeEntity(d\Buttons[1]) : d\Buttons[1] = 0
			
			em.Emitters = CreateEmitter(r\x + 512.0 * RoomScale, r\y - 76.0 * RoomScale, r\z - 688.0 * RoomScale, 0)
			em\RandAngle = 55.0 : em\Speed = 0.0005 : em\AlphaChange = -0.015 : em\SizeChange = 0.007
			TurnEntity(em\OBJ, -90.0, 0.0, 0.0)
			EntityParent(em\OBJ, r\OBJ)
			
			it.Items = CreateItem("Dr. L's Note #2", "paper", r\x - 160.0 * RoomScale, r\y + 32.0 * RoomScale, r\z - 353.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "dimension1499"
			;[Block]
			r\Levers[0] = CreatePivot()
			PositionEntity(r\Levers[0], r\x + 205.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 2287.0 * RoomScale)
			EntityParent(r\Levers[0], r\OBJ)
			
			r\Levers[1] = LoadMesh_Strict("GFX\map\dimension1499\1499object0_cull.b3d", r\OBJ)
			EntityType(r\Levers[1], HIT_MAP)
			EntityAlpha(r\Levers[1], 0.0)
			;[End Block]
		Case "room4info"
			;[Block]
			r\Objects[0] = CopyEntity(o\MonitorModelID[1], r\OBJ)
			PositionEntity(r\Objects[0], r\x - 700.0 * RoomScale, r\y + 384.0 * RoomScale, r\z + 290.0 * RoomScale, True)
			ScaleEntity(r\Objects[0], 2.0, 2.0, 2.0)
			RotateEntity(r\Objects[0], 0.0, 0.0, 0.0)
			EntityFX(r\Objects[0], 1)
			;[End Block]
		Case "room2bio"
			;[Block]	
			r\Objects[0] = LoadMesh_Strict("GFX\map\room2bio_terrain.b3d")
			ScaleEntity(r\Objects[0], RoomScale, RoomScale, RoomScale)
			RotateEntity(r\Objects[0], 0.0, r\Angle, 0.0)
			PositionEntity(r\Objects[0], r\x, r\y - 1.0 * RoomScale, r\z)	
			EntityParent(r\Objects[0], r\OBJ)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 475.0 * RoomScale, r\y + 385.0 * RoomScale, r\z + 305.0 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 30.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			;[End Block]
		Case "room2offices5"
			;[Block]
			r\Objects[0] = LoadMesh_Strict("GFX\map\room2offices5_hb.b3d", r\OBJ)
			EntityPickMode(r\Objects[0], 2)
			EntityType(r\Objects[0], HIT_MAP)
			EntityAlpha(r\Objects[0], 0.0)
			
			sc.SecurityCams = CreateSecurityCam(r\x - 475.0 * RoomScale, r\y + 385.0 * RoomScale, r\z + 305.0 * RoomScale, r)
			sc\Angle = 225.0 : sc\Turn = 30.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 360.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 960.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ) 
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("9V Battery", "bat", r\x + 435.0 * RoomScale, r\y + 230.0 * RoomScale, r\z + 960.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ) 
			EndIf
			;[End Block]
		Case "roomo5"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z - 240.0 * RoomScale, 0.0, r, False, Default_Door, 0, "2411")
			d\AutoClose = False
			
			it.Items = CreateItem("Field Agent Log #235-001-CO5", "paper", r\x, r\y + 200.0 * RoomScale, r\z + 870.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Groups of Interest Log", "paper", r\x + 100.0 * RoomScale, r\y + 200.0 * RoomScale, r\z + 100.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x + 680.0 * RoomScale, r\y + 260.0 * RoomScale, r\z + 892.5 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x - 700.0 * RoomScale, r\y + 210.0 * RoomScale, r\z + 920.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room096"
			;[Block]
			d.Doors = CreateDoor(r\x - 320.0 * RoomScale, r\y, r\z + 320.0 * RoomScale, 90.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x, r\y, r\z, 0.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			d.Doors = CreateDoor(r\x - 385.0 * RoomScale, r\y, r\z - 512.0 * RoomScale, 90.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			de.Decals = CreateDecal(3, r\x - 477.0 * RoomScale, r\y + 0.005, r\z - 710.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Data Report", "paper", r\x - 477.0 * RoomScale, r\y + 90.0 * RoomScale, r\z - 710.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x - 514.0 * RoomScale, r\y + 150.0 * RoomScale, r\z + 63.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Document SCP-096", "paper", r\x - 500.0 * RoomScale, r\y + 220.0 * RoomScale, r\z + 63.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 90.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("SCRAMBLE Gear", "scramble", r\x - 860.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 80.0 * RoomScale)
			it\State = Rnd(1000.0)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room409"
			;[Block]
			; ~ Elevators' doors
			r\RoomDoors.Doors[0] = CreateDoor(r\x + 264.0 * RoomScale, r\y, r\z + 655.0 * RoomScale, -90.0, r, True, Elevator_Door)
			r\RoomDoors[0]\AutoClose = False
			PositionEntity(r\RoomDoors[0]\Buttons[0], EntityX(r\RoomDoors[0]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[0]\Buttons[0], True), EntityZ(r\RoomDoors[0]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[0]\Buttons[1], EntityX(r\RoomDoors[0]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[0]\Buttons[1], True), EntityZ(r\RoomDoors[0]\Buttons[1], True), True)							
			
			r\RoomDoors.Doors[1] = CreateDoor(r\x - 2328.0 * RoomScale, r\y - 4256.0 * RoomScale, r\z - 648.0 * RoomScale, -90.0, r, False, Elevator_Door)
			r\RoomDoors[1]\AutoClose = False
			PositionEntity(r\RoomDoors[1]\Buttons[0], EntityX(r\RoomDoors[1]\Buttons[0], True) - 0.031, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[0], True), True)
			PositionEntity(r\RoomDoors[1]\Buttons[1], EntityX(r\RoomDoors[1]\Buttons[1], True) + 0.031, EntityY(r\RoomDoors[1]\Buttons[1], True), EntityZ(r\RoomDoors[1]\Buttons[1], True), True)					
			
			; ~ A door to the containment chamber	
			r\RoomDoors.Doors[2] = CreateDoor(r\x - 4336.0 * RoomScale, r\y - 4256.0 * RoomScale, r\z + 1560.0 * RoomScale, 0.0, r, False, Default_Door, 4)
			r\RoomDoors[2]\AutoClose = False
			PositionEntity(r\RoomDoors[2]\Buttons[0], EntityX(r\RoomDoors[2]\Buttons[0], True), EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[0], True) + 0.061, True)
			PositionEntity(r\RoomDoors[2]\Buttons[1], EntityX(r\RoomDoors[2]\Buttons[1], True), EntityY(r\RoomDoors[2]\Buttons[1], True), EntityZ(r\RoomDoors[2]\Buttons[1], True) - 0.061, True)					
			
			; ~ Elevator pivots
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x + 552.0 * RoomScale, r\y + 240.0 * RoomScale, r\z + 656.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x - 2040.0 * RoomScale, r\y - 4011.0 * RoomScale, r\z - 648.0 * RoomScale)
			
			; ~ Class-D spawn
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x - 4951.0 * RoomScale, r\y - 4491.0 * RoomScale, r\z + 1836.0 * RoomScale)
			
			; ~ Touching pivot
			r\Objects[3] = CreatePivot()
			PositionEntity(r\Objects[3], r\x - 4885.0 * RoomScale, r\y - 4326.0 * RoomScale, r\z + 2243.0 * RoomScale)
			
			; ~ Sparks pivot
			r\Objects[4] = CreatePivot()
			PositionEntity(r\Objects[4], r\x - 4468.0 * RoomScale, r\y - 4066.0 * RoomScale, r\z - 1937.0 * RoomScale)
			
			For i = 0 To 4
				EntityParent(r\Objects[i], r\OBJ)
			Next
			
			sc.SecurityCams = CreateSecurityCam(r\x - 3624.0 * RoomScale, r\y - 3840.0 * RoomScale, r\z + 2256.0 * RoomScale, r)
			sc\Angle = 100.0 : sc\Turn = 45.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			de.Decals = CreateDecal(19, r\x - 4951.0 * RoomScale, r\y - 4495.0 * RoomScale, r\z + 1700.0 * RoomScale, 90.0, Rnd(360.0), 0.0, 0.5, 0.8)
			EntityParent(de\OBJ, r\OBJ)
			
			it.Items = CreateItem("Document SCP-409", "paper", r\x - 3595.0 * RoomScale, r\y - 4336.0 * RoomScale, r\z + 2242.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			If I_005\ChanceToSpawn = 3 Then
				it.Items = CreateItem("SCP-005", "scp005", r\x - 5050.0 * RoomScale, r\y - 4416.0 * RoomScale, r\z + 1728.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)	
			EndIf
			;[End Block]
		Case "room2posters"
			;[Block]
			d.Doors = CreateDoor(r\x + 272.0 * RoomScale, r\y, r\z + 576.0 * RoomScale, 90.0, r, False, Default_Door, 1, "", True)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) + 0.061, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) - 0.061, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			sc.SecurityCams = CreateSecurityCam(r\x + 980.0 * RoomScale, r\y + 515.0 * RoomScale, r\z + 100.0 * RoomScale, r)
			sc\Angle = 30.0 : sc\Turn = 30.0
			TurnEntity(sc\CameraOBJ, 20.0, 0.0, 0.0)
			
			it.Items = CreateItem("Level 1 Key Card", "key1", r\x + 468.0 * RoomScale, r\y + 160.0 * RoomScale, r\z + 980.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Origami", "origami", r\x + 460.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 80.0 * RoomScale)
			RotateEntity(it\Collider, 0.0, 0.0, 0.0)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("9V Battery", "bat", r\x + 900.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 80.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			it.Items = CreateItem("Ballistic Helmet", "helmet", r\x + 980.0 * RoomScale, r\y + 250.0 * RoomScale, r\z + 300.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			;[End Block]
		Case "room2medibay", "room2medibay2"
			;[Block]
			d.Doors = CreateDoor(r\x - 264.0 * RoomScale, r\y, r\z + 640.0 * RoomScale, 90.0, r, False, Default_Door, 3)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True) - 0.031, EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True), True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True) + 0.031, EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True), True)
			
			For r2.Rooms = Each Rooms
				If r2 <> r Then
					If r2\RoomTemplate\Name = "room2medibay" Lor r2\RoomTemplate\Name = "room2medibay2" Then
						r\Objects[0] = CopyEntity(r2\Objects[0], r\OBJ) ; ~ Don't load the mesh again
						Exit
					EndIf
				EndIf
			Next
			If (Not r\Objects[0]) Then r\Objects[0] = LoadMesh_Strict("GFX\map\medibay_props.b3d", r\OBJ)
			EntityType(r\Objects[0], HIT_MAP)
			EntityPickMode(r\Objects[0], 2)
			
			If r\RoomTemplate\Name = "room2medibay" Then
				r\Objects[1] = CopyEntity(o\NPCModelID[NPCTypeDuck])
				Tex = LoadTexture_Strict("GFX\npcs\duck(4).png")
				EntityTexture(r\Objects[1], Tex)
				DeleteSingleTextureEntryFromCache(Tex)
				ScaleEntity(r\Objects[1], 0.07, 0.07, 0.07)
				PositionEntity(r\Objects[1], r\x - 910.0 * RoomScale, r\y + 144.0 * RoomScale, r\z - 778.0 * RoomScale)				
				TurnEntity(r\Objects[1], 6.0, 180.0, 0.0)
				EntityParent(r\Objects[1], r\OBJ)
			Else
				r\Objects[1] = CreatePivot()
				PositionEntity(r\Objects[1], r\x - 820.0 * RoomScale, r\y, r\z - 318.0 * RoomScale)
				EntityParent(r\Objects[1], r\OBJ)
			EndIf
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("Syringe", "syringe", r\x - 340.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 52.3 * RoomScale)
				RotateEntity(it\Collider, 0.0, Rnd(100.0, 110.0), 0.0)
				EntityParent(it\Collider, r\OBJ)
			Else
				it.Items = CreateItem("Syringe", "syringeinf", r\x - 340.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 52.3 * RoomScale)
				RotateEntity(it\Collider, 0.0, Rnd(100.0, 110.0), 0.0)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			If Rand(2) = 1 Then
				it.Items = CreateItem("Syringe", "syringe", r\x - 340.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 97.3 * RoomScale)
				RotateEntity(it\Collider, 0.0, Rnd(250.0, 260.0), 0.0)
				EntityParent(it\Collider, r\OBJ)
			Else
				it.Items = CreateItem("Syringe", "syringeinf", r\x - 340.0 * RoomScale, r\y + 100.0 * RoomScale, r\z + 97.3 * RoomScale)
				RotateEntity(it\Collider, 0.0, Rnd(250.0, 260.0), 0.0)
				EntityParent(it\Collider, r\OBJ)
			EndIf
			
			it.Items = CreateItem("First Aid Kit", "firstaid", r\x - 506.0 * RoomScale, r\y + 192.0 * RoomScale, r\z - 322.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)	
			;[End Block]
		Case "room005"
			;[Block]
			d.Doors = CreateDoor(r\x, r\y, r\z - 672.0 * RoomScale, 0.0, r, False, Default_Door, 4)
			d\AutoClose = False
			PositionEntity(d\Buttons[0], EntityX(d\Buttons[0], True), EntityY(d\Buttons[0], True), EntityZ(d\Buttons[0], True) + 0.061, True)
			PositionEntity(d\Buttons[1], EntityX(d\Buttons[1], True), EntityY(d\Buttons[1], True), EntityZ(d\Buttons[1], True) - 0.061, True)
			
			r\Objects[0] = CreatePivot()
			PositionEntity(r\Objects[0], r\x, r\y + 76.0 * RoomScale, r\z - 260.0 * RoomScale)
			
			r\Objects[1] = CreatePivot()
			PositionEntity(r\Objects[1], r\x, r\y + 188.0 * RoomScale, r\z - 25.0 * RoomScale)			
			
			r\Objects[2] = CreatePivot()
			PositionEntity(r\Objects[2], r\x, r\y + 12.0 * RoomScale, r\z + 55.0 * RoomScale)
			
			For i = 0 To 2
				EntityParent(r\Objects[i], r\OBJ)
			Next			
			
			sc.SecurityCams = CreateSecurityCam(r\x, r\y + 415.0 * RoomScale, r\z - 572.0 * RoomScale, r)
			sc\Angle = 0.0 : sc\Turn = 30.0
			TurnEntity(sc\CameraOBJ, 30.0, 0.0, 0.0)
			
			it.Items = CreateItem("Document SCP-005", "paper", r\x + 338.0 * RoomScale, r\y + 152.0 * RoomScale, r\z - 500.0 * RoomScale)
			EntityParent(it\Collider, r\OBJ)
			
			If I_005\ChanceToSpawn = 1 Then
				it.Items = CreateItem("SCP-005", "scp005", r\x, r\y + 254.0 * RoomScale, r\z - 260.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)
			ElseIf I_005\ChanceToSpawn = 2
				it.Items = CreateItem("Note from Maynard", "paper", r\x, r\y + 254.0 * RoomScale, r\z - 260.0 * RoomScale)
				EntityParent(it\Collider, r\OBJ)	
			EndIf
			;[End Block]
	End Select
	
	For lt.LightTemplates = Each LightTemplates
		If lt\RoomTemplate = r\RoomTemplate Then
			Local NewLight% = AddLight(r, r\x + lt\x, r\y + lt\y, r\z + lt\z, lt\lType, lt\Range, lt\R, lt\G, lt\B)
			
			If NewLight <> 0 Then 
				If lt\lType = 3 Then
					RotateEntity(NewLight, lt\Pitch, lt\Yaw, 0.0)
				EndIf
			EndIf
		EndIf
	Next
	
	For ts.TempScreens = Each TempScreens
		If ts\RoomTemplate = r\RoomTemplate Then
			CreateScreen(r\x + ts\x, r\y + ts\y, r\z + ts\z, ts\ImgPath, r)
		EndIf
	Next
	
	For tw.TempWayPoints = Each TempWayPoints
		If tw\roomtemplate = r\RoomTemplate Then
			CreateWaypoint(r\x + tw\x, r\y + tw\y, r\z + tw\z, Null, r)
		EndIf
	Next
	
	If r\RoomTemplate\TempTriggerBoxAmount > 0 Then
		r\TriggerBoxAmount = r\RoomTemplate\TempTriggerBoxAmount
		For i = 0 To r\TriggerBoxAmount - 1
			r\TriggerBoxes[i] = New TriggerBox
			r\TriggerBoxes[i]\OBJ = CopyEntity(r\RoomTemplate\TempTriggerBox[i], r\OBJ)
			EntityColor(r\TriggerBoxes[i]\OBJ, 255, 255, 0)
			EntityAlpha(r\TriggerBoxes[i]\OBJ, 0.0)
			r\TriggerBoxes[i]\Name = r\RoomTemplate\TempTriggerBoxName[i]
		Next
	EndIf
	
	For i = 0 To MaxRoomEmitters - 1
		If r\RoomTemplate\TempSoundEmitter[i] <> 0 Then
			r\SoundEmitterOBJ[i] = CreatePivot(r\OBJ)
			PositionEntity(r\SoundEmitterOBJ[i], r\x + r\RoomTemplate\TempSoundEmitterX[i], r\y + r\RoomTemplate\TempSoundEmitterY[i], r\z + r\RoomTemplate\TempSoundEmitterZ[i], True)
			EntityParent(r\SoundEmitterOBJ[i], r\OBJ)
			
			r\SoundEmitter[i] = r\RoomTemplate\TempSoundEmitter[i]
			r\SoundEmitterRange[i] = r\RoomTemplate\TempSoundEmitterRange[i]
		EndIf
	Next
	
	CatchErrors("FillRoom (" + r\RoomTemplate\Name + ")")
End Function

Function UpdateRooms()
	CatchErrors("Uncaught (UpdateRooms)")
	
	Local Dist#, i%, j%, r.Rooms
	Local x#, z#, Hide% = True
	
	; ~ The reason why it is like this:
	; ~ When the map gets spawned by a seed, it starts from LCZ to HCZ to EZ (bottom to top)
	; ~ A map loaded by the map creator starts from EZ to HCZ to LCZ (top to bottom) and that's why this little code thing with the (SelectedMap = "") needs to be there - ENDSHN
	If (EntityZ(me\Collider) / 8.0) < I_Zone\Transition[1] - (SelectedMap = "") Then
		me\Zone = 2
	ElseIf (EntityZ(me\Collider) / 8.0) >= I_Zone\Transition[1] - (SelectedMap = "") And (EntityZ(me\Collider) / 8.0) < I_Zone\Transition[0] - (SelectedMap = "") Then
		me\Zone = 1
	Else
		me\Zone = 0
	EndIf
	
	TempLightVolume = 0.0
	
	Local FoundNewPlayerRoom% = False
	
	If PlayerRoom <> Null Then
		If Abs(EntityY(me\Collider) - EntityY(PlayerRoom\OBJ)) < 1.5 Then
			x = Abs(PlayerRoom\x - EntityX(me\Collider, True))
			If x < 4.0 Then
				z = Abs(PlayerRoom\z - EntityZ(me\Collider, True))
				If z < 4.0 Then
					FoundNewPlayerRoom = True
				EndIf
			EndIf
			
			If (Not FoundNewPlayerRoom) Then ; ~ It's likely that an adjacent room is the new player room, check for that
				For i = 0 To 3
					If PlayerRoom\Adjacent[i] <> Null Then
						x = Abs(PlayerRoom\Adjacent[i]\x - EntityX(me\Collider, True))
						If x < 4.0 Then
							z = Abs(PlayerRoom\Adjacent[i]\z - EntityZ(me\Collider, True))
							If z < 4.0 Then
								FoundNewPlayerRoom = True
								PlayerRoom = PlayerRoom\Adjacent[i]
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		Else
			FoundNewPlayerRoom = True ; ~ PlayerRoom stays the same when you're high up, or deep down
		EndIf
	EndIf
	
	For r.Rooms = Each Rooms
		x = Abs(r\x - EntityX(me\Collider, True))
		z = Abs(r\z - EntityZ(me\Collider, True))
		r\Dist = Max(x, z)
		
		If x < 16 And z < 16 Then
			For i = 0 To MaxRoomEmitters - 1
				If r\SoundEmitter[i] <> 0 Then 
					If EntityDistanceSquared(r\SoundEmitterOBJ[i], me\Collider) < PowTwo(r\SoundEmitterRange[i]) Then
						r\SoundEmitterCHN[i] = LoopSound2(RoomAmbience[r\SoundEmitter[i] - 1], r\SoundEmitterCHN[i], Camera, r\SoundEmitterOBJ[i], r\SoundEmitterRange[i])
					EndIf
				EndIf
			Next
			
			If (Not FoundNewPlayerRoom) And PlayerRoom <> r Then				
				If x < 4.0 Then
					If z < 4.0 Then
						If Abs(EntityY(me\Collider) - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
						FoundNewPlayerRoom = True
					EndIf
				EndIf				
			EndIf
		EndIf
		
		Hide = True
		
		If r = PlayerRoom Then Hide = False
		If Hide Then
			If IsRoomAdjacent(PlayerRoom, r) Then Hide = False
		EndIf
		If Hide Then
			For i = 0 To 3
				If IsRoomAdjacent(PlayerRoom\Adjacent[i], r) Then
					Hide = False
					Exit
				EndIf
			Next
		EndIf
		
		If Hide Then
			HideEntity(r\OBJ)
		Else
			ShowEntity(r\OBJ)
			For i = 0 To MaxRoomLights - 1
				If r\Lights[i] <> 0 Then
					Dist = EntityDistanceSquared(me\Collider, r\Lights[i])
					If Dist < PowTwo(HideDistance) Then
						TempLightVolume = TempLightVolume + r\LightIntensity[i] * r\LightIntensity[i] * ((HideDistance - Sqr(Dist)) / HideDistance)						
					EndIf
				Else
					Exit
				EndIf
			Next
			If chs\DebugHUD <> 0 Then
				If r\TriggerBoxAmount > 0 Then
					For i = 0 To r\TriggerBoxAmount - 1
						EntityColor(r\TriggerBoxes[i]\OBJ, 255, 255, 0)
						EntityAlpha(r\TriggerBoxes[i]\OBJ, 0.2)
					Next
				EndIf
			Else
				If r\TriggerBoxAmount > 0 Then
					For i = 0 To r\TriggerBoxAmount - 1
						EntityColor(r\TriggerBoxes[i]\OBJ, 255, 255, 255)
						EntityAlpha(r\TriggerBoxes[i]\OBJ, 0.0)
					Next
				EndIf
			EndIf
		EndIf
	Next
	
	CurrMapGrid\Found[Floor(EntityX(PlayerRoom\OBJ) / 8.0) + (Floor(EntityZ(PlayerRoom\OBJ) / 8.0) * MapGridSize)] = 1
	PlayerRoom\Found = True
	
	TempLightVolume = Max(TempLightVolume / 5.0, 0.8)
	
	If PlayerRoom <> Null Then
		EntityAlpha(GetChild(PlayerRoom\OBJ, 2), 1.0)
		For i = 0 To 3
			If PlayerRoom\Adjacent[i] <> Null Then
				If PlayerRoom\AdjDoor[i] <> Null
					x = Abs(EntityX(me\Collider, True) - EntityX(PlayerRoom\AdjDoor[i]\FrameOBJ, True))
					z = Abs(EntityZ(me\Collider, True) - EntityZ(PlayerRoom\AdjDoor[i]\FrameOBJ, True))
					If PlayerRoom\AdjDoor[i]\OpenState = 0.0 Then
						EntityAlpha(GetChild(PlayerRoom\Adjacent[i]\OBJ, 2), 0.0)
					ElseIf (Not EntityInView(PlayerRoom\AdjDoor[i]\FrameOBJ, Camera))
						EntityAlpha(GetChild(PlayerRoom\Adjacent[i]\OBJ, 2), 0.0)
					Else
						EntityAlpha(GetChild(PlayerRoom\Adjacent[i]\OBJ, 2), 1.0)
					EndIf
				EndIf
				
				For j = 0 To 3
					If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null Then
						If PlayerRoom\Adjacent[i]\Adjacent[j] <> PlayerRoom Then EntityAlpha(GetChild(PlayerRoom\Adjacent[i]\Adjacent[j]\OBJ, 2), 0.0)
					EndIf
				Next
			EndIf
		Next
	EndIf
	
	CatchErrors("UpdateRooms")
End Function

Function IsRoomAdjacent(this.Rooms, that.Rooms)
	Local i%
	
	If this = Null Then Return(False)
	If this = that Then Return(True)
	For i = 0 To 3
		If that = this\Adjacent[i] Then Return(True)
	Next
	Return(False)
End Function

Dim MapRoom$(ROOM4 + 1, 0)

Function SetRoom%(RoomName$, RoomType%, RoomPosition%, MinPos%, MaxPos%) ; ~ Place a room without overwriting others
	Local Looped%, CanPlace%
	
	Looped = False
	CanPlace = True
	While MapRoom(RoomType, RoomPosition) <> ""
		RoomPosition = RoomPosition + 1
		If RoomPosition > MaxPos Then
			If (Not Looped) Then
				RoomPosition = MinPos + 1 : Looped = True
			Else
				CanPlace = False
				Exit
			EndIf
		EndIf
	Wend
	If CanPlace Then
		MapRoom(RoomType, RoomPosition) = RoomName
		Return(True)
	Else
		Return(False)
	EndIf
End Function

Function PreventRoomOverlap(r.Rooms)
	If r\RoomTemplate\DisableOverlapCheck Then Return
	
	Local r2.Rooms, r3.Rooms
	Local IsIntersecting% = False
	
	; ~ Just skip it when it would try to check for the checkpoints
	If r\RoomTemplate\Name = "room2checkpoint" Lor r\RoomTemplate\Name = "room2checkpoint2" Lor r\RoomTemplate\Name = "room173" Then Return(True)
	
	; ~ First, check if the room is actually intersecting at all
	For r2.Rooms = Each Rooms
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck) Then
			If CheckRoomOverlap(r, r2) Then
				IsIntersecting = True
				Exit
			EndIf
		EndIf
	Next
	
	; ~ If not, then simply return it as True
	If (Not IsIntersecting) Then Return(True)
	
	; ~ Room is interseting: First, check if the given room is a ROOM2, so we could potentially just turn it by 180.0 degrees
	IsIntersecting = False
	
	Local x% = r\x / 8.0
	Local y% = r\z / 8.0
	
	If r\RoomTemplate\Shape = ROOM2 Then
		; ~ Room is a ROOM2, let's check if turning it 180.0 degrees fixes the overlapping issue
		r\Angle = r\Angle + 180.0
		RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
		CalculateRoomExtents(r)
		
		For r2.Rooms = Each Rooms
			If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck) Then
				If CheckRoomOverlap(r, r2) Then
					; ~ If didn't work then rotate the room back and move to the next step
					IsIntersecting = True
					r\Angle = r\Angle - 180.0
					RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
					CalculateRoomExtents(r)
					Exit
				EndIf
			EndIf
		Next
	Else
		IsIntersecting = True
	EndIf
	
	; ~ Room is ROOM2 and was able to be turned by 180.0 degrees
	If (Not IsIntersecting) Then Return(True)
	
	; ~ Room is either not a ROOM2 or the ROOM2 is still intersecting, now trying to swap the room with another of the same type
	IsIntersecting = True
	
	Local x2%, y2%, Rot%, Rot2%
	
	For r2.Rooms = Each Rooms
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck) Then
			If r\RoomTemplate\Shape = r2\RoomTemplate\Shape And r\Zone = r2\Zone And (r2\RoomTemplate\Name <> "room2checkpoint" And r2\RoomTemplate\Name <> "room2checkpoint2" And r2\RoomTemplate\Name <> "room173") Then
				x = r\x / 8.0
				y = r\z / 8.0
				Rot = r\Angle
				
				x2 = r2\x / 8.0
				y2 = r2\z / 8.0
				Rot2 = r2\Angle
				
				IsIntersecting = False
				
				r\x = x2 * 8.0
				r\z = y2 * 8.0
				r\Angle = Rot2
				PositionEntity(r\OBJ, r\x, r\y, r\z)
				RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
				CalculateRoomExtents(r)
				
				r2\x = x * 8.0
				r2\z = y * 8.0
				r2\Angle = Rot
				PositionEntity(r2\OBJ, r2\x, r2\y, r2\z)
				RotateEntity(r2\OBJ, 0.0, r2\Angle, 0.0)
				CalculateRoomExtents(r2)
				
				; ~ Make sure neither room overlaps with anything after the swap
				For r3.Rooms = Each Rooms
					If (Not r3\RoomTemplate\DisableOverlapCheck) Then
						If r3 <> r Then
							If CheckRoomOverlap(r, r3) Then
								IsIntersecting = True
								Exit
							EndIf
						EndIf
						If r3 <> r2 Then
							If CheckRoomOverlap(r2, r3) Then
								IsIntersecting = True
								Exit
							EndIf
						EndIf	
					EndIf
				Next
				
				; ~ Either the original room or the "reposition" room is intersecting, reset the position of each room to their original one
				If IsIntersecting Then
					r\x = x * 8.0
					r\z = y * 8.0
					r\Angle = Rot
					PositionEntity(r\OBJ, r\x, r\y, r\z)
					RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
					CalculateRoomExtents(r)
					
					r2\x = x2 * 8.0
					r2\z = y2 * 8.0
					r2\Angle = Rot2
					PositionEntity(r2\OBJ, r2\x, r2\y, r2\z)
					RotateEntity(r2\OBJ, 0.0, r2\Angle, 0.0)
					CalculateRoomExtents(r2)
					
					IsIntersecting = False
				EndIf
			EndIf
		EndIf
	Next
	
	; ~ Room was able to the placed in a different spot
	If (Not IsIntersecting) Then Return(True)
	
	Return(False)
End Function

Const MapGridSize% = 18
Const RoomSpacing# = 8.0

Type MapGrid
	Field Grid%[(MapGridSize + 1) ^ 2]
	Field Found%[(MapGridSize + 1) ^ 2]
	Field RoomName$[MapGridSize ^ 2]
	Field RoomID%[ROOM4 + 1]
End Type

Global CurrMapGrid.MapGrid

; ~ Map Grid Tile IDs Constants
;[Block]
Const MapGrid_NoTile% = 0
Const MapGrid_Tile% = 1
Const MapGrid_CheckpointTile% = 255
;[End Block]

Function CreateMap()
	Local r.Rooms, r2.Rooms, d.Doors
	Local x%, y%, Temp%, Temp2
	Local i%, x2%, y2%
	Local Width%, Height%, TempHeight%, yHallways%
	Local ShouldSpawnDoor%, Zone%
	
	I_Zone\Transition[0] = 13
	I_Zone\Transition[1] = 7
	I_Zone\HasCustomForest = False
	I_Zone\HasCustomMT = False
	
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	If CurrMapGrid <> Null Then
		Delete(CurrMapGrid) : CurrMapGrid = Null
	EndIf
	CurrMapGrid = New MapGrid
	
	x = Floor(MapGridSize / 2)
	y = MapGridSize - 2
	
	For i = y To MapGridSize - 1
		CurrMapGrid\Grid[x + (i * MapGridSize)] = MapGrid_Tile
	Next
	
	Repeat
		Width = Rand(10, 15)
		
		If x > MapGridSize * 0.6 Then
			Width = -Width
		ElseIf x > MapGridSize * 0.4
			x = x - (Width / 2)
		EndIf
		
		; ~ Make sure the hallway doesn't go outside the array
		If x + Width > MapGridSize - 3 Then
			Width = MapGridSize - 3 - x
		ElseIf x + Width < 2
			Width = (-x) + 2
		EndIf
		
		x = Min(x, x + Width)
		Width = Abs(Width)
		For i = x To x + Width
			CurrMapGrid\Grid[Min(i, MapGridSize) + (y * MapGridSize)] = MapGrid_Tile
		Next
		
		Height = Rand(3, 4)
		If y - Height < 1 Then Height = y - 1
		
		yHallways = Rand(4, 5)
		
		If GetZone(y - Height) <> GetZone(y - Height + 1) Then Height = Height - 1
		
		For i = 1 To yHallways
			x2 = Max(Min(Rand(x, x + Width - 1), MapGridSize - 2), 2)
			While CurrMapGrid\Grid[x2 + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x2 - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x2 + 1) + ((y - 1) * MapGridSize)]
				x2 = x2 + 1
			Wend
			
			If x2 < x + Width Then
				If i = 1 Then
					TempHeight = Height 
					If Rand(2) = 1 Then
						x2 = x
					Else
						x2 = x + Width
					EndIf
				Else
					TempHeight = Rand(1, Height)
				EndIf
				
				For y2 = y - TempHeight To y
					If GetZone(y2) <> GetZone(y2 + 1) Then ; ~ A room leading from zone to another
						CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = MapGrid_CheckpointTile
					Else
						CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = MapGrid_Tile
					EndIf
				Next
				
				If TempHeight = Height Then Temp = x2
			EndIf
		Next
		x = Temp
		y = y - Height
	Until y < 2
	
	Local Room1Amount%[3], Room2Amount%[3], Room2CAmount%[3], Room3Amount%[3], Room4Amount%[3]
	
	; ~ Count the amount of rooms
	For y = 1 To MapGridSize - 1
		Zone = GetZone(y)
		For x = 1 To MapGridSize - 1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile Then
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0)			
				If CurrMapGrid\Grid[x + (y * MapGridSize)] <> MapGrid_CheckpointTile Then CurrMapGrid\Grid[x + (y * MapGridSize)] = Temp
				Select CurrMapGrid\Grid[x + (y * MapGridSize)]
					Case 1
						;[Block]
						Room1Amount[Zone] = Room1Amount[Zone] + 1
						;[End Block]
					Case 2
						;[Block]
						If Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) = 2 Then
							Room2Amount[Zone] = Room2Amount[Zone] + 1	
						ElseIf Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0) = 2
							Room2Amount[Zone] = Room2Amount[Zone] + 1	
						Else
							Room2CAmount[Zone] = Room2CAmount[Zone] + 1
						EndIf
						;[End Block]
					Case 3
						;[Block]
						Room3Amount[Zone] = Room3Amount[Zone] + 1
						;[End Block]
					Case 4
						;[Block]
						Room4Amount[Zone] = Room4Amount[Zone] + 1
						;[End Block]
				End Select
			EndIf
		Next
	Next		
	
	Local Placed%
	
	; ~ Force more room1s (if needed)
	For i = 0 To 2
		; ~ Need more rooms if there are less than 5 of them
		Temp = (-Room1Amount[i]) + 5
		If Temp > 0 Then
			For y = (MapGridSize / ZONEAMOUNT) * (2 - i) + 1 To ((MapGridSize / ZONEAMOUNT) * ((2 - i) + 1.0)) - 2			
				For x = 2 To MapGridSize - 2
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile Then
						If (Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1.0)) = 1 Then
							If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] Then
								x2 = x + 1 : y2 = y
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
								x2 = x - 1 : y2 = y
							ElseIf CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)]
								x2 = x : y2 = y + 1	
							ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)]
								x2 = x : y2 = y - 1
							EndIf
							
							Placed = False
							If CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] > 1 And CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] < 4 Then 
								Select CurrMapGrid\Grid[x2 + (y2 * MapGridSize)]
									Case 2
										;[Block]
										If Min(CurrMapGrid\Grid[(x2 + 1) + (y2 * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[(x2 - 1) + (y2 * MapGridSize)], 1.0) = 2 Then
											Room2Amount[i] = Room2Amount[i] - 1
											Room3Amount[i] = Room3Amount[i] + 1
											Placed = True
										ElseIf Min(CurrMapGrid\Grid[x2 + ((y2 + 1) * MapGridSize)], 1.0) + Min(CurrMapGrid\Grid[x2 + ((y2 - 1) * MapGridSize)], 1.0) = 2
											Room2Amount[i] = Room2Amount[i] - 1
											Room3Amount[i] = Room3Amount[i] + 1
											Placed = True
										EndIf
										;[End Block]
									Case 3
										;[Block]
										Room3Amount[i] = Room3Amount[i] - 1
										Room4Amount[i] = Room4Amount[i] + 1	
										Placed = True
										;[End Block]
								End Select
								
								If Placed Then
									CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] + 1
									
									CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile
									Room1Amount[i] = Room1Amount[i] + 1	
									
									Temp = Temp - 1
								EndIf
							EndIf
						EndIf
					EndIf
					If Temp = 0 Then Exit
				Next
				If Temp = 0 Then Exit
			Next
		EndIf
	Next
	
	; ~ Force more ROOM4 and ROOM2C
	For i = 0 To 2
		Select i
			Case 2
				;[Block]
				Zone = 2
				Temp2 = MapGridSize / 3
				;[End Block]
			Case 1
				;[Block]
				Zone = (MapGridSize / 3) + 1
				Temp2 = (MapGridSize * (2.0 / 3.0)) - 1
				;[End Block]
			Case 0
				;[Block]
				Zone = (MapGridSize * (2.0 / 3.0)) + 1
				Temp2 = MapGridSize - 2
				;[End Block]
		End Select
		
		If Room4Amount[i] < 1 Then ; ~ We want at least one ROOM4
			Temp = 0
			For y = Zone To Temp2
				For x = 2 To MapGridSize - 2
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = 3 Then
						Select False ; ~ See if adding a ROOM1 is possible
							Case (CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)])
								;[Block]
								CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)])
								;[Block]
								CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)])
								;[Block]
								CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)])
								;[Block]
								CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
						End Select
						If Temp = 1 Then
							CurrMapGrid\Grid[x + (y * MapGridSize)] = 4 ; ~ Turn this room into a ROOM4
							Room4Amount[i] = Room4Amount[i] + 1
							Room3Amount[i] = Room3Amount[i] - 1
							Room1Amount[i] = Room1Amount[i] + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
		
		If Room2CAmount[i] < 1 Then ; ~ We want at least one ROOM2C
			Temp = 0
			Zone = Zone + 1
			Temp2 = Temp2 - 1
			For y = Zone To Temp2
				For x = 3 To MapGridSize - 3
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile Then
						Select True ; ~ See if adding some rooms is possible
							Case CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)]) = 0 Then
									If (CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)]) = 0 Then
									If (CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] + CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)]) = 0 Then
									If (CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] + CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)]) = 0 Then
									If (CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)]) = 0 Then
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
						End Select
						If Temp = 1 Then
							Room2CAmount[i] = Room2CAmount[i] + 1
							Room2Amount[i] = Room2Amount[i] + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
	Next
	
	Local MaxRooms% = 55 * MapGridSize / 20
	
	MaxRooms = Max(MaxRooms, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room2Amount[0] + Room2Amount[1] + Room2Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room2CAmount[0] + Room2CAmount[1] + Room2CAmount[2] + 1)
	MaxRooms = Max(MaxRooms, Room3Amount[0] + Room3Amount[1] + Room3Amount[2] + 1)
	MaxRooms = Max(MaxRooms, Room4Amount[0] + Room4Amount[1] + Room4Amount[2] + 1)
	
	Dim MapRoom$(ROOM4 + 1, MaxRooms)
	
	; ~ [LIGHT CONTAINMENT ZONE]
	
	Local MinPos% = 1, MaxPos% = Room1Amount[0] - 1
	
	MapRoom(ROOM1, 0) = "room173"
	
	SetRoom("room372", ROOM1, Floor(0.1 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("room005", ROOM1, Floor(0.3 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("room914", ROOM1, Floor(0.35 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("room205", ROOM1, Floor(0.5 * Float(Room1Amount[0])), MinPos, MaxPos)
	SetRoom("room1archive", ROOM1, Floor(0.6 * Float(Room1Amount[0])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, 0) = "room2clockroom"
	
	MinPos = 1
	MaxPos = Room2Amount[0] - 1
	
	MapRoom(ROOM2, 0) = "room2closets" 
	
	SetRoom("room2testroom", ROOM2, Floor(0.1 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2scps", ROOM2, Floor(0.2 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2storage", ROOM2, Floor(0.3 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2gw_b", ROOM2, Floor(0.4 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2sl", ROOM2, Floor(0.5 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room012", ROOM2, Floor(0.55 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2scps2", ROOM2, Floor(0.6 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2medibay", ROOM2, Floor(0.7 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room1123", ROOM2, Floor(0.75 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2elevator", ROOM2, Floor(0.85 * Float(Room2Amount[0])), MinPos, MaxPos)
	SetRoom("room2posters", ROOM2, Floor(0.9 * Float(Room2Amount[0])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Floor(0.5 * Float(Room2CAmount[0]))) = "room1162"
	
	MapRoom(ROOM3, Floor(Rnd(0.2, 0.8) * Float(Room3Amount[0]))) = "room3storage"
	
	MapRoom(ROOM4, Floor(0.3 * Float(Room4Amount[0]))) = "room4info"
	
	; ~ [HEAVY CONTAINMENT ZONE]
	
	MinPos = Room1Amount[0]
	MaxPos = Room1Amount[0] + Room1Amount[1] - 1
	
	SetRoom("room079", ROOM1, Room1Amount[0] + Floor(0.15 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("room106", ROOM1, Room1Amount[0] + Floor(0.3 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("room096", ROOM1, Room1Amount[0] + Floor(0.4 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("room035", ROOM1, Room1Amount[0] + Floor(0.5 * Float(Room1Amount[1])), MinPos, MaxPos)
	SetRoom("room895", ROOM1, Room1Amount[0] + Floor(0.7 * Float(Room1Amount[1])), MinPos, MaxPos)
	
	MinPos = Room2Amount[0]
	MaxPos = Room2Amount[0] + Room2Amount[1] - 1
	
	MapRoom(ROOM2, Room2Amount[0] + Floor(0.1 * Float(Room2Amount[1]))) = "room2nuke"
	
	SetRoom("room409", ROOM2, Room2Amount[0] + Floor(0.15 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2mt", ROOM2, Room2Amount[0] + Floor(0.25 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room049", ROOM2, Room2Amount[0] + Floor(0.4 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room008", ROOM2, Room2Amount[0] + Floor(0.5 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2shaft", ROOM2, Room2Amount[0] + Floor(0.6 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2testroom2", ROOM2, Room2Amount[0] + Floor(0.7 * Float(Room2Amount[1])), MinPos, MaxPos)
	SetRoom("room2servers", ROOM2, Room2Amount[0] + Floor(0.9 * Room2Amount[1]), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Room2CAmount[0] + Floor(0.5 * Float(Room2CAmount[1]))) = "room2cpit"
	
	MapRoom(ROOM3, Room3Amount[0] + Floor(0.3 * Float(Room3Amount[1]))) = "room513"
	MapRoom(ROOM3, Room3Amount[0] + Floor(0.6 * Float(Room3Amount[1]))) = "room966"
	
	; ~ [ENTRANCE ZONE]
	
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 3) = "gatebentrance"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 2) = "gateaentrance"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1] + Room1Amount[2] - 1) = "roomo5"
	MapRoom(ROOM1, Room1Amount[0] + Room1Amount[1]) = "room1lifts"
	
	MinPos = Room2Amount[0] + Room2Amount[1]
	MaxPos = Room2Amount[0] + Room2Amount[1] + Room2Amount[2] - 1		
	
	MapRoom(ROOM2, MinPos + Floor(0.1 * Float(Room2Amount[2]))) = "room2poffices"
	
	SetRoom("room2cafeteria", ROOM2, MinPos + Floor(0.2 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2sroom", ROOM2, MinPos + Floor(0.3 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2bio", ROOM2, MinPos + Floor(0.35 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2servers2", ROOM2, MinPos + Floor(0.4 * Room2Amount[2]), MinPos, MaxPos)	
	SetRoom("room2offices", ROOM2, MinPos + Floor(0.45 * Room2Amount[2]), MinPos, MaxPos)
	SetRoom("room2offices4", ROOM2, MinPos + Floor(0.5 * Room2Amount[2]), MinPos, MaxPos)	
	SetRoom("room2offices5", ROOM2, MinPos + Floor(0.55 * Room2Amount[2]), MinPos, MaxPos)	
	SetRoom("room860", ROOM2, MinPos + Floor(0.6 * Room2Amount[2]), MinPos, MaxPos)
	SetRoom("room2medibay2", ROOM2, MinPos + Floor(0.7 * Float(Room2Amount[2])), MinPos, MaxPos)
	SetRoom("room2poffices2", ROOM2, MinPos + Floor(0.8 * Room2Amount[2]), MinPos, MaxPos)
	SetRoom("room2offices2", ROOM2, MinPos + Floor(0.9 * Float(Room2Amount[2])), MinPos, MaxPos)
	
	MapRoom(ROOM2C, Room2CAmount[0] + Room2CAmount[1]) = "room2ccont"	
	MapRoom(ROOM2C, Room2CAmount[0] + Room2CAmount[1] + 1) = "room2clockroom3"		
	
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.3 * Float(Room3Amount[2]))) = "room3servers"
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.7 * Float(Room3Amount[2]))) = "room3servers2"
	MapRoom(ROOM3, Room3Amount[0] + Room3Amount[1] + Floor(0.5 * Float(Room3Amount[2]))) = "room3offices"
	
	; ~ [GENERATE OTHER ROOMS]
	
	Temp = 0
	For y = MapGridSize - 1 To 1 Step - 1
		If y < (MapGridSize / 3) + 1 Then
			Zone = 3
		ElseIf y < MapGridSize * (2.0 / 3.0)
			Zone = 2
		Else
			Zone = 1
		EndIf
		For x = 1 To MapGridSize - 2
			If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_CheckpointTile Then
				If y > MapGridSize / 2 Then
					r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, "room2checkpoint")
				Else
					r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, "room2checkpoint2")
				EndIf
			ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile				
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1)
				Select Temp
					Case 1 ; ~ Generate ROOM1
						;[Block]
						If CurrMapGrid\RoomID[ROOM1] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = "" Then
							If MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1])	
						EndIf
						
						r.Rooms = CreateRoom(Zone, ROOM1, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
						If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] Then
							r\Angle = 180.0
						ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
							r\Angle = 270.0
						ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)]
							r\Angle = 90.0
						Else
							r\Angle = 0.0
						EndIf
						TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
						CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
						;[End Block]
					Case 2 ; ~ Generate ROOM2
						;[Block]
						If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile Then
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = "" Then
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2])	
							EndIf
							r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
							If Rand(2) = 1 Then
								r\Angle = 90.0
							Else
								r\Angle = 270.0
							EndIf
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""  Then
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2])	
							EndIf
							r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
							If Rand(2) = 1 Then
								r\Angle = 180.0
							Else
								r\Angle = 0.0
							EndIf
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						Else
							If CurrMapGrid\RoomID[ROOM2C] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""  Then
								If MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C])	
							EndIf
							If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile Then
								r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r\Angle = 180.0
							ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r\Angle = 90.0
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
								r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r\Angle = 270.0
							Else
								r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
								r\Angle = 0.0
							EndIf
							TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
							CurrMapGrid\RoomID[ROOM2C] = CurrMapGrid\RoomID[ROOM2C] + 1
						EndIf
						;[End Block]
					Case 3 ; ~ Generate ROOM3
						;[Block]
						If CurrMapGrid\RoomID[ROOM3] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""  Then
							If MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3])	
						EndIf
						r.Rooms = CreateRoom(Zone, ROOM3, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
						If (Not CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)]) Then
							r\Angle = 180.0
						ElseIf (Not CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)])
							r\Angle = 90.0
						ElseIf (Not CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)])
							r\Angle = 270.0
						Else
							r\Angle = 0.0
						EndIf
						TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
						CurrMapGrid\RoomID[ROOM3] = CurrMapGrid\RoomID[ROOM3] + 1
						;[End Block]
					Case 4 ; ~ Generate ROOM4
						;[Block]
						If CurrMapGrid\RoomID[ROOM4] < MaxRooms And CurrMapGrid\RoomName[x + (y * MapGridSize)] = ""  Then
							If MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4]) <> "" Then CurrMapGrid\RoomName[x + (y * MapGridSize)] = MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4])	
						EndIf
						r.Rooms = CreateRoom(Zone, ROOM4, x * RoomSpacing, 0.0, y * RoomSpacing, CurrMapGrid\RoomName[x + (y * MapGridSize)])
						If Rand(4) = 1 Then
							r\Angle = 0.0
						ElseIf Rand(3) = 1
							r\Angle = 90.0
						ElseIf Rand(2) = 1
							r\Angle = 180.0
						Else
							r\Angle = 270.0
						EndIf
						TurnEntity(r\OBJ, 0.0, r\Angle, 0.0)
						CurrMapGrid\RoomID[ROOM4] = CurrMapGrid\RoomID[ROOM4] + 1
						;[End Block]
				End Select
			EndIf
		Next
	Next		
	
	; ~ Spawn some rooms outside the map
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, RoomSpacing * (-RoomSpacing), "gateb")
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, RoomSpacing, "gatea")
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 0.0, (MapGridSize - 1) * RoomSpacing, "pocketdimension")
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1	
	
	If opt\IntroEnabled Then
		r.Rooms = CreateRoom(0, ROOM1, RoomSpacing, 0.0, (MapGridSize - 1) * RoomSpacing, "room173intro")
		CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	EndIf
	
	r.Rooms = CreateRoom(0, ROOM1, RoomSpacing, 800.0, RoomSpacing, "dimension1499")
	CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
	
	; ~ Prevent room overlaps
	For r.Rooms = Each Rooms
		PreventRoomOverlap(r)
	Next
	
	If opt\DebugMode Then
		Repeat
			Cls()
			i = MapGridSize - 1
			For x = 0 To MapGridSize - 1
				For y = 0 To MapGridSize - 1
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile Then
						Zone = GetZone(y)
						Color((50 * Zone) + 50, (50 * Zone) + 50, (50 * Zone) + 50)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					Else
						If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_CheckpointTile Then
							Color(0, 200, 0)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 4
							Color(50, 50, 255)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 3
							Color(50, 255, 255)
						ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
							Color(255, 255, 50)
						Else
							Color(255, 255, 255)
						EndIf
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					EndIf
				Next
				i = i - 1
			Next	
			
			i = MapGridSize - 1
			For x = 0 To MapGridSize - 1
				For y = 0 To MapGridSize - 1
					If ScaledMouseX() > (i * 32) * MenuScale And ScaledMouseX() < ((i * 32) + 32) * MenuScale And ScaledMouseY() > (y * 32) * MenuScale And ScaledMouseY() < ((y * 32) + 32) * MenuScale Then
						Color(255, 0, 0)
						Text(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, CurrMapGrid\Grid[x + (y * MapGridSize)] + " " + CurrMapGrid\RoomName[x + (y * MapGridSize)])
					Else
						If CurrMapGrid\RoomName[x + (y * MapGridSize)] <> "" Then
							Color(0, 0, 0)
							Text(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, CurrMapGrid\Grid[x + (y * MapGridSize)])
						EndIf
					EndIf
				Next
				i = i - 1
			Next
			Flip()
			If opt\DisplayMode = 0 Then DrawImage(CursorIMG, ScaledMouseX(), ScaledMouseY())
		Until GetKey() <> 0 Lor MouseHit(1)
	EndIf
	
	For y = 0 To MapGridSize
		For x = 0 To MapGridSize
			CurrMapGrid\Grid[x + (y * MapGridSize)] = Min(CurrMapGrid\Grid[x + (y * MapGridSize)], 1)
		Next
	Next
	
	; ~ Create the doors between rooms
	For y = MapGridSize To 0 Step -1
		If y < I_Zone\Transition[1] - 1 Then
			Zone = 3
		ElseIf y >= I_Zone\Transition[1] - 1 And y < I_Zone\Transition[0] - 1 Then
			Zone = 2
		Else
			Zone = 1
		EndIf
		For x = MapGridSize To 0 Step -1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile Then
				If Zone = 2 Then
					Temp = Heavy_Door
				Else
					Temp = Default_Door
				EndIf
				For r.Rooms = Each Rooms
					r\Angle = WrapAngle(r\Angle)
					If Int(r\x / RoomSpacing) = x And Int(r\z / RoomSpacing) = y Then
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 90.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 90.0 Lor r\Angle = 270.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 90.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 180.0 Lor r\Angle = 90.0 Then ShouldSpawnDoor = True
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						
						If ShouldSpawnDoor Then
							If x + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile Then
									d.Doors = CreateDoor(Float(x) * RoomSpacing + (RoomSpacing / 2.0), 0.0, Float(y) * RoomSpacing, 90.0, r, Max(Rand(-3, 1), 0), Temp)
									r\AdjDoor[0] = d
								EndIf
							EndIf
						EndIf
						
						ShouldSpawnDoor = False
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								If r\Angle = 180.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM2
								;[Block]
								If r\Angle = 0.0 Lor r\Angle = 180.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM2C
								;[Block]
								If r\Angle = 180.0 Lor r\Angle = 90.0 Then ShouldSpawnDoor = True
								;[End Block]
							Case ROOM3
								;[Block]
								If r\Angle = 180.0 Lor r\Angle = 90.0 Lor r\Angle = 270.0 Then ShouldSpawnDoor = True
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If y + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile Then
									d.Doors = CreateDoor(Float(x) * RoomSpacing, 0.0, Float(y) * RoomSpacing + (RoomSpacing / 2.0), 0.0, r, Max(Rand(-3, 1), 0), Temp)
									r\AdjDoor[3] = d
								EndIf
							EndIf
						EndIf
						Exit
					EndIf
				Next
			EndIf
		Next
	Next
	
	For r.Rooms = Each Rooms
		r\Angle = WrapAngle(r\Angle)
		SetupTriggerBoxes(r)
		r\Adjacent[0] = Null
		r\Adjacent[1] = Null
		r\Adjacent[2] = Null
		r\Adjacent[3] = Null
		For r2.Rooms = Each Rooms
			If r <> r2 Then
				If r2\z = r\z Then
					If r2\x = r\x + 8.0 Then
						r\Adjacent[0] = r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf r2\x = r\x - 8.0
						r\Adjacent[2] = r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x = r\x Then
					If r2\z = r\z - 8.0 Then
						r\Adjacent[1] = r2
						If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
					ElseIf r2\z = r\z + 8.0
						r\Adjacent[3] = r2
						If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
					EndIf
				EndIf
			EndIf
			If r\Adjacent[0] <> Null And r\Adjacent[1] <> Null And r\Adjacent[2] <> Null And r\Adjacent[3] <> Null Then Exit
		Next
	Next
End Function

Function LoadTerrain(HeightMap%, yScale# = 0.7, t1%, t2%, Mask%)
	; ~ Load the HeightMap
	If (Not HeightMap) Then RuntimeError("HeightMap Image " + HeightMap + " not found.")
	
	; ~ Store HeightMap dimensions
	Local x% = ImageWidth(HeightMap) - 1, y = ImageHeight(HeightMap) - 1
	Local lx%, ly%, Index%
	
	; ~ Load texture and lightmaps
	If (Not t1) Then RuntimeError("Texture #1 " + Chr(34) + t1 + Chr(34) + " not found.")
	If (Not t2) Then RuntimeError("Texture #2 " + Chr(34) + t2 + Chr(34) + " not found.")
	If (Not Mask) Then RuntimeError("Mask image " + Chr(34) + Mask + Chr(34) + " not found.")
	
	; ~ Auto scale the textures to the right size
	If t1 Then ScaleTexture(t1, x / 4, y / 4)
	If t2 Then ScaleTexture(t2, x / 4, y / 4)
	If Mask Then ScaleTexture(Mask, x, y)
	
	; ~ Start building the terrain
	Local Mesh% = CreateMesh()
	Local Surf% = CreateSurface(Mesh)
	
	; ~ Create some verts for the terrain
	For ly = 0 To y
		For lx = 0 To x
			AddVertex(Surf, lx, 0, ly, 1.0 / lx, 1.0 / ly)
		Next
	Next
	RenderWorld()
	
	; ~ Connect the verts with faces
	For ly = 0 To y - 1
		For lx = 0 To x - 1
			AddTriangle(Surf, lx + ((x + 1) * ly), lx + ((x + 1) * ly) + (x + 1), (lx + 1) + ((x + 1) * ly))
			AddTriangle(Surf, (lx + 1) + ((x + 1) * ly), lx + ((x + 1) * ly) + (x + 1), (lx + 1) + ((x + 1) * ly) + (x + 1))
		Next
	Next
	
	; ~ Position the terrain to center 0, 0, 0
	Local Mesh2% = CopyMesh(Mesh, Mesh)
	Local Surf2% = GetSurface(Mesh2, 1)
	
	PositionMesh(Mesh, (-x) / 2.0, 0.0, (-y) / 2.0)
	PositionMesh(Mesh2, (-x) / 2.0, 0.01, (-y) / 2.0)
	
	; ~ Alter vertice height to match the heightmap red channel
	LockBuffer(ImageBuffer(HeightMap))
	LockBuffer(TextureBuffer(Mask))
	
	For lx = 0 To x
		For ly = 0 To y
			; ~ Using vertex alpha and two meshes instead of FE_ALPHAWHATEVER
			; ~ It doesn't look perfect but it does the job
			; ~ You might get better results by downscaling the mask to the same size as the heightmap
			Local MaskX# = Min(lx * Float(TextureWidth(Mask)) / Float(ImageWidth(HeightMap)), TextureWidth(Mask) - 1)
			Local MaskY# = TextureHeight(Mask) - Min(ly * Float(TextureHeight(Mask)) / Float(ImageHeight(HeightMap)), TextureHeight(Mask) - 1)
			Local RGB%, RED%
			
			RGB = ReadPixelFast(Min(lx, x - 1.0), y - Min(ly, y - 1.0), ImageBuffer(HeightMap))
			RED = (RGB And $FF0000) Shr 16 ; ~ Separate out the red
			
			Local Alpha# = (((ReadPixelFast(Max(MaskX -5.0, 5.0), Max(MaskY - 5.0, 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, TextureWidth(Mask) - 5.0), Min(MaskY + 5.0, TextureHeight(Mask) - 5), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Min(MaskY + 5.0, TextureHeight(Mask) - 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, TextureWidth(Mask) - 5.0), Max(MaskY - 5.0, 5.0), TextureBuffer(Mask)) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha * 0.25
			Alpha = Sqr(Alpha)
			
			Index = lx + ((x + 1) * ly)
			VertexCoords(Surf, Index , VertexX(Surf, Index), RED * yScale, VertexZ(Surf, Index))
			VertexCoords(Surf2, Index , VertexX(Surf2, Index), RED * yScale, VertexZ(Surf2, Index))
			VertexColor(Surf2, Index, 255.0, 255.0, 255.0, Alpha)
			; ~ Set the terrain texture coordinates
			VertexTexCoords(Surf, Index, lx, -ly )
			VertexTexCoords(Surf2, Index, lx, -ly) 
		Next
	Next
	UnlockBuffer(TextureBuffer(Mask))
	UnlockBuffer(ImageBuffer(HeightMap))
	
	UpdateNormals(Mesh)
	UpdateNormals(Mesh2)
	
	EntityTexture(Mesh, t1, 0, 0)
	EntityTexture(Mesh2, t2, 0, 0)
	
	EntityFX(Mesh, 1)
	EntityFX(Mesh2, 1 + 2 + 32)
	
	Return(Mesh)
End Function

Function CheckTriggers$()
	Local i%
	
	If PlayerRoom\TriggerBoxAmount = 0 Then
		Return
	Else
		For i = 0 To PlayerRoom\TriggerBoxAmount - 1
			If chs\DebugHUD <> 0 Then
				EntityAlpha(PlayerRoom\TriggerBoxes[i]\OBJ, 0.2)
			Else
				EntityAlpha(PlayerRoom\TriggerBoxes[i]\OBJ, 0.0)
			EndIf
			
			If EntityX(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinX And EntityX(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxX
				If EntityY(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinY And EntityY(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxY
					If EntityZ(me\Collider) > PlayerRoom\TriggerBoxes[i]\MinZ And EntityZ(me\Collider) < PlayerRoom\TriggerBoxes[i]\MaxZ
						Return(PlayerRoom\TriggerBoxes[i]\Name)
					EndIf
				EndIf
			EndIf
		Next
	EndIf
End Function

RenderLoading(55, "SKY CORE")

Include "Source Code\Sky_Core.bb"

Global CHUNKDATA%[64 ^ 2]

Function SetChunkDataValues()
	Local StrTemp$, i%, j%
	
	StrTemp = ""
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	For i = 0 To 63
		For j = 0 To 63
			CHUNKDATA[(i * 64) + j] = Rand(0, GetINIInt("Data\1499chunks.ini", "general", "count"))
		Next
	Next
	
	SeedRnd(MilliSecs2())
End Function

Type ChunkPart
	Field Amount%
	Field OBJ%[128]
	Field RandomYaw#[128]
	Field ID%
End Type

Function CreateChunkParts(r.Rooms)
	Local chp.ChunkPart, chp2.ChunkPart
	Local File$ = "Data\1499chunks.ini"
	Local ChunkAmount% = GetINIInt(File, "general", "count")
	Local i%, StrTemp$, j%
	
	StrTemp = ""
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	For i = 0 To ChunkAmount
		Local Loc% = GetINISectionLocation(File, "chunk" + i)
		
		If Loc > 0 Then
			StrTemp = GetINIString2(File, Loc, "count")
			chp.ChunkPart = New ChunkPart
			chp\Amount = Int(StrTemp)
			For j = 0 To Int(StrTemp)
				Local OBJ_ID% = GetINIString2(File, Loc, "obj" + j)
				Local x$ = GetINIString2(File, Loc, "obj" + j +" -x")
				Local z$ = GetINIString2(File, Loc, "obj" + j + "-z")
				Local Yaw$ = GetINIString2(File, Loc, "obj" + j + "-yaw")
				
				chp\OBJ[j] = CopyEntity(r\Objects[OBJ_ID])
				If Lower(Yaw) = "random"
					chp\RandomYaw[j] = Rnd(360)
					RotateEntity(chp\OBJ[j], 0.0, chp\RandomYaw[j], 0.0)
				Else
					RotateEntity(chp\OBJ[j], 0.0, Float(Yaw), 0.0)
				EndIf
				PositionEntity(chp\OBJ[j], Float(x), 0, Float(z))
				ScaleEntity(chp\OBJ[j], RoomScale, RoomScale, RoomScale)
				EntityType(chp\OBJ[j], HIT_MAP)
				EntityPickMode(chp\OBJ[j], 2)
				HideEntity(chp\OBJ[j])
			Next
			chp2 = Before(chp)
			If chp2 <> Null Then chp\ID = chp2\ID + 1
		EndIf
	Next
	
	SeedRnd(MilliSecs2())
End Function

Type Chunk
	Field OBJ%[128]
	Field x#, z#, y#
	Field Amount%
	Field IsSpawnChunk%
	Field ChunkPivot%
	Field PlatForm%
End Type

Function CreateChunk.Chunk(OBJ%, x#, y#, z#, IsSpawnChunk% = False)
	Local ch.Chunk, chp.ChunkPart
	Local i%
	
	ch.Chunk = New Chunk
	ch\ChunkPivot = CreatePivot()
	ch\x = x
	ch\y = y
	ch\z = z
	PositionEntity(ch\ChunkPivot, ch\x + 20.0, ch\y, ch\z + 20.0, True)
	
	ch\IsSpawnChunk = IsSpawnChunk
	
	If OBJ > -1 Then
		ch\Amount = GetINIInt("Data\1499chunks.ini", "chunk" + OBJ, "count")
		For chp.ChunkPart = Each ChunkPart
			If chp\ID = OBJ
				For i = 0 To ch\Amount
					ch\OBJ[i] = CopyEntity(chp\OBJ[i], ch\ChunkPivot)
				Next
			EndIf
		Next
	EndIf
	
	ch\PlatForm = CopyEntity(PlayerRoom\Objects[0], ch\ChunkPivot)
	EntityType(ch\PlatForm, HIT_MAP)
	EntityPickMode(ch\PlatForm, 2)
	
	Return(ch)
End Function

Const ChunkMaxDistance# = 120.0

Function UpdateChunks(r.Rooms, ChunkPartAmount%, SpawnNPCs% = True)
	Local ch.Chunk, ch2.Chunk, n.NPCs
	Local StrTemp$, i%, j%, x#, z#, y#
	Local ChunkX#, ChunkZ#
	
	ChunkX = Int(EntityX(me\Collider) / 40.0)
	ChunkZ = Int(EntityZ(me\Collider) / 40.0)
	
	y = EntityY(PlayerRoom\OBJ)
	x = (-ChunkMaxDistance) + (ChunkX * 40.0)
	z = (-ChunkMaxDistance) + (ChunkZ * 40.0)
	
	Local CurrChunkData% = 0, MaxChunks% = GetINIInt("Data\1499chunks.ini", "general", "count")
	
	Repeat
		Local ChunkFound% = False
		
		For ch.Chunk = Each Chunk
			If ch\x = x And ch\z = z Then
				ChunkFound = True
				Exit
			EndIf
		Next
		If (Not ChunkFound) Then
			CurrChunkData = CHUNKDATA[Abs((((x + 32) / 40) Mod 64) * 64) + (Abs(((z + 32) / 40) Mod 64))]
			ch2 = CreateChunk(CurrChunkData, x, y, z)
			ch2\IsSpawnChunk = False
		EndIf
		x = x + 40.0
		If x > ChunkMaxDistance + (ChunkX * 40.0)
			z = z + 40.0
			x = (-ChunkMaxDistance) + (ChunkX * 40.0)
		EndIf
	Until z > ChunkMaxDistance + (ChunkZ * 40.0)
	
	For ch.Chunk = Each Chunk
		If (Not ch\IsSpawnChunk)
			If DistanceSquared(EntityX(me\Collider), EntityX(ch\ChunkPivot), EntityZ(me\Collider), EntityZ(ch\ChunkPivot)) > PowTwo(ChunkMaxDistance)
				FreeEntity(ch\ChunkPivot) : ch\ChunkPivot = 0
				Delete(ch)
			EndIf
		EndIf
	Next
	
	Local CurrNPCNumber% = 0
	
	For n.NPCs = Each NPCs
		If n\NPCType = NPCType1499_1 Then CurrNPCNumber = CurrNPCNumber + 1
	Next
	
	Local MaxNPCs% = 64 ; ~ The maximum amount of NPCs in dimension1499
	Local e.Events
	
	For e.Events = Each Events
		If e\room = PlayerRoom Then
			If e\room\NPC[0] <> Null Then
				MaxNPCs = 16
				Exit
			EndIf
		EndIf
	Next
	
	If CurrNPCNumber < MaxNPCs Then
		Select Rand(1, 8)
			Case 1
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(40.0, 80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(40.0, 80.0))
				;[End Block]
			Case 2
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(40.0, 80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(-40.0, 40.0))
				;[End Block]
			Case 3
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(40.0, 80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(-40.0, -80.0))
				;[End Block]
			Case 4
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-40.0, 40.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(-40.0, -80.0))
				;[End Block]
			Case 5
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-40.0, -80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(-40.0, -80.0))
				;[End Block]
			Case 6
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-40.0, -80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(-40.0, 40.0))
				;[End Block]
			Case 7
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-40.0, -80.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(40.0, 80.0))
				;[End Block]
			Case 8
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, EntityX(me\Collider) + Rnd(-40.0, 40.0), EntityY(PlayerRoom\OBJ) + 0.5, EntityZ(me\Collider) + Rnd(40.0, 80.0))
				;[End Block]
		End Select
		If Rand(2) = 1 Then n\State2 = 500.0 * 3.0
		n\Angle = Rnd(360.0)
	Else
		For n.NPCs = Each NPCs
			If n\NPCType = NPCType1499_1 Then
				If n\PrevState = 0 Then
					If EntityDistanceSquared(n\Collider, me\Collider) > PowTwo(ChunkMaxDistance) Lor EntityY(n\Collider) < EntityY(PlayerRoom\OBJ) - 5.0 Then
						; ~ This will be updated like this so that new NPCs can spawn for the player
						RemoveNPC(n)
					EndIf
				EndIf
			EndIf
		Next
	EndIf
	
End Function

Function HideChunks()
	Local ch.Chunk, i%
	
	For ch.Chunk = Each Chunk
		If (Not ch\IsSpawnChunk) Then
			For i = 0 To ch\Amount
				FreeEntity(ch\OBJ[i]) : ch\OBJ[i] = 0
			Next
			FreeEntity(ch\PlatForm) : ch\PlatForm = 0
			FreeEntity(ch\ChunkPivot) : ch\ChunkPivot = 0
			Delete(ch)
		EndIf
	Next
End Function

Function DeleteChunks()
	Delete Each Chunk
	Delete Each ChunkPart
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D