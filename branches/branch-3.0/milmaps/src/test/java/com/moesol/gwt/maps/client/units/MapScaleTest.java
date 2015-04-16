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


import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.MapScale;

public class MapScaleTest {

	@Test
	public void testAsDouble() {
		MapScale s = MapScale.forScale(1/50000.0);
		assertEquals(1/50000.0, s.asDouble(), 0.0);
	}

	@Test
	public void testParse() {
		assertEquals(1/1000000.0, MapScale.parse("1:1M").asDouble(), 0.0);
		assertEquals(1/50000.0, MapScale.parse("1:50K").asDouble(), 0.0);
	}
	@Test
	public void testEquals() {
		MapScale s1 = MapScale.forScale(1.0);
		MapScale s2 = MapScale.forScale(1.0);
		MapScale s3 = MapScale.forScale(2.0);
		
		assertEquals(s1, s2);
		assertFalse(s1.equals(s3));
	}

}
