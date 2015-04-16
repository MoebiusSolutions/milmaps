/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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
import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.units.MapScale;
import com.moesol.gwt.maps.server.units.JvmMapScale;
import com.moesol.gwt.maps.shared.BoundingBox;

public class ProjectionsTest {
	
	private ViewPort viewPort = new ViewPort();
	private GeodeticCoords origGeo;
	private ViewCoords origView;
	private IProjection origProj;
	private int origWidth;
	private int origHeight;
	
	@Before
	public void before() {
		JvmMapScale.init();
		IProjection proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
	}
	
	@After
	public void after() {
//		System.out.println("origProj2 = " + origProj);
//		System.out.println("viewPort2 = " + viewPort);
		
		assertSame(origProj, viewPort.getProjection());
		assertEquals(origWidth, viewPort.getWidth());
		assertEquals(origHeight, viewPort.getHeight());
		assertEquals(origGeo, viewPort.getVpWorker().getGeoCenter());
		assertEquals(origView, viewPort.worldToView(new WorldCoords(), true));
	}
	
	private void recordViewPort() {
		origProj = viewPort.getProjection();
		origGeo = viewPort.getVpWorker().getGeoCenter();
		origView = viewPort.worldToView(new WorldCoords(), true);
		origWidth = viewPort.getWidth();
		origHeight = viewPort.getHeight();
		
//		System.out.println("origProj1 = " + origProj);
//		System.out.println("viewPort1 = " + viewPort);
	}

	@Test
	public void given_zoomed_out_when_find_small_box_then_larger_scale() {
		recordViewPort();
		
		BoundingBox box = BoundingBox.builder().top(12).left(10).bottom(10).right(12).degrees().build();
		double start = viewPort.getProjection().getEquatorialScale();
		double found = Projections.findScaleFor(viewPort, box);
		System.out.println("start: " + MapScale.forScale(start));
		System.out.println("found: " + MapScale.forScale(found));
		assertTrue(found > start);
	}
	
	@Test
	public void when_box_empty_then_same_scale() {
		recordViewPort();
		
		double wholeWorld = viewPort.getProjection().getEquatorialScale();
		BoundingBox box = BoundingBox.builder().top(-80).left(12).bottom(-80).right(12).degrees().build();
		assertEquals(wholeWorld, Projections.findScaleFor(viewPort, box), 0.0);
	}

	@Test
	public void given_zoomed_in_when_find_large_box_then_smaller_scale() {
		viewPort.getProjection().zoomByFactor(100);
		// After modifying projection reset into viewPort to get viewPort to update
		viewPort.setProjection(viewPort.getProjection());

		recordViewPort();
		
		BoundingBox box = BoundingBox.builder().top(80).left(12).bottom(-80).right(12).degrees().build();
		double start = viewPort.getProjection().getEquatorialScale();
		double found = Projections.findScaleFor(viewPort, box);
		assertTrue(found < start);
	}
	
	private double wrapLng(double lng) {
		if (lng > 180.0) {
			while (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			while (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	
	@Test
	public void wrapLngTest(){
		recordViewPort();
		IProjection proj = viewPort.getProjection();
		for (int lng = 0; lng < 1000000; lng += 45){
			double lng1 = wrapLng(lng);
			double lng2 = proj.wrapLng(lng);
			assertEquals(lng1,lng2,0.0000001);
		}
		for (int lng = 0; lng > -1000000; lng -= 45){
			double lng1 = wrapLng(lng);
			double lng2 = proj.wrapLng(lng);
			assertEquals(lng1,lng2,0.0000001);
		}
	}
	
}
