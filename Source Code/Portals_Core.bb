; ~ Modified DrawPortals.bb because FastExt/FastText is not used anymore
; ~ its just so aethereal

Type PortalRender
	Field Width#, Height#
	Field Cam%, Portal%, Surf%
	Field CamPitch#, CamYaw#, CamRoll#
	Field CamZoom#
	Field Tex%
	Field TexWidth%, TexHeight%
	Field TexSizeWidth%, TexSizeHeight%
	Field ID%
End Type

Function CreatePortalRender.PortalRender(x#, y#, z#, Pitch#, Yaw#, Roll#, Width#, Height#, CamX# = 0.0, CamY# = 0.0, CamZ# = 0.0, CamPitch# = 0.0, CamYaw# = 0.0, CamRoll# = 0.0, CamZoom# = 1.0, TexWidth% = 0, TexHeight% = 0)
	CatchErrors("CreatePortalRender(" + x + ", " + y + ", " + z + ", " + Pitch + ", " + Yaw + ", " + Roll + ", " + Width + ", " + Height + ", " + CamX + ", " + CamY + ", " + CamZ + ", " + CamPitch + ", " + CamYaw + ", " + CamRoll + ", " + CamZoom + ", " + TexWidth + ", " + TexHeight + ")")
	
	Local prtl.PortalRender
	
	prtl.PortalRender = New PortalRender
	
	prtl\Width = Width
	prtl\Height = Height
	
	If TexWidth = 0 Then TexWidth = opt\GraphicWidth
	If TexHeight = 0 Then TexHeight = opt\GraphicHeight
	prtl\TexWidth = TexWidth
	prtl\TexHeight = TexHeight
	
	prtl\TexSizeWidth = 1
	While prtl\TexSizeWidth < prtl\TexWidth
		prtl\TexSizeWidth = prtl\TexSizeWidth * 2
	Wend
	
	prtl\TexSizeHeight = 1
	While prtl\TexSizeHeight < prtl\TexHeight
		prtl\TexSizeHeight = prtl\TexSizeHeight * 2
	Wend
	
	prtl\Tex = CreateTexture(prtl\TexSizeWidth, prtl\TexSizeHeight, 1 + 256)
	TextureBlend(prtl\Tex, 1)
	
	prtl\Cam = CreateCamera()
	CameraRange(prtl\Cam, 0.1, 5.0)
	PositionEntity(prtl\Cam, CamX, CamY, CamZ, True)
	RotateEntity(prtl\Cam, CamPitch, CamYaw, CamRoll, True)
	CameraZoom(prtl\Cam, CamZoom)
	CameraProjMode(prtl\Cam, 0)
	
	prtl\CamPitch = CamPitch
	prtl\CamYaw = CamYaw
	prtl\CamRoll = CamRoll
	prtl\CamZoom = CamZoom
	
	prtl\Portal = CreateMesh()
	prtl\Surf = CreateSurface(prtl\Portal)
	
	Local i%, j%
	Local v0%, v1%, v2%, v3%
	
	For j = 0 To 64
		For i = 0 To 64
			AddVertex(prtl\Surf, -Width / 2.0 + Width * Float(i) / 64, Height / 2.0 - Height * Float(j) / 64, 0.0, 0.0, 0.0)
		Next
	Next
	
	For j = 0 To 64 - 1
		For i = 0 To 64 - 1
			v0 = j * (64 + 1) + i
			v1 = v0 + 1
			v3 = v0 + (64 + 1)
			v2 = v3 + 1
			AddTriangle(prtl\Surf, v0, v1, v2)
			AddTriangle(prtl\Surf, v0, v2, v3)
		Next
	Next
	
	EntityTexture(prtl\Portal, prtl\Tex)
	EntityFX(prtl\Portal, 1 + 16)
	PositionEntity(prtl\Portal, x, y, z, True)
	RotateEntity(prtl\Portal, Pitch, Yaw, Roll, True)
	
	Local Temp% = 0
	Local prtl2.PortalRender
	For prtl2.PortalRender = Each PortalRender
		Temp = Max(prtl2\ID, Temp)
	Next
	prtl\ID = Temp + 1
	
	CatchErrors("Uncaught: CreatePortalRender(" + x + ", " + y + ", " + z + ", " + Pitch + ", " + Yaw + ", " + Roll + ", " + Width + ", " + Height + ", " + CamX + ", " + CamY + ", " + CamZ + ", " + CamPitch + ", " + CamYaw + ", " + CamRoll + ", " + CamZoom + ", " + TexWidth + ", " + TexHeight + ")")
	
	Return(prtl)
End Function

Function UpdatePortalRender%(prtl.PortalRender)
	If (Not EntityExist(prtl\Cam)) Then Return
	
	CatchErrors("UpdatePortalRender()")
	
	Local i%
	Local CamX#, CamY#, CamZ#
	Local Count% = PowTwo((64 + 1))
	Local TexWidthFloat# = Float(prtl\TexWidth), TexSizeWidthFloat# = Float(prtl\TexSizeWidth)
	Local TexHeightFloat# = Float(prtl\TexHeight), TexSizeHeightFloat# = Float(prtl\TexSizeHeight)
	
	For i = 0 To Count - 1
		TFormPoint(VertexX(prtl\Surf, i), VertexY(prtl\Surf, i), 0.0, prtl\Portal, Camera)
		CamX = TFormedX() : CamY = TFormedY() : CamZ = TFormedZ()
		
		If CamZ < 0.05 Then CamZ = 0.05
		
		TFormPoint(CamX, CamY, CamZ, Camera, 0)
		CameraProject(Camera, TFormedX(), TFormedY(), TFormedZ())
		
		VertexTexCoords(prtl\Surf, i, (ProjectedX() / GraphicWidthFloat) * (TexWidthFloat / TexSizeWidthFloat), (ProjectedY() / GraphicHeightFloat) * (TexHeightFloat / TexSizeHeightFloat))
	Next
	
	RotateEntity(prtl\Cam, prtl\CamPitch, prtl\CamYaw, prtl\CamRoll, True)
	CameraZoom(prtl\Cam, prtl\CamZoom)
	
	HideEntity(Camera)
	HideEntity(prtl\Portal)
	CameraProjMode(prtl\Cam, 1)
	CameraViewport(prtl\Cam, 0, 0, prtl\TexWidth, prtl\TexHeight)
	
	RenderWorld()
	CopyRect(0, 0, prtl\TexWidth, prtl\TexHeight, 0, 0, BackBuffer(), TextureBuffer(prtl\Tex))
	
	CameraProjMode(prtl\Cam, 0)
	ShowEntity(prtl\Portal)
	ShowEntity(Camera)
	
	CatchErrors("Uncaught: UpdatePortalRender()")
End Function

Function RemovePortalRender%(prtl.PortalRender)
	CatchErrors("RemovePortalRender()")
	
	If prtl\Tex <> 0 Then FreeTexture(prtl\Tex) : prtl\Tex = 0
	prtl\TexWidth = 0 : prtl\TexHeight = 0
	If prtl\Cam <> 0 Then FreeEntity(prtl\Cam) : prtl\Cam = 0
	If prtl\Portal <> 0 Then FreeEntity(prtl\Portal) : prtl\Portal = 0
	Delete(prtl)
	
	CatchErrors("Uncaught: RemovePortalRender()")
End Function

Function UpdateForestCam%(prtl.PortalRender, fr.Forest, i%)
	Local Door% = fr\ForestDoors[i]\OBJ
	
	Local Pvt% = GetDummyPivot(EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
	
	PointEntity(Pvt, prtl\Portal)
	Local Angle# = WrapAngle(EntityYaw(Pvt) - EntityYaw(prtl\Portal, True))
	
	PositionEntity(Pvt, EntityX(Door, True), EntityY(Door, True), EntityZ(Door, True))
	RotateEntity(Pvt, 0.0, EntityYaw(Door, True), 0.0)
	
	TFormPoint(EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), 0, prtl\Portal)
	
	Local lx# = TFormedX(), ly# = TFormedY(), lz# = TFormedZ()
	
	Local FlipAngle% = (Not (Angle > 90.0 And Angle < 270.0))
	
	If FlipAngle
		TFormPoint(-lx + -0.265, ly + 0.635, -lz, Pvt, 0)
		prtl\CamYaw = WrapAngle(EntityYaw(Camera, True) - EntityYaw(prtl\Portal, True) + EntityYaw(Door, True) - 180.0)
	Else
		TFormPoint(lx + -0.265, ly + 0.635, lz, Pvt, 0)
		prtl\CamYaw = WrapAngle(EntityYaw(Camera, True) - EntityYaw(prtl\Portal, True) + EntityYaw(Door, True))
	EndIf
	
	If EntityExist(prtl\Cam) Then PositionEntity(prtl\Cam, TFormedX(), TFormedY(), TFormedZ(), True)
	Return(FlipAngle)
End Function

