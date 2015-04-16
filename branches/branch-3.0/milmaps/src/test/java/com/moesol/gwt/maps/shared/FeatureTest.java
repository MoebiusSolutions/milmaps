/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared;

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

public class FeatureTest {

	private static final double EPSILON = 0.000001;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBuilder_constructorFields() {
		final String title = "title";
		final double lat = 0.1;
		final double lng = 18.2;
		Feature.Builder builder = new Feature.Builder(title, lat, lng);
		Feature feature = builder.build();
		
		assertEquals(title, feature.getTitle());
		assertEquals(lat, feature.getLat(), EPSILON);
		assertEquals(lng, feature.getLng(), EPSILON);
	}

	@Test
	public void testBuilder_noIcon() {
		final String title = "title";
		final double lat = 0.1;
		final double lng = 18.2;
		Feature.Builder builder = new Feature.Builder(title, lat, lng);
		Feature feature = builder.build();
		
		assertEquals(null, feature.getIcon());
	}
	
	@Test
	public void testBuilder_icon() {
		final String title = "title";
		final double lat = 0.1;
		final double lng = 18.2;
		final String iconUrl = "iconUrl";
		Feature.Builder builder = new Feature.Builder(title, lat, lng).iconUrl(iconUrl);
		Feature feature = builder.build();
		
		assertEquals(iconUrl, feature.getIcon());
	}
}
