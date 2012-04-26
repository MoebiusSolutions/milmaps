package com.moesol.gwt.maps.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.http.client.URL;

// This is a tile
public class TileCoords {
	
	private int m_level;
	private int m_tileX;
	private int m_tileY;
	private int m_offsetX;
	private int m_offsetY;
	private int m_tileWidth = 512;
	private int m_tileHeight = 512;
	private int m_drawTileWidth;
	private int m_drawTileHeight;
	private boolean m_tiled = true;
	private boolean m_inViewPort = false;
	private boolean m_bZeroTop = false;
	private double m_degWidth = 180.0;
	private double m_degHeight = 180.0;
	
	private static URLProvider s_urlProvider = new URLProvider() {
		@Override
		public String encodeComponent(String decodedURLComponent) {
			return URL.encodeQueryString(decodedURLComponent);
		}
	};

	public TileCoords() {
		super();
	}

	public TileCoords(int x, int y) {
		m_tileX = x;
		m_tileY = y;
	}
	
	public static void setGlobalURLProvider(URLProvider urlProvider) {
		s_urlProvider = urlProvider;
	}
	
	public TileCoords(int ox, int oy, int x, int y) {
		m_tileX = x;
		m_tileY = y;
		m_offsetX = ox;
		m_offsetY = oy;
	}
	
	public boolean isZeroTop() {
		return m_bZeroTop;
	}

	public void setZeroTop( boolean bZeroTop) {
		m_bZeroTop = bZeroTop;
	}
	
	public int getOffsetX() {
		return m_offsetX;
	}

	public void setOffsetX(int offsetX) {
		m_offsetX = offsetX;
	}

	public int getOffsetY() {
		return m_offsetY;
	}

	public void setOffsetY(int offsetY) {
		m_offsetY = offsetY;
	}
	
	@Override
	public String toString() {
		return "[l="+ m_level +",ox=" + getOffsetX() + ",oy=" + getOffsetY()
		+ ",w=" + getTileWidth() + ",h=" + getTileHeight()
		+ ",dw=" + getDrawTileWidth() + ",dh=" + getDrawTileHeight()
		+ ",x=" + getX() + ",y=" + getY() + "]";
	}

	public int getX() {
		return m_tileX;
	}

	public void setX(int tileX) {
		m_tileX = tileX;
	}

	public int getY() {
		return m_tileY;
	}

	public void setY(int tileY) {
		m_tileY = tileY;
	}

	public int getTileWidth() {
		return m_tileWidth;
	}

	public void setTileWidth(int width) {
		m_tileWidth = width;
	}

	public int getTileHeight() {
		return m_tileHeight;
	}

	public void setTileHeight(int height) {
		m_tileHeight = height;
	}
	
	
	public int getDrawTileWidth() {
		return m_drawTileWidth;
	}

	public void setDrawTileWidth(int width ) {
		m_drawTileWidth = width;
	}
	
	public int getDrawTileHeight() {
		return m_drawTileHeight;
	}

	public void setDrawTileHeight(int height ) {
		m_drawTileHeight = height;
	}
	
