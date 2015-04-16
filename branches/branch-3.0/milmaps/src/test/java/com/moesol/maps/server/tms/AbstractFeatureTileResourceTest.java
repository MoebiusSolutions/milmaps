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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.moesol.gwt.maps.shared.BoundingBox;
import com.moesol.gwt.maps.shared.Feature;

public class AbstractFeatureTileResourceTest {

	private static class TestFeatureTileResource extends
			AbstractFeatureTileResource {
		private TestFeatureProvider m_featureProvider;
		private TestFeatureTileRenderer m_renderer;

		public TestFeatureTileResource() {
			m_featureProvider = new TestFeatureProvider();
			m_renderer = new TestFeatureTileRenderer(m_featureProvider);
		}

		@Override
		public IFeatureTileRenderer getFeatureTileRenderer() {
			return m_renderer;
		}

		public TestFeatureProvider getFeatureProvider() {
			return m_featureProvider;
		}

		@Override
		protected ITileRenderer getRenderer() {
			return m_renderer;
		}
	}

	private TestFeatureTileResource m_resource;
	private ArrayList<Feature> m_features;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		m_resource = new TestFeatureTileResource();
		m_features = new ArrayList<Feature>();
		m_resource.getFeatureProvider().setData(m_features);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTracksAt_noTracksAtLocation() throws Exception {
		Feature f1 = new Feature.Builder("f1", 10.0, 20.0).build();
		m_features.add(f1);
		Feature f2 = new Feature.Builder("f2", -89.0, -179.0).build();
		m_features.add(f2);

		TestFeatureTileRenderer renderer = (TestFeatureTileRenderer) m_resource
				.getFeatureTileRenderer();
		final BufferedImage iconMock = renderer.getIcon(null);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);

		EasyMock.replay(iconMock);

		String json = m_resource.getHitFeatures(1, 1, 1);
		assertEquals("[]", json);
	}

	@Test
	public void testGetTracksAt_oneTrackAtLocation() throws Exception {
		Feature f1 = new Feature.Builder("f1", 10.0, 10.0).build();
		m_features.add(f1);
		Feature f2 = new Feature.Builder("f2", 1, 1).build();
		m_features.add(f2);

		TestFeatureTileRenderer renderer = (TestFeatureTileRenderer) m_resource
				.getFeatureTileRenderer();
		final BufferedImage iconMock = renderer.getIcon(null);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);

		EasyMock.replay(iconMock);

		String json = m_resource.getHitFeatures(1, 1, 1);
		assertTrue(json.contains("\"title\":\"f2\""));
		assertFalse(json.contains("\"title\":\"f1\""));
	}

	@Test
	public void testGetBoundingBoxJson_precision() {
		BoundingBox bbox = BoundingBox.builder()
				.top(89.999999999999)
				.left(-0.10000000000005568)
				.bottom(-0.100000000012)
				.right(179.99999999999)
				.degrees()
				.build();
		assertEquals(
				"{\"minLat\":-0.100000000012,\"minLng\":-0.10000000000005568,\"maxLat\":89.999999999999,\"maxLng\":179.99999999999}",
				m_resource.createBoundingBoxJson(bbox));
	}

	@Test
	public void testGetTracksAt_multipleTracksAtLocation() throws Exception {
		Feature f1 = new Feature.Builder("f1", 1, 1).build();
		m_features.add(f1);
		Feature f2 = new Feature.Builder("f2", 1, 1).build();
		m_features.add(f2);

		TestFeatureTileRenderer renderer = (TestFeatureTileRenderer) m_resource
				.getFeatureTileRenderer();
		final BufferedImage iconMock = renderer.getIcon(null);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);
		EasyMock.expect(iconMock.getHeight()).andReturn(10);
		EasyMock.expect(iconMock.getWidth()).andReturn(10);

		EasyMock.replay(iconMock);

		String json = m_resource.getHitFeatures(1, 1, 1);
		assertTrue(json.contains("\"title\":\"f2\""));
		assertTrue(json.contains("\"title\":\"f1\""));
	}
}
