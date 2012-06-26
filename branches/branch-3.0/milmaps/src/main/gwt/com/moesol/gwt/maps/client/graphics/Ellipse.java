/**
 * (c) Copyright, Moebius Solutions, Inc., 2012
 *
 *                        All Rights Reserved
 *
 * LICENSE: GPLv3
 */
package com.moesol.gwt.maps.client.graphics;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.algorithms.Func;
import com.moesol.gwt.maps.client.algorithms.RangeBearingS;
import com.moesol.gwt.maps.client.algorithms.RngBrg;

public class Ellipse extends AbstractShape {
	private static final int NUM_ELLIPSE_PTS = 50;
	private static final RangeBearingS m_rb = new RangeBearingS();
	private final AnchorHandle m_centerHandle = new AnchorHandle();
	private final AnchorHandle m_smjHandle = new AnchorHandle();
	private final AnchorHandle m_smnHandle = new AnchorHandle();
	private GeodeticCoords m_center = null;
	protected GeodeticCoords m_smjPos = null;
	protected GeodeticCoords m_smnPos = null;
	protected RngBrg m_smjRngBrg = null;
	protected RngBrg m_smnRngBrg = null;
	protected ViewCoords[] m_pts = null;
	protected RngBrg[] m_rbPts = null;
	// private boolean m_mouseDown = true;
	protected IAnchorTool m_centerTool = null;
	protected IAnchorTool m_smjTool = null;
	protected IAnchorTool m_smnTool = null;

