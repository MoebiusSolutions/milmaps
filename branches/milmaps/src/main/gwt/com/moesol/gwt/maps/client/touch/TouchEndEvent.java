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
