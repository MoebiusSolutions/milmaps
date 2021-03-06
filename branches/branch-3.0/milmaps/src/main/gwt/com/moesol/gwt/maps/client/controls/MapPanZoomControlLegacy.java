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


import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.moesol.gwt.maps.client.MapView;

/**
 * Control for panning and zooming the map.
 */
public class MapPanZoomControlLegacy extends FlowPanel {

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

	private MapPanZoomControl.Presenter m_presenter = new MapPanZoomControl.Presenter();

	private MapView m_map;
	private boolean m_doingMapAction = false;

	private int m_maxPanPixels;
	private int m_millisBetweenConsecutiveActions;
	
	HTML panButton;
        
	HTML clickHighlight;

	HTML zoomInButton;

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
	
	public MapPanZoomControlLegacy( ) {
		buildUI();
	}

	/**
	 * @param map The map to plug this control into.
	 * @param maxPanPixels The maximum number of pixels to
	 * move the map each time a pan occurs. 
	 * @param millisBetweenPans The milliseconds between consecutive
	 * pans that occur while the mouse is down on the pan portion
	 * of the control.
	 */
	public MapPanZoomControlLegacy(MapView map, int maxPanPixels, int millisBetweenPans) {
		m_map = map;
		m_maxPanPixels = maxPanPixels;
		m_millisBetweenConsecutiveActions = millisBetweenPans;
		buildUI();
	}
        
        private void buildUI() {
            /*
                <g:HTMLPanel styleName="map-PanZoomControl">
                    <g:HTML styleName="map-PanZoomControlPanButton" ui:field="panButton">
                            <div class="map-PanZoomControlPanClickHighlight" ui:field="clickHighlight" />
                    </g:HTML>
                    <div class="map-PanZoomControlZoom">
                            <g:HTML styleName="map-PanZoomControlZoomInButton" ui:field="zoomInButton" />
                            <g:HTML styleName="map-PanZoomControlZoomOutButton" ui:field="zoomOutButton" />
                    </div>
                </g:HTMLPanel>
            */
            this.setStyleName("map-PanZoomControl");
            panButton = new HTML();
//            panButton.setHTML("<div class=\"map-PanZoomControlPanClickHighlight\"/>");
            panButton.setStyleName("map-PanZoomControlPanButton");
            clickHighlight = new HTML();
            clickHighlight.setStyleName("map-PanZoomControlPanClickHighlight");
            panButton.getElement().appendChild(clickHighlight.getElement());
            this.add(panButton);
            FlowPanel zooms = new FlowPanel();
            zooms.setStyleName("map-PanZoomControlZoom");
            zoomInButton = new HTML();
            zoomInButton.setStyleName("map-PanZoomControlZoomInButton");
//            zoomInButton.getElement().getStyle().setProperty("marginLeft", "18px");
            zoomOutButton = new HTML();
            zoomOutButton.setStyleName("map-PanZoomControlZoomOutButton");
//            zoomOutButton.getElement().getStyle().setProperty("marginLeft", "18px");
            zooms.add(zoomInButton);
            zooms.add(zoomOutButton);
            this.add(zooms);
            
            
            panButton.addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent e) {
                        e.preventDefault();
                        stopPanLoop();
                }
            });
            
            panButton.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent e) {
                        e.preventDefault();
                        stopPanLoop();
                }
            });
            
            panButton.addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent e) {
                    e.preventDefault();
                    updateVelocity(e);
                    startPanLoop();
                }
            });
            
            panButton.addMouseMoveHandler(new MouseMoveHandler() {
                @Override
                public void onMouseMove(MouseMoveEvent e) {
                    e.preventDefault();
                    if (m_panning) {
                            updateVelocity(e);
                    }
                }
            });
            
            zoomInButton.addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouse");
                    startZoomLoop(m_zoomInButtonTimer);
                }
            });
            
            zoomInButton.addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouseOver");
                    stopZoomLoop(m_zoomInButtonTimer);
                }
            });
            
            zoomInButton.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouseOver");
                    stopZoomLoop(m_zoomInButtonTimer);
                }
            });
            
            zoomInButton.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomInButton, true, "map-PanZoomControlZoomInButtonMouse");
                    stopZoomLoop(m_zoomInButtonTimer);
                }
            });
            
            zoomOutButton.addMouseDownHandler(new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouse");
                    startZoomLoop(m_zoomOutButtonTimer);
                }
            });
            
            zoomOutButton.addMouseUpHandler(new MouseUpHandler() {
                @Override
                public void onMouseUp(MouseUpEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouseOver");
                    stopZoomLoop(m_zoomOutButtonTimer);
                }
            });
            
            zoomOutButton.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent e) {
                    e.preventDefault();
		resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouseOver");
		stopZoomLoop(m_zoomOutButtonTimer);
                }
            });
        
            zoomOutButton.addMouseOutHandler(new MouseOutHandler() {
                @Override
                public void onMouseOut(MouseOutEvent e) {
                    e.preventDefault();
                    resetStyleNamesTo(zoomOutButton, false, "map-PanZoomControlZoomOutButtonMouse");
                    stopZoomLoop(m_zoomOutButtonTimer);
                }
            });
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
			final Style clickHighlightStyle = clickHighlight.getElement().getStyle();
			clickHighlightStyle.setDisplay(Display.BLOCK);
			clickHighlightStyle.setLeft(
					eventRelativeX - (clickHighlight.getElement().getClientWidth() / 3),
					Unit.PX);
			clickHighlightStyle.setTop(
					eventRelativeY - (clickHighlight.getElement().getClientHeight() / 3),
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
		final Style clickHighlightStyle = clickHighlight.getElement().getStyle();
		clickHighlightStyle.setDisplay(Display.NONE);
		m_panning = false;
		m_panButtonTimer.cancel();
	}

	private void startZoomLoop(Timer zoomTimer) {
		m_doingMapAction = true;
		zoomTimer.run();
		zoomTimer.scheduleRepeating(m_millisBetweenConsecutiveActions);
	}

	private void stopZoomLoop(Timer zoomTimer) {
		zoomTimer.cancel();
		if ( m_doingMapAction ){
			m_doingMapAction = false;
			m_map.updateView();
		}
	}
}
