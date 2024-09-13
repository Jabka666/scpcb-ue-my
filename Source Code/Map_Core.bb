RenderLoading(50, GetLocalString("loading", "core.mat"))

Include "Source Code\Materials_Core.bb"

RenderLoading(55, GetLocalString("loading", "core.texcache"))

Include "Source Code\Texture_Cache_Core.bb"

Type Props
	Field Name$
	Field OBJ%
	Field room.Rooms
	Field TexPath$
End Type

Type TempProps
	Field Name$
	Field x#, y#, z#
	Field Pitch#, Yaw#, Roll#
	Field ScaleX#, ScaleY#, ScaleZ#
	Field HasCollision%
	Field FX%
	Field Texture$
	Field RoomTemplate.RoomTemplates
End Type

Function CheckForPropModel%(File$)
	Select StripPath(File)
		Case "door01.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_DEFAULT_MODEL]))
			;[End Block]
		Case "contdoorleft.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_BIG_MODEL_1]))
			;[End Block]
		Case "contdoorright.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_BIG_MODEL_2]))
			;[End Block]
		Case "officedoor.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorModelID[DOOR_OFFICE_MODEL]))
			;[End Block]
		Case "doorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_DEFAULT_FRAME_MODEL]))
			;[End Block]
		Case "contdoorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_BIG_FRAME_MODEL]))
			;[End Block]
		Case "officedoorframe.b3d"
			;[Block]
			Return(CopyEntity(d_I\DoorFrameModelID[DOOR_OFFICE_FRAME_MODEL]))
			;[End Block]
		Case "button.b3d"
			;[Block]
			Return(CopyEntity(d_I\ButtonModelID[BUTTON_DEFAULT]))
			;[End Block]
		Case "buttonkeycard.b3d"
			;[Block]
			Return(CopyEntity(d_I\ButtonModelID[BUTTON_KEYCARD]))
			;[End Block]
		Case "elevator_panel.b3d"
			;[Block]
			Return(CopyEntity(d_I\ElevatorPanelModel))
			;[End Block]
		Case "leverbase.b3d"
			;[Block]
			Return(CopyEntity(lvr_I\LeverModelID[LEVER_BASE_MODEL]))
			;[End Block]
		Case "leverhandle.b3d"
			;[Block]
			Return(CopyEntity(lvr_I\LeverModelID[LEVER_HANDLE_MODEL]))
			;[End Block]
		Case "monitor2.b3d"
			;[Block]
			Return(CopyEntity(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]))
			;[End Block]
		Default
			;[Block]
			Return(LoadMesh_Strict(File))
			;[End Block]
	End Select
End Function

