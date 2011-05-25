package com.moesol.gwt.maps.client;


import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class ArrangeTileTest {
	private URLProvider m_provider = new URLProvider() {
		@Override
		public String encodeComponent(String decodedURLComponent) {
			try {
				return URLEncoder.encode(decodedURLComponent, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	};
	private IProjection m_proj = new CylEquiDistProj( 512, 180, 180);
	private ViewPort m_VP = new ViewPort(m_proj);
	
	private final double EarthCirMeters  = 2.0*Math.PI*6378137;
	private final double MeterPerDeg  = EarthCirMeters/360.0;
	
	int m_startLevel = 0;
	double m_tileDegWidth = 180;
	double m_tileDegHeight = 180;
	
	int m_tilePixWidth  = 512;
	int m_tilePixHeight = 512;
	
	int m_dpi = 75;

	private GeodeticCoords m_geo = new GeodeticCoords();
	
	protected void compareTileCoords(TileCoords[] c, TileCoords[] d ){
		int cn = c.length;
		int dn = d.length;
		assertEquals(cn,dn);
		for ( int i = 0; i < cn; i++ ){
			TileCoords tc = c[i];
			TileCoords td = d[i];
			assertEquals(tc.getX(), td.getX());
			assertEquals(tc.getY(), td.getY());
			assertEquals(tc.getOffsetX(), td.getOffsetX());
			assertEquals(tc.getOffsetY(), td.getOffsetY());
			assertEquals(tc.getDrawTileWidth(),td.getDrawTileWidth());
			assertEquals(tc.getDrawTileWidth(),td.getDrawTileWidth());				
		}	
	}
	
	@Test
	public void testArrangeTiles(){
		m_VP.setSize(2400,1200);
		LayerSet ls = createLayerSet();
		m_geo.set(0,0,AngleUnit.DEGREES);
		m_proj.setViewGeoCenter(m_geo);
		for ( int level = 0; level < 3; level++){
			double factor = ( level< 1 ? 1 : 2 );
			m_proj.zoomByFactor(factor);
			double lsScale = findScale(m_dpi, level, ls);
			TileCoords[] tc = m_VP.arrangeTiles(ls, lsScale, level);
			compareTileCoords( tc, tc );
		}
	}
	
	@Test
	public void testQuadkey(){
		for ( int level = 1; level < 5; level++ ){
			int count = (int)Math.pow(2, level);
			for ( int x = 0; x < count; x++ ){
				for ( int y = 0; y < count; y++ ){
					String quadKey = QuadKey.tileXYToKey(x, y, level);
					TileXY tile = QuadKey.keyToTileXY(quadKey);
					assertEquals(x,tile.m_x);
					assertEquals(y,tile.m_y);
				}
			}
		}
	}
	
	protected String getExpectedUrl(int x, int y ){
		String[] key = {"00","01","10","11",
						"02","03","12","13",
						"20","21","30","31",
						"22","23","32","33"};

		String url = "http://TestServer/mercator/" + key[x + 4*y];
		return url;
	}
	
	@Test
	public void testQuadkeyUrl(){
		LayerSet ls = createLayerSet();
		TileCoords tc = new TileCoords(0,0);
		TileCoords.setGlobalURLProvider(m_provider);
		for ( int level = 2; level < 3; level++ ){
			int count = (int)Math.pow(2, level);
			for ( int x = 0; x < count; x++ ){
				for ( int y = 0; y < count; y++ ){
					tc.setX(x);
					tc.setY(y);
					String url = tc.makeTileURL( ls, level, x+y);
					String expectedStr = getExpectedUrl( x, y );
					assertEquals( expectedStr, url );
				}
			}
		}
	}
	
	
	protected double findScale( double dpi, int level, LayerSet ls ) {
		double mpp = 2.54 / (dpi * 100);
		double m_dx = ls.getPixelWidth();
		double l_mpp = ls.getStartLevelTileWidthInDeg()* (MeterPerDeg / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}
	
	protected LayerSet createLayerSet(){
		LayerSet ls = new LayerSet();
  	    ls.setServer("http://TestServer");
  	    ls.setData("mercator");
  	    ls.setUrlPattern("{server}/{data}/{quadkey}");
		ls.setPixelWidth(512);
		ls.setPixelHeight(512);
		ls.setStartLevel(0);
		ls.setStartLevelTileHeightInDeg(180);
		ls.setStartLevelTileWidthInDeg(180);
		ls.setZeroTop(true);
		return ls;
	}

}
