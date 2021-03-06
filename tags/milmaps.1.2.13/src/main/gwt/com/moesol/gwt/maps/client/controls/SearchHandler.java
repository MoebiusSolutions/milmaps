package com.moesol.gwt.maps.client.controls;

import com.google.gwt.event.shared.EventHandler;

/**
 * Implemented by classes capable of handling search events.
 */
public interface SearchHandler extends EventHandler {
	void onSearch(SearchEvent searchEvent);
}