Function CreateProp.Props(room.Rooms, Name$, x#, y#, z#, Pitch#, Yaw#, Roll#, ScaleX#, ScaleY#, ScaleZ#, HasCollision%, FX%, TexturePath$)
	Local p.Props, p2.Props
	
	p.Props = New Props
	For p2.Props = Each Props
		If p2\Name = Name
			p\OBJ = CopyEntity(p2\OBJ)
			Exit
		EndIf
	Next
	
	p\Name = Name
	p\room = room
	p\TexPath = TexturePath
	
	If p\OBJ = 0 Then p\OBJ = CheckForPropModel(Name) ; ~ A hacky optimization (just copy models that loaded as variable). Also fixes models folder if the CBRE was used
	PositionEntity(p\OBJ, x, y, z)
	RotateEntity(p\OBJ, Pitch, Yaw, Roll)
	If room <> Null Then EntityParent(p\OBJ, room\OBJ)
	ScaleEntity(p\OBJ, ScaleX, ScaleY, ScaleZ)
	EntityType(p\OBJ, HasCollision) ; ~ DO NOT FORGET THAT Const HIT_MAP% = 1
	EntityFX(p\OBJ, FX)
	EntityPickMode(p\OBJ, 2)
	
	Return(p)
End Function

Function RemoveProp%(pr.Props)
	FreeEntity(pr\OBJ) : pr\OBJ = 0
	Delete(pr) : pr = Null
End Function

Type TempLights
	Field RoomTemplate.RoomTemplates
	Field lType%
	Field x#, y#, z#
	Field Range#
	Field R%, G%, B%
	Field Pitch#, Yaw#
	Field InnerConeAngle%, OuterConeAngle#
	Field HasSprite%
End Type

Global LightVolume#, TempLightVolume#

Type Lights
	Field OBJ%
	Field Sprite%, AdvancedSprite%
	Field x#, y#, z#
	Field Range#
	Field R%, G%, B%
	Field Intensity#
	Field Flickers% = False
	Field room.Rooms
End Type

Function AddLight.Lights(room.Rooms, x#, y#, z#, Type_%, Range#, R%, G%, B%, HasSprite% = True)
	Local l.Lights, l2.Lights
	
	l.Lights = New Lights
	l\room = room
	
	l\OBJ = CreateLight(Type_)
	LightRange(l\OBJ, Range)
	LightColor(l\OBJ, R, G, B)
	PositionEntity(l\OBJ, x, y, z, True)
	If room <> Null Then EntityParent(l\OBJ, room\OBJ)
	HideEntity(l\OBJ)
	
	If HasSprite
		l\Sprite = CreateSprite()
		PositionEntity(l\Sprite, x, y, z)
		ScaleSprite(l\Sprite, 0.13 , 0.13)
		EntityTexture(l\Sprite, misc_I\LightSpriteID[LIGHT_SPRITE_DEFAULT])
		EntityFX(l\Sprite, 1 + 8)
		EntityBlend(l\Sprite, 3)
		EntityColor(l\Sprite, R, G, B)
		EntityParent(l\Sprite, l\OBJ)
		HideEntity(l\Sprite)
		
		l\AdvancedSprite = CreateSprite()
		PositionEntity(l\AdvancedSprite, x, y, z)
		ScaleSprite(l\AdvancedSprite, Rnd(0.36, 0.4), Rnd(0.36, 0.4))
		EntityTexture(l\AdvancedSprite, misc_I\AdvancedLightSprite)
		EntityFX(l\AdvancedSprite, 1 + 8)
		EntityBlend(l\AdvancedSprite, 3)
		EntityOrder(l\AdvancedSprite, -1)
		EntityColor(l\AdvancedSprite, R, G, B)
		RotateEntity(l\AdvancedSprite, 0.0, 0.0, Rnd(360.0))
		SpriteViewMode(l\AdvancedSprite, 1)
		EntityParent(l\AdvancedSprite, l\OBJ)
		HideEntity(l\AdvancedSprite)
	EndIf
	
	l\Intensity = (R + G + B) / 255.0 / 3.0
	If room <> Null
		If Rand(50) = 1
			Local RID% = room\RoomTemplate\RoomID
			
			If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499 Then l\Flickers = True
		EndIf
	EndIf
	Return(l)
End Function

Global IsBlackOut%, PrevIsBlackOut%
Global SecondaryLightOn#

Global UpdateLightsTimer#
Global LightRenderDistance#

Function UpdateLightVolume%()
	Local l.Lights
	
	If SecondaryLightOn > 0.3
		If UpdateLightsTimer < 8.0
			UpdateLightsTimer = UpdateLightsTimer + fps\Factor[0]
		Else
			For l.Lights = Each Lights
				If l\room <> Null
					If l\room\Dist < 8.0 Lor l\room = PlayerRoom
						Local Dist# = EntityDistanceSquared(Camera, l\OBJ)
						
						If Dist < PowTwo(HideDistance) Then TempLightVolume = Max((TempLightVolume + PowTwo(l\Intensity) * ((HideDistance - Sqr(Dist)) / HideDistance)) / 4.5, 1.0)
					EndIf
				EndIf
			Next
			UpdateLightsTimer = 0.0
		EndIf
		LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
	Else
		LightVolume = 1.0
		UpdateLightsTimer = 0.0
	EndIf
End Function

Function UpdateLights%(Cam%)
	Local l.Lights, i%, Random#, Alpha#
	
	For l.Lights = Each Lights
		If SecondaryLightOn > 0.3
			If l\room <> Null
				If l\room\Dist < 8.0 Lor l\room = PlayerRoom
					Local LightOBJHidden%
					
					If l\Sprite <> 0
						If Cam = Camera ; ~ The lights are rendered by player's cam
							EntityOrder(l\AdvancedSprite, -1)
							If UpdateLightsTimer = 0.0
								Local Dist# = EntityDistanceSquared(Cam, l\OBJ)
								Local LightSpriteHidden% = EntityHidden(l\Sprite)
								Local LightAdvancedSpriteHidden% = EntityHidden(l\AdvancedSprite)
								
								LightOBJHidden = EntityHidden(l\OBJ)
								If Dist < LightRenderDistance * LightVolume
									EntityAutoFade(l\Sprite, 0.1 * LightVolume, me\CameraFogDist * LightVolume)
									
									Local LightVisible% = EntityVisible(Cam, l\OBJ)
									Local LightInView% = EntityInView(l\OBJ, Cam)
									
									If LightOBJHidden Then ShowEntity(l\OBJ)
									If l\Flickers And Rand(13) = 1 And LightVisible
										If (Not LightOBJHidden) Then HideEntity(l\OBJ)
										PlaySound2(snd_I\LightSFX[Rand(0, 2)], Cam, l\OBJ, 4.0)
										If LightInView
											If (Not LightSpriteHidden) Then HideEntity(l\Sprite)
											If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
										EndIf
										Random = Rnd(0.35, 0.8)
										SecondaryLightOn = Clamp(SecondaryLightOn - Random, 0.301, 1.0)
										TempLightVolume = Clamp(TempLightVolume - Random, 0.5, 1.0)
									EndIf
									If LightInView And LightVisible
										If LightSpriteHidden Then ShowEntity(l\Sprite)
										If opt\AdvancedRoomLights
											Alpha = 1.0 - Max(Min(((Sqr(Dist) + 0.5) / 7.5), 1.0), 0.0)
											If Alpha > 0.0
												If LightAdvancedSpriteHidden Then ShowEntity(l\AdvancedSprite)
												EntityAlpha(l\AdvancedSprite, Max(3.0 * (((CurrAmbientColorR + CurrAmbientColorG + CurrAmbientColorB) / 3) / 255.0) * (l\Intensity / 2.0), 1.0) * Alpha)
												
												Random = Rnd(0.36, 0.4)
												ScaleSprite(l\AdvancedSprite, Random, Random)
											Else
												; ~ Instead of rendering the sprite invisible, just hiding it if the player is far away from it
												If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
											EndIf
										Else
											; ~ The additional sprites option is disabled, hide the sprites
											If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
										EndIf
									Else
										; ~ Hide the sprites because they aren't visible
										If (Not LightSpriteHidden) Then HideEntity(l\Sprite)
										If (Not LightAdvancedSpriteHidden) Then HideEntity(l\AdvancedSprite)
									EndIf
								Else
									; ~ Hide the light emitter because it is too far
									If (Not LightOBJHidden) Then HideEntity(l\OBJ)
								EndIf
							EndIf
						Else
							; ~ This will make the lightsprites not glitch through the wall when they are rendered by the cameras
							EntityOrder(l\AdvancedSprite, 0)
						EndIf
					Else
						If Cam = Camera ; ~ The lights are rendered by player's cam
							If UpdateLightsTimer = 0.0
								LightOBJHidden = EntityHidden(l\OBJ)
								
								If EntityDistanceSquared(Cam, l\OBJ) < LightRenderDistance * LightVolume
									If LightOBJHidden Then ShowEntity(l\OBJ)
									If l\Flickers And Rand(13) = 1 And EntityVisible(Cam, l\OBJ)
										If (Not LightOBJHidden) Then HideEntity(l\OBJ)
										PlaySound2(snd_I\LightSFX[Rand(0, 2)], Cam, l\OBJ, 4.0)
										Random = Rnd(0.35, 0.8)
										SecondaryLightOn = Clamp(SecondaryLightOn - Random, 0.301, 1.0)
										TempLightVolume = Clamp(TempLightVolume - Random, 0.5, 1.0)
									EndIf
								Else
									; ~ Hide the light emitter because it is too far
									If (Not LightOBJHidden) Then HideEntity(l\OBJ)
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		Else
			; ~ The lights were turned off
			If l\Sprite <> 0
				If (Not EntityHidden(l\Sprite)) Then HideEntity(l\Sprite)
				If (Not EntityHidden(l\AdvancedSprite)) Then HideEntity(l\AdvancedSprite)
			EndIf
			If (Not EntityHidden(l\OBJ)) Then HideEntity(l\OBJ)
		EndIf
	Next
End Function

Function RemoveLight%(l.Lights)
	If l\Sprite <> 0
		FreeEntity(l\Sprite) : l\Sprite = 0
		FreeEntity(l\AdvancedSprite) : l\AdvancedSprite = 0
	EndIf
	FreeEntity(l\OBJ) : l\OBJ = 0
	Delete(l)
End Function

Global AmbientLightRoomTex%

Function AmbientLightRooms%(R%, G%, B%)
	; ~ Save the current backbuffer
	Local OldBuffer% = BackBuffer() ; ~ Probably shouldn't make assumptions here but who cares, why wouldn't it use the BackBuffer()
	
	; ~ Change draw target to AmbientLightRoomTex
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	; ~ Clear color to provided values (R, G, B)
	ClsColor(R, G, B)
	Cls()
	; ~ Reset clear color to black (default)
	ClsColor(0, 0, 0)
	; ~ Restore the previous buffer
	SetBuffer(OldBuffer)
End Function

Const RoomScale# = 8.0 / 2048.0

Type SoundEmitters
	Field OBJ%
	Field ID%
	Field Range#
	Field SoundCHN%
	Field room.Rooms
End Type

Type TempSoundEmitters
	Field x#, y#, z#
	Field ID%
	Field Range#
	Field RoomTemplate.RoomTemplates
End Type

Function CreateSoundEmitter.SoundEmitters(room.Rooms, ID%, x#, y#, z#, Range#)
	Local se.SoundEmitters
	
	se.SoundEmitters = New SoundEmitters
	se\room = room
	
	se\OBJ = CreatePivot()
	PositionEntity(se\OBJ, x, y, z)
	If room <> Null Then EntityParent(se\OBJ, room\OBJ)
	
	se\ID = ID
	se\Range = Range
	
	Return(se)
End Function

Function UpdateSoundEmitters%()
	Local se.SoundEmitters
	
	For se.SoundEmitters = Each SoundEmitters
		If se\room <> Null
			If se\room\Dist < 6.0 Lor se\room = PlayerRoom
				If EntityDistanceSquared(se\OBJ, me\Collider) < PowTwo(se\Range) Then se\SoundCHN = LoopSound2(RoomAmbience[se\ID - 1], se\SoundCHN, Camera, se\OBJ, se\Range)
			EndIf
		EndIf
	Next
End Function

Function RemoveSoundEmitter%(se.SoundEmitters)
	FreeEntity(se\OBJ) : se\OBJ = 0
	Delete(se)
End Function

Function LoadRMesh%(File$, rt.RoomTemplates, HasCollision% = True)
	CatchErrors("LoadRMesh(" + File + ")")
	
	Local mat.Materials
	
	ClsColor(0, 0, 0)
	
	Local i%, j%, k%, x#, y#, z#
	Local Vertex%
	Local Temp1i% = 0, Temp2i% = 0, Temp3i% = 0
	Local Temp1s$
	Local CollisionMeshes% = CreatePivot()
	;Local HasTriggerBox% = False
	; ~ Read the file
	Local f% = ReadFile_Strict(File)
	
	If f = 0 Then RuntimeError(Format(GetLocalString("runerr", "file"), File))
	
	Local IsRMesh$ = ReadString(f)
	
	If IsRMesh = "RoomMesh"
		; ~ Continue
	;ElseIf IsRMesh = "RoomMesh.HasTriggerBox"
	;	HasTriggerBox = True
	Else
		RuntimeError(Format(Format(GetLocalString("runerr", "notrmesh"), File, "{0}"), IsRMesh, "{1}"))
	EndIf
	
	Local FilePath$ = StripFileName(File)
	
	; ~ Drawn meshes
	Local Opaque% = CreateMesh(), Alpha% = CreateMesh()
	
	Local ChildMesh%
	Local Surf%, Tex%[2], Brush%
	Local IsAlpha%
	Local u#, v#
	
	Local Count% = ReadInt(f)
	Local Count2%
	
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
				If FileType(FilePath + Temp1s) = 1 ; ~ Check if texture is existing in original path
					If Temp1i < 3
						If Instr(Temp1s, "_lm") <> 0
							Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s, 1 + 256)
						Else
							Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s)
						EndIf
					Else
						Tex[j] = LoadTextureCheckingIfInCache(FilePath + Temp1s, 3)
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
				BrushTexture(Brush, Tex[1], 0, 0)
			Else
				BrushTexture(Brush, MissingTexture, 0, 0)
			EndIf
		Else
			Local BumpTex% = 0
			
			If Tex[0] <> 0 And Tex[1] <> 0
				If opt\BumpEnabled
					Local Temp$ = StripPath(TextureName(Tex[1]))
					
					For mat.Materials = Each Materials
						If mat\Name = Temp
							BumpTex = mat\Bump
							Exit
						EndIf
					Next
				Else
					BumpTex = 0
				EndIf
				If BumpTex = 0
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
				If opt\BumpEnabled
					If Tex[1] <> 0
						Temp = StripPath(TextureName(Tex[1]))
						For mat.Materials = Each Materials
							If mat\Name = Temp
								BumpTex = mat\Bump
								Exit
							EndIf
						Next
					EndIf
				Else
					BumpTex = 0
				EndIf
				If BumpTex = 0
					For j = 0 To 1
						If Tex[j] <> 0
							BrushTexture(Brush, Tex[j], 0, j)
						Else
							BrushTexture(Brush, MissingTexture, 0, j)
						EndIf
					Next
				Else
					TextureCoords(BumpTex, 0)
					For j = 0 To 1
						If Tex[j] <> 0
							BrushTexture(Brush, Tex[j], 0, j + 1)
						Else
							BrushTexture(Brush, MissingTexture, 0, j + 1)
						EndIf
					Next
					BrushTexture(Brush, BumpTex, 0, 0)
				EndIf
			EndIf
		EndIf
		
		Surf = CreateSurface(ChildMesh) ; ~ Check if this don't needed anymore
		
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
			EntityAlpha(ChildMesh, 0.0)
		Else
			AddMesh(ChildMesh, Opaque)
			EntityParent(ChildMesh, CollisionMeshes)
			EntityAlpha(ChildMesh, 0.0)
			EntityType(ChildMesh, HasCollision) ; ~ DO NOT FORGET THAT Const HIT_MAP% = 1
			EntityPickMode(ChildMesh, 2)
			
			; ~ Make collision double-sided
			If HasCollision
				Local FlipChild% = CopyMesh(ChildMesh)
				
				FlipMesh(FlipChild)
				AddMesh(FlipChild, ChildMesh)
				FreeEntity(FlipChild) : FlipChild = 0
			EndIf
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
	;If HasTriggerBox
	;	Local TB%
	;	
	;	rt\TempTriggerBoxAmount = ReadInt(f)
	;	For TB = 0 To rt\TempTriggerBoxAmount - 1
	;		rt\TempTriggerBox[TB] = CreateMesh(rt\OBJ)
	;		Count = ReadInt(f)
	;		For i = 1 To Count
	;			Surf = CreateSurface(rt\TempTriggerBox[TB])
	;			Count2 = ReadInt(f)
	;			For j = 1 To Count2
	;				x = ReadFloat(f) : y = ReadFloat(f) : z = ReadFloat(f)
	;				Vertex = AddVertex(Surf, x, y, z)
	;			Next
	;			Count2 = ReadInt(f)
	;			For j = 1 To Count2
	;				Temp1i = ReadInt(f) : Temp2i = ReadInt(f) : Temp3i = ReadInt(f)
	;				AddTriangle(Surf, Temp1i, Temp2i, Temp3i)
	;				AddTriangle(Surf, Temp1i, Temp3i, Temp2i)
	;			Next
	;		Next
	;		rt\TempTriggerBoxName[TB] = ReadString(f)
	;	Next
	;EndIf
	
	Count = ReadInt(f) ; ~ Point entities
	
	Local ts.TempScreens, twp.TempWayPoints, tl.TempLights, tse.TempSoundEmitters, tp.TempProps
	Local Range#, lColor$, Intensity#
	Local R%, G%, B%
	Local Angles$
	Local Temp2s$
	
	If rt <> Null ; ~ TEMPORARY SOLUTION
		For i = 1 To Count
			Temp1s = ReadString(f)
			Select Temp1s
				Case "screen"
					;[Block]
					ts.TempScreens = New TempScreens
					ts\RoomTemplate = rt
					
					ts\x = ReadFloat(f) * RoomScale
					ts\y = ReadFloat(f) * RoomScale
					ts\z = ReadFloat(f) * RoomScale
					
					Temp2s = ReadString(f)
					ts\ImgPath = Temp2s
					;[End Block]
				Case "waypoint"
					;[Block]
					twp.TempWayPoints = New TempWayPoints
					twp\RoomTemplate = rt
					
					twp\x = ReadFloat(f) * RoomScale
					twp\y = ReadFloat(f) * RoomScale
					twp\z = ReadFloat(f) * RoomScale
					;[End Block]
				Case "light"
					;[Block]
					tl.TempLights = New TempLights
					tl\RoomTemplate = rt
					
					tl\x = ReadFloat(f) * RoomScale
					tl\y = ReadFloat(f) * RoomScale
					tl\z = ReadFloat(f) * RoomScale
					tl\lType = 2
					tl\Range = ReadFloat(f) / 2000.0
					
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					tl\R = Int(Piece(lColor, 1, " ")) * Intensity
					tl\G = Int(Piece(lColor, 2, " ")) * Intensity
					tl\B = Int(Piece(lColor, 3, " ")) * Intensity
					
					tl\HasSprite = True
					;[End Block]
				Case "light_fix"
					;[Block]
					tl.TempLights = New TempLights
					tl\RoomTemplate = rt
					
					tl\x = ReadFloat(f) * RoomScale
					tl\y = ReadFloat(f) * RoomScale
					tl\z = ReadFloat(f) * RoomScale
					tl\lType = 2
					
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					tl\Range = ReadFloat(f) / 2000.0
					tl\R = Int(Piece(lColor, 1, " ")) * Intensity
					tl\G = Int(Piece(lColor, 2, " ")) * Intensity
					tl\B = Int(Piece(lColor, 3, " ")) * Intensity
					
					tl\HasSprite = False
					;[End Block]
				Case "spotlight"
					;[Block]
					tl.TempLights = New TempLights
					tl\RoomTemplate = rt
					
					tl\x = ReadFloat(f) * RoomScale
					tl\y = ReadFloat(f) * RoomScale
					tl\z = ReadFloat(f) * RoomScale
					tl\lType = 3
					tl\Range = ReadFloat(f) / 2000.0
					
					lColor = ReadString(f)
					Intensity = Min(ReadFloat(f) * 0.8, 1.0)
					tl\R = Int(Piece(lColor, 1, " ")) * Intensity
					tl\G = Int(Piece(lColor, 2, " ")) * Intensity
					tl\B = Int(Piece(lColor, 3, " ")) * Intensity
					
					Angles = ReadString(f)
					tl\Pitch = Piece(Angles, 1, " ")
					tl\Yaw = Piece(Angles, 2, " ")
					
					tl\InnerConeAngle = ReadInt(f)
					tl\OuterConeAngle = ReadInt(f)
					;[End Block]
				Case "soundemitter"
					;[Block]
					tse.TempSoundEmitters = New TempSoundEmitters
					tse\RoomTemplate = rt
					
					tse\x = ReadFloat(f) * RoomScale
					tse\y = ReadFloat(f) * RoomScale
					tse\z = ReadFloat(f) * RoomScale
					
					tse\ID = ReadInt(f)
					
					tse\Range = ReadFloat(f)
					;[End Block]
				Case "model"
					;[Block]
					Temp2s = ReadString(f)
					RuntimeError(Format(Format(GetLocalString("runerr", "model.support"), rt\Name, "{0}"), "GFX\Map\Props\" + Temp2s, "{1}"))
					;[End Block]
				Case "mesh"
					;[Block]
					tp.TempProps = New TempProps
					tp\RoomTemplate = rt
					
					tp\x = ReadFloat(f) * RoomScale
					tp\y = ReadFloat(f) * RoomScale
					tp\z = ReadFloat(f) * RoomScale
					
					Temp2s = ReadString(f)
					; ~ A hacky way to use .b3d format
					If FileExtension(Temp2s) = "x"
						Temp2s = Left(Temp2s, Len(Temp2s) - 2)
					ElseIf FileExtension(Temp2s) = "b3d"
						Temp2s = Left(Temp2s, Len(Temp2s) - 4)
					EndIf
					tp\Name = "GFX\Map\Props\" + Temp2s + ".b3d"
					
					tp\Pitch = ReadFloat(f)
					tp\Yaw = ReadFloat(f)
					tp\Roll = ReadFloat(f)
					
					tp\ScaleX = ReadFloat(f)
					tp\ScaleY = ReadFloat(f)
					tp\ScaleZ = ReadFloat(f)
					
					tp\HasCollision = ReadByte(f)
					tp\FX = ReadInt(f)
					tp\Texture = ReadString(f)
					;[End Block]
			End Select
		Next
	EndIf
	
	Temp1i = CopyMesh(Alpha)
	FlipMesh(Temp1i)
	AddMesh(Temp1i, Alpha)
	FreeEntity(Temp1i) : Temp1i = 0
	
	If Brush <> 0 Then FreeBrush(Brush) : Brush = 0
	
	AddMesh(Alpha, Opaque)
	FreeEntity(Alpha) : Alpha = 0
	
	EntityFX(Opaque, 3)
	
	EntityAlpha(HiddenMesh, 0.0)
	EntityType(HiddenMesh, HasCollision) ; ~ DO NOT FORGET THAT Const HIT_MAP% = 1
	EntityAlpha(Opaque, 1.0)
	
	Local OBJ% = CreatePivot()
	
	CreatePivot(OBJ) ; ~ Skip "meshes" object
	EntityParent(Opaque, OBJ)
	EntityParent(HiddenMesh, OBJ)
	CreatePivot(OBJ) ; ~ Skip "pointentites" object
	CreatePivot(OBJ) ; ~ Skip "solidentites" object
	EntityParent(CollisionMeshes, OBJ)
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadRMesh(" + File + ")")
	
	Return(OBJ)
End Function

Const ForestGridSize% = 10

Type Forest
	Field TileMesh%[5]
	Field DetailMesh%[4]
	Field Grid%[PowTwo(ForestGridSize) + 11]
	Field TileEntities%[PowTwo(ForestGridSize) + 1]
	Field Forest_Pivot%
	Field ForestDoors.Doors[2]
	Field DetailEntities%[2]
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
Const MinDoorPos% = 3, MaxDoorPos% = 7
;[End Block]

Function GenForestGrid%(fr.Forest)
	CatchErrors("GenForestGrid()")
	
	Local Door1Pos%, Door2Pos%
	Local i%, j%
	
	Door1Pos = Rand(MinDoorPos, MaxDoorPos)
	Door2Pos = Rand(MinDoorPos, MaxDoorPos)
	
	; ~ Clear the grid
	For i = 0 To ForestGridSize - 1
		For j = 0 To ForestGridSize - 1
			fr\Grid[(j * ForestGridSize) + i] = 0
		Next
	Next
	
	; ~ Set the position of the concrete and doors
	fr\Grid[Door1Pos] = 3
	fr\Grid[((ForestGridSize - 1) * ForestGridSize) + Door2Pos] = 3
	
	; ~ Generate the path
	Local PathX% = Door2Pos
	Local PathY% = 1
	Local Dir% = 1 ; ~ 0 = left, 1 = up, 2 = right
	
	fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	
	Local Deviated%
	
	While PathY < ForestGridSize - 4
		If Dir = 1 ; ~ Determine whether to go forward or to the side
			If Chance(Deviation_Chance)
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
			If Dir = 1
				fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
				PathX = MoveForward(Dir, PathX, PathY)
				PathY = MoveForward(Dir, PathX, PathY, True)
			EndIf
		EndIf
		; ~ Add our position to the grid
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	; ~ Finally, bring the path back to the door now that we have reached the end
	Dir = 1
	While PathY < ForestGridSize - 2
		PathX = MoveForward(Dir, PathX, PathY)
		PathY = MoveForward(Dir, PathX, PathY, True)
		fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
	Wend
	
	If PathX <> Door1Pos
		Dir = 0
		If Door1Pos > PathX Then Dir = 2
		While PathX <> Door1Pos
			PathX = MoveForward(Dir, PathX, PathY)
			PathY = MoveForward(Dir, PathX, PathY, True)
			fr\Grid[((ForestGridSize - 1 - PathY) * ForestGridSize) + PathX] = 1
		Wend
	EndIf
	
	; ~ Attempt to create new branches
	Local NewY%, TempY%, NewX%
	Local BranchPos%, LeftMost%, RightMost%
	
	NewY = -3 ; ~ Used for counting off; branches will only be considered once every 4 units so as to avoid potentially too many branches
	While NewY < ForestGridSize - 6
		NewY = NewY + 4
		TempY = NewY
		NewX = 0 
		If Chance(Branch_Chance)
			; ~ Create a branch at this spot
			; ~ Determine if on left or on right
			BranchPos = 2 * Rand(0, 1)
			; ~ Get leftmost or rightmost path in this row
			LeftMost = ForestGridSize - 1
			RightMost = 0
			For i = 0 To ForestGridSize - 1
				If fr\Grid[((ForestGridSize - 1 - NewY) * ForestGridSize) + i] = 1
					LeftMost = Min(LeftMost, i)
					RightMost = Max(RightMost, i)
				EndIf
			Next
			If BranchPos = 0
				NewX = LeftMost - 1
			Else
				NewX = RightMost + 1
			EndIf
			; ~ Before creating a branch make sure it won't pass the border and there are no 1's above or below
			If NewX >= 0 And NewX < ForestGridSize And fr\Grid[((ForestGridSize - 1 - TempY - 1) * ForestGridSize) + NewX] <> 1 And fr\Grid[((ForestGridSize - 1 - TempY + 1) * ForestGridSize) + NewX] <> 1
				fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Make -1s so you don't confuse your branch for a path; will be changed later
				If BranchPos = 0
					NewX = LeftMost - 2
				Else
					NewX = RightMost + 2
				EndIf
				; ~ Before continuing the branch make sure it won't pass the border
				If NewX >= 0 And NewX < ForestGridSize
					fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Branch out twice to avoid creating an unwanted 2x2 path with the real path
					i = 2
					While i < Branch_Max_Life
						i = i + 1
						If Chance(Branch_Die_Chance) Then Exit
						If Rand(0, 3) = 0 ; ~ Have a higher chance to go up to confuse the player
							NewX = NewX + (1 - 2 * (BranchPos = 0))
						Else
							TempY = TempY + 1
						EndIf
						
						; ~ before continuing the branch make sure it won't pass the border and there are no 1's above
						If NewX < 0 Lor NewX >= ForestGridSize Lor fr\Grid[((ForestGridSize - 1 - TempY - 1) * ForestGridSize) + NewX] = 1 Then Exit
						
						fr\Grid[((ForestGridSize - 1 - TempY) * ForestGridSize) + NewX] = -1 ; ~ Make -1s so you don't confuse your branch for a path; will be changed later
						If TempY >= ForestGridSize - 2 Then Exit
					Wend
				EndIf
			EndIf
		EndIf
	Wend
	
	If opt\DebugMode
		Local x%, y%
		
		Repeat
			ShowPointer()
			Cls()
			
			MousePosX = ScaledMouseX()
			MousePosY = ScaledMouseY()
			
			i = ForestGridSize - 1
			For x = 0 To ForestGridSize - 1
				For y = 0 To ForestGridSize - 1
					If fr\Grid[x + (y * ForestGridSize)] = 0
						Color(50, 50, 50)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					Else
						Color(255, 255, 255)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					EndIf
				Next
				i = i - 1
			Next
			
			i = ForestGridSize - 1
			For x = 0 To ForestGridSize - 1
				For y = 0 To ForestGridSize - 1
					If MouseOn((i * 32) * MenuScale, (y * 32) * MenuScale, 32 * MenuScale, 32 * MenuScale)
						Color(255, 0, 0)
					Else
						Color(0, 0, 0)
					EndIf
					TextEx(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, fr\Grid[x + (y * ForestGridSize)])
				Next
				i = i - 1
			Next
			
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"), True, True)
			
			Flip()
			RenderCursor()
		Until (GetKey() <> 0 Lor MouseHit(1))
	EndIf
	
	; ~ Change branches from -1s to 1s
	For i = 1 To ForestGridSize - 2
		For j = 0 To ForestGridSize - 1
			If fr\Grid[(i * ForestGridSize) + j] = -1 Then fr\Grid[(i * ForestGridSize) + j] = 1
		Next
	Next
	
	CatchErrors("Uncaught: GenForestGrid()")
End Function

; ~ Shapes ID Constants
;[Block]
Const ROOM1% = 0
Const ROOM2% = 1
Const ROOM2C% = 2
Const ROOM3% = 3
Const ROOM4% = 4
;[End Block]

Function PlaceForest%(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("PlaceForest(" + x + ", " + y + ", " + z + ")")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%
	Local Tile_Entity%, Detail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, lX%, lY%, d%
	
	DestroyForest(fr, False)
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	; ~ Load assets
	Local hMap%[5], Mask%[5]
	Local GroundTexture% = LoadTexture_Strict("GFX\Map\Textures\forestfloor.jpg", 1 + 256)
	Local PathTexture% = LoadTexture_Strict("GFX\Map\Textures\forestpath.jpg", 1 + 256)
	
	hMap[ROOM1] = LoadImage_Strict("GFX\Map\Forest\forest1h.png")
	Mask[ROOM1] = LoadTexture_Strict("GFX\Map\Forest\forest1h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM2] = LoadImage_Strict("GFX\Map\Forest\forest2h.png")
	Mask[ROOM2] = LoadTexture_Strict("GFX\Map\Forest\forest2h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM2C] = LoadImage_Strict("GFX\Map\Forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture_Strict("GFX\Map\Forest\forest2Ch_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM3] = LoadImage_Strict("GFX\Map\Forest\forest3h.png")
	Mask[ROOM3] = LoadTexture_Strict("GFX\Map\Forest\forest3h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM4] = LoadImage_Strict("GFX\Map\Forest\forest4h.png")
	Mask[ROOM4] = LoadTexture_Strict("GFX\Map\Forest\forest4h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
		HideEntity(fr\TileMesh[i])
		DeleteSingleTextureEntryFromCache(Mask[i]) : Mask[i] = 0
	Next
	DeleteSingleTextureEntryFromCache(GroundTexture) : GroundTexture = 0
	DeleteSingleTextureEntryFromCache(PathTexture) : PathTexture = 0
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\Map\Props\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\Map\Props\rock.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\Map\Props\tree2.b3d")
	fr\DetailMesh[3] = LoadRMesh("GFX\Map\cont2_860_1_wall.rmesh", Null)
	
	For i = 0 To 3
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 1 To ForestGridSize - 2
			If fr\Grid[(tY * ForestGridSize) + tX] = 1
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
						
						If fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + (tX - 1)] > 0
							Angle = 270.0
						ElseIf fr\Grid[(tY * ForestGridSize) + (tX + 1)] > 0
							Angle = 90.0
						EndIf
						
						Tile_Type = ROOM1 + 1
						;[End Block]
					Case 2
						;[Block]
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Tile_Type = ROOM2 + 1
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] > 0 And fr\Grid[(tY * ForestGridSize) + tX - 1] > 0
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2])
							Angle = 90.0
							Tile_Type = ROOM2 + 1
						Else
							Tile_Entity = CopyEntity(fr\TileMesh[ROOM2C])
							If fr\Grid[(tY * ForestGridSize) + tX - 1] > 0 And fr\Grid[((tY + 1) * ForestGridSize) + tX] > 0
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
						
						If fr\Grid[((tY - 1) * ForestGridSize) + tX] = 0
							Angle = 180.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX - 1] = 0
							Angle = 90.0
						ElseIf fr\Grid[(tY * ForestGridSize) + tX + 1] = 0
							Angle = 270.0
						EndIf
						
						Tile_Type = ROOM3 + 1
						;[End Block]
					Case 4
						;[Block]
						Tile_Entity = CopyEntity(fr\TileMesh[ROOM4])
						
						Angle = (fr\Grid[(tY * ForestGridSize) + tX] Mod 4) * 90.0
						
						Tile_Type = ROOM4 + 1
						;[End Block]
				End Select
				
				If Tile_Type > 0
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							
							Local ColorR% = ColorRed()
							Local DetailEntityPosX# = (lX * Tempf4) - (Tempf3 / 2.0)
							Local DetailEntityPosZ# = (lY * Tempf4) - (Tempf3 / 2.0)
							
							If ColorR > Rand(100, 260)
								Detail_Entity = 0
								Select Rand(0, 7)
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[2])
											RotateEntity(d, 0.0, (90.0 * i) + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - Rnd(3.0, 3.2), DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[2])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
								End Select
								If Detail_Entity <> 0
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					
					Local ItemPlaced%[4], iX#, iZ#
					Local tYFloor% = Floor(tY / 3)
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[tYFloor])
						ItemPlaced[tYFloor] = True
						
						If Tile_Type = ROOM1 + 1
							iX = 0.4 : iZ = 0.0
						ElseIf Tile_Type = ROOM2C + 1
							iX = 1.7 : iZ = -1.0
						Else
							iX = 0.0 : iZ = 0.0
						EndIf
						it.Items = CreateItem("Log #" + Int(tYFloor + 1), it_paper, iX, 0.2, iZ)
						EntityType(it\Collider, HIT_ITEM)
						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
					HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
			EndIf
		Next
	Next
	FreeImage(hMap[i]) : hMap[i] = 0
	
	; ~ Place the wall
	For i = 0 To 1
		tY = i * (ForestGridSize - 1)
		For tX = MinDoorPos To MaxDoorPos
			If fr\Grid[(tY * ForestGridSize) + tX] = 3
				fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[3])
				ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
				
				fr\ForestDoors[i] = CreateDoor(Null, 0.0, 32.0 * RoomScale, 0.0, 180.0, False, WOODEN_DOOR, KEY_860, "", fr\DetailEntities[i])
				fr\ForestDoors[i]\Locked = 2
				
				EntityType(fr\DetailEntities[i], HIT_MAP)
				EntityPickMode(fr\DetailEntities[i], 2)
				PositionEntity(fr\DetailEntities[i], x + (tX * Tile_Size), y, z + (tY * Tile_Size) + (Tile_Size / 2) - (Tile_Size * i), True)
				RotateEntity(fr\DetailEntities[i], 0.0, 180.0 * i, 0.0)
				EntityParent(fr\DetailEntities[i], fr\Forest_Pivot)
				Exit
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: PlaceForest(" + x + ", " + y + ", " + z + ")")
End Function

Function PlaceMapCreatorForest%(fr.Forest, x#, y#, z#, r.Rooms)
	CatchErrors("PlaceMapCreatorForest(" + x + ", " + y + ", " + z + ")")
	
	Local tX%, tY%
	Local Tile_Size# = 12.0
	Local Tile_Type%, Detail_Entity%
	Local Tile_Entity%, Eetail_Entity%
	Local Tempf1#, Tempf2#, Tempf3#, Tempf4#
	Local i%, Width%, lX%, lY%, d%
	
	DestroyForest(fr, False)
	
	fr\Forest_Pivot = CreatePivot()
	PositionEntity(fr\Forest_Pivot, x, y, z, True)
	
	Local hMap%[5], Mask%[5]
	; ~ Load assets
	Local GroundTexture% = LoadTexture_Strict("GFX\Map\Textures\forestfloor.jpg", 1 + 256)
	Local PathTexture% = LoadTexture_Strict("GFX\Map\Textures\forestpath.jpg", 1 + 256)
	
	hMap[ROOM1] = LoadImage_Strict("GFX\Map\Forest\forest1h.png")
	Mask[ROOM1] = LoadTexture_Strict("GFX\Map\Forest\forest1h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM2] = LoadImage_Strict("GFX\Map\Forest\forest2h.png")
	Mask[ROOM2] = LoadTexture_Strict("GFX\Map\Forest\forest2h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM2C] = LoadImage_Strict("GFX\Map\Forest\forest2Ch.png")
	Mask[ROOM2C] = LoadTexture_Strict("GFX\Map\Forest\forest2Ch_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM3] = LoadImage_Strict("GFX\Map\Forest\forest3h.png")
	Mask[ROOM3] = LoadTexture_Strict("GFX\Map\Forest\forest3h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	hMap[ROOM4] = LoadImage_Strict("GFX\Map\Forest\forest4h.png")
	Mask[ROOM4] = LoadTexture_Strict("GFX\Map\Forest\forest4h_mask.png", 1 + 2 + 256, DeleteMapTextures, False)
	
	For i = ROOM1 To ROOM4
		fr\TileMesh[i] = LoadTerrain(hMap[i], 0.03, GroundTexture, PathTexture, Mask[i])
		HideEntity(fr\TileMesh[i])
		DeleteSingleTextureEntryFromCache(Mask[i]) : Mask[i] = 0
	Next
	DeleteSingleTextureEntryFromCache(GroundTexture) : GroundTexture = 0
	DeleteSingleTextureEntryFromCache(PathTexture) : PathTexture = 0
	
	; ~ Detail meshes
	fr\DetailMesh[0] = LoadMesh_Strict("GFX\Map\Props\tree1.b3d")
	fr\DetailMesh[1] = LoadMesh_Strict("GFX\Map\Props\rock.b3d")
	fr\DetailMesh[2] = LoadMesh_Strict("GFX\Map\Props\tree2.b3d")
	fr\DetailMesh[3] = LoadRMesh("GFX\Map\cont2_860_1_wall.rmesh", Null)
	
	For i = 0 To 3
		HideEntity(fr\DetailMesh[i])
	Next
	
	Tempf3 = MeshWidth(fr\TileMesh[ROOM1])
	Tempf1 = Tile_Size / Tempf3
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\Grid[(tY * ForestGridSize) + tX] > 0
				Tile_Type = 0
				
				Local Angle# = 0.0
				
				Tile_Type = Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0)
				If Tile_Type = 6 Then Tile_Type = 2
				Angle = (fr\Grid[(tY * ForestGridSize) + tX] Mod 4) * 90.0
				
				Tile_Entity = CopyEntity(fr\TileMesh[Tile_Type - 1])
				
				If Tile_Type > 0
					; ~ Place trees and other details
					; ~ Only placed on spots where the value of the heightmap is above 100
					SetBuffer(ImageBuffer(hMap[Tile_Type - 1]))
					Width = ImageWidth(hMap[Tile_Type - 1])
					Tempf4 = (Tempf3 / Float(Width))
					For lX = 3 To Width - 2
						For lY = 3 To Width - 2
							GetColor(lX, Width - lY)
							
							Local ColorR% = ColorRed()
							Local DetailEntityPosX# = (lX * Tempf4) - (Tempf3 / 2.0)
							Local DetailEntityPosZ# = (lY * Tempf4) - (Tempf3 / 2.0)
							
							If ColorR > Rand(100, 260)
								Detail_Entity = 0
								Select Rand(0, 7)
									Case 0, 1, 2, 3, 4, 5, 6 ; ~ Create a tree
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[0])
										Tempf2 = Rnd(0.25, 0.4)
										For i = 0 To 3
											d = CopyEntity(fr\DetailMesh[2])
											RotateEntity(d, 0.0, (90.0 * i) + Rnd(-20.0, 20.0), 0.0)
											EntityParent(d, Detail_Entity)
											EntityFX(d, 1)
										Next
										ScaleEntity(Detail_Entity, Tempf2 * 1.1, Tempf2, Tempf2 * 1.1, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - Rnd(3.0, 3.2), DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, Rnd(-5.0, 5.0), Rnd(360.0), 0.0, True)
										;[End Block]
									Case 6 ; ~ Add a stump
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[2])
										Tempf2 = Rnd(0.1, 0.12)
										ScaleEntity(Detail_Entity, Tempf2, Tempf2, Tempf2, True)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										;[End Block]
									Case 7 ; ~ Add a rock
										;[Block]
										Detail_Entity = CopyEntity(fr\DetailMesh[1])
										Tempf2 = Rnd(0.01, 0.012)
										PositionEntity(Detail_Entity, DetailEntityPosX, ColorR * 0.03 - 1.3, DetailEntityPosZ, True)
										RotateEntity(Detail_Entity, 0.0, Rnd(360.0), 0.0, True)
										;[End Block]
								End Select
								If Detail_Entity <> 0
									EntityFX(Detail_Entity, 1)
									EntityParent(Detail_Entity, Tile_Entity)
								EndIf
							EndIf
						Next
					Next
					SetBuffer(BackBuffer())
					
					ScaleEntity(Tile_Entity, Tempf1, Tempf1, Tempf1)
					
					Local ItemPlaced%[4], iX#, iZ#
					Local tYFloor% = Floor(tY / 3)
					Local it.Items = Null
					
					If (tY Mod 3) = 2 And (Not ItemPlaced[tYFloor])
						ItemPlaced[tYFloor] = True
						
						If Tile_Type = ROOM1 + 1
							iX = 0.4 : iZ = 0.0
						ElseIf Tile_Type = ROOM2C + 1
							iX = 1.7 : iZ = -1.0
						Else
							iX = 0.0 : iZ = 0.0
						EndIf
						it.Items = CreateItem("Log #" + Int(tYFloor + 1), it_paper, iX, 0.2, iZ)
						EntityType(it\Collider, HIT_ITEM)
						EntityParent(it\Collider, Tile_Entity)
					EndIf
					
					TurnEntity(Tile_Entity, 0.0, Angle, 0.0)
					PositionEntity(Tile_Entity, x + (tX * Tile_Size), y, z + (tY * Tile_Size), True)
					EntityType(Tile_Entity, HIT_MAP)
					EntityFX(Tile_Entity, 1)
					EntityParent(Tile_Entity, fr\Forest_Pivot)
					EntityPickMode(Tile_Entity, 2)
					
					If it <> Null Then EntityParent(it\Collider, 0)
					
					fr\TileEntities[tX + (tY * ForestGridSize)] = Tile_Entity
					HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
				
				If Ceil(Float(fr\Grid[(tY * ForestGridSize) + tX]) / 4.0) = 6
					For i = 0 To 1
						If fr\ForestDoors[i] = Null
							fr\DetailEntities[i] = CopyEntity(fr\DetailMesh[3])
							ScaleEntity(fr\DetailEntities[i], RoomScale, RoomScale, RoomScale)
							
							fr\ForestDoors[i] = CreateDoor(Null, 0.0, 32.0 * RoomScale, 0.0, 180.0, False, WOODEN_DOOR, KEY_860, "", fr\DetailEntities[i])
							fr\ForestDoors[i]\Locked = 2
							
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
	FreeImage(hMap[i]) : hMap[i] = 0
	
	CatchErrors("Uncaught: PlaceMapCreatorForest(" + x + ", " + y + ", " + z + ")")
End Function

Function DestroyForest%(fr.Forest, RemoveGrid% = True)
	CatchErrors("DestroyForest(" + RemoveGrid + ")")
	
	Local tX%, tY%, i%
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0
				FreeEntity(fr\TileEntities[tX + (tY * ForestGridSize)]) : fr\TileEntities[tX + (tY * ForestGridSize)] = 0
				If RemoveGrid Then fr\Grid[tX + (tY * ForestGridSize)] = 0
			EndIf
		Next
	Next
	For i = 0 To 1
		If fr\ForestDoors[i] <> Null Then RemoveDoor(fr\ForestDoors[i])
		If fr\DetailEntities[i] <> 0 Then FreeEntity(fr\DetailEntities[i]) : fr\DetailEntities[i] = 0
	Next
	If fr\Forest_Pivot <> 0 Then FreeEntity(fr\Forest_Pivot) : fr\Forest_Pivot = 0
	For i = ROOM1 To ROOM4
		If fr\TileMesh[i] <> 0 Then FreeEntity(fr\TileMesh[i]) : fr\TileMesh[i] = 0
	Next
	For i = 0 To 2 Step 2
		If fr\DetailMesh[i] <> 0 Then FreeEntity(fr\DetailMesh[i]) : fr\DetailMesh[i] = 0
		If fr\DetailMesh[i + 1] <> 0 Then FreeEntity(fr\DetailMesh[i + 1]) : fr\DetailMesh[i + 1] = 0
	Next
	
	CatchErrors("Uncaught: DestroyForest(" + RemoveGrid + ")")
End Function

Function UpdateForest%(fr.Forest)
	CatchErrors("UpdateForest()")
	
	Local tX%, tY%
	
	For tX = 0 To ForestGridSize - 1
		For tY = 0 To ForestGridSize - 1
			If fr\TileEntities[tX + (tY * ForestGridSize)] <> 0
				If DistanceSquared(EntityX(me\Collider, True), EntityX(fr\TileEntities[tX + (tY * ForestGridSize)], True), EntityZ(me\Collider, True), EntityZ(fr\TileEntities[tX + (tY * ForestGridSize)], True)) < PowTwo(HideDistance)
					If EntityHidden(fr\TileEntities[tX + (tY * ForestGridSize)]) Then ShowEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				Else
					If (Not EntityHidden(fr\TileEntities[tX + (tY * ForestGridSize)])) Then HideEntity(fr\TileEntities[tX + (tY * ForestGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: UpdateForest()")
End Function

Global RoomTempID%
Global RoomAmbience%[11]

Type RoomTemplates
	Field OBJ%, ID%
	Field OBJPath$
	Field Zone%[5]
	Field Shape%, Name$, RoomID% ; ~ Name is for debugging
	Field Commonness%
	Field DisableDecals%
	;Field TempTriggerBoxAmount%
	;Field TempTriggerBox%[8]
	;Field TempTriggerBoxName$[8]
	Field DisableOverlapCheck% = True
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
End Type

; ~ Room ID constants
;[Block]
; ~ LCZ
Const r_room1_storage% = 0
Const r_room1_dead_end_lcz% = 1
Const r_cont1_005% = 2
Const r_cont1_173% = 3, r_cont1_173_intro% = 4, r_cont1_205% = 5, r_cont1_914% = 6
Const r_room2_lcz% = 7, r_room2_2_lcz% = 8, r_room2_3_lcz% = 9, r_room2_4_lcz% = 10, r_room2_5_lcz% = 11, r_room2_6_lcz% = 12
Const r_room2_closets% = 13
Const r_room2_elevator% = 14
Const r_room2_gw% = 15, r_room2_gw_2% = 16
Const r_room2_js% = 17
Const r_room2_sl% = 18
Const r_room2_storage% = 19
Const r_room2_tesla_lcz% = 20
Const r_room2_test_lcz% = 21
Const r_cont2_012% = 22, r_cont2_427_714_860_1025% = 23, r_cont2_500_1499% = 24, r_cont2_1123% = 25
Const r_room2c_lcz% = 26, r_room2c_2_lcz% = 27
Const r_room2c_gw_lcz% = 28, r_room2c_gw_2_lcz% = 29
Const r_cont2c_066_1162_arc% = 30
Const r_room3_storage% = 31
Const r_room3_lcz% = 32, r_room3_2_lcz% = 33, r_room3_3_lcz% = 34
Const r_cont3_372% = 35
Const r_room4_lcz% = 36, r_room4_2_lcz% = 37
Const r_room4_ic% = 38
; ~ CHECKPOINT
Const r_room2_checkpoint_lcz_hcz% = 39
; ~ HCZ
Const r_room1_dead_end_hcz% = 40
Const r_cont1_035% = 41, r_cont1_079% = 42, r_cont1_106% = 43, r_cont1_895% = 44
Const r_room2_hcz% = 45, r_room2_2_hcz% = 46, r_room2_3_hcz% = 47, r_room2_4_hcz% = 48, r_room2_5_hcz% = 49, r_room2_6_hcz% = 50
Const r_room2_mt% = 51
Const r_room2_nuke% = 52
Const r_room2_servers_hcz% = 53
Const r_room2_shaft% = 54
Const r_room2_tesla_hcz% = 55
Const r_room2_test_hcz% = 56
Const r_cont2_008% = 57, r_cont2_049% = 58, r_cont2_409% = 59
Const r_room2c_hcz% = 60
Const r_cont2c_096% = 61
Const r_room3_hcz% = 62, r_room3_2_hcz% = 63, r_room3_3_hcz% = 64
Const r_cont3_513% = 65, r_cont3_966% = 66
Const r_room4_hcz% = 67, r_room4_2_hcz% = 68
; ~ CHECKPOINT
Const r_room2_checkpoint_hcz_ez% = 69
; ~ EZ
Const r_gate_a_entrance% = 70, r_gate_a% = 71, r_gate_b_entrance% = 72, r_gate_b% = 73
Const r_room1_dead_end_ez% = 74
Const r_room1_lifts% = 75
Const r_room1_o5% = 76
Const r_room2_ez% = 77, r_room2_2_ez% = 78, r_room2_3_ez% = 79, r_room2_4_ez% = 80, r_room2_5_ez% = 81, r_room2_6_ez% = 82
Const r_room2_cafeteria% = 83
Const r_room2_ic% = 84
Const r_room2_medibay% = 85
Const r_room2_office% = 86, r_room2_office_2% = 87, r_room2_office_3% = 88
Const r_room2_servers_ez% = 89
Const r_room2_scientists% = 90, r_room2_scientists_2% = 91
Const r_room2_tesla_ez% = 92
Const r_cont2_860_1% = 93
Const r_room2c_ez% = 94, r_room2c_2_ez% = 95
Const r_room2c_ec% = 96
Const r_room2c_gw_ez% = 97
Const r_room3_gw% = 98
Const r_room3_office% = 99
Const r_room3_ez% = 100, r_room3_2_ez% = 101, r_room3_3_ez% = 102, r_room3_4_ez% = 103
Const r_room4_ez% = 104
; ~ OTHERS
Const r_dimension_106% = 105, r_dimension_1499% = 106
;[End Block]

Function FindRoomID%(RoomName$)
	Select RoomName
		Case "room1_storage"
			;[Block]
			Return(r_room1_storage)
			;[End Block]
		Case "room1_dead_end_lcz"
			;[Block]
			Return(r_room1_dead_end_lcz)
			;[End Block]
		Case "cont1_005"
			;[Block]
			Return(r_cont1_005)
			;[End Block]
		Case "cont1_173"
			;[Block]
			Return(r_cont1_173)
			;[End Block]
		Case "cont1_173_intro"
			;[Block]
			Return(r_cont1_173_intro)
			;[End Block]
		Case "cont1_205"
			;[Block]
			Return(r_cont1_205)
			;[End Block]
		Case "cont1_914"
			;[Block]
			Return(r_cont1_914)
			;[End Block]
		Case "room2_lcz"
			;[Block]
			Return(r_room2_lcz)
			;[End Block]
		Case "room2_2_lcz"
			;[Block]
			Return(r_room2_2_lcz)
			;[End Block]
		Case "room2_3_lcz"
			;[Block]
			Return(r_room2_3_lcz)
			;[End Block]
		Case "room2_4_lcz"
			;[Block]
			Return(r_room2_4_lcz)
			;[End Block]
		Case "room2_5_lcz"
			;[Block]
			Return(r_room2_5_lcz)
			;[End Block]
		Case "room2_6_lcz"
			;[Block]
			Return(r_room2_6_lcz)
			;[End Block]
		Case "room2_closets"
			;[Block]
			Return(r_room2_closets)
			;[End Block]
		Case "room2_elevator"
			;[Block]
			Return(r_room2_elevator)
			;[End Block]
		Case "room2_gw"
			;[Block]
			Return(r_room2_gw)
			;[End Block]
		Case "room2_gw_2"
			;[Block]
			Return(r_room2_gw_2)
			;[End Block]
		Case "room2_js"
			;[Block]
			Return(r_room2_js)
			;[End Block]
		Case "room2_sl"
			;[Block]
			Return(r_room2_sl)
			;[End Block]
		Case "room2_storage"
			;[Block]
			Return(r_room2_storage)
			;[End Block]
		Case "room2_tesla_lcz"
			;[Block]
			Return(r_room2_tesla_lcz)
			;[End Block]
		Case "room2_test_lcz"
			;[Block]
			Return(r_room2_test_lcz)
			;[End Block]
		Case "cont2_012"
			;[Block]
			Return(r_cont2_012)
			;[End Block]
		Case "cont2_427_714_860_1025"
			;[Block]
			Return(r_cont2_427_714_860_1025)
			;[End Block]
		Case "cont2_500_1499"
			;[Block]
			Return(r_cont2_500_1499)
			;[End Block]
		Case "cont2_1123"
			;[Block]
			Return(r_cont2_1123)
			;[End Block]
		Case "room2c_lcz"
			;[Block]
			Return(r_room2c_lcz)
			;[End Block]
		Case "room2c_2_lcz"
			;[Block]
			Return(r_room2c_2_lcz)
			;[End Block]
		Case "room2c_gw_lcz"
			;[Block]
			Return(r_room2c_gw_lcz)
			;[End Block]
		Case "room2c_gw_2_lcz"
			;[Block]
			Return(r_room2c_gw_2_lcz)
			;[End Block]
		Case "cont2c_066_1162_arc"
			;[Block]
			Return(r_cont2c_066_1162_arc)
			;[End Block]
		Case "room3_storage"
			;[Block]
			Return(r_room3_storage)
			;[End Block]
		Case "room3_lcz"
			;[Block]
			Return(r_room3_lcz)
			;[End Block]
		Case "room3_2_lcz"
			;[Block]
			Return(r_room3_2_lcz)
			;[End Block]
		Case "room3_3_lcz"
			;[Block]
			Return(r_room3_3_lcz)
			;[End Block]
		Case "cont3_372"
			;[Block]
			Return(r_cont3_372)
			;[End Block]
		Case "room4_lcz"
			;[Block]
			Return(r_room4_lcz)
			;[End Block]
		Case "room4_2_lcz"
			;[Block]
			Return(r_room4_2_lcz)
			;[End Block]
		Case "room4_ic"
			;[Block]
			Return(r_room4_ic)
			;[End Block]
		Case "room2_checkpoint_lcz_hcz"
			;[Block]
			Return(r_room2_checkpoint_lcz_hcz)
			;[End Block]
		Case "room1_dead_end_hcz"
			;[Block]
			Return(r_room1_dead_end_hcz)
			;[End Block]
		Case "cont1_035"
			;[Block]
			Return(r_cont1_035)
			;[End Block]
		Case "cont1_079"
			;[Block]
			Return(r_cont1_079)
			;[End Block]
		Case "cont1_106"
			;[Block]
			Return(r_cont1_106)
			;[End Block]
		Case "cont1_895"
			;[Block]
			Return(r_cont1_895)
			;[End Block]
		Case "room2_hcz"
			;[Block]
			Return(r_room2_hcz)
			;[End Block]
		Case "room2_2_hcz"
			;[Block]
			Return(r_room2_2_hcz)
			;[End Block]
		Case "room2_3_hcz"
			;[Block]
			Return(r_room2_3_hcz)
			;[End Block]
		Case "room2_4_hcz"
			;[Block]
			Return(r_room2_4_hcz)
			;[End Block]
		Case "room2_5_hcz"
			;[Block]
			Return(r_room2_5_hcz)
			;[End Block]
		Case "room2_6_hcz"
			;[Block]
			Return(r_room2_6_hcz)
			;[End Block]
		Case "room2_mt"
			;[Block]
			Return(r_room2_mt)
			;[End Block]
		Case "room2_nuke"
			;[Block]
			Return(r_room2_nuke)
			;[End Block]
		Case "room2_servers_hcz"
			;[Block]
			Return(r_room2_servers_hcz)
			;[End Block]
		Case "room2_shaft"
			;[Block]
			Return(r_room2_shaft)
			;[End Block]
		Case "room2_tesla_hcz"
			;[Block]
			Return(r_room2_tesla_hcz)
			;[End Block]
		Case "room2_test_hcz"
			;[Block]
			Return(r_room2_test_hcz)
			;[End Block]
		Case "cont2_008"
			;[Block]
			Return(r_cont2_008)
			;[End Block]
		Case "cont2_049"
			;[Block]
			Return(r_cont2_049)
			;[End Block]
		Case "cont2_409"
			;[Block]
			Return(r_cont2_409)
			;[End Block]
		Case "room2c_hcz"
			;[Block]
			Return(r_room2c_hcz)
			;[End Block]
		Case "cont2c_096"
			;[Block]
			Return(r_cont2c_096)
			;[End Block]
		Case "room3_hcz"
			;[Block]
			Return(r_room3_hcz)
			;[End Block]
		Case "room3_2_hcz"
			;[Block]
			Return(r_room3_2_hcz)
			;[End Block]
		Case "room3_3_hcz"
			;[Block]
			Return(r_room3_3_hcz)
			;[End Block]
		Case "cont3_513"
			;[Block]
			Return(r_cont3_513)
			;[End Block]
		Case "cont3_966"
			;[Block]
			Return(r_cont3_966)
			;[End Block]
		Case "room4_hcz"
			;[Block]
			Return(r_room4_hcz)
			;[End Block]
		Case "room4_2_hcz"
			;[Block]
			Return(r_room4_2_hcz)
			;[End Block]
		Case "room2_checkpoint_hcz_ez"
			;[Block]
			Return(r_room2_checkpoint_hcz_ez)
			;[End Block]
		Case "gate_a_entrance"
			;[Block]
			Return(r_gate_a_entrance)
			;[End Block]
		Case "gate_a"
			;[Block]
			Return(r_gate_a)
			;[End Block]
		Case "gate_b_entrance"
			;[Block]
			Return(r_gate_b_entrance)
			;[End Block]
		Case "gate_b"
			;[Block]
			Return(r_gate_b)
			;[End Block]
		Case "room1_dead_end_ez"
			;[Block]
			Return(r_room1_dead_end_ez)
			;[End Block]
		Case "room1_lifts"
			;[Block]
			Return(r_room1_lifts)
			;[End Block]
		Case "room1_o5"
			;[Block]
			Return(r_room1_o5)
			;[End Block]
		Case "room2_ez"
			;[Block]
			Return(r_room2_ez)
			;[End Block]
		Case "room2_2_ez"
			;[Block]
			Return(r_room2_2_ez)
			;[End Block]
		Case "room2_3_ez"
			;[Block]
			Return(r_room2_3_ez)
			;[End Block]
		Case "room2_4_ez"
			;[Block]
			Return(r_room2_4_ez)
			;[End Block]
		Case "room2_5_ez"
			;[Block]
			Return(r_room2_5_ez)
			;[End Block]
		Case "room2_6_ez"
			;[Block]
			Return(r_room2_6_ez)
			;[End Block]
		Case "room2_cafeteria"
			;[Block]
			Return(r_room2_cafeteria)
			;[End Block]
		Case "room2_ic"
			;[Block]
			Return(r_room2_ic)
			;[End Block]
		Case "room2_medibay"
			;[Block]
			Return(r_room2_medibay)
			;[End Block]
		Case "room2_office"
			;[Block]
			Return(r_room2_office)
			;[End Block]
		Case "room2_office_2"
			;[Block]
			Return(r_room2_office_2)
			;[End Block]
		Case "room2_office_3"
			;[Block]
			Return(r_room2_office_3)
			;[End Block]
		Case "room2_servers_ez"
			;[Block]
			Return(r_room2_servers_ez)
			;[End Block]
		Case "room2_scientists"
			;[Block]
			Return(r_room2_scientists)
			;[End Block]
		Case "room2_scientists_2"
			;[Block]
			Return(r_room2_scientists_2)
			;[End Block]
		Case "room2_tesla_ez"
			;[Block]
			Return(r_room2_tesla_ez)
			;[End Block]
		Case "cont2_860_1"
			;[Block]
			Return(r_cont2_860_1)
			;[End Block]
		Case "room2c_ez"
			;[Block]
			Return(r_room2c_ez)
			;[End Block]
		Case "room2c_2_ez"
			;[Block]
			Return(r_room2c_2_ez)
			;[End Block]
		Case "room2c_ec"
			;[Block]
			Return(r_room2c_ec)
			;[End Block]
		Case "room2c_gw_ez"
			;[Block]
			Return(r_room2c_gw_ez)
			;[End Block]
		Case "room3_gw"
			;[Block]
			Return(r_room3_gw)
			;[End Block]
		Case "room3_office"
			;[Block]
			Return(r_room3_office)
			;[End Block]
		Case "room3_ez"
			;[Block]
			Return(r_room3_ez)
			;[End Block]
		Case "room3_2_ez"
			;[Block]
			Return(r_room3_2_ez)
			;[End Block]
		Case "room3_3_ez"
			;[Block]
			Return(r_room3_3_ez)
			;[End Block]
		Case "room3_4_ez"
			;[Block]
			Return(r_room3_4_ez)
			;[End Block]
		Case "room4_ez"
			;[Block]
			Return(r_room4_ez)
			;[End Block]
		Case "dimension_106"
			;[Block]
			Return(r_dimension_106)
			;[End Block]
		Case "dimension_1499"
			;[Block]
			Return(r_dimension_1499)
			;[End Block]
		Default
			;[Block]
			Return(-1)
			;[End Block]
	End Select
End Function

Function CreateRoomTemplate.RoomTemplates(MeshPath$)
	Local rt.RoomTemplates
	
	rt.RoomTemplates = New RoomTemplates
	rt\OBJPath = "GFX\Map\" + MeshPath
	rt\ID = RoomTempID
	RoomTempID = RoomTempID + 1
	
	Return(rt)
End Function

Function LoadRoomTemplates%(File$)
	CatchErrors("LoadRoomTemplates(" + File + ")")
	
	Local Loc$, i%
	Local rt.RoomTemplates = Null
	Local StrTemp$ = ""
	Local f% = OpenFile_Strict(File)
	
	While (Not Eof(f))
		Loc = Trim(ReadLine(f))
		If Left(Loc, 1) = "["
			Loc = Mid(Loc, 2, Len(Loc) - 2)
			If Loc <> "room ambience"
				StrTemp = IniGetString(File, Loc, "Mesh Path")
				
				rt.RoomTemplates = CreateRoomTemplate(StrTemp)
				rt\Name = Lower(Loc)
				rt\RoomID = FindRoomID(rt\Name)
				
				StrTemp = IniGetString(File, Loc, "Shape")
				
				Select StrTemp
					Case "1"
						;[Block]
						rt\Shape = ROOM1
						;[End Block]
					Case "2"
						;[Block]
						rt\Shape = ROOM2
						;[End Block]
					Case "2C"
						;[Block]
						rt\Shape = ROOM2C
						;[End Block]
					Case "3"
						;[Block]
						rt\Shape = ROOM3
						;[End Block]
					Case "4"
						;[Block]
						rt\Shape = ROOM4
						;[End Block]
				End Select
				
				For i = 0 To 4
					rt\Zone[i] = IniGetInt(File, Loc, "Zone" + (i + 1))
				Next
				
				rt\Commonness = Max(Min(IniGetInt(File, Loc, "Commonness"), 100), 0)
				rt\DisableDecals = IniGetInt(File, Loc, "DisableDecals")
				rt\DisableOverlapCheck = IniGetInt(File, Loc, "DisableOverlapCheck")
			EndIf
		EndIf
	Wend
	
	i = 0
	Repeat
		StrTemp = IniGetString(File, "room ambience", "Ambience" + i)
		If StrTemp = "" Then Exit
		
		RoomAmbience[i] = LoadSound_Strict(StrTemp)
		i = i + 1
	Forever
	
	CloseFile(f)
	
	CatchErrors("Uncaught: LoadRoomTemplates(" + File + ")")
End Function

Function LoadRoomMesh%(rt.RoomTemplates)
	If FileExtension(rt\OBJPath) = "rmesh" ; ~ File is .rmesh
		rt\OBJ = LoadRMesh(rt\OBJPath, rt)
	ElseIf FileExtension(rt\OBJPath) = "b3d" ; ~ File is .b3d
		RuntimeError(Format(GetLocalString("runerr", "b3d"), rt\OBJPath))
	Else ; ~ File not found
		RuntimeError(Format(GetLocalString("runerr", "notfound"), rt\OBJPath))
	EndIf
	
	If rt\OBJ = 0 Then RuntimeError(Format(GetLocalString("runerr", "failedload"), rt\OBJPath))
	
	CalculateRoomTemplateExtents(rt)
	
	HideEntity(rt\OBJ)
End Function

Function RemoveRoomTemplate%(rt.RoomTemplates)
	FreeEntity(rt\OBJ) : rt\OBJ = 0
	Delete(rt)
End Function

;Type TriggerBox
;	Field OBJ%
;	Field Name$
;	Field MinX#, MinY#, MinZ#
;	Field MaxX#, MaxY#, MaxZ#
;End Type

; ~ Room Objects Constants
;[Block]
Const MaxRoomObjects% = 30
Const MaxRoomLevers% = 4
Const MaxRoomDoors% = 8
Const MaxRoomNPCs% = 12
Const MaxRoomSecurityCams% = 8
Const MaxRoomEmitters% = 8
Const MaxRoomAdjacents% = 4
Const MaxRoomTextures% = 3
;Const MaxRoomTriggerBoxes% = 8
;[End Block]

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
	Field Objects%[MaxRoomObjects], ScriptedObject%[MaxRoomObjects]
	Field RoomLevers.Levers[MaxRoomLevers]
	Field RoomDoors.Doors[MaxRoomDoors]
	Field NPC.NPCs[MaxRoomNPCs]
	Field RoomSecurityCams.SecurityCams[MaxRoomSecurityCams]
	Field RoomEmitters.Emitter[MaxRoomEmitters]
	Field mt.MTGrid
	Field Adjacent.Rooms[MaxRoomAdjacents]
	Field AdjDoor.Doors[MaxRoomAdjacents]
	Field Textures%[MaxRoomTextures]
	;Field TriggerBoxAmount%
	;Field TriggerBoxes.TriggerBox[MaxRoomTriggerBoxes]
	Field MaxWayPointY#
	Field MinX#, MinY#, MinZ#
	Field MaxX#, MaxY#, MaxZ#
	Field HiddenAlpha% = True
	Field RoomCenter%
End Type

Global PlayerRoom.Rooms

Const MTGridSize% = 19 ; ~ Same size as the main map itself (better for the map creator)
Const MTGridY# = 8.0

; ~ MT Model ID Constants
;[Block]
Const MT_ROOM2C% = 0
Const MT_ROOM1% = 1
Const MT_ROOM2% = 2
Const MT_ROOM3% = 3
Const MT_ROOM4% = 4
Const MT_FIRST_ELEVATOR% = 5
Const MT_SECOND_ELEVATOR% = 6
Const MT_GENERATOR% = 7
;[End Block]

Type MTGrid
	Field Grid%[PowTwo(MTGridSize)]
	Field Angles%[PowTwo(MTGridSize)]
	Field Meshes%[MaxMTModelIDAmount]
	Field Entities%[PowTwo(MTGridSize)]
	Field waypoints.WayPoints[PowTwo(MTGridSize)]
End Type

Function UpdateMT%(mt.MTGrid)
	CatchErrors("UpdateMT()")
	
	Local tX%, tY%
	
	For tX = 0 To MTGridSize - 1
		For tY = 0 To MTGridSize - 1
			If mt\Entities[tX + (tY * MTGridSize)] <> 0
				If DistanceSquared(EntityX(me\Collider, True), EntityX(mt\Entities[tX + (tY * MTGridSize)], True), EntityZ(me\Collider, True), EntityZ(mt\Entities[tX + (tY * MTGridSize)], True)) < PowTwo(me\CameraFogDist * LightVolume * 1.2)
					If EntityHidden(mt\Entities[tX + (tY * MTGridSize)]) Then ShowEntity(mt\Entities[tX + (tY * MTGridSize)])
				Else
					If (Not EntityHidden(mt\Entities[tX + (tY * MTGridSize)])) Then HideEntity(mt\Entities[tX + (tY * MTGridSize)])
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: UpdateMT()")
End Function

Function DestroyMT%(mt.MTGrid, DestroyWaypoint% = True)
	Local x%, y%
	
	For x = 0 To MTGridSize - 1
		For y = 0 To MTGridSize - 1
			If mt\Entities[x + (y * MTGridSize)] <> 0 Then FreeEntity(mt\Entities[x + (y * MTGridSize)]) : mt\Entities[x + (y * MTGridSize)] = 0
			If DestroyWaypoint And mt\waypoints[x + (y * MTGridSize)] <> Null Then RemoveWaypoint(mt\waypoints[x + (y * MTGridSize)]) : mt\waypoints[x + (y * MTGridSize)] = Null
		Next
	Next
	For x = 0 To MaxMTModelIDAmount - 1
		If mt\Meshes[x] <> 0 Then FreeEntity(mt\Meshes[x]) : mt\Meshes[x] = 0
	Next
End Function

Function PlaceMapCreatorMT%(r.Rooms)
	CatchErrors("PlaceMapCreatorMT()")
	
	Local d.Doors, it.Items, wayp.WayPoints
	Local x%, y%, i%, Dist#
	Local Meshes%[MaxMTModelIDAmount]
	Local SinValue#, CosValue#
	
	For i = 0 To MaxMTModelIDAmount - 1
		Meshes[i] = CopyEntity(misc_I\MTModelID[i])
		HideEntity(Meshes[i])
	Next
	
	For y = 0 To MTGridSize - 1
		For x = 0 To MTGridSize - 1
			If r\mt\Grid[x + (y * MTGridSize)] > 0
				Local Tile_Type% = 0
				Local Angle# = 0.0
				
				Tile_Type = r\mt\Grid[x + (y * MTGridSize)]
				Angle = r\mt\Angles[x + (y * MTGridSize)] * 90.0
				
				Local Tile_Entity% = CopyEntity(Meshes[Tile_Type - 1])
				
				RotateEntity(Tile_Entity, 0.0, Angle, 0.0)
				ScaleEntity(Tile_Entity, RoomScale, RoomScale, RoomScale, True)
				PositionEntity(Tile_Entity, r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
				SinValue = Sin(EntityYaw(Tile_Entity, True))
				CosValue = Cos(EntityYaw(Tile_Entity, True))
				
				Select Tile_Type
					Case 1, 2
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (409.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						;[End Block]
					Case 3, 4, 5
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (424.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						;[End Block]
					Case 6
						;[Block]
						AddLight(r, r\x + (x * 2.0), r\y + MTGridY + (409.0 * RoomScale), r\z + (y * 2.0), 2, 0.25, 255, 200, 200)
						AddLight(r, r\x + (x * 2.0) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (469.0 * RoomScale), r\z + (y * 2.0) + (SinValue * 560.0 * RoomScale), 2, 0.25, 255, 200, 200)
						CreateProp(r, "GFX\map\Props\lamp3.b3d", r\x + (x * 2.0) + (SinValue * 254.0 * RoomScale) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (432.0 * RoomScale), (y * 2.0) + (CosValue * 254.0 * RoomScale) + (SinValue * 560.0 * RoomScale), 0.0, 90.0, 90.0, 400.0, 400.0, 400.0, False, 0, "")
						CreateProp(r, "GFX\map\Props\lamp3.b3d", r\x + (x * 2.0) - (SinValue * 254.0 * RoomScale) + (CosValue * 560.0 * RoomScale), r\y + MTGridY + (432.0 * RoomScale), (y * 2.0) - (CosValue * 254.0 * RoomScale) + (SinValue * 560.0 * RoomScale), 0.0, -90.0, 90.0, 400.0, 400.0, 400.0, False, 0, "")
						
						d.Doors = CreateDoor(Null, r\x + (x * 2.0) + (CosValue * 256.0 * RoomScale), r\y + MTGridY, r\z + (y * 2.0) + (SinValue * 256.0 * RoomScale), EntityYaw(Tile_Entity, True) - 90.0, False, ELEVATOR_DOOR)
						PositionEntity(d\ElevatorPanel[1], EntityX(d\ElevatorPanel[1], True) + (CosValue * 0.05), EntityY(d\ElevatorPanel[1], True) + 0.1, EntityZ(d\ElevatorPanel[1], True) + (SinValue * (-0.28)), True)
						RotateEntity(d\ElevatorPanel[1], EntityPitch(d\ElevatorPanel[1], True) + 45.0, EntityYaw(d\ElevatorPanel[1], True), EntityRoll(d\ElevatorPanel[1], True), True)
						
						Local TempInt2% = CreatePivot()
						
						PositionEntity(TempInt2, r\x + (x * 2.0) + (CosValue * 552.0 * RoomScale), r\y + MTGridY + (240.0 * RoomScale), r\z + (y * 2.0) + (SinValue * 552.0 * RoomScale))
						If r\RoomDoors[1] = Null
							r\RoomDoors[1] = d
							r\Objects[3] = TempInt2
							RotateEntity(r\Objects[3], 0.0, EntityYaw(Tile_Entity, True), 0.0, True)
							PositionEntity(r\Objects[0], r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
						ElseIf r\RoomDoors[1] <> Null And r\RoomDoors[3] = Null
							r\RoomDoors[3] = d
							r\Objects[5] = TempInt2
							RotateEntity(r\Objects[5], 0.0, EntityYaw(Tile_Entity, True) + 180.0, 0.0, True)
							PositionEntity(r\Objects[1], r\x + (x * 2.0), r\y + MTGridY, r\z + (y * 2.0), True)
						EndIf
						;[End Block]
					Case 7
						;[Block]
						AddLight(r, r\x + (x * 2.0) - (SinValue * 521.0 * RoomScale) + (CosValue * 16.0 * RoomScale), r\y + MTGridY + (396.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 521.0 * RoomScale) + (SinValue * 16.0 * RoomScale), 2, 0.425, 255, 200, 200)
						CreateProp(r, "GFX\map\Props\tank2.b3d", r\x + (x * 2.0) - (SinValue * 369.0 * RoomScale) + (CosValue * 320.0 * RoomScale), r\y + MTGridY - (144.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 369.0 * RoomScale) + (SinValue * 320.0 * RoomScale), 0.0, 0.0, 0.0, 3.0, 3.0, 3.0, True, 0, "")
						CreateProp(r, "GFX\map\Props\tank2.b3d", r\x + (x * 2.0) - (SinValue * 977.0 * RoomScale) + (CosValue * 320.0 * RoomScale), r\y + MTGridY - (144.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 977.0 * RoomScale) + (SinValue * 320.0 * RoomScale), 0.0, 0.0, 0.0, 3.0, 3.0, 3.0, True, 0, "")
						
						it.Items = CreateItem("SCP-500-01", it_scp500pill, r\x + (x * 2.0) + (CosValue * (-208.0) * RoomScale) - (SinValue * 1226.0 * RoomScale), r\y + MTGridY + (110.0 * RoomScale), r\z + (y * 2.0) + (SinValue * (-208.0) * RoomScale) + (CosValue * 1226.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						
						it.Items = CreateItem("Night Vision Goggles", it_nvg, r\x + (x * 2.0) - (SinValue * 504.0 * RoomScale) + (CosValue * 16.0 * RoomScale), r\y + MTGridY + (90.0 * RoomScale), r\z + (y * 2.0) + (CosValue * 504.0 * RoomScale) + (SinValue * 16.0 * RoomScale))
						EntityType(it\Collider, HIT_ITEM)
						;[End Block]
				End Select
				
				r\mt\Entities[x + (y * MTGridSize)] = Tile_Entity
				wayp = CreateWaypoint(Null, r, r\x + (x * 2.0), r\y + MTGridY + 0.2, r\z + (y * 2.0))
				r\mt\waypoints[x + (y * MTGridSize)] = wayp
				
				If y < MTGridSize - 1
					If r\mt\waypoints[x + ((y + 1) * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y + 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y + 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y + 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If y > 0
					If r\mt\waypoints[x + ((y - 1) * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + ((y - 1) * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + ((y - 1) * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x + ((y - 1) * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x > 0
					If r\mt\waypoints[x - 1 + (y * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x - 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x - 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								r\mt\waypoints[x - 1 + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
					EndIf
				EndIf
				If x < MTGridSize - 1
					If r\mt\waypoints[x + 1 + (y * MTGridSize)] <> Null
						Dist = EntityDistance(r\mt\waypoints[x + (y * MTGridSize)]\OBJ, r\mt\waypoints[x + 1 + (y * MTGridSize)]\OBJ)
						For i = 0 To 3
							If r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
								r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + 1 + (y * MTGridSize)]
								r\mt\waypoints[x + (y * MTGridSize)]\Dist[i] = Dist
								Exit
							EndIf
						Next
						For i = 0 To 3
							If r\mt\waypoints[x + 1 + (y * MTGridSize)]\connected[i] = r\mt\waypoints[x + (y * MTGridSize)]
								Exit
							ElseIf r\mt\waypoints[x + (y * MTGridSize)]\connected[i] = Null
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
	
	For i = 0 To MaxMTModelIDAmount - 1
		r\mt\Meshes[i] = Meshes[i]
	Next
	
	CatchErrors("Uncaught: PlaceMapCreatorMT()")
End Function

Function CreateRoom.Rooms(Zone%, RoomShape%, x#, y#, z#, RoomID% = -1, Angle# = 0.0)
	CatchErrors("CreateRoom.Rooms(" + RoomShape + ", " + x + ", " + y + ", " + z + ", " + RoomID + ")")
	
	Local r.Rooms, rt.RoomTemplates
	Local i%
	
	r.Rooms = New Rooms
	r\Zone = Zone
	r\x = x : r\y = y : r\z = z
	
	If RoomID <> -1
		For rt.RoomTemplates = Each RoomTemplates
			If rt\RoomID = RoomID
				r\RoomTemplate = rt
				
				If rt\OBJ = 0 Then LoadRoomMesh(rt)
				
				r\OBJ = CopyEntity(rt\OBJ)
				ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
				EntityType(r\OBJ, HIT_MAP)
				EntityPickMode(r\OBJ, 2)
				PositionEntity(r\OBJ, x, y, z)
				
				For i = 0 To MaxRoomObjects - 1
					r\ScriptedObject[i] = False
				Next
				FillRoom(r)
				
				r\Angle = Angle
				RotateEntity(r\OBJ, 0.0, Angle, 0.0)
				
				Return(r)
			EndIf
		Next
	EndIf
	
	Local Temp% = 0
	
	For rt.RoomTemplates = Each RoomTemplates
		For i = 0 To 4
			If rt\Zone[i] = Zone
				If rt\Shape = RoomShape
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
			If rt\Zone[i] = Zone And rt\Shape = RoomShape
				Temp = Temp + rt\Commonness
				If RandomRoom > Temp - rt\Commonness And RandomRoom <= Temp
					r\RoomTemplate = rt
					
					If rt\OBJ = 0 Then LoadRoomMesh(rt)
					
					r\OBJ = CopyEntity(rt\OBJ)
					ScaleEntity(r\OBJ, RoomScale, RoomScale, RoomScale)
					EntityType(r\OBJ, HIT_MAP)
					EntityPickMode(r\OBJ, 2)
					PositionEntity(r\OBJ, x, y, z)
					
					For i = 0 To MaxRoomObjects - 1
						r\ScriptedObject[i] = False
					Next
					FillRoom(r)
					
					r\Angle = Angle
					RotateEntity(r\OBJ, 0.0, Angle, 0.0)
					
					Return(r)
				EndIf
			EndIf
		Next
	Next
	
	CatchErrors("Uncaught: CreateRoom.Rooms(" + RoomShape + ", " + x + ", " + y + ", " + z + ", " + RoomID + "))")
End Function

Function RemoveRoom%(r.Rooms)
	Local i%
	
	For i = 0 To MaxRoomTextures - 1
		r\Textures[i] = 0
	Next
	For i = 0 To MaxRoomObjects - 1
		If r\Objects[i] <> 0 Then EntityParent(r\Objects[i], 0)
	Next
	For i = 0 To MaxRoomObjects - 1
		If r\Objects[i] <> 0 Then FreeEntity(r\Objects[i]) : r\Objects[i] = 0
	Next
	
	If r\RoomCenter <> 0 Then FreeEntity(r\RoomCenter) : r\RoomCenter = 0
	FreeEntity(r\OBJ) : r\OBJ = 0
	Delete(r)
End Function

Type TempWayPoints
	Field x#, y#, z#
	Field RoomTemplate.RoomTemplates
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

Function CreateWaypoint.WayPoints(door.Doors, room.Rooms, x#, y#, z#)
	Local w.WayPoints
	
	w.WayPoints = New WayPoints
	w\OBJ = CreatePivot()
	PositionEntity(w\OBJ, x, y, z)
	If room <> Null Then EntityParent(w\OBJ, room\OBJ)
	
	w\room = room
	w\door = door
	
	Return(w)
End Function

Function RemoveWaypoint%(w.WayPoints)
	FreeEntity(w\OBJ) : w\OBJ = 0
	Delete(w)
End Function

; ~ Button ID Constants
;[Block]
Const BUTTON_DEFAULT% = 0
Const BUTTON_KEYCARD% = 1
Const BUTTON_KEYPAD% = 2
Const BUTTON_SCANNER% = 3
Const BUTTON_ELEVATOR% = 4
;[End Block]

Function CreateButton%(ButtonID% = BUTTON_DEFAULT, x#, y#, z#, Pitch# = 0.0, Yaw# = 0.0, Roll# = 0.0, Parent% = 0, Locked% = False)
	Local OBJ% = CopyEntity(d_I\ButtonModelID[ButtonID])
	
	ScaleEntity(OBJ, 0.03, 0.03, 0.03)
	PositionEntity(OBJ, x, y, z)
	RotateEntity(OBJ, Pitch, Yaw, Roll)
	EntityPickMode(OBJ, 2)
	If Locked Then EntityTexture(OBJ, d_I\ButtonTextureID[BUTTON_RED_TEXTURE])
	If Parent <> 0 Then EntityParent(OBJ, Parent)
	
	Return(OBJ)
End Function

Function UpdateButton%(OBJ%)
	Local Dist# = EntityDistanceSquared(me\Collider, OBJ)
	
	If Dist < 0.64
		Local Pvt% = CreatePivot()
		
		PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
		PointEntity(Pvt, OBJ)
		
		If EntityPick(Pvt, 0.8) = OBJ
			d_I\ClosestButton = OBJ
			FreeEntity(Pvt) : Pvt = 0
			Return(True)
		EndIf
		FreeEntity(Pvt) : Pvt = 0
	EndIf
	Return(False)
End Function

Type BrokenDoor
	Field IsBroken%
	Field x#, z#
End Type

Global bk.BrokenDoor

Type Doors
	Field OBJ%, OBJ2%, FrameOBJ%, Buttons%[2]
	Field Locked%, LockedUpdated%, Open%, Angle%, OpenState#, FastOpen%
	Field DoorType%, Dist#
	Field Timer%, TimerState#
	Field KeyCard%
	Field room.Rooms
	Field DisableWaypoint%
	Field SoundCHN%, SoundCHN2%
	Field ButtonCHN%
	Field Code%
	Field AutoClose%
	Field LinkedDoor.Doors
	Field IsElevatorDoor% = False
	Field MTFClose% = True
	Field ElevatorPanel%[2]
	Field PlayCautionSFX%
	Field ButtonsUpdateTimer#
	Field IsAffected% = False
End Type

; ~ Door ID Constants
;[Block]
Const DEFAULT_DOOR% = 0
Const ELEVATOR_DOOR% = 1
Const HEAVY_DOOR% = 2
Const BIG_DOOR% = 3
Const OFFICE_DOOR% = 4
Const WOODEN_DOOR% = 5
Const ONE_SIDED_DOOR% = 6
Const SCP_914_DOOR% = 7
;[End Block]

Function CreateDoor.Doors(room.Rooms, x#, y#, z#, Angle#, Open% = False, DoorType% = DEFAULT_DOOR, Keycard% = KEY_MISC, Code% = 0, CustomParent% = 0)
	Local d.Doors
	Local Parent%, i%
	Local FrameScaleX#, FrameScaleY#, FrameScaleZ#
	Local DoorScaleX#, DoorScaleY#, DoorScaleZ#
	Local FrameModelID%, DoorModelID_1%, DoorModelID_2%, ButtonID%
	
	If room <> Null
		Parent = room\OBJ
	Else
		Parent = CustomParent
	EndIf
	
	d.Doors = New Doors
	
	; ~ (Keycard > 0) - KEY CARD
	; ~ (Keycard = 0) - DEFAULT
	; ~ (Keycard > -4 And Keycard < 0) - HAND
	; ~ (Keycard <= -4) - KEY
	
	d\KeyCard = Keycard 
	
	d\Code = Code
	
	d\Angle = Angle
	d\Open = Open
	
	; ~ Set "d\Locked = 1" for elevator doors to fix buttons color. Anyway the door will be unlocked by "UpdateElevators" function. -- Jabka
	If DoorType = ELEVATOR_DOOR Then d\Locked = 1
	d\DoorType = DoorType
	If DoorType = SCP_914_DOOR Then DoorType = ONE_SIDED_DOOR
	
	d\MTFClose = True
	d\AutoClose = (Open And ((DoorType = DEFAULT_DOOR) Lor (DoorType = HEAVY_DOOR)) And (Keycard = 0) And (Code = 0) And Rand(10) = 1)
	
	d\room = room
	
	Select DoorType
		Case DEFAULT_DOOR
			;[Block]
			DoorModelID_1 = DOOR_DEFAULT_MODEL
			DoorModelID_2 = DoorModelID_1
			DoorScaleX = 203.0 * RoomScale / MeshWidth(d_I\DoorModelID[DoorModelID_1]) : DoorScaleY = 313.0 * RoomScale / MeshHeight(d_I\DoorModelID[DoorModelID_1]) : DoorScaleZ = 15.0 * RoomScale / MeshDepth(d_I\DoorModelID[DoorModelID_1])
			
			FrameModelID = DOOR_DEFAULT_FRAME_MODEL
			FrameScaleX = RoomScale : FrameScaleY = RoomScale : FrameScaleZ = RoomScale
			;[End Block]
		Case ONE_SIDED_DOOR
			;[Block]
			DoorModelID_1 = DOOR_ONE_SIDED_MODEL
			DoorModelID_2 = DoorModelID_1
			DoorScaleX = 203.0 * RoomScale / MeshWidth(d_I\DoorModelID[DoorModelID_1]) : DoorScaleY = 313.0 * RoomScale / MeshHeight(d_I\DoorModelID[DoorModelID_1]) : DoorScaleZ = 15.0 * RoomScale / MeshDepth(d_I\DoorModelID[DoorModelID_1])
			
			FrameModelID = DOOR_DEFAULT_FRAME_MODEL
			FrameScaleX = RoomScale : FrameScaleY = RoomScale : FrameScaleZ = RoomScale
			;[End Block]
		Case ELEVATOR_DOOR
			;[Block]
			DoorModelID_1 = DOOR_ELEVATOR_MODEL
			DoorModelID_2 = DoorModelID_1
			DoorScaleX = RoomScale : DoorScaleY = RoomScale : DoorScaleZ = RoomScale
			
			FrameModelID = DOOR_DEFAULT_FRAME_MODEL
			FrameScaleX = RoomScale : FrameScaleY = RoomScale : FrameScaleZ = RoomScale
			;[End Block]
		Case HEAVY_DOOR
			;[Block]
			DoorModelID_1 = DOOR_HEAVY_MODEL_1
			DoorModelID_2 = DOOR_HEAVY_MODEL_2
			DoorScaleX = RoomScale : DoorScaleY = RoomScale : DoorScaleZ = RoomScale
			
			FrameModelID = DOOR_DEFAULT_FRAME_MODEL
			FrameScaleX = RoomScale : FrameScaleY = RoomScale : FrameScaleZ = RoomScale
			;[End Block]
		Case BIG_DOOR
			;[Block]
			DoorModelID_1 = DOOR_BIG_MODEL_1
			DoorModelID_2 = DOOR_BIG_MODEL_2
			DoorScaleX = 55.0 * RoomScale : DoorScaleY = 55.0 * RoomScale : DoorScaleZ = 55.0 * RoomScale
			
			FrameModelID = DOOR_BIG_FRAME_MODEL
			FrameScaleX = 55.0 * RoomScale : FrameScaleY = 55.0 * RoomScale : FrameScaleZ = 55.0 * RoomScale
			;[End Block]
		Case OFFICE_DOOR
			;[Block]
			DoorModelID_1 = DOOR_OFFICE_MODEL
			DoorScaleX = RoomScale : DoorScaleY = RoomScale : DoorScaleZ = RoomScale
			
			FrameModelID = DOOR_OFFICE_FRAME_MODEL
			FrameScaleX = RoomScale : FrameScaleY = RoomScale : FrameScaleZ = RoomScale
			;[End Block]
		Case WOODEN_DOOR
			;[Block]
			DoorModelID_1 = DOOR_WOODEN_MODEL
			DoorScaleX = 46.0 * RoomScale : DoorScaleY = 44.0 * RoomScale : DoorScaleZ = 46.0 * RoomScale
			
			FrameModelID = DOOR_WOODEN_FRAME_MODEL
			FrameScaleX = 45.0 * RoomScale : FrameScaleY = 44.0 * RoomScale : FrameScaleZ = 80.0 * RoomScale
			;[End Block]
	End Select
	
	d\FrameOBJ = CopyEntity(d_I\DoorFrameModelID[FrameModelID])
	ScaleEntity(d\FrameOBJ, FrameScaleX, FrameScaleY, FrameScaleZ)
	PositionEntity(d\FrameOBJ, x, y, z)
	If DoorType = BIG_DOOR Then EntityType(d\FrameOBJ, HIT_MAP)
	EntityPickMode(d\FrameOBJ, 2)
	
	d\OBJ = CopyEntity(d_I\DoorModelID[DoorModelID_1])
	ScaleEntity(d\OBJ, DoorScaleX, DoorScaleY, DoorScaleZ)
	PositionEntity(d\OBJ, x, y, z)
	RotateEntity(d\OBJ, 0.0, Angle, 0.0)
	EntityType(d\OBJ, HIT_MAP)
	EntityPickMode(d\OBJ, 2)
	EntityParent(d\OBJ, Parent)
	
	If DoorType <> OFFICE_DOOR And DoorType <> WOODEN_DOOR
		d\OBJ2 = CopyEntity(d_I\DoorModelID[DoorModelID_2])
		ScaleEntity(d\OBJ2, DoorScaleX, DoorScaleY, DoorScaleZ)
		PositionEntity(d\OBJ2, x, y, z)
		RotateEntity(d\OBJ2, 0.0, Angle + ((DoorType <> BIG_DOOR) * 180.0), 0.0)
		EntityType(d\OBJ2, HIT_MAP)
		EntityPickMode(d\OBJ2, 2)
		EntityParent(d\OBJ2, Parent)
	EndIf
	
	Local Temp% = (DoorType = BIG_DOOR)
	
	For i = 0 To 1
		Select DoorType
			Case OFFICE_DOOR, WOODEN_DOOR
				;[Block]
				d\Buttons[i] = CreatePivot()
				PositionEntity(d\Buttons[i], x - 0.22, y + 0.6, z + 0.1 + (i * (-0.2)))
				EntityRadius(d\Buttons[i], 0.1)
				EntityPickMode(d\Buttons[i], 1)
				EntityParent(d\Buttons[i], d\FrameOBJ)
				;[End Block]
			Default
				;[Block]
				If Code <> 0
					ButtonID = BUTTON_KEYPAD
				ElseIf Keycard > KEY_MISC
					ButtonID = BUTTON_KEYCARD
				ElseIf Keycard > KEY_860 And Keycard < KEY_MISC
					ButtonID = BUTTON_SCANNER
				Else
					ButtonID = BUTTON_DEFAULT
					If DoorType = ELEVATOR_DOOR
						ButtonID = i * BUTTON_ELEVATOR
						
						d\ElevatorPanel[i] = CopyEntity(d_I\ElevatorPanelModel)
						ScaleEntity(d\ElevatorPanel[i], RoomScale, RoomScale, RoomScale)
						RotateEntity(d\ElevatorPanel[i], 0.0, i * 180.0, 0.0)
						PositionEntity(d\ElevatorPanel[i], x, y + 1.27, z + 0.13 + (i * (-0.26)))
						EntityParent(d\ElevatorPanel[i], d\FrameOBJ)
					EndIf
				EndIf
				d\Buttons[i] = CreateButton(ButtonID, x + ((Not Temp) * (0.6 + (i * (-1.2)))) + (Temp * ((-432.0 + (i * 864.0)) * RoomScale)), y + 0.7, z + ((Not Temp) * ((-0.1) + (i * 0.2))) + (Temp * ((192.0 + (i * (-384.0)))) * RoomScale), 0.0, ((Not Temp) * (i * 180.0)) + (Temp * (90.0 + (i * 180.0))), 0.0, d\FrameOBJ, d\Locked)
				;[End Block]
		End Select
	Next
	RotateEntity(d\FrameOBJ, 0.0, Angle, 0.0)
	EntityParent(d\FrameOBJ, Parent)
	
	Return(d)
End Function

Function UpdateDoors%()
	Local d.Doors
	Local x#, z#, Dist#, i%, FindButton%
	Local SinValue#
	Local FPSFactorEx#
	
	d_I\ClosestButton = 0
	d_I\ClosestDoor = Null
	For d.Doors = Each Doors
		If (EntityDistanceSquared(d\FrameOBJ, me\Collider) <= PowTwo(HideDistance * 1.75)) Lor (d\IsElevatorDoor > 0) ; ~ Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2_mt -- ENDSHN
			FindButton = (1 - (d\Open And ((d\DoorType = OFFICE_DOOR) Lor (d\DoorType = WOODEN_DOOR))))
			
			If ((d\OpenState >= 180.0 Lor d\OpenState <= 0.0) And FindButton) And GrabbedEntity = 0
				For i = 0 To 1
					If d\Buttons[i] <> 0
						If Abs(EntityX(me\Collider) - EntityX(d\Buttons[i], True)) < 1.0 And Abs(EntityZ(me\Collider) - EntityZ(d\Buttons[i], True)) < 1.0
							If UpdateButton(d\Buttons[i])
								d_I\ClosestDoor = d
								me\SndVolume = 4.0
								d_I\AnimButton = d_I\ClosestButton
								Exit
							EndIf
						EndIf
					EndIf
				Next
			EndIf
			
			Local FPSFactorDoubled# = fps\Factor[0] * 2.0
			Local OpenFactor# = (d\FastOpen + 1 - (d\IsAffected * 0.375))
			
			If d\Open
				If d\OpenState < 180.0
					Select d\DoorType
						Case DEFAULT_DOOR
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (FPSFactorDoubled * OpenFactor))
							FPSFactorEx = Sin(d\OpenState) * OpenFactor * fps\Factor[0] / 80.0
							MoveEntity(d\OBJ, FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case ELEVATOR_DOOR
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (FPSFactorDoubled * OpenFactor))
							FPSFactorEx = Sin(d\OpenState) * OpenFactor * fps\Factor[0] / 162.0
							MoveEntity(d\OBJ, FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case HEAVY_DOOR
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (FPSFactorDoubled * OpenFactor))
							SinValue = Sin(d\OpenState)
							MoveEntity(d\OBJ, SinValue * OpenFactor * fps\Factor[0] / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * (d\FastOpen + 1) * fps\Factor[0] / 120.0, 0.0, 0.0)
							;[End Block]
						Case BIG_DOOR
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 0.8))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 180.0
							MoveEntity(d\OBJ, SinValue * FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, (-SinValue) * FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case OFFICE_DOOR, WOODEN_DOOR
							;[Block]
							If d\room <> Null
								d\OpenState = CurveValue(180.0, d\OpenState, 40.0) + (fps\Factor[0] * 0.01)
								RotateEntity(d\OBJ, 0.0, d\room\Angle + d\Angle + (d\OpenState / 2.5), 0.0)
							EndIf
							;[End Block]
						Case ONE_SIDED_DOOR
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (FPSFactorDoubled * OpenFactor))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 80.0
							MoveEntity(d\OBJ, SinValue * OpenFactor * FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * (d\FastOpen + 1) * (-FPSFactorEx), 0.0, 0.0)
							;[End Block]
						Case SCP_914_DOOR ; ~ Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState + (fps\Factor[0] * 1.4))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 114.0
							MoveEntity(d\OBJ, SinValue * FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * (-FPSFactorEx), 0.0, 0.0)
							;[End Block]
					End Select
				Else
					d\FastOpen = False
					ResetEntity(d\OBJ)
					If d\OBJ2 <> 0 Then ResetEntity(d\OBJ2)
					If d\TimerState > 0.0
						d\TimerState = Max(0.0, d\TimerState - fps\Factor[0])
						If d\PlayCautionSFX And (d\TimerState + fps\Factor[0] > 110.0 And d\TimerState <= 110.0) Then d\SoundCHN = PlaySound2(snd_I\CautionSFX, Camera, d\OBJ)
						If d\TimerState = 0.0 Then OpenCloseDoor(d)
					EndIf
					If d\AutoClose And RemoteDoorOn
						If EntityDistanceSquared(Camera, d\OBJ) < 4.41
							If I_714\Using = 0 And wi\GasMask <> 4 And wi\HazmatSuit <> 4 Then PlaySound_Strict(snd_I\HorrorSFX[7])
							OpenCloseDoor(d) : d\AutoClose = False
						EndIf
					EndIf
				EndIf
			Else
				Local FrameX# = EntityX(d\FrameOBJ, True)
				Local FrameY# = EntityY(d\FrameOBJ, True)
				Local FrameZ# = EntityZ(d\FrameOBJ, True)
				
				If d\OpenState > 0.0
					Select d\DoorType
						Case DEFAULT_DOOR
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (FPSFactorDoubled * OpenFactor))
							FPSFactorEx = Sin(d\OpenState) * OpenFactor * (-fps\Factor[0]) / 80.0
							MoveEntity(d\OBJ, FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case ELEVATOR_DOOR
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (FPSFactorDoubled * OpenFactor))
							FPSFactorEx = Sin(d\OpenState) * OpenFactor * (-fps\Factor[0]) / 162.0
							MoveEntity(d\OBJ, FPSFactorEx, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case HEAVY_DOOR
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (FPSFactorDoubled * OpenFactor))
							SinValue = Sin(d\OpenState)
							MoveEntity(d\OBJ, SinValue * OpenFactor * (-fps\Factor[0]) / 85.0, 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * (d\FastOpen + 1) * (-fps\Factor[0]) / 120.0, 0.0, 0.0)
							;[End Block]
						Case BIG_DOOR
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (fps\Factor[0] * 0.8))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 180.0
							MoveEntity(d\OBJ, SinValue * (-FPSFactorEx), 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * FPSFactorEx, 0.0, 0.0)
							If d\OpenState < 15.0 And d\OpenState + fps\Factor[0] >= 15.0 Then SetEmitter(Null, FrameX, FrameY, FrameZ, 11)
							;[End Block]
						Case OFFICE_DOOR, WOODEN_DOOR
							;[Block]
							d\OpenState = 0.0
							RotateEntity(d\OBJ, 0.0, EntityYaw(d\FrameOBJ), 0.0)
							;[End Block]
						Case ONE_SIDED_DOOR
							;[Block]
							d\OpenState = Max(0.0, d\OpenState - (FPSFactorDoubled * OpenFactor))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 80.0
							MoveEntity(d\OBJ, SinValue * OpenFactor * (-FPSFactorEx), 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * (d\FastOpen + 1) * FPSFactorEx, 0.0, 0.0)
							;[End Block]
						Case SCP_914_DOOR ; ~ Used for SCP-914 only
							;[Block]
							d\OpenState = Min(180.0, d\OpenState - (fps\Factor[0] * 1.4))
							SinValue = Sin(d\OpenState)
							FPSFactorEx = fps\Factor[0] / 114.0
							MoveEntity(d\OBJ, SinValue * (-FPSFactorEx), 0.0, 0.0)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, SinValue * FPSFactorEx, 0.0, 0.0)
							;[End Block]
					End Select
				Else
					d\FastOpen = False
					PositionEntity(d\OBJ, FrameX, FrameY, FrameZ)
					If d\OBJ2 <> 0 Then PositionEntity(d\OBJ2, FrameX, FrameY, FrameZ)
					Select d\DoorType
						Case DEFAULT_DOOR, ONE_SIDED_DOOR, SCP_914_DOOR
							;[Block]
							MoveEntity(d\OBJ, 0.0, 0.0, RoomSpacing * RoomScale)
							If d\OBJ2 <> 0 Then MoveEntity(d\OBJ2, 0.0, 0.0, RoomSpacing * RoomScale)
							;[End Block]
						Case OFFICE_DOOR, WOODEN_DOOR
							;[Block]
							MoveEntity(d\OBJ, (((d\DoorType = OFFICE_DOOR) * 92.0) + ((d\DoorType = WOODEN_DOOR) * 68.0)) * RoomScale, 0.0, 0.0)
							;[End Block]
					End Select
				EndIf
			EndIf
			If (Not (d\DoorType = WOODEN_DOOR And PlayerRoom\RoomTemplate\RoomID = r_cont2_860_1)) Then UpdateSoundOrigin(d\SoundCHN, Camera, d\FrameOBJ)
			
			If d\DoorType = BIG_DOOR
				If d\Locked = 2
					If d\OpenState > 48.0
						d\Open = False
						d\OpenState = Min(d\OpenState, 48.0)
					EndIf
				EndIf
				If EntityDistanceSquared(me\Collider, d\FrameOBJ) < 0.1225
					If d\OpenState > 6.0 And d\OpenState < 48.0 And (Not d\Open)
						If (Not me\Terminated) And (Not chs\GodMode)
							PlaySound_Strict(snd_I\Death914SFX)
							msg\DeathMsg = Format(GetLocalString("death", "door"), SubjectName)
							Kill(True)
						EndIf
					EndIf
				EndIf
			EndIf
			
			If d\DoorType <> OFFICE_DOOR And d\DoorType <> WOODEN_DOOR
				If d\ButtonsUpdateTimer =< 0.0
					; ~ Automatically disable d\AutoClose parameter in order to prevent player get stuck -- Jabka
					If d\AutoClose And d\Locked > 0 Then d\AutoClose = False
					
					Local TextureID%
					
					If d\KeyCard = KEY_005
						TextureID = BUTTON_106_TEXTURE
					ElseIf d\OpenState > 0.0 And d\OpenState < 180.0
						TextureID = BUTTON_YELLOW_TEXTURE
					ElseIf d\Locked = 1
						TextureID = BUTTON_RED_TEXTURE
					Else
						TextureID = BUTTON_GREEN_TEXTURE
					EndIf
					For i = 0 To 1
						If d\Buttons[i] <> 0 Then EntityTexture(d\Buttons[i], d_I\ButtonTextureID[TextureID])
					Next
					d\ButtonsUpdateTimer = fps\Factor[0] * 4.0
				Else
					d\ButtonsUpdateTimer = d\ButtonsUpdateTimer - fps\Factor[0]
				EndIf
				
				If d\KeyCard = KEY_MISC
					If ChannelPlaying(d\ButtonCHN)
						If d_I\AnimButton <> 0
							If me\InsideElevator
								If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToElevatorFloor = UpperFloor)
									Animate2(d_I\AnimButton, AnimTime(d_I\AnimButton), 1.0, 20.0, 2.0, False)
								Else
									Animate2(d_I\AnimButton, AnimTime(d_I\AnimButton), 21.0, 40.0, 2.0, False)
								EndIf
							Else
								Animate2(d_I\AnimButton, AnimTime(d_I\AnimButton), 1.0, 20.0, 2.0, False)
							EndIf
						EndIf
					EndIf
				EndIf
			ElseIf d\DoorType = OFFICE_DOOR
				If ChannelPlaying(d\ButtonCHN) Then Animate2(d\OBJ, AnimTime(d\OBJ), 1.0, 41.0, 1.2, False)
			EndIf
		EndIf
	Next
End Function

Global ToElevatorFloor%

; ~ Elevator Floor Constants
;[Block]
Const LowerFloor% = -1
Const NullFloor% = 0
Const UpperFloor% = 1
Const FloorOther% = 2
Const Floor1499% = 3
;[End Block]

Function UpdateElevatorPanel%(d.Doors)
	Local TextureID%, i%
	
	If me\InsideElevator
		If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToElevatorFloor = UpperFloor)
			TextureID = ELEVATOR_PANEL_UP
		Else
			TextureID = ELEVATOR_PANEL_DOWN
		EndIf
	Else
		If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToElevatorFloor = UpperFloor)
			TextureID = ELEVATOR_PANEL_DOWN
		Else
			TextureID = ELEVATOR_PANEL_UP
		EndIf
	EndIf
	
	For i = 0 To 1
		If d\ElevatorPanel[i] <> 0 Then EntityTexture(d\ElevatorPanel[i], d_I\ElevatorPanelTextureID[TextureID])
	Next
End Function

Function ClearElevatorPanelTexture%(d.Doors)
	Local i%
	
	For i = 0 To 1
		If d\ElevatorPanel[i] <> 0 Then EntityTexture(d\ElevatorPanel[i], d_I\ElevatorPanelTextureID[ELEVATOR_PANEL_IDLE])
	Next
End Function

Function UpdateElevators#(State#, door1.Doors, door2.Doors, FirstPivot%, SecondPivot%, event.Events, IgnoreRotation% = True, Blackout% = False)
	Local n.NPCs, it.Items, de.Decals
	Local x#, z#, Dist#, Dir#, i%
	
	door1\IsElevatorDoor = 1
	door2\IsElevatorDoor = 1
	If door1\Open And (Not door2\Open) And door1\OpenState = 180.0 And State < 70.0 * 7.4
		State = -1.0
		door1\Locked = 0
		If (d_I\ClosestButton = door2\Buttons[0] Lor d_I\ClosestButton = door2\Buttons[1]) And mo\MouseHit1
			OpenCloseDoor(door1)
			UpdateElevatorPanel(door2)
		EndIf
	ElseIf door2\Open And (Not door1\Open) And door2\OpenState = 180.0
		State = 1.0
		door2\Locked = 0
		If (d_I\ClosestButton = door1\Buttons[0] Lor d_I\ClosestButton = door1\Buttons[1]) And mo\MouseHit1
			OpenCloseDoor(door2)
			UpdateElevatorPanel(door1)
		EndIf
	ElseIf Abs(door1\OpenState - door2\OpenState) < 0.2
		door1\IsElevatorDoor = 2
		door2\IsElevatorDoor = 2
	EndIf
	
	door1\Locked = 1
	door2\Locked = 1
	If door1\Open
		door1\IsElevatorDoor = 3
		If me\InsideElevator
			If State < 70.0 * 7.4 Then door1\Locked = 0
			door1\IsElevatorDoor = 1
		EndIf
	EndIf
	If door2\Open
		door2\IsElevatorDoor = 3
		If me\InsideElevator
			door2\Locked = 0
			door2\IsElevatorDoor = 1
		EndIf
	EndIf
	
	Local IsSceneTriggered% = False
	
	If n_I\Curr096 <> Null
		If n_I\Curr096\State > 1.0 And n_I\Curr096\Target = Null And (InFacility = NullFloor) And me\InsideElevator And (Not chs\NoTarget) Then IsSceneTriggered = True
	EndIf
	If (Not IsSceneTriggered)
		If (Not door1\Open) And (Not door2\Open)
			If me\InsideElevator Then CanSave = 0
			door1\Locked = 1
			door2\Locked = 1
			If door1\OpenState = 0.0 And door2\OpenState = 0.0
				Local PlayerX# = EntityX(me\Collider, True)
				Local PlayerY# = EntityY(me\Collider, True)
				Local PlayerZ# = EntityZ(me\Collider, True)
				Local FirstPivotX# = EntityX(FirstPivot, True)
				Local FirstPivotY# = EntityY(FirstPivot, True)
				Local FirstPivotZ# = EntityZ(FirstPivot, True)
				Local FirstPivotYaw# = EntityYaw(FirstPivot, True)
				Local SecondPivotX# = EntityX(SecondPivot, True)
				Local SecondPivotY# = EntityY(SecondPivot, True)
				Local SecondPivotZ# = EntityZ(SecondPivot, True)
				Local SecondPivotYaw# = EntityYaw(SecondPivot, True)
				Local Minus022# = (280.0 * RoomScale) - 0.22
				Local Plus022# = ((-280.0) * RoomScale) + 0.22
				Local FPSFactor01# = fps\Factor[0] * 0.1
				Local OBJPosX#, OBJPosY#, OBJPosZ#
				Local IsInside% = False
				Local PowerUp%
				
				If State < 0.0
					State = State - fps\Factor[0]
					IsInside = IsInsideElevator(PlayerX, PlayerY, PlayerZ, FirstPivot)
					If IsInside
						; ~ Not sure if using local ``Blackout`` is a good idea. Better to rewrite this part cause I don't like it. This code was a hot idea, so the code is kinda dumb
						If Blackout
							If State > -250.0 Lor State =< -500.0
								If (Not ChannelPlaying(door1\SoundCHN2))
									door1\SoundCHN2 = PlaySound_Strict(snd_I\ElevatorMoveSFX)
									UpdateElevatorPanel(door1)
								EndIf
								
								PowerUp = 1.0 + (State =< -500.0)
								
								me\CameraShake = Sin(Abs(State) / (3.0 * PowerUp)) * (0.3 * PowerUp)
								If State >= -235.0 And State - fps\Factor[0] < -235.0 Then PlaySound_Strict(LoadTempSound("SFX\Room\Blackout.ogg"))
							ElseIf State > -500.0
								If ChannelPlaying(door1\SoundCHN2)
									PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Bang2.ogg"))
									me\LightBlink = 6.5
									StopChannel(door1\SoundCHN2) : door1\SoundCHN2 = 0
									ClearElevatorPanelTexture(door1)
									ClearElevatorPanelTexture(door2)
									me\BigCameraShake = 5.3
								EndIf
								If State >= -490.0 And State - fps\Factor[0] < -490.0 Then PlaySound_Strict(snd_I\TeslaPowerUpSFX)
							EndIf
						Else
							If (Not ChannelPlaying(door1\SoundCHN2))
								door1\SoundCHN2 = PlaySound_Strict(snd_I\ElevatorMoveSFX)
								UpdateElevatorPanel(door1)
							EndIf
							
							me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						EndIf
					EndIf
					
					If ((Not Blackout) And State < -500.0) Lor State < -1000.0
						door1\Locked = 1
						door2\Locked = 0
						State = 0.0
						If IsInside
							If (Not IgnoreRotation)
								Dist = Distance(PlayerX, FirstPivotX, PlayerZ, FirstPivotZ)
								Dir = PointDirection(PlayerX, PlayerZ, FirstPivotX, FirstPivotZ)
								Dir = Dir + SecondPivotYaw - FirstPivotYaw
								Dir = WrapAngle(Dir)
								x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
								z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
								RotateEntity(me\Collider, EntityPitch(me\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(me\Collider, True), FirstPivotYaw), EntityRoll(me\Collider, True), True)
							Else
								x = Max(Min((PlayerX - FirstPivotX), Minus022), Plus022)
								z = Max(Min((PlayerZ - FirstPivotZ), Minus022), Plus022)
							EndIf
							
							TeleportEntity(me\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (PlayerY - FirstPivotY), SecondPivotZ + z, 0.3, True)
							me\DropSpeed = 0.0
							UpdateLightsTimer = 0.0
							UpdateLightVolume()
							UpdateDoors()
							UpdateRooms()
							
							door1\SoundCHN = PlaySound2(OpenDoorSFX(ELEVATOR_DOOR, Rand(0, 2)), Camera, door1\OBJ)
						EndIf
						
						For n.NPCs = Each NPCs
							OBJPosX = EntityX(n\Collider, True) : OBJPosY = EntityY(n\Collider, True) : OBJPosZ = EntityZ(n\Collider, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, OBJPosZ, FirstPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(n\Collider, EntityPitch(n\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(n\Collider, True), FirstPivotYaw), EntityRoll(n\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(n\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, n\CollRadius, True)
							EndIf
						Next
						
						For it.Items = Each Items
							OBJPosX = EntityX(it\Collider, True) : OBJPosY = EntityY(it\Collider, True) : OBJPosZ = EntityZ(it\Collider, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, OBJPosZ, FirstPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(it\Collider, EntityPitch(it\Collider, True), SecondPivotYaw + AngleDist(EntityYaw(it\Collider, True), FirstPivotYaw), EntityRoll(it\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(it\Collider, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, 0.01, True)
								it\DistTimer = 0.0
								UpdateItems()
							EndIf
						Next
						
						For de.Decals = Each Decals
							OBJPosX = EntityX(de\OBJ, True) : OBJPosY = EntityY(de\OBJ, True) : OBJPosZ = EntityZ(de\OBJ, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, FirstPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, FirstPivotX, EntityZ(de\OBJ, True), FirstPivotZ)
									Dir = PointDirection(OBJPosX, EntityZ(de\OBJ, True), FirstPivotX, FirstPivotZ)
									Dir = Dir + SecondPivotYaw - FirstPivotYaw
									Dir = WrapAngle(Dir)
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), SecondPivotYaw + AngleDist(EntityYaw(de\OBJ, True), FirstPivotYaw), EntityRoll(de\OBJ, True), True)
								Else
									x = Max(Min((OBJPosX - FirstPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - FirstPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(de\OBJ, SecondPivotX + x, FPSFactor01 + SecondPivotY + (OBJPosY - FirstPivotY), SecondPivotZ + z, -0.01, True)
								UpdateDecals()
							EndIf
						Next
						OpenCloseDoor(door2, (Not me\InsideElevator))
						door1\Open = False
						
						; ~ Return to default panel texture
						ClearElevatorPanelTexture(door1)
						ClearElevatorPanelTexture(door2)
						PlaySound2(snd_I\ElevatorBeepSFX, Camera, FirstPivot, 4.0)
					EndIf
				Else
					State = State + fps\Factor[0]
					IsInside = IsInsideElevator(PlayerX, PlayerY, PlayerZ, SecondPivot)
					If IsInside
						; ~ Not sure if using local ``Blackout`` is a good idea. Better to rewrite this part cause I don't like it. This code was a hot idea, so the code is kinda dumb
						If Blackout
							If State < 250.0 Lor State => 500.0
								If (Not ChannelPlaying(door2\SoundCHN2))
									door2\SoundCHN2 = PlaySound_Strict(snd_I\ElevatorMoveSFX)
									UpdateElevatorPanel(door2)
								EndIf
								
								PowerUp = 1.0 + (State => 500.0)
								
								me\CameraShake = Sin(Abs(State) / (3.0 * PowerUp)) * (0.3 * PowerUp)
								If State <= 235.0 And State + fps\Factor[0] > 235.0 Then PlaySound_Strict(LoadTempSound("SFX\Room\Blackout.ogg"))
							ElseIf State < 500.0
								If ChannelPlaying(door2\SoundCHN2)
									PlaySound_Strict(LoadTempSound("SFX\Room\Intro\Bang2.ogg"))
									me\LightBlink = 6.5
									StopChannel(door2\SoundCHN2) : door2\SoundCHN2 = 0
									ClearElevatorPanelTexture(door1)
									ClearElevatorPanelTexture(door2)
									me\BigCameraShake = 5.3
								EndIf
								If State <= 490.0 And State + fps\Factor[0] > 490.0 Then PlaySound_Strict(snd_I\TeslaPowerUpSFX)
							EndIf
						Else
							If (Not ChannelPlaying(door2\SoundCHN2))
								door2\SoundCHN2 = PlaySound_Strict(snd_I\ElevatorMoveSFX)
								UpdateElevatorPanel(door2)
							EndIf
							
							me\CameraShake = Sin(Abs(State) / 3.0) * 0.3
						EndIf
					EndIf
					
					If ((Not Blackout) And State > 500.0) Lor State > 1000.0
						door1\Locked = 0
						door2\Locked = 1
						State = 0.0
						If IsInside
							If (Not IgnoreRotation)
								Dist = Distance(PlayerX, SecondPivotX, PlayerZ, SecondPivotZ)
								Dir = PointDirection(PlayerX, PlayerZ, SecondPivotX, SecondPivotZ)
								Dir = Dir + FirstPivotYaw - SecondPivotYaw
								x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
								z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
								RotateEntity(me\Collider, EntityPitch(me\Collider, True), FirstPivotYaw + AngleDist(EntityYaw(me\Collider, True), SecondPivotYaw), EntityRoll(me\Collider, True), True)
							Else
								x = Max(Min((PlayerX - SecondPivotX), Minus022), Plus022)
								z = Max(Min((PlayerZ - SecondPivotZ), Minus022), Plus022)
							EndIf
							TeleportEntity(me\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (PlayerY - SecondPivotY), FirstPivotZ + z, 0.3, True)
							me\DropSpeed = 0.0
							UpdateLightsTimer = 0.0
							UpdateLightVolume()
							UpdateDoors()
							UpdateRooms()
							
							door2\SoundCHN = PlaySound2(OpenDoorSFX(ELEVATOR_DOOR, Rand(0, 2)), Camera, door2\OBJ)
						EndIf
						
						For n.NPCs = Each NPCs
							OBJPosX = EntityX(n\Collider, True) : OBJPosY = EntityY(n\Collider, True) : OBJPosZ = EntityZ(n\Collider, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(n\Collider, EntityPitch(n\Collider, True), FirstPivotYaw + AngleDist(EntityYaw(n\Collider, True), SecondPivotYaw), EntityRoll(n\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(n\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, n\CollRadius, True)
							EndIf
						Next
						
						For it.Items = Each Items
							OBJPosX = EntityX(it\Collider, True) : OBJPosY = EntityY(it\Collider, True) : OBJPosZ = EntityZ(it\Collider, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(it\Collider, EntityPitch(it\Collider, True), FirstPivotYaw + AngleDist(EntityYaw(it\Collider, True), SecondPivotYaw), EntityRoll(it\Collider, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(it\Collider, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, 0.01, True)
								it\DistTimer = 0.0
								UpdateItems()
							EndIf
						Next
						
						For de.Decals = Each Decals
							OBJPosX = EntityX(de\OBJ, True) : OBJPosY = EntityY(de\OBJ, True) : OBJPosZ = EntityZ(de\OBJ, True)
							If IsInsideElevator(OBJPosX, OBJPosY, OBJPosZ, SecondPivot)
								If (Not IgnoreRotation)
									Dist = Distance(OBJPosX, SecondPivotX, OBJPosZ, SecondPivotZ)
									Dir = PointDirection(OBJPosX, OBJPosZ, SecondPivotX, SecondPivotZ)
									Dir = Dir + FirstPivotYaw - SecondPivotYaw
									x = Max(Min(Cos(Dir) * Dist, Minus022), Plus022)
									z = Max(Min(Sin(Dir) * Dist, Minus022), Plus022)
									RotateEntity(de\OBJ, EntityPitch(de\OBJ, True), FirstPivotYaw + AngleDist(EntityYaw(de\OBJ, True), SecondPivotYaw), EntityRoll(de\OBJ, True), True)
								Else
									x = Max(Min((OBJPosX - SecondPivotX), Minus022), Plus022)
									z = Max(Min((OBJPosZ - SecondPivotZ), Minus022), Plus022)
								EndIf
								TeleportEntity(de\OBJ, FirstPivotX + x, FPSFactor01 + FirstPivotY + (OBJPosY - SecondPivotY), FirstPivotZ + z, -0.01, True)
								UpdateDecals()
							EndIf
						Next
						OpenCloseDoor(door1, (Not me\InsideElevator))
						door2\Open = False
						
						; ~ Return to default panel texture
						ClearElevatorPanelTexture(door1)
						ClearElevatorPanelTexture(door2)
						PlaySound2(snd_I\ElevatorBeepSFX, Camera, SecondPivot, 4.0)
					EndIf
				EndIf
			EndIf
			For i = 0 To 1
				EntityTexture(door1\Buttons[i], d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE])
				EntityTexture(door2\Buttons[i], d_I\ButtonTextureID[BUTTON_YELLOW_TEXTURE])
			Next
		EndIf
	Else
		Local PrevEventState# = State
		Local emit.Emitter
		
		If State < 0.0
			State = 0.0
			PrevEventState = 0.0
		EndIf
		
		If door1\OpenState = 0.0 And (Not door1\Open)
			If me\InsideElevator
				If State = 0.0
					TeleportEntity(n_I\Curr096\Collider, EntityX(door1\FrameOBJ), EntityY(door1\FrameOBJ) + 1.0, EntityZ(door1\FrameOBJ), n_I\Curr096\CollRadius)
					PointEntity(n_I\Curr096\Collider, FirstPivot)
					RotateEntity(n_I\Curr096\Collider, 0.0, EntityYaw(n_I\Curr096\Collider), 0.0)
					MoveEntity(n_I\Curr096\Collider, 0.0, 0.0, -0.5)
					ResetEntity(n_I\Curr096\Collider)
					n_I\Curr096\State = 6.0
					SetNPCFrame(n_I\Curr096, 0.0)
					LoadEventSound(event, "SFX\SCP\096\ElevatorSlam.ogg")
					State = State + (fps\Factor[0] * 1.4)
					door1\Locked = 1
					UpdateElevatorPanel(door1)
				EndIf
			EndIf
		EndIf
		
		If State > 0.0
			If PrevEventState = 0.0 Then event\SoundCHN = PlaySound_Strict(event\Sound, True)
			
			If State > 70.0 * 1.9 And State < (70.0 * 2.0) + fps\Factor[0]
				me\BigCameraShake = 7.0
			ElseIf State > 70.0 * 4.2 And State < (70.0 * 4.25) + fps\Factor[0]
				me\BigCameraShake = 2.0
			ElseIf State > 70.0 * 5.9 And State < (70.0 * 5.95) + fps\Factor[0]
				me\BigCameraShake = 2.0
			ElseIf State > 70.0 * 7.25 And State < (70.0 * 7.3) + fps\Factor[0]
				me\BigCameraShake = 2.0
				door1\FastOpen = True : door1\Open = True
				emit.Emitter = SetEmitter(Null, EntityX(door1\OBJ, True), EntityY(door1\OBJ, True), EntityZ(door1\OBJ, True), 16)
				EntityParent(emit\Owner, door1\OBJ)
				n_I\Curr096\State = 5.0
				n_I\Curr096\LastSeen = 1.0
			ElseIf State > 70.0 * 8.1 And State < 70.0 * 8.15 + fps\Factor[0]
				me\BigCameraShake = 2.0
			EndIf
			
			If State <= 70.0 * 8.1 Then door1\OpenState = Min(door1\OpenState, 20.0)
			State = State + fps\Factor[0]
		EndIf
	EndIf
	Return(State)
End Function

Global CODE_DR_MAYNARD%, CODE_DR_GEARS, CODE_O5_COUNCIL%, CODE_MAINTENANCE_TUNNELS%
; ~ Doors Code Constants
;[Block]
Const CODE_DR_HARP% = 7816
Const CODE_DR_L% = 2411
Const CODE_CONT1_035% = 5731
Const CODE_LOCKED% = -1
;[End Block]

Function UseDoor%(PlaySFX% = True)
	Local Temp%, i%
	
	If SelectedItem <> Null Then Temp = GetUsingItem(SelectedItem)
	If d_I\ClosestDoor\KeyCard > KEY_MISC
		If SelectedItem = Null
			CreateMsg(GetLocalString("msg", "key.require"))
			d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[0], Camera, d_I\ClosestButton)
			Return
		Else
			If Temp <= KEY_MISC
				CreateMsg(GetLocalString("msg", "key.require"))
			Else
				If Temp = KEY_CARD_6
					CreateMsg(GetLocalString("msg", "key.slot.6"))
				Else
					If d_I\ClosestDoor\Locked = 1
						If Temp = KEY_005
							CreateMsg(GetLocalString("msg", "key.nothappend.005"))
						Else
							CreateMsg(GetLocalString("msg", "key.nothappend"))
						EndIf
					Else
						If Temp = KEY_005
							CreateMsg(GetLocalString("msg", "key.005"))
						Else
							If Temp < d_I\ClosestDoor\KeyCard
								If d_I\ClosestDoor\KeyCard = KEY_005
									CreateMsg(GetLocalString("msg", "key.required.106"))
								Else
									CreateMsg(Format(GetLocalString("msg", "key.higher"), d_I\ClosestDoor\KeyCard - 2))
								EndIf
							Else
								CreateMsg(GetLocalString("msg", "key.slot"))
							EndIf
						EndIf
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
			If (d_I\ClosestDoor\Locked <> 1) And (((Temp > KEY_MISC) And (Temp <> KEY_CARD_6) And (Temp >= d_I\ClosestDoor\KeyCard)) Lor (Temp = KEY_005))
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\KeyCardSFX[0], Camera, d_I\ClosestButton)
			Else
				If Temp <= KEY_MISC
					d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[0], Camera, d_I\ClosestButton)
				Else
					d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\KeyCardSFX[1], Camera, d_I\ClosestButton)
				EndIf
				Return
			EndIf
		EndIf
	ElseIf d_I\ClosestDoor\KeyCard > KEY_860 And d_I\ClosestDoor\KeyCard < KEY_MISC
		If SelectedItem = Null
			CreateMsg(GetLocalString("msg", "dna.denied_1"))
			d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[1], Camera, d_I\ClosestButton)
			Return
		Else
			If ((Temp >= KEY_MISC) Lor (Temp < KEY_HAND_YELLOW)) And (Temp <> KEY_005)
				CreateMsg(GetLocalString("msg", "dna.denied_1"))
			Else
				If (d_I\ClosestDoor\KeyCard <> Temp) And (Temp <> KEY_005)
					CreateMsg(GetLocalString("msg", "dna.denied_2"))
				Else
					If d_I\ClosestDoor\Locked = 1
						If Temp = KEY_005
							CreateMsg(GetLocalString("msg", "key.nothappend.005"))
						Else
							CreateMsg(GetLocalString("msg", "dna.nothappend"))
						EndIf
					Else
						If Temp = KEY_005
							CreateMsg(GetLocalString("msg", "dna.granted.005"))
						Else
							CreateMsg(GetLocalString("msg", "dna.granted"))
						EndIf
					EndIf
				EndIf
				SelectedItem = Null
			EndIf
			If (d_I\ClosestDoor\Locked = 0) And ((Temp = d_I\ClosestDoor\KeyCard) Lor (Temp = KEY_005))
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[0], Camera, d_I\ClosestButton)
			Else
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[1], Camera, d_I\ClosestButton)
				Return
			EndIf
		EndIf
	ElseIf d_I\ClosestDoor\Code <> 0
		If SelectedItem = Null
			If (d_I\ClosestDoor\Locked = 0) And (d_I\ClosestDoor\Code <> CODE_LOCKED) And (d_I\ClosestDoor\Code = Int(msg\KeyPadInput))
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[0], Camera, d_I\ClosestButton)
			Else
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[1], Camera, d_I\ClosestButton)
				Return
			EndIf
		Else
			If Temp = KEY_005
				If d_I\ClosestDoor\Locked = 1
					CreateMsg(GetLocalString("msg", "keypad.nothappend.005"))
				Else
					CreateMsg(GetLocalString("msg", "keypad.nothappend"))
				EndIf
			EndIf
			SelectedItem = Null
			
			If (d_I\ClosestDoor\Locked = 0) And (d_I\ClosestDoor\Code <> CODE_LOCKED) And (Temp = KEY_005)
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[0], Camera, d_I\ClosestButton)
			Else
				d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\ScannerSFX[1], Camera, d_I\ClosestButton)
				Return
			EndIf
		EndIf
		
		
		Select d_I\ClosestDoor\Code
			Case CODE_DR_MAYNARD
				;[Break]
				GiveAchievement("maynard")
				;[End Break]
			Case CODE_DR_GEARS
				;[Break]
				GiveAchievement("gears")
				;[End Break]
			Case CODE_DR_HARP
				;[Break]
				GiveAchievement("harp")
				;[End Break]
			Case CODE_O5_COUNCIL
				;[Break]
				GiveAchievement("o5")
				;[End Break]
		End Select
	Else
		If d_I\ClosestDoor\DoorType = WOODEN_DOOR Lor d_I\ClosestDoor\DoorType = OFFICE_DOOR
			If d_I\ClosestDoor\Locked > 0
				If SelectedItem = Null
					CreateMsg(GetLocalString("msg", "wood.wontbudge"))
					If d_I\ClosestDoor\DoorType = OFFICE_DOOR
						d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorBudgeSFX[0], Camera, d_I\ClosestButton)
						SetAnimTime(d_I\ClosestDoor\OBJ, 1.0)
					Else
						d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorBudgeSFX[1], Camera, d_I\ClosestButton)
					EndIf
				Else
					If (Temp > KEY_860) And (Temp <> KEY_005)
						CreateMsg(GetLocalString("msg", "wood.wontbudge"))
					Else
						If d_I\ClosestDoor\Locked = 2 Lor ((Temp <> d_I\ClosestDoor\KeyCard) And (Temp <> KEY_005))
							CreateMsg(GetLocalString("msg", "wood.nothappend.005"))
						Else
							CreateMsg(GetLocalString("msg", "wood.unlock"))
							d_I\ClosestDoor\Locked = 0
						EndIf
						SelectedItem = Null
					EndIf
					If (Temp > KEY_860) And (Temp <> KEY_005)
						If d_I\ClosestDoor\DoorType = OFFICE_DOOR
							d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorBudgeSFX[0], Camera, d_I\ClosestButton)
							SetAnimTime(d_I\ClosestDoor\OBJ, 1.0)
						Else
							d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorBudgeSFX[1], Camera, d_I\ClosestButton)
						EndIf
					Else
						d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorLockSFX, Camera, d_I\ClosestButton)
					EndIf
				EndIf
				Return
			Else
				If d_I\ClosestDoor\DoorType = OFFICE_DOOR
					d_I\ClosestDoor\ButtonCHN = PlaySound2(snd_I\DoorBudgeSFX[0], Camera, d_I\ClosestButton)
					SetAnimTime(d_I\ClosestDoor\OBJ, 1.0)
				EndIf
			EndIf
		Else
			If d_I\ClosestDoor\Locked = 1
				If d_I\ClosestDoor\DoorType = ELEVATOR_DOOR
					If (Not d_I\ClosestDoor\IsElevatorDoor > 0)
						CreateMsg(GetLocalString("msg", "elev.broken"))
						d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[1], Camera, d_I\ClosestButton)
						If me\InsideElevator
							If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToElevatorFloor = UpperFloor)
								SetAnimTime(d_I\ClosestButton, 1.0)
							Else
								SetAnimTime(d_I\ClosestButton, 21.0)
							EndIf
						Else
							SetAnimTime(d_I\ClosestButton, 1.0)
						EndIf
						Return
					Else
						If d_I\ClosestDoor\IsElevatorDoor = 1
							CreateMsg(GetLocalString("msg", "elev.called"))
						ElseIf d_I\ClosestDoor\IsElevatorDoor = 3
							CreateMsg(GetLocalString("msg", "elev.floor"))
						ElseIf msg\Txt <> GetLocalString("msg", "elev.called")
							Select Rand(10)
								Case 1
									;[Block]
									CreateMsg(GetLocalString("msg", "elev.stop"))
									;[End Block]
								Case 2
									;[Block]
									CreateMsg(GetLocalString("msg", "elev.faster"))
									;[End Block]
								Case 3
									;[Block]
									CreateMsg(GetLocalString("msg", "elev.mav"))
									;[End Block]
								Default
									;[Block]
									CreateMsg(GetLocalString("msg", "elev.already"))
									;[End Block]
							End Select
						Else
							CreateMsg(GetLocalString("msg", "elev.already"))
						EndIf
						d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[0], Camera, d_I\ClosestButton)
						SetAnimTime(d_I\ClosestButton, 1.0)
						Return
					EndIf
				Else
					If d_I\ClosestDoor\Open
						CreateMsg(GetLocalString("msg", "button.nothappend"))
					Else
						CreateMsg(GetLocalString("msg", "button.locked"))
					EndIf
					d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[1], Camera, d_I\ClosestButton)
					SetAnimTime(d_I\ClosestButton, 1.0)
					Return
				EndIf
			Else
				d_I\ClosestDoor\ButtonCHN = PlaySound2(ButtonSFX[0], Camera, d_I\ClosestButton)
				If me\InsideElevator
					If InFacility = LowerFloor Lor (InFacility <> UpperFloor And ToElevatorFloor = UpperFloor)
						SetAnimTime(d_I\ClosestButton, 1.0)
					Else
						SetAnimTime(d_I\ClosestButton, 21.0)
					EndIf
				Else
					SetAnimTime(d_I\ClosestButton, 1.0)
				EndIf
			EndIf
		EndIf
	EndIf
	
	OpenCloseDoor(d_I\ClosestDoor, PlaySFX)
End Function

Function OpenCloseDoor%(d.Doors, PlaySFX% = True, PlayCautionSFX% = False)
	d\PlayCautionSFX = PlayCautionSFX
	
	d\Open = (Not d\Open)
	If d\LinkedDoor <> Null
		d\LinkedDoor\Open = (Not d\LinkedDoor\Open)
		d\PlayCautionSFX = True
		d\LinkedDoor\PlayCautionSFX = True
	EndIf
	
	If d\Open
		If d\LinkedDoor <> Null Then d\LinkedDoor\TimerState = d\LinkedDoor\Timer
		d\TimerState = d\Timer
	EndIf
	
	Local DoorType% = d\DoorType
	
	If DoorType = ONE_SIDED_DOOR Lor DoorType = SCP_914_DOOR Then DoorType = DEFAULT_DOOR
	
	If PlaySFX
		Local SoundRand% = Rand(0, 2)
		
		If DoorType = WOODEN_DOOR
			If PlayerRoom\RoomTemplate\RoomID = r_cont2_860_1
				SoundRand = 2
			Else
				SoundRand = Rand(0, 1)
			EndIf
		EndIf
		
		Local SoundOpen%
		Local SoundClose% = CloseDoorSFX(DoorType, SoundRand)
		
		If DoorType = BIG_DOOR And d\Locked = 2
			SoundOpen = snd_I\BigDoorErrorSFX[Rand(0, 2)]
		Else
			SoundOpen = OpenDoorSFX(DoorType, SoundRand)
		EndIf
		
		If d\Open
			d\SoundCHN = PlaySound2(SoundOpen, Camera, d\OBJ)
		Else
			d\SoundCHN = PlaySound2(SoundClose, Camera, d\OBJ)
		EndIf
	EndIf
End Function

Function RemoveDoor%(d.Doors)
	Local i%
	
	FreeEntity(d\OBJ) : d\OBJ = 0
	If d\OBJ2 <> 0 Then FreeEntity(d\OBJ2) : d\OBJ2 = 0
	For i = 0 To 1
		If d\Buttons[i] <> 0 Then FreeEntity(d\Buttons[i]) : d\Buttons[i] = 0
		If d\ElevatorPanel[i] <> 0 Then FreeEntity(d\ElevatorPanel[i]) : d\ElevatorPanel[i] = 0
	Next
	FreeEntity(d\FrameOBJ) : d\FrameOBJ = 0
	Delete(d)
End Function

Type Decals
	Field OBJ%, Surf%, ID%
	Field Size#, SizeChange#, MaxSize#
	Field Alpha#, AlphaChange#
	Field BlendMode%, FX%
	Field R%, G%, B%
	Field Timer#, LifeTime#
End Type

Function CreateDecal.Decals(ID%, x#, y#, z#, Pitch#, Yaw#, Roll#, Size# = 1.0, Alpha# = 1.0, FX% = 0, BlendMode% = 1, R% = 0, G% = 0, B% = 0)
	Local de.Decals
	
	de.Decals = New Decals
	de\ID = ID
	de\Size = Size
	de\Alpha = Alpha
	de\FX = FX : de\BlendMode = BlendMode
	de\R = R : de\G = G : de\B = B
	de\MaxSize = 1.0
	
	de\OBJ = CreateMesh()
	de\Surf = CreateSurface(de\OBJ)
	
	Local v0% = AddVertex(de\Surf, -1.0, 1.0, 0.0, 0.0, 0.0)
	Local v1% = AddVertex(de\Surf, 1.0, 1.0, 0.0, 1.0, 0.0)
	Local v2% = AddVertex(de\Surf, 1.0, -1.0, 0.0, 1.0, 1.0)
	Local v3% = AddVertex(de\Surf, -1.0, -1.0, 0.0, 0.0, 1.0)
	
	AddTriangle(de\Surf, v0, v1, v2)
	AddTriangle(de\Surf, v0, v2, v3)
	
	PositionEntity(de\OBJ, x, y, z, True)
	ScaleEntity(de\OBJ, Size, Size, 1.0, True)
	RotateEntity(de\OBJ, Pitch, Yaw, Roll, True)
	EntityTexture(de\OBJ, de_I\DecalTextureID[ID])
	EntityAlpha(de\OBJ, Alpha)
	EntityFX(de\OBJ, FX)
	EntityBlend(de\OBJ, BlendMode)
	If R <> 0 Lor G <> 0 Lor B <> 0 Then EntityColor(de\OBJ, R, G, B)
	
	UpdateNormals(de\OBJ)
	HideEntity(de\OBJ)
	
	If de_I\DecalTextureID[ID] = 0 Then RuntimeError(Format(GetLocalString("runerr", "decals"), ID))
	
	Return(de)
End Function

Function RemoveDecal%(de.Decals)
	FreeEntity(de\OBJ) : de\OBJ = 0
	de\Surf = 0 
	Delete(de)
End Function

Function UpdateDecals%()
	Local de.Decals
	
	For de.Decals = Each Decals
		If EntityDistanceSquared(de\OBJ, me\Collider) < PowTwo(HideDistance)
			If EntityHidden(de\OBJ) Then ShowEntity(de\OBJ)
			
			Local DecalPosY# = EntityY(de\OBJ, True)
			
			If de\SizeChange <> 0.0
				de\Size = de\Size + (de\SizeChange * fps\Factor[0])
				ScaleEntity(de\OBJ, de\Size, de\Size, 1.0, True)
				
				Select de\ID
					Case 0
						;[Block]
						If de\Timer <= 0.0
							Local Angle# = Rnd(360.0)
							Local Temp# = Rnd(de\Size)
							Local de2.Decals
							
							de2.Decals = CreateDecal(DECAL_CORROSIVE_2, EntityX(de\OBJ, True) + Cos(Angle) * Temp, DecalPosY - 0.0005, EntityZ(de\OBJ, True) + Sin(Angle) * Temp, EntityPitch(de\OBJ, True), EntityYaw(de\OBJ, True), EntityRoll(de\OBJ, True), Rnd(0.1, 0.5))
							EntityParent(de2\OBJ, GetParent(de\OBJ))
							PlaySound2(snd_I\DecaySFX[Rand(3)], Camera, de2\OBJ, 10.0, Rnd(0.1, 0.5))
							de\Timer = Rnd(50.0, 100.0)
						Else
							de\Timer = de\Timer - fps\Factor[0]
						EndIf
						;[End Block]
				End Select
				
				If de\Size >= de\MaxSize
					de\SizeChange = 0.0
					de\Size = de\MaxSize
				EndIf
			EndIf
			
			If de\AlphaChange <> 0.0
				de\Alpha = Min(de\Alpha + (fps\Factor[0] * de\AlphaChange), 1.0)
				EntityAlpha(de\OBJ, de\Alpha)
			EndIf
			If de\LifeTime > 0.0 Then de\LifeTime = Max(de\LifeTime - fps\Factor[0], 5.0)
			
			Local Dist# = DistanceSquared(EntityX(me\Collider), EntityX(de\OBJ, True), EntityZ(me\Collider), EntityZ(de\OBJ, True))
			Local ActualSize# = PowTwo(de\Size * 0.8)
			
			If (Dist < ActualSize) And (Int(EntityPitch(de\OBJ, True)) = 90.0) And (Abs((EntityY(me\Collider) - 0.3) - DecalPosY) < 0.05)
				Select de\ID
					Case 0
						;[Block]
						If de\FX <> 1
							DecalStep = 1
							me\CurrSpeed = CurveValue(0.0, me\CurrSpeed, Max(100.0 - (Sqr(ActualSize - Dist)) * 15.0, 1.0))
							me\CrouchState = Max(me\CrouchState, (ActualSize - Dist) / 2.0)
						Else
							DustParticleChance = Max(DustParticleChance - 10, 10)
						EndIf
						;[End Block]
					Case 2, 3, 4, 5, 6, 7, 16, 17, 18, 20
						;[Block]
						DecalStep = 2
						;[End Block]
				End Select
			EndIf
			
			If de\Size <= 0.0 Lor de\Alpha <= 0.0 Lor de\LifeTime = 5.0 Then RemoveDecal(de)
		Else
			If (Not EntityHidden(de\OBJ)) Then HideEntity(de\OBJ)
		EndIf
	Next
End Function

Type SecurityCams
	Field BaseOBJ%, CameraOBJ%, MonitorOBJ%, Pvt%
	Field ScrOBJ%
	Field Screen%, Cam%, ScrOverlay%
	Field Angle#, Turn#, CurrAngle#
	Field State#, PlayerState%
	Field SoundCHN%
	Field InSight% = False
	Field RenderInterval#
	Field room.Rooms
	Field FollowPlayer%
	Field CoffinEffect%
	Field AllowSaving%
	Field Dir%
	Field ScriptedMonitor% = False
	Field ScriptedCamera% = False
End Type

Function CreateSecurityCam.SecurityCams(room.Rooms, x1#, y1#, z1#, Pitch1#, Screen% = False, x2# = 0.0, y2# = 0.0, z2# = 0.0, Pitch2# = 0.0, Yaw2# = 0.0, Roll2# = 0.0)
	Local sc.SecurityCams
	
	sc.SecurityCams = New SecurityCams
	sc\room = room
	sc\ScriptedCamera = False
	sc\ScriptedMonitor = False
	
	sc\BaseOBJ = CopyEntity(sc_I\CamModelID[CAM_BASE_MODEL])
	ScaleEntity(sc\BaseOBJ, 0.0015, 0.0015, 0.0015)
	PositionEntity(sc\BaseOBJ, x1, y1, z1)
	If room <> Null Then EntityParent(sc\BaseOBJ, room\OBJ)
	HideEntity(sc\BaseOBJ)
	
	sc\CameraOBJ = CopyEntity(sc_I\CamModelID[CAM_HEAD_MODEL])
	ScaleEntity(sc\CameraOBJ, 0.01, 0.01, 0.01)
	RotateEntity(sc\CameraOBJ, Pitch1, 0.0, 0.0)
	HideEntity(sc\CameraOBJ)
	
	sc\Screen = Screen
	If Screen
		sc\AllowSaving = True
		
		sc\RenderInterval = opt\SecurityCamRenderIntervalLevel
		
		Local Scale# = RoomScale * 1.8
		Local MonWidth# = MeshWidth(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.475
		Local MonHeight# = MeshHeight(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL]) * Scale * 0.475
		
		sc\ScrOBJ = CreateSprite()
		ScaleSprite(sc\ScrOBJ, MonWidth, MonHeight)
		PositionEntity(sc\ScrOBJ, x2, y2, z2)
		RotateEntity(sc\ScrOBJ, Pitch2, Yaw2, Roll2)
		EntityFX(sc\ScrOBJ, 17)
		SpriteViewMode(sc\ScrOBJ, 2)
		EntityTexture(sc\ScrOBJ, sc_I\ScreenTex)
		If room <> Null Then EntityParent(sc\ScrOBJ, room\OBJ)
		HideEntity(sc\ScrOBJ)
		
		sc\ScrOverlay = CreateSprite(sc\ScrOBJ)
		ScaleSprite(sc\ScrOverlay, MonWidth, MonHeight)
		MoveEntity(sc\ScrOverlay, 0.0, 0.0, -0.005)
		EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
		SpriteViewMode(sc\ScrOverlay, 2)
		EntityFX(sc\ScrOverlay, 1)
		EntityBlend(sc\ScrOverlay, 3)
		HideEntity(sc\ScrOverlay)
		
		sc\MonitorOBJ = CopyEntity(mon_I\MonitorModelID[MONITOR_DEFAULT_MODEL], sc\ScrOBJ)
		ScaleEntity(sc\MonitorOBJ, Scale, Scale, Scale)
		
		sc\Cam = CreateCamera()
		CameraViewport(sc\Cam, 0, 0, 512, 512)
		CameraRange(sc\Cam, 0.05, 8.0)
		CameraZoom(sc\Cam, 0.8)
		If sc\room\RoomTemplate\RoomID <> r_cont1_173_intro
			CameraFogMode(sc\Cam, 1)
			CameraFogRange(sc\Cam, 0.1, 6.0)
		EndIf
		
		HideEntity(sc\Cam)
	EndIf
	
	Return(sc)
End Function

Function TurnOffSecurityCam%(room.Rooms, TurnOff%)
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		If sc\room = room
			If TurnOff
				If sc\Screen
					If sc\CoffinEffect <> 1 Then sc\CoffinEffect = 0
					HideEntity(sc\ScrOverlay)
					HideEntity(sc\ScrOBJ)
					sc\Screen = False
				EndIf
			Else
				If (Not sc\Screen)
					If sc\CoffinEffect = 0 Then sc\CoffinEffect = 2
					ShowEntity(sc\ScrOverlay)
					ShowEntity(sc\ScrOBJ)
					sc\Screen = True
				EndIf
			EndIf
			Exit
		EndIf
	Next
End Function

Function UpdateSecurityCams%()
	CatchErrors("UpdateSecurityCams()")
	
	Local sc.SecurityCams
	
	; ~ CoffinEffect = 0, not affected by SCP-895
	; ~ CoffinEffect = 1, constantly affected by SCP-895
	; ~ CoffinEffect = 2, SCP-079 can broadcast SCP-895 feed on this screen
	; ~ CoffinEffect = 3, SCP-079 broadcasting SCP-895 feed
	
	ParticleCam = Camera
	For sc.SecurityCams = Each SecurityCams
		Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
		
		If Close Lor sc = sc_I\CoffinCam
			If sc\FollowPlayer
				If sc\Pvt = 0
					sc\Pvt = CreatePivot(sc\BaseOBJ)
					EntityParent(sc\Pvt, 0) ; ~ Sets position and rotation of the pivot to the cam object
				EndIf
				If EntityVisible(sc\CameraOBJ, Camera)
					If sc <> sc_I\CoffinCam
						MTFCameraCheckDetected = (MTFCameraCheckTimer > 0.0)
					EndIf
					
					PointEntity(sc\Pvt, Camera)
					
					RotateEntity(sc\CameraOBJ, CurveAngle(EntityPitch(sc\Pvt), EntityPitch(sc\CameraOBJ), 75.0), CurveAngle(EntityYaw(sc\Pvt), EntityYaw(sc\CameraOBJ), 75.0), 0.0)
				EndIf
				PositionEntity(sc\CameraOBJ, EntityX(sc\BaseOBJ, True), EntityY(sc\BaseOBJ, True) - 0.083, EntityZ(sc\BaseOBJ, True))
			Else
				If sc\Turn > 0.0
					If (Not sc\Dir)
						sc\CurrAngle = sc\CurrAngle + (0.2 * fps\Factor[0])
						If sc\CurrAngle > sc\Turn * 1.3 Then sc\Dir = True
					Else
						sc\CurrAngle = sc\CurrAngle - (0.2 * fps\Factor[0])
						If sc\CurrAngle < (-sc\Turn) * 1.3 Then sc\Dir = False
					EndIf
				EndIf
				PositionEntity(sc\CameraOBJ, EntityX(sc\BaseOBJ, True), EntityY(sc\BaseOBJ, True) - 0.083, EntityZ(sc\BaseOBJ, True))
				RotateEntity(sc\CameraOBJ, EntityPitch(sc\CameraOBJ), sc\room\Angle + sc\Angle + Max(Min(sc\CurrAngle, sc\Turn), -sc\Turn), 0.0)
				
				If sc\Cam <> 0
					PositionEntity(sc\Cam, EntityX(sc\CameraOBJ, True), EntityY(sc\CameraOBJ, True), EntityZ(sc\CameraOBJ, True))
					RotateEntity(sc\Cam, EntityPitch(sc\CameraOBJ), EntityYaw(sc\CameraOBJ), 0.0)
					MoveEntity(sc\Cam, 0.0, 0.0, 0.1)
				EndIf
				
				If sc <> sc_I\CoffinCam
					If Abs(DeltaYaw(sc\CameraOBJ, Camera)) < 60.0 And EntityVisible(sc\CameraOBJ, Camera) Then MTFCameraCheckDetected = (MTFCameraCheckTimer > 0.0)
				EndIf
			EndIf
			If (MilliSec Mod 1350) < 800
				EntityTexture(sc\CameraOBJ, sc_I\CamTextureID[CAM_HEAD_DEFAULT_TEXTURE])
			Else
				EntityTexture(sc\CameraOBJ, sc_I\CamTextureID[CAM_HEAD_RED_LIGHT_TEXTURE])
			EndIf
		EndIf
		
		If Close
			If sc\Screen
				If me\Sanity < -800.0
					me\RestoreSanity = False
					me\Sanity = -1010.0
					msg\DeathMsg = GetLocalString("death", "895")
					If me\VomitTimer < -10.0 Then Kill()
				EndIf
				
				sc\InSight = False
				If EntityDistanceSquared(me\Collider, sc\ScrOBJ) < PowTwo(me\CameraFogDist * LightVolume * 1.2) And SecondaryLightOn > 0.3
					sc\InSight = (EntityInView(sc\MonitorOBJ, Camera) And EntityVisible(Camera, sc\ScrOBJ))
					
					If me\BlinkTimer > -6.0 And sc\InSight
						ParticleCam = sc\Cam
						
						Local Temp% = False
						Local RID% = sc\room\RoomTemplate\RoomID
						
						If RID = r_cont1_205 Lor RID = r_cont1_173_intro
							sc\CoffinEffect = 0
							Temp = True
						Else
							CameraFogColor(sc\Cam, CurrFogColorR, CurrFogColorG, CurrFogColorB)
							CameraClsColor(sc\Cam, CurrFogColorR, CurrFogColorG, CurrFogColorB)
						EndIf
						
						If sc\State < sc\RenderInterval
							sc\State = sc\State + fps\Factor[0]
						Else
							If sc_I\CoffinCam = Null Lor Rand(5) = 5 Lor sc\CoffinEffect <> 3
								UpdateLights(sc\Cam)
							Else
								UpdateLights(sc_I\CoffinCam\Cam)
							EndIf
							sc\State = 0.0
						EndIf
						
						If sc\CoffinEffect = 1 Lor sc\CoffinEffect = 3
							If I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4 And (Not chs\NoTarget)
								me\Sanity = me\Sanity - (fps\Factor[0] * (1.0 + (0.2 * SelectedDifficulty\OtherFactors)) / (1.0 + I_714\Using))
								me\RestoreSanity = False
								If SelectedDifficulty\SaveType = SAVE_ON_SCREENS Then CanSave = 0
								
								Local Pvt% = CreatePivot()
								
								PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
								PointEntity(Pvt, sc\ScrOBJ)
								
								RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
								
								TurnEntity(Pvt, 90.0, 0.0, 0.0)
								CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
								CameraPitch = CameraPitch - 90.0
								
								FreeEntity(Pvt) : Pvt = 0
								If me\Sanity < -800.0
									If Rand(3) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
									If Rand(6) < 5
										EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_5)])
										If sc\PlayerState = 1 Then PlaySound_Strict(snd_I\HorrorSFX[1])
										sc\PlayerState = 2
										If (Not ChannelPlaying(sc\SoundCHN)) Then sc\SoundCHN = PlaySound_Strict(snd_I\HorrorSFX[4])
										If sc\CoffinEffect = 3 And Rand(200) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf
									me\BlurTimer = 1000.0
									If me\VomitTimer = 0.0 Then me\VomitTimer = 1.0
								ElseIf me\Sanity < -500.0
									If Rand(7) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
									If Rand(50) = 1
										EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_895_OVERLAY_1, MONITOR_895_OVERLAY_5)])
										If sc\PlayerState = 0 Then PlaySound_Strict(snd_I\HorrorSFX[0])
										sc\PlayerState = Max(sc\PlayerState, 1)
										If sc\CoffinEffect = 3 And Rand(100) = 1 Then sc\CoffinEffect = 2 : sc\PlayerState = Rand(10000, 20000)
									EndIf
								Else
									EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
								EndIf
							Else
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
							EndIf
						ElseIf (Not Temp)
							If sc\PlayerState = 0 Then sc\PlayerState = Rand(60000, 65000) - (20000 * SelectedDifficulty\AggressiveNPCs)
							If Rand(500) = 1 Then EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_079_OVERLAY_2, MONITOR_079_OVERLAY_7)])
							If (MilliSec Mod sc\PlayerState) >= Rand(600)
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[MONITOR_DEFAULT_OVERLAY])
							Else
								If (Not ChannelPlaying(sc\SoundCHN))
									sc\SoundCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\079\Broadcast" + Rand(0, 2) + ".ogg"))
									If sc\CoffinEffect = 2 Then sc\CoffinEffect = 3 : sc\PlayerState = 0
								EndIf
								EntityTexture(sc\ScrOverlay, mon_I\MonitorOverlayID[Rand(MONITOR_079_OVERLAY_2, MONITOR_079_OVERLAY_7)])
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			If (Not sc\InSight) And (Not sc\ScriptedCamera) Then sc\SoundCHN = LoopSound2(snd_I\CameraSFX, sc\SoundCHN, Camera, sc\CameraOBJ, 4.0)
		EndIf
		
		If sc <> Null
			CatchErrors("Uncaught: UpdateSecurityCameras(Room ID: " + RID + ")")
		Else
			CatchErrors("Uncaught: UpdateSecurityCameras(Screen doesn't exist anymore!)")
		EndIf
	Next
