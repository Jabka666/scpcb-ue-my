Global ItemAmount%, MaxItemAmount%

Global LastItemID%

Type ItemTemplates
	Field DisplayName$
	Field Name$
	Field ID%
	Field SoundID%
	Field Found%
	Field OBJ%, OBJPath$
	Field InvImg%, InvImg2%, InvImgPath$
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
Const it_burntbadge% = 4
Const it_harnbadge% = 5
Const it_oldbadge% = 6

Const it_ticket% = 7
;[End Block]

; ~ [SCPs AND VARIATIONS]
;[Block]
Const it_scp005% = 8
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

Const it_scp714% = 24
Const it_coarse714% = 25
Const it_fine714% = 26
Const it_ring% = 27

Const it_scp860% = 28
Const it_fine860% = 29

Const it_scp1025% = 30
Const it_fine1025% = 31
Const it_book% = 32

Const it_scp1123% = 33

Const it_scp1499% = 34
Const it_fine1499% = 35
;[End Block]

; ~ [MISC ITEMS]
;[Block]
Const it_helmet% = 36

Const it_vest% = 37
Const it_corrvest% = 38
Const it_finevest% = 39
Const it_veryfinevest% = 40

Const it_cup% = 41
Const it_emptycup% = 42

Const it_clipboard% = 43
Const it_wallet% = 44

Const it_electronics% = 45

Const it_eyedrops% = 46
Const it_eyedrops2% = 47
Const it_fineeyedrops% = 48
Const it_veryfineeyedrops% = 49

Const it_firstaid% = 50
Const it_firstaid2% = 51
Const it_finefirstaid% = 52
Const it_veryfinefirstaid% = 53

Const it_gasmask% = 54
Const it_finegasmask% = 55
Const it_veryfinegasmask% = 56
Const it_gasmask148% = 57

Const it_hazmatsuit% = 58
Const it_finehazmatsuit% = 59
Const it_veryfinehazmatsuit% = 60
Const it_hazmatsuit148% = 61

Const it_nvg% = 62
Const it_finenvg% = 63
Const it_veryfinenvg% = 64
Const it_scramble% = 65
Const it_finescramble% = 66

Const it_radio% = 67
Const it_18vradio% = 68
Const it_fineradio% = 69
Const it_veryfineradio% = 70

Const it_nav% = 71
Const it_nav300% = 72
Const it_nav310% = 73
Const it_navulti% = 74

Const it_e_reader% = 75
Const it_e_reader20% = 76
Const it_e_reader30% = 77

Const it_bat% = 78
Const it_coarsebat% = 79
Const it_finebat% = 80
Const it_veryfinebat% = 81
Const it_killbat% = 82

Const it_syringe% = 83
Const it_finesyringe% = 84
Const it_veryfinesyringe% = 85
Const it_syringeinf% = 86
;[End Block]

; ~ [KEYCARDS, HANDS, KEYS, CARDS, COINS]
;[Block]
Const it_key0% = 87, it_key0_bloody% = 88
Const it_key1% = 89, it_key1_bloody% = 90
Const it_key2% = 91
Const it_key3% = 92, it_key3_bloody% = 93
Const it_key4% = 94
Const it_key5% = 95
Const it_key6% = 96
Const it_keyomni% = 97

Const it_mastercard% = 98
Const it_mastercard_golden% = 99
Const it_playcard% = 100

Const it_hand% = 101
Const it_hand2% = 102
Const it_hand3% = 103

Const it_key_yellow% = 104
Const it_key_white% = 105
Const it_lostkey% = 106

Const it_25ct% = 107
Const it_coin% = 108

