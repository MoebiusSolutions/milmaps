/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import static org.junit.Assert.*;

import org.junit.Test;

public class AnchorHandleTest {
	protected AnchorHandle m_handle = new AnchorHandle();
	
	@Test
	public void setGetTest(){
		m_handle.setX(100);
		assertEquals(100,m_handle.getX());
		
		m_handle.setY(100);
		assertEquals(100,m_handle.getY());
		
		m_handle.setCenter(50, 50);
		assertEquals(50,m_handle.getX());
		assertEquals(50,m_handle.getY());
		
		m_handle.setCenter(50, 50);
		m_handle.moveByOffset(-20, 20);
		assertEquals(30,m_handle.getX());
		assertEquals(70,m_handle.getY());
		
		m_handle.setSize(20);
		assertEquals(20,m_handle.getSize());
		
		m_handle.setLineWidth(2);
		assertEquals(2,m_handle.getLineWidth());
		
		m_handle.setStrokeColor(200, 100, 50, 1);
		String color = m_handle.getStrokeColor();
		assertEquals("rgba(200,100,50,1.0)", color);
	}
}
