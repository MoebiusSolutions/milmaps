/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.moesol.gwt.maps.client.graphics.CanvasTool;

/**
 * Puts the MapView into a panel so that the map will fill the
 * parent. Parent widget must be a LayoutPanel.
 */
public class MapPanel extends Composite implements RequiresResize {
	private final MapView m_mapView;
	private int lastWidth = 0;
	private int lastHeight = 0;
	private CanvasTool m_canvas;
	
	public MapPanel(MapView mapView) {
		m_mapView = mapView;
		m_canvas = mapView.getDivManager().getCanvasTool();
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
		m_canvas.setSize(width, height);
		lastWidth = width;
		lastHeight = height;
	}

	public MapView getMapView() {
		return m_mapView;
	}
	
	public void setCanvas(CanvasTool ct){
		m_canvas = ct;
	}

}
