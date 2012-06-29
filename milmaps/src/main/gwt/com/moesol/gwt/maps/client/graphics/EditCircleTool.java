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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class EditCircleTool implements IShapeTool{
	private Circle m_circle = null;
	private Canvas m_canvas = null;
	private boolean m_mouseDown = false;
	private IAnchorTool m_anchorTool = null;
	private ICoordConverter m_convert;
	private IShapeEditor m_editor;

	public EditCircleTool(IShapeEditor se) {
		m_editor = se;
		m_canvas = se.getCanvasTool().canvas();
		m_convert = se.getCoordinateConverter();
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
	public void handleMouseDown(MouseDownEvent event) {
		// Get Selected Anchor
		int x = event.getX();
		int y = event.getY();
		m_mouseDown = true;
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		m_anchorTool = m_circle.getAnchorByPosition(gc);
		if(m_anchorTool == null){
			m_circle.selected(false);
			m_editor.clearCanvas().renderObjects();
			m_editor.setShapeTool(new SelectShape(m_editor));
		}
	}

	@Override
	public void handleMouseMove(MouseMoveEvent event) {
		if (m_mouseDown == true){
			if (m_anchorTool != null){
				m_anchorTool.handleMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(MouseUpEvent event) {
		m_mouseDown = false;
		if (m_anchorTool == null){
			return;
		}	
		m_editor.renderObjects();
		m_anchorTool.handleMouseUp(event);
		return;
	}

	@Override
	public void handleMouseOut(MouseOutEvent event) {
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

	@Override
	public void handleMouseDblClick(Event event) {
	}
}
