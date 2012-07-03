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

	public static String[] m_objName = {"Arc", "Box", "Circle", "Ellipse"};
	
	public SelectShape(IShapeEditor editor) {
		m_editor = editor;
		m_convert = editor.getCoordinateConverter();
		m_objs = editor.getShapes();
	}
	
	private GeodeticCoords pixelToGeodetic(int x, int y){
		ViewCoords vc = new ViewCoords(x,y);
		return m_convert.viewToGeodetic(vc);
	}

    protected IShapeTool createShapeEditTool(IShape obj) {
    	String strShape = obj.id();
    	IShapeTool tool = null;
    	if( strShape.compareTo("Arc") == 0){
    		return null;
    	}
    	if( strShape.compareTo("Box") == 0){
    		return null;
    	}
    	if( strShape.compareTo("Circle") == 0){
    		tool = new EditCircleTool(m_editor);
    	}
    	if( strShape.compareTo("Ellipse") == 0){
    		tool = new EditEllipseTool(m_editor);
    	}
    	if( strShape.compareTo("Free Form") == 0){
    		tool = new EditFreeFormTool(m_editor);
    	}
    	if (tool != null){
    		tool.setShape(obj);
    	}
        return tool;
    }
    
    private boolean handleSelect(Event event){
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
			tool = createShapeEditTool(selectedShape);
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
		return CAPTURE_EVENT;
    }
	
	@Override
	public boolean handleMouseDown(Event event) {
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleMouseMove(Event event) {
		return PASS_EVENT;
	}

	@Override
	public boolean handleMouseUp(Event event) {
		return handleSelect(event);
	}

	@Override
	public boolean handleMouseOut(Event event) {
		return CAPTURE_EVENT;
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
	public boolean handleMouseDblClick(Event event) {
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleKeyDown(Event event) {
		int x = 0;
		return CAPTURE_EVENT;
	}

	@Override
	public boolean handleKeyUp(Event event) {
		return CAPTURE_EVENT;
	}
}
