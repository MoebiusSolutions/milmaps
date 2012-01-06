package com.moesol.gwt.maps.client.stats;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;

public class StatsDialogBox extends DialogBox {
	private FlexTable table = new FlexTable();
	private int row = 0;
	
	public StatsDialogBox() {
		addRow("geodeticToWorld calls:", Stats.getNumGeodeticToWorld());
		addRow("worldToView calls:",  Stats.getNumWorldToView());
		addSeperator();
		addRow("viewToWorld calls:", Stats.getNumViewToWorld());
		addRow("worldToGeodetic calls:", Stats.getNumWorldToGeodetic());
		addSeperator();
		addRow("viewToGeodetic calls:", Stats.getNumViewToGeodetic());
		addSeperator();
		addRow("new GeodeticCoord calls:", Stats.getNumNewGeodeticCoords());
		addRow("new WorldCoord calls:", Stats.getNumNewWorldCoords());
		addRow("new ViewCoord calls:", Stats.getNumNewViewCoords());
		addSeperator();
		for (Sample s : Sample.values()) {
			addRow(s.toString(), 
					s.getNumCalls() + " calls, avg " + 
					s.getAvgCallMillis() + " millis, total " + 
					s.getMillisSum() + " millis");
		}
		
		addButton(new Button("Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		}));
		addButton(new Button("Reset", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Stats.reset();
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
