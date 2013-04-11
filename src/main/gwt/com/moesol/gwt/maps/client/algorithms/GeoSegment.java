/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class GeoSegment {
	//
	GeodeticCoords m_p;
	GeodeticCoords m_q;
	//
	public GeoSegment() {
		m_p = new GeodeticCoords();
		m_q = new GeodeticCoords();
	}
	//
	public GeoSegment(GeodeticCoords p,GeodeticCoords q) {
		m_p = new GeodeticCoords(p.getLambda(AngleUnit.DEGREES),
								 p.getPhi(AngleUnit.DEGREES),AngleUnit.DEGREES );
		m_q = new GeodeticCoords(q.getLambda(AngleUnit.DEGREES),
				 				 q.getPhi(AngleUnit.DEGREES),AngleUnit.DEGREES );
	}
	//
	public GeodeticCoords getP() { return(m_p); }
	public GeodeticCoords getQ() { return(m_q); }
}
