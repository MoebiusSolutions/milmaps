/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Panel;
import com.moesol.gwt.maps.client.DivWorker.BoxBounds;
import com.moesol.gwt.maps.client.DivWorker.ImageBounds;
import com.moesol.gwt.maps.client.stats.Sample;


/**
 * Images are placed into an absolute panel. 
 **/
public class TiledImageLayer {
	private final TileImageLoadListener m_tileImageLoadListener = new TileImageLoadListener();
	private final MyTileImageEngineListener m_tileImageEngineListener = new MyTileImageEngineListener();
	/** The one and only layer set this tiled image layer will render */
	private final LayerSet m_layerSet;
	
	private final Panel m_dimLayoutPanel;
	private final Panel m_nonDimLayoutPanel;
	private final TileImageCache m_tileImageMgr = new TileImageCache(m_tileImageEngineListener);
	
	private final double EarthCirMeters  = 2.0*Math.PI*IProjection.EARTH_RADIUS_METERS;
	private final double MeterPerDeg  = EarthCirMeters/360.0;
	
	private TileCoords[] m_tileCoords;
	private int m_level;
	private DivWorker m_divWorker;
	private final ImageBounds m_imgBounds = DivWorker.newImageBounds();
	private final DivPanel m_divPanel;
	/** Marked as priority when this image layer is the best for the scale */
	private boolean m_priority = false;
	
	
	private class MyTileImageEngineListener implements TileImageEngineListener {
		@Override
		public Object createImage(ViewBox vb, TileCoords tileCoords) {
			ImageDiv image = new ImageDiv();
			image.addHandlers(m_tileImageLoadListener);
			String url = tileCoords.makeTileURL(vb, getLayerSet(), getLevel(), getDynamicCounter());
			image.setUrl(url);
			image.setStyleName(m_layerSet.getStyleName());

			layoutPanel().add(image);
			return image;
		}
		@Override
		public void destroyImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			image.removeHandlers();
			image.removeImage();
			image.removeFromParent();
		}
		
		@Override
		public void useImage(ViewBox vb, TileCoords tileCoords, Object object) {
			ImageDiv image = (ImageDiv)object;
			// Better performance in IE7 to skip for non-auto update, but always setting fixes some IE7 issues.
			image.setUrl(tileCoords.makeTileURL(vb, getLayerSet(), getLevel(), getDynamicCounter()));
			image.setVisible(true);
		}

