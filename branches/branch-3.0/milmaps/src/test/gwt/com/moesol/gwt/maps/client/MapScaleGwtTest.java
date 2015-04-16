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


import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;


public class MapScaleGwtTest extends GWTTestCase {

	@Test
	public void testToString() {
		assertEquals("1:1.0M", MapScale.parse("1:1M").toString());
		assertEquals("1:50.0K", MapScale.parse("1:50K").toString());
	}
	
	@Override
	public String getModuleName() {
		return "com.moesol.gwt.maps.Maps";
	}

}
