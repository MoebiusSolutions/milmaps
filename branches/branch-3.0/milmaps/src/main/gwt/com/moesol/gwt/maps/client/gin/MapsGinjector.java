/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.gin;

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
