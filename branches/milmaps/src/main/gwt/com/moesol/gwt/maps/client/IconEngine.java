package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private static double CharWidthInPixels = 10.0;
	private static int CharHeightInPixels = 15; 
	private final IMapView m_mapView;
	
	public IconEngine(IMapView mv) {
		m_mapView = mv;
	}

	public void positionIcons(WidgetPositioner widgetPositioner, DivWorker divWorker) {
		Sample.DIV_POSITION_ICONS.beginSample();
		
		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			positionOneIcon(icon, widgetPositioner, divWorker);
		}
		
		Sample.DIV_POSITION_ICONS.endSample();
	}

	public void positionOneIcon(Icon icon, WidgetPositioner widgetPositioner, DivWorker divWorker) {
		IProjection projection = divWorker.getProjection();
		WorldCoords wc = projection.geodeticToWorld(icon.getLocation());
		DivCoords dc = divWorker.worldToDiv(wc);
		
		Image image = icon.getImage();
		if (image != null) {
			widgetPositioner.place(image, 
					dc.getX() + icon.getIconOffset().getX(), 
					dc.getY() + icon.getIconOffset().getY(), 
					icon.getZIndex());
		}
		
		Label label = icon.getLabel();
		if (label != null) {
			// We may want to move this block of code
			int width = 0;
			int height = 0;
			if (label.getOffsetWidth() > 0) {
				width = label.getOffsetWidth();
				height = label.getOffsetHeight();
			}
			else if (label.getText().length() > 0) {
				width = (int)(label.getText().length()*CharWidthInPixels +0.5);
				height = CharHeightInPixels;
			}
			//// End of block
			widgetPositioner.place(label, 
					dc.getX() + icon.getDeclutterOffset().getX(), 
					dc.getY() + icon.getDeclutterOffset().getY(),
					icon.getZIndex() );
		}
		
		Image leader = icon.getLabelLeaderImage();
		if (leader != null) {
			widgetPositioner.place(leader, 
					dc.getX() - DeclutterEngine.LEADER_IMAGE_WIDTH / 2, 
					dc.getY() - DeclutterEngine.LEADER_IMAGE_HEIGHT / 2,
					icon.getZIndex());
		}
	}

}
