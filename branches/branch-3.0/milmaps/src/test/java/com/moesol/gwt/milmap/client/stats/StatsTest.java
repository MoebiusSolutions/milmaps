/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.milmap.client.stats;

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

import org.junit.BeforeClass;
import org.junit.Test;

import com.moesol.gwt.maps.client.stats.Stats;

public class StatsTest {
	
	@BeforeClass
	public static void beforeClass() {
		Stats.reset();
	}

	@Test
	public void testIncrementGeodeticToWorld() {
		Stats.incrementGeodeticToWorld();
		assertEquals(1, Stats.getNumGeodeticToWorld());
	}

	@Test
	public void testIncrementWorldToView() {
		Stats.incrementWorldToView();
		Stats.incrementWorldToView();
		assertEquals(2, Stats.getNumWorldToView());
	}
	
	@Test
	public void testIncrementViewToWorld() {
		Stats.incrementViewToWorld();
		Stats.incrementViewToWorld();
		Stats.incrementViewToWorld();
		assertEquals(3, Stats.getNumViewToWorld());
	}
	
	@Test
	public void testIncrementWorldToGeodetic() {
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		assertEquals(4, Stats.getNumWorldToGeodetic());
	}

	@Test
	public void testIncrementNewGeodeticCoords() {
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		assertEquals(5, Stats.getNumNewGeodeticCoords());
	}

	@Test
	public void testIncrementNewWorldCoords() {
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		assertEquals(6, Stats.getNumNewWorldCoords());
	}

	@Test
	public void testIncrementNewViewCoords() {
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		assertEquals(7, Stats.getNumNewViewCoords());
	}

}
