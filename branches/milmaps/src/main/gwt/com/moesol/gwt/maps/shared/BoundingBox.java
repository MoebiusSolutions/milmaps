package com.moesol.gwt.maps.shared;

import com.moesol.gwt.maps.client.units.Radians;

public class BoundingBox {
	
	// TODO make this class immutable
	private double m_topLat;
	private double m_leftLon;
	private double m_botLat;
	private double m_rightLon;
	
	public static class Builder {
		private double left;
		private double bottom;
		private double right;
		private double top;
		
		public Builder left(double v) {
			left = v;
			return this;
		}
		public Builder bottom(double v) {
			bottom = v;
			return this;
		}
		public Builder right(double v) {
			right = v;
			return this;
		}
		public Builder top(double v) {
			top = v;
			return this;
		}
		public DegreesBuilder degrees() {
			return new DegreesBuilder(this);
		}
		public RadiansBuilder radians() {
			return new RadiansBuilder(this);
		}
	}
	public static class DegreesBuilder extends Builder {
		private final Builder builder;
		
		public DegreesBuilder(Builder b) {
			builder = b;
		}
		public BoundingBox build() {
			return new BoundingBox(builder.top, builder.left, builder.bottom, builder.right);
		}
	}
	public static class RadiansBuilder extends Builder {
		private final Builder builder;
		
		public RadiansBuilder(Builder b) {
			builder = b;
		}
		public BoundingBox build() {
			return new BoundingBox(Radians.asDegrees(builder.top), Radians.asDegrees(builder.left), Radians.asDegrees(builder.bottom), Radians.asDegrees(builder.right));
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}

	// TODO consider just hidding this as package private or
	// Using WMS order from BBOX left,bottom,right,top
	private BoundingBox(double topLat, double leftLon, double botLat, double rightLon) {
		if (topLat < botLat) {
			throw new IllegalArgumentException(
					"Top lat must be greater than bottom lat: top=" + topLat + " bottom=" + botLat);
		}

		m_topLat   = topLat;
		m_leftLon  = wrapLon(leftLon);
		m_botLat   = botLat;
		m_rightLon = wrapLon(rightLon);
		if( ( 0 <= m_leftLon && 0 <= m_rightLon )||
			( m_leftLon <= 0 && m_rightLon <= 0 )){
			if ( m_rightLon < m_leftLon )
				throw new IllegalArgumentException("Switch the longitudes");
		}
	}

	public BoundingBox() {
		m_topLat = Double.POSITIVE_INFINITY;
		m_leftLon = Double.NEGATIVE_INFINITY;
		m_botLat = Double.NEGATIVE_INFINITY;
		m_rightLon = Double.POSITIVE_INFINITY;
	}

	public static BoundingBox valueOf(String boundingBoxString)
			throws Exception {

		String[] bboxTokens = boundingBoxString.split(",");

		if (bboxTokens.length < 4) {
			throw new IllegalArgumentException(
					"Bounding box contained too few arguments");
		} else {
			try {
				double topLat   = Double.parseDouble(bboxTokens[0]);
				double leftLon  = wrapLon(Double.parseDouble(bboxTokens[1]));
				double botLat   = Double.parseDouble(bboxTokens[2]);
				double rightLon = wrapLon(Double.parseDouble(bboxTokens[3]));

				return new BoundingBox(topLat, leftLon, botLat, rightLon);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"not all bbox arguments were numeric", e);
			}
		}
	}
	
	private static double wrapLon(double lng) {
		if (lng > 180.0) {
			while (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			while (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	// TODO
	/*
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
*/
	public double getTopLat() {
		return m_topLat;
	}
	public double top() {
		return getTopLat();
	}

	public double getLeftLon() {
		return m_leftLon;
	}
	public double left() {
		return getLeftLon();
	}

	public double getBotLat() {
		return m_botLat;
	}
	public double bottom() {
		return getBotLat();
	}

	public double getRightLon() {
		return m_rightLon;
	}
	public double right() {
		return getRightLon();
	}

	public double getLatSpan() {
		return (m_topLat - m_botLat);
	}
	
	//This computes the shorter distance
	public double getLonSpan() {
		double dist = m_rightLon - m_leftLon;
		if ( dist < 0.0 ){
			// contains the 180 mark.
			dist *= -1;
			if ( dist > 180.0 ){
				dist = 360 - dist;
			}
		}
		return dist;
	}

	@Override
	public String toString() {
		return m_topLat + "," + m_leftLon + "," + m_botLat + "," + m_rightLon;
	}

	public boolean contains(double lat, double lon) {
		if (lat < m_botLat || lat > m_topLat) {
			return false;
		} else {
			return containsLon(lon);
		}
	}

	boolean containsLon(double lon) {
		lon = wrapLon(lon);
		if ( m_leftLon <= m_rightLon ){
			if( m_leftLon <= lon && lon <= m_rightLon ){
				return true;
			}
		}
		else{ // we have a 180 wrap
			if( ( -180 <= lon && lon <= m_rightLon) ||
			    ( m_leftLon <= lon && lon <= 180) ){
				return true;
			}
		}
		return false;
	}
	
	public void union(double lat, double lng) {
		lng = wrapLon(lng);
		if( Double.isInfinite(m_leftLon) ){
			m_leftLon = lng;
		}
		if( Double.isInfinite(m_rightLon) ){
			m_rightLon = lng;
		}
		if( Double.isInfinite(m_botLat) ){
			m_botLat = lat;
		}
		if( Double.isInfinite(m_topLat) ){
			m_topLat = lat;
		}
		double dist = m_rightLon - m_leftLon;
		if ( dist < 0 ){
			if ( lng >= 0 ){
				m_leftLon = Math.min(m_leftLon, lng);
			}
			else {
				m_rightLon = Math.max(m_rightLon, lng);
			}
		}
		else{
			m_leftLon = Math.min(m_leftLon, lng);
			m_rightLon = Math.max(m_rightLon, lng);
		}
		fixLon();
		m_botLat = Math.min(m_botLat, lat);
		m_topLat = Math.max(m_topLat, lat);
	}
	
	private void fixLon(){
		m_leftLon = wrapLon(m_leftLon);
		m_rightLon = wrapLon(m_rightLon);
		if ( m_leftLon < 0 && m_rightLon > 0 ){
			if (m_rightLon - m_leftLon > 180.0 ){
				double tempLon = m_rightLon;
				m_rightLon = m_leftLon;
				m_leftLon = tempLon;
			}
		}
	}

	public void union(BoundingBox other) {
		union(other.getTopLat(),other.getLeftLon());
		union(other.getBotLat(),other.getRightLon());
	}

	public static BoundingBox wrapPositive(BoundingBox bbox) {
		return new BoundingBox(	bbox.getTopLat(), bbox.getLeftLon(),
								bbox.getBotLat(), bbox.getRightLon() );
	}

	public double getCenterLng() {
		return wrapLon(m_leftLon + (getLonSpan() / 2.0));
	}
	
	public double getCenterLat() {
		return m_botLat + (getLatSpan() / 2.0);
	}
}
