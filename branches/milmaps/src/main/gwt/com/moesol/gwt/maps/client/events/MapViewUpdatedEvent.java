package com.moesol.gwt.maps.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.moesol.gwt.maps.client.MapView;

/**
 * Called after every updateView completes.
 * @author hastings
 */
public class MapViewUpdatedEvent extends GwtEvent<MapViewUpdatedHandler> {
	public static final Type<MapViewUpdatedHandler> TYPE = new Type<MapViewUpdatedHandler>();
	private final MapView m_mapView;

	public MapViewUpdatedEvent(MapView mapView) {
    	m_mapView = mapView;
	}

	@Override
	public Type<MapViewUpdatedHandler> getAssociatedType() {
		return TYPE;
	}

    public static Type<MapViewUpdatedHandler> getType() {
        return TYPE;
    }

	@Override
	protected void dispatch(MapViewUpdatedHandler handler) {
		handler.onMapViewUpdated(this);
	}
	
    public MapView getMapView() {
    	return m_mapView;
	}

}
