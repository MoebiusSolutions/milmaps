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
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.Distance;

public class NewBoxTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private Box m_box = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private static final RangeBearingS m_rb = new RangeBearingS();

	public NewBoxTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
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
		double disKm = m_rb.gcRangeFromTo(cenPos, smjPos)/4;
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_box.setSmnAxis(dis);
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
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
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_box != null && m_canvas != null) {
				m_box.getSmjAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				setSmnHandle();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_box.getSmjAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
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
