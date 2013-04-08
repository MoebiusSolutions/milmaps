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

import com.google.gwt.event.dom.client.KeyCodes;
import com.moesol.gwt.maps.client.CylEquiDistProj;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class EditObjectToolTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	//private ICanvasTool m_canvas = new CanvasToolMock();
	IShapeEditor m_se;
	
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);	
		m_se = new ShapeEditorMock();
		m_se.setCoordConverter(m_conv);
	}
	
	// we will test the commonEditTool for one object
	@Test
	public void commonEditToolTest(){
		// first create the circle using the new tool
		NewCircleTool tool = new NewCircleTool(m_se);
		tool.handleMouseDown(300, 200);
		IShape s = tool.getShape();
		Circle cir  = (Circle)(s);
		tool.handleMouseMove(300,50);
		tool.handleMouseUp(300, 100);
		
		CommonEditTool editTool = (CommonEditTool) m_se.getShapeTool();
		IShape shape = editTool.getShape();
		assertEquals(s,shape);
		// select an object
		editTool.handleMouseDown(300, 200);
		IAnchorTool t = editTool.getAnchorTool();
		IAnchorTool t2 = cir.getCenterAnchorTool();
		assertEquals(t,t2);
		
		editTool.handleKeyDown(KeyCodes.KEY_CTRL);
		boolean bKey = cir.getKeyDownFlag(KeyCodes.KEY_CTRL);
		assertEquals(true,bKey);
		
		editTool.handleKeyDown(KeyCodes.KEY_SHIFT);
		bKey = cir.getKeyDownFlag(KeyCodes.KEY_SHIFT);
		assertEquals(true,bKey);
		
		editTool.handleKeyUp(KeyCodes.KEY_CTRL);
		bKey = cir.getKeyDownFlag(KeyCodes.KEY_CTRL);
		assertEquals(false,bKey);
		
		editTool.handleKeyUp(KeyCodes.KEY_SHIFT);
		bKey = cir.getKeyDownFlag(KeyCodes.KEY_SHIFT);
		assertEquals(false,bKey);
		
		// deselect an objbect
		editTool.handleMouseDown(300, 50);
		t = editTool.getAnchorTool();
		//IAnchorTool t2 = cir.getCenterAnchorTool();
		assertEquals(null,t);
		// Coverage count
		editTool.handleMouseOut(1000, 1000);
		
		// Check for Radius Anchor tool
		editTool.handleMouseDown(300, 100);
		t = editTool.getAnchorTool();
		t2 = cir.getRadiusAnchorTool();
		assertEquals(t,t2);
		
		editTool.handleMouseMove(400, 100);
		editTool.handleMouseUp(400, 100); 
		GeodeticCoords gc = m_util.pixToPos(400, 100);
		boolean bSelected = t.isSlected(gc);
		assertEquals(true,bSelected);
	}
	
	@Test
	public void FreeXEditToolTest(){
		NewPolygonTool tool = new NewPolygonTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		Polygon ff  = (Polygon)(tool.getShape());

		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseDown(400,200);
		tool.handleMouseUp(400,200);
		tool.handleMouseDblClick(400, 200);
		
		PolygonEditTool editTool = (PolygonEditTool) m_se.getShapeTool();
		IShape shape = editTool.getShape();
		IShape s = editTool.getShape();
		assertEquals(s,shape);
		// select an object
		
		editTool.handleMouseDown(300, 200);
		editTool.handleMouseMove(300, 200);
		editTool.handleMouseUp(300, 200);
		IAnchorTool t = editTool.getAnchorTool();
		IAnchorTool t2 = ff.getVertexTool(0);
		assertEquals(t,t2);
		
		editTool.handleKeyDown(KeyCodes.KEY_CTRL);
		boolean bKey = editTool.getKeyDownFlag(KeyCodes.KEY_CTRL);
		assertEquals(true,bKey);
		
		editTool.handleKeyDown(KeyCodes.KEY_SHIFT);
		bKey = editTool.getKeyDownFlag(KeyCodes.KEY_SHIFT);
		assertEquals(true,bKey);
		
		editTool.handleKeyUp(KeyCodes.KEY_CTRL);
		bKey = editTool.getKeyDownFlag(KeyCodes.KEY_CTRL);
		assertEquals(false,bKey);
		
		editTool.handleKeyUp(KeyCodes.KEY_SHIFT);
		bKey = editTool.getKeyDownFlag(KeyCodes.KEY_SHIFT);
		assertEquals(false,bKey);
		
		// Check for vertex Anchor tool
		editTool.handleMouseDown(300, 50);
		t = editTool.getAnchorTool();
		t2 = ff.getVertexTool(1);
		assertEquals(t,t2);
		 
		boolean bSelected = t.isSlected(m_util.pixToPos(300, 50));
		assertEquals(true,bSelected);
		
		// write test for deleting and inserting points
		// Insert Object between vertex 0 and 1
		editTool.handleKeyDown(KeyCodes.KEY_CTRL);
		editTool.handleMouseDown(300,100);
		editTool.handleMouseUp(300,100);
		t = ff.getVertexTool(1);
		t2 = ff.getAnchorByPosition(m_util.pixToPos(300, 100));
		assertEquals(t,t2);
		// Delete vertex 1.
		editTool.handleKeyDown(KeyCodes.KEY_SHIFT);
		editTool.handleMouseDown(300,100);
		editTool.handleMouseUp(300,100);
		t = ff.getVertexTool(1);
		t2 = ff.getAnchorByPosition(m_util.pixToPos(300,50));
		assertEquals(t,t2);
		
		editTool.handleKeyUp(KeyCodes.KEY_CTRL);
		editTool.handleKeyUp(KeyCodes.KEY_SHIFT);

		assertEquals(false,bKey);
		// deselect an objbect
		editTool.handleMouseDown(300, 90);
		t = editTool.getAnchorTool();
		//IAnchorTool t2 = cir.getCenterAnchorTool();
		assertEquals(null,t);
		// Coverage count
		editTool.handleMouseOut(1000, 1000);
	}
}
