/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.json;

import com.google.gwt.core.client.JavaScriptObject;

public class DeclutterSearchOffsetJson extends JavaScriptObject {
	// Overlay types always have protected, zero-arg ctors
	protected DeclutterSearchOffsetJson() {
	}

	public final native int getRowOffset() /*-{ return this.row; }-*/;
	public final native int getColOffset() /*-{ return this.col; }-*/;
}
