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
