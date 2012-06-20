/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

import com.google.gwt.event.shared.EventHandler;

public interface TouchStartHandler extends EventHandler {
	void onTouchStart(TouchStartEvent event);
}
