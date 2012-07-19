/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

import java.util.Arrays;
import java.util.LinkedList;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.MapCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class Spline {
	//
	static public void ComputeCoefficients(double u, double[] c){
		for (int j=0;j<c.length;j++) {
			c[j] = 1.0;
			for (int i=0;i<c.length;i++) {
				if (j != i) {
					c[j] *= (double)(u-i)/(double)(j-i);
				}
			}
		}
	}
	//
	static public MapCoords ComputePoint(ViewCoords[] pnts, double[] c)
	{
		double x = 0.0;
		double y = 0.0;
		for (int j=0;j<pnts.length;j++) {
			x += c[j]*pnts[j].getX();
			y += c[j]*pnts[j].getY();
		}
		return new MapCoords(x,y);
	}
	//
	static public GeodeticCoords ComputeSpline(GeodeticCoords[] p, double[] c)
	{
		double lat = 0.0;
		double lng = 0.0;
		for (int j=0;j<p.length;j++) {
			lat += c[j]*p[j].getPhi(AngleUnit.DEGREES);
			lng += c[j]*p[j].getLambda(AngleUnit.DEGREES);
		}
		return(new GeodeticCoords(lng,lat,AngleUnit.DEGREES));
	}
	//
	public static GeodeticCoords branch(GeodeticCoords p, 
							     GeodeticCoords refPt) {
		double lat = p.getPhi(AngleUnit.DEGREES);
		double lng = p.getLambda(AngleUnit.DEGREES);
		while (lng >= (refPt.getLambda(AngleUnit.DEGREES)+180.0)){ 
			lng-=360;
		}
		while (lng <  (refPt.getLambda(AngleUnit.DEGREES)-180.0)){
			lng+=360;
		}
		return new GeodeticCoords(lng,lat,AngleUnit.DEGREES);
	}
	
	//
	static public int GetSplinePoints(GeodeticCoords[] p,
									  LinkedList<GeodeticCoords> pnts)
	{
		int n = p.length;
		double u = 0.0;
		double umax = 0.0;
		double diff = 0.1;
		int k;
		int m=4;
		//
		// Make sure they are in the same branch.
		//
		for (int i=1;i<n;i++) {
			p[i] = branch(p[i],p[i-1]);
		}
		if (n < 4) m=n;

		for (int i=0;i<(n-1);i++) {
			if (i == 0) {
				u = 0.0;
				umax = 1.0;
				k = i;
			}
			else if (i==(n-2)) {
				if (m==4) {
					u = 2.0+diff;
					umax = 3.0;
					k = i-2;
				}
				else {
					u = 1.0+diff;
					umax = 2.0;
					k = i-1;
				}
			}
			else {
				u = 1.0+diff;
				umax = 2.0;
				k = i-1;
			}
			double c[] = new double[m];
			while (u <= umax) {
				ComputeCoefficients(u, c);
				GeodeticCoords q[] = new GeodeticCoords[m];
				for (int nn=0,mm=k;nn<m;nn++,mm++) {
					q[nn] = p[mm];
				}
				GeodeticCoords qq = ComputeSpline(q, c);
				pnts.add(qq);
				if (u == umax) break;
				u += diff;
				if (u > umax) u = umax;
			}
		}
		pnts.add(p[n-1]);
		return(pnts.size());
	}
	//
	static public int findPointIndex(
		RangeBearingS rb,
		GeodeticCoords p,
		GeodeticCoords[] pnts,
		int start
	)
	{
		double d = Double.MAX_VALUE;
		int index = -1;
		for (int i=start;i<pnts.length;i++) {
			double distKm = rb.gcDistanceFromTo(p,pnts[i]);
			if (distKm < d) {
				d = distKm;
				index = i;
			}
			if (d == 0.0) break;
		}
		return(index);
	}
	
	//
	//
	public static double getLength(
		RangeBearingS rb,
		GeodeticCoords[] pnts,
		int start, 
		int end
	) {
		double length = 0;
		for (int i=start, ii=start+1;ii<end;i++,ii++) {
			// Skip identical points
			if (!pnts[i].equals(pnts[ii])) {
				double rng = rb.gcDistanceFromTo(pnts[i],pnts[ii]);
				length += rng;
			}
		}
		return(length);
	}
	//
	public static GeodeticCoords computeIntermediate(
		RangeBearingS rb,	
		GeodeticCoords[] pnts, 
		double dist
	) {
		double total = 0.0;
		GeodeticCoords result;
		for (int i=0,ii=1; ii<pnts.length; i++,ii++) {
			RngBrg BR = rb.gcRngBrgFromTo(pnts[i],pnts[ii]);
			
			if (total + BR.getRanegKm() > dist) {
				double diff = (dist-total);
				result = rb.gcPointFrom(pnts[i],BR.getBearing(),diff);
				BR = rb.gcRngBrgFromTo(result,pnts[ii]);
				return result;
			} else {
				total += BR.getRanegKm();
			}
		}
		return null;
	}
	//
	public static GeodeticCoords getMidPoint(
		RangeBearingS rb,
		GeodeticCoords p, 
		GeodeticCoords q
		) {
		double brg = rb.gcBearingFromTo(p, q);
		double rng = rb.gcDistanceFromTo(p, q)/2.0;
		return rb.gcPointFrom(p, brg, rng);
	}
	
	//
	static public GeodeticCoords getSplinedMidPoint(
		RangeBearingS rb,
		GeodeticCoords p,
		GeodeticCoords q,
		GeodeticCoords[] pnts
	)
	{
		int pIndex = findPointIndex(rb,p,pnts,0);
		if (pIndex == -1) {
			return(rb.gcMidpointFromTo(p, q));
		}
		int qIndex = findPointIndex(rb,q,pnts,pIndex+1);
		if (qIndex == -1) {
			return(rb.gcMidpointFromTo(p,q));
		}
		double len1 = getLength(rb, pnts,0,pIndex);
		double len2 = getLength(rb, pnts,pIndex,qIndex);
		double len = len1+len2/2;
		GeodeticCoords result = computeIntermediate(rb,pnts,len);
		if ( result != null) {
			return(result);
		}
		return getMidPoint(rb,p,q);
	}
	//
	static public int GetSplinePoints(
		GeodeticCoords[] p,
		int startIndex,
		int endIndex,
		LinkedList<GeodeticCoords> pnts
	)
	{
		int n = p.length;
		double u = 0.0;
		double umax = 0.0;
		double diff = 0.1;
		int k;
		int m=4;
		//
		// Make sure they are in the same branch.
		//
		for (int i=1;i<n;i++) {
			p[i] = branch(p[i],p[i-1]);
		}
		if (n < 4) m=n;

		for (int i=startIndex;i<endIndex;i++) {
			if (i == 0) {
				u = 0.0;
				umax = 1.0;
				k = i;
			}
			else if (i==(endIndex-1)) {
				if (m==4) {
					u = 2.0+diff;
					umax = 3.0;
					k = i-2;
				}
				else {
					u = 1.0+diff;
					umax = 2.0;
					k = i-1;
				}
			}
			else {
				u = 1.0+diff;
				umax = 2.0;
				k = i-1;
			}
			double c[] = new double[m];
			while (u<=umax) {
				ComputeCoefficients(u, c);
				GeodeticCoords q[] = new GeodeticCoords[m];
				for (int nn=0,mm=k;nn<m;nn++,mm++) {
					q[nn] = p[mm];
				}
				GeodeticCoords qq = ComputeSpline(q, c);
				pnts.add(qq);
				if (u == umax) break;
				u += diff;
				if (u > umax) u = umax;
			}
		}
		pnts.add(p[endIndex]);
		return(pnts.size());
	}
	//
	static public GeodeticCoords[] SplinePolygon(GeodeticCoords[] pnts) {
		LinkedList<GeodeticCoords> list = new LinkedList<GeodeticCoords>();
		GetSplinePoints(pnts,list);
		return(list.toArray(new GeodeticCoords[0]));
	}
	//
	static public GeodeticCoords[] SplinePolygon(
			GeodeticCoords[] pnts, int start,int end) {
		LinkedList<GeodeticCoords> list = new LinkedList<GeodeticCoords>();
		GetSplinePoints(pnts,start,end,list);
		return(list.toArray(new GeodeticCoords[0]));
	}
	//
	
	static protected GeodeticCoords[] copyPnts(GeodeticCoords[] p){
		int n = p.length;
		if (n > 0){
			GeodeticCoords[]  pnts = new GeodeticCoords[n];
			int k = 0;
			for(int j = 0; j < n; j++){
				if( p[j] != null){
					pnts[k++] = new GeodeticCoords(p[j].getLambda(AngleUnit.RADIANS),
											 	   p[j].getPhi(AngleUnit.RADIANS),
											       AngleUnit.RADIANS);
				}
			}
			return pnts;
		}
		return null;
	}
	
	static public GeodeticCoords GetSplinePoint(
		GeodeticCoords[] _pnts,
		double tm
	)
	{
		int n = _pnts.length;
		double[] c = new double[n];
		double u    = 0.0;
		double diff = 0.1;
		int k=0;
		int m=4;

		if (tm < 0.0 || tm > (n-1))  return(null);

		GeodeticCoords[] pnts = copyPnts(_pnts);

		if (n < 4) m=n;

		for (int i=0;i<n;i++) {
			if (i > 0) {
				pnts[i] = branch(pnts[i],pnts[i-1]);
			}
			if (tm>=i && tm<(i+1)) {
				diff = tm-i;
				if (i==0) {
					u = diff;
					k = i;
				}
				else if (i==(n-2)) {
					if (m==4) {
						u = 2.0+diff;
						k = i-2;
					}
					else {
						u = 1.0+diff;
						k = i-1;
					}
				}
				else {
					u = 1.0+diff;
					k = i-1;
				}
			}
		}
		ComputeCoefficients(u,c);
		GeodeticCoords q[] = new GeodeticCoords[m];
		for (int nn=0,mm=k;nn<m;nn++,mm++) {
			q[nn] = pnts[mm];
		}
		GeodeticCoords qq = ComputeSpline(q, c);

		return(qq);
	}
}
