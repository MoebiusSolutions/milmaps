/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public class Kilometers implements DistanceUnit {
	private static double KilometersToMeters = 1000.0;
	public static double asMeters(double d) {
		return d*KilometersToMeters;
	}

	@Override
	public double toKilometers(double v) {
		return v;
	}

	@Override
	public double toMeters(double v) {
		return asMeters(v);
	}

	@Override
	public double fromKilometers(double v) {
		return v;
	}
	
	@Override
	public double fromMeters(double v) {
		// TODO Auto-generated method stub
		return v/KilometersToMeters;
	}
	
	public static Distance distance(double d) {
		return new Distance(d, KILOMETERS);
	}
}
