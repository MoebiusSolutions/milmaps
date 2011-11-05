package com.moesol.gwt.maps.shared;

public class BoundingBox {

	private double m_minLon;
	private double m_minLat;
	private double m_maxLon;
	private double m_maxLat;

	public BoundingBox(double minLat, double minLon, double maxLat,
			double maxLon) {
		if (maxLat < minLat) {
			throw new IllegalArgumentException(
					"Max lat must be greater than min lat");
		}
		if (maxLon < minLon) {
			throw new IllegalArgumentException(
					"Max lon must be greater than min lon");
		}

		m_minLon = minLon;
		m_minLat = minLat;
		m_maxLon = maxLon;
		m_maxLat = maxLat;
	}

	public BoundingBox() {
		m_minLon = Double.POSITIVE_INFINITY;
		m_minLat = Double.POSITIVE_INFINITY;
		m_maxLon = Double.NEGATIVE_INFINITY;
		m_maxLat = Double.NEGATIVE_INFINITY;
	}

	public static BoundingBox valueOf(String boundingBoxString)
			throws Exception {

		String[] bboxTokens = boundingBoxString.split(",");

		if (bboxTokens.length < 4) {
			throw new IllegalArgumentException(
					"Bounding box contained too few arguments");
		} else {
			try {
				double minLon = Double.parseDouble(bboxTokens[0]);
				double minLat = Double.parseDouble(bboxTokens[1]);
				double maxLon = Double.parseDouble(bboxTokens[2]);
				double maxLat = Double.parseDouble(bboxTokens[3]);

				return new BoundingBox(minLat, minLon, maxLat, maxLon);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"not all bbox arguments were numeric", e);
			}
		}
	}

	public static BoundingBox union(BoundingBox first, BoundingBox second) {
		double minLon = Math.min(first.getMinLon(), second.getMinLon());
		double minLat = Math.min(first.getMinLat(), second.getMinLat());
		double maxLon = Math.max(first.getMaxLon(), second.getMaxLon());
		double maxLat = Math.max(first.getMaxLat(), second.getMaxLat());

		return new BoundingBox(minLat, minLon, maxLat, maxLon);
	}

	public static BoundingBox intersection(BoundingBox first, BoundingBox second) {
		double minLon = Math.max(first.getMinLon(), second.getMinLon());
		double minLat = Math.max(first.getMinLat(), second.getMinLat());
		double maxLon = Math.min(first.getMaxLon(), second.getMaxLon());
		double maxLat = Math.min(first.getMaxLat(), second.getMaxLat());

		return new BoundingBox(minLat, minLon, maxLat, maxLon);
	}

	public boolean intersects(BoundingBox other) {
		if (other.m_maxLat <= m_minLat)
			return false;
		if (other.m_maxLon <= m_minLon)
			return false;
		if (other.m_minLat >= m_maxLat)
			return false;
		if (other.m_minLon >= m_maxLon)
			return false;
		return true;
	}

	public double getMaxLat() {
		return m_maxLat;
	}

	public double getMaxLon() {
		return m_maxLon;
	}

	public double getMinLat() {
		return m_minLat;
	}

	public double getMinLon() {
		return m_minLon;
	}

	public double getLatSpan() {
		return getMaxLat() - getMinLat();
	}

	public double getLonSpan() {
		return getMaxLon() - getMinLon();
	}

	@Override
	public String toString() {
		return m_minLon + "," + m_minLat + "," + m_maxLon + "," + m_maxLat;
	}

	public boolean contains(double lat, double lon) {
		if (lat < m_minLat || lat > m_maxLat) {
			return false;
		} else {
			return containsLon(lon) || containsLon(lon - 360.0)
					|| containsLon(lon + 360.0);
		}
	}

	boolean containsLon(double lon) {
		return lon >= m_minLon && lon <= m_maxLon;
	}
	
	public void union(double lat, double lng) {
		double centerLng = getCenterLng();
		if (centerLng != Double.NaN && centerLng > 0.0 && lng < 0.0) {
			lng += 360.0;
		}
		
		m_minLon = Math.min(m_minLon, lng);
		m_minLat = Math.min(m_minLat, lat);
		m_maxLon = Math.max(m_maxLon, lng);
		m_maxLat = Math.max(m_maxLat, lat);
	}

	public void union(BoundingBox other) {
		double centerLng = getCenterLng();
		double otherCenterLng = other.getCenterLng();

		if (centerLng != Double.NaN && centerLng > 0.0 && otherCenterLng < 0.0) {
			other = wrapPositive(other);
		}

		m_minLon = Math.min(m_minLon, other.getMinLon());
		m_minLat = Math.min(m_minLat, other.getMinLat());
		m_maxLon = Math.max(m_maxLon, other.getMaxLon());
		m_maxLat = Math.max(m_maxLat, other.getMaxLat());
	}

	public static BoundingBox wrapPositive(BoundingBox bbox) {
		return new BoundingBox(bbox.getMinLat(), bbox.getMinLon() + 360.0,
				bbox.getMaxLat(), bbox.getMaxLon() + 360.0);
	}

	public double getCenterLng() {
		return m_minLon + (getLonSpan() / 2.0);
	}
	
	public double getCenterLat() {
		return m_minLat + (getLatSpan() / 2.0);
	}
}
