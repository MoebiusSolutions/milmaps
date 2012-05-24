/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import com.google.gwt.i18n.client.NumberFormat;

/**
 * Map scale. For example 1:1M is one to one million.
 * This class is a value object.
 * 
 * @author hastings
 *
 */
public class MapScale {
	protected static final double ONE_MILLION = 1000000.0;
	protected static final double ONE_THOUSAND = 1000.0;
	private final double m_scale;
	public static Factory DEFAULT = new Factory() {
		@Override
		public MapScale make(double scale) {
			return new MapScale(scale);
		}
	};
	
	public interface Factory {
		MapScale make(double scale);
	}
	
	protected MapScale(double scale) {
		this.m_scale = scale;
	}
	
	public double asDouble() {
		return m_scale;
	}
	
	public static MapScale forScale(double scale) {
		return DEFAULT.make(scale);
	}
	
	public static MapScale parse(String s) {
		String[] parts = s.split(":");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Could not parse: " + s);
		}
		double top = Double.parseDouble(parts[0]);
		double bottom = parseBottom(parts[1]);
		
		return forScale(top / bottom);
	}

	private static double parseBottom(String string) {
		double multiplier;
		String bottomNumber;
		if (string.endsWith("M")) {
			multiplier = ONE_MILLION;
			bottomNumber = stripLastCharacter(string);
		} else if (string.endsWith("K")) {
			multiplier = ONE_THOUSAND;
			bottomNumber = stripLastCharacter(string);
		} else {
			multiplier = 1.0;
			bottomNumber = string;
		}
		return Double.parseDouble(bottomNumber) * multiplier;
	}

	private static String stripLastCharacter(String string) {
		return string.substring(0, string.length() - 1);
	}

	@Override
	public String toString() {
		double bottom = 1/m_scale;
		if (bottom >= ONE_MILLION) {
			double d = bottom / ONE_MILLION;
			return NumberFormat.getFormat("0.0").format(d) + "M";
		} else if (bottom >= ONE_THOUSAND) {
			return NumberFormat.getFormat("0.0").format(bottom / ONE_THOUSAND) + "K";
		} else {
			return NumberFormat.getFormat("0.0").format(bottom);
		}
	}
	
	@Override
	public int hashCode() {
		return new Double(m_scale).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapScale other = (MapScale) obj;
		Double left = new Double(m_scale);
		Double right = new Double(other.m_scale);
		return left.equals(right);
	}
	
}
