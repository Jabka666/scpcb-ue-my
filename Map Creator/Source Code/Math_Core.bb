Function Min#(a#, b#)
	If a < b Then 
		Return(a)
	Else
		Return(b)
	EndIf
End Function

Function Max#(a#, b#)
	If a > b Then 
		Return(a)
	Else
		Return(b)
	EndIf
End Function

Const ZONEAMOUNT% = 3

Function GetZone%(y%)
	Return(Min(Floor((Float(MapGridSize - y) / MapGridSize * ZONEAMOUNT)), ZONEAMOUNT - 1))
End Function

;~IDEal Editor Parameters:
;~C#BlitzPlus