	public void setDegWidth( double deg ){m_degWidth = deg;}
	public double getDegWidth(){ return m_degWidth; }
	public void setDegHeight( double deg ){m_degHeight = deg;}
	public double getDegHeight(){ return m_degHeight; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_offsetX;
		result = prime * result + m_offsetY;
		result = prime * result + m_tileHeight;
		result = prime * result + m_tileWidth;
		result = prime * result + m_tileX;
		result = prime * result + m_tileY;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TileCoords))
			return false;
		
		final TileCoords other = (TileCoords) obj;
		if (m_level != other.m_level) {
			return false;
		}
		if (m_tileX != other.m_tileX) {
			return false;
		}
		if (m_tileY != other.m_tileY) {
			return false;
		}
		if (m_offsetX != other.m_offsetX) {
			return false;
		}
		if (m_offsetY != other.m_offsetY) {
			return false;
		}
		if (m_tileHeight != other.m_tileHeight) {
			return false;
		}
		if (m_tileWidth != other.m_tileWidth) {
			return false;
		}
		return true;
	}
	
	public boolean isTiled() {
		return m_tiled;
	}

	public void setTiled(boolean tiled) {
		m_tiled = tiled;
	}

	public boolean isInViewPort() {
		return m_inViewPort;
	}

	public void setInViewPort(boolean inViewPort) {
		m_inViewPort = inViewPort;
	}

	public int getLevel() {
		return m_level;
	}

	public void setLevel(int level) {
		m_level = Math.max(0,level);
	}

	public String makeTileURL(ViewBox vb,LayerSet layerSet, 
							  int level, long dynamicCounter) {
		if (level < 0) {
			level = 0;
		}
		return doMakeTileURL(vb, layerSet, 
							 computeLevelFromSkipLevel(layerSet, level), 
							 dynamicCounter);
	}
	

	String doMakeTileURL(ViewBox vb, LayerSet layerSet, 
						 int levelInUrl, long dynamicCounter ) {
		Map<String, Object> replacements = new HashMap<String, Object>(layerSet.getProperties());
		int width;
		int height;
		String bBoxStr;
		replacements.put("server", layerSet.getServer());
		replacements.put("data", encode(layerSet.getData()));
		replacements.put("level", Integer.toString(levelInUrl));
		replacements.put("srs", layerSet.getSrs());
		replacements.put("imgSize", Integer.toString(layerSet.getPixelWidth()));
		if (layerSet.isTiled()) {
			width   = layerSet.getPixelWidth();
			height  = layerSet.getPixelHeight();
			bBoxStr = computeBbox(layerSet);
		}
		else {
			if (vb == null) {
				throw new IllegalArgumentException("ViewBox shouldn't be null");
			}	
			width   = vb.getWidth();
			height  = vb.getHeight();
			bBoxStr = vb.getWmsString();			
		}
		replacements.put("width", Integer.toString(width));
		replacements.put("height", Integer.toString(height));
		replacements.put("x", Integer.toString(getX()));
		replacements.put("y", Integer.toString(getY()));
		replacements.put("bbox", bBoxStr);
		replacements.put("quadkey", computeQuadKey(layerSet, levelInUrl));
		replacements.put("quad", computeQuad(layerSet, levelInUrl));
		
		String returnStr = layerSet.getUrlPattern();
		for (Entry<String, Object> e : replacements.entrySet()) {
			returnStr = returnStr.replaceAll("\\{" + e.getKey() + "\\}", e.getValue().toString());
		}
		return maybeAppendDynamicCounter(layerSet, returnStr, dynamicCounter);
	}

	private String computeBbox(LayerSet layerSet ) {
		// WMS-C says that for EPSG:4326 we have a top level bounding box of -180 -90, 180 90
		// We know our x, y, degWidth, degHeight so we can compute the bbox.
		double left =   getX() * getDegWidth() - 180.0;
		double bottom = getY() * getDegHeight() - 90.0;
		double right = (getX() + 1) * getDegWidth() - 180.0;
		double top =   (getY() + 1) * getDegHeight() - 90.0;
		return left + "," + bottom + "," + right + "," + top;
	}
	
	private String computeQuadKey(LayerSet layerSet, int levelInUrl) {
		return QuadKey.tileXYToKey(getX(), getY(), levelInUrl);
	}
	private String computeQuad(LayerSet layerSet, int levelInUrl) {
		String key = QuadKey.tileXYToKey(getX(), getY(), levelInUrl);
		String q = key.replace('0', 'q');
		String r = q.replace('1', 'r');
		String s = r.replace('2', 't');
		String t = s.replace('3', 's');
		
		return t;
	}

	/**
	 * Appends the query parameter '_' with a value of the current dynamic
	 * counter to force the browser to reload the tiles for dynamic layers.
	 * 
	 * @param layerSet
	 * @param returnStr
	 * @param dynamicCounter
	 * @return returnStr or returnStr with counter appended.
	 */
	private String maybeAppendDynamicCounter(LayerSet layerSet, String returnStr, long dynamicCounter) {
		if (!layerSet.isAutoRefreshOnTimer()) {
			return returnStr;
		}
		if (returnStr.contains("?")) {
			return returnStr + "&_=" + dynamicCounter;
		}
		return returnStr + "?_=" + dynamicCounter;
	}

	private static String encode(String v) {
		if (v == null) {
			return "";
		}
		return s_urlProvider.encodeComponent(v);
	}

	private int computeLevelFromSkipLevel(LayerSet layerSet, int level) {
		return Math.max(0, level - layerSet.getSkipLevels());
	}
}
