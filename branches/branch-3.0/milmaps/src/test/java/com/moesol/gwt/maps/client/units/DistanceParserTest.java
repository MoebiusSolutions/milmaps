/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

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
