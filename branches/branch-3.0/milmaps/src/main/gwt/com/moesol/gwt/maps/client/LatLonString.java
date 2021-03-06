/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

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


public class LatLonString {

	/**
	 * This function converts a degree in lat or lon
	 * @param val
	 * @param degMax : 10 if converting a lat and 100 if converting a lon.
	 * @return String value of lat or lon
	 */
	private static String builDegMinSecs( double val, double degMax ){
	    double tVal = Math.abs(val);
	    double deg = Math.floor(tVal);
	    tVal = (tVal - deg)*60;
	    double min = Math.floor(tVal);
	    double sec = (tVal-min)*60;
	    sec = Math.floor((sec+0.005)*100)/100;
	    String degStr = ( deg < degMax ? "0" + (int)deg : (int)deg + "");
	    String minStr = ( min < 10 ? "0" + (int)min : (int)min + "");
	    String secStr = ( sec < 10 ? "0" + sec : sec + "");
	    if ( secStr.length() < 3 ){
	        secStr += ".00";
	    }
	    else if (secStr.length() < 5 ){
	        secStr += "0";
	    }
	    return ( degStr + "\u00B0" + minStr + "\'" + secStr + "\"" );
	}
	
	/**
	 * This function converts a degree in lat or lon
	 * @param val
	 * @param degMax : 10 if converting a lat and 100 if converting a lon.
	 * @return String value of lat or lon
	 */
	private static String builSpacDegMinSecs( double val, double degMax ){
	    double tVal = Math.abs(val);
	    double deg = Math.floor(tVal);
	    tVal = (tVal - deg)*60;
	    double min = Math.floor(tVal);
	    double sec = (tVal-min)*60;
	    sec = Math.floor((sec+0.005)*100)/100;
	    String degStr = ( deg < degMax ? "0" + (int)deg : (int)deg + "");
	    String minStr = ( min < 10 ? "0" + (int)min : (int)min + "");
	    String secStr = ( sec < 10 ? "0" + sec : sec + "");
	    if ( secStr.length() < 3 ){
	        secStr += ".00";
	    }
	    else if (secStr.length() < 5 ){
	        secStr += "0";
	    }
	    return ( degStr + " " + minStr + " " + secStr + " " );
	}
	/**
	 * This function will take the lat/lon doubles
	 * and convert it to degs mins decimal seconds.
	 * @param lat
	 * @param lon
	 * @return The formatted string
	 */
	public static String build( double lat, double lon ){
	    String latStr = builDegMinSecs(lat,10) + (lat < 0 ?  "S " : "N ");
	    String lonStr = builDegMinSecs(lon,100) + (lon < 0 ? "W " : "E ");
	    return (latStr + " " + lonStr);
	}

	/**
	 * This function will take the lat/lon doubles
	 * and convert it to degs mins decimal seconds.
	 * @param lat
	 * @param lon
	 * @return The formatted string
	 */
	public static String build2( double lat, double lon ){
	    String latStr = builSpacDegMinSecs(lat,10) + (lat < 0 ?  "S " : "N ");
	    String lonStr = builSpacDegMinSecs(lon,100) + (lon < 0 ? "W " : "E ");
	    return (latStr + " " + lonStr);
	}
}
