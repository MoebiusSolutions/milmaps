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


public class DragTracker {
	private ViewCoords m_origPort = new ViewCoords();
	private ViewCoords m_deltaPort = new ViewCoords();
	private WorldCoords m_origWorld = new WorldCoords();
	private boolean m_sameAsLast = false;
	private boolean m_mouseDownMoved = false;
	
	public DragTracker(int x, int y, WorldCoords center) {
		m_origPort = new ViewCoords(x, y);
		m_origWorld = center;
	}
	
	public void set(int x, int y, ViewCoords center){
		m_origPort = new ViewCoords(x, y);
		m_origWorld = new WorldCoords(center.getX(), center.getY());
		m_mouseDownMoved = false;
	}
	
	public WorldCoords update(int x, int y) {
		int newDeltaPortX = x - m_origPort.getX();
		int newDeltaPortY = m_origPort.getY() - y;  // Flip y axis
		computeSameAsLast(newDeltaPortX, newDeltaPortY);
		m_deltaPort = new ViewCoords(newDeltaPortX, newDeltaPortY);
		
		return WorldCoords.builder()
				.setX(m_origWorld.getX() - m_deltaPort.getX())
				.setY(m_origWorld.getY() - m_deltaPort.getY())
				.build();
	}
	
	public ViewCoords getDelta(){
		return m_deltaPort;
	}

	private void computeSameAsLast(int newDeltaPortX, int newDeltaPortY) {
		if (newDeltaPortX != m_deltaPort.getX()) {
			m_sameAsLast = false;
			m_mouseDownMoved = true;
			return;
		}
		if (newDeltaPortY != m_deltaPort.getY()) {
			m_sameAsLast = false;
			m_mouseDownMoved = true;
			return;
		}
		m_sameAsLast = true;
	}
	
	public boolean mouseDownMoved(){
		return m_mouseDownMoved;
	}

	public boolean isSameAsLast() {
		return m_sameAsLast;
	}
}
