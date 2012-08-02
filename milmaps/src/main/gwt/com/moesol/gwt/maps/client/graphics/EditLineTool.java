/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Event;

public class EditLineTool extends AbstractEditTool{
	private Line m_line = null;

	public EditLineTool(IShapeEditor se) {
		super(se);
	}
	
	private void drawHandles() {
		if (m_line != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			m_line.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}
	
	@Override
	public void handleMouseDown(Event event) {
		AbstractShape shape = (AbstractShape)m_line;
		handleMouseDown(shape,event);
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
		return "edit_line_tool";
	}
	
	@Override
	public void setShape(IShape shape){
		m_line = (Line)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_line;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

}
