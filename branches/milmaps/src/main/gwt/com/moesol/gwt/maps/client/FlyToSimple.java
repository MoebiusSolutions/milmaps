/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;



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
