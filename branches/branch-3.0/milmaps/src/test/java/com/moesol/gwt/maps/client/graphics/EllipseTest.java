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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.CylEquiDistProj;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.SRngBrg;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class EllipseTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Ellipse m_ellipse;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	private ICanvasTool m_canvas = new CanvasToolMock();
	private List<Point> m_list = new ArrayList<Point>();
	
	private ViewCoords compViewPt( GeodeticCoords cent, double rotBrg, 
								   double brgDeg, double a, double b) {
		double angle = Func.DegToRad(brgDeg);
		double x = (a * Math.cos(angle));
		double y = (b * Math.sin(angle));
		if (a != b) {
			angle = Math.atan2(y, x);
		}
		double rngKm = Math.sqrt(x * x + y * y);
		GeodeticCoords gc = m_rb.gcPointFrom(cent, rotBrg + Func.RadToDeg(angle), rngKm);
		return m_conv.geodeticToView(gc);
	}
	
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		m_ellipse = new Ellipse();
		m_ellipse.setCoordConverter(m_conv);
		
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_ellipse.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords gc = m_conv.viewToGeodetic(new ViewCoords(300,200));
		SRngBrg rb = m_util.pixPointsToRngBrg(300, 200, 400, 200);
		Bearing brg = Bearing.builder(). value(rb.getBearing()).degrees().build();
		Distance smj = Distance.builder().value(rb.getRanegKm()).kilometers().build();
		Distance smn = Distance.builder().value(rb.getRanegKm()/2).kilometers().build();
		Ellipse ellipse = (Ellipse) Ellipse.create(m_conv, gc, brg, smj, smn);
		GeodeticCoords gc2 = ellipse.getCenter();
		assertEquals(gc,gc2);
		
		GeodeticCoords pos = m_util.pixToPos(300, 200);
		IAnchorTool tool = ellipse.getCenterAnchorTool();
		IAnchorTool tool2 = ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		pos = m_util.pixToPos(400, 200);
		tool = ellipse.getSmjAnchorTool();
		tool2 = ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		pos = m_util.pixToPos(300, 150);
		tool = ellipse.getSmnAnchorTool();
		tool2 = ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		return;
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_ellipse.getSmjPos();
		double disKm = m_rb.gcRangeFromTo(cent,gc);
		double rotBrg = m_rb.gcBearingFromTo(cent,gc);
		
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		ViewCoords pt = compViewPt(cent, rotBrg, 45, disKm, disKm/2);
		gc = m_util.pixToPos(pt.getX(), pt.getY());
		boolean bTouches = m_ellipse.positionTouches(gc);
		assertEquals(true,bTouches);
	}

	@Test
	public void mouseMoveSemiMinorTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords pos = m_util.pixToPos(300, 150);
		double disKm = m_rb.gcRangeFromTo(cent,pos);
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		
		IAnchorTool tool = m_ellipse.getSmnAnchorTool();
		tool.handleMouseMove(300, 100);
		tool.handleMouseUp(300, 100);
		//Test for semi-minor movement
		pos = m_ellipse.getSmnPos();
		GeodeticCoords testPos = m_util.pixToPos(300,100);
		assertEquals(pos.getPhi(AngleUnit.DEGREES),
					 testPos.getPhi(AngleUnit.DEGREES),0.0001);
		assertEquals(pos.getLambda(AngleUnit.DEGREES),
				 	 testPos.getLambda(AngleUnit.DEGREES),0.0001);
	}

	@Test
	public void mouseMoveSemiMajorTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_ellipse.getSmjPos();
		double disKm = m_rb.gcRangeFromTo(cent,gc);
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		
		int len = (int)(100*Math.sqrt(2));
		IAnchorTool tool = m_ellipse.getSmjAnchorTool();
		tool.handleMouseMove( 300 + len, 200-len);
		tool.handleMouseUp( 300 + len, 200-len);
		//Test for semi-minor movement
		GeodeticCoords pos = m_ellipse.getSmjPos();
		SRngBrg rb = m_rb.gcRngBrgFromTo(cent, pos);
		double brg = Func.wrap360(rb.getBearing()- 90);
		gc = m_rb.gcPointFrom(cent, brg, disKm/2);
		tool = m_ellipse.getSmnAnchorTool();
		IAnchorTool tool2 = m_ellipse.getAnchorByPosition(gc);
		assertEquals(tool,tool2);
	}
	

	@Test
	public void mouseMoveAndUpCenterTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_ellipse.getSmjPos();
		double disKm = m_rb.gcRangeFromTo(cent,gc)/2;
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		IAnchorTool tool = m_ellipse.getCenterAnchorTool();
		tool.handleMouseMove (350, 200);
		tool.handleMouseUp (350, 200);
		// test center anchor
		GeodeticCoords pos = m_util.pixToPos(350,200);;
		IAnchorTool tool2 = m_ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// test semi-minor anchor
		tool = m_ellipse.getSmnAnchorTool();
		pos = m_conv.viewToGeodetic(new ViewCoords(350,150));
		tool2 = m_ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// test semi-major anchor
		tool = m_ellipse.getSmjAnchorTool();
		pos = m_conv.viewToGeodetic(new ViewCoords(450,200));
		tool2 = m_ellipse.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}
	
	protected void copyList(List<Point> list){
		int n = list.size();
		for (int i = 0; i < n; i++){
			Point p = list.get(i);
			p = new Point(p.x,p.y);
			m_list.add(p);
		}
	}
	
	protected void writeFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(true);
		ctm.createFile("ellipse");
		IContext ct = m_canvas.getContext();
		m_ellipse.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("ellipse");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_ellipse.render(ct);
		List<Point> list = ctm.getList();
		int n = list.size();
		int m = m_list.size();
		assertEquals(m,n);
		for (int i = 0; i < n; i++){
			Point p = list.get(i);
			Point q = m_list.get(i);
			assertEquals(q.x, p.x);
			assertEquals(q.y, p.y);
		}
		m_list.clear();
		ctm.clearList();
	}
	
	@Test
	public void drawObjectTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_ellipse.getSmjPos();
		double disKm = m_rb.gcRangeFromTo(cent,gc);
		
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		
		boolean bWrite = false;
		if (bWrite){
			writeFile();
		}else{
			compareToFile();
		}
	}
	
	@Test
	public void drawHandleTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_ellipse.setCenter(cent);
		m_ellipse.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_ellipse.getSmjPos();
		double disKm = m_rb.gcRangeFromTo(cent,gc);
		
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_ellipse.setSmnAxis(dis);
		
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_ellipse.drawHandles(ct);
	}
}
