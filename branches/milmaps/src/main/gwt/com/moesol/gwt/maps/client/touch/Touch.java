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

import com.google.gwt.core.client.JavaScriptObject;

public class Touch extends JavaScriptObject {

    protected Touch() {
    }

    public final native int pageX() /*-{
		return this.pageX;
	}-*/;
    
    public final native int pageY() /*-{
    	return this.pageY;
    }-*/;
    
}
