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


// TODO Immutable
public class DivDimensions {
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	
	private int m_width;
	private int m_height;
	
	public DivDimensions() {
		m_width = WIDTH;
		m_height = HEIGHT;
	}
	
	public DivDimensions(int width, int height) {
		m_width = width;
		m_height = height;
	}
	
	public void copyFrom( DivDimensions dd ){
		m_width  = dd.getWidth();
		m_height = dd.getHeight();
	}
	
	public int getHeight() {
		return m_height;
	}
	public void setHeight(int height) {
		m_height = height;
	}
	public int getWidth() {
		return m_width;
	}
	public void setWidth(int width) {
		m_width = width;
	}

	@Override
	public String toString() {
		return "[w=" + m_width + ",h=" + m_height + "]";
	}
	
}
