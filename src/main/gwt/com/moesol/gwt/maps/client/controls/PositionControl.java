package com.moesol.gwt.maps.client.controls;

import mil.geotransj.GeoTransException;
import mil.geotransj.Geodetic;
import mil.geotransj.Mgrs;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.Composite;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.LatLonString;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

/**
 * Hook to MapView to display the Latitude, Longitude, and MGRS position of the
 * mouse over the map.
 */
public class PositionControl extends Composite {
	private final OutlinedLabel m_mousePosLabel = new OutlinedLabel();
	private final Mgrs m_mgrs = new Mgrs();
	private final Geodetic m_geo = new Geodetic();
	private final MapView m_mapView;
	private final ViewCoords m_vc = new ViewCoords();
	
	public PositionControl(MapView mapView) {
		m_mapView = mapView;
		initWidget(m_mousePosLabel);
		
		MouseMoveHandler handler = new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				mouseMove(event.getX(), event.getY());
			}
		};
		addStyleName("map-PositionControl");
		m_mapView.addDomHandler(handler, MouseMoveEvent.getType());
	}
	
	public void mouseMove( int x, int y ) {
		m_vc.set(x,y);
		GeodeticCoords gc = m_mapView.getProjection().viewToGeodetic(m_vc);
		String pos = LatLonString.build(gc.getPhi(AngleUnit.DEGREES), 
										gc.getLambda(AngleUnit.DEGREES));
		String mgrsPos = "";
		try {
			m_geo.setLatitude(gc.getPhi(AngleUnit.DEGREES));
			m_geo.setLongitude(gc.getLambda(AngleUnit.DEGREES));
			mgrsPos = m_mgrs.GeodeticToMgrs(m_geo, 5);
		} catch (GeoTransException e) {
			mgrsPos = "";
		}
		m_mousePosLabel.setText( pos + " " + mgrsPos );
	}
	
}
