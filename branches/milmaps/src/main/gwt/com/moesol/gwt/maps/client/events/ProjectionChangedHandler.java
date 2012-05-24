/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ProjectionChangedHandler extends EventHandler {
	public void onProjectionChanged(ProjectionChangedEvent event);
}
