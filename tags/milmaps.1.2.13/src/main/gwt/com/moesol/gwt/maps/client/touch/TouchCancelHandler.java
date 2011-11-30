package com.moesol.gwt.maps.client.touch;

import com.google.gwt.event.shared.EventHandler;

public interface TouchCancelHandler extends EventHandler {
	void onTouchCancel(TouchCancelEvent event);
}
