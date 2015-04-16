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


import com.moesol.gwt.maps.client.ViewCoords;

public class NewPolygonTool extends  AbstractNewTool {
	private ICanvasTool m_canvas = null;
	private Polygon m_polygon = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private boolean m_firstMouseDown = false;
	private int m_lastX;
	private int m_lastY;

	public NewPolygonTool(IShapeEditor editor) {
		m_lastX = m_lastY = -10000;
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
		m_polygon = new Polygon();
	}

	
	private void drawLastLine(int x, int y) {
		AbstractPosTool tool = m_polygon.getLastPosTool();
		if (tool == null){
			return;
		}
		IContext context = m_canvas.getContext();
		context.beginPath();
		context.setStrokeStyle(m_polygon.getColor());
		context.setLineWidth(2);
		ViewCoords v = m_convert.geodeticToView(tool.getGeoPos());
		int tx  = m_convert.getISplit().adjustFirstX(v.getX(),x);
		context.moveTo(tx, v.getY());
		context.lineTo(x, y);
		context.stroke();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		m_polygon.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_polygon = (Polygon)shape;
	}

	@Override
	public IShape getShape() {
		return m_polygon;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		if (m_firstMouseDown == false){
			m_firstMouseDown = true;
			m_editor.addShape(m_polygon);
			m_polygon.selected(true);
			m_polygon.setCoordConverter(m_editor.getCoordinateConverter());
		}
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_polygon != null && m_canvas != null) {
			m_editor.clearCanvas().renderObjects();
			drawHandles();
			drawLastLine(x,y);
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		if (m_lastX != x || m_lastY != y){
			m_lastX = x;
			m_lastY = y;
			m_polygon.addVertex(x, y);
		}
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
	}

	@Override
	public void hilite() {	
	}

	@Override
	public void handleMouseDblClick(int x, int y) {
		if (m_polygon != null){
			drawHandles();
			// we are done with initial creation so set the edit tool
			IShapeTool tool = new PolygonEditTool(m_editor);
			tool.setShape((IShape)m_polygon);
			m_editor.setShapeTool(tool);
			m_editor.renderObjects();
			drawHandles();
			m_polygon = null;
		}
	}
}
