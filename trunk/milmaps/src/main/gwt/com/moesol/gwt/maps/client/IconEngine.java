package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.IProjection.ZoomFlag;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private final IMapView m_mapView;
	IconPlacer iconPlacer = new IconPlacer() {
		@Override
		public void place(Widget widget, int x, int y) {
			if (widget.getParent() == null) {
				m_mapView.getIconPanel().add(widget, x, y);
			} else {
				m_mapView.getIconPanel().setWidgetPosition(widget, x, y);
			}
		}
	};
	
	interface IconPlacer {
		void place(Widget widget, int x, int y);
	}

	public IconEngine(IMapView mv) {
		m_mapView = mv;
	}

	// RFH: drawIcons is basically the same as positionIcons with extra scale,
	// offsetX, offsetY
	// If animation just called doUpdateView then drawIcons could be removed.
	public void drawIcons(double scale, double offsetX, double offsetY) {
		IProjection projection = m_mapView.getProjection();
		IProjection tempProj = m_mapView.getTempProjection();

		ZoomFlag flag = projection.getZoomFlag();
		double val = (flag == ZoomFlag.IN ? projection.getPrevScale()
				: projection.getScale());
		tempProj.setScale(val);

		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			drawIcon(icon, scale, offsetX, offsetY);
		}
	}

	public void positionIcons() {
		Sample.MAP_POSITION_ICONS.beginSample();
		
		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			positionOneIcon(icon);
		}
		
		Sample.MAP_POSITION_ICONS.endSample();
	}

	// RFH: drawIcon is basically the same as positionOneIcon with extra scale,
	// offsetX, offsetY
	// If animation just called doUpdateView then drawIcon could be removed.
	private void drawIcon(Icon icon, double scale, double offsetX, double offsetY) {
		AbsolutePanel iconPanel = m_mapView.getIconPanel();
		IProjection tempProj = m_mapView.getTempProjection();
		ViewPort viewPort = m_mapView.getViewport();
		
		WorldCoords w = tempProj.geodeticToWorld(icon.getLocation());
		ViewCoords vc = viewPort.worldToView(w, false);
		Image image = icon.getImage();
		int x = (int) (scale * vc.getX() - offsetX)
				+ icon.getIconOffset().getX();
		int y = (int) (scale * vc.getY() - offsetY)
				+ icon.getIconOffset().getY();
		iconPanel.setWidgetPosition(image, x, y);
		Label label = icon.getLabel();
		if (label != null) {
			x -= icon.getIconOffset().getX();
			y -= icon.getIconOffset().getY();
			x += icon.getDeclutterOffset().getX();
			y += icon.getDeclutterOffset().getY();
			iconPanel.setWidgetPosition(label, x, y);
		}
	}

	private void positionOneIcon(Icon icon) {
		AbsolutePanel iconPanel = m_mapView.getIconPanel();
		positionOneIconOn(icon, iconPanel);
	}

	void positionOneIconOn(Icon icon, AbsolutePanel iconPanel) {
		IProjection projection = m_mapView.getProjection();
		WorldCoords v = projection.geodeticToWorld(icon.getLocation());
		ViewCoords portCoords = m_mapView.getViewport().worldToView(v, true);
		Image image = icon.getImage();
		int x = portCoords.getX() + icon.getIconOffset().getX();
		int y = portCoords.getY() + icon.getIconOffset().getY();
		iconPlacer.place(image, x, y);
		Label label = icon.getLabel();
		if (label != null) {
			x -= icon.getIconOffset().getX();
			y -= icon.getIconOffset().getY();
			x += icon.getDeclutterOffset().getX();
			y += icon.getDeclutterOffset().getY();
			iconPlacer.place(label, x, y);
		}
	}

}
