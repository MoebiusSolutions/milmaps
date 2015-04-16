/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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


import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.WorldCoords;

public interface ICoordConverter {
	public abstract void setViewPort(ViewPort vp);
	
	public abstract ViewCoords geodeticToView(GeodeticCoords gc);

	public abstract GeodeticCoords viewToGeodetic(ViewCoords vc);

	public abstract WorldCoords geodeticToWorld(GeodeticCoords gc);

	public abstract GeodeticCoords worldToGeodetic(WorldCoords wc);
	
	public abstract ViewCoords worldToView(WorldCoords wc);
	
	public abstract WorldCoords viewToWorld(ViewCoords vc);
	
	public abstract int mapWidth();
	
	public abstract void setISplit(ISplit split);
	
	public abstract ISplit getISplit();
}
