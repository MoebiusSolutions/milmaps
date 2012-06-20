/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.algorithms;

import com.moesol.gwt.maps.client.ViewCoords;

public class Func {
	private static double RadToDeg = 57.29577951;
	private static double DegToRad = 0.017453293;
	public static int PIX_SELECT_TOLERANCE = 3;
	
	public static double RadToDeg(double radian){
		return RadToDeg*radian;
	}
	public static double DegToRad(double deg){
		return DegToRad*deg;
	}
	
	public static boolean isClose(ViewCoords p, ViewCoords q, int epsilon){
		if ( Math.abs(p.getX() - q.getX())< epsilon ){
			if(Math.abs(p.getY() - q.getY())< epsilon ){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isClose(double p, double q, double epsilon){
		if(Math.abs(p-q)< epsilon ){
			return true;
		}
		return false;
	}

	public static ViewCoords closestPt(ViewCoords p, ViewCoords q, double x, double y ) {
		double x1 = p.getX();
		double y1 = p.getY();
		double x2 = q.getX();
		double y2 = q.getY();
		double Y = y2 - y1;
		double X = x2 - x1;
		
		// A is the vector from (x1,y1) to (x,y)
		// B is the vector from (x1,y1) to (x2,y2)
		
		double AdotB = ((x - x1)*X + (y - y1)*(Y));
		double magB = Math.sqrt(X*X + Y*Y);
		// The proj of vector A on the vector B 
		// is given by proj = AdotB/|B|; hence if we devide by |B| again we have
		// "w = proj/|B|", where w is a  percentage of the line segment's length.
		// So the point we want is P = (x1,y1) + wB;
		// |A|cos(theta) = AdotB/|B| so the closest point is on the 
		// the line segment B if 0 < AdotB/|B| <= |B|.
		// and dividing through by |B| we have 0 < AdotB/(|B||B|) <= 1

		double w = AdotB/(magB*magB);
		if(w <= 0){
			return p;
		}
		if (w >= 1){
			return q;
		}

		int tx = (int)(Math.rint(x1 + w*X));
		int ty = (int)(Math.rint(y1 + w*Y));
		return new ViewCoords(tx, ty);
	}
	
	public static double ptLineDist(ViewCoords p, ViewCoords q, double px, double py){
		ViewCoords vc = closestPt(p,q,px,py);
		double aSqr = (vc.getX()-px)*(vc.getX()-px);
		double bSqr = (vc.getY()-py)*(vc.getY()-py);
		return Math.sqrt( aSqr + bSqr); 
	}
}

