package com.milmaps.client;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.moesol.gwt.maps.client.DragTracker;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.WorldCoords;

public class MapTouchController implements 
	TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler, 
	GestureStartHandler, GestureChangeHandler, GestureEndHandler
{
	private final MapView m_map;
	private DragTracker m_dragTracker;
	
	private HasText m_msg = new HasText() {
		@Override
		public void setText(String text) {
		}
		@Override
		public String getText() {
			return null;
		}
	};

	public MapTouchController(MapView map) {
		m_map = map;
	}

	public MapTouchController withMsg(HasText msg) {
		m_msg = msg;
		return this;
	}

	public void bindHandlers(FocusPanel touchPanel) {
		touchPanel.addTouchStartHandler(this);
		touchPanel.addTouchMoveHandler(this);
		touchPanel.addTouchEndHandler(this);
		touchPanel.addTouchCancelHandler(this);
		touchPanel.addGestureStartHandler(this);
		touchPanel.addGestureChangeHandler(this);
		touchPanel.addGestureEndHandler(this);
	}

	private void maybeDragMap(int x, int y) {
		if (m_dragTracker == null) {
			return; // Not dragging
		}
		WorldCoords newWorldCenter = m_dragTracker.update(x, y);
		if (m_dragTracker.isSameAsLast()) {
			return;
		}
		
		m_map.cancelAnimations();
		m_map.setWorldCenter(newWorldCenter);
		m_map.partialUpdateView();
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event) {
		m_msg.setText("ts: " + event.getTargetTouches().length());
		event.preventDefault();
		
		switch (event.getTargetTouches().length()) {
		case 1: {
			Touch touch = event.getTargetTouches().get(0);
			m_dragTracker = new DragTracker(
					touch.getPageX(), touch.getPageY(), m_map.getWorldCenter());
			break;
		}
		default:
			m_dragTracker = null;
		}
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		m_msg.setText("tm: " + event.getTargetTouches().length());
		event.preventDefault();
		
		switch (event.getTargetTouches().length()) {
		case 1: {
			Touch touch = event.getTargetTouches().get(0);
			maybeDragMap(touch.getPageX(), touch.getPageY());
			break;
		}
		default:
			m_dragTracker = null;
		}
	}
	
	@Override
	public void onTouchEnd(TouchEndEvent event) {
		m_msg.setText("te: " + event.getTargetTouches().length());
		event.preventDefault();
		if (m_dragTracker != null) {
			m_dragTracker = null;
			m_map.dumbUpdateView();
		}
	}
	
	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		m_msg.setText("tc: " + event.getTargetTouches().length());
		event.preventDefault();
		if (m_dragTracker != null) {
			m_dragTracker = null;
			m_map.dumbUpdateView();
		}
	}

	@Override
	public void onGestureStart(GestureStartEvent event) {
		m_msg.setText("gs: " + event.toDebugString());
		event.preventDefault();
		//
		// m_map.animateZoom(event.getScale());
	}

	@Override
	public void onGestureChange(GestureChangeEvent event) {
		m_msg.setText("gc: " + event.toDebugString());
		event.preventDefault();
		
		// m_map.animateZoom(event.getScale());
	}

	@Override
	public void onGestureEnd(GestureEndEvent event) {
		m_msg.setText("ge: " + event.toDebugString());
		event.preventDefault();
	}

}
