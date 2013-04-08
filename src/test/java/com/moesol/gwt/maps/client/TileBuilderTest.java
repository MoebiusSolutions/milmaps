/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TileBuilderTest {
	private DivWorker m_dw;
	private ViewWorker m_vw;
	//private DivDimensions m_dims = new DivDimensions();
	private ViewDimension m_viewDims;;
	private IProjection m_proj, m_mapProj, m_divProj;
	private ViewPort m_vp;
	private TileBuilder m_tb;
	
	int m_dpi = AbstractProjection.DOTS_PER_INCH;
	
	@Before
	public void before(){}
	
	public void before(int size, double deg){
		
		m_vw = new ViewWorker();
		m_viewDims = new ViewDimension(600, 400);
		m_vp = new ViewPort();
		// DivPanel projection
		m_proj = Projection.createProj(IProjection.T.CylEquiDist);
		m_proj.initialize(size, deg, deg);

		// Map view projection
		m_mapProj = Projection.createProj(IProjection.T.CylEquiDist);
		m_mapProj.initialize(size, deg, deg);
		// Initialize map view worker
		m_vw.intialize(m_viewDims, m_mapProj);
		//set view center.
		m_vw.setGeoCenter(new GeodeticCoords());
	
		m_vp.setProjection(m_proj);
		m_vp.setSize(600,400);
	}
	
	void initializeDivStuf(int level, int vwWidth, int vwHeight) {
		// Simulate div for given level
		double eqScale = m_proj.getBaseEquatorialScale();
		double scale = eqScale*(1 << level);
		m_divProj = Projection.createProj(IProjection.T.CylEquiDist);
		m_divProj.setBaseEquatorialScale(scale);
		m_divProj.setEquatorialScale(scale);
		////////////////////////////////////////////////////////
		// Simulate new DivPanel DivWorker
		m_dw = new DivWorker();
		m_dw.setProjection(m_divProj);
		m_dw.setDiv(m_vw.getGeoCenter());
		m_vp.setDivWorker(m_dw);
		/////////////////////////////////////////////////////////
		// Simulate view resizing
		m_vp.setSize(vwWidth,vwHeight);
		/////////////////////////////////////////////////////////
		// create Tile builder and set values to match view resize
		m_tb = new TileBuilder();
		m_tb.setDivWorker(m_dw);
		m_tb.setProjection(m_divProj);
		m_tb.setDivLevel(level);
		m_tb.setMapViewWorker(m_vp.getVpWorker());
		m_tb.upadteViewCenter(m_vw.getGeoCenter());
		m_tb.getTileViewWorker().setDimension(new ViewDimension(1200,vwHeight));
		m_dw.resize(vwWidth, vwHeight);
	}

	protected int get180TileNum(int level, int i){
		int[] numY = {1, 1, 1, 3};
		if (level == 0){
			return numY[i];
		}
		else if (level == 1){
			return 2;
		}
		return 2;
	}
	
	@Test
	public void testHowMany_512_180_YTiles(){
		before(512, 180);
		LayerSet ls = createLayerSet(0);
		LayerSetWorker lw = new LayerSetWorker();
		//GeodeticCoords gc = new GeodeticCoords(0,0,AngleUnit.DEGREES);
		
		int[] height = { 200, 400, 500, 600, };
		for (int i = 0; i < 4; i++){
			m_vp.setSize(1200,height[i]);

			for ( int level = 0; level < 2; level++){
				initializeDivStuf(level,1200,height[i]);
				lw.setDivProjection(m_divProj);
				double degFactor = ( level < 1 ? 1 : Math.pow(2, level) );
				double degWidth = ls.getStartLevelTileWidthInDeg()/degFactor;
				double degHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
				TileCoords tc = lw.findTile(ls, level, degWidth , degHeight, m_vw.getGeoCenter());
				m_tb.positionTileOffsetForDiv(tc);
				m_tb.computeHowManyYTiles(height[i], ls.getPixelHeight(), tc.getOffsetY());
				assertEquals(get180TileNum(level, i),m_tb.m_cyTiles);
			}
		}
	}
	
	protected int get90TileNum(int level, int i){
		if (level == 0){
			int[] numY = {2, 2, 4, 4};
			return numY[i];
		}
		else if (level == 1){
			int[] numY = {2, 2, 4, 4};
			return numY[i];
		}
		return 2;
	}

	@Test
	public void testHowMany_256_90_YTiles(){
		before(256, 90);
		LayerSet ls = createLayerSet(1);
		LayerSetWorker lw = new LayerSetWorker();
		//GeodeticCoords gc = new GeodeticCoords(0,0,AngleUnit.DEGREES);
		
		int[] height = { 200, 400, 520, 600, };
		for (int i = 0; i < 4; i++){
			m_vp.setSize(1200,height[i]);
			//ViewDimension vd = new ViewDimension(600,height[i]);
			for ( int level = 0; level < 2; level++){
				initializeDivStuf(level,1200,height[i]);
				lw.setDivProjection(m_divProj);
				double degFactor = ( level < 1 ? 1 : Math.pow(2, level) );
				double degWidth = ls.getStartLevelTileWidthInDeg()/degFactor;
				double degHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
				TileCoords tc = lw.findTile(ls, level,degWidth , degHeight, m_vw.getGeoCenter());
				m_tb.positionTileOffsetForDiv(tc);
				m_tb.computeHowManyYTiles(height[i], ls.getPixelHeight(), tc.getOffsetY());
				assertEquals(get90TileNum(level,i),m_tb.m_cyTiles);
			}
		}
	}
	
	// Simulate map layers
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
	  	    ls.setUrlPattern("{server}/{data}/{quadkey}");
	  	    ls.setAutoRefreshOnTimer(false);
			ls.setPixelWidth(256);
			ls.setPixelHeight(256);
			ls.setStartLevel(0);
			ls.setStartLevelTileHeightInDeg(90);
			ls.setStartLevelTileWidthInDeg(90);
			ls.setZeroTop(true);
		}
		else if ( j == 2 ){
			ls.setServer("http://bv.moesol.com/rpf-ww-server"); 
			ls.setData("BMNG (Shaded %2B Bathymetry) Tiled - 5.2004");
			ls.setUrlPattern("{server}/tileset/BMNG/{data}/level/{level}/x/{x}/y/{y}");
			ls.setSrs("EPSG:4326");
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
