package com.moesol.gwt.maps.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface MapViewUpdatedHandler extends EventHandler {
	public void onMapViewUpdated(MapViewUpdatedEvent mapViewChangedEvent);
}
