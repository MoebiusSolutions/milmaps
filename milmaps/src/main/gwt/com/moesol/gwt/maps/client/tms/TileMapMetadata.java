/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

import com.google.gwt.xml.client.Element;

public class TileMapMetadata {
	private String id;
	private TileSetMetadata[] tileSets;
	private String title;
	private String srs;
	private String url;
	private int tilePixelHeight;
	private int tilePixelWidth;
	private String tileImageFormat;
	private boolean visible;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public int getTilePixelHeight() {
		return tilePixelHeight;
	}

	public void setTilePixelHeight(int tilePixelHeight) {
		this.tilePixelHeight = tilePixelHeight;
	}

	public int getTilePixelWidth() {
		return tilePixelWidth;
	}

	public void setTilePixelWidth(int tilePixelWidth) {
		this.tilePixelWidth = tilePixelWidth;
	}

	public String getTileImageFormat() {
		return tileImageFormat;
	}

	public void setTileImageFormat(String tileImageFormat) {
		this.tileImageFormat = tileImageFormat;
	}

	public void setTileSets(TileSetMetadata[] tileSets) {
		this.tileSets = tileSets;
	}

	public TileSetMetadata[] getTileSets() {
		return tileSets;
	}

	public static TileMapMetadata parse(Element item) {
		TileMapMetadata metadata = new TileMapMetadata();
		metadata.setTitle(item.getAttribute("title"));
		metadata.setSrs(item.getAttribute("srs"));
		
		String url = item.getAttribute("href");
		metadata.setUrl(url);
		
		String[] urlPathTokens = url.split("/");
		String id = urlPathTokens[urlPathTokens.length - 1];
		metadata.setId(id);
		
		String[] idTokens = id.split("@");
		metadata.setTileImageFormat(idTokens[idTokens.length - 1]);
		
//		Element tileFormatEl = (Element)item.getElementsByTagName("TileFormat").item(0);
//		metadata.setTilePixelHeight(Integer.parseInt(tileFormatEl.getAttribute("height")));
//		metadata.setTilePixelWidth(Integer.parseInt(tileFormatEl.getAttribute("width")));
//		
//		NodeList tileSetEls = item.getElementsByTagName("TileSet");
//		
//		TileSetMetadata[] tileSets = new TileSetMetadata[tileSetEls.getLength()];
//		for (int i = 0; i < tileSetEls.getLength(); i++) {
//			TileSetMetadata tileSet = TileSetMetadata.parse((Element)tileSetEls.item(i));
//			tileSets[i] = tileSet;
//		}
//		metadata.setTileSets(tileSets);
		
		return metadata;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
