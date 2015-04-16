/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.MapView;

/**
 * Control for panning and zooming the map.
 */
public class MapPanZoomControl extends Composite {

	// encapsulates code testable with regular JUnit tests
	// TODO: move more testable code here.
	static class Presenter {
		double calculateDelta(int panButtonDimension, int eventRelativeCoord,
				int maxPanPixels) {
			final double halfPanButtonDimension = panButtonDimension / 2.0;

			return ((eventRelativeCoord / halfPanButtonDimension) - 1.0)
					* maxPanPixels;
		}
	}

	private static MapPanZoomControlUiBinder uiBinder = GWT
			.create(MapPanZoomControlUiBinder.class);

	interface MapPanZoomControlUiBinder extends
			UiBinder<Widget, MapPanZoomControl> {
	}

	private Presenter m_presenter = new Presenter();

	private MapView m_map;
	private boolean m_doingMapAction = false;

	private int m_maxPanPixels;
	private int m_millisBetweenConsecutiveActions;
	
	@UiField
	DivElement clickHighlight;

	@UiField
	HTML zoomInButton;

	@UiField
	HTML zoomOutButton;

	private Timer m_panButtonTimer = new Timer() {
		@Override
		public void run() {
			m_map.moveMapByPixels((int) m_dx, (int) m_dy);
		}
	};

	private Timer m_zoomInButtonTimer = new Timer() {
		@Override
		public void run() {
			m_map.zoomByFactor(1.05);
		}
	};

	private Timer m_zoomOutButtonTimer = new Timer() {
		@Override
		public void run() {
			// 0.952380952 = 1/1.05
			m_map.zoomByFactor(0.952380952);
		}
	};

	private boolean m_panning;
	
	/**
	 * @param map The map to plug this control into.
	 * @param maxPanPixels The maximum number of pixels to
	 * move the map each time a pan occurs. 
	 * @param millisBetweenPans The milliseconds between consecutive
	 * pans that occur while the mouse is down on the pan portion
	 * of the control.
	 */
	public void initPanVals( int maxPanPixels, int millisBetweenPans ) {
		m_maxPanPixels = maxPanPixels;
		m_millisBetweenConsecutiveActions = millisBetweenPans;	
	}
	
	public void setMapView( MapView map ){
		m_map = map;
	}
	
