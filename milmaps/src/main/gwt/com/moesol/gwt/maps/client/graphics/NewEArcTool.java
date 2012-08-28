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
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;

public class NewEArcTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private EArc m_arc = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	private static final RangeBearingS m_rb = new RangeBearingS();

	public NewEArcTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		m_arc.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_arc = (EArc)shape;
	}

	@Override
	public IShape getShape() {
		return m_arc;
	}
	
	private void setSmnHandle(){
		// this initial value is a default.
		GeodeticCoords smjPos = m_arc.getSmjPos();
		GeodeticCoords cenPos = m_arc.getCenter();
		double disKm = m_rb.gcRangeFromTo(cenPos, smjPos)/4;
		double brgDeg = m_rb.gcBearingFromTo(cenPos, smjPos);
		brgDeg = Func.wrap360(brgDeg-90);
		GeodeticCoords smnPos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		m_arc.getSmnAnchorTool();
		m_arc.setSmnAxis(smnPos);
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_arc = new EArc().withCenter(center);
		m_editor.addShape(m_arc);
		m_arc.selected(true);
		m_arc.setCoordConverter(m_editor.getCoordinateConverter());
		m_arc.getSmnAnchorTool();
		IAnchorTool tool = m_arc.getSmjAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_arc != null && m_canvas != null) {
				m_arc.getSmjAnchorTool().handleMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				setSmnHandle();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_arc.getSmjAnchorTool().handleMouseUp(x,y);
		//drawCenterHandle();
		// we are done with initial creation so set the edit tool
		IShapeTool tool = new CommonEditTool(m_editor);
		tool.setShape((IShape)m_arc);
		m_editor.setShapeTool(tool);
		m_editor.renderObjects();
		drawHandles();
		m_arc = null;
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