		@Override
		public void hideImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			image.setVisible(false);
		}
	};
	
	
	public int getImgBoundLeft(){ return m_imgBounds.left; }
	public int getImgBoundRight(){ return m_imgBounds.right; }
	
	public int getImgBoundTop(){ return m_imgBounds.top; }
	public int getImgBoundBottom(){ return m_imgBounds.bottom; }

	public TiledImageLayer( DivPanel divPanel, Panel dimPanel, Panel nonDimPanel, 
						    DivWorker divWorker, LayerSet layerSet ) {
		m_divPanel = divPanel;
		m_layerSet = layerSet;
		m_divWorker = divWorker;
		m_dimLayoutPanel = dimPanel;//divPanel.getTileLayerPanel();
		m_nonDimLayoutPanel = nonDimPanel;//divPanel.getNonDimTileLayerPanel();
		m_tileImageLoadListener.setTileImageEngine(m_tileImageMgr);
	}
	
	public void clearTileImages(){
		m_tileImageMgr.clear();
	}
	
	public long getDynamicCounter() {
		return m_divPanel.getDynamicCounter();
	}
	
	private Panel layoutPanel(){
		if (m_layerSet.isDimmable()) {
			return m_dimLayoutPanel;
		}
		return m_nonDimLayoutPanel;
	}

	public void setTileCoords(TileCoords[] tileCoords) {
		m_tileCoords = tileCoords; //translateTileCoords(tileCoords);
	}
	
	public TileCoords[] getTileCoords() {
	  return m_tileCoords;
	}
	
	public void removeAllTiles() {
		m_tileImageMgr.removeAllImages();
	}
	
	/**
	 * Draw scaled images. Only called from animation.
	 */
	

	public void updateView(ViewBox vb) {
		Sample.LAYER_UPDATE_VIEW.beginSample();
		try {
			_updateView(vb);
		} finally {
			Sample.LAYER_UPDATE_VIEW.endSample();
		}
	}
	
	private void _updateView(ViewBox vb) {
		if ( (m_layerSet.isAlwaysDraw() == false && isPriority() == false ) ){
			m_tileImageMgr.hideUnplacedImages();
			return;
		}
		if (!m_layerSet.isActive()) {
			m_tileImageMgr.hideUnplacedImages();
			return; // do nothing.
		}
		
		Sample.LAYER_POSITION_IMAGES.beginSample();
		positionImages(vb);
		Sample.LAYER_POSITION_IMAGES.endSample();
		
		m_tileImageMgr.hideUnplacedImages();
	}

	private void clipImgBoundsToDiv(){
		DivDimensions dim = m_divWorker.getDivBaseDimensions();
		m_imgBounds.left = Math.max(0, m_imgBounds.left);
		m_imgBounds.right = Math.min(dim.getWidth(), m_imgBounds.right);
		m_imgBounds.top = Math.max(0, m_imgBounds.top);
		m_imgBounds.bottom = Math.min(dim.getHeight(), m_imgBounds.bottom);
	}
	
	private void positionImages(ViewBox vb) {
		m_imgBounds.left   = Integer.MAX_VALUE;
		m_imgBounds.right  = Integer.MIN_VALUE;
		m_imgBounds.top    = Integer.MAX_VALUE;
		m_imgBounds.bottom = Integer.MIN_VALUE;
		DivDimensions divBaseDim = m_divWorker.getDivBaseDimensions();
		for (int i = 0; i < m_tileCoords.length; i++) {
			positionOneImage(vb, m_tileCoords[i]);
			m_divWorker.computeImageBounds(m_tileCoords[i],divBaseDim, m_imgBounds);
		}
		// note, we are clipping the images by the div's boundaries
		clipImgBoundsToDiv();
	}


	private void positionOneImage(ViewBox vb, TileCoords tileCoords) {
		if (tileCoords == null) {
			return;
		}
		ImageDiv image = (ImageDiv)m_tileImageMgr.findOrCreateImage(vb,tileCoords);
		setImageZIndex(image, m_layerSet.getZIndex());
		int x = tileCoords.getOffsetX();
		int y = tileCoords.getOffsetY();
		int width = tileCoords.getTileWidth();
		int height = tileCoords.getTileHeight();
		BoxBounds b = m_divWorker.computePercentBounds(x, y, width, height);
		
		Style imageStyle = image.getElement().getStyle();
		imageStyle.setLeft(b.left, Unit.PCT);
		imageStyle.setRight(100 - b.right, Unit.PCT);
		imageStyle.setTop(b.top, Unit.PCT);
		imageStyle.setBottom(100 - b.bottom, Unit.PCT);
	}

	private void setImageZIndex(ImageDiv image, int zIndex) {
		image.getElement().getStyle().setZIndex(zIndex);
	}

	public void setLevel(int level) {
		m_level = level;
	}
	
	public int getLevel() {
		return m_level;
	}
	
	public boolean isZeroTop(){
		return m_layerSet.isZeroTop();
	}
	
	public boolean areAllLoaded() {
		ImageDiv image = (ImageDiv)m_tileImageMgr.firstUnloadedImage();
		if (image!= null) {
			System.out.println("Waiting for " + image.getUrl());
		}
		return image == null;
	}

	@Override
	public String toString() {
		return m_layerSet.getData();
	}

	public LayerSet getLayerSet() {
		return m_layerSet;
	}

	public void destroy() {
		m_tileImageMgr.clear();
	}

	public boolean isPriority() {
		return m_priority;
	}

	public void setPriority(boolean b) {
		m_priority = b;
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
	public int findLevel(double dpi, double projScale) {
		if (m_layerSet.isTiled() == false){
			return -1;
		}
		double mpp = 2.54 / (dpi * 100); // meters per pixel for physical screen
		double m_dx = m_layerSet.getPixelWidth();
		double l_mpp = (m_layerSet.getStartLevelTileWidthInDeg()*MeterPerDeg)/m_dx;
		// compute the best level.
		if ( projScale == 0.0 ){
			projScale = (mpp / l_mpp);
		}
		double logMess = Math.log(projScale) + Math.log(l_mpp)
				- Math.log(mpp);
		double dN = logMess / Math.log(2);
		return (int)(Math.rint(dN)) + m_layerSet.getStartLevel();
	}

	public double findScale( double dpi, int level ) {
		double mpp = 2.54 / (dpi * 100);
		double m_dx = m_layerSet.getPixelWidth();
		double l_mpp = (m_layerSet.getStartLevelTileWidthInDeg()*MeterPerDeg)/m_dx;
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}
	
	public int boundLevel(int level){
		int minLevel = m_layerSet.getMinLevel();
		int maxLevel = m_layerSet.getMaxLevel();
		return Math.max(Math.min(level, maxLevel),minLevel);
	}

}
