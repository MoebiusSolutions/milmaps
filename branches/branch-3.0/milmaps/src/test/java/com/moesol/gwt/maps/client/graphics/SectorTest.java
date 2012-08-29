/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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
import com.moesol.gwt.maps.client.units.DistanceUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class SectorTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Sector m_sec;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	private ICanvasTool m_canvas = new CanvasToolMock();
	private List<Point> m_list = new ArrayList<Point>();
	
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		m_sec = new Sector();
		m_sec.setCoordConverter(m_conv);
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		
		GeodeticCoords start = m_rb.gcPointFrom(cent, 10, 1000);
		GeodeticCoords end = m_rb.gcPointFrom(cent, 100, 1500);
		Sector sec = (Sector)Sector.create(m_conv, cent, start, end);
		
		GeodeticCoords gc2 = sec.getCenter();
		assertEquals(cent,gc2);
		
		double brg = m_rb.gcBearingFromTo(cent, start);
		double rng = m_rb.gcRangeFromTo(cent, start);

		// check start bearing:
		GeodeticCoords pos = m_rb.gcPointFrom(cent, brg, rng);
		IAnchorTool tool = sec.getStartRngBrgAnchorTool();
		IAnchorTool tool2 = sec.getAnchorByPosition(pos);
		assertEquals(tool,tool2);		
		// check end bearing
		brg = m_rb.gcBearingFromTo(cent, end);
		rng = m_rb.gcRangeFromTo(cent, end);
		pos = m_rb.gcPointFrom(cent, brg, rng);
		tool = sec.getEndRngBrgAnchorTool();
		tool2 = sec.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_sec.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void setGetTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_sec.setCenter(gc);
		GeodeticCoords rtnGc = m_sec.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		
		m_sec.setStartRngBrgPos(gc);
		rtnGc = m_sec.getStartRngBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		
		m_sec.setEndRngBrgPos(gc);
		rtnGc = m_sec.getEndRngBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
	}
	
	@Test
	public void withTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_sec.withCenter(gc);
		GeodeticCoords rtnGc = m_sec.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		
		m_sec.withStartRngBrgPos(gc);
		rtnGc = m_sec.getStartRngBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		
		m_sec.withEndRngBrgPos(gc);
		rtnGc = m_sec.getEndRngBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		assertEquals(true, bSame);
	}
	
	@Test
	public void ptCloseToEdgeTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(325,200);
		m_sec.setEndRngBrgPos(endBrgPos);	
		int x = 300 + (int)(50*Math.cos(Math.PI/4));
		int y = 200 - (int)(50*Math.sin(Math.PI/4));
		boolean bTouches = m_sec.ptCloseToEdge(x, y, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_sec.ptCloseToEdge(300, 175, 1.5);
		assertEquals(true,bTouches);
		
		x = 300 + (int)(25*Math.cos(Math.PI/4));
		y = 200 - (int)(25*Math.sin(Math.PI/4));
		bTouches = m_sec.ptCloseToEdge(x, y, 1.5);
		assertEquals(true,bTouches);
		
		bTouches = m_sec.ptCloseToEdge(330, 200, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		int x = 300 + (int)(50*Math.cos(Math.PI/4));
		int y = 200 - (int)(50*Math.sin(Math.PI/4));
		GeodeticCoords pos = m_util.pixToPos(x,y);
		boolean bTouches = m_sec.positionTouches(pos);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void getAnchorByPositionTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		IAnchorTool tool = m_sec.getCenterAnchorTool();
		IAnchorTool tool2 = m_sec.getAnchorByPosition(cent);
		assertEquals(tool,tool2);
		tool = m_sec.getStartRngBrgAnchorTool();
		tool2 = m_sec.getAnchorByPosition(startBrgPos);
		assertEquals(tool,tool2);
		tool = m_sec.getEndRngBrgAnchorTool();
		tool2 = m_sec.getAnchorByPosition(endBrgPos);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void moveStartBrgTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		
		// move end bearing
		IAnchorTool tool = m_sec.getStartRngBrgAnchorTool();
		tool.handleMouseMove (300, 100);
		tool.handleMouseUp (300, 100);
		GeodeticCoords pos = m_util.pixToPos(300, 100);
		IAnchorTool tool2 = m_sec.getAnchorByPosition(pos);
		tool = m_sec.getStartRngBrgAnchorTool();
		assertEquals(tool,tool2);
	}
	
	@Test
	public void moveEndBrgTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		
		// move end bearing
		IAnchorTool tool = m_sec.getEndRngBrgAnchorTool();
		tool.handleMouseMove (400, 200);
		tool.handleMouseUp (400, 200);
		GeodeticCoords pos = m_util.pixToPos(400, 200);
		IAnchorTool tool2 = m_sec.getAnchorByPosition(pos);
		tool = m_sec.getEndRngBrgAnchorTool();
		assertEquals(tool,tool2);
	}
	
	protected GeodeticCoords initBrgPos(int x, int y){
		GeodeticCoords startBrgPos = m_util.pixToPos(x, y);
		GeodeticCoords cenPos = m_sec.getCenter();
		double brgDeg = m_rb.gcBearingFromTo(cenPos, startBrgPos);
		brgDeg = Func.wrap360(brgDeg - 90);
		double disKm = m_rb.gcRangeFromTo(cenPos, startBrgPos);

		return m_rb.gcPointFrom(cenPos, brgDeg, disKm*0.8);
	}
	
	@Test
	public void initialMouseMoveTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		m_sec.initialMouseMove(300, 150);
		GeodeticCoords gc = initBrgPos(300,150);
		GeodeticCoords endGC = m_sec.getEndRngBrgTool().getGeoPos();
		boolean bValue = gc.equals(endGC);
		assertEquals(true,bValue);
	}
	
	@Test
	public void mouseMoveCenterTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		// move center
		IAnchorTool tool = m_sec.getCenterAnchorTool();
		tool.handleMouseMove (350, 200);
		tool.handleMouseUp (350, 200);
		// check center at new position
		GeodeticCoords pos = m_util.pixToPos(350,200);
		IAnchorTool tool2 = m_sec.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// check start bearing tool selection
		tool = m_sec.getStartRngBrgAnchorTool();
		pos = m_util.pixToPos(350,150);
		tool2 = m_sec.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		// check end bearing tool selection
		tool = m_sec.getEndRngBrgAnchorTool();
		pos = m_util.pixToPos(400,200);
		tool2 = m_sec.getAnchorByPosition(pos);
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
		ctm.createFile("sector");
		IContext ct = m_canvas.getContext();
		m_sec.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("sector");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_sec.render(ct);
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
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(325,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		
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
		m_sec.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_sec.setStartRngBrgPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(325,200);
		m_sec.setEndRngBrgPos(endBrgPos);
		
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_sec.drawHandles(ct);
	}
}
