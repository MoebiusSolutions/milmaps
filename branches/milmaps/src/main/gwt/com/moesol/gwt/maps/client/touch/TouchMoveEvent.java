/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
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
