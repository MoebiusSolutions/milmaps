package com.moesol.gwt.maps.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SymbologyRefreshEvent
        extends GwtEvent<SymbologyRefreshEvent.Handler>
{
    public interface Handler
            extends EventHandler
    {
        void onSymbologyRefresh(final SymbologyRefreshEvent event);
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    private SymbologyRefreshEvent()
    {

    }

    public static void fire(final EventBus eventBus)
    {
        eventBus.fireEvent(new SymbologyRefreshEvent());
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

    @Override
    public final Type<Handler> getAssociatedType()
    {
        return TYPE;
    }

    @Override
    protected void dispatch(final Handler handler)
    {
        handler.onSymbologyRefresh(this);
    }
}
