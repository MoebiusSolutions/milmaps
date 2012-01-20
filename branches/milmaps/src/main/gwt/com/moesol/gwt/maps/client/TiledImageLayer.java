package com.moesol.gwt.maps.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Images are placed into an absolute panel. The z-index (zIndex) style is used
 * to ensure the images are layered correctly. We divide the z-index values
 * into three groups.
 * <ol>
 * <li>Real tiles for the current zoom level</li>
 * <li>Animated tiles being zoomed</li>
 * <li>Tiles not in use</li>
 * </ol>
 * Each group is separated by 1000 z-index values. That way within a group the z-index
 * defines which LayerSet has priority. Therefore we have:
 * <table>
 * <tr><td>Real</td><td>2000 - 2999</td></tr>
 * <tr><td>Animated</td><td>1000 - 1999</td></tr>
 * <tr><td>Not in Use</td><td>0 - 999</td></tr>
 * </table> 
 **/
public class TiledImageLayer {
	private final TileImageLoadListener m_tileImageLoadListener = new TileImageLoadListener();
	private final MyTileImageEngineListener m_tileImageEngineListener = new MyTileImageEngineListener();
	/** The one and only layer set this tiled image layer will render */
	private final LayerSet m_layerSet;
	
	private final LayoutPanel m_layoutPanel;
	private final TileImageManager m_tileImageMgr = new TileImageManager(this,m_tileImageEngineListener);
	private final int REAL_ZOFFSET = 2000;
	private final int ANIMATED_ZOFFSET = 1000;
	private final int NOT_IN_USE_ZOFFSET = 0;
	
	private final double EarthCirMeters  = 2.0*Math.PI*6378137;
	private final double MeterPerDeg  = EarthCirMeters/360.0;
	
	private TileCoords[] m_tileCoords;
	private int m_level;
	private IProjection m_proj;
	private LayerSetWorker m_lsWorker;
	private DivWorker m_divWorker;
	private final DivPanel m_divPanel;
	private final WorldCoords m_wc = new WorldCoords();
	/** Marked as priority when this image layer is the best for the scale */
	private boolean m_priority = false;
	private boolean m_allTilesLoaded = false;
	
	
	private class MyTileImageEngineListener implements TileImageEngineListener {
		@Override
		public Object createImage(TileCoords tileCoords) {
			ImageDiv image = new ImageDiv();
			image.addLoadListener(m_tileImageLoadListener);
			String url = tileCoords.makeTileURL(getLayerSet(), getLevel(), getDynamicCounter());
			image.setUrl(url);
			//image.setStyleName("moesol-MapTile");
			//m_absolutePanel.add(image);
			m_layoutPanel.add(image);
			return image;
		}
		/*
		public Object createImage(TileCoords tileCoords) {
			Image image = new Image();
			image.addLoadListener(m_tileImageLoadListener);
			String url = tileCoords.makeTileURL(getLayerSet(), getLevel(), getDynamicCounter());
			image.setUrl(url);
			image.setStyleName("moesol-MapTile");
			//m_absolutePanel.add(image);
			m_layoutPanel.add(image);
			return image;
		}
		*/
		@Override
		public void destroyImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			image.removeLoadListener(m_tileImageLoadListener);
			image.removeFromParent();
		}
		
		@Override
		public void useImage(TileCoords tileCoords, Object object) {
			ImageDiv image = (ImageDiv)object;
			// needed only for dynamic layers, but should not impact normal layers
			image.setUrl(tileCoords.makeTileURL(getLayerSet(), getLevel(), getDynamicCounter()));
			m_layoutPanel.setWidgetVisible(image, true);
		}

		@Override
		public void hideImage(Object object) {
			ImageDiv image = (ImageDiv)object;
			//image.setPixelSize(512, 512);
			//m_absolutePanel.setWidgetPosition(image, -513, -513);
			m_layoutPanel.setWidgetVisible(image, false);
		}
	};

	public TiledImageLayer( DivPanel divPanel, LayerSet layerSet ) {
		m_divPanel = divPanel;
		m_layerSet = layerSet;
		m_proj = divPanel.getProjection();
		m_lsWorker = new LayerSetWorker(m_proj);
		m_divWorker = divPanel.getDivWorker();
		m_layoutPanel = divPanel.getTileLayerPanel();
		m_tileImageLoadListener.setTileImageEngine(m_tileImageMgr);
	}
	
	public void clearTileImages(){
		m_tileImageMgr.clear();
	}
	
	public long getDynamicCounter() {
		return m_divPanel.getDynamicCounter();
	}

	public void setTileCoords(TileCoords[] tileCoords) {
		m_tileCoords = tileCoords; //translateTileCoords(tileCoords);
	}
	
	public TileCoords[] getTileCoords() {
	  return m_tileCoords;
	}
	
	
	public void hideAnimatedTiles(){
		if ( m_divPanel.getMapBrightness() < 1.0 ){
			if ( areAllLoaded() && !m_divPanel.isMapActionSuspended() ){
				m_tileImageMgr.doHideAnimatedImages();
			}	
		}
	}
	
	public void hideAllTiles() {
		m_tileImageMgr.hideAllImages();
	}
	
	/**
	 * Draw scaled images. Only called from animation.
	 */
	

	public void updateView() {
		if ( (m_layerSet.isAlwaysDraw() == false && isPriority() == false ) ){
			m_tileImageMgr.hideUnplacedImages();
			return; 
		}
		if (!m_layerSet.isActive()) {
			m_tileImageMgr.hideUnplacedImages();
			return; // do nothing.
		}
		positionImages();
	}

	private void positionImages() {
		for (int i = 0; i < m_tileCoords.length; i++) {
			positionOneImage(m_tileCoords[i]);
		}
		m_tileImageMgr.hideUnplacedImages();
	}

	private void positionOneImage(TileCoords tileCoords) {
		if (tileCoords == null) {
			return;
		}
		ImageDiv image = (ImageDiv)m_tileImageMgr.findOrCreateImage(tileCoords);
		setImageZIndex(image, REAL_ZOFFSET + m_layerSet.getZIndex());
		int x = tileCoords.getOffsetX();
		int y = tileCoords.getOffsetY();
		int width = tileCoords.getTileWidth();
		int height = tileCoords.getTileHeight();
		DivWorker.BoxBounds b = m_divWorker.computePerccentBounds(x, y, width, height);
		m_layoutPanel.setWidgetLeftRight(image, b.left, Unit.PCT, 100-b.right, Unit.PCT);
		m_layoutPanel.setWidgetTopBottom(image, b.top, Unit.PCT, 100-b.bot, Unit.PCT);
	}

	private void setImageZIndex(ImageDiv image, int zIndex) {
		// Note if you try and use "zindex" it WON'T work.
		image.getElement().getStyle().setProperty("zIndex", Integer.toString(zIndex) );
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
		if (image != null) {
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
		double l_mpp = m_layerSet.getStartLevelTileWidthInDeg()* (MeterPerDeg / m_dx);
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
		double l_mpp = m_layerSet.getStartLevelTileWidthInDeg()* (MeterPerDeg / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}

}
