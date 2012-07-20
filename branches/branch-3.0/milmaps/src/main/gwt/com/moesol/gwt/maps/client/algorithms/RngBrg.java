/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

public class RngBrg {
	private double m_ranegKm; // Distance in kilometers
	private double m_bearing; // Bearing in degrees
	
	public RngBrg(){

	}
	
	public RngBrg(double rngKm, double brgDeg){
		m_ranegKm = rngKm;
		m_bearing = brgDeg;
	}
	
	public RngBrg setRanegKm(double rangeKm) {
		m_ranegKm = rangeKm;
		return this;
	}
	
	public double getRanegKm() {
		return m_ranegKm;
	}
	
	public RngBrg setBearing(double bearing) {
		m_bearing = bearing;
		return this;
	}

	public double getBearing() {
		return m_bearing;
	}
}