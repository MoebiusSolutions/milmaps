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

public class ShapeEditorTest {
	MapViewMock m_map = new MapViewMock();
	ShapeEditor m_se;
	private Util m_util = new Util(m_map.m_conv,MapViewMock.m_rb);
	
	@Before
	public void before() throws Exception {
		m_se = new ShapeEditor(m_map);
	}
	
	@Test
	public void getcanvasTest(){
		ICanvasTool ct = m_se.getCanvasTool();
		assertEquals(true, ct != null);
		IContext ict = ct.getContext();
		assertEquals(true, ict != null);
		ICoordConverter conv = m_se.getConverter();
		assertEquals(true, conv != null);
		m_se.setEventFocus(true);
		assertEquals(true,m_map.m_editorEventFocus);
	}
	
	@Test
	public void circleToolHandleMouseTest(){
		NewCircleTool tool = new NewCircleTool(m_se);
		m_se.setShapeTool(tool);
		m_se.handleMouseDown(300, 200);
		tool.handleMouseMove(300,50);
		tool.handleMouseUp(300, 100);
		IShapeTool st = m_se.getShapeTool();
		IShape shape = st.getShape();
		IShape shape1 = m_se.getShapes().get(0);
		assertEquals(shape,shape1);
	}
	
	@Test
	public void deleteSelectedShapesTest(){
		NewCircleTool tool = new NewCircleTool(m_se);
		m_se.setShapeTool(tool);
		m_se.handleMouseDown(300, 200);
		m_se.handleMouseMove(300,50);
		m_se.handleMouseUp(300, 100);
		IShape shape = m_se.getShapes().get(0);
		m_se.addShape(shape);
		int size = m_se.getShapes().size();
		assertEquals(2,size);
		m_se.selectAllShapes();
		m_se.deselectAllShapes();
		m_se.deleteSelectedShapes();
		size = m_se.getShapes().size();
		assertEquals(2,size);
		m_se.selectAllShapes();
		m_se.deleteSelectedShapes();
		size = m_se.getShapes().size();
		assertEquals(0,size);
	}
	
	@Test
	public void dblClickHandleMouseTest(){
		NewFreeFormTool tool = new NewFreeFormTool(m_se);
		m_se.setShapeTool(tool);
		m_se.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		FreeForm s  = (FreeForm)(m_se.getShapes().get(0));

		m_se.handleMouseMove(300,50);
		m_se.handleMouseDown(300,50);
		m_se.handleMouseUp(300,50);
		
		m_se.handleMouseMove(350,50);
		m_se.handleMouseDown(350,50);
		m_se.handleMouseUp(350,50);
		
		m_se.handleMouseMove(400,200);
		m_se.handleMouseDblClick(400, 200);
		
		boolean needsUpdate = m_se.needsUpdate();
		assertEquals(true,needsUpdate);

		IShape iShape = s;
		IShape shape = m_se.getShapes().get(0);
		assertEquals(iShape,shape);
		
		IAnchorTool iat =  s.getLastVertexTool();
		AbstractPosTool abTool =  s.getLastPosTool();

		assertEquals(iat,abTool);
	}
	
	@Test
	public void keyDownUpTest(){
		NewFreeFormTool tool = new NewFreeFormTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);
		FreeForm ff  = (FreeForm)(tool.getShape());

		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseDown(400,200);
		tool.handleMouseUp(400,200);
		tool.handleMouseDblClick(400, 200);
		// Select objects vertex
		m_se.handleMouseDown(300,50);
		
		// Check for vertex Anchor tool
		m_se.handleMouseDown(300, 50);
		boolean bSelected = m_se.getShapes().get(0).isSelected();
		assertEquals(true,bSelected);
		
		m_se.keyDownCode(KeyCodes.KEY_CTRL);
		 
		
		// write test for deleting and inserting points
		// Insert Object between vertex 0 and 1
		m_se.keyDownCode(KeyCodes.KEY_CTRL);
		m_se.handleMouseDown(300,100);
		m_se.handleMouseUp(300,100);
		IAnchorTool t = ff.getVertexTool(1);
		IAnchorTool t2 = ff.getAnchorByPosition(m_util.pixToPos(300, 100));
		assertEquals(t,t2);
		// Delete vertex 1.
		m_se.keyDownCode(KeyCodes.KEY_SHIFT);
		m_se.handleMouseDown(300,100);
		m_se.handleMouseUp(300,100);
		t = ff.getVertexTool(1);
		t2 = ff.getAnchorByPosition(m_util.pixToPos(300,50));
		assertEquals(t,t2);
		
		m_se.keyUpCode(KeyCodes.KEY_CTRL);
		m_se.keyUpCode(KeyCodes.KEY_SHIFT);
	}
}
