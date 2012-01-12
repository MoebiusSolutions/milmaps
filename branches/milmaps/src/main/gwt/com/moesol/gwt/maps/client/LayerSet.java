package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.moesol.gwt.maps.client.units.AngleUnit;

/**
 * Properties configuring a layer set for the map view.
 * @see MapView.addLayer
 */
public class LayerSet implements IsSerializable {
	private String m_server;
	private String m_data = "";
	private int m_skipLevels = 0;
	private String m_urlPattern = "{server}?T={data}&L={level}&X={x}&Y={y}";
	private boolean m_active = true; // If set to false engine will not render.
	private boolean m_zeroTop = false;
	private boolean m_alwaysDraw = false;
	private boolean m_useToScale = true;
	private boolean m_autoRefreshOnTimer = false;
	private int m_epsg = 4326;
	private int m_minLevel = -20;
	private int m_maxLevel = 20;
	private int m_zIndex = 4;
	private int m_startLevel = 0;

	private int m_origPixWidth = 512; // cell width in pixels
	private int m_origPixHeight = 512; // cell height in pixels
	private int m_drawPixX= 512;
	private int m_drawPixY = 512;
	//private double m_degWidth = 180.0; // default, but note that world wind uses 36.0
	//private double m_degHeight = 180.0;
	private double m_startLevelTileWidthInDeg = 180.0;
	private double m_startLevelTileHeightInDeg = 180.0;
	
	public double getStartLevelTileWidthInDeg() {
		return m_startLevelTileWidthInDeg;
	}

	public void setStartLevelTileWidthInDeg(double deg) {
		m_startLevelTileWidthInDeg = deg;
	}
	
	public double getStartLevelTileHeightInDeg() {
		return m_startLevelTileHeightInDeg;
	}
	
	public void setStartLevelTileHeightInDeg(double deg) {
		m_startLevelTileHeightInDeg = deg;
	}
	
	public void setStartLevelTileDimensionsInDeg( 
			double degWidth, double degHeight ){
		m_startLevelTileWidthInDeg = degWidth;
		m_startLevelTileHeightInDeg = degHeight;
	}
	
	
	public int getEpsg() {
		return m_epsg;
	}

	public void setEpsg(int epsg) {
		m_epsg = epsg;
	}
	
	public LayerSet withEpsg(int epsg) {
		setEpsg(epsg);
		return this;
	}

	
	public int getStartLevel() {
		return m_startLevel;
	}

	public void setStartLevel(int level) {
		m_startLevel = level;
	}
	
	public void setUseToScale( boolean flag ){
		m_useToScale = flag;
	}
	
	public boolean useToScale(){
		return m_useToScale;
	}
	
	public void setZIndex(int index) {
		m_zIndex = index;
	}
	public LayerSet withZIndex(int index) {
		setZIndex(index);
		return this;
	}
	public int getZIndex(){
		return m_zIndex;
	}
	
	public boolean levelIsInRange( int level ){
		return ( m_minLevel <= level && level <= m_maxLevel );
	}
	
	public double getLevelDeg( int level ){
		return m_startLevelTileWidthInDeg/(Math.pow(2, (level-m_startLevel)));
	}
	
	public void setAlwaysDraw(boolean flag) {
		m_alwaysDraw = flag;
	}
	
	public LayerSet withAlwaysDraw(boolean flag) {
		setAlwaysDraw(flag);
		return this;
	}
	
	public boolean isAlwaysDraw() {
		return m_alwaysDraw;
	}

	public String getData() {
		return m_data;
	}

	public void setData(String data) {
		m_data = data;
	}
	
	public LayerSet withData(String data) {
		m_data = data;
		return this;
	}

	public String getServer() {
		return m_server;
	}

	public void setServer(String server) {
		m_server = server;
	}

	public LayerSet withServer(String server) {
		m_server = server;
		return this;
	}

	public int getSkipLevels() {
		return m_skipLevels;
	}

	public void setSkipLevels(int skipLevels) {
		m_skipLevels = skipLevels;
	}

	public LayerSet withSkipLevels(int skipLevels) {
		m_skipLevels = skipLevels;
		return this;
	}

	public String getUrlPattern() {
		return m_urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		m_urlPattern = urlPattern;
	}

	public LayerSet withUrlPattern(String urlPattern) {
		m_urlPattern = urlPattern;
		return this;
	}

	/**
	 * @return true if this layer set is active. In active layer sets are not
	 *         drawn.
	 */
	public boolean isActive() {
		return m_active;
	}
	public void setActive(boolean active) {
		m_active = active;
	}
	public LayerSet withActive(boolean active) {
		setActive(active); return this;
	}

