Global BurntNote%

Global ItemAmount%
Dim Inventory.Items(MaxItemAmount + 1)
Global InvSelect%, SelectedItem.Items

Global ClosestItem.Items

Global LastItemID%

Type ItemTemplates
	Field Name$
	Field TempName$
	Field Sound%
	Field Found%
	Field OBJ%, OBJPath$, ParentOBJPath$
	Field InvImg%, InvImg2%, InvImgPath$
	Field ImgPath$, Img%
	Field IsAnim%
	Field Scale#
	Field Tex%, TexPath$
End Type 

Function CreateItemTemplate.ItemTemplates(Name$, TempName$, OBJPath$, InvImgPath$, ImgPath$, Scale#, SoundID%, TexturePath$ = "", InvImgPath2$ = "", Anim% = 0, TexFlags% = 9)
	Local it.ItemTemplates = New ItemTemplates
	
	; ~ If another item shares the same object, copy it
	For it2.ItemTemplates = Each ItemTemplates
		If it2\OBJPath = OBJPath And it2\OBJ <> 0 Then it\OBJ = CopyEntity(it2\OBJ) : it\ParentOBJPath = it2\OBJPath : Exit
	Next
	
	If it\OBJ = 0 Then
		If Anim <> 0 Then
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
		If Texture = 0 Then Texture = LoadTexture_Strict(TexturePath, TexFlags) : it\TexPath = TexturePath
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
	If it\InvImg = 0 Then
		it\InvImg = LoadImage_Strict(InvImgPath)
		it\InvImgPath = InvImgPath
		MaskImage(it\InvImg, 255, 0, 255)
	EndIf
	
	If (InvImgPath2 <> "") Then
		If it\InvImg2 = 0 Then
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

Function InitItemTemplates()
	Local it.ItemTemplates,it2.ItemTemplates
	
	; ~ [PAPER]
	
	CreateItemTemplate("Document SCP-008", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc008.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-012", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc012.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-035", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc035.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-049", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc049.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-079", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc079.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-096", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc096.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-106", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc106.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-173", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc173.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-372", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc372.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-500", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc500.png", 0.003, 0)
	CreateItemTemplate("Document SCP-513", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc513.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-682", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc682.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-714", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc714.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-860", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc_860.png", 0.003, 0)
	CreateItemTemplate("Document SCP-860-1", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc8601.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-895", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc_895.png", 0.003, 0)
	CreateItemTemplate("Document SCP-939", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc939.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-966", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc966.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-970", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc970.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-1048", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc1048.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-1123", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc1123.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-1162", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc1162.jpg", 0.003, 0)
	CreateItemTemplate("Document SCP-1499", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc1499.png", 0.003, 0)
	
	CreateItemTemplate("Incident Report SCP-1048-A", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc1048a.jpg", 0.003, 0)
	
	CreateItemTemplate("SCP-035 Addendum", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc035ad.jpg", 0.003, 0)
	
	CreateItemTemplate("SCP-093 Recovered Materials", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc093rm.jpg", 0.003, 0)
	
	CreateItemTemplate("Addendum: 5/14 Test Log", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docRAND2.jpg", 0.003, 0)
	
	CreateItemTemplate("Class D Orientation Leaflet", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docORI.jpg", 0.003, 0)
	
	CreateItemTemplate("Disciplinary Hearing DH-S-4137-17092", "oldpaper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "dh.s", 0.003, 0)
	
	CreateItemTemplate("Document", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docRAND3.jpg", 0.003, 0)
	
	CreateItemTemplate("Incident Report SCP-106-0204", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docIR106.jpg", 0.003, 0)
	
	CreateItemTemplate("Mobile Task Forces", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docMTF.jpg", 0.003, 0)
	
	CreateItemTemplate("Note from Daniel", "paper", ItemsPath + "note.x", ItemsPath + "INVnote2.jpg", ItemsPath + "docdan.jpg", 0.0025, 0)
	
	CreateItemTemplate("Nuclear Device Document", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docNDP.jpg", 0.003, 0)
	
	CreateItemTemplate("Object Classes", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docOBJC.jpg", 0.003, 0)
	
	CreateItemTemplate("Recall Protocol RP-106-N", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docRP.jpg", 0.0025, 0)
	
	CreateItemTemplate("Research Sector-02 Scheme", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docmap.jpg", 0.003, 0)
	
	CreateItemTemplate("Security Clearance Levels", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docSC.jpg", 0.003, 0)
	
	CreateItemTemplate("Sticky Note", "paper", ItemsPath + "note.x", ItemsPath + "INVnote2.jpg", ItemsPath + "note682.jpg", 0.0025, 0)
	
	CreateItemTemplate("The Modular Site Project", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "docMSP.jpg", 0.003, 0)
	
	CreateItemTemplate("Blood-stained Note", "paper", ItemsPath + "paper.x", ItemsPath + "INV_note_bloody.png", ItemsPath + "docL3.jpg", 0.0025, 0, ItemsPath + "note_bloody.png")
	
	it = CreateItemTemplate("Burnt Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVbn.jpg", ItemsPath + "bn.it", 0.003, 0, ItemsPath + "BurntNoteTexture.jpg")
	it\Img = BurntNote
	
	CreateItemTemplate("Document SCP-427", "paper", ItemsPath + "paper.x", ItemsPath + "INV_paper_bloody.png", ItemsPath + "doc427.jpg", 0.003, 0, ItemsPath + "paper_bloody.png")
	
	CreateItemTemplate("Drawing", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "doc1048.jpg", 0.003, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Dr. Allok's Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVpaper.jpg", ItemsPath + "doc106_2.jpg", 0.006, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Dr. L's Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "docL1.jpg", 0.0025, 0, ItemsPath + "notetexture.jpg")
	CreateItemTemplate("Dr L's Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "docL2.jpg", 0.0025, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Dr. L's Burnt Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVbn.jpg", ItemsPath + "docL4.jpg", 0.0025, 0, ItemsPath + "BurntNoteTexture.jpg")
	CreateItemTemplate("Dr L's Burnt Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVbn.jpg", ItemsPath + "docL5.jpg", 0.0025, 0, ItemsPath + "BurntNoteTexture.jpg")
	
	CreateItemTemplate("Emily Ross' Badge", "badge", ItemsPath + "badge.x", ItemsPath + "INVbadge.jpg", ItemsPath + "badge1.jpg", 0.0001, 1, ItemsPath + "badge1_tex.jpg")
	
	CreateItemTemplate("Journal Page", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "docGonzales.jpg", 0.0025, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Leaflet", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "leaflet.jpg", 0.003, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Log #1", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "f4.jpg", 0.002, 0, ItemsPath + "notetexture.jpg")
	CreateItemTemplate("Log #2", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "f5.jpg", 0.002, 0, ItemsPath + "notetexture.jpg")
	CreateItemTemplate("Log #3", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "f6.jpg", 0.002, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Movie Ticket", "ticket", ItemsPath + "ticket.b3d", ItemsPath + "INVticket.jpg", ItemsPath + "ticket_HUD.png", 0.002, 0, ItemsPath + "ticket.png", "", 0, 1 + 2 + 8)
	
	CreateItemTemplate("Mysterious Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "sn.it", 0.003, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Notification", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "docRAND1.jpg", 0.003, 0, ItemsPath + "notetexture.jpg")
	
	CreateItemTemplate("Old Badge", "badge", ItemsPath + "badge.x", ItemsPath + "INVoldbadge.jpg", ItemsPath + "badge2.png", 0.0001, 1, ItemsPath + "badge2_tex.png", "", 0, 1 + 2 + 8)
	
	CreateItemTemplate("Origami", "misc", ItemsPath + "origami.b3d", ItemsPath + "INVorigami.jpg", "", 0.003, 0)
	
	CreateItemTemplate("Scorched Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVbn.jpg", ItemsPath + "docL6.jpg", 0.0025, 0, ItemsPath + "BurntNoteTexture.jpg")
	
	CreateItemTemplate("Strange Note", "paper", ItemsPath + "paper.x", ItemsPath + "INVnote.jpg", ItemsPath + "docStrange.jpg", 0.0025, 0, ItemsPath + "notetexture.jpg")
	
	; ~ [SCPs]
	
	CreateItemTemplate("SCP-148 Ingot", "scp148ingot", ItemsPath + "scp148.x", ItemsPath + "INVscp148.jpg", "", RoomScale, 2)
	CreateItemTemplate("SCP-427", "scp427", ItemsPath + "427.b3d", ItemsPath + "INVscp427.jpg", "", 0.001, 3)
	
	it = CreateItemTemplate("SCP-500-01", "scp500", ItemsPath + "pill.b3d", ItemsPath + "INVpill.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	CreateItemTemplate("SCP-513", "scp513", ItemsPath + "513.x", ItemsPath + "INV513.jpg", "", 0.1, 2)
	CreateItemTemplate("SCP-714", "scp714", ItemsPath + "scp714.b3d", ItemsPath + "INV714.jpg", "", 0.3, 3)
	CreateItemTemplate("SCP-860", "scp860", ItemsPath + "scp_860.b3d", ItemsPath + "INV_scp_860.png", "", 0.0028, 3)
	CreateItemTemplate("SCP-1025", "scp1025", ItemsPath + "scp1025.b3d", ItemsPath + "INV1025.jpg", "", 0.1, 0)
	CreateItemTemplate("SCP-1123", "scp1123", ItemsPath + "HGIB_Skull1.b3d", ItemsPath + "INV1123.jpg", "", 0.015, 2)
	CreateItemTemplate("SCP-1499","scp1499",ItemsPath + "SCP-1499.b3d",ItemsPath + "INVscp1499.jpg", "", 0.022, 2)
	CreateItemTemplate("SCP-1499","super1499",ItemsPath + "SCP-1499.b3d",ItemsPath + "INVscp1499.jpg", "", 0.022, 2)
	
	CreateItemTemplate("Joint", "joint", ItemsPath + "420.x", ItemsPath + "INV420.jpg", "", 0.0004, 2)
	
	CreateItemTemplate("Metal Panel", "scp148", ItemsPath + "metalpanel.x", ItemsPath + "INVmetalpanel.jpg", "", RoomScale, 2)
	
	CreateItemTemplate("Smelly Joint", "scp420s", ItemsPath + "420.x", ItemsPath + "INV420.jpg", "", 0.0004, 2)
	
	CreateItemTemplate("Some SCP-420-J", "scp420j", ItemsPath + "420.x", ItemsPath + "INV420.jpg", "", 0.0005, 2)
	
	it = CreateItemTemplate("Upgraded pill", "scp500death", ItemsPath + "pill.b3d", ItemsPath + "INVpill.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	; ~ [MISC ITEMS]
	
	CreateItemTemplate("Ballistic Vest", "vest", ItemsPath + "vest.x", ItemsPath + "INVvest.jpg", "", 0.02, 2)
	CreateItemTemplate("Bulky Ballistic Vest", "veryfinevest", ItemsPath + "vest.x", ItemsPath + "INVvest.jpg", "", 0.025, 2)
	CreateItemTemplate("Heavy Ballistic Vest", "finevest", ItemsPath + "vest.x", ItemsPath + "INVvest.jpg", "", 0.022, 2)
	
	CreateItemTemplate("Cigarette", "cigarette", ItemsPath + "420.x", ItemsPath + "INV420.jpg", "", 0.0004, 2)
	
	CreateItemTemplate("Cup", "cup", ItemsPath + "cup.x", ItemsPath + "INVcup.jpg", "", 0.04, 2)
	
	CreateItemTemplate("Clipboard", "clipboard", ItemsPath + "clipboard.b3d", ItemsPath + "INVclipboard.jpg", "", 0.003, 1, "", ItemsPath + "INVclipboard2.jpg", 1)
	
	CreateItemTemplate("Electronical components", "misc", ItemsPath + "electronics.x", ItemsPath + "INVelectronics.jpg", "", 0.0011, 1)
	
	CreateItemTemplate("Empty Cup", "emptycup", ItemsPath + "cup.x", ItemsPath + "INVcup.jpg", "", 0.04, 2)
	
	CreateItemTemplate("ReVision Eyedrops", "eyedrops",ItemsPath + "eyedrops.b3d", ItemsPath + "INVeyedrops.jpg", "", 0.0012, 1, ItemsPath + "eyedrops.jpg")
	CreateItemTemplate("Eyedrops", "fineeyedrops", ItemsPath + "eyedrops.b3d", ItemsPath + "INVeyedrops.jpg", "", 0.0012, 1, ItemsPath + "eyedrops.jpg")
	CreateItemTemplate("Eyedrops", "supereyedrops", ItemsPath + "eyedrops.b3d", ItemsPath + "INVeyedrops.jpg", "", 0.0012, 1, ItemsPath + "eyedrops.jpg")
	CreateItemTemplate("RedVision Eyedrops", "eyedrops", ItemsPath + "eyedrops.b3d", ItemsPath + "INVeyedropsred.jpg", "", 0.0012, 1, ItemsPath + "eyedropsred.jpg")
	
	CreateItemTemplate("First Aid Kit", "firstaid", ItemsPath + "firstaid.x", ItemsPath + "INVfirstaid.jpg", "", 0.05, 1)
	CreateItemTemplate("Small First Aid Kit", "finefirstaid", ItemsPath + "firstaid.x", ItemsPath + "INVfirstaid.jpg", "", 0.03, 1)
	CreateItemTemplate("Blue First Aid Kit", "firstaid2", ItemsPath + "firstaid.x", ItemsPath + "INVfirstaid2.jpg", "", 0.03, 1, ItemsPath + "firstaidkit2.jpg")
	CreateItemTemplate("Strange Bottle", "veryfinefirstaid", ItemsPath + "eyedrops.b3d", ItemsPath + "INVbottle.jpg", "", 0.002, 1, ItemsPath + "bottle.jpg")	
	
	CreateItemTemplate("Gas Mask", "gasmask", ItemsPath + "gasmask.b3d", ItemsPath + "INVgasmask.jpg", "", 0.019, 2)
	CreateItemTemplate("Gas Mask", "supergasmask", ItemsPath + "gasmask.b3d", ItemsPath + "INVgasmask.jpg", "", 0.02, 2)
	CreateItemTemplate("Heavy Gas Mask", "gasmask3", ItemsPath + "gasmask.b3d", ItemsPath + "INVgasmask.jpg", "", 0.02, 2)
	
	CreateItemTemplate("Hazmat Suit", "hazmatsuit", ItemsPath + "hazmat.b3d", ItemsPath + "INVhazmat.jpg", "", 0.013, 2)
	CreateItemTemplate("Hazmat Suit", "hazmatsuit2", ItemsPath + "hazmat.b3d", ItemsPath + "INVhazmat.jpg", "", 0.013, 2)
	CreateItemTemplate("Heavy Hazmat Suit", "hazmatsuit3", ItemsPath + "hazmat.b3d", ItemsPath + "INVhazmat.jpg", "", 0.013, 2)
	
	CreateItemTemplate("Night Vision Goggles", "nvgoggles", ItemsPath + "NVG.b3d", ItemsPath + "INVnightvision.jpg", "", 0.02, 2)
	CreateItemTemplate("Night Vision Goggles", "finenvgoggles", ItemsPath + "NVG.b3d", ItemsPath + "INVveryfinenightvision.jpg", "", 0.02, 2)
	CreateItemTemplate("Night Vision Goggles", "supernv", ItemsPath + "NVG.b3d", ItemsPath + "INVsupernightvision.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Pill", "pill", ItemsPath + "pill.b3d", ItemsPath + "INVpillwhite.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 255.0, 255.0)
	
	CreateItemTemplate("Radio Transceiver", "radio", ItemsPath + "radio.x", ItemsPath + "INVradio.jpg", ItemsPath + "radioHUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "fineradio", ItemsPath + "radio.x", ItemsPath + "INVradio.jpg", ItemsPath + "radioHUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "veryfineradio", ItemsPath + "radio.x", ItemsPath + "INVradio.jpg", ItemsPath + "radioHUD.png", 1.0, 1)
	CreateItemTemplate("Radio Transceiver", "18vradio", ItemsPath + "radio.x", ItemsPath + "INVradio.jpg", ItemsPath + "radioHUD.png", 1.02, 1)
	
	CreateItemTemplate("Severed Hand", "hand", ItemsPath + "severedhand.b3d", ItemsPath + "INVhand.jpg", "", 0.03, 2)
	CreateItemTemplate("Black Severed Hand", "hand2", ItemsPath + "severedhand.b3d", ItemsPath + "INVhand2.jpg", "", 0.03, 2, ItemsPath + "shand2.png")
	
	CreateItemTemplate("S-NAV Navigator", "nav", ItemsPath + "navigator.x", ItemsPath + "INVnavigator.jpg", ItemsPath + "navigator.png", 0.0008, 1)
	CreateItemTemplate("S-NAV Navigator Ultimate", "nav", ItemsPath + "navigator.x", ItemsPath + "INVnavigator.jpg", ItemsPath + "navigator.png", 0.0008, 1)
	CreateItemTemplate("S-NAV 300 Navigator", "nav", ItemsPath + "navigator.x", ItemsPath + "INVnavigator.jpg", ItemsPath + "navigator.png", 0.0008, 1)
	CreateItemTemplate("S-NAV 310 Navigator", "nav", ItemsPath + "navigator.x", ItemsPath + "INVnavigator.jpg", ItemsPath + "navigator.png", 0.0008, 1)
	
	CreateItemTemplate("9V Battery", "bat", ItemsPath + "Battery\Battery.x", ItemsPath + "Battery\INVbattery9v.jpg", "", 0.008, 1)
	CreateItemTemplate("18V Battery", "18vbat", ItemsPath + "Battery\Battery.x", ItemsPath + "Battery\INVbattery18v.jpg", "", 0.01, 1, ItemsPath + "Battery\Battery 18V.jpg")
	CreateItemTemplate("Strange Battery", "killbat", ItemsPath + "Battery\Battery.x", ItemsPath + "Battery\INVbattery22900.jpg", "", 0.01, 1, ItemsPath + "Battery\Strange Battery.jpg")
	
	CreateItemTemplate("Syringe", "syringe", ItemsPath + "Syringe\syringe.b3d", ItemsPath + "Syringe\inv.png", "", 0.005, 2)
	CreateItemTemplate("Syringe", "finesyringe", ItemsPath + "Syringe\syringe.b3d", ItemsPath + "Syringe\inv.png", "", 0.005, 2)
	CreateItemTemplate("Syringe", "veryfinesyringe", ItemsPath + "Syringe\syringe.b3d", ItemsPath + "Syringe\inv.png", "", 0.005, 2)
	
	CreateItemTemplate("Wallet", "wallet", ItemsPath + "wallet.b3d", ItemsPath + "INVwallet.jpg", "", 0.0005, 2, "", "", 1)
	
	; ~ [KEYCARDS, KEYS, CARDS, COINS]
	
	CreateItemTemplate("Coin", "coin", ItemsPath + "coin.b3d", ItemsPath + "INVcoin.jpg", "", 0.0005, 3)
	
	CreateItemTemplate("Key Card Omni", "key6", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_omni.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_omni.png")
	CreateItemTemplate("Level 1 Key Card", "key1", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_1.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_1.png")
	CreateItemTemplate("Level 2 Key Card", "key2", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_2.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_2.png")
	CreateItemTemplate("Level 3 Key Card", "key3", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_3.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_3.png")
	CreateItemTemplate("Level 4 Key Card", "key4", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_4.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_4.png")
	CreateItemTemplate("Level 5 Key Card", "key5", ItemsPath + "key_card.x", ItemsPath + "INV_key_card_lvl_5.png", "", 0.0004, 1, ItemsPath + "key_card_lvl_5.png")
	
	CreateItemTemplate("Lost Key", "key", ItemsPath + "key.b3d", ItemsPath + "INV1162_1.jpg", "", 0.0028, 3)
	
	CreateItemTemplate("Mastercard", "misc", ItemsPath + "key_card.x", ItemsPath + "INV_master_card.png", "", 0.0004, 1, ItemsPath + "master_card.png")
	
	CreateItemTemplate("Playing Card", "misc", ItemsPath + "key_card.x", ItemsPath + "INV_playing_card.png", "", 0.0004, 1, ItemsPath + "playing_card.png")
	
	CreateItemTemplate("Quarter", "25ct", ItemsPath + "coin.b3d", ItemsPath + "INVcoin.jpg", "", 0.0005, 3)
	
	For it = Each ItemTemplates
		If (it\Tex <> 0) Then
			If (it\TexPath <> "") Then
				For it2 = Each ItemTemplates
					If (it2 <> it) And (it2\Tex = it\Tex) Then
						it2\Tex = 0
					EndIf
				Next
			EndIf
			FreeTexture(it\Tex) : it\Tex = 0
		EndIf
	Next
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
	Field State#, State2#
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
	Local o.Objects = First Objects
	
	Name = Lower(Name)
	TempName = Lower (TempName)
	
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\Name) = Name Then
			If Lower(it\TempName) = TempName Then
				i\ItemTemplate = it
				i\Collider = CreatePivot()			
				EntityRadius(i\Collider, 0.01)
				EntityPickMode(i\Collider, 1, False)
				i\Model = CopyEntity(it\OBJ, i\Collider)
				i\Name = it\Name
				ShowEntity(i\Collider)
				ShowEntity(i\Model)
			EndIf
		EndIf
	Next 
	
	i\WontColl = False
	
	If i\ItemTemplate = Null Then RuntimeError("Item template not found (" + Name + ", " + TempName + ")")
	
	ResetEntity(i\Collider)		
	PositionEntity(i\Collider, x, y, z, True)
	RotateEntity(i\Collider, 0.0, Rand(360.0), 0.0)
	i\Dist = EntityDistance(Collider, i\Collider)
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
	If (TempName = "clipboard") And (InvSlots = 0) Then
		InvSlots = 10
		SetAnimTime(i\Model, 17.0)
		i\InvImg = i\ItemTemplate\InvImg2
	ElseIf (TempName = "wallet") And (InvSlots = 0) Then
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
	
	FreeEntity(i\Model) : FreeEntity(i\Collider) : i\Collider = 0
	
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = i
			Inventory(n) = Null
			ItemAmount = ItemAmount - 1
			Exit
		EndIf
	Next
	If SelectedItem = i Then
		Select SelectedItem\ItemTemplate\TempName 
			Case "nvgoggles", "supernv"
				;[Block]
				WearingNightVision = 0
				;[End Block]
			Case "gasmask", "supergasmask", "gasmask2", "gasmask3"
				;[Block]
				WearingGasMask = 0
				;[End Block]
			Case "vest", "finevest", "veryfinevest"
				;[Block]
				WearingVest = 0
				;[End Block]
			Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
				;[Block]
				WearingHazmat = 0
				;[End Block]
			Case "scp714"
				;[Block]
				Wearing714 = 0
				;[End Block]
			Case "scp1499", "super1499"
				;[Block]
				Wearing1499 = 0
				;[End Block]
			Case "scp427"
				;[Block]
				I_427\Using = 0
				;[End Block]
		End Select
		
		SelectedItem = Null
	EndIf
	If i\ItemTemplate\Img <> 0
		FreeImage(i\ItemTemplate\Img)
		i\ItemTemplate\Img = 0
	EndIf
	Delete(i)
	
	CatchErrors("RemoveItem")
End Function

Function UpdateItems()
	CatchErrors("Uncaught (UpdateItems)")
	Local n%, i.Items, i2.Items
	Local xTemp#, yTemp#, zTemp#
	Local Temp%, np.NPCs
	Local Pick%
	Local HideDist# = HideDistance * 0.5
	Local DeletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked) Then
			If i\DistTimer < MilliSecs2() Then
				i\Dist = EntityDistance(Camera, i\Collider)
				i\DistTimer = MilliSecs2() + 700
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
					ElseIf ClosestItem = i Or i\Dist < EntityDistance(Camera, ClosestItem\Collider) Then 
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
					If ShouldEntitiesFall
						Pick = LinePick(EntityX(i\Collider), EntityY(i\Collider), EntityZ(i\Collider), 0.0, -10.0, 0.0)
						If Pick Then
							i\DropSpeed = i\DropSpeed - 0.0004 * FPSfactor
							TranslateEntity(i\Collider, i\xSpeed * FPSfactor, i\DropSpeed * FPSfactor, i\zSpeed * FPSfactor)
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
							xTemp# = EntityX(i2\Collider, True) - EntityX(i\Collider, True)
							yTemp# = EntityY(i2\Collider, True) - EntityY(i\Collider, True)
							zTemp# = EntityZ(i2\Collider, True) - EntityZ(i\Collider, True)
							
							ed# = (xTemp * xTemp + zTemp * zTemp)
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
			CatchErrors(Chr(34) + i\ItemTemplate\Name + Chr(34) + " item")
		EndIf
		DeletedItem = False
	Next
	
	If ClosestItem <> Null Then
		If MouseHit1 Then PickItem(ClosestItem)
	EndIf
End Function

Function PickItem(item.Items)
	Local n% = 0
	Local CanPickItem = True
	Local FullINV% = True
	Local ov.Overlays = First Overlays
	
	For n = 0 To MaxItemAmount - 1
		If Inventory(n) = Null
			FullINV = False
			Exit
		EndIf
	Next
	
	If WearingHazmat > 0 Then
		Msg = "You cannot pick up any items while wearing a hazmat suit."
		MsgTimer = 70 * 5.0
		Return
	EndIf
	
	CatchErrors("Uncaught (PickItem)")
	
	Local z%
	
	If (Not FullINV) Then
		For n = 0 To MaxItemAmount - 1
			If Inventory(n) = Null Then
				Select item\ItemTemplate\TempName
					Case "scp1123"
						;[Block]
						If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then
							If PlayerRoom\RoomTemplate\Name <> "room1123" Then
								ShowEntity(ov\OverlayID[7])
								LightFlash = 7.0
								PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
								DeathMSG = "Subject D-9341 was shot dead after attempting to attack a member of Nine-Tailed Fox. Surveillance tapes show that the subject had been "
								DeathMSG = DeathMSG + "wandering around the site approximately 9 minutes prior, shouting the phrase " + Chr(34) + "get rid of the four pests" + Chr(34)
								DeathMSG = DeathMSG + " in chinese. SCP-1123 was found in [REDACTED] nearby, suggesting the subject had come into physical contact with it. How "
								DeathMSG = DeathMSG + "exactly SCP-1123 was removed from its containment chamber is still unknown."
								Kill()
							EndIf
							
							For e.Events = Each Events
								If e\EventName = "room1123" Then 
									If e\EventState = 0.0 Then
										ShowEntity(ov\OverlayID[7])
										LightFlash = 3.0
										PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
									EndIf
									e\EventState = Max(1.0, e\EventState)
									Exit
								EndIf
							Next
						EndIf
						Return
						;[End Block]
					Case "killbat"
						;[Block]
						ShowEntity(ov\OverlayID[7])
						LightFlash = 1.0
						PlaySound_Strict(IntroSFX(Rand(8, 10)))
						DeathMSG = "Subject D-9341 found dead inside SCP-914's output booth next to what appears to be an ordinary nine-volt battery. The subject is covered in severe "
						DeathMSG = DeathMSG + "electrical burns, and assumed to be killed via an electrical shock caused by the battery. The battery has been stored for further study."
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
						GiveAchievement(AchvOmni)
						;[End Block]
					Case "veryfinevest"
						;[Block]
						Msg = "The vest is too heavy to pick up."
						MsgTimer = 70 * 6.0
						Exit
						;[End Block]
					Case "firstaid", "finefirstaid", "veryfinefirstaid", "firstaid2"
						;[Block]
						item\State = 0.0
						;[End Block]
					Case "navigator", "nav"
						;[Block]
						If item\ItemTemplate\Name = "S-NAV Navigator Ultimate" Then GiveAchievement(AchvSNAV)
						;[End Block]
					Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
						;[Block]
						CanPickItem = True
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\ItemTemplate\TempName = "hazmatsuit" Or Inventory(z)\ItemTemplate\TempName = "hazmatsuit2" Or Inventory(z)\ItemTemplate\TempName = "hazmatsuit3" Then
									CanPickItem = False
									Exit
								ElseIf Inventory(z)\ItemTemplate\TempName = "vest" Or Inventory(z)\ItemTemplate\TempName = "finevest" Then
									CanPickItem = 2
									Exit
								EndIf
							EndIf
						Next
						
						If CanPickItem = False Then
							Msg = "You are not able to wear two hazmat suits at the same time."
							MsgTimer = 70 * 5.0
							Return
						ElseIf CanPickItem = 2 Then
							Msg = "You are not able to wear a vest and a hazmat suit at the same time."
							MsgTimer = 70 * 5.0
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
								If Inventory(z)\ItemTemplate\TempName = "vest" Or Inventory(z)\ItemTemplate\TempName = "finevest" Then
									CanPickItem = False
									Exit
								ElseIf Inventory(z)\ItemTemplate\TempName = "hazmatsuit" Or Inventory(z)\ItemTemplate\TempName = "hazmatsuit2" Or Inventory(z)\ItemTemplate\TempName = "hazmatsuit3" Then
									CanPickItem = 2
									Exit
								EndIf
							EndIf
						Next
						
						If CanPickItem = False Then
							Msg = "You are not able to wear two vests at the same time."
							MsgTimer = 70 * 5.0
							Return
						ElseIf CanPickItem = 2 Then
							Msg = "You are not able to wear a vest and a hazmat suit at the same time."
							MsgTimer = 70 * 5.0
							Return
						Else
							SelectedItem = item
						EndIf
						;[End Block]
				End Select
				
				If item\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(item\ItemTemplate\Sound))
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
		Msg = "You cannot carry any more items."
		MsgTimer = 70 * 5.0
	EndIf
	
	CatchErrors("PickItem")
End Function

Function DropItem(item.Items, PlayDropSound% = True)
	If WearingHazmat > 0 Then
		Msg = "You cannot drop any items while wearing a hazmat suit."
		MsgTimer = 70 * 5.0
		Return
	EndIf
	
	CatchErrors("Uncaught (DropItem)")
	
	Local z%
	
	If PlayDropSound Then
		If item\ItemTemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(item\ItemTemplate\Sound))
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
		If Inventory(z) = item Then Inventory(z) = Null
	Next
	Select item\ItemTemplate\TempName
		Case "gasmask", "supergasmask", "gasmask3"
			;[Block]
			WearingGasMask = 0
			;[End Block]
		Case "hazmatsuit",  "hazmatsuit2", "hazmatsuit3"
			;[Block]
			WearingHazmat = 0
			;[End Block]
		Case "vest", "finevest"
			;[Block]
			WearingVest = 0
			;[End Block]
		Case "nvgoggles"
			;[Block]
			If WearingNightVision = 1 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = 0
			;[End Block]
		Case "supernv"
			;[Block]
			If WearingNightVision = 2 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = 0
			;[End Block]
		Case "finenvgoggles"
			;[Block]
			If WearingNightVision = 3 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = 0
			;[End Block]
		Case "scp714"
			;[Block]
			Wearing714 = 0
			;[End Block]
		Case "scp1499", "super1499"
			;[Block]
			Wearing1499 = 0
			;[End Block]
		Case "scp427"
			;[Block]
			I_427\Using = 0
			;[End Block]
	End Select
	
	CatchErrors("DropItem")
End Function

; ~ Update any ailments inflicted by SCP-294 drinks
Function Update294()
	CatchErrors("Uncaught (Update294)")
	
	Local Pvt%
	
	If CameraShakeTimer > 0.0 Then
		CameraShakeTimer = CameraShakeTimer - (FPSfactor / 70.0)
		CameraShake = 2.0
	EndIf
	
	If VomitTimer > 0.0 Then
		VomitTimer = VomitTimer - (FPSfactor / 70.0)
		
		If (MilliSecs2() Mod 1600) < Rand(200, 400) Then
			If BlurTimer = 0.0 Then BlurTimer = 70 * Rnd(10.0, 20.0)
			CameraShake = Rnd(0.0, 2.0)
		EndIf
		
		If Rand(50) = 50 And (MilliSecs2() Mod 4000) < 200 Then PlaySound_Strict(CoughSFX(Rand(0, 2)))
		
		; ~ Regurgitate when timer is below 10 seconds.
		If VomitTimer < 10.0 And Rnd(0.0, 500.0 * VomitTimer) < 2.0 Then
			If ChannelPlaying(VomitCHN) = False And (Not Regurgitate) Then
				VomitCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Retch" + Rand(1, 2) + ".ogg"))
				Regurgitate = MilliSecs2() + 50
			EndIf
		EndIf
		
		If Regurgitate > MilliSecs2() And Regurgitate <> 0 Then
			Mouse_Y_Speed_1 = Mouse_Y_Speed_1 + 1.0
		Else
			Regurgitate = 0
		EndIf
	ElseIf VomitTimer < 0.0 Then ; ~ Vomit
		VomitTimer = VomitTimer - (FPSfactor / 70.0)
		
		If VomitTimer > -5.0 Then
			If (MilliSecs2() Mod 400) < 50 Then CameraShake = 4.0 
			Mouse_X_Speed_1 = 0.0
			Playable = False
		Else
			Playable = True
		EndIf
		
		If (Not Vomit) Then
			BlurTimer = 70 * 40.0
			VomitSFX = LoadSound_Strict("SFX\SCP\294\Vomit.ogg")
			VomitCHN = PlaySound_Strict(VomitSFX)
			PrevInjuries = Injuries
			PrevBloodloss = Bloodloss
			Injuries = 1.5
			Bloodloss = 70.0
			EyeIrritation = 70 * 9.0
			
			Pvt = CreatePivot()
			PositionEntity(Pvt, EntityX(Camera), EntityY(Collider) - 0.05, EntityZ(Camera))
			TurnEntity(Pvt, 90.0, 0.0, 0.0)
			EntityPick(Pvt, 0.3)
			de.Decals = CreateDecal(5, PickedX(), PickedY() + 0.005, PickedZ(), 90.0, 180.0, 0.0)
			de\Size = 0.001 : de\SizeChange = 0.001 : de\MaxSize = 0.6
			EntityAlpha(de\OBJ, 1.0)
			EntityColor(de\OBJ, 0.0, Rnd(200.0, 255.0), 0.0)
			ScaleSprite(de\OBJ, de\Size, de\Size)
			FreeEntity(Pvt)
			Vomit = True
		EndIf
		
		UpdateDecals()
		
		Mouse_Y_Speed_1 = Mouse_Y_Speed_1 + Max((1.0 + VomitTimer / 10.0), 0.0)
		
		If VomitTimer < -15.0 Then
			FreeSound_Strict(VomitSFX)
			VomitTimer = 0.0
			If KillTimer >= 0.0 Then
				PlaySound_Strict(BreathSFX(0, 0))
			EndIf
			Injuries = PrevInjuries
			Bloodloss = PrevBloodloss
			Vomit = False
		EndIf
	EndIf
	
	CatchErrors("Update294")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D