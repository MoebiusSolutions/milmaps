/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class EditCircleTool extends AbstractEditTool{	
	private Circle m_circle = null;

	public EditCircleTool(IShapeEditor se) {
		super(se);
	}
	
	private void drawHandles() {
		if (m_circle != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			m_circle.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}
	
	@Override
	public void handleMouseDown(Event event) {
		handleMouseDown(m_circle,event);
	}

	@Override
	public void handleMouseMove(Event event) {
		if (m_mouseDown == true){
			if (m_anchorTool != null){
				m_anchorTool.handleMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(Event event) {
		m_mouseDown = false;
		if (m_anchorTool == null){
			return;
		}	
		m_editor.renderObjects();
		m_anchorTool.handleMouseUp(event);
	}

	@Override
	public void handleMouseOut(Event event) {
		if (m_anchorTool == null){
			return;
		}
		m_anchorTool.handleMouseOut(event);
	}

	@Override
	public void done() {
		m_editor.clearCanvas().renderObjects();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "edit_circle_tool";
	}
	
	@Override
	public void setShape(IShape shape){
		m_circle = (Circle)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_circle;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}
}
