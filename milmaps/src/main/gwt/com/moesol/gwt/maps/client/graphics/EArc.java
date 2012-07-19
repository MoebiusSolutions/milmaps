/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.Event;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.RngBrg;

public class EArc extends AbstractShape{
	private static final int NUM_ARC_PTS = 36;
	private static final int OUT = 12;
	private static final int IN = -OUT;
	private static final RangeBearingS m_rb = new RangeBearingS();
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_smjHandle = new AnchorHandle();
	private final AnchorHandle m_smnHandle = new AnchorHandle();
	private final AnchorHandle m_startBrgHandle = new AnchorHandle();
	private final AnchorHandle m_endBrgHandle = new AnchorHandle();
	
	protected RngBrg m_smjRngBrg = null;
	protected RngBrg m_smnRngBrg = null;
	protected RngBrg m_startRngDeg = null;
	protected RngBrg m_endRngDeg   = null;
	// private boolean m_mouseDown = true;
	protected AbstractPosTool m_centerTool = null;
	protected AbstractPosTool m_smjTool = null;
	protected AbstractPosTool m_smnTool = null;
	protected AbstractPosTool m_startDegTool = null;
	protected AbstractPosTool m_endDegTool = null;

	public EArc() {
		m_id = "EllipseArc";
		m_smjRngBrg   = new RngBrg(0,0);
		m_smnRngBrg   = new RngBrg(0,0);
		m_startRngDeg = new RngBrg(0,0);
		m_endRngDeg   = new RngBrg(0,90);
	}

	private void checkForExceptions() {
		if (m_convert == null) {
			throw new IllegalStateException("Ellipse: m_convert = null");
		}
	}
	
	private ViewCoords place(int tX, int tY, int hX, int hY, int n){
		double run  = hX - tX;
		double rise = hY - tY;
		double mag = Math.sqrt(run*run+rise*rise);
		int x = hX + (int)(n*run/mag);
		int y = hY + (int)(n*rise/mag);
		return new ViewCoords(x,y);
	}

