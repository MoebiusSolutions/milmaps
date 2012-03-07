package com.moesol.gwt.maps.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Label;

public class Browser {
	/**
	 * Gets the navigator.appName.
	 *
	 * @return the window's navigator.appName.
	 */
	public enum Type {
		IE_7,
		IE_8,
		IE_9,
		CHROME,
		FF,
		OTHER
	}
	
	public static class FontSize{
		private int width;
		private int height;
		public FontSize(int w, int h){
			width = w;
			height = h;
		}
		public int getWidth(){ return width; }
		public int getHeight(){ return height; }
	}
	
	public static native String userAgent() /*-{
	  return navigator.userAgent.toLowerCase();
	}-*/;
	
	public static Type getBrowser(){
		String cs = userAgent();
		if (cs.contains("msie 7")) {
			return Type.IE_7;
		}
		if (cs.contains("msie 8")) {
			return Type.IE_8;
		}
		if (cs.contains("msie 9")) {
			return Type.IE_9;
		}
		if (cs.contains("chrome")) {
			return Type.CHROME;
		}
		if (cs.contains("firefox")){
			return Type.FF;
		}
		return Type.OTHER;
	}

	public static String getBrowserName(){
		Type type = getBrowser();
		if (type == Type.IE_7) {
			return "ie 7";
		}
		if (type == Type.IE_8) {
			return "ie 8";
		}
		if (type == Type.IE_9) {
			return "ie 9";
		}
		if (type == Type.CHROME) {
			return "chrome";
		}
		if (type == Type.FF){
			return "firefox";
		}
		return "other";
	}

	public static boolean isBrowserIE7or8(){
		Type t = getBrowser();
		if (t == Type.IE_7 || t == Type.IE_8) {
			return true;
		}
		return false;
	}
	
	public static native int getFontPixWidth(String str) /*-{
		var h = document.getElementsByTagName("BODY")[0];
		var d = document.createElement("DIV");
		var s = document.createElement("SPAN");
		d.appendChild(s);
		//d.style.fontFamily = "sans";			//font for the parent element DIV.
		//s.style.fontFamily = "sans";			//serif font used as a comparator.
		//s.style.fontSize   = "72px";			//we test using 72px font size, we may use any size. I guess larger the better.
		s.innerHTML        = "mmmmmmmmmmlil";	//we use m or w because these two characters take up the maximum width. 
												//And we use a L so that the same matching fonts can get separated
		h.appendChild(d);
		var defaultWidth   = s.offsetWidth;		//now we have the defaultWidth
		var defaultHeight  = s.offsetHeight;	//and the defaultHeight, we compare other fonts with these.
		h.removeChild(d);
		return defaultWidth;
	}-*/;

	public static FontSize getFontSize(){
		Label l = new Label();
		l.setText("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz");
		Element e = l.getElement();
		int width = e.getClientWidth()/52;
		int height = e.getClientHeight();
		return new FontSize(width,height);
	}
}
