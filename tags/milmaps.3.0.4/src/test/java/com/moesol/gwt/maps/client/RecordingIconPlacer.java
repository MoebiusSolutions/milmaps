/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.moesol.maps.tools.Placement;

public class RecordingIconPlacer implements WidgetPositioner {

	List<Placement> images = new ArrayList<Placement>();
	List<Placement> labels = new ArrayList<Placement>();

	public void place(Widget widget, int x, int y, int zindex) {
		Placement p = new Placement();
		p.w = widget;
		p.x = x;
		p.y = y;
		p.zindex = zindex;

		if (widget instanceof Image) {
			images.add(p);
		} else {
			labels.add(p);
		}
	}

	@Override
	public void remove(Widget widget) {
	}

}
