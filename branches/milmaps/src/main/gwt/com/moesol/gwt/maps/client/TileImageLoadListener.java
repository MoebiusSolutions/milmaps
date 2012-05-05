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
	}
	@Override
	public void onLoad(LoadEvent event) {
		Widget image = (Widget) event.getSource();
		m_tileEngine.markLoaded(image);
		// Allow background css when image is loaded.
		image.addStyleDependentName("loaded");
	}

}
