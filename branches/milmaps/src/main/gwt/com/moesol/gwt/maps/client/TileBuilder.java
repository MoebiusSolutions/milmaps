package com.moesol.gwt.maps.client;

import java.util.ArrayList;

public class TileBuilder {
	private int m_leftTiles;
	private int m_topTiles;
	private int m_cxTiles;
	private int m_cyTiles;
	
	private double m_tileDegWidth;
	private double m_tileDegHeight;
	
	private ViewDimension m_scaledViewDims = new ViewDimension();
	private LayerSetWorker m_lsWorker = new LayerSetWorker();
	private DivWorker m_divWorker = null;
	private int m_divLevel;
	
	private ArrayList<TiledImageLayer> m_tiledImageLayers;

	private ViewWorker m_tileViewWork = new ViewWorker();
	private TileCoords m_centerTile;
	
	IProjection m_divProj = null;
	
	public void setTileImageLayers( ArrayList<TiledImageLayer> tiledImageLayers ) {
		m_tiledImageLayers = tiledImageLayers;
	}
	
	public void setDivWorker( DivWorker dw ) {
		m_divWorker = dw; 
	}
	
	public void setProjection( IProjection proj ) { 
		m_divProj = proj;
		m_lsWorker.setProjection(proj);
		m_tileViewWork.setProjection(proj);
	}
	
	public void setViewCenterWc( WorldCoords wc ) {
		m_tileViewWork.setCenterInWc(wc);
	}
	
	public IProjection getProjection() { return m_divProj; }
	
	public void setDivLevel(int divLevel) {
		this.m_divLevel = divLevel;
	}
	
	public int getDivLevel() {
		return m_divLevel;
	}
	
