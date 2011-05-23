package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.LoadListener;
import com.google.gwt.user.client.ui.Widget;

public class TileImageLoadListener implements LoadListener {
	private TileImageEngine m_tileEngine;
	
	public void setTileImageEngine(TileImageEngine e) {
		m_tileEngine = e;
	}
	
	@Override
	public void onError(Widget sender) {
		// Allow another zoom
		m_tileEngine.markLoaded(sender);
	}

	@Override
	public void onLoad(Widget sender) {
		m_tileEngine.markLoaded(sender);
	}

}