	/**
	 * @return true if this layer set uses 0 for 90N. False means this layer set
	 *         uses 0 for 90S.
	 */
	public boolean isZeroTop() {
		return m_zeroTop;
	}
	public void setZeroTop(boolean v) {
		m_zeroTop = v;
	}
	public LayerSet withZeroTop(boolean v) {
		setZeroTop(v);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_data == null) ? 0 : m_data.hashCode());
		result = prime * result
				+ ((m_server == null) ? 0 : m_server.hashCode());
		result = prime * result + m_skipLevels;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LayerSet)) {
			return false;
		}
		final LayerSet other = (LayerSet) obj;
		if (m_data == null) {
			if (other.m_data != null)
				return false;
		} else if (!m_data.equals(other.m_data))
			return false;
		if (m_server == null) {
			if (other.m_server != null)
				return false;
		} else if (!m_server.equals(other.m_server))
			return false;
		if (m_skipLevels != other.m_skipLevels)
			return false;
		return true;
	}

	/**
	 * @return pixel width of tiles in this layer set.
	 */
	public int getPixelWidth() {
		return m_origPixWidth;
	}
	public void setPixelWidth(int dx) {
		m_origPixWidth = dx;
	}
	public LayerSet withPixelWidth(int dx) {
		m_origPixWidth = dx;
		return this;
	}

	/**
	 * @return pixel height of tiles in this layer set.
	 */
	public int getPixelHeight() {
		return m_origPixHeight;
	}
	public void setPixelHeight(int dy) {
		m_origPixHeight = dy;
	}
	public LayerSet withPixelHeight(int dy) {
		m_origPixHeight = dy;
		return this;
	}
	
	/**
	 * @return pixel width of tiles in this layer set.
	 */
	public int getDrawPixelWidth() {
		return m_drawPixX;
	}
	public void setDrawPixelWidth(int dx) {
		m_drawPixX = dx;
	}
	public LayerSet withDrawPixelWidth(int dx) {
		m_drawPixX = dx;
		return this;
	}

	/**
	 * @return pixel height of tiles in this layer set.
	 */
	public int getDrawPixelHeight() {
		return m_drawPixY;
	}
	public void setDrawPixelHeight(int dy) {
		m_drawPixY = dy;
	}
	public LayerSet withDrawPixelHeight(int dy) {
		m_drawPixY = dy;
		return this;
	}

	/**
	 * @return true if this layer set should be refreshed periodically. For
	 *         example a layer that contains GPS data needs to be refreshed from
	 *         the server periodically to show the current GPS positions.
	 */
	public boolean isAutoRefreshOnTimer() {
		return m_autoRefreshOnTimer;
	}
	public void setAutoRefreshOnTimer(boolean refresh) {
		m_autoRefreshOnTimer = refresh;
	}
	public LayerSet withAutoRefreshOnTimer(boolean refresh) {
		setAutoRefreshOnTimer(refresh);
		return this;
	}
	

	@Override
	public String toString() {
		return "[s=" + getServer() + ",t=" + getData() + ",l="
				+ getSkipLevels() + "]";
	}

	/**
	 * After setting all the properties for this layer set call
	 * build to check validity.
	 * 
	 * @return this
	 */
	public LayerSet build() {
		if (m_data == null) {
			throw new IllegalStateException("data is null");
		}
		if (m_server == null) {
			throw new IllegalStateException("server is null");
		}
		if (m_urlPattern == null) {
			throw new IllegalStateException("urlPattern is null");
		}
		return this;
	}

	public LayerSet withUseToScale(boolean useToScale) {
		setUseToScale(useToScale);
		return this;
	}

	public LayerSet withStartLevel(int startLevel) {
		setStartLevel(startLevel);
		return this;
	}

	public LayerSet withStartLevelTileWidthInDeg(double startLevelTileWidthInDeg) {
		setStartLevelTileWidthInDeg(startLevelTileWidthInDeg);
		return this;
	}

	public LayerSet withStartLevelTileHeightInDeg(double startLevelTileHeightInDeg) {
		setStartLevelTileHeightInDeg(startLevelTileHeightInDeg);
		return this;
	}

	public void setLevelRange( int min, int max ){
		m_minLevel = min;
		m_maxLevel = max;
	}
	
	public int getMinLevel() {
		return m_minLevel;
	}
	public void setMinLevel(int l) {
		m_minLevel = l;
	}
	public LayerSet withMinLevel(int l) {
		setMinLevel(l); return this;
	}
	
	public int getMaxLevel() {
		return m_maxLevel;
	}
	public void setMaxLevel(int l) {
		m_maxLevel = l;
	}
	public LayerSet withMaxLevel(int l) {
		setMaxLevel(l); return this;
	}
}
