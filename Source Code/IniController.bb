; IniController - A part of BlitzToolbox
; Reading & writing INI files.
; v1.10 2026.02.23
; https://github.com/ZiYueCommentary/BlitzToolbox

Function IniWriteBuffer(path$, buffer$ = "", clearPrevious% = 1)
	IniWriteBuffer_(path$, buffer$, clearPrevious%)
End Function

Function IniWriteString(path$, section$, key$, value$, updateBuffer% = 1)
	IniWriteString_(path$, section$, key$, value$, updateBuffer%)
End Function

Function IniWriteInt(path$, section$, key$, value%, updateBuffer% = 1)
	IniWriteInt_(path$, section$, key$, value%, updateBuffer%)
End Function

Function IniWriteFloat(path$, section$, key$, value#, updateBuffer% = 1)
	IniWriteFloat_(path$, section$, key$, value#, updateBuffer%)
End Function

Function IniGetString$(path$, section$, key$, defaultValue$ = "", allowBuffer% = 1)
	Return IniGetString_(path$, section$, key$, defaultValue$, allowBuffer%)
End Function

Function IniGetInt%(path$, section$, key$, defaultValue% = 0, allowBuffer% = 1)
	Return IniGetInt_(path$, section$, key$, defaultValue%, allowBuffer%)
End Function

Function IniGetFloat#(path$, section$, key$, defaultValue# = 0.0, allowBuffer% = 1)
	Return IniGetFloat_(path$, section$, key$, defaultValue#, allowBuffer%)
End Function

Function IniGetBufferString$(path$, section$, key$, defaultValue$ = "")
	Return IniGetBufferString_(path$, section$, key$, defaultValue$)
End Function

Function IniGetBufferInt%(path$, section$, key$, defaultValue% = 0)
	Return IniGetBufferInt_(path$, section$, key$, defaultValue%)
End Function

Function IniGetBufferFloat#(path$, section$, key$, defaultValue# = 0.0)
	Return IniGetBufferFloat_(path$, section$, key$, defaultValue#)
End Function

Function IniSectionExist%(path$, section$, allowBuffer% = 1)
	Return IniSectionExist_(path$, section$, allowBuffer%)
End Function

Function IniKeyExist%(path$, section$, key$, allowBuffer% = 1)
	Return IniKeyExist_%(path$, section$, key$, allowBuffer%)
End Function

Function IniRemoveKey(path$, section$, key$, updateBuffer% = 1)
	IniRemoveKey_(path$, section$, key$, updateBuffer%)
End Function

Function IniRemoveSection(path$, section$, updateBuffer% = 1)
	IniRemoveSection_(path$, section$, updateBuffer%)
End Function

Function IniExportIni(path$, ini$, isMin% = 0, allowBuffer% = 1)
	IniExportIni_(path$, ini$, isMin%, allowBuffer%)
End Function

Function IniBufferExportIni(path$, ini$, isMin% = 0)
	IniBufferExportIni_(path$, ini$, isMin%)
End Function