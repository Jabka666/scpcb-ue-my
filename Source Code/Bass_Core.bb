; ~ BASS 2.4 For Blitz3D by "Ne4to"
; ~ BASS Constants
;[Block]
Const BASSVERSION% = $204
Const BASSVERSIONTEXT% = 2
Const BASS_OK% = 0
Const BASS_ERROR_MEM% = 1
Const BASS_ERROR_FILEOPEN% = 2
Const BASS_ERROR_DRIVER% = 3
Const BASS_ERROR_BUFLOST% = 4
Const BASS_ERROR_HANDLE% = 5
Const BASS_ERROR_FORMAT% = 6
Const BASS_ERROR_POSITION% = 7
Const BASS_ERROR_INIT% = 8
Const BASS_ERROR_START% = 9
Const BASS_ERROR_SSL% = 10
Const BASS_ERROR_REINIT% = 11
Const BASS_ERROR_ALREADY% = 14
Const BASS_ERROR_NOTAUDIO% = 17
Const BASS_ERROR_NOCHAN% = 18
Const BASS_ERROR_ILLTYPE% = 19
Const BASS_ERROR_ILLPARAM% = 20
Const BASS_ERROR_NO3D% = 21
Const BASS_ERROR_NOEAX% = 22
Const BASS_ERROR_DEVICE% = 23
Const BASS_ERROR_NOPLAY% = 24
Const BASS_ERROR_FREQ% = 25
Const BASS_ERROR_NOTFILE% = 27
Const BASS_ERROR_NOHW% = 29
Const BASS_ERROR_EMPTY% = 31
Const BASS_ERROR_NONET% = 32
Const BASS_ERROR_CREATE% = 33
Const BASS_ERROR_NOFX% = 34
Const BASS_ERROR_NOTAVAIL% = 37
Const BASS_ERROR_DECODE% = 38
Const BASS_ERROR_DX% = 39
Const BASS_ERROR_TIMEOUT% = 40
Const BASS_ERROR_FILEFORM% = 41
Const BASS_ERROR_SPEAKER% = 42
Const BASS_ERROR_VERSION% = 43
Const BASS_ERROR_CODEC% = 44
Const BASS_ERROR_ENDED% = 45
Const BASS_ERROR_BUSY% = 46
Const BASS_ERROR_UNSTREAMABLE% = 47
Const BASS_ERROR_PROTOCOL% = 48
Const BASS_ERROR_DENIED% = 49
Const BASS_ERROR_UNKNOWN% = 1
Const BASS_CONFIG_BUFFER% = 0
Const BASS_CONFIG_UPDATEPERIOD% = 1
Const BASS_CONFIG_GVOL_SAMPLE% = 4
Const BASS_CONFIG_GVOL_STREAM% = 5
Const BASS_CONFIG_GVOL_MUSIC% = 6
Const BASS_CONFIG_CURVE_VOL% = 7
Const BASS_CONFIG_CURVE_PAN% = 8
Const BASS_CONFIG_FLOATDSP% = 9
Const BASS_CONFIG_3DALGORITHM% = 10
Const BASS_CONFIG_NET_TIMEOUT% = 11
Const BASS_CONFIG_NET_BUFFER% = 12
Const BASS_CONFIG_PAUSE_NOPLAY% = 13
Const BASS_CONFIG_NET_PREBUF% = 15
Const BASS_CONFIG_NET_PASSIVE% = 18
Const BASS_CONFIG_REC_BUFFER% = 19
Const BASS_CONFIG_NET_PLAYLIST% = 21
Const BASS_CONFIG_MUSIC_VIRTUAL% = 22
Const BASS_CONFIG_VERIFY% = 23
Const BASS_CONFIG_UPDATETHREADS% = 24
Const BASS_CONFIG_DEV_BUFFER% = 27
Const BASS_CONFIG_REC_LOOPBACK% = 28
Const BASS_CONFIG_VISTA_TRUEPOS% = 30
Const BASS_CONFIG_IOS_SESSION% = 34
Const BASS_CONFIG_IOS_MIXAUDIO% = 34
Const BASS_CONFIG_DEV_DEFAULT% = 36
Const BASS_CONFIG_NET_READTIMEOUT% = 37
Const BASS_CONFIG_VISTA_SPEAKERS% = 38
Const BASS_CONFIG_IOS_SPEAKER% = 39
Const BASS_CONFIG_MF_DISABLE% = 40
Const BASS_CONFIG_HANDLES% = 41
Const BASS_CONFIG_UNICODE% = 42
Const BASS_CONFIG_SRC% = 43
Const BASS_CONFIG_SRC_SAMPLE% = 44
Const BASS_CONFIG_ASYNCFILE_BUFFER% = 45
Const BASS_CONFIG_OGG_PRESCAN% = 47
Const BASS_CONFIG_MF_VIDEO% = 48
Const BASS_CONFIG_AIRPLAY% = 49
Const BASS_CONFIG_DEV_NONSTOP% = 50
Const BASS_CONFIG_IOS_NOCATEGORY% = 51
Const BASS_CONFIG_VERIFY_NET% = 52
Const BASS_CONFIG_DEV_PERIOD% = 53
Const BASS_CONFIG_FLOAT% = 54
Const BASS_CONFIG_NET_SEEK% = 56
Const BASS_CONFIG_AM_DISABLE% = 58
Const BASS_CONFIG_NET_PLAYLIST_DEPTH% = 59
Const BASS_CONFIG_NET_PREBUF_WAIT% = 60
Const BASS_CONFIG_ANDROID_SESSIONID% = 62
Const BASS_CONFIG_WASAPI_PERSIST% = 65
Const BASS_CONFIG_REC_WASAPI% = 66
Const BASS_CONFIG_ANDROID_AAUDIO% = 67
Const BASS_CONFIG_SAMPLE_ONEHANDLE% = 69
Const BASS_CONFIG_NET_META% = 71
Const BASS_CONFIG_NET_RESTRATE% = 72
Const BASS_CONFIG_REC_DEFAULT% = 73
Const BASS_CONFIG_NORAMP% = 74
Const BASS_CONFIG_NET_AGENT% = 16
Const BASS_CONFIG_NET_PROXY% = 17
Const BASS_CONFIG_IOS_NOTIFY% = 46
Const BASS_CONFIG_ANDROID_JAVAVM% = 63
Const BASS_CONFIG_LIBSSL% = 64
Const BASS_CONFIG_FILENAME% = 75
Const BASS_CONFIG_THREAD% = $40000000
Const BASS_IOS_SESSION_MIX% = 1
Const BASS_IOS_SESSION_DUCK% = 2
Const BASS_IOS_SESSION_AMBIENT% = 4
Const BASS_IOS_SESSION_SPEAKER% = 8
Const BASS_IOS_SESSION_DISABLE% = 16
Const BASS_IOS_SESSION_DEACTIVATE% = 32
Const BASS_IOS_SESSION_AIRPLAY% = 64
Const BASS_IOS_SESSION_BTHFP% = 128
Const BASS_IOS_SESSION_BTA2DP% = $100
Const BASS_DEVICE_8BITS% = 1
Const BASS_DEVICE_MONO% = 2
Const BASS_DEVICE_3D% = 4
Const BASS_DEVICE_16BITS% = 8
Const BASS_DEVICE_REINIT% = 128
Const BASS_DEVICE_LATENCY% = $100
Const BASS_DEVICE_CPSPEAKERS% = $400
Const BASS_DEVICE_SPEAKERS% = $800
Const BASS_DEVICE_NOSPEAKER% = $1000
Const BASS_DEVICE_DMIX% = $2000
Const BASS_DEVICE_FREQ% = $4000
Const BASS_DEVICE_STEREO% = $8000
Const BASS_DEVICE_HOG% = $10000
Const BASS_DEVICE_AUDIOTRACK% = $20000
Const BASS_DEVICE_DSOUND% = $40000
Const BASS_DEVICE_SOFTWARE% = $80000
Const BASS_OBJECT_DS% = 1
Const BASS_OBJECT_DS3DL% = 2
Const BASS_DEVICE_ENABLED% = 1
Const BASS_DEVICE_DEFAULT% = 2
Const BASS_DEVICE_INIT% = 4
Const BASS_DEVICE_LOOPBACK% = 8
Const BASS_DEVICE_DEFAULTCOM% = 128
Const BASS_DEVICE_TYPE_MASK% = $ff000000
Const BASS_DEVICE_TYPE_NETWORK% = $01000000
Const BASS_DEVICE_TYPE_SPEAKERS% = $02000000
Const BASS_DEVICE_TYPE_LINE% = $03000000
Const BASS_DEVICE_TYPE_HEADPHONES% = $04000000
Const BASS_DEVICE_TYPE_MICROPHONE% = $05000000
Const BASS_DEVICE_TYPE_HEADSET% = $06000000
Const BASS_DEVICE_TYPE_HANDSET% = $07000000
Const BASS_DEVICE_TYPE_DIGITAL% = $08000000
Const BASS_DEVICE_TYPE_SPDIF% = $09000000
Const BASS_DEVICE_TYPE_HDMI% = $0a000000
Const BASS_DEVICE_TYPE_DISPLAYPORT% = $40000000
Const BASS_DEVICES_AIRPLAY% = $1000000
Const DSCAPS_EMULDRIVER% = $00000020
Const DSCAPS_CERTIFIED% = $00000040
Const DSCAPS_HARDWARE% = $80000000
Const DSCCAPS_EMULDRIVER% = DSCAPS_EMULDRIVER
Const DSCCAPS_CERTIFIED% = DSCAPS_CERTIFIED
Const WAVE_FORMAT_1M08% = $00000001
Const WAVE_FORMAT_1S08% = $00000002
Const WAVE_FORMAT_1M16% = $00000004
Const WAVE_FORMAT_1S16% = $00000008
Const WAVE_FORMAT_2M08% = $00000010
Const WAVE_FORMAT_2S08% = $00000020
Const WAVE_FORMAT_2M16% = $00000040
Const WAVE_FORMAT_2S16% = $00000080
Const WAVE_FORMAT_4M08% = $00000100
Const WAVE_FORMAT_4S08% = $00000200
Const WAVE_FORMAT_4M16% = $00000400
Const WAVE_FORMAT_4S16% = $00000800
Const BASS_SAMPLE_8BITS% = 1
Const BASS_SAMPLE_FLOAT% = 256
Const BASS_SAMPLE_MONO% = 2
Const BASS_SAMPLE_LOOP% = 4
Const BASS_SAMPLE_3D% = 8
Const BASS_SAMPLE_SOFTWARE% = 16
Const BASS_SAMPLE_MUTEMAX% = 32
Const BASS_SAMPLE_VAM% = 64
Const BASS_SAMPLE_FX% = 128
Const BASS_SAMPLE_OVER_VOL% = $10000
Const BASS_SAMPLE_OVER_POS% = $20000
Const BASS_SAMPLE_OVER_DIST% = $30000
Const BASS_STREAM_PRESCAN% = $20000
Const BASS_STREAM_AUTOFREE% = $40000
Const BASS_STREAM_RESTRATE% = $80000
Const BASS_STREAM_BLOCK% = $100000
Const BASS_STREAM_DECODE% = $200000
Const BASS_STREAM_STATUS% = $800000
Const BASS_MP3_IGNOREDELAY% = $200
Const BASS_MP3_SETPOS% = BASS_STREAM_PRESCAN
Const BASS_MUSIC_FLOAT% = BASS_SAMPLE_FLOAT
Const BASS_MUSIC_MONO% = BASS_SAMPLE_MONO
Const BASS_MUSIC_LOOP% = BASS_SAMPLE_LOOP
Const BASS_MUSIC_3D% = BASS_SAMPLE_3D
Const BASS_MUSIC_FX% = BASS_SAMPLE_FX
Const BASS_MUSIC_AUTOFREE% = BASS_STREAM_AUTOFREE
Const BASS_MUSIC_DECODE% = BASS_STREAM_DECODE
Const BASS_MUSIC_PRESCAN% = BASS_STREAM_PRESCAN
Const BASS_MUSIC_CALCLEN% = BASS_MUSIC_PRESCAN
Const BASS_MUSIC_RAMP% = $200
Const BASS_MUSIC_RAMPS% = $400
Const BASS_MUSIC_SURROUND% = $800
Const BASS_MUSIC_SURROUND2% = $1000
Const BASS_MUSIC_FT2PAN% = $2000
Const BASS_MUSIC_FT2MOD% = $2000
Const BASS_MUSIC_PT1MOD% = $4000
Const BASS_MUSIC_NONINTER% = $10000
Const BASS_MUSIC_SINCINTER% = $800000
Const BASS_MUSIC_POSRESET% = $8000
Const BASS_MUSIC_POSRESETEX% = $400000
Const BASS_MUSIC_STOPBACK% = $80000
Const BASS_MUSIC_NOSAMPLE% = $100000
Const BASS_SPEAKER_FRONT% = $1000000
Const BASS_SPEAKER_REAR% = $2000000
Const BASS_SPEAKER_CENLFE% = $3000000
Const BASS_SPEAKER_SIDE% = $4000000
Const BASS_SPEAKER_LEFT% = $10000000
Const BASS_SPEAKER_RIGHT% = $20000000
Const BASS_SPEAKER_FRONTLEFT% = BASS_SPEAKER_FRONT Or BASS_SPEAKER_LEFT
Const BASS_SPEAKER_FRONTRIGHT% = BASS_SPEAKER_FRONT Or BASS_SPEAKER_RIGHT
Const BASS_SPEAKER_REARLEFT% = BASS_SPEAKER_REAR Or BASS_SPEAKER_LEFT
Const BASS_SPEAKER_REARRIGHT% = BASS_SPEAKER_REAR Or BASS_SPEAKER_RIGHT
Const BASS_SPEAKER_CENTER% = BASS_SPEAKER_CENLFE Or BASS_SPEAKER_LEFT
Const BASS_SPEAKER_LFE% = BASS_SPEAKER_CENLFE Or BASS_SPEAKER_RIGHT
Const BASS_SPEAKER_SIDELEFT% = BASS_SPEAKER_SIDE Or BASS_SPEAKER_LEFT
Const BASS_SPEAKER_SIDERIGHT% = BASS_SPEAKER_SIDE Or BASS_SPEAKER_RIGHT
Const BASS_SPEAKER_REAR2% = BASS_SPEAKER_SIDE
Const BASS_SPEAKER_REAR2LEFT% = BASS_SPEAKER_SIDELEFT
Const BASS_SPEAKER_REAR2RIGHT% = BASS_SPEAKER_SIDERIGHT
Const BASS_ASYNCFILE% = $40000000
Const BASS_UNICODE% = $80000000
Const BASS_RECORD_ECHOCANCEL% = $2000
Const BASS_RECORD_AGC% = $4000
Const BASS_RECORD_PAUSE% = $8000
Const BASS_VAM_HARDWARE% = 1
Const BASS_VAM_SOFTWARE% = 2
Const BASS_VAM_TERM_TIME% = 4
Const BASS_VAM_TERM_DIST% = 8
Const BASS_VAM_TERM_PRIO% = 16
Const BASS_ORIGRES_FLOAT% = $10000
Const BASS_CTYPE_SAMPLE% = 1
Const BASS_CTYPE_RECORD% = 2
Const BASS_CTYPE_STREAM% = $10000
Const BASS_CTYPE_STREAM_VORBIS% = $10002
Const BASS_CTYPE_STREAM_OGG% = $10002
Const BASS_CTYPE_STREAM_MP1% = $10003
Const BASS_CTYPE_STREAM_MP2% = $10004
Const BASS_CTYPE_STREAM_MP3% = $10005
Const BASS_CTYPE_STREAM_AIFF% = $10006
Const BASS_CTYPE_STREAM_CA% = $10007
Const BASS_CTYPE_STREAM_MF% = $10008
Const BASS_CTYPE_STREAM_AM% = $10009
Const BASS_CTYPE_STREAM_SAMPLE% = $1000a
Const BASS_CTYPE_STREAM_DUMMY% = $18000
Const BASS_CTYPE_STREAM_DEVICE% = $18001
Const BASS_CTYPE_STREAM_WAV% = $40000
Const BASS_CTYPE_STREAM_WAV_PCM% = $50001
Const BASS_CTYPE_STREAM_WAV_FLOAT% = $50003
Const BASS_CTYPE_MUSIC_MOD% = $20000
Const BASS_CTYPE_MUSIC_MTM% = $20001
Const BASS_CTYPE_MUSIC_S3M% = $20002
Const BASS_CTYPE_MUSIC_XM% = $20003
Const BASS_CTYPE_MUSIC_IT% = $20004
Const BASS_CTYPE_MUSIC_MO3% = $00100
Const BASS_PLUGIN_PROC% = 1
Const BASS_3DMODE_NORMAL% = 0
Const BASS_3DMODE_RELATIVE% = 1
Const BASS_3DMODE_OFF% = 2
Const BASS_3DALG_DEFAULT% = 0
Const BASS_3DALG_OFF% = 1
Const BASS_3DALG_FULL% = 2
Const BASS_3DALG_LIGHT% = 3
Const BASS_SAMCHAN_NEW% = 1
Const BASS_SAMCHAN_STREAM% = 2
Const BASS_STREAMPROC_END% = $80000000
Const STREAMFILE_NOBUFFER% = 0
Const STREAMFILE_BUFFER% = 1
Const STREAMFILE_BUFFERPUSH% = 2
Const BASS_FILEDATA_END% = 0
Const BASS_FILEPOS_CURRENT% = 0
Const BASS_FILEPOS_DECODE% = BASS_FILEPOS_CURRENT
Const BASS_FILEPOS_DOWNLOAD% = 1
Const BASS_FILEPOS_END% = 2
Const BASS_FILEPOS_START% = 3
Const BASS_FILEPOS_CONNECTED% = 4
Const BASS_FILEPOS_BUFFER% = 5
Const BASS_FILEPOS_SOCKET% = 6
Const BASS_FILEPOS_ASYNCBUF% = 7
Const BASS_FILEPOS_SIZE% = 8
Const BASS_FILEPOS_BUFFERING% = 9
Const BASS_FILEPOS_AVAILABLE% = 10
Const BASS_SYNC_POS% = 0
Const BASS_SYNC_END% = 2
Const BASS_SYNC_META% = 4
Const BASS_SYNC_SLIDE% = 5
Const BASS_SYNC_STALL% = 6
Const BASS_SYNC_DOWNLOAD% = 7
Const BASS_SYNC_FREE% = 8
Const BASS_SYNC_SETPOS% = 11
Const BASS_SYNC_MUSICPOS% = 10
Const BASS_SYNC_MUSICINST% = 1
Const BASS_SYNC_MUSICFX% = 3
Const BASS_SYNC_OGG_CHANGE% = 12
Const BASS_SYNC_DEV_FAIL% = 14
Const BASS_SYNC_DEV_FORMAT% = 15
Const BASS_SYNC_THREAD% = $20000000
Const BASS_SYNC_MIXTIME% = $40000000
Const BASS_SYNC_ONETIME% = $80000000
Const BASS_ACTIVE_STOPPED% = 0
Const BASS_ACTIVE_PLAYING% = 1
Const BASS_ACTIVE_STALLED% = 2
Const BASS_ACTIVE_PAUSED% = 3
Const BASS_ACTIVE_PAUSED_DEVICE% = 4
Const BASS_ATTRIB_FREQ% = 1
Const BASS_ATTRIB_VOL% = 2
Const BASS_ATTRIB_PAN% = 3
Const BASS_ATTRIB_EAXMIX% = 4
Const BASS_ATTRIB_NOBUFFER% = 5
Const BASS_ATTRIB_VBR% = 6
Const BASS_ATTRIB_CPU% = 7
Const BASS_ATTRIB_SRC% = 8
Const BASS_ATTRIB_NET_RESUME% = 9
Const BASS_ATTRIB_SCANINFO% = 10
Const BASS_ATTRIB_NORAMP% = 11
Const BASS_ATTRIB_BITRATE% = 12
Const BASS_ATTRIB_BUFFER% = 13
Const BASS_ATTRIB_GRANULE% = 14
Const BASS_ATTRIB_USER% = 15
Const BASS_ATTRIB_TAIL% = 16
Const BASS_ATTRIB_PUSH_LIMIT% = 17
Const BASS_ATTRIB_DOWNLOADPROC% = 18
Const BASS_ATTRIB_VOLDSP% = 19
Const BASS_ATTRIB_VOLDSP_PRIORITY% = 20
Const BASS_ATTRIB_MUSIC_AMPLIFY% = $100
Const BASS_ATTRIB_MUSIC_PANSEP% = $101
Const BASS_ATTRIB_MUSIC_PSCALER% = $102
Const BASS_ATTRIB_MUSIC_BPM% = $103
Const BASS_ATTRIB_MUSIC_SPEED% = $104
Const BASS_ATTRIB_MUSIC_VOL_GLOBAL% = $105
Const BASS_ATTRIB_MUSIC_ACTIVE% = $106
Const BASS_ATTRIB_MUSIC_VOL_CHAN% = $200
Const BASS_ATTRIB_MUSIC_VOL_INST% = $300
Const BASS_SLIDE_LOG% = $1000000
Const BASS_DATA_AVAILABLE% = 0
Const BASS_DATA_NOREMOVE% = $10000000
Const BASS_DATA_FIXED% = $20000000
Const BASS_DATA_FLOAT% = $40000000
Const BASS_DATA_FFT256% = $80000000
Const BASS_DATA_FFT512% = $80000001
Const BASS_DATA_FFT1024% = $80000002
Const BASS_DATA_FFT2048% = $80000003
Const BASS_DATA_FFT4096% = $80000004
Const BASS_DATA_FFT8192% = $80000005
Const BASS_DATA_FFT16384% = $80000006
Const BASS_DATA_FFT32768% = $80000007
Const BASS_DATA_FFT_INDIVIDUAL% = $10
Const BASS_DATA_FFT_NOWINDOW% = $20
Const BASS_DATA_FFT_REMOVEDC% = $40
Const BASS_DATA_FFT_COMPLEX% = $80
Const BASS_DATA_FFT_NYQUIST% = $100
Const BASS_LEVEL_MONO% = 1
Const BASS_LEVEL_STEREO% = 2
Const BASS_LEVEL_RMS% = 4
Const BASS_LEVEL_VOLPAN% = 8
Const BASS_LEVEL_NOREMOVE% = 16
Const BASS_TAG_ID3% = 0
Const BASS_TAG_ID3V2% = 1
Const BASS_TAG_OGG% = 2
Const BASS_TAG_HTTP% = 3
Const BASS_TAG_ICY% = 4
Const BASS_TAG_META% = 5
Const BASS_TAG_APE% = 6
Const BASS_TAG_MP4% = 7
Const BASS_TAG_WMA% = 8
Const BASS_TAG_VENDOR% = 9
Const BASS_TAG_LYRICS3% = 10
Const BASS_TAG_CA_CODEC% = 11
Const BASS_TAG_MF% = 13
Const BASS_TAG_WAVEFORMAT% = 14
Const BASS_TAG_AM_NAME% = 16
Const BASS_TAG_ID3V2_2% = 17
Const BASS_TAG_AM_MIME% = 18
Const BASS_TAG_LOCATION% = 19
Const BASS_TAG_RIFF_INFO% = $100
Const BASS_TAG_RIFF_BEXT% = $101
Const BASS_TAG_RIFF_CART% = $102
Const BASS_TAG_RIFF_DISP% = $103
Const BASS_TAG_RIFF_CUE% = $104
Const BASS_TAG_RIFF_SMPL% = $105
Const BASS_TAG_APE_BINARY% = $1000
Const BASS_TAG_MUSIC_NAME% = $10000
Const BASS_TAG_MUSIC_MESSAGE% = $10001
Const BASS_TAG_MUSIC_ORDERS% = $10002
Const BASS_TAG_MUSIC_AUTH% = $10003
Const BASS_TAG_MUSIC_INST% = $10100
Const BASS_TAG_MUSIC_CHAN% = $10200
Const BASS_TAG_MUSIC_SAMPLE% = $10300
Const BASS_POS_BYTE% = 0
Const BASS_POS_MUSIC_ORDER% = 1
Const BASS_POS_OGG% = 3
Const BASS_POS_END% = $10
Const BASS_POS_LOOP% = $11
Const BASS_POS_FLUSH% = $1000000
Const BASS_POS_RESET% = $2000000
Const BASS_POS_RELATIVE% = $4000000
Const BASS_POS_INEXACT% = $8000000
Const BASS_POS_DECODE% = $10000000
Const BASS_POS_DECODETO% = $20000000
Const BASS_POS_SCAN% = $40000000
Const BASS_NODEVICE% = $20000
Const BASS_INPUT_OFF% = $10000
Const BASS_INPUT_ON% = $20000
Const BASS_INPUT_TYPE_MASK% = $ff000000
Const BASS_INPUT_TYPE_UNDEF% = $00000000
Const BASS_INPUT_TYPE_DIGITAL% = $01000000
Const BASS_INPUT_TYPE_LINE% = $02000000
Const BASS_INPUT_TYPE_MIC% = $03000000
Const BASS_INPUT_TYPE_SYNTH% = $04000000
Const BASS_INPUT_TYPE_CD% = $05000000
Const BASS_INPUT_TYPE_PHONE% = $06000000
Const BASS_INPUT_TYPE_SPEAKER% = $07000000
Const BASS_INPUT_TYPE_WAVE% = $08000000
Const BASS_INPUT_TYPE_AUX% = $09000000
Const BASS_INPUT_TYPE_ANALOG% = $0a000000
Const BASS_FX_DX8_CHORUS% = 0
Const BASS_FX_DX8_COMPRESSOR% = 1
Const BASS_FX_DX8_DISTORTION% = 2
Const BASS_FX_DX8_ECHO% = 3
Const BASS_FX_DX8_FLANGER% = 4
Const BASS_FX_DX8_GARGLE% = 5
Const BASS_FX_DX8_I3DL2REVERB% = 6
Const BASS_FX_DX8_PARAMEQ% = 7
Const BASS_FX_DX8_REVERB% = 8
Const BASS_FX_VOLUME% = 9
Const BASS_DX8_PHASE_NEG_180% = 0
Const BASS_DX8_PHASE_NEG_90% = 1
Const BASS_DX8_PHASE_ZERO% = 2
Const BASS_DX8_PHASE_90% = 3
Const BASS_DX8_PHASE_180% = 4
Const BASS_IOSNOTIFY_INTERRUPT% = 1
Const BASS_IOSNOTIFY_INTERRUPT_END% = 2
;[End Block]

Function BASS_CreateOpusHeader%(Samplerate%, Channels%)
	Local Header% = CreateBank(13 + 256)
	
	PokeByte(Header, 0, 1)
	PokeByte(Header, 1, Channels)
	PokeShort(Header, 2, 1)
	PokeInt(Header, 4, Samplerate)
	PokeShort(Header, 8, 0)
	Return(Header)
End Function

Function BASS_ChannelGet3DPosition%(Channel%, posPtrx%, posPtry%, posPtrz%)
	Local Bank% = CreateBank(12)
	BASS_ChannelGet3DPosition_(Channel, Bank, 0, 0)
	
	Memory_PokeFloat(posPtrx, PeekFloat(Bank, 0))
	Memory_PokeFloat(posPtry, PeekFloat(Bank, 4))
	Memory_PokeFloat(posPtrz, PeekFloat(Bank, 8))
	FreeBank Bank
End Function

Function BASS_ChannelSet3DPosition%(Channel%, x#, y#, z#)
	Local Bank% = CreateBank(12)
	
	PokeFloat(Bank, 0, x)
	PokeFloat(Bank, 4, y)
	PokeFloat(Bank, 8, z)
	
	Local Temp% = BASS_ChannelSet3DPosition_(Channel, Bank, 0, 0)
	
	FreeBank(Bank) : Bank = 0
	Return(Temp)
End Function

Function BASS_Update3DListener%(Entity%) 
	Local Bank1% = CreateBank(3 * 4)
	
	PokeFloat(Bank1, 0, EntityX(Entity, True))
	PokeFloat(Bank1, 4, EntityY(Entity, True))
	PokeFloat(Bank1, 8, EntityZ(Entity, True))
	
    ; ~ LookAt Vector (Front)
	TFormVector(0.0, 0.0, 1.0, Entity, 0)
	
	Local Bank2% = CreateBank(3 * 4)
	
	PokeFloat(Bank2, 0, TFormedX())
	PokeFloat(Bank2, 4, TFormedY())
	PokeFloat(Bank2, 8, TFormedZ())
	
	; ~ UpVector (Top)
	TFormVector(0.0, 1.0, 0.0, Entity, 0)
	
	Local Bank3% = CreateBank(3 * 4)
	
	PokeFloat(Bank3, 0, TFormedX())
	PokeFloat(Bank3, 4, TFormedY())
	PokeFloat(Bank3, 8, TFormedZ())
	
	Local Temp% = BASS_Set3DPositionPos_(Bank1, 0, Bank2, Bank3)
	
	FreeBank(Bank1) : Bank1 = 0
	FreeBank(Bank2) : Bank2 = 0
	FreeBank(Bank3) : Bank3 = 0
	
	Return(Temp)
End Function

Function BASS_FXSetEcho%(Hnd%, fWetDryMix#, fFeedback#, fLeftDelay#, fRightDelay#, lPanDelay%)
	Local Bank% = CreateBank(20)
	
	PokeFloat(Bank, 0, fWetDryMix)
	PokeFloat(Bank,	4, fFeedback)
	PokeFloat(Bank, 8, fLeftDelay)
	PokeFloat(Bank, 12, fRightDelay)
	PokeInt(Bank, 16, lPanDelay)
	
	Local Temp% = BASS_FXSetParameters(Hnd, Bank)
	
	FreeBank(Bank) : Bank = 0
	Return(Temp)
End Function

Function BASS_FXSetReverb%(Hnd%, fInGain#, fReverbMix#, fReverbTime#, fHighFreqRTRatio#)
	Local Bank% = CreateBank(16)
	
	PokeFloat(Bank, 0, fInGain)
	PokeFloat(Bank, 4, fReverbMix)
	PokeFloat(Bank, 8, fReverbTime)
	PokeFloat(Bank, 12, fHighFreqRTRatio)
	
	Local Temp% = BASS_FXSetParameters(Hnd, Bank)
	
	FreeBank(Bank) : Bank = 0
	Return(Temp)
End Function

Function BASS_FXSetDistortion%(Hnd%, fGain#, fEdge#, fPostEQCenterFrequency#, fPostEQBandwidth#, fPreLowpassCutoff#)
	Local Bank% = CreateBank(20)
	
	PokeFloat(Bank, 0, fGain)
	PokeFloat(Bank, 4, fEdge)
	PokeFloat(Bank, 8, fPostEQCenterFrequency)
	PokeFloat(Bank, 12, fPostEQBandwidth)
	PokeFloat(Bank, 16, fPreLowpassCutoff)
	
	Local Temp% = BASS_FXSetParameters(Hnd, Bank)
	
	FreeBank(Bank) : Bank = 0
	Return(Temp)
End Function

Function BASS_FXSetParamEQ%(Hnd%, fCenter#,fBandwidth#,fGain#)
	Local Bank = CreateBank(12)
	
	PokeFloat Bank, 0, fCenter#
	PokeFloat Bank, 4, fBandwidth#
	PokeFloat Bank, 8, fGain#
	
	Local Temp% = BASS_FXSetParameters(Hnd, Bank)
	
	FreeBank(Bank) : Bank = 0
	Return Temp
End Function

Function BASS_FXSetDamp%(Hnd%, fTarget#, fQuiet#, fRate#, fGain#, fDelay#)
	Local Bank = CreateBank(24)
	
	PokeFloat Bank, 0, fTarget
	PokeFloat Bank, 4, fQuiet
	PokeFloat Bank, 8, fRate
	PokeFloat Bank, 12, fGain
	PokeFloat Bank, 16, fDelay
	PokeInt Bank, 20, -1
	
	Local Temp% = BASS_FXSetParameters(Hnd, Bank)
	
	FreeBank(Bank) : Bank = 0
	Return Temp
End Function 

Function BASS_OPUS_StreamCreate%(Header%, Flags%)
	Local Temp = BASS_OPUS_StreamCreate_(Header, Flags, -1, 0)
	
	FreeBank(Header) : Header = 0
	Return(Temp)
End Function

Const MAX_RECORD_INPUTS% = 32
Global BASS_ActiveDevices%[MAX_RECORD_INPUTS]
Global BASS_ActiveDeviceName$[MAX_RECORD_INPUTS]

Function BASS_GetRecordDeviceCount%()
	Local Bank% = CreateBank(20)
	Local Count%, Iter%
	
	While True
		ResizeBank(Bank, 0) ; ~ Clear bank
		ResizeBank(Bank, 20)
		bass_RecordGetDeviceInfo(Iter, Bank)
		
		Local DeviceMask% = PeekInt(Bank, 8)
		Local DeviceType% = (DeviceMask And BASS_DEVICE_TYPE_MASK)
		
		If PeekInt(Bank, 0) = 0 Then Exit
		
		If (DeviceMask And BASS_DEVICE_ENABLED) And (DeviceType = BASS_DEVICE_TYPE_MICROPHONE Lor (DeviceMask And BASS_DEVICE_DEFAULTCOM) = BASS_DEVICE_DEFAULTCOM Lor (DeviceMask And BASS_DEVICE_TYPE_LINE) = BASS_DEVICE_TYPE_LINE) Then
			BASS_ActiveDevices[Count] = Iter
			BASS_ActiveDeviceName[Count] = ConvertToUTF8(Memory_PeekConstChar(PeekInt(Bank, 0)))
			Count = Count + 1
			If Count >= MAX_RECORD_INPUTS Then Exit
		EndIf
		Iter = Iter + 1
	Wend
	FreeBank(Bank) : Bank = 0
	Return(Count)
End Function

Function BASS_GetRecordDevice%(i)
	Return(BASS_ActiveDevices[i])
End Function

Function BASS_GetRecordDeviceName$(i)
	Return(BASS_ActiveDeviceName[i])
End Function

Global BassStructure% = CreateBank(17 * 4)

Const DEVICE_RATE% = 48000
Const DEVICE_UPDATE_PERIOD% = 50

BASS_GetInfo(GetBassStructure())
BASS_SetConfig(BASS_CONFIG_DEV_NONSTOP, 1)
BASS_SetConfig(BASS_CONFIG_UPDATETHREADS, 2)
BASS_SetConfig(BASS_CONFIG_UPDATEPERIOD, DEVICE_UPDATE_PERIOD)
BASS_SetConfig(BASS_CONFIG_BUFFER, 500)

BASS_SetConfig(BASS_CONFIG_SAMPLE_ONEHANDLE, 0)
BASS_PluginLoad("bassopus.dll", 0)

;typedef SampleStruct
;    DWORD freq0;
;    float volume4;
;    float pan8;
;    DWORD flags12;
;    DWORD length16;
;    DWORD max20;
;    DWORD origres24;
;    DWORD chans28;
;    DWORD mingap32;
;    DWORD mode3d36;
;    float mindist40;
;    float maxdist44;
;    DWORD iangle48;
;    DWORD oangle52;
;    float outvol56;
;    DWORD vam60;
;    DWORD priority64;

;typedef BASS_INFO
;    DWORD flags0;
;    DWORD hwsize4;
;    DWORD hwfree8;
;    DWORD freesam12;
;    DWORD free3d16;
;    DWORD minrate20;
;    DWORD maxrate24;
;    BOOL eax28;
;    DWORD minbuf32;
;    DWORD dsver36;
;    DWORD latency40;
;    DWORD initflags44;
;    DWORD speakers48;
;    DWORD freq52;

Function GetBassStructure%()
	Return(BassStructure)
End Function

Function LoadSound%(File$)
	Local Sound% = BASS_SampleLoad(False, File, 0, 0, 0, 8, 0)
	
	If Sound <> 0
		BASS_SampleGetInfo(Sound, GetBassStructure())
		PokeFloat(GetBassStructure(), 4, 0.0)
		BASS_SampleSetInfo(Sound, GetBassStructure())
	EndIf
	Return(Sound)
End Function

Function FreeSound%(Sound%)
	BASS_SampleFree(Sound)
End Function

Function LoopSound%(Sound%, Loop%)
	BASS_SampleGetInfo(Sound, GetBassStructure())
	
	Local flags = PeekInt(GetBassStructure(), 12)
	
	If Loop
		flags = flags Or BASS_SAMPLE_LOOP
	Else
		flags = flags And (flags Xor BASS_SAMPLE_LOOP)
	EndIf
	BASS_SampleSetInfo(Sound, GetBassStructure())
End Function

Function SoundPitch%(Sound%, Freq%)
	BASS_SampleGetInfo(Sound, GetBassStructure())
	PokeInt(GetBassStructure(), 0, Freq)
	BASS_SampleSetInfo(Sound, GetBassStructure())
End Function

Function SoundVolume%(Sound%, Vol#)
	RuntimeError("SoundVolume: Not supported")
End Function

Function SoundPan%(sound%, pan#)
	RuntimeError("SoundPan: Not supported")
End Function

Function PlaySound%(Sound%, Volume# = 1.0)
	Local Channel% = BASS_SampleGetChannel(Sound, BASS_SAMCHAN_STREAM Or BASS_STREAM_AUTOFREE)
	
	If Channel <> 0
		BASS_SampleGetInfo(Sound, GetBassStructure())
		If PeekInt(GetBassStructure(), 0) < DEVICE_RATE * 0.8 Then BASS_ChannelSetAttribute(Channel, BASS_ATTRIB_SRC, 0) ; Low quality sounds must have linear interpolation
		
		If Volume >= 0.0
			ChannelVolume(Channel, Volume)
			ResumeChannel(Channel)
		EndIf
	EndIf
	
	Return(Channel)
End Function

Function PlayMusic%(File$, Mode%, Volume# = 1.0)
	Local Flags% = BASS_STREAM_AUTOFREE
	
	If Mode And 2 Then Flags = Flags Or BASS_SAMPLE_LOOP
	
	Local Channel% = BASS_StreamCreateFile(False, File, 0, 0, 0, 0, Flags)
	
	If Channel <> 0 And Volume >= 0.0
		ChannelVolume(Channel, Volume)
		ResumeChannel(Channel)
	EndIf
	Return(Channel)
End Function

Function StopChannel%(Channel%)
	BASS_ChannelStop(Channel)
End Function

Function PauseChannel%(Channel%)
	BASS_ChannelPause(Channel)
End Function

Function ResumeChannel%(Channel%)
	BASS_ChannelStart(Channel)
End Function

Function ChannelPitch%(Channel%, Pitch%)
	BASS_ChannelSetAttribute(Channel, BASS_ATTRIB_FREQ, Pitch)
End Function

Function ChannelVolume(Channel%, Volume#)
	BASS_ChannelSetAttribute(Channel, BASS_ATTRIB_VOL, Clamp(Volume, 0.0, 1.0))
End Function

Function ChannelPan%(Channel%, Pan#)
	BASS_ChannelSetAttribute(Channel, BASS_ATTRIB_PAN, Clamp(Pan, -1.0, 1.0))
End Function

Function ChannelPlaying%(Channel)
	Local State% = BASS_ChannelIsActive(Channel)
	
	Return(State <> BASS_ACTIVE_STOPPED)
End Function

Function ChannelReverb%(Channel%)
	Local FX% = BASS_ChannelSetFX(Channel, BASS_FX_DX8_REVERB, 0)
	
	BASS_FXSetReverb(FX, -2.5, -19, 300, 0.1)
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D TSS