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
	
	private CssColor m_color = CssColor.make("rgba(255, 255, 255,1)");

	public CssColor getColor() {
		return m_color;
	}

	public void setColor(CssColor color) {
		m_color = color;
	}
	
	public int getSize() {
		return m_size;
	}
	
	public int getLineWidth() {
		return m_lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		m_lineWidth = lineWidth;
	}

	public void setSize(int size) {
		m_size = size;
	}
	
	public void setCenter(int x, int y){
		m_x = x;
		m_y = y;
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
	
	public void setStrokeColor(int r, int g, int b, double a){
		String stColor = "rgba(" + r + "," + g + "," + b + "," + a +")";
		m_color = CssColor.make(stColor);
	}
	
	
	public void draw(Context2d context){
		if (context != null) {
			context.beginPath();
			context.setStrokeStyle(m_color);
			context.setLineWidth(m_lineWidth);
			context.rect(m_x - m_size/2, m_y - m_size/2, m_size, m_size);
			context.closePath();
			context.stroke();
		}
	}
}
