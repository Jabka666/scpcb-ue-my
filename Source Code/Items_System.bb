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
	Local it.ItemTemplates, it2.ItemTemplates
	
	; ~ [KEYCARDS, KEYS, CARDS, COINS]
	
	it = CreateItemTemplate("Coin", "coin", "GFX\items\key.b3d", "GFX\items\INVcoin.jpg", "", 0.0005, 3, "GFX\items\coin.png", "", 0, 1 + 2 + 8)
	
	it = CreateItemTemplate("Key Card Omni", "key6", "GFX\items\keycard.x", "GFX\items\INVkeyomni.jpg", "", 0.0004, 1, "GFX\items\keycardomni.jpg")
	
	it = CreateItemTemplate("Level 1 Key Card", "key1", "GFX\items\keycard.x", "GFX\items\INVkey1.jpg", "", 0.0004, 1, "GFX\items\keycard1.jpg")
	
	it = CreateItemTemplate("Level 2 Key Card", "key2", "GFX\items\keycard.x", "GFX\items\INVkey2.jpg", "", 0.0004, 1, "GFX\items\keycard2.jpg")
	
	it = CreateItemTemplate("Level 3 Key Card", "key3", "GFX\items\keycard.x", "GFX\items\INVkey3.jpg", "", 0.0004, 1, "GFX\items\keycard3.jpg")
	
	it = CreateItemTemplate("Level 4 Key Card", "key4", "GFX\items\keycard.x", "GFX\items\INVkey4.jpg", "", 0.0004, 1, "GFX\items\keycard4.jpg")
	
	it = CreateItemTemplate("Level 5 Key Card", "key5", "GFX\items\keycard.x", "GFX\items\INVkey5.jpg", "", 0.0004, 1, "GFX\items\keycard5.jpg")
	
	it = CreateItemTemplate("Lost Key", "key", "GFX\items\key.b3d", "GFX\items\INV1162_1.jpg", "", 0.001, 3, "GFX\items\key2.png", "", 0, 1 + 2 + 8)
	
	it = CreateItemTemplate("Mastercard", "misc", "GFX\items\keycard.x", "GFX\items\INVmastercard.jpg", "", 0.0004, 1, "GFX\items\mastercard.jpg")
	
	it = CreateItemTemplate("Playing Card", "misc", "GFX\items\keycard.x", "GFX\items\INVcard.jpg", "", 0.0004, 1, "GFX\items\card.jpg")
	
	it = CreateItemTemplate("Quarter", "25ct", "GFX\items\key.b3d", "GFX\items\INVcoin.jpg", "", 0.0005, 3, "GFX\items\coin.png", "", 0, 1 + 2 + 8)
	
	; ~ [DOCUMENTS]
	
	it = CreateItemTemplate("Document SCP-008", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc008.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-012", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc012.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-035", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc035.jpg", 0.003, 0)
	
	it = CreateItemTemplate("SCP-035 Addendum", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc035ad.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-049", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc049.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-079", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc079.jpg", 0.003, 0)
	
	it = CreateItemTemplate("SCP-093 Recovered Materials", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc093rm.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-096", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc096.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-106", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc106.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-173", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc173.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-372", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc372.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-427", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc427.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-500", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc500.png", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-513", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc513.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-714", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc714.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-682", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc682.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-860", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc860.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-860-1", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc8601.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-895", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc895.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-939", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc939.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-966", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc966.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-970", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc970.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-1048", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1048.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Incident Report SCP-1048-A", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1048a.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-1123", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1123.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-1162", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1162.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Document SCP-1499", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1499.png", 0.003, 0)
	
	; ~ [NOTES, MISC DOCUMENTS, BADGES, TICKET, ORIGAMI]
	
	it = CreateItemTemplate("Addendum: 5/14 Test Log", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docRAND2.jpg", 0.003, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Blood-stained Note", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docL3.jpg", 0.0025, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Burnt Note", "paper", "GFX\items\paper.x", "GFX\items\INVbn.jpg", "GFX\items\bn.it", 0.003, 0, "GFX\items\BurntNoteTexture.jpg")
	it\Img = BurntNote
	
	it = CreateItemTemplate("Class D Orientation Leaflet", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docORI.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Disciplinary Hearing DH-S-4137-17092", "oldpaper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\dh.s", 0.003, 0)
	
	it = CreateItemTemplate("Document", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docRAND3.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Drawing", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc1048.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Dr. Allok's Note", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\doc106_2.jpg", 0.0025, 0)
	
	it = CreateItemTemplate("Dr. L's Note", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docL1.jpg", 0.0025, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Dr L's Note", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docL2.jpg", 0.0025, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Dr. L's Burnt Note", "paper", "GFX\items\paper.x", "GFX\items\INVbn.jpg", "GFX\items\docL4.jpg", 0.0025, 0, "GFX\items\BurntNoteTexture.jpg")
	
	it = CreateItemTemplate("Dr L's Burnt Note", "paper", "GFX\items\paper.x", "GFX\items\INVbn.jpg", "GFX\items\docL5.jpg", 0.0025, 0, "GFX\items\BurntNoteTexture.jpg")
	
	it = CreateItemTemplate("Emily Ross' Badge", "badge", "GFX\items\badge.x", "GFX\items\INVbadge.jpg", "GFX\items\badge1.jpg", 0.0001, 1, "GFX\items\badge1_tex.jpg")
	
	it = CreateItemTemplate("Incident Report SCP-106-0204", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docIR106.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Journal Page", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docGonzales.jpg", 0.0025, 0)
	
	it = CreateItemTemplate("Leaflet", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\leaflet.jpg", 0.003, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Log #1", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\f4.jpg", 0.004, 0, "GFX\items\f4.jpg")
	
	it = CreateItemTemplate("Log #2", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\f5.jpg", 0.004, 0, "GFX\items\f4.jpg")
	
	it = CreateItemTemplate("Log #3", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\f6.jpg", 0.004, 0, "GFX\items\f4.jpg")
	
	it = CreateItemTemplate("Mobile Task Forces", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docMTF.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Movie Ticket", "ticket", "GFX\items\key.b3d", "GFX\items\INVticket.jpg", "GFX\items\ticket.png", 0.002, 0, "GFX\items\tickettexture.png", "", 0, 1 + 2 + 8)
	
	it = CreateItemTemplate("Mysterious Note", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\sn.it", 0.003, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Note from Daniel", "paper", "GFX\items\note.x", "GFX\items\INVnote2.jpg", "GFX\items\docdan.jpg", 0.0025, 0)
	
	it = CreateItemTemplate("Notification", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docRAND1.jpg", 0.003, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("Nuclear Device Document", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docNDP.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Object Classes", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docOBJC.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Old Badge", "badge", "GFX\items\badge.x", "GFX\items\INVoldbadge.jpg", "GFX\items\badge2.png", 0.0001, 1, "GFX\items\badge2_tex.png", "", 0, 1 + 2 + 8)
	
	it = CreateItemTemplate("Origami", "misc", "GFX\items\origami.b3d", "GFX\items\INVorigami.jpg", "", 0.003, 0)
	
	it = CreateItemTemplate("Recall Protocol RP-106-N", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docRP.jpg", 0.0025, 0)
	
	it = CreateItemTemplate("Research Sector-02 Scheme", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docmap.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Scorched Note", "paper", "GFX\items\paper.x", "GFX\items\INVbn.jpg", "GFX\items\docL6.jpg", 0.0025, 0, "GFX\items\BurntNoteTexture.jpg")
	
	it = CreateItemTemplate("Security Clearance Levels", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docSC.jpg", 0.003, 0)
	
	it = CreateItemTemplate("Sticky Note", "paper", "GFX\items\note.x", "GFX\items\INVnote2.jpg", "GFX\items\note682.jpg", 0.0025, 0)
	
	it = CreateItemTemplate("Strange Note", "paper", "GFX\items\paper.x", "GFX\items\INVnote.jpg", "GFX\items\docStrange.jpg", 0.0025, 0, "GFX\items\notetexture.jpg")
	
	it = CreateItemTemplate("The Modular Site Project", "paper", "GFX\items\paper.x", "GFX\items\INVpaper.jpg", "GFX\items\docMSP.jpg", 0.003, 0)
	
	;[SCPs]
	
	it = CreateItemTemplate("SCP-148 Ingot", "scp148ingot", "GFX\items\scp148.x", "GFX\items\INVscp148.jpg", "", RoomScale, 2)
	
	it = CreateItemTemplate("SCP-427", "scp427", "GFX\items\427.b3d", "GFX\items\INVscp427.jpg", "", 0.001, 3)
	
	it = CreateItemTemplate("SCP-500-01", "scp500", "GFX\items\pill.b3d", "GFX\items\INVpill.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	it = CreateItemTemplate("SCP-513", "scp513", "GFX\items\513.x", "GFX\items\INV513.jpg", "", 0.1, 2)
	
	it = CreateItemTemplate("SCP-714", "scp714", "GFX\items\scp714.b3d", "GFX\items\INV714.jpg", "", 0.3, 3)
	
	it = CreateItemTemplate("SCP-860", "scp860", "GFX\items\key.b3d", "GFX\items\INVkey.jpg", "", 0.001, 3)
	
	it = CreateItemTemplate("SCP-1025", "scp1025", "GFX\items\scp1025.b3d", "GFX\items\INV1025.jpg", "", 0.1, 0)
	
	it = CreateItemTemplate("SCP-1123", "1123", "GFX\items\HGIB_Skull1.b3d", "GFX\items\INV1123.jpg", "", 0.015, 2)
	
	it = CreateItemTemplate("SCP-1499","scp1499","GFX\items\SCP-1499.b3d","GFX\items\INVscp1499.jpg", "", 0.023, 2)
	
	it = CreateItemTemplate("SCP-1499","super1499","GFX\items\SCP-1499.b3d","GFX\items\INVscp1499.jpg", "", 0.023, 2)
	
	it = CreateItemTemplate("Joint", "420s", "GFX\items\420.x", "GFX\items\INV420.jpg", "", 0.0004, 2)
	
	it = CreateItemTemplate("Metal Panel", "scp148", "GFX\items\metalpanel.x", "GFX\items\INVmetalpanel.jpg", "", RoomScale, 2)
	
	it = CreateItemTemplate("Smelly Joint", "420s", "GFX\items\420.x", "GFX\items\INV420.jpg", "", 0.0004, 2)
	
	it = CreateItemTemplate("Some SCP-420-J", "420", "GFX\items\420.x", "GFX\items\INV420.jpg", "", 0.0005, 2)
	
	it = CreateItemTemplate("Upgraded pill", "scp500death", "GFX\items\pill.b3d", "GFX\items\INVpill.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 0.0, 0.0)
	
	;[MISC ITEMS]
	
	it = CreateItemTemplate("Ballistic Vest", "vest", "GFX\items\vest.x", "GFX\items\INVvest.jpg", "", 0.02, 2, "GFX\items\Vest.png")
	
	it = CreateItemTemplate("Black Severed Hand", "hand2", "GFX\items\severedhand.b3d", "GFX\items\INVhand2.jpg", "", 0.02, 2, "GFX\items\shand2.png")
	
	it = CreateItemTemplate("Blue First Aid Kit", "firstaid2", "GFX\items\firstaid.x", "GFX\items\INVfirstaid2.jpg", "", 0.03, 1, "GFX\items\firstaidkit2.jpg")
	
	it = CreateItemTemplate("Bulky Ballistic Vest", "veryfinevest", "GFX\items\vest.x", "GFX\items\INVvest.jpg", "", 0.025, 2, "GFX\items\Vest.png")
	
	it = CreateItemTemplate("Cigarette", "cigarette", "GFX\items\420.x", "GFX\items\INV420.jpg", "", 0.0004, 2)
	
	it = CreateItemTemplate("Cup", "cup", "GFX\items\cup.x", "GFX\items\INVcup.jpg", "", 0.04, 2)
	
	it = CreateItemTemplate("Clipboard", "clipboard", "GFX\items\clipboard.b3d", "GFX\items\INVclipboard.jpg", "", 0.003, 1, "", "GFX\items\INVclipboard2.jpg", 1)
	
	it = CreateItemTemplate("Electronical components", "misc", "GFX\items\electronics.x", "GFX\items\INVelectronics.jpg", "", 0.0011, 1)
	
	it = CreateItemTemplate("Empty Cup", "emptycup", "GFX\items\cup.x", "GFX\items\INVcup.jpg", "", 0.04, 2)
	
	it = CreateItemTemplate("Eyedrops", "fineeyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "GFX\items\eyedrops.jpg")
	
	it = CreateItemTemplate("Eyedrops", "supereyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "GFX\items\eyedrops.jpg")
	
	it = CreateItemTemplate("First Aid Kit", "firstaid", "GFX\items\firstaid.x", "GFX\items\INVfirstaid.jpg", "", 0.05, 1)
	
	it = CreateItemTemplate("Gas Mask", "gasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Gas Mask", "supergasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.021, 2)
	
	it = CreateItemTemplate("Hazmat Suit", "hazmatsuit", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.013, 2)
	
	it = CreateItemTemplate("Hazmat Suit", "hazmatsuit2", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.013, 2)
	
	it = CreateItemTemplate("Heavy Ballistic Vest", "finevest", "GFX\items\vest.x", "GFX\items\INVvest.jpg", "", 0.022, 2, "GFX\items\Vest.png")
	
	it = CreateItemTemplate("Heavy Gas Mask", "gasmask3", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.021, 2)
	
	it = CreateItemTemplate("Heavy Hazmat Suit", "hazmatsuit3", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.013, 2)
	
	it = CreateItemTemplate("Night Vision Goggles", "nvgoggles", "GFX\items\NVG.b3d", "GFX\items\INVnightvision.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Night Vision Goggles", "finenvgoggles", "GFX\items\NVG.b3d", "GFX\items\INVveryfinenightvision.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Night Vision Goggles", "supernv", "GFX\items\NVG.b3d", "GFX\items\INVsupernightvision.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Pill", "pill", "GFX\items\pill.b3d", "GFX\items\INVpillwhite.jpg", "", 0.0001, 2)
	EntityColor(it\OBJ, 255.0, 255.0, 255.0)
	
	it = CreateItemTemplate("Radio Transceiver", "radio", "GFX\items\radio.x", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	
	it = CreateItemTemplate("Radio Transceiver", "fineradio", "GFX\items\radio.x", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	
	it = CreateItemTemplate("Radio Transceiver", "veryfineradio", "GFX\items\radio.x", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	
	it = CreateItemTemplate("Radio Transceiver", "18vradio", "GFX\items\radio.x", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.02, 1)
	
	it = CreateItemTemplate("ReVision Eyedrops", "eyedrops","GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "GFX\items\eyedrops.jpg")
	
	it = CreateItemTemplate("RedVision Eyedrops", "eyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedropsred.jpg", "", 0.0012, 1, "GFX\items\eyedropsred.jpg")
	
	it = CreateItemTemplate("Severed Hand", "hand", "GFX\items\severedhand.b3d", "GFX\items\INVhand.jpg", "", 0.02, 2)
	
	it = CreateItemTemplate("Small First Aid Kit", "finefirstaid", "GFX\items\firstaid.x", "GFX\items\INVfirstaid.jpg", "", 0.03, 1)
	
	it = CreateItemTemplate("S-NAV Navigator", "nav", "GFX\items\navigator.x", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	
	it = CreateItemTemplate("S-NAV Navigator Ultimate", "nav", "GFX\items\navigator.x", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	
	it = CreateItemTemplate("S-NAV 300 Navigator", "nav", "GFX\items\navigator.x", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	
	it = CreateItemTemplate("S-NAV 310 Navigator", "nav", "GFX\items\navigator.x", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	
	it = CreateItemTemplate("Strange Battery", "killbat", "GFX\items\Battery\Battery.x", "GFX\items\Battery\INVbattery22900.jpg", "", 0.01, 1, "GFX\items\Battery\Strange Battery.jpg")
	
	it = CreateItemTemplate("Strange Bottle", "veryfinefirstaid", "GFX\items\eyedrops.b3d", "GFX\items\INVbottle.jpg", "", 0.002, 1, "GFX\items\bottle.jpg")	
	
	it = CreateItemTemplate("Syringe", "syringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	
	it = CreateItemTemplate("Syringe", "finesyringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	
	it = CreateItemTemplate("Syringe", "veryfinesyringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	
	it = CreateItemTemplate("Wallet","wallet", "GFX\items\wallet.b3d", "GFX\items\INVwallet.jpg", "", 0.0005, 2, "", "", 1)
	
	it = CreateItemTemplate("9V Battery", "bat", "GFX\items\Battery\Battery.x", "GFX\items\Battery\INVbattery9v.jpg", "", 0.008, 1)
	
	it = CreateItemTemplate("18V Battery", "18vbat", "GFX\items\Battery\Battery.x", "GFX\items\Battery\INVbattery18v.jpg", "", 0.01, 1, "GFX\items\Battery\Battery 18V.jpg")
	
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
	Field itemtemplate.ItemTemplates
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
	
	Name = Lower(Name)
	TempName = Lower (TempName)
	
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\Name) = Name Then
			If Lower(it\TempName) = TempName Then
				i\itemtemplate = it
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
	
	If i\itemtemplate = Null Then RuntimeError("Item template not found (" + Name + ", " + TempName + ")")
	
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
		
		Local Liquid% = CopyEntity(LiquidOBJ)
		
		ScaleEntity(Liquid, i\itemtemplate\Scale, i\itemtemplate\Scale, i\itemtemplate\Scale, True)
		PositionEntity(Liquid, EntityX(i\Collider, True), EntityY(i\Collider, True), EntityZ(i\Collider, True))
		EntityParent(Liquid, i\Model)
		EntityColor(Liquid, R, G, B)
		
		If A < 0 Then 
			EntityFX(Liquid, 1)
			EntityAlpha(Liquid, Abs(A))
		Else
			EntityAlpha(Liquid, Abs(A))
		EndIf
		EntityShininess(Liquid, 1.0)
	EndIf
	
	i\InvImg = i\itemtemplate\InvImg
	If (TempName = "clipboard") And (InvSlots = 0) Then
		InvSlots = 10
		SetAnimTime(i\Model, 17.0)
		i\InvImg = i\itemtemplate\InvImg2
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
		Select SelectedItem\itemtemplate\TempName 
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
	If i\itemtemplate\Img <> 0
		FreeImage(i\itemtemplate\Img)
		i\itemtemplate\Img = 0
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
	Local HideDist = HideDistance * 0.5
	Local DeletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (Not i\Picked) Then
			If i\DistTimer < MilliSecs2() Then
				i\Dist = EntityDistance(Camera, i\Collider)
				i\DistTimer = MilliSecs2() + 700.0
				If i\Dist < HideDist Then ShowEntity(i\Collider)
			EndIf
			
			If i\Dist < HideDist Then
				ShowEntity(i\Collider)
				If i\Dist < 1.2 Then
					If ClosestItem = Null Then
						If EntityInView(i\Model, Camera) Then
							If EntityVisible(i\Collider,Camera) Then
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
						If Pick
							i\DropSpeed = i\DropSpeed - 0.0004 * FPSfactor
							TranslateEntity i\Collider, i\xSpeed * FPSfactor, i\DropSpeed * FPSfactor, i\zSpeed * FPSfactor
							If i\WontColl Then ResetEntity(i\Collider)
						Else
							i\DropSpeed = 0.0
							i\xSpeed = 0.0
							i\zSpeed = 0.0
						EndIf
					Else
						i\DropSpeed = 0
						i\xSpeed = 0.0
						i\zSpeed = 0.0
					EndIf
				EndIf
				
				If i\Dist < HideDist * 0.2 Then
					For i2.Items = Each Items
						If i <> i2 And (Not i2\Picked) And i2\Dist < HideDist * 0.2 Then
							xTemp# = (EntityX(i2\Collider, True) - EntityX(i\Collider, True))
							yTemp# = (EntityY(i2\Collider, True) - EntityY(i\Collider, True))
							zTemp# = (EntityZ(i2\Collider, True) - EntityZ(i\Collider, True))
							
							ed# = (xTemp * xTemp + zTemp * zTemp)
							If ed < 0.07 And Abs(yTemp) < 0.25 Then
								; ~ Items are too close together, push away
								If PlayerRoom\RoomTemplate\Name	<> "room2storage" Then
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
						EndIf
					Next
				EndIf
			Else
				HideEntity(i\Collider)
			EndIf
		Else
			i\DropSpeed = 0.0
			i\xSpeed = 0.0
			i\zSpeed = 0.0
		EndIf
		
		If Not DeletedItem Then
			CatchErrors(Chr(34) + i\itemtemplate\Name + Chr(34) + " item")
		EndIf
		DeletedItem = False
	Next
	
	If ClosestItem <> Null Then
		If MouseHit1 Then PickItem(ClosestItem)
	EndIf
End Function

Function PickItem(item.Items)
	Local n% = 0, z%
	Local CanPickItem = True
	Local FullINV% = True
	
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
	If (Not FullINV) Then
		For n% = 0 To MaxItemAmount - 1
			If Inventory(n) = Null Then
				Select item\itemtemplate\TempName
					Case "1123"
						;[Block]
						If Wearing714 = 0 And WearingGasMask < 3 And WearingHazmat < 3 Then
							If PlayerRoom\RoomTemplate\Name <> "room1123" Then
								ShowEntity(Light)
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
										ShowEntity(Light)
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
						ShowEntity(Light)
						LightFlash = 1.0
						PlaySound_Strict(IntroSFX(11))
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
						If item\itemtemplate\Name = "S-NAV Navigator Ultimate" Then GiveAchievement(AchvSNAV)
						;[End Block]
					Case "hazmatsuit", "hazmatsuit2", "hazmatsuit3"
						;[Block]
						CanPickItem = True
						For z = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\itemtemplate\TempName = "hazmatsuit" Or Inventory(z)\itemtemplate\TempName = "hazmatsuit2" Or Inventory(z)\itemtemplate\TempName = "hazmatsuit3" Then
									CanPickItem = False
									Exit
								ElseIf Inventory(z)\itemtemplate\TempName = "vest" Or Inventory(z)\itemtemplate\TempName = "finevest" Then
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
								If Inventory(z)\itemtemplate\TempName = "vest" Or Inventory(z)\itemtemplate\TempName = "finevest" Then
									CanPickItem = False
									Exit
								ElseIf Inventory(z)\itemtemplate\TempName = "hazmatsuit" Or Inventory(z)\itemtemplate\TempName = "hazmatsuit2" Or Inventory(z)\itemtemplate\TempName = "hazmatsuit3" Then
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
				End Select
				
				If item\itemtemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\Sound))
				item\Picked = True
				item\Dropped = -1
				
				item\itemtemplate\Found = True
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
	Local z%
	
	If WearingHazmat > 0 Then
		Msg = "You cannot drop any items while wearing a hazmat suit."
		MsgTimer = 70 * 5.0
		Return
	EndIf
	
	CatchErrors("Uncaught (DropItem)")
	
	If PlayDropSound Then
		If item\itemtemplate\Sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\Sound))
	EndIf
	
	item\Dropped = 1
	
	ShowEntity(item\Collider)
	PositionEntity(item\Collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
	RotateEntity(item\Collider, EntityPitch(Camera), EntityYaw(Camera) + Rnd(-20.0, 20.0), 0.0)
	MoveEntity(item\Collider, 0.0, -0.1, 0.1)
	RotateEntity(item\Collider, 0.0, EntityYaw(Camera) + Rnd(-110, 110), 0.0)
	ResetEntity(item\Collider)
	
	item\Picked = False
	For z = 0 To MaxItemAmount - 1
		If Inventory(z) = item Then Inventory(z) = Null
	Next
	Select item\itemtemplate\TempName
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

; ~ Update any ailments inflicted by SCP-294 drinks.
Function Update294()
	CatchErrors("Uncaught (Update294)")
	
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
			If (Not ChannelPlaying(VomitCHN)) And (Not Regurgitate) Then
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
			EntityColor(de\OBJ, 0.0, Rnd(200, 255), 0.0)
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