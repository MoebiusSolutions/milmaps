/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

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

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.shared.BoundingBox;
import com.moesol.gwt.maps.shared.Feature;

public class AbstractFeatureTileRendererTest {

	private static final double EPSILON = 1e-6;
	private TestFeatureProvider m_provider;
	private TestFeatureTileRenderer m_renderer;

	@Before
	public void setUp() throws Exception {
		m_provider = new TestFeatureProvider();
		m_renderer = new TestFeatureTileRenderer(m_provider);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBoundingBoxForFeatures_groupedAtPrimeMeridian_centerAtAntimeridian() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", 0.1, 0.1).build());
		features.add(new Feature.Builder("title2", -10.0, -20.0).build());
		features.add(new Feature.Builder("title3", 20.0, 20.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(-20.0, bbox.getLeftLon(), EPSILON);
		assertEquals(20.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_groupedAtPrimeMeridian_centerPositive() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", 0.1, 0.1).build());
		features.add(new Feature.Builder("title2", -10.0, -20.0).build());
		features.add(new Feature.Builder("title3", 20.0, 30.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(-20.0, bbox.getLeftLon(), EPSILON);
		assertEquals(30.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_groupedAtPrimeMeridian_centerNegative() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", 0.1, 0.1).build());
		features.add(new Feature.Builder("title2", -10.0, -30.0).build());
		features.add(new Feature.Builder("title3", 20.0, 20.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(-30.0, bbox.getLeftLon(), EPSILON);
		assertEquals(20.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_groupedAtAntimeridian_centerLonPositive() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -180.0).build());
		features.add(new Feature.Builder("title2", -10.0, -160.0).build());
		features.add(new Feature.Builder("title3", 20.0, 170.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(170, bbox.getLeftLon(), EPSILON);
		assertEquals(-180, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_groupedAtAntimeridian_centerLonNegative() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -180.0).build());
		features.add(new Feature.Builder("title2", -10.0, 160.0).build());
		features.add(new Feature.Builder("title3", 20.0, -170.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(160.0, bbox.getLeftLon(), EPSILON);
		assertEquals(-180.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_groupedAtAntimeridian_centerLonAtAntimeridian() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -180.0).build());
		features.add(new Feature.Builder("title2", -10.0, 160.0).build());
		features.add(new Feature.Builder("title3", 20.0, -160.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(160.0, bbox.getLeftLon(), EPSILON);
		assertEquals(-180.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_westernHemisphere() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -170.0).build());
		features.add(new Feature.Builder("title2", -10.0, -50.0).build());
		features.add(new Feature.Builder("title3", 20.0, -10.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(-170.0, bbox.getLeftLon(), EPSILON);
		assertEquals(-10.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_easternHemisphere() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, 170.0).build());
		features.add(new Feature.Builder("title2", -10.0, 50.0).build());
		features.add(new Feature.Builder("title3", 20.0, 10.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title2", "title3" });

		assertEquals(-10.0, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(10.0, bbox.getLeftLon(), EPSILON);
		assertEquals(170.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_eitherHemisphere_positive() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, 180.0).build());
		features.add(new Feature.Builder("title3", 20.0, 0.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title3" });

		assertEquals(-0.1, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(0.0, bbox.getLeftLon(), EPSILON);
		assertEquals(180.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_eitherHemisphere_negative() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -180.0).build());
		features.add(new Feature.Builder("title3", 20.0, 0.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title3" });

		assertEquals(-0.1, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(-180.0, bbox.getLeftLon(), EPSILON);
		assertEquals(0.0, bbox.getRightLon(), EPSILON);
	}
	
	@Test
	public void testGetBoundingBoxForFeatures_bothAtAntimeridian_onePositive_oneNegative() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		features.add(new Feature.Builder("title", -0.1, -180.0).build());
		features.add(new Feature.Builder("title3", 20.0, 180.0).build());

		m_provider.setData(features);

		BoundingBox bbox = m_renderer.getBoundingBoxForFeatures(
				new String[] { "title", "title3" });

		assertEquals(-0.1, bbox.getBotLat(), EPSILON);
		assertEquals(20.0, bbox.getTopLat(), EPSILON);
		assertEquals(180.0, bbox.getLeftLon(), EPSILON);
		assertEquals(-180.0, bbox.getRightLon(), EPSILON);
	}
}
