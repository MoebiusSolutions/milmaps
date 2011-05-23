package com.moesol.gwt.maps.shared.tms;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Element;

public class TileSetMetadata implements IsSerializable {
	public static TileSetMetadata parse(final Element item) {
		final TileSetMetadata metadata = new TileSetMetadata();
		metadata.setHref(item.getAttribute("href"));
		metadata.setUnitsPerPixel(Float.parseFloat(item
				.getAttribute("units-per-pixel")));
		metadata.setOrder(Integer.parseInt(item.getAttribute("order")));
		return metadata;
	}

	private String href;
	private int order;
	private float unitsPerPixel;

	public String getHref() {
		return href;
	}

	public int getOrder() {
		return order;
	}

	public float getUnitsPerPixel() {
		return unitsPerPixel;
	}

	public void setHref(final String href) {
		this.href = href;
	}

	public void setOrder(final int order) {
		this.order = order;
	}

	public void setUnitsPerPixel(final float unitsPerPixel) {
		this.unitsPerPixel = unitsPerPixel;
	}
}
