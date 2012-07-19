/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

import java.util.LinkedList;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class GeoOps {
	public static double getBrgBranch(double ref, double brg) {
		while (brg > ref) {
			brg -= 360;
		}
		while (brg < ref) {
			brg += 360;
		}
		return (brg);
	}

	//
	public static boolean isOnRight(RangeBearingS rb, GeodeticCoords start,
			GeodeticCoords end, GeodeticCoords base) {
		double brg1 = rb.gcBearingFromTo(start, end);
		double brg2 = rb.gcBearingFromTo(start, base);

		double brg = getBrgBranch(brg1, brg2);
		if (brg > brg1 + 180)
			return (false);
		return (true);
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	/*
	 * public static boolean isNear( GeodeticCoords p,IProjection proj,int x,int
	 * y,double diff) { WinPoint winPoint = new WinPoint();
	 * proj.geoToWinCenterBranch(p,winPoint); double xx = (x-winPoint.m_x);
	 * double yy = (y-winPoint.m_y); double d = Math.sqrt(xx*xx+yy*yy); if (d <=
	 * diff) return(true); return(false); }
	 */
	/* =================================================================== */
	/* =================================================================== */
	//
	public static double getLength(RangeBearingS rb, GeodeticCoords pnts[]) {
		double length = 0;
		for (int i = 0, ii = 1; ii < pnts.length; i++, ii++) {
			// Skip identical points
			if (!pnts[i].equals(pnts[ii])) {
				double rng = rb.gcDistanceFromTo(pnts[i], pnts[ii]);
				length += rng;
			}
		}
		return (length);
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static double getLength(RangeBearingS rb, GeodeticCoords pnts[],
			int start, int end) {
		double length = 0;
		for (int i = start, ii = start + 1; ii < end; i++, ii++) {
			// Skip identical points
			if (!pnts[i].equals(pnts[ii])) {
				double rng = rb.gcDistanceFromTo(pnts[i], pnts[ii]);
				length += rng;
			}
		}
		return (length);
	}

	//
	public static GeodeticCoords getMidPoint(RangeBearingS rb,
			GeodeticCoords p, GeodeticCoords q) {
		double rng = rb.gcDistanceFromTo(p, q) / 2;
		double brg = rb.gcBearingFromTo(p, q);
		GeodeticCoords result = rb.gcPointFrom(p, brg, rng);
		return (result);
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static int computeIntermediate(RangeBearingS rb,
			GeodeticCoords pnts[], double dist, GeodeticCoords[] p) {
		double total = 0.0;
		for (int i = 0, ii = 1; ii < pnts.length; i++, ii++) {
			double rngKm = rb.gcDistanceFromTo(pnts[i], pnts[ii]);
			double brg = rb.gcBearingFromTo(pnts[i], pnts[ii]);
			if (total + rngKm > dist) {
				double diff = (dist - total);
				GeodeticCoords result = rb.gcPointFrom(pnts[i], brg, diff);
				rngKm = rb.gcDistanceFromTo(result, pnts[ii]);
				p[0] = result;
				return (ii);
			} else {
				total += rngKm;
			}
		}
		return (-1);
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static GeodeticCoords[] pruneDups(RangeBearingS rb,
			GeodeticCoords pnts[], double width) {
		if (pnts.length == 0)
			return (pnts);
		LinkedList<GeodeticCoords> list = new LinkedList<GeodeticCoords>();
		list.add(pnts[0]);
		for (int i = 0, ii = 1; ii < pnts.length; ii++) {
			double rngKm = rb.gcDistanceFromTo(pnts[i], pnts[ii]);
			if (rngKm >= width / 50) {
				list.add(pnts[ii]);
				i = ii;
			}
		}
		return (list.toArray(new GeodeticCoords[0]));
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static GeodeticCoords[] InterpolateArrow(RangeBearingS rb,
												    GeodeticCoords backbone[], 
												    double width) {
		backbone = pruneDups(rb, backbone, width);
		LinkedList<GeodeticCoords> pntList = new LinkedList<GeodeticCoords>();
		//
		double arrowLength = width * Math.sqrt(3);
		//
		if (backbone.length < 2) {
			return (backbone);
		}
		//
		// First build the arrow head
		//
		int firstTubeIndex = 0;
		double lat = backbone[0].getPhi(AngleUnit.DEGREES);
		double lng = backbone[0].getLambda(AngleUnit.DEGREES);
		GeodeticCoords arrowTip = new GeodeticCoords(lng,lat,AngleUnit.DEGREES);
		GeodeticCoords[] arrowBase = new GeodeticCoords[1];
		arrowBase[0] = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
		//
		firstTubeIndex = computeIntermediate(rb,backbone,arrowLength,arrowBase);

		// Start with the first point as the arrow head
		pntList.add(arrowTip);

		// Add the points at right angle from the arrow base
		double brg = rb.gcBearingFromTo(arrowBase[0], arrowTip);
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase[0], brg + 90, width);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase[0], brg - 90, width);
			pntList.addFirst(p1);
			pntList.addLast(q1);
		}
		// First add the pipe fitting points
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase[0], brg + 90, width/2);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase[0], brg - 90, width/2);
			pntList.addFirst(p1);
			pntList.addLast(q1);
		}
		for (int i1 = firstTubeIndex - 1, i2 = firstTubeIndex, i3 = firstTubeIndex + 1; i2 < backbone.length; i1++, i2++, i3++) {
			if (i1 < 0) {
				continue;
			} else if (i3 >= backbone.length) {
				brg = rb.gcBearingFromTo(backbone[i2], backbone[i1]);
				GeodeticCoords p1 = rb.gcPointFrom(backbone[i2], brg + 90,
						width / 2);
				GeodeticCoords q1 = rb.gcPointFrom(backbone[i2], brg - 90,
						width / 2);
				pntList.addFirst(p1);
				pntList.addLast(q1);
			} else {
				double br21 = rb.gcBearingFromTo(backbone[i2], backbone[i1]);
				double br23 = rb.gcBearingFromTo(backbone[i2], backbone[i3]);
				GeodeticCoords p1 = rb.gcPointFrom(backbone[i2], br21 + 90,
						width / 2);
				GeodeticCoords p2 = rb.gcPointFrom(backbone[i2], br23 - 90,
						width / 2);
				GeodeticCoords q1 = rb.gcPointFrom(backbone[i2], br21 - 90,
						width / 2);
				GeodeticCoords q2 = rb.gcPointFrom(backbone[i2], br23 + 90,
						width / 2);
				GeodeticCoords m1 = getMidPoint(rb, p1, p2);
				GeodeticCoords m2 = getMidPoint(rb, q1, q2);
				double br1 = rb.gcBearingFromTo(backbone[i2], m1);
				double br2 = rb.gcBearingFromTo(backbone[i2], m2);
				m1 = rb.gcPointFrom(backbone[i2], br1, width / 2);
				m2 = rb.gcPointFrom(backbone[i2], br2, width / 2);
				pntList.addFirst(m1);
				pntList.addLast(m2);
			}
		}
		pntList.addFirst(backbone[backbone.length - 1]);
		pntList.addLast(backbone[backbone.length - 1]);
		return (pntList.toArray(new GeodeticCoords[0]));
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static GeodeticCoords[] InterpolateAirMovementArrow(
			RangeBearingS rb, GeodeticCoords backbone[], double width) {
		LinkedList<GeodeticCoords> pntList = new LinkedList<GeodeticCoords>();
		//
		double arrowLength = width * Math.sqrt(3);
		//
		if (backbone.length < 2) {
			return (backbone);
		}
		//
		// First build the arrow head
		//
		int firstTubeIndex = 0;
		GeodeticCoords arrowTip = backbone[0];
		GeodeticCoords arrowBase = backbone[0];
		//
		double len = 0.0;
		for (int i = 0, ii = 1; ii < backbone.length; i++, ii++) {
			// Skip identical points
			if (!backbone[i].equals(backbone[ii])) {
				double brg = rb.gcBearingFromTo(backbone[i], backbone[ii]);
				double rng = rb.gcDistanceFromTo(backbone[i], backbone[ii]);
				if ((len + rng) > arrowLength) {
					double diff = (len + rng - arrowLength);
					arrowBase = rb.gcPointFrom(backbone[i], brg, diff);
					len = arrowLength;
					firstTubeIndex = ii;
					break;
				} else {
					arrowBase = backbone[ii];
					len += rng;
					firstTubeIndex = ii;
				}
			}
		}
		//
		// At this point we have :
		// - The Tip of the arrow (arrowTip])
		// - The arrow base (arrowBase)
		// - The index of the first point in the pipe (firstTubeIndex)
		// - The length of the arrow (len=arrowLength)
		//

		// Start with the first point as the arrow head
		pntList.add(arrowTip);

		// Add the points at right angle from the arrow base
		double baseBR = rb.gcBearingFromTo(arrowBase, arrowTip);
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase, baseBR + 90, width);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase, baseBR - 90, width);
			pntList.addFirst(p1);
			pntList.addLast(q1);
		}
		// First add the pipe fitting points
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase, baseBR + 90,
					width / 2);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase, baseBR - 90,
					width / 2);
			pntList.addFirst(p1);
			pntList.addLast(q1);
		}
		double diffWidth = 2.0 / (backbone.length - firstTubeIndex);
		boolean firstTime = true;
		for (int i = firstTubeIndex, ii = firstTubeIndex + 1, m = 0; ii < backbone.length; i++, ii++, m++) {
			if (backbone[i].equals(backbone[ii]))
				continue;
			double brg = rb.gcBearingFromTo(backbone[ii], backbone[i]);
			double rng = rb.gcDistanceFromTo(backbone[ii], backbone[i]);
			if (rng != 0) {
				double newWidth = width * (1 - m * diffWidth);
				if (newWidth < 0.0) {
					if (firstTime) {
						pntList.addFirst(backbone[ii]);
						pntList.addLast(backbone[ii]);
						firstTime = false;
					} else {
						newWidth = Math.min(width, Math.abs(newWidth));
						GeodeticCoords p1 = rb.gcPointFrom(backbone[ii],
								                           brg + 90, newWidth/2);
						GeodeticCoords q1 = rb.gcPointFrom(backbone[ii],
								                           brg - 90, newWidth/2);
						pntList.addFirst(p1);
						pntList.addLast(q1);
					}
				} else {
					GeodeticCoords p1 = rb.gcPointFrom(backbone[ii],brg+90,newWidth/2);
					GeodeticCoords q1 = rb.gcPointFrom(backbone[ii],brg-90,newWidth/2);
					pntList.addFirst(p1);
					pntList.addLast(q1);
				}
			}
		}
		pntList.addFirst(backbone[backbone.length - 1]);
		pntList.addLast(backbone[backbone.length - 1]);
		return (pntList.toArray(new GeodeticCoords[0]));
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	//
	public static GeodeticCoords[] InterpolatePolygon(RangeBearingS rb,
			GeodeticCoords pnts[]) {
		LinkedList<GeodeticCoords> pntList = new LinkedList<GeodeticCoords>();
		if (pnts.length > 0) {
			if (pnts.length > 1) {
				double length = getLength(rb, pnts);
				double inc = length / (10 * pnts.length);
				// Add each segment separately
				for (int i = 0; i < pnts.length - 1; i++) {
					GeodeticCoords p = pnts[i];
					GeodeticCoords q = pnts[i + 1];
					double brg = rb.gcBearingFromTo(p, q);
					double rng = rb.gcDistanceFromTo(p, q);
					double diff = Math.max(inc, rng / 10);
					double total = 0;
					pntList.add(p);
					while (total < rng) {
						total = total + diff;
						if (total < rng) {
							GeodeticCoords qq = rb.gcPointFrom(p, brg, total);
							pntList.add(qq);
						}
					}
				}
				pntList.add(pnts[pnts.length - 1]);
			} else {
				pntList.add(pnts[0]);
			}
		}
		return (pntList.toArray(new GeodeticCoords[0]));
	}

	//
	/* =================================================================== */
	/* =================================================================== */
	
	public static GeoSegment[] InterpolateArrowRibs(RangeBearingS rb,
			GeodeticCoords backbone[], double width) {
		backbone = pruneDups(rb, backbone, width);
		LinkedList<GeoSegment> pntList = new LinkedList<GeoSegment>();
		//
		double arrowLength = width * Math.sqrt(3);
		//
		if (backbone.length < 2) {
			return (pntList.toArray(new GeoSegment[0]));
		}
		//
		// First build the arrow head
		//
		int firstTubeIndex = 0;
		double lat = backbone[0].getPhi(AngleUnit.DEGREES);
		double lng = backbone[0].getLambda(AngleUnit.DEGREES);
		GeodeticCoords[] arrowTip = new GeodeticCoords[1];
		arrowTip[0] = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
		GeodeticCoords[] arrowBase = new GeodeticCoords[1];
		arrowBase[0] = new GeodeticCoords(lng, lat, AngleUnit.DEGREES);
		//
		firstTubeIndex = computeIntermediate(rb,backbone,arrowLength,arrowBase);
		//
		// At this point we have :
		// - The Tip of the arrow (arrowTip])
		// - The arrow base (arrowBase)
		// - The index of the first point in the pipe (firstTubeIndex)
		// - The length of the arrow (len=arrowLength)
		//

		// Start with the first point as the arrow head
		pntList.add(new GeoSegment(arrowTip[0], arrowTip[0]));

		// Add the points at right angle from the arrow base
		double baseBrg = rb.gcBearingFromTo(arrowBase[0], arrowTip[0]);
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase[0], baseBrg + 90, width);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase[0], baseBrg - 90, width);
			pntList.add(new GeoSegment(p1, q1));
		}
		// First add the pipe fitting points
		{
			GeodeticCoords p1 = rb.gcPointFrom(arrowBase[0], baseBrg + 90, width/2);
			GeodeticCoords q1 = rb.gcPointFrom(arrowBase[0], baseBrg - 90, width/2);
			pntList.add(new GeoSegment(p1, q1));
		}
		for (int i1 = firstTubeIndex - 1, i2 = firstTubeIndex, i3 = firstTubeIndex + 1; i2 < backbone.length; i1++, i2++, i3++) {
			if (i3 >= backbone.length) {
				double br21 = rb.gcBearingFromTo(backbone[i2], backbone[i1]);
				GeodeticCoords p1 = rb.gcPointFrom(backbone[i2], br21 + 90, width/2);
				GeodeticCoords q1 = rb.gcPointFrom(backbone[i2], br21 - 90,width/2);
				pntList.add(new GeoSegment(p1, q1));
			} else {
				double br21 = rb.gcBearingFromTo(backbone[i2], backbone[i1]);
				double br23 = rb.gcBearingFromTo(backbone[i2], backbone[i3]);
				GeodeticCoords p1 = rb.gcPointFrom(backbone[i2], br21 + 90, width/2);
				GeodeticCoords p2 = rb.gcPointFrom(backbone[i2], br23 - 90, width/2);
				GeodeticCoords q1 = rb.gcPointFrom(backbone[i2], br21 - 90, width/2);
				GeodeticCoords q2 = rb.gcPointFrom(backbone[i2], br23 + 90, width/2);
				GeodeticCoords m1 = GeoOps.getMidPoint(rb, p1, p2);
				GeodeticCoords m2 = GeoOps.getMidPoint(rb, q1, q2);
				double br1 = rb.gcBearingFromTo(backbone[i2], m1);
				double br2 = rb.gcBearingFromTo(backbone[i2], m2);
				m1 = rb.gcPointFrom(backbone[i2], br1, width/2);
				m2 = rb.gcPointFrom(backbone[i2], br2, width/2);
				pntList.add(new GeoSegment(backbone[i2], m1));
				pntList.add(new GeoSegment(backbone[i2], m2));
			}
		}
		return (pntList.toArray(new GeoSegment[0]));
	}
}
