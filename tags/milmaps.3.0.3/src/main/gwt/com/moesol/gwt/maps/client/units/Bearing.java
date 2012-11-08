/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.moesol.gwt.maps.client.stats.Stats;

public class Bearing implements IsSerializable {
	// would be final, but IsSerializable does not support
	private double m_brg;
	
	public static class Builder {
		private double m_brg;
		private AngleUnit m_angleUnit;
		
		public Builder setValue(double v) { m_brg = v; return this; }
		public double getValue() { return m_brg; }
		public Builder degrees() {
			if (m_angleUnit != null && m_angleUnit != AngleUnit.DEGREES) {
				throw new IllegalStateException("Angle unit cannot be changed");
			}
			m_angleUnit = AngleUnit.DEGREES; return this; 
		}
		public Builder radians() { 
			if (m_angleUnit != null && m_angleUnit != AngleUnit.RADIANS) {
				throw new IllegalStateException("Angle unit cannot be changed");
			}
			m_angleUnit = AngleUnit.RADIANS; return this; 
		}
		public Builder value(double brg) { return setValue(brg); }
		public Bearing build() {
			if (m_angleUnit == null) {
				throw new IllegalStateException("No angle unit specified, use degrees() or radians()");
			}
			return new Bearing(m_brg, m_angleUnit);
		}
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public Bearing() {
		Stats.incrementNewGeodeticCoords();
		m_brg = 0.0;
	}

	/**
	 * @param Bearing
	 * @param bearing
	 * @param angleUnit angular units of bearing
	 */
	public Bearing(double brg, AngleUnit angleUnit) {
		Stats.incrementNewGeodeticCoords();
		m_brg = angleUnit.toRadians(brg);
	}
	
	public class GetBearing {
		public double degrees() {
			return value(AngleUnit.DEGREES);
		}
		public double radians() {
			return value(AngleUnit.RADIANS);
		}
	}
	
	public GetBearing bearing() {
		return new GetBearing();
	}

	/**
	 * @param unit angular unit to convert return value to
	 * @return latitude in the units specified by unit
	 */
	public double value(AngleUnit unit) {
		return unit.fromRadians(m_brg);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = (int)m_brg;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Bearing))
			return false;
		final Bearing other = (Bearing) obj;
		if (m_brg != other.m_brg)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[ Degree : " + Radians.asDegrees(m_brg) + " ]";
	}
}
