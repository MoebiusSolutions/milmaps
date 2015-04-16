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


import com.moesol.gwt.maps.client.units.Degrees;
import com.moesol.gwt.maps.shared.BoundingBox;

/**
 * Common computations needed for projections
 * @author hastings
 */
public class Projections {
	
	/**
	 * Search for the tightest scale where view port contains box. If box is currently visible in the view port
	 * then search in until box is not visible and return the last scale where box was still visible. If box
	 * is currently not visible in the view port, then search out until it is visible. Searching supports
	 * projections such as Mercator where the scale changes based on location.
	 * 
	 * @param viewPort ViewPort
	 * @param box BoundingBox to scale to
	 * @return projection scale that makes box visible in the viewport
	 */
	public static double findScaleFor(ViewPort viewPort, BoundingBox box) {
		GeodeticCoords oldCenter = viewPort.getVpWorker().getGeoCenter();
		IProjection oldProjection = viewPort.getProjection();
		
		IProjection tmp = oldProjection.cloneProj();
		viewPort.setProjection(tmp); // Updates view port state
		viewPort.getVpWorker().setGeoCenter( Degrees.geodetic(box.getCenterLat(), box.getCenterLng() ) );
		try {
			GeodeticCoords upperLeft = Degrees.geodetic(box.getTopLat(), box.getLeftLon());
			GeodeticCoords lowerRight = Degrees.geodetic(box.getBotLat(), box.getRightLon());
			// If both already fit then search by zooming in until they do not 
			boolean searchIn = viewPort.isInViewPort(upperLeft) && viewPort.isInViewPort(lowerRight);
			double searchMultiplier = searchIn ? 2.0 : 0.5;
			double lastScale = tmp.getEquatorialScale();
			// Max search 64 iterations
			for (int i = 0; i < 64; i++) {
				tmp.setEquatorialScale(tmp.getEquatorialScale() * searchMultiplier);
				viewPort.setProjection(tmp);
				boolean nowInView = viewPort.isInViewPort(upperLeft) && viewPort.isInViewPort(lowerRight);
				if (searchIn != nowInView) {
					if (searchIn) {
						return lastScale; // Last scale was the last to still show the coordinates
					}
					return tmp.getEquatorialScale(); // This scale is the first to show the coordinates
				}
				lastScale = tmp.getEquatorialScale();
			}
			return oldProjection.getEquatorialScale(); // Could not find what we were looking for return current scale.
		} finally {
			// We just mucked around with the view port state, restore it.
			viewPort.getVpWorker().setGeoCenter(oldCenter);
			viewPort.setProjection(oldProjection);
		}
	}
}
