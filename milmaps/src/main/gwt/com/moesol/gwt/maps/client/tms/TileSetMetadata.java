/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

import com.google.gwt.xml.client.Element;

public class TileSetMetadata {
	private String href;
	private float unitsPerPixel;
	private int order;
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public float getUnitsPerPixel() {
		return unitsPerPixel;
	}
	public void setUnitsPerPixel(float unitsPerPixel) {
		this.unitsPerPixel = unitsPerPixel;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public static TileSetMetadata parse(Element item) {
		TileSetMetadata metadata = new TileSetMetadata();
		metadata.setHref(item.getAttribute("href"));
		metadata.setUnitsPerPixel(Float.parseFloat(item.getAttribute("units-per-pixel")));
		metadata.setOrder(Integer.parseInt(item.getAttribute("order")));
		return metadata;
	}
}