End Function

Function RenderSecurityCams%()
	CatchErrors("RenderSecurityCams()")
	
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
		
		If Close
			If sc\Screen
				If me\BlinkTimer > -6.0 And EntityDistanceSquared(me\Collider, sc\ScrOBJ) < PowTwo(me\CameraFogDist * LightVolume * 1.2) And sc\InSight And SecondaryLightOn > 0.3
					If sc\room\RoomTemplate\RoomID <> r_cont1_205
						If EntityHidden(sc\ScrOBJ) Then ShowEntity(sc\ScrOBJ)
						If EntityHidden(sc\ScrOverlay) Then ShowEntity(sc\ScrOverlay)
					EndIf
					
					If sc\State >= sc\RenderInterval
						If sc_I\CoffinCam = Null Lor Rand(5) = 5 Lor sc\CoffinEffect <> 3
							If (Not EntityHidden(Camera))
								ShowEntity(sc\Cam)
								HideEntity(Camera)
							EndIf
							Cls()
							
							SetBuffer(BackBuffer())
							RenderWorld(RenderTween)
							CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(sc_I\ScreenTex))
							
							If (Not EntityHidden(sc\Cam))
								ShowEntity(Camera)
								HideEntity(sc\Cam)
							EndIf
						Else
							If (Not EntityHidden(Camera))
								HideEntity(Camera)
								ShowEntity(sc_I\CoffinCam\room\OBJ)
								EntityAlpha(GetChild(sc_I\CoffinCam\room\OBJ, 2), 1.0)
								ShowEntity(sc_I\CoffinCam\Cam)
							EndIf
							Cls()
							
							SetBuffer(BackBuffer())
							RenderWorld(RenderTween)
							CopyRect(0, 0, 512, 512, 0, 0, BackBuffer(), TextureBuffer(sc_I\ScreenTex))
							
							If (Not EntityHidden(sc_I\CoffinCam\room\OBJ))
								HideEntity(sc_I\CoffinCam\Cam)
								ShowEntity(Camera)
								HideEntity(sc_I\CoffinCam\room\OBJ)
							EndIf
						EndIf
					EndIf
				Else
					If (Not EntityHidden(sc\ScrOBJ)) Then HideEntity(sc\ScrOBJ)
					If (Not EntityHidden(sc\ScrOverlay)) Then HideEntity(sc\ScrOverlay)
				EndIf
			EndIf
		EndIf
		
		If sc <> Null
			CatchErrors("Uncaught: RenderSecurityCameras(Room ID: " + sc\room\RoomTemplate\RoomID + ")")
		Else
			CatchErrors("Uncaught: RenderSecurityCameras(Screen doesn't exist anymore!)")
		EndIf
	Next
	Cls()
