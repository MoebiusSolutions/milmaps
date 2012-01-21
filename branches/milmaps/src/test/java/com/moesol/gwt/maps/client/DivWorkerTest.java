package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DivWorkerTest {
	private DivWorker m_dw = new DivWorker();
	private ViewWorker m_vw = new ViewWorker();
	private DivDimensions m_dims = new DivDimensions();
	private ViewDimension m_viewDims = new ViewDimension(600, 400);
	private IProjection m_proj, m_mapProj;
	
	@Before
	public void setUp() throws Exception {
		// DivPanel projection
		m_proj = Projection.createProj(IProjection.T.CylEquiDist);
		m_proj.initialize(256, 90, 90);
		// DivPanel DivWorker
		m_dw.setProjection(m_proj);
		// Map view projection
		m_mapProj = Projection.createProj(IProjection.T.CylEquiDist);
		m_mapProj.initialize(256, 90, 90);
		// Initialize map view worker
		m_vw.intialize(m_viewDims, m_mapProj);
		//set view and divPanel center.
		GeodeticCoords gc = new GeodeticCoords();
		m_vw.setGeoCenter(gc);
		m_vw.update(true);
		m_dw.setDiv(gc);
	}
	
	
	public DivWorkerTest(){

	}
	
	private void zoomIn2X(){
		double eqScale = m_mapProj.getEquatorialScale()*2;
		m_mapProj.setEquatorialScale(eqScale);
		m_vw.update(true);	
	}
	
	@Test
	public void computeOffsetTest(){
		zoomIn2X();
		PixelXY pix = m_dw.computeDivLayoutInView(m_mapProj, m_vw, m_dims);
		assertEquals(pix.m_x,-900);
		assertEquals(pix.m_y,-600);
	}
}
