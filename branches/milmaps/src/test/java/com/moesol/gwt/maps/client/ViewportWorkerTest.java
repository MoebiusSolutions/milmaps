package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;


public class ViewportWorkerTest {
	private final ViewDimension m_viewDimensions = new ViewDimension(600, 400);
	private final ViewWorker m_vpWorker = new ViewWorker();
	private final IProjection m_proj = new CylEquiDistProj();//512, 180, 180);
	private final WorldCoords m_wc = new WorldCoords();
	private final ViewCoords m_vc = new ViewCoords();
	
	@Test
	public void  coordinateConverstionTest(){
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		m_wc.setX(512);
		m_wc.setY(256);
		m_vpWorker.setVpCenterInWc(m_wc);
		m_wc.setX(200);
		m_wc.setY(500);
		ViewCoords vc = m_vpWorker.wcToVC(m_wc);
		assertEquals( vc.getX(), -12);
		assertEquals( vc.getY(), -44);
		WorldCoords wc = m_vpWorker.vcToWC(vc);
		assertEquals( wc.getX(), m_wc.getX());
		assertEquals( wc.getY(), m_wc.getY());
	}
	
	
	@Test
	public void updateTest(){
		m_vpWorker.intialize(m_viewDimensions, m_proj);
		m_vpWorker.update(true);
		int offsetWcX = m_vpWorker.getOffsetInWcX();
		int offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 212);
		assertEquals( offsetWcY, 456);
		
		GeodeticCoords gc = new GeodeticCoords( 90, 45, AngleUnit.DEGREES);
		m_vpWorker.setGeoCenter(gc);
		m_vpWorker.update(true);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, 468);
		assertEquals( offsetWcY, 584);
		
		gc.set( -90, -45, AngleUnit.DEGREES);
		m_vpWorker.setGeoCenter(gc);
		m_vpWorker.update(true);
		offsetWcX = m_vpWorker.getOffsetInWcX();
		offsetWcY = m_vpWorker.getOffsetInWcY();
		assertEquals( offsetWcX, -44);
		assertEquals( offsetWcY, 328);
	}
}
