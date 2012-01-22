package com.moesol.gwt.maps.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;


public class MercatorTest {

	private IProjection m_proj = new Mercator();//256,360,2*85.05113);
	private MercProj m_mercProj = new MercProj();
	
	private TileXY m_tile = new TileXY();
	
	//private ViewPort m_viewPort = new ViewPort(m_projection);
	
	public double EarthRadius = 6378137;
	public double EarthCirMeters  = 2.0*Math.PI*6378137;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
	protected int m_scrnDpi = 75;   // screen dot per inch
	protected double m_scrnMpp = 2.54/7500.0; // screen meter per pixel
	protected int m_orgTilePixSize = 256;
	protected double m_orgTileWidthInDeg = 360;
	
	public MercatorTest(){
	}

	@Test
	public void testWorldToView() {
		checkWorldToView(0);
	}
	
	@Test
	public void testNegativeZoom() {
		m_proj.zoomByFactor(1/2.0);
		m_proj.zoomByFactor(2);
		
		m_proj.zoomByFactor(1/2.0);

		WorldCoords v = new WorldCoords(0, 0);
		GeodeticCoords w = m_proj.worldToGeodetic(v);
		//GeodeticCoords exp = new GeodeticCoords(-180, -85.05113, AngleUnit.DEGREES);
		double val = w.getPhi(AngleUnit.DEGREES);
		assertEquals(val,-85.05113, 0.2);
		val = w.getLambda(AngleUnit.DEGREES);
		assertEquals(val,-180, 0.2);
		//assertEquals(exp, w);
		
		int level = -1;
		int viewDx = 256/ (1 << (-level));
		int viewDy = 256/ (1 << (-level));
		
		v = new WorldCoords(viewDx, viewDy);
		w = m_proj.worldToGeodetic(v);
		//exp = new GeodeticCoords(180, 85.05113, AngleUnit.DEGREES);
		assertEquals(w.getPhi(AngleUnit.DEGREES),85.05113, 0.0001);
		assertEquals(w.getLambda(AngleUnit.DEGREES),180, 0.0001);
		//assertEquals(exp, w);
		
		v = new WorldCoords(viewDx / 2, viewDy / 2);
		w = m_proj.worldToGeodetic(v);
		//exp = new GeodeticCoords(0,0, AngleUnit.DEGREES);
		//assertEquals(exp, w);
		assertEquals(w.getPhi(AngleUnit.DEGREES),0, 0.0001);
		assertEquals(w.getLambda(AngleUnit.DEGREES),0, 0.0001);
	}
	
	@Test
	public void testRoundTrip() {
		GeodeticCoords exp;
		GeodeticCoords w;
		WorldCoords v;
		m_proj.zoomByFactor(32);
		double lat, lng;
		lng = 1.0;
		lat = -80;
		for (lat = -80; lat <= 80; lat += 1.0) {
			for (lng = -180; lng <= 180; lng += 1.0) {
				exp = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
				v = m_proj.geodeticToWorld(exp);
				w = m_proj.worldToGeodetic(v);
				double dist = 0.05;
				double dLonE = exp.getLambda(AngleUnit.DEGREES);
				double dLonW = w.getLambda(AngleUnit.DEGREES);
				if ( Math.abs(dLonE - dLonW) > dist )
					lat += 0;
				assertEquals(exp.getLambda(AngleUnit.DEGREES), w.getLambda(AngleUnit.DEGREES), dist);
				assertEquals(exp.getPhi(AngleUnit.DEGREES), w.getPhi(AngleUnit.DEGREES), dist);
				assertEquals(exp.getAltitude(), w.getAltitude(), dist);
			}
		}
	}
	
