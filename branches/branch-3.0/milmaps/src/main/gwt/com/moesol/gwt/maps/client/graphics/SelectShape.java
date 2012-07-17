/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import java.util.List;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class SelectShape implements IShapeTool{
	protected static final boolean PASS_EVENT = true;
	protected static final boolean CAPTURE_EVENT = false;
	//private boolean m_mouseDown = false;
	private IShapeEditor m_editor = null;
	private ICoordConverter m_convert;
	List<IShape> m_objs = null;
	
	public SelectShape(IShapeEditor editor) {
		m_editor = editor;
		m_convert = editor.getCoordinateConverter();
		m_objs = editor.getShapes();
	}
	
	private GeodeticCoords pixelToGeodetic(int x, int y){
		ViewCoords vc = new ViewCoords(x,y);
		return m_convert.viewToGeodetic(vc);
	}
    
    private void handleSelect(Event event){
		int x = event.getClientX();
		int y = event.getClientY();
		Canvas canvas = m_editor.getCanvasTool().canvas();
		Context2d context = canvas.getContext2d();
		int width = canvas.getOffsetWidth();
		int height = canvas.getOffsetHeight();
		context.clearRect(0, 0, width, height);
		
		GeodeticCoords pos = pixelToGeodetic(x,y);
		IShape selectedShape = null;
		IShapeTool tool = null;
		for (IShape obj : m_objs) {
			if ( obj.positionTouches(pos) ){
				selectedShape = obj;
				break;
			}
		}
		if (selectedShape != null){
			selectedShape.selected(true);
			tool = selectedShape.createEditTool(m_editor);
			m_editor.setShapeTool(tool);
			if (tool != null){
				tool.hilite();
			}
			for (IShape obj : m_objs) {
				if ( obj != selectedShape ){
					obj.selected(false).render(context);
				}
			} 
		}
		else{
			m_editor.deselectAllShapes();
			m_editor.clearCanvas().renderObjects();
		}  	
    }
	
	@Override
	public void handleMouseDown(Event event) {
	}

	@Override
	public void handleMouseMove(Event event) {
	}

	@Override
	public void handleMouseUp(Event event) {
		handleSelect(event);
	}

	@Override
	public void handleMouseOut(Event event) {
	}
	
	@Override
	public String getType() {
		return null;
	}

	@Override
	public IShape getShape() {
		return null;
	}

	@Override
	public void done() {
	}

	@Override
	public void setShape(IShape shape) {
	}

	@Override
	public void setAnchor(IAnchorTool anchor) {
	}

	@Override
	public void hilite() {
	}

	@Override
	public void handleMouseDblClick(Event event) {
	}

	@Override
	public void handleKeyDown(Event event) {
		int x = 0;
	}

	@Override
	public void handleKeyUp(Event event) {
	}
}
