/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;

public class CanvasTool { //implements ResizeHandler {
	protected final Canvas m_canvas = Canvas.createIfSupported();
	
	public CanvasTool(){
		if (m_canvas != null){
			m_canvas.setStyleName("milmaps-canvas");
		}
	}
	
	public CanvasTool( int width, int height){
		if (m_canvas != null){
			m_canvas.setStyleName("milmaps-canvas");
			setSize(width, height);
		}
	}
	
	//@Override
	//public void onResize(ResizeEvent event){;
	//	int w = event.getWidth();
	//	int h = event.getHeight();
	//	setSize(w,h);
	//}
	
	public Canvas canvas(){ return m_canvas; }
	
	public void setSize(int width, int height){
		if(m_canvas != null){
			m_canvas.setWidth(width+"px");
			m_canvas.setHeight(height + "px");
			m_canvas.setCoordinateSpaceWidth(width);
			m_canvas.setCoordinateSpaceHeight(height);
		}
	}
}
