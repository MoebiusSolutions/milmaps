/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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


import org.junit.Before;
import org.junit.Test;

public class ImageTest {
	boolean bDraw = false;
	int count = 1;
	@Before
	public void before() throws Exception {
		
	}
	
	@Test 
	public void DrawImagTest(){
		if (bDraw){
			DrawObject.draw("arc",count,false);
			DrawObject.draw("arrow",count,true);
			DrawObject.draw("box",count,true);
			DrawObject.draw("circle",count,true);
			DrawObject.draw("ellipse",count,true);
			DrawObject.draw("polygon",count,false);
			DrawObject.draw("line",count,false);
			DrawObject.draw("rect",count,true);
			DrawObject.draw("sector",count,true);
			DrawObject.draw("triangle",count,true);
		}
	}
}
