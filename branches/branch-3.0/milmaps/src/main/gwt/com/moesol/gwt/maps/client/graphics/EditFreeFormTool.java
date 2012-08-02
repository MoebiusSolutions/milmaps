/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.user.client.Event;

public class EditFreeFormTool extends FreeXEditTool{

	public EditFreeFormTool(IShapeEditor se) {
		super(se);
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
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
	public void setShape(IShape shape){
		m_freeForm = (FreeForm)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_freeForm;
	}
}
