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
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.server.units.JvmMapScale;

public class NewArcToolTest {
	protected static final RangeBearingS m_rb = new RangeBearingS();
	private ViewPort viewPort = new ViewPort();
	private IProjection proj;
	private Converter m_conv;
	private Util m_util;
	private ICanvasTool m_canvas = new CanvasToolMock();
	private NewArcTool m_tool;
	@Before
	public void before() throws Exception {
		JvmMapScale.init();
		proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
		m_conv = new Converter(viewPort);
		m_util = new Util(m_conv,m_rb);
		IShapeEditor se = new ShapeEditorMock();	
		se.setCoordConverter(m_conv);
		m_tool = new NewArcTool(se);
		
	}
	
	@Test
	public void handleMouseDownTest(){
		m_tool.handleMouseDown(300, 200);
		Arc arc  = (Arc)(m_tool.getShape());
		GeodeticCoords  gc = arc.getCenterTool().getGeoPos();
		assertEquals(0.0,gc.getPhi(AngleUnit.DEGREES),0.5);
		assertEquals(0.0,gc.getLambda(AngleUnit.DEGREES),0.5);
	}
}
