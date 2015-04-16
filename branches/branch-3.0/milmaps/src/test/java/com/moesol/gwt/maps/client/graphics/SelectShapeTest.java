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

public class SelectShapeTest {
	MapViewMock m_map = new MapViewMock();
	ShapeEditor m_se;
	private Util m_util = new Util(m_map.m_conv,MapViewMock.m_rb);
	
	@Before
	public void before() throws Exception {
		m_se = new ShapeEditor(m_map);
	}
	
	@Test
	public void selectShapeTest(){
		NewPolygonTool tool = new NewPolygonTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);

		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseDown(400,200);
		tool.handleMouseUp(400,200);
		tool.handleMouseDblClick(400, 200);
		
		SelectShape ss = new SelectShape(m_se);
		// Select objects vertex
		m_se.handleMouseDown(300, 50);
		ss.handleMouseUp(300,50);
		
		boolean bSelected = m_se.getShapes().get(0).isSelected();
		assertEquals(true,bSelected);
		
		m_se.handleMouseDown(100, 50);
		ss.handleMouseUp(100,50);
		bSelected = m_se.getShapes().get(0).isSelected();
		assertEquals(false,bSelected);
	}
}
