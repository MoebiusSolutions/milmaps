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

public class BearingParserTest {
	@Test
	public void parserTest() {

		Bearing brg = BearingParser.parse("0.444Rad");
		assertEquals(25.439326,brg.value(AngleUnit.DEGREES), 0.0001);
		
		brg = BearingParser.parse("0.444R");
		assertEquals(25.439326,brg.value(AngleUnit.DEGREES), 0.0001);
		
		brg = BearingParser.parse("56 deg");
		assertEquals(56,brg.value(AngleUnit.DEGREES), 0.0001);
		
		brg = BearingParser.parse("56D");
		assertEquals(56,brg.value(AngleUnit.DEGREES), 0.0001);
		
		brg = BearingParser.parse("123.5�");
		assertEquals(123.5,brg.value(AngleUnit.DEGREES), 0.0001);
		
		brg = BearingParser.parse("89");
		assertEquals(89,brg.value(AngleUnit.DEGREES), 0.0001);
	}
}
