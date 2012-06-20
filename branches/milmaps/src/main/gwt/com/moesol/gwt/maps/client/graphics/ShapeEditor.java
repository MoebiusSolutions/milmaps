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
import com.moesol.gwt.maps.client.ViewDimension;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;

public class ShapeEditor implements IShapeEditor, ICoordConverter {
	private MapView m_map = null;
	private CanvasTool m_canvas = null;//new CanvasTool();
	IActiveTool m_mapControl = null;
	IShapeTool m_shapeTool; 
	//IAnchorTool m_anchorTool = null;
	List<IShape> m_objs = new ArrayList<IShape>();

	public ShapeEditor(MapView mapView) {
		super();
		m_map = mapView;
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
	
	private void refreshCanvas(boolean erase, boolean show){
		// TODO we may want to remove the check for empty.
		if(erase || show){
			checkForException();
			Context2d context = m_canvas.canvas().getContext2d();
			if(erase){
				int w = m_canvas.canvas().getOffsetWidth();
				int h = m_canvas.canvas().getOffsetHeight();
				context.clearRect(0, 0, w, h);
			}
			if(show){
				for (IShape obj : m_objs){
					if(obj.isSelected() == false){
						obj.render(context);
					}
				}
			}
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
		if(shape != null && shape.getType() != "SelectTool"){
			deselectAllShapes();
		}
		m_shapeTool= shape;
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
	public void updateCanvas(boolean erase, boolean show) {
		refreshCanvas(erase,show);
	}
	
	@Override
	public ICoordConverter getCoordinateConverter(){
		return (ICoordConverter)this; 
	}
	
	// Coordinate conversion
	@Override
	public ViewCoords geodeticToView(GeodeticCoords gc) {
		ViewWorker vw = m_map.getViewport().getVpWorker();
		return vw.geodeticToView(gc);
	}

	@Override
	public GeodeticCoords viewToGeodetic(ViewCoords vc) {
		ViewWorker vw = m_map.getViewport().getVpWorker();
		WorldCoords wc = vw.viewToWorld(vc);
		IProjection proj = m_map.getProjection();
		return proj.worldToGeodetic(wc);
	}

	@Override
	public WorldCoords geodeticToWorld(GeodeticCoords gc) {
		IProjection proj = m_map.getProjection();
		return proj.geodeticToWorld(gc);
	}

	@Override
	public GeodeticCoords worldToGeodetic(WorldCoords wc) {
		IProjection proj = m_map.getProjection();
		return proj.worldToGeodetic(wc);
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
	public boolean handleMouseDown(MouseDownEvent event) {
		if (m_shapeTool != null){
			return m_shapeTool.handleMouseDown(event);
		}
		return false;
	}

	@Override
	public boolean handleMouseMove(MouseMoveEvent event) {
		if (m_shapeTool != null){
			return m_shapeTool.handleMouseMove(event);
		}
		return false;
	}

	@Override
	public boolean handleMouseUp(MouseUpEvent event) {
		if (m_shapeTool != null){
			return m_shapeTool.handleMouseUp(event);
		}
		return false;
	}

	@Override
	public boolean handleMouseOut(MouseOutEvent event) {
		if (m_shapeTool != null){
			return m_shapeTool.handleMouseOut(event);
		}
		return false;
	}
}
