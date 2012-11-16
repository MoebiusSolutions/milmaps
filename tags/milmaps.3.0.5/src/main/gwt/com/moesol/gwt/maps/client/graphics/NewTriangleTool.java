/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.ViewCoords;

public class NewTriangleTool extends  AbstractNewTool {
	private ICanvasTool m_canvas = null;
	private Triangle m_triangle = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private boolean m_firstMouseDown = false;
	private int m_lastX;
	private int m_lastY;

	public NewTriangleTool(IShapeEditor editor) {
		m_lastX = m_lastY = -10000;
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
		m_triangle = new Triangle();
	}
	
	private void drawLastLine(int x, int y) {
		AbstractPosTool tool = m_triangle.getLastPosTool();
		if (tool == null){
			return;
		}
		IContext context = m_canvas.getContext();
		context.beginPath();
		context.setStrokeStyle(m_triangle.getColor());
		context.setLineWidth(2);
		ViewCoords v = m_convert.geodeticToView(tool.getGeoPos());
		int tx  = m_convert.getISplit().adjustFirstX(v.getX(),x);
		context.moveTo(tx, v.getY());
		context.lineTo(x, y);
		context.stroke();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		m_triangle.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_triangle = (Triangle)shape;
	}

	@Override
	public IShape getShape() {
		return m_triangle;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		if (m_firstMouseDown == false){
			m_firstMouseDown = true;
			m_editor.addShape(m_triangle);
			m_triangle.selected(true);
			m_triangle.setCoordConverter(m_editor.getCoordinateConverter());
		}
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_triangle != null && m_canvas != null) {
			m_editor.clearCanvas().renderObjects();
			drawHandles();
			drawLastLine(x,y);
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		if (m_lastX != x || m_lastY != y){
			m_lastX = x;
			m_lastY = y;
			m_triangle.addVertex(x, y);
		}
		if (m_triangle.size() == 3){
			drawHandles();
			// we are done with initial creation so set the edit tool
			IShapeTool tool = new CommonEditTool(m_editor);
			tool.setShape((IShape)m_triangle);
			m_editor.setShapeTool(tool);
			m_editor.renderObjects();
			drawHandles();
			m_triangle = null;		
		}
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
