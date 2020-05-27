Global AASelectedFont%
Global AATextCam%, AATextSprite%[150]
Global AACharW%, AACharH%
Global AATextEnable_Prev% = AATextEnable
Global AACamViewW%, AACamViewH%

Type AAFont
	Field Texture%
	Field Backup% ; ~ Images don't get erased by clearworld
	Field x%[128] ; ~ Not going to bother with unicode
	Field y%[128]
	Field w%[128]
	Field h%[128]
	Field LowResFont% ; ~ For use on other buffers
	Field mW%
	Field mH%
	Field TexH%
	Field IsAA%
End Type

Function InitAAFont()
	If (Not AATextEnable) Then Return
	
	; ~ Create camera
	Local Cam% = CreateCamera()
	
	CameraViewport(Cam, 0, 0, 10, 10)
	CameraZoom(Cam, 0.1)
	CameraClsMode(Cam, 0, 0)
	CameraRange(Cam, 0.1, 1.5)
	MoveEntity(Cam, 0, 0, -20000.0)
	AATextCam = Cam
	CameraProjMode(Cam, 0)

	; ~ Create sprite
	Local SPR% = CreateMesh(Cam)
	Local SF% = CreateSurface(SPR)
	Local i%
	
	AddVertex(SF, -1.0, 1.0, 0.0, 0.0, 0.0)
	AddVertex(SF, 1.0, 1.0, 0.0, 1.0, 0.0)  
	AddVertex(SF, -1.0, -1.0, 0.0, 0.0, 1.0)
	AddVertex(SF, 1.0, -1.0, 0.0, 1.0, 1.0) 
	AddTriangle(SF, 0, 1, 2)
	AddTriangle(SF, 3, 2, 1)
	EntityFX(SPR, 17 + 32)
	PositionEntity SPR, 0, 0, 1.0001
	EntityOrder(SPR, -100001)
	EntityBlend(SPR, 1)
	AATextSprite[0] = SPR
	HideEntity(AATextSprite[0])
	For i = 1 To 149
		SPR = CopyMesh(AATextSprite[0], Cam)
		EntityFX(SPR, 17 + 32)
		PositionEntity(SPR, 0, 0, 1.0001)
		EntityOrder(SPR, -100001)
		EntityBlend(SPR, 1)
		AATextSprite[i] = SPR
		HideEntity AATextSprite[i]
	Next
		
End Function

Function AASpritePosition(Ind%, x%, y%)
	; ~ THE HORROR
	Local nX# = (((Float(x - (AACamViewW / 2)) / Float(AACamViewW)) * 2))
	Local nY# = -(((Float(y - (AACamViewH / 2)) / Float(AACamViewW)) * 2))
	
	; ~ How does this work pls help
	nX = nX - ((1.0 / Float(AACamViewW)) * (((AACharW - 2) Mod 2))) + (1.0 / Float(AACamViewW))
	nY = nY - ((1.0 / Float(AACamViewW)) * (((AACharH - 2) Mod 2))) + (1.0 / Float(AACamViewW))
	
	PositionEntity(AATextSprite[Ind], nX, nY, 1.0)
End Function

Function AASpriteScale(Ind%, w%, h%)
	ScaleEntity(AATextSprite[Ind], 1.0 / Float(AACamViewW) * Float(w), 1.0 / Float(AACamViewW) * Float(h), 1)
	AACharW = w
	AACharH = h
End Function

Function ReloadAAFont() ; ~ CALL ONLY AFTER CLEARWORLD
	If (Not AATextEnable) Then Return
	
	InitAAFont()
	For font.AAFont = Each AAFont
		If font\IsAA Then
			font\Texture = CreateTexture(1024, 1024, 3)
			LockBuffer(ImageBuffer(font\Backup))
			LockBuffer(TextureBuffer(font\Texture))
			For ix% = 0 To 1023
				For iy% = 0 To font\texH
					px% = ReadPixelFast(ix, iy, ImageBuffer(font\Backup)) Shl 24
					WritePixelFast(ix, iy, $FFFFFF + px, TextureBuffer(font\Texture))
				Next
			Next
			UnlockBuffer(TextureBuffer(font\Texture))
			UnlockBuffer(ImageBuffer(font\Backup))
		EndIf
	Next
End Function

Function AASetFont(Fnt%)
	AASelectedFont = Fnt
	
	Local font.AAFont = Object.AAFont(AASelectedFont)
	Local i%
	
	If AATextEnable And font\IsAA Then
		For i = 0 To 149
			EntityTexture(AATextSprite[i], font\Texture)
		Next
	EndIf
End Function

