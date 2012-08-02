/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;

public class EditArcTool extends AbstractEditTool{
	private Arc m_arc = null;
	
	private boolean m_ctrlKeydown = false;
	private boolean m_shiftKeydown = false;

	public EditArcTool(IShapeEditor se) {
		super(se);
	}
	
	private void drawHandles() {
		if (m_arc != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			m_arc.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}
	
	@Override
	public void handleMouseDown(Event event) {
		handleMouseDown(m_arc,event);
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
		return "edit_arc_tool";
	}
	
	@Override
	public void setShape(IShape shape){
		m_arc = (Arc)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_arc;
	}
	
	@Override
	public void setAnchor(IAnchorTool anchor) {
	}

	@Override
	public void handleKeyDown(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = true;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = true;
		}
		m_arc.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}

	@Override
	public void handleKeyUp(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_ctrlKeydown = false;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = false;
		}
		m_arc.setKeyboardFlags(m_ctrlKeydown, m_shiftKeydown);
	}
}
