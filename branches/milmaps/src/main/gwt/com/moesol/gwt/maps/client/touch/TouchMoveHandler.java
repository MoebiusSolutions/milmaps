package com.moesol.gwt.maps.client.touch;

import com.google.gwt.event.shared.EventHandler;

public interface TouchMoveHandler extends EventHandler {
	void onTouchMove(TouchMoveEvent event);
}
