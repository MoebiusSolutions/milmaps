/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class GeodeticCoordsTest {

	@Test
	public void testBuilder() {
		GeodeticCoords expected = Degrees.geodetic(-117, 33);
		GeodeticCoords result = GeodeticCoords.builder()
				.setLatitude(-117).setLongitude(33).degrees()
				.build();
		assertEquals(expected, result);
		
		expected = new GeodeticCoords(23, 24, AngleUnit.DEGREES, 25);
		result = GeodeticCoords.builder()
				.setLambda(23).setPhi(24).degrees().setAltitude(25)
				.build();
		assertEquals(expected, result);
	}
	
	@Test
	public void testDSL() {
		GeodeticCoords example = Degrees.geodetic(-117, 33);
		assertEquals(-117, example.latitude().degrees(), 0.0);
		assertEquals(33, example.longitude().degrees(), 0.0);
	}
	
	@Test
	public void testLegacyUsage() {
		GeodeticCoords example = Degrees.geodetic(-117, 33);
		assertEquals(-117, example.getPhi(AngleUnit.DEGREES), 0.0);
		assertEquals(33, example.getLambda(AngleUnit.DEGREES), 0.0);
	}

}
