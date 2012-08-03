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
	private static final int NUM_LINESEG_PTS = 20;
	
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
		// set p to null for first point
		int x = splitter.shift(null, qt);
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
	
	private ViewCoords loopAndDraw(GeodeticCoords p, GeodeticCoords q, 
							 ViewCoords qt, Context2d context){
		ISplit splitter = m_convert.getISplit();
		ViewCoords pt;
		double length = m_rb.gcDistanceFromTo(p, q);
		double brng = m_rb.gcBearingFromTo(p,q);
		double lenInc = length/NUM_LINESEG_PTS;
		GeodeticCoords gc = p;
		int x;
		for (int i = 1; i < NUM_LINESEG_PTS; i++) {
			pt = qt;
			brng = m_rb.gcBearingFromTo(gc, q);
			gc = m_rb.gcPointFrom(gc, brng, lenInc);	
			qt = m_convert.geodeticToView(gc);
			x = splitter.shift(pt, qt);
			context.lineTo(x, qt.getY());
		}	
		return qt;
	}
	
	protected void drawBoxSides(GeodeticCoords p, GeodeticCoords q,
								   GeodeticCoords r, GeodeticCoords s,
								   Context2d context){
		ISplit splitter = m_convert.getISplit();
		ViewCoords pt, qt;
		qt = m_convert.geodeticToView(p);  
		// set p to null for first point
		int x = splitter.shift(null, qt);
		context.moveTo(x, qt.getY());
		// first side
		qt = loopAndDraw(p, q, qt, context);
		// second side
		qt = loopAndDraw(q, r, qt, context);
		// third side
		qt = loopAndDraw(r, s, qt, context);
		// forth side
		qt = loopAndDraw(s, p, qt, context);
		pt = qt;
		qt = m_convert.geodeticToView(p);
		x = splitter.shift(pt, qt);
		context.lineTo(x, qt.getY());
	}
}
