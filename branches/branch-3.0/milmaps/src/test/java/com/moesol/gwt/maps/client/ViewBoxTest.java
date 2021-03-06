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


import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class ViewBoxTest {
	private ViewBox newViewBox(double top, double left, double bot, double right){
		return ViewBox.builder().bottom(bot).top(top).left(left)
							    .right(right).width(100).height(100).degrees().build();
	}

	protected double wrapLng(double lng) {
		int k = (int)Math.abs((lng/360));
		if (lng > 180.0) {
			lng -= k*360;
			if (lng > 180.0)
				lng -= 360.0;
		} else if (lng < -180.0) {
			lng += k*360;
			if (lng < -180.0)
				lng += 360.0;
		}
		return lng;
	}
	
	@Test
	public void  longSpanTest(){
		double left = 10; 
		double right = -150;
		for (int j = 0; j < 400; j++) {
			left = wrapLng(left+10);
			right = wrapLng(right+10);
			ViewBox vb = newViewBox(40,left,-40,right);
			double degSpan = vb.getLonSpan();
			assertEquals( 200, degSpan, 0.01);
		}
	}
}
