package com.moesol.gwt.maps.client;

import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.moesol.gwt.maps.client.stats.Sample;

public class IconEngine {
	private static double CharWidthInPixels = 10.0;
	private static int CharHeightInPixels = 15; 
	private final IMapView m_mapView;
	private final ViewWorker m_viewWorker;
	private final DivManager m_divMgr;
	
	private DivCoords m_divAjustment;
	
	public IconEngine(IMapView mv) {
		m_mapView = mv;
		m_viewWorker = m_mapView.getViewport().getVpWorker();
		m_divMgr = m_mapView.getDivManager();
	}

	public void positionIcons(WidgetPositioner widgetPositioner, DivWorker divWorker) {
		Sample.DIV_POSITION_ICONS.beginSample();
		
		List<Icon> icons = m_mapView.getIconLayer().getIcons();
		for (Icon icon : icons) {
			positionOneIcon(icon, widgetPositioner, divWorker);
		}
		
		Sample.DIV_POSITION_ICONS.endSample();
	}
	
	public DivCoords getIconDivCoords( DivWorker dw, GeodeticCoords gc){
		ViewCoords vc = m_viewWorker.geodeticToView(gc);
		return m_viewWorker.viewToDivCoords(dw, vc);
	}

	public void positionOneIcon(Icon icon, WidgetPositioner widgetPositioner, 
														    DivWorker divWorker) {
		DivCoords dc = getIconDivCoords(divWorker, icon.getLocation());
		
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