End Function

Function RemoveSecurityCam%(sc.SecurityCams)
	If sc\Pvt <> 0 Then FreeEntity(sc\Pvt) : sc\Pvt = 0
	FreeEntity(sc\CameraOBJ) : sc\CameraOBJ = 0
	FreeEntity(sc\BaseOBJ) : sc\BaseOBJ = 0
	If sc\Screen
		FreeEntity(sc\MonitorOBJ) : sc\MonitorOBJ = 0
		FreeEntity(sc\ScrOverlay) : sc\ScrOverlay = 0
		FreeEntity(sc\ScrOBJ) : sc\ScrOBJ = 0
		FreeEntity(sc\Cam) : sc\Cam = 0
	EndIf
	Delete(sc)
End Function

Function UpdateMonitorSaving%()
	If SelectedDifficulty\SaveType <> SAVE_ON_SCREENS Lor InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated Lor SecondaryLightOn =< 0.3 Then Return
	
	Local sc.SecurityCams
	
	For sc.SecurityCams = Each SecurityCams
		If sc\AllowSaving And sc\Screen
			Local Close% = (sc\room\Dist < 6.0 Lor PlayerRoom = sc\room)
			
			If Close
				If sc\InSight And EntityDistanceSquared(sc\ScrOBJ, Camera) < 1.0 And GrabbedEntity = 0 And d_I\ClosestButton = 0
					HandEntity = sc\MonitorOBJ
					If mo\MouseHit1 Then sc_I\SelectedMonitor = sc
					
					If sc_I\SelectedMonitor = sc
						Local Pvt% = CreatePivot()
						
						PositionEntity(Pvt, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
						PointEntity(Pvt, sc\MonitorOBJ)
						RotateEntity(me\Collider, EntityPitch(me\Collider), CurveAngle(EntityYaw(Pvt), EntityYaw(me\Collider), Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0)), 0.0)
						TurnEntity(Pvt, 90.0, 0.0, 0.0)
						CameraPitch = CurveAngle(EntityPitch(Pvt), CameraPitch + 90.0, Min(Max(15000.0 / (-me\Sanity), 20.0), 200.0))
						CameraPitch = CameraPitch - 90.0
						FreeEntity(Pvt) : Pvt = 0
					EndIf
				Else
					If sc_I\SelectedMonitor = sc Then sc_I\SelectedMonitor = Null
				EndIf
			Else
				If sc_I\SelectedMonitor = sc Then sc_I\SelectedMonitor = Null
			EndIf
		EndIf
	Next
