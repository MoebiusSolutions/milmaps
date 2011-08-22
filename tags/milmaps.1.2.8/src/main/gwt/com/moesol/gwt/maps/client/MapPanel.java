package com.moesol.gwt.maps.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Puts the MapView into a panel so that the map will fill the
 * parent. Parent widget must be a LayoutPanel.
 */
public class MapPanel extends Composite implements RequiresResize {
	private final MapView m_mapView;
	private int lastWidth = 0;
	private int lastHeight = 0;
	
	public MapPanel() {
		this(new MapView());
	}
	
	public MapPanel(MapView mapView) {
		m_mapView = mapView;
		initWidget(m_mapView);
		
		// IE7 blows, just periodically sync...
		Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
			@Override
			public boolean execute() {
				synchronizeSize();
				return true;
			}
		}, 1000);
	}
	
	@Override
	public void onResize() {
		synchronizeSize();
	}

	private void synchronizeSize() {
		Element el = getElement();
		if (el == null) {
			return; // we'll be back
		}
		el = el.getParentElement();
		if (el == null) {
			return; // we'll be back
		}
		int width = el.getClientWidth();
		int height = el.getClientHeight();
		
		if (width == lastWidth && height == lastHeight) {
			return;
		}
		
		m_mapView.resizeMap(width, height);
		m_mapView.updateView();
		
		lastWidth = width;
		lastHeight = height;
	}

	public MapView getMapView() {
		return m_mapView;
	}

}
