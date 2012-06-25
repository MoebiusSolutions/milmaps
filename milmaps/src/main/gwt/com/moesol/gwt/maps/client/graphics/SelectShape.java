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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.EventHandler;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;

public class SelectShape implements IShapeTool{
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
    	if (tool != null){
    		tool.setShape(obj);
    	}
        return tool;
    }
    
    private void handleSelect(@SuppressWarnings("rawtypes") MouseEvent event){
		int x = event.getX();
		int y = event.getY();
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
			tool.hilite();
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
	public boolean handleMouseDown(MouseDownEvent event) {
		return true;
	}

	@Override
	public boolean handleMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleMouseUp(MouseUpEvent event) {
		handleSelect(event);
		return true;
	}

	@Override
	public boolean handleMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setShape(IShape shape) {
		// TODO Auto-generated method stub
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
