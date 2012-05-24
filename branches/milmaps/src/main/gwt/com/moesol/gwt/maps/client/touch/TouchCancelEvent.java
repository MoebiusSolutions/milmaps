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
