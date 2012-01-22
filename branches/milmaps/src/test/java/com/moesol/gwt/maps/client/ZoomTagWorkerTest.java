package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;


public class ZoomTagWorkerTest {
	private double m_oldScale;
		
	private final ViewDimension m_viewDim = new ViewDimension(600, 400);
	private final WorldCoords m_viewCentInWc = new WorldCoords(512,256); 
	
	private final WorldCoords m_wc = new WorldCoords();
	private final ViewCoords m_vc = new ViewCoords();
	private GeodeticCoords m_gc = new GeodeticCoords();
	
	private final IProjection m_proj = new CylEquiDistProj();//512, 180, 180);
	
	private final ViewWorker m_vpWorker = new ViewWorker();
	private ZoomTagWorker m_ztWorker = null;
	private ZoomTagWorker m_ztWorker2 = null;
	
	public ZoomTagWorkerTest(){
		m_vpWorker.intialize(m_viewDim, m_proj);
		m_vpWorker.setCenterInWc(m_viewCentInWc);
		m_ztWorker = new ZoomTagWorker();
		m_ztWorker2 = new ZoomTagWorker();
	}
	
	@Test
	public void ProjectionTest(){
		m_proj.zoomByFactor(128);
		int tagX = 10500;
		int tagY = 499;
		double lng = m_proj.xPixToDegLng(tagX);
		double lat = m_proj.yPixToDegLat(tagY);
		int pix = m_proj.lngDegToPixX(lng);
		assertEquals(tagX, pix, 1);
		pix = m_proj.latDegToPixY(lat);
		assertEquals(tagY, pix, 1);
		m_proj.zoomByFactor(1.0/128.0);
	}
	
	private int compOffsetX( double oldScale, double newScale, int tagX ){
		double f = newScale/oldScale;
		return (int)( tagX - f*tagX );
	}
	
	private int compOffsetY( double startScale, double newScale, int tagY ){
		double f = newScale/startScale;
		return (int)(tagY - f*tagY);
	}
	
	@Test
	public void zoomfactorTest(){
		//m_vpWorker.zoomByFactor(32);
		int tagX = 450;
		int tagY = 50;
		double factor = 2.0;
		double startScale = m_proj.getEquatorialScale();
		double finalScale = startScale*factor;
		// now we want to incrementally increase the scale
		int numIncs = 30;
		double incScale = (finalScale-startScale)/numIncs;
		double oldScale = startScale;
		double scaleDiff = (factor - 1.0)*startScale;
		double fTotal = 1;
		double nextScale = startScale;
		for ( int i = 1; i <= numIncs; i++ ){
			nextScale = startScale + i*incScale;
			double f = nextScale/oldScale;
			fTotal *= f;
			oldScale = nextScale;
		}
		assertEquals(fTotal, 2, 0.1);
		assertEquals(finalScale, nextScale, 0);
	}
	
	private void zoom(double nextScale ){
		double f = nextScale/m_oldScale;
		m_ztWorker.compViewOffsets(f);	
	}

	@Test
	public void zoomInTest(){
		int tagX = 100;
		int tagY = 100;
		double factor = 2.0;
		double startScale = m_proj.getEquatorialScale();
		double scaleDiff = (factor - 1.0)*startScale;;
		// now we want to incrementally increase the scale
		int numIncs = 15;
		m_oldScale = startScale;
		m_ztWorker.setTagInVC(tagX, tagY);
		m_ztWorker2.setTagInVC(tagX, tagY);
		m_ztWorker2.compViewOffsets(factor);
		// loop test.
		double fTotal = 1;
		for ( int i = 1; i <= numIncs; i++ ){
			double nextScale = startScale +(scaleDiff*i)/numIncs;
			// zoom
			double f = nextScale/m_oldScale;
			m_ztWorker.compViewOffsets(f);	
			// end zoom
			m_ztWorker.setViewOffsets(m_ztWorker.getOffsetX(), m_ztWorker.getOffsetY());
			m_oldScale = nextScale;
			fTotal *= f;
		}
		int ox  = (int)m_ztWorker.getOffsetX();
		int ox2 = (int)m_ztWorker2.getOffsetX();
		int oy  = (int)m_ztWorker.getOffsetY();
		int oy2 = (int)m_ztWorker2.getOffsetY();
		assertEquals( ox2, ox, 1);
		assertEquals( oy2, oy, 1);
	}


	@Test
	public void zoomOutTest(){
		int tagX = 100;
		int tagY = 100;
		double factor = 0.5;
		double startScale = m_proj.getEquatorialScale();
		double scaleDiff = (factor - 1.0)*startScale;;
		// now we want to incrementally increase the scale
		int numIncs = 15;
		m_oldScale = startScale;
		m_ztWorker.setTagInVC(tagX, tagY);
		m_ztWorker2.setTagInVC(tagX, tagY);
		m_ztWorker2.compViewOffsets(factor);
		// loop test.
		double fTotal = 1;
		for ( int i = 1; i <= numIncs; i++ ){
			double nextScale = startScale +(scaleDiff*i)/numIncs;
			// zoom
			double f = nextScale/m_oldScale;
			m_ztWorker.compViewOffsets(f);	
			// end zoom
			m_ztWorker.setViewOffsets(m_ztWorker.getOffsetX(), m_ztWorker.getOffsetY());
			m_oldScale = nextScale;
			fTotal *= f;
		}
		int ox  = (int)m_ztWorker.getOffsetX();
		int ox2 = (int)m_ztWorker2.getOffsetX();
		int oy  = (int)m_ztWorker.getOffsetY();
		int oy2 = (int)m_ztWorker2.getOffsetY();
		assertEquals( ox2, ox, 1);
		assertEquals( oy2, oy, 1);
	}
}
