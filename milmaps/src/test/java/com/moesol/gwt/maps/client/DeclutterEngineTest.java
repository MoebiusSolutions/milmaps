package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Provider;
import com.moesol.gwt.maps.client.units.Degrees;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ GWT.class, DOM.class })
public class DeclutterEngineTest {
	class IconListBuilder {
		private List<Icon> icons = new ArrayList<Icon>();
		
		public IconListBuilder add(double lat,  double lng, String label) {
			Icon icon = new Icon();
			icon.setLocation(Degrees.geodetic(lat, lng));
			icon.setLabel(label);
			icon.getIconOffset().setX(-8);
			icon.getIconOffset().setY(-8);
			icons.add(icon);
			return this;
		}
		public List<Icon> build() {
			return icons;
		}
	}

	private static final int[] SEARCH_ROW_OFFSETS = {
		0, -1, -2, -3, -4, 1, 2, 3, 4, -5, -6, -7, -8, 5, 6, 7, 8,
		0, -1, -2, -3, -4, 1, 2, 3, 4, -5, -6, -7, -8, 5, 6, 7, 8,
	};
	private static final int[] SEARCH_COL_OFFSETS = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	};
	private IMapView m_mapView;
	private IProjection m_projection = new CylEquiDistProj(512, 180, 180);
	private Label m_labelMock;
	private ViewPort m_viewport = new ViewPort();
	private DeclutterEngine engine;
	
	@Before
	public void before() {
		mockStatic(GWT.class);
		mockStatic(DOM.class);
		
		m_mapView = mock(IMapView.class);
		m_labelMock = mock(Label.class);
		when(m_mapView.getViewport()).thenReturn(m_viewport);
		when(m_mapView.getProjection()).thenReturn(m_projection);
		m_viewport.setProjection(m_projection);
		when(m_labelMock.getOffsetHeight()).thenReturn(16);
		when(m_labelMock.getOffsetWidth()).thenReturn(48);
		
		Icon.IMAGE_PROVIDER = new Provider<Image>() {
			@Override
			public Image get() {
				return null;
			}
		};
		Icon.LABEL_PROVIDER = new Provider<Label>() {
			@Override
			public Label get() {
				return m_labelMock;
			}
		};
		Icon.LABEL_STYLER = mock(Icon.LabelStyler.class);
		
		/* Reset to match test in case we use different values later */
		DeclutterEngine.CELL_WIDTH = 16;
		DeclutterEngine.CELL_HEIGHT = 16;
		DeclutterEngine.SEARCH_ROW_OFFSETS = SEARCH_ROW_OFFSETS;
		DeclutterEngine.SEARCH_COL_OFFSETS = SEARCH_COL_OFFSETS;
		
		engine = new DeclutterEngine(m_mapView);
	}
	
	@Test
	public void testRoundUp() {
		assertEquals(1, DeclutterEngine.roundUp(1, 4));
		assertEquals(1, DeclutterEngine.roundUp(3, 4));
		assertEquals(1, DeclutterEngine.roundUp(4, 4));
		
		assertEquals(2, DeclutterEngine.roundUp(5, 4));
		assertEquals(2, DeclutterEngine.roundUp(7, 4));
		assertEquals(2, DeclutterEngine.roundUp(8, 4));
	}

	@Test
	public void testMakeBitSet() {
		int n = engine.makeBitSet();
		assertEquals(25, engine.m_nRowsInView);
		assertEquals(19, engine.m_nColsInView);
		assertEquals(25 * 19, n);
	}
	
	@Test
	public void testComputeIconCenterViewCoords() {
		Icon icon = new Icon();
		icon.setLocation(Degrees.geodetic(0, 0));
		icon.getIconOffset().setX(0);
		icon.getIconOffset().setY(0);

		ViewCoords vc = engine.computeIconCenterViewCoords(icon);
		
		assertEquals(300, vc.getX());
		assertEquals(200, vc.getY());
		
		icon.getIconOffset().setX(1);
		vc = engine.computeIconCenterViewCoords(icon);
		assertEquals(301, vc.getX());
		assertEquals(200, vc.getY());
	}
	@Test
	public void when_two_labels_at_same_position_then_second_is_offset() {
		List<Icon> icons = builder().add(0, 0, "Label1").add(0, 0, "Label2").build();
		engine.declutter(icons);
		
		assertEquals(12, icons.get(0).getDeclutterOffset().getX());
		assertEquals(0, icons.get(0).getDeclutterOffset().getY());
		assertEquals(12, icons.get(1).getDeclutterOffset().getX());
		assertEquals(-16, icons.get(1).getDeclutterOffset().getY());
	}

	@Test
	public void when_two_labels_not_near_then_neither_is_offset() {
		List<Icon> icons = builder().add(0, 0, "Label1").add(5, 0, "Label2").build();
		engine.declutter(icons);
		
		assertEquals(12, icons.get(0).getDeclutterOffset().getX());
		assertEquals(0, icons.get(0).getDeclutterOffset().getY());
		assertEquals(12, icons.get(1).getDeclutterOffset().getX());
		assertEquals(-2, icons.get(1).getDeclutterOffset().getY());
	}
	
	@Test
	public void testAllRightHandLabels() {
		IconListBuilder builder = builder();
		for (int i = 0; i < 17; i++) {
			builder.add(0, 0, "label" + i);
		}
		List<Icon> icons = builder.build();
		engine.declutter(icons);
		
		int n = 0;
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(0, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-16, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-32, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-48, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-64, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(16, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(32, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(48, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(64, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-80, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-96, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-112, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-128, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(80, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(96, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(112, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(12, icons.get(n).getDeclutterOffset().getX());
		assertEquals(128, icons.get(n++).getDeclutterOffset().getY());
	}
	
	@Test
	public void testAllLeftHandLabels() {
		IconListBuilder builder = builder();
		for (int i = 0; i < 34; i++) {
			builder.add(0, 0, "label" + i);
		}
		List<Icon> icons = builder.build();
		engine.declutter(icons);
		
		int n = 17;
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(0, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-16, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-32, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-48, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-64, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(16, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(32, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(48, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(64, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-80, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-96, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-112, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(-128, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(80, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(96, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(112, icons.get(n++).getDeclutterOffset().getY());
		assertEquals(-20, icons.get(n).getDeclutterOffset().getX());
		assertEquals(128, icons.get(n++).getDeclutterOffset().getY());
	}
	
	private IconListBuilder builder() {
		return new IconListBuilder();
	}

}
