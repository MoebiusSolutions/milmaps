/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public class BearingParser {
	protected static String units[] = {"deg", "rad"};
	protected static double RadToDegree = 57.29577951;
	
	private static double radToDegree(double v){
		return	v*RadToDegree;
	}
	
	public static String stripNonDigits(final String input){
	    final StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < input.length(); i++){
	        final char c = input.charAt(i);
	        if((c > 47 && c < 58)|| c == '.'){
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}

	public static Bearing parse( String value){
		String brgstr = value.toLowerCase();
		String num = stripNonDigits(value);
		if ( num.length() > 0 ){
			double d = Double.valueOf(num);
			double v = 0;
			if (brgstr.contains("rad")){
				v = radToDegree(d);
			}
			else if (brgstr.contains("r")){
				v = radToDegree(d);
			}
			else if (brgstr.contains("deg")){
				v = d;
			}
			else if (brgstr.contains("d")){
				v = d;
			}
			else if (brgstr.contains("�")){
				v = d;
			}
			else if (!Double.isNaN(d)){
				v = d;
			}
			else {
				return null;
			}
			if (v < 0.0 || 360 < v){
				return null;
			}
			return Bearing.builder().value(v).degrees().build();
		}
		return null;
	}
}
