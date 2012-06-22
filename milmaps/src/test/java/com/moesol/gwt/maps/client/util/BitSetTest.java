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