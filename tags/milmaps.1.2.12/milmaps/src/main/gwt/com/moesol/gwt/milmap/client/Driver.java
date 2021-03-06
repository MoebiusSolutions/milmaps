/*
 * $Id$
 *
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                       All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.milmap.client;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ILayerConfig;
import com.moesol.gwt.maps.client.ILayerConfigAsync;
import com.moesol.gwt.maps.client.Icon;
import com.moesol.gwt.maps.client.LayerSet;
import com.moesol.gwt.maps.client.LayerSetJson;
import com.moesol.gwt.maps.client.MapPanel;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.controls.EdgeHoverPanControl;
import com.moesol.gwt.maps.client.controls.FlyToController;
import com.moesol.gwt.maps.client.controls.MapPanZoomControl;
import com.moesol.gwt.maps.client.controls.SearchControl;
import com.moesol.gwt.maps.client.controls.MapDimmerControl;
import com.moesol.gwt.maps.client.controls.PositionControl;
import com.moesol.gwt.maps.client.controls.SearchEvent;
import com.moesol.gwt.maps.client.controls.SearchHandler;
import com.moesol.gwt.maps.client.controls.TagControl;
import com.moesol.gwt.maps.client.gin.MapsGinjector;
import com.moesol.gwt.maps.client.place.MapsActivityMapper;
import com.moesol.gwt.maps.client.place.MapsPlaceHistoryMapper;
import com.moesol.gwt.maps.client.tms.TileMapServicePlace;
import com.moesol.gwt.maps.client.tms.TileMapServiceView;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class Driver implements EntryPoint {

	private static final int MAP_EDGE_HOVER_MAX_PAN_PER_INTERVAL_PIXELS = 10;
	private static final int MAP_EDGE_HOVER_PAN_INTERVAL = 100;
	private static final int MAP_EDGE_HOVER_RADIUS_PIXELS = 50;
	private static final int MAP_HOVER_DELAY_MILLIS = 300;
	private static final int TILE_DY = 3;
	private static final int TILE_DX = 4;
	private static Dictionary OPTIONS = Dictionary.getDictionary("milMap_options");
	private int m_scrnDpi = 75;
	private MapView m_map;
	//private PositionControl m_mousePosLabel;
	private final Label m_centerLabel = new Label();
	private final Grid m_tiles = new Grid(TILE_DY, TILE_DX);
	private SuggestBox m_levelBox;
	
	private Place defaultPlace = new TileMapServicePlace(new String[]{"BMNG@EPSG:4326@png"}, 0, 0, 0);
	
	private MapsGinjector injector = GWT.create(MapsGinjector.class);
	EventBus eventBus;

	public Driver() {
		super();
	}

	/** Hook out to layerSet json data */
	private final native JsArray<LayerSetJson> getLayerSets() /*-{
    	return $wnd.layerSets;
	}-*/;

	@Override
	public void onModuleLoad() {
		PlaceHistoryHandler historyHandler = null;
		if (isTrue("showLayerPanel", false)) {
			eventBus = injector.getEventBus();
			PlaceController placeController = injector.getPlaceController();

			ActivityMapper activityMapper = new MapsActivityMapper(injector);
			ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
			activityManager.setDisplay(new SimplePanel());

			// Start PlaceHistoryHandler with our PlaceHistoryMapper
			MapsPlaceHistoryMapper historyMapper = GWT.create(MapsPlaceHistoryMapper.class);
			historyHandler = new PlaceHistoryHandler(historyMapper);
			historyHandler.register(placeController, eventBus, defaultPlace);
		}
		RootPanel mapPanel = RootPanel.get("mapPanel");
		if (mapPanel == null) {
			throw new IllegalStateException("No element with id 'mapPanel'");
		}
		doMapPanel(mapPanel);
		if (isTrue("showLayerPanel", false)) {
			historyHandler.handleCurrentHistory();
		}
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
		dockPanel.setHeight("420px");
		dockPanel.setWidth("600px");
		mapPanel.add(dockPanel);

		// loadLayerConfigsFromServer();
		m_map = new MapView();
		loadLayerConfigsFromClient(); 
		
		MapPanel mapFillPanel = new MapPanel(m_map);
		m_map.setDpi( m_scrnDpi );
		m_map.getController().withHoverDelayMillis(MAP_HOVER_DELAY_MILLIS);
		
		//new EdgeHoverPanControl(m_map, MAP_EDGE_HOVER_RADIUS_PIXELS,
		//		MAP_EDGE_HOVER_PAN_INTERVAL,
		//		MAP_EDGE_HOVER_MAX_PAN_PER_INTERVAL_PIXELS);
//		m_map.getController().addHoverHandler(new HoverHandler() {
//			BubbleControl bc = new BubbleControl(m_map);
//			
//			@Override
//			public void onHover(HoverEvent e) {
//				ViewCoords m_vc = new ViewCoords(e.getX(), e.getY());
//				GeodeticCoords gc = m_map.getProjection().viewToGeodetic(m_vc);
//				bc.getHtml().setHTML("Hover: " + gc.toString());
//				bc.animateShow(e.getClientX(), e.getClientY());
//			}
//		});
		
		if (isTrue("showSomeIcons", false)) {
			addSomeIcons();
		}
		
		m_map.updateView();
		

		Button removeIcons = new Button("Remove Icons", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeIcons();
			}});
		Button moveIcons = new Button("Move Icons", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveIcons();
			}});
		Button resizeMap = new Button("Fill Viewport", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dockPanel.setHeight("100%");
				dockPanel.setWidth("100%");
			}});
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.add("0");
		oracle.add("01");
		oracle.add("1");
		oracle.add("3");
		m_levelBox = new SuggestBox();
		
		m_map.addChangeListener(new ChangeListener() {
			@Override
			public void onChange(Widget sender) {
				mapChanged();
			}});

		HorizontalPanel bar = new HorizontalPanel();
		bar.add(removeIcons);
		bar.add(moveIcons);
		bar.add(resizeMap);

		dockPanel.addNorth(bar, 20);
		
		LayoutPanel hp = new LayoutPanel();
		dockPanel.add(hp);

		if (isTrue("showLayerPanel", false)) {
			addTileMapServiceView(hp);
		}
		
		// The map viewport
		hp.add(mapFillPanel);
		//count = hp.getWidgetCount();
		//hp.setWidgetLeftWidth(mapFillPanel,0, Style.Unit.PCT, 100, Style.Unit.PCT);
		// The map controls
		MapPanZoomControl mapControls = new MapPanZoomControl(m_map, 15, 100);
		mapControls.getElement().getStyle().setZIndex(100000);
		hp.add(mapControls);
		hp.setWidgetLeftWidth(mapControls,0, Style.Unit.PX, 56, Style.Unit.PX);
		hp.setWidgetTopHeight(mapControls,0, Style.Unit.PX, 121, Style.Unit.PX);
		
		//The mouse position label;
		PositionControl mousePosLabel = new PositionControl(m_map);
		hp.add(mousePosLabel);
		hp.setWidgetRightWidth(mousePosLabel, 10, Style.Unit.PX, 500, Style.Unit.PX );
		hp.setWidgetTopHeight(mousePosLabel, 10, Style.Unit.PX, 20, Style.Unit.PX );
		
		// Map dimmer control
		MapDimmerControl dimmer = new MapDimmerControl(m_map, true);
		hp.add(dimmer);
		hp.setWidgetRightWidth(dimmer,10, Style.Unit.PX, 35, Style.Unit.PX);
		hp.setWidgetBottomHeight(dimmer, 10, Style.Unit.PX, 22, Style.Unit.PX);
		
		// Map tag control
		TagControl tag = new TagControl(m_map, true);
		hp.add(tag);
		hp.setWidgetRightWidth(tag,10, Style.Unit.PX, 35, Style.Unit.PX);
		hp.setWidgetBottomHeight(tag, 40, Style.Unit.PX, 22, Style.Unit.PX);
		
		SearchControl flyToControl = new SearchControl();
		hp.add(flyToControl);
		hp.setWidgetRightWidth(flyToControl, 10, Style.Unit.PX, 160, Style.Unit.PX);
		hp.setWidgetTopHeight(flyToControl, 10, Style.Unit.PX, 50, Style.Unit.PX);
		
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

	private void addTileMapServiceView(Panel panel) {
		TileMapServiceView tileMapServiceView = injector.getTileMapServiceView();
		panel.add(tileMapServiceView);
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
		Icon icon = new Icon();
		icon.setIconUrl("http://www.moesol.com/icons/moesol_logo_small.jpg");
		icon.setLocation(new GeodeticCoords(0,0,AngleUnit.DEGREES));
		icon.getImage().setPixelSize(212/2, 86/2);
		m_map.getIconLayer().addIcon(icon);

		// int num = 1024, workable, sluggish
		// int num = 512, better
		// int num = 256, good
		// int num = 128, rocking
		int num = 128;
		double maxx = 360.0;
		double maxy = 30.0;
		double incx = maxx/num;
		double incy = maxy/num;
		double lat = 0.0;
		double lng = -180.0;
		for (int i = 0; i < num; i++) {
			addOneIcon(lat, lng);
			lat += incy;
			lng += incx;
		}
	}

	private void addOneIcon(double lat, double lng) {
		Icon icon;
		icon = new Icon();
		icon.setIconUrl("http://www.moesol.com/products/mx/js/mil_picker/mil_picker_images/sfapmfq--------.jpeg");
		icon.setLocation(new GeodeticCoords(lng, lat, AngleUnit.DEGREES));
		icon.getImage().setPixelSize(16, 16);
		m_map.getIconLayer().addIcon(icon);
	}

	protected void mapChanged() {
		m_centerLabel.setText(m_map.getCenter() +"," + m_map.getViewport().getLevel());
	}

	protected void resizeMap() {
		int h = Window.getClientHeight();
		int w = Window.getClientWidth();
		m_map.resizeMap(w - 50, h - 50);
		
		updateTileInfo();
	}

	protected void removeIcons() {
		List<Icon> icons = m_map.getIconLayer().getIcons();
		while (icons.size() != 0) {
			Icon icon = icons.get(0);
			m_map.getIconLayer().removeIcon(icon);
		}
	}
	
	protected void moveIcons() {
		List<Icon> icons = m_map.getIconLayer().getIcons();
		Iterator<Icon> it = icons.iterator();
		while (it.hasNext()) {
			Icon icon = it.next();
			GeodeticCoords location = icon.getLocation();
			location.setLambda(location.getLambda(AngleUnit.DEGREES) + 1.0, AngleUnit.DEGREES);
		}
		m_map.updateView();
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
