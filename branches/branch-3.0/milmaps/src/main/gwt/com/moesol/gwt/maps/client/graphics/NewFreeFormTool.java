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
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.ViewCoords;

public class NewFreeFormTool extends  AbstractNewTool {
	private Canvas m_canvas = null;
	private FreeForm m_freeForm = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	
	private int m_lastX;
	private int m_lastY;

	public NewFreeFormTool(IShapeEditor editor) {
		m_lastX = m_lastY = -10000;
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}

	
	private void drawLastLine(int x, int y) {
		AbstractPosTool tool = m_freeForm.getLastPosTool();
		if (tool == null){
			return;
		}
		Context2d context = m_canvas.getContext2d();
		context.beginPath();
		context.setStrokeStyle(m_freeForm.getColor());
		context.setLineWidth(2);
		ViewCoords v = m_convert.geodeticToView(tool.getGeoPos());
		int tx  = m_convert.getISplit().adjustFirstX(v.getX(),x);
		context.moveTo(tx, v.getY());
		context.lineTo(x, y);
		context.stroke();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		m_freeForm.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_freeForm = (FreeForm)shape;
	}

	@Override
	public IShape getShape() {
		return m_freeForm;
	}

	@Override
	public void handleMouseDown(Event event) {
		if (m_freeForm == null){
			m_freeForm = new FreeForm();
			m_editor.addShape(m_freeForm);
			m_freeForm.selected(true);
			m_freeForm.setCoordConverter(m_editor.getCoordinateConverter());
		}
	}

	@Override
	public void handleMouseMove(Event event) {
		if (m_freeForm != null && m_canvas != null) {
			m_editor.clearCanvas().renderObjects();
			drawHandles();
			int x = event.getClientX();
			int y = event.getClientY();
			drawLastLine(x,y);
		}
	}

	@Override
	public void handleMouseUp(Event event) {
		int x = event.getClientX();
		int y = event.getClientY();
		if (m_lastX != x || m_lastY != y){
			m_lastX = x;
			m_lastY = y;
			m_freeForm.addVertex(x, y);
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
	public void handleMouseDblClick(Event event) {
		if (m_freeForm != null){
			drawHandles();
			// we are done with initial creation so set the edit tool
			IShapeTool tool = new FreeXEditTool(m_editor);
			tool.setShape((IShape)m_freeForm);
			m_editor.setShapeTool(tool);
			m_editor.renderObjects();
			drawHandles();
			m_freeForm = null;
		}
	}
}
