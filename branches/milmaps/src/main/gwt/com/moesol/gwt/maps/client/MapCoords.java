package com.moesol.gwt.maps.client;


public class MapCoords {
	private final double m_x;
	private final double m_y;

	public static class Builder {
		private double m_x;
		private double m_y;
		
		public MapCoords build() {
			return new MapCoords(m_x, m_y);
		}
		public Builder setX(double x) { m_x = x; return this; }
		public Builder setY(double y) { m_y = y; return this; }
		public double getX() { return m_x; }
		public double getY() { return m_y; }
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public MapCoords() {
		m_x = m_y = 0;
	}
	public MapCoords(double x, double y) {
		m_x = x;
		m_y = y;
	}

	public MapCoords(WorldCoords wc) {
		this(wc.getX(), wc.getY());
	}

	public double getX() {
		return m_x;
	}

	public double getY() {
		return m_y;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		double result = 1;
		result = PRIME * result + m_x;
		result = PRIME * result + m_y;
		return (int)(result + 0.5);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MapCoords)) 
			return false;
		final MapCoords other = (MapCoords) obj;
		if (m_x != other.m_x)
			return false;
		if (m_y != other.m_y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + m_x + "," + m_y + "]";
	}

}
