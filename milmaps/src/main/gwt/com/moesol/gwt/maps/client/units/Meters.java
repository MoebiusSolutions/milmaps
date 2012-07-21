/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;


public class Meters implements DistanceUnit {
	private static double MetersToKilometers = 0.001;
	public static double asKilometers(double d) {
		return d*MetersToKilometers;
	}

	@Override
	public double toMeters(double v) {
		return v;
	}

	@Override
	public double toKilometers(double v) {
		return asKilometers(v);
	}

	@Override
	public double fromMeters(double v) {
		return v;
	}

	@Override
	public double fromKilometers(double v) {
		return Kilometers.asMeters(v);
	}
	
	public static Distance distance(double d) {
		return new Distance(d, METERS);
	}
}
