Type DCaps
	Field IndependentBits%
End Type

Global DeviceCaps.DCaps

Function GetCaps%()
	If DeviceCaps <> Null Then Delete(DeviceCaps)
	DeviceCaps = New DCaps
	DeviceCaps\IndependentBits = True
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS