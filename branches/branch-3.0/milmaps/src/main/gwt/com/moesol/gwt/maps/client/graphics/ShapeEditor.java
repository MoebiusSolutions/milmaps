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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.IMapView;
import com.moesol.gwt.maps.client.MapView;

public class ShapeEditor implements IShapeEditor{
	//protected static final boolean PASS_EVENT = true;
	//protected static final boolean CAPTURE_EVENT = false;
	
	private IMapView m_map = null;
	private ICanvasTool m_canvas = null;//new CanvasTool();
	private IShapeTool m_shapeTool; 
	//IAnchorTool m_anchorTool = null;
	List<IGraphicChanged> m_handlers = new ArrayList<IGraphicChanged>();
	List<IShape> m_objs = new ArrayList<IShape>();
	ICoordConverter m_converter;

	public ICoordConverter getConverter() {
		return m_converter;
	}

	public ShapeEditor(IMapView map) {
		super();
		m_map = map;
		map.setShapeEditor(this);
		setCoordConverter(new Converter());
		m_converter.setViewPort(map.getViewport());
		m_canvas = map.getICanvasTool();
		map.attachCanvas();
	}
	/*
	public Canvas getCanvas() {
		return m_canvas.canvas();
	}
	*/
	private void checkForException() {
		if (m_canvas == null) {
			throw new IllegalStateException("ShapeEditor: m_canvas = null");
		}
		if (m_map == null) {
			throw new IllegalStateException("Shapeeditor: m_map = null");
		}
	}
	
	@Override
	public ICanvasTool getCanvasTool() {
		return m_canvas;
	}
	
	@Override
	public void setEventFocus(boolean focus){
		m_map.setEditorFocus(focus);
		
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
		//m_anchorTool = tool;
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
	public void deleteSelectedShapes(){
		int size = m_objs.size();
		for (int i = size-1; i > -1; i--){
			IShape s = m_objs.get(i);
			if (s.isSelected()){
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
		IContext context = m_canvas.getContext();
		int w = m_canvas.getOffsetWidth();
		int h = m_canvas.getOffsetHeight();
		context.clearRect(0, 0, w, h);	
		return (IShapeEditor)this;
	}
	
	@Override
	public IShapeEditor clearExistingObjs() {
		checkForException();
		if(!m_objs.isEmpty()){
			clearCanvas();
		}
		return (IShapeEditor)this;
	}

	@Override
	public IShapeEditor renderObjects() {
		checkForException();
		IContext context = m_canvas.getContext();
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

	//@Override
	public void done() {
		// TODO Auto-generated method stub
		
	}
	// Handler Events
	
	//@Override
	public boolean handleMouseDown(int x, int y) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseDown(x, y);
			return false;
		}
		return true;
	}

	//@Override
	public boolean handleMouseMove(int x, int y) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseMove(x, y);
			return false;
		}
		return true;
	}

	//@Override
	public boolean handleMouseUp(int x, int y) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseUp(x, y);
			return false;
		}
		return true;
	}

	//@Override
	public boolean handleMouseOut(int x, int y) {
		if (m_shapeTool != null){
			m_shapeTool.handleMouseOut(x, y);
			return false;
		}
		return true;
	}

	//@Override
	public boolean handleMouseDblClick(int x, int y) {
		if (m_shapeTool != null){
			
			m_shapeTool.handleMouseDblClick(x, y);
			return false;
		}
		return true;
	}
	
	void keyDownCode(int keyCode){
		//DOM.eventGetKeyCode(event) == KeyCodes.KEY_CTRL){
		m_shapeTool.handleKeyDown(keyCode);
	}
	
	void keyUpCode(int keyCode){
		m_shapeTool.handleKeyUp(keyCode);
	}

	@Override
	public void onEventPreview(Event event) {
		//DOM.eventPreventDefault(event);
		int x = event.getClientX();
		int y = event.getClientY();
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			handleMouseDown(x, y);
			break;
		case Event.ONMOUSEUP:
			handleMouseUp(x, y);
			break;
		case Event.ONMOUSEMOVE:
			handleMouseMove(x, y);
			break;
		case Event.ONMOUSEOUT:
			handleMouseOut(x, y);
			break;
		case Event.ONDBLCLICK:
			handleMouseDblClick(x, y);
			break;
		case Event.ONKEYDOWN:
			keyDownCode(event.getKeyCode());
			break;
		case Event.ONKEYUP:
			keyUpCode(event.getKeyCode());
			break;
		}
		//onHandlersEventPreview(event);
		return;
	}
	
	/*
	@Override
	public void onHandlersEventPreview(Event event) {
		//DOM.eventPreventDefault(event);
		int x = event.getClientX();
		int y = event.getClientY();
		int n = m_handlers.size();	
		if (n > 0){
			switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEDOWN:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleMouseDown(x, y);
				break;
			case Event.ONMOUSEUP:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleMouseUp(x, y);
				break;
			case Event.ONMOUSEMOVE:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleMouseMove(x, y);
				break;
			case Event.ONMOUSEOUT:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleMouseOut(x, y);
				break;
			case Event.ONDBLCLICK:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleMouseDblClick(x, y);
				break;
			case Event.ONKEYDOWN:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleKeyDown(event.getKeyCode());
				break;
			case Event.ONKEYUP:
				for (int i = 0; i < n; i++)
					m_handlers.get(i).handleKeyUp(event.getKeyCode());
				break;
			}
		}
		return;
	}
	*/
	@Override
	public void addGraphicChangedHandler(IGraphicChanged handler) {
		int n = m_handlers.size();
		boolean found = false;
		for (int i = 0; i < n; i++){
			if (m_handlers.get(i) == handler){
				found = true;
				break;
			}
		}
		if (!found){
			m_handlers.add(handler);
		}
	}

	@Override
	public void removeGraphicChangedHandler(IGraphicChanged handler) {
		int n = m_handlers.size();
		for (int i = n-1; i > -1; i--){
			if (m_handlers.get(i) == handler){
				m_handlers.remove(i);
			}
		}
	}
}
