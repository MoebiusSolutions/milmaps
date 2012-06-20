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
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.moesol.gwt.maps.client.GeodeticCoords;
import com.moesol.gwt.maps.client.ViewCoords;
import com.moesol.gwt.maps.client.units.Distance;
import com.moesol.gwt.maps.client.units.DistanceUnit;

import com.moesol.gwt.maps.client.algorithms.*;

public class Circle implements IShape {
	private static final int NUM_CIR_PTS = 50;
	private static final RangeBearingS m_rb = new RangeBearingS();
	private ICoordConverter m_convert;
	private GeodeticCoords m_center = null;
	private GeodeticCoords m_radiusPos = null;
	private Distance m_radius = null;
	private RngBrg m_radRngBrg = null;
	private ViewCoords m_vCenter;
	private ViewCoords[] m_pts = null;
	private int m_iRadius;
	private boolean m_needsUpdate = false;
	//private boolean m_mouseDown = true;
	private IAnchorTool m_radiusTool = null;
	private IAnchorTool m_centerTool = null;
	private String m_id;
	private CssColor m_color = CssColor.make(255, 255, 255);
	private boolean m_bSeletected = false;

	public CssColor getColor() {
		return m_color;
	}

	public void setColor(CssColor color) {
		m_color = color;
	}

	private void checkForException() {
		if (m_convert == null) {
			throw new IllegalStateException("Circle: m_convert = null");
		}
		if (m_center == null) {
			throw new IllegalStateException("Circle: m_center = null");
		}
	}

	private void setRadiusFromPix(int x, int y) {
		checkForException();
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (m_radiusPos == null || !m_radiusPos.equals(gc)) {
			m_radiusPos = gc;
			upDateRngBrg();
			m_needsUpdate = true;
		}
	}

	private void upDateRngBrg() {
		checkForException();
		m_radRngBrg = m_rb.RngBrgFromTo(m_center, m_radiusPos);
	}

