package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;




public class ViewPort {
	private final ViewDimension m_viewDims = new ViewDimension(600, 400);
	private final DivDimensions m_divDims = new DivDimensions();
	private IProjection m_proj = null;
	private final WorldCoords m_returnedWorldCoords = new WorldCoords();
	private final ViewCoords  m_returnedViewCoords = new ViewCoords();
	private final DivCoords  m_returnedDivCoords = new DivCoords();
	private final LayerSetWorker m_lsWorker = new LayerSetWorker();
	private final ViewWorker m_vpWorker = new ViewWorker();
	private DivWorker m_divWorker;
	private int m_tilePixWidth  = 512;
	private int m_tilePixHeight = 512;
	private double m_tileDegWidth  = 180;
	private double m_tileDegHeight = 180;
	private int m_level = 0;
	private int m_oldLevel = -100;
  
	private int m_cxTiles;
	private int m_cyTiles;
	private TileCoords m_centerTile;
	private int m_leftTiles;
	private int m_rightTiles;
	private int m_topTiles;
	private int m_bottomTiles;
	
	public ViewPort(){
	}
	
	public void setProjection( IProjection proj ){
		m_proj = proj;
		m_lsWorker.setProjection(proj);
		m_vpWorker.intialize(m_viewDims, proj);
		
		GeodeticCoords g = new GeodeticCoords();
		m_vpWorker.setGeoCenter(g);
	}
	
	public ViewWorker getVpWorker(){ return m_vpWorker; }
	
	public void setDivWorker( DivWorker dw ){ 
		m_divWorker = dw; 
		m_divDims.copyFrom(dw.getDivDimension());
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
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
		int dpi = m_proj.getScrnDpi();
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
		//if ( level == m_oldLevel )
		//	return null;
		m_oldLevel = level;
		m_tilePixWidth  = ls.getPixelWidth();
		m_tilePixHeight = ls.getPixelHeight();
		int tLevel = level - ls.getStartLevel();

		double degFactor = ( tLevel < 1 ? 1 : Math.pow(2, tLevel) );
		m_tileDegWidth  = ls.getStartLevelTileWidthInDeg()/degFactor;
		m_tileDegHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
		GeodeticCoords gc = m_vpWorker.getGeoCenter();
		if ( m_proj.getScale() == 0.0 ){
			m_proj.setScale(lsScale);
		}
		m_centerTile = m_lsWorker.findTile( ls, level, m_tileDegWidth, m_tileDegHeight, gc );
		positionCenterOffsetForDiv();
		//TODO
		// we should probably use the view dimensions here to keep
		// the number of tile down to a reasonable value.
		computeHowManyXTiles(m_viewDims.getWidth()*2);
		computeHowManyYTiles(m_viewDims.getHeight()*2);
		
		TileCoords[] r = makeTilesResult(level);
		buildTilesRelativeToCenter( r, ls.isZeroTop() );

		return r;
	}
	
	public DivCoords worldToDiv(WorldCoords wc, boolean checkWrap) {
		DivCoords r = m_returnedDivCoords;
		r.copyFrom(m_divWorker.wcToDC(wc));

		// Check for world wrap
		// We may want to remove the wrap check all together.
		if ( checkWrap == true ){ 
			if (r.getX() < 0) {
				int checkX = r.getX() + m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			} else if (r.getX() >= m_divDims.getWidth()) {
				int checkX = r.getX() - m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			}
		}
		return r;
	}
	
