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


import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TileCoordsTest {
	private final URLProvider m_provider = new URLProvider() {
		@Override
		public String encodeComponent(String decodedURLComponent) {
			try {
				return URLEncoder.encode(decodedURLComponent, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	};

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testDoMakeTileURL() {
		TileCoords tc = new TileCoords(2, 3);
		TileCoords.setGlobalURLProvider(m_provider);

		LayerSet ls = new LayerSet();
		ls.setData("data with space");
		ls.setServer("server");
		ls.setSkipLevels(0);
        ls.setAffiliation("f");
        ls.setDimension("g");

		String url = tc.doMakeTileURL(null,ls, 1, 1);
		assertEquals("server?T=data+with+space&L=1&X=2&Y=3", url);

		ls.setAutoRefreshOnTimer(true);
		url = tc.doMakeTileURL(null, ls, 1, 23);
		assertEquals("server?T=data+with+space&L=1&X=2&Y=3&_=23", url);

		ls.setAutoRefreshOnTimer(false);
		ls.setUrlPattern("{server}/tileset/{data}/level/{level}/x/{x}/y/{y}");
		url = tc.doMakeTileURL(null,ls, 1, 1);
		assertEquals("server/tileset/data+with+space/level/1/x/2/y/3", url);

		ls.setAutoRefreshOnTimer(true);
		url = tc.doMakeTileURL(null,ls, 1, 49);
		assertEquals("server/tileset/data+with+space/level/1/x/2/y/3?_=49", url);
	}

	@Test
	public void testDoMakeTileURLReplacements() {
		TileCoords tc = new TileCoords(2, 3);
		TileCoords.setGlobalURLProvider(m_provider);

		LayerSet ls = new LayerSet();
		ls.setData("data with space");
		ls.setServer("server");
		ls.setSkipLevels(0);
		ls.getProperties().put("my.world", "world");
		ls.setUrlPattern("hello {my.world}");
        ls.setAffiliation("f");
        ls.setDimension("g");

		String url = tc.doMakeTileURL(null,ls, 1, 1);
		assertEquals("hello world", url);
	}
}
