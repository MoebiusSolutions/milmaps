package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.MouseWheelListener;
import com.google.gwt.user.client.ui.MouseWheelVelocity;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.controls.HoverEvent;
import com.moesol.gwt.maps.client.controls.HoverHandler;
import com.moesol.gwt.maps.client.touch.Touch;
import com.moesol.gwt.maps.client.touch.TouchCancelEvent;
import com.moesol.gwt.maps.client.touch.TouchCancelHandler;
import com.moesol.gwt.maps.client.touch.TouchEndEvent;
import com.moesol.gwt.maps.client.touch.TouchEndHandler;
import com.moesol.gwt.maps.client.touch.TouchMoveEvent;
import com.moesol.gwt.maps.client.touch.TouchMoveHandler;
import com.moesol.gwt.maps.client.touch.TouchStartEvent;
import com.moesol.gwt.maps.client.touch.TouchStartHandler;

public class MapController implements 
	HasHandlers,
	MouseMoveHandler, MouseDownHandler, MouseUpHandler, MouseOutHandler,
	MouseWheelListener, EventPreview, KeyboardListener, 
	TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler  
{
	private static boolean s_previewInstalled = false;
	private final MapView m_map;
	private final DoubleClickTracker m_doubleClickTracker = new DoubleClickTracker();
	private final MouseWheelTracker m_mouseWheelTracker = new MouseWheelTracker();
	private DragTracker m_dragTracker;
	private int m_wheelAccum = 0;
	private int m_keyVelocity = 1;
	private boolean m_bUseDragTracker = true;
	private HasText m_msg = new HasText() {
		@Override
		public void setText(String text) {
		}
		@Override
		public String getText() {
			return null;
		}
	};
	private int m_moveClientX;
	private int m_moveClientY;
	private Timer m_hoverTimer = new Timer() {
		@Override
		public void run() {
			m_handlerManager.fireEvent(new HoverEvent()
				.withX(m_mouseWheelTracker.m_x)
				.withY(m_mouseWheelTracker.m_y)
				.withClientX(m_moveClientX)
				.withClientY(m_moveClientY)
				);
		}};
	private int m_hoverDelayMillis = 500;
	private final HandlerManager m_handlerManager = new HandlerManager(this);
	
	public MapController(MapView map) {
		m_map = map;
		
		if (!s_previewInstalled) {
			s_previewInstalled = true;
			DOM.addEventPreview(this);
		}
	}
	
	public MapController withMsg(HasText msg) {
		m_msg = msg;
		return this;
	}
	
	public void bindHandlers(FocusPanel focusPanel) {
		focusPanel.addMouseMoveHandler(this);
		focusPanel.addMouseDownHandler(this);
		focusPanel.addMouseUpHandler(this);
		focusPanel.addMouseOutHandler(this);
		focusPanel.addMouseWheelListener(this);
		focusPanel.addKeyboardListener(this);
	}
	
	public void zoomAndCenter(int x, int y, boolean bZoomIn) {
		double zoomFactor = (bZoomIn ? 2.0 : 0.5 );
		m_map.zoomOnPixel(x,y,zoomFactor);
		
	}
	
	public void setUseDragTracker( boolean bUseDragTracker ){
		m_bUseDragTracker = bUseDragTracker;
	}

	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		Widget sender = (Widget) event.getSource();
		int x = event.getX();
		int y = event.getY();
		DOM.setCapture(sender.getElement());
		boolean bMouseDown = m_doubleClickTracker.onMouseDown(x, y);
		if (bMouseDown) {
			// If the browser will not generate double-click event previews
			// we could call zoomAndCenter here, but both Firefox and IE7 work
			// See event preview code.
			// zoomAndCenter(x, y, true);
			m_dragTracker = null;
		} else if( m_bUseDragTracker ){
			m_dragTracker = new DragTracker(x, y, m_map.getWorldCenter());
		}
	}
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		int x = event.getX();
		int y = event.getY();
		m_moveClientX = event.getClientX();
		m_moveClientY = event.getClientY();
		m_mouseWheelTracker.update(x, y);
		maybeDragMap(x, y);
		maybeHover(event);
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		m_hoverTimer .cancel();
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		m_hoverTimer.cancel();
		Widget sender = (Widget) event.getSource();
		int x = event.getX();
		int y = event.getY();
		DOM.releaseCapture(sender.getElement());
		
		try {
			maybeDragMap(x, y);
			m_map.setFocus(true);
		} finally {
			m_dragTracker = null;
		}
	}

	private void maybeDragMap(int x, int y) {
		if (m_dragTracker == null) {
			// Not dragging
			return;
		}
		WorldCoords newWorldCenter = m_dragTracker.update(x, y);
		if (m_dragTracker.isSameAsLast()) {
			return;
		}
		m_map.setWorldCenter(newWorldCenter);
		m_map.updateView();
	}
	private void maybeHover(MouseMoveEvent event) {
		m_hoverTimer .cancel();
		if (m_dragTracker != null) {
			// We are dragging
			return;
		}
		m_hoverTimer.schedule(getHoverDelayMillis());
	}
	
	public int getHoverDelayMillis() {
		return m_hoverDelayMillis;
	}
	public MapController withHoverDelayMillis(int v) {
		m_hoverDelayMillis = v;
		return this;
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		m_msg.setText("s: " + event.touches().length());
		if (event.touches().length() != 1) {
			m_dragTracker = null;
		} else {
			event.preventDefault();
			Touch touch = event.touches().get(0);
			m_dragTracker = new DragTracker(touch.pageX(), touch.pageY(), m_map.getWorldCenter());
		}
	}
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		m_msg.setText("m: " + event.touches().length());
		if (event.touches().length() != 1) {
			return;
		}
		event.preventDefault();
		Touch touch = event.touches().get(0);
		maybeDragMap(touch.pageX(), touch.pageY());
	}
	@Override
	public void onTouchEnd(TouchEndEvent event) {
		m_dragTracker = null;
	}
	@Override
	public void onTouchCancel(TouchCancelEvent event) {
		m_dragTracker = null;
	}

	
	@Override
	public void onMouseWheel(Widget sender, MouseWheelVelocity velocity) {
		m_wheelAccum += velocity.getDeltaY();
		int jumpPoint = 8;
		
		ViewCoords vCoords = m_mouseWheelTracker.getViewCoordinates();
		//	DJD NOTE: removed centering of the map when we zoom with wheel, beacuse
		// its just strange. Most applications when using a mouse wheel, the cursor pos is irrelevant
		// For example, most image editing software / IE / etc they do something to the entire image, where the 
		// cursor is when this is done does not mean anything.
		
		if (velocity.getDeltaY() < 0) {
			m_wheelAccum = 0;
			zoomAndCenter(vCoords.getX(), vCoords.getY(), true);
		} else if (velocity.getDeltaY() > 0) {
			m_wheelAccum = 0;
			zoomAndCenter(vCoords.getX(), vCoords.getY(), false);
		}
	}

	@Override
	public boolean onEventPreview(Event event) {

//		if (m_map.getMapControls().handleEventPreview(event)) {
//			return false;
//		}
		
		if (!DOM.isOrHasChild(m_map.getElement(), DOM.eventGetTarget(event))) {
			return true;
		}
		
		onEventPreviewForMap(event);

		// Allow the event to file
		return true;
	}

	private void onEventPreviewForMap(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
		// case Event.ONMOUSEWHEEL: See comment on onMouseWheel.
			DOM.eventPreventDefault(event);
			break;
			
		case Event.ONDBLCLICK:
			zoomAndCenter(m_doubleClickTracker.getX(), m_doubleClickTracker.getY(), true);
			DOM.eventPreventDefault(event);
			break;
			
		case Event.ONKEYDOWN:
		case Event.ONKEYUP:
			switch (DOM.eventGetKeyCode(event)) {
			case KeyboardListener.KEY_LEFT:
			case KeyboardListener.KEY_RIGHT:
			case KeyboardListener.KEY_UP:
			case KeyboardListener.KEY_DOWN:
				DOM.eventPreventDefault(event);
				break;
			}
			break;
		}
	}

	@Override
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
		if ((KeyboardListener.MODIFIER_CTRL & modifiers) != 0) {
			onKeyDownWithControl(keyCode);
		} else {
			onKeyDownNoModifiers(keyCode);
		}
	}

	private void onKeyDownWithControl(char keyCode) {
		switch (keyCode) {
		case KeyboardListener.KEY_UP:
			m_map.animateZoom(2);
			//m_map.updateView();
			break;
		case KeyboardListener.KEY_DOWN:
			m_map.animateZoom(1/2.0);
			//m_map.updateView();
			break;
		}
	}

	private void onKeyDownNoModifiers(char keyCode) {
		switch (keyCode) {
		case KeyboardListener.KEY_LEFT:
			moveMap(-1, 0);
			break;
		case KeyboardListener.KEY_RIGHT:
			moveMap(1, 0);
			break;
		case KeyboardListener.KEY_UP:
			moveMap(0, 1);
			break;
		case KeyboardListener.KEY_DOWN:
			moveMap(0, -1);
			break;
		}
		if (m_keyVelocity < 64) {
			m_keyVelocity += 1;
		}
	}

	private void moveMap(int x, int y) {
		int xdist = m_keyVelocity;
		int ydist = m_keyVelocity;
		WorldCoords v = m_map.getWorldCenter();
		v.setX(v.getX() + xdist * x);
		v.setY(v.getY() + ydist * y);
		m_map.setWorldCenter(v);
		m_map.updateView();
	}

	@Override
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	}

	@Override
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		m_keyVelocity = 1;
	}

	public void addHoverHandler(HoverHandler h) {
		m_handlerManager.addHandler(HoverEvent.getType(), h);
	}
	@Override
	public void fireEvent(GwtEvent<?> event) {
		m_handlerManager.fireEvent(event);
	}

}
