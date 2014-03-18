/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class CanvasTool implements ICanvasTool { //implements ResizeHandler {
	protected final Canvas m_canvas = Canvas.createIfSupported();
	IContext m_icontext = null;
	public CanvasTool(){
		if (m_canvas != null){
			m_icontext = new IContext(){
				Context2d m_context = m_canvas.getContext2d();	
				@Override
				public void beginPath() {
					m_context.beginPath();
				}
				
				@Override
				public void clearRect(double x, double y, double w, double h){
					m_context.clearRect(x, y, w, h);
				}
		
				@Override
				public void closePath() {
					m_context.closePath();
				}
				
				@Override
				public void lineTo(double x, double y) {
					m_context.lineTo(x, y);
				}
				
				@Override
				public void moveTo(double x, double y) {
					m_context.moveTo(x, y);
				}
				
				@Override
				public void setLineWidth(double width) {
					m_context.setLineWidth(width);
				}
				
				@Override
				public void setStrokeStyle(String style) {
					m_context.setStrokeStyle(style);
				}
		
				@Override
				public void stroke() {
					m_context.stroke();
				}
				
				public void strokeRect(double x, double y, double w, double h){
					m_context.strokeRect(x, y, w, h);
				}
			};
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
	
	public void safeAddCanvasTo(AbsolutePanel target) {
		if (m_canvas == null) { return; }
		if (m_canvas.isAttached()) { return; }
		target.add(m_canvas);
	}
	
	@Override
	public IContext getContext(){ 
		return m_icontext; 
	}
	
	@Override
	public int getOffsetWidth(){ 
		return m_canvas.getOffsetWidth();
	}
	
	@Override
	public int getOffsetHeight(){
		return m_canvas.getOffsetHeight();
	}
	
	@Override
	public void setSize(int width, int height){
		if(m_canvas != null){
			m_canvas.setWidth(width+"px");
			m_canvas.setHeight(height + "px");
			m_canvas.setCoordinateSpaceWidth(width);
			m_canvas.setCoordinateSpaceHeight(height);
		}
	}
}
