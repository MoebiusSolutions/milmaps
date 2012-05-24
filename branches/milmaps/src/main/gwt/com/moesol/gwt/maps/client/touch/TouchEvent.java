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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;

public abstract class TouchEvent<H extends EventHandler> extends DomEvent<H> {

    public JsArray<Touch> touches() {
        return touches(getNativeEvent());
    }

    private native JsArray<Touch> touches(NativeEvent nativeEvent) /*-{
      return nativeEvent.touches;
    }-*/;
}
