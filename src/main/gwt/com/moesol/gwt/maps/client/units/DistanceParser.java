/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

public class DistanceParser {
	protected static String units[] = {"ft", "km", "km", "mi"};
	protected static double FeetToMeters = 0.30480061;
	
	private static double feetToMeters(double v){
		return	v*FeetToMeters;
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

	public static Distance parse( String value){
		// use regEx to delete non digits from string
		//String num = value.replaceAll("\\D+","");
		String disStr = value.toLowerCase();
		String num = stripNonDigits(value);
		if ( num.length() > 0 ){
			double d = Double.valueOf(num);
			double v = 0;
			if (disStr.contains("ft")){
				v = feetToMeters(d);
			}
			else if (disStr.contains("km")){
				v = Kilometers.asMeters(d);
			}
			else if (disStr.contains("mi")){
				v = Miles.asMeters(d);
			}
			else if (disStr.contains("m")){
				v = d;
			}
			else{
				return null;
			}
			return Distance.builder().value(v).meters().build();
		}
		return null;
	}
}
