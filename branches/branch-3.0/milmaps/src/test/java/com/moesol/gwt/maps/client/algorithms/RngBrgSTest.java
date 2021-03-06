/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

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

import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class RngBrgSTest {
	private static final RangeBearingS m_rb = new RangeBearingS();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void gcRoundTripTest() {
		GeodeticCoords p = new GeodeticCoords(45,45,AngleUnit.DEGREES);
		GeodeticCoords q = new GeodeticCoords(90,-45,AngleUnit.DEGREES);
		
		double dist = m_rb.gcRangeFromTo(p, q);
		double brg  = m_rb.gcBearingFromTo(p, q);
		GeodeticCoords gc = m_rb.gcPointFrom(p, brg, dist);
		assertEquals(gc.getPhi(AngleUnit.DEGREES),q.getPhi(AngleUnit.DEGREES),0.0001);
	}
	
	@Test
	public void rlRoundTripTest() {
		GeodeticCoords p = new GeodeticCoords(45,45,AngleUnit.DEGREES);
		GeodeticCoords q = new GeodeticCoords(90,-45,AngleUnit.DEGREES);
		
		double dist = m_rb.rlRangeFromTo(p, q);
		double brg  = m_rb.rlBearingFromTo(p, q);
		
		GeodeticCoords gc = m_rb.rlPointFrom(p, brg, dist);
		assertEquals(gc.getPhi(AngleUnit.DEGREES),q.getPhi(AngleUnit.DEGREES),0.0001);
	}
	
	@Test
	public void roundTripTest() {
		GeodeticCoords p = new GeodeticCoords(25,45,AngleUnit.DEGREES);
		
		double disKm = 2880;
		double inc = 10;
		for(int i = 0; i < 36; i++){
			double brg = i*inc;
			GeodeticCoords q = m_rb.gcPointFrom(p, brg, disKm);
			double dis = m_rb.gcRangeFromTo(p, q);
			assertEquals(disKm, dis, 0.0001);
		}
	}	
}
