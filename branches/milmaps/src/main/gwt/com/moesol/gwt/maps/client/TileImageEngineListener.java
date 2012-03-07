package com.moesol.gwt.maps.client;

public interface TileImageEngineListener {
	Object createImage(ViewBox vb, TileCoords tileCoords);
	void useImage(ViewBox vb, TileCoords tileCoords, Object image);
	void hideImage(Object image);
	void destroyImage(Object image);
}
