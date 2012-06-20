/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class DivWorkerTest {
	private DivWorker m_dw = new DivWorker();
	private ViewWorker m_vw = new ViewWorker();
	private DivDimensions m_dims = new DivDimensions();
	private ViewDimension m_viewDims = new ViewDimension(600, 400);
	private IProjection m_proj, m_mapProj;
	
	@Before
	public void before() throws Exception {
		// DivPanel projection
		m_proj = Projection.createProj(IProjection.T.CylEquiDist);
		m_proj.initialize(512, 180, 180);
		// DivPanel DivWorker
		m_dw.setProjection(m_proj);
		// Map view projection
		m_mapProj = Projection.createProj(IProjection.T.CylEquiDist);
		m_mapProj.initialize(512, 180, 180);
		// Initialize map view worker
		m_vw.intialize(m_viewDims, m_mapProj);
		//set view and divPanel center.
		m_vw.setGeoCenter(new GeodeticCoords());
		m_dw.setDiv(m_vw.getGeoCenter());
	}
	
	
	public DivWorkerTest(){
	}
	
	private void zoomIn2X(){
		double eqScale = m_mapProj.getEquatorialScale()*2.0;
		m_mapProj.setEquatorialScale(eqScale);
	}
	
	private void zoomOut2X(){
		double eqScale = m_mapProj.getEquatorialScale()*0.5;
		m_mapProj.setEquatorialScale(eqScale);
	}
	
	@Test
	public void computeOffsetTest(){
		zoomIn2X();
		ViewCoords pix = m_dw.computeDivLayoutInView(m_mapProj, m_vw, m_dims);
		assertEquals(pix.getX(),-900);
		assertEquals(pix.getY(),-600);
		zoomOut2X();
	}
	
	@Test
	public void computeImageSpanTest(){
		DivWorker.ImageBounds b = DivWorker.newImageBounds();
		b.left   = Integer.MAX_VALUE;
		b.right  = Integer.MIN_VALUE;
		b.top    = Integer.MAX_VALUE;
		b.bottom = Integer.MIN_VALUE;
		
		TileCoords[] tc = new TileCoords[2];
		tc[0] = new TileCoords(5, 5, 0, 0);
		tc[1] = new TileCoords(10, 10, 0, 0);
		for (int i = 0; i < tc.length; i++) {
			m_dw.computeImageBounds(tc[i], m_dims, b);
		}
		assertEquals(5,b.left);
		assertEquals(5,b.top);
		assertEquals(522,b.right);
		assertEquals(522,b.bottom);
	}
	
	@Test
	public void geodeticToDivTest(){
		GeodeticCoords gc = new GeodeticCoords(90,0,AngleUnit.DEGREES);
		WorldCoords wc = m_dw.geodeticToWc(gc);
		DivCoords dc =  m_dw.worldToDiv(wc);
		assertEquals(856,dc.getX());
	}
	
	@Test
	public void hasDivMovedToFarTest(){
		DivCoordSpan ds = new DivCoordSpan(0, 0, 800, 1200);
		boolean bMovedToFar = m_dw.hasDivMovedTooFar(m_mapProj, m_vw, ds);
		assertEquals(false,bMovedToFar);
		// moved left to0 far
		ds = new DivCoordSpan(0, -400, 800, -400+1200);
		bMovedToFar = m_dw.hasDivMovedTooFar(m_mapProj, m_vw, ds);
		assertEquals(true,bMovedToFar);
		// moved right too far
		ds = new DivCoordSpan(400, 0, 800, 400+1200);
		bMovedToFar = m_dw.hasDivMovedTooFar(m_mapProj, m_vw, ds);
		assertEquals(true,bMovedToFar);
		// moved down too far
		ds = new DivCoordSpan(210, 0, 210+800, 1200);
		bMovedToFar = m_dw.hasDivMovedTooFar(m_mapProj, m_vw, ds);
		assertEquals(true,bMovedToFar);
		// moved up too far
		ds = new DivCoordSpan(-210, 0, -210+800, 1200);
		bMovedToFar = m_dw.hasDivMovedTooFar(m_mapProj, m_vw, ds);
		assertEquals(true,bMovedToFar);
	}
}
