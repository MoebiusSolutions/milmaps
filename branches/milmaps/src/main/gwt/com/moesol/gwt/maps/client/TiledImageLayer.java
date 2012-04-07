package com.moesol.gwt.maps.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.moesol.gwt.maps.client.stats.Sample;

/**
 * Images are placed into an absolute panel. 
 **/
public class TiledImageLayer {
	private final TileImageLoadListener m_tileImageLoadListener = new TileImageLoadListener();
	private final MyTileImageEngineListener m_tileImageEngineListener = new MyTileImageEngineListener();
	/** The one and only layer set this tiled image layer will render */
	private final LayerSet m_layerSet;
	
	private final LayoutPanel m_dimLayoutPanel;
	private final LayoutPanel m_nonDimLayoutPanel;
	private final TileImageManager m_tileImageMgr = new TileImageManager(m_tileImageEngineListener);
	
	private final double EarthCirMeters  = 2.0*Math.PI*IProjection.EARTH_RADIUS_MEERS;
	private final double MeterPerDeg  = EarthCirMeters/360.0;
	
	private TileCoords[] m_tileCoords;
	private int m_level;
	private DivWorker m_divWorker;
	private final DivPanel m_divPanel;
	/** Marked as priority when this image layer is the best for the scale */
	private boolean m_priority = false;
	int m_minLeft  = Integer.MAX_VALUE;
	int m_maxRight = Integer.MIN_VALUE;
	
	
	private class MyTileImageEngineListener implements TileImageEngineListener {
		@Override
		public Object createImage(ViewBox vb, TileCoords tileCoords) {
			ImageDiv image = new ImageDiv();
			image.addLoadListener(m_tileImageLoadListener);
			String url = tileCoords.makeTileURL(vb, getLayerSet(), getLevel(), getDynamicCounter());
			image.setUrl(url);
			image.setStyleName("moesol-MapTile");

			layoutPanel().add(image);
			return image;
		}
		@Override
		public void destroyImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			image.removeLoadListener(m_tileImageLoadListener);
			image.removeImage();
			image.removeFromParent();
		}
		
		@Override
		public void useImage(ViewBox vb, TileCoords tileCoords, Object object) {
			ImageDiv image = (ImageDiv)object;
			// Better performance in IE7 to skip for non-auto update, but always setting fixes some IE7 issues.
			image.setUrl(tileCoords.makeTileURL(vb, getLayerSet(), getLevel(), getDynamicCounter()));
			layoutPanel().setWidgetVisible(image, true);
		}

		@Override
		public void hideImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			//image.setPixelSize(512, 512);
			//m_absolutePanel.setWidgetPosition(image, -513, -513);
			layoutPanel().setWidgetVisible(image, false);
		}
	};
	
	
	public int getMinLeft(){ return m_minLeft; }
	public int getMaxRight(){ return m_maxRight; }

	public TiledImageLayer( DivPanel divPanel, LayerSet layerSet ) {
		m_divPanel = divPanel;
		m_layerSet = layerSet;
		m_divWorker = divPanel.getDivWorker();
		m_dimLayoutPanel = divPanel.getTileLayerPanel();
		m_nonDimLayoutPanel = divPanel.getNonDimTileLayerPanel();
		m_tileImageLoadListener.setTileImageEngine(m_tileImageMgr);
	}
	
	public void clearTileImages(){
		m_tileImageMgr.clear();
	}
	
	public long getDynamicCounter() {
		return m_divPanel.getDynamicCounter();
	}
	
	private LayoutPanel layoutPanel(){
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
	
	public void hideAllTiles() {
		m_tileImageMgr.hideAllImages();
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

	private void positionImages(ViewBox vb) {
		m_minLeft = Integer.MAX_VALUE;
		m_maxRight = Integer.MIN_VALUE;
		for (int i = 0; i < m_tileCoords.length; i++) {
			positionOneImage(vb, m_tileCoords[i]);
		}
	}

	private void positionOneImage(ViewBox vb, TileCoords tileCoords) {
		if (tileCoords == null) {
			return;
		}
		//String bboxStr = vw.getBoundingBoxStr(m_divWorker.getProjection());
		ImageDiv image = (ImageDiv)m_tileImageMgr.findOrCreateImage(vb,tileCoords);
		setImageZIndex(image, m_layerSet.getZIndex());
		int x = tileCoords.getOffsetX();
		int y = tileCoords.getOffsetY();
		int width = tileCoords.getTileWidth();
		int height = tileCoords.getTileHeight();
		DivWorker.BoxBounds b = m_divWorker.computePerccentBounds(x, y, width, height);
		layoutPanel().setWidgetLeftRight(image, b.left, Unit.PCT, 100-b.right, Unit.PCT);
		layoutPanel().setWidgetTopBottom(image, b.top, Unit.PCT, 100-b.bot, Unit.PCT);
		DivDimensions dd = m_divWorker.getDivBaseDimensions();
		m_minLeft  = Math.max(0,Math.min(x, m_minLeft));
		m_maxRight = Math.min(dd.getWidth(),Math.max(x+width, m_maxRight));
	}

	private void setImageZIndex(ImageDiv image, int zIndex) {
		// Note if you try and use "zindex" it WON'T work.
		//image.getElement().getStyle().setProperty("zIndex", Integer.toString(zIndex) );
		image.getElement().getStyle().setZIndex(zIndex);
		image.getElement().getParentElement().getStyle().setZIndex(zIndex);
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

}
