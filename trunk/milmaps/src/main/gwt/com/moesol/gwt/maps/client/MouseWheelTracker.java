package com.moesol.gwt.maps.client;

public class MouseWheelTracker {

	int m_x = 0;
	int m_y = 0;
	
	
	public void update(int x, int y) {
		m_x = x;
		m_y = y;
	}
	
	public ViewCoords getViewCoordinates() {
		ViewCoords result = new ViewCoords();
		result.setX(m_x);
		result.setY(m_y);
		
		return result;
	}
}
