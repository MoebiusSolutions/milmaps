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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class NewCircleTool implements IShapeTool {
	private boolean m_mouseDown = false;
	private Canvas m_canvas = null;
	private Circle m_circle = null;
	private final AnchorHandle m_radHandle = new AnchorHandle();
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewCircleTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool().canvas();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawCircle(){
		Context2d context = m_canvas.getContext2d();
		//m_circle.erase(context);
		m_circle.render(context);
		m_radHandle.draw(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_circle = (Circle)shape;
	}

	@Override
	public IShape getShape() {
		return m_circle;
	}

	@Override
	public boolean handleMouseDown(MouseDownEvent event) {
		m_mouseDown = true;
		int x = event.getX();
		int y = event.getY();
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_circle = new Circle().withCenter(center);
		m_circle.selected(true);
		m_circle.setCoordConverter(m_editor.getCoordinateConverter());
		IAnchorTool tool = m_circle.getRadiusAnchorTool();
		m_editor.setAnchorTool(tool);
		return true;
	}

	@Override
	public boolean handleMouseMove(MouseMoveEvent event) {
		if (m_mouseDown) {
			if (m_circle != null && m_canvas != null) {
				
				int x = event.getX();
				int y = event.getY();
				m_circle.getRadiusAnchorTool().handleMouseMove(event);
				m_radHandle.setCenter(x, y);
				m_editor.updateCanvas(true,true);
				drawCircle();
				return true;
			}
		}
		return false;
	}

	private boolean drawCenterHandle() {
		if (m_circle != null && m_canvas != null) {
			Context2d context = m_canvas.getContext2d();
			AnchorHandle handle = new AnchorHandle();
			GeodeticCoords gc = m_circle.getCenter();
			ViewCoords vc = m_convert.geodeticToView(gc);
			handle.setCenter(vc.getX(), vc.getY());
			handle.draw(context);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleMouseUp(MouseUpEvent event) {
		m_mouseDown = false;
		m_circle.getRadiusAnchorTool().handleMouseUp(event);
		drawCenterHandle();
		m_editor.addShape(m_circle);
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new EditCircleTool(m_editor);
		tool.setShape((IShape)m_circle);
		m_editor.setShapeTool(tool);
		m_editor.updateCanvas(false,true);
		drawCircle();
		m_circle = null;
		return true;
	}

	@Override
	public boolean handleMouseOut(MouseOutEvent event) {
		return false;
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

	@Override
	public void hilite() {
		// TODO Auto-generated method stub	
	}
}