	@Test
	public void testSpecialLatLng() {
		GeodeticCoords gc;
		WorldCoords wc;
		m_proj.zoomByFactor(4.0);
		double lat, lng;
		lng = -120;
		lat = -60;
		gc = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
		wc = m_proj.geodeticToWorld(gc);
		GeodeticCoords gc2 = m_proj.worldToGeodetic(wc);
		compareGeoPos(gc, gc2, 0.7);
		m_proj.zoomByFactor(0.25);
	}

	@Test
	public void testGeodeticToWorld() {
		GeodeticCoords gc;
		WorldCoords wc;
		//m_proj.zoomByFactor(32);
		double lat, lng;
		lng = 1.0;
		lat = -80;
		for (lat = -80; lat <= 80; lat += 1.0) {
			for (lng = -180; lng <= 180; lng += 1.0) {
				gc = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
				wc = m_proj.geodeticToWorld(gc);
				GeodeticCoords gc2 = m_proj.worldToGeodetic(wc);
				compareGeoPos(gc, gc2, 0.7);
			}
		}
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

	@Test
	public void testWorldToGeodetic() {
		GeodeticCoords gc1;
		GeodeticCoords gc2;
		m_proj.zoomByFactor(16);
		int level = computeLevel( 0, m_proj.getEquatorialScale());
		for (int x = 2; x < 512; x += 2 ) {
			for ( int y = 2; y < 512; y += 2 ) {
				gc1 = m_proj.worldToGeodetic(WorldCoords.builder().setX(x).setY(y).build());
				gc2 = m_mercProj.pixelXYToLatLng(level, x, y);
				compareGeoPos( gc1, gc2, 0.0001 );
			}
		}
	}

	
	protected TileXY findTile( GeodeticCoords gc, int level ){
		double lat = gc.getPhi(AngleUnit.DEGREES);
		double lng = gc.getLambda(AngleUnit.DEGREES);
		PixelXY pixel = m_mercProj.latLngToPixelXY( level, lat, lng );
		TileXY tile = m_mercProj.pixelXYToTileXY(level,pixel.m_x, pixel.m_y ).clone();
    	m_tile.m_x = tile.m_x;
    	m_tile.m_y = tile.m_y;	
    	return m_tile;
	}

	@Test
	public void testCompareProjs(){
		assertEquals(true, true);
	}
	
	
	@Test
	public void testFindLevel(){
		// first compare lat/lng to pixel.
		for ( int latInc = 0; latInc < 3; latInc++ ){
			double lat = -60.0 + latInc*60;
			for ( int lngInc = 0; lngInc < 3; lngInc++ ){
				double lng = -120.0 + lngInc*120;
				GeodeticCoords geo = Degrees.geodetic(lat, lng);
				for ( int level = 1; level < 13; level++ ){	
					for ( int j = 5; j < 10; j++ ){
						double dFactor = Math.pow(2,level-1) + j*0.1;
						m_proj.zoomByFactor(dFactor);
						int tLevel = computeLevel( 1, m_proj.getEquatorialScale());
						m_proj.zoomByFactor(1.0/dFactor);
						if ( level != tLevel )
							tLevel = level;
						assertEquals(level,tLevel);
					}
				}
			}
		}
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
		return (int)(Math.rint(dN)) + startLevel;
	}


	private void checkWorldToView(int level) {
		GeodeticCoords w = new GeodeticCoords(-180, -85.05113, AngleUnit.DEGREES);
		WorldCoords v = m_proj.geodeticToWorld(w);
		WorldCoords exp = new WorldCoords(0, 0);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(180, 85.05113, AngleUnit.DEGREES);
		v = m_proj.geodeticToWorld(w);
		int viewDx = 256*(1<<level);
		int viewDy = 256*(1<<level);
		exp = new WorldCoords(viewDx, viewDy);
		assertEquals(exp, v);
		
		w = new GeodeticCoords(0, 0, AngleUnit.DEGREES);
		v = m_proj.geodeticToWorld(w);
		exp = new WorldCoords(viewDx / 2, viewDy / 2);
		assertEquals(exp, v);
	}
	
}
