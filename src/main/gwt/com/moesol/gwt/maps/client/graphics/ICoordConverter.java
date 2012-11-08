/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.WorldCoords;

public interface ICoordConverter {
	public abstract void setViewPort(ViewPort vp);
	
	public abstract ViewCoords geodeticToView(GeodeticCoords gc);

	public abstract GeodeticCoords viewToGeodetic(ViewCoords vc);

	public abstract WorldCoords geodeticToWorld(GeodeticCoords gc);

	public abstract GeodeticCoords worldToGeodetic(WorldCoords wc);
	
	public abstract ViewCoords worldToView(WorldCoords wc);
	
	public abstract WorldCoords viewToWorld(ViewCoords vc);
	
	public abstract int mapWidth();
	
	public abstract void setISplit(ISplit split);
	
	public abstract ISplit getISplit();
}
