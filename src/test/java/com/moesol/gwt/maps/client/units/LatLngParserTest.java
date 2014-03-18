/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moesol.gwt.maps.client.GeodeticCoords;


public class LatLngParserTest {

	@Test
	public void parserTest() {
		String ll = "45\u00b0 39' 12.52\" N 25\u00b0 36' 55.57\" E";
		GeodeticCoords gc = LatLngParser.parse(ll);
		assertEquals(45.653477,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.615436,gc.getLambda(AngleUnit.DEGREES), 0.0001);
		ll = "45\u00b039'12.52\"N 25\u00b036'55.57\"E";
		gc = LatLngParser.parse(ll);
		assertEquals(45.653477,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.615436,gc.getLambda(AngleUnit.DEGREES), 0.0001);
		ll = "45 39 N 25 36 E";
		gc = LatLngParser.parse(ll);
		assertEquals(45.65000,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.6,gc.getLambda(AngleUnit.DEGREES), 0.0001);
		ll = "45 N 25 E";
		gc = LatLngParser.parse(ll);
		assertEquals(45,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25,gc.getLambda(AngleUnit.DEGREES), 0.0001);
		ll = "45.6 N 25.7 E";
		gc = LatLngParser.parse(ll);
		assertEquals(45.6,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.7,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "45\u00b0 39'12.52\" S 25\u00b0 36'55.57\" W";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.653477,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-25.615436,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "45\u00b0 39'12.52\" S 25\u00b0 36'55.57\" W";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.653477,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-25.615436,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "45 39 S 25 36 W";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.65000,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-25.6,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "45 S 25 W";
		gc = LatLngParser.parse(ll);
		assertEquals(-45,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-25,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "45.6 S 25.7 W";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.6,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-25.7,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "-45.6 25.7";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.6,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.7,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		ll = "-45.6, 25.7";
		gc = LatLngParser.parse(ll);
		assertEquals(-45.6,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(25.7,gc.getLambda(AngleUnit.DEGREES), 0.0001);


		ll = "45N, 45W";
		gc = LatLngParser.parse(ll);
		assertEquals(45,gc.getPhi(AngleUnit.DEGREES), 0.0001);
		assertEquals(-45,gc.getLambda(AngleUnit.DEGREES), 0.0001);

		// TEST BAD ENTRIES
		ll = "45x, 44?";
		gc = LatLngParser.parse(ll);
		assertEquals(null,gc);

		ll = "45N, 44x";
		gc = LatLngParser.parse(ll);
		assertEquals(null,gc);
	}
}
