/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.Distance;

public class NewEllipseTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private Ellipse m_ellipse = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private static final RangeBearingS m_rb = new RangeBearingS();

	public NewEllipseTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
		m_ellipse = new Ellipse();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		m_ellipse.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_ellipse = (Ellipse)shape;
	}

	@Override
	public IShape getShape() {
		return m_ellipse;
	}
	
	private void setSmnHandle(){
		// this initial value is a default.
		GeodeticCoords smjPos = m_ellipse.getSmjPos();
		GeodeticCoords cenPos = m_ellipse.getCenter();
		double disKm = m_rb.gcRangeFromTo(cenPos, smjPos)/4;
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_ellipse.setSmnAxis(dis);
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_ellipse.withCenter(center);
		m_editor.addShape(m_ellipse);
		m_ellipse.selected(true);
		m_ellipse.setCoordConverter(m_editor.getCoordinateConverter());
		m_ellipse.getSmnAnchorTool();
		IAnchorTool tool = m_ellipse.getSmjAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_ellipse != null && m_canvas != null) {
				m_ellipse.getSmjAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				setSmnHandle();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_ellipse.getSmjAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
		tool.setShape((IShape)m_ellipse);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_ellipse = null;
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		// TODO Auto-generated method stub
	}
}
