;modified drawportal.bb because fastext/fasttext is not used anymore
;its just so aethereal

Const ResolutionScale# = 1

Type DrawPortal
	Field w#,h#
	Field cam%,portal%,surf%
	Field campitch#,camyaw#,camroll#
	Field camZoom#
	Field tex%
	Field texw%,texh%
	Field texsizew%,texsizeh%
	Field id%
End Type

Function CreateDrawPortal.DrawPortal(x#,y#,z#,pitch#,yaw#,roll#,w#,h#,camx#=0.0,camy#=0.0,camz#=0.0,campitch#=0.0,camyaw#=0.0,camroll#=0.0,camZoom#=1.0,texw%=0,texh%=0)
	Local ndp.DrawPortal = New DrawPortal
	
	ndp\w = w
	ndp\h = h
	
	If texw=0 Then texw = opt\GraphicWidth * ResolutionScale
	If texh=0 Then texh = opt\GraphicHeight * ResolutionScale
	ndp\texw = texw
	ndp\texh = texh
	
	ndp\texsizew = 1 : While ndp\texsizew < ndp\texw : ndp\texsizew = ndp\texsizew*2 : Wend
	ndp\texsizeh = 1 : While ndp\texsizeh < ndp\texh : ndp\texsizeh = ndp\texsizeh*2 : Wend
	
	ndp\tex = CreateTexture(ndp\texsizew, ndp\texsizeh, 1+256)
	TextureBlend ndp\tex,1
	
	ndp\cam = CreateCamera()
	CameraRange ndp\cam,0.1,5
	PositionEntity ndp\cam,camx,camy,camz,True
	RotateEntity ndp\cam,campitch,camyaw,camroll,True
	CameraZoom ndp\cam,camZoom
	CameraProjMode ndp\cam,0
	
	ndp\campitch = campitch
	ndp\camyaw = camyaw
	ndp\camroll = camroll
	ndp\camZoom = camZoom
	
	ndp\portal = CreateMesh()
	ndp\surf = CreateSurface(ndp\portal)
	Local i%, j%
	Local v0%, v1%, v2%, v3%
	
	For j = 0 To 96
		For i = 0 To 96
			AddVertex ndp\surf,-w/2.0 + w*Float(i)/96, h/2.0 - h*Float(j)/96, 0.0, 0.0, 0.0
		Next
	Next
	
	For j= 0 To 96-1
		For i = 0 To 96-1
			v0 = j * (96+1) + i
			v1 = v0+1
			v3 = v0+(96+1)
			v2 = v3+1
			AddTriangle ndp\surf,v0,v1,v2
			AddTriangle ndp\surf,v0,v2,v3
		Next
	Next
	
	EntityTexture ndp\portal,ndp\tex
	EntityFX ndp\portal,1+16
	PositionEntity ndp\portal,x,y,z,True
	RotateEntity ndp\portal,pitch,yaw,roll,True
	
	Local temp%=0
	Local e.DrawPortal
	For e.DrawPortal = Each DrawPortal
		temp = Max(e\id,temp)
	Next
	ndp\id = temp+1
	
	Return ndp
End Function

Function DestroyDrawPortal(ndp.DrawPortal)
	If ndp\tex<>0 Then FreeTexture ndp\tex
	ndp\tex = 0 : ndp\texw = 0 : ndp\texh = 0
	If ndp\cam<>0 Then FreeEntity ndp\cam
	ndp\cam = 0
	If ndp\portal<>0 Then FreeEntity ndp\portal
	ndp\portal = 0
	Delete ndp
End Function

Function UpdateDrawPortal(ndp.DrawPortal)
	Local i%
	Local cx#,cy#,cz#
	Local count% = (96+1)*(96+1)
	
	If Not EntityExist(ndp\cam) Then Return
	
	For i = 0 To count-1
		TFormPoint(VertexX(ndp\surf,i), VertexY(ndp\surf,i), 0.0, ndp\portal, Camera)
		cx = TFormedX() : cy = TFormedY() : cz = TFormedZ()
		
		If cz < 0.05 Then cz = 0.05
		
		TFormPoint cx,cy,cz,Camera,0
		CameraProject Camera,TFormedX(),TFormedY(),TFormedZ()
		
		VertexTexCoords(ndp\surf, i, (ProjectedX()/Float(opt\GraphicWidth)) * (Float(ndp\texw)/Float(ndp\texsizew)), (ProjectedY()/Float(opt\GraphicHeight)) * (Float(ndp\texh)/Float(ndp\texsizeh)))
	Next
	
	RotateEntity ndp\cam,ndp\campitch,ndp\camyaw,ndp\camroll,True
	CameraZoom ndp\cam,ndp\camZoom
	
	HideEntity Camera
	HideEntity ndp\portal
	CameraProjMode ndp\cam,1
	CameraViewport ndp\cam,0,0,ndp\texw,ndp\texh
	
	RenderWorld
	CopyRect 0,0,ndp\texw,ndp\texh,0,0,BackBuffer(),TextureBuffer(ndp\tex)
	
	CameraProjMode ndp\cam,0
	ShowEntity ndp\portal
	ShowEntity Camera
End Function

Function UpdateForestCam%(dp.DrawPortal, fr.Forest, i%)
	Local door% = fr\ForestDoors[i]\OBJ
	
	Local p% = CreatePivot()
	PositionEntity p, EntityX(Camera,True), EntityY(Camera,True), EntityZ(Camera,True)
	PointEntity p, dp\portal
	Local ang# = WrapAngle(EntityYaw(p) - EntityYaw(dp\portal,True))
	
	PositionEntity p, EntityX(door,True), EntityY(door,True), EntityZ(door,True)
	RotateEntity p, 0, EntityYaw(door,True), 0
	
	TFormPoint EntityX(Camera,True), EntityY(Camera,True), EntityZ(Camera,True), 0, dp\portal
	
	Local lx# = TFormedX(), ly# = TFormedY(), lz# = TFormedZ()
	
	Local flip% = (Not (ang > 90 And ang < 270))
	
	If flip
		TFormPoint -lx + -0.265, ly + 0.635, -lz, p, 0
		dp\camyaw = WrapAngle(EntityYaw(Camera,True) - EntityYaw(dp\portal,True) + EntityYaw(door,True) - 180.0)
	Else
		TFormPoint lx + -0.265, ly + 0.635, lz, p, 0
		dp\camyaw = WrapAngle(EntityYaw(Camera,True) - EntityYaw(dp\portal,True) + EntityYaw(door,True))
	EndIf
	
	If EntityExist(dp\cam) Then PositionEntity dp\cam, TFormedX(), TFormedY(), TFormedZ(), True
	FreeEntity p
	Return flip
End Function