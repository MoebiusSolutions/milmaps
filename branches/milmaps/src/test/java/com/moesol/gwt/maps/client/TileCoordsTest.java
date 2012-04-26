package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TileCoordsTest {
	private URLProvider m_provider = new URLProvider() {
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
		
		String url = tc.doMakeTileURL(null,ls, 1, 1);
		assertEquals("hello world", url);
	}
}
