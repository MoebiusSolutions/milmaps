/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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
import com.moesol.gwt.maps.client.stats.Stats;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Radians;

// TODO move the three coordinate classes to their own package (coords or units).
/**
 * Immutable geodetic coordinates.
 * @author hastings
 */
public class GeodeticCoords implements IsSerializable {
	// would be final, but IsSerializable does not support
	private double m_lambda;
	private double m_phi;
	private double m_altitude;
	
	public static class Builder {
		private double m_lambda;
		private double m_phi;
		private double m_altitude;
		private AngleUnit m_angleUnit;
		
		public Builder setLambda(double v) { m_lambda = v; return this; }
		public Builder setLongitude(double v) { m_lambda = v; return this; }
		public Builder setPhi(double v) { m_phi = v; return this; }
		public Builder setLatitude(double v) { m_phi = v; return this; }
		public Builder setAltitude(double v) { m_altitude = v; return this; }
		public double getLambda() { return m_lambda; }
		public double getLongitude() { return m_lambda; }
		public double getPhi() { return m_phi; }
		public double getLatitude() { return m_phi; }
		public double getAltitude() { return m_altitude; }
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
		public Builder latitude(double lat) { return setLatitude(lat); }
		public Builder longitude(double lng) { return setLongitude(lng); }
		public GeodeticCoords build() {
			if (m_angleUnit == null) {
				throw new IllegalStateException("No angle unit specified, use degrees() or radians()");
			}
			return new GeodeticCoords(m_lambda, m_phi, m_angleUnit, m_altitude);
		}
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public GeodeticCoords() {
		Stats.incrementNewGeodeticCoords();
		
		m_lambda = m_phi = m_altitude = 0.0;
	}

	/**
	 * @param lambdaLongitude in radians or degrees based on unit
	 * @param phiLatitude in radians or degrees based on unit
	 * @param angleUnit angular units of lambda and phi
	 */
	public GeodeticCoords(double lambdaLongitude, double phiLatitude, AngleUnit angleUnit) {
		this(lambdaLongitude, phiLatitude, angleUnit, 0.0);
	}

	/**
	 * @param lambda longitude
	 * @param phi latitude
	 * @param angleUnit angular units of lambda and phi
	 * @param altitude
	 */
	public GeodeticCoords(double lambda, double phi, AngleUnit angleUnit, double altitude) {
		Stats.incrementNewGeodeticCoords();

		// TODO remove altitude or add DistanceUnit parameter
		
		// similar to x, y, z
		m_lambda = angleUnit.toRadians(lambda);
		m_phi = angleUnit.toRadians(phi);
		m_altitude = altitude;
	}
	
	public class GetLatitude {
		public double degrees() {
			return getPhi(AngleUnit.DEGREES);
		}
		public double radians() {
			return getPhi(AngleUnit.RADIANS);
		}
	}
	public class GetLongitude {
		public double degrees() {
			return getLambda(AngleUnit.DEGREES);
		}
		public double radians() {
			return getLambda(AngleUnit.RADIANS);
		}
	}
	public GetLatitude latitude() {
		return new GetLatitude();
	}
	public GetLongitude longitude() {
		return new GetLongitude();
	}
	
	/**
	 * @return altitude in meters
	 */
	public double getAltitude() {
		return m_altitude;
	}
	
	/** 
	 * @param unit angular unit to convert return value to
	 * @return longitude in the units specified by unit
	 */
	public double getLambda(AngleUnit unit) {
		return unit.fromRadians(m_lambda);
	}

	/**
	 * @param unit angular unit to convert return value to
	 * @return latitude in the units specified by unit
	 */
	public double getPhi(AngleUnit unit) {
		return unit.fromRadians(m_phi);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = (int)m_altitude;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = (int)m_lambda;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		temp = (int)m_phi;
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GeodeticCoords))
			return false;
		final GeodeticCoords other = (GeodeticCoords) obj;
		if (m_altitude != other.m_altitude)
			return false;
		if (m_lambda != other.m_lambda)
			return false;
		if (m_phi != other.m_phi)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "[ lat: " + Radians.asDegrees(m_phi) + ", lng: " + Radians.asDegrees(m_lambda) + " ]";
	}

}
