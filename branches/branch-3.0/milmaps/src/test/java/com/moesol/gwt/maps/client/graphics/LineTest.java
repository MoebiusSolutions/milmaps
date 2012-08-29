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

public class LineTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	protected Line m_line;
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
		m_line = new Line();
		m_line.setCoordConverter(m_conv);
		
		GeodeticCoords start = m_util.pixToPos(300,200);
		GeodeticCoords end = m_util.pixToPos(350,150);
		
		m_line.setStartPos(start);
		m_line.setEndPos(end);
	}
	
	@Test
	public void creatIShapeTest(){
		GeodeticCoords[] pos = new GeodeticCoords[2];
		for ( int i = 0; i < 2; i++){
			pos[i] = new GeodeticCoords(-120 + 4*i, 34 - 4*i, AngleUnit.DEGREES); 
		}
		Line line = (Line)Line.create(m_conv, pos[0],pos[1]);
		
		IAnchorTool u = line.getStartAnchorTool();
		IAnchorTool v = line.getAnchorByPosition(pos[0]);
		assertEquals(u,v);

		u = line.getEndAnchorTool();
		v = line.getAnchorByPosition(pos[1]);
		assertEquals(u,v);
		return;
	}
	
	@Test
	public void creatEditToolTest(){
		IShapeEditor se = new ShapeEditorMock();
		IShapeTool st = m_line.createEditTool(se);
		assertEquals(true, st != null);
	}
	
	@Test
	public void ptCloseToEdgeTest(){

		boolean bTouches = m_line.ptClose(325, 175, 1.5);
		assertEquals(true,bTouches);
		
	}
	
	@Test
	public void positionTouchesTest(){
		GeodeticCoords pos = m_util.pixToPos(300,200);
		
		boolean bTouches = m_line.positionTouches(pos);
		assertEquals(true,bTouches);
	}
	
	@Test
	public void movePointTest(){	
		// move line
		GeodeticCoords gc = m_util.pixToPos(300, 200);
		IAnchorTool tool = m_line.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (475, 150);
		tool.handleMouseUp (475, 150);

		// create moved positions and check for anchor selections
		GeodeticCoords pos = m_util.pixToPos(475, 150);
		IAnchorTool tool2 = m_line.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		gc = m_util.pixToPos(350, 150);
		tool = m_line.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (475, 150);
		tool.handleMouseUp (475, 150);

		// create moved positions and check for anchor selections
		pos = m_util.pixToPos(475, 150);
		tool2 = m_line.getAnchorByPosition(pos);
		assertEquals(tool,tool2);
		
		return;
	}

	@Test
	public void moveWholeLineTest(){	
		
		// move whole line
		GeodeticCoords gc = m_util.pixToPos(280,200);
		IAnchorTool tool = m_line.getAnchorByPosition(gc);
		assertEquals(false,tool == null);
		tool.handleMouseMove (350, 150);
		tool.handleMouseUp (350, 150);
		int moveX = 70;
		int moveY = -50;
		// create moved positions and check for anchor selections
		GeodeticCoords start = m_util.pixToPos(300+moveX,200+moveY);
		//GeodeticCoords end = m_util.pixToPos(350+moveX,150+moveY);
		
		IAnchorTool u = m_line.getStartAnchorTool();
		IAnchorTool v = m_line.getAnchorByPosition(start);
		assertEquals(u,v);
		
		//u = m_line.getEndAnchorTool();
		//v = m_line.getAnchorByPosition(end);
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
		ctm.createFile("line");
		IContext ct = m_canvas.getContext();
		m_line.render(ct);
		ctm.closeFile();
	}
	
	protected void compareToFile(){
		CanvasToolMock ctm = (CanvasToolMock)m_canvas;
		ctm.setWriteFlag(false);
		ctm.readFile("line");
		copyList(ctm.getList());
		ctm.clearList();
		IContext ct = m_canvas.getContext();
		m_line.render(ct);
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
		m_line.drawHandles(ct);
	}
}
