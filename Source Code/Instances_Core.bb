Type InstanceBase
	Field Model%
	Field Mesh$
	Field Texture$
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
			HideInstanceTree(IB\Model) ; ~ The base must stay hidden: only its instanced batch is rendered
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
	
	If IB\Model = 0 Then IB\Model = LoadMesh_Strict(Mesh)
	IB\Mesh = Mesh
	IB\Texture = Texture
	EntityDestructor(IB\Model, FuncPtr(InstanceBaseDestructor))
	
	SetShadowsCasting(IB\Model, True)
	
	If Texture <> ""
		Local Tex% = LoadTexture_Strict(Texture)
		
		EntityTexture(IB\Model, Tex)
		UpdateEntityMaterial(IB\Model)
		DeleteSingleTextureEntryFromCache(Tex) : Tex = 0
	EndIf
	
	HideInstanceTree(IB\Model)
	
	MaskRecursive(IB\Model, 256)
	
	Return(IB\Model)
End Function

Function RemoveInstanceBase%(IB.InstanceBase)
	If IB = Null Then Return
	FreeEntity(IB\Model)
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
			Delete(IB)
			Exit
		EndIf
	Next
End Function

Function CopyInstanced%(Mesh%, Parent% = 0)
	Local Entity% = CopyEntity(Mesh, Parent)
	
	If AnimLength(Mesh) < 0
		If GetInstance(Mesh) <> 0 Then Mesh = GetInstance(Mesh)
		EntityInstance(Entity, Mesh)
		MakeInstanceChildren(Entity, Mesh)
	EndIf
	ShowInstanceTree(Entity)
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

Function HideInstanceTree%(Mesh%)
	HideEntity(Mesh)
	
	Local ChildrenCount% = CountChildren(Mesh)
	Local i%
	
	For i = 1 To ChildrenCount
		HideInstanceTree(GetChild(Mesh, i))
	Next
End Function

Function ShowInstanceTree%(Mesh%)
	ShowEntity(Mesh)
	
	Local ChildrenCount% = CountChildren(Mesh)
	Local i%
	
	For i = 1 To ChildrenCount
		ShowInstanceTree(GetChild(Mesh, i))
	Next
End Function

Function CreateInstanceHider%(Mesh%)
	HideInstanceTree(Mesh)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS