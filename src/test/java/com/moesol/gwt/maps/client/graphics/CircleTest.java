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
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;


/*
 * Test the following methods:
 * * 
 */

public class CircleTest {//extends GwtTest{
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Circle m_cir;
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private ICanvasTool m_canvas = new CanvasToolMock();
	private List<Point> m_list = new ArrayList<Point>();
	
	//@Override
	//public String getModuleName() {
	//	return "com.moesol.gwt.maps.Maps";
	//}
	
	@Before
	public void before() throws Exception {
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
		return;
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_cir.createEditTool(se);
		assertEquals(true, st != null);
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
		int x = 300 + (int)(50*Math.cos(Math.PI/4));
		int y = 200 - (int)(50*Math.sin(Math.PI/4));
		boolean bTouches = m_cir.ptCloseToEdge(x, y, 1);
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

		IAnchorTool tool = m_cir.getRadiusAnchorTool();
		tool.handleMouseMove(350, 200);
		IAnchorTool tool2 = m_cir.getAnchorByPosition(radGc);
		assertEquals(tool,tool2);
	}
	
	@Test
	public void mouseMoveCenterTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		IAnchorTool tool = m_cir.getCenterAnchorTool();
		tool.handleMouseMove (350, 200);
		GeodeticCoords pos = m_conv.viewToGeodetic(new ViewCoords(350,200));
		IAnchorTool tool2 = m_cir.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
	}

	@Test
	public void mouseUpCenterTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		IAnchorTool tool = m_cir.getCenterAnchorTool();
		tool.handleMouseUp (350, 200);
		GeodeticCoords pos = m_conv.viewToGeodetic(new ViewCoords(350,200));
		IAnchorTool tool2 = m_cir.getAnchorByPosition(pos);
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
		ctm.createFile("circle");
		IContext ct = m_canvas.getContext();
		m_cir.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("circle");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_cir.render(ct);
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
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		
		boolean bWrite = false;
		if (bWrite){
			writeFile();
		}else{
			compareToFile();
		}
		
	}
	
	@Test
	public void drawHandleTest(){
		GeodeticCoords cent = m_conv.viewToGeodetic(new ViewCoords(300,200));
		m_cir.setCenter(cent);
		GeodeticCoords radGc = m_conv.viewToGeodetic(new ViewCoords(350,200));
		m_cir.setRadiusPos(radGc);
		
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_cir.drawHandles(ct);
	}
}
