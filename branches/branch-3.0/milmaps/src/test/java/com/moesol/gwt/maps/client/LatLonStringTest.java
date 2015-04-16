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
