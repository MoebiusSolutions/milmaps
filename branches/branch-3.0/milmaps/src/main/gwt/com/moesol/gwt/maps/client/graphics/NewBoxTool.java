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
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;

public class NewBoxTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private Canvas m_canvas = null;
	private Box m_box = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private static final RangeBearingS m_rb = new RangeBearingS();

	public NewBoxTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		m_box.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_box = (Box)shape;
	}

	@Override
	public IShape getShape() {
		return m_box;
	}
	
	private void setSmnHandle(){
		// this initial value is a default.
		GeodeticCoords smjPos = m_box.getSmjPos();
		GeodeticCoords cenPos = m_box.getCenter();
		double disKm = m_rb.gcDistanceFromTo(cenPos, smjPos)/4;
		double brgDeg = m_rb.gcBearingFromTo(cenPos, smjPos);
		brgDeg = Func.wrap360(brgDeg-90);
		GeodeticCoords smnPos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		m_box.getSmnAnchorTool();
		m_box.setSmnAxis(smnPos);
	}

	@Override
	public void handleMouseDown(Event event) {
		m_mouseDown = true;
		int x = event.getClientX();
		int y = event.getClientY();
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_box = new Box().withCenter(center);
		m_editor.addShape(m_box);
		m_box.selected(true);
		m_box.setCoordConverter(m_editor.getCoordinateConverter());
		m_box.getSmnAnchorTool();
		IAnchorTool tool = m_box.getSmjAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(Event event) {
		if (m_mouseDown) {
			if (m_box != null && m_canvas != null) {
				m_box.getSmjAnchorTool().handleMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				setSmnHandle();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(Event event) {
		m_mouseDown = false;
		m_box.getSmjAnchorTool().handleMouseUp(event);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new EditBoxTool(m_editor);
		tool.setShape((IShape)m_box);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_box = null;
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		// TODO Auto-generated method stub
	}
}
