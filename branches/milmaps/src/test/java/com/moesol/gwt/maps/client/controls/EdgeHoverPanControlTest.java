/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.controls;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class EdgeHoverPanControlTest {
	
	private EdgeHoverPanControl m_control;
	
	@Before
	public void setUp() {
		m_control = EasyMock.createMock(EdgeHoverPanControl.class);
	}
	
	@Test
	public void testOnHover_barelyInsideHoverRadius() {
		int hoverX = 11;
		int hoverY = 11;
		
		testOnHover_noPan(hoverX, hoverY);
	}

	protected void testOnHover_noPan(int hoverX, int hoverY) {
		int mapWidth = 100;
		int mapHeight = 100;
		int mapTop = 0;
		int mapLeft = 0;
		
		int hoverRadius = 10;
		int panInterval = 1000;
		int maxPanPixels = 100;
		EdgeHoverPanControl.Presenter presenter = new EdgeHoverPanControl.Presenter(m_control, hoverRadius, panInterval, maxPanPixels);
		
		m_control.cancelPanLoop();
		
		EasyMock.replay(m_control);
		
		presenter.onHover(hoverX, hoverY, mapWidth, mapHeight, mapLeft, mapTop, false);
		
		assertEquals(0, presenter.getDeltaX());
		assertEquals(0, presenter.getDeltaY());
	}
	
	@Test
	public void testOnHover_panLeft_50PCT() {
		testOnHover_pan(5, 11, -50, 0);
	}
	
	@Test
	public void testOnHover_panLeft_100PCT() {
		testOnHover_pan(0, 11, -100, 0);
	}
	
	@Test
	public void testOnHover_panLeft_10PCT() {
		testOnHover_pan(9, 11, -10, 0);
	}
	
	@Test
	public void testOnHover_panLeft_90PCT() {
		testOnHover_pan(1, 11, -90, 0);
	}
	
	@Test
	public void testOnHover_panRight_50PCT() {
		testOnHover_pan(95, 11, 50, 0);
	}
	
	@Test
	public void testOnHover_panRight_100PCT() {
		testOnHover_pan(100, 11, 100, 0);
	}
	
	@Test
	public void testOnHover_panRight_10PCT() {
		testOnHover_pan(91, 11, 10, 0);
	}
	
	@Test
	public void testOnHover_panRight_90PCT() {
		int hoverX = 99;
		int hoverY = 11;
		int expectedDx = 90;
		int expectedDy = 0;
		
		testOnHover_pan(hoverX, hoverY, expectedDx, expectedDy);
	}
	
	@Test
	public void testOnHover_lessThanLeft() {
		int hoverX = -10;
		int hoverY = 11;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_lessThanLeft_withinTopHoverRadius() {
		int hoverX = -10;
		int hoverY = 9;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_greaterThanRight_withinBottomHoverRadius() {
		int hoverX = -10;
		int hoverY = 95;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_greaterThanRight_withinTopHoverRadius() {
		int hoverX = 110;
		int hoverY = 9;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_greaterThanRight_greaterThanBottom() {
		int hoverX = 110;
		int hoverY = 120;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_lessThanLeft_greaterThanBottom() {
		int hoverX = -100;
		int hoverY = 120;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_lessThanLeft_middleY() {
		int hoverX = -100;
		int hoverY = 50;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_lessThanTop_middleX() {
		int hoverX = 50;
		int hoverY = -1;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_lessThanLeft_withinBottomHoverRadius() {
		int hoverX = -10;
		int hoverY = 95;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_greaterThanBottom_withinRightHoverRadius() {
		int hoverX = 95;
		int hoverY = 110;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_greaterThanBottom_withinLeftHoverRadius() {
		int hoverX = 5;
		int hoverY = 110;
		
		testOnHover_noPan(hoverX, hoverY);
	}
	
	@Test
	public void testOnHover_panUp_50PCT() {
		testOnHover_pan(11, 5, 0, -50);
	}
	
	@Test
	public void testOnHover_panUp_100PCT() {
		testOnHover_pan(11, 0, 0, -100);
	}
	
	@Test
	public void testOnHover_panUp_10PCT() {
		testOnHover_pan(11, 9, 0, -10);
	}
	
	@Test
	public void testOnHover_panUp_90PCT() {
		testOnHover_pan(11, 1, 0, -90);
	}
	
	@Test
	public void testOnHover_panUp_50PCT_panRight_10PCT() {
		testOnHover_pan(91, 5, 10, -50);
	}
	
	@Test
	public void testOnHover_panUp_100PCT_panRight_50PCT() {
		testOnHover_pan(95, 0, 50, -100);
	}
	
	@Test
	public void testOnHover_panUp_10PCT_panLeft_60PCT() {
		testOnHover_pan(4, 9, -60, -10);
	}
	
	@Test
	public void testOnHover_panUp_90PCT_panLeft_70PCT() {
		testOnHover_pan(3, 1, -70, -90);
	}
	
	@Test
	public void testOnHover_panDown_50PCT_panRight_20PCT() {
		testOnHover_pan(92, 95, 20, 50);
	}
	
	@Test
	public void testOnHover_panDown_100PCT_panRight_30PCT() {
		testOnHover_pan(93, 100, 30, 100);
	}
	
	@Test
	public void testOnHover_panDown_10PCT_panLeft_50PCT() {
		testOnHover_pan(5, 91, -50, 10);
	}
	
	@Test
	public void testOnHover_panDown_90PCT_panLeft_80PCT() {
		int hoverX = 11;
		int hoverY = 99;
		int expectedDx = 0;
		int expectedDy = 90;
		
		testOnHover_pan(hoverX, hoverY, expectedDx, expectedDy);
	}

	void testOnHover_pan(int hoverX, int hoverY, int expectedDx, int expectedDy) {
		int hoverRadius = 10;
		int panInterval = 1000;
		int maxPanPixels = 100;
		EdgeHoverPanControl.Presenter presenter = new EdgeHoverPanControl.Presenter(m_control, hoverRadius, panInterval, maxPanPixels);
		
		int mapWidth = 100;
		int mapHeight = 100;
		int mapTop = 0;
		int mapLeft = 0;
		
		m_control.startPanLoop(panInterval);
		
		EasyMock.replay(m_control);
		
		presenter.onHover(hoverX, hoverY, mapWidth, mapHeight, mapLeft, mapTop, false);
		
		assertEquals(expectedDx, presenter.getDeltaX());
		assertEquals(expectedDy, presenter.getDeltaY());
	}
}