Function AAStringWidth%(Txt$)
	Local font.AAFont = Object.AAFont(AASelectedFont)
	
	If AATextEnable And font\IsAA Then
		Local RetVal% = 0
		Local i%
		
		For i = 1 To Len(Txt)
			Local Char% = Asc(Mid(Txt, i, 1))
			
			If Char >= 0 And Char =< 127 Then
				RetVal = RetVal + font\w[Char] - 2
			EndIf
		Next
		Return(RetVal + 2)
	Else
		SetFont(font\LowResFont)
		Return(StringWidth(Txt))
	EndIf
End Function

Function AAStringHeight%(Txt$)
	Local font.AAFont = Object.AAFont(AASelectedFont)
	
	If AATextEnable And font\IsAA Then
		Return(font\mH)
	Else
		SetFont(font\LowResFont)
		Return(StringHeight(Txt))
	EndIf
End Function

Function AAText(x%, y%, Txt$, cX% = False, cY% = False, A# = 1.0)
	If Len(Txt) = 0 Then Return
	
	Local font.AAFont = Object.AAFont(AASelectedFont)
	
	If (GraphicsBuffer() <> BackBuffer()) Or (Not AATextEnable) Or (Not font\IsAA) Then
		SetFont(font\LowResFont)
		
		Local oldR% = ColorRed()
		Local oldG% = ColorGreen()
		Local oldB% = ColorBlue()
		
		Color(oldR * A, oldG * A, oldB * A)
		Text(x, y, Txt, cX, cY)
		Color(oldR, oldG, oldB)
		Return
	EndIf
	
	If cX Then
		x = x - (AAStringWidth(Txt) / 2)
	EndIf
	
	If cY Then
		y = y - (AAStringHeight(Txt) / 2)
	EndIf
	
	If Camera <> 0 Then HideEntity Camera
	If Ark_Blur_Cam <> 0 Then HideEntity(Ark_Blur_Cam)
	
	Local tX% = 0
	
	CameraProjMode(AATextCam, 2)
	
	Local Char%
	Local tW% = 0
	Local i%
	
	For i = 1 To Len(Txt)
		Char = Asc(Mid(Txt, i, 1))
		If Char >= 0 And Char =< 127 Then
			tW = tW + font\w[Char]
		EndIf
	Next
	
	AACamViewW = tW
	AACamViewW = AACamViewW + (AACamViewW Mod 2)
	AACamViewH = AAStringHeight(Txt)
	AACamViewH = AACamViewH + (AACamViewH Mod 2)
	
	Local vX% = x : If vX < 0 Then vX = 0
	Local vY% = y : If vY < 0 Then vY = 0
	Local vW% = AACamViewW + (x - vX) : If vW + vX > GraphicWidth Then vW = GraphicWidth - vX
	Local vH% = AACamViewH + (y - vY) : If vH + vY > GraphicHeight Then vH = GraphicHeight - vY
	
	vW = vW - (vW Mod 2)
	vH = vH - (vH Mod 2)
	AACamViewH = AACamViewH + (AACamViewH Mod 2)
	AACamViewW = vW : AACamViewH = vH
	
	CameraViewport(AATextCam, vX, vY, vW, vH)
	For i = 1 To Len(Txt)
		EntityAlpha(AATextSprite[i - 1], A)
		EntityColor(AATextSprite[i - 1], ColorRed(), ColorGreen(), ColorBlue())
		ShowEntity(AATextSprite[i - 1])
		Char% = Asc(Mid(Txt, i, 1))
		If Char>=0 And Char =< 127 Then
			AASpriteScale(i - 1, font\w[Char], font\h[Char])
			AASpritePosition(i - 1, tX + (x - vX) + (font\w[Char] / 2), (y - vY) + (font\h[Char] / 2))
			VertexTexCoords(GetSurface(AATextSprite[i - 1], 1), 0, Float(font\x[Char]) / 1024.0, Float(font\y[Char]) / 1024.0)
			VertexTexCoords(GetSurface(AATextSprite[i - 1], 1), 1, Float(font\x[Char] + font\w[Char]) / 1024.0, Float(font\y[Char]) / 1024.0)
			VertexTexCoords(GetSurface(AATextSprite[i - 1], 1), 2, Float(font\x[Char]) / 1024.0, Float(font\y[Char] + font\h[Char]) / 1024.0)
			VertexTexCoords(GetSurface(AATextSprite[i - 1], 1), 3, Float(font\x[Char] + font\w[Char]) / 1024.0, Float(font\y[Char] + font\h[Char]) / 1024.0)
			tX = tX + font\w[Char] - 2
		EndIf
	Next
	RenderWorld
	CameraProjMode(AATextCam, 0)
	
	For i = 1 To Len(Txt)
		HideEntity(AATextSprite[i - 1])
	Next
	
	If Camera <> 0 Then ShowEntity(Camera)
	If Ark_Blur_Cam <> 0 Then ShowEntity(Ark_Blur_Cam)
End Function

Function AALoadFont%(File$ = "Tahoma", Height% = 13, Bold% = 0, Italic% = 0, UnderLine% = 0, AATextScaleFactor% = 2)
	Local newFont.AAFont = New AAFont
	
	newFont\LowResFont = LoadFont(File, Height, Bold, Italic, UnderLine)
	
	SetFont(newFont\LowResFont)
	newFont\mW = FontWidth()
	newFont\mH = FontHeight()
	
	If AATextEnable And AATextScaleFactor > 1 Then
		Local hResFont% = LoadFont(File, Height * AATextScaleFactor, Bold, Italic, UnderLine)
		Local tImage% = CreateTexture(1024, 1024, 3)
		Local tX% = 0
		Local tY% = 1
		
		SetFont(hResFont)
		
		Local tCharImage% = CreateImage(FontWidth() + 2 * AATextScaleFactor, FontHeight() + 2 * AATextScaleFactor)
		
		ClsColor(0, 0, 0)
		LockBuffer(TextureBuffer(tImage))
		
		Local miy% = newFont\mH * ((newFont\mW * 95 / 1024) + 2)
		
		newFont\mW = 0
		
		Local iX%, iY%
		
		For iX% = 0 To 1023
			For iY% = 0 To miy
				WritePixelFast(iX, iY, $FFFFFF, TextureBuffer(tImage))
			Next
		Next
		
		Local i%
		
		For i = 32 To 126
			SetBuffer(ImageBuffer(tCharImage))
			Cls
			
			Color(255, 255, 255)
			SetFont(hResFont)
			Text(AATextScaleFactor / 2, AATextScaleFactor / 2, Chr(i))
			
			Local tW% = StringWidth(Chr(i))
			Local th% = FontHeight()
			
			SetFont(newFont\LowResFont)
			
			Local dsW% = StringWidth(Chr(i))
			Local dsH% = FontHeight()
			
			Local wRatio# = Float(tW) / Float(dsW)
			Local hRatio# = Float(th) / Float(dsH)
			
			SetBuffer(BackBuffer())
				
			LockBuffer(ImageBuffer(tCharImage))
			
			For iY = 0 To dsH - 1
				For iX% = 0 To dsW - 1
					Local rsX% = Int(Float(iX) * wRatio - (wRatio * 0.0))
					
					If (rsX < 0) Then rsX = 0
					
					Local rsY% = Int(Float(iY) * hRatio - (hRatio * 0.0))
					
					If (rsY < 0) Then rsY = 0
					
					Local rdX% = Int(Float(iX) * wRatio + (wRatio * 1.0))
					
					If (rdX > tW) Then rdX = tW - 1
					
					Local rdY% = Int(Float(iY) * hRatio + (hRatio * 1.0))
					
					If (rdY > th) Then rdY = th - 1
					
					Local Ar% = 0
					
					If Abs(rsX - rdX) * Abs(rsY-rdY) > 0 Then
						Local iiY%, iiX%
						
						For iiY% = rsY To rdY - 1
							For iiX = rsX To rdX - 1
								Ar = Ar + ((ReadPixelFast(iiX, iiY, ImageBuffer(tCharImage)) And $FF))
							Next
						Next
						Ar = Ar / (Abs(rsX - rdX) * Abs(rsY - rdY))
						If Ar > 255 Then Ar = 255
						Ar = ((Float(Ar) / 255.0) ^ (0.5)) * 255
					EndIf
					WritePixelFast(iX + tX, iY + tY, $FFFFFF + (Ar Shl 24), TextureBuffer(tImage))
				Next
			Next
			
			UnlockBuffer(ImageBuffer(tCharImage))
			
			newFont\x[i] = tX
			newFont\y[i] = tY
			newFont\w[i] = dsW + 2
			
			If newFont\mW < newFont\w[i] - 3 Then newFont\mW = newFont\w[i] - 3
			
			newFont\h[i] = dsH + 2
			tX = tX + newFont\w[i] + 2
			If (tX > 1024 - FontWidth() - 4) Then
				tX = 0
				tY = tY + FontHeight() + 6
			EndIf
		Next
		
		newFont\TexH = miy
		
		Local Backup% = CreateImage(1024, 1024)
		
		LockBuffer(ImageBuffer(Backup))
		For iX = 0 To 1023
			For iY = 0 To newFont\TexH
				Local pX%
				
				pX = ReadPixelFast(iX, iY, TextureBuffer(tImage)) Shr 24
				pX = pX + (pX Shl 8) + (pX Shl 16)
				WritePixelFast(iX, iY, $FF000000 + pX, ImageBuffer(Backup))
			Next
		Next
		UnlockBuffer(ImageBuffer(Backup))
		newFont\Backup = Backup
		
		UnlockBuffer(TextureBuffer(tImage))
		
		FreeImage(tCharImage)
		FreeFont(hResFont)
		newFont\Texture = tImage
		newFont\IsAA = True
	Else
		newFont\IsAA = False
	EndIf
	Return(Handle(newFont))
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D