/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.xmilmaps.client;

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


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class MilmapsExp implements EntryPoint {

	public void onModuleLoad() {

		GWT.create(XMap.class);
		GWT.create(XFlyToControl.class);
		GWT.create(XMapDimmerControl.class);
		GWT.create(XMapPanZoomControl.class);
		GWT.create(XPositionControl.class);
		GWT.create(XIcon.class);
		loadImpl();
	}
	private native void loadImpl() /*-{    
		if ($wnd.mapsOnLoad && typeof $wnd.mapsOnLoad == 'function') $wnd.mapsOnLoad();  
	}-*/;
}
