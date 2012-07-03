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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class EditFreeFormTool extends AbstractEditTool{
	private FreeForm m_freeForm = null;
	private Canvas m_canvas = null;
	private boolean m_mouseDown = false;
	private IAnchorTool m_anchorTool = null;
	private ICoordConverter m_convert;
	private IShapeEditor m_editor;
	private boolean m_cntrlKeydown = false;

	public EditFreeFormTool(IShapeEditor se) {
		m_editor = se;
		m_canvas = se.getCanvasTool().canvas();
		m_convert = se.getCoordinateConverter();
	}
	
	private void drawHandles() {
		if (m_freeForm != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			m_freeForm.drawHandles(context);
		}
	}
	
	@Override
	public void hilite() {
		m_editor.renderObjects();
		drawHandles();
	}
	
	@Override
	public boolean handleMouseDown(Event event) {
		// Get Selected Anchor
		int x = event.getClientX();
		int y = event.getClientY();
		m_mouseDown = true;
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		m_anchorTool = m_freeForm.getAnchorByPosition(gc);
		if(m_anchorTool == null){
			m_freeForm.selected(false);
			m_editor.clearCanvas().renderObjects();
			m_editor.setShapeTool(new SelectShape(m_editor));
		}
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleMouseMove(Event event) {
		if (m_mouseDown == true){
			if (m_anchorTool != null){
				m_anchorTool.handleMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
			return CAPTURE_EVENT;
		}
		return PASS_EVENT;
	}

	@Override
	public boolean handleMouseUp(Event event) {
		m_mouseDown = false;
		if (m_anchorTool == null){
			return PASS_EVENT;
		}	
		m_editor.renderObjects();
		m_anchorTool.handleMouseUp(event);
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleMouseOut(Event event) {
		if (m_anchorTool == null){
			return PASS_EVENT;
		}
		return m_anchorTool.handleMouseOut(event);
	}

	@Override
	public void done() {
		m_editor.clearCanvas().renderObjects();
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "edit_freeForm_tool";
	}
	
	@Override
	public void setShape(IShape shape){
		m_freeForm = (FreeForm)shape; 
	}

	@Override
	public IShape getShape() {
		return (IShape)m_freeForm;
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		m_anchorTool = anchor;
	}

	@Override
	public boolean handleKeyDown(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = true;
			return CAPTURE_EVENT;
		}
		return PASS_EVENT;
	}

	@Override
	public boolean handleKeyUp(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = false;
			return CAPTURE_EVENT;
		}
		return PASS_EVENT;
	}
}
