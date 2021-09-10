Global BurntNote%

Global ItemAmount%, MaxItemAmount%

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
	Local it.ItemTemplates, it2.ItemTemplates
	
	it.ItemTemplates = New ItemTemplates
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
				If opt\Atmosphere Then TextureBlend(Texture, 5)
				Exit
			EndIf
		Next
		If (Not Texture) Then
			Texture = LoadTexture_Strict(TexturePath, TexFlags)
			If opt\Atmosphere Then TextureBlend(Texture, 5)
			it\TexPath = TexturePath
		EndIf
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
	CreateItemTemplate("Document SCP-035", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_035_smile.png", 0.003, 0)
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
	CreateItemTemplate("Document SCP-1162-ARC", "paper", ItemsPath + "paper.b3d", ItemsPath + "INV_paper.png", ItemsPath + "doc_1162_ARC.png", 0.003, 0)
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
	
	CreateItemTemplate("Ballistic Helmet", "helmet", ItemsPath + "ballistic_helmet.b3d", ItemsPath + "INV_ballistic_helmet.png", "", 0.018, 2)
	
	CreateItemTemplate("Ballistic Vest", "vest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.02, 2)
	CreateItemTemplate("Bulky Ballistic Vest", "veryfinevest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.025, 2)
	CreateItemTemplate("Heavy Ballistic Vest", "finevest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.022, 2)
	CreateItemTemplate("Corrosive Ballistic Vest", "corrvest", ItemsPath + "ballistic_vest.b3d", ItemsPath + "INV_ballistic_vest.png", "", 0.02, 2, ItemsPath + "ballistic_vest_corrosive.png")
	
	CreateItemTemplate("Book", "book", ItemsPath + "scp_1025.b3d", ItemsPath + "INV_book.png", "", 0.07, 0, ItemsPath + "book.png")
	
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
	
	CreateItemTemplate("Severed Hand", "hand", ItemsPath + "severed_hand.b3d", ItemsPath + "INV_severed_hand(1).png", "", 0.03, 2)
	CreateItemTemplate("Black Severed Hand", "hand2", ItemsPath + "severed_hand.b3d", ItemsPath + "INV_severed_hand(2).png", "", 0.03, 2, ItemsPath + "severed_hand(2).png")
	CreateItemTemplate("Severed Hand", "hand3", ItemsPath + "severed_hand.b3d", ItemsPath + "INV_severed_hand(3).png", "", 0.03, 2, ItemsPath + "severed_hand(3).png")
	
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
	CreateItemTemplate("Coin", "coin", ItemsPath + "coin.b3d", ItemsPath + "INV_coin_rusty.png", "", 0.0005, 3, ItemsPath + "coin_rusty.png")
	
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
			Return("1162-ARC")
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

Dim Inventory.Items(0)

Global SelectedItem.Items

Global ClosestItem.Items
Global OtherOpen.Items = Null

Function CreateItem.Items(Name$, TempName$, x#, y#, z#, R% = 0, G% = 0, B% = 0, A# = 1.0, InvSlots% = 0)
	CatchErrors("Uncaught (CreateItem)")
	
	Local i.Items, it.ItemTemplates
	
	Name = Lower(Name)
	TempName = Lower(TempName)
	
	i.Items = New Items
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
	
	i\Dist = EntityDistanceSquared(me\Collider, i\Collider)
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
		If Inventory(n) = i
			Inventory(n) = Null
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
			SetAnimTime(item\Model, 4.0)
			;[End Block]
		Case "vest", "finevest"
			;[Block]
			wi\BallisticVest = 0
			;[End Block]
		Case "helmet"
			;[Block]
			wi\BallisticHelmet = False
			;[End Block]
		Case "nvg", "supernvg", "finenvg"
			;[Block]
			If wi\NightVision > 0 Then opt\CameraFogFar = opt\StoredCameraFogFar : wi\NightVision = 0
			;[End Block]
		Case "scp714"
			;[Block]
			I_714\Using = False
			;[End Block]
		Case "scp1499", "super1499"
			;[Block]
			I_1499\Using = 0
			;[End Block]
		Case "scp427"
			;[Block]
			I_427\Using = False
			;[End Block]
		Case "scramble"
			;[Block]
			wi\SCRAMBLE = False
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
	
	Local i.Items, i2.Items, np.NPCs
	Local xTemp#, yTemp#, zTemp#
	Local Temp%, n%
	Local Pick%, ed#
	Local HideDist# = PowTwo(HideDistance / 2.0)
	Local DeletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked) Then
			If i\DistTimer < MilliSecs2() Then
				i\Dist = EntityDistanceSquared(Camera, i\Collider)
				i\DistTimer = MilliSecs2() + 700
				If i\Dist < HideDist Then ShowEntity(i\Collider)
			EndIf
			
			If i\Dist < HideDist Then
				ShowEntity(i\Collider)
				If i\Dist < 1.44 Then
					If ClosestItem = Null Then
						If EntityInView(i\Model, Camera) Then
							If EntityVisible(i\Collider, Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					ElseIf ClosestItem = i Lor i\Dist < EntityDistanceSquared(Camera, ClosestItem\Collider) Then 
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
							i\DropSpeed = i\DropSpeed - (0.0004 * fps\Factor[0])
							TranslateEntity(i\Collider, i\xSpeed * fps\Factor[0], i\DropSpeed * fps\Factor[0], i\zSpeed * fps\Factor[0])
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
				
				If i\Dist < HideDist * 0.04 Then
					For i2.Items = Each Items
						If i <> i2 And (Not i2\Picked) And i2\Dist < HideDist * 0.04 Then
							xTemp = EntityX(i2\Collider, True) - EntityX(i\Collider, True)
							yTemp = EntityY(i2\Collider, True) - EntityY(i\Collider, True)
							zTemp = EntityZ(i2\Collider, True) - EntityZ(i\Collider, True)
							
							ed = (xTemp ^ 2) + (zTemp ^ 2)
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
		CreateMsg("You cannot pick up any items while wearing a hazmat suit.")
		Return
	EndIf
	If InvOpen Lor I_294\Using Lor OtherOpen <> Null Lor SelectedDoor <> Null Lor SelectedScreen <> Null Then Return
	
	CatchErrors("Uncaught (PickItem)")
	
	Local e.Events
	Local n% = 0, z%
	Local CanPickItem = 1
	Local FullINV% = True
	
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = Null Then
			FullINV = False
			Exit
		EndIf
	Next
	
	If (Not FullINV) Then
		For n = 0 To MaxItemAmount - 1
			If Inventory(n) = Null Then
				Select item\ItemTemplate\TempName
					Case "scp1123"
						;[Block]
						Use1123(False)
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
						CreateMsg("The vest is too heavy to pick up.")
						Return
						;[End Block]
					Case "corrvest"
						;[Block]
						CreateMsg(Chr(34) + "I won't pick up that!" + Chr(34))
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
							If Inventory(z) <> Null Then
								If Inventory(z)\ItemTemplate\TempName = "hazmatsuit" Lor Inventory(z)\ItemTemplate\TempName = "hazmatsuit2" Lor Inventory(z)\ItemTemplate\TempName = "hazmatsuit3" Then
									CanPickItem = 0
									Return
								ElseIf Inventory(z)\ItemTemplate\TempName = "vest" Lor Inventory(z)\ItemTemplate\TempName = "finevest"
									CanPickItem = 2
									Return
								EndIf
							EndIf
						Next
						
						If CanPickItem = 0 Then
							CreateMsg("You are not able to wear two hazmat suits at the same time.")
							Return
						ElseIf CanPickItem = 2 Then
							CreateMsg("You are not able to wear a vest and a hazmat suit at the same time.")
							Return
						Else
							SelectedItem = item
						EndIf
						;[End Block]
					Case "vest", "finevest"
						;[Block]
						CanPickItem = True
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\ItemTemplate\TempName = "vest" Lor Inventory(z)\ItemTemplate\TempName = "finevest" Then
									CanPickItem = 0
									Return
								ElseIf Inventory(z)\ItemTemplate\TempName = "hazmatsuit" Lor Inventory(z)\ItemTemplate\TempName = "hazmatsuit2" Lor Inventory(z)\ItemTemplate\TempName = "hazmatsuit3"
									CanPickItem = 2
									Return
								EndIf
							EndIf
						Next
						
						If CanPickItem = 0 Then
							CreateMsg("You are not able to wear two vests at the same time.")
							Return
						ElseIf CanPickItem = 2 Then
							CreateMsg("You are not able to wear a vest and a hazmat suit at the same time.")
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
				
				Inventory(n) = item
				HideEntity(item\Collider)
				Exit
			EndIf
		Next
	Else
		CreateMsg("You cannot carry any more items.")
	EndIf
	
	CatchErrors("PickItem")
End Function

Function DropItem(item.Items, PlayDropSound% = True)
	If wi\HazmatSuit > 0 Then
		CreateMsg("You cannot drop any items while wearing a hazmat suit.")
		Return
	EndIf
	
	CatchErrors("Uncaught (DropItem)")
	
	Local n%
	
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
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = item Then
			Inventory(n) = Null
			ItemAmount = ItemAmount - 1
			Exit
		EndIf
	Next
	
	RemoveWearableItems(item)
	
	CatchErrors("DropItem")
End Function

Function IsItemGoodFor1162ARC%(itt.ItemTemplates)
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
		CreateMsg("You can't use that item while wearing a gas mask.")
		Return(False)
	ElseIf (Not CanUseWithEyewear) And (wi\NightVision > 0 Lor wi\SCRAMBLE)
		CreateMsg("You can't use that item while wearing headgear.")
		Return(False)
	EndIf
	Return(True)
End Function

; ~ Maybe re-work?
Function PreventItemOverlapping%(GasMask% = False, NVG% = False, SCP1499% = False, Helmet% = False, SCRAMBLE% = False)
	If (Not GasMask) And wi\GasMask > 0 Then
		CreateMsg("You need to take off the gas mask in order to use that item.")
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCP1499) And I_1499\Using > 0
		CreateMsg("You need to take off SCP-1499 in order to use that item.")
		SelectedItem = Null
		Return(True)
	ElseIf (Not NVG) And wi\NightVision > 0 Then
		CreateMsg("You need to take off the goggles in order to use that item.")
		SelectedItem = Null
		Return(True)
	ElseIf (Not Helmet) And wi\BallisticHelmet
		CreateMsg("You need to take off the helmet in order to use that item.")
		SelectedItem = Null
		Return(True)
	ElseIf (Not SCRAMBLE) And wi\SCRAMBLE
		CreateMsg("You need to take off the gear in order to use that item.")
		SelectedItem = Null
		Return(True)
	EndIf
	Return(False)
End Function

Function IsDoubleItem%(Variable, ID, Msg$)
	If Variable > 0 And Variable <> ID Then
		CreateMsg("You are not able to wear two " + Msg + " at the same time.")
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

Function Use914(item.Items, Setting%, x#, y#, z#)
	me\RefinedItems = me\RefinedItems + 1
	
	Local it.Items, it2.Items, it3.Items, it4.Items, it5.Items, d.Decals, n.NPCs
	Local Remove% = True, i%
	
	Select item\ItemTemplate\TempName
		Case "gasmask", "gasmask3"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(50) = 1 Then
						it2.Items = CreateItem("SCP-1499", "scp1499", x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Gas Mask", "supergasmask", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "scp1499", "super1499"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Gas Mask", "gasmask", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("SCP-1499", "super1499", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					n.NPCs = CreateNPC(NPCType1499_1, x, y, z)
					n\State = 1.0 : n\State3 = 1.0
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundCHN = PlaySound2(n\Sound, Camera, n\Collider, 20.0)
					;[End Block]
			End Select
			;[End Block]
		Case "vest"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Corrosive Ballistic Vest", "corrvest", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(10) = 1 Then
						it2.Items = CreateItem("Ballistic Helmet", "helmet", x, y, z)
					Else
						Remove = False
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Bulky Ballistic Vest", "veryfinevest", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "helmet"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Ballistic Vest", "vest", x, y, z)
					;[End Block]	
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "clipboard"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case COARSE
					;[Block]
					If item\InvSlots > 5 Then
						item\InvSlots = item\InvSlots - 5
						ClearSecondInv(item, item\InvSlots)
					ElseIf item\InvSlots = 5
						item\InvSlots = 1
						ClearSecondInv(item, 1)
					Else
						d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.12, 0.8)
						ClearSecondInv(item, 0)
					EndIf
					Remove = False
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					If item\InvSlots = 1 Then
						item\InvSlots = 5
					Else
						item\InvSlots = Min(20.0, item\InvSlots + 5.0)
					EndIf
					Remove = False
					;[End Block]
				Case VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case "wallet"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case COARSE
					;[Block]
					If item\InvSlots > 5 Then
						item\InvSlots = item\InvSlots - 5
						ClearSecondInv(item, item\InvSlots)
					ElseIf item\InvSlots = 5
						item\InvSlots = 1
						ClearSecondInv(item, 1)
					Else
						d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.12, 0.8)
						ClearSecondInv(item, 0)
					EndIf
					Remove = False
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					If item\InvSlots = 1 Then
						item\InvSlots = 5
					Else
						item\InvSlots = Min(20.0, item\InvSlots + 5.0)
					EndIf
					Remove = False
					;[End Block]
				Case VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case "nvg", "supernvg", "finenvg", "scramble"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("SCRAMBLE Gear", "scramble", x, y, z)
					it2\State = Rnd(0.0, 1000.0)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Night Vision Goggles", "finenvg", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(5) = 1 Then
						it2.Items = CreateItem("SCRAMBLE Gear", "scramble", x, y, z)
						it2\State = Rnd(0.0, 1000.0)
					Else
						it2.Items = CreateItem("Night Vision Goggles", "supernvg", x, y, z)
						it2\State = Rnd(0.0, 1000.0)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "electronics"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					Select Rand(3)
						Case 1
							;[Block]
							it2.Items = CreateItem("Radio Transceiver", "radio", x, y, z)
							it2\State = Rnd(0.0, 100.0)
							;[End Block]
						Case 2
							;[Block]
							If Rand(3) = 1 Then
								it2.Items = CreateItem("S-NAV 300 Navigator", "nav300", x, y, z)
							Else
								it2.Items = CreateItem("S-NAV Navigator", "nav", x, y, z)
								it2\State = Rnd(0.0, 100.0)
							EndIf
							;[End Block]
						Case 3
							;[Block]
							it2.Items = CreateItem("Night Vision Goggles", "nvg", x, y, z)
							it2\State = Rnd(0.0, 1000.0)
							;[End Block]
					End Select
					;[End Block]
				Case VERYFINE
					;[Block]
					Select Rand(3)
						Case 1
							;[Block]
							If Rand(3) = 1 Then
								it2.Items = CreateItem("Radio Transceiver", "fineradio", x, y, z)
							Else
								it2.Items = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
							EndIf
							;[End Block]
						Case 2
							;[Block]
							If Rand(2) = 1 Then
								it2.Items = CreateItem("S-NAV Navigator Ultimate", "navulti", x, y, z)
							Else
								it2.Items = CreateItem("S-NAV 310 Navigator", "nav310", x, y, z)
								it2\State = Rnd(0.0, 100.0)
							EndIf
							;[End Block]
						Case 3
							;[Block]
							Select Rand(3)
								Case 1
									;[Block]
									it2.Items = CreateItem("Night Vision Goggles", "finenvg", x, y, z)
									;[End Block]
								Case 2
									;[Block]
									it2.Items = CreateItem("Night Vision Goggles", "supernvg", x, y, z)
									it2\State = Rnd(0.0, 1000.0)
									;[End Block]
								Case 3
									;[Block]
									it2.Items = CreateItem("SCRAMBLE Gear", "scramble", x, y, z)
									it2\State = Rnd(0.0, 1000.0)
									;[End Block]
							End Select
							;[End Block]
					End Select
					;[End Block]
			End Select
			;[End Block]
		Case "scp148"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2.Items = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
		Case "scp148ingot"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2.Items = CreateItem("SCP-148 Ingot", "scp148ingot", x, y, z)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					For it.Items = Each Items
						If it <> item And it\Collider <> 0 And (Not it\Picked) Then
							If DistanceSquared(EntityX(it\Collider, True), x, EntityZ(it\Collider, True), z) < PowTwo(180.0 * RoomScale)
								Select it\ItemTemplate\TempName
									Case "gasmask", "supergasmask"
										;[Block]
										RemoveItem(it)
										it2.Items = CreateItem("Heavy Gas Mask", "gasmask3", x, y, z)
										Exit
										;[End Block]
									Case "vest"
										;[Block]
										RemoveItem(it)
										it2.Items = CreateItem("Heavy Ballistic Vest", "finevest", x, y, z)
										Exit
										;[End Block]
									Case "hazmatsuit", "hazmatsuit2"
										;[Block]
										RemoveItem(it)
										it2.Items = CreateItem("Heavy Hazmat Suit", "hazmatsuit3", x, y, z)
										Exit
										;[End Block]
								End Select
							EndIf
						EndIf
					Next
					
					If it2 = Null Then
						it2.Items = CreateItem("Metal Panel", "scp148", x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "hand", "hand2", "hand3"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(3, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					If item\ItemTemplate\TempName = "hand" Then
						If Rand(2) = 1 Then
							it2.Items = CreateItem("Black Severed Hand", "hand2", x, y, z)
						Else
							it2.Items = CreateItem("Severed Hand", "hand3", x, y, z)
						EndIf
					ElseIf item\ItemTemplate\TempName = "hand2"
						If Rand(2) = 1 Then
							it2.Items = CreateItem("Severed Hand", "hand", x, y, z)
						Else
							it2.Items = CreateItem("Severed Hand", "hand3", x, y, z)
						EndIf
					Else
						If Rand(2) = 1 Then
							it2.Items = CreateItem("Severed Hand", "hand", x, y, z)
						Else
							it2.Items = CreateItem("Black Severed Hand", "hand2", x, y, z)
						EndIf
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "firstaid", "firstaid2", "finefirstaid", "veryfinefirstaid"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.12)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1 Then
						it2.Items = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					Else
						it2.Items = CreateItem("First Aid Kit", "firstaid", x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Strange Bottle", "veryfinefirstaid", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "key0", "key1", "key2", "key3", "key4", "key5", "key6"
			;[Block]
			Local Level% = Right(item\ItemTemplate\TempName, 1)
			
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case COARSE
					;[Block]
					If Level = 0 Then
						d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					Else
						it2.Items = CreateItem("Level " + (Level - 1) + " Key Card", "key" + (Level - 1), x, y, z)
					EndIf
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Playing Card", "playcard", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					Select Level
						Case 0
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(6) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(5) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(4) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 1
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(5) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(4) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(3) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 2
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 3 Key Card", "key3", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(3) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(2) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 3 Key Card", "key3", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 3
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(10) = 1 Then
										it2.Items = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2.Items = CreateItem("Playing Card", "playcard", x, y, z)	
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(15) = 1 Then
										it2.Items = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2.Items = CreateItem("Playing Card", "playcard", x, y, z)	
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(20) = 1 Then
										it2.Items = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2.Items = CreateItem("Playing Card", "playcard", x, y, z)	
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(25) = 1 Then
										it2.Items = CreateItem("Level 4 Key Card", "key4", x, y, z)
									Else
										it2.Items = CreateItem("Playing Card", "playcard", x, y, z)	
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 4
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									it2.Items = CreateItem("Level 5 Key Card", "key5", x, y, z)
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(3) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(2) = 1 Then
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									Else
										it2.Items = CreateItem("Level 5 Key Card", "key5", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 5
							;[Block]
							Local CurrAchvAmount% = 0
							
							For i = 0 To MAXACHIEVEMENTS - 1
								If achv\Achievement[i] = True
									CurrAchvAmount = CurrAchvAmount + 1
								EndIf
							Next
							
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(10) = 1 Then
											it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
										EndIf
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(15) = 1 Then
											it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
										EndIf
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(20) = 1 Then
											it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
										EndIf
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(0, ((MAXACHIEVEMENTS - 1) * 6) - ((CurrAchvAmount - 1) * 3)) = 0 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										If Rand(25) = 1 Then
											it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
										Else
											it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
										EndIf
									EndIf
									;[End Block]
							End Select
							;[End Block]
						Case 6
							;[Block]
							Select SelectedDifficulty\OtherFactors
								Case EASY
									;[Block]
									If Rand(3) = 1 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									EndIf
									;[End Block]
								Case NORMAL
									;[Block]
									If Rand(4) = 1 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									EndIf
									;[End Block]
								Case HARD
									;[Block]
									If Rand(5) = 1 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									EndIf
									;[End Block]
								Case EXTREME
									;[Block]
									If Rand(6) = 1 Then
										it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
									Else
										it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
									EndIf
									;[End Block]
							End Select
							;[End Block]
					End Select
					;[End Block]
				Case VERYFINE
					;[Block]
					CurrAchvAmount = 0
					For i = 0 To MAXACHIEVEMENTS - 1
						If achv\Achievement[i] = True
							CurrAchvAmount = CurrAchvAmount + 1
						EndIf
					Next
					
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 3) - ((CurrAchvAmount - 1) * 3)) = 0
								it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(20) = 1 Then
									it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case NORMAL
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 4) - ((CurrAchvAmount - 1) * 3)) = 0
								it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(25) = 1 Then
									it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case HARD
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 5) - ((CurrAchvAmount - 1) * 3)) = 0
								it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(30) = 1 Then
									it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
								EndIf
							EndIf
							;[End Block]
						Case EXTREME
							;[Block]
							If Rand(0, ((MAXACHIEVEMENTS - 1) * 6) - ((CurrAchvAmount - 1) * 3)) = 0
								it2.Items = CreateItem("Key Card Omni", "keyomni", x, y, z)
							Else
								If Rand(35) = 1 Then
									it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
								Else
									it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
								EndIf
							EndIf
							;[End Block]
					End Select
					;[End Block]
			End Select
			;[End Block]
		Case "keyomni"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1 Then
						it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
					Else
						it2.Items = CreateItem("Playing Card", "playcard", x, y, z)			
					EndIf	
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					Select SelectedDifficulty\OtherFactors
						Case EASY
							;[Block]
							If Rand(3) = 1 Then
								it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
							EndIf
							;[End Block]
						Case NORMAL
							;[Block]
							If Rand(4) = 1 Then
								it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
							EndIf
							;[End Block]
						Case HARD
							;[Block]
							If Rand(5) = 1 Then
								it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
							EndIf
							;[End Block]
						Case EXTREME
							;[Block]
							If Rand(6) = 1 Then
								it2.Items = CreateItem("Level 6 Key Card", "key6", x, y, z)
							Else
								it2.Items = CreateItem("Mastercard", "mastercard", x, y, z)
							EndIf
							;[End Block]
					End Select
					;[End Block]
			End Select		
			;[End Block]
		Case "playcard", "coin", "25ct"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "mastercard"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Quarter", "25ct", x, y, z)
					
					If Rand(2) = 1 Then
						it3.Items = CreateItem("Quarter", "25ct", x, y, z)
						EntityType(it3\Collider, HIT_ITEM)
					EndIf
					
					If Rand(3) = 1 Then
						it4.Items = CreateItem("Quarter", "25ct", x, y, z)
						EntityType(it4\Collider, HIT_ITEM)
					EndIf
					
					If Rand(4) = 1 Then
						it5.Items = CreateItem("Quarter", "25ct", x, y, z)
						EntityType(it5\Collider, HIT_ITEM)
					EndIf
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Level 0 Key Card", "key0", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Level 1 Key Card", "key1", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Level 2 Key Card", "key2", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "nav", "nav300", "nav310", "navulti"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", "electronics", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("S-NAV Navigator", "nav", x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case FINE
					;[Block]
					If Rand(3) = 1 Then
						it2.Items = CreateItem("S-NAV 300 Navigator", "nav300", x, y, z)
					Else
						it2.Items = CreateItem("S-NAV 310 Navigator", "nav310", x, y, z)
						it2\State = Rnd(0.0, 100.0)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("S-NAV Navigator Ultimate", "navulti", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "radio", "18vradio", "fineradio", "veryfineradio"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					it2.Items = CreateItem("Electronical Components", "electronics", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Radio Transceiver", "18vradio", x, y, z)
					it2\State = Rnd(0.0, 100.0)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Radio Transceiver", "fineradio", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Radio Transceiver", "veryfineradio", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "scp513"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					PlaySound_Strict(LoadTempSound("SFX\SCP\513\914Refine.ogg"))
					For n.NPCs = Each NPCs
						If n\NPCType = NPCType513_1 Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("SCP-513", "scp513", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "scp420j", "cigarette", "joint", "scp420s"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Cigarette", "cigarette", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Joint", "joint", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Smelly Joint", "scp420s", x + 1.5, y + 0.5, z + 1.0)
					;[End Block]
			End Select
			;[End Block]
		Case "badbat"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("9V Battery", "bat", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("18V Battery", "finebat", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "bat"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("4.5V Battery", "badbat", x, y, z)
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("18V Battery", "finebat", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(5) = 1 Then
						it2.Items = CreateItem("999V Battery", "superbat", x, y, z)
					Else
						it2.Items = CreateItem("Strange Battery", "killbat", x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "finebat"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					it2.Items = CreateItem("4.5V Battery", "badbat", x, y, z)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("9V Battery", "bat", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					Remove = False
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("18V Battery", "finebat", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(3) = 1 Then
						it2.Items = CreateItem("999V Battery", "superbat", x, y, z)
					Else
						it2.Items = CreateItem("Strange Battery", "killbat", x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "superbat"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					it2.Items = CreateItem("4.5V Battery", "badbat", x, y, z)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("9V Battery", "bat", x, y, z)
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Strange Battery", "killbat", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "eyedrops", "eyedrops2", "fineeyedrops", "supereyedrops"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(2) = 1 Then
						it2.Items = CreateItem("ReVision Eyedrops", "eyedrops", x, y, z)
					Else
						it2.Items = CreateItem("RedVision Eyedrops", "eyedrops2", x, y, z)
					EndIf
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Eyedrops", "fineeyedrops", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Eyedrops", "supereyedrops", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "hazmatsuit", "hazmatsuit3"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Hazmat Suit", "hazmatsuit", x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Hazmat Suit", "hazmatsuit2", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "hazmatsuit2"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Hazmat Suit", "hazmatsuit", x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Syringe", "syringeinf", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "syringe"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Small First Aid Kit", "finefirstaid", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Syringe", "finesyringe", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(3) = 1 Then
						it2.Items = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					Else
						it2.Items = CreateItem("Syringe", "syringeinf", x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "finesyringe"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("First Aid Kit", "firstaid", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					If Rand(3) = 1 Then
						it2.Items = CreateItem("Syringe", "veryfinesyringe", x, y, z)
					Else
						it2.Items = CreateItem("Syringe", "syringeinf", x, y, z)
					EndIf
					;[End Block]
			End Select
			;[End Block]
		Case "veryfinesyringe"
			;[Block]
			Select Setting
				Case ROUGH, COARSE, ONETOONE
					;[Block]
					it2.Items = CreateItem("Electronical Components", "electronics", x, y, z)	
					;[End Block]
				Case FINE
					it2.Items = CreateItem("Syringe", "syringeinf", x, y, z)
				Case VERYFINE
					;[Block]
					If Rand(2) = 1 Then
						n.NPCs = CreateNPC(NPCType008_1, x, y, z)
						n\State = 2.0
					Else
						it2.Items = CreateItem("Syringe", "syringeinf", x, y, z)
					EndIf
					;[End Block]
			End Select
		Case "syringeinf"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90.0, Rnd(360.0), 0.0, 0.07)
					;[End Block]
				Case ONETOONE
					;[Block]
					n.NPCs = CreateNPC(NPCType008_1, x, y, z)
					n\State = 2.0
					;[End Block]	
				Case FINE
					;[Block]
					it2.Items = CreateItem("Syringe", "syringe", x, y, z)
					;[End Block]
				Case VERYFINE
					;[Block]
					If Rand(4) = 1 Then
						it2.Items = CreateItem("Blue First Aid Kit", "firstaid2", x, y, z)
					Else
						it2.Items = CreateItem("Syringe", "finesyringe", x, y, z)
					EndIf
					;[End Block]
			End Select
		Case "scp500pill", "scp500pilldeath", "pill"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Pill", "pill", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					Local NO427Spawn% = False
					
					For it3.Items = Each Items
						If it3\ItemTemplate\TempName = "scp427" Then
							NO427Spawn = True
							Exit
						EndIf
					Next
					If (Not NO427Spawn) Then
						it2.Items = CreateItem("SCP-427", "scp427", x, y, z)
					Else
						it2.Items = CreateItem("Upgraded Pill", "scp500pilldeath", x, y, z)
					EndIf
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Upgraded Pill", "scp500pilldeath", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "origami"
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", "paper", x, y, z)
					;[End Block]
				Case ONETOONE, VERYFINE, FINE
					;[Block]
					it2.Items = CreateItem("Document SCP-" + GetRandDocument(), "paper", x, y, z)
					;[End Block]
			End Select
		Case "cup"
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Cup", "cup", x, y, z)
					it2\Name = item\Name
					it2\R = 255.0 - item\R : it2\G = 255.0 - item\G : it2\B = 255.0 - item\B
					;[End Block]
				Case FINE
					;[Block]
					it2.Items = CreateItem("Cup", "cup", x, y, z)
					it2\Name = item\Name : it2\State = 1.0
					it2\R = Min(item\R * Rnd(0.9, 1.1), 255.0) : it2\G = Min(item\G * Rnd(0.9, 1.1), 255.0) : it2\B = Min(item\B * Rnd(0.9, 1.1), 255.0)
					;[End Block]
				Case VERYFINE
					;[Block]
					it2.Items = CreateItem("Cup", "cup", x, y, z)
					it2\Name = item\Name : it2\State = Max(it2\State * 2.0, 2.0)	
					it2\R = Min(item\R * Rnd(0.5, 1.5), 255.0) : it2\G = Min(item\G * Rnd(0.5, 1.5), 255.0) : it2\B = Min(item\B * Rnd(0.5, 1.5), 255.0)
					If Rand(5) = 1 Then me\ExplosionTimer = 135.0
					;[End Block]
			End Select	
			;[End Block]
		Case "paper"
			;[Block]
			Select Setting
				Case ROUGH
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case COARSE
					;[Block]
					it2.Items = CreateItem("Blank Paper", "paper", x, y, z)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Document SCP-" + GetRandDocument(), "paper", x, y, z)
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					it2.Items = CreateItem("Origami", "origami", x, y, z)
					;[End Block]
			End Select
			;[End Block]
		Case "scp1025"
			Remove = False
			Select Setting
				Case ROUGH
					;[Block]
					If item\State2 > 0.0 Then
						item\State2 = -1.0
					Else
						d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
						Remove = True
					EndIf
					;[End Block]
				Case COARSE
					;[Block]
					item\State2 = Max(-1.0, item\State2 - 1.0)
					;[End Block]
				Case ONETOONE
					;[Block]
					it2.Items = CreateItem("Book", "book", x, y, z)
					;[End Block]
				Case FINE
					;[Block]
					item\State2 = Min(1.0, item\State2 + 1.0)
					;[End Block]
				Case VERYFINE
					;[Block]
					item\State2 = 2.0
					;[End Block]
			End Select
		Case "book"
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE
					;[Block]
					If Rand(3) = 1 Then
						it2.Items = CreateItem("SCP-1025", "scp1025", x, y, z) ; ~ I know that this can be exploited to get a SCP-1025 reset, but this effort makes it seem fair to me -- Salvage
					Else
						Remove = False
					EndIf
					;[End Block]
				Case FINE, VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
		Default
			;[Block]
			Select Setting
				Case ROUGH, COARSE
					;[Block]
					d.Decals = CreateDecal(0, x, 8.0 * RoomScale + 0.010, z, 90.0, Rnd(360.0), 0.0, 0.2, 0.8)
					;[End Block]
				Case ONETOONE, FINE, VERYFINE
					;[Block]
					Remove = False
					;[End Block]
			End Select
			;[End Block]
	End Select
	
	If Remove Then
		RemoveItem(item)
	Else
		PositionEntity(item\Collider, x, y, z)
		ResetEntity(item\Collider)
	EndIf
	
	If it2 <> Null Then EntityType(it2\Collider, HIT_ITEM)
End Function

; ~ Made a function for SCP-1123 so we don't have to use duplicate code (since picking it up and using it does the same thing)
Function Use1123%(Use% = False)
	; ~ Temp:	; ~		False: Stay alive
	; ~		True: Die
	
	; ~ State:	; ~		False: Pick up SCP-1123
	; ~		True: Use SCP-1123
	
	Local e.Events
	Local Temp%
	
	If (Not I_714\Using) And wi\GasMask <> 3 And wi\HazmatSuit <> 3 Then
		me\LightFlash = 3.0
		PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
		
		Temp = True ; ~ The event occured (kill)
		
		For e.Events = Each Events
			If e\EventID = e_cont2_1123 Then
				If PlayerRoom = e\room Then
					If e\EventState < 1.0 Then ; ~ The event didn't occur and the player is in the room (starts the event)
						e\EventState = 1.0
						Temp = False
						Exit
					Else
						Temp = True ; ~ The player is currently in the event (kill)
					EndIf
				Else
					Temp = True ; ~ The event didn't occur and the player isn't in the room (kill)
				EndIf
			EndIf
		Next
	Else
		If Use Then
			CreateMsg("You touched the skull, but nothing happened.")
		EndIf
	EndIf
	
	If Temp Then
		msg\DeathMSG = SubjectName + " was shot dead after attempting to attack a member of Nine-Tailed Fox. Surveillance tapes show that the subject had been"
		msg\DeathMSG = msg\DeathMSG + " wandering around the site approximately 9 minutes prior, shouting the phrase " + Chr(34) + "get rid of the four pests" + Chr(34)
		msg\DeathMSG = msg\DeathMSG + " in chinese. SCP-1123 was found in [REDACTED] nearby, suggesting the subject had come into physical contact with it."
		Kill()
		Return
	EndIf
End Function

; ~ Key Items Constants
;[Block]
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
Const KEY_MISC% = 0
;[End Block]

; ~ Only for "UseDoor" function
Function GetUsingItem(item.Items)
	Select item\ItemTemplate\TempName
		Case "key6"
			;[Block]
			Return(KEY_CARD_6)
		Case "key0"
			;[Block]
			Return(KEY_CARD_0)
			;[End Block]
		Case "key1"
			;[Block]
			Return(KEY_CARD_1)
			;[End Block]
		Case "key2"
			;[Block]
			Return(KEY_CARD_2)
			;[End Block]
		Case "key3"
			;[Block]
			Return(KEY_CARD_3)
			;[End Block]
		Case "key4"
			;[Block]
			Return(KEY_CARD_4)
			;[End Block]
		Case "key5"
			;[Block]
			Return(KEY_CARD_5)
			;[End Block]
		Case "keyomni"
			;[Block]
			Return(KEY_CARD_OMNI)
			;[End Block]
		Case "scp005"
			;[Block]
			Return(KEY_005)
			;[End Block]
		Case "hand"
			;[Block]
			Return(KEY_HAND_WHITE)
			;[End Block]
		Case "hand2"
			;[Block]
			Return(KEY_HAND_BLACK)
			;[End Block]
		Case "hand3"
			;[Block]
			Return(KEY_HAND_YELLOW)
			;[End Block]
		Case "scp860"
			;[Block]
			Return(KEY_860)
			;[End Block]
		Default
			;[Block]
			Return(KEY_MISC)
			;[End Block]
	End Select
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D