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

Function LoadEffectEx%(File$, Defines$ = "", Necessary% = True)
	UpdateEffectHash(File)
	
	Local Effect% = LoadEffect(File, Defines)
	
	If Necessary And GetEffectError() <> "" Then RuntimeErrorEx(Format(Format(GetLocalString("runerr", "effect.failed.load"), File, "{0}"), GetEffectError(), "{1}"))
	Return(Effect)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS