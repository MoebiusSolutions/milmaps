/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class AnchorHandle {
	private int m_x;
	private int m_y;
	private int m_size = 8;
	private int m_lineWidth = 2;
	private String m_strColor = null;
	
	private CssColor m_color = null;

	public CssColor getColor() {
		return m_color;
	}

	public AnchorHandle setColor(CssColor color) {
		m_color = color;
		return this;
	}
	
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

	public AnchorHandle setSize(int size) {
		m_size = size;
		return this;
	}
	
	public AnchorHandle setCenter(int x, int y){
		m_x = x;
		m_y = y;
		return this;
	}
	
	public boolean isSelected(int x, int y){
		int hw = m_size/2;
		if (m_x - hw <= x && x <= m_x + hw){
			if (m_y - hw <= y && y <= m_y + hw){
				return true;
			}
		}
		return false;
	}
	
	public void setStrokeColor(String color){
		m_color = CssColor.make(color);
		return;
	}
	
	public void setStrokeColor(int r, int g, int b, double a){
		String stColor = "rgba(" + r + "," + g + "," + b + "," + a +")";
		if ( m_strColor == null || !stColor.equalsIgnoreCase(m_strColor)){
			m_color = CssColor.make(stColor);
		}
		return;
	}
	
	
	public void draw(Context2d context){
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
