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

public class SelectShapeTest {
	MapViewMock m_map = new MapViewMock();
	ShapeEditor m_se;
	private Util m_util = new Util(m_map.m_conv,MapViewMock.m_rb);
	
	@Before
	public void before() throws Exception {
		m_se = new ShapeEditor(m_map);
	}
	
	@Test
	public void selectShapeTest(){
		NewFreeFormTool tool = new NewFreeFormTool(m_se);
		tool.handleMouseDown(300, 200);
		tool.handleMouseUp(300, 200);

		tool.handleMouseDown(300,50);
		tool.handleMouseUp(300,50);
		
		tool.handleMouseDown(350,50);
		tool.handleMouseUp(350,50);
		
		tool.handleMouseDown(400,200);
		tool.handleMouseUp(400,200);
		tool.handleMouseDblClick(400, 200);
		
		SelectShape ss = new SelectShape(m_se);
		// Select objects vertex
		m_se.handleMouseDown(300, 50);
		ss.handleMouseUp(300,50);
		
		boolean bSelected = m_se.getShapes().get(0).isSelected();
		assertEquals(true,bSelected);
		
		m_se.handleMouseDown(100, 50);
		ss.handleMouseUp(100,50);
		bSelected = m_se.getShapes().get(0).isSelected();
		assertEquals(false,bSelected);
	}
}
