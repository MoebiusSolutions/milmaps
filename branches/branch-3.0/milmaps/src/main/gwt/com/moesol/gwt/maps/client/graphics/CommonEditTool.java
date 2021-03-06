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

public class CommonEditTool extends AbstractEditTool {
	protected AbstractShape m_abShape = null;
	
	public CommonEditTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void setShape(IShape shape){
		m_abShape = (AbstractShape)shape;
	}
	
	@Override
	public void handleMouseDown(int x, int y) {
		if (m_abShape == null) {
			throw new IllegalStateException("CommonEditTool: m_abShape = null");
		}
		// Get Selected Anchor
		m_mouseDown = true;
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		m_anchorTool = m_abShape.getAnchorByPosition(gc);
		if(m_anchorTool == null){
			m_abShape.selected(false);
			m_editor.clearCanvas().renderObjects();
			m_editor.setShapeTool(new SelectShape(m_editor));
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
	
	protected void drawHandles() {
		if (m_abShape != null && m_canvas != null) {
			IContext context = m_canvas.getContext();
			m_abShape.drawHandles(context);
		}
	}
	
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
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
	public IShape getShape() {
		return (IShape)m_abShape;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

	@Override
	public void handleKeyDown(int keyCode) {
		boolean ctrlKeydown = false;
		boolean shiftKeydown = false;
		if (keyCode == KeyCodes.KEY_CTRL){
			ctrlKeydown = true;
		}
		else if (keyCode == KeyCodes.KEY_SHIFT){
			shiftKeydown = true;
		}
		m_abShape.setKeyboardFlags(ctrlKeydown, shiftKeydown);
	}

	@Override
	public void handleKeyUp(int keyCode) {
		boolean ctrlKeydown = false;
		boolean shiftKeydown = false;
		if (keyCode == KeyCodes.KEY_CTRL){
			ctrlKeydown = false;
		}
		else if (keyCode == KeyCodes.KEY_SHIFT){
			shiftKeydown = false;
		}
		m_abShape.setKeyboardFlags(ctrlKeydown, shiftKeydown);
	}
}
