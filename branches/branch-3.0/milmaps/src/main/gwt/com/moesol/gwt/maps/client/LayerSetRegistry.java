/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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


/** These are the well known public, free map tile servers */
public class LayerSetRegistry {
	public final LayerSet ARCGIS_I3 = new LayerSet()
		.withServer("http://localhost:8080/geowebcache/service/tms/1.0.0")
		.withData("I3_Imagery_Prime_World_2D")
		.withUrlPattern("{server}/BMNG@EPSG:4326@png/{level}/{y}/{x}.png")
		//.withServer("http://services.arcgisonline.com/ArcGIS/rest/services")
		//.withData("I3_Imagery_Prime_World_2D")
		//.withUrlPattern("{server}/{data}/MapServer/tile/{level}/{y}/{x}")
		.withZeroTop(true)
		.build();
	public final LayerSet BLUE_MARBLE_NEXTGEN = new LayerSet()
		.withServer("http://worldwind25.arc.nasa.gov/tile/tile.aspx")
		.withData("bmng.topo.bathy.200405")
		.build();
	public final LayerSet ESAT_WORLD = new LayerSet()
		.withServer("http://worldwind25.arc.nasa.gov/lstile/lstile.aspx")
		.withData("esat_world") // esat_worlddds give dds files.
		.withSkipLevels(4)
		.build();
}