	public ViewCoords worldToView(WorldCoords wc, boolean checkWrap) {
		ViewCoords r = m_returnedViewCoords;
		r.copyFrom(m_vpWorker.wcToVC(wc));
		
		// Check for world wrap
		// We may want to remove the wrap check all together.
		if ( checkWrap == true ){ 
			if (r.getX() < 0) {
				int checkX = r.getX() + m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			} else if (r.getX() >= m_viewDims.getWidth()) {
				int checkX = r.getX() - m_proj.getWorldDimension().getWidth();
				r.setX(checkX);
			}
		}
		return r;
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
	
    public int getNumberOfRows( double degHeight, int level ){
    	return (int)((180.0 /degHeight)*(1<<level));
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
			int level = m_centerTile.getLevel();
			y = ((1<<level) -y - 1);
		}
		
		if (badYTile(y)) {
			return null;
		}
		x = wrapTileX(x);
		TileCoords tc = new TileCoords(x,y);
		tc.setZeroTop(zeroTop);
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

	boolean computeInViewPort(TileCoords tc) {
		if (tc.getOffsetX() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetX() >= m_viewDims.getWidth()) {
			return false;
		}
		if (tc.getOffsetY() + tc.getTileHeight() < 0) {
			return false;
		}
		if (tc.getOffsetY() >= m_viewDims.getHeight()) {
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
		return m_proj.getNumXtiles(m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return m_proj.getNumYtiles(m_tileDegHeight);
	}

	private void setTile(TileCoords[] r, int xIdx, int yIdx, TileCoords tc) {
		r[yIdx * m_cxTiles + xIdx] = tc;
	}

	private void computeHowManyXTiles( int dimWidth ) {
		int tileWidth = m_centerTile.getDrawTileWidth();
		int wcX = m_divWorker.dcXtoWcX( m_centerTile.getOffsetX());
		int leftDist = m_vpWorker.wcXtoVcX(wcX);
		int rightDist = dimWidth - (leftDist + tileWidth);
		
		m_leftTiles = leftDist <= 0 ? 0 : (leftDist + tileWidth - 1) / tileWidth;
		m_rightTiles = rightDist <= 0 ? 0 : (rightDist + tileWidth - 1) / tileWidth;
		m_cxTiles = 1 + m_leftTiles + m_rightTiles;
	}

	private void computeHowManyYTiles( int dimHeight ) {
		int tileHeight = m_centerTile.getDrawTileHeight();
		int wcY = m_divWorker.dcYtoWcY( m_centerTile.getOffsetY());
		int topDist = m_vpWorker.wcYtoVcY(wcY);
		int bottomDist = dimHeight - (topDist + tileHeight);
		
		m_topTiles = topDist <= 0 ? 0 : (topDist + tileHeight - 1) / tileHeight;
		m_bottomTiles = bottomDist <= 0 ? 0 : (bottomDist + tileHeight - 1) / tileHeight;
		m_cyTiles = 1 + m_topTiles + m_bottomTiles;
	}
	
	private void positionCenterOffsetForDiv() {
	    // This routine positions the center tile relative to the view it sits in.
		m_returnedWorldCoords.setX(m_centerTile.getOffsetX());
		m_returnedWorldCoords.setY(m_centerTile.getOffsetY());
		DivCoords dc = worldToDiv(m_returnedWorldCoords, true);
		m_centerTile.setOffsetY(dc.getY());
		m_centerTile.setOffsetX(dc.getX());
	}
/*
	private void positionCenterOffsetForView() {
	    // This routine positions the center tile relative to the view it sits in.
		m_returnedWorldCoords.setX(m_centerTile.getOffsetX());
		m_returnedWorldCoords.setY(m_centerTile.getOffsetY());
		ViewCoords vc = worldToView(m_returnedWorldCoords, false);
		m_centerTile.setOffsetY(vc.getY());
		m_centerTile.setOffsetX(vc.getX());
	}
*/
	private int getCenterX() {
		return m_viewDims.getWidth() / 2;
	}

	private int getCenterY() {
		return m_viewDims.getHeight() / 2;
	}
	
	public int getWidth() {
		return m_viewDims.getWidth();
	}
	
	public int getHeight() {
		return m_viewDims.getHeight();
	}
	
	public int getCxTiles() {
		return m_cxTiles;
	}
	
	public int getCyTiles() {
		return m_cyTiles;
	}
	
	public void setSize(int w, int h) {
		m_viewDims.setWidth(w);
		m_viewDims.setHeight(h);
		m_vpWorker.setDimension(m_viewDims);
	}
	
	public int getLevel() { return m_level; }
	
		/**
		 * Keep the view center x on the view and the y within the view port.
		 * 
		 * @param viewCenter
		 * @return viewCenter
		 */
	public void constrainAsWorldCenter(WorldCoords centerToUpdate) {
		WorldDimension dim = m_proj.getWorldDimension();
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