	private void setSmjFromPix(int x, int y) {
		checkForExceptions();
		ViewCoords cent = m_convert.geodeticToView(getCenter());
		ViewCoords vc = place(cent.getX(),cent.getY(),x,y,IN);
		GeodeticCoords gc = m_convert.viewToGeodetic(vc);
		
		if(m_smjTool == null){
			m_smjTool = getSmjTool();
		}
		GeodeticCoords pos = m_smjTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			m_smjTool.setGeoPos(gc);
			GeodeticCoords centGc = m_centerTool.getGeoPos();
			m_smjRngBrg = m_rb.gcRngBrgFromTo(centGc, gc);
			m_needsUpdate = true;
		}		
	}
	
	private ViewCoords rngBrgToView(double rngKm, double degBrg){
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords gc = m_rb.gcPointFrom(cent, degBrg, rngKm);
		return m_convert.geodeticToView(gc);	
	}
	
	protected double adjustDegLen(double startDeg, double endDeg){
		double degLen = endDeg - startDeg;
		if (endDeg <= startDeg){
			return 360+degLen;
		}
		return degLen;
	}
	
	private ViewCoords compViewPt(double rotBrg, double brgDeg, double a, double b){
		double rngKm = compRangeForDeg(brgDeg, a, b);
		return rngBrgToView(rngKm,rotBrg-brgDeg);	
	}
	
	private ViewCoords compViewPt2(double rotBrg, double t, double a, double b){
		double angle = Func.DegToRad(t);
		double x = (a * Math.cos(angle));
		double y = (b * Math.sin(angle));
		if (a != b) {
			angle = Math.atan2(y, x);
		}
		double rngKm = Math.sqrt(x*x + y*y);
		return rngBrgToView(rngKm,rotBrg-Func.RadToDeg(angle));	
	}

	protected void drawSegments(Context2d context){
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		ISplit splitter = m_convert.getISplit();
		double startDeg = 0;// m_startRngDeg.getBearing();
		ViewCoords p;
		ViewCoords q = compViewPt2(rotBrg,startDeg,a,b);
		int move = splitter.getMove();
		int x = q.getX();
		if (move!= ConvertBase.DONT_MOVE){
			x += splitter.getDistance(move);
		}
		context.moveTo(x, q.getY());
		double degLen = 360;//adjustDegLen(startDeg,m_endRngDeg.getBearing());
		double incBrg = degLen/(NUM_ARC_PTS-1);
		for (int i = 1; i < NUM_ARC_PTS; i++) {
			p = q;
			q = compViewPt2(rotBrg,startDeg+i*incBrg,a,b);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}	
	}
	
	
	private void drawBoundary(Context2d context) {
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
	
	private void setSmnPosFromSmjBrg(){
		GeodeticCoords smjPos = getSmjPos();
		GeodeticCoords cenPos = getCenter();
		double brgDeg = m_rb.gcBearingFromTo(cenPos, smjPos);
		brgDeg = Func.wrap360(brgDeg-90);
		double disKm = m_smnRngBrg.getRanegKm();
		m_smnRngBrg.setBearing(brgDeg);
		GeodeticCoords smnPos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		setSmnPos(smnPos);	
	}
	
	private void setStartEndBrgFromPix(int x, int y, RngBrg rb, AbstractPosTool tool) {
		checkForExceptions();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords pos = tool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			GeodeticCoords centGc = m_centerTool.getGeoPos();
			double brg = m_rb.gcBearingFromTo(centGc, gc);
			double smjBrg = m_smjRngBrg.getBearing();
			double deg = Func.BrgToAngleDeg(smjBrg) + Func.BrgToAngleDeg(brg);
			double rngKm = compRangeForDeg(deg, m_smjRngBrg.getRanegKm(),
												m_smnRngBrg.getRanegKm());
			rb.setRanegKm(rngKm).setBearing(deg);
			pos = m_rb.gcPointFrom(centGc, brg, rngKm);
			tool.setGeoPos(pos);
			m_needsUpdate = true;
		}	
	}
	
	private void updateStartEndDeg(RngBrg rb, AbstractPosTool tool) {
		checkForExceptions();
		GeodeticCoords centGc = m_centerTool.getGeoPos();
		double brg = rb.getBearing();
		double rngKm = compRangeForDeg(brg, m_smjRngBrg.getRanegKm(),
											m_smnRngBrg.getRanegKm());
		rb.setRanegKm(rngKm);
		double deg = m_smjRngBrg.getBearing() - brg;
		GeodeticCoords pos = m_rb.gcPointFrom(centGc, deg, rngKm);
		tool.setGeoPos(pos);
		m_needsUpdate = true;	
	}
	
	protected void updateStartEndDeg(){
		m_startDegTool = getStartDegTool();
		updateStartEndDeg(m_startRngDeg, m_startDegTool);
		m_endDegTool = getEndDegTool();
		updateStartEndDeg(m_endRngDeg, m_endDegTool);		
	}
	
	public IAnchorTool getSmjAnchorTool(){
		if(m_smjTool == null){
			m_smjTool = getSmjTool();
		}
		return (IAnchorTool)m_smjTool;
	}

	protected AbstractPosTool getSmjTool() {
		if (m_smjTool == null) {
			m_smjTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {;
				    if (event != null){
						setSmjFromPix(event.getClientX(), event.getClientY());
						setSmnPosFromSmjBrg();
						updateStartEndDeg();
				    }
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
					updateStartEndDeg();
				}

				@Override
				public void handleMouseOut(Event event) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					if ( m_geoPos != null){
						ViewCoords vc = m_convert.geodeticToView(gc);
						ViewCoords c = m_convert.geodeticToView(getCenter());
						vc = place(c.getX(),c.getY(),vc.getX(),vc.getY(),IN);
						ViewCoords radPt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(radPt, vc, 4);						
					}
					return false;
				}

				@Override
				public void handleMouseDblClick(Event event) {
				}
			};
		}
		return m_smjTool;
	}

	private void setSmnRangePix(int x, int y) {
		checkForExceptions();
		ViewCoords cent = m_convert.geodeticToView(getCenter());
		ViewCoords vc = place(cent.getX(),cent.getY(),x,y,IN);
		GeodeticCoords gc = m_convert.viewToGeodetic(vc);
		if(m_smnTool == null){
			m_smnTool = getSmnTool();
		}
		GeodeticCoords pos = m_smjTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			GeodeticCoords centGc = m_centerTool.getGeoPos();
			double rangeKm = m_rb.gcDistanceFromTo(centGc, gc);
			double bearing = m_smnRngBrg.getBearing();
			m_smnRngBrg.setRanegKm(rangeKm);
			pos = m_rb.gcPointFrom(centGc, bearing , rangeKm);
			m_smnTool.setGeoPos(pos);
			m_needsUpdate = true;
		}	
	}
	
	public void setSmnAxis(GeodeticCoords smnPos){
		if(m_smnTool == null){
			m_smnTool = getSmnTool();
		}
		m_smnTool.setGeoPos(smnPos);
		GeodeticCoords cent = m_centerTool.getGeoPos();
		double rangeKm = m_rb.gcDistanceFromTo(cent, smnPos);
		double brg = m_rb.gcBearingFromTo(cent, smnPos);
		m_smnRngBrg.setRanegKm(rangeKm);
		m_smnRngBrg.setBearing(brg);
		m_needsUpdate = true;		
	}
	
	public IAnchorTool getSmnAnchorTool(){
		if(m_smnTool == null){
			m_smnTool = getSmnTool();
		}
		return (IAnchorTool)m_smnTool;
	}

	protected AbstractPosTool getSmnTool() {
		if (m_smnTool == null) {
			m_smnTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setSmnRangePix(x, y);
					updateStartEndDeg();
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setSmnRangePix(x, y);
					updateStartEndDeg();
				}

				@Override
				public void handleMouseOut(Event event) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					if ( m_geoPos != null){
						ViewCoords vc = m_convert.geodeticToView(gc);
						ViewCoords c = m_convert.geodeticToView(getCenter());
						vc = place(c.getX(),c.getY(),vc.getX(),vc.getY(),IN);
						ViewCoords radPt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(radPt, vc, 4);						
					}
					return false;
				}
				
				@Override
				public void handleMouseDblClick(Event event) {
				}
			};
		}
		return m_smnTool;
	}

	private void setCenterFromPix(int x, int y) {
		checkForExceptions();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if(m_centerTool == null){
			m_centerTool = getCenterTool();
		}
		GeodeticCoords pos = m_centerTool.getGeoPos();
		if (pos == null || !pos.equals(gc)){
			m_centerTool.setGeoPos(gc);
			m_needsUpdate = true;
		}	
	}

	private void moveAxisPos() {
		GeodeticCoords cent = m_centerTool.getGeoPos();
		if (m_smjRngBrg != null) {
			double rng = m_smjRngBrg.getRanegKm();
			double brg = m_smjRngBrg.getBearing();
			
			m_smjTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
		}

		if (m_smnRngBrg != null) {
			double rng = m_smnRngBrg.getRanegKm();
			double brg = m_smnRngBrg.getBearing();
			m_smnTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
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
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveAxisPos();
					updateStartEndDeg();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveAxisPos();
					updateStartEndDeg();
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
				
				@Override
				public void handleMouseDblClick(Event event) {
				}
			};
		}
		return m_centerTool;
	}
	
	public IAnchorTool getStartBrgAnchorTool(){
		if(m_startDegTool == null){
			m_startDegTool = getSmnTool();
		}
		return (IAnchorTool)m_startDegTool;
	}
	
	protected AbstractPosTool getStartDegTool() {
		if (m_startDegTool == null) {
			m_startDegTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartEndBrgFromPix(x, y, m_startRngDeg, this);
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setStartEndBrgFromPix(x, y, m_startRngDeg,this);
				}

				@Override
				public void handleMouseOut(Event event) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					if ( m_geoPos != null){
						ViewCoords vc = m_convert.geodeticToView(gc);
						ViewCoords pt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(pt, vc, 4);						
					}
					return false;
				}
				
				@Override
				public void handleMouseDblClick(Event event) {
				}
			};
		}
		return m_startDegTool;
	}
	
	public IAnchorTool getEndDegAnchorTool(){
		if(m_endDegTool == null){
			m_endDegTool = getSmnTool();
		}
		return (IAnchorTool)m_endDegTool;
	}
	
	protected AbstractPosTool getEndDegTool() {
		if (m_endDegTool == null) {
			m_endDegTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setStartEndBrgFromPix(x, y, m_endRngDeg,this);
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setStartEndBrgFromPix(x, y, m_endRngDeg,this);
				}

				@Override
				public void handleMouseOut(Event event) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					if ( m_geoPos != null){
						ViewCoords vc = m_convert.geodeticToView(gc);
						ViewCoords pt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(pt, vc, 4);						
					}
					return false;
				}
				
				@Override
				public void handleMouseDblClick(Event event) {
				}
			};
		}
		return m_endDegTool;
	}

	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return (IShape)this;
	}

	@Override
	public IShape erase(Context2d context) {
		return null;
	}

	@Override
	public IShape render(Context2d context) {
		draw(context);
		return (IShape)this;
	}

	//
	public GeodeticCoords getSmjPos() {
		return m_smjTool.getGeoPos();
	}

	public void setSmjPos(GeodeticCoords pos) {
		if (m_smjTool == null){
			m_smnTool = getSmjTool();
		}
		m_smjTool.setGeoPos(pos);
	}

	public EArc withSmjPos(GeodeticCoords pos) {
		setSmjPos(pos);
		return this;
	}

	//
	public GeodeticCoords getSmnPos() {
		return m_smnTool.getGeoPos();
	}

	public void setSmnPos(GeodeticCoords pos) {
		if (m_smnTool == null){
			m_smnTool = getSmnTool();
		}
		m_smnTool.setGeoPos(pos);
	}

	public EArc withSmnPos(GeodeticCoords pos) {
		setSmnPos(pos);
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

	public EArc withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}
	
	private void moveHandles(AnchorHandle handle, ViewCoords vc, Context2d context){
		ISplit splitter = m_convert.getISplit();
		if(splitter.isSplit()){
			int side = splitter.switchMove(splitter.side(vc.getX()));
			int x = vc.getX() + splitter.getDistance(side);
			handle.setCenter(x, vc.getY()).draw(context);
		}		
	}

	@Override
	public IShape drawHandles(Context2d context) {
		if (context != null) {
			// Center Handle
			GeodeticCoords gc = getCenter();
			ViewCoords cent = m_convert.geodeticToView(gc);
			m_centerHandle.setCenter(cent.getX(), cent.getY());
			m_centerHandle.draw(context);
			moveHandles(m_centerHandle, cent, context);
			// semi-major handle
			gc = getSmjPos();
			ViewCoords vc = m_convert.geodeticToView(gc);
			vc = place(cent.getX(), cent.getY(), vc.getX(), vc.getY(),OUT);
			m_smjHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_smjHandle, vc, context);
			// semi-minor handle
			gc = getSmnPos();
			vc = m_convert.geodeticToView(gc);
			vc = place(cent.getX(), cent.getY(), vc.getX(), vc.getY(),OUT);
			m_smnHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_smnHandle, vc, context);
			
			// start-deg handle
			gc = m_startDegTool.getGeoPos();
			vc = m_convert.geodeticToView(gc);
			m_startBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_startBrgHandle, vc, context);
			
			// end-deg handle
			gc = m_endDegTool.getGeoPos();
			vc = m_convert.geodeticToView(gc);
			m_endBrgHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_endBrgHandle, vc, context);
		}
		return (IShape)this;
	}
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditEArcTool(se);
	   	tool.setShape(this);
	   	return tool;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		double incBrg = 360.0/(NUM_ARC_PTS-1);

		ViewCoords p1 = compViewPt(rotBrg,0,a,b);
		ViewCoords p2 = compViewPt(rotBrg,incBrg,a,b);
		double dist = Func.ptLineDist(p1, p2, px, py);
		if (dist < eps) {
			return true;
		}
		for (int i = 1; i < NUM_ARC_PTS-1; i++) {
			p1 = p2;
			p2 = compViewPt(rotBrg,(i+1)*incBrg,a,b);
			dist = Func.ptLineDist(p1, p2, px, py);
			if (dist < eps) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForExceptions();
		ViewCoords vc = m_convert.geodeticToView(position);
		GeodeticCoords cent = m_centerTool.getGeoPos();
		ViewCoords centPix = m_convert.geodeticToView(cent);
		if (Func.isClose(centPix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptCloseToEdge(vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForExceptions();
		IAnchorTool tool = getSmjTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getSmnTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getCenterAnchorTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getStartDegTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getEndDegTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		return null;
	}
	
	// This find the intersection range of a line with
	// angle "deg" from the origin to the point of 
	// intersection with the ellipse (with smj: a and smn: b)
	
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
}
