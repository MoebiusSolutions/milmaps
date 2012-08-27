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
import com.moesol.gwt.maps.client.algorithms.SRngBrg;
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;

public class Ellipse extends AbstractShape {
	private static final int NUM_ELLIPSE_PTS = 36;

	protected SRngBrg m_smjRngBrg = null;
	protected SRngBrg m_smnRngBrg = null;
	// private boolean m_mouseDown = true;
	protected AbstractPosTool m_centerTool = null;
	protected AbstractPosTool m_smjTool = null;
	protected AbstractPosTool m_smnTool = null;

	public Ellipse() {
		m_id = "Ellipse";
		m_smjRngBrg = new SRngBrg(0, 0);
		m_smnRngBrg = new SRngBrg(0, 0);
	}

	public static IShape create(ICoordConverter conv, GeodeticCoords center,
								Bearing brg, Distance smj, Distance smn) {
		Ellipse ellipse = new Ellipse();
		ellipse.setCoordConverter(conv);
		ellipse.getCenterTool().setGeoPos(center);
		double deg = brg.bearing().degrees();
		double rngKm = smj.getDistance(DistanceUnit.KILOMETERS);
		ellipse.m_smjRngBrg.widthRangeKm(rngKm).setBearing(deg);
		GeodeticCoords pos = m_rb.gcPointFrom(center, deg, rngKm);
		ellipse.getSmjTool().setGeoPos(pos);
		rngKm = smn.getDistance(DistanceUnit.KILOMETERS);
		ellipse.m_smnRngBrg.widthRangeKm(rngKm).setBearing(deg - 90);
		pos = m_rb.gcPointFrom(center, deg - 90, rngKm);
		ellipse.getSmnTool().setGeoPos(pos);

		return (IShape) ellipse;
	}