End Function

Function UpdateCheckpointMonitors%(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Name$
	Local Entity% = mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL]
	Local SurfCount% = CountSurfaces(Entity)
	
	For i = 2 To SurfCount
		SF = GetSurface(Entity, i)
		b = GetSurfaceBrush(SF)
		If b <> 0
			t1 = GetBrushTexture(b, 0)
			If t1 <> 0
				Name = StripPath(TextureName(t1))
				If Lower(Name) <> "monitortexture.jpg"
					If LCZ
						If mon_I\MonitorTimer[0] < 50.0
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_2_OVERLAY], 0, 0)
						Else
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_3_OVERLAY], 0, 0)
						EndIf
					Else
						If mon_I\MonitorTimer[1] < 50.0
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_2_OVERLAY], 0, 0)
						Else
							BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_1_OVERLAY], 0, 0)
						EndIf
					EndIf
					PaintSurface(SF, b)
				EndIf
				If Name <> "" Then FreeTexture(t1) : t1 = 0
			EndIf
			FreeBrush(b) : b = 0
		EndIf
	Next
	SF = 0
	Entity = 0
	mon_I\UpdateCheckpoint[(1 - LCZ)] = True
End Function

Function TurnCheckpointMonitorsOff%(LCZ% = True)
	Local i%, SF%, b%, t1%
	Local Name$
	
	If mon_I\UpdateCheckpoint[(1 - LCZ)]
		Local Entity% = mon_I\MonitorModelID[MONITOR_CHECKPOINT_MODEL]
		Local SurfCount% = CountSurfaces(Entity)
		
		For i = 2 To SurfCount
			SF = GetSurface(Entity, i)
			b = GetSurfaceBrush(SF)
			If b <> 0
				t1 = GetBrushTexture(b, 0)
				If t1 <> 0
					Name = StripPath(TextureName(t1))
					If Lower(Name) <> "monitortexture.jpg"
						BrushTexture(b, mon_I\MonitorOverlayID[MONITOR_LOCKDOWN_4_OVERLAY], 0, 0)
						PaintSurface(SF, b)
					EndIf
					If Name <> "" Then FreeTexture(t1) : t1 = 0
				EndIf
				FreeBrush(b) : b = 0
			EndIf
		Next
		SF = 0
		Entity = 0
		mon_I\UpdateCheckpoint[(1 - LCZ)] = False
		mon_I\MonitorTimer[(1 - LCZ)] = 0.0
	EndIf
