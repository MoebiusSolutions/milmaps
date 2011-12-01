package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


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
}