	public Ellipse() {
		m_id = "Ellipse";
		m_pts = new ViewCoords[NUM_ELLIPSE_PTS];
		m_rbPts = new RngBrg[NUM_ELLIPSE_PTS];
		m_smjRngBrg = new RngBrg(0,0);
		m_smnRngBrg = new RngBrg(0,0);
	}

	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Ellipse: m_convert = null");
		}
		if (m_center == null) {
			throw new IllegalStateException("Ellipse: m_center = null");
		}
	}

	private void setSmjFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_smjPos == null || !m_smjPos.equals(gc)) {
			m_smjPos = gc;
			m_smjRngBrg = m_rb.RngBrgFromTo(m_center, m_smjPos);
			m_needsUpdate = true;
		}
	}

	private boolean _isSelected(GeodeticCoords gc, GeodeticCoords axisPos) {
		ViewCoords vc = m_convert.geodeticToView(gc);
		ViewCoords radPt = m_convert.geodeticToView(axisPos);
		return Func.isClose(radPt, vc, 4);
	}

	// Used to draw boundary
	public void computeRngBrgPts(
			double a, // Semi-major axis in Km
			double b, // Semi-minor axis in Km
			double start, // Starting angle in degrees
			double end, // Ending angle in degrees
			int numPts // Number of points in array
	) {
		double diff = Func.wrap360(end - start);
		diff = Func.DegToRad(diff);
		start = Func.DegToRad(start);
		end = Func.DegToRad(end);
		double angle, theta;
		theta = (diff / (numPts - 1));
		for (int i = 0; i < numPts; i++) {
			angle = start + (i * theta);
			double x = (a * Math.cos(angle));
			double y = (b * Math.sin(angle));
			if (a != b) {
				angle = Math.atan2(y, x);
			}
			m_rbPts[i] = new RngBrg(Math.sqrt(x * x + y * y),
					Func.RadToDeg(angle));
		}
	}

	// This creates an ellipse that has the semi-major axis
	// run along the x-axis or the y-axis. ie rotates only 90 degrees.
	// Centered at the origin
	private void createBoundary() {
		double a = m_smjRngBrg.getRanegKm();
		double b = m_smnRngBrg.getRanegKm();
		double brg = m_smjRngBrg.getBearing();
		computeRngBrgPts(a, b, 0, 360, NUM_ELLIPSE_PTS);
		for (int i = 0; i < NUM_ELLIPSE_PTS; i++) {
			double degBrg = brg + m_rbPts[i].getBearing();
			double rngKm = m_rbPts[i].getRanegKm();
			GeodeticCoords gc = m_rb.gcPointFrom(m_center, degBrg, rngKm);
			m_pts[i] = m_convert.geodeticToView(gc);
		}
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

	public IAnchorTool getSmjAnchorTool() {
		if (m_smjTool == null) {
			m_smjTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					// m_mouseDown = true;
					return true;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					int x = event.getX();
					int y = event.getY();
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
					//upDateRngBrg();
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					// m_mouseDown = false;
					int x = event.getX();
					int y = event.getY();
					setSmjFromPix(x, y);
					setSmnPosFromSmjBrg();
					//upDateRngBrg();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					// m_mouseDown = false;
					return true;
				}

				@Override
				public void done() {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					return _isSelected(gc, m_smjPos);
				}
			};
		}
		return m_smjTool;
	}

	private void setSmnRangePix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_smnPos == null || !m_smnPos.equals(gc)) {
			double rangeKm = m_rb.gcDistanceFromTo(m_center, gc);
			double bearing = m_smnRngBrg.getBearing();
			m_smnRngBrg.setRanegKm(rangeKm);
			m_smnPos = m_rb.gcPointFrom(m_center, bearing , rangeKm);
			m_needsUpdate = true;
		}
	}
	
	public void setSmnAxis(GeodeticCoords smnPos){
		m_smnPos = smnPos;
		double rangeKm = m_rb.gcDistanceFromTo(m_center, m_smnPos);
		double brg = m_rb.gcBearingFromTo(m_center, m_smnPos);
		m_smnRngBrg.setRanegKm(rangeKm);
		m_smnRngBrg.setBearing(brg);
		m_needsUpdate = true;		
	}

	public IAnchorTool getSmnAnchorTool() {
		if (m_smnTool == null) {
			m_smnTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					// m_mouseDown = true;
					return true;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					// if (m_mouseDown == true) {
					int x = event.getX();
					int y = event.getY();
					setSmnRangePix(x, y);
					// }
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					// m_mouseDown = false;
					int x = event.getX();
					int y = event.getY();
					setSmnRangePix(x, y);
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					// m_mouseDown = false;
					return true;
				}

				@Override
				public void done() {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					return _isSelected(gc, m_smnPos);
				}
			};
		}
		return m_smnTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (!m_center.equals(gc)) {
			m_center = gc;
			m_needsUpdate = true;
		}
	}

	private void moveAxisPos() {
		if (m_smjRngBrg != null) {
			double rng = m_smjRngBrg.getRanegKm();
			double brg = m_smjRngBrg.getBearing();
			m_smjPos = m_rb.gcPointFrom(m_center, brg, rng);
		}

		if (m_smnRngBrg != null) {
			double rng = m_smnRngBrg.getRanegKm();
			double brg = m_smnRngBrg.getBearing();
			m_smnPos = m_rb.gcPointFrom(m_center, brg, rng);
		}
	}

	public IAnchorTool getCenterAnchorTool() {
		if (m_centerTool == null) {
			m_centerTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					return false;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					int x = event.getX();
					int y = event.getY();
					setCenterFromPix(x, y);
					moveAxisPos();
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					int x = event.getX();
					int y = event.getY();
					setCenterFromPix(x, y);
					moveAxisPos();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					return false;
				}

				@Override
				public void done() {
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					return _isSelected(gc, m_center);
				}
			};
		}
		return m_centerTool;
	}

	public ViewCoords[] createBoundary(int size) {
		if (size < 3) {
			throw new IllegalArgumentException("array size is less than 3");
		}
		checkForException();
		m_pts = new ViewCoords[size];
		double degInc = 360.0 / (size - 1);
		double distKm = m_smjRngBrg.getRanegKm();
		for (int i = 0; i < size - 1; i++) {
			double brng = degInc * i;
			GeodeticCoords gc = m_rb.gcPointFrom(m_center, brng, distKm);
			m_pts[i] = m_convert.geodeticToView(gc);
		}
		m_pts[size - 1] = m_pts[0];
		return m_pts;
	}

	private void drawBoundary(Context2d context) {
		context.beginPath();
		context.setStrokeStyle(m_color);
		context.setLineWidth(2);
		createBoundary();
		context.moveTo(m_pts[0].getX(), m_pts[0].getY());
		for (int i = 1; i < NUM_ELLIPSE_PTS; i++) {
			context.lineTo(m_pts[i].getX(), m_pts[i].getY());
		}
		context.closePath();
		context.stroke();
	}

	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return this;
	}

	@Override
	public IShape erase(Context2d context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IShape render(Context2d context) {
		drawBoundary(context);
		return this;
	}

	//
	public GeodeticCoords getSmjPos() {
		return m_smjPos;
	}

	public void setSmjPos(GeodeticCoords pos) {
		m_smjPos = pos;
	}

	public Ellipse withSmjPos(GeodeticCoords pos) {
		setSmjPos(pos);
		return this;
	}

	//
	public GeodeticCoords getSmnPos() {
		return m_smnPos;
	}

	public void setSmnPos(GeodeticCoords pos) {
		m_smnPos = pos;
	}

	public Ellipse withSmnPos(GeodeticCoords pos) {
		setSmnPos(pos);
		return this;
	}

	public GeodeticCoords getCenter() {
		return m_center;
	}

	public void setCenter(GeodeticCoords center) {
		m_center = center;
	}

	public Ellipse withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}

	@Override
	public IShape drawHandles(Context2d context) {
		if (context != null) {
			// Center Handle
			GeodeticCoords gc = getCenter();
			ViewCoords vc = m_convert.geodeticToView(gc);
			m_centerHandle.setCenter(vc.getX(), vc.getY());
			m_centerHandle.draw(context);
			// semi-major handle
			gc = getSmjPos();
			vc = m_convert.geodeticToView(gc);
			m_smjHandle.setCenter(vc.getX(), vc.getY()).draw(context);
			// semi-minor handle
			gc = getSmnPos();
			vc = m_convert.geodeticToView(gc);
			m_smnHandle.setCenter(vc.getX(), vc.getY()).draw(context);

		}
		return this;
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		if (m_pts != null) {
			int n = m_pts.length;
			if (n > 3) {
				for (int i = 0; i < n - 1; i++) {
					double dist = Func.ptLineDist(m_pts[i], m_pts[i + 1], px,
							py);
					if (dist < eps) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean positionTouches(GeodeticCoords position) {
		checkForException();
		ViewCoords vc = m_convert.geodeticToView(position);
		ViewCoords centPix = m_convert.geodeticToView(m_center);
		if (Func.isClose(centPix, vc, Func.PIX_SELECT_TOLERANCE)) {
			return true;
		}
		return ptCloseToEdge(vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		IAnchorTool tool = getSmjAnchorTool();
		if (tool.isSlected(position)) {
			return tool;
		}
		tool = getSmnAnchorTool();
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
