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


import java.awt.image.BufferedImage;

import org.easymock.EasyMock;
import org.junit.Ignore;

import com.moesol.gwt.maps.shared.Feature;

@Ignore
class TestFeatureTileRenderer extends
		AbstractFeatureTileRenderer {
	public TestFeatureTileRenderer(IFeatureProvider dataProvider) {
		super(dataProvider);
	}

	private BufferedImage m_icon = EasyMock.createMock(BufferedImage.class);

	@Override
	public BufferedImage getIcon(Feature feature) {
		return m_icon;
	}
}