	public IAnchorTool getRadiusAnchorTool() {
		if (m_radiusTool == null) {
			m_radiusTool = new IAnchorTool() {
				@Override
				public boolean handleMouseDown(MouseDownEvent event) {
					//m_mouseDown = true;
					return true;
				}

				@Override
				public boolean handleMouseMove(MouseMoveEvent event) {
					//if (m_mouseDown == true) {
						int x = event.getX();
						int y = event.getY();
						setRadiusFromPix(x, y);
					//}
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					//m_mouseDown = false;
					int x = event.getX();
					int y = event.getY();
					setRadiusFromPix(x, y);
					upDateRngBrg();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					//m_mouseDown = false;
					upDateRngBrg();
					return true;
				}

				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					ViewCoords radPt = m_convert.geodeticToView(m_radiusPos);
					return Func.isClose(radPt, vc, 4);
				}
			};
		}
		return m_radiusTool;
	}

	private void setCenterFromPix(int x, int y) {
		GeodeticCoords gc = m_convert.viewToGeodetic(new ViewCoords(x, y));
		if (!m_center.equals(gc)) {
			m_center = gc;
			m_needsUpdate = true;
		}
	}

	private void moveRadiusPos() {
		if (m_radRngBrg != null) {
			double rng = m_radRngBrg.getRanegKm();
			double brg = m_radRngBrg.getBearing();
			m_radiusPos = m_rb.gcPointFrom(m_center, brg, rng);
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
					moveRadiusPos();
					return true;
				}

				@Override
				public boolean handleMouseUp(MouseUpEvent event) {
					int x = event.getX();
					int y = event.getY();
					setCenterFromPix(x, y);
					moveRadiusPos();
					return true;
				}

				@Override
				public boolean handleMouseOut(MouseOutEvent event) {
					return false;
				}

				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isSlected(GeodeticCoords gc) {
					ViewCoords vc = m_convert.geodeticToView(gc);
					ViewCoords centPt = m_convert.geodeticToView(m_center);
					return Func.isClose( centPt, vc, 4);
				}
			};
		}
		return m_centerTool;
	}

	// TODO change the following distance calculation to
	// something usable. This next routine is temporary.

	private Distance computeDistance() {
		double dist = m_rb.gcDistanceFromTo(m_center, m_radiusPos);
		return new Distance(dist, DistanceUnit.KILOMETERS);
	}

	private void computeRadius() {
		m_radius = computeDistance();
	}

	// Shape interface implementation
	public void setCoordConverter(ICoordConverter cc) {
		m_convert = cc;
	}

	private int computePixelDistance(ViewCoords vc, ViewCoords vr) {
		int xDist = Math.abs(vc.getX() - vr.getX());
		int yDist = Math.abs(vc.getY() - vr.getY());
		return (int) (Math.sqrt(xDist * xDist + yDist * yDist));
	}

	public ViewCoords[] createBoundary(int size) {
		if (size < 3) {
			throw new IllegalArgumentException("array size is less than 3");
		}
		checkForException();
		m_pts = new ViewCoords[size];
		double degInc = 360.0 / (size - 1);
		double distKm = m_radRngBrg.getRanegKm();
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
		createBoundary(NUM_CIR_PTS);
		context.moveTo(m_pts[0].getX(), m_pts[0].getY());
		for (int i = 1; i < NUM_CIR_PTS; i++) {
			context.lineTo(m_pts[i].getX(), m_pts[i].getY());
		}
		context.closePath();
		context.stroke();
	}

	private void draw(Context2d context) {
		if (context != null) {
			m_vCenter = m_convert.geodeticToView(m_center);
			ViewCoords vr = m_convert.geodeticToView(m_radiusPos);
			m_iRadius = computePixelDistance(m_vCenter, vr);
			drawBoundary(context);
		}
	}

	private void _erase(Context2d context) {
		if (m_vCenter != null && context != null) {
			int rad = m_iRadius + 50;
			context.clearRect(m_vCenter.getX() - rad, m_vCenter.getY() - rad,
					m_vCenter.getX() + rad, m_vCenter.getY() + rad);
		}
	}

	@Override
	public void setId(String id) {
		m_id = id;
	}

	@Override
	public String id() {
		return "Circle";
	}

	@Override
	public boolean touchesCoordinates() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IShape selected(boolean selected) {
		m_bSeletected = selected;
		return this;
	}

	@Override
	public IShape render(Context2d ct) {
		draw(ct);
		return this;
	}

	@Override
	public IShape erase(Context2d ct) {
		_erase(ct);
		return this;
	}
	
	@Override
	public boolean isSelected() {
		return m_bSeletected;
	}

	@Override
	public boolean needsUpdate() {
		if (m_needsUpdate) {
			m_needsUpdate = false;
			return true;
		}
		return false;
	}

	public Distance getRadius() {
		return m_radius;
	}

	public void setRadius(Distance radius) {
		m_radius = radius;
	}

	public Circle withRadius(Distance radius) {
		setRadius(radius);
		return this;
	}

	public GeodeticCoords getRadiusPos() {
		return m_radiusPos;
	}

	public void setRadiusPos(GeodeticCoords radPos) {
		m_radiusPos = radPos;
	}

	public Circle withRadiusPos(GeodeticCoords radPos) {
		setRadiusPos(radPos);
		return this;
	}

	public GeodeticCoords getCenter() {
		return m_center;
	}

	public void setCenter(GeodeticCoords center) {
		m_center = center;
	}

	public Circle withCenter(GeodeticCoords center) {
		setCenter(center);
		return this;
	}

	public void setCenterRadiusPos(GeodeticCoords center, GeodeticCoords radius) {
		setCenter(center);
		setRadiusPos(radius);
		m_radRngBrg = m_rb.RngBrgFromTo(m_center, m_radiusPos);
	}

	public boolean ptCloseToEdge(int px, int py, double eps) {
		if (m_pts != null) {
			int n = m_pts.length;
			if (n > 3) {
				for (int i = 0; i < n - 1; i++) {
					double dist = Func.ptLineDist(m_pts[i], m_pts[i+1], px, py);
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
		return ptCloseToEdge( vc.getX(), vc.getY(), Func.PIX_SELECT_TOLERANCE);
	}

	@Override
	public IAnchorTool getAnchorByPosition(GeodeticCoords position) {
		checkForException();
		IAnchorTool tool = getRadiusAnchorTool();
		if (tool.isSlected(position)){
			return tool;
		}
		tool = getCenterAnchorTool();
		if (tool.isSlected(position)){
			return tool;
		}
		return null;
	}
}
