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


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ProjectionValues {
	
	private static final long ONE_YEAR = 365*24*60*60*1000;
	private static final String S_COOKIE_LEVEL = "Level";
	
	// TODO use projection "neutral" values, lat/lng/scale...
	public static ProjectionValues readCookies(IProjection projection)  {
		// if any part is not found, just use defaults
		ProjectionValues result = new ProjectionValues();
		
		//String sLevel = Cookies.getCookie(S_COOKIE_LEVEL);
		//if (sLevel != null) {
		//	try {
		//		int level = Integer.parseInt(sLevel);
		//		//projection.setLevel(level);
		//	} catch (NumberFormatException e) {
		//		log("Unable to read projection information from cookie.");
		//	}	
		//}
		return result;
	}
	
	public static void writeCookies(IProjection projection) {
		//String level = Integer.toString(projection.getLevel());
		
		//Date expire = new Date(new Date().getTime() + ONE_YEAR);
		//Cookies.setCookie(S_COOKIE_LEVEL, level, expire);
	}

	static void log(String msg) {
		ILayerConfigAsync service = (ILayerConfigAsync) GWT.create(ILayerConfig.class);
		ServiceDefTarget endpoint = (ServiceDefTarget)service;
		endpoint.setServiceEntryPoint("/BvWebapp/com.moesol.bv.map.Map/mapLayers");
		
		service.log(ILayerConfig.S_LOG_WARNING,msg, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to log error " + caught);
			}
			@Override
			public void onSuccess(Void result) {
			}});
	}
	
}