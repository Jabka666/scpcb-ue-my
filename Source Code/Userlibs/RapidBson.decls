; RapidBson - A part of BlitzToolbox
; A fast JSON parser/generator for Blitz3D with both SAX/DOM style API.
; v1.0 2024.10.3
; https://github.com/ZiYueCommentary/BlitzToolbox

.lib "RapidBson.dll"

JsonSuppressWarnings(flags%):"_JsonSuppressWarnings@4"
JsonParseFromString%(json$):"_JsonParseFromString@4"
JsonParseFromFile%(path$):"_JsonParseFromFile@4"
JsonHasParseError%(document%):"_JsonHasParseError@4"
JsonGetParseErrorCode%(document%):"_JsonGetParseErrorCode@4"
JsonHasMember%(object%, name$):"_JsonHasMember@8"
JsonGetValue%(object%, name$):"_JsonGetValue@8"
JsonIsString%(value%):"_JsonIsString@4"
JsonIsInt%(value%):"_JsonIsInt@4"
JsonIsFloat%(value%):"_JsonIsFloat@4"
JsonIsBool%(value%):"_JsonIsBool@4"
JsonIsArray%(object%):"_JsonIsArray@4"
JsonIsObject%(object%):"_JsonIsObject@4"
JsonIsNull%(object%):"_JsonIsNull@4"
JsonGetString$(value%):"_JsonGetString@4"
JsonGetInt%(value%):"_JsonGetInt@4"
JsonGetFloat#(value%):"_JsonGetFloat@4"
JsonGetBool%(value%):"_JsonGetBool@4"
JsonGetArray%(object%):"_JsonGetArray@4"
JsonGetArraySize%(array%):"_JsonGetArraySize@4"
JsonGetArrayValue%(array%, index%):"_JsonGetArrayValue@8"
JsonGetArrayCapacity%(array%):"_JsonGetArrayCapacity@4"
JsonFreeDocument(document%):"_JsonFreeDocument@4"