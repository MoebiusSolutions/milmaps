/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.touch;

public class TouchMoveEvent extends TouchEvent<TouchMoveHandler> {

    private static final Type<TouchMoveHandler> TYPE =
        new Type<TouchMoveHandler>("touchmove", new TouchMoveEvent());

    public static Type<TouchMoveHandler> getType() {
        return TYPE;
    }

	@Override
	public com.google.gwt.event.dom.client.DomEvent.Type<TouchMoveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TouchMoveHandler handler) {
		handler.onTouchMove(this);
	}
}
