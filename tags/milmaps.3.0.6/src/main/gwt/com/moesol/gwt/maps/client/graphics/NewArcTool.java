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

public class NewArcTool extends  AbstractNewTool {
	private boolean m_mouseDown = false;
	private ICanvasTool m_canvas = null;
	private Arc m_arc = null;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;

	public NewArcTool(IShapeEditor editor) {
		m_editor = editor;
		m_canvas = editor.getCanvasTool();
		m_convert = editor.getCoordinateConverter();
		m_arc = new Arc();
	}
	
	private void drawHandles(){
		IContext context = m_canvas.getContext();
		//m_circle.erase(context);
		m_arc.drawHandles(context);
	}
	
	@Override
	public void setShape(IShape shape) {
		m_arc = (Arc)shape;
	}

	@Override
	public IShape getShape() {
		return m_arc;
	}

	@Override
	public void handleMouseDown(int x, int y) {
		m_mouseDown = true;
		ViewCoords vc = new ViewCoords(x, y);
		GeodeticCoords center = m_convert.viewToGeodetic(vc);
		m_arc.withCenter(center);
		m_editor.addShape(m_arc);
		m_arc.selected(true);
		m_arc.setCoordConverter(m_convert);
		IAnchorTool tool = m_arc.getStartBrgAnchorTool();
		m_editor.setAnchorTool(tool);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (m_mouseDown) {
			if (m_arc != null && m_canvas != null) {
				m_arc.initialMouseMove(x,y);
				m_editor.clearCanvas().renderObjects();
				drawHandles();
			}
		}
	}

	@Override
	public void handleMouseUp(int x, int y) {
		m_mouseDown = false;
		m_arc.getStartBrgAnchorTool().handleMouseUp(x,y);
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
	public void handleMouseOut(int x, int y) {
	}

	@Override
	public void done() {
		m_editor.setShapeTool(null);
	}
}
