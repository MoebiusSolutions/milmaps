package com.moesol.gwt.maps.client;

public class DivCoordSpan {
	private final int m_top;
	private final int m_left;
	private final int m_bottom;
	private final int m_right;

	public static class Builder {
		private int m_left;
		private int m_right;
		private int m_top;
		private int m_bottom;		
		
		public DivCoordSpan build() {
			return new DivCoordSpan( m_top, m_left, m_bottom, m_right);
		}
		public Builder setTop(int top) { m_top = top; return this; }
		public Builder setLeft(int left) { m_left = left; return this; }
		public Builder setBottom(int bottom) { m_bottom = bottom; return this; }
		public Builder setRight(int right) { m_right = right; return this; }
		
		public int getTop() { return m_top; }
		public int getLeft() { return m_left; }
		public int getBottom() { return m_bottom; }
		public int getRight() { return m_right; }
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public DivCoordSpan() {		
		m_top = Integer.MAX_VALUE;
		m_left = Integer.MAX_VALUE;
		
		m_bottom = Integer.MIN_VALUE;
		m_right = Integer.MIN_VALUE;
	}
	
	public DivCoordSpan(int top, int left, int bottom, int right) {		
		m_top = top;
		m_left = left;
		m_bottom = bottom;
		m_right = right;
	}
	
	public boolean isBad(){
		return (m_top == Integer.MAX_VALUE  || m_left == Integer.MAX_VALUE || 
				m_bottom == Integer.MAX_VALUE  || m_right == Integer.MIN_VALUE );
	}
	
	public int getTop() {
		return m_top;
	}
	
	public int getLeft() {
		return m_left;
	}

	public int getBottom() {
		return m_bottom;
	}
	
	public int getRight() {
		return m_right;
	}

	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + m_top;
		result = PRIME * result + m_left;
		result = PRIME * result + m_bottom;
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
		if (m_top != other.m_top)
			return false;
		if (m_left != other.m_left)
			return false;
		if (m_bottom != other.m_bottom)
			return false;
		if (m_right != other.m_right)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[ top = "+ m_top +", left=" + m_left + ", bottom = " + m_bottom +", right=" + m_right + "]";
	}

}
