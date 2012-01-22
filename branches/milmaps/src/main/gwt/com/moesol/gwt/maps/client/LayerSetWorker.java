package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Degrees;

public class LayerSetWorker {
	private final double EarthCirMeters  = 2.0*Math.PI*6378137;
	private final double MeterPerDeg  = EarthCirMeters/360.0;
	protected IProjection m_proj = null;
	protected final TileXY m_tile = new TileXY();
	protected final GeodeticCoords m_gc = new GeodeticCoords();
	
	public LayerSetWorker(){
		
	}
	
	public LayerSetWorker( IProjection p ){ m_proj = p; }
	
	public void setProjection( IProjection p ){ m_proj = p; }
	
	public IProjection getProjection(){ return m_proj; }
	
    protected double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    
    public int compTileDrawWidth( LayerSet ls, int level  ){
		int tLevel = level - ls.getStartLevel();
		double f = ( tLevel < 1 ? 1 : Math.pow(2, tLevel) );
		double degWidth  = ls.getStartLevelTileWidthInDeg();
		if ( degWidth > 359 && level == 0 ){
			return( m_proj.compWidthInPixels(0, 180)*2);
		}
		degWidth /= f;
    	return m_proj.compWidthInPixels(0, degWidth);	
    }
    
    public int compTileDrawHeight( LayerSet ls, int level  ){		
    	// Tiles are square so we can use this.
    	return compTileDrawWidth(ls,level);
    }   
    
    public int getNumberOfRows( LayerSet ls, int level ){
    	return (int)((180.0 /ls.getStartLevelTileHeightInDeg())*(1<<level));
    }
    
    public int getNumberOfCols( LayerSet ls, int level ){
    	return (int)((360.0 /ls.getStartLevelTileWidthInDeg())*(1<<level));
    }
    
    protected static double computeMinLat(LayerSet ls){
    	int numRows = (int)(180.0/ls.getStartLevelTileHeightInDeg() );
    	double minLat = -1*(numRows*ls.getStartLevelTileHeightInDeg())/2.0;
    	return minLat;
    }
    
	public TileXY cyleGeoPosToTileXY( int level, int pixWidth, 
			  					  double degW, double degH, GeodeticCoords g  )
	{
		// This computes the tile (x,y) with y = 0 as the bottom tile.
		int tLevel = Math.max(0,level);
		double degCellWidth = degW/Math.pow(2.0,tLevel);
		double degCellHeight = degH/Math.pow(2.0,tLevel);
		double degMag = Math.abs(g.getLambda(AngleUnit.DEGREES)+180);
		m_tile.m_x = (int)(degMag/degCellWidth);
		degMag = Math.abs(g.getPhi(AngleUnit.DEGREES)+90);
		m_tile.m_y = (int)(degMag/degCellHeight);
		return m_tile;
}
    
	public TileXY mercGeoPosToTileXY( int level, int pixSize,  
			  					  double degW, double degH, GeodeticCoords g ){
		double minLat = m_proj.getMinLat();
		double maxLat = m_proj.getMaxLat();
		double lat = clip(g.getPhi(AngleUnit.DEGREES),   minLat, maxLat);
		double lng = clip(g.getLambda(AngleUnit.DEGREES), -180.0, 180.0);
		
		double x = (lng + 180) / 360; 
		double sinLat = Math.sin(lat * Math.PI / 180);
		// The original equation with y = 0 at the top of the map is
		// y = 0.5 - Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI);
		// but since we want y = 0 at the bottom of the map we will subtract the 
		// above equation from 1 which gives us:
		double y = 0.5 + Math.log((1 + sinLat) / (1 - sinLat)) / (4 * Math.PI) ;
		
		int mapSize = (int) ( pixSize << level );
		double pixX = clip(x * mapSize + 0.5, 0, mapSize);
		double pixY = clip(y * mapSize + 0.5, 0, mapSize);
		m_tile.m_x =  (int)Math.floor(pixX/ pixSize);
		m_tile.m_y =  (int)Math.floor(pixY/pixSize);
		// translate so it is based on row zero at the bottom of the map
		//int j = (1<<level) - 1;
		//m_tile.m_y = j - m_tile.m_y;
		return m_tile;
	}
	
	public TileXY geoPosToTileXY( LayerSet ls, int level, GeodeticCoords g  )
	{
		// This computes the tile (x,y) with y = 0 as the bottom tile.
		int pixWidth = ls.getPixelWidth();
		double degW = ls.getStartLevelTileWidthInDeg(); 
		double degH = ls.getStartLevelTileHeightInDeg();
		int tLevel = Math.max(0,level);
		if ( m_proj.getType() == IProjection.T.Mercator ){
			return  mercGeoPosToTileXY( tLevel, pixWidth, degW, degH,  g  );
		}
		else{
			return  cyleGeoPosToTileXY( tLevel, pixWidth, degW, degH,  g  );
		}
	}
	
	
/*
	public TileXY geoPosToTileXY( LayerSet ls,  GeodeticCoords g ){
		// This is based on zero row at the bottom of the map
		//level = Math.max(0, level);
		double minLat = computeMinLat(ls);
		double maxLat = -1*minLat;
        double lat = clip(g.getPhi(AngleUnit.DEGREES), minLat, maxLat);
        double lng = clip(g.getLambda(AngleUnit.DEGREES), -180.0, 180.0);
        // based on zero bottom
        int pixX = m_proj.lngDegToPixX(lng);
        int pixY = m_proj.latDegToPixY(lat);
        m_tile.m_x = pixX/ls.getDrawPixelWidth();
        m_tile.m_y = pixY/ls.getDrawPixelHeight();
        return m_tile;
	}
*/    
	/**
	 * tileXY2TopLeftXY: computes the (i,j) tile's top left corner in Map World Coords.
	 * @param ls: The layer set for the given tiles
	 * @param zeroTop: This flag is true if the i = 0 starts at the top of the map
	 * @param level: The level of the tiles 
	 * @param tile: a TileXY structure that contains the (ith,jth) position of the tile.
	 * @return
	 */
    public WorldCoords tileXYToTopLeftXY( LayerSet ls, boolean zeroTop, int level, TileXY tile  ){
    	return tileXYToTopLeftXY( ls, zeroTop, level, tile.m_x, tile.m_y );
    }
    
