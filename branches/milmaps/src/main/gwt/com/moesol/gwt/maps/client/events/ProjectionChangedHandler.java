package com.moesol.gwt.maps.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface ProjectionChangedHandler extends EventHandler {
	public void onProjectionChanged(ProjectionChangedEvent event);
}
