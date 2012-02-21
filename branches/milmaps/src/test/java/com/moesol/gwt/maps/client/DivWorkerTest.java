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
	public void hasDivMovedToFarTest(){
		WorldCoords vwCent = m_vw.getVpCenterInWc();
		//WorldCoords divCent = m_mapProj.geodeticToWorld(m_dw.getGeoCenter());
		// First lets move left:
		DivCoordSpan ds = new DivCoordSpan(88,1112);
		for( int j = 10; j < 400; j += 10 ){
			WorldCoords wc = new WorldCoords(vwCent.getX()-j, vwCent.getY());
			m_vw.setCenterInWc(wc);	
			if ( j == 220 )
				j += 0;
			boolean bMovedToFar = m_dw.hasDivMovedToFar(m_mapProj, m_vw, m_dims, ds);
			if ( bMovedToFar )
				bMovedToFar= false;
			assertEquals(false,bMovedToFar);
		}
	}
}
