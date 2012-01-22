package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private final IMapView m_mapView;
	private final WidgetPositioner widgetPositioner;
	
	public IconEngine(IMapView mv) {
		m_mapView = mv;
		widgetPositioner = mv.getWidgetPositioner();
	}

	public void positionIcons() {
		Sample.MAP_POSITION_ICONS.beginSample();
		
		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			positionOneIcon(icon);
		}
		
		Sample.MAP_POSITION_ICONS.endSample();
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