Const it_pizza% = 109
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
			If Left(TexturePath, ItemHUDTexturePathLen) = ItemHUDTexturePath
				Texture = GetRescaledTexture(TexturePath, TexFlags, 256, 256)
			Else
				Texture = LoadTexture_Strict(TexturePath, TexFlags)
			EndIf
			it\TexPath = TexturePath
			EntityTexture(it\OBJ, Texture)
			it\Tex = Texture
		EndIf
	EndIf
	
	ScaleEntity(it\OBJ, Scale, Scale, Scale, True)
	it\Scale = Scale
	
	; ~ If another item shares the same inv icon, copy it
	InvImgPath = ItemINVIconPath + InvImgPath
	For it2.ItemTemplates = Each ItemTemplates
		If it2\InvImgPath = InvImgPath And it2\InvImg <> 0
			it\InvImg = it2\InvImg
			If it2\InvImg2 <> 0 Then it\InvImg2 = it2\InvImg2
			Exit
		EndIf
	Next
	If it\InvImg = 0
		it\InvImg = ScaleImageEx(LoadImage_Strict(InvImgPath), MenuScale, MenuScale)
		it\InvImgPath = InvImgPath
	EndIf
	
	If InvImgPath2 <> ""
		If it\InvImg2 = 0
			InvImgPath2 = ItemINVIconPath + InvImgPath2
			it\InvImg2 = ScaleImageEx(LoadImage_Strict(InvImgPath2), MenuScale, MenuScale)
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
	Select Rand(22)
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
			Return("012")
			;[End Block]
		Case 4
			;[Block]
			Return("049")
			;[End Block]
		Case 5
			;[Block]
			Return("066")
			;[End Block]
		Case 6
			;[Block]
			Return("096")
			;[End Block]
		Case 7
			;[Block]
			Return("106")
			;[End Block]
		Case 8
			;[Block]
			Return("173")
			;[End Block]
		Case 9
			;[Block]
			Return("205")
			;[End Block]
		Case 10
			;[Block]
			Return("409")
			;[End Block]
		Case 11
			;[Block]
			Return("513")
			;[End Block]
		Case 12
			;[Block]
			Return("682")
			;[End Block]
		Case 13
			;[Block]
			Return("714")
			;[End Block]
		Case 14
			;[Block]
			Return("860")
			;[End Block]
		Case 15
			;[Block]
			Return("860-1")
			;[End Block]
		Case 16
			;[Block]
			Return("895")
			;[End Block]
		Case 17
			;[Block]
			Return("939")
			;[End Block]
		Case 18
			;[Block]
			Return("966")
			;[End Block]
		Case 19
			;[Block]
			Return("970")
			;[End Block]
		Case 20
			;[Block]
			Return("1048")
			;[End Block]
		Case 21
			;[Block]
			Return("1162-ARC")
			;[End Block]
		Case 22
			;[Block]
			Return("1499")
			;[End Block]
	End Select
End Function

Const TotalSCPDocumentsAmount% = 40

Function GetEReaderDocument$(State%)
	Select State
		Case 0
			;[Block]
			Return("")
			;[End Block]
		Case 1
			;[Block]
			Return("doc_OBJC")
			;[End Block]
		Case 2
			;[Block]
			Return("doc_SCL")
			;[End Block]
		Case 3
			;[Block]
			Return("doc_O5(2)")
			;[End Block]
		Case 4
			;[Block]
			Return("doc_MTF")
			;[End Block]
		Case 5
			;[Block]
			Return("doc_MSP")
			;[End Block]
		Case 6
			;[Block]
			Return("doc_NDP")
			;[End Block]
		Case 7
			;[Block]
			Return("doc_005")
			;[End Block]
		Case 8
			;[Block]
			Return("doc_008")
			;[End Block]
		Case 9
			;[Block]
			Return("doc_012")
			;[End Block]
		Case 10
			;[Block]
			If I_035\Sad = 0
				Return("doc_035_smile")
			Else
				Return("doc_035_sad")
			EndIf
			;[End Block]
		Case 11
			;[Block]
			Return("doc_049")
			;[End Block]
		Case 12
			;[Block]
			Return("doc_066")
			;[End Block]
		Case 13
			;[Block]
			Return("doc_IR_066")
			;[End Block]
		Case 14
			;[Block]
			Return("doc_079")
			;[End Block]
		Case 15
			;[Block]
			Return("doc_093_rm")
			;[End Block]
		Case 16
			;[Block]
			Return("doc_096")
			;[End Block]
		Case 17
			;[Block]
			Return("doc_106")
			;[End Block]
		Case 18
			;[Block]
			Return("doc_IR_106")
			;[End Block]
		Case 19
			;[Block]
			Return("doc_RP")
			;[End Block]
		Case 20
			;[Block]
			Return("doc_173")
			;[End Block]
		Case 21
			;[Block]
			Return("doc_205")
			;[End Block]
		Case 22
			;[Block]
			Return("doc_372")
			;[End Block]
		Case 23
			;[Block]
			Return("doc_409")
			;[End Block]
		Case 24
			;[Block]
			Return("doc_427")
			;[End Block]
		Case 25
			;[Block]
			Return("doc_500")
			;[End Block]
		Case 26
			;[Block]
			Return("doc_513")
			;[End Block]
		Case 27
			;[Block]
			Return("doc_682")
			;[End Block]
		Case 28
			;[Block]
			Return("doc_714")
			;[End Block]
		Case 29
			;[Block]
			Return("doc_860")
			;[End Block]
		Case 30
			;[Block]
			Return("doc_860_1")
			;[End Block]
		Case 31
			;[Block]
			Return("doc_895")
			;[End Block]
		Case 32
			;[Block]
			Return("doc_939")
			;[End Block]
		Case 33
			;[Block]
			Return("doc_966")
			;[End Block]
		Case 34
			;[Block]
			Return("doc_970")
			;[End Block]
		Case 35
			;[Block]
			Return("doc_1025")
			;[End Block]
		Case 36
			;[Block]
			Return("doc_1048")
			;[End Block]
		Case 37
			;[Block]
			Return("doc_IR_1048_a")
			;[End Block]
		Case 38
			;[Block]
			Return("doc_1123")
			;[End Block]
		Case 39
			;[Block]
			Return("doc_1162_ARC")
			;[End Block]
		Case 40
			;[Block]
			Return("doc_1499")
			;[End Block]
	End Select
