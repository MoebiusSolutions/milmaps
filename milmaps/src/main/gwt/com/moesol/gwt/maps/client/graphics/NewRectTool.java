/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class NewRectTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private Rect m_rect = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewRectTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		//m_circle.erase(context);
		m_rect.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_rect = (Rect)shape;
	}

	@Override
	public IShape getShape() {
		return (IShape)m_rect;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords gc = m_convert.viewToGeodetic(vc);
		m_rect = new Rect();
		m_rect.setCoordConverter(m_convert);
		m_rect.withStartPos(gc);
		m_editor.addShape(m_rect);
		m_rect.selected(true);
		m_rect.setCoordConverter(m_editor.getCoordinateConverter());
		IAnchorTool tool = m_rect.getEndAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_rect != null && m_canvas != null) {
				m_rect.getEndAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_rect.getEndAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
		tool.setShape((IShape)m_rect);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_rect = null;
	}

	@Override
	public void handleMouseOut(int x, int y) {
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

}
