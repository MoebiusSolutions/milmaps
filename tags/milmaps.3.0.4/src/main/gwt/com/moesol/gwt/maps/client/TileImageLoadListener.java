/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Widget;

public class TileImageLoadListener implements LoadHandler, ErrorHandler {
	private TileImageCache m_tileEngine;
	
	public void setTileImageEngine(TileImageCache e) {
		m_tileEngine = e;
	}
	
	@Override
	public void onError(ErrorEvent event) {
		// Allow another zoom
		Widget image = (Widget) event.getSource();
		m_tileEngine.markLoaded(image);
		// Allow different css when image is error.
		image.addStyleDependentName("error");
	}
	@Override
	public void onLoad(LoadEvent event) {
		Widget image = (Widget) event.getSource();
		m_tileEngine.markLoaded(image);
		// Allow background css when image is loaded.
		image.addStyleDependentName("loaded");
	}

}
