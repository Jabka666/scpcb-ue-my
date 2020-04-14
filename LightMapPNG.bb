Graphics3D 640,480,0,2

AppTitle "Optimize lightmaps (convert bmp to png)"

Const FIF_UNKNOWN = -1
Const FIF_BMP = 0
Const FIF_ICO = 1
Const FIF_JPEG = 2
Const FIF_JNG = 3
Const FIF_KOALA = 4
Const FIF_LBM = 5
Const FIF_IFF = FIF_LBM
Const FIF_MNG = 6
Const FIF_PBM = 7
Const FIF_PBMRAW = 8
Const FIF_PCD = 9
Const FIF_PCX = 10
Const FIF_PGM = 11
Const FIF_PGMRAW = 12
Const FIF_PNG = 13
Const FIF_PPM = 14
Const FIF_PPMRAW = 15
Const FIF_RAS = 16
Const FIF_TARGA = 17
Const FIF_TIFF = 18
Const FIF_WBMP = 19
Const FIF_PSD = 20
Const FIF_CUT = 21
Const FIF_XBM = 22
Const FIF_XPM = 23
Const FIF_DDS = 24
Const FIF_GIF = 25
Const FIF_HDR = 26
Const FIF_FAXG3	= 27
Const FIF_SGI = 28
Const FIF_EXR = 29
Const FIF_J2K = 30
Const FIF_JP2 = 31
Const FIF_PFM = 32
Const FIF_PICT = 33
Const FIF_RAW = 34

;INI-funktiot:
Function GetINIString$(file$, section$, parameter$)
	Local TemporaryString$ = ""
	Local f = ReadFile(file)
	
	While Not Eof(f)
		If ReadLine(f) = "["+section+"]" Then
			Repeat 
				TemporaryString = ReadLine(f)
				If Trim( Left(TemporaryString, Max(Instr(TemporaryString,"=")-1,0)) ) = parameter Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString,1) = "[" Or Eof(f)
			CloseFile f
			Return ""
		EndIf
	Wend
	
	CloseFile f
End Function

Function GetINIInt%(file$, section$, parameter$)
	Local strtemp$ = Lower(GetINIString(file$, section$, parameter$))
	
	Select strtemp
		Case "true"
			Return 1
		Case "false"
			Return 0
		Default
			Return Int(strtemp)
	End Select
	Return 
End Function

Function GetINIFloat#(file$, section$, parameter$)
	Return GetINIString(file$, section$, parameter$)
End Function

Function PutINIValue%(INI_sAppName$, INI_sSection$, INI_sKey$, INI_sValue$)
	
; Returns: True (Success) or False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	INI_sFilename$ = CurrentDir$() + "\"  + INI_sAppName
	
; Retrieve the INI data (if it exists)
	
	INI_sContents$= INI_FileToString(INI_sFilename)
	
; (Re)Create the INI file updating/adding the SECTION, KEY and VALUE
	
	INI_bWrittenKey% = False
	INI_bSectionFound% = False
	INI_sCurrentSection$ = ""
	
	INI_lFileHandle = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	INI_lOldPos% = 1
	INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		INI_sTemp$ =Trim$(Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos)))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
				; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				
				; KEY=VALUE
				
				lEqualsPos% = Instr(INI_sTemp, "=")
				If (lEqualsPos <> 0) Then
					If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
						If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
						INI_bWrittenKey = True
					Else
						WriteLine INI_lFileHandle, INI_sTemp
					End If
				End If
				
			End If
			
		End If
		
		; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
	; KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY=VALUE line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	End If
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function


Function INI_FileToString$(INI_sFilename$)
	
	INI_sString$ = ""
	INI_lFileHandle% = ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	End If
	Return INI_sString
	
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
	
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	
	WriteLine INI_lFileHandle, INI_sKey + "=" + INI_sValue
	Return True
	
End Function

; matemaattiset funktiot:
Function Min#(a#,b#)
	If a < b Then Return a Else Return b
End Function

Function Max#(a#,b#)
	If a > b Then Return a Else Return b
End Function

