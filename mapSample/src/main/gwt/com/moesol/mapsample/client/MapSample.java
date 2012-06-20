/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.mapsample.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.moesol.gwt.maps.client.AbstractProjection;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ILayerConfig;
import com.moesol.gwt.maps.client.ILayerConfigAsync;
import com.moesol.gwt.maps.client.Icon;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapPanel;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewDimension;
import com.moesol.gwt.maps.client.controls.FlyToController;
import com.moesol.gwt.maps.client.controls.MapDimmerControl;
import com.moesol.gwt.maps.client.controls.MapPanZoomControl;
import com.moesol.gwt.maps.client.controls.PositionControl;
import com.moesol.gwt.maps.client.controls.SearchControl;
import com.moesol.gwt.maps.client.controls.TextControl;
import com.moesol.gwt.maps.client.stats.StatsDialogBox;
import com.moesol.gwt.maps.client.tms.TileMapServicePlace;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.MapScale;
import com.moesol.mapsample.client.controls.TagControl;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MapSample implements EntryPoint {
	private static final int MAP_EDGE_HOVER_MAX_PAN_PER_INTERVAL_PIXELS = 10;
	private static final int MAP_EDGE_HOVER_PAN_INTERVAL = 100;
	private static final int MAP_EDGE_HOVER_RADIUS_PIXELS = 50;
	private static final int MAP_HOVER_DELAY_MILLIS = 300;
	private static final int TILE_DY = 3;
	private static final int TILE_DX = 4;
	private static Dictionary OPTIONS = Dictionary.getDictionary("milMap_options");
	private int m_scrnDpi = AbstractProjection.DOTS_PER_INCH;
	private MapView m_map;
	private final Label m_centerLabel = new Label();
	private final Grid m_tiles = new Grid(TILE_DY, TILE_DX);
	private SuggestBox m_levelBox;
	TextControl m_textControl = null;
	
	private Place defaultPlace = new TileMapServicePlace(new String[]{"BMNG@EPSG:4326@png"}, 0, 0);
	
	EventBus eventBus;
	
	/** Hook out to layerSet json data */
	private final native JsArray<LayerSetJson> getLayerSets() /*-{
    	return $wnd.layerSets;
	}-*/;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel mapPanel = RootPanel.get("mapPanel2");
		if (mapPanel == null) {
			throw new IllegalStateException("No element with id 'mapPanel2'");
		}
		doMapPanel(mapPanel);
		return;
	}

	private boolean isTrue(String key, boolean def) {
		if (!OPTIONS.keySet().contains(key)) {
			return def;
		}
		String value = OPTIONS.get(key);
		return Boolean.valueOf(value);
	}
	
	
	private void doMapPanel(RootPanel mapPanel) {
		doMap(mapPanel);
	}
	private void doMap(RootPanel mapPanel) {
		//doMap(mapPanel);

		mapPanel.setHeight("100%");
		mapPanel.setWidth("100%");
		
		DOM.setInnerHTML(mapPanel.getElement(), "");
		
		final DockLayoutPanel dockPanel = new DockLayoutPanel(Unit.PX);
		dockPanel.setHeight("100%");
		dockPanel.setWidth("100%");
		mapPanel.add(dockPanel);

		// loadLayerConfigsFromServer();
		m_map = new MapView();
		loadLayerConfigsFromClient(); 
		
		MapPanel mapFillPanel = new MapPanel(m_map);
		m_map.setDpi( m_scrnDpi );
		m_map.getController().withHoverDelayMillis(MAP_HOVER_DELAY_MILLIS);
		m_map.setDeclutterLabels(true);

		if (isTrue("showSomeIcons", false)) {
			addSomeIcons();
		}
		
		m_map.updateView();
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.add("0");
		oracle.add("01");
		oracle.add("1");
		oracle.add("3");
		m_levelBox = new SuggestBox();
		
		m_map.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				mapChanged();
			}	
		});
		
		LayoutPanel lp = new LayoutPanel();
		dockPanel.add(lp);
		
		// The map viewport
		lp.add(mapFillPanel);
		//count = hp.getWidgetCount();
		//hp.setWidgetLeftWidth(mapFillPanel,0, Style.Unit.PCT, 100, Style.Unit.PCT);
		// The map controls
		MapPanZoomControl mapControls = new MapPanZoomControl(m_map, 15, 10);
		mapControls.getElement().getStyle().setZIndex(100000);
		lp.add(mapControls);
		lp.setWidgetLeftWidth(mapControls,0, Style.Unit.PX, 56, Style.Unit.PX);
		lp.setWidgetTopHeight(mapControls,0, Style.Unit.PX, 121, Style.Unit.PX);
		
		//The mouse position label;
		PositionControl mousePosLabel = new PositionControl(m_map);
		lp.add(mousePosLabel);
		lp.setWidgetRightWidth(mousePosLabel, 10, Style.Unit.PX, 500, Style.Unit.PX );
		lp.setWidgetTopHeight(mousePosLabel, 10, Style.Unit.PX, 20, Style.Unit.PX );

		//The Text control;
		m_textControl= new TextControl();
		lp.add(m_textControl);
		lp.setWidgetLeftWidth(m_textControl, 10, Style.Unit.PX, 500, Style.Unit.PX );
		lp.setWidgetBottomHeight(m_textControl, 10, Style.Unit.PX, 20, Style.Unit.PX );
		
		// Map dimmer control
		MapDimmerControl dimmer = new MapDimmerControl(m_map, true);
		lp.add(dimmer);
		lp.setWidgetRightWidth(dimmer,10, Style.Unit.PX, 60, Style.Unit.PX);
		lp.setWidgetBottomHeight(dimmer, 10, Style.Unit.PX, 34, Style.Unit.PX);
		
		// Map tag control
		TagControl tag = new TagControl(m_map, true);
		lp.add(tag);
		lp.setWidgetRightWidth(tag,10, Style.Unit.PX, 35, Style.Unit.PX);
		lp.setWidgetBottomHeight(tag, 60, Style.Unit.PX, 22, Style.Unit.PX);
		
		SearchControl flyToControl = new SearchControl();
		lp.add(flyToControl);
		lp.setWidgetRightWidth(flyToControl, 10, Style.Unit.PX, 160, Style.Unit.PX);
		lp.setWidgetTopHeight(flyToControl, 10, Style.Unit.PX, 50, Style.Unit.PX);
		
		flyToControl.addSearchHandler(new FlyToController(m_map));
		
		// Layer a transparent dialog...
		if (isTrue("showTestDialog", false)) {
			DialogBox db = new DialogBox();
			db.setHTML("<a href='' onclick='false'>x</a>");
			db.setPopupPosition(100, 10);
			db.setPixelSize(100, 100);
			db.show();
		}

	}


	private void loadLayerConfigsFromClient() {
		// this assumes we are starting with cell sizes of 
		// 180x180 and pixels sizes of 512 x 512
		//m_map = new MapView();
		// Try to pull from json in .html
		
		JsArray<LayerSetJson> layerSets = getLayerSets();
		if (layerSets != null) {
			for (int i = 0, n = layerSets.length(); i < n; ++i) {
				LayerSetJson l = layerSets.get(i);
				LayerSet ls = l.toLayerSet();
				m_map.addLayer(ls);
			}
		}

		if (isTrue("useTmsBMNG", false)) {
			LayerSet layerSet = new LayerSet();
			layerSet.setServer(OPTIONS.get("tmsURL"));
			layerSet.setUrlPattern("{server}/BMNG@EPSG:4326@png/{level}/{x}/{y}.png");
			layerSet.setData("");
			layerSet.setPixelHeight(256);
			layerSet.setPixelWidth(256);
			m_map.addLayer(layerSet);
		}	
	}

	private void loadLayerConfigsFromServer() {
		ILayerConfigAsync service = (ILayerConfigAsync) GWT.create(ILayerConfig.class);
		ServiceDefTarget endpoint = (ServiceDefTarget)service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "wwLayerConfigs";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		m_map.addLayersFromLayerConfig(service);
	}

	private void addSomeIcons() {
		addOneIcon(0,0.0,90.0);
		
	}

	private void addOneIcon(int i, double lat, double lng) {
		Icon icon;
		icon = new Icon();
		icon.setLabel("icon number " + i);
		icon.setIconUrl("http://www.moesol.com/products/mx/js/mil_picker/mil_picker_images/sfapmfq--------.jpeg");
		icon.setLocation(new GeodeticCoords(lng, lat, AngleUnit.DEGREES));
		icon.setImageSize(new ViewDimension(16,16));
		icon.setZIndex(2010);
		m_map.getIconLayer().addIcon(icon);
	}

	protected void mapChanged() {
		m_centerLabel.setText(m_map.getCenter() +"," + 
				MapScale.forScale(m_map.getProjection().getEquatorialScale()));
		
		//Window.alert("mapChanged");
		String s = m_map.getDivManager().getCurrentDiv().getBestLayerData();
		if(s != null && m_textControl != null){
			m_textControl.setText(s);
		}
	}

	protected void resizeMap() {
		int h = Window.getClientHeight();
		int w = Window.getClientWidth();
		m_map.resizeMap(w - 50, h - 50);
		updateTileInfo();
	}
	
	public void stats() {
		new StatsDialogBox().show();
	}

	protected void goRight() {
		m_map.moveMapByPixels(10,0);
		updateTileInfo();
	}

	protected void goLeft() {
		m_map.moveMapByPixels(-10,0);
		updateTileInfo();
	}

	protected void goUp() {
		m_map.moveMapByPixels(0,-10);
		updateTileInfo();
	}
	
	protected void goDown() {
		m_map.moveMapByPixels(0,10);
		updateTileInfo();
	}

	protected void goIn() {
		m_map.animateZoom(2);
	}
	
	protected void goOut() {
		m_map.animateZoom(1.0/2.0);
	}
	
	//protected void flyTo() {
	//	m_map.flyTo(0,0,0);
	//}

	private void updateTileInfo() {
		m_centerLabel.setText(m_map.toString());
		for (int y = 0; y < TILE_DY; y++) {
			for (int x = 0; x < TILE_DX; x++) {
				m_tiles.setText(y, x, getTile(y, x));
				m_tiles.getCellFormatter().setWordWrap(y, x, false);
			}
		}
	}

	private String getTile(int y, int x) {
		// return "" + m_map.getTileCoords()[y * TILE_DX + x];
		return "TODO";
	}

}
