package com.moesol.gwt.maps.shared;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TileTest {

	private static final double EPSILON = 0.0000000000001;

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
	public void testBuilder_constructorFields() {
		int level = 4;
		int x = 5;
		int y = 6;
		Tile.Builder builder = new Tile.Builder(level, x, y);
		Tile tile = builder.build();
		
		assertEquals(level, tile.getLevel());
		assertEquals(x, tile.getX());
		assertEquals(y, tile.getY());
	}
	
	@Test
	public void testBuilder_constructorFields_imageWidthAndHeight_specified() {
		int level = 4;
		int x = 5;
		int y = 6;
		int pixelHeight = 128;
		int pixelWidth = 200;
		Tile.Builder builder = new Tile.Builder(level, x, y);
		builder.pixelHeight(pixelHeight).pixelWidth(pixelWidth);
		Tile tile = builder.build();
		
		assertEquals(level, tile.getLevel());
		assertEquals(x, tile.getX());
		assertEquals(y, tile.getY());
		assertEquals(pixelWidth, tile.getPixelWidth());
		assertEquals(pixelHeight, tile.getPixelHeight());
	}
	
	@Test
	public void testBuilder_constructorFields_imageWidthAndHeight_notSpecified() {
		int level = 22;
		int x = 88;
		int y = 33;
		Tile.Builder builder = new Tile.Builder(level, x, y);
		Tile tile = builder.build();
		
		assertEquals(level, tile.getLevel());
		assertEquals(x, tile.getX());
		assertEquals(y, tile.getY());
		assertEquals(512, tile.getPixelWidth());
		assertEquals(512, tile.getPixelHeight());
	}
	
	@Test
	public void testGetTileLongitudeSpan() {
		assertEquals(180, Tile.getTileLongitudeSpan(0), EPSILON);
		assertEquals(90, Tile.getTileLongitudeSpan(1), EPSILON);
		assertEquals(45, Tile.getTileLongitudeSpan(2), EPSILON);
		assertEquals(22.5, Tile.getTileLongitudeSpan(3), EPSILON);
		
		double degrees = 11.25;
		for (int i = 4; i < 50; i++, degrees /= 2.0) {
			assertEquals(degrees, Tile.getTileLongitudeSpan(i), EPSILON);
		}
	}
	
	@Test
	public void testGetTileLatitudeSpan() {
		assertEquals(180, Tile.getTileLatitudeSpan(0), EPSILON);
		assertEquals(90, Tile.getTileLatitudeSpan(1), EPSILON);
		assertEquals(45, Tile.getTileLatitudeSpan(2), EPSILON);
		assertEquals(22.5, Tile.getTileLatitudeSpan(3), EPSILON);
		
		double degrees = 11.25;
		for (int i = 4; i < 50; i++, degrees /= 2.0) {
			assertEquals(degrees, Tile.getTileLatitudeSpan(i), EPSILON);
		}
	}
}
