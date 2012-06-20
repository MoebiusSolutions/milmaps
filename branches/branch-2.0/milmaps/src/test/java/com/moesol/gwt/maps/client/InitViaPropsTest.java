/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class InitViaPropsTest {
	static class ManyProps {
		private int m_p1;
		private int m_p2;
		private int m_p3;
		private boolean m_isSetP3;
		
		public void verifyState() {
			if (!m_isSetP3) {
				throw new IllegalStateException("Must init p3");
			}
		}
		public int getP1() {
			return m_p1;
		}
		public void setP1(int p1) {
			m_p1 = p1;
		}
		public int getP2() {
			return m_p2;
		}
		public void setP2(int p2) {
			m_p2 = p2;
		}
		public int getP3() {
			return m_p3;
		}
		public void setP3(int p3) {
			m_p3 = p3;
			m_isSetP3 = true;
		}
		
	}
	
	@Test
	public void testInit() {
		ManyProps p = new ManyProps() {
			{
				setP1(0);
				setP2(1);
				setP3(2);
				verifyState();
			}
		};
		
		assertEquals(0, p.getP1());
		assertEquals(1, p.getP2());
		assertEquals(2, p.getP3());
	}
}
