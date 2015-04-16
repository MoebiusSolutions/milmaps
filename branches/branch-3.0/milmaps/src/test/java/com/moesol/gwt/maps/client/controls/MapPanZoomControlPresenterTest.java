/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapPanZoomControlPresenterTest {
	
	MapPanZoomControl.Presenter presenter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		presenter = new MapPanZoomControl.Presenter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalculateDelta_negative_half() {
		int delta = (int)presenter.calculateDelta(20, 5, 100);
		assertEquals(-50, delta);
	}
	
	@Test
	public void testCalculateDelta_positive_half() {
		int delta = (int)presenter.calculateDelta(20, 15, 100);
		assertEquals(50, delta);
	}
	
	@Test
	public void testCalculateDelta_negative_full() {
		int delta = (int)presenter.calculateDelta(20, 0, 100);
		assertEquals(-100, delta);
	}
	
	@Test
	public void testCalculateDelta_positive_full() {
		int delta = (int)presenter.calculateDelta(20, 20, 100);
		assertEquals(100, delta);
	}
	
	@Test
	public void testCalculateDelta_zero() {
		int delta = (int)presenter.calculateDelta(20, 10, 100);
		assertEquals(0, delta);
	}
	
	@Test
	public void testCalculateDelta_negativeOneTenth() {
		long delta = Math.round(presenter.calculateDelta(100, 45, 100));
		assertEquals(-10L, delta);
	}
	
	@Test
	public void testCalculateDelta_positiveOneTenth() {
		long delta = Math.round(presenter.calculateDelta(100, 55, 100));
		assertEquals(10L, delta);
	}
}
