/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;

public interface ICanvasTool {
	
	public abstract Canvas canvas();
	
	public abstract void setSize(int width, int height);
}
