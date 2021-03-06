package com.moesol.gwt.maps.client.util;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** A simplified and streamlined version of {@link java.util.BitSet}. */
public final class BitSet implements Cloneable {

	private final long[] bits;

	public BitSet(int numBits) {
		int numLongs = numBits >>> 6;
		if ((numBits & 0x3F) != 0) {
			numLongs++;
		}
		bits = new long[numLongs];
	}

	private BitSet(long[] bits) {
		this.bits = bits;
	}

	public boolean get(int index) {
		// skipping range check for speed
		return (bits[index >>> 6] & 1L << (index & 0x3F)) != 0L;
	}

	public void set(int index) {
		// skipping range check for speed
		bits[index >>> 6] |= 1L << (index & 0x3F);
	}

	public void clear(int index) {
		// skipping range check for speed
		bits[index >>> 6] &= ~(1L << (index & 0x3F));
	}

	public void clear() {
		int length = bits.length;
		for (int i = 0; i < length; i++) {
			bits[i] = 0L;
		}
	}

	public BitSet clone() {
		return new BitSet(bits);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(64 * bits.length);
		for (long l : bits) {
			for (int j = 0; j < 64; j++) {
				result.append((l & 1L << j) == 0 ? '0' : '1');
			}
			result.append(' ');
		}
		return result.toString();
	}

}