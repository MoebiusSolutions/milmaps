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


import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class TileImageCacheTest {
	private TileImageCache m_engine;
	private TileImageEngineListener m_listener = EasyMock.createMock(TileImageEngineListener.class);

	@Before
	public void setUp() throws Exception {
		m_engine = new TileImageCache(m_listener);
	}
	
	@Test
	public void testTileImageEngine() {
		TileImageCache.MAX_CACHE_SIZE = 64;
		
		TileCoords[] coords = new TileCoords[] {
				new TileCoords(0, 0),
				new TileCoords(1, 1),
				new TileCoords(2, 2),
		};
		EasyMock.expect(m_listener.createImage(null, coords[0])).andReturn("1");
		EasyMock.expect(m_listener.createImage(null, coords[1])).andReturn("2");
		EasyMock.expect(m_listener.createImage(null, coords[2])).andReturn("3");
		EasyMock.replay(new Object[] { m_listener });
		
		String r;
		r = (String)m_engine.findOrCreateImage(null, coords[0]);
		assertEquals("1", r);
		r = (String)m_engine.findOrCreateImage(null, coords[1]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(null, coords[2]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(null, coords[0], "1");
		m_listener.useImage(null, coords[1], "2");
		m_listener.useImage(null, coords[2], "3");
		EasyMock.replay(new Object[] { m_listener });

		r = (String)m_engine.findOrCreateImage(null, coords[0]);
		assertEquals("1", r);
		r = (String)m_engine.findOrCreateImage(null, coords[1]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(null, coords[2]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		// Scramble coords
		TileCoords tmp = coords[0];
		coords[0] = coords[1];
		coords[1] = coords[2];
		coords[2] = tmp;
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(null, coords[0], "2");
		m_listener.useImage(null, coords[1], "3");
		m_listener.useImage(null, coords[2], "1");
		EasyMock.replay(new Object[] { m_listener });
		
		r = (String)m_engine.findOrCreateImage(null, coords[0]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(null, coords[1]);
		assertEquals("3", r);
		r = (String)m_engine.findOrCreateImage(null, coords[2]);
		assertEquals("1", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		// Empty coords
		coords[0] = null;
		coords[2] = null;
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(null, coords[1], "3");
		m_listener.hideImage("2");
		m_listener.hideImage("1");
		EasyMock.replay(new Object[] { m_listener });
		
		r = (String)m_engine.findOrCreateImage(null, coords[1]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
	}
	
	@Test
	public void testKickOutOfCache() {
		TileImageCache.MAX_CACHE_SIZE = 64;
		
		for (int i = 0; i < TileImageCache.MAX_CACHE_SIZE; i++) {
			TileCoords tileCoords = new TileCoords(i, 0);
			EasyMock.expect(m_listener.createImage(null, tileCoords)).andReturn("x=" + i);
		}
		for (int i = 1; i <= 5; i++) {
			TileCoords tileCoords = new TileCoords(0, i);
			EasyMock.expect(m_listener.createImage(null, tileCoords)).andReturn("y=" + i);
		}
		for (int i = 0; i < TileImageCache.MAX_CACHE_SIZE; i++) {
			m_listener.hideImage("x=" + i);
		}
		for (int i = 0; i < 5; i++) {
			m_listener.destroyImage("x=" + i);
		}
		EasyMock.replay(new Object[] { m_listener });
		
		for (int i = 0; i < TileImageCache.MAX_CACHE_SIZE; i++) {
			TileCoords tileCoords = new TileCoords(i, 0);
			m_engine.findOrCreateImage(null, tileCoords);
		}
		m_engine.hideUnplacedImages();
		for (int i = 1; i <= 5; i++) {
			TileCoords tileCoords = new TileCoords(0, i);
			m_engine.findOrCreateImage(null, tileCoords);
		}
		m_engine.hideUnplacedImages();
		
		EasyMock.verify(new Object[] { m_listener });
	}

}
