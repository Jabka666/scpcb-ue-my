; ~ Instance Base Core
Type InstanceBase
	Field Model%
	Field Mesh$
	Field Texture$
	Field Hider%
End Type

Function CopyInstanceBase%(Mesh$, Texture$ = "")
	Return(CopyInstanced(FindInstanceBase(Mesh, Texture)))
End Function

Function FindInstanceBase%(Mesh$, Texture$ = "")
	Local IB.InstanceBase, IBC.InstanceBase
	
	Mesh = Lower(Mesh)
	Texture = Lower(Texture)
	For IB.InstanceBase = Each InstanceBase
		If IB\Mesh = Mesh And IB\Texture = Texture
			ShowEntity(IB\Model)
			Return(IB\Model)
		EndIf
	Next

	; ~ If can't find, then create it
	IB.InstanceBase = New InstanceBase
	
	For IBC.InstanceBase = Each InstanceBase ; ~ Find same
		If IBC\Mesh = Mesh
			IB\Model = CopyEntity(IBC\Model)
			Exit
		EndIf
	Next
	
	If IB\Model = 0 
		IB\Model = LoadMesh_Strict(Mesh, 0, Instr(Mesh, "addons") < 1)
		SetDeferredEntity(IB\Model, True, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
	EndIf
	
	IB\Mesh = Mesh
	IB\Texture = Texture
	EntityDestructor(IB\Model, @InstanceBaseDestructor)
	
	If Texture <> ""
		Local Tex% = LoadTexture_Strict(Texture)
		
		EntityTexture(IB\Model, Tex)
		UpdateEntityMaterial(IB\Model, DEFERRED_ADDITIVE Or DEFERRED_INSTANTIATED)
		DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	EndIf
	
	; ~ We make a pivot so that the base model is permanently hidden
	IB\Hider = CreateInstanceHider(IB\Model)
	
	Return(IB\Model)
End Function

Function RemoveInstanceBase%(IB.InstanceBase)
	If IB = Null Then Return
	FreeEntity(IB\Model) : IB\Model = 0
End Function

Function DestructInstanceCore%()
	Local IB.InstanceBase
	
	For IB.InstanceBase = Each InstanceBase
		RemoveInstanceBase(IB)
	Next
End Function

Function InstanceBaseDestructor%(Entity%)
	Local IB.InstanceBase
	
	For IB.InstanceBase = Each InstanceBase
		If IB\Model = Entity
			FreeEntity(IB\Hider) : IB\Hider = 0
			Delete IB
			Exit
		EndIf
	Next
End Function

Function CopyInstanced%(Mesh, Parent% = 0)
	Local Entity% = CopyEntity(Mesh, Parent)
	
	If AnimLength(Mesh) < 0
		EntityInstance(Entity, Mesh)
		MakeInstanceChildren(Entity, Mesh)
		ShowEntity(Mesh) ; ~ Instance parent must be show always
	EndIf
	Return(Entity)
End Function

Function MakeInstanceChildren%(Entity%, Mesh%)
	Local Count% = CountChildren(Entity)
	Local i%
	
	For i = 1 To Count
		Local Src% = GetChild(Mesh, i)
		Local Dest% = GetChild(Entity, i)
		
		If EntityClass(Src) = "Mesh" Then EntityInstance(Dest, Src)
		
		MakeInstanceChildren(Dest, Src)
	Next
End Function

Function CreateInstanceHider%(Mesh%)
	Local Hider% = CreatePivot()
	
	EntityInstance(Hider, Mesh)
	HideEntity(Hider)
	Return(Hider)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS