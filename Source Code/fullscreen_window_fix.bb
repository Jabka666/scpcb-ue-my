; ~ Requires:-
; ~ user32.decls - http://www.blitzbasic.com/codearcs/codearcs.php?code=1179

; ~ Place this file into your Blitz3D 'userlibs' folder (eg. C:\Program Files (x86)\Blitz3D\userlibs).

; ~ Probably also a good idea to have these in your userlibs folder, just on general principles.
; ~ kernel32.decls - http://www.blitzbasic.com/codearcs/codearcs.php?code=1180
; ~ gdi32.decls - http://www.blitzbasic.com/codearcs/codearcs.php?code=1181

; ~ The Win32 Constants are also useful for windows API stuff.
; ~ Win32 Constants - http://www.blitzbasic.com/codearcs/codearcs.php?code=1607

; ~ Additional reference links:-
; ~ http://www.gamedev.net/topic/418170-win32-borderless-window/
; ~ http://stackoverflow.com/questions/15254078/win32-fullscreen-borderless-window-overlapping-taskbar



; ~ Get the width and height of the desktop and place them into these globals.
Global G_Desktop_Screen_Width%
Global G_Desktop_Screen_Height%
GetDesktopSize()

Global G_Viewport_X% = 0
Global G_Viewport_Y% = 0
Global G_Viewport_Width% = G_Desktop_Screen_Width
Global G_Viewport_Height% = G_Desktop_Screen_Height

; ~ Get the OS handle of the app window.
Global G_App_Handle% = SystemProperty("AppHWND")

If (Not Windowed3D()) Then RuntimeError("FATAL ERROR: Your computer does not support the rendering of 3D graphics within a window.")

Function GetDesktopSize()
	; ~ Gets the width and height of the desktop on the main monitor and returns them in the globals G_Desktop_Screen_Width And G_Desktop_Screen_Height.
	
	Local RectAngle% = CreateBank(16)
	
	Api_GetClientRect(Api_GetDesktopWindow(), RectAngle)
	G_Desktop_Screen_Width = PeekInt(RectAngle, 8) - PeekInt(RectAngle, 0)
	G_Desktop_Screen_Height = PeekInt(RectAngle, 12) - PeekInt(RectAngle, 4)
	FreeBank RectAngle
End Function

Function SyncGame()
	; ~ NOTES:
	; ~ This function should be run immediately before a game session begins and also after resuming from a pause.

	; ~ At this point everything should be setup and ready to start/restart the game immediately.

	; ~ Reset the input devices.
	MoveMouse(G_Viewport_Center_X, G_Viewport_Center_Y)
	FlushMouse
	FlushKeys 
	MouseXSpeed()
	MouseYSpeed()
	MouseHit(1)
	MouseHit(2)
	MouseHit(3)

	; ~ Set and render the backbuffer, and then flip it to the frontbuffer.
	SetBuffer(BackBuffer())
	RenderWorld
	Flip

	; ~ Synch the timing. This assumes that the 'G_old_time' global holds the Millisecs() time
	; ~ Set at the start of the previous loop and is used with render-tweening or delta-timing.
	G_Old_Time = MilliSecs()
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D