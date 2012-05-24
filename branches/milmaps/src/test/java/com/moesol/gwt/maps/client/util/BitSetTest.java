/**
 * (c) Copyright, Moebius Solutions, Inc., 2006
 *
 *                        All Rights Reserved
 *
 * This material may be reproduced by or for the U. S. Government
 * pursuant to the copyright license under the clause at
 * DFARS 252.227-7014 (OCT 2001).
 */
package com.moesol.gwt.maps.client.util;

import static org.junit.Assert.*;

import org.junit.Test;

public final class BitSetTest {

	private static final int NUM_BITS = 100;

	@Test
	public void testGetSet() {
		BitSet bitSet = new BitSet(NUM_BITS);
		for (int i = 0; i < NUM_BITS; i++) {
			assertFalse(bitSet.get(i));
		}
		bitSet.set(0);
		bitSet.set(NUM_BITS - 1);
		assertTrue(bitSet.get(0));
		assertTrue(bitSet.get(NUM_BITS - 1));
	}

	@Test
	public void testBounds() {
		BitSet bitSet = new BitSet(NUM_BITS);
		try {
			bitSet.set(1000);
			fail("Should have thrown exception");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			// continue
		}
		try {
			bitSet.set(-1);
			fail("Should have thrown exception");
		} catch (ArrayIndexOutOfBoundsException aioobe) {
			// continue
		}
	}

	@Test
	public void testClear() {
		BitSet bitSet = new BitSet(NUM_BITS);
		for (int i = 0; i < NUM_BITS; i++) {
			bitSet.set(i);
		}
		for (int i = 0; i < NUM_BITS; i++) {
			assertTrue(bitSet.get(i));
		}
		bitSet.clear();
		for (int i = 0; i < NUM_BITS; i++) {
			assertFalse(bitSet.get(i));
		}
	}

	@Test
	public void testClone() {
		BitSet bitSet = new BitSet(NUM_BITS);
		bitSet.set(NUM_BITS - 1);
		bitSet = bitSet.clone();
		assertTrue(bitSet.get(NUM_BITS - 1));
	}

}