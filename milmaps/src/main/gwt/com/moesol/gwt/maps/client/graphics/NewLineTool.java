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
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class NewLineTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private Canvas m_canvas = null;
	private Line m_line = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewLineTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		//m_circle.erase(context);
		m_line.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_line = (Line)shape;
	}

	@Override
	public IShape getShape() {
		return (IShape)m_line;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords gc = m_convert.viewToGeodetic(vc);
		m_line = new Line();
		m_line.setCoordConverter(m_convert);
		m_line.withStartPos(gc);
		m_editor.addShape(m_line);
		m_line.selected(true);
		m_line.setCoordConverter(m_editor.getCoordinateConverter());
		IAnchorTool tool = m_line.getEndAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_line != null && m_canvas != null) {
				m_line.getEndAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_line.getEndAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
		tool.setShape((IShape)m_line);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_line = null;
	}

	@Override
	public void handleMouseOut(int x, int y) {
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}
}
