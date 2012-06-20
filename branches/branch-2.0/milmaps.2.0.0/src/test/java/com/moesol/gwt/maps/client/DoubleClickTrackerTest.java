/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;


import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

import com.moesol.gwt.maps.client.DoubleClickTracker.TimeSource;

public class DoubleClickTrackerTest {
	
	@Test
	public void testGoodClick() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(249L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
	
		// simulate double click iteration
		assertFalse(tracker.onMouseDown(10, 10));
		assertTrue(tracker.onMouseDown(12, 8));
		EasyMock.verify(new Object[] {timeSource});
	}
	
	@Test
	public void testBadPosition() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
		
		// simulate failed double click iteration - position failure
		assertFalse(tracker.onMouseDown(10, 10));
		assertFalse(tracker.onMouseDown(20, 20));
	}
	
	@Test
	public void testBadTiming() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(250L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
		
		assertFalse(tracker.onMouseDown(10, 10));
		assertFalse(tracker.onMouseDown(10, 10));
		
		EasyMock.verify(new Object[] {timeSource});
	}
	
	@Test
	public void testTripleClick() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(100L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(249L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
		
		assertFalse(tracker.onMouseDown(10, 10));
		assertTrue(tracker.onMouseDown(10, 10));
		assertFalse(tracker.onMouseDown(10, 10));
		
		EasyMock.verify(new Object[] {timeSource});
	}
	
	@Test
	public void testDoubleBreakDouble() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(100L));
		
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(350L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(350L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
		
		assertFalse(tracker.onMouseDown(10, 10));
		assertTrue(tracker.onMouseDown(10, 10));
		
		assertFalse(tracker.onMouseDown(10, 10));
		assertTrue(tracker.onMouseDown(10, 10));
		
		EasyMock.verify(new Object[] {timeSource});
	}
	
	@Test
	public void testDoubleMoveDouble() {
		TimeSource timeSource = EasyMock.createMock(TimeSource.class);
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(0L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(100L));
		
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(150L));
		EasyMock.expect(new Long(timeSource.currentTimeMillis())).andReturn(new Long(200L));
		EasyMock.replay(new Object[] {timeSource});
		
		DoubleClickTracker tracker = new DoubleClickTracker();
		tracker.setTimeSource(timeSource);
		
		assertFalse(tracker.onMouseDown(10, 10));
		assertTrue(tracker.onMouseDown(10, 10));
		
		assertFalse(tracker.onMouseDown(20, 20));
		assertTrue(tracker.onMouseDown(18, 22));
		
		EasyMock.verify(new Object[] {timeSource});
	}
}
