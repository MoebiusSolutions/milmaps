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


import com.moesol.gwt.maps.client.GeodeticCoords;

public class Degrees implements AngleUnit {
	public static double asRadians(double d) {
		return Math.PI * d / 180.0;
	}

	@Override
	public double toDegrees(double v) {
		return v;
	}

	@Override
	public double toRadians(double v) {
		return asRadians(v);
	}

	@Override
	public double fromDegrees(double v) {
		return v;
	}

	@Override
	public double fromRadians(double v) {
		return Radians.asDegrees(v);
	}
	
	public static GeodeticCoords geodetic(double lat, double lng) {
		return new GeodeticCoords(lng, lat, DEGREES);
	}
	
}
