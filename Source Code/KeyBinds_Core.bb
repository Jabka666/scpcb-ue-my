Type Keys
	Field Name$[211]
	Field MOVEMENT_LEFT%, MOVEMENT_RIGHT%, MOVEMENT_UP%, MOVEMENT_DOWN%
	Field CONSOLE%, INVENTORY%, SPRINT%, BLINK%, SAVE%, CROUCH%, SCREENSHOT%
	Field LEAN_LEFT%, LEAN_RIGHT%
End Type

Global key.Keys = New Keys

Function InitKeyNames%()
	Local i%
	
	key\Name[1] = "Esc"
	
	For i = 2 To 10
		key\Name[i] = i - 1
	Next
	
	key\Name[11] = "0"
	key\Name[12] = "-"
	key\Name[13] = "="
	key\Name[14] = GetLocalString("key", "backspace")
	key\Name[15] = GetLocalString("key", "tab")
	key\Name[16] = "Q"
	key\Name[17] = "W"
	key\Name[18] = "E"
	key\Name[19] = "R"
	key\Name[20] = "T"
	key\Name[21] = "Y"
	key\Name[22] = "U"
	key\Name[23] = "I"
	key\Name[24] = "O"
	key\Name[25] = "P"
	key\Name[26] = "["
	key\Name[27] = "]"
	key\Name[28] = GetLocalString("key", "enter")
	key\Name[29] = GetLocalString("key", "lctrl")
	key\Name[30] = "A"
	key\Name[31] = "S"
	key\Name[32] = "D"
	key\Name[33] = "F"
	key\Name[34] = "G"
	key\Name[35] = "H"
	key\Name[36] = "J"
	key\Name[37] = "K"
	key\Name[38] = "L"
	key\Name[39] = ";"
	key\Name[40] = "'"
	key\Name[42] = GetLocalString("key", "lshift")
	key\Name[43] = "\"
	key\Name[44] = "Z"
	key\Name[45] = "X"
	key\Name[46] = "C"
	key\Name[47] = "V"
	key\Name[48] = "B"
	key\Name[49] = "N"
	key\Name[50] = "M"
	key\Name[51] = ","
	key\Name[52] = "."
	key\Name[54] = GetLocalString("key", "rshift")
	key\Name[56] = GetLocalString("key", "lalt")
	key\Name[57] = GetLocalString("key", "space")
	key\Name[58] = GetLocalString("key", "caps")
	key\Name[59] = "F1"
	key\Name[60] = "F2"
	key\Name[61] = "F3"
	key\Name[62] = "F4"
	key\Name[63] = "F5"
	key\Name[64] = "F6"
	key\Name[65] = "F7"
	key\Name[66] = "F8"
	key\Name[67] = "F9"
	key\Name[68] = "F10"
	key\Name[69] = GetLocalString("key", "numlock")
	key\Name[70] = GetLocalString("key", "scrolllock")
	key\Name[71] = GetLocalString("key", "num7")
	key\Name[72] = GetLocalString("key", "num8")
	key\Name[73] = GetLocalString("key", "num9")
	key\Name[74] = GetLocalString("key", "subtract")
	key\Name[75] = GetLocalString("key", "num4")
	key\Name[76] = GetLocalString("key", "num5")
	key\Name[77] = GetLocalString("key", "num6")
	key\Name[78] = GetLocalString("key", "add")
	key\Name[79] = GetLocalString("key", "num1")
	key\Name[80] = GetLocalString("key", "num2")
	key\Name[81] = GetLocalString("key", "num3")
	key\Name[82] = GetLocalString("key", "num0")
	key\Name[83] = GetLocalString("key", "decimal")
	key\Name[87] = "F11"
	key\Name[88] = "F12"
	key\Name[156] = GetLocalString("key", "numenter")
	key\Name[157] = GetLocalString("key", "rctrl")
	key\Name[181] = GetLocalString("key", "divide")
	key\Name[183] = GetLocalString("key", "sysreq")
	key\Name[184] = GetLocalString("key", "ralt")
	key\Name[197] = GetLocalString("key", "pause")
	key\Name[199] = GetLocalString("key", "home")
	key\Name[200] = GetLocalString("key", "up")
	key\Name[201] = GetLocalString("key", "pageup")
	key\Name[203] = GetLocalString("key", "left")
	key\Name[205] = GetLocalString("key", "right")
	key\Name[207] = GetLocalString("key", "end")
	key\Name[208] = GetLocalString("key", "down")
	key\Name[209] = GetLocalString("key", "pagedown")
	key\Name[210] = GetLocalString("key", "insert")
	key\Name[211] = GetLocalString("key", "delete")
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS