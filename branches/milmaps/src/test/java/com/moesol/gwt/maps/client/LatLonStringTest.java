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

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class LatLonStringTest {
	
	@Test
	public void testLatLonToString() {
		double lat = 30.12345;
		double lng = -30.12345;
		String pos = LatLonString.build(lat,lng);
		String answer = "30"+"\u00B0"+"07"+"\'"+"24.42"+"\""+"N " + " " +
						"030"+"\u00B0"+"07"+"\'"+"24.42"+"\"" + "W ";
		assertEquals(pos,answer );
		lat = 30.25;
		lng = 30.25;
		pos = LatLonString.build(lat,lng);
		answer = "30"+"\u00B0"+"15"+"\'"+ "00.00"+"\""+"N " + " " +
				 "030"+"\u00B0"+"15"+"\'"+"00.00"+"\"" + "E ";
		assertEquals(pos,answer );
		lat = -30.25;
		lng = -30.25;
		pos = LatLonString.build(lat,lng);
		answer = "30"+"\u00B0"+"15"+"\'"+ "00.00"+"\""+"S " + " " +
				 "030"+"\u00B0"+"15"+"\'"+"00.00"+"\"" + "W ";
		assertEquals(pos,answer );
		lat = -30.25;
		lng = 30.25;
		pos = LatLonString.build(lat,lng);
		answer = "30"+"\u00B0"+"15"+"\'"+"00.00"+"\""+"S " + " " +
				 "030"+"\u00B0"+"15"+"\'"+"00.00"+"\"" + "E ";
		assertEquals(pos,answer );
	}
}
