/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.server;

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


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.moesol.gwt.maps.client.ILayerConfig;
import com.moesol.gwt.maps.client.LayerSet;

public class WorldwindLayerConfigs extends RemoteServiceServlet implements ILayerConfig {

	public static final Logger s_logger = Logger.getLogger(WorldwindLayerConfigs.class.getName());
	
	@Override
	public LayerSet[] getLayerSets() {
		ArrayList<LayerSet> result = new ArrayList<LayerSet>();
		LayerSet layerSet;
			
//		layerSet = new LayerSet();
//		layerSet.setServer("http://worldwind25.arc.nasa.gov/tile/tile.aspx");
//		layerSet.setData("bmng.topo.bathy.200405");
//		result.add(layerSet);
//			
//		layerSet = new LayerSet();
//		layerSet.setServer("http://worldwind25.arc.nasa.gov/lstile/lstile.aspx");
//		layerSet.setData("esat_world"); // esat_worlddds give dds files.
//		layerSet.setSkipLevels(4);
//		result.add(layerSet);
		
		layerSet = new LayerSet();
		layerSet.setServer("http://services.arcgisonline.com/ArcGIS/rest/services");
		layerSet.setData("I3_Imagery_Prime_World_2D");
		layerSet.setUrlPattern("{server}/{data}/MapServer/tile/{level}/{y}/{x}");
		layerSet.setZeroTop(true);
		result.add(layerSet);
			
		layerSet = new LayerSet();
		layerSet.setServer("http://localhost/rpf-ww-server/tiles");
		layerSet.setData("tracks");
		result.add(layerSet);
		
		return result.toArray(new LayerSet[result.size()]);
	}
	
	@Override
	public void log(int level, String logString) {
		
		switch(level) {
		case 1000:
			s_logger.log(Level.SEVERE, logString);
			break;
		case 900:
			s_logger.log(Level.WARNING, logString);
			break;
		case 800:
			s_logger.log(Level.INFO, logString);
			break;
		case 700:
			s_logger.log(Level.CONFIG, logString);
			break;
		case 500:
			s_logger.log(Level.FINE, logString);
			break;
		case 400:
			s_logger.log(Level.FINER, logString);
			break;
		case 300:
			s_logger.log(Level.FINEST, logString);
			break;
		
		default:
			s_logger.log(Level.ALL, logString);
			break;
		}
	}
}
