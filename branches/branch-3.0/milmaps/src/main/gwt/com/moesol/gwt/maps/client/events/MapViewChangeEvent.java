/**
b * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.events;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.moesol.gwt.maps.client.MapView;

public class MapViewChangeEvent
		extends GwtEvent<MapViewChangeEvent.Handler>
{
	public interface Handler
			extends EventHandler
	{
		void onMapViewChangeEvent(final MapViewChangeEvent event);
	}

	private static final Type<Handler> TYPE = new Type<Handler>();
	private final MapView map;

	private MapViewChangeEvent(final MapView map)
	{
		this.map = map;
	}

	public static void fire(final EventBus eventBus, final MapView map)
	{
		eventBus.fireEvent(new MapViewChangeEvent(map));
	}

	public static Type<Handler> getType()
	{
		return TYPE;
	}

	public static HandlerRegistration register(EventBus eventBus, Handler handler)
	{
		return eventBus.addHandler(TYPE, handler);
	}

	public MapView getMap()
	{
		return map;
	}

	@Override
	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(final Handler handler)
	{
		handler.onMapViewChangeEvent(this);
	}
}
