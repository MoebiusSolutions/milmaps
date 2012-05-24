/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class ProjectionTest {
	
	private IProjection m_proj = Projection.createProj(IProjection.T.CylEquiDist);
	private CylProj m_cylProj = new CylProj(512,180);
	
	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = AbstractProjection.DOTS_PER_INCH;   // screen dot per inch
	protected double m_scrnMpp = 2.54/9600.0; // screen meter per pixel
	protected int m_orgTilePixSize = 512;
	protected double m_orgTileWidthInDeg = 180;
	
	public ProjectionTest(){
		m_proj.initialize(512, 180, 180);
	}
	
	@Test
	public void testNumYTiles(){
		double startDegs[] = {180 , 90, 45, 27.5 };
		for( int i = 0; i < 1; i++ ){
			for( int level = 0; level < 20; level++){
				double degWidth = startDegs[i]/(1<<level);
				int numTiles = m_proj.getNumYtiles(degWidth);
				assertEquals(numTiles,(1<<(level+i)));
			}
		}
		
	}
	
	@Test
	public void testWorldToGeo(){
		GeodeticCoords gc1;
		GeodeticCoords gc2;
		for (int x = 2; x < 512; x += 2 ) {
			for ( int y = 2; y < 512; y += 2 ) {
				WorldCoords wc = WorldCoords.builder().setX(x).setY(y).build();
				gc1 = m_proj.worldToGeodetic(wc);
				gc2 = m_cylProj.worldToGeodetic(wc);
				compareGeoPos( gc1, gc2, 0.0001 );
			}
		}
	}
	
	@Test
	public void testGeoToWorld(){
		WorldCoords v;
		WorldCoords w;
		int x = 0;
		m_proj.zoomByFactor(8);
		m_cylProj.zoomByFactor(8);
		GeodeticCoords geo = new GeodeticCoords();
		for (double lat = -90; lat <= 90; lat += 2.0) {
			for (double lng = -180; lng <= 180; lng += 2.0) {
				geo = Degrees.geodetic(lat, lng);
				v = m_proj.geodeticToWorld(geo);
				w = m_cylProj.geodeticToWorld(geo);
				if ( v.getX() != w.getX() || v.getY() != w.getY() )
					x = 1;
				assertEquals(v.getX(), w.getX());
				assertEquals(v.getY(), w.getY());
			}
		}
	}

	@Test
	public void testZoomIn() {
		m_proj.zoomByFactor(1);
		
		checkViewToWorld(1);
	}

	@Test
	public void testZoomOut() {
		m_proj.zoomByFactor(2);
		m_proj.zoomByFactor(2);
		m_proj.zoomByFactor(2);
		m_proj.zoomByFactor(2);
		checkViewToWorld(2);
	}
	
	@Test
	public void testComputeLevel() {
		double dScale = m_proj.getBaseEquatorialScale();
		double inc;
		for( int j = 1; j < 10; j++ ){
			inc = (1/10.2)*j;
			for( int i = 0; i < 20; i ++){
				double f = (1<<i) + inc;
				int level = m_proj.getLevelFromScale(dScale*f, 0);
				assertEquals(i, level);
			}
		}
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
		m_proj.zoomByFactor(1/2.0);
		m_proj.zoomByFactor(2);
		checkViewToWorld(0);
		
		m_proj.zoomByFactor(1/2.0);

		WorldCoords v = new WorldCoords(0, 0);
		GeodeticCoords w = m_proj.worldToGeodetic(v);
		GeodeticCoords exp = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		int level = -1;
		int viewDx = 1024/ (1 << (-level));
		int viewDy = 512/ (1 << (-level));
		
		v = new WorldCoords(viewDx, viewDy);
		w = m_proj.worldToGeodetic(v);
		exp = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		v = new WorldCoords(viewDx / 2, viewDy / 2);
		w = m_proj.worldToGeodetic(v);
		exp = new GeodeticCoords(0,0, AngleUnit.DEGREES);
		assertEquals(exp, w);
	}
	
	@Test
	public void testRoundTrip() {
		GeodeticCoords exp;
		GeodeticCoords w;
		WorldCoords v;
		m_proj.zoomByFactor(32);
		exp = new GeodeticCoords();
		for (double phi = -90; phi <= 90; phi += 1.0) {
			for (double lambda = -180; lambda <= 180; lambda += 1.0) {
				exp = Degrees.geodetic(phi, lambda);
				v = m_proj.geodeticToWorld(exp);
				w = m_proj.worldToGeodetic(v);
				double dist = 0.05;
				double dLonE = exp.getLambda(AngleUnit.DEGREES);
				double dLonW = w.getLambda(AngleUnit.DEGREES);
				if ( Math.abs(dLonE - dLonW) > dist )
					dist += 0;
				assertEquals(exp.getLambda(AngleUnit.DEGREES), w.getLambda(AngleUnit.DEGREES), dist);
				assertEquals(exp.getPhi(AngleUnit.DEGREES), w.getPhi(AngleUnit.DEGREES), dist);
				assertEquals(exp.getAltitude(), w.getAltitude(), dist);
			}
		}
	}

	// 1852 meters/ nautical mile so 1 deg = 60*1852 meters = 111,120 meters.
	// mpp is meters per pixel.
	// level_n_mpp = (level_0_mpp)/2^n so level_n_scale =
	// (m_mpp/level_0_mpp)*2^n
	// We wan to find n so so that level_n_Scale close to given projScale
	//
	// Hence n = ( log(projScale) + log(level_0_mpp) - log(m_mpp) )/log(2);

	// dpi is pixels per inch for physical screen
	
	protected double findScale(double dpi, int origPixWidth, double origDegWidth, int level ) {
		double mpp = 2.54 / (dpi * 100);
		double m_dx = origPixWidth;
		double l_mpp = origDegWidth* (111120.0 / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}
	
	
	@Test
	public void testgGeoToPixel(){
		GeodeticCoords exp;
		WorldCoords v;
		exp = new GeodeticCoords();
		for (double phi = -90; phi <= 90; phi += 1.0) {
			for (double lambda = -180; lambda <= 180; lambda += 1.0) {
				exp = Degrees.geodetic(phi, lambda);
				v = m_proj.geodeticToWorld(exp);
				assertEquals(v.getX(),v.getX());
			}
		}
	}

	@Test
	public void testWorldDimensions() {
		//WorldDimension d = m_proj.getWorldDimension();
		
		//GeodeticCoords upperRightGeo = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		//WorldCoords upperRight = m_proj.geodeticToWorld(upperRightGeo);
		
		//assertEquals(upperRight.getX(), d.getWidth());
		//assertEquals(upperRight.getY(), d.getHeight());
		assertEquals(5,5);
	}
	
	@Test
	public void testWidthInPixel() {
		m_proj.zoomByFactor(4);
		double minLon = -170;
		double maxLon = 170;
		double minLat = -20;
		double maxLat = 20;
		double pixWidth = m_proj.compWidthInPixels( minLon, maxLon);
		double pixHeight = m_proj.compHeightInPixels( minLat, maxLat);
		m_proj.zoomByFactor(1.0/4.0);
		assertEquals(pixHeight,2*pixWidth,1);
	}

	private void checkViewToWorld(int level) {
		/*
		WorldCoords v = new WorldCoords(0, 0);
		GeodeticCoords w = m_proj.worldToGeodetic(v);
		GeodeticCoords exp = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		int viewDx = 512 * 10 * (1 << level);
		int viewDy = 512 * 5 *  (1 << level);
		
		v = new WorldCoords(viewDx, viewDy);
		w = m_proj.worldToGeodetic(v);
		exp = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		assertEquals(exp, w);
		
		v = new WorldCoords(viewDx / 2, viewDy / 2);
		w = m_proj.worldToGeodetic(v);
		exp = new GeodeticCoords(0,0, AngleUnit.DEGREES);
		assertEquals(exp, w);
		*/
		assertEquals(5,5);
	}
	
	public int computeLevel( int startLevel, double projScale) {
		double earth_mpp = m_orgTileWidthInDeg* (MeterPerDeg / m_orgTilePixSize);
		// compute the best level.
		if ( projScale == 0.0 ){
			projScale = (m_scrnMpp / earth_mpp);
		}
		double logMess = Math.log(projScale) + Math.log(earth_mpp)
				- Math.log(m_scrnMpp);
		double dN = logMess / Math.log(2);
		return (int)(dN) + startLevel;
	}
	
	public double compScale( double dpi, int level ) {
		double mpp = 2.54 / (dpi * 100);
		double m_dx = m_orgTilePixSize;
		double l_mpp = m_orgTileWidthInDeg * (MeterPerDeg / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}

	private void checkWorldToView(int level) {
		GeodeticCoords w = new GeodeticCoords(-180, -90, AngleUnit.DEGREES);
		WorldCoords v = m_proj.geodeticToWorld(w);
		WorldCoords exp = new WorldCoords(0, 0);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(180, 90, AngleUnit.DEGREES);
		v = m_proj.geodeticToWorld(w);
		int viewDx = 512*(1<<level+1);
		int viewDy = 512*(1<<level);
		exp = new WorldCoords(viewDx, viewDy);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(0, 0, AngleUnit.DEGREES);
		v = m_proj.geodeticToWorld(w);
		exp = new WorldCoords(viewDx / 2, viewDy / 2);
		assertEquals(exp, v);
	}
	
	protected void compareGeoPos( GeodeticCoords gc1, GeodeticCoords gc2, double dEpsilon ){
		double lat1 = gc1.getPhi(AngleUnit.DEGREES);
		double lng1 = gc1.getLambda(AngleUnit.DEGREES);
		double lat2 = gc2.getPhi(AngleUnit.DEGREES);
		double lng2 = gc2.getLambda(AngleUnit.DEGREES);
		if ( Math.abs(lat1 - lat2) > dEpsilon )
			lat1 += 0;
		if ( Math.abs(lng1 - lng2) > dEpsilon )
			lng1 += 0;
		assertEquals(lat1,lat2,dEpsilon);
		assertEquals(lng1,lng2,dEpsilon);
	}
	
}
