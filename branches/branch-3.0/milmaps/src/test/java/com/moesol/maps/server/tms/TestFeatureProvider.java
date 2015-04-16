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

import org.junit.Ignore;

import com.moesol.gwt.maps.shared.Feature;

@Ignore
class TestFeatureProvider implements IFeatureProvider {
	private List<Feature> m_data;

	@Override
	public List<Feature> getData() {
		return m_data;
	}

	public void setData(List<Feature> data) {
		m_data = data;
	}

	@Override
	public Feature getFeature(String featureId) {
		for (Feature feature : m_data) {
			if (feature.getTitle().equals(featureId)) {
				return feature;
			}
		}
		return null;
	}
}