package com.moesol.gwt.maps.client;

public class PixelXY {
	public int m_x;
	public int m_y;
	
	public PixelXY() {
		super();
	}
	
	public PixelXY(int x, int y) {
		m_x = x;
		m_y = y;
	}
	
	public void copy( PixelXY p ){
		m_x = p.m_x;
		m_y = p.m_y;
	}
	
	public PixelXY clone(){
		PixelXY c = new PixelXY();
		c.copy(this);
		return c;
	}
}
