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
import com.moesol.gwt.maps.client.algorithms.RngBrg;


public class Sector extends AbstractShape {
	private static final int NUM_SEC_PTS = 36;
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_startRngBrgHandle = new AnchorHandle();
	private final AnchorHandle m_endRngBrgHandle = new AnchorHandle();
	private RngBrg m_startRngBrg = null;
	private RngBrg m_endRngBrg = null;
	private AbstractPosTool m_startRngBrgTool = null;
	private AbstractPosTool m_endRngBrgTool = null;
	private AbstractPosTool m_centerTool = null;
	
	private boolean m_altKeydown = false;
	private boolean m_shiftKeydown = false;

	public Sector(){
		m_id = "Sector";
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Arc: m_convert = null");
		}
	}
	
	private void moveRngBrgPos(AbstractPosTool tool, 
							   RngBrg toolRngBrg,
							   GeodeticCoords pos){
		if(!m_altKeydown || !m_shiftKeydown){
			GeodeticCoords cent = m_centerTool.getGeoPos();
			double rng = m_rb.gcDistanceFromTo(cent, pos);
			double brg = m_rb.gcBearingFromTo(cent, pos);
			if(toolRngBrg != null){
				if (m_altKeydown && !m_shiftKeydown){
					rng = toolRngBrg.getRanegKm();
				}
				else if(!m_altKeydown && m_shiftKeydown){
					brg = toolRngBrg.getBearing();
				}
			}
			tool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
			if (toolRngBrg != null){
				toolRngBrg.setBearing(brg);
				toolRngBrg.setRanegKm(rng);				
			}
		}
		return;
	}

	private void setStartRngBrgFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_startRngBrgTool == null){
			m_startRngBrgTool = getStartRngBrgTool();
		}
		GeodeticCoords pos = m_startRngBrgTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			moveRngBrgPos(m_startRngBrgTool,m_startRngBrg,gc);
			updateStartRngBrg();
			m_needsUpdate = true;
		}
	}

	private void updateStartRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords pos = m_startRngBrgTool.getGeoPos();
		m_startRngBrg = m_rb.gcRngBrgFromTo(cent, pos);
	}
	
	public IAnchorTool getStartRngBrgAnchorTool(){
		if(m_startRngBrgTool == null){
			m_startRngBrgTool = getStartRngBrgTool();
		}
		return (IAnchorTool)m_startRngBrgTool;
	}

	protected AbstractPosTool getStartRngBrgTool() {
		if (m_startRngBrgTool == null) {
			m_startRngBrgTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartRngBrgFromPix(x, y);
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartRngBrgFromPix(x, y);
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
		return m_startRngBrgTool;
	}
	
	private void setEndRngBrgFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_endRngBrgTool == null){
			m_endRngBrgTool = getEndRngBrgTool();
		}
		GeodeticCoords pos = m_endRngBrgTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			moveRngBrgPos(m_endRngBrgTool,m_endRngBrg,gc);
			updateEndRngBrg();
			m_needsUpdate = true;
		}
	}

	private void updateEndRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords pos = m_endRngBrgTool.getGeoPos();
		m_endRngBrg = m_rb.gcRngBrgFromTo(cent, pos);
	}
	
	public void setKeyboardFlags(boolean altKey, boolean shiftKey){
		m_altKeydown = altKey;
		m_shiftKeydown = shiftKey;
	}
	
	public IAnchorTool getEndRngBrgAnchorTool(){
		if(m_endRngBrgTool == null){
			m_endRngBrgTool = getEndRngBrgTool();
		}
		return (IAnchorTool)m_endRngBrgTool;
	}

	protected AbstractPosTool getEndRngBrgTool() {
		if (m_endRngBrgTool == null) {
			m_endRngBrgTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setEndRngBrgFromPix(x, y);
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setEndRngBrgFromPix(x, y);
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
		return m_endRngBrgTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords cent = m_centerTool.getGeoPos();
		if (cent == null  || !cent.equals(gc)) {
			m_centerTool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}

	private void moveRngBrgPos() {
		if (m_startRngBrg != null) {
			double rng = m_startRngBrg.getRanegKm();
			double brg = m_startRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_startRngBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
		}
		if (m_endRngBrg != null) {
			double rng = m_endRngBrg.getRanegKm();
			double brg = m_endRngBrg.getBearing();
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_endRngBrgTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
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
					moveRngBrgPos();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveRngBrgPos();
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
		ISplit splitter = m_convert.getISplit();
		ViewCoords p, q;
		double degInc = brgSpan()/(NUM_SEC_PTS - 1);
		double rng = m_startRngBrg.getRanegKm();
		double brg = m_startRngBrg.getBearing();
		q = getBoundaryPt( brg, rng);  
		// set p to null for first point
		int x = splitter.shift(null, q);
		context.moveTo(x, q.getY());
		for (int i = 1; i < NUM_SEC_PTS; i++) {
			p = q;
			double brng = brg + degInc*i;
			q = getBoundaryPt(brng, rng);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}	
		p = q;
		rng = m_endRngBrg.getRanegKm();
		brg = m_endRngBrg.getBearing();
		q = getBoundaryPt( brg, rng); 
		x = splitter.shift(p, q);
		context.lineTo(x, q.getY());
		for (int i = 1; i < NUM_SEC_PTS; i++) {
			p = q;
			double brng = brg - degInc*i;
			q = getBoundaryPt(brng, rng);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}
		p = q;
		q = getBoundaryPt(m_startRngBrg.getBearing(),m_startRngBrg.getRanegKm()); 
		x = splitter.shift(p, q);
		context.lineTo(x, q.getY());
	}

	private void drawBoundary(Context2d context) {
		checkForException();
		ISplit splitter = m_convert.getISplit();
		// MUST first initialize
		splitter.initialize(ISplit.NO_ADJUST);
		/////////////////////////////////////////
		drawSegments(context);
		
		if (splitter.isSplit()){
			// Must initialize with new values.
			splitter.initialize(ISplit.ADJUST);
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
	
	public void initialMouseMove(Event event) {
		int x = event.getClientX();
		int y = event.getClientY();
		setStartRngBrgFromPix(x, y);
		GeodeticCoords startBrgPos = m_startRngBrgTool.getGeoPos();
		GeodeticCoords cenPos = getCenter();
		double brgDeg = m_rb.gcBearingFromTo(cenPos, startBrgPos);
		brgDeg = Func.wrap360(brgDeg-90);
		double disKm = m_startRngBrg.getRanegKm();
		if (m_endRngBrg == null){
			m_endRngBrg = new RngBrg();
		}
		m_endRngBrg.setRanegKm(disKm*0.8).setBearing(brgDeg);
		GeodeticCoords pos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		setEndRngBrgPos(pos);
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
			gc = getStartRngBrgPos();
			vc = m_convert.geodeticToView(gc);
			m_startRngBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			m_startRngBrgHandle.setStrokeColor(0, 200, 0, 1.0);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_startRngBrgHandle.setCenter(x, vc.getY()).draw(context);
			}
			// end Brg handle
			gc = getEndRngBrgPos();
			vc = m_convert.geodeticToView(gc);
			m_endRngBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			m_endRngBrgHandle.setStrokeColor(200, 0, 0, 1.0);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_endRngBrgHandle.setCenter(x, vc.getY()).draw(context);
			}
		}
		return (IShape)this;
	}

	public GeodeticCoords getStartRngBrgPos() {
		return m_startRngBrgTool.getGeoPos();
	}
	
	public void setStartRngBrgPos(GeodeticCoords radPos) {
		m_startRngBrgTool.setGeoPos(radPos);
	}

	public Sector withStartRngBrgPos(GeodeticCoords radPos) {
		setStartRngBrgPos(radPos);
		return this;
	}
	
	public GeodeticCoords getEndRngBrgPos() {
		return m_endRngBrgTool.getGeoPos();
	}

	private void setEndRngBrgPos(GeodeticCoords pos) {
		if (m_endRngBrgTool == null){
			m_endRngBrgTool = getEndRngBrgTool();
		}
		m_endRngBrgTool.setGeoPos(pos);
	}

	public Sector withEndRngBrgPos(GeodeticCoords pos) {
		setEndRngBrgPos(pos);
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

	public Sector withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}
	
	protected boolean ptClose(int px, int py, double eps){
		double degInc = brgSpan()/(NUM_SEC_PTS - 1);
		double rng = m_startRngBrg.getRanegKm();
		double brg = m_startRngBrg.getBearing();
		ViewCoords p, q = getBoundaryPt(brg, rng);  
		for (int i = 1; i < NUM_SEC_PTS; i++) {
			double brng = brg + degInc * i;
			p = q;
			q = getBoundaryPt( brng, rng);
			if (Func.ptLineDist(p, q, px, py) < eps) {
				return true;
			}
		}
		p = q;
		rng = m_endRngBrg.getRanegKm();
		brg = m_endRngBrg.getBearing();
		q = getBoundaryPt(brg, rng);
		if (Func.ptLineDist(p, q, px, py) < eps) {
			return true;
		}
		for (int i = 1; i < NUM_SEC_PTS; i++) {
			double brng = brg - degInc * i;
			p = q;
			q = getBoundaryPt( brng, rng);
			if (Func.ptLineDist(p, q, px, py) < eps) {
				return true;
			}
		}
		p = q;
		rng = m_startRngBrg.getRanegKm();
		brg = m_startRngBrg.getBearing();
		q = getBoundaryPt(brg, rng);
		if (Func.ptLineDist(p, q, px, py) < eps) {
			return true;
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
		AbstractPosTool tool = getStartRngBrgTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getEndRngBrgTool();
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
	   	IShapeTool tool = new EditSectorTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
