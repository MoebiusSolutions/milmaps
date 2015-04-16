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
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.server.units.JvmMapScale;

/*
 * Test the following methods
 *  * adjustFirstX
 *  * switchMove
 *  * getDistance
 *  * moveNextPoint
 *  * side
 *  * shift
 */
public class SplitterTest {
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		double scale = proj.getBaseEquatorialScale();
		proj.setEquatorialScale(scale/2);
		viewPort.setProjection(proj);
	}

	@Test
	public void adjustFirstXTest() {
		Splitter split = new Splitter(viewPort);
		int mapWidth = proj.iMapWidth();
		// test move Left:
		int x = split.adjustFirstX(500, 2);
		assertEquals(500-mapWidth,x);
		// test move right
		x = split.adjustFirstX(2, 500);
		assertEquals(2+mapWidth,x);
	}

	@Test 
	public void switchMoveTest(){
		Splitter split = new Splitter(viewPort);
		int move = split.switchMove(Splitter.MOVE_LEFT);
		assertEquals(Splitter.MOVE_RIGHT,move);
		move = split.switchMove(Splitter.MOVE_RIGHT);
		assertEquals(Splitter.MOVE_LEFT,move);
	}
	
	@Test
	public void getDistanceTest(){
		Splitter split = new Splitter(viewPort);
		int mapWidth = proj.iMapWidth();
		int dist = split.getDistance(Splitter.MOVE_LEFT);
		assertEquals(-1*mapWidth,dist);
		dist = split.getDistance(Splitter.MOVE_RIGHT);
		assertEquals(mapWidth,dist);
	}
	
	@Test
	public void moveNextPointTest(){
		ViewCoords p = new ViewCoords(0,100); 
		ViewCoords q = new ViewCoords(258,100);
		Splitter split = new Splitter(viewPort);
		boolean move = split.moveNextPoint(p, q);
		assertEquals(true,move);
		q = new ViewCoords(255,100);
		move = split.moveNextPoint(p, q);
		assertEquals(false,move);
	}
	
	@Test
	public void sideTest(){
		Splitter split = new Splitter(viewPort);
		int vwWidth = viewPort.getWidth()/2;
		int side = split.side(vwWidth-1);
		assertEquals(Splitter.MOVE_LEFT,side);
		side = split.side(vwWidth + 1);
		assertEquals(Splitter.MOVE_RIGHT,side);
	}
	
	@Test
	public void shiftTest(){
		ViewCoords p = new ViewCoords(0,100); 
		ViewCoords q = new ViewCoords(258,100);
		Splitter split = new Splitter(viewPort);
		//int mapWidth = proj.iMapWidth();
		// Test first Moves
		split.setMove(Splitter.MOVE_LEFT);
		int dist = split.getDistance(Splitter.MOVE_LEFT);
		int x = split.shift(null, q);		
		assertEquals(q.getX()+dist,x);
		split.setMove(Splitter.MOVE_RIGHT);
		dist = split.getDistance(Splitter.MOVE_RIGHT);
		x = split.shift(null, q);		
		assertEquals(q.getX()+dist,x);
		// Each of these test will be with adjust set to false
		// meaning nothing has been adjusted at this point.
		// Test Move Next Point
		split.setAjustFlag(false);
		dist = split.getDistance(Splitter.MOVE_LEFT);
		split.setMove(Splitter.MOVE_LEFT);
		x = split.shift(p, q);
		assertEquals(q.getX()+dist,x);
		// Test Move Next Point
		split.setAjustFlag(false);
		dist = split.getDistance(Splitter.MOVE_RIGHT);
		split.setMove(Splitter.MOVE_RIGHT);
		x = split.shift(p, q);
		assertEquals(q.getX()+dist,x);
		
		// This is going to force a move left
		split.setAjustFlag(false);
		dist = split.getDistance(Splitter.MOVE_LEFT);
		split.setMove(Splitter.DONT_MOVE);
		x = split.shift(p, q);
		assertEquals(q.getX()+dist,x);
		
		// Next we will assume a point has been adjusted
		// Test shift
		split.setAjustFlag(true);
		dist = split.getDistance(Splitter.MOVE_LEFT);
		split.setMove(Splitter.MOVE_LEFT);
		x = split.shift(p, q);
		assertEquals(q.getX(),x);

		split.setAjustFlag(true);
		dist = split.getDistance(Splitter.MOVE_RIGHT);
		split.setMove(Splitter.MOVE_RIGHT);
		x = split.shift(p, q);
		assertEquals(q.getX(),x);
	}
}
