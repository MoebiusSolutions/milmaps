package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.shared.BoundingBox;

public class ProjectionsTest {
	
	private ViewPort viewPort = new ViewPort();
	private GeodeticCoords origGeo;
	private ViewCoords origView;
	private IProjection origProj;
	private int origWidth;
	private int origHeight;
	
	@Before
	public void before() {
		IProjection proj = new CylEquiDistProj(512, 180, 180);
		viewPort.setProjection(proj);
	}
	
	@After
	public void after() {
//		System.out.println("origProj2 = " + origProj);
//		System.out.println("viewPort2 = " + viewPort);
		
		assertSame(origProj, viewPort.getProjection());
		assertEquals(origWidth, viewPort.getWidth());
		assertEquals(origHeight, viewPort.getHeight());
		assertEquals(origGeo, viewPort.getProjection().getViewGeoCenter());
		assertEquals(origView, viewPort.worldToView(new WorldCoords(), true));
	}
	
	private void recordViewPort() {
		origProj = viewPort.getProjection();
		origGeo = viewPort.getProjection().getViewGeoCenter();
		origView = viewPort.worldToView(new WorldCoords(), true);
		origWidth = viewPort.getWidth();
		origHeight = viewPort.getHeight();
		
//		System.out.println("origProj1 = " + origProj);
//		System.out.println("viewPort1 = " + viewPort);
	}

	@Test
	public void given_zoomed_out_when_find_small_box_then_larget_scale() {
		recordViewPort();
		
		BoundingBox box = new BoundingBox(12, 10, 10, 12);
		double start = viewPort.getProjection().getScale();
		double found = Projections.findScaleFor(viewPort, box);
		assertTrue(found > start);
	}
	
	@Test
	public void when_box_empty_then_same_scale() {
		recordViewPort();
		
		double wholeWorld = viewPort.getProjection().getScale();
		BoundingBox box = new BoundingBox(-80, 12, -80, 12);
		assertEquals(wholeWorld, Projections.findScaleFor(viewPort, box), 0.0);
	}

	@Test
	public void given_zoomed_in_when_find_large_box_then_smaller_scale() {
		viewPort.getProjection().zoomByFactor(100);
		// After modifying projection reset into viewPort to get viewPort to update
		viewPort.setProjection(viewPort.getProjection());

		recordViewPort();
		
		BoundingBox box = new BoundingBox(80, 12, -80, 12);
		double start = viewPort.getProjection().getScale();
		double found = Projections.findScaleFor(viewPort, box);
		assertTrue(found < start);
	}
	
}
