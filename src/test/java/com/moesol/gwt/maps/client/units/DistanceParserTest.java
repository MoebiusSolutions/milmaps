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

public class DistanceParserTest {

	@Test
	public void parserTest() {
		String dis = "444M";
		Distance d = DistanceParser.parse(dis);
		assertEquals(444,d.getDistance(DistanceUnit.METERS), 0.0001);
		
		dis = "0.444KM";
		d = DistanceParser.parse(dis);
		assertEquals(444,d.getDistance(DistanceUnit.METERS), 0.0001);
	}

}
