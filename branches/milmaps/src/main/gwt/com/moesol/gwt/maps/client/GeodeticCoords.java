package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.moesol.gwt.maps.client.units.AngleUnit;
import com.moesol.gwt.maps.client.units.Radians;

public class GeodeticCoords implements IsSerializable {
	private double m_lambda;
	private double m_phi;
	private double m_altitude;
	
	public GeodeticCoords() {
		m_lambda = m_phi = m_altitude = 0.0;
	}

	/**
	 * @param lambda longitude
	 * @param phi latitude
	 * @param angleUnit angular units of lambda and phi
	 */
	public GeodeticCoords(double lambda, double phi, AngleUnit angleUnit) {
		set(lambda, phi, angleUnit);
		m_altitude = 0.0;
	}

	/**
	 * @param lambda longitude
	 * @param phi latitude
	 * @param angleUnit angular units of lambda and phi
	 * @param altitude
	 */
	public GeodeticCoords(double lambda, double phi, AngleUnit angleUnit, double altitude) {
		// similar to x, y, z
		set(lambda, phi, angleUnit);
		m_altitude = altitude;
	}
	
	/**
	 * @param lambda longitude
	 * @param phi latitude
	 * @param angleUnit angular units of lambda and phi
	 */
	public void set(double lambda, double phi, AngleUnit angleUnit) {
		m_lambda = angleUnit.toRadians(lambda);
		m_phi = angleUnit.toRadians(phi);
	}

	/**
	 * Copy location's state to this
	 * @param location
	 */
	public void copyFrom(GeodeticCoords location) {
		m_altitude = location.m_altitude;
		m_lambda = location.m_lambda;
		m_phi = location.m_phi;
	}

	/**
	 * @return altitude in meters
	 */
	public double getAltitude() {
		return m_altitude;
	}
	public void setAltitude(double meters) {
		m_altitude = meters;
	}
	
	/** 
	 * @param unit angular unit to convert return value to
	 * @return longitude in the units specified by unit
	 */
	public double getLambda(AngleUnit unit) {
		return unit.fromRadians(m_lambda);
	}
	/**
	 * @param lambda longitude
	 * @param unit angular units of longitude
	 */
	public void setLambda(double lambda, AngleUnit unit) {
		m_lambda = unit.toRadians(lambda);
	}

	/**
	 * @param unit angular unit to convert return value to
	 * @return latitude in the units specified by unit
	 */
	public double getPhi(AngleUnit unit) {
		return unit.fromRadians(m_phi);
	}
	/**
	 * @param phi latitude
	 * @param unit angular units of latitude
	 */
	public void setPhi(double phi, AngleUnit unit) {
		m_phi = unit.toRadians(phi);
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
