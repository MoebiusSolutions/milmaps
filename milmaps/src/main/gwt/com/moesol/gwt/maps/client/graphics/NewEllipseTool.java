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

public class NewEllipseTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private Canvas m_canvas = null;
	private Ellipse m_ellipse = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private static final RangeBearingS m_rb = new RangeBearingS();

	public NewEllipseTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		m_ellipse.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_ellipse = (Ellipse)shape;
	}

	@Override
	public IShape getShape() {
		return m_ellipse;
	}
	
	private void setSmnHandle(){
		// this initial value is a default.
		GeodeticCoords smjPos = m_ellipse.getSmjPos();
		GeodeticCoords cenPos = m_ellipse.getCenter();
		double disKm = m_rb.gcDistanceFromTo(cenPos, smjPos)/4;
		double brgDeg = m_rb.gcBearingFromTo(cenPos, smjPos);
		brgDeg = Func.wrap360(brgDeg-90);
		GeodeticCoords smnPos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		m_ellipse.getSmnAnchorTool();
		m_ellipse.setSmnAxis(smnPos);
	}

	@Override
	public boolean handleMouseDown(Event event) {
		m_mouseDown = true;
		int x = event.getClientX();
		int y = event.getClientY();
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_ellipse = new Ellipse().withCenter(center);
		m_editor.addShape(m_ellipse);
		m_ellipse.selected(true);
		m_ellipse.setCoordConverter(m_editor.getCoordinateConverter());
		m_ellipse.getSmnAnchorTool();
		IAnchorTool tool = m_ellipse.getSmjAnchorTool();
		m_editor.setAnchorTool(tool);
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleMouseMove(Event event) {
		if (m_mouseDown) {
			if (m_ellipse != null && m_canvas != null) {
				m_ellipse.getSmjAnchorTool().handleMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				setSmnHandle();
				drawHandles();
			}
		}
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleMouseUp(Event event) {
		m_mouseDown = false;
		m_ellipse.getSmjAnchorTool().handleMouseUp(event);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new EditEllipseTool(m_editor);
		tool.setShape((IShape)m_ellipse);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_ellipse = null;
		return CAPTURE_EVENT;
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}

	@Override
	public String getType() {
		return "new_circle_tool";
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
		// TODO Auto-generated method stub
	}
}
