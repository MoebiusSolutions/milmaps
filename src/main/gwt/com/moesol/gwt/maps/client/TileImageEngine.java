package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class TileImageEngine {
	private static class TileInfo {
		long m_lastUsedMillis;
		int m_x;
		int m_y;
		int m_level;
		Object m_image;
		/* true if tile was placed/drawn for level */
		boolean m_placed;
		/* true if tile was zoomed/drawn during animation */
		boolean m_animated;
		/* scale of last animation */
		double m_scale;
		/* true if loaded */
		boolean m_loaded;
		
		@Override
		public String toString() {
			return "l=" + m_level + ",x="+m_x +",y="+m_y + ",placed="+ m_placed + ",loaded="+m_loaded;
		}
	}
	
	public final static int MAX_CACHE_SIZE = 64;
	private final ArrayList<TileInfo> m_infoCache = new ArrayList<TileInfo>();
	private final TileImageEngineListener m_listener;
	private final TiledImageLayer m_imgLayer;
	public TileImageEngine( TiledImageLayer imgLayer, TileImageEngineListener l) {
		m_listener = l;
		m_imgLayer = imgLayer;
	}
	
	private void clearFlags() {
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			tileInfo.m_placed = false;
			tileInfo.m_animated = false;
			tileInfo.m_scale = Double.NaN;
		}
	}
	
	public Object findImage(TileCoords tileCoords, double scale, boolean placed, boolean animated) {
		int iSize = m_infoCache.size();
		for (int i = 0; i < iSize; i++) {
			TileInfo tileInfo = (TileInfo) m_infoCache.get(i);
			if (isMatch(tileInfo, tileCoords)) {
				if (tileInfo.m_placed) {
					continue;
				}
				if (tileInfo.m_scale == scale) {
					continue;
				}
				m_listener.useImage(tileCoords, tileInfo.m_image);
				tileInfo.m_placed = placed;
				tileInfo.m_animated = animated;
				tileInfo.m_scale = scale;
				tileInfo.m_lastUsedMillis = System.currentTimeMillis();
				return tileInfo.m_image;
			}
		}
		return null;
	}
	
	public Object findOrCreateImage(TileCoords tileCoords) {
		int iSize = m_infoCache.size();
		for (int i = 0; i < iSize; i++) {
		  TileInfo tileInfo = m_infoCache.get(i);
		  if (isMatch(tileInfo, tileCoords)) {
			if (tileInfo.m_placed) {
				continue;
			}
			m_listener.useImage(tileCoords, tileInfo.m_image);
			tileInfo.m_placed = true;
			tileInfo.m_lastUsedMillis = System.currentTimeMillis();
			return tileInfo.m_image;
		  }
		}
		
		TileInfo result = new TileInfo();
		result.m_image = m_listener.createImage(tileCoords);
		result.m_lastUsedMillis = System.currentTimeMillis();
		result.m_level = levelOrZero(tileCoords);
		result.m_loaded = false;
		result.m_placed = true;
		result.m_x = tileCoords.getX();
		result.m_y = tileCoords.getY();
		m_infoCache.add(result);
		return result.m_image;
	}
	
	public void clear() {
		for (TileInfo info : m_infoCache) {
			m_listener.destroyImage(info.m_image);
		}
	}
	
	private int levelOrZero(TileCoords tileCoords) {
		int level = tileCoords.getLevel();
		return level < 0 ? 0 : level;
	}
	
	public void hideUnplacedImages() {
		doHideUnplacedImages();
		removeStaleEntries();
		clearFlags();
	}

	private void doHideUnplacedImages() {
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			if (!tileInfo.m_placed && !tileInfo.m_animated) {
				m_listener.hideImage(tileInfo.m_image);
			}
		}
	}
	
	public void doHideAnimatedImages() {
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			if ( tileInfo.m_animated ) {
				tileInfo.m_animated = false;
				m_listener.hideImage(tileInfo.m_image);
			}
		}
	}

	private void removeStaleEntries() {
		if (m_infoCache.size() <= MAX_CACHE_SIZE) {
			return;
		}

		Collections.sort(m_infoCache, new Comparator<TileInfo>() {
			@Override
			public int compare(TileInfo arg0, TileInfo arg1) {
				TileInfo i0 = arg0;
				TileInfo i1 = arg1;
				long r = i0.m_lastUsedMillis - i1.m_lastUsedMillis;
				return r == 0 ? 0 : (r < 0 ? -1 : 1);
			}});
		
		int removed = 0;
		int needToRemove = m_infoCache.size() - MAX_CACHE_SIZE;
		for (int i = 0; i < m_infoCache.size(); ) {
			if (removed >= needToRemove) {
				return;
			}
			
			TileInfo tileInfo = m_infoCache.get(i);
			if (!tileInfo.m_placed) {
				m_listener.destroyImage(tileInfo.m_image);
				m_infoCache.remove(i);
				removed++;
			} else {
				i++;
			}
		}
	}
	
	private boolean isMatch(TileInfo tileInfo, TileCoords tileCoords) {
		if (tileInfo.m_level != levelOrZero(tileCoords)) {
			return false;
		}
		if (tileInfo.m_x != tileCoords.getX()) {
			return false;
		}
		if (tileInfo.m_y != tileCoords.getY()) {
			return false;
		}
		return true;
	}
	
	
	public void markLoaded(Widget sender) {
		boolean bUnmatched = true;
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			if (tileInfo.m_image == sender) {
				tileInfo.m_loaded = true;
				bUnmatched = false;
				break;
			}
		}
		if ( bUnmatched ){
			System.out.println("unmatched?: " + sender);
		}
		for (int i = 0; i < m_infoCache.size(); i++){
			TileInfo tileInfo = m_infoCache.get(i);
			if ( tileInfo.m_loaded == false ){
				return;
			}
		}
		m_imgLayer.hideAnimatedTiles();
	}
	
	public Object firstUnloadedImage() {
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			if (tileInfo.m_loaded == false) {
				return tileInfo.m_image;
			}
		}
		return null;
	}

	public void setAllZIndex(int zindex) {
		for (int i = 0; i < m_infoCache.size(); i++) {
			TileInfo tileInfo = m_infoCache.get(i);
			Image img = (Image) tileInfo.m_image;
			img.getElement().getStyle().setProperty("zIndex", zindex + "");
		}
	}
}
