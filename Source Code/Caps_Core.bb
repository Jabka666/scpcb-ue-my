Type DCaps
	Field IndependentBits%
End Type

Global DeviceCaps.DCaps

Function GetCaps%()
	If DeviceCaps <> Null Then Delete(DeviceCaps)
	DeviceCaps = New DCaps
	DeviceCaps\IndependentBits = (GfxDeviceCaps(8) And $00040000) <> 0
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS