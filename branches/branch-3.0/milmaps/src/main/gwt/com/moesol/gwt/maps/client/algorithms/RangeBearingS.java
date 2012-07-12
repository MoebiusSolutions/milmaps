/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/*  Latitude/longitude spherical geodesy formulae & scripts (c) Chris Veness 2002-2011            */
/*   - www.movable-type.co.uk/scripts/latlong.html                                                */
/*                                                                                                */
/*  Sample usage:                                                                                 */
/*    var p1 = new LatLon(51.5136, -0.0983);                                                      */
/*    var p2 = new LatLon(51.4778, -0.0015);                                                      */
/*    var dist = p1.distanceTo(p2);          // in km                                             */
/*    var brng = p1.bearingTo(p2);           // in degrees clockwise from north                   */
/*    ... etc                                                                                     */
/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

package com.moesol.gwt.maps.client.algorithms;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.units.AngleUnit;

// This model assumes the earth is a sphere.
public class RangeBearingS {
	private double m_radius = IProjection.EARTH_RADIUS_METERS/1000.0;

	public double getRadius() {
		return m_radius;
	}

	public void setRadius(double radius) {
		m_radius = radius;
	}

	/**
	 * Returns the distance from this point to the supplied point, in km (using
	 * Haversine formula)
	 * 
	 * from: Haversine formula - R. W. Sinnott, "Virtues of the Haversine", Sky
	 * and Telescope, vol 68, no 2, 1984
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @param {Number} R: Earth Radius in Km. Enter 0 to use default
	 * @returns {Number} Distance in km between this point and destination point
	 */
	public double gcDistanceFromTo(GeodeticCoords p, GeodeticCoords q) {
		double R = m_radius;
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLat = lat2 - lat1;
		double dLon = lon2 - lon1;

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d;
	}

	/**
	 * Returns the (initial) bearing from this point to the supplied point, in
	 * degrees see http://williams.best.vwh.net/avform.htm#Crs
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @returns {Number} Initial bearing in degrees from North
	 */
	public double gcBearingFromTo(GeodeticCoords p, GeodeticCoords q) {
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLon = lon2 - lon1;

		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dLon);
		double brg = Math.atan2(y, x);

		return (Func.RadToDeg(brg) + 360) % 360;
	}

	/**
	 * Returns final bearing arriving at supplied destination point from this
	 * point; the final bearing will differ from the initial bearing by varying
	 * degrees according to distance and latitude
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @returns {Number} Final bearing in degrees from North
	 */
	public double gcFinalBearingFromTo(GeodeticCoords p, GeodeticCoords q) {
		// get initial bearing from supplied point back to this point...
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLon = lon1 - lon2;

		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dLon);
		double brg = Math.atan2(y, x);

		// ... & reverse it by adding 180°
		return (Func.RadToDeg(brg) + 180) % 360;
	}

	/**
	 * Returns the midpoint between this point and the supplied point. see
	 * http://mathforum.org/library/drmath/view/51822.html for derivation
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @returns {LatLon} Midpoint between this point and the supplied point
	 */
	public GeodeticCoords gcMidpointFromTo(GeodeticCoords p, GeodeticCoords q) {
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLon = lon2 - lon1;

		double Bx = Math.cos(lat2) * Math.cos(dLon);
		double By = Math.cos(lat2) * Math.sin(dLon);

		double lat3 = Math.atan2(
				Math.sin(lat1) + Math.sin(lat2),
				Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By
						* By));
		double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
		lon3 = (lon3 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to
																// -180..+180º

		return new GeodeticCoords(lon3, lat3, AngleUnit.RADIANS);
	}

	/**
	 * Returns the destination point from this point having travelled the given
	 * distance (in km) on the given initial bearing (bearing may vary before
	 * destination is reached)
	 * 
	 * see http://williams.best.vwh.net/avform.htm#LL
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param {Number} brng: Initial bearing in degrees
	 * @param {Number} dist: Distance in km
	 * @returns {GeodeticCoords} Destination point
	 */
	public GeodeticCoords gcPointFrom(GeodeticCoords p, double brng, double distKm) {
		distKm = distKm / m_radius; // convert dist to angular distance in
									// radians
		brng = Func.DegToRad(brng); //
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);

		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distKm)
				+ Math.cos(lat1) * Math.sin(distKm) * Math.cos(brng));
		double lon2 = lon1
				+ Math.atan2(
						Math.sin(brng) * Math.sin(distKm) * Math.cos(lat1),
						Math.cos(distKm) - Math.sin(lat1) * Math.sin(lat2));
		lon2 = (lon2 + 3 * Math.PI) % (2 * Math.PI) - Math.PI; // normalise to
																// -180..+180º

		return new GeodeticCoords(lon2, lat2, AngleUnit.RADIANS);
	}

	/**
	 * Returns the distance from this point to the supplied point, in km,
	 * travelling along a rhumb line
	 * 
	 * see http://williams.best.vwh.net/avform.htm#Rhumb
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @param {Number} R: Earth Radius in Km. Enter 0 to use default
	 * @returns {Number} Distance in km between this point and destination point
	 */
	public double rlDistFromTo(GeodeticCoords p, GeodeticCoords q) {
		double R = m_radius;
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLat = lat2 - lat1;
		double dLon = Math.abs(lon2 - lon1);

		double dPhi = Math.log(Math.tan(lat2 / 2 + Math.PI / 4)
				/ Math.tan(lat1 / 2 + Math.PI / 4));
		double z = (!Double.isNaN(dLat / dPhi)) ? dLat / dPhi : Math.cos(lat1); // E-W
																				// line
																				// gives
																				// dPhi=0
		// if dLon over 180° take shorter rhumb across 180° meridian:
		if (dLon > Math.PI) {
			dLon = 2 * Math.PI - dLon;
		}
		double dist = Math.sqrt(dLat * dLat + z * z * dLon * dLon) * R;

		return dist; // 4 sig figs reflects typical 0.3% accuracy of spherical
						// model
	}

	/**
	 * Returns the bearing from this point to the supplied point along a rhumb
	 * line, in degrees
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param GeodeticCoords
	 *            q: end point
	 * @returns {Number} Bearing in degrees from North
	 */
	public double rlBearingFromTo(GeodeticCoords p, GeodeticCoords q) {
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		double lat2 = q.getPhi(AngleUnit.RADIANS);
		double lon2 = q.getLambda(AngleUnit.RADIANS);
		double dLon = lon2 - lon1;

		double dPhi = Math.log(Math.tan(lat2 / 2 + Math.PI / 4)
				/ Math.tan(lat1 / 2 + Math.PI / 4));
		if (Math.abs(dLon) > Math.PI) {
			dLon = dLon > 0 ? -(2 * Math.PI - dLon) : (2 * Math.PI + dLon);
		}
		double brg = Math.atan2(dLon, dPhi);

		return (Func.RadToDeg(brg) + 360) % 360;
	}

	/**
	 * Returns the destination point from this point having travelled the given
	 * distance (in km) on the given bearing along a rhumb line
	 * 
	 * @param GeodeticCoords
	 *            p: start point
	 * @param {Number} brng: Bearing in degrees from North
	 * @param {Number} distKm: Distance in km
	 * @param {Number} R: Earth Radius in Km. Enter 0 to use default
	 * @returns {LatLon} Destination point
	 */
	public GeodeticCoords rlPointFrom(GeodeticCoords p, double brng, double distKm) {
		double R = m_radius;
		double d = distKm / R; // d = angular distance covered on earth's
								// surface
		double lat1 = p.getPhi(AngleUnit.RADIANS);
		double lon1 = p.getLambda(AngleUnit.RADIANS);
		brng = Func.DegToRad(brng);

		double lat2 = lat1 + d * Math.cos(brng);
		double dLat = lat2 - lat1;
		double dPhi = Math.log(Math.tan(lat2 / 2 + Math.PI / 4)
				/ Math.tan(lat1 / 2 + Math.PI / 4));
		double q = (!Double.isNaN(dLat / dPhi)) ? dLat / dPhi : Math.cos(lat1); // E-W
																				// line
																				// gives
																				// dPhi=0
		double dLon = d * Math.sin(brng) / q;
		// check for some daft bugger going past the pole
		if (Math.abs(lat2) > Math.PI / 2) {
			lat2 = (lat2 > 0 ? Math.PI - lat2 : -(Math.PI - lat2));
		}
		double lon2 = (lon1 + dLon + 3 * Math.PI) % (2 * Math.PI) - Math.PI;

		return new GeodeticCoords(lon2, lat2, AngleUnit.RADIANS);
	}
	// new code
	public RngBrg RngBrgFromTo(GeodeticCoords p, GeodeticCoords q){
		double rngKm = gcDistanceFromTo(p,q);
		double brgDeg = gcBearingFromTo(p,q);
		return new RngBrg(rngKm, brgDeg);
	}
}
