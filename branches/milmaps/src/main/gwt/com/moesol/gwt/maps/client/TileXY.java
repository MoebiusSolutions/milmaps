/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

public class TileXY {
	public int m_x = 0;
	public int m_y = 0;
	public int m_levelOfDetail = 0;
	
	public TileXY( ) {
		super();
	}
	
	public TileXY( int x, int y ) {
		m_x = x;
		m_y = y;
	}
	
	public void copy( TileXY t ){
		m_x = t.m_x;
		m_y = t.m_y;
		m_levelOfDetail = t.m_levelOfDetail;
	}
	
	public TileXY clone(){
		TileXY c = new TileXY();
		c.copy(this);
		return c;
	}
}
