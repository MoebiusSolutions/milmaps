/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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

public class AnchorHandleTest {
	protected AnchorHandle m_handle = new AnchorHandle();
	
	@Test
	public void setGetTest(){
		m_handle.setX(100);
		assertEquals(100,m_handle.getX());
		
		m_handle.setY(100);
		assertEquals(100,m_handle.getY());
		
		m_handle.setCenter(50, 50);
		assertEquals(50,m_handle.getX());
		assertEquals(50,m_handle.getY());
		
		m_handle.setCenter(50, 50);
		m_handle.moveByOffset(-20, 20);
		assertEquals(30,m_handle.getX());
		assertEquals(70,m_handle.getY());
		
		m_handle.setSize(20);
		assertEquals(20,m_handle.getSize());
		
		m_handle.setLineWidth(2);
		assertEquals(2,m_handle.getLineWidth());
		
		m_handle.setStrokeColor(200, 100, 50, 1);
		String color = m_handle.getStrokeColor();
		assertEquals("rgba(200,100,50,1.0)", color);
	}
}
