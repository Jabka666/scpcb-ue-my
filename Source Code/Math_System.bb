Function GenerateSeedNumber(Seed$)
 	Local Temp% = 0
 	Local Shift% = 0
	Local i%
	
 	For i = 1 To Len(Seed)
 		Temp = Temp Xor (Asc(Mid(Seed, i, 1)) Shl Shift)
 		Shift = (Shift + 1) Mod 24
	Next
	Return(Temp)
End Function

Function Distance#(x1#, y1#, x2#, y2#)
	Local x# = x2 - x1, y# = y2 - y1
	
	Return(Sqr(x * x + y * y))
End Function

Function CurveValue#(Number#, Old#, Smooth#)
	If fpst\FPSFactor[0] = 0.0 Then Return(Old)
	
	If Number < Old Then
		Return(Max(Old + (Number - Old) * (1.0 / Smooth * fpst\FPSFactor[0]), Number))
	Else
		Return(Min(Old + (Number - Old) * (1.0 / Smooth * fpst\FPSFactor[0]), Number))
	EndIf
End Function

Function CurveAngle#(Val#, Old#, Smooth#)
	If fpst\FPSFactor[0] = 0.0 Then Return(Old)
	
	Local Diff# = WrapAngle(Val) - WrapAngle(Old)
	
	If Diff > 180.0 Then Diff = Diff - 360.0
	If Diff < - 180.0 Then Diff = Diff + 360.0
	Return(WrapAngle(Old + Diff * (1.0 / Smooth * fpst\FPSFactor[0])))
End Function

Function WrapAngle#(Angle#)
	If Angle = INFINITY Then Return(0.0)
	
	While Angle < 0.0
		Angle = Angle + 360.0
	Wend 
	
	While Angle >= 360.0
		Angle = Angle - 360.0
	Wend
	Return(Angle)
End Function

Function GetAngle#(x1#, y1#, x2#, y2#)
	Return(ATan2(y2 - y1, x2 - x1))
End Function

Function CircleToLineSegIsect#(cx#, cy#, r#, l1x#, l1y#, l2x#, l2y#)
	If Distance(cx, cy, l1x, l1y) =< r Then
		Return(True)
	EndIf
	
	If Distance(cx, cy, l2x, l2y) =< r Then
		Return(True)
	EndIf	
	
	Local SegVecX# = l2x - l1x
	Local SegVecY# = l2y - l1y
	Local PntVec1X# = cx - l1x
	Local PntVec1Y# = cy - l1y
	Local PntVec2X# = cx - l2x
	Local PntVec2Y# = cy - l2y
	Local dp1# = SegVecX * PntVec1X + SegVecY * PntVec1Y
	Local dp2# = -SegVecX * PntVec2X - SegVecY * PntVec2Y
	
	If dp1 = 0.0 Or dp2 = 0.0 Then
		; ~ Continue
	ElseIf (dp1 > 0.0 And dp2 > 0.0) Or (dp1 < 0.0 And dp2 < 0.0) Then
		; ~ Continue
	Else
		Return(False)
	EndIf
	
	Local a# = (l2y - l1y) / (l2x - l1x)
	Local b# = -1.0
	Local c# = -(l2y - l1y) / (l2x - l1x) * l1x + l1y
	Local d# = Abs(a * cx + b * cy + c) / Sqr(a * a + b * b)
	
	If d > r Then Return(False)
	Return(True)
End Function

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

Function point_direction#(x1#, z1#, x2#, z2#)
	Local dx#, dz#
	
	dx = x1 - x2
	dz = z1 - z2
	
	Return(ATan2(dz, dx))
End Function

Function point_distance#(x1#, z1#, x2#, z2#)
	Local dx#, dy#
	
	dx = x1 - x2
	dy = z1 - z2
	
	Return(Sqr((dx * dx) + (dy * dy)))
End Function

Function AngleDist#(a0#, a1#)
	Local b# = a0 - a1
	Local bb#
	
	If b < -180.0 Then
		bb = b + 360.0
	Else If b > 180.0 Then
		bb = b - 360.0
	Else
		bb = b
	EndIf
	
	Return(bb)
End Function

Function Inverse#(Number#)
	Return(Float(1.0 - Number#))
End Function

Function Rnd_Array#(Number1#, Number2#, Array1#, Array2#)
	Local WhatArray% = Rand(1, 2)
	
	If WhatArray% = 1
		Return(Rnd(Array1#, Number1#))
	Else
		Return(Rnd(Number2#, Array2#))
	EndIf
End Function

Function f2s$(n#, Count%)
	Return(Left(n, Len(Int(n)) + Count + 1))
End Function

Function Chance%(Chanc%)
	; ~ Perform a chance given a probability
	Return(Rand(0, 100) =< Chanc)
End Function

Function MilliSecs2()
	Local RetVal% = MilliSecs()
	
	If RetVal < 0 Then RetVal = RetVal + 2147483648
	Return(RetVal)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D