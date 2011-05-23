package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.units.AngleUnit;



public class ViewPort {
	private final ViewDimension m_dimension = new ViewDimension(600, 400);
	private final IProjection m_projection;
	private final ViewCoords m_returnedViewCoords = new ViewCoords();
	
	private final WorldCoords m_worldCenter = new WorldCoords();
	private final WorldCoords m_returnedWorldCoords = new WorldCoords();
	private int m_tilePixWidth  = 512;
	private int m_tilePixHeight = 512;
	private double m_tileDegWidth  = 180;
	private double m_tileDegHeight = 180;
	private int m_level = 0;
  
	private int m_cxTiles;
	private int m_cyTiles;
	private TileCoords m_centerTile;
	private int m_leftTiles;
	private int m_rightTiles;
	private int m_topTiles;
	private int m_bottomTiles;
	
	public ViewPort(IProjection proj) {
		m_projection = proj;
	}
	
	public void setTileDegWidth( double deg ){
		m_tileDegWidth = deg;
	}
	public double getTileDegWidth(){ return m_tileDegWidth; }
	
	public void setTileDegHeight( double deg ){
	  	m_tileDegHeight = deg;
	}
	public double getTileDegHeight(){ return m_tileDegHeight; }
  
    public void setTilePixWidth( int pix ){m_tilePixWidth = pix;}
    public int getTilePixWidth(){ return m_tilePixWidth; }
    public void setTilePixHeight( int pix ){m_tilePixHeight = pix;}
    public int getTilePixHeight(){ return m_tilePixHeight; }
	
    public TileCoords findTile(  int level, double lsScale, GeodeticCoords gc ) {
    	int tempTileWidth  = m_projection.lngDegToPixMag(m_tileDegWidth);
    	double centLat = gc.getPhi(AngleUnit.DEGREES);
    	int tempTileHeight = m_projection.latDegToPixMag(centLat,m_tileDegHeight);
    	WorldCoords wc = m_projection.geodeticToWorld(gc); // center point
    	int modX = wc.getX() / tempTileWidth;
    	int modY = wc.getY() / tempTileHeight;
    	TileCoords tileCoords = new TileCoords( modX, modY );
    	// This is the magnitude of the difference between the top left
    	// corner of the tile that contains the center point and the 
    	// center point.
    	GeodeticCoords topLeft = new GeodeticCoords();
    	topLeft.setPhi(-90+(modY+1)*m_tileDegHeight, AngleUnit.DEGREES);
    	topLeft.setLambda(-180 + modX*m_tileDegWidth, AngleUnit.DEGREES);
    	// setting the tile height one last time for mercator proj.
    	double tLat = topLeft.getPhi(AngleUnit.DEGREES) - m_tileDegHeight/2;
    	tempTileHeight = m_projection.latDegToPixMag(tLat,m_tileDegHeight);
    	wc = m_projection.geodeticToWorld(topLeft);
    	tileCoords.setOffsetX( wc.getX());
    	tileCoords.setOffsetY( wc.getY());
    	///////////
    	tileCoords.setTileWidth( m_tilePixWidth );
    	tileCoords.setTileHeight( m_tilePixHeight );    
    	tileCoords.setDrawTileWidth( tempTileWidth );
    	tileCoords.setDrawTileHeight( tempTileHeight );
    
    	tileCoords.setDegWidth(m_tileDegWidth);
    	tileCoords.setDegHeight(m_tileDegHeight);
    	tileCoords.setLevel(level);
    	return tileCoords;
    }
	
