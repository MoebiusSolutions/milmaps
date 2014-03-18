/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import java.util.LinkedList;

import com.moesol.gwt.maps.client.GeodeticCoords;

public class GeoPolygon extends LinkedList<GeodeticCoords> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GeoPolygon() {
	}
	
	public GeoPolygon(GeodeticCoords pnts[]) {
		setPosits(pnts);
	}
	
	public synchronized void setPosits(GeodeticCoords pnts[]) {
		clear();
		for (int i=0;i<pnts.length;i++) {
			add(pnts[i]);
		}
	}
}
