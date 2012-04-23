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
import com.moesol.gwt.maps.client.DivWorker;
import com.moesol.gwt.maps.client.IProjection;
import com.moesol.gwt.maps.client.MapView;
import com.moesol.gwt.maps.client.TileCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.ViewDimension;
import com.moesol.gwt.maps.client.ViewPort;
import com.moesol.gwt.maps.client.ViewWorker;
import com.moesol.gwt.maps.client.WorldCoords;

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
		
		addRow("Map Projection Scale: ", mapProj.getEquatorialScale());
		addRow("Div Current Level: ", divMgr.getCurrentLevel() );
		ViewPort vp = mapView.getViewport();
		ViewDimension vd = vp.getVpWorker().getDimension();
		addRow("View width: ", vd.getWidth());
		addRow("View height: ", vd.getHeight());
		AbsolutePanel panel = mapView.getViewPanel();
		Element el = panel.getElement();
		int width = el.getClientWidth();
		int height = el.getClientHeight();
		addRow("ViewPanel ClientWidth: ", width);
		addRow("ViewPanel ClientWidth: : ", height);
		int s = Browser.getFontPixWidth("test");
		addRow("Font Width: ", s);
		WorldCoords wcVb = divMgr.getCurrentDiv().getVBOffsetWc();
		ViewCoords vc = divMgr.getCurrentDiv().getDivTopLeft();
		TileCoords ct = divMgr.getCurrentDiv().getCenterTile();
		int offsetXWc = vw.getOffsetInWcX();
		int offsetYWc = vw.getOffsetInWcY();
		addRow("ViewOffset X in Wc: ", offsetXWc);
		if (wcVb != null){
			addRow("ViewBoxOffset X in Wc: ", wcVb.getX());
		}
		addRow("ViewBoxOffset X in Div: ", ct.getOffsetX());
		addRow("DivOffset X in view : ", vc.getX());
		
		addRow("ViewOffset Y in Wc: ", offsetYWc);
		if (wcVb != null){
			addRow("ViewBoxOffset Y in Wc: ", wcVb.getY());	
		}
		addRow("ViewBoxOffset Y in Div: ", ct.getOffsetY());
		addRow("DivOffset Y in view : ", vc.getY());
		addSeperator();
		
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
