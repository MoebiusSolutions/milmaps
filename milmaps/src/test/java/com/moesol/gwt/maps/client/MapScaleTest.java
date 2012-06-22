package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class MapScaleTest {

	@Test
	public void testAsDouble() {
		MapScale s = new MapScale(1/50000.0);
		assertEquals(1/50000.0, s.asDouble(), 0.0);
	}

	@Test
	public void testParse() {
		assertEquals(1/1000000.0, MapScale.parse("1:1M").asDouble(), 0.0);
		assertEquals(1/50000.0, MapScale.parse("1:50K").asDouble(), 0.0);
	}
	@Test
	public void testEquals() {
		MapScale s1 = new MapScale(1.0);
		MapScale s2 = new MapScale(1.0);
		MapScale s3 = new MapScale(2.0);
		
		assertEquals(s1, s2);
		assertFalse(s1.equals(s3));
	}

}