	public static IShapeTool create(IShapeEditor editor, GeodeticCoords center,
									Bearing brg, Distance smj, Distance smn) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv, center, brg, smj, smn);
		editor.addShape(shape);
		return shape.createEditTool(editor);
	}

	private void checkForExceptions() {
		if (m_convert == null) {
			throw new IllegalStateException("Ellipse: m_convert = null");
		}
	}
	
	private void checkSmjExist(){
		if (m_smjTool == null) {
			throw new IllegalStateException("Ellipse: m_smjTool = null");
		}
		if (m_smjTool.getGeoPos() == null ){
			throw new IllegalStateException("Ellipse: m_smjTool geoPos = null");
		}
	}

	private void checkCenterExist(){
		if (m_centerTool == null) {
			throw new IllegalStateException("Ellipse: m_centerTool = null");
		}
		if (m_centerTool.getGeoPos() == null ){
			throw new IllegalStateException("Ellipse: m_centerTool geoPos = null");
		}
	}
	
	public void setSmjFromPos(GeodeticCoords gc) {
		checkForExceptions();
		checkCenterExist();
		m_smjTool = getSmjTool();
		GeodeticCoords pos = m_smjTool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
			m_smjTool.setGeoPos(gc);
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_smjRngBrg = m_rb.gcRngBrgFromTo(cent, gc);
			m_needsUpdate = true;
		}
	}

	public void setSmjFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		setSmjFromPos(gc);
	}

	private ViewCoords rngBrgToView(double rngKm, double degBrg) {
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords gc = m_rb.gcPointFrom(cent, degBrg, rngKm);
		return m_convert.geodeticToView(gc);
	}

	private ViewCoords compViewPt(double rotBrg, double brgDeg, double a,
			double b) {
		double angle = Func.DegToRad(brgDeg);
		double x = (a * Math.cos(angle));
		double y = (b * Math.sin(angle));
		if (a != b) {
			angle = Math.atan2(y, x);
		}
		double rngKm = Math.sqrt(x * x + y * y);
		return rngBrgToView(rngKm, rotBrg + Func.RadToDeg(angle));
	}

	protected void drawSegments(Context2d context) {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		ISplit splitter = m_convert.getISplit();
		ViewCoords p, q = compViewPt(rotBrg, 0, a, b);
		// set p to null for first point
		int x = splitter.shift(null, q);
		context.moveTo(x, q.getY());
		double incBrg = 360.0 / (NUM_ELLIPSE_PTS - 1);
		for (int i = 1; i < NUM_ELLIPSE_PTS; i++) {
			p = q;
			q = compViewPt(rotBrg, i * incBrg, a, b);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}
	}

	private void drawBoundary(Context2d context) {
		ISplit splitter = m_convert.getISplit();
		// MUST first initialize
		splitter.initialize(ISplit.NO_ADJUST);
		// ///////////////////////////////////////
		drawSegments(context);

		if (splitter.isSplit()) {
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
		context.closePath();
		context.stroke();
	}

	private void setSmnPosFromSmjBrg() {
		GeodeticCoords smjPos = getSmjPos();
		GeodeticCoords cenPos = getCenter();
		double brgDeg = m_rb.gcBearingFromTo(cenPos, smjPos);
		brgDeg = Func.wrap360(brgDeg - 90);
		double disKm = m_smnRngBrg.getRanegKm();
		m_smnRngBrg.setBearing(brgDeg);
		GeodeticCoords smnPos = m_rb.gcPointFrom(cenPos, brgDeg, disKm);
		setSmnPos(smnPos);
	}

	public IAnchorTool getSmjAnchorTool() {
		if (m_smjTool == null) {
			m_smjTool = getSmjTool();
		}
		return (IAnchorTool) m_smjTool;
	}

	protected AbstractPosTool getSmjTool() {
		if (m_smjTool == null) {
			m_smjTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
				}

				@Override
				public void handleMouseUp(int x, int y) {
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
				}

				@Override
				public void handleMouseOut(int x, int y) {
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
			};
		}
		return m_smjTool;
	}

	private void setSmnRangePix(int x, int y) {
		checkForExceptions();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_smnTool == null) {
			m_smnTool = getSmnTool();
		}
		GeodeticCoords pos = m_smjTool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
			GeodeticCoords cent = m_centerTool.getGeoPos();
			double rangeKm = m_rb.gcRangeFromTo(cent, gc);
			double bearing = m_smnRngBrg.getBearing();
			m_smnRngBrg.setRangeKm(rangeKm);
			pos = m_rb.gcPointFrom(cent, bearing, rangeKm);
			m_smnTool.setGeoPos(pos);
			m_needsUpdate = true;
		}
	}
	
	// The center position and the  semi-major axis must 
	// be set before using this method
	public void setSmnAxis(Distance dis) {
		checkSmjExist();
		checkCenterExist();

		double brgDeg = m_rb.gcBearingFromTo(m_centerTool.getGeoPos(), 
											 m_smjTool.getGeoPos());
		brgDeg = Func.wrap360(brgDeg-90);
		m_smnTool = getSmnTool();
		double disKm = dis.getDistance(DistanceUnit.KILOMETERS);
		GeodeticCoords pos = m_rb.gcPointFrom(m_centerTool.getGeoPos(), brgDeg, disKm);
		m_smnTool.setGeoPos(pos);
		m_smnRngBrg.setRangeKm(disKm);
		m_smnRngBrg.setBearing(brgDeg);
		m_needsUpdate = true;
	}

	
	public IAnchorTool getSmnAnchorTool() {
		if (m_smnTool == null) {
			m_smnTool = getSmnTool();
		}
		return (IAnchorTool) m_smnTool;
	}

	protected AbstractPosTool getSmnTool() {
		if (m_smnTool == null) {
			m_smnTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					setSmnRangePix(x, y);
				}

				@Override
				public void handleMouseUp(int x, int y) {
					setSmnRangePix(x, y);
				}

				@Override
				public void handleMouseOut(int x, int y) {
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
			};
		}
		return m_smnTool;
	}

	private void setCenterFromPix(int x, int y) {
		checkForExceptions();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_centerTool == null) {
			m_centerTool = getCenterTool();
		}
		GeodeticCoords pos = m_centerTool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
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

	public IAnchorTool getCenterAnchorTool() {
		if (m_centerTool == null) {
			m_centerTool = getCenterTool();
		}
		return (IAnchorTool) m_centerTool;
	}

	protected AbstractPosTool getCenterTool() {
		if (m_centerTool == null) {
			m_centerTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					setCenterFromPix(x, y);
					moveAxisPos();
				}

				@Override
				public void handleMouseUp(int x, int y) {
					setCenterFromPix(x, y);
					moveAxisPos();
				}

				@Override
				public void handleMouseOut(int x, int y) {
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
			};
		}
		return m_centerTool;
	}

	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return (IShape) this;
	}

	@Override
	public IShape erase(Context2d context) {
		return null;
	}

	@Override
	public IShape render(Context2d context) {
		syncColor();
		draw(context);
		return (IShape) this;
	}

	//
	public GeodeticCoords getSmjPos() {
		return m_smjTool.getGeoPos();
	}

	//
	public GeodeticCoords getSmnPos() {
		return m_smnTool.getGeoPos();
	}

	private void setSmnPos(GeodeticCoords pos) {
		if (m_smnTool == null) {
			m_smnTool = getSmnTool();
		}
		m_smnTool.setGeoPos(pos);
	}
	
	public GeodeticCoords getCenter() {
		return m_centerTool.getGeoPos();
	}

	public void setCenter(GeodeticCoords center) {
		if (m_centerTool == null) {
			m_centerTool = getCenterTool();
		}
		m_centerTool.setGeoPos(center);
	}

	public Ellipse withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}

	private void moveHandles(AnchorHandle handle, ViewCoords vc,
			Context2d context) {
		ISplit splitter = m_convert.getISplit();
		if (splitter.isSplit()) {
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
			ViewCoords vc = m_convert.geodeticToView(gc);
			AnchorHandle handle = new AnchorHandle();
			handle.setStrokeColor(255, 255, 255, 1);
			handle.setCenter(vc.getX(), vc.getY());
			handle.draw(context);
			moveHandles(handle, vc, context);
			// semi-major handle
			gc = getSmjPos();
			vc = m_convert.geodeticToView(gc);
			handle.setStrokeColor(255, 0, 0, 1);
			handle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(handle, vc, context);
			// semi-minor handle
			gc = getSmnPos();
			vc = m_convert.geodeticToView(gc);
			handle.setStrokeColor(255, 255, 255, 1);
			handle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(handle, vc, context);
		}
		return (IShape) this;
	}

	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
		IShapeTool tool = new CommonEditTool(se);
		tool.setShape(this);
		return tool;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		double incBrg = 360.0 / (NUM_ELLIPSE_PTS - 1);

		ViewCoords p1 = compViewPt(rotBrg, 0, a, b);
		ViewCoords p2 = compViewPt(rotBrg, incBrg, a, b);
		double dist = Func.ptLineDist(p1, p2, px, py);
		if (dist < eps) {
			return true;
		}
		for (int i = 1; i < NUM_ELLIPSE_PTS - 1; i++) {
			p1 = p2;
			p2 = compViewPt(rotBrg, (i + 1) * incBrg, a, b);
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
		return null;
	}
}
