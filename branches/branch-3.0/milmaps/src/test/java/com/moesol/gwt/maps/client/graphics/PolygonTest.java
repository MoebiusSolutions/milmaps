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
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class PolygonTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Polygon m_ff;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	private ICanvasTool m_canvas = new CanvasToolMock();
	private List<Point> m_list = new ArrayList<Point>();
	
	@Before
	public void before() throws Exception {
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		m_ff = new Polygon();
		m_ff.setCoordConverter(m_conv);
		
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		Polygon ff = (Polygon)Polygon.create(m_conv, pos);
		for (int i = 0; i < 4; i++){
			IAnchorTool u = ff.getVertexTool(i);
			IAnchorTool v = ff.getAnchorByPosition(pos[i]);
			assertEquals(u,v);
		}
		return;
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_ff.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void ptCloseToEdgeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pos[i]);
		}

		boolean bTouches = m_ff.ptCloseToEdge(325, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(400, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(475, 225, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void AddVertexByPixelTest(){
		ViewCoords[] pts = new ViewCoords[4];
		pts[0] = new ViewCoords(300,200);
		pts[1] = new ViewCoords(350,150);
		pts[2] = new ViewCoords(450,200);
		pts[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pts[i].getX(),pts[i].getY());
		}
		
		boolean bTouches = m_ff.ptCloseToEdge(325, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(400, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(475, 225, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void InsertVertexByPixelTest(){
		ViewCoords[] pts = new ViewCoords[4];
		pts[0] = new ViewCoords(300,200);
		pts[1] = new ViewCoords(350,150);
		pts[2] = new ViewCoords(450,200);
		pts[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			if ( i != 2 ){
				m_ff.addVertex(pts[i].getX(),pts[i].getY());
			}
		}
		m_ff.insertVertex(2, pts[2].getX(),pts[2].getY());
		//
		boolean bTouches = m_ff.ptCloseToEdge(325, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(400, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(475, 225, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pos[i]);
		}

		boolean bTouches = m_ff.ptCloseToEdge(325, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(400, 175, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_ff.ptCloseToEdge(475, 225, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void miscVertexToolTest(){	
		ViewCoords[] pts = new ViewCoords[4];
		pts[0] = new ViewCoords(300,200);
		pts[1] = new ViewCoords(350,150);
		pts[2] = new ViewCoords(450,200);
		pts[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pts[i].getX(),pts[i].getY());
		}
		
		for (int i= 0; i < 4; i++){
			AbstractPosTool t = m_ff.getAbstractPosTool(i);
			GeodeticCoords p = t.getGeoPos();
			GeodeticCoords q = m_util.pixToPos(pts[i].getX(), pts[i].getY());
			assertEquals(true,p.equals(q));
		}
		
		for (int i= 0; i < 4; i++){
			IAnchorTool t = m_ff.getVertexTool(i);
			GeodeticCoords p = ((AbstractPosTool)t).getGeoPos();
			GeodeticCoords q = m_util.pixToPos(pts[i].getX(), pts[i].getY());
			assertEquals(true,p.equals(q));
		}
		
		AbstractPosTool t =  m_ff.getLastPosTool();
		GeodeticCoords p = t.getGeoPos();
		GeodeticCoords q = m_util.pixToPos(pts[3].getX(), pts[3].getY());
		assertEquals(true,p.equals(q));
		
		IAnchorTool tool =  m_ff.getLastVertexTool();
		AbstractPosTool abTool =  m_ff.getLastPosTool();

		assertEquals(tool,abTool);
	}
	
	@Test
	public void moveVertexTest(){	
		ViewCoords[] pts = new ViewCoords[4];
		pts[0] = new ViewCoords(300,200);
		pts[1] = new ViewCoords(350,150);
		pts[2] = new ViewCoords(450,200);
		pts[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			if ( i != 2 ){
				m_ff.addVertex(pts[i].getX(),pts[i].getY());
			}
		}
		m_ff.insertVertex(2, pts[2].getX(),pts[2].getY());
		
		// move free form
		GeodeticCoords gc = m_util.pixToPos(450, 200);
		IAnchorTool tool = m_ff.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (475, 150);
		tool.handleMouseUp (475, 150);

		// create moved positions and check for anchor selections
		GeodeticCoords pos = m_util.pixToPos(475, 150);
		IAnchorTool tool2 = m_ff.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		return;
	}
	
	@Test
	public void movePolygonTest(){	
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pos[i]);
		}
		
		// move free form
		GeodeticCoords gc = m_conv.viewToGeodetic(new ViewCoords(280,200));
		IAnchorTool tool = m_ff.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (350, 150);
		tool.handleMouseUp (350, 150);
		int moveX = 70;
		int moveY = -50;
		// create moved positions and check for anchor selections
		pos[0] = m_util.pixToPos(300+moveX,200+moveY);
		pos[1] = m_util.pixToPos(350+moveX,150+moveY);
		pos[2] = m_util.pixToPos(450+moveX,200+moveY);
		pos[3] = m_util.pixToPos(500+moveX,250+moveY);
		for (int i = 0; i < 4; i++){
			IAnchorTool u = m_ff.getVertexTool(i);
			IAnchorTool v = m_ff.getAnchorByPosition(pos[i]);
			assertEquals(u,v);
		}
		return;
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
		ctm.createFile("polygon");
		IContext ct = m_canvas.getContext();
		m_ff.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("polygon");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_ff.render(ct);
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
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pos[i]);
		}
		
		boolean bWrite = false;
		if (bWrite){
			writeFile();
		}else{
			compareToFile();
		}
	}
	
	@Test
	public void drawHandleTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_ff.addVertex(pos[i]);
		}
		
		
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_ff.drawHandles(ct);
	}
}
