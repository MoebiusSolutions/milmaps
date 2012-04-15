package com.moesol.gwt.maps.client;

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
