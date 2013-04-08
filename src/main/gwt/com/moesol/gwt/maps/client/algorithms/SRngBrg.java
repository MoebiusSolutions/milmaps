/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

public class SRngBrg {
	private double m_ranegKm; // Distance in kilometers
	private double m_bearing; // Bearing in degrees
	
	public SRngBrg(){

	}
	
	public SRngBrg(double rngKm, double brgDeg){
		m_ranegKm = rngKm;
		m_bearing = brgDeg;
	}
	
	public void setRangeKm(double rangeKm) {
		m_ranegKm = rangeKm;
	}
	
	public SRngBrg widthRangeKm(double rangeKm){
		setRangeKm(rangeKm);
		return this;
	}
	
	public double getRanegKm() {
		return m_ranegKm;
	}
	
	public void setBearing(double bearing) {
		m_bearing = bearing;
	}
	
	public SRngBrg widthBearing(double bearing) {
		setBearing(bearing);
		return this;
	}

	public double getBearing() {
		return m_bearing;
	}
}
