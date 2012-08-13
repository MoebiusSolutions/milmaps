/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public class Miles implements DistanceUnit {
	private static double MilesToMeters = 1609.344;
	public static double asKilometers(double d) {
		return d*MilesToMeters/1000.0;
	}
	
	public static double asMeters(double d) {
		return d*MilesToMeters;
	}


	@Override
	public double toMeters(double v) {
		return asMeters(v);
	}

	@Override
	public double toKilometers(double v) {
		return asKilometers(v);
	}
	
	@Override
	public double toMiles(double v) {
		return v;
	}

	@Override
	public double fromMeters(double v) {
		return Meters.asMiles(v);
	}

	@Override
	public double fromKilometers(double v) {
		return Kilometers.asMeters(v);
	}
	
	@Override
	public double fromMiles(double v) {
		return v;
	}
	
	public static Distance distance(double d) {
		return new Distance(d, MILES);
	}

}
