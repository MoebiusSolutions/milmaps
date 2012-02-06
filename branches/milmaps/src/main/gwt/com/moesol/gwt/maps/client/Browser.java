package com.moesol.gwt.maps.client;

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
}
