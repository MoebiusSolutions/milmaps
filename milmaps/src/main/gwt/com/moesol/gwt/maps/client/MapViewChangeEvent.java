package com.moesol.gwt.maps.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class MapViewChangeEvent
        extends ValueChangeEvent<MapView>
{
    public interface Handler
            extends ValueChangeHandler<MapView>
    {
    }

    private static final Type<Handler> TYPE = new Type<Handler>();

    private MapViewChangeEvent(final MapView value)
    {
        super(value);
    }

    public static void fire(final EventBus eventBus, final MapView map)
    {
        eventBus.fireEvent(new MapViewChangeEvent(map));
    }

    public static HandlerRegistration register(EventBus eventBus,
            Handler handler)
    {
        return eventBus.addHandler(TYPE, handler);
    }
}
