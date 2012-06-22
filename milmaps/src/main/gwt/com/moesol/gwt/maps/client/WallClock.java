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
	public long getDuration() {
		return m_stop - m_start;
	}
	public double computeOperationsPersecond(long nops) {
		return (nops * 1000.0) / getDuration();
	}

	@Override
	public String toString() {
		return "milli=" + getDuration();
	}
}
