package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.units.AngleUnit;


public class LayerSetWorkerTest {
	
	private class P {
		public int x;
		public int y;
		
		public void set(int x, int y ){
			this.x = x;
			this.y = y;
		}
	};
	
	private TileXY m_tile = new TileXY();
	private GeodeticCoords m_geo = new GeodeticCoords();
	private WorldCoords m_wc = new WorldCoords();
	private LayerSet[] m_ls = new LayerSet[2];
	private P[] m_p = new P[27];
	private LayerSetWorker m_lsWorker = new LayerSetWorker();
	
	public LayerSetWorkerTest(){
		m_ls[0] = new LayerSet();
		m_ls[1] = new LayerSet();
		m_ls[1].setPixelWidth(256);
		m_ls[1].setPixelHeight(256);
		m_ls[1].setStartLevelTileDimensionsInDeg(360, 170.10226);
		m_ls[1].setEpsg(900913);
		for( int i = 0; i < 27; i++ ){
			m_p[i] = new P();
		}
	}
	
	private void intializeTileP(){
		m_p[0].set(0,0);  m_p[1].set(0,0);  m_p[2].set(0,1);
		m_p[3].set(0,0);  m_p[4].set(1,0);  m_p[5].set(2,1);
		m_p[6].set(0,0);  m_p[7].set(1,0);  m_p[8].set(3,1);
		m_p[9].set(0,0);  m_p[10].set(0,1); m_p[11].set(0,2);
		m_p[12].set(0,0); m_p[13].set(1,1); m_p[14].set(2,2);
		m_p[15].set(0,0); m_p[16].set(1,1); m_p[17].set(3,2);
		m_p[18].set(0,0); m_p[19].set(0,1); m_p[20].set(0,2);
		m_p[21].set(0,0); m_p[22].set(1,1); m_p[23].set(2,2);
		m_p[24].set(0,0); m_p[25].set(1,1); m_p[26].set(3,2);
	}
	
	protected TileXY findTile( IProjection proj, int count, LayerSet ls, int level, 
							                 double maxLat, double lat, double lng ){
		if ( proj.getType() == IProjection.T.CylEquiDist ){
			double d = Math.pow(2.0, level);
			double degWSize = ls.getStartLevelTileWidthInDeg()/d;
			double degHSize = ls.getStartLevelTileHeightInDeg()/d;
			m_tile.m_x = (int)(( 180 + lng )/degWSize);
	    	m_tile.m_y = (int)(( maxLat + lat )/degHSize);	
		}
		else {
			m_tile.m_x = m_p[count].x;
			m_tile.m_y = m_p[count].y;
		}
    	return m_tile;
	}
	
	@Test
	public void testGeoPosToTileXY() {
		double[] maxLat = new double[2];
		maxLat[0] = 90;
		maxLat[1] = 85.05113;
		int count = 0;
		intializeTileP();
		for ( int i = 0; i < 2; i++ ){
			IProjection proj = Projection.getProj(m_ls[i]);
			m_lsWorker.setProjection(proj);
			count = 0;
			for ( int latInc = 0; latInc < 3; latInc++ ){
				double lat = -60.0 + latInc*60;
				for ( int lngInc = 0; lngInc < 3; lngInc++ ){
					double lng = -120.0 + lngInc*120;
					m_geo.set(lng, lat, AngleUnit.DEGREES);
					for ( int level = 1; level < 4; level++ ){
						double dFactor = 1 << level;
						proj.zoomByFactor(dFactor);
						TileXY tile = m_lsWorker.geoPosToTileXY(m_ls[i], m_geo);
						TileXY tile2 = findTile( proj, count, m_ls[i], level, maxLat[i],lat, lng );		
						proj.zoomByFactor(1.0/dFactor);
						count++;
						assertEquals(tile.m_x, tile2.m_x);
						assertEquals( tile.m_y, tile2.m_y);
					}
				}
			}
		}
	}
	
