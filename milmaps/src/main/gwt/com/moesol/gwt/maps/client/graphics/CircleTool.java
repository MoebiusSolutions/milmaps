/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

public class CircleTool extends IToolEventHandler{
	private Boolean m_mouseDown = false;
	private Context2d m_context;
	public CircleTool(CanvasTool canvas){
		canvas.setEventHandler(this);
		m_context = canvas.canvas().getContext2d();
		m_context.setStrokeStyle("rgb(0,0,0)");
	}
	
	public void drawCircle(int x, int y){
		m_context.beginPath();
		m_context.arc(x, y, 30, 0, 2*Math.PI, true);
		m_context.closePath();
		m_context.fill();
	}
	
	@Override
	public void onKeyUp(KeyUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		m_mouseDown = false;
		
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		m_mouseDown = true;
		int x = event.getX();
		int y = event.getY();
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
