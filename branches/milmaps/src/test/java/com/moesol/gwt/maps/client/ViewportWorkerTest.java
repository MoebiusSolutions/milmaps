package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;


public class ViewportWorkerTest {
	private final ViewDimension m_viewDimensions = new ViewDimension(600, 400);
	private final ViewWorker m_vpWorker = new ViewWorker();
	private final IProjection m_proj = new CylEquiDistProj();//512, 180, 180);
	
	@Test
	public void  coordinateConverstionTest(){
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		WorldCoords wc = new WorldCoords(512, 256);
		m_vpWorker.setCenterInWc(wc);
		
		wc = new WorldCoords(200, 500);
		ViewCoords vc = m_vpWorker.wcToVC(wc);
		assertEquals( vc.getX(), -12);
		assertEquals( vc.getY(), -44);
		
		wc = m_vpWorker.viewToWorld(vc);
		assertEquals( wc.getX(), wc.getX());
		assertEquals( wc.getY(), wc.getY());
	}
	
	
	@Test
	public void updateTest() {
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		int offsetWcX = m_vpWorker.getOffsetInWcX();
		int offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 212);
		assertEquals( offsetWcY, 456);
		
		GeodeticCoords gc = new GeodeticCoords( 90, 45, AngleUnit.DEGREES);
		m_vpWorker.setGeoCenter(gc);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 468);
		assertEquals( offsetWcY, 584);
		
		gc = Degrees.geodetic(-45, -90);
				
		m_vpWorker.setGeoCenter(gc);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, -44);
		assertEquals( offsetWcY, 328);
	}
}
