package com.moesol.gwt.maps.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.tms.TileMapServiceActivity;
import com.moesol.gwt.maps.client.tms.TileMapServiceView;

@GinModules(MapsGinModule.class)
public interface MapsGinjector extends Ginjector {
	public EventBus getEventBus();
	public PlaceController getPlaceController();
	public TileMapServiceView getTileMapServiceView();
	public TileMapServiceActivity getTileMapServiceActivity();
	public MapView getMapView();
}
