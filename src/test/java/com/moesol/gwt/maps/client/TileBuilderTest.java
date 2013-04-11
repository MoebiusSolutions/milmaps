/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.HandlerManager;

public class TileBuilderTest {
	private DivWorker m_dw;
	private ViewWorker m_vw;
	//private DivDimensions m_dims = new DivDimensions();
	private ViewDimension m_viewDims;;
	private IProjection m_proj, m_mapProj, m_divProj;
	private ViewPort m_vp;
	private TileBuilder m_tb;
	
	int m_dpi = AbstractProjection.DOTS_PER_INCH;
	protected double m_scrnMpp = 2.54/m_dpi*100.0;
																  // map size
	public double EarthCirMeters  = 2.0*Math.PI*IProjection.EARTH_RADIUS_METERS;
	public double MeterPerDeg  = EarthCirMeters/360.0;
	
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
	
	protected double computeScale(double deg, int pix) {
		double mpp = deg*(MeterPerDeg / pix);
		return (m_scrnMpp / mpp);
	}
	
	
	void initializeDivStuf(int level, boolean forceScale, int vwWidth, int vwHeight) {
		// Simulate div for given level
		double eqScale = m_proj.getBaseEquatorialScale();
		double scale = eqScale*(1 << level);
		m_divProj = Projection.createProj(IProjection.T.CylEquiDist);
		m_divProj.setBaseEquatorialScale(scale);
		if (forceScale){
			// we will force the scale to match 36 degree tiles
			scale = computeScale(36, 512);
			m_divProj.setBaseEquatorialScale(scale);
			scale = scale*(1 << level);
		}
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
				initializeDivStuf(level,false,1200,height[i]);
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
				initializeDivStuf(level,false,1200,height[i]);
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
	
	@Test
	public void testFindBestLayerSet(){
		testFindBestLayerSet(0,0,false);
		testFindBestLayerSet(3,2,true);
		testFindBestLayerSet(3,1,false);
	}
	
	protected void assertOnlyOnePriorityImageLayer(ArrayList<TiledImageLayer> array){
		int n = array.size();
		int count = 0;
		for (int i = 0; i < n; i++){
			if ( array.get(i).isPriority()){
				count ++;
			}
		}
		assertEquals(1,count);
	}
	
	protected void testFindBestLayerSet(int level, int j, boolean forceScale){
		before(512, 180);
		initializeDivStuf(level,forceScale,1200,520);
		ArrayList<TiledImageLayer> array = createTiledImageLayerList();
		LayerSetWorker lw = new LayerSetWorker();
		m_tb.setTileImageLayers(array);
		lw.setDivProjection(m_divProj);
		m_tb.setLayerBestSuitedForScale();
		assertEquals(array.get(j).getLayerSet().getData(),m_tb.getBestLayerData());	
		assertOnlyOnePriorityImageLayer(array);
	}
	
	protected ArrayList<TiledImageLayer> createTiledImageLayerList(){
		ArrayList<TiledImageLayer> imageLayers = new ArrayList<TiledImageLayer>();
		for (int i = 0; i < 3; i++){
			LayerSet ls = createLayerSet(i);
			TiledImageLayer til = new TiledImageLayer(null,null,null, m_dw, ls);
			imageLayers.add(til);
		}
		return imageLayers;
	}
	
	// Simulate map layers
	protected LayerSet createLayerSet( int j ){
		LayerSet ls = new LayerSet();
		if ( j == 0 ){
	  	    ls.setServer("http://TestServer");
	  	    ls.setData("Test512-180");
	  	    ls.setUrlPattern("{server}/{data}/{quadkey}");
	  	    ls.setAutoRefreshOnTimer(false);
			ls.setPixelWidth(512);
			ls.setPixelHeight(512);
			ls.setStartLevel(0);
			ls.setMaxLevel(2);
			ls.setStartLevelTileHeightInDeg(180);
			ls.setStartLevelTileWidthInDeg(180);
			ls.setZeroTop(true);
		}
		else if ( j == 1 ){
	  	    ls.setServer("http://TestServer");
	  	    ls.setData("Test256-90");
	  	    ls.setUrlPattern("{server}/{data}/{quadkey}");
	  	    ls.setAutoRefreshOnTimer(false);
			ls.setPixelWidth(256);
			ls.setPixelHeight(256);
			ls.setStartLevel(0);
			ls.setMinLevel(2);
			ls.setStartLevelTileHeightInDeg(90);
			ls.setStartLevelTileWidthInDeg(90);
			ls.setZeroTop(true);
		}
		else if ( j == 2 ){
			ls.setServer("http://bv.moesol.com/rpf-ww-server"); 
			ls.setData("Test512-36");
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
