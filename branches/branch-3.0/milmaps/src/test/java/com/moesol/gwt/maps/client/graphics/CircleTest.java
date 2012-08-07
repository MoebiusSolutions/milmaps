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
import com.moesol.gwt.maps.client.units.DistanceUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;


/*
 * Test the following methods:
 * * 
 */

public class CircleTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Circle m_cir;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_cir = new Circle();
		m_cir.setCoordConverter(m_conv);
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		Distance dis = new Distance(1500,DistanceUnit.KILOMETERS);
		Circle cir = (Circle)Circle.create(m_conv, gc, dis);
		GeodeticCoords gc2 = cir.getCenter();
		assertEquals(gc,gc2);
		GeodeticCoords pos = m_rb.gcPointFrom(gc, 90, 1500);
		IAnchorTool tool = cir.getRadiusAnchorTool();
		IAnchorTool tool2 = cir.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
	}

	@Test
	public void setGetTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_cir.setCenter(gc);
		GeodeticCoords rtnGc = m_cir.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_cir.setRadiusPos(gc);
		rtnGc = m_cir.getRadiusPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
	}
	
	@Test
	public void withTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_cir.withCenter(gc);
		GeodeticCoords rtnGc = m_cir.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_cir.withRadiusPos(gc);
		rtnGc = m_cir.getRadiusPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
	}
	
	@Test
	public void ptCloseToEdgeTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		boolean bTouches = m_cir.ptCloseToEdge(350, 201, 1);
		assertEquals(true,bTouches);
	}

	@Test
	public void positionTouchesTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		GeodeticCoords pos = m_conv.viewToGeodetic(new ViewCoords(300,250));
		boolean bTouches = m_cir.positionTouches(pos);
		assertEquals(true,bTouches);
	}

	@Test
	public void getAnchorByPositionTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		IAnchorTool tool = m_cir.getCenterAnchorTool();
		IAnchorTool tool2 = m_cir.getAnchorByPosition(cent);
		assertEquals(tool,tool2);
		tool = m_cir.getRadiusAnchorTool();
		tool2 = m_cir.getAnchorByPosition(radGc);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void mouseMoveRadiusTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		m_cir.handleRadiusMouseUp(350, 200);
		IAnchorTool tool = m_cir.getRadiusAnchorTool();
		IAnchorTool tool2 = m_cir.getAnchorByPosition(radGc);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void mouseMoveCenterTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		m_cir.handleCenterMouseUp(350, 200);
		GeodeticCoords pos = m_conv.viewToGeodetic(new ViewCoords(350,200));
		IAnchorTool tool = m_cir.getCenterAnchorTool();
		IAnchorTool tool2 = m_cir.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}
}
