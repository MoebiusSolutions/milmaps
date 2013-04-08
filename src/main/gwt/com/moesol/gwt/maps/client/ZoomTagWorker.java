/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

public class ZoomTagWorker {
	private int m_tagX; // In view coordinates
	private int m_tagY; // In view coordinates
	
	private double m_offsetX = 0;
	private double m_offsetY = 0;
	private double m_vwOffsetWcX; // View's x offset in WCs
	private double m_vwOffsetWcY; // View's y offset in WCs
	
	public ZoomTagWorker(){

	}
	
	public void setViewOffsets( double ox, double oy ){
		m_vwOffsetWcX = ox;
		m_vwOffsetWcY = oy;
	}
	
	public void setTagInVC( int tagX, int tagY ){
		m_tagX = tagX;
		m_tagY = tagY;
	}
	
	public double getOffsetX(){ return m_offsetX; }
	
	public double getOffsetY() { return m_offsetY; }
	
	/**
	 * compViewOffsets: computes the view's new offsets so that 
	 * the tagged points stay in the same spot on the view when
	 * scaled by the factor.
	 * @param factor
	 */
	public void compViewOffsets( double factor ) {
		m_offsetX = factor*(m_vwOffsetWcX + m_tagX)- m_tagX;
		m_offsetY = factor*(m_vwOffsetWcY - m_tagY)+ m_tagY;
	}
}
