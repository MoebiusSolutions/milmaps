package com.moesol.gwt.maps.client;

import com.google.gwt.animation.client.Animation;

public interface IFlyTo {

	public abstract Animation getAnimation();

	/**
	 * Start a flyTo animation.
	 * 
	 * @param endLat
	 * @param endLng
	 * @param projectionScale
	 */
	public abstract void flyTo(GeodeticCoords endPt, double projectionScale);

}