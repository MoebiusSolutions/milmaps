package com.moesol.gwt.maps.client.place;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

public class MapsPlaceController extends PlaceController {
	@Inject
	public MapsPlaceController(EventBus eventBus) {
		super(eventBus);
	}
}
