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

public class Line extends AbstractShape {
	private static final int NUM_LINE_PTS = 36;
	protected static int TRANSLATE_HANDLE_OFFSET_X = 20;
	private static final RangeBearingS m_rb = new RangeBearingS();
	private final AnchorHandle m_startHandle = new AnchorHandle();
	private final AnchorHandle m_endHandle = new AnchorHandle();
	private RngBrg m_endRngBrg = null;
	private AbstractPosTool m_startTool = null;
	private AbstractPosTool m_endTool = null;
	protected AbstractPosTool m_translationTool = null;
	private final AnchorHandle m_translationHandle = new AnchorHandle();
	
	private int m_X, m_Y;
	
	public Line(){
		m_id = "Line";
		m_translationHandle.setStrokeColor(255, 0, 0, 1);
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Line: m_convert = null");
		}
	}
	
	private void setPosFromPix(int x, int y, AbstractPosTool tool) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords pos = tool.getGeoPos();
		if (pos == null  || !pos.equals(gc)) {
			tool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}

	private void updateRngBrg() {
		checkForException();
		GeodeticCoords startGc = m_startTool.getGeoPos();
		GeodeticCoords endGc = m_endTool.getGeoPos();
		m_endRngBrg = m_rb.RngBrgFromTo(startGc, endGc);
		
	}
	
	public IAnchorTool getStartAnchorTool(){
		if(m_startTool == null){
			m_startTool = getStartTool();
		}
		return (IAnchorTool)m_startTool;
	}

	protected AbstractPosTool getStartTool() {
		if (m_startTool == null) {
			m_startTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {;
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					m_startTool = getStartTool();
					setPosFromPix(x,y,m_startTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					m_startTool = getStartTool();
					setPosFromPix(x,y,m_startTool);
					updateRngBrg();
					updateTranslationHandle();
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
		return m_startTool;
	}
	
	public IAnchorTool getEndAnchorTool(){
		if(m_endTool == null){
			m_endTool = getEndTool();
		}
		return (IAnchorTool)m_endTool;
	}

	protected AbstractPosTool getEndTool() {
		if (m_endTool == null) {
			m_endTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					m_endTool = getEndTool();
					setPosFromPix(x,y,m_endTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void  handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					m_endTool = getEndTool();
					setPosFromPix(x,y,m_endTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseOut(Event event) {
					updateRngBrg();
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
		return m_endTool;
	}
	
	// TODO
	private void updateTranslationHandle(){
		GeodeticCoords gc = m_startTool.getGeoPos();
		ViewCoords p = m_convert.geodeticToView(gc); 
		 int x = p.getX()- TRANSLATE_HANDLE_OFFSET_X;
		 m_translationHandle.setCenter(x, p.getY());
	}
	
	private void moveLineByOffset(int x, int y){
		m_startHandle.moveByOffset(x, y);
		int ix = m_startHandle.getX();
		int iy = m_startHandle.getY();
		setPosFromPix(ix,iy,m_startTool);
		double rngKm = m_endRngBrg.getRanegKm();
		double brg = m_endRngBrg.getBearing();
		GeodeticCoords gc = m_rb.gcPointFrom(m_startTool.getGeoPos(), brg, rngKm);
		m_endTool.setGeoPos(gc);
		// Update translation handle
		m_translationHandle.setCenter(x, y);
	}
	
	protected AbstractPosTool getTranslationTool(){
		if (m_translationTool == null){
			m_translationTool = new AbstractPosTool(){
				@Override
				public void handleMouseDown(Event event) {
				}
	
				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX()- m_X;
					int y = event.getClientY()- m_Y;
					moveLineByOffset(x,y);
					m_X = event.getClientX();
					m_Y = event.getClientY();
				}
	
				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX()- m_X;
					int y = event.getClientY()- m_Y;
					moveLineByOffset(x,y);
					m_X = event.getClientX();
					m_Y = event.getClientY();
				}
	
				@Override
				public void handleMouseOut(Event event) {
				}
	
				@Override
				public void handleMouseDblClick(Event event) {
				}
				
				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					ViewCoords pt = m_convert.geodeticToView(m_geoPos);
					return Func.isClose(pt, vc, 4);
				}
			};
		}
		return m_translationTool;
	}
	
	protected void drawSegments(Context2d context){
		double lenInc = m_endRngBrg.getRanegKm()/NUM_LINE_PTS;
		ISplit splitter = m_convert.getISplit();
		GeodeticCoords gc = m_startTool.getGeoPos();
		ViewCoords p, q;
		q = m_convert.geodeticToView(gc);  
		double brng = m_rb.gcBearingFromTo(gc,m_endTool.getGeoPos());
		int move = splitter.getMove();
		int x = q.getX();
		if ( move!= ConvertBase.DONT_MOVE){
			x += splitter.getDistance(move);
		}
		context.moveTo(x, q.getY());
		for (int i = 1; i < NUM_LINE_PTS-1; i++) {
			p = q;
			brng = m_rb.gcBearingFromTo(gc, m_endTool.getGeoPos());
			gc = m_rb.gcPointFrom(gc, brng, lenInc);	
			q = m_convert.geodeticToView(gc);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}
		p = q;
		q = m_convert.geodeticToView(m_endTool.getGeoPos());
		x = splitter.shift(p, q);
		context.lineTo(x, q.getY());
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
			GeodeticCoords gc = getStartPos();
			ViewCoords vc = m_convert.geodeticToView(gc);
			m_startHandle.setCenter(vc.getX(), vc.getY());
			m_startHandle.draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_startHandle.setCenter(x, vc.getY()).draw(context);
			}

			gc = getEndPos();
			vc = m_convert.geodeticToView(gc);
			m_endHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				m_endHandle.setCenter(x, vc.getY()).draw(context);
			}
			int x = m_startHandle.getX()-TRANSLATE_HANDLE_OFFSET_X;
			int y = m_startHandle.getY();
			m_translationHandle.setCenter(x, y).draw(context);
			if(splitter.isSplit()){
				int side = splitter.switchMove(splitter.side(x));
				x += splitter.getDistance(side);
				m_translationHandle.setCenter(x, y).draw(context);
			}
		}
		return (IShape)this;
	}

	public GeodeticCoords getEndPos() {
		return m_endTool.getGeoPos();
	}

	public void setEndPos(GeodeticCoords radPos) {
		m_endTool.setGeoPos(radPos);
	}

	public Line withEndPos(GeodeticCoords radPos) {
		setEndPos(radPos);
		return this;
	}

	public GeodeticCoords getStartPos() {
		return m_startTool.getGeoPos();
	}

	public void setStartPos(GeodeticCoords pos) {
		if (m_startTool == null){
			m_startTool = getStartTool();
		}
		m_startTool.setGeoPos(pos);
	}

	public Line withStartPos(GeodeticCoords pos) {
		setStartPos(pos);
		return this;
	}
	
	protected boolean ptClose(int px, int py, double eps){
		double lenInc = m_endRngBrg.getRanegKm()/NUM_LINE_PTS;
		GeodeticCoords gc = m_startTool.getGeoPos();
		ViewCoords p, q;
		q = m_convert.geodeticToView(gc);  
		double brng = m_rb.gcBearingFromTo(gc,m_endTool.getGeoPos());
		/////////////////////////////////////////;  
		for (int i = 1; i < NUM_LINE_PTS-1; i++) {
			p = q;
			brng = m_rb.gcBearingFromTo(gc, m_endTool.getGeoPos());
			gc = m_rb.gcPointFrom(gc, brng, lenInc);	
			q = m_convert.geodeticToView(gc);
			double dist = Func.ptLineDist(p, q, px, py);
			if (dist < eps) {
				return true;
			}
		}
		p = q;
		q = m_convert.geodeticToView(m_endTool.getGeoPos());
		double dist = Func.ptLineDist(p, q, px, py);
		if (dist < eps) {
			return true;
		}
		return false;
	}


	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForException();
		ViewCoords vc = m_convert.geodeticToView(position);
		GeodeticCoords gc = m_startTool.getGeoPos();
		ViewCoords pix = m_convert.geodeticToView(gc);
		if (Func.isClose(pix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		gc = m_startTool.getGeoPos();
		pix = m_convert.geodeticToView(gc);
		if (Func.isClose(pix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptClose( vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		AbstractPosTool tool = getEndTool();
		if (tool.isSlected(position)){
			return (IAnchorTool)tool;
		}
		tool = getStartTool();
		if (tool.isSlected(position)){
			return (IAnchorTool)tool;
		}
		tool = getTranslationTool();
		setPosFromPix(m_translationHandle.getX(),
					  m_translationHandle.getY(),tool);
		if (tool.isSlected(position)){
			m_X = m_translationHandle.getX();
			m_Y = m_translationHandle.getY();
			return (IAnchorTool)tool;
		}
		return null;
	}

}
