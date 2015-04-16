/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


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