	public MapPanZoomControl( ) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * @param map The map to plug this control into.
	 * @param maxPanPixels The maximum number of pixels to
	 * move the map each time a pan occurs. 
	 * @param millisBetweenPans The milliseconds between consecutive
	 * pans that occur while the mouse is down on the pan portion
	 * of the control.
	 */
	public MapPanZoomControl(MapView map, int maxPanPixels, int millisBetweenPans) {
		m_map = map;
		m_maxPanPixels = maxPanPixels;
		m_millisBetweenConsecutiveActions = millisBetweenPans;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("panButton")
	public void onPanButtonMouseUp(MouseUpEvent e) {
		e.preventDefault();
		stopPanLoop();
	}

	@UiHandler("panButton")
	public void onPanButtonMouseOut(MouseOutEvent e) {
		e.preventDefault();
		stopPanLoop();
	}

	@UiHandler("panButton")
	public void onPanButtonMouseDown(MouseDownEvent e) {
		e.preventDefault();
		updateVelocity(e);
		startPanLoop();
	}

	@UiHandler("panButton")
	public void onPanButtonMouseMove(MouseMoveEvent e) {
		e.preventDefault();
		if (m_panning) {
			updateVelocity(e);
		}
	}

	@UiField
	HTML panButton;

	private double m_dx;

	private double m_dy;

	@SuppressWarnings("rawtypes")
	private void updateVelocity(MouseEvent e) {
		int eventRelativeX = e.getRelativeX(panButton.getElement());
		int eventRelativeY = e.getRelativeY(panButton.getElement());

		final int panButtonWidth = panButton.getOffsetWidth();
		final int panButtonHeight = panButton.getOffsetHeight();

		if (eventRelativeX > 0 && eventRelativeX < panButtonWidth
				&& eventRelativeY > 0 && eventRelativeY < panButtonHeight) {
			final Style clickHighlightStyle = clickHighlight.getStyle();
			clickHighlightStyle.setDisplay(Display.BLOCK);
			clickHighlightStyle.setLeft(
					eventRelativeX - (clickHighlight.getClientWidth() / 3),
					Unit.PX);
			clickHighlightStyle.setTop(
					eventRelativeY - (clickHighlight.getClientHeight() / 3),
					Unit.PX);

			m_dx = m_presenter.calculateDelta(panButtonWidth, eventRelativeX,
					m_maxPanPixels);
			m_dy = -m_presenter.calculateDelta(panButtonHeight, eventRelativeY,
					m_maxPanPixels);
		}
	}

	private void startPanLoop() {
		m_panning = true;
		m_panButtonTimer.run();
		m_panButtonTimer.scheduleRepeating(m_millisBetweenConsecutiveActions);
	}

	private void stopPanLoop() {
		final Style clickHighlightStyle = clickHighlight.getStyle();
		clickHighlightStyle.setDisplay(Display.NONE);
		m_panning = false;
		m_panButtonTimer.cancel();
	}

	@UiHandler("zoomInButton")
	public void onZoomInButtonMouseDown(MouseDownEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouse");
		startZoomLoop(m_zoomInButtonTimer);
	}

	private void startZoomLoop(Timer zoomTimer) {
		m_doingMapAction = true;
		zoomTimer.run();
		zoomTimer.scheduleRepeating(m_millisBetweenConsecutiveActions);
	}

	@UiHandler("zoomInButton")
	public void onZoomInButtonMouseUp(MouseUpEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouseOver");
		stopZoomLoop(m_zoomInButtonTimer);
	}

	private void stopZoomLoop(Timer zoomTimer) {
		zoomTimer.cancel();
		if ( m_doingMapAction ){
			m_doingMapAction = false;
			m_map.updateView();
		}
	}

	@UiHandler("zoomInButton")
	public void onZoomInButtonMouseOver(MouseOverEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouseOver");
		stopZoomLoop(m_zoomInButtonTimer);
	}
        
	@UiHandler("zoomInButton")
	public void onZoomInButtonMouseOut(MouseOutEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouse");
		stopZoomLoop(m_zoomInButtonTimer);
	}

	@UiHandler("zoomOutButton")
	public void onZoomOutButtonMouseDown(MouseDownEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouse");
		startZoomLoop(m_zoomOutButtonTimer);
	}

	@UiHandler("zoomOutButton")
	public void onZoomOutButtonMouseUp(MouseUpEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouseOver");
		stopZoomLoop(m_zoomOutButtonTimer);
	}

	@UiHandler("zoomOutButton")
	public void onZoomOutButtonMouseOut(MouseOutEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouse");
		stopZoomLoop(m_zoomOutButtonTimer);
	}
        
	@UiHandler("zoomOutButton")
	public void onZoomOutButtonMouseOver(MouseOverEvent e) {
		e.preventDefault();
		resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouseOver");
		stopZoomLoop(m_zoomOutButtonTimer);
	}
        
    /**
     * Catch-all method for making sure only one button type is set on the element at any time.
     * This is the easiest necessary step to take cases such as the user hold-clicking on a button,
     * dragging onto the other button, and releasing their click. 
     * 
     * @param button
     * @param isInButton
     * @param newName 
     */
    private void resetStyleNamesTo(HTML button, boolean isInButton, String newName) {
        if (isInButton) {
            button.removeStyleName("map-PanZoomControlZoomInButtonMouse");
            button.removeStyleName("map-PanZoomControlZoomInButtonMouseOver");
        }
        else {
            button.removeStyleName("map-PanZoomControlZoomOutButtonMouse");
            button.removeStyleName("map-PanZoomControlZoomOutButtonMouseOver");
        }
        button.addStyleName(newName);
    }
}
