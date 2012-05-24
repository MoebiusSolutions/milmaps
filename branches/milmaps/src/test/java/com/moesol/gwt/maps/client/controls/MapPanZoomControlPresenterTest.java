/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.controls;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MapPanZoomControlPresenterTest {
	
	MapPanZoomControl.Presenter presenter;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		presenter = new MapPanZoomControl.Presenter();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalculateDelta_negative_half() {
		int delta = (int)presenter.calculateDelta(20, 5, 100);
		assertEquals(-50, delta);
	}
	
	@Test
	public void testCalculateDelta_positive_half() {
		int delta = (int)presenter.calculateDelta(20, 15, 100);
		assertEquals(50, delta);
	}
	
	@Test
	public void testCalculateDelta_negative_full() {
		int delta = (int)presenter.calculateDelta(20, 0, 100);
		assertEquals(-100, delta);
	}
	
	@Test
	public void testCalculateDelta_positive_full() {
		int delta = (int)presenter.calculateDelta(20, 20, 100);
		assertEquals(100, delta);
	}
	
	@Test
	public void testCalculateDelta_zero() {
		int delta = (int)presenter.calculateDelta(20, 10, 100);
		assertEquals(0, delta);
	}
	
	@Test
	public void testCalculateDelta_negativeOneTenth() {
		long delta = Math.round(presenter.calculateDelta(100, 45, 100));
		assertEquals(-10L, delta);
	}
	
	@Test
	public void testCalculateDelta_positiveOneTenth() {
		long delta = Math.round(presenter.calculateDelta(100, 55, 100));
		assertEquals(10L, delta);
	}
}
