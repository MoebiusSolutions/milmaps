package com.moesol.gwt.milmap.client.stats;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.moesol.gwt.maps.client.stats.Stats;

public class StatsTest {
	
	@BeforeClass
	public static void beforeClass() {
		Stats.reset();
	}

	@Test
	public void testIncrementGeodeticToWorld() {
		Stats.incrementGeodeticToWorld();
		assertEquals(1, Stats.getNumGeodeticToWorld());
	}

	@Test
	public void testIncrementWorldToView() {
		Stats.incrementWorldToView();
		Stats.incrementWorldToView();
		assertEquals(2, Stats.getNumWorldToView());
	}
	
	@Test
	public void testIncrementViewToWorld() {
		Stats.incrementViewToWorld();
		Stats.incrementViewToWorld();
		Stats.incrementViewToWorld();
		assertEquals(3, Stats.getNumViewToWorld());
	}
	
	@Test
	public void testIncrementWorldToGeodetic() {
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		Stats.incrementWorldToGeodetic();
		assertEquals(4, Stats.getNumWorldToGeodetic());
	}

	@Test
	public void testIncrementNewGeodeticCoords() {
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		Stats.incrementNewGeodeticCoords();
		assertEquals(5, Stats.getNumNewGeodeticCoords());
	}

	@Test
	public void testIncrementNewWorldCoords() {
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		Stats.incrementNewWorldCoords();
		assertEquals(6, Stats.getNumNewWorldCoords());
	}

	@Test
	public void testIncrementNewViewCoords() {
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		Stats.incrementNewViewCoords();
		assertEquals(7, Stats.getNumNewViewCoords());
	}

}
