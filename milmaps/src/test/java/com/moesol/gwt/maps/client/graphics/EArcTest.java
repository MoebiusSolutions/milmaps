/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RngBrg;


public class EArcTest {
	@Before
	public void setUp() throws Exception {
	}
	
	private double compRangeForDeg(double deg, double a, double b){
		double rad = Func.DegToRad(deg);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		if (Func.isClose(sin,0.0,0.0000001)){
			return a;
		}
		if (Func.isClose(cos,0,0.0000001)){
			return b;
		}
		double m = sin/cos;
		double m2 = m*m;
		double a2 = a*a;
		double b2 = b*b;
		double x2 = (a2*b2)/(m2*a2+b2);
		double y2 = b2*(1-(x2/a2));
		return Math.sqrt(x2+y2);
	}
	
	private RngBrg compRngBrg(double brgDeg, double a, double b){
		double rngKm = compRangeForDeg(brgDeg, a, b);
		return new RngBrg(rngKm, brgDeg);
	}
	
	private RngBrg compRngBrg2(double t, double a, double b){
		double angle = Func.DegToRad(t);
		double x = (a * Math.cos(angle));
		double y = (b * Math.sin(angle));
		if (a != b) {
			angle = Math.atan2(y, x);
		}
		double rngKm = Math.sqrt(x*x + y*y);
		return new RngBrg(rngKm,Func.RadToDeg(angle));	
	}
	
	@Test
	public void methodTest() {
		double a = 1200;
		double b = 850;
		double inc = 10;
		for(int i = 0; i < 36; i++){
			RngBrg rb2 = compRngBrg2(i*inc, a, b);
			RngBrg rb  = compRngBrg(rb2.getBearing(), a, b);
			assertEquals(rb2.getRanegKm(), rb.getRanegKm(), 0.001);
		}
	}
}
