/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.Widget;

public interface WidgetPositioner {
	void remove(Widget widget);
	void place(Widget widget, int divX, int divY, int zindex);
}