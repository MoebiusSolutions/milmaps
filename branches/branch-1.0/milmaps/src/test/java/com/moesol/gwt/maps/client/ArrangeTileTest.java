package com.moesol.gwt.maps.client;


import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.Degrees;

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
	private ViewPort m_VP = new ViewPort();
	
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
	
	protected void compareTileCoord( TileCoords c, int level, int j ){
		int tileX, tileY;
		int offsetX, offsetY;
		if ( j == 0 ){ //180 x 180, 512 x 512
			tileX = 1; tileY = 0;
			offsetX = 512; offsetY = 512;
		}
		else if ( j == 1 ){ // 90 x 90, 512 X 512
			tileX = 2; tileY = 1;
			offsetX = 1024; offsetY = 1024;
		}
		else { // 36 x 36, 512 x 512
			tileX = 5; tileY = 2;
			offsetX = 2560; offsetY = 1536;
		}
		assertEquals(c.getX(), tileX);
		assertEquals(c.getY(), tileY);
		assertEquals(c.getOffsetX(), offsetX);
		assertEquals(c.getOffsetY(), offsetY);
	}
	
	
	@Test
	public void testArrangeTiles(){
		m_VP.setProjection(m_proj);
		m_VP.setSize(2400,1200);
		for ( int j = 0; j < 3; j++ ){
		    LayerSet ls = createLayerSet(j);
		    m_geo = Degrees.geodetic(0, 0);
			m_proj.setViewGeoCenter(m_geo);
			for ( int level = 0; level < 3; level++){
				double factor = ( level< 1 ? 1 : 2 );
				m_proj.zoomByFactor(factor);
				double lsScale = findScale(m_dpi, level, ls);
				TileCoords[] tc = m_VP.arrangeTiles(ls, lsScale, level);
				compareTileCoords( tc, tc );
			}
		}
	}
	
	@Test
	public void testFindTile(){
		m_VP.setProjection(m_proj);
		m_VP.setSize(600,400);
		m_geo = Degrees.geodetic(0, 0);
		for ( int j = 0; j < 3; j++ ){
		    LayerSet ls = createLayerSet(j);
		    int tileSize = ls.getPixelWidth();
		    double degWidth = ls.getStartLevelTileWidthInDeg();
		    double degHeight = ls.getStartLevelTileHeightInDeg();
			m_proj.initialize( tileSize, degWidth, degHeight );
			m_proj.setViewGeoCenter(m_geo);
			for ( int level = 0; level < 1; level++){
				double factor = ( level< 1 ? 1 : 2 );
				m_proj.zoomByFactor(factor);
				TileCoords tc = m_VP.findTile(ls, level, m_geo);
				compareTileCoord( tc, level, j);
			}
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

		String url = "http://TestServer/cylendrical/" + key[x + 4*y];
		return url;
	}
	
	@Test
	public void testQuadkeyUrl(){
		LayerSet ls = createLayerSet(0);
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
	
	protected LayerSet createLayerSet( int j ){
		LayerSet ls = new LayerSet();
		if ( j == 0 ){
	  	    ls.setServer("http://TestServer");
	  	    ls.setData("cylendrical");
	  	    ls.setUrlPattern("{server}/{data}/{quadkey}");
	  	    ls.setAutoRefreshOnTimer(false);
			ls.setPixelWidth(512);
			ls.setPixelHeight(512);
			ls.setStartLevel(0);
			ls.setStartLevelTileHeightInDeg(180);
			ls.setStartLevelTileWidthInDeg(180);
			ls.setZeroTop(true);
		}
		else if ( j == 1 ){
			ls.setServer("http://TestServer"); 
			ls.setData("cylendrical");
			ls.setUrlPattern("{server}/{data}/MapServer/tile/{level}/{y}/{x}");
       	    ls.setEpsg(4326);
       	    ls.setZeroTop(true);
       	    ls.setAutoRefreshOnTimer(false);
			ls.setStartLevelTileHeightInDeg(90);
			ls.setStartLevelTileWidthInDeg(90);
			ls.setPixelWidth(512);
			ls.setPixelHeight(512);	
			ls.setStartLevel(0);
		}
		else if ( j == 2 ){
			ls.setServer("http://bv.moesol.com/rpf-ww-server"); 
			ls.setData("BMNG (Shaded %2B Bathymetry) Tiled - 5.2004");
			ls.setUrlPattern("{server}/tileset/BMNG/{data}/level/{level}/x/{x}/y/{y}");
       	    ls.setEpsg(4326);
       	    ls.setZeroTop(false);
       	    ls.setAutoRefreshOnTimer(false);
			ls.setStartLevelTileHeightInDeg(36);
			ls.setStartLevelTileWidthInDeg(36);
			ls.setPixelWidth(512);
			ls.setPixelHeight(512);	
			ls.setStartLevel(0);
		}
		return ls;
	}

}
