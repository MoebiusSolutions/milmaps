package com.moesol.gwt.maps.client;

public interface TileImageEngineListener {
	Object createImage(TileCoords tileCoords);
	void useImage(TileCoords tileCoords, Object image);
	void hideImage(Object image);
	void destroyImage(Object image);
}
