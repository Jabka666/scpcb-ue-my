; ~ Modified DrawPortals.bb because FastExt/FastText is not used anymore
; ~ its just so aethereal

Const ResolutionScale# = 1.0

Type DrawPortal
	Field Width#, Height#
	Field Cam%, Portal%, Surf%
	Field CamPitch#, CamYaw#, CamRoll#
	Field CamZoom#
	Field Tex%
	Field TexWidth%, TexHeight%
	Field TexSizeWidth%, TexSizeHeight%
	Field ID%
End Type

Function CreateDrawPortal.DrawPortal(x#, y#, z#, Pitch#, Yaw#, Roll#, Width#, Height#, CamX# = 0.0, CamY# = 0.0, CamZ# = 0.0, CamPitch# = 0.0, CamYaw# = 0.0, CamRoll# = 0.0, CamZoom# = 1.0, TexWidth% = 0, TexHeight% = 0)
	Local ndp.DrawPortal
	
	ndp.DrawPortal = New DrawPortal
	
	ndp\Width = Width
	ndp\Height = Height
	
	If TexWidth = 0 Then TexWidth = opt\GraphicWidth * ResolutionScale
	If TexHeight = 0 Then TexHeight = opt\GraphicHeight * ResolutionScale
	ndp\TexWidth = TexWidth
	ndp\TexHeight = TexHeight
	
	ndp\TexSizeWidth = 1
	While ndp\TexSizeWidth < ndp\TexWidth
		ndp\TexSizeWidth = ndp\TexSizeWidth * 2
	Wend
	
	ndp\TexSizeHeight = 1
	While ndp\TexSizeHeight < ndp\TexHeight
		ndp\TexSizeHeight = ndp\TexSizeHeight * 2
	Wend
	
	ndp\Tex = CreateTexture(ndp\TexSizeWidth, ndp\TexSizeHeight, 1 + 256)
	TextureBlend(ndp\Tex, 1)
	
	ndp\Cam = CreateCamera()
	CameraRange(ndp\Cam, 0.1, 5.0)
	PositionEntity(ndp\Cam, CamX, CamY, CamZ, True)
	RotateEntity(ndp\Cam, CamPitch, CamYaw, CamRoll, True)
	CameraZoom(ndp\Cam, CamZoom)
	CameraProjMode(ndp\Cam, 0)
	
	ndp\CamPitch = CamPitch
	ndp\CamYaw = CamYaw
	ndp\CamRoll = CamRoll
	ndp\CamZoom = CamZoom
	
	ndp\Portal = CreateMesh()
	ndp\Surf = CreateSurface(ndp\Portal)
	
	Local i%, j%
	Local v0%, v1%, v2%, v3%
	
	For j = 0 To 96
		For i = 0 To 96
			AddVertex(ndp\Surf, -Width / 2.0 + Width * Float(i) / 96, Height / 2.0 - Height * Float(j) / 96, 0.0, 0.0, 0.0)
		Next
	Next
	
	For j = 0 To 96 - 1
		For i = 0 To 96 - 1
			v0 = j * (96 + 1) + i
			v1 = v0 + 1
			v3 = v0 + (96 + 1)
			v2 = v3 + 1
			AddTriangle(ndp\Surf, v0, v1, v2)
			AddTriangle(ndp\Surf, v0, v2, v3)
		Next
	Next
	
	EntityTexture(ndp\Portal, ndp\Tex)
	EntityFX(ndp\Portal, 1 + 16)
	PositionEntity(ndp\Portal, x, y, z, True)
	RotateEntity(ndp\Portal, Pitch, Yaw, Roll, True)
	
	Local Temp% = 0
	Local e.DrawPortal
	For e.DrawPortal = Each DrawPortal
		Temp = Max(e\ID, Temp)
	Next
	ndp\ID = Temp + 1
	
	Return(ndp)
End Function

Function DestroyDrawPortal%(ndp.DrawPortal)
	If ndp\Tex <> 0 Then FreeTexture(ndp\Tex)
	ndp\Tex = 0 : ndp\TexWidth = 0 : ndp\TexHeight = 0
	If ndp\Cam <> 0 Then FreeEntity(ndp\Cam)
	ndp\Cam = 0
	If ndp\Portal <> 0 Then FreeEntity(ndp\Portal)
	ndp\Portal = 0
	Delete(ndp)
End Function

Function UpdateDrawPortal%(ndp.DrawPortal)
	Local i%
	Local CamX#, CamY#, CamZ#
	Local Count% = PowTwo((96 + 1))
	
	If (Not EntityExist(ndp\Cam)) Then Return
	
	For i = 0 To Count - 1
		TFormPoint(VertexX(ndp\Surf, i), VertexY(ndp\Surf, i), 0.0, ndp\Portal, Camera)
		CamX = TFormedX() : CamY = TFormedY() : CamZ = TFormedZ()
		
		If CamZ < 0.05 Then CamZ = 0.05
		
		TFormPoint(CamX, CamY, CamZ, Camera, 0)
		CameraProject(Camera, TFormedX(), TFormedY(), TFormedZ())
		
		VertexTexCoords(ndp\Surf, i, (ProjectedX() / GraphicWidthFloat) * (Float(ndp\TexWidth) / Float(ndp\TexSizeWidth)), (ProjectedY() / GraphicHeightFloat) * (Float(ndp\TexHeight) / Float(ndp\TexSizeHeight)))
	Next
	
	RotateEntity(ndp\Cam, ndp\CamPitch, ndp\CamYaw, ndp\CamRoll, True)
	CameraZoom(ndp\Cam, ndp\CamZoom)
	
	HideEntity(Camera)
	HideEntity(ndp\Portal)
	CameraProjMode(ndp\Cam, 1)
	CameraViewport(ndp\Cam, 0, 0, ndp\TexWidth, ndp\TexHeight)
	
	RenderWorld()
	CopyRect(0, 0, ndp\TexWidth, ndp\TexHeight, 0, 0, BackBuffer(), TextureBuffer(ndp\Tex))
	
	CameraProjMode(ndp\Cam, 0)
	ShowEntity(ndp\Portal)
	ShowEntity(Camera)
End Function

Function UpdateForestCam%(dp.DrawPortal, fr.Forest, i%)
	Local Door% = fr\ForestDoors[i]\OBJ
	
	Local Pvt% = CreatePivot()
	
	PositionEntity Pvt, EntityX(Camera,True), EntityY(Camera,True), EntityZ(Camera,True)
	PointEntity(Pvt, dp\Portal)
	Local Angle# = WrapAngle(EntityYaw(Pvt) - EntityYaw(dp\Portal, True))
	
	PositionEntity Pvt, EntityX(Door,True), EntityY(Door,True), EntityZ(Door,True)
	RotateEntity Pvt, 0, EntityYaw(Door,True), 0
	
	TFormPoint EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 0, dp\Portal
	
	Local lx# = TFormedX(), ly# = TFormedY(), lz# = TFormedZ()
	
	Local Flip_% = (Not (Angle > 90 And Angle < 270))
	
	If Flip_
		TFormPoint(-lx + -0.265, ly + 0.635, -lz, Pvt, 0)
		dp\CamYaw = WrapAngle(EntityYaw(Camera, True) - EntityYaw(dp\Portal, True) + EntityYaw(Door, True) - 180.0)
	Else
		TFormPoint(lx + -0.265, ly + 0.635, lz, Pvt, 0)
		dp\CamYaw = WrapAngle(EntityYaw(Camera, True) - EntityYaw(dp\Portal, True) + EntityYaw(Door, True))
	EndIf
	
	If EntityExist(dp\Cam) Then PositionEntity(dp\Cam, TFormedX(), TFormedY(), TFormedZ(), True)
	FreeEntity(Pvt)
	Return(Flip_)
End Function
