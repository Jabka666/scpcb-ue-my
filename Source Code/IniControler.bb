; IniControler - A part of BlitzToolBox
; Write & Read ini file.
; v1.06 2022.11.12
; https://github.com/ZiYueCommentary/BlitzToolbox

Function IniWriteBuffer(path$, clearPrevious% = 1)
	IniWriteBuffer_(path$, clearPrevious%)
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
	Local Result$ = IniGetString(path$, section$, key$, defaultValue%, allowBuffer%)
	Select Result$
		Case "True", "true"
			Return True
		Case "False", "false"
			Return False
		Default
			Return Int(Result)
	End Select
End Function

Function IniGetFloat#(path$, section$, key$, defaultValue# = 0.0, allowBuffer% = 1)
	Return IniGetFloat_(path$, section$, key$, defaultValue#, allowBuffer%)
End Function

Function IniGetBufferString$(path$, section$, key$, defaultValue$ = "")
	Return IniGetBufferString_(path$, section$, key$, defaultValue$)
End Function

Function IniGetBufferInt%(path$, section$, key$, defaultValue% = 0)
	Local Result$ = IniGetBufferInt_(path$, section$, key$, defaultValue%)
	Select Result$
		Case "True", "true"
			Return True
		Case "False", "false"
			Return False
		Default
			Return Int(Result)
	End Select
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

Function IniExportJson(path$, json$, isMin% = 0, stringOnly% = 0, allowBuffer% = 1)
	IniExportJson_(path$, json$, isMin%, stringOnly%, allowBuffer%)
End Function

Function IniBufferExportJson(path$, json$, isMin% = 0, stringOnly% = 0)
	IniBufferExportJson_(path$, json$, isMin%, stringOnly%)
End Function

Function IniExportHtml(path$, html$, isMin% = 0, isList% = 0, allowBuffer% = 1)
	IniExportHtml_(path$, html$, isMin%, isList%, allowBuffer%)
End Function

Function IniBufferExportHtml(path$, html$, isMin% = 0, isList% = 0)
	IniBufferExportHtml_(path$, html$, isMin%, isList%)
End Function

Function IniExportXml(path$, xml$, isMin% = 0, allowBuffer% = 1)
	IniExportXml_(path$, xml$, isMin%, allowBuffer%)
End Function

Function IniBufferExportXml(path$, xml$, isMin% = 0)
	IniBufferExportXml_(path$, xml$, isMin%)
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
;~IDEal Editor Parameters:
;~C#Blitz3D