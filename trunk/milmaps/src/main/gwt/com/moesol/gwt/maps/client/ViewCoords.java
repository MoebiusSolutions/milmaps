package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.stats.Stats;

public class ViewCoords {
	private final int m_x;
	private final int m_y;

	public static class Builder {
		private int m_x;
		private int m_y;
		
		public ViewCoords build() {
			return new ViewCoords(m_x, m_y);
		}
		public Builder setX(int x) { m_x = x; return this; }
		public Builder setY(int y) { m_y = y; return this; }
		public int getX() { return m_x; }
		public int getY() { return m_y; }
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public ViewCoords() {
		Stats.incrementNewViewCoords();
		
		m_x = m_y = 0;
	}
	
	public ViewCoords(int x, int y) {
		Stats.incrementNewViewCoords();
		
		m_x = x;
		m_y = y;
	}
	
	public int getX() {
		return m_x;
	}

	public int getY() {
		return m_y;
	}

	/**
	 * @param offset
	 * @return new view coords with x and y translated by offset.
	 */
	public ViewCoords translate(ViewCoords offset) {
		return new ViewCoords(m_x + offset.m_x, m_y + offset.m_y);
	}
	/**
	 * @param offsetX
	 * @param offsetY
	 * @return new view coords with x translated by offsetX and y by offsetY.
	 */
	public ViewCoords translate(int offsetX, int offsetY) {
		return new ViewCoords(m_x + offsetX, m_y + offsetY);
	}

	
	public ViewCoords scale(int s) {
		return new ViewCoords(s * m_x, s * m_y);
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
		final ViewCoords other = (ViewCoords) obj;
		if (m_x != other.m_x)
			return false;
		if (m_y != other.m_y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[x=" + m_x + ", y=" + m_y + "]";
	}

}