End Function

Type Items
	Field DisplayName$
	Field Name$
	Field Collider%, OBJ%
	Field ItemTemplate.ItemTemplates
	Field DropSpeed#
	Field R%, G%, B%, Alpha#
	Field Dist#, DistTimer#
	Field State#, State2#, State3#
	Field Picked%, Dropped%
	Field InvImg%
	Field SecondInv.Items[20]
	Field ID%
	Field InvSlots%
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
			EntityPickMode(i\Collider, 1, False)
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
	
	i\Dist = EntityDistanceSquared(me\Collider, i\Collider)
	i\DropSpeed = 0.0
	
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
		Case it_clipboard
			;[Block]
			If InvSlots = 0
				InvSlots = 10
				SetAnimTime(i\OBJ, 17.0)
				i\InvImg = i\ItemTemplate\InvImg2
			EndIf
			;[End Block]
		Case it_wallet
			;[Block]
			If InvSlots = 0
				InvSlots = 10
				SetAnimTime(i\OBJ, 0.0)
				i\InvImg = i\ItemTemplate\InvImg2
			EndIf
			;[End Block]
	End Select
	
	i\InvSlots = InvSlots
	
	i\ID = LastItemID + 1
	LastItemID = i\ID
	
	CatchErrors("Uncaught: CreateItem.Items(" + Name + ", " + ID + ", " + x + ", " + y + ", " + z + ", " + R + ", " + G + ", " + B + ", " + Alpha + ", " + InvSlots + ")")
	
	Return(i)
End Function

Function RemoveItem%(i.Items)
	CatchErrors("RemoveItem()")
	
	Local n%
	
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
			If wi\NightVision > 0 Then me\CameraFogDist = 6.0 : wi\NightVision = 0
			;[End Block]
		Case it_scramble, it_finescramble
			;[Block]
			If wi\SCRAMBLE > 0 Then me\CameraFogDist = 6.0 : wi\SCRAMBLE = 0
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
	Local HideDist# = PowTwo(HideDistance)
	Local PushDist# = HideDist * 0.04
	Local DeletedItem% = False
	Local RandomVal# = Rnd(-0.002, 0.002)
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked)
			If i\DistTimer <= 0.0
				i\Dist = EntityDistanceSquared(Camera, i\Collider)
				i\DistTimer = 35.0
			Else
				i\DistTimer = Max(0.0, i\DistTimer - fps\Factor[0])
			EndIf
			
			If i\Dist < HideDist
				If EntityHidden(i\Collider) Then ShowEntity(i\Collider)
				
				If i\Dist < 1.44
					If ClosestItem = Null Lor i\Dist < EntityDistanceSquared(Camera, ClosestItem\Collider)
						If EntityInView(i\OBJ, Camera) And EntityVisible(i\Collider, Camera) Then ClosestItem = i
					EndIf
				EndIf
				
				If EntityCollided(i\Collider, HIT_MAP)
					i\DropSpeed = 0.0
				Else
					If ShouldEntitiesFall
						Pick = LinePick(EntityX(i\Collider), EntityY(i\Collider), EntityZ(i\Collider), 0.0, -3.0, 0.0)
						If Pick
							i\DropSpeed = Max(i\DropSpeed - 0.0004 * fps\Factor[0], -0.03)
							TranslateEntity(i\Collider, 0.0, i\DropSpeed * fps\Factor[0], 0.0)
						Else
							i\DropSpeed = 0.0
						EndIf
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
									
									While Abs(xTemp) + Abs(zTemp) < 0.001
										xTemp = xTemp + RandomVal
										zTemp = zTemp + RandomVal
									Wend
									
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
			If mo\MouseHit1 Then PickItem(ClosestItem)
		EndIf
	EndIf
End Function

Function PickItem%(item.Items, PlayPickUpSound% = True)
	If MenuOpen Lor ConsoleOpen Lor I_294\Using Lor d_I\SelectedDoor <> Null Lor SelectedScreen <> Null Lor me\Terminated Then Return
	
	CatchErrors("PickItem()")
	
	Local n%, z%
	
	If ItemAmount < MaxItemAmount
		Local CanPickItem% = 1
		
		For n = 0 To MaxItemAmount - 1
			If Inventory(n) = Null
				Select item\ItemTemplate\ID
					Case it_scp1123
						;[Block]
						Use1123()
						If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4 Then Return
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
					Case it_key6
						;[Block]
						GiveAchievement("keycard6")
						;[End Block]
					Case it_keyomni
						;[Block]
						GiveAchievement("omni")
						;[End Block]
					Case it_scp005
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
		me\SndVolume = Max(2.0, me\SndVolume)
	Else
		CreateMsg(GetLocalString("msg", "cantcarry"))
	EndIf
	
	CatchErrors("Uncaught: PickItem()")