Function StripPath$(file$) 
	
	If Len(file$)>0 
		
		For i=Len(file$) To 1 Step -1 
			
			mi$=Mid$(file$,i,1) 
			If mi$="\" Or mi$="/" Then Return name$ Else name$=mi$+name$ 
			
		Next 
		
	EndIf 
	
	Return name$ 
	
End Function 

Function StripFilename$(file$)
	;Local name$=""
	Local mi$=""
	Local lastSlash%=0
	If Len(file)>0
		For i%=1 To Len(file)
			mi=Mid(file$,i,1)
			If mi="\" Or mi="/" Then
				lastSlash=i
			EndIf
		Next
	EndIf
	
	Return Left(file,lastSlash)
End Function

Function EntityScaleX#(entity, globl=False) 
	If globl Then TFormVector 1,0,0,entity,0 Else TFormVector 1,0,0,entity,GetParent(entity) 
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleY#(entity, globl=False)
	If globl Then TFormVector 0,1,0,entity,0 Else TFormVector 0,1,0,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleZ#(entity, globl=False)
	If globl Then TFormVector 0,0,1,entity,0 Else TFormVector 0,0,1,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function

Function Piece$(s$,entry,char$=" ")
	While Instr(s,char+char)
		s=Replace(s,char+char,char)
	Wend
	For n=1 To entry-1
		p=Instr(s,char)
		s=Right(s,Len(s)-p)
	Next
	p=Instr(s,char)
	If p<1
		a$=s
	Else
		a=Left(s,p-1)
	EndIf
	Return a
End Function

Function KeyValue$(entity,key$,defaultvalue$="")
	properties$=EntityName(entity)
	properties$=Replace(properties$,Chr(13),"")
	key$=Lower(key)
	Repeat
		p=Instr(properties,Chr(10))
		If p Then test$=(Left(properties,p-1)) Else test=properties
		testkey$=Piece(test,1,"=")
		testkey=Trim(testkey)
		testkey=Replace(testkey,Chr(34),"")
		testkey=Lower(testkey)
		If testkey=key Then
			value$=Piece(test,2,"=")
			value$=Trim(value$)
			value$=Replace(value$,Chr(34),"")
			Return value
		EndIf
		If Not p Then Return defaultvalue$
		properties=Right(properties,Len(properties)-p)
	Forever 
End Function

Type Converted
	Field name$
End Type

