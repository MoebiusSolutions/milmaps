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
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class ArcTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Arc m_arc;
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
		m_arc = new Arc();
		m_arc.setCoordConverter(m_conv);
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords cent = new GeodeticCoords(10,10,AngleUnit.DEGREES);
		Bearing startBrg = Bearing.builder(). value(200).degrees().build();
		Bearing endBrg = Bearing.builder().value(70).degrees().build();
		Distance rad = Distance.builder().value(4000).kilometers().build();
		Arc arc = (Arc)Arc.create(m_conv, cent, startBrg, endBrg, rad);
		GeodeticCoords gc2 = arc.getCenter();
		assertEquals(cent,gc2);
		// check start bearing:
		GeodeticCoords pos = m_rb.gcPointFrom(cent, startBrg.value(AngleUnit.DEGREES),
											  rad.getDistance(DistanceUnit.KILOMETERS));
		IAnchorTool tool = arc.getStartBrgAnchorTool();
		IAnchorTool tool2 = arc.getAnchorByPosition(pos);
		assertEquals(tool,tool2);		
		// check end bearing
		pos = m_rb.gcPointFrom(cent, endBrg.value(AngleUnit.DEGREES),
				  			 rad.getDistance(DistanceUnit.KILOMETERS));
		tool = arc.getEndBrgAnchorTool();
		tool2 = arc.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_arc.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void setGetTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_arc.setCenter(gc);
		GeodeticCoords rtnGc = m_arc.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_arc.setStartBrgPos(gc);
		rtnGc = m_arc.getStartBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_arc.setEndBearingPos(gc);
		rtnGc = m_arc.getEndBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
	}
	
	@Test
	public void withTest() {
		GeodeticCoords gc = new GeodeticCoords(-120, 33, AngleUnit.DEGREES);
		m_arc.withCenter(gc);
		GeodeticCoords rtnGc = m_arc.getCenter();
		boolean bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_arc.withStartBrgPos(gc);
		rtnGc = m_arc.getStartBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
		m_arc.withEndBrgPos(gc);
		rtnGc = m_arc.getEndBrgPos();
		bSame = gc.equals(rtnGc);
		assertEquals(true, bSame);
	}
	
	@Test
	public void ptCloseToEdgeTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);	
		int x = 300 + (int)(50*Math.cos(Math.PI/4));
		int y = 200 - (int)(50*Math.sin(Math.PI/4));
		boolean bTouches = m_arc.ptCloseToEdge(x, y, 1.5);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		int x = 300 + (int)(50*Math.cos(Math.PI/4));
		int y = 200 - (int)(50*Math.sin(Math.PI/4));
		GeodeticCoords pos = m_util.pixToPos(x,y);
		boolean bTouches = m_arc.positionTouches(pos);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void getAnchorByPositionTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		IAnchorTool tool = m_arc.getCenterAnchorTool();
		IAnchorTool tool2 = m_arc.getAnchorByPosition(cent);
		assertEquals(tool,tool2);
		tool = m_arc.getStartBrgAnchorTool();
		tool2 = m_arc.getAnchorByPosition(startBrgPos);
		assertEquals(tool,tool2);
		tool = m_arc.getEndBrgAnchorTool();
		tool2 = m_arc.getAnchorByPosition(endBrgPos);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void moveEndBrgTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		
		// move start bearing
		IAnchorTool tool = m_arc.getEndBrgAnchorTool();
		tool.handleMouseMove (400, 200);
		tool.handleMouseUp (400, 200);
		GeodeticCoords pos = m_util.pixToPos(300,100);
		IAnchorTool tool2 = m_arc.getAnchorByPosition(pos);
		tool = m_arc.getStartBrgAnchorTool();
		assertEquals(tool,tool2);
	}
	
	@Test
	public void moveStartBrgTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		
		// move start bearing
		IAnchorTool tool = m_arc.getStartBrgAnchorTool();
		tool.handleMouseMove (300, 100);
		tool.handleMouseUp (300, 100);
		GeodeticCoords pos = m_util.pixToPos(400,200);
		IAnchorTool tool2 = m_arc.getAnchorByPosition(pos);
		tool = m_arc.getEndBrgAnchorTool();
		assertEquals(tool,tool2);
	}

	@Test
	public void mouseMoveCenterTest(){
		GeodeticCoords cent = m_util.pixToPos(300,200);
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		// move center
		IAnchorTool tool = m_arc.getCenterAnchorTool();
		tool.handleMouseUp (350, 200);
		// check center at new position
		GeodeticCoords pos = m_util.pixToPos(350,200);
		IAnchorTool tool2 = m_arc.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		// check start bearing tool selection
		tool = m_arc.getStartBrgAnchorTool();
		pos = m_util.pixToPos(350,150);
		tool2 = m_arc.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		// check end bearing tool selection
		tool = m_arc.getEndBrgAnchorTool();
		pos = m_util.pixToPos(400,200);
		tool2 = m_arc.getAnchorByPosition(pos);
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
		ctm.createFile("arc");
		IContext ct = m_canvas.getContext();
		m_arc.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("arc");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_arc.render(ct);
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
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		
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
		m_arc.setCenter(cent);
		GeodeticCoords startBrgPos = m_util.pixToPos(300,150);
		m_arc.setStartBearingPos(startBrgPos);
		GeodeticCoords endBrgPos = m_util.pixToPos(350,200);
		m_arc.setEndBearingPos(endBrgPos);
		
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_arc.drawHandles(ct);
	}
}
