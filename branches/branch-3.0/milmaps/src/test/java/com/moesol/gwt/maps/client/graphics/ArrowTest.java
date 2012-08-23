/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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
import com.moesol.gwt.maps.client.units.Distance;

public class ArrowTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Arrow m_arrow;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	
	@Before
	public void before() throws Exception {
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		m_arrow = new Arrow();
		m_arrow.setCoordConverter(m_conv);
		
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		for ( int i = 0; i < 4; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		Distance width = Distance.builder().value(400).kilometers().build();
		Arrow arrow = (Arrow)Arrow.create(m_conv, width, pos);
		for (int i = 0; i < 4; i++){
			IAnchorTool u = arrow.getVertexTool(i);
			IAnchorTool v = arrow.getAnchorByPosition(pos[i]);
			assertEquals(u,v);
		}
		return;
	}
	
	@Test
	public void ptCloseToEdgeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_arrow.addVertex(pos[i]);
		}
		m_arrow.setWidth(20);
		m_arrow.buildSpline();
		GeodeticCoords p = new GeodeticCoords(-0.125099,4.346933, AngleUnit.DEGREES);
		ViewCoords vc = m_conv.geodeticToView(p);
		boolean bTouches = m_arrow.ptCloseToEdge(vc.getX(), vc.getY(), 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void AddVertexByPixelTest(){
		ViewCoords[] vc = new ViewCoords[4];
		vc[0] = new ViewCoords(300,200);
		vc[1] = new ViewCoords(350,150);
		vc[2] = new ViewCoords(450,200);
		vc[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			m_arrow.addVertex(vc[i].getX(),vc[i].getY());
		}
		double width = m_arrow.getCurrentWidth();
		assertEquals(457.5416,width,0.001);
		m_arrow.setWidth(20);
		m_arrow.buildSpline();
		GeodeticCoords p = new GeodeticCoords(-0.125099,4.346933, AngleUnit.DEGREES);
		boolean bTouches = m_arrow.positionTouches(p);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void InsertVertexByPixelTest(){
		ViewCoords[] vc = new ViewCoords[4];
		vc[0] = new ViewCoords(300,200);
		vc[1] = new ViewCoords(350,150);
		vc[2] = new ViewCoords(450,200);
		vc[3] = new ViewCoords(500,250);
		for(int i = 0; i < 4; i++){
			if ( i != 2 ){
				m_arrow.addVertex(vc[i].getX(),vc[i].getY());
			}
		}
		m_arrow.insertVertex(2, vc[2].getX(),vc[2].getY());
		double width = m_arrow.getCurrentWidth();
		assertEquals(457.5416,width,0.001);
		m_arrow.setWidth(20);
		m_arrow.buildSpline();
		GeodeticCoords p = new GeodeticCoords(-0.125099,4.346933, AngleUnit.DEGREES);
		boolean bTouches = m_arrow.positionTouches(p);
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
			m_arrow.addVertex(pos[i]);
		}
		double width = m_arrow.getCurrentWidth();
		assertEquals(457.5416,width,0.001);
		m_arrow.setWidth(20);
		m_arrow.buildSpline();
		GeodeticCoords p = new GeodeticCoords(-0.125099,4.346933, AngleUnit.DEGREES);
		boolean bTouches = m_arrow.positionTouches(p);
		assertEquals(true,bTouches);
	}
	

	@Test
	public void moveArrowTest(){	
		GeodeticCoords[] pos = new GeodeticCoords[4];
		pos[0] = m_util.pixToPos(300,200);
		pos[1] = m_util.pixToPos(350,150);
		pos[2] = m_util.pixToPos(450,200);
		pos[3] = m_util.pixToPos(500,250);
		for(int i = 0; i < 4; i++){
			m_arrow.addVertex(pos[i]);
		}
		
		// move arrow
		GeodeticCoords gc = m_conv.viewToGeodetic(new ViewCoords(280,200));
		IAnchorTool tool = m_arrow.getAnchorByPosition(gc);
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
			IAnchorTool u = m_arrow.getVertexTool(i);
			IAnchorTool v = m_arrow.getAnchorByPosition(pos[i]);
			assertEquals(u,v);
		}
		return;
	}
}
