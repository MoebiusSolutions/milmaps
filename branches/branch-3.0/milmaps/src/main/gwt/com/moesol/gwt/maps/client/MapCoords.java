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



public class MapCoords {
	private final double m_x;
	private final double m_y;

	public static class Builder {
		private double m_x;
		private double m_y;
		
		public MapCoords build() {
			return new MapCoords(m_x, m_y);
		}
		public Builder setX(double x) { m_x = x; return this; }
		public Builder setY(double y) { m_y = y; return this; }
		public double getX() { return m_x; }
		public double getY() { return m_y; }
	}
	public static Builder builder() {
		return new Builder();
	}
	
	public MapCoords() {
		m_x = m_y = 0;
	}
	public MapCoords(double x, double y) {
		m_x = x;
		m_y = y;
	}

	public MapCoords(WorldCoords wc) {
		this(wc.getX(), wc.getY());
	}

	public double getX() {
		return m_x;
	}

	public double getY() {
		return m_y;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		double result = 1;
		result = PRIME * result + m_x;
		result = PRIME * result + m_y;
		return (int)(result + 0.5);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MapCoords)) 
			return false;
		final MapCoords other = (MapCoords) obj;
		if (m_x != other.m_x)
			return false;
		if (m_y != other.m_y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + m_x + "," + m_y + "]";
	}

}
