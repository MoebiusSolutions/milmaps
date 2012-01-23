package com.moesol.gwt.maps.client.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class MapSingleClickEvent
        extends GwtEvent<MapSingleClickEvent.Handler>
{
    public interface Handler
            extends EventHandler
    {
        void onMapSingleClick(MapSingleClickEvent event);
    }

    private static final Type<Handler> TYPE = new Type<Handler>();
    private final int y;
    private final int x;

    private MapSingleClickEvent(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    public static void fire(final EventBus eventBus, final int x, final int y)
    {
        eventBus.fireEvent(new MapSingleClickEvent(x, y));
    }

    public static Type<Handler> getType()
    {
        return TYPE;
    }

    public static HandlerRegistration register(final EventBus eventBus,
            final Handler handler)
    {
        return eventBus.addHandler(TYPE, handler);
    }

    public int getY()
    {
        return y;
    }

    public int getX()
    {
        return x;
    }

    @Override
    public Type<Handler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final Handler handler)
    {
        handler.onMapSingleClick(this);
    }
}
