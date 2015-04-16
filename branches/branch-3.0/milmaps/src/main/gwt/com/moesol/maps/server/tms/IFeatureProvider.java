/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.maps.server.tms;

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


import java.util.List;

import com.moesol.gwt.maps.shared.Feature;

/**
 * Implemented by classes capable of providing features.
 */
public interface IFeatureProvider extends ITileDataProvider<List<Feature>> {
	/**
	 * Get a feature by ID.
	 * 
	 * @param featureId The ID of the feature.
	 * @return The requested feature, or null if not found.
	 */
	public Feature getFeature(String featureId);
}
