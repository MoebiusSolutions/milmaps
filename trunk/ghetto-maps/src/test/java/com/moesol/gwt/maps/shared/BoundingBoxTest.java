package com.moesol.gwt.maps.shared;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BoundingBoxTest {

	private static final double EPSILON = 1e-6;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testContains_doesContain_positive() {
		BoundingBox boundingBox = new BoundingBox(0.0, 0.0, 1.0, 1.0);
		assertTrue(boundingBox.contains(0.5, 0.5));
		assertTrue(boundingBox.contains(0.0, 0.0));
		assertTrue(boundingBox.contains(1.0, 1.0));
	}
	
	@Test
	public void testContains_doesContain_negative() {
		BoundingBox boundingBox = new BoundingBox(-1.0, -1.0, 0.0, 0.0);
		assertTrue(boundingBox.contains(-0.5, -0.5));
		assertTrue(boundingBox.contains(0.0, 0.0));
		assertTrue(boundingBox.contains(-1.0, -1.0));
	}
	
	@Test
	public void testContains_doesContain_straddlesAntimeridian_negative() {
		BoundingBox boundingBox = new BoundingBox(0.0, -182.0, 4.0, -178.0);
		assertTrue(boundingBox.contains(0.5, 180.5));
		assertTrue(boundingBox.contains(0.1, 180.0));
		assertTrue(boundingBox.contains(0.1, 179.0));
		assertTrue(boundingBox.contains(0.2, -180.0));
		assertTrue(boundingBox.contains(0.2, -179.0));
	}
	
	@Test
	public void testContains_doesContain_straddlesAntimeridian_positive() {
		BoundingBox boundingBox = new BoundingBox(0.0, 178.0, 4.0, 182.0);
		assertTrue(boundingBox.contains(0.5, 180.5));
		assertTrue(boundingBox.contains(0.1, 180.0));
		assertTrue(boundingBox.contains(0.1, 179.0));
		assertTrue(boundingBox.contains(0.2, -180.0));
		assertTrue(boundingBox.contains(0.2, -179.0));
	}
	
	@Test
	public void testContains_doesNotContain_positive() {
		BoundingBox boundingBox = new BoundingBox(0.0, 0.0, 1.0, 1.0);
		assertFalse(boundingBox.contains(1.1, 1.1));
		assertFalse(boundingBox.contains(-0.01, 0.0));
		assertFalse(boundingBox.contains(1.0, 1.01));
	}
	
	@Test
	public void testContains_doesNotContain_negative() {
		BoundingBox boundingBox = new BoundingBox(-1.0, -1.0, 0.0, 0.0);
		assertFalse(boundingBox.contains(-1.1, -1.1));
		assertFalse(boundingBox.contains(0.01, 0.01));
		assertFalse(boundingBox.contains(-1.1, -1.0));
	}
	
	@Test
	public void testContains_doesNotContain_straddlesAntimeridian_negative() {
		BoundingBox boundingBox = new BoundingBox(0.0, -182.0, 4.0, -178.0);
		assertFalse(boundingBox.contains(4.0, 182.1));
		assertFalse(boundingBox.contains(4.0, -177.9));
		assertFalse(boundingBox.contains(0.1, Math.nextAfter(178, Double.NEGATIVE_INFINITY)));
	}
	
	@Test
	public void testContains_doesNotContain_straddlesAntimeridian_positive() {
		BoundingBox boundingBox = new BoundingBox(0.0, 178.0, 4.0, 182.0);
		assertFalse(boundingBox.contains(4.0, 182.1));
		assertFalse(boundingBox.contains(4.0, -177.9));
		assertFalse(boundingBox.contains(0.1, Math.nextAfter(178, Double.NEGATIVE_INFINITY)));
	}
	
	@Test
	public void testGetCenterLat_minAndMaxNegative() {
		BoundingBox bbox = new BoundingBox(-80.0, 0.0, -60, 10.0);
		assertEquals(bbox.getCenterLat(), -70.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLat_minAndMaxPositive() {
		BoundingBox bbox = new BoundingBox(60.0, 0.0, 80, 10.0);
		assertEquals(bbox.getCenterLat(), 70.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLat_minNegative_maxPositive() {
		BoundingBox bbox = new BoundingBox(-80.0, 0.0, 70, 10.0);
		assertEquals(bbox.getCenterLat(), -5.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minAndMaxNegative() {
		BoundingBox bbox = new BoundingBox(10.0, -50.0, 20.0, -20);
		assertEquals(bbox.getCenterLng(), -35.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minAndMaxPositive() {
		BoundingBox bbox = new BoundingBox(0.0, 10.0, 0.1, 11.0);
		assertEquals(bbox.getCenterLng(), 10.5, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minNegative_maxPositive() {
		BoundingBox bbox = new BoundingBox(-80.0, -80.0, 70.0, 70.0);
		assertEquals(bbox.getCenterLng(), -5.0, EPSILON);
	}
}
