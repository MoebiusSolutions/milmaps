package com.moesol.gwt.maps.client;

public class ViewDimension {
	private int m_width;
	private int m_height;
	
	public ViewDimension() {
		m_width = m_height = 0;
	}
	
	public ViewDimension(int width, int height) {
		m_width = width;
		m_height = height;
	}
	
	public void copyFrom( ViewDimension vd ){
		m_width  = vd.getWidth();
		m_height = vd.getHeight();
	}
	
	public int getHeight() {
		return m_height;
	}
	public void setHeight(int height) {
		m_height = height;
	}
	public int getWidth() {
		return m_width;
	}
	public void setWidth(int width) {
		m_width = width;
	}

	@Override
	public String toString() {
		return "[w=" + m_width + ",h=" + m_height + "]";
	}
	
}
