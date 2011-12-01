package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class LayerSetWorker {
	
	protected IProjection m_proj = null;
	protected final TileXY m_tile = new TileXY();
	protected final GeodeticCoords m_gc = new GeodeticCoords();
	
	public LayerSetWorker(){
		
	}
	
	public LayerSetWorker( IProjection p ){
		m_proj = p;
	}
	
	public void setProjection( IProjection p ){
		m_proj = p;
	}
	
	public IProjection getProjection(){
		return m_proj;
	}
	
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
    	
    	int drawWidth  = ls.getDrawPixelHeight();
    	int drawHeight = ls.getDrawPixelWidth();
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
    	
    	m_gc.setPhi(dLat, AngleUnit.DEGREES);
    	m_gc.setLambda( dLng, AngleUnit.DEGREES);
    	
    	return m_proj.geodeticToWorld(m_gc);
    }
    
    public TileCoords findTile( 
    		LayerSet ls, 
    		int level,
    		double tileDegWidth, 
    		double tileDegHeight, 
    		GeodeticCoords gc ) 
    {

    	int drawPixWidth  = compTileDrawWidth( ls, level );
    	int drawPixHeight = compTileDrawHeight( ls, level );
    	ls.setDrawPixelWidth(drawPixWidth);
    	ls.setDrawPixelHeight(drawPixHeight);
    	TileXY tile = geoPosToTileXY(ls, gc);
    	int modX = tile.m_x;
    	int modY = tile.m_y;
    	TileCoords tileCoords = new TileCoords( modX, modY );

    	WorldCoords wc = tileXYToTopLeftXY( ls, false, level, tile );
    	tileCoords.setOffsetX( wc.getX());
    	tileCoords.setOffsetY( wc.getY());
    	///////////
    	tileCoords.setTileWidth( ls.getPixelWidth() );
    	tileCoords.setTileHeight( ls.getPixelHeight() );   
		
    	tileCoords.setDrawTileWidth( drawPixWidth );
    	tileCoords.setDrawTileHeight( drawPixHeight );
    
    	tileCoords.setDegWidth(tileDegWidth);
    	tileCoords.setDegHeight(tileDegHeight);
    	tileCoords.setLevel(level);
    	return tileCoords;
    }
}