End Function

Function TimeCheckpointMonitors%()
	If mon_I\UpdateCheckpoint[0]
		If mon_I\MonitorTimer[0] < 100.0
			mon_I\MonitorTimer[0] = mon_I\MonitorTimer[0] + fps\Factor[0]
		Else
			mon_I\MonitorTimer[0] = 0.0
		EndIf
	EndIf
	If mon_I\UpdateCheckpoint[1]
		If mon_I\MonitorTimer[1] < 100.0
			mon_I\MonitorTimer[1] = mon_I\MonitorTimer[1] + fps\Factor[0]
		Else
			mon_I\MonitorTimer[1] = 0.0
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

Function CreateScreen.Screens(room.Rooms, x#, y#, z#, ImgPath$)
	Local s.Screens
	
	s.Screens = New Screens
	s\OBJ = CreatePivot()
	EntityRadius(s\OBJ, 0.1)
	EntityPickMode(s\OBJ, 1)
	PositionEntity(s\OBJ, x, y, z)
	If room <> Null Then EntityParent(s\OBJ, room\OBJ)
	
	s\ImgPath = ImgPath
	s\room = room
	
	Return(s)
End Function

Function UpdateScreens%()
	If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor SecondaryLightOn =< 0.3 Then Return
	
	Local s.Screens
	
	For s.Screens = Each Screens
		If s\room = PlayerRoom
			If InteractObject(s\OBJ, 1.0, 2)
				SelectedScreen = s
				s\Img = LoadImage_Strict("GFX\Map\Screens\" + s\ImgPath)
				s\Img = ScaleImage2(s\Img, MenuScale, MenuScale)
				PlaySound_Strict(ButtonSFX[0])
				mo\MouseUp1 = False
				Exit
			EndIf
		EndIf
	Next
End Function

Function RemoveScreen%(s.Screens)
	FreeEntity(s\OBJ) : s\OBJ = 0
	If s\Img <> 0 Then FreeImage(s\Img) : s\Img = 0
	Delete(s)
End Function

Type Levers
	Field OBJ%, BaseOBJ%
	Field room.Rooms
End Type

Function CreateLever.Levers(room.Rooms, x#, y#, z#, Rotation# = 0.0, TurnedOn% = False)
	Local lvr.Levers
	
	lvr.Levers = New Levers
	
	lvr\room = room
	
	lvr\BaseOBJ = CopyEntity(lvr_I\LeverModelID[LEVER_BASE_MODEL])
	ScaleEntity(lvr\BaseOBJ, 0.036, 0.036, 0.036)
	PositionEntity(lvr\BaseOBJ, x, y, z, True)
	EntityParent(lvr\BaseOBJ, room\OBJ)
	RotateEntity(lvr\BaseOBJ, 0.0, Rotation, 0.0)
	
	lvr\OBJ = CopyEntity(lvr_I\LeverModelID[LEVER_HANDLE_MODEL])
	ScaleEntity(lvr\OBJ, 0.036, 0.036, 0.036)
	PositionEntity(lvr\OBJ, x, y, z, True)
	EntityParent(lvr\OBJ, room\OBJ)
	RotateEntity(lvr\OBJ, 80.0 + (-160.0 * TurnedOn), Rotation - 180.0, 0.0)
	EntityRadius(lvr\OBJ, 0.1)
	EntityPickMode(lvr\OBJ, 1, False)
	
	Return(lvr)
End Function

Function UpdateLever%(OBJ%, Locked% = False, MaxValue = 80.0, MinValue# = -80.0)
	Local PrevValue#, RefValue#
	Local Dist# = EntityDistanceSquared(Camera, OBJ)
	
	If Dist < 4.0 
		If Dist <= 0.64 And (Not Locked)
			If EntityPick(Camera, 0.8) = OBJ
				HandEntity = OBJ
				If mo\MouseHit1 Lor mo\MouseDown1 Then GrabbedEntity = OBJ
			EndIf
			
			PrevValue = EntityPitch(OBJ)
			
			If GrabbedEntity = OBJ
				HandEntity = OBJ
				RotateEntity(GrabbedEntity, Max(Min(EntityPitch(OBJ) + Max(Min(mo\Mouse_Y_Speed_1 * 8.0, 30.0), -30.0), MaxValue), MinValue), EntityYaw(OBJ), 0.0)
				DrawArrowIcon[0] = True
				DrawArrowIcon[2] = True
			EndIf
			
			RefValue = EntityPitch(OBJ, True)
			If RefValue > (MaxValue - 5.0)
				If PrevValue =< (MaxValue - 5.0) Then PlaySound2(snd_I\LeverSFX, Camera, OBJ, 1.0)
			ElseIf RefValue < (MinValue + 5.0)
				If PrevValue => (MinValue + 5.0) Then PlaySound2(snd_I\LeverSFX, Camera, OBJ, 1.0)	
			EndIf
		Else
			GrabbedEntity = 0
		EndIf
		If GrabbedEntity = 0 Lor Dist > 0.64
			If EntityPitch(OBJ, True) > ((MaxValue + MinValue) / 2.0)
				RotateEntity(OBJ, CurveValue(MaxValue, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			Else
				RotateEntity(OBJ, CurveValue(MinValue, EntityPitch(OBJ), 10.0), EntityYaw(OBJ), 0.0)
			EndIf
		EndIf
	EndIf
	
	RefValue = EntityPitch(OBJ, True)
	Return(Not (RefValue > ((MaxValue + MinValue) / 2.0)))
End Function

Function RemoveLever(lvr.Levers)
	FreeEntity(lvr\OBJ) : lvr\OBJ = 0
	FreeEntity(lvr\BaseOBJ) : lvr\BaseOBJ = 0
	Delete(lvr)
End Function

Function CreateRedLight%(x#, y#, z#)
	Local Sprite%
	
	Sprite = CreateSprite()
	PositionEntity(Sprite, x, y, z)
	ScaleSprite(Sprite, 0.015, 0.015)
	EntityTexture(Sprite, misc_I\LightSpriteID[LIGHT_SPRITE_RED])
	EntityBlend(Sprite, 3)
	
	Return(Sprite)
End Function

Function UpdateRedLight%(Light%, Value1#, Value2#)
	If (MilliSec Mod Value1) < Value2
		If EntityHidden(Light) Then ShowEntity(Light)
	Else
		If (Not EntityHidden(Light)) Then HideEntity(Light)
	EndIf
End Function

Function CreateCustomCenter%(room.Rooms, x#, z#)
	room\RoomCenter = CreatePivot()
	PositionEntity(room\RoomCenter, x, 0.0, z)
	EntityParent(room\RoomCenter, room\OBJ)
End Function

Include "Source Code\Rooms_Core.bb"

Function ResetRender%()
	Local it.Items, n.NPCs
	
	UpdateLightsTimer = 0.0
	UpdateLightVolume()
	UpdateLights(Camera)
	UpdateDoors()
	UpdateDecals()
	UpdateRooms()
	For it.Items = Each Items
		it\DistTimer = 0.0
		it\DropSpeed = 0.0
	Next
	For n.NPCs = Each NPCs
		n\AnimTimer = 0.0
		n\DropSpeed = 0.0
	Next
	me\DropSpeed = 0.0
	ShouldEntitiesFall = False
End Function

Function TeleportToRoom%(r.Rooms)
	Local it.Items
	
	PlayerRoom = r
	ResetRender()
	me\InsideElevator = False
End Function

Function HideRoomsNoColl%(room.Rooms)
	Local i%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If (Not EntityHidden(room\OBJ))
		For p.Props = Each Props
			If p\room = room Then HideEntity(p\OBJ)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				HideEntity(d\OBJ)
				If d\OBJ2 <> 0 Then HideEntity(d\OBJ2)
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then HideEntity(d\Buttons[i])
					If d\ElevatorPanel[i] <> 0 Then HideEntity(d\ElevatorPanel[i])
				Next
				HideEntity(d\FrameOBJ)
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then HideEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					HideEntity(sc\CameraOBJ)
					HideEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				HideEntity(lvr\OBJ)
				HideEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then HideEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		HideEntity(room\OBJ)
	EndIf
End Function

Function ShowRoomsNoColl%(room.Rooms)
	Local i%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If EntityHidden(room\OBJ)
		For p.Props = Each Props
			If p\room = room Then ShowEntity(p\OBJ)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				ShowEntity(d\OBJ)
				If d\OBJ2 <> 0 Then ShowEntity(d\OBJ2)
				For i = 0 To 1
					If d\Buttons[i] <> 0 Then ShowEntity(d\Buttons[i])
					If d\ElevatorPanel[i] <> 0 Then ShowEntity(d\ElevatorPanel[i])
				Next
				ShowEntity(d\FrameOBJ)
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then ShowEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					ShowEntity(sc\CameraOBJ)
					ShowEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				ShowEntity(lvr\OBJ)
				ShowEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then ShowEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		;If room\TriggerBoxAmount > 0
		;	For i = 0 To room\TriggerBoxAmount - 1
		;		If chs\DebugHUD <> 0
		;			EntityColor(room\TriggerBoxes[i]\OBJ, 255, 255, 0)
		;			EntityAlpha(room\TriggerBoxes[i]\OBJ, 0.2)
		;		Else
		;			EntityColor(room\TriggerBoxes[i]\OBJ, 255, 255, 255)
		;			EntityAlpha(room\TriggerBoxes[i]\OBJ, 0.0)
		;		EndIf
		;	Next
		;EndIf
		
		ShowEntity(room\OBJ)
	EndIf
End Function

Function HideRoomsColl%(room.Rooms)
	Local i%, j%, k%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If (Not room\HiddenAlpha)
		For p.Props = Each Props
			If p\room = room Then EntityAlpha(p\OBJ, 0.0)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				Local Hide% = True
				
				For i = 0 To MaxRoomAdjacents - 1
					If room\AdjDoor[i] <> Null
						If room\AdjDoor[i] = d
							Hide = False
							Exit
						EndIf
					EndIf
				Next
				
				If Hide
					EntityAlpha(d\OBJ, 0.0)
					If d\OBJ2 <> 0 Then EntityAlpha(d\OBJ2, 0.0)
					For i = 0 To 1
						If d\Buttons[i] <> 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR Then EntityAlpha(d\Buttons[i], 0.0)
						; ~ Hide it anyway because player's collider cannot interact with it
						If d\ElevatorPanel[i] <> 0 Then HideEntity(d\ElevatorPanel[i])
					Next
					EntityAlpha(d\FrameOBJ, 0.0)
				EndIf
			EndIf
		Next
		
		; ~ Hide it anyway because the player/NPC cannot interact with it
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then HideEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					HideEntity(sc\CameraOBJ)
					HideEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		; ~ Hide it anyway because the player/NPC cannot interact with it
		For lvr.Levers = Each Levers
			If lvr\room = room
				HideEntity(lvr\OBJ)
				HideEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		; ~ Hide it anyway because the player/NPC cannot interact with it
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then HideEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		EntityAlpha(GetChild(room\OBJ, 2), 0.0)
		room\HiddenAlpha = True
	EndIf
End Function

Function ShowRoomsColl%(room.Rooms)
	Local i%, j%, k%
	Local p.Props, d.Doors, sc.SecurityCams, lvr.Levers
	
	If room\HiddenAlpha
		For p.Props = Each Props
			If p\room = room Then EntityAlpha(p\OBJ, 1.0)
		Next
		
		For d.Doors = Each Doors
			If d\room = room
				EntityAlpha(d\OBJ, 1.0)
				If d\OBJ2 <> 0 Then EntityAlpha(d\OBJ2, 1.0)
				For i = 0 To 1
					If d\Buttons[i] <> 0 And d\DoorType <> WOODEN_DOOR And d\DoorType <> OFFICE_DOOR Then EntityAlpha(d\Buttons[i], 1.0)
					If d\ElevatorPanel[i] <> 0 Then ShowEntity(d\ElevatorPanel[i])
				Next
				EntityAlpha(d\FrameOBJ, 1.0)
			EndIf
		Next
		
		For sc.SecurityCams = Each SecurityCams
			If sc\room = room
				If sc\MonitorOBJ <> 0
					If (Not sc\ScriptedMonitor) Then ShowEntity(sc\MonitorOBJ)
				EndIf
				If (Not sc\ScriptedCamera)
					ShowEntity(sc\CameraOBJ)
					ShowEntity(sc\BaseOBJ)
				EndIf
			EndIf
		Next
		
		For lvr.Levers = Each Levers
			If lvr\room = room
				ShowEntity(lvr\OBJ)
				ShowEntity(lvr\BaseOBJ)
			EndIf
		Next
		
		For i = 0 To MaxRoomObjects - 1
			If room\Objects[i] <> 0
				If (Not room\ScriptedObject[i]) Then ShowEntity(room\Objects[i])
			Else
				Exit
			EndIf
		Next
		
		EntityAlpha(GetChild(room\OBJ, 2), 1.0)
		room\HiddenAlpha = False
	EndIf
End Function

Function UpdateRooms%()
	CatchErrors("UpdateRooms()")
	
	Local Dist#, i%, j%, r.Rooms
	Local x#, y#, z#, Hide%
	Local PlayerX# = EntityX(me\Collider, True)
	Local PlayerY# = EntityY(me\Collider, True)
	Local PlayerZ# = EntityZ(me\Collider, True)
	
	; ~ The reason why it is like this:
	; ~ When the map gets spawned by a seed, it starts from LCZ to HCZ to EZ (bottom to top)
	; ~ A map loaded by the map creator starts from EZ to HCZ to LCZ (top to bottom) and that's why this little code thing with the (SelectedCustomMap = Null) needs to be there - ENDSHN
	If (PlayerZ / RoomSpacing) < I_Zone\Transition[1] - (SelectedCustomMap = Null)
		me\Zone = 2
	ElseIf (PlayerZ / RoomSpacing) >= I_Zone\Transition[1] - (SelectedCustomMap = Null) And (PlayerZ / RoomSpacing) < I_Zone\Transition[0] - (SelectedCustomMap = Null)
		me\Zone = 1
	Else
		me\Zone = 0
	EndIf
	
	Local FoundNewPlayerRoom% = False
	
	If Abs(PlayerY - EntityY(PlayerRoom\OBJ)) < 1.5
		x = Abs(PlayerRoom\x - PlayerX)
		If x < 4.0
			z = Abs(PlayerRoom\z - PlayerZ)
			If z < 4.0 Then FoundNewPlayerRoom = True
		EndIf
		
		If (Not FoundNewPlayerRoom) ; ~ It's likely that an adjacent room is the new player room, check for that
			For i = 0 To MaxRoomAdjacents - 1
				If PlayerRoom\Adjacent[i] <> Null
					x = Abs(PlayerRoom\Adjacent[i]\x - PlayerX)
					If x < 4.0
						z = Abs(PlayerRoom\Adjacent[i]\z - PlayerZ)
						If z < 4.0
							y = Abs(PlayerRoom\Adjacent[i]\y - PlayerY)
							If y < 4.0
								FoundNewPlayerRoom = True
								PlayerRoom = PlayerRoom\Adjacent[i]
								Exit
							EndIf
						EndIf
					EndIf
				EndIf
			Next
		EndIf
	EndIf
	
	For r.Rooms = Each Rooms
		x = Abs(r\x - PlayerX)
		z = Abs(r\z - PlayerZ)
		r\Dist = Max(x, z)
		
		If x < 4.0 And z < 4.0
			If (Not FoundNewPlayerRoom) And PlayerRoom <> r
				If Abs(PlayerY - EntityY(r\OBJ)) < 1.5 Then PlayerRoom = r
				FoundNewPlayerRoom = True
			EndIf
		EndIf
		
		Hide = True
		If r = PlayerRoom Then Hide = False
		If IsRoomAdjacent(PlayerRoom, r) Then Hide = False
		For i = 0 To MaxRoomAdjacents - 1
			If IsRoomAdjacent(PlayerRoom\Adjacent[i], r)
				Hide = False
				Exit
			EndIf
		Next
		
		If Hide
			HideRoomsNoColl(r)
		Else
			ShowRoomsNoColl(r)
		EndIf
	Next
	
	CurrMapGrid\Found[Floor(EntityX(PlayerRoom\OBJ) / RoomSpacing) + (Floor(EntityZ(PlayerRoom\OBJ) / RoomSpacing) * MapGridSize)] = MapGrid_Tile
	PlayerRoom\Found = True
	
	ShowRoomsColl(PlayerRoom)
	For i = 0 To MaxRoomAdjacents - 1
		If PlayerRoom\Adjacent[i] <> Null
			If PlayerRoom\AdjDoor[i] <> Null
				If PlayerRoom\Adjacent[i] <> PlayerRoom
					If PlayerRoom\AdjDoor[i]\OpenState = 0.0 Lor (Not EntityInView(PlayerRoom\AdjDoor[i]\FrameOBJ, Camera)) Lor PlayerY > 8.0 Lor PlayerY < -8.0
						HideRoomsColl(PlayerRoom\Adjacent[i])
					Else
						ShowRoomsColl(PlayerRoom\Adjacent[i])
					EndIf
				EndIf
			EndIf
			
			For j = 0 To MaxRoomAdjacents - 1
				If PlayerRoom\Adjacent[i]\Adjacent[j] <> Null
					If PlayerRoom\Adjacent[i]\Adjacent[j] <> PlayerRoom Then HideRoomsColl(PlayerRoom\Adjacent[i]\Adjacent[j])
				EndIf
			Next
		EndIf
	Next
	
	CatchErrors("Uncaught: UpdateRooms()")
End Function

Function IsRoomAdjacent%(this.Rooms, that.Rooms)
	If this = Null Lor that = Null Then Return(False)
	If this = that Then Return(True)
	
	Local i%
	
	For i = 0 To MaxRoomAdjacents - 1
		If that = this\Adjacent[i] Then Return(True)
	Next
	Return(False)
End Function

Dim MapRoom$(0, 0)
Dim RoomAmount%(0, 0)

Function SetRoom%(RoomZone%, RoomType%, RoomName$, RoomPosWeight# = 0.0) ; ~ Place a room without overwriting others
	Local Zone%, Offset%
	Local MinPos% = 0
	
	For Zone% = 0 To RoomZone - 1
		MinPos = MinPos + RoomAmount(RoomType, Zone)
	Next
	
	Local MaxPos% = MinPos + RoomAmount(RoomType, RoomZone) - 1
	
	If MaxPos < MinPos Then Return(False)
	
	RoomPosWeight = Clamp(RoomPosWeight, 0.0, 1.0)
	
	Local RoomPos% = MinPos + Floor(RoomPosWeight * (MaxPos - MinPos))
	
	If MapRoom(RoomType, RoomPos) = "" Then
		MapRoom(RoomType, RoomPos) = RoomName
		Return(True)
	EndIf
	
	For Offset = 1 To Max(MaxPos - RoomPos, RoomPos - MinPos)	
		If RoomPos + Offset <= MaxPos And MapRoom(RoomType, RoomPos + Offset) = ""
			MapRoom(RoomType, RoomPos + Offset) = RoomName
			Return(True)
		EndIf
		If RoomPos - Offset >= MinPos And MapRoom(RoomType, RoomPos - Offset) = ""
			MapRoom(RoomType, RoomPos - Offset) = RoomName
			Return(True)
		EndIf
	Next
	
	Return(False)
End Function

Function PreventRoomOverlap%(r.Rooms)
	If r\RoomTemplate\DisableOverlapCheck Then Return
	
	Local r2.Rooms, r3.Rooms
	Local IsIntersecting% = False
	Local RID% = r\RoomTemplate\RoomID
	
	; ~ Just skip it when it would try to check for the checkpoints
	If RID = r_room2_checkpoint_lcz_hcz Lor RID = r_room2_checkpoint_hcz_ez Then Return(True)
	
	; ~ First, check if the room is actually intersecting at all
	For r2.Rooms = Each Rooms
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
			If CheckRoomOverlap(r, r2)
				IsIntersecting = True
				Exit
			EndIf
		EndIf
	Next
	
	; ~ If not, then simply return it as True
	If (Not IsIntersecting) Then Return(True)
	
	; ~ Room is interseting: First, check if the given room is a ROOM2, so we could potentially just turn it by 180.0 degrees
	IsIntersecting = False
	
	Local x% = r\x / RoomSpacing
	Local y% = r\z / RoomSpacing
	
	If r\RoomTemplate\Shape = ROOM2
		; ~ Room is a ROOM2, let's check if turning it 180.0 degrees fixes the overlapping issue
		r\Angle = r\Angle + 180.0
		RotateEntity(r\OBJ, 0.0, r\Angle, 0.0)
		CalculateRoomExtents(r)
		
		For r2.Rooms = Each Rooms
			If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
				If CheckRoomOverlap(r, r2)
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
		If r2 <> r And (Not r2\RoomTemplate\DisableOverlapCheck)
			RID = r2\RoomTemplate\RoomID
			
			If r\RoomTemplate\Shape = r2\RoomTemplate\Shape And r\Zone = r2\Zone And (RID <> r_room2_checkpoint_lcz_hcz And RID <> r_room2_checkpoint_hcz_ez And RID <> r_cont1_173)
				x = r\x / RoomSpacing
				y = r\z / RoomSpacing
				Rot = r\Angle
				
				x2 = r2\x / RoomSpacing
				y2 = r2\z / RoomSpacing
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
					If (Not r3\RoomTemplate\DisableOverlapCheck)
						If r3 <> r
							If CheckRoomOverlap(r, r3)
								IsIntersecting = True
								Exit
							EndIf
						EndIf
						If r3 <> r2
							If CheckRoomOverlap(r2, r3)
								IsIntersecting = True
								Exit
							EndIf
						EndIf
					EndIf
				Next
				
				; ~ Either the original room or the "reposition" room is intersecting, reset the position of each room to their original one
				If IsIntersecting
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
	Field Grid%[PowTwo(MapGridSize + 1)]
	Field Angle%[PowTwo(MapGridSize + 1)]
	Field Found%[PowTwo(MapGridSize + 1)]
	Field RoomName$[PowTwo(MapGridSize)]
	Field RoomID%[ROOM4 + 1]
End Type

Global CurrMapGrid.MapGrid

; ~ Map Grid Tile ID Constants
;[Block]
Const MapGrid_NoTile% = 0
Const MapGrid_Tile% = 1
Const MapGrid_CheckpointTile% = 255
;[End Block]

Type MapZones
	Field Transition%[2]
	Field HasCustomForest%
	Field HasCustomMT%
End Type

Global I_Zone.MapZones

Function CreateMap%()
	Local r.Rooms, r2.Rooms, d.Doors
	Local x%, y%, Temp%, Temp2%
	Local i%, x2%, y2%
	Local Width%, Height%, TempHeight%, yHallways%
	Local ShouldSpawnDoor%, Zone%
	Local RoomID%
	
	I_Zone\Transition[0] = Floor(MapGridSize * (2.0 / 3.0)) + 1
	I_Zone\Transition[1] = Floor(MapGridSize * (1.0 / 3.0)) + 1
	I_Zone\HasCustomForest = False
	I_Zone\HasCustomMT = False
	
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	Delete(CurrMapGrid)
	CurrMapGrid = New MapGrid
	
	x = MapGridSize / 2
	y = MapGridSize - 2
	
	For i = y To MapGridSize - 1
		CurrMapGrid\Grid[x + (i * MapGridSize)] = MapGrid_Tile
	Next
	
	Repeat
		Width = Rand(Floor(MapGridSize * 0.6), Floor(MapGridSize * 0.85))
		
		If x > Floor(MapGridSize * 0.6)
			Width = -Width
		ElseIf x > Floor(MapGridSize * 0.4)
			x = x - (Width / 2)
		EndIf
		
		; ~ Make sure the hallway doesn't go outside the array
		If x + Width > MapGridSize - 3
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
			
			If x2 < x + Width
				If i = 1
					TempHeight = Height
					If Rand(2) = 1
						x2 = x
					Else
						x2 = x + Width
					EndIf
				Else
					TempHeight = Rand(Height)
				EndIf
				
				For y2 = y - TempHeight To y
					If GetZone(y2) <> GetZone(y2 + 1) ; ~ A room leading from zone to another
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
	
	Dim RoomAmount%(ROOM4 + 1, ZONEAMOUNT)
	
	; ~ Count the amount of rooms
	For y = 1 To MapGridSize - 1
		Zone = GetZone(y)
		For x = 1 To MapGridSize - 1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				Temp = 0
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1)
				If CurrMapGrid\Grid[x + (y * MapGridSize)] <> MapGrid_CheckpointTile Then CurrMapGrid\Grid[x + (y * MapGridSize)] = Temp
				Select CurrMapGrid\Grid[x + (y * MapGridSize)]
					Case 1
						;[Block]
						RoomAmount(ROOM1, Zone) = RoomAmount(ROOM1, Zone) + 1
						;[End Block]
					Case 2
						;[Block]
						If Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1) = 2
							RoomAmount(ROOM2, Zone) = RoomAmount(ROOM2, Zone) + 1
						ElseIf Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1) = 2
							RoomAmount(ROOM2, Zone) = RoomAmount(ROOM2, Zone) + 1
						Else
							RoomAmount(ROOM2C, Zone) = RoomAmount(ROOM2C, Zone) + 1
						EndIf
						;[End Block]
					Case 3
						;[Block]
						RoomAmount(ROOM3, Zone) = RoomAmount(ROOM3, Zone) + 1
						;[End Block]
					Case 4
						;[Block]
						RoomAmount(ROOM4, Zone) = RoomAmount(ROOM4, Zone) + 1
						;[End Block]
				End Select
			EndIf
		Next
	Next
	
	Local Placed%
	Local y_min%, y_max%, x_min%, x_max%
	
	; ~ Force more ROOM1 (if needed)
	For i = 0 To 2
		; ~ Need more rooms if there are less than 5 of them
		Temp = (-RoomAmount(ROOM1, i)) + 5
		If Temp > 0
			If i = 2
				y_min = 1
			Else
				y_min = I_Zone\Transition[i]
			EndIf
			If i = 0
				y_max = MapGridSize - 2
			Else
				y_max = I_Zone\Transition[i - 1] - 1
			EndIf
			x_min = 1
			x_max = MapGridSize - 2
			
			For y = y_min To y_max
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile
						If (Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1)) = 1
							If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)]
								x2 = x + 1 : y2 = y
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
								x2 = x - 1 : y2 = y
							ElseIf CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)]
								x2 = x : y2 = y + 1
							ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)]
								x2 = x : y2 = y - 1
							EndIf
							
							Placed = False
							If CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] > 1 And CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] < 4 And (y < y_max Lor y2 < y Lor i = 0)
								Select CurrMapGrid\Grid[x2 + (y2 * MapGridSize)]
									Case 2
										;[Block]
										If Min(CurrMapGrid\Grid[(x2 + 1) + (y2 * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x2 - 1) + (y2 * MapGridSize)], 1) = 2
											RoomAmount(ROOM2, i) = RoomAmount(ROOM2, i) - 1
											RoomAmount(ROOM3, i) = RoomAmount(ROOM3, i) + 1
											Placed = True
										ElseIf Min(CurrMapGrid\Grid[x2 + ((y2 + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x2 + ((y2 - 1) * MapGridSize)], 1) = 2
											RoomAmount(ROOM2, i) = RoomAmount(ROOM2, i) - 1
											RoomAmount(ROOM3, i) = RoomAmount(ROOM3, i) + 1
											Placed = True
										EndIf
										;[End Block]
									Case 3
										;[Block]
										RoomAmount(ROOM3, i) = RoomAmount(ROOM3, i) - 1
										RoomAmount(ROOM4, i) = RoomAmount(ROOM4, i) + 1
										Placed = True
										;[End Block]
								End Select
								
								If Placed
									CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] = CurrMapGrid\Grid[x2 + (y2 * MapGridSize)] + 1
									
									CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile
									RoomAmount(ROOM1, i) = RoomAmount(ROOM1, i) + 1
									
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
		If i = 2
			y_min = 2
		Else
			y_min = I_Zone\Transition[i]
		EndIf
		If i = 0
			y_max = MapGridSize - 2
		Else
			y_max = I_Zone\Transition[i - 1] - 2
		EndIf
		x_min = 1
		x_max = MapGridSize - 2
		
		If RoomAmount(ROOM4, i) < 1 ; ~ We want at least one ROOM4
			Temp = 0
			For y = y_min To y_max
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = 3
						Select False ; ~ See if adding a ROOM1 is possible
							Case (CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)] Lor x = x_max)
								;[Block]
								CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)] Lor x = x_min)
								;[Block]
								CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)] Lor (i = 0 And y = y_max))
								;[Block]
								CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
							Case (CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] Lor CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)] Lor (i < 2 And y = y_min))
								;[Block]
								CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 1
								Temp = 1
								;[End Block]
						End Select
						If Temp = 1
							CurrMapGrid\Grid[x + (y * MapGridSize)] = 4 ; ~ Turn this room into a ROOM4
							RoomAmount(ROOM4, i) = RoomAmount(ROOM4, i) + 1
							RoomAmount(ROOM3, i) = RoomAmount(ROOM3, i) - 1
							RoomAmount(ROOM1, i) = RoomAmount(ROOM1, i) + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
		
		If RoomAmount(ROOM2C, i) < 2 ; ~ We want at least two ROOM2C
			Temp = 0
			For y = y_max To y_min Step -1
				For x = x_min To x_max
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_Tile
						Select True ; ~ See if adding some rooms is possible
							Case CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + (y * MapGridSize)]) = 0 And x < x_max
									If (CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + (y * MapGridSize)]) = 0 And x > x_min
									If (CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)] + CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y + 2) * MapGridSize)]) = 0 And (y < y_max Lor i > 0)
									If (CurrMapGrid\Grid[(x - 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y + 2) * MapGridSize)]) = 0 And x > x_min
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y + 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y + 2) * MapGridSize)]) = 0 And x < x_max
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y + 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
							Case CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								;[Block]
								If (CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[x + ((y - 2) * MapGridSize)]) = 0 And (y > y_min Lor i = 2)
									If (CurrMapGrid\Grid[(x - 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x - 1) + ((y - 2) * MapGridSize)]) = 0 And x > x_min
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x - 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									ElseIf (CurrMapGrid\Grid[(x + 2) + ((y - 1) * MapGridSize)] + CurrMapGrid\Grid[(x + 1) + ((y - 2) * MapGridSize)]) = 0 And x < x_max
										CurrMapGrid\Grid[x + (y * MapGridSize)] = 2
										CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] = 2
										CurrMapGrid\Grid[(x + 1) + ((y - 1) * MapGridSize)] = 1
										Temp = 1
									EndIf
								EndIf
								;[End Block]
						End Select
						If Temp = 1
							RoomAmount(ROOM2C, i) = RoomAmount(ROOM2C, i) + 1
							RoomAmount(ROOM2, i) = RoomAmount(ROOM2, i) + 1
						EndIf
					EndIf
					If Temp = 1 Then Exit
				Next
				If Temp = 1 Then Exit
			Next
		EndIf
	Next
	
	Local MaxRooms% = RoomAmount(ROOM1, 0) + RoomAmount(ROOM1, 1) + RoomAmount(ROOM1, 2)
	
	MaxRooms = Max(MaxRooms, RoomAmount(ROOM2, 0) + RoomAmount(ROOM2, 1) + RoomAmount(ROOM2, 2))
	MaxRooms = Max(MaxRooms, RoomAmount(ROOM2C, 0) + RoomAmount(ROOM2C, 1) + RoomAmount(ROOM2C, 2))
	MaxRooms = Max(MaxRooms, RoomAmount(ROOM3, 0) + RoomAmount(ROOM3, 1) + RoomAmount(ROOM3, 2))
	MaxRooms = Max(MaxRooms, RoomAmount(ROOM4, 0) + RoomAmount(ROOM4, 1) + RoomAmount(ROOM4, 2))
	
	Dim MapRoom$(ROOM4 + 1, MaxRooms)
	
	; ~ Forced room assignments
	; ~ Earlier SetRoom calls in each block take priority, so set important rooms first!
	
	; ~ [LIGHT CONTAINMENT ZONE]
	
	SetRoom(0, ROOM1, "cont1_173", 0.0)
	SetRoom(0, ROOM1, "cont1_005", 0.15)
	SetRoom(0, ROOM1, "room1_storage", 0.35)
	SetRoom(0, ROOM1, "cont1_914", 0.5)
	SetRoom(0, ROOM1, "cont1_205", 0.65)
	
	SetRoom(0, ROOM2, "room2_closets", 0.0)
	SetRoom(0, ROOM2, "room2_test_lcz", 0.1)
	SetRoom(0, ROOM2, "cont2_427_714_860_1025", 0.2)
	SetRoom(0, ROOM2, "room2_storage", 0.3)
	SetRoom(0, ROOM2, "room2_gw_2", 0.4)
	SetRoom(0, ROOM2, "cont2_012", 0.5)
	SetRoom(0, ROOM2, "room2_sl", 0.55)
	SetRoom(0, ROOM2, "cont2_500_1499", 0.6)
	SetRoom(0, ROOM2, "cont2_1123", 0.75)
	SetRoom(0, ROOM2, "room2_js", 0.85)
	SetRoom(0, ROOM2, "room2_elevator", 0.9)
	
	SetRoom(0, ROOM2C, "room2c_gw_lcz", 0.0)
	SetRoom(0, ROOM2C, "cont2c_066_1162_arc", 0.5)
	
	SetRoom(0, ROOM3, "room3_storage", Rnd(0.2, 0.6))
	SetRoom(0, ROOM3, "cont3_372", 0.8)
	
	SetRoom(0, ROOM4, "room4_ic", 0.3)
	
	; ~ [HEAVY CONTAINMENT ZONE]
	
	SetRoom(1, ROOM1, "cont1_079", 0.15)
	SetRoom(1, ROOM1, "cont1_106", 0.3)
	SetRoom(1, ROOM1, "cont1_035", 0.45)
	SetRoom(1, ROOM1, "cont1_895", 0.7)
	
	SetRoom(1, ROOM2, "room2_nuke", 0.1)
	SetRoom(1, ROOM2, "cont2_409", 0.15)
	SetRoom(1, ROOM2, "room2_mt", 0.25)
	SetRoom(1, ROOM2, "cont2_008", 0.4)
	SetRoom(1, ROOM2, "room2_shaft", 0.5)
	SetRoom(1, ROOM2, "cont2_049", 0.6)
	SetRoom(1, ROOM2, "room2_test_hcz", 0.7)
	SetRoom(1, ROOM2, "room2_servers_hcz", 0.9)
	
	SetRoom(1, ROOM2C, "cont2c_096", 0.5)
	
	SetRoom(1, ROOM3, "cont3_513", 0.5)
	SetRoom(1, ROOM3, "cont3_966", 0.8)
	
	; ~ [ENTRANCE ZONE]
	
	SetRoom(2, ROOM1, "gate_b_entrance", 1.0)
	SetRoom(2, ROOM1, "gate_a_entrance", 1.0)
	SetRoom(2, ROOM1, "room1_o5", 1.0)
	SetRoom(2, ROOM1, "room1_lifts", 0.0)
	
	SetRoom(2, ROOM2, "room2_scientists", 0.1)
	SetRoom(2, ROOM2, "room2_cafeteria", 0.2)
	SetRoom(2, ROOM2, "room2_6_ez", 0.25)
	SetRoom(2, ROOM2, "room2_office_3", 0.3)
	SetRoom(2, ROOM2, "room2_servers_ez", 0.4)
	SetRoom(2, ROOM2, "room2_office", 0.5)
	SetRoom(2, ROOM2, "room2_office_2", 0.55)
	SetRoom(2, ROOM2, "cont2_860_1", 0.6)
	SetRoom(2, ROOM2, "room2_medibay", 0.7)
	SetRoom(2, ROOM2, "room2_scientists_2", 0.8)
	SetRoom(2, ROOM2, "room2_ic", 0.9)
	
	SetRoom(2, ROOM2C, "room2c_ec", 0.0)
	SetRoom(2, ROOM2C, "room2c_gw_ez", 0.0)
	
	SetRoom(2, ROOM3, "room3_2_ez", 0.3)
	SetRoom(2, ROOM3, "room3_office", 0.5)
	SetRoom(2, ROOM3, "room3_3_ez", 0.7)
	
	Temp = 0
	For y = MapGridSize - 1 To 1 Step -1
		If y < (MapGridSize / 3) + 1
			Zone = 3
		ElseIf y < MapGridSize * (2.0 / 3.0)
			Zone = 2
		Else
			Zone = 1
		EndIf
		For x = 1 To MapGridSize - 2
			If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_CheckpointTile
				If y > MapGridSize / 2
					RoomID = r_room2_checkpoint_lcz_hcz
				Else
					RoomID = r_room2_checkpoint_hcz_ez
				EndIf
				r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID)
				CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
				CalculateRoomExtents(r)
			ElseIf CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				RoomID = -1
				Temp = Min(CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)], 1) + Min(CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)], 1)
				Select Temp
					Case 1 ; ~ Generate ROOM1
						;[Block]
						If CurrMapGrid\RoomID[ROOM1] < MaxRooms
							If MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM1, CurrMapGrid\RoomID[ROOM1]))
						EndIf
						
						If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)]
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
						ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)]
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
						ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)]
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
						Else
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
						EndIf
						r.Rooms = CreateRoom(Zone, ROOM1, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
						CurrMapGrid\RoomID[ROOM1] = CurrMapGrid\RoomID[ROOM1] + 1
						;[End Block]
					Case 2 ; ~ Generate ROOM2
						;[Block]
						If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]))
							EndIf
							If Rand(2) = 1
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
							Else
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
							EndIf
							r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						ElseIf CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
							If CurrMapGrid\RoomID[ROOM2] < MaxRooms
								If MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM2, CurrMapGrid\RoomID[ROOM2]))
							EndIf
							If Rand(2) = 1
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
							Else
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
							EndIf
							r.Rooms = CreateRoom(Zone, ROOM2, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
							CurrMapGrid\RoomID[ROOM2] = CurrMapGrid\RoomID[ROOM2] + 1
						Else
							If CurrMapGrid\RoomID[ROOM2C] < MaxRooms
								If MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM2C, CurrMapGrid\RoomID[ROOM2C]))
							EndIf
							If CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
							ElseIf CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
							ElseIf CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)] > MapGrid_NoTile And CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)] > MapGrid_NoTile
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
							Else
								CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
							EndIf
							r.Rooms = CreateRoom(Zone, ROOM2C, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
							CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
							CurrMapGrid\RoomID[ROOM2C] = CurrMapGrid\RoomID[ROOM2C] + 1
						EndIf
						;[End Block]
					Case 3 ; ~ Generate ROOM3
						;[Block]
						If CurrMapGrid\RoomID[ROOM3] < MaxRooms
							If MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM3, CurrMapGrid\RoomID[ROOM3]))
						EndIf
						If (Not CurrMapGrid\Grid[x + ((y - 1) * MapGridSize)])
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 2
						ElseIf (Not CurrMapGrid\Grid[(x - 1) + (y * MapGridSize)])
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 1
						ElseIf (Not CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)])
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 3
						Else
							CurrMapGrid\Angle[x + (y * MapGridSize)] = 0
						EndIf
						r.Rooms = CreateRoom(Zone, ROOM3, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
						CurrMapGrid\RoomID[ROOM3] = CurrMapGrid\RoomID[ROOM3] + 1
						;[End Block]
					Case 4 ; ~ Generate ROOM4
						;[Block]
						If CurrMapGrid\RoomID[ROOM4] < MaxRooms
							If MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4]) <> "" Then RoomID = FindRoomID(MapRoom(ROOM4, CurrMapGrid\RoomID[ROOM4]))
						EndIf
						CurrMapGrid\Angle[x + (y * MapGridSize)] = Rand(4)
						r.Rooms = CreateRoom(Zone, ROOM4, x * RoomSpacing, 0.0, y * RoomSpacing, RoomID, CurrMapGrid\Angle[x + (y * MapGridSize)] * 90.0)
						CurrMapGrid\RoomName[x + (y * MapGridSize)] = r\RoomTemplate\Name
						CurrMapGrid\RoomID[ROOM4] = CurrMapGrid\RoomID[ROOM4] + 1
						;[End Block]
				End Select
				CalculateRoomExtents(r)
			EndIf
		Next
	Next
	
	; ~ Spawn some rooms outside the map
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, PowTwo(RoomSpacing) * 2.0, r_gate_b)
	CalculateRoomExtents(r)
	
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 500.0, PowTwo(RoomSpacing), r_gate_a)
	CalculateRoomExtents(r)
	
	r.Rooms = CreateRoom(0, ROOM1, (MapGridSize - 1) * RoomSpacing, 0.0, (MapGridSize - 1) * RoomSpacing, r_dimension_106)
	CalculateRoomExtents(r)
	
	If opt\IntroEnabled
		r.Rooms = CreateRoom(0, ROOM1, RoomSpacing, 250.0, (MapGridSize - 1) * RoomSpacing, r_cont1_173_intro)
		CalculateRoomExtents(r)
	EndIf
	
	r.Rooms = CreateRoom(0, ROOM1, RoomSpacing, 800.0, 0.0, r_dimension_1499)
	CalculateRoomExtents(r)
	
	; ~ Prevent room overlaps
	For r.Rooms = Each Rooms
		PreventRoomOverlap(r)
	Next
	
	If opt\DebugMode
		Repeat
			ShowPointer()
			Cls()
			
			MousePosX = ScaledMouseX()
			MousePosY = ScaledMouseY()
			
			i = MapGridSize - 1
			For x = 0 To MapGridSize - 1
				For y = 0 To MapGridSize - 1
					If CurrMapGrid\Grid[x + (y * MapGridSize)] = MapGrid_NoTile
						Zone = GetZone(y)
						Color((50 * Zone) + 50, (50 * Zone) + 50, (50 * Zone) + 50)
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
					Else
						Select CurrMapGrid\Grid[x + (y * MapGridSize)]
							Case MapGrid_CheckpointTile
								;[Block]
								Color(0, 200, 0)
								;[End Block]
