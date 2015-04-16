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


import com.google.gwt.event.dom.client.KeyCodes;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class PolygonEditTool extends AbstractEditTool{
	protected Polygon m_polygon = null;
	protected boolean m_cntrlKeydown = false;
	protected boolean m_shiftKeydown = false;
	
	public PolygonEditTool(IShapeEditor se) {
		super(se);
	}
	
	protected void drawHandles() {
		if (m_polygon != null && m_canvas != null) {
			IContext context = m_canvas.getContext();
			m_polygon.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}

	@Override
	public void handleMouseDown(int x, int y) {
		// Get Selected Anchor
		m_mouseDown = true;
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		m_anchorTool = m_polygon.getAnchorByPosition(gc);
		if(m_anchorTool == null){
			if (m_cntrlKeydown && !m_shiftKeydown){
				int j = m_polygon.pointHitSegment(x,y);
				if (j < m_polygon.size()){
					m_polygon.insertVertex(j, x, y);
					m_editor.clearCanvas().renderObjects();
					drawHandles();
				}
			}
			else{
				m_polygon.selected(false);
				m_editor.clearCanvas().renderObjects();
				m_editor.setShapeTool(new SelectShape(m_editor));
			}
		}
		else{
			if (m_cntrlKeydown && m_shiftKeydown){
				m_polygon.removeVertex((AbstractPosTool)m_anchorTool);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
				m_anchorTool = null;
			}
		}
	}
	
	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown == true){
			if (m_anchorTool != null){
				m_anchorTool.handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}
	
	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		if (m_anchorTool != null){
			m_editor.renderObjects();
			m_anchorTool.handleMouseUp(x,y);
		}	
	}

	@Override
	public void handleMouseOut(int x, int y) {
		if (m_anchorTool != null){
			m_anchorTool.handleMouseOut(x,y);
		}
	}

	@Override
	public void done() {
		m_editor.clearCanvas().renderObjects();
	}
	
	@Override
	public void setShape(IShape shape){
		m_polygon = (Polygon)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_polygon;
	}
	
	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}
	
	@Override
	public void handleKeyDown(int keyCode) {
		if (keyCode == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = true;
		}
		else if (keyCode == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = true;
		}
	}

	@Override
	public void handleKeyUp(int keyCode) {
		if (keyCode == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = false;
		}
		else if (keyCode == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = false;
		}
	}
	
	public boolean getKeyDownFlag(int keyCode){
		if(keyCode == KeyCodes.KEY_CTRL){
			return m_cntrlKeydown;
		}
		else if(keyCode == KeyCodes.KEY_SHIFT){
			return m_shiftKeydown;
		}
		return false;
	}
}
