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


public interface DistanceUnit {
	public static final DistanceUnit METERS = new Meters();
	public static final DistanceUnit KILOMETERS = new Kilometers();
	public static final DistanceUnit MILES = new Miles();
	public double toMeters(double v);
	public double toKilometers(double v);
	public double toMiles(double v);
	public double fromKilometers(double v);
	public double fromMeters(double v);
	public double fromMiles(double v);
}
