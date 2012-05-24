/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.DOM;

public class CanvasTool
{
	protected final Canvas m_canvas = Canvas.createIfSupported();
	
	public CanvasTool(){
		//m_canvas = Canvas.createIfSupported();
		if (m_canvas != null){
			DOM.setStyleAttribute(m_canvas.getElement(), "position", "absolute");
			DOM.setStyleAttribute(m_canvas.getElement(), "backgroundColor", "transparent");
			//DOM.setStyleAttribute(m_canvas.getElement(), "border", "1px solid blue");
		}
	}
	
	public CanvasTool( int width, int height){
		//m_canvas = Canvas.createIfSupported();
		if (m_canvas != null){
			DOM.setStyleAttribute(m_canvas.getElement(), "position", "absolute");
			DOM.setStyleAttribute(m_canvas.getElement(), "backgroundColor", "transparent");
			//DOM.setStyleAttribute(m_canvas.getElement(), "border", "1px solid blue");	
			setSize(width, height);
		}
	}
	
	public Canvas canvas(){ return m_canvas; }
	
	public void setSize(int width, int height){
		if(m_canvas != null){
			m_canvas.setWidth(width+"px");
			m_canvas.setHeight(height + "px");
			m_canvas.setCoordinateSpaceWidth(width);
			m_canvas.setCoordinateSpaceHeight(height);
		}
	}
	
	public void setEventHandler(IToolEventHandler handler){
		if (m_canvas != null){
			//add Listeners
			m_canvas.addMouseDownHandler(handler);
			m_canvas.addMouseMoveHandler(handler);
			m_canvas.addMouseUpHandler(handler);
		}
	}
}
