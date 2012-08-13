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
	private static double MetersToMiles = 1.0/1609.344;
	public static double asMeters(double d) {
		return d*KilometersToMeters;
	}
	
	public static double asMiles(double d){
		return d*MetersToMiles/1000.0;
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
	public double toMiles(double v) {
		return asMiles(v);
	}

	@Override
	public double fromKilometers(double v) {
		return v;
	}
	
	@Override
	public double fromMeters(double v) {
		return v/KilometersToMeters;
	}
	
	@Override
	public double fromMiles(double v) {
		return Miles.asKilometers(v);
	}
	
	public static Distance distance(double d) {
		return new Distance(d, KILOMETERS);
	}
}
