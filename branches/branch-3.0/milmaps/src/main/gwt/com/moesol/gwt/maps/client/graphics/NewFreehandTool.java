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
import com.moesol.gwt.maps.client.ViewCoords;

public class NewFreehandTool extends  AbstractNewTool {
	private Canvas m_canvas = null;
	private Freehand m_freehand = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private int m_count = 0;
	private boolean m_mouseDown = false;
	private int m_lastX;
	private int m_lastY;

	public NewFreehandTool(IShapeEditor editor) {
		m_lastX = m_lastY = -10000;
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawLastLine(int x, int y) {
		AbstractPosTool tool = m_freehand.getLastPosTool();
		if (tool == null){
			return;
		}
		Context2d context = m_canvas.getContext2d();
		context.beginPath();
		context.setStrokeStyle(m_freehand.getColor());
		context.setLineWidth(2);
		ViewCoords v = m_convert.geodeticToView(tool.getGeoPos());
		int tx  = m_convert.getISplit().adjustFirstX(v.getX(),x);
		context.moveTo(tx, v.getY());
		context.lineTo(x, y);
		context.stroke();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		m_freehand.drawHandles(context);
	}
	
	private void createEditTool(){
		if (m_freehand != null){
			drawHandles();
			// we are done with initial creation so set the edit tool
			IShapeTool tool = new FreeXEditTool(m_editor);
			tool.setShape((IShape)m_freehand);
			m_editor.setShapeTool(tool);
			m_editor.renderObjects();
			drawHandles();
			m_freehand = null;
		}
	}
	
	private void addVertex(int x, int y){
		m_count++;
		if (m_count%3 == 0){
			m_count = 0;
			if (m_lastX != x || m_lastY != y){
				m_lastX = x;
				m_lastY = y;
				m_freehand.addVertex(x, y);
			}			
		}
	}
	
	@Override
	public void setShape(IShape shape) {
		m_freehand = (Freehand)shape;
	}

	@Override
	public IShape getShape() {
		return m_freehand;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		if (m_freehand == null){
			m_freehand = new Freehand();
			m_editor.addShape(m_freehand);
			m_freehand.selected(true);
			m_freehand.setCoordConverter(m_editor.getCoordinateConverter());
		}
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown && m_freehand != null && m_canvas != null) {
			m_editor.clearCanvas().renderObjects();
			drawHandles();
			drawLastLine(x,y);
			addVertex(x,y);
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		addVertex(x, y);
		createEditTool();
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
	}

	@Override
	public void hilite() {	
	}

	@Override
	public void handleMouseDblClick(int x, int y) {
	}

}
