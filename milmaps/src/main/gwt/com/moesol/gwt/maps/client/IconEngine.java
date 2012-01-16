package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.IProjection.ZoomFlag;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private final IMapView m_mapView;
	private final WidgetPositioner widgetPositioner;
	
	public IconEngine(IMapView mv) {
		m_mapView = mv;
		widgetPositioner = mv.getWidgetPositioner();
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
		IProjection tempProj = m_mapView.getTempProjection();
		ViewPort viewPort = m_mapView.getViewport();
		
		WorldCoords wc = tempProj.geodeticToWorld(icon.getLocation());
		ViewCoords vc = viewPort.worldToView(wc, false);
		
		int baseX = (int) (scale * vc.getX() - offsetX);
		int baseY = (int) (scale * vc.getY() - offsetY);
		
		Image image = icon.getImage();
		if (image != null) {
			widgetPositioner.place(image, 
					baseX + icon.getIconOffset().getX(), 
					baseY + icon.getIconOffset().getY());
		}
		
		Label label = icon.getLabel();
		if (label != null) {
			widgetPositioner.place(label, 
					baseX + icon.getDeclutterOffset().getX(), 
					baseY + icon.getDeclutterOffset().getY());
		}

		Image leader = icon.getLabelLeaderImage();
		if (leader != null) {
			widgetPositioner.place(leader, 
					baseX - DeclutterEngine.LEADER_IMAGE_WIDTH / 2, 
					baseY - DeclutterEngine.LEADER_IMAGE_HEIGHT / 2);
		}
	}

	public void positionOneIcon(Icon icon) {
		IProjection projection = m_mapView.getProjection();
		WorldCoords wc = projection.geodeticToWorld(icon.getLocation());
		ViewCoords vc = m_mapView.getViewport().worldToView(wc, true);
		
		Image image = icon.getImage();
		if (image != null) {
			widgetPositioner.place(image, 
					vc.getX() + icon.getIconOffset().getX(), 
					vc.getY() + icon.getIconOffset().getY());
		}
		
		Label label = icon.getLabel();
		if (label != null) {
			widgetPositioner.place(label, 
					vc.getX() + icon.getDeclutterOffset().getX(), 
					vc.getY() + icon.getDeclutterOffset().getY());
		}
		
		Image leader = icon.getLabelLeaderImage();
		if (leader != null) {
			widgetPositioner.place(leader, 
					vc.getX() - DeclutterEngine.LEADER_IMAGE_WIDTH / 2, 
					vc.getY() - DeclutterEngine.LEADER_IMAGE_HEIGHT / 2);
		}
	}

}
