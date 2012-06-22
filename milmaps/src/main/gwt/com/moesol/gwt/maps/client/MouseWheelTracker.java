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
