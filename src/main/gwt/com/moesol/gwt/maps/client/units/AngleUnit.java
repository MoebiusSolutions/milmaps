/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public interface AngleUnit {
	public static final AngleUnit DEGREES = new Degrees();
	public static final AngleUnit RADIANS = new Radians();
	public double toDegrees(double v);
	public double toRadians(double v);
	public double fromRadians(double v);
	public double fromDegrees(double v);
}
