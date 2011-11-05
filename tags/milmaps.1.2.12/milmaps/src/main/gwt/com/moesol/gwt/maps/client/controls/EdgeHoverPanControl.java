package com.moesol.gwt.maps.client.controls;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Timer;
import com.moesol.gwt.maps.client.MapView;

public class EdgeHoverPanControl implements HoverHandler {
	/**
	 * Encapsulates code that can be tested with regular JUnit tests.
	 */
	static class Presenter {
		private static final int MIN_HOVER_RADII = 4;
		private int m_hoverRadiusPixels;
		private int m_panIntervalMillis;
		private int m_maxPanPixels;
		private int m_dx;
		private int m_dy;
		private boolean m_panning;

		private EdgeHoverPanControl m_control;

		public Presenter(EdgeHoverPanControl control, int hoverRadiusPixels,
				int panIntervalMillis, int maxPanPixels) {
			m_control = control;
			m_hoverRadiusPixels = hoverRadiusPixels;
			m_panIntervalMillis = panIntervalMillis;
			m_maxPanPixels = maxPanPixels;
		}

		public int getDeltaX() {
			return m_dx;
		}

		public int getDeltaY() {
			return m_dy;
		}

		public void onHover(int x, int y, int mapWidth, int mapHeight,
				int mapLeft, int mapTop, boolean justCheckCancel) {
			if (mapWidth >= m_hoverRadiusPixels * MIN_HOVER_RADII
					&& mapHeight >= m_hoverRadiusPixels * MIN_HOVER_RADII) {
				int mapRight = mapWidth;
				int mapBottom = mapHeight;

				int distFromLeft = x;
				int distFromRight = mapRight - x;

				int distFromTop = y;
				int distFromBottom = mapBottom - y;

				if (distFromLeft >= 0 && distFromRight >= 0 && distFromTop >= 0
						&& distFromBottom >= 0) {
					int dx = 0;
					if (distFromLeft < m_hoverRadiusPixels) {
						float magnitude = (float) (distFromLeft - m_hoverRadiusPixels)
								/ (float) m_hoverRadiusPixels;
						dx = Math.round(magnitude * m_maxPanPixels);
					} else if (distFromRight < m_hoverRadiusPixels) {
						float magnitude = (float) (m_hoverRadiusPixels - distFromRight)
								/ (float) m_hoverRadiusPixels;
						dx = Math.round(magnitude * m_maxPanPixels);
					} else {
						dx = 0;
					}

					int dy = 0;
					if (distFromTop < m_hoverRadiusPixels) {
						float magnitude = (float) (distFromTop - m_hoverRadiusPixels)
								/ (float) m_hoverRadiusPixels;
						dy = Math.round(magnitude * m_maxPanPixels);
					} else if (distFromBottom < m_hoverRadiusPixels) {
						float magnitude = (float) (m_hoverRadiusPixels - distFromBottom)
								/ (float) m_hoverRadiusPixels;
						dy = Math.round(magnitude * m_maxPanPixels);
					} else {
						dy = 0;
					}

					if (!justCheckCancel) {
						m_dx = dx;
						m_dy = dy;
					}

					if (!(dx == 0 && dy == 0)) {
						if (!m_panning) {
							m_control.startPanLoop(m_panIntervalMillis);
							m_panning = true;
						}
						return;
					}
				}
			}
			m_control.cancelPanLoop();
			m_panning = false;
		}
	}

	private MapView m_map;

	private Timer m_panTimer = new Timer() {
		@Override
		public void run() {
			m_map.moveMapByPixels(m_presenter.getDeltaX(),
					m_presenter.getDeltaY());
		}
	};

	private Presenter m_presenter;

	public EdgeHoverPanControl(MapView map, int hoverRadiusPixels,
			int panIntervalMillis, int maxPanPixels) {
		m_map = map;

		m_presenter = new Presenter(this, hoverRadiusPixels, panIntervalMillis,
				maxPanPixels);

		m_map.getController().addHoverHandler(this);

		m_map.getFocusPanel().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				cancelPanLoop();
			}
		});

		m_map.getFocusPanel().addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent arg0) {
				cancelPanLoop();
			}
		});

		m_map.getFocusPanel().addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent e) {
				if (m_presenter.m_panning) {
					// just want to check if pan should be canceled
					onHover(e.getX(), e.getY(), true);
				}
			}
		});
	}

	@Override
	public void onHover(HoverEvent hoverEvent) {
		onHover(hoverEvent.getX(), hoverEvent.getY(), false);
	}

	private void onHover(int x, int y, boolean justCheckCancel) {
		int mapWidth = m_map.getOffsetWidth();
		int mapHeight = m_map.getOffsetHeight();
		int mapLeft = m_map.getAbsoluteLeft();
		int mapTop = m_map.getAbsoluteTop();

		m_presenter.onHover(x, y, mapWidth, mapHeight, mapLeft, mapTop,
				justCheckCancel);
	}

	void startPanLoop(int panIntervalMillis) {
		m_panTimer.run();
		m_panTimer.scheduleRepeating(panIntervalMillis);
		m_presenter.m_panning = true;
	}

	void cancelPanLoop() {
		m_presenter.m_panning = false;
		m_panTimer.cancel();
	}
}
