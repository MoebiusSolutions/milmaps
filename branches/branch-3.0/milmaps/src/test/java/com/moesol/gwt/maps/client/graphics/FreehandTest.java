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
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class FreehandTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Freehand m_fh;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv = new Converter(viewPort);
	//private Util m_util = new Util(m_conv,m_rb);
	
	@Before
	public void before() throws Exception {
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_fh = new Freehand();
		m_fh.setCoordConverter(m_conv);
		
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		Freehand fh = (Freehand)Freehand.create(m_conv, pos);
		for (int i = 0; i < 4; i++){
			IAnchorTool u = fh.getVertexTool(i);
			IAnchorTool v = fh.getAnchorByPosition(pos[i]);
			assertEquals(u,v);
		}
		return;
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_fh.createEditTool(se);
		assertEquals(true, st != null);
	}
}