End Function

Function DropItem%(item.Items, PlayDropSound% = True)
	CatchErrors("DropItem()")
	
	Local n%
	Local CameraYaw# = EntityYaw(Camera)
	
	If item\ItemTemplate\SoundID <> 66 And PlayDropSound Then PlaySound_Strict(snd_I\PickSFX[item\ItemTemplate\SoundID])
	
	item\Dropped = 1
	
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
	me\SndVolume = Max(2.0, me\SndVolume)
	
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
			ElseIf Instr(itt\Name, "Leaflet")
				Return(False)
			Else
				; ~ If the item is a paper, only allow spawning it if the name contains the word "note" or "log"
				; ~ (Because those are items created recently, which D-9341 has most likely never seen)
				Return(((Not Instr(itt\Name, "Note")) And (Not Instr(itt\Name, "Log"))))
			EndIf
			;[End Block]
	End Select
End Function

Function IsItemInFocus%()
	Select SelectedItem\ItemTemplate\ID
		Case it_nav, it_nav300, it_nav310, it_navulti, it_paper, it_oldpaper, it_badge, it_oldbadge, it_burntbadge, it_harnbadge, it_scp1025, it_fine1025, it_e_reader, it_e_reader20, it_e_reader30
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
Const ROUGH% = -2
Const COARSE% = -1
Const ONETOONE% = 0
Const FINE% = 1
Const VERYFINE% = 2
;[End Block]

