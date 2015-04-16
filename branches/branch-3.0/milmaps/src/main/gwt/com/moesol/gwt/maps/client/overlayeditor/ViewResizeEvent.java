/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.overlayeditor;

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
import com.moesol.gwt.maps.client.events.MapViewChangeEvent.Handler;

public class ViewResizeEvent extends GwtEvent<ViewResizeEvent.IResizeEventHandler> {
	  
	public interface IResizeEventHandler extends EventHandler
	{
		void onResizeEvent(final ViewResizeEvent event);
	}

	private static final Type<IResizeEventHandler> TYPE = new Type<IResizeEventHandler>();
	private final MapView map;
	
	private ViewResizeEvent(final MapView map)
	{
		this.map = map;
	}
	
	public MapView getMapView(){return this.map;}
	

	public static Type<IResizeEventHandler> getType()
	{
		return TYPE;
	}

	@Override
	public Type<IResizeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(final IResizeEventHandler handler)
	{
		handler.onResizeEvent(this);
	}
	
	public static void fire(final EventBus eventBus, final MapView map)
	{
		eventBus.fireEvent(new ViewResizeEvent(map));
	}
	
	public static HandlerRegistration register(EventBus eventBus, IResizeEventHandler handler)
	{
		return eventBus.addHandler(TYPE, handler);
	}
}
