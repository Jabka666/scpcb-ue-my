Global ItemAmount%, MaxItemAmount%

Global LastItemID%

Type ItemTemplates
	Field DisplayName$
	Field Name$
	Field ID%
	Field SoundID%
	Field Found%
	Field OBJ%, OBJPath$
	Field InvImg%, InvImg2%, InvImgPath$, InvImgPath2$
	Field ImgPath$, Img%, Img2%
	Field ImgWidth%, ImgHeight%
	Field Img2Width%, Img2Height%
	Field IsAnim%
	Field Scale#
	Field Tex%, TexPath$
End Type

; ~ Item ID Constants
;[Block]
; ~ [PAPER]
;[Block]
Const it_paper% = 0
Const it_oldpaper% = 1

Const it_origami% = 2

Const it_badge% = 3
Const it_badge2% = 4

Const it_ticket% = 5
;[End Block]

; ~ [SCPs AND VARIATIONS]
;[Block]
Const it_scp005% = 6
Const it_coarse005% = 7
Const it_crystal005% = 8

Const it_scp148ingot% = 9
Const it_scp148% = 10

Const it_scp268% = 11
Const it_fine268% = 12
Const it_cap% = 13

Const it_scp420j% = 14
Const it_cigarette% = 15
Const it_joint% = 16
Const it_joint_smelly% = 17

Const it_scp427% = 18
Const it_scp500% = 19
Const it_scp500pill% = 20
Const it_scp500pilldeath% = 21
Const it_pill = 22

Const it_scp513% = 23
Const it_fine513% = 24

Const it_scp714% = 25
Const it_coarse714% = 26
Const it_fine714% = 27
Const it_ring% = 28

Const it_scp860% = 29
Const it_fine860% = 30

Const it_scp1025% = 31
Const it_fine1025% = 32
Const it_book% = 33

Const it_scp1123% = 34

Const it_scp1499% = 35
Const it_fine1499% = 36

Const it_scp2022% = 37
Const it_scp2022pill% = 38
;[End Block]

; ~ [MISC ITEMS]
;[Block]
Const it_helmet% = 39

Const it_vest% = 40
Const it_corrvest% = 41
Const it_finevest% = 42
Const it_veryfinevest% = 43

Const it_cup% = 44
Const it_emptycup% = 45

Const it_clipboard% = 46
Const it_wallet% = 47

Const it_electronics% = 48

Const it_eyedrops% = 49
Const it_eyedrops2% = 50
Const it_fineeyedrops% = 51
Const it_veryfineeyedrops% = 52

Const it_firstaid% = 53
Const it_firstaid2% = 54
Const it_finefirstaid% = 55
Const it_veryfinefirstaid% = 56

Const it_gasmask% = 57
Const it_finegasmask% = 58
Const it_veryfinegasmask% = 59
Const it_gasmask148% = 60

Const it_headphones% = 61

Const it_hazmatsuit% = 62
Const it_finehazmatsuit% = 63
Const it_veryfinehazmatsuit% = 64
Const it_hazmatsuit148% = 65

Const it_nvg% = 66
Const it_finenvg% = 67
Const it_veryfinenvg% = 68
Const it_scramble% = 69
Const it_finescramble% = 70

Const it_radio% = 71
Const it_18vradio% = 72
Const it_fineradio% = 73
Const it_veryfineradio% = 74

Const it_nav% = 75
Const it_nav300% = 76
Const it_nav310% = 77
Const it_navulti% = 78

Const it_e_reader% = 79
Const it_e_reader20% = 80
Const it_e_readerulti% = 81

Const it_coarsebat% = 82
Const it_bat% = 83
Const it_finebat% = 84
Const it_veryfinebat% = 85
Const it_killbat% = 86

Const it_syringe% = 87
Const it_finesyringe% = 88
Const it_veryfinesyringe% = 89
Const it_syringeinf% = 90
;[End Block]

; ~ [KEYCARDS, HANDS, KEYS, CARDS, COINS]
;[Block]
Const it_key0% = 91
Const it_key1% = 92
Const it_key2% = 93
Const it_key3% = 94
Const it_key4% = 95
Const it_key5% = 96
Const it_key6% = 97
Const it_keyomni% = 98

Const it_mastercard% = 99
Const it_mastercard_golden% = 100
Const it_playcard% = 101

Const it_hand% = 102
Const it_hand2% = 103
Const it_hand3% = 104

Const it_key_yellow% = 105
Const it_key_white% = 106
Const it_lostkey% = 107

Const it_25ct% = 108
Const it_coin% = 109

Const it_pizza% = 110
;[End Block]
;[End Block]

Const ItemHUDTexturePathLen% = 23

