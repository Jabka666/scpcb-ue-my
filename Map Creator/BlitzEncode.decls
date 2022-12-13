; BlitzEncode - A part of BlitzToolbox
; Encoding converter.
; v1.0 2022.9.22
; https://github.com/ZiYueCommentary/BlitzToolbox

.lib "BlitzEncode.dll"

ConvertEncoding$(txt$, sourceCodePage%, destCodePage%):"_ConvertEncoding@12"
GetCodePage%():"_GetCodePage@0"

; header file
.lib " "

ConvertANSItoUTF8$(txt$)
ConvertUTF8toANSI$(txt$)