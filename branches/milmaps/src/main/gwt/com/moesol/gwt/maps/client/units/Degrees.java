/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import com.moesol.gwt.maps.client.GeodeticCoords;

public class Degrees implements AngleUnit {
	public static double asRadians(double d) {
		return Math.PI * d / 180.0;
	}

	@Override
	public double toDegrees(double v) {
		return v;
	}

	@Override
	public double toRadians(double v) {
		return asRadians(v);
	}

	@Override
	public double fromDegrees(double v) {
		return v;
	}

	@Override
	public double fromRadians(double v) {
		return Radians.asDegrees(v);
	}
	
	public static GeodeticCoords geodetic(double lat, double lng) {
		return new GeodeticCoords(lng, lat, DEGREES);
	}
	
}
