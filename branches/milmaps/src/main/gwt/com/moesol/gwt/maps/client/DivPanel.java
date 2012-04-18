package com.moesol.gwt.maps.client;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DivPanel extends AbsolutePanel {
	private final AbsolutePanel m_dimPanel = new AbsolutePanel();
	private final AbsolutePanel m_nonDimPanel = new AbsolutePanel();
	private final DivWorker m_divWorker = new DivWorker();
	private final TileBuilder m_tileBuilder = new TileBuilder();
	private final DivDimensions m_scaledDims = new DivDimensions();
	private IMapView m_map = null;
	private final int m_level;
	private boolean m_firstSearch = true;
	private boolean m_firstCenter = true;
	private final ArrayList<TiledImageLayer> m_tiledImageLayers = new ArrayList<TiledImageLayer>();
	protected IProjection m_divProj = null;
	private WidgetPositioner m_widgetPositioner; // null unless needed
	
	public DivPanel(int level) {
		m_level = level;
		
		m_tileBuilder.setDivWorker(m_divWorker);
		m_tileBuilder.setTileImageLayers(m_tiledImageLayers);
		
		m_dimPanel.setStylePrimaryName("mm-DimTileLayerPanel");
		m_dimPanel.setWidth("100%");
		m_dimPanel.setHeight("100%");
		m_dimPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		m_dimPanel.getElement().getStyle().setZIndex(1);

		m_nonDimPanel.setStylePrimaryName("mm-NonDimTileLayerPanel");
		m_nonDimPanel.setWidth("100%");
		m_nonDimPanel.setHeight("100%");
		m_nonDimPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		m_nonDimPanel.getElement().getStyle().setZIndex(2);
		
		DivDimensions dd = m_divWorker.getDivBaseDimensions();
		super.setPixelSize(dd.getWidth(), dd.getHeight());
		this.add(m_dimPanel);
		this.add(m_nonDimPanel);
		this.getElement().setClassName("mm-DivPanelContainer");
		this.getElement().getStyle().setZIndex(level);
	}
	
	public void close() {
		removeAllTiles();
		this.remove(m_dimPanel);
		this.remove(m_nonDimPanel);
	}

	public void initialize(int level, IMapView map, IProjection.T type, double eqScale) {
		m_map = map;
		m_divProj = Projection.createProj(type);
		m_divProj.setBaseEquatorialScale(eqScale);
		m_divProj.setEquatorialScale(eqScale);
		m_divWorker.setProjection(m_divProj);
		m_tileBuilder.setProjection(m_divProj);
		m_tileBuilder.setDivLevel(level);
		m_tileBuilder.setMapViewWorker(map.getViewport().getVpWorker());
	}
	
	public void initialize(IMapView map) {
		m_map = map;
		m_divProj = map.getProjection();
		m_divWorker.setProjection(m_divProj);
		m_tileBuilder.setProjection(m_divProj);
		m_tileBuilder.setDivLevel(0);
		m_tileBuilder.setMapViewWorker(map.getViewport().getVpWorker());
	}
	
	public int getDivLevel(){ return m_level; }
	
	public void setProjection(IProjection proj){ m_divProj = proj; }
	public IProjection getProjection(){ return m_divProj; }
	
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
		m_divWorker.setDivBaseDimensions(width, height);
	}
	
	public void makePanelSmall(AbsolutePanel panel, int width, int height){
		super.setPixelSize(width, height);
		panel.setWidgetPosition(this, 0, 0);
	}
	
	public DivDimensions getScaledDims(){
		return m_scaledDims;
	}
	
	public DivCoordSpan getUsedDivSpan() {
		int top = Integer.MIN_VALUE;
		int left = Integer.MIN_VALUE;
		int bottom = Integer.MAX_VALUE;
		int right = Integer.MAX_VALUE;
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (!layer.getLayerSet().isActive()){
				continue;
			}
			top    = Math.max(top, layer.getMinTop());
			left   = Math.max(left, layer.getMinLeft());
			bottom = Math.min(bottom, layer.getMaxBottom());
			right  = Math.min(right, layer.getMaxRight());			
		}
		return new DivCoordSpan(top, left, bottom, right);
	}
	
	@Override
	public void setPixelSize( int width, int height){
		m_scaledDims.setWidth(width);
		m_scaledDims.setHeight(height);
		super.setPixelSize(width, height);
	}
	
	public DivWorker getDivWorker(){ return m_divWorker; }
	
	public Panel getTileLayerPanel() {
		return m_dimPanel;
	}
	
	public Panel getNonDimTileLayerPanel() {
		return m_nonDimPanel;
	}
	
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
	
	public void setOpacity(boolean bothPanels, double opacity){
		m_dimPanel.getElement().getStyle().setOpacity(opacity);
		opacity = (bothPanels ? opacity : 1);
		m_nonDimPanel.getElement().getStyle().setOpacity(opacity);
	}

	public void removeAllTiles() {
		for (TiledImageLayer layer : m_tiledImageLayers) {
			if (layer.getLayerSet().isActive()) {
				layer.removeAllTiles();
			}
		}
	}
	
	public void doUpdate( double eqScale ){
		ViewPort vp = m_map.getViewport();
		ViewDimension vd = vp.getVpWorker().getDimension();
		if ( m_firstSearch ) {
			m_tileBuilder.setLayerBestSuitedForScale();
			m_firstSearch = false;
		}
		m_tileBuilder.layoutTiles(vd, eqScale);
	}
	
	
	public void placeInViewPanel( AbsolutePanel panel ) {
		IProjection mp = m_map.getProjection();
		ViewWorker vw = m_map.getViewport().getVpWorker();
		ViewCoords tl = m_divWorker.computeDivLayoutInView(mp, vw, m_scaledDims);
		super.setPixelSize(m_scaledDims.getWidth(), m_scaledDims.getHeight());
		panel.setWidgetPosition(this, tl.getX(), tl.getY());
	}
	
	// TODO unit test
	public void resize(int w, int h) {
		DivDimensions dd = m_divWorker.getDivBaseDimensions();
		int dW = dd.getWidth();
		int dH = dd.getHeight();
		if ( dW < w ){
			int f = (w/dW) + 1;
			dW *= f; 
		}
		if ( dH < h ){
			int f = (h/dH) + 1;
			dH *= f;
		}
		m_divWorker.setDivBaseDimensions( dW, dH);
		m_divWorker.updateDivWithCurrentGeoCenter();
	}
	
	public void positionIcons() {
		m_map.getIconEngine().positionIcons(getWidgetPositioner(), m_divWorker);
	}

	public WidgetPositioner getWidgetPositioner() {
		if (m_widgetPositioner == null) {
			m_widgetPositioner = new WidgetPositioner() {
				@Override
				public void place(Widget widget, int divX, int divY, int z) {
					placeIcon(widget, divX, divY, z);
				}
				@Override
				public void remove(Widget widget) {
					remove(widget);
				}
			};
		}
		return m_widgetPositioner;
	}
	
	private void placeIcon(Widget widget, int dx, int dy, int z) {
		Panel lp = getNonDimTileLayerPanel();
		if (widget.getParent() == null || widget.getParent() != lp) {
			lp.add(widget);
		}
		
		DivWorker.BoxBounds b = m_divWorker.computePerccentBounds(dx, dy, 1, 1);
		
		widget.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widget.getElement().getStyle().setLeft(b.left, Unit.PCT);
		widget.getElement().getStyle().setTop(b.top, Unit.PCT);
		widget.getElement().getStyle().setZIndex(z);
	}

}
