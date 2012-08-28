/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.event.dom.client.KeyCodes;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class FreeXEditTool extends AbstractEditTool{
	protected FreeForm m_freeForm = null;
	protected boolean m_cntrlKeydown = false;
	protected boolean m_shiftKeydown = false;
	
	public FreeXEditTool(IShapeEditor se) {
		super(se);
	}
	
	protected void drawHandles() {
		if (m_freeForm != null && m_canvas != null) {
			IContext context = m_canvas.getContext();
			m_freeForm.drawHandles(context);
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
}
