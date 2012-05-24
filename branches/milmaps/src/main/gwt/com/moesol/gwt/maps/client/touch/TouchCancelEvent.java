/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

public class TouchCancelEvent extends TouchEvent<TouchCancelHandler> {

    private static final Type<TouchCancelHandler> TYPE =
        new Type<TouchCancelHandler>("touchcancel", new TouchCancelEvent());

    public static Type<TouchCancelHandler> getType() {
        return TYPE;
    }

	@Override
	public com.google.gwt.event.dom.client.DomEvent.Type<TouchCancelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TouchCancelHandler handler) {
		handler.onTouchCancel(this);
	}
}