	/**
	 * Determines how many tiles are needed and positions them
	 * so that the view port is filled.
	 * 
	 * @param center
	 * @return array of tile coordinates
	 */
	public TileCoords[] arrangeTiles( TiledImageLayer layer, int level ) {
		LayerSet ls = layer.getLayerSet();
		m_tilePixWidth  = ls.getPixelWidth();
		m_tilePixHeight = ls.getPixelHeight();
		int dpi = m_projection.getScrnDpi();
		double degFactor = ( level < 0 ? 1 : Math.pow(2, level) );
		m_tileDegWidth  = ls.getStartLevelTileWidthInDeg()/degFactor;
		m_tileDegHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
		GeodeticCoords gc = m_projection.getVpGeoCenter();
		m_worldCenter.copyFrom(m_projection.geodeticToWorld(gc));
		double lsScale = layer.findScale(dpi, level);
		if ( m_projection.getScale() == 0.0 ){
			m_projection.setScale(lsScale);
		}
		m_centerTile = findTile(  level, lsScale, gc );
		positionCenterOffsetForView();
		computeHowManyXTiles();
		computeHowManyYTiles();
		
		TileCoords[] r = makeTilesResult();
		buildTilesRelativeToCenter( r );
		
		return r;
	}
	
	public ViewCoords worldToView(WorldCoords v) {
		ViewCoords r = m_returnedViewCoords;
		r.setX(v.getX() - m_worldCenter.getX() + getCenterX());
		r.setY(-(v.getY() - m_worldCenter.getY()) + getCenterY()); // flip y axis
		
		// Check for world wrap
		if (r.getX() < 0) {
			int checkX = r.getX() + m_projection.getWorldDimension().getWidth();
			r.setX(checkX);
		} else if (r.getX() >= m_dimension.getWidth()) {
			int checkX = r.getX() - m_projection.getWorldDimension().getWidth();
			r.setX(checkX);
		}
		return r;
	}
	
	public WorldCoords viewToWorld(ViewCoords in) {
		m_returnedWorldCoords.setX(in.getX() + m_worldCenter.getX());
		m_returnedWorldCoords.setY(in.getY() + m_worldCenter.getY());
		return m_returnedWorldCoords;
	}

	private TileCoords[] makeTilesResult() {
		TileCoords[] r = new TileCoords[m_cyTiles * m_cxTiles];
		return r;
	}

	private void buildTilesRelativeToCenter( TileCoords[] r ) {
		for (int ty = 0; ty < m_cyTiles; ty++) {
			for (int tx = 0; tx < m_cxTiles; tx++) {
				TileCoords tc = makeTilePositionedFromCenter( tx, ty );
				setTile(r, tx, ty, tc);
			}
		}
	}

	private TileCoords makeTilePositionedFromCenter(int tx, int ty) {
		int tileWidth  = m_centerTile.getDrawTileWidth();//(int)(m_tilePixWidth*factor);
		int tileHeight = m_centerTile.getDrawTileHeight();//(int)(m_tilePixHeight*factor);
		int centerOffsetXIdx = tx - m_leftTiles;
		int centerOffsetYIdx = ty - m_topTiles;
		
		int x = m_centerTile.getX() + centerOffsetXIdx;
		int y = m_centerTile.getY() - centerOffsetYIdx; // Flip y
		
		//if (badYTile(y)) {
		//	return null;
		//}
		x = wrapTileX(x);
		TileCoords tc = new TileCoords(x,y);
		tc.setOffsetX(centerOffsetXIdx * tileWidth + m_centerTile.getOffsetX());
		tc.setOffsetY(centerOffsetYIdx * tileHeight + m_centerTile.getOffsetY());
		tc.setTileWidth(m_centerTile.getTileWidth());
		tc.setTileHeight(m_centerTile.getTileHeight());
		tc.setDrawTileWidth(m_centerTile.getDrawTileWidth());
		tc.setDrawTileHeight(m_centerTile.getDrawTileHeight());
		tc.setInViewPort( true );//computeInViewPort(tc));
		tc.setDegWidth(m_centerTile.getDegWidth());
		tc.setDegHeight(m_centerTile.getDegHeight());
		tc.setLevel(m_centerTile.getLevel());
		
		return tc;
	}

