package com.moesol.gwt.maps.client;

public class MapCoords {
	private double m_x;
	private double m_y;

	public MapCoords() {
		m_x = m_y = 0;
	}
	
	public MapCoords(double x, double y) {
		m_x = x;
		m_y = y;
	}

	public MapCoords(WorldCoords v) {
		copyFrom(v);
	}

	public double getX() {
		return m_x;
	}

	public void setX(double x) {
		m_x = x;
	}

	public double getY() {
		return m_y;
	}

	public void setY(double y) {
		m_y = y;
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

	public void copyFrom(MapCoords wc) {
		m_x = wc.getX();
		m_y = wc.getY();
	}
	
	public void copyFrom(WorldCoords wc) {
		m_x = wc.getX();
		m_y = wc.getY();
	}
}
