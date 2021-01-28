Global BurntNote%

Global ItemAmount%

Const MaxItemAmount% = 10

Global Inventory.Items[MaxItemAmount + 1]
Global InvSelect%, SelectedItem.Items

Global ClosestItem.Items

Global LastItemID%

Type ItemTemplates
	Field Name$
	Field TempName$
	Field Sound%
	Field Found%
	Field OBJ%, OBJPath$
	Field InvImg%, InvImg2%, InvImgPath$
	Field ImgPath$, Img%
	Field IsAnim%
	Field Scale#
	Field Tex%, TexPath$
End Type 

Function CreateItemTemplate.ItemTemplates(Name$, TempName$, OBJPath$, InvImgPath$, ImgPath$, Scale#, SoundID%, TexturePath$ = "", InvImgPath2$ = "", HasAnim% = False, TexFlags% = 9)
	Local it.ItemTemplates = New ItemTemplates
	Local it2.ItemTemplates
	
	; ~ If another item shares the same object, copy it
	For it2.ItemTemplates = Each ItemTemplates
		If it2\OBJPath = OBJPath And it2\OBJ <> 0 Then
			it\OBJ = CopyEntity(it2\OBJ)
			Exit
		EndIf
	Next
	
	If (Not it\OBJ) Then
		If HasAnim Then
			it\OBJ = LoadAnimMesh_Strict(OBJPath)
			it\IsAnim = True
		Else
			it\OBJ = LoadMesh_Strict(OBJPath)
			it\IsAnim = False
		EndIf
		it\OBJPath = OBJPath
	EndIf
	it\OBJPath = OBJPath
	
	Local Texture%
	
	If TexturePath <> "" Then
		For it2.ItemTemplates = Each ItemTemplates
			If it2\TexPath = TexturePath And it2\Tex <> 0 Then
				Texture = it2\Tex
				Exit
			EndIf
		Next
		If (Not Texture) Then Texture = LoadTexture_Strict(TexturePath, TexFlags) : it\TexPath = TexturePath
		EntityTexture(it\OBJ, Texture)
		it\Tex = Texture
	EndIf  
	
	it\Scale = Scale
	ScaleEntity(it\OBJ, Scale, Scale, Scale, True)
	
	; ~ If another item shares the same object, copy it
	For it2.ItemTemplates = Each ItemTemplates
		If it2\InvImgPath = InvImgPath And it2\InvImg <> 0 Then
			it\InvImg = it2\InvImg
			If it2\InvImg2 <> 0 Then
				it\InvImg2 = it2\InvImg2
			EndIf
			Exit
		EndIf
	Next
	If (Not it\InvImg) Then
		it\InvImg = LoadImage_Strict(InvImgPath)
		it\InvImgPath = InvImgPath
		MaskImage(it\InvImg, 255, 0, 255)
	EndIf
	
	If InvImgPath2 <> "" Then
		If (Not it\InvImg2) Then
			it\InvImg2 = LoadImage_Strict(InvImgPath2)
			MaskImage(it\InvImg2, 255, 0, 255)
		EndIf
	Else
		it\InvImg2 = 0
	EndIf
	
	it\ImgPath = ImgPath
	
	it\TempName = TempName
	it\Name = Name
	
	it\Sound = SoundID
	
	HideEntity(it\OBJ)
	
	Return(it)
End Function

Const ItemsPath$ = "GFX\items\"

Function InitItemTemplates()
	Local it.ItemTemplates, it2.ItemTemplates
	
	; ~ [PAPER]
	
	CreateItemTemplate("Document SCP-005", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_005.png", 0.003, 0)
	CreateItemTemplate("Document SCP-008", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_008.png", 0.003, 0)
	CreateItemTemplate("Document SCP-012", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_012.png", 0.003, 0)
	CreateItemTemplate("Document SCP-035", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_035.png", 0.003, 0)
	CreateItemTemplate("Document SCP-049", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_049.png", 0.003, 0)
	CreateItemTemplate("Document SCP-079", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_079.png", 0.003, 0)
	CreateItemTemplate("Document SCP-096", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_096.png", 0.003, 0)
	CreateItemTemplate("Document SCP-106", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_106.png", 0.003, 0)
	CreateItemTemplate("Document SCP-173", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_173.png", 0.003, 0)
	CreateItemTemplate("Document SCP-205", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_205.png", 0.003, 0)
	CreateItemTemplate("Document SCP-372", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_372.png", 0.003, 0)
	CreateItemTemplate("Document SCP-409", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_409.png", 0.003, 0)
	CreateItemTemplate("Document SCP-500", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_500.png", 0.003, 0)
	CreateItemTemplate("Document SCP-513", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_513.png", 0.003, 0)
	CreateItemTemplate("Document SCP-682", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_682.png", 0.003, 0)
	CreateItemTemplate("Document SCP-714", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_714.png", 0.003, 0)
	CreateItemTemplate("Document SCP-860", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_860.png", 0.003, 0)
	CreateItemTemplate("Document SCP-860-1", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_860_1.png", 0.003, 0)
	CreateItemTemplate("Document SCP-895", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_895.png", 0.003, 0)
	CreateItemTemplate("Document SCP-939", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_939.png", 0.003, 0)
	CreateItemTemplate("Document SCP-966", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_966.png", 0.003, 0)
	CreateItemTemplate("Document SCP-970", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_970.png", 0.003, 0)
	CreateItemTemplate("Document SCP-1048", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_1048.png", 0.003, 0)
	CreateItemTemplate("Document SCP-1123", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_1123.png", 0.003, 0)
	CreateItemTemplate("Document SCP-1162", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_1162.png", 0.003, 0)
	CreateItemTemplate("Document SCP-1499", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_1499.png", 0.003, 0)
	
	CreateItemTemplate("Incident Report SCP-1048-A", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_IR_1048_a.png", 0.003, 0)
	
	CreateItemTemplate("SCP-035 Addendum", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_035_ad.png", 0.003, 0)
	
	CreateItemTemplate("SCP-093 Recovered Materials", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_093_rm.png", 0.003, 0)
	
	CreateItemTemplate("Addendum: 5/14 Test Log", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_RAND(2).png", 0.003, 0)
	
	CreateItemTemplate("Class D Orientation Leaflet", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_OL.png", 0.003, 0)
	
	CreateItemTemplate("Disciplinary Hearing DH-S-4137-17092", "oldpaper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_DH.png", 0.003, 0)
	
	CreateItemTemplate("Document", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_RAND(3).png", 0.003, 0)
	
	CreateItemTemplate("Field Agent Log #235-001-CO5", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_O5.png", 0.003, 0)
	
	CreateItemTemplate("Groups of Interest Log", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_O5(2).png", 0.003, 0)
	
	CreateItemTemplate("Incident Report SCP-106-0204", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_IR_106.png", 0.003, 0)
	
	CreateItemTemplate("Mobile Task Forces", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_MTF.png", 0.003, 0)
	
	CreateItemTemplate("Note from Daniel", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note(2).png", ItemsPath + "note_Daniel.png", 0.0025, 0)
	
	CreateItemTemplate("Nuclear Device Document", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_NDP.png", 0.003, 0)
	
	CreateItemTemplate("Object Classes", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_OBJC.png", 0.003, 0)
	
	CreateItemTemplate("Recall Protocol RP-106-N", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_RP.png", 0.0025, 0)
	
	CreateItemTemplate("Research Sector-02 Scheme", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_RS.png", 0.003, 0)
	
	CreateItemTemplate("Security Clearance Levels", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_SCL.png", 0.003, 0)
	
	CreateItemTemplate("Sticky Note", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note(2).png", ItemsPath + "note_682.png", 0.0025, 0)
	
	CreateItemTemplate("The Modular Site Project", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_MSP.png", 0.003, 0)
	
	CreateItemTemplate("Blank Paper", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper_blank.png", ItemsPath + "doc_blank.png", 0.003, 0, ItemsPath + "paper_blank.png")
	
	CreateItemTemplate("Blood-stained Note", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note_bloody.png", ItemsPath + "note_L(3).png", 0.0025, 0, ItemsPath + "note_bloody.png")
	
	it.ItemTemplates = CreateItemTemplate("Burnt Note", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_burnt_note.png", ItemsPath + "note_Maynard.png", 0.003, 0, ItemsPath + "burnt_note.png")
	it\Img = BurntNote
	
	CreateItemTemplate("Data Report", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper_bloody.png", ItemsPath + "doc_data.png", 0.003, 0, ItemsPath + "paper_bloody.png")
	
	CreateItemTemplate("Document SCP-427", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper_bloody.png", ItemsPath + "doc_427.png", 0.003, 0, ItemsPath + "paper_bloody.png")
	
	CreateItemTemplate("Drawing", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "doc_1048.png", 0.003, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Dr. Allok's Note", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_Allok.png", 0.004, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Dr. L's Note #1", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_L.png", 0.0025, 0, ItemsPath + "note.png")
	CreateItemTemplate("Dr. L's Note #2", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_L(2).png", 0.0025, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Dr. L's Burnt Note #1", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_burnt_note.png", ItemsPath + "note_L(4).png", 0.0025, 0, ItemsPath + "burnt_note.png")
	CreateItemTemplate("Dr. L's Burnt Note #2", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_burnt_note.png", ItemsPath + "note_L(5).png", 0.0025, 0, ItemsPath + "burnt_note.png")
	
	CreateItemTemplate("Emily Ross' Badge", "badge", ItemsPath + "badge.b3d", ItemsPath + "INV_Emily_badge.png", ItemsPath + "Emily_badge_HUD.png", 0.0001, 1, ItemsPath + "Emily_badge.png")
	
	CreateItemTemplate("Journal Page", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_Gonzales.png", 0.0025, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Leaflet", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "leaflet.png", 0.003, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Log #1", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_forest.png", 0.004, 0, ItemsPath + "note.png")
	CreateItemTemplate("Log #2", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_forest(2).png", 0.004, 0, ItemsPath + "note.png")
	CreateItemTemplate("Log #3", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_forest(3).png", 0.004, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Movie Ticket", "ticket", ItemsPath + "ticket.b3d", ItemsPath + "INV_ticket.png", ItemsPath + "ticket_HUD.png", 0.002, 0, ItemsPath + "ticket.png", "", 0, 1 + 2 + 8)
	
	CreateItemTemplate("Mysterious Note", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_mysterious.png", 0.003, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Note from Maynard", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_Maynard(2).png", 0.0025, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Notification", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "doc_RAND.png", 0.003, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Old Badge", "badge", ItemsPath + "badge.b3d", ItemsPath + "INV_D_9341_badge.png", ItemsPath + "D_9341_badge_HUD.png", 0.0001, 1, ItemsPath + "D_9341_badge.png", "", 0, 1 + 2 + 8)
	
	CreateItemTemplate("Origami", "origami", ItemsPath + "origami.b3d", ItemsPath + "INV_origami.png", "", 0.003, 0)
	
	CreateItemTemplate("Scorched Note", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_burnt_note.png", ItemsPath + "note_L(6).png", 0.0025, 0, ItemsPath + "burnt_note.png")
	
	CreateItemTemplate("Strange Note", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_note.png", ItemsPath + "note_strange.png", 0.0025, 0, ItemsPath + "note.png")
	
	CreateItemTemplate("Unknown Note", "paper", ItemsPath + "note.b3d", ItemsPath + "INV_note_bloody.png", ItemsPath + "note_unknown.png", 0.003, 0, ItemsPath + "note_bloody.png")
	
	; ~ [SCPs]
	
	CreateItemTemplate("SCP-005", "scp005", ItemsPath + "scp_005.b3d", ItemsPath + "INV_scp_005.png", "", 0.0004, 1)
	CreateItemTemplate("SCP-148 Ingot", "scp148ingot", ItemsPath + "scp_148.b3d", ItemsPath + "INV_scp_148.png", "", RoomScale, 2)
	CreateItemTemplate("SCP-427", "scp427", ItemsPath + "scp_427.b3d", ItemsPath + "INV_scp_427.png", "", 0.001, 3)
	
	it.ItemTemplates = CreateItemTemplate("SCP-500-01", "scp500pill", ItemsPath + "pill.b3d", ItemsPath + "INV_scp_500_pill.png", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	CreateItemTemplate("SCP-500", "scp500", ItemsPath + "scp_500.b3d", ItemsPath + "INV_scp_500.png", "", 0.035, 2)
	CreateItemTemplate("SCP-513", "scp513", ItemsPath + "scp_513.b3d", ItemsPath + "INV_scp_513.png", "", 0.1, 2)
	CreateItemTemplate("SCP-714", "scp714", ItemsPath + "scp_714.b3d", ItemsPath + "INV_scp_714.png", "", 0.3, 3)
	CreateItemTemplate("SCP-860", "scp860", ItemsPath + "scp_860.b3d", ItemsPath + "INV_scp_860.png", "", 0.003, 3)
	CreateItemTemplate("SCP-1025", "scp1025", ItemsPath + "scp_1025.b3d", ItemsPath + "INV_scp_1025.png", "", 0.1, 0)
	CreateItemTemplate("SCP-1123", "scp1123", ItemsPath + "scp_1123.b3d", ItemsPath + "INV_scp_1123.png", "", 0.015, 2)
	CreateItemTemplate("SCP-1499", "scp1499", ItemsPath + "scp_1499.b3d", ItemsPath + "INV_scp_1499.png", "", 0.022, 2)
	CreateItemTemplate("SCP-1499", "super1499", ItemsPath + "scp_1499.b3d", ItemsPath + "INV_scp_1499.png", "", 0.022, 2)
	
	CreateItemTemplate("Joint", "joint", ItemsPath + "scp_420_j.b3d", ItemsPath + "INV_scp_420_j.png", "", 0.0004, 2)
	
	CreateItemTemplate("Metal Panel", "scp148", ItemsPath + "metal_panel.b3d", ItemsPath + "INV_metal_panel.png", "", RoomScale, 2)
	
	CreateItemTemplate("Smelly Joint", "scp420s", ItemsPath + "scp_420_j.b3d", ItemsPath + "INV_scp_420_j.png", "", 0.0004, 2)
	
	CreateItemTemplate("Some SCP-420-J", "scp420j", ItemsPath + "scp_420_j.b3d", ItemsPath + "INV_scp_420_j.png", "", 0.0005, 2)
	
	it.ItemTemplates = CreateItemTemplate("Upgraded Pill", "scp500pilldeath", ItemsPath + "pill.b3d", ItemsPath + "INV_scp_500_pill.png", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	; ~ [MISC ITEMS]
	
	CreateItemTemplate("Ballistic Helmet", "helmet", ItemsPath + "ballistic_helmet.b3d", ItemsPath + "INV_ballistic_helmet.png", "", 0.02, 2)
	
	CreateItemTemplate("Ballistic Vest", "vest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.02, 2)
	CreateItemTemplate("Bulky Ballistic Vest", "veryfinevest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.025, 2)
	CreateItemTemplate("Heavy Ballistic Vest", "finevest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.022, 2)
	CreateItemTemplate("Corrosive Ballistic Vest", "corrvest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.02, 2, ItemsPath + "ballistic_vest_corrosive.png")
	
	CreateItemTemplate("Cigarette", "cigarette", ItemsPath + "scp_420_j.b3d", ItemsPath + "INV_scp_420_j.png", "", 0.0004, 2)
	
	CreateItemTemplate("Cup", "cup", ItemsPath + "cup.b3d", ItemsPath + "INV_cup.png", "", 0.04, 2)
	
	CreateItemTemplate("Clipboard", "clipboard", ItemsPath + "clipboard.b3d", ItemsPath + "INV_clipboard.png", "", 0.003, 1, "", ItemsPath + "INV_clipboard(2).png", 1)
	
	CreateItemTemplate("Electronical Components", "electronics", ItemsPath + "circuits.b3d", ItemsPath + "INV_circuits.png", "", 0.0011, 1)
	
	CreateItemTemplate("Empty Cup", "emptycup", ItemsPath + "cup.b3d", ItemsPath + "INV_cup.png", "", 0.04, 2)
	
	CreateItemTemplate("ReVision Eyedrops", "eyedrops", ItemsPath + "eye_drops.b3d", ItemsPath + "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate("Eyedrops", "fineeyedrops", ItemsPath + "eye_drops.b3d", ItemsPath + "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate("Eyedrops", "supereyedrops", ItemsPath + "eye_drops.b3d", ItemsPath + "INV_eye_drops.png", "", 0.0012, 1)
	CreateItemTemplate("RedVision Eyedrops", "eyedrops2", ItemsPath + "eye_drops.b3d", ItemsPath + "INV_eye_drops_red.png", "", 0.0012, 1, ItemsPath + "eye_drops_red.png")
	
	CreateItemTemplate("First Aid Kit", "firstaid", ItemsPath + "first_aid_kit.b3d", ItemsPath + "INV_first_aid_kit.png", "", 0.05, 1)
	CreateItemTemplate("Small First Aid Kit", "finefirstaid", ItemsPath + "first_aid_kit.b3d", ItemsPath + "INV_first_aid_kit.png", "", 0.03, 1)
	CreateItemTemplate("Blue First Aid Kit", "firstaid2", ItemsPath + "first_aid_kit.b3d", ItemsPath + "INV_first_aid_kit(2).png", "", 0.03, 1, ItemsPath + "first_aid_kit(2).png")
	CreateItemTemplate("Strange Bottle", "veryfinefirstaid", ItemsPath + "eye_drops.b3d", ItemsPath + "INV_strange_bottle.png", "", 0.002, 1, ItemsPath + "strange_bottle.png")	

	CreateItemTemplate("Gas Mask", "gasmask", ItemsPath + "gas_mask.b3d", ItemsPath + "INV_gas_mask.png", "", 0.019, 2)
	CreateItemTemplate("Gas Mask", "supergasmask", ItemsPath + "gas_mask.b3d", ItemsPath + "INV_gas_mask.png", "", 0.02, 2)
	CreateItemTemplate("Heavy Gas Mask", "gasmask3", ItemsPath + "gas_mask.b3d", ItemsPath + "INV_gas_mask.png", "", 0.02, 2)
	
	CreateItemTemplate("Hazmat Suit", "hazmatsuit", ItemsPath + "hazmat_suit.b3d", ItemsPath + "INV_hazmat_suit.png", "", 0.013, 2, "", "", 1)
	CreateItemTemplate("Hazmat Suit", "hazmatsuit2", ItemsPath + "hazmat_suit.b3d", ItemsPath + "INV_hazmat_suit.png", "", 0.013, 2, "", "", 1)
	CreateItemTemplate("Heavy Hazmat Suit", "hazmatsuit3", ItemsPath + "hazmat_suit.b3d", ItemsPath + "INV_hazmat_suit.png", "", 0.013, 2, "", "", 1)
	
	CreateItemTemplate("Night Vision Goggles", "nvg", ItemsPath + "night_vision_goggles.b3d", ItemsPath + "INV_night_vision_goggles.png", "", 0.02, 2)
	CreateItemTemplate("Night Vision Goggles", "finenvg", ItemsPath + "night_vision_goggles.b3d", ItemsPath + "INV_night_vision_goggles(2).png", "", 0.02, 2)
	CreateItemTemplate("Night Vision Goggles", "supernvg", ItemsPath + "night_vision_goggles.b3d", ItemsPath + "INV_night_vision_goggles(3).png", "", 0.02, 2)
	CreateItemTemplate("SCRAMBLE Gear", "scramble", ItemsPath + "SCRAMBLE_gear.b3d", ItemsPath + "INV_SCRAMBLE_gear.png", "", 0.02, 2)
	
	it.ItemTemplates = CreateItemTemplate("Pill", "pill", ItemsPath + "pill.b3d", ItemsPath + "INV_pill.png", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 255.0, 255.0)
	
	CreateItemTemplate("Radio Transceiver", "radio", ItemsPath + "radio.b3d", ItemsPath + "INV_radio.png", ItemsPath + "radio_HUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "fineradio", ItemsPath + "radio.b3d", ItemsPath + "INV_radio.png", ItemsPath + "radio_HUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "veryfineradio", ItemsPath + "radio.b3d", ItemsPath + "INV_radio.png", ItemsPath + "radio_HUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "18vradio", ItemsPath + "radio.b3d", ItemsPath + "INV_radio.png", ItemsPath + "radio_HUD.png", 1.02, 1)
	
	CreateItemTemplate("Severed Hand", "hand", ItemsPath + "severed_hand.b3d", ItemsPath + "INV_severed_hand.png", "", 0.03, 2)
	CreateItemTemplate("Black Severed Hand", "hand2", ItemsPath + "severed_hand.b3d", ItemsPath + "INV_severed_hand(2).png", "", 0.03, 2, ItemsPath + "severed_hand(2).png")
	
	CreateItemTemplate("S-NAV Navigator", "nav", ItemsPath + "navigator.b3d", ItemsPath + "INV_navigator.png", ItemsPath + "navigator_HUD.png", 0.0008, 1)
	CreateItemTemplate("S-NAV Navigator Ultimate", "navulti", ItemsPath + "navigator.b3d", ItemsPath + "INV_navigator.png", ItemsPath + "navigator_HUD.png", 0.0008, 1)
	CreateItemTemplate("S-NAV 300 Navigator", "nav300", ItemsPath + "navigator.b3d", ItemsPath + "INV_navigator.png", ItemsPath + "navigator_HUD.png", 0.0008, 1)
	CreateItemTemplate("S-NAV 310 Navigator", "nav310", ItemsPath + "navigator.b3d", ItemsPath + "INV_navigator.png", ItemsPath + "navigator_HUD.png", 0.0008, 1)
	
	CreateItemTemplate("9V Battery", "bat", ItemsPath + "battery.b3d", ItemsPath + "INV_battery_9v.png", "", 0.008, 1)
	CreateItemTemplate("4.5V Battery", "badbat", ItemsPath + "battery.b3d", ItemsPath + "INV_battery_4.5v.png", "", 0.008, 1, ItemsPath + "battery_4.5V.png")
	CreateItemTemplate("18V Battery", "finebat", ItemsPath + "battery.b3d", ItemsPath + "INV_battery_18v.png", "", 0.01, 1, ItemsPath + "battery_18V.png")
	CreateItemTemplate("999V Battery", "superbat", ItemsPath + "battery.b3d", ItemsPath + "INV_battery_999v.png", "", 0.009, 1, ItemsPath + "battery_999V.png")
	CreateItemTemplate("Strange Battery", "killbat", ItemsPath + "battery.b3d", ItemsPath + "INV_strange_battery.png", "", 0.01, 1, ItemsPath + "strange_battery.png")
	
	CreateItemTemplate("Syringe", "syringe", ItemsPath + "syringe.b3d", ItemsPath + "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate("Syringe", "finesyringe", ItemsPath + "syringe.b3d", ItemsPath + "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate("Syringe", "veryfinesyringe", ItemsPath + "syringe.b3d", ItemsPath + "INV_syringe.png", "", 0.005, 2)
	CreateItemTemplate("Syringe", "syringeinf", ItemsPath + "syringe.b3d", ItemsPath + "INV_syringe_infect.png", "", 0.005, 2, ItemsPath + "syringe_infect.png")
	
	CreateItemTemplate("Wallet", "wallet", ItemsPath + "wallet.b3d", ItemsPath + "INV_wallet.png", "", 0.0006, 2, "", "", 1)
	
	; ~ [KEYCARDS, KEYS, CARDS, COINS]
	
	CreateItemTemplate("Coin", "coin", ItemsPath + "coin.b3d", ItemsPath + "INV_coin_rusty.png", "", 0.0005, 3, ItemsPath + "coin_rusty.png")
	
	CreateItemTemplate("Level 0 Key Card", "key0", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_0.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_0.png")
	CreateItemTemplate("Level 1 Key Card", "key1", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_1.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_1.png")
	CreateItemTemplate("Level 2 Key Card", "key2", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_2.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_2.png")
	CreateItemTemplate("Level 3 Key Card", "key3", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_3.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_3.png")
	CreateItemTemplate("Level 4 Key Card", "key4", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_4.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_4.png")
	CreateItemTemplate("Level 5 Key Card", "key5", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_5.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_5.png")
	CreateItemTemplate("Level 6 Key Card", "key6", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_6.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_6.png")
	CreateItemTemplate("Key Card Omni", "keyomni", ItemsPath + "key_card.b3d", ItemsPath + "INV_key_card_lvl_omni.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_omni.png")
	
	CreateItemTemplate("Lost Key", "key", ItemsPath + "key.b3d", ItemsPath + "INV_key.png", "", 0.003, 3)
	
	CreateItemTemplate("Mastercard", "mastercard", ItemsPath + "key_card.b3d", ItemsPath + "INV_master_card.png", "", 0.0004, 1, ItemsPath + "master_card.png")
	
	CreateItemTemplate("Playing Card", "playcard", ItemsPath + "key_card.b3d", ItemsPath + "INV_playing_card.png", "", 0.0004, 1, ItemsPath + "playing_card.png")
	
	CreateItemTemplate("Quarter", "25ct", ItemsPath + "coin.b3d", ItemsPath + "INV_coin.png", "", 0.0005, 3)
	
	For it.ItemTemplates = Each ItemTemplates
		If it\Tex <> 0 Then
			If it\TexPath <> "" Then
				For it2.ItemTemplates = Each ItemTemplates
					If it2 <> it And it2\Tex = it\Tex Then
						it2\Tex = 0
						Exit
					EndIf
				Next
			EndIf
			DeleteSingleTextureEntryFromCache(it\Tex) : it\Tex = 0
		EndIf
	Next
End Function 

Function GetRandDocument$()
	Select Rand(0, 21)
		Case 0
			;[Block]
			Return("005")
			;[End Block]
		Case 1
			;[Block]
			Return("008")
			;[End Block]
		Case 2
			;[Block]
			Return("012")
			;[End Block]
		Case 3
			;[Block]
			Return("035")
			;[End Block]
		Case 4
			;[Block]
			Return("049")
			;[End Block]
		Case 5
			;[Block]
			Return("096")
			;[End Block]
		Case 6
			;[Block]
			Return("106")
			;[End Block]
		Case 7
			;[Block]
			Return("173")
			;[End Block]
		Case 8
			;[Block]
			Return("205")
			;[End Block]
		Case 9
			;[Block]
			Return("409")
			;[End Block]
		Case 10
			;[Block]
			Return("513")
			;[End Block]
		Case 11
			;[Block]
			Return("682")
			;[Block]
		Case 12
			;[Block]
			Return("714")
			;[End Block]
		Case 13
			;[Block]
			Return("860")
			;[End Block]
		Case 14
			;[Block]
			Return("860-1")
			;[End Block]
		Case 15
			;[Block]
			Return("895")
			;[End Block]
		Case 16
			;[Block]
			Return("939")
			;[End Block]
		Case 17
			;[Block]
			Return("966")
			;[End Block]
		Case 18
			;[Block]
			Return("970")
			;[End Block]
		Case 19
			;[Block]
			Return("1048")
			;[End Block]
		Case 20
			;[Block]
			Return("1162")
			;[End Block]
		Case 21
			;[Block]
			Return("1499")
			;[End Block]
	End Select
End Function

Type Items
	Field Name$
	Field Collider%, Model%
	Field ItemTemplate.ItemTemplates
	Field DropSpeed#
	Field R%, G%, B%, A#
	Field Level%
	Field SoundCHN%
	Field Dist#, DistTimer#
	Field State#, State2#, State3#
	Field Picked%, Dropped%
	Field InvImg%
	Field WontColl% = False
	Field xSpeed#, zSpeed#
	Field SecondInv.Items[20]
	Field ID%
	Field InvSlots%
End Type 

Function CreateItem.Items(Name$, TempName$, x#, y#, z#, R% = 0, G% = 0, B% = 0, A# = 1.0, InvSlots% = 0)
	CatchErrors("Uncaught (CreateItem)")
	
	Local i.Items = New Items
	Local it.ItemTemplates
	
	Name = Lower(Name)
	TempName = Lower(TempName)
	
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\Name) = Name And Lower(it\TempName) = TempName Then
			i\ItemTemplate = it
			i\Collider = CreatePivot()			
			EntityRadius(i\Collider, 0.01)
			EntityPickMode(i\Collider, 1, False)
			i\Model = CopyEntity(it\OBJ, i\Collider)
			i\Name = it\Name
			ShowEntity(i\Collider)
			ShowEntity(i\Model)
			Exit
		EndIf
	Next 
	
	i\WontColl = False
	
	If i\ItemTemplate = Null Then RuntimeError("Item template not found (" + Name + ", " + TempName + ")")
	
	ResetEntity(i\Collider)
	PositionEntity(i\Collider, x, y, z, True)
	RotateEntity(i\Collider, 0.0, Rnd(360.0), 0.0)
	
	i\Dist = EntityDistance(me\Collider, i\Collider)
	i\DropSpeed = 0.0
	
	If TempName = "cup" Then
		i\R = R
		i\G = G
		i\B = B
		i\A = A
		
		Local Liquid% = CopyEntity(o\MiscModelID[0])
		
		ScaleEntity(Liquid, i\ItemTemplate\Scale, i\ItemTemplate\Scale, i\ItemTemplate\Scale, True)
		PositionEntity(Liquid, EntityX(i\Collider, True), EntityY(i\Collider, True), EntityZ(i\Collider, True))
		EntityParent(Liquid, i\Model)
		EntityColor(Liquid, R, G, B)
		
		If A < 0.0 Then 
			EntityFX(Liquid, 1)
			EntityAlpha(Liquid, Abs(A))
		Else
			EntityAlpha(Liquid, Abs(A))
		EndIf
		EntityShininess(Liquid, 1.0)
	EndIf
	
	i\InvImg = i\ItemTemplate\InvImg
	If TempName = "clipboard" And InvSlots = 0 Then
		InvSlots = 10
		SetAnimTime(i\Model, 17.0)
		i\InvImg = i\ItemTemplate\InvImg2
	ElseIf TempName = "wallet" And InvSlots = 0 Then
		InvSlots = 10
		SetAnimTime(i\Model, 0.0)
	EndIf
	
	i\InvSlots = InvSlots
	
	i\ID = LastItemID + 1
	LastItemID = i\ID
	
	CatchErrors("CreateItem")
	
	Return(i)
End Function

Function RemoveItem(i.Items)
	CatchErrors("Uncaught (RemoveItem)")
	
	Local n%
	
	FreeEntity(i\Model) : i\Model = 0
	FreeEntity(i\Collider) : i\Collider = 0
	
	For n = 0 To MaxItemAmount - 1
		If Inventory[n] = i
			Inventory[n] = Null
			ItemAmount = ItemAmount - 1
			Exit
		EndIf
	Next
	
	If SelectedItem = i Then SelectedItem = Null
	
	If i\ItemTemplate\Img <> 0 Then
		FreeImage(i\ItemTemplate\Img) : i\ItemTemplate\Img = 0
	EndIf
	Delete(i)
	
	CatchErrors("RemoveItem")
End Function

Function RemoveWearableItems(item.Items)
	CatchErrors("Uncaught (RemoveWearableItems)")
	
	Select item\ItemTemplate\TempName
		Case "gasmask", "supergasmask", "gasmask3"
			;[Block]
			wi\GasMask = 0
			;[End Block]
		Case "hazmatsuit",  "hazmatsuit2", "hazmatsuit3"
			;[Block]
			wi\HazmatSuit = 0
			;[End Block]
		Case "vest", "finevest"
			;[Block]
			wi\BallisticVest = 0
			;[End Block]
		Case "helmet"
			;[Block]
			wi\BallisticHelmet = 0
			;[End Block]
		Case "nvg", "supernvg", "finenvg"
			;[Block]
			If wi\NightVision > 0 Then opt\CameraFogFar = opt\StoredCameraFogFar : wi\NightVision = 0
			;[End Block]
		Case "scp714"
			;[Block]
			I_714\Using = 0
			;[End Block]
		Case "scp1499", "super1499"
			;[Block]
			I_1499\Using = 0
			;[End Block]
		Case "scp427"
			;[Block]
			I_427\Using = 0
			;[End Block]
		Case "scramble"
			;[Block]
			wi\SCRAMBLE = 0
			;[End Block]
	End Select
	
	CatchErrors("RemoveWearableItems")
End Function

Function ClearSecondInv(item.Items, From% = 0)
	Local i%
	
	For i = From To 19
		If item\SecondInv[i] <> Null Then RemoveItem(item\SecondInv[i])
		item\SecondInv[i] = Null
	Next
End Function

Function UpdateItems()
	CatchErrors("Uncaught (UpdateItems)")
	
	Local n%, i.Items, i2.Items
	Local xTemp#, yTemp#, zTemp#
	Local Temp%, np.NPCs
	Local Pick%, ed#
	Local HideDist# = HideDistance * 0.5
	Local DeletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked) Then
			If i\DistTimer < MilliSecs() Then
				i\Dist = EntityDistance(Camera, i\Collider)
				i\DistTimer = MilliSecs() + 700
				If i\Dist < HideDist Then ShowEntity(i\Collider)
			EndIf
			
			If i\Dist < HideDist Then
				ShowEntity(i\Collider)
				If i\Dist < 1.2 Then
					If ClosestItem = Null Then
						If EntityInView(i\Model, Camera) Then
							If EntityVisible(i\Collider, Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					ElseIf ClosestItem = i Lor i\Dist < EntityDistance(Camera, ClosestItem\Collider) Then 
						If EntityInView(i\Model, Camera) Then
							If EntityVisible(i\Collider, Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					EndIf
				EndIf
				
				If EntityCollided(i\Collider, HIT_MAP) Then
					i\DropSpeed = 0.0
					i\xSpeed = 0.0
					i\zSpeed = 0.0
				Else
					If ShouldEntitiesFall Then
						Pick = LinePick(EntityX(i\Collider), EntityY(i\Collider), EntityZ(i\Collider), 0.0, -10.0, 0.0)
						If Pick Then
							i\DropSpeed = i\DropSpeed - 0.0004 * fps\FPSfactor[0]
							TranslateEntity(i\Collider, i\xSpeed * fps\FPSfactor[0], i\DropSpeed * fps\FPSfactor[0], i\zSpeed * fps\FPSfactor[0])
							If i\WontColl Then ResetEntity(i\Collider)
						Else
							i\DropSpeed = 0.0
							i\xSpeed = 0.0
							i\zSpeed = 0.0
						EndIf
					Else
						i\DropSpeed = 0.0
						i\xSpeed = 0.0
						i\zSpeed = 0.0
					EndIf
				EndIf
				
				If i\Dist < HideDist * 0.2 Then
					For i2.Items = Each Items
						If i <> i2 And (Not i2\Picked) And i2\Dist < HideDist * 0.2 Then
							xTemp = EntityX(i2\Collider, True) - EntityX(i\Collider, True)
							yTemp = EntityY(i2\Collider, True) - EntityY(i\Collider, True)
							zTemp = EntityZ(i2\Collider, True) - EntityZ(i\Collider, True)
							
							ed = xTemp * xTemp + zTemp * zTemp
							If ed < 0.07 And Abs(yTemp) < 0.25 Then
								; ~ Items are too close together, push away
								xTemp = xTemp * (0.07 - ed)
								zTemp = zTemp * (0.07 - ed)
								
								While Abs(xTemp) + Abs(zTemp) < 0.001
									xTemp = xTemp + Rnd(-0.002, 0.002)
									zTemp = zTemp + Rnd(-0.002, 0.002)
								Wend
								
								TranslateEntity(i2\Collider, xTemp, 0.0, zTemp)
								TranslateEntity(i\Collider, -xTemp, 0.0, -zTemp)
							EndIf
						EndIf
					Next
				EndIf
				If EntityY(i\Collider) < -60.0 Then 
					RemoveItem(i)
					DeletedItem = True
				EndIf
			Else
				HideEntity(i\Collider)
			EndIf
		Else
			i\DropSpeed = 0.0
			i\xSpeed = 0.0
			i\zSpeed = 0.0
		EndIf
		
		If (Not DeletedItem) Then
			CatchErrors("Detected error for:" + Chr(34) + i\ItemTemplate\Name + Chr(34) + " item!")
		Else
			CatchErrors("Detected error for deleted item!")
		EndIf
		DeletedItem = False
	Next
	
	If ClosestItem <> Null Then
		If mo\MouseHit1 Then PickItem(ClosestItem)
	EndIf
End Function

Function PickItem(item.Items)
	If wi\HazmatSuit > 0 Then
		CreateMsg("You cannot pick up any items while wearing a hazmat suit.", 6.0)
		Return
	EndIf
	
	CatchErrors("Uncaught (PickItem)")
	
	Local n% = 0
	Local CanPickItem = 1
	Local FullINV% = True
	Local e.Events
	
	For n = 0 To MaxItemAmount - 1
		If Inventory[n] = Null
			FullINV = False
			Exit
		EndIf
	Next
	
	Local z%
	
	If (Not FullINV) Then
		For n = 0 To MaxItemAmount - 1
			If Inventory[n] = Null Then
				Select item\ItemTemplate\TempName
					Case "scp1123"
						;[Block]
						If (Not I_714\Using) And wi\GasMask <> 3 And wi\HazmatSuit <> 3 Then
							For e.Events = Each Events
								If e\EventName = "room1123" Then 
									If e\EventState = 0.0 Then
										me\LightFlash = 3.0
										PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
									EndIf
									e\EventState = 1.0
									Exit
								EndIf
							Next
						EndIf
						Return
						;[End Block]
					Case "killbat"
						;[Block]
						me\LightFlash = 1.0
						PlaySound_Strict(IntroSFX[Rand(8, 10)])
						msg\DeathMsg = SubjectName + " found dead inside SCP-914's output booth next to what appears to be an ordinary nine-volt battery. The subject is covered in severe "
						msg\DeathMsg = msg\DeathMsg + "electrical burns, and assumed to be killed via an electrical shock caused by the battery. The battery has been stored for further study."
						Kill()
						;[End Block]
					Case "scp148"
						;[Block]
						GiveAchievement(Achv148)
						;[End Block]
					Case "scp513"
						;[Block]
						GiveAchievement(Achv513)
						;[End Block]
					Case "scp860"
						;[Block]
						GiveAchievement(Achv860)
						;[End Block]
					Case "key6"
						;[Block]
						GiveAchievement(AchvKeyCard6)
						;[End Block]
					Case "keyomni"
						;[Block]
						GiveAchievement(AchvOmni)
						;[End Block]
					Case "scp005"
						;[Block]    
						GiveAchievement(Achv005)
						;[End Block]
					Case "veryfinevest"
						;[Block]
						CreateMsg("The vest is too heavy to pick up.", 6.0)
						Return
						;[End Block]
					Case "corrvest"
						;[Block]
						CreateMsg(Chr(34) + "I won't pick up that!" + Chr(34), 6.0)
						Return
						;[End Block]
					Case "firstaid", "finefirstaid", "veryfinefirstaid", "firstaid2"
						;[Block]
						item\State = 0.0
						;[End Block]
					Case "navulti"
						;[Block]
						GiveAchievement(AchvSNAV)
						;[End Block]
					Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
						;[Block]
						CanPickItem = True
						For z = 0 To MaxItemAmount - 1
							If Inventory[z] <> Null Then
								If Inventory[z]\ItemTemplate\TempName = "hazmatsuit" Lor Inventory[z]\ItemTemplate\TempName = "hazmatsuit2" Lor Inventory[z]\ItemTemplate\TempName = "hazmatsuit3" Then
									CanPickItem = 0
									Return
								ElseIf Inventory[z]\ItemTemplate\TempName = "vest" Lor Inventory[z]\ItemTemplate\TempName = "finevest"
									CanPickItem = 2
									Return
								EndIf
							EndIf
						Next
						
						If CanPickItem = 0 Then
							CreateMsg("You are not able to wear two hazmat suits at the same time.", 6.0)
							Return
						ElseIf CanPickItem = 2 Then
							CreateMsg("You are not able to wear a vest and a hazmat suit at the same time.", 6.0)
							Return
						Else
							SelectedItem = item
						EndIf
						;[End Block]
					Case "vest", "finevest"
						;[Block]
						CanPickItem = True
						For z = 0 To MaxItemAmount - 1
							If Inventory[z] <> Null Then
								If Inventory[z]\ItemTemplate\TempName = "vest" Lor Inventory[z]\ItemTemplate\TempName = "finevest" Then
									CanPickItem = 0
									Return
								ElseIf Inventory[z]\ItemTemplate\TempName = "hazmatsuit" Lor Inventory[z]\ItemTemplate\TempName = "hazmatsuit2" Lor Inventory[z]\ItemTemplate\TempName = "hazmatsuit3"
									CanPickItem = 2
									Return
								EndIf
							EndIf
						Next
						
						If CanPickItem = 0 Then
							CreateMsg("You are not able to wear two vests at the same time.", 6.0)
							Return
						ElseIf CanPickItem = 2 Then
							CreateMsg("You are not able to wear a vest and a hazmat suit at the same time.", 6.0)
							Return
						Else
							SelectedItem = item
						EndIf
						;[End Block]
				End Select
				
				If item\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[item\ItemTemplate\Sound])
				item\Picked = True
				item\Dropped = -1
				
				item\ItemTemplate\Found = True
				ItemAmount = ItemAmount + 1
				
				Inventory[n] = item
				HideEntity(item\Collider)
				Exit
			EndIf
		Next
	Else
		CreateMsg("You cannot carry any more items.", 6.0)
	EndIf
	
	CatchErrors("PickItem")
End Function

Function DropItem(item.Items, PlayDropSound% = True)
	If wi\HazmatSuit > 0 Then
		CreateMsg("You cannot drop any items while wearing a hazmat suit.", 6.0)
		Return
	EndIf
	
	CatchErrors("Uncaught (DropItem)")
	
	Local z%
	
	If PlayDropSound Then
		If item\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX[item\ItemTemplate\Sound])
	EndIf
	
	item\Dropped = 1
	
	ShowEntity(item\Collider)
	PositionEntity(item\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
	RotateEntity(item\Collider, EntityPitch(Camera), EntityYaw(Camera) + Rnd(-20.0, 20.0), 0.0)
	MoveEntity(item\Collider, 0.0, -0.1, 0.1)
	RotateEntity(item\Collider, 0.0, EntityYaw(Camera) + Rnd(-110.0, 110.0), 0.0)
	ResetEntity(item\Collider)
	
	item\Picked = False
	For z = 0 To MaxItemAmount - 1
		If Inventory[z] = item Then Inventory[z] = Null
	Next
	RemoveWearableItems(item)
	
	CatchErrors("DropItem")
End Function

Function IsItemGoodFor1162%(itt.ItemTemplates)
	Select itt\TempName
		Case "key0", "key1", "key2", "key3"
			;[Block]
			Return(True)
			;[End Block]
		Case "mastercard", "playcard", "origami", "electronics", "scp420j", "cigarette"
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
		Case "clipboard", "eyedrops", "nvg"
			;[Block]
			Return(True)
			;[End Block]
		Default
			;[Block]
			If itt\TempName <> "paper" Then
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
	Select SelectedItem\ItemTemplate\TempName
		Case "nav", "nav300", "nav310", "navulti", "paper", "oldpaper", "badge", "radio", "18vradio", "fineradio", "veryfineradio"
			;[Block]
			Return(True)
			;[End Block]
	End Select
	Return(False)
End Function

Function CanUseItem%(CanUseWithGasMask%, CanUseWithEyewear%)
	If (Not CanUseWithGasMask) And (wi\GasMask > 0 Lor I_1499\Using > 0) Then
		CreateMsg("You can't use that item while wearing a gas mask.", 6.0)
		Return(False)
	ElseIf (Not CanUseWithEyewear) And (wi\NightVision > 0 Lor wi\SCRAMBLE > 0)
		CreateMsg("You can't use that item while wearing headgear.", 6.0)
		Return(False)
	EndIf
	Return(True)
End Function

Function PreventItemOverlapping%(GasMask% = False, NVG% = False, SCP1499% = False, Helmet% = False, SCRAMBLE% = False)
	If (Not GasMask) And wi\GasMask > 0 Then
		CreateMsg("You need to take off the gas mask in order to use that item.", 6.0)
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCP1499) And I_1499\Using > 0
		CreateMsg("You need to take off SCP-1499 in order to use that item.", 6.0)
		SelectedItem = Null
		Return(True)
	ElseIf (Not NVG) And wi\NightVision > 0 Then
		CreateMsg("You need to take off the goggles in order to use that item.", 6.0)
		SelectedItem = Null
		Return(True)
	ElseIf (Not Helmet) And wi\BallisticHelmet > 0
		CreateMsg("You need to take off the helmet in order to use that item.", 6.0)
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCRAMBLE) And wi\SCRAMBLE > 0
		CreateMsg("You need to take off the gear in order to use that item.", 6.0)
		SelectedItem = Null
		Return(True)
	EndIf
	Return(False)
End Function

;TODO: IsDoubleItem function

;~IDEal Editor Parameters:
;~C#Blitz3D