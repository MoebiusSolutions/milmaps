/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


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
