/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

public class WorldDimension {
	private int m_width;
	private int m_height;
	
	public WorldDimension() {
		m_width = m_height = 0;
	}
	
	public WorldDimension(int width, int height) {
		m_width = width;
		m_height = height;
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
		return "[w=" + m_width + ", h=" + m_height + "]";
	}
	
}
