package com.moesol.gwt.maps.client;

public class ZoomTagWorker {
	private int m_offsetX = 0;
	private int m_offsetY = 0;
	private int m_tagX; // In view coordinates
	private int m_tagY; // In view coordinates
	private int m_vwOffsetWcX; // View's x offset in WCs
	private int m_vwOffsetWcY; // View's y offset in WCs
	
	public ZoomTagWorker(){

	}
	
	public void setViewOffsets( int ox, int oy ){
		m_vwOffsetWcX = ox;
		m_vwOffsetWcY = oy;
	}
	
	public void setTagInVC( int tagX, int tagY ){
		m_tagX = tagX;
		m_tagY = tagY;
	}
	
	public int getOffsetX(){ return m_offsetX; }
	
	public int getOffsetY() { return m_offsetY; }
	
	/**
	 * compViewOffsets: computes the view's new offsets so that 
	 * the tagged points stay in the same spot on the view when
	 * scaled by the factor.
	 * @param factor
	 */
	public void compViewOffsets( double factor ) {
		m_offsetX = (int)(factor*(m_vwOffsetWcX + m_tagX))- m_tagX;
		m_offsetY = (int)(factor*(m_vwOffsetWcY - m_tagY))+ m_tagY;
	}
}
