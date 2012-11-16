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
	private static double MetersToMiles = 1.0/1609.344;
	public static double asKilometers(double d) {
		return d*MetersToKilometers;
	}
	
	public static double asMiles(double d){
		return d*MetersToMiles;
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
	public double toMiles(double v) {
		return asMiles(v);
	}

	@Override
	public double fromMeters(double v) {
		return v;
	}

	@Override
	public double fromKilometers(double v) {
		return Kilometers.asMeters(v);
	}
	
	@Override
	public double fromMiles(double v) {
		return Miles.asMeters(v);
	}
	
	public static Distance distance(double d) {
		return new Distance(d, METERS);
	}
}