Function CreateItemTemplate.ItemTemplates(DisplayName$, Name$, ID%, OBJPath$, InvImgPath$, ImgPath$, Scale#, SoundID%, TexturePath$ = "", InvImgPath2$ = "", HasAnim% = False, TexFlags% = 1)
	Local it.ItemTemplates, it2.ItemTemplates
	
	it.ItemTemplates = New ItemTemplates
	
	; ~ If another item shares the same object, copy it
	OBJPath = ItemsPath + OBJPath
	For it2.ItemTemplates = Each ItemTemplates
		If it2\OBJPath = OBJPath And it2\OBJ <> 0
			it\OBJ = CopyEntity(it2\OBJ)
			Exit
		EndIf
	Next
	
	If it\OBJ = 0
		If HasAnim
			it\OBJ = LoadAnimMesh_Strict(OBJPath)
		Else
			it\OBJ = LoadMesh_Strict(OBJPath)
		EndIf
	EndIf
	
	it\IsAnim = HasAnim
	it\OBJPath = OBJPath
	
	Local Texture% = 0
	
	If TexturePath <> ""
		If TexturePath = ImgPath And FileType(ItemsPath + TexturePath) = 0
			TexturePath = ItemHUDTexturePath + TexturePath
		Else
			TexturePath = ItemsPath + TexturePath
		EndIf
		For it2.ItemTemplates = Each ItemTemplates
			If it2\TexPath = TexturePath And it2\Tex <> 0
				Texture = it2\Tex
				Exit
			EndIf
		Next
		If Texture = 0
			Texture = LoadTexture_Strict(TexturePath, TexFlags)
			it\TexPath = TexturePath
			EntityTexture(it\OBJ, Texture)
			it\Tex = Texture
			UpdateEntityMaterial(it\OBJ)
		EndIf
	EndIf
	
	ScaleEntity(it\OBJ, Scale, Scale, Scale, True)
	it\Scale = Scale
	
	; ~ If another item shares the same inv icon, copy it
	InvImgPath = ItemINVIconPath + InvImgPath
	For it2.ItemTemplates = Each ItemTemplates
		If it2\InvImgPath = InvImgPath
			If it2\InvImg <> 0
				it\InvImg = it2\InvImg
				it\InvImgPath = it2\InvImgPath
			EndIf
			Exit
		EndIf
	Next
	If it\InvImg = 0
		it\InvImg = ResizeImageEx(LoadImage_Strict(InvImgPath), MenuScale, MenuScale)
		it\InvImgPath = InvImgPath
	EndIf
	
	If InvImgPath2 <> ""
		InvImgPath2 = ItemINVIconPath + InvImgPath2
		For it2.ItemTemplates = Each ItemTemplates
			If it2\InvImgPath2 = InvImgPath2
				If it2\InvImg2 <> 0
					it\InvImg2 = it2\InvImg2
					it\InvImgPath2 = it2\InvImgPath2
				EndIf
				Exit
			EndIf
		Next
		If it\InvImg2 = 0
			it\InvImg2 = ResizeImageEx(LoadImage_Strict(InvImgPath2), MenuScale, MenuScale)
			it\InvImgPath2 = InvImgPath2
		EndIf
	EndIf
	
	If ImgPath <> ""
		ImgPath = ItemHUDTexturePath + ImgPath
		it\ImgPath = ImgPath
	EndIf
	
	it\ID = ID
	it\Name = Name
	it\DisplayName = DisplayName
	
	it\SoundID = SoundID
	
	HideEntity(it\OBJ)
	
	Return(it)
End Function

Function RemoveItemTemplate(itt.ItemTemplates)
	FreeEntity(itt\OBJ) : itt\OBJ = 0
	
	FreeImage(itt\InvImg) : itt\InvImg = 0
	If itt\InvImg2 <> 0 Then FreeImage(itt\InvImg2) : itt\InvImg2 = 0
	
	If itt\Img <> 0 Then FreeImage(itt\Img) : itt\Img = 0
	If itt\Img2 <> 0 Then FreeImage(itt\Img2) : itt\Img2 = 0
	
	If itt\Tex <> 0 Then DeleteSingleTextureEntryFromCache(itt\Tex) : itt\Tex = 0
	Delete(itt)
End Function

Function GetRandDocument$()
	Select Rand(26)
		Case 1
			;[Block]
			Return("005")
			;[End Block]
		Case 2
			;[Block]
			Return("008")
			;[End Block]
		Case 3
			;[Block]
			Return("009")
			;[End Block]
		Case 4
			;[Block]
			Return("012")
			;[End Block]
		Case 5
			;[Block]
			Return("049")
			;[End Block]
		Case 6
			;[Block]
			Return("066")
			;[End Block]
		Case 7
			;[Block]
			Return("096")
			;[End Block]
		Case 8
			;[Block]
			Return("106")
			;[End Block]
		Case 9
			;[Block]
			Return("173")
			;[End Block]
		Case 10
			;[Block]
			Return("205")
			;[End Block]
		Case 11
			;[Block]
			Return("294")
			;[End Block]
		Case 12
			;[Block]
			Return("409")
			;[End Block]
		Case 13
			;[Block]
			Return("513")
			;[End Block]
		Case 14
			;[Block]
			Return("682")
			;[End Block]
		Case 15
			;[Block]
			Return("714")
			;[End Block]
		Case 16
			;[Block]
			Return("860")
			;[End Block]
		Case 17
			;[Block]
			Return("860-1")
			;[End Block]
		Case 18
			;[Block]
			Return("914")
			;[End Block]
		Case 19
			;[Block]
			Return("895")
			;[End Block]
		Case 20
			;[Block]
			Return("939")
			;[End Block]
		Case 21
			;[Block]
			Return("966")
			;[End Block]
		Case 22
			;[Block]
			Return("970")
			;[End Block]
		Case 23
			;[Block]
			Return("1048")
			;[End Block]
		Case 24
			;[Block]
			Return("1162-ARC")
			;[End Block]
		Case 25
			;[Block]
			Return("1499")
			;[End Block]
		Case 26
			;[Block]
			Return("2022")
			;[End Block]
	End Select
End Function

Global CurrEReaderPage.ItemTemplates

Const PossibleEReaderPageAmount% = 78

Type Items
	Field DisplayName$
	Field Name$
	Field Collider%, OBJ%, OBJ2%
	Field ItemTemplate.ItemTemplates
	Field DropSpeed#
	Field R%, G%, B%, Alpha#
	Field Dist#, Nearby%
	Field State#, State2#, State3#
	Field UsageTimer#
	Field Picked%, Dropped%
	Field InvImg%
	Field SecondInv.Items[20]
	Field ID%
	Field InvSlots%
	Field ItemHeight#
	Field TargetNX#, TargetNY#, TargetNZ#
	Field EReaderPage.ItemTemplates[PossibleEReaderPageAmount] ; ~ 0 is a home page
	Field EReaderPageAmount%
End Type

Dim Inventory.Items(0)

Global SelectedItem.Items

Global ClosestItem.Items
Global OtherOpen.Items = Null

Function CreateItem.Items(Name$, ID%, x#, y#, z#, R% = 0, G% = 0, B% = 0, Alpha# = 1.0, InvSlots% = 0)
	CatchErrors("CreateItem.Items(" + Name + ", " + ID + ", " + x + ", " + y + ", " + z + ", " + R + ", " + G + ", " + B + ", " + Alpha + ", " + InvSlots + ")")
	
	Local i.Items, it.ItemTemplates
	
	Name = Lower(Name)
	
	i.Items = New Items
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\Name) = Name And it\ID = ID
			i\ItemTemplate = it
			i\Collider = CreatePivot()
			EntityRadius(i\Collider, 0.01)
			i\OBJ = CopyEntity(it\OBJ, i\Collider)
			i\DisplayName = it\DisplayName
			i\Name = it\Name
			ShowEntity(i\Collider)
			ShowEntity(i\OBJ)
			Exit
		EndIf
	Next 
	
	If i\ItemTemplate = Null Then RuntimeErrorEx(Format(Format(GetLocalString("runerr", "item"), Name, "{0}"), ID, "{1}"))
	
	ResetEntity(i\Collider)
	PositionEntity(i\Collider, x, y, z, True)
	RotateEntity(i\Collider, 0.0, Rnd(360.0), 0.0)
	EntityType(i\Collider, HIT_ITEM)
	
	i\Dist = EntityDistanceSquared(me\Collider, i\Collider)
	i\DropSpeed = 0.0
	i\Nearby = True
	
	i\InvImg = i\ItemTemplate\InvImg
	
	Select ID
		Case it_cup
			;[Block]
			i\R = R
			i\G = G
			i\B = B
			i\Alpha = Alpha
			i\State = 1.0
			
			Local Liquid% = CopyEntity(misc_I\CupLiquid)
			
			ScaleEntity(Liquid, i\ItemTemplate\Scale, i\ItemTemplate\Scale, i\ItemTemplate\Scale, True)
			PositionEntity(Liquid, EntityX(i\Collider, True), EntityY(i\Collider, True), EntityZ(i\Collider, True))
			EntityParent(Liquid, i\OBJ)
			EntityColor(Liquid, R, G, B)
			
			If Alpha < 0.0 Then EntityFX(Liquid, 1)
			EntityAlpha(Liquid, Abs(Alpha))
			EntityShininess(Liquid, 1.0)
			;[End Block]
		Case it_clipboard, it_wallet, it_scp500
			;[Block]
			If InvSlots = 0
				InvSlots = 10 + (10 * (i\ItemTemplate\ID = it_scp500))
				SetAnimTime(i\OBJ, (i\ItemTemplate\ID = it_clipboard) * 17.0 + (i\ItemTemplate\ID = it_wallet) * 2.0 + (i\ItemTemplate\ID = it_scp500) * 11.0)
				If i\ItemTemplate\InvImg2 <> 0 Then i\InvImg = i\ItemTemplate\InvImg2
			EndIf
			;[End Block]
		Case it_scp2022pill, it_scp2022
			;[Block]
			Local Light% = CreateLight(2, i\Collider)
			
			LightRange(Light, 0.15)
			LightColor(Light, 255.0, 255.0, 140.0)
			MoveEntity(Light, 0.0, 0.1, 0.0)
			
			If i\ItemTemplate\ID = it_scp2022pill
				i\OBJ2 = CreateSprite()
				PositionEntity(i\OBJ2, x, y, z)
				ScaleSprite(i\OBJ2, 0.03, 0.03)
				EntityTexture(i\OBJ2, misc_I\AdvancedLightSprite)
				EntityFX(i\OBJ2, 1 + 8)
				EntityBlend(i\OBJ2, 3)
				EntityColor(i\OBJ2, 200.0, 200.0, 70.0)
				RotateEntity(i\OBJ2, 0.0, 0.0, Rnd(360.0))
				EntityAlpha(i\OBJ2, 0.6)
				SpriteViewMode(i\OBJ2, 1)
				EntityParent(i\OBJ2, i\Collider)
			EndIf
			;[End Block]
	End Select
	SetDeferredEntity(i\OBJ, True)
	
	i\InvSlots = InvSlots
	
	i\ID = LastItemID + 1
	LastItemID = i\ID
	
	CatchErrors("Uncaught: CreateItem.Items(" + Name + ", " + ID + ", " + x + ", " + y + ", " + z + ", " + R + ", " + G + ", " + B + ", " + Alpha + ", " + InvSlots + ")")
	
	Return(i)
End Function

Function RemoveItem%(i.Items)
	CatchErrors("RemoveItem()")
	
	Local n%
	
	If i\OBJ2 <> 0 Then FreeEntity(i\OBJ2) : i\OBJ2 = 0
	FreeEntity(i\OBJ) : i\OBJ = 0
	FreeEntity(i\Collider) : i\Collider = 0
	
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = i
			Inventory(n) = Null
			ItemAmount = ItemAmount - 1
			Exit
		EndIf
	Next
	If SelectedItem = i Then SelectedItem = Null
	
	If i\ItemTemplate\Img <> 0 Then FreeImage(i\ItemTemplate\Img) : i\ItemTemplate\Img = 0
	If i\ItemTemplate\Img2 <> 0 Then FreeImage(i\ItemTemplate\Img2) : i\ItemTemplate\Img2 = 0
	Delete(i)
	
	CatchErrors("Uncaught: RemoveItem()")
End Function

Function RemoveWearableItems%(item.Items)
	Select item\ItemTemplate\ID
		Case it_gasmask, it_finegasmask, it_veryfinegasmask, it_gasmask148
			;[Block]
			wi\GasMask = 0
			;[End Block]
		Case it_scp1499, it_fine1499
			;[Block]
			I_1499\Using = 0
			;[End Block]
		Case it_nvg, it_finenvg, it_veryfinenvg
			;[Block]
			If wi\NightVision > 0 Then fog\FarDist = 6.0 : wi\NightVision = 0
			;[End Block]
		Case it_scramble, it_finescramble
			;[Block]
			If wi\SCRAMBLE > 0 Then fog\FarDist = 6.0 : wi\SCRAMBLE = 0
			;[End Block]
		Case it_helmet
			;[Block]
			wi\BallisticHelmet = False
			;[End Block]
		Case it_vest, it_finevest
			;[Block]
			wi\BallisticVest = 0
			;[End Block]
		Case it_hazmatsuit, it_finehazmatsuit, it_veryfinehazmatsuit, it_hazmatsuit148
			;[Block]
			wi\HazmatSuit = 0
			;[End Block]
		Case it_cap, it_scp268, it_fine268
			;[Block]
			I_268\Using = 0
			;[End Block]
		Case it_scp427
			;[Block]
			I_427\Using = False
			;[End Block]
		Case it_scp714, it_coarse714
			;[Block]
			I_714\Using = 0
			;[End Block]
	End Select
End Function

Function ClearSecondInv%(item.Items, From% = 0)
	Local i%
	
	For i = From To item\InvSlots - 1
		If item\SecondInv[i] <> Null Then RemoveItem(item\SecondInv[i]) : item\SecondInv[i] = Null
	Next
End Function

Function UpdateItems%()
	CatchErrors("UpdateItems()")
	
	Local i.Items, i2.Items, np.NPCs
	Local xTemp#, yTemp#, zTemp#
	Local Pick%, ed#
	Local HideDist# = PowTwo(Min(HideDistance, fog\FarDist * LightVolume * 1.2))
	Local PushDist# = HideDist * 0.04
	Local DeletedItem% = False
	Local RandomVal# = Rnd(-0.002, 0.002)
	
	opttimer\ItemsTimer = opttimer\ItemsTimer - fps\Factor[0]
	If opttimer\ItemsTimer <= 0.0
		For i.Items = Each Items
			If (Not i\Picked)
				i\Dist = EntityDistanceSquared(Camera, i\Collider)
				i\Nearby = (i\Dist < HideDist)
			EndIf
		Next
		opttimer\ItemsTimer = 35.0
	EndIf
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked)
			If i\Nearby
				If EntityHidden(i\Collider) Then ShowEntity(i\Collider)
				
				If i\Dist < 1.44
					If ClosestItem = Null Lor i\Dist < EntityDistanceSquared(Camera, ClosestItem\Collider)
						If EntityInView(i\OBJ, Camera) And EntityVisible(i\Collider, Camera) Then ClosestItem = i
					EndIf
				EndIf
				
				If EntityCollided(i\Collider, HIT_MAP)
					i\DropSpeed = 0.0
					AlignToVector(i\Collider, -i\TargetNX, -i\TargetNY, -i\TargetNZ, 3)
					TurnEntity(i\Collider, -90.0, 0, 0.0)
				Else
					If ShouldEntitiesFall
						EntityPickMode(me\Collider, 0)
						Pick = LinePick(EntityX(i\Collider), EntityY(i\Collider), EntityZ(i\Collider), 0.0, -3.0, 0.0)
						If Pick
							i\DropSpeed = Max(i\DropSpeed - 0.0004 * fps\Factor[0], -0.03)
							i\TargetNX = PickedNX()
							i\TargetNY = PickedNY()
							i\TargetNZ = PickedNZ()
							TranslateEntity(i\Collider, 0.0, i\DropSpeed * fps\Factor[0], 0.0)
						Else
							i\DropSpeed = 0.0
						EndIf
						EntityPickMode(me\Collider, 1)
					Else
						i\DropSpeed = 0.0
					EndIf
				EndIf
				
				If PlayerRoom\RoomTemplate\RoomID <> r_room2_storage
					If i\Dist < PushDist
						For i2.Items = Each Items
							If i <> i2 And (Not i2\Picked) And i2\Dist < PushDist
								xTemp = EntityX(i2\Collider, True) - EntityX(i\Collider, True)
								yTemp = EntityY(i2\Collider, True) - EntityY(i\Collider, True)
								zTemp = EntityZ(i2\Collider, True) - EntityZ(i\Collider, True)
								
								ed = PowTwo(xTemp) + PowTwo(zTemp)
								If ed < 0.07 And Abs(yTemp) < 0.25
									; ~ Items are too close together, push away
									Local PushVal# = 0.07 - ed
									
									xTemp = xTemp * PushVal
									zTemp = zTemp * PushVal
									
									If Abs(xTemp) + Abs(zTemp) < 0.001
										xTemp = xTemp + RandomVal
										zTemp = zTemp + RandomVal
									EndIf
									
									TranslateEntity(i2\Collider, xTemp, 0.0, zTemp)
									TranslateEntity(i\Collider, -xTemp, 0.0, -zTemp)
								EndIf
							EndIf
						Next
					EndIf
				EndIf
				If EntityY(i\Collider) < -60.0
					RemoveItem(i)
					DeletedItem = True
				EndIf
			Else
				If (Not EntityHidden(i\Collider)) Then HideEntity(i\Collider)
				i\DropSpeed = 0.0
			EndIf
		Else
			i\DropSpeed = 0.0
		EndIf
		
		If (Not DeletedItem)
			CatchErrors("Uncaught: UpdateItems(Item Name:" + Chr(34) + i\ItemTemplate\Name + Chr(34) + ")")
		Else
			CatchErrors("Uncaught: UpdateItems(Item doesn't exist anymore!)")
		EndIf
		DeletedItem = False
	Next
	
	If (Not InvOpen) And OtherOpen = Null
		If ClosestItem <> Null
			Select ClosestItem\ItemTemplate\ID
				Case it_scp2022pill
					;[Block]
					ed = Rnd(0.028, 0.032)
					ScaleSprite(ClosestItem\OBJ2, ed, ed)
					;[End Block]
				Case it_scp1123
					;[Block]
					If I_714\Using <> 2 And wi\HazmatSuit <> 4 And wi\GasMask <> 4
						me\BlurTimer = 800.0 - (200.0 * (I_714\Using = 1 Lor wi\HazmatSuit <> 0))
						me\CameraShake = 1.0 - (0.5 * (I_714\Using = 1 Lor wi\HazmatSuit <> 0))
						I_1123\SoundCHN = LoopSoundLocal(I_1123\Sound, I_1123\SoundCHN)
					EndIf
					;[End Block]
			End Select
			If mo\MouseHit1
				If ItemAmount < MaxItemAmount
					PickItem(ClosestItem)
				Else
					CreateMsg(GetLocalString("msg", "cantcarry"))
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function PickItem%(item.Items, PlayPickUpSound% = True)
	If MenuOpen Lor ConsoleOpen Lor I_294\Using Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated Then Return
	
	CatchErrors("PickItem()")
	
	Local n%, z%
	Local itt.ItemTemplates
	Local CanPickItem% = 1
	
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = Null
			Select item\ItemTemplate\ID
				Case it_scp1123
					;[Block]
					Use1123()
					If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit = 0 Then Return
					;[End Block]
				Case it_crystal005
					;[Block]
					If I_409\Timer = 0.0 And (Not I_427\Using)
						me\BlurTimer = Max(1000.0, me\BlurTimer)
						I_409\Timer = 0.001
					EndIf
					GiveAchievement("005")
					;[End Block]
				Case it_killbat
					;[Block]
					me\LightFlash = 1.0
					PlaySound_Strict(snd_I\LightSFX[Rand(0, 2)])
					msg\DeathMsg = Format(GetLocalString("death", "killbat"), SubjectName)
					Kill()
					;[End Block]
				Case it_scp148
					;[Block]
					GiveAchievement("148")
					;[End Block]
				Case it_keyomni
					;[Block]
					GiveAchievement("omni")
					;[End Block]
				Case it_scp005, it_coarse005
					;[Block]
					GiveAchievement("005")
					;[End Block]
				Case it_veryfinevest
					;[Block]
					CreateMsg(GetLocalString("msg", "vfvest"))
					Return
					;[End Block]
				Case it_corrvest
					;[Block]
					CreateMsg(GetLocalString("msg", "corrvest"))
					Return
					;[End Block]
				Case it_firstaid, it_finefirstaid, it_veryfinefirstaid, it_firstaid2
					;[Block]
					item\State = 0.0
					;[End Block]
				Case it_navulti
					;[Block]
					GiveAchievement("snav")
					;[End Block]
				Case it_vest, it_finevest
					;[Block]
					For z = 0 To MaxItemAmount - 1
						If Inventory(z) <> Null
							If Inventory(z)\ItemTemplate\ID = it_vest Lor Inventory(z)\ItemTemplate\ID = it_finevest
								CanPickItem = 0
							ElseIf Inventory(z)\ItemTemplate\ID = it_hazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_finehazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_veryfinehazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_hazmatsuit148
								CanPickItem = 2
							EndIf
						EndIf
					Next
					
					If CanPickItem = 0
						CreateMsg(GetLocalString("msg", "twovest"))
						Return
					ElseIf CanPickItem = 2
						CreateMsg(GetLocalString("msg", "vestsuit"))
						Return
					Else
						SelectedItem = item
					EndIf
					;[End Block]
				Case it_hazmatsuit, it_finehazmatsuit, it_veryfinehazmatsuit, it_hazmatsuit148
					;[Block]
					For z = 0 To MaxItemAmount - 1
						If Inventory(z) <> Null
							If Inventory(z)\ItemTemplate\ID = it_hazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_finehazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_veryfinehazmatsuit Lor Inventory(z)\ItemTemplate\ID = it_hazmatsuit148
								CanPickItem = 0
							ElseIf Inventory(z)\ItemTemplate\ID = it_vest Lor Inventory(z)\ItemTemplate\ID = it_finevest
								CanPickItem = 2
							EndIf
						EndIf
					Next
					
					If CanPickItem = 0
						CreateMsg(GetLocalString("msg", "twosuit"))
						Return
					ElseIf CanPickItem = 2
						CreateMsg(GetLocalString("msg", "vestsuit"))
						Return
					Else
						SelectedItem = item
					EndIf
					;[End Block]
				Case it_e_readerulti
					;[Block]
					For itt.ItemTemplates = Each ItemTemplates
						If itt\ID = it_paper
							Local i% = (Not (itt\Name = "Leaflet" Lor itt\Name = "Drawing" Lor itt\Name = "Blank Paper" Lor itt\Name = "Note from Maynard" Lor itt\Name = "SCP-085"))
							Local k%
							
							If i
								For k = 1 To PossibleEReaderPageAmount - 1
									If item\EReaderPage[k] = Null
										item\EReaderPage[k] = itt
										item\EReaderPageAmount = PossibleEReaderPageAmount - 1
										itt\Found = True
										Exit
									EndIf
								Next
							EndIf
						EndIf
					Next
					;[End Block]
				Case it_scp2022
					;[Block]
					If item\State = 0.0 Then item\State = Rand(2, 8)
					;[End Block]
			End Select
			
			If item\ItemTemplate\SoundID <> 66 And PlayPickUpSound Then PlaySound_Strict(snd_I\PickSFX[item\ItemTemplate\SoundID])
			item\Picked = True
			item\Dropped = -1
			
			item\ItemTemplate\Found = True
			If item\InvSlots > 0
				For z = 0 To item\InvSlots - 1
					If item\SecondInv[z] <> Null Then item\SecondInv[z]\ItemTemplate\Found = True
				Next
			EndIf
			ItemAmount = ItemAmount + 1
			
			Inventory(n) = item
			HideEntity(item\Collider)
			Exit
		EndIf
	Next
	If PlayPickUpSound
		me\SndVolume = Max(2.0, me\SndVolume)
		SetPlayerModelAnimation(PLAYER_ANIM_LEFT_PICK_UP + me\Crouch, item\Collider)
	EndIf
	
	CatchErrors("Uncaught: PickItem()")
