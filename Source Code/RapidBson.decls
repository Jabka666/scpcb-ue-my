; RapidBson - A part of BlitzToolbox
; A fast JSON parser/generator for Blitz3D with both SAX/DOM style API.
; v1.0 2023.9.12
; https://github.com/ZiYueCommentary/BlitzToolbox

.lib "RapidBson.dll"

JsonParseFromString%(json$):"_JsonParseFromString@4"
JsonParseFromFile%(path$):"_JsonParseFromFile@4"
JsonGetValue%(value%, name$):"_JsonGetValue@8"
JsonIsString%(value%):"_JsonIsString@4"
JsonIsInt%(value%):"_JsonIsInt@4"
JsonIsFloat%(value%):"_JsonIsFloat@4"
JsonIsBool%(value%):"_JsonIsBool@4"
JsonIsArray%(value%):"_JsonIsArray@4"
JsonIsObject%(value%):"_JsonIsObject@4"
JsonIsNull%(value%):"_JsonIsNull@4"
JsonGetNull%():"_JsonGetNull@0"
JsonGetString$(value%):"_JsonGetString@4"
JsonGetInt%(value%):"_JsonGetInt@4"
JsonGetFloat#(value%):"_JsonGetFloat@4"
JsonGetBool%(value%):"_JsonGetBool@4"
JsonGetArray%(value%):"_JsonGetArray@4"
JsonGetArraySize%(array%):"_JsonGetArraySize@4"
JsonGetArrayValue%(array%, index%):"_JsonGetArrayValue@8"
JsonGetArrayCapacity%(array%):"_JsonGetArrayCapacity@4"