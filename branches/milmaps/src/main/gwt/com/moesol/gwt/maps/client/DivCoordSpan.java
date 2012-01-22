package com.moesol.gwt.maps.client;

import com.moesol.gwt.maps.client.stats.Stats;

public class DivCoordSpan {
	private final int m_left;
	private final int m_right;

	public static class Builder {
		private int m_left;
		private int m_right;
		
		public DivCoordSpan build() {
			return new DivCoordSpan(m_left, m_right);
		}
		public Builder setLeft(int left) { m_left = left; return this; }
		public Builder setRight(int right) { m_right = right; return this; }
		public int getLeft() { return m_left; }
		public int getRight() { return m_right; }
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public DivCoordSpan() {
		Stats.incrementNewViewCoords();
		
		m_left = Integer.MAX_VALUE;
		m_right = Integer.MIN_VALUE;
	}
	
	public DivCoordSpan(int left, int right) {
		Stats.incrementNewViewCoords();
		
		m_left = left;
		m_right = right;
	}
	
	public int getLeft() {
		return m_left;
	}

	public int getRight() {
		return m_right;
	}

	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + m_left;
		result = PRIME * result + m_right;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DivCoordSpan)) 
			return false;
		final DivCoordSpan other = (DivCoordSpan) obj;
		if (m_left != other.m_left)
			return false;
		if (m_right != other.m_right)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[left=" + m_left + ", right=" + m_right + "]";
	}

}
