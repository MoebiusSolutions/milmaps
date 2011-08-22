package com.moesol.gwt.maps.client;

public class DoubleClickTracker {

	private static final long s_dblClickWindowInMilis = 250;
	private static final long s_doubleClickMovementAllowance = 2;
	
	private int m_clickCount = 0; // start state
	private long m_timeOfFirstClick = 0;
	private long m_now = 0;
	private int m_priorX = 0;
	private int m_priorY = 0;
	private TimeSource m_timeSource = new TimeSource() {
		@Override
		public long currentTimeMillis() {
			return System.currentTimeMillis();
		}
	};
	
	interface TimeSource {
		public long currentTimeMillis();
	}
	
	public boolean onMouseDown(int x, int y) {
		m_now = m_timeSource.currentTimeMillis();
		
		switch (m_clickCount) {
		case 0:
			return state0(x, y);
			
		case 1:
			return state1(x, y);
			
		default:
			return stateOther(x, y);
		}
	}
	
	private boolean state0(int x, int y) {
		m_priorX = x;
		m_priorY = y;
		m_timeOfFirstClick = m_now;
		m_clickCount = 1;
		return false;
	}
	
	private boolean state1(int x, int y) {
		if (!clickInRange(x, y)) {
			return state0(x, y);
		}
		if (!timeInRange()) {
			return state0(x, y);
		}
		m_clickCount++;
		return true;
	}
	
	private boolean stateOther(int x, int y) {
		if (!clickInRange(x, y)) {
			return state0(x, y);
		}
		if (!timeInRange()) {
			return state0(x, y);
		}
		m_clickCount++;
		return false; // multi-click, but not double click
	}
		
	private boolean clickInRange(int x, int y) {
		int dx = Math.abs(m_priorX - x);
		int dy = Math.abs(m_priorY - y);
		return dx <= s_doubleClickMovementAllowance && dy <= s_doubleClickMovementAllowance;
	}
	
	private boolean timeInRange() {
		long dt =  m_now - m_timeOfFirstClick;
		return dt < s_dblClickWindowInMilis;
	}

	public void setTimeSource(TimeSource timeSource) {
		m_timeSource = timeSource;
	}
	
	public int getX() {
		return m_priorX;
	}
	
	public int getY() {
		return m_priorY;
	}
}
