/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.SRngBrg;
import com.moesol.gwt.maps.client.units.AngleUnit;

public class Rectangle extends AbstractSegment {
	private SRngBrg m_diagRngBrg = null;
	private AbstractPosTool m_startTool = null;
	private AbstractPosTool m_endTool = null;
	protected AbstractPosTool m_translationTool = null;
	private final AnchorHandle m_translationHandle = new AnchorHandle();

	private int m_X, m_Y;

	public Rectangle() {
		m_id = "Rectangle";
	}

	public static IShape create(ICoordConverter conv, GeodeticCoords start,
													  GeodeticCoords end) {
		Rectangle rect = new Rectangle();
		rect.setCoordConverter(conv);
		rect.setCornerPts(start, end);
		return (IShape) rect;
	}

	public static IShapeTool create(IShapeEditor editor, GeodeticCoords start,
														 GeodeticCoords end) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv, start, end);
		return shape.createEditTool(editor);
	}

	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Rect: m_convert = null");
		}
	}
	
	private void checkStartExist(){
		if (m_startTool == null) {
			throw new IllegalStateException("Rect: m_startTool = null");
		}
		if (m_startTool.getGeoPos() == null ){
			throw new IllegalStateException("Rect: m_startTool geoPos = null");
		}
	}

	private void checkEndExist(){
		if (m_endTool == null) {
			throw new IllegalStateException("Rect: m_endTool = null");
		}
		if (m_endTool.getGeoPos() == null ){
			throw new IllegalStateException("Rect: m_endTool geoPos = null");
		}
	}
	
	public void setCornerPts(GeodeticCoords start, GeodeticCoords end) {
		getStartTool().setGeoPos(start);
		updateTranslationHandle();
		getEndTool().setGeoPos(end);
		updateRngBrg();
	}

	private void setPosFromPix(int x, int y, AbstractPosTool tool) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords pos = tool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
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

	public IAnchorTool getStartAnchorTool() {
		if (m_startTool == null) {
			m_startTool = getStartTool();
		}
		return (IAnchorTool) m_startTool;
	}

	protected AbstractPosTool getStartTool() {
		if (m_startTool == null) {
			m_startTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					m_startTool = getStartTool();
					setPosFromPix(x, y, m_startTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseUp(int x, int y) {
					m_startTool = getStartTool();
					setPosFromPix(x, y, m_startTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseOut(int x, int y) {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					if (m_geoPos != null) {
						ViewCoords pt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(pt, vc, 4);
					}
					return false;
				}
			};
		}
		return m_startTool;
	}

	public IAnchorTool getEndAnchorTool() {
		if (m_endTool == null) {
			m_endTool = getEndTool();
		}
		return (IAnchorTool) m_endTool;
	}

	protected AbstractPosTool getEndTool() {
		if (m_endTool == null) {
			m_endTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					m_endTool = getEndTool();
					setPosFromPix(x, y, m_endTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseUp(int x, int y) {
					m_endTool = getEndTool();
					setPosFromPix(x, y, m_endTool);
					updateRngBrg();
					updateTranslationHandle();
				}

				@Override
				public void handleMouseOut(int x, int y) {
					updateRngBrg();
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					if (m_geoPos != null) {
						ViewCoords radPt = m_convert.geodeticToView(m_geoPos);
						return Func.isClose(radPt, vc, 4);
					}
					return false;
				}

				@Override
				public void handleMouseDblClick(int x, int y) {
				}

				@Override
				public void handleKeyDown(int keyCode) {
				}

				@Override
				public void handleKeyUp(int keyCode) {
				}
			};
		}
		return m_endTool;
	}

	// TODO
	private void updateTranslationHandle() {
		GeodeticCoords gc = m_startTool.getGeoPos();
		ViewCoords p = m_convert.geodeticToView(gc);
		int x = p.getX() - TRANSLATE_HANDLE_OFFSET_X;
		m_translationHandle.setCenter(x, p.getY());
	}

	private void moveRectByOffset(int x, int y) {
		ViewCoords vc = m_convert.geodeticToView(getStartPos());
		setPosFromPix(vc.getX()+x,vc.getY()+y,m_startTool);
		double rngKm = m_diagRngBrg.getRanegKm();
		double brg = m_diagRngBrg.getBearing();
		GeodeticCoords gc = m_rb.gcPointFrom(m_startTool.getGeoPos(), brg,
				rngKm);
		m_endTool.setGeoPos(gc);
		// Update translation handle
		updateTranslationHandle();
	}

	protected AbstractPosTool getTranslationTool() {
		if (m_translationTool == null) {
			m_translationTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					moveRectByOffset(x - m_X, y - m_Y);
					m_X = x;
					m_Y = y;
				}

				@Override
				public void handleMouseUp(int x, int y) {
					moveRectByOffset(x - m_X, y - m_Y);
					m_X = x;
					m_Y = y;
				}

				@Override
				public void handleMouseOut(int x, int y) {
				}

				@Override
				public void handleMouseDblClick(int x, int y) {
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

	private void drawSegments(IContext context) {
		GeodeticCoords tl = m_startTool.getGeoPos();
		GeodeticCoords br = m_endTool.getGeoPos();
		GeodeticCoords tr = new GeodeticCoords(br.getLambda(AngleUnit.DEGREES),
				tl.getPhi(AngleUnit.DEGREES), AngleUnit.DEGREES);
		GeodeticCoords bl = new GeodeticCoords(tl.getLambda(AngleUnit.DEGREES),
				br.getPhi(AngleUnit.DEGREES), AngleUnit.DEGREES);
		ISplit splitter = m_convert.getISplit();
		// MUST first initialize
		splitter.initialize(ISplit.NO_ADJUST);
		drawBoxSides(tl, tr, br, bl, context);
		if (splitter.isSplit()) {
			// Must initialize with new values.
			splitter.initialize(ISplit.ADJUST);
			drawBoxSides(tl, tr, br, bl, context);
		}
	}

	private void drawBoundary(IContext context) {
		checkForException();
		// ///////////////////////////////////////
		drawSegments(context);
	}

	private void draw(IContext context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
		context.stroke();
	}

	@Override
	public IShape erase(IContext ct) {
		// _erase(ct);
		return (IShape) this;
	}

	@Override
	public IShape render(IContext ct) {
		draw(ct);
		return (IShape) this;
	}

	@Override
	public IShape drawHandles(IContext context) {
		if (context != null) {
			ISplit splitter = m_convert.getISplit();
			// Center Handle
			GeodeticCoords gc = getStartPos();
			ViewCoords vc = m_convert.geodeticToView(gc);
			AnchorHandle startHandle = new AnchorHandle();
			startHandle.setStrokeColor(255, 255, 255, 1);
			startHandle.setCenter(vc.getX(), vc.getY());
			startHandle.draw(context);
			if (splitter.isSplit()) {
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				startHandle.setCenter(x, vc.getY()).draw(context);
			}

			gc = getEndPos();
			vc = m_convert.geodeticToView(gc);
			AnchorHandle endHandle = new AnchorHandle();
			endHandle.setStrokeColor(255, 255, 255, 1);
			endHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			if (splitter.isSplit()) {
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				endHandle.setCenter(x, vc.getY()).draw(context);
			}
			int x = startHandle.getX() - TRANSLATE_HANDLE_OFFSET_X;
			int y = startHandle.getY();
			m_translationHandle.setStrokeColor(255, 0, 0, 1);
			m_translationHandle.setCenter(x, y).draw(context);
			if (splitter.isSplit()) {
				int side = splitter.switchMove(splitter.side(x));
				x += splitter.getDistance(side);
				m_translationHandle.setCenter(x, y).draw(context);
			}
		}
		return (IShape) this;
	}

	public GeodeticCoords getEndPos() {
		return m_endTool.getGeoPos();
	}
	

	public void setEndPos(GeodeticCoords radPos) {
		checkStartExist();
		getEndTool().setGeoPos(radPos);
		updateRngBrg();
	}

	public Rectangle withEndPos(GeodeticCoords radPos) {
		setEndPos(radPos);
		return this;
	}

	public GeodeticCoords getStartPos() {
		return m_startTool.getGeoPos();
	}

	public void setStartPos(GeodeticCoords pos) {
		getStartTool().setGeoPos(pos);
		updateTranslationHandle();
		if ( m_endTool != null){
			updateRngBrg();
		}
	}

	public Rectangle withStartPos(GeodeticCoords pos) {
		setStartPos(pos);
		return this;
	}

	protected boolean ptClose(int px, int py, double eps) {
		GeodeticCoords tl = m_startTool.getGeoPos();
		GeodeticCoords br = m_endTool.getGeoPos();
		GeodeticCoords tr = new GeodeticCoords(br.getLambda(AngleUnit.DEGREES),
				tl.getPhi(AngleUnit.DEGREES), AngleUnit.DEGREES);
		GeodeticCoords bl = new GeodeticCoords(tl.getLambda(AngleUnit.DEGREES),
				br.getPhi(AngleUnit.DEGREES), AngleUnit.DEGREES);

		// Top left to top right
		if (ptClose(tl, tr, px, py, eps)) {
			return true;
		}
		// top right to bottom right
		if (ptClose(tr, br, px, py, eps)) {
			return true;
		}
		// bottom right to bottom left
		if (ptClose(br, bl, px, py, eps)) {
			return true;
		}

		// bottom left to top left
		if (ptClose(bl, tl, px, py, eps)) {
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
		gc = m_endTool.getGeoPos();
		pix = m_convert.geodeticToView(gc);
		if (Func.isClose(pix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptClose(vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		AbstractPosTool tool = getEndTool();
		if (tool.isSlected(position)) {
			return (IAnchorTool) tool;
		}
		tool = getStartTool();
		if (tool.isSlected(position)) {
			return (IAnchorTool) tool;
		}
		tool = getTranslationTool();
		setPosFromPix(m_translationHandle.getX(), m_translationHandle.getY(),
				tool);
		if (tool.isSlected(position)) {
			m_X = m_translationHandle.getX();
			m_Y = m_translationHandle.getY();
			return (IAnchorTool) tool;
		}
		return null;
	}

	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
		IShapeTool tool = new CommonEditTool(se);
		tool.setShape(this);
		return tool;
	}
}
