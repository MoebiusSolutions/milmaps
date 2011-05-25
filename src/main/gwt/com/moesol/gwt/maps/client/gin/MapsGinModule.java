package com.moesol.gwt.maps.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Singleton;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.place.MapsPlaceController;
import com.moesol.gwt.maps.client.tms.TileMapServiceListView;
import com.moesol.gwt.maps.client.tms.TileMapServiceView;

public class MapsGinModule extends AbstractGinModule {
	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(PlaceController.class).to(MapsPlaceController.class).in(Singleton.class);
		bind(TileMapServiceView.class).to(TileMapServiceListView.class).in(Singleton.class);
		bind(MapView.class).in(Singleton.class);
	}
}
