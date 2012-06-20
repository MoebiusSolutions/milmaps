/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public interface DistanceUnit {
	public static final DistanceUnit METERS = new Meters();
	public static final DistanceUnit KILOMETERS = new Kilometers();
	public double toMeters(double v);
	public double toKilometers(double v);
	public double fromKilometers(double v);
	public double fromMeters(double v);
}
