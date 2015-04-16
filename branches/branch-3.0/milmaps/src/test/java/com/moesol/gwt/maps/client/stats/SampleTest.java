/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.stats;

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


import static org.junit.Assert.*;

import org.junit.Test;

public class SampleTest {

	@Test
	public void testCallTree() {
		Sample.ROOT.getChildren().clear();
		Sample.MAP_UPDATE_VIEW.beginSample();
		// Do work.
		Sample.MAP_UPDATE_VIEW.endSample();
		
		assertEquals(1, Sample.ROOT.getChildren().size());
		assertTrue(Sample.ROOT.getChildren().contains(Sample.MAP_UPDATE_VIEW));
	}

	@Test
	public void testCallTree2() {
		Sample.MAP_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.endSample();
		Sample.LAYER_POSITION_IMAGES.beginSample();
		Sample.LAYER_POSITION_IMAGES.endSample();
		Sample.MAP_UPDATE_VIEW.endSample();
		
		Sample.MAP_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.beginSample();
		Sample.LAYER_UPDATE_VIEW.endSample();
		Sample.LAYER_POSITION_IMAGES.beginSample();
		Sample.LAYER_POSITION_IMAGES.endSample();
		Sample.MAP_UPDATE_VIEW.endSample();
		
		assertEquals(1, Sample.ROOT.getChildren().size());
		assertTrue(Sample.ROOT.getChildren().contains(Sample.MAP_UPDATE_VIEW));

		assertEquals(2, Sample.MAP_UPDATE_VIEW.getChildren().size());
		assertTrue(Sample.MAP_UPDATE_VIEW.getChildren().contains(Sample.LAYER_UPDATE_VIEW));
		assertTrue(Sample.MAP_UPDATE_VIEW.getChildren().contains(Sample.LAYER_POSITION_IMAGES));
		
		depthFirstTraversal();
	}

	private void depthFirstTraversal() {
		traverse(0, Sample.ROOT);
	}

	private void traverse(int indent, Sample node) {
		for (Sample s : node.getChildren()) {
			outLeader(indent);
			System.out.println(s);
			traverse(indent + 1, s);
		}
	}

	private void outLeader(int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print('-');
		}
	}
	
}
