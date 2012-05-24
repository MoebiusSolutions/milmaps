/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
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
