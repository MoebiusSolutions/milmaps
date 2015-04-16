/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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

import com.moesol.gwt.maps.client.CylEquiDistProj;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.WorldCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;

/*
 * Test the following methods
 * * geodeticToView
 * * viewToGeodetic
 * * geodeticToWorld
 * * worldToGeodetic
 * * mapWidth
 * * worldToView
 * * viewToWorld
 */

public class ConverterTest {
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter conv;
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		double scale = proj.getBaseEquatorialScale();
		proj.setEquatorialScale(scale/2);
		viewPort.setProjection(proj);
		conv = new Converter(viewPort);
	}

	@Test
	public void geodeticToViewTest() {
		GeodeticCoords gc = new GeodeticCoords(0,0,AngleUnit.DEGREES);
		ViewCoords vc = conv.geodeticToView(gc);
		assertEquals(300,vc.getX());
		assertEquals(200,vc.getY());
	}
	
	@Test
	public void viewToGeodeticTest(){
		ViewCoords vc = new ViewCoords(300,200);
		GeodeticCoords gc = conv.viewToGeodetic(vc);
		assertEquals(0,gc.getPhi(AngleUnit.DEGREES),0.001);
		assertEquals(0,gc.getLambda(AngleUnit.DEGREES),0.001);
	}
	
	@Test
	public void geodeticToWorldTest(){
		GeodeticCoords gc = new GeodeticCoords(0,0,AngleUnit.DEGREES);
		WorldCoords wc = conv.geodeticToWorld(gc);
		assertEquals(256,wc.getX());
		assertEquals(128,wc.getY());
	}
	
	@Test
	public void worldToGeodeticTest(){
		WorldCoords wc = new WorldCoords(256,128);
		GeodeticCoords gc = conv.worldToGeodetic(wc);
		assertEquals(0,gc.getPhi(AngleUnit.DEGREES),0.001);
		assertEquals(0,gc.getLambda(AngleUnit.DEGREES),0.001);		
	}
	
	@Test
	public void mapWidthTest(){
		int width = conv.mapWidth();
		assertEquals(512,width);
	}
	
	@Test
	public void worldToViewTest(){
		WorldCoords wc = new WorldCoords(256,128);
		ViewCoords vc = conv.worldToView(wc);
		assertEquals(300,vc.getX());
		assertEquals(200,vc.getY());	
	}
	
	@Test
	public void viewToWorldTest(){
		ViewCoords vc = new ViewCoords(300,200);
		WorldCoords wc = conv.viewToWorld(vc);
		assertEquals(256,wc.getX());
		assertEquals(128,wc.getY());
	}
}
