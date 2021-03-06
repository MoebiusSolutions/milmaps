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

public class NewCircleTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private Circle m_circle = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewCircleTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
		m_circle = new Circle();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		//m_circle.erase(context);
		m_circle.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_circle = (Circle)shape;
	}

	@Override
	public IShape getShape() {
		return m_circle;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_circle.withCenter(center);
		m_editor.addShape(m_circle);
		m_circle.selected(true);
		m_circle.setCoordConverter(m_editor.getCoordinateConverter());
		IAnchorTool tool = m_circle.getRadiusAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_circle != null && m_canvas != null) {
				m_circle.getRadiusAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_circle.getRadiusAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
		tool.setShape((IShape)m_circle);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_circle = null;
	}

	@Override
	public void handleMouseOut(int x, int y) {
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}
}