    /**
     * tileXY2TopLeftXY: computes the (i,j) tile's top left corner in Map World Coords. 
     * @param ls: The layer set for the given tiles
     * @param zeroTop: This flag is true if the i = 0 starts at the top of the map
     * @param level: The level of the tiles 
     * @param tileX: This is the ith file going left to right.
     * @param tileY: This is the jth tile going (top to bot) or (bot to top) depending on zeroTop.
     * @return: The tiles top/left pixel value in world coordinates.
     */
    
    public WorldCoords tileXYToTopLeftXY( LayerSet ls, boolean zeroTop, int level, int tileX, int tileY ){
    	
    	int drawWidth  = ls.getPixelWidth();
    	int drawHeight = ls.getPixelHeight();
    	int numRows = getNumberOfRows(ls, level);
    	return tileXY2TopLeftXY( zeroTop, numRows, tileX, tileY, drawWidth, drawHeight );
    }
    
    /**
     * tileXY2TopLeftXY: computes the (i,j) tile's top left corner in Map World Coords. 
     * @param zeroTop: This flag is true if the i = 0 starts at the top of the map
     * @param numRows: The number of tile rows for the whole map  
     * @param tileX: This is the ith file going left to right.
     * @param tileY: This is the jth tile going (top to bot) or (bot to top) depending on zeroTop.
     * @param drawWidth: This is the width of the tile in pixels for the given map scale.
     * @param drawHeight: This is the height of the tile in pixels for the given map scale.
     * @return: The tiles top/left pixel value in world coordinates.
     */
    public WorldCoords tileXY2TopLeftXY( boolean zeroTop, int numRows,
    									  int tileX, int tileY, 
    									  int drawWidth, int drawHeight ){
    	// TODO This sucks and needs to be reworked
    	int topLeftX = tileX;
    	int topLeftY = ( zeroTop ? numRows - tileY : tileY + 1 );
    	
    	int pixX = topLeftX*drawWidth;
    	int pixY = topLeftY*drawHeight;
    	
    	double dLat = m_proj.yPixToDegLat(pixY);
    	double dLng = m_proj.xPixToDegLng(pixX);
    	
    	return m_proj.geodeticToWorld(Degrees.geodetic(dLat, dLng));
    }
    
    public TileCoords findTile( 
    		LayerSet ls, 
    		int level,
    		double tileDegWidth, 
    		double tileDegHeight, 
    		GeodeticCoords gc ) 
    {
    	//int drawPixWidth  = compTileDrawWidth( ls, level );
    	//int drawPixHeight = compTileDrawHeight( ls, level );
 
    	//ls.setDrawPixelWidth(drawPixWidth);
    	//ls.setDrawPixelHeight(drawPixHeight);
    	TileXY tile = geoPosToTileXY(ls, level, gc);
    	int modX = tile.m_x;
    	int modY = tile.m_y;
    	TileCoords tileCoords = new TileCoords( modX, modY );

    	WorldCoords wc = tileXYToTopLeftXY( ls, false, level, tile );
    	tileCoords.setOffsetX( wc.getX());
    	tileCoords.setOffsetY( wc.getY());
    	///////////
    	tileCoords.setTileWidth( ls.getPixelWidth() );
    	tileCoords.setTileHeight( ls.getPixelHeight() );   
		
    	//tileCoords.setDrawTileWidth( drawPixWidth );
    	//tileCoords.setDrawTileHeight( drawPixHeight );
    
    	tileCoords.setDegWidth(tileDegWidth);
    	tileCoords.setDegHeight(tileDegHeight);
    	tileCoords.setLevel(level);
    	return tileCoords;
    }
    
	// we can make the latitude as an input param if we want to
	// compute scale at latitudes other than the equator.

	// 1852 meters/ nautical mile so 1 deg = 60*1852 meters = 111,120 meters.
	// mpp is meters per pixel.
	// level_n_mpp = (level_0_mpp)/2^n so level_n_scale =
	// (m_mpp/level_0_mpp)*2^n
	// We wan to find n so so that level_n_Scale close to given projScale
	//
	// Hence n = ( log(projScale) + log(level_0_mpp) - log(m_mpp) )/log(2);

	// dpi is pixels per inch for physical screen
	public int findLevel( LayerSet layerSet, double dpi, double projScale) {
		double mpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		double m_dx = layerSet.getPixelWidth();
		double l_mpp = layerSet.getStartLevelTileWidthInDeg()* (MeterPerDeg / m_dx);
		// compute the best level.
		if ( projScale == 0.0 ){
			projScale = (mpp / l_mpp);
		}
		double logMess = Math.log(projScale) + Math.log(l_mpp)- Math.log(mpp);
		double dN = logMess / Math.log(2);
		return (int)(Math.rint(dN)) + layerSet.getStartLevel();
	}

	public double findScale( LayerSet layerSet, double dpi, int level ) {
		double mpp = 2.54 / (dpi * 100);
		double m_dx = layerSet.getPixelWidth();
		double l_mpp = layerSet.getStartLevelTileWidthInDeg()* (MeterPerDeg / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}
}
