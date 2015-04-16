/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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



public class FlyToSimple extends FlyToCommon {
	
	private final MapView m_mapView;
	
	public FlyToSimple(MapView mapView) {
		m_mapView = mapView;
	}

	@Override
	public void flyTo(GeodeticCoords endPt, double projectionScale) {
		m_mapView.setCenter(endPt);
		m_mapView.getProjection().setEquatorialScale(projectionScale);
		m_mapView.updateView();
	}

}
