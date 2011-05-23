package com.moesol.gwt.maps.client.controls;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.HTML;

/**
 * Simple button used by several map controls.
 */
// TODO: probably should just skin normal HTML button objects, I naively implemented this, not realizing at the time
// that you could provide background images for buttons objects.
public class MapButton extends HTML implements MouseDownHandler, MouseUpHandler, MouseOutHandler, MouseOverHandler {
	public MapButton() {
		addStyleName("map-SmallButton");
		
		addMouseDownHandler(this);
		addMouseOutHandler(this);
		addMouseUpHandler(this);
		addMouseOverHandler(this);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		addStyleName("map-SmallButtonMouseOver");
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		removeStyleName("map-SmallButtonMouseOver");
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		removeStyleName("map-SmallButtonMouseDown");
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		addStyleName("map-SmallButtonMouseDown");
	}
}
