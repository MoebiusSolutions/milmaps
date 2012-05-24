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

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;


public class MapScaleGwtTest extends GWTTestCase {

	@Test
	public void testToString() {
		assertEquals("1:1.0M", MapScale.parse("1:1M").toString());
		assertEquals("1:50.0K", MapScale.parse("1:50K").toString());
	}
	
	@Override
	public String getModuleName() {
		return "com.moesol.gwt.maps.Maps";
	}

}
