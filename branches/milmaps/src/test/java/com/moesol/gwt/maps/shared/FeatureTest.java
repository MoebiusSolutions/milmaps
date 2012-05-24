/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.shared;


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
