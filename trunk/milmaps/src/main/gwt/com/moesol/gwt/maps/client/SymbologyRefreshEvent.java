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

	private static final Type<Handler> TYPE = new Type<Handler>();
	private final MapView map;

	private SymbologyRefreshEvent(final MapView map)
	{
		this.map = map;
	}

	public static void fire(final EventBus eventBus, final MapView map)
	{
		eventBus.fireEvent(new SymbologyRefreshEvent(map));
	}

	public static Type<Handler> getType()
	{
		return TYPE;
	}

	public static HandlerRegistration register(final EventBus eventBus,
			final Handler handler)
	{
		return eventBus.addHandler(SymbologyRefreshEvent.getType(), handler);
	}

	public MapView getMap()
	{
		return this.map;
	}

	@Override
	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler)
	{
		handler.onSymbologyRefresh(this);
	}
}
