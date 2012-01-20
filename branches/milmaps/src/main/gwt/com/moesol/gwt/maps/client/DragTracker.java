package com.moesol.gwt.maps.client;

public class DragTracker {
	private final ViewCoords m_origPort = new ViewCoords();
	private final ViewCoords m_deltaPort = new ViewCoords();
	private final ViewCoords m_vwCoords = new ViewCoords();
	private final WorldCoords m_origWorld = new WorldCoords();
	private final WorldCoords m_returnedWorld = new WorldCoords();
	private boolean m_sameAsLast = false;
	
	public DragTracker(int x, int y, WorldCoords center) {
		m_origPort.setX(x);
		m_origPort.setY(y);
		m_origWorld.setX(center.getX());
		m_origWorld.setY(center.getY());
	}
	
	public DragTracker(int x, int y, ViewCoords center) {
		set( x, y, center );
	}
	
	public void set(int x, int y, ViewCoords center){
		m_origPort.setX(x);
		m_origPort.setY(y);
		m_origWorld.setX(center.getX());
		m_origWorld.setY(center.getY());
	}
	
	public WorldCoords update(int x, int y) {
		int newDeltaPortX = x - m_origPort.getX();
		int newDeltaPortY = m_origPort.getY() - y;  // Flip y axis
		computeSameAsLast(newDeltaPortX, newDeltaPortY);
		m_deltaPort.setX(newDeltaPortX);
		m_deltaPort.setY(newDeltaPortY);
		m_returnedWorld.setX(m_origWorld.getX() - m_deltaPort.getX());
		m_returnedWorld.setY(m_origWorld.getY() - m_deltaPort.getY());
		return m_returnedWorld;
	}
	
	public ViewCoords getDelta(){
		return m_deltaPort;
	}
	
	public ViewCoords updateView(int x, int y) {
		int newDeltaPortX = x - m_origPort.getX();
		int newDeltaPortY = m_origPort.getY() - y;  // Flip y axis
		computeSameAsLast(newDeltaPortX, newDeltaPortY);
		m_deltaPort.setX(newDeltaPortX);
		m_deltaPort.setY(newDeltaPortY);
		m_vwCoords.setX(m_origWorld.getX() - m_deltaPort.getX());
		m_vwCoords.setY(m_origWorld.getY() - m_deltaPort.getY());
		return m_vwCoords;
	}

	private void computeSameAsLast(int newDeltaPortX, int newDeltaPortY) {
		if (newDeltaPortX != m_deltaPort.getX()) {
			m_sameAsLast = false;
			return;
		}
		if (newDeltaPortY != m_deltaPort.getY()) {
			m_sameAsLast = false;
			return;
		}
		m_sameAsLast = true;
	}

	public boolean isSameAsLast() {
		return m_sameAsLast;
	}
}