;							Case 5
;								;[Block]
;								Color(255, 50, 50)
;								;[End Block]
							Case 4
								;[Block]
								Color(50, 50, 255)
								;[End Block]
							Case 3
								;[Block]
								Color(50, 255, 255)
								;[End Block]
							Case 2
								;[Block]
								Color(255, 255, 50)
								;[End Block]
							Case 1
								;[Block]
								Color(255, 255, 255)
								;[End Block]
						End Select
						Rect((i * 32) * MenuScale, (y * 32) * MenuScale, 30 * MenuScale, 30 * MenuScale)
						If MouseOn((i * 32) * MenuScale, (y * 32) * MenuScale, 32 * MenuScale, 32 * MenuScale)
							Color(255, 0, 0)
							TextEx(((i * 32) + 2) * MenuScale, ((y * 32) + 2) * MenuScale, CurrMapGrid\Grid[x + (y * MapGridSize)] + " " + CurrMapGrid\RoomName[x + (y * MapGridSize)])
						EndIf
					EndIf
				Next
				i = i - 1
			Next
			
			Color(255, 255, 255)
			TextEx(6 * MenuScale, 12 * MenuScale, CurrMapGrid\RoomID[ROOM1])
			Color(255, 255, 50)
			TextEx(6 * MenuScale, 44 * MenuScale, CurrMapGrid\RoomID[ROOM2])
			Color(255, 50, 50)
			TextEx(6 * MenuScale, 76 * MenuScale, CurrMapGrid\RoomID[ROOM2C])
			Color(50, 255, 255)
			TextEx(6 * MenuScale, 108 * MenuScale, CurrMapGrid\RoomID[ROOM3])
			Color(50, 50, 255)
			TextEx(6 * MenuScale, 140 * MenuScale, CurrMapGrid\RoomID[ROOM4])
			
			Color(255, 255, 255)
			TextEx(mo\Viewport_Center_X, opt\GraphicHeight - (15 * MenuScale), Format(GetLocalString("menu", "new.seed"), RandomSeed), True, True)
			RenderLoadingText(mo\Viewport_Center_X, opt\GraphicHeight - (35 * MenuScale), GetLocalString("menu", "anykey"), True, True)
			
			Flip()
			RenderCursor()
		Until (GetKey() <> 0 Lor MouseHit(1))
	EndIf
	
	For y = 0 To MapGridSize
		For x = 0 To MapGridSize
			CurrMapGrid\Grid[x + (y * MapGridSize)] = Min(CurrMapGrid\Grid[x + (y * MapGridSize)], 1)
		Next
	Next
	
	; ~ Create the doors between rooms
	For y = MapGridSize To 0 Step -1
		If y < I_Zone\Transition[1] - 1
			Zone = 3
		ElseIf y >= I_Zone\Transition[1] - 1 And y < I_Zone\Transition[0] - 1
			Zone = 2
		Else
			Zone = 1
		EndIf
		For x = MapGridSize To 0 Step -1
			If CurrMapGrid\Grid[x + (y * MapGridSize)] > MapGrid_NoTile
				For r.Rooms = Each Rooms
					r\Angle = WrapAngle(r\Angle)
					If Int(r\x / RoomSpacing) = x And Int(r\z / RoomSpacing) = y
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								ShouldSpawnDoor = (r\Angle = 90.0)
								;[End Block]
							Case ROOM2
								;[Block]
								ShouldSpawnDoor = (r\Angle = 90.0 Lor r\Angle = 270.0 )
								;[End Block]
							Case ROOM2C
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 90.0)
								;[End Block]
							Case ROOM3
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 180.0 Lor r\Angle = 90.0)
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						
						If ShouldSpawnDoor
							If x + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[(x + 1) + (y * MapGridSize)] > MapGrid_NoTile Then r\AdjDoor[0] = CreateDoor(r, Float(x) * RoomSpacing + (RoomSpacing / 2.0), 0.0, Float(y) * RoomSpacing, 90.0, Max(Rand(-3, 1), 0), ((Zone - 1) Mod 2) * 2)
							EndIf
						EndIf
						
						Select r\RoomTemplate\Shape
							Case ROOM1
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0)
								;[End Block]
							Case ROOM2
								;[Block]
								ShouldSpawnDoor = (r\Angle = 0.0 Lor r\Angle = 180.0)
								;[End Block]
							Case ROOM2C
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0 Lor r\Angle = 90.0)
								;[End Block]
							Case ROOM3
								;[Block]
								ShouldSpawnDoor = (r\Angle = 180.0 Lor r\Angle = 90.0 Lor r\Angle = 270.0)
								;[End Block]
							Default
								;[Block]
								ShouldSpawnDoor = True
								;[End Block]
						End Select
						If ShouldSpawnDoor
							If y + 1 < MapGridSize + 1
								If CurrMapGrid\Grid[x + ((y + 1) * MapGridSize)] > MapGrid_NoTile Then r\AdjDoor[3] = CreateDoor(r, Float(x) * RoomSpacing, 0.0, Float(y) * RoomSpacing + (RoomSpacing / 2.0), 0.0, Max(Rand(-3, 1), 0), ((Zone - 1) Mod 2) * 2)
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
		;SetupTriggerBoxes(r)
		For i = 0 To MaxRoomAdjacents - 1
			r\Adjacent[i] = Null
		Next
		For r2.Rooms = Each Rooms
			If r <> r2
				If r2\z = r\z
					If r2\x = r\x + 8.0
						r\Adjacent[0] = r2
						If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
					ElseIf r2\x = r\x - 8.0
						r\Adjacent[2] = r2
						If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
					EndIf
				ElseIf r2\x = r\x
					If r2\z = r\z - 8.0
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