	public boolean upadteViewCenter(GeodeticCoords gc){
		GeodeticCoords g = m_tileViewWork.getGeoCenter();
		if ( g.equals(gc) == false ){
			m_tileViewWork.setGeoCenter( gc );
			return true;
		}
		return false;
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
	public TileCoords[] arrangeTiles( int level, TiledImageLayer layer ) {
		LayerSet ls = layer.getLayerSet();
		int dpi = m_divProj.getScrnDpi();
		double lsScale = layer.findScale(dpi, level);
		return arrangeTiles( level, ls, lsScale );
	}
		
	// Note, in all these routines Use the div projection that has a 
	// fixed scale and is designed to be used at the level natural scale.
	private void computeHowManyXTiles( int dimWidth ) {
		int tileWidth = m_centerTile.getTileWidth();
		int wcX = m_divWorker.dcXtoWcX( m_centerTile.getOffsetX());
		int leftDist = m_tileViewWork.wcXtoVcX(wcX);
		int rightDist = dimWidth - (leftDist + tileWidth);
		
		m_leftTiles = leftDist <= 0 ? 0 : (leftDist + tileWidth - 1) / tileWidth;
		int rightTiles = rightDist <= 0 ? 0 : (rightDist + tileWidth - 1) / tileWidth;
		m_cxTiles = 1 + m_leftTiles + rightTiles;
	}

	private void computeHowManyYTiles( int dimHeight ) {
		int tileHeight = m_centerTile.getTileHeight();
		int wcY = m_divWorker.dcYtoWcY( m_centerTile.getOffsetY());
		int topDist = m_tileViewWork.wcYtoVcY(wcY);
		int bottomDist = dimHeight - (topDist + tileHeight);
		
		m_topTiles = topDist <= 0 ? 0 : (topDist + tileHeight - 1) / tileHeight;
		int bottomTiles = bottomDist <= 0 ? 0 : (bottomDist + tileHeight - 1) / tileHeight;
		m_cyTiles = 1 + m_topTiles + bottomTiles;
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
		int tileWidth  = m_centerTile.getTileWidth();
		int tileHeight = m_centerTile.getTileHeight();
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
		//tc.setDrawTileWidth(m_centerTile.getDrawTileWidth());
		//tc.setDrawTileHeight(m_centerTile.getDrawTileHeight());
		tc.setInViewPort( true );//computeInViewPort(tc));
		tc.setDegWidth(m_centerTile.getDegWidth());
		tc.setDegHeight(m_centerTile.getDegHeight());
		tc.setLevel(m_centerTile.getLevel());
		
		return tc;
	}
	
	private void positionCenterOffsetForDiv() {
	    // This routine positions the center tile relative to the view it sits in.
		int wcX = m_centerTile.getOffsetX();
		int wcY = m_centerTile.getOffsetY();
		DivCoords dc = worldToDiv( wcX, wcY );
		m_centerTile.setOffsetY(dc.getY());
		m_centerTile.setOffsetX(dc.getX());
	}
	
	public DivCoords worldToDiv(int wcX, int wcY) {
		return m_divWorker.wcToDC(wcX, wcY);
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
		return m_divProj.getNumXtiles(m_tileDegWidth);
	}
	
	public int getNumYTiles() {
		return m_divProj.getNumYtiles(m_tileDegHeight);
	}

	private void setTile(TileCoords[] r, int xIdx, int yIdx, TileCoords tc) {
		r[yIdx * m_cxTiles + xIdx] = tc;
	}
	
	private TileCoords[] makeTilesResult(int level) {
		int count = m_cyTiles * m_cxTiles;
		TileCoords[] r = new TileCoords[count];
		return r;
	}
    
    
	/**
	 * Determines how many tiles are needed and positions them
	 * so that the view port is filled.
	 * 
	 * @param LayerSet
	 * @param int level
	 * @return array of tile coordinates (never null)
	 */
	public TileCoords[] arrangeTiles( int level, LayerSet ls, double lsScale ) {
		int tLevel = level - ls.getStartLevel();

		double degFactor = ( tLevel < 1 ? 1 : Math.pow(2, tLevel) );
		m_tileDegWidth = ls.getStartLevelTileWidthInDeg()/degFactor;
		m_tileDegHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
		GeodeticCoords gc = m_tileViewWork.getGeoCenter();
		if ( m_divProj.getEquatorialScale() == 0.0 ){
			m_divProj.setEquatorialScale(lsScale);
		}
		m_centerTile = m_lsWorker.findTile( ls, level, m_tileDegWidth, m_tileDegHeight, gc );
		positionCenterOffsetForDiv();
		//TODO
		// we should probably use the view dimensions here to keep
		// the number of tile down to a reasonable value.
		computeHowManyXTiles(m_scaledViewDims.getWidth());
		computeHowManyYTiles(m_scaledViewDims.getHeight());
		
		TileCoords[] r = makeTilesResult(level);
		buildTilesRelativeToCenter( r, ls.isZeroTop() );

		return r;
	}
	
	private void placeTilesForOneLayer( int level, TiledImageLayer layer ) {
		TileCoords[] tileCoords = arrangeTiles( level, layer);
		if (layer.getLayerSet().isAlwaysDraw() || layer.isPriority()) {
			layer.setTileCoords(tileCoords);
			layer.setLevel(level);
			
			layer.updateView();
		}
	}

	public void layoutTiles( ViewDimension vd, double eqScale ) {
		double projScale = m_divProj.getEquatorialScale();
		double dFactor = eqScale/projScale;
		m_scaledViewDims = ViewDimension.builder()
			.setWidth((int)(vd.getWidth()/dFactor))
			.setHeight((int)(vd.getHeight()/dFactor)).build();
		
		m_tileViewWork.setDimension(m_scaledViewDims);
		
		int dpi = m_divProj.getScrnDpi();
		for (TiledImageLayer layer : m_tiledImageLayers) {
			LayerSet ls = layer.getLayerSet();
			if (!ls.isActive()) {
				layer.updateView(); // Force hiding inactive layers, but skip placing their tiles.
				continue;
			}
			if (ls.isAlwaysDraw() || layer.isPriority()) {
				int level = layer.findLevel(dpi, projScale);
				placeTilesForOneLayer(level, layer );
			}
		}
	}
	
	private boolean isLayerCandidateForScale(TiledImageLayer layer, int level) {
		LayerSet layerSet = layer.getLayerSet();
		if (layerSet.isAlwaysDraw()) {
			return false;
		}
		if (!layerSet.useToScale()) {
			return false;
		}
		if (!layerSet.levelIsInRange(level)) {
			return false;
		}

		return true;
	}
	
	/**
	 * Marks the tiled image layer that is the best for this projection as
	 * priority.
	 *
	 * @param zoomFlag
	 */
	public void setLayerBestSuitedForScale() {
		int dpi = m_divProj.getScrnDpi();
		double projScale = m_divProj.getEquatorialScale();

		TiledImageLayer bestLayerSoFar = null;
		int LevelWithBestScaleSoFar = -10000;
		double bestScaleSoFar = 0.0;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (!layer.getLayerSet().isActive()) {
				continue;
			}
			layer.setPriority(false);

			int level = layer.findLevel(dpi, projScale);
			if (!isLayerCandidateForScale(layer, level)) {
				layer.setPriority(false);
				continue;
			}
			double layerScale = layer.findScale(dpi, level);
			if (bestLayerSoFar == null) {
				bestLayerSoFar = layer;
				bestScaleSoFar = layerScale;
				LevelWithBestScaleSoFar = level;
				continue;
			}
			if (bestScaleSoFar == layerScale) {
				if (level >= 0 && level < LevelWithBestScaleSoFar) {
					bestLayerSoFar = layer;
					bestScaleSoFar = layerScale;
					LevelWithBestScaleSoFar = level;
				}
				continue;
			}
			double oldDistance = Math.abs(projScale - bestScaleSoFar);
			double newDistance = Math.abs(projScale - layerScale);
			if (newDistance < oldDistance) {
				bestLayerSoFar = layer;
				bestScaleSoFar = layerScale;
				LevelWithBestScaleSoFar = level;
			}
		}
		if (bestLayerSoFar != null) {
			bestLayerSoFar.setPriority(true);
		}
	}
}
