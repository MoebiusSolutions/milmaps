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
