package com.moesol.gwt.maps.client.tms;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	private int level;

	public TileMapServicePlace(String[] layers, double latitude,
			double longitude, int level) {
		super();
		this.layers = layers;
		this.latitude = latitude;
		this.longitude = longitude;
		this.level = level;
	}

	public static class Tokenizer implements
			PlaceTokenizer<TileMapServicePlace> {

		@Override
		public TileMapServicePlace getPlace(String token) {
			// String[] mainTokens = token.split(";");

			// String[] positionTokens = mainTokens[0].split(",");

			// String[] layers = mainTokens[1].split(",");
			String[] layers = token.split(",");

			return new TileMapServicePlace(layers, 0, 0, 0);
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
