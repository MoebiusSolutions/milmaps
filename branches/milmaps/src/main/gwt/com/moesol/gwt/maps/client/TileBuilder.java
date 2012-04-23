package com.moesol.gwt.maps.client;

import java.util.ArrayList;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class TileBuilder {
	private int m_leftTiles;
	private int m_topTiles;
	private int m_cxTiles;
	private int m_cyTiles;
	
	private double m_tileDegWidth;
	private double m_tileDegHeight;
	
	private ViewDimension m_scaledViewDims = new ViewDimension();
	private LayerSetWorker m_lsWorker = new LayerSetWorker();
	private ViewWorker m_mapViewWorker = null;
	private DivWorker m_divWorker = null;
	private IMapView m_map = null;
	private int m_divLevel;
	
	private ArrayList<TiledImageLayer> m_tiledImageLayers;

	private ViewWorker m_tileViewWorker = new ViewWorker();
	private TileCoords m_centerTile;
	
	IProjection m_divProj = null;
	// TODO This will come out when done testing
	private WorldCoords m_vbOffsetWc = null;
	////////////////////////////////////////////
	public void setTileImageLayers( ArrayList<TiledImageLayer> tiledImageLayers ) {
		m_tiledImageLayers = tiledImageLayers;
	}
	
	public void setMapView(IMapView map){
		m_map = map;
		m_mapViewWorker = map.getViewport().getVpWorker();
	}
	
	public void setDivWorker( DivWorker dw ) {
		m_divWorker = dw; 
	}
	
	public void setProjection( IProjection proj ) { 
		m_divProj = proj;
		m_lsWorker.setProjection(proj);
		m_tileViewWorker.setProjection(proj);
	}
	
	public void setViewCenterWc( WorldCoords wc ) {
		m_tileViewWorker.setCenterInWc(wc);
	}
	
	public IProjection getProjection() { return m_divProj; }
	
	public void setDivLevel(int divLevel) {
		this.m_divLevel = divLevel;
	}
	
	public int getDivLevel() {
		return m_divLevel;
	}
	
	public boolean upadteViewCenter(GeodeticCoords gc){
		GeodeticCoords g = m_tileViewWorker.getGeoCenter();
		if ( g.equals(gc) == false ){
			m_tileViewWorker.setGeoCenter( gc );
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
	public TileCoords[] arrangeTiles( int level, ViewBox vb, TiledImageLayer layer ) {
		LayerSet ls = layer.getLayerSet();
		int dpi = m_divProj.getScrnDpi();
		TileCoords[] tiles = null;
		if (ls.isTiled()) {
			double lsScale = layer.findScale(dpi, level);
			tiles = arrangeTiles( level, ls, lsScale );
		}
		else {
			tiles = arrangeViewTile(vb, ls);
		}
		return tiles;
	}
		
	// Note, in all these routines Use the div projection that has a 
	// fixed scale and is designed to be used at the level natural scale.
	private void computeHowManyXTiles(int dimWidth) {
		int tileWidth = m_centerTile.getTileWidth();
		int wcX = m_divWorker.divToWorldX( m_centerTile.getOffsetX());
		int leftDist = m_tileViewWorker.wcXtoVcX(wcX);
		int rightDist = dimWidth - (leftDist + tileWidth);
		
		m_leftTiles = leftDist <= 0 ? 0 : (leftDist + tileWidth - 1) / tileWidth;
		int rightTiles = rightDist <= 0 ? 0 : (rightDist + tileWidth - 1) / tileWidth;
		m_cxTiles = 1 + m_leftTiles + rightTiles;
	}

	private void computeHowManyYTiles(int dimHeight) {
		int tileHeight = m_centerTile.getTileHeight();
		int wcY = m_divWorker.divToWorldY( m_centerTile.getOffsetY());
		int topDist = m_tileViewWorker.wcYtoVcY(wcY);
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
			int numYTiles = getNumYTiles();
			y = (numYTiles - y - 1);
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
	

	private WorldCoords getViewBoxWcOffset(ViewBox vb, boolean useProj){
		if( useProj == false){
			int boxWidth = vb.getWidth();
			int boxHeight = vb.getHeight();
			ViewDimension vd = m_mapViewWorker.getDimension();
			double vwWidth = vd.getWidth();
			double f = boxWidth/vwWidth;
			int x = (int)(f*(m_mapViewWorker.getOffsetInWcX()+vd.getWidth()/2));
			int y = (int)(f*(m_mapViewWorker.getOffsetInWcY()+vd.getHeight()/2));
			WorldDimension wd = m_map.getProjection().getWorldDimension();
			if (wd.getHeight()*f < boxHeight) {
				GeodeticCoords gc = new GeodeticCoords(vb.left(),vb.top(),AngleUnit.DEGREES);
				WorldCoords wc = m_divWorker.getProjection().geodeticToWorld(gc);
				return new WorldCoords(x- boxWidth/2, wc.getY());
			}
			return new WorldCoords(x- boxWidth/2, y-boxHeight/2);
		}
		GeodeticCoords gc = new GeodeticCoords(vb.left(),vb.top(),AngleUnit.DEGREES);
		WorldCoords wc = m_divWorker.getProjection().geodeticToWorld(gc);
		return wc;
	}


	private ViewCoords getDivOffsetInView(){
		IProjection proj = m_map.getProjection();
		DivDimensions t = m_divWorker.getDivBaseDimensions();
		DivDimensions dd = new DivDimensions(t.getWidth(),t.getHeight());
		ViewCoords vc = m_divWorker.computeDivLayoutInView(proj, m_mapViewWorker, dd);
		return vc;
	}
	
	public WorldCoords getVbOffsets(){ return m_vbOffsetWc; }
	public TileCoords getCenterTile(){ return m_centerTile; }
	
	private TileCoords makeCenterTileUsingMapView(ViewBox vb) {	
		WorldCoords wc;
		if (vb.isSingleTile()) {
			wc = getViewBoxWcOffset(vb,false);
		}
		else {
			wc = getViewBoxWcOffset(vb,true);
		}
		m_vbOffsetWc = wc;
		
		TileCoords tc = new TileCoords(0,0);
		tc.setOffsetX(wc.getX());
		tc.setOffsetY(wc.getY());
		tc.setTileWidth( vb.getWidth());
		tc.setTileHeight(vb.getHeight());
		tc.setInViewPort( true );
		tc.setDegWidth(vb.getLonSpan());
		tc.setDegHeight(vb.getLatSpan());
		tc.setLevel(m_divLevel);
		return tc;
	}
	
	private TileCoords makeViewTileFromCenter() {
		int tileWidth  = m_centerTile.getTileWidth();
		int tileHeight = m_centerTile.getTileHeight();
		
		TileCoords tc = new TileCoords(0,0);
		tc.setZeroTop(true);
		int offsetX = m_centerTile.getOffsetX();
		int offsetY = m_centerTile.getOffsetY();
		tc.setOffsetX(offsetX);
		tc.setOffsetY(offsetY);
		tc.setTileWidth(tileWidth);
		tc.setTileHeight(tileHeight);
		tc.setTiled(false);
		//tc.setDrawTileWidth(m_centerTile.getDrawTileWidth());
		//tc.setDrawTileHeight(m_centerTile.getDrawTileHeight());
		tc.setInViewPort( true );//computeInViewPort(tc));
		tc.setDegWidth(m_centerTile.getDegWidth());
		tc.setDegHeight(m_centerTile.getDegHeight());
		tc.setLevel(m_centerTile.getLevel());
		
		return tc;
	}
	
	private TileCoords makeViewTilePositionedFromCenter(int x) {
		int tileWidth  = m_centerTile.getTileWidth();
		int tileHeight = m_centerTile.getTileHeight();
		
		TileCoords tc = new TileCoords(x,0);
		tc.setZeroTop(true);
		int offsetX = (x-1) * tileWidth + m_centerTile.getOffsetX();
		int offsetY = m_centerTile.getOffsetY();
		tc.setOffsetX(offsetX);
		tc.setOffsetY(offsetY);
		tc.setTileWidth(tileWidth);
		tc.setTileHeight(tileHeight);
		tc.setTiled(false);
		//tc.setDrawTileWidth(m_centerTile.getDrawTileWidth());
		//tc.setDrawTileHeight(m_centerTile.getDrawTileHeight());
		tc.setInViewPort( true );//computeInViewPort(tc));
		tc.setDegWidth(m_centerTile.getDegWidth());
		tc.setDegHeight(m_centerTile.getDegHeight());
		tc.setLevel(m_centerTile.getLevel());
		
		return tc;
	}
	
	private void positionTileOffsetForDiv(TileCoords tc) {
	    // This routine positions the center tile relative to the view it sits in.
		int wcX = tc.getOffsetX();
		int wcY = tc.getOffsetY();
		DivCoords dc = worldToDiv(wcX, wcY);
		tc.setOffsetY(dc.getY());
		tc.setOffsetX(dc.getX());
	}
	
	private void positionNonTileOffsetForDiv(TileCoords tc) {
	    // This routine positions the center tile relative to the view it sits in.
		int wcX = tc.getOffsetX();
		int wcY = tc.getOffsetY();
		DivCoords dc = worldToDiv(wcX, wcY);
		ViewCoords vc = getDivOffsetInView();
		tc.setOffsetX(-1*vc.getX());
		//tc.setOffsetX(dc.getX());
		tc.setOffsetY(dc.getY());
	}
	
	public DivCoords worldToDiv(int wcX, int wcY) {
		return m_divWorker.worldToDiv(wcX, wcY);
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
	
	private TileCoords[] makeTilesResult() {
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
	public TileCoords[] arrangeTiles(int level, LayerSet ls, double lsScale) {
		int tLevel = level - ls.getStartLevel();

		double degFactor = ( tLevel < 1 ? 1 : Math.pow(2, tLevel) );
		m_tileDegWidth = ls.getStartLevelTileWidthInDeg()/degFactor;
		m_tileDegHeight = ls.getStartLevelTileHeightInDeg()/degFactor;
		GeodeticCoords gc = m_tileViewWorker.getGeoCenter();
		if ( m_divProj.getEquatorialScale() == 0.0 ){
			m_divProj.setEquatorialScale(lsScale);
		}
		m_centerTile = m_lsWorker.findTile( ls, level, m_tileDegWidth, m_tileDegHeight, gc );
		positionTileOffsetForDiv(m_centerTile);
		//TODO
		// we should probably use the view dimensions here to keep
		// the number of tile down to a reasonable value.
		computeHowManyXTiles(m_scaledViewDims.getWidth());
		computeHowManyYTiles(m_scaledViewDims.getHeight());
		
		TileCoords[] r = makeTilesResult();
		buildTilesRelativeToCenter( r, ls.isZeroTop() );

		return r;
	}
	
	public TileCoords[] arrangeViewTile(ViewBox vb, LayerSet ls) {
		boolean singleTile = vb.isSingleTile();
		TileCoords[] r = (singleTile? new TileCoords[1]:new TileCoords[3]);

		m_centerTile = makeCenterTileUsingMapView(vb);
		if (singleTile == true){
			positionNonTileOffsetForDiv(m_centerTile);
			r[0] = makeViewTileFromCenter();
		}
		else{
			// we will make three copies of tile
			positionTileOffsetForDiv(m_centerTile);
			r[0] = makeViewTilePositionedFromCenter(0);
			r[1] = makeViewTilePositionedFromCenter(1);
			r[2] = makeViewTilePositionedFromCenter(2);
		}
		return r;
	}
	
	private void placeTilesForOneLayer( ViewBox vb, ViewDimension vd, 
										int level, TiledImageLayer layer ) {
		if (layer.getLayerSet().isAlwaysDraw() || layer.isPriority()) {
			TileCoords[] tileCoords = arrangeTiles( level, vb, layer);
			layer.setTileCoords(tileCoords);
			layer.setLevel(level);
			layer.updateView(vb);
		}
	}

	public void layoutTiles(ViewDimension vd, double eqScale) {
		double projScale = m_divProj.getEquatorialScale();
		double dFactor = eqScale/projScale;
		m_scaledViewDims = ViewDimension.builder()
			.setWidth((int)(vd.getWidth()/dFactor))
			.setHeight((int)(vd.getHeight()/dFactor)).build();
		
		m_tileViewWorker.setDimension(m_scaledViewDims);
		double factor = eqScale/m_divProj.getEquatorialScale();
		ViewBox vb = m_mapViewWorker.getViewBox(factor);
		int dpi = m_divProj.getScrnDpi();
		for (TiledImageLayer layer : m_tiledImageLayers) {
			LayerSet ls = layer.getLayerSet();
			if (!ls.isActive()) {
				// Force hiding inactive layers, but skip placing their tiles.
				layer.updateView(vb); 
				continue;
			}
			if (ls.isAlwaysDraw() || layer.isPriority()) {
				int level = layer.findLevel(dpi, projScale);
				placeTilesForOneLayer(vb,vd, level, layer);
			}
		}
	}
	
	private boolean isLayerCandidateForScale(TiledImageLayer layer, int level) {
		LayerSet layerSet = layer.getLayerSet();
		if (layerSet.isAlwaysDraw()) {
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
