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
import com.moesol.gwt.maps.client.units.Bearing;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;

public class Box extends AbstractSegment {
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_smjHandle = new AnchorHandle();
	private final AnchorHandle m_smnHandle = new AnchorHandle();

	protected RngBrg m_smjRngBrg = null;
	protected RngBrg m_smnRngBrg = null;
	// private boolean m_mouseDown = true;
	protected AbstractPosTool m_centerTool = null;
	protected AbstractPosTool m_smjTool = null;
	protected AbstractPosTool m_smnTool = null;

	public Box() {
		m_id = "Box";
		m_smjRngBrg = new RngBrg(0, 0);
		m_smnRngBrg = new RngBrg(0, 0);
	}

	public static IShape create(ICoordConverter conv, GeodeticCoords center,
									Bearing brg, Distance smj, Distance smn) {
		Box box = new Box();
		box.setCoordConverter(conv);
		box.getCenterTool().setGeoPos(center);
		double deg = brg.bearing().degrees();
		double rngKm = smj.getDistance(DistanceUnit.KILOMETERS);
		box.m_smjRngBrg.widthRangeKm(rngKm).setBearing(deg);
		GeodeticCoords pos = m_rb.gcPointFrom(center, deg, rngKm);
		box.getSmjTool().setGeoPos(pos);
		rngKm = smn.getDistance(DistanceUnit.KILOMETERS);
		box.m_smnRngBrg.widthRangeKm(rngKm).setBearing(deg - 90);
		pos = m_rb.gcPointFrom(center, deg - 90, rngKm);
		box.getSmnTool().setGeoPos(pos);

		return (IShape) box;
	}

	public static IShapeTool create(IShapeEditor editor, GeodeticCoords center,
									Bearing brg, Distance smj, Distance smn) {
		ICoordConverter conv = editor.getCoordinateConverter();
		IShape shape = create(conv,center,brg,smj,smn);
		editor.addShape(shape);
		return shape.createEditTool(editor);
	}

	private void checkForExceptions() {
		if (m_convert == null) {
			throw new IllegalStateException("Ellipse: m_convert = null");
		}
	}

	private void setSmjFromPix(int x, int y) {
		checkForExceptions();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_smjTool == null) {
			m_smjTool = getSmjTool();
		}
		GeodeticCoords pos = m_smjTool.getGeoPos();
		if (pos == null || !pos.equals(gc)) {
			m_smjTool.setGeoPos(gc);
			GeodeticCoords cent = m_centerTool.getGeoPos();
			m_smjRngBrg = m_rb.gcRngBrgFromTo(cent, gc);
			m_needsUpdate = true;
		}
	}

	protected void drawSegments(Context2d context) {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		double theta = Func.RadToDeg(Math.atan2(b, a));
		double rngKm = Math.sqrt(a * a + b * b);
		GeodeticCoords tl = m_rb.gcPointFrom(cent, rotBrg - 180 + theta, rngKm);
		GeodeticCoords tr = m_rb.gcPointFrom(cent, rotBrg - theta, rngKm);
		GeodeticCoords br = m_rb.gcPointFrom(cent, rotBrg + theta, rngKm);
		GeodeticCoords bl = m_rb.gcPointFrom(cent, rotBrg + 180 - theta, rngKm);
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

	private void drawBoundary(Context2d context) {
		drawSegments(context);
	}

	private void draw(Context2d context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		drawBoundary(context);
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
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					;
					if (event != null) {
						setSmjFromPix(event.getClientX(), event.getClientY());
						setSmnPosFromSmjBrg();
					}
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
				}

				@Override
				public void handleMouseOut(Event event) {
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
				public void handleMouseDblClick(Event event) {
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
			double rangeKm = m_rb.gcDistanceFromTo(cent, gc);
			double bearing = m_smnRngBrg.getBearing();
			m_smnRngBrg.setRangeKm(rangeKm);
			pos = m_rb.gcPointFrom(cent, bearing, rangeKm);
			m_smnTool.setGeoPos(pos);
			m_needsUpdate = true;
		}
	}

	public void setSmnAxis(GeodeticCoords smnPos) {
		if (m_smnTool == null) {
			m_smnTool = getSmnTool();
		}
		m_smnTool.setGeoPos(smnPos);
		GeodeticCoords cent = m_centerTool.getGeoPos();
		double rangeKm = m_rb.gcDistanceFromTo(cent, smnPos);
		double brg = m_rb.gcBearingFromTo(cent, smnPos);
		m_smnRngBrg.setRangeKm(rangeKm);
		m_smnRngBrg.setBearing(brg);
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
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setSmnRangePix(x, y);
				}

				@Override
				public void handleMouseUp(Event event) {
					// m_mouseDown = false;
					int x = event.getClientX();
					int y = event.getClientY();
					setSmnRangePix(x, y);
				}

				@Override
				public void handleMouseOut(Event event) {
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
				public void handleMouseDblClick(Event event) {
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
				public void handleMouseDown(Event event) {
				}

				@Override
				public void handleMouseMove(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveAxisPos();
				}

				@Override
				public void handleMouseUp(Event event) {
					int x = event.getClientX();
					int y = event.getClientY();
					setCenterFromPix(x, y);
					moveAxisPos();
				}

				@Override
				public void handleMouseOut(Event event) {
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
				public void handleMouseDblClick(Event event) {
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
		draw(context);
		return (IShape) this;
	}

	//
	public GeodeticCoords getSmjPos() {
		return m_smjTool.getGeoPos();
	}

	public void setSmjPos(GeodeticCoords pos) {
		if (m_smjTool == null) {
			m_smnTool = getSmjTool();
		}
		m_smjTool.setGeoPos(pos);
	}

	public Box withSmjPos(GeodeticCoords pos) {
		setSmjPos(pos);
		return this;
	}

	//
	public GeodeticCoords getSmnPos() {
		return m_smnTool.getGeoPos();
	}

	public void setSmnPos(GeodeticCoords pos) {
		if (m_smnTool == null) {
			m_smnTool = getSmnTool();
		}
		m_smnTool.setGeoPos(pos);
	}

	public Box withSmnPos(GeodeticCoords pos) {
		setSmnPos(pos);
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

	public Box withCenter(GeodeticCoords center) {
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
			m_centerHandle.setCenter(vc.getX(), vc.getY());
			m_centerHandle.draw(context);
			moveHandles(m_centerHandle, vc, context);
			// semi-major handle
			gc = getSmjPos();
			vc = m_convert.geodeticToView(gc);
			m_smjHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_smjHandle, vc, context);
			// semi-minor handle
			gc = getSmnPos();
			vc = m_convert.geodeticToView(gc);
			m_smnHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			moveHandles(m_smnHandle, vc, context);
		}
		return (IShape) this;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double rotBrg = m_smjRngBrg.getBearing();
		GeodeticCoords cent = m_centerTool.getGeoPos();
		double theta = Func.RadToDeg(Math.atan2(b, a));
		double rngKm = Math.sqrt(a * a + b * b);
		GeodeticCoords tl = m_rb.gcPointFrom(cent, rotBrg - 180 + theta, rngKm);
		GeodeticCoords tr = m_rb.gcPointFrom(cent, rotBrg - theta, rngKm);
		GeodeticCoords br = m_rb.gcPointFrom(cent, rotBrg + theta, rngKm);
		GeodeticCoords bl = m_rb.gcPointFrom(cent, rotBrg + 180 - theta, rngKm);
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

	@Override
	public IShapeTool createEditTool(IShapeEditor se) {
		IShapeTool tool = new CommonEditTool(se);
		tool.setShape(this);
		return tool;
	}
}
