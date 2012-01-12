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
	private final WorldCoords m_wc = new WorldCoords();
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
	
	public boolean upadteViewCenter( GeodeticCoords gc ){
		boolean bRtn = m_tileBuilder.upadteViewCenter(gc);
		if ( m_firstCenter || bRtn ){
			bRtn = true;
			m_firstCenter = false;
			m_divWorker.setDiv(gc, true);
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
	
	public void zoomByFactor( double factor ){
		//m_divWorker.zoomByFactor(factor);
		//DivDimensions dd = m_divWorker.getDivDimension();
		//super.setPixelSize(dd.getWidth(), dd.getHeight());
		return;
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
	
	public boolean allTilesLoaded() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.areAllLoaded() == false) {
				return false;
			}
		}
		return true;
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
		m_tileBuilder.computeTileCoords(vd,eqScale,true);
	}
	
	public void placeInViewPanel( AbsolutePanel panel, boolean show ){
		ViewWorker vw = m_map.getViewport().getVpWorker();
		int viewOx = vw.getOffsetInWcX();
		int viewOy = vw.getOffsetInWcY();
		int left;
		int top;
		if ( show ){
			IProjection mapProj = m_map.getProjection();
			double scale = mapProj.getEquatorialScale();
			double factor = scale/m_proj.getEquatorialScale();
			DivDimensions d = m_divWorker.getDivBaseDimensions();
			int width = (int)(d.getWidth()*factor + 0.5);
			int height = (int)(d.getHeight()*factor + 0.5);
			setPixelSize(width, height);
			m_wc.copyFrom(mapProj.geodeticToWorld(m_divWorker.getGeoCenter()));
			left = (m_wc.getX()- width/2) - viewOx;
			top  = viewOy - (m_wc.getY()+ height/2);
		}
		else{
			ViewDimension vd = vw.getDimension();
			left = viewOx + vd.getWidth();
			top  = viewOy + vd.getHeight();
		}
		panel.setWidgetPosition(this, left, top);
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
