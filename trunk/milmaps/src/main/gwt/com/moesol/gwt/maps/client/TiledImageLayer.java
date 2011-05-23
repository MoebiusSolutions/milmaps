package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

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
	private final AbsolutePanel m_absolutePanel;
	private final TileImageEngine m_tileImageEngine = new TileImageEngine(this,m_tileImageEngineListener);
	private final int REAL_ZOFFSET = 2000;
	private final int ANIMATED_ZOFFSET = 1000;
	private final int NOT_IN_USE_ZOFFSET = 0;
	
	private TileCoords[] m_tileCoords;
	private int m_level;
	private final MapView m_mapView;
	/** Marked as priority when this image layer is the best for the scale */
	private boolean m_priority = false;
	private boolean m_allTilesLoaded = false;
	
	private class MyTileImageEngineListener implements TileImageEngineListener {
		@Override
		public Object createImage(TileCoords tileCoords) {
			Image image = new Image();
			image.addLoadListener(m_tileImageLoadListener);
			image.setUrl(tileCoords.makeTileURL(getLayerSet(), getLevel(), getDynamicCounter()));
			image.setStyleName("moesol-MapTile");
			m_absolutePanel.add(image);
			return image;
		}

		@Override
		public void destroyImage(Object object) {
			Image image = (Image)object;
			image.removeLoadListener(m_tileImageLoadListener);
			image.removeFromParent();
		}
		
		@Override
		public void useImage(TileCoords tileCoords, Object object) {
			Image image = (Image)object;
			// needed only for dynamic layers, but should not impact normal layers
			image.setUrl(tileCoords.makeTileURL(getLayerSet(), getLevel(), getDynamicCounter()));
		}

		@Override
		public void hideImage(Object object) {
			Image image = (Image)object;
			image.setPixelSize(512, 512);
			m_absolutePanel.setWidgetPosition(image, -513, -513);
		}
	};

	public TiledImageLayer(MapView mapView, LayerSet layerSet, AbsolutePanel absolutePanel) {
		m_mapView = mapView;
		m_layerSet = layerSet;
		m_absolutePanel = absolutePanel;
		m_tileImageLoadListener.setTileImageEngine(m_tileImageEngine);
	}
	
	public long getDynamicCounter() {
		return m_mapView.getDynamicCounter();
	}

	public void setTileCoords(TileCoords[] tileCoords) {
		m_tileCoords = translateTileCoords(tileCoords);
	}
	
	public TileCoords[] getTileCoords() {
	  return m_tileCoords;
	}
	
	
	/**
	 * Found tile server where 0 is at the top instead of the bottom.
	 * Translate the tile coords here...
	 * 
	 * @param tileCoords
	 * @return
	 */
	private TileCoords[] translateTileCoords(TileCoords[] tileCoords) {
		if (!m_layerSet.isZeroTop()) {
			return tileCoords;
		}
		
		TileCoords[] newTileCoords = new TileCoords[tileCoords.length];
		for (int i = 0; i < newTileCoords.length; i++) {
			TileCoords otc = tileCoords[i];
			if (otc == null) {
				continue;
			}
			TileCoords ntc = new TileCoords();
			ntc.setInViewPort(otc.isInViewPort());
			ntc.setLevel(otc.getLevel());
			ntc.setOffsetX(otc.getOffsetX());
			ntc.setOffsetY(otc.getOffsetY());
			ntc.setTileHeight(otc.getTileHeight());
			ntc.setTileWidth(otc.getTileWidth());
			ntc.setDrawTileHeight(otc.getDrawTileHeight());
			ntc.setDrawTileWidth(otc.getDrawTileWidth());
			ntc.setDegWidth(otc.getDegWidth());
			ntc.setDegHeight(otc.getDegHeight());
			ntc.setX(otc.getX());
			ntc.setY((1 << otc.getLevel()) - otc.getY() - 1);
			newTileCoords[i] = ntc;
		}
		return newTileCoords;
	}
	
	public void hideAnimatedTiles(){
		if ( m_mapView.getMapBrightness() < 1.0 ){
			if ( areAllLoaded() && !m_mapView.isMapActionSuspended() ){
				m_tileImageEngine.doHideAnimatedImages();
			}	
		}
	}
	
	/**
	 * Draw scaled images. Only called from animation.
	 */
	public void drawTileImages( double scale, double offsetX, double offsetY  ) {
		m_tileImageEngine.setAllZIndex(NOT_IN_USE_ZOFFSET + m_layerSet.getZIndex());
		if ( m_tileCoords != null ){
		    for (int i = 0; i < m_tileCoords.length; i++) {
				 drawImage( m_tileCoords[i], scale, offsetX, offsetY );
		    }
		}
	}
	
	private void drawImage( TileCoords tileCoords, double scale, double offsetX, double offsetY ){
		if (tileCoords != null) {
			// passing false in forces a redraw.
			Image image = (Image)m_tileImageEngine.findImage(tileCoords, scale, false, true);
			if ( image != null ) {
				if ( m_layerSet.isAlwaysDraw() == false ){
					image.getElement().getStyle().setOpacity(m_mapView.getMapBrightness());
				}
				setImageZIndex(image, ANIMATED_ZOFFSET + m_layerSet.getZIndex());
				int width = (int)(tileCoords.getDrawTileWidth()*scale + 0.5);
				int height = (int)(tileCoords.getDrawTileHeight()*scale + 0.5);
				image.setPixelSize(width, height);
				int left = (int)(tileCoords.getOffsetX()*scale - offsetX);
				int top  = (int)(tileCoords.getOffsetY()*scale - offsetY);
				m_absolutePanel.setWidgetPosition( image, left , top );
			}
		}
	}

	public void updateView() {
		if ( (m_layerSet.isAlwaysDraw() == false && isPriority() == false ) ){
			m_tileImageEngine.hideUnplacedImages();
			return; 
		}
		if (!m_layerSet.isActive()) {
			m_tileImageEngine.hideUnplacedImages();
			return; // do nothing.
		}
		positionImages();
	}

	private void positionImages() {
		for (int i = 0; i < m_tileCoords.length; i++) {
			positionOneImage(m_tileCoords[i]);
		}
		m_tileImageEngine.hideUnplacedImages();
	}

	private void positionOneImage(TileCoords tileCoords) {
		if (tileCoords == null) {
			return;
		}
		Image image = (Image)m_tileImageEngine.findOrCreateImage(tileCoords);
		if ( m_layerSet.isAlwaysDraw() == false ){
			image.getElement().getStyle().setOpacity(m_mapView.getMapBrightness());
		}
		setImageZIndex(image, REAL_ZOFFSET + m_layerSet.getZIndex());
		image.setPixelSize(tileCoords.getDrawTileWidth(), tileCoords.getDrawTileHeight());
		m_absolutePanel.setWidgetPosition(image, tileCoords.getOffsetX(), tileCoords.getOffsetY());
	}

	private void setImageZIndex(Image image, int zIndex) {
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
		Image image = (Image)m_tileImageEngine.firstUnloadedImage();
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
		m_tileImageEngine.clear();
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
		double l_mpp = m_layerSet.getStartLevelTileWidthInDeg()* (111120.0 / m_dx);
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
		double l_mpp = m_layerSet.getStartLevelTileWidthInDeg()* (111120.0 / m_dx);
		// we want to return ( (mpp*2^n)/(l_mpp) );
		return ((mpp * Math.pow(2, level)) / l_mpp);
	}
	
}
