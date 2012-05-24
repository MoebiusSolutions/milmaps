/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client;

import com.google.gwt.user.client.ui.Widget;

public interface WidgetPositioner {
	void remove(Widget widget);
	void place(Widget widget, int divX, int divY, int zindex);
}