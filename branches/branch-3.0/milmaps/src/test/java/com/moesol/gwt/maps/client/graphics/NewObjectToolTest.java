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

import com.google.gwt.event.dom.client.KeyCodes;
import com.moesol.gwt.maps.client.CylEquiDistProj;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class NewObjectToolTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	//private ICanvasTool m_canvas = new CanvasToolMock();
	IShapeEditor m_se;
	
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);	
		m_se = new ShapeEditorMock();
		m_se.setCoordConverter(m_conv);
	}
	
	@Test
	public void arcToolHandleMouseTest(){
		NewArcTool tool = new NewArcTool(m_se);
		tool.handleMouseDown(300, 200);
		Arc s  = (Arc)(tool.getShape());
		GeodeticCoords  gc = s.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void arrowToolHandleMouseTest(){
		NewArrowTool tool = new NewArrowTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		Arrow s  = (Arrow)(tool.getShape());

		tool.handleMouseMove(300,50);
		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseMove(350,50);
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseMove(400,200);
		tool.handleMouseDown(400,200);
		tool.handleMouseUp(400,200);
		tool.handleMouseDblClick(400, 200);

		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void boxToolHandleMouseTest(){
		NewBoxTool tool = new NewBoxTool(m_se);
		tool.handleMouseDown(300, 200);
		Box s  = (Box)(tool.getShape());
		GeodeticCoords  gc = s.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void circleToolHandleMouseTest(){
		NewCircleTool tool = new NewCircleTool(m_se);
		tool.handleMouseDown(300, 200);
		Circle s  = (Circle)(tool.getShape());
		GeodeticCoords  gc = s.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void ellipseToolHandleMouseTest(){
		NewEllipseTool tool = new NewEllipseTool(m_se);
		tool.handleMouseDown(300, 200);
		Ellipse s  = (Ellipse)(tool.getShape());
		GeodeticCoords  gc = s.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void polygonToolHandleMouseTest(){
		NewPolygonTool tool = new NewPolygonTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		Polygon s  = (Polygon)(tool.getShape());

		tool.handleMouseMove(300,50);
		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseMove(350,50);
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseMove(400,200);
		tool.handleMouseDblClick(400, 200);

		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void freehandToolHandleMouseTest(){
		NewFreehandTool tool = new NewFreehandTool(m_se);
		tool.handleMouseDown(300, 200);
		Freehand s  = (Freehand)(tool.getShape());

		tool.handleMouseMove(300,50);
		tool.handleMouseDown(300,50);
		
		tool.handleMouseMove(350,50);
		tool.handleMouseDown(350,50);
		
		tool.handleMouseMove(400,200);
		tool.handleMouseUp(400, 200);

		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void lineToolHandleMouseTest(){
		NewLineTool tool = new NewLineTool(m_se);
		tool.handleMouseDown(300, 200);
		Line s  = (Line)(tool.getShape());
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void rectangleToolHandleMouseTest(){
		NewRectangleTool tool = new NewRectangleTool(m_se);
		tool.handleMouseDown(300, 200);
		Rectangle s  = (Rectangle)(tool.getShape());
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void sectorToolHandleMouseTest(){
		NewSectorTool tool = new NewSectorTool(m_se);
		tool.handleMouseDown(300, 200);
		Sector s  = (Sector)(tool.getShape());
		GeodeticCoords  gc = s.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
		
		tool.handleMouseMove(300,50);
		
		tool.handleMouseUp(300, 100);
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	}
	
	@Test
	public void triangleToolHandleMouseTest(){
		NewTriangleTool tool = new NewTriangleTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		Triangle s  = (Triangle)(tool.getShape());

		tool.handleMouseMove(300,50);
		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseMove(350,50);
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
	} 
}