Function Use914%(item.Items, Setting%, x#, y#, z#)
	me\RefinedItems = me\RefinedItems + 1
	
	Local it.Items, it2.Items, it3.Items, de.Decals, n.NPCs, r.Rooms
	Local Remove% = True, i%
	Local MakeDecal% = False
	
	Select item\ItemTemplate\ID
		Case it_gasmask, it_finegasmask, it_veryfinegasmask, it_gasmask148
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(4) = 1
						it2.Items = CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					Else
						it2.Items = CreateItem("Gas Mask", it_gasmask, x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					If Rand(50) = 1
						it2.Items = CreateItem("SCP-1499", it_scp1499, x, y, z)
					Else
						it2.Items = CreateItem("Fine Gas Mask", it_finegasmask, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(100) = 1
						it2.Items = CreateItem("SCP-1499", it_scp1499, x, y, z)
					Else
						it2.Items = CreateItem("Very Fine Gas Mask", it_veryfinegasmask, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1499, it_fine1499
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Gas Mask", it_gasmask, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Fine SCP-1499", it_fine1499, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					n.NPCs = CreateNPC(NPCType1499_1, x, y, z)
					n\State = 1.0 : n\State3 = 1.0
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundCHN = PlaySoundEx(n\Sound, Camera, n\Collider, 20.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_nvg, it_veryfinenvg, it_finenvg
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("SCRAMBLE Gear", it_scramble, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
				Case FINE
					;[Block]
					If Rand(5) = 1
						it2.Items = CreateItem("Fine SCRAMBLE Gear", it_finescramble, x, y, z)
						it2\State = Rnd(0.0, 1000.0)
					Else
						it2.Items = CreateItem("Fine Night Vision Goggles", it_finenvg, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Very Fine Night Vision Goggles", it_veryfinenvg, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_scramble, it_finescramble
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Night Vision Goggles", it_nvg, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Fine SCRAMBLE Gear", it_finescramble, x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
			End Select
			;[End Block]
		Case it_helmet
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Ballistic Vest", it_vest, x, y, z)
					;[End Block]	
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Heavy Ballistic Vest", it_finevest, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_cap, it_scp268, it_fine268
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Newsboy Cap", it_cap, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Fine SCP-268", it_fine268, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_vest, it_finevest
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Corrosive Ballistic Vest", it_corrvest, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Ballistic Helmet", it_helmet, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Heavy Ballistic Vest", it_finevest, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Bulky Ballistic Vest", it_veryfinevest, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_hazmatsuit, it_finehazmatsuit, it_hazmatsuit148
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Fine Hazmat Suit", it_finehazmatsuit, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Very Fine Hazmat Suit", it_veryfinehazmatsuit, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinehazmatsuit
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Hazmat Suit", it_hazmatsuit, x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_clipboard, it_wallet
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					ClearSecondInv(item, 0)
					;[End Block]
				Case COARSE
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
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					If item\InvSlots = 1
						item\InvSlots = 5
					Else
						item\InvSlots = Min(20, item\InvSlots + 5)
					EndIf
					Remove = False
					;[End Block]
				Case VERYFINE
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
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked)
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								If it\ItemTemplate\ID = it_clipboard
									RemoveItem(it)
									it2.Items = CreateItem("E-Reader", it_e_reader, x, y, z)
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
									it2.Items = CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
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
				Case VERYFINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked)
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								If it\ItemTemplate\ID = it_clipboard
									RemoveItem(it)
									it2.Items = CreateItem("E-Reader", it_e_reader, x, y, z)
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
									it2.Items = CreateItem("Fine Radio Transceiver", it_fineradio, x, y, z)
								Else
									it2.Items = CreateItem("Very Fine Radio Transceiver", it_veryfineradio, x, y, z)
								EndIf
								;[End Block]
							Case 2
								;[Block]
								If Rand(2) = 1
									it2.Items = CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
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
										it2.Items = CreateItem("Fine Night Vision Goggles", it_finenvg, x, y, z)
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
										it2.Items = CreateItem("Fine SCRAMBLE Gear", it_finescramble, x, y, z)
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
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("SCP-148 Ingot", it_scp148ingot, x, y, z)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case it_scp148ingot
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2.Items = CreateItem("SCP-148 Ingot", it_scp148ingot, x, y, z)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
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
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_hand
						If Rand(2) = 1
							it2.Items = CreateItem("Black Severed Hand", it_hand2, x, y, z)
						Else
							it2.Items = CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
						EndIf
					ElseIf item\ItemTemplate\ID = it_hand2
						If Rand(2) = 1
							it2.Items = CreateItem("White Severed Hand", it_hand, x, y, z)
						Else
							it2.Items = CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
						EndIf
					Else
						If Rand(2) = 1
							it2.Items = CreateItem("White Severed Hand", it_hand, x, y, z)
						Else
							it2.Items = CreateItem("Black Severed Hand", it_hand2, x, y, z)
						EndIf
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					n.NPCs = CreateNPC(NPCType008_1, x, y, z)
					n\State = 3.0
					;[End Block]
			End Select
			;[End Block]
		Case it_firstaid, it_firstaid2, it_finefirstaid, it_veryfinefirstaid
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_firstaid
						it2.Items = CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					Else
						it2.Items = CreateItem("First Aid Kit", it_firstaid, x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Compact First Aid Kit", it_finefirstaid, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Strange Bottle", it_veryfinefirstaid, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_key0, it_key0_bloody, it_key1, it_key1_bloody, it_key2, it_key3, it_key3_bloody, it_key4, it_key5, it_key6
			;[Block]
			Local Level% = item\ItemTemplate\ID
			Local LevelName%
			
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					If Level = it_key0 Lor Level = it_key0_bloody
						MakeDecal = True
					ElseIf Level = it_key6
						it2.Items = CreateItem("Level 0 Key Card", it_key0, x, y, z)
					Else
						Select Level
							Case it_key1, it_key1_bloody
								;[Block]
								LevelName = 1
								;[End Block]
							Case it_key2
								;[Block]
								LevelName = 2
								;[End Block]
							Case it_key3, it_key3_bloody
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
						it2.Items = CreateItem("Level " + (LevelName - 1) + " Key Card", Level - 1, x, y, z)
					EndIf
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Playing Card", it_playcard, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					Select Level
						Case it_key0, it_key0_bloody
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(6) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(5) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key1, it_key1_bloody
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(5) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key2
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(2) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key3, it_key3_bloody
							;[Block]
							If Rand(12 + (6 * SelectedDifficulty\OtherFactors)) = 1
								it2.Items = CreateItem("Level 4 Key Card", it_key4, x, y, z)
							Else
								it2.Items = CreateItem("Playing Card", it_playcard, x, y, z)
							EndIf
							;[End Block]
						Case it_key4
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(3) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(2) = 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(3) > 1
										it2.Items = CreateItem("Mastercard", it_mastercard, x, y, z)
										it2\State = Rand(0, 6)
									Else
										it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case it_key5
							;[Block]
							Local CurrAchvAmount% = Max(((S2IMapSize(AchievementsIndex) - 3) - (S2IMapSize(UnlockedAchievements) - 1) - S2IMapContains(UnlockedAchievements, "apollyon")) * (2 + SelectedDifficulty\OtherFactors), 0)
							
							If Rand(0, CurrAchvAmount) = 0
								it2.Items = CreateItem("Key Card Omni", it_keyomni, x, y, z)
							ElseIf Rand(12 + (6 * SelectedDifficulty\OtherFactors)) = 1
								it2.Items = CreateItem("Level 6 Key Card", it_key6, x, y, z)
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
							If Rand(6 + (3 * SelectedDifficulty\OtherFactors)) = 1
								it2.Items = CreateItem("Key Card Omni", it_keyomni, x, y, z)
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
				Case VERYFINE
					;[Block]
					CurrAchvAmount = Max(((S2IMapSize(AchievementsIndex) - 3) - (S2IMapSize(UnlockedAchievements) - 1) - S2IMapContains(UnlockedAchievements, "apollyon")) * (4 + SelectedDifficulty\OtherFactors), 0)
					If Rand(0, CurrAchvAmount) = 0
						it2.Items = CreateItem("Key Card Omni", it_keyomni, x, y, z)
					ElseIf Rand(24 + (6 * SelectedDifficulty\OtherFactors)) = 1
						it2.Items = CreateItem("Level 6 Key Card", it_key6, x, y, z)
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
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						it2.Items = CreateItem("Playing Card", it_playcard, x, y, z)
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					If Rand(4 + (2 * SelectedDifficulty\OtherFactors)) = 1
						it2.Items = CreateItem("Level 6 Key Card", it_key6, x, y, z)
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
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 0 Key Card", it_key0, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_mastercard
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Quarter", it_25ct, x, y, z)
					
					If Rand(2) = 1
						it3.Items = CreateItem("Quarter", it_25ct, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(3) = 1
						it3.Items = CreateItem("Quarter", it_25ct, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(4) = 1
						it3.Items = CreateItem("Quarter", it_25ct, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 0 Key Card", it_key0, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					If Rand(35) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						it2.Items = CreateItem("Level 1 Key Card", it_key1, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(50) = 1
						it2.Items = CreateItem("Mastercard", it_mastercard_golden, x, y, z)
						it2\State = 1000
					Else
						it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_mastercard_golden
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					For i = 0 To 20
						it2.Items = CreateItem("Quarter", it_25ct, x, y, z)
					Next
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					If Rand(35) = 1
						it2.Items = CreateItem("Level 4 Key Card", it_key4, x, y, z)
					Else
						it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(45) = 1
						it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
					Else
						it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp005
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 5 Key Card", it_key5, x, y, z)
					
					it3.Items = CreateItem("White Severed Hand", it_hand, x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					it3.Items = CreateItem("Black Severed Hand", it_hand2, x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					it3.Items = CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					it3.Items = CreateItem("White Key", it_key_white, x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					it3.Items = CreateItem("Yellow Key", it_key_yellow, x, y, z)
					EntityType(it3\Collider, HIT_ITEM)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case it_scp860, it_fine860
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID <> it_scp860
						it2.Items = CreateItem("SCP-860", it_scp860, x, y, z)
					Else
						it2.Items = CreateItem("White Key", it_key_white, x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					If Rand(8 + (4 * SelectedDifficulty\OtherFactors)) = 1
						it2.Items = CreateItem("Fine SCP-860", it_fine860, x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(12 + (6 * SelectedDifficulty\OtherFactors)) = 1
						it2.Items = CreateItem("Fine SCP-860", it_fine860, x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_lostkey, it_key_white, it_key_yellow
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_key_white
						it2.Items = CreateItem("Yellow Key", it_key_yellow, x, y, z)
					Else
						it2.Items = CreateItem("White Key", it_key_white, x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
					Else
						If item\ItemTemplate\ID = it_key_white
							it2.Items = CreateItem("Yellow Key", it_key_yellow, x, y, z)
						Else
							it2.Items = CreateItem("White Key", it_key_white, x, y, z)
						EndIf
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(6) = 1
						it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
					Else
						If item\ItemTemplate\ID = it_key_white
							it2.Items = CreateItem("Yellow Key", it_key_yellow, x, y, z)
						Else
							it2.Items = CreateItem("White Key", it_key_white, x, y, z)
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_radio, it_18vradio, it_fineradio, it_veryfineradio
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("18V Radio Transceiver", it_18vradio, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Fine Radio Transceiver", it_fineradio, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Very Fine Radio Transceiver", it_veryfineradio, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_nav, it_nav300, it_nav310, it_navulti
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("S-NAV Navigator", it_nav, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("S-NAV 310 Navigator", it_nav310, x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case VERYFINE
					;[Block]
					Local RoomAmount% = 0, RoomsFound% = 0
					
					For r.Rooms = Each Rooms
						Local RID% = r\RoomTemplate\RoomID
						
						If RID <> r_cont1_173_intro And RID <> r_gate_a And RID <> r_gate_b And RID <> r_dimension_106 And RID <> r_dimension_1499
							RoomAmount = RoomAmount + 1
							RoomsFound = RoomsFound + r\Found
						EndIf
					Next
					If Rand(Max((RoomAmount - (RoomsFound * 2)) * (2 + SelectedDifficulty\OtherFactors), 1)) = 1
						it2.Items = CreateItem("S-NAV Navigator Ultimate", it_navulti, x, y, z)
					Else
						it2.Items = CreateItem("S-NAV 300 Navigator", it_nav300, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp513
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType513_1 Then RemoveNPC(n)
					Next
					MakeDecal = True
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("SCP-513", it_scp513, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp420j, it_cigarette, it_joint, it_joint_smelly
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Cigarette", it_cigarette, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Joint", it_joint, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Smelly Joint", it_joint_smelly, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp714, it_coarse714, it_fine714, it_ring
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Coarse SCP-714", it_coarse714, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID = it_scp714
						it2.Items = CreateItem("Green Jade Ring", it_ring, x, y, z)
					Else
						it2.Items = CreateItem("SCP-714", it_scp714, x, y, z)
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Fine SCP-714", it_fine714, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_coarsebat
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("18V Battery", it_finebat, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_bat
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("18V Battery", it_finebat, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(5) = 1
						it2.Items = CreateItem("999V Battery", it_veryfinebat, x, y, z)
					Else
						it2.Items = CreateItem("Strange Battery", it_killbat, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_finebat
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					it2.Items = CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("18V Battery", it_finebat, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("999V Battery", it_veryfinebat, x, y, z)
					Else
						it2.Items = CreateItem("Strange Battery", it_killbat, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinebat
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					it2.Items = CreateItem("4.5V Battery", it_coarsebat, x, y, z)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("9V Battery", it_bat, x, y, z)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Strange Battery", it_killbat, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_eyedrops, it_eyedrops2, it_fineeyedrops, it_veryfineeyedrops
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1
						it2.Items = CreateItem("ReVision Eyedrops", it_eyedrops, x, y, z)
					Else
						it2.Items = CreateItem("RedVision Eyedrops", it_eyedrops2, x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Fine Eyedrops", it_fineeyedrops, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Very Fine Eyedrops", it_veryfineeyedrops, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_syringe
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Compact First Aid Kit", it_finefirstaid, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Fine Syringe", it_finesyringe, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("Very Fine Syringe", it_veryfinesyringe, x, y, z)
					Else
						it2.Items = CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_finesyringe
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("First Aid Kit", it_firstaid, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					If Rand(3) = 1
						it2.Items = CreateItem("Very Fine Syringe", it_veryfinesyringe, x, y, z)
					Else
						it2.Items = CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_veryfinesyringe
			;[Block]
			Select Setting
				Case ROUGH, COARSE, ONETOONE
					;[Block]
					it2.Items = CreateItem("Electronical Components", it_electronics, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(2) = 1
						n.NPCs = CreateNPC(NPCType008_1, x, y, z)
						n\State = 2.0
					Else
						it2.Items = CreateItem("Infected Syringe", it_syringeinf, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_syringeinf
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					n.NPCs = CreateNPC(NPCType008_1, x, y, z)
					n\State = 2.0
					;[End Block]	
				Case FINE
					;[Block]
					it2.Items = CreateItem("Syringe", it_syringe, x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(4) = 1
						it2.Items = CreateItem("Blue First Aid Kit", it_firstaid2, x, y, z)
					Else
						it2.Items = CreateItem("Fine Syringe", it_finesyringe, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp500pill, it_scp500pilldeath, it_pill
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Pill", it_pill, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					Local NO427Spawn% = False
					
					For it3.Items = Each Items
						If it3\ItemTemplate\ID = it_scp427
							NO427Spawn = True
							Exit
						EndIf
					Next
					If NO427Spawn
						it2.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					Else
						it2.Items = CreateItem("SCP-427", it_scp427, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(10) = 1
						it2.Items = CreateItem("SCP-500", it_scp500, x, y, z)
					Else
						it2.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_scp500
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("SCP-500-01", it_scp500pill, x, y, z)
					
					If Rand(2) = 1
						it3.Items = CreateItem("SCP-500-01", it_scp500pill, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(3) = 1
						it3.Items = CreateItem("SCP-500-01", it_scp500pill, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(4) = 1
						it3.Items = CreateItem("SCP-500-01", it_scp500pill, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					NO427Spawn = False
					
					For it3.Items = Each Items
						If it3\ItemTemplate\ID = it_scp427
							NO427Spawn = True
							Exit
						EndIf
					Next
					If (Not NO427Spawn)
						it2.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						
						If Rand(2) = 1
							it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							EntityType(it3\Collider, HIT_ITEM)
						EndIf
						
						If Rand(3) = 1
							it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							EntityType(it3\Collider, HIT_ITEM)
						EndIf
						
						If Rand(4) = 1
							it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
							EntityType(it3\Collider, HIT_ITEM)
						EndIf
					Else
						it2.Items = CreateItem("SCP-427", it_scp427, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
					
					If Rand(2) = 1
						it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(3) = 1
						it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(4) = 1
						it3.Items = CreateItem("Upgraded Pill", it_scp500pilldeath, x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_cup
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Cup", it_cup, x, y, z, 255.0 - item\R, 255.0 - item\G, 255.0 - item\B)
					it2\Name = item\Name
					it2\DisplayName = item\DisplayName
					it2\State = item\State
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Cup", it_cup, x, y, z, Min(item\R * Rnd(0.9, 1.1), 255.0), Min(item\G * Rnd(0.9, 1.1), 255.0), Min(item\B * Rnd(0.9, 1.1), 255.0))
					it2\Name = item\Name
					it2\DisplayName = item\DisplayName
					it2\State = 2.0
					;[End Block]
				Case VERYFINE
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
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					If Rand(10) = 1
						it2.Items = CreateItem("SCP-085", it_paper, x, y, z)
					Else
						it2.Items = CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_paper, it_oldpaper
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Origami", it_origami, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1025, it_fine1025, it_book
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					If item\ItemTemplate\ID <> it_scp1025
						it2.Items = CreateItem("SCP-1025", it_scp1025, x, y, z)
					Else
						it2.Items = CreateItem("Book", it_book, x, y, z)
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Fine SCP-1025", it_fine1025, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_scp1123
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Horror.ogg"))
					de.Decals = CreateDecal(DECAL_BLOOD_2, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, Rnd(0.3, 0.4), Rnd(0.8, 1.0), 1)
					EntityParent(de\OBJ, PlayerRoom\OBJ)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					If Rand(2) = 1
						it2.Items = CreateItem("Yellow Severed Hand", it_hand3, x, y, z)
					Else
						If Rand(2) = 1
							it2.Items = CreateItem("Black Severed Hand", it_hand2, x, y, z)
						Else
							it2.Items = CreateItem("White Severed Hand", it_hand, x, y, z)
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case it_badge, it_burntbadge, it_oldbadge, it_harnbadge
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", it_paper, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Document SCP-" + GetRandDocument(), it_paper, x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					If Rand(8) = 1
						If item\ItemTemplate\ID = it_badge
							it2.Items = CreateItem("Level 2 Key Card", it_key2, x, y, z)
						ElseIf item\ItemTemplate\ID = it_harnbadge
							it2.Items = CreateItem("Level 3 Key Card", it_key3, x, y, z)
						Else
							it2.Items = CreateItem("Level 4 Key Card", it_key4, x, y, z)
						EndIf
					Else
						it2.Items = CreateItem("Wallet", it_wallet, x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case it_e_reader, it_e_reader20, it_e_reader30
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					MakeDecal = True
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					If Rand(5) = 1
						it2.Items = CreateItem("E-Reader 20", it_e_reader20, x, y, z)
					Else
						it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
						it2\InvSlots = 15
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(15) = 1
						it2.Items = CreateItem("E-Reader 30", it_e_reader30, x, y, z)
					Else
						it2.Items = CreateItem("Clipboard", it_clipboard, x, y, z)
						it2\InvSlots = 20
					EndIf
					;[End Block]
			End Select
		Default
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					MakeDecal = True
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
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
	
	If it2 <> Null Then EntityType(it2\Collider, HIT_ITEM)
End Function

; ~ Made a function for SCP-1123 so we don't have to use duplicate code (since picking it up and using it does the same thing)
Function Use1123%()
	; ~ Temp:
	; ~		False: Stay alive
	; ~		True: Die
	
	Local e.Events
	Local Temp%
	
	If I_714\Using <> 2 And wi\GasMask <> 4 And wi\HazmatSuit <> 4
		me\LightFlash = 3.0
		PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
		
		Temp = True
		For e.Events = Each Events
			If e\EventID = e_cont2_1123
				If PlayerRoom = e\room
					If e\EventState < 1.0 ; ~ Start the event
						e\EventState = 1.0
						Temp = False
						Exit
					EndIf
				EndIf
			EndIf
		Next
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
		Case it_key0, it_key0_bloody
			;[Block]
			Return(KEY_CARD_0)
			;[End Block]
		Case it_key1, it_key1_bloody
			;[Block]
			Return(KEY_CARD_1)
			;[End Block]
		Case it_key2
			;[Block]
			Return(KEY_CARD_2)
			;[End Block]
		Case it_key3, it_key3_bloody
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
		Case it_scp005
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
	
	Local BatteryChance% = 10 + (5 * SelectedDifficulty\OtherFactors)
	
	Local RandomChance% = Rand(BatteryChance)
	
	If RandomChance > 0 And RandomChance <= 5
		BatteryName = "9V Battery"
		BatteryID = it_bat
	ElseIf RandomChance > 5 And RandomChance < BatteryChance - 2
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