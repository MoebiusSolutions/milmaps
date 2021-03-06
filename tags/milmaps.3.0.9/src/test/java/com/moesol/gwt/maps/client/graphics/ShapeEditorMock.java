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

import com.google.gwt.user.client.Event;

public class ShapeEditorMock implements IShapeEditor{
	IShapeTool m_shapeTool; 
	//IAnchorTool m_anchorTool = null;
	List<IShape> m_objs = new ArrayList<IShape>();
	
	private ICoordConverter m_conv;
	
	ICanvasTool m_canvas = new CanvasToolMock();

	@Override
	public void setEventFocus(boolean on) {
	}

	@Override
	public ICanvasTool getCanvasTool() {
		return m_canvas;
	}

	@Override
	public void addShape(IShape shape) {
		m_objs.add(shape);
	}
	
	@Override
	public void removeShape(IShape shape) {
		int size = m_objs.size();
		for (int i = size-1; i > -1; i--){
			IShape s = m_objs.get(i);
			if (s == shape){
				m_objs.remove(i);
			}
		}
	}

	@Override
	public void removeShape(String id) {
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
	public void setShapeTool(IShapeTool shape) {
		m_shapeTool = shape;
	}
	
	@Override
	public IShapeTool getShapeTool() {
		return m_shapeTool;
	}

	@Override
	public void setAnchorTool(IAnchorTool tool) {
	}

	@Override
	public void clearActiveTool() {
	}

	@Override
	public List<IShape> getShapes() {
		return m_objs;
	}

	@Override
	public IShape findById(String id) {
		return null;
	}

	@Override
	public void setCoordConverter(ICoordConverter converter) {
		m_conv = converter;
	}

	@Override
	public ICoordConverter getCoordinateConverter() {
		return m_conv;
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
	public IShapeEditor clearCanvas() {
		return this;
	}

	@Override
	public IShapeEditor clearExistingObjs() {
		return this;
	}

	@Override
	public IShapeEditor renderObjects() {
		return this;
	}

	@Override
	public void onEventPreview(Event event) {
	}

	@Override
	public void done() {
	}

	@Override
	public void deleteSelectedShapes() {
	}

	@Override
	public IGraphicChanged addGraphicChangedHandler(IGraphicChanged handler) {
		// TODO Auto-generated method stub
		return handler;
	}

	@Override
	public void removeGraphicChangedHandler(IGraphicChanged handler) {
		// TODO Auto-generated method stub
		
	}
}
