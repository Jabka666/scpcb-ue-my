.lib "bass.dll"

; Bass
BASS_Init%(device%, freq%, flags%, win%, clsid%):"BASS_Init"
BASS_Stop%():"BASS_Stop"
BASS_Free%():"BASS_Free"
BASS_GetVersion%() : "BASS_GetVersion"
BASS_GetInfo%(info*) : "BASS_GetInfo"
BASS_ErrorGetCode%() : "BASS_ErrorGetCode"
BASS_PluginLoad%(file$,flags%) : "BASS_PluginLoad" 
BASS_Apply3D() : "BASS_Apply3D" 
BASS_SetVolume%(volume#) : "BASS_SetVolume"
BASS_Set3DPositionPos_%(pos*,vel%,front*,top*) : "BASS_Set3DPosition"
BASS_SetConfig(config%, value%) : "BASS_SetConfig"
; Stream
BASS_StreamCreate%(freq%,chans%,flags%,proc%,user%) : "BASS_StreamCreate"
BASS_StreamCreateFile%(mem%,file$,offsetlow%,offsethigh%,lengthlow%,lengthhigh%,flags%) : "BASS_StreamCreateFile"
BASS_StreamFree%(Handle%) : "BASS_StreamFree"    
BASS_StreamPutData%(Handle%, buffer*, length%) : "BASS_StreamPutData"
BASS_StreamCreateURL%(url$,offset%,flags%,callback%,user%) : "BASS_StreamCreateURL"

; Sample
BASS_SampleLoad%(mem%,file$,offsetlow%,offsethigh%,length%,max%,flags%) : "BASS_SampleLoad"
BASS_SampleSetInfo%(Handle%, info*) : "BASS_SampleSetInfo"
BASS_SampleGetInfo%(Handle%, info*) : "BASS_SampleGetInfo"
BASS_SampleGetChannel%(Handle%, flags%) : "BASS_SampleGetChannel"
BASS_SampleFree%(Handle%) : "BASS_SampleFree"    

; Channel
BASS_ChannelPlay%(Handle%,restart%) : "BASS_ChannelPlay"
BASS_ChannelStart%(Handle%) : "BASS_ChannelStart"
BASS_ChannelPause%(Handle%) : "BASS_ChannelPause"
BASS_ChannelStop%(Handle%) : "BASS_ChannelStop"
BASS_ChannelFlags%(Handle%,flags%,mask%) : "BASS_ChannelFlags"
BASS_ChannelGetData%(Handle%,buffer*,length%) : "BASS_ChannelGetData"
BASS_ChannelGetPosition%(Handle%, Mode%) : "BASS_ChannelGetPosition"
BASS_ChannelGetLevel%(Handle%) : "BASS_ChannelGetLevel"
BASS_ChannelGetLevelEx(Handle%, levels*, length#, flags%) : "BASS_ChannelGetLevelEx"
BASS_ChannelIsActive%(Handle%) : "BASS_ChannelIsActive"
BASS_ChannelLock%(Handle%, lock%)
BASS_ChannelSetPosition%(Handle%, poslow%, poshigh%, Mode%) : "BASS_ChannelSetPosition"
BASS_ChannelSetAttribute%(Handle%, attrib%, value#) : "BASS_ChannelSetAttribute"
BASS_ChannelSetFX%(Handle%,type%,priority%) : "BASS_ChannelSetFX"
BASS_ChannelRemoveFX%(Handle%,fx%) : "BASS_ChannelRemoveFX"    
BASS_ChannelSet3DAttributes%(Handle%,mode%,min#,max#,iangle%,oangle%,outvol#) : "BASS_ChannelSet3DAttributes"
BASS_ChannelSet3DPosition_%(Handle,pos*,orient%,vel%) : "BASS_ChannelSet3DPosition"
BASS_ChannelGet3DPosition_%(Handle,pos*,orient%,vel%) : "BASS_ChannelGet3DPosition"

; Record
BASS_RecordFree%() : "BASS_RecordFree"
BASS_RecordGetDevice%() : "BASS_RecordGetDevice"
BASS_RecordGetInput%(in_put%, volumeptr%) : "BASS_RecordGetInput"
BASS_RecordGetDeviceInfo%(device%, info*) : "BASS_RecordGetDeviceInfo"
BASS_RecordGetInfo%(info*) : "BASS_RecordGetInfo"
BASS_RecordGetInputName%(in_put%) : "BASS_RecordGetInputName"
BASS_RecordInit%(device%) : "BASS_RecordInit"
BASS_RecordSetDevice%(device%) : "BASS_RecordSetDevice"
BASS_RecordSetInput%(in_put%,setting%,volume#) : "BASS_RecordSetInput"
BASS_RecordStart%(freq%,chans%,flags%,proc%,user%) : "BASS_RecordStart"

; FX
BASS_FXSetParameters%(Handle,par*) : "BASS_FXSetParameters"
BASS_FXGetParameters%(Handle,par*) : "BASS_FXGetParameters"   

; Opus
.lib "bassopus.dll"

BASS_OPUS_StreamCreate_%(header*,flags%,proc%,user%) : "BASS_OPUS_StreamCreate"
BASS_OPUS_StreamPutData%(handle%, buffer*, length%) : "BASS_OPUS_StreamPutData"

; Mixer
.lib "bassmix.dll"

BASS_Mixer_StreamCreate%(freq%,chans%,flags%) : "BASS_Mixer_StreamCreate"
BASS_Mixer_StreamAddChannel%(handle%, channel%, flags%) : "BASS_Mixer_StreamAddChannel"
BASS_Mixer_ChannelRemove%(channel%) : "BASS_Mixer_ChannelRemove"