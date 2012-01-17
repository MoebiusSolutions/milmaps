package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.TileImageEngine.TileInfo;

public class TileImageEngineTest {
	private TileImageEngine m_engine;
	private TileImageEngineListener m_listener = EasyMock.createMock(TileImageEngineListener.class);

	@Before
	public void setUp() throws Exception {
		m_engine = new TileImageEngine(null,m_listener);
	}
	
	@Test
	public void testTileImageEngine() {
		TileCoords[] coords = new TileCoords[] {
				new TileCoords(0, 0),
				new TileCoords(1, 1),
				new TileCoords(2, 2),
		};
		EasyMock.expect(m_listener.createImage(coords[0])).andReturn("1");
		EasyMock.expect(m_listener.createImage(coords[1])).andReturn("2");
		EasyMock.expect(m_listener.createImage(coords[2])).andReturn("3");
		EasyMock.replay(new Object[] { m_listener });
		
		String r;
		r = (String)m_engine.findOrCreateImage(coords[0]);
		assertEquals("1", r);
		r = (String)m_engine.findOrCreateImage(coords[1]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(coords[2]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(coords[0], "1");
		m_listener.useImage(coords[1], "2");
		m_listener.useImage(coords[2], "3");
		EasyMock.replay(new Object[] { m_listener });

		r = (String)m_engine.findOrCreateImage(coords[0]);
		assertEquals("1", r);
		r = (String)m_engine.findOrCreateImage(coords[1]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(coords[2]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		// Scramble coords
		TileCoords tmp = coords[0];
		coords[0] = coords[1];
		coords[1] = coords[2];
		coords[2] = tmp;
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(coords[0], "2");
		m_listener.useImage(coords[1], "3");
		m_listener.useImage(coords[2], "1");
		EasyMock.replay(new Object[] { m_listener });
		
		r = (String)m_engine.findOrCreateImage(coords[0]);
		assertEquals("2", r);
		r = (String)m_engine.findOrCreateImage(coords[1]);
		assertEquals("3", r);
		r = (String)m_engine.findOrCreateImage(coords[2]);
		assertEquals("1", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
		
		// Empty coords
		coords[0] = null;
		coords[2] = null;
		
		EasyMock.reset(new Object[] { m_listener });
		m_listener.useImage(coords[1], "3");
		m_listener.hideImage("2");
		m_listener.hideImage("1");
		EasyMock.replay(new Object[] { m_listener });
		
		r = (String)m_engine.findOrCreateImage(coords[1]);
		assertEquals("3", r);
		m_engine.hideUnplacedImages();
		EasyMock.verify(new Object[] { m_listener });
	}
	
	@Test
	public void testKickOutOfCache() {
		for (int i = 0; i < TileImageEngine.MAX_CACHE_SIZE; i++) {
			TileCoords tileCoords = new TileCoords(i, 0);
			EasyMock.expect(m_listener.createImage(tileCoords)).andReturn("x=" + i);
		}
		for (int i = 1; i <= 5; i++) {
			TileCoords tileCoords = new TileCoords(0, i);
			EasyMock.expect(m_listener.createImage(tileCoords)).andReturn("y=" + i);
		}
		for (int i = 0; i < TileImageEngine.MAX_CACHE_SIZE; i++) {
			m_listener.hideImage("x=" + i);
		}
		for (int i = 0; i < 5; i++) {
			m_listener.destroyImage("x=" + i);
		}
		EasyMock.replay(new Object[] { m_listener });
		
		for (int i = 0; i < TileImageEngine.MAX_CACHE_SIZE; i++) {
			TileCoords tileCoords = new TileCoords(i, 0);
			m_engine.findOrCreateImage(tileCoords);
		}
		m_engine.hideUnplacedImages();
		for (int i = 1; i <= 5; i++) {
			TileCoords tileCoords = new TileCoords(0, i);
			m_engine.findOrCreateImage(tileCoords);
		}
		m_engine.hideUnplacedImages();
		
		EasyMock.verify(new Object[] { m_listener });
	}
	
	@Test
	public void testLookupNegativeLevel() {
		TileInfo info = makeTileInfo(0, 2, 1);
		m_engine.addTileInfo(info);
		
		TileCoords tileCoords = makeTileCoords(-1, 2, 1);
		TileInfo result = m_engine.lookupTileInfo(tileCoords);
		
		assertSame(info, result);
		
		m_engine.removeTileInfo(0);
		result = m_engine.lookupTileInfo(tileCoords);
		assertNull(result);
	}

	@Test
	public void testLookupTwoSameKey() {
		TileInfo info = makeTileInfo(0, 2, 1);
		TileInfo info2 = makeTileInfo(0, 2, 1);
		m_engine.addTileInfo(info);
		m_engine.addTileInfo(info2);
		
		TileCoords tileCoords = makeTileCoords(-1, 2, 1);
		TileInfo result = m_engine.lookupTileInfo(tileCoords);
		assertSame(info, result);
		result.m_placed = true;
		result = m_engine.lookupTileInfo(tileCoords);
		assertSame(info2, result);
		
		m_engine.removeTileInfo(0);
		result = m_engine.lookupTileInfo(tileCoords);
		assertSame(info2, result);
		
		m_engine.removeTileInfo(0);
		result = m_engine.lookupTileInfo(tileCoords);
		assertNull(result);
	}

	private TileCoords makeTileCoords(int level, int y, int x) {
		TileCoords tileCoords = new TileCoords();
		tileCoords.setLevel(level);
		tileCoords.setY(y);
		tileCoords.setX(x);
		return tileCoords;
	}

	private TileInfo makeTileInfo(int level, int y, int x) {
		TileInfo info = new TileInfo();
		info.m_level = level;
		info.m_y = y;
		info.m_x = x;
		return info;
	}

}
