package com.moesol.gwt.xmilmaps.client;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.ExportPackage;
import org.timepedia.exporter.client.Exportable;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapPanel;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.controls.FlyToController;
import com.moesol.gwt.maps.client.controls.MapDimmerControl;
import com.moesol.gwt.maps.client.controls.MapPanZoomControl;

@ExportPackage("milmaps")

public class XMap implements Exportable {
	private static Dictionary OPTIONS = Dictionary.getDictionary("milMap_options");
	private static final int MAP_EDGE_HOVER_PAN_INTERVAL = 100;
	private static final int MAP_EDGE_HOVER_RADIUS_PIXELS = 50;
	private static final int MAP_HOVER_DELAY_MILLIS = 300;
	private static final int TILE_DY = 3;
	private static final int TILE_DX = 4;
	
	private int m_width = 600;
	private int m_height = 420;
	
	final DockLayoutPanel m_dockPanel = new DockLayoutPanel(Unit.PX);

	private int m_scrnDpi = 75;
	private LayoutPanel m_lp;
	MapView m_mapView;
	
	/** Hook out to layerSet json data */
	private final native JsArray<LayerSetJson> getLayerSets() /*-{
    	return $wnd.layerSets;
	}-*/;
	
	@Export("zoomByFactor")
	public void zoomByFactor(double factor ){
		m_mapView.zoomByFactor(factor);
	}
	
	@Export("attachToDiv")
	public void setDiv( String divId, int width, int height ){
		m_width = width;
		m_height = height;
		doMap(divId);
	}

	@Export("setPanelSize")
	public void setPanelSize( int width, int height ){
		m_width = width;
		m_height = height;
		String strWidth = Integer.toString(m_width)+"px";
		String strHeight = Integer.toString(m_height)+"px";
		m_dockPanel.setWidth(strWidth);
		m_dockPanel.setHeight(strHeight);
		m_mapView.setPixelSize( m_width, m_height );
	}
	
	@Export("addPanZoomControl")
	public void addMapPanZoomControl( XMapPanZoomControl control ){
		control.setMapView(m_mapView);
		control.getElement().getStyle().setZIndex(100000);
		m_lp.add(control);
		m_lp.setWidgetLeftWidth(control, 0, Style.Unit.PX, 56, Style.Unit.PX );
		m_lp.setWidgetTopHeight(control, 0, Style.Unit.PX, 121, Style.Unit.PX );
	}
	
	@Export("addPositionControl")
	public void addMapPositionControl( XPositionControl control ){
		control.setMapView(m_mapView);
		control.getElement().getStyle().setZIndex(100000);
		m_lp.add(control);
		m_lp.setWidgetRightWidth(control, 10, Style.Unit.PX, 500, Style.Unit.PX );
		m_lp.setWidgetTopHeight(control, 10, Style.Unit.PX, 20, Style.Unit.PX );
	}
	
	@Export("addDimmerControl")
	public void addMapDimmerControl( XMapDimmerControl control ){
		// Map dimmer control
		control.setMapView( m_mapView, true );
		control.getElement().getStyle().setZIndex(100000);
		m_lp.add(control);
		m_lp.setWidgetRightWidth(control,10, Style.Unit.PX, 35, Style.Unit.PX);
		m_lp.setWidgetBottomHeight(control, 10, Style.Unit.PX, 22, Style.Unit.PX);
	}
	
	@Export("addFlyToControl")
	public void addMapFlyToControl( XFlyToControl control ){
		// Fly To control
		//control.getElement().getStyle().setZIndex(100000);
		m_lp.add(control);
		m_lp.setWidgetRightWidth( control, 0, Style.Unit.PX, 160, Style.Unit.PX );
		m_lp.setWidgetTopHeight( control, 0, Style.Unit.PX, 50, Style.Unit.PX );
		control.addSearchHandler(new FlyToController(m_mapView));
	}

	@Export("addResizeButton")
	private void addResizeButton(){
		Button resizeMap = new Button("Fill Viewport", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_dockPanel.setHeight("100%");
				m_dockPanel.setWidth("100%");
			}});
		
		m_mapView.addChangeListener(new ChangeListener() {
			@Override
			public void onChange(Widget sender) {
				mapChanged();
			}});

		HorizontalPanel bar = new HorizontalPanel();
		bar.add(resizeMap);

		m_dockPanel.addNorth(bar, 20);
	}

	private void doMap( String divId ) {
		
		RootPanel mapPanel = RootPanel.get(divId);
		mapPanel.setHeight("100%");
		mapPanel.setWidth("100%");
		
		DOM.setInnerHTML(mapPanel.getElement(), "");
		String strWidth = Integer.toString(m_width)+"px";
		String strHeight = Integer.toString(m_height)+"px";	
		m_dockPanel.setWidth(strWidth);
		m_dockPanel.setHeight(strHeight);
		
		mapPanel.add(m_dockPanel);
		
		m_mapView = new MapView();
		m_mapView.setPixelSize( m_width, m_height );
		// loadLayerConfigsFromServer();
		loadLayerConfigsFromClient(); 
		
		addResizeButton();
		
		MapPanel mapFillPanel = new MapPanel(m_mapView);
		m_mapView.setDpi( m_scrnDpi );
		m_mapView.getController().withHoverDelayMillis(MAP_HOVER_DELAY_MILLIS);
		
		m_mapView.updateView();
		
		m_lp = new LayoutPanel();
		m_dockPanel.add(m_lp);
		// Add the map viewport 
		m_lp.add(mapFillPanel);
	
	}
	
	private void loadLayerConfigsFromClient() {
		// this assumes we are starting with cell sizes of 
		// 180x180 and pixels sizes of 512 x 512
		// Try to pull from json in .html
		JsArray<LayerSetJson> layerSets = getLayerSets();
		if (layerSets != null) {
			for (int i = 0, n = layerSets.length(); i < n; ++i) {
				LayerSetJson l = layerSets.get(i);
				LayerSet ls = l.toLayerSet();
				m_mapView.addLayer(ls);
			}
		}
	  
		// TODO need a library of layer configs. I see that BV uses the same
		// code over and over again
		// to setup the layers and if change them we'd have to change it
		// everywhere.

		if (isTrue("useTmsBMNG", false)) {
			LayerSet layerSet = new LayerSet();
			layerSet.setServer(OPTIONS.get("tmsURL"));
			layerSet.setUrlPattern("{server}/BMNG@EPSG:4326@png/{level}/{x}/{y}.png");
			layerSet.setData("");
			layerSet.setPixelHeight(256);
			layerSet.setPixelWidth(256);
			m_mapView.addLayer(layerSet);
		}	
	}

	private void mapChanged(){
	}
	
	private boolean isTrue(String key, boolean def) {
		if (!OPTIONS.keySet().contains(key)) {
			return def;
		}
		String value = OPTIONS.get(key);
		return Boolean.valueOf(value);
	}

}
