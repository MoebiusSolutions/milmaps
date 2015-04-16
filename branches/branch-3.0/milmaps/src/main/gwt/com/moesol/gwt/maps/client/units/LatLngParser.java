/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.units;

/*
 * #%L
 * milmaps
 * %%
 * Copyright (C) 2015 Moebius Solutions, Inc.
 * %%
 * Copyright 2015 Moebius Solutions Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #L%
 */


import com.moesol.gwt.maps.client.GeodeticCoords;

public class LatLngParser {
	private static double INVALID = -99999.0;
	private static char[] m_delim = {'\u00B0', '\'', '\"', ' ' };
	protected static class SignIndex {
		public int sign;
		public int n;
	};



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

	public static GeodeticCoords parse(String input){
		GeodeticCoords gc = parseStr(input);
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

	public static void testNewParse(String inputStr){
		parseLatorLng(inputStr, m_delim );
	}

	private static String replace(String value, char[] c){
		for ( int i = 0; i < c.length; i++){
			value = value.replace(c[i], ',');
		}
		// next remove duplicate ","
		value = value.replace(",,", ",");
		return value;
	}

	public static double parseLatorLng(String inputStr, char[] delim ){
		int n = inputStr.indexOf('.');
		if (-1 < n && n < 4){
			return INVALID;
		}
		inputStr = replace(inputStr,m_delim);
		inputStr = inputStr.trim();
		String[] substr = new String[4];
		int k = 0;
		for( int j = 0; j < m_delim.length; j++){
			int i = inputStr.indexOf(',');
			if (i > 0){
				k++;
				substr[j] = inputStr.substring(0,i);
				inputStr = inputStr.substring(i+1);
			}

		}

		double dd = INVALID;
		if (k > 0) {
			dd = parseDouble(substr[0]);
			double div = 60.0;
			for ( int i = 1; i < k; i++){
				dd += parseDouble(substr[i])/div;
				div *= 60.0;
			}
		}
		return dd;
	}

	/*
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
			}
		}
		return dd;
	}
	*/

	private static SignIndex nsIndex(String str){
		SignIndex si = new LatLngParser.SignIndex();
	    si.n = str.indexOf("n");
	    si.sign = 1;
	    if (si.n == -1){
		   si.n = str.indexOf("s");
		   si.sign = -1;
	   }
	   return si;
	}

	private static SignIndex ewIndex(String str){
		SignIndex si = new LatLngParser.SignIndex();
	    si.n = str.indexOf("e");
	    si.sign = 1;
	    if (si.n == -1){
		   si.n = str.indexOf("w");
		   si.sign = -1;
	   }
	   return si;
	}

	private static GeodeticCoords parseStr(String input){
		int length = input.length();
		String lowerStr = input.toLowerCase();
		SignIndex nsJ = nsIndex(lowerStr);
		if (nsJ.n == -1){
			return null;
		}
		SignIndex ewJ = ewIndex(lowerStr);
		if ( ewJ.n < nsJ.n){
			return null;
		}
		int n = nsJ.n;
		String latStr = lowerStr.substring(0, n+1).trim();
		// Check for space between last NSEWS and last digit
		latStr = correctSpace(latStr);
		double latVal = parseLatorLng(latStr, m_delim )*nsJ.sign;
		if (latVal == INVALID){
			return null;
		}

		String lngStr = lowerStr.substring(n+1,length).trim();
		lngStr = correctSpace(lngStr);
		double lngVal = parseLatorLng(lngStr, m_delim )*ewJ.sign;
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
		String lowerStr = input.toLowerCase();
		SignIndex nsJ = nsIndex(lowerStr);
		if (nsJ.n == -1){
			return null;
		}
		SignIndex ewJ = ewIndex(lowerStr);
		if ( ewJ.n <= nsJ.n){
			return null;
		}
		int n = nsJ.n;
		double lat = INVALID;
		double lng = INVALID;
		String s = stripNonDigits(lowerStr.substring(0,n));
		lat = parseDouble(s)*nsJ.sign;
		s = stripNonDigits(lowerStr.substring(n+1,ewJ.n));
		lng = parseDouble(s)*ewJ.sign;

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

// These don't work in GWT
//String m_pattern = "^(?<deg>[-+0-9]+)[^0-9]+(?<min>[0-9]+)[^0-9]+(?<sec>[0-9.,]+)[^0-9.,ENSW]+(?<pos>[ENSW]*)$";
//String m_patternNoSecs = "^(?<deg>[-+0-9]+)[^0-9]+(?<min>[0-9]+)[^0-9.,ENSW]+(?<pos>[ENSW]*)$";
