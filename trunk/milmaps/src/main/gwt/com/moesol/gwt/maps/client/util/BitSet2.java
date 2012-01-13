package com.moesol.gwt.maps.client.util;

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