	boolean computeInViewPort(TileCoords tc) {
		if (tc.getOffsetX() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetX() >= m_dimension.getWidth()) {
			return false;
		}
		if (tc.getOffsetY() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetY() >= m_dimension.getHeight()) {
			return false;
		}
		return true;
	}

	private boolean badYTile(int y) {
		if (y < 0) {
			return true;
		}
		if (y >= getNumYTiles()) {
			return true;
		}
		return false;
	}

	private int wrapTileX(int x) {
		int xTiles = getNumXTiles();
		if (x < 0) {
			return xTiles + x;
		}
		return x % xTiles;
	}
	
	public int getNumXTiles() {
		return (int)(360.0 / m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return (int)(180.0 / m_tileDegHeight);
	}

	private void setTile(TileCoords[] r, int xIdx, int yIdx, TileCoords tc) {
		r[yIdx * m_cxTiles + xIdx] = tc;
	}

	private void computeHowManyXTiles() {
		int tileWidth = m_centerTile.getDrawTileWidth();
		int leftDist = m_centerTile.getOffsetX();
		int rightDist = m_dimension.getWidth() - (m_centerTile.getOffsetX() + tileWidth);
		
		m_leftTiles = leftDist <= 0 ? 0 : (leftDist + tileWidth - 1) / tileWidth;
		m_rightTiles = rightDist <= 0 ? 0 : (rightDist + tileWidth - 1) / tileWidth;
		m_cxTiles = 1 + m_leftTiles + m_rightTiles;
	}

	private void computeHowManyYTiles() {
		int tileHeight = m_centerTile.getDrawTileHeight();
		int topDist = m_centerTile.getOffsetY();
		int bottomDist = m_dimension.getHeight() - (m_centerTile.getOffsetY() + tileHeight);
		
		m_topTiles = topDist <= 0 ? 0 : (topDist + tileHeight - 1) / tileHeight;
		m_bottomTiles = bottomDist <= 0 ? 0 : (bottomDist + tileHeight - 1) / tileHeight;
		m_cyTiles = 1 + m_topTiles + m_bottomTiles;
	}

	private void positionCenterOffsetForView() {
	  // This routine positions the center tile relative the view it sits in.
		m_returnedWorldCoords.setX(m_centerTile.getOffsetX());
		m_returnedWorldCoords.setY(m_centerTile.getOffsetY());
		ViewCoords vc = worldToView(m_returnedWorldCoords);
		m_centerTile.setOffsetY(vc.getY());
		m_centerTile.setOffsetX(vc.getX());
	}

	private int getCenterX() {
		return m_dimension.getWidth() / 2;
	}

	private int getCenterY() {
		return m_dimension.getHeight() / 2;
	}
	
	public int getWidth() {
		return m_dimension.getWidth();
	}
	
	public int getHeight() {
		return m_dimension.getHeight();
	}
	
	public int getCxTiles() {
		return m_cxTiles;
	}
	
	public int getCyTiles() {
		return m_cyTiles;
	}
	
	public void setSize(int w, int h) {
		m_dimension.setWidth(w);
		m_dimension.setHeight(h);
		m_projection.setViewSize(m_dimension);
	}
	
	 public int getLevel() { return m_level; }
	
		/**
		 * Keep the view center x on the view and the y within the view port.
		 * 
		 * @param viewCenter
		 * @return viewCenter
		 */
		public void constrainAsWorldCenter(WorldCoords centerToUpdate) {
			WorldDimension dim = m_projection.getWorldDimension();
			if (centerToUpdate.getX() < 0) {
				centerToUpdate.setX(dim.getWidth() + centerToUpdate.getX());
			} else {
				centerToUpdate.setX(centerToUpdate.getX() % dim.getWidth());
			}
			
			int hmid = getCenterY();
			if (centerToUpdate.getY() < hmid) {
				centerToUpdate.setY(hmid);
			} if (centerToUpdate.getY() > dim.getHeight() - hmid) {
				centerToUpdate.setY(dim.getHeight() - hmid);
			}
		}
}
