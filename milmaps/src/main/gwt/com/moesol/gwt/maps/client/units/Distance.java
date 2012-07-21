/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Distance implements IsSerializable {
	// would be final, but IsSerializable does not support
	private double m_dist;
	
	public static class Builder {
		private double m_dist;
		private DistanceUnit m_distUnit;
		
		public Builder setValue(double v) { m_dist = v; return this; }
		public double getValue() { return m_dist; }
		public Builder meters() {
			if (m_distUnit != null && m_distUnit != DistanceUnit.METERS) {
				throw new IllegalStateException("Distance unit cannot be changed");
			}
			m_distUnit = DistanceUnit.METERS; return this; 
		}
		public Builder kilometers() { 
			if (m_distUnit != null && m_distUnit != DistanceUnit.KILOMETERS) {
				throw new IllegalStateException("Angle unit cannot be changed");
			}
			m_distUnit = DistanceUnit.KILOMETERS; return this; 
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

	/**
	 * @param lambda longitude
	 * @param phi latitude
	 * @param angleUnit angular units of lambda and phi
	 * @param altitude
	 */
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
	}
	
	/** 
	 * @param unit angular unit to convert return value to
	 * @return longitude in the units specified by unit
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
