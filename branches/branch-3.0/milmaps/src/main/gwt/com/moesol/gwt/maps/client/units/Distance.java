/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

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


import com.google.gwt.user.client.rpc.IsSerializable;

public class Distance implements IsSerializable {
	// would be final, but IsSerializable does not support
	private double m_dist; // this is always in meters.
	
	public static class Builder {
		private double m_dist;
		private DistanceUnit m_distUnit;
		
		public Builder setValue(double v) { m_dist = v; return this; }
		public double getValue() { return m_dist; }
		public Builder meters() {
			if (m_distUnit != null && m_distUnit != DistanceUnit.METERS) {
				throw new IllegalStateException("Distance unit cannot be changed");
			}
			m_distUnit = DistanceUnit.METERS; 
			return this; 
		}
		public Builder kilometers() { 
			if (m_distUnit != null && m_distUnit != DistanceUnit.KILOMETERS) {
				throw new IllegalStateException("Distance unit cannot be changed");
			}
			m_distUnit = DistanceUnit.KILOMETERS; 
			return this; 
		}
		
		public Builder miles() { 
			if (m_distUnit != null && m_distUnit != DistanceUnit.MILES) {
				throw new IllegalStateException("Distance unit cannot be changed");
			}
			m_distUnit = DistanceUnit.MILES; 
			return this; 
		}
		public Builder value(double d) { return setValue(d); }
		public Distance build() {
			if (m_distUnit == null) {
				throw new IllegalStateException("No distance unit specified, use degrees() or radians()");
			}
			return new Distance(m_dist, m_distUnit);
		}
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public Distance() {
		m_dist = 0.0;
	}

	public Distance(double dist, DistanceUnit distUnit) {
		m_dist = distUnit.toMeters(dist);
	}
	
	public class GetDistance {
		public double meters() {
			return getDistance(DistanceUnit.METERS);
		}
		public double kilometers() {
			return getDistance(DistanceUnit.KILOMETERS);
		}
		public double miles() {
			return getDistance(DistanceUnit.MILES);
		}
	}
	
	/** 
	 * @param unit angular unit to convert return value to
	 * @return distance in the units specified by unit
	 */
	public double getDistance(DistanceUnit unit) {
		return unit.fromMeters(m_dist);
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = (int)m_dist;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Distance))
			return false;
		final Distance other = (Distance) obj;
		if (m_dist != other.m_dist)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[ dist: " + Meters.asKilometers(m_dist) + " KMs ]";
	}
	
}
