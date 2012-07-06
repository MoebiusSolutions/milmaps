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
	public static double TWO_PI = 2*Math.PI;
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
	
	public static ViewCoords closestPt(int px, int py, 
									   int qx, int qy, 
									   double x, double y ) {
		double x1 = px;
		double y1 = py;
		double x2 = qx;
		double y2 = qy;
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
			return new ViewCoords(px, py);
		}
		if (w >= 1){
			return new ViewCoords(qx, qy);
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
	
	public static double ptLineDist(int px, int py, int qx, int qy, double x, double y){
		ViewCoords vc = closestPt(px,py,qx,qy,x,y);
		double aSqr = (vc.getX()-x)*(vc.getX()-x);
		double bSqr = (vc.getY()-y)*(vc.getY()-y);
		return Math.sqrt( aSqr + bSqr); 
	}
	
	public static int computePixelDistance(ViewCoords vc, ViewCoords vr) {
		int xDist = Math.abs(vc.getX() - vr.getX());
		int yDist = Math.abs(vc.getY() - vr.getY());
		return (int) (Math.sqrt(xDist * xDist + yDist * yDist));
	}
	
	public static double wrap360(double deg) {
		int k = (int)Math.abs((deg/360));
		if (deg > 360) {
			deg -= k*360;
		} else if (deg < 0) {
			deg += k*360;
		}
		return deg;
	}
	
	// Taken from "http://softsurfer.com/Archive/algorithm_0103/algorithm_0103.htm"
	//===================================================================
	// cn_PnPoly(): crossing number test for a point in a polygon
	//	      Input:   P = a point,
	//	               V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
	//	      Return:  0 = outside, 1 = inside
	// This code is patterned after [Franklin, 2000]
	public int ptInPoly( ViewCoords p, ViewCoords[] vPts )
	{
	    int cn = 0;    // the crossing number counter
	    int len = vPts.length;
	    // loop through all edges of the polygon
	    for (int i = 0; i < len; i++) {    // edge from V[i] to V[i+1]
	       if (((vPts[i].getY() <= p.getY()) && (vPts[i+1].getY() > p.getY())) ||   // an upward crossing
	           ((vPts[i].getY() > p.getY()) && (vPts[i+1].getY() <= p.getY()))) { // a downward crossing
	            // compute the actual edge-ray intersect x-coordinate
	    	   double rise1 = p.getY()-vPts[i].getY();
	    	   double rise2 = vPts[i+1].getY()-vPts[i].getY();
	           double vt = rise1/rise2;
	           if (p.getX() < vPts[i].getX()+vt*(vPts[i+1].getX()-vPts[i].getX())){
	        	   // p.x < intersect
	        	   ++cn;   // a valid crossing of y=P.y right of P.x
	           }
	       }
	    }
	    return (cn&1);    // 0 if even (out), and 1 if odd (in)
	}
	// isLeft(): tests if a point is Left|On|Right of an infinite line.
	//  Input:  three points P0, P1, and P2
	//  Return: >0 for P2 left of the line through P0 and P1
	//          =0 for P2 on the line
	//          <0 for P2 right of the line
	//  See: the January 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
	public  int isLeft( ViewCoords P0, ViewCoords P1, ViewCoords P2 )
	{
		return ((P1.getX()-P0.getX())*(P2.getY()-P0.getY())
				-(P2.getX()-P0.getX())*(P1.getY()-P0.getY()));
	}

	// wn_PnPoly(): winding number test for a point in a polygon
	//  Input:   P = a point,
	//           V[] = vertex points of a polygon V[n+1] with V[n]=V[0]
	//  Return:  wn = the winding number (=0 only if P is outside V[])
	public int ptInPoly2( ViewCoords p, ViewCoords[] vPts ){
		int    wn = 0;    // the winding number counter
		int n = vPts.length;
		// loop through all edges of the polygon
		for (int i = 0; i < n; i++) {   // edge from V[i] to V[i+1]
			if (vPts[i].getY() <= p.getY()) {         // start y <= P.y
				if (vPts[i+1].getY() > p.getY()){      // an upward crossing
					if (isLeft( vPts[i], vPts[i+1], p) > 0){  // P left of edge
						++wn;   // have a valid up intersect
					}
				}
			}
			else {                       // start y > P.y (no test needed)
				if (vPts[i+1].getY() <= p.getY()) {    // a downward crossing
					if (isLeft( vPts[i], vPts[i+1], p) < 0){  // P right of edge
						--wn;            // have a valid down intersect
					}
				}
			}
		}
		return wn;
	}	
}

