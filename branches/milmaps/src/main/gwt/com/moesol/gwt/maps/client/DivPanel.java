package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public class DivPanel extends AbsolutePanel {
	private final LayoutPanel m_tileLayersPanel = new LayoutPanel();
	private final DivWorker m_divWorker = new DivWorker();
	private final TileBuilder m_tileBuilder = new TileBuilder();
	private final DivDimensions m_dims = new DivDimensions();
	private MapView m_map = null;
	private boolean m_firstSearch = true;
	private boolean m_firstCenter = true;
	private final ArrayList<TiledImageLayer> m_tiledImageLayers = new ArrayList<TiledImageLayer>();
	private int m_level = 0;
	protected IProjection m_proj = null;
	
	public DivPanel(){
		m_tileBuilder.setDivWorker(m_divWorker);
		m_tileBuilder.setTileImageLayers(m_tiledImageLayers);
		m_tileLayersPanel.setStylePrimaryName("tileLayerPanel");
		m_tileLayersPanel.setWidth("100%");
		m_tileLayersPanel.setHeight("100%");
		m_tileLayersPanel.getElement().setClassName("tileLayerPanel"); 
		DivDimensions dd = m_divWorker.getDivBaseDimensions();
		super.setPixelSize(dd.getWidth(), dd.getHeight());
		this.add(m_tileLayersPanel);
		this.getElement().setClassName("DivPanelContainer");
	}
	
	public void initialize( int level, MapView map, IProjection.T type, double eqScale){
		m_level = level;
		m_map = map;
		m_proj = Projection.createProj(type);
		m_proj.setEquatorialScale(eqScale);
		m_divWorker.setProjection(m_proj);
		m_tileBuilder.setProjection(m_proj);
		m_tileBuilder.setDivLevel(level);
	}
	
	public IProjection getProjection(){ return m_proj; }
	
	public boolean updateViewCenter( GeodeticCoords gc ){
		boolean bRtn = m_tileBuilder.upadteViewCenter(gc);
		if ( m_firstCenter || bRtn ){
			bRtn = true;
			m_firstCenter = false;
			m_divWorker.setDiv(gc);
		}
		return bRtn;
	}
	
	public void setLevel( int level ){
		m_level = level;
	}
	
	public long getDynamicCounter() {
		return m_map.getDynamicCounter();
	}
	
	public double getMapBrightness() {
		return m_map.getMapBrightness();
	}
	
	public boolean isMapActionSuspended() {
		return m_map.isMapActionSuspended();
	}
	
	public void setDivBaseSize(int width, int height ){
		m_divWorker.setDivBasePixelSize(width, height);
	}
	
	public DivDimensions getDimensions(){
		return m_dims;
	}
	
	@Override
	public void setPixelSize( int width, int height){
		m_dims.setWidth(width);
		m_dims.setHeight(height);
		super.setPixelSize(width, height);
	}
	
	public void setDimensions(DivDimensions d){
		m_dims.copyFrom(d);
		super.setPixelSize(d.getWidth(), d.getHeight());
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
	public LayoutPanel getTileLayerPanel(){ return m_tileLayersPanel; }
	
	public void addLayer(LayerSet layerSet) {
		TiledImageLayer tiledImageLayer = new TiledImageLayer(this, layerSet);
		m_tiledImageLayers.add(tiledImageLayer);
	}
	
	public void removeLayer(LayerSet layerSet) {
		int i = 0;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.getLayerSet().equals(layerSet)) {
				m_tiledImageLayers.remove(i);
				break;
			}
			i++;
		}
	}
	
	public void removeAllTileImages(){
		m_firstCenter = true;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.clearTileImages();
		}	
	}
	
	public void clearLayers() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.destroy();
		}
		m_tiledImageLayers.clear();
	}

	public boolean hasAutoRefreshOnTimerLayers() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.getLayerSet().isAutoRefreshOnTimer()) {
				return true;
			}
		}
		return false;
	}
	
	public void hideAllTiles() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.hideAllTiles();
		}
	}
	
	public void hideAnimatedTiles() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.hideAnimatedTiles();
		}
	}
	
	public void doUpdate( double eqScale ){
		ViewPort vp = m_map.getViewport();
		ViewDimension vd = vp.getVpWorker().getDimension();
		//GeodeticCoords gc = vp.getVpWorker().getGeoCenter();
		//m_divWorker.setDiv(gc, true);
		if ( m_firstSearch ){
			m_tileBuilder.setLayerBestSuitedForScale();
			m_firstSearch = false;
		}
		m_tileBuilder.layoutTiles(vd,eqScale,true);
	}
	
	
	public void placeInViewPanel( AbsolutePanel panel, boolean show ){
		IProjection mp = m_map.getProjection();
		ViewWorker vw = m_map.getViewport().getVpWorker();
		PixelXY tl = m_divWorker.computeDivLayoutInView(mp, vw, m_dims);
		super.setPixelSize(m_dims.getWidth(), m_dims.getHeight());
		panel.setWidgetPosition(this, tl.m_x, tl.m_y);
	}
	
	public void moveOffsetsInViewPanel( AbsolutePanel panel, int deltaX, int deltaY ){
		IProjection mp = m_map.getProjection();
		ViewWorker vw = m_map.getViewport().getVpWorker();
		PixelXY tl = m_divWorker.computeDivLayoutInView(mp, vw, m_dims);
		tl.m_x += deltaX;
		tl.m_y -= deltaY;
		panel.setWidgetPosition(this, tl.m_x, tl.m_y);
	}
	
	public void resize( int w, int h ){
		DivDimensions dd = m_divWorker.getDivBaseDimensions();	
		int dW = dd.getWidth();
		int dH = dd.getHeight();
		if ( dW <= w ){
			int tW = dW;
			for( int i = 1; i < 3; i++ ){
				dW += tW;
				if ( dW > w )
					break;
			}
		}
		if ( dH<= h ){
			int tH = dH;
			for( int i = 1; i < 3; i++ ){
				dH += tH;
				if ( dH > h )
					break;
			}
		}
		dd.setWidth(dW);
		dd.setHeight(dH);
	}
}
