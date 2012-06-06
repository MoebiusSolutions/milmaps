/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.stats;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.moesol.gwt.maps.client.Browser;
import com.moesol.gwt.maps.client.DivCoords;
import com.moesol.gwt.maps.client.DivManager;
import com.moesol.gwt.maps.client.DivPanel;
import com.moesol.gwt.maps.client.DivWorker;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.TileCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewDimension;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class MapStateDialog extends DialogBox {
	private FlexTable table = new FlexTable();
	private int row = 0;
	protected MapView m_mapVw;
	public MapStateDialog( MapView mapView ) {
		m_mapVw = mapView;
		IProjection mapProj = mapView.getProjection();
		DivManager divMgr = mapView.getDivManager();
		DivWorker divWorker = divMgr.getCurrentDiv().getDivWorker();
		ViewWorker vw = m_mapVw.getViewport().getVpWorker();
		
		addRow("Div Current Level: ", divMgr.getCurrentLevel() );
		GeodeticCoords gc = vw.getGeoCenter();
		double lat = gc.getPhi(AngleUnit.DEGREES);
		double lng = gc.getLambda(AngleUnit.DEGREES);
		addRow("View Geo Center: lat: ",lat);
		addRow( "View Geo Center: Lng: ", lng);
		gc = divWorker.getGeoCenter();
		lat = gc.getPhi(AngleUnit.DEGREES);
		lng = gc.getLambda(AngleUnit.DEGREES);
		addRow("Div Geo Center: lat: ",lat);
		addRow( "Div Geo Center: Lng: ", lng);
		int offsetXWc = divWorker.getOffsetInWcX();
		int offsetYWc = divWorker.getOffsetInWcY();
		addRow("Div offset X in WC : ",offsetXWc);
		addRow("Div offset Y in WC : ",offsetYWc);
		//AbsolutePanel panel = mapView.getViewPanel();
		//Element el = panel.getElement();
		//int width = el.getClientWidth();
		//int height = el.getClientHeight();
		//addRow("ViewPanel ClientWidth: ", width);
		//addRow("ViewPanel ClientWidth: : ", height);
		//int vbWidth = divMgr.getCurrentDiv().getVbWidth();
		//int vbHeight = divMgr.getCurrentDiv().getVbHeight();
		//addRow("ViewBox Width: ", vbWidth);
		//addRow("ViewBox height: : ", vbHeight);
		//int s = Browser.getFontPixWidth("test");
		//addRow("Font Width: ", s);
		
		addButton(new Button("Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		}));
		add(table);
		getElement().getStyle().setZIndex(10000);
		getElement().getStyle().setOpacity(9.0);
		center();
	}
	private void addSeperator() {
		addRow("------", "------");
	}

	private void addRow(String label, Object value) {
		table.setText(row, 0, label);
		table.setText(row, 1, value.toString());
		row++;
	}
	
	private void addButton(Button button) {
		table.setWidget(row, 1, button);
		row++;
	}
}
