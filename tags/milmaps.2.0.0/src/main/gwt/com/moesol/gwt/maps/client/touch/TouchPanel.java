/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

public class TouchPanel extends SimplePanel {
	
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addHandler(handler, TouchStartEvent.getType());
	}
	
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addHandler(handler, TouchEndEvent.getType());
	}

	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return addHandler(handler, TouchMoveEvent.getType());
	}
	
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return addHandler(handler, TouchCancelEvent.getType());
	}
	
	@Override
	protected void onAttach() {
		super.onAttach();
		sinkTouchEvents(getElement());
	}

	private native void sinkTouchEvents(Element elem) /*-{
		elem.addEventListener('touchstart', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchend', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchmove', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
		elem.addEventListener('touchcancel', @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent, false);
	}-*/;
	
}
