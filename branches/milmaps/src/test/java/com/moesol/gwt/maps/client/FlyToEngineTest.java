package com.moesol.gwt.maps.client;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.moesol.gwt.maps.client.units.AngleUnit;

public class FlyToEngineTest {
	
	private CylEquiDistProj proj;
	private ViewPort viewport;
	private IMapView mapView = mock(IMapView.class);
	FlyToEngine engine = new FlyToEngine(mapView);

	@Before
	public void before() {
		proj = new CylEquiDistProj(512, 180.0, 180.0);
		viewport = new ViewPort();
		viewport.getVpWorker().setProjection(proj);
		when(mapView.getProjection()).thenReturn(proj);
		when(mapView.getViewport()).thenReturn(viewport);
	}

	@Test
	public void when_animation_starts_then_zoom_nothing() {
		double orig = proj.getEquatorialScale();
		
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(0.0);
		
		assertEquals(orig, proj.getEquatorialScale(), 0.0);
	}
	
	@Test
	public void when_zoom_out_half_then_correct_scale() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(FlyToEngine.ZOOM_OUT_UNTIL / 2);
		
		MapScale result = new JmvMapScale(proj.getEquatorialScale());
		MapScale expect = JmvMapScale.parse("1:107.4M");
		
		assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void when_zoom_out_full_then_target_zoom_out_scale() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(FlyToEngine.ZOOM_OUT_UNTIL);
		
		MapScale expect = JmvMapScale.parse("1:100M");
		MapScale result = new JmvMapScale(proj.getEquatorialScale());
		
		assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void when_1_percent_then_pan_started() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(0.01 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertTrue(cap.getValue().getPhi(AngleUnit.DEGREES) > 0.0);
		assertTrue(cap.getValue().getLambda(AngleUnit.DEGREES) > 0.0);
	}

	@Test
	public void when_100_percent_then_pan_finished() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(1.0);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(20.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.00001);
		assertEquals(30.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}
	
	@Test
	public void when_51_percent_then_zoom_in_started() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(FlyToEngine.ZOOM_IN_AT + 0.1);
		
		MapScale result = new JmvMapScale(proj.getEquatorialScale());
		MapScale expect = JmvMapScale.parse("1:1.4M");
		assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void when_100_percent_then_pan_finished_and_zoom_in_finished() {
		engine.initEngine(20, 30, 1/50000.0);
		engine.onUpdate(1.0);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(20.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.00001);
		assertEquals(30.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
		assertEquals(1/50000.0, proj.getEquatorialScale(), 0.1);
	}
	
	@Test
	public void when_crossing_180_right_to_left_at_start_then_moving_shortest_direction() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(-179.0, 0.0, AngleUnit.DEGREES));
		engine.initEngine(0.0, 179.0, 1/50000.0);
		engine.onUpdate(0.35 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		assertEquals(-179.438992, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}

	@Test
	public void when_crossing_180_right_to_left_at_finish_then_pan_finsish() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(-179.0, 0.0, AngleUnit.DEGREES));
		engine.initEngine(0.0, 179.0, 1/50000.0);
		engine.onUpdate(FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		assertEquals(179.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.001);
	}

	@Test
	public void when_crossing_180_left_to_right_at_start_then_moving_shortest_direction() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(179.0, 0.0, AngleUnit.DEGREES));
		engine.initEngine(0.0, -179.0, 1/50000.0);
		engine.onUpdate(0.35 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		assertEquals(179.438992, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}

	@Test
	public void when_crossing_180_left_to_right_at_finish_then_pan_finsish() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(179.0, 0.0, AngleUnit.DEGREES));
		engine.initEngine(0.0, -179.0, 1/50000.0);
		engine.onUpdate(FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		assertEquals(-179.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.001);
	}
}
