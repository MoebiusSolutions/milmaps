/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared;


public class Tile {
	private static final double START_DLNG = 180.0;
	private static final double START_DLAT = 180.0;
	
	private final int m_level;
	private final int m_x;
	private final int m_y;
	private final double m_lng;
	private final double m_lat;
	private final double m_dlng;

	double getLng() {
		return m_lng;
	}

	double getLat() {
		return m_lat;
	}

	double getDlng() {
		return m_dlng;
	}

	double getDlat() {
		return m_dlat;
	}

	double getTileCenterLng() {
		return m_tileCenterLng;
	}

	double getDegreesPerPixel() {
		return m_degreesPerPixel;
	}

	private final double m_dlat;
	private final int m_tilePixelWidth;
	private final int m_tilePixelHeight;
	private final BoundingBox m_bbox;
	private final double m_tileCenterLng;
	private final double m_degreesPerPixel;

	private Tile(int level, int x, int y, int tilePixelWidth, int tilePixelHeight) {
		m_level = level;
		m_x = x;
		m_y = y;
		m_tilePixelWidth = tilePixelWidth;
		m_tilePixelHeight = tilePixelHeight;
		
		m_dlng = getTileLongitudeSpan(m_level);
		m_dlat = getTileLatitudeSpan(m_level);
		m_lng = -180.0 + m_x * m_dlng;
		m_lat = -90.0 + m_y * m_dlat;

		m_bbox = BoundingBox.builder()
				.top(m_lat + m_dlat).left(m_lng)
				.bottom(m_lat).right(m_lng + m_dlng).degrees().build();
		m_tileCenterLng = m_lng + (m_dlng / 2.0);
		m_degreesPerPixel = m_dlng / m_tilePixelWidth;
	}

	static double getTileLongitudeSpan(int level) {
		return getDegreeSpan(START_DLNG, level);
	}
	
	static double getTileLatitudeSpan(int level) {
		return getDegreeSpan(START_DLAT, level);
	}
	
	static double getDegreeSpan(double levelZeroDegrees, int level) {
		return levelZeroDegrees / Math.pow(2.0, level);
	}

	public static class Builder {
		private final int m_level;
		private final int m_x;
		private final int m_y;
		private int m_tilePixelWidth = 512;
		private int m_tilePixelHeight = 512;

		public Builder(int level, int x, int y) {
			m_level = level;
			m_x = x;
			m_y = y;
		}

		public Builder pixelWidth(int tilePixelWidth) {
			m_tilePixelWidth = tilePixelWidth;
			return this;
		}

		public Builder pixelHeight(int tilePixelHeight) {
			m_tilePixelHeight = tilePixelHeight;
			return this;
		}

		public Tile build() {
			return new Tile(m_level, m_x, m_y, m_tilePixelWidth,
					m_tilePixelHeight);
		}
	}

	public double pixelSpanToLongitudeSpan(int width) {
		return width * m_degreesPerPixel;
	}

	public double pixelSpanToLatitudeSpan(int height) {
		return height * m_degreesPerPixel;
	}

	public double longitudeToPixelSpace(double minLng) {
		double x = (minLng - m_lng) / m_degreesPerPixel;
		return x;
	}

	public double latitudeToPixelSpace(double minLat) {
		double y = m_tilePixelHeight - ((minLat - m_lat) / m_degreesPerPixel);
		return y;
	}

	public double getCenterLng() {
		return m_tileCenterLng;
	}

	public int getLevel() {
		return m_level;
	}
	
	public int getX() {
		return m_x;
	}
	
	public int getY() {
		return m_y;
	}

	public int getPixelWidth() {
		return m_tilePixelWidth;
	}
	
	public int getPixelHeight() {
		return m_tilePixelHeight;
	}

	@Override
	public String toString() {
		return "Tile [m_level=" + m_level + ", m_x=" + m_x + ", m_y=" + m_y
				+ ", m_lng=" + m_lng + ", m_lat=" + m_lat + ", m_dlng="
				+ m_dlng + ", m_dlat=" + m_dlat + ", m_tilePixelWidth="
				+ m_tilePixelWidth + ", m_tilePixelHeight=" + m_tilePixelHeight
				+ ", m_bbox=" + m_bbox + ", m_tileCenterLng=" + m_tileCenterLng
				+ ", m_degreesPerPixel=" + m_degreesPerPixel + "]";
	}
}
