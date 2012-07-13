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

public class Arc extends AbstractShape {
	private static final int NUM_ARC_PTS = 36;
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_startBrgHandle = new AnchorHandle();
	private final AnchorHandle m_endBrgHandle = new AnchorHandle();
	private RngBrg m_startRngBrg = null;
	private RngBrg m_endRngBrg = null;
	private AbstractPosTool m_startBrgTool = null;
	private AbstractPosTool m_endBrgTool = null;
	private AbstractPosTool m_centerTool = null;

	public Arc(){
		m_id = "Arc";
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Arc: m_convert = null");
		}
	}
	
	private void moveStartBrgPos(double rngKm) {
		if (m_startRngBrg != null) {
			m_startRngBrg.setRanegKm(rngKm);
			double brg = m_startRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_startBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rngKm));
		}
	}
	
	private void moveEndBrgPos(double rngKm) {
		if (m_endRngBrg != null) {
			double brg = m_endRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_endBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rngKm));
		}
	}

	private void setStartBrgFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_startBrgTool == null){
			m_startBrgTool = getStartBrgTool();
		}
		GeodeticCoords pos = m_startBrgTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			m_startBrgTool.setGeoPos(gc);
			updateStartRngBrg();
			m_needsUpdate = true;
		}
	}

	private void updateStartRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords radPos = m_startBrgTool.getGeoPos();
		m_startRngBrg = m_rb.RngBrgFromTo(cent, radPos);
	}
	
	public IAnchorTool getStartBrgAnchorTool(){
		if(m_startBrgTool == null){
			m_startBrgTool = getStartBrgTool();
		}
		return (IAnchorTool)m_startBrgTool;
	}

	protected AbstractPosTool getStartBrgTool() {
		if (m_startBrgTool == null) {
			m_startBrgTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartBrgFromPix(x, y);
					moveEndBrgPos(m_startRngBrg.getRanegKm());
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartBrgFromPix(x, y);
					moveEndBrgPos(m_startRngBrg.getRanegKm());
				}

				@Override
				public void handleMouseOut(Event event) {
					updateStartRngBrg();
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
		return m_startBrgTool;
	}
	
	private void setEndBrgFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_endBrgTool == null){
			m_endBrgTool = getEndBrgTool();
		}
		GeodeticCoords pos = m_endBrgTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			m_endBrgTool.setGeoPos(gc);
			updateEndRngBrg();
			m_needsUpdate = true;
		}
	}

	private void updateEndRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords pos = m_endBrgTool.getGeoPos();
		m_endRngBrg = m_rb.RngBrgFromTo(cent, pos);
	}
	
	public IAnchorTool getEndBrgAnchorTool(){
		if(m_endBrgTool == null){
			m_endBrgTool = getEndBrgTool();
		}
		return (IAnchorTool)m_endBrgTool;
	}

	protected AbstractPosTool getEndBrgTool() {
		if (m_endBrgTool == null) {
			m_endBrgTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setEndBrgFromPix(x, y);
					moveStartBrgPos(m_endRngBrg.getRanegKm());
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setEndBrgFromPix(x, y);
					moveStartBrgPos(m_endRngBrg.getRanegKm());
				}

				@Override
				public void handleMouseOut(Event event) {
					updateEndRngBrg();
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
		return m_endBrgTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords cent = m_centerTool.getGeoPos();
		if (cent == null  || !cent.equals(gc)) {
			m_centerTool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}

	private void moveBrgPos() {
		if (m_startRngBrg != null) {
			double rng = m_startRngBrg.getRanegKm();
			double brg = m_startRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_startBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
		}
		if (m_endRngBrg != null) {
			double rng = m_endRngBrg.getRanegKm();
			double brg = m_endRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_endBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
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
					moveBrgPos();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveBrgPos();
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
	
	protected double brgSpan(){
		double startDeg = m_startRngBrg.getBearing();
		double endDeg = m_endRngBrg.getBearing();
		double degLen = endDeg - startDeg;
		if (degLen < 0){
			return 360+degLen;
		}
		return degLen;
	}
	
	protected void drawSegments(Context2d context){
		double degInc = brgSpan()/(NUM_ARC_PTS - 1);
		double distKm = m_startRngBrg.getRanegKm();
		ISplit splitter = m_convert.getISplit();
		ViewCoords p, q;
		double startBrg = m_startRngBrg.getBearing();
		q = getBoundaryPt( startBrg, distKm);  
		int move = splitter.getMove();
		int x = q.getX();
		if ( move!= ConvertBase.DONT_MOVE){
			x += splitter.getDistance(move);
		}
		
		context.moveTo(x, q.getY());
		for (int i = 1; i < NUM_ARC_PTS; i++) {
			p = q;
			double brng = startBrg + degInc*i;
			q = getBoundaryPt(brng, distKm);
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
		context.stroke();
	}
	
	private void setEndBrgPos(GeodeticCoords pos) {
		if (m_endBrgTool == null){
			m_endBrgTool = getEndBrgTool();
		}
		m_endBrgTool.setGeoPos(pos);
	}
	
	public void initialMouseMove(Event event) {
		int x = event.getClientX();
		int y = event.getClientY();
		setStartBrgFromPix(x, y);
		GeodeticCoords startBrgPos = m_startBrgTool.getGeoPos();
		GeodeticCoords cenPos = getCenter();
		double brgDeg = m_rb.gcBearingFromTo(cenPos, startBrgPos);
		brgDeg = Func.wrap360(brgDeg-90);
		double disKm = m_startRngBrg.getRanegKm();
		if (m_endRngBrg == null){
			m_endRngBrg = new RngBrg();
		}
		m_endRngBrg.setRanegKm(disKm).setBearing(brgDeg);
		GeodeticCoords pos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		setEndBrgPos(pos);
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
			// start Brg handle
			gc = getStartBrgPos();
			vc = m_convert.geodeticToView(gc);
			m_startBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			m_startBrgHandle.setStrokeColor(0, 200, 0, 1.0);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_startBrgHandle.setCenter(x, vc.getY()).draw(context);
			}
			// end Brg handle
			gc = getEndBrgPos();
			vc = m_convert.geodeticToView(gc);
			m_endBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			m_endBrgHandle.setStrokeColor(200, 0, 0, 1.0);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_endBrgHandle.setCenter(x, vc.getY()).draw(context);
			}
		}
		return (IShape)this;
	}

	public GeodeticCoords getStartBrgPos() {
		return m_startBrgTool.getGeoPos();
	}
	
	public GeodeticCoords getEndBrgPos() {
		return m_endBrgTool.getGeoPos();
	}

	public void setRadiusPos(GeodeticCoords radPos) {
		m_startBrgTool.setGeoPos(radPos);
	}

	public Arc withRadiusPos(GeodeticCoords radPos) {
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

	public Arc withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}
	
	protected boolean ptClose(int px, int py, double eps){
		double degInc = brgSpan()/(NUM_ARC_PTS - 1);
		double distKm = m_startRngBrg.getRanegKm();
		double startDeg = m_startRngBrg.getBearing();
		/////////////////////////////////////////
		ViewCoords p, q = getBoundaryPt(startDeg, distKm);  
		for (int i = 1; i < NUM_ARC_PTS; i++) {
			double brg = startDeg+ degInc * i;
			p = q;
			q = getBoundaryPt( brg, distKm);
			double dist = Func.ptLineDist(p, q, px, py);
			if (dist < eps) {
				return true;
			}
		}
		return false;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		return ptClose(px, py, eps);
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
		return ptCloseToEdge( vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		AbstractPosTool tool = getStartBrgTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getEndBrgTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getCenterTool();
		if (tool.isSlected(position)){
			return (IAnchorTool)tool;
		}
		return null;
	}
}
