/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
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
