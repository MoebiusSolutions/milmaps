/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.controls.HoverEvent;
import com.moesol.gwt.maps.client.controls.HoverHandler;
import com.moesol.gwt.maps.client.events.MapViewChangeEvent;
import com.moesol.gwt.maps.client.graphics.IActiveTool;
import com.moesol.gwt.maps.client.graphics.IShapeEditor;
import com.moesol.gwt.maps.client.stats.MapStateDialog;
import com.moesol.gwt.maps.client.stats.StatsDialogBox;
import com.moesol.gwt.maps.client.touch.*;
import com.moesol.gwt.maps.client.touch.TouchCancelEvent;
import com.moesol.gwt.maps.client.touch.TouchCancelHandler;
import com.moesol.gwt.maps.client.touch.TouchEndEvent;
import com.moesol.gwt.maps.client.touch.TouchEndHandler;
import com.moesol.gwt.maps.client.touch.TouchMoveEvent;
import com.moesol.gwt.maps.client.touch.TouchMoveHandler;
import com.moesol.gwt.maps.client.touch.TouchStartEvent;
import com.moesol.gwt.maps.client.touch.TouchStartHandler;
import com.moesol.gwt.maps.client.units.AngleUnit;
public class MapController implements 
	HasHandlers, IActiveTool,
	MouseMoveHandler, MouseDownHandler, MouseUpHandler, MouseOutHandler,
	MouseWheelHandler, EventPreview, KeyDownHandler, KeyUpHandler, KeyPressHandler,
	TouchStartHandler, TouchMoveHandler, TouchEndHandler, TouchCancelHandler
{
	private static boolean s_previewInstalled = false;
	private static final int S_KEY = 83;//(int)'s';
	private static final int F_KEY = 70;//(int)'f';
	private final MapView m_map;
	private final DoubleClickTracker m_doubleClickTracker = new DoubleClickTracker();
	private final MouseWheelTracker m_mouseWheelTracker = new MouseWheelTracker();
	private DragTracker m_dragTracker;
	private int m_wheelAccum = 0;
	private int m_keyVelocity = 1;
	private boolean m_bUseDragTracker = true;
	private boolean b_altKeyDown = false;
	WorldCoords m_wc = new WorldCoords();
	IShapeEditor m_editor = null;
	
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
			m_eventBus.fireEvent(new HoverEvent()
				.withX(m_mouseWheelTracker.getViewCoordinates().getX())
				.withY(m_mouseWheelTracker.getViewCoordinates().getY())
				.withClientX(m_moveClientX)
				.withClientY(m_moveClientY)
				);
		}};
	private int m_hoverDelayMillis = 500;
	private final EventBus m_eventBus;

	public MapController(MapView map, final EventBus eventBus) {
		m_map = map;
		m_eventBus = eventBus;
		
		if (!s_previewInstalled) {
			s_previewInstalled = true;
			DOM.addEventPreview(this);
		}
	}
	
	@Override
	public void setEditor(IShapeEditor shapeEditor) {
		m_editor = shapeEditor;
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
		focusPanel.addMouseWheelHandler(this);
		focusPanel.addKeyDownHandler(this);
		focusPanel.addKeyUpHandler(this);
		focusPanel.addKeyPressHandler(this);
	}

	public void zoomAndCenter(int x, int y, boolean bZoomIn) {
		m_map.zoomToNextLevelOnPixel(x,y,bZoomIn);

	}

	public void setUseDragTracker( boolean bUseDragTracker ){
		m_bUseDragTracker = bUseDragTracker;
	}
	
	private void altKeyDown(Event event){
		if (DOM.eventGetType(event) == Event.ONKEYDOWN){
			if (event.getKeyCode() == KeyCodes.KEY_ALT){
				b_altKeyDown = true;
			}
		}
		else if (DOM.eventGetType(event) == Event.ONKEYUP){
			if (event.getKeyCode() == KeyCodes.KEY_ALT){
				b_altKeyDown = false;
			}
		}
	}
	
	private boolean blockEvent(){
		return (m_editor != null && b_altKeyDown == false);
	}
	
	

	@Override
	public void onMouseDown(MouseDownEvent event) {
		if (blockEvent()){
			return;
		}
		Widget sender = (Widget) event.getSource();
		DOM.setCapture(sender.getElement());
		int x = event.getX();
		int y = event.getY();
		boolean bMouseDown = m_doubleClickTracker.onMouseDown(x, y);
		if (bMouseDown) {
			// If the browser will not generate double-click event previews
			// we could call zoomAndCenter here, but both Firefox and IE7 work
			// See event preview code.
			// zoomAndCenter(x, y, true);
			m_dragTracker = null;
		} else if(m_bUseDragTracker){
			m_dragTracker = new DragTracker(x, y, m_map.getWorldCenter());
		}
	}
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (blockEvent()){
			return;
		}
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
		m_hoverTimer.cancel();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (blockEvent()){
			return;
		}
		Widget sender = (Widget) event.getSource();
		DOM.releaseCapture(sender.getElement());
		m_hoverTimer.cancel();
		try {
			int x = event.getX();
			int y = event.getY();
			maybeDragMap(x, y);
			if (m_dragTracker != null) {
				if ( m_dragTracker.mouseDownMoved()){
					m_map.dumbUpdateView();
				}
			}
			m_map.setFocus(true);
		} finally {
			m_dragTracker = null;
		}
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
	public void onMouseWheel(final MouseWheelEvent event) {
		m_wheelAccum += event.getDeltaY();
//		int jumpPoint = 8;

		ViewCoords vCoords = m_mouseWheelTracker.getViewCoordinates();
		//	DJD NOTE: removed centering of the map when we zoom with wheel, beacuse
		// its just strange. Most applications when using a mouse wheel, the cursor pos is irrelevant
		// For example, most image editing software / IE / etc they do something to the entire image, where the 
		// cursor is when this is done does not mean anything.

		if (event.getDeltaY() < 0) {
			m_wheelAccum = 0;
			zoomAndCenter(vCoords.getX(), vCoords.getY(), true);
		} else if (event.getDeltaY() > 0) {
			m_wheelAccum = 0;
			zoomAndCenter(vCoords.getX(), vCoords.getY(), false);
		}
		//MapViewChangeEvent.fire(m_eventBus, m_map);
                
        // Don't let containing elements in the browser catch the same scroll wheel event
        DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
	}

	@Override
	public boolean onEventPreview(Event event) {
		altKeyDown(event);
		if (blockEvent()){
			m_editor.onEventPreview(event);
		}
		Element target = DOM.eventGetTarget(event);
		if (target != null) {
			if (!DOM.isOrHasChild(m_map.getElement(), target)) {
				return true;
			}
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
			if (!blockEvent()){		
				zoomAndCenter(m_doubleClickTracker.getX(), 
						  	m_doubleClickTracker.getY(), true);
			}
			DOM.eventPreventDefault(event);
			break;
		case Event.ONKEYDOWN:
		case Event.ONKEYUP:
			switch (DOM.eventGetKeyCode(event)) {
			case KeyCodes.KEY_LEFT:
			case KeyCodes.KEY_RIGHT:
			case KeyCodes.KEY_UP:
			case KeyCodes.KEY_DOWN:
				DOM.eventPreventDefault(event);
				break;
			}
			break;
		}
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (event.isControlKeyDown()) {
			onKeyDownWithControl(event.getNativeKeyCode());
		} else {
			onKeyDownNoModifiers(event.getNativeKeyCode());
		}
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		switch (event.getCharCode()) {
		case '\\':
			new StatsDialogBox().show();
			break;
		}
	}

	private void onKeyDownWithControl(int keyCode) {
		switch (keyCode) {
		case KeyCodes.KEY_UP:
			m_map.animateZoomToNextLevel(true);
			break;
		case KeyCodes.KEY_DOWN:
			m_map.animateZoomToNextLevel(false);
			break;
		}
	}

	private void onKeyDownNoModifiers(int keyCode) {
		switch (keyCode) {
		case KeyCodes.KEY_LEFT:
			moveMap(-1, 0);
			break;
		case KeyCodes.KEY_RIGHT:
			moveMap(1, 0);
			break;
		case KeyCodes.KEY_UP:
			moveMap(0, 1);
			break;
		case KeyCodes.KEY_DOWN:
			moveMap(0, -1);
			break;
		case F_KEY:
			m_map.fullUpdateView();
			break;
		case S_KEY:
			new MapStateDialog(m_map).show();
			break;	
		}
		if (m_keyVelocity < 64) {
			m_keyVelocity += 1;
		}
	}

	private void moveMap(int directionX, int directionY) {
		int xdist = m_keyVelocity;
		int ydist = m_keyVelocity;
		WorldCoords wc = m_map.getWorldCenter();
		wc = wc.translate(directionX * xdist, directionY * ydist);
		
		m_map.cancelAnimations();
		m_map.setWorldCenter(wc);
		m_map.updateView();
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		m_keyVelocity = 1;
	}


	public void addHoverHandler(HoverHandler h) {
		m_eventBus.addHandler(HoverEvent.getType(), h);
	}
	@Override
	public void fireEvent(GwtEvent<?> event) {
		m_eventBus.fireEvent(event);
	}

	private Timer m_viewChangeTimer = null;
	private GeodeticCoords m_oldCenter = new GeodeticCoords(0,0, AngleUnit.DEGREES);
	private ViewDimension m_oldViewSize = new ViewDimension(0, 0);
	private double m_oldScale = 0.0;

	// TODO move this somewhere else???
	public void fireMapViewChangeEventWithMinElapsedInterval(final int minEventFireIntervalMillis) {
		if (m_viewChangeTimer != null) {
			m_viewChangeTimer.cancel();
		}
		m_viewChangeTimer = new Timer() {
			@Override
			public void run() {
				final IProjection newProjection = m_map.getProjection();
				ViewPort viewport = m_map.getViewport();
				final GeodeticCoords newCenter = viewport.getVpWorker().getGeoCenter();

				if (m_oldCenter.equals(newCenter)) {
					if (m_oldViewSize.equals(viewport.getVpWorker().getDimension())) {
						if (m_oldScale == newProjection.getEquatorialScale()) {
							m_map.onIdle();
							return; // Nothing has changed.
						}
					}
				}

				m_oldCenter = newCenter;
				m_oldViewSize = viewport.getVpWorker().getDimension();
				m_oldScale = newProjection.getEquatorialScale();
				
				MapViewChangeEvent.fire(m_eventBus, m_map);
				
				m_map.onChangeAndIdle();
			}
		};
		m_viewChangeTimer.schedule(minEventFireIntervalMillis);
	}
}
