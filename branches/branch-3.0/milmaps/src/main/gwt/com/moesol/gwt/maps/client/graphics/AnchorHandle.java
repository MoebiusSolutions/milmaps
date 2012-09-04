/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

public class AnchorHandle {
	private int m_x;
	private int m_y;
	private int m_size = 8;
	private int m_lineWidth = 2;
	private String m_color = null;
	
	
	public int getX() {
		return m_x;
	}

	public AnchorHandle setX(int x) {
		m_x = x;
		return this;
	}

	public int getY() {
		return m_y;
	}

	public AnchorHandle setY(int y) {
		m_y = y;
		return this;
	}
	
	public void moveByOffset(int x, int y){
		m_x += x;
		m_y += y;
	}
	
	public int getSize() {
		return m_size;
	}
	
	public int getLineWidth() {
		return m_lineWidth;
	}

	public AnchorHandle setLineWidth(int lineWidth) {
		m_lineWidth = lineWidth;
		return this;
	}

	public void setSize(int size) {
		m_size = size;
	}
	
	public AnchorHandle setCenter(int x, int y){
		m_x = x;
		m_y = y;
		return this;
	}
	/*
	public boolean isSelected(int x, int y){
		int hw = m_size/2;
		if (m_x - hw <= x && x <= m_x + hw){
			if (m_y - hw <= y && y <= m_y + hw){
				return true;
			}
		}
		return false;
	}
	*/
	public void setStrokeColor(String color){
		m_color = color;
		return;
	}
	
	public void setStrokeColor(int r, int g, int b, double a){
		String stColor = "rgba(" + r + "," + g + "," + b + "," + a +")";
		if ( m_color == null || !stColor.equalsIgnoreCase(m_color)){
			m_color = stColor;
		}
		return;
	}
	
	public String getStrokeColor(){
		return m_color;
	}
	
	
	public void draw(IContext context){
		if( m_color == null ){
			return;
		}
		if (context != null) {
			context.setStrokeStyle(m_color);
			context.setLineWidth(m_lineWidth);
			context.strokeRect(m_x - m_size/2, m_y - m_size/2, m_size, m_size);
		}
	}
}
