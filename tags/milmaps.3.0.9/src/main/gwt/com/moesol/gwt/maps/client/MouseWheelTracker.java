/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

public class MouseWheelTracker {

	private ViewCoords m_vc = new ViewCoords();
	
	public void update(int x, int y) {
		m_vc = new ViewCoords(x, y);
	}
	
	public ViewCoords getViewCoordinates() {
		return m_vc;
	}
	
}
