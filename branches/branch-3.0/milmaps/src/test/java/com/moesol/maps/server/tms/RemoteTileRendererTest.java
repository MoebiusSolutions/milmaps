/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

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


import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.LayerSet;

public class RemoteTileRendererTest {

	@Test
	public void testGenerateUrl() {
		String urlFormat = "{server}/{data}/{level}/{y}/{x}/{y}.png";
		
		LayerSet layerSet = new LayerSet();
		layerSet.withServer("some-server").withData("some-data");
		layerSet.withUrlPattern(urlFormat);
		
		RemoteTileRenderer remoteTileRenderer = new RemoteTileRenderer("some-server", layerSet);
		assertEquals("some-server/some-data/0/2/1/2.png", remoteTileRenderer.generateUrl(0, 1, 2));
	}
	
	@Test
	public void testGenerateUrl_ZeroTop() {
		String urlFormat = "{server}/{data}/{level}/{y}/{x}/{y}.png";
		
		LayerSet layerSet = new LayerSet();
		layerSet.withServer("some-server").withData("some-data").withZeroTop(true);
		layerSet.withUrlPattern(urlFormat);
		
		RemoteTileRenderer remoteTileRenderer = new RemoteTileRenderer("some-server", layerSet);
		assertEquals("some-server/some-data/2/3/1/3.png", remoteTileRenderer.generateUrl(2, 1, 0));
		
		assertEquals("some-server/some-data/2/0/1/0.png", remoteTileRenderer.generateUrl(2, 1, 3));
		
		assertEquals("some-server/some-data/2/2/1/2.png", remoteTileRenderer.generateUrl(2, 1, 1));
	}
}