Function LoadRMesh(file$)
	
	;generate a texture made of white
	;Local blankTexture%
	;blankTexture=CreateTexture(4,4,1,1)
	;ClsColor 255,255,255
	;SetBuffer TextureBuffer(blankTexture)
	;Cls
	;SetBuffer BackBuffer()
	;ClsColor 0,0,0
	
	;read the file
	Local f%=ReadFile(file)
	Local fw%=WriteFile(Replace(file,".rmesh","_opt.rmesh"))
	Local i%,j%,k%,x#,y#,z#,yaw#
	Local vertex%
	Local temp1i%,temp2i%,temp3i%
	Local temp1#,temp2#,temp3#
	Local temp1s$,temp2s$
	;For i=0 To 3 ;reattempt up to 3 times
	;	If f=0 Then
	;		Delay 8
	;		f=ReadFile(file)
	;	Else
	;		Exit
	;	EndIf
	;Next
	;If f=0 Then RuntimeError "Error reading file "+Chr(34)+file+Chr(34)
	;If ReadString(f)<>"RoomMesh" Then RuntimeError Chr(34)+file+Chr(34)+" is not RMESH"
	
	temp2s=ReadString(f)
	WriteString fw,temp2s
	
	file=StripFilename(file)
	
	If FileType(file+"bmp_lm")<>2 Then CreateDir(file+"bmp_lm")
	
	Local count%,count2%
	;drawn meshes
	
	;Local Opaque%,Alpha%,Ladder%
	
	;Opaque=CreateMesh()
	;Alpha=CreateMesh()
	;Ladder=CreateMesh()
	
	count = ReadInt(f)
	WriteInt fw,count
	;Local childMesh%
	;Local surf%,tex%[2],brush%
	
	;Local isAlpha%
	
	Local u#,v#
	
	For i=1 To count ;drawn mesh
		;childMesh=CreateMesh()
		
		;surf=CreateSurface(childMesh)
		
		;brush=CreateBrush()
		
		;tex[0]=0 : tex[1]=0
		
		;isAlpha=0
		For j=0 To 1
			temp1i=ReadByte(f)
			WriteByte fw,temp1i
			If temp1i<>0 Then
				temp1s=ReadString(f)
				;tex[j]=GetTextureFromCache(temp1s)
				;If tex[j]=0 Then ;texture is not in cache
				;	Select True
				;		Case temp1i<3
				;			tex[j]=LoadTexture(file+temp1s,1)
				;		Default
				;			tex[j]=LoadTexture(file+temp1s,3)
				;	End Select
				;	
				;	If tex[j]<>0 Then
				;		If temp1i=1 Then TextureBlend tex[j],5
				;		AddTextureToCache(tex[j])
				;	EndIf
				;	
				;EndIf
				;If tex[j]<>0 Then
				;	isAlpha=2
				;	If temp1i=3 Then isAlpha=1
				;	
				;	TextureCoords tex[j],1-j
				;EndIf
				If Instr(temp1s,".bmp")<>0 Then
					done%=0
					For r.Converted = Each Converted
						If r\name=temp1s Then done=1 : Exit
					Next
					If Not done Then
						r.Converted = New Converted
						r\name=temp1s
						loadTex%=FI_Load(FIF_BMP,file+temp1s,0)
						DebugLog temp1s
					EndIf
					CopyFile(file+temp1s,file+"bmp_lm\"+temp1s)
					DeleteFile file+temp1s
					temp1s=Replace(temp1s,".bmp",".png")
					If Not done Then
						FI_Save(FIF_PNG,loadTex,file+temp1s,0)
						FI_Unload(loadTex)
					EndIf
				EndIf
				WriteString fw,temp1s
			EndIf
		Next
		
		;If isAlpha=1 Then
		;	If tex[1]<>0 Then
		;		TextureBlend tex[1],2
		;		BrushTexture brush,tex[1],0,0
		;	Else
		;		BrushTexture brush,blankTexture,0,0
		;	EndIf
		;Else
			
			;If BumpEnabled And temp1s<>"" Then
			;	bumptex = GetBumpFromCache(temp1s)	
			;Else
			;	bumptex = 0
			;EndIf
			
			;If bumptex<>0 And 0 Then 
			;	BrushTexture brush, tex[1], 0, 0	
			;	BrushTexture brush, bumptex, 0, 1
			;	If tex[0]<>0 Then 
			;		BrushTexture brush, tex[0], 0, 2	
			;	Else
			;		BrushTexture brush,blankTexture,0,2
			;	EndIf
			;	
			;Else
			;For j=0 To 1
			;	If tex[j]<>0 Then
			;		BrushTexture brush,tex[j],0,j
			;	Else
			;		BrushTexture brush,blankTexture,0,j
			;	EndIf
			;Next
			;EndIf
			
		;EndIf
		
		;surf=CreateSurface(childMesh)
		
		;If isAlpha>0 Then PaintSurface surf,brush
		
		;FreeBrush brush : brush = 0
		
		count2=ReadInt(f) ;vertices
		WriteInt fw,count2
		
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			WriteFloat fw,x
			WriteFloat fw,y
			WriteFloat fw,z
			;vertex=AddVertex(surf,x,y,z)
			
			;texture coords
			For k%=0 To 1
				u=ReadFloat(f) : v=ReadFloat(f)
				WriteFloat fw,u
				WriteFloat fw,v
				;VertexTexCoords surf,vertex,u,v,0.0,k
			Next
			
			;colors
			temp1i=ReadByte(f)
			temp2i=ReadByte(f)
			temp3i=ReadByte(f)
			WriteByte fw,temp1i
			WriteByte fw,temp2i
			WriteByte fw,temp3i
		Next
		
		count2=ReadInt(f) ;polys
		WriteInt fw,count2
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			WriteInt fw,temp1i
			WriteInt fw,temp2i
			WriteInt fw,temp3i
			;AddTriangle(surf,temp1i,temp2i,temp3i)
		Next
		
		;If isAlpha=1 Then
		;	AddMesh childMesh,Alpha
		;Else
		;	AddMesh childMesh,Opaque
		;EndIf
		;EntityParent childMesh,Opaque
		;EntityAlpha childMesh,0.0
		;EntityType childMesh,HIT_MAP
		;EntityPickMode childMesh,2
		
	Next
	
	;If BumpEnabled Then ; And 0
	;	For i = 1 To CountSurfaces(Opaque)
	;		surf = GetSurface(Opaque,i)
	;		brush = GetSurfaceBrush(surf)
	;		tex[0] = GetBrushTexture(brush,1)
	;		temp1s$ =  StripPath(TextureName(tex[0]))
	;		;DebugLog temp1s$
	;		If temp1s$<>"" Then 
	;			mat.Materials=GetCache(temp1s)
	;			If mat<>Null Then
	;				If mat\Bump<>0 Then
	;					tex[1] = GetBrushTexture(brush,0)
	;					
	;					BrushTexture brush, tex[1], 0, 2	
	;					BrushTexture brush, mat\Bump, 0, 1
	;					BrushTexture brush, tex[0], 0, 0					
	;					
	;					PaintSurface surf,brush
	;					
	;					If tex[1]<>0 Then FreeTexture tex[1] : tex[1]=0 ;DebugLog "lkmlkm" : 
	;				EndIf
	;			EndIf
	;			
	;			If tex[0]<>0 Then FreeTexture tex[0] : tex[0]=0 ;DebugLog "sdfsf" : 
	;		EndIf
	;		If brush<>0 Then FreeBrush brush : brush=0
	;	Next
	;EndIf
	
	;Local hiddenMesh%
	;hiddenMesh=CreateMesh()
	
	;count=ReadInt(f) ;invisible collision mesh
	;For i%=1 To count
	;	surf=CreateSurface(hiddenMesh)
	;	count2=ReadInt(f) ;vertices
	;	For j%=1 To count2
	;		;world coords
	;		x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
	;		vertex=AddVertex(surf,x,y,z)
	;	Next
	;	
	;	count2=ReadInt(f) ;polys
	;	For j%=1 To count2
	;		temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
	;		AddTriangle(surf,temp1i,temp2i,temp3i)
	;	Next
	;Next
	;
	;count=ReadInt(f) ;point entities
	;For i%=1 To count
	;	temp1s=ReadString(f)
	;	DebugLog temp1s
	;	Select temp1s
	;		Case "screen"
	;			
	;			temp1=ReadFloat(f)*RoomScale
	;			temp2=ReadFloat(f)*RoomScale
	;			temp3=ReadFloat(f)*RoomScale
	;			
	;			temp2s=ReadString(f)
	;			
	;			If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
	;				Local ts.TempScreens = New TempScreens	
	;				ts\x = temp1
	;				ts\y = temp2
	;				ts\z = temp3
	;				ts\imgpath = temp2s
	;				ts\roomtemplate = rt
	;			EndIf
	;			
	;		Case "waypoint"
	;			
	;			temp1=ReadFloat(f)*RoomScale
	;			temp2=ReadFloat(f)*RoomScale
	;			temp3=ReadFloat(f)*RoomScale
	;			
	;			Local w.TempWayPoints = New TempWayPoints
	;			w\roomtemplate = rt
	;			w\x = temp1
	;			w\y = temp2
	;			w\z = temp3
	;			
	;		Case "light"
	;			
	;			temp1=ReadFloat(f)*RoomScale
	;			temp2=ReadFloat(f)*RoomScale
	;			temp3=ReadFloat(f)*RoomScale
	;			
	;			If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
	;				range# = ReadFloat(f)/2000.0
	;				lcolor$=ReadString(f)
	;				intensity# = Min(ReadFloat(f)*0.8,1.0)
	;				r%=Int(Piece(lcolor,1," "))*intensity
	;				g%=Int(Piece(lcolor,2," "))*intensity
	;				b%=Int(Piece(lcolor,3," "))*intensity
	;				
	;				AddTempLight(rt, temp1,temp2,temp3, 2, range, r,g,b)
	;			Else
	;				ReadFloat(f) : ReadString(f) : ReadFloat(f)
	;			EndIf
	;			
	;		Case "spotlight"
	;			
	;			temp1=ReadFloat(f)*RoomScale
	;			temp2=ReadFloat(f)*RoomScale
	;			temp3=ReadFloat(f)*RoomScale
	;			
	;			If temp1<>0 Or temp2<>0 Or temp3<>0 Then 
	;				range# = ReadFloat(f)/2000.0
	;				lcolor$=ReadString(f)
	;				intensity# = Min(ReadFloat(f)*0.8,1.0)
	;				r%=Int(Piece(lcolor,1," "))*intensity
	;				g%=Int(Piece(lcolor,2," "))*intensity
	;				b%=Int(Piece(lcolor,3," "))*intensity
	;				
	;				Local lt.LightTemplates = AddTempLight(rt, temp1,temp2,temp3, 2, range, r,g,b)
	;				angles$=ReadString(f)
	;				pitch#=Piece(angles,1," ")
	;				yaw#=Piece(angles,2," ")
	;				lt\pitch = pitch
	;				lt\yaw = yaw
	;				
	;				lt\innerconeangle = ReadInt(f)
	;				lt\outerconeangle = ReadInt(f)
	;			Else
	;				ReadFloat(f) : ReadString(f) : ReadFloat(f) : ReadString(f) : ReadInt(f) : ReadInt(f)
	;			EndIf
	;			
	;		Case "soundemitter"
	;			
	;			temp1i=0
	;			
	;			For j = 0 To 3
	;				If rt\TempSoundEmitter[j]=0 Then
	;					rt\TempSoundEmitterX[j]=ReadFloat(f)*RoomScale
	;					rt\TempSoundEmitterY[j]=ReadFloat(f)*RoomScale
	;					rt\TempSoundEmitterZ[j]=ReadFloat(f)*RoomScale
	;					rt\TempSoundEmitter[j]=ReadInt(f)
	;					
	;					rt\TempSoundEmitterRange[j]=ReadFloat(f)
	;					temp1i=1
	;					Exit
	;				EndIf
	;			Next
	;			
;				If temp1i=0 Then
;					ReadFloat(f)
;					ReadFloat(f)
;					ReadFloat(f)
;					ReadInt(f)
;					ReadFloat(f)
;				EndIf
;				
;			Case "playerstart"
;				
;				temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
;				
;				angles$=ReadString(f)
;				pitch#=Piece(angles,1," ")
;				yaw#=Piece(angles,2," ")
;				roll#=Piece(angles,3," ")
;				If cam Then
;					PositionEntity cam,temp1,temp2,temp3
;					RotateEntity cam,pitch,yaw,roll
;				EndIf
;				
;			Case "ladder"
;				
;				count2=ReadInt(f)
;				
;				surf = CreateSurface(Ladder)
;				
;				For j=1 To count2
;					temp1=ReadFloat(f) : temp2=ReadFloat(f) : temp3=ReadFloat(f)
;					AddVertex surf,temp1,temp2,temp3
;				Next
;				
;				count2=ReadInt(f)
;				
;				For j=1 To count2
;					temp1i=ReadInt(f) : temp2i=ReadInt(f) : temp3i=ReadInt(f)
;					AddTriangle(surf,temp1i,temp2i,temp3i)
;				Next
;				
;		End Select
	;	Next
	
	;Local obj%
	;
	;temp1i=CopyMesh(Alpha)
	;FlipMesh temp1i
	;AddMesh temp1i,Alpha
	;FreeEntity temp1i
	;
	;If brush <> 0 Then FreeBrush brush
	;
	;AddMesh Alpha,Opaque
	;FreeEntity Alpha
	;
	;EntityFX Opaque,3
	;
	;EntityAlpha hiddenMesh,0.0
	;EntityAlpha Opaque,1.0
	;
	;EntityType Opaque,HIT_MAP
	;EntityType hiddenMesh,HIT_MAP
	;FreeTexture blankTexture
	;
	;obj=CreatePivot()
	;CreatePivot(obj) ;skip "meshes" object
	;EntityParent Opaque,obj
	;EntityParent hiddenMesh,obj
	;CreatePivot(Room) ;skip "pointentites" object
	;CreatePivot(Room) ;skip "solidentites" object
	;EntityParent Ladder,obj
	;
	;EntityAlpha Ladder,0.0
	;EntityType Ladder,HIT_LADDER
	;
	;Return obj%
	
	While (Not Eof(f))
		temp1i=ReadByte(f)
		WriteByte fw,temp1i
	Wend
	
End Function


Global state%=0

SetBuffer BackBuffer()
ClsColor 0,0,0
Cls
Color 255,255,255
Text 5,5,"Press a key:"
Text 5,25,"1 - Convert BMPs to PNGs, and modify rmeshes to use them"
Text 5,45,"2 - Convert BMPs to PNGs for a specific room and modify it to use them"
Text 5,65,"3 - Revert options.ini to use the old rmeshes"
Text 5,85,"ESC - Close without doing anything"
Flip

While (Not KeyHit(1))
	If KeyHit(2) Or KeyHit(79) Then state=1 : Exit
	If KeyHit(3) Or KeyHit(80) Then state=2 : Exit
Wend

Local Stri$,TemporaryString$,f%

Type INIConvert
	Field file$
	Field section$
	Field key$
	Field value$
End Type

Local ic.INIConvert

If state=1 Then
	If FileSize("NineTailedFoxMod\Data\rooms_opt.ini")=0 Then
		CopyFile "NineTailedFoxMod\Data\rooms.ini","NineTailedFoxMod\Data\rooms_bmp.ini"
	EndIf
	
	f%=ReadFile("NineTailedFoxMod\Data\rooms.ini")
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			If TemporaryString <> "room ambience" Then
				Stri=GetINIString("NineTailedFoxMod\Data\rooms.ini",TemporaryString,"mesh path")
				
				LoadRMesh(Stri)
				
				;mesh=LoadAnimMesh(Stri)
				;SaveRoomMesh(mesh,"new\"+StripPath(Replace(Stri,".b3d",".rmesh")))
				
				Cls
				Text 5,5,"Converted "+Chr(34)+Stri+Chr(34)
				Flip
				
				ic.INIConvert=New INIConvert
				ic\file="NineTailedFoxMod\Data\rooms.ini"
				ic\section=TemporaryString
				ic\key="mesh path"
				ic\value=Replace(Stri,".rmesh",".rmesh")
				
				;PutINIValue("Data\rooms.ini",TemporaryString,"mesh path",Replace(Stri,".b3d",".rmesh"))
			EndIf
		EndIf
	Wend
	
	For ic.INIConvert=Each INIConvert
		PutINIValue(ic\file,ic\section,ic\key,ic\value)
	Next
	
	Cls
	Text 5,5,"Conversion complete"
	Flip
	Delay 1000
	
	CloseFile f
ElseIf state=2
	
	Cls
	Flip
	FlushKeys()
	Stri=Input("Path for the room to be converted: ")
	LoadRMesh(Stri)
	Cls
	Text 5,5,"Conversion of "+Stri+" complete"
	Flip
	Delay 1000
	
ElseIf state=3
	f%=ReadFile("NineTailedFoxMod\Data\rooms.ini")
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			If TemporaryString <> "room ambience" Then
				Stri=GetINIString("NineTailedFoxMod\Data\rooms.ini",TemporaryString,"mesh path")
				
				ic.INIConvert=New INIConvert
				ic\file="NineTailedFoxMod\Data\rooms.ini"
				ic\section=TemporaryString
				ic\key="mesh path"
				ic\value=Replace(Stri,"_opt.rmesh",".rmesh")
				
				;PutINIValue("Data\rooms.ini",TemporaryString,"mesh path",Replace(Stri,".rmesh",".b3d"))
			EndIf
		EndIf
	Wend
	
	For ic.INIConvert=Each INIConvert
		PutINIValue(ic\file,ic\section,ic\key,ic\value)
	Next
	
	Cls
	Text 5,5,"Reset complete, you need to restore the bmp lightmaps manually"
	Flip
	Delay 1000
	
	CloseFile(f)
EndIf


;~IDEal Editor Parameters:
;~F#2B#40#4E#52#A3#B1#B9#C1#C5#C9#DA#EA#EF#F4#F9#10A#120#124#2EC
;~C#Blitz3D