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
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;

public class Circle extends AbstractShape {
	private static final int NUM_CIR_PTS = 36;
	private SRngBrg m_radRngBrg = null;
	private AbstractPosTool m_radiusTool = null;
	private AbstractPosTool m_centerTool = null;

	public Circle() {
		m_id = "Circle";
	}

	public static IShape create(ICoordConverter conv, GeodeticCoords center,
								Distance radius) {
		Circle circle = new Circle();
		circle.setCoordConverter(conv);
		circle.getCenterTool().setGeoPos(center);
		double rngKm = radius.getDistance(DistanceUnit.KILOMETERS);
		GeodeticCoords pos = m_rb.gcPointFrom(center, 90, rngKm);
		circle.getRadiusTool().setGeoPos(pos);
		circle.m_radRngBrg = new SRngBrg(rngKm, 90);
		return (IShape) circle;
	}

	public static IShapeTool create(IShapeEditor editor, GeodeticCoords center,
									Distance radius) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv,center,radius);
		editor.addShape(shape);
		return shape.createEditTool(editor);
	}

	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Circle: m_convert = null");
		}
	}

	private void setRadiusFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_radiusTool == null) {
			m_radiusTool = getRadiusTool();
		}
		GeodeticCoords pos = m_radiusTool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
			m_radiusTool.setGeoPos(gc);
			updateRadisRngBrg();
			m_needsUpdate = true;
		}
	}

	private void updateRadisRngBrg() {
		checkForException();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords radPos = m_radiusTool.getGeoPos();
		m_radRngBrg = m_rb.gcRngBrgFromTo(cent, radPos);
	}

	public IAnchorTool getRadiusAnchorTool() {
		if (m_radiusTool == null) {
			m_radiusTool = getRadiusTool();
		}
		return (IAnchorTool) m_radiusTool;
	}

	protected AbstractPosTool getRadiusTool() {
		if (m_radiusTool == null) {
			m_radiusTool = new AbstractPosTool() {
				@Override
				public void handleMouseDown(int x, int y) {
				}

				@Override
				public void handleMouseMove(int x, int y) {
					setRadiusFromPix(x, y);
				}

				@Override
				public void handleMouseUp(int x, int y) {
					setRadiusFromPix(x, y);
					updateRadisRngBrg();
				}

				@Override
				public void handleMouseOut(int x, int y) {
					updateRadisRngBrg();
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
		return m_radiusTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		GeodeticCoords cent = m_centerTool.getGeoPos();
		if (cent == null || !cent.equals(gc)) {
			m_centerTool.setGeoPos(gc);
			m_needsUpdate = true;
		}
	}

	private void moveRadiusPos() {
		if (m_radRngBrg == null) {
			updateRadisRngBrg();
		}
		double rng = m_radRngBrg.getRanegKm();
		double brg = m_radRngBrg.getBearing();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		m_radiusTool.setGeoPos(m_rb.gcPointFrom(cent, brg, rng));
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
					moveRadiusPos();
				}

				@Override
				public void handleMouseUp(int x, int y) {
					setCenterFromPix(x, y);
					moveRadiusPos();
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
			};
		}
		return m_centerTool;
	}

	protected ViewCoords getBoundaryPt(double brg, double distKm) {
		GeodeticCoords cent = m_centerTool.getGeoPos();
		GeodeticCoords gc = m_rb.gcPointFrom(cent, brg, distKm);
		return m_convert.geodeticToView(gc);
	}

	protected void drawSegments(IContext context) {
		double degInc = 360.0 / (NUM_CIR_PTS - 1);
		double distKm = m_radRngBrg.getRanegKm();
		ISplit splitter = m_convert.getISplit();
		ViewCoords p, q;
		q = getBoundaryPt(0, distKm);
		// set p to null for first point
		int x = splitter.shift(null, q);
		context.moveTo(x, q.getY());
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			p = q;
			double brng = degInc * i;
			q = getBoundaryPt(brng, distKm);
			x = splitter.shift(p, q);
			context.lineTo(x, q.getY());
		}
	}

	private void drawBoundary(IContext context) {
		checkForException();
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

	private void draw(IContext context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
		context.closePath();
		context.stroke();
	}

	@Override
	public IShape erase(IContext ct) {
		// _erase(ct);
		return (IShape) this;
	}

	@Override
	public IShape render(IContext ct) {
		syncColor();
		draw(ct);
		return (IShape) this;
	}

	@Override
	public IShape drawHandles(IContext context) {
		if (context != null) {
			ISplit splitter = m_convert.getISplit();
			// Center Handle
			GeodeticCoords gc = getCenter();
			ViewCoords vc = m_convert.geodeticToView(gc);
			AnchorHandle handle = new AnchorHandle();
			handle.setStrokeColor(255, 255, 255, 1);
			handle.setCenter(vc.getX(), vc.getY());
			handle.draw(context);
			if (splitter.isSplit()) {
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				handle.setCenter(x, vc.getY()).draw(context);
			}
			// Radius handle
			gc = getRadiusPos();
			vc = m_convert.geodeticToView(gc);
			handle.setStrokeColor(255, 0, 0, 1);
			handle.setCenter(vc.getX(), vc.getY()).draw(context);
			if (splitter.isSplit()) {
				int side = splitter.switchMove(splitter.side(vc.getX()));
				int x = vc.getX() + splitter.getDistance(side);
				handle.setCenter(x, vc.getY()).draw(context);
			}
		}
		return (IShape) this;
	}

	public GeodeticCoords getRadiusPos() {
		return m_radiusTool.getGeoPos();
	}

	public void setRadiusPos(GeodeticCoords radPos) {
		m_radiusTool = getRadiusTool();
		m_radiusTool.setGeoPos(radPos);
		updateRadisRngBrg();
	}

	public Circle withRadiusPos(GeodeticCoords radPos) {
		setRadiusPos(radPos);
		return this;
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

	public Circle withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}

	protected boolean ptCloseToEdge(int px, int py, double eps) {
		double degInc = 360.0 / (NUM_CIR_PTS - 1);
		double distKm = m_radRngBrg.getRanegKm();
		// ///////////////////////////////////////
		ViewCoords p, q = getBoundaryPt(0, distKm);
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			double brg = degInc * i;
			p = q;
			q = getBoundaryPt(brg, distKm);
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
		return ptCloseToEdge(vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		AbstractPosTool tool = getRadiusTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getCenterTool();
		if (tool.isSlected(position)) {
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
