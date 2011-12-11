package com.moesol.gwt.maps.client;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.moesol.gwt.maps.client.DynamicUpdateEngine.TimeProvider;

public class DynamicUpdateEngineTest {

	@Test
	public void testGetDynamicCounter() {
		DynamicUpdateEngine engine = new DynamicUpdateEngine(null, null);
		engine.m_timeProvider = mock(TimeProvider.class);;
		when(engine.m_timeProvider.now()).thenReturn(1L, 2L, 10000L, 19999L, 20000L);
		
		assertEquals(0, engine.getDynamicCounter());
		assertEquals(0, engine.getDynamicCounter());
		assertEquals(10000, engine.getDynamicCounter());
		assertEquals(10000, engine.getDynamicCounter());
		assertEquals(20000, engine.getDynamicCounter());
	}

}
