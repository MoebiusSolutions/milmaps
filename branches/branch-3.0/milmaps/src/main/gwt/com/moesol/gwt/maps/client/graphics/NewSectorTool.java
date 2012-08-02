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

public class NewSectorTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private Canvas m_canvas = null;
	private Sector m_sector = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewSectorTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		Context2d context = m_canvas.getContext2d();
		//m_circle.erase(context);
		m_sector.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_sector = (Sector)shape;
	}

	@Override
	public IShape getShape() {
		return m_sector;
	}

	@Override
	public void handleMouseDown(Event event) {
		m_mouseDown = true;
		int x = event.getClientX();
		int y = event.getClientY();
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_sector = new Sector().withCenter(center);
		m_editor.addShape(m_sector);
		m_sector.selected(true);
		m_sector.setCoordConverter(m_editor.getCoordinateConverter());
		IAnchorTool tool = m_sector.getStartRngBrgAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(Event event) {
		if (m_mouseDown) {
			if (m_sector != null && m_canvas != null) {
				//m_sector.getStartBrgAnchorTool().handleMouseMove(event);
				m_sector.initialMouseMove(event);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(Event event) {
		m_mouseDown = false;
		m_sector.getStartRngBrgAnchorTool().handleMouseUp(event);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new EditSectorTool(m_editor);
		tool.setShape((IShape)m_sector);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_sector = null;
	}

	@Override
	public void handleMouseOut(Event event) {
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}
}
