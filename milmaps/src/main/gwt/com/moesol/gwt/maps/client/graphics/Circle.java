/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.RngBrg;

public class Circle extends AbstractShape {
	private static final int NUM_CIR_PTS = 36;
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_radHandle = new AnchorHandle();
	private RngBrg m_radRngBrg = null;
	private AbstractPosTool m_radiusTool = null;
	private AbstractPosTool m_centerTool = null;

	public Circle(){
		m_id = "Circle";
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Circle: m_convert = null");
		}
	}

	private void setRadiusFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_radiusTool == null){
			m_radiusTool = getRadiusTool();
		}
		GeodeticCoords pos = m_radiusTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			m_radiusTool.setGeoPos(gc);
			upDateRngBrg();
			m_needsUpdate = true;
		}
	}

	private void upDateRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords radPos = m_radiusTool.getGeoPos();
		m_radRngBrg = m_rb.gcRngBrgFromTo(cent, radPos);
	}
	
	public IAnchorTool getRadiusAnchorTool(){
		if(m_radiusTool == null){
			m_radiusTool = getRadiusTool();
		}
		return (IAnchorTool)m_radiusTool;
	}

	protected AbstractPosTool getRadiusTool() {
		if (m_radiusTool == null) {
			m_radiusTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setRadiusFromPix(x, y);
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setRadiusFromPix(x, y);
					upDateRngBrg();
				}

				@Override
				public void handleMouseOut(Event event) {
					upDateRngBrg();
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					if ( m_geoPos != null){
						ViewCoords radPt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(radPt, vc, 4);						
					}
					return false;
				}

				@Override
				public void handleMouseDblClick(Event event) {
				}

				@Override
				public void handleKeyDown(Event event) {
				}

				@Override
				public void handleKeyUp(Event event) {
				}
			};
		}
		return m_radiusTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords cent = m_centerTool.getGeoPos();
		if (cent == null  || !cent.equals(gc)) {
			m_centerTool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}

	private void moveRadiusPos() {
		if (m_radRngBrg != null) {
			double rng = m_radRngBrg.getRanegKm();
			double brg = m_radRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_radiusTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
		}
	}
	
	public IAnchorTool getCenterAnchorTool(){
		if(m_centerTool == null){
			m_centerTool = getCenterTool();
		}
		return (IAnchorTool)m_centerTool;
	}

	protected AbstractPosTool getCenterTool() {
		if (m_centerTool == null) {
			m_centerTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {;
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveRadiusPos();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveRadiusPos();
				}

				@Override
				public void handleMouseOut(Event event) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					if ( m_geoPos != null){
						ViewCoords radPt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(radPt, vc, 4);						
					}
					return false;
				}
			};
		}
		return m_centerTool;
	}
	
	private ViewCoords getBoundaryPt(double brg, double distKm){
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords gc = m_rb.gcPointFrom(cent, brg, distKm);
		return m_convert.geodeticToView(gc);
	}
	
	protected void drawSegments(Context2d context){
		double degInc = 360.0 / (NUM_CIR_PTS - 1);
		double distKm = m_radRngBrg.getRanegKm();
		ISplit splitter = m_convert.getISplit();
		ViewCoords p, q;
		q = getBoundaryPt( 0, distKm);  
		int move = splitter.getMove();
		int x = q.getX();
		if ( move!= ConvertBase.DONT_MOVE){
			x += splitter.getDistance(move);
		}
		
		context.moveTo(x, q.getY());
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			p = q;
			double brng = degInc*i;
			q = getBoundaryPt( brng, distKm);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}		
	}

	private void drawBoundary(Context2d context) {
		checkForException();
		ISplit splitter = m_convert.getISplit();
		// MUST initialize with the next three lines
		splitter.setAjustFlag(false);
		splitter.setSplit(false);
		splitter.setMove(ConvertBase.DONT_MOVE);
		/////////////////////////////////////////
		drawSegments(context);
		
		if (splitter.isSplit()){
			// Must initialize with new values.
			splitter.setAjustFlag(true);
			splitter.setMove(splitter.switchMove(splitter.getMove()));
			drawSegments(context);
		}
	}

	private void draw(Context2d context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
		context.closePath();
		context.stroke();
	}


	@Override
	public IShape erase(Context2d ct) {
		//_erase(ct);
		return (IShape)this;
	}
	
	@Override
	public IShape render(Context2d ct) {
		draw(ct);
		return (IShape)this;
	}
	
	@Override
	public IShape drawHandles(Context2d context) {
		if (context != null) {
			ISplit splitter = m_convert.getISplit();
			// Center Handle
			GeodeticCoords gc = getCenter();
			ViewCoords vc = m_convert.geodeticToView(gc);
			m_centerHandle.setCenter(vc.getX(), vc.getY());
			m_centerHandle.draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_centerHandle.setCenter(x, vc.getY()).draw(context);
			}
			// Radius handle
			gc = getRadiusPos();
			vc = m_convert.geodeticToView(gc);
			m_radHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_radHandle.setCenter(x, vc.getY()).draw(context);
			}
		}
		return (IShape)this;
	}

	public GeodeticCoords getRadiusPos() {
		return m_radiusTool.getGeoPos();
	}

	public void setRadiusPos(GeodeticCoords radPos) {
		m_radiusTool.setGeoPos(radPos);
	}

	public Circle withRadiusPos(GeodeticCoords radPos) {
		setRadiusPos(radPos);
		return this;
	}

	public GeodeticCoords getCenter() {
		return m_centerTool.getGeoPos();
	}

	public void setCenter(GeodeticCoords center) {
		if (m_centerTool == null){
			m_centerTool = getCenterTool();
		}
		m_centerTool.setGeoPos(center);
	}

	public Circle withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}
	
	protected boolean ptCloseToEdge(int px, int py, double eps){
		double degInc = 360.0 / (NUM_CIR_PTS - 1);
		double distKm = m_radRngBrg.getRanegKm();
		/////////////////////////////////////////
		ViewCoords p, q = getBoundaryPt( 0, distKm);  
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			double brg = degInc * i;
			p = q;
			q = getBoundaryPt( brg, distKm);
			double dist = Func.ptLineDist(p, q, px, py);
			if (dist < eps) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForException();
		ViewCoords vc = m_convert.geodeticToView(position);
		GeodeticCoords cent = m_centerTool.getGeoPos();
		ViewCoords centPix = m_convert.geodeticToView(cent);
		if (Func.isClose(centPix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptCloseToEdge(vc.getX(),vc.getY(),Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		AbstractPosTool tool = getRadiusTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getCenterTool();
		if (tool.isSlected(position)){
			return (IAnchorTool)tool;
		}
		return null;
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditCircleTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
