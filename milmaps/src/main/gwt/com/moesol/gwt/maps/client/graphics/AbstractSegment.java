/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;

public abstract class AbstractSegment extends AbstractShape{
	private static final int NUM_LINESEG_PTS = 36;
	
	protected void drawSegment(GeodeticCoords p, 
			   				   GeodeticCoords q,
			   				   Context2d context){
		double length = m_rb.gcDistanceFromTo(p, q);
		double brng = m_rb.gcBearingFromTo(p,q);
		double lenInc = length/NUM_LINESEG_PTS;
		ISplit splitter = m_convert.getISplit();
		ViewCoords pt, qt;
		GeodeticCoords gc = p;
		qt = m_convert.geodeticToView(p);  
		int move = splitter.getMove();
		int x = qt.getX();
		if ( move!= ConvertBase.DONT_MOVE){
			x += splitter.getDistance(move);
		}
		context.moveTo(x, qt.getY());
		for (int i = 1; i < NUM_LINESEG_PTS-1; i++) {
			pt = qt;
			brng = m_rb.gcBearingFromTo(gc, q);
			gc = m_rb.gcPointFrom(gc, brng, lenInc);	
			qt = m_convert.geodeticToView(gc);
			x = splitter.shift(pt, qt);
			context.lineTo(x, qt.getY());
		}
		pt = qt;
		qt = m_convert.geodeticToView(q);
		x = splitter.shift(pt, qt);
		context.lineTo(x, qt.getY());
	}

	protected boolean ptClose(GeodeticCoords p,
			  				  GeodeticCoords q,
			  				  int px, int py, double eps){
		double length = m_rb.gcDistanceFromTo(p, q);
		double brng = m_rb.gcBearingFromTo(p,q);
		double lenInc = length/NUM_LINESEG_PTS;
		GeodeticCoords gc = p;
		ViewCoords pt, qt;
		qt = m_convert.geodeticToView(gc);  
		/////////////////////////////////////////;  
		for (int i = 1; i < NUM_LINESEG_PTS-1; i++) {
			pt = qt;
			brng = m_rb.gcBearingFromTo(gc, q);
			gc = m_rb.gcPointFrom(gc, brng, lenInc);	
			qt = m_convert.geodeticToView(gc);
			double dist = Func.ptLineDist(pt, qt, px, py);
			if (dist < eps) {
				return true;
			}
		}
		pt = qt;
		qt = m_convert.geodeticToView(q);
		double dist = Func.ptLineDist(pt, qt, px, py);
		if (dist < eps) {
			return true;
		}	
		return false;
	}
}
