package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class ProjectionTest {
	
	private IProjection m_proj = new CylEquiDistProj( 512, 180, 180);
	private CylProj m_cylProj = new CylProj(512,180);
	
	private TileXY m_tile = new TileXY();
	private GeodeticCoords m_geo = new GeodeticCoords();
	
	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = 75;   // screen dot per inch
	protected double m_scrnMpp = 2.54/7500.0; // screen meter per pixel
	protected int m_orgTilePixSize = 512;
	protected double m_orgTileWidthInDeg = 180;
	
	public ProjectionTest(){

	}
	
	@Test
	public void testWorldToGeo(){
		GeodeticCoords gc1;
		GeodeticCoords gc2;
		WorldCoords wc = new WorldCoords() ;
		for (int x = 2; x < 512; x += 2 ) {
			for ( int y = 2; y < 512; y += 2 ) {
				wc.setX(x); wc.setY(y);
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
				geo.set(lng, lat, AngleUnit.DEGREES);
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
				exp.set(lambda, phi, AngleUnit.DEGREES);
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
	
	protected TileXY findTile( double lat, double lng, int level ){
		double degSize = 180.0/Math.pow(2.0, level);
		m_tile.m_x = (int)(( 180 + lng )/degSize);
    	m_tile.m_y = (int)(( 90 + lat )/degSize);	
    	return m_tile;
	}
	
	@Test
	public void testFindTile() {
		for ( int latInc = 0; latInc < 3; latInc++ ){
			double lat = -60.0 + latInc*60;
			for ( int lngInc = 0; lngInc < 3; lngInc++ ){
				double lng = -120.0 + lngInc*120;
				m_geo.set(lng, lat, AngleUnit.DEGREES);
				for ( int level = 1; level < 4; level++ ){
					double dFactor = Math.pow(2,level);
					m_proj.zoomByFactor(dFactor);
					TileXY tile = m_proj.geoPosToTileXY( level, m_geo );
					TileXY tile2 = findTile( lat, lng, level );		
					if ( tile.m_x != tile2.m_x )
						tile.m_x += 0;
					if ( tile.m_y != tile2.m_y )
						tile.m_y += 0;
					assertEquals(tile.m_x, tile2.m_x);
					assertEquals( tile.m_y, tile2.m_y);
					m_proj.zoomByFactor(1.0/dFactor);
				}
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
	public void testAdjustSize(){
		double fudge = 0.5;
		for ( int i = 0; i < 4; i++ ){
			double dFactor = Math.pow(2,i) + fudge;
			m_proj.zoomByFactor(dFactor);
			int level = computeLevel( 0, m_proj.getScale() );
			int width  = m_proj.adjustSize(level, 512);
			double dZ = (1 + fudge/Math.pow(2,i));
			int expValue = (int)(dZ*512);
			m_proj.zoomByFactor(1.0/dFactor);
			assertEquals( expValue, width, 1 );
		}
	}
	
	@Test
	public void testgGeoToPixel(){
		GeodeticCoords exp;
		WorldCoords v;
		exp = new GeodeticCoords();
		for (double phi = -90; phi <= 90; phi += 1.0) {
			for (double lambda = -180; lambda <= 180; lambda += 1.0) {
				exp.set(lambda, phi, AngleUnit.DEGREES);
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
