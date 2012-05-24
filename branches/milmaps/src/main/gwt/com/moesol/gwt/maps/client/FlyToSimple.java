/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
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
