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


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.ViewCoords;

public class FuncTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void nearestPtTest() {
		// positive slope
		ViewCoords p = new ViewCoords(1,1);
		ViewCoords q = new ViewCoords(10,10);
		ViewCoords c = Func.closestPt(p, q, 11, 11);
		assertEquals(q.getX(),c.getX());
		assertEquals(q.getY(),c.getY());
		c = Func.closestPt(p, q, 11, 10);
		assertEquals(q.getX(),c.getX());
		assertEquals(q.getY(),c.getY());
		c = Func.closestPt(p, q, 0, 0);
		assertEquals(p.getX(),c.getX());
		assertEquals(p.getY(),c.getY());
		// negative slope
		p = new ViewCoords(1,10);
		q = new ViewCoords(10,1);
		c = Func.closestPt(p, q, 11, 2);
		assertEquals(q.getX(),c.getX());
		assertEquals(q.getY(),c.getY());
		c = Func.closestPt(p, q, 11, 0);
		assertEquals(q.getX(),c.getX());
		assertEquals(q.getY(),c.getY());
		c = Func.closestPt(p, q, 0, 11);
		assertEquals(p.getX(),c.getX());
		assertEquals(p.getY(),c.getY());
		//
		p = new ViewCoords(0,6);
		q = new ViewCoords(6,0);
		c = Func.closestPt(p, q, 0,0);
		assertEquals(3,c.getX());
		assertEquals(3,c.getY());
		c = Func.closestPt(p, q, 0,4);
		assertEquals(1,c.getX());
		assertEquals(5,c.getY());

	}
}
