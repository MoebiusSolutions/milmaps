/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

public class TouchEndEvent extends TouchEvent<TouchEndHandler> {

    private static final Type<TouchEndHandler> TYPE =
        new Type<TouchEndHandler>("touchend", new TouchEndEvent());

    public static Type<TouchEndHandler> getType() {
        return TYPE;
    }

	@Override
	public com.google.gwt.event.dom.client.DomEvent.Type<TouchEndHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TouchEndHandler handler) {
		handler.onTouchEnd(this);
	}
}
