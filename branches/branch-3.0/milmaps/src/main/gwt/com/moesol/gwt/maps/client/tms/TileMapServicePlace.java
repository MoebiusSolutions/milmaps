/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.tms;

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


import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TileMapServicePlace extends Place {

	private String[] layers;
	private double latitude;
	private double longitude;

	public String[] getLayers() {
		return layers;
	}

	public void setLayers(String[] layers) {
		this.layers = layers;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public TileMapServicePlace(String[] layers, double latitude, double longitude) {
		this.layers = layers;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static class Tokenizer implements
			PlaceTokenizer<TileMapServicePlace> {

		@Override
		public TileMapServicePlace getPlace(String token) {
			// String[] mainTokens = token.split(";");

			// String[] positionTokens = mainTokens[0].split(",");

			// String[] layers = mainTokens[1].split(",");
			String[] layers = token.split(",");

			return new TileMapServicePlace(layers, 0, 0);
		}

		@Override
		public String getToken(TileMapServicePlace place) {
			StringBuilder sb = new StringBuilder();
			// sb.append(String.valueOf(place.getLongitude()));
			// sb.append(",");
			// sb.append(String.valueOf(place.getLatitude()));
			// sb.append(",");
			// sb.append(String.valueOf(place.getLevel()));
			// sb.append(";");
			for (String url : place.getLayers()) {
				sb.append(url);
				sb.append(",");
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		}
	}
}