End Function

Function DropItem%(item.Items, PlayDropSound% = True)
	CatchErrors("DropItem()")
	
	Local n%
	Local CameraYaw# = EntityYaw(Camera)
	
	If item\ItemTemplate\SoundID <> 66 And PlayDropSound Then PlaySound_Strict(snd_I\PickSFX[item\ItemTemplate\SoundID])
	
	item\Dropped = 1
	item\Nearby = True
	
	ShowEntity(item\Collider)
	PositionEntity(item\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
	RotateEntity(item\Collider, EntityPitch(Camera), CameraYaw + Rnd(-20.0, 20.0), 0.0)
	MoveEntity(item\Collider, 0.0, -0.1, 0.1)
	RotateEntity(item\Collider, 0.0, CameraYaw + Rnd(-110.0, 110.0), 0.0)
	ResetEntity(item\Collider)
	
	Local ITID% = item\ItemTemplate\ID
	
	Select item\ItemTemplate\ID
		Case it_hazmatsuit, it_finehazmatsuit, it_veryfinehazmatsuit, it_hazmatsuit148
			;[Block]
			SetAnimTime(item\OBJ, 4.0)
			;[End Block]
		Case it_clipboard, it_wallet, it_scp500
			;[Block]
			Local IsEmpty% = True
			
			For n = 0 To item\InvSlots - 1
				If item\SecondInv[n] <> Null
					IsEmpty = False
					Exit
				EndIf
			Next
			
			Local ID% = item\ItemTemplate\ID
			Local PillsAmount% = 0
			
			If ID = it_scp500 And (Not IsEmpty)
				For n = 0 To item\InvSlots - 1
					If item\SecondInv[n] <> Null Then PillsAmount = PillsAmount + 1
				Next
			EndIf
			SetAnimTime(item\OBJ, (IsEmpty * ((ID = it_clipboard) * 17.0 + (ID = it_wallet) * 2.0 + (ID = it_scp500) * 11.0)) + ((Not IsEmpty) * ((ID = it_clipboard) + (ID = it_wallet) * 4.0 + (ID = it_scp500) * (Max(0.0, 11.0 - PillsAmount)))))
			;[End Block]
		Case it_scp1123
			;[Block]
			Use1123()
			;[End Block]
	End Select
	
	item\Picked = False
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = item
			Inventory(n) = Null
			ItemAmount = ItemAmount - 1
			Exit
		EndIf
	Next
	If PlayDropSound Then me\SndVolume = Max(2.0, me\SndVolume)
	
	CatchErrors("Uncaught: DropItem()")
End Function

Function IsItemGoodFor1162ARC%(itt.ItemTemplates)
	Select itt\ID
		Case it_key0, it_key1, it_key2, it_key3
			;[Block]
			Return(True)
			;[End Block]
		Case it_mastercard, it_playcard, it_origami, it_electronics
			;[Block]
			Return(True)
			;[End Block]
		Case it_vest, it_finevest, it_gasmask
			;[Block]
			Return(True)
			;[End Block]
		Case it_radio, it_18vradio
			;[Block]
			Return(True)
			;[End Block]
		Case it_eyedrops, it_syringe, it_scp420j, it_cigarette, it_joint
			;[Block]
			Return(True)
			;[End Block]
		Default
			;[Block]
			If itt\ID <> it_paper
				Return(False)
			ElseIf Instr(itt\Name, "Leaflet") Lor Instr(itt\Name, "SCP-085") Lor Instr(itt\Name, "Blank") Lor Instr(itt\Name, "Drawing")
				Return(False)
			Else
				; ~ If the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				; ~ (Because those are items created recently, which D-9341 has most likely never seen)
				Return((Not Instr(itt\Name, "Note")) And (Not Instr(itt\Name, "Log")))
			EndIf
			;[End Block]
	End Select
End Function

Function IsItemInFocus%()
	Select SelectedItem\ItemTemplate\ID
		Case it_nav, it_nav300, it_nav310, it_navulti, it_paper, it_oldpaper, it_badge, it_badge2, it_scp1025, it_fine1025, it_e_reader, it_e_reader20, it_e_readerulti
			;[Block]
			Return(True)
			;[End Block]
	End Select
	Return(False)
End Function

Function CanUseItem%(CanUseWithEyewear% = False, CanUseWithGasMask% = False, CanUseWithHazmat% = False)
	If (Not CanUseWithGasMask) And (wi\GasMask > 0 Lor I_1499\Using > 0)
		CreateMsg(GetLocalString("msg", "mask.use"))
		SelectedItem = Null
		Return(False)
	ElseIf (Not CanUseWithEyewear) And (wi\NightVision > 0 Lor wi\SCRAMBLE > 0)
		CreateMsg(GetLocalString("msg", "gear.use"))
		SelectedItem = Null
		Return(False)
	ElseIf (Not CanUseWithHazmat) And wi\HazmatSuit > 0
		CreateMsg(GetLocalString("msg", "suit.use"))
		SelectedItem = Null
		Return(False)
	EndIf
	Return(True)
End Function

; ~ Maybe re-work?
Function PreventItemOverlapping%(GasMask% = False, NVG% = False, SCP1499% = False, Helmet% = False, SCRAMBLE% = False, Suit% = False, Cap% = False)
	If (Not GasMask) And wi\GasMask > 0
		CreateMsg(GetLocalString("msg", "mask.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCP1499) And I_1499\Using > 0
		CreateMsg(GetLocalString("msg", "1499.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not NVG) And wi\NightVision > 0
		CreateMsg(GetLocalString("msg", "goggle.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not Helmet) And wi\BallisticHelmet
		CreateMsg(GetLocalString("msg", "helmet.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCRAMBLE) And wi\SCRAMBLE > 0
		CreateMsg(GetLocalString("msg", "gear.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not Suit) And wi\HazmatSuit > 0
		CreateMsg(GetLocalString("msg", "suit.use.off"))
		SelectedItem = Null
		Return(True)
	ElseIf (Not Cap) And I_268\Using > 0
		CreateMsg(GetLocalString("msg", "cap.use.off"))
		SelectedItem = Null
		Return(True)
	EndIf
	Return(False)
End Function

Function IsDoubleItem%(Variable%, ID%)
	Local Message$
	
	If Variable > 0 And Variable <> ID
		Select Variable
			Case wi\GasMask
				;[Block]
				Message = GetLocalString("msg", "weartwo.gas")
				;[End Block]
			Case wi\NightVision, wi\SCRAMBLE
				;[Block]
				Message = GetLocalString("msg", "weartwo.nvg")
				;[End Block]
			Case I_268\Using, I_714\Using, I_1499\Using
				;[Block]
				Message = GetLocalString("msg", "weartwo.scps")
				;[End Block]
		End Select
		CreateMsg(Message)
		SelectedItem = Null
		Return(True)
	EndIf
	Return(False)
End Function

; ~ SCP-914 Constants
;[Block]
Const SETTING_ROUGH% = -2
Const SETTING_COARSE% = -1
Const SETTING_ONETOONE% = 0
Const SETTING_FINE% = 1
Const SETTING_VERY_FINE% = 2
;[End Block]

Function Use914%(item.Items, Setting%, x#, y#, z#)
	me\RefinedItems = me\RefinedItems + 1
	
	Local it.Items, it2.Items, de.Decals, n.NPCs, r.Rooms
	Local Remove% = True, i%, j%
	Local MakeDecal% = False
	
	Select item\ItemTemplate\ID
		Case it_gasmask, it_finegasmask, it_veryfinegasmask, it_gasmask148
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If Rand(4) = 1
						CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					Else
						CreateItem("Gas Mask", it_gasmask, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(75) = 1
						CreateItem("SCP-1499", it_scp1499, x, y, z)
					Else
						CreateItem("Fine Gas Mask", it_finegasmask, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(150) = 1
						CreateItem("SCP-1499", it_scp1499, x, y, z)
					Else
						CreateItem("Very Fine Gas Mask", it_veryfinegasmask, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1499, it_fine1499
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Gas Mask", it_gasmask, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine SCP-1499", it_fine1499, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					n.NPCs = CreateNPC(NPCType1499_1, x, y, z)
					n\State = 1.0 : n\State3 = 1.0
					n\SoundCHN = PlaySoundEx(LoadTempSound("SFX\SCP\1499\Triggered.ogg"), Camera, n\Collider, 20.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_nvg, it_veryfinenvg, it_finenvg
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("SCRAMBLE Gear", it_scramble, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine Night Vision Goggles", it_finenvg, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					it2.Items = CreateItem("Very Fine Night Vision Goggles", it_veryfinenvg, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_scramble, it_finescramble
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("Night Vision Goggles", it_nvg, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					it2.Items = CreateItem("Fine SCRAMBLE Gear", it_finescramble, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_helmet
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Ballistic Vest", it_vest, x, y, z)
					;[End Block]	
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Heavy Ballistic Vest", it_finevest, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_cap, it_scp268, it_fine268
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Newsboy Cap", it_cap, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Fine SCP-268", it_fine268, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_vest, it_finevest
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Corrosive Ballistic Vest", it_corrvest, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Ballistic Helmet", it_helmet, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Heavy Ballistic Vest", it_finevest, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Bulky Ballistic Vest", it_veryfinevest, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_hazmatsuit, it_finehazmatsuit, it_hazmatsuit148
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine Hazmat Suit", it_finehazmatsuit, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Very Fine Hazmat Suit", it_veryfinehazmatsuit, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinehazmatsuit
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_clipboard, it_wallet
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					ClearSecondInv(item, 0)
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					If item\InvSlots > 5
						item\InvSlots = item\InvSlots - 5
						ClearSecondInv(item, item\InvSlots)
					ElseIf item\InvSlots = 5
						item\InvSlots = 1
						ClearSecondInv(item, 1)
					Else
						MakeDecal = True
						ClearSecondInv(item, 0)
					EndIf
					Remove = False
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If item\InvSlots = 1
						item\InvSlots = 5
					Else
						item\InvSlots = Min(20, item\InvSlots + 5)
					EndIf
					Remove = False
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If item\InvSlots = 1
						item\InvSlots = 10
					Else
						item\InvSlots = Min(20, item\InvSlots + 10)
					EndIf
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case it_electronics
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked)
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								If it\ItemTemplate\ID = it_clipboard
									RemoveItem(it)
									it2.Items = CreateItem("E-Reader", it_e_reader, x, y, z)
									it2\State = Rnd(0.0, 100.0)
									Exit
								EndIf
							EndIf
						EndIf
					Next
					
					If it2 = Null
						Select Rand(3)
							Case 1
								;[Block]
								it2.Items = CreateItem("Radio Transceiver", it_radio, x, y, z)
								it2\State = Rnd(0.0, 100.0)
								;[End Block]
							Case 2
								;[Block]
								If Rand(3) = 1
									CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
								Else
									it2.Items = CreateItem("S-NAV Navigator", it_nav, x, y, z)
									it2\State = Rnd(0.0, 100.0)
								EndIf
								;[End Block]
							Case 3
								;[Block]
								it2.Items = CreateItem("Night Vision Goggles", it_nvg, x, y, z)
								it2\State = Rnd(0.0, 1000.0)
								;[End Block]
						End Select
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked)
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								If it\ItemTemplate\ID = it_clipboard
									RemoveItem(it)
									it2.Items = CreateItem("E-Reader", it_e_reader, x, y, z)
									it2\State = Rnd(0.0, 100.0)
									Exit
								EndIf
							EndIf
						EndIf
					Next
					
					If it2 = Null
						Select Rand(3)
							Case 1
								;[Block]
								If Rand(3) = 1
									CreateItem("Fine Radio Transceiver", it_fineradio, x, y, z)
								Else
									CreateItem("Very Fine Radio Transceiver", it_veryfineradio, x, y, z)
								EndIf
								;[End Block]
							Case 2
								;[Block]
								If Rand(2) = 1
									CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
								Else
									it2.Items = CreateItem("S-NAV 310 Navigator", it_nav310, x, y, z)
									it2\State = Rnd(0.0, 100.0)
								EndIf
								;[End Block]
							Case 3
								;[Block]
								Select Rand(4)
									Case 1
										;[Block]
										CreateItem("Fine Night Vision Goggles", it_finenvg, x, y, z)
										;[End Block]
									Case 2
										;[Block]
										it2.Items = CreateItem("Very Fine Night Vision Goggles", it_veryfinenvg, x, y, z)
										it2\State = Rnd(0.0, 1000.0)
										;[End Block]
									Case 3
										;[Block]
										it2.Items = CreateItem("SCRAMBLE Gear", it_scramble, x, y, z)
										it2\State = Rnd(0.0, 1000.0)
										;[End Block]
									Case 4
										;[Block]
										CreateItem("Fine SCRAMBLE Gear", it_finescramble, x, y, z)
										;[End Block]
								End Select
								;[End Block]
						End Select
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp148
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("SCP-148 Ingot", it_scp148ingot, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE, SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case it_scp148ingot
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					CreateItem("SCP-148 Ingot", it_scp148ingot, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE, SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked)
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								Select it\ItemTemplate\ID
									Case it_gasmask, it_finegasmask, it_veryfinegasmask
										;[Block]
										RemoveItem(it)
										it2.Items = CreateItem("Heavy Gas Mask", it_gasmask148, x, y, z)
										Exit
										;[End Block]
									Case it_hazmatsuit, it_finehazmatsuit, it_veryfinehazmatsuit
										;[Block]
										RemoveItem(it)
										it2.Items = CreateItem("Heavy Hazmat Suit", it_hazmatsuit148, x, y, z)
										Exit
										;[End Block]
								End Select
							EndIf
						EndIf
					Next
					
					If it2 = Null Then it2.Items = CreateItem("Metal Panel", it_scp148, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_hand, it_hand2, it_hand3
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_hand2
						CreateItem("White Severed Hand", it_hand, x, y, z)
					Else
						CreateItem("Black Severed Hand", it_hand2, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					n.NPCs = CreateNPC(NPCType008_1_Surgeon, x, y, z)
					n\State = 3.0
					;[End Block]
			End Select
			;[End Block]
		Case it_firstaid, it_firstaid2, it_finefirstaid, it_veryfinefirstaid
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_firstaid
						CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					Else
						CreateItem("First Aid Kit", it_firstaid, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE
					;[Block]
					it2.Items = CreateItem("Compact First Aid Kit", it_finefirstaid, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					it2.Items = CreateItem("Strange Bottle", it_veryfinefirstaid, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_key0, it_key1, it_key2, it_key3, it_key4, it_key5, it_key6
			;[Block]
			Local Level% = item\ItemTemplate\ID
			Local LevelName%
			
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					If Level = it_key0
						MakeDecal = True
					ElseIf Level = it_key6
						CreateItem("Level 0 Key Card", it_key0, x, y, z)
					Else
						Select Level
							Case it_key1
								;[Block]
								LevelName = 1
								;[End Block]
							Case it_key2
								;[Block]
								LevelName = 2
								;[End Block]
							Case it_key3
								;[Block]
								LevelName = 3
								;[End Block]
							Case it_key4
								;[Block]
								LevelName = 4
								;[End Block]
							Case it_key5
								;[Block]
								LevelName = 5
								;[End Block]
						End Select
						CreateItem("Level " + (LevelName - 1) + " Key Card", Level - 1, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Playing Card", it_playcard, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					Select Level
						Case it_key0
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case DIFFICULTY_EASY
									;[Block]
									CreateItem("Level 1 Key Card", it_key1, x, y, z)
									;[End Block]
								Case DIFFICULTY_NORMAL
									;[Block]
									If Rand(6) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_HARD
									;[Block]
									If Rand(5) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_EXTREME
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key1
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case DIFFICULTY_EASY
									;[Block]
									CreateItem("Level 2 Key Card", it_key2, x, y, z)
									;[End Block]
								Case DIFFICULTY_NORMAL
									;[Block]
									If Rand(5) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_HARD
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_EXTREME
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key2
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case DIFFICULTY_EASY
									;[Block]
									CreateItem("Level 3 Key Card", it_key3, x, y, z)
									;[End Block]
								Case DIFFICULTY_NORMAL
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_HARD
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_EXTREME
									;[Block]
									If Rand(2) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key3
							;[Block]
							If Rand(16 + (8 * SelectedDifficulty\OtherFactors)) = 1
								CreateItem("Level 4 Key Card", it_key4, x, y, z)
							Else
								CreateItem("Playing Card", it_playcard, x, y, z)
							EndIf
							;[End Block]
						Case it_key4
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case DIFFICULTY_EASY
									;[Block]
									CreateItem("Level 5 Key Card", it_key5, x, y, z)
									;[End Block]
								Case DIFFICULTY_NORMAL
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_HARD
									;[Block]
									If Rand(2) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
								Case DIFFICULTY_EXTREME
									;[Block]
									If Rand(3) > 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key5
							;[Block]
							Local CurrAchvAmount% = Max(((S2IMapSize(AchievementsIndex) - 3) - (S2IMapSize(UnlockedAchievements) - 1) - S2IMapContains(UnlockedAchievements, "apollyon")) * (2 + SelectedDifficulty\OtherFactors), 0)
							
							If Rand(0, CurrAchvAmount) = 0
								CreateItem("Key Card Omni", it_keyomni, x, y, z)
							ElseIf Rand(16 + (8 * SelectedDifficulty\OtherFactors)) = 1
								CreateItem("Level 6 Key Card", it_key6, x, y, z)
							Else
								If Rand(15) = 1
									it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
									it2\State = 1000
								Else
									it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
									it2\State = Rand(0, 6)
								EndIf
							EndIf
							;[End Block]
						Case it_key6
							;[Block]
							If Rand(8 + (4 * SelectedDifficulty\OtherFactors)) = 1
								CreateItem("Key Card Omni", it_keyomni, x, y, z)
							Else
								If Rand(5) = 1
									it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
									it2\State = 1000
								Else
									it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
									it2\State = Rand(0, 6)
								EndIf
							EndIf
							;[End Block]
					End Select
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CurrAchvAmount = Max(((S2IMapSize(AchievementsIndex) - 3) - (S2IMapSize(UnlockedAchievements) - 1) - S2IMapContains(UnlockedAchievements, "apollyon")) * (5 + SelectedDifficulty\OtherFactors), 0)
					If Rand(0, CurrAchvAmount) = 0
						CreateItem("Key Card Omni", it_keyomni, x, y, z)
					ElseIf Rand(32 + (8 * SelectedDifficulty\OtherFactors)) = 1
						CreateItem("Level 6 Key Card", it_key6, x, y, z)
					Else
						it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
						it2\State = Rand(0, 6)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_keyomni
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If Rand(2) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						CreateItem("Playing Card", it_playcard, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					If Rand(6 + (3 * SelectedDifficulty\OtherFactors)) = 1
						CreateItem("Level 6 Key Card", it_key6, x, y, z)
					Else
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_playcard, it_coin, it_25ct
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Level 0 Key Card", it_key0, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Level 1 Key Card", it_key1, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Level 2 Key Card", it_key2, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_mastercard
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Quarter", it_25ct, x, y, z)
					
					If Rand(2) = 1 Then CreateItem("Quarter", it_25ct, x, y, z)
					
					If Rand(3) = 1 Then CreateItem("Quarter", it_25ct, x, y, z)
					
					If Rand(4) = 1 Then CreateItem("Quarter", it_25ct, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Level 0 Key Card", it_key0, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(35) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						CreateItem("Level 1 Key Card", it_key1, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(50) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						CreateItem("Level 2 Key Card", it_key2, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_mastercard_golden
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					For i = 0 To 20
						CreateItem("Quarter", it_25ct, x, y, z)
					Next
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Level 2 Key Card", it_key2, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(35) = 1
						CreateItem("Level 4 Key Card", it_key4, x, y, z)
					Else
						CreateItem("Level 3 Key Card", it_key3, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(45) = 1
						CreateItem("Level 5 Key Card", it_key5, x, y, z)
					Else
						CreateItem("Level 3 Key Card", it_key3, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp005, it_coarse005, it_crystal005
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Coarse SCP-005", it_coarse005, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If Rand(8) = 1
						CreateItem("Level 5 Key Card", it_key5, x, y, z)
						
						CreateItem("White Severed Hand", it_hand, x, y, z)
						
						CreateItem("Black Severed Hand", it_hand2, x, y, z)
						
						CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
						
						CreateItem("White Key", it_key_white, x, y, z)
						
						CreateItem("Yellow Key", it_key_yellow, x, y, z)
					Else
						CreateItem("SCP-005", it_scp005, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					If item\ItemTemplate\ID = it_crystal005
						CreateItem("SCP-005", it_scp005, x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp860, it_fine860
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID <> it_scp860
						CreateItem("SCP-860", it_scp860, x, y, z)
					Else
						CreateItem("White Key", it_key_white, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(8 + (4 * SelectedDifficulty\OtherFactors)) = 1
						CreateItem("Fine SCP-860", it_fine860, x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(12 + (6 * SelectedDifficulty\OtherFactors)) = 1
						CreateItem("Fine SCP-860", it_fine860, x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_lostkey, it_key_white, it_key_yellow
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_key_white
						CreateItem("Yellow Key", it_key_yellow, x, y, z)
					Else
						CreateItem("White Key", it_key_white, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("Level 2 Key Card", it_key2, x, y, z)
					Else
						If item\ItemTemplate\ID = it_key_white
							CreateItem("Yellow Key", it_key_yellow, x, y, z)
						Else
							CreateItem("White Key", it_key_white, x, y, z)
						EndIf
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(6) = 1
						CreateItem("Level 3 Key Card", it_key3, x, y, z)
					Else
						If item\ItemTemplate\ID = it_key_white
							CreateItem("Yellow Key", it_key_yellow, x, y, z)
						Else
							CreateItem("White Key", it_key_white, x, y, z)
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_radio, it_18vradio, it_fineradio, it_veryfineradio
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("18V Radio Transceiver", it_18vradio, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine Radio Transceiver", it_fineradio, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Very Fine Radio Transceiver", it_veryfineradio, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_nav, it_nav300, it_nav310, it_navulti
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("S-NAV Navigator", it_nav, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					it2.Items = CreateItem("S-NAV 310 Navigator", it_nav310, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					Local RoomsAmount% = 0, RoomsFound% = 0
					
					For r.Rooms = Each Rooms
						Local RID% = r\RoomTemplate\RoomID
						
						If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499
							RoomsAmount = RoomsAmount + 1
							RoomsFound = RoomsFound + r\Found
						EndIf
					Next
					If Rand(Max((RoomsAmount - (RoomsFound * 2)) * (1 + SelectedDifficulty\OtherFactors), 1)) = 1
						CreateItem("S-NAV Navigator Ultimate", it_navulti, x, y, z)
					Else
						CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp513, it_fine513
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType513_1 Then RemoveNPC(n)
					Next
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Fine SCP-513", it_fine513, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp420j, it_cigarette, it_joint, it_joint_smelly
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Cigarette", it_cigarette, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Joint", it_joint, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Smelly Joint", it_joint_smelly, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp714, it_coarse714, it_fine714, it_ring
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Coarse SCP-714", it_coarse714, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_scp714
						CreateItem("Green Jade Ring", it_ring, x, y, z)
					Else
						CreateItem("SCP-714", it_scp714, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Fine SCP-714", it_fine714, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_coarsebat
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("18V Battery", it_finebat, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_bat
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("18V Battery", it_finebat, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(5) = 1
						CreateItem("999V Battery", it_veryfinebat, x, y, z)
					Else
						CreateItem("Strange Battery", it_killbat, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_finebat
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("999V Battery", it_veryfinebat, x, y, z)
					Else
						CreateItem("Strange Battery", it_killbat, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinebat
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE, SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Strange Battery", it_killbat, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_eyedrops, it_eyedrops2, it_fineeyedrops, it_veryfineeyedrops
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If Rand(2) = 1
						CreateItem("ReVision Eyedrops", it_eyedrops, x, y, z)
					Else
						CreateItem("RedVision Eyedrops", it_eyedrops2, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine Eyedrops", it_fineeyedrops, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Very Fine Eyedrops", it_veryfineeyedrops, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_syringe
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Compact First Aid Kit", it_finefirstaid, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Fine Syringe", it_finesyringe, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("Very Fine Syringe", it_veryfinesyringe, x, y, z)
					Else
						CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_finesyringe
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("First Aid Kit", it_firstaid, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("Very Fine Syringe", it_veryfinesyringe, x, y, z)
					Else
						CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinesyringe
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE, SETTING_ONETOONE
					;[Block]
					CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(2) = 1
						n.NPCs = CreateNPC(NPCType008_1_Surgeon, x, y, z)
						n\State = 2.0
					Else
						CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_syringeinf
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					n.NPCs = CreateNPC(NPCType008_1_Surgeon, x, y, z)
					n\State = 2.0
					;[End Block]	
				Case SETTING_FINE
					;[Block]
					CreateItem("Syringe", it_syringe, x, y, z)
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(4) = 1
						CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					Else
						CreateItem("Fine Syringe", it_finesyringe, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp500pill, it_scp500pilldeath, it_pill
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Pill", it_pill, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					Local NO427Spawn% = False
					
					For it.Items = Each Items
						If it\ItemTemplate\ID = it_scp427
							NO427Spawn = True
							Exit
						EndIf
					Next
					If NO427Spawn
						CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					Else
						CreateItem("SCP-427", it_scp427, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					Else
						CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp500
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					
					If Rand(2) = 1 Then CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					
					If Rand(3) = 1 Then CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					
					If Rand(4) = 1 Then CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					NO427Spawn = False
					
					For it.Items = Each Items
						If it\ItemTemplate\ID = it_scp427
							NO427Spawn = True
							Exit
						EndIf
					Next
					If NO427Spawn
						If Rand(2) = 1
							CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							
							If Rand(2) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							
							If Rand(3) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							
							If Rand(4) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						Else
							it2.Items = CreateItem("SCP-2022", it_scp2022, x, y, z)
							it2\State = Rand(6)
						EndIf
					Else
						CreateItem("SCP-427", it_scp427, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("SCP-2022", it_scp2022, x, y, z)
						it2\State = Rand(6)
					Else
						CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(2) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(3) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(4) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp2022
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					
					If Rand(2) = 1 Then CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					
					If Rand(3) = 1 Then CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					
					If Rand(4) = 1 Then CreateItem("SCP-2022-01", it_scp2022pill, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(2) = 1
						CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(2) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(3) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(4) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					Else
						it2.Items = CreateItem("SCP-500", it_scp500, x, y, z)
						j = Rand(9)
						For i = 0 To it2\InvSlots - 1
							it.Items = CreateItem("SCP-500-01", it_scp500pill, 0.0, 0.0, 0.0)
							it\Picked = True : it\Dropped = -1 : it2\SecondInv[i] = it
							HideEntity(it\Collider)
							If i = j Then Exit
						Next
						SetAnimTime(it2\OBJ, Max(0.0, 11.0 - j))
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("SCP-500", it_scp500, x, y, z)
						j = Rand(9)
						For i = 0 To it2\InvSlots - 1
							it.Items = CreateItem("SCP-500-01", it_scp500pill, 0.0, 0.0, 0.0)
							it\Picked = True : it\Dropped = -1 : it2\SecondInv[i] = it
							HideEntity(it\Collider)
							If i = j Then Exit
						Next
						SetAnimTime(it2\OBJ, Max(0.0, 11.0 - j))
					Else
						CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(2) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(3) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(4) = 1 Then CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp2022pill
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Pill", it_pill, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(3) = 1
						CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					Else
						CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(10) = 1
						it2.Items = CreateItem("SCP-2022", it_scp2022, x, y, z)
						it2\State = Rand(6)
					Else
						If Rand(3) = 1
							CreateItem("SCP-500-01", it_scp500pill, x, y, z)
						Else
							CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_cup
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					it2.Items = CreateItem("Cup", it_cup, x, y, z, 255.0 - item\R, 255.0 - item\G, 255.0 - item\B)
					it2\Name = item\Name
					it2\DisplayName = item\DisplayName
					it2\State = item\State
					;[End Block]
				Case SETTING_FINE
					;[Block]
					it2.Items = CreateItem("Cup", it_cup, x, y, z, Min(item\R * Rnd(0.9, 1.1), 255.0), Min(item\G * Rnd(0.9, 1.1), 255.0), Min(item\B * Rnd(0.9, 1.1), 255.0))
					it2\Name = item\Name
					it2\DisplayName = item\DisplayName
					it2\State = 2.0
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					it2.Items = CreateItem("Cup", it_cup, x, y, z, Min(item\R * Rnd(0.5, 1.5), 255.0), Min(item\G * Rnd(0.5, 1.5), 255.0), Min(item\B * Rnd(0.5, 1.5), 255.0))
					it2\Name = item\Name
					it2\DisplayName = item\DisplayName
					it2\State = 5.0
					If Rand(5) = 1 Then me\ExplosionTimer = 135.0
					;[End Block]
			End Select
			;[End Block]
		Case it_origami
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					If Rand(10) = 1
						CreateItem("SCP-085", it_paper, x, y, z)
					Else
						CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_paper, it_oldpaper
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Origami", it_origami, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1025, it_fine1025, it_book
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					If item\ItemTemplate\ID <> it_scp1025
						CreateItem("SCP-1025", it_scp1025, x, y, z)
					Else
						CreateItem("Book", it_book, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Fine SCP-1025", it_fine1025, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1123
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Horror.ogg"))
					de.Decals = CreateDecal(DECAL_BLOOD_2, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, Rnd(0.3, 0.4), Rnd(0.8, 1.0), 1)
					EntityParent(de\OBJ, PlayerRoom\OBJ)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_badge, it_badge2
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(4) = 1
						Select item\ItemTemplate\Name
							Case "Jim Gonazales' Badge"
								;[Block]
								CreateItem("Level 0 Key Card", it_key0, x, y, z)
								;[End Block]
							Case "Emily Ross' Badge"
								;[Block]
								CreateItem("Level 2 Key Card", it_key2, x, y, z)
								;[End Block]
							Case "Asav Harn's Badge"
								;[Block]
								CreateItem("Level 3 Key Card", it_key3, x, y, z)
								;[End Block]
							Case "Victor Rosewood's Badge"
								;[Block]
								CreateItem("Level 5 Key Card", it_key5, x, y, z)
								;[End Block]
							Default
								;[Block]
								CreateItem("Level 4 Key Card", it_key4, x, y, z)
								;[End Block]
						End Select
					Else
						CreateItem("Wallet", it_wallet, x, y, z)
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					CreateItem("Clipboard", it_clipboard, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_e_reader, it_e_reader20, it_e_readerulti
			;[Block]
			Select Setting
				Case SETTING_ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_COARSE
					;[Block]
					CreateItem("Clipboard", it_clipboard, x, y, z)
					;[End Block]
				Case SETTING_ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case SETTING_FINE
					;[Block]
					If Rand(5) = 1
						CreateItem("E-Reader 20", it_e_reader20, x, y, z)
					Else
						it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
						it2\InvSlots = 15
					EndIf
					;[End Block]
				Case SETTING_VERY_FINE
					;[Block]
					If Rand(15) = 1
						CreateItem("E-Reader Ultimate", it_e_readerulti, x, y, z)
					Else
						it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
						it2\InvSlots = 20
					EndIf
					;[End Block]
			End Select
		Default
			;[Block]
			Select Setting
				Case SETTING_ROUGH, SETTING_COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case SETTING_ONETOONE, SETTING_FINE, SETTING_VERY_FINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
	End Select
	
	If MakeDecal
		de.Decals = CreateDecal(DECAL_CORROSIVE_1, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, Rnd(0.3, 0.8), Rnd(0.8, 1.0), 1)
		EntityParent(de\OBJ, PlayerRoom\OBJ)
	EndIf
	If Remove
		RemoveItem(item)
	Else
		PositionEntity(item\Collider, x, y, z)
		ResetEntity(item\Collider)
	EndIf
End Function

; ~ Made a function for SCP-1123 so we don't have to use duplicate code (since picking it up and using it does the same thing)
Function Use1123%()
	; ~ Temp:
	; ~		False: Stay alive
	; ~		True: Die
	
	Local e.Events
	Local Temp%
	
	If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit = 0
		me\LightFlash = 3.0
		PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
		
		Temp = True
		If skull_event <> Null
			If PlayerRoom = skull_event\room
				If skull_event\EventState < 1.
					skull_event\EventState = 1.0
					Temp = False
				EndIf
			EndIf
		EndIf
	Else
		CreateMsg(GetLocalString("msg", "skull.nothappend"))
	EndIf
	
	If Temp
		msg\DeathMsg = Format(GetLocalString("death", "1123"), SubjectName)
		Kill()
	EndIf
End Function

; ~ Key Items Constants
;[Block]
Const KEY_MISC% = 0

Const KEY_CARD_6% = 1
Const KEY_CARD_0% = 2
Const KEY_CARD_1% = 3
Const KEY_CARD_2% = 4
Const KEY_CARD_3% = 5
Const KEY_CARD_4% = 6
Const KEY_CARD_5% = 7
Const KEY_CARD_OMNI% = 8

Const KEY_005% = 9

Const KEY_HAND_WHITE% = -1
Const KEY_HAND_BLACK% = -2
Const KEY_HAND_YELLOW% = -3

Const KEY_860% = -4
Const KEY_KEY% = -5
Const KEY_KEY2% = -6
Const KEY_LOST_KEY% = -66
;[End Block]

; ~ Only for "UseDoor" function
Function GetUsingItem%(item.Items)
	Select item\ItemTemplate\ID
		Case it_key6
			;[Block]
			Return(KEY_CARD_6)
			;[End Block]
		Case it_key0
			;[Block]
			Return(KEY_CARD_0)
			;[End Block]
		Case it_key1
			;[Block]
			Return(KEY_CARD_1)
			;[End Block]
		Case it_key2
			;[Block]
			Return(KEY_CARD_2)
			;[End Block]
		Case it_key3
			;[Block]
			Return(KEY_CARD_3)
			;[End Block]
		Case it_key4
			;[Block]
			Return(KEY_CARD_4)
			;[End Block]
		Case it_key5
			;[Block]
			Return(KEY_CARD_5)
			;[End Block]
		Case it_keyomni
			;[Block]
			Return(KEY_CARD_OMNI)
			;[End Block]
		Case it_scp005, it_coarse005, it_crystal005
			;[Block]
			Return(KEY_005)
			;[End Block]
		Case it_hand
			;[Block]
			Return(KEY_HAND_WHITE)
			;[End Block]
		Case it_hand2
			;[Block]
			Return(KEY_HAND_BLACK)
			;[End Block]
		Case it_hand3
			;[Block]
			Return(KEY_HAND_YELLOW)
			;[End Block]
		Case it_scp860, it_fine860
			;[Block]
			Return(KEY_860)
			;[End Block]
		Case it_key_yellow
			;[Block]
			Return(KEY_KEY)
			;[End Block]
		Case it_key_white
			;[Block]
			Return(KEY_KEY2)
			;[End Block]
		Case it_lostkey
			;[Block]
			Return(KEY_LOST_KEY)
			;[End Block]
		Default
			;[Block]
			Return(KEY_MISC)
			;[End Block]
	End Select
End Function

Function CreateRandomBattery.Items(x#, y#, z#)
	Local BatteryName$, BatteryID%
	Local BatteryChance% = 10 + (4 * SelectedDifficulty\OtherFactors)
	Local RandomChance% = Rand(BatteryChance)
	
	If RandomChance > 0 And RandomChance <= 5
		BatteryName = "9V Battery"
		BatteryID = it_bat
	ElseIf RandomChance > 5 And RandomChance < BatteryChance - 1
		BatteryName = "4.5V Battery"
		BatteryID = it_coarsebat
	Else
		BatteryName = "18V Battery"
		BatteryID = it_finebat
	EndIf
	Return(CreateItem(BatteryName, BatteryID, x, y, z))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS