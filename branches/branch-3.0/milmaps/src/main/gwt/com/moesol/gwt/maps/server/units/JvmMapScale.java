/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.server.units;

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


import com.moesol.gwt.maps.client.units.MapScale;


/**
 * Use for unit tests. Override toString with jmv friendly version.
 * @author hastings
 */
public class JvmMapScale extends MapScale {

	public static void init() {
		MapScale.DEFAULT= new Factory() {
			@Override
			public MapScale make(double scale) {
				return new JvmMapScale(scale);
			}
		};
	}
		
	JvmMapScale(double scale) {
		super(scale);
	}

	@Override
	public String toString() {
		double bottom = 1/asDouble();
		if (bottom >= ONE_MILLION) {
			return String.format("1:%1.1fM", bottom / ONE_MILLION);
		} else if (bottom >= ONE_THOUSAND) {
			return String.format("1:%1.1fK", bottom / ONE_THOUSAND);
		} else {
			return String.format("1:%1.1f", bottom);
		}
	}
	
}
