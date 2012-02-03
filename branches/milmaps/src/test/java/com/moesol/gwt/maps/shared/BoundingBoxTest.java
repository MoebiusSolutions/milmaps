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
		BoundingBox boundingBox = BoundingBox.builder()
				.top(1.0).left(0.0)
				.bottom(0.0).right(1.0)
				.degrees().build();
		assertTrue(boundingBox.contains(0.5, 0.5));
		assertTrue(boundingBox.contains(0.0, 0.0));
		assertTrue(boundingBox.contains(1.0, 1.0));
	}
	
	@Test
	public void testContains_doesContain_negative() {
		BoundingBox boundingBox = BoundingBox.builder()
				.top(0.0).left(-1.0)
				.bottom(-1.0).right(0.0)
				.degrees().build();
		assertTrue(boundingBox.contains(-0.5, -0.5));
		assertTrue(boundingBox.contains(0.0, 0.0));
		assertTrue(boundingBox.contains(-1.0, -1.0));
	}
	
	@Test
	public void testContains_doesContain_straddlesAntimeridian_negative() {
		BoundingBox boundingBox = BoundingBox.builder().top(4.0).left(-182.0).bottom(0.0).right(-178.0).degrees().build();
		assertTrue(boundingBox.contains(0.5, 180.5));
		assertTrue(boundingBox.contains(0.1, 180.0));
		assertTrue(boundingBox.contains(0.1, 179.0));
		assertTrue(boundingBox.contains(0.2, -180.0));
		assertTrue(boundingBox.contains(0.2, -179.0));
	}
	
	@Test
	public void testContains_doesContain_straddlesAntimeridian_positive() {
		BoundingBox boundingBox = BoundingBox.builder().top(4.0).left(178.0).bottom(0.0).right(182.0).degrees().build();
		assertTrue(boundingBox.contains(0.5, 180.5));
		assertTrue(boundingBox.contains(0.1, 180.0));
		assertTrue(boundingBox.contains(0.1, 179.0));
		assertTrue(boundingBox.contains(0.2, -180.0));
		assertTrue(boundingBox.contains(0.2, -179.0));
	}
	
	@Test
	public void testContains_doesNotContain_positive() {
		BoundingBox boundingBox = BoundingBox.builder().top(1.0).left(0.0).bottom(0.0).right(1.0).degrees().build();
		assertFalse(boundingBox.contains(1.1, 1.1));
		assertFalse(boundingBox.contains(-0.01, 0.0));
		assertFalse(boundingBox.contains(1.0, 1.01));
	}
	
	@Test
	public void testContains_doesNotContain_negative() {
		BoundingBox boundingBox = BoundingBox.builder().top(0.0).left(-1.0).bottom(-1.0).right(0.0).degrees().build();
		assertFalse(boundingBox.contains(-1.1, -1.1));
		assertFalse(boundingBox.contains(0.01, 0.01));
		assertFalse(boundingBox.contains(-1.1, -1.0));
	}
	
	@Test
	public void testContains_doesNotContain_straddlesAntimeridian_negative() {
		BoundingBox boundingBox = BoundingBox.builder().top(4.0).left(-182.0).bottom(0.0).right(-178.0).degrees().build();
		assertFalse(boundingBox.contains(4.0, 182.1));
		assertFalse(boundingBox.contains(4.0, -177.9));
		assertFalse(boundingBox.contains(0.1, Math.nextAfter(178, Double.NEGATIVE_INFINITY)));
	}
	
	@Test
	public void testContains_doesNotContain_straddlesAntimeridian_positive() {
		BoundingBox boundingBox = BoundingBox.builder().top(4.0).left(178.0).bottom(0.0).right(182.0).degrees().build();
		assertFalse(boundingBox.contains(4.0, 182.1));
		assertFalse(boundingBox.contains(4.0, -177.9));
		assertFalse(boundingBox.contains(0.1, Math.nextAfter(178, Double.NEGATIVE_INFINITY)));
	}
	
	@Test
	public void testGetCenterLat_minAndMaxNegative() {
		BoundingBox bbox = BoundingBox.builder().top(-60.0).left(0.0).bottom(-80).right(10.0).degrees().build();
		assertEquals(bbox.getCenterLat(), -70.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLat_minAndMaxPositive() {
		BoundingBox bbox = BoundingBox.builder().top(80.0).left(0.0).bottom(60).right(10.0).degrees().build();
		assertEquals(bbox.getCenterLat(), 70.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLat_minNegative_maxPositive() {
		BoundingBox bbox = BoundingBox.builder().top(70.0).left(0.0).bottom(-80).right(10.0).degrees().build();
		assertEquals(bbox.getCenterLat(), -5.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minAndMaxNegative() {
		BoundingBox bbox = BoundingBox.builder().top(20.0).left(-50.0).bottom(10.0).right(-20).degrees().build();
		assertEquals(bbox.getCenterLng(), -35.0, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minAndMaxPositive() {
		BoundingBox bbox = BoundingBox.builder().top(0.1).left(10.0).bottom(0.0).right(11.0).degrees().build();
		assertEquals(bbox.getCenterLng(), 10.5, EPSILON);
	}
	
	@Test
	public void testGetCenterLon_minNegative_maxPositive() {
		BoundingBox bbox = BoundingBox.builder().top(70.0).left(-80.0).bottom(-80.0).right(70.0).degrees().build();
		assertEquals(bbox.getCenterLng(), -5.0, EPSILON);
	}
	
}
