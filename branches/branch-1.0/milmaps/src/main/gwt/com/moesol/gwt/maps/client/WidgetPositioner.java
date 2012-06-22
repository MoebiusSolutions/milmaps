package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.Widget;

public interface WidgetPositioner {
	void place(Widget widget, int x, int y);
	void remove(Widget widget);
}