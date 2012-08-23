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
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.RngBrg;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class BoxTest {
	protected static int TL = 0;
	protected static int TR = 1;
	protected static int BR = 2;
	protected static int BL = 3;
	
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Box m_box;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	

	
	private ViewCoords compViewPt( GeodeticCoords cent, double rotBrg, 
								   double a, double b, int tlbr) {
		double theta = Func.RadToDeg(Math.atan2(b, a));
		double rngKm = Math.sqrt(a * a + b * b);
		GeodeticCoords gc = null;
		if(tlbr == TL){
			gc = m_rb.gcPointFrom(cent, rotBrg - 180 + theta, rngKm);
		} 
		else if (tlbr == TR){
			gc = m_rb.gcPointFrom(cent, rotBrg - theta, rngKm);
		}
		else if (tlbr == BR){
			gc = m_rb.gcPointFrom(cent, rotBrg + theta, rngKm);
		}
		else {
			gc = m_rb.gcPointFrom(cent, rotBrg + 180 - theta, rngKm);
		}
		return m_conv.geodeticToView(gc);
	}
	
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		m_box = new Box();
		m_box.setCoordConverter(m_conv);
		
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords gc = m_conv.viewToGeodetic(new ViewCoords(300,200));
		RngBrg rb = m_util.pixPointsToRngBrg(300, 200, 400, 200);
		Bearing brg = Bearing.builder(). value(rb.getBearing()).degrees().build();
		Distance smj = Distance.builder().value(rb.getRanegKm()).kilometers().build();
		Distance smn = Distance.builder().value(rb.getRanegKm()/2).kilometers().build();
		Box box = (Box) Box.create(m_conv, gc, brg, smj, smn);
		GeodeticCoords gc2 = box.getCenter();
		assertEquals(gc,gc2);
		
		GeodeticCoords pos = m_util.pixToPos(300, 200);
		IAnchorTool tool = box.getCenterAnchorTool();
		IAnchorTool tool2 = box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		pos = m_util.pixToPos(400, 200);
		tool = box.getSmjAnchorTool();
		tool2 = box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		pos = m_util.pixToPos(300, 150);
		tool = box.getSmnAnchorTool();
		tool2 = box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		return;
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_box.setCenter(cent);
		m_box.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_box.getSmjPos();
		double disKm = m_rb.gcDistanceFromTo(cent,gc);
		double rotBrg = m_rb.gcBearingFromTo(cent,gc);
		
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_box.setSmnAxis(dis);
		// Checking the corner points
		ViewCoords pt = compViewPt(cent, rotBrg, disKm, disKm/2, TL);
		gc = m_util.pixToPos(pt.getX(), pt.getY());
		boolean bTouches = m_box.positionTouches(gc);
		assertEquals(true,bTouches);
		
		pt = compViewPt(cent, rotBrg, disKm, disKm/2, TR);
		gc = m_util.pixToPos(pt.getX(), pt.getY());
		bTouches = m_box.positionTouches(gc);
		assertEquals(true,bTouches);
		
		pt = compViewPt(cent, rotBrg, disKm, disKm/2, BR);
		gc = m_util.pixToPos(pt.getX(), pt.getY());
		bTouches = m_box.positionTouches(gc);
		assertEquals(true,bTouches);
		
		pt = compViewPt(cent, rotBrg, disKm, disKm/2, BL);
		gc = m_util.pixToPos(pt.getX(), pt.getY());
		bTouches = m_box.positionTouches(gc);
		assertEquals(true,bTouches);
	}

	@Test
	public void mouseMoveSemiMinorTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_box.setCenter(cent);
		m_box.setSmjFromPix(400, 200);
		GeodeticCoords pos = m_util.pixToPos(300, 150);
		double disKm = m_rb.gcDistanceFromTo(cent,pos);
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_box.setSmnAxis(dis);
		
		IAnchorTool tool = m_box.getSmnAnchorTool();
		tool.handleMouseMove(300, 100);
		tool.handleMouseUp(300, 100);
		//Test for semi-minor movement
		pos = m_box.getSmnPos();
		GeodeticCoords testPos = m_util.pixToPos(300,100);
		assertEquals(pos.getPhi(AngleUnit.DEGREES),
					 testPos.getPhi(AngleUnit.DEGREES),0.0001);
		assertEquals(pos.getLambda(AngleUnit.DEGREES),
				 	 testPos.getLambda(AngleUnit.DEGREES),0.0001);
	}

	@Test
	public void mouseMoveSemiMajorTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_box.setCenter(cent);
		m_box.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_box.getSmjPos();
		double disKm = m_rb.gcDistanceFromTo(cent,gc);
		Distance dis = Distance.builder().value(disKm/2).kilometers().build();
		m_box.setSmnAxis(dis);
		
		int len = (int)(100*Math.sqrt(2));
		IAnchorTool tool = m_box.getSmjAnchorTool();
		tool.handleMouseMove( 300 + len, 200-len);
		tool.handleMouseUp( 300 + len, 200-len);
		//Test for semi-minor movement
		GeodeticCoords pos = m_box.getSmjPos();
		RngBrg rb = m_rb.gcRngBrgFromTo(cent, pos);
		double brg = Func.wrap360(rb.getBearing()- 90);
		gc = m_rb.gcPointFrom(cent, brg, disKm/2);
		tool = m_box.getSmnAnchorTool();
		IAnchorTool tool2 = m_box.getAnchorByPosition(gc);
		assertEquals(tool,tool2);
	}
	

	@Test
	public void mouseMoveAndUpCenterTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_box.setCenter(cent);
		m_box.setSmjFromPix(400, 200);
		GeodeticCoords gc = m_box.getSmjPos();
		double disKm = m_rb.gcDistanceFromTo(cent,gc)/2;
		Distance dis = Distance.builder().value(disKm).kilometers().build();
		m_box.setSmnAxis(dis);
		IAnchorTool tool = m_box.getCenterAnchorTool();
		tool.handleMouseMove (350, 200);
		tool.handleMouseUp (350, 200);
		// test center anchor
		GeodeticCoords pos = m_util.pixToPos(350,200);;
		IAnchorTool tool2 = m_box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// test semi-minor anchor
		tool = m_box.getSmnAnchorTool();
		pos = m_conv.viewToGeodetic(new ViewCoords(350,150));
		tool2 = m_box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// test semi-major anchor
		tool = m_box.getSmjAnchorTool();
		pos = m_conv.viewToGeodetic(new ViewCoords(450,200));
		tool2 = m_box.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}
}
