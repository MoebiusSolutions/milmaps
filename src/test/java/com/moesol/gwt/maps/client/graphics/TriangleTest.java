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

public class TriangleTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Triangle m_triangle;
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
		m_triangle = new Triangle();
		m_triangle.setCoordConverter(m_conv);
		// add vertices using pixels
		m_triangle.addVertex(300, 200);
		m_triangle.addVertex(300, 150);
		m_triangle.addVertex(350, 200);		
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[3];
		pos[0]= m_util.pixToPos(300, 200);
		pos[1]= m_util.pixToPos(300, 150);
		pos[2]= m_util.pixToPos(350, 200);

		Triangle tri = (Triangle)Triangle.create(m_conv, pos);
	
		
		IAnchorTool u = tri.getVertexTool(0);
		IAnchorTool v = tri.getAnchorByPosition(pos[0]);
		assertEquals(u,v);

		u = tri.getVertexTool(1);
		v = tri.getAnchorByPosition(pos[1]);
		assertEquals(u,v);
		
		u = tri.getVertexTool(2);
		v = tri.getAnchorByPosition(pos[2]);
		assertEquals(u,v);
		return;
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_triangle.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void ptCloseToEdgeTest(){

		boolean bTouches = m_triangle.ptCloseToEdge(300, 175, 1.5);
		assertEquals(true,bTouches);
		
	}
	
	@Test
	public void setVertextPtsTest(){
		GeodeticCoords[] pos = new GeodeticCoords[3];
		pos[0]= m_util.pixToPos(300, 200);
		pos[1]= m_util.pixToPos(300, 150);
		pos[2]= m_util.pixToPos(350, 200);
		m_triangle.addVerteces(pos);
		
		boolean bTouches = m_triangle.positionTouches(pos[0]);
		assertEquals(true,bTouches);
		
		bTouches = m_triangle.positionTouches(pos[1]);
		assertEquals(true,bTouches);

		bTouches = m_triangle.positionTouches(pos[2]);
		assertEquals(true,bTouches);
		
		// vertex 0 -> vertex 1
		GeodeticCoords p = m_util.pixToPos(300,175);
		bTouches = m_triangle.positionTouches(p);
		assertEquals(true,bTouches);
		
		// vertex 1 -> vertex 2
		p = m_util.pixToPos(325,175);
		bTouches = m_triangle.positionTouches(p);
		assertEquals(true,bTouches);
		
		// vertex 2 -> vertex 0
		p = m_util.pixToPos(325,200);
		bTouches = m_triangle.positionTouches(p);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void moverectTest(){	
		// move line
		GeodeticCoords gc = m_util.pixToPos(300, 200);
		IAnchorTool tool = m_triangle.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (475, 150);
		tool.handleMouseUp (475, 150);

		// create moved positions and check for anchor selections
		GeodeticCoords pos = m_util.pixToPos(475, 150);
		IAnchorTool tool2 = m_triangle.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		return;
	}
	
	@Test
	public void moveWholeTriangleTest(){	
		
		// move whole line
		GeodeticCoords gc = m_util.pixToPos(280,200);
		IAnchorTool tool = m_triangle.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (350, 150);
		tool.handleMouseUp (350, 150);
		int moveX = 70;
		int moveY = -50;
		// create moved positions and check for anchor selections
		gc = m_util.pixToPos(300+moveX,200+moveY);
		//GeodeticCoords end = m_util.pixToPos(350+moveX,150+moveY);
		
		IAnchorTool u = m_triangle.getVertexTool(0);
		IAnchorTool v = m_triangle.getAnchorByPosition(gc);
		assertEquals(u,v);
		
		//u = m_rect.getEndAnchorTool();
		//v = m_rect.getAnchorByPosition(end);
		//assertEquals(u,v);

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
		ctm.createFile("triangle");
		IContext ct = m_canvas.getContext();
		m_triangle.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("triangle");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_triangle.render(ct);
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
		boolean bWrite = false;
		if (bWrite){
			writeFile();
		}else{
			compareToFile();
		}
	}
	
	@Test
	public void drawHandleTest(){	
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		IContext ct = m_canvas.getContext();
		m_triangle.drawHandles(ct);
	}
}
