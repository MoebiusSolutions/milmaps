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


import java.util.Date;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;

public class DynamicUpdateEngine {
	private final MapView m_mapView;
	private final EventBus m_eventBus;
	private Timer m_dynamicTimer = null;
	private int m_dynamicRefreshMillis = 10000;
	private boolean m_dynamicUpdateNeeded = false;
	public interface TimeProvider {
		long now();
	}
	TimeProvider m_timeProvider = new TimeProvider() {
		@Override
		public long now() {
			return new Date().getTime();
		}
	};
	
	public DynamicUpdateEngine(MapView mapView, EventBus eventBus) {
		m_mapView = mapView;
		m_eventBus = eventBus;
	}

	public int getDynamicRefreshMillis() {
		return m_dynamicRefreshMillis;
	}

	public void setDynamicRefreshMillis(int dynamicRefreshMillis) {
		m_dynamicRefreshMillis = dynamicRefreshMillis;
	}
	
	public void initDynamicRefreshTimer() {
		if (m_dynamicTimer != null) {
			return;
		}
		m_dynamicTimer = new Timer() {
			@Override
			public void run() {
				doDynamicRefresh();
			}
		};
		m_dynamicTimer.scheduleRepeating(m_dynamicRefreshMillis);
	}

	private void doDynamicRefresh() {
		if (m_mapView.isMapActionSuspended()) {
			// An animation is in progress so tiles will be updating anyway
			return;
		}
		if (!m_mapView.hasAutoRefreshOnTimerLayers()) {
			return;
		}
		SymbologyRefreshEvent.fire(m_eventBus, m_mapView);
		m_dynamicUpdateNeeded = true;
		m_mapView.updateView();
	}

	public long getDynamicCounter() {
		long tenSecondWindow = m_timeProvider.now() / getDynamicRefreshMillis();
		long milliSeconds = tenSecondWindow * getDynamicRefreshMillis();
		return milliSeconds;
	}

	public boolean isDynamicUpdateNeeded() {
		return m_dynamicUpdateNeeded;
	}
	public void setDynamicUpdateNeeded(boolean dynamicUpdateNeeded) {
		m_dynamicUpdateNeeded = dynamicUpdateNeeded;
	}
	
}
