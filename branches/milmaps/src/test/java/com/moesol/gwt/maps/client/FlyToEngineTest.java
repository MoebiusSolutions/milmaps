package com.moesol.gwt.maps.client;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class FlyToEngineTest {
	
	private CylEquiDistProj m_proj;
	private ViewPort m_viewport;
	private IMapView m_mapview = mock(IMapView.class); 
	FlyToEngine m_engine = new FlyToEngine(m_mapview);

	@Before
	public void before() {
		m_proj = new CylEquiDistProj(512, 180.0, 180.0);
		//MapScale temp = new JmvMapScale(proj.getBaseEquatorialScale());
		m_proj.zoomByFactor(2.0);
		//MapScale temp2 = new JmvMapScale(proj.getEquatorialScale());
		//divMgr = new DivManager(mapView);
		WorldCoords wc = new WorldCoords(512, 256);
		m_viewport = new ViewPort();
		ViewDimension vd = new ViewDimension(600, 400);
		ViewWorker vw = m_viewport.getVpWorker();
		vw.intialize(vd, m_proj);	
		vw.setCenterInWc(wc);
		//divMgr.setViewWorker(vw);
		when(m_mapview.getProjection()).thenReturn(m_proj);
		when(m_mapview.getViewport()).thenReturn(m_viewport);
		//when(mapView.getDivManager()).thenReturn(divMgr);
	}

	@Test
	public void when_animation_starts_then_zoom_nothing() {
		double orig = m_proj.getEquatorialScale();
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		m_engine.initEngine(gc, 1/50000.0);
		m_engine.onUpdate(0.0);
		
		assertEquals(orig, m_proj.getEquatorialScale(), 0.0);
	}
	
	@Test
	public void test_preTeleport_scale() {
		double expectedScale = m_proj.getEquatorialScale();
		expectedScale *= FlyToEngine.ZOOM_OUT_FACTOR;
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		m_engine.initEngine(gc, 1/50000.0);
		m_engine.onUpdate(FlyToEngine.ZOOM_OUT_UNTIL);
		double preTeleportScale = m_proj.getEquatorialScale();
		MapScale result = new JmvMapScale(preTeleportScale);
		MapScale expect = JmvMapScale.parse("1:110.9M");
		assertEquals(preTeleportScale,expectedScale,0.00000001);
		assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void test_preTeleport_Position() {
		double preTeleportScale = m_proj.getEquatorialScale();
		preTeleportScale *= FlyToEngine.ZOOM_OUT_FACTOR;
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		m_engine.initEngine(gc, 1/50000.0);
		m_engine.onUpdate(FlyToEngine.ZOOM_OUT_UNTIL);
		ViewWorker vw = m_viewport.getVpWorker();
		GeodeticCoords pt = vw.getGeoCenter();
		assertEquals(-9.84375, pt.getPhi(AngleUnit.DEGREES),0.00001);
		assertEquals(-37.265625, pt.getLambda(AngleUnit.DEGREES),0.00001);
	}
	
	@Test
	public void test_teleport_Scale() {
		double preTeleportScale = m_proj.getEquatorialScale();
		preTeleportScale *= FlyToEngine.ZOOM_OUT_FACTOR;
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		IProjection proj = m_mapview.getProjection().cloneProj();
		double projScale = 1/50000.0;
		m_engine.initEngine(gc, projScale);
		m_engine.onUpdate(FlyToEngine.ZOOM_PAUSE);
		proj.setEquatorialScale(projScale);
		int level = proj.getLevelFromScale(projScale, 0);
		double teleportScale = proj.getScaleFromLevel(level);
		assertEquals(11,level);
		assertEquals(teleportScale,m_proj.getEquatorialScale(),0.00000001);
	}
	
	@Test
	public void test_teleport_Pt() {
		double preTeleportScale = m_proj.getEquatorialScale();
		preTeleportScale *= FlyToEngine.ZOOM_OUT_FACTOR;
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		double projScale = 1/50000.0;
		m_engine.initEngine(gc, projScale);
		m_engine.onUpdate(FlyToEngine.ZOOM_PAUSE);
		ViewWorker vw = m_viewport.getVpWorker();
		GeodeticCoords pt = vw.getGeoCenter();
		assertEquals(19.9999782, pt.getPhi(AngleUnit.DEGREES),0.00001);
		assertEquals(29.9643065, pt.getLambda(AngleUnit.DEGREES),0.00001);
	}
	
	@Test
	public void test_final_destination() {
		double preTeleportScale = m_proj.getEquatorialScale();
		preTeleportScale *= FlyToEngine.ZOOM_OUT_FACTOR;
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		double projScale = 1/50000.0;
		m_engine.initEngine(gc, projScale);
		m_engine.onUpdate(1.0);
		ViewWorker vw = m_viewport.getVpWorker();
		GeodeticCoords pt = vw.getGeoCenter();
		assertEquals(20, pt.getPhi(AngleUnit.DEGREES),0.0001);
		assertEquals(30, pt.getLambda(AngleUnit.DEGREES),0.0001);
		double scale = m_proj.getEquatorialScale();
		MapScale result = new JmvMapScale(scale);
		MapScale expect = JmvMapScale.parse("1:50K");
		assertEquals(expect.toString(), result.toString());
	}

	@Test
	public void test_go_left_right_stay() {
		FlyToEngine.Dir move;
		// Centered -170.
		move = m_engine.getDirection(-172, -170, -168, -174);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(-172, -170, -168, 170);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(-172, -170, -168, -166);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		move = m_engine.getDirection(-172, -170, -168, -169);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(-172, -170, -168, -171);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(-172, -170, -168, 2);
		assertEquals(FlyToEngine.Dir.RIGHT,move);	
		move = m_engine.getDirection(-172, -170, -168, -2);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		// Centered 170.
		move = m_engine.getDirection(168, 170, 172, -174);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		move = m_engine.getDirection(168, 170, 172, 174);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		move = m_engine.getDirection(168, 170, 172, 167);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(168, 170, 172, 171);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(168, 170, 172, 169);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(168, 170, 172, 2);
		assertEquals(FlyToEngine.Dir.LEFT,move);	
		move = m_engine.getDirection(168, 170, 172, -2);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		// Centered 180.
		move = m_engine.getDirection(178, 180, -178, 176);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(178, 180, -178, 179);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(178, 180, -178, -179);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(178, 180, -178, -176);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		
		// Centered -180.
		move = m_engine.getDirection(178, -180, -178, 176);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(178, -180, -178, 179);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(178, -180, -178, -179);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(178, -180, -178, -176);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		
		// Centered -2.
		move = m_engine.getDirection(-6, -2, 2, -7);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(-6, -2, 2, 0);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(-6, -2, 2, 1);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(-6, -2, 2, 3);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		move = m_engine.getDirection(-6, -2, 2, -170);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(-6, -2, 2, 170);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		
		// Centered 2.
		move = m_engine.getDirection(-2, 2, 6, -7);
		assertEquals(FlyToEngine.Dir.LEFT,move);
		move = m_engine.getDirection(-2, 2, 6, 0);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(-2, 2, 6, 1);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(-2, 2, 6, 8);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		move = m_engine.getDirection(-2, 2, 6, -170);
		assertEquals(FlyToEngine.Dir.LEFT,move);	
		move = m_engine.getDirection(-2, 2, 6, 170);
		assertEquals(FlyToEngine.Dir.RIGHT,move);
		
		// Centered 0.
		move = m_engine.getDirection(-170, 0, 170, -7);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(-170, 0, 170, 0);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(-170, 0, 170, 8);
		assertEquals(FlyToEngine.Dir.STAY,move);
		move = m_engine.getDirection(-170, 0, 170, -170);
		assertEquals(FlyToEngine.Dir.STAY,move);	
		move = m_engine.getDirection(-170, 0, 170, 170);
		assertEquals(FlyToEngine.Dir.STAY,move);
	}

	/*
	@Test
	public void when_zoom_out_full_then_target_zoom_out_scale() {
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(FlyToEngine.ZOOM_OUT_UNTIL);
		
		MapScale expect = JmvMapScale.parse("1:100M");
		MapScale result = new JmvMapScale(proj.getEquatorialScale());
		assertEquals(false,false);
		//assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void when_1_percent_then_pan_started() {
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(0.01 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertTrue(cap.getValue().getPhi(AngleUnit.DEGREES) > 0.0);
		//assertTrue(cap.getValue().getLambda(AngleUnit.DEGREES) > 0.0);
	}

	@Test
	public void when_100_percent_then_pan_finished() {
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(1.0);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(20.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.00001);
		//assertEquals(30.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}
	
	@Test
	public void when_51_percent_then_zoom_in_started() {
		GeodeticCoords gc = new GeodeticCoords(20,30,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(FlyToEngine.ZOOM_IN_AT + 0.1);
		
		MapScale result = new JmvMapScale(proj.getEquatorialScale());
		MapScale expect = JmvMapScale.parse("1:1.4M");
		assertEquals(false,false);
		//assertEquals(expect.toString(), result.toString());
	}
	
	@Test
	public void when_100_percent_then_pan_finished_and_zoom_in_finished() {
		GeodeticCoords gc = new GeodeticCoords(30,20,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(1.0);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(20.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.00001);
		//assertEquals(30.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
		//assertEquals(1/50000.0, proj.getEquatorialScale(), 0.1);
	}
	
	@Test
	public void when_crossing_180_right_to_left_at_start_then_moving_shortest_direction() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(-179.0, 0.0, AngleUnit.DEGREES));
		GeodeticCoords gc = new GeodeticCoords(179.0,0.0,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(0.35 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		//assertEquals(-179.48054, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}

	@Test
	public void when_crossing_180_right_to_left_at_finish_then_pan_finsish() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(-179.0, 0.0, AngleUnit.DEGREES));
		GeodeticCoords gc = new GeodeticCoords(179.0,0.0,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		//assertEquals(179.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.001);
	}

	@Test
	public void when_crossing_180_left_to_right_at_start_then_moving_shortest_direction() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(179.0, 0.0, AngleUnit.DEGREES));
		GeodeticCoords gc = new GeodeticCoords(-179.0,0.0,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(0.35 * FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		//assertEquals(179.48054, cap.getValue().getLambda(AngleUnit.DEGREES), 0.00001);
	}

	@Test
	public void when_crossing_180_left_to_right_at_finish_then_pan_finsish() {
		viewport.getVpWorker().setGeoCenter(new GeodeticCoords(179.0, 0.0, AngleUnit.DEGREES));
		GeodeticCoords gc = new GeodeticCoords(-179.0,0.0,AngleUnit.DEGREES);
		engine.initEngine(gc, 1/50000.0);
		engine.onUpdate(FlyToEngine.PAN_UNTIL);
		
		ArgumentCaptor<GeodeticCoords> cap = ArgumentCaptor.forClass(GeodeticCoords.class);
		//verify(mapView).setCenter(cap.capture());
		assertEquals(false,false);
		//assertEquals(0.0, cap.getValue().getPhi(AngleUnit.DEGREES), 0.0);
		//assertEquals(-179.0, cap.getValue().getLambda(AngleUnit.DEGREES), 0.001);
	}
	*/
}
