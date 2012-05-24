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

public class MouseWheelTracker {

	private ViewCoords m_vc = new ViewCoords();
	
	public void update(int x, int y) {
		m_vc = new ViewCoords(x, y);
	}
	
	public ViewCoords getViewCoordinates() {
		return m_vc;
	}
	
}
