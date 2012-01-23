package com.moesol.gwt.maps.client;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DivPanel extends AbsolutePanel {
	private final LayoutPanel m_tileLayersPanel = new LayoutPanel();
	private final DivWorker m_divWorker = new DivWorker();
	private final TileBuilder m_tileBuilder = new TileBuilder();
	private final DivDimensions m_dims = new DivDimensions();
	private MapView m_map = null;
	private boolean m_firstSearch = true;
	private boolean m_firstCenter = true;
	private final ArrayList<TiledImageLayer> m_tiledImageLayers = new ArrayList<TiledImageLayer>();
	protected IProjection m_proj = null;
	private WidgetPositioner m_widgetPositioner; // null unless needed
	
	public DivPanel() {
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
	
	public void initialize(int level, MapView map, IProjection.T type, double eqScale) {
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
	
	public DivCoordSpan getUsedDivSpan() {
		int minLeft = Integer.MAX_VALUE;
		int maxRight = Integer.MIN_VALUE;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			minLeft = Math.min(minLeft, layer.getMinLeft());
			maxRight = Math.max(maxRight, layer.getMaxRight());
		}
		return new DivCoordSpan(minLeft, maxRight);
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
	
	public void clearLayers() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			layer.destroy();
		}
		m_tiledImageLayers.clear();
	}

	public boolean hasAutoRefreshOnTimerLayers() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			LayerSet layerSet = layer.getLayerSet();
			if (!layerSet.isActive()) {
				continue;
			}
			if (layerSet.isAutoRefreshOnTimer()) {
				return true;
			}
		}
		return false;
	}
	
	public void hideAllTiles() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (!layer.getLayerSet().isActive()) {
				continue;
			}
			layer.hideAllTiles();
		}
	}
	
	public void doUpdate( double eqScale ){
		ViewPort vp = m_map.getViewport();
		ViewDimension vd = vp.getVpWorker().getDimension();
		if ( m_firstSearch ) {
			m_tileBuilder.setLayerBestSuitedForScale();
			m_firstSearch = false;
		}
		m_tileBuilder.layoutTiles( vd, eqScale );
	}
	
	public void placeInViewPanel( AbsolutePanel panel ) {
		IProjection mp = m_map.getProjection();
		ViewWorker vw = m_map.getViewport().getVpWorker();
		ViewCoords tl = m_divWorker.computeDivLayoutInView(mp, vw, m_dims);
		super.setPixelSize(m_dims.getWidth(), m_dims.getHeight());
		panel.setWidgetPosition(this, tl.getX(), tl.getY());
	}
	
	// TODO unit test
	public void resize(int w, int h) {
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
	
	public void positionIcons() {
		m_map.m_iconEngine.positionIcons(getWidgetPositioner(), m_divWorker);
	}

	public WidgetPositioner getWidgetPositioner() {
		if (m_widgetPositioner == null) {
			m_widgetPositioner = new WidgetPositioner() {
				@Override
				public void place(Widget widget, int divX, int divY, int width, int height, int z) {
					placeIcon(widget, divX, divY, width, height, z);
				}
				@Override
				public void remove(Widget widget) {
					remove(widget);
				}
			};
		}
		return m_widgetPositioner;
	}
	
	private void placeIcon(Widget widget, int dx, int dy, int dw, int dh, int z) {
		if (widget.getParent() == null || widget.getParent() != getTileLayerPanel()) {
			getTileLayerPanel().add(widget);
		}
		
		DivWorker.BoxBounds b = m_divWorker.computePerccentBounds(dx, dy, 1, 1);
		
		widget.setPixelSize(dw, dh);
		getTileLayerPanel().setWidgetLeftWidth(widget, b.left, Unit.PCT, dw, Unit.PX);
		getTileLayerPanel().setWidgetTopHeight(widget, b.top, Unit.PCT, dh, Unit.PX);
		getTileLayerPanel().getWidgetContainerElement(widget).getStyle().setZIndex(z);
	}

}
