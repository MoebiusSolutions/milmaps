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
