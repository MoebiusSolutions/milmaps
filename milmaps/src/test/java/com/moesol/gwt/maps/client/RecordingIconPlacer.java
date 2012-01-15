package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.gwt.maps.client.IconEngine.IconPlacer;
import com.moesol.maps.tools.Placement;

public class RecordingIconPlacer implements IconPlacer {

	List<Placement> images = new ArrayList<Placement>();
	List<Placement> labels = new ArrayList<Placement>();

	public void place(Widget widget, int x, int y) {
		Placement p = new Placement();
		p.w = widget;
		p.x = x;
		p.y = y;
		p.width = widget.getOffsetWidth();
		p.height = widget.getOffsetHeight();

		if (widget instanceof Image) {
			images.add(p);
		} else {
			labels.add(p);
		}
	}

}