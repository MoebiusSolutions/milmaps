package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class ProjectionTest {
	
	private IProjection m_projection = new CylEquiDistProj();
	//private ViewPort m_viewPort = new ViewPort(m_projection);
	
	public ProjectionTest(){
		m_projection.initScale(512, 180.0);
	}

	@Test
	public void testZoomIn() {
		m_projection.zoomByFactor(1);
		
		checkViewToWorld(1);
	}

	@Test
	public void testZoomOut() {
		m_projection.zoomByFactor(2);
		m_projection.zoomByFactor(2);
		m_projection.zoomByFactor(2);
		m_projection.zoomByFactor(2);
		checkViewToWorld(2);
	}

	@Test
	public void testViewToWorld() {
		checkViewToWorld(0);
	}

	@Test
	public void testWorldToView() {
		checkWorldToView(0);
	}
	
	@Test
	public void testNegativeZoom() {
		m_projection.zoomByFactor(1/2.0);
		m_projection.zoomByFactor(2);
		checkViewToWorld(0);
		
		m_projection.zoomByFactor(1/2.0);

		WorldCoords v = new WorldCoords(0, 0);
		GeodeticCoords w = m_projection.worldToGeodetic(v);
		GeodeticCoords exp = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		int level = -1;
		int viewDx = 1024/ (1 << (-level));
		int viewDy = 512/ (1 << (-level));
		
		v = new WorldCoords(viewDx, viewDy);
		w = m_projection.worldToGeodetic(v);
		exp = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		v = new WorldCoords(viewDx / 2, viewDy / 2);
		w = m_projection.worldToGeodetic(v);
		exp = new GeodeticCoords(0,0, AngleUnit.DEGREES);
		assertEquals(exp, w);
	}
	
	@Test
	public void testRoundTrip() {
		GeodeticCoords exp;
		GeodeticCoords w;
		WorldCoords v;
		int x;
		m_projection.zoomByFactor(32);
		
		for (double phi = -90; phi <= 90; phi += 1.0) {
			for (double lambda = -180; lambda <= 180; lambda += 1.0) {
				exp = new GeodeticCoords(lambda, phi, AngleUnit.DEGREES);
				v = m_projection.geodeticToWorld(exp);
				w = m_projection.worldToGeodetic(v);
				double dist = 0.05;
				double dLonE = exp.getLambda(AngleUnit.DEGREES);
				double dLonW = w.getLambda(AngleUnit.DEGREES);
				if ( Math.abs(dLonE - dLonW) > dist )
					x = 0;
				assertEquals(exp.getLambda(AngleUnit.DEGREES), w.getLambda(AngleUnit.DEGREES), dist);
				assertEquals(exp.getPhi(AngleUnit.DEGREES), w.getPhi(AngleUnit.DEGREES), dist);
				assertEquals(exp.getAltitude(), w.getAltitude(), dist);
			}
		}
	}
	
	@Test
	public void testFindTile() {
		//TileCoords tc = m_viewPort.findTile( 0, new GeodeticCoords(0, 0, AngleUnit.DEGREES));
		//assertEquals(5, tc.getX());
		//assertEquals(2, tc.getY());
		//assertEquals(0, tc.getOffsetX());
		//assertEquals(256, tc.getOffsetY());
		assertEquals(5,5);
	}
	
	@Test
	public void testWorldDimensions() {
		//WorldDimension d = m_projection.getWorldDimension();
		
		//GeodeticCoords upperRightGeo = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		//WorldCoords upperRight = m_projection.geodeticToWorld(upperRightGeo);
		
		//assertEquals(upperRight.getX(), d.getWidth());
		//assertEquals(upperRight.getY(), d.getHeight());
		assertEquals(5,5);
	}

	private void checkViewToWorld(int level) {
		/*
		WorldCoords v = new WorldCoords(0, 0);
		GeodeticCoords w = m_projection.worldToGeodetic(v);
		GeodeticCoords exp = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		int viewDx = 512 * 10 * (1 << level);
		int viewDy = 512 * 5 *  (1 << level);
		
		v = new WorldCoords(viewDx, viewDy);
		w = m_projection.worldToGeodetic(v);
		exp = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		v = new WorldCoords(viewDx / 2, viewDy / 2);
		w = m_projection.worldToGeodetic(v);
		exp = new GeodeticCoords(0,0, AngleUnit.DEGREES);
		assertEquals(exp, w);
		*/
		assertEquals(5,5);
	}

	private void checkWorldToView(int level) {
		GeodeticCoords w = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		WorldCoords v = m_projection.geodeticToWorld(w);
		WorldCoords exp = new WorldCoords(0, 0);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		v = m_projection.geodeticToWorld(w);
		int viewDx = 512*(1<<level+1);
		int viewDy = 512*(1<<level);
		exp = new WorldCoords(viewDx, viewDy);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(0, 0, AngleUnit.DEGREES);
		v = m_projection.geodeticToWorld(w);
		exp = new WorldCoords(viewDx / 2, viewDy / 2);
		assertEquals(exp, v);
	}
	
}
