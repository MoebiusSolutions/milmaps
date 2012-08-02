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

public class EditTriangleTool extends AbstractEditTool{
	private Triangle m_triangle = null;

	public EditTriangleTool(IShapeEditor se) {
		super(se);
	}
	
	private void drawHandles() {
		if (m_triangle != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			m_triangle.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}
	
	@Override
	public void handleMouseDown(Event event) {
		handleMouseDown(m_triangle,event);
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
		if (m_anchorTool != null){
			m_editor.renderObjects();
			m_anchorTool.handleMouseUp(event);
		}	
	}

	@Override
	public void handleMouseOut(Event event) {
		if (m_anchorTool != null){
			m_anchorTool.handleMouseOut(event);
		}
	}

	@Override
	public void done() {
		m_editor.clearCanvas().renderObjects();
	}

	@Override
	public String getType() {
		return "edit_triangle_tool";
	}
	
	@Override
	public void setShape(IShape shape){
		m_triangle = (Triangle)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_triangle;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

	@Override
	public void handleKeyDown(Event event) {
	}

	@Override
	public void handleKeyUp(Event event) {
	}
}
