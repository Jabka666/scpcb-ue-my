Type EffectHash
	Field File$
	Field Hash$
	Field CurHash$
End Type

Function CreateEffectHash%(File$, Hash$)
	Local h.EffectHash 
	
	h.EffectHash = New EffectHash
	h\File = Lower(File)
	h\Hash = Hash
	h\CurHash = ""
	UpdateEffectHash(h\File)
End Function

Function UpdateEffectHash%(File$)
	Local h.EffectHash 
	
	File = Lower(File)

	For h.EffectHash = Each EffectHash
		If h\File = File Then h\CurHash = FileHash(File)
	Next
End Function

Function IsAnyEffectsChanged%()
	Local h.EffectHash 
	
	For h.EffectHash = Each EffectHash
		If h\CurHash <> h\Hash Then Return(True)
	Next
	Return(False)
End Function

Function LoadEffectEx%(File$, Defines$ = "")
	UpdateEffectHash(File)
	
	Local f% = ReadFile(File)
	
	If f = 0 Then Return

	If Defines = "" Then Return(LoadEffect(File))
	
	Local Export$ = StripFileName(File) + "TEMP_EFFECT_FILE.fx"
	Local c% = WriteFile(Export)
	Local i%
	
	If c <> 0
		Local StringsAmount% = CountSplitString(Defines, " ")
		
		For i = 0 To StringsAmount - 1
			Local Splitted$ = SplitString(Defines, " ", i)
			
			If Splitted <> "" Then WriteLine(c, "#define " + Splitted)
		Next
		While (Not Eof(f))
			WriteLine(c, ReadLine(f))
		Wend
		CloseFile(c)
	EndIf
	
	Local Effect% = LoadEffect(Export)
	
	CloseFile(f)
	DeleteFile(Export)
	
	Return(Effect)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS