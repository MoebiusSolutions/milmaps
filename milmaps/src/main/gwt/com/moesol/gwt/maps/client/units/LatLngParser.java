/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.moesol.gwt.maps.client.GeodeticCoords;

public class LatLngParser {
	private static double INVALID = -99999.0;
	private static String m_pattern = "^(?<deg>[-+0-9]+)[^0-9]+(?<min>[0-9]+)[^0-9]+(?<sec>[0-9.,]+)[^0-9.,ENSW]+(?<pos>[ENSW]*)$";
	private static String m_patternNoSecs = "^(?<deg>[-+0-9]+)[^0-9]+(?<min>[0-9]+)[^0-9.,ENSW]+(?<pos>[ENSW]*)$";

	public static GeodeticCoords parse(String input){
		GeodeticCoords gc = parseStr(input, m_pattern);
		if (gc == null){
			gc = parseStr(input, m_patternNoSecs);
		}
		if (gc == null){
			gc = parseDegStr(input);
		}
		if (gc == null){
			gc = parseDecimalDegStr(input);
		}
		return gc;
	}
	
	private static double parseDouble(String val){
		if (!val.matches(".*[a-zA-Z!@#$%*~_=?]+.*")){
			return Double.valueOf(val);
		}
		else {
			return INVALID;
		}
	}
	
	public static double parseLatorLng(String inputStr, String pattern ){
		int n = inputStr.indexOf('.');
		if (-1 < n && n < 4){
			return INVALID;
		}
		RegExp regExp = RegExp.compile(pattern);
		MatchResult matcher = regExp.exec(inputStr);
		double dd = INVALID;
		if (matcher != null) {
		    // Get all groups for this match
			n = matcher.getGroupCount();
			if (n > 1){
				String s = matcher.getGroup(1);
				dd = parseDouble(s);
				double div = 60.0;
				for ( int i = 2; i < n-1; i++){
					s = matcher.getGroup(i);
					dd += parseDouble(s)/div;
					div *= 60.0;
				}
				s = matcher.getGroup(n-1);
				if ( s.equalsIgnoreCase("S") || s.equalsIgnoreCase("W")){
					dd *= -1;
				}
			}
		}
		return dd;
	}
	
	private static GeodeticCoords parseStr(String input, String pattern){
		int length = input.length();
		int n = input.indexOf("N");
		if (n == -1){
			n = input.indexOf("S");
		}
		if (n == -1){
			return null;
		}
		n += 1;
		String latStr = input.substring(0, n).trim();
		// Check for space between last NSEWS and last digit
		latStr = correctSpace(latStr);
		double latVal = parseLatorLng(latStr, pattern );
		if (latVal == INVALID){
			return null;
		}
		
		String lngStr = input.substring(n+1,length);
		lngStr = correctSpace(lngStr);
		double lngVal = parseLatorLng(lngStr, pattern );
		if(lngVal == INVALID){
			return null;
		}
		if (latVal < -90 || 90 < latVal){
			return null;
		}
		if (lngVal < -180 || 180 < lngVal){
			return null;
		}
		return new GeodeticCoords(lngVal,latVal,AngleUnit.DEGREES);
	}
	
	private static String correctSpace(String str){
		int n = str.length();
		char c = str.charAt(n-1);
		int k = n-2;
		char space = str.charAt(k);
		if (space != ' '){
			String s = str.substring(0, k+1);
			str =  s + " " + c;
		}
		return str;
	}
	
	private static GeodeticCoords parseDegStr(String input){
		int len = input.length();
		int sign = 1;
		int n = input.indexOf('N');
		if (n == -1){
			sign = -1;
			n = input.indexOf('S');
		}
		if (n == -1){
			return null;
		}
		double lat = INVALID;
		double lng = INVALID;
		String s = input.substring(0,n-1).trim();
		lat = parseDouble(s)*sign;
		input = input.substring(n+1,len);
		sign = 1;
		n = input.indexOf('E');
		if (n == -1){
			sign = -1;
			n = input.indexOf('W');
		}
		if (n == -1){
			return null;
		}
		s = input.substring(0,n-1).trim();
		lng = parseDouble(s)*sign;
		
		if (lat < -90 || 90 < lat){
			return null;
		}
		if (lng < -180 || 180 < lng){
			return null;
		}
		
		return new GeodeticCoords(lng,lat,AngleUnit.DEGREES);
	}
	
	private static GeodeticCoords parseDecimalDegStr(String input){
		int len = input.length();
		int n = input.indexOf(',');
		
		if (n == -1){
			n = input.indexOf(' ');
		}
		if (n == -1){
			return null;
		}

		double lat = INVALID;
		double lng = INVALID;
		String s = input.substring(0,n).trim();
		lat = parseDouble(s);
		s = input.substring(n+1,len).trim();
		lng = parseDouble(s);
		if (lat < -90 || 90 < lat){
			return null;
		}
		if (lng < -180 || 180 < lng){
			return null;
		}
		return new GeodeticCoords(lng,lat,AngleUnit.DEGREES);
	}
}
