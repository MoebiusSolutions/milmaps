/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

package com.moesol.gwt.maps.client.graphics;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;

public class ShapeEditor implements IShapeEditor{
	private MapView m_map = null;
	private CanvasTool m_canvas = null;//new CanvasTool();
	IActiveTool m_mapControl = null;
	IShapeTool m_shapeTool; 
	//IAnchorTool m_anchorTool = null;
	List<IShape> m_objs = new ArrayList<IShape>();
	ICoordConverter m_converter = new ShortDistConverter();

	public ICoordConverter getConverter() {
		return m_converter;
	}

	public ShapeEditor(MapView map) {
		super();
		m_map = map;
		m_map.setShapeEditor(this);
		m_converter.setMap(map);
		m_canvas = m_map.getDivManager().getCanvasTool();
		m_map.getViewPanel().add(m_canvas.canvas());
		m_mapControl = m_map.getController();
	}

	public Canvas getCanvas() {
		return m_canvas.canvas();
	}
	
	private void checkForException() {
		if (m_canvas == null) {
			throw new IllegalStateException("ShapeEditor: m_canvas = null");
		}
		if (m_mapControl == null) {
			throw new IllegalStateException("Shapeeditor: m_mapControl = null");
		}
		if (m_map == null) {
			throw new IllegalStateException("Shapeeditor: m_map = null");
		}
	}
	
	@Override
	public CanvasTool getCanvasTool() {
		return m_canvas;
	}
	
	@Override
	public void setEventFocus(boolean on){
		m_mapControl.setEditor((on ? this : null));
		
	}
	
	@Override
	public void setShapeTool(IShapeTool shape) {
		m_shapeTool = shape;
	}
	
	@Override
	public void setAnchorTool(IAnchorTool tool) {
		//m_anchorTool = tool;
	}

	@Override
	public void addShape(IShape shape) {
		m_objs.add(shape);
	}

	@Override
	public void removeShape(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAllShapes() {
		for (IShape obj : m_objs){
			obj.selected(true);
		}
	}

	@Override
	public void deselectAllShapes() {
		for (IShape obj : m_objs){
			obj.selected(false);
		}
	}

	@Override
	public void clearActiveTool() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IShape> getShapes() {
		return m_objs;
	}

	@Override
	public IShape findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IShapeEditor clearCanvas(){
		Context2d context = m_canvas.canvas().getContext2d();
		int w = m_canvas.canvas().getOffsetWidth();
		int h = m_canvas.canvas().getOffsetHeight();
		context.clearRect(0, 0, w, h);	
		return this;
	}
	
	@Override
	public IShapeEditor clearExistingObjs() {
		checkForException();
		if(!m_objs.isEmpty()){
			clearCanvas();
		}
		return this;
	}

	@Override
	public IShapeEditor renderObjects() {
		checkForException();
		Context2d context = m_canvas.canvas().getContext2d();
		for (IShape obj : m_objs){
			obj.render(context);
		}
		return this;
	}
	
	@Override
	public ICoordConverter getCoordinateConverter(){
		if (m_converter == null) {
			throw new IllegalStateException("ShapeEditor: m_converter = null");
		}
		return m_converter; 
	}
	
	@Override
	public void setCoordConverter(ICoordConverter converter) {
		m_converter = converter;
	}

	@Override
	public boolean needsUpdate() {
		if (m_shapeTool != null){
			IShape shape = m_shapeTool.getShape();
			if(shape != null){
				return shape.needsUpdate();
			}
		}
		return false;
	}

	@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}
	// Handler Events
	
	@Override
	public void handleMouseDown(MouseDownEvent event) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseDown(event);
		}
	}

	@Override
	public void handleMouseMove(MouseMoveEvent event) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseMove(event);
		}
	}

	@Override
	public void handleMouseUp(MouseUpEvent event) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseUp(event);
		}
	}

	@Override
	public void handleMouseOut(MouseOutEvent event) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseOut(event);
		}
	}
}
