/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.util;

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


/**
 * Use an array of boolean instead of bit twiddling to see if declutter is faster on IE7.
 * @author hastings
 */
public final class BitSet2 implements Cloneable {
	
	private final boolean[] bits;

	public BitSet2(int numBits) {
		bits = new boolean[numBits];
	}

	public boolean get(int index) {
		return bits[index];
	}

	public void set(int index) {
		bits[index] = true;
	}

	public void clear(int index) {
		bits[index] = false;
	}

	public void clear() {
		int length = bits.length;
		for (int i = 0; i < length; i++) {
			bits[i] = false;
		}
	}

}