Function LoadTerrain%(HeightMap%, yScale# = 0.7, Tex1%, Tex2%, Mask%)
	; ~ Load the HeightMap
	If HeightMap = 0 Then RuntimeError(Format(GetLocalString("runerr", "heightmap"), HeightMap))
	; ~ Load texture and lightmaps
	If Tex1 = 0 Then RuntimeError(Format(GetLocalString("runerr", "tex_1"), Tex1))
	If Tex2 = 0 Then RuntimeError(Format(GetLocalString("runerr", "tex_2"), Tex2))
	If Mask = 0 Then RuntimeError(Format(GetLocalString("runerr", "mask"), Mask))
	
	; ~ Store HeightMap dimensions
	Local HeightMapWidth% = ImageWidth(HeightMap) - 1
	Local HeightMapHeight% = ImageHeight(HeightMap) - 1
	Local PosX%, PosY%, VertexIndex%
	
	; ~ Scale the textures to the right size
	ScaleTexture(Tex1, HeightMapWidth / 4.0, HeightMapHeight / 4.0)
	ScaleTexture(Tex2, HeightMapWidth / 4.0, HeightMapHeight / 4.0)
	ScaleTexture(Mask, HeightMapWidth, HeightMapHeight)
	
	; ~ Start building the terrain
	Local Mesh% = CreateMesh()
	Local Surf% = CreateSurface(Mesh)
	
	; ~ Create some verts for the terrain
	For PosY = 0 To HeightMapHeight
		For PosX = 0 To HeightMapWidth
			AddVertex(Surf, PosX, 0.0, PosY, 1.0 / PosX, 1.0 / PosY)
		Next
	Next
	RenderWorld()
	
	Local HeightMapWidth2% = HeightMapWidth + 1
	
	; ~ Connect the verts with faces
	For PosY = 0 To HeightMapHeight - 1
		For PosX = 0 To HeightMapWidth - 1
			Local Shift% = PosX + (HeightMapWidth2 * PosY)
			
			AddTriangle(Surf, Shift, Shift + HeightMapWidth2, Shift + 1)
			AddTriangle(Surf, Shift + 1, Shift + HeightMapWidth2, Shift + HeightMapWidth2 + 1)
		Next
	Next
	
	; ~ Position the terrain to center 0.0, 0.0, 0.0
	Local Mesh2% = CopyMesh(Mesh, Mesh)
	Local Surf2% = GetSurface(Mesh2, 1)
	
	PositionMesh(Mesh, (-HeightMapWidth) / 2.0, 0.0, (-HeightMapHeight) / 2.0)
	PositionMesh(Mesh2, (-HeightMapWidth) / 2.0, 0.01, (-HeightMapHeight) / 2.0)
	
	Local HeightMapBuffer% = ImageBuffer(HeightMap)
	Local MaskBuffer% = TextureBuffer(Mask)
	Local MaskWidth% = TextureWidth(Mask)
	Local MaskHeight% = TextureHeight(Mask)
	
	; ~ Alter vertice height to match the heightmap red channel
	LockBuffer(HeightMapBuffer)
	LockBuffer(MaskBuffer)
	
	For PosX = 0 To HeightMapWidth
		For PosY = 0 To HeightMapHeight
			; ~ Using vertex alpha and two meshes instead of FE_ALPHAWHATEVER
			; ~ It doesn't look perfect but it does the job
			; ~ You might get better results by downscaling the mask to the same size as the heightmap
			Local MaskX# = Min(PosX * Float(MaskWidth) / Float(HeightMapWidth2), MaskWidth - 1)
			Local MaskY# = MaskHeight - Min(PosY * Float(MaskHeight) / Float(HeightMapHeight + 1), MaskHeight - 1)
			Local RGB%, RED%
			
			RGB = ReadPixelFast(Min(PosX, HeightMapWidth - 1), HeightMapHeight - Min(PosY, HeightMapHeight - 1), HeightMapBuffer)
			RED = (RGB And $FF0000) Shr 16 ; ~ Separate out the red
			
			Local Alpha# = (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Max(MaskY - 5.0, 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, MaskWidth - 5.0), Min(MaskY + 5.0, MaskHeight - 5), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Max(MaskX - 5.0, 5.0), Min(MaskY + 5.0, MaskHeight - 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha + (((ReadPixelFast(Min(MaskX + 5.0, MaskWidth - 5.0), Max(MaskY - 5.0, 5.0), MaskBuffer) And $FF000000) Shr 24) / $FF)
			Alpha = Alpha * 0.25
			Alpha = Sqr(Alpha)
			
			VertexIndex = PosX + (HeightMapWidth2 * PosY)
			VertexCoords(Surf, VertexIndex , VertexX(Surf, VertexIndex), RED * yScale, VertexZ(Surf, VertexIndex))
			VertexCoords(Surf2, VertexIndex , VertexX(Surf2, VertexIndex), RED * yScale, VertexZ(Surf2, VertexIndex))
			VertexColor(Surf2, VertexIndex, 255.0, 255.0, 255.0, Alpha)
			; ~ Set the terrain texture coordinates
			VertexTexCoords(Surf, VertexIndex, PosX, -PosY)
			VertexTexCoords(Surf2, VertexIndex, PosX, -PosY) 
		Next
	Next
	UnlockBuffer(MaskBuffer)
	UnlockBuffer(HeightMapBuffer)
	
	UpdateNormals(Mesh)
	UpdateNormals(Mesh2)
	
	EntityTexture(Mesh, Tex1, 0, 1)
	EntityTexture(Mesh2, Tex2, 0, 1)
	
	EntityFX(Mesh, 1)
	EntityFX(Mesh2, 1 + 2 + 32)
	
	Return(Mesh)
End Function

RenderLoading(60, GetLocalString("loading", "core.sky"))

Include "Source Code\Sky_Core.bb"

Global CHUNKDATA%[4096]
Global SCP1499Chunks%

Function SetChunkDataValues%()
	Local StrTemp$, i%, j%
	Local ChunkArray% = JsonGetArraySize(SCP1499Chunks)
	
	StrTemp = ""
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	For i = 0 To 62 Step 2
		For j = 0 To 62 Step 2
			CHUNKDATA[i + (j * 64)] = Rand(0, ChunkArray - 1)
			CHUNKDATA[(i + 1) + ((j + 1) * 64)] = Rand(0, ChunkArray - 1)
		Next
	Next
	
	SeedRnd(MilliSecs())
End Function

Type ChunkPart
	Field Amount%
	Field OBJ%[128]
	Field RandomYaw#[128]
	Field ID%
End Type

Function CreateChunkParts%(r.Rooms)
	Local chp.ChunkPart, chp2.ChunkPart
	Local i%, StrTemp$, j%
	Local ReadingChunk%
	Local ArraySize% = JsonGetArraySize(SCP1499Chunks)
	
	SeedRnd(GenerateSeedNumber(RandomSeed))
	
	For i = 0 To ArraySize - 1
		ReadingChunk = JsonGetArray(JsonGetValue(JsonGetArrayValue(SCP1499Chunks, i), "objects"))
		chp.ChunkPart = New ChunkPart
		chp\Amount = JsonGetArraySize(ReadingChunk)
		For j = 0 To chp\Amount - 1
			Local OBJ_ID% = JsonGetInt(JsonGetValue(JsonGetArrayValue(ReadingChunk, j), "id"))
			Local x$ = JsonGetInt(JsonGetValue(JsonGetArrayValue(ReadingChunk, j), "x"))
			Local z$ = JsonGetInt(JsonGetValue(JsonGetArrayValue(ReadingChunk, j), "z"))
			Local Yaw$ = JsonGetValue(JsonGetArrayValue(ReadingChunk, j), "yaw")
			
			chp\OBJ[j] = CopyEntity(r\Objects[OBJ_ID])
			If JsonIsNull(Yaw)
				chp\RandomYaw[j] = Rnd(360.0)
				RotateEntity(chp\OBJ[j], 0.0, chp\RandomYaw[j], 0.0)
			Else
				RotateEntity(chp\OBJ[j], 0.0, JsonGetFloat(Yaw), 0.0)
			EndIf
			PositionEntity(chp\OBJ[j], Float(x), 0, Float(z))
			ScaleEntity(chp\OBJ[j], RoomScale, RoomScale, RoomScale)
			EntityType(chp\OBJ[j], HIT_MAP)
			EntityPickMode(chp\OBJ[j], 2)
			HideEntity(chp\OBJ[j])
		Next
		chp2 = Before(chp)
		If chp2 <> Null Then chp\ID = chp2\ID + 1
	Next
	
	SeedRnd(MilliSecs())
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
	PositionEntity(ch\ChunkPivot, x + 20.0, y, z + 20.0, True)
	ch\x = x
	ch\y = y
	ch\z = z
	
	ch\IsSpawnChunk = IsSpawnChunk
	
	If OBJ > -1
		ch\Amount = JsonGetArraySize(JsonGetArray(JsonGetValue(JsonGetArrayValue(SCP1499Chunks, OBJ), "objects")))
		For chp.ChunkPart = Each ChunkPart
			If chp\ID = OBJ
				For i = 0 To ch\Amount - 1
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

Function UpdateChunks%(ChunkPartAmount%, SpawnNPCs% = True)
	Local ch.Chunk, ch2.Chunk, n.NPCs
	Local StrTemp$, i%, j%
	Local PlayerPosX# = EntityX(me\Collider)
	Local y# = EntityY(PlayerRoom\OBJ)
	Local PlayerPosZ# = EntityZ(me\Collider)
	Local ChunkX# = Int(PlayerPosX / 40.0)
	Local ChunkZ# = Int(PlayerPosZ / 40.0)
	Local PlayerRoomY# = y + 0.5
	Local x# = (-ChunkMaxDistance) + (ChunkX * 40.0)
	Local z# = (-ChunkMaxDistance) + (ChunkZ * 40.0)
	
	Local CurrChunkData% = 0, MaxChunks% = JsonGetArraySize(SCP1499Chunks)
	
	Repeat
		Local ChunkFound% = False
		
		For ch.Chunk = Each Chunk
			If ch\x = x And ch\z = z
				ChunkFound = True
				Exit
			EndIf
		Next
		If (Not ChunkFound)
			CurrChunkData = CHUNKDATA[Abs(((x + 32) / 40) Mod 64) + Abs((((z + 32) / 40) Mod 64) * 64)]
			ch2.Chunk = CreateChunk(CurrChunkData, x, y, z)
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
			If DistanceSquared(PlayerPosX, EntityX(ch\ChunkPivot), PlayerPosZ, EntityZ(ch\ChunkPivot)) > PowTwo(ChunkMaxDistance)
				RemoveChunk(ch)
			EndIf
		EndIf
	Next
	
	Local CurrNPCNumber% = 0
	
	For n.NPCs = Each NPCs
		If n\NPCType = NPCType1499_1 Then CurrNPCNumber = CurrNPCNumber + 1
	Next
	
	Local MaxNPCs% = 64 ; ~ The maximum amount of NPCs in dimension_1499
	Local e.Events
	
	For e.Events = Each Events
		If e\room = PlayerRoom
			If e\room\NPC[0] <> Null
				MaxNPCs = 16
				Exit
			EndIf
		EndIf
	Next
	
	If CurrNPCNumber < MaxNPCs
		Select Rand(8)
			Case 1
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(40.0, 80.0), PlayerRoomY, PlayerPosZ + Rnd(40.0, 80.0))
				;[End Block]
			Case 2
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(40.0, 80.0), PlayerRoomY, PlayerPosZ + Rnd(-40.0, 40.0))
				;[End Block]
			Case 3
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(40.0, 80.0), PlayerRoomY, PlayerPosZ + Rnd(-40.0, -80.0))
				;[End Block]
			Case 4
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(-40.0, 40.0), PlayerRoomY, PlayerPosZ + Rnd(-40.0, -80.0))
				;[End Block]
			Case 5
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(-40.0, -80.0), PlayerRoomY, PlayerPosZ + Rnd(-40.0, -80.0))
				;[End Block]
			Case 6
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(-40.0, -80.0), PlayerRoomY, PlayerPosZ + Rnd(-40.0, 40.0))
				;[End Block]
			Case 7
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(-40.0, -80.0), PlayerRoomY, PlayerPosZ + Rnd(40.0, 80.0))
				;[End Block]
			Case 8
				;[Block]
				n.NPCs = CreateNPC(NPCType1499_1, PlayerPosX + Rnd(-40.0, 40.0), PlayerRoomY, PlayerPosZ + Rnd(40.0, 80.0))
				;[End Block]
		End Select
		If Rand(2) = 1 Then n\State2 = 500.0 * 3.0
		n\Angle = Rnd(360.0)
	Else
		For n.NPCs = Each NPCs
			If n\NPCType = NPCType1499_1
				If n\PrevState = 0
					; ~ This will be updated like this so that new NPCs can spawn for the player
					If EntityDistanceSquared(n\Collider, me\Collider) > PowTwo(ChunkMaxDistance) Lor EntityY(n\Collider) < y - 5.0 Then RemoveNPC(n)
				EndIf
			EndIf
		Next
	EndIf
	
End Function

Function HideChunks%()
	Local ch.Chunk, i%
	
	For ch.Chunk = Each Chunk
		If (Not ch\IsSpawnChunk) Then RemoveChunk(ch)
	Next
End Function

Function RemoveChunk%(ch.Chunk)
	Local i%
	
	For i = 0 To 127
		If ch\OBJ[i] <> 0 Then FreeEntity(ch\OBJ[i]) : ch\OBJ[i] = 0
	Next
	FreeEntity(ch\PlatForm) : ch\PlatForm = 0
	FreeEntity(ch\ChunkPivot) : ch\ChunkPivot = 0
	Delete(ch)
End Function

Function RemoveChunkPart%(chp.ChunkPart)
	Local i%
	
	For i = 0 To 127
		If chp\OBJ[i] <> 0 Then FreeEntity(chp\OBJ[i]) : chp\OBJ[i] = 0
	Next
	Delete(chp)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS