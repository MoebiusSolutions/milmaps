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
	private boolean m_shiftKeydown = false;

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
	public void handleMouseDown(Event event) {
		// Get Selected Anchor
		int x = event.getClientX();
		int y = event.getClientY();
		m_mouseDown = true;
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		m_anchorTool = m_freeForm.getAnchorByPosition(gc);
		if(m_anchorTool == null){
			if (m_cntrlKeydown && !m_shiftKeydown){
				int j = m_freeForm.pointHitSegment(x,y);
				if (j < m_freeForm.size()){
					m_freeForm.insertVertex(j, x, y);
					m_editor.clearCanvas().renderObjects();
					drawHandles();
				}
			}
			else{
				m_freeForm.selected(false);
				m_editor.clearCanvas().renderObjects();
				m_editor.setShapeTool(new SelectShape(m_editor));
			}
		}
		else{
			if (m_cntrlKeydown && m_shiftKeydown){
				m_freeForm.removeVertex((AbstractPosTool)m_anchorTool);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
				m_anchorTool = null;
			}
		}
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
	public void handleKeyDown(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = true;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = true;
		}
	}

	@Override
	public void handleKeyUp(Event event) {
		if (event.getKeyCode() == KeyCodes.KEY_CTRL){
			m_cntrlKeydown = false;
		}
		else if (event.getKeyCode() == KeyCodes.KEY_SHIFT){
			m_shiftKeydown = false;
		}
	}
}
