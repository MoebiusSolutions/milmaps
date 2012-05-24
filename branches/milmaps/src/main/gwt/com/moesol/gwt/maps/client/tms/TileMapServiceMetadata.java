/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class TileMapServiceMetadata {
	public interface RequestCallback {
		void onSuccess(TileMapServiceMetadata metadata);
	}
	
	private String url;
	private TileMapMetadata[] tileMaps;

	public void setTileMaps(TileMapMetadata[] tileMaps) {
		this.tileMaps = tileMaps;
	}

	public TileMapMetadata[] getTileMaps() {
		return tileMaps;
	}

	public static TileMapServiceMetadata parse(String xml) {
		TileMapServiceMetadata metadata = new TileMapServiceMetadata();

		Document doc = XMLParser.parse(xml);
		NodeList tileMapEls = doc.getElementsByTagName("TileMap");
		if (tileMapEls != null && tileMapEls.getLength() != 0) {
			TileMapMetadata[] tileMaps = new TileMapMetadata[tileMapEls
					.getLength()];
			for (int i = 0; i < tileMapEls.getLength(); i++) {
				tileMaps[i] = TileMapMetadata.parse((Element)tileMapEls.item(i));
			}
			metadata.setTileMaps(tileMaps);
		}

		return metadata;
	}
	
	public TileMapMetadata findTileMapMetadata(String tileId) {
		for (int i = 0; i < tileMaps.length; i++) {
			TileMapMetadata tileMapMetadata = tileMaps[i];
			if (tileMapMetadata.getId().equals(tileId)) {
				return tileMapMetadata;
			}
		}
		return null;
	}
	
	public static void getMetadata(String url, final RequestCallback requestCallback) {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "text/xml");
		try {
			requestBuilder.sendRequest(null, new com.google.gwt.http.client.RequestCallback() {
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO: do something more useful here
					Window.alert("error in TileMapServiceMetadata.getMetadata");
				}

				@Override
				public void onResponseReceived(Request request,
						com.google.gwt.http.client.Response response) {
					// TODO: need to check to see if this response was actually a
					// 200
					String responseText = response.getText();
					requestCallback.onSuccess(parse(responseText));
				}
			});
		} catch (RequestException e) {
			// TODO: do something more useful here
			e.printStackTrace();
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
