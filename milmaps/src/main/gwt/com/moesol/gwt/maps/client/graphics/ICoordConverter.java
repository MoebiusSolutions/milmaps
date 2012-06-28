/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.WorldCoords;

public interface ICoordConverter {
	public abstract void setMap(MapView map);
	
	public abstract ViewCoords geodeticToView(GeodeticCoords gc);

	public abstract GeodeticCoords viewToGeodetic(ViewCoords vc);

	public abstract WorldCoords geodeticToWorld(GeodeticCoords gc);

	public abstract GeodeticCoords worldToGeodetic(WorldCoords wc);
	
	public abstract ViewCoords worldToView(WorldCoords wc);
	
	public abstract WorldCoords viewToWorld(ViewCoords vc);
	
	public abstract int mapWidth();
}
