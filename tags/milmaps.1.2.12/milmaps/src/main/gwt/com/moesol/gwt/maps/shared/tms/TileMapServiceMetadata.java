package com.moesol.gwt.maps.shared.tms;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.moesol.gwt.maps.shared.tms.ElementFacade.NodeListFacade;

/**
 * Corresponds (somewhat loosely) to the TileMapService element of a 
 * Tile Map Service metadata document.
 */
public class TileMapServiceMetadata implements IsSerializable {
	public static TileMapServiceMetadata parse(ElementFacade element) {
		final TileMapServiceMetadata metadata = new TileMapServiceMetadata();
		
		NodeListFacade result = element.getElementsByTagName("TileMap");
		final TileMapMetadata[] tileMaps = new TileMapMetadata[result.getLength()];
		
		for (int i = 0; i < result.getLength(); i++) {
			tileMaps[i] = TileMapMetadata.parseFromListElement(result.item(i));
		}
		metadata.setTileMaps(tileMaps);
		return metadata;
	}

	private TileMapMetadata[] tileMaps;

	private String url;

	public TileMapMetadata findTileMapMetadata(final String tileId) {
		for (final TileMapMetadata tileMapMetadata : tileMaps) {
			if (tileMapMetadata.getId().equals(tileId)) {
				return tileMapMetadata;
			}
		}
		return null;
	}

	public TileMapMetadata[] getTileMaps() {
		return tileMaps;
	}

	public String getUrl() {
		return url;
	}

	public void setTileMaps(final TileMapMetadata[] tileMaps) {
		this.tileMaps = tileMaps;
	}

	public void setUrl(final String url) {
		this.url = url;
	}
}
