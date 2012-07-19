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
import com.moesol.gwt.maps.client.units.AngleUnit;

public class Rect extends AbstractSegment {
	private final AnchorHandle m_startHandle = new AnchorHandle();
	private final AnchorHandle m_endHandle = new AnchorHandle();
	private RngBrg m_diagRngBrg = null;
	private AbstractPosTool m_startTool = null;
	private AbstractPosTool m_endTool = null;
	protected AbstractPosTool m_translationTool = null;
	private final AnchorHandle m_translationHandle = new AnchorHandle();
	
	private int m_X, m_Y;
	
	public Rect(){
		m_id = "Rectangle";
		m_translationHandle.setStrokeColor(255, 0, 0, 1);
	}
	
	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Rect: m_convert = null");
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
		m_diagRngBrg = m_rb.gcRngBrgFromTo(startGc, endGc);
		
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
		double rngKm = m_diagRngBrg.getRanegKm();
		double brg = m_diagRngBrg.getBearing();
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
	
	private void drawSegments(Context2d context){
		GeodeticCoords tl = m_startTool.getGeoPos();
		GeodeticCoords br = m_endTool.getGeoPos();
		GeodeticCoords tr = new GeodeticCoords(br.getLambda(AngleUnit.DEGREES),
											   tl.getPhi(AngleUnit.DEGREES),
											   AngleUnit.DEGREES);
		GeodeticCoords bl = new GeodeticCoords(tl.getLambda(AngleUnit.DEGREES),
				   							   br.getPhi(AngleUnit.DEGREES),
				   							   AngleUnit.DEGREES);
		ISplit splitter = m_convert.getISplit();
		// MUST initialize with the next three lines
		splitter.setAjustFlag(false).setSplit(false);
		splitter.setMove(ConvertBase.DONT_MOVE);
		drawBoxSides(tl, tr, br, bl,context);
		if (splitter.isSplit()){
			// Must initialize with new values.
			splitter.setAjustFlag(true);
			splitter.setMove(splitter.switchMove(splitter.getMove()));
			drawBoxSides(tl, tr, br, bl,context);
		}
	}
	
	private void drawBoundary(Context2d context) {
		checkForException();
		/////////////////////////////////////////
		drawSegments(context);
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

	public Rect withEndPos(GeodeticCoords radPos) {
		setEndPos(radPos);
		return this;
	}

	public GeodeticCoords getStartPos() {
		return m_startTool.getGeoPos();
	}

	public void setTlPos(GeodeticCoords pos) {
		if (m_startTool == null){
			m_startTool = getStartTool();
		}
		m_startTool.setGeoPos(pos);
	}

	public Rect withStartPos(GeodeticCoords pos) {
		setTlPos(pos);
		return this;
	}
	
	protected boolean ptClose(int px, int py, double eps){
		GeodeticCoords tl = m_startTool.getGeoPos();
		GeodeticCoords br = m_endTool.getGeoPos();
		GeodeticCoords tr = new GeodeticCoords(br.getLambda(AngleUnit.DEGREES),
											   tl.getPhi(AngleUnit.DEGREES),
											   AngleUnit.DEGREES);
		GeodeticCoords bl = new GeodeticCoords(tl.getLambda(AngleUnit.DEGREES),
				   							   br.getPhi(AngleUnit.DEGREES),
				   							   AngleUnit.DEGREES);
		
		// Top left to top right
		if (ptClose(tl,tr,px,py,eps)){
			return true;
		}
		//top right to bottom right
		if (ptClose(tr,br,px,py,eps)){
			return true;
		}
		// bottom right to bottom left
		if (ptClose(br,bl,px,py,eps)){
			return true;
		}
		
		// bottom left to top left
		if (ptClose(bl,tl,px,py,eps)){
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
	
	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
	   	IShapeTool tool = new EditRectTool(se);
	   	tool.setShape(this);
	   	return tool;
	}
}
