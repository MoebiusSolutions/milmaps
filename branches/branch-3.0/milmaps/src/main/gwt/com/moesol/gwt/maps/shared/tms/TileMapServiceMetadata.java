/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared.tms;

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
