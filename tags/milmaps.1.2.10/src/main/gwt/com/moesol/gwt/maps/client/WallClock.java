package com.moesol.gwt.maps.client;

public class WallClock {
	private long m_start;
	private long m_stop;
	
	public void start() {
		m_start = System.currentTimeMillis();
	}
	public void stop() {
		m_stop = System.currentTimeMillis();
	}
	@Override
	public String toString() {
		long dur = m_stop - m_start;
		return "milli=" + dur;
	}
}
