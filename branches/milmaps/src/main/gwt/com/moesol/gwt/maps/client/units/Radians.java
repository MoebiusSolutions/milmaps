/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public class Radians implements AngleUnit {
	
	public static double asDegrees(double r) {
		return 180.0 * r/ Math.PI; 
	}

	@Override
	public double toDegrees(double v) {
		return asDegrees(v);
	}

	@Override
	public double toRadians(double v) {
		return v;
	}

	@Override
	public double fromDegrees(double v) {
		return Degrees.asRadians(v);
	}

	@Override
	public double fromRadians(double v) {
		return v;
	}
}