	private void intializeWcP(){
		m_p[0].set(0,512);  m_p[1].set(0,512);     m_p[2].set(0,1024);
		m_p[3].set(0,512);  m_p[4].set(512,512);   m_p[5].set(1024,1024);
		m_p[6].set(0,512);  m_p[7].set(512,512);   m_p[8].set(1536,1024);
		m_p[9].set(0,512);  m_p[10].set(0,1024);   m_p[11].set(0,1536);
		m_p[12].set(0,512); m_p[13].set(512,1024); m_p[14].set(1024,1536);
		m_p[15].set(0,512); m_p[16].set(512,1024); m_p[17].set(1536,1536);
		m_p[18].set(0,512); m_p[19].set(0,1024);   m_p[20].set(0,1536);
		m_p[21].set(0,512); m_p[22].set(512,1024); m_p[23].set(1024,1536);
		m_p[24].set(0,512); m_p[25].set(512,1024); m_p[26].set(1536,1536);
	}
	
	private WorldCoords findConerWc( IProjection proj, int count, LayerSet ls, 
													   int level, TileXY tile ){
		if ( proj.getType() == IProjection.T.CylEquiDist ){
			double d = 1 << level;
			int width = ls.getPixelWidth();
			int height = ls.getPixelHeight();
			m_wc.setX(tile.m_x*width);
			m_wc.setY((tile.m_y+1)*height);
		}
		else{
			m_wc.setX(m_p[count].x);
			m_wc.setY(m_p[count].y);
		}
		return m_wc;
	}

	@Test 
	public void testTileXYToTopLeftXY(){
		double[] maxLat = new double[2];
		maxLat[0] = 90;
		maxLat[1] = 85.05113;
		int count = 0;
		intializeWcP();
		for ( int i = 0; i < 2; i++ ){
			boolean zeroTop = m_ls[i].isZeroTop();
			IProjection proj = Projection.getProj(m_ls[i]);
			m_lsWorker.setProjection(proj);
			if ( i == 1 )
				i = 1;
			count = 0;
			for ( int latInc = 0; latInc < 3; latInc++ ){
				double lat = -60.0 + latInc*60;
				for ( int lngInc = 0; lngInc < 3; lngInc++ ){
					double lng = -120.0 + lngInc*120;
					m_geo.set(lng, lat, AngleUnit.DEGREES);
					for ( int level = 1; level < 4; level++ ){
						double dFactor = 1 << level;
						proj.zoomByFactor(dFactor);
						TileXY tile = m_lsWorker.geoPosToTileXY(m_ls[i], m_geo);		
						WorldCoords wc = m_lsWorker.tileXYToTopLeftXY( m_ls[i], zeroTop, level, tile  );
						WorldCoords wc2 = findConerWc( proj, count, m_ls[i], level, tile );
						proj.zoomByFactor(1.0/dFactor);
						count++;
						assertEquals(wc.getX(), wc2.getX());
						assertEquals(wc.getY(), wc2.getY());
					}
				}
			}
		}		
	}
	
	protected int findWidth(LayerSet ls, int level ){
		//double f = 1 << level;
		return ls.getPixelWidth();
	}
	
	@Test 
	public void testCompTileDrawWidth(){
		double[] maxLat = new double[2];
		maxLat[0] = 90;
		maxLat[1] = 85.05113;
		intializeWcP();
		for ( int i = 0; i < 2; i++ ){
			IProjection proj = Projection.getProj(m_ls[i]);
			m_lsWorker.setProjection(proj);
			for ( int level = 0; level < 4; level++ ){
				double dFactor = 1 << level;
				proj.zoomByFactor(dFactor);
				int width = m_lsWorker.compTileDrawWidth(m_ls[i], level);
				int twidth = findWidth(m_ls[i],level);
				proj.zoomByFactor(1.0/dFactor);
				assertEquals( width, twidth);
			}	
		}		
	}
}
