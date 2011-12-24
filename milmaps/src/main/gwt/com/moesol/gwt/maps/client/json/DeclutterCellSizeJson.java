package com.moesol.gwt.maps.client.json;

import com.google.gwt.core.client.JavaScriptObject;

public class DeclutterCellSizeJson extends JavaScriptObject {
	// Overlay types always have protected, zero-arg ctors
	protected DeclutterCellSizeJson() {
	}

	public final native int getWidth() /*-{ return this.width; }-*/;
	public final native int getHeight() /*-{ return this.height; }-*/;
}
