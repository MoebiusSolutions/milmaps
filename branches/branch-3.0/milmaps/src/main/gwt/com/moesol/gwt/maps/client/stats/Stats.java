/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.stats;

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



public class Stats {
	private static int numGeodeticToWorld;
	private static int numWorldToView;
	private static int numViewToWorld;
	private static int numWorldToGeodetic;
	private static int numViewToGeodetic;
	
	private static int numNewGeodeticCoords;
	private static int numNewWorldCoords;
	private static int numNewViewCoords;
	
	private static int numLabelLeaderImageOutstanding;
	
	private Stats() {}
	
	public static void incrementGeodeticToWorld() {
		numGeodeticToWorld++;
	}
	public static void incrementWorldToView() {
		numWorldToView++;
	}
	
	public static void incrementViewToWorld() {
		numViewToWorld++;
	}
	public static void incrementWorldToGeodetic() {
		numWorldToGeodetic++;
	}
	
	public static void incrementViewToGeodetic() {
		numViewToGeodetic++;
	}

	public static void incrementNewGeodeticCoords() {
		numNewGeodeticCoords++;
	}
	public static void incrementNewWorldCoords() {
		numNewWorldCoords++;
	}
	public static void incrementNewViewCoords() {
		numNewViewCoords++;
	}

	public static int getNumGeodeticToWorld() {
		return numGeodeticToWorld;
	}
	public static int getNumWorldToView() {
		return numWorldToView;
	}

	public static int getNumViewToWorld() {
		return numViewToWorld;
	}
	public static int getNumWorldToGeodetic() {
		return numWorldToGeodetic;
	}
	
	public static int getNumViewToGeodetic() {
		return numViewToGeodetic;
	}
	

	public static int getNumNewGeodeticCoords() {
		return numNewGeodeticCoords;
	}

	public static int getNumNewWorldCoords() {
		return numNewWorldCoords;
	}

	public static int getNumNewViewCoords() {
		return numNewViewCoords;
	}

	public static void incrementLabelLeaderImageOutstanding() {
		numLabelLeaderImageOutstanding++;
	}
	public static void decrementLabelLeaderImageOutstanding() {
		numLabelLeaderImageOutstanding--;
	}
	public static int getNumLabelLeaderImageOutstanding() {
		return numLabelLeaderImageOutstanding;
	}

	public static void reset() {
		for (Sample s : Sample.values()) {
			s.reset();
		}
		
		numNewGeodeticCoords = 0;
		numNewViewCoords = 0;
		numNewWorldCoords = 0;
		
		numGeodeticToWorld = 0;
		numViewToGeodetic = 0;
		numViewToWorld = 0;
		numWorldToGeodetic = 0;
		numWorldToView = 0;
		
		numLabelLeaderImageOutstanding = 0;
	}

}
