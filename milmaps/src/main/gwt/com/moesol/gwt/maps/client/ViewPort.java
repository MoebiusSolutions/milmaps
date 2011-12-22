package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;




public class ViewPort {
	private final ViewDimension m_dimension = new ViewDimension(600, 400);
	private IProjection m_projection = null;
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
	
	public ViewPort() {
	}
	
	public IProjection getProjection() {
		return m_projection;
	}
	public void setProjection(IProjection proj) {
		m_projection = proj;
		GeodeticCoords gc = m_projection.getViewGeoCenter();
		m_worldCenter.copyFrom(m_projection.geodeticToWorld(gc));
		m_projection.setViewSize(m_dimension);
	}
	
	public DialogBox alertWidget(final String header, String content) {
        final DialogBox box = new DialogBox();
        final VerticalPanel panel = new VerticalPanel();
        final String strContent = ".  " + content + "  .";
        box.setText(header);
        panel.add(new Label( strContent ));
        final Button btnClose = new Button("Close");
        ClickHandler handler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                box.hide();
            }
        };
        btnClose.addClickHandler(handler);
        
        // few empty labels to make widget larger
        final Label emptyLabel = new Label("");
        emptyLabel.setSize("auto","25px");
        panel.add(emptyLabel);
        panel.add(emptyLabel);
        btnClose.setWidth("90px");
        panel.add(btnClose);
        panel.setCellHorizontalAlignment(btnClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        box.getElement().getStyle().setProperty("zIndex", Integer.toString(9000) );
        return box;
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
	

    public TileCoords findTile(  int level, GeodeticCoords gc ) {
    	int drawTileSize = m_projection.adjustSize(level, m_tilePixWidth);
    	//int drawTileHeight = m_projection.adjustSize(level, m_tilePixHeight);
    	TileXY tile = m_projection.geoPosToTileXY( level,  gc );
    	int modX = tile.m_x;
    	int modY = tile.m_y;
    	TileCoords tileCoords = new TileCoords( modX, modY );

    	WorldCoords wc = m_projection.tileXYToTopLeftXY( level,  tile );
    	tileCoords.setOffsetX( wc.getX());
    	tileCoords.setOffsetY( wc.getY());
    	///////////
    	tileCoords.setTileWidth( m_tilePixWidth );
    	tileCoords.setTileHeight( m_tilePixHeight );    
    	tileCoords.setDrawTileWidth( drawTileSize );
    	tileCoords.setDrawTileHeight( drawTileSize );
    
    	tileCoords.setDegWidth(m_tileDegWidth);
    	tileCoords.setDegHeight(m_tileDegHeight);
    	tileCoords.setLevel(level);
    	return tileCoords;
    }
    
	/**
	 * Determines how many tiles are needed and positions them
	 * so that the view port is filled.
	 * 
	 * @param TiledImageLayer
	 * @param int level
	 * @return array of tile coordinates
	 * 
	 */
	public TileCoords[] arrangeTiles( TiledImageLayer layer, int level ) {
		LayerSet ls = layer.getLayerSet();
		int dpi = m_projection.getScrnDpi();
		double lsScale = layer.findScale(dpi, level);
		return arrangeTiles( ls, lsScale, level );
	}
    
    
	/**
	 * Determines how many tiles are needed and positions them
	 * so that the view port is filled.
	 * 
	 * @param LayerSet
	 * @param int level
	 * @return array of tile coordinates
	 * 
	 */
	public TileCoords[] arrangeTiles( LayerSet ls, double lsScale, int level ) {
		m_tilePixWidth  = ls.getPixelWidth();
		m_tilePixHeight = ls.getPixelHeight();
		int tLevel = level - ls.getStartLevel();
		double degFactor = ( tLevel < 1 ? 1 : Math.pow(2, tLevel) );
		m_tileDegWidth  = ls.getStartLevelTileWidthInDeg()/degFactor;
		m_tileDegHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
		GeodeticCoords gc = m_projection.getViewGeoCenter();
		m_worldCenter.copyFrom(m_projection.geodeticToWorld(gc));
		if ( m_projection.getScale() == 0.0 ){
			m_projection.setScale(lsScale);
		}

		m_centerTile = findTile(  level, gc );
		positionCenterOffsetForView();
		computeHowManyXTiles();
		computeHowManyYTiles();
		
		TileCoords[] r = makeTilesResult(level);
		buildTilesRelativeToCenter( r, ls.isZeroTop() );
		
		return r;
	}
	
	public ViewCoords worldToView(WorldCoords wc, boolean checkWrap) {
		ViewCoords r = m_returnedViewCoords;
		r.setX(wc.getX() - m_worldCenter.getX() + getCenterX());
		r.setY(-(wc.getY() - m_worldCenter.getY()) + getCenterY()); // flip y axis
		
		// Check for world wrap
		// We may want to remove the wrap check all together.
		if ( checkWrap == true ){ 
			if (r.getX() < 0) {
				int checkX = r.getX() + m_projection.getWorldDimension().getWidth();
				r.setX(checkX);
			} else if (r.getX() >= m_dimension.getWidth()) {
				int checkX = r.getX() - m_projection.getWorldDimension().getWidth();
				r.setX(checkX);
			}
		}
		return r;
	}
	
	public WorldCoords viewToWorld(ViewCoords in) {
		m_returnedWorldCoords.setX(in.getX() + m_worldCenter.getX());
		m_returnedWorldCoords.setY(in.getY() + m_worldCenter.getY());
		return m_returnedWorldCoords;
	}

	private TileCoords[] makeTilesResult(int level) {
		int count = m_cyTiles * m_cxTiles;
		TileCoords[] r = new TileCoords[count];
		return r;
	}

	private void buildTilesRelativeToCenter( TileCoords[] r, boolean zeroTop ) {
		for (int ty = 0; ty < m_cyTiles; ty++) {
			for (int tx = 0; tx < m_cxTiles; tx++) {
				TileCoords tc = makeTilePositionedFromCenter( tx, ty, zeroTop );
				setTile(r, tx, ty, tc);
			}
		}
	}

	private TileCoords makeTilePositionedFromCenter( int tx, int ty, boolean zeroTop ) {
		int tileWidth  = m_centerTile.getDrawTileWidth();//(int)(m_tilePixWidth*factor);
		int tileHeight = m_centerTile.getDrawTileHeight();//(int)(m_tilePixHeight*factor);
		int centerOffsetXIdx = tx - m_leftTiles;
		int centerOffsetYIdx = ty - m_topTiles;
		int centTileX = m_centerTile.getX();
		int centTileY = m_centerTile.getY();
		
		int x = centTileX + centerOffsetXIdx;
		int y = centTileY - centerOffsetYIdx; // Flip y
		if ( zeroTop  ){
			// since tile is zero-top, we need to translate it.
			int numYTiles = getNumYTiles();
			y = (numYTiles - y - 1);
		}
		
		//if (badYTile(y)) {
		//	return null;
		//}
		x = wrapTileX(x);
		TileCoords tc = new TileCoords(x,y);
		int offsetX = centerOffsetXIdx * tileWidth + m_centerTile.getOffsetX();
		int offsetY = centerOffsetYIdx * tileHeight + m_centerTile.getOffsetY();
		tc.setOffsetX(offsetX);
		tc.setOffsetY(offsetY);
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

	/**
	 * @param vc ViewCoords
	 * @return true if {@code vc} is in the view port
	 */
	public boolean isInViewPort(ViewCoords vc) {
		if (vc.getX() < 0) {
			return false;
		}
		if (vc.getX() >= m_dimension.getWidth()) {
			return false;
		}
		if (vc.getY() < 0) {
			return false;
		}
		if (vc.getY() >= m_dimension.getHeight()) {
			return false;
		}
		return true;
	}
	
	/**
	 * @param wc WorldCoords
	 * @return true if {@code wc} is contained in view port
	 */
	public boolean isInViewPort(WorldCoords wc) {
		ViewCoords vc = worldToView(wc, false);
		return isInViewPort(vc);
	}

	/**
	 * @param gc GeodeticCoords
	 * @return true if {@code gc} is contained in view port
	 */
	public boolean isInViewPort(GeodeticCoords gc) {
		WorldCoords wc = m_projection.geodeticToWorld(gc);
		return isInViewPort(wc);
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

//	private boolean badYTile(int y) {
//		if (y < 0) {
//			return true;
//		}
//		if (y >= getNumYTiles()) {
//			return true;
//		}
//		return false;
//	}

	private int wrapTileX(int x) {
		int xTiles = getNumXTiles();
		if (x < 0) {
			return xTiles + x;
		}
		return x % xTiles;
	}
	
	public int getNumXTiles() {
		return m_projection.getNumXtiles(m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return m_projection.getNumYtiles(m_tileDegWidth);
	}

	private void setTile(TileCoords[] r, int xIdx, int yIdx, TileCoords tc) {
		r[yIdx * m_cxTiles + xIdx] = tc;
	}

	private void computeHowManyXTiles() {
		int tileWidth = m_centerTile.getDrawTileWidth();
		int leftDist = m_centerTile.getOffsetX();
		int rightDist = m_dimension.getWidth() - (leftDist + tileWidth);
		
		m_leftTiles = leftDist <= 0 ? 0 : (leftDist + tileWidth - 1) / tileWidth;
		m_rightTiles = rightDist <= 0 ? 0 : (rightDist + tileWidth - 1) / tileWidth;
		m_cxTiles = 1 + m_leftTiles + m_rightTiles;
	}

	private void computeHowManyYTiles() {
		int tileHeight = m_centerTile.getDrawTileHeight();
		int topDist = m_centerTile.getOffsetY();
		int bottomDist = m_dimension.getHeight() - (topDist + tileHeight);
		
		m_topTiles = topDist <= 0 ? 0 : (topDist + tileHeight - 1) / tileHeight;
		m_bottomTiles = bottomDist <= 0 ? 0 : (bottomDist + tileHeight - 1) / tileHeight;
		m_cyTiles = 1 + m_topTiles + m_bottomTiles;
	}

	private void positionCenterOffsetForView() {
	  // This routine positions the center tile relative to the view it sits in.
		m_returnedWorldCoords.setX(m_centerTile.getOffsetX());
		m_returnedWorldCoords.setY(m_centerTile.getOffsetY());
		ViewCoords vc = worldToView(m_returnedWorldCoords, false);
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
