package com.moesol.gwt.maps.client;

public class DivCoords {
	private final int m_x;
	private final int m_y;

	public static class Builder {
		private int m_x;
		private int m_y;
		
		public DivCoords build() {
			return new DivCoords(m_x, m_y);
		}
		public Builder setX(int x) { m_x = x; return this; }
		public Builder setY(int y) { m_y = y; return this; }
		public int getX() { return m_x; }
		public int getY() { return m_y; }
	}
	public static Builder builder() {
		return new Builder();
	}

	public DivCoords() {
		m_x = m_y = 0;
	}
	
	public DivCoords(int x, int y) {
		m_x = x;
		m_y = y;
	}
	
	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + m_x;
		result = PRIME * result + m_y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ViewCoords)) 
			return false;
		final DivCoords other = (DivCoords) obj;
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
