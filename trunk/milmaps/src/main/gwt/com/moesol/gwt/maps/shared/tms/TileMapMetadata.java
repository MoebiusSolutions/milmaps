package com.moesol.gwt.maps.shared.tms;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Element;

public class TileMapMetadata implements IsSerializable {
	private static final double GEODETIC_LEVEL_ZERO_DEGREES_PER_PIXEL = 0.703125;

	public static TileMapMetadata parse(final Element fullMetadataElement) {
		final TileMapMetadata metadata = new TileMapMetadata();
		final Element tileFormatEl = (Element) fullMetadataElement
				.getElementsByTagName("TileFormat").item(0);
		final int height = Integer
				.parseInt(tileFormatEl.getAttribute("height"));
		metadata.setTilePixelHeight(height);
		metadata.setTilePixelWidth(Integer.parseInt(tileFormatEl
				.getAttribute("width")));

		metadata.setLevelZeroTileDegreeSpan(height
				* GEODETIC_LEVEL_ZERO_DEGREES_PER_PIXEL);

		// final int tileSetElementCount = tileSetEls.getLength();
		// TileSetMetadata[] tileSets = new
		// TileSetMetadata[tileSetElementCount];
		// for (int i = 0; i < tileSetElementCount; i++) {
		// TileSetMetadata tileSet =
		// TileSetMetadata.parse((Element)tileSetEls.item(i));
		// tileSets[i] = tileSet;
		// }
		// metadata.setTileSets(tileSets);
		return metadata;
	}

	public static TileMapMetadata parseFromListElement(final ElementFacade child) {
		final TileMapMetadata metadata = new TileMapMetadata();
		metadata.setTitle(child.getAttribute("title"));
		metadata.setSrs(child.getAttribute("srs"));

		final String url = child.getAttribute("href");
		metadata.setUrl(url);

		final String[] urlPathTokens = url.split("/");
		final String id = urlPathTokens[urlPathTokens.length - 1];
		metadata.setId(id);

		final String[] idTokens = id.split("@");
		metadata.setTileImageFormat(idTokens[idTokens.length - 1]);

		return metadata;
	}

	private String id;
	private double levelZeroTileDegreeSpan;
	private String srs;
	private String tileImageFormat;
	private int tilePixelHeight;
	private int tilePixelWidth;
	private TileSetMetadata[] tileSets;

	private String title;

	private String url;

	private boolean visible;

	public String getId() {
		return id;
	}

	public double getLevelZeroTileDegreeSpan() {
		return levelZeroTileDegreeSpan;
	}

	public String getSrs() {
		return srs;
	}

	public String getTileImageFormat() {
		return tileImageFormat;
	}

	public int getTilePixelHeight() {
		return tilePixelHeight;
	}

	public int getTilePixelWidth() {
		return tilePixelWidth;
	}

	public TileSetMetadata[] getTileSets() {
		return tileSets;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLevelZeroTileDegreeSpan(final double tileDegreeSpan) {
		levelZeroTileDegreeSpan = tileDegreeSpan;
	}

	public void setSrs(final String srs) {
		this.srs = srs;
	}

	public void setTileImageFormat(final String tileImageFormat) {
		this.tileImageFormat = tileImageFormat;
	}

	public void setTilePixelHeight(final int tilePixelHeight) {
		this.tilePixelHeight = tilePixelHeight;
	}

	public void setTilePixelWidth(final int tilePixelWidth) {
		this.tilePixelWidth = tilePixelWidth;
	}

	public void setTileSets(final TileSetMetadata[] tileSets) {
		this.tileSets = tileSets;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
	}
}
