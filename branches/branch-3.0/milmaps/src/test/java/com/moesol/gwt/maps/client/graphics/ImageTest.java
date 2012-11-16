/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

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
