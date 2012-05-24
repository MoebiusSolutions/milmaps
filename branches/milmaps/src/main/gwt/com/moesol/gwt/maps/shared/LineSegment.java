/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.shared;

import com.moesol.gwt.maps.client.WorldCoords;

public class LineSegment {
	WorldCoords m_p;
	WorldCoords m_q;
	
	public static class Builder {
		private WorldCoords m_p;
		private WorldCoords m_q;
		
		public LineSegment build() {
			return new LineSegment(m_p, m_q);
		}
		public Builder setP(WorldCoords p) { m_p = p; return this; }
		public Builder setQ(WorldCoords q) { m_q = q; return this; }
		public WorldCoords getP() { return m_p; }
		public WorldCoords getQ() { return m_q; }
		@Override
		public String toString() {
			return "Builder [m_p = (" + m_p.getX() +"," + m_p.getY()+")"  +
			              ", m_q = (" + m_q.getX() +"," + m_q.getY()+")"  + "]";
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public LineSegment() {
		m_p = null;
		m_q = null;
	}	

	public LineSegment(WorldCoords p, WorldCoords q) {
		m_p = p;
		m_q = q;
	}
	
	public void setPointP(int x, int y) {
		m_p = new WorldCoords(x,y);
	}
	
	public void setPointQ(int x, int y) {
		m_q = new WorldCoords(x,y);
	}
	
	public void setSegment(int x1, int y1, int x2, int y2) {
		m_p = new WorldCoords(x1,y1);
		m_q = new WorldCoords(x2,y2);
	}
	
	public void setPointP(WorldCoords p) {
		m_p = p;
	}
	
	public void setPointQ(WorldCoords q) {
		m_q = q;
	}
	
	public void setSegment(WorldCoords p, WorldCoords q) {
		m_p = p;
		m_q = q;
	}
	
	public double getRun(){
		return (m_q.getX() - m_q.getX());
	}
	
	public double getRise(){
		return (m_q.getY() - m_p.getY());
	}
	
	public double getSlope(){
		double run  =getRun();
		if ( run != 0.0 ) {
			double rise = getRise();
			return (rise/run);
		}
		return Double.MAX_VALUE;
	}
	
	public WorldCoords getPointOnLineForX(int x) {
		double m = getSlope();
		if ( m == Double.MAX_VALUE) {
			return null;
		}
		int y = (int)(m*(x - m_p.getX())+m_p.getY());
		return new WorldCoords(x,y);
	}
	
	public WorldCoords getPointOnLineForY(int y) {
		double m = getSlope();
		if ( m == Double.MAX_VALUE) {
			return new WorldCoords(m_p.getX(),y) ;
		}
		int x = (int)((y - m_p.getY())/m + m_p.getX());
		return new WorldCoords(x,y);
	}
	
	public WorldCoords getPointRpercentFromP(double R) {
		double dX = getRun();
		if ( dX == 0.0 ){
			double dY = getRise();
			return getPointOnLineForY((int)(m_p.getY() + R*dY + 0.5));
		}
		return getPointOnLineForX((int)(m_p.getX() + R*dX + 0.5));
	}
}
