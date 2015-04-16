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


import mil.geotransj.GeoTransException;
import mil.geotransj.Geodetic;
import mil.geotransj.Mgrs;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.LatLonString;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.WorldCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

/**
 * Hook to MapView to display the Latitude, Longitude, and MGRS position of the
 * mouse over the map.
 */
public class PositionControlLegacy extends FlowPanel {
	private final OutlinedLabelLegacy m_mousePosLabel = new OutlinedLabelLegacy();
	private final Mgrs m_mgrs = new Mgrs();
	private final Geodetic m_geo = new Geodetic();
        private int prevMouseX, prevMouseY;
	
	private MapView m_mapView = null;
	
	public PositionControlLegacy() {
		
	}
	
	public PositionControlLegacy(MapView mapView) {
		setMapView(mapView);
	}
	
	public void setMapView( MapView view ) {
		m_mapView = view;
		this.add(m_mousePosLabel);
		
		MouseMoveHandler handler = new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
                            // This event occurs even when the cursor has not moved, at least in Internet Explorer.
                            // Filter out to only new movements.
                            if (event.getX() == prevMouseX && event.getY() == prevMouseY) {
                                return;
                            }
                            prevMouseX = event.getX();
                            prevMouseY = event.getY();
                            mouseMove(event.getX(), event.getY());
			}
		};
		addStyleName("map-PositionControl");
		m_mapView.addDomHandler(handler, MouseMoveEvent.getType());
	}
	
	public void mouseMove( int x, int y ) {
		ViewCoords vc = new ViewCoords(x, y);
		WorldCoords wc = m_mapView.getViewport().getVpWorker().viewToWorld(vc);
		GeodeticCoords gc = m_mapView.getProjection().worldToGeodetic(wc);
		String pos = LatLonString.build(gc.getPhi(AngleUnit.DEGREES), 
										gc.getLambda(AngleUnit.DEGREES));
		String mgrsPos = "";
		try {
			m_geo.setLatitude(gc.getPhi(AngleUnit.DEGREES));
			m_geo.setLongitude(gc.getLambda(AngleUnit.DEGREES));
			mgrsPos = m_mgrs.GeodeticToMgrs(m_geo, 5);
		} catch (GeoTransException e) {
			mgrsPos = "";
		}
		m_mousePosLabel.setText( pos + " " + mgrsPos );
	}
	
}
