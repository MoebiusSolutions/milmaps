package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.moesol.gwt.maps.client.units.Degrees;

public class MapJsApi implements EntryPoint {

	@Override
	public void onModuleLoad() {
		addJsMethods();
	}
	
	// Some default layers
	public static LayerSet[] getWorldWindLayerSets() {
		// Using bv.moesol.com for demo server
		String servletUrl = "http://bv.moesol.com/rpf-ww-server";
		return getWorldWindLayersForServer(servletUrl);
	}

	private static LayerSet[] getWorldWindLayersForServer(String servletUrl) {
		ArrayList<LayerSet> result = new ArrayList<LayerSet>();
		
		result.add(new LayerSet()
			.withServer(servletUrl)
			.withData("BMNG (Shaded %2B Bathymetry) Tiled - 5.2004") 
			.withUrlPattern("{server}/tileset/BMNG/{data}/level/{level}/x/{x}/y/{y}")
			.build());
		
		result.add(new LayerSet()
			.withServer(servletUrl)
			.withData("esat_world")
			.withSkipLevels(4)
			.withUrlPattern("{server}/tileset/{data}/level/{level}/x/{x}/y/{y}")
			.build()
		);
		
		return result.toArray(new LayerSet[result.size()]);
	}

	public static MapView newMapView(String id) {
		RootPanel root = RootPanel.get(id);
		MapView map = new MapView(new CylEquiDistProj());
//		map.addLayers(getWorldWindLayerSets());
		root.add(map);
//		map.updateView();
		return map;
	}
	
	public static void setLayerServerUrl(MapView map, String url) {
		map.clearLayers();
		map.addLayers(getWorldWindLayersForServer(url));
	}
	
	public static void setIconLocation(MapView map, int index, double latDegrees, double lngDegrees) {
		Icon icon = findOrMakeIcon(map, index);
		icon.setLocation(Degrees.geodetic(latDegrees, lngDegrees));
	}
	
	public static void setIconUrl(MapView map, int index, String url) {
		Icon icon = findOrMakeIcon(map, index);
		icon.setIconUrl(url);
	}
	
	public static void setIconOffsets(MapView map, int index, int x, int y) {
		Icon icon = findOrMakeIcon(map, index);
		icon.setIconOffset(new ViewCoords(x, y));
	}
	
	public static void setIconCount(MapView map, int count) {
		IconLayer iconLayer = map.getIconLayer();
		List<Icon> icons = iconLayer.getIcons();
		while (icons.size() > count) {
			iconLayer.removeIcon(icons.get(0));
		}
	}

	private static Icon findOrMakeIcon(MapView map, int index) {
		List<Icon> icons = map.getIconLayer().getIcons();
		if (icons.size() == index) {
			Icon icon = new Icon();
			icons.add(icon);
			return icon;
		}
		return icons.get(index);
	}

	private native void addJsMethods() /*-{
		$wnd.moesol = {
			newMapView: function(elId) {
				var map = @com.moesol.gwt.maps.client.MapJsApi::newMapView(Ljava/lang/String;)(elId);
				
				// Attach methods
				map.setLayerServerUrl = function(url) {
					@com.moesol.gwt.maps.client.MapJsApi::setLayerServerUrl(Lcom/moesol/gwt/maps/client/MapView;Ljava/lang/String;)(map, url);
				}
				map.setPixelSize = function(width, height) { 
					map.@com.moesol.gwt.maps.client.MapView::setPixelSize(II)(width, height); 
				}
				map.updateView = function() {
					map.@com.moesol.gwt.maps.client.MapView::updateView()();
				}
				map.setCenter = function(latDegrees, lngDegrees) {
					map.@com.moesol.gwt.maps.client.MapView::setCenter(DD)(latDegrees, lngDegrees);
				}
				map.zoomIn = function() {
					map.@com.moesol.gwt.maps.client.MapView::animateZoom(D)(scaleFactor);
				}
				map.zoomOut = function() {
					map.@com.moesol.gwt.maps.client.MapView::animateZoom(D)(scaleFactor);
				}
				map.setIconLocation = function(index, lat, lng) {
					@com.moesol.gwt.maps.client.MapJsApi::setIconLocation(Lcom/moesol/gwt/maps/client/MapView;IDD)(map, index, lat, lng);
				}
				map.setIconUrl = function(index, url) {
					@com.moesol.gwt.maps.client.MapJsApi::setIconUrl(Lcom/moesol/gwt/maps/client/MapView;ILjava/lang/String;)(map, index, url);
				}
				map.setIconOffsets = function(index, x, y) {
					@com.moesol.gwt.maps.client.MapJsApi::setIconOffsets(Lcom/moesol/gwt/maps/client/MapView;III)(map, index, x, y);
				}
				map.setIconCount = function(count) {
					@com.moesol.gwt.maps.client.MapJsApi::setIconCount(Lcom/moesol/gwt/maps/client/MapView;I)(map, count);
				}
				return map;
			}
		};
		
		if ($wnd.moesolMapReady) {
			$wnd.moesolMapReady();
		}
	}-*/;

}
